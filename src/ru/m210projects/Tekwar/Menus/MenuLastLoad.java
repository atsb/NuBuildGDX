package ru.m210projects.Tekwar.Menus;

import static ru.m210projects.Tekwar.Tekldsv.*;
import static ru.m210projects.Tekwar.Main.*;

import ru.m210projects.Build.Pattern.MenuItems.BuildMenu;
import ru.m210projects.Build.Pattern.MenuItems.MenuHandler;
import ru.m210projects.Build.Pattern.MenuItems.MenuText;
import ru.m210projects.Build.Pattern.MenuItems.MenuVariants;
import ru.m210projects.Tekwar.Main;

public class MenuLastLoad extends BuildMenu {

	public MenuLastLoad(final Main game) {
		MenuText QuitQuestion = new MenuText("Load Saved game?", game.getFont(0), 160, 90, 1);
		QuitQuestion.pal = 3;
		addItem(QuitQuestion, false);
		
		MenuVariants question = new MenuVariants(game.pEngine, "[Y/N]",
				game.getFont(0), 160, 105) {
			@Override
			public void positive(MenuHandler menu) {
				game.changeScreen(gLoadingScreen.setTitle(lastload));
				gLoadingScreen.init(new Runnable() {
					public void run() {
						if(!loadgame(lastload)) {
							game.GameMessage("Can't load saved game!");
							game.show();
						}
					}
				});
				menu.mClose();
			}
			
			@Override
			public void negative(MenuHandler menu)
			{
				menu.mClose();
			}
		};
		question.pal = 3;

		addItem(question, true);
	}
}
