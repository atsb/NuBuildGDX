// This file is part of DukeGDX.
// Copyright (C) 2019  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// DukeGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// DukeGDX is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with DukeGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.Duke3D;

import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Build.Gameutils.loadGdxDef;
import static ru.m210projects.Duke3D.LoadSave.*;
import static ru.m210projects.Build.Net.Mmulti.*;
import static ru.m210projects.Build.OnSceenDisplay.Console.*;
import static ru.m210projects.Duke3D.Animate.*;
import static ru.m210projects.Duke3D.Factory.DukeMenuHandler.*;
import static ru.m210projects.Duke3D.Gamedef.*;
import static ru.m210projects.Duke3D.Globals.*;
import static ru.m210projects.Duke3D.Names.*;
import static ru.m210projects.Duke3D.Premap.*;
import static ru.m210projects.Duke3D.Player.*;
import static ru.m210projects.Duke3D.ResourceHandler.*;
import static ru.m210projects.Duke3D.Sounds.*;
import static ru.m210projects.Duke3D.SoundDefs.*;
import static ru.m210projects.Duke3D.Types.RTS.*;

import java.util.Arrays;
import java.util.Iterator;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.utils.ByteArray;

import ru.m210projects.Duke3D.Screens.PrecacheScreen;
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
import ru.m210projects.Duke3D.Factory.DukeEngine;
import ru.m210projects.Duke3D.Factory.DukeFactory;
import ru.m210projects.Duke3D.Factory.DukeMenuHandler;
import ru.m210projects.Duke3D.Factory.DukeNetwork;
import ru.m210projects.Duke3D.Fonts.GameFont;
import ru.m210projects.Duke3D.Fonts.MenuFont;
import ru.m210projects.Duke3D.Menus.GameMenu;
import ru.m210projects.Duke3D.Menus.MainMenu;
import ru.m210projects.Duke3D.Menus.MenuCorruptGame;
import ru.m210projects.Duke3D.Screens.AnmScreen;
import ru.m210projects.Duke3D.Screens.DemoScreen;
import ru.m210projects.Duke3D.Screens.DisconnectScreen;
import ru.m210projects.Duke3D.Screens.EndScreen;
import ru.m210projects.Duke3D.Screens.GameScreen;
import ru.m210projects.Duke3D.Screens.LoadingScreen;
import ru.m210projects.Duke3D.Screens.LogoAScreen;
import ru.m210projects.Duke3D.Screens.LogoBScreen;
import ru.m210projects.Duke3D.Screens.MenuScreen;
import ru.m210projects.Duke3D.Screens.NetScreen;
import ru.m210projects.Duke3D.Screens.StatisticScreen;
import ru.m210projects.Duke3D.Sounds.Commentary;
import ru.m210projects.Duke3D.Types.Animwalltype;
import ru.m210projects.Duke3D.Types.PlayerOrig;
import ru.m210projects.Duke3D.Types.PlayerStruct;
import ru.m210projects.Duke3D.Types.DevCommScript;
import ru.m210projects.Duke3D.Types.Weaponhit;

public class Main extends BuildGame {

	public static final String dukeappdef = "dukegdx.def";
	public static final String namappdef = "namgdx.def";
	public static String appdef;

	/*
	 * Changelog: esc-skipping to end-cutscenes
	 *
	 * TODO: defscript to play anmfiles before an episode starts Check WT
	 * music/sound. Why music loaded from WT music folder if there is music addon in
	 * autoload folder caribian e1m1 camera bug
	 */

	public static AnmScreen gAnmScreen;
	public static LogoAScreen gLogoScreen;
	public static LogoBScreen gALogoScreen;
	public static MenuScreen gMenuScreen;
	public static LoadingScreen gLoadingScreen;
	public static GameScreen gGameScreen;
	public static DemoScreen gDemoScreen;
	public static StatisticScreen gStatisticScreen;
	public static EndScreen gEndScreen;
	public static NetScreen gNetScreen;
	public static DisconnectScreen gDisconnectScreen;
	public static PrecacheScreen gPrecacheScreen;

	public enum UserFlag {
		None, UserMap, Addon
	}

	public static UserFlag mUserFlag = UserFlag.None;
	public static Main game;
	public static DukeEngine engine;
	public static Config cfg;
	public DukeMenuHandler menu;
	public DukeNetwork net;
	public final String mainGrp;
	public int gameParam;

	public Main(BuildConfig dcfg, String appname, String sversion, int gameParam, boolean isDemo, boolean isRelease) {
		super(dcfg, appname, sversion, isRelease);
		game = this;
		cfg = (Config) dcfg;

		this.gameParam = gameParam;

		appdef = gameParam == 1 ? namappdef : dukeappdef;
		mainGrp = gameParam == 1 ? "nam.grp" : "duke3d.grp";
	}

	public Main(BuildConfig dcfg, String appname, String sversion, int gameParam, boolean isDemo) {
		this(dcfg, appname, sversion, gameParam, isDemo, true);
	}

	@Override
	public BuildFactory getFactory() {
		return new DukeFactory(this);
	}

	@Override
	public boolean init() throws Exception {
		net = (DukeNetwork) pNet;
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
		loadGdxDef(baseDef, appdef, "dukegdx.dat");

		if (cfg.autoloadFolder) {
			Console.Println("Initializing autoload folder");
			DirectoryEntry autoload;
			if ((autoload = BuildGdx.compat.checkDirectory("autoload")) != null) {
				for (FileEntry file : autoload.getFiles().values()) {
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
		this.setDefs(baseDef);

		if (currentGame.getCON().type == 20) { // Twentieth Anniversary World Tour
			DirectoryEntry defdir = BuildGdx.compat.checkDirectory("def");
			if (defdir != null) {
				FileEntry def = defdir.checkFile("developer_commentary.def");
				if (def != null) {
					DevCommScript scr = new DevCommScript(def);
					commentaries = scr.getCommentaries();
				}
			}
			pCommentary = new Commentary();
		}

		menu.mMenus[MAIN] = new MainMenu(this);
		menu.mMenus[GAME] = new GameMenu(this);
		menu.mMenus[CORRUPTLOAD] = new MenuCorruptGame(this);

		gAnmScreen = new AnmScreen(this);
		gLogoScreen = new LogoAScreen(this, 5.0f);
		gALogoScreen = new LogoBScreen(this);
		gMenuScreen = new MenuScreen(this);
		gLoadingScreen = new LoadingScreen(this);
		gGameScreen = new GameScreen(this);
		gDemoScreen = new DemoScreen(this);
		gStatisticScreen = new StatisticScreen(this);
		gEndScreen = new EndScreen(this);
		gNetScreen = new NetScreen(this);
		gDisconnectScreen = new DisconnectScreen(this);
		gPrecacheScreen = new PrecacheScreen(this);

		gALogoScreen.setCallback(rMenu);

		gDemoScreen.demoscan();

		return true;
	}

	public static boolean IsOriginalDemo() {
		ScreenAdapter screen = (ScreenAdapter) game.getScreen();
		if (screen instanceof DemoScreen)
			return ((DemoScreen) screen).IsOriginalGame();

		if (screen instanceof GameScreen)
			return ((GameScreen) screen).IsOriginalGame();

		return false;
	}

	@Override
	public boolean setDefs(DefScript script) {
		if (super.setDefs(script)) {
			((GameFont) this.getFont(1)).update();
			((MenuFont) this.getFont(2)).update();
			return true;
		}

		return false;
	}

	@Override
	public void show() {
		uGameFlags = 0;
		kGameCrash = false;
		if (usecustomarts)
			resetEpisodeResources();

		if (ud.recstat == 1 && ud.rec != null)
			ud.rec.close();

		if (gAnmScreen.init("logo.anm", 5)) {
			gAnmScreen.setCallback(new Runnable() {
				@Override
				public void run() {
					changeScreen(gLogoScreen.setCallback(nextLogo).setSkipping(rMenu));
				}
			});
			setScreen(gAnmScreen.escSkipping(false));
		} else
			setScreen(gLogoScreen.setCallback(nextLogo).setSkipping(rMenu));
	}

	private final Runnable rMenu = new Runnable() {
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

	private final Runnable nextLogo = new Runnable() {
		@Override
		public void run() {
			changeScreen(gALogoScreen.setCallback(rMenu));
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

		Console.RegisterCvar(new OSDCOMMAND("findexit", "mem usage / total", new OSDCVARFUNC() {
			@Override
			public void execute() {
				for (int i = 0; i < MAXSPRITES; i++) {
					if (sprite[i].statnum != MAXSTATUS && sprite[i].picnum == NUKEBUTTON)
						Console.Println("Exit at: " + sprite[i].x + " " + sprite[i].y + " " + sprite[i].z);
				}
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
					int p = game.net.PutPacketByte(packbuf, 0, DukeNetwork.kPacketPlayer);
					p = game.net.PutPacketByte(packbuf, p, pnum);

					int trail = game.net.gNetFifoTail;
					if (myconnectindex == connecthead)
						trail += 1;

					LittleEndian.putInt(packbuf, p, trail);
					p += 4;
					game.net.PlayerSyncRequest = pnum;

					game.net.sendtoall(packbuf, p);
				} catch (Exception ignored) {
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

		if (gDisconnectScreen != null)
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
				if (addon != null && currentGame.isPackage())
					text += "Episode filename: " + addon.getPath() + ":" + currentGame.ConName;
				else
					text += "Episode filename: " + currentGame.getDirectory().checkFile(currentGame.ConName).getPath();
				text += "\r\n";
			} catch (Exception e) {
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
		} else
			out = text.getBytes();

		return out;
	}
}
