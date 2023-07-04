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

package ru.m210projects.Powerslave.Menus;

import ru.m210projects.Build.Pattern.MenuItems.BuildMenu;
import ru.m210projects.Build.Pattern.MenuItems.MenuButton;
import ru.m210projects.Build.Pattern.MenuItems.MenuHandler;
import ru.m210projects.Build.Pattern.MenuItems.MenuItem;
import ru.m210projects.Build.Pattern.MenuItems.MenuProc;
import ru.m210projects.Build.Pattern.MenuItems.MenuTitle;
import ru.m210projects.Powerslave.Main;
import ru.m210projects.Powerslave.Factory.PSMenuHandler;
import ru.m210projects.Powerslave.Main.UserFlag;

import static ru.m210projects.Powerslave.Main.*;
import static ru.m210projects.Powerslave.LoadSave.*;
import static ru.m210projects.Powerslave.Globals.*;
import static ru.m210projects.Build.Net.Mmulti.numplayers;
import static ru.m210projects.Powerslave.Factory.PSMenuHandler.*;

public class MenuGame extends BuildMenu {
	
	public MenuGame(final Main app)
	{
		final PSMenuHandler menu = (PSMenuHandler) app.menu;
		
		MenuTitle mTitle = new PSTitle(160, 35, 65536);
		
		int posy = 40;
		MenuButton NewGame = new PSButton("New Game", 0, posy += 20, 320, 1, 0, menu.mMenus[NEWGAME], -1, null, 0);

		MenuButton LoadGame = new PSButton("Load Game", 0, posy += 20, 320, 1, 0, menu.mMenus[LOAD], -1, null, 0);
		
		MenuProc mScreenCapture = new MenuProc() {
			@Override
			public void run( MenuHandler handler, MenuItem pItem ) {
				gGameScreen.capture(160, 100);
			}
		};
		MenuButton SaveGame = new PSButton("Save Game", 0, posy += 20, 320, 1, 0, menu.mMenus[SAVE] = new PSMenuSave(app), -1, mScreenCapture, 0) {
			@Override
			public void open() {
				mCheckEnableItem(numplayers < 2 && !gClassicMode);
			}
			
			@Override
			public void mCheckEnableItem(boolean nEnable) {
				if (nEnable) 
					flags = 3 | 4;
				else flags = 2;
			}
		};

		MenuButton Options = new PSButton("Options", 0, posy += 20, 320, 1, 0, menu.mMenus[OPTIONS], -1, null, 0);
		
		MenuButton Title = new PSButton("End game", 0, posy += 20, 320, 1, 0, menu.mMenus[ENDGAME] = new MenuEndGame(app), -1, null, 0) {
			@Override
			public void open() {
				if(gCurrentEpisode == gTrainingEpisode || mUserFlag == UserFlag.Addon)
				{
					nextMenu = menu.mMenus[QUITTITLE];
					text = "Quit to title".toCharArray();
				} else {
					nextMenu = menu.mMenus[ENDGAME];
					text = "End game".toCharArray();
				}
			}
		};

		MenuButton Quit = new PSButton("Quit", 0, posy += 20, 320, 1, 0, menu.mMenus[QUIT], -1, null, 0);

		addItem(mTitle, false);
		
		addItem(NewGame, true);
		addItem(LoadGame, false);
		addItem(SaveGame, false);
		addItem(Options, false);
		addItem(Title, false);
		addItem(Quit, false);
	}

}
