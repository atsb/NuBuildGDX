package ru.m210projects.Tekwar.Screens;

import static ru.m210projects.Build.Engine.xdim;
import static ru.m210projects.Build.Engine.ydim;
import static ru.m210projects.Tekwar.Names.BACKGROUND;

import ru.m210projects.Build.Pattern.BuildFont.TextAlign;
import ru.m210projects.Build.Pattern.BuildGame;
import ru.m210projects.Build.Pattern.ScreenAdapters.LoadingAdapter;
import ru.m210projects.Build.Types.Tile;

public class LoadingScreen extends LoadingAdapter {

	public LoadingScreen(BuildGame game) {
		super(game);
	}

	@Override
	public void draw(String title, float delta) {
		Tile pic = engine.getTile(BACKGROUND);

		int frames = xdim / pic.getWidth();
		int x = 160;
		for (int i = 0; i <= frames; i++) {
			engine.rotatesprite(x << 16, 100 << 16, 0x10000, 0, BACKGROUND, 0, 0, 2 + 8 + 256, 0, 0, xdim - 1,
					ydim - 1);
			x += pic.getWidth();
		}

		game.getFont(0).drawText(160, 100, "Loading...".toCharArray(), 0, 0, TextAlign.Center, 0, false);
	}

}
