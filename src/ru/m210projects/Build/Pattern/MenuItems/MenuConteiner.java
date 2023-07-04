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

public class MenuConteiner extends MenuItem
{
	public int num;
	public MenuProc callback;
	public char[][] list;
	public BuildFont listFont;
	public boolean listShadow;
	
	public MenuConteiner(Object text, BuildFont font, int x, int y, int width, String[] list, int num, MenuProc callback)
	{
		super(text, font);
		this.listFont = font;
		this.flags = 3 | 4;
		if(list != null)
		{
			this.list = new char[list.length][];
			for(int i = 0; i < list.length; i++)
				this.list[i] = list[i].toCharArray();
		}

		this.x = x;
		this.y = y;
		this.width = width;
		this.callback = callback;
		this.num = num;
		this.pal = 0;
	}
	
	public MenuConteiner(Object text, BuildFont font, BuildFont listFont, int x, int y, int width, String[] list, int num, MenuProc callback)
	{
		this(text, font, x, y, width, list, num, callback);
		this.listFont = listFont;
	}
	
	@Override
	public void draw(MenuHandler handler) {
		int px = x, py = y;
		
		char[] key = null;
		if(list != null && num != -1 && num < list.length) 
			key = list[num];	

		int pal = handler.getPal(font, this);
		int shade = handler.getShade(this);
		font.drawText(px, py, text, shade, pal, TextAlign.Left, 2, fontShadow);
		
		if(key != null) {
			int bound = 10;
			int w1 = font.getWidth(text);
			int w2 = listFont.getWidth(key);

			if(w2 + bound >= width - w1) {
				int tx = px + w1 + bound;
				int ty = py + (font.getHeight() - listFont.getHeight()) / 2;
				brDrawText(listFont, key, tx + bound, ty, shade, handler.getPal(listFont, this), 0, px + width - 1);
			} else listFont.drawText(x + width - 1 - listFont.getWidth(key), py + (font.getHeight() - listFont.getHeight()) / 2, key, shade, handler.getPal(listFont, this), TextAlign.Left, 2, listShadow);
		}
		handler.mPostDraw(this);
	}
	
	protected void brDrawText( BuildFont font, char[] text, int x, int y, int shade, int pal, int x1, int x2 )
	{
		int tptr = 0;

	    while(tptr < text.length && text[tptr] != 0)
	    {
        	if(x > x1 && x <= x2) 
        		x += font.drawChar(x, y, text[tptr], shade, pal, 2, listShadow);
        	else x += font.getWidth(text[tptr]);
        	
	        tptr++;
	    }
	}

	@Override
	public boolean callback(MenuHandler handler, MenuOpt opt) {
		
		switch(opt)
		{
		case LEFT:
		case MWDW:
			if ( (flags & 4) == 0 ) return false;
			if(num > 0) num--;
			else num = 0;
			if(callback != null)
				callback.run(handler, this);
			return false;
		case RIGHT:
		case MWUP:
			if ( (flags & 4) == 0 ) return false;
			if(num < list.length - 1) num++;
			else num = list.length - 1;
			if(callback != null)
				callback.run(handler, this);
			return false;
		case ENTER:
		case LMB:
			if ( (flags & 4) == 0 ) return false;
			if(num < list.length - 1) {
				num++;
			} else num = 0;
			if(callback != null)
				callback.run(handler, this);
			return false;
		default:
			return m_pMenu.mNavigation(opt);
		}
	}

	@Override
	public boolean mouseAction(int mx, int my) {
		if(text != null)
		{
			if(mx > x && mx < x + font.getWidth(text))
				if(my > y && my < y + font.getHeight())
					return true;
		}
		
		if(list == null) return false;
		
		char[] key;
		if(num != -1 && num < list.length) {
			key = list[num];
			int fontx =  listFont.getWidth(key);
			int px = x + width - 1 - fontx;
			if(mx > px && mx < px + fontx)
                return my > y && my < y + font.getHeight();
		}
		
		return false;
	}

	@Override
	public void open() {
	}

	@Override
	public void close() {
	}	
}
