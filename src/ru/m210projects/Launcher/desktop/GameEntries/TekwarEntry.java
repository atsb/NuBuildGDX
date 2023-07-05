//This file is part of BuildGDX.
//Copyright (C) 2017-2019  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
//BuildGDX is free software: you can redistribute it and/or modify
//it under the terms of the GNU General Public License as published by
//the Free Software Foundation, either version 3 of the License, or
//(at your option) any later version.
//
//BuildGDX is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU General Public License for more details.
//
//You should have received a copy of the GNU General Public License
//along with BuildGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.Launcher.desktop.GameEntries;

import static ru.m210projects.Launcher.desktop.Main.*;

import java.io.File;
import java.net.URL;
import java.util.HashMap;

import static ru.m210projects.Build.Strhandler.toLowerCase;

import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Build.Pattern.BuildGame;
import ru.m210projects.Build.Settings.BuildConfig;
import ru.m210projects.Launcher.desktop.CheckFiles;
import ru.m210projects.Tekwar.Config;
import ru.m210projects.Tekwar.Main;

public abstract class TekwarEntry extends GameEntry {

	public boolean isDemo = false;

	public TekwarEntry() {
		super("NuTekWarGDX", appversion);
	}

	@Override
	public URL getLogo() {
		return this.getClass().getResource("/" +headerPath + "/headertw.png");
	}
	
	@Override
	public URL getIcon16() {
		return this.getClass().getResource("/" +iconPath + "/Tekwar/tekwar16.png");
	}

	@Override
	public String[] getIcons() {
		String icons[] = {
			iconPath + "/Tekwar/tekwar16.png",
			iconPath + "/Tekwar/tekwar32.png",
			iconPath + "/Tekwar/tekwar128.png",
		};
		
		return icons;
	}

	@Override
	public String getResourceName() {
		return "TekWar";
	}

	@Override
	public ResFile[] getResourceFiles() {
		ResFile[] resources = { 
			new ResFile("smkmm.smk"), 
			new ResFile("tiles000.art"),
			new ResFile("tables.dat"), 
			new ResFile("palette.dat"), 
			new ResFile("songs"),
			new ResFile("sounds") 
		};
		return resources;
	}
	
	public ResFile[] getDemoResource() {
		ResFile[] resources = { 
			new ResFile("city.map"), 
			new ResFile("tiles000.art"),
			new ResFile("tables.dat"), 
			new ResFile("palette.dat"), 
			new ResFile("songs"),
			new ResFile("sounds") 
		};
		return resources;
	}

	@Override
	public CheckFiles checkResources(String path, boolean errorMessage) {
		File directory = new File(path);
		
		File[] fList = directory.listFiles();
		if(fList == null)
			return new CheckFiles(false, null);
		
		HashMap<String, File> filesMap = new HashMap<String, File>();
		for (File file : fList) 
			filesMap.put(toLowerCase(file.getName()), file);
		
		ResFile[] resources = getResourceFiles();
		
		String missingFiles = "<html>";
		
		isDemo = false;
		boolean found = true;
		for (int i = 0; i < resources.length; i++) {
			if (filesMap.get(resources[i].name) == null) {
				found = false;
				if(errorMessage) Console.Println(appname + " error: " + resources[i].name + " is missing!");
				missingFiles = missingFiles + resources[i].name.toUpperCase() + " is missing<br>";
			}
		}		
		if(found) return new CheckFiles(true, null);

		if(errorMessage) Console.Println(appname + ": trying to find demo version");
		isDemo = found = true;
		resources = getDemoResource();
		for (int i = 0; i < resources.length; i++) {
			if(resources[i].name.equals("SOUNDS") && filesMap.get(resources[i].name).length() != 2138112) {
				found = false;
				if(errorMessage) Console.Println(appname + " demo error: " + resources[i].name + " is missing!");
				break;
			}
			
			if (filesMap.get(resources[i].name) == null) {
				found = false;
				if(errorMessage) Console.Println(appname + " demo error: " + resources[i].name + " is missing!");
				break;
			}
			
		}
		if(found) return new CheckFiles(true, null);
		else return new CheckFiles(false, missingFiles + "</html>");
	}

	@Override
	public BuildConfig buildConfig(String path) {
		return (currentConfig = new Config(path, appname + ".ini"));
	}

	@Override
	public BuildGame getGame(String[] args) {
		if(isDemo)
			Console.Println("Demo version detected");
		
		return new Main(getConfig(), appname, sversion, true, isDemo);
	}
}
