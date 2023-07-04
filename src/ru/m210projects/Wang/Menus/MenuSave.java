package ru.m210projects.Wang.Menus;

import static ru.m210projects.Wang.Main.*;
import static ru.m210projects.Wang.Names.*;
import static ru.m210projects.Wang.LoadSave.*;
import static ru.m210projects.Build.Engine.xdim;
import static ru.m210projects.Build.Engine.ydim;
import static ru.m210projects.Build.Gameutils.coordsConvertXScaled;

import ru.m210projects.Build.Engine;
import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.FileHandle.Compat.Path;
import ru.m210projects.Build.Gameutils.ConvertType;
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

public class MenuSave extends MenuLoadSave {

	public MenuSave(final BuildGame app) {
		super(app, app.getFont(0), 75, 50, 185, 240, 12, 16, 19, LOADSCREEN, new MenuProc() {
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
		
		list.owncursor = true;
		list.pal = 16;
		list.helpPal = 16;
		list.questionFont = app.getFont(1);
		list.nListOffset = 15;
		list.backgroundPal = 4;
	}

	@Override
	public boolean loadData(String filename) {
		return lsReadLoadData(filename) != -1;
	}

	@Override
	public MenuTitle getTitle(BuildGame app, String text) {
		return new WangTitle(text);
	}

	@Override
	public MenuPicnum getPicnum(Engine draw, int x, int y) {
		return new MenuPicnum(draw, x - 70, y - 3, LOADSCREEN, LOADSCREEN, 0) {
			@Override
			public void draw(MenuHandler handler) {
				if(nTile != defTile)
					draw.rotatesprite(x + 3 << 16, y << 16, 0x12400, 0, nTile, 0, 0, 10 | 16, 0, 0, xdim - 1, ydim - 1);
				else draw.rotatesprite((x + 94) << 16, y + 57 << 16, 0x9200, 0, nTile, 0, 0, 10, coordsConvertXScaled(x, ConvertType.Normal), 0, coordsConvertXScaled(x + 186, ConvertType.Normal), ydim - 1);
			}
			
			@Override
			public void close() {
				gGameScreen.captBuffer = null;
			}
		};
	}

	@Override
	public MenuText getInfo(BuildGame app, int x, int y) {
		return new MenuText(lsInf.info, app.getFont(0), x - 58, y + 100, 0) {
			@Override
			public void draw(MenuHandler handler) {
				int ty = y;
				if (lsInf.addonfile != null && !lsInf.addonfile.isEmpty()) {
					font.drawText(x, ty, lsInf.addonfile, -128, 4, TextAlign.Left, 2, true);
					ty -= 10;
				}
				if (lsInf.date != null && !lsInf.date.isEmpty()) {
					font.drawText(x, ty, lsInf.date, -128, 4, TextAlign.Left, 2, true);
					ty -= 10;
				}
				if (lsInf.info != null)
					font.drawText(x, ty, lsInf.info, -128, 4, TextAlign.Left, 2, true);
			}
		};
	}

}
