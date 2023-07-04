//This file is part of BuildGDX.
//Copyright (C) 2019  Alexander Makarov-[M210] (m210-2007@mail.ru)
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

package ru.m210projects.Build.FileHandle;

import static ru.m210projects.Build.OnSceenDisplay.Console.OSDTEXT_RED;
import static ru.m210projects.Build.Strhandler.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import ru.m210projects.Build.FileHandle.FileResource.Mode;
import ru.m210projects.Build.OnSceenDisplay.Console;

public class Compat {

	public class CacheList {
		private final DirectoryEntry entry;

		public CacheList(String path, String userpath) {
			entry = DirectoryEntry.init(path, userpath);
		}

		protected DirectoryEntry getDirectory() {
			return entry;
		}

		protected FileEntry checkFile(String filepath) {
			return entry.checkFile(filepath);
		}

		protected DirectoryEntry checkDirectory(String dirpath) {
			return entry.checkDirectory(dirpath);
		}

		@Override
		public String toString() {
			return entry.toString();
		}
	}

	private final boolean debug = false;
	private final int MAXOPENFILES = 64;

	private final FileResource[] list = new FileResource[MAXOPENFILES];
	private int pos = 0;

	private CacheList cache;

	public enum Path {
		Game, User, Absolute;

		String path;

		public String getPath() {
			return path;
		}

		protected void setPath(String path) {
			this.path = FileUtils.getCorrectPath(path);
		}
	}

	public Compat(String mainPath, String userPath) {
		Path.Game.setPath(mainPath);
		Path.User.setPath(userPath);
		for (int i = 0; i < MAXOPENFILES; i++)
			list[i] = new FileResource();
	}

	protected CacheList getCacheList() {
		if (cache == null)
			cache = new CacheList(Path.Game.getPath(), Path.User.getPath());
		return cache;
	}

	public FileEntry checkFile(String filepath) {
		return getCacheList().checkFile(filepath);
	}

	public DirectoryEntry checkDirectory(String dirpath) {
		return getCacheList().checkDirectory(dirpath);
	}

	public DirectoryEntry getDirectory(Path folder) {
		if (folder == Path.Game)
			return getCacheList().getDirectory();
		else if (folder == Path.User)
			return checkDirectory("<userdir>");

		return null;
	}

	public File checkFile(String name, Path folder) {
		FileEntry entry = null;
		switch (folder) {
		case Game:
			entry = checkFile(name);
			break;
		case User:
			entry = getDirectory(Path.User).checkFile(new File(toLowerCase(name)).getName());
			break;
		case Absolute:
			File file = new File(toLowerCase(name));
			if (file.exists())
				return file;
			break;
		}

		if (entry != null)
			return entry.getFile();

		return null;
	}

	private FileResource obtain() {
		FileResource res;
		for (int i = 0; i < MAXOPENFILES; i++) {
			res = list[(pos = (pos + 1) & (MAXOPENFILES - 1))];
			if (res.isClosed())
				return res;
		}

		Console.Println("TOO MANY FILES OPEN!", OSDTEXT_RED);
		return null;
	}

	public List<FileResource> getOpened() {
		List<FileResource> out = new ArrayList<FileResource>();
		for (int i = 0; i < MAXOPENFILES; i++) {
			if (!list[i].isClosed())
				out.add(list[i]);
		}

		return out;
	}

	public FileResource open(FileEntry fil) {
		FileResource res = obtain();
		if (res != null)
			res = res.open(fil.getFile(), Mode.Read);

		return res;
	}

	public FileResource open(String name) {
		return open(name, Path.Game, Mode.Read);
	}

	public FileResource open(String name, Path folder, Mode mode) {
		File fil;
		FileResource res = null;

		switch (mode) {
		case Read:
			fil = checkFile(name, folder);
			if (fil != null) {
				res = obtain();
				if (res != null)
					res = res.open(fil, mode);
			}
			break;
		case Write:
			if (folder == Path.Game) {
				res = obtain();
				if (res != null) {
					fil = new File(Path.Game.getPath() + name);
					if ((res = res.open(fil, mode)) != null)
						getCacheList().getDirectory().addFile(fil);
				}
			} else if (folder == Path.User) {
				res = obtain();
				if (res != null) {
					fil = new File(Path.User.getPath() + name);
					if ((res = res.open(fil, mode)) != null)
						getDirectory(Path.User).addFile(fil);
				}
			} else {
				res = obtain();
				if (res != null)
					res = res.open(new File(name), mode);
			}
			break;
		}

		if (res != null && debug)
			System.out.println(
					"Opening " + name + " [ " + ((pos - 1) & (MAXOPENFILES - 1)) + " / " + MAXOPENFILES + " ]");

		return res;
	}

	public byte[] getBytes(FileEntry entry) {
		byte[] out = null;
		Resource res = open(entry);
		if (res != null) {
			out = res.getBytes();
			res.close();
		}

		return out;
	}
}
