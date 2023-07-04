package ru.m210projects.Blood.AI;

import static ru.m210projects.Blood.Actor.DropPickupObject;
import static ru.m210projects.Blood.Actor.IsDudeSprite;
import static ru.m210projects.Blood.Actor.actCheckRespawn;
import static ru.m210projects.Blood.Actor.actFireVector;
import static ru.m210projects.Blood.Actor.actPostSprite;
import static ru.m210projects.Blood.Actor.actSpawnBlood;
import static ru.m210projects.Blood.Actor.kAttrAiming;
import static ru.m210projects.Blood.Actor.kAttrFalling;
import static ru.m210projects.Blood.Actor.kAttrGravity;
import static ru.m210projects.Blood.Actor.kAttrMove;
import static ru.m210projects.Blood.Actor.kDamageBurn;
import static ru.m210projects.Blood.Actor.kDamageExplode;
import static ru.m210projects.Blood.Actor.kDamageSpirit;
import static ru.m210projects.Blood.Actor.kVectorAxe;
import static ru.m210projects.Blood.Actor.sprXVel;
import static ru.m210projects.Blood.Actor.sprYVel;
import static ru.m210projects.Blood.DB.kDudeAxeZombie;
import static ru.m210projects.Blood.DB.kDudeAxeZombieBurning;
import static ru.m210projects.Blood.DB.kDudeBase;
import static ru.m210projects.Blood.DB.kDudeEarthZombie;
import static ru.m210projects.Blood.DB.kDudeInnocent;
import static ru.m210projects.Blood.DB.kDudePlayer1;
import static ru.m210projects.Blood.DB.kDudeSleepZombie;
import static ru.m210projects.Blood.DB.kItemBase;
import static ru.m210projects.Blood.DB.kItemDeathMask;
import static ru.m210projects.Blood.DB.kItemKey1;
import static ru.m210projects.Blood.DB.kItemLtdInvisibility;
import static ru.m210projects.Blood.DB.kThingGib;
import static ru.m210projects.Blood.DB.xsprite;
import static ru.m210projects.Blood.EVENT.checkEventList;
import static ru.m210projects.Blood.EVENT.evPostCallback;
import static ru.m210projects.Blood.Gameutils.Chance;
import static ru.m210projects.Blood.Gameutils.ClipLow;
import static ru.m210projects.Blood.Gameutils.GetSpriteExtents;
import static ru.m210projects.Blood.Gameutils.IsPlayerSprite;
import static ru.m210projects.Blood.Gameutils.M2X;
import static ru.m210projects.Blood.Gameutils.Random;
import static ru.m210projects.Blood.Gameutils.extents_zTop;
import static ru.m210projects.Blood.Gib.actGenerateGibs;
import static ru.m210projects.Blood.Gib.startPos;
import static ru.m210projects.Blood.Gib.startVel;
import static ru.m210projects.Blood.Globals.SS_SPRITE;
import static ru.m210projects.Blood.Globals.gPlayer;
import static ru.m210projects.Blood.Globals.kAngle15;
import static ru.m210projects.Blood.Globals.kAngle180;
import static ru.m210projects.Blood.Globals.kAngleMask;
import static ru.m210projects.Blood.Globals.kMarkerUpperGoo;
import static ru.m210projects.Blood.Globals.kMarkerUpperWater;
import static ru.m210projects.Blood.Globals.kMaxSprites;
import static ru.m210projects.Blood.Globals.kSeqDudeAttack;
import static ru.m210projects.Blood.Globals.kSeqDudeDeath1;
import static ru.m210projects.Blood.Globals.kSeqDudeDeath2;
import static ru.m210projects.Blood.Globals.kSeqDudeDeath3;
import static ru.m210projects.Blood.Globals.kSeqDudeIdle;
import static ru.m210projects.Blood.Globals.kSeqDudeRecoil;
import static ru.m210projects.Blood.Globals.kSeqDudeTesla;
import static ru.m210projects.Blood.Globals.kSeqDudeWalk;
import static ru.m210projects.Blood.Globals.kStatDude;
import static ru.m210projects.Blood.Globals.kStatFree;
import static ru.m210projects.Blood.Globals.kStatThing;
import static ru.m210projects.Blood.LEVELS.levelAddKills;
import static ru.m210projects.Blood.Main.engine;
import static ru.m210projects.Blood.Main.game;
import static ru.m210projects.Blood.PLAYER.powerupCheck;
import static ru.m210projects.Blood.SOUND.sfxStart3DSound;
import static ru.m210projects.Blood.Trig.Cos;
import static ru.m210projects.Blood.Trig.Sin;
import static ru.m210projects.Blood.Types.DEMO.IsOriginalDemo;
import static ru.m210projects.Blood.Types.DudeInfo.dudeInfo;
import static ru.m210projects.Blood.Types.Seq.SeqHandling.DamageDude;
import static ru.m210projects.Blood.Types.Seq.SeqHandling.DamageDude2;
import static ru.m210projects.Blood.Types.Seq.SeqHandling.callbacks;
import static ru.m210projects.Blood.Types.Seq.SeqHandling.seqKill;
import static ru.m210projects.Blood.Types.Seq.SeqHandling.seqSpawn;
import static ru.m210projects.Build.Engine.headspritestat;
import static ru.m210projects.Build.Engine.nextspritestat;
import static ru.m210projects.Build.Engine.sprite;
import static ru.m210projects.Build.Pragmas.klabs;

import ru.m210projects.Blood.PLAYER;
import ru.m210projects.Blood.Types.CALLPROC;
import ru.m210projects.Blood.Types.XSPRITE;
import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.Types.SPRITE;

public class AxeZombie extends GeneticDude {

	private static final int kAxeZombieMeleeDist = M2X(2.0);

	private final Runnable think = new Runnable() { public void run() { aiThinkTarget(); } };
	private final Runnable move = new Runnable() { public void run() { aiMoveForward(); } };
	
	private final AiState zombieAIdle = new AiState(kSeqDudeIdle, null, 0,
			new Runnable() {
				public void run() {
					pXSprite.target = -1;
				}
			}, null, think, null);
	
	private final AiState zombieEIdle = new AiState(12, null, 0, null, null, think, null);

	private final AiState zombieAPonder = new AiState(kSeqDudeIdle, null, 0,
			null, new Runnable() {
		public void run() {
			aiMoveTurn();
		}
	},
			new Runnable() {
				public void run() {
					thinkPonder();
				}
			}, null);
	
	private final AiState zombieARecoil = new AiState(kSeqDudeRecoil, null, 0, null, null, null, zombieAPonder);
	private final AiState zombieARTesla = new AiState(kSeqDudeTesla, null, 0, null, null, null, zombieAPonder);
	private final AiState zombieAUp = new AiState(11, null, 0, null, null, null, zombieAPonder);
	private final AiState zombieAFall = new AiState(kSeqDudeDeath1, null, 360, null, null, null, zombieAUp);
	
	private final AiState zombieAHack = new AiState(kSeqDudeAttack, new CALLPROC() {
		public void run(int nXSprite) {
			HackCallback();
		}
	}, 80, null, null, null, zombieAPonder);
	
	private final AiState zombieAChase = new AiState(kSeqDudeWalk, null, 0, null,
			move, new Runnable() {
		public void run() {
			thinkChase();
		}
	}, null);
	
	private final AiState zombieAGoto = new AiState(kSeqDudeWalk, null, 1800, null,
			move, new Runnable() {
		public void run() {
			thinkGoto();
		}
	}, zombieAIdle);
	
	private final AiState zombieASearch = new AiState(kSeqDudeWalk, null, 1800, null,
			move, new Runnable() {
		public void run() {
			thinkSearch();
		}
	}, zombieAIdle);
	
	private final Runnable entryEZombie = new Runnable() {
		@Override
		public void run() {
			pSprite.hitag |= kAttrMove;
			pSprite.lotag = kDudeAxeZombie;
		}
	};
	
	private final AiState zombieEUp2 = new AiState(kSeqDudeIdle, null, 1, entryEZombie, null, null, zombieASearch);
	
	private final AiState zombieEUp = new AiState(9, null, 180,
			new Runnable() {
				public void run() {
					sfxStart3DSound(pSprite, 1100, -1, 0);
					pSprite.ang = engine.getangle(pXSprite.targetX - pSprite.x, pXSprite.targetY - pSprite.y);
				}
			}, null, null, zombieEUp2);
	
	private final AiState zombieSLIdle = new AiState(10, null, 0, null, null, think, null);
	
	private final AiState zombieSLUP = new AiState(11, null, 0, entryEZombie, null, null, zombieAPonder);
	

	public AxeZombie(SPRITE pSprite) {
		super(pSprite);
		
		System.err.println("Zombie " + pSprite.xvel);

		pSprite.hitag = ( kAttrMove | kAttrGravity | kAttrFalling );
		switch(pSprite.lotag)
		{
		case kDudeSleepZombie:
			aiNewState(zombieSLIdle);
			break;
		case kDudeEarthZombie:
			aiNewState(zombieEIdle);
			break;
		case kDudeAxeZombie:
			aiNewState(zombieAIdle);
			pSprite.hitag |= kAttrAiming;
			break;
		case kDudeAxeZombieBurning:
//			aiNewState(burnGoto[ZOMBIE]); XXX
			pSprite.hitag |= kAttrAiming;
			pXSprite.burnTime = 1200;
			break;
		}
		
		setTarget(0, 0, 0);

		pXSprite.stateTimer	= 0;
	}

	@Override
	public void activate() {
		if ( pXSprite.state == 0 )
		{
			// this doesn't take into account sprites triggered w/o a target location....
			aiChooseDirection(engine.getangle(pXSprite.targetX - pSprite.x, pXSprite.targetY - pSprite.y));
			pXSprite.state = 1;
		}
		
		switch(pSprite.lotag)
		{
		case kDudeSleepZombie:
			if (aiState == zombieSLIdle)
				aiNewState(zombieSLUP);
			break;
		case kDudeEarthZombie:
			if (aiState == zombieEIdle)
				aiNewState(zombieEUp);
			break;
		case kDudeAxeZombie:
			if ( pXSprite.target == -1 )
				aiNewState(zombieASearch);
			else
			{
				if(!Chance(0x5000))
					aiNewState(zombieAChase);
				else {
					aiPlaySound(Random(3) + 1103, 1, -1);
					aiNewState(zombieAChase);
				}
			}
			break;
		case kDudeAxeZombieBurning:
//			if (pXSprite.target == -1)  XXX
//				aiNewState(burnSearch[ZOMBIE]);
//			else 
//				aiNewState(burnChase[ZOMBIE]);
//			break;
		}
	}

	@Override
	public int damage(int nSource, int nDamageType, int nDamage) {
		if (pXSprite.health == 0)
			return 0;

		pXSprite.health = ClipLow(pXSprite.health - nDamage, 0);
		cumulDamage += nDamage;	// add to cumulative damage

		if (nSource >= 0) {
			if(nSource == pXSprite.reference)
				return 0;
			
			if (pXSprite.target == -1)
			{
				// give a dude a target
				setTarget(nSource);
				activate();
			} 
			else if (nSource != pXSprite.target)
			{
				// retarget
				int nThresh = nDamage;

				if ( sprite[nSource].lotag == pSprite.lotag )
					nThresh *= pDudeInfo.changeTargetKin;
				else
					nThresh *= pDudeInfo.changeTarget;

				if ( Chance(nThresh / 2) )
				{
					setTarget(nSource);
					activate();
				}
			}

			if ( nDamageType == 6 )
				aiTeslaHit = 1;
			else {
				if(!IsOriginalDemo())
					aiTeslaHit = 0;
			}
			
			// you DO need special processing here or somewhere else (your choice) for dodging
			switch ( pSprite.lotag )
			{
				case kDudeEarthZombie:
				case kDudeAxeZombie:
					if ( nDamageType == kDamageBurn && pXSprite.health <= pDudeInfo.fleeHealth )
				    {
				    	aiPlaySound(361, 0, -1);
				    	aiPlaySound(1106, 2, -1);
				    	pSprite.lotag = kDudeAxeZombieBurning;
//				    	aiNewState(burnGoto[ZOMBIE]); XXX
				    	if(!IsOriginalDemo() && pXSprite.burnTime == 0)
					  		pXSprite.burnTime = 1200;
					  	heal(dudeInfo[41].startHealth, dudeInfo[41].startHealth);
					  	checkEventList(pXSprite.reference, SS_SPRITE, 0);
				    }
					break;
			}
		}
		
		return nDamage;
	}

	@Override
	public void kill(int nSource, int nDamageType, int nDamage) {
		pSprite.hitag |= kAttrMove | kAttrGravity | kAttrFalling;
		
		if (pXSprite.key > 0)
			DropPickupObject(pSprite, kItemKey1 + pXSprite.key - 1);
		if (pXSprite.dropMsg != 0) {
			DropPickupObject(pSprite, pXSprite.dropMsg);
			if (!IsOriginalDemo())
				pXSprite.dropMsg = 0;
		}
		
		int deathType = kSeqDudeDeath1;
		switch (nDamageType) {
			case kDamageBurn:
				deathType = kSeqDudeDeath3;
				sfxStart3DSound(pSprite, 351, -1, 0);
				break;
			case kDamageExplode:
				deathType = kSeqDudeDeath2;
				break;
			case kDamageSpirit:
				switch (pSprite.lotag) {
				case kDudeAxeZombie:
				case kDudeEarthZombie:
					deathType = 14;
					break;
				}
				break;
		}
		
		// are we missing this sequence? if so, just delete it
		if (!BuildGdx.cache.contains(pDudeInfo.seqStartID + deathType, "SEQ")) {
			levelAddKills(pSprite);
			
			seqKill(SS_SPRITE, pSprite.extra); // make sure we remove any active sequence
			actPostSprite(pSprite.xvel, kStatFree);
			return;
		}
		
		short nXSprite = pSprite.extra;
		
		switch (pSprite.lotag) {
		case kDudeAxeZombie:
			sfxStart3DSound(pSprite, Random(2) + 1107, -1, 0);
			if (deathType == 2) {
				seqSpawn(pDudeInfo.seqStartID + 2, SS_SPRITE, nXSprite, callbacks[DamageDude]);
				GetSpriteExtents(pSprite);
				startPos.set(pSprite.x, pSprite.y, extents_zTop);
				startVel.set(sprXVel[pSprite.xvel] >> 1,
						sprYVel[pSprite.xvel] >> 1, -838860);
				actGenerateGibs(pSprite, 27, startPos, startVel);
			} else if (deathType != 1 || !Chance(0x2000)) {
				if (deathType == 14)
					seqSpawn(pDudeInfo.seqStartID + 14, SS_SPRITE, nXSprite, null);
				else if (deathType == 3)
					seqSpawn(pDudeInfo.seqStartID + 13, SS_SPRITE, nXSprite, callbacks[DamageDude2]);
				else {
					seqSpawn(deathType + pDudeInfo.seqStartID, SS_SPRITE, nXSprite, callbacks[DamageDude]);
				}
			} else {
				seqSpawn(pDudeInfo.seqStartID + 7, SS_SPRITE, nXSprite, callbacks[DamageDude]);
				evPostCallback(pSprite.xvel, SS_SPRITE, 0, 5);
				pXSprite.data1 = 35;
				pXSprite.data2 = 5;
				GetSpriteExtents(pSprite);
				startPos.set(pSprite.x, pSprite.y, extents_zTop);
				startVel.set(sprXVel[pSprite.xvel] >> 1,
						sprYVel[pSprite.xvel] >> 1, -1118481);
				actGenerateGibs(pSprite, 27, startPos, startVel);
				sfxStart3DSound(pSprite, 362, -1, 0);
			}
			break;
		case kDudeAxeZombieBurning:
			if (Chance(0x4000) && deathType == kSeqDudeDeath3)
				sfxStart3DSound(pSprite, 1109, -1, 0);
			else
				sfxStart3DSound(pSprite, Random(2) + 1107, -1, 0);
			nDamageType = kDamageExplode;
			if (Chance(0x4000)) {
				seqSpawn(13 + pDudeInfo.seqStartID, SS_SPRITE, nXSprite, callbacks[DamageDude]);
				GetSpriteExtents(pSprite);
				startPos.set(pSprite.x, pSprite.y, extents_zTop);
				startVel.set(sprXVel[pSprite.xvel] >> 1,
						sprYVel[pSprite.xvel] >> 1, -838860);
				actGenerateGibs(pSprite, 27, startPos, startVel);
			} else
				seqSpawn(13 + pDudeInfo.seqStartID, SS_SPRITE, nXSprite, callbacks[DamageDude2]);
			break;

		default:
			seqSpawn(deathType + pDudeInfo.seqStartID, SS_SPRITE, nXSprite, null);
			break;
		}

		if (nDamageType == kDamageExplode) {
			for (int i = 0; i < 3; i++) {
				int nGibType = pDudeInfo.nGibType[i];
				if (nGibType > -1)
					actGenerateGibs(pSprite, nGibType, null, null);

			}
			for (int j = 0; j < 4; ++j)
				actSpawnBlood(pSprite);
		}
		
		levelAddKills(pSprite);
		actCheckRespawn(pSprite);

		if (!IsOriginalDemo()) {
			if (xsprite[nXSprite].Proximity)
				xsprite[nXSprite].Proximity = false;
		}

		pSprite.lotag = kThingGib;
		actPostSprite(pSprite.xvel, kStatThing);
	}

	@Override
	public void warp(int type) {
		switch (type) {
			case kMarkerUpperWater:
			case kMarkerUpperGoo:
				switch (pSprite.lotag) {
					case kDudeAxeZombie:
						pXSprite.burnTime = 0;
						evPostCallback(pSprite.xvel, SS_SPRITE, 0, 11);
						sfxStart3DSound(pSprite, 720, -1, 0);
						aiNewState(zombieAGoto);
						break;
				}
				break;
		}
	}

	@Override
	protected void recoil(boolean chance) {
		switch ( pSprite.lotag )
		{
			case kDudeAxeZombie:
			case kDudeEarthZombie:
				aiPlaySound(1106, 2, -1); //AZOMPAIN
				if ( aiTeslaHit != 0 && pXSprite.data3 > pDudeInfo.startHealth / 3 )
					aiNewState(zombieARTesla);
				else if ( pXSprite.data3 <= pDudeInfo.startHealth / 3 )
					aiNewState(zombieARecoil);
				else
					aiNewState(zombieAFall);
				break;
				
			case kDudeAxeZombieBurning:
				aiPlaySound(1106, 2, -1);
//				aiNewState(burnGoto[ZOMBIE]); XXX
				break;
		}
		
		aiTeslaHit = 0;
	}
	
	@Override
	public void touch() { /* nothing */ }

	private void thinkPonder()
	{
		if ( pXSprite.target == -1)
		{
			aiNewState(zombieASearch);
			return;
		}

		if(!(pXSprite.target >= 0 && pXSprite.target < kMaxSprites)) 
			game.dassert("pXSprite.target >= 0 && pXSprite.target < kMaxSprites");
		
		SPRITE pTarget = sprite[pXSprite.target];
		XSPRITE pXTarget = pTarget.extra > 0 ? xsprite[pTarget.extra] : null;

		// check target
		int dx = pTarget.x - pSprite.x;
		int dy = pTarget.y - pSprite.y;

		aiChooseDirection(engine.getangle(dx, dy));

		if ( pXTarget == null || pXTarget.health == 0 )
		{
			// target is dead
			aiNewState(zombieASearch);
			return;
		}

		if ( IsPlayerSprite( pTarget ) )
		{
			PLAYER pPlayer = gPlayer[ pTarget.lotag - kDudePlayer1 ];
			if ( powerupCheck( pPlayer, kItemLtdInvisibility - kItemBase ) > 0
			|| powerupCheck( pPlayer, kItemDeathMask - kItemBase ) > 0 )
			{
				aiNewState(zombieAGoto);
				return;
			}
		}

		long dist = engine.qdist(dx, dy);
		if ( dist <= pDudeInfo.seeDist )
		{
			int nAngle = engine.getangle(dx, dy);
			int losAngle = ((kAngle180 + nAngle - pSprite.ang) & kAngleMask) - kAngle180;
			int eyeAboveZ = pDudeInfo.eyeHeight * pSprite.yrepeat << 2;

			// is there a line of sight to the target?
			if ( engine.cansee(pTarget.x, pTarget.y, pTarget.z, pTarget.sectnum,
				pSprite.x, pSprite.y, pSprite.z - eyeAboveZ, pSprite.sectnum))
			{
				// is the target visible?
				if ( klabs(losAngle) <= pDudeInfo.periphery )
				{
					setTarget(pXSprite.target);

					// check to see if we can attack
					if ( dist < kAxeZombieMeleeDist )
					{
						if ( klabs(losAngle) < kAngle15 )
						{
							aiNewState(zombieAHack);
						}
						return;
					}
				}
			}
		}
		aiNewState(zombieAChase);
	}
	
	private void HackCallback() {
		int nAngle = engine.getangle(pXSprite.targetX - pSprite.x, pXSprite.targetY - pSprite.y);

		int nZOffset1 = pDudeInfo.eyeHeight * pSprite.yrepeat << 2;
		int nZOffset2 = 0;
		if(pXSprite.target != -1)
		{
			SPRITE pTarget = sprite[pXSprite.target];
			if(IsDudeSprite(pTarget))
				nZOffset2 = dudeInfo[pTarget.lotag - kDudeBase].eyeHeight * pTarget.yrepeat << 2;
		}
		int dx = Cos(nAngle) >> 16;
		int dy = Sin(nAngle) >> 16;
		int dz = nZOffset1 - nZOffset2;
		
		sfxStart3DSound(pSprite, 1101, 1, 0); //kSfxAxeAir
		
		actFireVector(pSprite, 0, 0, dx, dy, dz, kVectorAxe);
	}

	/****************************************************************************
	** thinkSearch()
	**
	**
	****************************************************************************/
	private void thinkSearch()
	{
		aiChooseDirection(pXSprite.goalAng);
		if(aiThinkTarget()) return;
		
		if(pXSprite.state == 0) return;
		
		for (short nSprite = headspritestat[kStatDude]; nSprite >= 0; nSprite = nextspritestat[nSprite])
		{
			SPRITE pTarget = sprite[nSprite];
			if(pTarget.lotag == kDudeInnocent) {
				int dx = pTarget.x - pSprite.x;
				int dy = pTarget.y - pSprite.y;
				long dist = engine.qdist(dx, dy);
				if ( dist <= dudeInfo[45].seeDist || dist <= dudeInfo[45].hearDist )
		        {
		            setTarget( pTarget.xvel );
					activate();
					return;
		        }
			}
		}
	}
	
	/****************************************************************************
	** thinkGoto()
	**
	**
	****************************************************************************/
	private void thinkGoto()
	{
		int dx = pXSprite.targetX - pSprite.x;
		int dy = pXSprite.targetY - pSprite.y;

		int nAngle = engine.getangle(dx, dy);
		long dist = engine.qdist(dx, dy);

		aiChooseDirection(nAngle);

		// if reached target, change to search mode
		if ( dist < M2X(1.8) && klabs(pSprite.ang - nAngle) < pDudeInfo.periphery )
		{
//			System.out.println("Axe zombie " +  xsprite[pSprite.extra].reference + " switching to search mode");
			aiNewState(zombieASearch);
		}

		aiThinkTarget();
	}
	
	/****************************************************************************
	** thinkChase()
	**
	**
	****************************************************************************/
	private void thinkChase()
	{
		if ( pXSprite.target == -1)
		{
			aiNewState(zombieASearch);
			return;
		}

		if(!(pXSprite.target >= 0 && pXSprite.target < kMaxSprites)) 
			game.dassert("pXSprite.target >= 0 && pXSprite.target < kMaxSprites");
		
		SPRITE pTarget = sprite[pXSprite.target];
		XSPRITE pXTarget = pTarget.extra > 0 ? xsprite[pTarget.extra] : null;

		// check target
		int dx = pTarget.x - pSprite.x;
		int dy = pTarget.y - pSprite.y;

		aiChooseDirection(engine.getangle(dx, dy));

		if ( pXTarget == null || pXTarget.health == 0 )
		{
			// target is dead
			aiNewState(zombieASearch);
			return;
		}

		if ( IsPlayerSprite( pTarget ) )
		{
			PLAYER pPlayer = gPlayer[ pTarget.lotag - kDudePlayer1 ];
			if ( powerupCheck( pPlayer, kItemLtdInvisibility - kItemBase ) > 0
			|| powerupCheck( pPlayer, kItemDeathMask - kItemBase ) > 0 )
			{
				aiNewState(zombieAGoto);
				return;
			}
		}

		long dist = engine.qdist(dx, dy);
		if ( dist <= pDudeInfo.seeDist )
		{
			int nAngle = engine.getangle(dx, dy);
			int losAngle = ((kAngle180 + nAngle - pSprite.ang) & kAngleMask) - kAngle180;
			int eyeAboveZ = pDudeInfo.eyeHeight * pSprite.yrepeat << 2;
			
			// is there a line of sight to the target?
			if ( engine.cansee(pTarget.x, pTarget.y, pTarget.z, pTarget.sectnum,
				pSprite.x, pSprite.y, pSprite.z - eyeAboveZ, pSprite.sectnum))
			{
				// is the target visible?
				if ( klabs(losAngle) <= pDudeInfo.periphery )
				{
					setTarget(pXSprite.target);

					// check to see if we can attack
					if ( dist < kAxeZombieMeleeDist && klabs(losAngle) < kAngle15 )
					{
						aiNewState(zombieAHack);
					}
					return;
				}
			}
		}
		//System.out.println("Axe zombie " + pXSprite.reference + " lost sight of target " + pXSprite.target);
		aiNewState(zombieAGoto);
		pXSprite.target = -1;
	}
}
