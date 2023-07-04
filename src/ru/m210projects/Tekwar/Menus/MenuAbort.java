package ru.m210projects.Tekwar.Menus;

import ru.m210projects.Build.Pattern.BuildGame;
import ru.m210projects.Build.Pattern.MenuItems.BuildMenu;
import ru.m210projects.Build.Pattern.MenuItems.MenuHandler;
import ru.m210projects.Build.Pattern.MenuItems.MenuText;
import ru.m210projects.Build.Pattern.MenuItems.MenuVariants;

import static ru.m210projects.Tekwar.Tekmap.*;

public class MenuAbort extends BuildMenu {

	public MenuAbort(final BuildGame game)
	{
		MenuText QuitQuestion = new MenuText("Abort mission?", game.getFont(0), 160, 50, 1);
		QuitQuestion.pal = 3;
		MenuVariants QuitVariants = new MenuVariants(game.pEngine, "[Y/N]", game.getFont(0), 160, 60) {
			@Override
			public void positive(MenuHandler menu) {
				gameover = 1;
				game.pInput.ctrlResetKeyStatus();
				menu.mClose();
			}
		};
		QuitVariants.pal = 3;

		addItem(QuitQuestion, false);
		addItem(QuitVariants, true);
	}
}
