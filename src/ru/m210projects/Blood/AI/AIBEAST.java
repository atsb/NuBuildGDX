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
import static ru.m210projects.Blood.AI.Ai.aiNewState;
import static ru.m210projects.Blood.AI.Ai.aiPlaySound;
import static ru.m210projects.Blood.AI.Ai.aiSetTarget;
import static ru.m210projects.Blood.AI.Ai.aiThinkTarget;
import static ru.m210projects.Blood.AI.Ai.gDudeSlope;
import static ru.m210projects.Blood.Actor.IsDudeSprite;
import static ru.m210projects.Blood.Actor.actDamageSprite;
import static ru.m210projects.Blood.Actor.actFireVector;
import static ru.m210projects.Blood.Actor.actHealDude;
import static ru.m210projects.Blood.Actor.gSectorExp;
import static ru.m210projects.Blood.Actor.gSpriteExp;
import static ru.m210projects.Blood.Actor.gWallExp;
import static ru.m210projects.Blood.Actor.kAttrFree;
import static ru.m210projects.Blood.Actor.kAttrGravity;
import static ru.m210projects.Blood.Actor.kDamageFall;
import static ru.m210projects.Blood.Actor.sprXVel;
import static ru.m210projects.Blood.Actor.sprYVel;
import static ru.m210projects.Blood.Actor.sprZVel;
import static ru.m210projects.Blood.DB.kDudeBase;
import static ru.m210projects.Blood.DB.kDudePlayer1;
import static ru.m210projects.Blood.DB.kDudeTheBeast;
import static ru.m210projects.Blood.DB.kItemBase;
import static ru.m210projects.Blood.DB.kItemLtdInvisibility;
import static ru.m210projects.Blood.DB.xsector;
import static ru.m210projects.Blood.DB.xsprite;
import static ru.m210projects.Blood.Gameutils.BiRandom2;
import static ru.m210projects.Blood.Gameutils.Chance;
import static ru.m210projects.Blood.Gameutils.CheckProximity;
import static ru.m210projects.Blood.Gameutils.ClipRange;
import static ru.m210projects.Blood.Gameutils.GetSpriteExtents;
import static ru.m210projects.Blood.Gameutils.HitScan;
import static ru.m210projects.Blood.Gameutils.IsPlayerSprite;
import static ru.m210projects.Blood.Gameutils.M2X;
import static ru.m210projects.Blood.Gameutils.NearSectors;
import static ru.m210projects.Blood.Gameutils.Random;
import static ru.m210projects.Blood.Gameutils.extents_zBot;
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
import static ru.m210projects.Blood.Globals.kStatDude;
import static ru.m210projects.Blood.Globals.kStatThing;
import static ru.m210projects.Blood.Globals.pGameInfo;
import static ru.m210projects.Blood.Main.engine;
import static ru.m210projects.Blood.PLAYER.powerupCheck;
import static ru.m210projects.Blood.SOUND.sfxStart3DSound;
import static ru.m210projects.Blood.Trig.Cos;
import static ru.m210projects.Blood.Trig.Sin;
import static ru.m210projects.Blood.Types.DEMO.IsOriginalDemo;
import static ru.m210projects.Blood.Types.DudeInfo.dudeInfo;
import static ru.m210projects.Blood.Types.DudeInfo.gPlayerTemplate;
import static ru.m210projects.Blood.Types.Seq.SeqHandling.seqFrame;
import static ru.m210projects.Build.Engine.headspritestat;
import static ru.m210projects.Build.Engine.nextspritestat;
import static ru.m210projects.Build.Engine.pHitInfo;
import static ru.m210projects.Build.Engine.sector;
import static ru.m210projects.Build.Engine.sprite;
import static ru.m210projects.Build.Gameutils.isValidSprite;
import static ru.m210projects.Build.Pragmas.divscale;
import static ru.m210projects.Build.Pragmas.dmulscale;
import static ru.m210projects.Build.Pragmas.klabs;
import static ru.m210projects.Build.Pragmas.mulscale;

import ru.m210projects.Blood.PLAYER;
import ru.m210projects.Blood.AI.AISTATEFUNC.Type;
import ru.m210projects.Blood.Types.CALLPROC;
import ru.m210projects.Blood.Types.DudeInfo;
import ru.m210projects.Blood.Types.XSECTOR;
import ru.m210projects.Blood.Types.XSPRITE;
import ru.m210projects.Build.Types.SPRITE;

public class AIBEAST {

	public static AISTATE[] beastIdle = new AISTATE[2];
	public static AISTATE[] beastSearch = new AISTATE[2];
	public static AISTATE[] beastGoto = new AISTATE[2];
	public static AISTATE[] beastDodge = new AISTATE[2];
	public static AISTATE[] beastChase = new AISTATE[2];
	public static AISTATE[] beastRecoil = new AISTATE[2];
	public static AISTATE[] beastHack = new AISTATE[2];
	public static AISTATE 	beastRTesla;
	public static AISTATE 	beastStomps;
	public static AISTATE 	beastTransforming;
	public static AISTATE 	beastTransform;
	public static AISTATE   beastUp;
	
	public static void Init()
	{
		beastIdle[LAND] = new AISTATE(Type.idle, kSeqDudeIdle, null, 0, false, false, true, null) {
			public void think(SPRITE sprite, XSPRITE xsprite) {
				aiThinkTarget(sprite, xsprite);
			}
		};
		beastIdle[WATER] = new AISTATE(Type.idle, 9, null, 0, false, false, true, null) {
			public void think(SPRITE sprite, XSPRITE xsprite) {
				aiThinkTarget(sprite, xsprite);
			}
		};
		beastChase[LAND] = new AISTATE(Type.other, 8, null, 0, false, true, true, null) {
			public void move(SPRITE sprite, XSPRITE xsprite) {
				myMoveForward(sprite, xsprite);
			}
			public void think(SPRITE sprite, XSPRITE xsprite) {
				thinkChase(sprite, xsprite);
			}
		};
		beastChase[WATER] = new AISTATE(Type.other, 9, null, 0, false, true, true, null) {
			public void move(SPRITE sprite, XSPRITE xsprite) {
				myMoveForwardWater(sprite, xsprite);
			}
			public void think(SPRITE sprite, XSPRITE xsprite) {
				thinkChaseWater(sprite, xsprite);
			}
		};
		beastDodge[LAND] = new AISTATE(Type.other, 8, null, 60, false, true, false, beastChase[LAND]) {
			public void move(SPRITE sprite, XSPRITE xsprite) {
				aiMoveDodge(sprite, xsprite);
			}
		};
		beastDodge[WATER] = new AISTATE(Type.other, 9, null, 90, false, true, false, beastChase[WATER]) {
			public void move(SPRITE sprite, XSPRITE xsprite) {
				aiMoveDodge(sprite, xsprite);
			}
		};
		beastGoto[LAND] = new AISTATE(Type.tgoto, 8, null, 600, false, true, true, beastIdle[LAND]) {
			public void move(SPRITE sprite, XSPRITE xsprite) {
				myMoveForward(sprite, xsprite);
			}
			public void think(SPRITE sprite, XSPRITE xsprite) {
				thinkGoto(sprite, xsprite);
			}
		};
		beastGoto[WATER] = new AISTATE(Type.tgoto, 9, null, 600, false, true, true, beastIdle[WATER]) {
			public void move(SPRITE sprite, XSPRITE xsprite) {
				myMoveForward(sprite, xsprite);
			}
			public void think(SPRITE sprite, XSPRITE xsprite) {
				thinkGotoWater(sprite, xsprite);
			}
		};
		beastHack[LAND]	= new AISTATE(Type.other,6, new CALLPROC() {
			public void run(int nXSprite) {
				HackCallback(nXSprite);
			}
		}, 120, false, false, false, beastChase[LAND] );
		beastHack[WATER] = new AISTATE(Type.other,9, null, 120, false, false, true, beastChase[WATER] ) {
			public void think(SPRITE sprite, XSPRITE xsprite) {
				thinkChaseWater(sprite, xsprite);
			}
		};
		beastStomps = new AISTATE(Type.other,7, new CALLPROC() {
			public void run(int nXSprite) {
				StompsCallback(nXSprite);
			}
		}, 120, false, false, false, beastChase[LAND] );
		beastSearch[LAND] = new AISTATE(Type.search, 8, null, 120, false, true, true, beastIdle[LAND]) {
			public void move(SPRITE sprite, XSPRITE xsprite) {
				myMoveForward(sprite, xsprite);
			}
			public void think(SPRITE sprite, XSPRITE xsprite) {
				thinkSearch(sprite, xsprite);
			}
		};
		beastSearch[WATER] = new AISTATE(Type.search, 9, null, 120, false, true, true, beastIdle[WATER]) {
			public void move(SPRITE sprite, XSPRITE xsprite) {
				myMoveForward(sprite, xsprite);
			}
			public void think(SPRITE sprite, XSPRITE xsprite) {
				thinkSearch(sprite, xsprite);
			}
		};
		beastRecoil[LAND] = new AISTATE(Type.other, 5, null, 0,	false,	false, false, beastDodge[LAND] );
		beastRecoil[WATER] = new AISTATE(Type.other, 5, null, 0,	false,	false, false, beastDodge[WATER] );
		
		beastRTesla = new AISTATE(Type.other, 4, null, 0,	false,	false, false, beastDodge[LAND] );
		
		beastTransform = new AISTATE(Type.other, -1, null, 0,	true,	false, false, beastIdle[LAND] )
		{
			public void enter(SPRITE sprite, XSPRITE xsprite) {
				thinkTransform(sprite, xsprite);
			}
		};
		beastTransforming = new AISTATE(Type.other, 2576, null, 0,	false,	false, false, beastTransform );
		beastUp = new AISTATE(Type.other, 9, null, 0, false, true, true, beastChase[WATER]) {
			public void move(SPRITE sprite, XSPRITE xsprite) {
				myMoveTarget(sprite, xsprite);
			}
			public void think(SPRITE sprite, XSPRITE xsprite) {
				thinkChaseWater(sprite, xsprite);
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
			
			long vel = kFrameTicks * pDudeInfo.frontSpeed;
			vel = (vel >> 1) + dmulscale(sprXVel[pSprite.xvel], cos, sprYVel[pSprite.xvel], sin, 30);
			long svel = dmulscale(sprXVel[pSprite.xvel], sin, -sprYVel[pSprite.xvel], cos, 30);
			int z1 = 0;
			if(pXSprite.target != -1) {
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

		int dx = pXSprite.targetX - pSprite.x;
		int dy = pXSprite.targetY - pSprite.y;

		if(engine.qdist(dx, dy) > 1024 || Random(64) >= 32)
		{
			int sin = Sin(pSprite.ang);
			int cos = Cos(pSprite.ang);

			sprXVel[pSprite.xvel] += mulscale(cos, pDudeInfo.frontSpeed, 30);
			sprYVel[pSprite.xvel] += mulscale(sin, pDudeInfo.frontSpeed, 30);
		}
	}
	
	private static void myMoveForwardWater( SPRITE pSprite, XSPRITE pXSprite )
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
			else vel += fvel >> 2;
			
			sprXVel[pSprite.xvel] = dmulscale(vel, cos, svel, sin, 30);
			sprYVel[pSprite.xvel] = dmulscale(vel, sin, -svel, cos, 30);
		}
	}
	
	private static void HackCallback(int nXSprite)
	{
		XSPRITE pXSprite = xsprite[nXSprite];
		
		int nSprite = xsprite[nXSprite].reference;
		SPRITE pSprite = sprite[nSprite];
		
		if(pXSprite.target == -1) return;
		
		int dx = Cos(pSprite.ang) >> 16;
		int dy = Sin(pSprite.ang) >> 16;
		int dz = pSprite.z - sprite[pXSprite.target].z;
		
		dx += BiRandom2( 4000 - 700 * pGameInfo.nDifficulty );
		dy += BiRandom2( 4000 - 700 * pGameInfo.nDifficulty );

		actFireVector(pSprite, 0, 0, dx, dy, dz, 13);
		actFireVector(pSprite, 0, 0, dx, dy, dz, 13);
		actFireVector(pSprite, 0, 0, dx, dy, dz, 13);
		sfxStart3DSound(pSprite, Random(2) + 9012, -1, 0);// Attack
	}
	
	private static void StompsCallback(int nXSprite)
	{
		int nSprite = xsprite[nXSprite].reference;
		SPRITE pSprite = sprite[nSprite];
		
		int radius = 400 << 4;
		int minDamage = 2 * pGameInfo.nDifficulty + 5;
		int maxDamage = 30 * pGameInfo.nDifficulty + 25;
		
		gSectorExp[0] = -1;
		gWallExp[0] = -1;
		NearSectors(pSprite.sectnum, pSprite.x, pSprite.y, 400, gSectorExp, gSpriteExp, gWallExp);
		for (int nTarget = headspritestat[kStatDude]; nTarget >= 0; nTarget = nextspritestat[nTarget]) {
			if ( nTarget != nSprite) {
				SPRITE pTarget = sprite[nTarget];
				int nXTarget = pTarget.extra;
				if(nXTarget > 0 && nXTarget < 2048 
					&& pTarget.lotag != kDudeTheBeast 
					&& (pTarget.hitag & kAttrFree) == 0
					&& (gSpriteExp[pTarget.sectnum >> 3] & (1 << (pTarget.sectnum & 7))) != 0
					&& CheckProximity(pTarget, pSprite.x, pSprite.y, pSprite.z, pSprite.sectnum, radius) )
				{
					GetSpriteExtents(pSprite);
					
					if( (extents_zBot - sector[pSprite.sectnum].floorz) == 0 )
					{
						int dist = engine.ksqrt((pSprite.x - pTarget.x) * (pSprite.x - pTarget.x) + (pSprite.y - pTarget.y) * (pSprite.y - pTarget.y));
						if( dist <= radius ) 
						{
							int damage = minDamage + maxDamage;
							if(dist != 0) 
								damage = minDamage + maxDamage * (radius - dist) / radius;

							if( IsPlayerSprite(pTarget) ) {
								PLAYER pPlayer = gPlayer[pTarget.lotag - kDudePlayer1];
								pPlayer.quakeTime += damage << 2;
							}
							actDamageSprite(nSprite, pTarget, kDamageFall, damage << 4);
						}
					}
				}			
			}
		}
		
		for (int nTarget = headspritestat[kStatThing]; nTarget >= 0; nTarget = nextspritestat[nTarget]) {
			SPRITE pTarget = sprite[nTarget];
			if((pTarget.hitag & kAttrFree) == 0
				&& (gSpriteExp[pTarget.sectnum >> 3] & (1 << (pTarget.sectnum & 7))) != 0
				&& xsprite[pTarget.extra].Locked == 0
				&& CheckProximity(pTarget, pSprite.x, pSprite.y, pSprite.z, pSprite.sectnum, radius) )
			{
				int dist = engine.ksqrt((pSprite.x - pTarget.x) * (pSprite.x - pTarget.x) + (pSprite.y - pTarget.y) * (pSprite.y - pTarget.y));
				if( dist <= radius ) 
				{
					int damage = minDamage + maxDamage;
					if(dist != 0) 
						damage = minDamage + maxDamage * (radius - dist) / radius;
					
					actDamageSprite(nSprite, pTarget, kDamageFall, damage << 4);
				}
			}
		}
		
		sfxStart3DSound(pSprite, Random(2) + 9015, -1, 0);
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
				aiNewState(pSprite, pXSprite, beastSearch[WATER]);
			else
				aiNewState(pSprite, pXSprite, beastSearch[LAND]);
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
			aiNewState(pSprite, pXSprite, beastSearch[WATER]);
		}

		aiThinkTarget(pSprite, pXSprite);
	}
	
	private static void thinkTransform(SPRITE pSprite, XSPRITE pXSPrite)
	{
		actHealDude(pXSPrite, dudeInfo[51].startHealth, dudeInfo[51].startHealth);
		pSprite.lotag = kDudeTheBeast;
	}
	
	private static void thinkChaseWater( SPRITE pSprite, XSPRITE pXSprite )
	{
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
			aiNewState(pSprite, pXSprite, beastSearch[WATER]);
			return;
		}
		if ( IsPlayerSprite( pTarget ) )
		{
			PLAYER pPlayer = gPlayer[ pTarget.lotag - kDudePlayer1 ];
			if ( powerupCheck( pPlayer, kItemLtdInvisibility - kItemBase ) > 0 )
			{
				aiNewState(pSprite, pXSprite, beastSearch[WATER]);
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
						aiNewState(pSprite, pXSprite, beastHack[WATER]);
						int nXSprite = pSprite.extra;
						if(!IsOriginalDemo() && seqFrame(SS_SPRITE, nXSprite) == 1)
							HackCallback(nXSprite);
						
						return;
					}
					aiPlaySound(pSprite, Random(2) + 9009, 1, -1);
					aiNewState(pSprite, pXSprite, beastUp);
					return;
				}
			} else
				aiNewState(pSprite, pXSprite, beastUp);
		} else {
			aiNewState(pSprite, pXSprite, beastGoto[WATER]);
			pXSprite.target = -1;
		}
	}
	
	/*
	private static void thinkAttack( SPRITE pSprite, XSPRITE pXSprite, XSECTOR pXSector, int dist, int nAngle, int hittype )
	{
		if ( dist < 921 )
        {
			if ( klabs(nAngle) < 28 )
		    {
				if(hittype != 3 || sprite[pHitInfo.hitsprite].type != pSprite.type) 
				{
					if(pXSector != null && pXSector.Underwater)
						aiNewState(pSprite, pXSprite, beastHack[WATER]);
					else aiNewState(pSprite, pXSprite, beastHack[LAND]);
				} else { 
					if(pXSector != null && pXSector.Underwater)
						aiNewState(pSprite, pXSprite, beastDodge[WATER]);
					else aiNewState(pSprite, pXSprite, beastDodge[LAND]);
				}
		    }
		}
	}
	*/
	
	private static void thinkChase( SPRITE pSprite, XSPRITE pXSprite )
	{
		int nXSector = sector[pSprite.sectnum].extra;
		
		XSECTOR pXSector = null;
		if ( nXSector > 0 )
			pXSector = xsector[nXSector];
			
		if ( pXSprite.target == -1)
		{
			if(pXSector != null && pXSector.Underwater)
				aiNewState(pSprite, pXSprite, beastSearch[WATER]);
			else aiNewState(pSprite, pXSprite, beastSearch[LAND]);
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
			if(pXSector != null && pXSector.Underwater)
				aiNewState(pSprite, pXSprite, beastSearch[WATER]);
			else aiNewState(pSprite, pXSprite, beastSearch[LAND]);
			return;
		}
		
		if ( IsPlayerSprite( pTarget ) )
		{
			PLAYER pPlayer = gPlayer[ pTarget.lotag - kDudePlayer1 ];
			if ( powerupCheck( pPlayer, kItemLtdInvisibility - kItemBase ) > 0 )
			{
				if(pXSector != null && pXSector.Underwater)
					aiNewState(pSprite, pXSprite, beastSearch[WATER]);
				else aiNewState(pSprite, pXSprite, beastSearch[LAND]);
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
					int hitType = HitScan(pSprite, pSprite.z, dx, dy, 0, pHitInfo, 16777280, 0);
					
					if ( dist < 5120 
						&& dist > 2560 
						&& klabs(losAngle) < kAngle15
				        && (pTarget.hitag & kAttrGravity) != 0
				        && IsPlayerSprite(pTarget)
				        && (Chance(0x4000))
						&& pXTarget.health > gPlayerTemplate[0].startHealth / 2)
					{
						if(hitType == SS_SPRITE && sprite[pHitInfo.hitsprite].lotag == pSprite.lotag)
						{
							if(pXSector != null && pXSector.Underwater)
								aiNewState(pSprite, pXSprite, beastDodge[WATER]);
							else aiNewState(pSprite, pXSprite, beastDodge[LAND]);
							return;
						}
						
						if(pXSector == null || !pXSector.Underwater)
							aiNewState(pSprite, pXSprite, beastStomps);
					}
					
					if ( dist < 921 && klabs(losAngle) < kAngle5)
			        {
						if(hitType == SS_SPRITE && sprite[pHitInfo.hitsprite].lotag == pSprite.lotag)
						{
							if(pXSector != null && pXSector.Underwater)
								aiNewState(pSprite, pXSprite, beastDodge[WATER]);
							else aiNewState(pSprite, pXSprite, beastDodge[LAND]);
							return;
						}

						if(pXSector != null && pXSector.Underwater)
							aiNewState(pSprite, pXSprite, beastHack[WATER]);
						else aiNewState(pSprite, pXSprite, beastHack[LAND]);
					}

					/*
					if ( dist >= 5120 || dist <= 2560 ) {
						thinkAttack( pSprite, pXSprite, pXSector, dist, losAngle, hitType );
						return;
					}

					if ( klabs(losAngle) >= kAngle15
				        || (pTarget.flags & kAttrGravity) == 0
				        || !IsPlayerSprite(pTarget)
				        || (!Chance(0x4000))
						|| pXTarget.health <= gPlayerTemplate[0].startHealth / 2)
					{
						thinkAttack( pSprite, pXSprite, pXSector, dist, losAngle, hitType );
						return;
					}
					
					if(hitType == -1)
					{
						if(pXSector == null || !pXSector.Underwater)
							aiNewState(pSprite, pXSprite, beastStomps);
						thinkAttack( pSprite, pXSprite, pXSector, dist, losAngle, hitType );
						return;
					}
					  
					if(hitType == SS_SPRITE)
					{
						if(sprite[pHitInfo.hitsprite].type == pSprite.type) {
							if(pXSector != null && pXSector.Underwater)
								aiNewState(pSprite, pXSprite, beastDodge[WATER]);
							else aiNewState(pSprite, pXSprite, beastDodge[LAND]);
						} else if(pXSector == null || !pXSector.Underwater)
							aiNewState(pSprite, pXSprite, beastStomps);
						thinkAttack( pSprite, pXSprite, pXSector, dist, losAngle, hitType );
						return;
					} 
					if(pXSector == null || !pXSector.Underwater)
						aiNewState(pSprite, pXSprite, beastStomps);
					thinkAttack( pSprite, pXSprite, pXSector, dist, losAngle, hitType );
					*/
					
					/*
					if ( hitType == SS_SPRITE && sprite[pHitInfo.hitsprite].type == pSprite.type) {
						if(pXSector != null && pXSector.Underwater)
							aiNewState(pSprite, pXSprite, beastDodge[WATER]);
						else aiNewState(pSprite, pXSprite, beastDodge[LAND]);
						return;
					}
						
					if ( dist > 2560 && dist < 5120 && klabs(losAngle) < kAngle15
						&& (pTarget.flags & kAttrGravity) != 0
						&& IsPlayerSprite(pTarget)
						&& pXTarget.health > gPlayerTemplate[0].startHealth / 2
						&& Chance(0x4000) )
					{
						if(pXSector == null || !pXSector.Underwater)
							aiNewState(pSprite, pXSprite, beastStomps);
					}
					
					if(dist < 921 && klabs(losAngle) < kAngle5) {
						if(pXSector != null && pXSector.Underwater)
							aiNewState(pSprite, pXSprite, beastHack[WATER]);
						else aiNewState(pSprite, pXSprite, beastHack[LAND]);
					}
					*/
				}
				return;
			}
		}
		
		if(pXSector != null && pXSector.Underwater)
			aiNewState(pSprite, pXSprite, beastGoto[WATER]);
		else aiNewState(pSprite, pXSprite, beastGoto[LAND]);
		pXSprite.target = -1;
	}
}
