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
import static ru.m210projects.Blood.Actor.kVectorShell;
import static ru.m210projects.Blood.Actor.sprXVel;
import static ru.m210projects.Blood.Actor.sprYVel;
import static ru.m210projects.Blood.Actor.sprZVel;
import static ru.m210projects.Blood.DB.kDudeBase;
import static ru.m210projects.Blood.DB.kDudePlayer1;
import static ru.m210projects.Blood.DB.kItemBase;
import static ru.m210projects.Blood.DB.kItemLtdInvisibility;
import static ru.m210projects.Blood.DB.xsector;
import static ru.m210projects.Blood.DB.xsprite;
import static ru.m210projects.Blood.Gameutils.BiRandom;
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

public class AICALEB {

	public static AISTATE[] calebIdle = new AISTATE[2];
	public static AISTATE[] calebSearch = new AISTATE[2];
	public static AISTATE[] calebGoto = new AISTATE[2];
	public static AISTATE[] calebDodge = new AISTATE[2];
	public static AISTATE[] calebChase = new AISTATE[2];
	public static AISTATE[] calebRecoil = new AISTATE[2];
	public static AISTATE[] calebHack = new AISTATE[2];
	public static AISTATE 	calebRTesla;
	public static AISTATE   calebMoveTarget;
	
	public static void Init()
	{
		calebIdle[LAND] = new AISTATE(Type.idle, kSeqDudeIdle, null, 0, false, false, true, null) {
			public void think(SPRITE sprite, XSPRITE xsprite) {
				aiThinkTarget(sprite, xsprite);
			}
		};
		calebIdle[WATER] = new AISTATE(Type.idle, 10, null, 0, false, false, true, null) {
			public void think(SPRITE sprite, XSPRITE xsprite) {
				aiThinkTarget(sprite, xsprite);
			}
		};
		calebChase[LAND] = new AISTATE(Type.other, 6, null, 0, false, true, true, null) {
			public void move(SPRITE sprite, XSPRITE xsprite) {
				aiMoveForward(sprite, xsprite);
			}
			public void think(SPRITE sprite, XSPRITE xsprite) {
				thinkChase(sprite, xsprite);
			}
		};
		calebChase[WATER] = new AISTATE(Type.other, 8, null, 0, false, true, true, null) {
			public void move(SPRITE sprite, XSPRITE xsprite) {
				myMoveForward(sprite, xsprite);
			}
			public void think(SPRITE sprite, XSPRITE xsprite) {
				thinkChaseWater(sprite, xsprite);
			}
		};
		calebDodge[LAND] = new AISTATE(Type.other, 6, null, 90, false, true, false, calebChase[LAND]) {
			public void move(SPRITE sprite, XSPRITE xsprite) {
				aiMoveDodge(sprite, xsprite);
			}
		};
		calebDodge[WATER] = new AISTATE(Type.other, 8, null, 90, false, true, false, calebChase[WATER]) {
			public void move(SPRITE sprite, XSPRITE xsprite) {
				aiMoveDodge(sprite, xsprite);
			}
		};
		calebGoto[LAND] = new AISTATE(Type.tgoto, 6, null, 600, false, true, true, calebIdle[LAND]) {
			public void move(SPRITE sprite, XSPRITE xsprite) {
				aiMoveForward(sprite, xsprite);
			}
			public void think(SPRITE sprite, XSPRITE xsprite) {
				thinkGoto(sprite, xsprite);
			}
		};
		calebGoto[WATER] = new AISTATE(Type.tgoto, 8, null, 600, false, true, true, calebIdle[WATER]) {
			public void move(SPRITE sprite, XSPRITE xsprite) {
				aiMoveForward(sprite, xsprite);
			}
			public void think(SPRITE sprite, XSPRITE xsprite) {
				thinkGotoWater(sprite, xsprite);
			}
		};
		
		CALLPROC hackCallback = new CALLPROC() {
			public void run(int nXSprite) {
				HackCallback(nXSprite);
			}
		};
		
		calebHack[LAND]	= new AISTATE(Type.other,0, hackCallback, 120, false, false, false, calebChase[LAND] );
		calebHack[WATER] = new AISTATE(Type.other,10, hackCallback, 0, false, false, true, calebChase[WATER] ) {
			public void think(SPRITE sprite, XSPRITE xsprite) {
				thinkChaseWater(sprite, xsprite);
			}
		};
		calebSearch[LAND] = new AISTATE(Type.search, 6, null, 120, false, true, true, calebIdle[LAND]) {
			public void move(SPRITE sprite, XSPRITE xsprite) {
				aiMoveForward(sprite, xsprite);
			}
			public void think(SPRITE sprite, XSPRITE xsprite) {
				thinkSearch(sprite, xsprite);
			}
		};
		calebSearch[WATER] = new AISTATE(Type.search, 8, null, 120, false, true, true, calebIdle[WATER]) {
			public void move(SPRITE sprite, XSPRITE xsprite) {
				aiMoveForward(sprite, xsprite);
			}
			public void think(SPRITE sprite, XSPRITE xsprite) {
				thinkSearch(sprite, xsprite);
			}
		};
		calebRecoil[LAND] = new AISTATE(Type.other, 5, null, 0,	false,	false, false, calebDodge[LAND] );
		calebRecoil[WATER] = new AISTATE(Type.other, 5, null, 0,	false,	false, false, calebDodge[WATER] );
		calebRTesla = new AISTATE(Type.other, 4, null, 0,	false,	false, false, calebDodge[LAND] );
		calebMoveTarget = new AISTATE(Type.other, 8, null, 0, false, true, true, calebChase[WATER]) {
			public void move(SPRITE sprite, XSPRITE xsprite) {
				myMoveTarget(sprite, xsprite);
			}
			public void think(SPRITE sprite, XSPRITE xsprite) {
				thinkChaseWater(sprite, xsprite);
			}
		};
	}
	
	private static void HackCallback(int nXSprite)
	{
		int nSprite = xsprite[nXSprite].reference;
		SPRITE pSprite = sprite[nSprite];
		
		int dx = Cos(pSprite.ang) >> 16;
		int dy = Sin(pSprite.ang) >> 16;
		int dz = gDudeSlope[nXSprite];
		
		dx += BiRandom( 1500 );
		dy += BiRandom( 1500 );
		dz += BiRandom( 1500 );
		
		for(int i = 0; i < 2; i++) {
			int vz = dz + BiRandom2( 500 );
			int vy = dy + BiRandom2( 1000 );
			int vx = dx + BiRandom2( 1000 );
			actFireVector(pSprite, 0, 0, vx, vy, vz, kVectorShell);
		}
		
		if(Chance(0x4000))
			sfxStart3DSound(pSprite, Random(5) + 10000, -1, 0);
		
		if(Chance(0x4000))
			sfxStart3DSound(pSprite, 1001, -1, 0);
		else sfxStart3DSound(pSprite, 1002, -1, 0);
	}
	
	private static void thinkSearch( SPRITE pSprite, XSPRITE pXSprite )
	{
		aiChooseDirection(pSprite, pXSprite, pXSprite.goalAng);
		aiThinkTarget(pSprite, pXSprite);
	}
	
	private static void thinkGoto( SPRITE pSprite, XSPRITE pXSprite )
	{
		int dx, dy, dist;

		if(!IsDudeSprite(pSprite))
			return;
		
		DudeInfo pDudeInfo = dudeInfo[pSprite.lotag - kDudeBase];
		
		int nXSector = sector[pSprite.sectnum].extra;
		
		XSECTOR pXSector = null;
		if ( nXSector > 0 )
			pXSector = xsector[nXSector];

		dx = pXSprite.targetX - pSprite.x;
		dy = pXSprite.targetY - pSprite.y;

		int nAngle = engine.getangle(dx, dy);
		dist = (int) engine.qdist(dx, dy);

		aiChooseDirection(pSprite, pXSprite, nAngle);

		// if reached target, change to search mode
		if ( dist < M2X(1.0) && klabs(pSprite.ang - nAngle) < pDudeInfo.periphery ) {
			if( pXSector != null && pXSector.Underwater )
				aiNewState(pSprite, pXSprite, calebSearch[WATER]);
			else
				aiNewState(pSprite, pXSprite, calebSearch[LAND]);
		}

		aiThinkTarget(pSprite, pXSprite);
	}
	
	private static void thinkGotoWater( SPRITE pSprite, XSPRITE pXSprite )
	{
		int dx, dy, dist;

		if(!IsDudeSprite(pSprite))
			return;
		
		DudeInfo pDudeInfo = dudeInfo[pSprite.lotag - kDudeBase];

		dx = pXSprite.targetX - pSprite.x;
		dy = pXSprite.targetY - pSprite.y;

		int nAngle = engine.getangle(dx, dy);
		dist = (int) engine.qdist(dx, dy);

		aiChooseDirection(pSprite, pXSprite, nAngle);

		// if reached target, change to search mode
		if ( dist < M2X(1.0) && klabs(pSprite.ang - nAngle) < pDudeInfo.periphery ) {
			aiNewState(pSprite, pXSprite, calebSearch[WATER]);
		}

		aiThinkTarget(pSprite, pXSprite);
	}
	
	private static void thinkChase( SPRITE pSprite, XSPRITE pXSprite )
	{
		int dx, dy, dist;
		
		int nXSector = sector[pSprite.sectnum].extra;
		XSECTOR pXSector = null;
		if ( nXSector > 0 )
			pXSector = xsector[nXSector];
		
		if ( pXSprite.target == -1)
		{
			if(pXSector != null && pXSector.Underwater)
				aiNewState(pSprite, pXSprite, calebSearch[WATER]);
			else aiNewState(pSprite, pXSprite, calebSearch[LAND]);
			return;
		}
		
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
			if(pXSector != null && pXSector.Underwater)
				aiNewState(pSprite, pXSprite, calebSearch[WATER]);
			else {
				aiPlaySound(pSprite, Random(4) + 11000, 1, -1);
				aiNewState(pSprite, pXSprite, calebSearch[LAND]);
			}
			return;
		}
		if ( IsPlayerSprite( pTarget ) )
		{
			PLAYER pPlayer = gPlayer[ pTarget.lotag - kDudePlayer1 ];
			if ( powerupCheck( pPlayer, kItemLtdInvisibility - kItemBase ) > 0 )
			{
				aiNewState(pSprite, pXSprite, calebSearch[WATER]);
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
				if ( klabs(losAngle) <= pDudeInfo.periphery )
				{
					aiSetTarget(pXSprite, pXSprite.target);
					int nXSprite = sprite[pXSprite.reference].extra;
					if(dist > 0)
						gDudeSlope[nXSprite] = divscale(pTarget.z - pSprite.z, dist, 10);
					if(dist < 1433 && klabs(losAngle) < kAngle5) 
					{
						int hitType = HitScan(pSprite, pSprite.z, dx, dy, 0, pHitInfo, 16777280, 0);
						if ( hitType == SS_SPRITE && sprite[pHitInfo.hitsprite].lotag == pSprite.lotag) {
							if(pXSector != null && pXSector.Underwater)
								aiNewState(pSprite, pXSprite, calebDodge[WATER]);
							else aiNewState(pSprite, pXSprite, calebDodge[LAND]);
							return;
						}

						if(pXSector != null && pXSector.Underwater)
							aiNewState(pSprite, pXSprite, calebHack[WATER]);
						else aiNewState(pSprite, pXSprite, calebHack[LAND]);
					}
				}
				return;
			} 
		}
		
		if(pXSector != null && pXSector.Underwater)
			aiNewState(pSprite, pXSprite, calebGoto[WATER]);
		else aiNewState(pSprite, pXSprite, calebGoto[LAND]);
		if(Chance(0x1000))
			sfxStart3DSound(pSprite, Random(5) + 10000, -1, 0);
		pXSprite.target = -1;
	
	}
	
	private static void thinkChaseWater( SPRITE pSprite, XSPRITE pXSprite )
	{
		int dx, dy, dist;
		
		if ( pXSprite.target == -1)
		{
			aiNewState(pSprite, pXSprite, calebGoto[WATER]);
			return;
		}
		
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
			aiNewState(pSprite, pXSprite, calebSearch[WATER]);
			return;
		}
		if ( IsPlayerSprite( pTarget ) )
		{
			PLAYER pPlayer = gPlayer[ pTarget.lotag - kDudePlayer1 ];
			if ( powerupCheck( pPlayer, kItemLtdInvisibility - kItemBase ) > 0 )
			{
				aiNewState(pSprite, pXSprite, calebSearch[WATER]);
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
				if ( klabs(losAngle) <= pDudeInfo.periphery )
				{
					aiSetTarget(pXSprite, pXSprite.target);

					if(dist < 1024 && klabs(losAngle) < kAngle15) {
						aiNewState(pSprite, pXSprite, calebHack[WATER]);
						return;
					}
					aiNewState(pSprite, pXSprite, calebMoveTarget);
					return;
				}
			} else
				aiNewState(pSprite, pXSprite, calebMoveTarget);
		} else {
			aiNewState(pSprite, pXSprite, calebGoto[WATER]);
			pXSprite.target = -1;
		}
	}
	
	private static void myMoveForward( SPRITE pSprite, XSPRITE pXSprite )
	{
		if(!IsDudeSprite(pSprite))
			return;
		
		DudeInfo pDudeInfo = dudeInfo[pSprite.lotag - kDudeBase];

		int maxTurn = pDudeInfo.angSpeed * kFrameTicks >> 4;
		int dang = ((kAngle180 + pXSprite.goalAng - pSprite.ang) & kAngleMask) - kAngle180;
		pSprite.ang = (short)((pSprite.ang + ClipRange(dang, -maxTurn, maxTurn)) & kAngleMask);
		
		// don't move forward if trying to turn around
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
			
			int fvel = kFrameTicks * pDudeInfo.frontSpeed;
			
			long vel = dmulscale(sprXVel[pSprite.xvel], cos, sprYVel[pSprite.xvel], sin, 30);
			long svel = dmulscale(sprXVel[pSprite.xvel], sin, -sprYVel[pSprite.xvel], cos, 30);
			
			if(pXSprite.target == -1)
				vel += fvel;
			else vel += (fvel >> 2);
			
			sprXVel[pSprite.xvel] = dmulscale(vel, cos, svel, sin, 30);
			sprYVel[pSprite.xvel] = dmulscale(vel, sin, -svel, cos, 30);
		}
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
			
			long vel = kFrameTicks * pDudeInfo.frontSpeed;
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
}
