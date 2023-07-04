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

import static ru.m210projects.Build.Net.Mmulti.myconnectindex;
import static ru.m210projects.Build.Net.Mmulti.numplayers;

import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.Pattern.MenuItems.BuildMenu;
import ru.m210projects.Build.Pattern.MenuItems.MenuHandler;
import ru.m210projects.Build.Pattern.MenuItems.MenuText;
import ru.m210projects.Build.Pattern.MenuItems.MenuVariants;
import ru.m210projects.Redneck.Main;

public class QuitMenu extends BuildMenu {

	public QuitMenu(final Main game)
	{
		MenuText QuitQuestion = new MenuText("Are you sure you want to quit?", game.getFont(1), 160, 90, 1);
		MenuVariants QuitVariants = new MenuVariants(game.pEngine, "[Y/N]", game.getFont(1), 160, 105) {
			@Override
			public void positive(MenuHandler menu) {
				if (numplayers > 1) {
					BuildGdx.app.postRunnable(new Runnable() {
						@Override
						public void run() {
							game.net.NetDisconnect(myconnectindex);
						}
					});
				}
				
				game.gExit = true;
				menu.mClose();
			}
		};

		addItem(QuitQuestion, false);
		addItem(QuitVariants, true);
	}
}
