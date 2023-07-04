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

public class BulletStruct {
	public static final int size = 32;
	
	public short baseSeq;
	public short frmOffset;
	public short nSprite;
	public short bull_6;
	public short bull_8;
	public short type;
	public short zang;
	public short bull_E;
	public short bull_10;
	public byte bull_12;
	public byte bull_13;
	public int xvec;
	public int yvec;
	public int zvec;
	
	public void save(ByteBuffer bb)
	{
		bb.putShort(baseSeq);
		bb.putShort(frmOffset);
		bb.putShort(nSprite);
		bb.putShort(bull_6);
		bb.putShort(bull_8);
		bb.putShort(type);
		bb.putShort(zang);
		bb.putShort(bull_E);
		bb.putShort(bull_10);
		bb.put(bull_12);
		bb.put(bull_13);
		bb.putInt(xvec);
		bb.putInt(yvec);
		bb.putInt(zvec);
	}
	
	public void load(Resource bb)
	{
		baseSeq = bb.readShort();
		frmOffset = bb.readShort();
		nSprite = bb.readShort();
		bull_6 = bb.readShort();
		bull_8 = bb.readShort();
		type = bb.readShort();
		zang = bb.readShort();
		bull_E = bb.readShort();
		bull_10 = bb.readShort();
		bull_12 = bb.readByte();
		bull_13 = bb.readByte();
		xvec = bb.readInt();
		yvec = bb.readInt();
		zvec = bb.readInt();
	}

	public void copy(BulletStruct src) {
		baseSeq = src.baseSeq;
		frmOffset = src.frmOffset;
		nSprite = src.nSprite;
		bull_6 = src.bull_6;
		bull_8 = src.bull_8;
		type = src.type;
		zang = src.zang;
		bull_E = src.bull_E;
		bull_10 = src.bull_10;
		bull_12 = src.bull_12;
		bull_13 = src.bull_13;
		xvec = src.xvec;
		yvec = src.yvec;
		zvec = src.zvec;
	}
}
