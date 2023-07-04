// This file is part of PowerslaveGDX.
// Copyright (C) 2019  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// PowerslaveGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// PowerslaveGDX is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with PowerslaveGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.Powerslave.Menus;

import static ru.m210projects.Powerslave.Main.*;

import ru.m210projects.Build.Pattern.MenuItems.BuildMenu;
import ru.m210projects.Build.Pattern.MenuItems.MenuConteiner;
import ru.m210projects.Build.Pattern.MenuItems.MenuHandler;
import ru.m210projects.Build.Pattern.MenuItems.MenuItem;
import ru.m210projects.Build.Pattern.MenuItems.MenuProc;
import ru.m210projects.Build.Pattern.MenuItems.MenuSwitch;
import ru.m210projects.Build.Pattern.MenuItems.MenuTitle;
import ru.m210projects.Powerslave.Main;

public class MenuGameSet extends BuildMenu {

	public MenuGameSet(Main app)
	{
		MenuTitle mTitle = new PSTitle("Game setup", 160, 15, 0);
		int pos = 50;
		
		MenuSwitch sAutoAim = new MenuSwitch("AutoAim:", app.getFont(0), 25, pos += 12, 280, cfg.gAutoAim, new MenuProc() {
			@Override
			public void run( MenuHandler handler, MenuItem pItem ) {
				MenuSwitch sw = (MenuSwitch) pItem;
				cfg.gAutoAim = sw.value;
			}
		}, null, null);
		sAutoAim.fontShadow = true;

		MenuSwitch sStartup = new MenuSwitch("Startup window:", app.getFont(0), 25, pos += 12, 280, cfg.startup, new MenuProc() {
			@Override
			public void run( MenuHandler handler, MenuItem pItem ) {
				MenuSwitch sw = (MenuSwitch) pItem;
				cfg.startup = sw.value;
			}
		}, null, null);
		sStartup.fontShadow = true;

		MenuSwitch sCheckVersion = new MenuSwitch("Check for updates:", app.getFont(0), 25, pos += 12, 280, cfg.checkVersion,
				new MenuProc() {
					@Override
					public void run( MenuHandler handler, MenuItem pItem ) {
						MenuSwitch sw = (MenuSwitch) pItem;
						cfg.checkVersion = sw.value;
					}
				}, null, null);
		sCheckVersion.fontShadow = true;
		
		MenuSwitch sShadows = new MenuSwitch("Object shadows:", app.getFont(0), 25, pos += 12, 280, cfg.bNewShadows,
				new MenuProc() {
					@Override
					public void run( MenuHandler handler, MenuItem pItem ) {
						MenuSwitch sw = (MenuSwitch) pItem;
						cfg.bNewShadows = sw.value;
					}
				}, "projected", "circular");
		sShadows.fontShadow = true;
		
		MenuSwitch sSubtitles = new MenuSwitch("Subtitles:", app.getFont(0), 25, pos += 12, 280, cfg.bSubtitles,
				new MenuProc() {
					@Override
					public void run( MenuHandler handler, MenuItem pItem ) {
						MenuSwitch sw = (MenuSwitch) pItem;
						cfg.bSubtitles = sw.value;
					}
				}, "YES", "NO");
		sSubtitles.fontShadow = true;
		
		MenuSwitch sGrenade = new MenuSwitch("Grenade throw fix:", app.getFont(0), 25, pos += 12, 280, cfg.bGrenadeFix,
				new MenuProc() {
					@Override
					public void run( MenuHandler handler, MenuItem pItem ) {
						MenuSwitch sw = (MenuSwitch) pItem;
						cfg.bGrenadeFix = sw.value;
					}
				}, null, null);
		sGrenade.fontShadow = true;
		
		MenuSwitch sWaspSound = new MenuSwitch("Wasp sound:", app.getFont(0), 25, pos += 12, 280, cfg.bWaspSound,
				new MenuProc() {
					@Override
					public void run( MenuHandler handler, MenuItem pItem ) {
						MenuSwitch sw = (MenuSwitch) pItem;
						cfg.bWaspSound = sw.value;
					}
				}, null, null);
		sWaspSound.fontShadow = true;

		MenuConteiner mPlayingDemo = new MenuConteiner("Demos playback:", app.getFont(0), 25, pos += 12, 280, null, 0,
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
		mPlayingDemo.fontShadow = true;
		mPlayingDemo.listShadow = true;

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
		sOverlay.fontShadow = true;
		sOverlay.listShadow = true;

		addItem(mTitle, false);
		addItem(sAutoAim, true);
		addItem(sStartup, false);
		addItem(sCheckVersion, false);
		addItem(sShadows, false);
		addItem(sSubtitles, false);
		addItem(sGrenade, false);
		addItem(sWaspSound, false);
		addItem(mPlayingDemo, false);
		addItem(sOverlay, false);
	}
}
