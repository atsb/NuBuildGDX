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

import static ru.m210projects.Blood.AI.Ai.aiChooseDirection;
import static ru.m210projects.Blood.AI.Ai.aiMoveDodge;
import static ru.m210projects.Blood.AI.Ai.aiMoveForward;
import static ru.m210projects.Blood.AI.Ai.aiNewState;
import static ru.m210projects.Blood.AI.Ai.aiSetTarget;
import static ru.m210projects.Blood.AI.Ai.aiThinkTarget;
import static ru.m210projects.Blood.AI.Ai.aiThinkTime;
import static ru.m210projects.Blood.Actor.IsDudeSprite;
import static ru.m210projects.Blood.Actor.actFireVector;
import static ru.m210projects.Blood.Actor.actSpawnDude;
import static ru.m210projects.Blood.Actor.kVectorSpiderBite;
import static ru.m210projects.Blood.Actor.sprXVel;
import static ru.m210projects.Blood.Actor.sprYVel;
import static ru.m210projects.Blood.Actor.sprZVel;
import static ru.m210projects.Blood.DB.kDudeBase;
import static ru.m210projects.Blood.DB.kDudeBlackSpider;
import static ru.m210projects.Blood.DB.kDudeBrownSpider;
import static ru.m210projects.Blood.DB.kDudeMotherSpider;
import static ru.m210projects.Blood.DB.kDudePlayer1;
import static ru.m210projects.Blood.DB.kDudeRedSpider;
import static ru.m210projects.Blood.DB.kItemBase;
import static ru.m210projects.Blood.DB.kItemInvulnerability;
import static ru.m210projects.Blood.DB.kItemLtdInvisibility;
import static ru.m210projects.Blood.DB.kItemShroomDelirium;
import static ru.m210projects.Blood.DB.xsprite;
import static ru.m210projects.Blood.Gameutils.BiRandom;
import static ru.m210projects.Blood.Gameutils.Chance;
import static ru.m210projects.Blood.Gameutils.ClipHigh;
import static ru.m210projects.Blood.Gameutils.HitScan;
import static ru.m210projects.Blood.Gameutils.IsPlayerSprite;
import static ru.m210projects.Blood.Gameutils.M2X;
import static ru.m210projects.Blood.Globals.SS_SPRITE;
import static ru.m210projects.Blood.Globals.gPlayer;
import static ru.m210projects.Blood.Globals.kAngle15;
import static ru.m210projects.Blood.Globals.kAngle180;
import static ru.m210projects.Blood.Globals.kAngleMask;
import static ru.m210projects.Blood.Globals.kSeqDudeIdle;
import static ru.m210projects.Blood.LEVELS.totalKills;
import static ru.m210projects.Blood.Main.engine;
import static ru.m210projects.Blood.Main.game;
import static ru.m210projects.Blood.PLAYER.powerupActivate;
import static ru.m210projects.Blood.PLAYER.powerupCheck;
import static ru.m210projects.Blood.Trig.Cos;
import static ru.m210projects.Blood.Trig.Sin;
import static ru.m210projects.Blood.Types.DudeInfo.dudeInfo;
import static ru.m210projects.Build.Engine.pHitInfo;
import static ru.m210projects.Build.Engine.sprite;
import static ru.m210projects.Build.Gameutils.isValidSprite;
import static ru.m210projects.Build.Pragmas.klabs;

import ru.m210projects.Blood.PLAYER;
import ru.m210projects.Blood.AI.AISTATEFUNC.Type;
import ru.m210projects.Blood.Types.CALLPROC;
import ru.m210projects.Blood.Types.DudeInfo;
import ru.m210projects.Blood.Types.XSPRITE;
import ru.m210projects.Build.Types.SPRITE;

public class AISPID {
	
	public static AISTATE spidIdle;
	public static AISTATE spidSearch;
	public static AISTATE spidChase;
	public static AISTATE spidDodge;
	public static AISTATE spidRecoil;
	public static AISTATE spidGoto;
	public static AISTATE spidBite;
	public static AISTATE spidJump;
	public static AISTATE spidSpawn;
	
	public static void Init() {
		spidIdle = new AISTATE(Type.idle,kSeqDudeIdle, null, 0, false, false, true, null) {
			public void think(SPRITE sprite, XSPRITE xsprite) {
				aiThinkTarget(sprite, xsprite);
			}
		};
		spidChase = new AISTATE(Type.other, 7, null, 0,	false,	true,	true, null) {
			public void move(SPRITE sprite, XSPRITE xsprite) {
				aiMoveForward(sprite, xsprite);
			}
			public void think(SPRITE sprite, XSPRITE xsprite) {
				thinkChase(sprite, xsprite);
			}
		};
		spidDodge = new AISTATE(Type.other, 7, null, 90,	false,	true, false, spidChase )
		{
			public void move(SPRITE sprite, XSPRITE xsprite) {
				aiMoveDodge(sprite, xsprite);
			}
		};
		spidGoto = new AISTATE(Type.tgoto, 7, null, 600, false,	true,	true, spidIdle) {
			public void move(SPRITE sprite, XSPRITE xsprite) {
				aiMoveForward(sprite, xsprite);
			}
			public void think(SPRITE sprite, XSPRITE xsprite) {
				thinkGoto(sprite, xsprite);
			}
		};
		spidSearch = new AISTATE(Type.search, 7,	null, 1800, false, true,	true, spidIdle) {
			public void move(SPRITE sprite, XSPRITE xsprite) {
				aiMoveForward(sprite, xsprite);
			}
			public void think(SPRITE sprite, XSPRITE xsprite) {
				thinkSearch(sprite, xsprite);
			}
		};
		spidBite = new AISTATE(Type.other,6, new CALLPROC() {
			public void run(int nXSprite) {
				BiteCallback(nXSprite);
			}
		}, 60, false, false, false, spidChase );
		
		spidJump = new AISTATE(Type.other,8, new CALLPROC() {
			public void run(int nXSprite) {
				JumpCallback(nXSprite);
			}
		}, 60, false, true, false, spidChase ) {
			public void move(SPRITE sprite, XSPRITE xsprite) {
				aiMoveForward(sprite, xsprite);
			}
		};
		spidSpawn = new AISTATE(Type.other,0, new CALLPROC() {
			public void run(int nXSprite) {
				SpawnCallback(nXSprite);
			}
		}, 60, false, false, false, spidIdle );
	}
	
	private static void SpawnCallback( int nXIndex )
	{
		XSPRITE pXSprite = xsprite[nXIndex];
		int nSprite = pXSprite.reference;
		SPRITE pSprite = sprite[nSprite];
		
		if(!IsDudeSprite(pSprite) || !isValidSprite(pXSprite.target))
			return;

		DudeInfo pDudeInfo = dudeInfo[pSprite.lotag - kDudeBase];
		SPRITE pTarget = sprite[pXSprite.target];
		
		int dx = pXSprite.targetX - pSprite.x;
		int dy = pXSprite.targetY - pSprite.y;

		int nAngle = engine.getangle(dx, dy);
		int dist = (int) engine.qdist(dx, dy);
		
		SPRITE pSpawned = null;
		if(IsPlayerSprite(pTarget))
		{
			if(aiThinkTime[nSprite] < 10)
			{
				int losAngle = pSprite.ang - nAngle;
				if(klabs(losAngle) <= pDudeInfo.periphery) {
					if(dist < 6656 && dist > 5120)
						pSpawned = actSpawnDude(pSprite, kDudeRedSpider, pSprite.clipdist);
					else if ( dist < 3072 || (dist < 5120 && dist > 3072) )
						pSpawned = actSpawnDude(pSprite, kDudeBrownSpider, pSprite.clipdist);
				}
			}
		}

		if(pSpawned != null)
		{
			aiThinkTime[nSprite]++;
			pSpawned.owner = (short) nSprite;
			totalKills++;
		}
	}
	
	private static void JumpCallback( int nXIndex )
	{
		XSPRITE pXSprite = xsprite[nXIndex];
		int nSprite = pXSprite.reference;
		SPRITE pSprite = sprite[nSprite];
		if(!IsDudeSprite(pSprite) || !isValidSprite(pXSprite.target))
			return;

		SPRITE pTarget = sprite[pXSprite.target];
		
		int dx = Cos(pSprite.ang) >> 16;
		int dy = Sin(pSprite.ang) >> 16;
		int dz = 0;
		
		dx += BiRandom(200);
		dy += BiRandom(200);
		dz += BiRandom(200);
		
		if(IsPlayerSprite(pTarget))
		{
			if( pSprite.lotag == kDudeBrownSpider || pSprite.lotag == kDudeBlackSpider )
			{
				dz += pTarget.z - pSprite.z;
				sprXVel[pSprite.xvel] =  dx << 16;
				sprYVel[pSprite.xvel] =  dy << 16;
				sprZVel[pSprite.xvel] =  dz << 16;
			}
		}
	}
	
	
	private static void spidAttack(XSPRITE pXDude, int add, int max)
	{
		if(pXDude == null)
			game.dassert("pXDude != null");
		SPRITE pDude = sprite[pXDude.reference];
		if(IsPlayerSprite(pDude))
		{
			PLAYER pPlayer = gPlayer[pDude.lotag - kDudePlayer1];
			pPlayer.blindEffect = ClipHigh(pPlayer.blindEffect + (add << 4), max << 4);
		}
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
		int dz = 0;

		// dispersal modifiers here
		dx += BiRandom(2000);
		dy += BiRandom(2000);
		dz += BiRandom(2000);
		
		if(IsPlayerSprite(pTarget))
		{
			if(HitScan(pSprite, pSprite.z, dx, dy, 0, pHitInfo, 16777280, 0) == SS_SPRITE)
			{
				if(IsPlayerSprite(sprite[pHitInfo.hitsprite]))
				{
					PLAYER pPlayer = gPlayer[sprite[pHitInfo.hitsprite].lotag - kDudePlayer1];
					switch(pSprite.lotag)
					{
						case kDudeBrownSpider:
							actFireVector(pSprite, 0, 0, dx, dy, pTarget.z - pSprite.z + dz, kVectorSpiderBite);
							if ( !pPlayer.godMode )
			                {
			                	if ( powerupCheck(pPlayer, kItemInvulnerability - kItemBase) <= 0 )
			                	{
			                		if ( Chance(0x2000) )
			                			powerupActivate(pPlayer, kItemShroomDelirium - kItemBase);
			                	}
			                }
							break;
						case kDudeRedSpider:
							actFireVector(pSprite, 0, 0, dx, dy, pTarget.z - pSprite.z + dz, kVectorSpiderBite);
							if ( Chance(0x2800) )
								spidAttack(xsprite[pTarget.extra], 4, 16);
							break;
						case kDudeBlackSpider:
							actFireVector(pSprite, 0, 0, dx, dy, pTarget.z - pSprite.z + dz, kVectorSpiderBite);
							spidAttack(xsprite[pTarget.extra], 8, 16);
							break;
						case kDudeMotherSpider:
							actFireVector(pSprite, 0, 0, dx, dy, pTarget.z - pSprite.z + dz, kVectorSpiderBite);
							actFireVector(pSprite, 0, 0, BiRandom(0x2000) + dx, BiRandom(0x2000) + dy, BiRandom(0x2000) + (pTarget.z - pSprite.z + dz), kVectorSpiderBite);
							spidAttack(xsprite[pTarget.extra], 8, 16);
							break;
					}
				}
			}
		}
	}
	
	private static void thinkSearch( SPRITE pSprite, XSPRITE pXSprite )
	{
		aiChooseDirection(pSprite, pXSprite, pXSprite.goalAng);
		aiThinkTarget(pSprite, pXSprite);
	}
	
	private static void thinkGoto( SPRITE pSprite, XSPRITE pXSprite )
	{
		int dx, dy, dist;

		if(!IsDudeSprite(pSprite)) return;
		DudeInfo pDudeInfo = dudeInfo[pSprite.lotag - kDudeBase];

		dx = pXSprite.targetX - pSprite.x;
		dy = pXSprite.targetY - pSprite.y;

		int nAngle = engine.getangle(dx, dy);
		dist = (int) engine.qdist(dx, dy);

		aiChooseDirection(pSprite, pXSprite, nAngle);

		// if reached target, change to search mode
		if ( dist < M2X(1.0) && klabs(pSprite.ang - nAngle) < pDudeInfo.periphery )
			aiNewState(pSprite, pXSprite, spidSearch);

		aiThinkTarget(pSprite, pXSprite);
	}
	
	private static void thinkChase( SPRITE pSprite, XSPRITE pXSprite )
	{
		if ( pXSprite.target == -1)
		{
			aiNewState(pSprite, pXSprite, spidGoto);
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
			aiNewState(pSprite, pXSprite, spidSearch);
			return;
		}

		if ( IsPlayerSprite( pTarget ) )
		{
			PLAYER pPlayer = gPlayer[ pTarget.lotag - kDudePlayer1 ];
			if ( powerupCheck( pPlayer, kItemLtdInvisibility - kItemBase ) > 0 )
			{
				aiNewState(pSprite, pXSprite, spidSearch);
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

					switch(pSprite.lotag)
					{
						case kDudeRedSpider:
						if ( dist < 921 && klabs(losAngle) < kAngle15 )
							aiNewState(pSprite, pXSprite, spidBite);
							break;
						case kDudeBrownSpider:
						case kDudeBlackSpider:
							if( dist > 921 && dist < 1843 ) {
								if(klabs(losAngle) < kAngle15) aiNewState(pSprite, pXSprite, spidJump);
							}
							else {
								if(dist < 921 && klabs(losAngle) < kAngle15) aiNewState(pSprite, pXSprite, spidBite);
							}
							break;
						case kDudeMotherSpider: 
							if( dist > 921 && dist < 1843 && klabs(losAngle) < kAngle15 ) 
								aiNewState(pSprite, pXSprite, spidJump);
							else if(Chance(0x4000))
								aiNewState(pSprite, pXSprite, spidSpawn);
							break;
					}

					return;
				}
			}
		}

		aiNewState(pSprite, pXSprite, spidGoto);
		pXSprite.target = -1;
	}
}
