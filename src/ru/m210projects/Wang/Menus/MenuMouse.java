package ru.m210projects.Wang.Menus;

import ru.m210projects.Wang.Main;
import ru.m210projects.Build.Pattern.BuildGame;
import ru.m210projects.Build.Pattern.MenuItems.MenuTitle;

public class MenuMouse extends ru.m210projects.Build.Pattern.CommonMenus.MenuMouse {

	public MenuMouse(Main app) {
		super(app, 35, 40, 250, 10, 5, app.getFont(1), 0);
		
		mMove.text = "Forw/Backw speed".toCharArray();
		mAdvance.align = 0;
		mAdvance.x = 35;
	}

	@Override
	public MenuTitle getTitle(BuildGame app, String text) {
		return new WangTitle(text);
	}

}
