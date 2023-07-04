//This file is part of BuildGDX.
//Copyright (C) 2019  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
//BuildGDX is free software: you can redistribute it and/or modify
//it under the terms of the GNU General Public License as published by
//the Free Software Foundation, either version 3 of the License, or
//(at your option) any later version.
//
//BuildGDX is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU General Public License for more details.
//
//You should have received a copy of the GNU General Public License
//along with BuildGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.Build.FileHandle;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class DataResource extends GroupResource {

	private static final byte[] tmp = new byte[1024];

	public DataResource(byte[] data) {
		super(null);
		initBuffer(data);
	}
	
	public DataResource(Group parent, String filename, int fileid, byte[] data) {
		super(parent);
		
		this.handleName(filename);
		this.fileid = fileid;
		initBuffer(data);
	}
	
	private void initBuffer(byte[] data)
	{
		if(data != null) {
			buffer = ByteBuffer.allocateDirect(data.length);
			buffer.order(ByteOrder.LITTLE_ENDIAN);
			buffer.put(data).rewind();
			this.size = data.length;
		}
	}
	
	@Override
	public void toMemory() { buffer.rewind(); }
	
	@Override
	public void flush() { /* nothing */ }

	@Override
	public void close() { synchronized(parent != null ? parent : this) { buffer.rewind(); } }

	@Override
	public int seek(long offset, Whence whence) {
		synchronized(parent != null ? parent : this) {
			switch (whence)
	        {
	        	case Set: buffer.position((int) offset); break;
	        	case Current: buffer.position(buffer.position() + (int)offset); break;
	        	case End: buffer.position(size + (int) offset);  break;
	        }
	        return position();
		}
	}

	@Override
	public int read(byte[] buf, int offset, int len) {
		synchronized(parent != null ? parent : this) {
			if(position() >= size) 
				return -1;
			
			len = Math.min(len, size - position());
			buffer.get(buf, offset, len);
			return len;
		}
	}
	
	@Override
	public int read(byte[] buf) {
		synchronized(parent != null ? parent : this) {
			return read(buf, 0, buf.length);
		}
	}
	
	@Override
	public int read(ByteBuffer bb, int offset, int len) {
		synchronized(parent != null ? parent : this) {
			if(position() >= size) 
				return -1;
			
			int var;
			bb.position(offset);
			int p = 0;
			while(len > 0)
			{
				if((var = read(tmp, 0, Math.min(len, tmp.length))) == -1)
					return p;
				bb.put(tmp, 0, var);
				len -= var;
				p += var;
			}
			return len;
		}
	}
	
	@Override
	public Byte readByte() {
		synchronized(parent != null ? parent : this) {
			return buffer.get();
		}
	}
	
	@Override
	public Boolean readBoolean() {
		Byte var = readByte();
		if(var != null)
			return var == 1;
		return null;
	}
	
	@Override
	public Short readShort() {
		synchronized(parent != null ? parent : this) {
			return buffer.getShort();
		}
	}

	@Override
	public Integer readInt() {
		synchronized(parent != null ? parent : this) {
			return buffer.getInt();
		}
	}
	
	@Override
	public Long readLong() {
		synchronized(parent != null ? parent : this) {
			return buffer.getLong();
		}
	}
	
	@Override
	public Float readFloat() {
		synchronized(parent != null ? parent : this) {
			return buffer.getFloat();
		}
	}

	@Override
	public String readString(int len)
	{
		synchronized(parent != null ? parent : this) {
			byte[] data = new byte[len];
			if(read(data) != len)
				return null;
			
			return new String(data);
		}
	}
	
	@Override
	public int position() {
		synchronized(parent != null ? parent : this) {
			return buffer.position();
		}
	}

	@Override
	public byte[] getBytes() {
		synchronized(parent != null ? parent : this) {
			byte[] data = new byte[buffer.capacity()];
			buffer.rewind();
			buffer.get(data);
			return data;
		}
	}

	@Override
	public boolean isClosed() {
		synchronized(parent != null ? parent : this) {
			return buffer == null;
		}
	}

	@Override
	public int remaining() {
		synchronized(parent != null ? parent : this) {
			return buffer.remaining();
		}
	}

	@Override
	public boolean hasRemaining() {
		synchronized(parent != null ? parent : this) {
			return buffer.hasRemaining();
		}
	}
}
