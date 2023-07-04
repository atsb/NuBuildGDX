// This file is part of DukeGDX.
// Copyright (C) 2019  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// DukeGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// DukeGDX is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with DukeGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.Duke3D.Menus;

import static ru.m210projects.Duke3D.Gamedef.preparescript;
import static ru.m210projects.Duke3D.Gamedef.isaltok;
import static ru.m210projects.Build.FileHandle.Compat.*;
import static ru.m210projects.Build.Strhandler.*;
import static ru.m210projects.Duke3D.Factory.DukeMenuHandler.*;
import static ru.m210projects.Duke3D.Names.*;
import static ru.m210projects.Duke3D.SoundDefs.PISTOL_BODYHIT;
import static ru.m210projects.Duke3D.Sounds.sound;
import static ru.m210projects.Duke3D.Globals.*;
import static ru.m210projects.Duke3D.Main.game;
import static ru.m210projects.Build.OnSceenDisplay.Console.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import ru.m210projects.Duke3D.Types.GameInfo;
import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.FileHandle.DirectoryEntry;
import ru.m210projects.Build.FileHandle.FileEntry;
import ru.m210projects.Build.FileHandle.Group;
import ru.m210projects.Build.FileHandle.GroupResource;
import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Build.Pattern.BuildFont.TextAlign;
import ru.m210projects.Build.Pattern.MenuItems.BuildMenu;
import ru.m210projects.Build.Pattern.MenuItems.MenuFileBrowser;
import ru.m210projects.Build.Pattern.MenuItems.MenuTitle;
import ru.m210projects.Duke3D.Main;

public class DUserContent extends BuildMenu {

	public boolean showmain;
	private final Main app;
	private final MenuFileBrowser list;
	
	public DUserContent(final Main app)
	{
		this.app = app;
		MenuTitle title = new DukeTitle("User Content");

		int width = 240;
		list = new MenuFileBrowser(app, app.getFont(0), app.getFont(1), app.getFont(0), 40, 45, width, 1, 14, LOADSCREEN) {
			
			@Override
			public void init() {
				registerExtension("map", 0, 0);
				registerExtension("grp", 2, 1);
				registerExtension("zip", 2, 1);
				registerExtension("con", 2, 1);
			}
			
			@Override
			public void handleFile(FileEntry fil) {
				if(fil.getExtension().equals("map"))
					addFile(fil, fil.getName());
				else if(fil.getExtension().equals("grp") || fil.getExtension().equals("zip"))
					buildPackage(this, fil);
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
						case "con":
							launchEpisode(episodes.get(fil.getPath()));
							break;
						case "grp":
						case "zip":
							launchEpisode(episodes.get(getFileName()));
							break;
					}
				}
			}
			
			@Override
			public void handleDirectory(DirectoryEntry dir)
			{
				if(app.menu.gShowMenu)
					sound(PISTOL_BODYHIT);
				buildAddons(this, dir);
			}

			@Override
			public void drawHeader(int x1, int x2, int y)
			{
				/*directories*/ topFont.drawText(x1, y, dirs, -32, topPal, TextAlign.Left, 2, true);
				/*files*/ topFont.drawText(x2, y, ffs, -32, topPal, TextAlign.Left, 2, true);
			}
			
			@Override
			public void drawPath(int x, int y)
			{
				brDrawText(pathFont, toCharArray(path), x, y, -32, pathPal, 0, this.x + this.width - 4);
			}
		};
		
		list.topPal = 10;
		list.pathPal = 10;
		list.listPal = 12;
		list.backgroundPal = 4;
		
		addItem(title, false);
		addItem(list, true);
	}
	
	public void checkDirectory(String path, String extension) {
		DirectoryEntry dir = BuildGdx.compat.checkDirectory(path);
		if(dir != null) {
			for (FileEntry file : dir.getFiles().values()) {
				if (file.getExtension().equals(extension))
					list.handleFile(file);
			}
		}
	}
	
	private void buildAddons(MenuFileBrowser blist, DirectoryEntry dir)
	{
		HashMap<String, List<String>> map = new HashMap<String, List<String>>();
		for (FileEntry file : dir.getFiles().values()) {
			if (file.getExtension().equals("con"))
				InitTree(map, preparescript(BuildGdx.compat.getBytes(file)), file.getName());
		}

		for (FileEntry file : dir.getFiles().values()) {
			if (file.getExtension().equals("con")) {
				List<String> list = map.get(file.getName());
				if (list != null)
					handleList(map, list);
			}
		}

		for (String con : map.keySet()) {
			if (dir != BuildGdx.compat.getDirectory(Path.Game) || !con.equals("game.con")) {
				FileEntry fil = dir.checkFile(con);
				GameInfo addon = episodes.get(fil.getPath());
				if (addon == null) {
					addon = new GameInfo(fil, con);
					addon.init();
					if (addon.isInited) {
						Console.Println("Addon found: " + addon.ConName);
						blist.addFile(fil, con);
						episodes.put(fil.getPath(), addon);
					}
				} else {
					if (addon.isInited)
						blist.addFile(fil, con);
				}
			}
		}
		
		if(showmain && dir == BuildGdx.compat.getDirectory(Path.Game)) {
			String entry = defGame.getFile().getPath();
			GameInfo addon = episodes.get(entry);
			if(addon == null) {
				blist.addFile(defGame.getFile(), entry);
				episodes.put(entry, defGame);
			} else {
				blist.addFile(defGame.getFile(), entry);
			}
		}
	}
	
	private void buildPackage(MenuFileBrowser blist, FileEntry file)
	{
		if(file.getParent() == BuildGdx.compat.getDirectory(Path.Game) && file.getName().equals(game.mainGrp))
			return; //show main
		
		try {
			Group res = BuildGdx.cache.isGroup(file.getPath());
			if(res != null)
			{
				HashMap<String, List<String>> map = new HashMap<String, List<String>>();
				for(GroupResource files : res.getList()) {
					if(files.getExtension().equals("con")) 
						InitTree(map, preparescript(files.getBytes()), files.getFullName());	
				}
				
				for(GroupResource files : res.getList()) {
					if(files.getExtension().equals("con")) {
						List<String> list = map.get(files.getFullName());
						if(list != null) 
							handleList(map, list);
					}
				}

				for (String s : map.keySet()) {
					String con = res.name + ":" + s;
					GameInfo addon = episodes.get(con);
					if (addon == null) {
						String conName = con.substring(con.indexOf(":") + 1);
						addon = new GameInfo(res, file, conName);
						if (addon.isInited) {
							Console.Println("Found addon: " + con);
							episodes.put(con, addon);
							blist.addFile(file, con);
						} else Console.Print(con + " found, but can't be loaded", OSDTEXT_RED);
					} else {
						if (addon.isInited) {
							blist.addFile(file, con);
						}
					}
				}
				
				res.dispose();
				res = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void InitTree(HashMap<String, List<String>> map, byte[] buf, String parentName)
	{
		if(buf == null) return;
		
        List<String> list = null;
		int index = -1;
        while( (index = indexOf("include ", buf, index+1)) != -1)
        {
        	int textptr = index + 7;
        	if(list == null) list = new ArrayList<String>();
        	
        	while( !isaltok(buf[textptr]) )
            {
                textptr++;
                if( textptr >= buf.length || buf[textptr] == 0 ) break;
            }

            int i = 0;
            while( textptr+i < buf.length && isaltok(buf[textptr+i]) ) i++;
            
            String name = new String(buf, textptr, i);
            list.add(name.toLowerCase());
        }

        if(list != null)
        	map.put(parentName, list);
	}
	
	private void handleList(HashMap<String, List<String>> map, List<String> list)
	{
		for(String child : list)
			for (Iterator<String> con = map.keySet().iterator(); con.hasNext();) {
				String name = con.next();
				if(name.equals(child)) {
					List<String> other = map.get(name);
					con.remove();
					if(other != null) 
						handleList(map, other);
					break;
				}
			}
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
	
	private void launchEpisode(GameInfo game)
	{
		if(game == null) return;

		if(mFromNetworkMenu())
		{
			NetworkMenu network = (NetworkMenu) app.menu.mMenus[NETWORKGAME];
			network.setEpisode(game);
			app.menu.mMenuBack();
			return;
		}

		NewAddonMenu next = (NewAddonMenu) app.menu.mMenus[NEWADDON];
		next.setEpisode(game);
		app.menu.mOpen(next, -1);
	}
	
	private void launchMap(FileEntry file)
	{
		if(file == null) return;
		
		if(mFromNetworkMenu())
		{
			NetworkMenu network = (NetworkMenu) app.menu.mMenus[NETWORKGAME];
			network.setMap(file);
			app.menu.mMenuBack();
			return;
		}
		
		DifficultyMenu next = (DifficultyMenu) app.menu.mMenus[DIFFICULTY];
		next.setMap(file);
		app.menu.mOpen(next, -1);
	}
}
