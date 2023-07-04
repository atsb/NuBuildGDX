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

package ru.m210projects.Powerslave.Screens;

import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Build.Strhandler.toCharArray;
import static ru.m210projects.Powerslave.Globals.*;

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

		addQueue("Preload hud tiles...", new Runnable() {
			@Override
			public void run() {
				for(int i = 3522; i <= 3554; i++) //small font
					addTile(i);
				for(int i = THEFONT; i <= THEFONT + 51; i++) //menu font
					addTile(i);

//				addTile(STATUSBAR);

				doprecache(1);
			}
		});

		addQueue("Preload other tiles...", new Runnable() {
			@Override
			public void run() {
				for(int i = 0; i < MAXTILES; i++)
				{
					if(!engine.getTile(i).isLoaded())
						engine.loadtile(i);
				}
			}
		});
	}

	@Override
	protected void draw(String title, int index) {
		engine.clearview(96);
		engine.rotatesprite(160 << 16, 100 << 16, 65536, 0, BACKGROUND, -128,
				0, 2 | 8 | 64, 0, 0, xdim - 1, ydim - 1);

		game.getFont(1).drawText(160,100, toCharArray("Loading"), 0, 0, TextAlign.Center, 2, true);

		game.getFont(1).drawText(160, 114, title, 0, 0, TextAlign.Center, 2, true);
	}

	private void cachespritenum(int i)
	{
		int maxc = 1;
//		switch(sprite[i].picnum)
//		{
//			case RAT:
//				maxc = 15;
//				break;
//		}
		for(int j = sprite[i].picnum; j < (sprite[i].picnum+maxc); j++)
			addTile(j);
	}

}
