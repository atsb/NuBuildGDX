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

import static ru.m210projects.Build.Strhandler.toLowerCase;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.FileHandle.FileEntry;
import ru.m210projects.Build.Script.Scriptfile;

public class DevCommScript extends Scriptfile {
	
	private final int T_DEF = 1;
	private final int T_SOUND = 2;
	private final int T_PATH = 3;
	private final int T_NUM = 4;
	private final int T_ERROR = 5;
	private final int T_EOF = 6;
	
	private final Map<String, Integer> basetokens = new HashMap<String, Integer>() {
		private static final long serialVersionUID = 1L;
		{
			put("def", T_DEF);
			put("sound", T_SOUND);
			put("path:", T_PATH);
			put("num:", T_NUM);
		}
	};
	
	private Map<Integer, String> commentaries;

	public DevCommScript(FileEntry file)
	{
		super(file.getName(), BuildGdx.compat.getBytes(file));
		
		int tokn = gettoken(basetokens), end = -1;
		if(tokn != T_DEF || !getstring().equals("developercommentary") || (end = getbraces()) == -1)
			return;

		commentaries = new HashMap<Integer, String>();
		while (textptr < end)
			process();
	}
	
	public Map<Integer, String> getCommentaries() {
		return commentaries;
	}
	
	private int gettoken(Map<String , Integer> list) {
		int tok;
		if ((tok = gettoken()) == -2) 
			return T_EOF;
		
		Integer out = list.get(toLowerCase(textbuf.substring(tok, textptr)));
		if (out != null)
			return out;

		errorptr = textptr;
		return T_ERROR;
	}
	
	private Integer getValue() {
		String txt = getstring();
		if(txt == null) return null;
		txt = txt.replaceAll("[^0-9]", "");

		try {
			return Integer.parseInt(txt);
		} catch (Exception e) {
			return null;
		}
	}
	
	private String getPath() {
		int out = gettoken();
		if (out == -1) 
			return null;

		String txt = textbuf.substring(out, textptr - 1);
		txt = txt.replaceAll("\"", "");
		txt = txt.replace("/", File.separator);
		return toLowerCase(txt);
	}
	
	private void process()
	{
		int tokn = gettoken(basetokens);
		switch (tokn) {
		case T_DEF:
			int sndtokn = gettoken(basetokens);
			int sndend = -1, num = -1;
			String path = null;
			if(sndtokn == T_SOUND)
			{
				if ((sndend = getbraces()) == -1) return;
				
		        while (textptr < sndend)
		        {
		        	switch(gettoken(basetokens))
		        	{
		        	case T_PATH:
		        		path = getPath();
		        		break;
		        	case T_NUM:
		        		num = getValue();
		        		break;
		        	}
		        }
		        
		        if(path != null && num != -1) 
		        	commentaries.put(num, path);
			}
			break;
		case T_ERROR:
		case T_EOF:
			break;
		}
	}

}
