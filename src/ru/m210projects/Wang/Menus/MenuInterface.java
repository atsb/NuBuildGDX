package ru.m210projects.Wang.Menus;

import static ru.m210projects.Build.Net.Mmulti.myconnectindex;
import static ru.m210projects.Wang.Border.SetBorder;
import static ru.m210projects.Wang.Game.Player;
import static ru.m210projects.Wang.Main.gs;

import ru.m210projects.Build.Pattern.MenuItems.BuildMenu;
import ru.m210projects.Build.Pattern.MenuItems.MenuConteiner;
import ru.m210projects.Build.Pattern.MenuItems.MenuHandler;
import ru.m210projects.Build.Pattern.MenuItems.MenuItem;
import ru.m210projects.Build.Pattern.MenuItems.MenuProc;
import ru.m210projects.Build.Pattern.MenuItems.MenuSlider;
import ru.m210projects.Build.Pattern.MenuItems.MenuSwitch;
import ru.m210projects.Wang.Main;

public class MenuInterface extends BuildMenu {

	public MenuInterface(final Main app) {
		WangTitle mTitle = new WangTitle("Interface Setup");
		int pos = 30;

		MenuSwitch messages = new WangSwitch("Messages", app.getFont(1), 35, pos += 10, 240, gs.Messages,
				new MenuProc() {
					@Override
					public void run(MenuHandler handler, MenuItem pItem) {
						MenuSwitch sw = (MenuSwitch) pItem;
						gs.Messages = sw.value;
					}
				}) {
			@Override
			public void open() {
				value = gs.Messages;
			}
		};

		MenuSlider sScreenSize = new MenuSlider(app.pSlider, "Screen size", app.getFont(1), 35, pos += 10, 240,
				gs.BorderNum, 0, 3, 1, new MenuProc() {
					@Override
					public void run(MenuHandler handler, MenuItem pItem) {
						MenuSlider slider = (MenuSlider) pItem;
						SetBorder(Player[myconnectindex], slider.value);
					}
				}, false);

		MenuSlider mCurSize = new MenuSlider(app.pSlider, "Mouse cursor size", app.getFont(1), 35, pos += 10, 240,
				gs.gMouseCursorSize, 0x1000, 0x28000, 4096, new MenuProc() {
					@Override
					public void run(MenuHandler handler, MenuItem pItem) {
						MenuSlider slider = (MenuSlider) pItem;
						gs.gMouseCursorSize = slider.value;
					}
				}, false);

		pos += 5;
		MenuSwitch sCrosshair = new WangSwitch("Crosshair", app.getFont(1), 35, pos += 10, 240, gs.Crosshair,
				new MenuProc() {
					@Override
					public void run(MenuHandler handler, MenuItem pItem) {
						MenuSwitch sw = (MenuSwitch) pItem;
						gs.Crosshair = sw.value;
					}
				}) {
			@Override
			public void open() {
				value = gs.Crosshair;
			}
		};

		MenuSlider sCrossSize = new MenuSlider(app.pSlider, "Crosshair size", app.getFont(1), 35, pos += 10, 240,
				gs.CrosshairSize, 16384, 65536, 8192, new MenuProc() {
					@Override
					public void run(MenuHandler handler, MenuItem pItem) {
						MenuSlider slider = (MenuSlider) pItem;
						gs.CrosshairSize = slider.value;
					}
				}, false);
		pos += 5;
		MenuConteiner sShowStat = new MenuConteiner("Statistics:", app.getFont(1), 35, pos += 10, 240, null, 0,
				new MenuProc() {
					@Override
					public void run(MenuHandler handler, MenuItem pItem) {
						MenuConteiner item = (MenuConteiner) pItem;
						gs.Stats = item.num;
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
				num = gs.Stats;
			}
		};

		MenuSlider sStatSize = new MenuSlider(app.pSlider, "Statistics size", app.getFont(1), 35, pos += 10, 240,
				gs.gStatSize, 16384, 2 * 65536, 4096, new MenuProc() {
					@Override
					public void run(MenuHandler handler, MenuItem pItem) {
						MenuSlider slider = (MenuSlider) pItem;
						gs.gStatSize = slider.value;
					}
				}, true);
		sStatSize.digitalMax = 65536.0f;
		pos += 5;

		WangSwitch sShowMapName = new WangSwitch("Info at level startup", app.getFont(1), 35, pos += 10, 240,
				gs.showMapInfo == 1, new MenuProc() {
					@Override
					public void run(MenuHandler handler, MenuItem pItem) {
						MenuSwitch sw = (MenuSwitch) pItem;
						gs.showMapInfo = sw.value ? 1 : 0;
					}
				});

		MenuSwitch sShowFPS = new WangSwitch("Fps counter", app.getFont(1), 35, pos += 10, 240, gs.gShowFPS,
				new MenuProc() {
					@Override
					public void run(MenuHandler handler, MenuItem pItem) {
						MenuSwitch sw = (MenuSwitch) pItem;
						gs.gShowFPS = sw.value;
					}
				});

		MenuSlider sFpsSize = new MenuSlider(app.pSlider, "Fps size", app.getFont(1), 35, pos += 10, 240,
				(int) (gs.gFpsScale * 65536), 32768, 3 * 65536, 8192, new MenuProc() {
					@Override
					public void run(MenuHandler handler, MenuItem pItem) {
						MenuSlider slider = (MenuSlider) pItem;
						gs.gFpsScale = slider.value / 65536.0f;
					}
				}, true);
		sFpsSize.pal = 16;
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
