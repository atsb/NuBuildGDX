package ru.m210projects.Wang.Menus;

import static ru.m210projects.Build.Engine.xdim;
import static ru.m210projects.Build.Engine.ydim;
import static ru.m210projects.Build.Net.Mmulti.numplayers;
import static ru.m210projects.Wang.Factory.WangMenuHandler.LOADGAME;
import static ru.m210projects.Wang.Factory.WangMenuHandler.NETWORKGAME;
import static ru.m210projects.Wang.Factory.WangMenuHandler.NEWGAME;
import static ru.m210projects.Wang.Factory.WangMenuHandler.OPTIONS;
import static ru.m210projects.Wang.Factory.WangMenuHandler.QUIT;
import static ru.m210projects.Wang.Factory.WangMenuHandler.QUITTITLE;
import static ru.m210projects.Wang.Factory.WangMenuHandler.SAVEGAME;
import static ru.m210projects.Wang.Main.gDemoScreen;
import static ru.m210projects.Wang.Main.gGameScreen;
import static ru.m210projects.Wang.Main.game;

import ru.m210projects.Build.Pattern.MenuItems.BuildMenu;
import ru.m210projects.Build.Pattern.MenuItems.MenuButton;
import ru.m210projects.Build.Pattern.MenuItems.MenuHandler;
import ru.m210projects.Build.Pattern.MenuItems.MenuItem;
import ru.m210projects.Build.Pattern.MenuItems.MenuPicnum;
import ru.m210projects.Build.Pattern.MenuItems.MenuProc;
import ru.m210projects.Wang.Main;
import ru.m210projects.Wang.Factory.WangMenuHandler;

public class MenuGame extends BuildMenu {

	public MenuGame(final Main app)
	{
		final WangMenuHandler menu = (WangMenuHandler) app.menu;
		
		MenuPicnum bLogo = new MenuPicnum(app.pEngine, 160, 15, 2366, 2366, 65536) {
			@Override
			public void draw(MenuHandler handler) {
				draw.rotatesprite(x << 16, y << 16, 65536, 0, nTile, 0, 0, 10, 0, 0, xdim - 1, ydim - 1);
			}
		};
		
		int posy = 30;
		
		MenuButton NewGame = new MenuButton("New Game", app.getFont(2), 55, posy += 16, 320, 0, 0, menu.mMenus[NEWGAME], -1, null, 0) {
			@Override
			public void open() {
				nextMenu = menu.mMenus[NEWGAME];
				if (numplayers > 1 || game.net.FakeMultiplayer)
					nextMenu = menu.mMenus[NETWORKGAME];
			}
		};
		MenuButton Load = new MenuButton("Load Game", app.getFont(2), 55, posy += 16, 320, 0, 0, menu.mMenus[LOADGAME], -1, null, 0) {
			@Override
			public void draw(MenuHandler handler) {
				mCheckEnableItem(app.isCurrentScreen(gDemoScreen) || numplayers < 2 && !game.net.FakeMultiplayer);
				super.draw(handler);
			}
		};
		
		MenuProc mScreenCapture = new MenuProc() {
			@Override
			public void run( MenuHandler handler, MenuItem pItem ) {
				gGameScreen.capture(160, 100);
			}
		};
		MenuButton Save = new MenuButton("Save Game", app.getFont(2), 55, posy += 16, 320, 0, 0, menu.mMenus[SAVEGAME], -1, mScreenCapture, 0) {
			@Override
			public void draw(MenuHandler handler) {
				mCheckEnableItem(app.isCurrentScreen(gDemoScreen) || numplayers < 2 && !game.net.FakeMultiplayer);
				super.draw(handler);
			}
		};
		
		MenuButton Options = new MenuButton("Options", app.getFont(2), 55, posy += 16, 320, 0, 0, menu.mMenus[OPTIONS], -1, null, 0);
	
		MenuButton End = new MenuButton("End game", app.getFont(2), 55, posy += 16, 320, 0, 0, menu.mMenus[QUITTITLE], -1, null, 0);
		
		MenuButton Quit = new MenuButton("Quit", app.getFont(2), 55, posy += 16, 320, 0, 0, menu.mMenus[QUIT], -1, null, 0);
	
		addItem(bLogo, false);
		addItem(NewGame, true);
		addItem(Load, false);
		addItem(Save, false);
		addItem(Options, false);
		addItem(End, false);
		addItem(Quit, false);
	}
	
}
