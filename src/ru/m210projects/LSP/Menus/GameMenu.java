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

package ru.m210projects.LSP.Menus;

import static ru.m210projects.Build.Engine.xdim;
import static ru.m210projects.Build.Engine.ydim;
import static ru.m210projects.Build.Net.Mmulti.numplayers;
import static ru.m210projects.LSP.Factory.LSPMenuHandler.*;
import static ru.m210projects.LSP.GdxResources.*;
import static ru.m210projects.LSP.Main.*;

import ru.m210projects.Build.Pattern.MenuItems.BuildMenu;
import ru.m210projects.Build.Pattern.MenuItems.MenuButton;
import ru.m210projects.Build.Pattern.MenuItems.MenuHandler;
import ru.m210projects.Build.Pattern.MenuItems.MenuItem;
import ru.m210projects.Build.Pattern.MenuItems.MenuPicnum;
import ru.m210projects.Build.Pattern.MenuItems.MenuProc;
import ru.m210projects.LSP.Main;
import ru.m210projects.LSP.Factory.LSPMenuHandler;

public class GameMenu extends BuildMenu {

	public GameMenu(final Main app) {
		LSPMenuHandler menu = (LSPMenuHandler) app.pMenu;

		int posx = 0;
		int posy = 10;

		MenuPicnum mLogo = new MenuPicnum(app.pEngine, 160, posy += 20, LOGO, LOGO, 0x10000) {
			@Override
			public void draw(MenuHandler handler) {
				draw.rotatesprite(x << 16, y << 16, nScale, 0, nTile, 0, 0, 10, 0, 0, xdim - 1, ydim - 1);
			}
		};

		MenuProc newgame = new MenuProc() {
			@Override
			public void run(MenuHandler handler, MenuItem pItem) {
				gGameScreen.newgame(0);
			}
		};

		MenuButton bNewgame = new MenuButton("New Game", app.getFont(2), posx, posy += 30, 320, 1, 0, null, -1, newgame,
				0);
		MenuButton bOptions = new MenuButton("Options", app.getFont(2), posx, posy += 14, 320, 1, 0,
				menu.mMenus[OPTIONS], -1, null, 0);
		MenuButton bLoad = new MenuButton("Load Game", app.getFont(2), posx, posy += 14, 320, 1, 0,
				menu.mMenus[LOADGAME], -1, null, 0);

		MenuProc mScreenCapture = new MenuProc() {
			@Override
			public void run(MenuHandler handler, MenuItem pItem) {
				gGameScreen.capture(160, 100);
			}
		};

		MenuButton bSave = new MenuButton("SAVE GAME", app.getFont(2), 0, posy += 14, 320, 1, 0, menu.mMenus[SAVEGAME],
				-1, mScreenCapture, 0) {
			@Override
			public void open() {
				mCheckEnableItem(numplayers < 2);
			}
		};

		MenuButton bTitle = new MenuButton("Quit to title", app.getFont(2), 0, posy += 14, 320, 1, 0,
				new LSPMenuQTitle(app), -1, null, 0);

		MenuButton bQuit = new MenuButton("Quit", app.getFont(2), posx, posy += 14, 320, 1, 0, menu.mMenus[QUIT], -1,
				null, 0);

		addItem(mLogo, false);
		addItem(bNewgame, true);
		addItem(bOptions, false);
		addItem(bLoad, false);
		addItem(bSave, false);
		addItem(bTitle, false);
		addItem(bQuit, false);
	}
}
