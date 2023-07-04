// This file is part of RedneckGDX.
// Copyright (C) 2017-2018  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// RedneckGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// RedneckGDX is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with RedneckGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.Redneck;

import static ru.m210projects.Redneck.Globals.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ru.m210projects.Build.Pattern.BuildNet.NetInput;
import ru.m210projects.Build.Types.LittleEndian;

public class Input implements NetInput {
	
	private static final int sizeof = 10;
	private static final int gdxsizeof = 17;
	
	public float avel;
	public float horz;
	public short fvel, svel;
	public int bits;
	public byte carang;
	
	public Input(){}
	public Input(Object data, int nVersion)
	{
		setBytes(data, nVersion);
	}
	
	public int setBytes(Object data, int nVersion, int... offs) {
		int offset = 0;
		if(offs.length == 1) offset = offs[0];
		
		ByteBuffer bb = null;
		if(data instanceof byte[]) {
			bb = ByteBuffer.wrap((byte[])data);
			bb.order(ByteOrder.LITTLE_ENDIAN); 
			bb.position(offset);
		} else if(data instanceof ByteBuffer)
			bb = (ByteBuffer) data;
		else return offset;
		
		if(nVersion < GDXBYTEVERSION) 
		{
			avel = bb.get();
			horz = bb.get();
		} else {
			avel = bb.getFloat();
			horz = bb.getFloat();
			carang = bb.get();
		}

		fvel = bb.getShort();
		svel = bb.getShort();
		
		bits = bb.getInt();  
		
		return bb.position();
	}
	
	private int bufferVersion;
	private ByteBuffer InputBuffer;
	public byte[] getBytes(int nVersion)
	{
		if(InputBuffer == null || nVersion != bufferVersion) {
			InputBuffer = ByteBuffer.allocate(sizeof(nVersion)); 
			bufferVersion = nVersion;
		} else InputBuffer.clear();
		InputBuffer.order(ByteOrder.LITTLE_ENDIAN); 
		
		if(nVersion < GDXBYTEVERSION) {
			InputBuffer.put((byte)avel);
			InputBuffer.put((byte)horz);
		} else {
			InputBuffer.putFloat(avel);
			InputBuffer.putFloat(horz);
			InputBuffer.put(carang);
		}
		
		InputBuffer.putShort(fvel);
		InputBuffer.putShort(svel);

		InputBuffer.putInt(bits);
	
		return InputBuffer.array();
	}
	
	public static int sizeof(int nVersion)
	{
		int size = sizeof;
		if(nVersion >= GDXBYTEVERSION) size = gdxsizeof;
		return size;
	}
	
	@Override
	public int GetInput(byte[] packbuf, int offset, NetInput oldInput) {
		
		int k = ((packbuf[offset++] & 0xFF) << 8) | packbuf[offset++] & 0xFF;
		Copy(oldInput);
    
		if ((k&1) != 0) { fvel = LittleEndian.getShort(packbuf, offset); offset += 2; }
		if ((k&2) != 0) { svel = LittleEndian.getShort(packbuf, offset); offset += 2; }
        if ((k&4) != 0) { avel = LittleEndian.getFloat(packbuf, offset); offset += 4; }
        
        if ((k&8) != 0)  { bits = ((bits&0xffffff00)|(packbuf[offset++]&0xFF)); }
        if ((k&16) != 0) { bits = ((bits&0xffff00ff)|(packbuf[offset++]&0xFF)<<8); }
        if ((k&32) != 0) { bits = ((bits&0xff00ffff)|(packbuf[offset++]&0xFF)<<16); }
        if ((k&64) != 0) { bits = ((bits&0x00ffffff)|(packbuf[offset++]&0xFF)<<24); }
        if ((k&128) != 0) { horz = LittleEndian.getFloat(packbuf, offset); offset += 4; }
        if ((k&256) != 0) { carang = packbuf[offset++]; }
        
		return offset;
	}
	
	@Override
	public int PutInput(byte[] packbuf, int offset, NetInput oldInput) {
		int syncptr = offset;
		packbuf[offset++] = 0;
		packbuf[offset++] = 0;
		
		Input osyn = (Input) oldInput;

        if (fvel != osyn.fvel)
        {
        	LittleEndian.putShort(packbuf, offset, fvel);
        	offset += 2;
            packbuf[syncptr + 1] |= 1;
        }
        if (svel != osyn.svel)
        {
        	LittleEndian.putShort(packbuf, offset, svel);
        	offset += 2;
            packbuf[syncptr + 1] |= 2;
        }
        if (avel != osyn.avel)
        {
        	LittleEndian.putFloat(packbuf, offset, avel);
        	offset += 4;
            packbuf[syncptr + 1] |= 4;
        }
        if (((bits^osyn.bits)&0x000000ff) != 0) { packbuf[offset++] = (byte) (bits&255); packbuf[syncptr + 1] |= 8; }
        if (((bits^osyn.bits)&0x0000ff00) != 0) { packbuf[offset++] = (byte) ((bits>>8)&255); packbuf[syncptr + 1] |= 16; }
        if (((bits^osyn.bits)&0x00ff0000) != 0) { packbuf[offset++] = (byte) ((bits>>16)&255); packbuf[syncptr + 1] |= 32; }
        if (((bits^osyn.bits)&0xff000000) != 0) { packbuf[offset++] = (byte) ((bits>>24)&255); packbuf[syncptr + 1] |= 64; }
        if (horz != osyn.horz)
        {
            LittleEndian.putFloat(packbuf, offset, horz);
            offset += 4;
            packbuf[syncptr + 1] |= 128;
        }
        
        if(carang != osyn.carang) {
        	packbuf[offset++] = carang; packbuf[syncptr] |= 1;
        }

		return offset;
	}

	@Override
	public void Reset() {
		this.fvel = 0;
		this.svel = 0;
		this.avel = 0;
		this.bits = 0;
		this.horz = 0;
		this.carang = 0;
	}
	@Override
	public NetInput Copy(NetInput netsrc) {
		if(netsrc == null) return null;
		
		Input src = (Input) netsrc;
		
		this.fvel = src.fvel;
		this.svel = src.svel;
		this.avel = src.avel;
		this.bits = src.bits;
		this.horz = src.horz;
		this.carang = src.carang;
		return this;
	}
}
