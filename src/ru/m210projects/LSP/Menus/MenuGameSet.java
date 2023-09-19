// This file is part of LSPGDX.
// Copyright (C) 2020  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// LSPGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// LSPGDX is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with LSPGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.LSP.Menus;

import static ru.m210projects.Build.Engine.numsectors;
import static ru.m210projects.Build.Engine.sector;
import static ru.m210projects.Build.Engine.show2dsector;
import static ru.m210projects.LSP.Main.cfg;

import ru.m210projects.Build.Pattern.MenuItems.BuildMenu;
import ru.m210projects.Build.Pattern.MenuItems.MenuConteiner;
import ru.m210projects.Build.Pattern.MenuItems.MenuHandler;
import ru.m210projects.Build.Pattern.MenuItems.MenuItem;
import ru.m210projects.Build.Pattern.MenuItems.MenuProc;
import ru.m210projects.Build.Pattern.MenuItems.MenuSwitch;
import ru.m210projects.LSP.Main;

public class MenuGameSet extends BuildMenu {

	public MenuGameSet(Main app)
	{
		int pos = 50;
		
		MenuSwitch sBob = new MenuSwitch("Head bob:", app.getFont(0), 25, pos += 12, 280, cfg.bHeadBob, new MenuProc() {
			@Override
			public void run( MenuHandler handler, MenuItem pItem ) {
				MenuSwitch sw = (MenuSwitch) pItem;
				cfg.bHeadBob = sw.value;
			}
		}, null, null);

		MenuSwitch sStartup = new MenuSwitch("Startup window:", app.getFont(0), 25, pos += 12, 280, cfg.startup, new MenuProc() {
			@Override
			public void run( MenuHandler handler, MenuItem pItem ) {
				MenuSwitch sw = (MenuSwitch) pItem;
				cfg.startup = sw.value;
			}
		}, null, null);
		
		MenuConteiner sOverlay = new MenuConteiner("Overlay map:", app.getFont(0), 25, pos += 12, 280, null, 0, new MenuProc() {
			@Override
			public void run( MenuHandler handler, MenuItem pItem ) {
				MenuConteiner item = (MenuConteiner) pItem;
				cfg.gOverlayMap = item.num;
			}
		}) {

			@Override
			public void open() {
				if (this.list == null) {
					this.list = new char[3][];
					this.list[0] = "Full only".toCharArray();
					this.list[1] = "Overlay only".toCharArray();
					this.list[2] = "Full and overlay".toCharArray();
				}
				num = cfg.gOverlayMap;
			}
		};
		
		MenuSwitch sSpamProjs = new MenuSwitch("Enemy attack spam:", app.getFont(0), 25, pos += 12, 280, cfg.bOriginal,
				new MenuProc() {
					@Override
					public void run( MenuHandler handler, MenuItem pItem ) {
						MenuSwitch sw = (MenuSwitch) pItem;
						cfg.bOriginal = sw.value;
					}
				}, null, null);
		
		MenuSwitch sShowExits = new MenuSwitch("Show exits on automap", app.getFont(0), 25, pos += 12, 280, true, new MenuProc() {
			@Override
			public void run( MenuHandler handler, MenuItem pItem ) {
				MenuSwitch sw = (MenuSwitch) pItem;
				cfg.bShowExit = sw.value;
				
				for (int i = 0; i < numsectors; i++) {
					switch(sector[i].lotag) {
					case 99:
					case 98:
					case 97:
						if(cfg.bShowExit)
							show2dsector[i >> 3] |= 1 << (i & 7);
						else show2dsector[i >> 3] &= ~(1 << (i & 7));
						break;
					}
				}
			}
		}, null, null) {
			@Override
			public void open() {
				value = cfg.bShowExit;
			}
		};

		addItem(sBob, true);
		addItem(sStartup, false);
		addItem(sOverlay, false);
		addItem(sSpamProjs, false);
		addItem(sShowExits, false);
	}
}
