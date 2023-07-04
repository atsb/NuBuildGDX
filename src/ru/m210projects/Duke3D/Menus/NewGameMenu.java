// This file is part of DukeGDX.
// Copyright (C) 2019  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// DukeGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// DukeGDX is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with DukeGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.Duke3D.Menus;

import static ru.m210projects.Duke3D.Factory.DukeMenuHandler.*;
import static ru.m210projects.Duke3D.Globals.*;

import ru.m210projects.Build.FileHandle.FileUtils;
import ru.m210projects.Build.Pattern.MenuItems.BuildMenu;
import ru.m210projects.Build.Pattern.MenuItems.MenuButton;
import ru.m210projects.Build.Pattern.MenuItems.MenuHandler;
import ru.m210projects.Build.Pattern.MenuItems.MenuItem;
import ru.m210projects.Build.Pattern.MenuItems.MenuProc;
import ru.m210projects.Duke3D.Main;
import ru.m210projects.Duke3D.Factory.DukeMenuHandler;
import ru.m210projects.Duke3D.Types.GameInfo;

public class NewGameMenu extends BuildMenu {

	private static class AddonMenu extends BuildMenu
	{
		public AddonMenu(final Main app, GameInfo... addons)
		{
			addItem(new DukeTitle("SELECT AN ADDON"), false);

			MenuProc newEpProc = new MenuProc() {
				@Override
				public void run( MenuHandler handler, MenuItem pItem ) {
					EpisodeButton but = (EpisodeButton) pItem;

					NewAddonMenu next = (NewAddonMenu) app.menu.mMenus[NEWADDON];
					if(next.setEpisode(but.game)) {
						DifficultyMenu diffic = (DifficultyMenu) app.menu.mMenus[DIFFICULTY];
						diffic.setEpisode(but.game, next.getFirstEpisode());
						app.menu.mOpen(diffic, -1);
					} else app.menu.mOpen(next, -1);
				}
			};

			int pos = 30;
			int add = 0;
			for (GameInfo addon : addons) {
				if (addon != null) {
					addItem(new EpisodeButton(addon, addon.Title, app.getFont(2), 0, pos += 19, 320, newEpProc, 0), add == 0);
					add++;
				}
			}
		}
	}

	private GameInfo searchAddon(DUserContent content, String name, String path) {
		GameInfo addon = null;
		if(path != null) {
			content.checkDirectory(path, "grp");
			content.setShowMain(false);
		}

		for(String keys : episodes.keySet())
		{
			int filenameIndex = -1;
			if(keys != null && (filenameIndex = keys.indexOf(":")) != -1) {
				String grp = FileUtils.getFullName(keys.substring(0, filenameIndex));
				if(grp.equalsIgnoreCase(name)) {
					addon = episodes.get(keys);
					break;
				}
			}
		}

		return addon;
	}

	private GameInfo getAddon(DUserContent content, String name, String path, String newTitle) {
		GameInfo addon = searchAddon(content, name, null);
		if(addon == null)
			addon = searchAddon(content, name, FileUtils.getCorrectPath(path));

		if(addon != null)
			addon.Title = newTitle;

		return addon;
	}

	public NewGameMenu(final Main app)
	{
		final DukeMenuHandler menu = app.menu;

		addItem(new DukeTitle("SELECT AN EPISODE"), false);
		final DUserContent usercont = (DUserContent) menu.mMenus[USERCONTENT];

		GameInfo vaca = null, dc = null, nw = null;
		if(app.gameParam == 0) {
			vaca = getAddon(usercont, "vacation.grp", "addons/vacation", "Life's a Beach");
			if(vaca != null) {
				for(int i = 0; i < vaca.episodes.length; i++) {
					if(vaca.episodes[i] != null && vaca.episodes[i].Title.contains("DUKEMATCH"))
						vaca.episodes[i] = null;
				}
			}
			dc = getAddon(usercont, "dukedc.grp", "addons/dc", "Duke it Out in D.C.");
			nw = getAddon(usercont, "nwinter.grp", "addons/nw", "Nuclear Winter");
		}

		MenuProc newEpProc = new MenuProc() {
			@Override
			public void run( MenuHandler handler, MenuItem pItem ) {
				EpisodeButton but = (EpisodeButton) pItem;
				DifficultyMenu next = (DifficultyMenu) menu.mMenus[DIFFICULTY];
				next.setEpisode(but.game, but.specialOpt);
				menu.mOpen(next, but.nItem);
			}
		};

		int epnum = 0;
		int pos = 30;
		for(int i = 0; i < defGame.nEpisodes; i++)
		{
			if(defGame.episodes[i] != null) { //empty check
				if(app.gameParam == 1 && defGame.episodes[i].Title.contains("MULTIPLAYER")) //disable NAM multiplayer episodes
					continue;

				EpisodeButton skill = new EpisodeButton(defGame, defGame.episodes[i].Title, app.getFont(2), 0, pos+=19, 320, newEpProc, i);
				addItem(skill, i == 0);
				epnum++;
			}
		}

		if(app.gameParam == 0) {
			if(vaca != null || dc != null || nw != null) {
				addItem(new MenuButton("Official addons", app.getFont(2), 0, pos+=19, 320, 1, 0, new AddonMenu(app, vaca, dc, nw), -1, null, -1), epnum == 0);
			} else {
				addItem(new MenuButton("Official addons", app.getFont(2), 0, pos += 19, 320, 1, 0, null, -1, null, -1) {
					@Override
					public void draw(MenuHandler handler) {
						this.mCheckEnableItem(false);
						super.draw(handler);
					}
				}, epnum == 0);
			}
			epnum++;
		}

		MenuButton mUser = new MenuButton("< USER CONTENT >", app.getFont(2), 0, pos+=19, 320, 1, 0, null, -1, new MenuProc() {
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
