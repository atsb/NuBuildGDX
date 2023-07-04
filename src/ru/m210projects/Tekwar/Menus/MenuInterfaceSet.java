package ru.m210projects.Tekwar.Menus;

import static ru.m210projects.Tekwar.Config.TOGGLE_HEALTH;
import static ru.m210projects.Tekwar.Config.TOGGLE_INVENTORY;
import static ru.m210projects.Tekwar.Config.TOGGLE_REARVIEW;
import static ru.m210projects.Tekwar.Config.TOGGLE_SCORE;
import static ru.m210projects.Tekwar.Config.TOGGLE_TIME;
import static ru.m210projects.Tekwar.Config.TOGGLE_UPRT;

import ru.m210projects.Build.Pattern.MenuItems.BuildMenu;
import ru.m210projects.Build.Pattern.MenuItems.MenuHandler;
import ru.m210projects.Build.Pattern.MenuItems.MenuItem;
import ru.m210projects.Build.Pattern.MenuItems.MenuProc;
import ru.m210projects.Build.Pattern.MenuItems.MenuSlider;
import ru.m210projects.Build.Pattern.MenuItems.MenuSwitch;
import ru.m210projects.Build.Pattern.MenuItems.MenuTitle;
import ru.m210projects.Tekwar.Config;
import ru.m210projects.Tekwar.Main;

public class MenuInterfaceSet extends BuildMenu {
	
	public MenuInterfaceSet(Main app)
	{
		MenuTitle Title = new MenuTitle(app.pEngine, "Interface setup", app.getFont(0), 160, 15, -1);
		Title.pal = 3;
		
		int pos = 25;
		final Config cfg = Main.tekcfg;
		
		MenuSwitch sShowMessages = new MenuSwitch("Show messages:", app.getFont(0), 86,
				pos += 10, 160, cfg.showMessages, new MenuProc() {
					public void run(MenuHandler handler, MenuItem pItem) {
						MenuSwitch sw = (MenuSwitch) pItem;
						cfg.showMessages = sw.value;
					}
				}, null, null);
		
		final MenuSlider sMouseSize = new MenuSlider(app.pSlider, "Mouse cur. size:", app.getFont(0), 86, pos += 10,
				160, cfg.gMouseCursorSize, 16384, 3*65536, 4096, new MenuProc() {
					public void run(MenuHandler handler, MenuItem pItem) {
						MenuSlider slider = (MenuSlider) pItem;
						cfg.gMouseCursorSize = slider.value;
					}
				}, false) {
		};

		MenuSwitch sShowCrosshair = new MenuSwitch("Show crosshair:", app.getFont(0), 86,
				pos += 15, 160, cfg.gCrosshair, new MenuProc() {
					public void run(MenuHandler handler, MenuItem pItem) {
						MenuSwitch sw = (MenuSwitch) pItem;
						cfg.gCrosshair = sw.value;
					}
				}, null, null);
		
		final MenuSlider sCrosshairSize = new MenuSlider(app.pSlider, "Crosshair size:", app.getFont(0), 86, pos += 10,
				160, cfg.gCrossSize, 16384, 3*65536, 4096, new MenuProc() {
					public void run(MenuHandler handler, MenuItem pItem) {
						MenuSlider slider = (MenuSlider) pItem;
						cfg.gCrossSize = slider.value;
					}
				}, false) {
		};
		
		final MenuSlider sHUDSize = new MenuSlider(app.pSlider, "HUD size:", app.getFont(0), 86, pos += 10,
				160, cfg.gHUDSize, 65536 / 4, 4*65536, 4096, new MenuProc() {
					public void run(MenuHandler handler, MenuItem pItem) {
						MenuSlider slider = (MenuSlider) pItem;
						cfg.gHUDSize = slider.value;
					}
				}, false) {
		};
		
		MenuSwitch sShowFps = new MenuSwitch("Show framerate:", app.getFont(2), 86,
				pos += 13, 160, cfg.gShowFPS, new MenuProc() {
					public void run(MenuHandler handler, MenuItem pItem) {
						MenuSwitch sw = (MenuSwitch) pItem;
						cfg.gShowFPS = sw.value;
					}
				}, null, null);
		
		MenuSwitch sShowHealth = new MenuSwitch("Show health:", app.getFont(2), 86,
				pos += 6, 160, cfg.toggles[TOGGLE_HEALTH], new MenuProc() {
					public void run(MenuHandler handler, MenuItem pItem) {
						MenuSwitch sw = (MenuSwitch) pItem;
						cfg.toggles[TOGGLE_HEALTH] = sw.value;
					}
				}, null, null);
		
		MenuSwitch sShowRear = new MenuSwitch("Show rearview:", app.getFont(2), 86,
				pos += 6, 160, cfg.toggles[TOGGLE_REARVIEW], new MenuProc() {
					public void run(MenuHandler handler, MenuItem pItem) {
						MenuSwitch sw = (MenuSwitch) pItem;
						cfg.toggles[TOGGLE_REARVIEW] = sw.value;
					}
				}, null, null);
		
		MenuSwitch sShowUPTR = new MenuSwitch("Show matrix meter:", app.getFont(2), 86,
				pos += 6, 160, cfg.toggles[TOGGLE_UPRT], new MenuProc() {
					public void run(MenuHandler handler, MenuItem pItem) {
						MenuSwitch sw = (MenuSwitch) pItem;
						cfg.toggles[TOGGLE_UPRT] = sw.value;
					}
				}, null, null);
		
		MenuSwitch sShowInv = new MenuSwitch("Show inventory:", app.getFont(2), 86,
				pos += 6, 160, cfg.toggles[TOGGLE_INVENTORY], new MenuProc() {
					public void run(MenuHandler handler, MenuItem pItem) {
						MenuSwitch sw = (MenuSwitch) pItem;
						cfg.toggles[TOGGLE_INVENTORY] = sw.value;
					}
				}, null, null);
		
		MenuSwitch sShowTime = new MenuSwitch("Show time:", app.getFont(2), 86,
				pos += 6, 160, cfg.toggles[TOGGLE_TIME], new MenuProc() {
					public void run(MenuHandler handler, MenuItem pItem) {
						MenuSwitch sw = (MenuSwitch) pItem;
						cfg.toggles[TOGGLE_TIME] = sw.value;
					}
				}, null, null);
		
		MenuSwitch sShowScore = new MenuSwitch("Show score:", app.getFont(2), 86,
				pos += 6, 160, cfg.toggles[TOGGLE_SCORE], new MenuProc() {
					public void run(MenuHandler handler, MenuItem pItem) {
						MenuSwitch sw = (MenuSwitch) pItem;
						cfg.toggles[TOGGLE_SCORE] = sw.value;
					}
				}, null, null);
		
		
		addItem(Title, false);
		addItem(sShowMessages, true);
		addItem(sMouseSize, false);
		addItem(sShowCrosshair, false);
		addItem(sCrosshairSize, false);
		addItem(sHUDSize, false);
		addItem(sShowFps, false);
		addItem(sShowHealth, false);
		addItem(sShowRear, false);
		addItem(sShowUPTR, false);
		addItem(sShowInv, false);
		addItem(sShowTime, false);
		addItem(sShowScore, false);
	}

}
