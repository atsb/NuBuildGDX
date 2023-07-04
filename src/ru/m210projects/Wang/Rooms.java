package ru.m210projects.Wang;

import static ru.m210projects.Build.Engine.CEIL;
import static ru.m210projects.Build.Engine.FLOOR;
import static ru.m210projects.Build.Engine.gotpic;
import static ru.m210projects.Build.Engine.headspritesect;
import static ru.m210projects.Build.Engine.headspritestat;
import static ru.m210projects.Build.Engine.nextspritesect;
import static ru.m210projects.Build.Engine.nextspritestat;
import static ru.m210projects.Build.Engine.numsectors;
import static ru.m210projects.Build.Engine.pHitInfo;
import static ru.m210projects.Build.Engine.sector;
import static ru.m210projects.Build.Engine.sintable;
import static ru.m210projects.Build.Engine.sprite;
import static ru.m210projects.Build.Engine.wall;
import static ru.m210projects.Build.Gameutils.isValidSector;
import static ru.m210projects.Build.Pragmas.klabs;
import static ru.m210projects.Build.Pragmas.scale;
import static ru.m210projects.Wang.Draw.analyzesprites;
import static ru.m210projects.Wang.Draw.post_analyzesprites;
import static ru.m210projects.Wang.Game.PlaxCeilGlobZadjust;
import static ru.m210projects.Wang.Game.PlaxFloorGlobZadjust;
import static ru.m210projects.Wang.Game.tmp_ptr;
import static ru.m210projects.Wang.Gameutils.CEILING_STAT_FAF_BLOCK_HITSCAN;
import static ru.m210projects.Wang.Gameutils.CEILING_STAT_PLAX;
import static ru.m210projects.Wang.Gameutils.CLIPMASK_MISSILE;
import static ru.m210projects.Wang.Gameutils.CLIPMASK_WARP_HITSCAN;
import static ru.m210projects.Wang.Gameutils.CSTAT_WALL_WARP_HITSCAN;
import static ru.m210projects.Wang.Gameutils.FAF_ConnectArea;
import static ru.m210projects.Wang.Gameutils.FAF_ConnectCeiling;
import static ru.m210projects.Wang.Gameutils.FAF_ConnectFloor;
import static ru.m210projects.Wang.Gameutils.FAF_MIRROR_PIC;
import static ru.m210projects.Wang.Gameutils.FAF_PLACE_MIRROR_PIC;
import static ru.m210projects.Wang.Gameutils.FLOOR_STAT_FAF_BLOCK_HITSCAN;
import static ru.m210projects.Wang.Gameutils.FLOOR_STAT_PLAX;
import static ru.m210projects.Wang.Gameutils.HIT_MASK;
import static ru.m210projects.Wang.Gameutils.HIT_SECTOR;
import static ru.m210projects.Wang.Gameutils.HIT_SPRITE;
import static ru.m210projects.Wang.Gameutils.NORM_ANGLE;
import static ru.m210projects.Wang.Gameutils.NORM_SECTOR;
import static ru.m210projects.Wang.Gameutils.SECTFX_WARP_SECTOR;
import static ru.m210projects.Wang.Gameutils.SECTFX_Z_ADJUST;
import static ru.m210projects.Wang.Gameutils.SQ;
import static ru.m210projects.Wang.Gameutils.Z;
import static ru.m210projects.Wang.Main.engine;
import static ru.m210projects.Wang.Names.STAT_FAF;
import static ru.m210projects.Wang.Names.STAT_ST1;
import static ru.m210projects.Wang.Sector.SectUser;
import static ru.m210projects.Wang.Stag.CEILING_Z_ADJUST;
import static ru.m210projects.Wang.Stag.FLOOR_Z_ADJUST;
import static ru.m210projects.Wang.Stag.VIEW_LEVEL1;
import static ru.m210projects.Wang.Stag.VIEW_LEVEL2;
import static ru.m210projects.Wang.Stag.VIEW_LEVEL6;
import static ru.m210projects.Wang.Stag.VIEW_THRU_CEILING;
import static ru.m210projects.Wang.Stag.VIEW_THRU_FLOOR;
import static ru.m210projects.Wang.Type.MyTypes.DTEST;
import static ru.m210projects.Wang.Type.MyTypes.TEST;
import static ru.m210projects.Wang.Warp.WarpM;
import static ru.m210projects.Wang.Warp.WarpPlane;
import static ru.m210projects.Wang.Warp.WarpSectorInfo;
import static ru.m210projects.Wang.Warp.warp;

import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Build.Types.Hitscan;
import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Wang.Type.LONGp;
import ru.m210projects.Wang.Type.Save;
import ru.m210projects.Wang.Type.Sect_User;

public class Rooms {

	public static final int ZMAX = 400;
	public static Save save = new Save();
	public static int[] zofslope = new int[2];
	public static short[] GlobStackSect = new short[2];

	public static short COVERupdatesector(int x, int y, short newsector) {
		if (isValidSector(newsector))
			return engine.updatesector(x, y, newsector);
		return -1;
	}

	public static short COVERinsertsprite(short sectnum, int stat) {
		short spnum = engine.insertsprite(sectnum, (short) stat);
		if(spnum == -1)
			return -1;

		sprite[spnum].x = sprite[spnum].y = sprite[spnum].z = 0;
		sprite[spnum].cstat = 0;
		sprite[spnum].picnum = 0;
		sprite[spnum].shade = 0;
		sprite[spnum].pal = 0;
		sprite[spnum].clipdist = 0;
		sprite[spnum].xrepeat = sprite[spnum].yrepeat = 0;
		sprite[spnum].xoffset = sprite[spnum].yoffset = 0;
		sprite[spnum].ang = 0;
		sprite[spnum].owner = -1;
		sprite[spnum].xvel = sprite[spnum].yvel = sprite[spnum].zvel = 0;
		sprite[spnum].lotag = 0;
		sprite[spnum].hitag = 0;
		sprite[spnum].extra = 0;

		return (spnum);
	}

	public static boolean FAF_Sector(short sectnum) {
		if(!isValidSector(sectnum))
			return false;

		short Next;
		for (short SpriteNum = headspritesect[sectnum]; SpriteNum != -1; SpriteNum = Next) {
			Next = nextspritesect[SpriteNum];
			SPRITE sp = sprite[SpriteNum];

			if (sp.statnum == STAT_FAF && (sp.hitag >= VIEW_LEVEL1 && sp.hitag <= VIEW_LEVEL6)) {
				return true;
			}
		}

		return false;
	}

	public static void SetWallWarpHitscan(short sectnum) {
		short start_wall, wall_num;
		SPRITE sp_warp = WarpSectorInfo(sectnum);

		if (sp_warp == null)
			return;

		// move the the next wall
		wall_num = start_wall = sector[sectnum].wallptr;

		// Travel all the way around loop setting wall bits
		do {
			if (wall[wall_num].nextwall >= 0)
				wall[wall_num].cstat |= (CSTAT_WALL_WARP_HITSCAN);
			wall_num = wall[wall_num].point2;
		} while (wall_num != start_wall);
	}

	public static void ResetWallWarpHitscan(short sectnum) {
		short start_wall, wall_num;

		// move the the next wall
		wall_num = start_wall = sector[sectnum].wallptr;

		// Travel all the way around loop setting wall bits
		do {
			wall[wall_num].cstat &= ~(CSTAT_WALL_WARP_HITSCAN);
			wall_num = wall[wall_num].point2;
		} while (wall_num != start_wall);
	}

	public static void FAFhitscan(int x, int y, int z, short sectnum, int xvect, int yvect, int zvect, Hitscan hit,
			int clipmask) {
		short newsectnum = sectnum;
		int startclipmask = 0;
		boolean plax_found = false;

		if (clipmask == CLIPMASK_MISSILE)
			startclipmask = CLIPMASK_WARP_HITSCAN;

		engine.hitscan(x, y, z, sectnum, xvect, yvect, zvect, hit, startclipmask);

		if (hit.hitsect < 0)
			return;

		if (hit.hitwall >= 0) {
			// hitscan warping
			if (TEST(wall[hit.hitwall].cstat, CSTAT_WALL_WARP_HITSCAN)) {
				// back it up a bit to get a correct warp location
				hit.hitx -= xvect >> 9;
				hit.hity -= yvect >> 9;

				// warp to new x,y,z, sectnum
				if (WarpM(hit.hitx, hit.hity, hit.hitz, hit.hitsect) != null) {
					hit.hitx = warp.x;
					hit.hity = warp.y;
					hit.hitz = warp.z;
					hit.hitsect = warp.sectnum;

					short dest_sect = hit.hitsect;

					// hitscan needs to pass through dest sect
					ResetWallWarpHitscan(dest_sect);

					// NOTE: This could be recursive I think if need be
					engine.hitscan(hit.hitx, hit.hity, hit.hitz, hit.hitsect, xvect, yvect, zvect, hit, startclipmask);

					// reset hitscan block for dest sect
					SetWallWarpHitscan(dest_sect);

					return;
				}
			}
		}

		// make sure it hit JUST a sector before doing a check
		if (hit.hitwall < 0 && hit.hitsprite < 0) {
			if (TEST(sector[hit.hitsect].extra, SECTFX_WARP_SECTOR)) {
				if (TEST(wall[sector[hit.hitsect].wallptr].cstat, CSTAT_WALL_WARP_HITSCAN)) {
					// hit the floor of a sector that is a warping sector
					if (WarpM(hit.hitx, hit.hity, hit.hitz, hit.hitsect) != null) {
						hit.hitx = warp.x;
						hit.hity = warp.y;
						hit.hitz = warp.z;
						hit.hitsect = warp.sectnum;

						engine.hitscan(hit.hitx, hit.hity, hit.hitz, hit.hitsect, xvect, yvect, zvect, hit, clipmask);
						return;
					}
				} else {
					if (WarpPlane(hit.hitx, hit.hity, hit.hitz, hit.hitsect) != null) {
						hit.hitx = warp.x;
						hit.hity = warp.y;
						hit.hitz = warp.z;
						hit.hitsect = warp.sectnum;

						engine.hitscan(hit.hitx, hit.hity, hit.hitz, hit.hitsect, xvect, yvect, zvect, hit, clipmask);

						return;
					}
				}
			}

			engine.getzsofslope(hit.hitsect, hit.hitx, hit.hity, zofslope);
			if (klabs(hit.hitz - zofslope[FLOOR]) < Z(4)) {
				if (FAF_ConnectFloor(hit.hitsect)
						&& !TEST(sector[hit.hitsect].floorstat, FLOOR_STAT_FAF_BLOCK_HITSCAN)) {
					newsectnum = engine.updatesectorz(hit.hitx, hit.hity, hit.hitz + Z(12), newsectnum);
					plax_found = true;
				}
			} else if (klabs(hit.hitz - zofslope[CEIL]) < Z(4)) {
				if (FAF_ConnectCeiling(hit.hitsect)
						&& !TEST(sector[hit.hitsect].floorstat, CEILING_STAT_FAF_BLOCK_HITSCAN)) {
					newsectnum = engine.updatesectorz(hit.hitx, hit.hity, hit.hitz - Z(12), newsectnum);
					plax_found = true;
				}
			}
		}

		if (plax_found) {
			engine.hitscan(hit.hitx, hit.hity, hit.hitz, newsectnum, xvect, yvect, zvect, hit, clipmask);
		}
	}

	public static boolean FAFcansee(int xs, int ys, int zs, short sects, int xe, int ye, int ze, short secte) {
		short newsectnum = sects;
		int xvect, yvect, zvect;
		short ang;
		int dist;
		boolean plax_found = false;

		// early out to regular routine
		if (!FAF_Sector(sects) && !FAF_Sector(secte)) {
			return (engine.cansee(xs, ys, zs, sects, xe, ye, ze, secte));
		} else {
		}

		// get angle
		ang = engine.getangle(xe - xs, ye - ys);

		// get x,y,z, vectors
		xvect = sintable[NORM_ANGLE(ang + 512)];
		yvect = sintable[NORM_ANGLE(ang)];

		// find the distance to the target
		dist = engine.ksqrt(SQ(xe - xs) + SQ(ye - ys));

		if (dist != 0) {
			if (xe - xs != 0)
				zvect = scale(xvect, ze - zs, xe - xs);
			else if (ye - ys != 0)
				zvect = scale(yvect, ze - zs, ye - ys);
			else
				zvect = 0;
		} else
			zvect = 0;

		engine.hitscan(xs, ys, zs, sects, xvect, yvect, zvect, pHitInfo, CLIPMASK_MISSILE);

		if (pHitInfo.hitsect < 0)
			return false;

		// make sure it hit JUST a sector before doing a check
		if (pHitInfo.hitwall < 0 && pHitInfo.hitsprite < 0) {
			engine.getzsofslope(pHitInfo.hitsect, pHitInfo.hitx, pHitInfo.hity, zofslope);
			if (klabs(pHitInfo.hitz - zofslope[FLOOR]) < Z(4)) {
				if (FAF_ConnectFloor(pHitInfo.hitsect)) {
					newsectnum = engine.updatesectorz(pHitInfo.hitx, pHitInfo.hity, pHitInfo.hitz + Z(12), newsectnum);
					plax_found = true;
				}
			} else if (klabs(pHitInfo.hitz - zofslope[CEIL]) < Z(4)) {
				if (FAF_ConnectCeiling(pHitInfo.hitsect)) {
					newsectnum = engine.updatesectorz(pHitInfo.hitx, pHitInfo.hity, pHitInfo.hitz - Z(12), newsectnum);
					plax_found = true;
				}
			}
		} else {
			return (engine.cansee(xs, ys, zs, sects, xe, ye, ze, secte));
		}

		if (plax_found)
			return (engine.cansee(pHitInfo.hitx, pHitInfo.hity, pHitInfo.hitz, newsectnum, xe, ye, ze, secte));

		return false;
	}

	public static int GetZadjustment(short sectnum, int hitag) {
		short i, nexti;
		SPRITE sp;

		if(!isValidSector(sectnum))
			return 0;

		if (!TEST(sector[sectnum].extra, SECTFX_Z_ADJUST))
			return (0);

		for (i = headspritestat[STAT_ST1]; i != -1; i = nexti) {
			nexti = nextspritestat[i];
			sp = sprite[i];

			if (sp.hitag == hitag && sp.sectnum == sectnum) {
				return (Z(sp.lotag));
			}
		}

		return (0);
	}

	public static boolean SectorZadjust(int ceilhit, LONGp hiz, int florhit, LONGp loz) {
		// extern long PlaxCeilGlobZadjust, PlaxFloorGlobZadjust;
		int z_amt = 0;

		boolean SkipFAFcheck = false;

		if (florhit != -1) {
			switch (DTEST(florhit, HIT_MASK)) {
			case HIT_SECTOR: {
				short hitsector = NORM_SECTOR(florhit);

				// don't jack with connect sectors
				if (FAF_ConnectFloor(hitsector)) {
					// rippers were dying through the floor in $rock
					if (TEST(sector[hitsector].floorstat, CEILING_STAT_FAF_BLOCK_HITSCAN))
						break;

					if (TEST(sector[hitsector].extra, SECTFX_Z_ADJUST)) {
						// see if a z adjust ST1 is around
						z_amt = GetZadjustment(hitsector, FLOOR_Z_ADJUST);

						if (z_amt != 0) {
							// explicit z adjust overrides Connect Floor
							loz.value += z_amt;
							SkipFAFcheck = true;
						}
					}

					break;
				}

				if (!TEST(sector[hitsector].extra, SECTFX_Z_ADJUST))
					break;

				// see if a z adjust ST1 is around
				z_amt = GetZadjustment(hitsector, FLOOR_Z_ADJUST);

				if (z_amt != 0) {
					// explicit z adjust overrides plax default
					loz.value += z_amt;
				} else
				// default adjustment for plax
				if (TEST(sector[hitsector].floorstat, FLOOR_STAT_PLAX)) {
					loz.value += PlaxFloorGlobZadjust;
				}

				break;
			}
			}
		}

		if (ceilhit != -1) {
			switch (DTEST(ceilhit, HIT_MASK)) {
			case HIT_SECTOR: {
				short hitsector = NORM_SECTOR(ceilhit);

				// don't jack with connect sectors
				if (FAF_ConnectCeiling(hitsector)) {
					if (TEST(sector[hitsector].extra, SECTFX_Z_ADJUST)) {
						// see if a z adjust ST1 is around
						z_amt = GetZadjustment(hitsector, CEILING_Z_ADJUST);

						if (z_amt != 0) {
							// explicit z adjust overrides Connect Floor
							if (loz != null)
								loz.value += z_amt;
							SkipFAFcheck = true;
						}
					}

					break;
				}

				if (!TEST(sector[hitsector].extra, SECTFX_Z_ADJUST))
					break;

				// see if a z adjust ST1 is around
				z_amt = GetZadjustment(hitsector, CEILING_Z_ADJUST);

				if (z_amt != 0) {
					// explicit z adjust overrides plax default
					hiz.value -= z_amt;
				} else
				// default adjustment for plax
				if (TEST(sector[hitsector].ceilingstat, CEILING_STAT_PLAX)) {
					hiz.value -= PlaxCeilGlobZadjust;
				}

				break;
			}
			}
		}

		return (SkipFAFcheck);
	}

	private static void WaterAdjust(int florhit, LONGp loz) {
		switch (DTEST(florhit, HIT_MASK)) {
		case HIT_SECTOR: {
			Sect_User sectu = SectUser[NORM_SECTOR(florhit)];

			if (sectu != null && sectu.depth != 0)
				loz.value += Z(sectu.depth);
		}
			break;
		case HIT_SPRITE:
			break;
		}
	}

	public static void FAFgetzrange(int x, int y, int z, short sectnum, LONGp hiz, LONGp ceilhit, LONGp loz,
			LONGp florhit, int clipdist, int clipmask) {
		boolean SkipFAFcheck;

		// IMPORTANT!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		// This will return invalid FAF ceiling and floor heights inside of
		// analyzesprite
		// because the ceiling and floors get moved out of the way for drawing.

		// early out to regular routine
		if (!FAF_ConnectArea(sectnum)) {
			engine.getzrange(x, y, z, sectnum, hiz, ceilhit, loz, florhit, clipdist, clipmask);
			SectorZadjust(ceilhit.value, hiz, florhit.value, loz);
			WaterAdjust(florhit.value, loz);
			return;
		}

		engine.getzrange(x, y, z, sectnum, hiz, ceilhit, loz, florhit, clipdist, clipmask);
		SkipFAFcheck = SectorZadjust(ceilhit.value, hiz, florhit.value, loz);
		WaterAdjust(florhit.value, loz);

		if (SkipFAFcheck)
			return;

		if (FAF_ConnectCeiling(sectnum)) {
			short uppersect = sectnum;
			int newz = hiz.value - Z(2);

			switch (DTEST(ceilhit.value, HIT_MASK)) {
			case HIT_SPRITE:
				return;
			}

			uppersect = engine.updatesectorz(x, y, newz, uppersect);
			if (uppersect < 0)
				Console.Println("Did not find a sector at " + x + " ," + y + " ," + newz);

			engine.getzrange(x, y, newz, uppersect, hiz, ceilhit, null, null, clipdist, clipmask);
			SectorZadjust(ceilhit.value, hiz, -1, null);
		} else if (FAF_ConnectFloor(sectnum) && !TEST(sector[sectnum].floorstat, FLOOR_STAT_FAF_BLOCK_HITSCAN)) {
			short lowersect = sectnum;
			int newz = loz.value + Z(2);

			switch (DTEST(florhit.value, HIT_MASK)) {
			case HIT_SPRITE:
				return;
			}

			lowersect = engine.updatesectorz(x, y, newz, lowersect);
			if (lowersect < 0)
				Console.Println("Did not find a sector at " + x + " ," + y + " ," + newz);

			engine.getzrange(x, y, newz, lowersect, null, null, loz, florhit, clipdist, clipmask);
			SectorZadjust(-1, null, florhit.value, loz);
			WaterAdjust(florhit.value, loz);
		}
	}

	public static void FAFgetzrangepoint(int x, int y, int z, short sectnum, LONGp hiz, LONGp ceilhit, LONGp loz,
			LONGp florhit) {
		boolean SkipFAFcheck;

		// IMPORTANT!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		// This will return invalid FAF ceiling and floor heights inside of
		// analyzesprite
		// because the ceiling and floors get moved out of the way for drawing.

		// early out to regular routine
		if (!FAF_ConnectArea(sectnum)) {
			engine.getzrangepoint(x, y, z, sectnum, hiz, ceilhit, loz, florhit);
			SectorZadjust(ceilhit.value, hiz, florhit.value, loz);
			WaterAdjust(florhit.value, loz);
			return;
		}

		engine.getzrangepoint(x, y, z, sectnum, hiz, ceilhit, loz, florhit);
		SkipFAFcheck = SectorZadjust(ceilhit.value, hiz, florhit.value, loz);
		WaterAdjust(florhit.value, loz);

		if (SkipFAFcheck)
			return;

		if (FAF_ConnectCeiling(sectnum)) {
			short uppersect = sectnum;
			int newz = hiz.value - Z(2);

			switch (DTEST(ceilhit.value, HIT_MASK)) {
			case HIT_SPRITE:
				return;
			}

			uppersect = engine.updatesectorz(x, y, newz, uppersect);
			if (uppersect < 0)
				Console.Println("Did not find a sector at " + x + ", " + y + ", " + newz + ", sectnum " + sectnum);

			engine.getzrangepoint(x, y, newz, uppersect, hiz, ceilhit, null, null);
			SectorZadjust(ceilhit.value, hiz, -1, null);
		} else if (FAF_ConnectFloor(sectnum) && !TEST(sector[sectnum].floorstat, FLOOR_STAT_FAF_BLOCK_HITSCAN)) {
			short lowersect = sectnum;
			int newz = loz.value + Z(2);

			switch (DTEST(florhit.value, HIT_MASK)) {
			case HIT_SPRITE:
				return;
			}

			lowersect = engine.updatesectorz(x, y, newz, lowersect);
			if (lowersect < 0)
				Console.Println("Did not find a sector at " + x + ", " + y + ", " + newz + ", sectnum " + sectnum);

			engine.getzrangepoint(x, y, newz, lowersect, null, null, loz, florhit);
			SectorZadjust(-1, null, florhit.value, loz);
			WaterAdjust(florhit.value, loz);
		}
	}

	public static int OLDsetsprite(short spritenum, int newx, int newy, int newz) {
		short tempsectnum;

		sprite[spritenum].x = newx;
		sprite[spritenum].y = newy;
		sprite[spritenum].z = newz;

		tempsectnum = sprite[spritenum].sectnum;
		tempsectnum = engine.updatesector(newx, newy, tempsectnum);
		if (tempsectnum < 0)
			return (-1);
		if (tempsectnum != sprite[spritenum].sectnum) {
			engine.changespritesect(spritenum, tempsectnum);
		}

		return (0);
	}

	// doesn't work for blank pics
	public static boolean PicInView(short tile_num, boolean reset) {
		if (TEST(gotpic[tile_num >> 3], 1 << (tile_num & 7))) {
			if (reset)
				gotpic[tile_num >> 3] &= ~(1 << (tile_num & 7));

			return true;
		}

		return false;
	}

	public static void SetupMirrorTiles() {
		short i, nexti;
		SPRITE sp;

		for (i = headspritestat[STAT_FAF]; i != -1; i = nexti) {
			nexti = nextspritestat[i];
			sp = sprite[i];

			if (sector[sp.sectnum].ceilingpicnum == FAF_PLACE_MIRROR_PIC) {
				sector[sp.sectnum].ceilingpicnum = FAF_MIRROR_PIC;
				sector[sp.sectnum].ceilingstat |= (CEILING_STAT_PLAX);
			}

			if (sector[sp.sectnum].floorpicnum == FAF_PLACE_MIRROR_PIC) {
				sector[sp.sectnum].floorpicnum = FAF_MIRROR_PIC;
				sector[sp.sectnum].floorstat |= (FLOOR_STAT_PLAX);
			}

			if (sector[sp.sectnum].ceilingpicnum == FAF_PLACE_MIRROR_PIC + 1)
				sector[sp.sectnum].ceilingpicnum = FAF_MIRROR_PIC + 1;

			if (sector[sp.sectnum].floorpicnum == FAF_PLACE_MIRROR_PIC + 1)
				sector[sp.sectnum].floorpicnum = FAF_MIRROR_PIC + 1;
		}
	}

	private static short[] sectorlist = new short[16];

	public static void GetUpperLowerSector(short match, int x, int y, LONGp upper, LONGp lower) {
		short i;
		short SpriteNum, Next;
		SPRITE sp;

		// keep a list of the last stacked sectors the view was in and
		// check those fisrt
		short sln = 0;
		for (i = 0; i < GlobStackSect.length; i++) {
			// will not hurt if GlobStackSect is invalid - inside checks for this
			if (engine.inside(x, y, GlobStackSect[i]) == 1) {
				boolean found = false;

				for (SpriteNum = headspritesect[GlobStackSect[i]]; SpriteNum != -1; SpriteNum = Next) {
					Next = nextspritesect[SpriteNum];
					sp = sprite[SpriteNum];

					if (sp.statnum == STAT_FAF && (sp.hitag >= VIEW_LEVEL1 && sp.hitag <= VIEW_LEVEL6)
							&& sp.lotag == match) {
						found = true;
					}
				}

				if (!found)
					continue;

				sectorlist[sln] = GlobStackSect[i];
				sln++;
			}
		}

		// didn't find it yet so test ALL sectors
		if (sln < 2) {
			sln = 0;
			for (i = (short) (numsectors - 1); i >= 0; i--) {
				if (engine.inside(x, y, i) == 1) {
					boolean found = false;

					for (SpriteNum = headspritesect[i]; SpriteNum != -1; SpriteNum = Next) {
						Next = nextspritesect[SpriteNum];
						sp = sprite[SpriteNum];

						if (sp.statnum == STAT_FAF && (sp.hitag >= VIEW_LEVEL1 && sp.hitag <= VIEW_LEVEL6)
								&& sp.lotag == match) {
							found = true;
						}
					}

					if (!found)
						continue;

					sectorlist[sln] = i;
					if (sln < GlobStackSect.length)
						GlobStackSect[sln] = i;
					sln++;
				}
			}
		}

		// might not find ANYTHING if not tagged right
		if (sln == 0) {
			upper.value = -1;
			lower.value = -1;
			return;
		} else
		// inside will somtimes find that you are in two different sectors if the x,y
		// is exactly on a sector line.
		if (sln > 2) {
			// try again moving the x,y pos around until you only get two sectors
			GetUpperLowerSector(match, x - 1, y, upper, lower);
		}

		if (sln == 2) {
			if (sector[sectorlist[0]].floorz < sector[sectorlist[1]].floorz) {
				// swap
				// make sectorlist[0] the LOW sector
				short hold;

				hold = sectorlist[0];
				sectorlist[0] = sectorlist[1];
				sectorlist[1] = hold;
			}

			lower.value = sectorlist[0];
			upper.value = sectorlist[1];
		}
	}

	private static LONGp upper = new LONGp(), lower = new LONGp();

	public static boolean FindCeilingView(short match, LONGp x, LONGp y, int z, LONGp sectnum) {
		int xoff = 0;
		int yoff = 0;
		short i, nexti;
		SPRITE sp = null;
		int pix_diff;
		int newz;

		save.zcount = 0;

		// Search Stat List For closest ceiling view sprite
		// Get the match, xoff, yoff from this point
		for (i = headspritestat[STAT_FAF]; i != -1; i = nexti) {
			nexti = nextspritestat[i];
			sp = sprite[i];

			if (sp.hitag == VIEW_THRU_CEILING && sp.lotag == match) {
				xoff = x.value - sp.x;
				yoff = y.value - sp.y;
				break;
			}
		}

		for (i = headspritestat[STAT_FAF]; i != -1; i = nexti) {
			nexti = nextspritestat[i];
			sp = sprite[i];

			if (sp.lotag == match) {
				// determine x,y position
				if (sp.hitag == VIEW_THRU_FLOOR) {
					x.value = sp.x + xoff;
					y.value = sp.y + yoff;

					// get new sector
					GetUpperLowerSector(match, x.value, y.value, sectnum, lower);
					break;
				}
			}
		}

		if (sectnum.value < 0)
			return false;

		if (sp == null || sp.hitag != VIEW_THRU_FLOOR)
			return false;

		pix_diff = klabs(z - sector[sp.sectnum].floorz) >> 8;
		newz = sector[sp.sectnum].floorz + ((pix_diff / 128) + 1) * Z(128);

		for (i = headspritestat[STAT_FAF]; i != -1; i = nexti) {
			nexti = nextspritestat[i];
			sp = sprite[i];

			if (sp.lotag == match) {
				// move lower levels ceilings up for the correct view
				if (sp.hitag == VIEW_LEVEL2) {
					// save it off
					save.sectnum[save.zcount] = sp.sectnum;
					save.zval[save.zcount] = sector[sp.sectnum].floorz;
					save.pic[save.zcount] = sector[sp.sectnum].floorpicnum;
					save.slope[save.zcount] = sector[sp.sectnum].floorheinum;

					sector[sp.sectnum].floorz = newz;
					// don't change FAF_MIRROR_PIC - ConnectArea
					if (sector[sp.sectnum].floorpicnum != FAF_MIRROR_PIC)
						sector[sp.sectnum].floorpicnum = FAF_MIRROR_PIC + 1;
					sector[sp.sectnum].floorheinum = 0;

					save.zcount++;
				}
			}
		}

		return true;
	}

	public static boolean FindFloorView(short match, LONGp x, LONGp y, int z, LONGp sectnum) {
		int xoff = 0;
		int yoff = 0;
		short i, nexti;
		SPRITE sp = null;
		int pix_diff;
		int newz;

		save.zcount = 0;

		// Search Stat List For closest ceiling view sprite
		// Get the match, xoff, yoff from this point
		for (i = headspritestat[STAT_FAF]; i != -1; i = nexti) {
			nexti = nextspritestat[i];
			sp = sprite[i];

			if (sp.hitag == VIEW_THRU_FLOOR && sp.lotag == match) {
				xoff = x.value - sp.x;
				yoff = y.value - sp.y;
				break;
			}
		}

		for (i = headspritestat[STAT_FAF]; i != -1; i = nexti) {
			nexti = nextspritestat[i];
			sp = sprite[i];

			if (sp.lotag == match) {
				// determine x,y position
				if (sp.hitag == VIEW_THRU_CEILING) {
					x.value = sp.x + xoff;
					y.value = sp.y + yoff;

					// get new sector
					GetUpperLowerSector(match, x.value, y.value, upper, sectnum);
					break;
				}
			}
		}

		if (sectnum.value < 0)
			return false;

		if (sp == null || sp.hitag != VIEW_THRU_CEILING)
			return false;

		// move ceiling multiple of 128 so that the wall tile will line up
		pix_diff = klabs(z - sector[sp.sectnum].ceilingz) >> 8;
		newz = sector[sp.sectnum].ceilingz - ((pix_diff / 128) + 1) * Z(128);

		for (i = headspritestat[STAT_FAF]; i != -1; i = nexti) {
			nexti = nextspritestat[i];
			sp = sprite[i];

			if (sp.lotag == match) {
				// move upper levels floors down for the correct view
				if (sp.hitag == VIEW_LEVEL1) {
					// save it off
					save.sectnum[save.zcount] = sp.sectnum;
					save.zval[save.zcount] = sector[sp.sectnum].ceilingz;
					save.pic[save.zcount] = sector[sp.sectnum].ceilingpicnum;
					save.slope[save.zcount] = sector[sp.sectnum].ceilingheinum;

					sector[sp.sectnum].ceilingz = newz;

					// don't change FAF_MIRROR_PIC - ConnectArea
					if (sector[sp.sectnum].ceilingpicnum != FAF_MIRROR_PIC)
						sector[sp.sectnum].ceilingpicnum = FAF_MIRROR_PIC + 1;
					sector[sp.sectnum].ceilingheinum = 0;

					save.zcount++;
				}
			}
		}

		return true;
	}

	public static short ViewSectorInScene(short cursectnum, short type, short level) {
		int i, nexti;
		SPRITE sp;
		short match;

		for (i = headspritestat[STAT_FAF]; i != -1; i = nexti) {
			nexti = nextspritestat[i];
			sp = sprite[i];

			if (sp.hitag == level) {
				if (cursectnum == sp.sectnum) {
					// ignore case if sprite is pointing up
					if (sp.ang == 1536)
						continue;

					// only gets to here is sprite is pointing down

					// found a potential match
					match = sp.lotag;

					if (!PicInView(FAF_MIRROR_PIC, true))
						return (-1);

					return (match);
				}
			}
		}

		return (-1);
	}

	public static void DrawOverlapRoom(int tx, int ty, int tz, float tang, float thoriz, short tsectnum, int smoothratio) {
		short i;
		short match;

		save.zcount = 0;

		match = ViewSectorInScene(tsectnum, VIEW_THRU_CEILING, VIEW_LEVEL1);
		if (match != -1) {
			FindCeilingView(match, tmp_ptr[0].set(tx), tmp_ptr[1].set(ty), tz, tmp_ptr[2].set(tsectnum));
			tx = tmp_ptr[0].value;
			ty = tmp_ptr[1].value;
			tsectnum = (short) tmp_ptr[2].value;

			if (tsectnum < 0)
				return;

			engine.drawrooms(tx, ty, tz, tang, thoriz, tsectnum);

			// reset Z's
			for (i = 0; i < save.zcount; i++) {
				sector[save.sectnum[i]].floorz = save.zval[i];
				sector[save.sectnum[i]].floorpicnum = save.pic[i];
				sector[save.sectnum[i]].floorheinum = save.slope[i];
			}

			analyzesprites(tx, ty, tz, false, smoothratio);
			post_analyzesprites(smoothratio);
			engine.drawmasks();

		} else {
			match = ViewSectorInScene(tsectnum, VIEW_THRU_FLOOR, VIEW_LEVEL2);
			if (match != -1) {
				FindFloorView(match, tmp_ptr[0].set(tx), tmp_ptr[1].set(ty), tz, tmp_ptr[2].set(tsectnum));
				tx = tmp_ptr[0].value;
				ty = tmp_ptr[1].value;
				tsectnum = (short) tmp_ptr[2].value;

				if (tsectnum < 0)
					return;

				engine.drawrooms(tx, ty, tz, tang, thoriz, tsectnum);

				// reset Z's
				for (i = 0; i < save.zcount; i++) {
					sector[save.sectnum[i]].ceilingz = save.zval[i];
					sector[save.sectnum[i]].ceilingpicnum = save.pic[i];
					sector[save.sectnum[i]].ceilingheinum = save.slope[i];
				}

				analyzesprites(tx, ty, tz, false, smoothratio);
				post_analyzesprites(smoothratio);
				engine.drawmasks();

			}
		}
	}

}
