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

package ru.m210projects.Redneck.Factory;

import static ru.m210projects.Redneck.Globals.*;
import static ru.m210projects.Redneck.Main.*;
import static ru.m210projects.Redneck.Screen.*;

import com.badlogic.gdx.Screen;

import ru.m210projects.Build.Pattern.BuildEngine;
import ru.m210projects.Build.Pattern.BuildGame;
import ru.m210projects.Build.Pattern.ScreenAdapters.GameAdapter;
import ru.m210projects.Build.Render.Renderer;
import ru.m210projects.Build.Render.Renderer.RenderType;
import ru.m210projects.Redneck.Fonts.StandartFont;

public class RREngine extends BuildEngine {

	public RREngine(BuildGame game) throws Exception {
		super(game, TICSPERFRAME);
	}

	@Override
	public boolean setgamemode(int davidoption, int daxdim, int daydim)
	{
		boolean out = super.setgamemode(davidoption, daxdim, daydim);
		gViewXScaled = (xdim << 16) / 320;
		gViewYScaled = (ydim << 16) / 200;

		return out;
	}

	@Override
	public boolean setrendermode(Renderer render) {
		if(this.render != null && this.render != render)
		{
			if(render.getType() != RenderType.Software)
			{
				((StandartFont) game.getFont(3)).reinit();
				final Screen screen = game.getScreen();
				if(screen instanceof GameAdapter) {
					gPrecacheScreen.init(true, new Runnable() {
						@Override
						public void run() {
							game.changeScreen(screen);
							if(game.isCurrentScreen(gGameScreen))
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
