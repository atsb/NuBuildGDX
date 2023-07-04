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

package ru.m210projects.Redneck.Screens;

import static ru.m210projects.Build.Engine.palette;
import static ru.m210projects.Build.Engine.xdim;
import static ru.m210projects.Build.Engine.ydim;
import static ru.m210projects.Build.Net.Mmulti.numplayers;
import static ru.m210projects.Redneck.Factory.RRMenuHandler.MAIN;
import static ru.m210projects.Redneck.Globals.ud;
import static ru.m210projects.Redneck.Names.BACKGROUND;
import static ru.m210projects.Redneck.Names.FRAGBAR;
import static ru.m210projects.Redneck.View.displayfragbar;

import ru.m210projects.Build.Pattern.ScreenAdapters.MenuAdapter;
import ru.m210projects.Build.Render.GLRenderer.GLInvalidateFlag;
import ru.m210projects.Build.Types.Tile;
import ru.m210projects.Redneck.Main;
import ru.m210projects.Redneck.Factory.RRMenuHandler;

public class MenuScreen extends MenuAdapter {

	private RRMenuHandler menu;
	public MenuScreen(Main game) {
		super(game, game.menu.mMenus[MAIN]);
		this.menu = game.menu;
	}

	@Override
	public void show() {
		engine.setbrightness(ud.brightness>>2, palette, GLInvalidateFlag.All);
		if(!menu.gShowMenu)
			menu.mOpen(menu.mMenus[MAIN], -1);
	}

	@Override
	public void process(float delta) {
		if (numplayers > 1)
			displayfragbar(200 - engine.getTile(FRAGBAR).getHeight() / 2, false);

		if (!game.gPaused)
			game.pNet.GetPackets();
	}

	@Override
	public void draw(float delta) {
		Tile pic = engine.getTile(BACKGROUND);

		if(!pic.hasSize())
			return;

		int framesx = xdim / pic.getWidth();
		int framesy = ydim / pic.getHeight();

		int x, y = 0;
		for(int j = 0; j <= framesy; j++) {
		    x = 0;
			for(int i = 0; i <= framesx; i++) {
		    	engine.rotatesprite(x<<16, y<<16, 0x10000, 0, BACKGROUND, 0, 0, 8 | 16 | 256, 0, 0, xdim-1, ydim-1);
		    	x += pic.getWidth();
		    }
		    y += pic.getHeight();
		}
	}

}
