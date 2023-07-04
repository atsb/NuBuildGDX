// This file is part of DukeGDX.
// Copyright (C) 2019  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// DukeGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// DukeGDX is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with DukeGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.Duke3D.Fonts;

import static ru.m210projects.Duke3D.Main.engine;
import static ru.m210projects.Duke3D.Names.BIGALPHANUM;
import static ru.m210projects.Duke3D.Names.BIGAPPOS;
import static ru.m210projects.Duke3D.Names.BIGCOLIN;
import static ru.m210projects.Duke3D.Names.BIGCOMMA;
import static ru.m210projects.Duke3D.Names.BIGPERIOD;
import static ru.m210projects.Duke3D.Names.BIGQ;
import static ru.m210projects.Duke3D.Names.BIGSEMI;
import static ru.m210projects.Duke3D.Names.BIGX;

import ru.m210projects.Build.Engine;
import ru.m210projects.Build.Pattern.BuildFont;
import ru.m210projects.Build.Types.Tile;

public class MenuFont extends BuildFont {

	public MenuFont(Engine draw) {
		super(draw, engine.getTile(BIGALPHANUM).getHeight(), 65536, 8 | 16);

		int width = 0;
		for (int i = 0; i < 26; i++) {
			int nTile = i + BIGALPHANUM;
			Tile pic = engine.getTile(nTile);
			if (pic.getWidth() > width)
				width = pic.getWidth();
		}
		for (int i = 0; i < 10; i++) {
			int nTile = i + BIGALPHANUM - 10;
			Tile pic = engine.getTile(nTile);
			if (pic.getWidth() > width)
				width = pic.getWidth();
		}

		this.addChar(' ', nSpace, 5, nScale, 0, 0);
		for (int i = 0; i < 26; i++) {
			int nTile = i + BIGALPHANUM;
			Tile pic = engine.getTile(nTile);

			addChar((char) ('A' + i), nTile, pic.getWidth(), nScale, 0, 0);
			addChar((char) ('a' + i), nTile, pic.getWidth(), nScale, 0, 0);
		}

		for (int i = 0; i < 10; i++) {
			int nTile = i + BIGALPHANUM - 10;
			addChar((char) ('0' + i), nTile, engine.getTile(nTile).getWidth(), nScale, 0, 0);
		}

		addChar('-', BIGALPHANUM - 11, engine.getTile(BIGALPHANUM - 11).getWidth(), nScale, 0, 0);
		addChar('.', BIGPERIOD, engine.getTile(BIGPERIOD).getWidth(), nScale, 0, 0);
		addChar(',', BIGCOMMA, engine.getTile(BIGCOMMA).getWidth(), nScale, 0, 0);
		addChar('!', BIGX, engine.getTile(BIGX).getWidth(), nScale, 0, 0);
		addChar('\'', BIGAPPOS, engine.getTile(BIGAPPOS).getWidth(), nScale, 0, 0);
		addChar('?', BIGQ, engine.getTile(BIGQ).getWidth(), nScale, 0, 0);
		addChar(';', BIGSEMI, engine.getTile(BIGSEMI).getWidth(), nScale, 0, 0);
		addChar(':', BIGCOLIN, engine.getTile(BIGCOLIN).getWidth(), nScale, 0, 0);
	}

	public void update() {

		for (int i = 0; i < 26; i++) {
			int nTile = i + BIGALPHANUM;

			charInfo[(char) ('A' + i)].nWidth = (short) engine.getTile(nTile).getWidth();
			charInfo[(char) ('a' + i)].nWidth = (short) engine.getTile(nTile).getWidth();
		}

		for (int i = 0; i < 10; i++) {
			int nTile = i + BIGALPHANUM - 10;
			charInfo[(char) ('0' + i)].nWidth = (short) engine.getTile(nTile).getWidth();
		}
	}

}
