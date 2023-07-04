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

import static ru.m210projects.Blood.Factory.BloodMenuHandler.*;
import static ru.m210projects.Blood.Globals.*;
import static ru.m210projects.Blood.LEVELS.*;

import ru.m210projects.Blood.Main;
import ru.m210projects.Blood.Factory.BloodMenuHandler;
import ru.m210projects.Blood.Types.BloodIniFile;
import ru.m210projects.Blood.Types.EpisodeInfo;
import ru.m210projects.Build.Pattern.MenuItems.BuildMenu;
import ru.m210projects.Build.Pattern.MenuItems.MenuButton;
import ru.m210projects.Build.Pattern.MenuItems.MenuHandler;
import ru.m210projects.Build.Pattern.MenuItems.MenuItem;
import ru.m210projects.Build.Pattern.MenuItems.MenuProc;
import ru.m210projects.Build.Pattern.MenuItems.MenuTitle;

import java.io.File;

public class MenuNewGame extends BuildMenu {

	public MenuNewGame(Main app)
	{
		final BloodMenuHandler menu = app.menu;
		
		MenuTitle EpTitle = new MenuTitle(app.pEngine, "Episode", app.getFont(1), 160, 20, 2038);
		addItem(EpTitle, false);

		int pos = 25;
		boolean first = true;
		
		MenuProc newEpProc = new MenuProc() {
			@Override
			public void run( MenuHandler handler, MenuItem pItem ) {
				EpisodeButton but = (EpisodeButton) pItem;
				MenuDifficulty next = (MenuDifficulty) menu.mMenus[DIFFICULTY];
				next.setEpisode(but.ini, but.specialOpt);
				menu.mOpen(next, but.nItem);
			}
		};
		
		for (int i = 0; i < kMaxEpisode; i++) {
			EpisodeInfo pEpisode = gEpisodeInfo[i];
			if (((pEpisode.BBOnly & 1) == 0 || pGameInfo.nGameType != 0) && i < nEpisodeCount
					&& pEpisode.Title != null) {

				EpisodeButton pItem = new EpisodeButton(null, pEpisode.Title, app.getFont(1), 0, pos += (app.getFont(1).getHeight() + 3), 320, newEpProc, i);
				pItem.fontShadow = true;
				addItem(pItem, i == 0);
				first = false;
			}
		}

		final BloodIniFile ini = searchCryptic();
		if (ini != null) {
			EpisodeButton pItem = new EpisodeButton(ini, "Cryptic Passage", app.getFont(1), 0, pos += (app.getFont(1).getHeight() + 3), 320, newEpProc, 0);
			pItem.fontShadow = true;
			addItem(pItem, first);
			first = false;
		}

		final BLUserContent usercont = new BLUserContent(app);
		menu.mMenus[USERCONTENT] = usercont;
		MenuButton pItem = new MenuButton("< User Content >", app.getFont(1), 0, pos + 25, 320, 1, 8, null, -1, new MenuProc() {
			@Override
			public void run(MenuHandler handler, MenuItem pItem) {
				if(usercont.showmain) 
					usercont.setShowMain(false);
				handler.mOpen(usercont, -1);
			}
		}, 0);
		pItem.fontShadow = true;
		
		addItem(pItem, first);
		addItem(menu.addMenuBlood(), false);
	}

	private BloodIniFile searchCryptic() {
		final String path[] = { "cryptic.ini", "addons" + File.separator + "cryptic passage" + File.separator + "cryptic.ini" };
		for(String s : path) {
			BloodIniFile ini = levelGetEpisode(s);
			if(ini != null)
				return ini;
		}
		return null;
	}

}
