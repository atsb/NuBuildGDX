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

import static ru.m210projects.Blood.Main.cfg;
import static ru.m210projects.Blood.Main.gGameScreen;
import static ru.m210projects.Blood.Main.game;
import static ru.m210projects.Build.Net.Mmulti.myconnectindex;
import static ru.m210projects.Build.Net.Mmulti.numplayers;

import ru.m210projects.Blood.Main;
import ru.m210projects.Build.Pattern.MenuItems.BuildMenu;
import ru.m210projects.Build.Pattern.MenuItems.MenuConteiner;
import ru.m210projects.Build.Pattern.MenuItems.MenuHandler;
import ru.m210projects.Build.Pattern.MenuItems.MenuItem;
import ru.m210projects.Build.Pattern.MenuItems.MenuProc;
import ru.m210projects.Build.Pattern.MenuItems.MenuSwitch;
import ru.m210projects.Build.Pattern.MenuItems.MenuTitle;

public class MenuGameSet extends BuildMenu {

	public MenuGameSet(Main app)
	{
		MenuTitle mTitle = new MenuTitle(app.pEngine, "Game Setup", app.getFont(1), 160, 20, 2038);
		int pos = 40;
		MenuSwitch sViewBobbing = new MenuSwitch("VIEW BOBBING:", app.getFont(3), 46, pos += 10, 240, cfg.gBobWidth, new MenuProc() {
			@Override
			public void run( MenuHandler handler, MenuItem pItem ) {
				MenuSwitch sw = (MenuSwitch) pItem;
				cfg.gBobWidth = sw.value;
			}
		}, null, null);
		MenuSwitch sViewSwaying = new MenuSwitch("VIEW SWAYING:", app.getFont(3), 46, pos += 10, 240, cfg.gBobHeight,
				new MenuProc() {
					@Override
					public void run( MenuHandler handler, MenuItem pItem ) {
						MenuSwitch sw = (MenuSwitch) pItem;
						cfg.gBobHeight = sw.value;
					}
				}, null, null);

		MenuSwitch sSlopeTilt = new MenuSwitch("SLOPE TILTING:", app.getFont(3), 46, pos += 10, 240, cfg.gSlopeTilt, new MenuProc() {
			@Override
			public void run( MenuHandler handler, MenuItem pItem ) {
				MenuSwitch sw = (MenuSwitch) pItem;
				cfg.gSlopeTilt = sw.value;
				game.net.gProfile[myconnectindex].slopetilt = cfg.gSlopeTilt;
				if (numplayers > 1)
					game.net.InitProfile(myconnectindex);
			}
		}, null, null) {

			@Override
			public void open() {
				mCheckEnableItem(numplayers < 2 || !game.isCurrentScreen(gGameScreen));
			}
		};

		MenuSwitch sAutoAim = new MenuSwitch("AutoAim:", app.getFont(3), 46, pos += 10, 240, cfg.gAutoAim, new MenuProc() {
			@Override
			public void run( MenuHandler handler, MenuItem pItem ) {
				MenuSwitch sw = (MenuSwitch) pItem;
				cfg.gAutoAim = sw.value;
				game.net.gProfile[myconnectindex].autoaim = cfg.gAutoAim;
				if (numplayers > 1)
					game.net.InitProfile(myconnectindex);
			}
		}, null, null) {

			@Override
			public void open() {
				mCheckEnableItem(numplayers < 2 || !game.isCurrentScreen(gGameScreen));
			}
		};

		MenuSwitch sSecretCount = new MenuSwitch("Auto secrets counter:", app.getFont(3), 46, pos += 10, 240, cfg.useautosecretcount,
				new MenuProc() {
					@Override
					public void run( MenuHandler handler, MenuItem pItem ) {
						MenuSwitch sw = (MenuSwitch) pItem;
						cfg.useautosecretcount = sw.value;
					}
				}, null, null);

		MenuSwitch sStartup = new MenuSwitch("Startup window:", app.getFont(3), 46, pos += 10, 240, cfg.startup, new MenuProc() {
			@Override
			public void run( MenuHandler handler, MenuItem pItem ) {
				MenuSwitch sw = (MenuSwitch) pItem;
				cfg.startup = sw.value;
			}
		}, null, null);

		MenuSwitch sShowCutscenes = new MenuSwitch("Cutscenes:", app.getFont(3), 46, pos += 10, 240, cfg.showCutscenes,
				new MenuProc() {
					@Override
					public void run( MenuHandler handler, MenuItem pItem ) {
						MenuSwitch sw = (MenuSwitch) pItem;
						cfg.showCutscenes = sw.value;
					}
				}, null, null);

		MenuConteiner mPlayingDemo = new MenuConteiner("Demos playback:", app.getFont(3), 46, pos += 10, 240, null, 0,
				new MenuProc() {
					@Override
					public void run( MenuHandler handler, MenuItem pItem ) {
						MenuConteiner item = (MenuConteiner) pItem;
						cfg.gDemoSeq = item.num;
					}
				}) {
			@Override
			public void open() {
				if (this.list == null) {
					this.list = new char[3][];
					this.list[0] = "Off".toCharArray();
					this.list[1] = "In order".toCharArray();
					this.list[2] = "Randomly".toCharArray();
				}
				num = cfg.gDemoSeq;
			}
		};

		MenuConteiner sOverlay = new MenuConteiner("Overlay map:", app.getFont(3), 46, pos += 10, 240, null, 0, new MenuProc() {
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
		
		MenuSwitch mVanilla = new MenuSwitch("Vanilla mode:", app.getFont(3), 46, pos += 10, 240, cfg.gVanilla, new MenuProc() {
			@Override
			public void run( MenuHandler handler, MenuItem pItem ) {
				MenuSwitch sw = (MenuSwitch) pItem;
				cfg.gVanilla = sw.value;
			}
		}, null, null);

		addItem(mTitle, false);
		addItem(sViewBobbing, true);
		addItem(sViewSwaying, false);
		addItem(sSlopeTilt, false);
		addItem(sAutoAim, false);
		addItem(sSecretCount, false);
		addItem(sStartup, false);
		addItem(sShowCutscenes, false);
		addItem(mPlayingDemo, false);
		addItem(sOverlay, false);
		addItem(mVanilla, false);

		addItem(app.menu.addMenuBlood(), false);
	}
}
