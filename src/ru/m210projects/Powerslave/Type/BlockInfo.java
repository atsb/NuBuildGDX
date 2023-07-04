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

import java.nio.ByteBuffer;

import ru.m210projects.Build.FileHandle.Resource;

public class BlockInfo {
	
	public static final int size = 16;
	
	public int cx, cy;
	public int field_8;
	public short sprite;
	public short field_E;

	public void clear()
	{
		this.cx = 0;
		this.cy = 0;
		this.field_8 = 0;
		this.sprite = -1;
		this.field_E = 0;
	}
	
	public void save(ByteBuffer bb)
	{
		bb.putInt(cx);
		bb.putInt(cy);
		bb.putInt(field_8);
		bb.putShort(sprite);
		bb.putShort(field_E);
	}
	
	public void load(Resource bb)
	{
		cx = bb.readInt();
		cy = bb.readInt();
		field_8 = bb.readInt();
		sprite = bb.readShort();
		field_E = bb.readShort();
	}

	public void copy(BlockInfo src) {
		cx = src.cx;
		cy = src.cy;
		field_8 = src.field_8;
		sprite = src.sprite;
		field_E = src.field_E;
	}
}
