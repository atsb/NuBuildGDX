// This file is part of BloodGDX.
// Copyright (C) 2017-2019  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// BloodGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// BloodGDX is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with BloodGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.Blood.Types;

import java.io.StringReader;

import ru.m210projects.Build.FileHandle.FileEntry;
import ru.m210projects.Build.Pattern.Tools.IniFile;

public class BloodIniFile extends IniFile {
	
	private boolean pack;
	public BloodIniFile(byte[] data, String name, FileEntry file) {
		super(data, name, file);
	}

	public boolean isPackage()
	{
		return pack;
	}
	
	public void setPackage(boolean pack)
	{
		this.pack = pack;
	}
	
	public boolean initDef()
	{
		String l = new String(data, 0, data.length);
		if(!l.contains("\\\\")) l = l.replace("\\","\\\\");
		
		if(reader != null) 
			reader.close();
		reader = new StringReader(l);

		try {
			ini.clear();
			ini.load(reader);
		} catch (Exception e) { 
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
