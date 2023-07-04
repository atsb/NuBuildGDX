// This file is part of LSPGDX.
// Copyright (C) 2020  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// LSPGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// LSPGDX is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with LSPGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.LSP.Menus;

import static ru.m210projects.LSP.Sounds.*;

import ru.m210projects.Build.Pattern.BuildGame;
import ru.m210projects.Build.Pattern.MenuItems.BuildMenu;
import ru.m210projects.Build.Pattern.MenuItems.MenuHandler;
import ru.m210projects.Build.Pattern.MenuItems.MenuText;
import ru.m210projects.Build.Pattern.MenuItems.MenuVariants;

public class LSPMenuQTitle extends BuildMenu {

	public LSPMenuQTitle(final BuildGame game)
	{
		MenuText QuitQuestion = new MenuText("Quit to title?", game.getFont(0), 160, 85, 1);
		MenuVariants QuitVariants = new MenuVariants(game.pEngine, "[Y/N]", game.getFont(0), 160, 102) {
			@Override
			public void positive(MenuHandler menu) {
				menu.mClose();
		
				game.pNet.ready2send = false;
				stopmusic();
				stopallsounds();
				
				game.pInput.ctrlResetKeyStatus();
				game.show();
			}
		};
		
		QuitVariants.pal = QuitQuestion.pal = 228;

		addItem(QuitQuestion, false);
		addItem(QuitVariants, true);
	}
}
