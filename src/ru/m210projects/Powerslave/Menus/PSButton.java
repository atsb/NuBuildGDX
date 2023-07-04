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

import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Powerslave.Main.*;

import ru.m210projects.Build.Pattern.MenuItems.BuildMenu;
import ru.m210projects.Build.Pattern.MenuItems.MenuButton;
import ru.m210projects.Build.Pattern.MenuItems.MenuHandler;
import ru.m210projects.Build.Pattern.MenuItems.MenuProc;

public class PSButton extends MenuButton {

	public PSButton(Object text, int x, int y, int width, int align, int pal, BuildMenu nextMenu,
			int nItem, MenuProc specialCall, int specialOpt) {
		super(text, game.getFont(1), x, y, width, align, pal, nextMenu, nItem, specialCall, specialOpt);
	}

	@Override
	public void draw(MenuHandler handler) {
		game.pEngine.rotatesprite(158 << 16, (y - 4) << 16, 52000, 0, 3469, handler.getShade(this), 0, 78, 0, 0, xdim - 1, ydim - 1);
		super.draw(handler);
	}

	@Override
	public void mCheckEnableItem(boolean nEnable) {
		if (nEnable)
			flags = 3 | 4;
		else flags = 3;
	}

	@Override
	public boolean mouseAction(int mx, int my) {
		if(text != null)
		{
			int wd = (int) (engine.getTile(3469).getWidth() / 1.25f);
			int px = x;
			if(align == 1)
		        px = width / 2 + x - wd / 2;

			if(align == 2)
				px = x + width - 1 - wd;

			if(mx > px && mx < px + wd)
				if(my > y && my < y + font.getHeight())
					return true;
		}
		return false;
	}
}
