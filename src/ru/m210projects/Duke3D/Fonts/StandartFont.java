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

package ru.m210projects.Duke3D.Fonts;

import static ru.m210projects.Build.Engine.curpalette;
import static ru.m210projects.Build.Engine.pTextfont;
import static ru.m210projects.Build.Engine.ydim;
import static ru.m210projects.Build.Gameutils.coordsConvertXScaled;
import static ru.m210projects.Build.Gameutils.coordsConvertYScaled;
import static ru.m210projects.Build.Pragmas.scale;

import ru.m210projects.Build.Engine;
import ru.m210projects.Build.Gameutils.ConvertType;
import ru.m210projects.Build.Pattern.BuildFont;
import ru.m210projects.Build.Render.Renderer.Transparent;
import ru.m210projects.Build.Types.TileFont;

public class StandartFont extends BuildFont {

	private final TileFont font;
	byte[] colpal = { (byte)255, (byte)255, (byte)255 };
	
	public StandartFont(Engine draw) {
		super(draw, 4, 32768, 0);
		
		font = pTextfont;
	}
	
	public int getWidth(char[] text) {
		int width = 0;

		if (text != null) {
			int pos = 0;
			while (pos < text.length && text[pos++] != 0) {
				width += 8;
			}
		}

		return width;
	}

	public int getWidth(char[] text, int scale) {
		int width = 0;
		if (text != null) {
			int pos = 0;
			while (pos < text.length && text[pos++] != 0) {
				width += scale(8, scale, 0x10000);
			}
		}
		return width;
	}

	private final char[] sym = new char[1];
	public int drawChar(int x, int y, char ch, int shade, int pal, int nBits, boolean shadow) {
		sym[0] = ch;
		drawText(x, y, sym, shade, pal, TextAlign.Center, nBits, shadow);

		return (int) ((nScale / 65536.0f) * font.charsizx);
	}

	public int drawText(int x, int y, char[] text, int shade, int pal, TextAlign align, int nBits, boolean shadow) {
		if(text == null) return 0;

		x = coordsConvertXScaled(x, ConvertType.Normal);
		y = coordsConvertYScaled(y);
		
		if ( align != TextAlign.Left )
		{
			int nWidth = getWidth(text);
			if ( align == TextAlign.Center ) 
				nWidth >>= 1;
			x -= nWidth;
		}
		
		int xdim = (4 * ydim) / 3;
		float scale = ((nScale / 65536.0f) * xdim) / 320.0f;
		
		byte[] palarray = curpalette.getBytes();
		System.arraycopy(palarray, 0, colpal, 0, 3);
		for(int i = 0; i < 3; i++)
			palarray[i] = (byte) (shade * 255 / 48); //48 - max shade (black)
		curpalette.update(palarray);
		
		draw.getrender().printext(font, x, y, text, 0, shade, Transparent.None, scale);
		
		System.arraycopy(colpal, 0, palarray, 0, 3);
		curpalette.update(palarray);
		return 0;
	}
}
