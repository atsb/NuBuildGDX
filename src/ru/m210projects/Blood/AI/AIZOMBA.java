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
import static ru.m210projects.Blood.AI.Ai.aiMoveForward;
import static ru.m210projects.Blood.AI.Ai.aiMoveTurn;
import static ru.m210projects.Blood.AI.Ai.aiNewState;
import static ru.m210projects.Blood.AI.Ai.aiSetTarget;
import static ru.m210projects.Blood.AI.Ai.aiThinkTarget;
import static ru.m210projects.Blood.AI.Ai.aiThinkTarget2;
import static ru.m210projects.Blood.Actor.IsDudeSprite;
import static ru.m210projects.Blood.Actor.actFireVector;
import static ru.m210projects.Blood.Actor.kAttrMove;
import static ru.m210projects.Blood.Actor.kVectorAxe;
import static ru.m210projects.Blood.DB.kDudeAxeZombie;
import static ru.m210projects.Blood.DB.kDudeBase;
import static ru.m210projects.Blood.DB.kDudePlayer1;
import static ru.m210projects.Blood.DB.kItemBase;
import static ru.m210projects.Blood.DB.kItemDeathMask;
import static ru.m210projects.Blood.DB.kItemLtdInvisibility;
import static ru.m210projects.Blood.DB.xsprite;
import static ru.m210projects.Blood.Gameutils.IsPlayerSprite;
import static ru.m210projects.Blood.Gameutils.M2X;
import static ru.m210projects.Blood.Globals.gPlayer;
import static ru.m210projects.Blood.Globals.kAngle15;
import static ru.m210projects.Blood.Globals.kAngle180;
import static ru.m210projects.Blood.Globals.kAngleMask;
import static ru.m210projects.Blood.Globals.kSeqDudeAttack;
import static ru.m210projects.Blood.Globals.kSeqDudeDeath1;
import static ru.m210projects.Blood.Globals.kSeqDudeIdle;
import static ru.m210projects.Blood.Globals.kSeqDudeRecoil;
import static ru.m210projects.Blood.Globals.kSeqDudeTesla;
import static ru.m210projects.Blood.Globals.kSeqDudeWalk;
import static ru.m210projects.Blood.Main.engine;
import static ru.m210projects.Blood.PLAYER.powerupCheck;
import static ru.m210projects.Blood.SOUND.sfxStart3DSound;
import static ru.m210projects.Blood.Trig.Cos;
import static ru.m210projects.Blood.Trig.Sin;
import static ru.m210projects.Blood.Types.DudeInfo.dudeInfo;
import static ru.m210projects.Build.Engine.sprite;
import static ru.m210projects.Build.Gameutils.isValidSprite;
import static ru.m210projects.Build.Pragmas.klabs;

import ru.m210projects.Blood.PLAYER;
import ru.m210projects.Blood.AI.AISTATEFUNC.Type;
import ru.m210projects.Blood.Types.CALLPROC;
import ru.m210projects.Blood.Types.DudeInfo;
import ru.m210projects.Blood.Types.XSPRITE;
import ru.m210projects.Build.Types.SPRITE;

public class AIZOMBA {
	
	public static final int kAxeZombieMeleeDist = M2X(2.0);
	
	public static AISTATE zombieAIdle;
	public static AISTATE zombieAChase;
	public static AISTATE zombieAPonder;
	public static AISTATE zombieAGoto;
	public static AISTATE zombieAHack;
	public static AISTATE zombieASearch;
	public static AISTATE zombieARecoil;
	public static AISTATE zombieARTesla;
	public static AISTATE zombieAFall;
	public static AISTATE zombieAUp;
	public static AISTATE zombieEIdle;
	public static AISTATE zombieEUp;
	public static AISTATE zombieEUp2;
	public static AISTATE zombieSLIdle;
	public static AISTATE zombieSLUP;
	
	public static void Init() {

		zombieAIdle = new AISTATE(Type.idle,kSeqDudeIdle, null, 0, true, false, true, null) {
			public void enter(SPRITE sprite, XSPRITE xsprite) {
				xsprite.target = -1;
			}
			public void think(SPRITE sprite, XSPRITE xsprite) {
				aiThinkTarget(sprite, xsprite);
			}
		};

		zombieEIdle = new AISTATE(Type.idle,12, null, 0, false, false, true, null) {
			public void think(SPRITE sprite, XSPRITE xsprite) {
				aiThinkTarget(sprite, xsprite);
			}
		};

		zombieAPonder = new AISTATE(Type.idle,kSeqDudeIdle, null, 0, false, true, true, null) {
			public void move(SPRITE sprite, XSPRITE xsprite) {
				aiMoveTurn(sprite, xsprite);
			}
			public void think(SPRITE sprite, XSPRITE xsprite) {
				thinkPonder(sprite, xsprite);
			}
		};
		zombieARecoil = new AISTATE(Type.other,kSeqDudeRecoil, null, 0, false, false, false, zombieAPonder);
		zombieARTesla = new AISTATE(Type.other,kSeqDudeTesla, null, 0, false, false, false, zombieAPonder);
		zombieAUp = new AISTATE(Type.other,11, null, 0, false, false, false, zombieAPonder);
		zombieAFall = new AISTATE(Type.other,kSeqDudeDeath1, null, 360, false, false, false, zombieAUp);
		zombieAHack = new AISTATE(Type.other,kSeqDudeAttack, new CALLPROC() {
			public void run(int nXSprite) {
				HackCallback(nXSprite);
			}
		}, 80, false, false, false, zombieAPonder);
		zombieAChase = new AISTATE(Type.other,kSeqDudeWalk, null, 0, false, true, true, null) {
			public void move(SPRITE sprite, XSPRITE xsprite) {
				aiMoveForward(sprite, xsprite);
			}
			public void think(SPRITE sprite, XSPRITE xsprite) {
				thinkChase(sprite, xsprite);
			}
		};
		zombieAGoto = new AISTATE(Type.tgoto,kSeqDudeWalk, null, 1800, false, true, true, zombieAIdle) {
			public void move(SPRITE sprite, XSPRITE xsprite) {
				aiMoveForward(sprite, xsprite);
			}
			public void think(SPRITE sprite, XSPRITE xsprite) {
				thinkGoto(sprite, xsprite);
			}
		};
		zombieASearch = new AISTATE(Type.search,kSeqDudeWalk, null, 1800, false, true, true, zombieAIdle) {
			public void move(SPRITE sprite, XSPRITE xsprite) {
				aiMoveForward(sprite, xsprite);
			}
			public void think(SPRITE sprite, XSPRITE xsprite) {
				thinkSearch(sprite, xsprite);
			}
		};
		zombieEUp2 = new AISTATE(Type.other,kSeqDudeIdle, null, 1, true, false, false, zombieASearch) {
			public void enter(SPRITE sprite, XSPRITE xsprite) {
				entryEZombie(sprite, xsprite);
			}
		};
		
		zombieEUp = new AISTATE(Type.other,9, null, 180, true, false, false, zombieEUp2) {
			public void enter(SPRITE sprite, XSPRITE xsprite) {
				entryEZombieUp(sprite, xsprite);
			}
		};
		
		zombieSLIdle = new AISTATE(Type.idle,10, null, 0, false, false, true, null) {
			public void think(SPRITE sprite, XSPRITE xsprite) {
				aiThinkTarget(sprite, xsprite);
			}
		};
		zombieSLUP = new AISTATE(Type.other,11, null, 0, true, false, false, zombieAPonder) {
			public void enter(SPRITE sprite, XSPRITE xsprite) {
				entryEZombie(sprite, xsprite);
			}
		};

	}
	
	/****************************************************************************
	** entryEZombie()
	**
	**
	****************************************************************************/
	private static void entryEZombie( SPRITE pSprite, XSPRITE pXSprite )
	{
		pSprite.hitag |= kAttrMove;
		pSprite.lotag = kDudeAxeZombie;
	}
	
	private static void entryEZombieUp( SPRITE pSprite, XSPRITE pXSprite ) {
		sfxStart3DSound(pSprite, 1100, -1, 0);
		pSprite.ang = engine.getangle(pXSprite.targetX - pSprite.x, pXSprite.targetY - pSprite.y);
	}

	/****************************************************************************
	** thinkSearch()
	**
	**
	****************************************************************************/
	private static void thinkSearch( SPRITE pSprite, XSPRITE pXSprite )
	{
		aiChooseDirection(pSprite, pXSprite, pXSprite.goalAng);
		aiThinkTarget2(pSprite, pXSprite);
	}
	
	/****************************************************************************
	** thinkGoto()
	**
	**
	****************************************************************************/
	private static void thinkGoto( SPRITE pSprite, XSPRITE pXSprite )
	{
		int dx, dy;

		if(!IsDudeSprite(pSprite))
			return;

		DudeInfo pDudeInfo = dudeInfo[pSprite.lotag - kDudeBase];

		dx = pXSprite.targetX - pSprite.x;
		dy = pXSprite.targetY - pSprite.y;

		int nAngle = engine.getangle(dx, dy);
		long dist = engine.qdist(dx, dy);

		aiChooseDirection(pSprite, pXSprite, nAngle);

		// if reached target, change to search mode
		
		if ( dist < M2X(1.8) && klabs(pSprite.ang - nAngle) < pDudeInfo.periphery )
		{
//			System.out.println("Axe zombie " +  xsprite[pSprite.extra].reference + " switching to search mode");
			aiNewState(pSprite, pXSprite, zombieASearch);
		}

		aiThinkTarget(pSprite, pXSprite);
		
	}
	
	/****************************************************************************
	** thinkChase()
	**
	**
	****************************************************************************/
	private static void thinkChase( SPRITE pSprite, XSPRITE pXSprite )
	{
		if ( pXSprite.target == -1)
		{
			aiNewState(pSprite, pXSprite, zombieASearch);
			return;
		}

		int dx, dy;
		
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
			aiNewState(pSprite, pXSprite, zombieASearch);
			return;
		}

		if ( IsPlayerSprite( pTarget ) )
		{
			PLAYER pPlayer = gPlayer[ pTarget.lotag - kDudePlayer1 ];
			if ( powerupCheck( pPlayer, kItemLtdInvisibility - kItemBase ) > 0
			|| powerupCheck( pPlayer, kItemDeathMask - kItemBase ) > 0 )
			{
				aiNewState(pSprite, pXSprite, zombieAGoto);
				return;
			}
		}

		long dist = engine.qdist(dx, dy);

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
				if ( klabs(losAngle) <= pDudeInfo.periphery )
				{
					aiSetTarget(pXSprite, pXSprite.target);

					// check to see if we can attack
					if ( dist < kAxeZombieMeleeDist && klabs(losAngle) < kAngle15 )
					{
						aiNewState(pSprite, pXSprite, zombieAHack);
					}
					return;
				}
			}
		}
		//System.out.println("Axe zombie " + pXSprite.reference + " lost sight of target " + pXSprite.target);
		aiNewState(pSprite, pXSprite, zombieAGoto);
		pXSprite.target = -1;
	}
	
	private static void thinkPonder( SPRITE pSprite, XSPRITE pXSprite )
	{
		if ( pXSprite.target == -1)
		{
			aiNewState(pSprite, pXSprite, zombieASearch);
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
			aiNewState(pSprite, pXSprite, zombieASearch);
			return;
		}

		if ( IsPlayerSprite( pTarget ) )
		{
			PLAYER pPlayer = gPlayer[ pTarget.lotag - kDudePlayer1 ];
			if ( powerupCheck( pPlayer, kItemLtdInvisibility - kItemBase ) > 0
			|| powerupCheck( pPlayer, kItemDeathMask - kItemBase ) > 0 )
			{
				aiNewState(pSprite, pXSprite, zombieAGoto);
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
				if ( klabs(losAngle) <= pDudeInfo.periphery )
				{
					aiSetTarget(pXSprite, pXSprite.target);

					// check to see if we can attack
					if ( dist < kAxeZombieMeleeDist )
					{
						if ( klabs(losAngle) < kAngle15 )
						{
							aiNewState(pSprite, pXSprite, zombieAHack);
						}
						return;
					}
				}
			}
		}
		aiNewState(pSprite, pXSprite, zombieAChase);
	}
	
	private static void HackCallback( int nXIndex ) {
		XSPRITE pXSprite = xsprite[nXIndex];
		int nSprite = pXSprite.reference;
		SPRITE pSprite = sprite[nSprite];
		
		if(!IsDudeSprite(pSprite))
			return;

		int nAngle = engine.getangle(pXSprite.targetX - pSprite.x, pXSprite.targetY - pSprite.y);

		int nZOffset1 = dudeInfo[pSprite.lotag - kDudeBase].eyeHeight * pSprite.yrepeat << 2;
		int nZOffset2 = 0;
		if(pXSprite.target != -1)
		{
			SPRITE pTarget = sprite[pXSprite.target];
			if(IsDudeSprite(pTarget))
				nZOffset2 = dudeInfo[pTarget.lotag - kDudeBase].eyeHeight * pTarget.yrepeat << 2;
		}
		int dx = Cos(nAngle) >> 16;
		int dy = Sin(nAngle) >> 16;
		int dz = nZOffset1 - nZOffset2;
		
		sfxStart3DSound(pSprite, 1101, 1, 0); //kSfxAxeAir
		
		actFireVector(pSprite, 0, 0, dx, dy, dz, kVectorAxe);
	}
}
