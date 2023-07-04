package ru.m210projects.Tekwar.Menus;

import static ru.m210projects.Tekwar.Factory.TekMenuHandler.GAME;
import static ru.m210projects.Tekwar.Factory.TekMenuHandler.LOADGAME;
import static ru.m210projects.Tekwar.Factory.TekMenuHandler.OPTIONS;
import static ru.m210projects.Tekwar.Factory.TekMenuHandler.QUIT;
import static ru.m210projects.Tekwar.Factory.TekMenuHandler.SAVEGAME;
import static ru.m210projects.Tekwar.Main.gGameScreen;
import static ru.m210projects.Tekwar.Main.mUserFlag;
import static ru.m210projects.Tekwar.Tekmap.mission;

import ru.m210projects.Build.Pattern.MenuItems.BuildMenu;
import ru.m210projects.Build.Pattern.MenuItems.MenuButton;
import ru.m210projects.Build.Pattern.MenuItems.MenuHandler;
import ru.m210projects.Build.Pattern.MenuItems.MenuItem;
import ru.m210projects.Build.Pattern.MenuItems.MenuProc;
import ru.m210projects.Build.Pattern.MenuItems.MenuTitle;
import ru.m210projects.Tekwar.Main;
import ru.m210projects.Tekwar.Main.UserFlag;
import ru.m210projects.Tekwar.Factory.TekMenuHandler;

public class MainMenu extends BuildMenu {
	
	public MainMenu(Main app)
	{
		MenuProc mScreenCapture = new MenuProc() {
			@Override
			public void run(MenuHandler handler, MenuItem pItem) {
				gGameScreen.capture(160, 100);
			}
		};
		
		MenuTitle title = new MenuTitle(app.pEngine, "Tekwar menu", app.getFont(0), 160, 15, -1);	
		
		TekMenuHandler menu = (TekMenuHandler) app.pMenu;
		int pos = 35;

		MenuButton NewGame = new MenuButton("New Game", app.getFont(0), 0, pos += 10, 320, 1, 2, menu.mMenus[GAME] =  new MenuGame(app), -1, null, 0) {
			@Override
			public void open() {
				if(mUserFlag != UserFlag.UserMap)
					text = "New Game".toCharArray();
				else text = "Restart".toCharArray();
			}
		};
		
		MenuButton UserGame = new MenuButton("User content", app.getFont(0), 0, pos += 10, 320, 1, 2, new MenuUserContent(app), -1, null, 0);
		MenuButton Options = new MenuButton("Options", app.getFont(0), 0, pos += 10, 320, 1, 2,  menu.mMenus[OPTIONS] = new MenuOptions(app), -1, null, 0);
		MenuButton Load = new MenuButton("Load Game", app.getFont(0), 0, pos += 10, 320, 1, 2, menu.mMenus[LOADGAME] = new TekMenuLoad(app), -1, null, 0);
		MenuButton Save = new MenuButton("Save Game", app.getFont(0), 0, pos += 10, 320, 1, 2, menu.mMenus[SAVEGAME] = new TekMenuSave(app), -1, mScreenCapture, 0);
		MenuButton Abort = new MenuButton("Abort", app.getFont(0), 0, pos += 10, 320, 1, 2, new MenuAbort(app), -1, null, 0) {
			@Override
			public void draw(MenuHandler handler) {
				super.draw(handler);
				mCheckEnableItem(mission != 9);
			}
		};
		MenuButton Quit = new MenuButton("Quit", app.getFont(0), 0, pos += 10, 320, 1, 2, menu.mMenus[QUIT] = new MenuQuit(app), -1, null, 0);

		addItem(title, false);
		addItem(NewGame, true);
		addItem(UserGame, false);
		addItem(Options, false);
		addItem(Load, false);
		addItem(Save, false);
		addItem(Abort, false);
		addItem(Quit, false);
	}

}
