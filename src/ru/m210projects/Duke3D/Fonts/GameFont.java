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

import static ru.m210projects.Build.Strhandler.isdigit;
import static ru.m210projects.Duke3D.Main.engine;
import static ru.m210projects.Duke3D.Names.STARTALPHANUM;

import ru.m210projects.Build.Engine;
import ru.m210projects.Build.Pattern.BuildFont;
import ru.m210projects.Build.Types.Tile;

public class GameFont extends BuildFont {

	public GameFont(Engine draw) {
		super(draw, engine.getTile(STARTALPHANUM).getHeight() + 2, 65536, 8 | 16);
		int nTile = STARTALPHANUM;
		this.addChar(' ', nSpace, 3, nScale, 0, 0);
		for (int i = 0; i < 95; i++) {
			Tile pic = engine.getTile(nTile + i);

			if (pic.getWidth() != 0) {
				char symbol = (char) (i + '!');
				this.addChar(symbol, nTile + i, isdigit(symbol) ? 8 : pic.getWidth(), nScale, 0, 0);
			}
		}
	}

	public void update() {
		for (int i = 0; i < 95; i++) {
			Tile pic = engine.getTile(STARTALPHANUM + i);

			if (pic.getWidth() != 0) {
				char symbol = (char) (i + '!');
				charInfo[symbol].nWidth = (short) (isdigit(symbol) ? 8 : pic.getWidth());
			}
		}
	}
}
