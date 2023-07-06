package ru.m210projects.Tekwar.Screens;

import static ru.m210projects.Build.Engine.MAXSPRITES;
import static ru.m210projects.Build.Engine.MAXSTATUS;
import static ru.m210projects.Build.Engine.numsectors;
import static ru.m210projects.Build.Engine.numwalls;
import static ru.m210projects.Build.Engine.sector;
import static ru.m210projects.Build.Engine.sprite;
import static ru.m210projects.Build.Engine.wall;
import static ru.m210projects.Build.Engine.xdim;
import static ru.m210projects.Build.Engine.ydim;
import static ru.m210projects.Tekwar.Names.BACKGROUND;

import ru.m210projects.Build.Pattern.BuildFont.TextAlign;
import ru.m210projects.Build.Pattern.BuildGame;
import ru.m210projects.Build.Pattern.ScreenAdapters.PrecacheAdapter;
import ru.m210projects.Build.Types.Tile;

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
						addTile(sprite[i].picnum);
				}

				doprecache(1);
			}
		});
	}

	@Override
	protected void draw(String title, int index) {
		Tile pic = engine.getTile(BACKGROUND);

		int frames = xdim / pic.getWidth();
		int x = 160;
		for (int i = 0; i <= frames; i++) {
			engine.rotatesprite(x << 16, 100 << 16, 0x10000, 0, BACKGROUND, 0, 0, 2 + 8 + 256, 0, 0, xdim - 1,
					ydim - 1);
			x += pic.getWidth();
		}

		game.getFont(0).drawText(160, 100, "Loading...", 0, 0, TextAlign.Center, 0, false);
		game.getFont(0).drawText(160, 120, title, 0, 0, TextAlign.Center, 0, false);
	}

}
