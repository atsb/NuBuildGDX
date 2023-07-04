//This file is part of BuildGDX.
//Copyright (C) 2017-2018  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
//BuildGDX is free software: you can redistribute it and/or modify
//it under the terms of the GNU General Public License as published by
//the Free Software Foundation, either version 3 of the License, or
//(at your option) any later version.
//
//BuildGDX is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU General Public License for more details.
//
//You should have received a copy of the GNU General Public License
//along with BuildGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.Build.Pattern.CommonMenus;

import static ru.m210projects.Build.Net.Mmulti.NETPORT;
import static ru.m210projects.Build.Pattern.MenuItems.MenuTextField.LETTERS;
import static ru.m210projects.Build.Pattern.MenuItems.MenuTextField.NUMBERS;
import static ru.m210projects.Build.Pattern.MenuItems.MenuTextField.POINT;

import java.util.Arrays;

import ru.m210projects.Build.Pattern.BuildFont;
import ru.m210projects.Build.Pattern.BuildGame;
import ru.m210projects.Build.Pattern.MenuItems.BuildMenu;
import ru.m210projects.Build.Pattern.MenuItems.MenuButton;
import ru.m210projects.Build.Pattern.MenuItems.MenuHandler;
import ru.m210projects.Build.Pattern.MenuItems.MenuItem;
import ru.m210projects.Build.Pattern.MenuItems.MenuProc;
import ru.m210projects.Build.Pattern.MenuItems.MenuTextField;
import ru.m210projects.Build.Pattern.MenuItems.MenuTitle;

public abstract class MenuJoin extends BuildMenu {
	
	public MenuTextField mPortnum;
	public MenuTextField mPlayer;
	public MenuTextField mIPAddress;
	public MenuButton mConnect;
	
	public MenuJoin(final BuildGame app, int posx, int posy, int menuHeight, int width, BuildFont style)
	{
		addItem(getTitle(app, "Join a game"), false);

		mPortnum = new MenuTextField("Network socket number", "" + app.pCfg.mPort, style, posx, posy += menuHeight, width,
				NUMBERS, new MenuProc() {
					@Override
					public void run(MenuHandler handler, MenuItem pItem) {
						MenuTextField item = (MenuTextField) pItem;
						if(item.typed.length() < 8) {
							app.pCfg.mPort = Integer.parseInt(item.typed);
						}
						else {
							System.arraycopy(item.otypingBuf, 0, item.typingBuf, 0, 16);
							item.inputlen = item.oinputlen;
						}
					}
				});

		mPlayer = new MenuTextField("Player name", app.pCfg.pName, style, posx, posy += menuHeight, width, NUMBERS | LETTERS,
				new MenuProc() {
					@Override
					public void run(MenuHandler handler, MenuItem pItem) {
						MenuTextField item = (MenuTextField) pItem;
						app.pCfg.pName = item.typed;
					}
				}) {
			@Override
			public void open() {
				Arrays.fill(typingBuf, (char) 0);
				inputlen = app.pCfg.pName.length();
				System.arraycopy(app.pCfg.pName.toCharArray(), 0, typingBuf, 0, inputlen);
			}
		};

		mIPAddress = new MenuTextField("IP Address", app.pCfg.mAddress, style, posx, posy += menuHeight, width,
				NUMBERS | LETTERS | POINT, new MenuProc() {
					@Override
					public void run(MenuHandler handler, MenuItem pItem) {
						MenuTextField item = (MenuTextField) pItem;
						app.pCfg.mAddress = item.typed;
					}
				}) {
			@Override
			public void open() {
				Arrays.fill(typingBuf, (char) 0);
				inputlen = app.pCfg.mAddress.length();
				System.arraycopy(app.pCfg.mAddress.toCharArray(), 0, typingBuf, 0, inputlen);
			}
		};

		mConnect = new MenuButton("Connect", style, 0, posy + (2 * menuHeight), 320, 1, 0, null, -1, new MenuProc() {
			@Override
			public void run(MenuHandler handler, MenuItem pItem) {
				if (app.pCfg.mAddress.isEmpty())
					return;
				String[] param = new String[] { "-n0", app.pCfg.mAddress, (app.pCfg.mPort != NETPORT ? ("-p " + app.pCfg.mPort) : null) };
				joinGame(param);
			}
		}, 0);

		addItem(mPortnum, true);
		addItem(mPlayer, false);
		addItem(mIPAddress, false);
		addItem(mConnect, false);
	}

	public abstract MenuTitle getTitle(BuildGame app, String text);
	
	public abstract void joinGame(String[] param);
}
