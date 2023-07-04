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
import static ru.m210projects.Build.Pragmas.mulscale;
import static ru.m210projects.LSP.Sprites.*;
import static ru.m210projects.LSP.Sounds.*;
import static ru.m210projects.LSP.Enemies.enemydie;
import static ru.m210projects.LSP.Globals.*;
import static ru.m210projects.LSP.Main.*;
import static ru.m210projects.LSP.Player.*;

import ru.m210projects.Build.Types.SPRITE;

public class Weapons {

	private static int weapx, weapy;
	private static int nFrameTicks;

	private static final short nWeaponTile[] = { 0, 1160, 1150, 1140, 1130, 1120, 1110, 1170, 1183, 1194, 1205, 1216, 1244,
			1090, 1090, 1090, 1090, 1090, 1100, 1040, 1060, 1030, 1070, 1080, 1050, 0, 0 };

	public static void overwritesprite(int thex, int they, int tilenum, int ang, int dapalnum, int stat) {
		engine.rotatesprite(thex << 16, they << 16, 65536, ang, tilenum, 0, dapalnum, stat, 0, 0, xdim, ydim);
	}

	public static int getIcon(int nWeapon) {
		int picnum = 660;
		if (nWeapon > 0 && nWeapon < 13)
			picnum += nWeapon;

		return nWeapon != 0 ? picnum : 0;
	}

	public static void moveweapons(int snum) {
		int currTime;
		int curr = gPlayer[snum].nWeapon;
		if (gPlayer[snum].nWeaponState != 0) {
			switch (nWeaponTile[curr]) {
			case 1170:
				switch (gPlayer[snum].nWeaponSeq) {
				case 0:
					gPlayer[snum].nWeaponImpact = -20;
					gPlayer[snum].nWeaponImpactAngle = (short) gPlayer[snum].ang;
					break;
				case 1:
					gPlayer[snum].nWeaponImpact = -24;
					gPlayer[snum].nWeaponImpactAngle = (short) gPlayer[snum].ang;
					break;
				case 2:
					gPlayer[snum].nWeaponImpact = -36;
					gPlayer[snum].nWeaponImpactAngle = (short) gPlayer[snum].ang;
					break;
				case 3:
					gPlayer[snum].nWeaponImpact = 20;
					gPlayer[snum].nWeaponImpactAngle = (short) gPlayer[snum].ang;
					break;
				case 4:
					gPlayer[snum].nWeaponImpact = 25;
					gPlayer[snum].nWeaponImpactAngle = (short) gPlayer[snum].ang;
					break;
				case 5:
					gPlayer[snum].nWeaponImpact = 43;
					gPlayer[snum].nWeaponImpactAngle = (short) gPlayer[snum].ang;
					break;
				case 6:
					gPlayer[snum].nWeaponImpact = 50;
					gPlayer[snum].nWeaponImpactAngle = (short) gPlayer[snum].ang;
					break;
				}
				break;
			case 1194:
				switch (gPlayer[snum].nWeaponSeq) {
				case 0:
					gPlayer[snum].nWeaponImpact = -30;
					gPlayer[snum].nWeaponImpactAngle = (short) gPlayer[snum].ang;
					break;
				case 1:
					gPlayer[snum].nWeaponImpact = -32;
					gPlayer[snum].nWeaponImpactAngle = (short) gPlayer[snum].ang;
					break;
				case 2:
					gPlayer[snum].nWeaponImpact = -12;
					gPlayer[snum].nWeaponImpactAngle = (short) gPlayer[snum].ang;
					break;
				case 3:
					gPlayer[snum].nWeaponImpact = -20;
					gPlayer[snum].nWeaponImpactAngle = (short) gPlayer[snum].ang;
					break;
				case 4:
					gPlayer[snum].nWeaponImpact = 10;
					gPlayer[snum].nWeaponImpactAngle = (short) gPlayer[snum].ang;
					break;
				case 5:
					gPlayer[snum].nWeaponImpact = 24;
					gPlayer[snum].nWeaponImpactAngle = (short) gPlayer[snum].ang;
					break;
				case 6:
					gPlayer[snum].nWeaponImpact = 35;
					gPlayer[snum].nWeaponImpactAngle = (short) gPlayer[snum].ang;
					break;
				}
				break;
			case 1183:
				switch (gPlayer[snum].nWeaponSeq) {
				case 0:
					gPlayer[snum].nWeaponImpact = -20;
					gPlayer[snum].nWeaponImpactAngle = (short) gPlayer[snum].ang;
					break;
				case 1:
					gPlayer[snum].nWeaponImpact = -24;
					gPlayer[snum].nWeaponImpactAngle = (short) gPlayer[snum].ang;
					break;
				case 2:
					gPlayer[snum].nWeaponImpact = -36;
					gPlayer[snum].nWeaponImpactAngle = (short) gPlayer[snum].ang;
					break;
				case 3:
					gPlayer[snum].nWeaponImpact = 40;
					gPlayer[snum].nWeaponImpactAngle = (short) gPlayer[snum].ang;
					break;
				case 4:
					gPlayer[snum].nWeaponImpact = 20;
					gPlayer[snum].nWeaponImpactAngle = (short) gPlayer[snum].ang;
					break;
				case 5:
					gPlayer[snum].nWeaponImpact = 64;
					gPlayer[snum].nWeaponImpactAngle = (short) gPlayer[snum].ang;
					break;
				case 6:
					gPlayer[snum].nWeaponImpact = 32;
					gPlayer[snum].nWeaponImpactAngle = (short) gPlayer[snum].ang;
					break;
				}
				break;
			case 1205:
				switch (gPlayer[snum].nWeaponSeq) {
				case 0:
					gPlayer[snum].nWeaponImpact = -42;
					gPlayer[snum].nWeaponImpactAngle = (short) gPlayer[snum].ang;
					break;
				case 1:
					gPlayer[snum].nWeaponImpact = -24;
					gPlayer[snum].nWeaponImpactAngle = (short) gPlayer[snum].ang;
					break;
				case 2:
					gPlayer[snum].nWeaponImpact = -12;
					gPlayer[snum].nWeaponImpactAngle = (short) gPlayer[snum].ang;
					break;
				case 3:
					gPlayer[snum].nWeaponImpact = 28;
					gPlayer[snum].nWeaponImpactAngle = (short) gPlayer[snum].ang;
					break;
				case 4:
					gPlayer[snum].nWeaponImpact = 24;
					gPlayer[snum].nWeaponImpactAngle = (short) gPlayer[snum].ang;
					break;
				case 5:
					gPlayer[snum].nWeaponImpact = 24;
					gPlayer[snum].nWeaponImpactAngle = (short) gPlayer[snum].ang;
					break;
				case 6:
					gPlayer[snum].nWeaponImpact = 24;
					gPlayer[snum].nWeaponImpactAngle = (short) gPlayer[snum].ang;
					break;
				}
				break;
			case 1216: // 11
				switch (gPlayer[snum].nWeaponSeq) {
				case 0:
					gPlayer[snum].nWeaponImpact = -36;
					gPlayer[snum].nWeaponImpactAngle = (short) gPlayer[snum].ang;
					break;
				case 1:
					gPlayer[snum].nWeaponImpact = -12;
					gPlayer[snum].nWeaponImpactAngle = (short) gPlayer[snum].ang;
					break;
				case 2:
					gPlayer[snum].nWeaponImpact = -24;
					gPlayer[snum].nWeaponImpactAngle = (short) gPlayer[snum].ang;
					break;
				case 3:
					gPlayer[snum].nWeaponImpact = -24;
					gPlayer[snum].nWeaponImpactAngle = (short) gPlayer[snum].ang;
					break;
				case 4:
					gPlayer[snum].nWeaponImpact = -12;
					gPlayer[snum].nWeaponImpactAngle = (short) gPlayer[snum].ang;
					break;
				case 5:
					gPlayer[snum].nWeaponImpact = -28;
					gPlayer[snum].nWeaponImpactAngle = (short) gPlayer[snum].ang;
					break;
				case 6:
					gPlayer[snum].nWeaponImpact = 30;
					gPlayer[snum].nWeaponImpactAngle = (short) gPlayer[snum].ang;
					break;
				case 7:
					gPlayer[snum].nWeaponImpact = 64;
					gPlayer[snum].nWeaponImpactAngle = (short) gPlayer[snum].ang;
					break;
				}
				break;
			case 1244:
				switch (gPlayer[snum].nWeaponSeq) {
				case 0:
					gPlayer[snum].nWeaponImpact = -28;
					gPlayer[snum].nWeaponImpactAngle = (short) gPlayer[snum].ang;
					break;
				case 1:
					gPlayer[snum].nWeaponImpact = -20;
					gPlayer[snum].nWeaponImpactAngle = (short) gPlayer[snum].ang;
					break;
				case 2:
					gPlayer[snum].nWeaponImpact = -48;
					gPlayer[snum].nWeaponImpactAngle = (short) gPlayer[snum].ang;
					break;
				case 3:
					gPlayer[snum].nWeaponImpact = -10;
					gPlayer[snum].nWeaponImpactAngle = (short) gPlayer[snum].ang;
					break;
				case 4:
					gPlayer[snum].nWeaponImpact = -18;
					gPlayer[snum].nWeaponImpactAngle = (short) gPlayer[snum].ang;
					break;
				case 5:
					gPlayer[snum].nWeaponImpact = -11;
					gPlayer[snum].nWeaponImpactAngle = (short) gPlayer[snum].ang;
					break;
				case 6:
					gPlayer[snum].nWeaponImpact = 0;
					break;
				case 7:
					gPlayer[snum].nWeaponImpact = 0;
					break;
				case 8:
					gPlayer[snum].nWeaponImpact = 0;
					break;
				case 9:
					gPlayer[snum].nWeaponImpact = 0;
					break;
				case 10:
					gPlayer[snum].nWeaponImpact = 0;
					break;
				case 11:
					gPlayer[snum].nWeaponImpact = 64;
					gPlayer[snum].nWeaponImpactAngle = (short) gPlayer[snum].ang;
					break;
				case 12:
					gPlayer[snum].nWeaponImpact = 64;
					gPlayer[snum].nWeaponImpactAngle = (short) gPlayer[snum].ang;
					break;
				case 13:
					gPlayer[snum].nWeaponImpact = 64;
					gPlayer[snum].nWeaponImpactAngle = (short) gPlayer[snum].ang;
					break;
				case 14:
					gPlayer[snum].nWeaponImpact = 64;
					gPlayer[snum].nWeaponImpactAngle = (short) gPlayer[snum].ang;
					break;
				}
			}
		}

		if (gPlayer[snum].nWeaponState == 0) {
			if (gPlayer[snum].nWeaponShootCount == 9 && gPlayer[snum].isWeaponFire != 0) {
				if (nWeaponTile[gPlayer[snum].nWeapon] >= 1080) {
					shoot(snum, gPlayer[snum].x, gPlayer[snum].y, gPlayer[snum].z, (int) gPlayer[snum].ang,
							(int) gPlayer[snum].horiz, gPlayer[snum].sectnum, gPlayer[snum].nWeapon);
				} else {
					shoot(snum, gPlayer[snum].x, gPlayer[snum].y, gPlayer[snum].z, (int) gPlayer[snum].ang,
							(int) gPlayer[snum].horiz, gPlayer[snum].sectnum, 0);
				}
				gPlayer[snum].nWeaponShootCount++;
			}
		} else {
			switch (nWeaponTile[gPlayer[snum].nWeapon]) {
			case 1100: // nWeapon 18
			case 1040: // nWeapon 19
				currTime = lockclock - gPlayer[snum].nWeaponClock;
				if (gPlayer[snum].nWeaponShootCount == 2) {
					shoot(snum, gPlayer[snum].x, gPlayer[snum].y, gPlayer[snum].z, (int) gPlayer[snum].ang,
							(int) gPlayer[snum].horiz, gPlayer[snum].sectnum, 0);
					gPlayer[snum].isWeaponFire = 0;
					gPlayer[snum].nWeaponShootCount++;
					return;
				}

				if (currTime > 88) {
					gPlayer[snum].nWeaponSeq = 0;
					gPlayer[snum].nWeaponState = 0;
					gPlayer[snum].nWeaponShootCount = 9;
					return;
				}

				if (currTime > 70) {
					gPlayer[snum].nWeaponSeq = 5;
					gPlayer[snum].nWeaponShootCount++;
					return;
				}

				if (currTime > 56) {
					gPlayer[snum].nWeaponSeq = 4;
					gPlayer[snum].nWeaponShootCount++;
					return;
				}

				if (currTime > 48) {
					gPlayer[snum].nWeaponSeq = 3;
					gPlayer[snum].nWeaponShootCount++;
					return;
				}

				if (currTime > 32) {
					gPlayer[snum].nWeaponSeq = 2;
					gPlayer[snum].nWeaponShootCount++;
					return;
				}

				if (currTime > 10) {
					gPlayer[snum].nWeaponSeq = 1;
					return;
				}

				gPlayer[snum].nWeaponSeq = 0;
				return;
			case 1090: // nWeapon 13 - 17
				currTime = lockclock - gPlayer[snum].nWeaponClock;
				if (gPlayer[snum].nWeaponShootCount == 2) {
					shoot(snum, gPlayer[snum].x, gPlayer[snum].y, gPlayer[snum].z, (int) gPlayer[snum].ang,
							(int) gPlayer[snum].horiz, gPlayer[snum].sectnum, 0);
					gPlayer[snum].isWeaponFire = 0;
					gPlayer[snum].nWeaponShootCount++;
					return;
				}

				if (currTime > 100) {
					gPlayer[snum].nWeaponSeq = 0;
					gPlayer[snum].nWeaponState = 0;
					gPlayer[snum].nWeaponShootCount = 9;
					return;
				}

				if (currTime > 88) {
					gPlayer[snum].nWeaponSeq = 6;
					gPlayer[snum].nWeaponShootCount++;
					return;
				}

				if (currTime > 70) {
					gPlayer[snum].nWeaponSeq = 5;
					gPlayer[snum].nWeaponShootCount++;
					return;
				}
				if (currTime > 56) {
					gPlayer[snum].nWeaponSeq = 4;
					gPlayer[snum].nWeaponShootCount++;
					return;
				}
				if (currTime > 48) {
					gPlayer[snum].nWeaponSeq = 3;
					gPlayer[snum].nWeaponShootCount++;
					return;
				}
				if (currTime > 32) {
					gPlayer[snum].nWeaponSeq = 2;
					gPlayer[snum].nWeaponShootCount++;
					return;
				}
				if (currTime > 10) {
					gPlayer[snum].nWeaponSeq = 1;
					return;
				}

				gPlayer[snum].nWeaponSeq = 0;
				return;

			case 1110: // nWeapon 6
			case 1120: // nWeapon 5
			case 1130: // nWeapon 4
			case 1140: // nWeapon 3
			case 1150: // nWeapon 2
			case 1160: // nWeapon 1
				currTime = lockclock - gPlayer[snum].nWeaponClock;
				if (gPlayer[snum].nWeaponShootCount != 0) {

					if (currTime > 108) {
						gPlayer[snum].nWeaponSeq = 0;
						gPlayer[snum].nWeaponState = 0;
						gPlayer[snum].nWeaponShootCount = 9;
						return;
					}

					if (currTime > 72) {
						gPlayer[snum].nWeaponSeq = 5;
						gPlayer[snum].nWeaponShootCount--;
						return;
					}

					if (currTime > 60) {
						gPlayer[snum].nWeaponSeq = 4;
						gPlayer[snum].nWeaponShootCount--;
						return;
					}
					if (currTime > 48) {
						gPlayer[snum].nWeaponSeq = 3;
						gPlayer[snum].nWeaponShootCount--;
						return;
					}
					if (currTime > 32) {
						gPlayer[snum].nWeaponSeq = 2;
						gPlayer[snum].nWeaponShootCount--;
						return;
					}
					if (currTime > 16) {
						gPlayer[snum].nWeaponSeq = 1;
						return;
					}

					gPlayer[snum].nWeaponSeq = 0;
					return;
				}
				shoot(snum, gPlayer[snum].x, gPlayer[snum].y, gPlayer[snum].z, (int) gPlayer[snum].ang,
						(int) gPlayer[snum].horiz, gPlayer[snum].sectnum, gPlayer[snum].nWeapon);
				gPlayer[snum].isWeaponFire = 0;
				gPlayer[snum].nWeaponShootCount--;
				return;
			case 1070: // nWeapon 22
				currTime = lockclock - gPlayer[snum].nWeaponClock;
				if (gPlayer[snum].nWeaponShootCount == 2) {
					shoot(snum, gPlayer[snum].x, gPlayer[snum].y, gPlayer[snum].z, (int) gPlayer[snum].ang,
							(int) gPlayer[snum].horiz, gPlayer[snum].sectnum, 0);
					gPlayer[snum].isWeaponFire = 0;
					gPlayer[snum].nWeaponShootCount++;
					return;
				}

				if (currTime > 68) {
					gPlayer[snum].nWeaponSeq = 0;
					gPlayer[snum].nWeaponState = 0;
					gPlayer[snum].nWeaponShootCount = 9;
					return;
				}
				if (currTime > 48) {
					gPlayer[snum].nWeaponSeq = 3;
					return;
				}
				if (currTime > 32) {
					gPlayer[snum].nWeaponSeq = 2;
					gPlayer[snum].nWeaponShootCount++;
					return;
				}
				if (currTime > 10) {
					gPlayer[snum].nWeaponSeq = 1;
					return;
				}
				gPlayer[snum].nWeaponSeq = 0;
				return;
			case 1080: // nWeapon 23
				currTime = lockclock - gPlayer[snum].nWeaponClock;
				if (gPlayer[snum].nWeaponShootCount == 2) {
					shoot(snum, gPlayer[snum].x, gPlayer[snum].y, gPlayer[snum].z, (int) gPlayer[snum].ang,
							(int) gPlayer[snum].horiz, gPlayer[snum].sectnum, 0);
					gPlayer[snum].isWeaponFire = 0;
					gPlayer[snum].nWeaponShootCount++;
					return;
				}

				if (currTime > 88) {
					gPlayer[snum].nWeaponSeq = 0;
					gPlayer[snum].nWeaponState = 0;
					gPlayer[snum].nWeaponShootCount = 3;
					return;
				}

				if (currTime > 60) {
					gPlayer[snum].nWeaponSeq = 4;
					gPlayer[snum].nWeaponShootCount++;
					return;
				}

				if (currTime > 48) {
					gPlayer[snum].nWeaponSeq = 3;
					gPlayer[snum].nWeaponShootCount++;
					return;
				}

				if (currTime > 32) {
					gPlayer[snum].nWeaponSeq = 2;
					gPlayer[snum].nWeaponShootCount++;
					return;
				}

				if (currTime > 10) {
					gPlayer[snum].nWeaponSeq = 1;
					return;
				}

				gPlayer[snum].nWeaponSeq = 0;
				return;
			case 1030: // nWeapon 21
				currTime = lockclock - gPlayer[snum].nWeaponClock;
				if (gPlayer[snum].nWeaponShootCount == 2) {
					shoot(snum, gPlayer[snum].x, gPlayer[snum].y, gPlayer[snum].z, (int) gPlayer[snum].ang,
							(int) gPlayer[snum].horiz, gPlayer[snum].sectnum, 0);
					gPlayer[snum].isWeaponFire = 0;
					gPlayer[snum].nWeaponShootCount++;
					return;
				}

				if (currTime > 88) {
					gPlayer[snum].nWeaponSeq = 0;
					gPlayer[snum].nWeaponState = 0;
					gPlayer[snum].nWeaponShootCount = 3;
					return;
				}

				if (currTime > 70) {
					gPlayer[snum].nWeaponSeq = 5;
					gPlayer[snum].nWeaponShootCount++;
					return;
				}

				if (currTime > 56) {
					gPlayer[snum].nWeaponSeq = 4;
					gPlayer[snum].nWeaponShootCount++;
					return;
				}

				if (currTime > 48) {
					gPlayer[snum].nWeaponSeq = 3;
					gPlayer[snum].nWeaponShootCount++;
					return;
				}

				if (currTime > 32) {
					gPlayer[snum].nWeaponSeq = 2;
					gPlayer[snum].nWeaponShootCount++;
					return;
				}

				if (currTime > 10) {
					gPlayer[snum].nWeaponSeq = 1;
					return;
				}

				gPlayer[snum].nWeaponSeq = 0;
				return;
			case 1050: // nWeapon 24
				currTime = lockclock - gPlayer[snum].nWeaponClock;
				if (gPlayer[snum].nWeaponShootCount == 13) {
					shoot(snum, gPlayer[snum].x, gPlayer[snum].y, gPlayer[snum].z, (int) gPlayer[snum].ang,
							(int) gPlayer[snum].horiz, gPlayer[snum].sectnum, 0);
					gPlayer[snum].isWeaponFire = 0;
					gPlayer[snum].nWeaponShootCount++;
					return;
				}

				if (currTime > 100) {
					gPlayer[snum].nWeaponSeq = 0;
					gPlayer[snum].nWeaponState = 0;
					gPlayer[snum].nWeaponShootCount = 9;
					return;
				}

				if (currTime > 88) {
					gPlayer[snum].nWeaponSeq = 5;
					gPlayer[snum].nWeaponShootCount++;
					return;
				}

				if (currTime > 60) {
					gPlayer[snum].nWeaponSeq = 4;
					gPlayer[snum].nWeaponShootCount++;
					return;
				}

				if (currTime > 48) {
					gPlayer[snum].nWeaponSeq = 3;
					gPlayer[snum].nWeaponShootCount++;
					return;
				}

				if (currTime > 32) {
					gPlayer[snum].nWeaponSeq = 2;
					gPlayer[snum].nWeaponShootCount++;
					return;
				}

				if (currTime > 10) {
					gPlayer[snum].nWeaponSeq = 1;
					gPlayer[snum].nWeaponShootCount++;
					return;
				}
				gPlayer[snum].nWeaponSeq = 0;
				return;
			case 1060: // nWeapon 20
				currTime = lockclock - gPlayer[snum].nWeaponClock;
				if (gPlayer[snum].nWeaponShootCount == 2) {
					shoot(snum, gPlayer[snum].x, gPlayer[snum].y, gPlayer[snum].z, (int) gPlayer[snum].ang,
							(int) gPlayer[snum].horiz, gPlayer[snum].sectnum, 0);
					gPlayer[snum].isWeaponFire = 0;
					gPlayer[snum].nWeaponShootCount++;
					return;
				}

				if (currTime > 88) {
					gPlayer[snum].nWeaponSeq = 0;
					gPlayer[snum].nWeaponState = 0;
					gPlayer[snum].nWeaponShootCount = 3;
					return;
				}

				if (currTime > 66) {
					gPlayer[snum].nWeaponSeq = 4;
					gPlayer[snum].nWeaponShootCount++;
					return;
				}

				if (currTime > 48) {
					gPlayer[snum].nWeaponSeq = 3;
					gPlayer[snum].nWeaponShootCount++;
					return;
				}

				if (currTime > 32) {
					gPlayer[snum].nWeaponSeq = 2;
					gPlayer[snum].nWeaponShootCount++;
					return;
				}

				if (currTime > 10) {
					gPlayer[snum].nWeaponSeq = 1;
					return;
				}
				gPlayer[snum].nWeaponSeq = 0;
				return;

			case 1170: // nWeapon 7
			case 1183: // nWeapon 8
			case 1194: // nWeapon 9
			case 1205: // nWeapon 10
				currTime = lockclock - gPlayer[snum].nWeaponClock;
				if (gPlayer[snum].nWeaponShootCount != 0) {
					if (currTime > 134) {
						gPlayer[snum].nWeaponSeq = 0;
						gPlayer[snum].nWeaponState = 0;
						gPlayer[snum].nWeaponShootCount = 9;
						return;
					}
					if (currTime > 110) {
						gPlayer[snum].nWeaponSeq = 6;
						gPlayer[snum].nWeaponShootCount--;
						return;
					}
					if (currTime > 94) {
						gPlayer[snum].nWeaponSeq = 5;
						gPlayer[snum].nWeaponShootCount--;
						return;
					}
					if (currTime > 82) {
						gPlayer[snum].nWeaponSeq = 4;
						return;
					}
					if (currTime > 70) {
						gPlayer[snum].nWeaponSeq = 3;
						return;
					}
					if (currTime > 32) {
						gPlayer[snum].nWeaponSeq = 2;
						return;
					}
					gPlayer[snum].nWeaponSeq = 0;
					return;
				}
				shoot(snum, gPlayer[snum].x, gPlayer[snum].y, gPlayer[snum].z, (int) gPlayer[snum].ang,
						(int) gPlayer[snum].horiz, gPlayer[snum].sectnum, gPlayer[snum].nWeapon);
				gPlayer[snum].isWeaponFire = 0;
				gPlayer[snum].nWeaponShootCount--;
				return;
			case 1244: // nWeapon 12
				currTime = lockclock - gPlayer[snum].nWeaponClock;
				if (gPlayer[snum].nWeaponShootCount != 0) {

					if (currTime > 240) {
						gPlayer[snum].nWeaponSeq = 0;
						gPlayer[snum].nWeaponState = 0;
						gPlayer[snum].nWeaponShootCount = 9;
						return;
					}

					if (currTime > 228) {
						gPlayer[snum].nWeaponSeq = 15;
						gPlayer[snum].nWeaponShootCount--;
						return;
					}
					if (currTime > 208) {
						gPlayer[snum].nWeaponSeq = 14;
						gPlayer[snum].nWeaponShootCount--;
						return;
					}
					if (currTime > 180) {
						gPlayer[snum].nWeaponSeq = 13;
						return;
					}
					if (currTime > 160) {
						gPlayer[snum].nWeaponSeq = 12;
						return;
					}
					if (currTime > 144) {
						gPlayer[snum].nWeaponSeq = 11;
						return;
					}
					if (currTime > 126) {
						gPlayer[snum].nWeaponSeq = 10;
						return;
					}
					if (currTime > 116) {
						gPlayer[snum].nWeaponSeq = 9;
						return;
					}
					if (currTime > 106) {
						gPlayer[snum].nWeaponSeq = 8;
						return;
					}
					if (currTime > 96) {
						gPlayer[snum].nWeaponSeq = 7;
						return;
					}
					if (currTime > 72) {
						gPlayer[snum].nWeaponSeq = 6;
						return;
					}
					if (currTime > 64) {
						gPlayer[snum].nWeaponSeq = 5;
						return;
					}
					if (currTime > 50) {
						gPlayer[snum].nWeaponSeq = 4;
						return;
					}
					if (currTime > 36) {
						gPlayer[snum].nWeaponSeq = 3;
						return;
					}
					if (currTime > 24) {
						gPlayer[snum].nWeaponSeq = 2;
						return;
					}
					if (currTime > 12) {
						gPlayer[snum].nWeaponSeq = 1;
						return;
					}

					gPlayer[snum].nWeaponSeq = 0;
					return;
				}

				shoot(snum, gPlayer[snum].x, gPlayer[snum].y, gPlayer[snum].z, (int) gPlayer[snum].ang,
						(int) gPlayer[snum].horiz, gPlayer[snum].sectnum, gPlayer[snum].nWeapon);
				gPlayer[snum].isWeaponFire = 0;
				gPlayer[snum].nWeaponShootCount--;
				return;
			case 1216: // nWeapon 11
				currTime = lockclock - gPlayer[snum].nWeaponClock;
				if (gPlayer[snum].nWeaponShootCount == 0) {
					shoot(snum, gPlayer[snum].x, gPlayer[snum].y, gPlayer[snum].z, (int) gPlayer[snum].ang,
							(int) gPlayer[snum].horiz, gPlayer[snum].sectnum, gPlayer[snum].nWeapon);
					gPlayer[snum].isWeaponFire = 0;
					gPlayer[snum].nWeaponShootCount--;
					return;
				}
				if (currTime > 720) {
					gPlayer[snum].nWeaponSeq = 0;
					gPlayer[snum].nWeaponState = 0;
					gPlayer[snum].nWeaponShootCount = 9;
					return;
				}
				if (currTime > 700) {
					gPlayer[snum].nWeaponSeq = 1;
					return;
				}
				if (currTime > 685) {
					gPlayer[snum].nWeaponSeq = 2;
					return;
				}
				if (currTime > 660) {
					gPlayer[snum].nWeaponSeq = 3;
					return;
				}
				if (currTime > 638) {
					gPlayer[snum].nWeaponSeq = 4;
					return;
				}
				if (currTime > 628) {
					gPlayer[snum].nWeaponSeq = 5;
					return;
				}
				if (currTime > 600) {
					gPlayer[snum].nWeaponSeq = 6;
					return;
				}
				if (currTime > 580) {
					gPlayer[snum].nWeaponSeq = 5;
					if (gPlayer[snum].nWeaponShootCount != 1)
						return;
					gPlayer[snum].nWeaponShootCount--;
					return;
				}
				if (currTime > 550) {
					gPlayer[snum].nWeaponSeq = 6;
					gPlayer[snum].nWeaponShootCount = 1;
					return;
				}
				if (currTime > 520) {
					gPlayer[snum].nWeaponSeq = 5;
					if (gPlayer[snum].nWeaponShootCount != 1)
						return;
					gPlayer[snum].nWeaponShootCount--;
					return;
				}
				if (currTime > 490) {
					gPlayer[snum].nWeaponSeq = 6;
					gPlayer[snum].nWeaponShootCount = 1;
					return;
				}
				if (currTime > 460) {
					gPlayer[snum].nWeaponSeq = 5;
					if (gPlayer[snum].nWeaponShootCount != 1)
						return;
					gPlayer[snum].nWeaponShootCount--;
					return;
				}
				if (currTime > 430) {
					gPlayer[snum].nWeaponSeq = 6;
					gPlayer[snum].nWeaponShootCount = 1;
					return;
				}
				if (currTime > 400) {
					gPlayer[snum].nWeaponSeq = 5;
					if (gPlayer[snum].nWeaponShootCount != 1)
						return;
					gPlayer[snum].nWeaponShootCount--;
					return;
				}
				if (currTime > 370) {
					gPlayer[snum].nWeaponSeq = 6;
					gPlayer[snum].nWeaponShootCount = 1;
					return;
				}
				if (currTime > 340) {
					gPlayer[snum].nWeaponSeq = 5;
					if (gPlayer[snum].nWeaponShootCount != 1)
						return;
					gPlayer[snum].nWeaponShootCount--;
					return;
				}
				if (currTime > 310) {
					gPlayer[snum].nWeaponSeq = 6;
					gPlayer[snum].nWeaponShootCount = 1;
					return;
				}
				if (currTime > 280) {
					gPlayer[snum].nWeaponSeq = 5;
					if (gPlayer[snum].nWeaponShootCount != 1)
						return;
					gPlayer[snum].nWeaponShootCount--;
					return;
				}
				if (currTime > 250) {
					gPlayer[snum].nWeaponSeq = 6;
					gPlayer[snum].nWeaponShootCount = 1;
					return;
				}
				if (currTime > 220) {
					gPlayer[snum].nWeaponSeq = 5;
					if (gPlayer[snum].nWeaponShootCount != 1)
						return;
					gPlayer[snum].nWeaponShootCount--;
					return;
				}
				if (currTime > 190) {
					gPlayer[snum].nWeaponSeq = 6;
					gPlayer[snum].nWeaponShootCount = 1;
					return;
				}
				if (currTime > 160) {
					gPlayer[snum].nWeaponSeq = 5;
					if (gPlayer[snum].nWeaponShootCount != 1)
						return;
					gPlayer[snum].nWeaponShootCount--;
					return;
				}
				if (currTime > 130) {
					gPlayer[snum].nWeaponSeq = 6;
					gPlayer[snum].nWeaponShootCount = 1;
					return;
				}
				if (currTime > 100) {
					gPlayer[snum].nWeaponSeq = 5;
					if (gPlayer[snum].nWeaponShootCount != 1)
						return;
					gPlayer[snum].nWeaponShootCount--;
					return;
				}
				if (currTime > 86) {
					gPlayer[snum].nWeaponSeq = 4;
					return;
				}
				if (currTime > 70) {
					gPlayer[snum].nWeaponSeq = 3;
					return;
				}
				if (currTime > 46) {
					gPlayer[snum].nWeaponSeq = 2;
					return;
				}
				if (currTime > 12) {
					gPlayer[snum].nWeaponSeq = 1;
					return;
				}
				gPlayer[snum].nWeaponSeq = 0;
				return;
			}
		}
		
		if (gPlayer[snum].nWeaponState == 0 && gPlayer[snum].nNewWeapon > 0 && gPlayer[snum].nNewWeapon != 999) {
			if (gPlayer[snum].isWeaponsSwitching == 0) {
				gPlayer[snum].isWeaponsSwitching = 1;
				gPlayer[snum].nSwitchingClock = lockclock;
			}
		}

		if (gPlayer[snum].isWeaponsSwitching != 0) {
			gPlayer[snum].nWeaponState = 0;
			if (gPlayer[snum].nNewWeapon >= 100) {
				weapy = 200 - 3 * (lockclock - gPlayer[snum].nSwitchingClock);
				if (weapy <= 0) {
					gPlayer[snum].isWeaponsSwitching = 0;
					weapy = 0;
				}
			} else {
				weapy = (lockclock - gPlayer[snum].nSwitchingClock) * 3;
				if (200 <= weapy) {
					gPlayer[snum].nLastWeapon = (short) (gPlayer[snum].nWeapon + 1);
					gPlayer[snum].nWeapon = (short) gPlayer[snum].nNewWeapon;
					gPlayer[snum].nNewWeapon = 999;
					gPlayer[snum].nSwitchingClock = lockclock;
					gPlayer[snum].nWeaponClock = lockclock;
				}
			}
		}
	}

	public static void weaponfire(int snum) {
		if (gPlayer[snum].nWeaponState == 0) {
			if (!isonwater(snum)) {
				gPlayer[snum].isWeaponFire = 0;
				gPlayer[snum].nWeaponShootCount = 0;
				gPlayer[snum].nWeaponState = 0;

				if (gPlayer[snum].nWeapon >= 13
						|| gPlayer[snum].nWeapon <= 6 && gPlayer[snum].nAmmo[gPlayer[snum].nWeapon] != 0
						|| gPlayer[snum].nMana > 0 && gPlayer[snum].nAmmo[gPlayer[snum].nWeapon] != 0) {
					gPlayer[snum].nWeaponClock = lockclock;
					gPlayer[snum].isWeaponFire = 1;
					gPlayer[snum].nWeaponShootCount = 1;
					gPlayer[snum].nWeaponState = 1;
					if (gPlayer[snum].nWeapon == 11)
						gPlayer[snum].word_5878C = 0;
				}
			}
		}
	}
	
	public static void nextweapon(int snum)
	{
		int nCurrentWeapon = gPlayer[snum].nWeapon + 1;
		if(nCurrentWeapon > 13)
			nCurrentWeapon = 1;
		int i = nCurrentWeapon + 1;
		while(!switchweapon(snum, i)) {
			if(i++ >= 13)
				i = 1;
		}
	}
	
	public static void prevweapon(int snum)
	{
		int nCurrentWeapon = gPlayer[snum].nWeapon + 1;
		if(nCurrentWeapon > 13)
			nCurrentWeapon = 1;
		int i = nCurrentWeapon - 1;
		while(!switchweapon(snum, i)) {
			if(i-- <= 0)
				i = 13;
		}
	}

	public static boolean switchweapon(int snum, int nWeapon)
	{
		int nNewWeap = nWeapon - 1;
		if(nWeapon > 13)
			nWeapon = 1;
		
		switch(nWeapon)
		{
		case 1:
			if (gPlayer[snum].nWeapon != nPlayerFirstWeapon)
				gPlayer[snum].nNewWeapon = nPlayerFirstWeapon;
			return true;
		case 8: //red ball
		case 9: //orange ball
		case 10: //green balls
		case 11: //electric
		case 12: //fire
		case 13: //bumerang
			if (gPlayer[snum].nMana <= 0)
				break;
		case 2: //shaken
		case 3: //ball
		case 4: //pikes
		case 5: //shuriken
		case 6: //dagger
		case 7: //kunai
			if (gPlayer[snum].nWeapon != nNewWeap && gPlayer[snum].nAmmo[nNewWeap] != 0) {
				gPlayer[snum].nNewWeapon = nNewWeap;
				if(nWeapon >= 2 && nWeapon <= 7)
					gPlayer[snum].nLastChoosedWeapon = (short) nNewWeap;
				else if(nWeapon >= 8 && nWeapon <= 13) gPlayer[snum].nLastManaWeapon = (short) nNewWeap;
			}
			break;
		default:
			return false;
		}
		
		return gPlayer[snum].nNewWeapon != 999 || gPlayer[snum].nWeapon == nNewWeap;
	}

	public static boolean switchweapgroup(int snum, int nWeapon) {
		boolean changed = false;
		int nNewWeap, i;
		switch (nWeapon) {
		case 1:
			if (gPlayer[snum].nWeapon != nPlayerFirstWeapon)
				gPlayer[snum].nNewWeapon = nPlayerFirstWeapon;
			break;
		case 2:
			nNewWeap = gPlayer[snum].nLastChoosedWeapon + 1;
			if(gPlayer[snum].nWeapon > 6)
				nNewWeap = gPlayer[snum].nLastChoosedWeapon;
			if (nNewWeap > 6)
				nNewWeap = 1;

			for (i = 0; i < 6 && gPlayer[snum].nAmmo[nNewWeap] == 0; i++) {
				if (++nNewWeap > 6)
					nNewWeap = 1;
			}

			if (gPlayer[snum].nAmmo[nNewWeap] != 0) {
				if (i < 5 || nNewWeap != gPlayer[snum].nWeapon) {
					gPlayer[snum].nLastChoosedWeapon = (short) nNewWeap;
					gPlayer[snum].nNewWeapon = nNewWeap;
					changed = true;
				}
			}
			break;
		case 3:
			if (gPlayer[snum].nMana > 0) {
				nNewWeap = gPlayer[snum].nLastManaWeapon + 1;
				if(gPlayer[snum].nWeapon < 7 || gPlayer[snum].nWeapon > 12)
					nNewWeap = gPlayer[snum].nLastManaWeapon;
					
				if (nNewWeap > 12 || nNewWeap < 7)
					nNewWeap = 7;

				for (i = 0; i < 6 && gPlayer[snum].nAmmo[nNewWeap] == 0; i++) {
					if (++nNewWeap > 12)
						nNewWeap = 7;
				}

				if (i < 5 || nNewWeap != gPlayer[snum].nWeapon) {
					gPlayer[snum].nLastManaWeapon = (short) nNewWeap;
					gPlayer[snum].nNewWeapon = nNewWeap;
					playsound(55);
					changed = true;
				}
			}
			break;
		}

		return changed;
	}

	public static void weaponbobbing(int snum) {
		if (gPlayer[snum].isWeaponsSwitching == 0) {
			int v112 = 2 * (8 - Math.abs(8 - gPlayer[snum].nBobCount));
			weapx = v112;
			weapy = v112;
		}
	}

	public static void drawweapons(int snum) {
		if (cfg.gCrosshair) {
//			overwritesprite(160, 100, 350, 0, 0, 2 | 8);
			int col = 4;
			engine.getrender().drawline256((xdim - mulscale(cfg.gCrossSize, 16, 16)) << 11, ydim << 11,
					(xdim - mulscale(cfg.gCrossSize, 4, 16)) << 11, ydim << 11, col);
			engine.getrender().drawline256((xdim + mulscale(cfg.gCrossSize, 4, 16)) << 11, ydim << 11,
					(xdim + mulscale(cfg.gCrossSize, 16, 16)) << 11, ydim << 11, col);
			engine.getrender().drawline256(xdim << 11, (ydim - mulscale(cfg.gCrossSize, 16, 16)) << 11, xdim << 11,
					(ydim - mulscale(cfg.gCrossSize, 4, 16)) << 11, col);
			engine.getrender().drawline256(xdim << 11, (ydim + mulscale(cfg.gCrossSize, 4, 16)) << 11, xdim << 11,
					(ydim + mulscale(cfg.gCrossSize, 16, 16)) << 11, col);
		}

		int curr = gPlayer[snum].nWeapon;
		switch (nWeaponTile[curr]) {
		case 1030:
			if (gPlayer[snum].nWeaponState == 0) {
				overwritesprite(weapx + 190, weapy + 107, 1030, 0, 0, 2 | 8 | 16);
				return;
			}

			switch (gPlayer[snum].nWeaponSeq) {
			case 0:
				overwritesprite(190, 107, 1030, 0, 0, 2 | 8 | 16);
				break;
			case 1:
				overwritesprite(252, 37, 1031, 0, 0, 2 | 8 | 16);
				break;
			case 2:
				overwritesprite(152, 65, 1032, 0, 0, 2 | 8 | 16);
				break;
			case 3:
				overwritesprite(49, 60, 1033, 0, 0, 2 | 8 | 16);
				break;
			case 4:
				overwritesprite(0, 85, 1034, 0, 0, 2 | 8 | 16);
				break;
			case 5:
				overwritesprite(0, 128, 1035, 0, 0, 2 | 8 | 16);
				break;
			}
			return;
		case 1070:
			if (gPlayer[snum].nWeaponState == 0) {
				overwritesprite(weapx + 202, weapy + 132, 1070, 0, 0, 2 | 8 | 16);
				return;
			}

			switch (gPlayer[snum].nWeaponSeq) {
			case 0:
				overwritesprite(202, 132, 1070, 0, 0, 2 | 8 | 16);
				break;
			case 1:
				overwritesprite(195, 135, 1071, 0, 0, 2 | 8 | 16);
				break;
			case 2:
				overwritesprite(149, 118, 1072, 0, 0, 2 | 8 | 16);
				break;
			case 3:
				overwritesprite(143, 112, 1073, 0, 0, 2 | 8 | 16);
				break;
			}
			return;
		case 1110:
			if (gPlayer[snum].nWeaponState == 0) {
				overwritesprite(weapx + 88, weapy + 64, 1110, 0, 0, 2 | 8 | 16);
				return;
			}

			switch (gPlayer[snum].nWeaponSeq) {
			case 0:
				overwritesprite(88, 64, 1110, 0, 0, 2 | 8 | 16);
				break;
			case 1:
				overwritesprite(78, 89, 1111, 0, 0, 2 | 8 | 16);
				break;
			case 2:
				overwritesprite(56, 120, 1112, 0, 0, 2 | 8 | 16);
				break;
			case 3:
				overwritesprite(35, 145, 1113, 0, 0, 2 | 8 | 16);
				break;
			case 4:
				overwritesprite(0, 176, 1114, 0, 0, 2 | 8 | 16);
				break;
			case 5:
				overwritesprite(182, 119, 1115, 0, 0, 2 | 8 | 16);
				break;
			}
			return;
		case 1100:
			if (gPlayer[snum].nWeaponState == 0) {
				overwritesprite(weapx + 41, weapy + 47, 1100, 0, 0, 2 | 8 | 16);
				return;
			}

			switch (gPlayer[snum].nWeaponSeq) {
			case 0:
				overwritesprite(41, 47, 1100, 0, 0, 2 | 8 | 16);
				break;
			case 1:
				overwritesprite(0, 124, 1101, 0, 0, 2 | 8 | 16);
				break;
			case 2:
				overwritesprite(83, 129, 1102, 0, 0, 2 | 8 | 16);
				break;
			case 3:
				overwritesprite(123, 136, 1103, 0, 0, 2 | 8 | 16);
				break;
			case 4:
				overwritesprite(247, 165, 1104, 0, 0, 2 | 8 | 16);
				break;
			case 5:
				overwritesprite(247, 165, 1104, 0, 0, 2 | 8 | 16);
				break;
			}
			return;
		case 1090:
			if (gPlayer[snum].nWeaponState == 0) {
				overwritesprite(weapx + 134, weapy + 60, 1090, 0, 0, 2 | 8 | 16);
				return;
			}

			switch (gPlayer[snum].nWeaponSeq) {
			case 0:
				overwritesprite(134, 60, 1090, 0, 0, 2 | 8 | 16);
				break;
			case 1:
				overwritesprite(212, 28, 1091, 0, 0, 2 | 8 | 16);
				break;
			case 2:
				overwritesprite(155, 80, 1092, 0, 0, 2 | 8 | 16);
				break;
			case 3:
				overwritesprite(49, 77, 1093, 0, 0, 2 | 8 | 16);
				break;
			case 4:
				overwritesprite(0, 95, 1094, 0, 0, 2 | 8 | 16);
				break;
			case 5:
				overwritesprite(0, 129, 1095, 0, 0, 2 | 8 | 16);
				break;
			case 6:
				overwritesprite(0, 158, 1096, 0, 0, 2 | 8 | 16);
				break;
			}
			return;
		case 1080:
			if (gPlayer[snum].nWeaponState == 0) {
				overwritesprite(weapx + 173, weapy + 48, 1080, 0, 0, 2 | 8 | 16);
				return;
			}

			switch (gPlayer[snum].nWeaponSeq) {
			case 0:
				overwritesprite(173, 48, 1080, 0, 0, 2 | 8 | 16);
				break;
			case 1:
				overwritesprite(78, 105, 1081, 0, 0, 2 | 8 | 16);
				break;
			case 2:
				overwritesprite(34, 108, 1082, 0, 0, 2 | 8 | 16);
				break;
			case 3:
				overwritesprite(0, 109, 1083, 0, 0, 2 | 8 | 16);
				break;
			case 4:
				overwritesprite(0, 133, 1084, 0, 0, 2 | 8 | 16);
				break;
			}
			return;
		case 1040:
			if (gPlayer[snum].nWeaponState == 0) {
				overwritesprite(weapx + 46, weapy + 65, 1040, 0, 0, 2 | 8 | 16);
				return;
			}

			switch (gPlayer[snum].nWeaponSeq) {
			case 0:
				overwritesprite(46, 65, 1040, 0, 0, 2 | 8 | 16);
				break;
			case 1:
				overwritesprite(0, 49, 1041, 0, 0, 2 | 8 | 16);
				break;
			case 2:
				overwritesprite(23, 54, 1042, 0, 0, 2 | 8 | 16);
				break;
			case 3:
			case 4:
			case 5:
				overwritesprite(46, 65, 1043, 0, 0, 2 | 8 | 16);
				break;
			}
			return;
		case 1060:
			if (gPlayer[snum].nWeaponState == 0) {
				overwritesprite(weapx, weapy + 133, 1060, 0, 0, 2 | 8 | 16);
				return;
			}

			switch (gPlayer[snum].nWeaponSeq) {
			case 0:
				overwritesprite(0, 133, 1060, 0, 0, 2 | 8 | 16);
				break;
			case 1:
				overwritesprite(0, 108, 1061, 0, 0, 2 | 8 | 16);
				break;
			case 2:
				overwritesprite(103, 102, 1062, 0, 0, 2 | 8 | 16);
				break;
			case 3:
				overwritesprite(168, 111, 1063, 0, 0, 2 | 8 | 16);
				break;
			case 4:
				overwritesprite(240, 132, 1064, 0, 0, 2 | 8 | 16);
				break;
			}
			return;
		case 1050:
			if (gPlayer[snum].nWeaponState == 0) {
				overwritesprite(weapx + 167, weapy + 108, 1050, 0, 0, 2 | 8 | 16);
				return;
			}

			switch (gPlayer[snum].nWeaponSeq) {
			case 0:
				overwritesprite(167, 108, 1050, 0, 0, 2 | 8 | 16);
				break;
			case 1:
				overwritesprite(252, 37, 1051, 0, 0, 2 | 8 | 16);
				break;
			case 2:
				overwritesprite(152, 75, 1052, 0, 0, 2 | 8 | 16);
				break;
			case 3:
				overwritesprite(60, 72, 1053, 0, 0, 2 | 8 | 16);
				break;
			case 4:
				overwritesprite(0, 96, 1054, 0, 0, 2 | 8 | 16);
				break;
			case 5:
				overwritesprite(0, 140, 1055, 0, 0, 2 | 8 | 16);
				break;
			}
			return;
		case 1120:
			if (gPlayer[snum].nWeaponState == 0) {
				overwritesprite(weapx + 12, weapy + 34, 1120, 0, 0, 2 | 8 | 16);
				return;
			}

			switch (gPlayer[snum].nWeaponSeq) {
			case 0:
				overwritesprite(12, 34, 1120, 0, 0, 2 | 8 | 16);
				break;
			case 1:
				overwritesprite(24, 105, 1121, 0, 0, 2 | 8 | 16);
				break;
			case 2:
				overwritesprite(133, 139, 1122, 0, 0, 2 | 8 | 16);
				break;
			case 3:
				overwritesprite(205, 161, 1123, 0, 0, 2 | 8 | 16);
				break;
			case 4:
				overwritesprite(273, 179, 1124, 0, 0, 2 | 8 | 16);
				break;
			case 5:
				overwritesprite(0, 104, 1125, 0, 0, 2 | 8 | 16);
				break;
			}
			return;
		case 1160:
			if (gPlayer[snum].nWeaponState == 0) {
				overwritesprite(weapx + 146, weapy + 99, 1160, 0, 0, 2 | 8 | 16);
				return;
			}

			switch (gPlayer[snum].nWeaponSeq) {
			case 0:
				overwritesprite(146, 99, 1160, 0, 0, 2 | 8 | 16);
				break;
			case 1:
				overwritesprite(109, 101, 1161, 0, 0, 2 | 8 | 16);
				break;
			case 2:
				overwritesprite(46, 125, 1162, 0, 0, 2 | 8 | 16);
				break;
			case 3:
				overwritesprite(28, 150, 1163, 0, 0, 2 | 8 | 16);
				break;
			case 4:
				overwritesprite(0, 180, 1164, 0, 0, 2 | 8 | 16);
				break;
			case 5:
				overwritesprite(243, 155, 1165, 0, 0, 2 | 8 | 16);
				break;
			}
			return;
		case 1150:
			if (gPlayer[snum].nWeaponState == 0) {
				overwritesprite(weapx + 6, weapy + 83, 1150, 0, 0, 2 | 8 | 16);
				return;
			}

			switch (gPlayer[snum].nWeaponSeq) {
			case 0:
				overwritesprite(6, 83, 1150, 0, 0, 2 | 8 | 16);
				break;
			case 1:
				overwritesprite(78, 106, 1151, 0, 0, 2 | 8 | 16);
				break;
			case 2:
				overwritesprite(157, 125, 1152, 0, 0, 2 | 8 | 16);
				break;
			case 3:
				overwritesprite(215, 150, 1153, 0, 0, 2 | 8 | 16);
				break;
			case 4:
				overwritesprite(271, 177, 1154, 0, 0, 2 | 8 | 16);
				break;
			case 5:
				overwritesprite(0, 151, 1155, 0, 0, 2 | 8 | 16);
				break;
			}
			return;
		case 1140:
			if (gPlayer[snum].nWeaponState == 0) {
				overwritesprite(weapx + 147, weapy + 57, 1140, 0, 0, 2 | 8 | 16);
				return;
			}

			switch (gPlayer[snum].nWeaponSeq) {
			case 0:
				overwritesprite(147, 57, 1140, 0, 0, 2 | 8 | 16);
				break;
			case 1:
				overwritesprite(78, 96, 1141, 0, 0, 2 | 8 | 16);
				break;
			case 2:
				overwritesprite(50, 139, 1142, 0, 0, 2 | 8 | 16);
				break;
			case 3:
				overwritesprite(4, 161, 1143, 0, 0, 2 | 8 | 16);
				break;
			case 4:
				overwritesprite(0, 179, 1144, 0, 0, 2 | 8 | 16);
				break;
			case 5:
				overwritesprite(263, 122, 1145, 0, 0, 2 | 8 | 16);
				break;
			}
			return;
		case 1130:
			if (gPlayer[snum].nWeaponState == 0) {
				overwritesprite(weapx + 134, weapy + 68, 1130, 0, 0, 2 | 8 | 16);
				return;
			}

			switch (gPlayer[snum].nWeaponSeq) {
			case 0:
				overwritesprite(134, 68, 1130, 0, 0, 2 | 8 | 16);
				break;
			case 1:
				overwritesprite(78, 104, 1131, 0, 0, 2 | 8 | 16);
				break;
			case 2:
				overwritesprite(50, 139, 1132, 0, 0, 2 | 8 | 16);
				break;
			case 3:
				overwritesprite(4, 161, 1133, 0, 0, 2 | 8 | 16);
				break;
			case 4:
				overwritesprite(0, 179, 1134, 0, 0, 2 | 8 | 16);
				break;
			case 5:
				overwritesprite(194, 115, 1135, 0, 0, 2 | 8 | 16);
				break;
			}
			return;
		case 1170:
			if (gPlayer[snum].nWeaponState == 0) {
				overwritesprite(weapx + 116, weapy + 123, 1170, 0, 0, 2 | 8 | 16);
				return;
			}

			switch (gPlayer[snum].nWeaponSeq) {
			case 0:
				overwritesprite(116, 123, 1170, 0, 0, 2 | 8 | 16);
				break;
			case 1:
				overwritesprite(78, 104, 1173, 0, 0, 2 | 8 | 16);
				break;
			case 2:
				overwritesprite(241, 132, 1174, 0, 0, 2 | 8 | 16);
				break;
			case 3:
				overwritesprite(177, 132, 1175, 0, 0, 2 | 8 | 16);
				break;
			case 4:
				overwritesprite(138, 123, 1176, 0, 0, 2 | 8 | 16);
				break;
			case 5:
				overwritesprite(104, 98, 1177, 0, 0, 2 | 8 | 16);
				break;
			case 6:
				overwritesprite(101, 94, 1178, 0, 0, 2 | 8 | 16);
				break;
			}
			return;
		case 1194:
			if (gPlayer[snum].nWeaponState == 0) {
				overwritesprite(weapx + 111, weapy + 118, Math.abs(nFrameTicks - 2) + 1194, 0, 0, 2 | 8 | 16);
				if ((totalmoves % 2) == 0)
					nFrameTicks = (nFrameTicks + 1) & 3;
				return;
			}

			switch (gPlayer[snum].nWeaponSeq) {
			case 0:
				overwritesprite(111, 118, 1194, 0, 0, 2 | 8 | 16);
				break;
			case 1:
				overwritesprite(139, 135, 1197, 0, 0, 2 | 8 | 16);
				break;
			case 2:
				overwritesprite(241, 145, 1198, 0, 0, 2 | 8 | 16);
				break;
			case 3:
				overwritesprite(179, 127, 1199, 0, 0, 2 | 8 | 16);
				break;
			case 4:
				overwritesprite(134, 116, 1200, 0, 0, 2 | 8 | 16);
				break;
			case 5:
				overwritesprite(104, 98, 1201, 0, 0, 2 | 8 | 16);
				break;
			case 6:
				overwritesprite(101, 94, 1202, 0, 0, 2 | 8 | 16);
				break;
			}
			return;
		case 1183:
			if (gPlayer[snum].nWeaponState == 0) {
				overwritesprite(weapx + 25, weapy + 116, Math.abs(nFrameTicks - 2) + 1183, 0, 0, 2 | 8 | 16);
				if ((totalmoves % 2) == 0)
					nFrameTicks = (nFrameTicks + 1) & 3;
				return;
			}

			switch (gPlayer[snum].nWeaponSeq) {
			case 0:
				overwritesprite(25, 116, 1183, 0, 0, 2 | 8 | 16);
				break;
			case 1:
				overwritesprite(29, 135, 1186, 0, 0, 2 | 8 | 16);
				break;
			case 2:
				overwritesprite(0, 145, 1187, 0, 0, 2 | 8 | 16);
				break;
			case 3:
				overwritesprite(0, 127, 1188, 0, 0, 2 | 8 | 16);
				break;
			case 4:
				overwritesprite(7, 111, 1189, 0, 0, 2 | 8 | 16);
				break;
			case 5:
				overwritesprite(45, 96, 1190, 0, 0, 2 | 8 | 16);
				break;
			case 6:
				overwritesprite(44, 93, 1191, 0, 0, 2 | 8 | 16);
				break;
			}
			return;
		case 1205:
			if (gPlayer[snum].nWeaponState == 0) {
				overwritesprite(weapx + 101, weapy + 117, Math.abs(nFrameTicks - 2) + 1205, 0, 0, 2 | 8 | 16);
				if ((totalmoves % 2) == 0)
					nFrameTicks = (nFrameTicks + 1) & 3;
				return;
			}

			switch (gPlayer[snum].nWeaponSeq) {
			case 0:
				overwritesprite(101, 117, 1205, 0, 0, 2 | 8 | 16);
				break;
			case 1:
				overwritesprite(160, 135, 1208, 0, 0, 2 | 8 | 16);
				break;
			case 2:
				overwritesprite(241, 145, 1209, 0, 0, 2 | 8 | 16);
				break;
			case 3:
				overwritesprite(154, 125, 1210, 0, 0, 2 | 8 | 16);
				break;
			case 4:
				overwritesprite(128, 114, 1211, 0, 0, 2 | 8 | 16);
				break;
			case 5:
				overwritesprite(101, 100, 1212, 0, 0, 2 | 8 | 16);
				break;
			case 6:
				overwritesprite(101, 94, 1213, 0, 0, 2 | 8 | 16);
				break;
			}
			return;
		case 1216:
			if (gPlayer[snum].nWeaponState == 0) {
				overwritesprite(weapx, weapy + 132, Math.abs(nFrameTicks - 2) + 1216, 0, 0, 2 | 8 | 16);
				if ((totalmoves % 4) == 0)
					nFrameTicks = (nFrameTicks + 1) & 3;
				return;
			}

			switch (gPlayer[snum].nWeaponSeq) {
			case 0:
				overwritesprite(0, 132, 1216, 0, 0, 2 | 8 | 16);
				break;
			case 1:
				overwritesprite(0, 132, 1217, 0, 0, 2 | 8 | 16);
				break;
			case 2:
				overwritesprite(0, 129, 1218, 0, 0, 2 | 8 | 16);
				break;
			case 3:
				overwritesprite(0, 146, 1219, 0, 0, 2 | 8 | 16);
				break;
			case 4:
				overwritesprite(0, 130, 1220, 0, 0, 2 | 8 | 16);
				break;
			case 5:
				overwritesprite(0, 128, 1221, 0, 0, 2 | 8 | 16);
				break;
			case 6:
				overwritesprite(0, 108, 1222, 0, 0, 2 | 8 | 16);
				break;
			case 7:
				overwritesprite(0, 97, 1223, 0, 0, 2 | 8 | 16);
				break;
			}
			return;
		case 1244: // 12
			if (gPlayer[snum].nWeaponState == 0) {
				overwritesprite(weapx, weapy + 128, Math.abs(nFrameTicks - 2) + 1244, 0, 0, 2 | 8 | 16);
				if ((totalmoves % 4) == 0)
					nFrameTicks = (nFrameTicks + 1) & 3;
				return;
			}

			switch (gPlayer[snum].nWeaponSeq) {
			case 0:
				overwritesprite(0, 128, 1244, 0, 0, 2 | 8 | 16);
				break;
			case 1:
				overwritesprite(0, 128, 1245, 0, 0, 2 | 8 | 16);
				break;
			case 2:
				overwritesprite(0, 124, 1246, 0, 0, 2 | 8 | 16);
				break;
			case 3:
				overwritesprite(82, 135, 1247, 0, 0, 2 | 8 | 16);
				break;
			case 4:
				overwritesprite(44, 142, 1248, 0, 0, 2 | 8 | 16);
				break;
			case 5:
				overwritesprite(0, 142, 1249, 0, 0, 2 | 8 | 16);
				break;
			case 6:
				overwritesprite(0, 139, 1250, 0, 0, 2 | 8 | 16);
				break;
			case 7:
				overwritesprite(0, 135, 1251, 0, 0, 2 | 8 | 16);
				break;
			case 8:
				overwritesprite(0, 139, 1252, 0, 0, 2 | 8 | 16);
				break;
			case 9:
				overwritesprite(0, 1442, 1253, 0, 0, 2 | 8 | 16);
				break;
			case 10:
				overwritesprite(0, 134, 1254, 0, 0, 2 | 8 | 16);
				break;
			case 11:
				overwritesprite(0, 122, 1255, 0, 0, 2 | 8 | 16);
				break;
			case 12:
				overwritesprite(0, 111, 1256, 0, 0, 2 | 8 | 16);
				break;
			case 13:
				overwritesprite(0, 96, 1257, 0, 0, 2 | 8 | 16);
				break;
			case 14:
				overwritesprite(0, 89, 1258, 0, 0, 2 | 8 | 16);
				break;
			}
			return;
		}
	}

	public static void shoot(int plr, int x, int y, int z, int ang, int horiz, short sectnum, int opt) {
		short spr;
		switch (opt) {
		case 0:
			engine.hitscan(x, y, z, sectnum, sintable[(ang + 512) & 0x7FF], sintable[ang & 0x7FF], 2000 * (100 - horiz),
					pHitInfo, CLIPMASK0);

			if (pHitInfo.hitsect >= 0 && pHitInfo.hitsprite < 0) {
				if (Math.abs(pHitInfo.hitx - x) + Math.abs(pHitInfo.hity - y) < 600) {
					spr = engine.insertsprite(pHitInfo.hitsect, SHOTSPARK);

					sprite[spr].x = pHitInfo.hitx;
					sprite[spr].y = pHitInfo.hity;
					sprite[spr].z = pHitInfo.hitz + 2560;
					sprite[spr].cstat = 2 | 128;
					sprite[spr].picnum = 1341;
					sprite[spr].shade = -4;
					sprite[spr].xrepeat = 40;
					sprite[spr].yrepeat = 40;
					sprite[spr].ang = (short) ang;
					sprite[spr].xvel = 0;
					sprite[spr].yvel = 0;
					sprite[spr].zvel = 0;
					sprite[spr].owner = (short) (plr + 4096);
					sprite[spr].lotag = 63;

					// engine.movesprite(spr, -(sintable[(ang + 512) & 0x7FF] >> 7), -(sintable[ang]
					// >> 7), 0, 128, 1024, 1024, CLIPMASK1, 4);
				}
			} else if (pHitInfo.hitsprite >= 0) {
				if (Math.abs(pHitInfo.hitx - x) + Math.abs(pHitInfo.hity - y) < 600) {
					SPRITE en = sprite[pHitInfo.hitsprite];

					switch (en.picnum) {
					case BLUEDUDE:
					case GREENDUDE:
					case REDDUDE:
					case PURPLEDUDE:
					case YELLOWDUDE:
						en.lotag -= gPlayer[plr].nFirstWeaponDamage;
						if (en.lotag <= 0) {
							if (en.picnum == PURPLEDUDE || en.picnum == BLUEDUDE)
								playsound(14, pHitInfo.hitsprite);
							else if (en.picnum == YELLOWDUDE)
								playsound(18, pHitInfo.hitsprite);
							else
								playsound(17, pHitInfo.hitsprite);
							enemydie(pHitInfo.hitsprite);
						} else {
							if (en.statnum == GUARD)
								engine.changespritestat(pHitInfo.hitsprite, CHASE);
						}
						break;
					}
				}
			}

			gPlayer[plr].word_58794++;
			gPlayer[plr].word_58794 &= 3;
			playsound(gPlayer[plr].word_58794 + 45);
			return;
		case 1:
		case 2:
		case 3:
		case 4:
		case 5:
		case 6:
			spr = engine.insertsprite(sectnum, PROJECTILE);
			sprite[spr].x = x;
			sprite[spr].y = y;
			sprite[spr].z = z + 3328;
			sprite[spr].clipdist = 16;
			sprite[spr].cstat = 128;
			sprite[spr].picnum = (short) (nWeaponTile[gPlayer[plr].nWeapon] + 6);
			sprite[spr].shade = -24;
			sprite[spr].xrepeat = 64;
			sprite[spr].yrepeat = 64;
			sprite[spr].ang = (short) ang;
			sprite[spr].xvel = (short) (sintable[(ang + 2560) & 0x7FF] >> 5);
			sprite[spr].yvel = (short) (sintable[(ang + 2048) & 0x7FF] >> 5);
			sprite[spr].zvel = (short) ((100 - horiz) << 6);
			sprite[spr].owner = (short) (plr + 4096);

			if (opt >= 4)
				sprite[spr].lotag = (short) (8 * opt - 24);
			else
				sprite[spr].lotag = (short) (opt + 4);

			changeammo(plr, opt, -1);
			if (gPlayer[plr].nAmmo[opt] == 0) {
				if(!switchweapgroup(plr, 2))
					switchweapgroup(plr, 1);
			}
			playsound(80 + (opt - 1));
			return;
		case 7:
		case 8:
			playsound(36);
			spr = engine.insertsprite(sectnum, PROJECTILE);
			sprite[spr].x = x;
			sprite[spr].y = y;
			sprite[spr].z = z + 3584;
			sprite[spr].cstat = 128;
			sprite[spr].picnum = (short) (nWeaponTile[gPlayer[plr].nWeapon] + 9);
			sprite[spr].shade = -23;
			sprite[spr].xrepeat = 48;
			sprite[spr].yrepeat = 48;
			sprite[spr].ang = (short) ang;
			sprite[spr].xvel = (short) (sintable[(ang + 2560) & 0x7FF] >> 5);
			sprite[spr].yvel = (short) (sintable[(ang + 2048) & 0x7FF] >> 5);
			sprite[spr].zvel = (short) (50 * (100 - horiz));
			sprite[spr].owner = (short) (plr + 4096);
			sprite[spr].lotag = (short) (8 * opt - 48);
			changemanna(plr, -2);
			if (opt == 7)
				playsound(57);
			else
				playsound(58);
			if (gPlayer[plr].nMana == 0) {
				if(!switchweapgroup(plr, 3) && !switchweapgroup(plr, 2))
					switchweapgroup(plr, 1);
			}
			return;
		case 9:
			playsound(36);
			short anga = (short) (ang + 1856);
			for (int i = 0; i < 7; i++) {
				spr = engine.insertsprite(sectnum, PROJECTILE);
				sprite[spr].x = x;
				sprite[spr].y = y;
				sprite[spr].z = z + 5120;
				sprite[spr].cstat = 128;
				sprite[spr].picnum = 1203;
				sprite[spr].shade = -23;
				sprite[spr].xrepeat = 24;
				sprite[spr].yrepeat = 24;
				sprite[spr].ang = anga;
				sprite[spr].xvel = (short) (sintable[(anga + 512) & 0x7FF] >> 5);
				sprite[spr].yvel = (short) (sintable[anga & 0x7FF] >> 5);
				sprite[spr].zvel = (short) (50 * (100 - horiz));
				sprite[spr].owner = (short) (plr + 4096);
				sprite[spr].lotag = 5;
				anga += 64;
			}
			changemanna(plr, -7);
			playsound(59);
			if (gPlayer[plr].nMana == 0) {
				if(!switchweapgroup(plr, 3) && !switchweapgroup(plr, 2))
					switchweapgroup(plr, 1);
			}
			return;
		case 10:
			playsound(36);
			int v29 = (sintable[(ang + 512) & 0x7FF] >> 4) + x;
			int v30 = (sintable[ang & 0x7FF] >> 4) + y;
			ang += 1280;
			for (int i = 0; i < 9; i++) {
				spr = engine.insertsprite(sectnum, PROJECTILE);
				sprite[spr].x = v29;
				sprite[spr].y = v30;
				sprite[spr].z = z + 5120;
				sprite[spr].cstat = 128;
				sprite[spr].picnum = 1214;
				sprite[spr].shade = -24;
				sprite[spr].xrepeat = 24;
				sprite[spr].yrepeat = 24;
				sprite[spr].ang = (short) ang;
				sprite[spr].xvel = (short) (sintable[(ang + 512) & 0x7FF] >> 6);
				sprite[spr].yvel = (short) (sintable[ang & 0x7FF] >> 6);
				sprite[spr].zvel = (short) (50 * (100 - horiz));
				sprite[spr].owner = (short) (plr + 4096);
				sprite[spr].lotag = 10;
				ang += 192;
			}
			changemanna(plr, -10);
			playsound(60);
			if (gPlayer[plr].nMana == 0) {
				if(!switchweapgroup(plr, 3) && !switchweapgroup(plr, 2))
					switchweapgroup(plr, 1);
			}
			return;
		case 11:
			playsound(36);
			if (gPlayer[plr].word_5878C == 5 || gPlayer[plr].word_5878C == 6)
				playsound(61);

			spr = engine.insertsprite(sectnum, PROJECTILE);
			sprite[spr].x = x + (sintable[(ang + 2816) & 0x7FF] >> 5);
			sprite[spr].y = y + (sintable[(ang + 2304) & 0x7FF] >> 5);
			sprite[spr].z = z + 0x2000;
			sprite[spr].cstat = 128;
			sprite[spr].picnum = (short) (gPlayer[plr].word_5878C + 1225);
			sprite[spr].shade = -23;
			sprite[spr].xrepeat = 32;
			sprite[spr].yrepeat = 32;
			sprite[spr].ang = (short) ang;
			sprite[spr].xvel = (short) (sintable[(ang + 2560) & 0x7FF] >> 8);
			sprite[spr].yvel = (short) (sintable[(ang + 2048) & 0x7FF] >> 8);
			sprite[spr].zvel = (short) (50 * (100 - horiz));
			sprite[spr].owner = (short) (plr + 4096);
			sprite[spr].lotag = 48;
			if (++gPlayer[plr].word_5878C > 8)
				gPlayer[plr].word_5878C = 0;

			spr = engine.insertsprite(sectnum, PROJECTILE);
			sprite[spr].x = (sintable[(ang + 2304) & 0x7FF] >> 5) + x;
			sprite[spr].y = (sintable[(ang + 1792) & 0x7FF] >> 5) + y;
			sprite[spr].z = z + 0x2000;
			sprite[spr].cstat = 128;
			sprite[spr].picnum = (short) (gPlayer[plr].word_5878C + 1225);
			sprite[spr].shade = -23;
			sprite[spr].xrepeat = 32;
			sprite[spr].yrepeat = 32;
			sprite[spr].ang = (short) ang;
			sprite[spr].xvel = (short) (sintable[(ang + 2560) & 0x7FF] >> 8);
			sprite[spr].yvel = (short) (sintable[(ang + 2048) & 0x7FF] >> 8);
			sprite[spr].zvel = (short) (50 * (100 - horiz));
			sprite[spr].owner = (short) (plr + 4096);
			sprite[spr].lotag = 48;
			if (++gPlayer[plr].word_5878C > 8)
				gPlayer[plr].word_5878C = 0;
			changemanna(plr, -2);
			if (gPlayer[plr].nMana == 0) {
				if(!switchweapgroup(plr, 3) && !switchweapgroup(plr, 2))
					switchweapgroup(plr, 1);
			}
			return;
		case 12:
			playsound(36);
			short v49;
			spr = engine.insertsprite(sectnum, PROJECTILE);
			sprite[spr].x = x;
			sprite[spr].y = y;
			sprite[spr].z = z + 3584;
			sprite[spr].cstat = 128;
			sprite[spr].picnum = 1259;
			sprite[spr].shade = -23;
			sprite[spr].xrepeat = 64;
			sprite[spr].yrepeat = 64;
			if (headspritestat[1] == -1) {
				sprite[spr].ang = (short) ang;
				v49 = sprite[spr].ang;
				sprite[spr].xvel = (short) (sintable[(v49 + 2560) & 0x7FF] >> 5);
				sprite[spr].yvel = (short) (sintable[(v49 + 2048) & 0x7FF] >> 5);
			} else {
				short v44 = headspritestat[1];
				if (sprite[v44].picnum == 1536 || sprite[v44].picnum == 1792 || sprite[v44].picnum == 2048
						|| sprite[v44].picnum == 2304 || sprite[v44].picnum == 2560) {
					sprite[spr].ang = engine.getangle(sprite[v44].x - gPlayer[plr].x, sprite[v44].y - gPlayer[plr].y);
					sprite[spr].xvel = (short) (sintable[(sprite[spr].ang + 2560) & 0x7FF] >> 5);
					sprite[spr].yvel = (short) (sintable[(sprite[spr].ang + 2048) & 0x7FF] >> 5);
				} else {
					sprite[spr].ang = (short) ((ang - 128) & 0x7FF);
					sprite[spr].xvel = (short) (sintable[(sprite[spr].ang + 2560) & 0x7FF] >> 5);
					sprite[spr].yvel = (short) (sintable[(sprite[spr].ang + 2048) & 0x7FF] >> 5);
				}
			}
			sprite[spr].zvel = (short) (50 * (100 - horiz));
			sprite[spr].owner = (short) (plr + 4096);
			sprite[spr].lotag = 54;
			changemanna(plr, -20);
			playsound(62);
			if (gPlayer[plr].nMana == 0) {
				if(!switchweapgroup(plr, 3) && !switchweapgroup(plr, 2))
					switchweapgroup(plr, 1);
			}
			return;
		}
	}
}
