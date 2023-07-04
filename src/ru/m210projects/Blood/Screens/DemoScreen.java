// This file is part of BloodGDX.
// Copyright (C) 2017-2019  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// BloodGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// BloodGDX is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with BloodGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.Blood.Screens;

import static ru.m210projects.Blood.Factory.BloodMenuHandler.MAIN;
import static ru.m210projects.Blood.Factory.BloodMenuHandler.OPTIONS;
import static ru.m210projects.Blood.Factory.BloodMenuHandler.QUIT;
import static ru.m210projects.Blood.Factory.BloodMenuHandler.SOUNDSET;
import static ru.m210projects.Blood.Globals.defGameInfo;
import static ru.m210projects.Blood.Globals.gFullMap;
import static ru.m210projects.Blood.Globals.gInfiniteAmmo;
import static ru.m210projects.Blood.Globals.gNoClip;
import static ru.m210projects.Blood.Globals.kFakeMultiplayer;
import static ru.m210projects.Blood.Globals.kFrameTicks;
import static ru.m210projects.Blood.Globals.kMaxPlayers;
import static ru.m210projects.Blood.Globals.pGameInfo;
import static ru.m210projects.Blood.LEVELS.levelGetEpisode;
import static ru.m210projects.Blood.LOADSAVE.lastload;
import static ru.m210projects.Blood.Main.cfg;
import static ru.m210projects.Blood.Main.engine;
import static ru.m210projects.Blood.Main.gDemoScreen;
import static ru.m210projects.Blood.Main.gMenuScreen;
import static ru.m210projects.Blood.Main.gPrecacheScreen;
import static ru.m210projects.Blood.Main.game;
import static ru.m210projects.Blood.Types.DEMO.demfile;
import static ru.m210projects.Blood.Types.DEMO.demofiles;
import static ru.m210projects.Blood.Types.DEMO.nDemonum;
import static ru.m210projects.Blood.View.gViewIndex;
import static ru.m210projects.Blood.View.resetQuotes;
import static ru.m210projects.Blood.View.viewResizeView;
import static ru.m210projects.Build.Engine.totalclock;
import static ru.m210projects.Build.Gameutils.BClipRange;
import static ru.m210projects.Build.Net.Mmulti.connecthead;
import static ru.m210projects.Build.Net.Mmulti.connectpoint2;
import static ru.m210projects.Build.Net.Mmulti.myconnectindex;
import static ru.m210projects.Build.Net.Mmulti.numplayers;
import static ru.m210projects.Build.OnSceenDisplay.Console.OSDTEXT_RED;

import ru.m210projects.Blood.Config.BloodKeys;
import ru.m210projects.Blood.Main;
import ru.m210projects.Blood.Factory.BloodMenuHandler;
import ru.m210projects.Blood.Types.DEMO;
import ru.m210projects.Blood.Types.INPUT;
import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.FileHandle.Resource;
import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Build.Pattern.BuildControls;
import ru.m210projects.Build.Pattern.BuildGame.NetMode;
import ru.m210projects.Build.Settings.BuildConfig.GameKeys;

public class DemoScreen extends GameScreen {

	public DemoScreen(Main game) {
		super(game);
	}

	@Override
	public void show() {
		lastload = null;
		resetQuotes();
	}

	public boolean showDemo(String name, String ini)
	{
		Resource res = BuildGdx.cache.open(name, 0);
		if(res != null) {
			if(!res.getExtension().equals("dem")) {
				Console.Println("Wrong file format: " + name, OSDTEXT_RED);
				return false;
			}

			gInfiniteAmmo = false;
			gFullMap = false;
			gNoClip = false;
			kFakeMultiplayer = false;
			pGameInfo.copy(defGameInfo);
			if (numplayers > 1)
				game.pNet.NetDisconnect(myconnectindex);

			demfile = new DEMO(res);
			res.close();

			if(demfile.nInputCount == 0)
			{
				Console.Println("Can't play the demo file: " + name, OSDTEXT_RED);
				return false;
			}

			gViewIndex = demfile.nMyConnectIndex;
			connecthead = demfile.nConnectHead;
			System.arraycopy(demfile.connectPoints, 0, connectpoint2, 0, kMaxPlayers);

			gDemoScreen.newgame(false, levelGetEpisode(ini), pGameInfo.nEpisode, pGameInfo.nLevel, pGameInfo.nDifficulty, pGameInfo.nDifficulty, pGameInfo.nDifficulty, false);

			Console.Println("Playing demo " + name);

			return true;
		}

		Console.Println("Can't play the demo file: " + name, OSDTEXT_RED);
		return false;
	}

	@Override
	protected void startboard(final Runnable startboard)
	{
		gPrecacheScreen.init(false, new Runnable() {
			@Override
			public void run() {
				startboard.run(); //call faketimehandler
				pNet.ResetTimers(); //reset ototalclock
				pNet.ready2send = false;
			}
		});
		game.changeScreen(gPrecacheScreen);
	}

	@Override
	public void KeyHandler() {
		pEngine.handleevents();

		BloodMenuHandler menu = game.menu;
		if (menu.gShowMenu) {
			menu.mKeyHandler(game.pInput, BuildGdx.graphics.getDeltaTime());
			return;
		}

		if(Console.IsShown())
			return;

		BuildControls input = game.pInput;
		if (input.ctrlGetInputKey(GameKeys.Menu_Toggle, true))
			menu.mOpen(menu.mMenus[MAIN], -1);
		if (input.ctrlGetInputKey(BloodKeys.Show_SoundSetup, true))
			menu.mOpen(menu.mMenus[SOUNDSET], -1);

		if (input.ctrlGetInputKey(BloodKeys.Show_Options, true))
			menu.mOpen(menu.mMenus[OPTIONS], -1);

		if (input.ctrlGetInputKey(BloodKeys.Quit, true))
			menu.mOpen(menu.mMenus[QUIT], -1);

		if (input.ctrlGetInputKey(BloodKeys.Gamma, true))
			openGamma(menu);

		if (input.ctrlGetInputKey(BloodKeys.Make_Screenshot, true))
			makeScreenshot();

		if(input.ctrlGetInputKey(GameKeys.Shrink_Screen, true)) {
			viewResizeView(cfg.gViewSize + 1);
		}

		if(input.ctrlGetInputKey(GameKeys.Enlarge_Screen, true)) {
			viewResizeView(cfg.gViewSize - 1);
		}
	}

	@Override
	public void render(float delta) {
		KeyHandler();

		if (numplayers > 1) {
			pEngine.faketimerhandler();
			pNet.GetPackets();
		}

		DemoRender();

		float smoothratio = 65536;
		if (!game.gPaused) {
			smoothratio = pEngine.getsmoothratio();
			if (smoothratio < 0 || smoothratio > 0x10000) {
				smoothratio = BClipRange(smoothratio, 0, 0x10000);
			}
		}

		game.pInt.dointerpolations(smoothratio);
		DrawWorld(smoothratio); //smooth sprites

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

		while (!game.gPaused && totalclock >= pNet.ototalclock) {
			for (int i = connecthead; i >= 0; i = connectpoint2[i]) {
				INPUT pInput = demfile.pDemoInput[demfile.rcnt][i];
				if (pInput != null) {
					pNet.gFifoInput[pNet.gNetFifoHead[i] & 0xFF][i].Copy(pInput);
					pNet.gNetFifoHead[i]++;
				}
				if (++demfile.rcnt >= demfile.nInputCount) {
					demfile = null;

					if(!showDemo())
						game.changeScreen(gMenuScreen);
					return;
				}
			}

			engine.updatesmoothticks();
			game.pInt.clearinterpolations();
			pNet.ototalclock += kFrameTicks;

			ProcessFrame(pNet);
		}

//		if(game.pInput.ctrlKeyStatusOnce(Keys.BACKSPACE))
//		{
//			System.err.println("rcnt " + demfile.rcnt + " gGameClock " + totalclock);
//			System.err.println(Gameutils.bseed);

//			for(int id = 192; id <= 209; id++) {
//				System.err.println(id + " coord " + sprite[id].x + " " + sprite[id].y + " " + sprite[id].z + " " + sprite[id].sectnum + " " + sprite[id].ang);
//				System.err.println(id + " vel " + sprXVel[id] + " " + sprYVel[id] + " " + sprZVel[id]);
//				System.err.println(id + " aistate " + xsprite[sprite[id].extra].aiState.name);
//			}

//			int id = 895;
//			System.err.println(id + " coord " + sprite[id].x + " " + sprite[id].y + " " + sprite[id].z + " " + sprite[id].sectnum + " " + sprite[id].ang);
//			System.err.println(sprite[id]);
//			System.err.println(id + " vel " + sprXVel[id] + " " + sprYVel[id] + " " + sprZVel[id]);
//			if(sprite[id].extra != -1)
//				System.err.println(xsprite[sprite[id].extra].health + " " + xsprite[sprite[id].extra].aiState.name);

//			BPriorityQueue a = (BPriorityQueue) EVENT.eventQ;
//			System.out.println("fNodeCount " + a.fNodeCount + " " + Globals.gGameClock);
//			for(int i = 0; i < a.fNodeCount; i++)
//				System.out.println("qList[" + i + "] priority: " + a.qList[i].priority + "  event: " + a.qList[i].event);
//			System.out.println();
//		}
	}

	public boolean showDemo()
	{
		switch(cfg.gDemoSeq)
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

	@Override
	public void resume () {
		if (game.nNetMode == NetMode.Single && numplayers < 2) {
				game.gPaused = false;
			sndHandlePause(game.gPaused);
		}
		game.updateColorCorrection();
	}
}
