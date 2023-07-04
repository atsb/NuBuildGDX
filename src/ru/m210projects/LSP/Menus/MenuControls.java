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

import static ru.m210projects.LSP.Main.game;

import ru.m210projects.Build.Pattern.MenuItems.BuildMenu;
import ru.m210projects.Build.Pattern.MenuItems.MenuButton;
import ru.m210projects.LSP.Main;

public class MenuControls extends BuildMenu {

	public MenuControls(Main app)
	{
		int pos = 30;

		MenuButton mMouseSet = new MenuButton("Mouse setup", game.getFont(2),0, pos += 20, 320, 1, 0, new LSPMenuMouse(app), -1, null, 0);
		MenuButton mJoySet = new MenuButton("Joystick setup", game.getFont(2),0, pos += 20, 320, 1, 0, new LSPMenuJoystick(app), -1, null, 0);
		MenuButton mKeySet = new MenuButton("Keyboard Setup", game.getFont(2),0, pos += 20, 320, 1, 0, new MenuKeyboardSet(app), -1, null, 0);

		addItem(mMouseSet, true);
		addItem(mJoySet, false);
		addItem(mKeySet, false);
	}
}
