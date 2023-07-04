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

import static ru.m210projects.Blood.Factory.BloodMenuHandler.*;
import static ru.m210projects.Blood.Globals.gMe;
import static ru.m210projects.Blood.Globals.gPlayer;
import static ru.m210projects.Blood.Globals.kMaxPlayers;
import static ru.m210projects.Blood.Globals.kPalNormal;
import static ru.m210projects.Blood.Globals.pGameInfo;
import static ru.m210projects.Blood.Main.cfg;
import static ru.m210projects.Blood.Main.gDisconnectScreen;
import static ru.m210projects.Blood.Main.gMenuScreen;
import static ru.m210projects.Blood.SOUND.sndStopAllSounds;
import static ru.m210projects.Blood.Screen.scrReset;
import static ru.m210projects.Blood.Screen.scrGLSetDac;
import static ru.m210projects.Blood.Screen.scrSetPalette;
import static ru.m210projects.Blood.View.gViewIndex;
import static ru.m210projects.Build.Net.Mmulti.myconnectindex;

import ru.m210projects.Blood.Main;
import ru.m210projects.Build.Pattern.ScreenAdapters.ConnectAdapter;

public class NetScreen extends ConnectAdapter {

	private final Main app;
	public NetScreen(Main game) {
		super(game, 2046, game.getFont(3));
		this.app = game;
	}

	@Override
	public void show() {
		super.show();
		sndStopAllSounds();

		scrSetPalette(kPalNormal);
		scrReset();
		scrGLSetDac(0);
	}

	@Override
	public void back() {
		game.changeScreen(gMenuScreen);
	}

	@Override
	public void connect() {
		gDisconnectScreen.updateList();
		for (int i = 0; i < kMaxPlayers; i++)
			gPlayer[i].reset();

		gMe = gPlayer[myconnectindex];
		gViewIndex = myconnectindex;
		pGameInfo.nGameType = 1; //to prepareboard in multiplayer

		app.net.gProfile[myconnectindex].autoaim = cfg.gAutoAim;
		app.net.gProfile[myconnectindex].slopetilt = cfg.gSlopeTilt;
		app.net.gProfile[myconnectindex].name = cfg.pName;

		app.net.InitProfile(myconnectindex);
		app.changeScreen(gMenuScreen);

		app.menu.mClose();
		app.menu.mOpen(app.menu.mMenus[NETWORKGAME], -1);
	}
}
