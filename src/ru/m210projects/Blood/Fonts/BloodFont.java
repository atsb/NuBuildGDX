// This file is part of BloodGDX.
// Copyright (C) 2017-2019  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// BloodGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// BloodGDX is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with BloodGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.Blood.Fonts;

import static ru.m210projects.Blood.Main.engine;

import ru.m210projects.Build.Engine;
import ru.m210projects.Build.Pattern.BuildFont;
import ru.m210projects.Build.Types.Tile;

public class BloodFont extends BuildFont {

	public BloodFont(Engine draw, int nTile, int charSpace) {
		super(draw);
		this.nScale = 65536;
		this.nFlags = 8 | 16;
		this.nHeight = 0;
		Tile pic;

		for (int i = 0; i < 96; i++) {
			pic = engine.getTile(nTile + i);
			if (pic.getHeight() > this.nHeight)
				this.nHeight = pic.getHeight();
		}

		this.addChar(' ', nSpace, engine.getTile(nTile++).getWidth() + charSpace, nScale, 0, 0);
		for(int i = 0; i < 95; i++) {
			pic = engine.getTile(nTile + i);
			if(pic.getWidth() != 0)
				this.addChar((char) (i + '!'), nTile + i, pic.getWidth() + charSpace, nScale, 0, 0);
		}
	}

}
