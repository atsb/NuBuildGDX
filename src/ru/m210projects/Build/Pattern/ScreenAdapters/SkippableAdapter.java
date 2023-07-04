//This file is part of BuildGDX.
//Copyright (C) 2017-2018  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
//BuildGDX is free software: you can redistribute it and/or modify
//it under the terms of the GNU General Public License as published by
//the Free Software Foundation, either version 3 of the License, or
//(at your option) any later version.
//
//BuildGDX is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU General Public License for more details.
//
//You should have received a copy of the GNU General Public License
//along with BuildGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.Build.Pattern.ScreenAdapters;

import static ru.m210projects.Build.Input.Keymap.*;

import com.badlogic.gdx.ScreenAdapter;

import ru.m210projects.Build.Engine;
import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.Architecture.BuildFrame.FrameType;
import ru.m210projects.Build.Architecture.BuildGraphics.Option;
import ru.m210projects.Build.Pattern.BuildGame;
import ru.m210projects.Build.Settings.BuildConfig.GameKeys;

public abstract class SkippableAdapter extends ScreenAdapter {
	
	protected BuildGame game;
	protected Engine engine;
	protected boolean escSkip;
	protected Runnable skipCallback;
	
	public SkippableAdapter(BuildGame game)
	{
		this.game = game;
		this.engine = game.pEngine;
	}

	public SkippableAdapter setSkipping(Runnable skipCallback) {
		game.pInput.ctrlResetKeyStatus();
		this.skipCallback = skipCallback;
		return this;
	}
	
	public SkippableAdapter escSkipping(boolean escSkip) {
		this.escSkip = escSkip;
		return this;
	}
	
	public abstract void draw(float delta);

	public void skip() {
		if(skipCallback != null) {
			BuildGdx.app.postRunnable(skipCallback);
			skipCallback = null;
		}
		game.pInput.ctrlResetKeyStatus();
	}
	
	@Override
	public final void render(float delta) {
		engine.clearview(0);
		engine.sampletimer();

		skippingHandler();
		draw(delta);

		engine.nextpage();
	}
	
	private boolean skippingHandler() {
		if((escSkip && (game.pInput.ctrlGetInputKey(GameKeys.Menu_Toggle, true) 
				|| game.pInput.ctrlPadStatusOnce(GameKeys.Menu_Toggle))) 
				|| (!escSkip && game.pInput.ctrlKeyStatusOnce(ANYKEY))) {
			
			skip();
			return true;
		}
		
		return false;
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
