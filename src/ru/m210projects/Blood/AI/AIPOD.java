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
import static ru.m210projects.Blood.AI.Ai.aiMoveTurn;
import static ru.m210projects.Blood.AI.Ai.aiNewState;
import static ru.m210projects.Blood.AI.Ai.aiSetTarget;
import static ru.m210projects.Blood.AI.Ai.aiThinkTarget;
import static ru.m210projects.Blood.Actor.IsDudeSprite;
import static ru.m210projects.Blood.Actor.actDistanceDamage;
import static ru.m210projects.Blood.Actor.actFireThing;
import static ru.m210projects.Blood.Actor.actSpawnTentacleBlood;
import static ru.m210projects.Blood.DB.kDudeBase;
import static ru.m210projects.Blood.DB.kDudeFirePod;
import static ru.m210projects.Blood.DB.kDudeFireTentacle;
import static ru.m210projects.Blood.DB.kDudeGreenPod;
import static ru.m210projects.Blood.DB.kDudeGreenTentacle;
import static ru.m210projects.Blood.DB.kThingPodFire;
import static ru.m210projects.Blood.DB.kThingPodGreen;
import static ru.m210projects.Blood.DB.xsprite;
import static ru.m210projects.Blood.Gameutils.BiRandom;
import static ru.m210projects.Blood.Gameutils.Chance;
import static ru.m210projects.Blood.Gameutils.M2X;
import static ru.m210projects.Blood.Globals.SS_SPRITE;
import static ru.m210projects.Blood.Globals.kAngle15;
import static ru.m210projects.Blood.Globals.kAngle180;
import static ru.m210projects.Blood.Globals.kAngleMask;
import static ru.m210projects.Blood.Globals.kSeqDudeIdle;
import static ru.m210projects.Blood.Globals.pGameInfo;
import static ru.m210projects.Blood.Main.engine;
import static ru.m210projects.Blood.SOUND.sfxStart3DSound;
import static ru.m210projects.Blood.Types.DudeInfo.dudeInfo;
import static ru.m210projects.Blood.Types.Seq.SeqHandling.seqSpawn;
import static ru.m210projects.Build.Engine.sprite;
import static ru.m210projects.Build.Gameutils.isValidSprite;
import static ru.m210projects.Build.Pragmas.divscale;
import static ru.m210projects.Build.Pragmas.klabs;

import ru.m210projects.Blood.AI.AISTATEFUNC.Type;
import ru.m210projects.Blood.Types.CALLPROC;
import ru.m210projects.Blood.Types.DudeInfo;
import ru.m210projects.Blood.Types.XSPRITE;
import ru.m210projects.Build.Types.SPRITE;

public class AIPOD {
	
	public static final int kSlopeThrow	= -14500;
	
	public static AISTATE podIdle;
	public static AISTATE podSearch;
	public static AISTATE podChase;
	public static AISTATE podRecoil;
	public static AISTATE podGoto;
	public static AISTATE podHack;
	
	public static AISTATE tentacleIdle;
	public static AISTATE tentacleSearch;
	public static AISTATE tentacleChase;
	public static AISTATE tentacleRecoil;
	public static AISTATE tentacleDown;
	public static AISTATE tentacleHack;
	
	public static void Init() {
		podIdle = new AISTATE(Type.idle,kSeqDudeIdle, null, 0, false, false, true, null) {
			public void think(SPRITE sprite, XSPRITE xsprite) {
				aiThinkTarget(sprite, xsprite);
			}
		};
		podSearch = new AISTATE(Type.search,kSeqDudeIdle, null, 3600, false, true, true, podSearch) {
			public void move(SPRITE sprite, XSPRITE xsprite) {
				aiMoveTurn(sprite, xsprite);
			}
			public void think(SPRITE sprite, XSPRITE xsprite) {
				thinkSearch(sprite, xsprite);
			}
		};
		podSearch.next = podSearch;
		podGoto = new AISTATE(Type.tgoto,7, null, 3600, false, true, true, podSearch) {
			public void move(SPRITE sprite, XSPRITE xsprite) {
				aiMoveTurn(sprite, xsprite);
			}
			public void think(SPRITE sprite, XSPRITE xsprite) {
				thinkGoto(sprite, xsprite);
			}
		};
		podChase = new AISTATE(Type.other,6, null, 0, false, true, true, null) {
			public void move(SPRITE sprite, XSPRITE xsprite) {
				aiMoveTurn(sprite, xsprite);
			}
			public void think(SPRITE sprite, XSPRITE xsprite) {
				thinkChase(sprite, xsprite);
			}
		};
		podHack = new AISTATE(Type.other,8, new CALLPROC() {
			public void run(int nXSprite) {
				pHackCallback(nXSprite);
			}
		}, 600, false, false, false, podChase);
		podRecoil = new AISTATE(Type.other,5, null, 0, false, false, false, podChase);
		
		
		tentacleIdle = new AISTATE(Type.idle,kSeqDudeIdle, null, 0, false, false, true, null) {
			public void think(SPRITE sprite, XSPRITE xsprite) {
				aiThinkTarget(sprite, xsprite);
			}
		};
		tentacleSearch = new AISTATE(Type.search,kSeqDudeIdle, null, 3600, false, true, true, null) {
			public void move(SPRITE sprite, XSPRITE xsprite) {
				aiMoveTurn(sprite, xsprite);
			}
			public void think(SPRITE sprite, XSPRITE xsprite) {
				thinkSearch(sprite, xsprite);
			}
		};
		tentacleDown = new AISTATE(Type.other,8, null, 3600, false, true, true, tentacleSearch) {
			public void move(SPRITE sprite, XSPRITE xsprite) {
				aiMoveTurn(sprite, xsprite);
			}
			public void think(SPRITE sprite, XSPRITE xsprite) {
				thinkGoto(sprite, xsprite);
			}
		};
		tentacleChase = new AISTATE(Type.other,6, null, 0, false, true, true, null) {
			public void move(SPRITE sprite, XSPRITE xsprite) {
				aiMoveTurn(sprite, xsprite);
			}
			public void think(SPRITE sprite, XSPRITE xsprite) {
				thinkChase(sprite, xsprite);
			}
		};
		tentacleHack = new AISTATE(Type.other,6, new CALLPROC() {
			public void run(int nXSprite) {
				tHackCallback(nXSprite);
			}
		}, 120, false, false, false, tentacleChase);
		tentacleRecoil = new AISTATE(Type.other,5, null, 0, false, false, false, tentacleChase);
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

		dx = pXSprite.targetX - pSprite.x;
		dy = pXSprite.targetY - pSprite.y;

		int nAngle = engine.getangle(dx, dy);
		dist = (int) engine.qdist(dx, dy);

		aiChooseDirection(pSprite, pXSprite, nAngle);

		if ( dist < M2X(1.0) && klabs(pSprite.ang - nAngle) < pDudeInfo.periphery )
		{
			switch ( pSprite.lotag )
			{
				case kDudeGreenPod:
				case kDudeFirePod:
					aiNewState(pSprite, pXSprite, podSearch);
					break;
				case kDudeGreenTentacle:
				case kDudeFireTentacle:
					aiNewState(pSprite, pXSprite, tentacleSearch);
					break;
			}
		}
		aiThinkTarget(pSprite, pXSprite);
	}
	
	private static void thinkChase( SPRITE pSprite, XSPRITE pXSprite )
	{
		if ( pXSprite.target == -1)
		{
			switch ( pSprite.lotag )
			{
				case kDudeGreenPod:
				case kDudeFirePod:                              
					aiNewState(pSprite, pXSprite, podGoto);
					break;
				case kDudeGreenTentacle:
				case kDudeFireTentacle:
					aiNewState(pSprite, pXSprite, tentacleDown);
					break;
			}
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
			switch ( pSprite.lotag )
			{
				case kDudeGreenPod:
				case kDudeFirePod:
				 	aiNewState(pSprite, pXSprite, podSearch);
				 	break;
				case kDudeGreenTentacle:
				case kDudeFireTentacle:
					aiNewState(pSprite, pXSprite, tentacleSearch);
					break;
			}
			return;
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

					// check to see if we can attack
					if ( klabs(losAngle) < kAngle15 )
					{
						if ( pTarget.lotag != 221 && pTarget.lotag != 223 ) {
							switch ( pSprite.lotag )
							{
								case kDudeGreenPod:
								case kDudeFirePod:
									aiNewState(pSprite, pXSprite, podHack);
								 	break;
								case kDudeGreenTentacle:
								case kDudeFireTentacle:
									aiNewState(pSprite, pXSprite, tentacleHack);
									break;
							}
						}
					}
					return;
				}
			}
		}

		switch ( pSprite.lotag )
		{
			case kDudeGreenPod:
			case kDudeFirePod:
				aiNewState(pSprite, pXSprite, podGoto);
				break;
			case kDudeGreenTentacle:
			case kDudeFireTentacle:
				aiNewState(pSprite, pXSprite, tentacleDown);
				break;
		}
		pXSprite.target = -1;
	}
	
	private static void tHackCallback(int nXSprite)
	{
		int nSprite = xsprite[nXSprite].reference;
		SPRITE pSprite = sprite[nSprite];
		sfxStart3DSound(pSprite, 2502, -1, 0);       // tentacle sweeps
		int nDamageType, nDistance, nBurnTime, nDamageHit = 5 * pGameInfo.nDifficulty + 5;
		if ( pSprite.lotag == kDudeGreenTentacle )
		{
			nDamageType = 2;
			nDistance = 50;
			nBurnTime = 0;
		}
		else                               
		{
			nDamageType = 3;
			nDistance = 75;
			nBurnTime = 120 * pGameInfo.nDifficulty;
		}
		actDistanceDamage(nSprite, pSprite.x, pSprite.y, pSprite.z, pSprite.sectnum, nDistance, 1, nDamageType, nDamageHit, 2, nBurnTime);
	}
     
	private static void pHackCallback(int nXSprite) 
	{
		XSPRITE pXSprite = xsprite[nXSprite];
		SPRITE pSprite = sprite[pXSprite.reference];
		if(!IsDudeSprite(pSprite) || !isValidSprite(pXSprite.target))
			return;
		
		int nTarget = pXSprite.target;
		SPRITE pTarget = sprite[nTarget];
	    DudeInfo pDudeInfo = dudeInfo[pSprite.lotag - 200];
	  
		int dx = pTarget.x - pSprite.x;
		int dy = pTarget.y - pSprite.y;
		int dz = pTarget.z - pSprite.z;
		
		dx += BiRandom(1000); 
		dy += BiRandom(1000);
		dz += 8000;

	    int dist = (int) engine.qdist(dx,dy);
		if(dist > pDudeInfo.seeDist / 10) {
			 
			int nSlope = (dz / 128) + kSlopeThrow;
			SPRITE pThing = null;
			int velocity = divscale(dist / 540, 120, 23);
			
			switch(pSprite.lotag) {
				case kDudeGreenPod:
					if ( Chance(0x4000) )
						sfxStart3DSound(pSprite, 2474, -1, 0);
					else
						sfxStart3DSound(pSprite, 2475, -1, 0);
					
					//if(gFrameClock == 131804) {
						//System.err.println(pSprite.xvel);
					//}
					
					pThing = actFireThing(pSprite.xvel, 0, -8000, nSlope, kThingPodGreen, velocity);
					if(pThing != null)
						seqSpawn(68, SS_SPRITE, pThing.extra, null);
				break;
				case kDudeFirePod:
					sfxStart3DSound(pSprite, 2454, -1, 0);
					pThing = actFireThing(pSprite.xvel, 0, -8000, nSlope, kThingPodFire, velocity);
					if(pThing != null)
						seqSpawn(22, SS_SPRITE, pThing.extra, null);
				break;
			}
		}
		for(int i = 0; i < 4; i++)
			actSpawnTentacleBlood(pSprite);
	}
}
