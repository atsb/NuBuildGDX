package ru.m210projects.Wang.Menus;

import static ru.m210projects.Build.Net.Mmulti.myconnectindex;
import static ru.m210projects.Build.Net.Mmulti.numplayers;
import static ru.m210projects.Wang.Main.gDemoScreen;

import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.Pattern.MenuItems.BuildMenu;
import ru.m210projects.Build.Pattern.MenuItems.MenuHandler;
import ru.m210projects.Build.Pattern.MenuItems.MenuText;
import ru.m210projects.Build.Pattern.MenuItems.MenuVariants;
import ru.m210projects.Wang.Main;

public class MenuQuit extends BuildMenu {

	public MenuQuit(final Main game, final boolean toTitle) {
		if(toTitle) {
			MenuText QuitQuestion = new MenuText("QUIT TO TITLE?", game.getFont(1), 160, 90, 1);
			QuitQuestion.pal = 4;
			addItem(QuitQuestion, false);
		}
		
		MenuVariants question = new MenuVariants(game.pEngine, toTitle ? "[Y/N]" : "PRESS [Y] TO QUIT, [N] TO FIGHT ON.",
				game.getFont(1), 160, 105) {
			@Override
			public void positive(MenuHandler menu) {
				if(!toTitle) {
					if (numplayers > 1) {
						BuildGdx.app.postRunnable(new Runnable() {
							@Override
							public void run() {
								game.pNet.NetDisconnect(myconnectindex);
							}
						});
					}
					game.gExit = true;
				} else {
					if (!game.isCurrentScreen(gDemoScreen) && (numplayers > 1 || game.net.FakeMultiplayer)) {
						BuildGdx.app.postRunnable(new Runnable() {
							@Override
							public void run() {
								game.net.NetDisconnect(myconnectindex);
							}
						});
					} else game.show();
				}
				menu.mClose();
			}
		};
		question.pal = 4;

		addItem(question, true);
	}
}
