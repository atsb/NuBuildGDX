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

import static ru.m210projects.Redneck.Globals.ud;
import static ru.m210projects.Redneck.Factory.RRMenuHandler.COLORCORR;
import static ru.m210projects.Redneck.Names.*;
import static ru.m210projects.Redneck.Screen.*;

import ru.m210projects.Build.Pattern.BuildFont;
import ru.m210projects.Build.Pattern.BuildGame;
import ru.m210projects.Build.Pattern.CommonMenus.MenuRendererSettings;
import ru.m210projects.Build.Pattern.CommonMenus.MenuVideoMode;
import ru.m210projects.Build.Pattern.MenuItems.MenuHandler;
import ru.m210projects.Build.Pattern.MenuItems.MenuTitle;
import ru.m210projects.Build.Settings.BuildConfig;
import ru.m210projects.Build.Settings.BuildSettings;
import ru.m210projects.Redneck.Factory.RRMenuHandler;

public class RMenuVideoMode extends MenuVideoMode {

	public RMenuVideoMode(BuildGame app) {
		super(app, 46, 40, 240, 12, app.getFont(1), 10, 180, LOADSCREEN);
		
		mApplyChanges.font = app.getFont(2);
		mSlot.backgroundPal = 4;
	}

	@Override
	public MenuTitle getTitle(BuildGame app, String text) {
		return new RRTitle(text);
	}

	@Override
	public void setMode(BuildConfig cfg) {
		setup3dscreen(choosedMode.xdim, choosedMode.ydim);
	}
	
	@Override
	public MenuRendererSettings getRenSettingsMenu(BuildGame app, int posx, int posy, int width,
			int nHeight, BuildFont style) {
		
		final RRMenuHandler rmenu = (RRMenuHandler) app.pMenu;
		
		MenuRendererSettings menu = new MenuRendererSettings(app, posx, posy - 10, width, nHeight, style) {
			@Override
			public MenuTitle getTitle(BuildGame app, String text) {
				return new RRTitle(text);
			}
			
			@Override
			public void mDraw(MenuHandler handler)
			{
				super.mDraw(handler);
				ud.brightness = BuildSettings.paletteGamma.get() << 2;
			}
		};
		
		rmenu.mMenus[COLORCORR] = menu;
		return menu;
	}

}
