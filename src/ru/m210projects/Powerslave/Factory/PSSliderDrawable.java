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

package ru.m210projects.Powerslave.Factory;

import static ru.m210projects.Powerslave.Main.*;
import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Build.Gameutils.*;

import ru.m210projects.Build.Pattern.MenuItems.SliderDrawable;
import ru.m210projects.Build.Types.Tile;

public class PSSliderDrawable extends SliderDrawable {

	@Override
	public int getSliderWidth() {
		return 26;
	}

	@Override
	public int getSliderRange() {
		return 77;
	}

	@Override
	public int getScrollerWidth() {
		return 6;
	}

	@Override
	public int getScrollerHeight() {
		return 13;
	}

	@Override
	public void drawSliderBackground(int x, int y, int shade, int pal) {
		engine.rotatesprite((x - 1) << 16, y << 16, 24000, 0, 3469, 0, pal, 10 | 16, 0, 0, xdim-1, ydim-1);
	}

	@Override
	public void drawSlider(int x, int y, int shade, int pal) {
		engine.rotatesprite((x + 1)<< 16, (y + 1) << 16, 24000, 0, 3470 + (x % 2), 0, pal, 10 | 16, 0, 0, xdim-1, ydim-1);
	}

	@Override
	public void drawScrollerBackground(int x, int y, int height, int shade, int pal) {
		int SLIDEBAR = 3469;

		int ang = 512;
		int sy = y + 4;

		engine.rotatesprite(x + 6 << 16, y - 1 << 16, 24000, ang, SLIDEBAR, 8, 0, 10 | 16, 0, 0, xdim-1, coordsConvertYScaled(sy));

		int clen = height - 4;
		Tile pic = engine.getTile(SLIDEBAR);

		int dy = (pic.getWidth() / 3) - 5;
		int posy = sy;
		while(clen > 0)
		{
			if(dy > clen) dy = clen;
			engine.rotatesprite(x + 6 << 16, (posy-6) << 16, 24000, ang, SLIDEBAR, 8, 0, 10 | 16, 0, coordsConvertYScaled(posy), xdim-1, coordsConvertYScaled(posy + dy));
			posy += dy;
			clen -= dy;
		}

		int y2 = sy + height - 16;
		engine.rotatesprite(x + 6 << 16, (y2 - (pic.getWidth() / 3) + 6) << 16, 24000, ang, SLIDEBAR, 8, 0, 10 | 16, 0, coordsConvertYScaled(y2), xdim-1, ydim-1);
	}

	@Override
	public void drawScroller(int x, int y, int shade, int pal) {
		engine.rotatesprite((x + 5)<< 16, (y - 4) << 16, 24000, 512, 3470, 0, pal, 10 | 16, 0, coordsConvertYScaled(y), xdim-1, coordsConvertYScaled(y + getScrollerHeight()));
	}

}
