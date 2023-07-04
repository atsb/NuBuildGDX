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

package ru.m210projects.Build.OnSceenDisplay;

public interface OSDFunc {
	void drawchar(int x, int y, char ch, int shade, int pal, int scale);
	void drawosdstr(int x, int y, int ptr, int len, int shade, int pal, int scale);
	void drawstr(int x, int y, char[] text, int len, int shade, int pal, int scale);
	void drawcursor(int x, int y, int type, int lastkeypress, int scale);
	void drawlogo(int daydim);
	
	void clearbg(int col, int row);
	void showosd(int shown);
	int gettime();
	long getticksfunc();
	int getcolumnwidth(int width);
	int getrowheight(int height);
	boolean textHandler(String text);
}
