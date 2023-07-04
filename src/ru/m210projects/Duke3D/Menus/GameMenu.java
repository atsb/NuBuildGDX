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

package ru.m210projects.Duke3D.Menus;

import static ru.m210projects.Duke3D.Main.gGameScreen;
import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Build.Net.Mmulti.numplayers;
import static ru.m210projects.Duke3D.Factory.DukeMenuHandler.*;
import static ru.m210projects.Duke3D.Names.*;
import static ru.m210projects.Duke3D.Globals.*;

import ru.m210projects.Build.Pattern.MenuItems.BuildMenu;
import ru.m210projects.Build.Pattern.MenuItems.MenuButton;
import ru.m210projects.Build.Pattern.MenuItems.MenuHandler;
import ru.m210projects.Build.Pattern.MenuItems.MenuItem;
import ru.m210projects.Build.Pattern.MenuItems.MenuPicnum;
import ru.m210projects.Build.Pattern.MenuItems.MenuProc;
import ru.m210projects.Duke3D.Main;
import ru.m210projects.Duke3D.Factory.DukeMenuHandler;

public class GameMenu extends BuildMenu {

	public GameMenu(final Main app)
	{
		final DukeMenuHandler menu = app.menu;
		MenuPicnum bLogo = new MenuPicnum(app.pEngine, 160, 28, INGAMEDUKETHREEDEE, INGAMEDUKETHREEDEE, 65536) {
			@Override
			public void draw(MenuHandler handler) {
				draw.rotatesprite(x << 16, y << 16, 65536, 0, nTile, 0, 0, 10, 0, 0, xdim - 1, ydim - 1);
				if (currentGame.getCON().PLUTOPAK)
					draw.rotatesprite((x + 100)<<16,(y + 8)<<16,65536,0,PLUTOPAKSPRITE+2,(sintable[(totalclock<<4)&2047]>>11),0,2+8,0,0,xdim-1,ydim-1);
			}
		};
		
		int posy = 40;

		MenuButton NewGame = new MenuButton("New Game", app.getFont(2), 0, posy += 16, 320, 1, 0, menu.mMenus[NEWGAME], -1, null, 0) {
			@Override
			public void open() {
				nextMenu = menu.mMenus[NEWGAME];
				if (numplayers > 1 || mFakeMultiplayer)
					nextMenu = menu.mMenus[NETWORKGAME];
			}
		};
		
		MenuProc mScreenCapture = new MenuProc() {
			@Override
			public void run( MenuHandler handler, MenuItem pItem ) {
				gGameScreen.capture(160, 100);
			}
		};
		MenuButton Save = new MenuButton("Save Game", app.getFont(2), 0, posy += 16, 320, 1, 0, menu.mMenus[SAVEGAME], -1, mScreenCapture, 0) {
			@Override
			public void draw(MenuHandler handler) {
				mCheckEnableItem(numplayers < 2 && !mFakeMultiplayer);
				super.draw(handler);
			}
		};
		MenuButton Load = new MenuButton("Load Game", app.getFont(2), 0, posy += 16, 320, 1, 0, menu.mMenus[LOADGAME], -1, null, 0) {
			@Override
			public void draw(MenuHandler handler) {
				mCheckEnableItem(numplayers < 2 && !mFakeMultiplayer);
				super.draw(handler);
			}
		};
		MenuButton Options = new MenuButton("Options", app.getFont(2), 0, posy += 16, 320, 1, 0, menu.mMenus[OPTIONS], -1, null, 0);
		MenuButton Help = new MenuButton("Help", app.getFont(2), 0, posy += 16, 320, 1, 0, menu.mMenus[HELP], -1, null, 0);
		MenuButton QuitTitle = new MenuButton("Quit to title", app.getFont(2), 0, posy += 16, 320, 1, 0, menu.mMenus[QUITTITLE], -1, null, 0);
		MenuButton Quit = new MenuButton("Quit game", app.getFont(2), 0, posy += 16, 320, 1, 0, menu.mMenus[QUIT], -1, null, 0);
	
		addItem(bLogo, false);
		addItem(NewGame, true);
		addItem(Save, false);
		addItem(Load, false);
		addItem(Options, false);
		addItem(Help, false);
		addItem(QuitTitle, false);
		addItem(Quit, false);
	}
}
