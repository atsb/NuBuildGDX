package ru.m210projects.Wang.Fonts;

import static ru.m210projects.Wang.Names.MINIFONT;

import ru.m210projects.Build.Engine;
import ru.m210projects.Build.Pattern.BuildFont;

public class MiniFont extends BuildFont {

	public MiniFont(Engine draw) {
		super(draw, draw.getTile(MINIFONT).getHeight() + 2, 65536, 8 | 16);

		this.addChar(' ', nSpace, 5, nScale, 0, 0);
		int nTile = MINIFONT;

		for(int i = 0; i < 64; i++) {
			char symbol = (char) (i + '!');
			int w = draw.getTile(nTile + i).getWidth();
			if(w != 0)
				this.addChar(symbol, nTile + i, w + 1, nScale, 0, 0);
		}
		for(int i = 64; i < 90; i++) {
			char symbol = (char) (i + '!');
			int w = draw.getTile(nTile + i - 32).getWidth();
			if(w != 0)
				this.addChar(symbol, nTile + i - 32, w + 1, nScale, 0, 0);
		}
		for(int i = 90; i < 95; i++) {
			char symbol = (char) (i + '!');
			int w = draw.getTile(nTile + i).getWidth();
			if(w != 0)
				this.addChar(symbol, nTile + i, w + 1, nScale, 0, 0);
		}
	}
}
