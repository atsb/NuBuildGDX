package ru.m210projects.Tekwar.Menus;

import static ru.m210projects.Tekwar.Main.*;
import ru.m210projects.Build.Pattern.BuildGame;
import ru.m210projects.Build.Pattern.MenuItems.BuildMenu;
import ru.m210projects.Build.Pattern.MenuItems.MenuHandler;
import ru.m210projects.Build.Pattern.MenuItems.MenuText;
import ru.m210projects.Build.Pattern.MenuItems.MenuTitle;
import ru.m210projects.Build.Pattern.MenuItems.MenuVariants;

public class MenuQuit extends BuildMenu {
	
	public MenuQuit(final BuildGame game)
	{
		MenuTitle QuitTitle = new MenuTitle(game.pEngine, "Quit game", game.getFont(0), 160, 15, -1);
		QuitTitle.pal = 3;
		MenuText QuitQuestion = new MenuText("Do you really want to quit?", game.getFont(0), 160, 50, 1);
		QuitQuestion.pal = 3;
		MenuVariants QuitVariants = new MenuVariants(game.pEngine, "[Y/N]", game.getFont(0), 160, 60) {
			@Override
			public void positive(MenuHandler menu) {
				game.changeScreen(gCreditsScreen);
				menu.mClose();
			}
		};
		QuitVariants.pal = 3;
		addItem(QuitTitle, false);
		addItem(QuitQuestion, false);
		addItem(QuitVariants, true);
	}
}
