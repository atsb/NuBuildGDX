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

import static ru.m210projects.Build.Engine.getInput;
import static ru.m210projects.Build.Engine.xdim;
import static ru.m210projects.Build.Engine.ydim;
import static ru.m210projects.Build.Render.VideoMode.validmodes;
import static ru.m210projects.Build.Gameutils.*;

import java.util.List;

import ru.m210projects.Build.Engine;
import ru.m210projects.Build.Gameutils.ConvertType;
import ru.m210projects.Build.Pattern.BuildFont;
import ru.m210projects.Build.Pattern.BuildFont.TextAlign;
import ru.m210projects.Build.Pattern.MenuItems.MenuHandler.MenuOpt;

public class MenuResolutionList extends MenuList {

	private final int nBackground;
	protected Engine draw;
	public int transparent = 1;
	public int backgroundPal;

	public MenuResolutionList(Engine draw, List<char[]> text, BuildFont font, int x, int y,
			int width, int align, BuildMenu nextMenu,
			MenuProc specialCall, int nListItems, int nBackground) {
		super(text, font, x, y, width, align, nextMenu, specialCall, nListItems);
		this.nBackground = nBackground;
		this.draw = draw;
	}
	
	@Override
	public void open() {
		l_nFocus = -1;
		l_nMin = 0;
		for (int m = 0; m < validmodes.size(); m++) {
			if ((validmodes.get(m).xdim == xdim)
					&& (validmodes.get(m).ydim == ydim)) {
				l_nFocus = m;
				break;
			}
		}

		if (l_nFocus != -1) {
			if (l_nFocus >= l_nMin + nListItems)
				l_nMin = l_nFocus - nListItems + 1;
		} 
	}
	
	@Override
	public void draw(MenuHandler handler) {
		draw.rotatesprite((x - 10) << 16, (y - 8) << 16, 65536, 0, nBackground, 128, backgroundPal, 10 | 16 | transparent, 0, 0, coordsConvertXScaled(x + width, ConvertType.Normal), coordsConvertYScaled(y + nListItems * mFontOffset() + 3));

		if(text.size() > 0) {
			int px = x, py = y;
			for(int i = l_nMin; i >= 0 && i < l_nMin + nListItems && i < text.size(); i++) {	
				int pal = handler.getPal(font, null);
				int shade = handler.getShade(null);
				if ( i == l_nFocus ) {
					shade = handler.getShade(this);
					pal = handler.getPal(font, this);
				}
			    if(align == 1) 
			        px = width / 2 + x - font.getWidth(text.get(i)) / 2;
			    if(align == 2) 
			        px = x + width - 1 - font.getWidth(text.get(i));
			    font.drawText(px, py, text.get(i), shade, pal, TextAlign.Left, 2, fontShadow);

				py += mFontOffset();
			}
		}
		else 
		{
			int pal = handler.getPal(font, this);
			String text = "List is empty";
			
			int px = x, py = y;		
			if(align == 1) 
		        px = width / 2 + x - font.getWidth(text.toCharArray()) / 2;
		    if(align == 2) 
		        px = x + width - 1 - font.getWidth(text.toCharArray());
		    int shade = handler.getShade(this);
		    
		    font.drawText(px, py, text.toCharArray(), shade, pal, TextAlign.Left, 2, fontShadow);
		}

		handler.mPostDraw(this);
	}
	
	@Override
	public boolean callback(MenuHandler handler, MenuOpt opt) {
		switch(opt)
		{
			case MWUP:
				ListMouseWheelUp(handler);
				return false;
			case MWDW:
				if(text != null)
					ListMouseWheelDown(handler, text.size());
				return false;
			case UP:
				ListUp(handler, text.size());
				return false;
			case DW:
				ListDown(handler, text.size());
				return false;
			case LEFT:
				ListLeft(handler);
				return false;
			case RIGHT:
				ListRight(handler);
				return false;
			case ENTER:
			case LMB:
				if ( (flags & 4) == 0 ) return false;
				
				if(l_nFocus != -1 && text.size() > 0 && callback != null) {
					callback.run(handler, this);
					if ( nextMenu != null )
				    	handler.mOpen(nextMenu, -1);
				}
				getInput().resetKeyStatus();
				return false;
			case ESC:
			case RMB:
				ListEscape(handler, opt);
				return true;
			case PGUP:
				ListPGUp(handler);
				return false;
			case PGDW:
				ListPGDown(handler, text.size());
				return false;
			case HOME:
				ListHome(handler);
				return false;
			case END:
				ListEnd(handler, text.size());
				return false;
			default: 
				return false;
		}
	}

	@Override
	public boolean mouseAction(int mx, int my) {

		if(text.size() > 0) {
			int px = x, py = y;
			for(int i = l_nMin; i >= 0 && i < l_nMin + nListItems && i < text.size(); i++) {	
				int fontx = font.getWidth(text.get(i));
				if(align == 1) 
			        px = width / 2 + x - fontx / 2;
			    if(align == 2) 
			        px = x + width - 1 - fontx;
	
			    if(mx > px && mx < px + fontx)
					if(my > py && my < py + font.getHeight())
					{
						l_nFocus = i;
						return true;
					}
			    
				py += mFontOffset();
			}
		}
		return false;
	}
}
