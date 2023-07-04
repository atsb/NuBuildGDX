package ru.m210projects.Witchaven.Menu;

import static ru.m210projects.Witchaven.Main.gGameScreen;
import static ru.m210projects.Witchaven.Globals.*;

import ru.m210projects.Build.Pattern.MenuItems.BuildMenu;
import ru.m210projects.Build.Pattern.MenuItems.MenuButton;
import ru.m210projects.Build.Pattern.MenuItems.MenuHandler;
import ru.m210projects.Build.Pattern.MenuItems.MenuItem;
import ru.m210projects.Build.Pattern.MenuItems.MenuProc;
import ru.m210projects.Witchaven.Main;

public class WHMenuNewGame extends BuildMenu {

	private int skills = 2;
	
	public WHMenuNewGame(final Main app)
	{
		final WHMenuUserContent content = new WHMenuUserContent(app);
		
		WHTitle mLogo = new WHTitle("New game", 90, 0);
		int pos = 70;
		WHSwitch mMenuDifficulty = new WHSwitch("Difficulty", app.getFont(0), 0, pos, skills, 4, new MenuProc() {
			@Override
			public void run( MenuHandler handler, MenuItem pItem ) {
				WHSwitch sw = (WHSwitch) pItem;
				skills = sw.nValue;
			}
		}) {
			@Override
			public void open() {
				this.nValue = skills;
			}
		};
		
		MenuProc newgame = new MenuProc() {
			@Override
			public void run( MenuHandler handler, MenuItem pItem ) {
				gGameScreen.newgame(gOriginalEpisode, 1, skills);
			}
		};
		MenuButton mStart = new MenuButton("Start game", app.getFont(0), 0, pos += 36, 320, 1, 0, null, -1, newgame, 0);
		MenuButton mUser = new MenuButton("User Content", app.getFont(0), 0, pos += 15, 320, 1, 0, content, -1, new MenuProc() {
			@Override
			public void run( MenuHandler handler, MenuItem pItem ) {
				content.skills = skills;
			}
		}, -1);

		addItem(mLogo, false);
		addItem(mMenuDifficulty, true);
		addItem(mStart, false);
		addItem(mUser, false);
	}
}
