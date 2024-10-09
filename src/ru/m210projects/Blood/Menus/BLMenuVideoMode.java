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

import static ru.m210projects.Blood.View.*;
import static ru.m210projects.Build.Engine.xdim;
import static ru.m210projects.Build.Engine.ydim;
import static ru.m210projects.Build.Render.VideoMode.validmodes;
import static ru.m210projects.Blood.Factory.BloodMenuHandler.COLORCORR;
import static ru.m210projects.Blood.Screen.scrSetGameMode;

import ru.m210projects.Blood.Config;
import ru.m210projects.Blood.Factory.BloodMenuHandler;
import ru.m210projects.Build.Pattern.BuildFont;
import ru.m210projects.Build.Pattern.BuildGame;
import ru.m210projects.Build.Pattern.BuildFont.TextAlign;
import ru.m210projects.Build.Pattern.CommonMenus.MenuRendererSettings;
import ru.m210projects.Build.Pattern.CommonMenus.MenuVideoMode;
import ru.m210projects.Build.Pattern.MenuItems.MenuHandler;
import ru.m210projects.Build.Pattern.MenuItems.MenuTitle;
import ru.m210projects.Build.Render.VideoMode;
import ru.m210projects.Build.Settings.BuildConfig;

public class BLMenuVideoMode extends MenuVideoMode {

	public BLMenuVideoMode(BuildGame app) {
		super(app, 46, 40, 240, 10, app.getFont(3), 10, 180, 2051);

		mApplyChanges.font = app.getFont(1);
		mApplyChanges.fontShadow = true;

		mSlot.transparent = 33;
		mResList.addItem(((BloodMenuHandler) app.pMenu).addMenuBlood(), false);
		addItem(((BloodMenuHandler) app.pMenu).addMenuBlood(), false);
	}

	@Override
	public MenuTitle getTitle(BuildGame app, String text) {
		return new MenuTitle(app.pEngine, text, app.getFont(1), 160, 20, 2038);
	}

	@Override
	public void setMode(BuildConfig cfg) {
		Config bcfg = (Config) cfg;
		if (!scrSetGameMode(bcfg.fullscreen, choosedMode.xdim, choosedMode.ydim)) {
			VideoMode mode = validmodes.get(0);
			scrSetGameMode(bcfg.fullscreen, mode.xdim, mode.ydim);
			currentMode = mode;
		}

		viewResizeView(bcfg.gViewSize);
	}

	@Override
	public MenuRendererSettings getRenSettingsMenu(final BuildGame app, int posx, int posy, int width,
			int nHeight, BuildFont style) {

		final BloodMenuHandler bmenu = (BloodMenuHandler) app.pMenu;

		MenuRendererSettings menu = new MenuRendererSettings(app, posx, posy, width, nHeight, style) {
			@Override
			public MenuTitle getTitle(BuildGame app, String text) {
				return new MenuTitle(app.pEngine, text, app.getFont(1), 160, 20, 2038) {
					@Override
					public void draw(MenuHandler handler) {
						if ( text != null )
						{
							draw.rotatesprite(150 << 16, y << 16, 65536, 0, nTile, -128, 0, 78, 0, 0, (xdim / 2) - 2, ydim - 1);
							draw.rotatesprite(170 << 16, y << 16, 65536, 0, nTile, -128, 0, 78, (xdim / 2) - 2, 0, xdim - 1, ydim - 1);
							font.drawText(x, y - font.getHeight() / 2, text, -128, pal, TextAlign.Center, 2, fontShadow);
						}
					}
				};
			}

			@Override
			protected void rebuild()
			{
				super.rebuild();
				this.addItem(((BloodMenuHandler) app.pMenu).addMenuBlood(), false);
			}
		};

		bmenu.mMenus[COLORCORR] = menu;

		return menu;
	}

}
