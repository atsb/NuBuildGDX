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

package ru.m210projects.Powerslave.Screens;

import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Build.Gameutils.BClipHigh;
import static ru.m210projects.Build.Gameutils.BClipLow;
import static ru.m210projects.Build.Gameutils.BClipRange;
import static ru.m210projects.Build.Net.Mmulti.connecthead;
import static ru.m210projects.Build.Net.Mmulti.connectpoint2;
import static ru.m210projects.Build.OnSceenDisplay.Console.OSDTEXT_GOLD;
import static ru.m210projects.Build.OnSceenDisplay.Console.OSDTEXT_RED;
import static ru.m210projects.Powerslave.Factory.PSMenuHandler.*;
import static ru.m210projects.Powerslave.Globals.*;
import static ru.m210projects.Powerslave.Main.*;
import static ru.m210projects.Powerslave.Sound.*;
import static ru.m210projects.Powerslave.Weapons.*;
import static ru.m210projects.Powerslave.Player.*;
import static ru.m210projects.Powerslave.Random.InitRandom;
import static ru.m210projects.Powerslave.LoadSave.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.FileHandle.FileEntry;
import ru.m210projects.Build.FileHandle.Compat.Path;
import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Build.Pattern.BuildControls;
import ru.m210projects.Build.Pattern.BuildNet;
import ru.m210projects.Build.Pattern.BuildGame.NetMode;
import ru.m210projects.Build.Settings.BuildConfig.GameKeys;
import ru.m210projects.Powerslave.Config.PsKeys;
import ru.m210projects.Powerslave.Main;
import ru.m210projects.Powerslave.Factory.PSMenuHandler;
import ru.m210projects.Powerslave.Type.DemoFile;
import ru.m210projects.Powerslave.Type.Input;

public class DemoScreen extends GameScreen {

	private int lockclock;
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
		
		playCDtrack(19, true);
	}
	
	public boolean showDemo(String name)
	{
		demfile = null;
		try {
			demfile = new DemoFile(name);
		} catch(Exception e) {
			Console.Println("Can't play the demo file: " + name, OSDTEXT_RED);
			return false;
		}
		
		game.nNetMode = NetMode.Single;
		levelnew = demfile.level;
		nPlayerWeapons[nLocalPlayer] = demfile.weapons;
		nPlayerClip[nLocalPlayer] = demfile.clip;
		nPlayerItem[nLocalPlayer] = demfile.items;
		nPistolClip[nLocalPlayer] = (short) Math.min(PlayerList[nLocalPlayer].AmmosAmount[1], 6);
		PlayerList[nLocalPlayer].copy(demfile.player);
		nPlayerLives[nLocalPlayer] = demfile.lives;
		SetPlayerItem(nLocalPlayer, nPlayerItem[nLocalPlayer]);
		CheckClip(nLocalPlayer);
		gClassicMode = true;
		
		InitRandom();
		InitPlayerInventory(GrabPlayer());
		
		PlayerCount = 1;
		if(levelnew == 0) 
			gCurrentEpisode = gTrainingEpisode;
		else {
			gCurrentEpisode = gOriginalEpisode;
			levelnew--;
		}
		
		boardfilename = gCurrentEpisode.gMapInfo.get(levelnew).path;
		loadboard(boardfilename, null).setTitle("Loading " + gCurrentEpisode.gMapInfo.get(levelnew).title);
		game.pInput.resetMousePos();
		bPlayback = true;
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
	public void KeyHandler() {
		pEngine.handleevents();
		
		PSMenuHandler menu = game.menu;
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
		
		if (input.ctrlGetInputKey(GameKeys.Shrink_Hud, true)) {
			if (cfg.nScreenSize > 0) {
				cfg.nScreenSize = BClipLow(cfg.nScreenSize - 1, 0);
			}
		} else if (input.ctrlGetInputKey(GameKeys.Enlarge_Hud, true)) {
			if (cfg.nScreenSize < 2) {
				cfg.nScreenSize = BClipHigh(cfg.nScreenSize + 1, 2);
			}
		} else if (input.ctrlGetInputKey(PsKeys.Show_LoadMenu, true)) {
			if (game.nNetMode == NetMode.Single) {
				menu.mOpen(menu.mMenus[LOAD], -1);
			}
		} else if (input.ctrlGetInputKey(PsKeys.Quit, true)) {
			menu.mOpen(menu.mMenus[QUIT], -1);
		} else if (input.ctrlGetInputKey(PsKeys.Show_SoundSetup, true)) {
			menu.mOpen(menu.mMenus[AUDIO], -1);
		} else if (input.ctrlGetInputKey(PsKeys.Show_Options, true)) {
			menu.mOpen(menu.mMenus[OPTIONS], -1);
		} else if (input.ctrlGetInputKey(PsKeys.Gamma, true)) {
			menu.mOpen(menu.mMenus[COLORCORR], -1);
		} else if (input.ctrlGetInputKey(PsKeys.Make_Screenshot, true)) {
			makeScreenshot();
		} 
	}
	
	@Override
	public void render(float delta) {
		KeyHandler();
		
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
			while (totalclock >= (lockclock + 4)) {
				Input pInput = demfile.ReadPlaybackInput();
				if(pInput != null) {
					pNet.gFifoInput[pNet.gNetFifoHead[nLocalPlayer] & 0xFF][nLocalPlayer].Copy(pInput);
					Ra[nLocalPlayer].nTarget = besttarget = pInput.nTarget;
					pNet.gNetFifoHead[nLocalPlayer]++;
				} else {
					if(!showDemo()) {
						bPlayback = false;
						game.changeScreen(gMenuScreen);
					}
					return;
				}

				engine.updatesmoothticks();
				game.pInt.clearinterpolations();
				while(moveframes > 0) {
					ProcessFrame(pNet);
					lockclock += 4;
				}
				pNet.gNetFifoTail++;
			}
		} else lockclock = totalclock;
	}
	
	@Override
	protected void UpdateInputs(BuildNet net)
	{
		for (short i = connecthead; i >= 0; i = connectpoint2[i]) {
			Input src = (Input) net.gFifoInput[net.gNetFifoTail & 0xFF][i];
			sPlayerInput[i].Copy(src);
		}
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
			nDemonum = BClipRange(nextnum, 0, demofiles.size() - 1);
			break;
		}

		if(demofiles != null && demofiles.size() > 0) 
			return showDemo(demofiles.get(nDemonum));
		
		return false;
	}
	
	public void demoscan() {
		for (Iterator<FileEntry> it = BuildGdx.compat.getDirectory(Path.Game).getFiles().values().iterator(); it.hasNext();) {
			FileEntry file = it.next();
			if (file.getExtension().equals("vcr")) {
				String name = file.getFile().getName();
				demofiles.add(name);
			}
		}
		
		if (demofiles.size() != 0)
			Collections.sort(demofiles);
		Console.Println("There are " + demofiles.size() + " demo(s) in the loop", OSDTEXT_GOLD);
	}
	
	@Override
	public boolean isOriginal()
	{
		return true;
	}

}
