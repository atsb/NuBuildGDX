// This file is part of RedneckGDX.
// Copyright (C) 2017-2019  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// RedneckGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// RedneckGDX is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with RedneckGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.Redneck;

import static ru.m210projects.Build.Engine.MAXPLAYERS;
import static ru.m210projects.Build.Engine.MAXSPRITES;
import static ru.m210projects.Build.Gameutils.loadGdxDef;
import static ru.m210projects.Build.Net.Mmulti.connecthead;
import static ru.m210projects.Build.Net.Mmulti.myconnectindex;
import static ru.m210projects.Build.Net.Mmulti.numplayers;
import static ru.m210projects.Build.OnSceenDisplay.Console.OSDTEXT_GOLD;
import static ru.m210projects.Build.OnSceenDisplay.Console.OSDTEXT_RED;
import static ru.m210projects.Build.OnSceenDisplay.Console.OSDTEXT_YELLOW;
import static ru.m210projects.Build.OnSceenDisplay.Console.osd_argv;
import static ru.m210projects.Redneck.Factory.RRMenuHandler.*;
import static ru.m210projects.Redneck.Animate.initanimations;
import static ru.m210projects.Redneck.Factory.RRMenuHandler.GAME;
import static ru.m210projects.Redneck.Factory.RRMenuHandler.MAIN;
import static ru.m210projects.Redneck.Gamedef.compilecons;
import static ru.m210projects.Redneck.Globals.MAXANIMWALLS;
import static ru.m210projects.Redneck.Globals.RRRA;
import static ru.m210projects.Redneck.Globals.TICRATE;
import static ru.m210projects.Redneck.Globals.animwall;
import static ru.m210projects.Redneck.Globals.boardfilename;
import static ru.m210projects.Redneck.Globals.currentGame;
import static ru.m210projects.Redneck.Globals.hittype;
import static ru.m210projects.Redneck.Globals.kGameCrash;
import static ru.m210projects.Redneck.Globals.mFakeMultiplayer;
import static ru.m210projects.Redneck.Globals.po;
import static ru.m210projects.Redneck.Globals.ps;
import static ru.m210projects.Redneck.Globals.uGameFlags;
import static ru.m210projects.Redneck.Globals.ud;
import static ru.m210projects.Redneck.LoadSave.FindSaves;
import static ru.m210projects.Redneck.LoadSave.quickload;
import static ru.m210projects.Redneck.LoadSave.quicksave;
import static ru.m210projects.Redneck.Player.InitPlayers;
import static ru.m210projects.Redneck.Premap.LeaveMap;
import static ru.m210projects.Redneck.Premap.genspriteremaps;
import static ru.m210projects.Redneck.Premap.packbuf;
import static ru.m210projects.Redneck.ResourceHandler.InitSpecialTextures;
import static ru.m210projects.Redneck.ResourceHandler.resetEpisodeResources;
import static ru.m210projects.Redneck.ResourceHandler.usecustomarts;
import static ru.m210projects.Redneck.Sounds.MusicStartup;
import static ru.m210projects.Redneck.Sounds.SoundStartup;
import static ru.m210projects.Redneck.Sounds.StopAllSounds;
import static ru.m210projects.Redneck.Sounds.searchCDtracks;
import static ru.m210projects.Redneck.Types.RTS.RTS_Init;
import static ru.m210projects.Redneck.Types.RTS.numlumps;

import java.util.Arrays;
import java.util.Iterator;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.utils.ByteArray;

import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.Architecture.BuildMessage.MessageType;
import ru.m210projects.Build.FileHandle.DirectoryEntry;
import ru.m210projects.Build.FileHandle.FileEntry;
import ru.m210projects.Build.FileHandle.Group;
import ru.m210projects.Build.FileHandle.GroupResource;
import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Build.OnSceenDisplay.OSDCOMMAND;
import ru.m210projects.Build.OnSceenDisplay.OSDCVARFUNC;
import ru.m210projects.Build.Pattern.BuildFactory;
import ru.m210projects.Build.Pattern.BuildGame;
import ru.m210projects.Build.Script.DefScript;
import ru.m210projects.Build.Settings.BuildConfig;
import ru.m210projects.Build.Types.LittleEndian;
import ru.m210projects.Redneck.Menus.MenuCorruptGame;
import ru.m210projects.Redneck.Menus.TrackPlayerMenu;
import ru.m210projects.Redneck.Factory.RREngine;
import ru.m210projects.Redneck.Factory.RRFactory;
import ru.m210projects.Redneck.Factory.RRMenuHandler;
import ru.m210projects.Redneck.Factory.RRNetwork;
import ru.m210projects.Redneck.Fonts.GameFont;
import ru.m210projects.Redneck.Fonts.MenuFont;
import ru.m210projects.Redneck.Menus.GameMenu;
import ru.m210projects.Redneck.Menus.MainMenu;
import ru.m210projects.Redneck.Screens.AnmScreen;
import ru.m210projects.Redneck.Screens.DemoScreen;
import ru.m210projects.Redneck.Screens.DisconnectScreen;
import ru.m210projects.Redneck.Screens.EndScreen;
import ru.m210projects.Redneck.Screens.GameScreen;
import ru.m210projects.Redneck.Screens.LoadingScreen;
import ru.m210projects.Redneck.Screens.MVEScreen;
import ru.m210projects.Redneck.Screens.MenuScreen;
import ru.m210projects.Redneck.Screens.NetScreen;
import ru.m210projects.Redneck.Screens.PrecacheScreen;
import ru.m210projects.Redneck.Screens.StatisticScreen;
import ru.m210projects.Redneck.Types.Animwalltype;
import ru.m210projects.Redneck.Types.PlayerOrig;
import ru.m210projects.Redneck.Types.PlayerStruct;
import ru.m210projects.Redneck.Types.Weaponhit;

public class Main extends BuildGame {

	/*
	 * Changelog:
	 * 8 Track player
	 * Shuffle music option
	 * Dead cows don't block your shots
	 * Quick weapon switch without waiting fully raised
	 *
	 * TODO:
	 * as I said once, you cannot pickup a weapon if you already have it (in MP?)
	 * Улучшение, которое я хотел бы увидеть, - зафиксировать счетчик врагов. NukeyT сказал мне много вещей, которые не следует считать врагами (например, торнадо или даже Бубба), а Виксен считается только мертвым, если их тела выбиты, что должно быть только для стражей Халка.
	 */

	public static final String appdef = "rrgdx.def";

	public static AnmScreen gAnmScreen;
	public static MVEScreen gMveScreen;
	public static MenuScreen gMenuScreen;
	public static LoadingScreen gLoadingScreen;
	public static GameScreen gGameScreen;
	public static DemoScreen gDemoScreen;
	public static StatisticScreen gStatisticScreen;
	public static EndScreen gEndScreen;
	public static NetScreen gNetScreen;
	public static DisconnectScreen gDisconnectScreen;
	public static PrecacheScreen gPrecacheScreen;

	public static enum UserFlag {
		None, UserMap, Addon
	};

	public static UserFlag mUserFlag = UserFlag.None;
	public static Main game;
	public static RREngine engine;
	public static Config cfg;
	public RRMenuHandler menu;
	public RRNetwork net;

	public Main(BuildConfig dcfg, String appname, String sversion, boolean isDemo, boolean isRelease) {
		super(dcfg, appname, sversion, isRelease);
		game = this;
		cfg = (Config) dcfg;
	}

	public Main(BuildConfig dcfg, String appname, String sversion, boolean isDemo) {
		this(dcfg, appname, sversion, isDemo, true);
	}

	@Override
	public BuildFactory getFactory() {
		return new RRFactory(this);
	}

	@Override
	public boolean init() throws Exception {
		net = (RRNetwork) pNet;

		ConsoleInit();

		genspriteremaps();

		compilecons();

		engine.inittimer(TICRATE);

		InitSpecialTextures();

		InitUserDefs();

		RTS_Init(ud.rtsname);
		if (numlumps != 0)
			Console.Println("Using .RTS file:" + ud.rtsname);

		SoundStartup();
		MusicStartup();
		searchCDtracks();

		initanimations();
		FindSaves();

		for (int i = 0; i < MAXPLAYERS; i++) {
			ps[i] = new PlayerStruct();
			po[i] = new PlayerOrig();
		}

		InitPlayers();

		for (int i = 0; i < MAXANIMWALLS; i++)
			animwall[i] = new Animwalltype();

		for (int i = 0; i < MAXSPRITES; i++)
			hittype[i] = new Weaponhit();

		Console.Println("Initializing def-scripts...");

		loadGdxDef(baseDef, appdef, "rrgdx.dat");

		if (cfg.autoloadFolder) {
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

		menu.mMenus[TRACKPLAYER] = new TrackPlayerMenu();
		menu.mMenus[MAIN] = new MainMenu(this);
		menu.mMenus[GAME] = new GameMenu(this);
		menu.mMenus[CORRUPTLOAD] = new MenuCorruptGame(this);

		gAnmScreen = new AnmScreen(this);
		gMveScreen = new MVEScreen(this);
		gMenuScreen = new MenuScreen(this);
		gLoadingScreen = new LoadingScreen(this);
		gGameScreen = new GameScreen(this);
		gDemoScreen = new DemoScreen(this);
		gStatisticScreen = new StatisticScreen(this);
		gEndScreen = new EndScreen();
		gNetScreen = new NetScreen(this);
		gDisconnectScreen = new DisconnectScreen(this);
		gPrecacheScreen = new PrecacheScreen(this);

		gDemoScreen.demoscan();

		return true;
	}

	public static boolean IsOriginalDemo() {
		ScreenAdapter screen = (ScreenAdapter) game.getScreen();
		if(screen instanceof DemoScreen)
			return ((DemoScreen) screen).IsOriginalGame();
		if(screen instanceof GameScreen)
			return ((GameScreen) screen).IsOriginalGame();

		return false;
	}

	@Override
	public boolean setDefs(DefScript script)
	{
		if(super.setDefs(script))
		{
			((GameFont)this.getFont(1)).update();
			((MenuFont)this.getFont(2)).update();
			return true;
		}

		return false;
	}

	@Override
	public void show() {
		uGameFlags = 0;
		kGameCrash = false;
		if(usecustomarts)
			resetEpisodeResources();

		if (ud.recstat == 1 && ud.rec != null)
			ud.rec.close();

		if(currentGame.getCON().type == RRRA && gMveScreen.init("redint.mve")) {
			gMveScreen.setCallback(rMenu);
			setScreen(gMveScreen.escSkipping(true));
		} else if (gAnmScreen.init("rr_intro.anm", 0)) {
			gAnmScreen.setCallback(new Runnable() {
				@Override
				public void run() {
					if (gAnmScreen.init("redneck.anm", 1)) {
						gAnmScreen.setCallback(new Runnable() {
							@Override
							public void run() {
								if (gAnmScreen.init("xatlogo.anm", 2)) {
									setScreen(gAnmScreen.setCallback(rMenu).escSkipping(false));
								}
							}
						});
						setScreen(gAnmScreen.escSkipping(false));
					}
				}
			}).setSkipping(rMenu);
			setScreen(gAnmScreen.escSkipping(false));
		} else {
			setScreen(gLoadingScreen);
			BuildGdx.app.postRunnable(rMenu);
		}
	}

	private Runnable rMenu = new Runnable() {
		@Override
		public void run() {
			StopAllSounds();
			ud.level_number = 0;
			ud.multimode = 1;
			mFakeMultiplayer = false;

			if (!menu.gShowMenu)
				menu.mOpen(menu.mMenus[MAIN], -1);

			if (numplayers > 1 || gDemoScreen.demofiles.size() == 0 || cfg.gDemoSeq == 0 || !gDemoScreen.showDemo())
				changeScreen(gMenuScreen);
		}
	};

	public void InitUserDefs() {
		ud.setDefaults(cfg);
		ud.god = false;
		ud.cashman = 0;
		ud.player_skill = 2;
	}

	public void ConsoleInit() {
		Console.Println("Initializing on-screen display system");
		Console.setVersion(appname + " " + sversion, 10, OSDTEXT_GOLD);

		Console.RegisterCvar(new OSDCOMMAND("restart", "restart", new OSDCVARFUNC() {
			@Override
			public void execute() {
				LeaveMap();
			}
		}));

		Console.RegisterCvar(new OSDCOMMAND("net_player", "net_player", new OSDCVARFUNC() {
			@Override
			public void execute() {
				if (Console.osd_argc != 2) {
					Console.Println("net_player: num");
					return;
				}
				try {
					String num = osd_argv[1];

					int pnum = Integer.parseInt(num);
					int p = game.net.PutPacketByte(packbuf, 0, RRNetwork.kPacketPlayer);
					p = game.net.PutPacketByte(packbuf, p, pnum);

					int trail = game.net.gNetFifoTail;
					if (myconnectindex == connecthead)
						trail += 1;

					LittleEndian.putInt(packbuf, p, trail);
					p += 4;
					game.net.PlayerSyncRequest = pnum;

					game.net.sendtoall(packbuf, p);
				} catch (Exception e) {
				}
			}
		}));

		Console.RegisterCvar(new OSDCOMMAND("quicksave", "quicksave: performs a quick save", new OSDCVARFUNC() {
			@Override
			public void execute() {
				if (ud.multimode != 1 || numplayers > 1) {
					Console.Println("quicksave: Single player only");
					return;
				}

				if (isCurrentScreen(gGameScreen)) {
					quicksave();
				} else
					Console.Println("quicksave: not in a game");
			}
		}));

		Console.RegisterCvar(new OSDCOMMAND("quickload", "quickload: performs a quick load", new OSDCVARFUNC() {
			@Override
			public void execute() {
				if (ud.multimode != 1 || numplayers > 1) {
					Console.Println("quickload: Single player only");
					return;
				}

				if (isCurrentScreen(gGameScreen)) {
					quickload();
				} else
					Console.Println("quickload: not in a game");
			}
		}));

//		Console.RegisterCvar(new OSDCOMMAND("net_nextmap",
//				"net_nextmap", new OSDCVARFUNC() {
//					@Override
//					public void execute() {
//						LeaveMap();
//						ud.level_number++;
//						game.net.sendtoall(new byte[] { kPacketLevelEnd }, 1);
//					}
//		}));
	}

	public void dassert(String msg) {
		if (kGameCrash)
			return;

		ThrowError(msg);
		System.exit(0);
	}

	public boolean GameMessage(String text, boolean question) {
		if (!question) {
			BuildGdx.message.show("Warning: ", text, MessageType.Info);
			Console.Println("Warning: " + text, OSDTEXT_YELLOW);
			return true;
		} else {
			Console.Println("Warning: " + text, OSDTEXT_YELLOW);
			return BuildGdx.message.show("Warning: ", text, MessageType.Question);
		}
	}

	public void GameCrash(String errorText) {
		BuildGdx.message.show("Error: ", errorText, MessageType.Info);
		Console.Println("Game error: " + errorText, OSDTEXT_RED);

		kGameCrash = true;
	}

	public void Disconnect() {
		if (ud.recstat == 1 && ud.rec != null)
			ud.rec.close();
		if(gDisconnectScreen != null)
			changeScreen(gDisconnectScreen);
	}

	@Override
	public void dispose() {
		if (ud.rec != null)
			ud.rec.close();
		super.dispose();
	}

	@Override
	protected byte[] reportData() {
		byte[] out = null;

		String text = "Mapname: " + boardfilename;
		text += "\r\n";
		text += "UserFlag: " + mUserFlag;
		text += "\r\n";

		if (mUserFlag == UserFlag.Addon && currentGame != null) {
			try {
				FileEntry addon = currentGame.getFile();
				if(addon != null  && currentGame.isPackage())
					text += "Episode filename: " + addon.getPath() + ":" + currentGame.ConName;
				else
					text += "Episode filename: " + currentGame.getDirectory().checkFile(currentGame.ConName).getPath();
				text += "\r\n";
			} catch(Exception e) {
				text += "Episode filename get error \r\n";
			}
		}

		text += "volume " + (ud.volume_number + 1);
		text += "\r\n";
		text += "level " + (ud.level_number + 1);
		text += "\r\n";
		text += "nDifficulty: " + ud.player_skill;
		text += "\r\n";

		if (ps != null) {
			text += "PlayerX: " + ps[myconnectindex].posx;
			text += "\r\n";
			text += "PlayerY: " + ps[myconnectindex].posy;
			text += "\r\n";
			text += "PlayerZ: " + ps[myconnectindex].posz;
			text += "\r\n";
			text += "PlayerAng: " + ps[myconnectindex].ang;
			text += "\r\n";
			text += "PlayerHoriz: " + ps[myconnectindex].horiz;
			text += "\r\n";
			text += "PlayerSect: " + ps[myconnectindex].cursectnum;
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
		} else out = text.getBytes();

		return out;
	}
}
