// This file is part of RedneckGDX.
// Copyright (C) 2017-2019  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// RedneckGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// RedneckGDX is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with RedneckGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.Redneck.Fonts;

import static ru.m210projects.Redneck.Names.BIGALPHANUM;
import static ru.m210projects.Redneck.Names.BIGAPPOS;
import static ru.m210projects.Redneck.Names.BIGCOLIN;
import static ru.m210projects.Redneck.Names.BIGCOMMA;
import static ru.m210projects.Redneck.Names.BIGPERIOD;
import static ru.m210projects.Redneck.Names.BIGQ;
import static ru.m210projects.Redneck.Names.BIGSEMI;
import static ru.m210projects.Redneck.Names.BIGX;

import ru.m210projects.Build.Engine;
import ru.m210projects.Build.Pattern.BuildFont;
import ru.m210projects.Build.Types.Tile;

public class MenuFont extends BuildFont {

	public MenuFont(Engine draw) {
		super(draw, draw.getTile(BIGALPHANUM).getHeight() / 2, 32768, 8 | 16);

		this.addChar(' ', nSpace, 5, nScale, 0, 0);
		for(int i = 0; i < 26; i++) {
			int nTile = i + BIGALPHANUM;
			Tile pic = draw.getTile(nTile);

			addChar((char) ('A' + i), nTile, pic.getWidth() / 2, nScale, 0, 0);
			addChar((char) ('a' + i), nTile, pic.getWidth() / 2, nScale, 0, 0);
		}

		for(int i = 0; i < 10; i++) {
			int nTile = i + BIGALPHANUM - 10;
			addChar((char) ('0' + i), nTile, draw.getTile(nTile).getWidth() / 2, nScale, 0, 0);
		}
		addChar('-', BIGALPHANUM-11, draw.getTile(BIGALPHANUM-11).getWidth() / 2, nScale, 0, 0);
		addChar('.', BIGPERIOD, draw.getTile(BIGPERIOD).getWidth() / 2, nScale, 0, 0);
		addChar(',', BIGCOMMA, draw.getTile(BIGCOMMA).getWidth() / 2, nScale, 0, 0);
		addChar('!', BIGX, draw.getTile(BIGX).getWidth() / 2, nScale, 0, 0);
		addChar('\'', BIGAPPOS, draw.getTile(BIGAPPOS).getWidth() / 2, nScale, 0, 0);
		addChar('?', BIGQ, draw.getTile(BIGQ).getWidth() / 2, nScale, 0, 0);
		addChar(';', BIGSEMI, draw.getTile(BIGSEMI).getWidth() / 2, nScale, 0, 0);
		addChar(':', BIGCOLIN, draw.getTile(BIGCOLIN).getWidth() / 2, nScale, 0, 0);
	}

	public void update()
	{

		for(int i = 0; i < 26; i++) {
			int nTile = i + BIGALPHANUM;

			charInfo[(char) ('A' + i)].nWidth = (short) (draw.getTile(nTile).getWidth() / 2);
			charInfo[(char) ('a' + i)].nWidth = (short) (draw.getTile(nTile).getWidth() / 2);
		}

		for(int i = 0; i < 10; i++) {
			int nTile = i + BIGALPHANUM - 10;
			charInfo[(char) ('0' + i)].nWidth = (short) (draw.getTile(nTile).getWidth() / 2);
		}
	}

}
