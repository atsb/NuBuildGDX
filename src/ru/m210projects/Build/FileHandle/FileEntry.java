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

import static ru.m210projects.Build.Strhandler.toLowerCase;

import java.io.File;

import ru.m210projects.Build.CRC32;
import ru.m210projects.Build.Pattern.Tools.NaturalComparator;

public class FileEntry implements Comparable<FileEntry> {
	private final File file;
	private final String extension;
	private final String relPath;
	private final DirectoryEntry parent;
	private long checksum;
	
	public FileEntry(File file, DirectoryEntry parent, String relPath)
	{
		this.file = file;
		this.parent = parent;
		String filename = file.getName();
		this.extension = toLowerCase(filename.substring(filename.lastIndexOf('.') + 1));
		this.relPath = relPath;
		this.checksum = -1;
	}
	
	public long getChecksum()
	{
		if(checksum == -1)
			checksum = CRC32.getChecksum(this);
		
		return checksum;
	}
	
	public File getFile()
	{
		return file;
	}
	
	public String getName()
	{
		return toLowerCase(file.getName());
	}
	
	public String getExtension()
	{
		return extension;
	}
	
	public String getPath()
	{
		return relPath;
	}
	
	public DirectoryEntry getParent()
	{
		return parent;
	}
	
	public String toString()
	{
		return file.getAbsolutePath() + ", Extension: " + extension;
	}

	@Override
	public int compareTo(FileEntry f) {
		
		String s1 = this.getName();
		String s2 = f.getName();
		
		return NaturalComparator.compare(s1, s2);
		
//		String t1 = s1.replaceAll("\\d", "");
//		String t2 = s2.replaceAll("\\d", "");
//		
//		int c = t1.compareTo(t2);
//		if(c != 0) return c;
//
//		String n1 = s1.replaceAll("\\D", "");
//		String n2 = s2.replaceAll("\\D", "");
//
//		if(!n1.isEmpty() && !n2.isEmpty()) 
//			return Integer.parseInt(n1) - Integer.parseInt(n2);
//
//		return 0;
	}
}
