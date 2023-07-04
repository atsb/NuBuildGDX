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

import java.io.File;

public class FileUtils {

	public static boolean isExtension(String filename, String ext) {
		return filename.endsWith("." + ext);
	}

	public static String getExtension(String filename) {
		if (filename == null || filename.isEmpty())
			return null;
		return filename.substring(filename.lastIndexOf('.') + 1);
	}
	
	public static String getNameWithoutExtension(String filename)
	{
		return filename.substring(0, filename.lastIndexOf('.'));
	}

	public static String getFullName(String path) {
		if (path != null) {
			int index;
			if ((index = path.lastIndexOf(File.separator)) != -1 || (index = path.lastIndexOf("\\")) != -1
					|| (index = path.lastIndexOf("/")) != -1)
				path = path.substring(index + 1);
		}

		return path;
	}

	public static String getCorrectPath(String path) {
		if (path != null) {
			String[] separators = { "/"/* - Linux separator */, "\\" };
			for (String separator : separators)
				if (!separator.equals(File.separator) && path.contains(separator))
					path = path.replace(separator, File.separator);
			path = path.replaceAll("(\\\\){2,}", "$1");
		}
		return path;
	}
}
