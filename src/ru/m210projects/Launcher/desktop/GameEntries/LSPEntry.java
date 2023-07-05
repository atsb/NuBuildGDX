//This file is part of BuildGDX.
//Copyright (C) 2017-2020  Alexander Makarov-[M210] (m210-2007@mail.ru)
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

import java.net.URL;

import ru.m210projects.Build.Pattern.BuildGame;
import ru.m210projects.Build.Settings.BuildConfig;
import ru.m210projects.LSP.Config;
import ru.m210projects.LSP.Main;

public abstract class LSPEntry extends GameEntry {

	public LSPEntry() {
		super("LSPGDX", appversion);
	}

	@Override
	public URL getLogo() {
		return this.getClass().getResource("/" +headerPath + "/headerlot7p.png");
	}

	@Override
	public URL getIcon16() {
		return this.getClass().getResource("/" +iconPath + "/Lot7P/lot7p16.png");
	}

	@Override
	public String[] getIcons() {
		String icons[] = {
				iconPath + "/Lot7P/lot7p16.png",
				iconPath + "/Lot7P/lot7p32.png",
				iconPath + "/Lot7P/lot7p128.png",
			};

			return icons;
	}

	@Override
	public String getResourceName() {
		return "Lot7P";
	}

	@Override
	public String getName() {
		return "Seven Paladins";
	}

	@Override
	public ResFile[] getResourceFiles() {
		ResFile[] resources = {
			new ResFile("l0art000.dat"),
			new ResFile("l1art000.dat"),
			new ResFile("lmart000.dat"),
			new ResFile("palette.dat"),
			new ResFile("tables.dat")
		};
		return resources;
	}

	@Override
	public BuildConfig buildConfig(String path) {
		return (currentConfig = new Config(path, appname + ".ini"));
	}

	@Override
	public BuildGame getGame(String[] args) {
		return new Main(getConfig(), appname, sversion);
	}
}
