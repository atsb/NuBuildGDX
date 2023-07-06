package ru.m210projects.Wang.Menus;

import static ru.m210projects.Wang.Factory.WangMenuHandler.DIFFICULTY;
import static ru.m210projects.Wang.Factory.WangMenuHandler.USERCONTENT;
import static ru.m210projects.Wang.Game.defGame;

import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.FileHandle.DirectoryEntry;
import ru.m210projects.Build.Pattern.MenuItems.BuildMenu;
import ru.m210projects.Build.Pattern.MenuItems.MenuButton;
import ru.m210projects.Build.Pattern.MenuItems.MenuHandler;
import ru.m210projects.Build.Pattern.MenuItems.MenuItem;
import ru.m210projects.Build.Pattern.MenuItems.MenuProc;
import ru.m210projects.Wang.Main;
import ru.m210projects.Wang.Factory.WangMenuHandler;
import ru.m210projects.Wang.Type.GameInfo;

public class MenuNewGame extends BuildMenu {

	private MenuProc newEpProc = new MenuProc() {
		@Override
		public void run(MenuHandler handler, MenuItem pItem) {
			final WangMenuHandler menu = (WangMenuHandler) handler;

			WangEpisodeButton but = (WangEpisodeButton) pItem;
			MenuDifficulty next = (MenuDifficulty) menu.mMenus[DIFFICULTY];
			next.setEpisode(but.game, but.specialOpt);
			menu.mOpen(next, but.nItem);
		}
	};

	private class AddonMenu extends BuildMenu {
		public AddonMenu(final Main app, GameInfo... addons) {
			addItem(new WangTitle("ADDON"), false);
			int pos = 15;
			int add = 0;
			for (int i = 0; i < addons.length; i++) {
				if (addons[i] != null) {
					addItem(new WangEpisodeButton(app, 35, pos += 32, addons[i], newEpProc, 1), add == 0);
					add++;
				}
			}
		}
	}

	public MenuNewGame(final Main app) {
		int posy = 45;
		final WangMenuHandler menu = (WangMenuHandler) app.menu;

		final MenuUserContent usercont = (MenuUserContent) (menu.mMenus[USERCONTENT] = new MenuUserContent(app));

		GameInfo wt = searchAddon(usercont, "wt.grp", "addons\\wt.grp");
		GameInfo td = searchAddon(usercont, "td.grp", "addons\\td.grp");
		if (td == null) {
			DirectoryEntry dir = BuildGdx.compat.checkDirectory("dragon");
			if (dir != null)
				td = usercont.getAddon(dir.getAbsolutePath(), dir);
		}

		addItem(new WangTitle("Episode"), false);
		addItem(new WangEpisodeButton(app, 35, posy, defGame, newEpProc, 0), true);
		addItem(new WangEpisodeButton(app, 35, posy += 32, defGame, newEpProc, 1), false);
		if (wt != null || td != null) {
			addItem(new MenuButton("Official addons", app.getFont(2), 35, posy += 32, 320, 0, 0,
					new AddonMenu(app, td, wt), -1, null, -1) {
            }, false);
		} else {
			addItem(new MenuButton("Official addons", app.getFont(2), 35, posy += 32, 320, 0, 0, null, -1, null, 0) {
				@Override
				public void draw(MenuHandler handler) {
					this.mCheckEnableItem(false);
					super.draw(handler);
				}
			}, false);
		}

		addItem(new MenuButton("User Content", app.getFont(2), 35, posy += 20, 320, 0, 0, null, -1,
				new MenuProc() {
					@Override
					public void run(MenuHandler handler, MenuItem pItem) {
						if (usercont.showmain)
							usercont.setShowMain(false);
						handler.mOpen(usercont, -1);
					}
				}, 0), false);
	}

	private GameInfo searchAddon(MenuUserContent content, String... paths) {
		GameInfo addon;
		for (String path : paths) {
			if ((addon = content.getAddon(path, null)) != null)
				return addon;
		}
		return null;
	}
}
