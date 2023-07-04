package ru.m210projects.Wang.Fonts;

import static ru.m210projects.Wang.Names.FONT_LARGE_ALPHA;
import static ru.m210projects.Wang.Names.FONT_LARGE_DIGIT;

import ru.m210projects.Build.Engine;
import ru.m210projects.Build.Pattern.BuildFont;

public class MenuFont extends BuildFont {

	public MenuFont(Engine draw) {
		super(draw, draw.getTile(FONT_LARGE_ALPHA).getHeight(), 65536, 8 | 16);

		this.addChar(' ', nSpace, 10, nScale, 0, 0);
		for(int i = 0; i < 26; i++) {
			int nTile = i + FONT_LARGE_ALPHA;
			int w = draw.getTile(nTile).getWidth();
			addChar((char) ('A' + i), nTile, w, nScale, 0, 0);
			addChar((char) ('a' + i), nTile, w, nScale, 0, 0);
		}

		for(int i = 0; i < 10; i++) {
			int nTile = i + FONT_LARGE_DIGIT;
			int w = draw.getTile(nTile).getWidth();
			addChar((char) ('0' + i), nTile, w, nScale, 0, 0);
		}

		addChar('\'', 9230, 6, nScale, 0, 0); //I added a font's width manually because this font called before def load
	}

	public void update()
	{

		for(int i = 0; i < 26; i++) {
			int nTile = i + FONT_LARGE_ALPHA;
			int w = draw.getTile(nTile).getWidth();
			charInfo[(char) ('A' + i)].nWidth = (short) w;
			charInfo[(char) ('a' + i)].nWidth = (short) w;
		}

		for(int i = 0; i < 10; i++) {
			int nTile = i + FONT_LARGE_DIGIT;
			int w = draw.getTile(nTile).getWidth();
			charInfo[(char) ('0' + i)].nWidth = (short) w;
		}
	}
}
