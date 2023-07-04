package ru.m210projects.Tekwar.Fonts;

import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Tekwar.Names.*;
import ru.m210projects.Build.Engine;
import ru.m210projects.Build.Pattern.BuildFont;

public class MenuFont extends BuildFont {

	public MenuFont(Engine draw) {
		super(draw, 5, 28000, 10 | 16);

		this.addChar(' ', nSpace, 5, nScale, 0, 0);

		for(int i = 0; i < 26; i++) {
			int nTile = i + MFONT_A;
			addChar((char) ('A' + i), nTile, (draw.getTile(nTile).getWidth() / 2) + 1, nScale, 0, 0);
			addChar((char) ('a' + i), nTile, (draw.getTile(nTile).getWidth() / 2) + 1, nScale, 0, 0);
		}

		for(int i = 0; i < 10; i++) {
			int nTile = i + MFONT_0;
			addChar((char) ('0' + i), nTile, (draw.getTile(nTile).getWidth() / 2) + 1, nScale, 0, 0);
		}

		for(int i = 0; i < 15; i++) {
			int nTile = i + MFONT_SPECIAL1;
			addChar((char) ('!' + i), nTile, (draw.getTile(nTile).getWidth() / 2) + 1, nScale, 0, 0);
		}

		this.addChar('_', 1543, 5, nScale, 0, 2);
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
