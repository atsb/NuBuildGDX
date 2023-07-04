package ru.m210projects.Wang;

import static ru.m210projects.Build.Engine.clipmove_sectnum;
import static ru.m210projects.Build.Engine.clipmove_x;
import static ru.m210projects.Build.Engine.clipmove_y;
import static ru.m210projects.Build.Engine.clipmove_z;
import static ru.m210projects.Build.Engine.clipmoveboxtracenum;
import static ru.m210projects.Build.Engine.sintable;
import static ru.m210projects.Wang.Game.tmp_ptr;
import static ru.m210projects.Wang.Gameutils.CLIPMASK_PLAYER;
import static ru.m210projects.Wang.Gameutils.MAX_CLIPBOX;
import static ru.m210projects.Wang.Gameutils.NORM_ANGLE;
import static ru.m210projects.Wang.Gameutils.SQ;
import static ru.m210projects.Wang.Gameutils.Z;
import static ru.m210projects.Wang.Sprites.*;
import static ru.m210projects.Wang.Main.engine;
import static ru.m210projects.Wang.Type.MyTypes.MOD4;

import java.util.Arrays;

import ru.m210projects.Build.Engine.Point;
import ru.m210projects.Wang.Type.LONGp;
import ru.m210projects.Wang.Type.PlayerStr;
import ru.m210projects.Wang.Type.Sector_Object;

public class MClip {

	private static int[] x = new int[MAX_CLIPBOX], y = new int[MAX_CLIPBOX];
	private static int[] ox = new int[MAX_CLIPBOX], oy = new int[MAX_CLIPBOX];

	public static int MultiClipMove(PlayerStr pp, int z, int floor_dist) {
		int i;
		Sector_Object sop = SectorObject[pp.sop];
		short ang;
		short min_ndx = 0;
		int min_dist = 999999;
		int dist;

		int ret_start;
		int ret;
		int min_ret = 0;

		int xvect, yvect;
		int xs, ys;

		Arrays.fill(x, 0);
		Arrays.fill(y, 0);
		Arrays.fill(ox, 0);
		Arrays.fill(oy, 0);

		for (i = 0; i < sop.clipbox_num; i++) {
			// move the box to position instead of using offset- this prevents small
			// rounding errors
			// allowing you to move through wall
			ang = (short) NORM_ANGLE(pp.getAnglei() + sop.clipbox_ang[i]);

			xs = pp.posx;
			ys = pp.posy;

			xvect = (sop.clipbox_vdist[i] * sintable[NORM_ANGLE(ang + 512)]);
			yvect = (sop.clipbox_vdist[i] * sintable[ang]);
			clipmoveboxtracenum = 1;
			ret_start = engine.clipmove(xs, ys, z, pp.cursectnum, xvect, yvect, sop.clipbox_dist[i], Z(4), floor_dist,
					CLIPMASK_PLAYER);

			if (clipmove_sectnum != -1) {
				xs = clipmove_x;
				ys = clipmove_y;
				z = clipmove_z;
				pp.cursectnum = clipmove_sectnum;
			}

			clipmoveboxtracenum = 3;

			if (ret_start != 0) {
				// hit something moving into start position
				min_dist = 0;
				min_ndx = (short) i;
				// ox is where it should be
				ox[i] = x[i] = pp.posx + (sop.clipbox_vdist[i] * sintable[NORM_ANGLE(ang + 512)] >> 14);
				oy[i] = y[i] = pp.posy + (sop.clipbox_vdist[i] * sintable[ang] >> 14);

				// xs is where it hit
				x[i] = xs;
				y[i] = ys;

				// see the dist moved
				dist = engine.ksqrt(SQ(x[i] - ox[i]) + SQ(y[i] - oy[i]));

				// save it off
				if (dist < min_dist) {
					min_dist = dist;
					min_ndx = (short) i;
					min_ret = ret_start;
				}
			} else {
				// save off the start position
				ox[i] = x[i] = xs;
				oy[i] = y[i] = ys;

				// move the box
				ret = engine.clipmove(x[i], y[i], z, pp.cursectnum, pp.xvect, pp.yvect, sop.clipbox_dist[i], Z(4),
						floor_dist, CLIPMASK_PLAYER);
				if (clipmove_sectnum != -1) {
					x[i] = clipmove_x;
					y[i] = clipmove_y;
					z = clipmove_z;
					pp.cursectnum = clipmove_sectnum;
				}

				// save the dist moved
				dist = engine.ksqrt(SQ(x[i] - ox[i]) + SQ(y[i] - oy[i]));

				if (dist < min_dist) {
					min_dist = dist;
					min_ndx = (short) i;
					min_ret = ret;
				}
			}
		}

		// put posx and y off from offset
		pp.posx += x[min_ndx] - ox[min_ndx];
		pp.posy += y[min_ndx] - oy[min_ndx];

		return (min_ret);
	}

	public static boolean MultiClipTurn(PlayerStr pp, short new_ang, int z, int floor_dist) {
		int i;
		Sector_Object sop = SectorObject[pp.sop];
		int ret;
		int x, y;
		short ang;
		int xvect, yvect;
		short cursectnum = pp.cursectnum;

		for (i = 0; i < sop.clipbox_num; i++) {
			ang = (short) NORM_ANGLE(new_ang + sop.clipbox_ang[i]);

			x = pp.posx;
			y = pp.posy;

			xvect = (sop.clipbox_vdist[i] * sintable[NORM_ANGLE(ang + 512)]);
			yvect = (sop.clipbox_vdist[i] * sintable[ang]);

			// move the box
			ret = engine.clipmove(x, y, z, cursectnum, xvect, yvect, sop.clipbox_dist[i], Z(4), floor_dist,
					CLIPMASK_PLAYER);
			if (clipmove_sectnum != -1) {
				x = clipmove_x;
				y = clipmove_y;
				z = clipmove_z;
				cursectnum = clipmove_sectnum;
			}

			if (ret != 0)
				return (false);
		}

		return (true);
	}

	public static boolean testquadinsect(LONGp point_num, int qx[], int qy[], short sectnum) {
		int i, next_i;

		point_num.value = -1;

		for (i = 0; i < 4; i++) {
			if (engine.inside(qx[i], qy[i], sectnum) == 0) {
				point_num.value = i;

				return (false);
			}
		}

		for (i = 0; i < 4; i++) {
			next_i = MOD4(i + 1);
			if (!engine.cansee(qx[i], qy[i], 0x3fffffff, sectnum, qx[next_i], qy[next_i], 0x3fffffff, sectnum)) {
//              DSPRINTF(ds,"cansee %ld failed, x1 %d, y1 %d, x2 %d, y2 %d, sectnum %d",i, qx[i], qy[i], qx[next_i], qy[next_i], sectnum);
				return (false);
			}
		}

		return (true);
	}

	// Ken gives the tank clippin' a try...
	public static boolean RectClipMove(PlayerStr pp, int[] qx, int[] qy) {
		int i;

		Arrays.fill(x, 0, 4, 0);
		Arrays.fill(y, 0, 4, 0);

		for (i = 0; i < 4; i++) {
			x[i] = qx[i] + (pp.xvect >> 14);
			y[i] = qy[i] + (pp.yvect >> 14);
		}

		// Given the 4 points: x[4], y[4]
		if (testquadinsect(tmp_ptr[0], x, y, pp.cursectnum)) {
			pp.posx += (pp.xvect >> 14);
			pp.posy += (pp.yvect >> 14);
			return (true);
		}

		if (tmp_ptr[0].value < 0)
			return (false);

		if ((tmp_ptr[0].value == 0) || (tmp_ptr[0].value == 3)) // Left side bad - strafe right
		{
			for (i = 0; i < 4; i++) {
				x[i] = qx[i] - (pp.yvect >> 15);
				y[i] = qy[i] + (pp.xvect >> 15);
			}
			if (testquadinsect(tmp_ptr[0], x, y, pp.cursectnum)) {
				pp.posx -= (pp.yvect >> 15);
				pp.posy += (pp.xvect >> 15);
			}

			return (false);
		}

		if ((tmp_ptr[0].value == 1) || (tmp_ptr[0].value == 2)) // Right side bad - strafe left
		{
			for (i = 0; i < 4; i++) {
				x[i] = qx[i] + (pp.yvect >> 15);
				y[i] = qy[i] - (pp.xvect >> 15);
			}
			if (testquadinsect(tmp_ptr[0], x, y, pp.cursectnum)) {
				pp.posx += (pp.yvect >> 15);
				pp.posy -= (pp.xvect >> 15);
			}

			return (false);
		}

		return (false);
	}

	public static int testpointinquad(int x, int y, int[] qx, int[] qy) {
		int i, cnt, x1, y1, x2, y2;

		cnt = 0;
		for (i = 0; i < 4; i++) {
			y1 = qy[i] - y;
			y2 = qy[(i + 1) & 3] - y;
			if ((y1 ^ y2) >= 0)
				continue;

			x1 = qx[i] - x;
			x2 = qx[(i + 1) & 3] - x;
			if ((x1 ^ x2) >= 0)
				cnt ^= x1;
			else
				cnt ^= (x1 * y2 - x2 * y1) ^ y2;
		}
		return (cnt >> 31);
	}

	public static boolean RectClipTurn(PlayerStr pp, short new_ang, int[] qx, int[] qy, int[] ox, int[] oy) {
		int i;

		Arrays.fill(x, 0, 4, 0);
		Arrays.fill(y, 0, 4, 0);

		Sector_Object sop = SectorObject[pp.sop];
		short rot_ang;

		rot_ang = (short) NORM_ANGLE(new_ang + sop.spin_ang - sop.ang_orig);
		for (i = 0; i < 4; i++) {
			Point p = engine.rotatepoint(pp.posx, pp.posy, ox[i], oy[i], rot_ang);
			x[i] = p.getX();
			y[i] = p.getY();
			// cannot use sop.xmid and ymid because the SO is off the map at this point
		}

		// Given the 4 points: x[4], y[4]
		if (testquadinsect(tmp_ptr[0], x, y, pp.cursectnum)) {
			// move to new pos
			for (i = 0; i < 4; i++) {
				qx[i] = x[i];
				qy[i] = y[i];
			}
			return (true);
		}

		if (tmp_ptr[0].value < 0)
			return (false);

		return (false);
	}

}
