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
import ru.m210projects.Build.Pattern.MenuItems.MenuTitle;
import ru.m210projects.Powerslave.Main;

public class MenuControls extends BuildMenu {

	public MenuControls(Main app)
	{
		MenuTitle mTitle = new PSTitle("Controls Setup", 160, 15, 0);

		addItem(mTitle, false);
		int pos = 30;

		MenuButton mMouseSet = new PSButton("Mouse setup", 0, pos += 20, 320, 1, 0, new PSMenuMouse(app), -1, null, 0);
		MenuButton mJoySet = new PSButton("Joystick setup", 0, pos += 20, 320, 1, 0, new PSMenuJoystick(app), -1, null, 0);
		MenuButton mKeySet = new PSButton("Keyboard Setup", 0, pos += 20, 320, 1, 0, new MenuKeyboardSet(app), -1, null, 0);

		addItem(mMouseSet, true);
		addItem(mJoySet, false);
		addItem(mKeySet, false);
	}
}
