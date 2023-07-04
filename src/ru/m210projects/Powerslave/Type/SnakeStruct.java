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

public class SnakeStruct {
	
	public static final int size = 32;
	
	public short nTarget;
	public short[] nSprite = new short[8];
	public short field_10;
	public short nFunc = -1;
	public byte[] field_14 = new byte[8];
	public short zvec;
	
	public void save(ByteBuffer bb)
	{
		bb.putShort(nTarget);
		for(int i = 0; i < 8; i++)
			bb.putShort(nSprite[i]);
		bb.putShort(field_10);
		bb.putShort(nFunc);
		for(int i = 0; i < 8; i++)
			bb.put(field_14[i]);
		bb.putShort(zvec);
	}
	
	public void load(Resource bb)
	{
		nTarget = bb.readShort();
		for(int i = 0; i < 8; i++)
			nSprite[i] = bb.readShort();
		field_10 = bb.readShort();
		nFunc = bb.readShort();
		bb.read(field_14);
		zvec = bb.readShort();
	}

	public void copy(SnakeStruct src) {
		nTarget = src.nTarget;
		System.arraycopy(src.nSprite, 0, nSprite, 0, 8);
		field_10 = src.field_10;
		nFunc = src.nFunc;
		System.arraycopy(src.field_14, 0, field_14, 0, 8);
		zvec = src.zvec;
	}

}
