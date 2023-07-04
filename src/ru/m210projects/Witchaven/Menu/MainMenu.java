package ru.m210projects.Witchaven.Menu;

import static ru.m210projects.Witchaven.Factory.WHMenuHandler.*;

import ru.m210projects.Build.Pattern.MenuItems.BuildMenu;
import ru.m210projects.Build.Pattern.MenuItems.MenuButton;
import ru.m210projects.Witchaven.Main;
import ru.m210projects.Witchaven.Factory.WHMenuHandler;

public class MainMenu extends BuildMenu {

	public MainMenu(final Main app)
	{
		WHMenuHandler menu = (WHMenuHandler) app.pMenu;
		
		if(app.WH2) {
			int posx = 0;
			int posy = 30;
			
			MenuButton bNewgame = new MenuButton("New Game", app.getFont(0), posx, posy += 20, 320, 1, 0, menu.mMenus[DIFFICULTY] = new WHMenuNewGame(app), -1, null, 0);
			MenuButton bMultiplayer = new MenuButton("Multiplayer", app.getFont(0), posx, posy += 14, 320, 1, 0, null, -1, null, 0) {
				@Override
				public void open() {
					this.mCheckEnableItem(false);
				}
			};
			MenuButton bOptions = new MenuButton("Options", app.getFont(0), posx, posy += 14, 320, 1, 0, menu.mMenus[OPTIONS] = new MenuOptions(app), -1, null, 0);
			MenuButton bLoad = new MenuButton("Load Game", app.getFont(0), posx, posy += 14, 320, 1, 0, menu.mMenus[LOADGAME] = new WHMenuLoad(app), -1, null, 0);
			MenuButton bQuit = new MenuButton("Quit", app.getFont(0), posx, posy += 14, 320, 1, 0, menu.mMenus[QUIT] = new MenuQuit(app), -1, null, 0);

			addItem(bNewgame, true);
			addItem(bMultiplayer, false);
			addItem(bOptions, false);
			addItem(bLoad, false);
			addItem(bQuit, false);
		} else {
		
			int posx = 70;
			int posy = 10;
			
			MenuButton bNewgame = new MenuButton("New Game", app.getFont(0), posx, posy += 20, 320, 0, 0, menu.mMenus[DIFFICULTY] = new WHMenuNewGame(app), -1, null, 0);
			MenuButton bMultiplayer = new MenuButton("Multiplayer", app.getFont(0), posx - 10, posy += 14, 320, 0, 0, null, -1, null, 0) {
				@Override
				public void open() {
					this.mCheckEnableItem(false);
				}
			};
			MenuButton bOptions = new MenuButton("Options", app.getFont(0), posx + 12, posy += 14, 320, 0, 0, menu.mMenus[OPTIONS] = new MenuOptions(app), -1, null, 0);
			MenuButton bLoad = new MenuButton("Load Game", app.getFont(0), posx - 2, posy += 14, 320, 0, 0, menu.mMenus[LOADGAME] = new WHMenuLoad(app), -1, null, 0);
			MenuButton bQuit = new MenuButton("Quit", app.getFont(0), posx + 25, posy += 14, 320, 0, 0, menu.mMenus[QUIT] = new MenuQuit(app), -1, null, 0);
	
			addItem(bNewgame, true);
			addItem(bMultiplayer, false);
			addItem(bOptions, false);
			addItem(bLoad, false);
			addItem(bQuit, false);
		}
	}
}
