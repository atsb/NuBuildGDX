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

public class BobStruct {
	
	public static final int size = 8;
	
	public short field_0;
	public byte field_2;
	public byte field_3;
	public int field_4;
	
	public void save(ByteBuffer bb)
	{
		bb.putShort(field_0);
		bb.put(field_2);
		bb.put(field_3);
		bb.putInt(field_4);
	}
	
	public void load(Resource bb)
	{
		field_0 = bb.readShort();
		field_2 = bb.readByte();
		field_3 = bb.readByte();
		field_4 = bb.readInt();
	}

	public void copy(BobStruct src) {
		field_0 = src.field_0;
		field_2 = src.field_2;
		field_3 = src.field_3;
		field_4 = src.field_4;
	}
}
