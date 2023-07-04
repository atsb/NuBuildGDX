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

package ru.m210projects.Duke3D.Screens;

import static ru.m210projects.Duke3D.Names.*;
import static ru.m210projects.Duke3D.Player.InitPlayers;
import static ru.m210projects.Duke3D.Factory.DukeMenuHandler.*;
import static ru.m210projects.Duke3D.Globals.*;
import static ru.m210projects.Duke3D.Main.*;
import static ru.m210projects.Duke3D.Sounds.*;
import static ru.m210projects.Build.Net.Mmulti.*;

import ru.m210projects.Duke3D.Main;
import ru.m210projects.Build.Pattern.ScreenAdapters.ConnectAdapter;

public class NetScreen extends ConnectAdapter {

	private final Main app;
	public NetScreen(Main game) {
		super(game, LOADSCREEN, game.getFont(1));
		this.app = game;
	}

	@Override
	public void show() {
		super.show();
		StopAllSounds();
	}

	@Override
	public void back() {
		game.changeScreen(gMenuScreen);
	}

	@Override
	public void connect() {
		gDisconnectScreen.updateList();
		screenpeek = myconnectindex;
		ud.multimode = numplayers;
        
		InitPlayers();

		app.changeScreen(gMenuScreen);

		app.menu.mClose();
		app.menu.mOpen(app.menu.mMenus[NETWORKGAME], -1);
	}
}
