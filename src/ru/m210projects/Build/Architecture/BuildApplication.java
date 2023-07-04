// This file is part of BuildGDX.
// Copyright (C) 2017-2020  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// BuildGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// BuildGDX is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with BuildGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.Build.Architecture;

import com.badlogic.gdx.Application;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.ApplicationLogger;
import com.badlogic.gdx.Audio;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.LifecycleListener;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Clipboard;
import com.badlogic.gdx.utils.GdxRuntimeException;

import ru.m210projects.Build.Architecture.BuildFrame.FrameType;
import ru.m210projects.Build.Pattern.BuildGame;
import ru.m210projects.Build.Render.Renderer.RenderType;

public class BuildApplication implements Application {

	public enum Platform { Windows, Linux, MacOSX, Android }

    protected BuildFrame frame;
	protected final BuildConfiguration config;
	protected final ApplicationListener listener;
	protected final Platform platform;
	protected final ApplicationType type;
	protected final Clipboard clipboard;
	protected final Files files;

	protected int version;
	protected boolean running = true;
	protected Thread mainLoopThread;
	protected final Array<Runnable> runnables = new Array<Runnable>();
	protected final Array<Runnable> executedRunnables = new Array<Runnable>();

	public BuildApplication (BuildGame listener, final ApplicationFactory factory, RenderType type) {
		this.listener = listener;
		this.config = factory.getConfiguration();

		if (config.title == null) config.title = listener.getClass().getSimpleName();

		frame = factory.getFrame();
		if(config.getIconPaths().size > 0)
			frame.icon = this.getClass().getResource("/" + config.getIconPaths().first());

		Gdx.app = BuildGdx.app = this;
		Gdx.files = BuildGdx.files = files = factory.getFiles();
		BuildGdx.audio = factory.getAudio();
		BuildGdx.message = factory.getMessage();
		BuildGdx.message.setFrame(frame);
		BuildGdx.controllers = factory.getControllers();
		this.platform = factory.getPlatform();
		this.type = factory.getApplicationType();
		this.clipboard = factory.getClipboard();
		this.version = factory.getVersion();

		initialize(type.getFrameType());
	}

	private void initialize (final FrameType type) {
		if(platform == Platform.Android) {
			frame.setType(type);
		}
		else {
			mainLoopThread = new Thread("Build Application") { //ContextGL
				@Override
				public void run () {
					try {
						frame.setType(type);

						mainLoop();
					} catch (Throwable t) {
						t.printStackTrace();

						destroyLoop();

						if (t instanceof RuntimeException)
							throw (RuntimeException)t;
						else throw new GdxRuntimeException(t);
					}
				}
			};
			mainLoopThread.start();
		}
	}

	private void mainLoop () {
		listener.create();

		while (running) {
			BuildGdx.input.processMessages();

			switch(frame.getStatus()) {
			case Closed:
				exit();
				break;
			case Running:
				break;
			case Pause:
				listener.pause();
				break;
			case Resume:
				listener.resume();
				break;
			case Changed:
				listener.resize(config.width, config.height);
				break;
			}

			boolean shouldRender = executeRunnables();

            // If one of the runnables set running to false, for example after an exit().
			if (!running) break;

			if(frame.process(shouldRender)) {
				listener.render();
				frame.update();
			}
		}
		destroyLoop();
	}

	private void destroyLoop() {
		if(BuildGdx.input != null)
			BuildGdx.input.setCursorCatched(false);
		if(listener != null) {
			listener.pause();
			listener.dispose();
		}
		if(BuildGdx.audio != null)
			BuildGdx.audio.dispose();
		if(BuildGdx.message != null)
			BuildGdx.message.dispose();
		frame.dispose();
	}

	public void stop () {
		running = false;
		try {
			mainLoopThread.join();
		} catch (Exception ex) {
		}
	}

	@Override
	public void exit () {
		postRunnable(new Runnable() {
			@Override
			public void run () {
				running = false;
			}
		});
	}

	public boolean executeRunnables () {
		synchronized (runnables) {
			for (int i = runnables.size - 1; i >= 0; i--)
				executedRunnables.add(runnables.get(i));
			runnables.clear();
		}
		if (executedRunnables.size == 0) return false;
		do
			executedRunnables.pop().run();
		while (executedRunnables.size > 0);
		return true;
	}

	@Override
	public ApplicationListener getApplicationListener()
	{
		return listener;
	}

	public void setFrame(FrameType type)
	{
		frame.setType(type);
	}

	public boolean isActive()
	{
		return frame.getGraphics().isActive();
	}

	@Override
	public void postRunnable (Runnable runnable) {
		synchronized (runnables) {
			runnables.add(runnable);
		}
	}

	@Override
	public Clipboard getClipboard() {
		return clipboard;
	}

	public Platform getPlatform() {
		return platform;
	}

	@Override
	public ApplicationType getType() {
		return type;
	}

	//Gdx Application

	@Override
	public Graphics getGraphics() {
		return frame.getGraphics();
	}

	@Override
	public Input getInput() {
		return frame.getInput();
	}

	@Override
	public Files getFiles() {
		return files;
	}

	@Override
	public int getVersion() {
		return version;
	}

	@Override
	public long getJavaHeap() {
		return Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
	}

	@Override
	public long getNativeHeap() {
		return getJavaHeap();
	}

	//Gdx Application unused

	@Override
	public void log(String tag, String message) {}

	@Override
	public void log(String tag, String message, Throwable exception) {}

	@Override
	public void error(String tag, String message) {}

	@Override
	public void error(String tag, String message, Throwable exception) {}

	@Override
	public void debug(String tag, String message) {}

	@Override
	public void debug(String tag, String message, Throwable exception) {}

	@Override
	public void setLogLevel(int logLevel) {}

	@Override
	public int getLogLevel() {
		return 0;
	}

	@Override
	public Audio getAudio() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Net getNet() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setApplicationLogger(ApplicationLogger applicationLogger) {
		// TODO Auto-generated method stub

	}

	@Override
	public ApplicationLogger getApplicationLogger() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Preferences getPreferences(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addLifecycleListener(LifecycleListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeLifecycleListener(LifecycleListener listener) {
		// TODO Auto-generated method stub

	}
}
