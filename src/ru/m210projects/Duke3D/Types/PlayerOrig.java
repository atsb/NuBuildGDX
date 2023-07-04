// This file is part of DukeGDX.
// Copyright (C) 2019  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// DukeGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// DukeGDX is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with DukeGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.Duke3D.Types;

import ru.m210projects.Build.FileHandle.Resource;
import ru.m210projects.Build.Types.LittleEndian;

public class PlayerOrig {
	public static final int sizeof = 16;
	
	public int ox,oy,oz;
    public short oa,os;
    
    private final byte[] buf = new byte[sizeof];
    public byte[] getBytes()
    {
    	LittleEndian.putInt(buf, 0, ox);
    	LittleEndian.putInt(buf, 4, oy);
    	LittleEndian.putInt(buf, 8, oz);
    	LittleEndian.putShort(buf, 12, oa);
    	LittleEndian.putShort(buf, 14, os);
    	return buf;
    }
    
    public void set(Resource bb)
    {
    	ox = bb.readInt();
    	oy = bb.readInt();
    	oz = bb.readInt();
    	oa = bb.readShort();
    	os = bb.readShort();
    }
    
    public void copy(PlayerOrig src)
    {
    	ox = src.ox;
    	oy = src.oy;
    	oz = src.oz;
    	oa = src.oa;
    	os = src.os;
    }
}
