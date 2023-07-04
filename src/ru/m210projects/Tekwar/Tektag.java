package ru.m210projects.Tekwar;

import static ru.m210projects.Build.Gameutils.BClampAngle;
import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Build.Net.Mmulti.*;
import static ru.m210projects.Build.Pragmas.*;
import static ru.m210projects.Tekwar.Main.*;
import static ru.m210projects.Tekwar.View.*;

import java.util.Arrays;

import static ru.m210projects.Tekwar.Animate.*;
import static ru.m210projects.Tekwar.Globals.*;
import static ru.m210projects.Tekwar.Names.*;
import static ru.m210projects.Tekwar.Tekchng.*;
import static ru.m210projects.Tekwar.Tekgun.*;
import static ru.m210projects.Tekwar.Tekmap.*;
import static ru.m210projects.Tekwar.Tekprep.*;
import static ru.m210projects.Tekwar.Teksnd.*;
import static ru.m210projects.Tekwar.Tekspr.*;
import static ru.m210projects.Tekwar.Tekstat.*;
import static ru.m210projects.Tekwar.Player.*;
import static ru.m210projects.Tekwar.Types.ANIMATION.*;
import static java.lang.Math.*;

import ru.m210projects.Build.Engine.Point;
import ru.m210projects.Build.Types.SECTOR;
import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Build.Types.WALL;
import ru.m210projects.Tekwar.Types.Animpic;
import ru.m210projects.Tekwar.Types.Delayfunc;
import ru.m210projects.Tekwar.Types.Doortype;
import ru.m210projects.Tekwar.Types.Elevatortype;
import ru.m210projects.Tekwar.Types.Floordoor;
import ru.m210projects.Tekwar.Types.Mapsndfxtype;
import ru.m210projects.Tekwar.Types.Sectoreffect;
import ru.m210projects.Tekwar.Types.Sectorvehicle;
import ru.m210projects.Tekwar.Types.Spriteelev;

public class Tektag {

	public static final int BOBBMAX = 512;
	public static final int BOBBDELTA = 128;

	public static final int VEHICLEHEIGHT = -13312;
	public static final int MAXANIMPICS = 32;
	public static final int MAXDELAYFUNCTIONS = 32;
	public static final int BLUEKEYITEM = 0;
	public static final int REDKEYITEM = 1;
	public static final int ELEVACTIVATED = 31;
	public static final int DOORUPTAG = 6;
	public static final int DOORDOWNTAG = 7;
	public static final int DOORSPLITHOR = 8;
	public static final int DOORSPLITVER = 9;
	public static final int DOORSWINGTAG = 13;
	public static final int DOORREVOLVETAG = 14; // sector tags
	public static final int PLATFORMELEVTAG = 1000;// additional sector tags
	public static final int BOXELEVTAG = 1003;
	public static final int PLATFORMDELAYTAG = 1004;
	public static final int BOXDELAYTAG = 1005;
	public static final int DOORFLOORTAG = 1006;
	public static final int PLATFORMDROPTAG = 1007;
	public static final int DST_BAYDOOR = 10;
	public static final int DST_HYDRAULICDOOR = 20;
	public static final int DST_ELEVATORDOOR = 30;
	public static final int DST_MATRIXDOOR1 = 40;
	public static final int DST_MATRIXDOOR2 = 50;
	public static final int DST_MATRIXDOOR3 = 60;
	public static final int DST_MATRIXDOOR4 = 70;
	public static final int SPRITEELEVATORTAG = 1500;
	public static final int PULSELIGHT = 0;// sector effect tags flags32[]
	public static final int FLICKERLIGHT = 1;
	public static final int DELAYEFFECT = 2;
	public static final int WPANNING = 3;
	public static final int FPANNING = 4;
	public static final int CPANNING = 5;
	public static final int FLICKERDELAY = 6;
	public static final int BLINKDELAY = 7;
	public static final int STEADYLIGHT = 8;
	public static final int WARPSECTOR = 9;
	public static final int KILLSECTOR = 10;
	public static final int DOORSPEEDEFFECT = 11;
	public static final int QUICKCLOSE = 12;
	public static final int SOUNDON = 13;
	public static final int SOUNDOFF = 14;
	public static final int DOORSUBTYPE = 15;
	public static final int DOORDELAY = (CLKIPS * 4);
	public static final int DOORSPEED = 128;
	public static final int ELEVSPEED = 256;

	public static final int MAXDOORS = 200;
	public static final int MAXFLOORDOORS = 25;

	public static final int D_NOTHING = 0;
	public static final int D_OPENDOOR = 1;
	public static final int D_CLOSEDOOR = 2;
	public static final int D_OPENING = 3;
	public static final int D_CLOSING = 4;
	public static final int D_WAITING = 5;
	public static final int D_SHUTSOUND = 6;
	public static final int D_OPENSOUND = 7;

	public static final int DOORCLOSED = 0;
	public static final int DOOROPENING = 1;
	public static final int DOOROPENED = 2;
	public static final int DOORCLOSING = 3;

	public static final int MAXSECTORVEHICLES = 10;
	public static final int MAXVEHICLEPOINTS = 200;
	public static final int MAXVEHICLETRACKS = 50;
	public static final int MAXVEHICLESECTORS = 30;
	public static final int SECTORVEHICLETAG = 1010;

	public static final int MAXSPRITEELEVS = 25;
	public static final int MAXPARTS = 20;
	public static final int MAXELEVFLOORS = 20;
	public static final int MAXELEVDOORS = 4;

	public static final int MAXMAPSOUNDFX = 32;
	public static final int MAP_SFX_AMBIENT = 0;
	public static final int MAP_SFX_SECTOR = 1;
	public static final int MAP_SFX_TOGGLED = 2;
	public static final int MAP_SFX_TURN_ON = 3;
	public static final int MAP_SFX_TURN_OFF = 4;

	public static final int E_OPENINGDOOR = 0;
	public static final int E_CLOSINGDOOR = 1;
	public static final int E_WAITING = 2;
	public static final int E_MOVING = 3;
	public static final int E_NEXTFLOOR = 4;

	public static final int E_GOINGUP = 0;
	public static final int E_GOINGDOWN = 1;

	public static final int E_WAITDELAY = CLKIPS * 4;
	public static final int E_DOOROPENPOS = 15360;

	public static int[] flags32 = { 0x80000000, 0x40000000, 0x20000000, 0x10000000, 0x08000000, 0x04000000, 0x02000000,
			0x01000000, 0x00800000, 0x00400000, 0x00200000, 0x00100000, 0x00080000, 0x00040000, 0x00020000, 0x00010000,
			0x00008000, 0x00004000, 0x00002000, 0x00001000, 0x00000800, 0x00000400, 0x00000200, 0x00000100, 0x00000080,
			0x00000040, 0x00000020, 0x00000010, 0x00000008, 0x00000004, 0x00000002, 0x00000001 };
	public static int ambupdateclock;
	public static int totalmapsndfx = 0;
	static Delayfunc delayfunc[] = new Delayfunc[MAXDELAYFUNCTIONS];
	static Animpic[] animpic = new Animpic[MAXANIMPICS];

	public static Spriteelev[] spriteelev = new Spriteelev[MAXSPRITEELEVS];
	public static int sprelevcnt;

	public static Doortype[] doortype = new Doortype[MAXDOORS];
	public static int doorxref[] = new int[MAXSECTORS], numdoors;

	public static Floordoor[] floordoor = new Floordoor[MAXFLOORDOORS];
	public static int fdxref[] = new int[MAXSECTORS], numfloordoors;

	public static Sectoreffect[] sectoreffect = new Sectoreffect[MAXSECTORS];
	static int secnt;
	static int sexref[] = new int[MAXSECTORS];
	public static Elevatortype[] elevator = new Elevatortype[MAXSECTORS];

	public static Sectorvehicle[] sectorvehicle = new Sectorvehicle[MAXSECTORVEHICLES];

	public static Mapsndfxtype[] mapsndfx = new Mapsndfxtype[MAXMAPSOUNDFX];

	public static int numvehicles;

	public static int headbob, bobstep = BOBBDELTA;

	public static boolean[] onelev = new boolean[MAXPLAYERS];

	public static int[] subwaystopdir = { 1, 1, 1, 1 };
	public static int numanimates;
	public static short numdelayfuncs;
	public static int loopinsound = -1;
	public static int baydoorloop = -1;
	public static int ambsubloop = -1;

	// Board animation variables
	public static short rotatespritelist[] = new short[16], rotatespritecnt;
	public static short warpsectorlist[] = new short[64], warpsectorcnt;
	public static short xpanningsectorlist[] = new short[16], xpanningsectorcnt;
	public static short ypanningwalllist[] = new short[64], ypanningwallcnt;
	public static short floorpanninglist[] = new short[64], floorpanningcnt;
	public static short dragsectorlist[] = new short[16], dragxdir[] = new short[16], dragydir[] = new short[16],
			dragsectorcnt;
	public static int[] dragx1 = new int[16], dragy1 = new int[16], dragx2 = new int[16], dragy2 = new int[16],
			dragfloorz = new int[16];
	public static short swingcnt, swingwall[][] = new short[32][5], swingsector[] = new short[32];
	public static short swingangopen[] = new short[32], swingangclosed[] = new short[32],
			swingangopendir[] = new short[32];
	public static short swingang[] = new short[32], swinganginc[] = new short[32];
	public static int[][] swingx = new int[32][8], swingy = new int[32][8];
	public static short revolvesector[] = new short[4], revolveang[] = new short[4], revolvecnt;
	public static int[][] revolvex = new int[4][32], revolvey = new int[4][32];
	public static int[] revolvepivotx = new int[4], revolvepivoty = new int[4];
	public static short subwaytracksector[][] = new short[4][128], subwaynumsectors[] = new short[4], subwaytrackcnt;
	public static int[] subwaystop[] = new int[4][8], subwaystopcnt = new int[4];
	public static int[] subwaytrackx1 = new int[4], subwaytracky1 = new int[4];
	public static int[] subwaytrackx2 = new int[4], subwaytracky2 = new int[4];
	public static int[] subwayx = new int[4], subwaygoalstop = new int[4], subwayvel = new int[4],
			subwaypausetime = new int[4];
	public static short waterfountainwall[] = new short[MAXPLAYERS], waterfountaincnt[] = new short[MAXPLAYERS];
	public static short slimesoundcnt[] = new short[MAXPLAYERS];

	public static void tekheadbob() {
		if (tekcfg.gHeadBob && !game.menu.gShowMenu) {
			headbob += bobstep;
			if (headbob < -BOBBMAX || headbob > BOBBMAX) {
				bobstep = -bobstep;
			}
		}
	}

	public static int krand_intercept(String stg) {
		return (engine.krand());
	}

	public static void tagcode() {
		int i, j, k, l, s, dax, day, cnt, good;
		short startwall, endwall, dasector, p, oldang, w;

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
			if (gPlayer[p].cursectnum >= 0 && sector[gPlayer[p].cursectnum].lotag == 10) // warp sector
			{
				if (gPlayer[p].cursectnum != gPlayer[p].ocursectnum) {
					warpsprite(gPlayer[p].playersprite);
					gPlayer[p].oposx = gPlayer[p].posx = sprite[gPlayer[p].playersprite].x;
					gPlayer[p].oposy = gPlayer[p].posy = sprite[gPlayer[p].playersprite].y;
					gPlayer[p].oposz = gPlayer[p].posz = sprite[gPlayer[p].playersprite].z;
					gPlayer[p].ang = sprite[gPlayer[p].playersprite].ang;
					gPlayer[p].cursectnum = sprite[gPlayer[p].playersprite].sectnum;
					sprite[gPlayer[p].playersprite].z += (KENSPLAYERHEIGHT << 8);
				}
			}

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

		for (i = 0; i < rotatespritecnt; i++) {
			sprite[rotatespritelist[i]].ang += (TICSPERFRAME << 2);
			sprite[rotatespritelist[i]].ang &= 2047;
		}

		// kens slime floor
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
			j = sector[dasector].floorz;
			game.pInt.setfloorinterpolate(dasector, sector[dasector]);
			sector[dasector].floorz = dragfloorz[i] + (sintable[(int) ((lockclock << 4) & 2047)] >> 3);
			for (p = connecthead; p >= 0; p = connectpoint2[p]) {
				if (gPlayer[p].cursectnum == dasector) {
					gPlayer[p].posx += dragxdir[i];
					gPlayer[p].posy += dragydir[i];
					gPlayer[p].posz += (sector[dasector].floorz - j);
					engine.setsprite(gPlayer[p].playersprite, gPlayer[p].posx, gPlayer[p].posy,
							gPlayer[p].posz + (KENSPLAYERHEIGHT << 8));
					sprite[gPlayer[p].playersprite].ang = (short) gPlayer[p].ang;
//					frameinterpolate = 0;
				}
			}
		}

		for (i = 0; i < swingcnt; i++) {
			if (swinganginc[i] != 0) {
				oldang = swingang[i];
				for (j = 0; j < (TICSPERFRAME << 2); j++) {
					swingang[i] = (short) ((swingang[i] + 2048 + swinganginc[i]) & 2047);
					if (swingang[i] == swingangclosed[i]) {
						if (j == ((TICSPERFRAME << 2) - 1)) {
							playsound(S_JUMP, swingx[i][0], swingy[i][0], 0, ST_UPDATE);
						}
						swinganginc[i] = 0;
					}
					if (swingang[i] == swingangopen[i]) {
						if (j == ((TICSPERFRAME << 2) - 1)) {
							playsound(S_JUMP, swingx[i][0], swingy[i][0], 0, ST_UPDATE);
						}
						swinganginc[i] = 0;
					}
				}
				for (k = 1; k <= 3; k++) {
					Point out = engine.rotatepoint(swingx[i][0], swingy[i][0], swingx[i][k], swingy[i][k], swingang[i]);
					engine.dragpoint(swingwall[i][k], out.getX(), out.getY());
				}

				if (swinganginc[i] != 0) {
					for (p = connecthead; p >= 0; p = connectpoint2[p])
						if (gPlayer[p].cursectnum >= 0 && ((gPlayer[p].cursectnum == swingsector[i])
								|| (testneighborsectors(gPlayer[p].cursectnum, swingsector[i]) == 1))) {
							cnt = 256;
							do {
								good = 1;

								// swingangopendir is -1 if forwards, 1 is backwards
								l = (swingangopendir[i] > 0) ? 1 : 0;
								for (k = l + 3; k >= l; k--)
									if (engine.clipinsidebox(gPlayer[p].posx, gPlayer[p].posy, swingwall[i][k],
											128) != 0) {
										good = 0;
										break;
									}

								if (good == 0) {
									if (cnt == 256) {
										swinganginc[i] = (short) -swinganginc[i];
										swingang[i] = oldang;
									} else {
										swingang[i] = (short) ((swingang[i] + 2048 - swinganginc[i]) & 2047);
									}
									for (k = 1; k <= 3; k++) {
										Point out = engine.rotatepoint(swingx[i][0], swingy[i][0], swingx[i][k],
												swingy[i][k], swingang[i]);
										engine.dragpoint(swingwall[i][k], out.getX(), out.getY());
									}
									if (swingang[i] == swingangclosed[i]) {
										swinganginc[i] = 0;
										break;
									}
									if (swingang[i] == swingangopen[i]) {
										swinganginc[i] = 0;
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
		}

		for (i = 0; i < subwaytrackcnt; i++) {
			if (subwaysound[i] == -1) {
				subwaysound[i] = playsound(S_SUBWAYLOOP, subwayx[i], subwaytracky1[i], -1, ST_VEHUPDATE);
			} else {
				updatevehiclesnds(subwaysound[i], subwayx[i], subwaytracky1[i]);
			}

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
				if (gPlayer[p].cursectnum >= 0 && gPlayer[p].cursectnum != subwaytracksector[i][0])
					if (sector[gPlayer[p].cursectnum].floorz != sector[subwaytracksector[i][0]].floorz)
						if (gPlayer[p].posx > subwaytrackx1[i])
							if (gPlayer[p].posy > subwaytracky1[i])
								if (gPlayer[p].posx < subwaytrackx2[i])
									if (gPlayer[p].posy < subwaytracky2[i]) {
										gPlayer[p].posx += (subwayvel[i] & 0xfffffffc);

										// Update sprite representation of player
										engine.setsprite(gPlayer[p].playersprite, gPlayer[p].posx, gPlayer[p].posy,
												gPlayer[p].posz + (KENSPLAYERHEIGHT << 8));
										sprite[gPlayer[p].playersprite].ang = (short) gPlayer[p].ang;
									}

			subwayx[i] += (subwayvel[i] & 0xfffffffc);
			if (subwaygoalstop[i] >= 8)
				continue;

			k = subwaystop[i][subwaygoalstop[i]] - subwayx[i];
			if (k > 0) {
				if (k > 2048) {
					if (subwayvel[i] == 12) {
						playsound(S_SUBWAYSTART, subwayx[0], subwaytracky1[0], 0, ST_UNIQUE | ST_NOUPDATE);
					}
					if (subwayvel[i] < 128)
						subwayvel[i]++;
				} else {
					if (subwayvel[i] == 32) {
						playsound(S_SUBWAYSTOP, subwayx[0], subwaytracky1[0], 0, ST_UNIQUE | ST_NOUPDATE);
					}
					subwayvel[i] = (k >> 4) + 1;
				}
			} else if (k < 0) {
				if (k < -2048) {
					if (subwayvel[i] == -12) {
						playsound(S_SUBWAYSTART, subwayx[0], subwaytracky1[0], 0, ST_UNIQUE | ST_NOUPDATE);
					}
					if (subwayvel[i] > -128)
						subwayvel[i]--;
				} else {
					if (subwayvel[i] == -32) {
						playsound(S_SUBWAYSTOP, subwayx[0], subwaytracky1[0], 0, ST_UNIQUE | ST_NOUPDATE);
					}
					subwayvel[i] = ((k >> 4) - 1);
				}
			}

			if (((subwayvel[i] >> 2) == 0) && (Math.abs(k) < 2048)) {
				if (subwaypausetime[i] == 720) {
					for (j = 1; j < subwaynumsectors[i]; j++) // Open all subway doors
					{
						dasector = subwaytracksector[i][j];
						if (sector[dasector].lotag == 17) {
							sector[dasector].lotag = 16;
							playsound(S_BIGSWINGCL, subwayx[i], subwaytracky1[i], 0, ST_NOUPDATE | ST_UNIQUE);
							operatesector(dasector);
							sector[dasector].lotag = 17;
						}
					}
				}
				if ((subwaypausetime[i] >= 120) && (subwaypausetime[i] - TICSPERFRAME < 120)) {
					for (j = 1; j < subwaynumsectors[i]; j++) // Close all subway doors
					{
						dasector = subwaytracksector[i][j];
						if (sector[dasector].lotag == 17) {
							sector[dasector].lotag = 16;
							playsound(S_BIGSWINGCL, subwayx[i], subwaytracky1[i], 0, ST_NOUPDATE | ST_UNIQUE);
							operatesector(dasector);
							sector[dasector].lotag = 17;
						}
					}
				}

				subwaypausetime[i] -= TICSPERFRAME;
				if (subwaypausetime[i] < 0) {
					subwaypausetime[i] = 720;
					if (subwaygoalstop[i] == (subwaystopcnt[i] - 1)) {
						subwaystopdir[i] = -1;
						subwaygoalstop[i] = subwaystopcnt[i] - 2;
					} else if (subwaygoalstop[i] == 0) {
						subwaygoalstop[i] = 1;
						subwaystopdir[i] = 1;
					} else {

						subwaygoalstop[i] += subwaystopdir[i];
					}
				}
			}
		}

		tektagcode();
	}

	public static void tektagcode() {
		int floorz, hi, i, j, k, lo, p, r, s, tics;
		int dax, dax2, day, day2, endwall, startwall;
		long effect;
		SECTOR sect;
		Sectoreffect septr;

		for (p = connecthead; p >= 0; p = connectpoint2[p]) {
			tekanimweap(p);
			tekhealstun(p);
		}

		for (i = 0; i < numdoors; i++) {
			movedoors(i);
		}

		if (game.nNetMode != NetMode.Multiplayer) {
			for (i = 0; i < secnt; i++) {
				s = sexref[i];
				if (s < 0 || s >= numsectors) {
					game.ThrowError("tag1402: Invalid sector effect index (" + s + ",e=" + i + ")");
				}
				sect = sector[s];
				septr = sectoreffect[s];
				if (septr.triggerable != 0) {
					continue;
				}
				effect = septr.sectorflags;
				if ((effect & flags32[WPANNING]) != 0) {
					tics = TICSPERFRAME;
					startwall = sect.wallptr;
					endwall = startwall + sect.wallnum - 1;
					dax = (tics * septr.cos) >> 15;
					day = (tics * septr.sin) >> 13;
					for (j = startwall; j <= endwall; j++) {
						wall[j].xpanning += (char) dax;
						wall[j].ypanning -= (char) day;
					}
				}
				if ((effect & flags32[FPANNING]) != 0) {
					tics = TICSPERFRAME;
					dax = (tics * septr.cos);
					day = (tics * septr.sin);
					short jj = headspritesect[s];
					while (jj != -1) {
						short kk = nextspritesect[jj];
						if (sprite[jj].owner < MAXSPRITES) {
							dax2 = dax >> 10;
							day2 = day >> 10;
							movesprite(jj, dax2, day2, 0, 4 << 8, 4 << 8, 0);
						}
						jj = kk;
					}
					for (p = connecthead; p >= 0; p = connectpoint2[p]) {
						if (gPlayer[p].cursectnum == s) {
							if (gPlayer[p].posz >= (sect.floorz - (42 << 8))) {
								engine.clipmove(gPlayer[p].posx, gPlayer[p].posy, gPlayer[p].posz,
										gPlayer[p].cursectnum, dax << 4, day << 4, 128, 4 << 8, 4 << 8, CLIPMASK0);
								gPlayer[p].posx = clipmove_x;
								gPlayer[p].posy = clipmove_y;
								gPlayer[p].posz = clipmove_z;
								gPlayer[p].cursectnum = (short) clipmove_sectnum;

								engine.setsprite(gPlayer[p].playersprite, gPlayer[p].posx, gPlayer[p].posy,
										gPlayer[p].posz + (42 << 8));
							}
						}
					}
					dax >>= 12;
					day >>= 12;
					sect.floorxpanning -= (char) dax;
					sect.floorypanning += (char) day;
				}
				if ((effect & flags32[CPANNING]) != 0) {
					tics = TICSPERFRAME;
					dax = (tics * septr.cos) >> 12;
					day = (tics * septr.sin) >> 12;
					sect.ceilingxpanning -= (char) dax;
					sect.ceilingypanning += (char) day;
				}
				if ((septr.delay -= TICSPERFRAME) > 0) {
					continue;
				}
				// negative overflow here without this - jeffy
				if (septr.delay < 0) {
					septr.delay = 0;
				}
				septr.delay += septr.delayreset;
				if ((effect & flags32[PULSELIGHT]) != 0) {
					sect.ceilingshade += septr.animate;
					if (septr.hi > septr.lo) {
						hi = septr.hi;
						lo = septr.lo;
					} else {
						hi = septr.lo;
						lo = septr.hi;
					}
					if (septr.animate < 0) {
						if (sect.ceilingshade <= lo) {
							septr.animate = abs(septr.animate);
						}
					} else {
						if (sect.ceilingshade >= hi) {
							septr.animate = -septr.animate;
						}
					}
					sect.floorshade = sect.ceilingshade;
					startwall = sect.wallptr;
					endwall = startwall + sect.wallnum - 1;
					for (j = startwall; j <= endwall; j++) {
						wall[j].shade = sect.ceilingshade;
					}
				} else if ((effect & flags32[FLICKERLIGHT]) != 0) {
					r = krand_intercept("TAG 1491");
					if (r < 16384) {
						sect.ceilingshade = (byte) septr.hi;
					} else if (r > 16384) {
						sect.ceilingshade = (byte) septr.lo;
					}
					sect.floorshade = sect.ceilingshade;
					startwall = sect.wallptr;
					endwall = startwall + sect.wallnum - 1;
					for (j = startwall; j <= endwall; j++) {
						wall[j].shade = sect.ceilingshade;
					}
				} else if ((effect & flags32[FLICKERDELAY]) != 0) {
					if (sect.ceilingshade == septr.lo) {
						sect.ceilingshade = (byte) septr.hi;
					} else {
						sect.ceilingshade = (byte) septr.lo;
						septr.delay >>= 2;
					}
					sect.floorshade = sect.ceilingshade;
					startwall = sect.wallptr;
					endwall = startwall + sect.wallnum - 1;
					for (j = startwall; j <= endwall; j++) {
						wall[j].shade = sect.ceilingshade;
					}
				} else if ((effect & flags32[BLINKDELAY]) != 0) {
					if (sect.ceilingshade == septr.lo) {
						sect.ceilingshade = (byte) septr.hi;
					} else {
						sect.ceilingshade = (byte) septr.lo;
					}
					sect.floorshade = sect.ceilingshade;
					startwall = sect.wallptr;
					endwall = startwall + sect.wallnum - 1;
					for (j = startwall; j <= endwall; j++) {
						wall[j].shade = sect.ceilingshade;
					}
				}
				if ((effect & flags32[KILLSECTOR]) != 0) {
					floorz = sector[s].floorz;
					for (p = connecthead; p >= 0; p = connectpoint2[p]) {
						if (gPlayer[p].cursectnum == s) {
							// matrix specific check here
							if ((klabs(gPlayer[p].posz - floorz) < 10240) || (mission == 7)) {
								if ((k = fdxref[s]) != -1) {
									if (!gPlayer[p].noclip && floordoor[k].state != DOORCLOSED) {
										changehealth(p, -septr.damage);
										changescore(p, -10);
									}
								} else {
									if (!gPlayer[p].noclip && septr.delay == septr.delayreset) {
										changehealth(p, -septr.damage);
										changescore(p, -10);
									}
								}
							}
						}
					}
				}
			}
		}

		if (game.nNetMode != NetMode.Multiplayer) {
			for (i = 0; i < sprelevcnt; i++) {
				movesprelevs(i);
			}
			for (i = 0; i < numfloordoors; i++) {
				movefloordoor(i);
			}
			for (i = 0; i < numvehicles; i++) {
				movevehicles(i);
			}
		}

		tekdoanimpic();
		tekdodelayfuncs();
		if ((lockclock - ambupdateclock) > 120) {
			checkmapsndfx(screenpeek);
		}
	}

	public static void setanimpic(int pic, int tics, int frames) {
		int i;

		for (i = 0; i < numanimates; i++) {
			if (animpic[i].pic == pic) {
				return;
			}
		}
		if (numanimates + 1 < MAXANIMPICS) {
			animpic[numanimates].pic = (short) pic;
			animpic[numanimates].tics = (short) tics;
			animpic[numanimates].frames = (short) frames;
			animpic[numanimates].nextclock = (int) (lockclock + tics);
			numanimates++;
		}
	}

	public static void tekdodelayfuncs() {
		int i, n, p;

		n = numdelayfuncs;
		for (i = 0; i < n; i++) {
			if (delayfunc[i].func == 0) {
				continue;
			}
			delayfunc[i].tics -= TICSPERFRAME;
			if (delayfunc[i].tics <= 0) {
				p = delayfunc[i].parm;
				delayfunc[i].func = (short) p; // (*delayfunc[i].func)(p);
				delayfunc[i].set(delayfunc[numdelayfuncs - 1]); // memmove(delayfunc[i],delayfunc[numdelayfuncs-1],
																// sizeof(struct delayfunc));
				delayfunc[numdelayfuncs - 1].set(0);
				numdelayfuncs--;
			}
		}
	}

	public static void tekdoanimpic() {
		int i, n;

		n = numanimates;
		for (i = 0; i < n; i++) {
			if (lockclock < animpic[i].nextclock) {
				continue;
			}
			if (animpic[i].frames > 0) {
				if (--animpic[i].frames > 0) {
					animpic[i].pic++;
					animpic[i].nextclock = (int) (lockclock + animpic[i].tics);
				}
			} else if (animpic[i].frames < 0) {
				if (++animpic[i].frames < 0) {
					animpic[i].pic--;
					animpic[i].nextclock = (int) (lockclock + animpic[i].tics);
				}
			} else {
				numanimates--;
				if (numanimates > 0) {
					animpic[i].set(animpic[numanimates]); // memmove(animpic[i],animpic[numanimates], sizeof(struct
															// animpic));
					animpic[i].set(0); // memset(animpic[numanimates],0,sizeof(struct animpic));
				}
			}
		}
	}

	public static void movefloordoor(int d) {
		int s, tics;
		short j;
		Floordoor dptr;

		tics = TICSPERFRAME << 2;
		dptr = floordoor[d];
		switch (dptr.state) {
		case DOOROPENING:
			if (dptr.dist1 > 0) {
				s = tics;
				dptr.dist1 -= s;
				if (dptr.dist1 < 0) {
					s += dptr.dist1;
					dptr.dist1 = 0;
				}
				switch (dptr.dir) {
				case 0:
					j = dptr.wall1;
					engine.dragpoint(j, wall[j].x, wall[j].y - s);
					j = wall[j].point2;
					engine.dragpoint(j, wall[j].x, wall[j].y - s);
					break;
				case 1:
					j = dptr.wall1;
					engine.dragpoint(j, wall[j].x + s, wall[j].y);
					j = wall[j].point2;
					engine.dragpoint(j, wall[j].x + s, wall[j].y);
					break;
				case 2:
					j = dptr.wall1;
					engine.dragpoint(j, wall[j].x, wall[j].y + s);
					j = wall[j].point2;
					engine.dragpoint(j, wall[j].x, wall[j].y + s);
					break;
				case 3:
					j = dptr.wall1;
					engine.dragpoint(j, wall[j].x - s, wall[j].y);
					j = wall[j].point2;
					engine.dragpoint(j, wall[j].x - s, wall[j].y);
					break;
				}
			}
			if (dptr.dist2 > 0) {
				s = tics;
				dptr.dist2 -= s;
				if (dptr.dist2 < 0) {
					s += dptr.dist2;
					dptr.dist2 = 0;
				}
				switch (dptr.dir) {
				case 0:
					j = dptr.wall2;
					engine.dragpoint(j, wall[j].x, wall[j].y + s);
					j = wall[j].point2;
					engine.dragpoint(j, wall[j].x, wall[j].y + s);
					break;
				case 1:
					j = dptr.wall2;
					engine.dragpoint(j, wall[j].x - s, wall[j].y);
					j = wall[j].point2;
					engine.dragpoint(j, wall[j].x - s, wall[j].y);
					break;
				case 2:
					j = dptr.wall2;
					engine.dragpoint(j, wall[j].x, wall[j].y - s);
					j = wall[j].point2;
					engine.dragpoint(j, wall[j].x, wall[j].y - s);
					break;
				case 3:
					j = dptr.wall2;
					engine.dragpoint(j, wall[j].x + s, wall[j].y);
					j = wall[j].point2;
					engine.dragpoint(j, wall[j].x + s, wall[j].y);
					break;
				}
			}
			if (dptr.dist1 <= 0 && dptr.dist2 <= 0) {
				dptr.state = DOOROPENED;
			}
			break;
		case DOORCLOSING:
		case DOOROPENED:
		case DOORCLOSED:
			break;
		}
	}

	public static void movesprelevs(int e) {
		int i, j, n, tics;
		long goalz;
		Spriteelev s;
		SPRITE spr;

		s = spriteelev[e];
		tics = TICSPERFRAME << 6;

		switch (s.state) {
		case E_WAITING:
			s.delay -= TICSPERFRAME;
			if (s.delay <= 0) {
				s.state = E_CLOSINGDOOR;
			}
			return;
		case E_CLOSINGDOOR:
			s.doorpos -= tics;
			if (s.doorpos <= 0) {
				s.doorpos = 0;
				s.state = E_NEXTFLOOR;
			}
			break;
		case E_OPENINGDOOR:
			s.doorpos += tics;
			if (s.doorpos >= E_DOOROPENPOS) {
				s.doorpos = E_DOOROPENPOS;
				s.state = E_WAITING;
				s.delay = E_WAITDELAY;
			}
			break;
		case E_MOVING:
			if (s.curfloor >= 0) {
				goalz = s.floorz[s.curfloor];
				if (s.curdir == E_GOINGUP) {
					s.floorpos -= tics;
					if (s.floorpos <= goalz) {
						s.floorpos += abs(s.floorpos - goalz);
						s.state = E_OPENINGDOOR;
					}
				} else {
					s.floorpos += tics;
					if (s.floorpos >= goalz) {
						s.floorpos -= abs(s.floorpos - goalz);
						s.state = E_OPENINGDOOR;
					}
				}
			}
			break;
		case E_NEXTFLOOR:
			if (s.curdir == E_GOINGUP) {
				s.curfloor++;
				if (s.curfloor > s.floors) {
					s.curfloor -= 2;
					s.curdir = E_GOINGDOWN;
				}
			} else if (s.curdir == E_GOINGDOWN) {
				s.curfloor--;
				if (s.curfloor < 0) {
					s.curfloor += 2;
					s.curdir = E_GOINGUP;
				}
			}
			s.state = E_MOVING;
			break;
		}
		for (i = 0; i < s.parts; i++) {
			j = s.sprnum[i];
			spr = sprite[j];
			game.pInt.setsprinterpolate(j, spr);
			spr.z = s.startz[i] + s.floorpos - s.floorz[0];
			for (n = 0; n < s.doors; n++) {
				if (j == s.door[n]) {
					spr.z -= s.doorpos;
				}
			}
		}
	}

	public static void tekpreptags() {
		int angle, k, n, s;
		int dax, day, endwall, x1, x2, y1, y2;
		short killwcnt, j, startwall, i = 0, w1 = 0, w2 = 0, w3 = 0, w4 = 0;
		;
		long effect;
		SPRITE spr;

		totalmapsndfx = 0;
		secnt = 0;

		for (j = 0; j < MAXSECTORS; j++) {
			sectoreffect[j].reset();
			sexref[j] = 0;
			elevator[j].reset();
		}
		for (j = 0; j < MAXDOORS; j++) {
			doortype[j].reset();
		}
		numfloordoors = 0;
		Arrays.fill(fdxref, -1);
		
		for (j = 0; j < MAXFLOORDOORS; j++) 
			floordoor[j].reset();
		for (j = 0; j < MAXSPRITEELEVS; j++) 
			spriteelev[j].reset();
		for (j = 0; j < MAXMAPSOUNDFX; j++)
			mapsndfx[j].reset();
		numvehicles = 0;
		for (j = 0; j < MAXSECTORVEHICLES; j++)
			sectorvehicle[j].reset();

		numdoors = 0;
		for (j = 0; j < numsectors; j++) {
			if (sector[j].ceilingpal != 0) {
				startwall = sector[j].wallptr;
				endwall = startwall + sector[j].wallnum - 1;
				for (i = startwall; i <= endwall; i++) {
					wall[i].pal = sector[j].ceilingpal;
				}
			}
			switch (sector[j].lotag) {
			case DOORUPTAG:
			case DOORDOWNTAG:
			case DOORSPLITHOR:
			case DOORSPLITVER:
			case PLATFORMELEVTAG:
			case PLATFORMDELAYTAG:
			case BOXELEVTAG:
			case BOXDELAYTAG:
			case PLATFORMDROPTAG:
				if (sector[j].lotag == BOXDELAYTAG || sector[j].lotag == PLATFORMDELAYTAG) {

					k = engine.nextsectorneighborz(j, sector[j].floorz, 1, 1);
					elevator[j].hilevel = sector[j].floorz;
					if (k > -1)
						elevator[j].lolevel = sector[k].floorz;
				}
				n = numdoors++;
				if (numdoors >= MAXDOORS) {
					break;
				}
				switch (sector[j].lotag) {
				case DOORUPTAG:
				case DOORDOWNTAG:
				case DOORSPLITHOR:
					doortype[n].step = DOORSPEED;
					break;
				case DOORSPLITVER:
					doortype[n].step = 4;
					break;
				case PLATFORMELEVTAG:
				case PLATFORMDELAYTAG:
				case BOXELEVTAG:
				case BOXDELAYTAG:
				case PLATFORMDROPTAG:
					doortype[n].step = ELEVSPEED;
					break;
				}
				doortype[n].centx = doortype[n].centy = 0;
				startwall = sector[j].wallptr;
				endwall = startwall + sector[j].wallnum - 1;
				for (k = 0, i = startwall; i <= endwall; i++) {
					if (wall[i].lotag == 6 && k < 8) {
						doortype[n].walls[k] = i;
						doortype[n].walls[k + 1] = (short) (i + 1);
						doortype[n].walls[k + 2] = (short) (i + 2);
						doortype[n].walls[k + 3] = (short) (i - 1);
						dax = wall[doortype[n].walls[k]].x;
						if (wall[doortype[n].walls[k + 3]].x == dax) {
							day = wall[i + 2].y;
							doortype[n].points[k / 2] = day;
							day = wall[wall[i + 2].point2].y;
							doortype[n].points[(k / 2) + 1] = day;
						} else {
							dax = wall[i + 2].x;
							doortype[n].points[k / 2] = dax;
							dax = wall[wall[i + 2].point2].x;
							doortype[n].points[(k / 2) + 1] = dax;
						}
						k += 4;
					}
					doortype[n].centx += wall[i].x;
					doortype[n].centy += wall[i].y;
				}
				doortype[n].centx /= (endwall - startwall + 1);
				doortype[n].centy /= (endwall - startwall + 1);
				doortype[n].centz = (sector[j].ceilingz + sector[j].floorz) / 2;
				doortype[n].type = sector[j].lotag;
				doortype[n].sector = j;
				doorxref[j] = n;
				break;
			case DOORFLOORTAG:
				k = fdxref[j] = numfloordoors++;
				floordoor[k].state = DOORCLOSED;
				floordoor[k].sectnum = i;
				startwall = sector[j].wallptr;
				endwall = startwall + sector[j].wallnum - 1;
				for (i = startwall; i <= endwall; i++) {
					if (wall[i].lotag == 6) {
						if (floordoor[k].wall1 == 0) {
							w1 = floordoor[k].wall1 = i;
							w2 = wall[w1].point2;
						} else {
							w3 = floordoor[k].wall2 = i;
							w4 = wall[w3].point2;
						}
					}
				}
				x1 = wall[w1].x; // close the doors all the way
				y1 = wall[w1].y;
				x2 = wall[w4].x;
				y2 = wall[w4].y;

				engine.dragpoint(w1, (x1 + x2) / 2, (y1 + y2) / 2);
				if (x1 != x2) {
					if (x1 < x2) {
						engine.dragpoint(w4, (x1 + x2) / 2 + 1, (y1 + y2) / 2);
						floordoor[k].dir = 3;
					} else {
						engine.dragpoint(w4, (x1 + x2) / 2 - 1, (y1 + y2) / 2);
						floordoor[k].dir = 1;
					}
					x1 = wall[w1].x;
					w1 = wall[wall[wall[w1].nextwall].point2].point2;
					floordoor[k].dist1 = abs(x1 - wall[w1].x) - 50;
				} else if (y1 != y2) {
					if (y1 < y2) {
						engine.dragpoint(w4, (x1 + x2) / 2, (y1 + y2) / 2 + 1);
						floordoor[k].dir = 0;
					} else {
						engine.dragpoint(w4, (x1 + x2) / 2, (y1 + y2) / 2 - 1);
						floordoor[k].dir = 2;
					}
					y1 = wall[w1].y;
					w1 = wall[wall[wall[w1].nextwall].point2].point2;
					floordoor[k].dist1 = abs(y1 - wall[w1].y) - 50;
				}
				x1 = wall[w2].x;
				y1 = wall[w2].y;
				x2 = wall[w3].x;
				y2 = wall[w3].y;
				engine.dragpoint(w2, (x1 + x2) / 2, (y1 + y2) / 2);
				if (x1 != x2) {
					if (x1 < x2) {
						engine.dragpoint(w3, (x1 + x2) / 2 + 1, (y1 + y2) / 2);
					} else {
						engine.dragpoint(w3, (x1 + x2) / 2 - 1, (y1 + y2) / 2);
					}
					x1 = wall[w2].x;
					w2 = wall[wall[wall[w2].nextwall].point2].point2;
					floordoor[k].dist2 = abs(x1 - wall[w2].x) - 50;
				} else if (y1 != y2) {
					if (y1 < y2)
						engine.dragpoint(w3, (x1 + x2) / 2, (y1 + y2) / 2 + 1);
					else
						engine.dragpoint(w3, (x1 + x2) / 2, (y1 + y2) / 2 - 1);

					y1 = wall[w2].y;
					w2 = wall[wall[wall[w2].nextwall].point2].point2;
					floordoor[k].dist2 = abs(y1 - wall[w2].y) - 50;
				}
				break;
			default:
				break;
			}
			if (sector[j].hitag >= SECTORVEHICLETAG && sector[j].hitag < SECTORVEHICLETAG + MAXSECTORVEHICLES) {
				k = sector[j].hitag - SECTORVEHICLETAG;
				if (sectorvehicle[k].pivotx == 0 && sectorvehicle[k].pivoty == 0) {
					numvehicles++;
					for (i = 0; i < MAXSPRITES; i++) {
						spr = sprite[i];
						if (spr.lotag == sector[j].hitag && spr.hitag == sector[j].hitag) {
							sectorvehicle[k].pivotx = spr.x;
							sectorvehicle[k].pivoty = spr.y;
							jsdeletesprite(i);
							break;
						}
					}
				}
				x1 = sectorvehicle[k].pivotx;
				y1 = sectorvehicle[k].pivoty;
				n = sectorvehicle[k].numpoints;
				startwall = sector[j].wallptr;
				endwall = startwall + sector[j].wallnum - 1;
				killwcnt = 0;
				for (i = startwall; i <= endwall; i++) {
					if (wall[i].lotag == 6) {
						sectorvehicle[k].killw[killwcnt++] = (short) i;
					}
					sectorvehicle[k].point[n] = (short) i;
					sectorvehicle[k].pointx[n] = x1 - wall[i].x;
					sectorvehicle[k].pointy[n] = y1 - wall[i].y;
					n++;
					if (n >= MAXVEHICLEPOINTS) {
						game.ThrowError("tekprepareboard: vehicle #" + sector[j].hitag + " has too many points");
					}
				}
				sectorvehicle[k].numpoints = (short) n;
				n = sectorvehicle[k].numsectors++;
				sectorvehicle[k].sector[n] = (short) j;
				sectorvehicle[k].soundindex = -1;
			}
		}

		sprelevcnt = 0;
		for (i = 0; i < MAXSPRITES; i++) {
			spr = sprite[i];

			if (spr.statnum < MAXSTATUS) {
				switch (spr.picnum) {
				case SNDFX_SECTOR:
				case SNDFX_AMBIENT:
				case SNDFX_TOGGLED:
					if (totalmapsndfx == MAXMAPSOUNDFX) {
						jsdeletesprite(i);
						break;
					}
					mapsndfx[totalmapsndfx].x = sprite[i].x;
					mapsndfx[totalmapsndfx].y = sprite[i].y;
					mapsndfx[totalmapsndfx].sector = sprite[i].sectnum;
					mapsndfx[totalmapsndfx].snum = sprite[i].lotag;
					mapsndfx[totalmapsndfx].id = -1;
					if (mapsndfx[totalmapsndfx].snum > TOTALSOUNDS) {
						mapsndfx[totalmapsndfx].snum = (TOTALSOUNDS - 1);
					}
					mapsndfx[totalmapsndfx].loops = sprite[i].hitag - 1;
					switch (spr.picnum) {
					case SNDFX_AMBIENT:
						mapsndfx[totalmapsndfx].type = MAP_SFX_AMBIENT;
						break;
					case SNDFX_SECTOR:
						mapsndfx[totalmapsndfx].type = MAP_SFX_SECTOR;
						break;
					case SNDFX_TOGGLED:
						mapsndfx[totalmapsndfx].type = MAP_SFX_TOGGLED;
						break;
					}
					totalmapsndfx++;
					jsdeletesprite(i);
					break;
				case SECTOREFFECT:
					if (spr.lotag < 1000) {
						continue;
					}
					s = spr.sectnum;

					if (sectoreffect[s].sectorflags == 0) {
						s = sexref[secnt++] = spr.sectnum;
						if (secnt == MAXSECTORS) {
							game.ThrowError("setupboard: Sector Effector limit exceeded");
						}
						sectoreffect[s].warpto = -1;
					}
					effect = flags32[spr.lotag - 1000];
					sectoreffect[s].sectorflags |= effect;
					if ((effect & flags32[QUICKCLOSE]) != 0) {
					} else if ((effect & flags32[SOUNDON]) != 0) {
						// match mapsndfx with same hitag
						sectoreffect[s].hi = spr.hitag;
					} else if ((effect & flags32[SOUNDOFF]) != 0) {
						// match mapsndfx with same hitag
						sectoreffect[s].hi = spr.hitag;
					} else if ((effect & flags32[PULSELIGHT]) != 0 || (effect & flags32[FLICKERLIGHT]) != 0
							|| (effect & flags32[FLICKERDELAY]) != 0 || (effect & flags32[BLINKDELAY]) != 0
							|| (effect & flags32[STEADYLIGHT]) != 0) {
						sectoreffect[s].lo = sector[s].floorshade;
						sectoreffect[s].hi = sector[s].ceilingshade;
						sectoreffect[s].animate = 1;
						if (spr.hitag != 0) {
							sectoreffect[s].triggerable = spr.hitag;
						} else {
							sectoreffect[s].triggerable = 0;
						}
						startwall = sector[s].wallptr;
						endwall = startwall + sector[s].wallnum - 1;
						for (j = startwall; j <= endwall; j++) {
							wall[j].shade = sector[s].floorshade;
						}
						sector[s].ceilingshade = sector[s].floorshade;
					} else if ((effect & flags32[DELAYEFFECT]) != 0) {
						sectoreffect[s].delay = spr.hitag;
						sectoreffect[s].delayreset = sectoreffect[s].delay;
					}
					if ((effect & flags32[WPANNING]) != 0) {
						angle = sectoreffect[s].ang = spr.ang;
						sectoreffect[s].sin = sintable[((angle + 2048) & 2047)];
						sectoreffect[s].cos = sintable[((angle + 2560) & 2047)];
						startwall = sector[s].wallptr;
						endwall = startwall + sector[s].wallnum - 1;
						for (j = startwall; j <= endwall; j++) {
							if (wall[j].lotag == 0) {
								wall[j].lotag = spr.lotag;
							}
						}
					}
					if ((effect & flags32[FPANNING]) != 0 || (effect & flags32[CPANNING]) != 0) {
						angle = sectoreffect[s].ang = spr.ang;
						sectoreffect[s].sin = sintable[((angle + 2048) & 2047)];
						sectoreffect[s].cos = sintable[((angle + 2560) & 2047)];
					}
					if ((effect & flags32[WARPSECTOR]) != 0) {
						for (j = 0; j < MAXSECTORS; j++) {
							if (sector[j] != null && sector[j].hitag == spr.hitag) {
								sectoreffect[s].warpto = j;
								j = (short) MAXSECTORS;
							}
						}
					}
					if ((effect & flags32[KILLSECTOR]) != 0) {
						if (spr.hitag == 0) {
							sectoreffect[s].damage = 9999;
						} else {
							sectoreffect[s].damage = spr.hitag;
						}
					}
					if ((effect & flags32[DOORSPEEDEFFECT]) != 0) {
						doortype[doorxref[spr.sectnum]].step = spr.hitag;
					}
					// jeff added 9-20
					if ((effect & flags32[DOORSUBTYPE]) != 0) {
						doortype[doorxref[spr.sectnum]].subtype = spr.hitag;
					}
					jsdeletesprite(i);
					break;
				default:
					if (spr.lotag >= SECTORVEHICLETAG && spr.lotag < SECTORVEHICLETAG + MAXSECTORVEHICLES) {
						k = spr.lotag - SECTORVEHICLETAG;
						n = spr.hitag;

						if (spr.picnum != SPEEDPIC) {
							sectorvehicle[k].trackx[n] = spr.x;
							sectorvehicle[k].tracky[n] = spr.y;
						}

						if (spr.picnum == STOPPIC) {
							sectorvehicle[k].stop[n] = 1;
							sectorvehicle[k].waitdelay = CLKIPS * 8;
						} else {
							if (spr.picnum != SPEEDPIC)
								sectorvehicle[k].stop[n] = 0;
						}
						if (spr.picnum == SPEEDPIC) {
							if (sectorvehicle[k].speedto == 0 && spr.hitag > 0) {
								sectorvehicle[k].speedto = spr.hitag;
								sectorvehicle[k].movespeed = sectorvehicle[k].speedto;
							}
							jsdeletesprite(i);
							break;
						}
						sectorvehicle[k].tracknum++;
						jsdeletesprite(i);
					}
					if ((spr.lotag < 2000) && (spr.lotag >= SPRITEELEVATORTAG)) {

						j = (short) (spr.lotag - SPRITEELEVATORTAG);
						if (spr.hitag >= 100) {
							k = (spr.hitag - 100) + 1;
							if (k >= MAXELEVFLOORS) {
								game.ThrowError(
										"setupboard: Only " + MAXELEVFLOORS + " levels allowed for sprite elevators");
							}
							spriteelev[j].floorz[k] = spr.z;
							spriteelev[j].floors++;
						} else {
							k = spriteelev[j].parts;
							spriteelev[j].sprnum[k] = i;
							if (spr.hitag == 6 || spr.hitag == 7) {
								if (spriteelev[j].floorpos == 0) {
									sprelevcnt++;
								}
								spriteelev[j].door[spriteelev[j].doors] = i;
								spriteelev[j].floorz[0] = spr.z;
								spriteelev[j].floorpos = spr.z;
								spriteelev[j].doors++;
							}
							spriteelev[j].startz[k] = spr.z;
							spriteelev[j].parts++;
						}
					}
					break;
				}
			}

		}

		numanimates = 0;
		for (j = 0; j < MAXANIMPICS; j++) 
			animpic[j].set(0);
		numdelayfuncs = 0;
		for (j = 0; j < MAXDELAYFUNCTIONS; j++) 
			delayfunc[j].set(0);
	}

	public static void movedoors(int d) {
		int hitag, i, s, sx;
		short j;
		int size, z;
		Doortype door;
		SPRITE spr;
		WALL wallt;
		int stayopen;

		door = doortype[d];
		s = door.sector;

		switch (door.state) {

		case D_NOTHING:
			break;

		case D_WAITING:
			stayopen = 0;
			for (i = 0; i < secnt; i++) {
				sx = sexref[i];
				if (sx == door.sector) {
					if (((sectoreffect[sx].sectorflags) & flags32[QUICKCLOSE]) != 0) {
						if (mission == 7) {
							stayopen = 1;
						} else {
							door.delay = 0;
						}
					}
				}
			}
			if (stayopen == 0) {
				door.delay -= TICSPERFRAME;
			}
			if (door.delay <= 0) {
				door.delay = 0;
				if (door.type < PLATFORMELEVTAG) {
					for (i = connecthead; i >= 0; i = connectpoint2[i]) {
						if (gPlayer[i].cursectnum == s) {
							door.delay = DOORDELAY;
							break;
						}
					}
				}
				if (door.delay == 0) {
					door.state = D_CLOSEDOOR;
				}
			}
			break;

		case D_OPENDOOR:
			switch (door.type) {
			case DOORUPTAG:
				switch (door.subtype) {
				case DST_BAYDOOR:
					playsound(S_BAYDOOR_OPEN, door.centx, door.centy, 0, ST_UPDATE);
					if (baydoorloop == -1) {
						baydoorloop = playsound(S_BAYDOORLOOP, door.centx, door.centy, 20, ST_UNIQUE);
					}
					break;
				case DST_HYDRAULICDOOR:
					playsound(S_AIRDOOR_OPEN, door.centx, door.centy, 0, ST_UPDATE);
					playsound(S_AIRDOOR, door.centx, door.centy, 0, ST_UPDATE);
					break;

				case DST_ELEVATORDOOR:
					playsound(S_ELEVATOR_DOOR, door.centx, door.centy, 0, ST_UPDATE);
					break;

				case DST_MATRIXDOOR1:
					playsound(S_MATRIX1, door.centx, door.centy, 0, ST_UPDATE);
					break;

				case DST_MATRIXDOOR2:
					playsound(S_MATRIX2, door.centx, door.centy, 0, ST_UPDATE);
					break;
				case DST_MATRIXDOOR3:
					playsound(S_MATRIX3, door.centx, door.centy, 0, ST_UPDATE);
					break;
				case DST_MATRIXDOOR4:
					playsound(S_MATRIX4, door.centx, door.centy, 0, ST_UPDATE);
					break;

				default:
					if (mission == 7) {
						playsound(S_MATRIXDOOR2, door.centx, door.centy, 0, ST_UPDATE);
					} else {
						playsound(S_UPDOWNDR2_OP, door.centx, door.centy, 0, ST_UPDATE);
					}
					break;
				}
				door.goalz[0] = sector[engine.nextsectorneighborz(s, sector[s].floorz, -1, -1)].ceilingz;
				break;
			case DOORDOWNTAG:
				playsound(S_BIGSWINGOP, door.centx, door.centy, 0, ST_UPDATE);

				door.goalz[0] = sector[engine.nextsectorneighborz(s, sector[s].ceilingz, 1, 1)].floorz;

				break;

			case PLATFORMDROPTAG:
				door.goalz[0] = sector[engine.nextsectorneighborz(s, sector[s].ceilingz, 1, 1)].floorz;
				break;
			case DOORSPLITHOR:
				if (mission == 7) {
					playsound(S_MATRIXDOOR1, door.centx, door.centy, 0, ST_UPDATE);
				} else {
					playsound(S_WH_7, door.centx, door.centy, 0, ST_UPDATE);
				}
				int s1 = engine.nextsectorneighborz(s, sector[s].ceilingz, -1, -1);
				int s2 = engine.nextsectorneighborz(s, sector[s].floorz, 1, 1);
				if(s1 == -1 || s2 == -1)
					break;
				
				door.goalz[0] = sector[s1].ceilingz;
				door.goalz[2] = sector[s2].floorz;
				break;
			case DOORSPLITVER:
				playsound(S_SIDEDOOR1, door.centx, door.centy, 0, ST_UPDATE);
				door.goalz[0] = door.points[0];
				door.goalz[1] = door.points[1];
				door.goalz[2] = door.points[2];
				door.goalz[3] = door.points[3];
				break;
			case BOXDELAYTAG:
			case PLATFORMDELAYTAG:
				playsound(S_PLATFORMSTART, door.centx, door.centy, 0, ST_UPDATE);
				if (loopinsound == -1) {
					loopinsound = playsound(S_PLATFORMLOOP, door.centx, door.centy, 20, ST_UNIQUE);
				}
				door.goalz[0] = elevator[s].lolevel;
				break;
			default:
				break;
			}
			door.state = D_OPENING;
			break;

		case D_CLOSEDOOR:
			switch (door.type) {
			case DOORUPTAG:
				switch (door.subtype) {
				case DST_BAYDOOR:
					playsound(S_BAYDOOR_OPEN, door.centx, door.centy, 0, ST_UPDATE);
					if (baydoorloop == -1) {
						baydoorloop = playsound(S_BAYDOORLOOP, door.centx, door.centy, 20, ST_UNIQUE);
					}
					break;

				case DST_HYDRAULICDOOR:
					playsound(S_AIRDOOR_OPEN, door.centx, door.centy, 0, ST_UPDATE);
					playsound(S_AIRDOOR, door.centx, door.centy, 0, ST_UPDATE);
					break;

				case DST_ELEVATORDOOR:
					playsound(S_ELEVATOR_DOOR, door.centx, door.centy, 0, ST_UPDATE);
					break;

				case DST_MATRIXDOOR1:
					playsound(S_MATRIX1, door.centx, door.centy, 0, ST_UPDATE);
					break;

				case DST_MATRIXDOOR2:
					playsound(S_MATRIX2, door.centx, door.centy, 0, ST_UPDATE);
					break;
				case DST_MATRIXDOOR3:
					playsound(S_MATRIX3, door.centx, door.centy, 0, ST_UPDATE);
					break;
				case DST_MATRIXDOOR4:
					playsound(S_MATRIX4, door.centx, door.centy, 0, ST_UPDATE);
					break;

				default:
					if (mission == 7) {
						playsound(S_MATRIXDOOR2, door.centx, door.centy, 0, ST_UPDATE);
					} else {
						playsound(S_UPDOWNDR2_CL, door.centx, door.centy, 0, ST_UPDATE);
					}
					break;
				}
				door.goalz[0] = sector[engine.nextsectorneighborz(s, sector[s].ceilingz, 1, 1)].floorz;
				break;
			case DOORDOWNTAG:
				playsound(S_BIGSWINGOP, door.centx, door.centy, 0, ST_UPDATE);
				door.goalz[0] = sector[s].ceilingz;
				break;
			case DOORSPLITHOR:
				if (mission == 7) {
					playsound(S_MATRIXDOOR1, door.centx, door.centy, 0, ST_UPDATE);
				} else {
					playsound(S_WH_7, door.centx, door.centy, 0, ST_UPDATE);
				}
				door.goalz[0] = door.centz;
				door.goalz[2] = door.centz;
				break;
			case DOORSPLITVER:
				playsound(S_SIDEDOOR2, door.centx, door.centy, 0, ST_UPDATE);
				if (wall[door.walls[0]].x == wall[door.walls[3]].x) {
					door.goalz[0] = door.centy;
					door.goalz[2] = door.centy;
				} else {
					door.goalz[0] = door.centx;
					door.goalz[2] = door.centx;
				}
				door.goalz[1] = door.points[0];
				door.goalz[3] = door.points[2];
				break;
			case BOXELEVTAG:
			case PLATFORMELEVTAG:
				door.state = D_NOTHING;
				break;
			case BOXDELAYTAG:
			case PLATFORMDELAYTAG:
				playsound(S_PLATFORMSTART, door.centx, door.centy, 0, ST_UPDATE);
				if (loopinsound == -1) {
					loopinsound = playsound(S_PLATFORMLOOP, door.centx, door.centy, 20, ST_UNIQUE);
				}
				door.goalz[0] = elevator[s].hilevel;
				break;
			default:
				break;
			}
			door.state = D_CLOSING;
			if ((hitag = sector[s].hitag) > 0) {
				for (i = 0; i < MAXSPRITES; i++) {
					spr = sprite[i];
					if (spr.hitag == hitag) {
						switch (spr.picnum) {
						case SWITCH2ON:
							spr.picnum = SWITCH2OFF;
							break;
						case SWITCH3ON:
							spr.picnum = SWITCH3OFF;
							break;
						}
					}
				}
				for (i = 0; i < numwalls; i++) {
					wallt = wall[i];
					if (wallt.hitag == hitag) {
						switch (wallt.picnum) {
						case SWITCH2ON:
							wallt.picnum = SWITCH2OFF;
							break;
						case SWITCH3ON:
							wallt.picnum = SWITCH3OFF;
							break;
						}
					}
				}
			}
			break;

		case D_OPENING:
			if (door.type == DOORUPTAG)
				game.pInt.setceilinterpolate(s, sector[s]);
			else
				game.pInt.setfloorinterpolate(s, sector[s]);
			switch (door.type) {
			case DOORUPTAG:
			case DOORDOWNTAG:
			case PLATFORMDROPTAG:
				if (door.type == DOORUPTAG) {
					z = sector[s].ceilingz;
				} else {
					z = sector[s].floorz;
				}
				z = stepdoor(z, door.goalz[0], door, D_OPENSOUND);
				if (door.type == DOORUPTAG)
					sector[s].ceilingz = z;

				else
					sector[s].floorz = z;
				break;
			case DOORSPLITHOR:
				z = sector[s].ceilingz;
				z = stepdoor(z, door.goalz[0], door, D_OPENSOUND);
				sector[s].ceilingz = z;
				z = sector[s].floorz;
				z = stepdoor(z, door.goalz[2], door, D_OPENSOUND);
				sector[s].floorz = z;
				break;
			case DOORSPLITVER:
				if (wall[door.walls[0]].x == wall[door.walls[3]].x) {
					for (i = 0; i < 8; i++) {
						j = door.walls[i];
						z = wall[j].y;
						z = stepdoor(z, door.goalz[i >> 1], door, D_OPENSOUND);
						engine.dragpoint(j, wall[j].x, z);
					}
				} else {
					for (i = 0; i < 8; i++) {
						j = door.walls[i];
						z = wall[j].x;
						z = stepdoor(z, door.goalz[i >> 1], door, D_OPENSOUND);
						engine.dragpoint(j, z, wall[j].y);
					}
				}
				break;
			case BOXELEVTAG:
			case PLATFORMELEVTAG:
			case BOXDELAYTAG:
			case PLATFORMDELAYTAG:
				size = sector[s].ceilingz - sector[s].floorz;
				z = sector[s].floorz;
				z = stepdoor(z, door.goalz[0], door, D_OPENSOUND);
				sector[s].floorz = z;
				if (door.type == BOXDELAYTAG || door.type == BOXELEVTAG) {
					sector[s].ceilingz = sector[s].floorz + size;
				}
				break;
			default:
				break;
			}
			break;

		case D_CLOSING:
			if (door.type == DOORUPTAG)
				game.pInt.setceilinterpolate(s, sector[s]);
			else
				game.pInt.setfloorinterpolate(s, sector[s]);
			switch (door.type) {
			case DOORUPTAG:
			case DOORDOWNTAG:
				if (door.type == DOORUPTAG) {
					z = sector[s].ceilingz;
				} else {
					z = sector[s].floorz;
				}
				z = stepdoor(z, door.goalz[0], door, D_SHUTSOUND);
				if (door.type == DOORUPTAG) {
					sector[s].ceilingz = z;
				} else {
					sector[s].floorz = z;
				}
				break;
			case DOORSPLITHOR:
				z = sector[s].ceilingz;
				z = stepdoor(z, door.goalz[0], door, D_SHUTSOUND);
				sector[s].ceilingz = z;
				z = sector[s].floorz;
				z = stepdoor(z, door.goalz[2], door, D_SHUTSOUND);
				sector[s].floorz = z;
				break;
			case DOORSPLITVER:
				i = headspritesect[s];
				if (i != -1) {
					door.state = D_OPENDOOR;
				}
				for (i = connecthead; i >= 0; i = connectpoint2[i]) {
					if (engine.inside(gPlayer[i].posx, gPlayer[i].posy, (short) s) != 0) {
						door.state = D_OPENDOOR;
					}
				}
				if (wall[door.walls[0]].x == wall[door.walls[3]].x) {
					for (i = 0; i < 8; i++) {
						j = door.walls[i];
						z = wall[j].y;
						z = stepdoor(z, door.goalz[i >> 1], door, D_SHUTSOUND);
						engine.dragpoint(j, wall[j].x, z);
					}
				} else {
					for (i = 0; i < 8; i++) {
						j = door.walls[i];
						z = wall[j].x;
						z = stepdoor(z, door.goalz[i >> 1], door, D_SHUTSOUND);
						engine.dragpoint(j, z, wall[j].y);
					}
				}
				break;
			case BOXDELAYTAG:
			case PLATFORMDELAYTAG:
				size = sector[s].ceilingz - sector[s].floorz;
				z = sector[s].floorz;
				z = stepdoor(z, door.goalz[0], door, D_SHUTSOUND);
				sector[s].floorz = z;
				if (door.type == BOXDELAYTAG) {
					sector[s].ceilingz = sector[s].floorz + size;
				}
				break;
			default:
				break;
			}
			break;

		case D_OPENSOUND:
			switch (door.type) {
			case DOORUPTAG:
				switch (door.subtype) {
				case DST_BAYDOOR:
					playsound(S_BAYDOOR_CLOSE, door.centx, door.centy, 0, ST_UPDATE);
					if (baydoorloop >= 0) {
						stopsound(baydoorloop);
						baydoorloop = -1;
					}
					break;
				case DST_HYDRAULICDOOR:
					playsound(S_AIRDOOR_CLOSE, door.centx, door.centy, 0, ST_UPDATE);
					break;

				case DST_ELEVATORDOOR:
				case DST_MATRIXDOOR1:
				case DST_MATRIXDOOR2:
				case DST_MATRIXDOOR3:
				case DST_MATRIXDOOR4:
					break;
				default:
					if (mission != 7) {
						playsound(S_DOORKLUNK, door.centx, door.centy, 0, ST_UPDATE);
					}
					break;
				}
				door.state = D_WAITING;
				showsect2d(door.sector, door.goalz[0]);
				break;

			case DOORDOWNTAG:
				playsound(S_WH_6, door.centx, door.centy, 0, ST_UPDATE);
				showsect2dtoggle(door.sector, 0);
				door.state = D_WAITING;
				break;

			case BOXELEVTAG:
			case PLATFORMELEVTAG:
			case PLATFORMDROPTAG:
				door.state = D_WAITING;
				showsect2d(door.sector, door.goalz[0]);
				break;
			case PLATFORMDELAYTAG:
			default:
				if (door.type == BOXDELAYTAG || door.type == PLATFORMDELAYTAG) {
					playsound(S_PLATFORMSTOP, door.centx, door.centy, 0, ST_UPDATE);
					if (loopinsound >= 0) {
						stopsound(loopinsound);
						loopinsound = -1;
					}
					showsect2d(door.sector, door.goalz[0]);
				} else {
					showsect2dtoggle(door.sector, 0);
				}
				door.state = D_WAITING;
				break;
			}
			break;

		case D_SHUTSOUND:
			switch (door.type) {
			case DOORUPTAG:
				switch (door.subtype) {
				case DST_BAYDOOR:
					playsound(S_BAYDOOR_CLOSE, door.centx, door.centy, 0, ST_UPDATE);
					if (baydoorloop >= 0) {
						stopsound(baydoorloop);
						baydoorloop = -1;
					}
					break;

				case DST_HYDRAULICDOOR:
					playsound(S_AIRDOOR_CLOSE, door.centx, door.centy, 0, ST_UPDATE);
					break;

				case DST_ELEVATORDOOR:
				case DST_MATRIXDOOR1:
				case DST_MATRIXDOOR2:
				case DST_MATRIXDOOR3:
				case DST_MATRIXDOOR4:
					break;
				default:
					if (mission != 7) {
						playsound(S_DOORKLUNK, door.centx, door.centy, 0, ST_UPDATE);
					}
					break;
				}
				door.state = D_NOTHING;
				showsect2d(door.sector, door.goalz[0]);
				break;

			case DOORDOWNTAG:
				showsect2dtoggle(door.sector, 1);
				playsound(S_WH_6, door.centx, door.centy, 0, ST_UPDATE);
				break;

			case BOXELEVTAG:
			case PLATFORMELEVTAG:
			case BOXDELAYTAG:
			case PLATFORMDELAYTAG:
				playsound(S_PLATFORMSTOP, door.centx, door.centy, 0, ST_UPDATE);
				if (loopinsound >= 0) {
					stopsound(loopinsound);
					loopinsound = -1;
				}
				showsect2d(door.sector, door.goalz[0]);
				break;
			default:
				showsect2dtoggle(door.sector, 1);
				break;
			}
			door.state = D_NOTHING;
			break;
		}

	}

	public static void showwall2d(int w, int onoff) {
		if (onoff != 0) {
			show2dwall[w >> 3] |= (1 << (w & 7));
		} else {
			show2dwall[w >> 3] &= ~(1 << (w & 7));
		}
	}

	public static void showsect2d(int s, int z) {
		int endwall, i, startwall;

		startwall = sector[s].wallptr;
		endwall = startwall + sector[s].wallnum - 1;
		for (i = startwall; i <= endwall; i++) {
			if (wall[i].nextwall != -1) {
				if (sector[wall[i].nextsector].floorz == z) {
					showwall2d(i, 0);
					showwall2d(wall[i].nextwall, 0);
				} else {
					showwall2d(i, 1);
					showwall2d(wall[i].nextwall, 1);
				}
			}
		}
	}

	public static void showsect2dtoggle(int s, int onoff) {
		int endwall, i, startwall;

		startwall = sector[s].wallptr;
		endwall = startwall + sector[s].wallnum - 1;
		for (i = startwall; i <= endwall; i++) {
			if (wall[i].nextwall != -1) {
				showwall2d(i, onoff);
				showwall2d(wall[i].nextwall, onoff);
			}
		}
	}

	public static int testneighborsectors(int sect1, int sect2) {
		short i, startwall, num1, num2;

		num1 = sector[sect1].wallnum;
		num2 = sector[sect2].wallnum;

		// traverse walls of sector with fewest walls (for speed)
		if (num1 < num2) {
			startwall = sector[sect1].wallptr;
			for (i = (short) (num1 - 1); i >= 0; i--)
				if (wall[i + startwall].nextsector == sect2)
					return (1);
		} else {
			startwall = sector[sect2].wallptr;
			for (i = (short) (num2 - 1); i >= 0; i--)
				if (wall[i + startwall].nextsector == sect1)
					return (1);
		}
		return (0);
	}

	public static void warpsprite(short spritenum) {
		SPRITE spr = sprite[spritenum];
		warp(spr.x, spr.y, spr.z, spr.ang, spr.sectnum);

		game.pInt.setsprinterpolate(spritenum, spr);
		spr.x = warpx;
		spr.y = warpy;
		spr.z = warpz;
		spr.ang = (short) warpang;

		engine.changespritesect(spritenum, warpsect);
	}

	public static int warpx, warpy, warpz, warpang;
	public static short warpsect;

	public static void warp(int x, int y, int z, int daang, short dasector) {
		short startwall, endwall, s;
		int i, j, dax, day;
		warpx = x;
		warpy = y;
		warpz = z;
		warpang = daang;
		warpsect = dasector;

		for (i = 0; i < warpsectorcnt; i++) {
			if (warpsectorlist[i] == warpsect) {
				j = sector[warpsect].hitag;
				do {
					i++;
					if (i >= warpsectorcnt)
						i = 0;
				} while (sector[warpsectorlist[i]].hitag != j);
				warpsect = warpsectorlist[i];
				break;
			}
		}

		// find center of sector
		startwall = sector[warpsect].wallptr;
		endwall = (short) (startwall + sector[warpsect].wallnum - 1);
		dax = 0;
		day = 0;
		for (s = startwall; s <= endwall; s++) {
			dax += wall[s].x;
			day += wall[s].y;
			if (wall[s].nextsector >= 0) {
				i = s;
			}
		}
		warpx = dax / (endwall - startwall + 1);
		warpy = day / (endwall - startwall + 1);
		warpz = sector[warpsect].floorz - (42 << 8);

		warpsect = engine.updatesector(warpx, warpy, warpsect);
	}

	public static final short[] opwallfind = new short[2];

	public static void operatesector(int dasector) { // Door code
		int i, j, datag;
		int dax2, day2, centx, centy;
		short startwall, endwall;

		datag = sector[dasector].lotag;

		// lights out / on
		if (datag == 33) {
			if (sector[dasector].visibility >= 212) {
				sector[dasector].visibility = 0;
			} else {
				sector[dasector].visibility = (byte) 212;
			}
			return;
		}

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

		// kens swinging door
		if (datag == 13) {
			for (i = 0; i < swingcnt; i++) {
				if (swingsector[i] == dasector) {
					if (swinganginc[i] == 0) {
						if (swingang[i] == swingangclosed[i]) {
							swinganginc[i] = swingangopendir[i];
						} else {
							swinganginc[i] = (short) -swingangopendir[i];
						}
					} else {
						swinganginc[i] = (short) -swinganginc[i];
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
		}

		tekoperatesector(dasector);
	}

	public static int sectorblocked(int s) {
		int i, rv;

		rv = 0;

		for (i = connecthead; i >= 0; i = connectpoint2[i]) {
			if (gPlayer[i].cursectnum == s)
				rv = 1;
			if (testneighborsectors(gPlayer[i].cursectnum, s) == 1)
				rv = 1;
		}
		if (headspritesect[s] != -1)
			rv = 1;

		return (rv);
	}

//	public static short neartagsector, neartagwall, neartagsprite;
//	public static long neartagdist, neartaghitdist;

	public static void tekswitchtrigger(int snum, int spr) {
		int i, j;
		long nexti;

		j = sprite[spr].picnum;

		switch (j) {
		case SWITCH2OFF:
			if (gPlayer[snum].invredcards == 0) {
				showmessage("PASSAGE REQUIRES RED KEYCARD");
				return;
			}
			break;
		case SWITCH4OFF:
			if (gPlayer[snum].invbluecards == 0) {
				showmessage("PASSAGE REQUIRES BLUE KEYCARD");
				return;
			}
			break;
		}

		switch (j) {
		case SWITCH2ON:
		case SWITCH2OFF:
		case SWITCH3ON:
		case SWITCH3OFF:
		case SWITCH4ON:
		case SWITCH4OFF:
			int dax = sprite[spr].x;
			int day = sprite[spr].y;
			playsound(S_KEYCARDBLIP, dax, day, 0, ST_UPDATE);
			break;
		default:
			break;
		}

		if (j == SWITCH2ON)
			sprite[spr].picnum = SWITCH2OFF;
		if (j == SWITCH2OFF)
			sprite[spr].picnum = SWITCH2ON;
		if (j == SWITCH3ON)
			sprite[spr].picnum = SWITCH3OFF;
		if (j == SWITCH3OFF)
			sprite[spr].picnum = SWITCH3ON;
		if (j == SWITCH4ON)
			sprite[spr].picnum = SWITCH4OFF;
		if (j == SWITCH4OFF)
			sprite[spr].picnum = SWITCH4ON;

		if (j == 3708)
			sprite[spr].picnum = 3709;
		if (j == 3709)
			sprite[spr].picnum = 3708;

		for (i = 0; i < numsectors; i++)
			if (sector[i].hitag == sprite[spr].hitag)
				if (sector[i].lotag != 1)
					operatesector(i);

		i = headspritestat[0];
		while (i != -1) {
			nexti = nextspritestat[i];
			if (sprite[i].hitag == sprite[spr].hitag)
				operatesprite(i);
			i = (int) nexti;
		}

	}

	public static final int NEWMAPLOTAG = 25;

	public static void teknewsector(int p) {
		int i, n, nexti, s;
		short sn;
		Sectoreffect septr;

		s = gPlayer[p].cursectnum;
		if (s == -1)
			return;

		septr = sectoreffect[s];

		if ((sector[s].lotag == NEWMAPLOTAG) && game.nNetMode != NetMode.Multiplayer) {
			if (mUserFlag == UserFlag.None) {
				changemap(sector[s].hitag);
				return;
			}
		}

		if (sector[gPlayer[p].cursectnum].lotag == 4) {
			playsound(S_SPLASH, gPlayer[p].posx, gPlayer[p].posy, 0, ST_UPDATE);
		}

		if (septr != null) {
			if (septr.warpto >= 0) {
				sn = gPlayer[p].playersprite;
				tekwarp(sprite[sn].x, sprite[sn].y, sprite[sn].z, septr.warpto);
				game.pInt.setsprinterpolate(sn, sprite[sn]);

				sprite[sn].x = tekwarpx;
				sprite[sn].y = tekwarpy;
				sprite[sn].z = tekwarpz;
				septr.warpto = tekwarsect;

				engine.changespritesect(sn, septr.warpto);
				gPlayer[p].posx = sprite[gPlayer[p].playersprite].x;
				gPlayer[p].posy = sprite[gPlayer[p].playersprite].y;
				gPlayer[p].posz = sprite[gPlayer[p].playersprite].z;
				gPlayer[p].ang = sprite[gPlayer[p].playersprite].ang;
				gPlayer[p].cursectnum = sprite[gPlayer[p].playersprite].sectnum;
				sprite[gPlayer[p].playersprite].z += (KENSPLAYERHEIGHT << 8);
			}
		}

		for (n = connecthead; n >= 0; n = connectpoint2[n]) {
			if ((sector[gPlayer[n].cursectnum].lotag == 1) || (sector[gPlayer[n].cursectnum].lotag == 2)) {
				for (i = 0; i < numsectors; i++) {
					if (sector[i].hitag == sector[gPlayer[n].cursectnum].hitag) {
						if ((sector[i].lotag != 1) && (sector[i].lotag != 2)) {
							operatesector(i);
						}
					}
				}
				i = headspritestat[0];
				while (i != -1) {
					nexti = nextspritestat[i];
					if (sprite[i].hitag == sector[gPlayer[n].cursectnum].hitag) {
						operatesprite(i);
					}
					i = nexti;
				}
			}
			if (TEKDEMO && sector[gPlayer[n].cursectnum].lotag == 10) {
				short newsect = sector[gPlayer[n].cursectnum].hitag;
				int x = wall[sector[newsect].wallptr].x;
				int y = wall[sector[newsect].wallptr].y;
				sn = gPlayer[p].playersprite;
				game.pInt.setsprinterpolate(sn, sprite[sn]);
				tekwarp(x, y, sector[newsect].floorz, newsect);
				sprite[sn].x = tekwarpx;
				sprite[sn].y = tekwarpy;
				sprite[sn].z = tekwarpz;
				septr.warpto = tekwarsect;

				engine.changespritesect(sn, newsect);
				gPlayer[p].posx = sprite[gPlayer[p].playersprite].x;
				gPlayer[p].posy = sprite[gPlayer[p].playersprite].y;
				gPlayer[p].posz = sprite[gPlayer[p].playersprite].z;
				gPlayer[p].ang = sprite[gPlayer[p].playersprite].ang;
				gPlayer[p].cursectnum = sprite[gPlayer[p].playersprite].sectnum;
				sprite[gPlayer[p].playersprite].z += (KENSPLAYERHEIGHT << 8);
			}
		}

		checkmapsndfx(p);
		sectortriggersprites(p);
	}

	public static int tekwarpx, tekwarpy, tekwarpz;
	public static short tekwarsect;

	public static void tekwarp(int x, int y, int z, short dasector) {
		short startwall, endwall, s;
		int dax, day;
		tekwarpx = x;
		tekwarpy = y;
		tekwarpz = z;
		tekwarsect = dasector;

		// find center of sector
		startwall = sector[tekwarsect].wallptr;
		endwall = (short) (startwall + sector[tekwarsect].wallnum - 1);
		dax = 0;
		day = 0;
		for (s = startwall; s <= endwall; s++) {
			dax += wall[s].x;
			day += wall[s].y;
		}
		tekwarpx = dax / (endwall - startwall + 1);
		tekwarpy = day / (endwall - startwall + 1);
		tekwarpz = sector[tekwarsect].floorz - (42 << 8);

		tekwarsect = engine.updatesector(tekwarpx, tekwarpy, tekwarsect);
	}

	public static int stepdoor(int z, int z2, Doortype door, int newstate) {
		if (z < z2) {
			z += (door.step * TICSPERFRAME);
			if (z >= z2) {
				door.delay = DOORDELAY;
				door.state = newstate;
				z = z2;
			}
		} else if (z > z2) {
			z -= (door.step * TICSPERFRAME);
			if (z <= z2) {
				door.delay = DOORDELAY;
				door.state = newstate;
				z = z2;
			}
		}
		return (z);
	}

	static short[] onveh = new short[MAXPLAYERS];

	public static void movevehicles(int v) {
		short a, ato, curang, i, n, p, rotang, s, sto, stoptrack, track;
		int distx, disty, px, py, x, y;
		int xvect, xvect2, yvect, yvect2;
		int lox, loy, hix, hiy;

		Sectorvehicle vptr = sectorvehicle[v];

		if (vptr.soundindex == -1) {

			for (i = 0; i < 32; i++) { // find mapno using names array
				if(boardfilename != null && boardfilename.equalsIgnoreCase(mapnames[i]))
					break;
			}
			switch (v) {
			case 0:
				switch (i) {
				case 4: // level1.map
					vptr.soundindex = playsound(S_TRAMBUSLOOP, vptr.pivotx, vptr.pivoty, -1, ST_VEHUPDATE);
					break;
				case 8: // city1.map
					vptr.soundindex = playsound(S_TRUCKLOOP, vptr.pivotx, vptr.pivoty, -1, ST_VEHUPDATE);
					break;
				case 11: // beach1.map
					vptr.soundindex = playsound(S_FORKLIFTLOOP, vptr.pivotx, vptr.pivoty, -1, ST_VEHUPDATE);
					break;
				case 17: // mid3.map
					vptr.soundindex = playsound(S_TRAMBUSLOOP, vptr.pivotx, vptr.pivoty, -1, ST_VEHUPDATE);
					break;
				case 19: // sewer2.map
					vptr.soundindex = playsound(S_FORKLIFTLOOP, vptr.pivotx, vptr.pivoty, -1, ST_VEHUPDATE);
					break;
				case 20: // inds1.map
					vptr.soundindex = playsound(S_FORKLIFTLOOP, vptr.pivotx, vptr.pivoty, -1, ST_VEHUPDATE);
					break;
				case 25: // ware1.map
					vptr.soundindex = playsound(S_FORKLIFTLOOP, vptr.pivotx, vptr.pivoty, -1, ST_VEHUPDATE);
					break;
				case 26: // ware2.map
					vptr.soundindex = playsound(S_TRUCKLOOP, vptr.pivotx, vptr.pivoty, -1, ST_VEHUPDATE);
					break;
				default:
					break;
				}
				break;
			case 1:
				switch (i) {
				case 4: // level1.map
					vptr.soundindex = playsound(S_TRAMBUSLOOP, vptr.pivotx, vptr.pivoty, -1, ST_VEHUPDATE);
					break;
				case 11: // beach1.map
					vptr.soundindex = playsound(S_BOATLOOP, vptr.pivotx, vptr.pivoty, -1, ST_VEHUPDATE);
					break;
				case 26: // ware2.map
					vptr.soundindex = playsound(S_CARTLOOP, vptr.pivotx, vptr.pivoty, -1, ST_VEHUPDATE);
					break;
				default:
					break;
				}
				break;
			default:
				break;
			}
		}

		if (vptr.waittics > 0) {
			vptr.waittics -= TICSPERFRAME;
			if (vptr.soundindex != -1) {
				updatevehiclesnds(vptr.soundindex, vptr.pivotx, vptr.pivoty);
			}
			return;
		}

		px = vptr.pivotx;
		py = vptr.pivoty;

		track = vptr.track;
		distx = vptr.distx;
		disty = vptr.disty;

		stoptrack = vptr.stoptrack;
		if (vptr.stop[track] != 0 && (x = distx + disty) > 0 && x < 8192) {
			vptr.accelto = 2;
			vptr.speedto = 32;
		} else if (vptr.accelto != 8) {
			vptr.accelto = 8;
			vptr.speedto = vptr.movespeed;
		}
		if (distx == 0 && disty == 0) {
			if (vptr.stop[stoptrack] != 0) {
				for (i = 0; i < vptr.numsectors; i++) {
					s = vptr.sector[i];
					if (sector[s].lotag != 0) {
						operatesector(s);
					}
				}
				vptr.waittics = vptr.waitdelay;
				vptr.acceleration = 0;
				vptr.speed = 0;

			}
			distx = vptr.trackx[track] - px;
			disty = vptr.tracky[track] - py;
			vptr.angleto = (short) engine.getangle(distx, disty);
			vptr.accelto = 8;
			vptr.distx = abs(distx);
			vptr.disty = abs(disty);
			distx = vptr.distx;
			disty = vptr.disty;
		}
		a = vptr.acceleration;
		ato = vptr.accelto;
		if (a < ato) {
			a += TICSPERFRAME;
			if (a > ato) {
				a = ato;
			}
			vptr.acceleration = a;
		} else if (a > ato) {
			a -= TICSPERFRAME;
			if (a < ato) {
				a = ato;
			}
			vptr.acceleration = a;
		}
		s = vptr.speed;
		sto = vptr.speedto;
		if (s > sto) {
			s -= a;
			if (s <= sto) {
				s = sto;
			}
			vptr.speed = s;
		} else if (s < sto) {
			s += a;
			if (s > sto) {
				s = sto;
			}
			vptr.speed = s;
		}
		rotang = curang = vptr.angle;
		if (curang != vptr.angleto) {
			vptr.angle = vptr.angleto;
			curang = vptr.angle;
		}
		xvect = (s * TICSPERFRAME * sintable[((curang + 2560) & 2047)]) >> 3;
		xvect2 = xvect >> 13;
		yvect = (s * TICSPERFRAME * sintable[((curang + 2048) & 2047)]) >> 3;
		yvect2 = yvect >> 13;
		distx -= abs(xvect2);
		if (distx < 0) {
			if (xvect2 < 0) {
				xvect2 -= distx;
			} else {
				xvect2 += distx;
			}
			distx = 0;
			vptr.angleto = (short) engine.getangle(vptr.trackx[track] - px, vptr.tracky[track] - py);
		}
		disty -= abs(yvect2);
		if (disty < 0) {
			if (yvect2 < 0) {
				yvect2 -= disty;
			} else {
				yvect2 += disty;
			}
			disty = 0;
			vptr.angleto = (short) engine.getangle(vptr.trackx[track] - px, vptr.tracky[track] - py);
		}
		if (distx == 0 && disty == 0) {
			vptr.stoptrack = track;
			track = (short) ((track + 1) % vptr.tracknum);
			vptr.track = track;
			switch (v) {
			// jsa vehicles
			case 0:
				if (boardfilename != null && (boardfilename.toUpperCase().equals("CITY1.MAP")
						|| boardfilename.toUpperCase().equals("WARE2.MAP")))
					playsound(S_TRUCKSTOP, vptr.pivotx, vptr.pivoty, 0, ST_AMBUPDATE);
				break;
			default:
				break;
			}
		}
		vptr.distx = distx;
		vptr.disty = disty;
		px += xvect2;
		py += yvect2;
		n = vptr.numpoints;
		for (i = 0; i < n; i++) {
			p = vptr.point[i];
			x = vptr.pointx[i];
			y = vptr.pointy[i];
			Point out = engine.rotatepoint(px, py, px - x, py - y, curang);
			x = out.getX();
			y = out.getY();
			engine.dragpoint(p, x, y);
		}
		vptr.pivotx = px;
		vptr.pivoty = py;
		rotang = (short) (((curang - rotang) + 2048) & 2047);
		n = vptr.numsectors;

		lox = loy = 0x7FFFFFFF;
		hix = hiy = -(0x7FFFFFFF);
		for (i = 0; i < 4; i++) {
			a = vptr.killw[i];

			if (wall[a].x < lox) {
				lox = wall[a].x;
			} else if (wall[a].x > hix) {
				hix = wall[a].x;
			}
			if (wall[a].y < loy) {
				loy = wall[a].y;
			} else if (wall[a].y > hiy) {
				hiy = wall[a].y;
			}
		}

		for (i = 0; i < MAXPLAYERS; i++)
			onveh[i] = 0;
		for (i = 0; i < n; i++) {
			p = headspritesect[vptr.sector[i]];
			while (p >= 0) {
				s = nextspritesect[p];
				x = sprite[p].x;
				y = sprite[p].y;
				x += xvect2;
				y += yvect2;
				Point out = engine.rotatepoint(px, py, x, y, rotang);
				game.pInt.setsprinterpolate(p, sprite[p]);
				sprite[p].x = out.getX();
				sprite[p].y = out.getY();
				sprite[p].ang += rotang;
				p = s;
			}
			for (p = connecthead; p >= 0; p = connectpoint2[p]) {
				x = gPlayer[p].posx;
				y = gPlayer[p].posy;
				if (gPlayer[p].cursectnum == vptr.sector[i]) {
					x += xvect2;
					y += yvect2;
					Point out = engine.rotatepoint(px, py, x, y, rotang);
					gPlayer[p].posx = out.getX();
					gPlayer[p].posy = out.getY();
					gPlayer[p].ang += rotang;
					gPlayer[p].ang = BClampAngle(gPlayer[p].ang);
					onveh[p] = 1;
				}
			}
		}
		for (p = connecthead; p >= 0; p = connectpoint2[p]) {

			if (onveh[p] != 0) {
				continue;
			}
			x = gPlayer[p].posx;
			y = gPlayer[p].posy;

			if (x > lox && x < hix && y > loy && y < hiy) {
				if (!gPlayer[p].noclip && (gPlayer[p].health > 0) && (gPlayer[p].posz > VEHICLEHEIGHT)) {
					for (i = 0; i < 4; i++) {
						a = vptr.killw[i];
						if (engine.clipinsidebox(x, y, a, 128) != 0) {
							changehealth(p, -9999);
							changescore(p, -100);
							if (goreflag) {
								tekexplodebody(gPlayer[p].playersprite);
							}
						}
					}
				}
			}
		}
		if (vptr.soundindex != -1) {
			updatevehiclesnds(vptr.soundindex, vptr.pivotx, vptr.pivoty);
		}
	}

	public static void checkmapsndfx(int p) {

		int i, s;
		int dist;
		int effect;

		s = gPlayer[p].cursectnum;
		if (s < 0)
			return;
		Sectoreffect septr = sectoreffect[s];

		for (i = 0; i < totalmapsndfx; i++) {
			switch (mapsndfx[i].type) {
			case MAP_SFX_AMBIENT:
				dist = abs(gPlayer[p].posx - mapsndfx[i].x) + abs(gPlayer[p].posy - mapsndfx[i].y);
				if ((dist > AMBUPDATEDIST) && (mapsndfx[i].id != -1)) {
					stopsound(mapsndfx[i].id);
					mapsndfx[i].id = -1;
				} else if ((dist < AMBUPDATEDIST) && (mapsndfx[i].id == -1)) {
					mapsndfx[i].id = playsound(mapsndfx[i].snum, mapsndfx[i].x, mapsndfx[i].y, mapsndfx[i].loops,
							ST_AMBUPDATE);
				}
				break;
			case MAP_SFX_SECTOR:
				if ((gPlayer[p].cursectnum != gPlayer[p].ocursectnum)
						&& (gPlayer[p].cursectnum == mapsndfx[i].sector)) {
					mapsndfx[i].id = playsound(mapsndfx[i].snum, mapsndfx[i].x, mapsndfx[i].y, mapsndfx[i].loops,
							ST_UNIQUE);
				}
				break;
			default:
				break;
			}
		}

		if (boardfilename.toUpperCase().regionMatches(0, "SUBWAY", 0, 6)) { // Bstrcasecmp("SUBWAY", boardfilename, 6)
																			// == 0 ) {
			if (ambsubloop == -1) {
				ambsubloop = playsound(S_SUBSTATIONLOOP, 0, 0, -1, ST_IMMEDIATE);
			}
		} else {
			if (ambsubloop != -1) {
				stopsound(ambsubloop);
				ambsubloop = -1;
			}
		}

		if (septr != null) {
			effect = septr.sectorflags;
			if ((effect & flags32[SOUNDON]) != 0) {
				for (i = 0; i < totalmapsndfx; i++) {
					if (mapsndfx[i].type == MAP_SFX_TOGGLED) {
						if (sector[mapsndfx[i].sector].hitag == septr.hi) {
							if (mapsndfx[i].id == -1) {
								mapsndfx[i].id = playsound(mapsndfx[i].snum, mapsndfx[i].x, mapsndfx[i].y,
										mapsndfx[i].loops, ST_UNIQUE | ST_IMMEDIATE);
								;
							}
						}
					}
				}
			}
			if ((effect & flags32[SOUNDOFF]) != 0) {
				for (i = 0; i < totalmapsndfx; i++) {
					if (mapsndfx[i].type == MAP_SFX_TOGGLED) {
						if (sector[mapsndfx[i].sector].hitag == septr.hi) {
							if (mapsndfx[i].id != -1) {
								stopsound(mapsndfx[i].id);
								mapsndfx[i].id = -1;
							}
						}
					}
				}
			}
		}

//	     ambupdateclock=totalclock;
		ambupdateclock = (int) lockclock;
	}

	public static void tekoperatesector(int dasector) {
		short s;
		int datag, i;

		s = (short) dasector;
		datag = sector[s].lotag;

		switch (datag) {
		case BOXELEVTAG:
		case PLATFORMELEVTAG:
		case BOXDELAYTAG:
		case PLATFORMDELAYTAG:
		case DOORUPTAG: // a door that opens up
		case DOORDOWNTAG:
		case DOORSPLITHOR:
		case DOORSPLITVER:
		case PLATFORMDROPTAG:
			i = doorxref[s];
			if (i == -1) {
				game.ThrowError("operatesector: invalid door reference for sector " + s);
			}
			switch (doortype[i].state) {
			case D_NOTHING:
			case D_CLOSING:
			case D_CLOSEDOOR:
			case D_SHUTSOUND:
				doortype[i].state = D_OPENDOOR;
				break;
			default:
				if (datag != PLATFORMDROPTAG) {
					doortype[i].state = D_CLOSEDOOR;
				}
				break;
			}
			break;
		case DOORFLOORTAG:
			floordoor[fdxref[s]].state = DOOROPENING;
			playsound(S_FLOOROPEN, 0, 0, 0, ST_IMMEDIATE);
			break;
		}
	}

	public static void clearvehiclesoundindexes() {
		int i;

		for (i = 0; i < MAXSECTORVEHICLES; i++) {
			sectorvehicle[i].soundindex = -1;
		}
	}
}
