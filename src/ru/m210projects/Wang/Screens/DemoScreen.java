package ru.m210projects.Wang.Screens;

import static ru.m210projects.Build.Engine.totalclock;
import static ru.m210projects.Build.Gameutils.BClipRange;
import static ru.m210projects.Build.Net.Mmulti.connecthead;
import static ru.m210projects.Build.Net.Mmulti.connectpoint2;
import static ru.m210projects.Build.Net.Mmulti.myconnectindex;
import static ru.m210projects.Build.Net.Mmulti.numplayers;
import static ru.m210projects.Build.OnSceenDisplay.Console.OSDTEXT_GOLD;
import static ru.m210projects.Build.OnSceenDisplay.Console.OSDTEXT_RED;
import static ru.m210projects.Wang.Border.SetBorder;
import static ru.m210projects.Wang.Factory.WangMenuHandler.MAIN;
import static ru.m210projects.Wang.Factory.WangMenuHandler.OPTIONS;
import static ru.m210projects.Wang.Factory.WangMenuHandler.QUIT;
import static ru.m210projects.Wang.Factory.WangMenuHandler.SOUNDSET;
import static ru.m210projects.Wang.Game.GodMode;
import static ru.m210projects.Wang.Game.Player;
import static ru.m210projects.Wang.Game.screenpeek;
import static ru.m210projects.Wang.Game.totalsynctics;
import static ru.m210projects.Wang.Gameutils.synctics;
import static ru.m210projects.Wang.LoadSave.lastload;
import static ru.m210projects.Wang.Main.engine;
import static ru.m210projects.Wang.Main.gDemoScreen;
import static ru.m210projects.Wang.Main.gMenuScreen;
import static ru.m210projects.Wang.Main.gNet;
import static ru.m210projects.Wang.Main.gPrecacheScreen;
import static ru.m210projects.Wang.Main.game;
import static ru.m210projects.Wang.Main.gs;
import static ru.m210projects.Wang.Palette.DoPlayerDivePalette;
import static ru.m210projects.Wang.Palette.DoPlayerNightVisionPalette;
import static ru.m210projects.Wang.Palette.FORCERESET;
import static ru.m210projects.Wang.Palette.ResetPalette;
import static ru.m210projects.Wang.Type.DemoFile.recfilep;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.FileHandle.Compat.Path;
import ru.m210projects.Build.FileHandle.FileEntry;
import ru.m210projects.Build.FileHandle.Resource;
import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Build.Pattern.BuildControls;
import ru.m210projects.Build.Settings.BuildConfig.GameKeys;
import ru.m210projects.Wang.Config.SwKeys;
import ru.m210projects.Wang.Main;
import ru.m210projects.Wang.Factory.WangMenuHandler;
import ru.m210projects.Wang.Factory.WangNetwork.MultiGameTypes;
import ru.m210projects.Wang.Type.DemoFile;
import ru.m210projects.Wang.Type.PlayerStr;

public class DemoScreen extends GameScreen {

	public static String firstdemofile;
	public int nDemonum = -1;
	public List<String> demofiles = new ArrayList<String>();
	public DemoFile demfile;

	public DemoScreen(Main game) {
		super(game);
	}

	@Override
	public void show() {
		lastload = null;
	}

	public boolean showDemo(String name, String ini)
	{
		demfile = null;
		try {
			demfile = new DemoFile(name);
		} catch(Exception e) { e.printStackTrace(); }

		if(demfile == null || demfile.reccnt == 0)
		{
			Console.Println("Can't play the demo file: " + name, OSDTEXT_RED);
			if(recfilep != null)
				recfilep.close();
			return false;
		}

		gNet.FakeMultiplayer = demfile.numplayers > 1;
		if(gNet.FakeMultiplayer)
			gNet.FakeMultiNumPlayers = demfile.numplayers;

		if (numplayers > 1)
			game.pNet.NetDisconnect(myconnectindex);

		if(demfile.nVersion != 1) {
			gNet.KillLimit = demfile.KillLimit;
			gNet.TimeLimit = demfile.TimeLimit;
			gNet.TimeLimitClock = demfile.TimeLimitClock;
			gNet.MultiGameType = demfile.MultiGameType;

			gNet.TeamPlay = demfile.TeamPlay;
			gNet.HurtTeammate = demfile.HurtTeammate;
			gNet.SpawnMarkers = demfile.SpawnMarkers;
			gNet.NoRespawn = demfile.NoRespawn;
			gNet.Nuke = demfile.Nuke;
		}

		GodMode = false;

        gDemoScreen.newgame(gNet.FakeMultiplayer, null, demfile.Episode, demfile.Level - 1, demfile.Skill);

		Console.Println("Playing demo " + name);

		return true;
	}

	@Override
	protected void startboard(final Runnable startboard)
	{
		gPrecacheScreen.init(false, new Runnable() {
			@Override
			public void run() {
				startboard.run(); //call faketimehandler
				pNet.ResetTimers(); //reset ototalclock
				totalsynctics = 0;
				pNet.ready2send = false;
			}
		});
		game.changeScreen(gPrecacheScreen);
	}

	@Override
	public void render(float delta) {
		KeyHandler();

		if(gNet.FakeMultiplayer)
			pEngine.faketimerhandler();

		if (numplayers > 1)
			pNet.GetPackets();

		DemoRender();

		float smoothratio = 65536;
		if (!game.gPaused) {
			smoothratio = pEngine.getsmoothratio();
			if (smoothratio < 0 || smoothratio > 0x10000) {
				smoothratio = BClipRange(smoothratio, 0, 0x10000);
			}
		}

		game.pInt.dointerpolations(smoothratio);
		DrawWorld(smoothratio);

		DrawHud(smoothratio);
		game.pInt.restoreinterpolations();

		if(pMenu.gShowMenu)
			pMenu.mDrawMenu();

		PostFrame(pNet);

		if (pCfg.gShowFPS)
			pEngine.printfps(pCfg.gFpsScale);

		pEngine.sampletimer();
		pEngine.nextpage();
	}


	private void DemoRender() {
		pNet.ready2send = false;

		if(!game.isCurrentScreen(this))
			return;

		if(!game.gPaused && demfile != null) {
			while (totalclock >= (totalsynctics + synctics)) {
				for (int j = connecthead; j >= 0; j = connectpoint2[j]) {
					pNet.gFifoInput[pNet.gNetFifoHead[j] & 0xFF][j].Copy(demfile.recsync[j][demfile.rcnt]);
					pNet.gNetFifoHead[j]++;
				}
				demfile.reccnt--;

				if (demfile.reccnt <= 0) {
					if(!showDemo())
						game.changeScreen(gMenuScreen);
					return;
				}

				demfile.rcnt++;
				engine.updatesmoothticks();
				game.pInt.clearinterpolations();
				game.pIntSkip2.clearinterpolations();
				game.pIntSkip4.clearinterpolations();
				ProcessFrame(pNet);
			}
		} else totalsynctics = totalclock;
	}

	@Override
	public void KeyHandler() {
		pEngine.handleevents();

		PlayerStr pp = Player[myconnectindex];
		WangMenuHandler menu = game.menu;
		if (menu.gShowMenu) {
			menu.mKeyHandler(game.pInput, BuildGdx.graphics.getDeltaTime());
			return;
		}

		if(Console.IsShown())
			return;

		BuildControls input = game.pInput;
		if (input.ctrlGetInputKey(GameKeys.Menu_Toggle, true))
			menu.mOpen(menu.mMenus[MAIN], -1);
		if (input.ctrlGetInputKey(SwKeys.Show_Sounds, true))
			menu.mOpen(menu.mMenus[SOUNDSET], -1);

		if (input.ctrlGetInputKey(SwKeys.Show_Options, true))
			menu.mOpen(menu.mMenus[OPTIONS], -1);

		if (input.ctrlGetInputKey(SwKeys.Gamma, true))
			openGamma(menu);

		if (input.ctrlGetInputKey(SwKeys.Quit, true))
			menu.mOpen(menu.mMenus[QUIT], -1);

		if (input.ctrlGetInputKey(SwKeys.Screenshot, true))
			makeScreenshot();

		if (input.ctrlGetInputKey(SwKeys.See_Coop_View, true)) {
			if (gNet.MultiGameType == MultiGameTypes.MULTI_GAME_COOPERATIVE || gNet.FakeMultiplayer) {
				screenpeek = connectpoint2[screenpeek];
				if (screenpeek < 0)
					screenpeek = connecthead;

				ResetPalette(Player[screenpeek], FORCERESET);
				DoPlayerDivePalette(Player[screenpeek]);
				DoPlayerNightVisionPalette(Player[screenpeek]);
			}
		}

		if (input.ctrlGetInputKey(GameKeys.Enlarge_Hud, true)) {
			SetBorder(pp, gs.BorderNum + 1);
		}
		if (input.ctrlGetInputKey(GameKeys.Shrink_Hud, true)) {
			SetBorder(pp, gs.BorderNum - 1);
		}
	}

	public boolean showDemo()
	{
		switch(gs.gDemoSeq)
		{
		case 0: //OFF
			return false;
		case 1: //Consistently
			if (nDemonum < (demofiles.size() - 1))
				nDemonum++;
			else
				nDemonum = 0;
			break;
		case 2: //Accidentally
			int nextnum = nDemonum;
			if(demofiles.size() > 1) {
				while(nextnum == nDemonum)
					nextnum = (int) (Math.random() * (demofiles.size()));
			}
			nDemonum = nextnum;
			break;
		}

		if(demofiles != null && demofiles.size() > 0)
			return showDemo(demofiles.get(nDemonum), null);

		return false;
	}

	public void demoscan() {
		Resource fil = null;
		for (Iterator<FileEntry> it = BuildGdx.compat.getDirectory(Path.Game).getFiles().values().iterator(); it.hasNext();) {
			FileEntry file = it.next();
			if (file.getExtension().equals("dmo")) {
				String name = file.getFile().getName();
				if ((fil = BuildGdx.compat.open(file)) != null) {
					demofiles.add(name);
					fil.close();
				}
			}
		}

		if (demofiles.size() != 0)
			Collections.sort(demofiles);
		Console.Println("There are " + demofiles.size() + " demo(s) in the loop", OSDTEXT_GOLD);
	}

	public static void DemoWriteBuffer()
	{

	}
}
