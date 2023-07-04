package ru.m210projects.Witchaven.Fonts;

import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Witchaven.Globals.*;

import ru.m210projects.Build.Engine;
import ru.m210projects.Build.Pattern.BuildFont;

public class MenuFont extends BuildFont {

	public MenuFont(Engine draw) {
		super(draw, 13, 32768, 10 | 16);

		this.addChar(' ', nSpace, 6, nScale, 0, 0);

		for(int i = 0; i < 26; i++) {
			int nTile = i + MENUFONT + 1;
			addChar((char) ('A' + i), nTile, draw.getTile(nTile).getWidth() / 2, nScale, 0, 0);
			addChar((char) ('a' + i), nTile, draw.getTile(nTile).getWidth() / 2, nScale, 0, 0);
		}

		this.addChar('/', MENUFONT + 32, draw.getTile(MENUFONT + 32).getWidth() / 2, nScale, 0, 0);
		this.addChar('&', MENUFONT, draw.getTile(MENUFONT).getWidth() / 2, nScale, 0, 0);
		this.addChar('?', MENUFONT + 27, draw.getTile(MENUFONT + 27).getWidth() / 2, nScale, 0, 1);
		this.addChar('[', MENUFONT + 28, draw.getTile(MENUFONT + 28).getWidth() / 2, nScale, 0, 0);
		this.addChar(']', MENUFONT + 29, draw.getTile(MENUFONT + 29).getWidth() / 2, nScale, 0, 0);
		this.addChar(':', MENUFONT + 30, draw.getTile(MENUFONT + 30).getWidth() / 2, nScale, 0, 0);
		this.addChar('.', MENUFONT + 31, draw.getTile(MENUFONT + 31).getWidth() / 2, nScale, 0, 0);

		byte[] remapbuf = new byte[256];
		for(int i = 242; i < 252; i++) //yellow to green
			remapbuf[i] = (byte) (368 - i);
		for(int i = 117; i < 127; i++) //green to yellow
			remapbuf[i] = (byte) (368 - i);
		draw.makepalookup(20, remapbuf, 0, 0, 0, 1);
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
