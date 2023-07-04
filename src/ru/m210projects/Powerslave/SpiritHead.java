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

import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Build.Pragmas.*;
import static ru.m210projects.Powerslave.Globals.*;
import static ru.m210projects.Powerslave.Main.*;
import static ru.m210projects.Powerslave.Palette.*;
import static ru.m210projects.Powerslave.Random.*;
import static ru.m210projects.Powerslave.Light.*;
import static ru.m210projects.Powerslave.Sound.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.Types.SECTOR;
import ru.m210projects.Build.Types.Tile;

public class SpiritHead {

	public static int nSpiritSprite;
	public static short nHeadStage;

	private static short nSpiritRepeatX, nSpiritRepeatY;
	private static short nMouthTile;
	private static short nPixels;
	private static int nHeadTimeStart, lHeadStartClock, lNextStateChange;
	private static int nPixelsToShow;
	public static boolean nTalkTime;
	private static int nPupData;
	private static ByteBuffer pPupData;

	private static int word_964E6;
	private static int word_964E8;
	private static int word_964EA;
	private static int word_964EC;

	private static byte[] pixelval = new byte[10282];
	private static byte[] origx = new byte[10282];
	private static byte[] origy = new byte[10282];
	private static byte[] velx = new byte[10282];
	private static byte[] vely = new byte[10282];
	private static short[] curx = new short[10282];
	private static short[] cury = new short[10282];
	private static byte[] destvelx = new byte[10282];
	private static byte[] destvely = new byte[10282];

	public static void InitSpiritHead() {
		nPixels = 0;
		nSpiritRepeatX = sprite[nSpiritSprite].xrepeat;
		nSpiritRepeatY = sprite[nSpiritSprite].yrepeat;
		engine.loadtile(590);
		engine.loadtile(592);

		for (int i = 0; i < MAXSPRITES; i++) {
			if (sprite[i].statnum != 0)
				sprite[i].cstat |= 0x8000;
		}

		int ptr = 0;
		int ox = -48;
		for (int x = 0; x < 97; x++) {
			int oy = -53;
			for (int y = 0; y < 106; y++) {
				if (engine.getTile(592).data[ptr] != -1) {
					pixelval[nPixels] = engine.getTile(590).data[(106 * x) + y];
					origx[nPixels] = (byte) ox;
					origy[nPixels] = (byte) oy;
					curx[nPixels] = 0;
					cury[nPixels] = 0;
					vely[nPixels] = 0;
					velx[nPixels] = 0;
					destvelx[nPixels] = (byte) (RandomSize(2) + 1);
					if (velx[nPixels] > 0)
						destvelx[nPixels] = (byte) -destvelx[nPixels];
					destvely[nPixels] = (byte) (RandomSize(2) + 1);
					if (vely[nPixels] > 0)
						destvely[nPixels] = (byte) -destvely[nPixels];
					nPixels++;
				}
				ptr++;
				oy++;
			}
			ox++;
		}

		engine.allocatepermanenttile(591, 194, 212);
		Arrays.fill(engine.getTile(591).data, (byte) 255);

		sprite[nSpiritSprite].yrepeat = 140;
		sprite[nSpiritSprite].xrepeat = 140;
		sprite[nSpiritSprite].picnum = 591;
		sprite[nSpiritSprite].cstat &= ~0x8000;
		nHeadStage = 0;
		nHeadTimeStart = totalclock;
		nPixelsToShow = 0;

		if (levelnum == 1)
			playCDtrack(3, false);
		else
			playCDtrack(7, false);
		StartSwirlies();

		lNextStateChange = totalclock;
		lHeadStartClock = totalclock;

		if (levelnum > 0) {
			pPupData = ByteBuffer.wrap(BuildGdx.cache.getBytes("LEV" + levelnum + ".PUP", 0));
			pPupData.order(ByteOrder.LITTLE_ENDIAN);
			nPupData = pPupData.capacity();
		}

		nMouthTile = 0;
		nTalkTime = true;
	}

	public static void DoSpiritHead() {
		PlayerList[0].horiz += (nDestVertPan[0] - PlayerList[0].horiz) / 4;

		switch (nHeadStage) {
		case 0:
		case 1:
			Arrays.fill(engine.getTile(591).data, (byte) 255);
			break;
		case 5:
			if (lNextStateChange <= totalclock) {
				if (nPupData != 0) {
					short clock = pPupData.getShort();
					nPupData -= 2;
					if (nPupData > 0) {
						lNextStateChange = lHeadStartClock + clock - 10;
						nTalkTime = !nTalkTime;
					} else {
						nTalkTime = false;
						nPupData = 0;
					}
				} else if (!cfg.bSubtitles) {
					levelnew = levelnum + 1;
				}
			}

			if (--word_964E8 <= 0) {
				word_964EA = 2 * RandomBit();
				word_964E8 = RandomSize(5) + 4;
			}

			int tilenum = 592;
			if (--word_964EC < 3) {
				tilenum = 593;
				if (word_964EC <= 0)
					word_964EC = RandomSize(6) + 4;
			}

			CopyHeadToWorkTile(word_964EA + tilenum);

			if (nTalkTime) {
				if (nMouthTile < 2)
					nMouthTile++;
			} else if (nMouthTile != 0)
				nMouthTile--;

			if (nMouthTile != 0) {
				int srctile = nMouthTile + 598;
				byte[] src = engine.loadtile(srctile);
				Tile pic = engine.getTile(srctile);
				int sizx = pic.getWidth();
				int sizy = pic.getHeight();
				int workptr = 212 * (97 - sizx / 2) + 159 - sizy;
				int srcptr = 0;
				while (sizx > 0) {
					System.arraycopy(src, srcptr, engine.getTile(591).data, workptr, sizy);
					workptr += 212;
					srcptr += sizy;
					sizx--;
				}
			}

			engine.getrender().invalidatetile(591, -1, -1);
			return;
		}

		nPixelsToShow = 15 * (totalclock - nHeadTimeStart);
		if (nPixelsToShow > nPixels)
			nPixelsToShow = nPixels;

		switch (nHeadStage) {
		case 3:
			FixPalette();
			if (nPalDiff == 0) {
				nFreeze = 2;
				nHeadStage++;
			}
			return;
		case 0:
		case 1:
		case 2:
			UpdateSwirlies();
			if (sprite[nSpiritSprite].shade > -127)
				sprite[nSpiritSprite].shade--;
			if (--word_964E6 < 0) {
				DimSector(sprite[nSpiritSprite].sectnum);
				word_964E6 = 5;
			}

			if (nHeadStage == 0) {
				if (totalclock - nHeadTimeStart > 480) {
					nHeadStage = 1;
					nHeadTimeStart = totalclock + 480;
				}

				Tile pic = engine.getTile(591);

				for (int i = 0; i < nPixelsToShow; i++) {
					if (destvely[i] >= 0) {
						if (++vely[i] >= destvely[i]) {
							destvely[i] = (byte) -(RandomSize(2) + 1);
						}
					} else {
						if (--vely[i] <= destvely[i]) {
							destvely[i] = (byte) (RandomSize(2) + 1);
						}
					}

					if (destvelx[i] >= 0) {
						if (++velx[i] >= destvelx[i]) {
							destvelx[i] = (byte) -(RandomSize(2) + 1);
						}
					} else {
						if (--velx[i] <= destvelx[i]) {
							destvelx[i] = (byte) (RandomSize(2) + 1);
						}
					}

					int x = (curx[i] >> 8) + velx[i];
					if (x < 97) {
						if (x < -96) {
							x = 0;
							velx[i] = 0;
						}
					} else {
						x = 0;
						velx[i] = 0;
					}

					int y = (cury[i] >> 8) + vely[i];
					if (y < 106) {
						if (y < -105) {
							y = 0;
							vely[i] = 0;
						}
					} else {
						y = 0;
						vely[i] = 0;
					}

					curx[i] = (short) (x << 8);
					cury[i] = (short) (y << 8);

					pic.data[212 * (x + 97) + 106 + y] = pixelval[i++];
				}
				engine.getrender().invalidatetile(591, -1, -1);
			}

			if (nHeadStage == 1) {
				if (sprite[nSpiritSprite].xrepeat > nSpiritRepeatX) {
					sprite[nSpiritSprite].xrepeat -= 2;
					if (sprite[nSpiritSprite].xrepeat < nSpiritRepeatX)
						sprite[nSpiritSprite].xrepeat = nSpiritRepeatX;
				}
				if (sprite[nSpiritSprite].yrepeat > nSpiritRepeatY) {
					sprite[nSpiritSprite].yrepeat -= 2;
					if (sprite[nSpiritSprite].yrepeat < nSpiritRepeatY)
						sprite[nSpiritSprite].yrepeat = nSpiritRepeatY;
				}

				int nCount = 0;
				Tile pic = engine.getTile(591);

				for (int i = 0; i < nPixels; i++) {
					int dx, dy;
					if (origx[i] << 8 == curx[i] || klabs((origx[i] << 8) - curx[i]) >= 8)
						dx = ((origx[i] << 8) - curx[i]) >> 3;
					else {
						dx = 0;
						curx[i] = (short) (origx[i] << 8);
					}

					if (origy[i] << 8 == cury[i] || klabs((origy[i] << 8) - cury[i]) >= 8)
						dy = ((origy[i] << 8) - cury[i]) >> 3;
					else {
						dy = 0;
						cury[i] = (short) (origy[i] << 8);
					}

					if ((dx | dy) != 0) {
						curx[i] += dx;
						cury[i] += dy;
						nCount++;
					}

					pic.data[((cury[i] >> 8) + (212 * ((curx[i] >> 8) + 97))) + 106] = pixelval[i];
				}

				if (totalclock - lHeadStartClock > 600)
					CopyHeadToWorkTile(590);

				if (nCount < (15 * nPixels) / 16) {
					SoundBigEntrance();
					AddGlow(sprite[nSpiritSprite].sectnum, 20);
					AddFlash(sprite[nSpiritSprite].sectnum, sprite[nSpiritSprite].x, sprite[nSpiritSprite].y,
							sprite[nSpiritSprite].z, 128);
					nHeadStage = 3;
					TintPalette(63, 63, 63);
					CopyHeadToWorkTile(592);
				}

				engine.getrender().invalidatetile(591, -1, -1);
			}
			break;
		}
	}

	private static void CopyHeadToWorkTile(int nTile) {
		byte[] src = engine.loadtile(nTile);

		int workptr = 10441;
		int srcptr = 0;
		Tile pic = engine.getTile(591);
		for (int i = 0; i < 97; i++) {
			System.arraycopy(src, srcptr, pic.data, workptr, 106);
			workptr += 212;
			srcptr += 106;
		}
	}

	private static void DimSector(int sectnum) {
		SECTOR pSector = sector[sectnum];
		int w = pSector.wallptr;
		for (int i = 0; i < pSector.wallnum; i++, w++) {
			if (wall[w].shade < 40)
				wall[w].shade++;
		}
		if (pSector.floorshade < 40)
			pSector.floorshade++;
		if (pSector.ceilingshade < 40)
			pSector.ceilingshade++;
	}

}
