// This file is part of PowerslaveGDX.
// Copyright (C) 2019  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// PowerslaveGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// PowerslaveGDX is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with PowerslaveGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.Powerslave.Enemies;

import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Powerslave.Globals.*;
import static ru.m210projects.Powerslave.Main.*;
import static ru.m210projects.Powerslave.Bullet.*;
import static ru.m210projects.Powerslave.Random.*;
import static ru.m210projects.Powerslave.Enemies.Enemy.*;
import static ru.m210projects.Powerslave.RunList.*;
import static ru.m210projects.Powerslave.Seq.*;
import static ru.m210projects.Powerslave.Sprites.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ru.m210projects.Build.FileHandle.Resource;
import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Powerslave.Enemies.Enemy.EnemyStruct;
import ru.m210projects.Powerslave.Type.SafeLoader;

public class Roach {

	public static final int MAXROACH = 100;
	
	private static int RoachCount;
//	private static int RoachSprite;
	private static EnemyStruct[] RoachList = new EnemyStruct[MAXROACH];
	private static final short[][] ActionSeq_X_11 = new short[][] { { 24, 0 }, { 0, 0 }, { 0, 0 }, { 16, 0 }, { 8, 0 },
			{ 32, 1 }, { 42, 1 } };

	public static void InitRoach() {
		RoachCount = MAXROACH;
//		RoachSprite = 1;
	}
	
	public static ByteBuffer saveRoach()
	{
		ByteBuffer bb = ByteBuffer.allocate((MAXROACH - RoachCount) * EnemyStruct.size + 2);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		
		bb.putShort((short)RoachCount);
		for(int i = RoachCount; i < MAXROACH; i++)
			RoachList[i].save(bb);
		
		return bb;
	}
	
	public static void loadRoach(SafeLoader loader, Resource bb)
	{
		if(bb != null)
		{
			loader.RoachCount = bb.readShort();
			for(int i = loader.RoachCount; i < MAXROACH; i++) {
				if(loader.RoachList[i] == null)
					loader.RoachList[i] = new EnemyStruct();
				loader.RoachList[i].load(bb);
			}
		}
		else
		{
			RoachCount = loader.RoachCount;
			for(int i = loader.RoachCount; i < MAXROACH; i++) {
				if (RoachList[i] == null)
					RoachList[i] = new EnemyStruct();
				RoachList[i].copy(loader.RoachList[i]);
			}
		}
	}

	public static void BuildRoach(int a1, int spr, int x, int y, int z, int sectnum, int ang) {
		int count = --RoachCount;

		if (count < 0)
			return;

		if (spr == -1) {
			spr = engine.insertsprite((short) sectnum, (short) 105);
		} else {
			x = sprite[spr].x;
			y = sprite[spr].y;
			z = sector[sprite[spr].sectnum].floorz;
			ang = sprite[spr].ang;
			engine.changespritestat((short) spr, (short) 105);
		}

		if (spr < 0 || spr >= MAXSPRITES) {
			game.ThrowError("spr>=0 && spr<MAXSPRITES");
			return;
		}

		sprite[spr].x = x;
		sprite[spr].y = y;
		sprite[spr].z = z;
		sprite[spr].cstat = 257;
		sprite[spr].shade = -12;
		sprite[spr].xoffset = 0;
		sprite[spr].yoffset = 0;
		sprite[spr].ang = (short) ang;
		sprite[spr].xrepeat = 40;
		sprite[spr].yrepeat = 40;
		sprite[spr].picnum = 1;
		sprite[spr].pal = sector[sprite[spr].sectnum].ceilingpal;
		sprite[spr].clipdist = 60;
		sprite[spr].xvel = 0;
		sprite[spr].yvel = 0;
		sprite[spr].zvel = 0;
		sprite[spr].hitag = 0;
		sprite[spr].lotag = (short) (HeadRun() + 1);
		sprite[spr].extra = -1;
		
		if (RoachList[count] == null)
			RoachList[count] = new EnemyStruct();

		if (a1 != 0)
			RoachList[count].nState = 0;
		else
			RoachList[count].nState = 1;

		RoachList[count].nSeq = 0;
		RoachList[count].nHealth = 600;
		RoachList[count].nTarget = -1;
		RoachList[count].nSprite = (short) spr;
		sprite[spr].owner = (short) AddRunRec(sprite[spr].lotag - 1, 0x1C0000 | count);
		RoachList[count].field_C = 0;
		RoachList[count].field_A = (short) AddRunRec(NewRun, 0x1C0000 | count);

		nCreaturesLeft++;
		nCreaturesMax++;
	}

	public static void GoRoach(int a1) {
		sprite[a1].xvel = (short) ((sintable[(sprite[a1].ang + 512) & 0x7FF] >> 1)
				- (sintable[(sprite[a1].ang + 512) & 0x7FF] >> 3));
		sprite[a1].yvel = (short) ((sintable[sprite[a1].ang & 0x7FF] >> 1) - (sintable[sprite[a1].ang & 0x7FF] >> 3));
	}

	public static void FuncRoach(int a1, int nDamage, int RunPtr) {
		short nRoach = (short) (RunData[RunPtr].RunEvent & 0xFFFF);
		if (nRoach < 0 || nRoach >= MAXROACH) {
			game.ThrowError("roach>=0 && roach<MAXROACH");
			return;
		}

		EnemyStruct pRoach = RoachList[nRoach];
		int nSprite = pRoach.nSprite;
		SPRITE pSprite = sprite[nSprite];

		int nState = pRoach.nState;
		int nTarget = pRoach.nTarget;
		int v42 = 0;

		short nObject = (short) (a1 & 0xFFFF);
		switch (a1 & 0x7F0000) {
		case nEventProcess:
			Gravity(nSprite);

			int nSeq = ActionSeq_X_11[nState][0] + SeqOffsets[50];
			pSprite.picnum = (short) GetSeqPicnum2(nSeq, pRoach.nSeq);
			MoveSequence(nSprite, nSeq, pRoach.nSeq);
			if (++pRoach.nSeq >= SeqSize[nSeq]) {
				pRoach.nSeq = 0;
				v42 = 1;
			}
			
			if(nState > 5)
				return;

			int plDist;
			switch (nState) {
			case 0:
				if (pRoach.nSeq == 1) {
					if (--pRoach.field_C > 0)
						pRoach.nSeq = 0;
					else
						pRoach.field_C = (short) RandomSize(6);
				}
				plDist = 50;
			case 1:
				plDist = 100;
				if ((nRoach & 0xF) == (totalmoves & 0xF) && nTarget < 0) {
					int v18 = FindPlayer(nSprite, plDist);
					if (v18 >= 0) {
						pRoach.nState = 2;
						pRoach.nSeq = 0;
						pRoach.nTarget = (short) v18;
						GoRoach(nSprite);
					}
				}
				return;
			case 2:
				if ((totalmoves & 0xF) == (nRoach & 0xF)) {
					PlotCourseToSprite(nSprite, nTarget);
					GoRoach(nSprite);
				}
				int hitMove = MoveCreatureWithCaution(nSprite);

				switch (hitMove & 0xC000) {
				default:
					if ( pRoach.field_C != 0 ) {
						pRoach.field_C--;
						break;
					}
				case 0xC000:
					if (((hitMove & 0xC000) == 0xC000 && (hitMove & 0x3FFF) == nTarget) || (hitMove & 0xC000) < 0x8000) {
						pRoach.nFunc = (short) (RandomSize(2) + 1);
						pRoach.nState = 3;
						pSprite.xvel = pSprite.yvel = 0;
						if(nTarget != -1)
							pSprite.ang = engine.GetMyAngle(sprite[nTarget].x - pSprite.x, sprite[nTarget].y - pSprite.y);
						pRoach.nSeq = 0;
						break;
					}
				case 0x8000:
					pSprite.ang = (short) ((pSprite.ang + 256) & 0x7FF);
					GoRoach(nSprite);
					break;
				
				}
				break;
			case 3:
				if (v42 != 0) {
					if (pRoach.nFunc-- <= 0) {
						pRoach.nState = 2;
						GoRoach(nSprite);
						pRoach.nSeq = 0;
						pRoach.field_C = (short) RandomSize(7);
					}
				} else if ((FrameFlag[pRoach.nSeq + SeqBase[nSeq]] & 0x80) != 0)
					BuildBullet(nSprite, 13, 0, 0, -1, pSprite.ang, nTarget + 10000, 1);
				return;
			case 4:
				if (v42 == 0)
					return;
				pRoach.nState = 2;
				pRoach.nSeq = 0;
				return;
			case 5:
				if (v42 != 0) {
					pSprite.cstat = 0;
					pRoach.nState = 6;
					pRoach.nSeq = 0;
				}
				return;
			}
			if (nTarget != -1 && (sprite[nTarget].cstat & 0x101) == 0) {
				pRoach.nState = 1;
				pRoach.nSeq = 0;
				pRoach.field_C = 100;
				pRoach.nTarget = -1;
				pSprite.xvel = 0;
				pSprite.yvel = 0;
			}
			return;
		case nEventView:
			PlotSequence(nObject, ActionSeq_X_11[nState][0] + SeqOffsets[50], pRoach.nSeq, ActionSeq_X_11[nState][1]);
			return;
		case nEventRadialDamage:
			nDamage = CheckRadialDamage(nSprite);
		case nEventDamage:
			if (nDamage != 0) {
				if (pRoach.nHealth > 0) {
					pRoach.nHealth -= nDamage;
					if (pRoach.nHealth > 0) {
						if (nObject >= 0) {
							if (sprite[nObject].statnum < 199) 
								pRoach.nTarget = nObject;

							if (nState != 0 && nState != 1) {
								if (RandomSize(4) == 0) {
									pRoach.nState = 4;
									pRoach.nSeq = 0;
								}
							} else {
								pRoach.nState = 2;
								GoRoach(nRoach);
								pRoach.nSeq = 0;
							}
						}
					}
					else {
						pSprite.xvel = 0;
						pSprite.yvel = 0;
						pSprite.zvel = 0;
						pSprite.cstat &= ~0x101;
						pRoach.nHealth = 0;
						if (pRoach.nState < 5) {
							DropMagic(nSprite);
							pRoach.nState = 5;
							pRoach.nSeq = 0;
						}
						nCreaturesLeft--;
					}
				} 
			}
			return;
		}
	}
}
