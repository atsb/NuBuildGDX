package ru.m210projects.Tekwar.Menus;

import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Tekwar.Names.*;
import static ru.m210projects.Build.Pragmas.*;
import ru.m210projects.Build.Pattern.MenuItems.BuildMenu;
import ru.m210projects.Build.Pattern.MenuItems.MenuHandler;
import ru.m210projects.Build.Pattern.MenuItems.MenuTitle;
import ru.m210projects.Build.Types.Tile;
import ru.m210projects.Build.Pattern.MenuItems.MenuHandler.MenuOpt;
import ru.m210projects.Tekwar.Main;

public class MenuHelp extends BuildMenu {

	public MenuHelp(Main app)
	{
		MenuTitle mTitle = new MenuTitle(app.pEngine, null, null, 0, 0, -1) {
			@Override
			public void draw(MenuHandler handler) {
				Tile pic = draw.getTile(BACKGROUND);

				int frames = xdim / pic.getWidth();
			    int x = 0;
			    for(int i = 0; i <= frames; i++) {
			    	draw.rotatesprite(x<<16, 0, 0x10000, 0, BACKGROUND, 0, 0, 2+8+16+256, 0, 0, xdim-1, ydim-1);
			    	x += pic.getWidth();
			    }
				draw.rotatesprite(160 << 16,50 << 16, divscale(200, draw.getTile(HELPSCREEN4801).getHeight(), 15), 0, HELPSCREEN4801, 0, 0, 2 | 8, 0, 0, xdim - 1, ydim - 1);
				draw.rotatesprite(160 << 16,150 << 16, divscale(200, draw.getTile(HELPSCREEN4802).getHeight(), 15), 0, HELPSCREEN4802, 0, 0, 2 | 8, 0, 0, xdim - 1, ydim - 1);
			}

			@Override
			public boolean callback(MenuHandler handler, MenuOpt opt) {
				if(opt == MenuOpt.ESC)
					return true;
				return false;
			}

		};
		mTitle.flags = 7;

		addItem(mTitle, true);
	}
}
