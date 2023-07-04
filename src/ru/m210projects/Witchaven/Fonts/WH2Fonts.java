package ru.m210projects.Witchaven.Fonts;

import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Witchaven.Globals.*;

import ru.m210projects.Build.Engine;
import ru.m210projects.Build.Pattern.BuildFont;

public class WH2Fonts extends BuildFont {

	public WH2Fonts(Engine engine) {
		super(engine, 12, 32768, 8 | 16);

		this.addChar(' ', nSpace, 5, nScale, 0, 0);

		int nTile;
		for(int i = 0; i < 26; i++) {
			nTile = i + WH2FONTBIGLETTERS;
			addChar((char) ('A' + i), nTile, (engine.getTile(nTile).getWidth() / 2) + 1, nScale, 0, 0);
			nTile = i + WH2FONTLILLETTERS;
			addChar((char) ('a' + i), nTile, (engine.getTile(nTile).getWidth() / 2)+ 1, nScale, 0, 0);
		}
		for(int i = 0; i < 10; i++) {
			nTile = i + WH2FONTNUMBERS;
			addChar((char) ('0' + i), nTile, (engine.getTile(nTile).getWidth() / 2) + 1, nScale, 0, 0);
		}

		addChar('.', 9318, (engine.getTile(9318).getWidth() / 2) + 1, nScale, 0, 0);
		addChar(',', 9319, (engine.getTile(9319).getWidth() / 2) + 1, nScale, 0, 0);
		addChar(';', 9320, (engine.getTile(9320).getWidth() / 2) + 1, nScale, 0, 0);
		addChar(':', 9321, (engine.getTile(9321).getWidth() / 2) + 1, nScale, 0, 0);
		addChar('"', 9322, (engine.getTile(9322).getWidth() / 2) + 1, nScale, 0, 0);
		addChar('!', 9323, (engine.getTile(9323).getWidth() / 2) + 1, nScale, 0, 0);
		addChar('(', 9324, (engine.getTile(9324).getWidth() / 2) + 1, nScale, 0, 0);
		addChar(')', 9325, (engine.getTile(9325).getWidth() / 2) + 1, nScale, 0, 0);
		addChar('[', 9326, (engine.getTile(9326).getWidth() / 2) + 1, nScale, 0, 0);
		addChar(']', 9327, (engine.getTile(9327).getWidth() / 2) + 1, nScale, 0, 0);
		addChar('%', 9328, (engine.getTile(9328).getWidth() / 2) + 1, nScale, 0, 0);
		addChar('&', 9329, (engine.getTile(9329).getWidth() / 2) + 1, nScale, 0, 0);
		addChar('/', 9330, (engine.getTile(9330).getWidth() / 2) + 1, nScale, 0, 0);
		addChar('\\', 9331, (engine.getTile(9331).getWidth() / 2) + 1, nScale, 0, 0);
		addChar('-', 9332, (engine.getTile(9332).getWidth() / 2) + 1, nScale, 0, 0);
		addChar('+', 9333, (engine.getTile(9333).getWidth() / 2) + 1, nScale, 0, 0);
		addChar('?', 9334,  (engine.getTile(9334).getWidth() / 2) + 1, nScale, 0, 0);
	}

	@Override
	public int drawChar(int x, int y, char ch, int shade, int pal, int nBits, boolean shadow) {
		if(charInfo[ch].nTile == -1) return 0;

		int scale = nScale;

		if(charInfo[ch].nTile != nSpace) {
			if(shadow)
				draw.rotatesprite((x + charInfo[ch].xOffset + 1) << 16, (y + charInfo[ch].yOffset + 1) << 16, scale, 0, charInfo[ch].nTile, 127, 0, nFlags | nBits, 0, 0, xdim - 1, ydim - 1);
			draw.rotatesprite((x + charInfo[ch].xOffset) << 16, (y + charInfo[ch].yOffset) << 16, scale, 0, charInfo[ch].nTile, shade, pal, nFlags | nBits, 0, 0, xdim - 1, ydim - 1);
		}
		return charInfo[ch].nWidth;
	}

}
