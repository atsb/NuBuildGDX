// This file is part of DukeGDX.
// Copyright (C) 2019  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// DukeGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// DukeGDX is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with DukeGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.Duke3D.Types;

public class ANIMATION {
	
	public static final int WALLX = 1;
	public static final int WALLY = 1 << 1;
	public static final int FLOORZ = 1 << 2;
	public static final int CEILZ = 1 << 3;
	
	public Object ptr;
	public short id;
	public byte type;
	public int goal;
	public int vel;
	public int sect;
	
	public ANIMATION copy(ANIMATION src)
	{
		this.ptr = src.ptr;
		this.id = src.id;
		this.type = src.type;
		this.goal = src.goal;
		this.vel = src.vel;
		this.sect = src.sect;
		
		return this;
	}
	
}
