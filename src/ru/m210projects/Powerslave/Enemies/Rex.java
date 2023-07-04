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
import static ru.m210projects.Powerslave.Random.*;
import static ru.m210projects.Powerslave.Enemies.Enemy.*;
import static ru.m210projects.Powerslave.RunList.*;
import static ru.m210projects.Powerslave.Seq.*;
import static ru.m210projects.Powerslave.Object.*;
import static ru.m210projects.Powerslave.Sound.*;
import static ru.m210projects.Powerslave.Player.*;
import static ru.m210projects.Powerslave.Sprites.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ru.m210projects.Build.FileHandle.Resource;
import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Powerslave.Enemies.Enemy.EnemyStruct;
import ru.m210projects.Powerslave.Type.SafeLoader;

public class Rex {

	public static final int MAX_REX = 50;
	
	private static int RexCount;
	private static EnemyStruct[] RexList = new EnemyStruct[MAX_REX];
	private static short[] RexChan = new short[MAX_REX];

	private static final short[][] ActionSeq_X_8 = new short[][] { { 29, 0 }, { 0, 0 }, { 0, 0 }, { 37, 0 }, { 9, 0 },
			{ 18, 0 }, { 27, 1 }, { 28, 1 } };

	public static void InitRexs() {
		RexCount = MAX_REX;
	}
	
	public static ByteBuffer saveRex()
	{
		ByteBuffer bb = ByteBuffer.allocate((MAX_REX - RexCount) * EnemyStruct.size + 2 + MAX_REX * 2);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		
		bb.putShort((short)RexCount);
		for(int i = RexCount; i < MAX_REX; i++)
			RexList[i].save(bb);
		for(int i = 0; i < MAX_REX; i++)
			bb.putShort(RexChan[i]);
		
		return bb;
	}
	
	public static void loadRex(SafeLoader loader, Resource bb)
	{
		if(bb != null) {
			loader.RexCount = bb.readShort();
			for(int i = loader.RexCount; i < MAX_REX; i++) {
				if(loader.RexList[i] == null)
					loader.RexList[i] = new EnemyStruct();
				loader.RexList[i].load(bb);
			}
			for(int i = 0; i < MAX_REX; i++)
				loader.RexChan[i] = bb.readShort();
		}
		else
		{
			RexCount = loader.RexCount;
			for(int i = loader.RexCount; i < MAX_REX; i++) {
				if (RexList[i] == null)
					RexList[i] = new EnemyStruct();
				RexList[i].copy(loader.RexList[i]);
			}
			System.arraycopy(loader.RexChan, 0, RexChan, 0, MAX_REX);
		}
	}

	public static void BuildRex(int spr, int x, int y, int z, int sectnum, int ang, int channel) {
		int count = --RexCount;

		if (count < 0)
			return;

		if (spr == -1) {
			spr = engine.insertsprite((short) sectnum, (short) 119);
		} else {
			x = sprite[spr].x;
			y = sprite[spr].y;
			z = sector[sprite[spr].sectnum].floorz;
			ang = sprite[spr].ang;
			engine.changespritestat((short) spr, (short) 119);
		}

		if (spr < 0 || spr >= MAXSPRITES) {
			game.ThrowError("spr>=0 && spr<MAXSPRITES");
			return;
		}

		sprite[spr].x = x;
		sprite[spr].y = y;
		sprite[spr].z = z;
		sprite[spr].cstat = 257;
		sprite[spr].xoffset = 0;
		sprite[spr].shade = -12;
		sprite[spr].yoffset = 0;
		sprite[spr].picnum = 1;
		sprite[spr].pal = sector[sprite[spr].sectnum].ceilingpal;
		sprite[spr].clipdist = 80;
		sprite[spr].ang = (short) ang;
		sprite[spr].xrepeat = 64;
		sprite[spr].yrepeat = 64;
		sprite[spr].xvel = 0;
		sprite[spr].yvel = 0;
		sprite[spr].zvel = 0;
		sprite[spr].hitag = 0;
		sprite[spr].lotag = (short) (HeadRun() + 1);
		sprite[spr].extra = -1;
		
		if (RexList[count] == null)
			RexList[count] = new EnemyStruct();

		RexList[count].nState = 0;
		RexList[count].nHealth = 4000;
		RexList[count].nSeq = 0;
		RexList[count].nSprite = (short) spr;
		RexList[count].nTarget = -1;
		RexList[count].field_A = 0;
		sprite[spr].owner = (short) AddRunRec(sprite[spr].lotag - 1, 0x180000 | count);
		AddRunRec(NewRun, 0x180000 | count);
		RexChan[count] = (short) channel;

		nCreaturesLeft++;
		nCreaturesMax++;
	}

	public static void FuncRex(int a1, int nDamage, int RunPtr) {
		short nRex = (short) (RunData[RunPtr].RunEvent & 0xFFFF);
		if (nRex < 0 || nRex >= MAX_REX) {
			game.ThrowError("rex>=0 && rex<MAX_REX");
			return;
		}

		EnemyStruct pRex = RexList[nRex];
		int nSprite = pRex.nSprite;
		SPRITE pSprite = sprite[nSprite];
		int nState = pRex.nState;
		int nTarget = pRex.nTarget;

		int v67 = 0;

		short nObject = (short) (a1 & 0xFFFF);
		
		lCheckTarget:
		switch (a1 & 0x7F0000) {
		case nEventProcess:
			Gravity(nSprite);

			int nSeq = ActionSeq_X_8[nState][0] + SeqOffsets[47];
			pSprite.picnum = (short) GetSeqPicnum2(nSeq, pRex.nSeq);

			int loops = 1;
			if (nState == 2)
				loops = 2;
			for (int i = 0; i < loops; i++) {
				MoveSequence(nSprite, nSeq, pRex.nSeq);
				if (++pRex.nSeq >= SeqSize[nSeq]) {
					pRex.nSeq = 0;
					v67 = 1;
				}
			}
			int nFlags = FrameFlag[pRex.nSeq + SeqBase[nSeq]];

			lToTarget:
			switch (nState) {
			case 0:
				if (pRex.field_A != 0) {
					if (--pRex.field_A <= 0) {
						pRex.nState = 1;
						pRex.nSeq = 0;
						pSprite.xvel = (short) (sintable[(pSprite.ang + 512) & 0x7FF] >> 2);
						pSprite.yvel = (short) (sintable[pSprite.ang & 0x7FF] >> 2);
						D3PlayFX(StaticSound[48], nSprite);
						pRex.field_A = 30;
					}
				} else if ((nRex & 0x1F) == (totalmoves & 0x1F)) {
					if (nTarget >= 0) {
						pRex.field_A = 60;
					} else {
						short v17 = sprite[nSprite].ang;
						pRex.nTarget = (short) FindPlayer(nSprite, 60);
						sprite[nSprite].ang = v17;
					}
				}
				return;
			case 2:
				if (--pRex.field_A > 0) {
					PlotCourseToSprite(nSprite, nTarget);
					pSprite.xvel = (short) (sintable[(pSprite.ang + 512) & 0x7FF] >> 1);
					pSprite.yvel = (short) (sintable[pSprite.ang & 0x7FF] >> 1);
					int hitMove = MoveCreatureWithCaution(nSprite);

					switch (hitMove & 0xC000) {
					case 0x8000:
						SetQuake(nSprite, 25);
						pRex.field_A = 60;
						break lToTarget;
					case 0xC000:
						pRex.nState = 3;
						pRex.nSeq = 0;
						int spr = hitMove & 0x3FFF;
						if (sprite[spr].statnum != 0 && sprite[spr].statnum < 107) {
							DamageEnemy(spr, nSprite, 15);
							int xvel = 15 * sintable[(pSprite.ang + 512) & 0x7FF];
							int yvel = 15 * sintable[pSprite.ang & 0x7FF];
							if (sprite[spr].statnum == 100) {
								int v32 = GetPlayerFromSprite(spr);
								nXDamage[v32] += 16 * xvel;
								nYDamage[v32] += 16 * yvel;
								sprite[spr].zvel = -3584;
							} else {
								sprite[spr].xvel += xvel >> 3;
								sprite[spr].yvel += yvel >> 3;
								sprite[spr].zvel = -2880;
							}
						}
						pRex.field_A >>= 2;
					}
				} else {
					pRex.nState = 1;
					pRex.nSeq = 0;
					pRex.field_A = 90;
				}

				return;
			case 3:
				if (v67 != 0)
					pRex.nState = 2;
				return;
			case 1:
				if (pRex.field_A > 0)
					pRex.field_A--;
				
				if ((totalmoves & 0xF) == (nRex & 0xF)) {
					if (RandomSize(1) == 0) {
						pRex.nState = 5;
						pRex.nSeq = 0;
						pSprite.xvel = 0;
						pSprite.yvel = 0;
						return;
					}
					if (PlotCourseToSprite(nSprite, nTarget) >> 8 < 60) {
						if (pRex.field_A <= 0) {
							pRex.nState = 2;
							pRex.field_A = 240;
							D3PlayFX(StaticSound[48], nSprite);
							pRex.nSeq = 0;
							return;
						}
					}

					pSprite.xvel = (short) (sintable[(pSprite.ang + 512) & 0x7FF] >> 2);
					pSprite.yvel = (short) (sintable[pSprite.ang] >> 2);
				}

				int hitMove = MoveCreatureWithCaution(nSprite);
				switch (hitMove & 0xC000) {
				case 0x8000:
					break lToTarget;
				case 0xC000:
					if ((hitMove & 0x3FFF) == nTarget) {
						PlotCourseToSprite(nSprite, nTarget);
						pRex.nState = 4;
						pRex.nSeq = 0;
						break lCheckTarget;
					}
					break lToTarget;
				}
				break lCheckTarget;
			case 5:
				if (v67 != 0) {
					pRex.nState = 1;
					pRex.field_A = 15;
				}
				return;
			case 4:
				if (nTarget == -1 || PlotCourseToSprite(nSprite, nTarget) >= 768) {
					pRex.nState = 1;
				} else if ((nFlags & 0x80) != 0) {
					DamageEnemy(nTarget, nSprite, 15);
				}
				break lCheckTarget;
			case 6:
				if (v67 != 0) {
					pRex.nState = 7;
					pRex.nSeq = 0;
					ChangeChannel(RexChan[nRex], 1);
				}
				return;
			case 7:
				pSprite.cstat &= 0xFEFE;
				return;
			default:
				return;
			}

			pSprite.ang = (short) ((pSprite.ang + 256) & 0x7FF);
			pSprite.xvel = (short) (sintable[(pSprite.ang + 512) & 0x7FF] >> 2);
			pSprite.yvel = (short) (sintable[pSprite.ang] >> 2);
			nState = pRex.nState = 1;
			pRex.nSeq = 0;
			break;
		case nEventView:
			PlotSequence(nObject, ActionSeq_X_8[nState][0] + SeqOffsets[47], pRex.nSeq, ActionSeq_X_8[nState][1]);
			return;
		case nEventRadialDamage:
			if (nState == 5)
				nDamage = CheckRadialDamage(nSprite);
		case nEventDamage:
			if (nDamage != 0) {
				if (nTarget >= 0 && sprite[nTarget].statnum == 100)
					pRex.nTarget = (short) nTarget;
				if (pRex.nState == 5) {
					if (pRex.nHealth > 0) {
						pRex.nHealth -= nDamage;
						if (pRex.nHealth <= 0) {
							pSprite.xvel = 0;
							pSprite.yvel = 0;
							pSprite.zvel = 0;
							pSprite.cstat &= ~0x101;
							pRex.nHealth = 0;
							nCreaturesLeft--;
							if (pRex.nState < 6) {
								pRex.nState = 6;
								pRex.nSeq = 0;
							}
						}
					}
				}
			}
			return;
		}
		
		if (nState > 0 && nTarget != -1 && (sprite[nTarget].cstat & 0x101) == 0) {
			pRex.nState = 0;
			pRex.nSeq = 0;
			pRex.field_A = 0;
			pRex.nTarget = -1;
			pSprite.xvel = 0;
			pSprite.yvel = 0;
		}
	}
}
