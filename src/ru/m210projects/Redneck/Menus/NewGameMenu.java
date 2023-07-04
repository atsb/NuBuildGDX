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
import static ru.m210projects.Redneck.ResourceHandler.*;
import static ru.m210projects.Redneck.Globals.*;

import ru.m210projects.Build.Pattern.MenuItems.BuildMenu;
import ru.m210projects.Build.Pattern.MenuItems.MenuButton;
import ru.m210projects.Build.Pattern.MenuItems.MenuHandler;
import ru.m210projects.Build.Pattern.MenuItems.MenuItem;
import ru.m210projects.Build.Pattern.MenuItems.MenuProc;
import ru.m210projects.Redneck.Main;
import ru.m210projects.Redneck.Factory.RRMenuHandler;
import ru.m210projects.Redneck.Types.GameInfo;

public class NewGameMenu extends BuildMenu {

	public NewGameMenu(final Main app)
	{
		final RRMenuHandler menu = (RRMenuHandler) app.menu;

		addItem(new RRTitle("SELECT AN EPISODE"), false);
		final GameInfo RR66Game = levelGetEpisode("game66.con");
		
		MenuProc newEpProc = new MenuProc() {
			@Override
			public void run( MenuHandler handler, MenuItem pItem ) {
				EpisodeButton but = (EpisodeButton) pItem;
				if(RR66Game != null && but.game == RR66Game) {
					NewAddonMenu next = (NewAddonMenu) app.menu.mMenus[NEWADDON];
					next.setEpisode(but.game);
					app.menu.mOpen(next, -1);
				} else {
					DifficultyMenu next = (DifficultyMenu) menu.mMenus[DIFFICULTY];
					next.setEpisode(but.game, but.specialOpt);
					menu.mOpen(next, but.nItem);
				}
			}
		};

		int epnum = 0;
		int pos = 30;
		for(int i = 0; i < 2; i++)
		{
			if(defGame.episodes[i] != null) { //empty check
				EpisodeButton skill = new EpisodeButton(defGame, defGame.episodes[i].Title, app.getFont(2), 0, pos+=19, 320, newEpProc, i);
				addItem(skill, i == 0);
				epnum++;
			}
		}
		
		
		if(RR66Game != null)
		{
			RR66Game.Title = "Route 66";
			EpisodeButton pItem = new EpisodeButton(RR66Game, RR66Game.Title, app.getFont(2), 0, pos += 19, 320, newEpProc, 0);
			addItem(pItem, epnum == 0);
			epnum++;
		}
		
		final RUserContent usercont = (RUserContent) menu.mMenus[USERCONTENT];
		MenuButton mUser = new MenuButton("< USER CONTENT >", app.getFont(2), 0, pos+=25, 320, 1, 2, null, -1, new MenuProc() {
			@Override
			public void run(MenuHandler handler, MenuItem pItem) {
				if(usercont.showmain) 
					usercont.setShowMain(false);
				handler.mOpen(usercont, -1);
			}
		}, -1);
		addItem(mUser, epnum == 0);
	}
}
