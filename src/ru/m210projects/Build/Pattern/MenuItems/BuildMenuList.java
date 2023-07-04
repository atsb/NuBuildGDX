//This file is part of BuildGDX.
//Copyright (C) 2020  Alexander Makarov-[M210] (m210-2007@mail.ru)
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

import static ru.m210projects.Build.Input.Keymap.MOUSE_LBUTTON;

import java.util.ArrayList;
import java.util.List;

import ru.m210projects.Build.Pattern.BuildGame;
import ru.m210projects.Build.Pattern.MenuItems.BuildMenu;
import ru.m210projects.Build.Pattern.MenuItems.MenuHandler;
import ru.m210projects.Build.Pattern.MenuItems.MenuHandler.MenuOpt;
import ru.m210projects.Build.Pattern.MenuItems.MenuItem;
import ru.m210projects.Build.Pattern.MenuItems.MenuList;
import ru.m210projects.Build.Pattern.MenuItems.MenuScroller;
import ru.m210projects.Build.Pattern.MenuItems.MenuSlider;
import ru.m210projects.Build.Pattern.MenuItems.MenuTitle;

public abstract class BuildMenuList extends BuildMenu {

	public class MenuItemList extends MenuList {

		protected List<MenuItem> m_pItems;
		protected int l_step;
		protected MenuScroller scroller;
		protected int l_OldMouseFocus;

		public MenuItemList(int x, int y, int width, int step, int nShowElements) {
			super(null, null, x, y, width, 0, null, null, nShowElements);

			this.m_pItems = new ArrayList<MenuItem>();
			this.l_step = step;
			this.scroller = null;
			this.l_OldMouseFocus = 0;
		}

		@Override
		public int mFontOffset() {
			return l_step;
		}

		public int addItem(final MenuItem pItem, boolean nFirstItem) {
			if (pItem == null)
				return -1;

			pItem.m_pMenu = BuildMenuList.this;
			m_pItems.add(pItem);
			if (nFirstItem)
				l_nFocus = l_nMin = (short) (m_pItems.size() - 1);

			pItem.listener = new FocusListener() {
				@Override
				public boolean isFocused() {
					return m_pItems.indexOf(pItem) == l_nFocus;
				}
			};

			if (scroller == null && m_pItems.size() > nListItems) {
				scroller = new MenuScroller(app.pSlider, this, width + x - app.pSlider.getScrollerWidth());
				BuildMenuList.this.addScroller(scroller);
			}

			return len = m_pItems.size();
		}

		public int removeItem(MenuItem pItem) {
			if (pItem == null)
				return -1;

			int i = m_pItems.indexOf(pItem);
			if (i != -1 && m_pItems.remove(i) == pItem)
				return i;

			return -1;
		}

		public void clear() {
			m_pItems.clear();
			l_OldMouseFocus = l_nFocus = l_nMin = len = 0;
			if (scroller != null) {
				BuildMenuList.this.removeItem(scroller);
				scroller = null;
			}
		}

		@Override
		public void draw(MenuHandler handler) {
			if (m_pItems.size() > 0) {
				int px = x, py = y;
				for (int i = l_nMin; i >= 0 && i < l_nMin + nListItems && i < len; i++) {
					MenuItem pItem = m_pItems.get(i);

					pItem.x = px;
					pItem.y = py;
					pItem.draw(handler);

					py += l_step;
				}
			}
		}

		@Override
		protected void ListLeft(MenuHandler handler) {
			MenuItem pItem = m_pItems.get(l_nFocus);
			if ((pItem.flags & 4) != 0)
				pItem.callback(handler, MenuOpt.LEFT);
		}

		@Override
		protected void ListRight(MenuHandler handler) {
			MenuItem pItem = m_pItems.get(l_nFocus);
			if ((pItem.flags & 4) != 0)
				pItem.callback(handler, MenuOpt.RIGHT);
		}

		@Override
		protected void ListEscape(MenuHandler handler, MenuOpt opt) {
			l_nFocus = l_nMin = 0;
		}

		@Override
		protected void ListCallback(MenuHandler handler, MenuOpt opt) {
			MenuItem pItem = m_pItems.get(l_nFocus);
			if ((pItem.flags & 4) != 0)
				pItem.callback(handler, opt);
		}

		@Override
		protected void ListDefault(MenuHandler handler, MenuOpt opt) {
			ListCallback(handler, opt);
		}

		@Override
		public boolean mouseAction(int mx, int my) {
			if (len > 0) {
				for (int i = l_nMin; i >= 0 && i < l_nMin + nListItems && i < len; i++) {
					MenuItem pItem = m_pItems.get(i);
					if (((pItem.flags & 2) != 0) && pItem.mouseAction(mx, my)) {
						l_OldMouseFocus = l_nFocus;
						l_nFocus = i;
						return true;
					}
				}
			}
			return false;
		}

		protected boolean mCheckListItemsFlags(int nItem) {
			if (nItem < 0 || nItem >= m_pItems.size() || m_pItems.get(nItem) == null)
				return false;

			MenuItem pItem = m_pItems.get(nItem);
			return (pItem.flags & 1) != 0 && (pItem.flags & 2) != 0;
		}

		@Override
		protected void ListUp(MenuHandler handler, int len) {
			do {
				super.ListUp(handler, len);
			} while (!mCheckListItemsFlags(l_nFocus));
		}

		@Override
		protected void ListDown(MenuHandler handler, int len) {
			do {
				super.ListDown(handler, len);
			} while (!mCheckListItemsFlags(l_nFocus));
		}

		@Override
		protected void ListEnd(MenuHandler handler, int len) {
			super.ListEnd(handler, len);
			if (!mCheckListItemsFlags(l_nFocus))
				ListUp(handler, len);
		}

		@Override
		protected void ListHome(MenuHandler handler) {
			super.ListHome(handler);
			if (!mCheckListItemsFlags(l_nFocus))
				ListDown(handler, len);
		}

		@Override
		protected void ListPGUp(MenuHandler handler) {
			super.ListPGUp(handler);
			if (!mCheckListItemsFlags(l_nFocus))
				ListDown(handler, len);
		}

		@Override
		protected void ListPGDown(MenuHandler handler, int len) {
			super.ListPGDown(handler, len);
			if (!mCheckListItemsFlags(l_nFocus))
				ListUp(handler, len);
		}
	}

	protected MenuItemList list;
	private final BuildGame app;

	public BuildMenuList(BuildGame app, String title, int x, int y, int width, int step, int nShowElements) {
		this.app = app;

		super.addItem(getTitle(app, title), false);
		super.addItem(list = new MenuItemList(x, y, width, step, nShowElements), true);
	}

	protected void addScroller(MenuScroller scroller) {
		if (scroller != null)
			super.addItem(scroller, false);
	}

	@Override
	public int addItem(MenuItem pItem, boolean nFirstItem) {
		if (pItem == null)
			return -1;

		return list.addItem(pItem, nFirstItem);
	}

	public void clear() {
		list.clear();
	}

	@Override
	public boolean mGetFocusedItem(MenuItem m_pItem) {
		if(!super.mGetFocusedItem(m_pItem)) {
			if ( list.l_nFocus >= 0 && list.l_nFocus < list.m_pItems.size())
			    return m_pItem == list.m_pItems.get(list.l_nFocus);
		}
		return false;
	}

	@Override
	public boolean mLoadRes(MenuHandler handler, MenuOpt opt) {
		MenuItem pItem;
		if (list.l_nFocus >= 0 && list.m_pItems.size() > 0
				&& ((pItem = list.m_pItems.get(list.l_nFocus)).flags & 2) != 0) {
			if (pItem instanceof MenuSlider || pItem instanceof MenuScroller) {
				if (app.pInput.ctrlKeyPressed(MOUSE_LBUTTON))
					opt = MenuOpt.LMB;
			}
		}

		if (list.l_OldMouseFocus != list.l_nFocus) {
			opt = MenuOpt.MCHANGE;
			list.l_OldMouseFocus = list.l_nFocus;
		}

		return super.mLoadRes(handler, opt);
	}

	public abstract MenuTitle getTitle(BuildGame app, String text);
}
