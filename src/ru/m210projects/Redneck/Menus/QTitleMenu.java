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
import static ru.m210projects.Redneck.Globals.*;
import static ru.m210projects.Redneck.Main.*;

import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.Pattern.MenuItems.BuildMenu;
import ru.m210projects.Build.Pattern.MenuItems.MenuHandler;
import ru.m210projects.Build.Pattern.MenuItems.MenuText;
import ru.m210projects.Build.Pattern.MenuItems.MenuVariants;
import ru.m210projects.Redneck.Main;

public class QTitleMenu extends BuildMenu {

	public QTitleMenu(final Main app)
	{
		MenuText QuitQuestion = new MenuText("Quit to title?", app.getFont(1), 160, 90, 1);
		MenuVariants QuitVariants = new MenuVariants(app.pEngine, "[Y/N]", app.getFont(1), 160, 105) {
			@Override
			public void positive(MenuHandler menu) {
				if (!app.isCurrentScreen(gDemoScreen) && (numplayers > 1 || mFakeMultiplayer)) {
					BuildGdx.app.postRunnable(new Runnable() {
						@Override
						public void run() {
							app.net.NetDisconnect(myconnectindex);
						}
					});
				} else app.show();
				menu.mClose();
			}
		};

		addItem(QuitQuestion, false);
		addItem(QuitVariants, true);
	}
}
