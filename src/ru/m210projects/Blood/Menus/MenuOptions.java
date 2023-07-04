// This file is part of BloodGDX.
// Copyright (C) 2017-2019  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// BloodGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// BloodGDX is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with BloodGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.Blood.Menus;

import static ru.m210projects.Blood.Factory.BloodMenuHandler.SOUNDSET;

import ru.m210projects.Blood.Main;
import ru.m210projects.Build.Pattern.MenuItems.BuildMenu;
import ru.m210projects.Build.Pattern.MenuItems.MenuButton;
import ru.m210projects.Build.Pattern.MenuItems.MenuTitle;

public class MenuOptions extends BuildMenu {

	public MenuOptions(final Main app)
	{
		MenuTitle OptionsTitle = new MenuTitle(app.pEngine, "Options", app.getFont(1), 160, 20, 2038);
		
		int pos = 30;
		MenuButton oGame = new MenuButton("Game Setup", app.getFont(1), 0, pos += 20, 320, 1, 0, new MenuGameSet(app), -1, null, 0);
		oGame.fontShadow = true;
		MenuButton oHUD = new MenuButton("Interface Setup", app.getFont(1), 0, pos += 20, 320, 1, 0, new MenuInterfaceSet(app), -1, null, 0);
		oHUD.fontShadow = true;
		MenuButton oVideo = new MenuButton("Video Setup", app.getFont(1), 0, pos += 20, 320, 1, 0, new BLMenuVideoMode(app), -1, null, 0);
		oVideo.fontShadow = true;
		MenuButton oSound = new MenuButton("Audio Setup", app.getFont(1), 0, pos += 20, 320, 1, 0, app.menu.mMenus[SOUNDSET] = new BLMenuAudio(app), -1, null, 0);
		oSound.fontShadow = true;
		MenuButton oControls = new MenuButton("Controls Setup", app.getFont(1), 0, pos += 20, 320, 1, 0, new MenuControls(app), -1, null, 0);
		oControls.fontShadow = true;

		addItem(OptionsTitle, false);
		addItem(oGame, true);
		addItem(oHUD, false);
		addItem(oVideo, false);
		addItem(oSound, false);
		addItem(oControls, false);

		addItem(app.menu.addMenuBlood(), false);
	}
}
