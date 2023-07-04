// This file is part of PowerslaveGDX.
// Copyright (C) 2019  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// PowerslaveGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// PowerslaveGDX is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with PowerslaveGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.Powerslave.Menus;

import static ru.m210projects.Powerslave.Globals.*;
import static ru.m210projects.Powerslave.LoadSave.gAutosaveRequest;
import static ru.m210projects.Powerslave.LoadSave.nSaveFile;
import static ru.m210projects.Powerslave.LoadSave.nSaveName;
import static ru.m210projects.Powerslave.Main.gGameScreen;

import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.Pattern.MenuItems.BuildMenu;
import ru.m210projects.Build.Pattern.MenuItems.MenuButton;
import ru.m210projects.Build.Pattern.MenuItems.MenuHandler;
import ru.m210projects.Build.Pattern.MenuItems.MenuItem;
import ru.m210projects.Build.Pattern.MenuItems.MenuProc;
import ru.m210projects.Build.Pattern.MenuItems.MenuTitle;
import ru.m210projects.Powerslave.Main;
import ru.m210projects.Powerslave.Type.EpisodeInfo;

public class NewAddon extends BuildMenu {
	
	private EpisodeInfo addon;
	public NewAddon(final Main app)
	{
		MenuTitle mTitle = new PSTitle(160, 35, 65536);
		int posy = 55;
		MenuProc start = new MenuProc() {
			@Override
			public void run(MenuHandler handler, final MenuItem pItem) {
				BuildGdx.app.postRunnable(new Runnable() {
					@Override
					public void run() {
						PSButton but = (PSButton) pItem;
						nSaveName = null;
						nSaveFile = null;
						levelnew = 1;
						
						boolean classicMode = but.specialOpt == 0;
						if(classicMode)
							gAutosaveRequest = true;
						gGameScreen.newgame(addon, 1, classicMode);
					}
				});
			}
		};

		MenuButton Checkpoints = new PSButton("Checkpoint mode", 0, posy += 20, 320, 1, 0, null, -1, start, 0);
		MenuButton FreeSave = new PSButton("Free save mode", 0, posy += 20, 320, 1, 0, null, -1, start, 1);

		addItem(mTitle, false);
		addItem(Checkpoints, true);
		addItem(FreeSave, false);
	}

	public void setAddon(EpisodeInfo addon)
	{
		this.addon = addon;
	}
}
