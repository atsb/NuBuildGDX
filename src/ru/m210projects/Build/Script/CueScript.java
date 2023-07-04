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

package ru.m210projects.Build.Script;

import static ru.m210projects.Build.OnSceenDisplay.Console.OSDTEXT_RED;
import static ru.m210projects.Build.Strhandler.toLowerCase;

import java.util.HashMap;
import java.util.Map;

import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.FileHandle.FileEntry;
import ru.m210projects.Build.FileHandle.FileUtils;
import ru.m210projects.Build.OnSceenDisplay.Console;

public class CueScript extends Scriptfile {
	
	private final int T_FILE = 1;
	private final int T_TRACK = 2;
	private final int T_MP3 = 3;
	private final int T_AUDIO = 4;
	private final int T_INDEX = 5;
	private final int T_ERROR = 6;
	private final int T_EOF = 7;
	
	private final Map<String, Integer> basetokens = new HashMap<String, Integer>() {
		private static final long serialVersionUID = 1L;
		{
			put("file", T_FILE);
			put("track", T_TRACK);
			put("mp3", T_MP3);
			put("audio", T_AUDIO);
			put("index", T_INDEX);
		}
	};
	
	private int nTracks;
	private final Map<Integer, String> pTrackList;
	
	public CueScript(String filename, byte[] data) throws RuntimeException {
		super(filename, data);

		nTracks = 0;
		pTrackList = new HashMap<Integer, String>();
		process();
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
	
	private void process() {
		while (!eof()) {
			int line = getlinum(textptr);
			if(textptr < lineoffs[0])
				line--; //it's NOT line2
			
			int tokn = gettoken(basetokens);
			switch (tokn) {
			case T_ERROR:
				Console.Println("Error on line " + filename + ":" + line + ", skipping...", OSDTEXT_RED);
				if (line < linenum)
					textptr = lineoffs[line - 1];
				break;
			case T_EOF:
				break;
			case T_FILE:
				String path = getstring();
				getstring(); //file type

				if (gettoken(basetokens) == T_TRACK) {
					nTracks++;
					int nTrack = getValue();
					int nTrackType = gettoken(basetokens);
					if(nTrackType == T_AUDIO) 
						pTrackList.put(nTrack, FileUtils.getCorrectPath(path));

					int oldptr = textptr;
					if(gettoken(basetokens) == T_INDEX) {
						getstring(); // index num
						getstring(); // index time
					} else textptr = oldptr;
				}
				break;
			}
		}
	}
	
	public String[] getTracks() {
		String[] out = new String[nTracks];
		for(int i = 1; i <= nTracks; i++)
			out[i - 1] = pTrackList.get(i);
		return out;
	}
}
