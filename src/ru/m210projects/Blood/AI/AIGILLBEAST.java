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

import static ru.m210projects.Blood.AI.Ai.LAND;
import static ru.m210projects.Blood.AI.Ai.WATER;
import static ru.m210projects.Blood.AI.Ai.aiChooseDirection;
import static ru.m210projects.Blood.AI.Ai.aiMoveDodge;
import static ru.m210projects.Blood.AI.Ai.aiMoveForward;
import static ru.m210projects.Blood.AI.Ai.aiNewState;
import static ru.m210projects.Blood.AI.Ai.aiPlaySound;
import static ru.m210projects.Blood.AI.Ai.aiSetTarget;
import static ru.m210projects.Blood.AI.Ai.aiThinkTarget;
import static ru.m210projects.Blood.AI.Ai.gDudeSlope;
import static ru.m210projects.Blood.Actor.IsDudeSprite;
import static ru.m210projects.Blood.Actor.actFireVector;
import static ru.m210projects.Blood.Actor.sprXVel;
import static ru.m210projects.Blood.Actor.sprYVel;
import static ru.m210projects.Blood.Actor.sprZVel;
import static ru.m210projects.Blood.DB.kDudeBase;
import static ru.m210projects.Blood.DB.kDudePlayer1;
import static ru.m210projects.Blood.DB.kItemBase;
import static ru.m210projects.Blood.DB.kItemLtdInvisibility;
import static ru.m210projects.Blood.DB.xsector;
import static ru.m210projects.Blood.DB.xsprite;
import static ru.m210projects.Blood.Gameutils.BiRandom2;
import static ru.m210projects.Blood.Gameutils.Chance;
import static ru.m210projects.Blood.Gameutils.ClipRange;
import static ru.m210projects.Blood.Gameutils.HitScan;
import static ru.m210projects.Blood.Gameutils.IsPlayerSprite;
import static ru.m210projects.Blood.Gameutils.M2X;
import static ru.m210projects.Blood.Gameutils.Random;
import static ru.m210projects.Blood.Globals.SS_SPRITE;
import static ru.m210projects.Blood.Globals.gPlayer;
import static ru.m210projects.Blood.Globals.kAngle15;
import static ru.m210projects.Blood.Globals.kAngle180;
import static ru.m210projects.Blood.Globals.kAngle45;
import static ru.m210projects.Blood.Globals.kAngle5;
import static ru.m210projects.Blood.Globals.kAngle60;
import static ru.m210projects.Blood.Globals.kAngle90;
import static ru.m210projects.Blood.Globals.kAngleMask;
import static ru.m210projects.Blood.Globals.kFrameTicks;
import static ru.m210projects.Blood.Globals.kSeqDudeIdle;
import static ru.m210projects.Blood.Globals.kTimerRate;
import static ru.m210projects.Blood.Globals.pGameInfo;
import static ru.m210projects.Blood.Main.engine;
import static ru.m210projects.Blood.PLAYER.powerupCheck;
import static ru.m210projects.Blood.SOUND.sfxStart3DSound;
import static ru.m210projects.Blood.Trig.Cos;
import static ru.m210projects.Blood.Trig.Sin;
import static ru.m210projects.Blood.Types.DudeInfo.dudeInfo;
import static ru.m210projects.Build.Engine.pHitInfo;
import static ru.m210projects.Build.Engine.sector;
import static ru.m210projects.Build.Engine.sprite;
import static ru.m210projects.Build.Gameutils.isValidSprite;
import static ru.m210projects.Build.Pragmas.divscale;
import static ru.m210projects.Build.Pragmas.dmulscale;
import static ru.m210projects.Build.Pragmas.klabs;

import ru.m210projects.Blood.PLAYER;
import ru.m210projects.Blood.AI.AISTATEFUNC.Type;
import ru.m210projects.Blood.Types.CALLPROC;
import ru.m210projects.Blood.Types.DudeInfo;
import ru.m210projects.Blood.Types.XSECTOR;
import ru.m210projects.Blood.Types.XSPRITE;
import ru.m210projects.Build.Types.SPRITE;

public class AIGILLBEAST {

	public static AISTATE[] gillBeastIdle = new AISTATE[2];
	public static AISTATE[] gillBeastChase = new AISTATE[2];
	public static AISTATE[] gillBeastSearch = new AISTATE[2];
	public static AISTATE[] gillBeastGoto = new AISTATE[2];
	public static AISTATE[] gillBeastDodge = new AISTATE[2];
	public static AISTATE[] gillBeastHack = new AISTATE[2];
	public static AISTATE[] gillBeastRecoil = new AISTATE[2];
	public static AISTATE gillBeastAttack;
	
	public static void Init()
	{
		gillBeastIdle[LAND] = new AISTATE(Type.idle,kSeqDudeIdle, null, 0, false, false, true, null) {
			public void think(SPRITE sprite, XSPRITE xsprite) {
				aiThinkTarget(sprite, xsprite);
			}
		};
		gillBeastChase[LAND] = new AISTATE(Type.other,9, null, 0, false, true, true, null) {
			public void move(SPRITE sprite, XSPRITE xsprite) {
				aiMoveForward(sprite, xsprite);
			}
			public void think(SPRITE sprite, XSPRITE xsprite) {
				thinkLandChase(sprite, xsprite);
			}
		};
		gillBeastDodge[LAND] = new AISTATE(Type.other,9, null, 90, false, true, false, gillBeastChase[LAND]) {
			public void move(SPRITE sprite, XSPRITE xsprite) {
				aiMoveDodge(sprite, xsprite);
			}
		}; 
		gillBeastGoto[LAND] = new AISTATE(Type.tgoto,9, null, 600, false, true, true, gillBeastIdle[LAND]) {
			public void move(SPRITE sprite, XSPRITE xsprite) {
				aiMoveForward(sprite, xsprite);
			}
			public void think(SPRITE sprite, XSPRITE xsprite) {
				thinkLandGoto(sprite, xsprite);
			}
		};
		gillBeastHack[LAND] = new AISTATE(Type.other,6, new CALLPROC() {
			public void run(int nXSprite) {
				HackCallback(nXSprite);
			}
		}, 120, false, false, false, gillBeastChase[LAND]);
		gillBeastSearch[LAND] = new AISTATE(Type.search,9, null, 120, false, true, true, gillBeastIdle[LAND]) {
			public void move(SPRITE sprite, XSPRITE xsprite) {
				aiMoveForward(sprite, xsprite);
			}
			public void think(SPRITE sprite, XSPRITE xsprite) {
				thinkSearch(sprite, xsprite);
			}
		};
		gillBeastRecoil[LAND] = new AISTATE(Type.other,5, null, 0, false, false, false, gillBeastDodge[LAND]);
		gillBeastIdle[WATER] = new AISTATE(Type.idle,10, null, 0, false, false, true, null) {
			public void think(SPRITE sprite, XSPRITE xsprite) {
				aiThinkTarget(sprite, xsprite);
			}
		};
		gillBeastChase[WATER] = new AISTATE(Type.other,10, null, 600, false, true, true, null) {
			public void move(SPRITE sprite, XSPRITE xsprite) {
				aiMoveChase(sprite, xsprite);
			}
			public void think(SPRITE sprite, XSPRITE xsprite) {
				thinkWaterChase(sprite, xsprite);
			}
		};
		gillBeastDodge[WATER] = new AISTATE(Type.other,10, null, 90, false, true, false, gillBeastChase[WATER]) {
			public void move(SPRITE sprite, XSPRITE xsprite) {
				aiMoveDodge(sprite, xsprite);
			}
		};
		gillBeastGoto[WATER] = new AISTATE(Type.tgoto,10, null, 600, false, true, true, gillBeastIdle[WATER]) {
			public void move(SPRITE sprite, XSPRITE xsprite) {
				aiMoveForward(sprite, xsprite);
			}
			public void think(SPRITE sprite, XSPRITE xsprite) {
				thinkWaterGoto(sprite, xsprite);
			}
		};
		gillBeastSearch[WATER] = new AISTATE(Type.search,10, null, 120, false, true, true, gillBeastIdle[WATER]) {
			public void move(SPRITE sprite, XSPRITE xsprite) {
				aiMoveForward(sprite, xsprite);
			}
			public void think(SPRITE sprite, XSPRITE xsprite) {
				thinkSearch(sprite, xsprite);
			}
		};
		gillBeastHack[WATER] = new AISTATE(Type.other,7, new CALLPROC() {
			public void run(int nXSprite) {
				HackCallback(nXSprite);
			}
		}, 0, false, false, true, gillBeastChase[WATER]) {
			public void think(SPRITE sprite, XSPRITE xsprite) {
				thinkWaterChase(sprite, xsprite);
			}
		};
		gillBeastRecoil[WATER] = new AISTATE(Type.other,5, null, 0, false, false, false, gillBeastDodge[WATER]);
		
		gillBeastAttack = new AISTATE(Type.other,10, null, 0, false, true, true, gillBeastChase[WATER]) {
			public void move(SPRITE sprite, XSPRITE xsprite) {
				myMoveTarget(sprite, xsprite);
			}
			public void think(SPRITE sprite, XSPRITE xsprite) {
				thinkWaterChase(sprite, xsprite);
			}
		};
	}
	
	private static void myMoveTarget( SPRITE pSprite, XSPRITE pXSprite )
	{
		if(!IsDudeSprite(pSprite))
			return;
		
		DudeInfo pDudeInfo = dudeInfo[pSprite.lotag - kDudeBase];

		int maxTurn = pDudeInfo.angSpeed * kFrameTicks >> 4;
		int dang = ((kAngle180 + pXSprite.goalAng - pSprite.ang) & kAngleMask) - kAngle180;
		pSprite.ang = (short)((pSprite.ang + ClipRange(dang, -maxTurn, maxTurn)) & kAngleMask);

		// don't move forward if trying to turn around
		if ( klabs(dang) > kAngle60 ) 
		{
			pSprite.ang = (short) ((pSprite.ang + kAngle90) & kAngleMask);
			return;
		}

		int dx = pXSprite.targetX - pSprite.x;
		int dy = pXSprite.targetY - pSprite.y;
		if(!Chance(0x2000) || engine.qdist(dx, dy) > 1024)
		{
			int sin = Sin(pSprite.ang);
			int cos = Cos(pSprite.ang);
			
			long vel = kFrameTicks * (pDudeInfo.frontSpeed - ( divscale(kFrameTicks - pGameInfo.nDifficulty, kTimerRate, 27) /  kTimerRate ) );
			vel = (vel >> 1) + dmulscale(sprXVel[pSprite.xvel], cos, sprYVel[pSprite.xvel], sin, 30);
			long svel = dmulscale(sprXVel[pSprite.xvel], sin, -sprYVel[pSprite.xvel], cos, 30);
			
			int z1 = 0;
			if(pXSprite.target != -1)
			{
				SPRITE pTarget = sprite[pXSprite.target];
				z1 = pTarget.z;
				if(IsDudeSprite(pTarget))
					z1 += dudeInfo[pTarget.lotag - kDudeBase].eyeHeight;
			}
			int z2 = pSprite.z + pDudeInfo.eyeHeight;
			
			sprXVel[pSprite.xvel] = dmulscale(vel, cos, svel, sin, 30);
			sprYVel[pSprite.xvel] = dmulscale(vel, sin, -svel, cos, 30);
			sprZVel[pSprite.xvel] = 8 * (z1 - z2);
		}
	}
	

	private static void aiMoveChase( SPRITE pSprite, XSPRITE pXSprite )
	{
		if(!IsDudeSprite(pSprite))
			return;
		
		DudeInfo pDudeInfo = dudeInfo[pSprite.lotag - kDudeBase];
		
		int maxTurn = pDudeInfo.angSpeed * kFrameTicks >> 4;
		int dang = ((kAngle180 + pXSprite.goalAng - pSprite.ang) & kAngleMask) - kAngle180;
		pSprite.ang = (short)((pSprite.ang + ClipRange(dang, -maxTurn, maxTurn)) & kAngleMask);

		if ( klabs(dang) > kAngle60 ) 
			return;
		
		if(pXSprite.target == -1) 
			pSprite.ang = (short) ((pSprite.ang + kAngle45) & kAngleMask);
		
		int dx = pXSprite.targetX - pSprite.x;
		int dy = pXSprite.targetY - pSprite.y;
		if(Random(64) >= 32 || engine.qdist(dx, dy) > 1024)
		{
			int sin = Sin(pSprite.ang);
			int cos = Cos(pSprite.ang);
			
			long fvel = kFrameTicks * (pDudeInfo.frontSpeed - ( divscale(kFrameTicks - pGameInfo.nDifficulty, kTimerRate, 27) /  kTimerRate ) );
			
			long vel = dmulscale(sprXVel[pSprite.xvel], cos, sprYVel[pSprite.xvel], sin, 30);
			long svel = dmulscale(sprXVel[pSprite.xvel], sin, -sprYVel[pSprite.xvel], cos, 30);
			
			if(pXSprite.target == -1)
				vel += fvel;
			else vel += fvel >> 2;

			sprXVel[pSprite.xvel] = dmulscale(vel, cos, svel, sin, 30);
			sprYVel[pSprite.xvel] = dmulscale(vel, sin, -svel, cos, 30);
		}
	}

	private static void thinkLandGoto( SPRITE pSprite, XSPRITE pXSprite )
	{
		int dx, dy, dist;

		if(!IsDudeSprite(pSprite))
			return;
		
		DudeInfo pDudeInfo = dudeInfo[pSprite.lotag - kDudeBase];

		dx = pXSprite.targetX - pSprite.x;
		dy = pXSprite.targetY - pSprite.y;

		int nAngle = engine.getangle(dx, dy);
		dist = (int)engine.qdist(dx, dy);
		
		aiChooseDirection(pSprite, pXSprite, nAngle);

		// if reached target, change to search mode
		if ( dist < M2X(1.0) && klabs(pSprite.ang - nAngle) < pDudeInfo.periphery ) {
			XSECTOR pXSector = sector[pSprite.sectnum].extra > 0 ? xsector[sector[pSprite.sectnum].extra] : null;
			if(pXSector != null && pXSector.Underwater)
				aiNewState(pSprite, pXSprite, gillBeastSearch[WATER]);
			else aiNewState(pSprite, pXSprite, gillBeastSearch[LAND]);
		}

		aiThinkTarget(pSprite, pXSprite);
	}
	
	private static void thinkLandChase( SPRITE pSprite, XSPRITE pXSprite )
	{
		XSECTOR pXSector = sector[pSprite.sectnum].extra > 0 ? xsector[sector[pSprite.sectnum].extra] : null;
		if ( pXSprite.target == -1)
		{
			if(pXSector != null && pXSector.Underwater)
				aiNewState(pSprite, pXSprite, gillBeastSearch[WATER]);
			else aiNewState(pSprite, pXSprite, gillBeastSearch[LAND]);
			return;
		}

		int dx, dy, dist;
		if(!IsDudeSprite(pSprite) || !isValidSprite(pXSprite.target))
			return;

		DudeInfo pDudeInfo = dudeInfo[pSprite.lotag - kDudeBase];
		SPRITE pTarget = sprite[pXSprite.target];
		XSPRITE pXTarget = pTarget.extra > 0 ? xsprite[pTarget.extra] : null;

		// check target
		dx = pTarget.x - pSprite.x;
		dy = pTarget.y - pSprite.y;

		aiChooseDirection(pSprite, pXSprite, engine.getangle(dx, dy));

		if ( pXTarget == null || pXTarget.health == 0 )
		{
			// target is dead
			if(pXSector != null && pXSector.Underwater)
				aiNewState(pSprite, pXSprite, gillBeastSearch[WATER]);
			else aiNewState(pSprite, pXSprite, gillBeastSearch[LAND]);
			return;
		}

		if ( IsPlayerSprite( pTarget ) )
		{
			PLAYER pPlayer = gPlayer[ pTarget.lotag - kDudePlayer1 ];
			if ( powerupCheck( pPlayer, kItemLtdInvisibility - kItemBase ) > 0 )
			{
				if(pXSector != null && pXSector.Underwater)
					aiNewState(pSprite, pXSprite, gillBeastSearch[WATER]);
				else aiNewState(pSprite, pXSprite, gillBeastSearch[LAND]);
				return;
			}
		}

		dist = (int) engine.qdist(dx, dy);
		if ( dist <= pDudeInfo.seeDist )
		{
			int nAngle = engine.getangle(dx, dy);
			int losAngle = ((kAngle180 + nAngle - pSprite.ang) & kAngleMask) - kAngle180;
			int eyeAboveZ = pDudeInfo.eyeHeight * pSprite.yrepeat << 2;

			// is there a line of sight to the target?
			if ( engine.cansee(pTarget.x, pTarget.y, pTarget.z, pTarget.sectnum,
				pSprite.x, pSprite.y, pSprite.z - eyeAboveZ, pSprite.sectnum) )
			{
				// is the target visible?
				if ( dist < pDudeInfo.seeDist && klabs(losAngle) <= pDudeInfo.periphery )
				{
					aiSetTarget(pXSprite, pXSprite.target);
					int dz = pTarget.z - pSprite.z;
					int tan = divscale(dz, dist, 10);
					gDudeSlope[pSprite.extra] = tan;

					if ( dist < 921 && klabs(losAngle) < kAngle5 ) {
						int nInfo = HitScan(pSprite, pSprite.z, dx, dy, 0, pHitInfo, 16777280, 0);
						if(nInfo == SS_SPRITE && pSprite.lotag == sprite[pHitInfo.hitsprite].lotag)
						{
							if(pXSector != null && pXSector.Underwater)
								aiNewState(pSprite, pXSprite, gillBeastDodge[WATER]);
							else aiNewState(pSprite, pXSprite, gillBeastDodge[LAND]);
							return;
						}
						if(pXSector != null && pXSector.Underwater)
							aiNewState(pSprite, pXSprite, gillBeastHack[WATER]);
						else aiNewState(pSprite, pXSprite, gillBeastHack[LAND]);
					}
				}
				return;
			}
		}
		if(pXSector != null && pXSector.Underwater)
			aiNewState(pSprite, pXSprite, gillBeastGoto[WATER]);
		else aiNewState(pSprite, pXSprite, gillBeastGoto[LAND]);
		sfxStart3DSound(pSprite, 1701, -1, 0);
		pXSprite.target = -1;
	}
	
	private static void thinkWaterChase( SPRITE pSprite, XSPRITE pXSprite )
	{
		if ( pXSprite.target == -1)
		{
			aiNewState(pSprite, pXSprite, gillBeastGoto[WATER]);
			return;
		}

		int dx, dy, dist;
		if(!IsDudeSprite(pSprite) || !isValidSprite(pXSprite.target))
			return;

		DudeInfo pDudeInfo = dudeInfo[pSprite.lotag - kDudeBase];
		SPRITE pTarget = sprite[pXSprite.target];
		XSPRITE pXTarget = pTarget.extra > 0 ? xsprite[pTarget.extra] : null;

		// check target
		dx = pTarget.x - pSprite.x;
		dy = pTarget.y - pSprite.y;

		aiChooseDirection(pSprite, pXSprite, engine.getangle(dx, dy));

		if ( pXTarget == null || pXTarget.health == 0 )
		{
			// target is dead
			aiNewState(pSprite, pXSprite, gillBeastSearch[WATER]);
			return;
		}

		if ( IsPlayerSprite( pTarget ) )
		{
			PLAYER pPlayer = gPlayer[ pTarget.lotag - kDudePlayer1 ];
			if ( powerupCheck( pPlayer, kItemLtdInvisibility - kItemBase ) > 0 )
			{
				aiNewState(pSprite, pXSprite, gillBeastSearch[WATER]);
				return;
			}
		}

		dist = (int) engine.qdist(dx, dy);
		if ( dist <= pDudeInfo.seeDist )
		{
			int nAngle = engine.getangle(dx, dy);
			int losAngle = ((kAngle180 + nAngle - pSprite.ang) & kAngleMask) - kAngle180;
			int eyeAboveZ = pDudeInfo.eyeHeight;
			
			// is there a line of sight to the target?
			if ( engine.cansee(pTarget.x, pTarget.y, pTarget.z, pTarget.sectnum,
				pSprite.x, pSprite.y, -eyeAboveZ, pSprite.sectnum) )
			{
				// is the target visible?
				if ( dist < pDudeInfo.seeDist && klabs(losAngle) <= pDudeInfo.periphery )
				{
					aiSetTarget(pXSprite, pXSprite.target);
					if ( dist < 1024 && klabs(losAngle) < kAngle15 ) {
						aiNewState(pSprite, pXSprite, gillBeastHack[WATER]);
						return;
					}
					aiPlaySound(pSprite, 1700, 1, -1);
			        aiNewState(pSprite, pXSprite, gillBeastAttack);
				}
			} else 
				aiNewState(pSprite, pXSprite, gillBeastAttack);
			
			return;
		}
		
		aiNewState(pSprite, pXSprite, gillBeastGoto[WATER]);
		pXSprite.target = -1;
	}
	
	private static void thinkWaterGoto( SPRITE pSprite, XSPRITE pXSprite )
	{
		int dx, dy, dist;
		if(!IsDudeSprite(pSprite))
			return;
		
		DudeInfo pDudeInfo = dudeInfo[pSprite.lotag - kDudeBase];

		dx = pXSprite.targetX - pSprite.x;
		dy = pXSprite.targetY - pSprite.y;

		int nAngle = engine.getangle(dx, dy);
		dist = (int)engine.qdist(dx, dy);

		aiChooseDirection(pSprite, pXSprite, nAngle);

		// if reached target, change to search mode
		if ( dist < M2X(1.0) && klabs(pSprite.ang - nAngle) < pDudeInfo.periphery ) {
			aiNewState(pSprite, pXSprite, gillBeastSearch[WATER]);
		}

		aiThinkTarget(pSprite, pXSprite);
	}
	
	private static void HackCallback( int nXIndex )
	{
		XSPRITE pXSprite = xsprite[nXIndex];
		int nSprite = pXSprite.reference;
		SPRITE pSprite = sprite[nSprite];
		
		int dx = Cos(pSprite.ang) >> 16;
		int dy = Sin(pSprite.ang) >> 16;
		int dz = pSprite.z;
		if(pXSprite.target != -1)
		{
			SPRITE pTarget = sprite[pXSprite.target];
			dz -= pTarget.z;
		}
		
		dx += BiRandom2(2000);
		dy += BiRandom2(2000);

		actFireVector(pSprite, 0, 0, dx, dy, dz, 8);
		actFireVector(pSprite, 0, 0, dx, dy, dz, 8);
		actFireVector(pSprite, 0, 0, dx, dy, dz, 8);
	}
	
	private static void thinkSearch( SPRITE pSprite, XSPRITE pXSprite )
	{
		aiChooseDirection(pSprite, pXSprite, pXSprite.goalAng);
		aiThinkTarget(pSprite, pXSprite);
	}
	
}
