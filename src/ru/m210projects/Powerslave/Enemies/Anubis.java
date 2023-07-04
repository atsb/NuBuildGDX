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
import static ru.m210projects.Powerslave.Sprites.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static ru.m210projects.Powerslave.Bullet.*;

import ru.m210projects.Build.FileHandle.Resource;
import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Powerslave.Type.SafeLoader;

public class Anubis {

	public static final int MAXANUBIS = 80;

	private static final short[][] ActionSeq_X_1 = new short[][] { { 0, 0 }, { 8, 0 }, { 16, 0 }, { 24, 0 }, { 32, 0 },
			{ -1, 0 }, { 46, 1 }, { 46, 1 }, { 47, 1 }, { 49, 1 }, { 49, 1 }, { 40, 1 }, { 42, 1 }, { 41, 1 },
			{ 43, 1 }, };
		
	private static int AnubisCount;
//	private static int AnubisSprite;
	private static int nAnubisDrum;

	private static EnemyStruct[] AnubisList = new EnemyStruct[MAXANUBIS];

	public static void InitAnubis() {
		AnubisCount = MAXANUBIS;
//		AnubisSprite = 1;
		nAnubisDrum = 1;
	}

	public static ByteBuffer saveAnubis() {
		ByteBuffer bb = ByteBuffer.allocate((MAXANUBIS - AnubisCount) * EnemyStruct.size + 3);
		bb.order(ByteOrder.LITTLE_ENDIAN);

		bb.putShort((short) AnubisCount);
		bb.put((byte) nAnubisDrum);

		for (int i = AnubisCount; i < MAXANUBIS; i++)
			AnubisList[i].save(bb);

		return bb;
	}

	public static void loadAnubis(SafeLoader loader, Resource bb) {
		if(bb != null) {
			loader.AnubisCount = bb.readShort();
			loader.nAnubisDrum = bb.readByte();
			for (int i = loader.AnubisCount; i < MAXANUBIS; i++) {
				if(loader.AnubisList[i] == null)
					loader.AnubisList[i] = new EnemyStruct();
				loader.AnubisList[i].load(bb);
			}
		}
		else
		{
			AnubisCount = loader.AnubisCount;
			nAnubisDrum = loader.nAnubisDrum;
			for (int i = loader.AnubisCount; i < MAXANUBIS; i++) {
				if(AnubisList[i] == null)
					AnubisList[i] = new EnemyStruct();
				AnubisList[i].copy(loader.AnubisList[i]);
			}
		}
	}

	public static void BuildAnubis(int spr, int x, int y, int z, int sectnum, int ang, int a7) {

		int count = --AnubisCount;

		if (count < 0)
			return;

		if (spr == -1) {
			spr = engine.insertsprite((short) sectnum, (short) 101);

		} else {
			x = sprite[spr].x;
			y = sprite[spr].y;
			z = sector[sprite[spr].sectnum].floorz;
			ang = sprite[spr].ang;
			engine.changespritestat((short) spr, (short) 101);
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
		sprite[spr].clipdist = 60;
		sprite[spr].ang = (short) ang;
		sprite[spr].xrepeat = 40;
		sprite[spr].yrepeat = 40;
		sprite[spr].xvel = 0;
		sprite[spr].yvel = 0;
		sprite[spr].zvel = 0;
		sprite[spr].hitag = 0;
		sprite[spr].lotag = (short) (HeadRun() + 1);
		sprite[spr].extra = -1;
		
		if (AnubisList[count] == null)
			AnubisList[count] = new EnemyStruct();

		if (a7 != 0) {
			AnubisList[count].nState = (short) (nAnubisDrum++ + 6);
			if (nAnubisDrum >= 5)
				nAnubisDrum = 0;
		} else {
			AnubisList[count].nState = 0;
		}

		AnubisList[count].nHealth = 540;
		AnubisList[count].nSeq = 0;
		AnubisList[count].nSprite = (short) spr;
		AnubisList[count].nTarget = -1;
		AnubisList[count].field_C = 0;
		sprite[spr].owner = (short) AddRunRec(sprite[spr].lotag - 1, 0x90000 | count);
		AddRunRec(NewRun, 0x90000 | count);
		nCreaturesLeft++;
		nCreaturesMax++;
	}

	public static void FuncAnubis(int a1, int a2, int RunPtr) {
		short nAnubis = (short) (RunData[RunPtr].RunEvent & 0xFFFF);
		if (nAnubis < 0 || nAnubis >= MAXANUBIS) {
			game.ThrowError("anubis>=0 && anubis<MAXANUBIS");
			return;
		}

		int nSprite = AnubisList[nAnubis].nSprite;
		SPRITE pSprite = sprite[nSprite];

		int damage = a2;

		int nState = AnubisList[nAnubis].nState;
		int v66 = 0;
		short plr = (short) (a1 & 0xFFFF);
		switch (a1 & 0x7F0000) {
		case nEventProcess:

			if (nState < 11)
				Gravity(nSprite);

			int nSeq = ActionSeq_X_1[nState][0] + SeqOffsets[17];
			pSprite.picnum = (short) GetSeqPicnum2(nSeq, AnubisList[nAnubis].nSeq);

			MoveSequence(nSprite, nSeq, AnubisList[nAnubis].nSeq);
			int nTarget = AnubisList[nAnubis].nTarget;
			if (++AnubisList[nAnubis].nSeq >= SeqSize[nSeq]) {
				AnubisList[nAnubis].nSeq = 0;
				v66 = 1;
			}
			
			int flags = FrameFlag[AnubisList[nAnubis].nSeq + SeqBase[nSeq]];

			int v60 = 0;
			if (AnubisList[nAnubis].nState > 0 && AnubisList[nAnubis].nState < 11)
				v60 = MoveCreatureWithCaution(nSprite);

			switch (nState) {
			default:
				return;
			case 0:
				if ((nAnubis & 0x1F) == (totalmoves & 0x1F)) {
					if (nTarget < 0)
						nTarget = FindPlayer(nSprite, 100);
					if (nTarget >= 0) {
						D3PlayFX(StaticSound[8], nSprite);
						AnubisList[nAnubis].nState = 1;
						AnubisList[nAnubis].nSeq = 0;
						AnubisList[nAnubis].nTarget = (short) nTarget;
						pSprite.xvel = (short) (sintable[(pSprite.ang + 512) & 0x7FF] >> 2);
						pSprite.yvel = (short) (sintable[pSprite.ang] >> 2);
					}
				}
				return;
			case 3:
				if (v66 == 0) {
					if ((flags & 0x80) != 0)
						BuildBullet(nSprite, 8, 0, 0, -1, pSprite.ang, nTarget + 10000, 1);
					return;
				}
				AnubisList[nAnubis].nState = 1;
				pSprite.xvel = (short) (sintable[(pSprite.ang + 512) & 0x7FF] >> 2);
				pSprite.yvel = (short) (sintable[pSprite.ang & 0x7FF] >> 2);
				AnubisList[nAnubis].nSeq = 0;
				return;
			case 4:
			case 5:
				pSprite.xvel = pSprite.yvel = 0;
				if (v66 != 0) {
					AnubisList[nAnubis].nState = 1;
					AnubisList[nAnubis].nSeq = 0;
				}
				return;
			case 6:
			case 7:
			case 8:
			case 9:
			case 10:
				if (v66 != 0) {
					AnubisList[nAnubis].nState = (short) (RandomSize(3) % 5 + 6);
					AnubisList[nAnubis].nSeq = 0;
				}
				return;
			case 11:
			case 12:
				if (v66 != 0) {
					AnubisList[nAnubis].nState = (short) (nState + 2);
					AnubisList[nAnubis].nSeq = 0;
					pSprite.xvel = pSprite.yvel = 0;
				}
				return;
			case 13:
			case 14:
				pSprite.cstat &= 0xFEFE;
				return;
			case 2:
				if (nTarget == -1) {
					AnubisList[nAnubis].nState = 0;
					AnubisList[nAnubis].field_C = 50;
				} else if (PlotCourseToSprite(nSprite, nTarget) >= 768)
					AnubisList[nAnubis].nState = 1;
				else if ((flags & 0x80) != 0)
					DamageEnemy(nTarget, nSprite, 7);
				break;
			case 1:
				if ((nAnubis & 0x1F) == (totalmoves & 0x1F)) {
					PlotCourseToSprite(nSprite, nTarget);
					int ang = pSprite.ang & 0xFFF8;
					pSprite.xvel = (short) (sintable[(ang + 512) & 0x7FF] >> 2);
					pSprite.yvel = (short) (sintable[ang & 0x7FF] >> 2);
				}
				switch (v60 & 0xC000) {
				case 0xC000:
					if ((v60 & 0x3FFF) == nTarget) {
						if (AngleDiff(pSprite.ang,
								engine.getangle(sprite[nTarget].x - pSprite.x, sprite[nTarget].y - pSprite.y)) < 64) {
							AnubisList[nAnubis].nState = 2;
							AnubisList[nAnubis].nSeq = 0;
						}
						break;
					}
				case 0x8000:
					pSprite.ang = (short) ((pSprite.ang + 256) & 0x7FF);
					pSprite.xvel = (short) (sintable[(pSprite.ang + 512) & 0x7FF] >> 2);
					pSprite.yvel = (short) (sintable[pSprite.ang & 0x7FF] >> 2);
					break;
				default:
					if (AnubisList[nAnubis].field_C != 0)
						AnubisList[nAnubis].field_C--;
					else {
						AnubisList[nAnubis].field_C = 60;
						if (nTarget != -1 && engine.cansee(pSprite.x, pSprite.y, pSprite.z - GetSpriteHeight(nSprite),
								pSprite.sectnum, sprite[nTarget].x, sprite[nTarget].y,
								sprite[nTarget].z - GetSpriteHeight(nTarget), sprite[nTarget].sectnum)) {
							AnubisList[nAnubis].nState = 3;
							pSprite.xvel = pSprite.yvel = 0;
							pSprite.ang = engine.GetMyAngle(sprite[nTarget].x - pSprite.x,
									sprite[nTarget].y - pSprite.y);
							AnubisList[nAnubis].nSeq = 0;
						}
					}
					break;
				}
				break;
			}

			if (nState != 0 && nTarget != -1 && (sprite[nTarget].cstat & 0x101) == 0) {
				AnubisList[nAnubis].nState = 0;
				AnubisList[nAnubis].nSeq = 0;
				AnubisList[nAnubis].field_C = 100;
				AnubisList[nAnubis].nTarget = -1;
				pSprite.xvel = pSprite.yvel = 0;
			}
			return;
		case nEventView:
			PlotSequence(plr, ActionSeq_X_1[AnubisList[nAnubis].nState][0] + SeqOffsets[17], AnubisList[nAnubis].nSeq,
					ActionSeq_X_1[AnubisList[nAnubis].nState][1]);
			return;
		case nEventRadialDamage:
			if (AnubisList[nAnubis].nState >= 11)
				return;

			damage = CheckRadialDamage(nSprite);
		case nEventDamage:
			if (damage != 0) {
				if (AnubisList[nAnubis].nHealth > 0) {
					AnubisList[nAnubis].nHealth -= damage;
					if (AnubisList[nAnubis].nHealth > 0) {
						if (plr >= 0) {
							int statnum = sprite[plr].statnum;
	
							if ((statnum == 100 || statnum < 199) && RandomSize(5) == 0)
								AnubisList[nAnubis].nTarget = (short) plr;
							if (RandomSize(1) != 0) {
								if (AnubisList[nAnubis].nState >= 6 && AnubisList[nAnubis].nState <= 10) {
									int v54 = engine.insertsprite(pSprite.sectnum, (short) 98);
									sprite[v54].x = pSprite.x;
									sprite[v54].y = pSprite.y;
									sprite[v54].z = sector[sprite[v54].sectnum].floorz;
									sprite[v54].yrepeat = 40;
									sprite[v54].xrepeat = 40;
									sprite[v54].shade = -64;
									BuildObject(v54, 2, 0);
								}
	
								AnubisList[nAnubis].nState = 4;
								AnubisList[nAnubis].nSeq = 0;
							} else {
								D3PlayFX(StaticSound[39], nSprite);
							}
						}
					} else {
						pSprite.xvel = 0;
						pSprite.yvel = 0;
						pSprite.zvel = 0;
						pSprite.z = sector[pSprite.sectnum].floorz;
						AnubisList[nAnubis].nHealth = 0;
						pSprite.cstat &= 0xFEFE;
	
						if (AnubisList[nAnubis].nState < 11) {
							DropMagic(nSprite);
							AnubisList[nAnubis].nState = (short) ((((a1 & 0x7F0000) == 0xA0000) ? 1 : 0) + 11);
							AnubisList[nAnubis].nSeq = 0;
						}
						nCreaturesLeft--;
					}
				}
			}
			return;
		}

	}
}
