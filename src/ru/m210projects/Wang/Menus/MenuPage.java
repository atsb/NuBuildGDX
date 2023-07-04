package ru.m210projects.Wang.Menus;

import static ru.m210projects.Build.Engine.xdim;
import static ru.m210projects.Build.Engine.ydim;
import static ru.m210projects.Wang.Main.engine;

import ru.m210projects.Build.Pattern.MenuItems.MenuHandler;
import ru.m210projects.Build.Pattern.MenuItems.MenuTitle;
import ru.m210projects.Build.Pattern.MenuItems.MenuHandler.MenuOpt;

public class MenuPage extends MenuTitle {
	
	public MenuPage(int x, int y, int nTile) {
		super(engine, null, null, x, y, nTile);
	}
	
	@Override
	public void draw(MenuHandler handler) {
		engine.rotatesprite(x << 16, y << 16, 65536, 0, nTile, -128, 0, 10 | 16, 0, 0, xdim - 1, ydim - 1);
	}
	
	@Override
	public boolean callback(MenuHandler handler, MenuOpt opt) {
		switch(opt) {
		case LEFT:
		case BSPACE:
		case RMB:
			m_pMenu.mNavUp();
			return false;
		case RIGHT:
		case ENTER:
		case SPACE:
		case LMB:
			m_pMenu.mNavDown();
			return false;
		case UP:
		case DW:
		case ESC:
		case DELETE:
		    return m_pMenu.mNavigation(opt);
		default:
			return false;
		}
	}
}
