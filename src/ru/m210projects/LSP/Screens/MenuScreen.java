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

package ru.m210projects.LSP.Screens;

import static ru.m210projects.Build.Engine.xdim;
import static ru.m210projects.Build.Engine.ydim;
import static ru.m210projects.LSP.Factory.LSPMenuHandler.*;
import static ru.m210projects.LSP.Sounds.*;

import ru.m210projects.Build.Architecture.BuildFrame.FrameType;
import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.Architecture.BuildGraphics.Option;
import ru.m210projects.Build.Pattern.ScreenAdapters.MenuAdapter;
import ru.m210projects.LSP.Main;
import ru.m210projects.LSP.Factory.LSPMenuHandler;


public class MenuScreen extends MenuAdapter {
	
	private LSPMenuHandler menu;
	public MenuScreen(Main game) {
		super(game, game.menu.mMenus[MAIN]);
		this.menu = game.menu;
	}

	@Override
	public void draw(float delta) {
		engine.rotatesprite(0 << 16, 0, 65536, 0, 1102, 10, 0, 2 | 8 | 16, 0, 0, xdim, ydim);
		engine.rotatesprite(0, 10 << 16, 65536, 0, 1100, 10, 0, 2 | 8 | 16, 0, 0, xdim, ydim);
	}
	
	@Override
	public void show() {
		menu.mOpen(mainMenu, -1);
		game.pInput.ctrlResetKeyStatus();
		stopallsounds();
		startmusic(13);
	}
	
	@Override
	public void pause () {
		stopmusic();
		if (BuildGdx.graphics.getFrameType() == FrameType.GL) 
			BuildGdx.graphics.extra(Option.GLDefConfiguration);
	}

	@Override
	public void resume () {
		startmusic(13);
		game.updateColorCorrection();
	}
	
}
