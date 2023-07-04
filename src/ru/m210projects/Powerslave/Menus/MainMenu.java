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

import ru.m210projects.Build.Pattern.MenuItems.*;
import ru.m210projects.Powerslave.Main;
import ru.m210projects.Powerslave.Factory.PSMenuHandler;
import static ru.m210projects.Powerslave.Factory.PSMenuHandler.*;
import static ru.m210projects.Powerslave.Main.*;

public class MainMenu extends BuildMenu {
	
	public MainMenu(final Main app)
	{
		final PSMenuHandler menu = (PSMenuHandler) app.menu;
		
		MenuTitle mTitle = new PSTitle(160, 35, 65536);
		
		int posy = 45;
		MenuButton NewGame = new PSButton("New Game", 0, posy += 20, 320, 1, 0, menu.mMenus[NEWGAME] = new NewGame(app), -1, null, 0);

		MenuButton LoadGame = new PSButton("Load Game", 0, posy += 20, 320, 1, 0, menu.mMenus[LOAD] = new PSMenuLoad(app), -1, null, 0);

		MenuButton Options = new PSButton("Options", 0, posy += 20, 320, 1, 0, menu.mMenus[OPTIONS] = new MenuOptions(app), -1, null, 0);

		MenuButton cutscene = new PSButton("Show cutscene", 0, posy += 20, 320, 1, 0, null, -1, new MenuProc() {
			@Override
			public void run(MenuHandler handler, MenuItem pItem) {
				if (gMovieScreen.init("book.mov"))
					game.changeScreen(gMovieScreen.setCallback(game.toLogo3).escSkipping(true));
				else
					game.changeScreen(gCinemaScreen.setNum(2).setSkipping(game.toLogo3));
			}
		}, 0);

		MenuButton Quit = new PSButton("Quit", 0, posy += 20, 320, 1, 0, menu.mMenus[QUIT] = new MenuQuit(app), -1, null, 0);

		addItem(mTitle, false);

		addItem(NewGame, true);
		addItem(LoadGame, false);
		addItem(Options, false);
		addItem(cutscene, false);
		addItem(Quit, false);
	}

}
