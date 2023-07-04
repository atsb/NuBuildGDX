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

import ru.m210projects.Build.FileHandle.Resource;

public class LSInfo {
	public int skill;
	public int episode;
	public int level;
	public String iniName;
	public String info;
	public String date;
	
	public void read(Resource bb)
	{
		skill = bb.readByte() + 1;
		episode = bb.readInt() + 1;
		level = bb.readInt() + 1;
		update();
	}
	
	public void update()
	{
		info = "Episode:" + episode + " / Level:" + level + " / Skill:" + (skill != 6 ? skill : "CM");
	}
	
	public LSInfo clear()
	{
		skill = 0;
		episode = 0;
		level = 0;
		iniName = null;
		info = "Empty slot";
		date = null;
		
		return this;
	}
}
