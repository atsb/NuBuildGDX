package ru.m210projects.Tekwar;

import static ru.m210projects.Build.Net.Mmulti.myconnectindex;
import static ru.m210projects.Build.Engine.sprite;
import static ru.m210projects.Build.Engine.xdim;
import static ru.m210projects.Build.Engine.ydim;
import static ru.m210projects.Tekwar.Main.*;
import static ru.m210projects.Tekwar.Globals.*;
import static ru.m210projects.Tekwar.Names.ALPHABET2;
import static ru.m210projects.Tekwar.Names.ANTDEATHPIC;
import static ru.m210projects.Tekwar.Names.DIDEATHPIC;
import static ru.m210projects.Tekwar.Names.REDHDEATHPIC;
import static ru.m210projects.Tekwar.Names.SFRODEATHPIC;
import static ru.m210projects.Tekwar.Names.SGOLDEATHPIC;
import static ru.m210projects.Tekwar.Names.SSALDEATHPIC;
import static ru.m210projects.Tekwar.Names.SUNGDEATHPIC;
import static ru.m210projects.Tekwar.Names.WINGDEATHPIC;
import static ru.m210projects.Tekwar.Player.*;
import static ru.m210projects.Tekwar.Tekchng.stun;
import static ru.m210projects.Tekwar.Tekgun.CLASS_CIVILLIAN;
import static ru.m210projects.Tekwar.Tekgun.CLASS_TEKBOSS;
import static ru.m210projects.Tekwar.Tekgun.CLASS_TEKLORD;
import static ru.m210projects.Tekwar.Tekprep.spriteXT;
import static ru.m210projects.Tekwar.Teksnd.menusong;
import static ru.m210projects.Tekwar.Teksnd.startmusic;
import static ru.m210projects.Tekwar.Teksnd.stopallsounds;
import static ru.m210projects.Tekwar.Tekstat.validext;
import static ru.m210projects.Tekwar.View.*;

public class Tekmap {

	public static final boolean MUSICINMATRIX = true;

	public static final int MAXSYMBOLS = 7;
	public static boolean[] symbols = new boolean[MAXSYMBOLS];
	public static boolean[] symbolsdeposited = new boolean[MAXSYMBOLS];

	public static int allsymsdeposited = 0;
	public static char generalplay = 0;
	public static char singlemapmode;
	public static int killedsonny = 0;
	public static int civillianskilled = 0;
	public static char mission_accomplished = 0;
	public static char gameover = 0;
	public static char numlives = 0;
	public static int mission = 0;
	public static int warpretx, warprety, warpretz;
	public static short warpretang, warpretsect;

	public static final int TOTALMISSIONS = 7;
	public static final int MAXMAPSPERMISSION = 6;
	static int missionset[][] = {
			// SUB M1 M2 M3 M4 MTRX
			{ 0, 4, 5, -1, 10, -1 }, // mission 0
			{ 0, 8, -1, -1, -1, -1 }, // mission 1
			{ 1, 11, 12, -1, -1, -1 }, // mission 2
			{ 1, 15, 16, 17, -1, -1 }, // mission 3
			{ 2, 18, 19, 20, -1, -1 }, // mission 4
			{ 2, 22, 23, -1, -1, -1 }, // mission 5
			{ 3, 25, 26, 27, -1, -1 }, // mission 6
	};

	public static int currentmapno = 0;

	public static final int TOTALMAPS = 32;
	public static final String mapnames[] = { "subway0.map", // 0
			"subway1.map", // 1
			"subway2.map", // 2
			"subway3.map", // 3
			"level1.map", // 4
			"level2.map", // 5
			"", // 6
			"", // 7
			"city1.map", // 8
			"", // 9
			"city,map", // 10
			"beach1.map", // 11
			"park1.map", // 12
			"", // 13
			"", // 14
			"mid1.map", // 15
			"mid2.map", // 16
			"mid3.map", // 17
			"sewer1.map", // 18
			"sewer2.map", // 19
			"inds1.map", // 20
			"", // 21
			"free1.map", // 22
			"free2.map", // 23
			"", // 24
			"ware1.map", // 25
			"ware2.map", // 26
			"ware3.map", // 27
			"", // 28
			"", // 29
			"final1.map", // 30
			"" // 31
	};

	public static int mapcoords(int cmap) {
		/*
		 * "level1.map", // 4 "level2.map", // 5 "city1.map", // 8
		 * 
		 * "beach1.map", // 11 "park1.map", // 12 "mid1.map", // 15 "mid2.map", // 16
		 * "mid3.map", // 17
		 * 
		 * "sewer1.map", // 18 "sewer2.map", // 19 "inds1.map", // 20 "free1.map", // 22
		 * "free2.map", // 23
		 * 
		 * "ware1.map", // 25 "ware2.map", // 26 "ware3.map", // 27
		 */
		int rv;

		switch (cmap) {
		case 4:
			rv = 0;
			warpretx = -27200;
			warprety = 21500;
			warpretang = 512;
			warpretsect = 351;
			break;
		case 5:
			rv = 0;
			warpretx = 7295;
			warprety = 21500;
			warpretang = 512;
			warpretsect = 349;
			break;
		case 8:
			rv = 0;
			warpretx = -11902;
			warprety = 39300;
			warpretang = 1536;
			warpretsect = 353;
			break;
		case 11:
			rv = 1;
			warpretx = -27200;
			warprety = 21500;
			warpretang = 512;
			warpretsect = 489;
			break;
		case 12:
			rv = 1;
			warpretx = 7295;
			warprety = 21500;
			warpretang = 512;
			warpretsect = 492;
			break;
		case 15:
			rv = 1;
			warpretx = -11904;
			warprety = 39300;
			warpretang = 1536;
			warpretsect = 488;
			break;
		case 16:
			rv = 1;
			warpretx = 24322;
			warprety = 39300;
			warpretang = 1536;
			warpretsect = 486;
			break;
		case 17:
			rv = 1;
			warpretx = 57346;
			warprety = 39300;
			warpretang = 1536;
			warpretsect = 483;
			break;
		case 18:
			rv = 2;
			warpretx = -27200;
			warprety = 21500;
			warpretang = 512;
			warpretsect = 486;
			break;
		case 19:
			rv = 2;
			warpretx = 7295;
			warprety = 21500;
			warpretang = 512;
			warpretsect = 488;
			break;
		case 20:
			rv = 2;
			warpretx = 41600;
			warprety = 21500;
			warpretang = 512;
			warpretsect = 490;
			break;
		case 22:
			rv = 2;
			warpretx = -11904;
			warprety = 39300;
			warpretang = 1536;
			warpretsect = 483;
			break;
		case 23:
			rv = 2;
			warpretx = 24380;
			warprety = 39300;
			warpretang = 1536;
			warpretsect = 482;
			break;
		case 25:
			rv = 3;
			warpretx = -27200;
			warprety = 21500;
			warpretang = 512;
			warpretsect = 477;
			break;
		case 26:
			rv = 3;
			warpretx = 7295;
			warprety = 21500;
			warpretang = 512;
			warpretsect = 472;
			break;
		case 27:
			rv = 3;
			warpretx = 41600;
			warprety = 21500;
			warpretang = 512;
			warpretsect = 494;
			break;
		default:
			rv = -1;
			break;
		}

		return (rv);
	}

	public static void changemap(final int mapno) {
		final int p = myconnectindex;
		final int savhealth;
		final int savestun;
		final int savereds;
		final int saveblues;
		final int saveaccutrk;
		final int newmap;

		if (game.nNetMode == NetMode.Multiplayer)
			return;

		if (mUserFlag == UserFlag.UserMap) {
			gameover = 1;
//			fadein();
			return;
		}

		if (mapno >= TOTALMAPS) {
			return;
		}

		switch (mapno) {
		case 0:
		case 1:
		case 2:
		case 3:
			newmap = mapcoords(currentmapno);
			if (newmap != mapno) {
				game.ThrowError("bad return map");
			}
			break;
		}

		printext((xdim >> 1) - 44, (ydim >> 1) - 32, "LOADING MAP \0".toCharArray(), ALPHABET2, 255);
		engine.nextpage();

		resetEffects();

		savhealth = gPlayer[p].health;
		savestun = stun[p];
		savereds = gPlayer[p].invredcards;
		saveblues = gPlayer[p].invbluecards;
		saveaccutrk = gPlayer[p].invaccutrak;
		
		final short[] saveammo = new short[8];
		if(tekcfg.gSaveWeapons) 
			System.arraycopy(gPlayer[p].ammo, 0, saveammo, 0, 8);
		final int saveweap = gPlayer[p].weapons;
		final int savefireseq = gPlayer[p].fireseq;

		newgame(mapnames[mapno], new Runnable() {
			@Override
			public void run() {
				gPlayer[p].health = savhealth;
				stun[p] = savestun;
				gPlayer[p].invredcards = savereds;
				gPlayer[p].invbluecards = saveblues;
				gPlayer[p].invaccutrak = saveaccutrk;

				if(tekcfg.gSaveWeapons) {
					System.arraycopy(saveammo, 0, gPlayer[p].ammo, 0, 8);
					gPlayer[p].weapons = saveweap;
					gPlayer[p].fireseq = savefireseq;
				}

				switch (mapno) {
				case 0:
				case 1:
				case 2:
				case 3:
					short sn = gPlayer[p].playersprite;

					sprite[sn].x = (int) warpretx;
					sprite[sn].y = (int) warprety;
					sprite[sn].ang = (short) warpretang;
					game.pInt.setsprinterpolate(sn, sprite[sn]);
					engine.changespritesect(sn, warpretsect);

					gPlayer[p].posx = sprite[sn].x;
					gPlayer[p].posy = sprite[sn].y;
					gPlayer[p].ang = sprite[sn].ang;
					gPlayer[p].cursectnum = sprite[sn].sectnum;
					break;
				default:
					break;
				}

				game.pNet.gInput.Reset();

				currentmapno = mapno;

				showmessage(mapnames[mapno]);

				if (mapno <= 3) {
					menusong(1);
				} else {
					startmusic(mission);
				}
			}
		});
	}

	public static int missionfailed() {
		if (game.nNetMode == NetMode.Multiplayer)
			return (0);

		numlives++;

		switch (gDifficulty) {
		case 0:
		case 1:
			if (numlives < 6)
				return (0);
			break;
		case 2:
			if (numlives < 4)
				return (0);
			break;
		default:
			if (numlives < 2)
				return (0);
			break;
		}

		mission_accomplished = 0;
		gameover = 1;
//		fadein();
		return (1);
	}

	public static void missionaccomplished(int sn) {
		if (game.nNetMode == NetMode.Multiplayer)
			return;

		int ext = sprite[sn].extra;
		if (validext(ext) == 0) {
			return;
		}

		if (spriteXT[ext].sclass == CLASS_CIVILLIAN) {
			civillianskilled++;
		}

		if (TEKDEMO && spriteXT[ext].sclass == CLASS_TEKBOSS) {
			if (spriteXT[ext].deathpic == DIDEATHPIC) {
				mission_accomplished = 1;
				gameover = 1;
//				fadein();
			}
		}

		if (spriteXT[ext].sclass != CLASS_TEKLORD) {
			return;
		}

		if (mUserFlag == UserFlag.UserMap)
			gameover = 1;

//		fadein();
		if (mUserFlag == UserFlag.None) {
			switch (spriteXT[ext].deathpic) {
			case WINGDEATHPIC:
				symbols[0] = true;
				mission_accomplished = 1;
				gameover = 1;
				break;
			case DIDEATHPIC:
				symbols[1] = true;
				mission_accomplished = 1;
				gameover = 1;
				break;
			case SFRODEATHPIC:
				symbols[2] = true;
				mission_accomplished = 1;
				gameover = 1;
				break;
			case ANTDEATHPIC:
				symbols[3] = true;
				mission_accomplished = 1;
				gameover = 1;
				break;
			case SGOLDEATHPIC:
				symbols[4] = true;
				mission_accomplished = 1;
				gameover = 1;
				break;
			case SUNGDEATHPIC:
				symbols[5] = true;
				mission_accomplished = 1;
				gameover = 1;
				break;
			case REDHDEATHPIC:
				symbols[6] = true;
				mission_accomplished = 1;
				gameover = 1;
				break;
			case SSALDEATHPIC:
				killedsonny = 1;
				mission_accomplished = 1;
				gameover = 1;
				break;
			}
		}
	}

	public static void newgame(String mapname, Runnable callback) {
		if (game.nNetMode == NetMode.Multiplayer)
			return;

		stopallsounds();

		if (boardfilename == null || boardfilename.compareToIgnoreCase(mapname) != 0)
			boardfilename = mapname;
		resetEffects();

		gGameScreen.loadboard(mapname, callback);
	}

	public static void donewgame() {
		gameover = 0;
		numlives = 0;
		civillianskilled = 0;
		mission_accomplished = 0;
		currentmapno = 0;

		switch (mission) {
		case 0:
		case 1:
			newgame("subway0.map", null);
			break;
		case 2:
		case 3:
			newgame("subway1.map", null);
			break;
		case 4:
		case 5:
			newgame("subway2.map", null);
			break;
		case 6:
			newgame("subway3.map", null);
			break;
		case 7:
			newgame("matrix.map", null);
			break;
		case 8:
			newgame("load.map", null);
			break;
		case 9:
			newgame("final1.map", null);
			break;
		case 10: // DEMO VERSION
			newgame("city.map", null);
			break;
		}

		engine.clearview(0);
		resetEffects();

		if (mission == 7) {
			if (MUSICINMATRIX) {
				startmusic((int) (7 * Math.random()));
			}
		} else
			menusong(1);

		// if matrix, reset time
		if (mission == 7) {
			seconds = minutes = hours = 0;
		}
	}

	public static void donewmap() {
		gameover = 0;
		numlives = 0;
		civillianskilled = 0;
		mission_accomplished = 0;
		switch (mission) {
		case 0:
			newgame("level1.map", null);
			break;
		case 1:
			newgame("level2.map", null);
			break;
		case 2:
			newgame("city1.map", null);
			break;
		case 3:
			newgame("beach1.map", null);
			break;

		case 5:
			newgame("park1.map", null);
			break;
		case 6:
			newgame("mid1.map", null);
			break;
		case 7:
			newgame("mid2.map", null);
			break;
		case 8:
			newgame("mid3.map", null);
			break;

		case 9:
			newgame("sewer1.map", null);
			break;
		case 10:
			newgame("sewer2.map", null);
			break;
		case 11:
			newgame("inds1.map", null);
			break;
		case 12:
			newgame("free1.map", null);
			break;

		case 14:
			newgame("free2.map", null);
			break;
		case 15:
			newgame("ware1.map", null);
			break;
		case 16:
			newgame("ware2.map", null);
			break;
		case 17:
			newgame("ware3.map", null);
			break;

		case 18:
			newgame("load.map", null);
			break;

		default:
			game.ThrowError("chsmp: bad map num");
		}
	}

	public static int accessiblemap(int mn) {
		if ((mn < 0) || (mn >= TOTALMAPS)) {
			return (0);
		}
		if (mapnames[mn].length() < 2) {
			return (0);
		}
		for (int i = 0; i < MAXMAPSPERMISSION; i++) {
			if (mission < 7 && missionset[mission][i] == mn) {
				return (1);
			}
		}
		return (0);
	}

	public static String debriefing() {
		String name = null;
		
		if(mUserFlag == UserFlag.UserMap || gameover == 2)
			return null;
		
		if (mission_accomplished != 0) {
			if (civillianskilled == 0) {
				switch (mission) {
				case 2:
					name = "ROSSI2.SMK";
					break;
				case 1:
					name = "DIMARCO2.SMK";
					break;
				case 5:
					name = "CONNOR2.SMK";
					break;
				case 4:
					name = "SONNY2.SMK";
					break;
				case 6:
					name = "JANUS2.SMK";
					break;
				case 3:
					name = "LOWELL2.SMK";
					break;
				case 0:
					name = "DOLLAR2.SMK";
					break;
				}
			} else {
				switch (mission) {
				case 2:
					name = "ROSSI3.SMK";
					break;
				case 1:
					name = "DIMARCO3.SMK";
					break;
				case 5:
					name = "CONNOR3.SMK";
					break;
				case 4:
					name = "SONNY3.SMK";
					break;
				case 6:
					name = "JANUS3.SMK";
					break;
				case 3:
					name = "LOWELL3.SMK";
					break;
				case 0:
					name = "DOLLAR3.SMK";
					break;
				}
			}
		} else {
			if (civillianskilled == 0) {
				switch (mission) {
				case 2:
					name = "ROSSI4.SMK";
					break;
				case 1:
					name = "DIMARCO4.SMK";
					break;
				case 5:
					name = "CONNOR4.SMK";
					break;
				case 4:
					name = "SONNY4.SMK";
					break;
				case 6:
					name = "JANUS4.SMK";
					break;
				case 3:
					name = "LOWELL4.SMK";
					break;
				case 0:
					name = "DOLLAR4.SMK";
					break;
				}
			} else {
				switch (mission) {
				case 2:
					name = "ROSSI5.SMK";
					break;
				case 1:
					name = "DIMARCO5.SMK";
					break;
				case 5:
					name = "CONNOR5.SMK";
					break;
				case 4:
					name = "SONNY5.SMK";
					break;
				case 6:
					name = "JANUS5.SMK";
					break;
				case 3:
					name = "LOWELL5.SMK";
					break;
				case 0:
					name = "DOLLAR5.SMK";
					break;
				}
			}
		}
		return name;
	}
}
