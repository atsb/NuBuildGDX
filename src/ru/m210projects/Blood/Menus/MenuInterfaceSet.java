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
import static ru.m210projects.Blood.View.*;

import ru.m210projects.Blood.Main;
import ru.m210projects.Build.Pattern.MenuItems.BuildMenu;
import ru.m210projects.Build.Pattern.MenuItems.MenuConteiner;
import ru.m210projects.Build.Pattern.MenuItems.MenuHandler;
import ru.m210projects.Build.Pattern.MenuItems.MenuItem;
import ru.m210projects.Build.Pattern.MenuItems.MenuProc;
import ru.m210projects.Build.Pattern.MenuItems.MenuSlider;
import ru.m210projects.Build.Pattern.MenuItems.MenuSwitch;
import ru.m210projects.Build.Pattern.MenuItems.MenuTitle;

public class MenuInterfaceSet extends BuildMenu {
	
	public MenuInterfaceSet(Main app)
	{
		int pos = 0;
		MenuTitle mLogo = new MenuTitle(app.pEngine, "Interface setup", app.getFont(1), 160, 20, 2038);

		MenuSwitch messages = new MenuSwitch("Messages:", app.getFont(3), 47, pos += 60, 240, true, new MenuProc() {
			@Override
			public void run( MenuHandler handler, MenuItem pItem ) {
				MenuSwitch sw = (MenuSwitch) pItem;
				cfg.MessageState = sw.value;
			}
		}, null, null) {
			@Override
			public void open() {
				value = cfg.MessageState;
			}
		};
		
		MenuSlider sScreenSize = new MenuSlider(app.pSlider, "Screen size:", app.getFont(3), 47, pos += 10, 240, cfg.gViewSize, 0, 3, 1,
				new MenuProc() {
					@Override
					public void run( MenuHandler handler, MenuItem pItem ) {
						MenuSlider slider = (MenuSlider) pItem;
						cfg.gViewSize = slider.value;
						viewResizeView(cfg.gViewSize);
					}
				}, false) {
			@Override
			public void open() {
				value = cfg.gViewSize;
			}
		};

		MenuSlider mCurSize = new MenuSlider(app.pSlider, "Mouse cursor size:", app.getFont(3), 47, pos += 10, 240, cfg.gMouseCursorSize,
				0x1000, 0x28000, 4096, new MenuProc() {
					@Override
					public void run( MenuHandler handler, MenuItem pItem ) {
						MenuSlider slider = (MenuSlider) pItem;
						cfg.gMouseCursorSize = slider.value;
					}
				}, false);
		
		pos += 5;
		MenuSwitch sCrosshair = new MenuSwitch("CROSSHAIR:", app.getFont(3), 47, pos += 10, 240, true, new MenuProc() {
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
		
		MenuSlider sCrossSize = new MenuSlider(app.pSlider, "Crosshair size:", app.getFont(3), 47, pos += 10, 240, cfg.gCrossSize, 8192,
				3 * 65536, 8192, new MenuProc() {
					@Override
					public void run( MenuHandler handler, MenuItem pItem ) {
						MenuSlider slider = (MenuSlider) pItem;
						cfg.gCrossSize = slider.value;
					}
				}, true);
		sCrossSize.digitalMax = 65536.0f;

		MenuConteiner sShowStat = new MenuConteiner("Statistics:", app.getFont(3), 47, pos += 10, 240, null, 0, new MenuProc() {
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
		
		MenuSlider sStatSize = new MenuSlider(app.pSlider,"Statistics size:", app.getFont(3), 47, pos += 10, 240, cfg.gStatSize, 16384,
				2*65536, 4096, new MenuProc() {
					@Override
					public void run( MenuHandler handler, MenuItem pItem ) {
						MenuSlider slider = (MenuSlider) pItem;
						cfg.gStatSize = slider.value;
					}
				}, true);
		sStatSize.digitalMax = 65536.0f;
//		MenuSlider sHUDSize = new MenuSlider(app.pSlider,"Hud size:", app.getFont(3), 47, pos += 10, 240, cfg.gHudSize, 16384,
//				2*65536, 4096, new MenuProc() {
//					@Override
//					public void run( MenuHandler handler, MenuItem pItem ) {
//						MenuSlider slider = (MenuSlider) pItem;
//						cfg.gHudSize = slider.value;
//					}
//				}, true);
//		sHUDSize.digitalMax = 65536.0f;
		pos += 5;
		
		MenuConteiner sShowMapName = new MenuConteiner("Info at level startup:", app.getFont(3), 46, pos += 10, 240, null, 0,
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
					this.list = new char[3][];
					this.list[0] = "No".toCharArray();
					this.list[1] = "map title".toCharArray();
					this.list[2] = "map title & author".toCharArray();
				}

				num = cfg.showMapInfo;
			}
		};

		MenuSwitch sShowFPS = new MenuSwitch("fps counter:", app.getFont(3), 47, pos += 10, 240, cfg.gShowFPS, new MenuProc() {
			@Override
			public void run( MenuHandler handler, MenuItem pItem ) {
				MenuSwitch sw = (MenuSwitch) pItem;
				cfg.gShowFPS = sw.value;
			}
		}, null, null);
		
		MenuSlider sFpsSize = new MenuSlider(app.pSlider, "Fps size:", app.getFont(3), 47, pos += 10, 240, (int)(cfg.gFpsScale * 65536), 32768, 3 * 65536, 8192,
			new MenuProc() {
				@Override
				public void run( MenuHandler handler, MenuItem pItem ) {
					MenuSlider slider = (MenuSlider) pItem;
					cfg.gFpsScale = slider.value / 65536.0f;
				}
			}, true);
		sFpsSize.digitalMax = 65536f;
	
		addItem(mLogo, false);

		addItem(messages, true);
		addItem(sScreenSize, false);
		addItem(mCurSize, false);
		addItem(sCrosshair, false);
		addItem(sCrossSize, false);
		addItem(sShowStat, false);
		addItem(sStatSize, false);
//		addItem(sHUDSize, false);
		addItem(sShowMapName, false);
		addItem(sShowFPS, false);
		addItem(sFpsSize, false);
		
		addItem(app.menu.addMenuBlood(), false);
	}

}
