/*
 * File Tokeniser/Parser/Whatever
 * by Jonathon Fowler
 * Remixed completely by Ken Silverman
 * See the included license file "BUILDLIC.TXT" for license info.
 * 
 * This file has been ported to Java and modified by Alexander Makarov-[M210] (m210-2007@mail.ru)
 */

package ru.m210projects.Build.Script;

import static ru.m210projects.Build.Strhandler.toLowerCase;

import java.io.File;
import java.util.Arrays;

public class Scriptfile {

	public String textbuf;
	public int errorptr;
	public int ltextptr; // pointer to start of the last token fetched (use this for line numbers)
	public int textptr;
	public String path;
	public String filename;
	public int eof;
	public int linenum;
	public int[] lineoffs;

	protected void skipovertoken() {
		while ((textptr < eof) && textptr < textbuf.length() && (textbuf.charAt(textptr)) != 0)
			textptr++;
	}

	protected void skipoverws() {
		if ((textptr < eof) && textptr < textbuf.length() && (textbuf.charAt(textptr)) == 0)
			textptr++;
	}

	public String getstring() {
		int out = gettoken();
		if (out == -1 || out == -2) {
			// initprintf("Error on line %s:%d: unexpected
			// eof\n",sf.filename,scriptfile_getlinum(sf,sf.textptr));
			return null;
		}

		String txt = textbuf.substring(out, textptr);
		txt = txt.replaceAll("\"", "");
		txt = txt.replace("/", File.separator);
		return toLowerCase(txt);
	}

	public int gettoken() {
		int start;

		skipoverws();
		if (textptr >= eof)
			return -2;
		start = ltextptr = textptr;
		skipovertoken();

		return start;
	}

	public Double getdouble() {
		int t = gettoken();
		if (t == -2)
			return null;

		try {
			return Double.parseDouble(textbuf.substring(t, textptr));
		} catch (Exception e) {
			return null; // not found
		}
	}

	public Integer getsymbol() {
		int t = gettoken();
		if (t == -2)
			return null;

		try {
			return (int) Long.parseLong(textbuf.substring(t, textptr), 10);
		} catch (Exception e) {
			// looks like a string, so find it in the symbol table
            return null; // not found
		}
	}

	public int getbraces() {
		int bracecnt;
		int bracestart;
		int inquote = 0;

		skipoverws();
		if (textptr >= eof) {
			// initprintf("Error on line %s:%d: unexpected
			// eof\n",sf.filename,scriptfile_getlinum(sf,sf.textptr));
			return -1;
		}

		if (textbuf.charAt(textptr) != '{') {
			// initprintf("Error on line %s:%d: expecting
			// '{'\n",sf.filename,scriptfile_getlinum(sf,sf.textptr));
			return -1;
		}
		bracestart = ++textptr;
		bracecnt = 1;
		while (true) {
			if (textptr >= eof)
				return (0);
			
			if (textbuf.charAt(textptr) == '\"') 
				inquote ^= 1;
			if (inquote == 0 && textbuf.charAt(textptr) == '{')
				bracecnt++;
			if (inquote == 0 && textbuf.charAt(textptr) == '}') {
				bracecnt--;
				if (bracecnt == 0)
					break;
			}
			textptr++;
		}
		int braceend = textptr - 1;
		textptr = bracestart;
		return braceend;
	}
	
	public void skipbrace(int braceend)
	{
		textptr = braceend + 1;
		gettoken(); //go to the next token
	}

	public boolean eof() {
		skipoverws();
		return textptr >= eof;
	}

	public int getlinum(int ptr) {
		int i, stp;

		for (stp = 1; stp + stp < linenum; stp += stp)
			; // stp = highest power of 2 less than linenum
		for (i = 0; stp != 0; stp >>= 1)
			if ((i + stp < linenum) && (lineoffs[i + stp] < ptr))
				i += stp;
		return i + 2; // i = index to highest lineoffs which is less than ind; convert to 1-based line
						// numbers
	}

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

			// quotes inside strings: \"
			if ((data[i] == '\\') && (data[i + 1] == '\"')) {
				i++;
				data[nflen++] = '\"';
				continue;
			}
			if (data[i] == '\"') {
				inquote ^= 1;
				//continue;
			}
			data[nflen++] = data[i];
		}
		data[nflen++] = 0;
		lineoffs[numcr] = nflen;
		data[nflen++] = 0;

		flen = nflen;

		textbuf = new String(data, 0, flen);
		textptr = 0;
		eof = textbuf.length() - 1;
	}

	public Scriptfile(String filename, byte[] data) {
		if(data == null)
			throw new RuntimeException("byte[] data == NULL");

		int flen = data.length;
		byte[] tx = Arrays.copyOf(data, flen + 2);
		tx[flen] = tx[flen + 1] = 0;

		preparse(tx, flen);
		this.filename = filename;
	}
}
