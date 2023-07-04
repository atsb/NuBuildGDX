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

import com.badlogic.gdx.ScreenAdapter;

import ru.m210projects.Build.Engine;
import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.Architecture.BuildFrame.FrameType;
import ru.m210projects.Build.Architecture.BuildGraphics.Option;
import ru.m210projects.Build.Pattern.BuildGame;
import ru.m210projects.Build.Pattern.BuildNet;
import ru.m210projects.Build.Pattern.MenuItems.MenuHandler;

public abstract class LoadingAdapter extends ScreenAdapter {

	protected Runnable toLoad;
	protected int frames;
	private String title;
	
	public BuildNet net;
	public Engine engine;
	protected BuildGame game;
	protected MenuHandler menu;
	
	public LoadingAdapter(BuildGame game)
	{
		this.game = game;
		this.engine = game.pEngine;
		this.net = game.pNet;
		this.menu = game.pMenu;
	}
	
	@Override
	public void show()
	{
		net.ready2send = false;
		frames = 0;
	}
	
	@Override
	public void hide () {
		title = null;
	}
	
	public ScreenAdapter init(Runnable toLoad)
	{
		this.toLoad = toLoad;
		return this;
	}
	
	public ScreenAdapter setTitle(String title)
	{
		this.title = title;
		return this;
	}
	
	protected abstract void draw(String title, float delta);
	
	@Override
	public void render(float delta) {
		engine.clearview(0);
		
		draw(title, delta);

		if(toLoad != null && frames > 10)
		{
			BuildGdx.app.postRunnable(toLoad);
			toLoad = null;
		}
		
		engine.sampletimer();
		engine.nextpage();
		frames++;
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
