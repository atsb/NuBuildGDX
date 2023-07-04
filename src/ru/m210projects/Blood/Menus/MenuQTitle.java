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

import static ru.m210projects.Blood.Globals.*;
import static ru.m210projects.Build.Net.Mmulti.*;

import ru.m210projects.Blood.Main;
import ru.m210projects.Blood.Factory.BloodMenuHandler;
import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.Pattern.MenuItems.BuildMenu;
import ru.m210projects.Build.Pattern.MenuItems.MenuHandler;
import ru.m210projects.Build.Pattern.MenuItems.MenuText;
import ru.m210projects.Build.Pattern.MenuItems.MenuTitle;
import ru.m210projects.Build.Pattern.MenuItems.MenuVariants;

public class MenuQTitle extends BuildMenu {

	public MenuQTitle(final Main game)
	{
		MenuTitle QuitTitle = new MenuTitle(game.pEngine, "Quit to title", game.getFont(1), 160, 20, 2038);
		
		MenuText QuitQuestion = new MenuText("Quit to title?", game.getFont(0), 160, 100, 1);
		MenuVariants QuitVariants = new MenuVariants(game.pEngine, "[Y/N]", game.getFont(0), 160, 115) {
			@Override
			public void positive(MenuHandler menu) {
				if (pGameInfo.nGameType != kNetModeOff && numplayers > 1) {
					BuildGdx.app.postRunnable(new Runnable() {
						@Override
						public void run() {
							game.net.NetDisconnect(myconnectindex);
						}
					});
				} else game.EndGame();
				
				menu.mClose();
			}
		};
		
		addItem(QuitTitle, false);
		addItem(QuitQuestion, false);
		addItem(QuitVariants, true);
		addItem(game.menu.addMenuBlood(), false);
	}
}
