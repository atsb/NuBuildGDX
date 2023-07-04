package ru.m210projects.Witchaven.Fonts;

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

	private TileFont font;
	
	public StandartFont(Engine draw) {
		super(draw, 4, 32768, 0);
		
		font = pTextfont;
	}
	
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
	
		draw.getrender().printext(font, x, y, text, 30, shade, Transparent.None, scale);
		return 0;
	}
}
