// This file is part of BloodGDX.
// Copyright (C) 2017-2019  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// BloodGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// BloodGDX is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with BloodGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.Blood.Factory;

import static ru.m210projects.Blood.Globals.kFrameTicks;
import static ru.m210projects.Blood.Globals.kPalNormal;
import static ru.m210projects.Blood.Main.gDemoScreen;
import static ru.m210projects.Blood.Main.gGameScreen;
import static ru.m210projects.Blood.Main.gPrecacheScreen;
import static ru.m210projects.Blood.Main.game;
import static ru.m210projects.Blood.Screen.curPalette;
import static ru.m210projects.Blood.Screen.gGammaLevels;
import static ru.m210projects.Blood.Screen.gammaTable;
import static ru.m210projects.Blood.Screen.scrFindClosestColor;
import static ru.m210projects.Blood.Screen.scrLoadPalette;
import static ru.m210projects.Blood.Screen.scrSetPalette;
import static ru.m210projects.Blood.Trig.Cos;
import static ru.m210projects.Blood.Trig.Sin;
import static ru.m210projects.Blood.Types.DEMO.IsOriginalDemo;
import static ru.m210projects.Blood.VERSION.SHAREWARE;
import static ru.m210projects.Blood.View.PaletteView;
import static ru.m210projects.Build.Gameutils.BClipRange;
import static ru.m210projects.Build.OnSceenDisplay.Console.OSDTEXT_RED;
import static ru.m210projects.Build.Pragmas.divscale;
import static ru.m210projects.Build.Pragmas.dmulscale;
import static ru.m210projects.Build.Pragmas.dmulscaler;
import static ru.m210projects.Build.Pragmas.klabs;
import static ru.m210projects.Build.Pragmas.mulscale;

import com.badlogic.gdx.Screen;

import ru.m210projects.Blood.DB;
import ru.m210projects.Blood.Types.BloodTile;
import ru.m210projects.Build.Architecture.BuildFrame.FrameType;
import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Build.Pattern.BuildEngine;
import ru.m210projects.Build.Pattern.BuildGame;
import ru.m210projects.Build.Pattern.ScreenAdapters.GameAdapter;


import ru.m210projects.Build.Render.Renderer;
import ru.m210projects.Build.Render.Renderer.RenderType;
import ru.m210projects.Build.Settings.BuildSettings.IntVar;
import ru.m210projects.Build.Types.BuildVariable;
import ru.m210projects.Build.Types.SECTOR;
import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Build.Types.Tile;
import ru.m210projects.Build.Types.WALL;

public class BloodEngine extends BuildEngine {

	public BuildVariable<Integer> fastDemo;

	public BloodEngine(BuildGame game) throws Exception {
		super(game, kFrameTicks);
		if (SHAREWARE)
			this.tilesPath = "shareXXX.art";
		fastDemo = new IntVar(0, "FastDemo");

		if (BuildGdx.cache.getGroup("blood.rff") == null || BuildGdx.cache.getGroup("sounds.rff") == null)
			throw new Exception("Looks like you have tried to launch unsupported version of Blood");
	}

	@Override
	public BloodTile getTile(int tilenum) {
		if (tiles[tilenum] == null)
			tiles[tilenum] = new BloodTile();
		return (BloodTile) tiles[tilenum];
	}

	@Override
	public void setbrightness(int nGamma, byte[] dapal, boolean flags) {
		curbrightness = BClipRange(nGamma, 0, gGammaLevels);

		if (curbrightness != 0) {
			for (int i = 0; i < dapal.length; i++)
				temppal[i] = gammaTable[nGamma][dapal[i] & 0xFF];
			changepalette(temppal);
		} else
			changepalette(dapal);
	}

	@Override
	public boolean setrendermode(Renderer render) {
		if (this.render != null && this.render != render) {
			if (render.getType() != RenderType.Software) {
				final Screen screen = game.getScreen();
				if (screen instanceof GameAdapter) {
					gPrecacheScreen.init(true, new Runnable() {
						@Override
						public void run() {
							game.changeScreen(screen);
							if (game.isCurrentScreen(gGameScreen))
								game.net.ready2send = true;
						}
					});
					game.changeScreen(gPrecacheScreen);
				}
			}

			PaletteView = kPalNormal;
			scrSetPalette(PaletteView);
		}

		return super.setrendermode(render);
	}

	@Override
	public byte[] loadtile(int nTile) {
		if (nTile < 0 || nTile >= MAXTILES)
			return null;

		Tile pic = getTile(nTile);
		if (pic.data == null)
			super.loadtile(nTile);

		return pic.data;
	}

//	private boolean key = false;
	@Override
	public void sampletimer() {
		if (timerfreq == 0)
			return;

		long n = (getticks() * timerticspersec / timerfreq) - timerlastsample;
		if (n > 0) {
			if (game.isCurrentScreen(gDemoScreen) && fastDemo.get() != 0) {
				totalclock += fastDemo.get() * 4;
			} else
				totalclock += n;

			timerlastsample += n;
		}
	}

	@Override
	public byte getclosestcol(byte[] palette, int r, int g, int b) {
		int rgb = ((r << 12) | (g << 6) | b);
		Byte out = palcache[rgb & (palcache.length - 1)];
		if (out != null)
			return out;

		byte col = scrFindClosestColor(palette, r << 2, g << 2, b << 2);
		palcache[rgb & (palcache.length - 1)] = col;
		return col;
	}

	@Override
	public void loadpalette() {
		curPalette = kPalNormal;
		scrLoadPalette();

		Console.Println("Loading gamma correction table", 0);

		byte[] buf = BuildGdx.cache.getBytes("gamma.dat", 0);
		if (buf == null)
			Console.Println("Gamma table not found", OSDTEXT_RED);
		else {
			gGammaLevels = buf.length / 256;
			for (int i = 0; i < 16; i++)
				System.arraycopy(buf, i * 256 + 0, gammaTable[i], 0, 256);
		}
	}

	@Override
	public short setsprite(int spritenum, int newx, int newy, int newz) {
		return DB.setSprite(spritenum, newx, newy, newz);
	}

	@Override
	public void initspritelists() {
		DB.initspritelists();
	}

	@Override
	public short insertsprite(int sectnum, int statnum) {
		return DB.insertsprite((short) sectnum, (short) statnum);
	}

	@Override
	public short deletesprite(int spritenum) {
		DB.deletesprite((short) spritenum);
		return 0;
	}

	@Override
	public short changespritesect(int spritenum, int newsectnum) {
		return DB.changespritesect((short) spritenum, (short) newsectnum);
	}

	@Override
	public short changespritestat(int spritenum, int newstatnum) {
		return DB.changespritestat((short) spritenum, (short) newstatnum);
	}

	@Override
	public Point rotatepoint(int xpivot, int ypivot, int x, int y, int daang) {
		x -= xpivot;
		y -= ypivot;

		rotatepoint.set(dmulscaler(x, Cos(daang), -y, Sin(daang), 30) + xpivot,
				dmulscaler(y, Cos(daang), x, Sin(daang), 30) + ypivot, 0);
		return rotatepoint;
	}

	// Forcing original Blood bug
	@Override
	public int clipmove(int x, int y, int z, int sectnum, long xvect, long yvect, int walldist, int ceildist,
			int flordist, int cliptype) {
		clipmove_x = x;
		clipmove_y = y;
		clipmove_z = z;
		clipmove_sectnum = (short) sectnum;
		WALL wal, wal2;
		SPRITE spr;
		SECTOR sec, sec2;
		int i, templong1, templong2;
		long oxvect, oyvect;
		int lx, ly, retval;
		int intx, inty, goalx, goaly;

		int k, l, clipsectcnt, endwall, cstat;
		int x1, y1, x2, y2, cx, cy, rad, xmin, ymin, xmax, ymax;
		int bsz, xoff, yoff, xspan, yspan, cosang, sinang;
		int xrepeat, yrepeat, gx, gy, dx, dy, dasprclipmask, dawalclipmask;
		int cnt, clipyou;
		short hitwall, j, startwall, dasect;

		int dax, day, daz, daz2;

		if (((xvect | yvect) == 0) || (clipmove_sectnum < 0))
			return (0);
		retval = 0;

		oxvect = xvect;
		oyvect = yvect;

		goalx = clipmove_x + (int) (xvect >> 14);
		goaly = clipmove_y + (int) (yvect >> 14);

		clipnum = 0;

		cx = (clipmove_x + goalx) >> 1;
		cy = (clipmove_y + goaly) >> 1;
		// Extra walldist for sprites on sector lines
		gx = goalx - clipmove_x;
		gy = goaly - clipmove_y;
		rad = (ksqrt(gx * gx + gy * gy) + MAXCLIPDIST + walldist + 8);
		xmin = cx - rad;
		ymin = cy - rad;
		xmax = cx + rad;
		ymax = cy + rad;

		dawalclipmask = (cliptype & 65535); // CLIPMASK0 = 0x00010001
		dasprclipmask = (cliptype >> 16); // CLIPMASK1 = 0x01000040

		clipsectorlist[0] = clipmove_sectnum;
		clipsectcnt = 0;
		clipsectnum = 1;
		do {
			dasect = clipsectorlist[clipsectcnt++];
			sec = sector[dasect];
			startwall = sec.wallptr;
			endwall = startwall + sec.wallnum;
			if (startwall < 0 || endwall < 0) {
				clipsectcnt++;
				continue;
			}
			for (j = startwall; j < endwall; j++) {
				wal = wall[j];
				if (wal == null || wal.point2 < 0 || wal.point2 >= MAXWALLS)
					continue;
				wal2 = wall[wal.point2];
				if (wal2 == null)
					continue;
				if ((wal.x < xmin) && (wal2.x < xmin))
					continue;
				if ((wal.x > xmax) && (wal2.x > xmax))
					continue;
				if ((wal.y < ymin) && (wal2.y < ymin))
					continue;
				if ((wal.y > ymax) && (wal2.y > ymax))
					continue;

				x1 = wal.x;
				y1 = wal.y;
				x2 = wal2.x;
				y2 = wal2.y;

				dx = x2 - x1;
				dy = y2 - y1;
				if (dx * ((clipmove_y) - y1) < ((clipmove_x) - x1) * dy)
					continue; // If wall's not facing you

				if (dx > 0)
					dax = dx * (ymin - y1);
				else
					dax = dx * (ymax - y1);
				if (dy > 0)
					day = dy * (xmax - x1);
				else
					day = dy * (xmin - x1);
				if (dax >= day)
					continue;

				clipyou = 0;
				if ((wal.nextsector < 0) || ((wal.cstat & dawalclipmask) != 0)) {
					clipyou = 1;
				} else {
					Point out = rintersect(clipmove_x, clipmove_y, 0, gx, gy, 0, x1, y1, x2, y2);
					if (out == null) {
						dax = clipmove_x;
						day = clipmove_y;
					} else {
						dax = out.getX();
						day = out.getY();
					}

					daz = getflorzofslope(dasect, dax, day);
					daz2 = getflorzofslope(wal.nextsector, dax, day);

					sec2 = sector[wal.nextsector];
					if (sec2 == null)
						continue;
					if (daz2 < daz - (1 << 8))
						if ((sec2.floorstat & 1) == 0)
							if ((clipmove_z) >= daz2 - (flordist - 1))
								clipyou = 1;
					if (clipyou == 0) {
						daz = getceilzofslope(dasect, dax, day);
						daz2 = getceilzofslope(wal.nextsector, dax, day);
						if (daz2 > daz + (1 << 8))
							if ((sec2.ceilingstat & 1) == 0)
								if ((clipmove_z) <= daz2 + (ceildist - 1))
									clipyou = 1;
					}
				}

				if (clipyou == 1) {
					// Add 2 boxes at endpoints
					bsz = walldist;
					if (gx < 0)
						bsz = -bsz;
					addclipline(x1 - bsz, y1 - bsz, x1 - bsz, y1 + bsz, j + 32768);
					addclipline(x2 - bsz, y2 - bsz, x2 - bsz, y2 + bsz, j + 32768);
					bsz = walldist;
					if (gy < 0)
						bsz = -bsz;
					addclipline(x1 + bsz, y1 - bsz, x1 - bsz, y1 - bsz, j + 32768);
					addclipline(x2 + bsz, y2 - bsz, x2 - bsz, y2 - bsz, j + 32768);

					dax = walldist;
					if (dy > 0)
						dax = -dax;
					day = walldist;
					if (dx < 0)
						day = -day;
					addclipline(x1 + dax, y1 + day, x2 + dax, y2 + day, j + 32768);
				} else {
					for (i = clipsectnum - 1; i >= 0; i--)
						if (wal.nextsector == clipsectorlist[i])
							break;
					if (i < 0)
						clipsectorlist[clipsectnum++] = wal.nextsector;
				}
			}

			for (j = headspritesect[dasect]; j >= 0; j = nextspritesect[j]) {
				spr = sprite[j];

				Tile pic = getTile(spr.picnum);

				cstat = spr.cstat;

				if ((cstat & dasprclipmask) == 0)
					continue;

				x1 = spr.x;
				y1 = spr.y;
				switch (cstat & 48) {
				case 0:

					if ((x1 >= xmin) && (x1 <= xmax) && (y1 >= ymin) && (y1 <= ymax)) {
						k = ((pic.getHeight() * spr.yrepeat) << 2);
						if ((cstat & 128) != 0)
							daz = spr.z + (k >> 1);
						else
							daz = spr.z;
						if ((pic.anm & 0x00ff0000) != 0)
							daz -= (pic.getOffsetY() * spr.yrepeat << 2);

						if ((clipmove_z < (daz + ceildist)) && (clipmove_z > (daz - k - flordist))) {
							bsz = (spr.clipdist << 2) + walldist;
							if (gx < 0)
								bsz = -bsz;
							addclipline(x1 - bsz, y1 - bsz, x1 - bsz, y1 + bsz, j + 49152);
							bsz = (spr.clipdist << 2) + walldist;
							if (gy < 0)
								bsz = -bsz;
							addclipline(x1 + bsz, y1 - bsz, x1 - bsz, y1 - bsz, j + 49152);
						}
					}
					break;
				case 16:
					k = ((pic.getHeight() * spr.yrepeat) << 2);
					if ((cstat & 128) != 0)
						daz = spr.z + (k >> 1);
					else
						daz = spr.z;
					if ((pic.anm & 0x00ff0000) != 0)
						daz -= (pic.getOffsetY() * spr.yrepeat << 2);
					daz2 = daz - k;
					daz += ceildist;
					daz2 -= flordist;
					if (((clipmove_z) < daz) && ((clipmove_z) > daz2)) {
						// These lines get the 2 points of the rotated sprite
						// Given: (x1, y1) starts out as the center point
						xoff = (byte) (pic.getOffsetX() + spr.xoffset);
						if ((cstat & 4) > 0)
							xoff = -xoff;
						k = spr.ang;
						l = spr.xrepeat;
						dax = sintable[k & 2047] * l;
						day = sintable[(k + 1536) & 2047] * l;
						l = pic.getWidth();
						k = (l >> 1) + xoff;
						x1 -= mulscale(dax, k, 16);
						x2 = x1 + mulscale(dax, l, 16);
						y1 -= mulscale(day, k, 16);
						y2 = y1 + mulscale(day, l, 16);

						if (clipinsideboxline(cx, cy, x1, y1, x2, y2, rad) != 0) {
							dax = mulscale(sintable[(spr.ang + 256 + 512) & 2047], walldist, 14);
							day = mulscale(sintable[(spr.ang + 256) & 2047], walldist, 14);

							if ((x1 - (clipmove_x)) * (y2 - (clipmove_y)) >= (x2 - (clipmove_x)) * (y1 - (clipmove_y))) // Front
							{
								addclipline(x1 + dax, y1 + day, x2 + day, y2 - dax, j + 49152);
							} else {
								if ((cstat & 64) != 0)
									continue;
								addclipline(x2 - dax, y2 - day, x1 - day, y1 + dax, j + 49152);
							}

							// Side blocker
							if ((x2 - x1) * ((clipmove_x) - x1) + (y2 - y1) * ((clipmove_y) - y1) < 0) {
								addclipline(x1 - day, y1 + dax, x1 + dax, y1 + day, j + 49152);
							} else if ((x1 - x2) * ((clipmove_x) - x2) + (y1 - y2) * ((clipmove_y) - y2) < 0) {
								addclipline(x2 + day, y2 - dax, x2 - dax, y2 - day, j + 49152);
							}
						}
					}

					break;
				case 32:
					daz = spr.z + ceildist;
					daz2 = spr.z - flordist;
					if (((clipmove_z) < daz) && ((clipmove_z) > daz2)) {
						if ((cstat & 64) != 0)
							if (((clipmove_z) > spr.z) == ((cstat & 8) == 0))
								continue;

						xoff = (byte) (pic.getOffsetX() + (spr.xoffset));
						yoff = (byte) (pic.getOffsetY() + (spr.yoffset));
						if ((cstat & 4) > 0)
							xoff = -xoff;
						if ((cstat & 8) > 0)
							yoff = -yoff;

						k = spr.ang;
						cosang = sintable[(k + 512) & 2047];
						sinang = sintable[k & 2047];
						xspan = pic.getWidth();
						xrepeat = spr.xrepeat;
						yspan = pic.getHeight();
						yrepeat = spr.yrepeat;

						dax = ((xspan >> 1) + xoff) * xrepeat;
						day = ((yspan >> 1) + yoff) * yrepeat;
						rxi[0] = x1 + dmulscale(sinang, dax, cosang, day, 16);
						ryi[0] = y1 + dmulscale(sinang, day, -cosang, dax, 16);
						l = xspan * xrepeat;
						rxi[1] = rxi[0] - mulscale(sinang, l, 16);
						ryi[1] = ryi[0] + mulscale(cosang, l, 16);
						l = yspan * yrepeat;
						k = -mulscale(cosang, l, 16);
						rxi[2] = rxi[1] + k;
						rxi[3] = rxi[0] + k;
						k = -mulscale(sinang, l, 16);
						ryi[2] = ryi[1] + k;
						ryi[3] = ryi[0] + k;

						dax = mulscale(sintable[(spr.ang - 256 + 512) & 2047], walldist, 14);
						day = mulscale(sintable[(spr.ang - 256) & 2047], walldist, 14);

						if ((rxi[0] - (clipmove_x)) * (ryi[1] - (clipmove_y)) < (rxi[1] - (clipmove_x))
								* (ryi[0] - (clipmove_y))) {
							if (clipinsideboxline(cx, cy, rxi[1], ryi[1], rxi[0], ryi[0], rad) != 0)
								addclipline(rxi[1] - day, ryi[1] + dax, rxi[0] + dax, ryi[0] + day, j + 49152);
						} else if ((rxi[2] - (clipmove_x)) * (ryi[3] - (clipmove_y)) < (rxi[3] - (clipmove_x))
								* (ryi[2] - (clipmove_y))) {
							if (clipinsideboxline(cx, cy, rxi[3], ryi[3], rxi[2], ryi[2], rad) != 0)
								addclipline(rxi[3] + day, ryi[3] - dax, rxi[2] - dax, ryi[2] - day, j + 49152);
						}

						if ((rxi[1] - (clipmove_x)) * (ryi[2] - (clipmove_y)) < (rxi[2] - (clipmove_x))
								* (ryi[1] - (clipmove_y))) {
							if (clipinsideboxline(cx, cy, rxi[2], ryi[2], rxi[1], ryi[1], rad) != 0)
								addclipline(rxi[2] - dax, ryi[2] - day, rxi[1] - day, ryi[1] + dax, j + 49152);
						} else if ((rxi[3] - (clipmove_x)) * (ryi[0] - (clipmove_y)) < (rxi[0] - (clipmove_x))
								* (ryi[3] - (clipmove_y))) {
							if (clipinsideboxline(cx, cy, rxi[0], ryi[0], rxi[3], ryi[3], rad) != 0)
								addclipline(rxi[0] + dax, ryi[0] + day, rxi[3] + day, ryi[3] - dax, j + 49152);
						}
					}
					break;
				}
			}
		} while (clipsectcnt < clipsectnum);

		hitwall = 0;
		cnt = clipmoveboxtracenum;
		do {
			Clip out = raytrace(clipmove_x, clipmove_y, goalx, goaly);
			intx = out.getX();
			inty = out.getY();
			hitwall = out.getNum();

			if (hitwall >= 0) {
				lx = clipit[hitwall].x2 - clipit[hitwall].x1;
				ly = clipit[hitwall].y2 - clipit[hitwall].y1;
				templong2 = lx * lx + ly * ly;
				if (templong2 > 0) {
					templong1 = (goalx - intx) * lx + (goaly - inty) * ly;

					if ((klabs(templong1) >> 11) < templong2)
						i = divscale(templong1, templong2, 20);
					else
						i = 0;
					goalx = mulscale(lx, i, 20) + intx;
					goaly = mulscale(ly, i, 20) + inty;
				}

				templong1 = dmulscale(lx, oxvect, ly, oyvect, 6);
				for (i = cnt + 1; i <= clipmoveboxtracenum; i++) {
					j = hitwalls[i];
					templong2 = dmulscale(clipit[j].x2 - clipit[j].x1, oxvect, clipit[j].y2 - clipit[j].y1, oyvect, 6);
					if ((templong1 ^ templong2) < 0) {
						if (!IsOriginalDemo())
							clipmove_sectnum = updatesector(clipmove_x, clipmove_y, clipmove_sectnum);
						return (retval);
					}
				}

				Point goal = keepaway(goalx, goaly, hitwall);
				goalx = goal.getX();
				goaly = goal.getY();
				xvect = ((goalx - intx) << 14);
				yvect = ((goaly - inty) << 14);

				if (cnt == clipmoveboxtracenum)
					retval = clipobjectval[hitwall];
				hitwalls[cnt] = hitwall;
			}
			cnt--;

			clipmove_x = intx;
			clipmove_y = inty;
		} while (((xvect | yvect) != 0) && (hitwall >= 0) && (cnt > 0));

		for (j = 0; j < clipsectnum; j++)
			if (inside(clipmove_x, clipmove_y, clipsectorlist[j]) == 1) {
				clipmove_sectnum = clipsectorlist[j];
				return (retval);
			}

		clipmove_sectnum = -1;
		templong1 = 0x7fffffff;
		for (j = (short) (numsectors - 1); j >= 0; j--)
			if (inside(clipmove_x, clipmove_y, j) == 1) {
				if ((sector[j].ceilingstat & 2) != 0)
					templong2 = (getceilzofslope(j, clipmove_x, clipmove_y) - (clipmove_z));
				else
					templong2 = (sector[j].ceilingz - (clipmove_z));

				if (templong2 <= 0) {
					if ((sector[j].floorstat & 2) != 0)
						templong2 = ((clipmove_z) - getflorzofslope(j, clipmove_x, clipmove_y));
					else
						templong2 = ((clipmove_z) - sector[j].floorz);

					if (templong2 <= 0) {
						clipmove_sectnum = j;
						return (retval);
					}
				}
				if (templong2 < templong1) {
					clipmove_sectnum = j;
					templong1 = templong2;
				}
			}

		return (retval);
	}
}
