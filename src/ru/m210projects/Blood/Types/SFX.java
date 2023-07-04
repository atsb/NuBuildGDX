// This file is part of BloodGDX.
// Copyright (C) 2017-2019  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// BloodGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// BloodGDX is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with BloodGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.Blood.Types;

import static ru.m210projects.Build.StringUtils.*;

import java.nio.ByteBuffer;

import ru.m210projects.Build.FileHandle.Resource;
import ru.m210projects.Build.Types.LittleEndian;

public class SFX {
	public int relVol;
	public int pitch;
	public int pitchrange;
	public int format;
	public int loopStart;
	public String rawName;
	public ByteBuffer hResource;
	public int size;
	
	public SFX(Resource bb) {
    	relVol = bb.readInt();
    	pitch = bb.readInt();
    	pitchrange = bb.readInt();
    	format = bb.readInt();
    	if(format == -1) format = 0;
    	loopStart = bb.readInt();
    	rawName = bb.readString(bb.size() - bb.position());
    	rawName = toUnicode(rawName);
	}
	
	public SFX(int relVol, int pitch, int pitchrange, int format, int loopStart, String rawName) {
    	this.relVol = relVol;
    	this.pitch = pitch;
    	this.pitchrange = pitchrange;
    	if(format == -1) format = 0;
    	this.format = format;
    	this.loopStart = loopStart;
    	this.rawName = toUnicode(rawName);
	}
	
	public byte[] getBytes()
	{
		byte[] buf = new byte[21 + rawName.length()];
    	int ptr = 0;
		LittleEndian.putInt(buf, ptr, relVol); ptr += 4;
		LittleEndian.putInt(buf, ptr, pitch); ptr += 4;
		LittleEndian.putInt(buf, ptr, pitchrange); ptr += 4;
		LittleEndian.putInt(buf, ptr, format); ptr += 4;
		LittleEndian.putInt(buf, ptr, loopStart); ptr += 4;
		System.arraycopy(rawName.getBytes(), 0, buf, ptr, rawName.length());
		
		return buf;
	}
}
