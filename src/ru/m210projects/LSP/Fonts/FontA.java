// This file is part of LSPGDX.
// Copyright (C) 2020  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// LSPGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// LSPGDX is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with LSPGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.LSP.Fonts;

import static ru.m210projects.Build.Engine.ydim;
import static ru.m210projects.Build.Gameutils.coordsConvertXScaled;
import static ru.m210projects.Build.Gameutils.coordsConvertYScaled;
import static ru.m210projects.Build.Pragmas.scale;

import ru.m210projects.Build.Engine;
import ru.m210projects.Build.Gameutils.ConvertType;
import ru.m210projects.Build.Pattern.BuildFont;
import ru.m210projects.Build.Render.Renderer.Transparent;
import ru.m210projects.Build.Types.TileFont;
import ru.m210projects.Build.Types.TileFont.FontType;

public class FontA extends BuildFont {

	private TileFont font;

	public FontA(final Engine draw) {
		super(draw, 8, 65536, 0);

		font = new TileFont(FontType.Tilemap, 628, 8, 8, 16, 8);
	}

	@Override
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

	private char[] sym = new char[1];
	@Override
	public int drawChar(int x, int y, char ch, int shade, int pal, int nBits, boolean shadow) {
		sym[0] = ch;
		drawText(x, y, sym, shade, pal, TextAlign.Center, nBits, shadow);

		return (int) ((65536.0f / nScale) * font.charsizx);
	}

	@Override
	public int drawText(int x, int y, char[] text, int shade, int pal, TextAlign align, int nBits, boolean shadow) {
		if(text == null) return 0;

		if ( align != TextAlign.Left )
		{
			int nWidth = getWidth(text);
			if ( align == TextAlign.Center )
				nWidth >>= 1;
			x -= nWidth;
		}

		x = coordsConvertXScaled(x, ConvertType.Normal);
		y = coordsConvertYScaled(y);

		int xdim = (4 * ydim) / 3;
		float scale = ((65536.0f / nScale) * xdim) / 320.0f;

		draw.getrender().printext(font, x, y, text, pal, shade, Transparent.None, scale);
		return 0;
	}
}
