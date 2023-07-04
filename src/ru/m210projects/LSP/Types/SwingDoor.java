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

import ru.m210projects.Build.FileHandle.Resource;

public class SwingDoor {
	public int[] wall = new int[8];
	public int  sector;
	public int  angopen;
	public int  angclosed;
	public int  angopendir;
	public int  ang;
	public int  anginc;
	public int[] x = new int[8];
	public int[] y = new int[8];
	
	public void copy(SwingDoor src)
	{
		System.arraycopy(src.wall, 0, wall, 0, 8);

		sector = src.sector;
		angopen = src.angopen;
		angclosed = src.angclosed;
		angopendir = src.angopendir;
		ang = src.ang;
		anginc = src.anginc;
		
		System.arraycopy(src.x, 0, x, 0, 8);
		System.arraycopy(src.y, 0, y, 0, 8);
	}
	
	public void set(Resource bb) {
		for(int j = 0; j < 8; j++)
			wall[j] = bb.readShort();
		sector = bb.readShort();
		angopen = bb.readShort();
		angclosed = bb.readShort();
		angopendir = bb.readShort();
		ang = bb.readShort();
		anginc = bb.readShort();
		for(int j = 0; j < 8; j++)
			x[j] = bb.readInt();
		for(int j = 0; j < 8; j++)
			y[j] = bb.readInt();
	}
}
