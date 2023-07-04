// This file is part of PowerslaveGDX.
// Copyright (C) 2019  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// PowerslaveGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// PowerslaveGDX is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with PowerslaveGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.Powerslave.Fonts;

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

		font = new TileFont(FontType.Tilemap, 159, 8, 8, 16, 8);
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
		drawText(x, y, sym, 65536, shade, pal, TextAlign.Center, nBits, shadow);

		return (int) ((nScale / 65536.0f) * font.charsizx);
	}

	@Override
	public int drawText(int x, int y, char[] text, int tscale, int shade, int pal, TextAlign align, int nBits, boolean shadow) {
		if(text == null) return 0;

		float fscale = tscale / (float) nScale;
		if((nBits & 2) != 0) {
			ConvertType type = ConvertType.Normal;
			if((nBits & 256) != 0)
				type = ConvertType.AlignLeft;
			if((nBits & 512) != 0)
				type = ConvertType.AlignRight;
			if((nBits & 1024) != 0)
				type = ConvertType.Stretch;

			x = coordsConvertXScaled(x, type);
			y = coordsConvertYScaled(y);

			int xdim = (4 * ydim) / 3;
			fscale = ((tscale / (float) nScale) * xdim) / 320.0f;
		}

		if ( align != TextAlign.Left )
		{
			int nWidth = getWidth(text, tscale);
			if ( align == TextAlign.Center )
				nWidth >>= 1;
			x -= nWidth;
		}

		if(shadow) {
			int offs = (int) (1 * fscale);
			draw.getrender().printext(font, x+offs, y+offs, text, 0, 255, Transparent.None, fscale);
		}
		draw.getrender().printext(font, x, y, text, pal, shade, Transparent.None, fscale);
		return 0;
	}
}
