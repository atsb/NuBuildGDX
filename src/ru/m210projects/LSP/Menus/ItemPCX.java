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

import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.LSP.Main.*;
import static ru.m210projects.LSP.Globals.*;

import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.Pattern.MenuItems.MenuHandler;
import ru.m210projects.Build.Pattern.MenuItems.MenuHandler.MenuOpt;
import ru.m210projects.Build.Pattern.MenuItems.MenuItem;
import ru.m210projects.Build.Types.Tile;
import ru.m210projects.LSP.Types.PCXFile;

public class ItemPCX extends MenuItem {

	private PCXFile fil;
	private byte index;

	public ItemPCX(int x, int y, int index) {
		super(null, null);
		this.x = x;
		this.y = y;
		this.flags |= 11;
		this.index = (byte) index;
	}

	@Override
	public void draw(MenuHandler handler) {
		engine.rotatesprite(x << 16, y << 16, 65536, 512, TILE_ANIM, -128, ANIM_PAL, 4 | 10 | 16 | 64, 0, 0, xdim - 1,
				ydim - 1);
	}

	@Override
	public boolean callback(MenuHandler handler, MenuOpt opt) {
		switch (opt) {
		case LEFT:
		case BSPACE:
		case RMB:
			m_pMenu.mNavUp();
			return false;
		case RIGHT:
		case ENTER:
		case SPACE:
		case LMB:
			m_pMenu.mNavDown();
			return false;
		case UP:
		case DW:
		case ESC:
		case DELETE:
			return m_pMenu.mNavigation(opt);
		default:
			return false;
		}
	}

	public void show() {
		if (fil == null) {
			try {
				fil = new PCXFile(BuildGdx.cache.open((index & 0xFF), ""));
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
		}
		engine.changepalette(fil.getPalette());
		Tile pic = engine.getTile(TILE_ANIM);
		pic.data = fil.getData();
		pic.setWidth(fil.getHeight());
		pic.setHeight(fil.getWidth());
		engine.getrender().invalidatetile(TILE_ANIM, ANIM_PAL, 1 << 4);
	}

	@Override
	public void open() {
	}

	@Override
	public void close() {
	}

	@Override
	public boolean mouseAction(int x, int y) {
		return false;
	}

}
