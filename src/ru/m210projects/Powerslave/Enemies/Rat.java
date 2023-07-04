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
import static ru.m210projects.Build.Pragmas.*;
import static ru.m210projects.Powerslave.Globals.*;
import static ru.m210projects.Powerslave.Main.*;
import static ru.m210projects.Powerslave.Random.*;
import static ru.m210projects.Powerslave.RunList.*;
import static ru.m210projects.Powerslave.Seq.*;
import static ru.m210projects.Powerslave.Sprites.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static ru.m210projects.Powerslave.Enemies.Enemy.*;

import ru.m210projects.Build.FileHandle.Resource;
import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Powerslave.Enemies.Enemy.EnemyStruct;
import ru.m210projects.Powerslave.Type.SafeLoader;

public class Rat {
	
	public static final int MAX_RAT = 50;

	private static int nRatCount;
	private static int nMinChunk;
	private static int nMaxChunk;

	private static EnemyStruct[] RatList = new EnemyStruct[MAX_RAT];
	private static final short[][] ActionSeq_X_14 = new short[][] { { 0, 1 }, { 1, 0 }, { 1, 0 }, { 9, 1 }, { 0, 1 } };

	public static void InitRats() {
		nRatCount = 0;
		nMinChunk = 9999;
		nMaxChunk = -1;

		for (int i = 122; i <= 131; i++) {
			int pic = GetSeqPicnum(25, i, 0);
			if (pic < nMinChunk)
				nMinChunk = pic;
			if (pic > nMaxChunk)
				nMaxChunk = pic;
		}

		nPlayerPic = GetSeqPicnum(25, 120, 0);
	}
	
	public static ByteBuffer saveRat()
	{
		ByteBuffer bb = ByteBuffer.allocate(nRatCount * EnemyStruct.size + 14);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		
		bb.putShort((short)nRatCount);
		for(int i = 0; i < nRatCount; i++)
			RatList[i].save(bb);
		
		bb.putInt(nMinChunk);
		bb.putInt(nMaxChunk);
		bb.putInt(nPlayerPic);
		
		return bb;
	}
	
	public static void loadRat(SafeLoader loader, Resource bb)
	{
		if(bb != null) {
			loader.nRatCount = bb.readShort();
			for(int i = 0; i < loader.nRatCount; i++) {
				if(loader.RatList[i] == null)
					loader.RatList[i] = new EnemyStruct();
				loader.RatList[i].load(bb);
			}
			
			loader.nMinChunk = bb.readInt();
			loader.nMaxChunk = bb.readInt();
			loader.nPlayerPic = bb.readInt();
		}
		else
		{
			nRatCount = loader.nRatCount;
			for(int i = 0; i < loader.nRatCount; i++) {
				if (RatList[i] == null)
					RatList[i] = new EnemyStruct();
				RatList[i].copy(loader.RatList[i]);
			}
			
			nMinChunk = loader.nMinChunk;
			nMaxChunk = loader.nMaxChunk;
			nPlayerPic = loader.nPlayerPic;
		}
	}

	public static void SetRatVel(int a1) {
		sprite[a1].xvel = (short) (sintable[(sprite[a1].ang + 512) & 0x7FF] >> 2);
		sprite[a1].yvel = (short) (sintable[sprite[a1].ang & 0x7FF] >> 2);
	}

	public static int BuildRat(int spr, int x, int y, int z, int sectnum, int ang) {
		if (nRatCount >= MAX_RAT)
			return -1;

		int count = nRatCount++;
		if (spr == -1) {
			spr = engine.insertsprite((short) sectnum, (short) 108);
			sprite[spr].x = x;
			sprite[spr].y = x;
			sprite[spr].z = x;
		} else {
			ang = sprite[spr].ang;
			engine.changespritestat((short) spr, (short) 108);
		}

		sprite[spr].shade = -12;
		sprite[spr].cstat = 257;
		sprite[spr].clipdist = 30;
		sprite[spr].xrepeat = 50;
		sprite[spr].yrepeat = 50;
		sprite[spr].pal = sector[sprite[spr].sectnum].ceilingpal;
		sprite[spr].xoffset = 0;
		sprite[spr].yoffset = 0;
		sprite[spr].picnum = 1;
		sprite[spr].ang = (short) ang;
		sprite[spr].xvel = 0;
		sprite[spr].yvel = 0;
		sprite[spr].zvel = 0;

		sprite[spr].hitag = 0;
		sprite[spr].lotag = (short) (HeadRun() + 1);
		sprite[spr].extra = -1;
		
		if (RatList[count] == null)
			RatList[count] = new EnemyStruct();

		if (ang >= 0)
			RatList[count].nState = 2;
		else
			RatList[count].nState = 4;
		RatList[count].nSeq = 0;
		RatList[count].nSprite = (short) spr;
		RatList[count].nTarget = -1;
		RatList[count].field_A = (short) RandomSize(5);
		RatList[count].field_C = (short) RandomSize(3);

		sprite[spr].owner = (short) AddRunRec(sprite[spr].lotag - 1, 0x240000 | count);
		RatList[count].nFunc = (short) AddRunRec(NewRun, 0x240000 | count);

		return spr;
	}

	public static int FindFood(int a1) {
		short sectnum = sprite[a1].sectnum;
		int sx = sprite[a1].x;
		int sy = sprite[a1].y;
		int sz = (sector[sprite[a1].sectnum].ceilingz + sprite[a1].z) / 2;

		int v6, v7, v10, v9;
		if (nChunkTotal != 0 && ((v7 = nChunkSprite[v6 = (RandomSize(7) % nChunkTotal)]) != -1)
				&& engine.cansee(sx, sy, sz, sectnum, sprite[v7].x, sprite[v7].y, sprite[v7].z, sprite[v7].sectnum)) {
			return nChunkSprite[v6];
		} else if (nBodyTotal != 0 && ((v10 = nBodySprite[v9 = (RandomSize(7) % nBodyTotal)]) != -1)
				&& (nPlayerPic == sprite[v10].picnum) && engine.cansee(sx, sy, sprite[a1].z, sprite[a1].sectnum,
						sprite[v10].x, sprite[v10].y, sprite[v10].z, sprite[v10].sectnum)) {
			return nBodySprite[v9];
		}

		return -1;
	}

	public static void FuncRat(int a1, int a2, int RunPtr) {
		short nRat = (short) (RunData[RunPtr].RunEvent & 0xFFFF);
		int nSprite = RatList[nRat].nSprite;
		SPRITE pSprite = sprite[nSprite];
		int nState = RatList[nRat].nState;
		short plr = (short) (a1 & 0xFFFF);
		int damage = a2;

		switch (a1 & 0x7F0000) {
		case nEventProcess:
			int v51 = 0;
			int v10 = ActionSeq_X_14[nState][0] + SeqOffsets[77];
			short v13 = RatList[nRat].nSeq;
			pSprite.picnum = (short) GetSeqPicnum2(v10, v13);
			MoveSequence(nSprite, v10, v13);
			RatList[nRat].nSeq++;
			if (RatList[nRat].nSeq >= SeqSize[v10]) {
				RatList[nRat].nSeq = 0;
				v51 = 1;
			}
			int nTarget = RatList[nRat].nTarget;
			Gravity(nSprite);

			switch (nState) {
			case 2:
				if ((pSprite.yvel | pSprite.xvel | pSprite.zvel) != 0) {
					if(isOriginal())
						MoveCreature(nSprite);
					else MoveCreatureWithCaution(nSprite);
				}
				
				RatList[nRat].field_A--;
				if (RatList[nRat].field_A <= 0) {
					RatList[nRat].nTarget = (short) FindFood(nSprite);
					if (RatList[nRat].nTarget > -1) {
						PlotCourseToSprite(nSprite, RatList[nRat].nTarget);
						SetRatVel(nSprite);
						RatList[nRat].nState = 1;
						RatList[nRat].field_C = 900;
						RatList[nRat].nSeq = 0;
						break;
					}
					RatList[nRat].field_A = (short) RandomSize(6);
					if ((pSprite.xvel | pSprite.yvel) != 0) {
						pSprite.xvel = pSprite.yvel = 0;
					} else {
						pSprite.ang = (short) RandomSize(11);
						SetRatVel(nSprite);
					}
				}
				break;
			case 1:
				if (--RatList[nRat].field_C <= 0) {
					RatList[nRat].nState = 2;
					RatList[nRat].nSeq = 0;

					RatList[nRat].nTarget = -1;
					pSprite.xvel = 0;
					pSprite.yvel = 0;
				}
				if(isOriginal())
					MoveCreature(nSprite);
				else MoveCreatureWithCaution(nSprite);
				
				if (klabs(sprite[nSprite].x - sprite[nTarget].x) >= 50
						|| klabs(sprite[nSprite].y - sprite[nTarget].y) >= 50) {
					if (--RatList[nRat].field_A < 0) {
						PlotCourseToSprite(nSprite, nTarget);
						SetRatVel(nSprite);
						RatList[nRat].field_A = 32;
					}
				} else {
					RatList[nRat].nState = 0;
					RatList[nRat].nSeq = 0;
					RatList[nRat].field_C = (short) RandomSize(3);
					pSprite.xvel = 0;
					pSprite.yvel = 0;
				}
				break;
			case 0:
				if (--RatList[nRat].field_A <= 0) {
					if (klabs(sprite[nSprite].x - sprite[nTarget].x) <= 50
							&& klabs(sprite[nSprite].y - sprite[nTarget].y) <= 50) {
						RatList[nRat].nSeq ^= 1;
						RatList[nRat].field_A = (short) (RandomSize(5) + 4);
						if (--RatList[nRat].field_C <= 0) {
							int v44 = FindFood(nSprite);
							if (v44 != -1) {
								RatList[nRat].nTarget = (short) v44;
								PlotCourseToSprite(nSprite, v44);
								SetRatVel(nSprite);
								RatList[nRat].nState = 1;
								RatList[nRat].field_C = 900;
								RatList[nRat].nSeq = 0;
							}
						}
					} else {
						RatList[nRat].nState = 2;
						RatList[nRat].nSeq = 0;
						RatList[nRat].nTarget = -1;
						sprite[nSprite].xvel = 0;
						sprite[nSprite].yvel = 0;
					}
				}
				break;
			case 3:
				if (v51 != 0) {
					DoSubRunRec(sprite[nSprite].owner);
					FreeRun(sprite[nSprite].lotag - 1);
					SubRunRec(RatList[nRat].nFunc);
					sprite[nSprite].cstat = (short) 32768;
					engine.mydeletesprite((short) nSprite);
				}
				break;
			}
			return;
		case nEventView:
			PlotSequence(plr, ActionSeq_X_14[nState][0] + SeqOffsets[77], RatList[nRat].nSeq,
					ActionSeq_X_14[nState][1]);
			return;
		case nEventRadialDamage:
			damage = CheckRadialDamage(nSprite);
		case nEventDamage:
			if (damage != 0) {
				sprite[nSprite].cstat = 0;
				sprite[nSprite].xvel = 0;
				sprite[nSprite].yvel = 0;
				RatList[nRat].nSeq = 0;
				RatList[nRat].nState = 3;
			}
			return;
		}
	}
}
