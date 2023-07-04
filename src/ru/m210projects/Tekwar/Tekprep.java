package ru.m210projects.Tekwar;

import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Tekwar.Globals.*;
import static ru.m210projects.Tekwar.View.*;

import java.io.FileNotFoundException;

import static ru.m210projects.Tekwar.Animate.*;
import static ru.m210projects.Tekwar.Main.*;
import static ru.m210projects.Tekwar.Names.*;
import static ru.m210projects.Tekwar.Tekchng.*;
import static ru.m210projects.Tekwar.Tekgun.*;
import static ru.m210projects.Tekwar.Tekmap.*;
import static ru.m210projects.Tekwar.Tekstat.*;
import static ru.m210projects.Tekwar.Tektag.*;
import static ru.m210projects.Tekwar.Player.*;

import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.FileHandle.Resource;
import ru.m210projects.Build.Types.BuildPos;
import ru.m210projects.Build.Types.InvalidVersionException;
import ru.m210projects.Build.Types.SECTOR;
import ru.m210projects.Tekwar.Types.SpriteXT;
import ru.m210projects.Tekwar.Types.Startspottype;

public class Tekprep {
	public static final int MAXSTARTSPOTS = 16;

	public static SpriteXT[] spriteXT = new SpriteXT[MAXSPRITES];
	public static int startx, starty, startz, starta, starts;

	static int firsttimethru = 1;

	static int coopmode, switchlevelsflag;

	public static int[] subwaysound = new int[4];
	static int startspotcnt;
	static Startspottype[] startspot = new Startspottype[MAXSTARTSPOTS];

	public static void setup3dscreen(int w, int h) {
		if (!engine.setgamemode(tekcfg.fullscreen, w, h))
			tekcfg.fullscreen = 0;

		tekcfg.ScreenWidth = BuildGdx.graphics.getWidth();
		tekcfg.ScreenHeight = BuildGdx.graphics.getHeight();
	}

	public static int tekpreinit() {
		int j, k, l;

		byte[] tempbuf = new byte[256];
		Resource fp;
		if ((fp = BuildGdx.cache.open("lookup.dat", 0)) != null) {
			l = fp.readByte() & 0xFF;
			for (j = 0; j < l; j++) {
				k = fp.readByte() & 0xFF;
				fp.read(tempbuf, 0, 256);
				engine.makepalookup(k, tempbuf, 0, 0, 0, 1);
			}
			fp.close();
		}
		if ((game.nNetMode == NetMode.Multiplayer) && ((fp = BuildGdx.cache.open("nlookup.dat", 0)) != null)) {
			l = fp.readByte() & 0xFF;
			for (j = 0; j < l; j++) {
				k = fp.readByte() & 0xFF;
				fp.read(tempbuf, 9, 256);
				engine.makepalookup(k + 15, tempbuf, 0, 0, 0, 1);
			}
			fp.close();
		}

		engine.makepalookup(255, tempbuf, 60, 60, 60, 1);

		pskyoff[0] = 0; // 2 tiles
		pskyoff[1] = 1;
		pskyoff[2] = 2;
		pskyoff[3] = 3;
		pskybits = 2; // tile 4 times, every 90 deg.
		parallaxtype = 1;
		parallaxyoffs = 112;
		if (game.nNetMode == NetMode.Multiplayer)
			gDifficulty = 2;

		return 0;
	}

	public static void initplayersprite(short snum) {
		int i;

		if (gPlayer[snum].playersprite >= 0)
			return;

		i = jsinsertsprite(gPlayer[snum].cursectnum, (short) 8);

		if (i == -1) {
			System.err.println("initplayersprite: jsinsertsprite on player " + snum + " failed");
		}
		gPlayer[snum].playersprite = (short) i;
		sprite[i].x = gPlayer[snum].posx;
		sprite[i].y = gPlayer[snum].posy;
		sprite[i].z = gPlayer[snum].posz + (KENSPLAYERHEIGHT << 8);
		sprite[i].cstat = 0x101;
		sprite[i].shade = 0;
		if (game.nNetMode != NetMode.Multiplayer) {
			sprite[i].pal = 0;
		} else {
			sprite[i].pal = (byte) (snum + 16);
		}
		sprite[i].clipdist = 32;
		sprite[i].xrepeat = 24;
		sprite[i].yrepeat = 24;
		sprite[i].xoffset = 0;
		sprite[i].yoffset = 0;
		sprite[i].picnum = DOOMGUY;
		sprite[i].ang = (short) gPlayer[snum].ang;
		sprite[i].xvel = 0;
		sprite[i].yvel = 0;
		sprite[i].zvel = 0;
		sprite[i].owner = (short) (snum + 4096);
		sprite[i].sectnum = gPlayer[snum].cursectnum;
		sprite[i].statnum = 8;
		sprite[i].lotag = 0;
		sprite[i].hitag = 0;
		// important to set extra = -1
		sprite[i].extra = -1;

		gPlayer[snum].pInput.Reset();

		tekrestoreplayer(snum);
	}

	static short[] dieframe = new short[MAXPLAYERS];

	public static void tekrestoreplayer(short snum) {
		resetEffects();
		engine.setsprite(gPlayer[snum].playersprite, gPlayer[snum].posx, gPlayer[snum].posy,
				gPlayer[snum].posz + (KENSPLAYERHEIGHT << 8));
		sprite[gPlayer[snum].playersprite].ang = (short) gPlayer[snum].ang;
		sprite[gPlayer[snum].playersprite].xrepeat = 24;
		sprite[gPlayer[snum].playersprite].yrepeat = 24;
		gPlayer[snum].horiz = 100;
		gPlayer[snum].health = MAXHEALTH;
		gPlayer[snum].fireseq = 0;
		restockammo(snum);
		stun[snum] = MAXSTUN;
		fallz[snum] = 0;
		gPlayer[snum].drawweap = 0;
		gPlayer[snum].invredcards = 0;
		gPlayer[snum].invbluecards = 0;
		gPlayer[snum].invaccutrak = 0;
		dieframe[snum] = 0;
		if (game.nNetMode == NetMode.Multiplayer) {
			gPlayer[snum].weapons = 0;
			gPlayer[snum].weapons = flags32[GUN2FLAG] | flags32[GUN3FLAG];
		} else {
			gPlayer[snum].weapons = 0;
			if (TEKDEMO)
				gPlayer[snum].weapons = flags32[GUN1FLAG] | flags32[GUN2FLAG];
			else
				gPlayer[snum].weapons = flags32[GUN1FLAG] | flags32[GUN2FLAG] | flags32[GUN3FLAG];
		}
	}

	static boolean RESETSCORE = false;

	public static boolean prepareboard(String daboardfilename) {
		short startwall, endwall, dasector;
		int i, j, k = 0, s, dax, day, dax2, day2;
		int l;

		boardfilename = daboardfilename;

		initsprites();

		if (firsttimethru != 0) {
//	    	getmessageleng = 0;
			engine.srand(17);
		}

		gAnimationCount = 0;

		BuildPos out = null;
		try {
			out = engine.loadboard(daboardfilename);
		} catch (FileNotFoundException | InvalidVersionException | RuntimeException e) {
			game.GameMessage(e.getMessage());
			return false;
		}

		startx = out.x;
		starty = out.y;
		startz = out.z;
		starta = out.ang;
		starts = out.sectnum;

		gViewMode = kView3D;
		zoom = 768;
		for (i = 0; i < MAXPLAYERS; i++) {
			gPlayer[i].posx = startx;
			gPlayer[i].posy = starty;
			gPlayer[i].posz = startz;
			gPlayer[i].ang = starta;
			gPlayer[i].cursectnum = (short) starts;
			gPlayer[i].ocursectnum = (short) starts;
			gPlayer[i].horiz = 100;
			gPlayer[i].lastchaingun = 0;
			gPlayer[i].health = 100;
			if (RESETSCORE)
				gPlayer[i].score = 0;

			gPlayer[i].numbombs = -1;
			gPlayer[i].deaths = 0;
			gPlayer[i].playersprite = -1;
			gPlayer[i].saywatchit = -1;
		}

		if (firsttimethru != 0) {
			for (i = 0; i < MAXPLAYERS; i++) {
				gPlayer[i].pInput.Reset();
			}
		}

		for (i = 0; i < MAXPLAYERS; i++) {
			waterfountainwall[i] = -1;
			waterfountaincnt[i] = 0;
			slimesoundcnt[i] = 0;
		}

		for (i = 0; i < MAXPLAYERS; i++)
			gPlayer[i].updategun = 1;

		warpsectorcnt = 0; // Make a list of warping sectors
		xpanningsectorcnt = 0; // Make a list of wall x-panning sectors
		floorpanningcnt = 0; // Make a list of slime sectors
		dragsectorcnt = 0; // Make a list of moving platforms
		swingcnt = 0; // Make a list of swinging doors
		revolvecnt = 0; // Make a list of revolving doors
		subwaytrackcnt = 0; // Make a list of subways

		// intitialize subwaysound[]s
		for (i = 0; i < 4; i++) {
			subwaysound[i] = -1;
		}

		// scan sector tags
		for (i = 0; i < numsectors; i++) {
			switch (sector[i].lotag) {
			case 4:
				if (floorpanningcnt < 64)
					floorpanninglist[floorpanningcnt++] = (short) i;
				break;
			case 5060:
				if (game.nNetMode == NetMode.Multiplayer) {
					sector[i].lotag = 0;
				}
				break;
			case 25:
				if ((singlemapmode != 0) || (generalplay != 0) || (game.nNetMode == NetMode.Multiplayer)) {
					sector[i].lotag = 0;
				}
				break;
			case 10:
				if ((generalplay == 0) && (game.nNetMode != NetMode.Multiplayer) && (warpsectorcnt < 64)) {
					warpsectorlist[warpsectorcnt++] = (short) i;
				}
				break;
			case 11:
				xpanningsectorlist[xpanningsectorcnt++] = (short) i;
				break;
			case 12:
				dasector = (short) i;
				dax = 0x7fffffff;
				day = 0x7fffffff;
				dax2 = 0x80000000;
				day2 = 0x80000000;
				startwall = sector[i].wallptr;
				endwall = (short) (startwall + sector[i].wallnum - 1);
				for (j = startwall; j <= endwall; j++) {
					if (wall[j].x < dax)
						dax = wall[j].x;
					if (wall[j].y < day)
						day = wall[j].y;
					if (wall[j].x > dax2)
						dax2 = wall[j].x;
					if (wall[j].y > day2)
						day2 = wall[j].y;
					if (wall[j].lotag == 3)
						k = j;
				}
				if (wall[k].x == dax)
					dragxdir[dragsectorcnt] = -16;
				if (wall[k].y == day)
					dragydir[dragsectorcnt] = -16;
				if (wall[k].x == dax2)
					dragxdir[dragsectorcnt] = 16;
				if (wall[k].y == day2)
					dragydir[dragsectorcnt] = 16;

				dasector = wall[startwall].nextsector;
				dragx1[dragsectorcnt] = 0x7fffffff;
				dragy1[dragsectorcnt] = 0x7fffffff;
				dragx2[dragsectorcnt] = 0x80000000;
				dragy2[dragsectorcnt] = 0x80000000;
				startwall = sector[dasector].wallptr;
				endwall = (short) (startwall + sector[dasector].wallnum - 1);
				for (j = startwall; j <= endwall; j++) {
					if (wall[j].x < dragx1[dragsectorcnt])
						dragx1[dragsectorcnt] = wall[j].x;
					if (wall[j].y < dragy1[dragsectorcnt])
						dragy1[dragsectorcnt] = wall[j].y;
					if (wall[j].x > dragx2[dragsectorcnt])
						dragx2[dragsectorcnt] = wall[j].x;
					if (wall[j].y > dragy2[dragsectorcnt])
						dragy2[dragsectorcnt] = wall[j].y;
				}

				dragx1[dragsectorcnt] += (wall[sector[i].wallptr].x - dax);
				dragy1[dragsectorcnt] += (wall[sector[i].wallptr].y - day);
				dragx2[dragsectorcnt] -= (dax2 - wall[sector[i].wallptr].x);
				dragy2[dragsectorcnt] -= (day2 - wall[sector[i].wallptr].y);

				dragfloorz[dragsectorcnt] = sector[i].floorz;

				dragsectorlist[dragsectorcnt++] = (short) i;
				break;
			case 13:
				startwall = sector[i].wallptr;
				endwall = (short) (startwall + sector[i].wallnum - 1);
				for (j = startwall; j <= endwall; j++) {
					if (wall[j].lotag == 4) {
						k = wall[wall[wall[wall[j].point2].point2].point2].point2;
						if ((wall[j].x == wall[k].x) && (wall[j].y == wall[k].y)) { // Door opens counterclockwise
							swingwall[swingcnt][0] = (short) j;
							swingwall[swingcnt][1] = wall[j].point2;
							swingwall[swingcnt][2] = wall[wall[j].point2].point2;
							swingwall[swingcnt][3] = wall[wall[wall[j].point2].point2].point2;
							swingangopen[swingcnt] = 1536;
							swingangclosed[swingcnt] = 0;
							swingangopendir[swingcnt] = -1;
						} else { // Door opens clockwise
							swingwall[swingcnt][0] = wall[j].point2;
							swingwall[swingcnt][1] = (short) j;
							swingwall[swingcnt][2] = (short) engine.lastwall(j);
							swingwall[swingcnt][3] = (short) engine.lastwall(swingwall[swingcnt][2]);
							swingwall[swingcnt][4] = (short) engine.lastwall(swingwall[swingcnt][3]);
							swingangopen[swingcnt] = 512;
							swingangclosed[swingcnt] = 0;
							swingangopendir[swingcnt] = 1;
						}
						for (k = 0; k < 4; k++) {
							swingx[swingcnt][k] = wall[swingwall[swingcnt][k]].x;
							swingy[swingcnt][k] = wall[swingwall[swingcnt][k]].y;
						}

						swingsector[swingcnt] = (short) i;
						swingang[swingcnt] = swingangclosed[swingcnt];
						swinganginc[swingcnt] = 0;
						swingcnt++;
					}
				}
				break;
			case 14:
				startwall = sector[i].wallptr;
				endwall = (short) (startwall + sector[i].wallnum - 1);
				dax = 0;
				day = 0;
				for (j = startwall; j <= endwall; j++) {
					dax += wall[j].x;
					day += wall[j].y;
				}
				revolvepivotx[revolvecnt] = dax / (endwall - startwall + 1);
				revolvepivoty[revolvecnt] = day / (endwall - startwall + 1);

				k = 0;
				for (j = startwall; j <= endwall; j++) {
					revolvex[revolvecnt][k] = wall[j].x;
					revolvey[revolvecnt][k] = wall[j].y;
					k++;
				}
				revolvesector[revolvecnt] = (short) i;
				revolveang[revolvecnt] = 0;

				revolvecnt++;
				break;
			case 15:
				subwaytracksector[subwaytrackcnt][0] = (short) i;
				subwaystopcnt[subwaytrackcnt] = 0;
				dax = 0x7fffffff;
				day = 0x7fffffff;
				dax2 = 0x80000000;
				day2 = 0x80000000;
				startwall = sector[i].wallptr;
				endwall = (short) (startwall + sector[i].wallnum - 1);
				for (j = startwall; j <= endwall; j++) {
					if (wall[j].x < dax)
						dax = wall[j].x;
					if (wall[j].y < day)
						day = wall[j].y;
					if (wall[j].x > dax2)
						dax2 = wall[j].x;
					if (wall[j].y > day2)
						day2 = wall[j].y;
				}
				for (j = startwall; j <= endwall; j++) {
					if (wall[j].lotag == 5) {
						if ((wall[j].x > dax) && (wall[j].y > day) && (wall[j].x < dax2) && (wall[j].y < day2)) {
							subwayx[subwaytrackcnt] = wall[j].x;
						} else {
							subwaystop[subwaytrackcnt][subwaystopcnt[subwaytrackcnt]] = wall[j].x;
							if (accessiblemap(wall[j].hitag) == 0) {
								subwaystop[subwaytrackcnt][subwaystopcnt[subwaytrackcnt]] = 0;
							}
							subwaystopcnt[subwaytrackcnt]++;
						}
					}
				}
				// de-sparse stoplist but keep increasing x order
				for (j = 0; j < subwaystopcnt[subwaytrackcnt]; j++) {
					if (subwaystop[subwaytrackcnt][j] == 0) {
						for (l = j + 1; l < subwaystopcnt[subwaytrackcnt]; l++) {
							if (subwaystop[subwaytrackcnt][l] != 0) {
								subwaystop[subwaytrackcnt][j] = subwaystop[subwaytrackcnt][l];
								subwaystop[subwaytrackcnt][l] = 0;
								l = subwaystopcnt[subwaytrackcnt];
							}
						}
					}
				}
				// recount stopcnt
				subwaystopcnt[subwaytrackcnt] = 0;
				while (subwaystop[subwaytrackcnt][subwaystopcnt[subwaytrackcnt]] != 0) {
					subwaystopcnt[subwaytrackcnt]++;
				}

				for (j = 1; j < subwaystopcnt[subwaytrackcnt]; j++)
					for (k = 0; k < j; k++)
						if (subwaystop[subwaytrackcnt][j] < subwaystop[subwaytrackcnt][k]) {
							s = subwaystop[subwaytrackcnt][j];
							subwaystop[subwaytrackcnt][j] = subwaystop[subwaytrackcnt][k];
							subwaystop[subwaytrackcnt][k] = s;
						}

				subwaygoalstop[subwaytrackcnt] = 0;
				for (j = 0; j < subwaystopcnt[subwaytrackcnt]; j++)
					if (Math.abs(subwaystop[subwaytrackcnt][j] - subwayx[subwaytrackcnt]) < Math
							.abs(subwaystop[subwaytrackcnt][subwaygoalstop[subwaytrackcnt]] - subwayx[subwaytrackcnt]))
						subwaygoalstop[subwaytrackcnt] = j;

				subwaytrackx1[subwaytrackcnt] = dax;
				subwaytracky1[subwaytrackcnt] = day;
				subwaytrackx2[subwaytrackcnt] = dax2;
				subwaytracky2[subwaytrackcnt] = day2;

				subwaynumsectors[subwaytrackcnt] = 1;
				for (j = 0; j < numsectors; j++)
					if (j != i) {
						startwall = sector[j].wallptr;
						if (wall[startwall].x > subwaytrackx1[subwaytrackcnt])
							if (wall[startwall].y > subwaytracky1[subwaytrackcnt])
								if (wall[startwall].x < subwaytrackx2[subwaytrackcnt])
									if (wall[startwall].y < subwaytracky2[subwaytrackcnt]) {
										if (sector[j].lotag == 16)
											sector[j].lotag = 17; // Make special subway door

										if (sector[j].floorz != sector[i].floorz) {
											sector[j].ceilingstat |= 64;
											sector[j].floorstat |= 64;
										}
										subwaytracksector[subwaytrackcnt][subwaynumsectors[subwaytrackcnt]] = (short) j;
										subwaynumsectors[subwaytrackcnt]++;
									}
					}

				subwayvel[subwaytrackcnt] = 32; // orig 64
				subwaypausetime[subwaytrackcnt] = 720;
				subwaytrackcnt++;
				break;
			}
		}

		// scan wall tags
		ypanningwallcnt = 0;
		for (i = 0; i < numwalls; i++) {
			if (wall[i].lotag == 1)
				ypanningwalllist[ypanningwallcnt++] = (short) i;
		}

		// scan sprite tags&picnum's
		rotatespritecnt = 0;
		startspotcnt = 0;
		for (i = 0; i < MAXSPRITES; i++) {
			if (sprite[i].picnum == STARTPOS) {
				if (startspotcnt < MAXSTARTSPOTS) {
					if (startspot[startspotcnt] == null)
						startspot[startspotcnt] = new Startspottype();
					startspot[startspotcnt].x = sprite[i].x;
					startspot[startspotcnt].y = sprite[i].y;
					startspot[startspotcnt].z = sprite[i].z;
					startspot[startspotcnt].sectnum = sprite[i].sectnum;
					startspotcnt++;
				}
				jsdeletesprite((short) i);
			} else if (sprite[i].lotag == 3) {
				rotatespritelist[rotatespritecnt++] = (short) i;
			} else if (game.nNetMode == NetMode.Multiplayer) {
				if (sprite[i].lotag == 1009) {
					jsdeletesprite((short) i);
				}
			}
		}
		if ((startspotcnt == 0) && (game.nNetMode == NetMode.Multiplayer)) {
			System.err.println("no net startspots");
		}

		for (i = 0; i < (MAXSECTORS >> 3); i++)
			show2dsector[i] = (byte) 0xff;
		for (i = 0; i < (MAXWALLS >> 3); i++)
			show2dwall[i] = (byte) 0xff;
		automapping = 0;
		// tags that make wall/sector not show up on 2D map
		for (i = 0; i < MAXSECTORS; i++) {
			if (sector[i] != null && sector[i].lotag == 9901) {
				show2dsector[i >> 3] &= ~(1 << (i & 7));
				startwall = sector[i].wallptr;
				endwall = (short) (startwall + sector[i].wallnum - 1);
				for (j = startwall; j <= endwall; j++) {
					show2dwall[j >> 3] &= ~(1 << (j & 7));
					if (wall[j].nextwall != -1)
						show2dwall[(wall[j].nextwall) >> 3] &= ~(1 << ((wall[j].nextwall) & 7));
				}
			}
		}
		for (i = 0; i < MAXWALLS; i++) {
			if (wall[i] != null && wall[i].lotag == 9900) {
				show2dwall[i >> 3] &= ~(1 << (i & 7));
			}
		}

		if (firsttimethru != 0) {
			lockclock = 0;
//		     ototalclock = 0;
//		     gotlastpacketclock = 0;
//		     masterslavetexttime = 0;
		}

		if (game.nNetMode == NetMode.Multiplayer) {
			firsttimethru = 0;
		}

		tekpreptags();
		initspriteXTs();

		if (currentmapno == 0) {
			SECTOR sec = sector[333];
			if (sec != null && sec.lotag == 5020 && sec.hitag == 901) {
				sec.lotag = sec.hitag = 0;
			}
		}

		// put guns somewhere on map
//	     if( option[4] > 0 ) {
//	          placerandompic(3094L);
//	          placerandompic(3093L);
//	          placerandompic(3095L);
//	          placerandompic(3091L);
//	          placerandompic(3090L);
//	     }

		return true;
	}

}
