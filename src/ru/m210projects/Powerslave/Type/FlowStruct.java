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

public class FlowStruct {
	
	public static final int size = 28;
	
	public short nObject;
	public short nState;
	public int xpanning;
	public int ypanning;
	public int xvel;
	public int yvel;
	public int xmax;
	public int ymax;
	
	public void save(ByteBuffer bb)
	{
		bb.putShort(nObject);
		bb.putShort(nState);
		bb.putInt(xpanning);
		bb.putInt(ypanning);
		bb.putInt(xvel);
		bb.putInt(yvel);
		bb.putInt(xmax);
		bb.putInt(ymax);
	}
	
	public void load(Resource bb)
	{
		nObject = bb.readShort();
		nState = bb.readShort();
		xpanning = bb.readInt();
		ypanning = bb.readInt();
		xvel = bb.readInt();
		yvel = bb.readInt();
		xmax = bb.readInt();
		ymax = bb.readInt();
	}

	public void copy(FlowStruct src) {
		nObject = src.nObject;
		nState = src.nState;
		xpanning = src.xpanning;
		ypanning = src.ypanning;
		xvel = src.xvel;
		yvel = src.yvel;
		xmax = src.xmax;
		ymax = src.ymax;
	}
}
