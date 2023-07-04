package ru.m210projects.Wang.Screens;

import static ru.m210projects.Build.Engine.palette;
import static ru.m210projects.Build.Engine.xdim;
import static ru.m210projects.Build.Engine.ydim;
import static ru.m210projects.Wang.Digi.DIGI_JG95012;
import static ru.m210projects.Wang.Digi.DIGI_NOLIKEMUSIC;
import static ru.m210projects.Wang.Gameutils.ROTATE_SPRITE_CORNER;
import static ru.m210projects.Wang.Gameutils.ROTATE_SPRITE_NON_MASK;
import static ru.m210projects.Wang.Gameutils.ROTATE_SPRITE_SCREEN_CLIP;
import static ru.m210projects.Wang.Gameutils.RS_SCALE;
import static ru.m210projects.Wang.Main.gs;
import static ru.m210projects.Wang.Sound.*;

import ru.m210projects.Build.Pattern.ScreenAdapters.SkippableAdapter;
import ru.m210projects.Build.Render.GLRenderer.GLInvalidateFlag;
import ru.m210projects.Wang.Main;
import ru.m210projects.Wang.Type.VOC3D;

public class CreditsScreen extends SkippableAdapter {

	private final int CREDITS1_PIC = 5111;
	private final int CREDITS2_PIC = 5118;

	private VOC3D handle;
	private boolean domusic, dopic;
	private boolean readytoskip;
	private int curpic;
	private float timer = 0;

	public CreditsScreen(Main game) {
		super(game);
	}

	@Override
	public void show() {
		engine.setbrightness(gs.brightness, palette, GLInvalidateFlag.All);

		COVER_SetReverb(0);
		StopSound();
		// Lo Wang feel like singing!
		handle = PlaySound(DIGI_JG95012, null, v3df_none);
		curpic = CREDITS1_PIC;
		domusic = false;
		dopic = false;
		readytoskip = false;
		
		timer = 0;
		game.pNet.ResetTimers();
		game.pInput.ctrlResetKeyStatus();
	}

	@Override
	public void draw(float delta) {
		timer += delta;
	
		if (!readytoskip && handle != null && !handle.isActive()) 
			domusic = true;

		if (domusic) {
			dopic = true;
			handle = null;
			
			// try 14 then 2 then quit
			CDAudio_Stop();
			CDAudio_Play(14, false);
			if (!CDAudio_Playing()) {
				CDAudio_Play(2, false);
				if (!CDAudio_Playing()) {
					handle = PlaySound(DIGI_NOLIKEMUSIC, null, v3df_none);
					readytoskip = true;
					dopic = false;
				}
			}
			domusic = false;
		}

		if(dopic) {
			engine.rotatesprite(0, 0, RS_SCALE, 0, curpic, 0, 0,
					(ROTATE_SPRITE_CORNER | ROTATE_SPRITE_SCREEN_CLIP | ROTATE_SPRITE_NON_MASK), 0, 0, xdim - 1, ydim - 1);
	
			if (timer > 10.0f) //960
				curpic = CREDITS2_PIC;
			
			if (timer > 20.0f) { //1920
				timer = 0;
				curpic = CREDITS1_PIC;
			}
		}
		
		if ((readytoskip && handle != null && !handle.isActive()) || (handle == null && !CDAudio_Playing()))
			skip();
	}

}
