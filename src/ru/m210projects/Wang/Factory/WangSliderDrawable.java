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

package ru.m210projects.Wang.Factory;

import static ru.m210projects.Build.Engine.xdim;
import static ru.m210projects.Build.Engine.ydim;
import static ru.m210projects.Build.Gameutils.coordsConvertYScaled;
import static ru.m210projects.Wang.Main.engine;

import ru.m210projects.Build.Pattern.MenuItems.SliderDrawable;

public class WangSliderDrawable extends SliderDrawable {

	public static final int SLIDEBAR = 2846;

	public static final int SLIDER = 2849;

	@Override
	public int getSliderWidth() {
		return 6;
	}

	@Override
	public int getSliderRange() {
		return 48;
	}

	@Override
	public int getScrollerWidth() {
		return 6;
	}

	@Override
	public int getScrollerHeight() {
		return 5;
	}

	@Override
	public void drawSliderBackground(int x, int y, int shade, int pal) {
		engine.rotatesprite(x - 2 << 16, (y + 1) << 16, 48000, 0, SLIDEBAR, 8, pal, 10 | 16, 0, 0, xdim - 1, ydim - 1);
		int posx = -4;
		for(int i = 0; i < 11; i++)
			engine.rotatesprite(x + (posx += 4) << 16, (y + 1) << 16, 48000, 0, SLIDEBAR + 1, 8, pal, 10 | 16, 0, 0, xdim - 1, ydim - 1);
		engine.rotatesprite(x + posx + 5 << 16, (y + 1) << 16, 48000, 0, SLIDEBAR + 2, 8, pal, 10 | 16, 0, 0, xdim - 1, ydim - 1);
	}

	@Override
	public void drawSlider(int x, int y, int shade, int pal) {
		y += 2;
		engine.rotatesprite((int) ((x - 0.5f) * 65536.0f), (y - 1)  << 16, 48000, 0, SLIDER, shade, pal, 10 | 16, 0, 0,
				xdim - 1, ydim - 1);
	}

	@Override
	public void drawScrollerBackground(int x, int y, int height, int shade, int pal) {
		int ang = 512;
		int sy = y + 9;

		engine.rotatesprite(x + 6 << 16, y - 2 << 16, 65536, ang, SLIDEBAR, 8, 0, 10 | 16, 0, 0, xdim - 1,
				coordsConvertYScaled(sy));

		int clen = height - 2;
		int dy = engine.getTile(SLIDEBAR + 1).getWidth();
		int posy = sy - 8;

		while (clen > 0) {
			if (dy > clen)
				dy = clen;
			engine.rotatesprite(x + 6 << 16, posy << 16, 65536, ang, SLIDEBAR + 1, 8, 0, 10 | 16, 0, 0, xdim - 1,
					coordsConvertYScaled(posy + dy));
			posy += dy;
			clen -= dy;
		}

		int y2 = sy + height - 11;
		engine.rotatesprite(x + 6 << 16, y2 << 16, 65536, ang, SLIDEBAR + 2, 8, 0, 10 | 16, 0, 0, xdim - 1, ydim - 1);
	}

	@Override
	public void drawScroller(int x, int y, int shade, int pal) {
		engine.rotatesprite((x + 5) << 16, y << 16, 0xC000, 512, SLIDER, shade, pal, 10 | 16, 0, 0, xdim - 1, ydim - 1);
	}

}
