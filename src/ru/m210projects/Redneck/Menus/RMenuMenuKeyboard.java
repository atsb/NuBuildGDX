// This file is part of RedneckGDX.
// Copyright (C) 2017-2019  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// RedneckGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// RedneckGDX is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with RedneckGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.Redneck.Menus;

import static com.badlogic.gdx.Input.Keys.BACKSPACE;

import ru.m210projects.Redneck.Main;
import ru.m210projects.Build.Input.Keymap;
import ru.m210projects.Build.Pattern.BuildGame;
import ru.m210projects.Build.Pattern.CommonMenus.MenuKeyboard;
import ru.m210projects.Build.Pattern.MenuItems.MenuTitle;

public class RMenuMenuKeyboard extends MenuKeyboard {

	public RMenuMenuKeyboard(Main app) {
		super(app, 20, 40, 280, 14, app.getFont(0));
		
		mList.pal_left = 2;
		mList.pal_right = 12;
		mList.fontShadow = true;
		
		mText.y -= 10;
		mText2.y -= 10;
		mText.fontShadow = mText2.fontShadow = true;
	}

	@Override
	public MenuTitle getTitle(BuildGame app, String text) {
		return new RRTitle(text);
	}

	@Override
	public String keyNames(int keycode) {
		
		if(keycode == BACKSPACE)
			return "BKSPACE";
		
		return Keymap.toString(keycode);
	}

}
