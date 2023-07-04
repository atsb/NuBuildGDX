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

import static ru.m210projects.Launcher.desktop.Main.appversion;
import static ru.m210projects.Launcher.desktop.Main.headerPath;
import static ru.m210projects.Launcher.desktop.Main.iconPath;

import java.net.URL;

import ru.m210projects.Build.Pattern.BuildGame;
import ru.m210projects.Build.Settings.BuildConfig;
import ru.m210projects.Duke3D.Config;
import ru.m210projects.Duke3D.Main;

public abstract class DukeEntry extends GameEntry {

	public DukeEntry() {
		super("DukeGDX", appversion);
	}

	@Override
	public URL getLogo() {
		return this.getClass().getResource("/" +headerPath + "/headerduke.png");
	}

	@Override
	public URL getIcon16() {
		return this.getClass().getResource("/" +iconPath + "/Duke3D/duke16.png");
	}

	@Override
	public String getResourceName() {
		return "Duke Nukem 3D";
	}

	@Override
	public ResFile[] getResourceFiles() {
		ResFile[] resources = {
			new ResFile("duke3d.grp")
		};
		return resources;
	}

	@Override
	public String[] getIcons() {
		String icons[] = {
			iconPath + "/Duke3D/duke16.png",
			iconPath + "/Duke3D/duke32.png",
			iconPath + "/Duke3D/duke128.png",
		};

		return icons;
	}

	@Override
	public BuildConfig buildConfig(String path) {
		return (currentConfig = new Config(path, appname + ".ini"));
	}

	@Override
	public BuildGame getGame(String[] args) {
		return new Main(getConfig(), appname, sversion, 0, true);
	}
}
