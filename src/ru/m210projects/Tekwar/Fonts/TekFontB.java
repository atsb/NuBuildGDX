package ru.m210projects.Tekwar.Fonts;

import static ru.m210projects.Build.Engine.ydim;
import static ru.m210projects.Build.Gameutils.coordsConvertXScaled;
import static ru.m210projects.Build.Gameutils.coordsConvertYScaled;
import static ru.m210projects.Build.Pragmas.scale;
import static ru.m210projects.Tekwar.Names.ALPHABET2;

import ru.m210projects.Build.Engine;
import ru.m210projects.Build.Gameutils.ConvertType;
import ru.m210projects.Build.Pattern.BuildFont;
import ru.m210projects.Build.Render.Renderer.Transparent;
import ru.m210projects.Build.Types.TileFont;
import ru.m210projects.Build.Types.TileFont.FontType;

public class TekFontB extends BuildFont {

	private TileFont font;
	public TekFontB(final Engine draw) {
		super(draw, 4, 32768, 0);

		font = new TileFont(FontType.Tilemap, ALPHABET2, 8, 8, 16, 8);
	}

	@Override
	public int getWidth(char[] text) {
		int width = 0;

		if (text != null) {
			int pos = 0;
			while (pos < text.length && text[pos++] != 0) {
				width += 4;
			}
		}

		return width;
	}

	public int getWidth(char[] text, int scale) {
		int width = 0;

		if (text != null) {
			int pos = 0;
			while (pos < text.length && text[pos++] != 0) {
				width += scale(4, scale, 0x10000);
			}
		}
		return width;
	}

	private char[] sym = new char[1];
	@Override
	public int drawChar(int x, int y, char ch, int shade, int pal, int nBits, boolean shadow) {
		sym[0] = ch;
		drawText(x, y, sym, shade, pal, TextAlign.Center, nBits, shadow);

		return (int) ((nScale / 65536.0f) * font.charsizx);
	}

	@Override
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

		draw.getrender().printext(font, x, y, text, pal, shade, Transparent.None, scale);
		return 0;
	}
}
