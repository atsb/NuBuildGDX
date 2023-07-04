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

import static ru.m210projects.Build.FileHandle.Compat.*;
import static ru.m210projects.Build.Strhandler.*;
import static ru.m210projects.Blood.Factory.BloodMenuHandler.*;
import static ru.m210projects.Blood.Globals.MainINI;
import static ru.m210projects.Blood.Main.*;
import static ru.m210projects.Blood.Types.DEMO.*;
import static ru.m210projects.Blood.LEVELS.episodes;
import static ru.m210projects.Blood.LEVELS.kMaxEpisode;

import ru.m210projects.Blood.Main;
import ru.m210projects.Blood.Factory.BloodMenuHandler;
import ru.m210projects.Blood.Types.BloodIniFile;
import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.FileHandle.DirectoryEntry;
import ru.m210projects.Build.FileHandle.FileEntry;
import ru.m210projects.Build.FileHandle.Group;
import ru.m210projects.Build.FileHandle.GroupResource;
import ru.m210projects.Build.FileHandle.Resource;
import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Build.Pattern.BuildFont.TextAlign;
import ru.m210projects.Build.Pattern.MenuItems.BuildMenu;
import ru.m210projects.Build.Pattern.MenuItems.MenuFileBrowser;
import ru.m210projects.Build.Pattern.MenuItems.MenuTitle;

public class BLUserContent extends BuildMenu {

	public boolean showmain;
	private final Main app;
	private final MenuFileBrowser list;

	public BLUserContent(final Main app)
	{
		this.app = app;
		MenuTitle title = new MenuTitle(app.pEngine, "User content", app.getFont(1), 160, 20, 2038);

		int width = 240;
		list = new MenuFileBrowser(app, app.getFont(3), app.getFont(1), app.getFont(3), 40, 40, width, 1, 10, 2046) {

			@Override
			public void init() {
				registerExtension("map", 0, 0);
				registerExtension("rff", 7, 1);
				registerExtension("grp", 7, 1);
				registerExtension("zip", 7, 1);
				registerExtension("ini", 7, 1);
				registerExtension("dem", 10, -1);
			}

			@Override
			public void handleFile(FileEntry fil) {
				switch (fil.getExtension()) {
					case "ini":
						boolean main = fil.getParent() == BuildGdx.compat.getDirectory(Path.Game);
						if (showmain) main = false;
						if (!main || (main && !fil.getName().equalsIgnoreCase(MainINI.getName()))) {
							BloodIniFile ini = episodes.get(fil.getPath());
							String name;
							if (ini == null) //initialize episode fils list
								name = buildEpisode(BuildGdx.cache.getBytes(fil.getPath(), 0), fil.getName(), fil, false);
							else name = fil.getName();

							if (name != null) {
								addFile(fil, name);
							}
						}
						break;
					case "grp":
					case "zip":
					case "rff":
						buildPackage(this, fil);
						break;
					case "dem":
						if (demoscan(fil, false))
							addFile(fil, fil.getName());
						break;
					default:
						addFile(fil, fil.getName());
						break;
				}
			}

			@Override
			public void invoke(Object obj) {
				if(obj == null) return;

				if(obj instanceof FileEntry) {
					FileEntry fil = (FileEntry) obj;
					switch (fil.getExtension()) {
						case "map":
							launchMap(fil);
							break;
						case "ini":
							launchEpisode(episodes.get(fil.getPath()));
							break;
						case "grp":
						case "zip":
						case "rff":
							launchEpisode(episodes.get(getFileName()));
							break;
						case "dem":
							gDemoScreen.showDemo(fil.getPath(), null);
							app.menu.mClose();
							break;
					}
				}
			}

			@Override
			public void handleDirectory(DirectoryEntry dir)
			{
				/*nothing*/
			}

			@Override
			public void drawHeader(int x1, int x2, int y)
			{
				/*directories*/ topFont.drawText(x1 + 11, y + 5, dirs, 32768, -32, topPal, TextAlign.Left, 2, true);
				/*files*/ topFont.drawText(x2 + 10, y + 5, ffs, 32768, -32, topPal, TextAlign.Left, 2, true);
			}

			@Override
			public void drawPath(int x, int y)
			{
				brDrawText(pathFont, toCharArray(path), x, y, -32, pathPal, 0, this.x + this.width - 4);
			}
		};

		list.transparent = 33;
		list.topPal = 8;
		list.pathPal = 8;
		addItem(title, false);
		addItem(list, true);
		addItem(((BloodMenuHandler) app.pMenu).addMenuBlood(), false);
	}

	public boolean mFromNetworkMenu() {
		return app.menu.getLastMenu() == app.menu.mMenus[NETWORKGAME];
	}

	public void setShowMain(boolean show)
	{
		this.showmain = show;
		if(list.getDirectory() == BuildGdx.compat.getDirectory(Path.Game))
			list.refreshList();
	}

	private void launchEpisode(BloodIniFile ini)
	{
		if(ini == null) return;

		if(mFromNetworkMenu())
		{
			MenuNetwork network = (MenuNetwork) app.menu.mMenus[NETWORKGAME];
			network.setEpisode(ini);
			app.menu.mMenuBack();
			return;
		}

		MenuNewAddon next = (MenuNewAddon) app.menu.mMenus[NEWADDON];
		next.setEpisode(ini);
		app.menu.mOpen(next, -1);
	}

	private void launchMap(FileEntry file)
	{
		if(file == null) return;

		if(mFromNetworkMenu())
		{
			MenuNetwork network = (MenuNetwork) app.menu.mMenus[NETWORKGAME];
			network.setMap(file);
			app.menu.mMenuBack();
			return;
		}

		MenuDifficulty next = (MenuDifficulty) app.menu.mMenus[DIFFICULTY];
		next.setMap(file);
		app.menu.mOpen(next, -1);
	}

	private String buildPackage(MenuFileBrowser list, FileEntry file)
	{
		try {
			Group res = BuildGdx.cache.isGroup(file.getPath());
			if(res != null)
			{
				for(GroupResource files : res.getList()) {
					if(files.getExtension().equals("ini")) {
						String ptr = file.getPath() + ":" + files.getFullName();
						BloodIniFile ini = episodes.get(ptr);
						if(ini == null) //initialize episode files list
						{
							Resource gres = res.open(files.getFullName());
							byte[] data = gres.getBytes();
							if((ptr = buildEpisode(data, files.getFullName(), file, true)) != null) {
								list.addFile(file, ptr);
							}
							gres.close();
						} else {
							list.addFile(file, ptr);
						}
					}
				}
				res.dispose();
				res = null;
			}
		} catch (Exception e) {
			Console.Println("Can't load " + file.getPath(), Console.OSDTEXT_RED);
			e.printStackTrace();
		}

		return null;
	}

	private String buildEpisode(byte[] data, String name, FileEntry file, boolean isPackage)
	{
		if(data == null) return null;

		BloodIniFile ini = new BloodIniFile(data, name, file);
		boolean isEpisode = false;
		for(int i = 0; i < kMaxEpisode; i++)
			if(ini.set("Episode" + (i + 1)))
				isEpisode = true;

		if(isEpisode) {
			ini.setPackage(isPackage);
			if(isPackage) {
				String ptr = file.getPath() + ":" + ini.getName();
				episodes.put(ptr, ini);
				return ptr;
			} else {
				episodes.put(file.getPath(), ini);
				return file.getName();
			}
		}
		else
			ini.close();

		return null;
	}
}
