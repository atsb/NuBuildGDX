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

package ru.m210projects.LSP.Screens;

import static ru.m210projects.Build.Engine.totalclock;
import static ru.m210projects.Build.Gameutils.BClipRange;
import static ru.m210projects.Build.Net.Mmulti.connecthead;
import static ru.m210projects.Build.Net.Mmulti.connectpoint2;
import static ru.m210projects.Build.Net.Mmulti.myconnectindex;
import static ru.m210projects.Build.Net.Mmulti.numplayers;
import static ru.m210projects.Build.OnSceenDisplay.Console.OSDTEXT_GOLD;
import static ru.m210projects.Build.OnSceenDisplay.Console.OSDTEXT_RED;
import static ru.m210projects.LSP.Factory.LSPMenuHandler.AUDIOSET;
import static ru.m210projects.LSP.Factory.LSPMenuHandler.COLORCORR;
import static ru.m210projects.LSP.Factory.LSPMenuHandler.LOADGAME;
import static ru.m210projects.LSP.Factory.LSPMenuHandler.MAIN;
import static ru.m210projects.LSP.Factory.LSPMenuHandler.OPTIONS;
import static ru.m210projects.LSP.Factory.LSPMenuHandler.QUIT;
import static ru.m210projects.LSP.Globals.TICSPERFRAME;
import static ru.m210projects.LSP.Globals.lockclock;
import static ru.m210projects.LSP.Globals.nDifficult;
import static ru.m210projects.LSP.Globals.recstat;
import static ru.m210projects.LSP.LoadSave.lastload;
import static ru.m210projects.LSP.Main.engine;
import static ru.m210projects.LSP.Main.gDemoScreen;
import static ru.m210projects.LSP.Main.gMenuScreen;
import static ru.m210projects.LSP.Main.gPrecacheScreen;

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
import ru.m210projects.Build.Pattern.BuildGame.NetMode;
import ru.m210projects.Build.Settings.BuildConfig.GameKeys;
import ru.m210projects.LSP.Config.LSPKeys;
import ru.m210projects.LSP.Main;
import ru.m210projects.LSP.Factory.LSPMenuHandler;
import ru.m210projects.LSP.Types.DemoFile;

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
		} catch(Exception e) {}
		
		if(demfile == null || demfile.reccnt == 0)
		{
			Console.Println("Can't play the demo file: " + name, OSDTEXT_RED);
			return false;
		}
		
		if (numplayers > 1)
			game.pNet.NetDisconnect(myconnectindex);
		
		nDifficult = (short) demfile.skill;

		recstat = 2;

		gDemoScreen.newgame(demfile.map);
		
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
				lockclock = 0;
				pNet.ready2send = false;
			}
		});
		game.changeScreen(gPrecacheScreen);
	}
	
	@Override
	public void render(float delta) {
		KeyHandler();

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
	
	@Override
	public void KeyHandler() {
		pEngine.handleevents();

		LSPMenuHandler menu = game.menu;
		if (menu.gShowMenu) {
			engine.handleevents();
			menu.mKeyHandler(game.pInput, BuildGdx.graphics.getDeltaTime());
			return;
		}

		if (Console.IsShown())
			return;

		BuildControls input = game.pInput;
		if (input.ctrlGetInputKey(GameKeys.Menu_Toggle, true))
			menu.mOpen(menu.mMenus[MAIN], -1);

		if (input.ctrlGetInputKey(LSPKeys.Show_Loadmenu, true)) {
			if (game.nNetMode == NetMode.Single) {
				menu.mOpen(menu.mMenus[LOADGAME], -1);
			}
		} else if (input.ctrlGetInputKey(LSPKeys.Quit, true)) {
			menu.mOpen(menu.mMenus[QUIT], -1);
		} else if (input.ctrlGetInputKey(LSPKeys.Show_Options, true)) {
			menu.mOpen(menu.mMenus[OPTIONS], -1);
		} else if (input.ctrlGetInputKey(LSPKeys.Show_Sounds, true)) {
			menu.mOpen(menu.mMenus[AUDIOSET], -1);
		} else if (input.ctrlGetInputKey(LSPKeys.Gamma, true)) {
			menu.mOpen(menu.mMenus[COLORCORR], -1);
		} else if (input.ctrlGetInputKey(LSPKeys.Make_Screenshot, true)) {
			makeScreenshot();
		} 

	}
	
	private void DemoRender() { 
		pNet.ready2send = false;
		
		if(!game.isCurrentScreen(this))
			return;
		
		if(!game.gPaused && demfile != null) {
			while (totalclock >= (lockclock + TICSPERFRAME)) {
				for (int j = connecthead; j >= 0; j = connectpoint2[j]) {
					pNet.gFifoInput[pNet.gNetFifoHead[j] & 0xFF][j].Copy(demfile.recsync[demfile.rcnt][j]);
					pNet.gNetFifoHead[j]++;
					demfile.reccnt--;
				}
				
				if (demfile.reccnt <= 0) {
					if(!showDemo())
						game.changeScreen(gMenuScreen);
					return;
				}
				
				demfile.rcnt++;
				engine.updatesmoothticks();
				game.pInt.clearinterpolations();
			
				ProcessFrame(pNet);

//				if(game.pInput.ctrlKeyStatusOnce(Keys.BACKSPACE))
//				{
//					System.err.println("rcnt " + demfile.rcnt + " clock " + lockclock);
//					System.err.println(engine.getrand());	
//				}
			}
		} else lockclock = totalclock;
	}

	public boolean showDemo()
	{
//		switch(cfg.gDemoSeq)
//		{
//		case 0: //OFF
//			return false;
//		case 1: //Consistently
//			if (nDemonum < (demofiles.size() - 1))
//				nDemonum++;
//			else
//				nDemonum = 0;
//			break;
//		case 2: //Accidentally
//			int nextnum = nDemonum;
//			if(demofiles.size() > 1) {
//				while(nextnum == nDemonum) 
//					nextnum = (int) (Math.random() * (demofiles.size()));
//			}
//			nDemonum = nextnum;
//			break;
//		}
		
		if (nDemonum < (demofiles.size() - 1))
			nDemonum++;
		else
			nDemonum = 0;
		
		if(demofiles != null && demofiles.size() > 0) 
			return showDemo(demofiles.get(nDemonum), null);
		
		return false;
	}
	
	public void demoscan() {
		byte[] buf = new byte[4];

		Resource fil = null;
		for (Iterator<FileEntry> it = BuildGdx.compat.getDirectory(Path.Game).getFiles().values().iterator(); it.hasNext();) {
			FileEntry file = it.next();
			if (file.getExtension().equals("dmo")) {
				String name = file.getFile().getName();
				if ((fil = BuildGdx.compat.open(file)) != null) {
					fil.read(buf, 0, 4);
					fil.read(buf, 0, 1);
					int version = buf[0] & 0xFF;
					if (version == 100)
						demofiles.add(name);
					fil.close();
				}
			}
		}

		if (demofiles.size() != 0)
			Collections.sort(demofiles);
		Console.Println("There are " + demofiles.size() + " demo(s) in the loop", OSDTEXT_GOLD);
	}

}
