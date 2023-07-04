//Copyright (C) 1996, 2003 - 3D Realms Entertainment
//
//This file is part of Duke Nukem 3D version 1.5 - Atomic Edition
//
//Duke Nukem 3D is free software; you can redistribute it and/or
//modify it under the terms of the GNU General Public License
//as published by the Free Software Foundation; either version 2
//of the License, or (at your option) any later version.
//
//This program is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
//
//See the GNU General Public License for more details.
//
//You should have received a copy of the GNU General Public License
//along with this program; if not, write to the Free Software
//Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
//
//Original Source: 1996 - Todd Replogle
//Prepared for public release: 03/21/2003 - Charlie Wiederhold, 3D Realms
//This file has been modified by Jonathon Fowler (jf@jonof.id.au)
//and Alexander Makarov-[M210] (m210-2007@mail.ru)

package ru.m210projects.Redneck;

import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Build.Gameutils.*;
import static ru.m210projects.Build.Net.Mmulti.*;
import static ru.m210projects.Build.Pragmas.*;
import static ru.m210projects.Redneck.LoadSave.*;
import static ru.m210projects.Redneck.Main.*;
import static ru.m210projects.Redneck.Animate.*;
import static ru.m210projects.Redneck.Premap.*;
import static ru.m210projects.Redneck.Types.ANIMATION.*;
import static ru.m210projects.Redneck.Player.*;
import static ru.m210projects.Redneck.Globals.*;
import static ru.m210projects.Redneck.Sector.*;
import static ru.m210projects.Redneck.Spawn.*;
import static ru.m210projects.Redneck.Gamedef.*;
import static ru.m210projects.Redneck.Gameutils.*;
import static ru.m210projects.Redneck.Names.*;
import static ru.m210projects.Redneck.Sounds.*;
import static ru.m210projects.Redneck.SoundDefs.*;
import static ru.m210projects.Redneck.View.*;
import static ru.m210projects.Redneck.Weapons.*;

import java.util.Arrays;

import com.badlogic.gdx.Input.Keys;

import ru.m210projects.Build.Engine.Point;
import ru.m210projects.Build.FileHandle.Compat.Path;
import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.Types.SECTOR;
import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Build.Types.WALL;
import ru.m210projects.Redneck.Types.PlayerStruct;

public class Actors {
	public static int actor_tog = 0;

	public static int UFO_SpawnCount;
	public static int UFO_SpawnTime;
	public static int UFO_SpawnHulk;

	public static boolean checkaddkills(SPRITE s) {
		switch (s.picnum) {
		case BOULDER:
		case BOULDER1:
		case 1147:
		case RAT:
		case TORNADO:
		case BILLYCOCK:
		case BILLYRAY:
		case BILLYRAYSTAYPUT:
		case 4249:
		case DOGRUN:
		case LTH:
		case 4429:
		case BUBBASTAND:
		case HULK:
		case HULKSTAYPUT:
		case MOSQUITO:
		case PIG:
		case 4989:
		case SBMOVE:
		case MINION:
		case MINIONSTAYPUT:
		case UFO1:
		case UFO2:
		case UFO3:
		case UFO4:
		case UFO5:
		case COOT:
		case COOTSTAYPUT:
		case 5501:
		case VIXEN:
			return true;

		case 5890: // RA
		case 5891:
		case 5995:
		case 6225:
		case 6401:
		case 6658:
		case 6659:
		case 7030:
		case 7035:
		case 7192:
		case 7199:
		case 7206:
		case 7280:
		case 8035:
		case 8036:
		case 8705:
			if (currentGame.getCON().type == RRRA)
				return true;
			break;
		}
		return false;
	}

	public static int LocateTheLocator(int n, int sn) {
		short i;

		i = headspritestat[7];
		while (i >= 0) {
			if ((sn == -1 || sn == sprite[i].sectnum) && n == sprite[i].lotag)
				return i;
			i = nextspritestat[i];
		}
		return -1;
	}

	public static boolean badguy(SPRITE s) {
		switch (s.picnum) {
		case BOULDER:
		case BOULDER1:
		case TORNADO:
		case BILLYCOCK:
		case BILLYSHOOT:
		case BILLYRAYSTAYPUT:
		case DOGRUN:
		case LTH:
		case 4429: // LTH
		case BUBBASTAND:
		case HULK:
		case HULKSTAYPUT:
		case HEN:
		case MOSQUITO:
		case PIG:
		case 4989:
		case SBMOVE:
		case MINION:
		case MINIONSTAYPUT:
		case UFO1:
		case UFO2:
		case UFO3:
		case UFO4:
		case UFO5:
		case COOT:
		case COOTSTAYPUT:
		case 5501:
		case VIXEN:
			return true;

		case 1147: // RA
		case RAT:
		case 4249:
		case 4770:
		case MINIONUFO:
		case BIKERRIDE:
		case BIKERRIDE + 1:
		case BIKERSTAND:
		case 6225:
		case BIKERRIDEDAISY:
		case DAISYMAE:
		case DAISYMAE + 1:
		case BANJOCOOTER:
		case GUITARBILLY:
		case MINIONAIRBOAT:
		case HULKAIRBOAT:
		case DAISYAIRBOAT:
		case JACKOLOPE:
		case MAMAJACKOLOPE:
			if (currentGame.getCON().type == RRRA)
				return true;
			break;
		}

		if (currentGame.getCON().actortype[s.picnum] != 0)
			return true;

		return false;
	}

	public static boolean badguypic(int pn) // Redneck
	{
		switch (pn) {
		case BOULDER:
		case BOULDER1:
		case 1147:
		case 1344:
		case TORNADO:
		case BILLYCOCK:
		case BILLYRAY:
		case BILLYRAYSTAYPUT:
		case 4249:
		case DOGRUN:
		case LTH:
		case 4429:
		case BUBBASTAND:
		case HULK:
		case HULKSTAYPUT:
		case MOSQUITO:
		case PIG:
		case 4989:
		case SBMOVE:
		case MINION:
		case MINIONSTAYPUT:
		case UFO1:
		case UFO2:
		case UFO3:
		case UFO4:
		case UFO5:
		case COOT:
		case COOTSTAYPUT:
		case 5501:
		case VIXEN:
			return true;

		// RA
		case 4770:
		case MINIONUFO:
		case 5890:
		case 5891:
		case 5995:
		case 6225:
		case 6401:
		case 6658:
		case 6659:
		case 7030:
		case 7035:
		case 7192:
		case 7199:
		case 7206:
		case 7280:
		case 8705:
			if (currentGame.getCON().type == RRRA)
				return true;
			break;
		}

		if (pn < MAXTILES && currentGame.getCON().actortype[pn] != 0)
			return true;

		return false;
	}

	public static boolean ifsquished(int i, int p) {
		boolean squishme = false;
		if (sprite[i].picnum == APLAYER && ud.clipping)
			return false;

		SECTOR sc = sector[sprite[i].sectnum];
		int floorceildist = sc.floorz - sc.ceilingz;

		if (sc.lotag != 23) {
			if (sprite[i].pal == 1)
				squishme = floorceildist < (32 << 8) && (sc.lotag & 32768) == 0;
			else
				squishme = floorceildist < (12 << 8);
		}

		if (squishme) {
			FTA(10, ps[p]);

			if (badguy(sprite[i]))
				sprite[i].xvel = 0;

			if (sprite[i].pal == 1) {
				hittype[i].picnum = SHOTSPARK1;
				hittype[i].extra = 1;
				return false;
			}

			return true;
		}
		return false;
	}

	public static final short tempshort[] = new short[MAXSECTORS];
	public static final byte statlist[] = { 0, 1, 6, 10, 12, 2, 5 };

	public static void hitradius(short i, int r, int hp1, int hp2, int hp3, int hp4) {
		SPRITE s, sj;
		WALL wal;
		int d, q, x1, y1;
		int sectcnt, sectend, endwall, x;
		short j, k, p, nextj, sect = 0, dasect, startwall, nextsect;

		s = sprite[i];

		if (s.picnum != CROSSBOW || s.picnum != CHIKENCROSSBOW || s.xrepeat >= 11) {

			tempshort[0] = s.sectnum;
			dasect = s.sectnum;
			sectcnt = 0;
			sectend = 1;

			do {
				dasect = tempshort[sectcnt++];
				if (((sector[dasect].ceilingz - s.z) >> 8) < r) {
					d = klabs(wall[sector[dasect].wallptr].x - s.x) + klabs(wall[sector[dasect].wallptr].y - s.y);
					if (d < r)
						checkhitceiling(dasect);
					else {
						d = klabs(wall[wall[wall[sector[dasect].wallptr].point2].point2].x - s.x)
								+ klabs(wall[wall[wall[sector[dasect].wallptr].point2].point2].y - s.y);
						if (d < r)
							checkhitceiling(dasect);
					}
				}

				startwall = sector[dasect].wallptr;
				endwall = startwall + sector[dasect].wallnum;
				for (x = startwall; x < endwall; x++) {
					wal = wall[x];
					if ((klabs(wal.x - s.x) + klabs(wal.y - s.y)) < r) {
						nextsect = wal.nextsector;
						if (nextsect >= 0) {
							for (dasect = (short) (sectend - 1); dasect >= 0; dasect--)
								if (tempshort[dasect] == nextsect)
									break;
							if (dasect < 0)
								tempshort[sectend++] = nextsect;
						}
						x1 = (((wal.x + wall[wal.point2].x) >> 1) + s.x) >> 1;
						y1 = (((wal.y + wall[wal.point2].y) >> 1) + s.y) >> 1;
						sect = engine.updatesector(x1, y1, sect);
						if (sect >= 0 && engine.cansee(x1, y1, s.z, sect, s.x, s.y, s.z, s.sectnum))
							checkhitwall(i, x, wal.x, wal.y, s.z, s.picnum);
					}
				}
			} while (sectcnt < sectend);

		}

		q = -(16 << 8) + (engine.krand() & ((32 << 8) - 1));

		for (x = 0; x < 7; x++) {
			j = headspritestat[statlist[x]];
			nextj = j >= 0 ? nextspritestat[j] : 0;

			for (; j >= 0; j = nextj) {
				nextj = nextspritestat[j];
				sj = sprite[j];

				if (x == 0 || x >= 5 || AFLAMABLE(sj.picnum)) {
					if ((sj.cstat & 257) != 0)
						if (dist(s, sj) < r) {
							if (badguy(sj)
									&& !engine.cansee(sj.x, sj.y, sj.z + q, sj.sectnum, s.x, s.y, s.z + q, s.sectnum))
								continue;
							checkhitsprite(j, i);
						}
				} else if (sj.extra >= 0 && sj != s && (badguy(sj) || sj.picnum == 1185 || sj.picnum == 3440
						|| sj.picnum == 1184 || (sj.cstat & 257) != 0 || sj.picnum == LNRDLYINGDEAD)) {
					if (s.picnum == MORTER && j == s.owner)
						continue;

					if (sj.picnum == APLAYER)
						sj.z -= PHEIGHT;
					d = dist(s, sj);
					if (sj.picnum == APLAYER)
						sj.z += PHEIGHT;

					if (d < r && engine.cansee(sj.x, sj.y, sj.z - (8 << 8), sj.sectnum, s.x, s.y, s.z - (12 << 8),
							s.sectnum)) {
						hittype[j].ang = engine.getangle(sj.x - s.x, sj.y - s.y);

						if (sprite[j].picnum == MINION && sprite[j].pal == 19)
							continue;

						if (s.picnum == CROSSBOW && sj.extra > 0)
							hittype[j].picnum = CROSSBOW;
						else if (s.picnum == CHIKENCROSSBOW && sj.extra > 0)
							hittype[j].picnum = CROSSBOW;
						else
							hittype[j].picnum = RADIUSEXPLOSION;

						if (d < r / 3) {
							if (hp4 == hp3)
								hp4++;
							hittype[j].extra = hp3 + (engine.krand() % (hp4 - hp3));
						} else if (d < 2 * r / 3) {
							if (hp3 == hp2)
								hp3++;
							hittype[j].extra = hp2 + (engine.krand() % (hp3 - hp2));
						} else if (d < r) {
							if (hp2 == hp1)
								hp2++;
							hittype[j].extra = hp1 + (engine.krand() % (hp2 - hp1));
						}

						if (sprite[j].picnum != HULK && sprite[j].picnum != SBMOVE
						// GDX RA 2.10.2018
								&& sprite[j].picnum != MAMAJACKOLOPE && sprite[j].picnum != GUITARBILLY
								&& sprite[j].picnum != BANJOCOOTER && sprite[j].picnum != 8663) {
							if (sj.xvel < 0)
								sj.xvel = 0;
							sj.xvel += (s.extra << 2);
						}
						if (sj.picnum == 2231 || sj.picnum == 1185 || sj.picnum == 1184 || sj.picnum == 3440)
							checkhitsprite(j, i);

						else if (s.extra == 0)
							hittype[j].extra = 0;

						if (sj.picnum != RADIUSEXPLOSION && s.owner >= 0 && sprite[s.owner].statnum < MAXSTATUS) {
							if (sj.picnum == APLAYER) {
								p = sj.yvel;
								if (ps[p].newowner >= 0) {
									ps[p].newowner = -1;
									ps[p].posx = ps[p].oposx;
									ps[p].posy = ps[p].oposy;
									ps[p].posz = ps[p].oposz;
									ps[p].ang = ps[p].oang;
									ps[p].cursectnum = engine.updatesector(ps[p].posx, ps[p].posy, ps[p].cursectnum);
									setpal(ps[p]);

									k = headspritestat[1];
									while (k >= 0) {
										if (sprite[k].picnum == CAMERA1)
											sprite[k].yvel = 0;
										k = nextspritestat[k];
									}
								}
							}
							hittype[j].owner = s.owner;
						}
					}
				}
			}
		}
	}

	public static int movesprite(short spritenum, int xchange, int ychange, int zchange, int cliptype) {
		int retval;

		boolean bg = badguy(sprite[spritenum]);

		game.pInt.setsprinterpolate(spritenum, sprite[spritenum]);

		if (sprite[spritenum].statnum == 5 || (bg && sprite[spritenum].xrepeat < 4)) {
			sprite[spritenum].x += (xchange * TICSPERFRAME) >> 2;
			sprite[spritenum].y += (ychange * TICSPERFRAME) >> 2;
			sprite[spritenum].z += (zchange * TICSPERFRAME) >> 2;
			if (bg)
				engine.setsprite(spritenum, sprite[spritenum].x, sprite[spritenum].y, sprite[spritenum].z);
			return 0;
		}

		short dasectnum = sprite[spritenum].sectnum;

		int daz = sprite[spritenum].z;
		int h = ((engine.getTile(sprite[spritenum].picnum).getHeight() * sprite[spritenum].yrepeat) << 1);
		daz -= h;

		if (bg) {
			int oldx = sprite[spritenum].x;
			int oldy = sprite[spritenum].y;

			if (sprite[spritenum].xrepeat > 60) {
				retval = engine.clipmove(sprite[spritenum].x, sprite[spritenum].y, daz, dasectnum,
						((xchange * TICSPERFRAME) << 11), ((ychange * TICSPERFRAME) << 11), 1024, (4 << 8), (4 << 8),
						cliptype);
				sprite[spritenum].x = clipmove_x;
				sprite[spritenum].y = clipmove_y;
				daz = clipmove_z;
				dasectnum = clipmove_sectnum;
			} else {
				retval = engine.clipmove(sprite[spritenum].x, sprite[spritenum].y, daz, dasectnum,
						((xchange * TICSPERFRAME) << 11), ((ychange * TICSPERFRAME) << 11), 192, (4 << 8), (4 << 8),
						cliptype);
				sprite[spritenum].x = clipmove_x;
				sprite[spritenum].y = clipmove_y;
				daz = clipmove_z;
				dasectnum = clipmove_sectnum;
			}

			if (dasectnum < 0 || (dasectnum >= 0
					&& (hittype[spritenum].actorstayput >= 0 && hittype[spritenum].actorstayput != dasectnum))) {
				if (dasectnum < 0)
					dasectnum = 0;
				sprite[spritenum].x = oldx;
				sprite[spritenum].y = oldy;
				if (sector[dasectnum].lotag == 1 || (hittype[spritenum].temp_data[0] & 3) == 1)
					sprite[spritenum].ang = (short) (engine.krand() & 2047);
				engine.setsprite(spritenum, oldx, oldy, sprite[spritenum].z);

				return (kHitSector | dasectnum);
			}
			if ((retval & kHitTypeMask) >= kHitWall && (hittype[spritenum].cgg == 0))
				sprite[spritenum].ang += 768;
		} else {
			if (sprite[spritenum].statnum == 4)
				retval = engine.clipmove(sprite[spritenum].x, sprite[spritenum].y, daz, dasectnum,
						((xchange * TICSPERFRAME) << 11), ((ychange * TICSPERFRAME) << 11), 8, (4 << 8), (4 << 8),
						cliptype);
			else
				retval = engine.clipmove(sprite[spritenum].x, sprite[spritenum].y, daz, dasectnum,
						((xchange * TICSPERFRAME) << 11), ((ychange * TICSPERFRAME) << 11), 128, (4 << 8), (4 << 8),
						cliptype);
			sprite[spritenum].x = clipmove_x;
			sprite[spritenum].y = clipmove_y;
			daz = clipmove_z;
			dasectnum = clipmove_sectnum;
		}

		if (dasectnum >= 0)
			if ((dasectnum != sprite[spritenum].sectnum))
				engine.changespritesect(spritenum, dasectnum);
		daz = sprite[spritenum].z + ((zchange * TICSPERFRAME) >> 3);
		if ((daz > hittype[spritenum].ceilingz) && (daz <= hittype[spritenum].floorz))
			sprite[spritenum].z = daz;
		else if (retval == 0)
			return (kHitSector | dasectnum);

		return (retval);
	}

	public static boolean ssp(short i, int cliptype) // The set sprite function
	{
		SPRITE s = sprite[i];

		int movetype = movesprite(i, (s.xvel * (sintable[(s.ang + 512) & 2047])) >> 14,
				(s.xvel * (sintable[s.ang & 2047])) >> 14, s.zvel, cliptype);

		return (movetype == 0);
	}

	public static void insertspriteq(int i) {
		if (currentGame.getCON().spriteqamount > 0) {
			if (spriteq[spriteqloc] >= 0)
				sprite[spriteq[spriteqloc]].xrepeat = 0;
			spriteq[spriteqloc] = (short) i;
			spriteqloc = (short) ((spriteqloc + 1) % currentGame.getCON().spriteqamount);
		} else
			sprite[i].xrepeat = sprite[i].yrepeat = 0;
	}

	public static void ms(int i) {
		// T1,T2 and T3 are used for all the sector moving stuff!!!
		SPRITE s = sprite[i];

		s.x += (s.xvel * (sintable[(s.ang + 512) & 2047])) >> 14;
		s.y += (s.xvel * (sintable[s.ang & 2047])) >> 14;

		int j = hittype[i].temp_data[1];
		int k = hittype[i].temp_data[2];

		short startwall = sector[s.sectnum].wallptr;
		int endwall = startwall + sector[s.sectnum].wallnum;
		for (short x = startwall; x < endwall; x++) {
			Point out = engine.rotatepoint(0, 0, msx[j], msy[j], (short) (k & 2047));

			engine.dragpoint(x, s.x + out.getX(), s.y + out.getY());
			j++;
		}
	}

	public static void movefta() {
		int p, px, py, sx, sy;
		short psect, ssect, nexti;
		SPRITE s;

		short i = headspritestat[2];
		while (i >= 0) {
			nexti = nextspritestat[i];

			s = sprite[i];
			p = findplayer(s);

			ssect = psect = s.sectnum;

			if (sprite[ps[p].i].extra > 0) {
				if (player_dist < 30000) {
					hittype[i].timetosleep++;
					boolean cansee = false;
					if (hittype[i].timetosleep >= (player_dist >> 8)) {
						if (badguy(s)) {
							px = ps[p].oposx + 64 - (engine.krand() & 127);
							py = ps[p].oposy + 64 - (engine.krand() & 127);
							psect = engine.updatesector(px, py, psect);
							if (psect == -1) {
								i = nexti;
								continue;
							}
							sx = s.x + 64 - (engine.krand() & 127);
							sy = s.y + 64 - (engine.krand() & 127);
							ssect = engine.updatesector(px, py, ssect);
							if (ssect == -1) {
								i = nexti;
								continue;
							}

							if (currentGame.getCON().type != RRRA) {
								if ((s.pal == 33 || s.picnum == VIXEN)) {
									int cz1 = ps[p].oposz - (engine.krand() % (32 << 8));
									int cz2 = s.z - (engine.krand() % (52 << 8));
									cansee = engine.cansee(sx, sy, cz2, s.sectnum, px, py, cz1, ps[p].cursectnum);
								} else {
									if (((px - sx) * sintable[(s.ang + 512) & kAngleMask])
											+ ((py - sy) * sintable[(s.ang) & kAngleMask]) >= 0) {
										int cz1 = ps[p].oposz - (engine.krand() % (32 << 8));
										int cz2 = s.z - (engine.krand() % (52 << 8));
										cansee = engine.cansee(sx, sy, cz2, s.sectnum, px, py, cz1, ps[p].cursectnum);
									}
								}
							}

							if (currentGame.getCON().type == RRRA) {
								if (s.pal == 33 || s.picnum == COOT || s.picnum == COOTSTAYPUT || s.picnum == VIXEN
										|| s.picnum == BIKERSTAND || s.picnum == BIKERRIDE || s.picnum == 5891
										|| s.picnum == 6658 || s.picnum == BIKERRIDEDAISY || s.picnum == 6659
										|| s.picnum == MINIONAIRBOAT || s.picnum == HULKAIRBOAT
										|| s.picnum == DAISYAIRBOAT || s.picnum == JACKOLOPE || s.picnum == BANJOCOOTER
										|| s.picnum == GUITARBILLY || s.picnum == 6225 || s.picnum == MAMAJACKOLOPE
										|| (s.picnum == MINION && s.pal == 8)) {
									int cz1 = ps[p].oposz - (engine.krand() % (32 << 8));
									int cz2 = s.z - (engine.krand() % (52 << 8));
									cansee = engine.cansee(sx, sy, cz2, s.sectnum, px, py, cz1, ps[p].cursectnum);
								} else {
									if (((px - sx) * sintable[(s.ang + 512) & kAngleMask])
											+ ((py - sy) * sintable[(s.ang) & kAngleMask]) >= 0) {
										int cz1 = ps[p].oposz - (engine.krand() % (32 << 8));
										int cz2 = s.z - (engine.krand() % (52 << 8));
										cansee = engine.cansee(sx, sy, cz2, s.sectnum, px, py, cz1, ps[p].cursectnum);
									}
								}
							}
						} else {
							int cz1 = ps[p].oposz - ((engine.krand() & 31) << 8);
							int cz2 = s.z - ((engine.krand() & 31) << 8);
							cansee = engine.cansee(s.x, s.y, cz2, s.sectnum, ps[p].oposx, ps[p].oposy, cz1,
									ps[p].cursectnum);
						}

						if (cansee)
							switch (s.picnum) {
							case 1251:
							case 1268:
							case 1187:
							case 1304:
							case 1305:
							case 1306:
							case 1309:
							case 1315:
							case 1317:
							case 1388:
								if ((sector[s.sectnum].ceilingstat & 1) != 0)
									s.shade = sector[s.sectnum].ceilingshade;
								else
									s.shade = sector[s.sectnum].floorshade;

								hittype[i].timetosleep = 0;
								engine.changespritestat(i, (short) 6);
								break;
							default:
								hittype[i].timetosleep = 0;
								check_fta_sounds(i);
								engine.changespritestat(i, (short) 1);
								break;
							}
						else
							hittype[i].timetosleep = 0;
					}
				}
				if (badguy(s)) {
					if ((sector[s.sectnum].ceilingstat & 1) != 0)
						s.shade = sector[s.sectnum].ceilingshade;
					else
						s.shade = sector[s.sectnum].floorshade;

					switch (s.picnum) {
					case HEN:
					case COW:
					case PIG:
					case DOGRUN:
					case JACKOLOPE:
						if (sub_64F28(i, p)) {
							hittype[i].timetosleep = 0;
							check_fta_sounds(i);
							engine.changespritestat(i, (short) 1);
						}
						break;
					}
				}
			}
			i = nexti;
		}
	}

	public static void sub_64EF0(int snum) {
		PlayerStruct p = ps[snum];
		p.field_28E = 1;
		p.field_X = p.posx;
		p.field_Y = p.posy;
	}

	public static boolean sub_64F28(int nSprite, int nPlayer) {
		PlayerStruct p = ps[nPlayer];
		SPRITE pSprite = sprite[nSprite];

		if (p.field_28E != 0) {
			int pal = pSprite.pal;
			if (pal != 30 && pal != 32 && pal != 33) {
				if (p.field_X - p.field_290 < pSprite.x && p.field_290 + p.field_X > pSprite.x) {
					if (p.field_Y - p.field_290 < pSprite.y && p.field_Y + p.field_290 > pSprite.y)
						return true;
				}
			}
		}
		return false;
	}

	public static int ifhitsectors(short sectnum) {
		int i = headspritestat[5];
		while (i >= 0) {
			if (sprite[i].picnum == EXPLOSION2 || sprite[i].picnum == EXPLOSION3 && sectnum == sprite[i].sectnum)
				return i;
			i = nextspritestat[i];
		}
		return -1;
	}

	public static int ifhitbyweapon(int sn) {
		int j, p;
		SPRITE npc;

		if (hittype[sn].extra >= 0) {
			if (sprite[sn].extra >= 0) {
				npc = sprite[sn];

				if (npc.picnum == APLAYER) {
					if (ud.god)
						return -1;

					p = npc.yvel;
					j = hittype[sn].owner;

					if (j >= 0 && sprite[j].picnum == APLAYER && ud.coop == 1 && ud.ffire == 0)
						return -1;

					npc.extra -= hittype[sn].extra;

					if (j >= 0) {
						if (npc.extra <= 0 && hittype[sn].picnum != 1409) {
							npc.extra = 0;

							ps[p].wackedbyactor = (short) j;

							if (sprite[hittype[sn].owner].picnum == APLAYER && p != sprite[hittype[sn].owner].yvel)
								ps[p].frag_ps = sprite[j].yvel;

							hittype[sn].owner = ps[p].i;
						}
					}

					switch (hittype[sn].picnum) {
					case DYNAMITE:
					case POWDERKEGSPRITE:
					case 1228:
					case 1273:
					case 1315:
					case SEENINE:
					case RADIUSEXPLOSION:
					case CROSSBOW:
					case CHIKENCROSSBOW:
						ps[p].posxv += hittype[sn].extra * (sintable[(hittype[sn].ang + 512) & 2047]) << 2;
						ps[p].posyv += hittype[sn].extra * (sintable[hittype[sn].ang & 2047]) << 2;
						break;
					default:
						ps[p].posxv += hittype[sn].extra * (sintable[(hittype[sn].ang + 512) & 2047]) << 1;
						ps[p].posyv += hittype[sn].extra * (sintable[hittype[sn].ang & 2047]) << 1;
						break;
					}
				} else {
					if (hittype[sn].extra == 0 && npc.xrepeat < 24)
						return -1;

					npc.extra -= hittype[sn].extra;
					if (npc.picnum != 4989 && npc.owner >= 0 && sprite[npc.owner].statnum < MAXSTATUS)
						npc.owner = (short) hittype[sn].owner;
				}

				hittype[sn].extra = -1;
				return hittype[sn].picnum;
			}
		}

		hittype[sn].extra = -1;
		return -1;
	}

	public static void movecyclers() {
		short j, t;
		byte cshade;

		for (int q = numcyclers - 1; q >= 0; q--) {
			short s = cyclers[q][0];
			t = cyclers[q][3];
			j = (short) (t + (sintable[cyclers[q][1] & 2047] >> 10));
			cshade = (byte) cyclers[q][2];

			if (j < cshade)
				j = cshade;
			else if (j > t)
				j = t;

			cyclers[q][1] += sector[s].extra;
			if (cyclers[q][5] != 0) {
				int wallstart = sector[s].wallptr;
				int wallend = wallstart + sector[s].wallnum;
				for (int i = wallstart; i < wallend; i++) {
					WALL wal = wall[i];
					if (wal.hitag != 1) {
						wal.shade = (byte) j;
						if ((wal.cstat & 2) != 0 && wal.nextwall >= 0)
							wall[wal.nextwall].shade = (byte) j;
					}
				}
				sector[s].floorshade = sector[s].ceilingshade = (byte) j;
			}
		}
	}

	public static void movedummyplayers() {
		short p, nexti;

		short i = headspritestat[13];
		while (i >= 0) {
			nexti = nextspritestat[i];

			p = sprite[sprite[i].owner].yvel;

			if (sector[ps[p].cursectnum].lotag != 1 || sprite[ps[p].i].extra <= 0) {
				ps[p].dummyplayersprite = -1;
				engine.deletesprite(i);
				i = nexti;
				continue;
			} else {
				if (ps[p].on_ground && ps[p].on_warping_sector == 1 && sector[ps[p].cursectnum].lotag == 1) {
					sprite[i].cstat = 257;
					sprite[i].z = sector[sprite[i].sectnum].ceilingz + (27 << 8);
					sprite[i].ang = (short) ps[p].ang;
					if (hittype[i].temp_data[0] == 8)
						hittype[i].temp_data[0] = 0;
					else
						hittype[i].temp_data[0]++;
				} else {
					if (sector[sprite[i].sectnum].lotag != 2)
						sprite[i].z = sector[sprite[i].sectnum].floorz;
					sprite[i].cstat = (short) 32768;
				}
			}

			sprite[i].x += (ps[p].posx - ps[p].oposx);
			sprite[i].y += (ps[p].posy - ps[p].oposy);
			engine.setsprite(i, sprite[i].x, sprite[i].y, sprite[i].z);

			i = nexti;
		}
	}

	public static int otherp;

	public static void moveplayers() // Players
	{
		short i, nexti;
		int otherx;
		SPRITE s;
		PlayerStruct p;

		i = headspritestat[10];
		while (i >= 0) {
			nexti = nextspritestat[i];

			s = sprite[i];
			p = ps[s.yvel];
			if (s.owner >= 0) {
				if (p.newowner >= 0) // Looking thru the camera
				{
					game.pInt.setsprinterpolate(i, s);
					s.x = p.oposx;
					s.y = p.oposy;
					s.z = p.oposz + PHEIGHT;
					s.ang = (short) p.oang;
					engine.setsprite(i, s.x, s.y, s.z);
				} else {
					if (ud.multimode > 1) {
						otherp = findotherplayer(s.yvel);
						otherx = player_dist;
					} else {
						otherp = s.yvel;
						otherx = 0;
					}

					execute(currentGame.getCON(), i, s.yvel, otherx);

					if (ud.multimode > 1)
						if (sprite[ps[otherp].i].extra > 0) {
							if (s.yrepeat > 32 && sprite[ps[otherp].i].yrepeat < 32) {
								if (otherx < 1400 && p.knee_incs == 0) {
									p.knee_incs = 1;
									p.weapon_pos = -1;
									p.actorsqu = ps[otherp].i;
								}
							}
						}
					if (ud.god) {
						s.extra = (short) currentGame.getCON().max_player_health;
						s.cstat = 257;
					}

					if (s.extra > 0) {
						hittype[i].owner = i;

						if (!ud.god)
							if (ceilingspace(s.sectnum) || floorspace(s.sectnum))
								quickkill(p);
					} else {

						p.posx = s.x;
						p.posy = s.y;
						p.posz = s.z - (20 << 8);

						p.newowner = -1;

						if (p.wackedbyactor >= 0 && sprite[p.wackedbyactor].statnum < MAXSTATUS) {
							p.ang += getincangle(p.ang, engine.getangle(sprite[p.wackedbyactor].x - p.posx,
									sprite[p.wackedbyactor].y - p.posy)) / 2.0f;
							p.ang = BClampAngle(p.ang);
						}

						if (!isPsychoSkill()) {
							if (ud.multimode < 2 && lastload != null && !lastload.isEmpty() && ud.recstat != 2
									&& BuildGdx.compat.checkFile(lastload, Path.User) != null) {
								if (game.pInput.ctrlKeyPressed(Keys.ENTER)) {
									game.changeScreen(gLoadingScreen.setTitle(lastload));
									gLoadingScreen.init(new Runnable() {
										@Override
										public void run() {
											if (!loadgame(lastload)) {
												game.GameMessage("Can't load game!");
												game.show();
											}
										}
									});
									return;
								}
							}
						}
					}
					s.ang = (short) p.ang;
				}
			} else {
				if (p.holoduke_on == -1) {
					engine.deletesprite(i);
					i = nexti;
					continue;
				}

				game.pInt.setsprinterpolate(i, s);

				s.cstat = 0;

				if (s.xrepeat < 42) {
					s.xrepeat += 4;
					s.cstat |= 2;
				} else
					s.xrepeat = 42;
				if (s.yrepeat < 36)
					s.yrepeat += 4;
				else {
					s.yrepeat = 36;
					if (sector[s.sectnum].lotag != 2)
						makeitfall(currentGame.getCON(), i);
					if (s.zvel == 0 && sector[s.sectnum].lotag == 1)
						s.z += (32 << 8);
				}

				if (s.extra < 8) {
					s.xvel = 128;
					s.ang = (short) p.ang;
					s.extra++;
					ssp(i, CLIPMASK0);
				} else {
					s.ang = (short) (2047 - p.ang);
					engine.setsprite(i, s.x, s.y, s.z);
				}
			}

			if (isValidSector(s.sectnum)) {
				if ((sector[s.sectnum].ceilingstat & 1) != 0)
					s.shade += (sector[s.sectnum].ceilingshade - s.shade) >> 1;
				else
					s.shade += (sector[s.sectnum].floorshade - s.shade) >> 1;
			}

			i = nexti;
		}
	}

	public static void movefx() {
		int j, p;
		int x, ht;
		SPRITE s;

		short i = headspritestat[11];
		while (i >= 0) {
			s = sprite[i];

			short nexti = nextspritestat[i];

			switch (s.picnum) {
			case RESPAWN:
				if (sprite[i].extra == 66) {
					j = spawn(i, sprite[i].hitag);
					if (currentGame.getCON().type == RRRA) {
						sprite[j].pal = sprite[i].pal;
						if (sprite[j].picnum == MAMAJACKOLOPE) {
							if (sprite[j].pal == 30) {
								sprite[j].xrepeat = 26;
								sprite[j].yrepeat = 26;
								sprite[j].clipdist = 75;
							} else if (sprite[j].pal == 31) {
								sprite[j].xrepeat = 36;
								sprite[j].yrepeat = 36;
								sprite[j].clipdist = 100;
							} else {
								sprite[j].xrepeat = 50;
								sprite[j].yrepeat = 50;
								sprite[j].clipdist = 100;
							}
						}
						if (sprite[j].pal == 8)
							sprite[j].cstat |= 2;
						if (sprite[j].pal == 6) {
							sprite[i].extra = 53;
							sprite[j].pal = 0;
						} else
							engine.deletesprite(i);
					} else
						engine.deletesprite(i);
				} else if (sprite[i].extra > (66 - 13))
					sprite[i].extra++;
				break;

			case MUSICANDSFX:

				ht = s.hitag;
				if (hittype[i].temp_data[1] != (cfg.noSound ? 0 : 1)) {
					hittype[i].temp_data[1] = (cfg.noSound ? 0 : 1);
					hittype[i].temp_data[0] = 0;
				}

				if (s.lotag >= 1000 && s.lotag < 2000) {
					x = ldist(sprite[ps[screenpeek].i], s);
					if (x < ht && hittype[i].temp_data[0] == 0) {
						BuildGdx.audio.getSound().setReverb(true, (s.lotag - 1000) / 255f);
						hittype[i].temp_data[0] = 1;
					}
					if (x >= ht && hittype[i].temp_data[0] == 1) {
						BuildGdx.audio.getSound().setReverb(false, 0);
						hittype[i].temp_data[0] = 0;
					}
				} else if (s.lotag < 999 && sector[s.sectnum].lotag < 9 && cfg.AmbienceToggle
						&& sector[sprite[i].sectnum].floorz != sector[sprite[i].sectnum].ceilingz) {
					if ((currentGame.getCON().soundm[s.lotag] & 2) != 0) {
						x = dist(sprite[ps[screenpeek].i], s);
						if (x < ht && hittype[i].temp_data[0] == 0
								&& BuildGdx.audio.getSound().isAvailable(currentGame.getCON().soundpr[s.lotag] - 1)) {
							if (numenvsnds == cfg.maxvoices) {
								j = headspritestat[11];
								while (j >= 0) {
									if (sprite[i].picnum == MUSICANDSFX && j != i && sprite[j].lotag < 999
											&& hittype[j].temp_data[0] == 1
											&& dist(sprite[j], sprite[ps[screenpeek].i]) > x) {
										stopenvsound(sprite[j].lotag, j);
										break;
									}
									j = nextspritestat[j];
								}
								if (j == -1) {
									i = nexti;
									continue;
								}
							}
							spritesound(s.lotag, i);
							hittype[i].temp_data[0] = 1;
						}
						if (x >= ht && hittype[i].temp_data[0] == 1) {
							hittype[i].temp_data[0] = 0;
							stopenvsound(s.lotag, i);
						}
					}
					if ((currentGame.getCON().soundm[s.lotag] & 16) != 0) {
						if (hittype[i].temp_data[4] > 0)
							hittype[i].temp_data[4]--;
						else
							for (p = connecthead; p >= 0; p = connectpoint2[p])
								if (p == myconnectindex && ps[p].cursectnum == s.sectnum) {
									j = s.lotag + ((global_random & 0xFFFF) % (s.hitag + 1));
									sound(j);
									hittype[i].temp_data[4] = 26 * 40 + (global_random % (26 * 40));
								}
					}
				}
				break;
			}
			i = nexti;
		}
	}

	public static void movefallers() {
		short i, nexti, sect;
		SPRITE s;
		int x, j;

		i = headspritestat[12];
		while (i >= 0) {
			nexti = nextspritestat[i];
			s = sprite[i];

			sect = s.sectnum;

			if (hittype[i].temp_data[0] == 0) {
				s.z -= (16 << 8);
				hittype[i].temp_data[1] = s.ang;
				x = s.extra;

				j = ifhitbyweapon(i);
				if (j >= 0) {
					if (j == CROSSBOW || j == CHIKENCROSSBOW || j == RADIUSEXPLOSION || j == SEENINE || j == 1273) {
						if (s.extra <= 0) {
							hittype[i].temp_data[0] = 1;
							j = headspritestat[12];
							while (j >= 0) {
								if (sprite[j].hitag == sprite[i].hitag) {
									hittype[j].temp_data[0] = 1;
									sprite[j].cstat &= (65535 - 64);
									if (sprite[j].picnum == CEILINGSTEAM || sprite[j].picnum == STEAM)
										sprite[j].cstat |= 32768;
								}
								j = nextspritestat[j];
							}
						}
					} else {
						hittype[i].extra = 0;
						s.extra = (short) x;
					}
				}
				s.ang = (short) hittype[i].temp_data[1];
				s.z += (16 << 8);
			} else if (hittype[i].temp_data[0] == 1) {
				if (s.lotag > 0) {
					s.lotag -= 3;
					// if(s.lotag <= 0)
					{
						s.xvel = (short) (64 + engine.krand() & 127);
						s.zvel = (short) -(1024 + (engine.krand() & 1023)); // XXX
																			// IDA
																			// PRO
					}
				} else {
					if (s.xvel > 0) {
						s.xvel -= 2;
						ssp(i, CLIPMASK0);
					}

					if (floorspace(s.sectnum))
						x = 0;
					else {
						if (ceilingspace(s.sectnum))
							x = currentGame.getCON().gc / 6;
						else
							x = currentGame.getCON().gc;
					}

					if (s.z < (sector[sect].floorz - FOURSLEIGHT)) {
						s.zvel += x;
						if (s.zvel > 6144)
							s.zvel = 6144;
						s.z += s.zvel;
					}
					if ((sector[sect].floorz - s.z) < (16 << 8)) {
						j = 1 + (engine.krand() & 7);
						for (x = 0; x < j; x++)
							RANDOMSCRAP(s, i);
						engine.deletesprite(i);
					}
				}
			}

			i = nexti;
		}
	}

	private static void Detonate(SPRITE s, short i, int[] t) {
		earthquaketime = 16;

		int j = headspritestat[3];
		while (j >= 0) {
			if (s.hitag == sprite[j].hitag) {
				if (sprite[j].lotag == 13) {
					if (hittype[j].temp_data[2] == 0)
						hittype[j].temp_data[2] = 1;
				} else if (sprite[j].lotag == 8)
					hittype[j].temp_data[4] = 1;
				else if (sprite[j].lotag == 18) {
					if (hittype[j].temp_data[0] == 0)
						hittype[j].temp_data[0] = 1;
				} else if (sprite[j].lotag == 21)
					hittype[j].temp_data[0] = 1;
			}
			j = nextspritestat[j];
		}

		s.z -= (32 << 8);

		if ((t[3] == 1 && s.xrepeat != 0) || s.lotag == -99) {
			int x = s.extra;
			spawn(i, EXPLOSION2);
			hitradius(i, currentGame.getCON().seenineblastradius, x >> 2, x - (x >> 1), x - (x >> 2), x);
			spritesound(PIPEBOMB_EXPLODE, i);
		}

		if (s.xrepeat != 0)
			for (int x = 0; x < 8; x++)
				RANDOMSCRAP(s, i);

		engine.deletesprite(i);
	}

	public static void movestandables() {
		int p = 0, sect;
		int l = 0, x, j;

		short i = headspritestat[6], nextj;
		short nexti = i >= 0 ? nextspritestat[i] : 0;

		BOLT: for (; i >= 0; i = nexti) {
			nexti = nextspritestat[i];

			int[] t = hittype[i].temp_data;
			SPRITE s = sprite[i];
			sect = s.sectnum;

			if (sect < 0) {
				engine.deletesprite(i);
				continue BOLT;
			}

			game.pInt.setsprinterpolate(i, s);

			if (IFWITHIN(s, CRANE, CRANE + 3)) {
				// t[0] = state
				// t[1] = checking sector number

				if (s.xvel != 0)
					getglobalz(i);

				if (t[0] == 0) // Waiting to check the sector
				{
					j = headspritesect[t[1]];
					while (j >= 0) {
						nextj = nextspritesect[j];
						switch (sprite[j].statnum) {
						case 1:
						case 2:
						case 6:
						case 10:
							s.ang = engine.getangle(msx[t[4] + 1] - s.x, msy[t[4] + 1] - s.y);
							engine.setsprite((short) j, msx[t[4] + 1], msy[t[4] + 1], sprite[j].z);
							t[0]++;
							continue BOLT;
						}
						j = nextj;
					}
				}

				else if (t[0] == 1) {
					if (s.xvel < 184) {
						s.picnum = CRANE + 1;
						s.xvel += 8;
					}
					ssp(i, CLIPMASK0);
					if (sect == t[1])
						t[0]++;
				} else if (t[0] == 2 || t[0] == 7) {
					s.z += (1024 + 512);

					if (t[0] == 2) {
						if ((sector[sect].floorz - s.z) < (64 << 8))
							if (s.picnum > CRANE)
								s.picnum--;

						if ((sector[sect].floorz - s.z) < (4096 + 1024))
							t[0]++;
					}
					if (t[0] == 7) {
						if ((sector[sect].floorz - s.z) < (64 << 8)) {
							if (s.picnum > CRANE)
								s.picnum--;
							else {
								if (s.owner == -2) {
									p = findplayer(s);
									spritesound(390, ps[p].i);
									x = player_dist;
									if (ps[p].on_crane == i)
										ps[p].on_crane = -1;
								}
								t[0]++;
								s.owner = -1;
							}
						}
					}
				} else if (t[0] == 3) {
					s.picnum++;
					if (s.picnum == (CRANE + 2)) {
						p = checkcursectnums(t[1]);
						if (p >= 0 && ps[p].on_ground) {
							s.owner = -2;
							ps[p].on_crane = i;
							spritesound(390, ps[p].i);
							ps[p].ang = (short) (s.ang + 1024);

							ps[p].posxv = 0;
							ps[p].posyv = 0;
							ps[p].poszv = 0;
							sprite[ps[p].i].xvel = 0;
							ps[p].look_ang = 0;
							ps[p].rotscrnang = 0;
						} else {
							j = headspritesect[t[1]];
							while (j >= 0) {
								switch (sprite[j].statnum) {
								case 1:
								case 6:
									s.owner = (short) j;
									break;
								}
								j = nextspritesect[j];
							}
						}

						t[0]++;// Grabbed the sprite
						t[2] = 0;
						continue;
					}
				} else if (t[0] == 4) // Delay before going up
				{
					t[2]++;
					if (t[2] > 10)
						t[0]++;
				} else if (t[0] == 5 || t[0] == 8) {
					if (t[0] == 8 && s.picnum < (CRANE + 2))
						if ((sector[sect].floorz - s.z) > 8192)
							s.picnum++;

					if (s.z < msx[t[4] + 2]) {
						t[0]++;
						s.xvel = 0;
					} else
						s.z -= (1024 + 512);
				} else if (t[0] == 6) {
					if (s.xvel < 192)
						s.xvel += 8;
					s.ang = engine.getangle(msx[t[4]] - s.x, msy[t[4]] - s.y);
					ssp(i, CLIPMASK0);
					if (((s.x - msx[t[4]]) * (s.x - msx[t[4]]) + (s.y - msy[t[4]]) * (s.y - msy[t[4]])) < (128 * 128))
						t[0]++;
				}

				else if (t[0] == 9)
					t[0] = 0;

				if (isValidSprite(msy[t[4] + 2])) {
					game.pInt.setsprinterpolate(msy[t[4] + 2], sprite[msy[t[4] + 2]]);
					engine.setsprite((short) msy[t[4] + 2], s.x, s.y, s.z - (34 << 8));
				}

				if (s.owner != -1) {
					p = findplayer(s);
					x = player_dist;
					j = ifhitbyweapon(i);
					if (j >= 0) {
						if (s.owner == -2)
							if (ps[p].on_crane == i)
								ps[p].on_crane = -1;
						s.owner = -1;
						s.picnum = CRANE;
						continue;
					}

					if (s.owner >= 0) {
						engine.setsprite(s.owner, s.x, s.y, s.z);
						s.zvel = 0;
					} else if (s.owner == -2) {

						ps[p].oposx = ps[p].posx;
						ps[p].oposy = ps[p].posy;
						ps[p].oposz = ps[p].posz;
						ps[p].oang = ps[p].ang;
						ps[p].opyoff = ps[p].pyoff;

						if (IsOriginalDemo()) {
							ps[p].posx = s.x - (sintable[((int) ps[p].ang + 512) & 2047] >> 6);
							ps[p].posy = s.y - (sintable[(int) ps[p].ang & 2047] >> 6);
						} else {
							ps[p].posx = (int) (s.x - (BCosAngle(BClampAngle(ps[p].ang)) / 64.0f));
							ps[p].posy = (int) (s.y - (BSinAngle(BClampAngle(ps[p].ang)) / 64.0f));
						}

						ps[p].posz = s.z + (2 << 8);
						engine.setsprite(ps[p].i, ps[p].posx, ps[p].posy, ps[p].posz);
						ps[p].cursectnum = sprite[ps[p].i].sectnum;
					}
				}

				continue;
			}

			if (IFWITHIN(sprite[i], WATERFOUNTAIN, WATERFOUNTAIN + 3)) {
				if (t[0] > 0) {
					if (t[0] < 20) {
						t[0]++;

						s.picnum++;

						if (s.picnum == (WATERFOUNTAIN + 3))
							s.picnum = WATERFOUNTAIN + 1;
					} else {
						p = findplayer(s);
						x = player_dist;
						if (x > 512) {
							t[0] = 0;
							s.picnum = WATERFOUNTAIN;
						} else
							t[0] = 1;
					}
				}
				continue;
			}

			if (AFLAMABLE(s.picnum)) {
				if (hittype[i].temp_data[0] == 1) {
					hittype[i].temp_data[1]++;
					if ((hittype[i].temp_data[1] & 3) > 0)
						continue;

					if (s.picnum == TIRE && hittype[i].temp_data[1] == 32) {
						s.cstat = 0;
						j = spawn(i, BLOODPOOL);
						sprite[j].shade = 127;
					} else {
						if (s.shade < 64)
							s.shade++;
						else {
							engine.deletesprite(i);
							continue;
						}
					}

					j = s.xrepeat - (engine.krand() & 7);
					if (j < 10) {
						engine.deletesprite(i);
						continue;
					}

					s.xrepeat = (short) j;

					j = s.yrepeat - (engine.krand() & 7);
					if (j < 4) {
						engine.deletesprite(i);
						continue;
					}
					s.yrepeat = (short) j;
				}
				continue;
			}

			if (s.picnum >= CRACK1 && s.picnum <= CRACK4) {
				if (s.hitag > 0) {
					t[0] = s.cstat;
					t[1] = s.ang;
					j = ifhitbyweapon(i);
					if (j == CROSSBOW || j == CHIKENCROSSBOW || j == RADIUSEXPLOSION || j == SEENINE
							|| j == OOZFILTER) {
						j = headspritestat[6];
						while (j >= 0) {
							if (s.hitag == sprite[j].hitag
									&& (sprite[j].picnum == OOZFILTER || sprite[j].picnum == SEENINE))
								if (sprite[j].shade != -32)
									sprite[j].shade = -32;
							j = nextspritestat[j];
						}

						Detonate(s, i, t);
						continue;
					} else {
						s.cstat = (short) t[0];
						s.ang = (short) t[1];
						s.extra = 0;
					}
				}
				continue;
			}

			if (s.picnum == OOZFILTER || s.picnum == SEENINE || s.picnum == SEENINEDEAD
					|| s.picnum == (SEENINEDEAD + 1)) {
				if (s.shade != -32 && s.shade != -33) {
					if (s.xrepeat != 0)
						j = (ifhitbyweapon(i) >= 0) ? 1 : 0;
					else
						j = 0;

					if (j != 0 || s.shade == -31) {
						if (j != 0)
							s.lotag = 0;

						t[3] = 1;

						j = headspritestat[6];
						while (j >= 0) {
							if (s.hitag == sprite[j].hitag
									&& (sprite[j].picnum == SEENINE || sprite[j].picnum == OOZFILTER))
								sprite[j].shade = -32;
							j = nextspritestat[j];
						}
					}
				} else {
					if (s.shade == -32) {
						if (s.lotag > 0) {
							s.lotag -= 3;
							if (s.lotag <= 0)
								s.lotag = -99;
						} else
							s.shade = -33;
					} else {
						if (s.xrepeat > 0) {
							hittype[i].temp_data[2]++;
							if (hittype[i].temp_data[2] == 3) {
								if (s.picnum == OOZFILTER) {
									hittype[i].temp_data[2] = 0;
									Detonate(s, i, t);
									continue;
								}
								if (s.picnum != (SEENINEDEAD + 1)) {
									hittype[i].temp_data[2] = 0;

									if (s.picnum == SEENINEDEAD)
										s.picnum++;
									else if (s.picnum == SEENINE)
										s.picnum = SEENINEDEAD;
								} else {
									Detonate(s, i, t);
									continue;
								}
							}
							continue;
						}

						Detonate(s, i, t);
					}
				}

				continue;
			}

			if (s.picnum == MASTERSWITCH) {
				if (s.yvel == 1) {
					s.hitag--;
					if (s.hitag <= 0) {
						operatesectors(sect, i);

						j = headspritesect[sect];
						while (j >= 0) {
							if (sprite[j].statnum == 3) {
								switch (sprite[j].lotag) {
								case 2:
								case 21:
								case 31:
								case 32:
								case 36:
									hittype[j].temp_data[0] = 1;
									break;
								case 3:
									hittype[j].temp_data[4] = 1;
									break;
								}
							} else if (sprite[j].statnum == 6) {
								switch (sprite[j].picnum) {
								case SEENINE:
								case OOZFILTER:
									sprite[j].shade = -31;
									break;
								}
							}
							j = nextspritesect[j];
						}
						engine.deletesprite(i);
					}
				}
				continue;
			}

			switch (s.picnum) {
			case TRASH: // ok

				if (s.xvel == 0)
					s.xvel = 1;
				if (ssp(i, CLIPMASK0)) {
					makeitfall(currentGame.getCON(), i);
					if ((engine.krand() & 1) != 0)
						s.zvel -= 256;
					if (klabs(s.xvel) < 48)
						s.xvel += (engine.krand() & 3);
				} else {
					engine.deletesprite(i);
					continue;
				}
				break;

			case BOLT1:
			case BOLT1 + 1:
			case BOLT1 + 2:
			case BOLT1 + 3:
				p = findplayer(s);
				x = player_dist;

				if (x > 20480)
					continue;

				if (t[3] == 0)
					t[3] = sector[sect].floorshade;

				CLEAR_THE_BOLT: do {
					if (t[2] != 0) {
						t[2]--;
						sector[sect].floorshade = 20;
						sector[sect].ceilingshade = 20;
						continue BOLT;
					}

					if ((s.xrepeat | s.yrepeat) == 0) {
						s.xrepeat = (short) t[0];
						s.yrepeat = (short) t[1];
					} else if (((engine.krand() & 8) == 0)) {
						t[0] = s.xrepeat;
						t[1] = s.yrepeat;
						t[2] = global_random & 4;
						s.xrepeat = s.yrepeat = 0;
						continue CLEAR_THE_BOLT;
					}
					break;
				} while (true);

				s.picnum++;

				l = global_random & 7;
				s.xrepeat = (short) (l + 8);

				if ((l & 1) != 0)
					s.cstat ^= 2;

				if (s.picnum == (BOLT1 + 1) && (engine.krand() & 7) == 0 && sector[sect].floorpicnum == HURTRAIL)
					spritesound(SHORT_CIRCUIT, i);

				if (s.picnum == BOLT1 + 4)
					s.picnum = BOLT1;

				if ((s.picnum & 1) != 0) {
					sector[sect].floorshade = 0;
					sector[sect].ceilingshade = 0;
				} else {
					sector[sect].floorshade = 20;
					sector[sect].ceilingshade = 20;
				}
				continue;

			case WATERDRIP:
				if (t[1] != 0) {
					t[1]--;
					if (t[1] == 1) // XXX == 0 in duke
						s.cstat &= 32767;
				} else {
					makeitfall(currentGame.getCON(), i);
					ssp(i, CLIPMASK0);
					if (s.xvel > 0)
						s.xvel -= 2;

					if (s.zvel == 0) {
						s.cstat |= 32768;

						if (s.pal != 2 && s.hitag == 0)
							spritesound(SOMETHING_DRIPPING, i);

						if (sprite[s.owner].picnum != WATERDRIP) {
							engine.deletesprite(i);
							continue;
						} else {
							game.pInt.setsprinterpolate(i, s);
							s.z = t[0];
							t[1] = 48 + (engine.krand() & 31);
						}
					}
				}

				continue;

			case DOORSHOCK:
				j = klabs(sector[sect].ceilingz - sector[sect].floorz) >> 9;
				s.yrepeat = (short) (j + 4);
				s.xrepeat = 16;
				s.z = sector[sect].floorz;
				continue;

			case TOUCHPLATE: // ok
				if (t[1] == 1 && s.hitag >= 0) // Move the sector floor
				{
					x = sector[sect].floorz;

					if (t[3] == 1) {
						if (x >= t[2]) {
							game.pInt.setfloorinterpolate(sect, sector[sect]);
							sector[sect].floorz = x;
							t[1] = 0;
						} else {
							game.pInt.setfloorinterpolate(sect, sector[sect]);
							sector[sect].floorz += sector[sect].extra;
							p = checkcursectnums(sect);
							if (p >= 0)
								ps[p].posz += sector[sect].extra;
						}
					} else {
						if (x <= s.z) {
							game.pInt.setfloorinterpolate(sect, sector[sect]);
							sector[sect].floorz = s.z;
							t[1] = 0;
						} else {
							game.pInt.setfloorinterpolate(sect, sector[sect]);
							sector[sect].floorz -= sector[sect].extra;
							p = checkcursectnums(sect);
							if (p >= 0)
								ps[p].posz -= sector[sect].extra;
						}
					}
					continue;
				}

				if (t[5] == 1)
					continue;

				p = checkcursectnums(sect);
				if (p >= 0 && (ps[p].on_ground || s.ang == 512)) {
					if (t[0] == 0 && !check_activator_motion(s.lotag)) {
						t[0] = 1;
						t[1] = 1;
						t[3] ^= 1;
						operatemasterswitches(s.lotag);
						operateactivators(s.lotag, p);
						if (s.hitag > 0) {
							s.hitag--;
							if (s.hitag == 0)
								t[5] = 1;
						}
					}
				} else
					t[0] = 0;

				if (t[1] == 1) {
					j = headspritestat[6];
					while (j >= 0) {
						if (j != i && sprite[j].picnum == TOUCHPLATE && sprite[j].lotag == s.lotag) {
							hittype[j].temp_data[1] = 1;
							hittype[j].temp_data[3] = t[3];
						}
						j = nextspritestat[j];
					}
				}
				continue;

			case CANWITHSOMETHING: // ok?
				makeitfall(currentGame.getCON(), i);
				j = ifhitbyweapon(i);

				if (j >= 0) {
					spritesound(VENT_BUST, i);
					for (j = 0; j < 10; j++)
						RANDOMSCRAP(s, i);

					if (s.lotag != 0)
						spawn(i, s.lotag);

					engine.deletesprite(i);
				}
				continue;

			case 1187:
			case 1196:
			case 1251:
			case 1268:
			case 1304:
			case 1305:
			case 1306:
			case 1315:
			case 1317:
			case 1388:
			case STEAM:
			case CEILINGSTEAM:
				p = findplayer(s);
				execute(currentGame.getCON(), i, p, player_dist);
				continue;
			case WATERBUBBLEMAKER: // ok
				p = findplayer(s);
				execute(currentGame.getCON(), i, p, player_dist);
				continue;
			}
		}
	}

	public static void bounce(int i) {
		int k, l, daang, dax, day, daz, xvect, yvect, zvect;
		short hitsect;
		SPRITE s = sprite[i];

		xvect = mulscale(s.xvel, sintable[(s.ang + 512) & 2047], 10);
		yvect = mulscale(s.xvel, sintable[s.ang & 2047], 10);
		zvect = s.zvel;

		hitsect = s.sectnum;

		k = sector[hitsect].wallptr;
		l = wall[k].point2;
		daang = engine.getangle(wall[l].x - wall[k].x, wall[l].y - wall[k].y);

		if (s.z < (hittype[i].floorz + hittype[i].ceilingz) >> 1)
			k = sector[hitsect].ceilingheinum;
		else
			k = sector[hitsect].floorheinum;

		dax = mulscale(k, sintable[(daang) & 2047], 14);
		day = mulscale(k, sintable[(daang + 1536) & 2047], 14);
		daz = 4096;

		k = xvect * dax + yvect * day + zvect * daz;
		l = dax * dax + day * day + daz * daz;
		if ((klabs(k) >> 14) < l) {
			k = divscale(k, l, 17);
			xvect -= mulscale(dax, k, 16);
			yvect -= mulscale(day, k, 16);
			zvect -= mulscale(daz, k, 16);
		}

		s.zvel = (short) zvect;
		s.xvel = (short) engine.ksqrt(dmulscale(xvect, xvect, yvect, yvect, 8));
		s.ang = engine.getangle(xvect, yvect);
	}

	public static void movetransports() {
		char warpspriteto;
		short k, p, sect, sectlotag;
		int ll, onfloorz, zz = 0;

		int i = headspritestat[9]; // Transporters
		int nexti = i >= 0 ? nextspritestat[i] : 0;

		BOLT: for (; i >= 0; i = nexti) {
			nexti = nextspritestat[i];
			sect = sprite[i].sectnum;
			sectlotag = sector[sect].lotag;

			if (sprite[i].owner == i) {
				i = nexti;
				continue;
			}

			onfloorz = hittype[i].temp_data[4];

			if (hittype[i].temp_data[0] > 0)
				hittype[i].temp_data[0]--;

			short j = headspritesect[sect];
			short nextj = j >= 0 ? nextspritesect[j] : 0;

			for (; j >= 0; j = nextj) {
				nextj = nextspritesect[j];

				switch (sprite[j].statnum) {
				case 10: // Player

					if (sprite[j].owner != -1) {
						p = sprite[j].yvel;

						ps[p].on_warping_sector = 1;

						if (ps[p].transporter_hold == 0 && ps[p].jumping_counter == 0) {
							if (ps[p].on_ground && sectlotag == 0 && onfloorz != 0 && ps[p].jetpack_on == 0) {
								if (sprite[i].pal == 0) {
									spawn(i, TRANSPORTERBEAM);
									spritesound(TELEPORTER, i);
								}

								for (k = connecthead; k >= 0; k = connectpoint2[k])
									if (ps[k].cursectnum == sprite[sprite[i].owner].sectnum) {
										ps[k].frag_ps = p;
										sprite[ps[k].i].extra = 0;
									}

								ps[p].ang = sprite[sprite[i].owner].ang;

								if (sprite[sprite[i].owner].owner != sprite[i].owner) {
									hittype[i].temp_data[0] = 13;
									hittype[sprite[i].owner].temp_data[0] = 13;
									ps[p].transporter_hold = 13;
								}

								ps[p].bobposx = ps[p].oposx = ps[p].posx = sprite[sprite[i].owner].x;
								ps[p].bobposy = ps[p].oposy = ps[p].posy = sprite[sprite[i].owner].y;
								ps[p].oposz = ps[p].posz = sprite[sprite[i].owner].z - PHEIGHT;

								game.pInt.setsprinterpolate(ps[p].i, sprite[ps[p].i]);
								ps[p].UpdatePlayerLoc();

								engine.changespritesect(j, sprite[sprite[i].owner].sectnum);
								ps[p].cursectnum = sprite[j].sectnum;

								k = (short) spawn(sprite[i].owner, TRANSPORTERBEAM);
								spritesound(TELEPORTER, k);

								break;
							}
						} else // if (!(sectlotag == 1 && ps[p].on_ground)) GDX 2.10.18
							break;

						if (onfloorz == 0 && klabs(sprite[i].z - ps[p].posz) < 6144)
							if ((ps[p].jetpack_on == 0) || (ps[p].jetpack_on != 0 && (sync[p].bits & 1) != 0)
									|| (ps[p].jetpack_on != 0 && (sync[p].bits & 2) != 0)) {
								ps[p].oposx = ps[p].posx += sprite[sprite[i].owner].x - sprite[i].x;
								ps[p].oposy = ps[p].posy += sprite[sprite[i].owner].y - sprite[i].y;

								if (ps[p].jetpack_on != 0 && ((sync[p].bits & 1) != 0 || ps[p].jetpack_on < 11))
									ps[p].posz = sprite[sprite[i].owner].z - 6144;
								else
									ps[p].posz = sprite[sprite[i].owner].z + 6144;
								ps[p].oposz = ps[p].posz;

								game.pInt.setsprinterpolate(ps[p].i, sprite[ps[p].i]);
								ps[p].UpdatePlayerLoc();

								engine.changespritesect(j, sprite[sprite[i].owner].sectnum);
								ps[p].cursectnum = sprite[sprite[i].owner].sectnum;

								break;
							}

						k = 0;
						if (currentGame.getCON().type == RRRA) {
							if (onfloorz != 0 && sectlotag == 160 && ps[p].posz > (sector[sect].floorz - 12288)) {
								k = 2;
								ps[p].oposz = ps[p].posz = sector[sprite[sprite[i].owner].sectnum].ceilingz + 1792;
							}

							if (onfloorz != 0 && sectlotag == 161 && ps[p].posz < (sector[sect].ceilingz + 1536)) {
								k = 2;
								if (sprite[ps[p].i].extra <= 0)
									break;
								ps[p].oposz = ps[p].posz = sector[sprite[sprite[i].owner].sectnum].floorz - 12544;
							}
						}

						if ((onfloorz != 0 && sectlotag == 1
						// && ps[p].on_ground GDX
								&& ps[p].posz > (sector[sect].floorz - 1536))
								|| (onfloorz != 0 && sectlotag == 1 && ps[p].OnMotorcycle)) // (16 << 8)))
						// && ((sync[p].bits & 2) != 0 || ps[p].poszv > 2048)) GDX
						{
							if (ps[p].OnBoat)
								break;
							;
							k = 1;
							if (screenpeek == p) {
								BuildGdx.audio.getSound().stopAllSounds();
								clearsoundlocks();
							}
							spritesound(DUKE_UNDERWATER, j);
							ps[p].oposz = ps[p].posz = sector[sprite[sprite[i].owner].sectnum].ceilingz + (7 << 8);
							if (ps[p].OnMotorcycle)
								ps[p].CarVar1 = 1;

							game.pInt.setsprinterpolate(ps[p].i, sprite[ps[p].i]);
							ps[p].UpdatePlayerLoc();

//							ps[p].posxv = 4096 - (engine.krand() & 8192); GDX
//							ps[p].posyv = 4096 - (engine.krand() & 8192);

						}

						if (onfloorz != 0 && sectlotag == 2 && ps[p].posz < (sector[sect].ceilingz + (6 << 8))) {
							k = 1;
							if (sprite[j].extra <= 0)
								break;
							if (screenpeek == p) {
								BuildGdx.audio.getSound().stopAllSounds();
								clearsoundlocks();
							}
							spritesound(DUKE_GASP, j);

							ps[p].oposz = ps[p].posz = sector[sprite[sprite[i].owner].sectnum].floorz - (7 << 8);

							game.pInt.setsprinterpolate(ps[p].i, sprite[ps[p].i]);
							ps[p].UpdatePlayerLoc();

//							ps[p].jumping_toggle = 1; GDX 2.10.2018
//							ps[p].jumping_counter = 0;
						}

						if (k == 1) {
							game.pInt.setsprinterpolate(ps[p].i, sprite[ps[p].i]);

							ps[p].oposx = ps[p].posx += sprite[sprite[i].owner].x - sprite[i].x;
							ps[p].oposy = ps[p].posy += sprite[sprite[i].owner].y - sprite[i].y;

							if (sprite[sprite[i].owner].owner != sprite[i].owner)
								ps[p].transporter_hold = -2;
							ps[p].cursectnum = sprite[sprite[i].owner].sectnum;

							engine.changespritesect(j, sprite[sprite[i].owner].sectnum);
//							engine.setsprite(ps[p].i, ps[p].posx, ps[p].posy,
//									ps[p].posz + PHEIGHT); GDX 2.10.2018

							setpal(ps[p]);

							if ((engine.krand() & 255) < 32)
								spawn(j, WATERSPLASH2);

//							if (sectlotag == 1) GDX 2.10.2018
//								for (l = 0; l < 9; l++) {
//									q = spawn(ps[p].i, WATERBUBBLE);
//									sprite[q].z += engine.krand() & 16383;
//								}

							ps[p].UpdatePlayerLoc();
						} else if (k == 2) {
							game.pInt.setsprinterpolate(ps[p].i, sprite[ps[p].i]);

							ps[p].oposx = ps[p].posx += sprite[sprite[i].owner].x - sprite[i].x;
							ps[p].oposy = ps[p].posy += sprite[sprite[i].owner].y - sprite[i].y;

							if (sprite[sprite[i].owner].owner != sprite[i].owner)
								ps[p].transporter_hold = -2;
							ps[p].cursectnum = sprite[sprite[i].owner].sectnum;
							engine.changespritesect(j, sprite[sprite[i].owner].sectnum);

							ps[p].UpdatePlayerLoc();
						}
					}
					break;

				case 1:
					if (currentGame.getCON().type != RRRA) {
						switch (sprite[j].picnum) {
						case 5501:
						case UFO1:
						case UFO2:
						case UFO3:
						case UFO4:
						case UFO5:
							// if (sprite[j].extra > 0) GDX 2.10.2018
							continue;
						}
					} else {
						switch (sprite[j].picnum) {
						case 5501:
						case DAISYAIRBOAT:
						case HULKAIRBOAT:
						case MINIONAIRBOAT:
						case MINIONUFO:
							continue;
						}
					}
				case 4:
				case 5:
				case 12:
				case 13:

					ll = klabs(sprite[j].zvel);

				{
					warpspriteto = 0;
					if (ll != 0 && sectlotag == 2 && sprite[j].z < (sector[sect].ceilingz + ll))
						warpspriteto = 1;

					if (ll != 0 && sectlotag == 1 && sprite[j].z > (sector[sect].floorz - ll)
							&& sprite[j].picnum != DAISYAIRBOAT && sprite[j].picnum != HULKAIRBOAT
							&& sprite[j].picnum != MINIONAIRBOAT)
						warpspriteto = 1;

					if (currentGame.getCON().type == RRRA) {
						if (ll != 0 && sectlotag == 161 && sprite[j].z < (sector[sect].ceilingz + ll)
								&& sprite[j].zvel < 0) {
							warpspriteto = 1;
							zz = ll - klabs(sprite[j].z - sector[sect].ceilingz);
						} else if (sectlotag == 161) {
							if (sector[sect].ceilingz + 1000 > sprite[j].z && sprite[j].zvel < 0) {
								warpspriteto = 1;
								zz = 1;
							}
						}
						if (ll != 0 && sectlotag == 160 && sprite[j].z > (sector[sect].floorz - ll)
								&& sprite[j].zvel >= 0) {
							warpspriteto = 1;
							zz = ll - klabs(sector[sect].floorz - sprite[j].z);
						} else if (sectlotag == 160) {
							if (sector[sect].floorz - 1000 < sprite[j].z && sprite[j].zvel >= 0) {
								warpspriteto = 1;
								zz = 1;
							}
						}
					}

					if (sectlotag == 0 && (onfloorz != 0 || klabs(sprite[j].z - sprite[i].z) < 4096)) {
						if (sprite[sprite[i].owner].owner != sprite[i].owner && onfloorz != 0
								&& hittype[i].temp_data[0] > 0 && sprite[j].statnum != 5) {
							hittype[i].temp_data[0]++;
							continue BOLT;
						}
						warpspriteto = 1;
					}

					if (warpspriteto != 0)
						switch (sprite[j].picnum) {
						case TRANSPORTERSTAR:
						case TRANSPORTERBEAM:
						case BULLETHOLE:
						case WATERSPLASH2:
						case BURNING:
						case BURNING2:
						case FIRE:
						case FIRE2:
						case MUD:
							continue;
						case PLAYERONWATER:
							if (sectlotag == 2) {
								sprite[j].cstat &= 32767;
								break;
							}
						default:
							if (sprite[j].statnum == 5
									&& !(sectlotag == 1 || sectlotag == 2 || sectlotag == 160 || sectlotag == 161))
								break;

						case WATERBUBBLE:
							if ((engine.krand() >> 8) < 63) {
								if (sectlotag > 0) {
									k = (short) spawn(j, WATERSPLASH2);
									if (sectlotag == 1 && sprite[j].statnum == 4) {
										sprite[k].xvel = (short) (sprite[j].xvel >> 1);
										sprite[k].ang = sprite[j].ang;
										ssp(k, CLIPMASK0);
									}
								}

								switch (sectlotag) {
								case 0:
									if (onfloorz != 0) {
										if (sprite[j].statnum == 4 || (checkcursectnums(sect) == -1
												&& checkcursectnums(sprite[sprite[i].owner].sectnum) == -1)) {
											game.pInt.setsprinterpolate(j, sprite[j]);

											sprite[j].x += (sprite[sprite[i].owner].x - sprite[i].x);
											sprite[j].y += (sprite[sprite[i].owner].y - sprite[i].y);
											sprite[j].z -= sprite[i].z - sector[sprite[sprite[i].owner].sectnum].floorz;
											sprite[j].ang = sprite[sprite[i].owner].ang;

											if (sprite[i].pal == 0) {
												k = (short) spawn(i, TRANSPORTERBEAM);
												spritesound(TELEPORTER, k);

												k = (short) spawn(sprite[i].owner, TRANSPORTERBEAM);
												spritesound(TELEPORTER, k);
											}

											if (sprite[sprite[i].owner].owner != sprite[i].owner) {
												hittype[i].temp_data[0] = 13;
												hittype[sprite[i].owner].temp_data[0] = 13;
											}

											engine.changespritesect(j, sprite[sprite[i].owner].sectnum);
										}
									} else {
										game.pInt.setsprinterpolate(j, sprite[j]);

										sprite[j].x += (sprite[sprite[i].owner].x - sprite[i].x);
										sprite[j].y += (sprite[sprite[i].owner].y - sprite[i].y);
										sprite[j].z = sprite[sprite[i].owner].z + 4096;

										engine.changespritesect(j, sprite[sprite[i].owner].sectnum);
									}
									break;
								case 1:
									game.pInt.setsprinterpolate(j, sprite[j]);

									sprite[j].x += (sprite[sprite[i].owner].x - sprite[i].x);
									sprite[j].y += (sprite[sprite[i].owner].y - sprite[i].y);
									sprite[j].z = sector[sprite[sprite[i].owner].sectnum].ceilingz + ll;

									engine.changespritesect(j, sprite[sprite[i].owner].sectnum);

									break;
								case 2:
									game.pInt.setsprinterpolate(j, sprite[j]);

									sprite[j].x += (sprite[sprite[i].owner].x - sprite[i].x);
									sprite[j].y += (sprite[sprite[i].owner].y - sprite[i].y);
									sprite[j].z = sector[sprite[sprite[i].owner].sectnum].floorz - ll;

									engine.changespritesect(j, sprite[sprite[i].owner].sectnum);

									break;
								case 160:
									if (currentGame.getCON().type == RRRA) {
										game.pInt.setsprinterpolate(j, sprite[j]);

										sprite[j].x += (sprite[sprite[i].owner].x - sprite[i].x);
										sprite[j].y += (sprite[sprite[i].owner].y - sprite[i].y);
										sprite[j].z = zz + sector[sprite[sprite[i].owner].sectnum].ceilingz;

										engine.changespritesect(j, sprite[sprite[i].owner].sectnum);

										movesprite(j, (sprite[j].xvel * (sintable[(sprite[j].ang + 512) & 2047])) >> 14,
												(sprite[j].xvel * (sintable[sprite[j].ang & 2047])) >> 14, 0,
												CLIPMASK1);
									}
									break;
								case 161:
									if (currentGame.getCON().type == RRRA) {
										game.pInt.setsprinterpolate(j, sprite[j]);

										sprite[j].x += (sprite[sprite[i].owner].x - sprite[i].x);
										sprite[j].y += (sprite[sprite[i].owner].y - sprite[i].y);
										sprite[j].z = sector[sprite[sprite[i].owner].sectnum].floorz - zz;

										engine.changespritesect(j, sprite[sprite[i].owner].sectnum);

										movesprite(j, (sprite[j].xvel * (sintable[(sprite[j].ang + 512) & 2047])) >> 14,
												(sprite[j].xvel * (sintable[sprite[j].ang & 2047])) >> 14, 0,
												CLIPMASK1);
									}
									break;
								}
							}

							break;
						}
				}
					break;

				}
			}
		}
	}

	public static void moveactors() {
		int x, m, l, t[];
		int a, j, nextj, sect, p;
		SPRITE s;
		int k;

		if (numjaildoors != 0)
			movejails();
		if (numminecart != 0)
			movecarts();

		for (int i = headspritestat[115]; i >= 0; i = nextspritestat[i]) { // RA
			if (sprite[i].extra != 0) {
				if (sprite[i].picnum != 8162)
					sprite[i].picnum = 8162;
				sprite[i].extra--;
				if (sprite[i].extra == 0) {
					int chance = engine.krand() & 0x7F;
					if (chance >= 96) {
						if (chance >= 112) {
							if (chance >= 120) {
								if (chance >= 126) {
									if ((ps[connecthead].SlotWin & 8) != 0)
										sprite[i].picnum = 8165;
									else {
										sprite[i].picnum = 8166;
										spawn(i, 5595);
										ps[connecthead].SlotWin |= 8;
										spritesound(52, i);
									}
								} else if ((ps[connecthead].SlotWin & 4) != 0)
									sprite[i].picnum = 8165;
								else {
									sprite[i].picnum = 8167;
									spawn(i, 52);
									ps[connecthead].SlotWin |= 4;
									spritesound(52, i);
								}
							} else if ((ps[connecthead].SlotWin & 2) != 0)
								sprite[i].picnum = 8165;
							else {
								sprite[i].picnum = 8168;
								spawn(i, 26);
								ps[connecthead].SlotWin |= 2;
								spritesound(52, i);
							}
						} else if ((ps[connecthead].SlotWin & 1) != 0)
							sprite[i].picnum = 8165;
						else {
							sprite[i].picnum = 8164;
							spawn(i, 41);
							ps[connecthead].SlotWin |= 1;
							spritesound(52, i);
						}
					} else
						sprite[i].picnum = 8165;
				}
			}
		}

		for (short i = headspritestat[116], nexti; i >= 0; i = nexti) { // RA
			nexti = nextspritestat[i];
			if (sprite[i].extra != 0) {
				if (sprite[i].extra == sprite[i].lotag)
					sound(183);
				sprite[i].extra--;

				k = movesprite(i, mulscale(sintable[(sprite[i].ang + 512) & kAngleMask], sprite[i].hitag, 14),
						mulscale(sintable[sprite[i].ang & kAngleMask], sprite[i].hitag, 14), 2 * sprite[i].hitag,
						CLIPMASK0);
				if (k > 0) {
					spritesound(14, i);
					engine.deletesprite(i);
				}
				if (sprite[i].extra == 0) {
					sound(215);
					engine.deletesprite(i);
					earthquaketime = 32;
					for (p = connecthead; p >= 0; p = connectpoint2[p]) {
						ps[p].pals[0] = 32;
						ps[p].pals[1] = 32;
						ps[p].pals[2] = 32;
						ps[p].pals_time = 48;
					}
				}
			}
		}

		for (short i = headspritestat[117], nexti; i >= 0; i = nexti) { // RA
			nexti = nextspritestat[i];
			if (sprite[i].hitag > 2)
				sprite[i].hitag = 0;
			if ((sprite[i].picnum == 8488 || sprite[i].picnum == 8490) && sprite[i].hitag != 2) {
				sprite[i].hitag = 2;
				sprite[i].extra = -100;
			}

			if (sprite[i].hitag != 0) {
				if (sprite[i].hitag == 1) {
					sprite[i].extra--;
					if (sprite[i].extra <= -30)
						sprite[i].hitag = 0;
				} else if (sprite[i].hitag == 2) {
					sprite[i].extra--;
					if (sprite[i].extra <= -104) {
						spawn(i, sprite[i].lotag);
						engine.deletesprite(i);
					}
				}
			} else if (++sprite[i].extra >= 30)
				sprite[i].hitag = 1;

			movesprite(i, 0, 0, 2 * sprite[i].extra, CLIPMASK0);
		}

		for (short i = headspritestat[118]; i >= 0; i = nextspritestat[i]) { // RA
			if (sprite[i].hitag > 1)
				sprite[i].hitag = 0;
			if (sprite[i].hitag != 0) {
				if (sprite[i].hitag == 1) {
					sprite[i].extra--;
					if (sprite[i].extra <= -20)
						sprite[i].hitag = 0;
				}
			} else if (++sprite[i].extra >= 20)
				sprite[i].hitag = 1;

			movesprite(i, 0, 0, sprite[i].extra, CLIPMASK0);
		}

		if (ps[connecthead].MamaEnd > 0 && --ps[connecthead].MamaEnd == 0) {
			LeaveMap();
			ud.eog = 1;
		}

		if (ps[connecthead].field_607 > 0) {
			for (j = 0; j < MAXSPRITES; j++) {
				switch (sprite[j].picnum) {
				case 4147:
				case 4162:
				case 4163:
				case 4249:
				case 4260:
				case 4352:
				case 4429:
				case 4649:
				case 4650:
				case 4861:
				case 4916:
				case 4945:
				case 5120:
				case 5121:
				case MINIONUFO:
				case 5274:
				case 5278:
				case 5282:
				case 5286:
				case 5376:
				case 5377:
				case 5635:
				case 5890:
				case 5891:
				case 5995:
				case 6225:
				case 6401:
				case 6658:
				case 6659:
				case 7030:
				case 7035:
				case 7192:
				case 7199:
				case 7206:
				case 7280:
				case 8705:
					if (ps[connecthead].field_607 == 3) {
						sprite[j].xrepeat <<= 1;
						sprite[j].yrepeat <<= 1;
						sprite[j].clipdist = engine.getTile(sprite[j].picnum).getWidth() * sprite[j].xrepeat >> 7;
					} else if (ps[connecthead].field_607 == 2) {
						sprite[j].xrepeat >>= 1;
						sprite[j].yrepeat >>= 1;
						sprite[j].clipdist = engine.getTile(sprite[j].picnum).getWidth() * sprite[j].xrepeat >> 7;
					}
					break;
				}
			}
			ps[connecthead].field_607 = 0;
		}

		for (int i = headspritestat[119]; i >= 0; i = nextspritestat[i]) { // RA
			if (sprite[i].hitag > 0) {
				if (sprite[i].extra != 0)
					sprite[i].extra--;
				else {
					sprite[i].hitag--;
					sprite[i].extra = 150;
					spawn(i, 7280);
				}
			}
		}

		for (short i = headspritestat[121]; i >= 0; i = nextspritestat[i]) { // RA
			if (++sprite[i].extra >= 100) {
				if (sprite[i].extra == 200) {
					engine.setsprite(i, sprite[i].x, sprite[i].y, sector[sprite[i].sectnum].floorz - 10);
					sprite[i].extra = 1;
					sprite[i].picnum = 4956;
					spawn(i, 1398);
				}
			} else {
				if (sprite[i].extra == 90) {
					if (--sprite[i].picnum < 4952)
						sprite[i].picnum = 4952;
					sprite[i].extra = 1;
				}

				movesprite(i, 0, 0, -300, CLIPMASK0);
				if (sector[sprite[i].sectnum].ceilingz + 1024 > sprite[i].z) {
					sprite[i].picnum = 0;
					sprite[i].extra = 100;
				}
			}
		}

		for (int i = headspritestat[122]; i >= 0; i = nextspritestat[i]) { // RA
			if (sprite[i].extra != 0) {
				if (sprite[i].picnum != 8589)
					sprite[i].picnum = 8589;
				sprite[i].extra--;
				if (sprite[i].extra == 0) {
					int change = engine.krand() & 0x7F;
					if (change >= 96) {
						if (change >= 112) {
							if (change >= 120) {
								if (change >= 126) {
									if ((ps[connecthead].SlotWin & 8) != 0)
										sprite[i].picnum = 8593;
									else {
										sprite[i].picnum = 8592;
										spawn(i, 5595);
										ps[connecthead].SlotWin |= 8;
										spritesound(342, i);
									}
								} else if ((ps[connecthead].SlotWin & 4) != 0)
									sprite[i].picnum = 8593;
								else {
									sprite[i].picnum = 8591;
									spawn(i, 52);
									ps[connecthead].SlotWin |= 4;
									spritesound(342, i);
								}
							} else if ((ps[connecthead].SlotWin & 2) != 0)
								sprite[i].picnum = 8593;
							else {
								sprite[i].picnum = 8595;
								spawn(i, 26);
								ps[connecthead].SlotWin |= 2;
								spritesound(342, i);
							}
						} else if ((ps[connecthead].SlotWin & 1) != 0)
							sprite[i].picnum = 8593;
						else {
							sprite[i].picnum = 8594;
							spawn(i, 41);
							ps[connecthead].SlotWin |= 1;
							spritesound(342, i);
						}
					} else
						sprite[i].picnum = 8593;
				}
			}
		}

		for (int i = headspritestat[123]; i >= 0; i = nextspritestat[i]) { // RA
			if (sprite[i].lotag == 5 && Sound[WITNESSSTAND].num == 0)
				spritesound(WITNESSSTAND, i);
		}

		for (int i = headspritestat[107]; i >= 0; i = nextspritestat[i]) {
			s = sprite[i];
			if (s.hitag == 100) {
				s.z += 1024;
				int z = sector[s.sectnum].floorz + 15168;
				if (z <= s.z)
					s.z = z;
			}
			if (s.picnum == LUMBERBLADE) {
				s.extra++;
				if (s.extra == 192) {
					s.hitag = 0;
					s.extra = 0;
					s.picnum = LUMBERBLADE - 1;
					s.z = sector[s.sectnum].floorz - 15168;

					j = headspritestat[0];
					while (j >= 0) {
						nextj = nextspritestat[j];

						if (sprite[j].picnum == 128 && sprite[j].hitag == 999)
							sprite[j].picnum = 127;

						j = nextj;
					}
				}
			}
		}

		if (plantProcess) {
			for (int i = headspritestat[106]; i >= 0; i = nextspritestat[i]) {
				s = sprite[i];
				switch (s.picnum) {
				case CHICKENA:
					s.lotag--;
					if (s.lotag < 0) {
						int c = spawn(i, CHICKENATILE);
						sprite[c].ang = s.ang;
						s.lotag = 128;
					}
					break;
				case CHICKENC:
					s.lotag--;
					if (s.lotag < 0) {
						int c = spawn(i, CHICKENCTILE);
						sprite[c].ang = s.ang;
						s.lotag = 256;
					}
					break;
				case HEADCHK:
					s.lotag--;
					if (s.lotag < 0) {
						lotsofmoney(s, ((engine.krand() & 3) + 4));
						s.lotag = 84;
					}
					break;
				case FEATHERCHK:
					s.lotag--;
					if (s.lotag < 0) {
						s.lotag = 96;
						spritesound(472, spawn(i, HEAD1TILE));
					}
					break;
				case LOAF:
					s.lotag--;
					if (s.lotag < 0) {
						int c = spawn(i, LOAFTILE);
						sprite[c].ang = s.ang;
						s.lotag = 448;
					}
					break;
				case NUGGETS:
					s.lotag--;
					if (s.lotag < 0) {
						int c = spawn(i, NUGGETTILE);
						sprite[c].ang = s.ang;
						s.lotag = 64;
					}
					break;
				case PACKEDCHK:
					s.lotag--;
					if (s.lotag < 0) {
						int c = spawn(i, BROASTEDTILE);
						sprite[c].ang = s.ang;
						s.lotag = 512;
					}
					break;
				case BONELESSCHK:
					s.lotag--;
					if (s.lotag < 0) {
						int c = spawn(i, BONELESSTILE);
						sprite[c].ang = s.ang;
						s.lotag = 224;
					}
					break;
				case JIBSCHK:
					s.lotag--;
					if (s.lotag < 0) {
						guts(s, 1463, 1, myconnectindex);
						guts(s, 1468, 1, myconnectindex);
						guts(s, 1473, 1, myconnectindex);
						guts(s, 1478, 1, myconnectindex);
						s.lotag = 256;
					}
					break;
				}
			}
		}

		for (int i = headspritestat[105]; i >= 0; i = nextspritestat[i]) {
			s = sprite[i];
			if (s.picnum == BOWLLINE && s.lotag == 100) {
				if (BowlOpen(s.sectnum)) {
					s.lotag = 0;
					if (s.extra == 1 && BowlCheck(s.sectnum) == 0)
						s.extra = 2;
					if (s.extra == 2) {
						s.extra = 0;
						BowlUpdate(s.sectnum);
					}
				}
			}

		}

		for (short i = headspritestat[108], nexti; i >= 0; i = nexti) {
			nexti = nextspritestat[i];
			if (sprite[i].picnum == 296) {
				p = (short) findplayer(sprite[i]);

				if (player_dist < 2048) {
					j = headspritestat[108];
					while (j >= 0) {
						nextj = nextspritestat[j];
						s = sprite[j];
						if (s.picnum == 297) {
							ps[p].ang = s.ang;
							ps[p].posx = ps[p].oposx = ps[p].bobposx = s.x;
							ps[p].posy = ps[p].oposy = ps[p].bobposy = s.y;
							ps[p].posz = ps[p].oposz = s.z;
							engine.changespritesect(ps[p].i, s.sectnum);
							ps[p].cursectnum = sprite[p].sectnum;
							spritesound(TELEPORTER, j);

							game.pInt.setsprinterpolate(ps[p].i, sprite[ps[p].i]);
							ps[p].UpdatePlayerLoc();

							if (ud.multimode < 2)
								engine.deletesprite((short) j);
							else {
								s.cstat &= ~1;
								s.cstat |= 32768;
							}
						}
						j = nextj;
					}
				}
			}
		}

		short i = headspritestat[1];
		short nexti = i >= 0 ? nextspritestat[i] : 0;

		BOLT: for (; i >= 0; i = nexti) {
			nexti = nextspritestat[i];

			s = sprite[i];

			sect = s.sectnum;

			if (s.xrepeat == 0 || sect < 0 || sect >= MAXSECTORS) {
				engine.deletesprite(i);
				continue;
			}

			t = hittype[i].temp_data;

			game.pInt.clearspriteinterpolate(i);
			game.pInt.setsprinterpolate(i, sprite[i]);

			switch (s.picnum) {
			case CAMERA1:

				if (t[0] == 0) {
					t[1] += 8;
					if (currentGame.getCON().camerashitable != 0) {
						j = ifhitbyweapon(i);
						if (j >= 0) {
							t[0] = 1; // static
							s.cstat = (short) 32768;
							for (x = 0; x < 5; x++)
								RANDOMSCRAP(s, i);
							continue;
						}
					}

					if (s.hitag > 0) {
						if (t[1] < s.hitag)
							s.ang += 8;
						else if (t[1] < (s.hitag * 3))
							s.ang -= 8;
						else if (t[1] < (s.hitag << 2))
							s.ang += 8;
						else {
							t[1] = 8;
							s.ang += 16;
						}
					}
				}
				continue;

			case QUEBALL:
			case STRIPEBALL:
				if (s.xvel != 0) {
					j = headspritestat[0];
					while (j >= 0) {
						nextj = nextspritestat[j];
						if (sprite[j].picnum == POCKET && ldist(sprite[j], s) < 52) {
							engine.deletesprite(i);
							continue BOLT;
						}
						j = nextj;
					}

					j = (short) engine.clipmove(s.x, s.y, s.z, s.sectnum,
							(((s.xvel * (sintable[(s.ang + 512) & 2047])) >> 14) * TICSPERFRAME) << 11,
							(((s.xvel * (sintable[s.ang & 2047])) >> 14) * TICSPERFRAME) << 11, 24, (4 << 8), (4 << 8),
							CLIPMASK1);

					if (clipmove_sectnum != -1) {
						s.x = clipmove_x;
						s.y = clipmove_y;
						s.z = clipmove_z;
						s.sectnum = clipmove_sectnum;
					}

					int nHitObject = j & kHitTypeMask;
					if (nHitObject != 0) {
						if (nHitObject == kHitWall) {
							j &= kHitIndexMask;
							k = engine.getangle(wall[wall[j].point2].x - wall[j].x, wall[wall[j].point2].y - wall[j].y);
							s.ang = (short) (((k << 1) - s.ang) & 2047);
						} else if (nHitObject == kHitSprite) {
							j &= kHitIndexMask;
							checkhitsprite(i, (short) j);
						}
					}
					s.xvel--;
					if (s.xvel < 0)
						s.xvel = 0;
					if (s.picnum == STRIPEBALL) {
						s.cstat = 257;
						s.cstat |= 4 & s.xvel;
						s.cstat |= 8 & s.xvel;
					}
				} else {
					p = (short) findplayer(s);
					x = player_dist;
					if (x < 1596) {

						// if(s.pal == 12)
						{
							j = getincangle((int) ps[p].ang, engine.getangle(s.x - ps[p].posx, s.y - ps[p].posy));
							if (j > -64 && j < 64 && ((sync[p].bits & (1 << 29)) != 0))
								if (ps[p].toggle_key_flag == 1) {
									a = headspritestat[1];
									while (a >= 0) {
										if (sprite[a].picnum == QUEBALL || sprite[a].picnum == STRIPEBALL) {
											j = getincangle((int) ps[p].ang, engine.getangle(sprite[a].x - ps[p].posx,
													sprite[a].y - ps[p].posy));
											if (j > -64 && j < 64) {
												findplayer(sprite[a]);
												l = player_dist;
												if (x > l)
													break;
											}
										}
										a = nextspritestat[a];
									}
									if (a == -1) {
										if (s.pal == 12)
											s.xvel = 164;
										else
											s.xvel = 140;
										s.ang = (short) ps[p].ang;
										ps[p].toggle_key_flag = 2;
									}
								}
						}
					}
					if (x < 512 && s.sectnum == ps[p].cursectnum) {
						s.ang = engine.getangle(s.x - ps[p].posx, s.y - ps[p].posy);
						s.xvel = 48;
					}
				}

				break;

			case RAT:
				makeitfall(currentGame.getCON(), i);
				if (ssp(i, CLIPMASK0)) {
					if ((engine.krand() & 255) < 3)
						spritesound(RATTY, i);
					s.ang += (engine.krand() & 31) - 15 + (sintable[(t[0] << 8) & 2047] >> 11);
				} else {
					hittype[i].temp_data[0]++;
					if (hittype[i].temp_data[0] > 1) {
						engine.deletesprite(i);
						continue;
					} else
						s.ang = (short) (engine.krand() & 2047);
				}
				if (s.xvel < 128)
					s.xvel += 2;
				s.ang += (engine.krand() & 3) - 6;
				break;

			case MORTER:
			case DYNAMITE:
			case 3464:
				if ((s.cstat & 32768) != 0) {
					t[2]--;
					if (t[2] <= 0) {
						spritesound(TELEPORTER, i);
						spawn(i, TRANSPORTERSTAR);
						s.cstat = 257;
					}
					continue;
				}

				p = findplayer(s);
				x = player_dist;

				if (x < 1220)
					s.cstat &= ~257;
				else
					s.cstat |= 257;

				do {
					if (t[3] == 0) {
						j = ifhitbyweapon(i);
						if (j >= 0) {
							t[3] = 1;
							t[4] = 0;
							l = 0;
							s.xvel = 0;
							break;
						}
					}

					makeitfall(currentGame.getCON(), i);

					if (sector[sect].lotag != 1 && sector[sect].lotag != 160 && s.z >= hittype[i].floorz - (FOURSLEIGHT)
							&& s.yvel < 3) {
						if (s.yvel > 0 || (s.yvel == 0 && hittype[i].floorz == sector[sect].floorz && s.picnum != 3464))
							spritesound(PIPEBOMB_BOUNCE, i);

						if (s.picnum != 3464) {
							s.zvel = (short) -((4 - s.yvel) << 8);
							if (sector[s.sectnum].lotag == 2)
								s.zvel >>= 2;
							s.yvel++;
						} else {
							t[3] = 1;
							t[4] = 1;
							l = 0;
							break;
						}
					}
					if (s.picnum != 3464 && s.z < hittype[i].ceilingz && sector[sect].lotag != 2) {
						s.z = hittype[i].ceilingz + (3 << 8);
						s.zvel = 0;
					}

					j = movesprite(i, (s.xvel * (sintable[(s.ang + 512) & 2047])) >> 14,
							(s.xvel * (sintable[s.ang & 2047])) >> 14, s.zvel, CLIPMASK0);

					if (sector[sprite[i].sectnum].lotag == 1 && s.zvel == 0) {
						s.z += (32 << 8);
						if (t[5] == 0) {
							t[5] = 1;
							spawn(i, WATERSPLASH2);
						}
					} else
						t[5] = 0;

					if (t[3] == 0 && (s.picnum == MORTER || s.picnum == 3464) && (j != 0 || x < 844)) {
						t[3] = 1;
						t[4] = 0;
						l = 0;
						s.xvel = 0;
						break;
					}

					if (sprite[s.owner].picnum == APLAYER)
						l = sprite[s.owner].yvel;
					else
						l = -1;

					if (s.xvel > 0) {
						s.xvel -= 5;
						if (sector[sect].lotag == 2)
							s.xvel -= 10;

						if (s.xvel < 0)
							s.xvel = 0;
						if ((s.xvel & 8) != 0)
							s.cstat ^= 4;
					}

					if ((j & kHitTypeMask) == kHitWall) {
						j &= kHitIndexMask;

						checkhitwall(i, j, s.x, s.y, s.z, s.picnum);

						k = engine.getangle(wall[wall[j].point2].x - wall[j].x, wall[wall[j].point2].y - wall[j].y);
						if (s.picnum == 3464) {
							t[3] = 1;
							t[4] = 0;
							m = 0;
							s.xvel = 0;
						} else {
							s.ang = (short) (((k << 1) - s.ang) & 2047);
							s.xvel >>= 1;
						}
					}

				} while (false);

				if ((l >= 0 && ps[l].hbomb_on == 0) || t[3] == 1) {
					t[4]++;

					if (t[4] == 2) {
						x = s.extra;
						m = 0;
						switch (s.picnum) {
						case DYNAMITE:
							m = currentGame.getCON().tntblastradius;
							break;
						case MORTER:
						case 3464:
							m = currentGame.getCON().morterblastradius;
							break;
						case POWDERKEGSPRITE:
							m = currentGame.getCON().powderblastradius;
							break;
						}

						if (sector[s.sectnum].lotag != 800) {
							hitradius(i, m, x >> 2, x >> 1, x - (x >> 2), x);
							spawn(i, EXPLOSION2);
							spritesound(PIPEBOMB_EXPLODE, i);
							for (x = 0; x < 8; x++)
								RANDOMSCRAP(s, i);
						}
					}

					if (s.yrepeat != 0) {
						s.yrepeat = 0;
						continue;
					}

					if (t[4] > 20) {
						if (s.owner != i || !ud.respawn_items) {
							engine.deletesprite(i);
							continue;
						} else {
							t[2] = currentGame.getCON().respawnitemtime;
							spawn(i, RESPAWNMARKERRED);
							s.cstat = (short) 32768;
							s.yrepeat = 9;
							continue;
						}
					}
					if (s.picnum == 3464) {
						spawn(i, BURNING);
						engine.deletesprite(i);
						continue;
					}
				} else if (s.picnum == DYNAMITE && x < 788 && t[0] > 7 && s.xvel == 0)
					if (engine.cansee(s.x, s.y, s.z - (8 << 8), s.sectnum, ps[p].posx, ps[p].posy, ps[p].posz,
							ps[p].cursectnum))
						if (ps[p].ammo_amount[4] < currentGame.getCON().max_ammo_amount[4]) { // GDX 3.10.2018
							if (ud.coop >= 1 && s.owner == i) {
								for (j = 0; j < ps[p].weapreccnt; j++)
									if (ps[p].weaprecs[j] == s.picnum)
										continue BOLT; // v0.751

								if (ps[p].weapreccnt < 16) // v0.751
									ps[p].weaprecs[ps[p].weapreccnt++] = s.picnum;
							}

							addammo(4, ps[p], 1);
							addammo(5, ps[p], 1);
							spritesound(DUKE_GET, ps[p].i);

							if (!ps[p].gotweapon[4] || s.owner == ps[p].i)
								addweapon(ps[p], 4);

							if (sprite[s.owner].picnum != APLAYER) {
								ps[p].pals[0] = 0;
								ps[p].pals[1] = 32;
								ps[p].pals[2] = 0;
								ps[p].pals_time = 32;
							}

							if (s.owner != i || !ud.respawn_items) {
								if (s.owner == i && ud.coop >= 1)
									continue;

								engine.deletesprite(i);
								continue;
							} else {
								t[2] = currentGame.getCON().respawnitemtime;
								spawn(i, RESPAWNMARKERRED);
								s.cstat = (short) 32768;
							}
						}

				if (t[0] < 8)
					t[0]++;
				continue;

			case OOZ:

				getglobalz(i);

				j = (hittype[i].floorz - hittype[i].ceilingz) >> 9;
				if (j > 255)
					j = 255;

				x = 25 - (j >> 1);
				if (x < 8)
					x = 8;
				else if (x > 48)
					x = 48;

				s.yrepeat = (short) j;
				s.xrepeat = (short) x;
				s.z = hittype[i].floorz;

				continue;

			case FORCESPHERE:
				if (s.yvel == 0) {
					s.yvel = 1;

					for (l = 512; l < (2048 - 512); l += 128)
						for (j = 0; j < 2048; j += 128) {
							k = spawn(i, FORCESPHERE);
							sprite[k].cstat = 257 + 128;
							sprite[k].clipdist = 64;
							sprite[k].ang = (short) j;
							sprite[k].zvel = (short) (sintable[l & 2047] >> 5);
							sprite[k].xvel = (short) (sintable[(l + 512) & 2047] >> 9);
							sprite[k].owner = i;
						}
				}

				if (t[3] > 0) {
					if (s.zvel < 6144)
						s.zvel += 192;
					s.z += s.zvel;
					if (s.z > sector[sect].floorz)
						s.z = sector[sect].floorz;
					t[3]--;
					if (t[3] == 0) {
						engine.deletesprite(i);
						continue;
					}
				} else if (t[2] > 10) {
					j = headspritestat[5];
					while (j >= 0) {
						if (sprite[j].owner == i && sprite[j].picnum == FORCESPHERE)
							hittype[j].temp_data[1] = 1 + (engine.krand() & 63);
						j = nextspritestat[j];
					}
					t[3] = 64;
				}

				continue;

			case POWDERKEGSPRITE:
				if (sector[s.sectnum].lotag != 1 && sector[s.sectnum].lotag != 160 && s.xvel != 0) {
					j = movesprite(i, (s.xvel * (sintable[(s.ang + 512) & 2047])) >> 14,
							(s.xvel * (sintable[s.ang & 2047])) >> 14, s.zvel, CLIPMASK0);
					s.xvel--;
				}

				break;

			case RESPAWNMARKERRED:
			case 876:
				hittype[i].temp_data[0]++;
				if (hittype[i].temp_data[0] > currentGame.getCON().respawnitemtime) {
					engine.deletesprite(i);
					continue;
				}
				if (hittype[i].temp_data[0] >= (currentGame.getCON().respawnitemtime >> 1)
						&& hittype[i].temp_data[0] < ((currentGame.getCON().respawnitemtime >> 1)
								+ (currentGame.getCON().respawnitemtime >> 2)))
					sprite[i].picnum = 876;
				makeitfall(currentGame.getCON(), i);
				break;

			case REACTORBURNT:
			case REACTOR2BURNT:
				continue;

			case REACTOR:
			case REACTOR2:
				if (t[4] == 1) {
					j = headspritesect[sect];
					while (j >= 0) {
						switch (sprite[j].picnum) {
						case SECTOREFFECTOR:
							if (sprite[j].lotag == 1) {
								sprite[j].lotag = -1;
								sprite[j].hitag = -1;
							}
							break;
						case REACTOR:
							sprite[j].picnum = REACTORBURNT;
							break;
						case REACTOR2:
							sprite[j].picnum = REACTOR2BURNT;
							break;
						case REACTORSPARK:
						case REACTOR2SPARK:
							sprite[j].cstat = (short) 32768;
							break;
						}
						j = nextspritesect[j];
					}
					continue;
				}

				if (t[1] >= 20) {
					t[4] = 1;
					continue;
				}

				p = findplayer(s);
				x = player_dist;

				t[2]++;
				if (t[2] == 4)
					t[2] = 0;

				if (x < 4096) {
					if ((engine.krand() & 255) < 16) {
						if (Sound[DUKE_LONGTERM_PAIN].num < 1)
							spritesound(DUKE_LONGTERM_PAIN, ps[p].i);

						spritesound(SHORT_CIRCUIT, i);

						sprite[ps[p].i].extra--;
						ps[p].pals_time = 32;
						ps[p].pals[0] = 32;
						ps[p].pals[1] = 0;
						ps[p].pals[2] = 0;
					}
					t[0] += 128;
					if (t[3] == 0)
						t[3] = 1;
				} else
					t[3] = 0;

				if (t[1] != 0) {
					t[1]++;

					t[4] = s.z;
					s.z = sector[sect].floorz;
					int dz = sector[sect].floorz - sector[sect].ceilingz;
					if (dz != 0)
						s.z -= (engine.krand() % dz);

					switch (t[1]) {
					case 3:
						// Turn on all of those flashing sectoreffector.
						hitradius(i, 4096, currentGame.getCON().impact_damage << 2,
								currentGame.getCON().impact_damage << 2, currentGame.getCON().impact_damage << 2,
								currentGame.getCON().impact_damage << 2);

						j = headspritestat[6];
						while (j >= 0) {
							if (sprite[j].picnum == MASTERSWITCH)
								if (sprite[j].hitag == s.hitag)
									if (sprite[j].yvel == 0)
										sprite[j].yvel = 1;
							j = nextspritestat[j];
						}
						break;

					case 4:
					case 7:
					case 10:
					case 15:
						short jj = headspritesect[sect];
						while (jj >= 0) {
							short ll = nextspritesect[jj];

							if (jj != i) {
								engine.deletesprite(jj);
								break;
							}
							jj = ll;
						}
						break;
					}
					for (x = 0; x < 16; x++)
						RANDOMSCRAP(s, i);

					s.z = t[4];
					t[4] = 0;

				} else {
					j = ifhitbyweapon(i);

					if (j >= 0) {
						for (x = 0; x < 32; x++)
							RANDOMSCRAP(s, i);
						if (s.extra < 0)
							t[1] = 1;
					}
				}
				continue;
			case BOWLINGBALL:
				if (s.xvel == 0) {
					spawn(i, BOWLINGBALLSPRITE);
					engine.deletesprite(i);
					continue;
				}
				if (Sound[356].num == 0)
					spritesound(356, i);
				if (sector[s.sectnum].lotag == 900)
					stopsound(356);
			case 3440:
			case 3441:
			case HENSTAND:
			case HENSTAND + 1:
				if (s.picnum == HENSTAND || s.picnum == HENSTAND + 1) {
					s.lotag--;
					if (s.lotag == 0) {
						spawn(i, HEN);
						engine.deletesprite(i);
						continue;
					}
				}

				if (sector[s.sectnum].lotag == 900)
					s.xvel = 0;
				if (s.xvel != 0) {
					makeitfall(currentGame.getCON(), i);
					j = movesprite(i, (s.xvel * (sintable[(s.ang + 512) & 2047])) >> 14,
							(s.xvel * (sintable[s.ang & 2047])) >> 14, s.zvel, CLIPMASK0);

					if ((j & kHitTypeMask) == kHitWall) {
						j &= kHitIndexMask;
						k = engine.getangle(wall[wall[j].point2].x - wall[j].x, wall[wall[j].point2].y - wall[j].y);
						s.ang = (short) (((k << 1) - s.ang) & 2047);
					} else if ((j & kHitTypeMask) == kHitSprite) {
						j &= kHitIndexMask;
						checkhitsprite((short) j, i);
						if (sprite[j].picnum == HEN) {
							int ss = spawn(j, HENSTAND);
							engine.deletesprite((short) j);

							sprite[ss].xvel = 32;
							sprite[ss].lotag = 40;
							sprite[ss].ang = s.ang;
						}
					}

					s.xvel--;
					if (s.xvel < 0)
						s.xvel = 0;
					s.cstat = 257;

					if (s.picnum == 3440) {
						s.cstat |= (s.xvel & 4) | (s.xvel & 8);
						if ((engine.krand() & 1) != 0)
							s.picnum = 3441;
					} else if (s.picnum == HENSTAND) {
						s.cstat |= (s.xvel & 4) | (s.xvel & 8);
						if ((engine.krand() & 1) != 0)
							s.picnum = HENSTAND + 1;
						if (s.xvel == 0)
							engine.deletesprite(i);
					}

					if ((s.picnum == 3440 || s.picnum == 3441) && s.xvel == 0)
						engine.deletesprite(i);
				} else if (sector[s.sectnum].lotag == 900) {
					if (s.picnum == BOWLINGBALL)
						BowlClose(i);
					engine.deletesprite(i);
					continue;
				}
				break;
			case LOAFTILE:
			case NUGGETTILE:
			case BROASTEDTILE:
			case BONELESSTILE:
			case HEAD1TILE:

				if (!plantProcess) {
					engine.deletesprite(i);
					continue;
				}

				makeitfall(currentGame.getCON(), i);
				j = movesprite(i, (s.xvel * (sintable[(s.ang + 512) & 2047])) >> 14,
						(s.xvel * (sintable[s.ang & 2047])) >> 14, s.zvel, CLIPMASK0);
				if (s.picnum == HEAD1TILE) {
					if ((sector[s.sectnum].floorz - 2048) <= s.z) {
						if (sector[s.sectnum].lotag == 1)
							sprite[spawn(i, WATERSPLASH2)].z = sector[s.sectnum].floorz;

						engine.deletesprite(i);
						continue;
					}
				}

				if ((j & kHitTypeMask) == kHitWall || (j & kHitTypeMask) == kHitSprite) {
					engine.deletesprite(i);
					continue;
				}
				if (sector[s.sectnum].lotag == 903) {
					if (sector[s.sectnum].floorz - 1024 <= s.z) {
						engine.deletesprite(i);
						continue;
					}
				}
				if (sector[s.sectnum].lotag == 904) {
					engine.deletesprite(i);
					continue;
				}

				break;
			case CHICKENATILE:
			case CHICKENBTILE:
			case CHICKENCTILE:

				if (!plantProcess) {
					engine.deletesprite(i);
					continue;
				}

				if (sector[s.sectnum].lotag == 903)
					makeitfall(currentGame.getCON(), i);

				j = movesprite(i, (s.xvel * (sintable[(s.ang + 512) & 2047])) >> 14,
						(s.xvel * (sintable[s.ang & 2047])) >> 14, s.zvel, CLIPMASK0);

				switch (sector[s.sectnum].lotag) {
				case 901:
					s.picnum = CHICKENBTILE;
					break;
				case 902:
					s.picnum = CHICKENCTILE;
					break;
				case 903:
					if ((sector[s.sectnum].floorz - 2048) <= s.z) {
						engine.deletesprite(i);
						continue;
					}
					break;
				case 904:
					engine.deletesprite(i);
					continue;
				}

				if ((j & kHitTypeMask) == kHitWall || (j & kHitTypeMask) == kHitSprite) {
					engine.deletesprite(i);
					continue;
				}
				break;
			case MOTORCYCLE: // RA
				makeitfall(currentGame.getCON(), i);
				getglobalz(i);
				if (sector[s.sectnum].lotag == 1)
					engine.setsprite(i, s.x, s.y, hittype[i].floorz + 4096);
				break;
			case SWAMPBUGGY: // RA
				makeitfall(currentGame.getCON(), i);
				getglobalz(i);
				break;
			case UFO1:
			case UFO2:
			case UFO3:
			case UFO4:
			case UFO5:
			case MINIONUFO: // RA

				getglobalz(i);

				if ((sector[s.sectnum].ceilingstat & 1) != 0)
					s.shade += (sector[s.sectnum].ceilingshade - s.shade) >> 1;
				else
					s.shade += (sector[s.sectnum].floorshade - s.shade) >> 1;

				if (s.z < sector[sect].ceilingz + (32 << 8))
					s.z = sector[sect].ceilingz + (32 << 8);

				if (ud.multimode < 2) {
					if (actor_tog != 0) {
						s.cstat = (short) 32768;
						continue;
					} else if (actor_tog == 2)
						s.cstat = 257;
				}

				j = ifhitbyweapon(i);
				if (j >= 0) {
					if (s.extra < 0 && t[0] != -1) {
						t[0] = -1;
						s.extra = 0;
					}
					RANDOMSCRAP(s, i);
				}

				if (t[0] == -1) {
					s.z += 1024;
					t[2]++;
					if ((t[2] & 3) == 0)
						spawn(i, EXPLOSION2);
					getglobalz(i);
					s.ang += 96;
					s.xvel = 128;
					j = ssp(i, CLIPMASK0) ? 1 : 0;
					if (j != 1 || s.z > hittype[i].floorz) {
						for (l = 0; l < 16; l++)
							RANDOMSCRAP(s, i);
						spritesound(LASERTRIP_EXPLODE, i);
						if (ps[connecthead].isSwamp)
							spawn(i, MINION);
						else {
							switch (s.picnum) {
							case UFO1:
								spawn(i, HEN);
								break;
							case UFO2:
								spawn(i, COOT);
								break;
							case UFO3:
								spawn(i, COW);
								break;
							case UFO4:
								spawn(i, PIG);
								break;
							case UFO5:
								spawn(i, BILLYRAY);
								break;
							case MINIONUFO: // RA
								spawn(i, HEN);
								break;
							}
						}

						ps[connecthead].actors_killed++;
						engine.deletesprite(i);
					}
					continue;
				} else {
					if (s.z > hittype[i].floorz - (48 << 8))
						s.z = hittype[i].floorz - (48 << 8);
				}

				p = findplayer(s);
				x = player_dist;
				j = s.owner;

				// 3 = findplayerz, 4 = shoot

				if (t[0] >= 4) {
					t[2]++;
					if ((t[2] & 15) == 0) {
						a = s.ang;
						s.ang = (short) hittype[i].tempang;
						shoot(i, FIRELASER);
						s.ang = (short) a;
					}
					if (t[2] > (26 * 3) || !engine.cansee(s.x, s.y, s.z - (16 << 8), s.sectnum, ps[p].posx, ps[p].posy,
							ps[p].posz, ps[p].cursectnum)) {
						t[0] = 0;
						t[2] = 0;
					} else
						hittype[i].tempang += getincangle(hittype[i].tempang,
								engine.getangle(ps[p].posx - s.x, ps[p].posy - s.y)) / 3;
				} else if (t[0] == 2 || t[0] == 3) {
					t[3] = 0;
					if (s.xvel > 0)
						s.xvel -= 16;
					else
						s.xvel = 0;

					if (t[0] == 2) {
						l = ps[p].posz - s.z;
						if (klabs(l) < (48 << 8))
							t[0] = 3;
						else
							s.z += sgn(ps[p].posz - s.z) << 10;
					} else {
						t[2]++;
						if (t[2] > (26 * 3) || !engine.cansee(s.x, s.y, s.z - (16 << 8), s.sectnum, ps[p].posx,
								ps[p].posy, ps[p].posz, ps[p].cursectnum)) {
							t[0] = 1;
							t[2] = 0;
						} else if ((t[2] & 15) == 0) {
							shoot(i, FIRELASER);
						}
					}
					s.ang += getincangle(s.ang, engine.getangle(ps[p].posx - s.x, ps[p].posy - s.y)) >> 2;
				}

				if (t[0] != 2 && t[0] != 3) {
					l = ldist(sprite[j], s);
					if (l <= 1524) {
						a = s.ang;
						s.xvel >>= 1;
					} else
						a = engine.getangle(sprite[j].x - s.x, sprite[j].y - s.y);

					if (t[0] == 1 || t[0] == 4) // Found a locator and going
												// with it
					{
						l = dist(sprite[j], s);

						if (l <= 1524) {
							if (t[0] == 1)
								t[0] = 0;
							else
								t[0] = 5;
						} else {
							// Control speed here
							if (l > 1524) {
								if (s.xvel < 256)
									s.xvel += 32;
							} else {
								if (s.xvel > 0)
									s.xvel -= 16;
								else
									s.xvel = 0;
							}
						}

						if (t[0] < 2)
							t[2]++;

						if (x < 6144 && t[0] < 2 && t[2] > (26 * 4)) {
							t[0] = 2 + (engine.krand() & 2);
							t[2] = 0;
							hittype[i].tempang = s.ang;
						}
					}

					if (t[0] == 0 || t[0] == 5) {
						if (t[0] == 0)
							t[0] = 1;
						else
							t[0] = 4;
						j = s.owner = (short) LocateTheLocator(s.hitag, -1);
						if (j == -1) {
							s.hitag = (short) (j = hittype[i].temp_data[5]);
							s.owner = (short) LocateTheLocator(j, -1);
							j = s.owner;
							if (j == -1) {
								engine.deletesprite(i);
								continue;
							}
						} else
							s.hitag++;
					}

					t[3] = getincangle(s.ang, a);
					s.ang += t[3] >> 3;

					if (s.z < sprite[j].z)
						s.z += 1024;
					else
						s.z -= 1024;
				}

				if (Sound[457].num < 2)
					spritesound(457, i);

				ssp(i, CLIPMASK0);

				continue;
			}

			if (ud.multimode < 2 && badguy(s)) {
				if (actor_tog == 1) {
					s.cstat = (short) 32768;
					continue;
				} else if (actor_tog == 2)
					s.cstat = 257;
			}

			p = findplayer(s);
			x = player_dist;

			execute(currentGame.getCON(), i, p, x);

			if (!IsOriginalDemo() && s.picnum == COW && s.extra <= 0 && (s.cstat & 256) != 0)
				s.cstat &= ~256;
		}

	}

	public static void moveexplosions() // STATNUM 5
	{
		int p;
		int l, x, t[];
		SPRITE s;

		short i = headspritestat[5], sect, j;
		short nexti = i >= 0 ? nextspritestat[i] : 0;

		for (; i >= 0; i = nexti) {
			nexti = nextspritestat[i];

			t = hittype[i].temp_data;
			s = sprite[i];
			sect = s.sectnum;

			if (sect < 0 || s.xrepeat == 0) {
				engine.deletesprite(i);
				continue;
			}

			game.pInt.setsprinterpolate(i, sprite[i]);

			switch (s.picnum) {
			case BLOODPOOL:
				if (t[0] == 0) {
					t[0] = 1;
					if ((sector[sect].floorstat & 2) != 0) {
						engine.deletesprite(i);
						continue;
					} else
						insertspriteq(i);
				}

				makeitfall(currentGame.getCON(), i);

				p = findplayer(s);
				x = player_dist;

				s.z = hittype[i].floorz - (FOURSLEIGHT);

				if (t[2] < 32) {
					t[2]++;
					if (hittype[i].picnum == TIRE) {
						if (s.xrepeat < 64 && s.yrepeat < 64) {
							s.xrepeat += engine.krand() & 3;
							s.yrepeat += engine.krand() & 3;
						}
					} else {
						if (s.xrepeat < 32 && s.yrepeat < 32) {
							s.xrepeat += engine.krand() & 3;
							s.yrepeat += engine.krand() & 3;
						}
					}
				}

				if (x < 844 && s.xrepeat > 6 && s.yrepeat > 6) {
					if (s.pal == 0 && (engine.krand() & 255) < 16) {
						if (ps[p].boot_amount > 0)
							ps[p].boot_amount--;
						else {
							if (Sound[DUKE_LONGTERM_PAIN].num < 1)
								spritesound(DUKE_LONGTERM_PAIN, ps[p].i);
							sprite[ps[p].i].extra--;
							ps[p].pals_time = 32;
							ps[p].pals[0] = 16;
							ps[p].pals[1] = 0;
							ps[p].pals[2] = 0;
						}
					}

					if (t[1] == 1)
						continue;
					t[1] = 1;

					if (hittype[i].picnum == TIRE)
						ps[p].footprintcount = 10;
					else
						ps[p].footprintcount = 3;

					ps[p].footprintpal = s.pal;
					ps[p].footprintshade = s.shade;

					if (t[2] == 32) {
						s.xrepeat -= 6;
						s.yrepeat -= 6;
					}
				} else
					t[1] = 0;

				if (sector[s.sectnum].lotag == 800 && sector[s.sectnum].floorz - 2048 <= s.z)
					engine.deletesprite(i);

				continue;

			case NEON1:
			case NEON2:
			case NEON3:
			case NEON4:
			case NEON5:
			case NEON6:
				if ((global_random / (s.lotag + 1) & 31) > 4)
					s.shade = -127;
				else
					s.shade = 127;
				continue;

			case GLASSPIECES:
			case GLASSPIECES + 1:
			case GLASSPIECES + 2:
			case POPCORN:

				makeitfall(currentGame.getCON(), i);

				if (s.zvel > 4096)
					s.zvel = 4096;
				if (sect < 0) {
					engine.deletesprite(i);
					continue;
				}

				if (s.z == hittype[i].floorz - (FOURSLEIGHT) && t[0] < 3) {
					s.zvel = (short) (-((3 - t[0]) << 8) - (engine.krand() & 511));
					if (sector[sect].lotag == 2)
						s.zvel >>= 1;
					s.xrepeat >>= 1;
					s.yrepeat >>= 1;
					if (rnd(96))
						engine.setsprite(i, s.x, s.y, s.z);
					t[0]++;// Number of bounces
				} else if (t[0] == 3) {
					engine.deletesprite(i);
					continue;
				}

				if (s.xvel > 0) {
					s.xvel -= 2;
					s.cstat = (short) ((s.xvel & 3) << 2);
				} else
					s.xvel = 0;

				ssp(i, CLIPMASK0);

				continue;

			case FEATHERS:

				s.xvel = (short) ((engine.krand() & 7) + (sintable[hittype[i].temp_data[0] & 2047] >> 9));
				hittype[i].temp_data[0] += (engine.krand() & 63);
				if ((hittype[i].temp_data[0] & 2047) > 512 && (hittype[i].temp_data[0] & 2047) < 1596) {
					if (sector[sect].lotag == 2) {
						if (s.zvel < 64)
							s.zvel += (currentGame.getCON().gc >> 5) + (engine.krand() & 7);
					} else if (s.zvel < 144)
						s.zvel += (currentGame.getCON().gc >> 5) + (engine.krand() & 7);
				}

				ssp(i, CLIPMASK0);

				if ((engine.krand() & 3) == 0)
					engine.setsprite(i, s.x, s.y, s.z);

				if (s.sectnum == -1) {
					engine.deletesprite(i);
					continue;
				}
				l = engine.getflorzofslope(s.sectnum, s.x, s.y);

				if (s.z > l) {
					s.z = l;

					insertspriteq(i);
					sprite[i].picnum++;

					j = headspritestat[5];
					while (j >= 0) {
						if (sprite[j].picnum == BLOODPOOL)
							if (ldist(s, sprite[j]) < 348) {
								s.pal = 2;
								break;
							}
						j = nextspritestat[j];
					}
				}

				if (sector[s.sectnum].lotag != 800)
					break;
				if (sector[s.sectnum].floorz - 2048 > s.z)
					break;

				engine.deletesprite(i);
				continue;
			case FEATHERS + 1:
				hittype[i].floorz = s.z = engine.getflorzofslope(s.sectnum, s.x, s.y);
				if (sector[s.sectnum].lotag != 800)
					break;
				engine.deletesprite(i);
				continue;

			case SHOTGUNSPRITE:
				if (sector[s.sectnum].lotag != 800 || sector[s.sectnum].floorz - 2048 > s.z)
					break;

				engine.deletesprite(i);
				continue;

			case WATERSPLASH2:

				t[0]++;
				if (t[0] == 1) {
					if (sector[sect].lotag != 1 && sector[sect].lotag != 2) {
						engine.deletesprite(i);
						continue;
					}
					if (Sound[ITEM_SPLASH].num == 0)
						spritesound(ITEM_SPLASH, i);
				}
				if (t[0] == 3) {
					t[0] = 0;
					t[1]++;
				}
				if (t[1] == 5)
					engine.deletesprite(i);
				continue;

			case BURNING:
			case EXPLOSION2:
			case EXPLOSION3:
			case WATERBUBBLE:
			case SMALLSMOKE:
			case BLOOD:
			case FORCERIPPLE:
			case TRANSPORTERSTAR:
			case TRANSPORTERBEAM:
				p = findplayer(s);
				x = player_dist;
				execute(currentGame.getCON(), i, p, x);
				continue;

			case JIBS1:
			case JIBS2:
			case JIBS3:
			case JIBS4:
			case JIBS5:
			case JIBS6:
			case LNRLEG:
			case LNRDTORSO:
			case LNRDGUN:
			case BILLYJIBA:
			case BILLYJIBB:
			case HULKJIBA:
			case HULKJIBB:
			case HULKJIBC:
			case MINJIBA:
			case MINJIBB:
			case MINJIBC:
			case COOTJIBA:
			case COOTJIBB:
			case COOTJIBC:

			case 2460: // RA green jibs1
			case 2465: // RA green jibs2
			case 5872: // RA motowheel
			case 5877: // RA mototank
			case 5882: // RA boarddebris
			case 6112: // RA bikenbody
			case 6117: // RA bikenhead
			case 6121: // RA bikerhead2
			case 6127: // RA bikerhand
			case 7000: // RA babahead
			case 7005: // RA bababody
			case 7010: // RA babafoot
			case 7015: // RA babahand
			case 7020: // RA debris
			case 7025: // RA debbris
			case 7387: // RA deepjibs
			case 7392: // RA deepjibs2
			case 7397: // RA deepjibs3
			case 8890: // RA deep2jibs
			case 8895: // RA deep2jibs2

				if (s.xvel > 0)
					s.xvel--;
				else
					s.xvel = 0;

				if (t[5] < 30 * 10)
					t[5]++;
				else {
					engine.deletesprite(i);
					continue;
				}

				if (s.zvel > 1024 && s.zvel < 1280) {
					engine.setsprite(i, s.x, s.y, s.z);
					sect = s.sectnum;
				}

				l = engine.getflorzofslope(sect, s.x, s.y);
				x = engine.getceilzofslope(sect, s.x, s.y);
				if (x == l || sect < 0 || sect >= MAXSECTORS) {
					engine.deletesprite(i);
					continue;
				}

				if (s.z < l - (2 << 8)) {
					if (t[1] < 2)
						t[1]++;
					else if (sector[sect].lotag != 2) {
						t[1] = 0;
						if (s.picnum == LNRLEG || s.picnum == LNRDTORSO || s.picnum == LNRDGUN) {
							if (t[0] > 6)
								t[0] = 0;
							else
								t[0]++;
						} else {
							if (t[0] > 2)
								t[0] = 0;
							else
								t[0]++;
						}
					}

					if (s.zvel < 6144) {
						if (sector[sect].lotag == 2) {
							if (s.zvel < 1024)
								s.zvel += 48;
							else
								s.zvel = 1024;
						} else
							s.zvel += currentGame.getCON().gc - 50;
					}

					s.x += (s.xvel * sintable[(s.ang + 512) & 2047]) >> 14;
					s.y += (s.xvel * sintable[s.ang & 2047]) >> 14;
					s.z += s.zvel;

					if (s.z < sector[s.sectnum].floorz) {
						if (sector[s.sectnum].lotag == 800 && sector[s.sectnum].floorz - 2048 <= s.z)
							engine.deletesprite(i);
						continue;
					}
					engine.deletesprite(i);
				} else {
					if (s.picnum == 2465 || s.picnum == 2460) // RA
					{
						engine.deletesprite(i);
						continue;
					}

					if (t[2] == 0) {
						if (s.sectnum == -1) {
							engine.deletesprite(i);
							continue;
						}
						if ((sector[s.sectnum].floorstat & 2) != 0) {
							engine.deletesprite(i);
							continue;
						}
						t[2]++;
					}
					l = engine.getflorzofslope(s.sectnum, s.x, s.y);

					s.z = l - (2 << 8);
					s.xvel = 0;

					if (s.picnum == JIBS6) {
						t[1]++;
						if ((t[1] & 3) == 0 && t[0] < 7)
							t[0]++;
						if (t[1] > 20) {
							engine.deletesprite(i);
							continue;
						}
						if (sector[s.sectnum].lotag == 800 && sector[s.sectnum].floorz - 2048 <= s.z)
							engine.deletesprite(i);

						continue;
					} else {
						s.picnum = JIBS6;
						t[0] = 0;
						t[1] = 0;
					}
				}
				continue;

			case TONGUE:
				engine.deletesprite(i);
				continue;

			case INNERJAW:
			case INNERJAW + 1:

				p = findplayer(s);
				x = player_dist;
				if (x < 512) {
					ps[p].pals_time = 32;
					ps[p].pals[0] = 32;
					ps[p].pals[1] = 0;
					ps[p].pals[2] = 0;
					sprite[ps[p].i].extra -= 4;
				}

				// break GDX 3.10.2018
			case FIRELASER:
			case UWHIP:
			case OWHIP:
			case DILDO:
				if (s.extra != 999)
					s.extra = 999;
				else {
					engine.deletesprite(i);
					continue;
				}
				break;

			case MUD:
				t[0]++;
				if (t[0] == 1) {
					if (sector[s.sectnum].floorpicnum == 3073) {
						if (Sound[ITEM_SPLASH].num == 0)
							spritesound(ITEM_SPLASH, i);

					} else
						engine.deletesprite(i);
				}

				if (t[0] == 3) {
					t[0] = 0;
					t[1]++;
				}
				if (t[1] == 5)
					engine.deletesprite(i);

				continue;

			case SHELL:
			case SHOTGUNSHELL:

				ssp(i, CLIPMASK0);

				if (sect < 0 || (sector[sect].floorz + (24 << 8)) < s.z) {
					engine.deletesprite(i);
					continue;
				}

				if (sector[sect].lotag == 2) {
					t[1]++;
					if (t[1] > 8) {
						t[1] = 0;
						t[0]++;
						t[0] &= 3;
					}
					if (s.zvel < 128)
						s.zvel += (currentGame.getCON().gc / 13); // 8
					else
						s.zvel -= 64;
					if (s.xvel > 0)
						s.xvel -= 4;
					else
						s.xvel = 0;
				} else {
					t[1]++;
					if (t[1] > 3) {
						t[1] = 0;
						t[0]++;
						t[0] &= 3;
					}
					if (s.zvel < 512)
						s.zvel += (currentGame.getCON().gc / 3); // 52;
					if (s.xvel > 0)
						s.xvel--;
					else {
						engine.deletesprite(i);
						continue;
					}
				}

				continue;

			case BLOODSPLAT1:
			case BLOODSPLAT2:
			case BLOODSPLAT3:
			case BLOODSPLAT4:

				if (t[0] == 7 * 26)
					continue;
				s.z += 16 + (engine.krand() & 15);
				t[0]++;
				if ((t[0] % 9) == 0)
					s.yrepeat++;
				continue;

			case FORCESPHERE:

				l = s.xrepeat;
				if (t[1] > 0) {
					t[1]--;
					if (t[1] == 0) {
						engine.deletesprite(i);
						continue;
					}
				}
				if (hittype[s.owner].temp_data[1] == 0) {
					if (t[0] < 64) {
						t[0]++;
						l += 3;
					}
				} else if (t[0] > 64) {
					t[0]--;
					l -= 3;
				}

				s.x = sprite[s.owner].x;
				s.y = sprite[s.owner].y;
				s.z = sprite[s.owner].z;
				s.ang += hittype[s.owner].temp_data[0];

				if (l > 64)
					l = 64;
				else if (l < 1)
					l = 1;

				s.xrepeat = (short) l;
				s.yrepeat = (short) l;
				s.shade = (byte) ((l >> 1) - 48);

				for (j = (short) t[0]; j > 0; j--)
					ssp(i, CLIPMASK0);
				continue;

			case FRAMEEFFECT1:

				if (s.owner >= 0) {
					t[0]++;

					if (t[0] > 7) {
						engine.deletesprite(i);
						continue;
					} else if (t[0] > 4)
						s.cstat |= 512 + 2;
					else if (t[0] > 2)
						s.cstat |= 2;
					s.xoffset = sprite[s.owner].xoffset;
					s.yoffset = sprite[s.owner].yoffset;
				}
				continue;
			}

			if (IFWITHIN(s, SCRAP6, SCRAP5 + 3)) {
				if (s.xvel > 0)
					s.xvel--;
				else
					s.xvel = 0;

				if (s.zvel > 1024 && s.zvel < 1280) {
					engine.setsprite(i, s.x, s.y, s.z);
					sect = s.sectnum;
				}

				if (s.z < sector[sect].floorz - (2 << 8)) {
					if (t[1] < 1)
						t[1]++;
					else {
						t[1] = 0;

						if (s.picnum < SCRAP6 + 8) {
							if (t[0] > 6)
								t[0] = 0;
							else
								t[0]++;
						} else {
							if (t[0] > 2)
								t[0] = 0;
							else
								t[0]++;
						}
					}
					if (s.zvel < 4096)
						s.zvel += currentGame.getCON().gc - 50;
					s.x += (s.xvel * sintable[(s.ang + 512) & 2047]) >> 14;
					s.y += (s.xvel * sintable[s.ang & 2047]) >> 14;
					s.z += s.zvel;
				} else {
					if (s.picnum == SCRAP1 && s.yvel > 0) {
						j = (short) spawn(i, s.yvel);
						engine.setsprite(j, s.x, s.y, s.z);
						getglobalz(j);
						sprite[j].hitag = sprite[j].lotag = 0;
					}
					engine.deletesprite(i);
				}
				continue;
			}
		}
	}

	public static void moveeffectors() // STATNUM 3
	{
		int q = 0, l, m, x, st, j, t[];
		int startwall, endwall;
		short k, nextk, p, sh, nextj, jj, ll;
		SPRITE s;

		fricxv = fricyv = 0;

		short i = headspritestat[3];
		short nexti = i >= 0 ? nextspritestat[i] : 0;

		BOLT: for (; i >= 0; i = nexti) {
			nexti = nextspritestat[i];
			s = sprite[i];

			SECTOR sc = sector[s.sectnum];
			st = s.lotag;
			sh = s.hitag;

			t = hittype[i].temp_data;

			switch (st) {
			case 0: {
				int zchange = 0;

				zchange = 0;

				j = s.owner;

				if (j == -1 || sprite[j].lotag == (short) 65535) {
					engine.deletesprite(i);
					continue;
				}

				q = sc.extra >> 3;
				l = 0;

				if (sc.lotag == 30) {
					q >>= 2;

					if (sprite[i].extra == 1) {
						if (hittype[i].tempang < 256) {
							hittype[i].tempang += 4;
							if (hittype[i].tempang >= 256)
								callsound(s.sectnum, i);
							if (s.clipdist != 0)
								l = 1;
							else
								l = -1;
						} else
							hittype[i].tempang = 256;

						if (sc.floorz > s.z) // z's are touching
						{
							game.pInt.setfloorinterpolate(s.sectnum, sc);
							sc.floorz -= 512;
							zchange = -512;
							if (sc.floorz < s.z)
								sc.floorz = s.z;
						}

						else if (sc.floorz < s.z) // z's are touching
						{
							game.pInt.setfloorinterpolate(s.sectnum, sc);
							sc.floorz += 512;
							zchange = 512;
							if (sc.floorz > s.z)
								sc.floorz = s.z;
						}
					} else if (sprite[i].extra == 3) {
						if (hittype[i].tempang > 0) {
							hittype[i].tempang -= 4;
							if (hittype[i].tempang <= 0)
								callsound(s.sectnum, i);
							if (s.clipdist != 0)
								l = -1;
							else
								l = 1;
						} else
							hittype[i].tempang = 0;

						if (sc.floorz > hittype[i].temp_data[3]) // z's are touching
						{
							game.pInt.setfloorinterpolate(s.sectnum, sc);
							sc.floorz -= 512;
							zchange = -512;
							if (sc.floorz < hittype[i].temp_data[3])
								sc.floorz = hittype[i].temp_data[3];
						}

						else if (sc.floorz < hittype[i].temp_data[3]) // z's are touching
						{
							game.pInt.setfloorinterpolate(s.sectnum, sc);
							sc.floorz += 512;
							zchange = 512;
							if (sc.floorz > hittype[i].temp_data[3])
								sc.floorz = hittype[i].temp_data[3];
						}
					}

					s.ang += (l * q);
					t[2] += (l * q);
				} else {
					if (hittype[j].temp_data[0] == 0)
						break;
					if (hittype[j].temp_data[0] == 2) {
						engine.deletesprite(i);
						continue;
					}

					if (sprite[j].ang > 1024)
						l = -1;
					else
						l = 1;
					if (t[3] == 0)
						t[3] = ldist(s, sprite[j]);
					s.xvel = (short) t[3];
					s.x = sprite[j].x;
					s.y = sprite[j].y;
					s.ang += (l * q);
					t[2] += (l * q);
				}

				if (l != 0 && (sc.floorstat & 64) != 0) {
					for (p = connecthead; p >= 0; p = connectpoint2[p]) {
						if (ps[p].cursectnum == s.sectnum && ps[p].on_ground) {

							ps[p].ang += (l * q);
							ps[p].ang = BClampAngle(ps[p].ang);

							ps[p].posz += zchange;

							Point out = engine.rotatepoint(sprite[j].x, sprite[j].y, ps[p].posx, ps[p].posy,
									(short) (q * l));

							m = out.getX();
							x = out.getY();

							ps[p].bobposx += m - ps[p].posx;
							ps[p].bobposy += x - ps[p].posy;

							ps[p].posx = m;
							ps[p].posy = x;

							if (sprite[ps[p].i].extra <= 0) {
								sprite[ps[p].i].x = m;
								sprite[ps[p].i].y = x;
							}
						}
					}

					p = headspritesect[s.sectnum];
					while (p >= 0) {
						if (sprite[p].statnum != 3 && sprite[p].statnum != 4) {
							if (sprite[p].picnum == APLAYER && sprite[p].owner >= 0) {
								p = nextspritesect[p];
								continue;
							}

							game.pInt.setsprinterpolate(p, sprite[p]);

							sprite[p].ang += (l * q);
							sprite[p].ang &= 2047;
							sprite[p].z += zchange;

							Point out = engine.rotatepoint(sprite[j].x, sprite[j].y, sprite[p].x, sprite[p].y,
									(short) (q * l));
							sprite[p].x = out.getX();
							sprite[p].y = out.getY();
						}
						p = nextspritesect[p];
					}
				}

				ms(i);
			}
				break;
			case 1: // Nothing for now used as the pivot
				if (s.owner == -1) // Init
				{
					s.owner = i;

					j = headspritestat[3];
					while (j >= 0) {
						if (sprite[j].lotag == 19 && sprite[j].hitag == sh) {
							t[0] = 0;
							break;
						}
						j = nextspritestat[j];
					}
				}

				break;
			case 6:
				k = sc.extra;
				if (t[4] > 0) {
					t[4]--;
					if (t[4] >= (k - (k >> 3)))
						s.xvel -= (k >> 5);
					if (t[4] > ((k >> 1) - 1) && t[4] < (k - (k >> 3)))
						s.xvel = 0;
					if (t[4] < (k >> 1))
						s.xvel += (k >> 5);
					if (t[4] < ((k >> 1) - (k >> 3))) {
						t[4] = 0;
						s.xvel = k;

						if (UFO_SpawnHulk > 0) {
							UFO_SpawnHulk--;
							int nSpawn = spawn(i, HULK);
							sprite[nSpawn].z = sector[sprite[nSpawn].sectnum].ceilingz;
							sprite[nSpawn].pal = 33;

							if (UFO_SpawnHulk == 0) {
								nSpawn = EGS(s.sectnum, s.x, s.y, sector[s.sectnum].ceilingz + 119428, 3677, -8, 16, 16,
										0, 0, 0, i, (short) 5);
								sprite[nSpawn].cstat = 514;
								sprite[nSpawn].yrepeat = 255;
								sprite[nSpawn].pal = 7;
								sprite[nSpawn].xrepeat = 80;
								nSpawn = spawn(i, 296);
								sprite[nSpawn].cstat = (short) 32768;
								sprite[nSpawn].z = sector[s.sectnum].floorz - 6144;
								engine.deletesprite(i);
								continue;
							}
						}
					}
				} else
					s.xvel = k;

				int count = 0;
				j = headspritesect[s.sectnum];
				while (j >= 0) {
					if (sprite[j].picnum == UFOBEAM && count != UFO_SpawnCount && ++UFO_SpawnTime == 64) {
						UFO_SpawnTime = count;
						--UFO_SpawnCount;
						int spawntile = 0;
						switch (engine.krand() & 3) {
						case 0:
							spawntile = UFO1;
							break;
						case 1:
							spawntile = UFO2;
							break;
						case 2:
							spawntile = UFO3;
							break;
						case 3:
							spawntile = UFO4;
							break;
						}
						if (currentGame.getCON().type == RRRA)
							spawntile = MINIONUFO;
						int nSpawn = spawn(i, spawntile);
						sprite[nSpawn].z = sector[sprite[nSpawn].sectnum].ceilingz;
					}
					j = nextspritesect[j];
				}

				j = headspritestat[3];
				while (j >= 0) {
					if ((sprite[j].lotag == 14) && (sh == sprite[j].hitag) && (hittype[j].temp_data[0] == t[0])) {
						sprite[j].xvel = s.xvel;

						if (hittype[j].temp_data[5] == 0)
							hittype[j].temp_data[5] = dist(sprite[j], s);
						x = sgn(dist(sprite[j], s) - hittype[j].temp_data[5]);
						if (sprite[j].extra != 0)
							x = -x;
						s.xvel += x;

						hittype[j].temp_data[4] = t[4];
					}
					j = nextspritestat[j];
				}
				x = 0;

			case 14:
				if (s.owner == -1)
					s.owner = (short) LocateTheLocator((short) t[3], (short) t[0]);

				if (s.owner == -1) {
					game.dassert("Could not find any locators for SE# 6 and 14 with a hitag of " + t[3]);
				}

				j = ldist(sprite[s.owner], s);

				if (j < 1024) {
					if (st == 6)
						if ((sprite[s.owner].hitag & 1) != 0)
							t[4] = sc.extra; // Slow it down
					t[3]++;
					s.owner = (short) LocateTheLocator(t[3], t[0]);
					if (s.owner == -1) {
						t[3] = 0;
						s.owner = (short) LocateTheLocator(0, t[0]);
					}
				}

				if (s.xvel != 0) {
					x = engine.getangle(sprite[s.owner].x - s.x, sprite[s.owner].y - s.y);
					q = getincangle(s.ang, x) >> 3;

					t[2] += q;
					s.ang += q;

					if (s.xvel == sc.extra) {
						if (Sound[hittype[i].lastvx].num == 0)
							spritesound(hittype[i].lastvx, i);
						if (!ud.monsters_off && sc.floorpal == 0 && (sc.floorstat & 1) != 0 && rnd(8)) {
							p = (short) findplayer(s);
							x = player_dist;
							if (x < 20480) {
								j = s.ang;
								s.ang = engine.getangle(s.x - ps[p].posx, s.y - ps[p].posy);
								shoot(i, CROSSBOW);
								s.ang = (short) j;
							}
						}
					}

					if (s.xvel <= 64 && (sc.floorstat & 1) == 0 && (sc.ceilingstat & 1) == 0)
						stopsound(hittype[i].lastvx, i);

					if ((sc.floorz - sc.ceilingz) < (108 << 8)) {
						if (!ud.clipping && s.xvel >= 192)
							for (p = connecthead; p >= 0; p = connectpoint2[p])
								if (sprite[ps[p].i].extra > 0) {
									k = ps[p].cursectnum;
									k = engine.updatesector(ps[p].posx, ps[p].posy, k);
									if ((k == -1 && !ud.clipping)
											|| (k == s.sectnum && ps[p].cursectnum != s.sectnum)) {
										ps[p].posx = s.x;
										ps[p].posy = s.y;
										ps[p].cursectnum = s.sectnum;

										engine.setsprite(ps[p].i, s.x, s.y, s.z);
										quickkill(ps[p]);
									}
								}
					}

					m = (s.xvel * sintable[(s.ang + 512) & 2047]) >> 14;
					x = (s.xvel * sintable[s.ang & 2047]) >> 14;

					for (p = connecthead; p >= 0; p = connectpoint2[p])
						if (ps[p].cursectnum != -1 && sector[ps[p].cursectnum].lotag != 2) { // v0.751
							if (po[p].os == s.sectnum) {
								po[p].ox += m;
								po[p].oy += x;
							}

							if (s.sectnum == sprite[ps[p].i].sectnum) {
								Point out = engine.rotatepoint(s.x, s.y, ps[p].posx, ps[p].posy, (short) q);
								ps[p].posx = out.getX();
								ps[p].posy = out.getY();

								ps[p].posx += m;
								ps[p].posy += x;

								ps[p].bobposx += m;
								ps[p].bobposy += x;

								ps[p].ang += q;
								ps[p].ang = BClampAngle(ps[p].ang);

								if (numplayers > 1) {
									ps[p].oposx = ps[p].posx;
									ps[p].oposy = ps[p].posy;
								}
								if (sprite[ps[p].i].extra <= 0) {
									sprite[ps[p].i].x = ps[p].posx;
									sprite[ps[p].i].y = ps[p].posy;
								}
							}
						}
					j = headspritesect[s.sectnum];
					while (j >= 0) {
						if (sprite[j].statnum != 10 && sector[sprite[j].sectnum].lotag != 2
								&& sprite[j].picnum != SECTOREFFECTOR && sprite[j].picnum != LOCATORS) {
							Point out = engine.rotatepoint(s.x, s.y, sprite[j].x, sprite[j].y, (short) q);

							game.pInt.setsprinterpolate(j, sprite[j]);

							sprite[j].x = out.getX();
							sprite[j].y = out.getY();

							sprite[j].x += m;
							sprite[j].y += x;

							sprite[j].ang += q;

//							if (numplayers > 1) {
//								hittype[j].bposx = sprite[j].x;
//								hittype[j].bposy = sprite[j].y;
//							}
						}
						j = nextspritesect[j];
					}

					ms(i);
					engine.setsprite(i, s.x, s.y, s.z);

					if ((sc.floorz - sc.ceilingz) < (108 << 8)) {
						if (!ud.clipping && s.xvel >= 192)
							for (p = connecthead; p >= 0; p = connectpoint2[p])
								if (sprite[ps[p].i].extra > 0) {
									k = ps[p].cursectnum;
									k = engine.updatesector(ps[p].posx, ps[p].posy, k);
									if ((k == -1 && !ud.clipping)
											|| (k == s.sectnum && ps[p].cursectnum != s.sectnum)) {
										ps[p].oposx = ps[p].posx = s.x;
										ps[p].oposy = ps[p].posy = s.y;
										ps[p].cursectnum = s.sectnum;

										engine.setsprite(ps[p].i, s.x, s.y, s.z);
										quickkill(ps[p]);
									}
								}

						jj = headspritesect[sprite[sprite[i].owner].sectnum];
						while (jj >= 0) {
							ll = nextspritesect[jj];
							if (sprite[jj].statnum == 1 && badguy(sprite[jj]) && sprite[jj].picnum != SECTOREFFECTOR
									&& sprite[jj].picnum != LOCATORS) {
								k = sprite[jj].sectnum;
								k = engine.updatesector(sprite[jj].x, sprite[jj].y, k);
								if (sprite[jj].extra >= 0 && k == s.sectnum) {
									gutsdir(sprite[jj], JIBS6, 72, myconnectindex);
									spritesound(SQUISHED, i);
									engine.deletesprite(jj);
								}
							}
							jj = ll;
						}
					}
				}

				break;
			case 30:
				if (s.owner == -1) {
					t[3] ^= 1;
					s.owner = (short) LocateTheLocator(t[3], t[0]);
				} else {

					if (t[4] == 1) // Starting to go
					{
						if (ldist(sprite[s.owner], s) < (2048 - 128))
							t[4] = 2;
						else {
							if (s.xvel == 0)
								operateactivators(s.hitag + (t[3] == 0 ? 1 : 0), -1);
							if (s.xvel < 256)
								s.xvel += 16;
						}
					}
					if (t[4] == 2) {
						l = FindDistance2D(sprite[s.owner].x - s.x, sprite[s.owner].y - s.y);

						if (l <= 128)
							s.xvel = 0;

						if (s.xvel > 0)
							s.xvel -= 16;
						else {
							s.xvel = 0;
							operateactivators(s.hitag + (short) t[3], -1);
							s.owner = -1;
							s.ang += 1024;
							t[4] = 0;
							operateforcefields(i, s.hitag);

							j = headspritesect[s.sectnum];
							while (j >= 0) {
								if (sprite[j].picnum != SECTOREFFECTOR && sprite[j].picnum != LOCATORS) {
									game.pInt.setsprinterpolate(j, sprite[j]);
								}
								j = nextspritesect[j];
							}

						}
					}
				}

				if (s.xvel != 0) {
					l = (s.xvel * sintable[(s.ang + 512) & 2047]) >> 14;
					x = (s.xvel * sintable[s.ang & 2047]) >> 14;

					if ((sc.floorz - sc.ceilingz) < (108 << 8))
						if (!ud.clipping)
							for (p = connecthead; p >= 0; p = connectpoint2[p])
								if (sprite[ps[p].i].extra > 0) {
									k = ps[p].cursectnum;
									k = engine.updatesector(ps[p].posx, ps[p].posy, k);
									if ((k == -1 && !ud.clipping)
											|| (k == s.sectnum && ps[p].cursectnum != s.sectnum)) {
										ps[p].posx = s.x;
										ps[p].posy = s.y;
										ps[p].cursectnum = s.sectnum;

										engine.setsprite(ps[p].i, s.x, s.y, s.z);
										quickkill(ps[p]);
									}
								}

					for (p = connecthead; p >= 0; p = connectpoint2[p]) {
						if (sprite[ps[p].i].sectnum == s.sectnum) {
							ps[p].posx += l;
							ps[p].posy += x;

							if (numplayers > 1) {
								ps[p].oposx = ps[p].posx;
								ps[p].oposy = ps[p].posy;
							}

							ps[p].bobposx += l;
							ps[p].bobposy += x;
						}

						if (po[p].os == s.sectnum) {
							po[p].ox += l;
							po[p].oy += x;
						}
					}

					j = headspritesect[s.sectnum];
					while (j >= 0) {
						if (sprite[j].picnum != SECTOREFFECTOR && sprite[j].picnum != LOCATORS) {

							game.pInt.setsprinterpolate(j, sprite[j]);

							sprite[j].x += l;
							sprite[j].y += x;

//							if (numplayers > 1) {
//								hittype[j].bposx = sprite[j].x;
//								hittype[j].bposy = sprite[j].y;
//							}
						}
						j = nextspritesect[j];
					}

					ms(i);
					engine.setsprite(i, s.x, s.y, s.z);

					if ((sc.floorz - sc.ceilingz) < (108 << 8)) {
						if (!ud.clipping)
							for (p = connecthead; p >= 0; p = connectpoint2[p])
								if (sprite[ps[p].i].extra > 0) {
									k = ps[p].cursectnum;
									k = engine.updatesector(ps[p].posx, ps[p].posy, k);
									if ((k == -1 && !ud.clipping)
											|| (k == s.sectnum && ps[p].cursectnum != s.sectnum)) {
										ps[p].posx = s.x;
										ps[p].posy = s.y;

										ps[p].oposx = ps[p].posx;
										ps[p].oposy = ps[p].posy;

										ps[p].cursectnum = s.sectnum;

										engine.setsprite(ps[p].i, s.x, s.y, s.z);
										quickkill(ps[p]);
									}
								}

						jj = headspritesect[sprite[sprite[i].owner].sectnum];
						while (jj >= 0) {
							ll = nextspritesect[jj];
							if (sprite[jj].statnum == 1 && badguy(sprite[jj]) && sprite[jj].picnum != SECTOREFFECTOR
									&& sprite[jj].picnum != LOCATORS) {
								// if(sprite[j].sectnum != s.sectnum)
								{
									k = sprite[jj].sectnum;
									k = engine.updatesector(sprite[jj].x, sprite[jj].y, k);
									if (sprite[jj].extra >= 0 && k == s.sectnum) {
										gutsdir(sprite[jj], JIBS6, 24, myconnectindex);
										spritesound(SQUISHED, jj);
										engine.deletesprite(jj);
									}
								}

							}
							jj = ll;
						}
					}
				}

				break;

			case 2:// Quakes
				if (t[4] > 0 && t[0] == 0) {
					if (t[4] < sh)
						t[4]++;
					else
						t[0] = 1;
				}

				if (t[0] > 0) {
					t[0]++;

					s.xvel = 3;

					if (t[0] > 96) {
						t[0] = -1; // Stop the quake
						t[4] = -1;
						engine.deletesprite(i);
						continue;
					} else {
						if ((t[0] & 31) == 8) {
							earthquaketime = 48;
							spritesound(EARTHQUAKE, ps[screenpeek].i);
						}

						if (klabs(sc.floorheinum - t[5]) < 8)
							sc.floorheinum = (short) t[5];
						else
							sc.floorheinum += (sgn(t[5] - sc.floorheinum) << 4);
					}

					m = (s.xvel * sintable[(s.ang + 512) & 2047]) >> 14;
					x = (s.xvel * sintable[s.ang & 2047]) >> 14;

					for (p = connecthead; p >= 0; p = connectpoint2[p])
						if (ps[p].cursectnum == s.sectnum && ps[p].on_ground) {
							ps[p].posx += m;
							ps[p].posy += x;

							ps[p].bobposx += m;
							ps[p].bobposy += x;
						}

					jj = headspritesect[s.sectnum];
					while (jj >= 0) {
						nextj = nextspritesect[jj];

						if (sprite[jj].picnum != SECTOREFFECTOR) {
							game.pInt.setsprinterpolate(jj, sprite[jj]);

							sprite[jj].x += m;
							sprite[jj].y += x;
							engine.setsprite(jj, sprite[jj].x, sprite[jj].y, sprite[jj].z);
						}
						jj = nextj;
					}
					ms(i);
					engine.setsprite(i, s.x, s.y, s.z);
				}
				break;

			// Flashing sector lights after reactor EXPLOSION2

			case 3:
				if (t[4] == 0)
					break;
				p = (short) findplayer(s);
				x = player_dist;

				if ((global_random / (sh + 1) & 31) < 4 && t[2] == 0) {
					sc.ceilingpal = (short) (s.owner >> 8);
					sc.floorpal = (short) (s.owner & 0xff);
					t[0] = s.shade + (global_random & 15);
				} else {
					sc.ceilingpal = s.pal;
					sc.floorpal = s.pal;
					t[0] = t[3];
				}

				sc.ceilingshade = (byte) t[0];
				sc.floorshade = (byte) t[0];

				startwall = sc.wallptr;
				endwall = startwall + sc.wallnum;

				for (x = startwall; x < endwall; x++) {
					WALL wal = wall[x];
					if (wal.hitag != 1) {
						wal.shade = (byte) t[0];
						if ((wal.cstat & 2) != 0 && wal.nextwall >= 0) {
							wall[wal.nextwall].shade = wal.shade;
						}
					}
				}

				break;
			case 4:
				if ((global_random / (sh + 1) & 31) < 4) {
					t[1] = s.shade + (global_random & 15);// Got really bright
					t[0] = s.shade + (global_random & 15);
					sc.ceilingpal = (short) (s.owner >> 8);
					sc.floorpal = (short) (s.owner & 0xff);
					j = 1;
				} else {
					t[1] = t[2];
					t[0] = t[3];

					sc.ceilingpal = s.pal;
					sc.floorpal = s.pal;

					j = 0;
				}

				sc.floorshade = (byte) t[1];
				sc.ceilingshade = (byte) t[1];

				startwall = sc.wallptr;
				endwall = startwall + sc.wallnum;

				for (x = startwall; x < endwall; x++) {
					WALL wal = wall[x];
					if (j != 0)
						wal.pal = (short) (s.owner & 0xff);
					else
						wal.pal = s.pal;

					if (wal.hitag != 1) {
						wal.shade = (byte) t[0];
						if ((wal.cstat & 2) != 0 && wal.nextwall >= 0)
							wall[wal.nextwall].shade = wal.shade;
					}
				}

				j = headspritesect[sprite[i].sectnum];
				while (j >= 0) {
					if ((sprite[j].cstat & 16) != 0) {
						if ((sc.ceilingstat & 1) != 0)
							sprite[j].shade = sc.ceilingshade;
						else
							sprite[j].shade = sc.floorshade;
					}

					j = nextspritesect[j];
				}

				if (t[4] != 0) {
					engine.deletesprite(i);
					continue;
				}

				break;
			// BOSS
			case 5:
				p = (short) findplayer(s);
				x = player_dist;
				if (x < 8192) {
					j = s.ang;
					s.ang = engine.getangle(s.x - ps[p].posx, s.y - ps[p].posy);
					shoot(i, FIRELASER);
					s.ang = (short) j;
				}

				if (s.owner == -1) // Start search
				{
					t[4] = 0;
					l = 0x7fffffff;
					while (true) // Find the shortest dist
					{
						s.owner = (short) LocateTheLocator((short) t[4], -1); // t[0]
																				// hold
																				// sectnum

						if (s.owner == -1)
							break;

						m = ldist(sprite[ps[p].i], sprite[s.owner]);

						if (l > m) {
							q = s.owner;
							l = m;
						}

						t[4]++;
					}

					s.owner = (short) q;
					s.zvel = (short) (ksgn(sprite[q].z - s.z) << 4);
				}

				if (ldist(sprite[s.owner], s) < 1024) {
					short ta;
					ta = s.ang;
					s.ang = engine.getangle(ps[p].posx - s.x, ps[p].posy - s.y);
					s.ang = ta;
					s.owner = -1;
					continue;

				} else
					s.xvel = 256;

				x = engine.getangle(sprite[s.owner].x - s.x, sprite[s.owner].y - s.y);
				q = getincangle(s.ang, x) >> 3;
				s.ang += q;

				if (rnd(32)) {
					t[2] += q;
					sc.ceilingshade = 127;
				} else {
					t[2] += getincangle(t[2] + 512, engine.getangle(ps[p].posx - s.x, ps[p].posy - s.y)) >> 2;
					sc.ceilingshade = 0;
				}

				j = ifhitbyweapon(i);
				if (j >= 0) {
					t[3]++;
					if (t[3] == 5) {
						s.zvel += 1024;
						FTA(7, ps[myconnectindex]);
					}
				}

				s.z += s.zvel;
				game.pInt.setceilinterpolate(s.sectnum, sc);
				sc.ceilingz += s.zvel;
				game.pInt.setceilinterpolate(t[0], sector[t[0]]);
				sector[t[0]].ceilingz += s.zvel;
				ms(i);
				engine.setsprite(i, s.x, s.y, s.z);
				break;

			case 8:
			case 9:

				// work only if its moving

				j = -1;

				if (hittype[i].temp_data[4] != 0) {
					hittype[i].temp_data[4]++;
					if (hittype[i].temp_data[4] > 8) {
						engine.deletesprite(i);
						continue;
					}
					j = 1;
				} else
					j = getanimationgoal(sc, CEILZ);

				if (j >= 0) {
					short sn;

					if ((sc.lotag & 0x8000) != 0 || hittype[i].temp_data[4] != 0)
						x = -t[3];
					else
						x = t[3];

					if (st == 9)
						x = -x;

					j = headspritestat[3];
					while (j >= 0) {
						if (((sprite[j].lotag) == st) && (sprite[j].hitag) == sh) {
							sn = sprite[j].sectnum;
							m = sprite[j].shade;

							startwall = sector[sn].wallptr;
							endwall = startwall + sector[sn].wallnum;

							for (l = startwall; l < endwall; l++) {
								WALL wal = wall[l];
								if (wal.hitag != 1) {
									wal.shade += x;

									if (wal.shade < m)
										wal.shade = (byte) m;
									else if (wal.shade > hittype[j].temp_data[2])
										wal.shade = (byte) hittype[j].temp_data[2];

									if (wal.nextwall >= 0)
										if (wall[wal.nextwall].hitag != 1)
											wall[wal.nextwall].shade = wal.shade;
								}
							}

							sector[sn].floorshade += x;
							sector[sn].ceilingshade += x;

							if (sector[sn].floorshade < m)
								sector[sn].floorshade = (byte) m;
							else if (sector[sn].floorshade > hittype[j].temp_data[0])
								sector[sn].floorshade = (byte) hittype[j].temp_data[0];

							if (sector[sn].ceilingshade < m)
								sector[sn].ceilingshade = (byte) m;
							else if (sector[sn].ceilingshade > hittype[j].temp_data[1])
								sector[sn].ceilingshade = (byte) hittype[j].temp_data[1];

							if (sector[sn].hitag == 1)
								sector[sn].ceilingshade = (byte) hittype[j].temp_data[1];
						}
						j = nextspritestat[j];
					}
				}
				break;

			case 10:

				if ((sc.lotag & 0xff) == 27 || (sc.floorz > sc.ceilingz && (sc.lotag & 0xff) != 23)
						|| sc.lotag == (short) 32791) {
					j = 1;

					if ((sc.lotag & 0xff) != 27)
						for (p = connecthead; p >= 0; p = connectpoint2[p])
							if (sc.lotag != 30 && sc.lotag != 31 && sc.lotag != 0)
								if (s.sectnum == sprite[ps[p].i].sectnum)
									j = 0;

					if (j == 1) {
						if (t[0] > sh)
							switch (sector[s.sectnum].lotag) {
							case 20:
							case 21:
							case 22:
							case 26:
								if (currentGame.getCON().type != RRRA) { // GDX 3.10.2018
									if (getanimationgoal(sector[s.sectnum], CEILZ) >= 0)
										break;
								}
							default:
								activatebysector(s.sectnum, i);
								t[0] = 0;
								break;
							}
						else
							t[0]++;
					}
				} else
					t[0] = 0;
				break;

			case 11: // Swingdoor

				if (t[5] > 0) {
					t[5]--;
					break;
				}

				if (t[4] != 0) {
					startwall = sc.wallptr;
					endwall = startwall + sc.wallnum;

					for (j = startwall; j < endwall; j++) {
						k = headspritestat[1];
						while (k >= 0) {
							if (sprite[k].extra > 0 && badguy(sprite[k])
									&& engine.clipinsidebox(sprite[k].x, sprite[k].y, (short) j, 256) == 1)
								continue BOLT;
							k = nextspritestat[k];
						}

						k = headspritestat[10];
						while (k >= 0) {
							if (sprite[k].owner >= 0
									&& engine.clipinsidebox(sprite[k].x, sprite[k].y, (short) j, 144) == 1) {
								t[5] = 8; // Delay
								k = (short) ((sprite[i].yvel >> 3) * t[3]);
								t[2] -= k;
								t[4] -= k;
								ms(i);
								engine.setsprite(i, s.x, s.y, s.z);
								continue BOLT;
							}
							k = nextspritestat[k];
						}
					}

					k = (short) ((sprite[i].yvel >> 3) * t[3]);
					t[2] += k;
					t[4] += k;
					ms(i);
					engine.setsprite(i, s.x, s.y, s.z);

					if (t[4] <= -511 || t[4] >= 512) {
						t[4] = 0;
						t[2] &= 0xffffff00;
						ms(i);
						engine.setsprite(i, s.x, s.y, s.z);
						break;
					}
				}
				break;
			case 12:
			case 47:
			case 48:
				if (t[0] == 3 || t[3] == 1) // Lights going off
				{
					sc.floorpal = 0;
					sc.ceilingpal = 0;

					startwall = sc.wallptr;
					endwall = startwall + sc.wallnum;

					for (j = startwall; j < endwall; j++) {
						WALL wal = wall[j];
						if (wal.hitag != 1) {
							wal.shade = (byte) t[1];
							wal.pal = 0;
						}
					}

					sc.floorshade = (byte) t[1];
					sc.ceilingshade = (byte) t[2];
					t[0] = 0;

					j = headspritesect[sprite[i].sectnum];
					while (j >= 0) {
						if ((sprite[j].cstat & 16) != 0) {
							if ((sc.ceilingstat & 1) != 0)
								sprite[j].shade = sc.ceilingshade;
							else
								sprite[j].shade = sc.floorshade;
						}
						j = nextspritesect[j];

					}

					if (t[3] == 1) {
						engine.deletesprite(i);
						continue;
					}
				}

				if (t[0] == 1) // Lights flickering on
				{
					if (st != 48) {
						if (sc.floorshade > s.shade) {
							sc.floorpal = s.pal;
							sc.floorshade -= 2;
							if (st != 47) {
								sc.ceilingpal = s.pal;
								sc.ceilingshade -= 2;
							}

							startwall = sc.wallptr;
							endwall = startwall + sc.wallnum;

							for (j = startwall; j < endwall; j++) {
								WALL wal = wall[j];
								if (wal.hitag != 1) {
									wal.pal = s.pal;
									wal.shade -= 2;
								}
							}
						} else
							t[0] = 2;
					} else {
						if (sc.ceilingshade > s.shade) {
							sc.ceilingpal = s.pal;
							sc.ceilingshade -= 2;

							startwall = sc.wallptr;
							endwall = startwall + sc.wallnum;

							for (j = startwall; j < endwall; j++) {
								WALL wal = wall[j];
								if (wal.hitag != 1) {
									wal.pal = s.pal;
									wal.shade -= 2;
								}
							}
						} else
							t[0] = 2;
					}

					j = headspritesect[sprite[i].sectnum];
					while (j >= 0) {
						if ((sprite[j].cstat & 16) != 0) {
							if ((sc.ceilingstat & 1) != 0)
								sprite[j].shade = sc.ceilingshade;
							else
								sprite[j].shade = sc.floorshade;
						}
						j = nextspritesect[j];
					}
				}
				break;

			case 13:
				if (t[2] != 0) {
					j = (sprite[i].yvel << 5) | 1;

					if (s.ang == 512) {
						if (s.owner != 0) {
							game.pInt.setceilinterpolate(s.sectnum, sc);
							if (klabs(t[0] - sc.ceilingz) >= j)
								sc.ceilingz += sgn(t[0] - sc.ceilingz) * j;
							else
								sc.ceilingz = t[0];
						} else {
							game.pInt.setfloorinterpolate(s.sectnum, sc);
							if (klabs(t[1] - sc.floorz) >= j)
								sc.floorz += sgn(t[1] - sc.floorz) * j;
							else
								sc.floorz = t[1];
						}
					} else {
						game.pInt.setfloorinterpolate(s.sectnum, sc);
						game.pInt.setceilinterpolate(s.sectnum, sc);
						if (klabs(t[1] - sc.floorz) >= j)
							sc.floorz += sgn(t[1] - sc.floorz) * j;
						else
							sc.floorz = t[1];
						if (klabs(t[0] - sc.ceilingz) >= j)
							sc.ceilingz += sgn(t[0] - sc.ceilingz) * j;
						sc.ceilingz = t[0];
					}

					if (t[3] == 1) {
						// Change the shades

						t[3]++;
						sc.ceilingstat ^= 1;

						if (s.ang == 512) {
							startwall = sc.wallptr;
							endwall = startwall + sc.wallnum;

							for (j = startwall; j < endwall; j++) {
								WALL wal = wall[j];
								wal.shade = s.shade;
							}

							sc.floorshade = s.shade;

							if (ps[connecthead].one_parallax_sectnum >= 0) {
								sc.ceilingpicnum = sector[ps[connecthead].one_parallax_sectnum].ceilingpicnum;
								sc.ceilingshade = sector[ps[connecthead].one_parallax_sectnum].ceilingshade;
							}
						}
					}
					t[2]++;
					if (t[2] > 256) {
						engine.deletesprite(i);
						continue;
					}
				}

				if (t[2] == 4 && s.ang != 512)
					for (x = 0; x < 7; x++)
						RANDOMSCRAP(s, i);
				break;

			case 15:

				if (t[4] != 0) {
					s.xvel = 16;

					if (t[4] == 1) // Opening
					{
						if (t[3] >= (sprite[i].yvel >> 3)) {
							t[4] = 0; // Turn off the sliders
							callsound(s.sectnum, i);
							break;
						}
						t[3]++;
					} else if (t[4] == 2) {
						if (t[3] < 1) {
							t[4] = 0;
							callsound(s.sectnum, i);
							break;
						}
						t[3]--;
					}

					ms(i);
					engine.setsprite(i, s.x, s.y, s.z);
				}
				break;

			case 16: // Reactor

				t[2] += 32;
				if (sc.floorz < sc.ceilingz)
					s.shade = 0;

				else if (sc.ceilingz < t[3]) {

					// The following code check to see if
					// there is any other sprites in the sector.
					// If there isn't, then kill this sectoreffector
					// itself.....

					j = headspritesect[s.sectnum];
					while (j >= 0) {
						if (sprite[j].picnum == REACTOR || sprite[j].picnum == REACTOR2)
							break;
						j = nextspritesect[j];
					}
					if (j == -1) {
						engine.deletesprite(i);
						continue;
					} else
						s.shade = 1;
				}

				game.pInt.setceilinterpolate(s.sectnum, sc);
				if (s.shade != 0)
					sc.ceilingz += 1024;
				else
					sc.ceilingz -= 512;

				ms(i);
				engine.setsprite(i, s.x, s.y, s.z);

				break;

			case 17:

				q = t[0] * (sprite[i].yvel << 2);

				game.pInt.setfloorinterpolate(s.sectnum, sc);
				game.pInt.setceilinterpolate(s.sectnum, sc);
				sc.ceilingz += q;
				sc.floorz += q;

				j = headspritesect[s.sectnum];
				while (j >= 0) {
					if (sprite[j].statnum == 10 && sprite[j].owner >= 0) {
						p = sprite[j].yvel;

						ps[p].posz += q;
						ps[p].truefz += q;
						ps[p].truecz += q;
						if (numplayers > 1)
							ps[p].oposz = ps[p].posz;
					}
					if (sprite[j].statnum != 3) {
						game.pInt.setsprinterpolate(j, sprite[j]);
						sprite[j].z += q;
					}

					hittype[j].floorz = sc.floorz;
					hittype[j].ceilingz = sc.ceilingz;

					j = nextspritesect[j];
				}

				if (t[0] != 0) // If in motion
				{
					if (klabs(sc.floorz - t[2]) <= sprite[i].yvel) {
						activatewarpelevators(i, 0);
						break;
					}

					if (t[0] == -1) {
						if (sc.floorz > t[3])
							break;
					} else if (sc.ceilingz < t[4])
						break;

					if (t[1] == 0)
						break;
					t[1] = 0;

					j = headspritestat[3];
					while (j >= 0) {
						if (i != j && (sprite[j].lotag) == 17)
							if ((sc.hitag - t[0]) == (sector[sprite[j].sectnum].hitag) && sh == (sprite[j].hitag))
								break;
						j = nextspritestat[j];
					}

					if (j == -1)
						break;

					k = headspritesect[s.sectnum];
					while (k >= 0) {
						nextk = nextspritesect[k];

						if (sprite[k].statnum == 10 && sprite[k].owner >= 0) {
							p = sprite[k].yvel;

							ps[p].posx += sprite[j].x - s.x;
							ps[p].posy += sprite[j].y - s.y;
							ps[p].posz = sector[sprite[j].sectnum].floorz - (sc.floorz - ps[p].posz);

							hittype[k].floorz = sector[sprite[j].sectnum].floorz;
							hittype[k].ceilingz = sector[sprite[j].sectnum].ceilingz;

							ps[p].bobposx = ps[p].oposx = ps[p].posx;
							ps[p].bobposy = ps[p].oposy = ps[p].posy;
							ps[p].oposz = ps[p].posz;

							ps[p].truefz = hittype[k].floorz;
							ps[p].truecz = hittype[k].ceilingz;
							ps[p].bobcounter = 0;

							game.pInt.setsprinterpolate(ps[p].i, sprite[ps[p].i]);
							engine.changespritesect(k, sprite[j].sectnum);
							ps[p].cursectnum = sprite[j].sectnum;

							ps[p].UpdatePlayerLoc();
						} else if (sprite[k].statnum != 3) {
							game.pInt.setsprinterpolate(k, sprite[k]);

							sprite[k].x += sprite[j].x - s.x;
							sprite[k].y += sprite[j].y - s.y;
							sprite[k].z = sector[sprite[j].sectnum].floorz - (sc.floorz - sprite[k].z);

							engine.changespritesect(k, sprite[j].sectnum);
							engine.setsprite(k, sprite[k].x, sprite[k].y, sprite[k].z);

							hittype[k].floorz = sector[sprite[j].sectnum].floorz;
							hittype[k].ceilingz = sector[sprite[j].sectnum].ceilingz;

						}
						k = nextk;
					}
				}
				break;

			case 18:
				if (t[0] != 0) {
					if (s.pal != 0) {
						if (s.ang == 512) {
							game.pInt.setceilinterpolate(s.sectnum, sc);
							sc.ceilingz -= sc.extra;
							if (sc.ceilingz <= t[1]) {
								sc.ceilingz = t[1];
								engine.deletesprite(i);
								continue;
							}
						} else {
							game.pInt.setfloorinterpolate(s.sectnum, sc);
							sc.floorz += sc.extra;
							j = headspritesect[s.sectnum];
							while (j >= 0) {
								if (sprite[j].picnum == APLAYER && sprite[j].owner >= 0)
									if (ps[sprite[j].yvel].on_ground)
										ps[sprite[j].yvel].posz += sc.extra;
								if (sprite[j].zvel == 0 && sprite[j].statnum != 3 && sprite[j].statnum != 4) {
									game.pInt.setsprinterpolate(j, sprite[j]);
									sprite[j].z += sc.extra;
									hittype[j].floorz = sc.floorz;
								}
								j = nextspritesect[j];
							}
							if (sc.floorz >= t[1]) {
								sc.floorz = t[1];
								engine.deletesprite(i);
								continue;
							}
						}
					} else {
						if (s.ang == 512) {
							game.pInt.setceilinterpolate(s.sectnum, sc);
							sc.ceilingz += sc.extra;
							if (sc.ceilingz >= s.z) {
								sc.ceilingz = s.z;
								engine.deletesprite(i);
								continue;
							}
						} else {
							game.pInt.setfloorinterpolate(s.sectnum, sc);
							sc.floorz -= sc.extra;
							j = headspritesect[s.sectnum];
							while (j >= 0) {
								if (sprite[j].picnum == APLAYER && sprite[j].owner >= 0)
									if (ps[sprite[j].yvel].on_ground)
										ps[sprite[j].yvel].posz -= sc.extra;
								if (sprite[j].zvel == 0 && sprite[j].statnum != 3 && sprite[j].statnum != 4) {
									game.pInt.setsprinterpolate(j, sprite[j]);
									sprite[j].z -= sc.extra;
									hittype[j].floorz = sc.floorz;
								}
								j = nextspritesect[j];
							}
							if (sc.floorz <= s.z) {
								sc.floorz = s.z;
								engine.deletesprite(i);
								continue;
							}
						}
					}

					t[2]++;
					if (t[2] >= s.hitag) {
						t[2] = 0;
						t[0] = 0;
					}
				}
				break;

			case 19: // Battlestar galactia shields

				if (t[0] != 0) {
					if (t[0] == 1) {
						t[0] = 2;
						x = sc.wallptr;
						q = x + sc.wallnum;
						for (j = x; j < q; j++)
							if (wall[j].overpicnum == BIGFORCE) {
								wall[j].cstat &= (128 + 32 + 8 + 4 + 2);
								wall[j].overpicnum = 0;
								if (wall[j].nextwall >= 0) {
									wall[wall[j].nextwall].overpicnum = 0;
									wall[wall[j].nextwall].cstat &= (128 + 32 + 8 + 4 + 2);
								}
							}
					}

					game.pInt.setceilinterpolate(s.sectnum, sc);
					if (sc.ceilingz < sc.floorz)
						sc.ceilingz += sprite[i].yvel;
					else {
						sc.ceilingz = sc.floorz;

						j = headspritestat[3];
						while (j >= 0) {
							if (sprite[j].lotag == 0 && sprite[j].hitag == sh) {
								q = sprite[sprite[j].owner].sectnum;
								sector[sprite[j].sectnum].floorpal = sector[sprite[j].sectnum].ceilingpal = sector[q].floorpal;
								sector[sprite[j].sectnum].floorshade = sector[sprite[j].sectnum].ceilingshade = sector[q].floorshade;

								hittype[sprite[j].owner].temp_data[0] = 2;
							}
							j = nextspritestat[j];
						}
						engine.deletesprite(i);
						continue;
					}
				} else // Not hit yet
				{
					j = ifhitsectors(s.sectnum);
					if (j >= 0) {
						FTA(8, ps[myconnectindex]);

						l = headspritestat[3];
						while (l >= 0) {
							x = sprite[l].lotag & 0x7fff;
							switch (x) {
							case 0:
								if (sprite[l].hitag == sh) {
									q = sprite[l].sectnum;
									sector[q].floorshade = sector[q].ceilingshade = sprite[sprite[l].owner].shade;
									sector[q].floorpal = sector[q].ceilingpal = sprite[sprite[l].owner].pal;
								}
								break;

							case 1:
							case 12:
							case 19:

								if (sh == sprite[l].hitag)
									if (hittype[l].temp_data[0] == 0) {
										hittype[l].temp_data[0] = 1; // Shut
																		// them
																		// all
																		// on
										sprite[l].owner = i;
									}

								break;
							}
							l = nextspritestat[l];
						}
					}
				}

				break;

			case 20: // Extend-o-bridge

				if (t[0] == 0)
					break;
				if (t[0] == 1)
					s.xvel = 8;
				else
					s.xvel = -8;

				if (s.xvel != 0) // Moving
				{
					x = (s.xvel * sintable[(s.ang + 512) & 2047]) >> 14;
					l = (s.xvel * sintable[s.ang & 2047]) >> 14;

					t[3] += s.xvel;

					s.x += x;
					s.y += l;

					if (t[3] <= 0 || (t[3] >> 6) >= (sprite[i].yvel >> 6)) {
						s.x -= x;
						s.y -= l;
						t[0] = 0;
						callsound(s.sectnum, i);
						break;
					}

					jj = headspritesect[s.sectnum];
					while (jj >= 0) {
						nextj = nextspritesect[jj];

						if (sprite[jj].statnum != 3 && sprite[jj].zvel == 0) {
							game.pInt.setsprinterpolate(jj, sprite[jj]);

							sprite[jj].x += x;
							sprite[jj].y += l;
							engine.setsprite(jj, sprite[jj].x, sprite[jj].y, sprite[jj].z);
							if ((sector[sprite[jj].sectnum].floorstat & 2) != 0)
								if (sprite[jj].statnum == 2)
									makeitfall(currentGame.getCON(), jj);
						}
						jj = nextj;
					}

					engine.dragpoint((short) t[1], wall[t[1]].x + x, wall[t[1]].y + l);
					engine.dragpoint((short) t[2], wall[t[2]].x + x, wall[t[2]].y + l);

					for (p = connecthead; p >= 0; p = connectpoint2[p])
						if (ps[p].cursectnum == s.sectnum && ps[p].on_ground) {
							ps[p].posx += x;
							ps[p].posy += l;

//							ps[p].oposx = ps[p].posx;
//							ps[p].oposy = ps[p].posy;

							engine.setsprite(ps[p].i, ps[p].posx, ps[p].posy, ps[p].posz + PHEIGHT);
						}

					sc.floorxpanning -= x >> 3;
					sc.floorypanning -= l >> 3;

					sc.ceilingxpanning -= x >> 3;
					sc.ceilingypanning -= l >> 3;
				}

				break;

			case 21: // Cascading effect

				if (t[0] == 0)
					break;

				if (s.ang == 1536)
					l = sc.ceilingz;
				else
					l = sc.floorz;

				if (t[0] == 1) // Decide if the s.sectnum should go up or down
				{
					s.zvel = (short) (ksgn(s.z - l) * (sprite[i].yvel << 4));
					t[0]++;
				}

				if (sc.extra == 0) {
					l += s.zvel;
					if (s.ang == 1536)
						sc.ceilingz = l;
					else
						sc.floorz = l;

					if (klabs(l - s.z) < 1024) {
						l = s.z;
						if (s.ang == 1536) {
							game.pInt.setceilinterpolate(s.sectnum, sc);
							sc.ceilingz = l;
						} else {
							game.pInt.setfloorinterpolate(s.sectnum, sc);
							sc.floorz = l;
						}
						engine.deletesprite(i);
						continue;
					}
				} else
					sc.extra--;
				break;

			case 22:

				if (t[1] != 0) {
					if (getanimationgoal(sector[t[0]], CEILZ) >= 0) {
						game.pInt.setceilinterpolate(s.sectnum, sc);
						sc.ceilingz += sc.extra * 9;
					} else
						t[1] = 0;
				}
				break;

			case 24:
			case 34:

				if (t[4] != 0)
					break;

				x = (sprite[i].yvel * sintable[(s.ang + 512) & 2047]) >> 18;
				l = (sprite[i].yvel * sintable[s.ang & 2047]) >> 18;

				k = 0;

				jj = headspritesect[s.sectnum];
				while (jj >= 0) {
					nextj = nextspritesect[jj];
					if (sprite[jj].zvel >= 0)
						switch (sprite[jj].statnum) {
						case 5:
							switch (sprite[jj].picnum) {
							case BLOODPOOL:
							case FOOTPRINTS:
							case FOOTPRINTS2:
							case FOOTPRINTS3:
							case FOOTPRINTS4:
							case BULLETHOLE:
							case BLOODSPLAT1:
							case BLOODSPLAT2:
							case BLOODSPLAT3:
							case BLOODSPLAT4:
								sprite[jj].xrepeat = sprite[jj].yrepeat = 0;
								jj = nextj;
								continue;
							}
						case 6:
						case 1:
						case 0:
							if (sprite[jj].picnum == BOLT1 || sprite[jj].picnum == BOLT1 + 1
									|| sprite[jj].picnum == BOLT1 + 2 || sprite[jj].picnum == BOLT1 + 3
									|| wallswitchcheck(jj))
								break;

							if (!(sprite[jj].picnum >= CRANE && sprite[jj].picnum <= (CRANE + 3))) {
								if (sprite[jj].z > (hittype[jj].floorz - (16 << 8))) {
									game.pInt.setsprinterpolate(jj, sprite[jj]);

									sprite[jj].x += x >> 2;
									sprite[jj].y += l >> 2;

									engine.setsprite(jj, sprite[jj].x, sprite[jj].y, sprite[jj].z);

									if ((sector[sprite[jj].sectnum].floorstat & 2) != 0)
										if (sprite[jj].statnum == 2)
											makeitfall(currentGame.getCON(), jj);
								}
							}
							break;
						}
					jj = nextj;
				}

				p = myconnectindex;
				if (ps[p].cursectnum == s.sectnum && ps[p].on_ground)
					if (klabs(ps[p].posz - ps[p].truefz) < PHEIGHT + (9 << 8)) {
						fricxv += x << 3;
						fricyv += l << 3;
					}

				sc.floorxpanning += sprite[i].yvel >> 7;

				break;

			case 35:
				if (sc.ceilingz > s.z)
					for (j = 0; j < 8; j++) {
						s.ang += engine.krand() & 511;
						k = (short) spawn(i, SMALLSMOKE);
						sprite[k].xvel = (short) (96 + (engine.krand() & 127));
						ssp(k, CLIPMASK0);
						engine.setsprite(k, sprite[k].x, sprite[k].y, sprite[k].z);
						if (rnd(16))
							spawn(i, EXPLOSION2);
					}

				switch (t[0]) {
				case 0:
					game.pInt.setfloorinterpolate(s.sectnum, sc);
					game.pInt.setceilinterpolate(s.sectnum, sc);

					sc.ceilingz += s.yvel;
					if (sc.ceilingz > sc.floorz)
						sc.floorz = sc.ceilingz;
					if (sc.ceilingz > s.z + (32 << 8))
						t[0]++;
					break;
				case 1:
					game.pInt.setceilinterpolate(s.sectnum, sc);
					sc.ceilingz -= (s.yvel << 2);
					if (sc.ceilingz < t[4]) {
						sc.ceilingz = t[4];
						t[0] = 0;
					}
					break;
				}
				break;

			case 25:

				if (t[4] == 0)
					break;

				if (sc.floorz <= sc.ceilingz)
					s.shade = 0;
				else if (sc.ceilingz <= t[3])
					s.shade = 1;

				game.pInt.setceilinterpolate(s.sectnum, sc);
				if (s.shade != 0) {
					sc.ceilingz += sprite[i].yvel << 4;
					if (sc.ceilingz > sc.floorz) {
						sc.ceilingz = sc.floorz;
						if (ps[screenpeek].field_601 != 0)
							spritesound(371, i);
					}
				} else {
					sc.ceilingz -= sprite[i].yvel << 4;
					if (sc.ceilingz < t[3]) { // XXX data[4] RA
						sc.ceilingz = t[3];
						if (ps[screenpeek].field_601 != 0)
							spritesound(167, i);
					}
				}

				break;

			case 26:

				s.xvel = 32;
				l = (s.xvel * sintable[(s.ang + 512) & 2047]) >> 14;
				x = (s.xvel * sintable[s.ang & 2047]) >> 14;

				s.shade++;
				game.pInt.setfloorinterpolate(s.sectnum, sc);
				if (s.shade > 7) {
					s.x = t[3];
					s.y = t[4];
					sc.floorz -= ((s.zvel * s.shade) - s.zvel);
					s.shade = 0;
				} else
					sc.floorz += s.zvel;

				jj = headspritesect[s.sectnum];
				while (jj >= 0) {
					nextj = nextspritesect[jj];
					if (sprite[jj].statnum != 3 && sprite[jj].statnum != 10) {
						game.pInt.setsprinterpolate(jj, sprite[jj]);

						sprite[jj].x += l;
						sprite[jj].y += x;

						sprite[jj].z += s.zvel;
						engine.setsprite(jj, sprite[jj].x, sprite[jj].y, sprite[jj].z);
					}
					jj = nextj;
				}

				p = myconnectindex;
				if (sprite[ps[p].i].sectnum == s.sectnum && ps[p].on_ground) {
					fricxv += l << 5;
					fricyv += x << 5;
				}

				for (p = connecthead; p >= 0; p = connectpoint2[p])
					if (sprite[ps[p].i].sectnum == s.sectnum && ps[p].on_ground)
						ps[p].posz += s.zvel;

				ms(i);
				engine.setsprite(i, s.x, s.y, s.z);

				break;

			case 27:

				if (ud.recstat == 0)
					break;

				hittype[i].tempang = s.ang;

				p = (short) findplayer(s);
				x = player_dist;
				if (sprite[ps[p].i].extra > 0 && myconnectindex == screenpeek) {
					if (t[0] < 0) {
						ud.camerasprite = i;
						t[0]++;
					} else if (ud.recstat == 2 && ps[p].newowner == -1) {
						if (engine.cansee(s.x, s.y, s.z, sprite[i].sectnum, ps[p].posx, ps[p].posy, ps[p].posz,
								ps[p].cursectnum)) {
							if (x < (sh & 0xFFFF)) {
								ud.camerasprite = i;
								t[0] = 999;
								s.ang += getincangle(s.ang, engine.getangle(ps[p].posx - s.x, ps[p].posy - s.y)) >> 3;
								sprite[i].yvel = (short) (100 + ((s.z - ps[p].posz) / 257));

							} else if (t[0] == 999) {
								if (ud.camerasprite == i)
									t[0] = 0;
								else
									t[0] = -10;
								ud.camerasprite = i;

							}
						} else {
							s.ang = engine.getangle(ps[p].posx - s.x, ps[p].posy - s.y);

							if (t[0] == 999) {
								if (ud.camerasprite == i)
									t[0] = 0;
								else
									t[0] = -20;
								ud.camerasprite = i;
							}
						}
					}
				}
				break;

			case 29:
//				viewBackupSectorLoc(s.sectnum, sc);
				s.hitag += 64;
				l = mulscale(s.yvel, sintable[s.hitag & 2047], 12);
				sc.floorz = s.z + l;
				break;

			case 31: // True Drop Floor
				if (t[0] == 1) {
					// Choose dir

					if (t[3] > 0) {
						t[3]--;
						break;
					}

					game.pInt.setfloorinterpolate(s.sectnum, sc);
					j = headspritesect[s.sectnum];
					while (j >= 0) {
						game.pInt.setsprinterpolate(j, sprite[j]);
						j = nextspritesect[j];
					}

					if (t[2] == 1) // Retract
					{
						if (sprite[i].ang != 1536) {
							if (klabs(sc.floorz - s.z) < sprite[i].yvel) {
								sc.floorz = s.z;
								t[2] = 0;
								t[0] = 0;
								callsound(s.sectnum, i);
							} else {
								l = sgn(s.z - sc.floorz) * sprite[i].yvel;
								sc.floorz += l;

								j = headspritesect[s.sectnum];
								while (j >= 0) {
									if (sprite[j].picnum == APLAYER && sprite[j].owner >= 0)
										if (ps[sprite[j].yvel].on_ground)
											ps[sprite[j].yvel].posz += l;
									if (sprite[j].zvel == 0 && sprite[j].statnum != 3) {
										sprite[j].z += l;
										hittype[j].floorz = sc.floorz;
									}
									j = nextspritesect[j];
								}
							}
						} else {
							if (klabs(sc.floorz - t[1]) < sprite[i].yvel) {
								sc.floorz = t[1];
								callsound(s.sectnum, i);
								t[2] = 0;
								t[0] = 0;
							} else {
								l = sgn(t[1] - sc.floorz) * sprite[i].yvel;
								sc.floorz += l;

								j = headspritesect[s.sectnum];
								while (j >= 0) {
									if (sprite[j].picnum == APLAYER && sprite[j].owner >= 0)
										if (ps[sprite[j].yvel].on_ground)
											ps[sprite[j].yvel].posz += l;
									if (sprite[j].zvel == 0 && sprite[j].statnum != 3) {
										sprite[j].z += l;
										hittype[j].floorz = sc.floorz;
									}
									j = nextspritesect[j];
								}
							}
						}
						break;
					}

					if ((s.ang & 2047) == 1536) {
						if (klabs(s.z - sc.floorz) < sprite[i].yvel) {
							callsound(s.sectnum, i);
							t[0] = 0;
							t[2] = 1;
						} else {
							l = sgn(s.z - sc.floorz) * sprite[i].yvel;
							sc.floorz += l;

							j = headspritesect[s.sectnum];
							while (j >= 0) {
								if (sprite[j].picnum == APLAYER && sprite[j].owner >= 0)
									if (ps[sprite[j].yvel].on_ground)
										ps[sprite[j].yvel].posz += l;
								if (sprite[j].zvel == 0 && sprite[j].statnum != 3 && sprite[j].statnum != 4) {
									sprite[j].z += l;
									hittype[j].floorz = sc.floorz;
								}
								j = nextspritesect[j];
							}
						}
					} else {
						if (klabs(sc.floorz - t[1]) < sprite[i].yvel) {
							t[0] = 0;
							callsound(s.sectnum, i);
							t[2] = 1;
						} else {
							l = sgn(s.z - t[1]) * sprite[i].yvel;
							sc.floorz -= l;

							j = headspritesect[s.sectnum];
							while (j >= 0) {
								if (sprite[j].picnum == APLAYER && sprite[j].owner >= 0)
									if (ps[sprite[j].yvel].on_ground)
										ps[sprite[j].yvel].posz -= l;
								if (sprite[j].zvel == 0 && sprite[j].statnum != 3 && sprite[j].statnum != 4) {
									sprite[j].z -= l;
									hittype[j].floorz = sc.floorz;
								}
								j = nextspritesect[j];
							}
						}
					}
				}
				break;

			case 33:
				if (earthquaketime > 0 && (engine.krand() & 7) == 0)
					RANDOMSCRAP(s, i);
				break;

			case 32: // True Drop Ceiling
				if (t[0] == 1) {
					// Choose dir

					game.pInt.setceilinterpolate(s.sectnum, sc);
					if (t[2] == 1) // Retract
					{
						if (sprite[i].ang != 1536) {
							if (klabs(sc.ceilingz - s.z) < (sprite[i].yvel << 1)) {
								sc.ceilingz = s.z;
								callsound(s.sectnum, i);
								t[2] = 0;
								t[0] = 0;
							} else
								sc.ceilingz += sgn(s.z - sc.ceilingz) * sprite[i].yvel;
						} else {
							if (klabs(sc.ceilingz - t[1]) < (sprite[i].yvel << 1)) {
								sc.ceilingz = t[1];
								callsound(s.sectnum, i);
								t[2] = 0;
								t[0] = 0;
							} else
								sc.ceilingz += sgn(t[1] - sc.ceilingz) * sprite[i].yvel;
						}
						break;
					}

					if ((s.ang & 2047) == 1536) {
						if (klabs(sc.ceilingz - s.z) < (sprite[i].yvel << 1)) {
							t[0] = 0;
							t[2] ^= 1;
							callsound(s.sectnum, i);
							sc.ceilingz = s.z;
						} else
							sc.ceilingz += sgn(s.z - sc.ceilingz) * sprite[i].yvel;
					} else {
						if (klabs(sc.ceilingz - t[1]) < (sprite[i].yvel << 1)) {
							t[0] = 0;
							t[2] ^= 1;
							callsound(s.sectnum, i);
						} else
							sc.ceilingz -= sgn(s.z - t[1]) * sprite[i].yvel;
					}
				}
				break;

			case 36:

				if (t[0] != 0) {
					if (t[0] == 1)
						shoot(i, sc.extra);
					else if (t[0] == 26 * 5)
						t[0] = 0;
					t[0]++;
				}
				break;

			case 128: // SE to control glass breakage

				WALL wal = wall[t[2]];

				if ((wal.cstat | 32) != 0) {
					wal.cstat &= (255 - 32);
					wal.cstat |= 16;
					if (wal.nextwall >= 0) {
						wall[wal.nextwall].cstat &= (255 - 32);
						wall[wal.nextwall].cstat |= 16;
					}
				} else
					break;

				wal.overpicnum++;
				if (wal.nextwall >= 0)
					wall[wal.nextwall].overpicnum++;

				if (t[0] < t[1])
					t[0]++;
				else {
					wal.cstat &= (128 + 32 + 8 + 4 + 2);
					if (wal.nextwall >= 0)
						wall[wal.nextwall].cstat &= (128 + 32 + 8 + 4 + 2);
					engine.deletesprite(i);
					continue;
				}
				break;

			case 130:
				if (t[0] > 80) {
					engine.deletesprite(i);
					continue;
				} else
					t[0]++;

				x = sc.floorz - sc.ceilingz;

				if (rnd(64)) {
					k = (short) spawn(i, EXPLOSION2);
					sprite[k].xrepeat = sprite[k].yrepeat = (short) (2 + (engine.krand() & 7));
					sprite[k].z = sc.floorz - (engine.krand() % x);
					sprite[k].ang += 256 - (engine.krand() % 511);
					sprite[k].xvel = (short) (engine.krand() & 127);
					ssp(k, CLIPMASK0);
				}
				break;

			case 131:
				if (t[0] > 40) {
					engine.deletesprite(i);
					continue;
				} else
					t[0]++;

				x = sc.floorz - sc.ceilingz;

				if (rnd(32)) {
					k = (short) spawn(i, EXPLOSION2);
					sprite[k].xrepeat = sprite[k].yrepeat = (short) (2 + (engine.krand() & 3));
					sprite[k].z = sc.floorz - (engine.krand() % x);
					sprite[k].ang += 256 - (engine.krand() % 511);
					sprite[k].xvel = (short) (engine.krand() & 127);
					ssp(k, CLIPMASK0);
				}
				break;
			}
		}

		for (i = headspritestat[3]; i >= 0; i = nextspritestat[i]) {
			s = sprite[i];
			if (s.lotag != 29)
				continue;
			SECTOR sc = sector[s.sectnum];
			if (sc.wallnum != 4)
				continue;
			WALL wal = wall[sc.wallptr + 2];
			if (wal.nextsector == -1)
				continue;

			engine.alignflorslope(s.sectnum, wal.x, wal.y, sector[wal.nextsector].floorz);
		}
	}

	public static boolean BowlOpen(int sectorid) {
		if (getanimationgoal(sector[sectorid], CEILZ) == -1) {
			int i = engine.nextsectorneighborz(sectorid, sector[sectorid].ceilingz, -1, -1);
			if (i == -1)
				return false;
			setanimation(sectorid, sectorid, sector[i].ceilingz, 64, CEILZ);
			return true;
		}

		return false;
	}

	private static boolean pinstay[] = new boolean[10];

	public static int BowlCheck(int nSprite) {
		Arrays.fill(pinstay, false);
		int pinums = 0, bowline = 0;
		for (int i = headspritesect[nSprite]; i >= 0; i = nextspritesect[i]) {
			if (sprite[i].picnum == 3440) { // pin sprite
				pinstay[sprite[i].lotag] = true;
				pinums++;
			}
			if (sprite[i].picnum == 280)
				bowline = sprite[i].hitag;
		}

		if (bowline != 0) {
			engine.copytilepiece(2024, 0, 0, 128, 64, bowline + 2024, 0, 0);
			for (int i = 0; i < 10; i++) {
				int x = 0, y = 0;
				if (pinstay[i]) {
					switch (i) {
					case 0:
						x = 64;
						y = 48;
						break;
					case 1:
						x = 56;
						y = 40;
						break;
					case 2:
						x = 72;
						y = 40;
						break;
					case 3:
						x = 48;
						y = 32;
						break;
					case 4:
						x = 64;
						y = 32;
						break;
					case 5:
						x = 80;
						y = 32;
						break;
					case 6:
						x = 40;
						y = 24;
						break;
					case 7:
						x = 56;
						y = 24;
						break;
					case 8:
						x = 72;
						y = 24;
						break;
					case 9:
						x = 88;
						y = 24;
						break;
					}
					engine.copytilepiece(2023, 0, 0, 8, 8, bowline + 2024, x - 4, y - 10);
				}
			}
			engine.getrender().invalidatetile(bowline + 2024, 0, 1 << 4);
		}

		return pinums;
	}

	public static void BowlReset() {
		for (int i = 0; i < 4; i++) {
			engine.copytilepiece(2024, 0, 0, 128, 64, i + 2025, 0, 0);
			for (int j = 0; j < 10; j++) {
				int x = 0, y = 0;
				switch (j) {
				case 0:
					x = 64;
					y = 48;
					break;
				case 1:
					x = 56;
					y = 40;
					break;
				case 2:
					x = 72;
					y = 40;
					break;
				case 3:
					x = 48;
					y = 32;
					break;
				case 4:
					x = 64;
					y = 32;
					break;
				case 5:
					x = 80;
					y = 32;
					break;
				case 6:
					x = 40;
					y = 24;
					break;
				case 7:
					x = 56;
					y = 24;
					break;
				case 8:
					x = 72;
					y = 24;
					break;
				case 9:
					x = 88;
					y = 24;
					break;
				}
				engine.copytilepiece(2023, 0, 0, 8, 8, i + 2025, x - 4, y - 10);
			}
			engine.getrender().invalidatetile(i + 2025, 0, 1 << 4);
		}
	}

	public static void BowlUpdate(int nSprite) {
		for (short i = headspritesect[nSprite], nexti; i >= 0; i = nexti) {
			nexti = nextspritesect[i];
			if (sprite[i].picnum == 3440) // pin sprite
				engine.deletesprite(i);
		}

		int bowline = 0;
		for (int i = headspritesect[nSprite]; i >= 0; i = nextspritesect[i]) {
			if (sprite[i].picnum == 283) // pinid sprite
			{
				int spawnid = spawn(i, 3440);
				sprite[spawnid].lotag = sprite[i].lotag;
				if (sprite[i].lotag != 3 && sprite[i].lotag != 5) {
					engine.krand();
					sprite[spawnid].clipdist = 48;
				} else {
					engine.krand();
					sprite[spawnid].clipdist = 48;
				}

				int rand = engine.krand();
				sprite[spawnid].ang -= ((rand & 0x20) - (engine.krand() & 0x40)) & kAngleMask;
			}

			if (sprite[i].picnum == 280)
				bowline = sprite[i].hitag;
		}

		if (bowline != -1) {
			engine.copytilepiece(2024, 0, 0, 128, 64, bowline + 2024, 0, 0);
			for (int i = 0; i < 10; i++) {
				int x = 0, y = 0;
				switch (i) {
				case 0:
					x = 64;
					y = 48;
					break;
				case 1:
					x = 56;
					y = 40;
					break;
				case 2:
					x = 72;
					y = 40;
					break;
				case 3:
					x = 48;
					y = 32;
					break;
				case 4:
					x = 64;
					y = 32;
					break;
				case 5:
					x = 80;
					y = 32;
					break;
				case 6:
					x = 40;
					y = 24;
					break;
				case 7:
					x = 56;
					y = 24;
					break;
				case 8:
					x = 72;
					y = 24;
					break;
				case 9:
					x = 88;
					y = 24;
					break;
				}
				engine.copytilepiece(2023, 0, 0, 8, 8, bowline + 2024, x - 4, y - 10);
			}
			engine.getrender().invalidatetile(bowline + 2024, 0, 1 << 4);
		}
	}

	public static void BowlClose(int nSprite) {
		for (int i = headspritestat[105]; i >= 0; i = nextspritestat[i]) {
			if (sprite[i].picnum == 281 && sprite[i].sectnum == sprite[nSprite].sectnum) {
				for (int j = headspritestat[105]; j >= 0; j = nextspritestat[j]) {
					if (sprite[j].picnum == 282 && sprite[i].hitag == sprite[j].hitag)
						spawn(j, 3437);

					if (sprite[j].picnum == 280 && sprite[j].hitag == sprite[i].hitag && sprite[j].lotag == 0) {
						sprite[j].lotag = 100;
						sprite[j].extra++;
						int sectnum = sprite[j].sectnum;
						if (getanimationgoal(sector[sectnum], CEILZ) == -1) {
							setanimation(sectnum, sectnum, sector[sectnum].floorz, 64, CEILZ);
						}
					}
				}
			}
		}
	}

	public static boolean isPsychoSkill() {
		return currentGame.getCON().type != RRRA && ud.player_skill >= 5
				|| currentGame.getCON().type == RRRA && ud.player_skill >= 4;
	}
}
