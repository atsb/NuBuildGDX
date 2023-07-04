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

public class GrenadeStruct {
	public static final int size = 32;
	
	public short field_0;
	public short field_2;
	public short nSprite;
	public short field_6;
	public short field_8;
	public short ActionSeq;
	public short field_C;
	public short field_E;
	public int field_10;
	public int xvel;
	public int yvel;
	public short field_1C;
	public short field_1E;
	
	public void save(ByteBuffer bb)
	{
		bb.putShort(field_0);
		bb.putShort(field_2);
		bb.putShort(nSprite);
		bb.putShort(field_6);
		bb.putShort(field_8);
		bb.putShort(ActionSeq);
		bb.putShort(field_C);
		bb.putShort(field_E);
		bb.putInt(field_10);
		bb.putInt(xvel);
		bb.putInt(yvel);
		bb.putShort(field_1C);
		bb.putShort(field_1E);
	}
	
	public void load(Resource bb)
	{
		field_0 = bb.readShort();
		field_2 = bb.readShort();
		nSprite = bb.readShort();
		field_6 = bb.readShort();
		field_8 = bb.readShort();
		ActionSeq = bb.readShort();
		field_C = bb.readShort();
		field_E = bb.readShort();
		field_10 = bb.readInt();
		xvel = bb.readInt();
		yvel = bb.readInt();
		field_1C = bb.readShort();
		field_1E = bb.readShort();
	}

	public void copy(GrenadeStruct src) {
		field_0 = src.field_0;
		field_2 = src.field_2;
		nSprite = src.nSprite;
		field_6 = src.field_6;
		field_8 = src.field_8;
		ActionSeq = src.ActionSeq;
		field_C = src.field_C;
		field_E = src.field_E;
		field_10 = src.field_10;
		xvel = src.xvel;
		yvel = src.yvel;
		field_1C = src.field_1C;
		field_1E = src.field_1E;
	}
}
