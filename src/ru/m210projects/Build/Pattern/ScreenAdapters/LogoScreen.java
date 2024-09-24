//This file is part of BuildGDX.
//Copyright (C) 2017-2018  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
//BuildGDX is free software: you can redistribute it and/or modify
//it under the terms of the GNU General Public License as published by
//the Free Software Foundation, either version 3 of the License, or
//(at your option) any later version.
//
//BuildGDX is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU General Public License for more details.
//
//You should have received a copy of the GNU General Public License
//along with BuildGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.Build.Pattern.ScreenAdapters;

import static ru.m210projects.Build.Engine.xdim;
import static ru.m210projects.Build.Engine.ydim;

import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.Architecture.BuildFrame.FrameType;
import ru.m210projects.Build.Architecture.BuildGraphics.Option;
import ru.m210projects.Build.Pattern.BuildGame;

public class LogoScreen extends SkippableAdapter {
	
	protected int nTile;
	protected float gTicks;
	protected float gShowTime;
	protected Runnable callback;
	
	public LogoScreen(BuildGame game, float gShowTime)
	{
		super(game);
		this.gShowTime = gShowTime;
	}
	
	public LogoScreen setTile(int nTile) {
		this.nTile = nTile;
		return this;
	}
	
	public LogoScreen setCallback(Runnable callback) {
		this.callback = callback;
		this.skipCallback = callback;
		return this;
	}

	@Override
	public void show()
	{
		this.gTicks = 0;
		game.pInput.ctrlResetKeyStatus();
	}

	@Override
	public void draw(float delta) {
		
		if( (gTicks += delta) >= gShowTime && callback != null)
		{
			BuildGdx.app.postRunnable(callback);
			callback = null;
		}

		engine.clearview(0);
		engine.rotatesprite(160 << 16, 100 << 16, 65536, 0, nTile, 0, 0, 10 | 64, 0, 0, xdim - 1, ydim - 1);
	}
	
	@Override
	public void pause () {
	}

	@Override
	public void resume () {
		game.updateColorCorrection();
	}

}
