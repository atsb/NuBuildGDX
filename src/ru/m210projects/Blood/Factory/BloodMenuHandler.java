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

package ru.m210projects.Blood.Factory;

import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Build.Pragmas.*;
import static ru.m210projects.Blood.Globals.*;

import ru.m210projects.Blood.Menus.MenuInterfaceSet;
import ru.m210projects.Blood.Menus.MenuQav;
import ru.m210projects.Blood.Screens.MenuScreen;
import ru.m210projects.Build.Engine;
import ru.m210projects.Build.Pattern.BuildFont;
import ru.m210projects.Build.Pattern.BuildGame;
import ru.m210projects.Build.Pattern.CommonMenus.MenuRendererSettings;
import ru.m210projects.Build.Pattern.MenuItems.BuildMenu;
import ru.m210projects.Build.Pattern.MenuItems.MenuHandler;
import ru.m210projects.Build.Pattern.MenuItems.MenuItem;
import ru.m210projects.Build.Types.Tile;

public class BloodMenuHandler extends MenuHandler {

	public BuildMenu[] mMenus;
	private final Engine engine;
	private final BuildGame app;

	public static final int MAIN = 0;
	public static final int GAME = 1;
	public static final int NEWGAME = 2;
	public static final int SOUNDSET = 3;
	public static final int DIFFICULTY = 4;
	public static final int CREDITS = 5;
	public static final int HELP = 6;
	public static final int LOADGAME = 7;
	public static final int SAVEGAME = 8;
	public static final int QUIT = 9;
	public static final int QUITTITLE = 10;
	public static final int NETWORKGAME = 11;
	public static final int MULTIPLAYER = 12;
	public static final int COLORCORR = 13;
	public static final int NEWADDON = 14;
	public static final int OPTIONS = 15;
	public static final int USERCONTENT = 16;
	public static final int CORRUPTLOAD = 17;

    private MenuQav Bdrip;

	public BloodMenuHandler(BuildGame app) {
		mMenus = new BuildMenu[19];
		this.engine = app.pEngine;
		this.app = app;
	}

	@Override
	public void mDrawMenu() {
		if (!(app.getScreen() instanceof MenuScreen) && !(app.pMenu.getCurrentMenu() instanceof MenuRendererSettings)
				&& !(app.pMenu.getCurrentMenu() instanceof MenuInterfaceSet)) {

            int tile = 2046;
			Tile pic = engine.getTile(tile);
			float kt = xdim / (float) ydim;
			float kv = pic.getWidth() / (float) pic.getHeight();
			float scale;
			if (kv >= kt)
				scale = (ydim + 1) / (float) pic.getHeight();
			else
				scale = (xdim + 1) / (float) pic.getWidth();

			engine.rotatesprite(0, 0, (int) (scale * 65536), 0, tile, 127, 5, 8 | 16 | 33, 0, 0, xdim - 1, ydim - 1);
		}

		super.mDrawMenu();
	}

	public MenuQav addMenuBlood() {
		if (Bdrip == null) {
			Bdrip = new MenuQav(160, 100, 256) {
				@Override
				public void draw(MenuHandler handler) {
					int frames = xdim / 320;
					pQAV.origin.x = 0;
					for (int i = 0; i <= frames; i++) {
						super.draw(handler);
						pQAV.origin.x += 319;
					}
				}
			};
		}
		return Bdrip;
	}

	@Override
	public int getShade(MenuItem item) {
		int shade = 32;
		if (item != null && item.isFocused())
			shade = 32 - (totalclock & 0x3F);
		return shade;
	}

	@Override
	public int getPal(BuildFont font, MenuItem item) {
		if (item != null) {
			if (!item.isEnabled())
				return 10;

			return item.pal;
		}

		return 0;
	}

	@Override
	public void mDrawMouse(int x, int y) {

        if (!app.pCfg.menuMouse)
			return;

		int zoom = scale(0x10000, ydim, 200);
		int czoom = mulscale(0x8000, mulscale(zoom, app.pCfg.gMouseCursorSize, 16), 16);
		int xoffset = 0; // mulscale(16, czoom, 16);
		int yoffset = 0; // mulscale(16, czoom, 16);
		int ang = 0; // 1800;

		engine.rotatesprite((x + xoffset) << 16, (y + yoffset) << 16, czoom, ang, MOUSECURSOR, 0, 0, 8, 0, 0, xdim - 1,
				ydim - 1);
	}

	@Override
	public void mDrawBackButton() {
		if (!app.pCfg.menuMouse)
			return;

		int zoom = scale(16384, ydim, 200);
		if (mCount > 1) {
			// Back button
			Tile pic = engine.getTile(BACKBUTTON);

			int shade = 16 + mulscale(16, sintable[(20 * totalclock) & 2047], 16);
			engine.rotatesprite(0, (ydim - mulscale(pic.getHeight(), zoom, 16)) << 16, zoom, 0, BACKBUTTON, shade,
					0, 8 | 16, 0, 0, mulscale(zoom, pic.getWidth() - 1, 16), ydim - 1);
		}
	}

	@Override
	public boolean mCheckBackButton(int mx, int my) {
		int zoom = scale(16384, ydim, 200);
		Tile pic = engine.getTile(BACKBUTTON);

		int size = mulscale(pic.getWidth(), zoom, 16);
		int bx = 0;
		int by = ydim - mulscale(pic.getHeight(), zoom, 16);
		if (mx >= bx && mx < bx + size)
			return my >= by && my < by + size;
		return false;
	}

	@Override
	public void mSound(MenuItem item, MenuOpt opt) {
		/* nothing */ }

	@Override
	public void mPostDraw(MenuItem item) {
		/* nothing */ }

}
