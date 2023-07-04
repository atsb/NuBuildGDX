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

import static ru.m210projects.Build.Engine.*;
import com.badlogic.gdx.Input.Keys;

import ru.m210projects.Build.Engine;
import ru.m210projects.Build.Pattern.BuildFont;
import ru.m210projects.Build.Pattern.BuildFont.TextAlign;
import ru.m210projects.Build.Pattern.MenuItems.MenuHandler.MenuOpt;

public abstract class MenuVariants extends MenuTitle
{
	public MenuVariants(Engine draw, String text, BuildFont font, int x, int y) {
		super(draw, text, font, x, y, -1);
		this.flags = 3 | 4;
	}

	@Override
	public void draw(MenuHandler handler) {
		if ( text != null )
		    font.drawText(x, y - font.getHeight() / 2, text, handler.getShade(this), pal, TextAlign.Center, 2, fontShadow);
		
		handler.mPostDraw(this);
	}
	
	@Override
	public boolean callback(MenuHandler handler, MenuOpt opt) {
		if(getInput().getKey(Keys.Y) != 0 || opt == MenuOpt.ENTER || opt == MenuOpt.LMB) {
			positive(handler);
		    getInput().setKey(Keys.Y, 0);
		}
		if(getInput().getKey(Keys.N) != 0 || opt == MenuOpt.ESC || opt == MenuOpt.RMB) {
			negative(handler);
			getInput().setKey(Keys.N, 0);
		}
		return false;
	}

	public abstract void positive(MenuHandler handler);
	
	public void negative(MenuHandler handler)
	{
		handler.mMenuBack();
	}

	@Override
	public boolean mouseAction(int x, int y) {
		return false;
	}
}