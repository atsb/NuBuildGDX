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
import static ru.m210projects.Powerslave.Enemies.Enemy.*;
import static ru.m210projects.Powerslave.RunList.*;
import static ru.m210projects.Powerslave.Seq.*;
import static ru.m210projects.Powerslave.Sound.*;
import static ru.m210projects.Powerslave.Sprites.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ru.m210projects.Build.FileHandle.Resource;
import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Powerslave.Enemies.Enemy.EnemyStruct;
import ru.m210projects.Powerslave.Type.SafeLoader;

public class Lion {
	
	public static class LionStruct extends EnemyStruct {
		public static final int size = EnemyStruct.size + 2;
		
		public short MoveHook;
		
		@Override
		public void save(ByteBuffer bb)
		{
			super.save(bb);
			bb.putShort(MoveHook);
		}
		
		@Override
		public void load(Resource bb)
		{
			super.load(bb);
			MoveHook = bb.readShort();
		}
		
		public LionStruct copy(LionStruct src)
		{
			super.copy(src);
			
			MoveHook = src.MoveHook;
			return this;
		}
	}

	public static final int MAXLION = 40;
	
	private static int LionCount;
	private static LionStruct LionList[] = new LionStruct[MAXLION];
	private static final short[][] ActionSeq_X_13 = new short[][] { { 54, 1 }, { 18, 0 }, { 0, 0 }, { 10, 0 }, { 44, 0 },
			{ 18, 0 }, { 26, 0 }, { 34, 0 }, { 8, 1 }, { 9, 1 }, { 52, 1 }, { 53, 1 } };

	public static void InitLion() {
		LionCount = MAXLION;
	}

	public static ByteBuffer saveLion()
	{
		ByteBuffer bb = ByteBuffer.allocate((MAXLION - LionCount) * LionStruct.size + 2);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		
		bb.putShort((short)LionCount);
		for(int i = LionCount; i < MAXLION; i++)
			LionList[i].save(bb);
		
		return bb;
	}
	
	public static void loadLion(SafeLoader loader, Resource bb)
	{
		if(bb != null) {
			loader.LionCount = bb.readShort();
			for(int i = loader.LionCount; i < MAXLION; i++) {
				if(loader.LionList[i] == null)
					loader.LionList[i] = new LionStruct();
				loader.LionList[i].load(bb);
			}
		}
		else
		{
			LionCount = loader.LionCount;
			for(int i = loader.LionCount; i < MAXLION; i++) {
				if (LionList[i] == null)
					LionList[i] = new LionStruct();
				LionList[i].copy(loader.LionList[i]);
			}
		}
	}
	
	public static void BuildLion(int spr, int x, int y, int z, int sectnum, int ang) {
		int count = --LionCount;

		if (count < 0)
			return;

		if (spr == -1) {
			spr = engine.insertsprite((short) sectnum, (short) 104);
		} else {
			x = sprite[spr].x;
			y = sprite[spr].y;
			z = sector[sprite[spr].sectnum].floorz;
			ang = sprite[spr].ang;
			engine.changespritestat((short) spr, (short) 104);
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
		
		if (LionList[count] == null)
			LionList[count] = new LionStruct();

		LionList[count].nState = 0;
		LionList[count].nHealth = 500;
		LionList[count].nSeq = 0;
		LionList[count].nSprite = (short) spr;
		LionList[count].nTarget = -1;
		LionList[count].field_C = 0;
		LionList[count].field_A = (short) count;
		sprite[spr].owner = (short) AddRunRec(sprite[spr].lotag - 1, 0x130000 | count);
		LionList[count].MoveHook = (short) AddRunRec(NewRun, 0x130000 | count);
		nCreaturesLeft++;
		nCreaturesMax++;
	}

	public static void FuncLion(int a1, int nDamage, int RunPtr) {
		short nLion = (short) (RunData[RunPtr].RunEvent & 0xFFFF);
		if (nLion < 0 || nLion >= MAXLION) {
			game.ThrowError("lion>=0 && lion<MAXLION");
			return;
		}

		LionStruct pLion = LionList[nLion];
		int nSprite = pLion.nSprite;
		SPRITE pSprite = sprite[nSprite];
		int v114 = 0;

		int nState = pLion.nState;
		int nTarget = pLion.nTarget;

		short nObject = (short) (a1 & 0xFFFF);
		switch (a1 & 0x7F0000) {
		case nEventProcess:
			if (nState != 7)
				Gravity(nSprite);

			int nSeq = ActionSeq_X_13[nState][0] + SeqOffsets[40];
			pSprite.picnum = (short) GetSeqPicnum2(nSeq, pLion.nSeq);
			MoveSequence(nSprite, nSeq, pLion.nSeq);
			if (++pLion.nSeq >= SeqSize[nSeq]) {
				pLion.nSeq = 0;
				v114 = 1;
			}

			int nFlags = FrameFlag[pLion.nSeq + SeqBase[nSeq]];
			int moveHit = MoveCreatureWithCaution(nSprite);

			switch (nState) {
			case 0:
			case 1:
				int nTarg = -1;
				if ((pLion.field_A & 0x1F) != (totalmoves & 0x1F) || nTarget >= 0
						|| (nTarg = FindPlayer(nSprite, 40)) < 0) {
					if (nState != 0) {
						if (--pLion.field_C <= 0) {
							if (RandomBit() != 0) {
								pSprite.ang = (short) (RandomWord() & 0x7FF);
								pSprite.xvel = (short) (sintable[(pSprite.ang + 512) & 0x7FF] >> 1);
								pSprite.yvel = (short) (sintable[pSprite.ang] >> 1);
							} else {
								pSprite.xvel = 0;
								pSprite.yvel = 0;
							}
							pLion.field_C = 100;
						}
					}
				} else {
					D3PlayFX(StaticSound[24], nSprite);
					pLion.nState = 2;
					pLion.nSeq = 0;
					pLion.nTarget = (short) nTarg;
					pSprite.xvel = (short) (sintable[(pSprite.ang + 512) & 0x7FF] >> 1);
					pSprite.yvel = (short) (sintable[pSprite.ang] >> 1);
				}
				return;
			case 2:
				if ((totalmoves & 0x1F) == (pLion.field_A & 0x1F)) {
					PlotCourseToSprite(nSprite, nTarget);
					int ang = pSprite.ang & 0xFFF8;
					if ((pSprite.cstat & 0x8000) != 0) {
						pSprite.xvel = (short) (2 * sintable[(ang + 512) & 0x7FF]);
						pSprite.yvel = (short) (2 * sintable[ang & 0x7FF]);
					} else {
						pSprite.xvel = (short) (sintable[(ang + 512) & 0x7FF] >> 1);
						pSprite.yvel = (short) (sintable[ang & 0x7FF] >> 1);
					}
				}
				
				switch (moveHit & 0xC000) {
				case 0xC000:
					if ((moveHit & 0x3FFF) == nTarget) {
						if ((pSprite.cstat & 0x8000) != 0) {
							pLion.nState = 9;
							pSprite.cstat &= ~0x8000;
							pSprite.xvel = 0;
							pSprite.yvel = 0;
						} else {
							if (AngleDiff(pSprite.ang, engine.getangle(sprite[nTarget].x - pSprite.x,
									sprite[nTarget].y - pSprite.y)) < 64)
								pLion.nState = 3;
						}
						pLion.nSeq = 0;
					}
				case 0x8000:
					pSprite.ang = (short) ((pSprite.ang + 256) & 0x7FF);
					pSprite.xvel = (short) (sintable[(pSprite.ang + 512) & 0x7FF] >> 1);
					pSprite.yvel = (short) (sintable[pSprite.ang] >> 1);
					break;
				}
				break;
			case 3:
				if (nTarget == -1) {
					pLion.nState = 1;
					pLion.field_C = 50;
				} else if (PlotCourseToSprite(nSprite, nTarget) >= 768)
					pLion.nState = 2;
				else if ((nFlags & 0x80) != 0)
					DamageEnemy(nTarget, nSprite, 10);
				break;
			case 10:
			case 11:
				if (v114 != 0) {
					SubRunRec(pSprite.owner);
					SubRunRec(pLion.MoveHook);
					pSprite.cstat = (short) 32768;
				}
				return;
			case 4:
				if (v114 != 0) {
					pLion.nState = 2;
					pLion.nSeq = 0;
				}
				if ((moveHit & 0x200) != 0) {
					pSprite.xvel >>= 1;
					pSprite.yvel >>= 1;
				}
				return;
			case 8:
				if (v114 != 0) {
					pLion.nSeq = 0;
					pLion.nState = 2;
					pSprite.cstat |= 0x8000;
				}
				return;
			case 9:
				if (v114 != 0) {
					pLion.nSeq = 0;
					pLion.nState = 2;
					pSprite.cstat |= 0x101;
				}
				return;
			case 6:
				if ((moveHit & 0x30000) != 0) {
					pLion.nState = 2;
					pLion.nSeq = 0;
				} else {
					if ((moveHit & 0xC000) == 0x8000) {
						pLion.nState = 7;
						pSprite.ang = (short) ((engine.GetWallNormal(moveHit & 0x3FFF) + 1024) & 0x7FF);
						pLion.field_C = (short) RandomSize(4);
					} else if ((moveHit & 0xC000) == 0xC000) {
						if ((moveHit & 0x3FFF) == nTarget) {
							if (AngleDiff(pSprite.ang, engine.getangle(sprite[nTarget].x - pSprite.x,
									sprite[nTarget].y - pSprite.y)) < 64) {
								pLion.nState = 3;
								pLion.nSeq = 0;
							}
						} else {
							pSprite.ang = (short) ((pSprite.ang + 256) & 0x7FF);
							pSprite.xvel = (short) (sintable[(pSprite.ang + 512) & 0x7FF] >> 1);
							pSprite.yvel = (short) (sintable[pSprite.ang] >> 1);
							break;
						}
					}
				}
				return;
			case 5:
			case 7:
				if (--pLion.field_C <= 0) {
					pLion.field_C = 0;
					if (nState == 7) {
						if (nTarget <= -1)
							pSprite.ang = (short) ((pSprite.ang + RandomSize(9)) & 0x7FF);
						else
							PlotCourseToSprite(nSprite, nTarget);
						pSprite.zvel = -1000;
					} else {
						pSprite.zvel = -4000;
						short ang = pSprite.ang;

						int hita = (ang - 512) & 0x7FF;
						int xs = pSprite.x;
						int ys = pSprite.y;
						int zs = pSprite.z - GetSpriteHeight(nSprite) >> 1;
						short sectnum = pSprite.sectnum;
						int v104 = 0x7FFFFFFF, v70;
						for (int i = 0; i < 5; i++) {
							engine.hitscan(xs, ys, zs, sectnum, sintable[(hita + 512) & 0x7FF], sintable[hita & 0x7FF],
									0, pHitInfo, CLIPMASK1);
							if (pHitInfo.hitwall != -1) {
								if ((v70 = klabs(pHitInfo.hitx - xs) + klabs(pHitInfo.hity - ys)) < v104) {
									v104 = v70;
									ang = (short) hita;
								}
							}
							hita = (hita + 256) & 0x7FF;
						}
						pSprite.ang = ang;
					}

					pLion.nState = 6;
					pSprite.xvel = (short) (sintable[(pSprite.ang + 512) & 0x7FF]
							- (sintable[(pSprite.ang + 512) & 0x7FF] >> 3));
					pSprite.yvel = (short) (sintable[pSprite.ang & 0x7FF] - (sintable[pSprite.ang & 0x7FF] >> 3));
					D3PlayFX(StaticSound[24], nSprite);
				}
				return;
			}
			
			if (nState != 1 && nTarget != -1 && (sprite[nTarget].cstat & 0x101) == 0) {
				pLion.nState = 1;
				pLion.nSeq = 0;
				pLion.field_C = 100;
				pLion.nTarget = -1;
				pSprite.xvel = 0;
				pSprite.yvel = 0;
			}
			return;
		case nEventView:
			PlotSequence(nObject, ActionSeq_X_13[nState][0] + SeqOffsets[40], pLion.nSeq,
					ActionSeq_X_13[nState][1]);
			return;
		case nEventRadialDamage:
			nDamage = CheckRadialDamage(nSprite);
		case nEventDamage:
			if (nDamage != 0) {
				if (pLion.nHealth > 0) {
					pLion.nHealth -= nDamage;
					if (pLion.nHealth > 0) {
						if (nObject >= 0) {
							if (sprite[nObject].statnum < 199)
								pLion.nTarget = nObject;
							if (nState != 6) {
								if (RandomSize(8) <= pLion.nHealth) {
									pLion.nState = 4;
									pSprite.xvel = 0;
									pSprite.yvel = 0;
								} else if (RandomSize(1) != 0) {
									PlotCourseToSprite(nSprite, nObject);
									pSprite.ang -= RandomSize(1) << 8;
									pSprite.ang += RandomSize(1) << 8;
									pSprite.ang &= 0x7FF;
									pLion.nState = 5;
									pLion.field_C = (short) RandomSize(3);
								} else {
									pSprite.xvel = 0;
									pSprite.yvel = 0;
									pSprite.cstat &= ~0x101;
									pLion.nState = 8;
								}
								pLion.nSeq = 0;
							}
						}
					}
					else {
						pSprite.xvel = 0;
						pSprite.yvel = 0;
						pSprite.zvel = 0;
						pSprite.cstat &= ~0x101;
						pLion.nHealth = 0;
						if (pLion.nState < 10) {
							DropMagic(nSprite);
							pLion.nState = (short) ((((a1 & 0x7F0000) == nEventRadialDamage) ? 1 : 0) + 10);
							pLion.nSeq = 0;
						}
						nCreaturesLeft--;
					}
				} 
			}
			return;
		}
	}
}
