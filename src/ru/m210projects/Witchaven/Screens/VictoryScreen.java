package ru.m210projects.Witchaven.Screens;

import static ru.m210projects.Build.Engine.getInput;
import static ru.m210projects.Build.Engine.xdim;
import static ru.m210projects.Build.Engine.ydim;
import static ru.m210projects.Build.Input.Keymap.ANYKEY;
import static ru.m210projects.Witchaven.Main.engine;
import static ru.m210projects.Witchaven.Main.gMenuScreen;
import static ru.m210projects.Witchaven.Main.game;
import static ru.m210projects.Witchaven.WH1Names.*;
import static ru.m210projects.Witchaven.WHSND.SND_CheckLoops;
import static ru.m210projects.Witchaven.WHSND.SND_Sound;
import static ru.m210projects.Witchaven.WHSND.S_DROPFLAG;
import static ru.m210projects.Witchaven.WHSND.S_PICKUPFLAG;
import static ru.m210projects.Witchaven.WHSND.S_WISP2;
import static ru.m210projects.Witchaven.WHSND.sndStopMusic;

import com.badlogic.gdx.ScreenAdapter;

import ru.m210projects.Build.Architecture.BuildFrame.FrameType;
import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.Architecture.BuildGraphics.Option;

public class VictoryScreen extends ScreenAdapter {

	public int state;
	
	@Override
	public void show()
	{
		state = 0;
		SND_CheckLoops();
		sndStopMusic();
		
		SND_Sound(S_PICKUPFLAG);
	}
	
	@Override
	public void render(float delta) {
		engine.clearview(0);
		engine.handleevents();
		
		if (getInput().getKey(ANYKEY) != 0) {
			state++;
			
			switch(state)
			{
			case 1:
				SND_Sound(S_DROPFLAG);
				break;
			case 2:
				SND_Sound(S_WISP2);
				break;
			}
			
			getInput().resetKeyStatus();
		}

		switch(state)
		{
		case 0:
			engine.rotatesprite(0 << 16, 0 << 16, 65536, 0, VICTORYA, 0, 0, 2 | 8 | 16, 0, 0, xdim - 1, ydim - 1);
			break;
		case 1:
			engine.rotatesprite(0 << 16, 0 << 16, 65536, 0, VICTORYB, 0, 0, 2 | 8 | 16, 0, 0, xdim - 1, ydim - 1);
			break;
		case 2:
			engine.rotatesprite(0 << 16, 0 << 16, 65536, 0, VICTORYC, 0, 0, 2 | 8 | 16, 0, 0, xdim - 1, ydim - 1);
			break;
		case 3:
			game.changeScreen(gMenuScreen);
			break;
		}
		
		engine.sampletimer();
		engine.nextpage();
	}
	
	@Override
	public void pause () {
		if (BuildGdx.graphics.getFrameType() == FrameType.GL) 
			BuildGdx.graphics.extra(Option.GLDefConfiguration);
	}

	@Override
	public void resume () {
		game.updateColorCorrection();
	}

}
