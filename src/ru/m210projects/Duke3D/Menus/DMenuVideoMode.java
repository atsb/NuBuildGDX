// This file is part of DukeGDX.
// Copyright (C) 2019  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// DukeGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// DukeGDX is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with DukeGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.Duke3D.Menus;

import static ru.m210projects.Duke3D.Factory.DukeMenuHandler.COLORCORR;
import static ru.m210projects.Duke3D.Names.*;
import static ru.m210projects.Duke3D.Screen.*;
import static ru.m210projects.Duke3D.Globals.*;

import ru.m210projects.Build.Pattern.BuildFont;
import ru.m210projects.Build.Pattern.BuildGame;
import ru.m210projects.Build.Pattern.CommonMenus.MenuRendererSettings;
import ru.m210projects.Build.Pattern.CommonMenus.MenuVideoMode;
import ru.m210projects.Build.Pattern.MenuItems.MenuHandler;
import ru.m210projects.Build.Pattern.MenuItems.MenuTitle;
import ru.m210projects.Build.Settings.BuildConfig;
import ru.m210projects.Build.Settings.BuildSettings;
import ru.m210projects.Duke3D.Factory.DukeMenuHandler;

public class DMenuVideoMode extends MenuVideoMode {

	public DMenuVideoMode(BuildGame app) {
		super(app, 46, 40, 240, 10, app.getFont(1), 10, 180, LOADSCREEN);
		
		mApplyChanges.font = app.getFont(2);
		mSlot.backgroundPal = 4;
	}

	@Override
	public MenuTitle getTitle(BuildGame app, String text) {
		return new DukeTitle(text);
	}

	@Override
	public void setMode(BuildConfig cfg) {
		setup3dscreen(choosedMode.xdim, choosedMode.ydim);
	}

	@Override
	public MenuRendererSettings getRenSettingsMenu(BuildGame app, int posx, int posy, int width,
			int nHeight, BuildFont style) {
		
		final DukeMenuHandler menu = (DukeMenuHandler) app.pMenu;
		
		MenuRendererSettings rmenu = new MenuRendererSettings(app, posx, posy, width, nHeight, style) {
			@Override
			public MenuTitle getTitle(BuildGame app, String text) {
				return new DukeTitle(text);
			}
			
			@Override
			public void mDraw(MenuHandler handler)
			{
				super.mDraw(handler);
				ud.brightness = BuildSettings.paletteGamma.get() << 2;
			}
		};
		
		menu.mMenus[COLORCORR] = rmenu;
		return rmenu;
	}

}
