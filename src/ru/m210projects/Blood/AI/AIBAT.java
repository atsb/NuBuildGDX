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

import static ru.m210projects.Blood.AI.Ai.aiActivateDude;
import static ru.m210projects.Blood.AI.Ai.aiActive;
import static ru.m210projects.Blood.AI.Ai.aiChooseDirection;
import static ru.m210projects.Blood.AI.Ai.aiMoveTurn;
import static ru.m210projects.Blood.AI.Ai.aiNewState;
import static ru.m210projects.Blood.AI.Ai.aiSetTarget;
import static ru.m210projects.Blood.AI.Ai.aiThinkTime;
import static ru.m210projects.Blood.Actor.IsDudeSprite;
import static ru.m210projects.Blood.Actor.actFireVector;
import static ru.m210projects.Blood.Actor.kVectorBatBite;
import static ru.m210projects.Blood.Actor.sprXVel;
import static ru.m210projects.Blood.Actor.sprYVel;
import static ru.m210projects.Blood.Actor.sprZVel;
import static ru.m210projects.Blood.DB.kDudeBase;
import static ru.m210projects.Blood.DB.kDudeMax;
import static ru.m210projects.Blood.DB.kDudePlayer1;
import static ru.m210projects.Blood.DB.kItemBase;
import static ru.m210projects.Blood.DB.kItemLtdInvisibility;
import static ru.m210projects.Blood.DB.xsprite;
import static ru.m210projects.Blood.Gameutils.Chance;
import static ru.m210projects.Blood.Gameutils.ClipRange;
import static ru.m210projects.Blood.Gameutils.GetSpriteExtents;
import static ru.m210projects.Blood.Gameutils.IsPlayerSprite;
import static ru.m210projects.Blood.Gameutils.M2X;
import static ru.m210projects.Blood.Gameutils.Random;
import static ru.m210projects.Blood.Gameutils.extents_zBot;
import static ru.m210projects.Blood.Globals.gPlayer;
import static ru.m210projects.Blood.Globals.kAngle15;
import static ru.m210projects.Blood.Globals.kAngle180;
import static ru.m210projects.Blood.Globals.kAngle45;
import static ru.m210projects.Blood.Globals.kAngle60;
import static ru.m210projects.Blood.Globals.kAngle90;
import static ru.m210projects.Blood.Globals.kAngleMask;
import static ru.m210projects.Blood.Globals.kFrameTicks;
import static ru.m210projects.Blood.Globals.kMaxSprites;
import static ru.m210projects.Blood.Globals.kSeqDudeIdle;
import static ru.m210projects.Blood.Main.*;
import static ru.m210projects.Blood.PLAYER.powerupCheck;
import static ru.m210projects.Blood.Trig.Cos;
import static ru.m210projects.Blood.Trig.Sin;
import static ru.m210projects.Blood.Trigger.ksprite;
import static ru.m210projects.Blood.Types.DudeInfo.dudeInfo;
import static ru.m210projects.Build.Engine.sector;
import static ru.m210projects.Build.Engine.sprite;
import static ru.m210projects.Build.Net.Mmulti.connecthead;
import static ru.m210projects.Build.Net.Mmulti.connectpoint2;
import static ru.m210projects.Build.Pragmas.dmulscale;
import static ru.m210projects.Build.Pragmas.klabs;
import ru.m210projects.Blood.PLAYER;
import ru.m210projects.Blood.AI.AISTATEFUNC.Type;
import ru.m210projects.Blood.Types.CALLPROC;
import ru.m210projects.Blood.Types.DudeInfo;
import ru.m210projects.Blood.Types.XSPRITE;
import ru.m210projects.Build.Types.SPRITE;

import com.badlogic.gdx.math.Vector3;

public class AIBAT {
	
	public static AISTATE batSleep;
	public static AISTATE batIdle;
	public static AISTATE batSearch;
	public static AISTATE batChase;
	public static AISTATE batTurn;
	public static AISTATE batUp;
	public static AISTATE batDown;
	public static AISTATE batRecoil;
	public static AISTATE batGoto;
	public static AISTATE batBite;
	public static AISTATE batThinkMove;
	public static AISTATE batThinkIdle;
	public static AISTATE batDodgeUp;
	public static AISTATE batDodgeDown;

	public static void Init() {
		batSleep = new AISTATE(Type.idle, kSeqDudeIdle, null, 0, false, false, true, null) {
			public void think(SPRITE sprite, XSPRITE xsprite) {
				myThinkTarget(sprite, xsprite);
			}
		};
		batIdle = new AISTATE(Type.idle, 6, null, 0, false, false, true, null) {
			public void think(SPRITE sprite, XSPRITE xsprite) {
				myThinkTarget(sprite, xsprite);
			}
		};
		batChase = new AISTATE(Type.other, 6, null, 0, false, true, true, batIdle) {
			public void move(SPRITE sprite, XSPRITE xsprite) {
				myMoveForward(sprite, xsprite);
			}
			public void think(SPRITE sprite, XSPRITE xsprite) {
				thinkChase(sprite, xsprite);
			}
		};
		batThinkMove = new AISTATE(Type.other, 6, null, 0, false, false, true, null) {
			public void think(SPRITE sprite, XSPRITE xsprite) {
				thinkMove(sprite, xsprite);
			}
		};
		batGoto = new AISTATE(Type.tgoto, 6, null, 600, false, true, true, batIdle) {
			public void move(SPRITE sprite, XSPRITE xsprite) {
				myMoveForward(sprite, xsprite);
			}
			public void think(SPRITE sprite, XSPRITE xsprite) {
				thinkGoto(sprite, xsprite);
			}
		};
		batBite = new AISTATE(Type.attack, 7, new CALLPROC() {
			public void run(int i)
			{
				BiteCallback(i);
			}
		}, 60,	false,	false, false, batThinkMove );
		
		
		batRecoil = new AISTATE(Type.recoil, 5, null, 0,	false,	false, false, batChase );
		
		batSearch = new AISTATE(Type.search, 6,	null, 120, false, true,	true, batIdle) {
			public void move(SPRITE sprite, XSPRITE xsprite) {
				myMoveForward(sprite, xsprite);
			}
			public void think(SPRITE sprite, XSPRITE xsprite) {
				thinkSearch(sprite, xsprite);
			}
		};
		batUp = new AISTATE(Type.other, 6, null, 0,	false,	true, true, batChase ) {
			public void move(SPRITE sprite, XSPRITE xsprite) {
				myMoveUp(sprite, xsprite);
			}
			public void think(SPRITE sprite, XSPRITE xsprite) {
				thinkChase(sprite, xsprite);
			}
		};
		batDown = new AISTATE(Type.other, 6, null, 60,	false,	true, true, batChase ) {
			public void move(SPRITE sprite, XSPRITE xsprite) {
				myMoveDown(sprite, xsprite);
			}
			public void think(SPRITE sprite, XSPRITE xsprite) {
				thinkChase(sprite, xsprite);
			}
		};
		batTurn = new AISTATE(Type.other, 6, null, 60,	false,	true, false, batChase ) {
			public void move(SPRITE sprite, XSPRITE xsprite) {
				aiMoveTurn(sprite, xsprite);
			}
		};
		batThinkIdle = new AISTATE(Type.other, 6, null, 0,	false,	true, true, null ) {
			public void move(SPRITE sprite, XSPRITE xsprite) {
				thinkIdle(sprite, xsprite);
			}
			public void think(SPRITE sprite, XSPRITE xsprite) {
				myMoveForward(sprite, xsprite);
			}
		};
		batDodgeUp = new AISTATE(Type.other, 6, null, 120,	false,	true, false, batChase ) {
			public void move(SPRITE sprite, XSPRITE xsprite) {
				myDodgeUp(sprite, xsprite);
			}
		};
		batDodgeDown = new AISTATE(Type.other, 6, null, 120,	false,	true, false, batChase ) {
			public void move(SPRITE sprite, XSPRITE xsprite) {
				myDodgeDown(sprite, xsprite);
			}
		};
	}
	
	private static void BiteCallback( int nXIndex )
	{
		XSPRITE pXSprite = xsprite[nXIndex];
		int nSprite = pXSprite.reference;
		SPRITE pSprite = sprite[nSprite];
		if(!(pSprite.lotag >= kDudeBase && pSprite.lotag < kDudeMax))
			game.dassert("pSprite.type >= kDudeBase && pSprite.type < kDudeMax");
		if(!(pXSprite.target >= 0 && pXSprite.target < kMaxSprites)) game.dassert("pXSprite.target >= 0 && pXSprite.target < kMaxSprites");
		SPRITE pTarget = sprite[pXSprite.target];
		
		int dx = Cos(pSprite.ang) >> 16;
		int dy = Sin(pSprite.ang) >> 16;
		
		int eyeAboveZ = dudeInfo[pSprite.lotag - kDudeBase].eyeHeight * pSprite.yrepeat << 2;
		
		int z1 = 0;
		if(pXSprite.target != -1) {
			if(IsDudeSprite(pTarget))
				z1 = dudeInfo[pTarget.lotag - kDudeBase].eyeHeight * pTarget.yrepeat << 2;
		}
		int dz = z1 - eyeAboveZ;
		
		actFireVector(pSprite, 0, 0, dx, dy, dz, kVectorBatBite);
	}
	
	private static void thinkIdle( SPRITE pSprite, XSPRITE pXSprite )
	{
		if ( pSprite.z - pXSprite.targetZ >= 4096 )
		{
			aiSetTarget(pXSprite, pSprite.x, pSprite.y, sector[pSprite.sectnum].ceilingz);
		}
		else
		{
			aiActive[pSprite.xvel] = 0;
			pSprite.hitag = 0;
			aiNewState(pSprite, pXSprite, batSleep);
		}
	}
	
	private static void myThinkTarget( SPRITE pSprite, XSPRITE pXSprite )
	{
		if(!(pSprite.lotag >= kDudeBase && pSprite.lotag < kDudeMax))
			game.dassert("pSprite.type >= kDudeBase && pSprite.type < kDudeMax");
		DudeInfo pDudeInfo = dudeInfo[pSprite.lotag - kDudeBase];
	
		if(aiActive[pSprite.xvel] != 0)
		{
			if ( aiThinkTime[pSprite.xvel] >= 10 )
			{
				aiThinkTime[pSprite.xvel] = 0;
				pXSprite.goalAng += kAngle45;
				Vector3 kSprite = ksprite[pSprite.xvel];
				aiSetTarget(pXSprite, (int)kSprite.x, (int)kSprite.y, (int)kSprite.z);
				aiNewState(pSprite, pXSprite, batTurn);
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
						aiSetTarget( pXSprite, pPlayer.nSprite );
						aiActivateDude( pSprite, pXSprite );
						return;
					}
					
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
	
	private static void myMoveForward( SPRITE pSprite, XSPRITE pXSprite )
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
		
		if(pXSprite.target == -1) 
			pSprite.ang = (short) ((pSprite.ang + kAngle45) & kAngleMask);

		int dx = pXSprite.targetX - pSprite.x;
		int dy = pXSprite.targetY - pSprite.y;

		if(Random(64) >= 32 || engine.qdist(dx, dy) > 512)
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

	private static void myMoveUp( SPRITE pSprite, XSPRITE pXSprite )
	{
		if(!(pSprite.lotag >= kDudeBase && pSprite.lotag < kDudeMax))
			game.dassert("pSprite.type >= kDudeBase && pSprite.type < kDudeMax");
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
		if( !Chance(0x2000) || engine.qdist(dx, dy) > 512 )
		{
			int sin = Sin(pSprite.ang);
			int cos = Cos(pSprite.ang);
			
			long fvel = kFrameTicks * pDudeInfo.frontSpeed;
			
			long vel = dmulscale(sprXVel[pSprite.xvel], cos, sprYVel[pSprite.xvel], sin, 30);
			long svel = dmulscale(sprXVel[pSprite.xvel], sin, -sprYVel[pSprite.xvel], cos, 30);
			
			vel += fvel >> 1;

			sprXVel[pSprite.xvel] = dmulscale(vel, cos, svel, sin, 30);
			sprYVel[pSprite.xvel] = dmulscale(vel, sin, -svel, cos, 30);
			sprZVel[pSprite.xvel] = -185685;
		}
	}

	private static void myMoveDown( SPRITE pSprite, XSPRITE pXSprite )
	{
		if(!(pSprite.lotag >= kDudeBase && pSprite.lotag < kDudeMax))
			game.dassert("pSprite.type >= kDudeBase && pSprite.type < kDudeMax");
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
		if(!Chance(0x300) || engine.qdist(dx, dy) > 512 )
		{
			int sin = Sin(pSprite.ang);
			int cos = Cos(pSprite.ang);
			
			long fvel = kFrameTicks * pDudeInfo.frontSpeed;
			long vel = dmulscale(sprXVel[pSprite.xvel], cos, sprYVel[pSprite.xvel], sin, 30);
			long svel = dmulscale(sprXVel[pSprite.xvel], sin, -sprYVel[pSprite.xvel], cos, 30);
			
			vel += fvel >> 1;
			sprXVel[pSprite.xvel] = dmulscale(vel, cos, svel, sin, 30);
			sprYVel[pSprite.xvel] = dmulscale(vel, sin, -svel, cos, 30);
			sprZVel[pSprite.xvel] = 279620;
		}
	}
	
	public static void myDodgeUp( SPRITE pSprite, XSPRITE pXSprite ) { 
		if(!(pSprite.lotag >= kDudeBase && pSprite.lotag < kDudeMax)) game.dassert("pSprite.type >= kDudeBase && pSprite.type < kDudeMax");
		DudeInfo pDudeInfo = dudeInfo[pSprite.lotag - kDudeBase];

		int dang = ((kAngle180 + pXSprite.goalAng - pSprite.ang) & kAngleMask) - kAngle180;
		int maxTurn = pDudeInfo.angSpeed * kFrameTicks >> 4;
		pSprite.ang = (short)((pSprite.ang + ClipRange(dang, -maxTurn, maxTurn)) & kAngleMask);

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
	    sprZVel[pSprite.xvel] = -338602;
	}
	
	public static void myDodgeDown( SPRITE pSprite, XSPRITE pXSprite ) { 
		if(!(pSprite.lotag >= kDudeBase && pSprite.lotag < kDudeMax)) game.dassert("pSprite.type >= kDudeBase && pSprite.type < kDudeMax");
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
	    sprZVel[pSprite.xvel] = 279620;
	}

	private static void thinkSearch( SPRITE pSprite, XSPRITE pXSprite )
	{
		aiChooseDirection(pSprite, pXSprite, pXSprite.goalAng);
		myThinkTarget(pSprite, pXSprite);
	}
	
	private static void thinkGoto( SPRITE pSprite, XSPRITE pXSprite )
	{
		int dx, dy, dist;

		if(!(pSprite.lotag >= kDudeBase && pSprite.lotag < kDudeMax)) game.dassert("pSprite.type >= kDudeBase && pSprite.type < kDudeMax");
		DudeInfo pDudeInfo = dudeInfo[pSprite.lotag - kDudeBase];

		dx = pXSprite.targetX - pSprite.x;
		dy = pXSprite.targetY - pSprite.y;

		int nAngle = engine.getangle(dx, dy);
		dist = (int) engine.qdist(dx, dy);

		aiChooseDirection(pSprite, pXSprite, nAngle);

		// if reached target, change to search mode
		if ( dist < M2X(1.0) && klabs(pSprite.ang - nAngle) < pDudeInfo.periphery )
			aiNewState(pSprite, pXSprite, batSearch);

		myThinkTarget(pSprite, pXSprite);
	}
	
	private static void thinkChase( SPRITE pSprite, XSPRITE pXSprite )
	{
		if ( pXSprite.target == -1)
		{
			aiNewState(pSprite, pXSprite, batGoto);
			return;
		}

		int dx, dy, dist;

		if(!(pSprite.lotag >= kDudeBase && pSprite.lotag < kDudeMax)) game.dassert("pSprite.type >= kDudeBase && pSprite.type < kDudeMax");
		DudeInfo pDudeInfo = dudeInfo[pSprite.lotag - kDudeBase];

		if(!(pXSprite.target >= 0 && pXSprite.target < kMaxSprites)) game.dassert("pXSprite.target >= 0 && pXSprite.target < kMaxSprites");
		SPRITE pTarget = sprite[pXSprite.target];
		XSPRITE pXTarget = pTarget.extra > 0 ? xsprite[pTarget.extra] : null;

		// check target
		dx = pTarget.x - pSprite.x;
		dy = pTarget.y - pSprite.y;

		aiChooseDirection(pSprite, pXSprite, engine.getangle(dx, dy));

		if ( pXTarget == null || pXTarget.health == 0 )
		{
			// target is dead
			aiNewState(pSprite, pXSprite, batSearch);
			return;
		}

		if ( IsPlayerSprite( pTarget ) )
		{
			PLAYER pPlayer = gPlayer[ pTarget.lotag - kDudePlayer1 ];
			if ( powerupCheck( pPlayer, kItemLtdInvisibility - kItemBase ) > 0 )
			{
				aiNewState(pSprite, pXSprite, batSearch);
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
					
					if(klabs(losAngle) < kAngle15)
					{
						int targetZ = pDudeInfo.eyeHeight * pTarget.yrepeat << 2;
						if ( targetZ - eyeAboveZ < 0x2000 && dist < 512 )	
			            {
							aiNewState(pSprite, pXSprite, batBite);
							return;
			            }
						if ( (targetZ - eyeAboveZ > 20480 || floorz - extents_zBot > 20480) && dist < 5120 && dist > 2048 )
						{
							aiNewState(pSprite, pXSprite, batDown);
							return;
						}
						  
						if ( targetZ - eyeAboveZ < 12288 || floorz - extents_zBot < 12288 )
						{
							aiNewState(pSprite, pXSprite, batUp);
							return;
						}
					}
					return;
				}
			} 
			else
			{
				aiNewState(pSprite, pXSprite, batUp);
				return;
			}
		} 
		
		aiNewState(pSprite, pXSprite, batThinkIdle);
		pXSprite.target = -1;
		
	}
	
	private static void thinkMove( SPRITE pSprite, XSPRITE pXSprite )
	{
		if ( pXSprite.target == -1)
		{
			aiNewState(pSprite, pXSprite, batSearch);
			return;
		}

		int dx, dy, dist;

		if(!(pSprite.lotag >= kDudeBase && pSprite.lotag < kDudeMax)) game.dassert("pSprite.type >= kDudeBase && pSprite.type < kDudeMax");
		DudeInfo pDudeInfo = dudeInfo[pSprite.lotag - kDudeBase];

		if(!(pXSprite.target >= 0 && pXSprite.target < kMaxSprites)) game.dassert("pXSprite.target >= 0 && pXSprite.target < kMaxSprites");
		SPRITE pTarget = sprite[pXSprite.target];
		XSPRITE pXTarget = pTarget.extra > 0 ? xsprite[pTarget.extra] : null;

		// check target
		dx = pTarget.x - pSprite.x;
		dy = pTarget.y - pSprite.y;

		aiChooseDirection(pSprite, pXSprite, engine.getangle(dx, dy));

		if ( pXTarget == null || pXTarget.health == 0 )
		{
			// target is dead
			aiNewState(pSprite, pXSprite, batSearch);
			return;
		}

		if ( IsPlayerSprite( pTarget ) )
		{
			PLAYER pPlayer = gPlayer[ pTarget.lotag - kDudePlayer1 ];
			if ( powerupCheck( pPlayer, kItemLtdInvisibility - kItemBase ) > 0 )
			{
				aiNewState(pSprite, pXSprite, batSearch);
				return;
			}
		}

		dist = (int) engine.qdist(dx, dy);
		if ( dist <= pDudeInfo.seeDist )
		{
			int nAngle = engine.getangle(dx, dy);
			int losAngle = ((kAngle180 + nAngle - pSprite.ang) & kAngleMask) - kAngle180;
			int eyeAboveZ = pDudeInfo.eyeHeight * pSprite.yrepeat << 2;
			int dz = (pDudeInfo.eyeHeight * pTarget.yrepeat << 2) - eyeAboveZ;
			
			GetSpriteExtents(pSprite);

			// is there a line of sight to the target?
			if ( engine.cansee(pTarget.x, pTarget.y, pTarget.z, pTarget.sectnum,
				pSprite.x, pSprite.y, pSprite.z - eyeAboveZ, pSprite.sectnum) )
			{
				aiSetTarget(pXSprite, pXSprite.target);
				if(klabs(losAngle) < kAngle15)
				{
					if ( dist < 5120 && dist > 2048 ) {
						if ( dz < 8192 ) {
							aiNewState(pSprite, pXSprite, batDodgeUp);
							return;
						}
						if ( dz > 24576 ) {
							aiNewState(pSprite, pXSprite, batDodgeDown);
							return;
						}
					}
					if( dist < 6144 && dist > 3072 )
					{
						if ( dz < 12288 ) {
							aiNewState(pSprite, pXSprite, batDodgeUp);
							return;
						}
						if ( dz > 20480 ) {
							aiNewState(pSprite, pXSprite, batDodgeDown);
							return;
						}
					}
					if ( dist < 512 || dist > 5120 ) {
						if ( dz < 8192 ) {
							aiNewState(pSprite, pXSprite, batDodgeUp);
							return;
						}
					}
				}
				if ( dz <= 16384 )
					aiNewState(pSprite, pXSprite, batDodgeUp);
				else
					aiNewState(pSprite, pXSprite, batDodgeDown);
				return;
			} 
		} 
		
		aiNewState(pSprite, pXSprite, batGoto);
		pXSprite.target = -1;
	}
}
