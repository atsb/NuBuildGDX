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

public class MoveSectStruct {
	public static final int size = 16;
	
	public short field_0;
	public short field_2;
	public short field_4;
	public short field_6;
	public short field_8;
	public int field_A;
	public short field_E;
	
	public void save(ByteBuffer bb)
	{
		bb.putShort(field_0);
		bb.putShort(field_2);
		bb.putShort(field_4);
		bb.putShort(field_6);
		bb.putShort(field_8);
		bb.putInt(field_A);
		bb.putShort(field_E);
	}
	
	public void load(Resource bb)
	{
		field_0 = bb.readShort();
		field_2 = bb.readShort();
		field_4 = bb.readShort();
		field_6 = bb.readShort();
		field_8 = bb.readShort();
		field_A = bb.readInt();
		field_E = bb.readShort();
	}

	public void copy(MoveSectStruct bb) {
		field_0 = bb.field_0;
		field_2 = bb.field_2;
		field_4 = bb.field_4;
		field_6 = bb.field_6;
		field_8 = bb.field_8;
		field_A = bb.field_A;
		field_E = bb.field_E;
	}
}
