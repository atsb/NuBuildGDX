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

package ru.m210projects.Build.Pattern.CommonMenus;

import java.io.File;

import ru.m210projects.Build.Engine;
import ru.m210projects.Build.FileHandle.Compat.Path;
import ru.m210projects.Build.Pattern.BuildFont;
import ru.m210projects.Build.Pattern.BuildGame;
import ru.m210projects.Build.Pattern.MenuItems.BuildMenu;
import ru.m210projects.Build.Pattern.MenuItems.MenuHandler;
import ru.m210projects.Build.Pattern.MenuItems.MenuItem;
import ru.m210projects.Build.Pattern.MenuItems.MenuPicnum;
import ru.m210projects.Build.Pattern.MenuItems.MenuProc;
import ru.m210projects.Build.Pattern.MenuItems.MenuScroller;
import ru.m210projects.Build.Pattern.MenuItems.MenuSlotList;
import ru.m210projects.Build.Pattern.MenuItems.MenuText;
import ru.m210projects.Build.Pattern.MenuItems.MenuTitle;
import ru.m210projects.Build.Pattern.Tools.SaveManager;

public abstract class MenuLoadSave extends BuildMenu {

	public MenuPicnum picnum;
	public MenuSlotList list;
	public MenuScroller slider;
	public MenuText mInfo;
	
	public MenuLoadSave(BuildGame app, BuildFont style, int posx, int posy, int posyHelp, int width, int nItems, int listPal, int specPal, int nBackground, MenuProc confirm, boolean saveMenu)
	{
		addItem(getTitle(app, saveMenu ? "Save game" : "Load game"), false);

		picnum = getPicnum(app.pEngine, posx, posy);
		
		MenuProc updateCallback = new MenuProc() {
			@Override
			public void run(MenuHandler handler, MenuItem pItem) {
				MenuSlotList pSlot = (MenuSlotList) pItem;
				if (loadData(pSlot.FileName()))
					picnum.nTile = SaveManager.Screenshot;
				else
					picnum.nTile = picnum.defTile;
			}
		};
		
		list = new MenuSlotList(app.pEngine, app.pSavemgr, style, posx, posy, posyHelp, width, nItems, updateCallback, confirm, listPal, specPal, nBackground, saveMenu) {
			@Override
			public boolean checkFile(String filename) {
				return MenuLoadSave.this.checkFile(filename);
			}
		};
		slider = new MenuScroller(app.pSlider, list, width + posx - app.pSlider.getScrollerWidth());
		mInfo = getInfo(app, posx, posy);
		
		addItem(picnum, false);
		addItem(mInfo, false);
		addItem(list, true);
		addItem(slider, false);
	}
	
	public boolean checkFile(String filename)
	{
		File file = new File(Path.User.getPath() + filename);
		return file.exists();
	}
	
	public abstract boolean loadData(String filename);
	
	public abstract MenuTitle getTitle(BuildGame app, String text);
	
	public abstract MenuPicnum getPicnum(Engine draw, int x, int y);
	
	public abstract MenuText getInfo(BuildGame app, int x, int y);
	
}
