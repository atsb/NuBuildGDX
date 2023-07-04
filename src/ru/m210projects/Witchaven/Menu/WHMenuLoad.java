package ru.m210projects.Witchaven.Menu;

import static ru.m210projects.Build.Engine.xdim;
import static ru.m210projects.Build.Engine.ydim;
import static ru.m210projects.Witchaven.Main.gGameScreen;
import static ru.m210projects.Witchaven.Main.gLoadScreen;
import static ru.m210projects.Witchaven.Whldsv.*;
import static ru.m210projects.Witchaven.Names.*;
import static ru.m210projects.Witchaven.WHScreen.showmessage;

import ru.m210projects.Build.Engine;
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

public class WHMenuLoad extends MenuLoadSave {

	public WHMenuLoad(final BuildGame app) {
		super(app, app.getFont(1), 90, 60, 190, 210, 9, 0, 5, MAINMENU, new MenuProc() {
			@Override
			public void run(final MenuHandler handler, MenuItem pItem) {
				final MenuSlotList item = (MenuSlotList) pItem;
				if(canLoad(item.FileName())) {
					app.changeScreen(gLoadScreen);
					gLoadScreen.init(new Runnable() {
						public void run() {
							if(!loadgame(item.FileName())) {
								handler.mClose();
								app.setPrevScreen();
								if(app.isCurrentScreen(gGameScreen)) {
									showmessage("Incompatible version of saved game found!", 240);
									app.pNet.ready2send = true;
								}
							}
						}
					});
				}
			}
			
		}, false);
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
