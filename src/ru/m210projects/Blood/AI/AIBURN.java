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

import static ru.m210projects.Blood.AI.Ai.*;
import static ru.m210projects.Blood.Actor.IsDudeSprite;
import static ru.m210projects.Blood.DB.*;
import static ru.m210projects.Blood.Gameutils.*;
import static ru.m210projects.Blood.Globals.*;
import static ru.m210projects.Blood.Main.*;
import static ru.m210projects.Blood.PLAYER.*;
import static ru.m210projects.Blood.Types.DudeInfo.*;
import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Build.Gameutils.isValidSprite;
import static ru.m210projects.Build.Pragmas.*;

import ru.m210projects.Blood.PLAYER;
import ru.m210projects.Blood.AI.AISTATEFUNC.Type;
import ru.m210projects.Blood.Types.DudeInfo;
import ru.m210projects.Blood.Types.XSPRITE;
import ru.m210projects.Build.Types.SPRITE;

public class AIBURN {
	public static final int INNOCENT = 0;
	public static final int CULTIST = 1;
	public static final int ZOMBIE = 2;
	public static final int BUTCHER = 3;
	public static final int TINYCALEB = 4;
	public static final int BEAST = 5;
	public static final int GDXGENDUDE = 6;
	
	public static AISTATE[] burnChase = new AISTATE[7];
	public static AISTATE[] burnGoto = new AISTATE[7];
	public static AISTATE[] burnSearch = new AISTATE[7];
	public static AISTATE[] burnHack = new AISTATE[7];
	
	public static void Init()
	{
		for(int i = 0; i < burnChase.length; i++) {
			burnChase[i] = new AISTATE(Type.other,kSeqDudeDeath3, null, 0, false, true, true, null) {
				public void move(SPRITE sprite, XSPRITE xsprite) {
					aiMoveForward(sprite, xsprite);
				}
				public void think(SPRITE sprite, XSPRITE xsprite) {
					thinkChase(sprite, xsprite);
				}
			};
		}
		for(int i = 0; i < burnSearch.length; i++) {
			burnSearch[i] = new AISTATE(Type.search,kSeqDudeDeath3, null, 3600, false, true, true, null) {
				public void move(SPRITE sprite, XSPRITE xsprite) {
					aiMoveForward(sprite, xsprite);
				}
				public void think(SPRITE sprite, XSPRITE xsprite) {
					thinkSearch(sprite, xsprite);
				}
			};
			if(i != INNOCENT && i != ZOMBIE && i != BUTCHER)
				burnSearch[i].next = burnSearch[i];
		}
		for(int i = 0; i < burnGoto.length; i++) {
			burnGoto[i] = new AISTATE(Type.tgoto,kSeqDudeDeath3, null, 3600, false, true, true, burnSearch[i]) {
				public void move(SPRITE sprite, XSPRITE xsprite) {
					aiMoveForward(sprite, xsprite);
				}
				public void think(SPRITE sprite, XSPRITE xsprite) {
					thinkGoto(sprite, xsprite);
				}
			};
		}
		for(int i = 0; i < burnHack.length; i++) {
			burnHack[i] = new AISTATE(Type.other,kSeqDudeDeath3, null, 120, false, false, false, burnChase[i]);
			if(i == BUTCHER)
				burnHack[i].next = burnChase[INNOCENT];
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
			switch(pSprite.lotag)
			{
			case kDudeBurning:
				aiNewState(pSprite, pXSprite, burnSearch[INNOCENT]);
				break;
			case kDudeCultistBurning:
				aiNewState(pSprite, pXSprite, burnSearch[CULTIST]);
				break;
			case kDudeAxeZombieBurning:
				aiNewState(pSprite, pXSprite, burnSearch[ZOMBIE]);
				break;
			case kDudeBloatedButcherBurning:
				aiNewState(pSprite, pXSprite, burnSearch[BUTCHER]);
				break;
			case kDudeTinyCalebburning:
				aiNewState(pSprite, pXSprite, burnSearch[TINYCALEB]);
				break;
			case kDudeTheBeastburning:
				aiNewState(pSprite, pXSprite, burnSearch[BEAST]);
				break;
			case kGDXGenDudeBurning:
				aiNewState(pSprite, pXSprite, burnSearch[GDXGENDUDE]);
				break;
			}
		}

		aiThinkTarget(pSprite, pXSprite);
	}
	
	private static void thinkChase( SPRITE pSprite, XSPRITE pXSprite )
	{
		
		//System.out.println("HP: "+pXSprite.health);
		if ( pXSprite.target == -1)
		{
			switch(pSprite.lotag)
			{
			case kDudeBurning:
				aiNewState(pSprite, pXSprite, burnGoto[INNOCENT]);
				break;
			case kDudeCultistBurning:
				aiNewState(pSprite, pXSprite, burnGoto[CULTIST]);
				break;
			case kDudeAxeZombieBurning:
				aiNewState(pSprite, pXSprite, burnGoto[ZOMBIE]);
				break;
			case kDudeBloatedButcherBurning:
				aiNewState(pSprite, pXSprite, burnGoto[BUTCHER]);
				break;
			case kDudeTinyCalebburning:
				aiNewState(pSprite, pXSprite, burnGoto[TINYCALEB]);
				break;
			case kDudeTheBeastburning:
				aiNewState(pSprite, pXSprite, burnGoto[BEAST]);
				break;
			case kGDXGenDudeBurning:
				aiNewState(pSprite, pXSprite, burnGoto[GDXGENDUDE]);
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
			switch(pSprite.lotag)
			{
			case kDudeBurning:
				aiNewState(pSprite, pXSprite, burnSearch[INNOCENT]);
				break;
			case kDudeCultistBurning:
				aiNewState(pSprite, pXSprite, burnSearch[CULTIST]);
				break;
			case kDudeAxeZombieBurning:
				aiNewState(pSprite, pXSprite, burnSearch[ZOMBIE]);
				break;
			case kDudeBloatedButcherBurning:
				aiNewState(pSprite, pXSprite, burnSearch[BUTCHER]);
				break;
			case kDudeTinyCalebburning:
				aiNewState(pSprite, pXSprite, burnSearch[TINYCALEB]);
				break;
			case kDudeTheBeastburning:
				aiNewState(pSprite, pXSprite, burnSearch[BEAST]);
				break;
			case kGDXGenDudeBurning:
				aiNewState(pSprite, pXSprite, burnSearch[GDXGENDUDE]);
				break;
			}
			return;
		}

		if ( IsPlayerSprite( pTarget ) )
		{
			PLAYER pPlayer = gPlayer[ pTarget.lotag - kDudePlayer1 ];
			if ( powerupCheck( pPlayer, kItemLtdInvisibility - kItemBase ) > 0 )
			{
				switch(pSprite.lotag)
				{
				case kDudeBurning:
					aiNewState(pSprite, pXSprite, burnSearch[INNOCENT]);
					break;
				case kDudeCultistBurning:
					aiNewState(pSprite, pXSprite, burnSearch[CULTIST]);
					break;
				case kDudeAxeZombieBurning:
					aiNewState(pSprite, pXSprite, burnSearch[ZOMBIE]);
					break;
				case kDudeBloatedButcherBurning:
					aiNewState(pSprite, pXSprite, burnSearch[BUTCHER]);
					break;
				case kDudeTinyCalebburning:
					aiNewState(pSprite, pXSprite, burnSearch[TINYCALEB]);
					break;
				case kDudeTheBeastburning:
					aiNewState(pSprite, pXSprite, burnSearch[BEAST]);
					break;
				case kGDXGenDudeBurning:
					aiNewState(pSprite, pXSprite, burnSearch[GDXGENDUDE]);
					break;
				}
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

					if ( dist < 819 && klabs(losAngle) < kAngle15 ) {
						switch(pSprite.lotag)
						{
						case kDudeBurning:
							aiNewState(pSprite, pXSprite, burnHack[INNOCENT]);
							break;
						case kDudeCultistBurning:
							aiNewState(pSprite, pXSprite, burnHack[CULTIST]);
							break;
						case kDudeAxeZombieBurning:
							aiNewState(pSprite, pXSprite, burnHack[ZOMBIE]);
							break;
						case kDudeBloatedButcherBurning:
							aiNewState(pSprite, pXSprite, burnHack[BUTCHER]);
							break;
						case kDudeTinyCalebburning:
							aiNewState(pSprite, pXSprite, burnHack[TINYCALEB]);
							break;
						case kDudeTheBeastburning:
							aiNewState(pSprite, pXSprite, burnHack[BEAST]);
							break;
						case kGDXGenDudeBurning:
							aiNewState(pSprite, pXSprite, burnHack[GDXGENDUDE]);
							break;
						}
					}

					return;
				}
			}
		}

		switch(pSprite.lotag)
		{
		case kDudeBurning:
			aiNewState(pSprite, pXSprite, burnGoto[INNOCENT]);
			break;
		case kDudeCultistBurning:
			aiNewState(pSprite, pXSprite, burnGoto[CULTIST]);
			break;
		case kDudeAxeZombieBurning:
			aiNewState(pSprite, pXSprite, burnGoto[ZOMBIE]);
			break;
		case kDudeBloatedButcherBurning:
			aiNewState(pSprite, pXSprite, burnGoto[BUTCHER]);
			break;
		case kDudeTinyCalebburning:
			aiNewState(pSprite, pXSprite, burnGoto[TINYCALEB]);
			break;
		case kDudeTheBeastburning:
			aiNewState(pSprite, pXSprite, burnGoto[BEAST]);
			break;
		case kGDXGenDudeBurning:
			aiNewState(pSprite, pXSprite, burnGoto[GDXGENDUDE]);
			break;
		}
		pXSprite.target = -1;
	}
}
