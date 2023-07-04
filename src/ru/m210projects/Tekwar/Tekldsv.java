package ru.m210projects.Tekwar;

import static ru.m210projects.Build.Engine.MAXPLAYERS;
import static ru.m210projects.Build.Engine.MAXPSKYTILES;
import static ru.m210projects.Build.Engine.MAXSECTORS;
import static ru.m210projects.Build.Engine.MAXSPRITES;
import static ru.m210projects.Build.Engine.MAXSTATUS;
import static ru.m210projects.Build.Engine.MAXTILES;
import static ru.m210projects.Build.Engine.MAXWALLSV7;
import static ru.m210projects.Build.Engine.automapping;
import static ru.m210projects.Build.Engine.headspritesect;
import static ru.m210projects.Build.Engine.headspritestat;
import static ru.m210projects.Build.Engine.nextspritesect;
import static ru.m210projects.Build.Engine.nextspritestat;
import static ru.m210projects.Build.Engine.numsectors;
import static ru.m210projects.Build.Engine.numwalls;
import static ru.m210projects.Build.Engine.parallaxtype;
import static ru.m210projects.Build.Engine.parallaxvisibility;
import static ru.m210projects.Build.Engine.parallaxyoffs;
import static ru.m210projects.Build.Engine.prevspritesect;
import static ru.m210projects.Build.Engine.prevspritestat;
import static ru.m210projects.Build.Engine.pskybits;
import static ru.m210projects.Build.Engine.pskyoff;
import static ru.m210projects.Build.Engine.sector;
import static ru.m210projects.Build.Engine.show2dsector;
import static ru.m210projects.Build.Engine.show2dsprite;
import static ru.m210projects.Build.Engine.show2dwall;
import static ru.m210projects.Build.Engine.sprite;
import static ru.m210projects.Build.Engine.totalclock;
import static ru.m210projects.Build.Engine.visibility;
import static ru.m210projects.Build.Engine.wall;
import static ru.m210projects.Build.Net.Mmulti.connecthead;
import static ru.m210projects.Build.Net.Mmulti.connectpoint2;
import static ru.m210projects.Build.Net.Mmulti.myconnectindex;
import static ru.m210projects.Build.Net.Mmulti.numplayers;
import static ru.m210projects.Build.OnSceenDisplay.Console.OSDTEXT_BLUE;
import static ru.m210projects.Tekwar.Animate.MAXANIMATES;
import static ru.m210projects.Tekwar.Animate.gAnimationCount;
import static ru.m210projects.Tekwar.Animate.gAnimationData;
import static ru.m210projects.Tekwar.Animate.getobject;
import static ru.m210projects.Tekwar.Globals.ST_UNIQUE;
import static ru.m210projects.Tekwar.Globals.gDifficulty;
import static ru.m210projects.Tekwar.Main.boardfilename;
import static ru.m210projects.Tekwar.Main.engine;
import static ru.m210projects.Tekwar.Main.gGameScreen;
import static ru.m210projects.Tekwar.Main.gLoadingScreen;
import static ru.m210projects.Tekwar.Main.gPrecacheScreen;
import static ru.m210projects.Tekwar.Main.game;
import static ru.m210projects.Tekwar.Main.hours;
import static ru.m210projects.Tekwar.Main.lockclock;
import static ru.m210projects.Tekwar.Main.mUserFlag;
import static ru.m210projects.Tekwar.Main.minutes;
import static ru.m210projects.Tekwar.Main.screenpeek;
import static ru.m210projects.Tekwar.Main.seconds;
import static ru.m210projects.Tekwar.Player.gPlayer;
import static ru.m210projects.Tekwar.Tekgun.goreflag;
import static ru.m210projects.Tekwar.Tekmap.MAXSYMBOLS;
import static ru.m210projects.Tekwar.Tekmap.allsymsdeposited;
import static ru.m210projects.Tekwar.Tekmap.civillianskilled;
import static ru.m210projects.Tekwar.Tekmap.currentmapno;
import static ru.m210projects.Tekwar.Tekmap.generalplay;
import static ru.m210projects.Tekwar.Tekmap.killedsonny;
import static ru.m210projects.Tekwar.Tekmap.mission;
import static ru.m210projects.Tekwar.Tekmap.mission_accomplished;
import static ru.m210projects.Tekwar.Tekmap.numlives;
import static ru.m210projects.Tekwar.Tekmap.singlemapmode;
import static ru.m210projects.Tekwar.Tekmap.symbols;
import static ru.m210projects.Tekwar.Tekmap.symbolsdeposited;
import static ru.m210projects.Tekwar.Tekprep.initplayersprite;
import static ru.m210projects.Tekwar.Tekprep.spriteXT;
import static ru.m210projects.Tekwar.Tekprep.subwaysound;
import static ru.m210projects.Tekwar.Teksnd.menusong;
import static ru.m210projects.Tekwar.Teksnd.playsound;
import static ru.m210projects.Tekwar.Teksnd.startmusic;
import static ru.m210projects.Tekwar.Tekstat.sectflash;
import static ru.m210projects.Tekwar.Tektag.MAP_SFX_TOGGLED;
import static ru.m210projects.Tekwar.Tektag.animpic;
import static ru.m210projects.Tekwar.Tektag.clearvehiclesoundindexes;
import static ru.m210projects.Tekwar.Tektag.delayfunc;
import static ru.m210projects.Tekwar.Tektag.doortype;
import static ru.m210projects.Tekwar.Tektag.doorxref;
import static ru.m210projects.Tekwar.Tektag.dragfloorz;
import static ru.m210projects.Tekwar.Tektag.dragsectorcnt;
import static ru.m210projects.Tekwar.Tektag.dragsectorlist;
import static ru.m210projects.Tekwar.Tektag.dragx1;
import static ru.m210projects.Tekwar.Tektag.dragx2;
import static ru.m210projects.Tekwar.Tektag.dragxdir;
import static ru.m210projects.Tekwar.Tektag.dragy1;
import static ru.m210projects.Tekwar.Tektag.dragy2;
import static ru.m210projects.Tekwar.Tektag.dragydir;
import static ru.m210projects.Tekwar.Tektag.elevator;
import static ru.m210projects.Tekwar.Tektag.fdxref;
import static ru.m210projects.Tekwar.Tektag.floordoor;
import static ru.m210projects.Tekwar.Tektag.floorpanningcnt;
import static ru.m210projects.Tekwar.Tektag.floorpanninglist;
import static ru.m210projects.Tekwar.Tektag.mapsndfx;
import static ru.m210projects.Tekwar.Tektag.numanimates;
import static ru.m210projects.Tekwar.Tektag.numdelayfuncs;
import static ru.m210projects.Tekwar.Tektag.numdoors;
import static ru.m210projects.Tekwar.Tektag.numfloordoors;
import static ru.m210projects.Tekwar.Tektag.numvehicles;
import static ru.m210projects.Tekwar.Tektag.onelev;
import static ru.m210projects.Tekwar.Tektag.revolveang;
import static ru.m210projects.Tekwar.Tektag.revolvecnt;
import static ru.m210projects.Tekwar.Tektag.revolvepivotx;
import static ru.m210projects.Tekwar.Tektag.revolvepivoty;
import static ru.m210projects.Tekwar.Tektag.revolvesector;
import static ru.m210projects.Tekwar.Tektag.revolvex;
import static ru.m210projects.Tekwar.Tektag.revolvey;
import static ru.m210projects.Tekwar.Tektag.rotatespritecnt;
import static ru.m210projects.Tekwar.Tektag.rotatespritelist;
import static ru.m210projects.Tekwar.Tektag.secnt;
import static ru.m210projects.Tekwar.Tektag.sectoreffect;
import static ru.m210projects.Tekwar.Tektag.sectorvehicle;
import static ru.m210projects.Tekwar.Tektag.sexref;
import static ru.m210projects.Tekwar.Tektag.slimesoundcnt;
import static ru.m210projects.Tekwar.Tektag.sprelevcnt;
import static ru.m210projects.Tekwar.Tektag.spriteelev;
import static ru.m210projects.Tekwar.Tektag.subwaygoalstop;
import static ru.m210projects.Tekwar.Tektag.subwaynumsectors;
import static ru.m210projects.Tekwar.Tektag.subwaypausetime;
import static ru.m210projects.Tekwar.Tektag.subwaystop;
import static ru.m210projects.Tekwar.Tektag.subwaystopcnt;
import static ru.m210projects.Tekwar.Tektag.subwaytrackcnt;
import static ru.m210projects.Tekwar.Tektag.subwaytracksector;
import static ru.m210projects.Tekwar.Tektag.subwaytrackx1;
import static ru.m210projects.Tekwar.Tektag.subwaytrackx2;
import static ru.m210projects.Tekwar.Tektag.subwaytracky1;
import static ru.m210projects.Tekwar.Tektag.subwaytracky2;
import static ru.m210projects.Tekwar.Tektag.subwayvel;
import static ru.m210projects.Tekwar.Tektag.subwayx;
import static ru.m210projects.Tekwar.Tektag.swingang;
import static ru.m210projects.Tekwar.Tektag.swingangclosed;
import static ru.m210projects.Tekwar.Tektag.swinganginc;
import static ru.m210projects.Tekwar.Tektag.swingangopen;
import static ru.m210projects.Tekwar.Tektag.swingangopendir;
import static ru.m210projects.Tekwar.Tektag.swingcnt;
import static ru.m210projects.Tekwar.Tektag.swingsector;
import static ru.m210projects.Tekwar.Tektag.swingwall;
import static ru.m210projects.Tekwar.Tektag.swingx;
import static ru.m210projects.Tekwar.Tektag.swingy;
import static ru.m210projects.Tekwar.Tektag.totalmapsndfx;
import static ru.m210projects.Tekwar.Tektag.warpsectorcnt;
import static ru.m210projects.Tekwar.Tektag.warpsectorlist;
import static ru.m210projects.Tekwar.Tektag.waterfountaincnt;
import static ru.m210projects.Tekwar.Tektag.waterfountainwall;
import static ru.m210projects.Tekwar.Tektag.xpanningsectorcnt;
import static ru.m210projects.Tekwar.Tektag.xpanningsectorlist;
import static ru.m210projects.Tekwar.Tektag.ypanningwallcnt;
import static ru.m210projects.Tekwar.Tektag.ypanningwalllist;
import static ru.m210projects.Tekwar.Types.ANIMATION.CEILZ;
import static ru.m210projects.Tekwar.Types.ANIMATION.FLOORZ;
import static ru.m210projects.Tekwar.Types.ANIMATION.WALLX;
import static ru.m210projects.Tekwar.Types.ANIMATION.WALLY;
import static ru.m210projects.Tekwar.View.gViewMode;
import static ru.m210projects.Tekwar.View.kView3D;
import static ru.m210projects.Tekwar.View.showmessage;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Iterator;

import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.FileHandle.Compat.Path;
import ru.m210projects.Build.FileHandle.FileEntry;
import ru.m210projects.Build.FileHandle.FileResource;
import ru.m210projects.Build.FileHandle.FileResource.Mode;
import ru.m210projects.Build.FileHandle.Resource;
import ru.m210projects.Build.FileHandle.Resource.Whence;
import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Build.Pattern.BuildGame.NetMode;
import ru.m210projects.Build.Pattern.Tools.SaveManager;
import ru.m210projects.Build.Types.LittleEndian;
import ru.m210projects.Build.Types.SECTOR;
import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Build.Types.Tile;
import ru.m210projects.Build.Types.WALL;
import ru.m210projects.Tekwar.Main.UserFlag;
import ru.m210projects.Tekwar.Types.ANIMATION;
import ru.m210projects.Tekwar.Types.Animpic;
import ru.m210projects.Tekwar.Types.Delayfunc;
import ru.m210projects.Tekwar.Types.Doortype;
import ru.m210projects.Tekwar.Types.Elevatortype;
import ru.m210projects.Tekwar.Types.Floordoor;
import ru.m210projects.Tekwar.Types.Input;
import ru.m210projects.Tekwar.Types.LSInfo;
import ru.m210projects.Tekwar.Types.Mapsndfxtype;
import ru.m210projects.Tekwar.Types.SafeLoader;
import ru.m210projects.Tekwar.Types.Sectoreffect;
import ru.m210projects.Tekwar.Types.Sectorvehicle;
import ru.m210projects.Tekwar.Types.SpriteXT;
import ru.m210projects.Tekwar.Types.Spriteelev;

public class Tekldsv {

	public static final int kLoadShot = MAXTILES - 2;

	public static boolean gQuickSaving;
	public static boolean gAutosaveRequest;

	public static LSInfo lsInf = new LSInfo();

	public static final String savsign = "TKWR";
	public static final int gdxSave = 100;
	public static final int currentGdxSave = 102;

	public static final int SAVEVERSION = savsign.length() + 2; // version (2 bytes)
	public static final int SAVETIME = 8;
	public static final int SAVENAME = 32;
	public static final int SAVELEVELINFO = 8; // mapon + difficulty
	public static final int SAVEHEADER = SAVEVERSION + SAVETIME + SAVENAME + SAVELEVELINFO;

	public static final int SAVESCREENSHOTSIZE = 160 * 100;
	public static final int SAVEGDXDATA = SAVESCREENSHOTSIZE + 144 /* boardfilename */ + 128 /* gdx data */;

	public static String lastload;
	public static int quickslot = 0;
	public static SafeLoader loader = new SafeLoader();

	public static void FindSaves() {
		Resource fil = null;
		for (Iterator<FileEntry> it = BuildGdx.compat.getDirectory(Path.User).getFiles().values().iterator(); it
				.hasNext();) {
			FileEntry file = it.next();

			if (file.getExtension().equals("sav")) {
				fil = BuildGdx.compat.open(file);
				if (fil != null) {
					String signature = fil.readString(4);
					if (signature == null || signature.isEmpty()) {
						fil.close();
						continue;
					}
					if (signature.equals(savsign)) {
						int nVersion = fil.readShort();
						if (nVersion >= gdxSave) {
							long time = fil.readLong();
							String savname = fil.readString(SAVENAME).trim();
							game.pSavemgr.add(savname, time, file.getName());
						}
					}
					fil.close();
				}
			}
		}
		game.pSavemgr.sort();
	}

	public static int lsReadLoadData(String filename) {
		FileResource file = BuildGdx.compat.open(filename, Path.User, Mode.Read);
		if (file != null) {
			Tile pic = engine.getTile(SaveManager.Screenshot);

			if (pic.data == null)
				engine.allocatepermanenttile(SaveManager.Screenshot, 160, 100);

			int nVersion = checkSave(file) & 0xFFFF;
			lsInf.clear();

			if (nVersion == currentGdxSave) {
				file.seek(SAVEVERSION, Whence.Set);
				lsInf.date = game.date.getDate(file.readLong());
				file.seek(SAVEVERSION + SAVETIME + SAVENAME, Whence.Set); // to SAVELEVELINFO

				lsInf.read(file);
				if (file.remaining() <= SAVESCREENSHOTSIZE) {
					file.close();
					return -1;
				}

				file.read(pic.data, 0, SAVESCREENSHOTSIZE);

				engine.getrender().invalidatetile(SaveManager.Screenshot, 0, -1);
				file.close();
				return 1;
			} else
				lsInf.info = "Incompatible ver: " + nVersion;
			if (!file.isClosed())
				file.close();
		} else
			lsInf.clear();
		return -1;
	}

	public static final char[] filenum = new char[4];

	public static String makeNum(int num) {
		filenum[3] = (char) ((num % 10) + 48);
		filenum[2] = (char) (((num / 10) % 10) + 48);
		filenum[1] = (char) (((num / 100) % 10) + 48);
		filenum[0] = (char) (((num / 1000) % 10) + 48);

		return new String(filenum);
	}

	public static int checkSave(Resource bb) {
		String signature = bb.readString(4);

		if (signature == null || !signature.equals(savsign))
			return 0;

		return bb.readShort();
	}

	public static boolean checkfile(Resource bb) {
		int nVersion = checkSave(bb);
		if (nVersion != currentGdxSave)
			return false;

		if (!loader.load(bb))
			return false;

		return true;
	}

	public static void LoadGDXBlock() {
		mUserFlag = loader.gUserMap ? UserFlag.UserMap : UserFlag.None;
		boardfilename = loader.boardfilename;
	}

	public static void load() {
		mission = loader.mission;
		gDifficulty = loader.difficulty;

		LoadGDXBlock();
		PlayerLoad();
		MapLoad();
		SectorLoad();
		AnimationLoad();

		screenpeek = myconnectindex;
		lockclock = loader.lockclock;
		engine.srand(loader.randomseed);

		hours = loader.hours;
		minutes = loader.minutes;
		seconds = loader.seconds;

		visibility = loader.visibility;
		parallaxtype = loader.parallaxtype;
		parallaxyoffs = loader.parallaxyoffs;
		System.arraycopy(loader.pskyoff, 0, pskyoff, 0, MAXPSKYTILES);
		pskybits = loader.pskybits;
		parallaxvisibility = loader.parallaxvisibility;

		System.arraycopy(loader.show2dsector, 0, show2dsector, 0, (MAXSECTORS + 7) >> 3);
		System.arraycopy(loader.show2dwall, 0, show2dwall, 0, (MAXWALLSV7 + 7) >> 3);
		System.arraycopy(loader.show2dsprite, 0, show2dsprite, 0, (MAXSPRITES + 7) >> 3);
		automapping = loader.automapping;

		goreflag = loader.goreflag;
		gViewMode = kView3D;

		tektagload();

		System.arraycopy(loader.spriteXT, 0, spriteXT, 0, MAXSPRITES);

		tekloadmissioninfo();
	}

	public static void tekloadmissioninfo() {

		System.arraycopy(loader.symbols, 0, symbols, 0, MAXSYMBOLS);
		System.arraycopy(loader.symbolsdeposited, 0, symbolsdeposited, 0, MAXSYMBOLS);

		currentmapno = loader.currentmapno;
		numlives = loader.numlives;
		mission_accomplished = loader.mission_accomplished;
		civillianskilled = loader.civillianskilled;
		generalplay = loader.generalplay;
		singlemapmode = loader.singlemapmode;
		allsymsdeposited = loader.allsymsdeposited;
		killedsonny = loader.killedsonny;
	}

	public static void tektagload() {

		numanimates = loader.numanimates;
		System.arraycopy(loader.animpic, 0, animpic, 0, numanimates);
		numdelayfuncs = loader.numdelayfuncs;
		System.arraycopy(loader.delayfunc, 0, delayfunc, 0, numdelayfuncs);
		System.arraycopy(loader.onelev, 0, onelev, 0, MAXPLAYERS);
		secnt = loader.secnt;
		System.arraycopy(loader.sectoreffect, 0, sectoreffect, 0, MAXSECTORS);
		System.arraycopy(loader.sexref, 0, sexref, 0, MAXSECTORS);
		numdoors = loader.numdoors;
		System.arraycopy(loader.doortype, 0, doortype, 0, numdoors);
		System.arraycopy(loader.doorxref, 0, doorxref, 0, MAXSECTORS);
		numfloordoors = loader.numfloordoors;
		System.arraycopy(loader.floordoor, 0, floordoor, 0, numfloordoors);
		System.arraycopy(loader.fdxref, 0, fdxref, 0, MAXSECTORS);
		numvehicles = loader.numvehicles;
		System.arraycopy(loader.sectorvehicle, 0, sectorvehicle, 0, numvehicles);

		// must reinvoke vehicle sounds since all sounds were stopped
		// else updatevehiclesounds will update whatever is using
		// dsoundptr[vptr.soundindex]
		clearvehiclesoundindexes();

		System.arraycopy(loader.elevator, 0, elevator, 0, MAXSECTORS);
		sprelevcnt = loader.sprelevcnt;
		System.arraycopy(loader.spriteelev, 0, spriteelev, 0, sprelevcnt);
		totalmapsndfx = loader.totalmapsndfx;
		System.arraycopy(loader.mapsndfx, 0, mapsndfx, 0, totalmapsndfx);

		for (int i = 0; i < totalmapsndfx; i++) {
			// did we leave with a TOGGLED sound playong ?
			if ((mapsndfx[i].type == MAP_SFX_TOGGLED) && (mapsndfx[i].id != -1)) {
				mapsndfx[i].id = playsound(mapsndfx[i].snum, mapsndfx[i].x, mapsndfx[i].y, mapsndfx[i].loops,
						ST_UNIQUE);
			}
		}
	}

	public static void quicksave() {
		if (gPlayer[myconnectindex].health != 0) {
			gQuickSaving = true;
		}
	}

	public static boolean canLoad(String filename) {
		FileResource fil = BuildGdx.compat.open(filename, Path.User, Mode.Read);
		if (fil != null) {
			int nVersion = checkSave(fil) & 0xFFFF;

			fil.close();
			return nVersion == currentGdxSave;
		}
		return false;
	}

	public static void quickload() {
		if (numplayers > 1)
			return;

		final String loadname = game.pSavemgr.getLast();
		if (loadname != null) {
			if (canLoad(loadname)) {
				game.changeScreen(gLoadingScreen);
				gLoadingScreen.init(new Runnable() {
					@Override
					public void run() {
						if (!loadgame(loadname)) {
							game.setPrevScreen();
							if (game.isCurrentScreen(gGameScreen)) {
								showmessage("Incompatible version of saved game found!");
								game.pNet.ready2send = true;
							}
						}
					}
				});
			}
		}
	}

	public static void PlayerLoad() {
		numplayers = loader.numplayers;
		System.arraycopy(loader.connectpoint2, 0, connectpoint2, 0, MAXPLAYERS);

		for (int i = 0; i < MAXPLAYERS; i++) {
			gPlayer[i].copy(loader.gPlayer[i]);
			gPlayer[i].pInput.Copy(loader.gPlayer[i].pInput);
		}
	}

	public static void MapLoad() {
		numwalls = loader.numwalls;
		for (int w = 0; w < numwalls; w++) {
			if (wall[w] == null)
				wall[w] = new WALL();
			wall[w].set(loader.wall[w]);
		}

		numsectors = loader.numsectors;
		for (int s = 0; s < numsectors; s++) {
			if (sector[s] == null)
				sector[s] = new SECTOR();
			sector[s].set(loader.sector[s]);
		}

		for (int i = 0; i < MAXSPRITES; i++) {
			if (sprite[i] == null)
				sprite[i] = new SPRITE();
			sprite[i].set(loader.sprite[i]);
		}

		System.arraycopy(loader.headspritesect, 0, headspritesect, 0, MAXSECTORS + 1);
		System.arraycopy(loader.headspritestat, 0, headspritestat, 0, MAXSTATUS + 1);

		System.arraycopy(loader.prevspritesect, 0, prevspritesect, 0, MAXSPRITES);
		System.arraycopy(loader.prevspritestat, 0, prevspritestat, 0, MAXSPRITES);
		System.arraycopy(loader.nextspritesect, 0, nextspritesect, 0, MAXSPRITES);
		System.arraycopy(loader.nextspritestat, 0, nextspritestat, 0, MAXSPRITES);
	}

	public static void AnimationLoad() {
		for (int i = 0; i < MAXANIMATES; i++) {
			gAnimationData[i].id = loader.gAnimationData[i].id;
			gAnimationData[i].type = loader.gAnimationData[i].type;
			gAnimationData[i].ptr = loader.gAnimationData[i].ptr;
			gAnimationData[i].goal = loader.gAnimationData[i].goal;
			gAnimationData[i].vel = loader.gAnimationData[i].vel;
			gAnimationData[i].acc = loader.gAnimationData[i].acc;
		}
		gAnimationCount = loader.gAnimationCount;

		for (int i = gAnimationCount - 1; i >= 0; i--) {
			ANIMATION gAnm = gAnimationData[i];
			Object object = (gAnm.ptr = getobject(gAnm.id, gAnm.type));
			switch (gAnm.type) {
			case WALLX:
			case WALLY:
				game.pInt.setwallinterpolate(gAnm.id, (WALL) object);
				break;
			case FLOORZ:
				game.pInt.setfloorinterpolate(gAnm.id, (SECTOR) object);
				break;
			case CEILZ:
				game.pInt.setceilinterpolate(gAnm.id, (SECTOR) object);
				break;
			}
		}
	}

	public static void SectorLoad() {
		System.arraycopy(loader.rotatespritelist, 0, rotatespritelist, 0, loader.rotatespritelist.length);
		rotatespritecnt = loader.rotatespritecnt;
		System.arraycopy(loader.warpsectorlist, 0, warpsectorlist, 0, loader.warpsectorlist.length);
		warpsectorcnt = loader.warpsectorcnt;
		System.arraycopy(loader.xpanningsectorlist, 0, xpanningsectorlist, 0, loader.xpanningsectorlist.length);
		xpanningsectorcnt = loader.xpanningsectorcnt;
		System.arraycopy(loader.ypanningwalllist, 0, ypanningwalllist, 0, loader.ypanningwalllist.length);
		ypanningwallcnt = loader.ypanningwallcnt;
		System.arraycopy(loader.floorpanninglist, 0, floorpanninglist, 0, loader.floorpanninglist.length);
		floorpanningcnt = loader.floorpanningcnt;
		System.arraycopy(loader.dragsectorlist, 0, dragsectorlist, 0, loader.dragsectorlist.length);
		System.arraycopy(loader.dragxdir, 0, dragxdir, 0, loader.dragxdir.length);
		System.arraycopy(loader.dragydir, 0, dragydir, 0, loader.dragydir.length);
		dragsectorcnt = loader.dragsectorcnt;
		System.arraycopy(loader.dragx1, 0, dragx1, 0, loader.dragx1.length);
		System.arraycopy(loader.dragy1, 0, dragy1, 0, loader.dragy1.length);
		System.arraycopy(loader.dragx2, 0, dragx2, 0, loader.dragx2.length);
		System.arraycopy(loader.dragy2, 0, dragy2, 0, loader.dragy2.length);
		System.arraycopy(loader.dragfloorz, 0, dragfloorz, 0, loader.dragfloorz.length);
		swingcnt = loader.swingcnt;
		for (int i = 0; i < 32; i++)
			System.arraycopy(loader.swingwall[i], 0, swingwall[i], 0, loader.swingwall[i].length);
		System.arraycopy(loader.swingsector, 0, swingsector, 0, loader.swingsector.length);
		System.arraycopy(loader.swingangopen, 0, swingangopen, 0, loader.swingangopen.length);
		System.arraycopy(loader.swingangclosed, 0, swingangclosed, 0, loader.swingangclosed.length);
		System.arraycopy(loader.swingangopendir, 0, swingangopendir, 0, loader.swingangopendir.length);
		System.arraycopy(loader.swingang, 0, swingang, 0, loader.swingang.length);
		System.arraycopy(loader.swinganginc, 0, swinganginc, 0, loader.swinganginc.length);
		for (int i = 0; i < 32; i++)
			System.arraycopy(loader.swingx[i], 0, swingx[i], 0, loader.swingx[i].length);
		for (int i = 0; i < 32; i++)
			System.arraycopy(loader.swingy[i], 0, swingy[i], 0, loader.swingy[i].length);
		System.arraycopy(loader.revolvesector, 0, revolvesector, 0, loader.revolvesector.length);
		System.arraycopy(loader.revolveang, 0, revolveang, 0, loader.revolveang.length);
		revolvecnt = loader.revolvecnt;
		for (int i = 0; i < 4; i++)
			System.arraycopy(loader.revolvex[i], 0, revolvex[i], 0, loader.revolvex[i].length);
		for (int i = 0; i < 4; i++)
			System.arraycopy(loader.revolvey[i], 0, revolvey[i], 0, loader.revolvey[i].length);
		System.arraycopy(loader.revolvepivotx, 0, revolvepivotx, 0, loader.revolvepivotx.length);
		System.arraycopy(loader.revolvepivoty, 0, revolvepivoty, 0, loader.revolvepivoty.length);
		for (int i = 0; i < 4; i++)
			System.arraycopy(loader.subwaytracksector[i], 0, subwaytracksector[i], 0,
					loader.subwaytracksector[i].length);
		System.arraycopy(loader.subwaynumsectors, 0, subwaynumsectors, 0, loader.subwaynumsectors.length);
		subwaytrackcnt = loader.subwaytrackcnt;
		for (int i = 0; i < 4; i++)
			System.arraycopy(loader.subwaystop[i], 0, subwaystop[i], 0, loader.subwaystop[i].length);
		System.arraycopy(loader.subwaystopcnt, 0, subwaystopcnt, 0, loader.subwaystopcnt.length);
		System.arraycopy(loader.subwaytrackx1, 0, subwaytrackx1, 0, loader.subwaytrackx1.length);
		System.arraycopy(loader.subwaytracky1, 0, subwaytracky1, 0, loader.subwaytracky1.length);
		System.arraycopy(loader.subwaytrackx2, 0, subwaytrackx2, 0, loader.subwaytrackx2.length);
		System.arraycopy(loader.subwaytracky2, 0, subwaytracky2, 0, loader.subwaytracky2.length);
		System.arraycopy(loader.subwayx, 0, subwayx, 0, loader.subwayx.length);
		System.arraycopy(loader.subwaygoalstop, 0, subwaygoalstop, 0, loader.subwaygoalstop.length);
		System.arraycopy(loader.subwayvel, 0, subwayvel, 0, loader.subwayvel.length);
		System.arraycopy(loader.subwaypausetime, 0, subwaypausetime, 0, loader.subwaypausetime.length);
		System.arraycopy(loader.waterfountainwall, 0, waterfountainwall, 0, loader.waterfountainwall.length);
		System.arraycopy(loader.waterfountaincnt, 0, waterfountaincnt, 0, loader.waterfountaincnt.length);
		System.arraycopy(loader.slimesoundcnt, 0, slimesoundcnt, 0, loader.slimesoundcnt.length);
	}

	public static boolean loadgame(String filename) {
		FileResource fil = BuildGdx.compat.open(filename, Path.User, Mode.Read);
		if (fil != null) {
			boolean status = checkfile(fil);

			fil.close();
			if (status) {
				load();
				if (lastload == null || lastload.isEmpty())
					lastload = filename;

				// initialize blast sector flashes
				if (sectflash == null)
					sectflash = new sectflashtype();
				sectflash.sectnum = 0;
				sectflash.ovis = 0;
				sectflash.step = 0;

				// intitialize subwaysound[]s
				for (int i = 0; i < 4; i++)
					subwaysound[i] = -1;

				for (int i = connecthead; i >= 0; i = connectpoint2[i])
					initplayersprite((short) i);

				gPrecacheScreen.init(false, new Runnable() {
					@Override
					public void run() {
						totalclock = lockclock;
						game.pNet.ototalclock = lockclock;

						game.gPaused = false;
						game.changeScreen(gGameScreen);
						game.pNet.ready2send = true;

						game.nNetMode = NetMode.Single;

						showmessage("GAME LOADED");

						if (generalplay == 1)
							startmusic((int) (7 * Math.random()));
						else {
//						    	if(oldmapno != currentmapno) {
							if (currentmapno <= 3)
								menusong(1);
							else
								startmusic(mission);
//						    	}
						}

						game.pInput.resetMousePos();
						System.gc();
						Console.Println("debug: end loadgame()", OSDTEXT_BLUE);
					}
				});
				game.changeScreen(gPrecacheScreen);

			}
			return status;
		}

		return false;
	}

	public static void SectorSave(FileResource fil) {
		ByteBuffer bb = ByteBuffer.allocate(5498);
		bb.order(ByteOrder.LITTLE_ENDIAN);

		for (int i = 0; i < 16; i++)
			bb.putShort(rotatespritelist[i]);
		bb.putShort(rotatespritecnt); // 34
		for (int i = 0; i < 16; i++)
			bb.putShort(warpsectorlist[i]); // 66
		bb.putShort(warpsectorcnt); // 68
		for (int i = 0; i < 16; i++)
			bb.putShort(xpanningsectorlist[i]); // 100
		bb.putShort(xpanningsectorcnt); // 102
		for (int i = 0; i < 64; i++)
			bb.putShort(ypanningwalllist[i]); // 230
		bb.putShort(ypanningwallcnt); // 232
		for (int i = 0; i < 64; i++)
			bb.putShort(floorpanninglist[i]);
		bb.putShort(floorpanningcnt); // 362
		for (int i = 0; i < 16; i++)
			bb.putShort(dragsectorlist[i]); // 394
		for (int i = 0; i < 16; i++)
			bb.putShort(dragxdir[i]); // 426
		for (int i = 0; i < 16; i++)
			bb.putShort(dragydir[i]); // 458
		bb.putShort(dragsectorcnt); // 460
		for (int i = 0; i < 16; i++)
			bb.putInt(dragx1[i]); // 524
		for (int i = 0; i < 16; i++)
			bb.putInt(dragy1[i]); // 588
		for (int i = 0; i < 16; i++)
			bb.putInt(dragx2[i]); // 652
		for (int i = 0; i < 16; i++)
			bb.putInt(dragy2[i]); // 716
		for (int i = 0; i < 16; i++)
			bb.putInt(dragfloorz[i]); // 780
		bb.putShort(swingcnt); // 782
		for (int a = 0; a < 32; ++a)
			for (int b = 0; b < 5; ++b)
				bb.putShort(swingwall[a][b]); // 1102
		for (int i = 0; i < 32; i++)
			bb.putShort(swingsector[i]); // 1166
		for (int i = 0; i < 32; i++)
			bb.putShort(swingangopen[i]); // 1230
		for (int i = 0; i < 32; i++)
			bb.putShort(swingangclosed[i]); // 1294
		for (int i = 0; i < 32; i++)
			bb.putShort(swingangopendir[i]); // 1358
		for (int i = 0; i < 32; i++)
			bb.putShort(swingang[i]); // 1422
		for (int i = 0; i < 32; i++)
			bb.putShort(swinganginc[i]); // 1486
		for (int a = 0; a < 32; ++a)
			for (int b = 0; b < 8; ++b)
				bb.putInt(swingx[a][b]);
		;
		for (int a = 0; a < 32; ++a)
			for (int b = 0; b < 8; ++b)
				bb.putInt(swingy[a][b]); // 3534
		for (int i = 0; i < 4; i++)
			bb.putShort(revolvesector[i]); // 3542
		for (int i = 0; i < 4; i++)
			bb.putShort(revolveang[i]);
		bb.putShort(revolvecnt); // 3552
		for (int a = 0; a < 4; ++a)
			for (int b = 0; b < 16; ++b)
				bb.putInt(revolvex[a][b]);
		; // 3808
		for (int a = 0; a < 4; ++a)
			for (int b = 0; b < 16; ++b)
				bb.putInt(revolvey[a][b]);
		; // 4064
		for (int i = 0; i < 4; i++)
			bb.putInt(revolvepivotx[i]); // 4080
		for (int i = 0; i < 4; i++)
			bb.putInt(revolvepivoty[i]); // 4096
		for (int a = 0; a < 4; ++a)
			for (int b = 0; b < 128; ++b)
				bb.putShort(subwaytracksector[a][b]); // 5120
		for (int i = 0; i < 4; i++)
			bb.putShort(subwaynumsectors[i]);
		bb.putShort(subwaytrackcnt); // 5130
		for (int a = 0; a < 4; ++a)
			for (int b = 0; b < 8; ++b)
				bb.putInt(subwaystop[a][b]); // 5258
		for (int i = 0; i < 4; i++)
			bb.putInt(subwaystopcnt[i]);
		for (int i = 0; i < 4; i++)
			bb.putInt(subwaytrackx1[i]);
		for (int i = 0; i < 4; i++)
			bb.putInt(subwaytracky1[i]);
		for (int i = 0; i < 4; i++)
			bb.putInt(subwaytrackx2[i]);
		for (int i = 0; i < 4; i++)
			bb.putInt(subwaytracky2[i]);
		for (int i = 0; i < 4; i++)
			bb.putInt(subwayx[i]);
		for (int i = 0; i < 4; i++)
			bb.putInt(subwaygoalstop[i]);
		for (int i = 0; i < 4; i++)
			bb.putInt(subwayvel[i]);
		for (int i = 0; i < 4; i++)
			bb.putInt(subwaypausetime[i]); // 5402
		for (int i = 0; i < MAXPLAYERS; i++)
			bb.putShort(waterfountainwall[i]);
		for (int i = 0; i < MAXPLAYERS; i++)
			bb.putShort(waterfountaincnt[i]);
		for (int i = 0; i < MAXPLAYERS; i++)
			bb.putShort(slimesoundcnt[i]); // 5498

		fil.writeBytes(bb.array(), bb.capacity());
	}

	public static void PlayerSave(FileResource fil) {
		ByteBuffer bb = ByteBuffer.allocate(2 + MAXPLAYERS * Player.sizeof + MAXPLAYERS * Input.sizeof);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		bb.putShort(numplayers);

		for (int i = 0; i < MAXPLAYERS; i++) {
			bb.put(gPlayer[i].getBytes());
			bb.put(gPlayer[i].pInput.getBytes());
		}

		fil.writeBytes(bb.array(), bb.capacity());
	}

	public static void MapSave(FileResource fil) {
		int bufsize = 2 + (numsectors * SECTOR.sizeof) + 2 + (numwalls * WALL.sizeof) + (MAXSPRITES * SPRITE.sizeof)
				+ (MAXSECTORS + 1) * 2 + (MAXSTATUS + 1) * 2 + MAXSPRITES * 8;

		ByteBuffer bb = ByteBuffer.allocate(bufsize);
		bb.order(ByteOrder.LITTLE_ENDIAN);

		bb.putShort(numwalls);
		for (int w = 0; w < numwalls; w++)
			bb.put(wall[w].getBytes());

		bb.putShort(numsectors);
		for (int s = 0; s < numsectors; s++)
			bb.put(sector[s].getBytes());

		// Store all sprites (even holes) to preserve indeces
		for (int i = 0; i < MAXSPRITES; i++)
			bb.put(sprite[i].getBytes());

		for (int i = 0; i <= MAXSECTORS; i++)
			bb.putShort(headspritesect[i]);

		for (int i = 0; i <= MAXSTATUS; i++)
			bb.putShort(headspritestat[i]);

		for (int i = 0; i < MAXSPRITES; i++) {
			bb.putShort(prevspritesect[i]);
			bb.putShort(prevspritestat[i]);
			bb.putShort(nextspritesect[i]);
			bb.putShort(nextspritestat[i]);
		}
		fil.writeBytes(bb.array(), bb.capacity());
	}

	public static void AnimationSave(FileResource fil) {
		for (int i = 0; i < MAXANIMATES; i++) {
			fil.writeShort(gAnimationData[i].id);
			fil.writeByte(gAnimationData[i].type);
			fil.writeInt(gAnimationData[i].goal);
			fil.writeInt(gAnimationData[i].vel);
			fil.writeInt(gAnimationData[i].acc);
		}
		fil.writeInt(gAnimationCount);
	}

	public static void SaveVersion(FileResource fil, int nVersion) {
		fil.writeBytes(savsign.toCharArray(), 4);
		fil.writeShort(nVersion);
	}

	public static void SaveGDXBlock(FileResource fil) {
		ByteBuffer bb = ByteBuffer.allocate(SAVEGDXDATA);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		bb.put(gGameScreen.captBuffer);
		gGameScreen.captBuffer = null;

		bb.put(mUserFlag == UserFlag.UserMap ? (byte) 1 : 0);
		byte[] name = new byte[144];
		if (boardfilename != null)
			System.arraycopy(boardfilename.getBytes(), 0, name, 0, Math.min(boardfilename.length(), 144));
		bb.put(name);

		fil.writeBytes(bb.array(), SAVEGDXDATA);
	}

	public static void SaveHeader(FileResource fil, String savename, long time) {
		SaveVersion(fil, currentGdxSave);

		byte[] buf = new byte[8];
		LittleEndian.putLong(buf, 0, time);
		fil.writeBytes(buf, 8);
		fil.writeBytes(savename.toCharArray(), SAVENAME);

		fil.writeInt(mission);
		fil.writeInt(gDifficulty);
	}

	public static int savegame(String savename, String filename) {
		File file = BuildGdx.compat.checkFile(filename, Path.User);
		if (file != null)
			file.delete();

		FileResource fil = BuildGdx.compat.open(filename, Path.User, Mode.Write);
		if (fil != null) {
			long time = game.date.getCurrentDate();
			save(fil, savename, time);
			game.pSavemgr.add(savename, time, filename);
			lastload = filename;
			return 0;
		} else
			showmessage("Game not saved. Access denied!");

		return -1;
	}

	public static int save(FileResource fil, String savename, long time) {

		SaveHeader(fil, savename, time);
		SaveGDXBlock(fil); // ok

		PlayerSave(fil);
		MapSave(fil);
		SectorSave(fil);
		AnimationSave(fil);

		fil.writeInt(engine.getrand());
		fil.writeInt(hours);
		fil.writeInt(minutes);
		fil.writeInt(seconds);
		fil.writeInt(lockclock);

		fil.writeByte(parallaxtype);
		fil.writeInt(parallaxyoffs);

		ByteBuffer buffer = ByteBuffer.allocate(MAXPSKYTILES * 2);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		for (int i = 0; i < MAXPSKYTILES; i++)
			buffer.putShort(pskyoff[i]);
		fil.writeBytes(buffer.array(), buffer.capacity());
		fil.writeShort(pskybits);
		fil.writeInt(visibility);
		fil.writeInt(parallaxvisibility);
		fil.writeBytes(show2dsector, MAXSECTORS >> 3);
		fil.writeBytes(show2dwall, MAXWALLSV7 >> 3);
		fil.writeBytes(show2dsprite, MAXSPRITES >> 3);
		fil.writeByte(automapping);
		fil.writeByte(goreflag ? 1 : 0);

		tektagsave(fil);

		tekstatsave(fil);

		teksavemissioninfo(fil);

		fil.close();

		showmessage("GAME SAVED");

		System.gc();
		return 0;
	}

	public static void tekstatsave(FileResource fh) {
		ByteBuffer buffer = ByteBuffer.allocate(MAXSPRITES * SpriteXT.sizeof);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		for (int i = 0; i < MAXSPRITES; i++)
			buffer.put(spriteXT[i].getBytes());
		fh.writeBytes(buffer.array(), buffer.capacity());
	}

	public static void teksavemissioninfo(FileResource fil) { // ok
		for (int i = 0; i < MAXSYMBOLS; i++)
			fil.writeByte(symbols[i] ? 1 : 0);
		for (int i = 0; i < MAXSYMBOLS; i++)
			fil.writeByte(symbolsdeposited[i] ? 1 : 0);
		fil.writeInt(currentmapno);
		fil.writeByte(numlives);
		fil.writeByte(mission_accomplished);
		fil.writeInt(civillianskilled);
		fil.writeByte(generalplay);
		fil.writeByte(singlemapmode);
		fil.writeInt(allsymsdeposited);
		fil.writeInt(killedsonny);
	}

	public static void tektagsave(FileResource fil) {
		int bufsize = 4 + numanimates * Animpic.sizeof + 2 + numdelayfuncs * Delayfunc.sizeof + MAXPLAYERS + 4
				+ Sectoreffect.sizeof * MAXSECTORS + 4 * MAXSECTORS + 4 + 4 * MAXSECTORS + numdoors * Doortype.sizeof
				+ 4 + Floordoor.sizeof * numfloordoors + 4 * MAXSECTORS + 4 + Sectorvehicle.sizeof * numvehicles
				+ MAXSECTORS * Elevatortype.sizeof + 4 + sprelevcnt * Spriteelev.sizeof + 4
				+ totalmapsndfx * Mapsndfxtype.sizeof;

		ByteBuffer bb = ByteBuffer.allocate(bufsize);
		bb.order(ByteOrder.LITTLE_ENDIAN);

		bb.putInt(numanimates);
		for (int i = 0; i < numanimates; i++)
			bb.put(animpic[i].getBytes());

		bb.putShort(numdelayfuncs);
		for (int i = 0; i < numdelayfuncs; i++)
			bb.put(delayfunc[i].getBytes());

		for (int i = 0; i < MAXPLAYERS; i++)
			bb.put((byte) (onelev[i] ? 1 : 0));

		bb.putInt(secnt);
		for (int i = 0; i < MAXSECTORS; i++)
			bb.put(sectoreffect[i].getBytes());

		for (int i = 0; i < MAXSECTORS; i++)
			bb.putInt(sexref[i]);

		bb.putInt(numdoors);
		for (int i = 0; i < numdoors; i++)
			bb.put(doortype[i].getBytes());

		for (int i = 0; i < MAXSECTORS; i++)
			bb.putInt(doorxref[i]);

		bb.putInt(numfloordoors);
		for (int i = 0; i < numfloordoors; i++)
			bb.put(floordoor[i].getBytes());

		for (int i = 0; i < MAXSECTORS; i++)
			bb.putInt(fdxref[i]);

		bb.putInt(numvehicles);
		for (int i = 0; i < numvehicles; i++)
			bb.put(sectorvehicle[i].getBytes());

		for (int i = 0; i < MAXSECTORS; i++)
			bb.put(elevator[i].getBytes());

		bb.putInt(sprelevcnt);
		for (int i = 0; i < sprelevcnt; i++)
			bb.put(spriteelev[i].getBytes());

		bb.putInt(totalmapsndfx);
		for (int i = 0; i < totalmapsndfx; i++)
			bb.put(mapsndfx[i].getBytes());

		fil.writeBytes(bb.array(), bufsize);
	}
}
