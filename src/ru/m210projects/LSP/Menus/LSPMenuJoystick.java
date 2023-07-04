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

import ru.m210projects.Build.Input.Keymap;
import ru.m210projects.Build.Pattern.BuildGame;
import ru.m210projects.Build.Pattern.CommonMenus.MenuJoystick;
import ru.m210projects.Build.Pattern.MenuItems.MenuTitle;
import ru.m210projects.LSP.Main;

public class LSPMenuJoystick extends MenuJoystick {

	public LSPMenuJoystick(Main app) {
		super(app, 20, 50, 280, 10, 15, app.getFont(0), 10);

		mList.menupal = 20;
		mText.y -= 10;
		mText2.y -= 10;
	}

	@Override
	public MenuTitle getTitle(BuildGame app, String text) {
		return null;
	}

	@Override
	public String keyNames(int keycode) {
		return Keymap.toString(keycode);
	}

}
