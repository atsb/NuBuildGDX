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
import static ru.m210projects.Build.Engine.totalclock;
import static ru.m210projects.Build.Gameutils.BClipLow;
import static ru.m210projects.Build.Gameutils.BClipRange;

import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.Input.ButtonMap;
import ru.m210projects.Build.Input.BuildControllers;
import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Build.Pattern.BuildFont;
import ru.m210projects.Build.Pattern.BuildFont.TextAlign;
import ru.m210projects.Build.Pattern.BuildGame;
import ru.m210projects.Build.Pattern.MenuItems.MenuHandler.MenuOpt;
import ru.m210projects.Build.Settings.BuildConfig.GameKeys;
import ru.m210projects.Build.Settings.BuildConfig.MenuKeys;

public abstract class MenuJoyList extends MenuKeyboardList {

	private final BuildControllers gpmanager;
	public int menupal;

	public MenuJoyList(BuildGame app, BuildFont font, int x, int y, int width,
			int len, MenuProc callback) {
		super(app.pSlider, app.pCfg, font, x, y, width, len, callback);
		this.gpmanager = app.pInput.ctrlGetGamepadManager();
		this.len += cfg.joymap.length;
	}

	@Override
	public void draw(MenuHandler handler) {
		int px = x, py = y;
		boolean offset = false;
		for(int i = l_nMin; i >= 0 && i < l_nMin + nListItems && i < len; i++) {	
			int pal = this.pal_left; 
			int pal2 = this.pal_right; 
			int shade = handler.getShade(i == l_nFocus? m_pMenu.m_pItems[m_pMenu.m_nFocus] : null);
			String text;
			String key;

			if(i < cfg.joymap.length) {
				text = cfg.joymap[i].getName();
				pal = menupal;
			} else {
				text = keynames[i - cfg.joymap.length].getName();
				if( i - cfg.joymap.length == GameKeys.Menu_Toggle.getNum() )
					pal = menupal;
			}
		
			if(l_nMin < cfg.joymap.length && i == cfg.joymap.length) {
				py += mFontOffset();
				offset = true;
			}
			
			if(i == l_nFocus)
				pal2 = pal = handler.getPal(font, m_pMenu.m_pItems[m_pMenu.m_nFocus]);

			if(offset && i >= l_nMin + nListItems - 1)
				break;
				
			if(i < cfg.joymap.length)
			{
				key = ButtonMap.buttonName(cfg.gJoyMenukeys[((MenuKeys)cfg.joymap[i]).getJoyNum()]);
			} else {
				if(cfg.keymap[i - cfg.joymap.length] instanceof MenuKeys)
					key = ButtonMap.buttonName(cfg.gJoyMenukeys[((MenuKeys)cfg.keymap[i - cfg.joymap.length]).getJoyNum()]);
				else if(cfg.gpadkeys[i - cfg.joymap.length] >= 0)
					key = ButtonMap.buttonName(cfg.gpadkeys[i - cfg.joymap.length]);
				else key = "N/A";
			}

			if ( i == l_nFocus ) {
				if(l_set == 1 && (totalclock & 0x20) != 0)
				{
					key = "____";
				}
			}

			char[] k = key.toCharArray();
			
			font.drawText(px, py, text.toCharArray(), shade, pal, TextAlign.Left, 2, fontShadow);		
			font.drawText(x + width - slider.getScrollerWidth() - 2 - font.getWidth(k), py, k, shade, pal2, TextAlign.Left, 2, fontShadow);		
	
			py += mFontOffset();
		}

		scrollerHeight = nListItems * mFontOffset();

		//Files scroll
		int nList = BClipLow(len - nListItems, 1);
		int posy = y + (scrollerHeight - slider.getScrollerHeight()) * l_nMin / nList;
		
		scrollerX = x + width - slider.getScrollerWidth() + 5;
		slider.drawScrollerBackground(scrollerX, y, scrollerHeight, 0, 0);
		slider.drawScroller(scrollerX, posy, handler.getShade(isTouched ? m_pMenu.m_pItems[m_pMenu.m_nFocus] : null), 0);
		
		handler.mPostDraw(this);
	}

	@Override
	public boolean callback(MenuHandler handler, MenuOpt opt) {
		if(l_set == 0) {
			switch(opt)
			{
			case MWUP:
				if(l_nMin > 0)
					l_nMin--;
				return false;
			case MWDW:
				if(l_nMin < len - nListItems)
					l_nMin++;
				return false;
			case UP:
				l_nFocus--;
				if(l_nFocus >= 0 && l_nFocus < l_nMin)
					l_nMin--;
				if(l_nFocus < 0) {
					l_nFocus = len - 1;
					l_nMin = len - nListItems;
				}
				
				return false;
			case DW:
				l_nFocus++;
				if(l_nFocus >= l_nMin + nListItems && l_nFocus < len)
					l_nMin++;
				if(l_nFocus >= len) {
					l_nFocus = 0;
					l_nMin = 0;
				}
				
				if(l_nMin < cfg.joymap.length && l_nFocus == l_nMin + nListItems - 1) {
					l_nMin++;
				}

				return false;
			case ENTER:
			case LMB:
				if ( (flags & 4) == 0 ) return false;
				
				if(opt == MenuOpt.LMB && isTouched)
				{
					if(len <= nListItems)
						return false;

					int nList = BClipLow(len - nListItems, 1);
					int nRange = scrollerHeight;
					int py = y;

					l_nFocus = -1;
					l_nMin = BClipRange(((touchY - py) * nList) / nRange, 0, nList);
					
					return false;
				}
				
				if(l_nFocus != -1 && callback != null) 
					callback.run(handler, this);
				
				getInput().resetKeyStatus();
				return false;
			case DELETE:
				if(l_nFocus == -1) return false;
				
				if(l_nFocus < cfg.joymap.length)
					cfg.gJoyMenukeys[((MenuKeys)cfg.joymap[l_nFocus]).getJoyNum()] = -1;
				else
				{
					int focus = l_nFocus - cfg.joymap.length;
					cfg.gpadkeys[focus] = -1;
					if(focus == GameKeys.Show_Console.getNum()) {
						Console.setCaptureKey(-1, 3);
					}
				}

				return false;
			case PGUP:
				l_nFocus -= (nListItems - 1);
				if(l_nFocus >= 0 && l_nFocus < l_nMin)
					if(l_nMin > 0) l_nMin -= (nListItems - 1);
				if(l_nFocus < 0 || l_nMin < 0) {
					l_nFocus = 0;
					l_nMin = 0;
				}
				return false;
			case PGDW:
				l_nFocus += (nListItems - 1);
				if(l_nFocus >= l_nMin + nListItems && l_nFocus < len)
					l_nMin += (nListItems - 1);
				if(l_nFocus >= len || l_nMin > len - nListItems) {
					l_nFocus = len - 1;
					if(len >= nListItems)
						l_nMin = len - nListItems;
					else l_nMin = len - 1;
				}
				return false;
			case HOME:
				l_nFocus = 0;
				l_nMin = 0;
				return false;
			case END:
				l_nFocus = len - 1;
				if(len >= nListItems)
					l_nMin = len - nListItems;
				else l_nMin = len - 1;
				return false;
			default:
				return m_pMenu.mNavigation(opt);
			}
		}
		else
		{
			l_pressedId = opt;
			if((flags & 4) != 0 && callback != null)
				callback.run(handler, this);

			if(l_nFocus == GameKeys.Menu_Toggle.getNum())
				gpmanager.resetButtonStatus();

			return false;
		}
	}

	@Override
	public boolean mouseAction(int mx, int my) {
		if(l_set != 0)
			return false;
		
		if(!BuildGdx.input.isTouched()) 
			isTouched = false;
				
		touchY = my;
		if(mx > scrollerX && mx < scrollerX + slider.getScrollerWidth()) 
		{
			isTouched = BuildGdx.input.isTouched();
			return true;
		}

		if(!isTouched) {
			int py = y;
			for(int i = l_nMin; i >= 0 && i < l_nMin + nListItems && i < len; i++) {	
				if(my >= py && my < py + font.getHeight())
				{
					if(l_nMin < cfg.joymap.length && i == cfg.joymap.length) 
						return false;
	
					l_nFocus = i;
					if(l_nMin < cfg.joymap.length && i > cfg.joymap.length) 
						l_nFocus--;
	
					return true;
				}
			    
				py += mFontOffset();
			}
		}

		return false;
	}
}
