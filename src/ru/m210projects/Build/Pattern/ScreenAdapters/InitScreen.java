//This file is part of BuildGDX.
//Copyright (C) 2017-2018  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
//BuildGDX is free software: you can redistribute it and/or modify
//it under the terms of the GNU General Public License as published by
//the Free Software Foundation, either version 3 of the License, or
//(at your option) any later version.
//
//BuildGDX is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU General Public License for more details.
//
//You should have received a copy of the GNU General Public License
//along with BuildGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.Build.Pattern.ScreenAdapters;

import static ru.m210projects.Build.Net.Mmulti.uninitmultiplayer;
import static ru.m210projects.Build.OnSceenDisplay.Console.OSDTEXT_RED;

import java.io.File;
import java.io.FileNotFoundException;

import com.badlogic.gdx.ScreenAdapter;

import ru.m210projects.Build.Engine;
import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.Architecture.BuildMessage.MessageType;
import ru.m210projects.Build.Audio.BuildAudio.Driver;
import ru.m210projects.Build.FileHandle.Compat.Path;
import ru.m210projects.Build.FileHandle.DirectoryEntry;
import ru.m210projects.Build.FileHandle.Group;
import ru.m210projects.Build.FileHandle.GroupResource;
import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Build.OnSceenDisplay.OSDCOMMAND;
import ru.m210projects.Build.OnSceenDisplay.OSDCVARFUNC;
import ru.m210projects.Build.Pattern.BuildEngine;
import ru.m210projects.Build.Pattern.BuildGame;
import ru.m210projects.Build.Pattern.Tools.Interpolation;
import ru.m210projects.Build.Pattern.Tools.SaveManager;
import ru.m210projects.Build.Render.Renderer.RenderType;
import ru.m210projects.Build.Settings.BuildConfig;
import ru.m210projects.Build.Settings.BuildSettings;
import ru.m210projects.Build.Settings.GLSettings;
import ru.m210projects.Build.Settings.BuildConfig.GameKeys;
import ru.m210projects.Build.Types.MemLog;
import ru.m210projects.Build.Pattern.BuildFactory;

public class InitScreen extends ScreenAdapter {

	private int frames;
	private BuildEngine engine;
	private final BuildFactory factory;
	private Thread thread;
	private final BuildGame game;
	private boolean gameInitialized;
	private boolean disposing;

	@Override
	public void show() {
		frames = 0;
		Console.fullscreen(true);
		gameInitialized = false;
		disposing = false;
	}

	@Override
	public void hide() {
		Console.fullscreen(false);
	}

	private void ConsoleInit() {
		Console.RegisterCvar(new OSDCOMMAND("memusage", "mem usage / total", new OSDCVARFUNC() {
			@Override
			public void execute() {
				Console.Println("Memory used: " + MemLog.used() + " / " + MemLog.total() + " mb");
			}
		}));

		Console.RegisterCvar(new OSDCOMMAND("net_bufferjitter", "net_bufferjitter", new OSDCVARFUNC() {
			@Override
			public void execute() {
				Console.Println("bufferjitter: " + game.pNet.bufferJitter);
			}
		}));

		Console.RegisterCvar(new OSDCOMMAND("deb_filelist", "deb_filelist", new OSDCVARFUNC() {
			@Override
			public void execute() {
				for (Group g : BuildGdx.cache.getGroupList()) {
					Console.Println("group: " + g.name);
					for (GroupResource res : g.getList()) {
						Console.Println("\t   file: " + res.getFullName());
					}
				}
			}
		}));

		Console.RegisterCvar(new OSDCOMMAND("quit", null, new OSDCVARFUNC() {
			@Override
			public void execute() {
				game.gExit = true;
			}
		}));
	}

	public InitScreen(final BuildGame game) {
		this.game = game;
		BuildConfig cfg = game.pCfg;
		factory = game.getFactory();

		Console.SetLogFile(game.appname + ".log");

		Console.Println("BUILD engine by Ken Silverman (http://www.advsys.net/ken) \r\n" + game.appname + " "
				+ game.sversion + "(NuBuildGDX v" + Engine.version + ")");

		Console.Println("Current date " + game.date.getLaunchDate());

		String osver = System.getProperty("os.version");
		String jrever = System.getProperty("java.version");

		Console.Println("Running on " + game.OS + " (version " + osver + ")");
		Console.Println("\t with JRE version: " + jrever + "\r\n");

		Console.Println("Initializing resource archives");

		for (int i = 0; i < factory.resources.length; i++) {
			try {
				BuildGdx.cache.add(factory.resources[i]);
            } catch (Exception e) {
				BuildGdx.message.show("Init error!", "Resource initialization error! \r\n" + e.getMessage(),
						MessageType.Info);
				System.exit(1);
				return;
			}
		}

		try {
			Console.Println("Initializing Build 3D engine");
			this.engine = game.pEngine = factory.engine();
		} catch (Exception e) {
			BuildGdx.message.show("Build Engine Initialization Error!",
					"There was a problem initialising the Build engine: \r\n" + e.getMessage(), MessageType.Info);
			System.exit(1);
			return;
		}

		game.pInt = new Interpolation();
		game.pSavemgr = new SaveManager();

		Console.setFunction(factory.console());
		Console.ResizeDisplay(cfg.ScreenWidth, cfg.ScreenHeight);

		if (engine.loadpics() == 0) {
			BuildGdx.message.show("Build Engine Initialization Error!",
					"ART files not found " + new File(Path.Game.getPath() + engine.tilesPath).getAbsolutePath(),
					MessageType.Info);
			System.exit(1);
			return;
		}

		game.pFonts = factory.fonts();

		BuildSettings.init(engine, cfg);
		GLSettings.init(engine, cfg);

		if(!engine.setrendermode(factory.renderer(cfg.renderType))) {
			engine.setrendermode(factory.renderer(RenderType.Software));
			cfg.renderType = RenderType.Software;
		}

		if (!engine.setgamemode(cfg.fullscreen, cfg.ScreenWidth, cfg.ScreenHeight))
			cfg.fullscreen = 0;

		if(cfg.autoloadFolder) {
			DirectoryEntry autoloadDir = BuildGdx.compat.checkDirectory("autoload");
			if(autoloadDir == null) { // not found
				File f = new File(Path.Game.getPath() + File.separator + "autoload");
				if(!f.exists() && !f.mkdirs() && !f.isDirectory())
					Console.Println("Can't create autoload folder", OSDTEXT_RED);
			}
		}

		thread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					BuildConfig cfg = game.pCfg;
					cfg.InitKeymap();
					if (!cfg.isInited)
						cfg.isInited = cfg.InitConfig(!cfg.isExist());

					game.pNet = factory.net();
					game.pInput = factory.input(BuildGdx.controllers.init());
					game.pSlider = factory.slider();
					game.pMenu = factory.menus();
					game.baseDef = factory.getBaseDef(engine);

					uninitmultiplayer();

					cfg.snddrv = BuildGdx.audio.checkNum(Driver.Sound, cfg.snddrv);
					cfg.middrv = BuildGdx.audio.checkNum(Driver.Music, cfg.middrv);

					BuildGdx.audio.setDriver(Driver.Sound, cfg.snddrv);
					BuildGdx.audio.setDriver(Driver.Music, cfg.middrv);

					int consolekey = GameKeys.Show_Console.getNum();
					if (consolekey != -1) {
						Console.setCaptureKey(cfg.primarykeys[consolekey], 0);
						Console.setCaptureKey(cfg.secondkeys[consolekey], 1);
						Console.setCaptureKey(cfg.mousekeys[consolekey], 2);
						Console.setCaptureKey(cfg.gpadkeys[consolekey], 3);
					}

					BuildSettings.usenewaspect.set(cfg.widescreen == 1);
					BuildSettings.fov.set(cfg.gFov);
					BuildSettings.fpsLimit.set(cfg.fpslimit);

//					BuildGdx.threads = new ThreadProcessor();

					gameInitialized = game.init();

					ConsoleInit();
				} catch (OutOfMemoryError me) {
					System.gc();

					me.printStackTrace();
					String message = "Memory used: [ " + MemLog.used() + " / " + MemLog.total()
							+ " mb ] \r\nPlease, increase the java's heap size.";
					Console.Println(message, Console.OSDTEXT_RED);
					BuildGdx.message.show("OutOfMemory!", message, MessageType.Info);
					System.exit(1);
				} catch (FileNotFoundException fe) {
					fe.printStackTrace();

					String message = fe.getMessage();
					Console.Println(message, Console.OSDTEXT_RED);
					BuildGdx.message.show("File not found!", message, MessageType.Info);
					System.exit(1);
				} catch (Throwable e) {
					if (!disposing) {
						game.ThrowError("InitScreen error", e);
						System.exit(1);
					}
				}
			}
		});
		thread.setName("InitEngine thread");
		thread.setDaemon(true); // to make the thread as background process and kill it if the app was closed
	}

	public void start() {
		if (thread != null)
			thread.start();
	}

	@Override
	public void dispose() {
		synchronized (Engine.lock) {
			disposing = true;
		}
	}

	@Override
	public void render(float delta) {
		synchronized (Engine.lock) {
			if (!disposing && engine.getrender().isInited()) { // don't draw anything after disposed
				engine.clearview(0);

//				engine.rotatesprite(0, 0, 65536, 0, factory.getInitTile(), -128, 0, 10 | 16, 0, 0, xdim - 1, ydim - 1);

				factory.drawInitScreen();

				if (frames++ > 3) {
					if (!thread.isAlive()) {
						if (gameInitialized)
							game.show();
						else {
							game.GameMessage("InitScreen unknown error!");
							BuildGdx.app.exit();
						}
					}
				}
				engine.nextpage();
			}
		}
	}
}
