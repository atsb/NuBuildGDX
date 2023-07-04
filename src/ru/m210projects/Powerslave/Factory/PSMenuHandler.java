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

package ru.m210projects.Powerslave.Factory;

import static ru.m210projects.Powerslave.Globals.*;
import static ru.m210projects.Powerslave.Sound.*;
import static ru.m210projects.Build.Engine.sintable;
import static ru.m210projects.Build.Engine.totalclock;
import static ru.m210projects.Build.Engine.xdim;
import static ru.m210projects.Build.Engine.ydim;
import static ru.m210projects.Build.Pragmas.mulscale;
import static ru.m210projects.Build.Pragmas.scale;

import ru.m210projects.Build.Engine;
import ru.m210projects.Build.Pattern.BuildFont;
import ru.m210projects.Build.Pattern.BuildGame;
import ru.m210projects.Build.Pattern.CommonMenus.MenuRendererSettings;
import ru.m210projects.Build.Pattern.CommonMenus.MenuVideoMode;
import ru.m210projects.Build.Pattern.MenuItems.BuildMenu;
import ru.m210projects.Build.Pattern.MenuItems.MenuButton;
import ru.m210projects.Build.Pattern.MenuItems.MenuConteiner;
import ru.m210projects.Build.Pattern.MenuItems.MenuFileBrowser;
import ru.m210projects.Build.Pattern.MenuItems.MenuHandler;
import ru.m210projects.Build.Pattern.MenuItems.MenuItem;
import ru.m210projects.Build.Pattern.MenuItems.MenuJoyList;
import ru.m210projects.Build.Pattern.MenuItems.MenuKeyboardList;
import ru.m210projects.Build.Pattern.MenuItems.MenuList;
import ru.m210projects.Build.Pattern.MenuItems.MenuResolutionList;
import ru.m210projects.Build.Pattern.MenuItems.MenuScroller;
import ru.m210projects.Build.Pattern.MenuItems.MenuSlider;
import ru.m210projects.Build.Pattern.MenuItems.MenuVariants;
import ru.m210projects.Build.Types.Tile;
import ru.m210projects.Powerslave.Menus.MenuInterfaceSet;
import ru.m210projects.Powerslave.Screens.MenuScreen;

public class PSMenuHandler extends MenuHandler {

	public static final int MAIN = 0;
	public static final int GAME = 1;
	public static final int NEWGAME = 2;
	public static final int LOAD = 3;
	public static final int SAVE = 4;
	public static final int OPTIONS = 5;
	public static final int AUDIO = 6;
	public static final int COLORCORR = 7;
	public static final int QUIT = 8;
	public static final int QUITTITLE = 9;
	public static final int ENDGAME = 10;
	public static final int ADDON = 11;
	public static final int CORRUPTLOAD = 12;

	public BuildMenu[] mMenus;
	private Engine engine;
	private BuildGame app;

	public PSMenuHandler(BuildGame app) {
		mMenus = new BuildMenu[18];
		this.engine = app.pEngine;
		this.app = app;
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
				return 20;

			return item.pal;
		}

		return 0;
	}

	@Override
	public void mDrawMenu() {
		if (!(app.getScreen() instanceof MenuScreen) && !(app.pMenu.getCurrentMenu() instanceof MenuRendererSettings)
				&& !(app.pMenu.getCurrentMenu() instanceof MenuInterfaceSet)) {
			int tile = BACKGROUND;
			Tile pic = engine.getTile(tile);
			float kt = xdim / (float) ydim;
			float kv = pic.getWidth() / (float) pic.getHeight();
			float scale;
			if (kv >= kt)
				scale = (ydim + 1) / (float) pic.getHeight();
			else
				scale = (xdim + 1) / (float) pic.getWidth();

			engine.rotatesprite(0, 0, (int) (scale * 65536), 0, tile, 127, 0, 8 | 16 | 1, 0, 0, xdim - 1, ydim - 1);
		}

		super.mDrawMenu();
	}

	@Override
	public void mDrawMouse(int x, int y) {
		if (!app.pCfg.menuMouse)
			return;

		int zoom = scale(0x10000, ydim, 200);
		int czoom = mulscale(0x8000, mulscale(zoom, app.pCfg.gMouseCursorSize, 16), 16);
		int xoffset = 0; // 16;
		int yoffset = 0; // 23;
		int ang = 0;

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
			if (my >= by && my < by + size)
				return true;
		return false;
	}

	private int oldValue;
	private long soundTime;

	@Override
	public void mSound(MenuItem item, MenuOpt opt) {
		switch (opt) {
		case Open:
			PlayLocalSound(33, 0);
			break;
		case Close:
			PlayLocalSound(31, 0);
			break;
		case UP:
		case DW:
		case PGUP:
		case PGDW:
		case HOME:
		case END:
		case MCHANGE:
			PlayLocalSound(35, 0);
			break;
		case LEFT:
		case RIGHT:
		case ENTER:
		case MWUP:
		case MWDW:
			if (opt == MenuOpt.ENTER && item instanceof MenuConteiner
					&& item.getClass().getEnclosingClass() == MenuVideoMode.class) {
				break;
			}
			if(item instanceof MenuSlider) {
				if(GetLocalSound() != 23 || !LocalSoundPlaying())
					PlayLocalSound(23, 0);
			} else PlayLocalSound(35, 0);
			soundTime = System.currentTimeMillis();
			break;
		case LMB:
			if(item instanceof MenuSlider) {
				MenuSlider slider = (MenuSlider) item;
				if(oldValue != slider.value) {
					oldValue = slider.value;
					if(GetLocalSound() != 23 || !LocalSoundPlaying())
						PlayLocalSound(23, 0);
					soundTime = System.currentTimeMillis();
				}
			} else {
				if(!(item instanceof MenuScroller)
				&& !(item instanceof MenuFileBrowser)
				&& !(item instanceof MenuKeyboardList))
					PlayLocalSound(35, 0);
			}
			break;
		default:
			break;
		}

		if (GetLocalSound() == 23 && (System.currentTimeMillis() - soundTime) >= 200) {
			StopLocalSound();
        }
	}

	@Override
	public void mPostDraw(MenuItem item) {
		int shade = 8 - (totalclock & 0x3F);
		if (item.isFocused()) {
			if (item instanceof MenuButton) {
				int py = item.y;
				int scale = 48000;
				if (item.align == 1) {
					int centre = 320 >> 2;
					if (item.font == app.getFont(1)) {
						engine.rotatesprite(((320 >> 1) + (centre >> 1) + 35) << 16, (py) << 16, scale, 1024, 3468,
								shade, 0, 4 | 10, 0, 0, xdim - 1, ydim - 1);
						engine.rotatesprite(((320 >> 1) - (centre >> 1) - 35) << 16, (py) << 16, scale, 0, 3468, shade,
								0, 10, 0, 0, xdim - 1, ydim - 1);
					} else
						engine.rotatesprite(((320 >> 1) - (item.font.getWidth(item.text) >> 1) - 12) << 16,
								(item.y + 2) << 16, 16384, 0, 3468, shade, 0, 10, 0, 0, xdim - 1, ydim - 1);
				} else
					engine.rotatesprite((item.x - 10) << 16, (item.y + 2) << 16, 16384, 0, 3468, shade, 0, 10, 0, 0,
							xdim - 1, ydim - 1);
			} else if (item instanceof MenuList) {
				if (item instanceof MenuResolutionList) {
					MenuList list = (MenuList) item;

					int px = list.x;

					int focus = list.l_nFocus;
					if (focus == -1 || focus < list.l_nMin || focus >= list.l_nMin + list.nListItems)
						return;

					int py = list.y + (focus - list.l_nMin) * (list.mFontOffset());
					engine.rotatesprite((px + 45) << 16, (py + 2) << 16, 16384, 0, 3468, shade, 0, 10, 0, 0, xdim - 1,
							ydim - 1);
				} else if (item instanceof MenuKeyboardList || item instanceof MenuJoyList) {
					MenuList list = (MenuList) item;
					int px = list.x;
					int py = list.y + (list.l_nFocus - list.l_nMin) * (list.mFontOffset());

					int focus = list.l_nFocus;
					if (focus == -1 || focus < list.l_nMin || focus >= list.l_nMin + list.nListItems)
						return;

					engine.rotatesprite((px - 10) << 16, (py + 2) << 16, 16384, 0, 3468, shade, 0, 10, 0, 0, xdim - 1,
							ydim - 1);
				}
			} else if (item instanceof MenuVariants)
				engine.rotatesprite((item.x - 20) << 16, (item.y - 1) << 16, 16384, 0, 3468, shade, 0, 10, 0, 0,
						xdim - 1, ydim - 1);
			else {
				engine.rotatesprite((item.x - 10) << 16, (item.y + 2) << 16, 16384, 0, 3468, shade, 0, 10, 0, 0,
						xdim - 1, ydim - 1);
			}
		}
	}

}
