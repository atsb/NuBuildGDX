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

public class FishChunk {
	public static final int size = 6;
	
	public short nSprite;
	public short nSeq;
	public short ActionSeq;
	
	public void save(ByteBuffer bb)
	{
		bb.putShort(nSprite);
		bb.putShort(nSeq);
		bb.putShort(ActionSeq);
	}
	
	public void load(Resource bb)
	{
		nSprite = bb.readShort();
		nSeq = bb.readShort();
		ActionSeq = bb.readShort();
	}

	public void copy(FishChunk src) {
		nSprite = src.nSprite;
		nSeq = src.nSeq;
		ActionSeq = src.ActionSeq;
	}
}
