package ru.m210projects.Witchaven.Fonts;

import static ru.m210projects.Build.Engine.xdim;
import static ru.m210projects.Build.Engine.ydim;
import static ru.m210projects.Witchaven.Globals.MINITEXTPOINT;
import static ru.m210projects.Witchaven.Globals.MINITEXTSLASH;
import static ru.m210projects.Witchaven.Main.engine;
import static ru.m210projects.Witchaven.Main.game;
import static ru.m210projects.Witchaven.Names.THEFONT;

import ru.m210projects.Build.Engine;
import ru.m210projects.Build.Pattern.BuildFont;

public class TheFont extends BuildFont {

	public TheFont(Engine draw) {
		super(draw, 9, 32768, 8 | 16);

		this.addChar(' ', nSpace, 5, nScale, 0, 0);

		for(int i = 0; i < 26; i++) {
			int nTile = i + THEFONT;
			addChar((char) ('A' + i), nTile, (engine.getTile(nTile).getWidth() / 2) + 1, nScale, 0, 0);
			addChar((char) ('a' + i), nTile, (engine.getTile(nTile).getWidth() / 2) + 1, nScale, 0, 0);
		}

		for(int i = 0; i < 10; i++) {
			int nTile = i + THEFONT + 26;
			addChar((char) ('0' + i), nTile, (engine.getTile(nTile).getWidth() / 2) + 1, nScale, 0, 0);
		}

		if(game.WH2) {
			addChar('!', 1546, (engine.getTile(1546).getWidth() / 2) + 1, nScale, 0, 0);
			addChar('?', 1547, (engine.getTile(1547).getWidth() / 2) + 1, nScale, 0, 0);
			addChar('-', 1548, (engine.getTile(1548).getWidth() / 2) + 1, nScale, 0, 2);
			addChar('_', 1548, (engine.getTile(1548).getWidth() / 2) + 1, nScale, 0, 4);
			addChar(':', 1549, (engine.getTile(1549).getWidth() / 2) + 1, nScale, 0, 0);
			addChar('.', MINITEXTPOINT, (engine.getTile(MINITEXTPOINT).getWidth() / 2) + 1, nScale, 0, -2);
			addChar('/', MINITEXTSLASH, (engine.getTile(MINITEXTSLASH).getWidth() / 2) + 1, nScale, 0, -2);
			addChar('\\', MINITEXTSLASH, (engine.getTile(MINITEXTSLASH).getWidth() / 2) + 1, nScale, 0, -2);
		} else {
			addChar('!', 1547, (engine.getTile(1547).getWidth() / 2) + 1, nScale, 0, 0);
			addChar('?', 1548, (engine.getTile(1548).getWidth() / 2) + 1, nScale, 0, 0);
			addChar('-', 1549, (engine.getTile(1549).getWidth() / 2) + 1, nScale, 0, 0);
			addChar('_', 1549, (engine.getTile(1549).getWidth() / 2) + 1, nScale, 0, 4);
			addChar(':', 1550, (engine.getTile(1550).getWidth() / 2) + 1, nScale, 0, 0);
			addChar('.', MINITEXTPOINT, (engine.getTile(MINITEXTPOINT).getWidth() / 2) + 1, nScale, 0, 0);
			addChar('/', MINITEXTSLASH, (engine.getTile(MINITEXTSLASH).getWidth() / 2) + 1, nScale, 0, 0);
			addChar('\\', MINITEXTSLASH, (engine.getTile(MINITEXTSLASH).getWidth() / 2) + 1, nScale, 0, 0);
		}
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
