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

import static ru.m210projects.Build.Strhandler.toLowerCase;

import java.nio.ByteBuffer;

import ru.m210projects.Build.StringUtils;

public abstract class GroupResource implements Resource {

	public enum ResourceType { Data, GroupFile }

    protected ByteBuffer buffer;
	protected Group parent;
	
	protected String filename;
	protected String filenamext;
	protected String fileformat;
	protected int fileid;
	protected int size;
	
	protected void handleName(String fullname)
	{
		this.filenamext = toLowerCase(StringUtils.toUnicode(fullname)); //replaceAll("[^a-zA-Z0-9_. -]", "");
		int point = filenamext.lastIndexOf('.');
		if(point != -1) {
			this.fileformat = filenamext.substring(point + 1);
			this.filename = filenamext.substring(0, point);
		} else {
			this.fileformat = "";
			this.filename = this.filenamext;
		}
	}

	public GroupResource(Group parent)
	{
		this.parent = parent;
		this.fileid = -1;
	}
	
	@Override
	public Group getParent()
	{
		return parent;
	}
	
	public void flush()
	{
		if(buffer != null) {
			buffer.clear();
			buffer = null;
		}
	}
	
	public boolean isCached()
	{
		return buffer != null;
	}
	
	public String getFullName()
	{
		return filenamext;
	}
	
	public String getName()
	{
		return filename;
	}
	
	public String getExtension()
	{
		return fileformat;
	}
	
	public int getIdentification()
	{
		return fileid;
	}
	
	public void setIdentification(int fileid)
	{
		this.fileid = fileid;
	}

	@Override
	public int size() {
		return size;
	}
	
	public String toString()
	{
		return getFullName() + " fileid: " + getIdentification() + " parent: " + (parent != null ? parent.name : "") + "\r\n";
	}
}
