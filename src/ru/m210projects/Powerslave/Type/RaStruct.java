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

public class RaStruct {
	public static final int size = 16;
	
	public short nState;
	public short nSeq;
	public short nFunc;
	public short nSprite;
	public short nTarget;
	public short field_A;
	public short field_C;
	public short nPlayer;
	
	public void save(ByteBuffer bb)
	{
		bb.putShort(nState);
		bb.putShort(nSeq);
		bb.putShort(nFunc);
		bb.putShort(nSprite);
		bb.putShort(nTarget);
		bb.putShort(field_A);
		bb.putShort(field_C);
		bb.putShort(nPlayer);
	}
	
	public void load(Resource bb)
	{
		nState = bb.readShort();
		nSeq = bb.readShort();
		nFunc = bb.readShort();
		nSprite = bb.readShort();
		nTarget = bb.readShort();
		field_A = bb.readShort();
		field_C = bb.readShort();
		nPlayer = bb.readShort();
	}

	public void copy(RaStruct src) {
		nState = src.nState;
		nSeq = src.nSeq;
		nFunc = src.nFunc;
		nSprite = src.nSprite;
		nTarget = src.nTarget;
		field_A = src.field_A;
		field_C = src.field_C;
		nPlayer = src.nPlayer;
	}
}
