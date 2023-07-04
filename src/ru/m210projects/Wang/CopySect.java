package ru.m210projects.Wang;

import static ru.m210projects.Build.Engine.headspritesect;
import static ru.m210projects.Build.Engine.headspritestat;
import static ru.m210projects.Build.Engine.nextspritesect;
import static ru.m210projects.Build.Engine.nextspritestat;
import static ru.m210projects.Build.Engine.sector;
import static ru.m210projects.Build.Engine.sprite;
import static ru.m210projects.Build.Engine.wall;
import static ru.m210projects.Wang.Gameutils.CSTAT_SPRITE_INVISIBLE;
import static ru.m210projects.Wang.Gameutils.SECTFX_SECTOR_OBJECT;
import static ru.m210projects.Wang.Gameutils.SPRITE_TAG1;
import static ru.m210projects.Wang.Gameutils.SPRITE_TAG2;
import static ru.m210projects.Wang.Gameutils.SPRITE_TAG3;
import static ru.m210projects.Wang.Gameutils.SP_TAG2;
import static ru.m210projects.Wang.Gameutils.SP_TAG3;
import static ru.m210projects.Wang.Gameutils.pUser;
import static ru.m210projects.Wang.Main.engine;
import static ru.m210projects.Wang.Names.STAT_COPY_DEST;
import static ru.m210projects.Wang.Names.STAT_COPY_SOURCE;
import static ru.m210projects.Wang.Sector.SectUser;
import static ru.m210projects.Wang.Sector.SectorMidPoint;
import static ru.m210projects.Wang.Sprites.GetSectUser;
import static ru.m210projects.Wang.Sprites.KillSprite;
import static ru.m210projects.Wang.Stag.SECT_COPY_SOURCE;
import static ru.m210projects.Wang.Track.DetectSectorObject;
import static ru.m210projects.Wang.Track.GlobSpeedSO;
import static ru.m210projects.Wang.Track.RefreshPoints;
import static ru.m210projects.Wang.Type.MyTypes.TEST;
import static ru.m210projects.Wang.Weapon.AddSpriteToSectorObject;
import static ru.m210projects.Wang.Weapon.SpriteQueueDelete;

import ru.m210projects.Build.Types.SECTOR;
import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Wang.Type.Sect_User;
import ru.m210projects.Wang.Type.Vector3i;

public class CopySect {

	public static void CopySectorWalls(short dest_sectnum, short src_sectnum) {
		short dest_wall_num, src_wall_num, start_wall;

		dest_wall_num = sector[dest_sectnum].wallptr;
		src_wall_num = sector[src_sectnum].wallptr;

		start_wall = dest_wall_num;

		do {
			wall[dest_wall_num].picnum = wall[src_wall_num].picnum;

			wall[dest_wall_num].xrepeat = wall[src_wall_num].xrepeat;
			wall[dest_wall_num].yrepeat = wall[src_wall_num].yrepeat;
			wall[dest_wall_num].overpicnum = wall[src_wall_num].overpicnum;
			wall[dest_wall_num].pal = wall[src_wall_num].pal;
			wall[dest_wall_num].cstat = wall[src_wall_num].cstat;
			wall[dest_wall_num].shade = wall[src_wall_num].shade;
			wall[dest_wall_num].xpanning = wall[src_wall_num].xpanning;
			wall[dest_wall_num].ypanning = wall[src_wall_num].ypanning;
			wall[dest_wall_num].hitag = wall[src_wall_num].hitag;
			wall[dest_wall_num].lotag = wall[src_wall_num].lotag;
			wall[dest_wall_num].extra = wall[src_wall_num].extra;

			if (wall[dest_wall_num].nextwall >= 0 && wall[src_wall_num].nextwall >= 0) {
				wall[wall[dest_wall_num].nextwall].picnum = wall[wall[src_wall_num].nextwall].picnum;
				wall[wall[dest_wall_num].nextwall].xrepeat = wall[wall[src_wall_num].nextwall].xrepeat;
				wall[wall[dest_wall_num].nextwall].yrepeat = wall[wall[src_wall_num].nextwall].yrepeat;
				wall[wall[dest_wall_num].nextwall].overpicnum = wall[wall[src_wall_num].nextwall].overpicnum;
				wall[wall[dest_wall_num].nextwall].pal = wall[wall[src_wall_num].nextwall].pal;
				wall[wall[dest_wall_num].nextwall].cstat = wall[wall[src_wall_num].nextwall].cstat;
				wall[wall[dest_wall_num].nextwall].shade = wall[wall[src_wall_num].nextwall].shade;
				wall[wall[dest_wall_num].nextwall].xpanning = wall[wall[src_wall_num].nextwall].xpanning;
				wall[wall[dest_wall_num].nextwall].ypanning = wall[wall[src_wall_num].nextwall].ypanning;
				wall[wall[dest_wall_num].nextwall].hitag = wall[wall[src_wall_num].nextwall].hitag;
				wall[wall[dest_wall_num].nextwall].lotag = wall[wall[src_wall_num].nextwall].lotag;
				wall[wall[dest_wall_num].nextwall].extra = wall[wall[src_wall_num].nextwall].extra;
			}

			dest_wall_num = wall[dest_wall_num].point2;
			src_wall_num = wall[src_wall_num].point2;
		} while (dest_wall_num != start_wall);
	}

	public static void CopySectorMatch(int match) {
		short ed, nexted, ss, nextss;
		SPRITE dest_sp, src_sp;
		SECTOR dsectp, ssectp;

		for (ed = headspritestat[STAT_COPY_DEST]; ed != -1; ed = nexted) {
			nexted = nextspritestat[ed];
			dest_sp = sprite[ed];
			dsectp = sector[dest_sp.sectnum];

			if (match != sprite[ed].lotag)
				continue;

			for (ss = headspritestat[STAT_COPY_SOURCE]; ss != -1; ss = nextss) {
				nextss = nextspritestat[ss];
				src_sp = sprite[ss];

				if (SP_TAG2(src_sp) == SPRITE_TAG2(ed) && SP_TAG3(src_sp) == SPRITE_TAG3(ed)) {
					short src_move, nextsrc_move;
					ssectp = sector[src_sp.sectnum];

					// !!!!!AAAAAAAAAAAAAAAAAAAAAAHHHHHHHHHHHHHHHHHHHHHH
					// Don't kill anything you don't have to
					// this wall killing things on a Queue causing
					// invalid situations

					int nextkill;
					for (int kill = headspritesect[dest_sp.sectnum]; kill != -1; kill = nextkill) {
						nextkill = nextspritesect[kill];
						SPRITE k = sprite[kill];

						// kill anything not invisible
						if (!TEST(k.cstat, CSTAT_SPRITE_INVISIBLE)) {
							if (pUser[kill] != null) {
							} else {
								SpriteQueueDelete(kill); // new function to allow killing - hopefully
								KillSprite(kill);
							}
						}
					}

					CopySectorWalls(dest_sp.sectnum, src_sp.sectnum);

					for (src_move = headspritesect[src_sp.sectnum]; src_move != -1; src_move = nextsrc_move) {
						nextsrc_move = nextspritesect[src_move];
						// don't move ST1 Copy Tags
						if (SPRITE_TAG1(src_move) != SECT_COPY_SOURCE) {
							int sx, sy, dx, dy, src_xoff, src_yoff;

							// move sprites from source to dest - use center offset

							// get center of src and dest sect
							Vector3i s = SectorMidPoint(src_sp.sectnum);
							sx = s.x;
							sy = s.y;
							Vector3i d = SectorMidPoint(dest_sp.sectnum);
							dx = d.x;
							dy = d.y;

							// get offset
							src_xoff = sx - sprite[src_move].x;
							src_yoff = sy - sprite[src_move].y;

							// move sprite to dest sector
							sprite[src_move].x = dx - src_xoff;
							sprite[src_move].y = dy - src_yoff;

							// change sector
							engine.changespritesect(src_move, dest_sp.sectnum);

							// check to see if it moved on to a sector object
							if (TEST(sector[dest_sp.sectnum].extra, SECTFX_SECTOR_OBJECT)) {
								// find and add sprite to SO
								int sopi = DetectSectorObject(sector[sprite[src_move].sectnum]);
								AddSpriteToSectorObject(src_move, sopi);

								// update sprites postions so they aren't in the
								// wrong place for one frame
								GlobSpeedSO = 0;
								RefreshPoints(sopi, 0, 0, true, 1);
							}
						}
					}

					// copy sector user if there is one
					if (SectUser[src_sp.sectnum] != null || SectUser[dest_sp.sectnum] != null) {
						Sect_User ssectu = GetSectUser(src_sp.sectnum);
						Sect_User dsectu = GetSectUser(dest_sp.sectnum);

						dsectu.set(ssectu);
					}

					dsectp.hitag = ssectp.hitag;
					dsectp.lotag = ssectp.lotag;

					dsectp.floorz = ssectp.floorz;
					dsectp.ceilingz = ssectp.ceilingz;

					dsectp.floorshade = ssectp.floorshade;
					dsectp.ceilingshade = ssectp.ceilingshade;

					dsectp.floorpicnum = ssectp.floorpicnum;
					dsectp.ceilingpicnum = ssectp.ceilingpicnum;

					dsectp.floorheinum = ssectp.floorheinum;
					dsectp.ceilingheinum = ssectp.ceilingheinum;

					dsectp.floorpal = ssectp.floorpal;
					dsectp.ceilingpal = ssectp.ceilingpal;

					dsectp.floorxpanning = ssectp.floorxpanning;
					dsectp.ceilingxpanning = ssectp.ceilingxpanning;

					dsectp.floorypanning = ssectp.floorypanning;
					dsectp.ceilingypanning = ssectp.ceilingypanning;

					dsectp.floorstat = ssectp.floorstat;
					dsectp.ceilingstat = ssectp.ceilingstat;

					dsectp.extra = ssectp.extra;
					dsectp.visibility = ssectp.visibility;
				}
			}
		}

		// do this outside of processing loop for safety

		// kill all matching dest
		for (ed = headspritestat[STAT_COPY_DEST]; ed != -1; ed = nexted) {
			nexted = nextspritestat[ed];
			if (match == sprite[ed].lotag)
				KillSprite(ed);
		}

		// kill all matching sources
		for (ss = headspritestat[STAT_COPY_DEST]; ss != -1; ss = nextss) {
			nextss = nextspritestat[ss];
			if (match == sprite[ss].lotag)
				KillSprite(ss);
		}
	}
}
