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

import ru.m210projects.Build.Input.Keymap;
import ru.m210projects.Build.Pattern.BuildGame;
import ru.m210projects.Build.Pattern.CommonMenus.MenuJoystick;
import ru.m210projects.Build.Pattern.MenuItems.MenuTitle;
import ru.m210projects.Powerslave.Main;

public class PSMenuJoystick extends MenuJoystick {

	public PSMenuJoystick(Main app) {
		super(app, 20, 50, 280, 10, 15, app.getFont(0), 10);
		
		mJoyDevices.fontShadow = true;
		mJoyDevices.listShadow = true;
		mJoyKey.fontShadow = true;
		mJoyTurn.fontShadow = true;
		mJoyTurn.listShadow = true;
		mJoyLook.fontShadow = true;
		mJoyLook.listShadow = true;
		mJoyStrafe.fontShadow = true;
		mJoyStrafe.listShadow = true;
		mJoyMove.fontShadow = true;
		mJoyMove.listShadow = true;
		mDeadZone.fontShadow = true;
		mLookSpeed.fontShadow = true;
		mTurnSpeed.fontShadow = true;
		mInvert.fontShadow = true;
		mList.fontShadow = true;
		mText.fontShadow = true;
		mText2.fontShadow = true;
		
		mList.menupal = 20;
		mText.y -= 10;
		mText2.y -= 10;
	}

	@Override
	public MenuTitle getTitle(BuildGame app, String text) {
		return new PSTitle(text, 160, 15, 0);
	}

	@Override
	public String keyNames(int keycode) {
		return Keymap.toString(keycode);
	}

}
