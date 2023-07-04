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

public class SwitchStruct {
	
	public static final int size = 20;
	
	public short field_0;
	public short nPause;
	public short nChannel;
	public short nLink;
	public short field_8;
	public short nSector;
	public short field_C;
	public short nWall;
	public short field_10;
	public short field_12;
//	public short field_14;
//	public short field_16;
//	public short field_18;
//	public short field_1A;
//	public short field_1C;
//	public short field_1E;
	
	public void clear()
	{
		this.field_0 = 0;
		this.nPause = 0;
		this.nChannel = 0;
		this.nLink = 0;
		this.field_8 = 0;
		this.nSector = 0;
		this.field_C = 0;
		this.nWall = 0;
		this.field_10 = 0;
		this.field_12 = 0;
//		this.field_14 = 0;
//		this.field_16 = 0;
//		this.field_18 = 0;
//		this.field_1A = 0;
//		this.field_1C = 0;
//		this.field_1E = 0;
	}
	
	public void save(ByteBuffer bb)
	{
		bb.putShort(field_0);
		bb.putShort(nPause);
		bb.putShort(nChannel);
		bb.putShort(nLink);
		bb.putShort(field_8);
		bb.putShort(nSector);
		bb.putShort(field_C);
		bb.putShort(nWall);
		
		bb.putShort(field_10);
		bb.putShort(field_12);
//		bb.putShort(field_14);
//		bb.putShort(field_16);
//		bb.putShort(field_18);
//		bb.putShort(field_1A);
//		bb.putShort(field_1C);
//		bb.putShort(field_1E);
	}
	
	public void load(Resource bb)
	{
		field_0 = bb.readShort();
		nPause = bb.readShort();
		nChannel = bb.readShort();
		nLink = bb.readShort();
		field_8 = bb.readShort();
		nSector = bb.readShort();
		field_C = bb.readShort();
		nWall = bb.readShort();
		
		field_10 = bb.readShort();
		field_12 = bb.readShort();
//		field_14 = bb.readShort();
//		field_16 = bb.readShort();
//		field_18 = bb.readShort();
//		field_1A = bb.readShort();
//		field_1C = bb.readShort();
//		field_1E = bb.readShort();
	}

	public void copy(SwitchStruct src) {
		field_0 = src.field_0;
		nPause = src.nPause;
		nChannel = src.nChannel;
		nLink = src.nLink;
		field_8 = src.field_8;
		nSector = src.nSector;
		field_C = src.field_C;
		nWall = src.nWall;
		
		field_10 = src.field_10;
		field_12 = src.field_12;
	}
}
