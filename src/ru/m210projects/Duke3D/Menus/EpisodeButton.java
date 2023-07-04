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

import ru.m210projects.Build.Pattern.BuildFont;
import ru.m210projects.Build.Pattern.MenuItems.MenuButton;
import ru.m210projects.Build.Pattern.MenuItems.MenuProc;
import ru.m210projects.Duke3D.Types.GameInfo;

public class EpisodeButton extends MenuButton {

	public GameInfo game;
	public EpisodeButton(GameInfo game, Object text, BuildFont font, int x, int y, int width, MenuProc specialCall, int specialOpt) {
		super(text, font, x, y, width, 1, 0, null, 3, specialCall, specialOpt);
		this.game = game;
	}

}
