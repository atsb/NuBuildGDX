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

package ru.m210projects.Redneck.Menus;

import static ru.m210projects.Redneck.Main.*;

import ru.m210projects.Build.Pattern.BuildGame;
import ru.m210projects.Build.Pattern.CommonMenus.MenuJoin;
import ru.m210projects.Build.Pattern.MenuItems.MenuTitle;
import ru.m210projects.Build.Pattern.ScreenAdapters.ConnectAdapter.NetFlag;

public class RMenuJoin extends MenuJoin {

	public RMenuJoin(BuildGame app) {
		super(app, 46, 45, 12, 240, app.getFont(1));
		
		mConnect.font = app.getFont(2);
	}

	@Override
	public MenuTitle getTitle(BuildGame app, String text) {
		return new RRTitle(text);
	}

	@Override
	public void joinGame(String[] param) {
		game.changeScreen(gNetScreen.setFlag(NetFlag.Connect, param));
	}
	
}
