package ru.m210projects.Tekwar.Screens;

import static ru.m210projects.Build.Engine.xdim;
import static ru.m210projects.Build.Engine.ydim;
import static ru.m210projects.Build.Input.Keymap.ANYKEY;
import static ru.m210projects.Tekwar.Main.engine;
import static ru.m210projects.Tekwar.Main.game;

import com.badlogic.gdx.ScreenAdapter;

import ru.m210projects.Build.Architecture.BuildFrame.FrameType;
import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.Architecture.BuildGraphics.Option;

public class CreditsScreen extends ScreenAdapter {
	
	private long showTime;
	
	@Override
	public void show () {
		game.pInput.ctrlResetInput();
		showTime = System.currentTimeMillis();
	}
	
	@Override
	public void render (float delta) {
		engine.clearview(0);
		engine.sampletimer();
		
		engine.rotatesprite(160 << 16, 100 << 16, 0x10000, 0, 321, 0, 0, 2 + 8, 0, 0, xdim - 1, ydim - 1);
		
		if (System.currentTimeMillis() - showTime >= 100 && game.pInput.ctrlKeyStatusOnce(ANYKEY)) 
			game.gExit = true;
		
		engine.nextpage();
	}
	
	@Override
	public void pause () {

	}

	@Override
	public void resume () {
		game.updateColorCorrection();
	}

}
