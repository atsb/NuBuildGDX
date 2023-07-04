// This file is part of PowerslaveGDX.
// Copyright (C) 2019  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// PowerslaveGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// PowerslaveGDX is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with PowerslaveGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.Powerslave.Menus;

import static ru.m210projects.Powerslave.Factory.PSMenuHandler.ADDON;
import static ru.m210projects.Powerslave.Globals.*;
import static ru.m210projects.Powerslave.Main.*;

import java.util.Arrays;
import java.util.Comparator;

import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.FileHandle.Compat.Path;
import ru.m210projects.Build.FileHandle.DirectoryEntry;
import ru.m210projects.Build.FileHandle.FileEntry;
import ru.m210projects.Build.FileHandle.FileUtils;
import ru.m210projects.Build.FileHandle.Group;
import ru.m210projects.Build.FileHandle.GroupResource;
import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Build.Pattern.BuildFont.TextAlign;
import ru.m210projects.Build.Pattern.MenuItems.BuildMenu;
import ru.m210projects.Build.Pattern.MenuItems.MenuFileBrowser;
import ru.m210projects.Build.Pattern.MenuItems.MenuTitle;
import ru.m210projects.Build.Pattern.Tools.NaturalComparator;
import ru.m210projects.Powerslave.Main;
import ru.m210projects.Powerslave.Type.EpisodeInfo;
import ru.m210projects.Powerslave.Type.MapInfo;

public class PSMenuUserContent extends BuildMenu {

	private static Comparator<GroupResource> rescomp = new Comparator<GroupResource>() {
		@Override
		public int compare(GroupResource o1, GroupResource o2) {
			String s1 = o1.getName();
			String s2 = o2.getName();

			return NaturalComparator.compare(s1, s2);
		}
	};

	private Main app;
	public PSMenuUserContent(final Main app)
	{
		this.app = app;
		MenuTitle title = new PSTitle("User content", 160, 15, 0);

		int width = 240;
		MenuFileBrowser list = new MenuFileBrowser(app, app.getFont(3), app.getFont(0), app.getFont(3), 40, 55, width, 1, 17, BACKGROUND) {
			@Override
			public void init() {
				registerExtension("map", 0, 0);
				registerExtension("zip", 5, 1);
				registerClass(EpisodeInfo.class, 5, 1);
			}

			@Override
			public void handleDirectory(DirectoryEntry dir)
			{
				EpisodeInfo ep = getEpisode(dir);
				if(ep != null)
					addFile(ep, ep.Title);
			}

			@Override
			public void handleFile(FileEntry file) {

				if(file.getExtension().equals("map")) {
					EpisodeInfo ep = episodes.get(file.getParent().getAbsolutePath());
					if(ep != null) {
						for(int j = 0; j < ep.maps(); j++) {
							if(file.getPath().equals(ep.gMapInfo.get(j).path))
								return;
						}
					}
					addFile(file, file.getName());
				}
				else if(file.getExtension().equals("zip")) {
					EpisodeInfo ep = getEpisode(file);
					if(ep != null)
						addFile(ep, file.getName());
				}
			}

			@Override
			public void invoke(Object fil) {
				if(fil == null) return;

				if(fil instanceof FileEntry) {
					if(((FileEntry)fil).getExtension().equals("map")) {
						launchMap((FileEntry)fil);
						app.pMenu.mClose();
					}
				} else if(fil instanceof EpisodeInfo)
					launchEpisode((EpisodeInfo) fil);
			}

			@Override
			protected void drawHeader(int x1, int x2, int y)
			{
				/*directories*/ app.getFont(1).drawText(x1, y, dirs, 32768, -32, topPal, TextAlign.Left, 2, fontShadow);
				/*files*/ app.getFont(1).drawText(x2 + 13, y, ffs, 32768, -32, topPal, TextAlign.Left, 2, fontShadow);
			}
		};

		list.topPal = 20;
		addItem(title, false);
		addItem(list, true);
	}

	private void launchEpisode(EpisodeInfo ep)
	{
		if(ep == null) return;

		((NewAddon) app.menu.mMenus[ADDON]).setAddon(ep);
		app.pMenu.mOpen(app.menu.mMenus[ADDON], -1);
	}

	private void launchMap(final FileEntry fil)
	{
		if(fil == null) return;

		gGameScreen.newgame(fil, 0, false);
	}

	public static EpisodeInfo getEpisode(FileEntry pack)
	{
		if(pack == null) return null;

		EpisodeInfo ep = episodes.get(pack.getPath());
		if(ep == null) {
			Group gr = BuildGdx.cache.isGroup(pack.getPath());
			if(gr != null) {
				GroupResource[] list = new GroupResource[gr.numfiles];
				gr.getList().toArray(list);
				Arrays.sort(list, rescomp);

				int nCount = 0;
				char[] mapname = "lev1.map".toCharArray();
				for(int i = 0; i < list.length; i++) {
					GroupResource file = list[i];
					String name = file.getFullName();
					if(file.getExtension().equals("map")) {
						if(String.copyValueOf(mapname).equals(name)) {
							if(ep == null) {
								ep = new EpisodeInfo(pack.getName());
								ep.path = pack.getPath();
								ep.packed = true;
								episodes.put(ep.path, ep);
								Console.Println("Addon found: " + ep.Title);
							}
							ep.gMapInfo.add(new MapInfo(name, pack.getName() + ": Map" + (nCount + 1)));
							mapname = ("lev" + (1 + ++nCount) + ".map").toCharArray();
						}
					}
				}
			}
		}
		return ep;
	}

	public static EpisodeInfo getEpisode(DirectoryEntry dir)
	{
		if(dir == null) return null;

		EpisodeInfo ep = episodes.get(dir.getAbsolutePath());
		if(ep == null) {
			FileEntry[] list = new FileEntry[dir.getFiles().size()];
			dir.getFiles().values().toArray(list);
			Arrays.sort(list);

			int nCount = 0;

			char[] mapname = "lev1.map".toCharArray();
			if(dir != BuildGdx.compat.getDirectory(Path.Game)) {
				for(int i = 0; i < list.length; i++) {
					FileEntry file = list[i];
					String name = file.getName();
					if(file.getExtension().equals("map")) {
						if(String.copyValueOf(mapname).equals(name)) {
							if(ep == null) {
								ep = new EpisodeInfo(dir.getName());
								ep.path = dir.getRelativePath();
								episodes.put(dir.getAbsolutePath(), ep);
								Console.Println("Addon found: " + ep.Title);
							}
							ep.gMapInfo.add(new MapInfo(file.getPath(), FileUtils.getFullName(dir.getName()) + ": Map" + (nCount + 1)));
							mapname = ("lev" + (1 + ++nCount) + ".map").toCharArray();
						}
					}
				}
			}
		}

		return ep;
	}

	public static void resetEpisodeResources(EpisodeInfo def)
	{
		BuildGdx.cache.clearDynamicResources();
		gCurrentEpisode = def;
	}

	public static boolean checkEpisodeResources(EpisodeInfo addon)
	{
		resetEpisodeResources(gOriginalEpisode);

		if(addon.packed)
		{
			FileEntry fil = BuildGdx.compat.checkFile(addon.path);
			if(fil != null)
			{
				Group gr = BuildGdx.cache.add(fil.getPath());
				gr.setFlags(true, true);
			} else {
				game.GameMessage("Can't load addon: " + addon.path + "\r\n");
				return false;
			}
		}
		gCurrentEpisode = addon;
		return true;
	}

}
