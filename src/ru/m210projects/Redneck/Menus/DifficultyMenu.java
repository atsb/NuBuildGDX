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

import static ru.m210projects.Redneck.Main.*;
import static ru.m210projects.Redneck.Player.InitPlayers;

import java.util.ArrayList;
import java.util.List;

import static ru.m210projects.Redneck.Globals.*;

import ru.m210projects.Build.FileHandle.FileEntry;
import ru.m210projects.Build.Pattern.MenuItems.BuildMenu;
import ru.m210projects.Build.Pattern.MenuItems.MenuHandler;
import ru.m210projects.Build.Pattern.MenuItems.MenuItem;
import ru.m210projects.Build.Pattern.MenuItems.MenuList;
import ru.m210projects.Build.Pattern.MenuItems.MenuProc;
import ru.m210projects.Redneck.Main;
import ru.m210projects.Redneck.Types.GameInfo;

public class DifficultyMenu extends BuildMenu {

	private GameInfo game;
	private FileEntry map;
	private int episodeNum;
	
	private List<char[]> skillname;
	private MenuList mSlot;
	
	public DifficultyMenu(final Main app)
	{
		skillname = new ArrayList<char[]>();

		addItem(new RRTitle("SELECT SKILL"), false);
		
		MenuProc newGameProc = new MenuProc()
		{
			@Override
			public void run(MenuHandler handler, MenuItem pItem) {
				MenuList button = (MenuList) pItem;
				int nDifficulty = button.l_nFocus;
				InitPlayers();
				gGameScreen.newgame(false, map != null ? map : game, episodeNum, 0, nDifficulty);
			}
		};
		
		mSlot = new MenuList(skillname, app.getFont(2), 0, 59, 320, 1, null, newGameProc, nMaxSkills) {
			@Override
			public void open() {
				if(this.text.size() > 1)
					l_nFocus = 1;
			}
		};
		addItem(mSlot, true);
	}
	
	public void setEpisode(GameInfo game, int episodeNum) {
		this.map = null;
		this.game = game;
		this.episodeNum = episodeNum; 
		
		skillname.clear();
		for (int i = 0; i < nMaxSkills; i++) {
			if(game.skillnames[i] != null) 
				skillname.add(game.skillnames[i].toCharArray());
		}
		mSlot.len = skillname.size();
	}
	
	public void setMap(FileEntry map)
	{
		this.map = map;
		this.game = null;
		this.episodeNum = -1; 
		
		skillname.clear();
		for (int i = 0; i < nMaxSkills; i++) {
			if(defGame.skillnames[i] != null) 
				skillname.add(defGame.skillnames[i].toCharArray());
		}
		mSlot.len = skillname.size();
	}
}
