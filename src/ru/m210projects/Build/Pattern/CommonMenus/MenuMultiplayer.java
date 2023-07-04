//This file is part of BuildGDX.
//Copyright (C) 2017-2018  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
//BuildGDX is free software: you can redistribute it and/or modify
//it under the terms of the GNU General Public License as published by
//the Free Software Foundation, either version 3 of the License, or
//(at your option) any later version.
//
//BuildGDX is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU General Public License for more details.
//
//You should have received a copy of the GNU General Public License
//along with BuildGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.Build.Pattern.CommonMenus;

import ru.m210projects.Build.Pattern.BuildFont;
import ru.m210projects.Build.Pattern.BuildGame;
import ru.m210projects.Build.Pattern.MenuItems.BuildMenu;
import ru.m210projects.Build.Pattern.MenuItems.MenuButton;
import ru.m210projects.Build.Pattern.MenuItems.MenuTitle;

public abstract class MenuMultiplayer extends BuildMenu {
	
	public MenuButton mCreate;
	public MenuButton mJoin;

	public MenuMultiplayer(BuildGame app, int posx, int posy, int menuHeight, BuildFont style)
	{
		addItem(getTitle(app, "Multiplayer"), false);

		int pos = posy;
		mCreate = new MenuButton("New game", style, 0, pos += menuHeight, 320, 1, 0, getMenuCreate(app), -1, null, 0);
		mJoin = new MenuButton("Join a game", style, 0, pos + menuHeight, 320, 1, 0, getMenuJoin(app), -1, null, 0);
		// splitscreen game
		// end game

		addItem(mCreate, true);
		addItem(mJoin, false);
	}
	
	public abstract MenuTitle getTitle(BuildGame app, String text);
	
	public abstract BuildMenu getMenuCreate(BuildGame app);
	
	public abstract BuildMenu getMenuJoin(BuildGame app);

}
