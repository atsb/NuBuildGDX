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
import static ru.m210projects.Redneck.Factory.RRMenuHandler.*;
import static ru.m210projects.Redneck.Globals.*;

import ru.m210projects.Redneck.Main;
import ru.m210projects.Build.Pattern.BuildGame;
import ru.m210projects.Build.Pattern.CommonMenus.MenuCreate;
import ru.m210projects.Build.Pattern.MenuItems.MenuHandler;
import ru.m210projects.Build.Pattern.MenuItems.MenuItem;
import ru.m210projects.Build.Pattern.MenuItems.MenuProc;
import ru.m210projects.Build.Pattern.MenuItems.MenuSwitch;
import ru.m210projects.Build.Pattern.MenuItems.MenuTitle;
import ru.m210projects.Build.Pattern.ScreenAdapters.ConnectAdapter.NetFlag;

public class RMenuCreate extends MenuCreate {

	private Main app;
	public RMenuCreate(BuildGame app) {
		super(app, 46, 45, 12, 240, app.getFont(1), 8);
		this.app = (Main) app;
		
		MenuSwitch mMenuBots = new MenuSwitch("Use bots", app.getFont(1), 46, mMenuFakeMM.y + 10, 240, ud.playerai==1, new MenuProc() {
			@Override
			public void run( MenuHandler handler, MenuItem pItem ) {
				MenuSwitch sw = (MenuSwitch) pItem;
				ud.playerai = sw.value?1:0;
			}
		}, "Yes", "No") {
			@Override
			public void draw(MenuHandler handler) {
				value = ud.playerai == 1;
				mCheckEnableItem(mPlayers > 1 && mUseFakeMultiplayer);
				super.draw(handler);
			}
		};
		
		m_nItems--; //hack for add switch in front of mCreate button
		this.addItem(mMenuBots, false);
		
		mCreate.font = app.getFont(2);
		mCreate.y += 10;
		
		this.addItem(mCreate, false);
	}

	@Override
	public MenuTitle getTitle(BuildGame app, String text) {
		return new RRTitle(text);
	}

	@Override
	public void createGame(int mPlayers, boolean mUseFakeMultiplayer, String[] param) {
		mFakeMultiplayer = false;
		if (mPlayers == 1 || mUseFakeMultiplayer) {
			mFakeMultiplayer = true;
			nFakePlayers = (short) mPlayers;
			app.menu.mOpen(app.menu.mMenus[NETWORKGAME], -1);
		} else app.changeScreen(gNetScreen.setFlag(NetFlag.Create, param));
	}
	
}
