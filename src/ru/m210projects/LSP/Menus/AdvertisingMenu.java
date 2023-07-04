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

import ru.m210projects.Build.Pattern.MenuItems.BuildMenu;
import ru.m210projects.Build.Pattern.MenuItems.MenuHandler;
import ru.m210projects.Build.Pattern.MenuItems.MenuHandler.MenuOpt;
import ru.m210projects.Build.Render.GLRenderer.GLInvalidateFlag;
import ru.m210projects.Build.Settings.BuildSettings;

public class AdvertisingMenu extends BuildMenu {

	public AdvertisingMenu() {
		addItem(new ItemPCX(0, 0, 121), true);
		addItem(new ItemPCX(0, 0, 122), false);
		addItem(new ItemPCX(0, 0, 123), false);
		addItem(new ItemPCX(0, 0, 124), false);
		addItem(new ItemPCX(0, 0, 125), false);
		addItem(new ItemPCX(0, 0, 126), false);
		addItem(new ItemPCX(0, 0, 127), false);
		addItem(new ItemPCX(0, 0, 128), false);
		addItem(new ItemPCX(0, 0, 129), false);
		addItem(new ItemPCX(0, 0, 130), false);
		addItem(new ItemPCX(0, 0, 131), false);
		addItem(new ItemPCX(0, 0, 132), false);
		addItem(new ItemPCX(0, 0, 133), false);
	}

	@Override
	public void mDraw(MenuHandler handler) {
		engine.clearview(0);
		super.mDraw(handler);
	}

	@Override
	public boolean mLoadRes(MenuHandler handler, MenuOpt opt) {
		boolean out = super.mLoadRes(handler, opt);

		if (opt == MenuOpt.Close)
			engine.setbrightness(BuildSettings.paletteGamma.get(), palette, GLInvalidateFlag.All);
		else if (opt != MenuOpt.ANY)
			((ItemPCX) m_pItems[m_nFocus]).show();

		return out;
	}
}
