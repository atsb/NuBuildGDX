package ru.m210projects.Blood.AI;

import static ru.m210projects.Blood.Actor.IsDudeSprite;
import static ru.m210projects.Blood.Actor.kAttrFree;
import static ru.m210projects.Blood.Actor.sprXVel;
import static ru.m210projects.Blood.Actor.sprYVel;
import static ru.m210projects.Blood.DB.kDudeBase;
import static ru.m210projects.Blood.DB.kItemBase;
import static ru.m210projects.Blood.DB.kItemLtdInvisibility;
import static ru.m210projects.Blood.DB.xsprite;
import static ru.m210projects.Blood.Gameutils.Chance;
import static ru.m210projects.Blood.Gameutils.ClipHigh;
import static ru.m210projects.Blood.Gameutils.ClipLow;
import static ru.m210projects.Blood.Gameutils.ClipRange;
import static ru.m210projects.Blood.Gameutils.FindSector;
import static ru.m210projects.Blood.Gameutils.HitScan;
import static ru.m210projects.Blood.Globals.SS_SPRITE;
import static ru.m210projects.Blood.Globals.gFrame;
import static ru.m210projects.Blood.Globals.gFrameClock;
import static ru.m210projects.Blood.Globals.gPlayer;
import static ru.m210projects.Blood.Globals.kAngle180;
import static ru.m210projects.Blood.Globals.kAngle60;
import static ru.m210projects.Blood.Globals.kAngle90;
import static ru.m210projects.Blood.Globals.kAngleMask;
import static ru.m210projects.Blood.Globals.kFrameTicks;
import static ru.m210projects.Blood.Globals.kMaxSprites;
import static ru.m210projects.Blood.Globals.kStatDude;
import static ru.m210projects.Blood.Main.engine;
import static ru.m210projects.Blood.Main.game;
import static ru.m210projects.Blood.PLAYER.powerupCheck;
import static ru.m210projects.Blood.SOUND.sfxKill3DSound;
import static ru.m210projects.Blood.SOUND.sfxStart3DSound;
import static ru.m210projects.Blood.Trig.Cos;
import static ru.m210projects.Blood.Trig.Sin;
import static ru.m210projects.Blood.Types.DudeInfo.dudeInfo;
import static ru.m210projects.Blood.Types.Seq.SeqHandling.seqFrame;
import static ru.m210projects.Blood.Types.Seq.SeqHandling.seqSpawn;
import static ru.m210projects.Build.Engine.CLIPMASK0;
import static ru.m210projects.Build.Engine.pHitInfo;
import static ru.m210projects.Build.Engine.sprite;
import static ru.m210projects.Build.Net.Mmulti.connecthead;
import static ru.m210projects.Build.Net.Mmulti.connectpoint2;
import static ru.m210projects.Build.Pragmas.dmulscale;
import static ru.m210projects.Build.Pragmas.klabs;
import static ru.m210projects.Build.Pragmas.mulscale;

import ru.m210projects.Blood.PLAYER;
import ru.m210projects.Blood.Types.CALLPROC;
import ru.m210projects.Blood.Types.DudeInfo;
import ru.m210projects.Blood.Types.XSPRITE;
import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.Types.SPRITE;

public abstract class GeneticDude {
	
	protected static class AiState {
		final int seqId, ticks;
		final AiState next;
		final Runnable enter, move, think;
		final CALLPROC callback;
		
		public AiState(int seqId, CALLPROC callback, int ticks, Runnable enter, Runnable move, Runnable think, AiState next) {
			this.seqId = seqId;
			this.callback = callback;
			this.ticks = ticks;
			this.enter = enter;
			this.move = move;
			this.think = think;
			this.next = next;
		}
	}
	
	/*
	 * aiMoveForward if(pSprite.lotag == kDudeBurning)
	 * aiProcessDudes aiTickHandler
	 */

	private static final int kAIThinkMask = 3;

	protected SPRITE pSprite;
	protected XSPRITE pXSprite;
	protected DudeInfo pDudeInfo;
	protected AiState aiState;
	
	protected int aiClock;
	protected int aiSoundOnce;
	protected int aiTeslaHit;
	protected int cumulDamage; // cumulative damage per frame
	

	public GeneticDude(SPRITE pSprite)
	{
		this.pSprite = pSprite;
		if(!IsDudeSprite(pSprite))
			game.dassert("pSprite.type >= kDudeBase && pSprite.type < kDudeMax");
		
		this.pXSprite = xsprite[pSprite.extra];
		this.pDudeInfo = dudeInfo[pSprite.lotag - kDudeBase];
		
		this.aiClock = 0;
		this.aiSoundOnce = 0;
		this.cumulDamage = 0;
	}

	public abstract void activate();

	public abstract int damage(int nSource, int nDamageType, int nDamage);

	public abstract void kill(int nSource, int nDamageType, int nDamage);

	public abstract void warp(int type);

	public abstract void touch();
	
	protected abstract void recoil(boolean chance);
	

	protected boolean aiCanMove(int nTarget, int ang, int dist) {
		int dx = mulscale(dist, Cos(ang), 30);
		int dy = mulscale(dist, Sin(ang), 30);

		int x = pSprite.x;
		int y = pSprite.y;
		int z = pSprite.z;

		HitScan(pSprite, z, Cos(ang) >> 16, Sin(ang) >> 16, 0, pHitInfo, CLIPMASK0, dist);
		int hitDist = (int) engine.qdist(x - pHitInfo.hitx, y - pHitInfo.hity);

		if ( hitDist - (pSprite.clipdist << 2) < dist )
		{
			// okay to be blocked by target
			return pHitInfo.hitsprite >= 0 && pHitInfo.hitsprite == nTarget;
		}

		x += dx;
		y += dy;

		return FindSector(x, y, z, pSprite.sectnum);
	}

	protected void aiChooseDirection(int ang) {
		int dang = ((kAngle180 + ang - pSprite.ang) & kAngleMask) - kAngle180;
		
		int sin = Sin(pSprite.ang);
		int cos = Cos(pSprite.ang);
		
		// find vel relative to current angle
		long vel = dmulscale(sprXVel[pSprite.xvel], cos, sprYVel[pSprite.xvel], sin, 30);

//		int avoidDist = (int) (vel * (kTimerRate / 2 / kFrameTicks) >> 13);
		
		int avoidDist = (int) (  (      (15 * vel >> 12) - (15 * vel >> 43)     ) >> 1  );

		int turnTo = kAngle60;
		if (dang < 0 )
			turnTo = -turnTo;

		// clear movement toward target?			
		if ( aiCanMove(pXSprite.target, pSprite.ang + dang, avoidDist) )
			pXSprite.goalAng = (pSprite.ang + dang) & kAngleMask;
		// clear movement partially toward target?
		else if ( aiCanMove(pXSprite.target, pSprite.ang + dang / 2, avoidDist) )
			pXSprite.goalAng = (pSprite.ang + dang / 2) & kAngleMask;
		else if ( aiCanMove(pXSprite.target,  pSprite.ang - dang / 2, avoidDist) )
			pXSprite.goalAng = (pSprite.ang - dang / 2) & kAngleMask;
		// try turning in target direction
		else if ( aiCanMove(pXSprite.target, pSprite.ang + turnTo, avoidDist) )
			pXSprite.goalAng = (pSprite.ang + turnTo) & kAngleMask;
		// clear movement straight?
		else if ( aiCanMove(pXSprite.target, pSprite.ang, avoidDist) )
			pXSprite.goalAng = pSprite.ang;
		// try turning away
		else if ( aiCanMove(pXSprite.target, pSprite.ang - turnTo, avoidDist) )
			pXSprite.goalAng = (pSprite.ang - turnTo) & kAngleMask;
		else 
			pXSprite.goalAng = (pSprite.ang + kAngle60) & kAngleMask;
		
		// choose dodge direction
		pXSprite.dodgeDir = Chance(0x4000) ? 1 : -1;
		
		if ( !aiCanMove(pXSprite.target, pSprite.ang + kAngle90 * pXSprite.dodgeDir, 512) )
		{
			pXSprite.dodgeDir = -pXSprite.dodgeDir;
			if ( !aiCanMove(pXSprite.target, pSprite.ang + kAngle90 * pXSprite.dodgeDir, 512) )
				pXSprite.dodgeDir = 0;
		}
	}

	protected void aiMoveForward() {
		int maxTurn = pDudeInfo.angSpeed * kFrameTicks >> 4;
		int dang = ((kAngle180 + pXSprite.goalAng - pSprite.ang) & kAngleMask) - kAngle180;
		pSprite.ang = (short)((pSprite.ang + ClipRange(dang, -maxTurn, maxTurn)) & kAngleMask);
		
		// don't move forward if trying to turn around
		if ( klabs(dang) > kAngle60 )
			return;
		
		int sin = Sin(pSprite.ang);
		int cos = Cos(pSprite.ang);

		sprXVel[pSprite.xvel] += mulscale(cos, pDudeInfo.frontSpeed, 30);
		sprYVel[pSprite.xvel] += mulscale(sin, pDudeInfo.frontSpeed, 30);
	}

	protected void aiMoveTurn() {
		int dang = ((kAngle180 + pXSprite.goalAng - pSprite.ang) & kAngleMask) - kAngle180;
		int maxTurn = pDudeInfo.angSpeed * kFrameTicks >> 4;

		pSprite.ang = (short)((pSprite.ang + ClipRange(dang, -maxTurn, maxTurn)) & kAngleMask);
	}

	protected void aiMoveDodge() {
		int dang = ((kAngle180 + pXSprite.goalAng - pSprite.ang) & kAngleMask) - kAngle180;
		int maxTurn = pDudeInfo.angSpeed * kFrameTicks >> 4;
		pSprite.ang = (short)((pSprite.ang + ClipRange(dang, -maxTurn, maxTurn)) & kAngleMask);

		if ( pXSprite.dodgeDir == 0 )
			return;

		long sin = Sin(pSprite.ang);
		long cos = Cos(pSprite.ang);

		// find vel and svel relative to current angle
		long vel = dmulscale(sprXVel[pSprite.xvel], cos, sprYVel[pSprite.xvel], sin, 30);
		long svel = dmulscale(sprXVel[pSprite.xvel], sin, -sprYVel[pSprite.xvel], cos, 30);

		if ( pXSprite.dodgeDir > 0 )
			svel += pDudeInfo.sideSpeed;
		else
			svel -= pDudeInfo.sideSpeed;
		
		// reconstruct x and y velocities
		sprXVel[pSprite.xvel] = dmulscale(vel, cos, svel, sin, 30);
	    sprYVel[pSprite.xvel] = dmulscale(vel, sin, -svel, cos, 30);
	}

	protected boolean aiThinkTarget() {
		if ( !Chance(pDudeInfo.alertChance / 2) )
			return true;

		for (int i = connecthead; i >= 0; i = connectpoint2[i])
		{
			PLAYER pPlayer = gPlayer[i];
			
			// skip this player if the he owns the dude or is invisible
			if ( pSprite.owner == pPlayer.nSprite
			|| pPlayer.pXsprite.health == 0 
			|| powerupCheck( pPlayer, kItemLtdInvisibility - kItemBase ) > 0 )
				continue;
				
			int x = pPlayer.pSprite.x;
			int y = pPlayer.pSprite.y;
			int z = pPlayer.pSprite.z;
			short nSector = pPlayer.pSprite.sectnum;

			int dx = x - pSprite.x;
			int dy = y - pSprite.y;
			
			long dist = engine.qdist(dx, dy);
			
			if ( dist <= pDudeInfo.seeDist || dist <= pDudeInfo.hearDist )
			{
				int eyeAboveZ = pDudeInfo.eyeHeight * pSprite.yrepeat << 2;

				// is there a line of sight to the player?
				if ( engine.cansee(x, y, z, nSector, pSprite.x, pSprite.y, pSprite.z - eyeAboveZ,
					pSprite.sectnum) ) 
				{
					int nAngle = engine.getangle(dx, dy);
					int losAngle = ((kAngle180 + nAngle - pSprite.ang) & kAngleMask) - kAngle180;

					// is the player visible?
					if ( dist < pDudeInfo.seeDist && klabs(losAngle) <= pDudeInfo.periphery )
					{
						setTarget( pPlayer.nSprite );
						activate();
						return true;
					}
					
					// we may want to make hearing a function of sensitivity, rather than distance
					if ( dist < pDudeInfo.hearDist )
					{
						setTarget(x, y, z);
						activate();
						return true;
					}
				}
			}
		}
		
		return false;
	}

	protected void aiPlaySound(int nSound, int soundonce, int nChannel) {
		if(soundonce != 0)
		{
			if ( soundonce > aiSoundOnce || gFrameClock >= aiClock )
		    {
		     	sfxKill3DSound(pSprite, -1, -1);
		     	sfxStart3DSound(pSprite, nSound, nChannel, 0);
		     	aiSoundOnce = soundonce;
		     	aiClock = gFrameClock + 120;
		    }
		} 
		else sfxStart3DSound(pSprite, nSound, nChannel, 2);
	}
	
	protected void aiNewState(AiState pState) {
		pXSprite.stateTimer = pState.ticks;
		this.aiState = pState;
		int seqStartId = pDudeInfo.seqStartID;
		if ( pState.seqId >= 0 ) {
			if (!BuildGdx.cache.contains(seqStartId + pState.seqId, "SEQ")) {
				//System.err.println("NO SEQ: fullSeq = "+ (seqStartId + pState.seqId)+" / "+pXSprite.data2+" / "+seqStartId);
				return;
			}
			
			seqSpawn(seqStartId + pState.seqId, SS_SPRITE, pSprite.extra, pState.callback);
		}

		// call the enter function if defined
		if ( pState.enter != null ) 
			pState.enter.run();
	}
	
	public void setTarget(int x, int y, int z) {
		pXSprite.target = -1;
		pXSprite.targetX = x;
		pXSprite.targetY = y;
		pXSprite.targetZ = z;
	}

	public void setTarget(int nTarget) {
		if(nTarget < 0 || nTarget >= kMaxSprites) 
			game.dassert("nTarget >= 0 && nTarget < kMaxSprites");
		
		if ( nTarget == pSprite.owner )
			return;
		
		SPRITE pTarget = sprite[nTarget];
		if ( !IsDudeSprite(pTarget) )
			return;

		DudeInfo pTargetInfo = dudeInfo[pTarget.lotag - kDudeBase];

		pXSprite.target = nTarget;
		pXSprite.targetX = pTarget.x;
		pXSprite.targetY = pTarget.y;
		pXSprite.targetZ = pTarget.z - (pTargetInfo.eyeHeight * pTarget.yrepeat << 2);
	}

	public void process() {
		if ( (pSprite.hitag & kAttrFree) != 0 )
			return;

		int nXSprite = pSprite.extra;

		// don't manipulate dead guys
		if (( pXSprite.health == 0 ))
			return;

		pXSprite.stateTimer = ClipLow(pXSprite.stateTimer - kFrameTicks, 0);
		if ( aiState != null && aiState.move != null ) 
			aiState.move.run();
        if ( aiState != null && aiState.think != null && (gFrame & kAIThinkMask) == (pSprite.xvel & kAIThinkMask ) )
        	aiState.think.run();
		if ( aiState != null && pXSprite.stateTimer == 0 && aiState.next != null )
		{
			if ( aiState != null && aiState.ticks > 0 )
				aiNewState(aiState.next);
			else if ( seqFrame(SS_SPRITE, nXSprite) < 0 )
				aiNewState(aiState.next);
		}
		
        // process dudes for recoil
		if ( pXSprite.health != 0 && cumulDamage >= pDudeInfo.hinderDamage << 4 ) {
			pXSprite.data3 = (short) cumulDamage;
			
			boolean chance = Chance(0x4000);
			if(pSprite.statnum != kStatDude || !IsDudeSprite(pSprite))
				return;
			
			recoil(chance);
		}
		
		// reset the cumulative damages for the next frame
		cumulDamage = 0;
	}

	public boolean heal(int value, int max) {
		value <<= 4; // fix this later in the calling code
		max <<= 4;
		if (pXSprite.health < max) {
			pXSprite.health = ClipHigh(pXSprite.health + value, max);
			return true;
		}
		return false;
	}

	public int mass() {
		return pDudeInfo.mass;
	}

}
