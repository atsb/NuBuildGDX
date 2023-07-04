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
import static ru.m210projects.Powerslave.RunList.*;
import static ru.m210projects.Powerslave.Seq.*;
import static ru.m210projects.Powerslave.Sound.*;
import static ru.m210projects.Powerslave.Sprites.*;
import static ru.m210projects.Powerslave.Enemies.Enemy.*;


import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ru.m210projects.Build.FileHandle.Resource;
import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Powerslave.Enemies.Enemy.EnemyStruct;
import ru.m210projects.Powerslave.Type.SafeLoader;

import static ru.m210projects.Powerslave.Bullet.*;

public class LavaDude {

	public static final int MAX_LAVAS = 20;
	private static int LavaCount;
//	private static int LavaSprite;

	private static final short[][] ActionSeq_X_6 = new short[][] { { 0, 1 }, { 0, 1 }, { 1, 0 }, { 10, 0 }, { 19, 0 },
			{ 28, 1 }, { 29, 1 }, { 33, 0 }, { 42, 1 }, };
	private static EnemyStruct LavaList[] = new EnemyStruct[MAX_LAVAS];

	public static void InitLava() {
		LavaCount = 0;
//		LavaSprite = 1;	
	}
	
	public static ByteBuffer saveLava()
	{
		ByteBuffer bb = ByteBuffer.allocate(LavaCount * EnemyStruct.size + 2);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		
		bb.putShort((short)LavaCount);
		for(int i = 0; i < LavaCount; i++)
			LavaList[i].save(bb);
		
		return bb;
	}
	
	public static void loadLava(SafeLoader loader, Resource bb)
	{
		if(bb != null) {
			loader.LavaCount = bb.readShort();
			for(int i = 0; i < loader.LavaCount; i++) {
				if(loader.LavaList[i] == null)
					loader.LavaList[i] = new EnemyStruct();
				loader.LavaList[i].load(bb);
			}
		}
		else
		{
			LavaCount = loader.LavaCount;
			for(int i = 0; i < loader.LavaCount; i++) {
				if (LavaList[i] == null)
					LavaList[i] = new EnemyStruct();
				LavaList[i].copy(loader.LavaList[i]);
			}
		}
	}

	public static void BuildLava(int spr, int x, int y, int z, int sectnum, int ang, int channel) {
		int count = LavaCount++;
		if (count >= 20)
			return;

		if (spr == -1) {
			spr = engine.insertsprite((short) sectnum, (short) 118);
		} else {
			x = sprite[spr].x;
			y = sprite[spr].y;
			sectnum = sprite[spr].sectnum;
			ang = sprite[spr].ang;
			engine.changespritestat((short) spr, (short) 118);
		}

		if (spr < 0 || spr >= MAXSPRITES) {
			game.ThrowError("spr>=0 && spr<MAXSPRITES");
			return;
		}

		sprite[spr].x = x;
		sprite[spr].y = y;
		sprite[spr].z = sector[sectnum].floorz;
		sprite[spr].cstat = (short) 32768;
		sprite[spr].xoffset = 0;
		sprite[spr].shade = -12;
		sprite[spr].yoffset = 0;
		sprite[spr].picnum = GetSeqPicnum(42, ActionSeq_X_6[3][0], 0);
		sprite[spr].pal = sector[sprite[spr].sectnum].ceilingpal;
		sprite[spr].clipdist = 127;
		sprite[spr].ang = (short) ang;
		sprite[spr].xrepeat = 200;
		sprite[spr].yrepeat = 200;
		sprite[spr].xvel = 0;
		sprite[spr].yvel = 0;
		sprite[spr].zvel = 0;
		sprite[spr].hitag = 0;
		sprite[spr].lotag = (short) (HeadRun() + 1);
		sprite[spr].extra = -1;
		
		if (LavaList[count] == null)
			LavaList[count] = new EnemyStruct();

		LavaList[count].nState = 0;
		LavaList[count].nHealth = 4000;
		LavaList[count].nSprite = (short) spr;
		LavaList[count].nTarget = -1;
		LavaList[count].field_C = (short) channel;
		LavaList[count].nSeq = 0;
		sprite[spr].owner = (short) AddRunRec(sprite[spr].lotag - 1, 0x150000 | count);
		LavaList[count].nFunc = (short) AddRunRec(NewRun, 0x150000 | count);
		nCreaturesLeft++;
		nCreaturesMax++;
	}

	public static void FuncLava(int a1, int nDamage, int RunPtr) {
		short nLava = (short) (RunData[RunPtr].RunEvent & 0xFFFF);
		if (nLava < 0 || nLava >= MAX_LAVAS) {
			game.ThrowError("Lava>=0 && Lava<MAX_LAVAS");
			return;
		}

		EnemyStruct pLava = LavaList[nLava];
		int nSprite = pLava.nSprite;
		SPRITE pSprite = sprite[nSprite];
		int nState = pLava.nState;
		int nTarget = pLava.nTarget;
		short nObject = (short) (a1 & 0xFFFF);
		int v57 = 0;
		switch (a1 & 0x7F0000) {
		case nEventProcess:

			int nSeq = ActionSeq_X_6[nState][0] + SeqOffsets[42];
			pSprite.picnum = (short) GetSeqPicnum2(nSeq, pLava.nSeq);
			if (nState != 0) {
				MoveSequence(nSprite, nSeq, pLava.nSeq);
				if (++pLava.nSeq >= SeqSize[nSeq]) {
					pLava.nSeq = 0;
					v57 = 1;
				}
			}
			int nFlags = FrameFlag[pLava.nSeq + SeqBase[nSeq]];
			if (nTarget >= 0 && nState < 4) {
				if ((sprite[nTarget].cstat & 0x101) == 0 || sprite[nTarget].sectnum >= 1024) {
					nTarget = -1;
					pLava.nTarget = -1;
				}
			}

			switch (nState) {
			case 0:
				if ((nLava & 0x1F) == (totalmoves & 0x1F)) {
					if (nTarget < 0)
						nTarget = FindPlayer(nSprite, 76800);
					PlotCourseToSprite(nSprite, nTarget);
					pSprite.xvel = sintable[(pSprite.ang + 512) & 0x7FF];
					pSprite.yvel = sintable[pSprite.ang & 0x7FF];

					if (nTarget >= 0 && RandomSize(1) == 0) {
						pLava.nTarget = (short) nTarget;
						pLava.nState = 2;
						pSprite.cstat = 257;
						pLava.nSeq = 0;
						break;
					}
				}

				int sx = pSprite.x;
				int sy = pSprite.y;
				int sz = pSprite.z;
				short ssec = pSprite.sectnum;
				int hitMove = engine.movesprite((short) nSprite, pSprite.xvel << 8, pSprite.yvel << 8, 0, 0, 0, 0);
				if (ssec == pSprite.sectnum) {
					if (hitMove == 0)
						break;
					
					switch (hitMove & 0xC000) {
					case 0x8000:
						pSprite.ang = (short) ((pSprite.ang + ((RandomWord() & 0x300) + 1024)) & 0x7FF);
						pSprite.xvel = sintable[(pSprite.ang + 512) & 0x7FF];
						pSprite.yvel = sintable[pSprite.ang & 0x7FF];
						pSprite.pal = 1;
						return;
					case 0xC000:
						if ((hitMove & 0x3FFF) == nTarget) {
							if (AngleDiff(pSprite.ang, engine.getangle(sprite[nTarget].x - pSprite.x,
									sprite[nTarget].y - pSprite.y)) < 64) {
								pLava.nState = 2;
								pSprite.cstat = 257;
								pLava.nSeq = 0;
							}
						}
						pSprite.pal = 1;
						return;
					}
				} else {
					engine.changespritesect((short) nSprite, ssec);
					pSprite.x = sx;
					pSprite.y = sy;
					pSprite.z = sz;
				}

				pSprite.ang = (short) ((pSprite.ang + ((RandomWord() & 0x300) + 1024)) & 0x7FF);
				pSprite.xvel = sintable[(pSprite.ang + 512) & 0x7FF];
				pSprite.yvel = sintable[pSprite.ang & 0x7FF];
				break;
			case 2:
				if (v57 != 0) {
					pLava.nState = 3;
					pLava.nSeq = 0;
					PlotCourseToSprite(nSprite, nTarget);
					pSprite.cstat |= 0x101;
				}
				break;
			case 3:
				if ((nFlags & 0x80) != 0 && nTarget > -1) {
					GetUpAngle(nSprite, -64000, nTarget, -(GetSpriteHeight(nSprite) >> 1));
					BuildBullet(nSprite, 10, sintable[(pSprite.ang + 512) & 0x7FF] << 8,
							sintable[pSprite.ang & 0x7FF] << 8, -1, pSprite.ang, nTarget + 10000, 1);
				} else if (v57 != 0) {
					PlotCourseToSprite(nSprite, nTarget);
					pLava.nState = 7;
					pLava.nSeq = 0;
				}
				break;
			case 5:
				if ((nFlags & 0x40) != 0) {
					D3PlayFX(StaticSound[26], BuildLavaLimb(nSprite, pLava.nSeq, 64000));
				}
				if (pLava.nSeq != 0) {
					if ((nFlags & 0x80) != 0) {
						for (int i = 0; i < 20; i++)
							BuildLavaLimb(nSprite, i, 64000);
						ChangeChannel(pLava.field_C, 1);
					}
				} else {
					for (int i = 0; i < 20; i++)
						BuildLavaLimb(nSprite, i, 256);
					DoSubRunRec(pSprite.owner);
					FreeRun(pSprite.lotag - 1);
					SubRunRec(pLava.nFunc);
					engine.mydeletesprite((short) nSprite);
				}
				break;
			case 4:
				if (v57 != 0) {
					pLava.nState = 7;
					pSprite.cstat &= ~0x101;
				}
				break;
			case 7:
				if (v57 != 0) {
					pLava.nState = 8;
					pLava.nSeq = 0;
				}
				break;
			case 8:
				if (v57 != 0) {
					pLava.nState = 0;
					pLava.nSeq = 0;
					pSprite.cstat = (short) 32768;
				}
				break;
			}

			pSprite.pal = 1;
			return;
		case nEventView:
			PlotSequence(nObject, ActionSeq_X_6[nState][0] + SeqOffsets[42], pLava.nSeq, ActionSeq_X_6[nState][1]);
			tsprite[nObject].owner = -1;
			return;
		case nEventDamage:
			if (nDamage != 0) {
				if (pLava.nHealth > 0) {
					pLava.nHealth -= nDamage;
					if (pLava.nHealth <= 0) {
						pSprite.cstat &= ~0x101;
						pLava.nHealth = 0;
						pLava.nState = 5;
						pLava.nSeq = 0;
						nCreaturesLeft--;
					} else {
						if (nTarget >= 0 && sprite[nTarget].statnum < 199)
							pLava.nTarget = (short) nTarget;
						if (nState == 3 && RandomSize(2) == 0) {
							pLava.nState = 4;
							pLava.nSeq = 0;
							pSprite.cstat = 0;
						}
						BuildLavaLimb(nSprite, totalmoves, 64000);
					}
				}
			}
			return;
		}
	}

	public static int BuildLavaLimb(int nSprite, int a2, int a3) {
		int spr = engine.insertsprite(sprite[nSprite].sectnum, (short) 118);
		if (spr < 0 || spr >= MAXSPRITES) {
			game.ThrowError("spr>=0 && spr<MAXSPRITES");
			return -1;
		}

		sprite[spr].x = sprite[nSprite].x;
		sprite[spr].y = sprite[nSprite].y;
		sprite[spr].z = sprite[nSprite].z - RandomLong() % a3;
		sprite[spr].cstat = 0;
		sprite[spr].shade = -127;
		sprite[spr].pal = 1;
		sprite[spr].xvel = (short) ((RandomSize(5) - 16) << 8);
		sprite[spr].yvel = (short) ((RandomSize(5) - 16) << 8);
		sprite[spr].zvel = (short) (2560 - (RandomSize(5) << 8));
		sprite[spr].yoffset = 0;
		sprite[spr].xoffset = 0;
		sprite[spr].xrepeat = 90;
		sprite[spr].yrepeat = 90;
		sprite[spr].picnum = (short) ((a2 & 3) % 3);
		sprite[spr].clipdist = 0;
		sprite[spr].extra = -1;
		sprite[spr].lotag = (short) (HeadRun() + 1);
		sprite[spr].owner = (short) AddRunRec(sprite[spr].lotag - 1, 0x160000 | spr);
		sprite[spr].hitag = (short) AddRunRec(NewRun, 0x160000 | spr);

		return spr;
	}

	public static void FuncLavaLimb(int a1, int a2, int RunPtr) {
		short spr = (short) (RunData[RunPtr].RunEvent & 0xFFFF);
		if (spr < 0 || spr >= MAXSPRITES) {
			game.ThrowError("spr>=0 && spr<MAXSPRITES");
			return;
		}

		SPRITE pSprite = sprite[spr];
		short nObject = (short) (a1 & 0xFFFF);
		switch (a1 & 0x7F0000) {
		case nEventProcess:
			
			pSprite.shade += 3;
			if (engine.movesprite(spr, pSprite.xvel << 12, pSprite.yvel << 12, pSprite.zvel, 2560, -2560,
					1) != 0 || (pSprite.shade > 100)) {
				pSprite.xvel = 0;
				pSprite.yvel = 0;
				pSprite.zvel = 0;
				
				DoSubRunRec(pSprite.owner);
				FreeRun(pSprite.lotag - 1);
				SubRunRec(pSprite.hitag);
				engine.mydeletesprite(spr);
			}
			return;
		case nEventView:
			PlotSequence(nObject, pSprite.picnum + SeqOffsets[42] + 30, 0, 1);
			return;
		}
	}
}
