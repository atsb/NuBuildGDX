package ru.m210projects.Witchaven.Screens;

import static ru.m210projects.Build.Engine.xdim;
import static ru.m210projects.Build.Engine.ydim;
import static ru.m210projects.Build.Strhandler.toCharArray;
import static ru.m210projects.Witchaven.Names.*;
import static ru.m210projects.Witchaven.WHSND.SND_CheckLoops;
import static ru.m210projects.Witchaven.WHScreen.resetflash;

import ru.m210projects.Build.Architecture.BuildFrame.FrameType;
import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.Architecture.BuildGraphics.Option;
import ru.m210projects.Build.Pattern.BuildFont.TextAlign;
import ru.m210projects.Build.Pattern.BuildGame;
import ru.m210projects.Build.Pattern.ScreenAdapters.LoadingAdapter;

public class LoadingScreen extends LoadingAdapter {
	
	public LoadingScreen(BuildGame game) {
		super(game);
	}

	@Override
	public void draw(String title, float delta) {
		engine.clearview(77);
		engine.rotatesprite(160 << 16, 100 << 16, 65536, 0, MAINMENU, -128,
				0, 2 | 8 | 64, 0, 0, xdim - 1, ydim - 1);

		game.getFont(1).drawText(160,100, toCharArray("Loading"), 0, 0, TextAlign.Center, 2, true);
		game.getFont(1).drawText(160,114, toCharArray("please wait..."), 0, 0, TextAlign.Center, 2, true);
	}

	@Override
	public void show()
	{
		super.show();
		resetflash();
		SND_CheckLoops();
	}
	
	@Override
	public void pause () {
	}

	@Override
	public void resume () {
		game.updateColorCorrection();
	}
	
}
