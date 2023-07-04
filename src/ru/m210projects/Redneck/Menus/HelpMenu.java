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

import static ru.m210projects.Redneck.Globals.RRRA;
import static ru.m210projects.Redneck.Globals.currentGame;
import static ru.m210projects.Redneck.Names.*;

import ru.m210projects.Build.Pattern.MenuItems.BuildMenu;
import ru.m210projects.Redneck.Main;

public class HelpMenu extends BuildMenu {
	
	public HelpMenu(Main app)
	{
		MenuPage mPage1 = new MenuPage(0, 0, TEXTSTORY);
		mPage1.flags |= 10;
		MenuPage mPage2 = new MenuPage(0, 0, F1HELP);
		mPage2.flags |= 10;

		addItem(mPage2, true);
		addItem(mPage1, false);
		
		if(currentGame.getCON().type == RRRA) 
		{
			MenuPage mPage3 = new MenuPage(0, 0, 1636);
			mPage3.flags |= 10;
			addItem(mPage3, false);
		}
	}

}
