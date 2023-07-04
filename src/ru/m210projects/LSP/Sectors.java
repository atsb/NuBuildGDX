// This file is part of LSPGDX.
// Copyright (C) 2020  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// LSPGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// LSPGDX is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with LSPGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.LSP;

import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Build.Net.Mmulti.connecthead;
import static ru.m210projects.Build.Net.Mmulti.connectpoint2;
import static ru.m210projects.LSP.Globals.*;
import static ru.m210projects.LSP.Sprites.*;
import static ru.m210projects.LSP.Animate.*;
import static ru.m210projects.LSP.Types.ANIMATION.*;
import static ru.m210projects.LSP.Animate.setanimation;
import static ru.m210projects.LSP.Factory.LSPMenuHandler.*;
import static ru.m210projects.LSP.Sounds.*;
import static ru.m210projects.LSP.Main.*;

import ru.m210projects.Build.Engine.Point;
import ru.m210projects.Build.Types.BuildPos;
import ru.m210projects.Build.Types.SPRITE;

public class Sectors {

	public static void tagcode() {
		int i, j, k, l, s, dax, day, cnt, good;
		short startwall, endwall, dasector, p, oldang, w;

		for (p = connecthead; p >= 0; p = connectpoint2[p]) {
			if (gPlayer[p].sectnum == -1)
				continue;

			if (mapnum == 0) {
				if (sector[gPlayer[p].sectnum].lotag == 77) {
					bActiveTrain = true;
					bTrainSoundSwitch = true;
				}

				if (sector[gPlayer[p].sectnum].lotag == 78) {
					wall[nTrainWall].cstat = 17;
					bActiveTrain = false;
					bTrainSoundSwitch = false;
				}

				if (gPlayer[p].osectnum != gPlayer[p].sectnum) {
					if (sector[gPlayer[p].sectnum].lotag == 81) { // advertising
						game.menu.mOpen(game.menu.mMenus[ADVERTISING], -1);
					}

					if (sector[gPlayer[p].sectnum].lotag == 82) { // load game
						game.menu.mOpen(game.menu.mMenus[LOADGAME], -1);
					}
				}
			}

			if (sector[gPlayer[p].sectnum].lotag == 1
					|| (sector[gPlayer[p].sectnum].lotag == 2 && gPlayer[p].osectnum != gPlayer[p].sectnum)) {
				for (i = 0; i < numsectors; i++) {
					if (sector[i].hitag == sector[gPlayer[p].sectnum].hitag && sector[i].lotag != 1) {
						operatesector(i);
					}
				}

				if (sector[gPlayer[p].sectnum].lotag == 1)
					sector[gPlayer[p].sectnum].lotag = 0;

				if (neartag.tagsprite != -1) {
					i = headspritestat[0];
					while (i != -1) {
						int nexti = nextspritestat[i];
						if (sprite[i].hitag == sprite[neartag.tagsprite].hitag)
							operatesprite(i);
						i = nexti;
					}
				}

			} else {
				switch (sector[gPlayer[p].sectnum].lotag) { // new level
				case 99:
					nNextMap = 1;
					break;
				case 98:
					nNextMap = 2;
					break;
				case 97:
					nNextMap = 3;
					break;
				}

				if (nNextMap != 0) {
					if (recstat == 1 && rec != null)
						rec.close();

					startmusic(1);
					stopallsounds();
					game.changeScreen(gStatisticScreen.setSkipping(new Runnable() {
						@Override
						public void run() {
							globalvisibility = 15;
							if (gGameScreen.NextMap()) {
								if (gMovieScreen.init("LFART000.DAT")) {
									gMovieScreen.setCallback(new Runnable() {
										@Override
										public void run() {
											game.show();
										}
									});
									game.changeScreen(gMovieScreen.escSkipping(true));
								} else game.show();
							}
						}
					}));
				}
			}
			gPlayer[p].osectnum = gPlayer[p].sectnum;
		}

		for (i = 0; i < warpsector2cnt; i++) {
			dasector = warpsector2list[i];
			j = (int) ((lockclock & 127) >> 2);
			if (j >= 16)
				j = 31 - j;
			{
				sector[dasector].ceilingshade = (byte) j;
				sector[dasector].floorshade = (byte) j;
				startwall = sector[dasector].wallptr;
				endwall = (short) (startwall + sector[dasector].wallnum - 1);
				for (s = startwall; s <= endwall; s++)
					wall[s].shade = (byte) j;
			}
		}

		for (i = 0; i < warpsectorcnt; i++) {
			dasector = warpsectorlist[i];
			j = (int) ((lockclock & 127) >> 2);
			if (j >= 16)
				j = 31 - j;
			{
				sector[dasector].ceilingshade = (byte) j;
				sector[dasector].floorshade = (byte) j;
				startwall = sector[dasector].wallptr;
				endwall = (short) (startwall + sector[dasector].wallnum - 1);
				for (s = startwall; s <= endwall; s++)
					wall[s].shade = (byte) j;
			}
		}

		for (p = connecthead; p >= 0; p = connectpoint2[p])
			if (gPlayer[p].sectnum >= 0 && sector[gPlayer[p].sectnum].lotag == 10) // warp sector
			{
				if (gPlayer[p].word_586FC == 0) {
//					sndPlaySound("warp.wav", sub_1D021(gPlayer[p].x, gPlayer[p].y)); XXX

					for (i = 0; i < warpsectorcnt; i++) {
						if (warpsectorlist[i] == gPlayer[p].sectnum) {
							if (i == warpsectorcnt - 1)
								gPlayer[p].sectnum = warpsectorlist[0];
							else
								gPlayer[p].sectnum = warpsectorlist[i + 1];
							break;
						}
					}

					// find center of sector
					startwall = sector[gPlayer[p].sectnum].wallptr;
					endwall = (short) (startwall + sector[gPlayer[p].sectnum].wallnum - 1);
					dax = 0;
					day = 0;
					for (s = startwall; s <= endwall; s++) {
						dax += wall[s].x;
						day += wall[s].y;
						if (wall[s].nextsector >= 0) {
							i = s;
						}
					}
					gPlayer[p].x = dax / (endwall - startwall + 1);
					gPlayer[p].y = day / (endwall - startwall + 1);
					gPlayer[p].z = sector[gPlayer[p].sectnum].floorz - 0x2000;
					gPlayer[p].sectnum = engine.updatesector(gPlayer[p].x, gPlayer[p].y, gPlayer[p].sectnum);

					dax = (wall[wall[i].point2].x + wall[i].x) >> 1;
					day = (wall[wall[i].point2].y + wall[i].y) >> 1;

					gPlayer[p].ang = engine.getangle(dax - gPlayer[p].x, day - gPlayer[p].y);

					engine.setsprite(gPlayer[p].nSprite, gPlayer[p].x, gPlayer[p].y, gPlayer[p].z + 0x2000);
					sprite[gPlayer[0].nSprite].ang = (short) gPlayer[p].ang;
					gPlayer[0].word_586FC = 1;
				}
			} else
				gPlayer[p].word_586FC = 0;

		for (i = 0; i < xpanningsectorcnt; i++) {
			dasector = xpanningsectorlist[i];
			startwall = sector[dasector].wallptr;
			endwall = (short) (startwall + sector[dasector].wallnum - 1);
			for (s = startwall; s <= endwall; s++) {
				wall[s].xpanning = (byte) ((lockclock >> 2) & 255);
			}
		}

		for (i = 0; i < ypanningwallcnt; i++) {
			wall[ypanningwalllist[i]].ypanning = (byte) ~(lockclock & 255);
		}

		for (i = 0; i < floorpanningcnt; i++) {
			sector[floorpanninglist[i]].floorxpanning = (byte) ((lockclock >> 2) & 255);
			sector[floorpanninglist[i]].floorypanning = (byte) ((lockclock >> 2) & 255);
		}

		for (i = 0; i < dragsectorcnt; i++) {
			dasector = dragsectorlist[i];
			startwall = sector[dasector].wallptr;
			endwall = (short) (startwall + sector[dasector].wallnum - 1);
			if (wall[startwall].x + dragxdir[i] < dragx1[i])
				dragxdir[i] = 16;
			if (wall[startwall].y + dragydir[i] < dragy1[i])
				dragydir[i] = 16;
			if (wall[startwall].x + dragxdir[i] > dragx2[i])
				dragxdir[i] = -16;
			if (wall[startwall].y + dragydir[i] > dragy2[i])
				dragydir[i] = -16;
			for (w = startwall; w <= endwall; w++) {
				engine.dragpoint(w, wall[w].x + dragxdir[i], wall[w].y + dragydir[i]);
			}

//			sector[dasector].floorxpanning -= dragxdir[i] >> 2;
//			sector[dasector].floorypanning += dragydir[i] >> 2;

			j = sector[dasector].floorz;
			game.pInt.setfloorinterpolate(dasector, sector[dasector]);
			sector[dasector].floorz = dragfloorz[i] + (sintable[(int) ((lockclock << 4) & 2047)] >> 3);
			for (p = connecthead; p >= 0; p = connectpoint2[p]) {
				if (gPlayer[p].sectnum == dasector) {
					gPlayer[p].x += dragxdir[i];
					gPlayer[p].y += dragydir[i];
					gPlayer[p].z += (sector[dasector].floorz - j);
					engine.setsprite(gPlayer[p].nSprite, gPlayer[p].x, gPlayer[p].y, gPlayer[p].z + 0x2000);
					sprite[gPlayer[p].nSprite].ang = (short) gPlayer[p].ang;
				}
			}
		}

		for (i = 0; i < swingcnt; i++) {
			if (swingdoor[i].anginc != 0) {
				oldang = (short) swingdoor[i].ang;
				for (j = 0; j < (TICSPERFRAME << 2); j++) {
					swingdoor[i].ang = ((swingdoor[i].ang + 2048 + swingdoor[i].anginc) & 2047);
					if (swingdoor[i].ang == swingdoor[i].angclosed) {
						playsound(21);
						swingdoor[i].anginc = 0;
					}
					if (swingdoor[i].ang == swingdoor[i].angopen) {
						swingdoor[i].anginc = 0;
					}
				}
				for (k = 1; k <= 3; k++) {
					Point out = engine.rotatepoint(swingdoor[i].x[0], swingdoor[i].y[0], swingdoor[i].x[k],
							swingdoor[i].y[k], (short) swingdoor[i].ang);

					engine.dragpoint((short) swingdoor[i].wall[k], out.getX(), out.getY());
				}

				if (swingdoor[i].anginc != 0) {
					for (p = connecthead; p >= 0; p = connectpoint2[p])
						if (gPlayer[p].sectnum >= 0 && ((gPlayer[p].sectnum == swingdoor[i].sector))) {
							cnt = 256;
							do {
								good = 1;

								// swingangopendir is -1 if forwards, 1 is backwards
								l = (swingdoor[i].angopendir > 0) ? 1 : 0;
								for (k = l + 3; k >= l; k--) {
									if (engine.clipinsidebox(gPlayer[p].x + 64, gPlayer[p].y + 64,
											(short) swingdoor[i].wall[k], 128) != 0) {
										good = 0;
										break;
									}
								}

								if (good == 0) {
									if (cnt == 256) {
										swingdoor[i].anginc = (short) -swingdoor[i].anginc;
										swingdoor[i].ang = oldang;
									} else {
										swingdoor[i].ang = (short) ((swingdoor[i].ang + 2048 - swingdoor[i].anginc)
												& 2047);
									}
									for (k = 1; k <= 3; k++) {
										Point out = engine.rotatepoint(swingdoor[i].x[0], swingdoor[i].y[0],
												swingdoor[i].x[k], swingdoor[i].y[k], (short) swingdoor[i].ang);

										engine.dragpoint((short) swingdoor[i].wall[k], out.getX(), out.getY());
									}
									if (swingdoor[i].ang == swingdoor[i].angclosed) {
										swingdoor[i].anginc = 0;
										break;
									}
									if (swingdoor[i].ang == swingdoor[i].angopen) {
										swingdoor[i].anginc = 0;
										break;
									}
									cnt--;
								}
							} while ((good == 0) && (cnt > 0));
						}
				}
			}
		}

		for (i = 0; i < revolvecnt; i++) {
			startwall = sector[revolvesector[i]].wallptr;
			endwall = (short) (startwall + sector[revolvesector[i]].wallnum - 1);

			revolveang[i] = (short) ((revolveang[i] + 2048 - (TICSPERFRAME << 2)) & 2047);
			for (w = startwall; w <= endwall; w++) {
				Point out = engine.rotatepoint(revolvepivotx[i], revolvepivoty[i], revolvex[i][w - startwall],
						revolvey[i][w - startwall], revolveang[i]);
				dax = out.getX();
				day = out.getY();
				engine.dragpoint(w, dax, day);
			}

			for (p = connecthead; p >= 0; p = connectpoint2[p]) {
				if (gPlayer[p].sectnum != revolvesector[i]) {
					// XXX
				}
			}
		}

		if (bActiveTrain) {
			for (i = 0; i < subwaytrackcnt; i++) {
				dasector = subwaytracksector[i][0];
				startwall = sector[dasector].wallptr;
				endwall = (short) (startwall + sector[dasector].wallnum - 1);
				for (k = startwall; k <= endwall; k++) {
					game.pInt.setwallinterpolate(k, wall[k]);
					if (wall[k].x > subwaytrackx1[i])
						if (wall[k].y > subwaytracky1[i])
							if (wall[k].x < subwaytrackx2[i])
								if (wall[k].y < subwaytracky2[i])
									wall[k].x += (subwayvel[i] & 0xfffffffc);
				}

				for (j = 1; j < subwaynumsectors[i]; j++) {
					dasector = subwaytracksector[i][j];

					startwall = sector[dasector].wallptr;
					endwall = (short) (startwall + sector[dasector].wallnum - 1);
					for (k = startwall; k <= endwall; k++) {
						game.pInt.setwallinterpolate(k, wall[k]);
						wall[k].x += (subwayvel[i] & 0xfffffffc);
					}

					s = headspritesect[dasector];
					while (s != -1) {
						k = nextspritesect[s];
						game.pInt.setsprinterpolate(s, sprite[s]);
						sprite[s].x += (subwayvel[i] & 0xfffffffc);
						s = k;
					}
				}

				for (p = connecthead; p >= 0; p = connectpoint2[p])
					if (gPlayer[p].sectnum >= 0 && gPlayer[p].sectnum != subwaytracksector[i][0])
						if (sector[gPlayer[p].sectnum].floorz != sector[subwaytracksector[i][0]].floorz)
							if (gPlayer[p].x > subwaytrackx1[i])
								if (gPlayer[p].y > subwaytracky1[i])
									if (gPlayer[p].x < subwaytrackx2[i])
										if (gPlayer[p].y < subwaytracky2[i]) {
											gPlayer[p].x += (subwayvel[i] & 0xfffffffc);

											
//											if ( totalclock - dword_5AF58 > 200 )
//								            {
//								              playsound(97);
//								              dword_5AF58 = totalclock;
//								            }
											
											// Update sprite representation of player
											engine.setsprite(gPlayer[p].nSprite, gPlayer[p].x, gPlayer[p].y,
													gPlayer[p].z + 0x2000);
											sprite[gPlayer[p].nSprite].ang = (short) gPlayer[p].ang;
										}

				subwayx[i] += (subwayvel[i] & 0xfffffffc);
				if (subwaygoalstop[i] >= 8 || subwaygoalstop[i] < 0)
					continue;

				k = subwaystop[i][subwaygoalstop[i]] - subwayx[i];
				if (k > 0) {
					if (k > 2048) {
						if (subwayvel[i] < 128)
							subwayvel[i]++;
					} else
						subwayvel[i] = (k >> 4) + 1;
				} else if (k < 0) {
					if (k < -2048) {
						if (subwayvel[i] > -128)
							subwayvel[i]--;
					} else
						subwayvel[i] = ((k >> 4) - 1);
				}

				if (bTrainSoundSwitch && (k <= 320 && k >= 280 || k >= -320 && k <= -280)) {
					if (gPlayer[0].x < 0 && subwaygoalstop[i] == 0 || gPlayer[0].x > 0 && subwaygoalstop[i] == 1)
						playsound(96);
					bTrainSoundSwitch = false;
				} else if (!bTrainSoundSwitch) {
					if (k <= 44 && k >= -44 && subwaypausetime[i] < 490) {
						if (gPlayer[0].x < 0 && subwaygoalstop[i] == 0 || gPlayer[0].x > 0 && subwaygoalstop[i] == 1)
							playsound(95);
						bTrainSoundSwitch = true;
					}
				}

				if (((subwayvel[i] >> 2) == 0) && (Math.abs(k) < 2048)) {
					if (subwaypausetime[i] == 2400) {
						for (j = 1; j < subwaynumsectors[i]; j++) // Open all subway doors
						{
							dasector = subwaytracksector[i][j];
							if (sector[dasector].lotag == 17) {
								sector[dasector].lotag = 16;
								playsound(98);
								operatesector(dasector);
								sector[dasector].lotag = 17;
							}
						}
					}
			
					if ((subwaypausetime[i] >= 480) && (subwaypausetime[i] - TICSPERFRAME < 480)) {
						for (j = 1; j < subwaynumsectors[i]; j++) // Close all subway doors
						{
							dasector = subwaytracksector[i][j];
							if (sector[dasector].lotag == 17) {
								sector[dasector].lotag = 16;
								playsound(99);
								operatesector(dasector);
								sector[dasector].lotag = 17;
							}
						}
					}

					subwaypausetime[i] -= TICSPERFRAME;
					if (subwaypausetime[i] < 0) {
						subwaypausetime[i] = 2400;
						if (subwayvel[i] < 0 && --subwaygoalstop[i] < 0)
							subwaygoalstop[i] = 1;
						if (subwayvel[i] > 0) {
							if (++subwaygoalstop[i] >= subwaystopcnt[i]) {
								subwaygoalstop[i] = subwaystopcnt[i] - 2;
							}
						}
					}
				}
			}
		}
	}

	private static final short[] opwallfind = new short[2];

	public static void operatesector(int dasector) { // Door code
		int i, j;
		int dax2, day2, centx, centy, goalz;
		short startwall, endwall;

		int datag = sector[dasector].lotag;
		startwall = sector[dasector].wallptr;
		endwall = (short) (startwall + sector[dasector].wallnum - 1);
		centx = 0;
		centy = 0;
		for (i = startwall; i <= endwall; i++) {
			centx += wall[i].x;
			centy += wall[i].y;
		}
		centx /= (endwall - startwall + 1);
		centy /= (endwall - startwall + 1);

		if (datag == 6 || datag == 8) { // ceil door, split door
			i = getanimationgoal(sector[dasector], CEILZ);
			if (i >= 0) {
				if (datag == 8)
					goalz = (sector[dasector].floorz + sector[dasector].ceilingz) >> 1;
				else
					goalz = sector[dasector].floorz;

				if (goalz == gAnimationData[i].goal)
					gAnimationData[i].goal = sector[engine.nextsectorneighborz(dasector, sector[dasector].floorz, -1,
							-1)].ceilingz;
				else
					gAnimationData[i].goal = goalz;
			} else {
				int s = engine.nextsectorneighborz(dasector, sector[dasector].ceilingz, -1, -1);
				if (sector[dasector].ceilingz == sector[dasector].floorz && s != -1) {
					goalz = sector[s].ceilingz;
				} else if (datag == 8)
					goalz = (sector[dasector].floorz + sector[dasector].ceilingz) >> 1;
				else
					goalz = sector[dasector].floorz;

				if (setanimation(dasector, goalz, 128, 0, CEILZ) >= 0)
					playsound(74);
			}
		}

		if (datag == 7 || datag == 8) { // floor door, split door
			i = getanimationgoal(sector[dasector], FLOORZ);
			if (i >= 0) {
				if (datag == 8)
					goalz = (sector[dasector].floorz + sector[dasector].ceilingz) >> 1;
				else
					goalz = sector[dasector].ceilingz;

				if (gAnimationData[i].goal == goalz) {
					gAnimationData[i].goal = sector[engine.nextsectorneighborz(dasector, sector[dasector].ceilingz, 1,
							1)].floorz;
				} else {
					gAnimationData[i].goal = goalz;
				}
			} else {
				int s = engine.nextsectorneighborz(dasector, sector[dasector].ceilingz, 1, 1);
				if (sector[dasector].ceilingz == sector[dasector].floorz && s != -1) {
					goalz = sector[s].floorz;
				} else if (datag == 8)
					goalz = (sector[dasector].floorz + sector[dasector].ceilingz) >> 1;
				else {
					goalz = sector[dasector].ceilingz;
				}
				if (setanimation(dasector, goalz, 128, 0, FLOORZ) >= 0)
					playsound(76);
			}
		}

		if (datag == 20 || datag == 21) { // elevators
			i = getanimationgoal(sector[dasector], FLOORZ);
			if (i >= 0) {
				goalz = sector[engine.nextsectorneighborz(dasector, sector[dasector].ceilingz, 1, 1)].floorz;
				if (goalz == gAnimationData[i].goal)
					gAnimationData[i].goal = sector[engine.nextsectorneighborz(dasector, sector[dasector].ceilingz, 1,
							1)].floorz;
				else
					gAnimationData[i].goal = goalz;
			} else {
				int sect = engine.nextsectorneighborz(dasector, sector[dasector].floorz, 1, 1);
				if (sect == -1) {
					if (datag == 20)
						sect = engine.nextsectorneighborz(dasector, sector[dasector].floorz, -1, -1);
					else
						sect = engine.nextsectorneighborz(dasector, sector[dasector].floorz, 1, -1);
				}

				if (sect != -1 && setanimation(dasector, sector[sect].floorz, 128, 0, FLOORZ) >= 0)
					playsound(78);
			}
		}

		if (datag == 9) {
			opwallfind[0] = -1;
			opwallfind[1] = -1;
			for (i = startwall; i <= endwall; i++)
				if ((wall[i].x == centx) || (wall[i].y == centy)) {
					if (opwallfind[0] == -1)
						opwallfind[0] = (short) i;
					else
						opwallfind[1] = (short) i;
				}

			for (j = 0; j < 2; j++) {
				if ((wall[opwallfind[j]].x == centx) && (wall[opwallfind[j]].y == centy)) {
					i = opwallfind[j] - 1;
					if (i < startwall)
						i = endwall;
					dax2 = ((wall[i].x + wall[wall[opwallfind[j]].point2].x) >> 1) - wall[opwallfind[j]].x;
					day2 = ((wall[i].y + wall[wall[opwallfind[j]].point2].y) >> 1) - wall[opwallfind[j]].y;
					if (dax2 != 0) {
						dax2 = wall[wall[wall[opwallfind[j]].point2].point2].x;
						dax2 -= wall[wall[opwallfind[j]].point2].x;
						setanimation(opwallfind[j], wall[opwallfind[j]].x + dax2, 4, 0, WALLX);
						setanimation(i, wall[i].x + dax2, 4, 0, WALLX);
						setanimation(wall[opwallfind[j]].point2, wall[wall[opwallfind[j]].point2].x + dax2, 4, 0,
								WALLX);
					} else if (day2 != 0) {
						day2 = wall[wall[wall[opwallfind[j]].point2].point2].y;
						day2 -= wall[wall[opwallfind[j]].point2].y;
						setanimation(opwallfind[j], wall[opwallfind[j]].y + day2, 4, 0, WALLY);
						setanimation(i, wall[i].y + day2, 4, 0, WALLY);
						setanimation(wall[opwallfind[j]].point2, wall[wall[opwallfind[j]].point2].y + day2, 4, 0,
								WALLY);
					}
				} else {
					i = opwallfind[j] - 1;
					if (i < startwall)
						i = endwall;
					dax2 = ((wall[i].x + wall[wall[opwallfind[j]].point2].x) >> 1) - wall[opwallfind[j]].x;
					day2 = ((wall[i].y + wall[wall[opwallfind[j]].point2].y) >> 1) - wall[opwallfind[j]].y;
					if (dax2 != 0) {
						setanimation(opwallfind[j], centx, 4, 0, WALLX);
						setanimation(i, centx + dax2, 4, 0, WALLX);
						setanimation(wall[opwallfind[j]].point2, centx + dax2, 4, 0, WALLX);
					} else if (day2 != 0) {
						setanimation(opwallfind[j], centy, 4, 0, WALLY);
						setanimation(i, centy + day2, 4, 0, WALLY);
						setanimation(wall[opwallfind[j]].point2, centy + day2, 4, 0, WALLY);
					}
				}
			}
			playsound(74);
		}

		// kens swinging door
		if (datag == 13) {
			for (i = 0; i < swingcnt; i++) {
				if (swingdoor[i].sector == dasector) {
					if (swingdoor[i].anginc == 0) {
						if (swingdoor[i].ang == swingdoor[i].angclosed) {
							swingdoor[i].anginc = swingdoor[i].angopendir;
							playsound(22);
						} else {
							swingdoor[i].anginc = (short) -swingdoor[i].angopendir;
						}
					} else {
						swingdoor[i].anginc = (short) -swingdoor[i].anginc;
					}
				}
			}
		}

		// kens true sideways double-sliding door
		if (datag == 16) {
			// get 2 closest line segments to center (dax, day)
			opwallfind[0] = -1;
			opwallfind[1] = -1;
			for (i = startwall; i <= endwall; i++)
				if (wall[i].lotag == 6) {
					if (opwallfind[0] == -1)
						opwallfind[0] = (short) i;
					else
						opwallfind[1] = (short) i;
				}

			for (j = 0; j < 2; j++) {
				if ((((wall[opwallfind[j]].x + wall[wall[opwallfind[j]].point2].x) >> 1) == centx)
						&& (((wall[opwallfind[j]].y + wall[wall[opwallfind[j]].point2].y) >> 1) == centy)) { // door was
																												// closed.
																												// Find
																												// what
																												// direction
																												// door
																												// should
																												// open
					i = opwallfind[j] - 1;
					if (i < startwall)
						i = endwall;
					dax2 = wall[i].x - wall[opwallfind[j]].x;
					day2 = wall[i].y - wall[opwallfind[j]].y;
					if (dax2 != 0) {
						dax2 = wall[wall[wall[wall[opwallfind[j]].point2].point2].point2].x;
						dax2 -= wall[wall[wall[opwallfind[j]].point2].point2].x;
						setanimation(opwallfind[j], wall[opwallfind[j]].x + dax2, 4, 0, WALLX);
						setanimation(i, wall[i].x + dax2, 4, 0, WALLX);
						setanimation(wall[opwallfind[j]].point2, wall[wall[opwallfind[j]].point2].x + dax2, 4, 0,
								WALLX);
						setanimation(wall[wall[opwallfind[j]].point2].point2,
								wall[wall[wall[opwallfind[j]].point2].point2].x + dax2, 4, 0, WALLX);
					} else if (day2 != 0) {
						day2 = wall[wall[wall[wall[opwallfind[j]].point2].point2].point2].y;
						day2 -= wall[wall[wall[opwallfind[j]].point2].point2].y;
						setanimation(opwallfind[j], wall[opwallfind[j]].y + day2, 4, 0, WALLY);
						setanimation(i, wall[i].y + day2, 4, 0, WALLY);
						setanimation(wall[opwallfind[j]].point2, wall[wall[opwallfind[j]].point2].y + day2, 4, 0,
								WALLY);
						setanimation(wall[wall[opwallfind[j]].point2].point2,
								wall[wall[wall[opwallfind[j]].point2].point2].y + day2, 4, 0, WALLY);
					}
				} else { // door was not closed
					i = opwallfind[j] - 1;
					if (i < startwall)
						i = endwall;
					dax2 = wall[i].x - wall[opwallfind[j]].x;
					day2 = wall[i].y - wall[opwallfind[j]].y;
					if (dax2 != 0) {
						setanimation(opwallfind[j], centx, 4, 0, WALLX);
						setanimation(i, centx + dax2, 4, 0, WALLX);
						setanimation(wall[opwallfind[j]].point2, centx, 4, 0, WALLX);
						setanimation(wall[wall[opwallfind[j]].point2].point2, centx + dax2, 4, 0, WALLX);
					} else if (day2 != 0) {
						setanimation(opwallfind[j], centy, 4, 0, WALLY);
						setanimation(i, centy + day2, 4, 0, WALLY);
						setanimation(wall[opwallfind[j]].point2, centy, 4, 0, WALLY);
						setanimation(wall[wall[opwallfind[j]].point2].point2, centy + day2, 4, 0, WALLY);
					}
				}
			}
			if (mapnum > 1)
				playsound(23);
		}
	}

	public static int sub_1D021(int a1, int a2) {
		int v3 = gPlayer[screenpeek].x - a1;
		int v4 = gPlayer[screenpeek].y - a2;
		int v5 = v4 * v4 + v3 * v3;
		if (v5 >= 0x200000)
			return 1023 / ((v5 >> 21) + 4);
		else
			return 255;
	}

	public static void warpsprite(short spritenum) {
		SPRITE spr = sprite[spritenum];
		warp(spr.x, spr.y, spr.z, spr.ang, spr.sectnum);

		game.pInt.setsprinterpolate(spritenum, spr);
		spr.x = warp.x;
		spr.y = warp.y;
		spr.z = warp.z;
		spr.ang = (short) warp.ang;

		engine.changespritesect(spritenum, warp.sectnum);
	}

	public static BuildPos warp;

	public static void warp(int x, int y, int z, int daang, short dasector) {
		short startwall, endwall, s;
		int i, dax, day;
		warp.x = x;
		warp.y = y;
		warp.z = z;
		warp.ang = (short) daang;
		warp.sectnum = dasector;

		for (i = 0; i < warpsectorcnt; i++) {
			if (warpsectorlist[i] == warp.sectnum) {
				if (i == warpsectorcnt - 1)
					warp.sectnum = warpsectorlist[0];
				else
					warp.sectnum = warpsectorlist[i + 1];
				break;
			}
		}

		// find center of sector
		startwall = sector[warp.sectnum].wallptr;
		endwall = (short) (startwall + sector[warp.sectnum].wallnum - 1);
		dax = 0;
		day = 0;
		for (s = startwall; s <= endwall; s++) {
			dax += wall[s].x;
			day += wall[s].y;
			if (wall[s].nextsector >= 0) {
				i = s;
			}
		}
		warp.x = dax / (endwall - startwall + 1);
		warp.y = day / (endwall - startwall + 1);
		warp.z = sector[warp.sectnum].floorz - (42 << 8);
		warp.sectnum = engine.updatesector(warp.x, warp.y, warp.sectnum);
	}

}
