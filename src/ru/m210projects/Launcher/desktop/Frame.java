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

package ru.m210projects.Launcher.desktop;

import java.io.File;
import javax.swing.UIManager;

import ru.m210projects.Build.Settings.BuildConfig;
import ru.m210projects.Launcher.desktop.GameEntries.GameEntry;

public class Frame {

	public boolean forcePortable = false;

	private int currentEntry;
	private JLauncher frame;

	public Frame(String path, boolean showSettings, GameEntry... entry) {
		if(checkPortable(path, showSettings, entry))
			return;

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			//UIManager.setLookAndFeel( new FlatDarkLaf() );
		} catch (Throwable e) {}

		frame = new JLauncher("NuBuildGDX " + Main.appversion, currentEntry, entry);
		frame.start();

		if(currentEntry != -1)
			frame.showEntry(entry[currentEntry]);
	}

	public void dispose()
	{
		if(frame != null)
			frame.dispose();
	}

	public JLauncher getFrame()
	{
		return frame;
	}

	private boolean checkPortable(String path, boolean showSettings, GameEntry... entry)
	{
		String currentPath = (path != null) ? path : GameEntry.getDirPath();
		currentEntry = -1;

		// search game in current directory
		if (checkPortablePath(currentPath)) { // if can't write to current directory, then can't use game in portable mode
			for (int i = 0; i < entry.length; i++) {
				if (entry[i].checkResources(currentPath, false).getValue()) {
					currentEntry = i;
					break;
				}
			}
		}

		if(currentEntry != -1) {
			BuildConfig currentConfig = entry[currentEntry].buildConfig(currentPath);
			currentConfig.path = currentPath;

			if(!currentConfig.startup || (path != null && !showSettings))
			{
				entry[currentEntry].startGame(currentPath);
				return true;
			}
		}

		return false;
	}

    private boolean checkPortablePath(String path) {
		File directory = new File(path);
		File[] fList = directory.listFiles();
		if (fList != null) {
			for (File file : fList) {
				if(file.getName().toLowerCase().equals("portable"))
					forcePortable = true;
				return true;
			}
		}

		File f = new File(path + "tmp_check.write");
		if (f.exists())
			return f.delete();
		try {
			if (f.createNewFile())
				return f.delete();
		} catch (Exception e) {
		}
		return false;
	}
}