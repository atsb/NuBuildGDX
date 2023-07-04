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

import static ru.m210projects.Blood.AI.Ai.EnemyAim;
import static ru.m210projects.Blood.AI.Ai.UpdateEnemyAim;
import static ru.m210projects.Blood.AI.Ai.aiActivateDude;
import static ru.m210projects.Blood.AI.Ai.aiActive;
import static ru.m210projects.Blood.AI.Ai.aiChooseDirection;
import static ru.m210projects.Blood.AI.Ai.aiMoveTurn;
import static ru.m210projects.Blood.AI.Ai.aiNewState;
import static ru.m210projects.Blood.AI.Ai.aiPlaySound;
import static ru.m210projects.Blood.AI.Ai.aiSetTarget;
import static ru.m210projects.Blood.AI.Ai.aiThinkTarget;
import static ru.m210projects.Blood.AI.Ai.aiThinkTarget2;
import static ru.m210projects.Blood.AI.Ai.aiThinkTime;
import static ru.m210projects.Blood.AI.Ai.gDudeSlope;
import static ru.m210projects.Blood.Actor.IsDudeSprite;
import static ru.m210projects.Blood.Actor.actFireMissile;
import static ru.m210projects.Blood.Actor.actFireThing;
import static ru.m210projects.Blood.Actor.actFireVector;
import static ru.m210projects.Blood.Actor.actHealDude;
import static ru.m210projects.Blood.Actor.kVectorGargSlash;
import static ru.m210projects.Blood.Actor.sprXVel;
import static ru.m210projects.Blood.Actor.sprYVel;
import static ru.m210projects.Blood.Actor.sprZVel;
import static ru.m210projects.Blood.DB.kDudeBase;
import static ru.m210projects.Blood.DB.kDudeFleshGargoyle;
import static ru.m210projects.Blood.DB.kDudePlayer1;
import static ru.m210projects.Blood.DB.kDudeStoneGargoyle;
import static ru.m210projects.Blood.DB.kItemBase;
import static ru.m210projects.Blood.DB.kItemLtdInvisibility;
import static ru.m210projects.Blood.DB.kMissileBone;
import static ru.m210projects.Blood.DB.kThingBoneClub;
import static ru.m210projects.Blood.DB.xsprite;
import static ru.m210projects.Blood.Gameutils.Chance;
import static ru.m210projects.Blood.Gameutils.ClipRange;
import static ru.m210projects.Blood.Gameutils.GetSpriteExtents;
import static ru.m210projects.Blood.Gameutils.HitScan;
import static ru.m210projects.Blood.Gameutils.IsPlayerSprite;
import static ru.m210projects.Blood.Gameutils.M2X;
import static ru.m210projects.Blood.Gameutils.Random;
import static ru.m210projects.Blood.Gameutils.bRandom;
import static ru.m210projects.Blood.Gameutils.extents_zBot;
import static ru.m210projects.Blood.Globals.SS_MASKED;
import static ru.m210projects.Blood.Globals.SS_SPRITE;
import static ru.m210projects.Blood.Globals.SS_WALL;
import static ru.m210projects.Blood.Globals.gPlayer;
import static ru.m210projects.Blood.Globals.kAngle15;
import static ru.m210projects.Blood.Globals.kAngle180;
import static ru.m210projects.Blood.Globals.kAngle45;
import static ru.m210projects.Blood.Globals.kAngle60;
import static ru.m210projects.Blood.Globals.kAngle90;
import static ru.m210projects.Blood.Globals.kAngleMask;
import static ru.m210projects.Blood.Globals.kFrameTicks;
import static ru.m210projects.Blood.Globals.kSeqDudeIdle;
import static ru.m210projects.Blood.Globals.kSeqDudeRecoil;
import static ru.m210projects.Blood.Main.engine;
import static ru.m210projects.Blood.PLAYER.powerupCheck;
import static ru.m210projects.Blood.SOUND.sfxStart3DSound;
import static ru.m210projects.Blood.Trig.Cos;
import static ru.m210projects.Blood.Trig.Sin;
import static ru.m210projects.Blood.Trigger.ksprite;
import static ru.m210projects.Blood.Types.DEMO.IsOriginalDemo;
import static ru.m210projects.Blood.Types.DudeInfo.dudeInfo;
import static ru.m210projects.Build.Engine.pHitInfo;
import static ru.m210projects.Build.Engine.sprite;
import static ru.m210projects.Build.Gameutils.isValidSprite;
import static ru.m210projects.Build.Net.Mmulti.connecthead;
import static ru.m210projects.Build.Net.Mmulti.connectpoint2;
import static ru.m210projects.Build.Pragmas.dmulscale;
import static ru.m210projects.Build.Pragmas.klabs;

import com.badlogic.gdx.math.Vector3;

import ru.m210projects.Blood.PLAYER;
import ru.m210projects.Blood.AI.AISTATEFUNC.Type;
import ru.m210projects.Blood.Types.CALLPROC;
import ru.m210projects.Blood.Types.DudeInfo;
import ru.m210projects.Blood.Types.WeaponAim;
import ru.m210projects.Blood.Types.XSPRITE;
import ru.m210projects.Build.Types.SPRITE;

public class AIGARG {
	
	public static AISTATE gargoyleIdle;
	public static AISTATE gargoyleSearch;
	public static AISTATE gargoyleChase;
	public static AISTATE gargoyleRecoil;
	public static AISTATE gargoyleGoto;
	public static AISTATE gargoyleFThrow;
	public static AISTATE gargoyleFSlash;
	public static AISTATE gargoyleSThrow;
	public static AISTATE gargoyleTurn;
	public static AISTATE gargoyleUp;
	public static AISTATE gargoyleDown;
	
	public static AISTATE statueIdle;
	public static AISTATE statueFTransform;
	public static AISTATE statueFChase;
	public static AISTATE statueSTransform;
	public static AISTATE statueSChase;

	public static AISTATE statueFTransformNew;
	public static AISTATE statueSTransformNew;
	
	private static final WeaponAim gGargData = new WeaponAim(65536, 65536, 256, 85, 0x1AAAAA);

	public static void Init()
	{
		statueIdle = new AISTATE(Type.idle,kSeqDudeIdle, null, 0, false, false, false, null);

		gargoyleIdle = new AISTATE(Type.idle,kSeqDudeIdle, null, 0, false, false, true, null) {
			public void think(SPRITE sprite, XSPRITE xsprite) {
				myThinkTarget(sprite, xsprite);
			}
		};
		
		statueFTransform = new AISTATE(Type.other,-1, null, 0, true, false, false, gargoyleIdle) {
			public void enter(SPRITE sprite, XSPRITE xsprite) {
				actHealDude(xsprite, dudeInfo[6].startHealth, dudeInfo[6].startHealth);
				sprite.lotag = kDudeFleshGargoyle;
			}
		};
		
		statueFTransformNew = new AISTATE(Type.other,5, null, 0, true, false, true, statueFTransform) {
			public void think(SPRITE sprite, XSPRITE xsprite) {
				aiPlaySound(sprite, 313, 1, -1);
			}
		};

		statueFChase = new AISTATE(Type.other,6, null, 0, false, false, false, statueFTransform);
		
		statueSTransform = new AISTATE(Type.other,-1, null, 0, true, false, false, gargoyleIdle) {
			public void enter(SPRITE sprite, XSPRITE xsprite) {
				actHealDude(xsprite, dudeInfo[7].startHealth, dudeInfo[7].startHealth);
				sprite.lotag = kDudeStoneGargoyle;
			}
		};
		
		statueSTransformNew = new AISTATE(Type.other,5, null, 0, true, false, true, statueSTransform) {
			public void think(SPRITE sprite, XSPRITE xsprite) {
				aiPlaySound(sprite, 313, 1, -1);
			}
		};
		
		statueSChase = new AISTATE(Type.other,6, null, 0, false, false, false, statueSTransform);
		
		gargoyleGoto = new AISTATE(Type.tgoto,kSeqDudeIdle, null, 600, false, true, true, gargoyleIdle) {
			public void move(SPRITE sprite, XSPRITE xsprite) {
				myMoveForward(sprite, xsprite);
			}
			public void think(SPRITE sprite, XSPRITE xsprite) {
				thinkGoto(sprite, xsprite);
			}
		};
		gargoyleSearch = new AISTATE(Type.search,kSeqDudeIdle, null, 120, false, true, true, gargoyleIdle) {
			public void move(SPRITE sprite, XSPRITE xsprite) {
				myMoveForward(sprite, xsprite);
			}
			public void think(SPRITE sprite, XSPRITE xsprite) {
				thinkSearch(sprite, xsprite);
			}
		};
		gargoyleChase = new AISTATE(Type.other,kSeqDudeIdle, null, 0, false, true, true, gargoyleIdle) {
			public void move(SPRITE sprite, XSPRITE xsprite) {
				myMoveForward(sprite, xsprite);
			}
			public void think(SPRITE sprite, XSPRITE xsprite) {
				thinkChase(sprite, xsprite);
			}
		};
		gargoyleRecoil = new AISTATE(Type.other,kSeqDudeRecoil, null, 0, false, false, false, gargoyleChase);
		gargoyleFSlash = new AISTATE(Type.other,6, new CALLPROC() {
			public void run(int nXSprite) {
				SlashCallback(nXSprite);
			}
		}, 120, false, false, false, gargoyleChase);
		gargoyleFThrow = new AISTATE(Type.other,6, new CALLPROC() {
			public void run(int nXSprite) {
				FThrowCallback(nXSprite);
			}
		}, 120, false, false, false, gargoyleChase);
		gargoyleSThrow = new AISTATE(Type.other,7, new CALLPROC() {
			public void run(int nXSprite) {
				SThrowCallback(nXSprite);
			}
		}, 60, false, true, false, gargoyleChase) {
			public void move(SPRITE sprite, XSPRITE xsprite) {
				myMoveAttack(sprite, xsprite);
			}
		};
		gargoyleDown = new AISTATE(Type.other,kSeqDudeIdle, null, 120, false, true, true, gargoyleChase) {
			public void move(SPRITE sprite, XSPRITE xsprite) {
				myMoveDown(sprite, xsprite);
			}
			public void think(SPRITE sprite, XSPRITE xsprite) {
				thinkChase(sprite, xsprite);
			}
		};
		gargoyleUp = new AISTATE(Type.other,kSeqDudeIdle, null, 120, false, true, true, gargoyleChase) {
			public void move(SPRITE sprite, XSPRITE xsprite) {
				myMoveUp(sprite, xsprite);
			}
			public void think(SPRITE sprite, XSPRITE xsprite) {
				thinkChase(sprite, xsprite);
			}
		};

		gargoyleTurn = new AISTATE(Type.other,kSeqDudeIdle, null, 120, false, true, false, gargoyleChase) {
			public void move(SPRITE sprite, XSPRITE xsprite) {
				aiMoveTurn(sprite, xsprite);
			}
		};
	}
	
	private static void FThrowCallback( int nXIndex )
	{
		actFireThing(xsprite[nXIndex].reference, 0, 0, gDudeSlope[nXIndex] - 7500, kThingBoneClub, 978670);
	}
	
	private static void SThrowCallback( int nXIndex )
	{
		XSPRITE pXSprite = xsprite[nXIndex];
		int nSprite = pXSprite.reference;
		SPRITE pSprite = sprite[nSprite];
		SPRITE pTarget = pXSprite.target != -1 ? sprite[pXSprite.target] : null;
		bRandom();
		
		UpdateEnemyAim(pSprite, nXIndex, gGargData);

		if(IsPlayerSprite(pTarget) || !IsOriginalDemo()) {
			actFireMissile(pSprite, -120, 0, (int)EnemyAim.x, (int)EnemyAim.y, (int)EnemyAim.z, kMissileBone);
			actFireMissile(pSprite, 120, 0, (int)EnemyAim.x, (int)EnemyAim.y, (int)EnemyAim.z, kMissileBone);
		}
	}
	
	private static void SlashCallback( int nXIndex )
	{
		XSPRITE pXSprite = xsprite[nXIndex];
		int nSprite = pXSprite.reference;
		SPRITE pSprite = sprite[nSprite];
		
		if(!IsDudeSprite(pSprite))
			return;
		
		int dx = Cos(pSprite.ang) >> 16;
		int dy = Sin(pSprite.ang) >> 16;
		int dz;
		
		if(IsOriginalDemo()) {
			int nZOffset1 = dudeInfo[pSprite.lotag - kDudeBase].eyeHeight * pSprite.yrepeat << 2;
			int nZOffset2 = 0;
			if(pXSprite.target != -1)
			{
				SPRITE pTarget = sprite[pXSprite.target];
				if(IsDudeSprite(pTarget))
					nZOffset2 = dudeInfo[pTarget.lotag - kDudeBase].eyeHeight * pTarget.yrepeat << 2;
			}
			dz = nZOffset1 - nZOffset2;
		} else {
			dz = dudeInfo[pSprite.lotag - kDudeBase].eyeHeight * pSprite.yrepeat << 2;
			if(pXSprite.target != -1)
			{
				SPRITE pTarget = sprite[pXSprite.target];
				dz = pTarget.z - pSprite.z;
			}
		}

		int vx = dx, vy = dy, vz = dz;	
		actFireVector(pSprite, 0, 0, vx, vy, vz, kVectorGargSlash);
		vy = dy - Random(50);
		vx = dx + Random(50);
		actFireVector(pSprite, 0, 0, vx, vy, vz, kVectorGargSlash);
		vy = dy + Random(50);
		vx = dx - Random(50);
		actFireVector(pSprite, 0, 0, vx, vy, vz, kVectorGargSlash);
	}
	
	private static void myMoveAttack( SPRITE pSprite, XSPRITE pXSprite )
	{
		if(!IsDudeSprite(pSprite))
			return;
		
		DudeInfo pDudeInfo = dudeInfo[pSprite.lotag - kDudeBase];

		int maxTurn = pDudeInfo.angSpeed * kFrameTicks >> 4;
		int dang = ((kAngle180 + pXSprite.goalAng - pSprite.ang) & kAngleMask) - kAngle180;
		pSprite.ang = (short)((pSprite.ang + ClipRange(dang, -maxTurn, maxTurn)) & kAngleMask);

		// don't move forward if trying to turn around
		if ( klabs(dang) > kAngle60 ) {
			pXSprite.goalAng = (short) ((pSprite.ang + kAngle90) & kAngleMask);
			return;
		}

		int dx = pXSprite.targetX - pSprite.x;
		int dy = pXSprite.targetY - pSprite.y;
		if(!Chance(0x300) || engine.qdist(dx, dy) > 1024 )
		{
			int sin = Sin(pSprite.ang);
			int cos = Cos(pSprite.ang);
			
			int fvel = kFrameTicks * pDudeInfo.frontSpeed;
			long svel = dmulscale(sprXVel[pSprite.xvel], sin, -sprYVel[pSprite.xvel], cos, 30);
			
			sprXVel[pSprite.xvel] = dmulscale(fvel >> 1, cos, svel >> 1, sin, 30);
			sprYVel[pSprite.xvel] = dmulscale(fvel >> 1, sin, -(svel >> 1), cos, 30);
			
			if(pSprite.lotag == 206)
				sprZVel[pSprite.xvel] = 279620;
			else if(pSprite.lotag == 207)
				sprZVel[pSprite.xvel] = 218453;
		}
	}
	
	private static void myMoveDown( SPRITE pSprite, XSPRITE pXSprite )
	{
		if(!IsDudeSprite(pSprite))
			return;
		
		DudeInfo pDudeInfo = dudeInfo[pSprite.lotag - kDudeBase];

		int maxTurn = pDudeInfo.angSpeed * kFrameTicks >> 4;
		int dang = ((kAngle180 + pXSprite.goalAng - pSprite.ang) & kAngleMask) - kAngle180;
		pSprite.ang = (short)((pSprite.ang + ClipRange(dang, -maxTurn, maxTurn)) & kAngleMask);

		// don't move forward if trying to turn around
		if ( klabs(dang) > kAngle60 ) {
			pXSprite.goalAng = (short) ((pSprite.ang + kAngle90) & kAngleMask);
			return;
		}

		int dx = pXSprite.targetX - pSprite.x;
		int dy = pXSprite.targetY - pSprite.y;
		if(!Chance(0x300) || engine.qdist(dx, dy) > 1024 )
		{
			int sin = Sin(pSprite.ang);
			int cos = Cos(pSprite.ang);
			
			long fvel = kFrameTicks * pDudeInfo.frontSpeed;
			long vel = dmulscale(sprXVel[pSprite.xvel], cos, sprYVel[pSprite.xvel], sin, 30);
			long svel = dmulscale(sprXVel[pSprite.xvel], sin, -sprYVel[pSprite.xvel], cos, 30);
			
			vel += fvel >> 1;
			sprXVel[pSprite.xvel] = dmulscale(vel, cos, svel, sin, 30);
			sprYVel[pSprite.xvel] = dmulscale(vel, sin, -svel, cos, 30);
			sprZVel[pSprite.xvel] = vel;
		}
	}
	
	private static void myMoveUp( SPRITE pSprite, XSPRITE pXSprite )
	{
		if(!IsDudeSprite(pSprite))
			return;
		
		DudeInfo pDudeInfo = dudeInfo[pSprite.lotag - kDudeBase];

		int maxTurn = pDudeInfo.angSpeed * kFrameTicks >> 4;
		int dang = ((kAngle180 + pXSprite.goalAng - pSprite.ang) & kAngleMask) - kAngle180;
		pSprite.ang = (short)((pSprite.ang + ClipRange(dang, -maxTurn, maxTurn)) & kAngleMask);

		// don't move forward if trying to turn around
		if ( klabs(dang) > kAngle60 ) {
			pSprite.ang = (short) ((pSprite.ang + kAngle90) & kAngleMask);
			return;
		}

		int dx = pXSprite.targetX - pSprite.x;
		int dy = pXSprite.targetY - pSprite.y;
		if( !Chance(0x2000) || engine.qdist(dx, dy) > 1024 )
		{
			int sin = Sin(pSprite.ang);
			int cos = Cos(pSprite.ang);
			
			long fvel = kFrameTicks * pDudeInfo.frontSpeed;
			
			long vel = dmulscale(sprXVel[pSprite.xvel], cos, sprYVel[pSprite.xvel], sin, 30);
			long svel = dmulscale(sprXVel[pSprite.xvel], sin, -sprYVel[pSprite.xvel], cos, 30);
			
			vel += fvel >> 1;
			
			sprXVel[pSprite.xvel] = dmulscale(vel, cos, svel, sin, 30);
			sprYVel[pSprite.xvel] = dmulscale(vel, sin, -svel, cos, 30);
			sprZVel[pSprite.xvel] = -vel;
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
			else vel += fvel >> 1;
			
			sprXVel[pSprite.xvel] = dmulscale(vel, cos, svel, sin, 30);
			sprYVel[pSprite.xvel] = dmulscale(vel, sin, -svel, cos, 30);
		}
	}

	private static void myThinkTarget( SPRITE pSprite, XSPRITE pXSprite )
	{
		if(!IsDudeSprite(pSprite))
			return;
		
		DudeInfo pDudeInfo = dudeInfo[pSprite.lotag - kDudeBase];
		if(aiActive[pSprite.xvel] != 0)
		{
			if ( aiThinkTime[pSprite.xvel] >= 10 )
			{
				pXSprite.goalAng += kAngle45;
				Vector3 kSprite = ksprite[pSprite.xvel];
				aiSetTarget(pXSprite, (int)kSprite.x, (int)kSprite.y, (int)kSprite.z);
				aiNewState(pSprite, pXSprite, gargoyleTurn);
				return;
			} else aiThinkTime[pSprite.xvel]++;
		}
		
		if(!Chance(pDudeInfo.alertChance / 2))
			return;
		
		for ( int i = connecthead; i >= 0; i = connectpoint2[i] )
	    {
			PLAYER pPlayer = gPlayer[i];
			
			if (pPlayer.pXsprite.health == 0
			|| powerupCheck( pPlayer, kItemLtdInvisibility - kItemBase ) > 0 )
				continue;

			int x = pPlayer.pSprite.x;
			int y = pPlayer.pSprite.y;
			int z = pPlayer.pSprite.z;
			short nSector = pPlayer.pSprite.sectnum;

			int dx = x - pSprite.x;
			int dy = y - pSprite.y;

			int dist = (int) engine.qdist(dx, dy);
			
			if ( dist <= pDudeInfo.seeDist || dist <= pDudeInfo.hearDist )
			{
				int eyeAboveZ = pDudeInfo.eyeHeight * pSprite.yrepeat << 2;

				// is there a line of sight to the player?
				if ( engine.cansee(x, y, z, nSector, pSprite.x, pSprite.y, pSprite.z - eyeAboveZ,
					pSprite.sectnum) )
				{
					int nAngle = engine.getangle(dx, dy);
					int losAngle = ((kAngle180 + nAngle - pSprite.ang) & kAngleMask) - kAngle180;
					
					if ( dist < pDudeInfo.seeDist && klabs(losAngle) <= pDudeInfo.periphery )
					{
						aiThinkTime[pSprite.xvel] = 0;
						aiSetTarget( pXSprite, pPlayer.nSprite );
						aiActivateDude( pSprite, pXSprite );
						return;
					}
					
					if ( dist < pDudeInfo.hearDist )
					{
						aiThinkTime[pSprite.xvel] = 0;
						aiSetTarget(pXSprite, x, y, z);
						aiActivateDude( pSprite, pXSprite );
						return;
					}
				}
			}
		}
	}
	
	private static void thinkSearch( SPRITE pSprite, XSPRITE pXSprite )
	{
		aiChooseDirection(pSprite, pXSprite, pXSprite.goalAng);
		aiThinkTarget2(pSprite, pXSprite);
	}
	
	private static void thinkGoto( SPRITE pSprite, XSPRITE pXSprite )
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
		if ( dist < M2X(1.0) && klabs(pSprite.ang - nAngle) < pDudeInfo.periphery )
			aiNewState(pSprite, pXSprite, gargoyleSearch);

		aiThinkTarget(pSprite, pXSprite);
	}
	
	private static void thinkChase( SPRITE pSprite, XSPRITE pXSprite )
	{
		if ( pXSprite.target == -1)
		{
			aiNewState(pSprite, pXSprite, gargoyleGoto);
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
			aiNewState(pSprite, pXSprite, gargoyleSearch);
			return;
		}

		if ( IsPlayerSprite( pTarget ) )
		{
			PLAYER pPlayer = gPlayer[ pTarget.lotag - kDudePlayer1 ];
			if ( powerupCheck( pPlayer, kItemLtdInvisibility - kItemBase ) > 0 )
			{
				aiNewState(pSprite, pXSprite, gargoyleSearch);
				return;
			}
		}

		dist = (int) engine.qdist(dx, dy);
		if ( dist <= pDudeInfo.seeDist )
		{
			int nAngle = engine.getangle(dx, dy);
			int losAngle = ((kAngle180 + nAngle - pSprite.ang) & kAngleMask) - kAngle180;
			int eyeAboveZ = pDudeInfo.eyeHeight * pSprite.yrepeat << 2;

			GetSpriteExtents(pSprite);

			// is there a line of sight to the target?
			if ( engine.cansee(pTarget.x, pTarget.y, pTarget.z, pTarget.sectnum,
				pSprite.x, pSprite.y, pSprite.z - eyeAboveZ, pSprite.sectnum) )
			{
				// is the target visible?
				if ( dist < pDudeInfo.seeDist && klabs(losAngle) <= pDudeInfo.periphery )
				{
					aiSetTarget(pXSprite, pXSprite.target);
					int floorz = engine.getflorzofslope(pSprite.sectnum, pSprite.x, pSprite.y);
					
					switch(pSprite.lotag)
					{
					case kDudeFleshGargoyle:
						if ( dist < 6144 && dist > 3072 && klabs(losAngle) < kAngle15 ) {
							int nInfo = HitScan(pSprite, pSprite.z, dx, dy, 0, pHitInfo, 16777280, 0);
							if(nInfo == SS_WALL || nInfo == SS_MASKED)
								return;
							
							if(nInfo != SS_SPRITE || (nInfo == SS_SPRITE && pSprite.lotag != sprite[pHitInfo.hitsprite].lotag && sprite[pHitInfo.hitsprite].lotag != kDudeStoneGargoyle))
							{
								sfxStart3DSound(pSprite, 1408, 0, 0);
								aiNewState(pSprite, pXSprite, gargoyleFThrow);
							}
						}
						else {
							if ( dist < 1024 ) {
								if( klabs(losAngle) < kAngle15 )
								{
									int nInfo = HitScan(pSprite, pSprite.z, dx, dy, 0, pHitInfo, 16777280, 0);
									if(nInfo == SS_WALL || nInfo == SS_MASKED)
										return;
									if(nInfo != SS_SPRITE || (nInfo == SS_SPRITE && pSprite.lotag != sprite[pHitInfo.hitsprite].lotag && sprite[pHitInfo.hitsprite].lotag != kDudeStoneGargoyle))
									{
										sfxStart3DSound(pSprite, 1406, 0, 0);
										aiNewState(pSprite, pXSprite, gargoyleFSlash);
									}
									
								}
							} 
							else
							{
								int targetZ = pDudeInfo.eyeHeight * pTarget.yrepeat << 2;
								if ( targetZ - eyeAboveZ <= 0x2000 && floorz - extents_zBot <= 0x2000
				                	|| dist >= 5120
				                    || dist <= 2560 )
				                {
				                	if ( targetZ - eyeAboveZ < 0x2000 || floorz - extents_zBot  < 0x2000 )
				                	{
				                		if( klabs(losAngle) < kAngle15 ) 
					                		aiPlaySound(pSprite, 1400, 1, -1);
				                	}
				                }
				                else
				                {
				                	aiPlaySound(pSprite, 1400, 1, -1);
				                	aiNewState(pSprite, pXSprite, gargoyleDown);
				            	}
							}
						}
						break;
					case kDudeStoneGargoyle:
						if ( dist < 6144 && dist > 3072 && klabs(losAngle) < kAngle15 ) {
							int nInfo = HitScan(pSprite, pSprite.z, dx, dy, 0, pHitInfo, 16777280, 0);
							if(nInfo == SS_WALL || nInfo == SS_MASKED)
								return;
							
							if(nInfo != SS_SPRITE || (nInfo == SS_SPRITE && pSprite.lotag != sprite[pHitInfo.hitsprite].lotag && sprite[pHitInfo.hitsprite].lotag != kDudeFleshGargoyle))
							{
								sfxStart3DSound(pSprite, 1457, 0, 0);
								aiNewState(pSprite, pXSprite, gargoyleSThrow);
							}
						}
						else {
							if ( dist < 1024 ) {
								if( klabs(losAngle) < kAngle15 )
								{
									int nInfo = HitScan(pSprite, pSprite.z, dx, dy, 0, pHitInfo, 16777280, 0);
									if(nInfo == SS_WALL || nInfo == SS_MASKED)
										return;
									if(nInfo != SS_SPRITE || (nInfo == SS_SPRITE && pSprite.lotag != sprite[pHitInfo.hitsprite].lotag && sprite[pHitInfo.hitsprite].lotag != kDudeFleshGargoyle))
									{
										sfxStart3DSound(pSprite, 1406, 0, 0);
										aiNewState(pSprite, pXSprite, gargoyleFSlash);
									}
								}
							} 
							else
							{
								int targetZ = pDudeInfo.eyeHeight * pTarget.yrepeat << 2;
								if ( targetZ - eyeAboveZ <= 0x2000 && floorz - extents_zBot <= 0x2000
				                	|| dist >= 5120
				                    || dist <= 2048 )
				                {
				                	if ( targetZ - eyeAboveZ < 0x2000 || floorz - extents_zBot  < 0x2000 )
				                	{
				                		if( klabs(losAngle) < kAngle15 ) {
					                		aiPlaySound(pSprite, 1450, 1, -1);
				                		}
				                	}
				                }
				                else
				                {
				                	aiPlaySound(pSprite, 1450, 1, -1);
				                	aiNewState(pSprite, pXSprite, gargoyleDown);
				            	}
							}
						}
						break;
					}
					return;
				}
			}
			else
			{
				aiNewState(pSprite, pXSprite, gargoyleUp);
				return;
			}
		} else {
			aiNewState(pSprite, pXSprite, gargoyleGoto);
			pXSprite.target = -1;
		}
	}
}
