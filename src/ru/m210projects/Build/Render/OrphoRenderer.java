// This file is part of BuildGDX.
// Copyright (C) 2017-2018  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// BuildGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// BuildGDX is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with BuildGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.Build.Render;

import static ru.m210projects.Build.Engine.gotsector;
import static ru.m210projects.Build.Engine.headspritesect;
import static ru.m210projects.Build.Engine.nextspritesect;
import static ru.m210projects.Build.Engine.numsectors;
import static ru.m210projects.Build.Engine.sector;
import static ru.m210projects.Build.Engine.show2dsector;
import static ru.m210projects.Build.Engine.sintable;
import static ru.m210projects.Build.Engine.sprite;
import static ru.m210projects.Build.Engine.wall;
import static ru.m210projects.Build.Engine.windowx1;
import static ru.m210projects.Build.Engine.windowx2;
import static ru.m210projects.Build.Engine.windowy1;
import static ru.m210projects.Build.Engine.windowy2;
import static ru.m210projects.Build.Engine.xdim;
import static ru.m210projects.Build.Engine.ydim;
import static ru.m210projects.Build.Engine.yxaspect;
import static ru.m210projects.Build.Gameutils.isValidSector;
import static ru.m210projects.Build.Net.Mmulti.connecthead;
import static ru.m210projects.Build.Net.Mmulti.connectpoint2;
import static ru.m210projects.Build.Pragmas.dmulscale;
import static ru.m210projects.Build.Pragmas.mulscale;

import ru.m210projects.Build.Engine;
import ru.m210projects.Build.Gameutils;
import ru.m210projects.Build.Render.IOverheadMapSettings.MapView;
import ru.m210projects.Build.Render.Renderer.Transparent;
import ru.m210projects.Build.Types.SECTOR;
import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Build.Types.Tile;
import ru.m210projects.Build.Types.TileFont;
import ru.m210projects.Build.Types.WALL;

public abstract class OrphoRenderer {

	protected final Engine engine;

	public OrphoRenderer(Engine engine) {
		this(engine, null);
	}

	public OrphoRenderer(Engine engine, IOverheadMapSettings mapSettings) {
		this.engine = engine;
		if (mapSettings != null)
			this.mapSettings = mapSettings;
		else
			this.mapSettings = new DefaultMapSettings();
	}

	public abstract void init();

	public abstract void uninit();

	public abstract void printext(TileFont font, int xpos, int ypos, char[] text, int col, int shade, Transparent bit,
			float scale);

	public abstract void printext(int xpos, int ypos, int col, int backcol, char[] text, int fontsize, float scale);

	public abstract void drawline256(int x1, int y1, int x2, int y2, int col);

	public abstract void rotatesprite(int sx, int sy, int z, int a, int picnum, int dashade, int dapalnum, int dastat,
			int cx1, int cy1, int cx2, int cy2);

	public abstract void nextpage();

	public abstract void drawmapview(int dax, int day, int zoome, int ang);

	protected int getclipmask(int a, int b, int c, int d) { // Ken did this
		int bA = a < 0 ? 1 : 0;
		int bB = b < 0 ? 1 : 0;
		int bC = c < 0 ? 1 : 0;
		int bD = d < 0 ? 1 : 0;

		d = (bA * 8) + (bB * 4) + (bC * 2) + bD;
		return (((d << 4) ^ 0xf0) | d);
	}

	protected IOverheadMapSettings mapSettings;

	public void drawoverheadmap(int cposx, int cposy, int czoom, short cang) {
		int i, j, k, x1, y1, x2 = 0, y2 = 0, ox, oy;
		int startwall, endwall;
		int xvect, yvect, xvect2, yvect2;

		WALL wal;

		xvect = sintable[(-cang) & 2047] * czoom;
		yvect = sintable[(1536 - cang) & 2047] * czoom;
		xvect2 = mulscale(xvect, yxaspect, 16);
		yvect2 = mulscale(yvect, yxaspect, 16);

		// Draw red lines
		for (i = 0; i < numsectors; i++) {
			if ((!mapSettings.isFullMap() && (show2dsector[i >> 3] & (1 << (i & 7))) == 0)
					|| !Gameutils.isValidSector(i))
				continue;

			SECTOR sec = sector[i];
			if (!Gameutils.isValidWall(sec.wallptr) || sec.wallnum < 3)
				continue;

			startwall = sec.wallptr;
			endwall = sec.wallptr + sec.wallnum;

			if (startwall < 0 || endwall < 0)
				continue;

			for (j = startwall; j < endwall; j++) {
				if (!Gameutils.isValidWall(j) || !Gameutils.isValidWall(wall[j].point2))
					continue;

				wal = wall[j];
				if (mapSettings.isShowRedWalls() && Gameutils.isValidWall(wal.nextwall)) {
					if (Gameutils.isValidSector(wal.nextsector)) {
						if (mapSettings.isWallVisible(j, i)) {
							ox = mapSettings.getWallX(j) - cposx;
							oy = mapSettings.getWallY(j) - cposy;
							x1 = dmulscale(ox, xvect, -oy, yvect, 16) + (xdim << 11);
							y1 = dmulscale(oy, xvect2, ox, yvect2, 16) + (ydim << 11);

							ox = mapSettings.getWallX(wal.point2) - cposx;
							oy = mapSettings.getWallY(wal.point2) - cposy;
							x2 = dmulscale(ox, xvect, -oy, yvect, 16) + (xdim << 11);
							y2 = dmulscale(oy, xvect2, ox, yvect2, 16) + (ydim << 11);

							int col = mapSettings.getWallColor(j, i);
							if (col < 0)
								continue;

							drawline256(x1, y1, x2, y2, col);
						}
					}
				}
			}
		}

		// Draw sprites
		if (mapSettings.isShowSprites(MapView.Lines)) {
			for (i = 0; i < numsectors; i++) {
				if (!mapSettings.isFullMap() && (show2dsector[i >> 3] & (1 << (i & 7))) == 0)
					continue;

				for (j = headspritesect[i]; j >= 0; j = nextspritesect[j]) {
					SPRITE spr = sprite[j];

					if ((spr.cstat & 0x8000) != 0 || spr.xrepeat == 0 || spr.yrepeat == 0
							|| !mapSettings.isSpriteVisible(MapView.Lines, j))
						continue;

					switch (spr.cstat & 48) {
					case 0:
						if (mapSettings.isShowSprites(MapView.Lines) && ((gotsector[i >> 3] & (1 << (i & 7))) > 0)
								&& (czoom > 96)) {
							ox = mapSettings.getSpriteX(j) - cposx;
							oy = mapSettings.getSpriteY(j) - cposy;
							x1 = dmulscale(ox, xvect, -oy, yvect, 16);
							y1 = dmulscale(oy, xvect2, ox, yvect2, 16);
							int daang = (spr.ang - cang) & 2047;
							rotatesprite((x1 << 4) + (xdim << 15), (y1 << 4) + (ydim << 15),
									mulscale(czoom * spr.yrepeat, yxaspect, 16), daang, spr.picnum, spr.shade, spr.pal,
									(spr.cstat & 2) >> 1, windowx1, windowy1, windowx2, windowy2);
						}
						break;
					case 16: {
						Tile pic = engine.getTile(spr.picnum);

						x1 = mapSettings.getSpriteX(j);
						y1 = mapSettings.getSpriteY(j);
						byte xoff = (byte) (pic.getOffsetX() + spr.xoffset);
						if ((spr.cstat & 4) > 0)
							xoff = (byte) -xoff;
						k = spr.ang;
						int l = spr.xrepeat;
						int dax = sintable[k & 2047] * l;
						int day = sintable[(k + 1536) & 2047] * l;
						l = pic.getWidth();
						k = (l >> 1) + xoff;
						x1 -= mulscale(dax, k, 16);
						x2 = x1 + mulscale(dax, l, 16);
						y1 -= mulscale(day, k, 16);
						y2 = y1 + mulscale(day, l, 16);

						ox = x1 - cposx;
						oy = y1 - cposy;
						x1 = dmulscale(ox, xvect, -oy, yvect, 16);
						y1 = dmulscale(oy, xvect2, ox, yvect2, 16);

						ox = x2 - cposx;
						oy = y2 - cposy;
						x2 = dmulscale(ox, xvect, -oy, yvect, 16);
						y2 = dmulscale(oy, xvect2, ox, yvect2, 16);

						int col = mapSettings.getSpriteColor(j);
						if (col < 0)
							break;

						drawline256(x1 + (xdim << 11), y1 + (ydim << 11), x2 + (xdim << 11), y2 + (ydim << 11), col);
					}
						break;
					case 32: {
						Tile pic = engine.getTile(spr.picnum);

						byte xoff = (byte) (pic.getOffsetX() + spr.xoffset);
						byte yoff = (byte) (pic.getOffsetY() + spr.yoffset);
						if ((spr.cstat & 4) > 0)
							xoff = (byte) -xoff;
						if ((spr.cstat & 8) > 0)
							yoff = (byte) -yoff;

						k = spr.ang;
						int cosang = sintable[(k + 512) & 2047];
						int sinang = sintable[k & 2047];
						int xspan = pic.getWidth();
						int xrepeat = spr.xrepeat;
						int yspan = pic.getHeight();
						int yrepeat = spr.yrepeat;

						int dax = ((xspan >> 1) + xoff) * xrepeat;
						int day = ((yspan >> 1) + yoff) * yrepeat;
						x1 = mapSettings.getSpriteX(j) + dmulscale(sinang, dax, cosang, day, 16);
						y1 = mapSettings.getSpriteY(j) + dmulscale(sinang, day, -cosang, dax, 16);
						int l = xspan * xrepeat;
						x2 = x1 - mulscale(sinang, l, 16);
						y2 = y1 + mulscale(cosang, l, 16);
						l = yspan * yrepeat;
						k = -mulscale(cosang, l, 16);
						int x3 = x2 + k;
						int x4 = x1 + k;
						k = -mulscale(sinang, l, 16);
						int y3 = y2 + k;
						int y4 = y1 + k;

						ox = x1 - cposx;
						oy = y1 - cposy;
						x1 = dmulscale(ox, xvect, -oy, yvect, 16);
						y1 = dmulscale(oy, xvect2, ox, yvect2, 16);

						ox = x2 - cposx;
						oy = y2 - cposy;
						x2 = dmulscale(ox, xvect, -oy, yvect, 16);
						y2 = dmulscale(oy, xvect2, ox, yvect2, 16);

						ox = x3 - cposx;
						oy = y3 - cposy;
						x3 = dmulscale(ox, xvect, -oy, yvect, 16);
						y3 = dmulscale(oy, xvect2, ox, yvect2, 16);

						ox = x4 - cposx;
						oy = y4 - cposy;
						x4 = dmulscale(ox, xvect, -oy, yvect, 16);
						y4 = dmulscale(oy, xvect2, ox, yvect2, 16);

						int col = mapSettings.getSpriteColor(j);
						if (col < 0)
							break;

						drawline256(x1 + (xdim << 11), y1 + (ydim << 11), x2 + (xdim << 11), y2 + (ydim << 11), col);

						drawline256(x2 + (xdim << 11), y2 + (ydim << 11), x3 + (xdim << 11), y3 + (ydim << 11), col);

						drawline256(x3 + (xdim << 11), y3 + (ydim << 11), x4 + (xdim << 11), y4 + (ydim << 11), col);

						drawline256(x4 + (xdim << 11), y4 + (ydim << 11), x1 + (xdim << 11), y1 + (ydim << 11), col);
					}
						break;
					}
				}
			}
		}

		// Draw white lines
		for (i = 0; i < numsectors; i++) {

			if ((!mapSettings.isFullMap() && (show2dsector[i >> 3] & (1 << (i & 7))) == 0)
					|| !Gameutils.isValidSector(i))
				continue;

			startwall = sector[i].wallptr;
			endwall = sector[i].wallptr + sector[i].wallnum;

			if (startwall < 0 || endwall < 0)
				continue;

			k = -1;
			for (j = startwall; j < endwall; j++) {
				wal = wall[j];
				if (!Gameutils.isValidWall(j) || !Gameutils.isValidWall(wall[j].point2))
					continue;

				if (wal.nextwall >= 0)
					continue;
				Tile pic = engine.getTile(wal.picnum);
				if (!pic.hasSize())
					continue;

				if (j == k) {
					x1 = x2;
					y1 = y2;
				} else {
					ox = mapSettings.getWallX(j) - cposx;
					oy = mapSettings.getWallY(j) - cposy;
					x1 = dmulscale(ox, xvect, -oy, yvect, 16) + (xdim << 11);
					y1 = dmulscale(oy, xvect2, ox, yvect2, 16) + (ydim << 11);
				}

				k = wal.point2;
				if (wall[k] == null)
					continue;

				ox = mapSettings.getWallX(k) - cposx;
				oy = mapSettings.getWallY(k) - cposy;
				x2 = dmulscale(ox, xvect, -oy, yvect, 16) + (xdim << 11);
				y2 = dmulscale(oy, xvect2, ox, yvect2, 16) + (ydim << 11);

				int col = mapSettings.getWallColor(j, i);
				if (col < 0)
					continue;

				drawline256(x1, y1, x2, y2, col);
			}
		}

		// draw player
		for (i = connecthead; i >= 0; i = connectpoint2[i]) {
			int spr = mapSettings.getPlayerSprite(i);
			if (spr == -1 || !isValidSector(sprite[spr].sectnum))
				continue;

			SPRITE pPlayer = sprite[spr];
			ox = mapSettings.getSpriteX(spr) - cposx;
			oy = mapSettings.getSpriteY(spr) - cposy;

			int dx = mulscale(ox, xvect, 16) - mulscale(oy, yvect, 16);
			int dy = mulscale(oy, xvect2, 16) + mulscale(ox, yvect2, 16);

			int dang = (pPlayer.ang - cang) & 0x7FF;
			int viewindex = mapSettings.getViewPlayer();
			if (i == viewindex && !mapSettings.isScrollMode()) {
				dx = 0;
				dy = viewindex ^ i;
				dang = 0;
			}

			if (i == viewindex || mapSettings.isShowAllPlayers()) {
				int picnum = mapSettings.getPlayerPicnum(i);
				if (picnum == -1) { // draw it with lines
//					ox = (sintable[(pPlayer.ang + 512) & 2047] >> 7);
//					oy = (sintable[(pPlayer.ang) & 2047] >> 7);
					x2 = 0;
					y2 = -(mapSettings.getPlayerZoom(i, czoom) << 1);

					int x3 = mulscale(x2, yxaspect, 16);
					int y3 = mulscale(y2, yxaspect, 16);

					int col = mapSettings.getSpriteColor(spr);
					if (col < 0)
						continue;

					drawline256(dx - x2 + (xdim << 11), dy - y3 + (ydim << 11), dx + x2 + (xdim << 11),
							dy + y3 + (ydim << 11), col);
					drawline256(dx - y2 + (xdim << 11), dy + x3 + (ydim << 11), dx + x2 + (xdim << 11),
							dy + y3 + (ydim << 11), col);
					drawline256(dx + y2 + (xdim << 11), dy - x3 + (ydim << 11), dx + x2 + (xdim << 11),
							dy + y3 + (ydim << 11), col);
				} else {
					int nZoom = mapSettings.getPlayerZoom(i, czoom);
					int sx = (dx << 4) + (xdim << 15);
					int sy = (dy << 4) + (ydim << 15);

					rotatesprite(sx, sy, nZoom, (short) dang, mapSettings.getPlayerPicnum(i), pPlayer.shade,
							pPlayer.pal, (pPlayer.cstat & 2) >> 1, windowx1, windowy1, windowx2, windowy2);
				}
			}
		}
	}
}
