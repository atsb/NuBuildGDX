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

package ru.m210projects.LSP.Screens;

import static ru.m210projects.Build.Engine.xdim;
import static ru.m210projects.Build.Engine.ydim;
import static ru.m210projects.LSP.Globals.book;
import static ru.m210projects.LSP.Globals.chapter;
import static ru.m210projects.LSP.Globals.verse;

import ru.m210projects.Build.Pattern.BuildFont.TextAlign;
import ru.m210projects.Build.Pattern.BuildGame;
import ru.m210projects.Build.Pattern.ScreenAdapters.LoadingAdapter;

public class LoadingScreen extends LoadingAdapter {

	public LoadingScreen(BuildGame game) {
		super(game);
	}

	@Override
	protected void draw(String title, float delta) {
		engine.rotatesprite(160 << 16, 100 << 16, 65536, 0, 770, 0, 0, 2 | 8, 0, 0, xdim - 1, ydim - 1);
		game.getFont(2).drawText(160, 80, "Loading...", 0, 0, TextAlign.Center, 2, false);
		game.getFont(2).drawText(160, 100, book + "b " + chapter +  "c " + verse + "v", -128, 70, TextAlign.Center, 2, true);
	}

}
