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

import ru.m210projects.Blood.Main;
import ru.m210projects.Build.Pattern.MenuItems.BuildMenu;
import ru.m210projects.Build.Pattern.MenuItems.MenuButton;
import ru.m210projects.Build.Pattern.MenuItems.MenuTitle;

public class MenuControls extends BuildMenu {

	public MenuControls(Main app)
	{
		MenuTitle mTitle = new MenuTitle(app.pEngine, "Controls Setup", app.getFont(1), 160, 20, 2038);

		addItem(mTitle, false);
		int pos = 30;

		MenuButton mMouseSet = new MenuButton("Mouse setup", app.getFont(1), 0, pos += 20, 320, 1, 0, new BLMenuMouse(app), -1, null, 0);
		mMouseSet.fontShadow = true;
		MenuButton mJoySet = new MenuButton("Joystick setup", app.getFont(1), 0, pos += 20, 320, 1, 0, new BLMenuJoystick(app), -1, null, 0);
		mJoySet.fontShadow = true;
		MenuButton mKeySet = new MenuButton("Keyboard Setup", app.getFont(1), 0, pos += 20, 320, 1, 0, new MenuKeyboardSet(app), -1, null, 0);
		mKeySet.fontShadow = true;
		
		addItem(mMouseSet, true);
		addItem(mJoySet, false);
		addItem(mKeySet, false);
		
		addItem(app.menu.addMenuBlood(), false);
	}
}
