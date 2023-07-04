// This file is part of LSPGDX.
// Copyright (C) 2020  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// LSPGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// LSPGDX is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with LSPGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.LSP;

import static ru.m210projects.Build.Net.Mmulti.myconnectindex;
import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Build.Gameutils.BClampAngle;
import static ru.m210projects.Build.Gameutils.BClipHigh;
import static ru.m210projects.Build.Gameutils.BClipLow;
import static ru.m210projects.Build.Gameutils.BClipRange;
import static ru.m210projects.Build.Gameutils.*;
import static ru.m210projects.Build.Pragmas.*;
import static ru.m210projects.Build.Strhandler.Bitoa;
import static ru.m210projects.Build.Strhandler.buildString;
import static ru.m210projects.LSP.Main.*;
import static ru.m210projects.LSP.Globals.*;
import static ru.m210projects.LSP.Weapons.*;
import static ru.m210projects.LSP.Enemies.*;

import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Build.Pattern.BuildFont;
import ru.m210projects.Build.Pattern.BuildFont.TextAlign;
import ru.m210projects.Build.Pattern.Tools.Interpolation.ILoc;
import ru.m210projects.Build.Render.GLRenderer.GLInvalidateFlag;
import ru.m210projects.Build.Settings.BuildSettings;
import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Build.Types.Tile;

public class View {

	public static final int kView2D = 2;
	public static final int kView3D = 3;
	public static final int kView2DIcon = 4;
	public static int[] zofslope = new int[2];

	public static int nPalDiff, nPalDelay;
	public static int rtint, btint, gtint;
	public static byte[] curpal = new byte[768];

	public static void drawscreen(int snum, int dasmoothratio) {
		int cposx = gPlayer[snum].x;
		int cposy = gPlayer[snum].y;
		int cposz = gPlayer[snum].z;
		float choriz = gPlayer[snum].horiz + gPlayer[snum].nBobbing;
		int czoom = gPlayer[snum].zoom;
		float cang = gPlayer[snum].ang;
		short csect = gPlayer[snum].sectnum;

		if ((!game.menu.gShowMenu && !Console.IsShown()) || recstat == 2) {
			int ix = gPlayer[snum].ox;
			int iy = gPlayer[snum].oy;
			int iz = gPlayer[snum].oz;
			float iHoriz = gPlayer[snum].ohoriz;
			float inAngle = gPlayer[snum].oang;
			int izoom = gPlayer[snum].ozoom;

			ix += mulscale(cposx - gPlayer[snum].ox, dasmoothratio, 16);
			iy += mulscale(cposy - gPlayer[snum].oy, dasmoothratio, 16);
			iz += mulscale(cposz - gPlayer[snum].oz, dasmoothratio, 16);
			iHoriz += ((choriz - gPlayer[snum].ohoriz) * dasmoothratio) / 65536.0f;
			inAngle += ((BClampAngle(cang - gPlayer[snum].oang + 1024) - 1024) * dasmoothratio) / 65536.0f;
			izoom += mulscale(czoom - gPlayer[snum].ozoom, dasmoothratio, 16);

			cposx = ix;
			cposy = iy;
			cposz = iz;
			czoom = izoom;

			choriz = iHoriz;
			cang = inAngle;
		}

		engine.getzsofslope(csect, cposx, cposy, zofslope);
		int lz = 4 << 8;
		if (cposz < zofslope[CEIL] + lz)
			cposz = zofslope[CEIL] + lz;
		if (cposz > zofslope[FLOOR] - lz)
			cposz = zofslope[FLOOR] - lz;

		if (gPlayer[snum].gViewMode != kView2DIcon) {
			engine.drawrooms(cposx, cposy, cposz, cang, choriz, csect);
			analyzesprites(snum, dasmoothratio);
			engine.drawmasks();

			for (int i = 0; i < numsectors; i++)
				if ((gotsector[i >> 3] & pow2char[i & 7]) != 0)
					MarkSectorSeen(i);
		}

		if (gPlayer[snum].gViewMode != kView3D) {
			if (followmode) {
				cposx = followx;
				cposy = followy;
				cang = followa;
			}

			if (gPlayer[snum].gViewMode == kView2DIcon) {
				engine.clearview(0); // Clear screen to specified color
				engine.drawmapview(cposx, cposy, czoom, (int) cang);
			}
			engine.drawoverheadmap(cposx, cposy, czoom, (short) cang);
		}
	}

	public static void MarkSectorSeen(int sect) {
		if (!isValidSector(sect))
			return;
		if (((1 << (sect & 7)) & show2dsector[sect >> 3]) == 0) {
			show2dsector[sect >> 3] |= 1 << (sect & 7);

			int startwall = sector[sect].wallptr;
			int endwall = startwall + sector[sect].wallnum;

			for (int j = startwall; j < endwall; j++)
				show2dwall[j >> 3] |= (1 << (j & 7));
		}
	}

	public static void analyzesprites(int snum, int smoothratio) {
		for (int i = 0; i < spritesortcnt; i++) {
			SPRITE pTSprite = tsprite[i];
			if (pTSprite.owner == -1)
				continue;

			ILoc oldLoc = game.pInt.getsprinterpolate(pTSprite.owner);
			if (oldLoc != null) {
				int x = oldLoc.x;
				int y = oldLoc.y;
				int z = oldLoc.z;
				short nAngle = oldLoc.ang;

				// interpolate sprite position
				x += mulscale(pTSprite.x - oldLoc.x, smoothratio, 16);
				y += mulscale(pTSprite.y - oldLoc.y, smoothratio, 16);
				z += mulscale(pTSprite.z - oldLoc.z, smoothratio, 16);
				nAngle += mulscale(((pTSprite.ang - oldLoc.ang + 1024) & 0x7FF) - 1024, smoothratio, 16);

				pTSprite.x = x;
				pTSprite.y = y;
				pTSprite.z = z;
				pTSprite.ang = nAngle;
			}
		}

		int k;
		int dax = gPlayer[snum].x;
		int day = gPlayer[snum].y;

		for (int i = 0; i < spritesortcnt; i++) {
			SPRITE tspr = tsprite[i];
			short owner = tspr.owner;

			int dx = tspr.x - dax;
			int dy = tspr.y - day;

			k = engine.getangle(dx, dy);
			k = (((tspr.ang + 3072 + 128 - k) & 2047) >> 8) & 7;

			switch (tspr.picnum) {
			case 275:
			case 339:
				if (k <= 4) {
					tspr.picnum += (k + 3) & 4;
					tspr.cstat &= ~4;
				} else {
					tspr.picnum += ((8 - k + 3) & 4);
					tspr.cstat |= 4;
				}
				break;
			case BLUEDUDE:
			case GREENDUDE:
			case REDDUDE:
			case PURPLEDUDE:
			case YELLOWDUDE:
				if (sprite[owner].statnum == CHASE || sprite[owner].statnum == GUARD) {
					if (k <= 4) {
						tspr.picnum += 4 * k;
						tspr.cstat &= ~4;
					} else {
						tspr.picnum += 4 * (8 - k);
						tspr.cstat |= 4;
					}
				}

				if(tspr.statnum != 99) {
					if (spritesortcnt < (MAXSPRITESONSCREEN - 2)) {
						int fz = sector[sprite[owner].sectnum].floorz;
						if (fz > gPlayer[snum].z) {
							if (tsprite[spritesortcnt] == null)
								tsprite[spritesortcnt] = new SPRITE();
							SPRITE tshadow = tsprite[spritesortcnt];
							tshadow.set(tspr);
							int camangle = engine.getangle(dax - tshadow.x, day - tshadow.y);
							tshadow.x -= mulscale(sintable[(camangle + 512) & 2047], 100, 16);
							tshadow.y += mulscale(sintable[(camangle + 1024) & 2047], 100, 16);
							tshadow.z = fz + 1;
							tshadow.statnum = 99;

							tshadow.yrepeat = (short) (tspr.yrepeat >> 3);
							if (tshadow.yrepeat < 4)
								tshadow.yrepeat = 4;

							tshadow.shade = 127;
							tshadow.cstat |= 2;
							spritesortcnt++;
						}
					}
				}
				break;
			}
		}
	}

//	private static ConvertType getConvertType(int nFlags)
//	{
//		ConvertType type = ConvertType.Normal;
//		if((nFlags & 256) != 0)
//			type = ConvertType.AlignLeft;
//		if((nFlags & 512) != 0)
//			type = ConvertType.AlignRight;
//		if((nFlags & 1024) != 0)
//			type = ConvertType.Stretch;
//
//		return type;
//	}

	public static void drawhealth(int snum, int x, int y, int scale, int nFlags) {
		String name = "health: ";
		game.getFont(1).drawText(x, y, name, scale, 0, 175, TextAlign.Left, 8 | 256, true);
		x += game.getFont(1).getWidth(name, scale);

		int nGauge = gPlayer[snum].nHealth * engine.getTile(641).getWidth() / (2 * MAXHEALTH);
		engine.rotatesprite(x << 16, y << 16, scale, 0, 641, 0, 0, 8 | 16 | nFlags, 0, 0,
				x + scale(nGauge, scale, 65536), ydim);
	}

	public static void drawmana(int snum, int x, int y, int scale, int nFlags) {
		String name = "mana:  ";

		game.getFont(1).drawText(x, y, name, scale, 0, 197, TextAlign.Left, 8 | 256, true);
		x += game.getFont(1).getWidth(name, scale);

		Tile pic = engine.getTile(641);

		int nGauge = gPlayer[snum].nMana * pic.getWidth() / (2 * MAXMANNA);

		engine.rotatesprite((x - scale(pic.getWidth() / 2, scale, 65536) - 1) << 16, y << 16, scale, 0, 641, 0, 0,
				8 | 16 | nFlags, x, 0, x + scale(nGauge, scale, 65536), ydim);
	}

	public static void drawammo(int snum, int x, int y, int scale, int nFlags) {
		String name = "ammo:   ";
		Tile pic = engine.getTile(640);

		game.getFont(1).drawText(x, y, name, scale, 0, 120, TextAlign.Left, 8 | 256, true);
		x += game.getFont(1).getWidth(name, scale);

		int nGauge = gPlayer[snum].nAmmo[gPlayer[snum].nLastChoosedWeapon] * pic.getWidth() / (2 * MAXAMMO);
		engine.rotatesprite((x - scale(pic.getWidth() / 2, scale, 65536) - 1) << 16, y << 16, scale, 0, 640, 0, 0,
				8 | 16 | nFlags, x, 0, x + scale(nGauge, scale, 65536), ydim);
	}

	public static void drawbar(int x, int y, int scale, int snum) {
		float fscale = (scale / 65536.0f);

		int sx1 = (int) ((x - 6) * fscale);
		int sy1 = y + (int) (4 * fscale);
		int sx2 = (int) ((x + 230) * fscale);
		int sy2 = y + (int) (45 * fscale);
		engine.rotatesprite(sx1 << 16, sy1 << 16, scale, 0, 611, 64, 0, 1 | 8 | 16 | 32 | 256, 0, 0, sx2, sy2);

		x *= fscale;
		int yoffs = (int) (10 * fscale);
		if (gPlayer[snum].nWeapon > 6)
			drawmana(myconnectindex, x, y += yoffs, scale, 256);
		else
			drawammo(myconnectindex, x, y += yoffs, scale, 256);

		drawhealth(myconnectindex, x, y += yoffs, scale, 256);

		y += yoffs;
		for (int i = 1; i < 13; i++) {
			if (gPlayer[snum].nAmmo[i] != 0) {
				engine.rotatesprite((x + 1) << 16, (y + 1) << 16, scale, 0, getIcon(i), 48, 0, 8 | 16 | 256, 0, 0, xdim,
						ydim);
				engine.rotatesprite(x << 16, y << 16, scale, 0, getIcon(i), 0, 0, 8 | 16 | 256, 0, 0, xdim, ydim);
			} else {
				engine.rotatesprite((x + 1) << 16, (y + 1) << 16, scale, 0, getIcon(i), 48, 0, 8 | 16 | 256, 0, 0, xdim,
						ydim);
			}

			if (gPlayer[snum].nWeapon == i)
				engine.rotatesprite((x + 1) << 16, (y + (int) (13 * fscale)) << 16, scale / 2, 0, 9258, 0, 0,
						8 | 16 | 256, 0, 0, xdim, ydim);

			x += (int) (19 * fscale);
		}

	}

	public static void TintPalette(int r, int g, int b) {
		r = BClipRange(r, 0, 63);
		g = BClipRange(g, 0, 63);
		b = BClipRange(b, 0, 63);

		if (g != 0 && gtint > 64)
			return;

		gtint += g;

		if (r != 0 && rtint > 64)
			return;

		rtint += r;

		if (b != 0 && btint > 64)
			return;

		btint += b;

		int nDiff = r;
		if (nDiff <= g)
			nDiff = g;
		if (nDiff <= b)
			nDiff = b;
		nPalDiff += nDiff;

		if (engine.glrender() == null) {
			for (int i = 0; i < 256; i++) {
				curpal[3 * i + 0] = (byte) BClipHigh((curpal[3 * i + 0] & 0xFF) + r, 63);
				curpal[3 * i + 1] = (byte) BClipHigh((curpal[3 * i + 1] & 0xFF) + g, 63);
				curpal[3 * i + 2] = (byte) BClipHigh((curpal[3 * i + 2] & 0xFF) + b, 63);
			}
		}

		nPalDelay = 0;
	}

	public static void GrabPalette() {
		System.arraycopy(palette, 0, curpal, 0, 768);
		engine.setbrightness(BuildSettings.paletteGamma.get(), curpal, GLInvalidateFlag.All);

		nPalDiff = 0;
		nPalDelay = 0;
		btint = 0;
		gtint = 0;
		rtint = 0;
	}

	public static void FixPalette() {
		if (nPalDiff == 0)
			return;

		if (nPalDelay-- > 0)
			return;

		nPalDelay = 1;
		if (engine.glrender() == null) {
			for (int i = 0; i < 768; i++) {
				int dP = (curpal[i] & 0xFF) - (palette[i] & 0xFF);
				if (dP > 0) {
					if (dP <= 5)
						curpal[i] = palette[i];
					else
						curpal[i] -= 3;
				}
			}
			engine.setbrightness(BuildSettings.paletteGamma.get(), curpal, GLInvalidateFlag.All);
		} else {
			engine.setpalettefade(rtint, gtint, btint, 0);
		}

		nPalDiff = BClipLow(nPalDiff - 3, 0);
		rtint = BClipLow(rtint - 3, 0);
		gtint = BClipLow(gtint - 3, 0);
		btint = BClipLow(btint - 3, 0);
	}

	private static char[] statbuffer = new char[80];

	public static void viewDrawStats(int x, int y, int zoom) {
		if (cfg.gShowStat == 0 || cfg.gShowStat == 2 && gPlayer[myconnectindex].gViewMode == kView3D)
			return;

		int nBits = 256;
		float fscale = (zoom / 65536.0f);

		BuildFont f = game.getFont(1);
		int yoffset = (int) (2.5f * f.getHeight() * fscale);
		int sx1 = (int) ((x - 6) * fscale);
		int sy1 = y - (int) (4 * fscale) - yoffset;
		int sx2 = (int) ((x + (11 * f.getWidth('x') + 6)) * fscale);
		int sy2 = y - (int) fscale;

		engine.rotatesprite(sx1 << 16, sy1 << 16, zoom, 0, 611, 64, 0, 1 | 8 | 16 | 32 | nBits, 0, 0, sx2, sy2);

		buildString(statbuffer, 0, "k: ");
		int alignx = f.getWidth(statbuffer, zoom);

		x *= fscale;
		y -= yoffset;

		int statx = x;
		int staty = y;

		f.drawText(statx, staty, statbuffer, zoom, 0, 175, TextAlign.Left, 8 | nBits, true);

		int offs = Bitoa(nEnemyKills, statbuffer);
		offs = buildString(statbuffer, offs, " / ", nEnemyMax);
		f.drawText(statx += (alignx + 2), staty, statbuffer, zoom, 0, 4, TextAlign.Left, 8 | nBits, true);

		statx = x;
		staty = y + f.getHeight(zoom);

		buildString(statbuffer, 0, "t: ");
		f.drawText(statx, staty, statbuffer, zoom, 0, 175, TextAlign.Left, 8 | nBits, true);
		alignx = f.getWidth(statbuffer, zoom);

		int sec = (totalmoves / 30) % 60;
		int minutes = (totalmoves / (30 * 60)) % 60;
		int hours = (totalmoves / (30 * 3600)) % 60;

		offs = Bitoa(hours, statbuffer, 2);
		offs = buildString(statbuffer, offs, ":", minutes, 2);
		offs = buildString(statbuffer, offs, ":", sec, 2);
		f.drawText(statx += (alignx + 2), staty, statbuffer, zoom, 0, 4, TextAlign.Left, 8 | nBits, true);
	}
}
