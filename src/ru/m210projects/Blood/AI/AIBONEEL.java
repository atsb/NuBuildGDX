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
import static ru.m210projects.Blood.Actor.kVectorBoneelBite;
import static ru.m210projects.Blood.Actor.sprXVel;
import static ru.m210projects.Blood.Actor.sprYVel;
import static ru.m210projects.Blood.Actor.sprZVel;
import static ru.m210projects.Blood.DB.kDudeBase;
import static ru.m210projects.Blood.DB.kDudePlayer1;
import static ru.m210projects.Blood.DB.kItemBase;
import static ru.m210projects.Blood.DB.kItemLtdInvisibility;
import static ru.m210projects.Blood.DB.xsprite;
import static ru.m210projects.Blood.Gameutils.Chance;
import static ru.m210projects.Blood.Gameutils.ClipRange;
import static ru.m210projects.Blood.Gameutils.GetSpriteExtents;
import static ru.m210projects.Blood.Gameutils.IsPlayerSprite;
import static ru.m210projects.Blood.Gameutils.M2X;
import static ru.m210projects.Blood.Gameutils.extents_zBot;
import static ru.m210projects.Blood.Gameutils.extents_zTop;
import static ru.m210projects.Blood.Globals.gPlayer;
import static ru.m210projects.Blood.Globals.kAngle15;
import static ru.m210projects.Blood.Globals.kAngle180;
import static ru.m210projects.Blood.Globals.kAngle45;
import static ru.m210projects.Blood.Globals.kAngle60;
import static ru.m210projects.Blood.Globals.kAngleMask;
import static ru.m210projects.Blood.Globals.kFrameTicks;
import static ru.m210projects.Blood.Globals.kSeqDudeIdle;
import static ru.m210projects.Blood.Globals.kTimerRate;
import static ru.m210projects.Blood.Globals.pGameInfo;
import static ru.m210projects.Blood.Main.engine;
import static ru.m210projects.Blood.PLAYER.powerupCheck;
import static ru.m210projects.Blood.Trig.Cos;
import static ru.m210projects.Blood.Trig.Sin;
import static ru.m210projects.Blood.Trigger.ksprite;
import static ru.m210projects.Blood.Types.DEMO.IsOriginalDemo;
import static ru.m210projects.Blood.Types.DudeInfo.dudeInfo;
import static ru.m210projects.Build.Engine.sprite;
import static ru.m210projects.Build.Gameutils.isValidSprite;
import static ru.m210projects.Build.Net.Mmulti.connecthead;
import static ru.m210projects.Build.Net.Mmulti.connectpoint2;
import static ru.m210projects.Build.Pragmas.divscale;
import static ru.m210projects.Build.Pragmas.dmulscale;
import static ru.m210projects.Build.Pragmas.klabs;

import com.badlogic.gdx.math.Vector3;

import ru.m210projects.Blood.PLAYER;
import ru.m210projects.Blood.AI.AISTATEFUNC.Type;
import ru.m210projects.Blood.Types.CALLPROC;
import ru.m210projects.Blood.Types.DudeInfo;
import ru.m210projects.Blood.Types.XSPRITE;
import ru.m210projects.Build.Types.SPRITE;

public class AIBONEEL {

	public static AISTATE boneelIdle;
	public static AISTATE boneelChase;
	public static AISTATE boneelGoto;
	public static AISTATE boneelBite;
	public static AISTATE boneelRecoil;
	public static AISTATE boneelSearch;
	public static AISTATE boneelDown;
	public static AISTATE boneelUp;
	public static AISTATE boneelTurn;
	
	public static void Init() {
		boneelIdle = new AISTATE(Type.idle, kSeqDudeIdle, null, 0, false, false, true, null) {
			public void think(SPRITE sprite, XSPRITE xsprite) {
				myThinkTarget(sprite, xsprite);
			}
		};
		boneelChase = new AISTATE(Type.other,kSeqDudeIdle, null, 0, false, true, true, boneelIdle) {
			public void move(SPRITE sprite, XSPRITE xsprite) {
				myMoveForward(sprite, xsprite);
			}
			public void think(SPRITE sprite, XSPRITE xsprite) {
				thinkChase(sprite, xsprite);
			}
		};
		boneelGoto = new AISTATE(Type.tgoto,kSeqDudeIdle, null, 600, false, false, true, boneelIdle) {
			public void think(SPRITE sprite, XSPRITE xsprite) {
				thinkGoto(sprite, xsprite);
			}
		};
		boneelBite = new AISTATE(Type.other,7, new CALLPROC() {
			public void run(int nXSprite) {
				BiteCallback(nXSprite);
			}
		}, 60, false, false, false, boneelChase);
		boneelRecoil = new AISTATE(Type.other, 5, null, 1,	false,	false, false, boneelChase ) {
			public void callback(int nXSprite) {
				if(IsOriginalDemo()) 
					BiteCallback(nXSprite);
			}
		};
		
		boneelSearch = new AISTATE(Type.search,kSeqDudeIdle, null, 120, false, true, true, boneelIdle) {
			public void move(SPRITE sprite, XSPRITE xsprite) {
				myMoveForward(sprite, xsprite);
			}
			public void think(SPRITE sprite, XSPRITE xsprite) {
				thinkSearch(sprite, xsprite);
			}
		};
		boneelDown = new AISTATE(Type.other,kSeqDudeIdle, null, 60, false, true, true, boneelChase) {
			public void move(SPRITE sprite, XSPRITE xsprite) {
				myMoveDown(sprite, xsprite);
			}
			public void think(SPRITE sprite, XSPRITE xsprite) {
				thinkChase(sprite, xsprite);
			}
		};
		boneelUp = new AISTATE(Type.other,kSeqDudeIdle, null, 0, false, true, true, boneelChase) {
			public void move(SPRITE sprite, XSPRITE xsprite) {
				myMoveUp(sprite, xsprite);
			}
			public void think(SPRITE sprite, XSPRITE xsprite) {
				thinkChase(sprite, xsprite);
			}
		};
		boneelTurn = new AISTATE(Type.other,kSeqDudeIdle, null, 60, false, true, false, boneelChase) {
			public void move(SPRITE sprite, XSPRITE xsprite) {
				aiMoveTurn(sprite, xsprite);
			}
		};
	}
	
	private static void BiteCallback( int nXIndex )
	{
		XSPRITE pXSprite = xsprite[nXIndex];
		int nSprite = pXSprite.reference;
		SPRITE pSprite = sprite[nSprite];
		if(!IsDudeSprite(pSprite) || !isValidSprite(pXSprite.target))
			return;

		SPRITE pTarget = sprite[pXSprite.target];
		
		int dx = Cos(pSprite.ang) >> 16;
		int dy = Sin(pSprite.ang) >> 16;
		int dz;
		if(IsOriginalDemo()) {
			if(!IsDudeSprite(pTarget)) return;
			
			dz = dudeInfo[pTarget.lotag - kDudeBase].eyeHeight * pTarget.yrepeat << 2;
			dz -= dudeInfo[pSprite.lotag - kDudeBase].eyeHeight * pSprite.yrepeat << 2;
		} 
		else 
		{
			dz = dudeInfo[pSprite.lotag - kDudeBase].eyeHeight * pSprite.yrepeat;
			if(pXSprite.target != -1)
				dz = pTarget.z - pSprite.z;
		}

		actFireVector(pSprite, 0, 0, dx, dy, dz, kVectorBoneelBite);
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
			return;
		}

		int dx = pXSprite.targetX - pSprite.x;
		int dy = pXSprite.targetY - pSprite.y;
		if(!Chance(0x4000) || engine.qdist(dx, dy) > 921 )
		{
			int sin = Sin(pSprite.ang);
			int cos = Cos(pSprite.ang);
			
			long fvel = kFrameTicks * (pDudeInfo.frontSpeed - ( divscale(kFrameTicks - pGameInfo.nDifficulty, kTimerRate, 26) /  kTimerRate ) );
			
			long vel = dmulscale(sprXVel[pSprite.xvel], cos, sprYVel[pSprite.xvel], sin, 30);
			long svel = dmulscale(sprXVel[pSprite.xvel], sin, -sprYVel[pSprite.xvel], cos, 30);
			
			vel += fvel >> 1;
			sprXVel[pSprite.xvel] = dmulscale(vel, cos, svel, sin, 30);
			sprYVel[pSprite.xvel] = dmulscale(vel, sin, -svel, cos, 30);
			sprZVel[pSprite.xvel] = 139810;
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
			return;
		}

		int dx = pXSprite.targetX - pSprite.x;
		int dy = pXSprite.targetY - pSprite.y;
		if( !Chance(0x2000) || engine.qdist(dx, dy) > 921 )
		{
			int sin = Sin(pSprite.ang);
			int cos = Cos(pSprite.ang);
			
			long fvel = kFrameTicks * (pDudeInfo.frontSpeed - ( divscale(kFrameTicks - pGameInfo.nDifficulty, kTimerRate, 26) /  kTimerRate ) );

			long vel = dmulscale(sprXVel[pSprite.xvel], cos, sprYVel[pSprite.xvel], sin, 30);
			long svel = dmulscale(sprXVel[pSprite.xvel], sin, -sprYVel[pSprite.xvel], cos, 30);
			
			vel += fvel >> 1;
			
			sprXVel[pSprite.xvel] = dmulscale(vel, cos, svel, sin, 30);
			sprYVel[pSprite.xvel] = dmulscale(vel, sin, -svel, cos, 30);
			sprZVel[pSprite.xvel] = -32768;
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

		if(engine.qdist(dx, dy) > 921)
		{
			int sin = Sin(pSprite.ang);
			int cos = Cos(pSprite.ang);
			
			long fvel = kFrameTicks * (pDudeInfo.frontSpeed - ( divscale(kFrameTicks - pGameInfo.nDifficulty, kTimerRate, 26) /  kTimerRate ) );

			long vel = dmulscale(sprXVel[pSprite.xvel], cos, sprYVel[pSprite.xvel], sin, 30);
			long svel = dmulscale(sprXVel[pSprite.xvel], sin, -sprYVel[pSprite.xvel], cos, 30);
			
			if(pXSprite.target == -1)
				vel += fvel;
			else vel += fvel >> 1;
			
			sprXVel[pSprite.xvel] = dmulscale(vel, cos, svel, sin, 30);
			sprYVel[pSprite.xvel] = dmulscale(vel, sin, -svel, cos, 30);
		}
	}
	
	private static void thinkSearch( SPRITE pSprite, XSPRITE pXSprite )
	{
		aiChooseDirection(pSprite, pXSprite, pXSprite.goalAng);
		myThinkTarget(pSprite, pXSprite);
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
			aiNewState(pSprite, pXSprite, boneelSearch);

		myThinkTarget(pSprite, pXSprite);
	}
	
	
	private static void thinkChase( SPRITE pSprite, XSPRITE pXSprite )
	{
		if ( pXSprite.target == -1)
		{
			aiNewState(pSprite, pXSprite, boneelGoto);
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
			aiNewState(pSprite, pXSprite, boneelSearch);
			return;
		}

		if ( IsPlayerSprite( pTarget ) )
		{
			PLAYER pPlayer = gPlayer[ pTarget.lotag - kDudePlayer1 ];
			if ( powerupCheck( pPlayer, kItemLtdInvisibility - kItemBase ) > 0 )
			{
				aiNewState(pSprite, pXSprite, boneelSearch);
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
			int zTop1 = extents_zTop;
			GetSpriteExtents(pTarget);
			int zTop2 = extents_zTop;
			int zBot2 = extents_zBot;

			// is there a line of sight to the target?
			if ( engine.cansee(pTarget.x, pTarget.y, pTarget.z, pTarget.sectnum,
				pSprite.x, pSprite.y, pSprite.z - eyeAboveZ, pSprite.sectnum) )
			{
				// is the target visible?
				if ( dist < pDudeInfo.seeDist && klabs(losAngle) <= pDudeInfo.periphery )
				{
					aiSetTarget(pXSprite, pXSprite.target);

					if(dist <= 921 && klabs(losAngle) < kAngle15)
					{
						if(zTop2 > zTop1) {
							aiNewState(pSprite, pXSprite, boneelDown);
						} else aiNewState(pSprite, pXSprite, boneelBite);
						return;
					}

					if(zBot2 > zTop1 && klabs(losAngle) < kAngle15)
						aiNewState(pSprite, pXSprite, boneelDown);
					else 
					{
						if ( zTop2 < zTop1 && klabs(losAngle) < kAngle15)
							aiNewState(pSprite, pXSprite, boneelUp);
					}
				}
			} 
		} else {
			aiNewState(pSprite, pXSprite, boneelSearch);
			pXSprite.target = -1;
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
				aiThinkTime[pSprite.xvel] = 0;
				pXSprite.goalAng += kAngle45;
				Vector3 kSprite = ksprite[pSprite.xvel];
				aiSetTarget(pXSprite, (int)kSprite.x, (int)kSprite.y, (int)kSprite.z);
				aiNewState(pSprite, pXSprite, boneelTurn);
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
}
