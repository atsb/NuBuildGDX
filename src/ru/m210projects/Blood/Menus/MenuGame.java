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

package ru.m210projects.Blood.Menus;

import static ru.m210projects.Blood.Factory.BloodMenuHandler.*;
import static ru.m210projects.Blood.Globals.kFakeMultiplayer;
import static ru.m210projects.Blood.VERSION.SHAREWARE;
import static ru.m210projects.Build.Net.Mmulti.numplayers;
import static ru.m210projects.Blood.Main.gGameScreen;

import ru.m210projects.Blood.Main;
import ru.m210projects.Blood.Factory.BloodMenuHandler;
import ru.m210projects.Build.Pattern.MenuItems.BuildMenu;
import ru.m210projects.Build.Pattern.MenuItems.MenuButton;
import ru.m210projects.Build.Pattern.MenuItems.MenuHandler;
import ru.m210projects.Build.Pattern.MenuItems.MenuItem;
import ru.m210projects.Build.Pattern.MenuItems.MenuProc;
import ru.m210projects.Build.Pattern.MenuItems.MenuTitle;

public class MenuGame extends BuildMenu {

	public MenuGame(final Main app)
	{
		final BloodMenuHandler menu = app.menu;
		
		MenuTitle BloodTitle = new MenuTitle(app.pEngine, "Blood", app.getFont(1), 160, 20, 2038);

		int posy = 25;

		MenuButton NewGame = new MenuButton("New Game", app.getFont(1), 0, posy += 15, 320, 1, 0, menu.mMenus[NEWGAME], -1, null, 0) {
			@Override
			public void open() {
				nextMenu = menu.mMenus[NEWGAME];
				if (numplayers > 1 || kFakeMultiplayer)
					nextMenu = menu.mMenus[NETWORKGAME];
			}
		};
		NewGame.fontShadow = true;
		
		
		MenuButton Options = new MenuButton("Options", app.getFont(1), 0, posy += 15, 320, 1, 0, menu.mMenus[OPTIONS], -1, null, 0);
		Options.fontShadow = true;
		
		MenuProc mScreenCapture = new MenuProc() {
			@Override
			public void run( MenuHandler handler, MenuItem pItem ) {
				gGameScreen.capture(320, 200);
			}
		};
		MenuButton Save = new MenuButton("Save Game", app.getFont(1), 0, posy += 15, 320, 1, 0, menu.mMenus[SAVEGAME] = new BLMenuSave(app), -1, mScreenCapture, 0) {
			@Override
			public void open() {
				mCheckEnableItem(numplayers < 2 && !kFakeMultiplayer);
			}
		};
		Save.fontShadow = true;
		MenuButton Load = new MenuButton("Load Game", app.getFont(1), 0, posy += 15, 320, 1, 0, menu.mMenus[LOADGAME], -1, null, 0) {
			@Override
			public void open() {
				mCheckEnableItem(numplayers < 2 && !kFakeMultiplayer);
			}
		};
		Load.fontShadow = true;
		MenuButton Help = null;
		if (!SHAREWARE) {
			Help = new MenuButton("Help", app.getFont(1), 0, posy += 15, 320, 1, 0, menu.mMenus[HELP], -1, null, 0);
			Help.fontShadow = true;
		}
		MenuButton Credits = new MenuButton("Credits", app.getFont(1), 0, posy += 15, 320, 1, 0, menu.mMenus[CREDITS], -1, null, 0);
		Credits.fontShadow = true;
		MenuButton bTitle = new MenuButton("Quit to title", app.getFont(1), 0, posy += 15, 320, 1, 0, menu.mMenus[QUITTITLE] = new MenuQTitle(app), -1, null, 0);
		bTitle.fontShadow = true;
		MenuButton Quit = new MenuButton("Quit game", app.getFont(1), 0, posy += 15, 320, 1, 0, menu.mMenus[QUIT], -1, null, 0);
		Quit.fontShadow = true;

		addItem(BloodTitle, false);
		addItem(NewGame, true);
		addItem(Options, false);
		addItem(Save, false);
		addItem(Load, false);
		if (Help != null)
			addItem(Help, false);
		addItem(Credits, false);
		addItem(bTitle, false);
		addItem(Quit, false);
		addItem(menu.addMenuBlood(), false);
	}
}
