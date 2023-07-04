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

import ru.m210projects.Build.Pattern.MenuItems.BuildMenu;
import ru.m210projects.Build.Pattern.MenuItems.MenuHandler;
import ru.m210projects.Build.Pattern.MenuItems.MenuText;
import ru.m210projects.Build.Pattern.MenuItems.MenuTitle;
import ru.m210projects.Build.Pattern.MenuItems.MenuVariants;
import ru.m210projects.Powerslave.Main;

public class MenuQuit extends BuildMenu {

	public MenuQuit(final Main game)
	{
		MenuTitle QuitTitle = new PSTitle("Quit game", 160, 15, 0);
		
		MenuText QuitQuestion = new MenuText("Do you really want to quit?", game.getFont(0), 160, 100, 1);
		QuitQuestion.fontShadow = true;
		
		MenuVariants QuitVariants = new MenuVariants(game.pEngine, "[Y/N]", game.getFont(0), 160, 115) {
			@Override
			public void positive(MenuHandler menu) {
//				if (nGameType != kNetModeOff && numplayers > 1) {
//					BuildGdx.app.postRunnable(new Runnable() {
//						@Override
//						public void run() {
//							game.net.NetDisconnect(myconnectindex);
//						}
//					});
//				}
				
				game.gExit = true;
				menu.mClose();
			}
		};
		QuitVariants.fontShadow = true;
		
		addItem(QuitTitle, false);
		addItem(QuitQuestion, false);
		addItem(QuitVariants, true);
	}
}
