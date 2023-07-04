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

import static ru.m210projects.Blood.Main.gGameScreen;

import ru.m210projects.Blood.Main;
import ru.m210projects.Blood.Types.BloodIniFile;
import ru.m210projects.Build.FileHandle.FileEntry;
import ru.m210projects.Build.Pattern.MenuItems.BuildMenu;
import ru.m210projects.Build.Pattern.MenuItems.MenuButton;
import ru.m210projects.Build.Pattern.MenuItems.MenuHandler;
import ru.m210projects.Build.Pattern.MenuItems.MenuItem;
import ru.m210projects.Build.Pattern.MenuItems.MenuProc;
import ru.m210projects.Build.Pattern.MenuItems.MenuTitle;

public class MenuDifficulty extends BuildMenu {
	
	private BloodIniFile ini;
	private FileEntry map;
	private int episodeNum;
	private final MenuCustom custom;
	
	public MenuDifficulty(Main app)
	{
		MenuProc newGameProc = new MenuProc() {
			@Override
			public void run( MenuHandler handler, MenuItem pItem ) {
				MenuButton button = (MenuButton) pItem;
				int skill = button.specialOpt;
				gGameScreen.newgame(false, map != null ? map : ini, episodeNum, 0, skill, skill, skill, false);
			}
		};

		int pos = 50;
		MenuButton Skill0 = new MenuButton("STILL KICKING", app.getFont(1), 0, pos, 320, 1, 0, null, -1, newGameProc, 0);
		Skill0.fontShadow = true;
		MenuButton Skill1 = new MenuButton("PINK ON THE INSIDE", app.getFont(1), 0, pos += 20, 320, 1, 0, null, -1, newGameProc, 1);
		Skill1.fontShadow = true;
		MenuButton Skill2 = new MenuButton("LIGHTLY BROILED", app.getFont(1), 0, pos += 20, 320, 1, 0, null, -1, newGameProc, 2);
		Skill2.fontShadow = true;
		MenuButton Skill3 = new MenuButton("WELL DONE", app.getFont(1), 0, pos += 20, 320, 1, 0, null, -1, newGameProc, 3);
		Skill3.fontShadow = true;
		MenuButton Skill4 = new MenuButton("EXTRA CRISPY", app.getFont(1), 0, pos += 20, 320, 1, 0, null, -1, newGameProc, 4);
		Skill4.fontShadow = true;
		MenuButton SkillC = new MenuButton("< CUSTOM >", app.getFont(1), 0, pos += 20, 320, 1, 8, custom = new MenuCustom(app), -1, null, 0);
		SkillC.fontShadow = true;
		
		MenuTitle DiffTitle = new MenuTitle(app.pEngine, "DIFFICULTY", app.getFont(1), 160, 20, 2038);
		addItem(DiffTitle, false);
		addItem(Skill0, false);
		addItem(Skill1, false);
		addItem(Skill2, true);
		addItem(Skill3, false);
		addItem(Skill4, false);
		addItem(SkillC, false);
		addItem(app.menu.addMenuBlood(), false);
	}

	public void setMap(FileEntry map)
	{
		custom.setMap(map);
		this.map = map;
		this.ini = null;
		this.episodeNum = -1; 
	}

	public void setEpisode(BloodIniFile ini, int episodeNum)
	{
		custom.setEpisode(ini, episodeNum);
		this.map = null;
		this.ini = ini;
		this.episodeNum = episodeNum; 
	}
}
