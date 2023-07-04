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

import ru.m210projects.Blood.Factory.BloodMenuHandler;
import ru.m210projects.Build.Pattern.BuildGame;
import ru.m210projects.Build.Pattern.CommonMenus.MenuMultiplayer;
import ru.m210projects.Build.Pattern.MenuItems.BuildMenu;
import ru.m210projects.Build.Pattern.MenuItems.MenuTitle;

public class BLMenuMultiplayer extends MenuMultiplayer {

	public BLMenuMultiplayer(BuildGame app) {
		super(app, 0, 45, 20, app.getFont(1));
		
		mCreate.fontShadow = true;
		mJoin.fontShadow = true;
		
		addItem(((BloodMenuHandler) app.pMenu).addMenuBlood(), false);
	}

	@Override
	public MenuTitle getTitle(BuildGame app, String text) {
		return new MenuTitle(app.pEngine, text, app.getFont(1), 160, 20, 2038);
	}

	@Override
	public BuildMenu getMenuCreate(BuildGame app) {
		return new BLMenuCreate(app);
	}

	@Override
	public BuildMenu getMenuJoin(BuildGame app) {
		return new BLMenuJoin(app);
	}
}
