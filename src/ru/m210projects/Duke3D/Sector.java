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

package ru.m210projects.Duke3D;

import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Build.Gameutils.*;
import static ru.m210projects.Build.Pragmas.*;
import static ru.m210projects.Build.Net.Mmulti.*;
import static ru.m210projects.Duke3D.Types.ANIMATION.*;
import static ru.m210projects.Duke3D.Animate.*;
import static ru.m210projects.Duke3D.Main.*;
import static ru.m210projects.Duke3D.Premap.*;
import static ru.m210projects.Duke3D.Gameutils.*;
import static ru.m210projects.Duke3D.Names.*;
import static ru.m210projects.Duke3D.Sounds.*;
import static ru.m210projects.Duke3D.Actors.*;
import static ru.m210projects.Duke3D.Player.*;
import static ru.m210projects.Duke3D.Spawn.*;
import static ru.m210projects.Duke3D.View.*;
import static ru.m210projects.Duke3D.SoundDefs.*;
import static ru.m210projects.Duke3D.Weapons.*;
import static ru.m210projects.Duke3D.Globals.*;
import ru.m210projects.Build.Types.SECTOR;
import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Build.Types.Tile;
import ru.m210projects.Build.Types.WALL;
import ru.m210projects.Duke3D.Types.PlayerStruct;

public class Sector {

	public static void moveclouds() {
		if (totalclock > cloudtotalclock || totalclock < (cloudtotalclock - 7)) {
			cloudtotalclock = totalclock + 6;

			for (int i = 0; i < numclouds; i++) {
				cloudx[i] += (BCosAngle(BClampAngle(ps[screenpeek].ang)) / 512.0f);
				cloudy[i] += (BSinAngle(BClampAngle(ps[screenpeek].ang)) / 512.0f);

				sector[clouds[i]].ceilingxpanning = (short) (cloudx[i] >> 6);
				sector[clouds[i]].ceilingypanning = (short) (cloudy[i] >> 6);
			}
		}
	}

	public static boolean ceilingspace(short sectnum) {
		if ((sector[sectnum].ceilingstat & 1) != 0 && sector[sectnum].ceilingpal == 0) {
			switch (sector[sectnum].ceilingpicnum) {
			case MOONSKY1:
			case BIGORBIT1:
				return true;
			}
		}
		return false;
	}

	public static boolean floorspace(short sectnum) {
		if ((sector[sectnum].floorstat & 1) != 0 && sector[sectnum].ceilingpal == 0) {
			switch (sector[sectnum].floorpicnum) {
			case MOONSKY1:
			case BIGORBIT1:
				return true;
			}
		}
		return false;
	}

	public static boolean wallswitchcheck(int i) {
		switch (sprite[i].picnum) {
		case HANDPRINTSWITCH:
		case HANDPRINTSWITCH + 1:
		case ALIENSWITCH:
		case ALIENSWITCH + 1:
		case MULTISWITCH:
		case MULTISWITCH + 1:
		case MULTISWITCH + 2:
		case MULTISWITCH + 3:
		case ACCESSSWITCH:
		case ACCESSSWITCH2:
		case PULLSWITCH:
		case PULLSWITCH + 1:
		case HANDSWITCH:
		case HANDSWITCH + 1:
		case SLOTDOOR:
		case SLOTDOOR + 1:
		case LIGHTSWITCH:
		case LIGHTSWITCH + 1:
		case SPACELIGHTSWITCH:
		case SPACELIGHTSWITCH + 1:
		case SPACEDOORSWITCH:
		case SPACEDOORSWITCH + 1:
		case FRANKENSTINESWITCH:
		case FRANKENSTINESWITCH + 1:
		case LIGHTSWITCH2:
		case LIGHTSWITCH2 + 1:
		case POWERSWITCH1:
		case POWERSWITCH1 + 1:
		case LOCKSWITCH1:
		case LOCKSWITCH1 + 1:
		case POWERSWITCH2:
		case POWERSWITCH2 + 1:
		case DIPSWITCH:
		case DIPSWITCH + 1:
		case DIPSWITCH2:
		case DIPSWITCH2 + 1:
		case TECHSWITCH:
		case TECHSWITCH + 1:
		case DIPSWITCH3:
		case DIPSWITCH3 + 1:
			return true;
		}
		return false;
	}

	private static boolean haltsoundhack;

	public static int callsound(int sn, int whatsprite) {
		short i;

		if (haltsoundhack) {
			haltsoundhack = false;
			return -1;
		}

		i = headspritesect[sn];
		while (i >= 0) {
			if (sprite[i].picnum == MUSICANDSFX && sprite[i].lotag < 1000) {
				if (whatsprite == -1)
					whatsprite = i;

				if (hittype[i].temp_data[0] == 0) {
					if (sprite[i].lotag < NUM_SOUNDS && (currentGame.getCON().soundm[sprite[i].lotag] & 16) == 0) {
						if (sprite[i].lotag != 0) {
							spritesound(sprite[i].lotag, whatsprite);
							if (sprite[i].hitag != 0 && sprite[i].lotag != sprite[i].hitag
									&& sprite[i].hitag < NUM_SOUNDS)
								stopsound(sprite[i].hitag);
						}

						if ((sector[sprite[i].sectnum].lotag & 0xff) != 22)
							hittype[i].temp_data[0] = 1;
					}
				} else if (sprite[i].hitag < NUM_SOUNDS) {
					if (sprite[i].hitag != 0)
						spritesound(sprite[i].hitag, whatsprite);
					if ((currentGame.getCON().soundm[sprite[i].lotag] & 1) != 0
							|| (sprite[i].hitag != 0 && sprite[i].hitag != sprite[i].lotag))
						stopsound(sprite[i].lotag);
					hittype[i].temp_data[0] = 0;
				}
				return sprite[i].lotag;
			}
			i = nextspritesect[i];
		}
		return -1;
	}

	public static boolean check_activator_motion(int lotag) {
		int i = headspritestat[8];
		while (i >= 0) {
			if (sprite[i].lotag == lotag) {
				SPRITE s = sprite[i];

				for (int j = gAnimationCount - 1; j >= 0; j--)
					if (s.sectnum == gAnimationData[j].sect)
						return (true);

				int j = headspritestat[3];
				while (j >= 0) {
					if (s.sectnum == sprite[j].sectnum)
						switch (sprite[j].lotag) {
						case 11:
						case 30:
							if (hittype[j].temp_data[4] != 0)
								return (true);
							break;
						case 20:
						case 31:
						case 32:
						case 18:
							if (hittype[j].temp_data[0] != 0)
								return (true);
							break;
						}

					j = nextspritestat[j];
				}
			}
			i = nextspritestat[i];
		}
		return (false);
	}

	public static boolean isadoorwall(int dapic) {
		switch (dapic) {
		case DOORTILE1:
		case DOORTILE2:
		case DOORTILE3:
		case DOORTILE4:
		case DOORTILE5:
		case DOORTILE6:
		case DOORTILE7:
		case DOORTILE8:
		case DOORTILE9:
		case DOORTILE10:
		case DOORTILE11:
		case DOORTILE12:
		case DOORTILE14:
		case DOORTILE15:
		case DOORTILE16:
		case DOORTILE17:
		case DOORTILE18:
		case DOORTILE19:
		case DOORTILE20:
		case DOORTILE21:
		case DOORTILE22:
		case DOORTILE23:
			return true;
		}
		return false;
	}

	public static boolean isanunderoperator(int lotag) {
		switch (lotag & 0xff) {
		case 15:
		case 16:
		case 17:
		case 18:
		case 19:
		case 22:
		case 26:
			return true;
		}
		return false;
	}

	public static boolean isanearoperator(int lotag) {
		switch (lotag & 0xff) {
		case 9:
		case 15:
		case 16:
		case 17:
		case 18:
		case 19:
		case 20:
		case 21:
		case 22:
		case 23:
		case 25:
		case 26:
		case 29:// Toothed door
			return true;
		}
		return false;
	}

	public static int ldist(SPRITE s1, SPRITE s2) {
		int vx = s1.x - s2.x;
		int vy = s1.y - s2.y;
		return (FindDistance2D(vx, vy) + 1);
	}

	public static int dist(SPRITE s1, SPRITE s2) {
		int vx = s1.x - s2.x;
		int vy = s1.y - s2.y;
		int vz = s1.z - s2.z;
		return (FindDistance3D(vx, vy, vz >> 4));
	}

	public static int player_dist;

	public static int findplayer(SPRITE s) {
		if (ud.multimode < 2) {
			player_dist = klabs(ps[myconnectindex].oposx - s.x) + klabs(ps[myconnectindex].oposy - s.y)
					+ ((klabs(ps[myconnectindex].oposz - s.z + (28 << 8))) >> 4);
			return myconnectindex;
		}

		int closest = 0x7fffffff;
		int closest_player = 0;

		for (int j = connecthead; j >= 0; j = connectpoint2[j]) {
			int x = klabs(ps[j].oposx - s.x) + klabs(ps[j].oposy - s.y) + ((klabs(ps[j].oposz - s.z + (28 << 8))) >> 4);
			if (x < closest && sprite[ps[j].i].extra > 0) {
				closest_player = j;
				closest = x;
			}
		}

		player_dist = closest;
		return closest_player;
	}

	public static int findotherplayer(int p) {
		int closest = 0x7fffffff;
		int closest_player = p;

		for (int j = connecthead; j >= 0; j = connectpoint2[j])
			if (p != j && sprite[ps[j].i].extra > 0) {
				int x = klabs(ps[j].oposx - ps[p].posx) + klabs(ps[j].oposy - ps[p].posy)
						+ (klabs(ps[j].oposz - ps[p].posz) >> 4);

				if (x < closest) {
					closest_player = j;
					closest = x;
				}
			}

		player_dist = closest;
		return closest_player;
	}

	public static int checkcursectnums(int sect) {
		for (int i = connecthead; i >= 0; i = connectpoint2[i])
			if (sprite[ps[i].i].sectnum == sect)
				return i;
		return -1;
	}

	public static void animatecamsprite() {
		if (camsprite <= 0)
			return;

		int i = camsprite;

		if (hittype[i].temp_data[0] >= 11) {
			hittype[i].temp_data[0] = 0;

			if (ps[screenpeek].newowner >= 0)
				sprite[i].owner = ps[screenpeek].newowner;

			else if (sprite[i].owner >= 0 && dist(sprite[ps[screenpeek].i], sprite[i]) < 2048) {

				if (engine.getTile(TILE_VIEWSCR).data == null) {
					Tile pic = engine.getTile(sprite[i].picnum);
					engine.allocatepermanenttile(TILE_VIEWSCR, pic.getWidth(), pic.getHeight());
				} else
					VIEWSCR_Lock = 255;
				xyzmirror(sprite[i].owner, TILE_VIEWSCR);
			}
		} else
			hittype[i].temp_data[0]++;
	}

	public static void animatewalls() {
		int i, j, p, t;

		for (p = 0; p < numanimwalls; p++) {
			i = animwall[p].wallnum;
			j = wall[i].picnum;

			switch (j) {
			case SCREENBREAK1:
			case SCREENBREAK2:
			case SCREENBREAK3:
			case SCREENBREAK4:
			case SCREENBREAK5:

			case SCREENBREAK9:
			case SCREENBREAK10:
			case SCREENBREAK11:
			case SCREENBREAK12:
			case SCREENBREAK13:
			case SCREENBREAK14:
			case SCREENBREAK15:
			case SCREENBREAK16:
			case SCREENBREAK17:
			case SCREENBREAK18:
			case SCREENBREAK19:

				if ((engine.krand() & 255) < 16) {
					animwall[p].tag = wall[i].picnum;
					wall[i].picnum = SCREENBREAK6;
				}

				continue;

			case SCREENBREAK6:
			case SCREENBREAK7:
			case SCREENBREAK8:

				if (animwall[p].tag >= 0 && wall[i].extra != FEMPIC2 && wall[i].extra != FEMPIC3)
					wall[i].picnum = (short) animwall[p].tag;
				else {
					wall[i].picnum++;
					if (wall[i].picnum == (SCREENBREAK6 + 3))
						wall[i].picnum = SCREENBREAK6;
				}
				continue;

			}

			if ((wall[i].cstat & 16) != 0)
				switch (wall[i].overpicnum) {
				case W_FORCEFIELD:
				case W_FORCEFIELD + 1:
				case W_FORCEFIELD + 2:

					t = animwall[p].tag;

					if ((wall[i].cstat & 254) != 0) {
						wall[i].xpanning -= t >> 10;
						wall[i].ypanning -= t >> 10;

						if (wall[i].extra == 1) {
							wall[i].extra = 0;
							animwall[p].tag = 0;
						} else
							animwall[p].tag += 128;

						if (animwall[p].tag < (128 << 4)) {
							if ((animwall[p].tag & 128) != 0)
								wall[i].overpicnum = W_FORCEFIELD;
							else
								wall[i].overpicnum = W_FORCEFIELD + 1;
						} else {
							if ((engine.krand() & 255) < 32)
								animwall[p].tag = 128 << (engine.krand() & 3);
							else
								wall[i].overpicnum = W_FORCEFIELD + 1;
						}
					}

					break;
				}
		}
	}

	public static boolean activatewarpelevators(int s, int d) // Parm = sectoreffectornum
	{
		short i, sn;

		sn = sprite[s].sectnum;

		// See if the sector exists

		i = headspritestat[3];
		while (i >= 0) {
			if (sprite[i].lotag == 17)
				if (sprite[i].hitag == sprite[s].hitag)
					if ((klabs(sector[sn].floorz - hittype[s].temp_data[2]) > sprite[i].yvel)
							|| (sector[sprite[i].sectnum].hitag == (sector[sn].hitag - d)))
						break;
			i = nextspritestat[i];
		}

		if (i == -1) {
			d = 0;
			return true; // No find
		} else {
			if (d == 0)
				spritesound(ELEVATOR_OFF, s);
			else
				spritesound(ELEVATOR_ON, s);
		}

		i = headspritestat[3];
		while (i >= 0) {
			if (sprite[i].lotag == 17)
				if (sprite[i].hitag == sprite[s].hitag) {
					hittype[i].temp_data[0] = d;
					hittype[i].temp_data[1] = d; // Make all check warp
				}
			i = nextspritestat[i];
		}
		return false;
	}

	public static int[] wallfind = new int[2];

	public static void operatesectors(int sn, int ii) {
		int i, j = 0, l, q, startwall, endwall;
		SECTOR sptr = sector[sn];
		switch (sptr.lotag & (0xffff - 49152)) {

		case 30:
			j = sector[sn].hitag;
			if (hittype[j].tempang == 0 || hittype[j].tempang == 256)
				callsound(sn, ii);
			if (sprite[j].extra == 1)
				sprite[j].extra = 3;
			else
				sprite[j].extra = 1;
			break;

		case 31:

			j = sector[sn].hitag;
			if (hittype[j].temp_data[4] == 0)
				hittype[j].temp_data[4] = 1;

			callsound(sn, ii);
			break;

		case 26: // The split doors
			i = getanimationgoal(sptr, CEILZ);
			if (i == -1) // if the door has stopped
			{
				haltsoundhack = true;
				sptr.lotag &= 0xff00;
				sptr.lotag |= 22;
				operatesectors(sn, ii);
				sptr.lotag &= 0xff00;
				sptr.lotag |= 9;
				operatesectors(sn, ii);
				sptr.lotag &= 0xff00;
				sptr.lotag |= 26;
			}
			return;

		case 9: {
			int dax, day, dax2, day2, sp;

			startwall = sptr.wallptr;
			endwall = startwall + sptr.wallnum - 1;

			sp = sptr.extra >> 4;

			// first find center point by averaging all points
			dax = 0;
			day = 0;
			for (i = startwall; i <= endwall; i++) {
				dax += wall[i].x;
				day += wall[i].y;
			}
			dax /= (endwall - startwall + 1);
			day /= (endwall - startwall + 1);

			// find any points with either same x or same y coordinate
			// as center (dax, day) - should be 2 points found.
			wallfind[0] = -1;
			wallfind[1] = -1;
			for (i = startwall; i <= endwall; i++)
				if ((wall[i].x == dax) || (wall[i].y == day)) {
					if (wallfind[0] == -1)
						wallfind[0] = i;
					else
						wallfind[1] = i;
				}

			for (j = 0; j < 2; j++) {
				if ((wall[wallfind[j]].x == dax) && (wall[wallfind[j]].y == day)) {
					// find what direction door should open by averaging the
					// 2 neighboring points of wallfind[0] & wallfind[1].
					i = wallfind[j] - 1;
					if (i < startwall)
						i = endwall;
					dax2 = ((wall[i].x + wall[wall[wallfind[j]].point2].x) >> 1) - wall[wallfind[j]].x;
					day2 = ((wall[i].y + wall[wall[wallfind[j]].point2].y) >> 1) - wall[wallfind[j]].y;
					if (dax2 != 0) {
						dax2 = wall[wall[wall[wallfind[j]].point2].point2].x;
						dax2 -= wall[wall[wallfind[j]].point2].x;
						setanimation(sn, wallfind[j], wall[wallfind[j]].x + dax2, sp, WALLX);
						setanimation(sn, i, wall[i].x + dax2, sp, WALLX);
						setanimation(sn, wall[wallfind[j]].point2, wall[wall[wallfind[j]].point2].x + dax2, sp, WALLX);
						callsound(sn, ii);
					} else if (day2 != 0) {
						day2 = wall[wall[wall[wallfind[j]].point2].point2].y;
						day2 -= wall[wall[wallfind[j]].point2].y;
						setanimation(sn, wallfind[j], wall[wallfind[j]].y + day2, sp, WALLY);
						setanimation(sn, i, wall[i].y + day2, sp, WALLY);
						setanimation(sn, wall[wallfind[j]].point2, wall[wall[wallfind[j]].point2].y + day2, sp, WALLY);
						callsound(sn, ii);
					}
				} else {
					i = wallfind[j] - 1;
					if (i < startwall)
						i = endwall;
					dax2 = ((wall[i].x + wall[wall[wallfind[j]].point2].x) >> 1) - wall[wallfind[j]].x;
					day2 = ((wall[i].y + wall[wall[wallfind[j]].point2].y) >> 1) - wall[wallfind[j]].y;
					if (dax2 != 0) {
						// setanimation(short animsect,long *animptr, long thegoal, long thevel)
						setanimation(sn, wallfind[j], dax, sp, WALLX);
						setanimation(sn, i, dax + dax2, sp, WALLX);
						setanimation(sn, wall[wallfind[j]].point2, dax + dax2, sp, WALLX);
						callsound(sn, ii);
					} else if (day2 != 0) {
						setanimation(sn, wallfind[j], day, sp, WALLY);
						setanimation(sn, i, day + day2, sp, WALLY);
						setanimation(sn, wall[wallfind[j]].point2, day + day2, sp, WALLY);
						callsound(sn, ii);
					}
				}
			}

		}
			return;

		case 15:// Warping elevators

			if (sprite[ii].picnum != APLAYER)
				return;

			i = headspritesect[sn];
			while (i >= 0) {
				if (sprite[i].picnum == SECTOREFFECTOR && sprite[i].lotag == 17)
					break;
				i = nextspritesect[i];
			}

			if (i == -1)
				return;

			if (sprite[ii].sectnum == sn) {
				if (activatewarpelevators(i, -1))
					activatewarpelevators(i, 1);
				else if (activatewarpelevators(i, 1))
					activatewarpelevators(i, -1);
				return;
			} else {
				if (sptr.floorz > sprite[i].z)
					activatewarpelevators(i, -1);
				else
					activatewarpelevators(i, 1);
			}

			return;

		case 16:
		case 17:

			i = getanimationgoal(sptr, FLOORZ);
			if (i == -1) {
				i = engine.nextsectorneighborz(sn, sptr.floorz, 1, 1);
				if (i == -1) {
					i = engine.nextsectorneighborz(sn, sptr.floorz, 1, -1);
					if (i == -1)
						return;
				}
				j = sector[i].floorz;
				setanimation(sn, sn, j, sptr.extra, FLOORZ);
				callsound(sn, ii);
			}

			return;

		case 18:
		case 19:

			i = getanimationgoal(sptr, FLOORZ);

			if (i == -1) {
				i = engine.nextsectorneighborz(sn, sptr.floorz, 1, -1);
				if (i == -1)
					i = engine.nextsectorneighborz(sn, sptr.floorz, 1, 1);
				if (i == -1)
					return;
				j = sector[i].floorz;
				q = sptr.extra;
				l = sptr.ceilingz - sptr.floorz;
				setanimation(sn, sn, j, q, FLOORZ);
				setanimation(sn, sn, j + l, q, CEILZ);
				callsound(sn, ii);
			}
			return;

		case 29:

			if ((sptr.lotag & 0x8000) != 0)
				j = sector[engine.nextsectorneighborz(sn, sptr.ceilingz, 1, 1)].floorz;
			else
				j = sector[engine.nextsectorneighborz(sn, sptr.ceilingz, -1, -1)].ceilingz;

			i = headspritestat[3]; // Effectors
			while (i >= 0) {
				if ((sprite[i].lotag == 22) && (sprite[i].hitag == sptr.hitag)) {
					sector[sprite[i].sectnum].extra = (short) -sector[sprite[i].sectnum].extra;

					hittype[i].temp_data[0] = sn;
					hittype[i].temp_data[1] = 1;
				}
				i = nextspritestat[i];
			}

			sptr.lotag ^= 0x8000;

			setanimation(sn, sn, j, sptr.extra, CEILZ);

			callsound(sn, ii);

			return;

		case 20:

			boolean REDODOOR;
			do {
				REDODOOR = false;
				if ((sptr.lotag & 0x8000) != 0) {
					i = headspritesect[sn];
					while (i >= 0) {
						if (sprite[i].statnum == 3 && sprite[i].lotag == 9) {
							j = sprite[i].z;
							break;
						}
						i = nextspritesect[i];
					}
					if (i == -1)
						j = sptr.floorz;
				} else {
					j = engine.nextsectorneighborz(sn, sptr.ceilingz, -1, -1);

					if (j >= 0)
						j = sector[j].ceilingz;
					else {
						sptr.lotag |= 32768;
						REDODOOR = true;
					}
				}
			} while (REDODOOR);

			sptr.lotag ^= 0x8000;

			setanimation(sn, sn, j, sptr.extra, CEILZ);
			callsound(sn, ii);

			return;

		case 21:
			i = getanimationgoal(sptr, FLOORZ);
			if (i >= 0) {
				if (gAnimationData[i].goal == sptr.ceilingz)
					gAnimationData[i].goal = sector[engine.nextsectorneighborz(sn, sptr.ceilingz, 1, 1)].floorz;
				else
					gAnimationData[i].goal = sptr.ceilingz;
				j = gAnimationData[i].goal;
			} else {
				if (sptr.ceilingz == sptr.floorz)
					j = sector[engine.nextsectorneighborz(sn, sptr.ceilingz, 1, 1)].floorz;
				else
					j = sptr.ceilingz;

				sptr.lotag ^= 0x8000;

				if (setanimation(sn, sn, j, sptr.extra, FLOORZ) >= 0)
					callsound(sn, ii);
			}
			return;

		case 22:

			// REDODOOR22:

			if ((sptr.lotag & 0x8000) != 0) {
				q = (sptr.ceilingz + sptr.floorz) >> 1;
				j = setanimation(sn, sn, q, sptr.extra, FLOORZ);
				j = setanimation(sn, sn, q, sptr.extra, CEILZ);
			} else {
				int fsect = engine.nextsectorneighborz(sn, sptr.floorz, 1, 1);
				if (fsect != -1) {
					q = sector[fsect].floorz;
					j = setanimation(sn, sn, q, sptr.extra, FLOORZ);
				}
				int csect = engine.nextsectorneighborz(sn, sptr.ceilingz, -1, -1);
				if (csect != -1) {
					q = sector[csect].ceilingz;
					j = setanimation(sn, sn, q, sptr.extra, CEILZ);
				}
			}

			sptr.lotag ^= 0x8000;

			callsound(sn, ii);

			return;

		case 23: // Swingdoor

			j = -1;
			q = 0;

			i = headspritestat[3];
			while (i >= 0) {
				if (sprite[i].lotag == 11 && sprite[i].sectnum == sn && hittype[i].temp_data[4] == 0) {
					j = i;
					break;
				}
				i = nextspritestat[i];
			}

			if (i < 0)
				return;
			l = sector[sprite[i].sectnum].lotag & 0x8000;

			if (j >= 0) {
				i = headspritestat[3];
				while (i >= 0) {
					if (l == (sector[sprite[i].sectnum].lotag & 0x8000) && sprite[i].lotag == 11
							&& sprite[j].hitag == sprite[i].hitag && hittype[i].temp_data[4] == 0) {
						if ((sector[sprite[i].sectnum].lotag & 0x8000) != 0)
							sector[sprite[i].sectnum].lotag &= 0x7fff;
						else
							sector[sprite[i].sectnum].lotag |= 0x8000;
						hittype[i].temp_data[4] = 1;
						hittype[i].temp_data[3] = -hittype[i].temp_data[3];
						if (q == 0) {
							callsound(sn, i);
							q = 1;
						}
					}
					i = nextspritestat[i];
				}
			}
			return;

		case 25: // Subway type sliding doors

			j = headspritestat[3];
			while (j >= 0)// Find the sprite
			{
				if ((sprite[j].lotag) == 15 && sprite[j].sectnum == sn)
					break; // Found the sectoreffector.
				j = nextspritestat[j];
			}

			if (j < 0)
				return;

			i = headspritestat[3];
			while (i >= 0) {
				if (sprite[i].hitag == sprite[j].hitag) {
					if (sprite[i].lotag == 15) {
						sector[sprite[i].sectnum].lotag ^= 0x8000; // Toggle the open or close
						sprite[i].ang += 1024;
						if (hittype[i].temp_data[4] != 0)
							callsound(sprite[i].sectnum, i);
						callsound(sprite[i].sectnum, i);
						if ((sector[sprite[i].sectnum].lotag & 0x8000) != 0)
							hittype[i].temp_data[4] = 1;
						else
							hittype[i].temp_data[4] = 2;
					}
				}
				i = nextspritestat[i];
			}
			return;

		case 27: // Extended bridge

			j = headspritestat[3];
			while (j >= 0) {
				if ((sprite[j].lotag & 0xff) == 20 && sprite[j].sectnum == sn) // Bridge
				{

					sector[sn].lotag ^= 0x8000;
					if ((sector[sn].lotag & 0x8000) != 0) // OPENING
						hittype[j].temp_data[0] = 1;
					else
						hittype[j].temp_data[0] = 2;
					callsound(sn, ii);
					break;
				}
				j = nextspritestat[j];
			}
			return;

		case 28:
			// activate the rest of them

			j = headspritesect[sn];
			while (j >= 0) {
				if (sprite[j].statnum == 3 && (sprite[j].lotag & 0xff) == 21)
					break; // Found it
				j = nextspritesect[j];
			}

			if (j != -1)
				j = sprite[j].hitag;

			l = headspritestat[3];
			while (l >= 0) {
				if ((sprite[l].lotag & 0xff) == 21 && hittype[l].temp_data[0] == 0 && (sprite[l].hitag) == j)
					hittype[l].temp_data[0] = 1;
				l = nextspritestat[l];
			}
			callsound(sn, ii);

			return;
		}
	}

	public static void operaterespawns(int low) {
		short i, j, nexti;

		i = headspritestat[11];
		while (i >= 0) {
			nexti = nextspritestat[i];
			if (sprite[i].lotag == low)
				switch (sprite[i].picnum) {
				case RESPAWN:
					if (sprite[i].hitag < 0 || sprite[i].hitag >= MAXTILES
							|| (badguypic(sprite[i].hitag) && ud.monsters_off))
						break;

					j = (short) spawn(i, TRANSPORTERSTAR);
					sprite[j].z -= (32 << 8);

					sprite[i].extra = 66 - 12; // Just a way to killit
					break;
				}
			i = nexti;
		}
	}

	public static void operateactivators(int low, int snum) {
		short k;
		short[] p;
		for (int i = numcyclers - 1; i >= 0; i--) {
			p = cyclers[i];

			if (p[4] == low) {
				p[5] ^= 1;

				sector[p[0]].floorshade = sector[p[0]].ceilingshade = (byte) p[3];
				int startwall = sector[p[0]].wallptr;
				int endwall = startwall + sector[p[0]].wallnum;

				for (int j = startwall; j < endwall; j++) {
					WALL wal = wall[j];
					wal.shade = (byte) p[3];
				}
			}
		}

		int i = headspritestat[8];
		k = -1;
		while (i >= 0) {
			if (sprite[i].lotag == low) {
				if (sprite[i].picnum == ACTIVATORLOCKED) {
					if ((sector[sprite[i].sectnum].lotag & 16384) != 0)
						sector[sprite[i].sectnum].lotag &= 65535 - 16384;
					else
						sector[sprite[i].sectnum].lotag |= 16384;

					if (snum >= 0) {
						if ((sector[sprite[i].sectnum].lotag & 16384) != 0)
							FTA(4, ps[snum]);
						else
							FTA(8, ps[snum]);
					}
				} else {
					switch (sprite[i].hitag) {
					case 0:
						break;
					case 1:
						if (sector[sprite[i].sectnum].floorz != sector[sprite[i].sectnum].ceilingz) {
							i = nextspritestat[i];
							continue;
						}
						break;
					case 2:
						if (sector[sprite[i].sectnum].floorz == sector[sprite[i].sectnum].ceilingz) {
							i = nextspritestat[i];
							continue;
						}
						break;
					}

					if (sector[sprite[i].sectnum].lotag < 3) {
						int j = headspritesect[sprite[i].sectnum];
						while (j >= 0) {
							if (sprite[j].statnum == 3)
								switch (sprite[j].lotag) {
								case 36:
								case 31:
								case 32:
								case 18:
									hittype[j].temp_data[0] = 1 - hittype[j].temp_data[0];
									callsound(sprite[i].sectnum, j);
									break;
								}
							j = nextspritesect[j];
						}
					}

					if (k == -1 && (sector[sprite[i].sectnum].lotag & 0xff) == 22)
						k = (short) callsound(sprite[i].sectnum, i);

					operatesectors(sprite[i].sectnum, i);
				}
			}
			i = nextspritestat[i];
		}

		operaterespawns(low);
	}

	public static void operatemasterswitches(int low) {
		int i = headspritestat[6];
		while (i >= 0) {
			if (sprite[i].picnum == MASTERSWITCH && sprite[i].lotag == low && sprite[i].yvel == 0)
				sprite[i].yvel = 1;
			i = nextspritestat[i];
		}
	}

	public static void operateforcefields(int s, int low) {
		for (int p = numanimwalls; p >= 0; p--) {
			int i = animwall[p].wallnum;

			if (low == wall[i].lotag || low == -1)
				switch (wall[i].overpicnum) {
				case W_FORCEFIELD:
				case W_FORCEFIELD + 1:
				case W_FORCEFIELD + 2:
				case BIGFORCE:

					animwall[p].tag = 0;

					if (wall[i].cstat != 0) {
						wall[i].cstat = 0;

						if (s >= 0 && sprite[s].picnum == SECTOREFFECTOR && sprite[s].lotag == 30)
							wall[i].lotag = 0;
					} else
						wall[i].cstat = 85;
					break;
				}
		}
	}

	public static boolean checkhitswitch(int snum, int w, int switchtype) {
		int switchpal;
		int i, x, lotag, hitag, picnum, correctdips, numdips;
		int sx, sy;

		if (w < 0)
			return false;
		correctdips = 1;
		numdips = 0;

		if (switchtype == 1) // A wall sprite
		{
			lotag = sprite[w].lotag;
			if (lotag == 0)
				return false;
			hitag = sprite[w].hitag;
			sx = sprite[w].x;
			sy = sprite[w].y;
			picnum = sprite[w].picnum;
			switchpal = sprite[w].pal;
		} else {
			lotag = wall[w].lotag;
			if (lotag == 0)
				return false;
			hitag = wall[w].hitag;
			sx = wall[w].x;
			sy = wall[w].y;
			picnum = wall[w].picnum;
			switchpal = wall[w].pal;
		}

		switch (picnum) {
		case DIPSWITCH:
		case DIPSWITCH + 1:
		case TECHSWITCH:
		case TECHSWITCH + 1:
		case ALIENSWITCH:
		case ALIENSWITCH + 1:
			break;
		case DEVELOPERCOMMENTARY + 1: // Twentieth Anniversary World Tour
			if (switchtype == 1) {
				sprite[w].picnum--;
				StopCommentary(pCommentary);
				return true;
			}
			return false;
		case DEVELOPERCOMMENTARY: // Twentieth Anniversary World Tour
			if (switchtype == 1) {
				if (StartCommentary(lotag, w))
					sprite[w].picnum++;
				return true;
			}
			return false;
		case ACCESSSWITCH:
		case ACCESSSWITCH2:
			if (ps[snum].access_incs == 0) {
				if (switchpal == 0) {
					if ((ps[snum].got_access & 1) != 0)
						ps[snum].access_incs = 1;
					else
						FTA(70, ps[snum]);
				}

				else if (switchpal == 21) {
					if ((ps[snum].got_access & 2) != 0)
						ps[snum].access_incs = 1;
					else
						FTA(71, ps[snum]);
				}

				else if (switchpal == 23) {
					if ((ps[snum].got_access & 4) != 0)
						ps[snum].access_incs = 1;
					else
						FTA(72, ps[snum]);
				}

				if (ps[snum].access_incs == 1) {
					if (switchtype == 0)
						ps[snum].access_wallnum = (short) w;
					else
						ps[snum].access_spritenum = (short) w;
				}

				return false;
			}
		case DIPSWITCH2:
		case DIPSWITCH2 + 1:
		case DIPSWITCH3:
		case DIPSWITCH3 + 1:
		case MULTISWITCH:
		case MULTISWITCH + 1:
		case MULTISWITCH + 2:
		case MULTISWITCH + 3:
		case PULLSWITCH:
		case PULLSWITCH + 1:
		case HANDSWITCH:
		case HANDSWITCH + 1:
		case SLOTDOOR:
		case SLOTDOOR + 1:
		case LIGHTSWITCH:
		case LIGHTSWITCH + 1:
		case SPACELIGHTSWITCH:
		case SPACELIGHTSWITCH + 1:
		case SPACEDOORSWITCH:
		case SPACEDOORSWITCH + 1:
		case FRANKENSTINESWITCH:
		case FRANKENSTINESWITCH + 1:
		case LIGHTSWITCH2:
		case LIGHTSWITCH2 + 1:
		case POWERSWITCH1:
		case POWERSWITCH1 + 1:
		case LOCKSWITCH1:
		case LOCKSWITCH1 + 1:
		case POWERSWITCH2:
		case POWERSWITCH2 + 1:
			if (check_activator_motion(lotag))
				return false;
			break;
		default:
			if (!isadoorwall(picnum))
				return false;
			break;
		}

		i = headspritestat[0];
		while (i >= 0) {
			if (lotag == sprite[i].lotag)
				switch (sprite[i].picnum) {
				case DIPSWITCH:
				case TECHSWITCH:
				case ALIENSWITCH:
					if (switchtype == 1 && w == i)
						sprite[i].picnum++;
					else if (sprite[i].hitag == 0)
						correctdips++;
					numdips++;
					break;
				case TECHSWITCH + 1:
				case DIPSWITCH + 1:
				case ALIENSWITCH + 1:
					if (switchtype == 1 && w == i)
						sprite[i].picnum--;
					else if (sprite[i].hitag == 1)
						correctdips++;
					numdips++;
					break;
				case MULTISWITCH:
				case MULTISWITCH + 1:
				case MULTISWITCH + 2:
				case MULTISWITCH + 3:
					sprite[i].picnum++;
					if (sprite[i].picnum > (MULTISWITCH + 3))
						sprite[i].picnum = MULTISWITCH;
					break;
				case ACCESSSWITCH:
				case ACCESSSWITCH2:
				case SLOTDOOR:
				case LIGHTSWITCH:
				case SPACELIGHTSWITCH:
				case SPACEDOORSWITCH:
				case FRANKENSTINESWITCH:
				case LIGHTSWITCH2:
				case POWERSWITCH1:
				case LOCKSWITCH1:
				case POWERSWITCH2:
				case HANDSWITCH:
				case PULLSWITCH:
				case DIPSWITCH2:
				case DIPSWITCH3:
					sprite[i].picnum++;
					break;
				case PULLSWITCH + 1:
				case HANDSWITCH + 1:
				case LIGHTSWITCH2 + 1:
				case POWERSWITCH1 + 1:
				case LOCKSWITCH1 + 1:
				case POWERSWITCH2 + 1:
				case SLOTDOOR + 1:
				case LIGHTSWITCH + 1:
				case SPACELIGHTSWITCH + 1:
				case SPACEDOORSWITCH + 1:
				case FRANKENSTINESWITCH + 1:
				case DIPSWITCH2 + 1:
				case DIPSWITCH3 + 1:
					sprite[i].picnum--;
					break;
				}
			i = nextspritestat[i];
		}

		for (i = 0; i < numwalls; i++) {
			x = i;
			if (lotag == wall[x].lotag)
				switch (wall[x].picnum) {
				case DIPSWITCH:
				case TECHSWITCH:
				case ALIENSWITCH:
					if (switchtype == 0 && i == w)
						wall[x].picnum++;
					else if (wall[x].hitag == 0)
						correctdips++;
					numdips++;
					break;
				case DIPSWITCH + 1:
				case TECHSWITCH + 1:
				case ALIENSWITCH + 1:
					if (switchtype == 0 && i == w)
						wall[x].picnum--;
					else if (wall[x].hitag == 1)
						correctdips++;
					numdips++;
					break;
				case MULTISWITCH:
				case MULTISWITCH + 1:
				case MULTISWITCH + 2:
				case MULTISWITCH + 3:
					wall[x].picnum++;
					if (wall[x].picnum > (MULTISWITCH + 3))
						wall[x].picnum = MULTISWITCH;
					break;
				case ACCESSSWITCH:
				case ACCESSSWITCH2:
				case SLOTDOOR:
				case LIGHTSWITCH:
				case SPACELIGHTSWITCH:
				case SPACEDOORSWITCH:
				case LIGHTSWITCH2:
				case POWERSWITCH1:
				case LOCKSWITCH1:
				case POWERSWITCH2:
				case PULLSWITCH:
				case HANDSWITCH:
				case DIPSWITCH2:
				case DIPSWITCH3:
					wall[x].picnum++;
					break;
				case HANDSWITCH + 1:
				case PULLSWITCH + 1:
				case LIGHTSWITCH2 + 1:
				case POWERSWITCH1 + 1:
				case LOCKSWITCH1 + 1:
				case POWERSWITCH2 + 1:
				case SLOTDOOR + 1:
				case LIGHTSWITCH + 1:
				case SPACELIGHTSWITCH + 1:
				case SPACEDOORSWITCH + 1:
				case DIPSWITCH2 + 1:
				case DIPSWITCH3 + 1:
					wall[x].picnum--;
					break;
				}
		}

		if (lotag == (short) 65535) {
			LeaveMap();
			if (ud.from_bonus != 0) {
				ud.level_number = ud.from_bonus;
				ud.from_bonus = 0;
			} else {
				ud.level_number++;
			}
			return true;
		}

		switch (picnum) {
		default:
			if (!isadoorwall(picnum))
				break;
		case DIPSWITCH:
		case DIPSWITCH + 1:
		case TECHSWITCH:
		case TECHSWITCH + 1:
		case ALIENSWITCH:
		case ALIENSWITCH + 1:
			if (picnum == DIPSWITCH || picnum == DIPSWITCH + 1 || picnum == ALIENSWITCH || picnum == ALIENSWITCH + 1
					|| picnum == TECHSWITCH || picnum == TECHSWITCH + 1) {
				if (picnum == ALIENSWITCH || picnum == ALIENSWITCH + 1) {
					if (switchtype == 1)
						xyzsound(ALIEN_SWITCH1, w, sx, sy, ps[snum].posz);
					else
						xyzsound(ALIEN_SWITCH1, ps[snum].i, sx, sy, ps[snum].posz);
				} else {
					if (switchtype == 1)
						xyzsound(SWITCH_ON, w, sx, sy, ps[snum].posz);
					else
						xyzsound(SWITCH_ON, ps[snum].i, sx, sy, ps[snum].posz);
				}
				if (numdips != correctdips)
					break;
				xyzsound(END_OF_LEVEL_WARN, ps[snum].i, sx, sy, ps[snum].posz);
			}
		case DIPSWITCH2:
		case DIPSWITCH2 + 1:
		case DIPSWITCH3:
		case DIPSWITCH3 + 1:
		case MULTISWITCH:
		case MULTISWITCH + 1:
		case MULTISWITCH + 2:
		case MULTISWITCH + 3:
		case ACCESSSWITCH:
		case ACCESSSWITCH2:
		case SLOTDOOR:
		case SLOTDOOR + 1:
		case LIGHTSWITCH:
		case LIGHTSWITCH + 1:
		case SPACELIGHTSWITCH:
		case SPACELIGHTSWITCH + 1:
		case SPACEDOORSWITCH:
		case SPACEDOORSWITCH + 1:
		case FRANKENSTINESWITCH:
		case FRANKENSTINESWITCH + 1:
		case LIGHTSWITCH2:
		case LIGHTSWITCH2 + 1:
		case POWERSWITCH1:
		case POWERSWITCH1 + 1:
		case LOCKSWITCH1:
		case LOCKSWITCH1 + 1:
		case POWERSWITCH2:
		case POWERSWITCH2 + 1:
		case HANDSWITCH:
		case HANDSWITCH + 1:
		case PULLSWITCH:
		case PULLSWITCH + 1:

			if (picnum == MULTISWITCH || picnum == (MULTISWITCH + 1) || picnum == (MULTISWITCH + 2)
					|| picnum == (MULTISWITCH + 3))
				lotag += picnum - MULTISWITCH;

			x = headspritestat[3];
			while (x >= 0) {
				if (((sprite[x].hitag) == lotag)) {
					switch (sprite[x].lotag) {
					case 12:
						sector[sprite[x].sectnum].floorpal = 0;
						hittype[x].temp_data[0]++;
						if (hittype[x].temp_data[0] == 2)
							hittype[x].temp_data[0]++;

						break;
					case 24:
					case 34:
					case 25:
						hittype[x].temp_data[4] ^= 1;
						if (hittype[x].temp_data[4] != 0)
							FTA(15, ps[snum]);
						else
							FTA(2, ps[snum]);
						break;
					case 21:
						FTA(2, ps[screenpeek]);
						break;
					}
				}
				x = nextspritestat[x];
			}

			operateactivators(lotag, snum);
			operateforcefields(ps[snum].i, lotag);
			operatemasterswitches(lotag);

			if (picnum == DIPSWITCH || picnum == DIPSWITCH + 1 || picnum == ALIENSWITCH || picnum == ALIENSWITCH + 1
					|| picnum == TECHSWITCH || picnum == TECHSWITCH + 1)
				return true;

			if (hitag == 0 && !isadoorwall(picnum)) {
				if (switchtype == 1)
					xyzsound(SWITCH_ON, w, sx, sy, ps[snum].posz);
				else
					xyzsound(SWITCH_ON, ps[snum].i, sx, sy, ps[snum].posz);
			} else if (hitag != 0 && hitag < NUM_SOUNDS) {
				if (switchtype == 1 && (currentGame.getCON().soundm[hitag] & 4) == 0)
					xyzsound(hitag, w, sx, sy, ps[snum].posz);
				else
					spritesound(hitag, ps[snum].i);
			}

			return true;
		}
		return false;
	}

	public static void activatebysector(int sect, int j) {
		boolean didit = false;

		int i = headspritesect[sect];
		while (i >= 0) {
			if (sprite[i].picnum == ACTIVATOR) {
				operateactivators(sprite[i].lotag, -1);
				didit = true;
			}
			i = nextspritesect[i];
		}

		if (!didit)
			operatesectors(sect, j);
	}

	public static void checkplayerhurt(PlayerStruct p, int j) {
		if ((j & kHitTypeMask) == kHitSprite) {
			j &= kHitIndexMask;

			switch (sprite[j].picnum) {
			case CACTUS:
				if (p.hurt_delay < 8) {
					sprite[p.i].extra -= 5;

					p.hurt_delay = 16;
					p.pals_time = 32;
					p.pals[0] = 32;
					p.pals[1] = 0;
					p.pals[2] = 0;
					spritesound(DUKE_LONGTERM_PAIN, p.i);
				}
				break;
			}
			return;
		}

		if ((j & 49152) != 32768)
			return;
		j &= (MAXWALLS - 1);

		if (p.hurt_delay > 0)
			p.hurt_delay--;
		else if ((wall[j].cstat & 85) != 0)
			switch (wall[j].overpicnum) {
			case W_FORCEFIELD:
			case W_FORCEFIELD + 1:
			case W_FORCEFIELD + 2:
				sprite[p.i].extra -= 5;

				p.hurt_delay = 16;
				p.pals_time = 32;
				p.pals[0] = 32;
				p.pals[1] = 0;
				p.pals[2] = 0;

				if (IsOriginalDemo()) {
					p.posxv = -(sintable[((int) p.ang + 512) & 2047] << 8);
					p.posyv = -(sintable[((int) p.ang) & 2047] << 8);
					spritesound(DUKE_LONGTERM_PAIN, p.i);

					checkhitwall(p.i, j, p.posx + (sintable[((int) p.ang + 512) & 2047] >> 9),
							p.posy + (sintable[(int) p.ang & 2047] >> 9), p.posz, -1);
				} else {
					p.posxv = (int) -(BCosAngle(BClampAngle(p.ang)) * 256.0f);
					p.posyv = (int) -(BSinAngle(BClampAngle(p.ang)) * 256.0f);
					spritesound(DUKE_LONGTERM_PAIN, p.i);

					checkhitwall(p.i, j, (int) (p.posx + (BCosAngle(BClampAngle(p.ang)) / 512.0f)),
							(int) (p.posy + (BSinAngle(BClampAngle(p.ang)) / 512.0f)), p.posz, -1);
				}

				break;

			case BIGFORCE:
				p.hurt_delay = 26;
				if (IsOriginalDemo()) {
					checkhitwall(p.i, j, p.posx + (sintable[((int) p.ang + 512) & 2047] >> 9),
							p.posy + (sintable[(int) p.ang & 2047] >> 9), p.posz, -1);
				} else {
					checkhitwall(p.i, j, (int) (p.posx + (BCosAngle(BClampAngle(p.ang)) / 512.0f)),
							(int) (p.posy + (BSinAngle(BClampAngle(p.ang)) / 512.0f)), p.posz, -1);
				}
				break;

			}
	}

	public static void allignwarpelevators() {
		int i = headspritestat[3];
		while (i >= 0) {
			if (sprite[i].lotag == 17 && sprite[i].shade > 16) {
				int j = headspritestat[3];
				while (j >= 0) {
					if ((sprite[j].lotag) == 17 && i != j && (sprite[i].hitag) == (sprite[j].hitag)) {
						sector[sprite[j].sectnum].floorz = sector[sprite[i].sectnum].floorz;
						sector[sprite[j].sectnum].ceilingz = sector[sprite[i].sectnum].ceilingz;
					}

					j = nextspritestat[j];
				}
			}
			i = nextspritestat[i];
		}
	}

	public static void breakwall(int newpn, int spr, int dawallnum) {
		wall[dawallnum].picnum = (short) newpn;
		spritesound(VENT_BUST, spr);
		spritesound(GLASS_HEAVYBREAK, spr);
		lotsofglass(spr, dawallnum, 10);
	}

	public static void checkhitwall(int spr, int dawallnum, int x, int y, int z, int atwith) {
		int j, i, darkestwall;
		short sn = -1;

		WALL wal = wall[dawallnum];

		if (wal.overpicnum == MIRROR) {
			switch (atwith) {
			case HEAVYHBOMB:
			case RADIUSEXPLOSION:
			case RPG:
			case HYDRENT:
			case SEENINE:
			case OOZFILTER:
			case EXPLODINGBARREL:
				lotsofglass(spr, dawallnum, 70);
				wal.cstat &= ~16;
				wal.overpicnum = MIRRORBROKE;
				spritesound(GLASS_HEAVYBREAK, spr);
				return;
			}
		}

		if (((wal.cstat & 16) != 0 || wal.overpicnum == BIGFORCE) && wal.nextsector >= 0)
			if (sector[wal.nextsector].floorz > z)
				if (sector[wal.nextsector].floorz - sector[wal.nextsector].ceilingz != 0)
					switch (wal.overpicnum) {
					case W_FORCEFIELD:
					case W_FORCEFIELD + 1:
					case W_FORCEFIELD + 2:
						wal.extra = 1; // tell the forces to animate
					case BIGFORCE:
						sn = engine.updatesector(x, y, sn);
						if (sn < 0)
							return;

						if (atwith == -1)
							i = EGS(sn, x, y, z, FORCERIPPLE, -127, 8, 8, 0, 0, 0, spr, (short) 5);
						else {
							if (atwith == CHAINGUN)
								i = EGS(sn, x, y, z, FORCERIPPLE, -127, 16 + sprite[spr].xrepeat,
										16 + sprite[spr].yrepeat, 0, 0, 0, spr, (short) 5);
							else
								i = EGS(sn, x, y, z, FORCERIPPLE, -127, 32, 32, 0, 0, 0, spr, (short) 5);
						}

						sprite[i].cstat |= 18 + 128;
						sprite[i].ang = (short) (engine.getangle(wal.x - wall[wal.point2].x, wal.y - wall[wal.point2].y)
								- 512);

						spritesound(SOMETHINGHITFORCE, i);

						return;

					case FANSPRITE:
						wal.overpicnum = FANSPRITEBROKE;
						wal.cstat &= 65535 - 65;
						if (wal.nextwall >= 0) {
							wall[wal.nextwall].overpicnum = FANSPRITEBROKE;
							wall[wal.nextwall].cstat &= 65535 - 65;
						}
						spritesound(VENT_BUST, spr);
						spritesound(GLASS_BREAKING, spr);
						return;

					case GLASS:
						sn = engine.updatesector(x, y, sn);
						if (sn < 0)
							return;
						wal.overpicnum = GLASS2;
						lotsofglass(spr, dawallnum, 10);
						wal.cstat = 0;

						if (wal.nextwall >= 0)
							wall[wal.nextwall].cstat = 0;

						i = EGS(sn, x, y, z, SECTOREFFECTOR, 0, 0, 0, (short) ps[0].ang, 0, 0, spr, (short) 3);
						sprite[i].lotag = 128;
						hittype[i].temp_data[1] = 5;
						hittype[i].temp_data[2] = dawallnum;
						spritesound(GLASS_BREAKING, i);
						return;
					case STAINGLASS1:
						sn = engine.updatesector(x, y, sn);
						if (sn < 0)
							return;
						lotsofcolourglass(spr, dawallnum, 80);
						wal.cstat = 0;
						if (wal.nextwall >= 0)
							wall[wal.nextwall].cstat = 0;
						spritesound(VENT_BUST, spr);
						spritesound(GLASS_BREAKING, spr);
						return;
					}

		switch (wal.picnum) {
		case COLAMACHINE:
		case VENDMACHINE:
			breakwall(wal.picnum + 2, spr, dawallnum);
			spritesound(VENT_BUST, spr);
			return;

		case OJ:
		case FEMPIC2:
		case FEMPIC3:

		case SCREENBREAK6:
		case SCREENBREAK7:
		case SCREENBREAK8:

		case SCREENBREAK1:
		case SCREENBREAK2:
		case SCREENBREAK3:
		case SCREENBREAK4:
		case SCREENBREAK5:

		case SCREENBREAK9:
		case SCREENBREAK10:
		case SCREENBREAK11:
		case SCREENBREAK12:
		case SCREENBREAK13:
		case SCREENBREAK14:
		case SCREENBREAK15:
		case SCREENBREAK16:
		case SCREENBREAK17:
		case SCREENBREAK18:
		case SCREENBREAK19:
		case BORNTOBEWILDSCREEN:

			lotsofglass(spr, dawallnum, 30);
			wal.picnum = (short) (W_SCREENBREAK + (engine.krand() % 3));
			spritesound(GLASS_HEAVYBREAK, spr);
			return;

		case W_TECHWALL5:
		case W_TECHWALL6:
		case W_TECHWALL7:
		case W_TECHWALL8:
		case W_TECHWALL9:
			breakwall(wal.picnum + 1, spr, dawallnum);
			return;
		case W_MILKSHELF:
			breakwall(W_MILKSHELFBROKE, spr, dawallnum);
			return;

		case W_TECHWALL10:
			breakwall(W_HITTECHWALL10, spr, dawallnum);
			return;

		case W_TECHWALL1:
		case W_TECHWALL11:
		case W_TECHWALL12:
		case W_TECHWALL13:
		case W_TECHWALL14:
			breakwall(W_HITTECHWALL1, spr, dawallnum);
			return;

		case W_TECHWALL15:
			breakwall(W_HITTECHWALL15, spr, dawallnum);
			return;

		case W_TECHWALL16:
			breakwall(W_HITTECHWALL16, spr, dawallnum);
			return;

		case W_TECHWALL2:
			breakwall(W_HITTECHWALL2, spr, dawallnum);
			return;

		case W_TECHWALL3:
			breakwall(W_HITTECHWALL3, spr, dawallnum);
			return;

		case W_TECHWALL4:
			breakwall(W_HITTECHWALL4, spr, dawallnum);
			return;

		case ATM:
			wal.picnum = ATMBROKE;
			lotsofmoney(sprite[spr], 1 + (engine.krand() & 7));
			spritesound(GLASS_HEAVYBREAK, spr);
			break;

		case WALLLIGHT1:
		case WALLLIGHT2:
		case WALLLIGHT3:
		case WALLLIGHT4:
		case TECHLIGHT2:
		case TECHLIGHT4:

			if (rnd(128))
				spritesound(GLASS_HEAVYBREAK, spr);
			else
				spritesound(GLASS_BREAKING, spr);
			lotsofglass(spr, dawallnum, 30);

			if (wal.picnum == WALLLIGHT1)
				wal.picnum = WALLLIGHTBUST1;

			if (wal.picnum == WALLLIGHT2)
				wal.picnum = WALLLIGHTBUST2;

			if (wal.picnum == WALLLIGHT3)
				wal.picnum = WALLLIGHTBUST3;

			if (wal.picnum == WALLLIGHT4)
				wal.picnum = WALLLIGHTBUST4;

			if (wal.picnum == TECHLIGHT2)
				wal.picnum = TECHLIGHTBUST2;

			if (wal.picnum == TECHLIGHT4)
				wal.picnum = TECHLIGHTBUST4;

			if (wal.lotag == 0)
				return;

			sn = wal.nextsector;
			if (sn < 0)
				return;
			darkestwall = 0;

			int startwall = sector[sn].wallptr;
			int endwall = startwall + sector[sn].wallnum;

			for (i = startwall; i < endwall; i++) {
				wal = wall[i];
				if (wal.shade > darkestwall)
					darkestwall = wal.shade;
			}

			j = engine.krand() & 1;
			i = headspritestat[3];
			while (i >= 0) {
				if (sprite[i].hitag == wall[dawallnum].lotag && sprite[i].lotag == 3) {
					hittype[i].temp_data[2] = j;
					hittype[i].temp_data[3] = darkestwall;
					hittype[i].temp_data[4] = 1;
				}
				i = nextspritestat[i];
			}
			break;
		}
	}

	public static boolean checkhitceiling(short sn) {
		int i, j;

		switch (sector[sn].ceilingpicnum) {
		case WALLLIGHT1:
		case WALLLIGHT2:
		case WALLLIGHT3:
		case WALLLIGHT4:
		case TECHLIGHT2:
		case TECHLIGHT4:
			ceilingglass(ps[myconnectindex].i, sn, 10);
			spritesound(GLASS_BREAKING, ps[screenpeek].i);

			if (sector[sn].ceilingpicnum == WALLLIGHT1)
				sector[sn].ceilingpicnum = WALLLIGHTBUST1;

			if (sector[sn].ceilingpicnum == WALLLIGHT2)
				sector[sn].ceilingpicnum = WALLLIGHTBUST2;

			if (sector[sn].ceilingpicnum == WALLLIGHT3)
				sector[sn].ceilingpicnum = WALLLIGHTBUST3;

			if (sector[sn].ceilingpicnum == WALLLIGHT4)
				sector[sn].ceilingpicnum = WALLLIGHTBUST4;

			if (sector[sn].ceilingpicnum == TECHLIGHT2)
				sector[sn].ceilingpicnum = TECHLIGHTBUST2;

			if (sector[sn].ceilingpicnum == TECHLIGHT4)
				sector[sn].ceilingpicnum = TECHLIGHTBUST4;

			if (sector[sn].hitag == 0) {
				i = headspritesect[sn];
				while (i >= 0) {
					if (sprite[i].picnum == SECTOREFFECTOR && sprite[i].lotag == 12) {
						j = headspritestat[3];
						while (j >= 0) {
							if (sprite[j].hitag == sprite[i].hitag)
								hittype[j].temp_data[3] = 1;
							j = nextspritestat[j];
						}
						break;
					}
					i = nextspritesect[i];
				}
			}

			i = headspritestat[3];
			j = engine.krand() & 1;
			while (i >= 0) {
				if (sprite[i].hitag == (sector[sn].hitag) && sprite[i].lotag == 3) {
					hittype[i].temp_data[2] = j;
					hittype[i].temp_data[4] = 1;
				}
				i = nextspritestat[i];
			}

			return true;
		}

		return false;
	}

	public static void checkhitsprite(short i, short sn) {
		short j, k, p;
		SPRITE s;

		i &= (MAXSPRITES - 1);

		switch (sprite[i].picnum) {
		case OCEANSPRITE1:
		case OCEANSPRITE2:
		case OCEANSPRITE3:
		case OCEANSPRITE4:
		case OCEANSPRITE5:
			spawn(i, SMALLSMOKE);
			engine.deletesprite(i);
			break;
		case QUEBALL:
		case STRIPEBALL:
			if (sprite[sn].picnum == QUEBALL || sprite[sn].picnum == STRIPEBALL) {
				sprite[sn].xvel = (short) ((sprite[i].xvel >> 1) + (sprite[i].xvel >> 2));
				sprite[sn].ang -= (sprite[i].ang << 1) + 1024;
				sprite[i].ang = (short) (engine.getangle(sprite[i].x - sprite[sn].x, sprite[i].y - sprite[sn].y) - 512);
				if (Sound[POOLBALLHIT].num < 2)
					spritesound(POOLBALLHIT, i);
			} else {
				if ((engine.krand() & 3) != 0) {
					sprite[i].xvel = 164;
					sprite[i].ang = sprite[sn].ang;
				} else {
					lotsofglass(i, -1, 3);
					engine.deletesprite(i);
				}
			}
			break;
		case TREE1:
		case TREE2:
		case TIRE:
		case CONE:
		case BOX:
			switch (sprite[sn].picnum) {
			case RADIUSEXPLOSION:
			case RPG:
			case FIRELASER:
			case HYDRENT:
			case HEAVYHBOMB:
				if (hittype[i].temp_data[0] == 0) {
					sprite[i].cstat &= ~257;
					hittype[i].temp_data[0] = 1;
					spawn(i, BURNING);
				}
				break;
			}
			break;
		case CACTUS:
			switch (sprite[sn].picnum) {
			case RADIUSEXPLOSION:
			case RPG:
			case FIRELASER:
			case HYDRENT:
			case HEAVYHBOMB:
				for (k = 0; k < 64; k++) {
					int vz = -(engine.krand() & 4095) - (sprite[i].zvel >> 2);
					int ve = (engine.krand() & 63) + 64;
					int va = engine.krand() & 2047;
					int pn = SCRAP3 + (engine.krand() & 3);
					j = EGS(sprite[i].sectnum, sprite[i].x, sprite[i].y, sprite[i].z - (engine.krand() % (48 << 8)), pn,
							-8, 48, 48, va, ve, vz, i, (short) 5);
					sprite[j].pal = 8;
				}

				if (sprite[i].picnum == CACTUS)
					sprite[i].picnum = CACTUSBROKE;
				sprite[i].cstat &= ~257;
				break;
			}
			break;

		case HANGLIGHT:
		case GENERICPOLE2:
			for (k = 0; k < 6; k++) {
				int vz = -(engine.krand() & 4095) - (sprite[i].zvel >> 2);
				int ve = (engine.krand() & 63) + 64;
				int va = engine.krand() & 2047;
				EGS(sprite[i].sectnum, sprite[i].x, sprite[i].y, sprite[i].z - (8 << 8), SCRAP1 + (engine.krand() & 15),
						-8, 48, 48, va, ve, vz, i, (short) 5);
			}
			spritesound(GLASS_HEAVYBREAK, i);
			engine.deletesprite(i);
			break;

		case FANSPRITE:
			sprite[i].picnum = FANSPRITEBROKE;
			sprite[i].cstat &= (65535 - 257);
			if (sector[sprite[i].sectnum].floorpicnum == FANSHADOW)
				sector[sprite[i].sectnum].floorpicnum = FANSHADOWBROKE;

			spritesound(GLASS_HEAVYBREAK, i);
			s = sprite[i];
			for (j = 0; j < 16; j++)
				RANDOMSCRAP(s, i);

			break;
		case WATERFOUNTAIN:
		case WATERFOUNTAIN + 1:
		case WATERFOUNTAIN + 2:
		case WATERFOUNTAIN + 3:
			sprite[i].picnum = WATERFOUNTAINBROKE;
			spawn(i, TOILETWATER);
			break;
		case SATELITE:
		case FUELPOD:
		case SOLARPANNEL:
		case ANTENNA:
			if (sprite[sn].extra != currentGame.getCON().script[currentGame.getCON().actorscrptr[SHOTSPARK1]]) {
				for (j = 0; j < 15; j++) {
					int vz = -(engine.krand() & 511) - 256;
					int ve = (engine.krand() & 127) + 64;
					int va = engine.krand() & 2047;

					EGS(sprite[i].sectnum, sprite[i].x, sprite[i].y,
							sector[sprite[i].sectnum].floorz - (12 << 8) - (j << 9), SCRAP1 + (engine.krand() & 15), -8,
							64, 64, va, ve, vz, i, (short) 5);
				}
				spawn(i, EXPLOSION2);
				engine.deletesprite(i);
			}
			break;
		case BOTTLE1:
		case BOTTLE2:
		case BOTTLE3:
		case BOTTLE4:
		case BOTTLE5:
		case BOTTLE6:
		case BOTTLE8:
		case BOTTLE10:
		case BOTTLE11:
		case BOTTLE12:
		case BOTTLE13:
		case BOTTLE14:
		case BOTTLE15:
		case BOTTLE16:
		case BOTTLE17:
		case BOTTLE18:
		case BOTTLE19:
		case WATERFOUNTAINBROKE:
		case DOMELITE:
		case SUSHIPLATE1:
		case SUSHIPLATE2:
		case SUSHIPLATE3:
		case SUSHIPLATE4:
		case SUSHIPLATE5:
		case WAITTOBESEATED:
		case VASE:
		case STATUEFLASH:
		case STATUE:
			if (sprite[i].picnum == BOTTLE10)
				lotsofmoney(sprite[i], 4 + (engine.krand() & 3));
			else if (sprite[i].picnum == STATUE || sprite[i].picnum == STATUEFLASH) {
				lotsofcolourglass(i, -1, 40);
				spritesound(GLASS_HEAVYBREAK, i);
			} else if (sprite[i].picnum == VASE)
				lotsofglass(i, -1, 40);

			spritesound(GLASS_BREAKING, i);
			sprite[i].ang = (short) (engine.krand() & 2047);
			lotsofglass(i, -1, 8);
			engine.deletesprite(i);
			break;
		case FETUS:
			sprite[i].picnum = FETUSBROKE;
			spritesound(GLASS_BREAKING, i);
			lotsofglass(i, -1, 10);
			break;
		case FETUSBROKE:
			for (j = 0; j < 48; j++) {
				shoot(i, BLOODSPLAT1);
				sprite[i].ang += 333;
			}
			spritesound(GLASS_HEAVYBREAK, i);
			spritesound(SQUISHED, i);
		case BOTTLE7:
			spritesound(GLASS_BREAKING, i);
			lotsofglass(i, -1, 10);
			engine.deletesprite(i);
			break;
		case HYDROPLANT:
			sprite[i].picnum = BROKEHYDROPLANT;
			spritesound(GLASS_BREAKING, i);
			lotsofglass(i, -1, 10);
			break;

		case FORCESPHERE:
			sprite[i].xrepeat = 0;
			hittype[sprite[i].owner].temp_data[0] = 32;
			hittype[sprite[i].owner].temp_data[1] ^= 1;
			hittype[sprite[i].owner].temp_data[2]++;
			spawn(i, EXPLOSION2);
			break;

		case BROKEHYDROPLANT:
			if ((sprite[i].cstat & 1) != 0) {
				spritesound(GLASS_BREAKING, i);
				sprite[i].z += 16 << 8;
				sprite[i].cstat = 0;
				lotsofglass(i, -1, 5);
			}
			break;

		case TOILET:
			sprite[i].picnum = TOILETBROKE;
			sprite[i].cstat |= (engine.krand() & 1) << 2;
			sprite[i].cstat &= ~257;
			spawn(i, TOILETWATER);
			spritesound(GLASS_BREAKING, i);
			break;

		case STALL:
			sprite[i].picnum = STALLBROKE;
			sprite[i].cstat |= (engine.krand() & 1) << 2;
			sprite[i].cstat &= ~257;
			spawn(i, TOILETWATER);
			spritesound(GLASS_HEAVYBREAK, i);
			break;

		case HYDRENT:
			sprite[i].picnum = BROKEFIREHYDRENT;
			spawn(i, TOILETWATER);

			spritesound(GLASS_HEAVYBREAK, i);
			break;

		case GRATE1:
			sprite[i].picnum = BGRATE1;
			sprite[i].cstat &= (65535 - 256 - 1);
			spritesound(VENT_BUST, i);
			break;

		case CIRCLEPANNEL:
			sprite[i].picnum = CIRCLEPANNELBROKE;
			sprite[i].cstat &= (65535 - 256 - 1);
			spritesound(VENT_BUST, i);
			break;
		case PANNEL1:
		case PANNEL2:
			sprite[i].picnum = BPANNEL1;
			sprite[i].cstat &= (65535 - 256 - 1);
			spritesound(VENT_BUST, i);
			break;
		case PANNEL3:
			sprite[i].picnum = BPANNEL3;
			sprite[i].cstat &= (65535 - 256 - 1);
			spritesound(VENT_BUST, i);
			break;
		case PIPE1:
		case PIPE2:
		case PIPE3:
		case PIPE4:
		case PIPE5:
		case PIPE6:
			switch (sprite[i].picnum) {
			case PIPE1:
				sprite[i].picnum = PIPE1B;
				break;
			case PIPE2:
				sprite[i].picnum = PIPE2B;
				break;
			case PIPE3:
				sprite[i].picnum = PIPE3B;
				break;
			case PIPE4:
				sprite[i].picnum = PIPE4B;
				break;
			case PIPE5:
				sprite[i].picnum = PIPE5B;
				break;
			case PIPE6:
				sprite[i].picnum = PIPE6B;
				break;
			}

			j = (short) spawn(i, STEAM);
			sprite[j].z = sector[sprite[i].sectnum].floorz - (32 << 8);
			break;

		case MONK:
		case LUKE:
		case INDY:
		case JURYGUY:
			spritesound(sprite[i].lotag, i);
			spawn(i, sprite[i].hitag);
		case SPACEMARINE:
			sprite[i].extra -= sprite[sn].extra;
			if (sprite[i].extra > 0)
				break;
			sprite[i].ang = (short) (engine.krand() & 2047);
			shoot(i, BLOODSPLAT1);
			sprite[i].ang = (short) (engine.krand() & 2047);
			shoot(i, BLOODSPLAT2);
			sprite[i].ang = (short) (engine.krand() & 2047);
			shoot(i, BLOODSPLAT3);
			sprite[i].ang = (short) (engine.krand() & 2047);
			shoot(i, BLOODSPLAT4);
			sprite[i].ang = (short) (engine.krand() & 2047);
			shoot(i, BLOODSPLAT1);
			sprite[i].ang = (short) (engine.krand() & 2047);
			shoot(i, BLOODSPLAT2);
			sprite[i].ang = (short) (engine.krand() & 2047);
			shoot(i, BLOODSPLAT3);
			sprite[i].ang = (short) (engine.krand() & 2047);
			shoot(i, BLOODSPLAT4);
			guts(sprite[i], JIBS1, 1, myconnectindex);
			guts(sprite[i], JIBS2, 2, myconnectindex);
			guts(sprite[i], JIBS3, 3, myconnectindex);
			guts(sprite[i], JIBS4, 4, myconnectindex);
			guts(sprite[i], JIBS5, 1, myconnectindex);
			guts(sprite[i], JIBS3, 6, myconnectindex);
			sound(SQUISHED);
			engine.deletesprite(i);
			break;
		case CHAIR1:
		case CHAIR2:
			sprite[i].picnum = BROKENCHAIR;
			sprite[i].cstat = 0;
			break;
		case CHAIR3:
		case MOVIECAMERA:
		case SCALE:
		case VACUUM:
		case CAMERALIGHT:
		case IVUNIT:
		case POT1:
		case POT2:
		case POT3:
		case TRIPODCAMERA:
			spritesound(GLASS_HEAVYBREAK, i);
			s = sprite[i];
			for (j = 0; j < 16; j++)
				RANDOMSCRAP(s, i);
			engine.deletesprite(i);
			break;
		case PLAYERONWATER:
			i = sprite[i].owner;
		default:
			if ((sprite[i].cstat & 16) != 0 && sprite[i].hitag == 0 && sprite[i].lotag == 0 && sprite[i].statnum == 0)
				break;

			if ((sprite[sn].picnum == FREEZEBLAST || sprite[sn].owner != i) && sprite[i].statnum != 4) {
				if (badguy(sprite[i])) {
					if (sprite[i].picnum == FIREFLY && sprite[i].xrepeat < 48)
						break;

					if (sprite[sn].picnum == RPG)
						sprite[sn].extra <<= 1;

					if ((sprite[i].picnum != DRONE) && (sprite[i].picnum != ROTATEGUN)
							&& (sprite[i].picnum != COMMANDER)
							&& (sprite[i].picnum < GREENSLIME || sprite[i].picnum > GREENSLIME + 7))
						if (sprite[sn].picnum != FREEZEBLAST)
							if (currentGame.getCON().actortype[sprite[i].picnum] == 0) {
								j = (short) spawn(sn, JIBS6);
								if (sprite[sn].pal == 6)
									sprite[j].pal = 6;
								sprite[j].z += (4 << 8);
								sprite[j].xvel = 16;
								sprite[j].xrepeat = sprite[j].yrepeat = 24;
								sprite[j].ang += 32 - (engine.krand() & 63);
							}

					j = sprite[sn].owner;

					if (j >= 0 && sprite[j].picnum == APLAYER && sprite[i].picnum != ROTATEGUN
							&& sprite[i].picnum != DRONE)
						if (ps[sprite[j].yvel].curr_weapon == SHOTGUN_WEAPON) {
							shoot(i, BLOODSPLAT3);
							shoot(i, BLOODSPLAT1);
							shoot(i, BLOODSPLAT2);
							shoot(i, BLOODSPLAT4);
						}

					if (sprite[i].picnum != TANK && !bossguy(sprite[i].picnum) && sprite[i].picnum != RECON
							&& sprite[i].picnum != ROTATEGUN) {
						if ((sprite[i].cstat & 48) == 0)
							sprite[i].ang = (short) ((sprite[sn].ang + 1024) & 2047);
						sprite[i].xvel = (short) -(sprite[sn].extra << 2);
						j = sprite[i].sectnum;
						if (j >= 0 && j < MAXSECTORS) {
							engine.pushmove(sprite[i].x, sprite[i].y, sprite[i].z, j, 128, (4 << 8), (4 << 8),
									CLIPMASK0);
							sprite[i].x = pushmove_x;
							sprite[i].y = pushmove_y;
							sprite[i].z = pushmove_z;
							j = pushmove_sectnum;

							if (j != sprite[i].sectnum)
								engine.changespritesect(i, j);
						}
					}

					if (sprite[i].statnum == 2) {
						engine.changespritestat(i, (short) 1);
						hittype[i].timetosleep = SLEEPTIME;
					}
					if ((sprite[i].xrepeat < 24 || sprite[i].picnum == SHARK) && sprite[sn].picnum == SHRINKSPARK)
						return;
				}

				if (sprite[i].statnum != 2) {
					if (sprite[sn].picnum == FREEZEBLAST && ((sprite[i].picnum == APLAYER && sprite[i].pal == 1)
							|| (currentGame.getCON().freezerhurtowner == 0 && sprite[sn].owner == i)))
						return;

					int hitpic = sprite[sn].picnum;
					if (isValidSprite(sprite[sn].owner) && sprite[sprite[sn].owner].picnum == APLAYER) {
						if (sprite[i].picnum == APLAYER && ud.coop != 0 && ud.ffire == 0)
							return;

						if (hitpic == FIREBALL && sprite[sprite[i].owner].picnum != FIREBALL)
							hitpic = FLAMETHROWERFLAME;
					}

					hittype[i].picnum = hitpic;
					hittype[i].extra += sprite[sn].extra;
					hittype[i].ang = sprite[sn].ang;
					hittype[i].owner = sprite[sn].owner;
				}

				if (sprite[i].statnum == 10) {
					p = sprite[i].yvel;
					if (ps[p].newowner >= 0) {
						ps[p].newowner = -1;
						ps[p].posx = ps[p].oposx;
						ps[p].posy = ps[p].oposy;
						ps[p].posz = ps[p].oposz;
						ps[p].ang = ps[p].oang;

						System.err.println("a?");
						ps[p].cursectnum = engine.updatesector(ps[p].posx, ps[p].posy, ps[p].cursectnum);
						setpal(ps[p]);

						j = headspritestat[1];
						while (j >= 0) {
							if (sprite[j].picnum == CAMERA1)
								sprite[j].yvel = 0;
							j = nextspritestat[j];
						}
					}

					if (sprite[i].xrepeat < 24 && sprite[sn].picnum == SHRINKSPARK)
						return;

					if (sprite[hittype[i].owner].picnum != APLAYER)
						if (ud.player_skill >= 3)
							sprite[sn].extra += (sprite[sn].extra >> 1);
				}

			}
			break;
		}
	}

	public static void checksectors(int snum) {
		int i = -1, oldz;
		int j, hitscanwall;

		PlayerStruct p = ps[snum];

		if (p.cursectnum != -1) {
			switch (sector[p.cursectnum].lotag) {

			case 32767:
				sector[p.cursectnum].lotag = 0;
				FTA(9, p);
				ps[connecthead].secret_rooms++;
				return;
			case -1:
				LeaveMap();
				sector[p.cursectnum].lotag = 0;
				if (ud.from_bonus != 0) {
					ud.level_number = ud.from_bonus;
					ud.from_bonus = 0;
				} else {
					ud.level_number++;
				}
				return;
			case -2:
				sector[p.cursectnum].lotag = 0;
				p.timebeforeexit = 26 * 8;
				p.customexitsound = sector[p.cursectnum].hitag;
				return;
			default:
				if (sector[p.cursectnum].lotag >= 10000 && sector[p.cursectnum].lotag < 16383) {
					if (snum == screenpeek || ud.coop == 1)
						spritesound(sector[p.cursectnum].lotag - 10000, p.i);
					sector[p.cursectnum].lotag = 0;
				}
				break;

			}
		}

		// After this point the the player effects the map with space

		if (sprite[p.i].extra <= 0)
			return;

		if (ud.cashman != 0 && (sync[snum].bits & (1 << 29)) != 0)
			lotsofmoney(sprite[p.i], 2);

		if (p.newowner >= 0) {
			if (klabs(sync[snum].svel) > 768 || klabs(sync[snum].fvel) > 768) {
				CLEARCAMERAS(p, -1);
				return;
			}
		}

		if ((sync[snum].bits & (1 << 29)) == 0 && (sync[snum].bits & (1 << 31)) == 0)
			p.toggle_key_flag = 0;

		else if (p.toggle_key_flag == 0) {

			if ((sync[snum].bits & (1 << 31)) != 0) {
				if (p.newowner >= 0)
					CLEARCAMERAS(p, -1);
				return;
			}

			neartagsprite = -1;
			p.toggle_key_flag = 1;
			hitscanwall = -1;

			i = hitawall(p);
			hitscanwall = pHitInfo.hitwall;

			if (i < 1280 && hitscanwall >= 0 && wall[hitscanwall].overpicnum == MIRROR)
				if (wall[hitscanwall].lotag > 0 && Sound[wall[hitscanwall].lotag].num == 0 && snum == screenpeek) {
					spritesound(wall[hitscanwall].lotag, p.i);
					return;
				}

			if (hitscanwall >= 0 && (wall[hitscanwall].cstat & 16) != 0)
				switch (wall[hitscanwall].overpicnum) {
				default:
					if (wall[hitscanwall].lotag != 0)
						return;
				}

			if (p.newowner >= 0)
				neartag(p.oposx, p.oposy, p.oposz, sprite[p.i].sectnum, (short) p.oang, 1280, 1);
			else {
				neartag(p.posx, p.posy, p.posz, sprite[p.i].sectnum, (short) p.oang, 1280, 1);
				if (neartagsprite == -1 && neartagwall == -1 && neartagsector == -1)
					neartag(p.posx, p.posy, p.posz + (8 << 8), sprite[p.i].sectnum, (short) p.oang, 1280, 1);
				if (neartagsprite == -1 && neartagwall == -1 && neartagsector == -1)
					neartag(p.posx, p.posy, p.posz + (16 << 8), sprite[p.i].sectnum, (short) p.oang, 1280, 1);
				if (neartagsprite == -1 && neartagwall == -1 && neartagsector == -1) {
					neartag(p.posx, p.posy, p.posz + (16 << 8), sprite[p.i].sectnum, (short) p.oang, 1280, 3);
					if (neartagsprite >= 0) {
						switch (sprite[neartagsprite].picnum) {
						case FEM1:
						case FEM2:
						case FEM3:
						case FEM4:
						case FEM5:
						case FEM6:
						case FEM7:
						case FEM8:
						case FEM9:
						case FEM10:
						case PODFEM1:
						case NAKED1:
						case STATUE:
						case TOUGHGAL:
							return;
						}
					}

					neartagsprite = -1;
					neartagwall = -1;
					neartagsector = -1;
				}
			}

			if (p.newowner == -1 && neartagsprite == -1 && neartagsector == -1 && neartagwall == -1)
				if (isanunderoperator(sector[sprite[p.i].sectnum].lotag))
					neartagsector = sprite[p.i].sectnum;

			if (neartagsector >= 0 && (sector[neartagsector].lotag & 16384) != 0)
				return;

			if (neartagsprite == -1 && neartagwall == -1)
				if (sector[p.cursectnum].lotag == 2) {
					oldz = hitasprite(p.i);
					neartagsprite = pHitInfo.hitsprite;
					if (oldz > 1280)
						neartagsprite = -1;
				}

			if (neartagsprite >= 0) {
				if (checkhitswitch(snum, neartagsprite, 1))
					return;

				switch (sprite[neartagsprite].picnum) {
				case TOILET:
				case STALL:
					if (p.last_pissed_time == 0) {
						if (ud.lockout == 0)
							spritesound(DUKE_URINATE, p.i);

						p.last_pissed_time = 26 * 220;
						p.transporter_hold = 29 * 2;
						if (p.holster_weapon == 0) {
							p.holster_weapon = 1;
							p.weapon_pos = -1;
						}
						if (sprite[p.i].extra <= (currentGame.getCON().max_player_health
								- (currentGame.getCON().max_player_health / 10))) {
							sprite[p.i].extra += currentGame.getCON().max_player_health / 10;
							p.last_extra = sprite[p.i].extra;
						} else if (sprite[p.i].extra < currentGame.getCON().max_player_health)
							sprite[p.i].extra = (short) currentGame.getCON().max_player_health;
					} else if (Sound[FLUSH_TOILET].num == 0)
						spritesound(FLUSH_TOILET, p.i);
					return;

				case NUKEBUTTON:
					hitawall(p);
					j = pHitInfo.hitwall;
					if (j >= 0 && wall[j].overpicnum == 0)
						if (hittype[neartagsprite].temp_data[0] == 0) {
							hittype[neartagsprite].temp_data[0] = 1;
							sprite[neartagsprite].owner = p.i;
							p.buttonpalette = sprite[neartagsprite].pal;
							if (p.buttonpalette != 0)
								ud.secretlevel = sprite[neartagsprite].lotag;
							else
								ud.secretlevel = 0;
						}
					return;
				case WATERFOUNTAIN:
					if (hittype[neartagsprite].temp_data[0] != 1) {
						hittype[neartagsprite].temp_data[0] = 1;
						sprite[neartagsprite].owner = p.i;

						if (sprite[p.i].extra < currentGame.getCON().max_player_health) {
							sprite[p.i].extra++;
							spritesound(DUKE_DRINKING, p.i);
						}
					}
					return;
				case PLUG:
					spritesound(SHORT_CIRCUIT, p.i);
					sprite[p.i].extra -= 2 + (engine.krand() & 3);
					p.pals[0] = 48;
					p.pals[1] = 48;
					p.pals[2] = 64;
					p.pals_time = 32;
					break;
				case VIEWSCREEN:
				case VIEWSCREEN2: {
					i = headspritestat[1];

					while (i >= 0) {
						if (sprite[i].picnum == CAMERA1 && sprite[i].yvel == 0
								&& sprite[neartagsprite].hitag == sprite[i].lotag) {
							sprite[i].yvel = 1; // Using this camera
							spritesound(MONITOR_ACTIVE, neartagsprite);

							sprite[neartagsprite].owner = (short) i;
							sprite[neartagsprite].yvel = 1;
							hittype[p.i].tempang = sprite[i].ang;
							p.posx = sprite[i].x;
							p.posy = sprite[i].y;
							p.posz = sprite[i].z;
							p.ang = sprite[i].ang;
							j = p.cursectnum;
							p.cursectnum = sprite[i].sectnum;
							setpal(p);
							p.cursectnum = (short) j;
							p.newowner = (short) i;
							return;
						}
						i = nextspritestat[i];
					}
				}

					CLEARCAMERAS(p, i);
					return;
				}
			}

			if ((sync[snum].bits & (1 << 29)) == 0)
				return;
			else if (p.newowner >= 0) {
				i = -1;
				CLEARCAMERAS(p, i);
				return;
			}

			if (neartagwall == -1 && neartagsector == -1 && neartagsprite == -1)
				if (klabs(hits(p.i)) < 512) {
					if ((engine.krand() & 255) < 16)
						spritesound(DUKE_SEARCH2, p.i);
					else
						spritesound(DUKE_SEARCH, p.i);
					return;
				}

			if (neartagwall >= 0) {
				if (wall[neartagwall].lotag > 0 && isadoorwall(wall[neartagwall].picnum)) {
					if (hitscanwall == neartagwall || hitscanwall == -1)
						checkhitswitch(snum, neartagwall, 0);
					return;
				} else if (p.newowner >= 0) {
					CLEARCAMERAS(p, -1);
					return;
				}
			}

			if (neartagsector >= 0 && (sector[neartagsector].lotag & 16384) == 0
					&& isanearoperator(sector[neartagsector].lotag)) {
				i = headspritesect[neartagsector];
				while (i >= 0) {
					if (sprite[i].picnum == ACTIVATOR || sprite[i].picnum == MASTERSWITCH)
						return;
					i = nextspritesect[i];
				}
				operatesectors(neartagsector, p.i);
			} else if ((sector[sprite[p.i].sectnum].lotag & 16384) == 0) {
				if (isanunderoperator(sector[sprite[p.i].sectnum].lotag)) {
					i = headspritesect[sprite[p.i].sectnum];
					while (i >= 0) {
						if (sprite[i].picnum == ACTIVATOR || sprite[i].picnum == MASTERSWITCH)
							return;
						i = nextspritesect[i];
					}
					operatesectors(sprite[p.i].sectnum, p.i);
				} else
					checkhitswitch(snum, neartagwall, 0);
			}
		}
	}

	public static void CLEARCAMERAS(PlayerStruct p, int i) {
		if (i < 0) {
			p.posx = p.oposx;
			p.posy = p.oposy;
			p.posz = p.oposz;
			p.ang = p.oang;
			p.newowner = -1;

			p.cursectnum = engine.updatesector(p.posx, p.posy, p.cursectnum);
			setpal(p);

			p.UpdatePlayerLoc();

			i = headspritestat[1];
			while (i >= 0) {
				if (sprite[i].picnum == CAMERA1)
					sprite[i].yvel = 0;
				i = nextspritestat[i];
			}
		} else if (p.newowner >= 0)
			p.newowner = -1;
	}

	public static void setsectinterpolate(int i) {
		int j, k, startwall, endwall;

		startwall = sector[sprite[i].sectnum].wallptr;
		endwall = startwall + sector[sprite[i].sectnum].wallnum;

		for (j = startwall; j < endwall; j++) {
			game.pInt.setwallinterpolate(j, wall[j]);
			k = wall[j].nextwall;
			if (k >= 0) {
				game.pInt.setwallinterpolate(k, wall[k]);
				k = wall[k].point2;
				game.pInt.setwallinterpolate(k, wall[k]);
			}
		}
	}
}
