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

import static ru.m210projects.Build.Pragmas.mulscale;

import ru.m210projects.Build.Engine;
import ru.m210projects.Build.Pattern.BuildFont;
import ru.m210projects.Build.Types.Tile;

public class GameFont extends BuildFont {

	public GameFont(Engine draw) {
		super(draw, mulscale(draw.getTile(3522).getHeight() + 2, 48000, 16), 48000, 8 | 16);
		this.addChar(' ', nSpace, 6, nScale, 0, 0);

		int symbInfo[][] = { { 3522, 26, 'a' }, { 3522, 26, 'A' }, { 3555, 10, '0' } };
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
		this.addChar('.', 3548, 2, nScale, 0, 0);
		this.addChar('!', 3549, 2, nScale, 0, 0);
		this.addChar('?', 3550, 6, nScale, 0, 0);
		this.addChar(',', 3551, 2, nScale, 0, 0);
		this.addChar('`', 3552, 2, nScale, 0, 0);
		this.addChar('"', 3553, 3, nScale, 0, 0);
		this.addChar('-', 3554, 5, nScale, 0, 0);
		this.addChar('_', 3554, 5, nScale, 0, 3);
	}
}
