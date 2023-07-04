package ru.m210projects.Wang.Screens;

import static ru.m210projects.Build.Engine.palette;
import static ru.m210projects.Build.Strhandler.toCharArray;
import static ru.m210projects.Wang.Main.gs;
import static ru.m210projects.Wang.Main.kGameCrash;
import static ru.m210projects.Wang.Main.mUserFlag;
import static ru.m210projects.Wang.Screens.MenuScreen.DrawBackground;

import ru.m210projects.Build.Pattern.BuildFont.TextAlign;
import ru.m210projects.Build.Pattern.BuildGame;
import ru.m210projects.Build.Pattern.ScreenAdapters.LoadingAdapter;
import ru.m210projects.Build.Render.GLRenderer.GLInvalidateFlag;
import ru.m210projects.Wang.Main.UserFlag;

public class LoadingScreen extends LoadingAdapter {

	public LoadingScreen(BuildGame game) {
		super(game);
	}
	
	@Override
	public void show()
	{
		super.show();
		engine.setbrightness(gs.brightness, palette, GLInvalidateFlag.All);
	}

	@Override
	protected void draw(String title, float delta) {
		if(kGameCrash) {
			game.show();
			return;
		}
		
		engine.clearview(117);
		DrawBackground(engine);
		
		if(title == null) {
			game.getFont(1).drawText(160, 170, toCharArray("Please wait"),  -128, 4, TextAlign.Center, 2, false);
		} else {
			if(mUserFlag == UserFlag.UserMap )
				game.getFont(1).drawText(160, 170, toCharArray("Entering usermap"),  -128, 4, TextAlign.Center, 2, false);
			else game.getFont(1).drawText(160, 170, toCharArray("Entering "),  -128, 4, TextAlign.Center, 2, false);
			game.getFont(1).drawText(160, 170+15, toCharArray(title),  -128, 4, TextAlign.Center, 2, false);
		}
	}

}
