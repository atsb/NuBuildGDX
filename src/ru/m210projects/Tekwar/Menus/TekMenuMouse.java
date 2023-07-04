package ru.m210projects.Tekwar.Menus;

import ru.m210projects.Build.Pattern.BuildGame;
import ru.m210projects.Build.Pattern.CommonMenus.MenuMouse;
import ru.m210projects.Build.Pattern.MenuItems.MenuTitle;

public class TekMenuMouse extends MenuMouse {

	public TekMenuMouse(BuildGame app) {
		super(app, 40, 20, 240, app.getFont(0).getHeight() + 4, app.getFont(0).getHeight(), app.getFont(0), 0);
	}

	@Override
	public MenuTitle getTitle(BuildGame app, String text) {
		MenuTitle title = new MenuTitle(app.pEngine, text, app.getFont(0), 160, 15, -1);
		title.pal = 3;
		
		return title;
	}

}
