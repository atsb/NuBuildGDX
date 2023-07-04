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

import ru.m210projects.Duke3D.Main;
import ru.m210projects.Build.Pattern.BuildGame;
import ru.m210projects.Build.Pattern.CommonMenus.MenuMouse;
import ru.m210projects.Build.Pattern.MenuItems.MenuTitle;

public class DMenuMouse extends MenuMouse {

	public DMenuMouse(Main app) {
		super(app, 20, 40, 280, 10, 5, app.getFont(1), 0);
	}

	@Override
	public MenuTitle getTitle(BuildGame app, String text) {
		return new DukeTitle(text);
	}

}
