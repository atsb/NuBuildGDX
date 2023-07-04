package ru.m210projects.Witchaven.Menu;

import ru.m210projects.Build.Pattern.BuildGame;
import ru.m210projects.Build.Pattern.MenuItems.BuildMenu;
import ru.m210projects.Build.Pattern.MenuItems.MenuHandler;
import ru.m210projects.Build.Pattern.MenuItems.MenuText;
import ru.m210projects.Build.Pattern.MenuItems.MenuVariants;

public class MenuQuit extends BuildMenu {
	
	public MenuQuit(final BuildGame game)
	{
		
		MenuText QuitQuestion = new MenuText("Are you sure?", game.getFont(0), 160, 85, 1);
		MenuVariants QuitVariants = new MenuVariants(game.pEngine, "[Y/N]", game.getFont(0), 160, 102) {
			@Override
			public void positive(MenuHandler menu) {
				game.gExit = true;
				menu.mClose();
			}
		};

		addItem(QuitQuestion, false);
		addItem(QuitVariants, true);
	}
}
