/*
 * Software renderer code originally written by Ken Silverman
 * Ken Silverman's official web site: "http://www.advsys.net/ken"
 * See the included license file "BUILDLIC.TXT" for license info.
 *
 * This file has been modified from Ken Silverman's original release
 * by Jonathon Fowler (jf@jonof.id.au)
 * by Alexander Makarov-[M210] (m210-2007@mail.ru)
 */

package ru.m210projects.Build.Render.Software;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static ru.m210projects.Build.Engine.MAXSPRITESONSCREEN;
import static ru.m210projects.Build.Engine.MAXTILES;
import static ru.m210projects.Build.Engine.MAXWALLS;
import static ru.m210projects.Build.Engine.MAXYDIM;
import static ru.m210projects.Build.Engine.beforedrawrooms;
import static ru.m210projects.Build.Engine.globalpal;
import static ru.m210projects.Build.Engine.globalposx;
import static ru.m210projects.Build.Engine.globalposy;
import static ru.m210projects.Build.Engine.globalshade;
import static ru.m210projects.Build.Engine.gotsector;
import static ru.m210projects.Build.Engine.headspritesect;
import static ru.m210projects.Build.Engine.nextspritesect;
import static ru.m210projects.Build.Engine.numsectors;
import static ru.m210projects.Build.Engine.numshades;
import static ru.m210projects.Build.Engine.palookup;
import static ru.m210projects.Build.Engine.picsiz;
import static ru.m210projects.Build.Engine.pow2char;
import static ru.m210projects.Build.Engine.pow2long;
import static ru.m210projects.Build.Engine.sector;
import static ru.m210projects.Build.Engine.show2dsector;
import static ru.m210projects.Build.Engine.sintable;
import static ru.m210projects.Build.Engine.smalltextfont;
import static ru.m210projects.Build.Engine.sprite;
import static ru.m210projects.Build.Engine.textfont;
import static ru.m210projects.Build.Engine.tsprite;
import static ru.m210projects.Build.Engine.wall;
import static ru.m210projects.Build.Engine.windowx1;
import static ru.m210projects.Build.Engine.windowx2;
import static ru.m210projects.Build.Engine.windowy1;
import static ru.m210projects.Build.Engine.windowy2;
import static ru.m210projects.Build.Engine.wx1;
import static ru.m210projects.Build.Engine.wx2;
import static ru.m210projects.Build.Engine.wy1;
import static ru.m210projects.Build.Engine.wy2;
import static ru.m210projects.Build.Engine.xdim;
import static ru.m210projects.Build.Engine.xdimen;
import static ru.m210projects.Build.Engine.xdimenscale;
import static ru.m210projects.Build.Engine.xyaspect;
import static ru.m210projects.Build.Engine.ydim;
import static ru.m210projects.Build.Engine.yxaspect;
import static ru.m210projects.Build.Pragmas.divscale;
import static ru.m210projects.Build.Pragmas.dmulscale;
import static ru.m210projects.Build.Pragmas.klabs;
import static ru.m210projects.Build.Pragmas.mulscale;
import static ru.m210projects.Build.Pragmas.scale;

import java.util.Arrays;

import ru.m210projects.Build.Render.IOverheadMapSettings.MapView;
import ru.m210projects.Build.Render.IOverheadMapSettings;
import ru.m210projects.Build.Render.OrphoRenderer;
import ru.m210projects.Build.Render.Renderer.Transparent;
import ru.m210projects.Build.Types.SECTOR;
import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Build.Types.Tile;
import ru.m210projects.Build.Types.Tile.AnimType;
import ru.m210projects.Build.Types.TileFont;
import ru.m210projects.Build.Types.TileFont.FontType;
import ru.m210projects.Build.Types.WALL;

public class SoftwareOrpho extends OrphoRenderer {

	private final Software parent;
	protected int numpages;
	private final int MAXPERMS;

	protected final int MAXNODESPERLINE = 42; // Warning: This depends on MAXYSAVES & MAXYDIM!
	protected int[] dotp1 = new int[MAXYDIM], dotp2 = new int[MAXYDIM];

	protected final int MAXWALLSB = ((MAXWALLS >> 2) + (MAXWALLS >> 3));
	protected short[] p2 = new short[MAXWALLSB];
	protected int[] xb1 = new int[MAXWALLSB];
	protected int[] xb2 = new int[MAXWALLSB];
	protected int[] rx1 = new int[MAXWALLSB], rx2 = new int[MAXWALLSB];
	protected int[] ry1 = new int[MAXWALLSB], ry2 = new int[MAXWALLSB];

	protected short globalpicnum;
	protected int globalorientation;
	protected int globalx1;
	protected int globaly1;
	protected int globalx2;
	protected int globaly2;
	protected int asm1;
	protected int asm2;
	protected int globalpolytype;

	public int[] nrx1 = new int[8], nry1 = new int[8], nrx2 = new int[8], nry2 = new int[8]; // JBF 20031206: Thanks Ken

	public SoftwareOrpho(Software parent, IOverheadMapSettings settings) {
		super(parent.engine, settings);
		this.parent = parent;
		this.MAXPERMS = parent.MAXPERMS;
	}

	@Override
	public void printext(TileFont font, int xpos, int ypos, char[] text, int col, int shade, Transparent bit,
			float scale) {

		if(col < 0)
			return;

		if (font.type == FontType.Tilemap) {
			if (palookup[col] == null)
				col = 0;

			int nTile = (Integer) font.ptr;
			Tile pic = engine.getTile(nTile);
			if (pic.data == null && engine.loadtile(nTile) == null)
				return;

			int flags = 0;
			if (bit == Transparent.Bit1)
				flags = 1;
			if (bit == Transparent.Bit2)
				flags = 1 | 32;

			float tx, ty;
			int mscale = (int) (scale * 65536);
			int ctx, cty, textsize = mulscale(font.charsizx, mscale, 16);

			for (int i = 0; i < text.length && text[i] != 0; i++, xpos += textsize) {
				if (xpos < 0)
					continue;
				tx = (text[i] % font.cols) / (float) font.cols;
				ty = (text[i] / font.cols) / (float) font.rows;

				ctx = mulscale((int) (tx * pic.getWidth()), mscale, 16);
				cty = mulscale((int) (ty * pic.getHeight()), mscale, 16);

				engine.rotatesprite((xpos - ctx) << 16, (ypos - cty) << 16, mscale, 0, nTile, shade, col,
						8 | 16 | flags, xpos, ypos, xpos + textsize - 1, ypos + textsize - 1);
			}
		} else {
			int fontsize = 0;
			if (font.ptr == smalltextfont)
				fontsize = 1;

			printext(xpos, ypos, col, -1, text, fontsize, scale);
		}
	}

	@Override
	public void printext(int xpos, int ypos, int col, int backcol, char[] text, int fontsize, float scale) {
		int stx = xpos;
		int charxsiz = 8;
		int charysiz = (int) (scale * 7);
		byte[] fontptr = textfont;
		if (fontsize != 0) {
			fontptr = smalltextfont;
			charxsiz = 4;
		}

		for (int i = 0; i < text.length && text[i] != 0; i++) {
			int ptr = parent.bytesperline * (ypos + charysiz) + (stx - fontsize);
			if (ptr < 0)
				continue;

			for (int y = charysiz; y >= 0; y--) {
				for (int x = (int) (scale * (charxsiz - 1)); x >= 0; x--) {
					int index = Math.round(y / scale) + (text[i] << 3);
					if (index >= fontptr.length)
						continue;
					if ((fontptr[index] & pow2char[7 - fontsize - Math.round(x / scale)]) != 0) {
						parent.getA().drawpixel(ptr + x, (byte) col);
					} else if (backcol >= 0) {
						parent.getA().drawpixel(ptr + x, (byte) backcol);
					}
				}
				ptr -= parent.bytesperline;
			}
			stx += (scale * charxsiz);
		}
	}

	@Override
	public void drawline256(int x1, int y1, int x2, int y2, int c) {
		int dx, dy, i, j, inc, plc, daend;

		byte col = palookup[0][c];

		dx = x2 - x1;
		dy = y2 - y1;
		if (dx >= 0) {
			if ((x1 >= wx2) || (x2 < wx1))
				return;
			if (x1 < wx1) {
				y1 += scale(wx1 - x1, dy, dx);
				x1 = wx1;
			}
			if (x2 > wx2) {
				y2 += scale(wx2 - x2, dy, dx);
				x2 = wx2;
			}
		} else {
			if ((x2 >= wx2) || (x1 < wx1))
				return;
			if (x2 < wx1) {
				y2 += scale(wx1 - x2, dy, dx);
				x2 = wx1;
			}
			if (x1 > wx2) {
				y1 += scale(wx2 - x1, dy, dx);
				x1 = wx2;
			}
		}
		if (dy >= 0) {
			if ((y1 >= wy2) || (y2 < wy1))
				return;
			if (y1 < wy1) {
				x1 += scale(wy1 - y1, dx, dy);
				y1 = wy1;
			}
			if (y2 > wy2) {
				x2 += scale(wy2 - y2, dx, dy);
				y2 = wy2;
			}
		} else {
			if ((y2 >= wy2) || (y1 < wy1))
				return;
			if (y2 < wy1) {
				x2 += scale(wy1 - y2, dx, dy);
				y2 = wy1;
			}
			if (y1 > wy2) {
				x1 += scale(wy2 - y1, dx, dy);
				y1 = wy2;
			}
		}

		if (klabs(dx) >= klabs(dy)) {
			if (dx == 0)
				return;
			if (dx < 0) {
				i = x1;
				x1 = x2;
				x2 = i;
				i = y1;
				y1 = y2;
				y2 = i;
			}

			inc = divscale(dy, dx, 12);
			plc = y1 + mulscale((2047 - x1) & 4095, inc, 12);
			i = ((x1 + 2048) >> 12);
			daend = ((x2 + 2048) >> 12);

			for (; i < daend; i++) {
				j = (plc >> 12);
				if ((j >= parent.startumost[i]) && (j < parent.startdmost[i]))
					parent.getA().drawpixel(parent.ylookup[j] + i, col);
				plc += inc;
			}
		} else {
			if (dy < 0) {
				i = x1;
				x1 = x2;
				x2 = i;
				i = y1;
				y1 = y2;
				y2 = i;
			}

			inc = divscale(dx, dy, 12);
			plc = x1 + mulscale((2047 - y1) & 4095, inc, 12);
			i = ((y1 + 2048) >> 12);
			daend = ((y2 + 2048) >> 12);

			int p = parent.ylookup[i];

			for (; i < daend; i++) {
				j = (plc >> 12);
				if ((i >= parent.startumost[j]) && (i < parent.startdmost[j]))
					parent.getA().drawpixel(p + j, col);
				plc += inc;
				p += parent.ylookup[1];
			}
		}
	}

	@Override
	public void drawmapview(int dax, int day, int zoome, int ang) {
		WALL wal;
		SECTOR sec = null;

		int i, j, x, y, bakx1, baky1;
		int s, w, ox, oy, startwall, cx1, cy1, cx2, cy2;
		int bakgxvect, bakgyvect, npoints;
		int xvect, yvect, xvect2, yvect2, daslope;

		int xoff, yoff, k, l, cosang, sinang, xspan, yspan;
		int xrepeat, yrepeat, x1, y1, x2, y2, x3, y3, x4, y4;

		beforedrawrooms = 0;

		Arrays.fill(gotsector, (byte) 0);

		cx1 = (windowx1 << 12);
		cy1 = (windowy1 << 12);
		cx2 = ((windowx2 + 1) << 12) - 1;
		cy2 = ((windowy2 + 1) << 12) - 1;
		zoome <<= 8;
		bakgxvect = divscale(sintable[(1536 - ang) & 2047], zoome, 28);
		bakgyvect = divscale(sintable[(2048 - ang) & 2047], zoome, 28);
		xvect = mulscale(sintable[(2048 - ang) & 2047], zoome, 8);
		yvect = mulscale(sintable[(1536 - ang) & 2047], zoome, 8);
		xvect2 = mulscale(xvect, yxaspect, 16);
		yvect2 = mulscale(yvect, yxaspect, 16);

		int sortnum = 0;

		for (s = 0; s < numsectors; s++) {
			sec = sector[s];

			if (mapSettings.isFullMap() || (show2dsector[s >> 3] & pow2char[s & 7]) != 0) {
				npoints = 0;
				i = 0;
				startwall = sec.wallptr;

				j = startwall;
				if (startwall < 0)
					continue;
				for (w = sec.wallnum; w > 0; w--, j++) {
					wal = wall[j];
					if (wal == null)
						continue;
					ox = wal.x - dax;
					oy = wal.y - day;
					x = dmulscale(ox, xvect, -oy, yvect, 16) + (xdim << 11);
					y = dmulscale(oy, xvect2, ox, yvect2, 16) + (ydim << 11);
					i |= getclipmask(x - cx1, cx2 - x, y - cy1, cy2 - y);
					rx1[npoints] = x;
					ry1[npoints] = y;
					xb1[npoints] = wal.point2 - startwall;
					if (xb1[npoints] < 0)
						xb1[npoints] = 0;

					npoints++;
				}

				if ((i & 0xf0) != 0xf0)
					continue;

				bakx1 = rx1[0];
				baky1 = mulscale(ry1[0] - (ydim << 11), xyaspect, 16) + (ydim << 11);

				if ((i & 0x0f) != 0) {
					npoints = clippoly(npoints, i);
					if (npoints < 3)
						continue;
				}

				if (mapSettings.isShowFloorSprites()) {
					// Collect floor sprites to draw
					for (i = headspritesect[s]; i >= 0; i = nextspritesect[i])
						if ((sprite[i].cstat & 48) == 32) {
							if (sortnum >= MAXSPRITESONSCREEN)
								continue;
							if ((sprite[i].cstat & (64 + 8)) == (64 + 8))
								continue;

							if (tsprite[sortnum] == null)
								tsprite[sortnum] = new SPRITE();
							tsprite[sortnum].set(sprite[i]);
							tsprite[sortnum++].owner = (short) i;
						}
				}

				gotsector[s >> 3] |= pow2char[s & 7];

				globalorientation = sec.floorstat;
				if ((globalorientation & 1) != 0)
					continue;
				globalpal = sec.floorpal;
				if (sec.floorpal != parent.globalpalwritten) {
					parent.globalpalwritten = sec.floorpal;
					parent.getA().setpalookupaddress(palookup[parent.globalpalwritten]);
				}

				globalpicnum = sec.floorpicnum;
				if (globalpicnum >= MAXTILES)
					globalpicnum = 0;
				engine.setgotpic(globalpicnum);
				Tile pic = engine.getTile(globalpicnum);

				if (!pic.hasSize())
					continue;

				if (pic.getType() != AnimType.None) {
					globalpicnum += engine.animateoffs(globalpicnum, s);
					pic = engine.getTile(globalpicnum);
				}

				if (pic.data == null)
					engine.loadtile(globalpicnum);

				parent.globalbufplc = pic.data;
				globalshade = max(min(sec.floorshade, numshades - 1), 0);

				if ((globalorientation & 64) == 0) {
					globalposx = dax;
					globalx1 = bakgxvect;
					globaly1 = bakgyvect;
					globalposy = day;
					globalx2 = bakgxvect;
					globaly2 = bakgyvect;
				} else {
					ox = wall[wall[startwall].point2].x - wall[startwall].x;
					oy = wall[wall[startwall].point2].y - wall[startwall].y;
					i = engine.ksqrt(ox * ox + oy * oy);
					if (i == 0)
						continue;
					i = 1048576 / i;
					globalx1 = mulscale(dmulscale(ox, bakgxvect, oy, bakgyvect, 10), i, 10);
					globaly1 = mulscale(dmulscale(ox, bakgyvect, -oy, bakgxvect, 10), i, 10);
					ox = (bakx1 >> 4) - (xdim << 7);
					oy = (baky1 >> 4) - (ydim << 7);
					globalposx = dmulscale(-oy, globalx1, -ox, globaly1, 28);
					globalposy = dmulscale(-ox, globalx1, oy, globaly1, 28);
					globalx2 = -globalx1;
					globaly2 = -globaly1;

					daslope = sector[s].floorheinum;
					i = engine.ksqrt(daslope * daslope + 16777216);
					globalposy = mulscale(globalposy, i, 12);
					globalx2 = mulscale(globalx2, i, 12);
					globaly2 = mulscale(globaly2, i, 12);
				}
				int globalxshift = (8 - (picsiz[globalpicnum] & 15));
				int globalyshift = (8 - (picsiz[globalpicnum] >> 4));
				if ((globalorientation & 8) != 0) {
					globalxshift++;
					globalyshift++;
				}

				parent.globvis = parent.globalhisibility;
				if (sec.visibility != 0)
					parent.globvis = mulscale(parent.globvis, (sec.visibility + 16) & 0xFF, 4);
				globalpolytype = 0;

				if ((globalorientation & 0x4) > 0) {
					i = globalposx;
					globalposx = -globalposy;
					globalposy = -i;
					i = globalx2;
					globalx2 = globaly1;
					globaly1 = i;
					i = globalx1;
					globalx1 = -globaly2;
					globaly2 = -i;
				}
				if ((globalorientation & 0x10) > 0) {
					globalx1 = -globalx1;
					globaly1 = -globaly1;
					globalposx = -globalposx;
				}
				if ((globalorientation & 0x20) > 0) {
					globalx2 = -globalx2;
					globaly2 = -globaly2;
					globalposy = -globalposy;
				}
				asm1 = globaly1 << globalxshift;
				asm2 = globalx2 << globalyshift;
				globalx1 <<= globalxshift;
				globaly2 <<= globalyshift;
				globalposx = (globalposx << (20 + globalxshift)) + ((sec.floorxpanning) << 24);
				globalposy = (globalposy << (20 + globalyshift)) - ((sec.floorypanning) << 24);

				fillpolygon(npoints);
			}
		}

		if (mapSettings.isShowSprites(MapView.Polygons) || mapSettings.isShowFloorSprites()) {
			// Sort sprite list
			int gap = 1;
			while (gap < sortnum)
				gap = (gap << 1) + 1;
			for (gap >>= 1; gap > 0; gap >>= 1)
				for (i = 0; i < sortnum - gap; i++)
					for (j = i; j >= 0; j -= gap) {
						if (sprite[tsprite[j].owner].z <= sprite[tsprite[j + gap].owner].z)
							break;

						short tmp = tsprite[j].owner;
						tsprite[j].owner = tsprite[j + gap].owner;
						tsprite[j + gap].owner = tmp;
					}

			for (s = sortnum - 1; s >= 0; s--) {
				SPRITE spr = sprite[tsprite[s].owner];
				if ((spr.cstat & 48) == 32) {
					npoints = 0;

					if (spr.picnum >= MAXTILES)
						spr.picnum = 0;

					Tile pic = engine.getTile(spr.picnum);

					xoff = (byte) (pic.getOffsetX() + spr.xoffset);
					yoff = (byte) (pic.getOffsetY() + spr.yoffset);
					if ((spr.cstat & 4) > 0)
						xoff = -xoff;
					if ((spr.cstat & 8) > 0)
						yoff = -yoff;

					k = spr.ang & 2047;
					cosang = sintable[(k + 512) & 2047];
					sinang = sintable[k];
					xspan = pic.getWidth();
					xrepeat = spr.xrepeat;
					yspan = pic.getHeight();
					yrepeat = spr.yrepeat;
					ox = ((xspan >> 1) + xoff) * xrepeat;
					oy = ((yspan >> 1) + yoff) * yrepeat;
					x1 = spr.x + mulscale(sinang, ox, 16) + mulscale(cosang, oy, 16);
					y1 = spr.y + mulscale(sinang, oy, 16) - mulscale(cosang, ox, 16);
					l = xspan * xrepeat;
					x2 = x1 - mulscale(sinang, l, 16);
					y2 = y1 + mulscale(cosang, l, 16);
					l = yspan * yrepeat;
					k = -mulscale(cosang, l, 16);
					x3 = x2 + k;
					x4 = x1 + k;
					k = -mulscale(sinang, l, 16);
					y3 = y2 + k;
					y4 = y1 + k;

					xb1[0] = 1;
					xb1[1] = 2;
					xb1[2] = 3;
					xb1[3] = 0;
					npoints = 4;

					i = 0;

					ox = x1 - dax;
					oy = y1 - day;
					x = dmulscale(ox, xvect, -oy, yvect, 16) + (xdim << 11);
					y = dmulscale(oy, xvect2, ox, yvect2, 16) + (ydim << 11);
					i |= getclipmask(x - cx1, cx2 - x, y - cy1, cy2 - y);
					rx1[0] = x;
					ry1[0] = y;

					ox = x2 - dax;
					oy = y2 - day;
					x = dmulscale(ox, xvect, -oy, yvect, 16) + (xdim << 11);
					y = dmulscale(oy, xvect2, ox, yvect2, 16) + (ydim << 11);
					i |= getclipmask(x - cx1, cx2 - x, y - cy1, cy2 - y);
					rx1[1] = x;
					ry1[1] = y;

					ox = x3 - dax;
					oy = y3 - day;
					x = dmulscale(ox, xvect, -oy, yvect, 16) + (xdim << 11);
					y = dmulscale(oy, xvect2, ox, yvect2, 16) + (ydim << 11);
					i |= getclipmask(x - cx1, cx2 - x, y - cy1, cy2 - y);
					rx1[2] = x;
					ry1[2] = y;

					x = rx1[0] + rx1[2] - rx1[1];
					y = ry1[0] + ry1[2] - ry1[1];
					i |= getclipmask(x - cx1, cx2 - x, y - cy1, cy2 - y);
					rx1[3] = x;
					ry1[3] = y;

					if ((i & 0xf0) != 0xf0)
						continue;
					bakx1 = rx1[0];
					baky1 = mulscale(ry1[0] - (ydim << 11), xyaspect, 16) + (ydim << 11);

					if ((i & 0x0f) != 0) {
						npoints = clippoly(npoints, i);
						if (npoints < 3)
							continue;
					}

					globalpicnum = spr.picnum;
					globalpal = spr.pal; // GL needs this, software doesn't
					engine.setgotpic(globalpicnum);
					Tile sprpic = engine.getTile(globalpicnum);

					if (!sprpic.hasSize())
						continue;
					if (sprpic.getType() != AnimType.None) {
						globalpicnum += engine.animateoffs(globalpicnum, s);
						sprpic = engine.getTile(globalpicnum);
					}

					if (sprpic.data == null)
						engine.loadtile(globalpicnum);

					parent.globalbufplc = sprpic.data;

					// 'loading' the tile doesn't actually guarantee that it's there afterwards.
					// This can really happen when drawing the second frame of a floor-aligned
					// 'storm icon' sprite (4894+1)

					if ((sector[spr.sectnum].ceilingstat & 1) > 0)
						globalshade = (sector[spr.sectnum].ceilingshade);
					else
						globalshade = (sector[spr.sectnum].floorshade);
					globalshade = max(min(globalshade + spr.shade + 6, numshades - 1), 0);

					parent.globvis = parent.globalhisibility;
					if (sec.visibility != 0)
						parent.globvis = mulscale(parent.globvis, (sec.visibility + 16) & 0xFF, 4);
					globalpolytype = ((spr.cstat & 2) >> 1) + 1;

					parent.getA().setuphline(palookup[spr.pal], globalshade << 8);

					// relative alignment stuff
					ox = x2 - x1;
					oy = y2 - y1;
					i = ox * ox + oy * oy;
					if (i == 0)
						continue;
					i = (65536 * 16384) / i;
					globalx1 = mulscale(dmulscale(ox, bakgxvect, oy, bakgyvect, 10), i, 10);
					globaly1 = mulscale(dmulscale(ox, bakgyvect, -oy, bakgxvect, 10), i, 10);
					ox = y1 - y4;
					oy = x4 - x1;
					i = ox * ox + oy * oy;
					if (i == 0)
						continue;
					i = (65536 * 16384) / i;
					globalx2 = mulscale(dmulscale(ox, bakgxvect, oy, bakgyvect, 10), i, 10);
					globaly2 = mulscale(dmulscale(ox, bakgyvect, -oy, bakgxvect, 10), i, 10);

					ox = picsiz[globalpicnum];
					oy = ((ox >> 4) & 15);
					ox &= 15;
					if (pow2long[ox] != xspan) {
						ox++;
						globalx1 = mulscale(globalx1, xspan, ox);
						globaly1 = mulscale(globaly1, xspan, ox);
					}

					bakx1 = (bakx1 >> 4) - (xdim << 7);
					baky1 = (baky1 >> 4) - (ydim << 7);
					globalposx = dmulscale(-baky1, globalx1, -bakx1, globaly1, 28);
					globalposy = dmulscale(bakx1, globalx2, -baky1, globaly2, 28);

					if ((spr.cstat & 2) == 0)
						parent.getA().msethlineshift(ox, oy);
					else {
						if ((spr.cstat & 512) != 0)
							parent.getA().settransreverse();
						else
							parent.getA().settransnormal();
						parent.getA().tsethlineshift(ox, oy);
					}

					if ((spr.cstat & 0x4) > 0) {
						globalx1 = -globalx1;
						globaly1 = -globaly1;
						globalposx = -globalposx;
					}
					asm1 = globaly1 << 2;
					globalx1 <<= 2;
					globalposx <<= (20 + 2);
					asm2 = globalx2 << 2;
					globaly2 <<= 2;
					globalposy <<= (20 + 2);

					// so polymost can get the translucency. ignored in software mode:
					globalorientation = ((spr.cstat & 2) << 7) | ((spr.cstat & 512) >> 2);

					fillpolygon(npoints);
				}
			}
		}
	}

	private int clippoly(int npoints, int clipstat) {
		int z, zz, s1, s2, t, npoints2, start2, z1, z2, z3, z4, splitcnt;
		int cx1, cy1, cx2, cy2;

		cx1 = windowx1;
		cy1 = windowy1;
		cx2 = windowx2 + 1;
		cy2 = windowy2 + 1;
		cx1 <<= 12;
		cy1 <<= 12;
		cx2 <<= 12;
		cy2 <<= 12;

		if ((clipstat & 0xa) != 0) // Need to clip top or left
		{
			npoints2 = 0;
			start2 = 0;
			z = 0;
			splitcnt = 0;
			do {
				s2 = (cx1 - rx1[z]);
				do {
					zz = xb1[z];
					xb1[z] = -1;
					s1 = s2;
					s2 = (cx1 - rx1[zz]);
					if (s1 < 0) {
						rx2[npoints2] = rx1[z];
						ry2[npoints2] = ry1[z];
						xb2[npoints2] = npoints2 + 1;
						npoints2++;
					}
					if ((s1 ^ s2) < 0) {
						rx2[npoints2] = rx1[z] + scale((rx1[zz] - rx1[z]), s1, s1 - s2);
						ry2[npoints2] = ry1[z] + scale((ry1[zz] - ry1[z]), s1, s1 - s2);
						if (s1 < 0)
							p2[splitcnt++] = (short) npoints2;
						xb2[npoints2] = npoints2 + 1;
						npoints2++;
					}
					z = zz;
				} while (xb1[z] >= 0);

				if (npoints2 >= start2 + 3) {
					xb2[npoints2 - 1] = start2;
					start2 = npoints2;
				} else
					npoints2 = start2;

				z = 1;
				while ((z < npoints) && (xb1[z] < 0))
					z++;
			} while (z < npoints);
			if (npoints2 <= 2)
				return (0);

			for (z = 1; z < splitcnt; z++)
				for (zz = 0; zz < z; zz++) {
					z1 = p2[z];
					z2 = xb2[z1];
					z3 = p2[zz];
					z4 = xb2[z3];
					s1 = (klabs(rx2[z1] - rx2[z2]) + klabs(ry2[z1] - ry2[z2]));
					s1 += klabs(rx2[z3] - rx2[z4]) + klabs(ry2[z3] - ry2[z4]);
					s2 = (klabs(rx2[z1] - rx2[z4]) + klabs(ry2[z1] - ry2[z4]));
					s2 += klabs(rx2[z3] - rx2[z2]) + klabs(ry2[z3] - ry2[z2]);
					if (s2 < s1) {
						t = xb2[p2[z]];
						xb2[p2[z]] = xb2[p2[zz]];
						xb2[p2[zz]] = t;
					}
				}

			npoints = 0;
			start2 = 0;
			z = 0;
			splitcnt = 0;
			do {
				s2 = (cy1 - ry2[z]);
				do {
					zz = xb2[z];
					xb2[z] = -1;
					s1 = s2;
					s2 = (cy1 - ry2[zz]);
					if (s1 < 0) {
						rx1[npoints] = rx2[z];
						ry1[npoints] = ry2[z];
						xb1[npoints] = npoints + 1;
						npoints++;
					}
					if ((s1 ^ s2) < 0) {
						rx1[npoints] = rx2[z] + scale((rx2[zz] - rx2[z]), s1, s1 - s2);
						ry1[npoints] = ry2[z] + scale((ry2[zz] - ry2[z]), s1, s1 - s2);
						if (s1 < 0)
							p2[splitcnt++] = (short) npoints;
						xb1[npoints] = npoints + 1;
						npoints++;
					}
					z = zz;
				} while (xb2[z] >= 0);

				if (npoints >= start2 + 3) {
					xb1[npoints - 1] = start2;
					start2 = npoints;
				} else
					npoints = start2;

				z = 1;
				while ((z < npoints2) && (xb2[z] < 0))
					z++;
			} while (z < npoints2);
			if (npoints <= 2)
				return (0);

			for (z = 1; z < splitcnt; z++)
				for (zz = 0; zz < z; zz++) {
					z1 = p2[z];
					z2 = xb1[z1];
					z3 = p2[zz];
					z4 = xb1[z3];
					s1 = (klabs(rx1[z1] - rx1[z2]) + klabs(ry1[z1] - ry1[z2]));
					s1 += klabs(rx1[z3] - rx1[z4]) + klabs(ry1[z3] - ry1[z4]);
					s2 = (klabs(rx1[z1] - rx1[z4]) + klabs(ry1[z1] - ry1[z4]));
					s2 += klabs(rx1[z3] - rx1[z2]) + klabs(ry1[z3] - ry1[z2]);
					if (s2 < s1) {
						t = xb1[p2[z]];
						xb1[p2[z]] = xb1[p2[zz]];
						xb1[p2[zz]] = t;
					}
				}
		}

		if ((clipstat & 0x5) != 0) // Need to clip bottom or right
		{
			npoints2 = 0;
			start2 = 0;
			z = 0;
			splitcnt = 0;
			do {
				s2 = (rx1[z] - cx2);
				do {
					zz = xb1[z];
					xb1[z] = -1;
					s1 = s2;
					s2 = (rx1[zz] - cx2);
					if (s1 < 0) {
						rx2[npoints2] = rx1[z];
						ry2[npoints2] = ry1[z];
						xb2[npoints2] = npoints2 + 1;
						npoints2++;
					}
					if ((s1 ^ s2) < 0) {
						rx2[npoints2] = rx1[z] + scale((rx1[zz] - rx1[z]), s1, s1 - s2);
						ry2[npoints2] = ry1[z] + scale((ry1[zz] - ry1[z]), s1, s1 - s2);
						if (s1 < 0)
							p2[splitcnt++] = (short) npoints2;
						xb2[npoints2] = npoints2 + 1;
						npoints2++;
					}
					z = zz;
				} while (xb1[z] >= 0);

				if (npoints2 >= start2 + 3) {
					xb2[npoints2 - 1] = start2;
					start2 = npoints2;
				} else
					npoints2 = start2;

				z = 1;
				while ((z < npoints) && (xb1[z] < 0))
					z++;
			} while (z < npoints);
			if (npoints2 <= 2)
				return (0);

			for (z = 1; z < splitcnt; z++)
				for (zz = 0; zz < z; zz++) {
					z1 = p2[z];
					z2 = xb2[z1];
					z3 = p2[zz];
					z4 = xb2[z3];
					s1 = (klabs(rx2[z1] - rx2[z2]) + klabs(ry2[z1] - ry2[z2]));
					s1 += klabs(rx2[z3] - rx2[z4]) + klabs(ry2[z3] - ry2[z4]);
					s2 = (klabs(rx2[z1] - rx2[z4]) + klabs(ry2[z1] - ry2[z4]));
					s2 += klabs(rx2[z3] - rx2[z2]) + klabs(ry2[z3] - ry2[z2]);
					if (s2 < s1) {
						t = xb2[p2[z]];
						xb2[p2[z]] = xb2[p2[zz]];
						xb2[p2[zz]] = t;
					}
				}

			npoints = 0;
			start2 = 0;
			z = 0;
			splitcnt = 0;
			do {
				s2 = (ry2[z] - cy2);
				do {
					zz = xb2[z];
					xb2[z] = -1;
					s1 = s2;
					s2 = (ry2[zz] - cy2);
					if (s1 < 0) {
						rx1[npoints] = rx2[z];
						ry1[npoints] = ry2[z];
						xb1[npoints] = npoints + 1;
						npoints++;
					}
					if ((s1 ^ s2) < 0) {
						rx1[npoints] = rx2[z] + scale((rx2[zz] - rx2[z]), s1, s1 - s2);
						ry1[npoints] = ry2[z] + scale((ry2[zz] - ry2[z]), s1, s1 - s2);
						if (s1 < 0)
							p2[splitcnt++] = (short) npoints;
						xb1[npoints] = npoints + 1;
						npoints++;
					}
					z = zz;
				} while (xb2[z] >= 0);

				if (npoints >= start2 + 3) {
					xb1[npoints - 1] = start2;
					start2 = npoints;
				} else
					npoints = start2;

				z = 1;
				while ((z < npoints2) && (xb2[z] < 0))
					z++;
			} while (z < npoints2);
			if (npoints <= 2)
				return (0);

			for (z = 1; z < splitcnt; z++)
				for (zz = 0; zz < z; zz++) {
					z1 = p2[z];
					z2 = xb1[z1];
					z3 = p2[zz];
					z4 = xb1[z3];
					s1 = (klabs(rx1[z1] - rx1[z2]) + klabs(ry1[z1] - ry1[z2]));
					s1 += klabs(rx1[z3] - rx1[z4]) + klabs(ry1[z3] - ry1[z4]);
					s2 = (klabs(rx1[z1] - rx1[z4]) + klabs(ry1[z1] - ry1[z4]));
					s2 += klabs(rx1[z3] - rx1[z2]) + klabs(ry1[z3] - ry1[z2]);
					if (s2 < s1) {
						t = xb1[p2[z]];
						xb1[p2[z]] = xb1[p2[zz]];
						xb1[p2[zz]] = t;
					}
				}
		}
		return (npoints);
	}

	private void fillpolygon(int npoints) {
		int z, zz, x1, y1, x2, y2, miny, maxy, y, xinc, cnt;
		int ox, oy, bx, by, p, day1, day2;

		parent.getA().sethlinesizes(picsiz[globalpicnum] & 15, picsiz[globalpicnum] >> 4, parent.globalbufplc);

		miny = 0x7fffffff;
		maxy = 0x80000000;
		for (z = npoints - 1; z >= 0; z--) {
			y = ry1[z];
			miny = min(miny, y);
			maxy = max(maxy, y);
		}
		miny = (miny >> 12);
		maxy = (maxy >> 12);
		if (miny < 0)
			miny = 0;
		if (maxy >= ydim)
			maxy = ydim - 1;

		int ptr = 0; // They're pointers! - watch how you optimize this thing
		for (y = miny; y <= maxy; y++) {
			dotp1[y] = ptr;
			dotp2[y] = ptr + (MAXNODESPERLINE >> 1);
			ptr += MAXNODESPERLINE;
		}

		for (z = npoints - 1; z >= 0; z--) {
			zz = xb1[z];
			y1 = ry1[z];
			day1 = (y1 >> 12);
			y2 = ry1[zz];
			day2 = (y2 >> 12);

			if (day1 != day2) {
				x1 = rx1[z];
				x2 = rx1[zz];
				xinc = divscale(x2 - x1, y2 - y1, 12);
				if (day2 > day1) {
					x1 += mulscale((day1 << 12) + 4095 - y1, xinc, 12);
					for (y = day1; y < day2; y++) {
						parent.smost[dotp2[y]++] = (short) (x1 >> 12);
						x1 += xinc;
					}
				} else {
					x2 += mulscale((day2 << 12) + 4095 - y2, xinc, 12);
					for (y = day2; y < day1; y++) {
						parent.smost[dotp1[y]++] = (short) (x2 >> 12);
						x2 += xinc;
					}
				}
			}
		}

		globalx1 = mulscale(globalx1, xyaspect, 16);
		globaly2 = mulscale(globaly2, xyaspect, 16);

		oy = miny + 1 - (ydim >> 1);
		globalposx += oy * globalx1;
		globalposy += oy * globaly2;

		parent.getA().setuphlineasm4(asm1, asm2);

		ptr = 0;
		int ptr2;
		for (y = miny; y <= maxy; y++) {
			cnt = dotp1[y] - ptr;
			ptr2 = ptr + (MAXNODESPERLINE >> 1);
			for (z = cnt - 1; z >= 0; z--) {
				day1 = 0;
				day2 = 0;
				for (zz = z; zz > 0; zz--) {
					if (parent.smost[ptr + zz] < parent.smost[ptr + day1])
						day1 = zz;
					if (parent.smost[ptr2 + zz] < parent.smost[ptr2 + day2])
						day2 = zz;
				}
				x1 = parent.smost[ptr + day1];
				parent.smost[ptr + day1] = parent.smost[ptr + z];
				x2 = parent.smost[ptr2 + day2] - 1;
				parent.smost[ptr2 + day2] = parent.smost[ptr2 + z];
				if (x1 > x2)
					continue;

				if (globalpolytype < 1) {
					// maphline
					ox = x2 + 1 - (xdim >> 1);
					bx = ox * asm1 + globalposx;
					by = ox * asm2 - globalposy;

					p = parent.ylookup[y] + x2;
					parent.getA().hlineasm4(x2 - x1, -1, globalshade << 8, by, bx, p);
				} else {
					// maphline
					ox = x1 + 1 - (xdim >> 1);
					bx = ox * asm1 + globalposx;
					by = ox * asm2 - globalposy;

					p = parent.ylookup[y] + x1;
					if (globalpolytype == 1)
						parent.getA().mhline(parent.globalbufplc, bx, (x2 - x1) << 16, 0, by, p);
					else {
						parent.getA().thline(parent.globalbufplc, bx, (x2 - x1) << 16, 0, by, p);
					}
				}
			}
			globalposx += globalx1;
			globalposy += globaly2;
			ptr += MAXNODESPERLINE;
		}
//		engine.faketimerhandler();
	}

	@Override
	public void rotatesprite(int sx, int sy, int z, int a, int picnum, int dashade, int dapalnum, int dastat, int cx1,
			int cy1, int cx2, int cy2) {
		int i;
		PermFifo per, per2;

		if (picnum >= MAXTILES)
			return;

		if ((cx1 > cx2) || (cy1 > cy2))
			return;
		if (z <= 16)
			return;

		Tile pic = engine.getTile(picnum);

		if (pic.getType() != AnimType.None) {
			picnum += engine.animateoffs(picnum, 0xc000);
			pic = engine.getTile(picnum);
		}

		if (!pic.hasSize())
			return;

		// Experimental / development bits. ONLY FOR INTERNAL USE!
		// bit RS_CENTERORIGIN: see dorotspr_handle_bit2
		////////////////////

		if (((dastat & 128) == 0) || (numpages < 2) || (beforedrawrooms != 0)) {
			dorotatesprite(sx, sy, z, a, picnum, dashade, dapalnum, dastat, cx1, cy1, cx2, cy2, parent.guniqhudid);
		}

		if (((dastat & 64) != 0) && (cx1 <= 0) && (cy1 <= 0) && (cx2 >= xdim - 1) && (cy2 >= ydim - 1)
				&& (sx == (160 << 16)) && (sy == (100 << 16)) && (z == 65536L) && (a == 0) && ((dastat & 1) == 0))
			parent.permhead = parent.permtail = 0;

		if ((dastat & 128) == 0)
			return;

		if (numpages >= 2) {
			per = parent.permfifo[parent.permhead];
			if (per == null)
				per = new PermFifo();
			per.sx = sx;
			per.sy = sy;
			per.z = z;
			per.a = (short) a;
			per.picnum = (short) picnum;
			per.dashade = (short) dashade;
			per.dapalnum = (short) dapalnum;
			per.dastat = (short) dastat;
			per.pagesleft = (short) (numpages + ((beforedrawrooms & 1) << 7));
			per.cx1 = cx1;
			per.cy1 = cy1;
			per.cx2 = cx2;
			per.cy2 = cy2;
			per.uniqid = parent.guniqhudid; // JF extension

			// Would be better to optimize out true bounding boxes
			if ((dastat & 64) != 0) // If non-masking write, checking for overlapping cases
			{
				for (i = parent.permtail; i != parent.permhead; i = ((i + 1) & (MAXPERMS - 1))) {
					per2 = parent.permfifo[i];
					if (per2 == null)
						per2 = new PermFifo();
					if ((per2.pagesleft & 127) == 0)
						continue;
					if (per2.sx != per.sx)
						continue;
					if (per2.sy != per.sy)
						continue;
					if (per2.z != per.z)
						continue;
					if (per2.a != per.a)
						continue;
					Tile pic2 = engine.getTile(per2.picnum);

					if (pic2.getWidth() > pic.getWidth())
						continue;
					if (pic2.getHeight() > pic.getHeight())
						continue;
					if (per2.cx1 < per.cx1)
						continue;
					if (per2.cy1 < per.cy1)
						continue;
					if (per2.cx2 > per.cx2)
						continue;
					if (per2.cy2 > per.cy2)
						continue;
					per2.pagesleft = 0;
				}
				if ((per.z == 65536) && (per.a == 0))
					for (i = parent.permtail; i != parent.permhead; i = ((i + 1) & (MAXPERMS - 1))) {
						per2 = parent.permfifo[i];
						if (per2 == null)
							per2 = new PermFifo();
						if ((per2.pagesleft & 127) == 0)
							continue;
						if (per2.z != 65536)
							continue;
						if (per2.a != 0)
							continue;
						if (per2.cx1 < per.cx1)
							continue;
						if (per2.cy1 < per.cy1)
							continue;
						if (per2.cx2 > per.cx2)
							continue;
						if (per2.cy2 > per.cy2)
							continue;
						if ((per2.sx >> 16) < (per.sx >> 16))
							continue;
						if ((per2.sy >> 16) < (per.sy >> 16))
							continue;
						Tile pic2 = engine.getTile(per2.picnum);

						if ((per2.sx >> 16) + pic2.getWidth() > (per.sx >> 16) + pic.getWidth())
							continue;
						if ((per2.sy >> 16) + pic2.getHeight() > (per.sy >> 16) + pic.getHeight())
							continue;
						per2.pagesleft = 0;
					}
			}

			parent.permhead = ((parent.permhead + 1) & (MAXPERMS - 1));
		}
	}

	private void dorotatesprite(int sx, int sy, int z, int ang, int picnum, int dashade, int dapalnum, int dastat,
			int cx1, int cy1, int cx2, int cy2, int uniqid) {
		int x, y;

		if (dapalnum < 0 || dapalnum >= palookup.length || palookup[dapalnum] == null)
			dapalnum = 0;

		if (cx1 < 0)
			cx1 = 0;
		if (cy1 < 0)
			cy1 = 0;
		if (cx2 > xdim - 1)
			cx2 = xdim - 1;
		if (cy2 > ydim - 1)
			cy2 = ydim - 1;

		Tile pic = engine.getTile(picnum);

		int xsiz = pic.getWidth();
		int ysiz = pic.getHeight();

		int xoff = 0, yoff = 0;
		if ((dastat & 16) == 0) {
			xoff = pic.getOffsetX() + (xsiz >> 1);
			yoff = pic.getOffsetY() + (ysiz >> 1);
		}

		if ((dastat & 4) != 0)
			yoff = ysiz - yoff;

		int cosang = sintable[(ang + 512) & 2047];
		int sinang = sintable[ang & 2047];

		int ourxyaspect = xyaspect;
		int ouryxaspect = yxaspect;
		if ((dastat & 2) == 0) {
			if ((dastat & 1024) == 0 && 4 * ydim <= 3 * xdim) {
				ouryxaspect = (12 << 16) / 10;
				ourxyaspect = (10 << 16) / 12;
			}
		} else {
			// dastat&2: Auto window size scaling
			int oxdim = xdim, zoomsc;
			int xdim = oxdim; // SHADOWS global

			// screen center to s[xy], 320<<16 coords.
			int normxofs = sx - (320 << 15), normyofs = sy - (200 << 15);
			if ((dastat & 1024) == 0 && 4 * ydim <= 3 * xdim) {
				xdim = (4 * ydim) / 3;

				ouryxaspect = (12 << 16) / 10;
				ourxyaspect = (10 << 16) / 12;
			}

			// nasty hacks go here
			if ((dastat & 8) == 0) {
				int twice_midcx = (windowx1 + windowx2) + 2;

				// screen x center to sx1, scaled to viewport
				int scaledxofs = scale(normxofs, scale(xdimen, xdim, oxdim), 320);
				int xbord = 0;
				if ((dastat & (256 | 512)) != 0) {
					xbord = scale(oxdim - xdim, twice_midcx, oxdim);
					if ((dastat & 512) == 0)
						xbord = -xbord;
				}

				sx = ((twice_midcx + xbord) << 15) + scaledxofs;
				zoomsc = xdimenscale;
				sy = (((windowy1 + windowy2) + 2) << 15) + mulscale(normyofs, zoomsc, 16);
			} else {
				// If not clipping to startmosts, & auto-scaling on, as a
				// hard-coded bonus, scale to full screen instead

				sx = (xdim << 15) + 32768 + scale(normxofs, xdim, 320);

				if ((dastat & 512) != 0)
					sx += (oxdim - xdim) << 16;
				else if ((dastat & 256) == 0)
					sx += (oxdim - xdim) << 15;

				zoomsc = scale(xdim, ouryxaspect, 320);
				sy = (ydim << 15) + 32768 + mulscale(normyofs, zoomsc, 16);
			}
			z = mulscale(z, zoomsc, 16);
		}

		int xv = mulscale(cosang, z, 14), xv2;
		int yv = mulscale(sinang, z, 14), yv2;

		if (((dastat & 2) != 0) || ((dastat & 8) == 0)) // Don't aspect unscaled perms
		{
			xv2 = mulscale(xv, ourxyaspect, 16);
			yv2 = mulscale(yv, ourxyaspect, 16);
		} else {
			xv2 = xv;
			yv2 = yv;
		}

		nry1[0] = sy - (yv * xoff + xv * yoff);
		nry1[1] = nry1[0] + yv * xsiz;
		nry1[3] = nry1[0] + xv * ysiz;
		nry1[2] = nry1[1] + nry1[3] - nry1[0];
		int i = (cy1 << 16);
		if ((nry1[0] < i) && (nry1[1] < i) && (nry1[2] < i) && (nry1[3] < i))
			return;
		i = (cy2 << 16);
		if ((nry1[0] > i) && (nry1[1] > i) && (nry1[2] > i) && (nry1[3] > i))
			return;

		nrx1[0] = sx - (xv2 * xoff - yv2 * yoff);
		nrx1[1] = nrx1[0] + xv2 * xsiz;
		nrx1[3] = nrx1[0] - yv2 * ysiz;
		nrx1[2] = nrx1[1] + nrx1[3] - nrx1[0];
		i = (cx1 << 16);
		if ((nrx1[0] < i) && (nrx1[1] < i) && (nrx1[2] < i) && (nrx1[3] < i))
			return;
		i = (cx2 << 16);
		if ((nrx1[0] > i) && (nrx1[1] > i) && (nrx1[2] > i) && (nrx1[3] > i))
			return;

		int gx1 = nrx1[0];
		int gy1 = nry1[0]; // back up these before clipping

		int npoints;
		if ((npoints = clippoly4(cx1 << 16, cy1 << 16, (cx2 + 1) << 16, (cy2 + 1) << 16)) < 3)
			return;

		int lx = nrx1[0];
		int rx = nrx1[0];

		int nextv = 0;
		for (int v = npoints - 1; v >= 0; v--) {
			int x1 = nrx1[v];
			int x2 = nrx1[nextv];
			int dax1 = (x1 >> 16);
			if (x1 < lx)
				lx = x1;
			int dax2 = (x2 >> 16);
			if (x1 > rx)
				rx = x1;
			if (dax1 != dax2) {
				int y1 = nry1[v];
				int y2 = nry1[nextv];
				long yinc = divscale(y2 - y1, x2 - x1, 16);

				if (dax2 > dax1) {
					int yplc = y1 + mulscale((dax1 << 16) + 65535 - x1, yinc, 16);
					parent.qinterpolatedown16short(parent.uplc, dax1, dax2 - dax1, yplc, yinc);
				} else {
					int yplc = y2 + mulscale((dax2 << 16) + 65535 - x2, yinc, 16);
					parent.qinterpolatedown16short(parent.dplc, dax2, dax1 - dax2, yplc, yinc);
				}
			}
			nextv = v;
		}

		if (pic.data == null)
			engine.loadtile(picnum);
		engine.setgotpic(picnum);
		byte[] bufplc = pic.data;

		int palookupshade = engine.getpalookup(0, dashade) << 8;

		i = divscale(1, z, 32);
		xv = mulscale(sinang, i, 14);
		yv = mulscale(cosang, i, 14);
		if (((dastat & 2) != 0) || ((dastat & 8) == 0)) // Don't aspect unscaled perms
		{
			yv2 = mulscale(-xv, ouryxaspect, 16);
			xv2 = mulscale(yv, ouryxaspect, 16);
		} else {
			yv2 = -xv;
			xv2 = yv;
		}

		int x1 = (lx >> 16);
		int x2 = (rx >> 16);

		int oy = 0;
		x = (x1 << 16) - 1 - gx1;
		y = (oy << 16) + 65535 - gy1;
		int bx = dmulscale(x, xv2, y, xv, 16);
		int by = dmulscale(x, yv2, y, yv, 16);

		if ((dastat & 4) != 0) {
			yv = -yv;
			yv2 = -yv2;
			by = (ysiz << 16) - 1 - by;
		}

		if ((dastat & 1) == 0) {
			if ((dastat & 64) != 0)
				parent.getA().setupspritevline(palookup[dapalnum], palookupshade, xv, yv, ysiz);
			else
				parent.getA().msetupspritevline(palookup[dapalnum], palookupshade, xv, yv, ysiz);
		} else {
			parent.getA().tsetupspritevline(palookup[dapalnum], palookupshade, xv, yv, ysiz);
			if ((dastat & 32) != 0)
				parent.getA().settransreverse();
			else
				parent.getA().settransnormal();
		}

		if (x1 < 0)
			return; // GDX 24.03.2020 crash fix

		for (x = x1; x < x2; x++) {
			bx += xv2;
			by += yv2;
			int y1 = parent.uplc[x];
			int y2 = parent.dplc[x];
			if ((dastat & 8) == 0) {
				if (parent.startumost[x] > y1)
					y1 = parent.startumost[x];
				if (parent.startdmost[x] < y2)
					y2 = parent.startdmost[x];
			}
			if (y2 <= y1)
				continue;

			switch (y1 - oy) {
			case -1:
				bx -= xv;
				by -= yv;
				oy = y1;
				break;
			case 0:
				break;
			case 1:
				bx += xv;
				by += yv;
				oy = y1;
				break;
			default:
				bx += xv * (y1 - oy);
				by += yv * (y1 - oy);
				oy = y1;
				break;
			}

			int p = parent.ylookup[y1] + x;
			if ((dastat & 1) == 0) {
				if ((dastat & 64) != 0) {
					parent.getA().spritevline(bx & 65535, by & 65535, y2 - y1 + 1, bufplc,
							(bx >> 16) * ysiz + (by >> 16), p);
				} else {
					parent.getA().mspritevline(bx & 65535, by & 65535, y2 - y1 + 1, bufplc,
							(bx >> 16) * ysiz + (by >> 16), p);
				}
			} else {
				parent.getA().tspritevline(bx & 65535, by & 65535, y2 - y1 + 1, bufplc, (bx >> 16) * ysiz + (by >> 16),
						p);
			}
//			engine.faketimerhandler();
		}
	}

	private int clippoly4(int cx1, int cy1, int cx2, int cy2) {
		int n, nn, z, zz, x, x1, x2, y, y1, y2, t;

		nn = 0;
		z = 0;
		do {
			zz = ((z + 1) & 3);
			x1 = nrx1[z];
			x2 = nrx1[zz] - x1;

			if ((cx1 <= x1) && (x1 <= cx2)) {
				nrx2[nn] = x1;
				nry2[nn] = nry1[z];
				nn++;
			}

			if (x2 <= 0)
				x = cx2;
			else
				x = cx1;
			t = x - x1;
			if (((t - x2) ^ t) < 0) {
				nrx2[nn] = x;
				nry2[nn] = nry1[z] + scale(t, nry1[zz] - nry1[z], x2);
				nn++;
			}

			if (x2 <= 0)
				x = cx1;
			else
				x = cx2;
			t = x - x1;
			if (((t - x2) ^ t) < 0) {
				nrx2[nn] = x;
				nry2[nn] = nry1[z] + scale(t, nry1[zz] - nry1[z], x2);
				nn++;
			}

			z = zz;
		} while (z != 0);
		if (nn < 3)
			return (0);

		n = 0;
		z = 0;
		do {
			zz = z + 1;
			if (zz == nn)
				zz = 0;
			y1 = nry2[z];
			y2 = nry2[zz] - y1;

			if ((cy1 <= y1) && (y1 <= cy2)) {
				nry1[n] = y1;
				nrx1[n] = nrx2[z];
				n++;
			}

			if (y2 <= 0)
				y = cy2;
			else
				y = cy1;
			t = y - y1;
			if (((t - y2) ^ t) < 0) {
				nry1[n] = y;
				nrx1[n] = nrx2[z] + scale(t, nrx2[zz] - nrx2[z], y2);
				n++;
			}

			if (y2 <= 0)
				y = cy1;
			else
				y = cy2;
			t = y - y1;
			if (((t - y2) ^ t) < 0) {
				nry1[n] = y;
				nrx1[n] = nrx2[z] + scale(t, nrx2[zz] - nrx2[z], y2);
				n++;
			}

			z = zz;
		} while (z != 0);
		return (n);
	}

	@Override
	public void nextpage() {
		/* nothing */ }

	@Override
	public void init() {
		/* nothing */ }

	@Override
	public void uninit() {
		/* nothing */ }

}
