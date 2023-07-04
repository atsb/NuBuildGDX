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

package ru.m210projects.Duke3D;

import static ru.m210projects.Duke3D.Gamedef.*;
import static ru.m210projects.Duke3D.Main.*;
import static ru.m210projects.Duke3D.Globals.*;
import static ru.m210projects.Duke3D.Sounds.*;
import static ru.m210projects.Duke3D.Names.MIRROR;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.FileHandle.DirectoryEntry;
import ru.m210projects.Build.FileHandle.FileEntry;
import ru.m210projects.Build.FileHandle.Group;
import ru.m210projects.Build.FileHandle.GroupResource;
import ru.m210projects.Build.FileHandle.PackedZipGroup;
import ru.m210projects.Build.FileHandle.Resource;
import ru.m210projects.Build.FileHandle.UserGroup;
import ru.m210projects.Build.FileHandle.ZipGroup;
import ru.m210projects.Build.FileHandle.Cache1D.PackageType;
import ru.m210projects.Build.FileHandle.Compat.Path;
import ru.m210projects.Build.Script.DefScript;
import ru.m210projects.Duke3D.Types.GameInfo;

public class ResourceHandler {

	private static UserGroup usergroup;
	public static boolean usecustomarts;

	public static void resetEpisodeResources()
	{
		BuildGdx.cache.clearDynamicResources();

		usergroup = null;
		currentGame = defGame;

		for(int i = 0; i < NUM_SOUNDS; i++)
			Sound[i].ptr = null;

		if(!usecustomarts) {
			game.setDefs(game.baseDef);
			return;
		}

		System.err.println("Reset to default resources");
		for(int i = 0; i < kMaxTiles; i++) //don't touch usertiles
			engine.getTile(i).clear();

		if(engine.loadpics() == 0)
			game.dassert("ART files not found " + new File(Path.Game.getPath() + "TILES###.ART").getAbsolutePath());

		game.setDefs(game.baseDef);

		InitSpecialTextures();

	    usecustomarts = false;
	}

	public static void InitSpecialTextures()
	{
		engine.getTile(MIRROR).clear();
		engine.getTile(13).clear(); //ROR tile
	}

	public static void InitGroupResources(List<GroupResource> list)
	{
		for(GroupResource res : list) {
			if(res.getExtension().equals("art")) {
				engine.loadpic(res.getFullName());
				usecustomarts = true;
			}
		}
	}

	public static GameInfo levelGetEpisode(String filepath)
	{
		if(filepath == null) return null;

		String fullname = filepath;
		String conName = null;
		int filenameIndex = -1;
		if((filenameIndex = fullname.indexOf(":")) != -1)
		{
			filepath = fullname.substring(0, filenameIndex);
			conName = fullname.substring(filenameIndex+1);
		}

		FileEntry file = BuildGdx.compat.checkFile(filepath);
		if(file != null)
		{
			GameInfo ini = null;
			if(filenameIndex == -1 && (ini = episodes.get(file.getPath())) == null)
			{
				if(file.getExtension().equals("con")) {
					ini = new GameInfo(file, file.getName());
					ini.init();
					if(ini.isInited)
						episodes.put(file.getPath(), ini);
				}
			}
			else if(filenameIndex != -1 && (ini = episodes.get(fullname)) == null)
			{
				if(file.getExtension().equals("zip")
					|| file.getExtension().equals("grp"))
				{
					try {
						Group res = BuildGdx.cache.isGroup(file.getPath());
						if(res != null)
						{
							ini = new GameInfo(res, file, conName);
							if(ini.isInited) {
								System.err.println("load: put " + fullname);
								episodes.put(fullname, ini);
							}
							else ini = null;
						}
						res.dispose();
						res = null;
					} catch (Exception e) {
						e.printStackTrace();
						return null;
					}
				}
			}
			return ini;
		}
		return null;
	}

	public static void prepareusergroup(Group group, boolean removable) {
		if(group.type == PackageType.Zip) //Correct path in archive (files shouldn't be in a subfolder)
			((ZipGroup) group).removeFolders();
		else if(group.type == PackageType.PackedZip)
			((PackedZipGroup) group).removeFolders();

		List<GroupResource> list = group.getList();
		for(GroupResource res : list) {
			if(res.getExtension().equals("grp") || res.getExtension().equals("zip"))
			{
				BuildGdx.cache.add(res, removable);
			}

//			if(res.fileformat.equals("cue")) {
//				Console.Println("Cd tracks found...");
//				parserfs(removable?group:-1, res.filename, res.getBytes());
//			}
		}
	}

	private static void searchEpisodeResources(DirectoryEntry cache)
	{
		if(cache.getDirectories().size() > 0)
		{
			for (DirectoryEntry dir : cache.getDirectories().values()) {
				dir.InitDirectory(dir.getAbsolutePath());
				if (!dir.getName().equals("<userdir>"))
					searchEpisodeResources(dir);
			}
		}

		if(usergroup == null)
			usergroup = BuildGdx.cache.add("User", true);

		for (FileEntry file : cache.getFiles().values()) {
			if (!file.getExtension().equals("zip")
					&& !file.getExtension().equals("grp"))
				usergroup.add(file, -1);
		}
	}

	public static void checkEpisodeResources(GameInfo addon)
	{
		resetEpisodeResources();

		DefScript addonScript = new DefScript(game.baseDef, addon.getFile());
		if(addon.isPackage())
		{
			FileEntry fil = addon.getFile();
			try {
				Group gr = BuildGdx.cache.add(fil.getPath());
				gr.setFlags(true, true);

				Resource res = gr.open(appdef); //load def scripts before delete folders
				if(res != null)
				{
					addonScript.loadScript(gr.name + " script", res.getBytes());
					res.close();
				}

				prepareusergroup(gr, true);
			} catch(Exception e) {
				game.GameCrash("Error found in " + fil.getPath() + "\r\n" + e.getMessage());
				return;
			}
		} else if(addon.getDirectory() != BuildGdx.compat.getDirectory(Path.Game)) {
			searchEpisodeResources(addon.getDirectory());

			if(addon.getDirectory() != null) {
				FileEntry def = addon.getDirectory().checkFile(appdef);
				if(def != null) {
					addonScript.loadScript(def);
				}
			}
		}

		error = 0;
		//Loading user package files
		InitGroupResources(BuildGdx.cache.getDynamicResources());

		if(addon.getCON() == null)
			addon.setCON(loaduserdef(addon.ConName));

		if(error == 0) {
			currentGame = addon;
			game.setDefs(addonScript);
		}
		else {
			game.GameCrash("\nErrors found in " + addon.ConName + " file.");
		}
	}
}
