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

package ru.m210projects.Blood.Menus;

import static ru.m210projects.Blood.Main.*;
import static ru.m210projects.Blood.Factory.BloodMenuHandler.*;
import static ru.m210projects.Blood.Globals.*;

import ru.m210projects.Blood.Main;
import ru.m210projects.Build.Pattern.BuildGame;
import ru.m210projects.Build.Pattern.CommonMenus.MenuCreate;
import ru.m210projects.Build.Pattern.MenuItems.MenuTitle;
import ru.m210projects.Build.Pattern.ScreenAdapters.ConnectAdapter.NetFlag;

public class BLMenuCreate extends MenuCreate {

	private final Main app;
	public BLMenuCreate(BuildGame app) {
		super(app, 46, 45, 10, 240, app.getFont(3), kMaxPlayers);
		this.app = (Main) app;
		
		mCreate.font = app.getFont(1);
		mCreate.fontShadow = true;
		
		addItem(this.app.menu.addMenuBlood(), false);
	}

	@Override
	public MenuTitle getTitle(BuildGame app, String text) {
		return new MenuTitle(app.pEngine, text, app.getFont(1), 160, 20, 2038);
	}

	@Override
	public void createGame(int mPlayers, boolean mUseFakeMultiplayer, String[] param) {
		kFakeMultiplayer = false;
		if (mPlayers == 1 || mUseFakeMultiplayer) {
			kFakeMultiplayer = true;
			nFakePlayers = (short) mPlayers;
			app.menu.mOpen(app.menu.mMenus[NETWORKGAME], -1);
		} else app.changeScreen(gNetScreen.setFlag(NetFlag.Create, param));
	}
	
}
