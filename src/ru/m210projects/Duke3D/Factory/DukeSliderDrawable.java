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

package ru.m210projects.Duke3D.Factory;

import static ru.m210projects.Build.Engine.xdim;
import static ru.m210projects.Build.Engine.ydim;
import static ru.m210projects.Build.Gameutils.coordsConvertYScaled;
import static ru.m210projects.Duke3D.Main.engine;
import static ru.m210projects.Duke3D.Names.SLIDEBAR;

import ru.m210projects.Build.Pattern.MenuItems.SliderDrawable;
import ru.m210projects.Build.Types.Tile;

public class DukeSliderDrawable extends SliderDrawable {

	@Override
	public int getSliderWidth() {
		return 8;
	}

	@Override
	public int getSliderRange() {
		return 38;
	}

	@Override
	public int getScrollerWidth() {
		return 12;
	}

	@Override
	public int getScrollerHeight() {
		return 8;
	}

	@Override
	public void drawSliderBackground(int x, int y, int shade, int pal) {
		engine.rotatesprite(x - 2 << 16, (int) ((y + 0.5f) * 65536.0f), 32768, 0, SLIDEBAR, 8, pal, 10 | 16, 0, 0, xdim-1, ydim-1);
	}

	@Override
	public void drawSlider(int x, int y, int shade, int pal) {
		engine.rotatesprite((int) ((x - 0.5f) * 65536.0f), y + 1 << 16, 32768, 0, SLIDEBAR+1, shade, pal, 10 | 16, 0, 0, xdim-1, ydim-1);
	}

	@Override
	public void drawScrollerBackground(int x, int y, int height, int shade, int pal) {
		int ang = 512;
		int sy = y + 9;

		engine.rotatesprite(x + 12 << 16, y - 7 << 16, 65536, ang, SLIDEBAR, 8, 0, 10 | 16, 0, 0, xdim-1, coordsConvertYScaled(sy));

		Tile pic = engine.getTile(SLIDEBAR);
		int clen = height - 15;
		int dy = (pic.getWidth()) - 19;

		int posy = sy;
		while(clen > 0)
		{
			if(dy > clen) dy = clen;
			engine.rotatesprite(x + 12 << 16, (posy-9) << 16, 65536, ang, SLIDEBAR, 8, 0, 10 | 16, 0, coordsConvertYScaled(posy), xdim-1, coordsConvertYScaled(posy + dy));
			posy += dy;
			clen -= dy;
		}

		int y2 = sy + height - 15;
		engine.rotatesprite(x + 12 << 16, (y2 - (pic.getWidth()) + 13) << 16, 65536, ang, SLIDEBAR, 8, 0, 10 | 16, 0, coordsConvertYScaled(y2), xdim-1, ydim-1);
	}

	@Override
	public void drawScroller(int x, int y, int shade, int pal) {
		engine.rotatesprite((x + 10) << 16, (y - 1) << 16, 0xC000, 512, SLIDEBAR+1, shade, pal, 10 | 16, 0, 0, xdim-1, ydim-1);
	}

}
