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

package ru.m210projects.Powerslave.Screens;

import static ru.m210projects.Build.Engine.totalclock;
import static ru.m210projects.Build.Engine.xdim;
import static ru.m210projects.Build.Engine.ydim;
import static ru.m210projects.Powerslave.Factory.PSMenuHandler.*;
import static ru.m210projects.Powerslave.Globals.*;
import static ru.m210projects.Powerslave.Sound.playCDtrack;

import ru.m210projects.Build.Pattern.ScreenAdapters.MenuAdapter;
import ru.m210projects.Powerslave.Main;
import ru.m210projects.Powerslave.Factory.PSMenuHandler;

public class MenuScreen extends MenuAdapter {

	private PSMenuHandler menu;
	public MenuScreen(Main game) {
		super(game, game.menu.mMenus[MAIN]);
		this.menu = game.menu;
	}
	
	@Override
	public void show() {
		if(!menu.gShowMenu)
			menu.mOpen(menu.mMenus[MAIN], -1);
		playCDtrack(19, true);
	}
	
	public void process(float delta) { 
		if (!game.gPaused) 
			game.pNet.GetPackets();
	}

	@Override
	public void draw(float delta) {
		engine.clearview(96);
		
		engine.rotatesprite(160 << 16, 100 << 16, 65536, 0, BACKGROUND, 50, 0, 2 | 8 | 64, 0, 0, xdim - 1, ydim - 1);
		
		if(menu.mCount == 1 || menu.isOpened(menu.mMenus[NEWGAME])) {
			int nFireTile = (totalclock / 16) & 3;
			
			engine.rotatesprite(50 << 16, 150 << 16, 65536, 0, nFireTile + 3512, 0, 0, 10, 0, 0, xdim - 1, ydim - 1);
			engine.rotatesprite(270 << 16, 150 << 16, 65536, 0, ((nFireTile + 2) & 3) + 3512, 0, 0, 10, 0, 0, xdim - 1, ydim - 1);
		}
	}
}
