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

package ru.m210projects.Redneck.Menus
;
import static ru.m210projects.Redneck.Factory.RRMenuHandler.*;

import ru.m210projects.Build.Pattern.MenuItems.BuildMenu;
import ru.m210projects.Build.Pattern.MenuItems.MenuButton;
import ru.m210projects.Redneck.Main;
import ru.m210projects.Redneck.Factory.RRMenuHandler;

public class OptionsMenu extends BuildMenu {

	public OptionsMenu(Main app)
	{
		addItem(new RRTitle("Options"), false);
		
		final RRMenuHandler menu = (RRMenuHandler) app.menu;
		
		int pos = 35;
		MenuButton bGameSetup = new MenuButton("GAME SETUP", app.getFont(2), 0, pos += 20, 320, 1, 0, new MenuGameSetup(app), 1, null, 0);
		MenuButton bHUDSetup = new MenuButton("INTERFACE SETUP", app.getFont(2), 0, pos += 20, 320, 1, 0, new InterfaceMenu(app), 1, null, 0);
		MenuButton bSoundSetup = new MenuButton("AUDIO SETUP", app.getFont(2), 0, pos += 20, 320, 1, 0, menu.mMenus[SOUNDSET], 1, null, 0);
		MenuButton bVideoSetup = new MenuButton("VIDEO SETUP", app.getFont(2), 0, pos += 20, 320, 1, 0, new RMenuVideoMode(app), 1, null, 0);
		MenuButton bKeySetup = new MenuButton("CONTROL SETUP", app.getFont(2), 0, pos += 20, 320, 1, 0,  new MenuControls(app), 1, null, 0);
		
		addItem(bGameSetup, true);
		addItem(bHUDSetup, false);
		addItem(bSoundSetup, false);
		addItem(bVideoSetup, false);
		addItem(bKeySetup, false);
	}
}
