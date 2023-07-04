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

import static ru.m210projects.Build.Pragmas.*;
import static ru.m210projects.Powerslave.Globals.*;

import ru.m210projects.Build.Engine;
import ru.m210projects.Build.Pattern.BuildFont;
import ru.m210projects.Build.Types.Tile;

public class MenuFont extends BuildFont {

	public MenuFont(Engine draw) {
		super(draw, mulscale(draw.getTile(THEFONT).getHeight() + 2, 48000, 16), 48000, 8 | 16);
		this.addChar(' ', nSpace, 8, nScale, 0, 0);

		int symbInfo[][] = { { THEFONT, 26, 'a' }, { THEFONT, 26, 'A' }, { THEFONT + 26, 10, '0' } };
		for(int j = 0, nTile; j < symbInfo.length; j++) {
			nTile = symbInfo[j][0];
			for(int i = 0; i < symbInfo[j][1]; i++) {
				Tile pic = draw.getTile(nTile + i);

				if(pic.getWidth() != 0) {
					char symbol = (char) (i + symbInfo[j][2]);
					this.addChar(symbol, nTile + i, mulscale(pic.getWidth() + 2, 48000, 16), nScale, 0, 0);
				}
			}
		}

		this.addChar('!', THEFONT + 36, 6, nScale, 0, 0);
		this.addChar('$', THEFONT + 37, 9, nScale, 0, 0);
		this.addChar('%', THEFONT + 38, 9, nScale, 0, 0);
		this.addChar('(', THEFONT + 39, 9, nScale, 0, 0);
		this.addChar(')', THEFONT + 40, 9, nScale, 0, 0);
		this.addChar('<', THEFONT + 41, 9, nScale, 0, 2);
		this.addChar('>', THEFONT + 42, 9, nScale, 0, 2);
		this.addChar('?', THEFONT + 43, 9, nScale, 0, 0);
		this.addChar('/', THEFONT + 44, 9, nScale, 0, 0);
		this.addChar('-', THEFONT + 45, 9, nScale, 0, 0);
		this.addChar('_', THEFONT + 45, 9, nScale, 0, 5);
		this.addChar('.', THEFONT + 46, 6, nScale, 0, 6);
		this.addChar(',', THEFONT + 47, 6, nScale, 0, 6);
		this.addChar(';', THEFONT + 48, 6, nScale, 0, 0);
		this.addChar(':', THEFONT + 49, 6, nScale, 0, 2);
		this.addChar('[', THEFONT + 50, 6, nScale, 0, 0);
		this.addChar(']', THEFONT + 51, 6, nScale, 0, 0);
	}

}
