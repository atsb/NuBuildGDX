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

import static ru.m210projects.LSP.Sounds.sndInit;
import static ru.m210projects.Build.Engine.MAXPLAYERS;
import static ru.m210projects.Build.Gameutils.loadGdxDef;
import static ru.m210projects.Build.OnSceenDisplay.Console.OSDTEXT_GOLD;
import static ru.m210projects.Build.OnSceenDisplay.Console.osd_argv;
import static ru.m210projects.Build.Net.Mmulti.myconnectindex;
import static ru.m210projects.Build.Net.Mmulti.numplayers;
import static ru.m210projects.LSP.Animate.initanimations;
import static ru.m210projects.LSP.Quotes.*;
import static ru.m210projects.LSP.Globals.*;
import static ru.m210projects.LSP.LoadSave.*;
import static ru.m210projects.LSP.Factory.LSPMenuHandler.*;

import java.util.Iterator;

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
import ru.m210projects.Build.Settings.BuildConfig;
import ru.m210projects.Build.Types.MemLog;
import ru.m210projects.LSP.Factory.LSPEngine;
import ru.m210projects.LSP.Factory.LSPFactory;
import ru.m210projects.LSP.Factory.LSPMenuHandler;
import ru.m210projects.LSP.Menus.AdvertisingMenu;
import ru.m210projects.LSP.Menus.GameMenu;
import ru.m210projects.LSP.Menus.LSPMenuAudio;
import ru.m210projects.LSP.Menus.LSPMenuLoad;
import ru.m210projects.LSP.Menus.LSPMenuSave;
import ru.m210projects.LSP.Menus.MainMenu;
import ru.m210projects.LSP.Menus.MenuCorruptGame;
import ru.m210projects.LSP.Menus.MenuLastLoad;
import ru.m210projects.LSP.Menus.MenuOptions;
import ru.m210projects.LSP.Menus.MenuQuit;
import ru.m210projects.LSP.Screens.DemoScreen;
import ru.m210projects.LSP.Screens.GameScreen;
import ru.m210projects.LSP.Screens.IntroScreen;
import ru.m210projects.LSP.Screens.LoadingScreen;
import ru.m210projects.LSP.Screens.MenuScreen;
import ru.m210projects.LSP.Screens.MovieScreen;
import ru.m210projects.LSP.Screens.PrecacheScreen;
import ru.m210projects.LSP.Screens.StatisticScreen;
import ru.m210projects.LSP.Types.LSPGroup;
import ru.m210projects.LSP.Types.PlayerStruct;
import ru.m210projects.LSP.Types.SwingDoor;

public class Main extends BuildGame {
	
	/*
	 * TODO:
	 */

	public static final String appdef = "LSPGDX.def";

	public static Main game;
	public static LSPEngine engine;
	public static Config cfg;
	public LSPMenuHandler menu;

	public static LoadingScreen gLoadingScreen;
	public static GameScreen gGameScreen;
	public static MovieScreen gMovieScreen;
	public static IntroScreen gIntroScreen;
	public static MenuScreen gMenuScreen;
	public static PrecacheScreen gPrecacheScreen;
	public static StatisticScreen gStatisticScreen;
	public static DemoScreen gDemoScreen;

	public static LSPGroup gMapGroup;
	public static LSPGroup gResGroup;

	public static enum UserFlag {
		None, UserMap
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
		return new LSPFactory(this);
	}

	public Runnable toMenu = new Runnable() {
		@Override
		public void run() {
			if (numplayers > 1 || gDemoScreen.demofiles.size() == 0 || /*cfg.gDemoSeq == 0 ||*/ !gDemoScreen.showDemo())
				changeScreen(gMenuScreen);
		}
	};

	@Override
	public boolean init() throws Exception {
		Console.Println("Initializing timer");
		engine.inittimer(120);

		ConsoleInit();
		
		sndInit();

		for (int i = 0; i < MAXPLAYERS; i++)
			gPlayer[i] = new PlayerStruct();
		for (int i = 0; i < MAXSWINGDOORS; i++)
			swingdoor[i] = new SwingDoor();
		initanimations();

		InitQuotes();

		game.pNet.ResetTimers();
		Console.Println("Initializing def-scripts...");
		loadGdxDef(baseDef, appdef, "lspgdx.dat");
		
		if (pCfg.autoloadFolder) {
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
		if (filgdx != null)
			baseDef.loadScript(filgdx);
		this.setDefs(baseDef);

		FindSaves();

		menu.mMenus[LOADGAME] = new LSPMenuLoad(this);
		menu.mMenus[SAVEGAME] = new LSPMenuSave(this);
		menu.mMenus[AUDIOSET] = new LSPMenuAudio(this);
		menu.mMenus[OPTIONS] = new MenuOptions(this);
		menu.mMenus[QUIT] = new MenuQuit(this);

		menu.mMenus[GAME] = new GameMenu(this);
		menu.mMenus[MAIN] = new MainMenu(this);
		menu.mMenus[ADVERTISING] = new AdvertisingMenu();
		menu.mMenus[LASTSAVE] = new MenuLastLoad(this);
		menu.mMenus[CORRUPTLOAD] = new MenuCorruptGame(this);

		gLoadingScreen = new LoadingScreen(this);
		gGameScreen = new GameScreen(this);
		gMovieScreen = new MovieScreen(this);
		gIntroScreen = new IntroScreen(this);
		gMenuScreen = new MenuScreen(this);
		gPrecacheScreen = new PrecacheScreen(this);
		gStatisticScreen = new StatisticScreen(this);
		gDemoScreen = new DemoScreen(this);
		gDemoScreen.demoscan();
		
		System.gc();
		MemLog.log("create");

		return true;
	}

	private void ConsoleInit() {
		Console.Println("Initializing on-screen display system");
		Console.setParameters(0, 0, 0, 0, 0, 0);
		Console.setVersion(appname + " " + sversion, 10, OSDTEXT_GOLD);
		
		Console.RegisterCvar(new OSDCOMMAND("noclip", "noclip", new OSDCVARFUNC() {
			@Override
			public void execute() {
				if (isCurrentScreen(gGameScreen)) {
					gPlayer[myconnectindex].noclip = !gPlayer[myconnectindex].noclip;
					Console.Println("Noclip: " + (gPlayer[myconnectindex].noclip ? "ON" : "OFF"));
				} else Console.Println("noclip: not in a game");
			}
		}));
		
		Console.RegisterCvar(new OSDCOMMAND("god", "god", new OSDCVARFUNC() {
			@Override
			public void execute() {
				if (isCurrentScreen(gGameScreen)) {
					if(gPlayer[myconnectindex].nPlayerStatus != 5)
						gPlayer[myconnectindex].nPlayerStatus = 5;
					else gPlayer[myconnectindex].nPlayerStatus = 0;
					Console.Println("God mode: " + (gPlayer[myconnectindex].nPlayerStatus == 5 ? "ON" : "OFF"));
				} else Console.Println("god: not in a game");
			}
		}));

		Console.RegisterCvar(new OSDCOMMAND("changemap", "changemap: map number", new OSDCVARFUNC() {
			@Override
			public void execute() {
				if (isCurrentScreen(gGameScreen)) {
					if (Console.osd_argc != 2) {
						Console.Println("changemap: map number");
						return;
					}
					
					try {
						int num = Integer.parseInt(osd_argv[1]);
						gGameScreen.changemap(num);
					} catch(Exception e) { Console.Println("type out of range"); }	
				} else {
					Console.Println("changemap: not in a game");
				}
			}
		}));
		
		Console.RegisterCvar(new OSDCOMMAND("recordemo", "recordemo: map number", new OSDCVARFUNC() {
			@Override
			public void execute() {
				if (Console.osd_argc != 2) {
					Console.Println("recordemo: map number");
					return;
				}
				
				try {
					int num = Integer.parseInt(osd_argv[1]);
					m_recstat = 1;
					gGameScreen.newgame(num);
				} catch(Exception e) { Console.Println("type out of range"); }	
			}
		}));
		
		Console.RegisterCvar(new OSDCOMMAND("savemap", "savemap: map name", new OSDCVARFUNC() {
			@Override
			public void execute() {
				if (isCurrentScreen(gGameScreen)) {
					if (Console.osd_argc != 2) {
						Console.Println("savemap: map name with .map");
						return;
					}
					
					try {
						String name = osd_argv[1];
						ru.m210projects.Build.FileHandle.FileResource fil = BuildGdx.compat.open(name, ru.m210projects.Build.FileHandle.Compat.Path.Game, ru.m210projects.Build.FileHandle.FileResource.Mode.Write);
						PlayerStruct pos = gPlayer[myconnectindex];
						engine.saveboard(fil, pos.x, pos.y, pos.z, (int)pos.ang, pos.sectnum);
						fil.close();
						Console.Println("Map saved to file " + name);
					} catch(Exception e) { Console.Println("type out of range"); }	
				} else {
					Console.Println("savemap: not in a game");
				}
			}
		}));
	}

	@Override
	public void show() {
		
		if (recstat == 1 && rec != null)
			rec.close();
		
		engine.setTilesPath(0);
		game.changeScreen(gIntroScreen.setSkipping(toMenu));
	}

	@Override
	protected byte[] reportData() {
		String report = "Map: " + mapnum;
		report += "\r\n";
		report += "Skill " + nDifficult;
		report += "\r\n";

		if (gPlayer[myconnectindex] != null) {
			report += "PlayerX " + gPlayer[myconnectindex].x;
			report += "\r\n";
			report += "PlayerY " + gPlayer[myconnectindex].y;
			report += "\r\n";
			report += "PlayerZ " + gPlayer[myconnectindex].z;
			report += "\r\n";
			report += "PlayerAng " + gPlayer[myconnectindex].ang;
			report += "\r\n";
			report += "PlayerSect " + gPlayer[myconnectindex].sectnum;
			report += "\r\n";
		}

		return report.getBytes();
	}
	
	@Override
	public void dispose() {
		if (rec != null)
			rec.close();
		super.dispose();
	}
}
