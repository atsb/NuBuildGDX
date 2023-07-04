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

import ru.m210projects.Build.Pattern.MenuItems.BuildMenu;
import ru.m210projects.Duke3D.Main;

public class CreditsMenu extends BuildMenu {

	public CreditsMenu(Main app)
	{
		MenuPage mPage1 = new MenuPage(0, 0, 2504);
		mPage1.flags |= 10;
		MenuPage mPage2 = new MenuPage(0, 0, 2505);
		mPage2.flags |= 10;
		MenuPage mPage3 = new MenuPage(0, 0, 2506);
		mPage3.flags |= 10;
		
		addItem(mPage3, true);
		addItem(mPage1, false);
		addItem(mPage2, false);
	}
}
