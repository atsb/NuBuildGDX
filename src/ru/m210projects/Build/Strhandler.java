// This file is part of BuildGDX.
// Copyright (C) 2017-2018  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// BuildGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// BuildGDX is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with BuildGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.Build;

import java.util.Locale;

public class Strhandler {

	private static final Locale usLocal = Locale.US;
	public static String toLowerCase(String text)
	{
		if(text != null)
			return text.toLowerCase(usLocal); //Turkish language issue
		return null;
	}

	//String handler

	static char[] tmp_buffer = new char[80];
	public static char[] toCharArray(String... text)
	{
		buildString(tmp_buffer, 0, text);

		return tmp_buffer;
	}

	public static int buildString(char[] buffer, int boffset, String... text)
	{
		int pos = boffset;
		for(int i = 0; i < text.length && pos < buffer.length; i++)
		{
			if(text[i] != null) {
				text[i].getChars(0, Math.min(text[i].length(), buffer.length), buffer, pos);
				pos += text[i].length();
			}
		}
		if(pos< buffer.length)
			buffer[pos] = 0;

		return pos;
	}

	public static int buildString(char[] buffer, int boffset, String text, int number)
	{
		int pos = boffset;
		if(text != null) {
			text.getChars(0, Math.min(text.length(), buffer.length), buffer, pos);
			pos += text.length();
		}

		int lnum = Bitoa(number, tmp_buffer);
		System.arraycopy(tmp_buffer, 0, buffer, pos, lnum);

		pos += lnum;
		if(pos < buffer.length)
			buffer[pos] = 0;

		return pos;
	}

	public static int buildString(char[] buffer, int boffset, String text, int number, int symbols)
	{
		int pos = boffset;
		if(text != null) {
			text.getChars(0, Math.min(text.length(), buffer.length), buffer, pos);
			pos += text.length();
		}

		int lnum = Bitoa(number, tmp_buffer, symbols);
		System.arraycopy(tmp_buffer, 0, buffer, pos, lnum);

		pos += lnum;
		if(pos < buffer.length)
			buffer[pos] = 0;

		return pos;
	}

	public static int indexOf(String target, byte[] source, int fromIndex) {
        if (fromIndex >= source.length)
            return (target.length() == 0 ? source.length : -1);

        char first = target.charAt(0);
        int max = (source.length - target.length());

        for (int i = fromIndex; i <= max; i++) {
            if (source[i] != first)
                while (++i <= max && source[i] != first);

            if (i <= max) {
                int j = i + 1;
                int end = j + target.length() - 1;
                for (int k = 1; j < end && source[j] == target.charAt(k); j++, k++);
                if (j == end)
                    return i;

            }
        }
        return -1;
    }

	public static int Bsprintf(char[] b, int slen, int num, int align) {
		Bitoa(num, tmp_buffer);
		int len = Bstrlen(tmp_buffer);
		if( align == 0) {
			for(int i = 0; i < len && i < slen; i++)
				b[i] = tmp_buffer[i];
		} else if( align == 1 ) {
			int dx = (slen - 1) - (len - 1);
			for(int i = slen - 1; i >= 0; i--) {
				if(i-dx >= 0)
					b[i] = tmp_buffer[i-dx];
				else b[i] = ' ';
			}
		}
		return slen;
	}

	public static int Bitoa(int n, char[] buffer) {
		int i = 0;
		boolean isneg = n < 0;

		long n1 = isneg ? -n:n;

		while(n1 !=0) {
			buffer[i++] = (char) (n1%10+'0');
			n1=n1/10;
		}
		if(isneg)
			buffer[i++] = '-';
		if(i < buffer.length)
			buffer[i] = '\0';

		for(int t = 0; t < i/2; t++) {
			buffer[t] ^= buffer[i-t-1];
			buffer[i-t-1] ^= buffer[t];
			buffer[t] ^= buffer[i-t-1];
		}

		if(n == 0) {
			buffer[i++] = '0';
			if(i < buffer.length)
				buffer[i] = '\0';
		}
		return i;
	}

	public static int Bitoa(int n, char[] buffer, int numsymbols) {
		int i = 0;
		boolean isneg = n < 0;

		long n1 = isneg ? -n:n;

		while(n1 !=0) {
			buffer[i++] = (char) (n1%10+'0');
			n1=n1/10;
		}
		int num = i;
		for(i = num; i < numsymbols; i++) {
			buffer[i] = '0';
		}

		if(isneg)
			buffer[i++] = '-';
		if(i < buffer.length)
			buffer[i] = '\0';

		for(int t = 0; t < i/2; t++) {
			buffer[t] ^= buffer[i-t-1];
			buffer[i-t-1] ^= buffer[t];
			buffer[t] ^= buffer[i-t-1];
		}

		if(n == 0) {
			for(i = 0; i < numsymbols; i++) {
				buffer[i] = '0';
			}
			if(i < buffer.length)
				buffer[i] = '\0';
		}
		return i;
	}

	public static int Bstrcmp(char[] txt1, int offset1, char[] txt2, int offset2) {
		int i = 0;
		if(txt1 == null || txt2 == null)
			return -1;

		int len = Math.max(txt1.length - offset1, txt2.length - offset2);
		while(i < len) {
			char ch1 = 0, ch2 = 0;
			if(offset1 + i < txt1.length) ch1 = txt1[offset1 + i];
			if(offset2 + i < txt2.length) ch2 = txt2[offset2 + i];
			if(ch1 != ch2)
				return -1;
			if(ch1 == ch2 && ch1 == 0)
				return 0;
			i++;
		}
		return 0;
	}

	public static int Bstrcmp(char[] txt1, char[] txt2) {
		int i = 0;
		if(txt1 == null || txt2 == null)
			return -1;

		int len = Math.max(txt1.length, txt2.length);
		while(i < len) {
			char ch1 = 0, ch2 = 0;
			if(i < txt1.length) ch1 = txt1[i];
			if(i < txt2.length) ch2 = txt2[i];
			if(ch1 != ch2)
				return -1;
			if(ch1 == ch2 && ch1 == 0)
				return 0;
			i++;
		}
		return 0;
	}

	public static boolean isdigit(char ch) {
		return ch>='0' && ch<='9';
	}

	public static boolean isalpha(char ch) {
		return Character.isLetter(ch);
	}

	public static int Bstrlen(char[] src) {
		int len = 0;
		while(len < src.length && src[len] != '\0') {
			len++;
		}
		return len;
	}
}
