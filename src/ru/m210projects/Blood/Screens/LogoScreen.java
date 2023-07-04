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

import static ru.m210projects.Blood.Globals.kPalNormal;
import static ru.m210projects.Blood.SOUND.sndStartSample;
import static ru.m210projects.Blood.SOUND.sndStopAllSounds;
import static ru.m210projects.Blood.Screen.scrReset;
import static ru.m210projects.Blood.Screen.scrGLSetDac;
import static ru.m210projects.Blood.Screen.scrSetPalette;
import static ru.m210projects.Build.Engine.xdim;
import static ru.m210projects.Build.Engine.ydim;

import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.Pattern.BuildGame;
import ru.m210projects.Build.Pattern.ScreenAdapters.SkippableAdapter;

public class LogoScreen extends SkippableAdapter {

	private int nTile;
	private float gTicks;
	private final float gShowTime;
	private Runnable callback;

	public LogoScreen(BuildGame game, float gShowTime) {
		super(game);
		this.gShowTime = gShowTime;
	}

	public LogoScreen setTile(int nTile) {
		this.nTile = nTile;
		return this;
	}

	public LogoScreen setCallback(Runnable callback) {
		this.callback = callback;
		this.setSkipping(callback);
		return this;
	}

	@Override
	public void show() {
		this.gTicks = 0;

		sndStopAllSounds();
		sndStartSample(255, 128, 0, false);
		scrSetPalette(kPalNormal);
		scrReset();
		scrGLSetDac(0);
	}

	@Override
	public void draw(float delta) {

		if ((gTicks += delta) >= gShowTime && callback != null && skipCallback != null) {
			BuildGdx.app.postRunnable(callback);
			callback = null;
		}

		engine.getrender().clearview(0);
		engine.rotatesprite(160 << 16, 100 << 16, 65536, 0, nTile, 0, 0, 10 | 64, 0, 0, xdim - 1, ydim - 1);
	}
}
