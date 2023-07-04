// This file is part of BloodGDX.
// Copyright (C) 2017-2019  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// BloodGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// BloodGDX is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with BloodGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.Blood;

import static ru.m210projects.Blood.SOUND.*;
import static ru.m210projects.Blood.Main.*;
import static ru.m210projects.Build.Strhandler.toLowerCase;
import static ru.m210projects.Build.OnSceenDisplay.Console.*;

import java.util.HashMap;
import java.util.Map;

import ru.m210projects.Blood.Types.SFX;
import ru.m210projects.Build.FileHandle.Group;
import ru.m210projects.Build.FileHandle.GroupResource;
import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Build.Script.Scriptfile;

public class RFScript extends Scriptfile {

	private final int T_DATA = 0;
	private final int T_RESOURCE = 1;
	private final int T_AS = 2;
	private final int T_SYMBOL = 3;
//	private final int T_FILE = 4;
//	private final int T_CDTRACK = 5;
	private final int T_ERROR = 6;
	private final int T_EOF = 7;
	
//	public static final int MAXUSERTRACKS = 64;
//	public static String usertrack[] = new String[MAXUSERTRACKS];

	private final HashMap<String, Integer> fileids;

	public RFScript(Group gr, String name, byte[] buf) {
		super(name, buf);
		
		fileids = new HashMap<String, Integer>();
		parse(gr);
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
		txt = txt.replaceAll("[^0-9x-]", "");

		int hexIndex = txt.indexOf("0x");
		if (hexIndex != -1)
			txt = txt.substring(hexIndex + 2);

		try {
			return Integer.parseInt(txt, (hexIndex != -1) ? 16 : 10);
		} catch (Exception e) {
			return null;
		}
	}

	private int geteol() {
		int start = textptr;
		while ((textptr < eof) && (textbuf.charAt(textptr)) != ';')
			textptr++;

		int end = textptr + 1;
		textptr = start;
		return end;
	}

	public HashMap<String, Integer> getIds() {
		return fileids;
	}

	@Override
	protected void preparse(byte[] data, int flen) {
		// Count number of lines
		int numcr = 1;
		for (int i = 0; i < flen; i++) {
			// detect all 4 types of carriage return (\r, \n, \r\n, \n\r :)
			int cr = 0;
			if (data[i] == '\r') {
				i += ((data[i + 1] == '\n') ? 1 : 0);
				cr = 1;
			} else if (data[i] == '\n') {
				i += ((data[i + 1] == '\r') ? 1 : 0);
				cr = 1;
			}
			if (cr != 0) {
				numcr++;
				continue;
			}
		}

		linenum = numcr;
		lineoffs = new int[numcr];

		// Preprocess file for comments (// and /*...*/, and convert all whitespace to
		// single spaces)
		int nflen = 0, space = 0, cs = 0, inquote = 0;
		numcr = 0;
		for (int i = 0; i < flen; i++) {
			// detect all 4 types of carriage return (\r, \n, \r\n, \n\r :)
			int cr = 0;
			if (data[i] == '\r') {
				i += ((data[i + 1] == '\n') ? 1 : 0);
				cr = 1;
			} else if (data[i] == '\n') {
				i += ((data[i + 1] == '\r') ? 1 : 0);
				cr = 1;
			}
			if (cr != 0) {
				// Remember line numbers by storing the byte index at the start of each line
				// Line numbers can be retrieved by doing a binary search on the byte index :)
				lineoffs[numcr++] = nflen;
				inquote = 0; // Blood RFS
				if (cs == 1)
					cs = 0;
				space = 1;
				continue; // strip CR/LF
			}

			if ((inquote == 0) && ((data[i] == ' ') || (data[i] == '\t'))) {
				space = 1;
				continue;
			} // strip Space/Tab
			if ((data[i] == '/') && (data[i + 1] == '/') && (cs == 0))
				cs = 1;
			if ((data[i] == '\\') && (data[i + 1] == '\\') && (cs == 0))
				cs = 1;
			if ((data[i] == '/') && (data[i + 1] == '*') && (cs == 0)) {
				space = 1;
				cs = 2;
			}
			if ((data[i] == '*') && (data[i + 1] == '/') && (cs == 2)) {
				cs = 0;
				i++;
				continue;
			}

			if (cs != 0)
				continue;

			if (space != 0) {
				data[nflen++] = 0;
				space = 0;
			}

			if ((data[i] == '\\') && (data[i + 1] == '\"')) {
				i++;
				data[nflen++] = '\"';
				continue;
			}
			if (data[i] == '\"') {
				inquote ^= 1;
				continue;
			}

			data[nflen++] = data[i];
		}
		data[nflen++] = 0;
		lineoffs[numcr] = nflen;
		data[nflen++] = 0;

		flen = nflen;

		textbuf = new String(data);
		textptr = 0;
		eof = nflen - 1;
	}

	private void nextLine(int line) {
		if (line < linenum)
			textptr = lineoffs[line - 1];
	}
	
	private final Map<String, Integer> basetokens = new HashMap<String, Integer>() {
		private static final long serialVersionUID = 1L;
		{
			put("data", T_DATA);
			put("resource", T_RESOURCE);
			put("as", T_AS);
			put(";", T_SYMBOL); // end of file (error issue)
		}
	};
	
	private void parse(Group group) {
		while (!eof()) {
			int line = getlinum(textptr);
			if(textptr < lineoffs[0])
				line--; //it's NOT line2
			
			int tokn = gettoken(basetokens);
			switch (tokn) {
			case T_ERROR:
				Console.Println("Error on line " + filename + ":" + line + ", skipping...", OSDTEXT_RED);
				nextLine(line);
				break;
			case T_EOF:
			case T_SYMBOL:
				break;
			case T_DATA:
				String sfxpath = getstring();
				int dattoken = gettoken(basetokens);
				if (dattoken == T_AS) {
					try {
						int soundId = getValue();
						int relVol = getValue();
						int pitch = getValue();
						int pitchrange = getValue();
						int format = getValue();
						int loopStart = getValue();

						String rawName = getstring();
						int index = rawName.indexOf(";"); // end of the line
						if (index != -1)
							rawName = rawName.substring(0, index);
						pSFXs[soundId] = new SFX(relVol, pitch, pitchrange, format, loopStart, rawName);

						String idname = rawName;
						if (!idname.contains("."))
							idname = idname + ".raw";
						fileids.put(idname, soundId);

						if (group != null) {
							if(group.isRemovable())
								group.add(sfxpath, pSFXs[soundId].getBytes(), soundId);
							else {
								GroupResource res = group.open(idname);
								if(res != null)
									res.setIdentification(soundId);
							}
						}
					} catch (Exception e) {
						Console.Println("Error on line " + filename + ":" + line + ", skipping...", OSDTEXT_RED);
						nextLine(line);
					}
				} else {
					Console.Println("Error on line " + filename + ":" + line + ", skipping...", OSDTEXT_RED);
					nextLine(line);
				}
				break;
			case T_RESOURCE:
				int eol = geteol();
				String path = game.getFilename(getstring());
				if (path != null) {
					int optr = textptr;
					int restoken = gettoken(basetokens);
					String filename = toLowerCase(path);
					filename = filename.replaceAll("[,;]", "");
					if (restoken == T_AS) {
						Integer value = null;
						if ((value = getValue()) != null) {
							int fileid = value;
							fileids.put(filename, fileid);
							
							if (group != null) {
								GroupResource res = group.open(filename);
								if(res != null)
									res.setIdentification(fileid);
							}
						}
					} else
						textptr = optr;

					if (textptr < eol) {
//						String flag = getstring();
//						if (flag.startsWith("preload"))
//							fileflags.put(filename, 4);
//						else if (flag.startsWith("prelock"))
//							fileflags.put(filename, 8);
					}
					nextLine(line);
					break;
				}
				break;
			}
		}
	}
}
