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
import static ru.m210projects.Build.Engine.totalclock;
import static ru.m210projects.Build.Engine.xdim;
import static ru.m210projects.Build.Engine.ydim;
import static ru.m210projects.Redneck.Globals.*;
import static ru.m210projects.Redneck.Main.*;
import static ru.m210projects.Redneck.Sounds.*;

import com.badlogic.gdx.ScreenAdapter;

import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.Audio.Source;


public class EndScreen extends ScreenAdapter {

	private Source voice;
	
	@Override
	public void show() {
		engine.setbrightness(ud.brightness >> 2, palette, true);
		totalclock = 0;
		StopAllSounds();
		voice = sound(35);
	}

	@Override
	public void render(float delta) {
		engine.clearview(0);
		engine.sampletimer();

		if ((totalclock >> 4 & 1) != 0) {
			engine.rotatesprite(0, 0, 65536, 0, 8677, 0, 0, 2 + 8 + 16 + 64 + 128, 0, 0, xdim - 1, ydim - 1);
		} else
			engine.rotatesprite(0, 0, 65536, 0, 8678, 0, 0, 2 + 8 + 16 + 64 + 128, 0, 0, xdim - 1, ydim - 1);

		if (totalclock > 500 && (voice == null || !voice.isPlaying())) {
			BuildGdx.app.postRunnable(new Runnable() {
				@Override
				public void run() {
					game.changeScreen(gStatisticScreen);
				}
			});
		}

		engine.nextpage();
	}

	public void episode1() {
		BuildGdx.app.postRunnable(new Runnable() {
			@Override
			public void run() {
				String filename = "turdmov.anm";
				if (currentGame.getCON().type == RR66)
					filename = "turd66.anm";

				if (gAnmScreen.init(filename, 6)) {
					gAnmScreen.setCallback(new Runnable() {
						@Override
						public void run() {
							game.changeScreen(gStatisticScreen);
						}
					});
					game.setScreen(gAnmScreen.escSkipping(true));
				} else
					game.changeScreen(gStatisticScreen);
			}
		});
	}

	public void episode2() {
		BuildGdx.app.postRunnable(new Runnable() {
			@Override
			public void run() {
				if (currentGame.getCON().type == RRRA)
				{
					game.setScreen(gEndScreen);
					return;
				}
				
				String filename = "rr_outro.anm";
				if (currentGame.getCON().type == RR66)
					filename = "end66.anm";

				if (gAnmScreen.init(filename, 5)) {
					gAnmScreen.setCallback(new Runnable() {
						@Override
						public void run() {
							game.changeScreen(gStatisticScreen);
						}
					});
					game.setScreen(gAnmScreen.escSkipping(true));
				} else
					game.changeScreen(gStatisticScreen);
			}
		});
	}
}
