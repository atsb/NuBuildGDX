package ru.m210projects.Witchaven.Menu;

import static ru.m210projects.Witchaven.Main.*;
import static ru.m210projects.Witchaven.WHSND.*;
import static ru.m210projects.Witchaven.Globals.*;

import ru.m210projects.Build.Pattern.BuildGame;
import ru.m210projects.Build.Pattern.MenuItems.BuildMenu;
import ru.m210projects.Build.Pattern.MenuItems.MenuHandler;
import ru.m210projects.Build.Pattern.MenuItems.MenuText;
import ru.m210projects.Build.Pattern.MenuItems.MenuVariants;

public class WHMenuQTitle extends BuildMenu {

	public WHMenuQTitle(final BuildGame game)
	{
		MenuText QuitQuestion = new MenuText("Quit to title?", game.getFont(0), 160, 85, 1);
		MenuVariants QuitVariants = new MenuVariants(game.pEngine, "[Y/N]", game.getFont(0), 160, 102) {
			@Override
			public void positive(MenuHandler menu) {
				menu.mClose();
		
				game.pNet.ready2send = false;
				boardfilename = null;
				sndStopMusic();
				stopallsounds();
				
				game.pInput.ctrlResetKeyStatus();
				game.changeScreen(gMenuScreen);
			}
		};

		addItem(QuitQuestion, false);
		addItem(QuitVariants, true);
	}
}
