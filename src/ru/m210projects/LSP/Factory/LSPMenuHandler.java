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

package ru.m210projects.LSP.Factory;

import static ru.m210projects.Build.Engine.sintable;
import static ru.m210projects.Build.Engine.totalclock;
import static ru.m210projects.Build.Engine.xdim;
import static ru.m210projects.Build.Engine.ydim;
import static ru.m210projects.Build.Pragmas.mulscale;
import static ru.m210projects.Build.Pragmas.scale;
import static ru.m210projects.LSP.GdxResources.*;

import ru.m210projects.Build.Engine;
import ru.m210projects.Build.Pattern.BuildFont;
import ru.m210projects.Build.Pattern.BuildGame;
import ru.m210projects.Build.Pattern.CommonMenus.MenuRendererSettings;
import ru.m210projects.Build.Pattern.MenuItems.BuildMenu;
import ru.m210projects.Build.Pattern.MenuItems.MenuHandler;
import ru.m210projects.Build.Pattern.MenuItems.MenuItem;
import ru.m210projects.Build.Pattern.MenuItems.MenuJoyList;
import ru.m210projects.Build.Pattern.MenuItems.MenuKeyboardList;
import ru.m210projects.Build.Pattern.MenuItems.MenuList;
import ru.m210projects.Build.Pattern.MenuItems.MenuResolutionList;
import ru.m210projects.Build.Pattern.MenuItems.MenuSlider;
import ru.m210projects.Build.Pattern.MenuItems.MenuVariants;
import ru.m210projects.Build.Types.Tile;
import ru.m210projects.LSP.Fonts.MenuFont;
import ru.m210projects.LSP.Menus.MenuInterfaceSet;
import ru.m210projects.LSP.Screens.MenuScreen;

public class LSPMenuHandler extends MenuHandler {

	public BuildMenu[] mMenus;
	private Engine engine;
	private BuildGame app;

	public static final int MAIN = 0;
	public static final int GAME = 1;
	public static final int LOADGAME = 3;
	public static final int SAVEGAME = 4;
	public static final int QUIT = 6;
	public static final int HELP = 7;
	public static final int AUDIOSET = 8;
	public static final int CONTROLSET = 9;
	public static final int OPTIONS = 10;
	public static final int COLORCORR = 11;
	public static final int CREDITS = 12;
	public static final int ADVERTISING = 13;
	public static final int LASTSAVE = 14;
	public static final int CORRUPTLOAD = 15;

	public LSPMenuHandler(BuildGame app)
	{
		mMenus = new BuildMenu[16];
		this.engine = app.pEngine;
		this.app = app;
	}

	@Override
	public void mDrawMenu() {
		if(!(app.getScreen() instanceof MenuScreen) && !(app.pMenu.getCurrentMenu() instanceof MenuRendererSettings) && !(app.pMenu.getCurrentMenu() instanceof MenuInterfaceSet)) {
			int tile = 611;
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
	public int getShade(MenuItem item) {
		if(item != null && item.getClass().getSuperclass().equals(MenuSlider.class) && !item.isEnabled())
			return -127;

		if(item != null && item.isFocused())
			return 16 - (totalclock & 0x3F);
		return 0;
	}

	@Override
	public int getPal(BuildFont font, MenuItem item) {
		if(item != null)
		{
			if(item.isFocused()) {
				return 70;
			}

			if(!item.isEnabled()) {
				return 96;
			}

			if(font != null && font.getClass().equals(MenuFont.class))
				return item.pal;
			return 228;
		}

		return 228;
	}

	@Override
	public void mDrawMouse(int x, int y) {
		if(!app.pCfg.menuMouse) return;

		int zoom = scale(0x10000, ydim, 200);
		int czoom = mulscale(0x8000, mulscale(zoom, app.pCfg.gMouseCursorSize, 16), 16);
		int xoffset = 0;
		int yoffset = 0;
		int ang = 0;

		engine.rotatesprite((x + xoffset) << 16, (y + yoffset) << 16, czoom, ang, MOUSECURSOR, 0, 0, 8, 0, 0, xdim-1, ydim-1);
	}

	@Override
	public void mDrawBackButton() {
		if(!app.pCfg.menuMouse)
			return;

		int zoom = scale(BACKSCALE, ydim, 200);
		if(mCount > 1) {
			//Back button
			Tile pic = engine.getTile(BACKBUTTON);

			int shade = 4 + mulscale(16, sintable[(20 * totalclock) & 2047], 16);
			engine.rotatesprite(0, (ydim-mulscale(pic.getHeight(), zoom, 16))<<16, zoom, 0, BACKBUTTON, shade, 0, 8|16, 0, 0, mulscale(zoom,pic.getWidth()-1, 16), ydim-1);
		}
	}

	@Override
	public boolean mCheckBackButton(int mx, int my) {
		int zoom = scale(BACKSCALE, ydim, 200);
		Tile pic = engine.getTile(BACKBUTTON);

		int size = mulscale(pic.getWidth(), zoom, 16);
		int bx = 0;
		int by = ydim - mulscale(pic.getHeight(), zoom, 16);
		if(mx >= bx && mx < bx + size)
			if(my >= by && my < by + size)
				return true;
		return false;
	}

	@Override
	public void mSound(MenuItem item, MenuOpt opt) {
		/* nothing */
	}

	@Override
	public void mPostDraw(MenuItem item) {
		if ( item.isFocused() ) {
			int scale = 32768;
			int xoff = 0, yoff = 6;

	    	if (item instanceof MenuList) {
				if (item instanceof MenuResolutionList) {
					MenuList list = (MenuList) item;

					int px = list.x;
					scale = 24000;

					int focus = list.l_nFocus;
					if (focus == -1 || focus < list.l_nMin || focus >= list.l_nMin + list.nListItems)
						return;

					int py = list.y + (focus - list.l_nMin) * (list.mFontOffset());
					xoff = 35;
					yoff = 3;
					engine.rotatesprite((px + xoff)<<16,(py + yoff)<<16,scale,0,626,0,0,10,0,0,xdim-1,ydim-1);
				} else if (item instanceof MenuKeyboardList || item instanceof MenuJoyList) {
					MenuList list = (MenuList) item;
					int px = list.x;
					int py = list.y + (list.l_nFocus - list.l_nMin) * (list.mFontOffset());
					scale = 24000;
					xoff = -10;
					yoff = 3;

					int focus = list.l_nFocus;
					if (focus == -1 || focus < list.l_nMin || focus >= list.l_nMin + list.nListItems)
						return;

					engine.rotatesprite((px + xoff)<<16,(py + yoff)<<16,scale,0,626,0,0,10,0,0,xdim-1,ydim-1);

				}
	    	}
	    	else
	    	{
	    		int px = item.x;
		    	int py = item.y;


		    	if (item.align == 1) {
		    		int centre = 320 >> 2;
					xoff = centre;
					if (item.font != app.getFont(2))
						xoff -= 15;
		    	}

		    	if (item.font != app.getFont(2)) {
					xoff -= 10;
					yoff -= 3;
					scale = 24000;
		    	}

		    	if (item instanceof MenuVariants) {
		    		xoff += 10;
		    		yoff += 15;
		    	}
		    	engine.rotatesprite((px + xoff)<<16,(py + yoff)<<16,scale,0,626,0,0,10,0,0,xdim-1,ydim-1);
	    	}
		}
	}

}
