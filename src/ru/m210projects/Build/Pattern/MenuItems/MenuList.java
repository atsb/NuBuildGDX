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

import java.util.List;

import ru.m210projects.Build.Pattern.BuildFont;
import ru.m210projects.Build.Pattern.BuildFont.TextAlign;
import ru.m210projects.Build.Pattern.MenuItems.MenuHandler.MenuOpt;

public class MenuList extends MenuItem {
	public int len;
	public int l_nMin = 0;
	public int l_nFocus;
	public int nListItems;
	public List<char[]> text;
	public MenuProc callback;
	public BuildMenu nextMenu;

	public MenuList(List<char[]> text, BuildFont font, int x, int y, int width, int align, BuildMenu nextMenu,
			MenuProc callback, int nListItems) {

		super(null, font);
		this.text = text;
		this.align = align;
		this.flags = 3 | 4;
		this.m_pMenu = null;
		this.x = x;
		this.y = y;
		this.width = width;
		this.nListItems = nListItems;
		this.nextMenu = nextMenu;
		this.callback = callback;
		if (text != null)
			this.len = text.size();
	}

	public int mFontOffset() {
		return font.getHeight() + 2;
	}

	@Override
	public void draw(MenuHandler handler) {
		if (text.size() > 0) {
			int px = x, py = y;
			for (int i = l_nMin; i >= 0 && i < l_nMin + nListItems && i < len; i++) {
				int pal = this.pal; // handler.getPal(font, i == l_nFocus ? this : null);
				if (i == l_nFocus)
					pal = handler.getPal(font, this);
				int shade = handler.getShade(i == l_nFocus ? this : null);

				if (align == 1)
					px = width / 2 + x - font.getWidth(text.get(i)) / 2;
				if (align == 2)
					px = x + width - 1 - font.getWidth(text.get(i));
				font.drawText(px, py, text.get(i), shade, pal, TextAlign.Left, 2, fontShadow);
				py += mFontOffset();
			}
		} else {
			int pal = handler.getPal(font, this);

			String text = "List is empty";
			int fontx = font.getWidth(text.toCharArray());
			int px = x, py = y;
			if (align == 1)
				px = width / 2 + x - fontx / 2;
			if (align == 2)
				px = x + width - 1 - fontx;

			int shade = handler.getShade(this);
			font.drawText(px, py, text.toCharArray(), shade, pal, TextAlign.Left, 2, fontShadow);
		}

		handler.mPostDraw(this);
	}

	@Override
	public boolean callback(MenuHandler handler, MenuOpt opt) {
		switch (opt) {
		case MWUP:
			ListMouseWheelUp(handler);
			return false;
		case MWDW:
			ListMouseWheelDown(handler, len);
			return false;
		case UP:
			ListUp(handler, len);
			return false;
		case DW:
			ListDown(handler, len);
			return false;
		case LEFT:
			ListLeft(handler);
			return false;
		case RIGHT:
			ListRight(handler);
			return false;
		case ENTER:
		case LMB:
			if ((flags & 4) == 0)
				return false;
			if (len > 0)
				ListCallback(handler, opt);
			return false;
		case ESC:
		case RMB:
			ListEscape(handler, opt);
			return true;
		case PGUP:
			ListPGUp(handler);
			return false;
		case PGDW:
			ListPGDown(handler, len);
			return false;
		case HOME:
			ListHome(handler);
			return false;
		case END:
			ListEnd(handler, len);
			return false;
		default:
			ListDefault(handler, opt);
			return false;
		}
	}

	@Override
	public boolean mouseAction(int mx, int my) {
		if (len > 0) {
			int px = x, py = y;
			for (int i = l_nMin; i >= 0 && i < l_nMin + nListItems && i < len; i++) {
				int wd = font.getWidth(text.get(i));
				if (align == 1)
					px = width / 2 + x - wd / 2;
				if (align == 2)
					px = x + width - 1 - wd;

				if (mx > px && mx < px + wd)
					if (my > py && my < py + font.getHeight()) {
						l_nFocus = i;
						return true;
					}

				py += mFontOffset();
			}
		}
		return false;
	}

	protected void ListEnd(MenuHandler handler, int len) {
		l_nFocus = len - 1;
		if (len >= nListItems)
			l_nMin = len - nListItems;
		else if (l_nFocus >= l_nMin + nListItems)
			l_nMin = len - 1;
	}

	protected void ListHome(MenuHandler handler) {
		l_nFocus = 0;
		l_nMin = 0;
	}

	protected void ListPGUp(MenuHandler handler) {
		l_nFocus -= (nListItems - 1);
		if (l_nFocus >= 0 && l_nFocus < l_nMin)
			if (l_nMin > 0)
				l_nMin -= (nListItems - 1);
		if (l_nFocus < 0 || l_nMin < 0) {
			l_nFocus = 0;
			l_nMin = 0;
		}
	}

	protected void ListPGDown(MenuHandler handler, int len) {
		l_nFocus += (nListItems - 1);
		if (l_nFocus >= l_nMin + nListItems && l_nFocus < len)
			l_nMin += (nListItems - 1);
		if (l_nFocus >= len || l_nMin > len - nListItems) {
			l_nFocus = len - 1;
			if (len >= nListItems)
				l_nMin = len - nListItems;
			else if (l_nFocus >= l_nMin + nListItems)
				l_nMin = len - 1;
		}
	}

	protected void ListUp(MenuHandler handler, int len) {
		l_nFocus--;
		if (l_nFocus >= 0 && l_nFocus < l_nMin)
			if (l_nMin > 0)
				l_nMin--;
		if (l_nFocus < 0) {
			l_nFocus = len - 1;
			l_nMin = len - nListItems;
			if (l_nMin < 0)
				l_nMin = 0;
		}
	}

	protected void ListDown(MenuHandler handler, int len) {
		l_nFocus++;
		if (l_nFocus >= l_nMin + nListItems && l_nFocus < len)
			l_nMin++;
		if (l_nFocus >= len) {
			l_nFocus = 0;
			l_nMin = 0;
		}
	}

	protected void ListLeft(MenuHandler handler) {
		m_pMenu.mNavUp();
	}

	protected void ListRight(MenuHandler handler) {
		m_pMenu.mNavDown();
	}

	protected void ListMouseWheelUp(MenuHandler handler) {
		if (l_nMin > 0)
			l_nMin--;
	}

	protected void ListMouseWheelDown(MenuHandler handler, int len) {
		if (l_nMin < len - nListItems)
			l_nMin++;
	}

	protected void ListEscape(MenuHandler handler, MenuOpt opt) {
		// l_nFocus = l_nMin = 0;
	}

	protected void ListDefault(MenuHandler handler, MenuOpt opt) {
	}
	
	protected void ListCallback(MenuHandler handler, MenuOpt opt) {
		if (callback != null)
			callback.run(handler, this);
		if (nextMenu != null)
			handler.mOpen(nextMenu, -1);
	}

	@Override
	public void open() {
	}

	@Override
	public void close() {
	}
}
