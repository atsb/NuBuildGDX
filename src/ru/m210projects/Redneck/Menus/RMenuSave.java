// This file is part of RedneckGDX.
// Copyright (C) 2017-2019  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// RedneckGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// RedneckGDX is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with RedneckGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.Redneck.Menus;

import static ru.m210projects.Redneck.LoadSave.*;
import static ru.m210projects.Build.Engine.xdim;
import static ru.m210projects.Build.Engine.ydim;
import static ru.m210projects.Redneck.Main.gGameScreen;
import static ru.m210projects.Redneck.Names.LOADSCREEN;

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

public class RMenuSave extends MenuLoadSave {

	public RMenuSave(BuildGame app) {
		super(app, app.getFont(0), 75, 50, 185, 240, 13, 12, 2, LOADSCREEN, new MenuProc() {
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
		return new RRTitle(text);
	}

	@Override
	public MenuPicnum getPicnum(Engine draw, int x, int y) {
		return new MenuPicnum(draw, x - 65, y - 3, LOADSCREEN, LOADSCREEN, 0x12800) {
			@Override
			public void draw(MenuHandler handler) {
				if(nTile != defTile)
					draw.rotatesprite(x + 4 << 16, y << 16, 0x11A00, 0, nTile, 0, 0, 10 | 16, 0, 0, xdim - 1, ydim - 1);
				else 
					draw.rotatesprite(x + 4 << 16, y << 16, 0x8D00, 0, nTile, 0, 0, 10 | 16, 0, 0, xdim - 1, ydim - 1);
			}
			
			@Override
			public void close() {
				gGameScreen.captBuffer = null;
			}
		};
	}

	@Override
	public MenuText getInfo(BuildGame app, int x, int y) {
		return new MenuText(lsInf.info, app.getFont(0), x - 53, y + 100, 0) {
			@Override
			public void draw(MenuHandler handler) {
				int ty = y;
				if (lsInf.addonfile != null && !lsInf.addonfile.isEmpty()) {
					font.drawText(x, ty, lsInf.addonfile, -128, 12, TextAlign.Left, 2, true);
					ty -= 10;
				}
				if (lsInf.date != null && !lsInf.date.isEmpty()) {
					font.drawText(x, ty, lsInf.date, -128, 12, TextAlign.Left, 2, true);
					ty -= 10;
				}
				if (lsInf.info != null)
					font.drawText(x, ty, lsInf.info, -128, 12, TextAlign.Left, 2, true);
			}
		};
	}

}
