package ru.m210projects.Wang;

import static ru.m210projects.Build.Engine.headspritestat;
import static ru.m210projects.Build.Engine.nextspritestat;
import static ru.m210projects.Build.Engine.sector;
import static ru.m210projects.Build.Engine.sprite;
import static ru.m210projects.Wang.Factory.WangNetwork.Prediction;
import static ru.m210projects.Wang.Gameutils.RANDOM_RANGE;
import static ru.m210projects.Wang.Gameutils.SECTFX_WARP_SECTOR;
import static ru.m210projects.Wang.Gameutils.SP_TAG10;
import static ru.m210projects.Wang.Gameutils.SP_TAG2;
import static ru.m210projects.Wang.Gameutils.SP_TAG5;
import static ru.m210projects.Wang.Gameutils.SP_TAG6;
import static ru.m210projects.Wang.Gameutils.SP_TAG7;
import static ru.m210projects.Wang.Gameutils.SP_TAG8;
import static ru.m210projects.Wang.Gameutils.Z;
import static ru.m210projects.Wang.Main.engine;
import static ru.m210projects.Wang.Names.STAT_WARP;
import static ru.m210projects.Wang.Stag.WARP_CEILING_PLANE;
import static ru.m210projects.Wang.Stag.WARP_FLOOR_PLANE;
import static ru.m210projects.Wang.Stag.WARP_TELEPORTER;
import static ru.m210projects.Wang.Type.MyTypes.TEST;

import java.util.Arrays;

import ru.m210projects.Build.Types.BuildPos;
import ru.m210projects.Build.Types.SPRITE;

public class Warp {

	public static BuildPos warp = new BuildPos();

	private static short[] match_rand = new short[16];
	private static SPRITE WarpPlaneSectorInfo_fl, WarpPlaneSectorInfo_ceil;

	private static boolean WarpPlaneSectorInfo(short sectnum) {
		int i, nexti;
		SPRITE sp;

		WarpPlaneSectorInfo_fl = null;
		WarpPlaneSectorInfo_ceil = null;

		if (Prediction)
			return false;

		if (sectnum != -1 && !TEST(sector[sectnum].extra, SECTFX_WARP_SECTOR))
			return false;

		for (i = headspritestat[STAT_WARP]; i != -1; i = nexti) {
			nexti = nextspritestat[i];
			sp = sprite[i];

			if (sp.sectnum == sectnum) {
				// skip - don't teleport
				if (SP_TAG10(sp) == 1)
					continue;

				if (sp.hitag == WARP_CEILING_PLANE) {
					WarpPlaneSectorInfo_ceil = sp;
				} else if (sp.hitag == WARP_FLOOR_PLANE) {
					WarpPlaneSectorInfo_fl = sp;
				}
			}
		}

		return true;
	}

	public static SPRITE WarpPlane(int x, int y, int z, short sectnum) {

		if (Prediction)
			return null;

		if (!WarpPlaneSectorInfo(sectnum))
			return null;

		if (WarpPlaneSectorInfo_ceil != null) {
			if (z <= WarpPlaneSectorInfo_ceil.z) {
				return (WarpToArea(WarpPlaneSectorInfo_ceil, x, y, z, sectnum));
			}
		}

		if (WarpPlaneSectorInfo_fl != null) {
			if (z >= WarpPlaneSectorInfo_fl.z) {
				return (WarpToArea(WarpPlaneSectorInfo_fl, x, y, z, sectnum));
			}
		}

		return null;
	}

	private static SPRITE WarpToArea(SPRITE sp_from, int x, int y, int z, short sectnum) {
		int xoff;
		int yoff;
		int zoff;
		short i, nexti;
		SPRITE sp = sp_from;
		short match;
		short to_tag = 0;
		int z_adj = 0;

		xoff = x - sp.x;
		yoff = y - sp.y;
		zoff = z - sp.z;
		match = sp.lotag;

		Arrays.fill(match_rand, (short) 0);

		switch (sp.hitag) {
		case WARP_TELEPORTER:
			to_tag = WARP_TELEPORTER;

			// if tag 5 has something this is a random teleporter
			if (SP_TAG5(sp) != 0) {
				short ndx = 0;
				match_rand[ndx++] = (short) SP_TAG2(sp);
				match_rand[ndx++] = (short) SP_TAG5(sp);
				if (SP_TAG6(sp) != 0)
					match_rand[ndx++] = (short) SP_TAG6(sp);
				if (SP_TAG7(sp) != 0)
					match_rand[ndx++] = (short) SP_TAG7(sp);
				if (SP_TAG8(sp) != 0)
					match_rand[ndx++] = (short) SP_TAG8(sp);

				// reset the match you are looking for
				match = match_rand[RANDOM_RANGE(ndx)];
			}
			break;
		case WARP_CEILING_PLANE:
			to_tag = WARP_FLOOR_PLANE;
			// make sure you warp outside of warp plane
			z_adj = -Z(2);
			break;
		case WARP_FLOOR_PLANE:
			to_tag = WARP_CEILING_PLANE;
			// make sure you warp outside of warp plane
			z_adj = Z(2);
			break;
		}

		for (i = headspritestat[STAT_WARP]; i != -1; i = nexti) {
			nexti = nextspritestat[i];
			sp = sprite[i];

			if (sp.lotag == match && sp != sp_from) {
				// exp: WARP_CEILING or WARP_CEILING_PLANE
				if (sp.hitag == to_tag) {
					// determine new x,y,z position
					x = warp.x = sp.x + xoff;
					y = warp.y = sp.y + yoff;
					z = sp.z + zoff;

					// make sure you warp outside of warp plane
					z += z_adj;

					warp.z = z;

					// get new sector
					sectnum = warp.sectnum = engine.updatesector(x, y, sp.sectnum);

					if (sectnum >= 0)
						return (sp);
				}
			}
		}

		return null;
	}

	////////////////////////////////////////////////////////////////////////////////
	//
	// Warp - Teleporter style
	//
	////////////////////////////////////////////////////////////////////////////////

	public static SPRITE WarpSectorInfo(short sectnum) {
		int i, nexti;
		SPRITE sp;

		SPRITE sp_warp = null;

		if (!TEST(sector[sectnum].extra, SECTFX_WARP_SECTOR))
			return null;

		for (i = headspritestat[STAT_WARP]; i != -1; i = nexti) {
			nexti = nextspritestat[i];
			sp = sprite[i];

			if (sp.sectnum == sectnum) {
				// skip - don't teleport
				if (SP_TAG10(sp) == 1)
					continue;

				if (sp.hitag == WARP_TELEPORTER) {
					sp_warp = sp;
				}
			}
		}

		return sp_warp;
	}

	public static SPRITE WarpM(int x, int y, int z, short sectnum) {
		if (Prediction)
			return null;

		SPRITE sp_warp = WarpSectorInfo(sectnum);
		if (sp_warp != null)
			return (WarpToArea(sp_warp, x, y, z, sectnum));

		return null;
	}

}
