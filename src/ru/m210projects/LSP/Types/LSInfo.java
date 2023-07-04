// This file is part of LSPGDX.
// Copyright (C) 2020  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// LSPGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// LSPGDX is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with LSPGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.LSP.Types;

import static ru.m210projects.LSP.Globals.maps;

import ru.m210projects.Build.FileHandle.Resource;

public class LSInfo {
	public int level;
	public int skill;
	public String info;
	public String date;

	public void read(Resource bb)
	{
		level = bb.readInt();
		skill = bb.readInt();
		update();
	}
	
	public void update()
	{
		int mnum = maps[level].num & 0xFF;
		int book = (mnum % 100) % 10;
		int chapter = mnum / 100;
		int verse = (mnum % 100) / 10;
		
		info = book + "b " + chapter +  "c " + verse + "v, skill: " + skill;
	}
	
	public void clear()
	{
		skill = 0;
		level = 0;
		info = "Empty slot";
		date = null;
	}
}
