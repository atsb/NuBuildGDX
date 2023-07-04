//This file is part of BuildGDX.
//Copyright (C) 2017-2018  Alexander Makarov-[M210] (m210-2007@mail.ru)
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

package ru.m210projects.Build.Pattern.Tools;

import static ru.m210projects.Build.Strhandler.toLowerCase;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ru.m210projects.Build.FileHandle.FileEntry;
import ru.m210projects.Build.Types.PropertyIgnoreCase;

public class IniFile {
	
	protected Map<String, Integer> context;
	protected List<Integer> pointer;
	protected List<Integer> length;
	protected int nContext = -1;
	protected PropertyIgnoreCase ini;
	protected StringReader reader;
	protected String name;
	protected byte[] data;
	private FileEntry file;
	
	protected IniFile() { /* extends */	}

 	public IniFile(byte[] data, String name, FileEntry file) {
		this.name = toLowerCase(name);
		this.file = file;
		
		init(data);
	}
	
	public FileEntry getFile()
	{
		return file;
	}
	
	public String getName()
	{
		return name;
	}
	
	public boolean set(String context) {
		context = toLowerCase(context);
		try {
			nContext = this.context.get(context);
		} catch (Exception e) { nContext = -1; return false; }

		String l = new String(data, pointer.get(nContext), length.get(nContext));
		
		if(!l.contains("\\\\")) l = l.replace("\\","\\\\");
		
		if(reader != null) 
			reader.close();
		reader = new StringReader(l);

		try {
			ini.clear();
			ini.load(reader);
		} catch (Exception e) { 
			e.printStackTrace();
			nContext = -1; 
			return false; 
		}

		return true;
	}

	public String GetKeyString(String key) {
		String out = ini.getPropertyIgnoreCase(key);
		if(out != null)
			out = out.trim();
		return out;
	}
	
	public String GetKeyString(String key, int num)
	{
		String line = ini.getPropertyIgnoreCase(key);
		if(line == null)
			return null;
		
		line = line.trim();

		int startpos = 0, pos = 0, keynum = 0, mark = 0;
		while(pos < line.length()) {
			if(line.charAt(pos) == '"') {
				mark++;
			}
			if(mark < 2) {
				pos++;
				continue;
			}

			if(line.charAt(pos) == ',') {
				if(keynum == num)
					break; 
				else {
					keynum++;
					mark = 0;
				}
				startpos = pos+1;
			}
			pos++;
		}
		if(keynum != num)
			return null;
		line = line.substring(startpos, pos);
		line = line.replaceAll("\"", "");
		line = line.trim();

		return line;
	}
	
	public int GetKeyInt(String key, int num)
	{
		String line = ini.getPropertyIgnoreCase(key);
		if(line == null)
			return 0;
		line = line.trim();
		
		int startpos = 0, pos = 0, keynum = 0;
		while(pos < line.length()) {
			if(line.charAt(pos) == ',') {
				if(keynum == num)
					break; 
				else
					keynum++;
				startpos = pos+1;
			}
			pos++;
		}
		line = line.substring(startpos, pos);
		line = line.replaceAll("[^0-9]", ""); 
		if(!line.isEmpty())
			return Integer.parseInt(line);
		else return 0;
	}
	
	public int GetKeyInt(String key) {
		String s;
		if((s = ini.getPropertyIgnoreCase(key)) != null && !s.isEmpty()) {
			s = s.replaceAll("[^0-9-]", ""); 
			if(!s.isEmpty())
				return Integer.parseInt(s);
			else return 0;
		}
		
		return -1;
	}
	
	public List<String> getPropertyList() 
	{
	    List<String> result = new ArrayList<String>();
	    for (Map.Entry<Object, Object> entry : ini.entrySet())
	    	result.add((String) entry.getKey());
	    
	    return result;
	}
	
	protected boolean isContext(String line) {
		line = line.trim();
		return line.startsWith("[") && line.endsWith("]");
	}
	
	protected void init(byte[] data) {
		this.data = data;
		
		if(data == null) return;
	
		ini = new PropertyIgnoreCase();
		context = new HashMap<String, Integer>();
		pointer = new ArrayList<Integer>();
		length = new ArrayList<Integer>();

	    StringBuilder line;
	    char c;
	    int ptr = 0;
	    while(ptr < data.length) {
	    	line = new StringBuilder();
	    	while(ptr < data.length && (c = (char)data[ptr++]) != '\n') {
	    		line.append(c);
	    	}
	    	
	    	if(isContext(line.toString())) {
	    		pointer.add(ptr-line.length()-1);
	    		line = new StringBuilder(toLowerCase(line.toString().replaceAll("[^a-zA-Z0-9_-]", "")));
	    		context.put(line.toString(), pointer.size()-1);
			}
	    }

	    if(pointer.size() > 0)
		{
			for( int i = 0; i < pointer.size() - 1; i++) {
				if(i + 1 < pointer.size()) {
					length.add(pointer.get(i + 1) - pointer.get(i));
				}
			}
			length.add(data.length - pointer.get(pointer.size() - 1));
		}
	}
	
	public void close() {
		data = null;	
		if(ini != null) {
			ini.clear();
			ini = null;
		}
	}
}
