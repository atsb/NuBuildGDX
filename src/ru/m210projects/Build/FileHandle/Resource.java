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

import java.io.Closeable;
import java.nio.ByteBuffer;

public interface Resource extends Closeable {

	enum Whence { Set, Current, End }

    String getFullName();

	String getExtension();
	
	Group getParent();
	
	void close();
	
	boolean isClosed();
	
	int seek(long offset, Whence whence);
	
	int read(byte[] buf, int offset, int len);
	
	int read(byte[] buf);
	
	int read(ByteBuffer bb, int offset, int len);
	
	String readString(int len);
	
	Integer readInt();
	
	Short readShort();
	
	Byte readByte();
	
	Boolean readBoolean();
	
	Long readLong();
	
	Float readFloat();
	
	int size();
	
	int position();
	
	int remaining();
	
	boolean hasRemaining();
	
	void toMemory();
	
	byte[] getBytes();

}
