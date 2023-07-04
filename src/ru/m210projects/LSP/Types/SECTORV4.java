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

import static java.lang.Math.max;
import static java.lang.Math.min;

import ru.m210projects.Build.FileHandle.Resource;
import ru.m210projects.Build.Types.SECTOR;

public class SECTORV4 extends SECTOR {

	public SECTORV4(Resource fil) {
		buildSector(fil);
	}

	@Override
	public void buildSector(Resource bb)
	{
		this.wallptr = bb.readShort();
		this.wallnum = bb.readShort();

		this.ceilingstat = bb.readByte();
		this.ceilingxpanning = (short) (bb.readByte() & 0xFF);
		this.ceilingypanning = (short) (bb.readByte() & 0xFF);
		this.ceilingshade = bb.readByte();
		this.ceilingz = bb.readInt();
		this.ceilingpicnum = bb.readShort();
		this.ceilingheinum = (short) max(min(bb.readShort() << 5, 32767), -32768);

		this.floorstat = bb.readByte();
		this.floorxpanning = (short) (bb.readByte() & 0xFF);
		this.floorypanning = (short) (bb.readByte() & 0xFF);
		this.floorshade = bb.readByte();
		this.floorz = bb.readInt();
		this.floorpicnum = bb.readShort();
		this.floorheinum = (short) max(min(bb.readShort() << 5, 32767), -32768);
		this.lotag = bb.readShort();
		this.hitag = bb.readShort();
		this.extra = -1;
	}
}
