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

import static ru.m210projects.Build.Engine.MAXSPRITES;
import static ru.m210projects.Build.Engine.MAXSTATUS;
import static ru.m210projects.Build.Engine.numsectors;
import static ru.m210projects.Build.Engine.numwalls;
import static ru.m210projects.Build.Engine.sector;
import static ru.m210projects.Build.Engine.sprite;
import static ru.m210projects.Build.Engine.wall;
import static ru.m210projects.Build.Engine.xdim;
import static ru.m210projects.Build.Engine.ydim;
import static ru.m210projects.LSP.Globals.*;

import ru.m210projects.Build.Pattern.BuildFont.TextAlign;
import ru.m210projects.Build.Pattern.BuildGame;
import ru.m210projects.Build.Pattern.ScreenAdapters.PrecacheAdapter;

public class PrecacheScreen extends PrecacheAdapter {

	public PrecacheScreen(BuildGame game) {
		super(game);
		
		addQueue("Preload floor and ceiling tiles...", new Runnable() {
			@Override
			public void run() {
				for (int i = 0; i < numsectors; i++) {
					addTile(sector[i].floorpicnum);
					addTile(sector[i].ceilingpicnum);
				}
				doprecache(0);
			}
		});

		addQueue("Preload wall tiles...", new Runnable() {
			@Override
			public void run() {
				for (int i = 0; i < numwalls; i++) {
					addTile(wall[i].picnum);
					if (wall[i].overpicnum >= 0) {
						addTile(wall[i].overpicnum);
					}
				}
				doprecache(0);
			}
		});

		addQueue("Preload sprite tiles...", new Runnable() {
			@Override
			public void run() {
				for (int i = 0; i < MAXSPRITES; i++) {
					if (sprite[i].statnum < MAXSTATUS) 
						cachespritenum(i);
				}
				doprecache(1);
			}
		});
	}

	@Override
	protected void draw(String title, int index) {
		engine.rotatesprite(160 << 16, 100 << 16, 65536, 0, 770, -128,
				0, 2 | 8 | 64, 0, 0, xdim - 1, ydim - 1);

		game.getFont(2).drawText(160,80, "Loading", 0, 0, TextAlign.Center, 2, true);
		game.getFont(2).drawText(160,114, "please wait...", 0, 0, TextAlign.Center, 2, true);
		
		game.getFont(2).drawText(160, 130, title, 0, 0, TextAlign.Center, 0, false);
	}
	
	private void cachespritenum(int i)
	{
		int maxc = 1;
		switch(sprite[i].picnum)
		{
			case BLUEDUDE:
			case REDDUDE:
			case YELLOWDUDE:
			case PURPLEDUDE:
			case GREENDUDE:
				maxc = 40;
				break;
		}
		for(int j = sprite[i].picnum; j < (sprite[i].picnum+maxc); j++)
			addTile(j);
	}

}
