package ru.m210projects.Wang.Menus.Network;

import ru.m210projects.Build.Pattern.MenuItems.BuildMenu;
import ru.m210projects.Build.Pattern.MenuItems.MenuButton;
import ru.m210projects.Wang.Main;
import ru.m210projects.Wang.Menus.WangTitle;

public class MenuMultiplayer extends BuildMenu {

	public MenuMultiplayer(Main app) {
		int pos = 45;
		
		addItem(new WangTitle("Multiplayer"), false);
		addItem(new MenuButton("New game", app.getFont(2), 55, pos += 17, 240, 0, 0, new MenuCreate(app), -1, null, 0), true);
		addItem(new MenuButton("Join a game", app.getFont(2), 55, pos += 17, 240, 0, 0, new MenuJoin(app), -1, null, 0), false);
	}

}
