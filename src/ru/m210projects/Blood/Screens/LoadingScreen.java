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

import static ru.m210projects.Blood.Globals.kGameCrash;
import static ru.m210projects.Blood.Globals.kPalNormal;
import static ru.m210projects.Blood.Screen.scrReset;
import static ru.m210projects.Blood.Screen.scrGLSetDac;
import static ru.m210projects.Blood.Screen.scrSetPalette;
import static ru.m210projects.Blood.Strings.loading;
import static ru.m210projects.Blood.Strings.wait;
import static ru.m210projects.Blood.View.viewShowLoadingTile;
import static ru.m210projects.Build.Engine.xdim;
import static ru.m210projects.Build.Engine.ydim;

import ru.m210projects.Blood.Main;
import ru.m210projects.Build.Architecture.BuildFrame.FrameType;
import ru.m210projects.Build.Architecture.BuildGraphics.Option;
import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.Pattern.BuildFont.TextAlign;
import ru.m210projects.Build.Pattern.ScreenAdapters.LoadingAdapter;

public class LoadingScreen extends LoadingAdapter {
	
	private final Main app;
	public LoadingScreen(Main game) {
		super(game);
		this.app = game;
	}

	@Override
	public void draw(String title, float delta) {
		if(kGameCrash) {
			app.rMenu.run();
			return;
		}

		viewShowLoadingTile();
		engine.rotatesprite(160 << 16, 20 << 16, 65536, 0, 2038, -128, 0, 2 | 4 | 8 | 64, 0, 0, xdim - 1, ydim - 1);
		
		app.getFont(1).drawText(160, 13, loading,  -128, 0, TextAlign.Center, 2, false);
		app.getFont(3).drawText(160, 134, wait,  -128, 0, TextAlign.Center, 2, false);
		
		app.getFont(1).drawText(160, 60, title,  -128, 0, TextAlign.Center, 2, true);
	}

	@Override
	public void show()
	{
		super.show();
		
		scrSetPalette(kPalNormal);
		scrReset();
		scrGLSetDac(0);
	}
	
	@Override
	public void pause () {
	}

	@Override
	public void resume () {
		game.updateColorCorrection();
	}
	
}
