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
import static ru.m210projects.Powerslave.Bullet.*;
import static ru.m210projects.Powerslave.Enemies.Enemy.*;
import static ru.m210projects.Powerslave.RunList.*;
import static ru.m210projects.Powerslave.Seq.*;
import static ru.m210projects.Powerslave.Object.*;
import static ru.m210projects.Powerslave.Sprites.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ru.m210projects.Build.FileHandle.Resource;
import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Powerslave.Enemies.Enemy.EnemyStruct;
import ru.m210projects.Powerslave.Type.SafeLoader;

public class Set {
	
	public static class SetStruct extends EnemyStruct {
		public static final int size = EnemyStruct.size + 4;
		
		public short field_D;
		public short field_E;
		
		@Override
		public void save(ByteBuffer bb)
		{
			super.save(bb);
			bb.putShort(field_D);
			bb.putShort(field_E);
		}
		
		@Override
		public void load(Resource bb)
		{
			super.load(bb);
			field_D = bb.readShort();
			field_E = bb.readShort();
		}
		
		public void copy(SetStruct src)
		{
			super.copy(src);
			field_D = src.field_D;
			field_E = src.field_E;
		}
	}

	public static final int MAX_SET = 10;
	
	private static int SetCount;
	private static SetStruct[] SetList = new SetStruct[MAX_SET];
	private static short[] SetChan = new short[MAX_SET];

	private static final short[][] ActionSeq_X_9 = new short[][] { { 0, 0 }, { 77, 1 }, { 78, 1 }, { 0, 0 }, { 9, 0 },
			{ 63, 0 }, { 45, 0 }, { 18, 0 }, { 27, 0 }, { 36, 0 }, { 72, 1 }, { 74, 1 } };

	public static void InitSets() {
		SetCount = MAX_SET;
	}
	
	public static ByteBuffer saveSet()
	{
		ByteBuffer bb = ByteBuffer.allocate((MAX_SET - SetCount) * SetStruct.size + 2 + MAX_SET * 2);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		
		bb.putShort((short)SetCount);
		for(int i = SetCount; i < MAX_SET; i++)
			SetList[i].save(bb);
		for(int i = 0; i < MAX_SET; i++)
			bb.putShort(SetChan[i]);
		
		return bb;
	}
	
	public static void loadSet(SafeLoader loader, Resource bb)
	{
		if(bb != null)
		{
			loader.SetCount = bb.readShort();
			for(int i = loader.SetCount; i < MAX_SET; i++) {
				if(loader.SetList[i] == null)
					loader.SetList[i] = new SetStruct();
				loader.SetList[i].load(bb);
			}
			for(int i = 0; i < MAX_SET; i++)
				loader.SetChan[i] = bb.readShort();
		}
		else
		{
			SetCount = loader.SetCount;
			for(int i = loader.SetCount; i < MAX_SET; i++) {
				if (SetList[i] == null)
					SetList[i] = new SetStruct();
				SetList[i].copy(loader.SetList[i]);
			}
			System.arraycopy(loader.SetChan, 0, SetChan, 0, MAX_SET);
		}
	}


	public static void BuildSet(int spr, int x, int y, int z, int sectnum, int ang, int channel) {
		int count = --SetCount;
		if (count < 0)
			return;

		if (spr == -1) {
			spr = engine.insertsprite((short) sectnum, (short) 120);
		} else {
			x = sprite[spr].x;
			y = sprite[spr].y;
			z = sector[sprite[spr].sectnum].floorz;
			ang = sprite[spr].ang;
			engine.changespritestat((short) spr, (short) 120);
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
		sprite[spr].clipdist = 110;
		sprite[spr].ang = (short) ang;
		sprite[spr].xrepeat = 87;
		sprite[spr].yrepeat = 96;
		sprite[spr].xvel = 0;
		sprite[spr].yvel = 0;
		sprite[spr].zvel = 0;
		sprite[spr].hitag = 0;
		sprite[spr].lotag = (short) (HeadRun() + 1);
		sprite[spr].extra = -1;

		if (SetList[count] == null)
			SetList[count] = new SetStruct();
		
		SetList[count].nState = 1;
		SetList[count].nHealth = 8000;
		SetList[count].nSprite = (short) spr;
		SetList[count].nSeq = 0;
		SetList[count].nTarget = -1;
		SetList[count].field_A = 90;
		SetList[count].field_C = 0;
		SetList[count].field_D = 0;
		SetChan[count] = (short) channel;
		sprite[spr].owner = (short) AddRunRec(sprite[spr].lotag - 1, 0x190000 | count);
		AddRunRec(NewRun, 0x190000 | count);
		nCreaturesLeft++;
		nCreaturesMax++;
	}

	public static int BuildSoul(int nSet) {
		SPRITE pSet = sprite[SetList[nSet].nSprite];
		int spr = engine.insertsprite(pSet.sectnum, (short) 0);
		if (spr < 0 || spr >= MAXSPRITES) {
			game.ThrowError("spr>=0 && spr<MAXSPRITES");
			return -1;
		}

		SPRITE pSprite = sprite[spr];
		pSprite.cstat = (short) 32768;
		pSprite.shade = -127;
		pSprite.xrepeat = 1;
		pSprite.yrepeat = 1;
		pSprite.pal = 0;
		pSprite.clipdist = 5;
		pSprite.xoffset = 0;
		pSprite.yoffset = 0;

		pSprite.picnum = GetSeqPicnum(48, 75, 0);
		pSprite.ang = (short) RandomSize(11);
		pSprite.xvel = 0;
		pSprite.yvel = 0;
		pSprite.zvel = (short) (-256 - RandomSize(10));
		pSprite.x = pSet.x;
		pSprite.y = pSet.y;
		pSprite.z = (RandomSize(8) << 8) + 0x2000 + (sector[pSprite.sectnum].ceilingz - GetSpriteHeight(spr));
		pSprite.hitag = (short) nSet;
		pSprite.lotag = (short) (HeadRun() + 1);
		pSprite.extra = 0;
		pSprite.owner = (short) AddRunRec(NewRun, 0x230000 | spr);

		return spr;
	}

	public static void FuncSet(int a1, int nDamage, int RunPtr) {
		short nSet = (short) (RunData[RunPtr].RunEvent & 0xFFFF);
		if (nSet < 0 || nSet >= MAX_SET) {
			game.ThrowError("set>=0 && set<MAX_SET");
			return;
		}

		SetStruct pSet = SetList[nSet];
		int nSprite = pSet.nSprite;
		SPRITE pSprite = sprite[nSprite];

		int nState = pSet.nState;
		int nTarget = pSet.nTarget;
		int v68 = 0;

		short nObject = (short) (a1 & 0xFFFF);

		lCheckTarget: switch (a1 & 0x7F0000) {
		case nEventProcess:
			Gravity(nSprite);

			int nSeq = ActionSeq_X_9[nState][0] + SeqOffsets[48];
			pSprite.picnum = (short) GetSeqPicnum2(nSeq, pSet.nSeq);

			MoveSequence(nSprite, nSeq, pSet.nSeq);
			if (nState == 3 && (pSet.field_D) != 0)
				++pSet.nSeq;

			if (++pSet.nSeq >= SeqSize[nSeq]) {
				pSet.nSeq = 0;
				v68 = 1;
			}

			int nFlags = FrameFlag[pSet.nSeq + SeqBase[nSeq]];
			if (nTarget > -1 && nState < 10 && (sprite[nTarget].cstat & 0x101) == 0) {
				pSet.nTarget = -1;
				pSet.nState = 0;
				pSet.nSeq = 0;
				nTarget = -1;
			}
			int hitMove = MoveCreature(nSprite);
			engine.pushmove(pSprite.x, pSprite.y, pSprite.z, pSprite.sectnum, 4 * pSprite.clipdist, 5120, -5120, 0);
			if (pSprite.zvel > 4000 && (hitMove & 0x20000) != 0)
				SetQuake(nSprite, 100);
			switch (nState) {
			case 0:
				if ((nSet & 0x1F) == (totalmoves & 0x1F)) {
					if (nTarget < 0)
						nTarget = FindPlayer(nSprite, 1000);
					if (nTarget >= 0) {
						pSet.nState = 3;
						pSet.nSeq = 0;
						pSet.nTarget = (short) nTarget;
						pSprite.xvel = (short) (sintable[(pSprite.ang + 512) & 0x7FF] >> 1);
						pSprite.yvel = (short) (sintable[pSprite.ang & 0x7FF] >> 1);
					}
				}
				return;
			case 1:
				if (FindPlayer(nSprite, 1000) >= 0) {
					if (--pSet.field_A <= 0) {
						pSet.nState = 2;
						pSet.nSeq = 0;
					}
				}
				return;
			case 2:
				if (v68 != 0) {
					pSet.nState = 7;
					pSet.field_C = 0;
					pSet.nSeq = 0;
					pSprite.xvel = 0;
					pSprite.yvel = 0;
					pSet.nTarget = (short) FindPlayer(nSprite, 1000);
				}
				return;
			case 6:
				if ((nFlags & 0x80) != 0) {
					int nBullet = BuildBullet(nSprite, 11, 0, 0, -1, pSprite.ang, nTarget + 10000, 1);
					SetBulletEnemy((short)((nBullet >> 16) & 0xFFFF), nTarget);
					if (--pSet.field_E <= 0 || RandomBit() == 0) {
						pSet.nState = 0;
						pSet.nSeq = 0;
					}
				}
				return;
			case 3:
				if (nTarget == -1) {
					pSet.nState = 0;
					pSet.nSeq = 0;
					return;
				}

				if ((nFlags & 0x10) != 0 && (hitMove & 0x20000) != 0)
					SetQuake(nSprite, 100);
				int nGoalAngle = PlotCourseToSprite(nSprite, nTarget);
				
				boolean v34 = (totalmoves & 0x1F) == (nSet & 0x1F);
				if (v34) { 
					int v33 = RandomSize(3);
					v34 = v33 == 0 ||  v33 == 2;

					if (v34)
						pSet.field_C = 0;
					else if (v33 == 1)
						break;
					else
						pSet.field_D = (short) (nGoalAngle > 100 ? 1 : 0);
				}

				if(!v34)
				{
					int nAngle = pSprite.ang & 0xFFF8;
					pSprite.xvel = (short) (sintable[(nAngle + 512) & 0x7FF] >> 1);
					pSprite.yvel = (short) (sintable[nAngle] >> 1);
					if (pSet.field_D != 0) {
						pSprite.xvel *= 2;
						pSprite.yvel *= 2;
					}
	
					switch (hitMove & 0xC000) {
					default:
						break lCheckTarget;
					case 0x8000:
						int nNextSector = wall[hitMove & 0x3FFF].nextsector;
						if (nNextSector < 0 || (pSprite.z - sector[nNextSector].floorz >= 55000)
								|| pSprite.z <= sector[nNextSector].ceilingz) {
							pSprite.ang = (short) ((pSprite.ang + 256) & 0x7FF);
							pSprite.xvel = (short) (sintable[(pSprite.ang + 512) & 0x7FF] >> 1);
							pSprite.yvel = (short) (sintable[pSprite.ang & 0x7FF] >> 1);
							break lCheckTarget;
						}
						break;
					case 0xC000:
						if ((hitMove & 0x3FFF) == nTarget) {
							if (AngleDiff(pSprite.ang,
									engine.getangle(sprite[nTarget].x - pSprite.x, sprite[nTarget].y - pSprite.y)) < 64) {
								pSet.nState = 4;
								pSet.nSeq = 0;
							}
							break lCheckTarget;
						}
						break;
					}
					pSet.field_C = 1;
				}

				pSprite.xvel = 0;
				pSprite.yvel = 0;
				pSet.nState = 7;
				pSet.nSeq = 0;
				return;
			case 5:
				if (v68 != 0) {
					pSet.nState = 0;
					pSet.field_A = 15;
				}
				return;
			case 4:
				if (nTarget == -1) {
					pSet.nState = 0;
					pSet.field_A = 50;
				} else if (PlotCourseToSprite(nSprite, nTarget) >= 768) {
					pSet.nState = 3;
				} else if ((nFlags & 0x80) != 0) {
					DamageEnemy(nTarget, nSprite, 5);
				}
				break lCheckTarget;
			case 7:
				if (v68 != 0) {
					if (pSet.field_C != 0)
						pSprite.zvel = -10000;
					else
						pSprite.zvel = (short) -PlotCourseToSprite(nSprite, nTarget);

					pSet.nState = 8;
					pSet.nSeq = 0;
					pSprite.xvel = sintable[(pSprite.ang + 512) & 0x7FF];
					pSprite.yvel = sintable[pSprite.ang & 0x7FF];
				}
				return;
			case 8:
				if (v68 != 0)
					pSet.nSeq = (short) (SeqSize[nSeq] - 1);
				if ((hitMove & 0x20000) != 0) {
					SetQuake(nSprite, 200);
					pSet.nState = 9;
					pSet.nSeq = 0;
				}
				return;
			case 9:
				pSprite.xvel >>= 1;
				pSprite.yvel >>= 1;
				if (v68 != 0) {
					pSprite.xvel = 0;
					pSprite.yvel = 0;
					break;
				}
				return;
			case 10:
				if ((nFlags & 0x80) != 0) {
					pSprite.z -= GetSpriteHeight(nSprite);

					BuildCreatureChunk(nSprite, GetSeqPicnum(48, 76, 0));
					pSprite.z += GetSpriteHeight(nSprite);
				}
				if (v68 != 0) {
					pSet.nState = 11;
					pSet.nSeq = 0;
					ChangeChannel(SetChan[nSet], 1);
					for (int i = 0; i < 20; i++)
						BuildSoul(nSet);
				}
				return;
			case 11:
				pSprite.cstat &= ~0x101;
				return;
			}

			PlotCourseToSprite(nSprite, nTarget);
			pSet.nState = 6;
			pSet.nSeq = 0;
			pSet.field_E = 5;
			pSprite.xvel = 0;
			pSprite.yvel = 0;

			return;
		case nEventView:
			PlotSequence(nObject, ActionSeq_X_9[nState][0] + SeqOffsets[48], pSet.nSeq, ActionSeq_X_9[nState][1]);
			return;
		case nEventRadialDamage:
			if (nState == 5)
				nDamage = CheckRadialDamage(nSprite);
		case nEventDamage:
			if (nDamage != 0) {
				if (pSet.nHealth > 0) {
					if (nState != 1)
						pSet.nHealth -= nDamage;
					if (pSet.nHealth <= 0) {
						pSprite.xvel = 0;
						pSprite.yvel = 0;
						pSprite.zvel = 0;
						pSprite.cstat &= ~0x101;
						pSet.nHealth = 0;
						nCreaturesLeft--;
						if (pSet.nState < 10) {
							pSet.nState = 10;
							pSet.nSeq = 0;
						}
					} else {
						if (nState == 1) {
							pSet.nState = 2;
							pSet.nSeq = 0;
						}
					}
				}
			}
			return;
		}

		if (nState > 0 && nTarget != -1 && (sprite[nTarget].cstat & 0x101) == 0) {
			pSet.nState = 0;
			pSet.nSeq = 0;
			pSet.field_A = 100;
			pSet.nTarget = -1;
			pSprite.xvel = 0;
			pSprite.yvel = 0;
		}
	}

	public static void FuncSoul(int a1, int nDamage, int RunPtr) {
		short nSprite = (short) (RunData[RunPtr].RunEvent & 0xFFFF);
		SPRITE pSprite = sprite[nSprite];

		switch (a1 & 0x7F0000) {
		case nEventProcess:
			MoveSequence(nSprite, SeqOffsets[48] + 75, 0);
			if (pSprite.xrepeat < 32) {
				pSprite.xrepeat++;
				pSprite.yrepeat++;
			}
			pSprite.extra += (nSprite & 0xF) + 5;
			pSprite.extra &= 0x7FF;

			int vel = sintable[(pSprite.extra + 512) & 0x7FF] >> 7;
			if ((engine.movesprite((short) nSprite, sintable[(pSprite.ang + 512) & 0x7FF] * vel,
					vel * sintable[pSprite.ang], pSprite.zvel, 5120, 0, 0) & 0x10000) != 0) {
				int nSet = SetList[pSprite.hitag].nSprite;
				pSprite.cstat = 0;
				pSprite.yrepeat = 1;
				pSprite.xrepeat = 1;
				pSprite.x = sprite[nSet].x;
				pSprite.y = sprite[nSet].y;
				pSprite.z = sprite[nSet].z - (GetSpriteHeight(nSet) >> 1);
				engine.mychangespritesect(nSprite, sprite[nSet].sectnum);
			}
			return;
		}
	}
}
