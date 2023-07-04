package ru.m210projects.Witchaven.Menu;

import static ru.m210projects.Witchaven.Main.*;

import ru.m210projects.Build.Pattern.MenuItems.BuildMenu;
import ru.m210projects.Build.Pattern.MenuItems.MenuHandler;
import ru.m210projects.Build.Pattern.MenuItems.MenuItem;
import ru.m210projects.Build.Pattern.MenuItems.MenuProc;
import ru.m210projects.Build.Pattern.MenuItems.MenuSwitch;
import ru.m210projects.Witchaven.Main;

public class MenuGameSetup extends BuildMenu {

	public MenuGameSetup(Main app)
	{
		int pos = 0;
		WHTitle mLogo = new WHTitle("Game Setup", 90, pos);

		MenuSwitch sGore = new MenuSwitch("Gore mode:", app.getFont(0), 46, pos += 70, 240, whcfg.gGameGore, new MenuProc() {
			@Override
			public void run( MenuHandler handler, MenuItem pItem ) {
				MenuSwitch sw = (MenuSwitch) pItem;
				whcfg.gGameGore = sw.value;
			}
		}, null, null);
		if(!app.WH2)
			sGore.switchFont = app.getFont(1);
		
		MenuSwitch sShowCutscenes = new MenuSwitch("Cutscenes:", app.getFont(0), 46, pos += 15, 240, whcfg.showCutscenes,
			new MenuProc() {
				@Override
				public void run( MenuHandler handler, MenuItem pItem ) {
					MenuSwitch sw = (MenuSwitch) pItem;
					whcfg.showCutscenes = sw.value;
				}
			}, null, null);
		if(!app.WH2)
			sShowCutscenes.switchFont = app.getFont(1);
		
		MenuSwitch sStartup = new MenuSwitch("Startup window:", app.getFont(0), 46, pos += 15, 240, whcfg.startup, new MenuProc() {
			@Override
			public void run( MenuHandler handler, MenuItem pItem ) {
				MenuSwitch sw = (MenuSwitch) pItem;
				whcfg.startup = sw.value;
			}
		}, null, null);
		if(!app.WH2)
		sStartup.switchFont = app.getFont(1);

		MenuSwitch sCheckVersion = new MenuSwitch("Check for updates:", app.getFont(0), 46, pos += 15, 240, whcfg.checkVersion,
				new MenuProc() {
					@Override
					public void run( MenuHandler handler, MenuItem pItem ) {
						MenuSwitch sw = (MenuSwitch) pItem;
						whcfg.checkVersion = sw.value;
					}
				}, null, null);
		if(!app.WH2)
		sCheckVersion.switchFont = app.getFont(1);

		addItem(mLogo, false);
		addItem(sGore, true);
		addItem(sShowCutscenes, false);
		addItem(sStartup, false);
		addItem(sCheckVersion, false);
	}
}
