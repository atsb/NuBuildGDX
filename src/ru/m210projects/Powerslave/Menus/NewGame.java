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

public class NewGame extends BuildMenu {
	
	public NewGame(final Main app)
	{
		MenuTitle mTitle = new PSTitle(160, 35, 65536);
		int posy = 45;
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
						gGameScreen.newgame(gOriginalEpisode, 1, but.specialOpt == 0);
					}
				});
			}
		};
		
		MenuProc training = new MenuProc() {
			@Override
			public void run(MenuHandler handler, MenuItem pItem) {
				BuildGdx.app.postRunnable(new Runnable() {
					@Override
					public void run() {
						nSaveName = null;
						nSaveFile = null;
						gGameScreen.training();
					}
				});
			}
		};
		
		MenuButton Checkpoints = new PSButton("Checkpoint mode", 0, posy += 20, 320, 1, 0, null, -1, start, 0);
		MenuButton FreeSave = new PSButton("Free save mode", 0, posy += 20, 320, 1, 0, null, -1, start, 1);
		MenuButton Training = new PSButton("Training", 0, posy += 20, 320, 1, 0, null, -1, training, 0);
		MenuButton User = new PSButton("< User content >", 0, posy += 30, 320, 1, 0, new PSMenuUserContent(app), -1, null, 0);

		addItem(mTitle, false);
		addItem(Checkpoints, true);
		addItem(FreeSave, false);
		addItem(Training, false);
		addItem(User, false);
	}

}
