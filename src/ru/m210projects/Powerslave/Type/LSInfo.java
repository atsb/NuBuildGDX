// This file is part of PowerslaveGDX.
// Copyright (C) 2019  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// PowerslaveGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// PowerslaveGDX is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with PowerslaveGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.Powerslave.Type;

import ru.m210projects.Build.FileHandle.Resource;

public class LSInfo {
	public static final int size = 5;
	
	public int mode;
	public int level;
	public int best;
	
	public String info;
	public String date;
	public String addonfile;
	
	public void read(Resource bb)
	{
		mode = bb.readByte();
		level = bb.readShort();
		best = bb.readShort();
		update();
	}
	
	public void update()
	{
		info = "Mode: " + (mode == 1 ? "Classic" : "Free save") + ", Level: " + level + " / " + (best + 1);
	}
	
	public LSInfo clear()
	{
		mode = 0;
		level = 0;
		info = "Empty slot";
		date = null;
		addonfile = null;
		
		return this;
	}
}
