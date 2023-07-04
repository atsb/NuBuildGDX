//This file is part of BuildGDX.
//Copyright (C) 2017-2018  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
//BuildGDX is free software: you can redistribute it and/or modify
//it under the terms of the GNU General Public License as published by
//the Free Software Foundation, either version 3 of the License, or
//(at your option) any later version.
//
//BuildGDX is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU General Public License for more details.
//
//You should have received a copy of the GNU General Public License
//along with BuildGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.Build.Pattern.MenuItems;

import static ru.m210projects.Build.Engine.xdim;
import static ru.m210projects.Build.Engine.ydim;

import ru.m210projects.Build.Engine;
import ru.m210projects.Build.Pattern.BuildFont;
import ru.m210projects.Build.Pattern.BuildFont.TextAlign;
import ru.m210projects.Build.Pattern.MenuItems.MenuHandler.MenuOpt;

public class MenuTitle extends MenuItem {
	
	public int nTile;
	protected Engine draw;
	
	public MenuTitle(Engine draw, Object text, BuildFont font, int x, int y, int nTile) {
		super(text, font);
		
		this.flags = 1;
		this.width = 0;

		this.x = x;
		this.y = y;
		this.nTile = nTile;
		this.draw = draw;
	}
	
	@Override
	public void draw(MenuHandler handler) {
		if ( text != null )
		{
		    if(nTile != -1)
		    	draw.rotatesprite(160 << 16, y << 16, 65536, 0, nTile, -128, 0, 78, 0, 0, xdim - 1, ydim - 1);
		    font.drawText(x, y - font.getHeight() / 2, text, -128, pal, TextAlign.Center, 2, fontShadow);
		}
		handler.mPostDraw(this);
	}

	@Override
	public boolean callback(MenuHandler handler, MenuOpt opt) {
		return m_pMenu.mNavigation(opt);
	}

	@Override
	public boolean mouseAction(int mx, int my) {
		return false;
	}

	@Override
	public void open() {}

	@Override
	public void close() {}
}
