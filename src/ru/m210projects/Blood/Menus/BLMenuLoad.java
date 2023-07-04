// This file is part of BloodGDX.
// Copyright (C) 2017-2019  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// BloodGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// BloodGDX is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with BloodGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.Blood.Menus;

import static ru.m210projects.Blood.Main.*;
import static ru.m210projects.Blood.LOADSAVE.*;
import static ru.m210projects.Build.Engine.xdim;
import static ru.m210projects.Build.Engine.ydim;

import ru.m210projects.Blood.Factory.BloodMenuHandler;
import ru.m210projects.Build.Engine;
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

public class BLMenuLoad extends MenuLoadSave {

	public BLMenuLoad(final BuildGame app) {
		super(app, app.getFont(3), 75, 50, 185, 240, 10, 0, 8, 2046, new MenuProc() {
			@Override
			public void run(MenuHandler handler, MenuItem pItem) {
				final MenuSlotList item = (MenuSlotList) pItem;
				if(canLoad(item.FileName())) {
					app.changeScreen(gLoadingScreen);
					gLoadingScreen.init(new Runnable() {
						public void run() {
							if(!loadgame(item.FileName())) {
								app.setPrevScreen();
								if(app.isCurrentScreen(gGameScreen)) {
									app.pNet.ready2send = true;
								}
							}
						}
					});
				}
			}
			
		}, false);
		list.transparent = 33;
		
		addItem(((BloodMenuHandler) app.pMenu).addMenuBlood(), false);
	}

	@Override
	public boolean loadData(String filename) {
		return lsReadLoadData(filename) != -1;
	}

	@Override
	public MenuTitle getTitle(BuildGame app, String text) {
		return new MenuTitle(app.pEngine, text, app.getFont(1), 160, 20, 2038);
	}

	@Override
	public MenuPicnum getPicnum(Engine draw, int x, int y) {
		return new MenuPicnum(draw, x - 70, y - 3, 2046, 2046, 0x9470) {
			@Override
			public void draw(MenuHandler handler) {
				draw.rotatesprite(x << 16, y << 16, nScale, 0, nTile, 0, 0, 10 | 16, 0, 0, xdim - 1, ydim - 1);
			}
		};
	}

	@Override
	public MenuText getInfo(BuildGame app, int x, int y) {
		return new MenuText(lsInf.info, app.getFont(3), x - 58, y + 100, 0) {
			@Override
			public void draw(MenuHandler handler) {
				int ty = y;
				if (lsInf.iniName != null && !lsInf.iniName.isEmpty()) {
					font.drawText(x, ty, lsInf.iniName.toCharArray(), -128, 0, TextAlign.Left, 2, false);
					ty -= 10;
				}
				if (lsInf.date != null && !lsInf.date.isEmpty()) {
					font.drawText(x, ty, lsInf.date.toCharArray(), -128, 0, TextAlign.Left, 2, false);
					ty -= 10;
				}
				if (lsInf.info != null)
					font.drawText(x, ty, lsInf.info.toCharArray(), -128, 0, TextAlign.Left, 2, false);
			}
		};
	}

}
