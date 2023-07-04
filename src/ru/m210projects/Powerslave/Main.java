// This file is part of PowerslaveGDX.
// Copyright (C) 2019  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// PowerslaveGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// PowerslaveGDX is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with PowerslaveGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.Powerslave;

import static ru.m210projects.Build.Gameutils.*;
import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Build.Net.Mmulti.*;
import static ru.m210projects.Build.OnSceenDisplay.Console.OSDTEXT_GOLD;
import static ru.m210projects.Build.OnSceenDisplay.Console.osd_argv;
import static ru.m210projects.Powerslave.Player.*;
import static ru.m210projects.Powerslave.LoadSave.*;
import static ru.m210projects.Powerslave.Map.bMapCrash;
import static ru.m210projects.Powerslave.Menus.PSMenuUserContent.resetEpisodeResources;
import static ru.m210projects.Powerslave.Random.InitRandom;
import static ru.m210projects.Powerslave.Globals.*;
import static ru.m210projects.Powerslave.Palette.GrabPalette;
import static ru.m210projects.Powerslave.Sound.*;
import static ru.m210projects.Powerslave.Factory.PSMenuHandler.*;

import java.util.Arrays;
import java.util.Iterator;

import com.badlogic.gdx.utils.ByteArray;

import static ru.m210projects.Powerslave.Seq.*;

import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.FileHandle.DirectoryEntry;
import ru.m210projects.Build.FileHandle.FileEntry;
import ru.m210projects.Build.FileHandle.Group;
import ru.m210projects.Build.FileHandle.GroupResource;
import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Build.OnSceenDisplay.OSDCOMMAND;
import ru.m210projects.Build.OnSceenDisplay.OSDCVARFUNC;
import ru.m210projects.Build.Pattern.BuildFactory;
import ru.m210projects.Build.Pattern.BuildGame;
import ru.m210projects.Build.Render.GLRenderer.GLInvalidateFlag;
import ru.m210projects.Build.Settings.BuildConfig;
import ru.m210projects.Build.Settings.BuildSettings;
import ru.m210projects.Build.Types.MemLog;
import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Powerslave.Factory.PSEngine;
import ru.m210projects.Powerslave.Factory.PSFactory;
import ru.m210projects.Powerslave.Factory.PSMenuHandler;
import ru.m210projects.Powerslave.Menus.MainMenu;
import ru.m210projects.Powerslave.Menus.MenuCorruptGame;
import ru.m210projects.Powerslave.Menus.MenuGame;
import ru.m210projects.Powerslave.Menus.NewAddon;
import ru.m210projects.Powerslave.Screens.CinemaScreen;
import ru.m210projects.Powerslave.Screens.DemoScreen;
import ru.m210projects.Powerslave.Screens.GameScreen;
import ru.m210projects.Powerslave.Screens.LoadingScreen;
import ru.m210projects.Powerslave.Screens.LogoScreen2;
import ru.m210projects.Powerslave.Screens.MapScreen;
import ru.m210projects.Powerslave.Screens.MenuScreen;
import ru.m210projects.Powerslave.Screens.MovieScreen;
import ru.m210projects.Powerslave.Screens.PSLogoScreen;
import ru.m210projects.Powerslave.Screens.PrecacheScreen;
import ru.m210projects.Powerslave.Type.EpisodeInfo;
import ru.m210projects.Powerslave.Type.Input;
import ru.m210projects.Powerslave.Type.MapInfo;
import ru.m210projects.Powerslave.Type.PlayerSave;
import ru.m210projects.Powerslave.Type.PlayerStruct;

public class Main extends BuildGame {

	/*
	 * Changelog:
	 * Powerup timer indicator as BloodGDX and WHGDX
	 *
	 * TODO:
	 * ru.m210projects.Powerslave.Enemies.LavaDude.FuncLavaLimb(LavaDude.java:375) FatalError: RunPtr>=0 && RunPtr<MAXRUN: -1
	 * other autoaim method
	 * The enemies that throw blue fireballs don't attack when I'm underwater
	 */

	public static final String appdef = "psgdx.def";

	public static Main game;
	public static PSEngine engine;
	public static Config cfg;
	public PSMenuHandler menu;

	public static LoadingScreen gLoadingScreen;
	public static GameScreen gGameScreen;
	public static DemoScreen gDemoScreen;
	public static MapScreen gMap;
	public static PSLogoScreen gLogoScreen;
	public static LogoScreen2 gLogoScreen2;
	public static MovieScreen gMovieScreen;
	public static MenuScreen gMenuScreen;
	public static PrecacheScreen gPrecacheScreen;
	public static CinemaScreen gCinemaScreen;

	private final int LOGO1 = 3349; //3368

	public static enum UserFlag {
		None, UserMap, Addon
	};

	public Main(BuildConfig bcfg, String appname, String sversion, boolean isRelease) {
		super(bcfg, appname, sversion, isRelease);
		game = this;
		cfg = (Config) bcfg;
	}

	public Main(BuildConfig bcfg, String appname, String sversion) {
		this(bcfg, appname, sversion, true);
	}

	@Override
	public BuildFactory getFactory() {
		return new PSFactory(this);
	}

	@Override
	public boolean init() throws Exception {
		Console.Println("Initializing timer");
		engine.inittimer(120);

		ConsoleInit();

		for (int i = 0; i < 8; i++) {
			PlayerList[i] = new PlayerStruct();
			sPlayerSave[i] = new PlayerSave();
			sPlayerInput[i] = new Input();
		}
		sndInit();
		InitRandom();
		LoadFX();
		LoadSequences();
		InitOriginalEpisodes();

		byte[] remapbuf = new byte[256];
		for (int i = 0; i < 255; i++)
			remapbuf[i] = (byte) (99 + (i & 8)); // gray palette #20
		engine.makepalookup(20, remapbuf, 0, 0, 0, 1);
		GrabPalette();

		nItemTextIndex = FindGString("ITEMS");

		Arrays.fill(nPlayerLives, (short) 3);
		nBestLevel = 0;
		game.pNet.ResetTimers();

		Console.Println("Initializing def-scripts...");
		loadGdxDef(baseDef, appdef, "psgdx.dat");

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

		FindSaves();
		searchCDtracks();

		FileEntry filgdx = BuildGdx.compat.checkFile(appdef);
		if (filgdx != null)
			baseDef.loadScript(filgdx);
		this.setDefs(baseDef);

		menu.mMenus[MAIN] = new MainMenu(this);
		menu.mMenus[GAME] = new MenuGame(this);
		menu.mMenus[ADDON] = new NewAddon(this);
		menu.mMenus[CORRUPTLOAD] = new MenuCorruptGame(this);

		gLoadingScreen = new LoadingScreen(this);
		gGameScreen = new GameScreen(this);
		gDemoScreen = new DemoScreen(this);
		gMap = new MapScreen(this);
		gLogoScreen = new PSLogoScreen(this);
		gLogoScreen2 = new LogoScreen2(this, 1.0f);
		gMovieScreen = new MovieScreen(this);
		gMenuScreen = new MenuScreen(this);
		gPrecacheScreen = new PrecacheScreen(this);
		gCinemaScreen = new CinemaScreen(this);

		gDemoScreen.demoscan();

		System.gc();
		MemLog.log("create");
		return true;
	}

	private void InitOriginalEpisodes()
	{
		gTrainingEpisode = new EpisodeInfo("Training");
		gTrainingEpisode.gMapInfo.add(new MapInfo("lev0.map", "Training"));

		final String sMapName[] = { "Abu Simbel", "Dendur", "Kalabsh", "El Subua", "El Derr",
				"Abu Ghurab", "Philae", "El Kab", "Aswan", "Set Boss", "Qubbet el Hawa", "Abydos", "Edufu", "West Bank",
				"Luxor", "Karnak", "Saqqara", "Mittrahn", "Kilmaatikahn Boss", "Alien Mothership" };

		gOriginalEpisode = new EpisodeInfo("Original");
		for(int i = 0; i < 20; i++) {
			gOriginalEpisode.gMapInfo.add(new MapInfo("lev" + (i + 1) + ".map", sMapName[i]));
		}
	}

	private void ConsoleInit()
	{
		Console.Println("Initializing on-screen display system");
		Console.setParameters(0, 0, 0, 0, 0, 0);
		Console.setVersion(appname + " " + sversion, 10, OSDTEXT_GOLD);

		Console.RegisterCvar(new OSDCOMMAND("changemap",
			"", levelnum, new OSDCVARFUNC() {
				@Override
				public void execute() {
					if (isCurrentScreen(gGameScreen)) {
						levelnew = Integer.parseInt(osd_argv[1]);
						Console.toggle();
					} else
						Console.Println("changemap: not in a single-player game");
				}
			}, 0, 20));
	}

	public void resetState() {
		menu.mClose();
		menu.mOpen(menu.mMenus[MAIN], -1);

		resetEpisodeResources(gOriginalEpisode);

		gAutosaveRequest = false;
		bMapCrash = false;
		levelnum = 0;
		levelnew = 0;
		nBestLevel = -1;
		mUserFlag = UserFlag.None;
		boardfilename = null;
		lastload = null;

		StopAllSounds();
		if(MusicPlaying() && currTrack != 19)
			StopMusic();

		pInput.ctrlResetKeyStatus();
	}

	public Runnable rMenu = new Runnable() {
		@Override
		public void run() {
			resetState();

			engine.setbrightness(BuildSettings.paletteGamma.get(), palette, GLInvalidateFlag.All);

			if (numplayers > 1 || gDemoScreen.demofiles.size() == 0 || cfg.gDemoSeq == 0 || !gDemoScreen.showDemo())
				changeScreen(gMenuScreen);
		}
	};

	public Runnable toLogo3 = new Runnable() {
		@Override
		public void run() {
			game.changeScreen(gLogoScreen2.setCallback(rMenu));
		}
	};

	public Runnable logo = new Runnable() {
		Runnable cutscene = new Runnable() {
			@Override
			public void run() {
				if (gMovieScreen.init("book.mov"))
					game.changeScreen(gMovieScreen.setCallback(toLogo3).escSkipping(true));
				else
					game.changeScreen(gCinemaScreen.setNum(2).setSkipping(toLogo3));
			}
		};

		@Override
		public void run() {
			changeScreen(gLogoScreen.setTime(2.0f).setTile(GetSeqPicnum(59, 0, 0)).setCallback(cutscene).setSkipping(toLogo3));
		}
	};

	public int FindGString(String string) {
		for (int i = 0;; i++) {
			if (gString[i].equals(string))
				return i + 1;
			if (gString[i].equals("EOF"))
				break;
		}
		return -1;
	}

	@Override
	public void show() {
		changeScreen(gLogoScreen.setTime(2.0f).setTile(LOGO1).setCallback(logo).setSkipping(toLogo3));
	}

	@Override
	protected byte[] reportData() {
		byte[] out = null;

		String report = "Mapname: " + boardfilename;

		report += "\r\n";
		report += "UserFlag: " + mUserFlag;
		report += "\r\n";

		if (PlayerList[myconnectindex] != null && isValidSprite(PlayerList[myconnectindex].spriteId)) {
			SPRITE pSprite = sprite[PlayerList[myconnectindex].spriteId];
			report += "PlayerX " + pSprite.x;
			report += "\r\n";
			report += "PlayerY " + pSprite.y;
			report += "\r\n";
			report += "PlayerZ " + pSprite.z;
			report += "\r\n";
			report += "PlayerAng " + pSprite.ang;
			report += "\r\n";
			report += "PlayerSect: " + pSprite.sectnum;
			report += "\r\n";
		}

		if (mUserFlag == UserFlag.UserMap) {
			ByteArray array = new ByteArray();
			byte[] data = BuildGdx.cache.getBytes(boardfilename, 0);
			if (data != null) {
				report += "\r\n<------Start Map data------->\r\n";
				array.addAll(report.getBytes());
				array.addAll(data);
				array.addAll("\r\n<------End Map data------->\r\n".getBytes());

				out = Arrays.copyOf(array.items, array.size);
			}
		} else out = report.getBytes();

		return out;
	}

	public void EndGame() {
		StopAllSounds();
		StopMusic();
		changeScreen(gLogoScreen.setTime(2.0f).setTile(LOGO1).setCallback(logo).setSkipping(toLogo3));
	}

}
