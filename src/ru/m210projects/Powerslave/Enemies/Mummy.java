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
import static ru.m210projects.Build.Gameutils.*;
import static ru.m210projects.Powerslave.Globals.*;
import static ru.m210projects.Powerslave.Main.*;
import static ru.m210projects.Powerslave.Random.*;
import static ru.m210projects.Powerslave.Enemies.Enemy.*;
import static ru.m210projects.Powerslave.RunList.*;
import static ru.m210projects.Powerslave.Seq.*;
import static ru.m210projects.Powerslave.Object.*;
import static ru.m210projects.Powerslave.Sound.*;
import static ru.m210projects.Powerslave.Sprites.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static ru.m210projects.Powerslave.Bullet.*;

import ru.m210projects.Build.FileHandle.Resource;
import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Powerslave.Enemies.Enemy.EnemyStruct;
import ru.m210projects.Powerslave.Type.SafeLoader;

public class Mummy {

	public static final int MAX_MUMMIES = 150;
	
	private static final short[][] ActionSeq_X_4 = new short[][] { { 8, 0 }, { 0, 0 }, { 16, 0 }, { 24, 0 }, { 32, 1 },
			{ 40, 1 }, { 48, 1 }, { 50, 0 } };

	private static int MummyCount;
	private static EnemyStruct[] MummyList = new EnemyStruct[MAX_MUMMIES];

	public static void InitMummy() {
		MummyCount = 0;
	}
	
	public static ByteBuffer saveMummy()
	{
		ByteBuffer bb = ByteBuffer.allocate(MummyCount * EnemyStruct.size + 2);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		
		bb.putShort((short)MummyCount);	
		for(int i = 0; i < MummyCount; i++)
			MummyList[i].save(bb);
		
		return bb;
	}
	
	public static void loadMummy(SafeLoader loader, Resource bb)
	{
		if(bb != null) {
			loader.MummyCount = bb.readShort();
			for(int i = 0; i < loader.MummyCount; i++) {
				if(loader.MummyList[i] == null)
					loader.MummyList[i] = new EnemyStruct();
				loader.MummyList[i].load(bb);
			}
		}
		else
		{
			MummyCount = loader.MummyCount;
			for(int i = 0; i < loader.MummyCount; i++) {
				if (MummyList[i] == null)
					MummyList[i] = new EnemyStruct();
				MummyList[i].copy(loader.MummyList[i]);
			}
		}
	}

	public static void BuildMummy(int spr, int x, int y, int z, int sectnum, int ang) {
		int count = MummyCount++;

		if (count >= MAX_MUMMIES)
			return;

		if (spr == -1) {
			spr = engine.insertsprite((short) sectnum, (short) 102);

		} else {
			y = sprite[spr].y;
			z = sprite[spr].z;
			x = sprite[spr].x;
			engine.changespritestat((short) spr, (short) 102);
			ang = sprite[spr].ang;
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
		sprite[spr].clipdist = 32;
		sprite[spr].xvel = 0;
		sprite[spr].yvel = 0;
		sprite[spr].zvel = 0;
		sprite[spr].xrepeat = 42;
		sprite[spr].yrepeat = 42;
		sprite[spr].pal = sector[sprite[spr].sectnum].ceilingpal;
		sprite[spr].xoffset = 0;
		sprite[spr].yoffset = 0;
		sprite[spr].ang = (short) ang;
		sprite[spr].picnum = 1;

		sprite[spr].hitag = 0;
		sprite[spr].lotag = (short) (HeadRun() + 1);
		sprite[spr].extra = -1;
		sprite[spr].owner = (short) AddRunRec(sprite[spr].lotag - 1, 0xE0000 | count);

		if (MummyList[count] == null)
			MummyList[count] = new EnemyStruct();
		
		MummyList[count].nHealth = 640;
		MummyList[count].nSeq = 0;
		MummyList[count].nState = 0;
		MummyList[count].nSprite = (short) spr;
		MummyList[count].nTarget = -1;
		MummyList[count].field_A = (short) count;
		MummyList[count].field_C = 0;
		MummyList[count].nFunc = (short) AddRunRec(NewRun, 0xE0000 | count);
		nCreaturesLeft++;
		nCreaturesMax++;
	}

	public static void FuncMummy(int a1, int a2, int RunPtr) {
		short nMummy = (short) (RunData[RunPtr].RunEvent & 0xFFFF);
		if (nMummy < 0 || nMummy >= MAX_MUMMIES) {
			game.ThrowError("Mummy>=0 && Mummy<MAX_MUMMIES");
			return;
		}

		int nSprite = MummyList[nMummy].nSprite;
		SPRITE pSprite = sprite[nSprite];

		int damage = a2;
		short plr = (short) (a1 & 0xFFFF);

		int nTarget = UpdateEnemy(MummyList[nMummy].nTarget);
		int nState = MummyList[nMummy].nState;

		switch (a1 & 0x7F0000) {
		case nEventProcess:
			Gravity(nSprite);

			int v10 = ActionSeq_X_4[nState][0] + SeqOffsets[10];
			short v13 = MummyList[nMummy].nSeq;
			pSprite.picnum = (short) GetSeqPicnum2(v10, v13);

			int flags = FrameFlag[v13 + SeqBase[v10]];
			MoveSequence(nSprite, v10, v13);
			MummyList[nMummy].nSeq++;
			int v18 = 0;
			if (MummyList[nMummy].nSeq >= SeqSize[v10]) {
				MummyList[nMummy].nSeq = 0;
				v18 = 1;
			}

			if (nTarget != -1 && nState < 4 && sprite[nTarget].cstat == 0 && nState != 0) {
				MummyList[nMummy].nState = 0;
				MummyList[nMummy].nSeq = 0;
				pSprite.yvel = 0;
				pSprite.xvel = 0;
			}
			int v17 = MoveCreatureWithCaution(nSprite);
			switch (MummyList[nMummy].nState) {
			case 0:
				if ((MummyList[nMummy].field_A & 0x1F) == (totalmoves & 0x1F)) {
					pSprite.cstat = 257;
					if (nTarget < 0 && (nTarget = FindPlayer(nSprite, 100)) >= 0) {
						D3PlayFX(StaticSound[7], nSprite);
						MummyList[nMummy].nSeq = 0;
						MummyList[nMummy].nTarget = (short) nTarget;
						MummyList[nMummy].nState = 1;
						pSprite.xvel = (short) (sintable[(pSprite.ang + 512) & 0x7FF] >> 2);
						pSprite.yvel = (short) (sintable[pSprite.ang] >> 2);
						MummyList[nMummy].field_C = 90;
					}
				}
				return;
			case 1:
				int v20 = MummyList[nMummy].field_C;
				if (v20 > 0)
					MummyList[nMummy].field_C--;
				if ((totalmoves & 0x1F) == (MummyList[nMummy].field_A & 0x1F)) {
					pSprite.cstat = 257;
					PlotCourseToSprite(nSprite, nTarget);
					if (MummyList[nMummy].nState == 1) {
						if (RandomBit() != 0) {
							SPRITE pTarget = (nTarget != -1) ? sprite[nTarget] : null;
							if (pTarget != null && engine.cansee(pSprite.x, pSprite.y, pSprite.z - GetSpriteHeight(nSprite),
									pSprite.sectnum, pTarget.x, pTarget.y, pTarget.z - GetSpriteHeight(nTarget), pTarget.sectnum)) {
								MummyList[nMummy].nState = 3;
								MummyList[nMummy].nSeq = 0;
								pSprite.yvel = 0;
								pSprite.xvel = 0;
								return;
							}
						}
					}
				}
				if (MummyList[nMummy].nSeq == 0) {
					pSprite.xvel = (short) (sintable[(pSprite.ang + 512) & 0x7FF] >> 1);
					pSprite.yvel = (short) (sintable[pSprite.ang & 0x7FF] >> 1);
				}

				if (pSprite.xvel != 0 && pSprite.yvel != 0) {
					if (pSprite.xvel > 0)
						pSprite.xvel = (short) BClipLow(pSprite.xvel - 1024, 0);
					else
						pSprite.xvel = (short) BClipHigh(pSprite.xvel + 1024, 0);

					if (pSprite.yvel > 0)
						pSprite.yvel = (short) BClipLow(pSprite.yvel - 1024, 0);
					else
						pSprite.yvel = (short) BClipHigh(pSprite.yvel + 1024, 0);
				}
				if (v17 != 0) {
					switch (v17 & 0xC000) {
					case 0x8000:
						pSprite.ang = (short) ((pSprite.ang + (RandomWord() & 0x300) + 0x400) & 0x7FF);
						pSprite.xvel = (short) (sintable[(pSprite.ang + 512) & 0x7FF] >> 2);
						pSprite.yvel = (short) (sintable[pSprite.ang] >> 2);
						break;
					case 0xC000:
						if ((v17 & 0x3FFF) == nTarget) {
							if (AngleDiff(pSprite.ang, engine.getangle(sprite[nTarget].x - pSprite.x,
									sprite[nTarget].y - pSprite.y)) < 64) {
								MummyList[nMummy].nState = 2;
								MummyList[nMummy].nSeq = 0;
								pSprite.yvel = 0;
								pSprite.xvel = 0;
							}
						}

						break;
					}
				}
				return;
			case 2:
				if (nTarget == -1) {
					MummyList[nMummy].nState = 0;
				} else {
					if (PlotCourseToSprite(nSprite, nTarget) < 1024) {
						if ((flags & 0x80) != 0)
							DamageEnemy(nTarget, nSprite, 5);
						return;
					}
					MummyList[nMummy].nState = 1;
				}
				MummyList[nMummy].nSeq = 0;
				return;
			case 3:
				if (v18 != 0) {
					MummyList[nMummy].nSeq = 0;
					MummyList[nMummy].nState = 0;
					MummyList[nMummy].field_C = 100;
					MummyList[nMummy].nTarget = -1;
				} else if ((flags & 0x80) != 0) {
					SetQuake(nSprite, 100);
					int nBullet = BuildBullet(nSprite, 9, 0, 0, -15360, pSprite.ang, nTarget + 10000, 1);
					CheckMummyRevive(nMummy);
					if (nBullet > -1 && RandomSize(3) == 0) {
						SetBulletEnemy((short)((nBullet >> 16) & 0xFFFF), nTarget);
						sprite[nBullet & 0xFFFF].pal = 5;
					}
				}
				return;
			case 4:
				if (v18 != 0) {
					MummyList[nMummy].nSeq = 0;
					MummyList[nMummy].nState = 5;
				}
				return;
			case 5:
				MummyList[nMummy].nSeq = 0;
				return;
			case 6:
				if (v18 != 0) {
					MummyList[nMummy].nState = 0;
					pSprite.cstat = 257;
					MummyList[nMummy].nHealth = 300;
					MummyList[nMummy].nTarget = -1;
					nCreaturesLeft++;
				}
				return;
			case 7:
				if ((v17 & 0x20000) != 0) {
					pSprite.xvel >>= 1;
					pSprite.yvel >>= 1;
				}
				
				if (v18 != 0) {
					pSprite.xvel = pSprite.yvel = 0;
					MummyList[nMummy].nState = 0;
					MummyList[nMummy].nSeq = 0;
					pSprite.cstat = 257;
					MummyList[nMummy].nTarget = -1;
				}
				return;
			}
			return;
		case nEventView:
			PlotSequence(plr, ActionSeq_X_4[nState][0] + SeqOffsets[10], MummyList[nMummy].nSeq,
					ActionSeq_X_4[nState][1]);
			return;
		case nEventRadialDamage:
			if (MummyList[nMummy].nHealth <= 0)
				return;

			damage = CheckRadialDamage(nSprite);
		case nEventDamage:
			if (damage != 0) {
				if (MummyList[nMummy].nHealth > 0) {
					MummyList[nMummy].nHealth -= damage;
					if (MummyList[nMummy].nHealth > 0) {
						if (RandomSize(2) == 0) {
							MummyList[nMummy].nState = 7;
							MummyList[nMummy].nSeq = 0;
							pSprite.xvel = 0;
							pSprite.yvel = 0;
						}
					} else {
						MummyList[nMummy].nHealth = 0;
						pSprite.cstat &= 0xFEFE;
						DropMagic(nSprite);
						MummyList[nMummy].nSeq = 0;
						MummyList[nMummy].nState = 4;
						pSprite.zvel = 0;
						pSprite.yvel = 0;
						pSprite.xvel = 0;
						pSprite.z = sector[pSprite.sectnum].floorz;
						nCreaturesLeft--;
					}
				}
			}
			return;
		}
	}

	private static void CheckMummyRevive(int nMummy) {
		SPRITE pSprite = sprite[MummyList[nMummy].nSprite];
		for (int i = 0; i < MummyCount; i++) {
			EnemyStruct pOther = MummyList[i];
			if (i != nMummy) {
				SPRITE v3 = sprite[pOther.nSprite];
				if (v3.statnum == 102 && pOther.nState == 5) {
					if (klabs(v3.x - pSprite.x) >> 8 <= 20 && klabs(v3.y - pSprite.y) >> 8 <= 20) {
						if (engine.cansee(pSprite.x, pSprite.y, pSprite.z - 0x2000, pSprite.sectnum, v3.x, v3.y,
								v3.z - 0x2000, v3.sectnum)) {
							v3.cstat = 0;
							MummyList[i].nState = 6;
							MummyList[i].nSeq = 0;
						}
					}
				}
			}
		}
	}
}
