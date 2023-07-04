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

import static ru.m210projects.Powerslave.Factory.PSMenuHandler.QUITTITLE;
import static ru.m210projects.Powerslave.Globals.levelnum;
import static ru.m210projects.Powerslave.Globals.nBestLevel;
import static ru.m210projects.Powerslave.Main.*;

import ru.m210projects.Build.Pattern.MenuItems.BuildMenu;
import ru.m210projects.Build.Pattern.MenuItems.MenuButton;
import ru.m210projects.Build.Pattern.MenuItems.MenuHandler;
import ru.m210projects.Build.Pattern.MenuItems.MenuItem;
import ru.m210projects.Build.Pattern.MenuItems.MenuProc;
import ru.m210projects.Build.Pattern.MenuItems.MenuTitle;
import ru.m210projects.Powerslave.Main;
import ru.m210projects.Powerslave.Factory.PSMenuHandler;

public class MenuEndGame extends BuildMenu {
	
	public MenuEndGame(final Main app)
	{
		final PSMenuHandler menu = (PSMenuHandler) app.menu;
		
		MenuTitle mTitle = new PSTitle("End game", 160, 15, 0);
		
		MenuProc mShowMap = new MenuProc() {
			@Override
			public void run( MenuHandler handler, MenuItem pItem ) {
				gMap.showMap(levelnum, levelnum, nBestLevel);
			}
		};
		
		int posy = 60;
		MenuButton Map = new PSButton("Choose new map", 0, posy += 20, 320, 1, 0, null, -1, mShowMap, 0);
		MenuButton Title = new PSButton("Quit to title", 0, posy += 20, 320, 1, 0, menu.mMenus[QUITTITLE] = new MenuQTitle(app), -1, null, 0);

		addItem(mTitle, false);
		addItem(Map, true);
		addItem(Title, false);	
	}

}
