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

import ru.m210projects.Build.FileHandle.Compat.Path;

import static ru.m210projects.Build.Strhandler.toLowerCase;

import java.nio.ByteBuffer;

import ru.m210projects.Build.FileHandle.FileEntry;
import ru.m210projects.Build.FileHandle.FileResource.Mode;

public class UserGroup extends Group {
	
	private static final byte[] tmp = new byte[1024];
	
	public class UserResource extends GroupResource {

		public final FileEntry entry;
		public FileResource fil;
		public String absolutePath;
		
		public UserResource(String absolutePath, int fileid) {
			super(UserGroup.this);

			this.filenamext = toLowerCase(absolutePath);
			
			int point = filenamext.lastIndexOf('.');
			if(point != -1) {
				this.fileformat = filenamext.substring(point + 1);
				this.filename = filenamext.substring(0, point);
			} else {
				this.fileformat = "";
				this.filename = this.filenamext;
			}

			this.entry = null;
			this.fileid = fileid;
			this.fil = null;
			this.absolutePath = absolutePath;
		}
		
		public UserResource(FileEntry file, int fileid) {
			super(UserGroup.this);

			this.handleName(file.getName());

			this.entry = file;
			this.fileid = fileid;
			this.fil = null;
		}
		
		@Override
		public int size() {
			if(fil != null)
				return fil.size();
			
			return size;
		}

		@Override
		public void close() {
			synchronized(parent) {
				if(fil != null) {
					fil.close();
					
					if(entry != null) //else the fileresource with absolute path
						fil = null;
				}
			}
		}

		@Override
		public int seek(long offset, Whence whence) {
			synchronized(parent) {
				if(fil != null)
					return fil.seek(offset, whence);
				
				return -1;
			}
		}

		@Override
		public int read(byte[] buf, int offset, int len) {
			synchronized(parent) {
				if(fil != null)
					return fil.read(buf, offset, len);
				
				return -1;
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
				if(fil != null) {
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
				
				return -1;
			}
		}

		@Override
		public Byte readByte() {
			synchronized(parent) {
				if(fil != null)
					return fil.readByte();
				
				return null;
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
			synchronized(parent) {
				if(fil != null)
					return fil.readShort();
				
				return null;
			}
		}
		
		@Override
		public Integer readInt() {
			synchronized(parent) {
				if(fil != null)
					return fil.readInt();
				
				return null;
			}
		}
		
		@Override
		public Long readLong() {
			synchronized(parent) {
				if(fil != null)
					return fil.readLong();
				
				return null;
			}
		}
		
		@Override
		public Float readFloat() {
			synchronized(parent) {
				if(fil != null)
					return fil.readFloat();
				
				return null;
			}
		}
		
		@Override
		public String readString(int len)
		{
			synchronized(parent) {
				if(fil != null)
					return fil.readString(len);
				
				return null;
			}
		}

		@Override
		public int position() {
			synchronized(parent) {
				if(fil != null)
					return fil.position();
				
				return -1;
			}
		}

		@Override
		public void toMemory() {
			synchronized(parent) {
				if(isClosed())
					parent.open(this);
				
				if(fil != null)
					fil.toMemory();
			}
		}

		@Override
		public byte[] getBytes() {
			synchronized(parent) {
				if(isClosed())
					parent.open(this);
				
				if(fil != null)
					return fil.getBytes();
				
				return null;
			}
		}

		@Override
		public boolean isClosed() {
			if(fil != null)
				return fil.isClosed();
			return true;
		}

		@Override
		public int remaining() {
			return size() - position();
		}

		@Override
		public boolean hasRemaining() {
			return position() < size();
		}	
	}

	private final Compat compat;
	protected UserGroup(Compat compat)
	{
		this.compat = compat;
	}
	
	@Override
	protected boolean open(GroupResource res) {
		if(res != null) {
			if(res instanceof UserResource)
			{
				UserResource userres = (UserResource) res;
				if(userres.fil == null && userres.entry != null) {
					userres.fil = compat.open(userres.entry.getPath(), Path.Game, Mode.Read);
				} else if(userres.absolutePath != null) {
					userres.fil = compat.open(userres.absolutePath, Path.Absolute, Mode.Read);
				}
			}
			return true;
		}
		return false;
	}
	
	@Override
	public int position() {
		return -1;
	}

	public UserResource add(FileEntry entry, int fileid) {
		if(entry == null) return null;

		UserResource out = null;
		add(out = new UserResource(entry, fileid));
		numfiles++;
		
		return out;
	}
	
	public UserResource add(String absolutePath, int fileid) {
		if(absolutePath == null || absolutePath.isEmpty()) return null;

		UserResource out = null;
		add(out = new UserResource(absolutePath, fileid));
		numfiles++;
		
		return out;
	}

}
