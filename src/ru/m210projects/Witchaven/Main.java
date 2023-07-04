package ru.m210projects.Witchaven;

import static ru.m210projects.Build.Engine.MAXSECTORS;
import static ru.m210projects.Build.Engine.parallaxtype;
import static ru.m210projects.Build.Engine.show2dsector;
import static ru.m210projects.Build.Engine.visibility;
import static ru.m210projects.Build.Gameutils.loadGdxDef;
import static ru.m210projects.Build.Net.Mmulti.myconnectindex;
import static ru.m210projects.Build.OnSceenDisplay.Console.OSDTEXT_YELLOW;
import static ru.m210projects.Build.OnSceenDisplay.Console.osd_argv;
import static ru.m210projects.Witchaven.Animate.initanimations;
import static ru.m210projects.Witchaven.Factory.WHMenuHandler.CORRUPTLOAD;
import static ru.m210projects.Witchaven.Factory.WHMenuHandler.GAME;
import static ru.m210projects.Witchaven.Factory.WHMenuHandler.MAIN;
import static ru.m210projects.Witchaven.Globals.MAXSWINGDOORS;
import static ru.m210projects.Witchaven.Globals.TBLACKKEY;
import static ru.m210projects.Witchaven.Globals.TBRASSKEY;
import static ru.m210projects.Witchaven.Globals.TGLASSKEY;
import static ru.m210projects.Witchaven.Globals.TIMERRATE;
import static ru.m210projects.Witchaven.Globals.TIVORYKEY;
import static ru.m210projects.Witchaven.Globals.boardfilename;
import static ru.m210projects.Witchaven.Globals.gOriginalEpisode;
import static ru.m210projects.Witchaven.Globals.tempbuf;
import static ru.m210projects.Witchaven.Potions.MAXPOTIONS;
import static ru.m210projects.Witchaven.WH2Names.FLOORMIRROR;
import static ru.m210projects.Witchaven.WHFX.FadeInit;
import static ru.m210projects.Witchaven.WHFX.initlava;
import static ru.m210projects.Witchaven.WHFX.initwater;
import static ru.m210projects.Witchaven.WHFX.weaponpowerup;
import static ru.m210projects.Witchaven.WHPLR.addhealth;
import static ru.m210projects.Witchaven.WHPLR.justteleported;
import static ru.m210projects.Witchaven.WHPLR.mapon;
import static ru.m210projects.Witchaven.WHPLR.player;
import static ru.m210projects.Witchaven.WHPLR.pyrn;
import static ru.m210projects.Witchaven.WHSND.sndInit;
import static ru.m210projects.Witchaven.WHScreen.initpaletteshifts;
import static ru.m210projects.Witchaven.WHTAG.delayitem;
import static ru.m210projects.Witchaven.WHTAG.swingdoor;
import static ru.m210projects.Witchaven.Whldsv.FindSaves;
import static ru.m210projects.Witchaven.Whmap.loadnewlevel;

import java.io.File;
import java.util.Arrays;
import java.util.Iterator;

import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.FileHandle.Compat.Path;
import ru.m210projects.Build.FileHandle.DirectoryEntry;
import ru.m210projects.Build.FileHandle.FileEntry;
import ru.m210projects.Build.FileHandle.Group;
import ru.m210projects.Build.FileHandle.GroupResource;
import ru.m210projects.Build.FileHandle.Resource;
import ru.m210projects.Build.FileHandle.UserGroup;
import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Build.OnSceenDisplay.OSDCOMMAND;
import ru.m210projects.Build.OnSceenDisplay.OSDCVARFUNC;
import ru.m210projects.Build.Pattern.BuildFactory;
import ru.m210projects.Build.Pattern.BuildGame;
import ru.m210projects.Build.Settings.BuildConfig;
import ru.m210projects.Build.Types.MemLog;
import ru.m210projects.Witchaven.Factory.WHEngine;
import ru.m210projects.Witchaven.Factory.WHFactory;
import ru.m210projects.Witchaven.Factory.WHMenuHandler;
import ru.m210projects.Witchaven.Menu.GameMenu;
import ru.m210projects.Witchaven.Menu.MainMenu;
import ru.m210projects.Witchaven.Menu.MenuCorruptGame;
import ru.m210projects.Witchaven.Screens.CutsceneScreen;
import ru.m210projects.Witchaven.Screens.GameScreen;
import ru.m210projects.Witchaven.Screens.LoadingScreen;
import ru.m210projects.Witchaven.Screens.MenuScreen;
import ru.m210projects.Witchaven.Screens.PrecacheScreen;
import ru.m210projects.Witchaven.Screens.StatisticsScreen;
import ru.m210projects.Witchaven.Screens.VictoryScreen;
import ru.m210projects.Witchaven.Types.Delayitem;
import ru.m210projects.Witchaven.Types.EpisodeInfo;
import ru.m210projects.Witchaven.Types.MapInfo;
import ru.m210projects.Witchaven.Types.PLAYER;
import ru.m210projects.Witchaven.Types.SWINGDOOR;

public class Main extends BuildGame {

	public static final String appdefwh1 = "whgdx.def";
	public static final String appdefwh2 = "wh2gdx.def";

	/*
	 * TODO:
	 */

	public static MenuScreen gMenuScreen;
	public static LoadingScreen gLoadScreen;
	public static GameScreen gGameScreen;
	public static VictoryScreen gVictoryScreen;
	public static CutsceneScreen gCutsceneScreen;
	public static PrecacheScreen gPrecacheScreen;
	public static StatisticsScreen gStatisticsScreen;

	public static enum UserFlag {
		None, UserMap, Addon
	};

	public static UserFlag mUserFlag = UserFlag.None;
	public static Main game;
	public static WHEngine engine;
	public static Config whcfg;
	public WHMenuHandler menu;

	public final boolean WH2;

	public Main(BuildConfig cfg, String appname, String sversion, boolean release, boolean isWH2) {
		super(cfg, appname, sversion, release);
		WH2 = isWH2;
		game = this;
		whcfg = (Config) cfg;
	}

	@Override
	public boolean init() throws Exception {
		engine.inittimer(TIMERRATE);

		Names.init();

		initlava();
		initwater();

		ConsoleInit();

		visibility=1024;

	    parallaxtype=1;

	    if(WH2) {
	    	engine.getTile(FLOORMIRROR).clear();

	    	for (int j = 0; j < 256; j++)
	            tempbuf[j] = (byte) ((j + 32) & 0xFF);     // remap colors for screwy palette sectors
	    	engine.makepalookup(16, tempbuf, 0, 0, 0, 1);
	    	for (int j = 0; j < 256; j++)
	    		tempbuf[j] = (byte) j;
	    	engine.makepalookup(17, tempbuf, 24, 24, 24, 1);

	    	for (int j = 0; j < 256; j++)
	    		tempbuf[j] = (byte) j;          // (j&31)+32;
	    	engine.makepalookup(18, tempbuf, 8, 8, 48, 1);
	    }

	    readpalettetable();

	    for(int i = 0; i < MAXSECTORS; i++)
	    	delayitem[i] = new Delayitem();

	    FadeInit();

		sndInit();

		initstruct();

		initpaletteshifts();
		InitOriginalEpisodes();

		Console.Println("Initializing def-scripts...");
		loadGdxDef(baseDef, game.WH2 ? appdefwh2 : appdefwh1, "whgdx.dat");

		if (pCfg.autoloadFolder) {
			Console.Println("Initializing autoload folder");
			DirectoryEntry autoload;
			if((autoload = BuildGdx.compat.checkDirectory("autoload")) != null) {
				for (Iterator<FileEntry> it = autoload.getFiles().values().iterator(); it.hasNext();) {
					FileEntry file = it.next();
					if (file.getExtension().equals("zip")) {
						Group group = BuildGdx.cache.add(file.getPath());
						if(group == null) continue;

						GroupResource def = group.open(WH2 ? appdefwh2 : appdefwh1);
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

		FileEntry filgdx = BuildGdx.compat.checkFile(WH2 ? appdefwh2 : appdefwh1);
		if(filgdx != null)
			baseDef.loadScript(filgdx);
		this.setDefs(baseDef);

		FindSaves();
		InitCutscenes();

		menu.mMenus[MAIN] = new MainMenu(this);
		menu.mMenus[GAME] = new GameMenu(this);
		menu.mMenus[CORRUPTLOAD] = new MenuCorruptGame(this);

		gMenuScreen = new MenuScreen(this);
		gLoadScreen = new LoadingScreen(this);
		gGameScreen = new GameScreen(this);
		gVictoryScreen = new VictoryScreen();
		gCutsceneScreen = new CutsceneScreen(this);
		gPrecacheScreen = new PrecacheScreen(this);
		gStatisticsScreen = new StatisticsScreen(this);

		System.gc();
		MemLog.log("create");

		return true;
	}

	private void InitOriginalEpisodes()
	{
		final String[] sMapName;

		if(WH2) {
			sMapName = new String[] {
				     "Antechamber of asmodeus",
				     "Halls of ragnoth",
				     "Lokis tomb",
				     "Forsaken realm",
				     "Eye of midian",
				     "Dungeon of disembowlment",
				     "Stronghold of chaos",
				     "Jaws of venom",
				     "Descent into doom",
				     "Hack n sniff",
				     "Straits of perdition",
				     "Plateau of insanity",
				     "Crypt of decay",
				     "Mausoleum of madness",
				     "Gateway into oblivion",
				     "Lungs of hell"
				};
		} else {
			sMapName = new String[48];
			 for(int i = 0; i < sMapName.length; i++)
				 sMapName[i] = "Map " + (i + 1);
		}

		gOriginalEpisode = new EpisodeInfo("Original");
		for(int i = 0; i < sMapName.length; i++) {
			gOriginalEpisode.gMapInfo.add(new MapInfo("level" + (i + 1) + ".map", sMapName[i]));
		}
	}

	public Runnable main = new Runnable() {
		@Override
		public void run() {
			changeScreen(gMenuScreen);
		}
	};

	private void initstruct() {
		for(int i = 0; i < MAXSWINGDOORS; i++)
			swingdoor[i] = new SWINGDOOR();

		initanimations();
	}

	@Override
	public BuildFactory getFactory() {
		return new WHFactory(this);
	}

	public void ConsoleInit()
	{
		Console.Println("Initializing on-screen display system", 0);

		Console.setVersion(appname + " " + sversion, 10, OSDTEXT_YELLOW);

		Console.RegisterCvar(new OSDCOMMAND("god",
		"", new OSDCVARFUNC() {
			@Override
			public void execute() {
				if (isCurrentScreen(gGameScreen)) {
					player[myconnectindex].godMode = !player[myconnectindex].godMode;
					if(player[myconnectindex].godMode)
						Console.Println("God mode: On");
					else Console.Println("God mode: Off");
				} else
					Console.Println("god: not in a single-player game");
			}
		}));

		Console.RegisterCvar(new OSDCOMMAND("noclip",
				"", new OSDCVARFUNC() {
					@Override
					public void execute() {
						if (isCurrentScreen(gGameScreen)) {
							player[myconnectindex].noclip = !player[myconnectindex].noclip;
							if(player[myconnectindex].noclip)
								Console.Println("Noclip mode: On");
							else Console.Println("Noclip mode: Off");
						} else
							Console.Println("Noclip: not in a single-player game");
					}
				}));

		Console.RegisterCvar(new OSDCOMMAND("showmap",
			"", new OSDCVARFUNC() {
				@Override
				public void execute() {
					if (isCurrentScreen(gGameScreen)) {
						Arrays.fill(show2dsector, (byte)255);
					} else
						Console.Println("showmap: not in a single-player game");
				}
			}));

		Console.RegisterCvar(new OSDCOMMAND("scooter",
			"", new OSDCVARFUNC() {
				@Override
				public void execute() {
					if (isCurrentScreen(gGameScreen)) {
						PLAYER plr = player[pyrn];
						plr.weapon[1]=1;plr.ammo[1]=45; //DAGGER
						plr.weapon[2]=1;plr.ammo[2]=55; //MORNINGSTAR
						plr.weapon[3]=1;plr.ammo[3]=50; //SHORT SWORD
						plr.weapon[4]=1;plr.ammo[4]=80; //BROAD SWORD
						plr.weapon[5]=1;plr.ammo[5]=100; //BATTLE AXE
						plr.weapon[6]=1;plr.ammo[6]=50; // BOW
						plr.weapon[7]=2;plr.ammo[7]=40; //PIKE
						plr.weapon[8]=1;plr.ammo[8]=250; //TWO HANDED
						plr.weapon[9]=1;plr.ammo[9]=50;
						plr.currweapon=plr.selectedgun=4;
					} else
						Console.Println("scooter: not in a single-player game");
				}
			}));

		Console.RegisterCvar(new OSDCOMMAND("mommy",
			"", new OSDCVARFUNC() {
				@Override
				public void execute() {
					if (isCurrentScreen(gGameScreen)) {
						PLAYER plr = player[pyrn];
						for(int i=0;i<MAXPOTIONS;i++)
							plr.potion[i]=9;
					} else
						Console.Println("mommy: not in a single-player game");
				}
			}));
		Console.RegisterCvar(new OSDCOMMAND("wango",
			"", new OSDCVARFUNC() {
				@Override
				public void execute() {
					if (isCurrentScreen(gGameScreen)) {
						PLAYER plr = player[pyrn];
						for(int i=0;i<8;i++) {
							plr.orb[i]=1;
							plr.orbammo[i]=9;
						}
						plr.health=0;
						addhealth(plr, 200);
						plr.armor=150;
						plr.armortype=3;
						plr.lvl=7;
						plr.maxhealth=200;
						plr.treasure[TBRASSKEY]=1;
						plr.treasure[TBLACKKEY]=1;
						plr.treasure[TGLASSKEY]=1;
						plr.treasure[TIVORYKEY]=1;
					} else
						Console.Println("wango: not in a single-player game");
				}
			}));

		Console.RegisterCvar(new OSDCOMMAND("powerup",
				"", new OSDCVARFUNC() {
					@Override
					public void execute() {
						if (isCurrentScreen(gGameScreen)) {
							PLAYER plr = player[pyrn];
							weaponpowerup(plr);
						} else
							Console.Println("powerup: not in a single-player game");
					}
				}));

		Console.RegisterCvar(new OSDCOMMAND("changemap",
			"", mapon, new OSDCVARFUNC() {
				@Override
				public void execute() {
					if (isCurrentScreen(gGameScreen)) {
						justteleported=true;
						mapon = Integer.parseInt(osd_argv[1]);
						loadnewlevel(mapon);

					} else
						Console.Println("changemap: not in a single-player game");
				}
			}, 2, 25));
	}

	public void readpalettetable() {
	    int num_tables,lookup_num;

	    Resource fp;
		if ((fp = BuildGdx.cache.open("lookup.dat", 0)) != null) {
			num_tables = fp.readByte() & 0xFF;
			for (int j = 0; j < num_tables; j++) {
				lookup_num = fp.readByte() & 0xFF;
				fp.read(tempbuf, 0, 256);
				engine.makepalookup(lookup_num, tempbuf, 0, 0, 0, 1);
			}
			fp.close();
		}
	}

	private void InitCutscenes()
	{
		Console.Println("Initializing cutscenes");
		UserGroup group = BuildGdx.cache.add("Cutscenes", false);

		DirectoryEntry[] paths = new DirectoryEntry[2];

		paths[0] = BuildGdx.compat.getDirectory(Path.Game);
		paths[1] = BuildGdx.compat.checkDirectory("SMK");

		for(DirectoryEntry dir : paths)
		{
			if(dir != null) {
				for (Iterator<FileEntry> it = dir.getFiles().values().iterator(); it.hasNext();) {
					FileEntry file = it.next();
					if(file.getExtension().equals("smk")) {
						File f = file.getFile();
						if(f != null && !group.contains(file.getName()))
							group.add(file.getName(), -1).absolutePath = f.getAbsolutePath();
					}
				}
			}
		}

		if(group.numfiles == 0)
			BuildGdx.cache.remove(group);
	}

	@Override
	public void show() {
		if (gCutsceneScreen.init("intro.smk"))
			changeScreen(gCutsceneScreen.setSkipping(main).escSkipping(true));
		else
			changeScreen(gMenuScreen);
	}

	@Override
	protected byte[] reportData() {
		String report = "boardfilename " + boardfilename;

		report += "\r\n";
		if (player[0] != null) {
			report += "PlayerX " + player[0].x;
			report += "\r\n";
			report += "PlayerY " + player[0].y;
			report += "\r\n";
			report += "PlayerZ " + player[0].z;
			report += "\r\n";
		}

		return report.getBytes();
	}

}

