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
import static ru.m210projects.Duke3D.Names.*;

import ru.m210projects.Build.Engine;
import ru.m210projects.Build.Pattern.BuildFont;

public class MiniFont extends BuildFont {

	public MiniFont(Engine draw) {
		super(draw, engine.getTile(MINIFONT).getHeight() + 2, 65536, 8 | 16);

		this.addChar(' ', nSpace, 5, nScale, 0, 0);
		int nTile = MINIFONT;

		for(int i = 0; i < 64; i++) {
			char symbol = (char) (i + '!');
			if(engine.getTile(nTile + i).getWidth() != 0)
				this.addChar(symbol, nTile + i, 4, nScale, 0, 0);
		}
		for(int i = 64; i < 90; i++) {
			char symbol = (char) (i + '!');
			if(engine.getTile(nTile + i - 32).getWidth() != 0)
				this.addChar(symbol, nTile + i - 32, 4, nScale, 0, 0);
		}
		for(int i = 90; i < 95; i++) {
			char symbol = (char) (i + '!');
			if(engine.getTile(nTile + i).getWidth() != 0)
				this.addChar(symbol, nTile + i, 4, nScale, 0, 0);
		}
	}
}
