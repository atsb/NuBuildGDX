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

import static ru.m210projects.Blood.Main.*;
import static ru.m210projects.Blood.SOUND.sndStartSample;
import static ru.m210projects.Blood.SOUND.sndStopAllSounds;
import static ru.m210projects.Blood.Screen.scrReset;
import static ru.m210projects.Blood.Screen.scrSetPalette;
import static ru.m210projects.Blood.Strings.keycontinue;
import static ru.m210projects.Blood.View.*;
import static ru.m210projects.Build.Engine.totalclock;
import static ru.m210projects.Build.Engine.xdim;
import static ru.m210projects.Build.Engine.ydim;
import static ru.m210projects.Blood.Gameutils.*;
import static ru.m210projects.Blood.Globals.*;
import static ru.m210projects.Build.Net.Mmulti.*;

import ru.m210projects.Blood.Main;

import static ru.m210projects.Blood.Strings.*;

import ru.m210projects.Build.Pattern.BuildFont.TextAlign;
import ru.m210projects.Build.Pattern.ScreenAdapters.SkippableAdapter;

public class StatisticScreen extends SkippableAdapter {

	protected float gTicks;
	protected float gShowTime;
	protected boolean maySkipped;
	protected Main app;

	public StatisticScreen(Main game, float gShowTime) {
		super(game);
		this.app = game;
		this.gShowTime = gShowTime;
	}

	@Override
	public void show() {
		this.gTicks = 0;
		this.maySkipped = false;

		scrReset();
		scrSetPalette(kPalNormal);

		sndStopAllSounds();
		sndStartSample(268, 128, -1, false); // MOANS
	}

	@Override
	public void draw(float delta) {
		engine.getrender().clearview(0);
		viewShowStatus(maySkipped);

		if ((gTicks += delta) >= gShowTime)
			maySkipped = true;
	}

	public void viewShowStatus(boolean maySkipped) {
		viewShowLoadingTile();
		engine.rotatesprite(160 << 16, 20 << 16, 65536, 0, 2038, -128, 0, 78, 0, 0, xdim - 1, ydim - 1);

		if (pGameInfo.nGameType > kNetModeCoop) {
			game.getFont(1).drawText(160, 20 - game.getFont(1).getHeight() / 2, frags, -128, 0, TextAlign.Center, 2,
					false);

			game.getFont(3).drawText(85, 35, toCharArray("#"), -128, 0, TextAlign.Left, 2, false);
			game.getFont(3).drawText(110, 35, toCharArray("name"), -128, 0, TextAlign.Left, 2, false);
			game.getFont(3).drawText(225, 35, toCharArray("frags"), -128, 0, TextAlign.Left, 2, false);

			for (int i = connecthead; i >= 0; i = connectpoint2[i]) {
				viewDrawNumber(3, i, 85, 50 + i * 10, 65536, -128, 0, TextAlign.Left, 2, false);
				game.getFont(3).drawText(110, 50 + i * 10, toCharArray(app.net.gProfile[i].name), -128, 0,
						TextAlign.Left, 2, false);
				viewDrawNumber(3, gPlayer[i].fragCount, 225, 50 + i * 10, 65536, -128, 0, TextAlign.Left, 2, false);
			}
		} else {
			game.getFont(1).drawText(160, 20 - game.getFont(1).getHeight() / 2, levelstats, -128, 0, TextAlign.Center,
					2, false);
			if (cheatsOn)
				game.getFont(3).drawText(160, 32, toCharArray(">>> YOU CHEATED! <<<"), -128, 0, TextAlign.Center, 2,
						true);
			viewFragStat();
			viewSecretStat();
		}

		game.getFont(3).drawText(20, 191, game.sversion, 32, 0, TextAlign.Center, 2, false);
		if (maySkipped && (totalclock & 0x20) != 0)
			game.getFont(3).drawText(160, 134, keycontinue, -128, 0, TextAlign.Center, 2, true);
	}

	@Override
	public void skip() {
		if (maySkipped)
			gGameScreen.nextmap();
		super.skip();
	}

}
