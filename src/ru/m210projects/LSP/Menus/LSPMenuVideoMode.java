// This file is part of LSPGDX.
// Copyright (C) 2020  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// LSPGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// LSPGDX is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with LSPGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.LSP.Menus;

import static ru.m210projects.Build.Render.VideoMode.validmodes;
import static ru.m210projects.LSP.Factory.LSPMenuHandler.COLORCORR;
import static ru.m210projects.LSP.Main.engine;

import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.Pattern.BuildFont;
import ru.m210projects.Build.Pattern.BuildGame;
import ru.m210projects.Build.Pattern.CommonMenus.MenuRendererSettings;
import ru.m210projects.Build.Pattern.CommonMenus.MenuVideoMode;
import ru.m210projects.Build.Pattern.MenuItems.MenuTitle;
import ru.m210projects.Build.Render.VideoMode;
import ru.m210projects.Build.Settings.BuildConfig;
import ru.m210projects.LSP.Config;
import ru.m210projects.LSP.Factory.LSPMenuHandler;

public class LSPMenuVideoMode extends MenuVideoMode {

	public LSPMenuVideoMode(BuildGame app) {
		super(app, 46, 40, 240, 10, app.getFont(0), 10, 180, 611);
	}

	@Override
	public MenuTitle getTitle(BuildGame app, String text) {
		return null;
	}

	@Override
	public void setMode(BuildConfig cfg) {
		Config pcfg = (Config) cfg;
		if (!engine.setgamemode(pcfg.fullscreen, choosedMode.xdim, choosedMode.ydim)) {
			VideoMode mode = validmodes.get(0);
			engine.setgamemode(0, mode.xdim, mode.ydim);
			pcfg.fullscreen = 0;
			currentMode = mode;
		}
		
		pcfg.ScreenWidth = BuildGdx.graphics.getWidth();
		pcfg.ScreenHeight = BuildGdx.graphics.getHeight();
	}
	
	@Override
	public MenuRendererSettings getRenSettingsMenu(final BuildGame app, int posx, int posy, int width,
			int nHeight, BuildFont style) {
		
		final LSPMenuHandler bmenu = (LSPMenuHandler) app.pMenu;
		
		MenuRendererSettings menu = new MenuRendererSettings(app, posx, posy, width, nHeight, style) {
			@Override
			public MenuTitle getTitle(BuildGame app, String text) {
				return null;
			}
		};
		menu.fontShadow = true;
		menu.listShadow = true;
		bmenu.mMenus[COLORCORR] = menu;
		
		return menu;
	}

}
