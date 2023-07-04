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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.FileHandle.Group;
import ru.m210projects.Build.FileHandle.GroupResource;
import ru.m210projects.Build.FileHandle.Resource;
import ru.m210projects.Build.FileHandle.Cache1D.PackageType;
import ru.m210projects.Build.OnSceenDisplay.Console;

public class LSPGroup extends Group {
	
	private static final byte[] tmp = new byte[1024];
	private Resource file = null;	
	
	private class LSPResource extends GroupResource {

		public int offset;
		public int pos;
		
		public LSPResource(int num, int offset, int size) {
			super(LSPGroup.this);
			
			this.handleName("file" + num);

			this.size = size;
			this.offset = offset;
			this.fileid = num;
			
			if(debug) 
				System.out.println("\t" + filenamext + ", offset: " + offset + ", size: " +  size);
		}
		
		@Override
		public void flush() {
			synchronized(parent) {
				super.flush();
				this.close();
			}
		}

		@Override
		public void close() {
			synchronized(parent) {
				pos = 0; 
			}
		}

		@Override
		public int seek(long offset, Whence whence) {
			synchronized(parent) {
				switch (whence)
		        {
		        	case Set: pos = (int) offset; break;
		        	case Current: pos += offset; break;
		        	case End: pos = size + (int) offset;  break;
		        }
				file.seek(this.offset + pos, Whence.Set);
		        return pos;
			}
		}

		@Override
		public int read(byte[] buf, int offs, int len) {
			synchronized(parent) {
				if(pos >= size) 
					return -1;
				
				len = Math.min(len, size - pos);
				int i = offset + pos;
				int groupfilpos = file.position();
				if (i != groupfilpos) 
					file.seek(i, Whence.Set);
	
				len = file.read(buf,offs,len);
				pos += len;
				return len;
			}
		}
		
		@Override
		public int read(byte[] buf) {
			synchronized(parent) {
				return read(buf, 0, buf.length);
			}
		}
		
		@Override
		public int read(ByteBuffer bb, int offset, int len) {
			synchronized(parent) {
				int var = -1;
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
			synchronized(parent) {
				int len = 1;
				if(len > size - pos)
					return null;
				
				int i = offset + pos;
				int groupfilpos = file.position();
				if (i != groupfilpos) 
					file.seek(i, Whence.Set);

				Byte out;
				if((out = file.readByte()) == null)
					return null;

				pos += len;
				return out;
			}
		}
		
		@Override
		public Short readShort() {
			synchronized(parent) {
				int len = 2;
				if(len > size - pos)
					return null;
				
				int i = offset + pos;
				int groupfilpos = file.position();
				if (i != groupfilpos) 
					file.seek(i, Whence.Set);

				Short out;
				if((out = file.readShort()) == null)
					return null;

				pos += len;
				return out;
			}
		}
		
		@Override
		public Integer readInt() {
			synchronized(parent) {
				int len = 4;
				if(len > size - pos)
					return null;
				
				int i = offset + pos;
				int groupfilpos = file.position();
				if (i != groupfilpos) 
					file.seek(i, Whence.Set);

				Integer out;
				if((out = file.readInt()) == null)
					return null;

				pos += len;
				return out;
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
		public Long readLong() {
			synchronized(parent) {
				int len = 8;
				if(len > size - pos)
					return null;
				
				int i = offset + pos;
				int groupfilpos = file.position();
				if (i != groupfilpos) 
					file.seek(i, Whence.Set);

				Long out;
				if((out = file.readLong()) == null)
					return null;

				pos += len;
				return out;
			}
		}

		@Override
		public Float readFloat() {
			Integer i = readInt();
			if(i != null)
				return Float.intBitsToFloat( i );
			return null;
		}
		
		@Override
		public String readString(int len) {
			synchronized(parent) {
				byte[] data;
				if(len < tmp.length)
					data = tmp;
				else data = new byte[len];
				if(read(data, 0, len) != len)
					return null;
				
				return new String(data, 0, len);
			}
		}

		@Override
		public int position() {
			synchronized(parent) {
				return pos;
			}
		}

		@Override
		public byte[] getBytes() {
			synchronized(parent) {
				int size = this.size();
				if(size > 0) {
					if(file.seek(offset, Whence.Set) == -1) {
						Console.Println("Error seeking to resource!");
						return null;
					}
					
					byte[] data = new byte[size];
					if(file.read(data) == -1) {
						Console.Println("Error loading resource!");
						return null;
					}
					return data;
				}
				return null;
			}
		}

		@Override
		public boolean isClosed() {
			synchronized(parent) {
				if(file != null)
					return file.isClosed();
				return true;
			}
		}

		@Override
		public int remaining() {
			return size() - position();
		}

		@Override
		public boolean hasRemaining() {
			return position() < size();
		}

		@Override
		public void toMemory() {
			synchronized(parent) {
				if(buffer == null) {
					if(file.seek(offset, Whence.Set) == -1) {
						Console.Println("Error seeking to resource!");
						return;
					}
					
					buffer = ByteBuffer.allocateDirect(size);
					buffer.order(ByteOrder.LITTLE_ENDIAN);
					file.read(buffer, 0, size);
				}
	
				buffer.rewind();
			}
		}
	}

	public LSPGroup(String filename, int num) throws Exception {
		this.file = BuildGdx.cache.open(filename, 0);
		if(this.file == null)
			throw new Exception(filename + " not found !");
		this.name = filename;
		this.type = PackageType.User;

		int[] offset = new int[num];
		byte[] buf = new byte[num * 4];
		file.read(buf);
		
		ByteBuffer.wrap(buf)
			.order(ByteOrder.LITTLE_ENDIAN)
			.asIntBuffer().get(offset);

		for(int i = 0; i < num; i++) {
			int len = file.size();
			if(i < num - 1) len = offset[i + 1];
			int siz = len - offset[i];
			if(siz > 0 && siz < file.size() && offset[i] > 0 && offset[i] < file.size())
				add(new LSPResource(i, offset[i], siz));
		}
		
		this.numfiles = filelist.size();
	}
	
	public GroupResource open(int fileid)
	{
		return open(fileid, "");
	}
	
	@Override
	protected boolean open(GroupResource res) {
		if(file == null) return false;
		
		LSPResource gres = (LSPResource) res;
		if(gres != null) {
			gres.pos = 0;
			return true;
		}
		
		return false;
	}

	@Override
	public int position() {
		if(file != null)
			return file.position();
		
		return -1;
	}

	@Override
	public void dispose() {
		super.dispose();
		if(file != null && !file.isClosed())
			file.close();
		file = null;
	}
}
