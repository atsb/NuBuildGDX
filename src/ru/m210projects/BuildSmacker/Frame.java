// This file is part of BuildSmacker.
// Copyright (C) 2020  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// BuildSmacker is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// BuildSmacker is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with BuildSmacker.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.BuildSmacker;

import java.nio.ByteBuffer;

public class Frame {
	
	public ByteBuffer buf;
	public int size;
	public byte flags;
	
	public Frame(int size)
	{
		this.size = size;
	}

}
