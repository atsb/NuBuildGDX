// This file is part of RedneckGDX.
// Copyright (C) 2017-2019  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// RedneckGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// RedneckGDX is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with RedneckGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.Redneck.Menus;

import static ru.m210projects.Redneck.Factory.RRMenuHandler.*;
import static ru.m210projects.Redneck.Globals.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ru.m210projects.Build.Pattern.MenuItems.BuildMenu;
import ru.m210projects.Build.Pattern.MenuItems.MenuHandler;
import ru.m210projects.Build.Pattern.MenuItems.MenuItem;
import ru.m210projects.Build.Pattern.MenuItems.MenuList;
import ru.m210projects.Build.Pattern.MenuItems.MenuProc;
import ru.m210projects.Redneck.Main;
import ru.m210projects.Redneck.Factory.RRMenuHandler;
import ru.m210projects.Redneck.Types.GameInfo;

public class NewAddonMenu extends BuildMenu {

	private GameInfo game;
	private MenuList mSlot;
	private final List<char[]> mEpisodelist;
	private int[] episodeNum;
	
	public NewAddonMenu(Main app)
	{
		final RRMenuHandler menu = app.menu;
		addItem(new RRTitle("SELECT AN EPISODE"), false);
		
		mEpisodelist = new ArrayList<char[]>();
		episodeNum = new int[nMaxEpisodes];
		MenuProc newEpProc = new MenuProc() {
			@Override
			public void run( MenuHandler handler, MenuItem pItem ) {
				MenuList button = (MenuList) pItem;
				DifficultyMenu next = (DifficultyMenu) menu.mMenus[DIFFICULTY];
				next.setEpisode(game, episodeNum[button.l_nFocus]);
				menu.mOpen(next, -1);
			}
		};
		
		mSlot = new MenuList(mEpisodelist, app.getFont(2), 0, 49, 320, 1, null, newEpProc, nMaxEpisodes) {
			@Override
			public int mFontOffset() {
				return font.getHeight() + 5;
			}
		};
		addItem(mSlot, true);
	}
	
	public void setEpisode(GameInfo game)
	{
		this.game = game;
		mEpisodelist.clear();
		Arrays.fill(episodeNum, -1);
		
		for (int i = 0; i < nMaxEpisodes; i++) {
			if(game.episodes[i] != null && game.episodes[i].nMaps != 0) {
				episodeNum[mEpisodelist.size()] = i;
				mEpisodelist.add(game.episodes[i].Title.toCharArray());
			}
		}
		mSlot.len = mEpisodelist.size();
		mSlot.l_nFocus = mSlot.l_nMin = 0;
	}

}
