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

import static ru.m210projects.Build.OnSceenDisplay.Console.OSDTEXT_RED;
import static ru.m210projects.Build.Strhandler.toLowerCase;

import java.io.File;
import java.net.URL;
import java.util.HashMap;

import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Build.Pattern.BuildGame;
import ru.m210projects.Build.Settings.BuildConfig;
import ru.m210projects.Launcher.desktop.CheckFiles;

public abstract class GameEntry {

	public final String appname;
	public final String sversion;

	protected BuildConfig currentConfig;
	protected String homePath;

	protected static String currentPath;

	public GameEntry(String appname, String sversion)
	{
		this.appname = appname;
		this.sversion = sversion;
	}

	public abstract URL getLogo();

	public abstract URL getIcon16();

	public abstract String getResourceName();

	public abstract ResFile[] getResourceFiles();

	public abstract String[] getIcons();

	public static String getDirPath() {
		if(currentPath != null)
			return currentPath;

		currentPath = System.getProperty("user.dir") + File.separator;
		String OS = System.getProperty("os.name");
		if ((OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0)) {
			String parent = System.getProperty("java.class.path");
			if (parent != null)
				currentPath = new File(parent).getAbsolutePath() + File.separator;
		}

		return currentPath;
	}

	public String getHomePath() {
		if(homePath != null)
			return homePath;

		homePath = System.getProperty("user.home") + File.separator + "M210Projects" + File.separator + appname + File.separator;
		File f = new File(homePath);
        if(!f.exists() && !f.mkdirs() && !f.isDirectory())
        	Console.Println("Can't create path \"" + homePath + '"', OSDTEXT_RED);

        return homePath;
    }

	public CheckFiles checkResources(String path, boolean errorMessage) {
		File directory = new File(path);

		File[] fList = directory.listFiles();
		if(fList == null)
			return new CheckFiles(false, null);

		HashMap<String, File> filesMap = new HashMap<String, File>();
		for (File file : fList)
			filesMap.put(toLowerCase(file.getName()), file);

		/*ResFile[] resources = getResourceFiles();
			for (int i = 0; i < resources.length; i++) {
				if (filesMap.get(resources[i].name) == null) {
					if(errorMessage) Console.Println(appname + " error: " + resources[i].name + " is missing!");
					return new CheckFiles(false,resources[i].name.toUpperCase() + " is missing");
				}
			}*/

		String missingFiles = "<html>";
		boolean filesFound = true;

		ResFile[] resources = getResourceFiles();
		for (int i = 0; i < resources.length; i++) {
			if (filesMap.get(resources[i].name) == null) {
				if(errorMessage) Console.Println(appname + " error: " + resources[i].name + " is missing!");
				missingFiles = missingFiles + resources[i].name.toUpperCase() + " is missing<br>";
				filesFound = false;
			}
		}
		if (filesFound == false) return new CheckFiles(false,missingFiles + "</html>");
		else return new CheckFiles(true, null);
	}

	public BuildConfig getConfig()
	{
		if(currentConfig == null) {
			buildConfig(getHomePath());
		}

		return currentConfig;
	}

	public void setConfig(BuildConfig cfg)
	{
		currentConfig = cfg;
	}

	public String getName() {
		return getResourceName();
	}

	public abstract BuildConfig buildConfig(String path);

	public abstract BuildGame getGame(String[] args);

	public abstract void startGame(String gamePath);

}
