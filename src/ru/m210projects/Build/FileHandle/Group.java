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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import ru.m210projects.Build.FileHandle.Cache1D.PackageType;
import ru.m210projects.Build.OnSceenDisplay.Console;

public abstract class Group {

	protected boolean debug = false;
	
	protected static final byte DYNAMIC = 1;
	protected static final byte REMOVABLE = 2;

	public PackageType type;
	public byte flags;
	public String name;
	public int numfiles;
	
	protected HashMap<String, Integer> lookup = new HashMap<String, Integer>();
	protected List<GroupResource> filelist = new ArrayList<GroupResource>();
	
//	Hashmap не возволяет добавлять два одинаковых файла с разными fileid
//	protected HashMap<String, GroupResource> filelist =  new HashMap<String, GroupResource>();
	protected HashSet<String> fmtlist =  new HashSet<String>();
	
	protected void add(GroupResource res)
	{
		lookup.put(res.filenamext, filelist.size());
		filelist.add(res);
		fmtlist.add(res.fileformat);
	}
	
	public void setFlags(boolean dynamic, boolean removable)
	{
		if(removable)
			flags |= REMOVABLE;
		else flags &= ~REMOVABLE;
		
		if(dynamic)
			flags |= DYNAMIC;
		else flags &= ~DYNAMIC;
	}
	
	public boolean isRemovable()
	{
		return (flags & REMOVABLE) != 0;
	}
	
	public boolean isDynamic()
	{
		return (flags & DYNAMIC) != 0;
	}
	
	public boolean contains(String filename) {
		if (filename != null) {
			Integer out = lookup.get(toLowerCase(filename));
            return out != null;
			
//			GroupResource res = filelist.get(toLowerCase(filename));
//			if(res != null) 
//				return true;
		}

		return false;
	}
	
	public boolean contains(int fileid, String type) {
		if(type == null) 
			return false;

		type =  toLowerCase(type);
		
		if(!containsType(type)) return false;
		
		for(GroupResource res : filelist)
		{
			if(type.equals(res.fileformat)) {
				if(fileid == res.fileid)  {
					return true;
				}
			} 
		}

		return false;
	}

	public GroupResource open(String filename) {
		if (filename != null && !filename.isEmpty()) {
			Integer out = lookup.get(toLowerCase(filename));
			if(out != null) {
				GroupResource res = filelist.get(out);
				if(open(res))
					return res;
			}

//			GroupResource res = filelist.get(toLowerCase(filename));
//			if(res != null) 
//				return res;
		}
		return null;
	}

	public GroupResource open(int fileid, String type) {
		if(type == null) {
			Console.Println("type == null");
			return null;
		}

		type = toLowerCase(type);
		
		if(!containsType(type)) return null;
		
		for(GroupResource res : filelist)
		{
			if(type.equals(res.fileformat) && fileid == res.fileid) {
				if(open(res))  
					return res;
			} 
		}

		return null;
	}
	
	protected abstract boolean open(GroupResource res);
	
	public List<GroupResource> getList() {
		return new ArrayList<GroupResource>(filelist);
	}
	
	public boolean containsType(String type)
	{
		if(type == null) return false;
		return fmtlist.contains(toLowerCase(type));
	}
	
	public abstract int position();

	public void dispose() {
		for(GroupResource res : filelist) {
			if(!res.isClosed())
				res.close();
		}
		
		filelist.clear();
		fmtlist.clear();
	}
	
	public boolean add(String filename, byte[] data, int fileid) {
		if(filename == null || data == null) return false;
		
		DataResource file = new DataResource(this, filename, fileid, data);
		add(file);
		numfiles++;
		
		return true;
	}
	
	public String toString()
	{
		StringBuilder txt = new StringBuilder("Group name: " + name + "\r\n");
		txt.append("Num files: ").append(numfiles).append("\r\n");
		txt.append("File list: \r\n");

		for(GroupResource res : filelist)
			txt.append(res.toString());
		
		return txt.toString();
	}
	
}
