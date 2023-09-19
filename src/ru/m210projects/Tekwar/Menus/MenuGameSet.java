package ru.m210projects.Tekwar.Menus;

import ru.m210projects.Build.Pattern.MenuItems.BuildMenu;
import ru.m210projects.Build.Pattern.MenuItems.MenuConteiner;
import ru.m210projects.Build.Pattern.MenuItems.MenuHandler;
import ru.m210projects.Build.Pattern.MenuItems.MenuItem;
import ru.m210projects.Build.Pattern.MenuItems.MenuProc;
import ru.m210projects.Build.Pattern.MenuItems.MenuSwitch;
import ru.m210projects.Build.Pattern.MenuItems.MenuTitle;
import ru.m210projects.Tekwar.Config;
import ru.m210projects.Tekwar.Main;

public class MenuGameSet extends BuildMenu {

	public MenuGameSet(Main app)
	{
		MenuTitle Title = new MenuTitle(app.pEngine, "Game setup", app.getFont(0), 160, 15, -1);
		int pos = 35;
		final Config cfg = Main.tekcfg;
		
		int x = 45;
		int width = 240;
		
		MenuSwitch sStartup = new MenuSwitch("Startup window:", app.getFont(0), x,
				pos += 10, width, cfg.startup, new MenuProc() {
					
					public void run(MenuHandler handler, MenuItem pItem) {
						MenuSwitch sw = (MenuSwitch) pItem;
						cfg.startup = sw.value;
					}
				}, null, null);
		sStartup.pal = 2;

		

		MenuSwitch sShowCutscenes = new MenuSwitch("Show cutscenes:", app.getFont(0), x,
				pos += 10, width, cfg.showCutscenes, new MenuProc() {
					
					public void run(MenuHandler handler, MenuItem pItem) {
						MenuSwitch sw = (MenuSwitch) pItem;
						cfg.showCutscenes = sw.value;
					}
				}, null, null);
		sShowCutscenes.pal = 2;
		
		MenuSwitch mHead = new MenuSwitch("Head bob:", app.getFont(0), x, pos += 10, width, cfg.gHeadBob, new MenuProc() {
			public void run(MenuHandler handler, MenuItem pItem) {
				MenuSwitch sw = (MenuSwitch) pItem;
				cfg.gHeadBob = sw.value;
			}
		}, null, null);
		mHead.pal = 2;
		
		MenuConteiner sOverlay = new MenuConteiner("Overlay map:", app.getFont(0), x, pos += 10, width, null, 0, new MenuProc() {
			@Override
			public void run(MenuHandler handler, MenuItem pItem) {
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
		sOverlay.pal = 2;
		
		MenuSwitch saveguns = new MenuSwitch("Save weapons in next map:", app.getFont(0), x,
				pos += 10, width, cfg.gSaveWeapons, new MenuProc() {
					
					public void run(MenuHandler handler, MenuItem pItem) {
						MenuSwitch sw = (MenuSwitch) pItem;
						cfg.gSaveWeapons = sw.value;
					}
				}, "Yes", "No");
		saveguns.pal = 2;
		
		addItem(Title, false);
	
		addItem(sStartup, true);
		addItem(sShowCutscenes, false);
		addItem(mHead, false);
		addItem(sOverlay, false);
		addItem(saveguns, false);
	}
}
