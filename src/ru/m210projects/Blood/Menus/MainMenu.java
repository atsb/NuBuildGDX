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
import static ru.m210projects.Blood.Globals.*;
import static ru.m210projects.Blood.VERSION.*;
import static ru.m210projects.Build.Net.Mmulti.*;
import static ru.m210projects.Build.Strhandler.toCharArray;

import ru.m210projects.Blood.Main;
import ru.m210projects.Blood.Factory.BloodMenuHandler;
import ru.m210projects.Build.Pattern.MenuItems.BuildMenu;
import ru.m210projects.Build.Pattern.MenuItems.MenuButton;
import ru.m210projects.Build.Pattern.MenuItems.MenuHandler;
import ru.m210projects.Build.Pattern.MenuItems.MenuTitle;

public class MainMenu extends BuildMenu {

	public MainMenu(final Main app)
	{
		final BloodMenuHandler menu = app.menu;
		
		MenuTitle BloodTitle = new MenuTitle(app.pEngine, SHAREWARE ? "Blood Demo" : "Blood", app.getFont(1), 160, 20, 2038);

		int posy = 25;

		MenuButton NewGame = new MenuButton("New Game", app.getFont(1), 0, posy += 20, 320, 1, 0, menu.mMenus[NEWGAME] = new MenuNewGame(app), -1, null, 0);
		NewGame.fontShadow = true;
		MenuButton Multiplayer = new MenuButton("Multiplayer", app.getFont(1), 0, posy += 20, 320, 1, 0, null, -1, null, 0) {
			@Override
			public void draw(MenuHandler handler) {
				if (numplayers > 1 || kFakeMultiplayer)
					nextMenu = menu.mMenus[NETWORKGAME];
				else
					nextMenu = menu.mMenus[MULTIPLAYER];
				super.draw(handler);
			}
		};
		Multiplayer.fontShadow = true;
		MenuButton Options = new MenuButton("Options", app.getFont(1), 0, posy += 20, 320, 1, 0, menu.mMenus[OPTIONS] = new MenuOptions(app), -1, null, 0);
		Options.fontShadow = true;
		MenuButton Load = new MenuButton("Load Game", app.getFont(1), 0, posy += 20, 320, 1, 0, menu.mMenus[LOADGAME] = new BLMenuLoad(app), -1, null, 0) {
			@Override
			public void draw(MenuHandler handler) {
				mCheckEnableItem(numplayers < 2 && !kFakeMultiplayer);
				super.draw(handler);
			}
		};
		Load.fontShadow = true;
		MenuButton Help = null;
		if (!SHAREWARE) {
			Help = new MenuButton("Help", app.getFont(1), 0, posy += 20, 320, 1, 0, menu.mMenus[HELP] = new MenuHelp(), -1, null, 0);
			Help.fontShadow = true;
		}
		MenuButton Credits = new MenuButton("Credits", app.getFont(1), 0, posy += 20, 320, 1, 0, menu.mMenus[CREDITS] = new MenuCredits(), -1, null, 0);
		Credits.fontShadow = true;
		
		MenuButton Quit = new MenuButton("Quit game", app.getFont(1), 0, posy += 20, 320, 1, 0, menu.mMenus[QUIT] = new MenuQuit(app), -1, null, 0) {
			@Override
			public void draw(MenuHandler handler) {
				if (numplayers > 1 || kFakeMultiplayer) {
					text = toCharArray("Disconnect");
					nextMenu = menu.mMenus[QUITTITLE];
				} else {
					text = toCharArray("Quit game");
					nextMenu = menu.mMenus[QUIT];
				}
				super.draw(handler);
			}
		};
		Quit.fontShadow = true;

		addItem(BloodTitle, false);
		addItem(NewGame, true);
		addItem(Multiplayer, false);
		addItem(Options, false);
		addItem(Load, false);
		if (Help != null)
			addItem(Help, false);
		addItem(Credits, false);
		addItem(Quit, false);
		addItem(menu.addMenuBlood(), false);
	}
}
