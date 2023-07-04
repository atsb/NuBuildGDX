// This file is part of BuildGDX.
// Copyright (C) 2017-2020  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// BuildGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// BuildGDX is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with BuildGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.Build.Architecture;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.graphics.glutils.HdpiMode;
import com.badlogic.gdx.utils.Array;

public class BuildConfiguration {

	public int x = -1, y = -1;
	public String title;
	public boolean vsync;
	public int width, height;
	public boolean fullscreen = false;
	public boolean resizable = false;
	public boolean undecorated = false;
	public int depth = 16; // z-buffer
	public int stencil = 8;
	public int backgroundFPS = 60;
	public int foregroundFPS = 60;
	public boolean borderless;
	
	public boolean useGL30 = false;
	/** enable HDPI mode on Mac OS X **/
	public boolean useHDPI = false;
	public HdpiMode hdpiMode;
	
	protected Array<String> iconPaths = new Array<String>();
	protected Array<FileType> iconFileTypes = new Array<FileType>();
	
	
	public void addIcon (String path, FileType fileType) {
		iconPaths.add(path);
		iconFileTypes.add(fileType);
	}
	
	public Array<String> getIconPaths()
	{
		return iconPaths;
	}
	
	public Array<FileType> getIconFileTypes()
	{
		return iconFileTypes;
	}
	
}
