package ru.m210projects.Build.Pattern.ScreenAdapters;

import static ru.m210projects.Build.Engine.MAXTILES;
import static ru.m210projects.Build.Engine.pow2char;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.badlogic.gdx.ScreenAdapter;

import ru.m210projects.Build.Engine;
import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.Architecture.BuildFrame.FrameType;
import ru.m210projects.Build.Architecture.BuildGraphics.Option;
import ru.m210projects.Build.Pattern.BuildGame;
import ru.m210projects.Build.Pattern.BuildNet;
import ru.m210projects.Build.Pattern.MenuItems.MenuHandler;
import ru.m210projects.Build.Render.GLRenderer;
import ru.m210projects.Build.Render.GLRenderer.GLPreloadFlag;
import ru.m210projects.Build.Types.Tile;
import ru.m210projects.Build.Types.Tile.AnimType;

public abstract class PrecacheAdapter extends ScreenAdapter {

	private class PrecacheQueue {
		private final String name;
		private final Runnable runnable;

		public PrecacheQueue(String name, Runnable runnable) {
			this.name = name;
			this.runnable = runnable;
		}
	}

	private final byte[] tiles;
	private int currentIndex = 0;
	private final List<PrecacheQueue> queues = new ArrayList<PrecacheQueue>();
	protected Runnable toLoad;
	protected boolean revalidate;

	protected BuildNet net;
	protected Engine engine;
	protected BuildGame game;
	protected MenuHandler menu;

	public void addQueue(String name, Runnable runnable) {
		if (!game.pCfg.gPrecache)
			return;

		if(queues.size() == 0)
			queues.add(new PrecacheQueue("Preload models...", glmodels));

		queues.add(new PrecacheQueue(name, runnable));
	}

	public void clearQueue() {
		queues.clear();

		queues.add(new PrecacheQueue("Models loading...", glmodels));
	}

	public PrecacheAdapter(BuildGame game) {
		this.game = game;
		this.engine = game.pEngine;
		this.net = game.pNet;
		this.menu = game.pMenu;
		this.tiles = new byte[MAXTILES >> 3];
	}

	public ScreenAdapter init(boolean revalidate, Runnable toLoad) {
		this.toLoad = toLoad;
		this.revalidate = revalidate;
		return this;
	}

	@Override
	public void show() {
		net.ready2send = false;
		currentIndex = 0;
		Arrays.fill(tiles, (byte) 0);
	}

	public void addTile(int tile) {
		Tile pic = engine.getTile(tile);
		if (pic.getType() != AnimType.None) {
			int frames = pic.getFrames();
			for (int i = frames; i >= 0; i--) {
				if (pic.getType() == AnimType.Backward)
					tiles[(tile - i) >> 3] |= (1 << ((tile - i) & 7));
				else
					tiles[(tile + i) >> 3] |= (1 << ((tile + i) & 7));
			}
		} else
			tiles[tile >> 3] |= pow2char[tile & 7];
	}

	protected abstract void draw(String title, int index);

	protected Runnable glpreload = new Runnable() {
		@Override
		public void run() {
			GLRenderer gl = engine.glrender();
			if (gl != null)
				gl.preload(GLPreloadFlag.Other);

			BuildGdx.app.postRunnable(toLoad);
			toLoad = null;
		}
	};

	protected Runnable glmodels = new Runnable() {
		@Override
		public void run() {
			GLRenderer gl = engine.glrender();
			if (gl != null)
				gl.preload(GLPreloadFlag.Models);
		}
	};

	@Override
	public void render(float delta) {
		engine.clearview(0);
		if (currentIndex >= queues.size()) {
			draw("Getting ready...", -1);
			if (toLoad != null) {
				BuildGdx.app.postRunnable(glpreload);
			}
		} else {
			PrecacheQueue current = queues.get(currentIndex);
			draw(current.name, currentIndex);
			BuildGdx.app.postRunnable(current.runnable);
			currentIndex++;
		}

		engine.sampletimer();
		engine.nextpage();
	}

	protected void doprecache(int method) {
		GLRenderer gl = engine.glrender();
		for (int i = 0; i < MAXTILES; i++) {
			if (tiles[i >> 3] == 0) {
				i += 7;
				continue;
			}

			Tile pic = engine.getTile(i);
			if ((tiles[i >> 3] & pow2char[i & 7]) != 0) {
				if (!revalidate && pic.data != null)
					continue;

				if (pic.data == null)
					engine.loadtile(i);
				if (gl != null)
					gl.precache(i, 0, method);
			}
		}
		Arrays.fill(tiles, (byte) 0);
	}

	@Override
	public void pause() {
		if (BuildGdx.graphics.getFrameType() == FrameType.GL)
			BuildGdx.graphics.extra(Option.GLDefConfiguration);
	}

	@Override
	public void resume() {
		game.updateColorCorrection();
	}
}
