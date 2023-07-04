package ru.m210projects.Wang.Menus;

import static ru.m210projects.Wang.Factory.WangMenuHandler.*;

import ru.m210projects.Build.Pattern.MenuItems.BuildMenu;
import ru.m210projects.Build.Pattern.MenuItems.MenuButton;
import ru.m210projects.Wang.Main;
import ru.m210projects.Wang.Factory.WangMenuHandler;

public class MenuOptions extends BuildMenu {

	public MenuOptions(Main app)
	{
		final WangMenuHandler menu = (WangMenuHandler) app.menu;
		int pos = 30;
		
		addItem(new WangTitle("Options"), false);
		addItem(new MenuButton("GAME SETUP", app.getFont(2), 55, pos += 16, 320, 0, 0, new MenuGameSetup(app), 1, null, 0), true);
		addItem(new MenuButton("INTERFACE SETUP", app.getFont(2), 55, pos += 16, 320, 0, 0, new MenuInterface(app), 1, null, 0), false);
		addItem(new MenuButton("AUDIO SETUP", app.getFont(2), 55, pos += 16, 320, 0, 0, menu.mMenus[SOUNDSET], 1, null, 0), false);
		addItem(new MenuButton("VIDEO SETUP", app.getFont(2), 55, pos += 16, 320, 0, 0, new MenuVideoMode(app), 1, null, 0), false);
		addItem(new MenuButton("CONTROL SETUP", app.getFont(2), 55, pos += 16, 320, 0, 0,  new MenuControls(app), 1, null, 0), false);
	}
}
