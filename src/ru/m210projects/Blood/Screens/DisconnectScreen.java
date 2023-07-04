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

import static ru.m210projects.Blood.Gameutils.toCharArray;
import static ru.m210projects.Blood.Globals.gPlayer;
import static ru.m210projects.Blood.Strings.keycontinue;
import static ru.m210projects.Blood.View.viewDrawNumber;
import static ru.m210projects.Blood.View.viewShowLoadingTile;
import static ru.m210projects.Build.Engine.totalclock;
import static ru.m210projects.Build.Engine.xdim;
import static ru.m210projects.Build.Engine.ydim;
import static ru.m210projects.Build.Net.Mmulti.connecthead;
import static ru.m210projects.Build.Net.Mmulti.connectpoint2;

import java.util.ArrayList;
import java.util.List;

import ru.m210projects.Blood.Main;
import ru.m210projects.Build.Pattern.BuildFont.TextAlign;

public class DisconnectScreen extends StatisticScreen {

	public List<Integer> playerList;

	public DisconnectScreen(Main game, float gShowTime) {
		super(game, gShowTime);
		playerList = new ArrayList<Integer>();
	}

	public void updateList() {
		playerList.clear();
		for (int i = connecthead; i >= 0; i = connectpoint2[i]) {
			playerList.add(i);
		}
	}

	@Override
	public void hide() {
		updateList();
	}

	@Override
	public void viewShowStatus(boolean maySkipped) {
		engine.getrender().clearview(0);
		viewShowLoadingTile();
		engine.rotatesprite(160 << 16, 20 << 16, 65536, 0, 2038, -128, 0, 78, 0, 0, xdim - 1, ydim - 1);

		game.getFont(1).drawText(160, 20 - game.getFont(1).getHeight() / 2, toCharArray("Disconnected"), -128, 0,
				TextAlign.Center, 2, false);

		game.getFont(3).drawText(85, 35, toCharArray("#"), -128, 0, TextAlign.Left, 2, false);
		game.getFont(3).drawText(110, 35, toCharArray("name"), -128, 0, TextAlign.Left, 2, false);
		game.getFont(3).drawText(225, 35, toCharArray("frags"), -128, 0, TextAlign.Left, 2, false);

		for (int num : playerList) {
			viewDrawNumber(3, num, 85, 50 + num * 10, 65536, -128, 0, TextAlign.Left, 2, false);
			game.getFont(3).drawText(110, 50 + num * 10, toCharArray(app.net.gProfile[num].name), -128, 0,
					TextAlign.Left, 2, false);
			viewDrawNumber(3, gPlayer[num].fragCount, 225, 50 + num * 10, 65536, -128, 0, TextAlign.Left, 2, false);
		}

		game.getFont(3).drawText(20, 191, game.sversion, 32, 0, TextAlign.Center, 2, false);
		if (maySkipped && (totalclock & 0x20) != 0)
			game.getFont(3).drawText(160, 134, keycontinue, -128, 0, TextAlign.Center, 2, true);
	}

	@Override
	public void skip() {
		if (maySkipped)
			super.skip();
	}
}
