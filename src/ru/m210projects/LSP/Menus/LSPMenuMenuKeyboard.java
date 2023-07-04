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

import static com.badlogic.gdx.Input.Keys.*;

import ru.m210projects.Build.Input.Keymap;
import ru.m210projects.Build.Pattern.BuildGame;
import ru.m210projects.Build.Pattern.CommonMenus.MenuKeyboard;
import ru.m210projects.Build.Pattern.MenuItems.MenuTitle;
import ru.m210projects.LSP.Main;

public class LSPMenuMenuKeyboard extends MenuKeyboard {

	public LSPMenuMenuKeyboard(Main app) {
		super(app, 10, 40, 300, 15, app.getFont(0));

		mList.pal_right = mList.pal_left = 228;
		mText.y -= 10;
		mText2.y -= 10;
	}

	@Override
	public MenuTitle getTitle(BuildGame app, String text) {
		return null;
	}

	@Override
	public String keyNames(int keycode) {
		if(keycode == 0)
			return "None";
		
		switch(keycode)
		{
			case COMMA:
				return "Comma";
			case PERIOD:
				return "Period";
			case MINUS:
				return "Minus";
			case EQUALS:
				return "Equals";
			case LEFT_BRACKET:
				return "L_bracket";
			case RIGHT_BRACKET:
				return "R_bracket";
			case BACKSLASH:
				return "Bckslash";
			case SEMICOLON:
				return "Semicolon";
			case APOSTROPHE:
				return "Apostrophe";
			case SLASH:
				return "Slash";
			case AT:
				return "At";
			case COLON:
				return "Colon";
			case BACKSPACE:
				return "Bckspace";
			default:
				return Keymap.toString(keycode);
		}
	}

}
