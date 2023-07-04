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

import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.LSP.GdxResources.*;

import ru.m210projects.Build.Engine;
import ru.m210projects.Build.Pattern.BuildFont;
import ru.m210projects.Build.Types.Tile;

public class MenuFont extends BuildFont {

	public MenuFont(Engine draw) {
		super(draw, 13, 32768, 10 | 16);

		this.addChar(' ', nSpace, 8, nScale, 0, 0);

		for(int i = 0; i < 26; i++) {
			int nTile = i + MENUFONT + 1;
			Tile pic = draw.getTile(nTile);
			addChar((char) ('A' + i), nTile, pic.getWidth() / 2, nScale, 0, 0);
			addChar((char) ('a' + i), nTile, pic.getWidth() / 2, nScale, 0, 0);
		}

		for(int i = 0; i < 10; i++) {
			int nTile = i + MENUFONT + 27;
			addChar((char) ('0' + i), nTile, draw.getTile(nTile).getWidth() / 2, nScale, 0, 0);
		}

		this.addChar('&', MENUFONT, draw.getTile(MENUFONT).getWidth() / 2, nScale, 0, 0);
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
