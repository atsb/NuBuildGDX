// This file is part of LSPGDX.
// Copyright (C) 2020  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// LSPGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// LSPGDX is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with LSPGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.LSP.Screens;

import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Build.Input.Keymap.ANYKEY;
import static ru.m210projects.Build.Pragmas.mulscale;
import static ru.m210projects.LSP.Globals.*;

import ru.m210projects.Build.Pattern.BuildGame;
import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.Pattern.BuildFont.TextAlign;
import ru.m210projects.Build.Pattern.ScreenAdapters.SkippableAdapter;
import ru.m210projects.Build.Render.GLRenderer.GLInvalidateFlag;
import ru.m210projects.Build.Settings.BuildSettings;
import ru.m210projects.Build.Types.Tile;
import ru.m210projects.LSP.Types.MovieFile;

public class MovieScreen extends SkippableAdapter {

	private MovieFile fil;
	private Runnable callback;
	private int gCutsClock;
	private int frame;
	private long time;
	private long LastMS;

	private int whitepal;

	public MovieScreen(BuildGame game) {
		super(game);
	}

	@Override
	public void show() {
		if (game.pMenu.gShowMenu)
			game.pMenu.mClose();

		engine.sampletimer();
		LastMS = engine.getticks();
		gCutsClock = totalclock = 0;

//		StopAllSounds();
//		sndStopMusic();
	}

	@Override
	public void hide() {
		engine.setbrightness(BuildSettings.paletteGamma.get(), palette, GLInvalidateFlag.All);
	}

	@Override
	public void skip() {
		close();
		super.skip();
	}

	public MovieScreen setCallback(Runnable callback) {
		this.callback = callback;
		this.setSkipping(callback);
		return this;
	}

	public boolean init(String fn) {
		if (fil != null)
			return false;

		try {
			fil = new MovieFile(fn);

			Tile pic = engine.getTile(TILE_ANIM);

			pic.setWidth(fil.getHeight());
			pic.setHeight(fil.getWidth());
			frame = 0;

			time = 0;
			LastMS = -1;

			pic.data = null;
			if (fil.getPalette() != null)
				changepalette(fil.getPalette());

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private boolean play() {
		if (fil != null) {
			if (LastMS == -1)
				LastMS = engine.getticks();

			long ms = engine.getticks();
			long dt = ms - LastMS;
			time += dt;
			float tick = 1000f / fil.getRate();
			Tile pic = engine.getTile(TILE_ANIM);

			if (time >= tick) {
				if (frame < fil.numFrames()) {
					pic.data = fil.draw(frame);
					engine.getrender().invalidatetile(TILE_ANIM, ANIM_PAL, 1 << 4);

					frame++;
				}

				time -= tick;
			}

			LastMS = ms;
			if (pic.getWidth() <= 0)
				return false;

			if (pic.data != null)
				engine.rotatesprite(0 << 16, 0 << 16, 65536, 512, TILE_ANIM, 0, ANIM_PAL, 2 + 4 + 8 + 16 + 64, 0, 0,
						xdim - 1, ydim - 1);

			if (frame >= fil.numFrames())
				return false;

			return true;
		}

		return false;
	}

	private void close() {
		fil.close();
		fil = null;
	}

	public void changepalette(byte[] pal) {
		engine.changepalette(pal);
		whitepal = -1;
		int k = 0;
		for (int i = 0; i < 256; i += 3) {
			int j = (pal[3 * i] & 0xFF) + (pal[3 * i + 1] & 0xFF) + (pal[3 * i + 2] & 0xFF);
			if (j > k) {
				k = j;
				whitepal = i;
			}
		}
	}

	@Override
	public void draw(float delta) {
		if (!play() && skipCallback != null) {
			close();
			if (callback != null) {
				BuildGdx.app.postRunnable(callback);
				callback = null;
			}
		}

		if (game.pInput.ctrlKeyStatus(ANYKEY))
			gCutsClock = totalclock;

		int shade = 16 + mulscale(16, sintable[(20 * totalclock) & 2047], 16);
		if (totalclock - gCutsClock < 200 && escSkip) // 2 sec
			game.getFont(1).drawText(160, 5, "Press ESC to skip", shade, whitepal, TextAlign.Center, 2 | 8, true);
	}

}
