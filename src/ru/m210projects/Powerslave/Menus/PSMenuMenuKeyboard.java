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

import static com.badlogic.gdx.Input.Keys.*;

import ru.m210projects.Build.Input.Keymap;
import ru.m210projects.Build.Pattern.BuildGame;
import ru.m210projects.Build.Pattern.CommonMenus.MenuKeyboard;
import ru.m210projects.Build.Pattern.MenuItems.MenuTitle;
import ru.m210projects.Powerslave.Main;

public class PSMenuMenuKeyboard extends MenuKeyboard {

	public PSMenuMenuKeyboard(Main app) {
		super(app, 10, 40, 300, 15, app.getFont(0));
		
		mList.fontShadow = true;
		
		mText.y -= 10;
		mText2.y -= 10;
		
		mText.fontShadow = true;
		mText2.fontShadow = true;
	}

	@Override
	public MenuTitle getTitle(BuildGame app, String text) {
		return new PSTitle(text, 160, 15, 0);
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
