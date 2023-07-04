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

public class GrowStruct {
	
	public static final int size = 8;
	
	public short field_0;
	public short field_2;
	public short field_4;
	public short field_6;
	
	public void save(ByteBuffer bb)
	{
		bb.putShort(field_0);
		bb.putShort(field_2);
		bb.putShort(field_4);
		bb.putShort(field_6);
	}
	
	public void load(Resource bb)
	{
		field_0 = bb.readShort();
		field_2 = bb.readShort();
		field_4 = bb.readShort();
		field_6 = bb.readShort();
	}

	public void copy(GrowStruct src) {
		field_0 = src.field_0;
		field_2 = src.field_2;
		field_4 = src.field_4;
		field_6 = src.field_6;
	}
}
