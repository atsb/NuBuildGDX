package ru.m210projects.Wang;

import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Wang.Gameutils.*;
import static ru.m210projects.Wang.Names.*;
import static ru.m210projects.Wang.Sprites.*;
import static ru.m210projects.Wang.Main.*;
import static ru.m210projects.Wang.Track.*;
import static ru.m210projects.Wang.Weapon.*;
import static ru.m210projects.Wang.Type.MyTypes.*;

import ru.m210projects.Build.Types.SECTOR;
import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Build.Types.WALL;
import ru.m210projects.Wang.Type.LONGp;
import ru.m210projects.Wang.Type.Sector_Object;

public class WallMove {

	private static LONGp nx = new LONGp(), ny = new LONGp();

	public static void SOwallmove(Sector_Object sop, int sp, WALL find_WALL, int dist, LONGp nx, LONGp ny) {
		int j, k, wallcount;
		WALL wp;
		short startwall, endwall;
		SECTOR sectp;

		if (TEST(sop.flags, SOBJ_SPRITE_OBJ))
			return;

		wallcount = 0;

		for (j = 0; sop.sector[j] != -1; j++) {
			sectp = sector[sop.sector[j]];

			startwall = sectp.wallptr;
			endwall = (short) (startwall + sectp.wallnum - 1);

			// move all walls in sectors back to the original position
			for (k = startwall; k <= endwall; k++) {
				wp = wall[k];
				// find the one wall we want to adjust
				if (wp == find_WALL) {
					short ang;
					// move orig x and y in saved angle

					ang = pUser[sp].sang;

					nx.value = ((dist * sintable[NORM_ANGLE(ang + 512)]) >> 14);
					ny.value = ((dist * sintable[ang]) >> 14);

					sop.xorig[wallcount] -= nx.value;
					sop.yorig[wallcount] -= ny.value;

					sop.flags |= (SOBJ_UPDATE_ONCE);
					return;
				}

				wallcount++;
			}
		}
	}

	public static boolean DoWallMove(int spnum) {
		int dist;
		short shade1, shade2, ang, picnum1, picnum2;
		short prev_wall;
		boolean found = false;
		short dang;
		boolean SOsprite = false;

		SPRITE sp = sprite[spnum];

		dist = SP_TAG13(sp);
		ang = (short) SP_TAG4(sp);
		picnum1 = (short) SP_TAG5(sp);
		picnum2 = (short) SP_TAG6(sp);
		shade1 = (short) SP_TAG7(sp);
		shade2 = (short) SP_TAG8(sp);
		dang = (short) ((SP_TAG10(sp)) << 3);

		if (dang != 0)
			ang = (short) NORM_ANGLE(ang + (RANDOM_RANGE(dang) - dang / 2));

		nx.value = (dist * sintable[NORM_ANGLE(ang + 512)]) >> 14;
		ny.value = (dist * sintable[ang]) >> 14;

		for (int w = 0; w < numwalls; w++) {
			WALL WALL = wall[w];
			if (WALL.x == sp.x && WALL.y == sp.y) {
				found = true;

				if (TEST(WALL.extra, WALLFX_SECTOR_OBJECT)) {
					Sector_Object sop;
					sop = DetectSectorObjectByWall(WALL);
					SOwallmove(sop, spnum, WALL, dist, nx, ny);

					SOsprite = true;
				} else {
					WALL.x = sp.x + nx.value;
					WALL.y = sp.y + ny.value;
				}

				if (shade1 != 0)
					WALL.shade = (byte) shade1;
				if (picnum1 != 0)
					WALL.picnum = picnum1;

				// find the previous wall
				prev_wall = PrevWall((short) w);
				if (shade2 != 0)
					wall[prev_wall].shade = (byte) shade2;
				if (picnum2 != 0)
					wall[prev_wall].picnum = picnum2;
			}
		}

		SET_SP_TAG9(sp, SP_TAG9(sp) - 1);
		if ((byte) SP_TAG9(sp) <= 0) {
			KillSprite(spnum);
		} else {
			if (SOsprite) {
				// move the sprite offset from center
				pUser[spnum].sx -= nx.value;
				pUser[spnum].sy -= ny.value;
			} else {
				sp.x += nx.value;
				sp.y += ny.value;
			}
		}

		return (found);
	}

	public static boolean CanSeeWallMove(SPRITE wp, int match) {
		short i, nexti;
		boolean found = false;
		SPRITE sp;

		for (i = headspritestat[STAT_WALL_MOVE_CANSEE]; i != -1; i = nexti) {
			nexti = nextspritestat[i];
			sp = sprite[i];

			if (SP_TAG2(sp) == match) {
				found = true;

				if (engine.cansee(wp.x, wp.y, wp.z, wp.sectnum, sp.x, sp.y, sp.z, sp.sectnum)) {
					return (true);
				}
			}
		}

		if (found)
			return (false);
		else
			return (true);
	}

	public static boolean DoWallMoveMatch(int match) {
		SPRITE sp;
		short i, nexti;
		boolean found = false;

		// just all with the same matching tags
		for (i = headspritestat[STAT_WALL_MOVE]; i != -1; i = nexti) {
			nexti = nextspritestat[i];
			sp = sprite[i];

			if (SP_TAG2(sp) == match) {
				found = true;
				DoWallMove(i);
			}
		}

		return (found);
	}

}
