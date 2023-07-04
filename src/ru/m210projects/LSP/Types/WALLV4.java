// This file is part of LSPGDX.
// Copyright (C) 2020  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// LSPGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// LSPGDX is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with LSPGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.LSP.Types;

import ru.m210projects.Build.FileHandle.Resource;
import ru.m210projects.Build.Types.WALL;

public class WALLV4 extends WALL {
	
	public WALLV4(Resource fil) {
		buildWall(fil);
	}

	@Override
	public void buildWall(Resource bb)
	{
		this.x = bb.readInt();
		this.y = bb.readInt();
		this.point2 = bb.readShort();
		this.cstat = (short) (bb.readByte() & 0xFF);
		this.shade = bb.readByte();
		this.xrepeat = (short) (bb.readByte() & 0xFF);
		this.yrepeat = (short) (bb.readByte() & 0xFF);
		this.xpanning = (short) (bb.readByte() & 0xFF);
		this.ypanning = (short) (bb.readByte() & 0xFF);
		this.picnum = bb.readShort();
		this.overpicnum = bb.readShort();
		this.nextsector = bb.readShort();
		this.nextwall = bb.readShort();
		bb.readShort(); // nextsector2
		bb.readShort(); // nextwall2
		this.lotag = bb.readShort();
		this.hitag = bb.readShort();
		this.extra = -1;
	}
}
