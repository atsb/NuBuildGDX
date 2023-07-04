package ru.m210projects.Witchaven.Menu;

import static ru.m210projects.Build.Engine.xdim;
import static ru.m210projects.Build.Engine.ydim;
import static ru.m210projects.Witchaven.Whldsv.*;
import static ru.m210projects.Witchaven.Main.gGameScreen;
import static ru.m210projects.Witchaven.Names.*;

import ru.m210projects.Build.Engine;
import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.FileHandle.Compat.Path;
import ru.m210projects.Build.Pattern.BuildFont.TextAlign;
import ru.m210projects.Build.Pattern.BuildGame;
import ru.m210projects.Build.Pattern.CommonMenus.MenuLoadSave;
import ru.m210projects.Build.Pattern.MenuItems.MenuHandler;
import ru.m210projects.Build.Pattern.MenuItems.MenuItem;
import ru.m210projects.Build.Pattern.MenuItems.MenuPicnum;
import ru.m210projects.Build.Pattern.MenuItems.MenuProc;
import ru.m210projects.Build.Pattern.MenuItems.MenuSlotList;
import ru.m210projects.Build.Pattern.MenuItems.MenuText;
import ru.m210projects.Build.Pattern.MenuItems.MenuTitle;

public class WHMenuSave extends MenuLoadSave {

	public WHMenuSave(final BuildGame app) {
		super(app, app.getFont(1), 90, 60, 190, 210, 9, 0, 5, MAINMENU, new MenuProc() {
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
		return new WHTitle(text, 90, 0);
	}

	@Override
	public MenuPicnum getPicnum(Engine draw, int x, int y) {
		return new MenuPicnum(draw, x - 68, y - 3, MAINMENU, MAINMENU, 0x8700) {
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
		return new MenuText(lsInf.info, app.getFont(1), x - 58, y + 90, 0) {
			@Override
			public void draw(MenuHandler handler) {
				int ty = y;
				if (lsInf.addonfile != null && !lsInf.addonfile.isEmpty()) {
					font.drawText(x, ty, lsInf.addonfile, -128, 0, TextAlign.Left, 2, true);
					ty -= 10;
				}
				if (lsInf.date != null && !lsInf.date.isEmpty()) {
					font.drawText(x, ty, lsInf.date.toCharArray(), -128, 0, TextAlign.Left, 2, true);
					ty -= 10;
				}
				if (lsInf.info != null)
					font.drawText(x, ty, lsInf.info.toCharArray(), -128, 0, TextAlign.Left, 2, true);
			}
		};
	}

}
