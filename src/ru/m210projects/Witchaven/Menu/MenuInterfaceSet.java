package ru.m210projects.Witchaven.Menu;

import static ru.m210projects.Witchaven.Main.*;

import ru.m210projects.Build.Pattern.MenuItems.BuildMenu;
import ru.m210projects.Build.Pattern.MenuItems.MenuConteiner;
import ru.m210projects.Build.Pattern.MenuItems.MenuHandler;
import ru.m210projects.Build.Pattern.MenuItems.MenuItem;
import ru.m210projects.Build.Pattern.MenuItems.MenuProc;
import ru.m210projects.Build.Pattern.MenuItems.MenuSlider;
import ru.m210projects.Build.Pattern.MenuItems.MenuSwitch;
import ru.m210projects.Witchaven.Main;

public class MenuInterfaceSet extends BuildMenu {
	
	public MenuInterfaceSet(Main app)
	{
		int pos = 0;
		WHTitle mLogo = new WHTitle("Interface Setup", 90, pos);

		MenuSwitch messages = new MenuSwitch("Messages:", app.WH2 ? app.getFont(0) : app.getFont(1), 47, pos += 60, 240, true, new MenuProc() {
			@Override
			public void run( MenuHandler handler, MenuItem pItem ) {
				MenuSwitch sw = (MenuSwitch) pItem;
				whcfg.MessageState = sw.value;
			}
		}, null, null) {
			@Override
			public void open() {
				value = whcfg.MessageState;
			}
		};
		
		MenuSlider sScreenSize = new MenuSlider(app.pSlider, "Screen size:", app.WH2 ? app.getFont(0) : app.getFont(1), 47, pos += 10, 240, whcfg.gViewSize, 0, 1, 1,
				new MenuProc() {
					@Override
					public void run( MenuHandler handler, MenuItem pItem ) {
						MenuSlider slider = (MenuSlider) pItem;
						whcfg.gViewSize = slider.value;
					}
				}, false) {
			@Override
			public void open() {
				value = whcfg.gViewSize;
			}
		};

		MenuSlider mCurSize = new MenuSlider(app.pSlider, "Mouse cursor size:", app.WH2 ? app.getFont(0) : app.getFont(1), 47, pos += 10, 240, whcfg.gMouseCursorSize,
				0x1000, 0x28000, 4096, new MenuProc() {
					@Override
					public void run( MenuHandler handler, MenuItem pItem ) {
						MenuSlider slider = (MenuSlider) pItem;
						whcfg.gMouseCursorSize = slider.value;
					}
				}, false);
		
		pos += 5;
		MenuSwitch sCrosshair = new MenuSwitch("Crosshair:", app.WH2 ? app.getFont(0) : app.getFont(1), 47, pos += 10, 240, true, new MenuProc() {
			@Override
			public void run( MenuHandler handler, MenuItem pItem ) {
				MenuSwitch sw = (MenuSwitch) pItem;
				whcfg.gCrosshair = sw.value;
			}
		}, null, null) {
			@Override
			public void open() {
				value = whcfg.gCrosshair;
			}
		};
		
		MenuSlider sCrossSize = new MenuSlider(app.pSlider, "Crosshair size:", app.WH2 ? app.getFont(0) : app.getFont(1), 47, pos += 10, 240, whcfg.gCrossSize, 8192,
				3 * 65536, 8192, new MenuProc() {
					@Override
					public void run( MenuHandler handler, MenuItem pItem ) {
						MenuSlider slider = (MenuSlider) pItem;
						whcfg.gCrossSize = slider.value;
					}
				}, true);
		sCrossSize.digitalMax = 65536.0f;
		
		MenuSlider sHudSize = new MenuSlider(app.pSlider,"HUD size:", app.WH2 ? app.getFont(0) : app.getFont(1), 47, pos += 10, 240, whcfg.gHudScale, 32768,
				2*65536, 4096, new MenuProc() {
					@Override
					public void run( MenuHandler handler, MenuItem pItem ) {
						MenuSlider slider = (MenuSlider) pItem;
						whcfg.gHudScale = slider.value;
					}
				}, true);
		sHudSize.digitalMax = 65536.0f;

		MenuConteiner sShowStat = new MenuConteiner("Statistics:", app.WH2 ? app.getFont(0) : app.getFont(1), 47, pos += 10, 240, null, 0, new MenuProc() {
			@Override
			public void run( MenuHandler handler, MenuItem pItem ) {
				MenuConteiner item = (MenuConteiner) pItem;
				whcfg.gShowStat = item.num;
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
				num = whcfg.gShowStat;
			}
		};
		
		MenuSlider sStatSize = new MenuSlider(app.pSlider,"Statistics size:", app.WH2 ? app.getFont(0) : app.getFont(1), 47, pos += 10, 240, whcfg.gStatSize, 16384,
				2*65536, 4096, new MenuProc() {
					@Override
					public void run( MenuHandler handler, MenuItem pItem ) {
						MenuSlider slider = (MenuSlider) pItem;
						whcfg.gStatSize = slider.value;
					}
				}, true);
		sStatSize.digitalMax = 65536.0f;
		pos += 5;
		
		MenuSwitch sShowMapName = new MenuSwitch("Info at level startup:", app.WH2 ? app.getFont(0) : app.getFont(1), 47, pos += 10, 240, whcfg.showMapInfo, new MenuProc() {
			@Override
			public void run( MenuHandler handler, MenuItem pItem ) {
				MenuSwitch sw = (MenuSwitch) pItem;
				whcfg.showMapInfo = sw.value;
			}
		}, null, null);

		MenuSwitch sShowFPS = new MenuSwitch("Fps counter:", app.WH2 ? app.getFont(0) : app.getFont(1), 47, pos += 10, 240, whcfg.gShowFPS, new MenuProc() {
			@Override
			public void run( MenuHandler handler, MenuItem pItem ) {
				MenuSwitch sw = (MenuSwitch) pItem;
				whcfg.gShowFPS = sw.value;
			}
		}, null, null);
		
		MenuSlider sFpsSize = new MenuSlider(app.pSlider, "Fps size:", app.WH2 ? app.getFont(0) : app.getFont(1), 47, pos += 10, 240, (int)(whcfg.gFpsScale * 65536), 32768, 3 * 65536, 8192,
			new MenuProc() {
				@Override
				public void run( MenuHandler handler, MenuItem pItem ) {
					MenuSlider slider = (MenuSlider) pItem;
					whcfg.gFpsScale = slider.value / 65536.0f;
				}
			}, true);
		sFpsSize.digitalMax = 65536f;
	
		addItem(mLogo, false);

		addItem(messages, true);
		addItem(sScreenSize, false);
		addItem(mCurSize, false);
		addItem(sCrosshair, false);
		addItem(sCrossSize, false);
		addItem(sHudSize, false);
		addItem(sShowStat, false);
		addItem(sStatSize, false);
		addItem(sShowMapName, false);
		addItem(sShowFPS, false);
		addItem(sFpsSize, false);
	}

}
