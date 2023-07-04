// This file is part of BloodGDX.
// Copyright (C) 2017-2019  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// BloodGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// BloodGDX is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with BloodGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.Blood.Menus;

import static ru.m210projects.Blood.Factory.BloodMenuHandler.DIFFICULTY;
import static ru.m210projects.Blood.LEVELS.kMaxEpisode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ru.m210projects.Blood.Main;
import ru.m210projects.Blood.Factory.BloodMenuHandler;
import ru.m210projects.Blood.Types.BloodIniFile;
import ru.m210projects.Build.Pattern.MenuItems.BuildMenu;
import ru.m210projects.Build.Pattern.MenuItems.MenuHandler;
import ru.m210projects.Build.Pattern.MenuItems.MenuItem;
import ru.m210projects.Build.Pattern.MenuItems.MenuList;
import ru.m210projects.Build.Pattern.MenuItems.MenuProc;
import ru.m210projects.Build.Pattern.MenuItems.MenuTitle;

public class MenuNewAddon extends BuildMenu {

	private BloodIniFile ini;
	private final MenuList mSlot;
	private final List<char[]> mEpisodelist;
	private final int[] episodeNum;
	
	public MenuNewAddon(Main app)
	{
		final BloodMenuHandler menu = app.menu;
		MenuTitle EpTitle = new MenuTitle(app.pEngine, "Episode", app.getFont(1), 160, 20, 2038);
		
		mEpisodelist = new ArrayList<char[]>();
		episodeNum = new int[kMaxEpisode];
		MenuProc newEpProc = new MenuProc() {
			@Override
			public void run( MenuHandler handler, MenuItem pItem ) {
				MenuList button = (MenuList) pItem;
				MenuDifficulty next = (MenuDifficulty) menu.mMenus[DIFFICULTY];
				next.setEpisode(ini, episodeNum[button.l_nFocus]);
				menu.mOpen(next, -1);
			}
		};
		
		mSlot = new MenuList(mEpisodelist, app.getFont(1), 0, 45, 320, 1, null, newEpProc, kMaxEpisode) {
			@Override
			public int mFontOffset() {
				return font.getHeight() + 5;
			}
		};
		mSlot.fontShadow = true;
		
		addItem(EpTitle, false);
		addItem(mSlot, true);
		addItem(menu.addMenuBlood(), false);
	}
	
	public void setEpisode(BloodIniFile ini)
	{
		this.ini = ini;
		mEpisodelist.clear();
		Arrays.fill(episodeNum, -1);
		for (int i = 0; i < kMaxEpisode; i++) {
			if (ini.set("Episode" + (i + 1))) {
				String BloodBatchOnly = ini.GetKeyString("BloodBathOnly");
				if (BloodBatchOnly != null && BloodBatchOnly.charAt(0) == '1')
					continue;
				
				String title = ini.GetKeyString("Title");
				if (title == null || title.isEmpty())
					title = "Episode" + (i + 1);
				episodeNum[mEpisodelist.size()] = i;
				mEpisodelist.add(title.toCharArray());
			}
		}
		mSlot.len = mEpisodelist.size();
		mSlot.l_nFocus = mSlot.l_nMin = 0;
	}
}
