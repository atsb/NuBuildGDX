package ru.m210projects.Wang.Menus;

import static ru.m210projects.Build.Net.Mmulti.myconnectindex;
import static ru.m210projects.Wang.Game.Player;
import static ru.m210projects.Wang.LoadSave.lastload;
import static ru.m210projects.Wang.LoadSave.loadgame;
import static ru.m210projects.Wang.Main.gLoadingScreen;
import static ru.m210projects.Wang.Player.DoPlayerDeathRestart;

import ru.m210projects.Build.Pattern.MenuItems.BuildMenu;
import ru.m210projects.Build.Pattern.MenuItems.MenuHandler;
import ru.m210projects.Build.Pattern.MenuItems.MenuText;
import ru.m210projects.Build.Pattern.MenuItems.MenuVariants;
import ru.m210projects.Wang.Main;

public class MenuLastLoad extends BuildMenu {

	public MenuLastLoad(final Main game) {
		MenuText QuitQuestion = new MenuText("Load Saved game?", game.getFont(1), 160, 90, 1);
		QuitQuestion.pal = 4;
		addItem(QuitQuestion, false);

		MenuVariants question = new MenuVariants(game.pEngine, "[Y/N]", game.getFont(1), 160, 105) {
			@Override
			public void positive(MenuHandler menu) {
				game.changeScreen(gLoadingScreen.setTitle(lastload));
				gLoadingScreen.init(new Runnable() {
					public void run() {
						if (!loadgame(lastload)) {
							game.GameMessage("Can't load saved game!");
							game.show();
						}
					}
				});
				menu.mClose();
			}

			@Override
			public void negative(MenuHandler menu) {

                DoPlayerDeathRestart(Player[myconnectindex]);
				menu.mClose();
			}
		};
		question.pal = 4;

		addItem(question, true);
	}
}
