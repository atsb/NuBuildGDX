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


package ru.m210projects.Powerslave;

import static ru.m210projects.Powerslave.Globals.*;
import static ru.m210projects.Powerslave.Sector.*;
import static ru.m210projects.Powerslave.Seq.*;
import static ru.m210projects.Powerslave.RunList.*;
import static ru.m210projects.Powerslave.Random.*;
import static ru.m210projects.Powerslave.Main.*;
import static ru.m210projects.Powerslave.Sound.*;
import static ru.m210projects.Powerslave.Player.*;
import static ru.m210projects.Powerslave.Anim.*;
import static ru.m210projects.Powerslave.Object.*;
import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Build.Gameutils.*;
import static ru.m210projects.Build.Pragmas.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

import ru.m210projects.Build.FileHandle.Resource;
import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Powerslave.Type.BubbleMachineStruct;
import ru.m210projects.Powerslave.Type.BubbleStruct;
import ru.m210projects.Powerslave.Type.SafeLoader;


public class Sprites {

	public static short[] nBodySprite = new short[50];
	public static int nCurBodyNum, nBodyTotal;

	public static short[] nChunkSprite = new short[75];
	public static short[] nBodyGunSprite = new short[50];
	public static int nCurChunkNum, nCurBodyGunNum, nChunkTotal;

	public static void InitChunks() {
		nCurChunkNum = 0;
		Arrays.fill(nChunkSprite, (short) -1);
		Arrays.fill(nBodyGunSprite, (short) -1);
		Arrays.fill(nBodySprite, (short) -1);
		nCurBodyNum = 0;
		nCurBodyGunNum = 0;
		nBodyTotal = 0;
		nChunkTotal = 0;
	}

	public static short GrabBody() {
		short nSprite;
		do {
			nSprite = nBodySprite[nCurBodyNum];
			if (nBodySprite[nCurBodyNum] == -1) {
				nSprite = engine.insertsprite((short) 0, (short) 899);
				nBodySprite[nCurBodyNum] = nSprite;
				sprite[nSprite].cstat = (short) 32768;
			}
			if (++nCurBodyNum >= 50)
				nCurBodyNum = 0;
		} while ((sprite[nSprite].cstat & (1 | 256)) != 0);

		if (nBodyTotal < 50)
			++nBodyTotal;

		sprite[nSprite].cstat = 0;
		return nSprite;
	}

	public static boolean GrabItem(int nPlayer, int nItem) {
		if (PlayerList[nPlayer].ItemsAmount[nItem] < 5) {
			PlayerList[nPlayer].ItemsAmount[nItem]++;
			if (nPlayerItem[nPlayer] < 0 || nItem == nPlayerItem[nPlayer])
				SetPlayerItem(nPlayer, nItem);
			return true;
		}
		return false;
	}

	public static int CheckRadialDamage(int a1) {
		int v13 = 0;
		if (a1 != nRadialSpr) {
			SPRITE v2 = sprite[a1];
			if ((v2.cstat & 0x101) != 0) {
				int v3 = v2.statnum;
				SPRITE v4 = sprite[nRadialSpr];
				if (v3 < 1024 && v4.statnum < 1024 && (v3 == 100 || a1 != nRadialOwner)) {
					int v5 = (v2.x - v4.x) >> 8;
					int v18 = (v2.y - v4.y) >> 8;
					int v8 = (v2.z - v4.z) >> 12;
					if (klabs(v5) <= nDamageRadius && klabs(v18) <= nDamageRadius && klabs(v8) <= nDamageRadius) {
						int v19 = engine.ksqrt(v5 * v5 + v18 * v18);
						if (v19 < nDamageRadius) {
							short oldcstat = v2.cstat;
							v2.cstat = 257;
							if ((152 - v2.statnum) <= 1 || engine.cansee(v4.x, v4.y, v4.z - 512, v4.sectnum, v2.x, v2.y,
									v2.z - 0x2000, v2.sectnum)) {
								v13 = nRadialDamage * (nDamageRadius - v19) / nDamageRadius;
								if (v13 >= 0 && v13 > 20) {
									int ang = engine.GetMyAngle(v5, v18);
									v2.xvel += v13 * sintable[(ang + 512) & 0x7FF] >> 3;
									v2.yvel += v13 * sintable[ang & 0x7FF] >> 3;
									v2.zvel = (short) BClipLow(v2.zvel - 24 * v13, -3584);
								}
							}
							v2.cstat = oldcstat;
						} // else v13 = 32767;
					}
				}
			}
		}

		return v13;
	}

	public static void DestroyItemAnim(int nItem) {
		if (sprite[nItem].owner < 0 || sprite[nItem].owner >= MAX_LIL_ANIM)
			return;

		DestroyAnim(sprite[nItem].owner);
	}

	public static void ExplodeScreen(int a1) {
		sprite[a1].z -= GetSpriteHeight(a1) / 2;
		for (int i = 0; i < 30; i++)
			BuildSpark(a1, 0);

		sprite[a1].cstat = (short) 32768;
		PlayFX2(StaticSound[78], a1);
	}

	public static short GrabChunkSprite() {
		short nSprite = nChunkSprite[nCurChunkNum];
		if (nSprite == -1) {
			nSprite = engine.insertsprite((short) 0, (short) 899);
			nChunkSprite[nCurChunkNum] = nSprite;
		} else if (sprite[nSprite].statnum != 0) {
			//game.ThrowError("too many chunks being used at once!");
			System.err.println("too many chunks being used at once!");
			return -1;
		}

		engine.changespritestat(nSprite, (short) 899);
		if (++nCurChunkNum >= 75)
			nCurChunkNum = 0;
		if (nChunkTotal < 75)
			++nChunkTotal;
		sprite[nSprite].cstat = 128;
		return nSprite;
	}

	public static short BuildCreatureChunk(int a0, int a1) {
		short spr = GrabChunkSprite();
		boolean v15 = false;
		if (spr != -1) {
			SPRITE v6 = sprite[spr];
			if ((a0 & 0x4000) != 0) {
				a0 &= ~0x4000;
				v15 = true;
			}

			SPRITE v7 = sprite[a0];
			v6.x = v7.x;
			v6.y = v7.y;
			v6.z = v7.z;
			engine.mychangespritesect(spr, v7.sectnum);
			v6.cstat = 128;
			v6.shade = -12;
			v6.pal = 0;
			v6.xvel = (short) (((RandomSize(5) - 16) & 0xFFFF) << 7);
			v6.yvel = (short) (((RandomSize(5) - 16) & 0xFFFF) << 7);
			v6.zvel = (short) (-8 * (RandomSize(8) + 512));

			if (v15) {
				v6.xvel *= 4;
				v6.yvel *= 4;
				v6.zvel *= 2;
			}
			v6.xrepeat = 64;
			v6.yrepeat = 64;
			v6.xoffset = 0;
			v6.yoffset = 0;
			v6.picnum = (short) a1;
			v6.lotag = (short) (HeadRun() + 1);
			v6.clipdist = 40;
			v6.extra = -1;
			v6.owner = (short) AddRunRec(v6.lotag - 1, 0xD0000 | spr);
			v6.hitag = (short) AddRunRec(NewRun, 0xD0000 | spr);

		}
		return spr;
	}

	public static void FuncCreatureChunk(int a1, int a2, int RunPtr) {
		short spr = (short) (RunData[RunPtr].RunEvent & 0xFFFF);
		if (spr < 0 || spr >= MAXSPRITES) {
			game.ThrowError("spr>=0 && spr<MAXSPRITES");
			return;
		}

		if ((a1 & 0x7F0000) == 0x20000) {
			short sectnum = sprite[spr].sectnum;
			if (isValidSector(sectnum)) {
				Gravity(spr);
				sprite[spr].pal = sector[sectnum].ceilingpal;
				int hitMove = engine.movesprite(spr, sprite[spr].xvel << 10, sprite[spr].yvel << 10,
						sprite[spr].zvel, 2560, -2560, 1);

				if (sprite[spr].z < sector[sectnum].floorz) {
					if (hitMove == 0)
						return;

					if ((hitMove & 0x20000) == 0) {
						int ang;
						switch (hitMove & 0x3C000) {
						default:
							return;
						case 0x8000:
							ang = engine.GetWallNormal(hitMove & 0x3FFF);
							break;
						case 0xC000:
							if(!isValidSprite(hitMove & 0x3FFF))
								return;

							ang = sprite[hitMove & 0x3FFF].ang;
							break;
						case 0x10000:
							sprite[spr].xvel >>= 1;
							sprite[spr].yvel >>= 1;
							sprite[spr].zvel = (short) -sprite[spr].zvel;
							return;
						}

						int v20 = engine.ksqrt(((sprite[spr].yvel >> 10) * (sprite[spr].yvel >> 10)
								+ (sprite[spr].xvel >> 10) * (sprite[spr].xvel >> 10)) >> 8) >> 1;
						sprite[spr].xvel = (short) (v20 * sintable[(ang + 512) & 0x7FF]);
						sprite[spr].yvel = (short) (v20 * sintable[ang & 0x7FF]);
						return;
					} else
						sprite[spr].cstat = (short) 32768;
				} else {
					sprite[spr].xvel = 0;
					sprite[spr].yvel = 0;
					sprite[spr].zvel = 0;
					sprite[spr].z = sector[sprite[spr].sectnum].floorz;
				}
			}

			DoSubRunRec(sprite[spr].owner);
			FreeRun(sprite[spr].lotag - 1);
			SubRunRec(sprite[spr].hitag);
			engine.changespritestat(spr, (short) 0);
			sprite[spr].hitag = 0;
			sprite[spr].lotag = 0;
		}
	}

	public static void Gravity(int nSprite) {
		SPRITE pSprite = sprite[nSprite];
		if (pSprite.sectnum != 1024 && (SectFlag[pSprite.sectnum] & 0x2000) != 0) {
			if (pSprite.statnum == 100) {
				if (pSprite.zvel > 0) {
					pSprite.zvel -= 64;
					if (pSprite.zvel < 0) {
						pSprite.zvel = 0;
						return;
					}
				} else if (pSprite.zvel < 0) {
					pSprite.zvel += 64;
					if (pSprite.zvel > 0) {
						pSprite.zvel = 0;
						return;
					}
				}
			} else {
				if (pSprite.zvel <= 1024)
					pSprite.zvel += 512;
				else
					pSprite.zvel -= 64;
			}
		} else {
			pSprite.zvel += 512;
			pSprite.zvel = (short) BClipHigh(pSprite.zvel, 0x4000);
		}
	}

	public static void DamageEnemy(int nDest, int nSource, int nDamage) {
		int left = nCreaturesLeft;
		if (nDest != -1 && sprite[nDest].statnum < 1024 && sprite[nDest].owner > -1) {
			SendMessageToRunRec(sprite[nDest].owner, 0x80000 | (nSource & 0xFFFF), 4 * nDamage);
			if (left > nCreaturesLeft) {
				if (nSource >= 0 && sprite[nSource].statnum == 100) {
					int plr = GetPlayerFromSprite(nSource);
					nTauntTimer[plr]--;
					if (nTauntTimer[plr] <= 0) {
						if ((SectFlag[sprite[PlayerList[plr].spriteId].sectnum] & 0x2000) == 0)
							D3PlayFX(StaticSound[RandomSize(3) % 5 + 53],
									nDoppleSprite[plr] | (plr == nLocalPlayer ? 0x6000 : 0x4000));
						nTauntTimer[plr] = (short) (RandomSize(3) + 3);
					}
				}
			}
		}
	}

	public static int word_96760;

	public static void RadialDamageEnemy(int nSprite, int damage, int radius) {
		if (radius != 0) {
			++word_96760;
			if (nRadialSpr == -1) {
				nRadialDamage = 4 * damage;
				nDamageRadius = radius;
				nRadialSpr = nSprite;
				nRadialOwner = sprite[nSprite].owner;
				ExplodeSignalRun();
				nRadialSpr = -1;
				word_96760--;
			}
		}
	}

	public static short GetAngleToSprite(int nSprite1, int nSprite2) {
		if (nSprite1 >= 0 && nSprite2 >= 0)
			return engine.GetMyAngle(sprite[nSprite2].x - sprite[nSprite1].x, sprite[nSprite2].y - sprite[nSprite1].y);

		return -1;
	}

	public static int GetSpriteHeight(int num) {
		if(!isValidSprite(num)) return 0;
		return 4 * sprite[num].yrepeat * engine.getTile(sprite[num].picnum).getHeight();
	}

	public static void BuildSplash(int nSprite, int sectnum) {
		int v5, v6, v9, v10;
		if (sprite[nSprite].statnum == 200) {
			v5 = 20;
			v6 = 1;
		} else {
			v5 = (RandomWord() % sprite[nSprite].xrepeat) + sprite[nSprite].xrepeat;
			v6 = 0;
		}

		if ((SectFlag[sectnum] & 0x4000) != 0) {
			v9 = 43;
			v10 = 4;
		} else {
			v9 = 35;
			v10 = 0;
		}
		int v11 = AnimList[BuildAnim(-1, v9, 0, sprite[nSprite].x, sprite[nSprite].y, sector[sectnum].floorz, sectnum, v5, v10)].nSprite;
		if ((SectFlag[sectnum] & 0x4000) == 0) {
			D3PlayFX(StaticSound[v6] | 0xA00, v11);
		}
	}

	public static int AngleChase(int nSprite, int nTarget, int a3, int a4, int a5) {
		SPRITE pSprite = sprite[nSprite];
		short nNewAngle = pSprite.ang;
		if (nTarget >= 0) {
			SPRITE pTarget = sprite[nTarget];
			int zTop = 2 * pTarget.yrepeat * engine.getTile(pTarget.picnum).getHeight();

			int dx = pTarget.x - pSprite.x;
			int dy = pTarget.y - pSprite.y;
			int dz = pTarget.z - pSprite.z;

			int nGoalAngle = AngleDelta(pSprite.ang, engine.GetMyAngle(dx, dy), 1024);
			if (klabs(nGoalAngle) > 63) {
				a3 /= klabs(nGoalAngle >> 6);
				if (a3 < 5) a3 = 5;
			}
			if (klabs(nGoalAngle) > a5) {
				if (nGoalAngle >= 0)
					nGoalAngle = a5;
				else nGoalAngle = -a5;
			}

			nNewAngle = (short) ((pSprite.ang + nGoalAngle) & 0x7FF);
			pSprite.zvel = (short) (pSprite.zvel + (AngleDelta(pSprite.zvel, engine.GetMyAngle(engine.ksqrt(dx * dx + dy * dy), (dz - zTop) >> 8), 24)) & 0x7FF);
		} else
			pSprite.zvel = 0;
		pSprite.ang = nNewAngle;

		int v28 = klabs(sintable[(pSprite.zvel + 512) & 0x7FF]);
		int xvel = v28 * (a3 * sintable[(pSprite.ang + 512) & 0x7FF] >> 14);
		int yvel = v28 * (a3 * sintable[pSprite.ang & 0x7FF] >> 14);
		int v31 = engine.ksqrt((xvel >> 8) * (xvel >> 8) + (yvel >> 8) * (yvel >> 8));

		return engine.movesprite((short) nSprite, xvel >> 2, yvel >> 2,
				(sintable[a4 & 0x7FF] >> 5) + (v31 * sintable[pSprite.zvel & 0x7FF] >> 13), 0, 0, pSprite.statnum != 107 ? 1 : 0);
	}

	public static int AngleDelta(int ang1, int ang2, int a3) {
		int dang = ang2 - ang1;
		if (dang >= 0) {
			if (dang > 1024)
				dang = -(2048 - dang);
		} else if (dang < -1024) {
			dang += 2048;
		}

		if (klabs(dang) > a3)
			return dang < 0 ? -a3 : a3;
		return dang;
	}

	public static int AngleDiff(int a1, int a2) {
		int result = (a2 - a1) & 0x7FF;
		if (result > 1024)
			result = 2048 - result;
		return result;
	}

	public static int GetUpAngle(int a1, int a2, int a3, int a4)
	{
	  int v5 = sprite[a3].x - sprite[a1].x;
	  int v6 = sprite[a3].y - sprite[a1].y;
	  int v14 = (sprite[a3].z + a4) - (sprite[a1].z + a2);
	  int v10 = -(v14 >> 4) - (v14 >> 8);

	  return engine.GetMyAngle(engine.ksqrt(v5 * v5 + v6 * v6), v10);
	}

	public static int BelowNear(int nSprite, int lohit) {

		SPRITE v2 = sprite[nSprite];
		int v11 = v2.sectnum;
		int v3 = 0, v10 = 0;
		if ((lohit & 0xC000) == 0xC000) {
			v10 = 0xC000;
			v3 = sprite[lohit & 0x3FFF].z;
		} else {
			v3 = (SectDepth[v2.sectnum] & 0xFFFF) + sector[v2.sectnum].floorz;
			v10 = 0x20000;
			if (NearCount > 0) {
				int a2 = NearSector[0];
				for (int i = 0; i < NearCount; ++i) {
					for (int j = NearSector[i]; j >= 0; j = SectBelow[j])
						a2 = j;
					int v7 = (SectDepth[a2] & 0xFFFF) + sector[a2].floorz;
					int v8 = v7 - v2.z;
					if (v8 < 0 && v8 >= -5120) {
						v3 = v7;
						v11 = a2;
					}
				}
			}
		}

		if (v3 >= v2.z) {
			v10 = 0;
		} else {
			v2.z = v3;
			overridesect = v11;
			v2.zvel = 0;
			bTouchFloor[0] = true;
		}

		return v10;
	}

	public static void BuildNear(int x, int y, int walldist, short sectnum) {
		NearSector[0] = sectnum;
		int j = 0;
		NearCount = 1;
		while (true) {
			if (j >= NearCount)
				break;

			short nWall = sector[NearSector[j]].wallptr;
			short nWalls = sector[NearSector[j]].wallnum;
			while (--nWalls >= 0) {
				short v8 = wall[nWall].nextsector;
				if (v8 >= 0) {
					int i;
					for (i = 0; i < NearCount && v8 != NearSector[i]; ++i)
						;
					if (i >= NearCount) {
						if (engine.clipinsidebox(x, y, nWall, walldist) != 0) {
							NearSector[NearCount] = wall[nWall].nextsector;
							NearCount++;
						}
					}
				}
				++nWall;
			}
			++j;
		}
	}

	public static void InitBubbles() {
		nMachineCount = 0;
		for (int i = 0; i < 200; i++) {
			nBubblesFree[i] = (byte) i;
			if(BubbleList[i] != null)
				BubbleList[i].nSprite = -1;
		}
		nFreeCount = 200;
	}


	public static ByteBuffer saveBubbles()
	{
		int nBubbles = 0;
		for (int i = 0; i < 200; i++) {
			if(BubbleList[i] != null && BubbleList[i].nSprite != -1)
				nBubbles++;
		}

		ByteBuffer bb = ByteBuffer.allocate(4 + 200 + nBubbles * (BubbleStruct.size + 2) + nMachineCount * BubbleMachineStruct.size);
		bb.order(ByteOrder.LITTLE_ENDIAN);

		bb.putShort((short)nMachineCount);
		bb.putShort((short)nFreeCount);

		for (int i = 0; i < 200; i++)
			bb.put(nBubblesFree[i]);

		if(nBubbles != 0)
		{
			for (int i = 0; i < 200; i++) {
				if(BubbleList[i] != null && BubbleList[i].nSprite != -1) {
					bb.putShort((short)i);
					BubbleList[i].save(bb);
				}
			}
		}

		for (int i = 0; i < nMachineCount; i++)
			Machine[i].save(bb);

		return bb;
	}

	public static void loadBubbles(SafeLoader loader, Resource bb)
	{
		if(bb != null) {
			for (int i = 0; i < 200; i++)
				if(loader.BubbleList[i] != null)
					loader.BubbleList[i].nSprite = -1;

			loader.nMachineCount = bb.readShort();
			loader.nFreeCount = bb.readShort();
			bb.read(loader.nBubblesFree);

			int nBubbles = 200 - loader.nFreeCount;
			while(nBubbles > 0)
			{
				short i = bb.readShort();
				if(loader.BubbleList[i] == null)
					loader.BubbleList[i] = new BubbleStruct();
				loader.BubbleList[i].load(bb);
				nBubbles--;
			}

			for (int i = 0; i < loader.nMachineCount; i++) {
				if(loader.Machine[i] == null)
					loader.Machine[i] = new BubbleMachineStruct();
				loader.Machine[i].load(bb);
			}
		}
		else
		{
			nMachineCount = loader.nMachineCount;
			nFreeCount = loader.nFreeCount;

			System.arraycopy(loader.nBubblesFree, 0, nBubblesFree, 0, nBubblesFree.length);

			for (int i = 0; i < 200; i++) {
				if(BubbleList[i] != null)
					BubbleList[i].nSprite = -1;

				if(loader.BubbleList[i] != null && loader.BubbleList[i].nSprite != -1) {
					if(BubbleList[i] == null)
						BubbleList[i] = new BubbleStruct();
					BubbleList[i].copy(loader.BubbleList[i]);
				}
			}

			for (int i = 0; i < loader.nMachineCount; i++) {
				if(Machine[i] == null)
					Machine[i] = new BubbleMachineStruct();
				Machine[i].copy(loader.Machine[i]);
			}
		}
	}

	public static void DoBubbleMachines() {
		for (int i = 0; i < nMachineCount; i++) {
			if (--Machine[i].field_0 <= 0) {
				SPRITE spr = sprite[Machine[i].field_2];
				Machine[i].field_0 = (short) ((RandomWord() % Machine[i].field_4) + 30);
				BuildBubble(spr.x, spr.y, spr.z, spr.sectnum);
			}
		}
	}

	private static BubbleStruct BuildBubble(int x, int y, int z, int sectnum) {
		int v6 = RandomSize(3);
		if (v6 > 4)
			v6 -= 4;
		if (nFreeCount > 0) {
			int nBubble = nBubblesFree[--nFreeCount] & 0xFF;
			int spr = engine.insertsprite((short) sectnum, (short) 402);
			if (spr < 0 || spr >= MAXSPRITES) {
				game.ThrowError("spr>=0 && spr<MAXSPRITES");
				return null;
			}

			sprite[spr].x = x;
			sprite[spr].y = y;
			sprite[spr].z = z;
			sprite[spr].cstat = 0;
			sprite[spr].shade = -32;
			sprite[spr].pal = 0;
			sprite[spr].clipdist = 5;
			sprite[spr].xrepeat = 40;
			sprite[spr].yrepeat = 40;
			sprite[spr].xoffset = 0;
			sprite[spr].yoffset = 0;
			sprite[spr].picnum = 1;
			sprite[spr].ang = (short) inita;
			sprite[spr].xvel = 0;
			sprite[spr].yvel = 0;
			sprite[spr].zvel = -1200;
			sprite[spr].lotag = (short) (HeadRun() + 1);
			sprite[spr].hitag = 0;
			sprite[spr].extra = 0;

			if (BubbleList[nBubble] == null)
				BubbleList[nBubble] = new BubbleStruct();

			BubbleList[nBubble].nSprite = (short) spr;
			BubbleList[nBubble].field_0 = 0;
			BubbleList[nBubble].nSeq = (short) (v6 + SeqOffsets[15]);

			sprite[spr].owner = (short) AddRunRec(sprite[spr].lotag - 1, 0x140000 | nBubble);
			BubbleList[nBubble].field_6 = (short) AddRunRec(NewRun, 0x140000 | nBubble);
			return BubbleList[nBubble];
		}

		return null;
	}

	public static void FuncBubble(int a1, int a2, int RunPtr) {
		short nBubble = (short) (RunData[RunPtr].RunEvent & 0xFFFF);
		if (nBubble < 0 || nBubble >= 200) {
			game.ThrowError("Bubble>=0 && Bubble<MAX_BUBBLES");
			return;
		}

		int nSprite = BubbleList[nBubble].nSprite;
		switch (a1 & 0x7F0000) {
		case nEventProcess:
			MoveSequence(nSprite, BubbleList[nBubble].nSeq, BubbleList[nBubble].field_0);
			if (++BubbleList[nBubble].field_0 >= SeqSize[BubbleList[nBubble].nSeq])
				BubbleList[nBubble].field_0 = 0;

			game.pInt.setsprinterpolate(nSprite, sprite[nSprite]);
			sprite[nSprite].z += sprite[nSprite].zvel;
			if (sprite[nSprite].z <= sector[sprite[nSprite].sectnum].ceilingz) {
				int v13 = SectAbove[sprite[nSprite].sectnum];
				if (sprite[nSprite].hitag > -1 && v13 != -1)
					BuildAnim(-1, 70, 0, sprite[nSprite].x, sprite[nSprite].y, sector[v13].floorz, v13, 0x40, 0);
				DestroyBubble(nBubble);
			}
			return;
		case nEventView:
			short nObject = (short) (a1 & 0xFFFF);
			PlotSequence(nObject, BubbleList[nBubble].nSeq, BubbleList[nBubble].field_0, 1);
			tsprite[nObject].owner = -1;
			return;
		}
	}

	public static void DestroyBubble(int a1) {
		short nSprite = BubbleList[a1].nSprite;
		DoSubRunRec(sprite[nSprite].lotag - 1);
		DoSubRunRec(sprite[nSprite].owner);
		SubRunRec(BubbleList[a1].field_6);
		engine.mydeletesprite(nSprite);
		BubbleList[a1].nSprite = -1;
		nBubblesFree[nFreeCount] = (byte) a1;
		nFreeCount++;
	}

	public static int GetBubbleSprite(BubbleStruct pBubble) {
		return pBubble.nSprite;
	}

	public static void DoBubbles(int a1) {
		int[] out = WheresMyMouth(a1);
		if(out[3] != -1) {
			int spr = GetBubbleSprite(BuildBubble(out[0], out[1], out[2], out[3]));
			if (spr != -1)
				sprite[spr].hitag = (short) a1;
		}
	}

	private static int[] wheresMyMouth = new int[4];

	public static int[] WheresMyMouth(int plr) {
		int nSprite = PlayerList[plr].spriteId;
		engine.clipmove(sprite[nSprite].x, sprite[nSprite].y, sprite[nSprite].z - GetSpriteHeight(nSprite) >> 1,
				sprite[nSprite].sectnum, sintable[(sprite[nSprite].ang + 512) & 0x7FF] << 7,
				sintable[sprite[nSprite].ang & 0x7FF] << 7, 5120, 1280, 1280, CLIPMASK1);

		wheresMyMouth[0] = clipmove_x;
		wheresMyMouth[1] = clipmove_y;
		wheresMyMouth[2] = clipmove_z;
		wheresMyMouth[3] = clipmove_sectnum;

		return wheresMyMouth;
	}

}
