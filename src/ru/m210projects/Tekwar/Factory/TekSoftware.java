package ru.m210projects.Tekwar.Factory;

import ru.m210projects.Build.Engine;
import ru.m210projects.Build.Render.Software.Software;

public class TekSoftware extends Software {

	public TekSoftware(Engine engine) {
		super(engine, new TekMapSettings());
	}

	/*
	 * @Override public void drawoverheadmap(int cposx, int cposy, int czoom, short
	 * cang) { int i, j, k, l = 0, x1, y1, x2 = 0, y2 = 0, x3, y3, x4, y4, ox, oy,
	 * xoff, yoff; int dax, day, cosang, sinang, xspan, yspan, sprx, spry; int
	 * xrepeat, yrepeat, z1, z2, startwall, endwall; int xvect, yvect, xvect2,
	 * yvect2; char col; WALL wal, wal2; SPRITE spr;
	 * 
	 * xvect = sintable[(-cang) & 2047] * czoom; yvect = sintable[(1536 - cang) &
	 * 2047] * czoom; xvect2 = mulscale(xvect, yxaspect, 16); yvect2 =
	 * mulscale(yvect, yxaspect, 16);
	 * 
	 * // Draw red lines for (i = 0; i < numsectors; i++) { startwall =
	 * sector[i].wallptr; endwall = sector[i].wallptr + sector[i].wallnum;
	 * 
	 * z1 = sector[i].ceilingz; z2 = sector[i].floorz;
	 * 
	 * for (j = startwall; j < endwall; j++) { wal = wall[j]; k = wal.nextwall; if
	 * (k < 0) continue;
	 * 
	 * if ((show2dwall[j >> 3] & (1 << (j & 7))) == 0) continue; if ((k > j) &&
	 * ((show2dwall[k >> 3] & (1 << (k & 7))) > 0)) continue;
	 * 
	 * if (sector[wal.nextsector].ceilingz == z1) if (sector[wal.nextsector].floorz
	 * == z2) if (((wal.cstat | wall[wal.nextwall].cstat) & (16 + 32)) == 0)
	 * continue;
	 * 
	 * col = 232;
	 * 
	 * if (gViewMode == kView2DIcon) { if (sector[i].floorz != sector[i].ceilingz)
	 * if (sector[wal.nextsector].floorz != sector[wal.nextsector].ceilingz) if
	 * (((wal.cstat | wall[wal.nextwall].cstat) & (16 + 32)) == 0) if
	 * (sector[i].floorz == sector[wal.nextsector].floorz) continue; if
	 * (sector[i].floorpicnum != sector[wal.nextsector].floorpicnum) continue; if
	 * (sector[i].floorshade != sector[wal.nextsector].floorshade) continue; col =
	 * 232; }
	 * 
	 * ox = wal.x - cposx; oy = wal.y - cposy; x1 = dmulscale(ox, xvect, -oy, yvect,
	 * 16) + (xdim << 11); y1 = dmulscale(oy, xvect2, ox, yvect2, 16) + (ydim <<
	 * 11);
	 * 
	 * wal2 = wall[wal.point2]; ox = wal2.x - cposx; oy = wal2.y - cposy; x2 =
	 * dmulscale(ox, xvect, -oy, yvect, 16) + (xdim << 11); y2 = dmulscale(oy,
	 * xvect2, ox, yvect2, 16) + (ydim << 11);
	 * 
	 * drawline256(x1, y1, x2, y2, col); } }
	 * 
	 * // Draw sprites k = gPlayer[screenpeek].playersprite; for (i = 0; i <
	 * numsectors; i++) for (j = headspritesect[i]; j >= 0; j = nextspritesect[j])
	 * if ((show2dsprite[j >> 3] & (1 << (j & 7))) > 0) { spr = sprite[j]; Tile pic
	 * = engine.getTile(spr.picnum);
	 * 
	 * if ((spr.cstat & 0x8000) != 0) continue; col = 56; if ((spr.cstat & 1) != 0)
	 * col = 248; if (j == k) continue;
	 * 
	 * ILoc oldLoc = game.pInt.getsprinterpolate(j); sprx = spr.x; spry = spr.y; if
	 * (oldLoc != null) { sprx += mulscale(spr.x - oldLoc.x, smoothratio, 16); spry
	 * += mulscale(spr.y - oldLoc.y, smoothratio, 16); }
	 * 
	 * switch (spr.cstat & 48) { case 0: ox = sprx - cposx; oy = spry - cposy; x1 =
	 * dmulscale(ox, xvect, -oy, yvect, 16); y1 = dmulscale(oy, xvect2, ox, yvect2,
	 * 16);
	 * 
	 * ox = (sintable[(spr.ang + 512) & 2047] >> 7); oy = (sintable[(spr.ang) &
	 * 2047] >> 7); x2 = dmulscale(ox, xvect, -oy, yvect, 16); y2 = dmulscale(oy,
	 * xvect, ox, yvect, 16);
	 * 
	 * x3 = mulscale(x2, yxaspect, 16); y3 = mulscale(y2, yxaspect, 16);
	 * 
	 * drawline256(x1 - x2 + (xdim << 11), y1 - y3 + (ydim << 11), x1 + x2 + (xdim
	 * << 11), y1 + y3 + (ydim << 11), col); drawline256(x1 - y2 + (xdim << 11), y1
	 * + x3 + (ydim << 11), x1 + x2 + (xdim << 11), y1 + y3 + (ydim << 11), col);
	 * drawline256(x1 + y2 + (xdim << 11), y1 - x3 + (ydim << 11), x1 + x2 + (xdim
	 * << 11), y1 + y3 + (ydim << 11), col); break; case 16: x1 = sprx; y1 = spry;
	 * xoff = (byte) (pic.getOffsetX() + spr.xoffset); if ((spr.cstat & 4) > 0) xoff
	 * = -xoff; k = spr.ang; l = spr.xrepeat; dax = sintable[k & 2047] * l; day =
	 * sintable[(k + 1536) & 2047] * l; l = pic.getWidth(); k = (l >> 1) + xoff; x1
	 * -= mulscale(dax, k, 16); x2 = x1 + mulscale(dax, l, 16); y1 -= mulscale(day,
	 * k, 16); y2 = y1 + mulscale(day, l, 16);
	 * 
	 * ox = x1 - cposx; oy = y1 - cposy; x1 = dmulscale(ox, xvect, -oy, yvect, 16);
	 * y1 = dmulscale(oy, xvect2, ox, yvect2, 16);
	 * 
	 * ox = x2 - cposx; oy = y2 - cposy; x2 = dmulscale(ox, xvect, -oy, yvect, 16);
	 * y2 = dmulscale(oy, xvect2, ox, yvect2, 16);
	 * 
	 * drawline256(x1 + (xdim << 11), y1 + (ydim << 11), x2 + (xdim << 11), y2 +
	 * (ydim << 11), col);
	 * 
	 * break; case 32: if (gViewMode == kView2D) { xoff = (byte) (pic.getOffsetX() +
	 * spr.xoffset); yoff = (byte) (pic.getOffsetY() + spr.yoffset); if ((spr.cstat
	 * & 4) > 0) xoff = -xoff; if ((spr.cstat & 8) > 0) yoff = -yoff;
	 * 
	 * k = spr.ang; cosang = sintable[(k + 512) & 2047]; sinang = sintable[k]; xspan
	 * = pic.getWidth(); xrepeat = spr.xrepeat; yspan = pic.getHeight(); yrepeat =
	 * spr.yrepeat;
	 * 
	 * dax = ((xspan >> 1) + xoff) * xrepeat; day = ((yspan >> 1) + yoff) * yrepeat;
	 * x1 = sprx + dmulscale(sinang, dax, cosang, day, 16); y1 = spry +
	 * dmulscale(sinang, day, -cosang, dax, 16); l = xspan * xrepeat; x2 = x1 -
	 * mulscale(sinang, l, 16); y2 = y1 + mulscale(cosang, l, 16); l = yspan *
	 * yrepeat; k = -mulscale(cosang, l, 16); x3 = x2 + k; x4 = x1 + k; k =
	 * -mulscale(sinang, l, 16); y3 = y2 + k; y4 = y1 + k;
	 * 
	 * ox = x1 - cposx; oy = y1 - cposy; x1 = dmulscale(ox, xvect, -oy, yvect, 16);
	 * y1 = dmulscale(oy, xvect2, ox, yvect2, 16);
	 * 
	 * ox = x2 - cposx; oy = y2 - cposy; x2 = dmulscale(ox, xvect, -oy, yvect, 16);
	 * y2 = dmulscale(oy, xvect2, ox, yvect2, 16);
	 * 
	 * ox = x3 - cposx; oy = y3 - cposy; x3 = dmulscale(ox, xvect, -oy, yvect, 16);
	 * y3 = dmulscale(oy, xvect2, ox, yvect2, 16);
	 * 
	 * ox = x4 - cposx; oy = y4 - cposy; x4 = dmulscale(ox, xvect, -oy, yvect, 16);
	 * y4 = dmulscale(oy, xvect2, ox, yvect2, 16);
	 * 
	 * drawline256(x1 + (xdim << 11), y1 + (ydim << 11), x2 + (xdim << 11), y2 +
	 * (ydim << 11), col);
	 * 
	 * drawline256(x2 + (xdim << 11), y2 + (ydim << 11), x3 + (xdim << 11), y3 +
	 * (ydim << 11), col);
	 * 
	 * drawline256(x3 + (xdim << 11), y3 + (ydim << 11), x4 + (xdim << 11), y4 +
	 * (ydim << 11), col);
	 * 
	 * drawline256(x4 + (xdim << 11), y4 + (ydim << 11), x1 + (xdim << 11), y1 +
	 * (ydim << 11), col);
	 * 
	 * } break; } }
	 * 
	 * // Draw white lines for (i = 0; i < numsectors; i++) { startwall =
	 * sector[i].wallptr; endwall = sector[i].wallptr + sector[i].wallnum;
	 * 
	 * k = -1; for (j = startwall; j < endwall; j++) { wal = wall[j]; if
	 * (wal.nextwall >= 0) continue;
	 * 
	 * if ((show2dwall[j >> 3] & (1 << (j & 7))) == 0) continue;
	 * 
	 * if (engine.getTile(wal.picnum).getWidth() == 0) continue; if
	 * (engine.getTile(wal.picnum).getHeight() == 0) continue;
	 * 
	 * if (j == k) { x1 = x2; y1 = y2; } else { ox = wal.x - cposx; oy = wal.y -
	 * cposy; x1 = dmulscale(ox, xvect, -oy, yvect, 16) + (xdim << 11); y1 =
	 * dmulscale(oy, xvect2, ox, yvect2, 16) + (ydim << 11); }
	 * 
	 * k = wal.point2; wal2 = wall[k]; ox = wal2.x - cposx; oy = wal2.y - cposy; x2
	 * = dmulscale(ox, xvect, -oy, yvect, 16) + (xdim << 11); y2 = dmulscale(oy,
	 * xvect2, ox, yvect2, 16) + (ydim << 11);
	 * 
	 * drawline256(x1, y1, x2, y2, 239); } }
	 * 
	 * // draw player for (i = connecthead; i >= 0; i = connectpoint2[i]) { SPRITE
	 * pPlayer = sprite[gPlayer[i].playersprite]; ILoc oldLoc =
	 * game.pInt.getsprinterpolate(gPlayer[i].playersprite);
	 * 
	 * sprx = pPlayer.x; spry = pPlayer.y; if (oldLoc != null) { sprx +=
	 * mulscale(pPlayer.x - oldLoc.x, smoothratio, 16); spry += mulscale(pPlayer.y -
	 * oldLoc.y, smoothratio, 16); }
	 * 
	 * ox = sprx - cposx; oy = spry - cposy;
	 * 
	 * int dx = dmulscale(ox, xvect, -oy, yvect, 16); int dy = dmulscale(oy, xvect2,
	 * ox, yvect2, 16);
	 * 
	 * int dang = (pPlayer.ang - cang) & 0x7FF;
	 * 
	 * if (i == screenpeek) { dx = 0; dy = screenpeek ^ i; dang = 0; }
	 * 
	 * if (i == screenpeek /* || pGameInfo.nGameType == 1) {
	 * 
	 * if (gViewMode == kView2D) { ox = (sintable[(pPlayer.ang + 512) & 2047] >> 7);
	 * oy = (sintable[(pPlayer.ang) & 2047] >> 7); x2 = 0; y2 = -(czoom << 5);
	 * 
	 * x3 = mulscale(x2, yxaspect, 16); y3 = mulscale(y2, yxaspect, 16);
	 * 
	 * col = 31;
	 * 
	 * drawline256(dx - x2 + (xdim << 11), dy - y3 + (ydim << 11), dx + x2 + (xdim
	 * << 11), dy + y3 + (ydim << 11), col); drawline256(dx - y2 + (xdim << 11), dy
	 * + x3 + (ydim << 11), dx + x2 + (xdim << 11), dy + y3 + (ydim << 11), col);
	 * drawline256(dx + y2 + (xdim << 11), dy - x3 + (ydim << 11), dx + x2 + (xdim
	 * << 11), dy + y3 + (ydim << 11), col); } else { int nZoom = mulscale(yxaspect,
	 * czoom * (klabs((sector[pPlayer.sectnum].floorz - pPlayer.z) >> 8) +
	 * pPlayer.yrepeat), 16); nZoom = BClipRange(nZoom, 22000, 0x20000); int sx =
	 * (dx << 4) + (xdim << 15); int sy = (dy << 4) + (ydim << 15);
	 * 
	 * rotatesprite(sx, sy, nZoom, (short) dang, pPlayer.picnum, pPlayer.shade,
	 * pPlayer.pal, (pPlayer.cstat & 2) >> 1, wx1, wy1, wx2, wy2); } } } }
	 * 
	 * @Override public void dosetaspect() { int i, j, k, x, xinc;
	 * 
	 * if (xyaspect != oxyaspect) { oxyaspect = xyaspect; j = xyaspect * 320;
	 * lookups[horizlookup2 + horizycent - 1] = divscale(131072, j, 26); for (i =
	 * ydim * 4 - 1; i >= 0; i--) if (i != (horizycent - 1)) { lookups[horizlookup +
	 * i] = divscale(1, i - (horizycent - 1), 28); lookups[horizlookup2 + i] =
	 * divscale(klabs(lookups[horizlookup + i]), j, 14); } } if ((xdimen != oxdimen)
	 * || (viewingrange != oviewingrange)) { oxdimen = xdimen; oviewingrange =
	 * viewingrange;
	 * 
	 * x = 0; xinc = xdimscale;
	 * 
	 * for (i = 0; i < xdimen; i++) { j = x & 65535; k = x >> 16; x += xinc; if (k <
	 * 0 || k >= 1279) break;
	 * 
	 * if (j != 0) j = mulscale(radarang[k + 1] - radarang[k], j, 16); radarang2[i]
	 * = (short) ((radarang[k] + j) >> 6); }
	 * 
	 * for (i = 1; i < 65536; i++) distrecip[i] = divscale(xdimen, i, 20);
	 * nytooclose = xdimen * 2100; nytoofar = 65536 * 16384 - 1048576; } }
	 */
}
