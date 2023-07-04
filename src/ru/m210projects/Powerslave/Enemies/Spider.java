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

import static ru.m210projects.Build.Engine.MAXSPRITES;
import static ru.m210projects.Build.Engine.sector;
import static ru.m210projects.Build.Engine.sintable;
import static ru.m210projects.Build.Engine.sprite;
import static ru.m210projects.Build.Engine.zr_ceilhit;
import static ru.m210projects.Powerslave.Enemies.Enemy.FindPlayer;
import static ru.m210projects.Powerslave.Enemies.Enemy.PlotCourseToSprite;
import static ru.m210projects.Powerslave.Globals.*;
import static ru.m210projects.Powerslave.Main.engine;
import static ru.m210projects.Powerslave.Main.game;
import static ru.m210projects.Powerslave.Random.RandomBit;
import static ru.m210projects.Powerslave.Random.RandomSize;
import static ru.m210projects.Powerslave.RunList.AddRunRec;
import static ru.m210projects.Powerslave.RunList.DoSubRunRec;
import static ru.m210projects.Powerslave.RunList.FreeRun;
import static ru.m210projects.Powerslave.RunList.HeadRun;
import static ru.m210projects.Powerslave.RunList.NewRun;
import static ru.m210projects.Powerslave.RunList.RunData;
import static ru.m210projects.Powerslave.RunList.SubRunRec;
import static ru.m210projects.Powerslave.Seq.FrameFlag;
import static ru.m210projects.Powerslave.Seq.GetSeqPicnum;
import static ru.m210projects.Powerslave.Seq.GetSeqPicnum2;
import static ru.m210projects.Powerslave.Seq.MoveSequence;
import static ru.m210projects.Powerslave.Seq.PlotSequence;
import static ru.m210projects.Powerslave.Seq.SeqBase;
import static ru.m210projects.Powerslave.Seq.SeqOffsets;
import static ru.m210projects.Powerslave.Seq.SeqSize;
import static ru.m210projects.Powerslave.Sound.D3PlayFX;
import static ru.m210projects.Powerslave.Sound.StaticSound;
import static ru.m210projects.Powerslave.Sprites.AngleDiff;
import static ru.m210projects.Powerslave.Sprites.BuildCreatureChunk;
import static ru.m210projects.Powerslave.Sprites.CheckRadialDamage;
import static ru.m210projects.Powerslave.Sprites.DamageEnemy;
import static ru.m210projects.Powerslave.Sprites.GetSpriteHeight;
import static ru.m210projects.Powerslave.Sprites.Gravity;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ru.m210projects.Build.FileHandle.Resource;
import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Powerslave.Enemies.Enemy.EnemyStruct;
import ru.m210projects.Powerslave.Type.SafeLoader;

public class Spider {

	public static final int MAX_SPIDERS = 100;

	private static final short[][] ActionSeq_X_2 = new short[][] { { 16, 0 }, { 8, 0 }, { 32, 0 }, { 24, 0 }, { 0, 0 },
			{ 40, 1 }, { 41, 1 }, };

	public static EnemyStruct SpiderList[] = new EnemyStruct[MAX_SPIDERS];

	private static int SpiderCount;
//	private static int SpiderSprite;

	public static void InitSpider() {
		SpiderCount = 0;
//		SpiderSprite = 1;
	}

	public static ByteBuffer saveSpider()
	{
		ByteBuffer bb = ByteBuffer.allocate(SpiderCount * EnemyStruct.size + 2);
		bb.order(ByteOrder.LITTLE_ENDIAN);

		bb.putShort((short)SpiderCount);
		for(int i = 0; i < SpiderCount; i++)
			SpiderList[i].save(bb);

		return bb;
	}

	public static void loadSpider(SafeLoader loader, Resource bb)
	{
		if(bb != null)
		{
			loader.SpiderCount = bb.readShort();
			for(int i = 0; i < loader.SpiderCount; i++) {
				if(loader.SpiderList[i] == null)
					loader.SpiderList[i] = new EnemyStruct();
				loader.SpiderList[i].load(bb);
			}
		}
		else
		{
			SpiderCount = loader.SpiderCount;
			for(int i = 0; i < loader.SpiderCount; i++) {
				if (SpiderList[i] == null)
					SpiderList[i] = new EnemyStruct();
				SpiderList[i].copy(loader.SpiderList[i]);
			}
		}
	}

	public static int BuildSpider(int spr, int x, int y, int z, int sectnum, int ang) {
		int count = SpiderCount++;

		if (count >= MAX_SPIDERS)
			return -1;

		if (spr == -1) {
			spr = engine.insertsprite((short) sectnum, (short) 99);

		} else {
			y = sprite[spr].y;
			z = sprite[spr].z;
			x = sprite[spr].x;
			engine.changespritestat((short) spr, (short) 99);
			ang = sprite[spr].ang;
		}

		if (spr < 0 || spr >= MAXSPRITES) {
			game.ThrowError("spr>=0 && spr<MAXSPRITES");
			return -1;
		}

		sprite[spr].x = x;
		sprite[spr].y = y;
		sprite[spr].z = z;
		sprite[spr].cstat = 257;
		sprite[spr].shade = -12;
		sprite[spr].clipdist = 15;
		sprite[spr].xvel = 0;
		sprite[spr].yvel = 0;

		sprite[spr].zvel = 0;
		sprite[spr].xrepeat = 40;
		sprite[spr].yrepeat = 40;
		sprite[spr].pal = sector[sprite[spr].sectnum].ceilingpal;
		sprite[spr].xoffset = 0;
		sprite[spr].yoffset = 0;
		sprite[spr].ang = (short) ang;
		sprite[spr].picnum = 1;

		sprite[spr].hitag = 0;
		sprite[spr].lotag = (short) (HeadRun() + 1);
		sprite[spr].extra = -1;

		if (SpiderList[count] == null)
			SpiderList[count] = new EnemyStruct();

		SpiderList[count].nHealth = 160;
		SpiderList[count].nSeq = 0;
		SpiderList[count].nState = 0;
		SpiderList[count].nSprite = (short) spr;
		SpiderList[count].nTarget = -1;

		sprite[spr].owner = (short) AddRunRec(sprite[spr].lotag - 1, 0xC0000 | count);
		SpiderList[count].nFunc = (short) AddRunRec(NewRun, 0xC0000 | count);
		nCreaturesLeft++;
		nCreaturesMax++;

		return spr;
	}

	public static void FuncSpider(int a1, int nDamage, int RunPtr) {
		short nSpider = (short) (RunData[RunPtr].RunEvent & 0xFFFF);
		if (nSpider < 0 || nSpider >= MAX_SPIDERS) {
			game.ThrowError("nSpider>=0 && nSpider<MAX_SPIDERS");
			return;
		}

		EnemyStruct pSpider = SpiderList[nSpider];
		int nSprite = pSpider.nSprite;
		SPRITE pSprite = sprite[nSprite];

		int nState = pSpider.nState;
		int nTarget = pSpider.nTarget;

		short plr = (short) (a1 & 0xFFFF);
		switch (a1 & 0x7F0000) {
		case nEventProcess:
			int vel = 6;
			if (pSpider.nHealth != 0) {
				if ((pSprite.cstat & 8) != 0) {
					game.pInt.setsprinterpolate(nSprite, pSprite);
					pSprite.z = GetSpriteHeight(nSprite) + sector[pSprite.sectnum].ceilingz;
				} else
					Gravity(nSprite);
			}

			int nSeq = ActionSeq_X_2[nState][0] + SeqOffsets[16];
			pSprite.picnum = (short) GetSeqPicnum2(nSeq, pSpider.nSeq);
			MoveSequence(nSprite, nSeq, pSpider.nSeq);
			if (++pSpider.nSeq >= SeqSize[nSeq]) {
				pSpider.nSeq = 0;
			}
			int nFlags = FrameFlag[pSpider.nSeq + SeqBase[nSeq]];

			if (nTarget <= -1 || (sprite[pSpider.nTarget].cstat & 0x101) != 0) {
				switch (nState) {
				case 0:
					if ((nSpider & 0x1F) != (totalmoves & 0x1F))
						break;
					if (nTarget < 0)
						nTarget = FindPlayer(nSprite, 100);
					if (nTarget < 0)
						break;

					pSpider.nState = 1;
					pSpider.nSeq = 0;
					pSpider.nTarget = (short) nTarget;
					pSprite.xvel = sintable[(pSprite.ang + 512) & 0x7FF];
					pSprite.yvel = sintable[pSprite.ang & 0x7FF];
					return;
				case 1:
					if (nTarget >= 0)
						vel = 7;
				case 3:
				case 4:
					if (nState == 4 && pSpider.nSeq == 0) {
						pSpider.nSeq = 0;
						pSpider.nState = 1;
					}
					if ((pSprite.cstat & 8) != 0) {
						pSprite.zvel = 0;
						pSprite.z = 32 * engine.getTile(pSprite.picnum).getHeight() + sector[pSprite.sectnum].ceilingz;
						if ((sector[pSprite.sectnum].ceilingstat & 1) != 0) {
							pSprite.cstat ^= 8;
							pSprite.zvel = 1;
							pSpider.nState = 3;
							pSpider.nSeq = 0;
						}
					}
					if ((totalmoves & 0x1F) == (nSpider & 0x1F)) {
						PlotCourseToSprite(nSprite, nTarget);
						if (RandomSize(3) != 0) {
							pSprite.xvel = sintable[(pSprite.ang + 512) & 0x7FF];
							pSprite.yvel = sintable[pSprite.ang & 0x7FF];
						} else {
							pSprite.xvel = pSprite.yvel = 0;
						}

						if (nState == 1 && RandomBit() != 0) {
							if ((pSprite.cstat & 8) != 0) {
								pSprite.cstat ^= 8;
								pSprite.zvel = 1;
								pSprite.z = GetSpriteHeight(nSprite) + sector[pSprite.sectnum].ceilingz;
							} else {
								pSprite.zvel = -5120;
							}

							pSpider.nState = 3;
							pSpider.nSeq = 0;
							if (RandomSize(3) == 0)
								D3PlayFX(StaticSound[29], nSprite);
						}
					}
					break;
				case 2:
					if (nTarget == -1) {
						pSpider.nSeq = 0;
						pSpider.nState = 0;
						pSprite.xvel = 0;
						pSprite.yvel = 0;
						break;
					}
					if ((nFlags & 0x80) != 0) {
						DamageEnemy(nTarget, nSprite, 3);
						D3PlayFX(StaticSound[38], nSprite);
					}
					if (PlotCourseToSprite(nSprite, nTarget) < 1024)
						return;

					pSpider.nSeq = 0;
					pSpider.nState = 1;
					break;
				case 5:
					if (pSpider.nSeq == 0) {
						DoSubRunRec(pSprite.owner);
						FreeRun(pSprite.lotag - 1);
						SubRunRec(pSpider.nFunc);
						pSprite.cstat = (short) 32768;
						engine.mydeletesprite((short) nSprite);
					}
					return;
				default:
					return;
				}
			} else {
				pSpider.nTarget = -1;
				pSpider.nState = 0;
				pSprite.yvel = 0;
				pSprite.xvel = 0;
				pSpider.nSeq = 0;
			}

			short osect = pSprite.sectnum;
			int oz = pSprite.z;
			int hitMove = engine.movesprite((short) nSprite, pSprite.xvel << vel, pSprite.yvel << vel, pSprite.zvel,
					1280, -1280, 0);

			short sect = pSprite.sectnum;
			if(!isOriginal() && osect != sect && (SectFlag[osect] & 0x2000) == 0 && (SectFlag[sect] & 0x2000) != 0)
			{
				engine.changespritesect((short) nSprite, osect);

				pSprite.z = oz - 2048;
				pSpider.nState = 1;
				pSprite.zvel = -512;
				return;
			}

			if (hitMove != 0) {
				if ((hitMove & 0x10000) != 0 && pSprite.zvel < 0 && (zr_ceilhit & 0xC000) != 0xC000
						&& (sector[pSprite.sectnum].ceilingstat & 1) == 0) {
					pSprite.cstat |= 8;

					pSprite.z = GetSpriteHeight(nSprite) + sector[pSprite.sectnum].ceilingz;
					pSprite.zvel = 0;
					pSpider.nState = 1;
					pSpider.nSeq = 0;
					return;
				}

				switch (hitMove & 0xC000) {
				case 0x8000:
					pSprite.ang = (short) ((pSprite.ang + 256) & 0x7EF);
					pSprite.xvel = sintable[(pSprite.ang + 512) & 0x7FF];
					pSprite.yvel = sintable[pSprite.ang & 0x7FF];
					return;
				case 0xC000:
					if ((hitMove & 0x3FFF) == nTarget) {
						int v34 = engine.getangle(sprite[nTarget].x - pSprite.x, sprite[nTarget].y - pSprite.y);
						if (AngleDiff(pSprite.ang, v34) < 64) {
							pSpider.nState = 2;
							pSpider.nSeq = 0;
						}
					}
					return;
				}
				if (nState == 3) {
					pSpider.nState = 1;
					pSpider.nSeq = 0;
				}
			}

			return;
		case nEventView:
			PlotSequence(plr, ActionSeq_X_2[nState][0] + SeqOffsets[16], pSpider.nSeq,
					ActionSeq_X_2[nState][1]);
			return;
		case nEventRadialDamage:
			if (pSpider.nHealth <= 0)
				return;

			nDamage = CheckRadialDamage(nSprite);
		case nEventDamage:
			if (nDamage != 0) {
				if (pSpider.nHealth > 0) {
					pSpider.nHealth -= nDamage;
					if (pSpider.nHealth > 0) {
						if (plr != -1 && sprite[plr].statnum == 100)
							pSpider.nTarget = plr;
						pSpider.nSeq = 0;
						pSpider.nState = 4;
					} else {
						pSpider.nSeq = 0;
						pSpider.nHealth = 0;
						pSpider.nState = 5;
						pSprite.cstat &= ~0x101;
						for (int i = 0, seq = 41; i < 7; i++, seq++)
							BuildCreatureChunk(nSprite, GetSeqPicnum(16, seq, 0));

						nCreaturesLeft--;
					}
				}
			}
			return;
		}
	}
}
