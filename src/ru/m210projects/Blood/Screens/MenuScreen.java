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

package ru.m210projects.Blood.Screens;

import static ru.m210projects.Blood.Factory.BloodMenuHandler.MAIN;
import static ru.m210projects.Blood.SOUND.sndPlayMenu;
import static ru.m210projects.Blood.View.viewNetPlayers;
import static ru.m210projects.Build.Engine.xdim;
import static ru.m210projects.Build.Engine.ydim;
import static ru.m210projects.Build.Net.Mmulti.numplayers;

import ru.m210projects.Blood.Main;
import ru.m210projects.Blood.Factory.BloodMenuHandler;
import ru.m210projects.Build.Pattern.ScreenAdapters.MenuAdapter;

public class MenuScreen extends MenuAdapter {

	private final BloodMenuHandler menu;
	public MenuScreen(Main game) {
		super(game, game.menu.mMenus[MAIN]);
		this.menu = game.menu;
	}

	@Override
	public void show() {
		if(!menu.gShowMenu)
			menu.mOpen(menu.mMenus[MAIN], -1);
		sndPlayMenu();
	}

	@Override
	public void process(float delta) {
		if (numplayers > 1)
			viewNetPlayers(200 - engine.getTile(2229).getHeight(), false);

		if (!game.gPaused)
			game.pNet.GetPackets();
	}

	@Override
	public void draw(float delta) {
		engine.rotatesprite(160 << 16, 100 << 16, 65536, 0, 2046, 0, 0, 2 | 8 | 64, 0, 0, xdim - 1, ydim - 1);
	}
}
