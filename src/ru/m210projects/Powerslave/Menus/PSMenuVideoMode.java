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

import static ru.m210projects.Build.Render.VideoMode.validmodes;
import static ru.m210projects.Powerslave.Factory.PSMenuHandler.COLORCORR;
import static ru.m210projects.Powerslave.Main.cfg;
import static ru.m210projects.Powerslave.Main.engine;
import static ru.m210projects.Powerslave.Globals.*;

import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.Pattern.BuildFont;
import ru.m210projects.Build.Pattern.BuildGame;
import ru.m210projects.Build.Pattern.CommonMenus.MenuRendererSettings;
import ru.m210projects.Build.Pattern.CommonMenus.MenuVideoMode;
import ru.m210projects.Build.Pattern.MenuItems.MenuHandler;
import ru.m210projects.Build.Pattern.MenuItems.MenuTitle;
import ru.m210projects.Build.Render.VideoMode;
import ru.m210projects.Build.Settings.BuildConfig;
import ru.m210projects.Powerslave.Config;
import ru.m210projects.Powerslave.Factory.PSMenuHandler;

public class PSMenuVideoMode extends MenuVideoMode {

	public PSMenuVideoMode(BuildGame app) {
		super(app, 46, 40, 240, 10, app.getFont(0), 10, 180, BACKGROUND);
		
		mApplyChanges = new PSButton("Apply changes", 0, mApplyChanges.y, 320, 1, 0, null, -1, mApplyChanges.specialCall, 0) {
			@Override
			public void draw(MenuHandler handler) {
				mCheckEnableItem(choosedMode != null && (choosedMode != currentMode || isFullscreen != (cfg.fullscreen == 1) || currentRender != choosedRender));
				super.draw(handler);
			}
		};
		
		mResolution.listShadow = true;
		mResolution.fontShadow = true;
		mRenderer.fontShadow = true;
		mRenderer.listShadow = true;
		mRenderSettings.fontShadow = true;
		mFullscreen.fontShadow = true;
		mApplyChanges.fontShadow = true;
		mSlot.fontShadow = true;
		slider.fontShadow = true;

		this.m_nItems--;
		addItem(mApplyChanges, false);
	}

	@Override
	public MenuTitle getTitle(BuildGame app, String text) {
		return new PSTitle(text, 160, 15, 0);
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
		
		final PSMenuHandler bmenu = (PSMenuHandler) app.pMenu;
		
		MenuRendererSettings menu = new MenuRendererSettings(app, posx, posy, width, nHeight, style) {
			@Override
			public MenuTitle getTitle(BuildGame app, String text) {
				return new PSTitle(text, 160, 15, 0);
			}
		};
		menu.fontShadow = true;
		menu.listShadow = true;
		bmenu.mMenus[COLORCORR] = menu;
		
		return menu;
	}

}
