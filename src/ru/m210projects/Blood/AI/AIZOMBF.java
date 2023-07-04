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
import static ru.m210projects.Blood.Actor.IsDudeSprite;
import static ru.m210projects.Blood.Actor.actFireMissile;
import static ru.m210projects.Blood.Actor.actFireVector;
import static ru.m210projects.Blood.Actor.kVectorCleaver;
import static ru.m210projects.Blood.DB.kDudeBase;
import static ru.m210projects.Blood.DB.kDudeButcher;
import static ru.m210projects.Blood.DB.kDudePlayer1;
import static ru.m210projects.Blood.DB.kItemBase;
import static ru.m210projects.Blood.DB.kItemDeathMask;
import static ru.m210projects.Blood.DB.kItemLtdInvisibility;
import static ru.m210projects.Blood.DB.kMaxXSprites;
import static ru.m210projects.Blood.DB.kMissileButcherKnife;
import static ru.m210projects.Blood.DB.kMissileGreenPuke;
import static ru.m210projects.Blood.DB.xsprite;
import static ru.m210projects.Blood.Gameutils.HitScan;
import static ru.m210projects.Blood.Gameutils.IsPlayerSprite;
import static ru.m210projects.Blood.Gameutils.M2X;

import static ru.m210projects.Blood.Globals.gPlayer;
import static ru.m210projects.Blood.Globals.kAngle15;
import static ru.m210projects.Blood.Globals.kAngle180;
import static ru.m210projects.Blood.Globals.kAngleMask;
import static ru.m210projects.Blood.Globals.kMaxSprites;
import static ru.m210projects.Blood.Globals.kSeqDudeAttack;
import static ru.m210projects.Blood.Globals.kSeqDudeIdle;
import static ru.m210projects.Blood.Globals.kSeqDudeRecoil;
import static ru.m210projects.Blood.Globals.kSeqDudeTesla;
import static ru.m210projects.Blood.Globals.kSeqDudeWalk;
import static ru.m210projects.Blood.Main.*;
import static ru.m210projects.Blood.PLAYER.powerupCheck;
import static ru.m210projects.Blood.SOUND.sfxStart3DSound;
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

public class AIZOMBF {
	
	public static AISTATE zombieFIdle;
	public static AISTATE zombieFChase;
	public static AISTATE zombieFGoto;
	public static AISTATE zombieFDodge;
	public static AISTATE zombieFSearch;
	public static AISTATE zombieFRecoil;
	public static AISTATE zombieFRTesla;
	public static AISTATE zombieFThrow;
	public static AISTATE zombieFPuke;
	public static AISTATE zombieFHack;
	
	public static void Init()
	{
		zombieFIdle = new AISTATE(Type.idle,kSeqDudeIdle, null, 0, false, false, true, null) {
			public void think(SPRITE sprite, XSPRITE xsprite) {
				aiThinkTarget(sprite, xsprite);
			}
		};
		
		zombieFGoto = new AISTATE(Type.tgoto,kSeqDudeWalk, null, 600, false, true, true, zombieFIdle) {
			public void move(SPRITE sprite, XSPRITE xsprite) {
				aiMoveForward(sprite, xsprite);
			}
			public void think(SPRITE sprite, XSPRITE xsprite) {
				thinkGoto(sprite, xsprite);
			}
		};
		
		zombieFChase = new AISTATE(Type.other,kSeqDudeWalk, null, 0, false, true, true, null) {
			public void move(SPRITE sprite, XSPRITE xsprite) {
				aiMoveForward(sprite, xsprite);
			}
			public void think(SPRITE sprite, XSPRITE xsprite) {
				thinkChase(sprite, xsprite);
			}
		};
		
		zombieFDodge = new AISTATE(Type.other,kSeqDudeWalk, null, 0, false, true, true, zombieFChase) {
			public void move(SPRITE sprite, XSPRITE xsprite) {
				aiMoveDodge(sprite, xsprite);
			}
			public void think(SPRITE sprite, XSPRITE xsprite) {
				thinkChase(sprite, xsprite);
			}
		};
		
		zombieFHack = new AISTATE(Type.other,kSeqDudeAttack, new CALLPROC() {
			public void run(int nXSprite) {
				HackCallback(nXSprite);
			}
		}, 120, false, false, false, zombieFChase);
		zombieFPuke = new AISTATE(Type.other,9, new CALLPROC() {
			public void run(int nXSprite) {
				PukeCallback(nXSprite);
			}
		}, 120, false, false, false, zombieFChase);
		
		zombieFThrow = new AISTATE(Type.other,kSeqDudeAttack, new CALLPROC() {
			public void run(int nXSprite) {
				ThrowCallback(nXSprite);
			}
		}, 120, false, false, false, zombieFChase);
		
		zombieFSearch = new AISTATE(Type.search,kSeqDudeWalk, null, 1800, false, true, true, zombieFIdle) {
			public void move(SPRITE sprite, XSPRITE xsprite) {
				aiMoveForward(sprite, xsprite);
			}
			public void think(SPRITE sprite, XSPRITE xsprite) {
				thinkSearch(sprite, xsprite);
			}
		};
		
		zombieFRecoil = new AISTATE(Type.other,kSeqDudeRecoil, null, 0, false, false, false, zombieFChase);
		zombieFRTesla = new AISTATE(Type.other,kSeqDudeTesla, null, 0, false, false, false, zombieFChase);
	}
	
	private static void thinkChase( SPRITE pSprite, XSPRITE pXSprite )
	{
		if ( pXSprite.target == -1)
		{
			aiNewState(pSprite, pXSprite, zombieFGoto);
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
			aiNewState(pSprite, pXSprite, zombieFSearch);
			return;
		}

		if ( IsPlayerSprite( pTarget ) )
		{
			PLAYER pPlayer = gPlayer[ pTarget.lotag - kDudePlayer1 ];
			if ( powerupCheck( pPlayer, kItemLtdInvisibility - kItemBase ) > 0
			|| powerupCheck( pPlayer, kItemDeathMask - kItemBase ) > 0 )
			{
				aiNewState(pSprite, pXSprite, zombieFSearch);
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
				pSprite.x, pSprite.y, pSprite.z - eyeAboveZ, pSprite.sectnum))
			{
				// is the target visible?
				if ( dist < pDudeInfo.seeDist && klabs(losAngle) <= pDudeInfo.periphery )
				{
					aiSetTarget(pXSprite, pXSprite.target);

					// check to see if we can attack
					if ( dist < 5120 && dist > 3584 && klabs(losAngle) < kAngle15 ) {
						if(checkTarget(pSprite, dx, dy)) {
							aiNewState(pSprite, pXSprite, zombieFThrow);
						} else aiNewState(pSprite, pXSprite, zombieFDodge);
					}
					else
					if ( dist < 5120 && dist > 1536 && klabs(losAngle) < kAngle15 ) {
						if(checkTarget(pSprite, dx, dy)) {
							aiNewState(pSprite, pXSprite, zombieFPuke);
						} else aiNewState(pSprite, pXSprite, zombieFDodge);
					}
					else
					if ( dist < 1024 && klabs(losAngle) < kAngle15 ) {
						if(checkTarget(pSprite, dx, dy)) {
							aiNewState(pSprite, pXSprite, zombieFHack);
						} else aiNewState(pSprite, pXSprite, zombieFDodge);
					}
					return;
				}
			}
		}
		aiNewState(pSprite, pXSprite, zombieFSearch);
		pXSprite.target = -1;
	}
	
	private static boolean checkTarget(SPRITE pSprite, int dx, int dy)
	{
		int pInfo = HitScan(pSprite, pSprite.z, dx, dy, 0, pHitInfo, 16777280, 0);
	    return pInfo <= -1 || pInfo != 3 || pSprite.lotag != sprite[pHitInfo.hitsprite].lotag;
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
		dist = (int)engine.qdist(dx, dy);

		aiChooseDirection(pSprite, pXSprite, nAngle);

		// if reached target, change to search mode
		if ( dist < M2X(1.0) && klabs(pSprite.ang - nAngle) < pDudeInfo.periphery )
			aiNewState(pSprite, pXSprite, zombieFSearch);

		aiThinkTarget(pSprite, pXSprite);
	}
	
	private static void thinkSearch( SPRITE pSprite, XSPRITE pXSprite )
	{
		aiChooseDirection(pSprite, pXSprite, pXSprite.goalAng);
		aiThinkTarget(pSprite, pXSprite);
	}

	private static void PukeCallback( int nXIndex )
	{
		XSPRITE pXSprite = xsprite[nXIndex];
		int nSprite = pXSprite.reference;
		SPRITE pSprite = sprite[nSprite];
		
		if(!IsDudeSprite(pSprite))
			return;

		int nAngle = engine.getangle(pXSprite.targetX - pSprite.x, pXSprite.targetY - pSprite.y);
		int dx = Cos(nAngle) >> 16;
		int dy = Sin(nAngle) >> 16;
		int dz = 0;
		
		int nZOffset1 = dudeInfo[pSprite.lotag - kDudeBase].eyeHeight * pSprite.yrepeat;
		int nZOffset2 = 0;
		if(pXSprite.target != -1)
		{
			SPRITE pTarget = sprite[pXSprite.target];
			if(IsDudeSprite(pTarget))
				nZOffset2 = dudeInfo[pTarget.lotag - kDudeBase].eyeHeight * pTarget.yrepeat;
		}
		sfxStart3DSound(pSprite, 1203, 1, 0);
		actFireMissile( pSprite, 0, nZOffset2 - nZOffset1, dx, dy, dz, kMissileGreenPuke );
	}
	
	private static void ThrowCallback( int nXIndex )
	{
		XSPRITE pXSprite = xsprite[nXIndex];
		int nSprite = pXSprite.reference;
		SPRITE pSprite = sprite[nSprite];

		int dx = Cos(pSprite.ang) >> 16;
		int dy = Sin(pSprite.ang) >> 16;
		int dz = 0;

		int nZOffset = 0;
		if(IsDudeSprite(pSprite))
			nZOffset = dudeInfo[pSprite.lotag - kDudeBase].eyeHeight;

		actFireMissile( pSprite, 0, -nZOffset, dx, dy, dz, kMissileButcherKnife );
	}
	
	private static void HackCallback( int nXIndex )
	{
		if(nXIndex > 0 && nXIndex < kMaxXSprites) 
		{
			XSPRITE pXSprite = xsprite[nXIndex];
			int nSprite = pXSprite.reference;

			if(nSprite >= 0 && nSprite < kMaxSprites)
			{
				SPRITE pSprite = sprite[nSprite];
				if(pSprite.lotag == kDudeButcher) 
				{
					int dx = Cos(pSprite.ang) >> 16;
					int dy = Sin(pSprite.ang) >> 16;
					int dz = dudeInfo[4].eyeHeight * pSprite.yrepeat;
					if(pXSprite.target != -1)
					{
						SPRITE pTarget = sprite[pXSprite.target];
						if(IsDudeSprite(pTarget))
							dz -= dudeInfo[pTarget.lotag - kDudeBase].eyeHeight * pTarget.yrepeat;
					}
					actFireVector(pSprite, 0, 0, dx, dy, dz, kVectorCleaver);
				}
			}
		}
	}
}
