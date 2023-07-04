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
import ru.m210projects.Build.Pattern.MenuItems.MenuSlider;
import ru.m210projects.Build.Pattern.MenuItems.MenuSwitch;
import ru.m210projects.Build.Pattern.MenuItems.MenuTitle;
import ru.m210projects.Powerslave.Main;

public class MenuInterfaceSet extends BuildMenu {
	
	public MenuInterfaceSet(Main app)
	{
		int pos = 40;
		MenuTitle mLogo = new PSTitle("Interface setup", 160, 15, 0);

		MenuSwitch messages = new MenuSwitch("Messages:", app.getFont(0), 25, pos += 12, 280, true, new MenuProc() {
			@Override
			public void run( MenuHandler handler, MenuItem pItem ) {
				MenuSwitch sw = (MenuSwitch) pItem;
				cfg.gShowMessages = sw.value;
			}
		}, null, null) {
			@Override
			public void open() {
				value = cfg.gShowMessages;
			}
		};
		messages.fontShadow = true;
		
		MenuSlider sScreenSize = new MenuSlider(app.pSlider, "Screen size:", app.getFont(0), 25, pos += 12, 280, cfg.nScreenSize, 0, 2, 1,
				new MenuProc() {
					@Override
					public void run( MenuHandler handler, MenuItem pItem ) {
						MenuSlider slider = (MenuSlider) pItem;
						cfg.nScreenSize = slider.value;
					}
				}, false) {
			@Override
			public void open() {
				value = cfg.nScreenSize;
			}
		};
		sScreenSize.fontShadow = true;

		MenuSlider mCurSize = new MenuSlider(app.pSlider, "Mouse cursor size:", app.getFont(0), 25, pos += 12, 280, cfg.gMouseCursorSize,
				0x1000, 0x28000, 4096, new MenuProc() {
					@Override
					public void run( MenuHandler handler, MenuItem pItem ) {
						MenuSlider slider = (MenuSlider) pItem;
						cfg.gMouseCursorSize = slider.value;
					}
				}, false);
		mCurSize.fontShadow = true;
		
		pos += 5;
		MenuSwitch sCrosshair = new MenuSwitch("CROSSHAIR:", app.getFont(0), 25, pos += 12, 280, true, new MenuProc() {
			@Override
			public void run( MenuHandler handler, MenuItem pItem ) {
				MenuSwitch sw = (MenuSwitch) pItem;
				cfg.gCrosshair = sw.value;
			}
		}, null, null) {
			@Override
			public void open() {
				value = cfg.gCrosshair;
			}
		};
		sCrosshair.fontShadow = true;
		
		MenuSlider sCrossSize = new MenuSlider(app.pSlider, "Crosshair size:", app.getFont(0), 25, pos += 12, 280, cfg.gCrossSize, 8192,
				3 * 65536, 8192, new MenuProc() {
					@Override
					public void run( MenuHandler handler, MenuItem pItem ) {
						MenuSlider slider = (MenuSlider) pItem;
						cfg.gCrossSize = slider.value;
					}
				}, true);
		sCrossSize.digitalMax = 65536.0f;
		sCrossSize.fontShadow = true;

		MenuConteiner sShowStat = new MenuConteiner("Statistics:", app.getFont(0), 25, pos += 12, 280, null, 0, new MenuProc() {
			@Override
			public void run( MenuHandler handler, MenuItem pItem ) {
				MenuConteiner item = (MenuConteiner) pItem;
				cfg.gShowStat = item.num;
			}
		}) {
			@Override
			public void open() {
				if (this.list == null) {
					this.list = new char[3][];
					this.list[0] = "Off".toCharArray();
					this.list[1] = "Always show".toCharArray();
					this.list[2] = "Only on a minimap".toCharArray();
				}
				num = cfg.gShowStat;
			}
		};
		sShowStat.fontShadow = true;
		sShowStat.listShadow = true;
		
		MenuSlider sStatSize = new MenuSlider(app.pSlider,"Statistics size:", app.getFont(0), 25, pos += 12, 280, cfg.gStatSize, 16384,
				2*65536, 4096, new MenuProc() {
					@Override
					public void run( MenuHandler handler, MenuItem pItem ) {
						MenuSlider slider = (MenuSlider) pItem;
						cfg.gStatSize = slider.value;
					}
				}, true);
		sStatSize.digitalMax = 65536.0f;
		sStatSize.fontShadow = true;
		
		pos += 5;
		MenuConteiner sShowMapName = new MenuConteiner("Info at level startup:", app.getFont(0), 25, pos += 12, 280, null, 0,
				new MenuProc() {
					@Override
					public void run( MenuHandler handler, MenuItem pItem ) {
						MenuConteiner item = (MenuConteiner) pItem;
						cfg.showMapInfo = item.num;
					}
				}) {
			@Override
			public void open() {
				if (this.list == null) {
					this.list = new char[2][];
					this.list[0] = "No".toCharArray();
					this.list[1] = "map title".toCharArray();
				}

				num = cfg.showMapInfo;
			}
		};
		sShowMapName.fontShadow = true;
		sShowMapName.listShadow = true;

		MenuSwitch sShowFPS = new MenuSwitch("fps counter:", app.getFont(0), 25, pos += 12, 280, cfg.gShowFPS, new MenuProc() {
			@Override
			public void run( MenuHandler handler, MenuItem pItem ) {
				MenuSwitch sw = (MenuSwitch) pItem;
				cfg.gShowFPS = sw.value;
			}
		}, null, null);
		sShowFPS.fontShadow = true;
		
		MenuSlider sFpsSize = new MenuSlider(app.pSlider, "Fps size:", app.getFont(0), 25, pos += 12, 280, (int)(cfg.gFpsScale * 65536), 32768, 3 * 65536, 8192,
			new MenuProc() {
				@Override
				public void run( MenuHandler handler, MenuItem pItem ) {
					MenuSlider slider = (MenuSlider) pItem;
					cfg.gFpsScale = slider.value / 65536.0f;
				}
			}, true);
		sFpsSize.digitalMax = 65536f;
		sFpsSize.fontShadow = true;
	
		addItem(mLogo, false);

		addItem(messages, true);
		addItem(sScreenSize, false);
		addItem(mCurSize, false);
		addItem(sCrosshair, false);
		addItem(sCrossSize, false);
		addItem(sShowStat, false);
		addItem(sStatSize, false);
		addItem(sShowMapName, false);
		addItem(sShowFPS, false);
		addItem(sFpsSize, false);
	}

}
