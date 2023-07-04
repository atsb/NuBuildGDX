package ru.m210projects.Witchaven.Menu;

import static ru.m210projects.Witchaven.Main.gGameScreen;
import static ru.m210projects.Witchaven.Factory.WHMenuHandler.*;
import static ru.m210projects.Witchaven.Globals.*;
import static ru.m210projects.Build.Net.Mmulti.*;

import ru.m210projects.Build.Pattern.MenuItems.BuildMenu;
import ru.m210projects.Build.Pattern.MenuItems.MenuButton;
import ru.m210projects.Build.Pattern.MenuItems.MenuHandler;
import ru.m210projects.Build.Pattern.MenuItems.MenuItem;
import ru.m210projects.Build.Pattern.MenuItems.MenuPicnum;
import ru.m210projects.Build.Pattern.MenuItems.MenuProc;
import ru.m210projects.Witchaven.Main;
import ru.m210projects.Witchaven.Factory.WHMenuHandler;

public class GameMenu extends BuildMenu {

	public GameMenu(final Main app)
	{
		WHMenuHandler menu = (WHMenuHandler) app.pMenu;

		int pos = 0;
		MenuPicnum mLogo = new MenuPicnum(app.pEngine, 90, pos, app.WH2 ? WH2LOGO : WHLOGO, app.WH2 ? WH2LOGO : WHLOGO, 0x4000);

		MenuButton bNewgame = new MenuButton("New Game", app.getFont(0), 0, pos += 50, 320, 1, 0, menu.mMenus[DIFFICULTY], -1, null, 0);
		MenuButton bOptions = new MenuButton("Options", app.getFont(0), 0, pos += 15, 320, 1, 0, menu.mMenus[OPTIONS], -1, null, 0);
		MenuButton bLoad = new MenuButton("Load Game", app.getFont(0), 0, pos += 15, 320, 1, 0, menu.mMenus[LOADGAME], -1, null, 0) {
			@Override
			public void open() {
				mCheckEnableItem(numplayers < 2);
			}
		};
		
		MenuProc mScreenCapture = new MenuProc() {
			@Override
			public void run( MenuHandler handler, MenuItem pItem ) {
				gGameScreen.capture(160, 100);
			}
		};
		
		MenuButton bSave = new MenuButton("Save Game", app.getFont(0), 0, pos += 15, 320, 1, 0, menu.mMenus[SAVEGAME] = new WHMenuSave(app), -1, mScreenCapture, 0) {
			@Override
			public void open() {
				mCheckEnableItem(numplayers < 2);
			}
		};
		
		MenuButton bTitle = new MenuButton("Quit to title", app.getFont(0), 0, pos += 15, 320, 1, 0, new WHMenuQTitle(app), -1, null, 0);
		MenuButton bQuit = new MenuButton("Quit", app.getFont(0), 0, pos += 15, 320, 1, 0, menu.mMenus[QUIT], -1, null, 0);
		
		addItem(mLogo, false);
		addItem(bNewgame, true);
		addItem(bOptions, false);
		addItem(bLoad, false);
		addItem(bSave, false);
		addItem(bTitle, false);
		addItem(bQuit, false);
	}
}
