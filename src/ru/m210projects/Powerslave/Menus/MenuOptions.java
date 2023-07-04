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

import static ru.m210projects.Powerslave.Factory.PSMenuHandler.*;

import ru.m210projects.Build.Pattern.MenuItems.BuildMenu;
import ru.m210projects.Build.Pattern.MenuItems.MenuButton;
import ru.m210projects.Build.Pattern.MenuItems.MenuTitle;
import ru.m210projects.Powerslave.Main;
import ru.m210projects.Powerslave.Factory.PSMenuHandler;

public class MenuOptions extends BuildMenu {

	public MenuOptions(final Main app)
	{
		final PSMenuHandler menu = (PSMenuHandler) app.menu;
		
		MenuTitle mTitle = new PSTitle("Options", 160, 15, 0);
		
		int posy = 45;
		MenuButton mGameSet = new PSButton("Game Setup", 0, posy += 20, 320, 1, 0, new MenuGameSet(app), -1, null, 0);
		
		MenuButton mInterface = new PSButton("Interface Setup", 0, posy += 20, 320, 1, 0, new MenuInterfaceSet(app), -1, null, 0);

		MenuButton mVideo = new PSButton("Video Setup", 0, posy += 20, 320, 1, 0, new PSMenuVideoMode(app), -1, null, 0);

		MenuButton mAudio = new PSButton("Audio Setup", 0, posy += 20, 320, 1, 0, menu.mMenus[AUDIO] = new PSMenuAudio(app), -1, null, 0);
		
		MenuButton mControls = new PSButton("Controls Setup", 0, posy += 20, 320, 1, 0, new MenuControls(app), -1, null, 0);

		addItem(mTitle, false);

		addItem(mGameSet, true);
		addItem(mInterface, false);
		addItem(mVideo, false);
		addItem(mAudio, false);	
		addItem(mControls, false);
	}
}
