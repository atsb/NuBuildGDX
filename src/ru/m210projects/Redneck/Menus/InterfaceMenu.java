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
import static ru.m210projects.Redneck.Globals.*;

import ru.m210projects.Build.Pattern.MenuItems.BuildMenu;
import ru.m210projects.Build.Pattern.MenuItems.MenuConteiner;
import ru.m210projects.Build.Pattern.MenuItems.MenuHandler;
import ru.m210projects.Build.Pattern.MenuItems.MenuItem;
import ru.m210projects.Build.Pattern.MenuItems.MenuProc;
import ru.m210projects.Build.Pattern.MenuItems.MenuSlider;
import ru.m210projects.Build.Pattern.MenuItems.MenuSwitch;
import ru.m210projects.Redneck.Main;

public class InterfaceMenu extends BuildMenu {

	public InterfaceMenu(final Main app)
	{
		RRTitle mTitle = new RRTitle("Interface Setup");
		int pos = 30;

		MenuSwitch messages = new MenuSwitch("Messages:", app.getFont(1), 47, pos += 12, 240, ud.fta_on==1, new MenuProc() {
			@Override
			public void run( MenuHandler handler, MenuItem pItem ) {
				MenuSwitch sw = (MenuSwitch) pItem;
				ud.fta_on = sw.value?1:0;
			}
		}, null, null) {
			@Override
			public void open() {
				value = ud.fta_on == 1;
			}
		};

		MenuSlider sScreenSize = new MenuSlider(app.pSlider, "Screen size:", app.getFont(1), 47, pos += 12, 240, ud.screen_size, 0, 4, 1,
				new MenuProc() {
					@Override
					public void run( MenuHandler handler, MenuItem pItem ) {
						MenuSlider slider = (MenuSlider) pItem;
						ud.screen_size = slider.value;
					}
				}, false);
		
		MenuSlider mCurSize = new MenuSlider(app.pSlider, "Mouse cursor size:", app.getFont(1), 47, pos += 12, 240, cfg.gMouseCursorSize,
				0x1000, 0x28000, 4096, new MenuProc() {
					@Override
					public void run( MenuHandler handler, MenuItem pItem ) {
						MenuSlider slider = (MenuSlider) pItem;
						cfg.gMouseCursorSize = slider.value;
					}
				}, false);
		
		pos += 5;
		MenuSwitch sCrosshair = new MenuSwitch("CROSSHAIR:", app.getFont(1), 47, pos += 12, 240, ud.crosshair == 1, new MenuProc() {
			@Override
			public void run( MenuHandler handler, MenuItem pItem ) {
				MenuSwitch sw = (MenuSwitch) pItem;
				ud.crosshair = sw.value?1:0;
			}
		}, null, null) {
			@Override
			public void open() {
				value = ud.crosshair == 1;
			}
		};

		MenuSlider sCrossSize = new MenuSlider(app.pSlider, "Crosshair size:", app.getFont(1), 47, pos += 12, 240, cfg.gCrossSize, 8192,
				65536, 2048, new MenuProc() {
					@Override
					public void run( MenuHandler handler, MenuItem pItem ) {
						MenuSlider slider = (MenuSlider) pItem;
						cfg.gCrossSize = slider.value;
					}
				}, false);
		pos += 5;
		MenuConteiner sShowStat = new MenuConteiner("Statistics:", app.getFont(1), 47, pos += 12, 240, null, 0, new MenuProc() {
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

		MenuSlider sStatSize = new MenuSlider(app.pSlider, "Statistics size:", app.getFont(1), 47, pos += 12, 240, cfg.gStatSize, 16384,
				65536, 8192, new MenuProc() {
					@Override
					public void run( MenuHandler handler, MenuItem pItem ) {
						MenuSlider slider = (MenuSlider) pItem;
						cfg.gStatSize = slider.value;
					}
				}, false);
		pos += 5;
		
		
		MenuSwitch sShowMapName = new MenuSwitch("Info at level startup:", app.getFont(1), 47, pos += 12, 240, cfg.showMapInfo==1, new MenuProc() {
			@Override
			public void run( MenuHandler handler, MenuItem pItem ) {
				MenuSwitch sw = (MenuSwitch) pItem;
				cfg.showMapInfo = sw.value?1:0;
			}
		}, null, null);

		MenuSwitch sShowFPS = new MenuSwitch("fps counter:", app.getFont(1), 47, pos += 12, 240, cfg.gShowFPS, new MenuProc() {
			@Override
			public void run( MenuHandler handler, MenuItem pItem ) {
				MenuSwitch sw = (MenuSwitch) pItem;
				cfg.gShowFPS = sw.value;
			}
		}, null, null);
		
		MenuSlider sFpsSize = new MenuSlider(app.pSlider, "Fps size:", app.getFont(1), 47, pos += 12, 240, (int)(cfg.gFpsScale * 65536), 32768, 3 * 65536, 8192,
			new MenuProc() {
				@Override
				public void run( MenuHandler handler, MenuItem pItem ) {
					MenuSlider slider = (MenuSlider) pItem;
					cfg.gFpsScale = slider.value / 65536.0f;
				}
			}, true);
		sFpsSize.digitalMax = 65536f;

		addItem(mTitle, false);
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
