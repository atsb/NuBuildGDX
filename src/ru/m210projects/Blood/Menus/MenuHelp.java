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

import ru.m210projects.Build.Pattern.MenuItems.BuildMenu;

public class MenuHelp extends BuildMenu {
	
	public MenuHelp()
	{
		MenuQav QAVHelp3 = new MenuQav(160, 100, "HELP3.QAV");
		QAVHelp3.clearScreen = true;
		MenuQav QAVHelp3B = new MenuQav(160, 100, "HELP3B.QAV");
		QAVHelp3B.clearScreen = true;
		MenuQav QAVHelp4 = new MenuQav(160, 100, "HELP4.QAV");
		QAVHelp4.clearScreen = true;
		MenuQav QAVHelp5 = new MenuQav(160, 100, "HELP5.QAV");
		QAVHelp5.clearScreen = true;
		
		QAVHelp4.flags |= 10;
		QAVHelp5.flags |= 10;
		QAVHelp3.flags |= 10;
		QAVHelp3B.flags |= 10;

		addItem(QAVHelp4, true);
		addItem(QAVHelp5, false);
		addItem(QAVHelp3, false);
		addItem(QAVHelp3B, false);
	}

}
