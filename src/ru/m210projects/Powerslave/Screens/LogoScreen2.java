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

import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Powerslave.Energy.*;
import static ru.m210projects.Powerslave.Sound.*;

import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.Pattern.BuildGame;
import ru.m210projects.Build.Pattern.BuildFont.TextAlign;
import ru.m210projects.Build.Pattern.ScreenAdapters.LogoScreen;

public class LogoScreen2 extends LogoScreen {

	private int nFireTile = 0;
	private int loc_10010[] = { 6, 25, 43, 50, 68, 78, 101, 111, 134, 158, 173, 230, 6000 };
	private int clock, frame, anim, coord;

	public LogoScreen2(BuildGame game, float gShowTime) {
		super(game, gShowTime);
	}

	@Override
	public void show() {
		super.show();
		anim = frame = 0;
		clock = totalclock;
		coord = 130;
		playCDtrack(19, true);
		if((System.currentTimeMillis() & 0xF) != 0)
			PlayGameOverSound();
		else PlayLocalSound(StaticSound[61], 0);
	}

	@Override
	public void draw(float delta) {
		engine.clearview(96);
		DoPlasma(160, 40, 65536);

		nFireTile = (totalclock / 16) & 3;

		engine.rotatesprite(50 << 16, 150 << 16, 65536, 0, nFireTile + 3512, 0, 0, 10, 0, 0, xdim - 1, ydim - 1);
		engine.rotatesprite(270 << 16, 150 << 16, 65536, 0, ((nFireTile + 2) & 3) + 3512, 0, 0, 10, 0, 0, xdim - 1,
				ydim - 1);

		engine.rotatesprite(160 << 16, 100 << 16, 65536, 0, 3582, 0, 0, 10, 0, 0, xdim - 1, ydim - 1);

		int pic = 3437;
		if (LocalSoundPlaying()) {
			if (totalclock > clock + loc_10010[frame]) {
				anim ^= 1;
				frame++;
			}

			if (anim != 0) {
				if (coord >= 135)
					pic = 3583;
				else
					coord += 5;
			} else if (coord <= 130)
				coord = 130;
			else coord -= 2;
			
			if (pic == 3583) 
				coord = 131;
			else if (coord > 135)
				coord = 135;
		} else {
			if ((gTicks += delta) >= gShowTime && callback != null) {
				BuildGdx.app.postRunnable(callback);
				callback = null;
			}
		}
		
		if(frame >= 1)
		{
			game.getFont(0).drawText(160, 170, "Lobotomy software, Inc.", 0, 0, TextAlign.Center, 10, false);
			game.getFont(0).drawText(160, 178, "3D Engine by 3D Realms", 0, 0, TextAlign.Center, 10, false);
		}
		engine.rotatesprite(161 << 16, coord << 16, 65536, 0, pic, 0, 0, 10, 0, 0, xdim - 1, ydim - 1);
	}
}
