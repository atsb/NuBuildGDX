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

import ru.m210projects.Build.Pattern.BuildFont;
import ru.m210projects.Build.Pattern.BuildFont.TextAlign;
import ru.m210projects.Build.Pattern.MenuItems.MenuHandler.MenuOpt;

public class MenuButton extends MenuItem
{

	public BuildMenu nextMenu;
	public int nItem;
	public MenuProc specialCall;
	public int specialOpt;

	public MenuButton(Object text, BuildFont font, int x, int y, int width, int align, int pal, BuildMenu nextMenu, int nItem, MenuProc specialCall, int specialOpt) {
		super(text, font);
		
		this.flags = 3 | 4;

		this.pal = pal;
		this.x = x;
		this.y = y;
		this.width = width;
		this.align = align;
		this.nextMenu = nextMenu;
		this.nItem = nItem;
		this.specialCall = specialCall;
		this.specialOpt = specialOpt;
	}

	@Override
	public void draw(MenuHandler handler) {
		if ( text != null )
		{
			int shade = handler.getShade(this);
		   
		    int px = x;
		    int pal = handler.getPal(font, this);
		    
		    if(align == 1) 
		        px = width / 2 + x - font.getWidth(text) / 2;
		    if(align == 2) 
		        px = x + width - 1 - font.getWidth(text);

		    font.drawText(px, y, text, shade, pal, TextAlign.Left, 2, fontShadow);
		}
		
		handler.mPostDraw(this);
	}

	@Override
	public boolean callback(MenuHandler handler, MenuOpt opt) {
		if ( (flags & 4) != 0 && (opt == MenuOpt.ENTER || opt == MenuOpt.LMB) )
		{
			if ( specialCall != null )
				specialCall.run(handler, this);
		    if ( nextMenu != null )
		    	handler.mOpen(nextMenu, nItem);
		}
		else {
			return m_pMenu.mNavigation(opt);
		}
		return false;
	}
	
	@Override
	public void open() {
		
	}
	
	@Override
	public void close() {
		
	}

	@Override
	public boolean mouseAction(int mx, int my) {
		if(text != null)
		{
			int wd = font.getWidth(text);
			int px = x;
			if(align == 1) 
		        px = width / 2 + x - wd / 2;
		    
			if(align == 2) 
				px = x + width - 1 - wd;

			if(mx > px && mx < px + wd)
                return my > y && my < y + font.getHeight();
		}
		return false;
	}
}