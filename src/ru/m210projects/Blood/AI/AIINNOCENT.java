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
import static ru.m210projects.Blood.AI.Ai.aiNewState;
import static ru.m210projects.Blood.AI.Ai.aiPlaySound;
import static ru.m210projects.Blood.AI.Ai.aiSetTarget;
import static ru.m210projects.Blood.AI.Ai.aiThinkTarget;
import static ru.m210projects.Blood.Actor.IsDudeSprite;
import static ru.m210projects.Blood.DB.kDudeBase;
import static ru.m210projects.Blood.DB.xsprite;
import static ru.m210projects.Blood.Gameutils.BiRandom;
import static ru.m210projects.Blood.Gameutils.IsPlayerSprite;
import static ru.m210projects.Blood.Gameutils.M2X;
import static ru.m210projects.Blood.Globals.kAngle15;
import static ru.m210projects.Blood.Globals.kAngle180;
import static ru.m210projects.Blood.Globals.kAngleMask;
import static ru.m210projects.Blood.Globals.kSeqDudeIdle;
import static ru.m210projects.Blood.Main.engine;
import static ru.m210projects.Blood.Types.DudeInfo.dudeInfo;
import static ru.m210projects.Build.Engine.sprite;
import static ru.m210projects.Build.Gameutils.isValidSprite;
import static ru.m210projects.Build.Pragmas.klabs;

import ru.m210projects.Blood.AI.AISTATEFUNC.Type;
import ru.m210projects.Blood.Types.DudeInfo;
import ru.m210projects.Blood.Types.XSPRITE;
import ru.m210projects.Build.Types.SPRITE;

public class AIINNOCENT {

	public static AISTATE innocentIdle;
	public static AISTATE innocentSearch;
	public static AISTATE innocentGoto;
	public static AISTATE innocentChase;
	public static AISTATE innocentTesla;
	public static AISTATE innocentRecoil;
	
	public static void Init()
	{
		innocentIdle = new AISTATE(Type.idle,kSeqDudeIdle, null, 0, false, false, true, null) {
			public void think(SPRITE sprite, XSPRITE xsprite) {
				aiThinkTarget(sprite, xsprite);
			}
		};
		innocentSearch = new AISTATE(Type.search, 6,	null, 1800, false, true,	true, innocentIdle) {
			public void move(SPRITE sprite, XSPRITE xsprite) {
				aiMoveForward(sprite, xsprite);
			}
			public void think(SPRITE sprite, XSPRITE xsprite) {
				thinkSearch(sprite, xsprite);
			}
		};
		innocentGoto = new AISTATE(Type.tgoto, 6, null, 600,	false,	true,	true, innocentIdle) {
			public void move(SPRITE sprite, XSPRITE xsprite) {
				aiMoveForward(sprite, xsprite);
			}
			public void think(SPRITE sprite, XSPRITE xsprite) {
				thinkGoto(sprite, xsprite);
			}
		};
		innocentChase = new AISTATE(Type.other, 6, null, 0,	false,	true,	true, null) {
			public void move(SPRITE sprite, XSPRITE xsprite) {
				aiMoveForward(sprite, xsprite);
			}
			public void think(SPRITE sprite, XSPRITE xsprite) {
				thinkChase(sprite, xsprite);
			}
		};
		innocentRecoil = new AISTATE(Type.other, 5, null, 0,	false,	false, false, innocentChase );
		innocentTesla = new AISTATE(Type.other, 4, null, 0,	false,	false, false, innocentChase );
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

		// if reached target, change to search mode
		if ( dist < M2X(1.0) && klabs(pSprite.ang - nAngle) < pDudeInfo.periphery )
			aiNewState(pSprite, pXSprite, innocentSearch);

		aiThinkTarget(pSprite, pXSprite);
	}
	
	private static void thinkChase( SPRITE pSprite, XSPRITE pXSprite )
	{
		if ( pXSprite.target == -1)
		{
			aiNewState(pSprite, pXSprite, innocentGoto);
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
			aiNewState(pSprite, pXSprite, innocentSearch);
			return;
		}

		if ( IsPlayerSprite( pTarget ) )
		{
			aiNewState(pSprite, pXSprite, innocentSearch);
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
				if ( dist < pDudeInfo.seeDist && klabs(losAngle) <= pDudeInfo.periphery )
				{
					aiSetTarget(pXSprite, pXSprite.target);

					if ( dist < 1638 && klabs(losAngle) < kAngle15 )
						aiNewState(pSprite, pXSprite, innocentIdle);

					return;
				}
			}
		}
		aiPlaySound(pSprite, BiRandom(6) + 7000, 1, -1);
		aiNewState(pSprite, pXSprite, innocentGoto);
		pXSprite.target = -1;
	}
}
