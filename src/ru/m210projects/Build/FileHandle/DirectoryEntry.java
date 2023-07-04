// This file is part of BuildGDX.
// Copyright (C) 2017-2018  Alexander Makarov-[M210] (m210-2007@mail.ru)
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

package ru.m210projects.Build.FileHandle;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.FileHandle.Compat.Path;

import static ru.m210projects.Build.Strhandler.toLowerCase;

public class DirectoryEntry {
	private final HashMap<String, DirectoryEntry> subDirectory;
	private final HashMap<String, FileEntry> files;
	private final String name;
	private String relativePath;
	private final String absolutePath;
	private DirectoryEntry parentDir;
	private boolean inited;

	private static HashMap<String, DirectoryEntry> cache;

	protected DirectoryEntry(String name, String Path) {
		this.files = new HashMap<String, FileEntry>();
		this.subDirectory = new HashMap<String, DirectoryEntry>();
		this.name = name;
		if (Path != null)
			this.relativePath = getRelativePath(Path);
		this.absolutePath = Path;
	}

	public void addFile(File file) {
		files.put(toLowerCase(file.getName()), new FileEntry(file, this, getRelativePath(file.getAbsolutePath())));
	}

	public DirectoryEntry addDirectory(String name, String relPath) {
		DirectoryEntry added = new DirectoryEntry(name, relPath);
		subDirectory.put(toLowerCase(name), added);
		return added;
	}

	public String getAbsolutePath() {
		return absolutePath;
	}

	public String getRelativePath() {
		return relativePath;
	}

	public DirectoryEntry getParent() {
		return parentDir;
	}

	public FileEntry checkFile(String filepath) {
		if (filepath == null)
			return null;

		filepath = toLowerCase(filepath);
		DirectoryEntry dir = this;
		while (filepath.indexOf(File.separator) != -1) {
			int index = filepath.indexOf(File.separator);
			String folder = filepath.substring(0, index);
			if (dir == null)
				return null;
			dir = dir.checkDirectory(folder);
			filepath = filepath.substring(index + 1);
		}
		if (dir == null)
			return null;
		return dir.files.get(filepath);
	}

	public DirectoryEntry checkDirectory(String dirpath) {
		dirpath = toLowerCase(dirpath);
		DirectoryEntry dir = this;
		while (dirpath.indexOf(File.separator) != -1) {
			int index = dirpath.indexOf(File.separator);
			String folder = dirpath.substring(0, index);
			if (dir == null)
				return null;
			dir = dir.checkDirectory(folder);
			dirpath = dirpath.substring(index + 1);
		}
		if (dir == null)
			return null;
		DirectoryEntry subDir = dir.subDirectory.get(dirpath);
		if (subDir != null)
			subDir.InitDirectory(subDir.getAbsolutePath());
		return subDir;
	}

	public HashMap<String, FileEntry> getFiles() {
		return files;
	}

	public HashMap<String, DirectoryEntry> getDirectories() {
		return subDirectory;
	}

	public List<FEntryResource> getList() {
		List<FEntryResource> list = new ArrayList<FEntryResource>();
		for (Iterator<FileEntry> it = getFiles().values().iterator(); it.hasNext();) {
			FileEntry file = it.next();
			list.add(new FEntryResource(file));
		}
		return list;
	}

	public String getName() {
		return name;
	}

	protected static DirectoryEntry init(String mainpath, String userpath) {
		if (cache == null)
			cache = new HashMap<String, DirectoryEntry>();
		else
			cache.clear();

		String dirName = "<main>";
		DirectoryEntry dir = new DirectoryEntry(dirName, null);
		cache.put(dirName, dir);
		dir.InitDirectory(mainpath);

		DirectoryEntry user = dir.addDirectory("<userdir>", userpath);
		user.InitDirectory(userpath);

		return dir;
	}

	public boolean checkCacheList() {
		if (!inited)
			return false;
		int currentSize = files.size() + subDirectory.size();
		boolean isMain = this == BuildGdx.compat.getDirectory(Path.Game);
		File directory;

		if (isMain) {
			directory = new File(Path.Game.getPath());
			currentSize--; // because <userdir>
		} else
			directory = new File(absolutePath);

		File[] fList = directory.listFiles();
		if (fList != null && currentSize != fList.length) {
			DirectoryEntry userdir = null;
			if (isMain)
				userdir = BuildGdx.compat.getDirectory(Path.User);

			subDirectory.clear();
			files.clear();

			for (File file : fList) {
				if (file.isFile()) {
					addFile(file);
				} else {
					DirectoryEntry subDir = addDirectory(file.getName(), file.getAbsolutePath());
					subDir.parentDir = this;
				}
			}

			if (isMain)
				subDirectory.put("<userdir>", userdir);
			return currentSize != (files.size() + subDirectory.size() - 1);
		}

		return false;
	}

	public void InitDirectory(String directoryPath) {
		if (inited)
			return;
		File directory = new File(directoryPath);
		File[] fList = directory.listFiles();
		if (fList != null) {
			for (File file : fList) {
				if (file.isFile())
					addFile(file);
				else {
					DirectoryEntry subDir = addDirectory(file.getName(), file.getAbsolutePath());
					subDir.parentDir = this;
				}
			}
		}
		inited = true;
	}

	@Override
	public String toString() {
		StringBuilder out = new StringBuilder("Directory name: " + getName() + "\r\n");
		out.append("\r\nSubDirectories: \r\n");
		for (Iterator<String> it = getDirectories().keySet().iterator(); it.hasNext();) {
			String dir = it.next();
			out.append("\t").append(dir).append("\r\n");
		}
		out.append("\r\nFiles: \r\n");
		for (Iterator<FileEntry> it = getFiles().values().iterator(); it.hasNext();) {
			FileEntry file = it.next();
			out.append("\t").append(file.getFile().getName()).append("\r\n");
		}

		return out.toString();
	}

	public boolean isInited() {
		return inited;
	}

	private String getRelativePath(String path) {
		String mainpath = Path.Game.getPath();
		if (name.equals("<userdir>"))
			mainpath = Path.User.getPath();

		if (path.length() > mainpath.length()) {
			int i;
			for (i = 0; i < mainpath.length(); i++)
				if (mainpath.charAt(i) != path.charAt(i))
					break;
			if (i == 0)
				return null; // not match
			path = path.substring(i);
		} else if (mainpath.startsWith(path))
			return null;

		return toLowerCase(path);
	}
}
