package ru.m210projects.Tekwar.Menus;

import static ru.m210projects.Build.Engine.xdim;
import static ru.m210projects.Build.Engine.ydim;
import static ru.m210projects.Tekwar.Tekldsv.*;
import static ru.m210projects.Tekwar.Main.gGameScreen;

import ru.m210projects.Build.Engine;
import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.FileHandle.Compat.Path;
import ru.m210projects.Build.Pattern.BuildGame;
import ru.m210projects.Build.Pattern.BuildFont.TextAlign;
import ru.m210projects.Build.Pattern.CommonMenus.MenuLoadSave;
import ru.m210projects.Build.Pattern.MenuItems.MenuHandler;
import ru.m210projects.Build.Pattern.MenuItems.MenuItem;
import ru.m210projects.Build.Pattern.MenuItems.MenuPicnum;
import ru.m210projects.Build.Pattern.MenuItems.MenuProc;
import ru.m210projects.Build.Pattern.MenuItems.MenuSlotList;
import ru.m210projects.Build.Pattern.MenuItems.MenuText;
import ru.m210projects.Build.Pattern.MenuItems.MenuTitle;

public class TekMenuSave extends MenuLoadSave {

	public TekMenuSave(final BuildGame app) {
		super(app, app.getFont(0), 40, 40, 120, 240, 9, 0, 5, 321, new MenuProc() {
			@Override
			public void run(MenuHandler handler, MenuItem pItem) {
				MenuSlotList item = (MenuSlotList) pItem;
				String filename;
				if (item.l_nFocus == 0) {
					int num = 0;
					do {
						if (num > 9999)
							return;
						filename = "game" + makeNum(num) + ".sav";
						if (BuildGdx.compat.checkFile(filename, Path.User) == null)
							break;

						num++;
					} while (true);
				} else
					filename = item.FileName();
				if (item.typed == null || item.typed.isEmpty())
					item.typed = filename;

				savegame(item.typed, filename);
				handler.mClose();
			}
			
		}, true);
	}

	@Override
	public MenuTitle getTitle(BuildGame app, String text) {
		MenuTitle title = new MenuTitle(app.pEngine, text, app.getFont(0), 160, 15, -1);
		title.pal = 3;
		
		return title;
	}

	@Override
	public MenuPicnum getPicnum(Engine draw, int x, int y) {
		return new MenuPicnum(draw, x + 5, y - 3, 321, 321, 0x5800) {
			@Override
			public void draw(MenuHandler handler) {
				if(nTile != defTile)
					draw.rotatesprite(x << 16, y << 16, 2 * nScale, 0, nTile, 0, 0, 10 | 16, 0, 0, xdim - 1, ydim - 1);
				else draw.rotatesprite(x << 16, y << 16, nScale, 0, nTile, 0, 0, 10 | 16, 0, 0, xdim - 1, ydim - 1);
			}
			
			@Override
			public void close() {
				gGameScreen.captBuffer = null;
			}
		};
	}

	@Override
	public boolean loadData(String filename) {
		return lsReadLoadData(filename) != -1;
	}

	@Override
	public MenuText getInfo(BuildGame app, int x, int y) {
		return new MenuText(lsInf.info, app.getFont(1), x + 10, y + 60, 0) {
			@Override
			public void draw(MenuHandler handler) {
				int ty = y;
				if (lsInf.date != null && !lsInf.date.isEmpty()) {
					font.drawText(x, ty, lsInf.date.toCharArray(), -128, 4, TextAlign.Left, 0, false);
					ty -= 5;
				}
				if (lsInf.info != null)
					font.drawText(x, ty, lsInf.info.toCharArray(), -128, 4, TextAlign.Left, 0, false);
			}
		};
	}

}
