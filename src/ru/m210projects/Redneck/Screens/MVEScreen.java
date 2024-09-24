package ru.m210projects.Redneck.Screens;

import static ru.m210projects.Build.Engine.palette;
import static ru.m210projects.Build.Engine.sintable;
import static ru.m210projects.Build.Engine.totalclock;
import static ru.m210projects.Build.Engine.xdim;
import static ru.m210projects.Build.Engine.ydim;
import static ru.m210projects.Build.Input.Keymap.ANYKEY;
import static ru.m210projects.Build.Pragmas.divscale;
import static ru.m210projects.Build.Pragmas.mulscale;
import static ru.m210projects.Redneck.Globals.ANIM_PAL;
import static ru.m210projects.Redneck.Globals.TILE_ANIM;
import static ru.m210projects.Redneck.Globals.ud;
import static ru.m210projects.Redneck.Sounds.StopAllSounds;
import static ru.m210projects.Redneck.Sounds.sndStopMusic;

import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.FileHandle.Resource;
import ru.m210projects.Build.Pattern.BuildFont.TextAlign;
import ru.m210projects.Build.Pattern.BuildGame;
import ru.m210projects.Build.Pattern.ScreenAdapters.SkippableAdapter;

import ru.m210projects.Build.Types.Tile;
import ru.m210projects.Redneck.Types.MVEFile;

public class MVEScreen extends SkippableAdapter {

	private Runnable callback;
	private int gCutsClock;
	private MVEFile anmfil;
	private long anmtime;
	private long LastMS;

	public MVEScreen(BuildGame game) {
		super(game);
	}

	@Override
	public void show() {
		if (game.pMenu.gShowMenu)
			game.pMenu.mClose();

		engine.sampletimer();
		LastMS = engine.getticks();
		gCutsClock = totalclock = 0;

		StopAllSounds();
		sndStopMusic();
	}

	@Override
	public void hide() {
		engine.setbrightness(ud.brightness >> 2, palette, true);
	}

	@Override
	public void skip() {
		close();
		super.skip();
	}

	public MVEScreen setCallback(Runnable callback) {
		this.callback = callback;
		this.setSkipping(callback);
		return this;
	}

	public boolean init(String fn) {
		if (anmfil != null)
			return false;

		Resource dat = BuildGdx.cache.open(fn, 0);
		if (dat == null)
			return false;

		try {
			anmfil = new MVEFile(dat);
			Tile pic = engine.getTile(TILE_ANIM);

			pic.setWidth(anmfil.getHeight());
			pic.setHeight(anmfil.getWidth());

			anmtime = 0;
			LastMS = -1;

			pic.data = null;

			byte[] pal = anmfil.getPalette();
			engine.changepalette(pal);

			int white = -1;
			int k = 0;
			for (int i = 0; i < 256; i += 3) {
				int j = (pal[3 * i] & 0xFF) + (pal[3 * i + 1] & 0xFF) + (pal[3 * i + 2] & 0xFF);
				if (j > k) {
					k = j;
					white = i;
				}
			}

			int palnum = ANIM_PAL - 1;
			byte[] remapbuf = new byte[768];
			for (int i = 0; i < 768; i++)
				remapbuf[i] = (byte) white;
			engine.makepalookup(palnum, remapbuf, 0, 1, 0, 1);

			for (int i = 0; i < 256; i++) {
				int tile = game.getFont(0).getTile(i);
				if (tile >= 0)
					engine.getrender().invalidatetile(tile, palnum, -1);
			}

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean anmPlay() {
		if (anmfil != null) {
			if (LastMS == -1)
				LastMS = engine.getticks();

			long ms = engine.getticks();
			long dt = ms - LastMS;
			anmtime += dt;

			boolean isDone = false;

			float tick = anmfil.getRate() / 2000.0f;
			Tile pic = engine.getTile(TILE_ANIM);

			if (anmtime >= tick) {
				if (!(isDone = anmfil.process())) {
					pic.data = anmfil.getFrame();
					engine.getrender().invalidatetile(TILE_ANIM, ANIM_PAL, -1); // JBF 20031228
				}

				anmtime -= tick;
			}

			LastMS = ms;
			if (pic.getWidth() <= 0)
				return false;

			if (pic.data != null) {
				engine.rotatesprite(160 << 16, 100 << 16, divscale(200, pic.getWidth(), 16), 512, TILE_ANIM, 0,
						ANIM_PAL, 2 | 4 | 8 | 64, 0, 0, xdim - 1, ydim - 1);
			}

			if (isDone)
				return false;

			return true;
		}

		return false;
	}

	@Override
	public void draw(float delta) {
		if (!anmPlay() && skipCallback != null) {
			close();
			if (callback != null) {
				BuildGdx.app.postRunnable(callback);
				callback = null;
			}
		}

		if (game.pInput.ctrlKeyStatus(ANYKEY))
			gCutsClock = totalclock;

		int shade = 16 + mulscale(64, sintable[(20 * totalclock) & 2047], 16);
		if (totalclock - gCutsClock < 200 && escSkip) // 2 sec
			game.getFont(0).drawText(160, 5, "Press ESC to skip", shade, ANIM_PAL - 1, TextAlign.Center, 2, true);
	}

	public boolean isInited() {
		return anmfil != null;
	}

	public void close() {
		anmfil.close();
		anmfil = null;
	}
}
