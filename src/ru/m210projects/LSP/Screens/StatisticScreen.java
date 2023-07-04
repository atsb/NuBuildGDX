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
import static ru.m210projects.LSP.Enemies.*;
import static ru.m210projects.LSP.Globals.*;

import ru.m210projects.Build.Pattern.BuildGame;
import ru.m210projects.Build.Pattern.BuildFont;
import ru.m210projects.Build.Pattern.BuildFont.TextAlign;
import ru.m210projects.Build.Pattern.ScreenAdapters.SkippableAdapter;

public class StatisticScreen extends SkippableAdapter {

	public StatisticScreen(BuildGame game) {
		super(game);
	}

	@Override
	public void draw(float delta) {
	
		engine.rotatesprite(160 << 16, 100 << 16, 65536, 0, 611, 0, 0, 2 | 8, 0, 0, xdim - 1, ydim - 1);
		engine.rotatesprite(0, 0, 65536, 0, 611, 127, 0, 8 | 16 | 3, 0, 0, xdim - 1, ydim - 1);
		int x = 100; 
		int y = 10;
		engine.rotatesprite(x << 16, y << 16, 32768, 0, BLUEDUDE, 0, 0, 2 | 8 | 16, 0, 0, xdim - 1, ydim - 1);
		
		BuildFont f = game.getFont(0);
		
		f.drawText(x + 40, y + 10, "left: " + (nTotalKills[1] - nKills[1]), 0, 0, TextAlign.Left, 2 | 8, true);
		f.drawText(x + 40, y + 20, "total: " + nTotalKills[1], 0, 0, TextAlign.Left, 2 | 8, true);
		
		engine.rotatesprite(x << 16, (y += 35) << 16, 32768, 0, REDDUDE, 0, 0, 2 | 8 | 16, 0, 0, xdim - 1, ydim - 1);
		
		f.drawText(x + 40, y + 10, "left: " + (nTotalKills[2] - nKills[2]), 0, 0, TextAlign.Left, 2 | 8, true);
		f.drawText(x + 40, y + 20, "total: " + nTotalKills[2], 0, 0, TextAlign.Left, 2 | 8, true);
		
		engine.rotatesprite(x << 16, (y += 35) << 16, 32768, 0, GREENDUDE, 0, 0, 2 | 8 | 16, 0, 0, xdim - 1, ydim - 1);
		
		f.drawText(x + 40, y + 10, "left: " + (nTotalKills[3] - nKills[3]), 0, 0, TextAlign.Left, 2 | 8, true);
		f.drawText(x + 40, y + 20, "total: " + nTotalKills[3], 0, 0, TextAlign.Left, 2 | 8, true);
		
		engine.rotatesprite(x << 16, (y += 35) << 16, 32768, 0, PURPLEDUDE, 0, 0, 2 | 8 | 16, 0, 0, xdim - 1, ydim - 1);
		
		f.drawText(x + 40, y + 10, "left: " + (nTotalKills[4] - nKills[4]), 0, 0, TextAlign.Left, 2 | 8, true);
		f.drawText(x + 40, y + 20, "total: " + nTotalKills[4], 0, 0, TextAlign.Left, 2 | 8, true);
		
		engine.rotatesprite(x << 16, (y += 35) << 16, 32768, 0, YELLOWDUDE, 0, 0, 2 | 8 | 16, 0, 0, xdim - 1, ydim - 1);
		
		f.drawText(x + 40, y + 10, "left: " + (nTotalKills[5] - nKills[5]), 0, 0, TextAlign.Left, 2 | 8, true);
		f.drawText(x + 40, y + 20, "total: " + nTotalKills[5], 0, 0, TextAlign.Left, 2 | 8, true);
	}

}
