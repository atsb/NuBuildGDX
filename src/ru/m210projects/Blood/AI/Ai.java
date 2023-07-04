// This file is part of BloodGDX.
// Copyright (C) 2017-2019  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// BloodGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// BloodGDX is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with BloodGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.Blood.AI;

import static ru.m210projects.Blood.AI.AIUNICULT.*;
import static ru.m210projects.Blood.AI.AIBAT.*;
import static ru.m210projects.Blood.AI.AIBEAST.*;
import static ru.m210projects.Blood.AI.AIBONEEL.*;
import static ru.m210projects.Blood.AI.AIBURN.*;
import static ru.m210projects.Blood.AI.AICALEB.*;
import static ru.m210projects.Blood.AI.AICERBERUS.*;
import static ru.m210projects.Blood.AI.AICULTIST.*;
import static ru.m210projects.Blood.AI.AIGARG.*;
import static ru.m210projects.Blood.AI.AIGHOST.*;
import static ru.m210projects.Blood.AI.AIGILLBEAST.*;
import static ru.m210projects.Blood.AI.AIHAND.*;
import static ru.m210projects.Blood.AI.AIHOUND.*;
import static ru.m210projects.Blood.AI.AIINNOCENT.*;
import static ru.m210projects.Blood.AI.AIPOD.*;
import static ru.m210projects.Blood.AI.AIRAT.*;
import static ru.m210projects.Blood.AI.AISPID.*;
import static ru.m210projects.Blood.AI.AITCHERNOBOG.*;
import static ru.m210projects.Blood.AI.AIZOMBA.*;
import static ru.m210projects.Blood.AI.AIZOMBF.*;
import static ru.m210projects.Blood.Actor.*;
import static ru.m210projects.Blood.Globals.*;
import static ru.m210projects.Blood.DB.*;
import static ru.m210projects.Blood.EVENT.*;
import static ru.m210projects.Blood.Gameutils.*;
import static ru.m210projects.Blood.Main.*;
import static ru.m210projects.Blood.PLAYER.*;
import static ru.m210projects.Blood.SOUND.*;
import static ru.m210projects.Blood.Strings.*;
import static ru.m210projects.Blood.Trig.*;
import static ru.m210projects.Blood.Types.DEMO.*;
import static ru.m210projects.Blood.Types.DudeInfo.*;
import static ru.m210projects.Blood.Types.Seq.SeqHandling.*;
import static ru.m210projects.Blood.Warp.*;
import static ru.m210projects.Blood.Weapon.*;
import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Build.Net.Mmulti.*;
import static ru.m210projects.Build.Pragmas.*;

import java.util.Arrays;
import com.badlogic.gdx.math.Vector3;
import ru.m210projects.Blood.PLAYER;
import ru.m210projects.Blood.AI.AISTATEFUNC.Type;
import ru.m210projects.Blood.Types.DudeInfo;
import ru.m210projects.Blood.Types.WeaponAim;
import ru.m210projects.Blood.Types.XSECTOR;
import ru.m210projects.Blood.Types.XSPRITE;
import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.Types.SECTOR;
import ru.m210projects.Build.Types.SPRITE;


public class Ai {
	
	public static final GeneticDude[] pDudes = new GeneticDude[kMaxXSprites];
	
	static {
		AIZOMBA.Init();
		AIRAT.Init();
		AIINNOCENT.Init();
		AIHAND.Init();
		AICULTIST.Init();
		AIZOMBF.Init();
		AIGILLBEAST.Init();
		AIBURN.Init();
		AIGARG.Init();
		AIBAT.Init();
		AISPID.Init();
		AIHOUND.Init();
		AIGHOST.Init();
		AIBONEEL.Init();
		AITCHERNOBOG.Init();
		AIPOD.Init();
		AIBEAST.Init();
		AICALEB.Init();
		AICERBERUS.Init();
		
		AIUNICULT.Init();
	}
	
	public static AISTATE genIdle;
	public static AISTATE genRecoil;
	
	public static final int LAND = 0;
	public static final int WATER = 1;
	public static final int DUCK = 2;
	
	public static final int kAIThinkMask = 3;
	public static int[] cumulDamage = new int[kMaxXSprites];	// cumulative damage per frame
	public static int[] gDudeSlope = new int[kMaxXSprites];
	public static int[] aiTeslaHit = new int[kMaxSprites];
	public static int[] aiThinkTime = new int[kMaxSprites];
	public static int[] aiActive = new int[kMaxSprites];
	public static int[] aiClock = new int[kMaxSprites];
	public static int[] aiSoundOnce = new int[kMaxSprites];
	
	public static void aiNewState( SPRITE pSprite, XSPRITE pXSprite, AISTATE pState )
	{
		DudeInfo pDudeInfo = dudeInfo[pSprite.lotag - kDudeBase];
		//System.out.println(pState.seqId +" / "+ pState.ticks);
		pXSprite.stateTimer = pState.ticks;
		pXSprite.aiState = pState;
		int seqStartId = pDudeInfo.seqStartID;
		if ( pState.seqId >= 0 ) {
			switch(pSprite.lotag){
				case kGDXDudeUniversalCultist:
				case kGDXGenDudeBurning:
					seqStartId = pXSprite.data2;			
				break;
			}
			
			if (!BuildGdx.cache.contains(seqStartId + pState.seqId, seq)) {
				//System.err.println("NO SEQ: fullSeq = "+ (seqStartId + pState.seqId)+" / "+pXSprite.data2+" / "+seqStartId);
				//System.err.println("aiNewState : NULL sequence, dudeType = " + pSprite.lotag +", seqId = " + pState.seqId+ " data1: "+pXSprite.data1);
				return;
			}
			
			seqSpawn(seqStartId + pState.seqId, SS_SPRITE, pSprite.extra, pState.callback);
			
		}

		// call the enter function if defined
		if ( pState.enter ) 
			pState.enter(pSprite, pXSprite);
	}
	
	public static boolean dudeIsImmune(SPRITE pSprite,int dmgType){
		if (dmgType < 0 || dmgType > 6) return true;
		if (dudeInfo[pSprite.lotag - kDudeBase].startDamage[dmgType] == 0) return true;
		// if dude is locked, it immune to any dmg.
		return pSprite.extra >= 0 && xsprite[pSprite.extra].Locked == 1;
	}
	
	protected static boolean CanMove( SPRITE pSprite, int nTarget, int ang, int dist )
	{
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

		if ( !FindSector(x, y, z, pSprite.sectnum) )
			return false;
		
		long floorZ = engine.getflorzofslope(foundSector, x, y);
		int nXSector = sector[foundSector].extra;
		boolean Water = false, Underwater = false, Depth = false, Crusher = false;
		XSECTOR pXSector = null;
		if(nXSector > 0)
		{
			pXSector = xsector[nXSector];
			if(pXSector.Underwater)
				Underwater = true;
			if(pXSector.Depth != 0)
				Depth = true;
			if(sector[foundSector].lotag == 618 || pXSector.damageType != 0) {
				switch(pSprite.lotag){
					case kDudeCerberus:
					case kDudeCerberus2:
						if (!dudeIsImmune(pSprite,pXSector.damageType) || IsOriginalDemo())
							Crusher = true;
						break;
					default:
						Crusher = true;
						break;
				}
			}
		}
		int nUpper = gUpperLink[foundSector];
		int nLower = gLowerLink[foundSector];
		if (nUpper >= 0) {
			if (sprite[nUpper].lotag == kStatPurge
					|| sprite[nUpper].lotag == kStatSpares) { // wrong? should be water and goo markers.
				Depth = true;
		        Water = true;
			}
		}
		if (nLower >= 0) {
			if (sprite[nLower].lotag == kStatMarker
					|| sprite[nLower].lotag == kStatFlare) // wrong? should be water and goo markers.
				Depth = true;
		}

		GetSpriteExtents(pSprite);
		switch(pSprite.lotag)
		{
		case kDudeButcher:
		case kDudeHound:
		case kDudeBrownSpider:
		case kDudeRedSpider:
		case kDudeBlackSpider:
		case kDudeMotherSpider:
		case kDudeInnocent:
		case kDudeCerberus:
		case kDudeRat:
			if ( Crusher )
	            return false;
	        if ( Depth || Underwater )
	            return false;
			return floorZ - extents_zBot <= M2Z(1.0);
			case kDudeEel:
			if ( Water || !Underwater )
	            return false;
	        if ( Underwater )
	            return true;
	        return true;
		case kDudeBat:
		case kDudeFleshGargoyle:
		case kDudeStoneGargoyle:
			if ( pSprite.clipdist > hitDist )
				return false;

			return !Depth;
			case kGDXDudeUniversalCultist:
		case kGDXGenDudeBurning:
			return (!Crusher || dudeIsImmune(pSprite, pXSector.damageType)) && ((!Water && !Underwater) || canSwim(pSprite));
			default:
			if ( Crusher )
	            return false;
			break;
		}

		return (pXSector != null && pXSector.Depth != 0) || (pXSector != null && pXSector.Underwater) || floorZ - extents_zBot <= M2Z(1.0);
	}

	protected static void aiChooseDirection( SPRITE pSprite, XSPRITE pXSprite, int ang )
	{
		if(!(pSprite.lotag >= kDudeBase && pSprite.lotag < kDudeMax))
			game.dassert("pSprite.type >= kDudeBase && pSprite.type < kDudeMax");
		
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
		if ( CanMove(pSprite, pXSprite.target, pSprite.ang + dang, avoidDist) )
			pXSprite.goalAng = (pSprite.ang + dang) & kAngleMask;
		// clear movement partially toward target?
		else if ( CanMove(pSprite, pXSprite.target, pSprite.ang + dang / 2, avoidDist) )
			pXSprite.goalAng = (pSprite.ang + dang / 2) & kAngleMask;
		else if ( CanMove(pSprite, pXSprite.target,  pSprite.ang - dang / 2, avoidDist) )
			pXSprite.goalAng = (pSprite.ang - dang / 2) & kAngleMask;
		// try turning in target direction
		else if ( CanMove(pSprite, pXSprite.target, pSprite.ang + turnTo, avoidDist) )
			pXSprite.goalAng = (pSprite.ang + turnTo) & kAngleMask;
		// clear movement straight?
		else if ( CanMove(pSprite, pXSprite.target, pSprite.ang, avoidDist) )
			pXSprite.goalAng = pSprite.ang;
		// try turning away
		else if ( CanMove(pSprite, pXSprite.target, pSprite.ang - turnTo, avoidDist) )
			pXSprite.goalAng = (pSprite.ang - turnTo) & kAngleMask;
		else 
			pXSprite.goalAng = (pSprite.ang + kAngle60) & kAngleMask;
		
		// choose dodge direction
		pXSprite.dodgeDir = Chance(0x4000) ? 1 : -1;
		
		if ( !CanMove(pSprite, pXSprite.target, pSprite.ang + kAngle90 * pXSprite.dodgeDir, 512) )
		{
			pXSprite.dodgeDir = -pXSprite.dodgeDir;
			if ( !CanMove(pSprite, pXSprite.target, pSprite.ang + kAngle90 * pXSprite.dodgeDir, 512) )
				pXSprite.dodgeDir = 0;
		}
	}
	
	protected static void aiMoveForward( SPRITE pSprite, XSPRITE pXSprite )
	{
		if(!(pSprite.lotag >= kDudeBase && pSprite.lotag < kDudeMax))
			game.dassert("pSprite.type >= kDudeBase && pSprite.type < kDudeMax");
		DudeInfo pDudeInfo = dudeInfo[pSprite.lotag - kDudeBase];

		int maxTurn = pDudeInfo.angSpeed * kFrameTicks >> 4;
		int dang = ((kAngle180 + pXSprite.goalAng - pSprite.ang) & kAngleMask) - kAngle180;
		pSprite.ang = (short)((pSprite.ang + ClipRange(dang, -maxTurn, maxTurn)) & kAngleMask);
		
		// don't move forward if trying to turn around
		if ( klabs(dang) > kAngle60 )
			return;
		
		int sin = Sin(pSprite.ang);
		int cos = Cos(pSprite.ang);
		
		int frontSpeed = pDudeInfo.frontSpeed;
		if(pSprite.lotag == kDudeBurning) {
			if(IsOriginalDemo()) //Burning Innocent original bug
				frontSpeed = 0;
		}
		
		sprXVel[pSprite.xvel] += mulscale(cos, frontSpeed, 30);
		sprYVel[pSprite.xvel] += mulscale(sin, frontSpeed, 30);
	}
	
	protected static void aiMoveTurn( SPRITE pSprite, XSPRITE pXSprite )
	{
		if(!(pSprite.lotag >= kDudeBase && pSprite.lotag < kDudeMax))
			game.dassert("pSprite.type >= kDudeBase && pSprite.type < kDudeMax");
		DudeInfo pDudeInfo = dudeInfo[pSprite.lotag - kDudeBase];

		int dang = ((kAngle180 + pXSprite.goalAng - pSprite.ang) & kAngleMask) - kAngle180;
		int maxTurn = pDudeInfo.angSpeed * kFrameTicks >> 4;

		pSprite.ang = (short)((pSprite.ang + ClipRange(dang, -maxTurn, maxTurn)) & kAngleMask);
	}
	
	protected static void aiMoveDodge( SPRITE pSprite, XSPRITE pXSprite ) { 
		if(!(pSprite.lotag >= kDudeBase && pSprite.lotag < kDudeMax))
			game.dassert("pSprite.type >= kDudeBase && pSprite.type < kDudeMax");
		DudeInfo pDudeInfo = dudeInfo[pSprite.lotag - kDudeBase];

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
	
	public static void aiActivateDude( SPRITE pSprite, XSPRITE pXSprite )
	{
		if(!(pSprite.lotag >= kDudeBase && pSprite.lotag < kDudeMax))
			game.dassert("pSprite.type >= kDudeBase && pSprite.type < kDudeMax");

		if ( pXSprite.state == 0 )
		{
			// this doesn't take into account sprites triggered w/o a target location....
			int nAngle = engine.getangle(pXSprite.targetX - pSprite.x, pXSprite.targetY - pSprite.y);
			aiChooseDirection(pSprite, pXSprite, nAngle);
			pXSprite.state = 1;
		}

		switch ( pSprite.lotag ) 
		{
			case kDudePhantasm:
				aiActive[pSprite.xvel] = 1;
				aiThinkTime[pSprite.xvel] = 0;
				if (pXSprite.target == -1)
					aiNewState(pSprite, pXSprite, ghostSearch);
				else {
					aiPlaySound(pSprite, 1600, 1, -1);
					aiNewState(pSprite, pXSprite, ghostChase);
				} 
		    	break;
		    	
			case kDudeTommyCultist:
		    case kDudeShotgunCultist:
		    case kDudeBeastCultist:
		    case kDudeTeslaCultist:
		    case kDudeDynamiteCultist:
		    	aiActive[pSprite.xvel] = 1;
		    	if (pXSprite.target == -1) {
		    		if(pXSprite.palette != 0 && pXSprite.palette <= 2)
						aiNewState(pSprite, pXSprite, cultistSearch[WATER]);
					else {
						aiNewState(pSprite, pXSprite, cultistSearch[LAND]);	
						if(Chance(0x4000))
						{
							if(pSprite.lotag == kDudeTommyCultist)
								aiPlaySound(pSprite, Random(5) + 4008, 1, -1);
							else aiPlaySound(pSprite, Random(5) + 1008, 1, -1);
						}
					}
		    	}
				else {
					if(Chance(0x4000))
					{
						if(pSprite.lotag == kDudeTommyCultist)
							aiPlaySound(pSprite, Random(5) + 4003, 1, -1);
						else aiPlaySound(pSprite, Random(5) + 1003, 1, -1);
					}
					if(pXSprite.palette != 0 && pXSprite.palette <= 2)
						aiNewState(pSprite, pXSprite, cultistChase[WATER]);
					else {
						if(pSprite.lotag == kDudeTommyCultist)
							aiNewState(pSprite, pXSprite, cultistTurn);	
						else
							aiNewState(pSprite, pXSprite, cultistChase[LAND]);	
					}
				}
		    	break;
		    case kGDXDudeUniversalCultist:
		    	aiActive[pSprite.xvel] = 1;
		    	if (pXSprite.target == -1) {
		    		if(spriteIsUnderwater(pSprite,false))
						aiNewState(pSprite, pXSprite, GDXGenDudeSearch[WATER]);
					else {
						aiNewState(pSprite, pXSprite, GDXGenDudeSearch[LAND]);	
						if(Chance(0x4000))
							sfxPlayGDXGenDudeSound(pSprite,0);
					}
		    	} else {
					if(Chance(0x4000))
						sfxPlayGDXGenDudeSound(pSprite,0);
					
					if(spriteIsUnderwater(pSprite,false))
						aiNewState(pSprite, pXSprite, GDXGenDudeChase[WATER]);
					else 
						//aiNewState(pSprite, pXSprite, GDXGenDudeTurn);
						aiNewState(pSprite, pXSprite, GDXGenDudeChase[LAND]);
					
				}
				break;
		    case kDudeFanaticProne:
		    	pSprite.lotag = kDudeTommyCultist;
		    	aiActive[pSprite.xvel] = 1;
		    	if (pXSprite.target == -1) {
		    		if(pXSprite.palette != 0 && pXSprite.palette <= 2)
						aiNewState(pSprite, pXSprite, cultistSearch[WATER]);
					else {
						aiNewState(pSprite, pXSprite, cultistSearch[LAND]);	
						if(Chance(0x4000))
							aiPlaySound(pSprite, Random(5) + 4008, 1, -1);
					}
		    	}
				else {
					if(Chance(0x4000))
						aiPlaySound(pSprite, Random(5) + 4008, 1, -1);
					if(pXSprite.palette == 1|| pXSprite.palette == 2)
						aiNewState(pSprite, pXSprite, cultistChase[WATER]);
					else 
						aiNewState(pSprite, pXSprite, cultistChase[DUCK]);	
				}
		    	break;
		    	
		    case kDudeCultistProne:
		    	pSprite.lotag = kDudeShotgunCultist;
		    	aiActive[pSprite.xvel] = 1;
		    	if (pXSprite.target == -1) {
		    		if(pXSprite.palette != 0 && pXSprite.palette <= 2)
						aiNewState(pSprite, pXSprite, cultistSearch[WATER]);
					else {
						aiNewState(pSprite, pXSprite, cultistSearch[LAND]);	
						if(Chance(0x4000))
							aiPlaySound(pSprite, Random(5) + 1008, 1, -1);
					}
		    	}
				else {
					if(Chance(0x4000))
						aiPlaySound(pSprite, Random(5) + 1003, 1, -1);
					if(pXSprite.palette == 1|| pXSprite.palette == 2)
						aiNewState(pSprite, pXSprite, cultistChase[WATER]);
					else 
						aiNewState(pSprite, pXSprite, cultistChase[DUCK]);	
				}
		    	break;
		    	
		    case kDudeCultistBurning:
				if (pXSprite.target == -1) 
					aiNewState(pSprite, pXSprite, burnSearch[CULTIST]);
				else 
					aiNewState(pSprite, pXSprite, burnChase[CULTIST]);
				break;
			
			case kGDXGenDudeBurning:
				if (pXSprite.target == -1) 
					aiNewState(pSprite, pXSprite, burnSearch[GDXGENDUDE]);
				else 
					aiNewState(pSprite, pXSprite, burnChase[GDXGENDUDE]);
				break;
				
		    case kDudeBat:
				aiActive[pSprite.xvel] = 1;
				aiThinkTime[pSprite.xvel] = 0;
				if ( pSprite.hitag == 0 )
					pSprite.hitag = (kAttrMove | kAttrAiming);
				if (pXSprite.target == -1) 
					aiNewState(pSprite, pXSprite, batSearch);
				else {
					if ( Chance(0x5000) )
				          aiPlaySound(pSprite, 2000, 1, -1);
					aiNewState(pSprite, pXSprite, batChase);
				}
				break;
				
		    case kDudeEel:
				aiActive[pSprite.xvel] = 1;
				aiThinkTime[pSprite.xvel] = 0;
				if (pXSprite.target == -1)
					aiNewState(pSprite, pXSprite, boneelSearch);
				else {
					if(Chance(0x4000))
						aiPlaySound(pSprite, 1501, 1, -1);
					else aiPlaySound(pSprite, 1500, 1, -1);
					aiNewState(pSprite, pXSprite, boneelChase);
				}
				break;
				
		    case kDudeGillBeast:
				aiActive[pSprite.xvel] = 1;
				aiThinkTime[pSprite.xvel] = 0;
				XSECTOR pXSector = sector[pSprite.sectnum].extra > 0 ? xsector[sector[pSprite.sectnum].extra] : null;
				if (pXSprite.target == -1) {
					if(pXSector != null && pXSector.Underwater)
						aiNewState(pSprite, pXSprite, gillBeastSearch[WATER]);
					else aiNewState(pSprite, pXSprite, gillBeastSearch[LAND]);
				} 
				else
				{
					if(Chance(0x2000)) 
						aiPlaySound(pSprite, 1701, 1, -1);
					else aiPlaySound(pSprite, 1700, 1, -1);
					if(pXSector != null && pXSector.Underwater)
						aiNewState(pSprite, pXSprite, gillBeastChase[WATER]);
					else aiNewState(pSprite, pXSprite, gillBeastChase[LAND]);
				}
				break;
			case kDudeAxeZombie:
				aiThinkTime[pSprite.xvel] = 1;
				if ( pXSprite.target == -1 )
					aiNewState(pSprite, pXSprite, zombieASearch);
				else
				{
					if(!Chance(0x5000))
						aiNewState(pSprite, pXSprite, zombieAChase);
					else {
						aiPlaySound(pSprite, Random(3) + 1103, 1, -1);
						aiNewState(pSprite, pXSprite, zombieAChase);
					}
				}
				break;
	
			case kDudeEarthZombie:
				aiThinkTime[pSprite.xvel] = 1;
				if (pXSprite.aiState == zombieEIdle)
					aiNewState(pSprite, pXSprite, zombieEUp);
				break;
				
			case kDudeSleepZombie:
				aiThinkTime[pSprite.xvel] = 1;
				if (pXSprite.aiState == zombieSLIdle)
					aiNewState(pSprite, pXSprite, zombieSLUP);
				break;
				
			case kDudeButcher:
				aiThinkTime[pSprite.xvel] = 1;
				if (pXSprite.target == -1)
					aiNewState(pSprite, pXSprite, zombieFSearch);
				else {
					if(Chance(0x4000))
						aiPlaySound(pSprite, 1201, 1, -1);
					else aiPlaySound(pSprite, 1200, 1, -1);
					aiNewState(pSprite, pXSprite, zombieFChase);
				}
				break;
				
			case kDudeAxeZombieBurning:
				if (pXSprite.target == -1) 
					aiNewState(pSprite, pXSprite, burnSearch[ZOMBIE]);
				else 
					aiNewState(pSprite, pXSprite, burnChase[ZOMBIE]);
				break;
				
			case kDudeTinyCalebburning:
				if (pXSprite.target == -1) 
					aiNewState(pSprite, pXSprite, burnSearch[TINYCALEB]);
				else 
					aiNewState(pSprite, pXSprite, burnChase[TINYCALEB]);
				break;
				
			case kDudeBloatedButcherBurning:
				if (pXSprite.target == -1) 
					aiNewState(pSprite, pXSprite, burnSearch[BUTCHER]);
				else 
					aiNewState(pSprite, pXSprite, burnChase[INNOCENT]);
				break;
				
			case kDudeFleshGargoyle:
			case kDudeStoneGargoyle:
				aiActive[pSprite.xvel] = 1;
				aiThinkTime[pSprite.xvel] = 0;
				if (pXSprite.target == -1) 
					aiNewState(pSprite, pXSprite, gargoyleSearch);
				else {
					aiNewState(pSprite, pXSprite, gargoyleChase);
				
					if(pSprite.lotag == kDudeFleshGargoyle)
					{
						if(Chance(0x4000)) 
							aiPlaySound(pSprite, 1401, 1, -1);
						else aiPlaySound(pSprite, 1400, 1, -1);
					} else
					{
						if(Chance(0x4000)) 
							aiPlaySound(pSprite, 1451, 1, -1);
						else aiPlaySound(pSprite, 1450, 1, -1);
					}
				}
				break;
			case kDudeFleshStatue:
			case kDudeStoneStatue:

				if(IsOriginalDemo() || pXSprite.data1 != 1) 
				{
					if(Chance(0x4000)) 
						aiPlaySound(pSprite, 1401, 1, -1);
					else aiPlaySound(pSprite, 1400, 1, -1);
					
					if(pSprite.lotag == kDudeFleshStatue)
						aiNewState(pSprite, pXSprite, statueFChase);
					else
						aiNewState(pSprite, pXSprite, statueSChase);
				} else {
					if(pSprite.lotag == kDudeFleshStatue)
						aiNewState(pSprite, pXSprite, statueFTransformNew);
					else
						aiNewState(pSprite, pXSprite, statueSTransformNew);
				}
					
				break;
			case kDudeCerberus:
				if (pXSprite.target == -1) 
					aiNewState(pSprite, pXSprite, cerberusSearch[C1]);
				else { 
					aiPlaySound(pSprite, 2300, 1, -1);
					aiNewState(pSprite, pXSprite, cerberusChase[C1]);
				}
				break;
			case kDudeCerberus2:
				if (pXSprite.target == -1) 
					aiNewState(pSprite, pXSprite, cerberusSearch[C2]);
				else { 
					aiPlaySound(pSprite, 2300, 1, -1);
					aiNewState(pSprite, pXSprite, cerberusChase[C2]);
				}
				break;	
				
			case kDudeHound:
				if (pXSprite.target == -1) 
					aiNewState(pSprite, pXSprite, houndSearch);
				else  {
					aiPlaySound(pSprite, 1300, 1, -1);
					aiNewState(pSprite, pXSprite, houndChase);
				}
				break;
				
			case kDudeHand:
				if (pXSprite.target == -1)
					aiNewState(pSprite, pXSprite, handSearch);
				else {
					aiPlaySound(pSprite, 1900, 1, -1);
					aiNewState(pSprite, pXSprite, handChase);
				}
				break;	
	
			case kDudeRat:
				if (pXSprite.target == -1)
					aiNewState(pSprite, pXSprite, ratSearch);
				else {
					aiPlaySound(pSprite, 2100, 1, -1);
					aiNewState(pSprite, pXSprite, ratChase);
				}
				break;

			case kDudeInnocent:
				if (pXSprite.target == -1)
					aiNewState(pSprite, pXSprite, innocentSearch);
				else {
					if(pXSprite.health > 0)
						aiPlaySound(pSprite, BiRandom(6) + 7000, 1, -1); //ICRYING
					aiNewState(pSprite, pXSprite, innocentChase);
				}
				break;
				
			case kDudeTchernobog:
				if (pXSprite.target == -1)
					aiNewState(pSprite, pXSprite, tchernobogSearch);
				else {
					aiPlaySound(pSprite, Random(7) + 2350, 1, -1);
					aiNewState(pSprite, pXSprite, tchernobogChase);
				}
				break;

			case kDudeBrownSpider:
			case kDudeRedSpider:
			case kDudeBlackSpider:
			case kDudeMotherSpider:
				if(pSprite.lotag == kDudeMotherSpider) 
					 aiActive[pSprite.xvel] = 1;
				pSprite.cstat &= ~kSpriteFlipY;
				pSprite.hitag |= kAttrGravity;
				if (pXSprite.target == -1) 
					aiNewState(pSprite, pXSprite, spidSearch);
				else {
				    if(pSprite.lotag != kDudeMotherSpider) 
				    	aiPlaySound(pSprite, 1800, 1, -1);
				    else aiPlaySound(pSprite, Random(1) + 1853, 1, -1);
					aiNewState(pSprite, pXSprite, spidChase);
				}
				break;
				
			case kDudeTinyCaleb:
				aiThinkTime[pSprite.xvel] = 1;
				if (pXSprite.target == -1) {
		    		if(pXSprite.palette == 1 || pXSprite.palette == 2)
						aiNewState(pSprite, pXSprite, calebSearch[WATER]);
					else {
						aiNewState(pSprite, pXSprite, calebSearch[LAND]);	
					}
		    	}
				else {
					if(pXSprite.palette == 1 || pXSprite.palette == 2)
						aiNewState(pSprite, pXSprite, calebChase[WATER]);
					else 
						aiNewState(pSprite, pXSprite, calebChase[LAND]);	
				}
				break;
			case kDudeTheBeast:
				if (pXSprite.target == -1) {
		    		if(pXSprite.palette == 1 || pXSprite.palette == 2)
						aiNewState(pSprite, pXSprite, beastSearch[WATER]);
					else {
						aiNewState(pSprite, pXSprite, beastSearch[LAND]);	
					}
		    	}
				else {
					aiPlaySound(pSprite, Random(2) + 9009, 1, -1);
					if(pXSprite.palette == 1 || pXSprite.palette == 2)
						aiNewState(pSprite, pXSprite, beastChase[WATER]);
					else 
						aiNewState(pSprite, pXSprite, beastChase[LAND]);
				}
				break;
			case kDudeGreenPod:
			case kDudeFirePod:
				if (pXSprite.target == -1)
					aiNewState(pSprite, pXSprite, podSearch);
				else {
					if(pSprite.lotag == kDudeFirePod)
						aiPlaySound(pSprite, 2453, 1, -1);
					else aiPlaySound(pSprite, 2473, 1, -1);
					
					aiNewState(pSprite, pXSprite, podChase);
				}
				break;   
			case kDudeGreenTentacle:
			case kDudeFireTentacle:
				if (pXSprite.target == -1)
					aiNewState(pSprite, pXSprite, tentacleSearch);
				else {
					aiPlaySound(pSprite, 2503, 1, -1);
					aiNewState(pSprite, pXSprite, tentacleChase);
				}
				break;
		}
	}
	
	/*******************************************************************************
	FUNCTION:		aiSetTarget()

	DESCRIPTION:	Target a location (as opposed to a sprite)
	*******************************************************************************/
	public static void aiSetTarget( XSPRITE pXSprite, int x, int y, int z )
	{
		pXSprite.target = -1;
		pXSprite.targetX = x;
		pXSprite.targetY = y;
		pXSprite.targetZ = z;
	}
	
	public static void aiSetTarget( XSPRITE pXSprite, int nTarget )
	{
		
		if(!(nTarget >= 0 && nTarget < kMaxSprites)) 
			game.dassert("nTarget >= 0 && nTarget < kMaxSprites");
		SPRITE pTarget = sprite[nTarget];
		if ( pTarget.lotag < kDudeBase || pTarget.lotag >= kDudeMax )
			return;

		if ( nTarget == sprite[pXSprite.reference].owner )
		{
			return;
		}

		DudeInfo pTargetInfo = dudeInfo[pTarget.lotag - kDudeBase];

		pXSprite.target = nTarget;
		pXSprite.targetX = pTarget.x;
		pXSprite.targetY = pTarget.y;
		pXSprite.targetZ = pTarget.z - (pTargetInfo.eyeHeight * pTarget.yrepeat << 2);
	}

	public static int aiDamageSprite( SPRITE pSprite, XSPRITE pXSprite, int nSource, int nDamageType, int nDamage )
	{
		if(!(nSource < kMaxSprites)) game.dassert("nSource < kMaxSprites");
		
		if (pXSprite.health == 0)
			return 0;

		pXSprite.health = ClipLow(pXSprite.health - nDamage, 0);
		cumulDamage[pSprite.extra] += nDamage;	// add to cumulative damage

		DudeInfo pDudeInfo = dudeInfo[pSprite.lotag - kDudeBase];
		
		if (nSource >= 0) {
			if(nSource == pXSprite.reference)
				return 0;
			
			if (pXSprite.target == -1)
			{
				// give a dude a target
				aiSetTarget(pXSprite, nSource);
				aiActivateDude(pSprite, pXSprite);
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
					aiSetTarget(pXSprite, nSource);
					aiActivateDude(pSprite, pXSprite);
				}
			}

			if ( nDamageType == 6 )
				aiTeslaHit[pXSprite.reference] = 1;
			else {
				if(!IsOriginalDemo())
					aiTeslaHit[pXSprite.reference] = 0;
			}

			// you DO need special processing here or somewhere else (your choice) for dodging
			switch ( pSprite.lotag )
			{
				case kDudeFleshGargoyle:
					aiNewState(pSprite, pXSprite, gargoyleChase);
					break;
				case kDudeCultistBurning:
					if(Chance(0x2000) && gFrameClock > aiClock[pSprite.xvel])
					{
						aiPlaySound(pSprite, Random(2) + 1031, 2, -1);
						aiClock[pSprite.xvel] = gFrameClock + 360; 
					}
					boolean tommyChance = Chance(0x300);
					if(pXSprite.palette == 1 || pXSprite.palette == 2)
					{
						if(tommyChance)
							pSprite.lotag = kDudeTommyCultist;
						else
							pSprite.lotag = kDudeShotgunCultist;
						pXSprite.burnTime = 0;
		                aiNewState(pSprite, pXSprite, cultistGoto[WATER]);
					}
					break;
				
				case kGDXGenDudeBurning:
					if(Chance(0x2000) && gFrameClock > aiClock[pSprite.xvel]){
						sfxPlayGDXGenDudeSound(pSprite,3);
						aiClock[pSprite.xvel] = gFrameClock + 360; 
					}
					
					if (pXSprite.burnTime <= 0) pXSprite.burnTime = 2400;
					if(spriteIsUnderwater(pSprite,false)){

						pSprite.lotag = kGDXDudeUniversalCultist;
						pXSprite.burnTime = 0;
		                aiNewState(pSprite, pXSprite, GDXGenDudeGoto[WATER]);
					}
					break;
					
				case kDudeInnocent:
					if ( nDamageType == 1 && pXSprite.health <= pDudeInfo.fleeHealth )
				    {
				    	aiPlaySound(pSprite, 361, 0, -1);
				    	pSprite.lotag = kDudeBurning;
				    	aiClock[pSprite.xvel] = gFrameClock + 360;
				    	aiNewState(pSprite, pXSprite, burnGoto[CULTIST]);
					  	actHealDude(pXSprite, dudeInfo[39].startHealth, dudeInfo[39].startHealth);
					  	if(!IsOriginalDemo())
					  		if(pXSprite.burnTime == 0) pXSprite.burnTime = 1200;
					  	checkEventList(pXSprite.reference, SS_SPRITE, 0);
				    }
					break;
				case kDudeBeastCultist:
					if ( pXSprite.health <= pDudeInfo.fleeHealth )
				    {
				    	aiPlaySound(pSprite, 9008, 0, -1);
				    	pSprite.lotag = kDudeTheBeast;
				    	aiNewState(pSprite, pXSprite, beastTransforming);
					  	actHealDude(pXSprite, dudeInfo[51].startHealth, dudeInfo[51].startHealth);
				    }
					break;
				case kDudeTinyCaleb:
					if ( nDamageType == 1 && pXSprite.health <= pDudeInfo.fleeHealth )
				    {
				    	aiPlaySound(pSprite, 361, 0, -1);
				    	if(!IsOriginalDemo()) {
					  		if(pXSprite.burnTime == 0) pXSprite.burnTime = 1200;
					  		pSprite.lotag = kDudeTinyCalebburning;
					    	aiNewState(pSprite, pXSprite, burnGoto[TINYCALEB]);
					    	actHealDude(pXSprite, dudeInfo[51].startHealth, dudeInfo[51].startHealth);
				    	} 
				    	else
				    	{
				    		pSprite.lotag = kDudeBurning;
					    	aiNewState(pSprite, pXSprite, burnGoto[CULTIST]);
					    	actHealDude(pXSprite, dudeInfo[39].startHealth, dudeInfo[39].startHealth);
				    	}
				    	aiClock[pSprite.xvel] = gFrameClock + 360;
					  	checkEventList(pXSprite.reference, SS_SPRITE, 0);
				    }
					break;
				case kGDXDudeUniversalCultist:
					if (nDamageType == kDamageBurn) {
						if (pXSprite.health <= pDudeInfo.fleeHealth) {
							if (getNextIncarnation(pXSprite) == null) {
								removeDudeStuff(pSprite);	
								if (pXSprite.data1 >= kThingHiddenExploder && pXSprite.data1 < (kThingHiddenExploder + kExplodeMax) -1) {
									doExplosion(pSprite,pXSprite.data1 - kThingHiddenExploder);
								}
			
								if(spriteIsUnderwater(pSprite,false)) {
									pXSprite.health = 0;
									break;
								}
								
								if ((BuildGdx.cache.contains(pXSprite.data2 + 15,seq) || BuildGdx.cache.contains(pXSprite.data2 + 16,seq)) && BuildGdx.cache.contains(pXSprite.data2 + 3,seq)) {
	
										aiPlaySound(pSprite, 361, 0, -1);
										sfxPlayGDXGenDudeSound(pSprite,3);					
	
										pSprite.lotag = kGDXGenDudeBurning;
										
				                        if (pXSprite.data2 == 11520) // don't inherit palette for burning if using default animation
				                              pSprite.pal = 12;
										
										aiNewState(pSprite, pXSprite, burnGoto[CULTIST]);
										actHealDude(pXSprite, dudeInfo[55].startHealth, dudeInfo[55].startHealth);
										aiClock[pSprite.xvel] = gFrameClock + 360;
										checkEventList(pXSprite.reference, SS_SPRITE, 0);
								}
							} else {
								actKillSprite(nSource,pSprite,nDamageType,nDamage);
							}
						}
					    
					} else if (!inDodge(pXSprite.aiState)) {
							
						int chance3 = getDodgeChance(pSprite);
						if (Chance(chance3) || inIdle(pXSprite.aiState)) {
							//System.err.println("DODGE ON: "+chance3);
							if(!spriteIsUnderwater(pSprite,false)) {
								if(!canDuck(pSprite) || !checkUniCultistSeq(pSprite, 14)) aiNewState(pSprite, pXSprite, GDXGenDudeDodgeDmg[LAND]);
								else aiNewState(pSprite, pXSprite, GDXGenDudeDodgeDmg[DUCK]);
								
								if (Chance(0x0200))
									sfxPlayGDXGenDudeSound(pSprite, 1);
								
							} else if(checkUniCultistSeq(pSprite, 13)) 
								aiNewState(pSprite, pXSprite, GDXGenDudeDodgeDmg[WATER]);
						}
					}
					break;
					
				case kDudeTommyCultist:
				case kDudeShotgunCultist:
				case kDudeTeslaCultist:
				case kDudeDynamiteCultist:
					if ( nDamageType == kDamageBurn ) {
						if( pXSprite.health <= pDudeInfo.fleeHealth )
					    {
							if(!IsOriginalDemo()) {
								if(pXSprite.palette != 0) {
									pXSprite.health = 0;
									break;
								}
								if(pXSprite.burnTime == 0) pXSprite.burnTime = 1200;
							}
								
					    	aiPlaySound(pSprite, 361, 0, -1);
					    	aiPlaySound(pSprite, 1031 + Random(2), 2, -1);
					    	pSprite.lotag = kDudeCultistBurning;
					    	aiNewState(pSprite, pXSprite, burnGoto[CULTIST]);
						  	actHealDude(pXSprite, dudeInfo[40].startHealth, dudeInfo[40].startHealth);
						  	aiClock[pSprite.xvel] = gFrameClock + 360;
						  	checkEventList(pXSprite.reference, SS_SPRITE, 0);
					    }
					} 
					else
					{
						if(pXSprite.palette == 0) {
							if(!checkDudeSeq(pSprite, 14))
								aiNewState(pSprite, pXSprite, cultistDodge[LAND]);
							else aiNewState(pSprite, pXSprite, cultistDodge[DUCK]);
						} 
						else if(checkDudeSeq(pSprite, 13) && (pXSprite.palette == 1 || pXSprite.palette == 2))
							aiNewState(pSprite, pXSprite, cultistDodge[WATER]);
					}
					
					break;
				case kDudeAxeZombie:
				case kDudeEarthZombie:
					if ( nDamageType == kDamageBurn && pXSprite.health <= pDudeInfo.fleeHealth )
				    {
				    	aiPlaySound(pSprite, 361, 0, -1);
				    	aiPlaySound(pSprite, 1106, 2, -1);
				    	pSprite.lotag = kDudeAxeZombieBurning;
				    	aiNewState(pSprite, pXSprite, burnGoto[ZOMBIE]);
				    	if(!IsOriginalDemo())
					  		if(pXSprite.burnTime == 0) pXSprite.burnTime = 1200;
					  	actHealDude(pXSprite, dudeInfo[41].startHealth, dudeInfo[41].startHealth);
					  	checkEventList(pXSprite.reference, SS_SPRITE, 0);
				    }
					break;
				case kDudeButcher:
					if ( nDamageType == 1 && pXSprite.health <= pDudeInfo.fleeHealth )
				    {
				    	aiPlaySound(pSprite, 361, 0, -1);
				    	aiPlaySound(pSprite, 1202, 2, -1);
				    	
				    	pSprite.lotag = kDudeBloatedButcherBurning;
				    	aiNewState(pSprite, pXSprite, burnGoto[BUTCHER]);
				    	if(!IsOriginalDemo())
					  		if(pXSprite.burnTime == 0) pXSprite.burnTime = 1200;
					  	actHealDude(pXSprite, dudeInfo[42].startHealth, dudeInfo[42].startHealth);
					  	checkEventList(pXSprite.reference, SS_SPRITE, 0);
				    }
					break;
			}
		}
		
		return nDamage;
	}
	
	protected static void RecoilDude( SPRITE pSprite, XSPRITE pXSprite ) 
	{
		boolean chance = Chance(0x4000);
		if(pSprite.statnum != kStatDude || !IsDudeSprite(pSprite))
			return;
		
		switch ( pSprite.lotag )
		{
			case kGDXDudeUniversalCultist:
			
				int mass = getDudeMassBySpriteSize(pSprite); int chance4 = getRecoilChance(pSprite); boolean chance3 = Chance(chance4);
				if (aiTeslaHit[pXSprite.reference] != 0 && (inIdle(pXSprite.aiState) || mass < 155 || (mass >= 155 && chance3)) && !spriteIsUnderwater(pSprite,false)) {
					sfxPlayGDXGenDudeSound(pSprite,1);
					
					if (BuildGdx.cache.contains(pXSprite.data2 + 4,seq)) {
						GDXGenDudeRTesla.next = (Chance(chance4 * 2) ?  GDXGenDudeDodge[LAND] : GDXGenDudeDodgeDmg[LAND]);
						aiNewState(pSprite, pXSprite, GDXGenDudeRTesla);
					}
					else if(canDuck(pSprite) && (Chance(chance4) || pGameInfo.nDifficulty == 0 )) aiNewState(pSprite, pXSprite, GDXGenDudeRecoil[DUCK]);
					else if (canSwim(pSprite) && spriteIsUnderwater(pSprite,false)) aiNewState(pSprite, pXSprite, GDXGenDudeRecoil[WATER]);
					else aiNewState(pSprite, pXSprite, GDXGenDudeRecoil[LAND]);
					break;
				}
				
				
				if (inDodge(pXSprite.aiState)) {
					//System.out.println("!!!!!!! DODGING");
					sfxPlayGDXGenDudeSound(pSprite,1);
					break;
				}
				
				if (chance3 || inIdle(pXSprite.aiState) || Chance(getRecoilChance(pSprite)) || (!dudeIsMelee(pXSprite) && mass < 155)) {
					
					//System.out.println("RECOIL ON: "+chance3);
					sfxPlayGDXGenDudeSound(pSprite,1);
					
					if(canDuck(pSprite) && (Chance(chance4) || pGameInfo.nDifficulty == 0 )) aiNewState(pSprite, pXSprite, GDXGenDudeRecoil[DUCK]);
					else if (canSwim(pSprite) && spriteIsUnderwater(pSprite,false)) aiNewState(pSprite, pXSprite, GDXGenDudeRecoil[WATER]);
					else aiNewState(pSprite, pXSprite, GDXGenDudeRecoil[LAND]);
				}

				break;
			case kDudeTommyCultist:
			case kDudeShotgunCultist:
			case kDudeTeslaCultist:
			case kDudeDynamiteCultist:
			case kDudeBeastCultist:
				if(pSprite.lotag == kDudeTommyCultist)
					aiPlaySound(pSprite, Random(2) + 4013, 2, -1);
				else aiPlaySound(pSprite, Random(2) + 1013, 2, -1);

				/*
				if ( chance || pXSprite.palette != 0 )
		          {
		            if ( !chance || pXSprite.palette != 0 )
		            {
		            	if ( pXSprite.palette == 1 || pXSprite.palette == 2 ) {
							aiNewState(pSprite, pXSprite, cultistRecoil[WATER]);
							aiTeslaHit[pXSprite.reference] = 0;
							return;
		            	}
		            	
		            	if ( aiTeslaHit[pXSprite.reference] != 0)
						{
							aiNewState(pSprite, pXSprite, cultistRTesla);
							aiTeslaHit[pXSprite.reference] = 0;
							return;
						}
		            }
		            else
		            {
		            	if ( aiTeslaHit[pXSprite.reference] != 0)
						{
							aiNewState(pSprite, pXSprite, cultistRTesla);
							aiTeslaHit[pXSprite.reference] = 0;
							return;
						}
		            	if ( pGameInfo.nDifficulty > 0 )
		            	{
		            		aiNewState(pSprite, pXSprite, cultistRecoil[LAND]);
		            		aiTeslaHit[pXSprite.reference] = 0;
		            		return;
		            	}
		            }
		          }
		          else if ( aiTeslaHit[pXSprite.reference] != 0)
					{
						aiNewState(pSprite, pXSprite, cultistRTesla);
						aiTeslaHit[pXSprite.reference] = 0;
						return;
					}
				aiNewState(pSprite, pXSprite, cultistRecoil[DUCK]);
				aiTeslaHit[pXSprite.reference] = 0;
				*/
		
				
				if ( pXSprite.palette == 0 && aiTeslaHit[pXSprite.reference] != 0)
				{
					aiNewState(pSprite, pXSprite, cultistRTesla);
					aiTeslaHit[pXSprite.reference] = 0;
					return;
				}
				if ( pXSprite.palette == 1 || pXSprite.palette == 2 )
					aiNewState(pSprite, pXSprite, cultistRecoil[WATER]);
				else {
//					if(!chance)
//						aiNewState(pSprite, pXSprite, cultistRecoil[DUCK]);
//					else 
//						if(pGameInfo.nDifficulty > 0) 
//							aiNewState(pSprite, pXSprite, cultistRecoil[LAND]);
//					else 
//						aiNewState(pSprite, pXSprite, cultistRecoil[DUCK]);
					
					if(!chance || ( chance && pGameInfo.nDifficulty == 0 )) 
						aiNewState(pSprite, pXSprite, cultistRecoil[DUCK]);
					else aiNewState(pSprite, pXSprite, cultistRecoil[LAND]);
				}

				break;
			case kDudeCultistBurning:
				aiNewState(pSprite, pXSprite, burnGoto[CULTIST]);
				break;
			
			case kGDXGenDudeBurning:
				aiNewState(pSprite, pXSprite, burnGoto[GDXGENDUDE]);
				break;
				
			case kDudeTinyCalebburning:
				aiNewState(pSprite, pXSprite, burnGoto[TINYCALEB]);
				break;
				
			case kDudeButcher:
				aiPlaySound(pSprite, 1202, 2, -1);
				if ( aiTeslaHit[pXSprite.reference] != 0 )
					aiNewState(pSprite, pXSprite, zombieFRTesla);
				else
					aiNewState(pSprite, pXSprite, zombieFRecoil);
				break;
				
			case kDudeAxeZombie:
			case kDudeEarthZombie:
				aiPlaySound(pSprite, 1106, 2, -1); //AZOMPAIN
				if ( aiTeslaHit[pXSprite.reference] != 0 && pXSprite.data3 > dudeInfo[3].startHealth / 3 )
					aiNewState(pSprite, pXSprite, zombieARTesla);
				else if ( pXSprite.data3 <= dudeInfo[3].startHealth / 3 )
					aiNewState(pSprite, pXSprite, zombieARecoil);
				else
					aiNewState(pSprite, pXSprite, zombieAFall);
				break;
				
			case kDudeAxeZombieBurning:
				aiPlaySound(pSprite, 1106, 2, -1);
				aiNewState(pSprite, pXSprite, burnGoto[ZOMBIE]);
				break;
				
			case kDudeBloatedButcherBurning:
				aiPlaySound(pSprite, 1202, 2, -1);
				aiNewState(pSprite, pXSprite, burnGoto[BUTCHER]);
				break;
				
			case kDudeFleshGargoyle:
			case kDudeStoneGargoyle:
				aiPlaySound(pSprite, 1402, 2, -1);
				aiNewState(pSprite, pXSprite, gargoyleRecoil);
				break;
				
			case kDudeCerberus:
				aiPlaySound(pSprite, Random(2) + 2302, 2, -1);
				if ( aiTeslaHit[pXSprite.reference] != 0 && pXSprite.data3 > dudeInfo[3].startHealth / 3 )
					aiNewState(pSprite, pXSprite, cerberusRTesla);
				else
					aiNewState(pSprite, pXSprite, cerberusRecoil[C1]);
				break;
			case kDudeCerberus2:
				aiPlaySound(pSprite, Random(2) + 2302, 2, -1);	
				aiNewState(pSprite, pXSprite, cerberusRecoil[C2]);
				break;		
			
			case kDudeHound:
				aiPlaySound(pSprite, 1302, 2, -1);
				if ( aiTeslaHit[pXSprite.reference] != 0 )
					aiNewState(pSprite, pXSprite, houndRTesla);
				else
					aiNewState(pSprite, pXSprite, houndRecoil);
				break;
			case kDudeTchernobog:
				aiPlaySound(pSprite, Random(6) + 2370, 2, -1);	
				aiNewState(pSprite, pXSprite, tchernobogRecoil);
				break;
				
			case kDudeHand:
				aiPlaySound(pSprite, 1902, 2, -1);
			    aiNewState(pSprite, pXSprite, handRecoil);
			    break;

			case kDudeRat:
				aiPlaySound(pSprite, 2102, 2, -1);
				aiNewState(pSprite, pXSprite, ratRecoil);
				break;
			      
			case kDudeBat:
				aiPlaySound(pSprite, 2002, 2, -1);
				aiNewState(pSprite, pXSprite, batRecoil);
				break;
				
			case kDudeEel:
				aiPlaySound(pSprite, 1502, 2, -1);
				aiNewState(pSprite, pXSprite, boneelRecoil);
				break;
				
			case kDudeGillBeast:
				XSECTOR pXSector = sector[pSprite.sectnum].extra > 0 ? xsector[sector[pSprite.sectnum].extra] : null;
				aiPlaySound(pSprite, 1702, 2, -1);
				if(pXSector != null && pXSector.Underwater)
					aiNewState(pSprite, pXSprite, gillBeastRecoil[WATER]);
				else aiNewState(pSprite, pXSprite, gillBeastRecoil[LAND]);
				break;
				
			case kDudePhantasm:
				aiPlaySound(pSprite, 1602, 2, -1);
				if ( aiTeslaHit[pXSprite.reference] != 0 )
					aiNewState(pSprite, pXSprite, ghostRTesla);
				else
					aiNewState(pSprite, pXSprite, ghostRecoil);
				break;	
				
			case kDudeRedSpider:
			case kDudeBrownSpider:
			case kDudeBlackSpider:
				aiPlaySound(pSprite, Random(1) + 1802, 2, -1);
				aiNewState(pSprite, pXSprite, spidDodge);
				break;
				
			case kDudeMotherSpider:
				aiPlaySound(pSprite, Random(1) + 1851, 2, -1);
				aiNewState(pSprite, pXSprite, spidDodge);
				break;
				
			case kDudeInnocent:
				aiPlaySound(pSprite, BiRandom(2) + 7007, 2, -1); //ISCREAM1
				if ( aiTeslaHit[pXSprite.reference] != 0 )
			        aiNewState(pSprite, pXSprite, innocentTesla);
			    else
			        aiNewState(pSprite, pXSprite, innocentRecoil);
				break;
			case kDudeTinyCaleb:
				if ( aiTeslaHit[pXSprite.reference] != 0)
				{
					aiNewState(pSprite, pXSprite, calebRTesla);
					aiTeslaHit[pXSprite.reference] = 0;
					return;
				}
				
				if ( pXSprite.palette == 1 || pXSprite.palette == 2 )
					aiNewState(pSprite, pXSprite, calebRecoil[WATER]);
				else aiNewState(pSprite, pXSprite, calebRecoil[LAND]);	
				break;
			case kDudeTheBeast:
				aiPlaySound(pSprite, BiRandom(2) + 9004, 2, -1);
				if ( pXSprite.palette != 1 
					&& pXSprite.palette != 2 
					&& aiTeslaHit[pXSprite.reference] != 0)
				{
					aiNewState(pSprite, pXSprite, beastRTesla);
					aiTeslaHit[pXSprite.reference] = 0;
					return;
				}
				
				if ( pXSprite.palette == 1 || pXSprite.palette == 2 )
					aiNewState(pSprite, pXSprite, beastRecoil[WATER]);
				else aiNewState(pSprite, pXSprite, beastRecoil[LAND]);	
				break;
			case kDudeGreenPod:
			case kDudeFirePod:
				aiNewState(pSprite, pXSprite, podRecoil);
				break;   
			case kDudeGreenTentacle:
			case kDudeFireTentacle:
				aiNewState(pSprite, pXSprite, tentacleRecoil);
				break;			
			default:
				aiNewState(pSprite, pXSprite, genRecoil);
				break;
			case kDudeFleshStatue:
			case kDudeStoneStatue:
			    break;	
		}
		aiTeslaHit[pXSprite.reference] = 0;
	}

	protected static void aiPlaySound(SPRITE pSprite, int nSound, int soundonce, int nChannel) {
		if(soundonce != 0)
		{
			if ( soundonce > aiSoundOnce[pSprite.xvel] || gFrameClock >= aiClock[pSprite.xvel] )
		    {
		     	sfxKill3DSound(pSprite, -1, -1);
		     	sfxStart3DSound(pSprite, nSound, nChannel, 0);
		     	aiSoundOnce[pSprite.xvel] = soundonce;
		     	aiClock[pSprite.xvel] = gFrameClock + 120;
		    }
		} 
		else
		{
			sfxStart3DSound(pSprite, nSound, nChannel, 2);
		}
	}

	protected static void aiThinkTarget( SPRITE pSprite, XSPRITE pXSprite )
	{
		if(!(pSprite.lotag >= kDudeBase && pSprite.lotag < kDudeMax))
			game.dassert("pSprite.type >= kDudeBase && pSprite.type < kDudeMax");
		DudeInfo pDudeInfo = dudeInfo[pSprite.lotag - kDudeBase];	
		
		if ( !Chance(pDudeInfo.alertChance / 2) )
			return;

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
						aiSetTarget( pXSprite, pPlayer.nSprite );
						aiActivateDude( pSprite, pXSprite );
						return;
					}
					
					// we may want to make hearing a function of sensitivity, rather than distance
					if ( dist < pDudeInfo.hearDist )
					{
						aiSetTarget(pXSprite, x, y, z);
						aiActivateDude( pSprite, pXSprite );
						return;
					}
				}
			}
		}
	}
	
	protected static void aiThinkTarget2( SPRITE pSprite, XSPRITE pXSprite )
	{
		if(!(pSprite.lotag >= kDudeBase && pSprite.lotag < kDudeMax))
			game.dassert("pSprite.type >= kDudeBase && pSprite.type < kDudeMax");
		DudeInfo pDudeInfo = dudeInfo[pSprite.lotag - kDudeBase];	
		
		if ( !Chance(pDudeInfo.alertChance / 2) )
			return;

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
						aiSetTarget( pXSprite, pPlayer.nSprite );
						aiActivateDude( pSprite, pXSprite );
						return;
					}
					
					// we may want to make hearing a function of sensitivity, rather than distance
					if ( dist < pDudeInfo.hearDist )
					{
						aiSetTarget(pXSprite, x, y, z);
						aiActivateDude( pSprite, pXSprite );
						return;
					}
				}
			}
		}
		
		if(pXSprite.state != 0)
			for (short nSprite = headspritestat[kStatDude]; nSprite >= 0; nSprite = nextspritestat[nSprite])
			{
				SPRITE pTarget = sprite[nSprite];
				if(pTarget.lotag == kDudeInnocent) {
					int dx = pTarget.x - pSprite.x;
					int dy = pTarget.y - pSprite.y;
					long dist = engine.qdist(dx, dy);
					if ( dist <= dudeInfo[45].seeDist || dist <= dudeInfo[45].hearDist )
			        {
			            aiSetTarget( pXSprite, pTarget.xvel );
						aiActivateDude( pSprite, pXSprite );
						return;
			        }
				}
				
			}
	}
	
	protected static int aiTickHandler(SPRITE pSprite, XSPRITE pXSprite)
	{
		int ticks = pXSprite.aiState.ticks;
		if(IsOriginalDemo()) {
			if(pSprite.lotag == kDudeEel && pXSprite.aiState == boneelRecoil) //Bone Eel original bug
				ticks = 0;
			if(pSprite.lotag == kDudeTheBeast && pXSprite.aiState == beastHack[WATER]) //The Beast original bug
				ticks = 0;
		}
		
		return ticks;
	}

	public static void aiProcessDudes()
	{
		// process active sprites
		for (short nSprite = headspritestat[kStatDude]; nSprite >= 0; nSprite = nextspritestat[nSprite])
		{
			SPRITE pSprite = sprite[nSprite];
			if ( (pSprite.hitag & kAttrFree) != 0 || !IsDudeSprite(pSprite))
				continue;

			int nXSprite = pSprite.extra;
			XSPRITE pXSprite = xsprite[nXSprite];
		 	DudeInfo pDudeInfo = dudeInfo[pSprite.lotag - kDudeBase];

			// don't manipulate players or dead guys
			if (IsPlayerSprite(pSprite) || ( pXSprite.health == 0 ))
				continue;

			pXSprite.stateTimer = ClipLow(pXSprite.stateTimer - kFrameTicks, 0);
			if ( pXSprite.aiState != null && pXSprite.aiState.move ) 
				pXSprite.aiState.move(pSprite, pXSprite);
	        if ( pXSprite.aiState != null && pXSprite.aiState.think && (gFrame & kAIThinkMask) == (nSprite & kAIThinkMask ) )
	        	pXSprite.aiState.think(pSprite, pXSprite);
			if ( pXSprite.aiState != null && pXSprite.stateTimer == 0 && pXSprite.aiState.next != null )
			{
				if ( pXSprite.aiState != null && aiTickHandler(pSprite, pXSprite) > 0 )
					aiNewState(pSprite, pXSprite, pXSprite.aiState.next);
				else if ( seqFrame(SS_SPRITE, nXSprite) < 0 )
					aiNewState(pSprite, pXSprite, pXSprite.aiState.next);
			}
	        // process dudes for recoil
			if ( pXSprite.health != 0 && cumulDamage[nXSprite] >= pDudeInfo.hinderDamage << 4 ) {
				pXSprite.data3 = (short) cumulDamage[nXSprite];
				RecoilDude(pSprite, pXSprite);
			}
		}
		
		// reset the cumulative damages for the next frame
		Arrays.fill(cumulDamage, 0);
	}
	
	/*******************************************************************************
	FUNCTION:		aiInit()

	DESCRIPTION:

	PARAMETERS:		void

	RETURNS:		void

	NOTES:
	*******************************************************************************/
	public static void aiInit(boolean isOriginal)
	{
		genIdle = new AISTATE(Type.idle, kSeqDudeIdle, null, 0, false, false, false, null); 
		genRecoil = new AISTATE(Type.recoil, kSeqDudeRecoil, null, 20, false, false, false, genIdle); 

//		AIZOMBA.Init();
//		AIRAT.Init();
//		AIINNOCENT.Init();
//		AIHAND.Init();
//		AICULTIST.Init();
//		AIZOMBF.Init();
//		AIGILLBEAST.Init();
//		AIBURN.Init();
//		AIGARG.Init();
//		AIBAT.Init();
//		AISPID.Init();
//		AIHOUND.Init();
//		AIGHOST.Init();
//		AIBONEEL.Init();
//		AITCHERNOBOG.Init();
//		AIPOD.Init();
//		AIBEAST.Init();
//		AICALEB.Init();
//		AICERBERUS.Init();
//		
//		AIUNICULT.Init();
		
		Arrays.fill(aiThinkTime, 0);
		Arrays.fill(aiActive, 0);
		Arrays.fill(aiSoundOnce, 0);
		Arrays.fill(aiTeslaHit, 0);
		Arrays.fill(aiClock, 0);
		Arrays.fill(cumulDamage, 0);
		Arrays.fill(gDudeSlope, 0);

		
		Arrays.fill(pDudes, null);
		for (short nSprite = headspritestat[kStatDude]; nSprite >= 0; nSprite = nextspritestat[nSprite])
		{
			SPRITE pDude = sprite[nSprite];
			
//			switch( pDude.lotag )
//			{
//			case kDudeAxeZombie:	
//			case kDudeSleepZombie:
//			case kDudeEarthZombie:
//				pDudes[pDude.extra] = new AxeZombie(pDude);
//				continue;
//			}
			
			aiInit(pDude, isOriginal);
		}
	}

	public static void aiInit(SPRITE pDude, boolean isOriginal) {
		XSPRITE pXDude = xsprite[pDude.extra];
		switch ( pDude.lotag )
		{
			case kGDXDudeUniversalCultist:
				aiActive[pDude.xvel] = 0;
				aiNewState(pDude, pXDude, GDXGenDudeIdle[LAND]);
				break;
			case kDudeTommyCultist:
			case kDudeShotgunCultist:
			case kDudeTeslaCultist:
			case kDudeDynamiteCultist:
			case kDudeBeastCultist:
				aiActive[pDude.xvel] = 0;
				aiNewState(pDude, pXDude, cultistIdle[LAND]);
				break;
				
			case kDudeFanaticProne:
				aiActive[pDude.xvel] = 0;
				aiNewState(pDude, pXDude, cultistSProne);
				break;
				
			case kDudeCultistProne:
				aiActive[pDude.xvel] = 0;
				aiNewState(pDude, pXDude, cultistTProne);
				break;
				
			case kDudeButcher:
				aiThinkTime[pDude.xvel] = 0;
				aiNewState(pDude, pXDude, zombieFIdle);
				break;

			case kDudeAxeZombie:
				aiThinkTime[pDude.xvel] = 0;
				aiNewState(pDude, pXDude, zombieAIdle);
				break;
				
			case kDudeSleepZombie:
				aiThinkTime[pDude.xvel] = 0;
				aiNewState(pDude, pXDude, zombieSLIdle);
				break;

			case kDudeEarthZombie:
				aiThinkTime[pDude.xvel] = 0;
				aiNewState(pDude, pXDude, zombieEIdle);
				break;
				
			case kDudeFleshGargoyle:
			case kDudeStoneGargoyle:
				aiActive[pDude.xvel] = 0;
				aiThinkTime[pDude.xvel] = 0;
				aiNewState(pDude, pXDude, gargoyleIdle);
				break;	
				
			case kDudeFleshStatue:
			case kDudeStoneStatue:
				aiNewState(pDude, pXDude, statueIdle);
			    break;	
			case kDudeCerberus:
				aiThinkTime[pDude.xvel] = 0;
				aiNewState(pDude, pXDude, cerberusIdle[C1]);
			    break;
			    
			case kDudeCerberus2:
				aiThinkTime[pDude.xvel] = 0;
				aiNewState(pDude, pXDude, cerberusIdle[C2]);
			    break;	
			
			case kDudeHound:
			    aiNewState(pDude, pXDude, houndIdle);
			    break;
				
			case kDudeHand:
				aiNewState(pDude, pXDude, handIdle);
				break;	
				
			case kDudePhantasm:
				aiThinkTime[pDude.xvel] = 0;
				aiActive[pDude.xvel] = 0;
			    aiNewState(pDude, pXDude, ghostIdle);
			    break;
				
			case kDudeInnocent:
				aiNewState(pDude, pXDude, innocentIdle);
				break;

			case kDudeRat:
				aiNewState(pDude, pXDude, ratIdle);
				break;
				
			case kDudeEel:
				aiThinkTime[pDude.xvel] = 0;
				aiActive[pDude.xvel] = 0;
			    aiNewState(pDude, pXDude, boneelIdle);
			    break;

			case kDudeGillBeast:
				SECTOR pSector = sector[pDude.sectnum];
				if(isOriginal) {
					aiNewState(pDude, pXDude, gillBeastIdle[LAND]);
					break;
				}
				
				if(pSector.extra > 0 && xsector[pSector.extra].Underwater) 
					aiNewState(pDude, pXDude, gillBeastIdle[WATER]);
				else 
					aiNewState(pDude, pXDude, gillBeastIdle[LAND]);
				break;

			case kDudeBat:
				aiThinkTime[pDude.xvel] = 0;
				aiActive[pDude.xvel] = 0;
				aiNewState(pDude, pXDude, batSleep);
				break;

			case kDudeBrownSpider:
			case kDudeRedSpider:
			case kDudeBlackSpider:
			case kDudeMotherSpider:
				aiThinkTime[pDude.xvel] = 0;
				aiActive[pDude.xvel] = 0;
				aiNewState(pDude, pXDude, spidIdle);
				break;
				
			case kDudeTchernobog:
				aiThinkTime[pDude.xvel] = 0;
				aiNewState(pDude, pXDude, tchernobogIdle);
				break;
			case kDudeTinyCaleb:
				aiNewState(pDude, pXDude, calebIdle[LAND]);
				break;
			case kDudeTheBeast:
				aiNewState(pDude, pXDude, beastIdle[LAND]);
				break;
			case kDudeGreenPod:
			case kDudeFirePod:
				aiNewState(pDude, pXDude, podIdle);
				break;   
			case kDudeGreenTentacle:
			case kDudeFireTentacle:
				aiNewState(pDude, pXDude, tentacleIdle);
				break;	
			case kDudeBurning:
			case kDudeCultistBurning:
				aiNewState(pDude, pXDude, burnGoto[CULTIST]);
				pXDude.burnTime = 1200;
				break;
			case kGDXGenDudeBurning:
				aiNewState(pDude, pXDude, burnGoto[GDXGENDUDE]);
				pXDude.burnTime = 1200;
				break;
			case kDudeAxeZombieBurning:
				aiNewState(pDude, pXDude, burnGoto[ZOMBIE]);
				pXDude.burnTime = 1200;
				break;
			case kDudeBloatedButcherBurning:
				aiNewState(pDude, pXDude, burnGoto[BUTCHER]);
				pXDude.burnTime = 1200;
				break;
			case kDudeTinyCalebburning:
				aiNewState(pDude, pXDude, burnGoto[TINYCALEB]);
				pXDude.burnTime = 1200;
				break;
			case kDudeTheBeastburning:
				aiNewState(pDude, pXDude, burnGoto[BEAST]);
				pXDude.burnTime = 1200;
				break;
				
			default:
				aiNewState(pDude, pXDude, genIdle);
				break;
		}

		aiSetTarget(pXDude, 0, 0, 0);

		pXDude.stateTimer	= 0;

		switch ( pDude.lotag )
		{
			case kDudeEarthZombie:
			case kDudeSleepZombie:
				pDude.hitag = ( kAttrMove | kAttrGravity | kAttrFalling );
				break;
				
			case kDudeMotherSpider:
			case kDudeBrownSpider:
			case kDudeRedSpider:
			case kDudeBlackSpider:
				if(pDude.lotag == kDudeMotherSpider && isOriginal) {
					pDude.hitag = (kAttrMove | kAttrGravity | kAttrFalling | kAttrAiming);
					break;
				}
				
				if ( (pDude.cstat & kSpriteFlipY) != 0 )
					pDude.hitag = (kAttrMove | kAttrAiming);
				else pDude.hitag = (kAttrMove | kAttrGravity | kAttrFalling | kAttrAiming);
				break;
				

			case kDudeEel:
			case kDudeBat:
			case kDudeFleshGargoyle:
			case kDudeStoneGargoyle:
			case kDudePhantasm:
				pDude.hitag = (kAttrMove | kAttrAiming);
				break;
			case kDudeGillBeast:
				XSECTOR pXSector = sector[pDude.sectnum].extra > 0 ? xsector[sector[pDude.sectnum].extra] : null;
				if(pXSector != null && pXSector.Underwater)
					pDude.hitag = (kAttrMove | kAttrAiming);
				else pDude.hitag = (kAttrMove | kAttrGravity | kAttrFalling | kAttrAiming);
				break;
			
			case kDudeMotherPod: // Fake dude type
				break;
			// These will now have no gravity, so it's possible to flipY 
			// and place on ceilings if hitag = 1.
			case kDudeGreenPod:
			case kDudeFirePod:
			case kDudeGreenTentacle:
			case kDudeFireTentacle:
			case kDudeMotherTentacle:
				if ((pDude.cstat & kSpriteFlipY) != 0) {
					pDude.hitag = kAttrAiming;
					break;
				}

				// go default
			default:
				pDude.hitag = (kAttrMove | kAttrGravity | kAttrFalling | kAttrAiming);
				break;
		}
	}
	
	public static Vector3 EnemyAim = new Vector3();
	public static void UpdateEnemyAim(SPRITE pSprite, int nXSprite, WeaponAim pAimData)
	{
		if(!IsDudeSprite(pSprite))
				return;
		
		DudeInfo pDudeInfo = dudeInfo[pSprite.lotag - kDudeBase];
		int eyeAboveZ = pDudeInfo.eyeHeight * pSprite.yrepeat << 2;

		int x = pSprite.x;
		int y = pSprite.y;
		
		EnemyAim.x = Cos(pSprite.ang) >> 16;
		EnemyAim.y = Sin(pSprite.ang) >> 16;
		EnemyAim.z = gDudeSlope[nXSprite];

		int closest = 0x7FFFFFFF;
		for (int nDude = headspritestat[kStatDude]; nDude >= 0; nDude = nextspritestat[nDude])
		{
			SPRITE pDude = sprite[nDude];
			
			// don't target yourself!
			if ( pDude == pSprite )
				continue;

	    	if ( (pDude.hitag & kAttrAiming) == 0 )
	    		continue;
	        
    		int tx, ty, tz;

			tx = pDude.x;
			ty = pDude.y;
			tz = pDude.z;

			int dist = (int)engine.qdist(tx - x, ty - y);

			if ( dist == 0 || dist > 10240 )
				continue;

			if ( pAimData.kSeeker != 0 )
			{
                int k = (dist << 12) / pAimData.kSeeker;
                tx += k * sprXVel[nDude] >> 12;
                ty += k * sprYVel[nDude] >> 12;
                tz += k * sprZVel[nDude] >> 8;
			}

			int z1 = mulscale(dist, gDudeSlope[nXSprite], 10) + eyeAboveZ;
			int z2 = mulscale(kAimMaxSlope, dist, 10);
			
			GetSpriteExtents(pDude);

			if ( (z1 - z2 > extents_zBot) || (z1 + z2 < extents_zTop) )
				continue;

			int dx = mulscale(dist, Cos(pSprite.ang), 30) + x;
            int dy = mulscale(dist, Sin(pSprite.ang), 30) + y;

			int dist2 = engine.ksqrt(
					((dx - tx) >> 4) * ((dx - tx) >> 4) 
					+ ((dy - ty) >> 4) * ((dy - ty) >> 4)
					+ ((z1 - tz) >> 8) * ((z1 - tz) >> 8));
			
			if( dist2 < closest )
			{
				int ang = engine.getangle(tx - x, ty - y);
				if ( klabs(((ang - pSprite.ang + kAngle180) & kAngleMask) - kAngle180) > pAimData.kDudeAngle )
					continue;
				
				int dz = pDude.z - pSprite.z;

				if(engine.cansee(x, y, eyeAboveZ, pSprite.sectnum, tx, ty, tz, pDude.sectnum) )
				{
					closest = dist2;
	
					EnemyAim.x = Cos(ang) >> 16;
					EnemyAim.y = Sin(ang) >> 16;
					
					if ( (dz < -819 && dz > -2867) || (dz < -2867 && dz > -12288) ) {
						EnemyAim.z = divscale(dz, dist, 10) + 9460;
						continue;
					}
					if ( dz < -12288 )
	                {
						EnemyAim.z = divscale(dz, dist, 10) - 7500;
	                    continue;
	                }
//					EnemyAim.z = (int) divscale(dz, dist, 10);
				} 
				EnemyAim.z = divscale(dz, dist, 10); //Stone gargoyle fix
			}
		}
	}
}



