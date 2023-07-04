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

import com.badlogic.gdx.math.Vector2;

import ru.m210projects.Build.FileHandle.Resource;
import ru.m210projects.Build.Types.SECTOR;
import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Build.Types.WALL;
import ru.m210projects.Powerslave.Type.BlockInfo;
import ru.m210projects.Powerslave.Type.MoveSectStruct;
import ru.m210projects.Powerslave.Type.SafeLoader;
import ru.m210projects.Powerslave.Type.TrailPointStruct;

import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Build.Gameutils.*;
import static ru.m210projects.Build.Pragmas.*;
import static ru.m210projects.Powerslave.Globals.*;
import static ru.m210projects.Powerslave.Palette.*;
import static ru.m210projects.Powerslave.Map.*;
import static ru.m210projects.Powerslave.Random.*;
import static ru.m210projects.Powerslave.Switch.*;
import static ru.m210projects.Powerslave.Object.*;
import static ru.m210projects.Powerslave.RunList.*;
import static ru.m210projects.Powerslave.Main.*;
import static ru.m210projects.Powerslave.View.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static ru.m210projects.Powerslave.Sound.*;
import static ru.m210projects.Powerslave.Sprites.*;
import static ru.m210projects.Powerslave.Bullet.*;

public class Sector {

	public static int overridesect;
	public static short[] NearSector = new short[MAXSECTORS];
	public static int NearCount;

	public static int LinkCount;
	public static byte[][] LinkMap = new byte[1024][8];
	private static Vector2 tmpVec = new Vector2();

	public static Vector2 MoveSector(int nObject, int angle, int xvec, int yvec) {
		if (nObject != -1) {
			int v4, v5;
			if (angle < 0) {
				v5 = yvec;
				v4 = xvec;
				angle = engine.GetMyAngle(xvec, yvec);
			} else {
				v4 = sintable[(angle + 512) & 0x7FF] << 6;
				v5 = sintable[angle & 0x7FF] << 6;
			}

			SECTOR v7 = sector[nObject];
			int v53 = v7.floorz;
			int flags = SectFlag[nObject] & 0x2000;

			BlockInfo bInfo = sBlockInfo[sector[nObject].extra];

			int block_x = bInfo.cx;
			int oldx = block_x;
			int block_y = bInfo.cy;
			int oldy = block_y;

			short nSector = (short) nObject;
			short nNextSector = wall[v7.wallptr].nextsector;

			int v45 = 0, v54 = 0;
			if (flags != 0) {
				v54 = v7.ceilingz;
				v45 = sector[nNextSector].ceilingz + 256;
				v7.ceilingz = sector[nNextSector].ceilingz;
			} else {
				v54 = v7.floorz;
				v45 = sector[nNextSector].floorz - 256;
				v7.floorz = sector[nNextSector].floorz;
			}
			engine.clipmove(block_x, block_y, v45, nSector, v4, v5, bInfo.field_8, 0, 0, CLIPMASK1);
			if (clipmove_sectnum > -1) {
				block_x = clipmove_x;
				block_y = clipmove_y;
				nSector = clipmove_sectnum;
			}

			int dx = block_x - oldx;
			int dy = block_y - oldy;
			if (nSector == nNextSector || nSector == nObject) {
				if (flags == 0) {
					block_x = oldx;
					block_y = oldy;
					engine.clipmove(block_x, block_y, v54, nSector, v4, v5, bInfo.field_8, 0, 0, CLIPMASK1);
					if (clipmove_sectnum > -1) {
						block_x = clipmove_x;
						block_y = clipmove_y;
						nSector = clipmove_sectnum;
					}

					if (klabs(dx) > klabs(block_x - oldx))
						dx = block_x - oldx;
					if (klabs(dy) > klabs(block_y - oldy))
						dy = block_y - oldy;
				}
			} else {
				dx = 0;
				dy = 0;
			}

			if ((dx | dy) != 0) {
				for (int i = headspritesect[nObject]; i != -1; i = nextspritesect[i]) {
					SPRITE pSprite = sprite[i];
					game.pInt.setsprinterpolate(i, pSprite);
					if (pSprite.statnum >= 99) {
						if (flags != 0 || pSprite.z != v54 || (pSprite.cstat & 0x8000) != 0) {
							block_x = pSprite.x;
							block_y = pSprite.y;
							nSector = (short) nObject;

							engine.clipmove(block_x, block_y, pSprite.z, nSector, -dx, -dy, 4 * pSprite.clipdist, 0, 0,
									CLIPMASK0);
							if (clipmove_sectnum > -1) {
								block_x = clipmove_x;
								block_y = clipmove_y;
								pSprite.z = clipmove_z;
								nSector = clipmove_sectnum;
							}
							if (nSector >= 0 && nSector < 1024 && nSector != nObject) {
								engine.mychangespritesect((short) i, (short) nSector);
							}
						}
					} else {
						pSprite.x += dx;
						pSprite.y += dy;
					}
				}

				for (int i = headspritesect[nNextSector]; i != -1; i = nextspritesect[i]) {
					SPRITE pSprite = sprite[i];
					if (pSprite.statnum >= 99) {
						block_x = pSprite.x;
						block_y = pSprite.y;
						nSector = nNextSector;

						int dist = 4 * pSprite.clipdist;
						engine.clipmove(block_x, block_y, pSprite.z, nSector,
								-dx - sintable[(angle + 512) & 0x7FF] * dist, -dy - sintable[angle] * dist, dist, 0, 0,
								CLIPMASK0);
						if (clipmove_sectnum > -1) {
							block_x = clipmove_x;
							block_y = clipmove_y;
							pSprite.z = clipmove_z;
							nSector = clipmove_sectnum;
						}

						if (nSector != nNextSector && (nSector == nObject || nNextSector == nObject)) {
							if (nSector != nObject || v53 >= pSprite.z) {
								if (nSector >= 0 && nSector < 1024)
									engine.mychangespritesect((short) i, nSector);
							} else {
								engine.movesprite((short) i,
										(dx << 14) + sintable[(angle + 512) & 0x7FF] * pSprite.clipdist,
										(dy << 14) + pSprite.clipdist * sintable[angle], 0, 0, 0, 0);
							}
						}
					}
				}

				for (short i = v7.wallptr; i < v7.wallptr + v7.wallnum; i++)
					engine.dragpoint(i, dx + wall[i].x, dy + wall[i].y);
				bInfo.cx += dx;
				bInfo.cy += dy;
			}

			dx <<= 14;
			dy <<= 14;
			if (flags == 0) {
				for (int i = headspritesect[nObject]; i != -1; i = nextspritesect[i]) {
					SPRITE pSprite = sprite[i];
					if (pSprite.statnum >= 99 && v54 == pSprite.z && (pSprite.cstat & 0x8000) == 0) {
						nSector = (short) nObject;
						game.pInt.setsprinterpolate(i, pSprite);
						
						engine.clipmove(pSprite.x, pSprite.y, pSprite.z, nSector, dx, dy, 4 * pSprite.clipdist, 5120,
								-5120, CLIPMASK0);
						if (clipmove_sectnum > -1) {
							pSprite.x = clipmove_x;
							pSprite.y = clipmove_y;
							pSprite.z = clipmove_z;
							nSector = clipmove_sectnum;
						}
					}
				}
			}

			if (flags != 0)
				v7.ceilingz = v54;
			else
				v7.floorz = v54;
			tmpVec.set(dx, dy);
		} else
			tmpVec.set(xvec, yvec);
		return tmpVec;
	}

	public static int feebtag(int x, int y, int z, int sectnum, int a6, int dist) {
		int out = -1;

		int sec = sectnum;
		for (int i = sector[sectnum].wallptr; i < sector[sectnum].wallptr + sector[sectnum].wallnum; i++) {
			if (sec != -1) {
				for (int nSprite = headspritesect[sec]; nSprite >= 0; nSprite = nextspritesect[nSprite]) {
					SPRITE pSprite = sprite[nSprite];
					if (pSprite.statnum >= 900 && (pSprite.cstat & 0x8000) == 0) {
						int v11 = pSprite.x - x;
						int v14 = pSprite.y - y;
						int v13 = pSprite.z - z;

						if (v13 < 5120 && v13 > -25600) {
							int v15 = engine.ksqrt(v14 * v14 + v11 * v11);
							if (v15 < dist && (pSprite.statnum != 950 && pSprite.statnum != 949 || (a6 & 1) == 0)
									&& (pSprite.statnum != 912 && pSprite.statnum != 913 || (a6 & 2) == 0)) {
								dist = v15;
								out = nSprite;
							}
						}
					}

				}
			}
			sec = wall[i].nextsector;
		}

		return out;
	}

	public static void DoMovingSects() {
		for (int i = 0; i < nMoveSects; i++) {
			MoveSectStruct v0 = sMoveSect[i];
			if (v0.field_0 != -1) {
				int v2 = v0.field_E;
				if (v2 == -1 || channel[v2].field_4 != 0) {
					BlockInfo v3 = sBlockInfo[sector[v0.field_0].extra];
					if (v0.field_4 == -1) {
						int v4 = v0.field_6;
						if ((v4 & 0x20) != 0)
							ChangeChannel(v0.field_E, 0);
						int v5 = v4 & 0x10;
						if (v5 != 0) {
							sMoveDir[i] = (byte) -sMoveDir[i];
							if (sMoveDir[i] <= 0)
								v0.field_4 = sTrail[v0.field_2].field_4;
							else
								v0.field_4 = sTrail[v0.field_2].field_0;
						} else {
							v0.field_4 = sTrail[v0.field_2].field_0;
						}
					}

					TrailPointStruct v9 = sTrailPoint[v0.field_4];
					int dx = v9.x - v3.cx;
					int dy = v9.y - v3.cy;
					int ang = engine.GetMyAngle(dx, dy) & 0x7FF;
					int v29 = 16 * sintable[ang] * v0.field_A;
					int v30 = 16 * sintable[(ang + 512) & 0x7FF] * v0.field_A;
					int v16 = dx << 14;
					int v20 = dy << 14;

					if (klabs(v30) > klabs(v16) || klabs(v29) > klabs(v20)) {
						v29 = v20;
						v30 = v16;
						if (sMoveDir[i] <= 0)
							v0.field_4 = nTrailPointPrev[v0.field_4];
						else
							v0.field_4 = nTrailPointNext[v0.field_4];
					}

					if (v0.field_8 != -1) {
						Vector2 vec = MoveSector(v0.field_8, -1, v30, v29);
						v30 = (int) vec.x;
						v29 = (int) vec.y;
					}
					int v28 = v30;
					int v27 = v29;
					Vector2 vec = MoveSector(v0.field_0, -1, v30, v29);
					v30 = (int) vec.x;
					v29 = (int) vec.y;
					if (v30 != v28 || v29 != v27) {
						MoveSector(v0.field_8, -1, v28, v27);
						MoveSector(v0.field_8, -1, v30, v29);
					}
				}
			}
		}
	}

	public static Vector2 CheckSectorFloor(int nSector, int z, int dx, int dy) {
		tmpVec.set(dx, dy);
		if (SectSpeed[nSector] != 0) {

			int v10 = SectFlag[nSector] & 0x7FF;
			if (z >= sector[nSector].floorz) {
				tmpVec.x += SectSpeed[nSector] * 8 * sintable[(v10 + 512) & 0x7FF];
				tmpVec.y += SectSpeed[nSector] * 8 * sintable[v10 & 0x7FF];

			} else {

				if ((SectFlag[nSector] & 0x800) == 0)
					return tmpVec;
				tmpVec.x += SectSpeed[nSector] * 16 * sintable[(v10 + 512) & 0x7FF];
				tmpVec.y += SectSpeed[nSector] * 16 * sintable[v10 & 0x7FF];
			}
		}
		return tmpVec;
	}
	
	public static ByteBuffer saveSecExtra()
	{
		ByteBuffer bb = ByteBuffer.allocate(1024 * 16);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		
		for (int i = 0; i < 1024; i++) {
			bb.putShort(SectSoundSect[i]);
			bb.putShort(SectSound[i]);
			bb.putShort(SectAbove[i]);
			bb.putShort(SectBelow[i]);
			bb.putShort(SectDepth[i]);
			bb.putShort(SectFlag[i]);
			bb.putShort(SectSpeed[i]);
			bb.putShort(SectDamage[i]);
		}
		
		return bb;
	}
	
	public static void loadSecExtra(SafeLoader loader, Resource bb)
	{
		if(bb != null)
		{
			for (int i = 0; i < 1024; i++) {
				loader.SectSoundSect[i] = bb.readShort();
				loader.SectSound[i] = bb.readShort();
				loader.SectAbove[i] = bb.readShort();
				loader.SectBelow[i] = bb.readShort();
				loader.SectDepth[i] = bb.readShort();
				loader.SectFlag[i] = bb.readShort();
				loader.SectSpeed[i] = bb.readShort();
				loader.SectDamage[i] = bb.readShort();
			}
		}
		else
		{
			System.arraycopy(loader.SectSoundSect, 0, SectSoundSect, 0, 1024);
			System.arraycopy(loader.SectSound, 0, SectSound, 0, 1024);
			System.arraycopy(loader.SectAbove, 0, SectAbove, 0, 1024);
			System.arraycopy(loader.SectBelow, 0, SectBelow, 0, 1024);
			System.arraycopy(loader.SectDepth, 0, SectDepth, 0, 1024);
			System.arraycopy(loader.SectFlag, 0, SectFlag, 0, 1024);
			System.arraycopy(loader.SectSpeed, 0, SectSpeed, 0, 1024);
			System.arraycopy(loader.SectDamage, 0, SectDamage, 0, 1024);
		}
	}

	public static void SnapSectors(int a1, int a2, int a3) {
		int v20 = a1;
		int v19 = a2;
		int v18 = a3;
		SECTOR v3 = sector[a1];
		SECTOR v4 = sector[v19];
		int v29 = v3.wallnum;
		int v30 = v4.wallnum;
		int i = 0;
		while (i < v29) {
			int v5 = v4.wallptr;
			int v6 = 0x7FFFFFF;
			int v7 = v3.wallptr;
			int j = 0;
			int v9 = 0x7FFFFFF;
			int v26 = wall[v7].x;
			int v24 = wall[v7].y;
			int v31 = 0;
			while (j < v30) {
				int v11 = v26 - wall[v5].x;
				int v12 = v24 - wall[v5].y;
				int v13 = v26 - wall[v5].x;
				if (v13 < 0)
					v13 = -v13;
				int v22 = v13;
				int v14 = v12;
				if (v12 < 0)
					v14 = -v12;
				int v23 = v14 + v22;
				int v15 = v6;
				if (v6 < 0)
					v15 = -v6;
				int v21 = v15;
				int v16 = v9;
				if (v9 < 0)
					v16 = -v9;
				if (v23 < v16 + v21) {
					v31 = v5;
					v6 = v11;
					v9 = v12;
				}
				++j;
				++v5;
			}
			++i;
			++v7;
			engine.dragpoint((short) v31, v6 + wall[v31].x, v9 + wall[v31].y);
		}
		if (v18 != 0)
			sector[v19].ceilingz = sector[v20].floorz;
		if ((SectFlag[v20] & 0x1000) != 0)
			SnapBobs(v20, v19);
	}

	public static int FindWallSprites(int a1) {
		int miny = 0x7FFFFFFF;
		int maxy = -0x7FFFFFFE;
		int nwall = sector[a1].wallptr;
		int minx = 0x7FFFFFFF;
		int wallnum = sector[a1].wallnum;
		int maxx = -0x7FFFFFFE;
		for (int i = 0; i < wallnum; ++i) {
			if (minx > wall[nwall].x)
				minx = wall[nwall].x;
			if (maxx < wall[nwall].x)
				maxx = wall[nwall].x;
			if (miny > wall[nwall].y)
				miny = wall[nwall].y;
			if (maxy < wall[nwall].y)
				maxy = wall[nwall].y;
			nwall++;
		}
		int x1 = minx - 5;
		int y1 = miny - 5;
		int x2 = maxx + 5;
		int y2 = maxy + 5;
		short spr = -1;

		for (int i = 0; i < 4096; i++) {
			if (sprite[i].lotag == 0 && (sprite[i].cstat & 0x50) == 80) {
				if (sprite[i].x >= x1 && x2 >= sprite[i].x && sprite[i].y >= y1 && sprite[i].y <= y2) {
					sprite[i].owner = spr;
					spr = (short) i;
				}
			}
		}

		if (spr == -1) {
			spr = engine.insertsprite((short) a1, (short) 401);
			sprite[spr].x = (x2 + x1) / 2;
			sprite[spr].y = (y2 + y1) / 2;
			sprite[spr].z = sector[a1].floorz;
			sprite[spr].cstat = (short) 32768;
			sprite[spr].owner = -1;
			sprite[spr].lotag = 0;
			sprite[spr].hitag = 0;
		}

		return spr;
	}

	public static int BuildLink(int a1, int... a2) {
		int v2 = -1;
		if (LinkCount > 0) {
			int v3 = 0;
			v2 = --LinkCount;
			while (v3 < 8) {
				int v5 = -1;
				if (v3 < a1) {
					v5 = a2[v3];
				}
				LinkMap[v2][v3] = (byte) v5;
				v3++;
			}
		}
		return v2;
	}
	
	public static ByteBuffer saveLinks()
	{
		ByteBuffer bb = ByteBuffer.allocate(1024 * 8 + 2);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		
		bb.putShort((short)LinkCount);
		for(int i = 0, j; i < 1024; i++)
		{
			for(j = 0; j < 8; j++)
				bb.put(LinkMap[i][j]);
		}
		
		return bb;
	}

	public static void loadLinks(SafeLoader loader, Resource bb)
	{
		if(bb != null) {
			loader.LinkCount = bb.readShort();
			for(int i = 0, j; i < 1024; i++)
				for(j = 0; j < 8; j++)
					loader.LinkMap[i][j] = bb.readByte();
		}
		else
		{
			LinkCount = loader.LinkCount;
			for(int i = 0; i < 1024; i++)
				System.arraycopy(loader.LinkMap[i], 0, LinkMap[i], 0, 8);
		}
	}
	
	
	public static void ProcessSectorTag(short nSector, int nLotag, int nHitag) {
		int nNext = -1;

		int nChannel = AllocChannel(nHitag % 1000);
		int[] v263 = new int[8];

		int var_24 = nHitag / 1000 << 12;
		int v269 = 4 * BClipLow(nLotag / 1000, 1);
		int v270 = 100 * v269;

		switch (nLotag % 1000 - 1) {
		case 0:
			if ((nNext = engine.nextsectorneighborz(nSector, sector[nSector].ceilingz, -1, -1)) == -1)
				break;

			AddRunRec(channel[nChannel].head, BuildElevC(0, nChannel, nSector, FindWallSprites(nSector), v270, v270, 2,
					sector[nSector].floorz, sector[nNext].ceilingz));
			AddRunRec(channel[nChannel].head, BuildSwPressSector(nChannel, BuildLink(1, 1), nSector, var_24));
			AddRunRec(channel[nChannel].head, BuildSwPause(nChannel, BuildLink(2, -1, 0), 60));
			break;
		case 1:
			if ((nNext = engine.nextsectorneighborz(nSector, sector[nSector].floorz, 1, 1)) == -1)
				break;

			AddRunRec(channel[nChannel].head, BuildElevF(nChannel, nSector, FindWallSprites(nSector), v270, v270, 2,
					sector[nSector].ceilingz, sector[nNext].floorz));
			AddRunRec(channel[nChannel].head, BuildSwPressSector(nChannel, BuildLink(1, 1), nSector, var_24));
			AddRunRec(channel[nChannel].head, BuildSwPause(nChannel, BuildLink(2, -1, 0), 60));
			break;
		case 4:
			if ((nNext = engine.nextsectorneighborz(nSector, sector[nSector].floorz + 1, -1, -1)) == -1)
				break;

			AddRunRec(channel[nChannel].head, BuildElevF(nChannel, nSector, FindWallSprites(nSector), v270, v270, 2,
					sector[nSector].floorz, sector[nNext].ceilingz));
			break;
		case 5:
			if ((nNext = engine.nextsectorneighborz(nSector, sector[nSector].floorz, 1, -1)) == -1)
				break;

			AddRunRec(channel[nChannel].head, BuildElevF(nChannel, nSector, FindWallSprites(nSector), 400, 400, 2,
					sector[nNext].floorz, sector[nSector].floorz));
			AddRunRec(channel[nChannel].head, BuildSwStepOn(nChannel, BuildLink(2, 1, 1), nSector));
			sector[nSector].floorz = sector[nNext].floorz;
			return;
		case 6:
			if ((nNext = engine.nextsectorneighborz(nSector, sector[nSector].floorz, 1, 1)) == -1)
				break;

			AddRunRec(channel[nChannel].head, BuildElevF(nChannel, nSector, FindWallSprites(nSector), v270, v270, 2,
					sector[nSector].floorz, sector[nNext].floorz));
			AddRunRec(channel[nChannel].head, BuildSwStepOn(nChannel, BuildLink(1, 1), nSector));
			AddRunRec(channel[nChannel].head, BuildSwNotOnPause(nChannel, BuildLink(2, -1, 0), nSector, 8));
			break;
		case 7:
			if ((nNext = engine.nextsectorneighborz(nSector, sector[nSector].floorz + 1, 1, 1)) == -1)
				break;

			AddRunRec(channel[nChannel].head, BuildElevF(nChannel, nSector, FindWallSprites(nSector), v270, v270, 2,
					sector[nSector].floorz, sector[nNext].floorz));
			break;
		case 8:
			if ((nNext = engine.nextsectorneighborz(nSector, sector[nSector].floorz, 1, 1)) == -1)
				break;

			AddRunRec(channel[nChannel].head, BuildElevF(nChannel, nSector, FindWallSprites(nSector), v270, v270, 2,
					sector[nSector].floorz, sector[nNext].floorz));
			AddRunRec(channel[nChannel].head, BuildSwPause(nChannel, BuildLink(2, -1, 0), 150));
			break;
		case 9:
			if ((nNext = engine.nextsectorneighborz(nSector, sector[nSector].floorz, 1, -1)) == -1)
				break;

			AddRunRec(channel[nChannel].head, BuildElevF(nChannel, nSector, FindWallSprites(nSector), v270, v270, 2,
					sector[nSector].floorz, sector[nNext].floorz));

			AddRunRec(channel[nChannel].head, BuildSwStepOn(nChannel, BuildLink(1, 1), nSector));
			AddRunRec(channel[nChannel].head, BuildSwNotOnPause(nChannel, BuildLink(2, -1, 0), nSector, 8));
			break;
		case 10:
		case 13:
		case 37:
			if ((nNext = engine.nextsectorneighborz(nSector, sector[nSector].floorz, 1, -1)) == -1)
				break;

			AddRunRec(channel[nChannel].head, BuildElevF(nChannel, nSector, FindWallSprites(nSector), v270, v270, 2,
					sector[nSector].floorz, sector[nNext].floorz));
			break;
		case 11:
			if ((nNext = engine.nextsectorneighborz(nSector, sector[nSector].floorz, 1, -1)) == -1)
				break;

			AddRunRec(channel[nChannel].head, BuildElevF(nChannel, nSector, FindWallSprites(nSector), v270, v270, 2,
					sector[nSector].floorz, sector[nNext].floorz));
			AddRunRec(channel[nChannel].head, BuildSwPause(nChannel, BuildLink(2, -1, 0), 150));
			break;
		case 12:
			if ((nNext = engine.nextsectorneighborz(nSector, sector[nSector].floorz, 1, 1)) == -1)
				break;

			AddRunRec(channel[nChannel].head, BuildElevF(nChannel, nSector, FindWallSprites(nSector), v270, v270, 2,
					sector[nSector].floorz, sector[nNext].floorz));
			AddRunRec(channel[nChannel].head, BuildSwReady(nChannel, BuildLink(2, 1, 0)));
			break;
		case 14:
			if ((nNext = engine.nextsectorneighborz(nSector, sector[nSector].floorz, 1, -1)) == -1)
				break;

			AddRunRec(channel[nChannel].head, BuildElevF(nChannel, nSector, FindWallSprites(nSector), v270, v270, 2,
					sector[nSector].floorz, sector[nNext].floorz));
			break;
		case 15:
			AddRunRec(channel[nChannel].head, BuildElevC(0, nChannel, nSector, FindWallSprites(nSector), 200, v270, 2,
					sector[nSector].ceilingz, sector[nSector].floorz - 8));
			AddRunRec(channel[nChannel].head, BuildSwStepOn(nChannel, BuildLink(1, 1), nSector));
			AddRunRec(channel[nChannel].head, BuildSwReady(nChannel, BuildLink(2, -1, 0)));
			break;
		case 16:
			AddRunRec(channel[nChannel].head, BuildElevC(0, nChannel, nSector, FindWallSprites(nSector), 200, v270, 2,
					sector[nSector].ceilingz, sector[nSector].floorz - 8));
			AddRunRec(channel[nChannel].head, BuildSwReady(nChannel, BuildLink(2, -1, 0)));
			break;
		case 17:
			AddRunRec(channel[nChannel].head,
					BuildElevF(nChannel, nSector, FindWallSprites(nSector), 200, v270, 2, sector[nSector].floorz,
							sector[nSector].ceilingz + (sector[nSector].floorz - sector[nSector].ceilingz) / 2));
			AddRunRec(channel[nChannel].head,
					BuildElevC(0, nChannel, nSector, FindWallSprites(nSector), 200, v270, 2, sector[nSector].ceilingz,
							(sector[nSector].floorz - sector[nSector].ceilingz) / 2 + sector[nSector].ceilingz - 8));
			AddRunRec(channel[nChannel].head, BuildSwStepOn(nChannel, BuildLink(1, 1), nSector));
			break;
		case 20:
			AddRunRec(channel[nChannel].head, BuildSwStepOn(nChannel, BuildLink(2, 1, 1), nSector));
			break;
		case 23:
			if ((nNext = engine.nextsectorneighborz(nSector, sector[nSector].ceilingz, -1, -1)) == -1)
				break;

			AddRunRec(channel[nChannel].head, BuildElevC(0, nChannel, nSector, FindWallSprites(nSector), v270, v270, 2,
					sector[nSector].floorz, sector[nNext].ceilingz));
			AddRunRec(channel[nChannel].head, BuildSwPause(nChannel, BuildLink(2, -1, 0), 60));
			break;
		case 24:
			if ((nNext = engine.nextsectorneighborz(nSector, sector[nSector].floorz, 1, 1)) == -1)
				break;

			AddRunRec(channel[nChannel].head, BuildElevF(nChannel, nSector, FindWallSprites(nSector), v270, v270, 2,
					sector[nSector].floorz, sector[nNext].floorz));
			AddRunRec(channel[nChannel].head, BuildSwPause(nChannel, BuildLink(2, -1, 0), 300));
			break;
		case 25:
			if ((nNext = engine.nextsectorneighborz(nSector, sector[nSector].floorz, 1, 1)) == -1)
				break;

			AddRunRec(channel[nChannel].head, BuildElevF(nChannel, nSector, FindWallSprites(nSector), v270, v270, 2,
					sector[nSector].floorz, sector[nNext].floorz));
			AddRunRec(channel[nChannel].head, BuildSwPause(nChannel, BuildLink(2, -1, 0), 450));
			break;
		case 26:
			if ((nNext = engine.nextsectorneighborz(nSector, sector[nSector].floorz, 1, 1)) == -1)
				break;

			AddRunRec(channel[nChannel].head, BuildElevF(nChannel, nSector, FindWallSprites(nSector), v270, v270, 2,
					sector[nSector].floorz, sector[nNext].floorz));
			AddRunRec(channel[nChannel].head, BuildSwPause(nChannel, BuildLink(2, -1, 0), 600));
			break;
		case 27:
			if ((nNext = engine.nextsectorneighborz(nSector, sector[nSector].floorz, 1, 1)) == -1)
				break;

			AddRunRec(channel[nChannel].head, BuildElevF(nChannel, nSector, FindWallSprites(nSector), v270, v270, 2,
					sector[nSector].floorz, sector[nNext].floorz));
			AddRunRec(channel[nChannel].head, BuildSwPause(nChannel, BuildLink(2, -1, 0), 900));
			break;
		case 30:
			if ((nNext = engine.nextsectorneighborz(nSector, sector[nSector].floorz, 1, 1)) == -1)
				break;

			AddRunRec(channel[nChannel].head, BuildElevF(nChannel, nSector, FindWallSprites(nSector), 0x7FFF, 0x7FFF, 2,
					sector[nSector].floorz, sector[nNext].floorz));
			AddRunRec(channel[nChannel].head, BuildSwStepOn(nChannel, BuildLink(1, 1), nSector));
			break;
		case 31:
			if ((nNext = engine.nextsectorneighborz(nSector, sector[nSector].floorz, 1, 1)) == -1)
				break;

			AddRunRec(channel[nChannel].head, BuildElevF(nChannel, nSector, FindWallSprites(nSector), 0x7FFF, 0x7FFF, 2,
					sector[nSector].floorz, sector[nNext].floorz));
			break;
		case 32:
			if ((nNext = engine.nextsectorneighborz(nSector, sector[nSector].ceilingz, -1, -1)) == -1)
				break;

			AddRunRec(channel[nChannel].head, BuildElevC(20, nChannel, nSector, FindWallSprites(nSector), v270, v270, 2,
					sector[nNext].ceilingz, sector[nSector].floorz));
			break;
		case 33:
			if ((nNext = engine.nextsectorneighborz(nSector, sector[nSector].ceilingz, -1, -1)) == -1)
				break;

			AddRunRec(channel[nChannel].head, BuildElevC(28, nChannel, nSector, FindWallSprites(nSector), v270, v270, 2,
					sector[nNext].ceilingz, sector[nSector].floorz));
			break;
		case 34:
		case 35:
			nEnergyTowers++;
			int i = BuildEnergyBlock(nSector);
			if (nLotag == 36)
				nFinaleSpr = i;
			return;
		case 36:
			if ((nNext = engine.nextsectorneighborz(nSector, sector[nSector].floorz, 1, 1)) == -1)
				break;

			AddRunRec(channel[nChannel].head, BuildElevF(nChannel, nSector, FindWallSprites(nSector), v270, v270, 2,
					sector[nSector].floorz, sector[nNext].floorz));
			break;
		case 38:
			if ((nNext = engine.nextsectorneighborz(nSector, sector[nSector].floorz, 1, 1)) == -1)
				break;

			AddRunRec(channel[nChannel].head, BuildElevF(nChannel, nSector, FindWallSprites(nSector), 0x7FFF, 0x7FFF, 2,
					sector[nSector].floorz, sector[nNext].floorz));
			AddRunRec(channel[nChannel].head, BuildSwStepOn(nChannel, BuildLink(1, 1), nSector));
			AddRunRec(channel[nChannel].head, BuildSwNotOnPause(nChannel, BuildLink(2, -1, 0), nSector, 8));
			break;
		case 39:
			AddMovingSector(nSector, nLotag, nHitag % 1000, 2);
			return;
		case 40:
			AddMovingSector(nSector, nLotag, nHitag % 1000, 18);
			return;
		case 41:
			AddMovingSector(nSector, nLotag, nHitag % 1000, 58);
			return;
		case 42:
			AddMovingSector(nSector, nLotag, nHitag % 1000, 122);
			return;
		case 43:
			AddMovingSector(nSector, nLotag, nHitag % 1000, 90);
			return;
		case 44:
			CreatePushBlock(nSector);
			return;
		case 47:
			if ((nNext = engine.nextsectorneighborz(nSector, sector[nSector].ceilingz, -1, 1)) == -1)
				break;

			AddRunRec(channel[nChannel].head, BuildElevC(0, nChannel, nSector, FindWallSprites(nSector), 200, v270, 2,
					sector[nSector].ceilingz, sector[nNext].ceilingz));
			break;
		case 48:
			if ((nNext = engine.nextsectorneighborz(nSector, sector[nSector].ceilingz, -1, -1)) == -1)
				break;

			AddRunRec(channel[nChannel].head, BuildElevC(0, nChannel, nSector, FindWallSprites(nSector), 200, v270, 2,
					sector[nSector].ceilingz, sector[nNext].ceilingz));
			break;
		case 50:
			AddRunRec(channel[nChannel].head,
					BuildElevF(nChannel, nSector, FindWallSprites(nSector), 200, v270, 2, sector[nSector].floorz,
							sector[nSector].ceilingz + (sector[nSector].floorz - sector[nSector].ceilingz) / 2));
			AddRunRec(channel[nChannel].head,
					BuildElevC(0, nChannel, nSector, FindWallSprites(nSector), 200, v270, 2, sector[nSector].ceilingz,
							sector[nSector].ceilingz + (sector[nSector].floorz - sector[nSector].ceilingz) / 2 - 8));
			AddRunRec(channel[nChannel].head, BuildSwReady(nChannel, BuildLink(2, 1, 0)));
			break;
		case 51:
			AddRunRec(channel[nChannel].head,
					BuildElevF(nChannel, nSector, FindWallSprites(nSector), v270, v270, 2,
							sector[nSector].ceilingz + (sector[nSector].floorz - sector[nSector].ceilingz) / 2,
							sector[nSector].floorz));
			AddRunRec(channel[nChannel].head,
					BuildElevC(0, nChannel, nSector, FindWallSprites(nSector), v270, v270, 2,
							sector[nSector].ceilingz + (sector[nSector].floorz - sector[nSector].ceilingz) / 2,
							sector[nSector].ceilingz));
			AddRunRec(channel[nChannel].head, BuildSwPressSector(nChannel, BuildLink(1, 1), nSector, var_24));
			AddRunRec(channel[nChannel].head, BuildSwPause(nChannel, BuildLink(2, -1, 0), 60));
			break;
		case 52:
			AddRunRec(channel[nChannel].head,
					BuildElevC(0, nChannel, nSector, FindWallSprites(nSector), v270, v270, 2,
							sector[nSector].ceilingz + (sector[nSector].floorz - sector[nSector].ceilingz) / 2,
							sector[nSector].floorz));
			AddRunRec(channel[nChannel].head,
					BuildElevC(0, nChannel, nSector, FindWallSprites(nSector), v270, v270, 2,
							sector[nSector].ceilingz + (sector[nSector].floorz - sector[nSector].ceilingz) / 2,
							sector[nSector].ceilingz));
			AddRunRec(channel[nChannel].head, BuildSwPause(nChannel, BuildLink(2, -1, 0), 150));
			break;
		case 53:
			if ((nNext = engine.nextsectorneighborz(nSector, sector[nSector].ceilingz, -1, -1)) == -1)
				break;

			AddRunRec(channel[nChannel].head, BuildElevC(0, nChannel, nSector, FindWallSprites(nSector), v270, v270, 2,
					sector[nSector].floorz, sector[nNext].ceilingz));
			AddRunRec(channel[nChannel].head, BuildSwPressSector(nChannel, BuildLink(1, 1), nSector, var_24));
			break;
		case 54:
			if ((nNext = engine.nextsectorneighborz(nSector, sector[nSector].ceilingz, -1, -1)) == -1)
				break;

			AddRunRec(channel[nChannel].head, BuildElevC(0, nChannel, nSector, FindWallSprites(nSector), v270, v270, 2,
					sector[nSector].floorz, sector[nNext].ceilingz));
			AddRunRec(channel[nChannel].head, BuildSwPressSector(nChannel, BuildLink(1, 1), nSector, var_24));
			break;
		case 55:
			if ((nNext = engine.nextsectorneighborz(nSector, sector[nSector].ceilingz, -1, -1)) == -1)
				break;

			AddRunRec(channel[nChannel].head, BuildElevC(0, nChannel, nSector, FindWallSprites(nSector), v270, v270, 2,
					sector[nSector].floorz, sector[nNext].ceilingz));
			break;
		case 56:
			if ((nNext = engine.nextsectorneighborz(nSector, sector[nSector].floorz, 1, 1)) == -1)
				break;

			AddRunRec(channel[nChannel].head, BuildElevF(nChannel, nSector, FindWallSprites(nSector), v270, v270, 2,
					sector[nSector].ceilingz, sector[nNext].floorz));
			break;
		case 57:
			AddRunRec(channel[nChannel].head, BuildSwPressSector(nChannel, BuildLink(1, 1), nSector, var_24));
		case 62:
			if ((nLotag % 1000) == 63)
				nEnergyChan = nChannel;

			if ((nNext = engine.nextsectorneighborz(nSector, sector[nSector].ceilingz, -1, -1)) == -1)
				break;

			AddRunRec(channel[nChannel].head, BuildElevC(0, nChannel, nSector, FindWallSprites(nSector), v270, v270, 2,
					sector[nSector].floorz, sector[nNext].ceilingz));
			break;
		case 58:
			if ((nNext = engine.nextsectorneighborz(nSector, sector[nSector].floorz, 1, 1)) == -1)
				break;

			AddRunRec(channel[nChannel].head, BuildElevF(nChannel, nSector, FindWallSprites(nSector), v270, v270, 2,
					sector[nSector].floorz, sector[nNext].floorz));
			AddRunRec(channel[nChannel].head, BuildSwStepOn(nChannel, BuildLink(1, 1), nSector));
			AddRunRec(channel[nChannel].head, BuildSwNotOnPause(nChannel, BuildLink(1, 1), nSector, 60));
			break;
		case 60:
			v263[0] = sector[nSector].floorz;
			int v212 = nSector;
			for (i = 1; i < 8; i++) {
				v212 = engine.nextsectorneighborz(nSector, sector[v212].floorz, 1, -1);
				if (v212 < 0)
					break;
				v263[i] = sector[v212].floorz;
			}
			AddRunRec(channel[nChannel].head,
					BuildElevF(nChannel, nSector, FindWallSprites(nSector), v270, v270, i, v263));
			break;
		case 61:
			int v220 = nSector;
			v263[0] = sector[nSector].floorz;
			for (i = 1; i < 8; i++) {
				v220 = engine.nextsectorneighborz(nSector, sector[v220].floorz, 1, 1);
				if (v220 < 0)
					break;
				v263[i] = sector[v220].floorz;
			}
			AddRunRec(channel[nChannel].head,
					BuildElevF(nChannel, nSector, FindWallSprites(nSector), v270, v270, i, v263));
			break;
		case 63:
			AddRunRec(channel[nChannel].head, BuildSwStepOn(nChannel, BuildLink(2, 0, 0), nSector));
			break;
		case 74:
			AddRunRec(channel[nChannel].head, BuildElevC(0, nChannel, nSector, FindWallSprites(nSector), v270, v270, 2,
					sector[nSector].ceilingz, sector[nSector].floorz));
			break;
		case 67:
			if ((nNext = engine.nextsectorneighborz(nSector, sector[nSector].floorz, 1, 1)) == -1)
				break;

			AddRunRec(channel[nChannel].head, BuildElevF(nChannel, nSector, FindWallSprites(nSector), v270, v270, 2,
					sector[nSector].floorz, sector[nNext].floorz));
			break;
		case 22:
			if ((nNext = engine.nextsectorneighborz(nSector, sector[nSector].floorz, 1, 1)) == -1)
				break;

			AddRunRec(channel[nChannel].head, BuildElevF(nChannel, nSector, FindWallSprites(nSector), 0x7FFF, 200, 2,
					sector[nSector].floorz, sector[nNext].floorz));
			AddRunRec(channel[nChannel].head, BuildSwPause(nChannel, BuildLink(2, -1, 0), 60 * v269));
			break;
		case 49:
			if ((nNext = engine.nextsectorneighborz(nSector, sector[nSector].floorz, 1, 1)) == -1)
				break;

			AddRunRec(channel[nChannel].head, BuildElevF(nChannel, nSector, FindWallSprites(nSector), 0x7FFF, 200, 2,
					sector[nNext].floorz, sector[nSector].floorz));
			break;
		case 69:
		case 70:
			if ((nNext = engine.nextsectorneighborz(nSector, sector[nSector].ceilingz, -1, -1)) == -1)
				break;

			AddRunRec(channel[nChannel].head, BuildElevC(0, nChannel, nSector, FindWallSprites(nSector), v270, v270, 2,
					sector[nSector].floorz, sector[nNext].ceilingz));
			AddRunRec(channel[nChannel].head, BuildSwPressSector(nChannel, BuildLink(1, 1), nSector, var_24));
			AddRunRec(channel[nChannel].head, BuildSwPause(nChannel, BuildLink(2, -1, 0), 60));
			break;
		case 79:
			SectFlag[nSector] |= 0x8000;
			break;
		default:
			return;
		}
	}

	private static int GrabPushBlock() {
		if (nPushBlocks < 100) {
			if (sBlockInfo[nPushBlocks] == null)
				sBlockInfo[nPushBlocks] = new BlockInfo();
			else
				sBlockInfo[nPushBlocks].clear();
			return nPushBlocks++;
		}
		return -1;
	}
	
	public static ByteBuffer savePushBlocks()
	{
		ByteBuffer bb = ByteBuffer.allocate(2 + (BlockInfo.size * nPushBlocks));
		bb.order(ByteOrder.LITTLE_ENDIAN);
		
		bb.putShort((short)nPushBlocks);
		for(int i = 0; i < nPushBlocks; i++)
			sBlockInfo[i].save(bb);

		return bb;
	}
	
	public static void loadPushBlocks(SafeLoader loader, Resource bb)
	{
		if(bb != null) {
			loader.nPushBlocks = bb.readShort();
			for(int i = 0; i < loader.nPushBlocks; i++) {
				if(loader.sBlockInfo[i] == null)
					loader.sBlockInfo[i] = new BlockInfo();
				loader.sBlockInfo[i].load(bb);
			}
		}
		else
		{
			nPushBlocks = loader.nPushBlocks;
			for(int i = 0; i < loader.nPushBlocks; i++) {
				if(sBlockInfo[i] == null)
					sBlockInfo[i] = new BlockInfo();
				sBlockInfo[i].copy(loader.sBlockInfo[i]);
			}
		}
	}

	private static void CreatePushBlock(int a1) {
		int block = GrabPushBlock();
		BlockInfo binfo = sBlockInfo[block];

		SECTOR sec = sector[a1];
		int wx = 0, wy = 0;
		for (int i = sec.wallptr; i < sec.wallptr + sec.wallnum; i++) {
			wx += wall[i].x;
			wy += wall[i].y;
		}

		binfo.cx = wx / sec.wallnum;
		binfo.cy = wy / sec.wallnum;

		short i = engine.insertsprite((short) a1, (short) 0);
		binfo.sprite = i;

		SPRITE spr = sprite[i];
		spr.x = binfo.cx;
		spr.y = binfo.cy;
		spr.z = sector[a1].floorz - 256;
		spr.cstat = (short) 32768;

		int clipdist = 0;
		for (int j = sec.wallptr; j < sec.wallptr + sec.wallnum; j++) {
			int dist = engine.ksqrt(
					(binfo.cx - wall[j].x) * (binfo.cx - wall[j].x) + (binfo.cy - wall[j].y) * (binfo.cy - wall[j].y));
			if (dist > clipdist)
				clipdist = dist;
		}

		binfo.field_8 = clipdist;
		spr.clipdist = (4 * clipdist) & 0xFF;
		sec.extra = (short) block;
	}

	public static void AddMovingSector(int a1, int a2, int a3, int a4) {
		if (nMoveSects >= 50) {
			game.ThrowError("Too many moving sectors");
			return;
		}

		CreatePushBlock(a1);
		sMoveDir[nMoveSects] = 1;
		if (sMoveSect[nMoveSects] == null)
			sMoveSect[nMoveSects] = new MoveSectStruct();
		
		MoveSectStruct v10 = sMoveSect[nMoveSects++];
		v10.field_2 = (short) FindTrail(a3);
		v10.field_4 = -1;
		v10.field_8 = -1;
		v10.field_6 = (short) a4;
		v10.field_A = (short) (a2 / 1000 + 1);
		v10.field_0 = (short) a1;
		if ((a4 & 8) != 0)
			v10.field_E = (short) AllocChannel(a3 % 1000);
		else
			v10.field_E = -1;
		sector[a1].floorstat |= 0x40;
	}
	
	public static ByteBuffer saveMoves()
	{
		ByteBuffer bb = ByteBuffer.allocate(2 + nMoveSects * ( MoveSectStruct.size) + 50);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		
		bb.putShort((short)nMoveSects);
		for(int i = 0; i < 50; i++)
			bb.put(sMoveDir[i]);
		for(int i = 0; i < nMoveSects; i++)
			sMoveSect[i].save(bb);
		return bb;
	}
	
	public static void loadMoves(SafeLoader loader, Resource bb)
	{
		if(bb != null) {
			loader.nMoveSects = bb.readShort();
			for(int i = 0; i < 50; i++)
				loader.sMoveDir[i] = bb.readByte();
			for(int i = 0; i < loader.nMoveSects; i++)
			{
				if(loader.sMoveSect[i] == null)
					loader.sMoveSect[i] = new MoveSectStruct();
				loader.sMoveSect[i].load(bb);
			}
		}
		else
		{
			nMoveSects = loader.nMoveSects;
			System.arraycopy(loader.sMoveDir, 0, sMoveDir, 0, nMoveSects);
			for(int i = 0; i < loader.nMoveSects; i++)
			{
				if(sMoveSect[i] == null)
					sMoveSect[i] = new MoveSectStruct();
				sMoveSect[i].copy(loader.sMoveSect[i]);
			}
		}
	}

	private static int BuildEnergyBlock(int nSector) {
		SECTOR sec = sector[nSector];
		int wx = 0, wy = 0;
		for (int i = sec.wallptr; i < sec.wallptr + sec.wallnum; i++) {
			wx += wall[i].x;
			wy += wall[i].y;

			wall[i].pal = 0;
			wall[i].shade = 50;
			wall[i].picnum = 3621;
		}

		int cx = wx / sec.wallnum;
		int cy = wy / sec.wallnum;
		short nextsec = wall[sec.wallptr].nextsector;
		short i = engine.insertsprite((short) nSector, (short) 406);

		sprite[i].x = cx;
		sprite[i].y = cy;
		sprite[i].z = sector[nextsec].floorz;
		sprite[i].xrepeat = (short) BClipHigh((sector[nextsec].floorz - sec.floorz) >> 8, 255);
		sprite[i].cstat = (short) 32768;
		sprite[i].xvel = 0;
		sprite[i].yvel = 0;
		sprite[i].zvel = 0;
		sprite[i].lotag = (short) (HeadRun() + 1);
		sprite[i].hitag = 0;
		sprite[i].owner = (short) AddRunRec(sprite[i].lotag - 1, 0x250000 | i);
		sprite[i].extra = -1;

		sec.extra = i;
		nEnergyBlocks++;

		return i;
	}

	public static void FuncEnergyBlock(int a1, int nDamage, int RunPtr) {
		short nEnergy = (short) (RunData[RunPtr].RunEvent & 0xFFFF);
		short nSector = sprite[nEnergy].sectnum;
		if (sector[nSector].extra != -1) {
			switch (a1 & 0x7F0000) {
			case nEventRadialDamage:
				sector[nSector].floorz = sprite[nEnergy].z;
				sprite[nEnergy].z -= 256;
				nDamage = CheckRadialDamage(nEnergy);
				sprite[nEnergy].z += 256;
				sector[nSector].floorz = sector[nSector].floorz;
			case nEventDamage:
				if (nDamage > 0) {
					nDamage >>= 2;
					if (nDamage < sprite[nEnergy].xrepeat) {
						sprite[nEnergy].xrepeat -= nDamage;
						short spr = engine.insertsprite((short) lasthitsect, (short) 0);
						sprite[spr].ang = (short) a1;
						sprite[spr].x = lasthitx;
						sprite[spr].y = lasthity;
						sprite[spr].z = lasthitz;
						BuildSpark(spr, 0);
						engine.mydeletesprite(spr);
					} else {
						sprite[nEnergy].xrepeat = 0;
						ExplodeEnergyBlock(nEnergy);
					}
				}
				return;
			}
		}
	}

	private static void ExplodeEnergyBlock(short nEnergy) {
		SECTOR pSector = sector[sprite[nEnergy].sectnum];

		int wallptr = pSector.wallptr;
		for (int i = 0; i < pSector.wallnum; i++) {
			WALL pNext = wall[wall[wallptr++].nextwall];
			if (pNext.pal >= 4)
				pNext.pal = 7;
			else
				pNext.pal = 0;
			pNext.shade = 50;
		}

		if (pSector.floorpal >= 4)
			pSector.floorpal = 7;
		else
			pSector.floorpal = 0;
		pSector.floorshade = 50;
		pSector.extra = -1;
		pSector.floorz = sprite[nEnergy].z;
		sprite[nEnergy].z = (sprite[nEnergy].z + pSector.floorz) / 2;
		BuildSpark(nEnergy, 3);
		sprite[nEnergy].cstat = 0;
		sprite[nEnergy].xrepeat = 100;
		PlayFX2(StaticSound[78], nEnergy);
		sprite[nEnergy].xrepeat = 0;
		nEnergyTowers--;
		for (int i = 0; i < 20; i++) {
			sprite[nEnergy].ang = (short) RandomSize(11);
			BuildSpark(nEnergy, 1);
		}
		TintPalette(16, 16, 16);
		if (nEnergyTowers == 1) {
			ChangeChannel(nEnergyChan, 1);
			StatusMessage(1000, "TAKE OUT THE CONTROL CENTER!", -1);
		} else if (nEnergyTowers != 0) {
			StatusMessage(500, nEnergyTowers + " TOWERS REMAINING", -1);
		} else {
			nFinaleSpr = nEnergy;
			lFinaleStart = BClipLow(totalclock, 1);

			for (int s = 0, w; s < numsectors; s++) {
				SECTOR pSec = sector[s];
				if (pSec.ceilingpal == 1)
					pSec.ceilingpal = 0;
				if (pSec.floorpal == 1)
					pSec.floorpal = 0;

				for (w = pSec.wallptr; w < pSec.wallptr + pSec.wallnum; w++) {
					if (wall[w].pal == 1)
						wall[w].pal = 0;
				}
			}
			KillCreatures();
		}
		engine.changespritestat(nEnergy, (short) 0);
	}

	private static void KillCreatures() {
		for (int statnum = 99; statnum < 108; statnum++) {
			if (statnum != 100) {
				for (int i = headspritestat[statnum]; i != -1; i = nextspritestat[i]) {
					DamageEnemy(i, -1, 1600);
				}
			}
		}
	}
}
