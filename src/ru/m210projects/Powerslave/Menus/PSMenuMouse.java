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

import ru.m210projects.Build.Pattern.BuildGame;
import ru.m210projects.Build.Pattern.CommonMenus.MenuMouse;
import ru.m210projects.Build.Pattern.MenuItems.MenuTitle;
import ru.m210projects.Powerslave.Main;

public class PSMenuMouse extends MenuMouse {

	public PSMenuMouse(Main app) {
		super(app, 20, 40, 280, 10, 5, app.getFont(0), 0);
		
		mEnable.fontShadow = true;
		mMenuEnab.fontShadow = true;
		mSens.fontShadow = true;
		mTurn.fontShadow = true;
		mLook.fontShadow = true;
		mMove.fontShadow = true;
		mStrafe.fontShadow = true;
		mAiming.fontShadow = true;
		mInvert.fontShadow = true;
		mAdvance.fontShadow = true;
		
		mAxisUp.fontShadow = true;
		mAxisDown.fontShadow = true;
		mAxisLeft.fontShadow = true;
		mAxisRight.fontShadow = true;
		
		mAxisUp.listShadow = true;
		mAxisDown.listShadow = true;
		mAxisLeft.listShadow = true;
		mAxisRight.listShadow = true;

		m_pItems[6].text = "Fwd Bwd speed".toCharArray();	
	}

	@Override
	public MenuTitle getTitle(BuildGame app, String text) {
		return new PSTitle(text, 160, 15, 0);
	}

}
