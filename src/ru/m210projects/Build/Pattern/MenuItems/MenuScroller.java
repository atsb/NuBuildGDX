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

import static ru.m210projects.Build.Gameutils.*;

import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.Pattern.MenuItems.MenuHandler.MenuOpt;

public class MenuScroller extends MenuItem {

	protected MenuList parent;
	protected int touchY;
	protected boolean isTouched;
	protected static MenuScroller touchedObj;
	
	protected SliderDrawable slider;
	protected int height;
	
	public MenuScroller(SliderDrawable slider, MenuList parent, int x) {
		super(null, null);
		
		this.flags = 2;
		this.parent = parent;
		this.slider = slider;
		this.x = x;
		this.y = parent.y;

		this.height = parent.nListItems * parent.mFontOffset();
		this.width = slider.getScrollerWidth();
	}

	@Override
	public void draw(MenuHandler handler) {
		int nList = BClipLow(parent.len - parent.nListItems, 1);
		int nRange = height - slider.getScrollerHeight();
		int posy = y + nRange * parent.l_nMin / nList;
		
		slider.drawScrollerBackground(x, y, height, 0, pal);
		slider.drawScroller(x, posy, 0, pal);

		if(touchedObj == this) {
			parent.l_nFocus = -1;
			parent.l_nMin = BClipRange(((touchY - y) * nList) / nRange, 0, nList);
		}
	}

	@Override
	public boolean callback(MenuHandler handler, MenuOpt opt) {
		return m_pMenu.mNavigation(opt);
	}

	@Override
	public boolean mouseAction(int mx, int my) {
		touchY = my;
		if(!BuildGdx.input.isTouched())
			touchedObj = null;

		if(mx >= x && mx < x + width)
		{
			if(my >= y && my < y + height) {
				if(m_pMenu.m_pItems[m_pMenu.m_nFocus] != this)
				{
					for ( short i = 0; i < m_pMenu.m_nItems; ++i )
						if(m_pMenu.m_pItems[i] == this)
							m_pMenu.m_nFocus = i; //autofocus to scroller
				}
				
				if(BuildGdx.input.isTouched()) 
					touchedObj = this;
			}
		}

		return touchedObj != null;
	}

	@Override
	public void open() {
	}

	@Override
	public void close() {
	}

}
