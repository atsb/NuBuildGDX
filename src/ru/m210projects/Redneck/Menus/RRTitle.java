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

import static ru.m210projects.Build.Engine.xdim;
import static ru.m210projects.Build.Engine.ydim;
import static ru.m210projects.Redneck.Main.*;
import static ru.m210projects.Redneck.Names.MENUBAR;

import ru.m210projects.Build.Pattern.BuildFont.TextAlign;
import ru.m210projects.Build.Pattern.MenuItems.MenuHandler;
import ru.m210projects.Build.Pattern.MenuItems.MenuTitle;

public class RRTitle extends MenuTitle {

	public RRTitle(Object text) {
		super(engine, text, game.getFont(2), 160, 19, MENUBAR);
	}
	
	@Override
	public void draw(MenuHandler handler) {
		if ( text != null )
		{
			draw.rotatesprite(160 << 16, y << 16, 65536, 0, nTile, 16, 0, 78, 0, 0, xdim - 1, ydim - 1);
			font.drawText(x, y - font.getHeight() / 2, text, -128, 0, TextAlign.Center, 2, fontShadow);
		}
	}
}
