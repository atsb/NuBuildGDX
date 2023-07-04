package ru.m210projects.Powerslave.Menus;

import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.Pattern.BuildFont.TextAlign;
import ru.m210projects.Build.Pattern.MenuItems.BuildMenu;
import ru.m210projects.Build.Pattern.MenuItems.MenuHandler;
import ru.m210projects.Build.Pattern.MenuItems.MenuText;
import ru.m210projects.Build.Pattern.MenuItems.MenuVariants;
import ru.m210projects.Powerslave.Main;

public class MenuCorruptGame extends BuildMenu {

	private Runnable runnable;
	public MenuCorruptGame(final Main game) {
		MenuText QuitQuestion = new MenuText(
				"The saved game is incompatible",
				game.getFont(0), 160, 90, 1) {
			@Override
			public void draw(MenuHandler handler) {
				super.draw(handler);
				font.drawText(160, y + 10, "but can be loaded at the level start.", -128, pal, TextAlign.Center, 2, fontShadow);
				font.drawText(160, y + 20, "Do you want to load?", -128, pal, TextAlign.Center, 2, fontShadow);
			}
		};
		addItem(QuitQuestion, false);

		MenuVariants question = new MenuVariants(game.pEngine, "[Y/N]", game.getFont(0), 160, 130) {
			@Override
			public void positive(MenuHandler menu) {
				if(runnable != null)
					BuildGdx.app.postRunnable(runnable);
				menu.mClose();
			}
		};
		
		QuitQuestion.fontShadow = QuitQuestion.fontShadow = true;

		addItem(question, true);
	}

	public void setRunnable(Runnable run) {
		this.runnable = run;
	}
}
