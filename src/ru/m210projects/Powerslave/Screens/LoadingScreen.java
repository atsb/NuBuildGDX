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

import static ru.m210projects.Build.Engine.xdim;
import static ru.m210projects.Build.Engine.ydim;
import static ru.m210projects.Powerslave.Globals.*;
import static ru.m210projects.Powerslave.Map.*;
import static ru.m210projects.Powerslave.Sound.*;

import ru.m210projects.Build.Pattern.BuildGame;
import ru.m210projects.Build.Pattern.BuildFont.TextAlign;
import ru.m210projects.Build.Pattern.ScreenAdapters.LoadingAdapter;
import ru.m210projects.Powerslave.Main;

public class LoadingScreen extends LoadingAdapter {

	public LoadingScreen(BuildGame game) {
		super(game);
	}
	
	@Override
	public void show()
	{
		super.show();
		StopAllSounds();
	}

	@Override
	protected void draw(String title, float delta) {
		if(bMapCrash) {
			((Main)game).EndGame();
			return;
		}
		
		engine.clearview(96);
		engine.rotatesprite(160 << 16, 100 << 16, 65536, 0, BACKGROUND, 0, 0, 2 | 8, 0, 0, xdim - 1, ydim - 1);
	
		game.getFont(1).drawText(160, 100, title,  -128, 0, TextAlign.Center, 2, false);
	}

}
