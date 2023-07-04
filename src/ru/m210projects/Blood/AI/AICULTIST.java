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

import static ru.m210projects.Blood.AI.Ai.DUCK;
import static ru.m210projects.Blood.AI.Ai.LAND;
import static ru.m210projects.Blood.AI.Ai.WATER;
import static ru.m210projects.Blood.AI.Ai.aiChooseDirection;
import static ru.m210projects.Blood.AI.Ai.aiMoveDodge;
import static ru.m210projects.Blood.AI.Ai.aiMoveForward;
import static ru.m210projects.Blood.AI.Ai.aiMoveTurn;
import static ru.m210projects.Blood.AI.Ai.aiNewState;
import static ru.m210projects.Blood.AI.Ai.aiPlaySound;
import static ru.m210projects.Blood.AI.Ai.aiSetTarget;
import static ru.m210projects.Blood.AI.Ai.aiThinkTarget;
import static ru.m210projects.Blood.AI.Ai.aiThinkTarget2;
import static ru.m210projects.Blood.AI.Ai.gDudeSlope;
import static ru.m210projects.Blood.Actor.IsDudeSprite;
import static ru.m210projects.Blood.Actor.actFireMissile;
import static ru.m210projects.Blood.Actor.actFireThing;
import static ru.m210projects.Blood.Actor.actFireVector;
import static ru.m210projects.Blood.Actor.kAttrGravity;
import static ru.m210projects.Blood.Actor.kVectorBullet;
import static ru.m210projects.Blood.Actor.kVectorShell;
import static ru.m210projects.Blood.DB.kDudeBase;
import static ru.m210projects.Blood.DB.kDudeBeastCultist;
import static ru.m210projects.Blood.DB.kDudeDynamiteCultist;
import static ru.m210projects.Blood.DB.kDudePlayer1;
import static ru.m210projects.Blood.DB.kDudeShotgunCultist;
import static ru.m210projects.Blood.DB.kDudeTeslaCultist;
import static ru.m210projects.Blood.DB.kDudeTommyCultist;
import static ru.m210projects.Blood.DB.kItemBase;
import static ru.m210projects.Blood.DB.kItemLtdInvisibility;
import static ru.m210projects.Blood.DB.kThingTNTBundle;
import static ru.m210projects.Blood.DB.kThingTNTStick;
import static ru.m210projects.Blood.DB.xsprite;
import static ru.m210projects.Blood.EVENT.evPost;
import static ru.m210projects.Blood.Gameutils.BiRandom;
import static ru.m210projects.Blood.Gameutils.BiRandom2;
import static ru.m210projects.Blood.Gameutils.Chance;
import static ru.m210projects.Blood.Gameutils.HitScan;
import static ru.m210projects.Blood.Gameutils.IsPlayerSprite;
import static ru.m210projects.Blood.Gameutils.M2X;
import static ru.m210projects.Blood.Gameutils.Random;
import static ru.m210projects.Blood.Globals.SS_MASKED;
import static ru.m210projects.Blood.Globals.SS_SPRITE;
import static ru.m210projects.Blood.Globals.SS_WALL;
import static ru.m210projects.Blood.Globals.gPlayer;
import static ru.m210projects.Blood.Globals.kAngle15;
import static ru.m210projects.Blood.Globals.kAngle180;
import static ru.m210projects.Blood.Globals.kAngle5;
import static ru.m210projects.Blood.Globals.kAngleMask;
import static ru.m210projects.Blood.Globals.kSeqDudeAttack;
import static ru.m210projects.Blood.Globals.kSeqDudeIdle;
import static ru.m210projects.Blood.Globals.kSeqDudeRecoil;
import static ru.m210projects.Blood.Globals.kSeqDudeTesla;
import static ru.m210projects.Blood.Globals.kStatDude;
import static ru.m210projects.Blood.Globals.kStatExplosion;
import static ru.m210projects.Blood.Globals.kTimerRate;
import static ru.m210projects.Blood.Globals.pGameInfo;
import static ru.m210projects.Blood.Main.engine;
import static ru.m210projects.Blood.PLAYER.powerupCheck;
import static ru.m210projects.Blood.SOUND.sfxStart3DSound;
import static ru.m210projects.Blood.Trig.Cos;
import static ru.m210projects.Blood.Trig.Sin;
import static ru.m210projects.Blood.Types.DEMO.IsOriginalDemo;
import static ru.m210projects.Blood.Types.DudeInfo.dudeInfo;
import static ru.m210projects.Blood.Types.Seq.SeqHandling.GetInstance;
import static ru.m210projects.Blood.Types.Seq.SeqHandling.seqFrame;
import static ru.m210projects.Blood.VERSION.getSeq;
import static ru.m210projects.Blood.VERSION.kCultProneOffset;
import static ru.m210projects.Build.Engine.headspritesect;
import static ru.m210projects.Build.Engine.nextspritesect;
import static ru.m210projects.Build.Engine.pHitInfo;
import static ru.m210projects.Build.Engine.sprite;
import static ru.m210projects.Build.Gameutils.isValidSprite;
import static ru.m210projects.Build.Pragmas.divscale;
import static ru.m210projects.Build.Pragmas.klabs;

import ru.m210projects.Blood.PLAYER;
import ru.m210projects.Blood.AI.AISTATEFUNC.Type;
import ru.m210projects.Blood.Types.CALLPROC;
import ru.m210projects.Blood.Types.DudeInfo;
import ru.m210projects.Blood.Types.XSPRITE;
import ru.m210projects.Blood.Types.Seq.SeqInst;
import ru.m210projects.Build.Types.SPRITE;

public class AICULTIST {
	
	public static final int kSlopeThrow = -8192;
	
	public static AISTATE[] cultistIdle = new AISTATE[3];
	public static AISTATE[] cultistSearch = new AISTATE[3];
	public static AISTATE[] cultistGoto = new AISTATE[3];
	public static AISTATE 	cultistTThrow;
	public static AISTATE 	cultistSThrow;
	public static AISTATE 	cultistTESThrow;
	public static AISTATE[] cultistDodge = new AISTATE[3];
	public static AISTATE[] cultistChase = new AISTATE[3];
	public static AISTATE[] cultistTFire = new AISTATE[3];
	public static AISTATE[] cultistTESFire = new AISTATE[3];
	public static AISTATE[] cultistSFire = new AISTATE[3];
	public static AISTATE 	cultistRTesla;
	public static AISTATE[] cultistRecoil = new AISTATE[3];
	public static AISTATE   cultistSProne;
	public static AISTATE   cultistTProne;
	public static AISTATE   cultistTurn;
	public static AISTATE   cultistGThrow1;
	public static AISTATE   cultistGThrow2;
	
	public static void Init()
	{
		CALLPROC tommyCallback = new CALLPROC() {
			public void run(int nXSprite) {
				TommyCallback(nXSprite);
			}
		};
		
		CALLPROC teslaCallback = new CALLPROC() {
			public void run(int nXSprite) {
				TeslaCallback(nXSprite);
			}
		};
		
		CALLPROC shotCallback = new CALLPROC() {
			public void run(int nXSprite) {
				ShotCallback(nXSprite);
			}
		};
		
		cultistIdle[LAND] = new AISTATE(Type.idle, kSeqDudeIdle, null, 0, false, false, true, null) {
			public void think(SPRITE sprite, XSPRITE xsprite) {
				aiThinkTarget(sprite, xsprite);
			}
		};
		cultistIdle[WATER] = new AISTATE(Type.idle, 13, null, 0, false, false, true, null) {
			public void think(SPRITE sprite, XSPRITE xsprite) {
				aiThinkTarget(sprite, xsprite);
			}
		};
		cultistSearch[LAND] = new AISTATE(Type.search, 9,	null, 1800, false, true,	true, cultistIdle[LAND]) {
			public void move(SPRITE sprite, XSPRITE xsprite) {
				aiMoveForward(sprite, xsprite);
			}
			public void think(SPRITE sprite, XSPRITE xsprite) {
				thinkSearch(sprite, xsprite);
			}
		};
		cultistSearch[WATER] = new AISTATE(Type.search, 13,	null, 1800, false, true,	true, cultistIdle[WATER]) {
			public void move(SPRITE sprite, XSPRITE xsprite) {
				aiMoveForward(sprite, xsprite);
			}
			public void think(SPRITE sprite, XSPRITE xsprite) {
				thinkSearch(sprite, xsprite);
			}
		};
		cultistGoto[LAND] = new AISTATE(Type.tgoto, 9, null, 600, false,	true,	true, cultistIdle[LAND]) {
			public void move(SPRITE sprite, XSPRITE xsprite) {
				aiMoveForward(sprite, xsprite);
			}
			public void think(SPRITE sprite, XSPRITE xsprite) {
				thinkGoto(sprite, xsprite);
			}
		};
		cultistGoto[WATER] = new AISTATE(Type.tgoto, 13, null, 600, false,	true,	true, cultistIdle[WATER]) {
			public void move(SPRITE sprite, XSPRITE xsprite) {
				aiMoveForward(sprite, xsprite);
			}
			public void think(SPRITE sprite, XSPRITE xsprite) {
				thinkGoto(sprite, xsprite);
			}
		};
		cultistChase[LAND] = new AISTATE(Type.other, 9, null, 0, false,	true, true, null) {
			public void move(SPRITE sprite, XSPRITE xsprite) {
				aiMoveForward(sprite, xsprite);
			}
			public void think(SPRITE sprite, XSPRITE xsprite) {
				thinkChase(sprite, xsprite);
			}
		};
		cultistDodge[LAND] = new AISTATE(Type.other, 9, null, 90, false,	true,	false, cultistChase[LAND]) {
			public void move(SPRITE sprite, XSPRITE xsprite) {
				aiMoveDodge(sprite, xsprite);
			}
		};
		cultistChase[DUCK] = new AISTATE(Type.other, 14, null, 0, false, true, true, null) {
			public void move(SPRITE sprite, XSPRITE xsprite) {
				aiMoveForward(sprite, xsprite);
			}
			public void think(SPRITE sprite, XSPRITE xsprite) {
				thinkChase(sprite, xsprite);
			}
		};
		cultistDodge[DUCK] = new AISTATE(Type.other, 14, null, 90, false,	true,	false, cultistChase[DUCK]) {
			public void move(SPRITE sprite, XSPRITE xsprite) {
				aiMoveDodge(sprite, xsprite);
			}
		};
		cultistChase[WATER] = new AISTATE(Type.other, 13, null, 0, false, true, true, null) {
			public void move(SPRITE sprite, XSPRITE xsprite) {
				aiMoveForward(sprite, xsprite);
			}
			public void think(SPRITE sprite, XSPRITE xsprite) {
				thinkChase(sprite, xsprite);
			}
		};
		cultistDodge[WATER] = new AISTATE(Type.other, 13, null, 90, false,	true,	false, cultistChase[WATER]) {
			public void move(SPRITE sprite, XSPRITE xsprite) {
				aiMoveDodge(sprite, xsprite);
			}
		};
		cultistTFire[LAND] = new AISTATE(Type.other, kSeqDudeAttack, tommyCallback, 0, false, true, true, null) {
			public void move(SPRITE sprite, XSPRITE xsprite) {
				aiMoveTurn(sprite, xsprite);
			}
			public void think(SPRITE sprite, XSPRITE xsprite) {
				thinkChase(sprite, xsprite);
			}
		};
		cultistTFire[LAND].next = cultistTFire[LAND];
		cultistSFire[LAND] = new AISTATE(Type.other, kSeqDudeAttack, shotCallback, 60, false, false, false, cultistChase[LAND]);
		
		cultistTESFire[LAND] = new AISTATE(Type.other, kSeqDudeAttack, teslaCallback, 0, false, true, true, cultistChase[LAND]) {
			public void move(SPRITE sprite, XSPRITE xsprite) {
				aiMoveTurn(sprite, xsprite);
			}
			public void think(SPRITE sprite, XSPRITE xsprite) {
				thinkChase(sprite, xsprite);
			}
		};
		cultistTFire[WATER] = new AISTATE(Type.other, 8, tommyCallback, 0, false, true, true, null) {
			public void move(SPRITE sprite, XSPRITE xsprite) {
				aiMoveTurn(sprite, xsprite);
			}
			public void think(SPRITE sprite, XSPRITE xsprite) {
				thinkChase(sprite, xsprite);
			}
		};
		cultistTFire[WATER].next = cultistTFire[WATER];
		cultistSFire[WATER] = new AISTATE(Type.other, 8, shotCallback, 60, false, false, false, cultistChase[WATER]);
		
		cultistTESFire[WATER] = new AISTATE(Type.other, 8, teslaCallback, 0, false, true, true, null){
			public void move(SPRITE sprite, XSPRITE xsprite) {
				aiMoveTurn(sprite, xsprite);
			}
			public void think(SPRITE sprite, XSPRITE xsprite) {
				thinkChase(sprite, xsprite);
			}
		};
		cultistTESFire[WATER].next = cultistTESFire[WATER];
		
		cultistTFire[DUCK] = new AISTATE(Type.other, 8, tommyCallback, 0, false, true, true, null) {
			public void move(SPRITE sprite, XSPRITE xsprite) {
				aiMoveTurn(sprite, xsprite);
			}
			public void think(SPRITE sprite, XSPRITE xsprite) {
				thinkChase(sprite, xsprite);
			}
		};
		cultistTFire[DUCK].next = cultistTFire[DUCK];
		cultistSFire[DUCK] = new AISTATE(Type.other, 8, shotCallback, 60, false, false, false, cultistChase[DUCK]);
		cultistTESFire[DUCK] = new AISTATE(Type.other, 8, teslaCallback, 0, false, true, true, null) {
			public void move(SPRITE sprite, XSPRITE xsprite) {
				aiMoveTurn(sprite, xsprite);
			}
			public void think(SPRITE sprite, XSPRITE xsprite) {
				if(!IsOriginalDemo())
					thinkChase(sprite, xsprite);
			}
		};
		
		CALLPROC throwCallback = new CALLPROC() {
			public void run(int nXSprite) {
				ThrowCallback(nXSprite);
			}
		};
		
		cultistTESFire[DUCK].next = cultistTESFire[DUCK];
		cultistTThrow = new AISTATE(Type.other, 7, throwCallback, 120, false,	false,	false, cultistTFire[LAND]);
		cultistSThrow = new AISTATE(Type.other, 7, throwCallback, 120, false,	false,	false, cultistSFire[LAND]);
		cultistTESThrow = new AISTATE(Type.other, 7, throwCallback, 120, false,	false,	false, cultistTESFire[LAND]);
		
		cultistRTesla = new AISTATE(Type.other, kSeqDudeTesla, null, 0, false, false, false, cultistDodge[LAND]);
		cultistRecoil[LAND] = new AISTATE(Type.other, kSeqDudeRecoil, null, 0, false, false, false, null);
		cultistRecoil[WATER] = new AISTATE(Type.other, kSeqDudeRecoil, null, 0, false, false, false, cultistDodge[WATER]); 
		cultistRecoil[DUCK] = new AISTATE(Type.other, kSeqDudeRecoil, null, 0, false, false, false, null);
		cultistRecoil[LAND].next = cultistDodge[DUCK];
		cultistRecoil[DUCK].next = cultistDodge[LAND];
		
		cultistSProne = new AISTATE(Type.other, getSeq(kCultProneOffset), null, 0, false, false, true, null){
			public void think(SPRITE sprite, XSPRITE xsprite) {
				aiThinkTarget(sprite, xsprite);
			}
		};
		cultistTProne = new AISTATE(Type.other, getSeq(kCultProneOffset), null, 0, false, false, true, null){
			public void think(SPRITE sprite, XSPRITE xsprite) {
				aiThinkTarget(sprite, xsprite);
			}
		};
		cultistTurn = new AISTATE(Type.other, 0, null, 0, false, true, true, null){
			public void move(SPRITE sprite, XSPRITE xsprite) {
				aiMoveTurn(sprite, xsprite);
			}
			public void think(SPRITE sprite, XSPRITE xsprite) {
				thinkChase(sprite, xsprite);
			}
		};
		cultistGThrow1 = new AISTATE(Type.other, 7, throwCallback, 120, false,	false,	false, cultistChase[LAND]);
		cultistGThrow2 = new AISTATE(Type.other, 7, new CALLPROC() {
			public void run(int nXSprite) {
				GThrowCallback(nXSprite);
			}
		}, 120, false,	false,	false, cultistChase[LAND]);
	}
	
	private static final int[] TeslaChance = {
		0x2000,
		0x4000,
		0x8000,
		0xA000,
		0xE000
	};
	
	private static void TeslaCallback( int nXIndex )
	{
		XSPRITE pXSprite = xsprite[nXIndex];
		int nSprite = pXSprite.reference;
		SPRITE pSprite = sprite[nSprite];

		if(Chance(TeslaChance[pGameInfo.nDifficulty] >> 1)) {
			int dx = Cos(pSprite.ang) >> 16;
			int dy = Sin(pSprite.ang) >> 16;
			int dz = gDudeSlope[nXIndex];
	
			// dispersal modifiers here
			dx += BiRandom2(5000 - 1000 * pGameInfo.nDifficulty);
			dy += BiRandom2(5000 - 1000 * pGameInfo.nDifficulty);
			dz += BiRandom2(2500 - 500 * pGameInfo.nDifficulty);
	
			actFireMissile(pSprite, 0, 0, dx, dy, dz, 306);
			sfxStart3DSound(pSprite, 470, -1, 0);
		}
	}

	private static void TommyCallback( int nXIndex )
	{
		XSPRITE pXSprite = xsprite[nXIndex];
		int nSprite = pXSprite.reference;
		SPRITE pSprite = sprite[nSprite];

		int dx = Cos(pSprite.ang) >> 16;
		int dy = Sin(pSprite.ang) >> 16;
		int dz = gDudeSlope[nXIndex];

		// dispersal modifiers here
		dx += BiRandom2(5000 - 1000 * pGameInfo.nDifficulty);
		dy += BiRandom2(5000 - 1000 * pGameInfo.nDifficulty);
		dz += BiRandom2(2500 - 500 * pGameInfo.nDifficulty);

		actFireVector(pSprite, 0, 0, dx, dy, dz, kVectorBullet);
		sfxStart3DSound(pSprite, 4001, -1, 0); //kSfxTomFire
	}
	
	private static void ShotCallback( int nXIndex )
	{
		XSPRITE pXSprite = xsprite[nXIndex];
		int nSprite = pXSprite.reference;
		SPRITE pSprite = sprite[nSprite];

		int dx = Cos(pSprite.ang) >> 16;
		int dy = Sin(pSprite.ang) >> 16;
		int dz = gDudeSlope[nXIndex];

		// aim modifiers
		dx += BiRandom(4500 - 1000 * pGameInfo.nDifficulty);
		dy += BiRandom(4500 - 1000 * pGameInfo.nDifficulty);
		dz += BiRandom(2500 - 500 * pGameInfo.nDifficulty);
		
		for ( int i = 0; i < 8; i++ )
		{
			int ddz = dz + BiRandom2(500);
			int ddy = dy + BiRandom2(1000);
			int ddx = dx + BiRandom2(1000);

			actFireVector(pSprite, 0, 0, ddx, ddy, ddz, kVectorShell); //ok
		}
		
		if ( Chance(0x4000) )
			sfxStart3DSound(pSprite, 1001, -1, 0); //kSfxShotFire
		else
			sfxStart3DSound(pSprite, 1002, -1, 0);
	}
	
	private static void ThrowCallback( int nXIndex )
	{
		XSPRITE pXSprite = xsprite[nXIndex];
		int nSprite = pXSprite.reference;
		SPRITE pSprite = sprite[nSprite];
		
		if(!IsDudeSprite(pSprite))
			return;

		if(isValidSprite(pXSprite.target)) {
			SPRITE pTarget = sprite[pXSprite.target];
			sfxStart3DSound(pSprite, 455, -1, 0);
			int nTile = kThingTNTStick;
			if ( pGameInfo.nDifficulty > 2 )
				nTile = kThingTNTBundle;
			
			int dx = pTarget.x - pSprite.x;
			int dy = pTarget.y - pSprite.y;
			int dz = pTarget.z - pSprite.z;
	
			int dist = (int) engine.qdist(dx, dy);
	
			SPRITE pThing = actFireThing(nSprite, 0, 0, (dz / 128) - 14500, nTile, divscale(dist / 540, kTimerRate, 23));
			boolean chance = Chance(0x3000);
			if(dist <= 7680 && chance) 
				xsprite[pThing.extra].Impact = true;
			else evPost(pThing.xvel, SS_SPRITE, kTimerRate * Random(2) + kTimerRate, 1);
		} else if (!IsOriginalDemo()) 
			GThrowCallback(nXIndex);
	}
	
	private static void GThrowCallback( int nXIndex ) //Green cultist
	{
		XSPRITE pXSprite = xsprite[nXIndex];
		int nSprite = pXSprite.reference;
		SPRITE pSprite = sprite[nSprite];

		sfxStart3DSound(pSprite, 455, -1, 0); //kSfxTNTToss
		int nTile = kThingTNTStick;
		if ( pGameInfo.nDifficulty > 2 )
			nTile = kThingTNTBundle;
		  
		SPRITE pThing = actFireThing(nSprite, 0, 0, gDudeSlope[nXIndex] - 9460, nTile, 0x133333);
		
		evPost(pThing.xvel, SS_SPRITE, kTimerRate * Random(2) + 240, 1);
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
		if ( dist < M2X(10.0) && klabs(pSprite.ang - nAngle) < pDudeInfo.periphery ) {
			if( pXSprite.palette == 1 || pXSprite.palette == 2 )
				aiNewState(pSprite, pXSprite, cultistSearch[WATER]);
			else 
				aiNewState(pSprite, pXSprite, cultistSearch[LAND]);
		}
		aiThinkTarget(pSprite, pXSprite);
	}
	
	private static void thinkChase( SPRITE pSprite, XSPRITE pXSprite )
	{
		if ( pXSprite.target == -1)
		{
			if( pXSprite.palette == 1 || pXSprite.palette == 2 )
				aiNewState(pSprite, pXSprite, cultistGoto[WATER]);
			else
				aiNewState(pSprite, pXSprite, cultistGoto[LAND]);
			return;
		}

		int dx, dy, dist;
		
		if(!IsDudeSprite(pSprite) || !isValidSprite(pXSprite.target))
			return;

		DudeInfo pDudeInfo = dudeInfo[pSprite.lotag - kDudeBase];
		SPRITE pTarget = sprite[pXSprite.target];
		XSPRITE pXTarget = pTarget.extra > 0 ? xsprite[pTarget.extra] : null;
		if(!IsOriginalDemo() && !IsDudeSprite(pTarget))
			pXTarget = null;

		// check target
		dx = pTarget.x - pSprite.x;
		dy = pTarget.y - pSprite.y;

		aiChooseDirection(pSprite, pXSprite, engine.getangle(dx, dy));

		if ( pXTarget == null || pXTarget.health == 0 )
		{
			// target is dead
			if( pXSprite.palette == 1 || pXSprite.palette == 2 )
				aiNewState(pSprite, pXSprite, cultistSearch[WATER]);
			else
			{
				aiNewState(pSprite, pXSprite, cultistSearch[LAND]);
				if(pSprite.lotag == kDudeTommyCultist)
				{
					aiPlaySound(pSprite, Random(4) + 4021, 1, -1);
				} else
				{
					aiPlaySound(pSprite, Random(4) + 1021, 1, -1);
				}
			}
			return;
		}

		if ( IsPlayerSprite( pTarget ) )
		{
			PLAYER pPlayer = gPlayer[ pTarget.lotag - kDudePlayer1 ];
			if ( powerupCheck( pPlayer, kItemLtdInvisibility - kItemBase ) > 0 )
			{
				if( pXSprite.palette == 1 || pXSprite.palette == 2 )
					aiNewState(pSprite, pXSprite, cultistSearch[WATER]);
				else
					aiNewState(pSprite, pXSprite, cultistSearch[LAND]);
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
					if(dist > 0) gDudeSlope[sprite[pXSprite.reference].extra] = divscale(pTarget.z - pSprite.z, dist, 10);
					
					switch(pSprite.lotag)
					{
					case kDudeTommyCultist:
						/*
						if ( dist < 7680 && dist > 3584 && klabs(losAngle) < kAngle15 && pGameInfo.nDifficulty > 2
						&& !TargetNearExplosion(pTarget) && (pTarget.flags & kAttrGravity) != 0	
						&& ( IsPlayerSprite(pTarget) && gPlayer[pTarget.type - kDudePlayer1].Run )
						&& Chance(0x4000) ) {
							int pHit = HitScan(pSprite, pSprite.z, dx, dy, 0, pHitInfo, 16777280, 0);
							switch(pHit)
							{
								case SS_SPRITE:
									SPRITE pHitSprite = sprite[pHitInfo.hitsprite];
									if(pHitSprite.type != pSprite.type && pHitSprite.type != kDudeShotgunCultist)
									{
										if(pXSprite.palette != 1 && pXSprite.palette != 2) {
											aiNewState(pSprite, pXSprite, cultistTThrow);
											return;
										}
									}
									break;
								case SS_WALL:
								case SS_MASKED:
									return;
								default:
									if(pXSprite.palette != 1 && pXSprite.palette != 2) {
										aiNewState(pSprite, pXSprite, cultistTThrow);
										return;
									}
									break;
							}
						}
						else 
						*/
						if ( dist < 17920 && klabs(losAngle) < kAngle5 ) 
						{
							int state = checkAttackState(pSprite, pXSprite);
							int pHit = HitScan(pSprite, pSprite.z, dx, dy, 0, pHitInfo, 16777280, 0);
							if(pHit == -1)
							{
					            if ( state == 1 && checkDudeSeq(pSprite, 13) )
					            	aiNewState(pSprite, pXSprite, cultistTFire[WATER]);
								if(state == 2) aiNewState(pSprite, pXSprite, cultistTFire[DUCK]);
								if(state == 3) aiNewState(pSprite, pXSprite, cultistTFire[LAND]);
							} 
							else
							{
								if(pHit == 3) {
									int type = sprite[pHitInfo.hitsprite].lotag;
									if(type == pSprite.lotag || type == kDudeShotgunCultist) {
										if(state == 1) aiNewState(pSprite, pXSprite, cultistDodge[WATER]);
										if(state == 2) aiNewState(pSprite, pXSprite, cultistDodge[DUCK]);
										if(state == 3) aiNewState(pSprite, pXSprite, cultistDodge[LAND]);
										return;
									}
								} 
								
								if(state == 1) aiNewState(pSprite, pXSprite, cultistTFire[WATER]);
								if(state == 2) aiNewState(pSprite, pXSprite, cultistTFire[DUCK]);
								if(state == 3) aiNewState(pSprite, pXSprite, cultistTFire[LAND]);
							}
						}
						break;
					case kDudeShotgunCultist:
					case kDudeBeastCultist:
						if ( pSprite.lotag != kDudeBeastCultist && dist < 11264 && dist > 5120 
								&& !TargetNearExplosion(pTarget)
								&& (pTarget.hitag & kAttrGravity) != 0	
								&& pGameInfo.nDifficulty >= 2 && IsPlayerSprite(pTarget)
								&& Chance(0x4000) ) 
						{
							int pHit = HitScan(pSprite, pSprite.z, dx, dy, 0, pHitInfo, 16777280, 0);
							switch(pHit)
							{
								case SS_SPRITE:
									SPRITE pHitSprite = sprite[pHitInfo.hitsprite];
									if(pHitSprite.lotag != pSprite.lotag && pHitSprite.lotag != kDudeShotgunCultist)
									{
										if(pXSprite.palette != 1 && pXSprite.palette != 2) {
											aiNewState(pSprite, pXSprite, cultistSThrow);
											return;
										}
									}
									break;
								case SS_WALL:
								case SS_MASKED:
									return;
								case -1:
									if(pXSprite.palette != 1 && pXSprite.palette != 2) {
										aiNewState(pSprite, pXSprite, cultistSThrow);
										return;
									}
									break;
								default:
									aiNewState(pSprite, pXSprite, cultistSThrow);
									break;
							}
						}
						else if ( dist < 12800 && klabs(losAngle) < kAngle5 ) 
						{
							int state = checkAttackState(pSprite, pXSprite);
							int pHit = HitScan(pSprite, pSprite.z, dx, dy, 0, pHitInfo, 16777280, 0);
							if(pHit == -1)
							{
								if(state == 1) aiNewState(pSprite, pXSprite, cultistSFire[WATER]);
								if(state == 2) aiNewState(pSprite, pXSprite, cultistSFire[DUCK]);
								if(state == 3) aiNewState(pSprite, pXSprite, cultistSFire[LAND]);
							} 
							else
							{
								if(pHit == 3) {
									int type = sprite[pHitInfo.hitsprite].lotag;
									if(type == pSprite.lotag || type == kDudeTommyCultist) {
										if(state == 1) aiNewState(pSprite, pXSprite, cultistDodge[WATER]);
										if(state == 2) aiNewState(pSprite, pXSprite, cultistDodge[DUCK]);
										if(state == 3) aiNewState(pSprite, pXSprite, cultistDodge[LAND]);
										return;
									} 
								}
								if(state == 1) aiNewState(pSprite, pXSprite, cultistSFire[WATER]);
								if(state == 2) aiNewState(pSprite, pXSprite, cultistSFire[DUCK]);
								if(state == 3) aiNewState(pSprite, pXSprite, cultistSFire[LAND]);
							}
						}
						break;
					case kDudeTeslaCultist:
						/*
						if ( dist < 7680 && dist > 3584 && !TargetNearExplosion(pTarget)
						&& (pTarget.flags & kAttrGravity) != 0	
						&& pGameInfo.nDifficulty > 2
						&& ( IsPlayerSprite(pTarget) && gPlayer[pTarget.type - kDudePlayer1].Run )
						&& Chance(0x4000) ) {
							int pHit = HitScan(pSprite, pSprite.z, dx, dy, 0, pHitInfo, 16777280, 0);
							switch(pHit)
							{
								case SS_SPRITE:
									SPRITE pHitSprite = sprite[pHitInfo.hitsprite];
									if(pHitSprite.type != pSprite.type && pHitSprite.type != kDudeShotgunCultist)
									{
										if(pXSprite.palette != 1 && pXSprite.palette != 2) {
											aiNewState(pSprite, pXSprite, cultistTESThrow);
											return;
										}
									}
									break;
								case SS_WALL:
								case SS_MASKED:
									return;
								default:
									if(pXSprite.palette != 1 && pXSprite.palette != 2) {
										aiNewState(pSprite, pXSprite, cultistTESThrow);
										return;
									}
									break;
							}
						}
						else 
						*/
						if ( dist < 12800 && klabs(losAngle) < kAngle5 ) 
						{
							int state = checkAttackState(pSprite, pXSprite);
							int pHit = HitScan(pSprite, pSprite.z, dx, dy, 0, pHitInfo, 16777280, 0);
							if(pHit == -1)
							{
								if(state == 1) aiNewState(pSprite, pXSprite, cultistTESFire[WATER]);
								if(state == 2) aiNewState(pSprite, pXSprite, cultistTESFire[DUCK]);
								if(state == 3) aiNewState(pSprite, pXSprite, cultistTESFire[LAND]);
							} 
							else
							{
								if(pHit == 3) {
									int type = sprite[pHitInfo.hitsprite].lotag;
									if(type == pSprite.lotag || type == kDudeTommyCultist) {
										if(state == 1) aiNewState(pSprite, pXSprite, cultistDodge[WATER]);
										if(state == 2) aiNewState(pSprite, pXSprite, cultistDodge[DUCK]);
										if(state == 3) aiNewState(pSprite, pXSprite, cultistDodge[LAND]);
										return;
									} 
								}
								
								if(state == 1) aiNewState(pSprite, pXSprite, cultistTESFire[WATER]);
								if(state == 2) aiNewState(pSprite, pXSprite, cultistTESFire[DUCK]);
								if(state == 3) aiNewState(pSprite, pXSprite, cultistTESFire[LAND]);
							}
						}
						break;
						
					case kDudeDynamiteCultist:
						if(klabs(losAngle) < kAngle15 
							&& (pTarget.hitag & kAttrGravity) != 0 
							&& IsPlayerSprite(pTarget)) 
						{
							switch(HitScan(pSprite, pSprite.z, dx, dy, 0, pHitInfo, 16777280, 0))
							{
								case SS_SPRITE:
									SPRITE pHitSprite = sprite[pHitInfo.hitsprite];
									if(pHitSprite.lotag != pSprite.lotag && pHitSprite.lotag != kDudeShotgunCultist)
									{
										if(pXSprite.palette != 1 && pXSprite.palette != 2) {
											if(dist < 11264 && dist > 5120) aiNewState(pSprite, pXSprite, cultistGThrow1);
											else if(dist < 5120) aiNewState(pSprite, pXSprite, cultistGThrow2);
											return;
										}
									}
									break;
								case SS_MASKED:
									return;
								default:
									if(pXSprite.palette != 1 && pXSprite.palette != 2) {
										if(dist < 11264 && dist > 5120) aiNewState(pSprite, pXSprite, cultistGThrow1);
										else if(dist < 5120) aiNewState(pSprite, pXSprite, cultistGThrow2);
										return;
									}
									break;
							}
						}
					}
					return;
				}
			}
		}
		if( pXSprite.palette == 1 || pXSprite.palette == 2 )
			aiNewState(pSprite, pXSprite, cultistGoto[WATER]);
		else
			aiNewState(pSprite, pXSprite, cultistGoto[LAND]);
		pXSprite.target = -1;
	}
	
	public static int checkAttackState(SPRITE pSprite, XSPRITE pXSprite)
	{
		//DUCK - seq 14
		if ( checkDudeSeq(pSprite, 14) || pXSprite.palette != 0 )
        {
        	if ( !checkDudeSeq(pSprite, 14) || pXSprite.palette != 0 )
        	{
        		if ( pXSprite.palette == 1 || pXSprite.palette == 2 ) 
        			return 1; //water
        	}	
        	else
        		return 2; //duck
        }
        else
        	return 3; //land
		return 0;
	}
	
	public static boolean checkDudeSeq(SPRITE pSprite, int nSeqID)
	{
		if ( pSprite.statnum == kStatDude && IsDudeSprite(pSprite))
		{
			SeqInst pInst = GetInstance(SS_SPRITE, pSprite.extra);
			return pInst.getSeqIndex() == (dudeInfo[pSprite.lotag - kDudeBase].seqStartID + nSeqID) && seqFrame(SS_SPRITE, pSprite.extra) >= 0;
		}
		return false;
	}
	
	public static boolean TargetNearExplosion(SPRITE pSprite)
	{
		for ( int nSprite = headspritesect[pSprite.sectnum]; nSprite >= 0; nSprite = nextspritesect[nSprite] )
		{
			// check for TNT sticks or explosions in the same sector as the target
			if ( sprite[nSprite].lotag == kThingTNTStick || sprite[nSprite].statnum == kStatExplosion )
				return true; // indicate danger
		}
		return false;
	}
}
