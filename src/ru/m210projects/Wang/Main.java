package ru.m210projects.Wang;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.utils.ByteArray;
import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.Architecture.BuildMessage.MessageType;
import ru.m210projects.Build.FileHandle.*;
import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Build.Pattern.BuildFactory;
import ru.m210projects.Build.Pattern.BuildGame;
import ru.m210projects.Build.Pattern.ScreenAdapters.GameAdapter;
import ru.m210projects.Build.Settings.BuildConfig;
import ru.m210projects.Wang.Factory.*;
import ru.m210projects.Wang.Factory.WangNetwork.MultiGameTypes;
import ru.m210projects.Wang.Screens.*;
import ru.m210projects.Wang.Type.VoxelScript;

import java.util.Arrays;
import java.util.Iterator;

import static ru.m210projects.Build.Gameutils.loadGdxDef;
import static ru.m210projects.Build.Net.Mmulti.myconnectindex;
import static ru.m210projects.Build.Net.Mmulti.numplayers;
import static ru.m210projects.Build.OnSceenDisplay.Console.OSDTEXT_RED;
import static ru.m210projects.Wang.Enemies.Bunny.InitBunnyStates;
import static ru.m210projects.Wang.Enemies.Coolg.InitCoolgStates;
import static ru.m210projects.Wang.Enemies.Coolie.InitCoolieStates;
import static ru.m210projects.Wang.Enemies.Eel.InitEelStates;
import static ru.m210projects.Wang.Enemies.GirlNinj.InitGNinjaStates;
import static ru.m210projects.Wang.Enemies.Goro.InitGoroStates;
import static ru.m210projects.Wang.Enemies.Hornet.InitHornetStates;
import static ru.m210projects.Wang.Enemies.Lava.InitLavaStates;
import static ru.m210projects.Wang.Enemies.Ninja.InitNinjaStates;
import static ru.m210projects.Wang.Enemies.Ripper.InitRipperStates;
import static ru.m210projects.Wang.Enemies.Ripper2.InitRipper2States;
import static ru.m210projects.Wang.Enemies.Serp.InitSerpStates;
import static ru.m210projects.Wang.Enemies.Skel.InitSkelStates;
import static ru.m210projects.Wang.Enemies.Skull.InitSkullStates;
import static ru.m210projects.Wang.Enemies.Sumo.InitSumoStates;
import static ru.m210projects.Wang.Enemies.Zilla.InitZillaStates;
import static ru.m210projects.Wang.Enemies.Zombie.InitZombieStates;
import static ru.m210projects.Wang.Factory.WangMenuHandler.MAIN;
import static ru.m210projects.Wang.Factory.WangNetwork.CommPlayers;
import static ru.m210projects.Wang.Game.Player;
import static ru.m210projects.Wang.Game.*;
import static ru.m210projects.Wang.JSector.InitJSectorStructs;
import static ru.m210projects.Wang.JWeapon.InitJWeaponStates;
import static ru.m210projects.Wang.LoadSave.FindSaves;
import static ru.m210projects.Wang.MiscActr.InitMiscStates;
import static ru.m210projects.Wang.Player.InitPlayerStates;
import static ru.m210projects.Wang.Sector.InitSectorStructs;
import static ru.m210projects.Wang.Sound.*;
import static ru.m210projects.Wang.Sprites.*;
import static ru.m210projects.Wang.Type.ResourceHandler.resetEpisodeResources;
import static ru.m210projects.Wang.Type.ResourceHandler.usecustomarts;
import static ru.m210projects.Wang.Type.Saveable.InitSaveable;
import static ru.m210projects.Wang.Weapon.InitWeaponStates;

public class Main extends BuildGame {

	/*
	 * TODO:
	 * turn off the Lo Wang dialogue option
	 * increase vehicle turn speed with joystick and mouse
	 * demo sreenpeek
	 * pause affect to demo
	 * sync demo (and MP)
	 * slopetilt affect
	 *
	 *
	 * лифт не опускает трупы
	 */

	public static boolean NETTEST = false;
	public static boolean AUTOCOMPARE = false;

	public static final String appdef = "swgdx.def";

	public static Main game;
	public static WangEngine engine;
	public static Config gs;
	public static WangNetwork gNet;
	public WangMenuHandler menu;
	public WangNetwork net;
	public String mainGrp = "sw.grp";

	public WangInterpolation pIntSkip2;
	public WangInterpolation pIntSkip4;

	public static LoadingScreen gLoadingScreen;
	public static GameScreen gGameScreen;
	public static DemoScreen gDemoScreen;
	public static LogoAScreen gLogoScreen;
	public static AnmScreen gAnmScreen;
	public static MenuScreen gMenuScreen;
	public static StatisticScreen gStatisticScreen;
	public static PrecacheScreen gPrecacheScreen;
	public static NetScreen gNetScreen;
	public static DisconnectScreen gDisconnectScreen;
	public static CreditsScreen gCreditsScreen;

	public static boolean kGameCrash;

	public static enum UserFlag {
		None, UserMap, Addon
	};

	public static UserFlag mUserFlag = UserFlag.None;

	public Main(BuildConfig bcfg, String appname, String sversion, boolean isRelease) {
		super(bcfg, appname, sversion, isRelease);
		game = this;
		gs = (Config) bcfg;

		pIntSkip2 = new WangInterpolation() {
			@Override
			public int getSkipValue() {
				return MoveSkip2 ? 1 : 0;
			}

			@Override
			public int getSkipMax() {
				return 2;
			}
		};

		pIntSkip4 = new WangInterpolation() {
			@Override
			public int getSkipValue() {
				return MoveSkip4;
			}

			@Override
			public int getSkipMax() {
				return 4;
			}
		};
	}

	public Main(BuildConfig bcfg, String appname, String sversion) {
		this(bcfg, appname, sversion, true);
	}

	@Override
	public BuildFactory getFactory() {
		return new WangFactory(this);
	}

	@Override
	public boolean init() throws Exception {
		InitGame();

		InitPlayerStates();
		InitWeaponStates();
		InitJWeaponStates();
		InitSprStates();
		InitNinjaStates();
		InitZombieStates();
		InitRipper2States();
		InitBunnyStates();
		InitGoroStates();
		InitRipperStates();
		InitMiscStates();
		InitSerpStates();
		InitSumoStates();
		InitZillaStates();
		InitGNinjaStates();
		InitCoolgStates();
		InitCoolieStates();
		InitSkullStates();
		InitSkelStates();
		InitLavaStates();
		InitEelStates();
		InitHornetStates();

		InitJSectorStructs();
		InitSectorStructs();

		InitSaveable();

		Console.Println("Initializing def-scripts...");
		loadGdxDef(baseDef, appdef, "swgdx.dat");

		if (gs.autoloadFolder) {
			Console.Println("Initializing autoload folder");
			DirectoryEntry autoload;
			if ((autoload = BuildGdx.compat.checkDirectory("autoload")) != null) {
				for (Iterator<FileEntry> it = autoload.getFiles().values().iterator(); it.hasNext();) {
					FileEntry file = it.next();
					if (file.getExtension().equals("zip")) {
						Group group = BuildGdx.cache.add(file.getPath());
						if (group == null)
							continue;

						GroupResource def = group.open(appdef);
						if (def != null) {
							baseDef.loadScript(file.getName(), def.getBytes());
							def.close();
						}
					} else if (file.getExtension().equals("def"))
						baseDef.loadScript(file);
				}
			}
		}

		FileEntry filgdx = BuildGdx.compat.checkFile(appdef);
		if (filgdx != null)
			baseDef.loadScript(filgdx);

		Resource kvxres;
		if ((kvxres = BuildGdx.cache.open("swvoxfil.txt", 0)) != null) {
			VoxelScript vox = new VoxelScript(kvxres);
			vox.apply(baseDef);
			kvxres.close();
		}

		this.setDefs(baseDef);

		FindSaves();
		InitCDtracks();

		game.pNet.ResetTimers();

		gLoadingScreen = new LoadingScreen(this);
		gGameScreen = new GameScreen(this);
		gDemoScreen = new DemoScreen(this);
		gLogoScreen = new LogoAScreen(this, 5.0f);
		gAnmScreen = new AnmScreen(this);
		gMenuScreen = new MenuScreen(this);
		gStatisticScreen = new StatisticScreen(this);
		gPrecacheScreen = new PrecacheScreen(this);
		gNetScreen = new NetScreen(this);
		gDisconnectScreen = new DisconnectScreen(this);
		gCreditsScreen = new CreditsScreen(this);

//		gDemoScreen.demoscan();

		return true;
	}

	public Runnable rMenu = new Runnable() {
		@Override
		public void run() {
			if(game.getScreen() != gAnmScreen || gAnmScreen.getAnim() != ANIM_INTRO)
				StopFX();
			CDAudio_Play(2, true);
			Level = 0;

			nNetMode = NetMode.Single;
			CommPlayers = 1;
			gNet.MultiGameType = MultiGameTypes.MULTI_GAME_NONE;
			gNet.FakeMultiplayer = false;

			if (!menu.gShowMenu)
				menu.mOpen(menu.mMenus[MAIN], -1);

			if (numplayers > 1 || gDemoScreen.demofiles.size() == 0 || gs.gDemoSeq == 0 || !gDemoScreen.showDemo())
				changeScreen(gMenuScreen);
		}
	};

	public void MainMenu() {
		rMenu.run();
	}

	@Override
	public void show() {
		if(rec != null)
			rec.close();

		kGameCrash = false;
		if (usecustomarts)
			resetEpisodeResources();

		changeScreen(gLogoScreen.setCallback(new Runnable() {
			@Override
			public void run() {
				if (gAnmScreen.init(0)) {
					changeScreen(gAnmScreen.setCallback(rMenu).escSkipping(false));
				} else
					MainMenu();
			}
		}));
	}

	public void Disconnect() {
		if(rec != null)
			rec.close();

		changeScreen(gDisconnectScreen);
	}

	@Override
	public void dispose() {
		if (rec != null)
			rec.close();
		super.dispose();
	}

	public void GameCrash(String errorText) {
		BuildGdx.message.show("Error: ", errorText, MessageType.Info);
		Console.Println("Game error: " + errorText, OSDTEXT_RED);

		kGameCrash = true;
	}

	public void dassert(String msg) {
		if (kGameCrash)
			return;

		ThrowError(msg);
		System.exit(0);
	}

	@Override
	protected byte[] reportData() {
		byte[] out = null;

		String text = "Mapname: " + gGameScreen.getTitle();
		text += "\r\n";
		text += "UserFlag: " + mUserFlag;
		text += "\r\n";

		if (mUserFlag == UserFlag.Addon && currentGame != null) {
			try {
				FileEntry addon = currentGame.getFile();
				if (addon != null && currentGame.isPackage())
					text += "Episode filename: " + addon.getPath();
				else
					text += "Episode filename: " + currentGame.getDirectory().getAbsolutePath();
				text += "\r\n";
			} catch (Exception e) {
				text += "Episode filename get error \r\n";
			}
		}

		text += "level " + Level;
		text += "\r\n";
		text += "nDifficulty: " + Skill;
		text += "\r\n";

		if (Player != null) {
			text += "PlayerX: " + Player[myconnectindex].posx;
			text += "\r\n";
			text += "PlayerY: " + Player[myconnectindex].posy;
			text += "\r\n";
			text += "PlayerZ: " + Player[myconnectindex].posz;
			text += "\r\n";
			text += "PlayerAng: " + Player[myconnectindex].getAnglef();
			text += "\r\n";
			text += "PlayerHoriz: " + Player[myconnectindex].horiz;
			text += "\r\n";
			text += "PlayerSect: " + Player[myconnectindex].cursectnum;
			text += "\r\n";
		}

		if (mUserFlag == UserFlag.UserMap && boardfilename != null) {
			ByteArray array = new ByteArray();
			byte[] data = BuildGdx.cache.getBytes(boardfilename, 0);
			if (data != null) {
				text += "\r\n<------Start Map data------->\r\n";
				array.addAll(text.getBytes());
				array.addAll(data);
				array.addAll("\r\n<------End Map data------->\r\n".getBytes());

				out = Arrays.copyOf(array.items, array.size);
			}
		} else
			out = text.getBytes();

		return out;
	}

	public boolean isScreenSaving() {
		Screen scr = this.getScreen();
		if (scr instanceof GameAdapter)
			return ((GameAdapter) scr).isScreenSaving();
		return false;
	}
}
