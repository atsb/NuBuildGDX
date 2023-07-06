package ru.m210projects.Tekwar;

import static ru.m210projects.Build.Engine.MAXPSKYTILES;
import static ru.m210projects.Build.Engine.MAXSECTORS;
import static ru.m210projects.Build.Engine.MAXSPRITES;
import static ru.m210projects.Build.Engine.parallaxyscale;
import static ru.m210projects.Build.Engine.pskybits;
import static ru.m210projects.Build.Engine.pskyoff;
import static ru.m210projects.Build.Engine.zeropskyoff;
import static ru.m210projects.Build.Gameutils.loadGdxDef;
import static ru.m210projects.Build.Net.Mmulti.myconnectindex;
import static ru.m210projects.Build.OnSceenDisplay.Console.OSDTEXT_BLUE;
import static ru.m210projects.Build.OnSceenDisplay.Console.osd_argv;
import static ru.m210projects.Tekwar.Animate.initanimations;
import static ru.m210projects.Tekwar.Factory.TekMenuHandler.CORRUPTLOAD;
import static ru.m210projects.Tekwar.Factory.TekMenuHandler.HELP;
import static ru.m210projects.Tekwar.Factory.TekMenuHandler.LASTSAVE;
import static ru.m210projects.Tekwar.Factory.TekMenuHandler.MAIN;
import static ru.m210projects.Tekwar.Globals.CLKIPS;
import static ru.m210projects.Tekwar.Globals.GUN1FLAG;
import static ru.m210projects.Tekwar.Globals.GUN2FLAG;
import static ru.m210projects.Tekwar.Globals.GUN3FLAG;
import static ru.m210projects.Tekwar.Globals.GUN4FLAG;
import static ru.m210projects.Tekwar.Globals.GUN5FLAG;
import static ru.m210projects.Tekwar.Globals.GUN6FLAG;
import static ru.m210projects.Tekwar.Globals.GUN7FLAG;
import static ru.m210projects.Tekwar.Globals.GUN8FLAG;
import static ru.m210projects.Tekwar.Globals.KENSPLAYERHEIGHT;
import static ru.m210projects.Tekwar.Globals.MAXAMMO;
import static ru.m210projects.Tekwar.Globals.TICSPERFRAME;
import static ru.m210projects.Tekwar.Names.GUN08FIRESTART;
import static ru.m210projects.Tekwar.Names.GUNDEMFIREEND;
import static ru.m210projects.Tekwar.Names.GUNDEMFIRESTART;
import static ru.m210projects.Tekwar.Names.GUNDEMREADY;
import static ru.m210projects.Tekwar.Names.HCDEVICE;
import static ru.m210projects.Tekwar.Names.RVDEVICE;
import static ru.m210projects.Tekwar.Names.WPDEVICE;
import static ru.m210projects.Tekwar.Player.gPlayer;
import static ru.m210projects.Tekwar.Tekchng.changehealth;
import static ru.m210projects.Tekwar.Tekgun.guntype;
import static ru.m210projects.Tekwar.Tekldsv.FindSaves;
import static ru.m210projects.Tekwar.Tekmap.symbols;
import static ru.m210projects.Tekwar.Tekprep.spriteXT;
import static ru.m210projects.Tekwar.Tekprep.tekpreinit;
import static ru.m210projects.Tekwar.Teksnd.searchCDtracks;
import static ru.m210projects.Tekwar.Teksnd.sndInit;
import static ru.m210projects.Tekwar.Tektag.MAXANIMPICS;
import static ru.m210projects.Tekwar.Tektag.MAXDELAYFUNCTIONS;
import static ru.m210projects.Tekwar.Tektag.MAXDOORS;
import static ru.m210projects.Tekwar.Tektag.MAXFLOORDOORS;
import static ru.m210projects.Tekwar.Tektag.MAXMAPSOUNDFX;
import static ru.m210projects.Tekwar.Tektag.MAXSECTORVEHICLES;
import static ru.m210projects.Tekwar.Tektag.MAXSPRITEELEVS;
import static ru.m210projects.Tekwar.Tektag.animpic;
import static ru.m210projects.Tekwar.Tektag.delayfunc;
import static ru.m210projects.Tekwar.Tektag.doortype;
import static ru.m210projects.Tekwar.Tektag.elevator;
import static ru.m210projects.Tekwar.Tektag.flags32;
import static ru.m210projects.Tekwar.Tektag.floordoor;
import static ru.m210projects.Tekwar.Tektag.mapsndfx;
import static ru.m210projects.Tekwar.Tektag.sectoreffect;
import static ru.m210projects.Tekwar.Tektag.sectorvehicle;
import static ru.m210projects.Tekwar.Tektag.spriteelev;
import static ru.m210projects.Tekwar.View.FadeInit;
import static ru.m210projects.Tekwar.View.hcpos;
import static ru.m210projects.Tekwar.View.rvpos;
import static ru.m210projects.Tekwar.View.wppos;

import java.util.Arrays;
import java.util.Iterator;

import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.FileHandle.Compat.Path;
import ru.m210projects.Build.FileHandle.DirectoryEntry;
import ru.m210projects.Build.FileHandle.FileEntry;
import ru.m210projects.Build.FileHandle.Group;
import ru.m210projects.Build.FileHandle.GroupResource;
import ru.m210projects.Build.FileHandle.UserGroup;
import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Build.OnSceenDisplay.OSDCOMMAND;
import ru.m210projects.Build.OnSceenDisplay.OSDCVARFUNC;
import ru.m210projects.Build.Pattern.BuildFactory;
import ru.m210projects.Build.Pattern.BuildGame;
import ru.m210projects.Build.Settings.BuildConfig;
import ru.m210projects.Build.Types.MemLog;
import ru.m210projects.Tekwar.Factory.TekEngine;
import ru.m210projects.Tekwar.Factory.TekFactory;
import ru.m210projects.Tekwar.Factory.TekMenuHandler;
import ru.m210projects.Tekwar.Menus.MainMenu;
import ru.m210projects.Tekwar.Menus.MenuCorruptGame;
import ru.m210projects.Tekwar.Menus.MenuHelp;
import ru.m210projects.Tekwar.Menus.MenuLastLoad;
import ru.m210projects.Tekwar.Screens.CreditsScreen;
import ru.m210projects.Tekwar.Screens.CutsceneScreen;
import ru.m210projects.Tekwar.Screens.GameScreen;
import ru.m210projects.Tekwar.Screens.LoadingScreen;
import ru.m210projects.Tekwar.Screens.MissionScreen;
import ru.m210projects.Tekwar.Screens.PrecacheScreen;
import ru.m210projects.Tekwar.Screens.SmkMenu;
import ru.m210projects.Tekwar.Types.Animpic;
import ru.m210projects.Tekwar.Types.Delayfunc;
import ru.m210projects.Tekwar.Types.Doortype;
import ru.m210projects.Tekwar.Types.Elevatortype;
import ru.m210projects.Tekwar.Types.Floordoor;
import ru.m210projects.Tekwar.Types.Guntype;
import ru.m210projects.Tekwar.Types.Mapsndfxtype;
import ru.m210projects.Tekwar.Types.Sectoreffect;
import ru.m210projects.Tekwar.Types.Sectorvehicle;
import ru.m210projects.Tekwar.Types.SpriteXT;
import ru.m210projects.Tekwar.Types.Spriteelev;

public class Main extends BuildGame {

	/*
	 * Changelog:
	 * Flamethrower idle animation disabled
	 * next/prev weapon feature
	 * TODO:
	 */

	public static final String appdef = "twgdx.def";

	public TekMenuHandler menu;

	public static SmkMenu gMissionScreen;
	public static CutsceneScreen gCutsceneScreen;
	public static LoadingScreen gLoadingScreen;
	public static GameScreen gGameScreen;
	public static CreditsScreen gCreditsScreen;
	public static PrecacheScreen gPrecacheScreen;

	public static Main game;
	public static TekEngine engine;
	public static Config tekcfg;

	public static short screenpeek = 0;
	public static int hours, minutes, seconds;
	public static int fortieth;

	public static enum UserFlag {
		None, UserMap
	};

	public static UserFlag mUserFlag = UserFlag.None;

	public static boolean TEKDEMO;

	public static int lockclock;
	public static String boardfilename;

	public Main(BuildConfig cfg, String appname, String sversion, boolean release, boolean isDemo) {
		super(cfg, appname, sversion, release);
		game = this;
		tekcfg = (Config) cfg;
		TEKDEMO = isDemo;
	}

	@Override
	public BuildFactory getFactory() {
		return new TekFactory(this);
	}

	@Override
	public boolean init() throws Exception {
		engine.inittimer(CLKIPS);
		FadeInit();
		Console.Println("tekpreinit");
		tekpreinit();
		Console.Println("tekgamestarted");

		hcpos = (short) -engine.getTile(HCDEVICE).getWidth();
		wppos = (short) -engine.getTile(WPDEVICE).getWidth();
		rvpos = (short) -engine.getTile(RVDEVICE).getWidth();
		seconds = minutes = hours = 0;

		engine.getTile(GUN08FIRESTART).anm &= ~0xFF0000FF; //disable animation

		if(TEKDEMO)
		{
			KENSPLAYERHEIGHT = 44;
			guntype[2] = new Guntype(GUNDEMREADY, GUNDEMFIRESTART,
					GUNDEMFIREEND, 1, new byte[] { 0, 1, 0, 0, 0,
							0, 0, 0 }, 2, TICSPERFRAME * 4);
		}

		ConsoleInit();

		tekmultiskyinit();

		screenpeek = myconnectindex;

		sndInit();

//		tileLoadUserRes();

		Console.Println("Initializing def-scripts...");

		loadGdxDef(baseDef, appdef, "twgdx.dat");

		if (pCfg.autoloadFolder) {
			Console.Println("Initializing autoload folder");
			DirectoryEntry autoload;
			if((autoload = BuildGdx.compat.checkDirectory("autoload")) != null) {
				for (Iterator<FileEntry> it = autoload.getFiles().values().iterator(); it.hasNext();) {
					FileEntry file = it.next();
					if (file.getExtension().equals("zip")) {
						Group group = BuildGdx.cache.add(file.getPath());
						if(group == null) continue;

						GroupResource def = group.open(appdef);
						if(def != null)
						{
							byte[] buf = def.getBytes();
				    		baseDef.loadScript(file.getName(), buf);
				    		def.close();
						}
					} else if (file.getExtension().equals("def"))
						baseDef.loadScript(file);
				}
			}
		}

		FileEntry filgdx = BuildGdx.compat.checkFile(appdef);
		if(filgdx != null)
			baseDef.loadScript(filgdx);
		this.setDefs(baseDef);

		initstruct();

		gCutsceneScreen = new CutsceneScreen(this);
		gMissionScreen = new MissionScreen(this);
		gLoadingScreen = new LoadingScreen(this);
		gGameScreen = new GameScreen(this);
		gCreditsScreen = new CreditsScreen();
		gPrecacheScreen = new PrecacheScreen(this);

		InitCutscenes();
		FindSaves();
		searchCDtracks();

		menu.mMenus[MAIN] = new MainMenu(this);
		menu.mMenus[HELP] = new MenuHelp(this);
		menu.mMenus[LASTSAVE] = new MenuLastLoad(this);
		menu.mMenus[CORRUPTLOAD] = new MenuCorruptGame(this);

		System.gc();
		MemLog.log("create");

		return true;
	}

	private void InitCutscenes() {
		Console.Println("Initializing cutscenes", 0);
		UserGroup group = BuildGdx.cache.add("Cutscenes", false);

		DirectoryEntry dir = BuildGdx.compat.checkDirectory("smk");
		if (dir == null)
			dir = BuildGdx.compat.getDirectory(Path.Game);

		for (Iterator<FileEntry> it = dir.getFiles().values().iterator(); it.hasNext();) {
			FileEntry file = it.next();
			if (file.getExtension().equals("smk"))
				group.add(file, -1);
		}
	}

	private void tekmultiskyinit() {
		// new-style multi-psky handling

		Arrays.fill(pskyoff, (short)0);

		if(parallaxyscale != 65536)
			parallaxyscale = 32768;

        pskyoff[0] = 0;
		pskyoff[1] = 1;
		pskyoff[2] = 2;
		pskyoff[3] = 3;
		pskyoff[4] = 0;
		pskyoff[5] = 1;
		pskyoff[6] = 2;
		pskyoff[7] = 3;

		Arrays.fill(zeropskyoff, (short)0);
	    System.arraycopy(pskyoff, 0, zeropskyoff, 0, MAXPSKYTILES);

	    pskybits=3;
	}

	private void ConsoleInit() {
		Console.Println("Initializing on-screen display system");

		Console.setVersion(appname + " " + sversion, 10, OSDTEXT_BLUE);

		Console.RegisterCvar(new OSDCOMMAND("god", "", new OSDCVARFUNC() {
			@Override
			public void execute() {
				if (game.isCurrentScreen(gGameScreen)) {
					gPlayer[myconnectindex].godMode = !gPlayer[myconnectindex].godMode;
					if (gPlayer[myconnectindex].godMode)
						Console.Println("God mode: On");
					else
						Console.Println("God mode: Off");
				} else
					Console.Println("god: not in a single-player game");
			}
		}));

		Console.RegisterCvar(new OSDCOMMAND("noclip", "", new OSDCVARFUNC() {
			@Override
			public void execute() {
				if (game.isCurrentScreen(gGameScreen)) {
					gPlayer[myconnectindex].noclip = !gPlayer[myconnectindex].noclip;
					if (gPlayer[myconnectindex].noclip)
						Console.Println("Noclip: On");
					else
						Console.Println("Noclip: Off");
				} else
					Console.Println("noclip: not in a single-player game");
			}
		}));

		Console.RegisterCvar(new OSDCOMMAND("give", "", new OSDCVARFUNC() {
			@Override
			public void execute() {
				if (game.isCurrentScreen(gGameScreen)) {
					if (Console.osd_argc != 2) {
						Console.Println("give: <weapons, health, items, symbols>");
						return;
					}

					if (osd_argv[1].equals("health"))
						changehealth(screenpeek, 200);
					else if (osd_argv[1].equals("items")) {
						gPlayer[myconnectindex].invredcards = 1;
						gPlayer[myconnectindex].invbluecards = 1;
						gPlayer[myconnectindex].invaccutrak = 1;
					} else if (osd_argv[1].equals("symbols")) {
						if (TEKDEMO)
							return;

						symbols[0] = true;
						symbols[1] = true;
						symbols[2] = true;
						symbols[3] = true;
						symbols[4] = true;
						symbols[5] = true;
						symbols[6] = true;
					} else if (osd_argv[1].equals("weapons")) {
						for(int i = 0; i < 8; i++)
							gPlayer[myconnectindex].ammo[i] = MAXAMMO;
						gPlayer[myconnectindex].invredcards = 1;
						gPlayer[myconnectindex].invbluecards = 1;
						gPlayer[myconnectindex].invaccutrak = 1;
						gPlayer[myconnectindex].weapons = (flags32[GUN1FLAG] | flags32[GUN2FLAG] | flags32[GUN3FLAG] | flags32[GUN4FLAG]);
						gPlayer[myconnectindex].weapons |= (flags32[GUN5FLAG] | flags32[GUN6FLAG] | flags32[GUN7FLAG] | flags32[GUN8FLAG]);
					} else
						Console.Println("give: <weapons, health, items, symbols>");

				} else
					Console.Println("give: not in a single-player game");
			}
		}));
	}

	private void initstruct() {
		for (int i = 0; i < MAXDOORS; i++)
			doortype[i] = new Doortype();
		for (int i = 0; i < MAXFLOORDOORS; i++)
			floordoor[i] = new Floordoor();

		for (int j = 0; j < MAXANIMPICS; j++)
			animpic[j] = new Animpic();
		for (int j = 0; j < MAXDELAYFUNCTIONS; j++)
			delayfunc[j] = new Delayfunc();
		for (int j = 0; j < MAXSECTORS; j++) {
			sectoreffect[j] = new Sectoreffect();
			elevator[j] = new Elevatortype();
		}
		for (int j = 0; j < MAXSPRITEELEVS; j++)
			spriteelev[j] = new Spriteelev();
		for (int j = 0; j < MAXMAPSOUNDFX; j++)
			mapsndfx[j] = new Mapsndfxtype();
		for (int j = 0; j < MAXSECTORVEHICLES; j++)
			sectorvehicle[j] = new Sectorvehicle();
		for (int i = 0; i < MAXSPRITES; i++)
			spriteXT[i] = new SpriteXT();

		initanimations();
	}

	@Override
	public void show() {
		final Runnable nextLogo = new Runnable() {
			@Override
			public void run() {
				changeScreen(gMissionScreen);
			}
		};

		final Runnable movie4 = new Runnable() {
			@Override
			public void run() {
				if (gCutsceneScreen.init("intro.smk")) {
					gCutsceneScreen.setCallback(nextLogo).escSkipping(true);
					game.changeScreen(gCutsceneScreen);
				} else nextLogo.run();
			}
		};

		final Runnable movie3 = new Runnable() {
			@Override
			public void run() {
				if (gCutsceneScreen.init("tekintro.smk")) {
					gCutsceneScreen.setCallback(movie4).setSkipping(nextLogo).escSkipping(true);
					game.changeScreen(gCutsceneScreen);
				} else movie4.run();
			}
		};

		final Runnable movie2 = new Runnable() {
			@Override
			public void run() {
				if (gCutsceneScreen.init("tekndie.smk")) {
					gCutsceneScreen.setCallback(movie3).setSkipping(nextLogo).escSkipping(true);
					game.changeScreen(gCutsceneScreen);
				} else movie3.run();
			}
		};

		if (gCutsceneScreen.init("tvopen.smk")) {
			gCutsceneScreen.setCallback(movie2).setSkipping(nextLogo).escSkipping(true);
			game.changeScreen(gCutsceneScreen);
		} else movie2.run();
	}

	@Override
	protected byte[] reportData() {
		String text = "boardfilename " + boardfilename;
		text += "\r\n";
		text += "posx " + gPlayer[0].posx;
		text += "\r\n";
		text += "posy " + gPlayer[0].posy;
		text += "\r\n";
		text += "posz " + gPlayer[0].posz;
		text += "\r\n";
		text += "sectnum " + gPlayer[0].cursectnum;
		text += "\r\n";
		return text.getBytes();
	}

}
