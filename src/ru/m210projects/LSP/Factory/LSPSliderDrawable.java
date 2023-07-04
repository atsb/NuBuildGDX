// This file is part of LSPGDX.
// Copyright (C) 2020  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// LSPGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// LSPGDX is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with LSPGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.LSP.Factory;

import static ru.m210projects.LSP.Main.*;

import ru.m210projects.Build.Pattern.MenuItems.SliderDrawable;

public class LSPSliderDrawable extends SliderDrawable {

	@Override
	public int getSliderWidth() {
		return 9;
	}

	@Override
	public int getSliderRange() {
		return 50;
	}

	@Override
	public void drawSliderBackground(int x, int y, int shade, int pal) {
		this.drawSliderBackground(engine, x, y, game.getFont(0).getHeight(), 228);
	}

	@Override
	public void drawSlider(int x, int y, int shade, int pal) {
		this.drawSlider(engine, x, y, game.getFont(0).getHeight(), 31);
	}

	@Override
	public int getScrollerWidth() {
		return 10;
	}

	@Override
	public int getScrollerHeight() {
		return 10;
	}
	
	@Override
	public void drawScrollerBackground(int x, int y, int height, int shade, int pal) {
		this.drawScrollerBackground(engine, x, y, height, 228);
	}

	@Override
	public void drawScroller(int x, int y, int shade, int pal) {
		this.drawScroller(engine, x, y, 31);
	}

}
