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

package ru.m210projects.Duke3D.Factory;

import static ru.m210projects.Duke3D.Main.*;
import static ru.m210projects.Duke3D.Globals.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Input.Keys;

import ru.m210projects.Build.Pattern.BuildEngine;
import ru.m210projects.Build.Pattern.BuildGame;
import ru.m210projects.Build.Pattern.ScreenAdapters.GameAdapter;
import ru.m210projects.Build.Render.Renderer;
import ru.m210projects.Build.Render.Renderer.RenderType;

public class DukeEngine extends BuildEngine {

	public DukeEngine(BuildGame game) throws Exception {
		super(game, TICSPERFRAME);
	}

	@Override
	public void sampletimer() {

        super.sampletimer();
	}

	@Override
	public boolean setrendermode(Renderer render) {
		if (this.render != null && this.render != render) {
			if (render.getType() != RenderType.Software) {
				final Screen screen = game.getScreen();
				if (screen instanceof GameAdapter) {
					gPrecacheScreen.init(true, new Runnable() {
						@Override
						public void run() {
							game.changeScreen(screen);
							if (game.isCurrentScreen(gGameScreen))
								game.net.ready2send = true;
						}
					});
					game.changeScreen(gPrecacheScreen);
				}
			}
		}

		return super.setrendermode(render);
	}
}
