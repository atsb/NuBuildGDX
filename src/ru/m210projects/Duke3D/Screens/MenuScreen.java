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

package ru.m210projects.Duke3D.Screens;

import static ru.m210projects.Build.Engine.palette;
import static ru.m210projects.Build.Engine.xdim;
import static ru.m210projects.Build.Engine.ydim;
import static ru.m210projects.Build.Net.Mmulti.numplayers;
import static ru.m210projects.Duke3D.Factory.DukeMenuHandler.MAIN;
import static ru.m210projects.Duke3D.Globals.ud;
import static ru.m210projects.Duke3D.Names.BIGHOLE;
import static ru.m210projects.Duke3D.Names.FRAGBAR;
import static ru.m210projects.Duke3D.View.displayfragbar;

import ru.m210projects.Build.Pattern.ScreenAdapters.MenuAdapter;

import ru.m210projects.Build.Types.Tile;
import ru.m210projects.Duke3D.Main;
import ru.m210projects.Duke3D.Factory.DukeMenuHandler;

public class MenuScreen extends MenuAdapter {

	private final DukeMenuHandler menu;
	public MenuScreen(Main game) {
		super(game, game.menu.mMenus[MAIN]);
		this.menu = game.menu;
	}

	@Override
	public void show() {
		engine.setbrightness(ud.brightness>>2, palette, true);
		if(!menu.gShowMenu)
			menu.mOpen(menu.mMenus[MAIN], -1);
	}

	@Override
	public void process(float delta) {
		if (numplayers > 1)
			displayfragbar(200 - engine.getTile(FRAGBAR).getHeight(), false);

		if (!game.gPaused)
			game.pNet.GetPackets();
	}

	@Override
	public void draw(float delta) {
		Tile pic = engine.getTile(BIGHOLE);

		if(!pic.hasSize())
			return;

		int framesx = xdim / pic.getWidth();
		int framesy = ydim / pic.getHeight();

		int x, y = 0;
		for(int j = 0; j <= framesy; j++) {
		    x = 0;
			for(int i = 0; i <= framesx; i++) {
		    	engine.rotatesprite(x<<16, y<<16, 0x10000, 0, BIGHOLE, 0, 0, 8 | 16 | 64 | 256, 0, 0, xdim-1, ydim-1);
		    	x += pic.getWidth();
		    }
		    y += pic.getHeight();
		}
	}

}
