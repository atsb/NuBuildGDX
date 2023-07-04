// This file is part of DukeGDX.
// Copyright (C) 2019  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// DukeGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// DukeGDX is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with DukeGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.Duke3D.Screens;

import static ru.m210projects.Build.Engine.sintable;
import static ru.m210projects.Build.Engine.totalclock;
import static ru.m210projects.Build.Engine.xdim;
import static ru.m210projects.Build.Engine.ydim;
import static ru.m210projects.Build.Pragmas.mulscale;
import static ru.m210projects.Duke3D.Globals.TILE_ANIM;
import static ru.m210projects.Duke3D.Globals.ud;
import static ru.m210projects.Duke3D.SoundDefs.BIGBANG;
import static ru.m210projects.Duke3D.SoundDefs.BOSS4_DEADSPEECH;
import static ru.m210projects.Duke3D.SoundDefs.DUKE_GRUNT;
import static ru.m210projects.Duke3D.SoundDefs.DUKE_UNDERWATER;
import static ru.m210projects.Duke3D.SoundDefs.ENDSEQVOL2SND1;
import static ru.m210projects.Duke3D.SoundDefs.ENDSEQVOL2SND2;
import static ru.m210projects.Duke3D.SoundDefs.ENDSEQVOL2SND3;
import static ru.m210projects.Duke3D.SoundDefs.ENDSEQVOL2SND4;
import static ru.m210projects.Duke3D.SoundDefs.ENDSEQVOL2SND5;
import static ru.m210projects.Duke3D.SoundDefs.ENDSEQVOL2SND6;
import static ru.m210projects.Duke3D.SoundDefs.ENDSEQVOL2SND7;
import static ru.m210projects.Duke3D.SoundDefs.ENDSEQVOL3SND2;
import static ru.m210projects.Duke3D.SoundDefs.ENDSEQVOL3SND3;
import static ru.m210projects.Duke3D.SoundDefs.ENDSEQVOL3SND5;
import static ru.m210projects.Duke3D.SoundDefs.ENDSEQVOL3SND6;
import static ru.m210projects.Duke3D.SoundDefs.ENDSEQVOL3SND7;
import static ru.m210projects.Duke3D.SoundDefs.ENDSEQVOL3SND8;
import static ru.m210projects.Duke3D.SoundDefs.ENDSEQVOL3SND9;
import static ru.m210projects.Duke3D.SoundDefs.FLY_BY;
import static ru.m210projects.Duke3D.SoundDefs.INTRO4_1;
import static ru.m210projects.Duke3D.SoundDefs.INTRO4_2;
import static ru.m210projects.Duke3D.SoundDefs.INTRO4_3;
import static ru.m210projects.Duke3D.SoundDefs.INTRO4_4;
import static ru.m210projects.Duke3D.SoundDefs.INTRO4_5;
import static ru.m210projects.Duke3D.SoundDefs.INTRO4_6;
import static ru.m210projects.Duke3D.SoundDefs.INTRO4_B;
import static ru.m210projects.Duke3D.SoundDefs.PIPEBOMB_EXPLODE;
import static ru.m210projects.Duke3D.SoundDefs.SHORT_CIRCUIT;
import static ru.m210projects.Duke3D.SoundDefs.SQUISHED;
import static ru.m210projects.Duke3D.SoundDefs.THUD;
import static ru.m210projects.Duke3D.SoundDefs.VOL4ENDSND1;
import static ru.m210projects.Duke3D.SoundDefs.VOL4ENDSND2;
import static ru.m210projects.Duke3D.SoundDefs.WIND_AMBIENCE;
import static ru.m210projects.Duke3D.SoundDefs.WIND_REPEAT;
import static ru.m210projects.Duke3D.Sounds.sound;

import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.Audio.Source;
import ru.m210projects.Build.Pattern.BuildFont;
import ru.m210projects.Build.Pattern.BuildFont.TextAlign;
import ru.m210projects.Build.Pattern.BuildGame;
import ru.m210projects.Build.Pattern.ScreenAdapters.MovieScreen;
import ru.m210projects.Build.Types.Tile;
import ru.m210projects.Duke3D.Sounds;
import ru.m210projects.Duke3D.Types.AnimFile;

public class AnmScreen extends MovieScreen {

	private int lastanimhack;
	private int bonuscnt = 0;
	private String name;
	private Source scenevoice;
	private final int[] scenevoices = { ENDSEQVOL3SND5, ENDSEQVOL3SND6, ENDSEQVOL3SND7, ENDSEQVOL3SND8, ENDSEQVOL3SND9 };

	public AnmScreen(BuildGame game) {
		super(game, TILE_ANIM);

		this.nFlags |= 4;
	}

	public boolean init(String fn, int t) {
		if (isInited())
			return false;

		if (!open(fn))
			return false;

		lastanimhack = t;
		name = fn;

		return true;
	}

	@Override
	protected boolean play() {
		if (mvfil != null) {
			if (LastMS == -1)
				LastMS = engine.getticks();
			Tile pic = engine.getTile(TILE_MOVIE);

			long ms = engine.getticks();
			long dt = ms - LastMS;
			mvtime += dt;
			float tick = mvfil.getRate();
			if (mvtime >= tick) {
				if (frame < mvfil.getFrames()) {
					pic.data = DoDrawFrame(frame);
					engine.getrender().invalidatetile(TILE_MOVIE, 0, -1); // JBF 20031228

					frame++;
				} else if (!name.equals("radlogo.anm"))
					return false;
				mvtime -= tick;
			}
			LastMS = ms;

			if (pic.getWidth() <= 0)
				return false;

			if (pic.data != null)
				engine.rotatesprite(nPosX << 16, nPosY << 16, nScale, 512, TILE_MOVIE, 0, 0, nFlags, 0, 0, xdim - 1,
						ydim - 1);
			return true;
		}
		return false;
	}

	@Override
	protected MovieFile GetFile(String file) {
		byte[] animbuf = BuildGdx.cache.getBytes(file, 0);
		if (animbuf == null)
			return null;

		return new AnimFile(animbuf);
	}

	@Override
	protected void StopAllSounds() {
		Sounds.StopAllSounds();
		Sounds.sndStopMusic();
	}

	@Override
	protected byte[] DoDrawFrame(int frame) {
		if (lastanimhack == 8)
			endanimvol41(frame);
		else if (lastanimhack == 10)
			endanimvol42(frame);
		else if (lastanimhack == 11)
			endanimvol43(frame);
		else if (lastanimhack == 9)
			intro42animsounds(frame);
		else if (lastanimhack == 7)
			intro4animsounds(frame);
		else if (lastanimhack == 6)
			first4animsounds(frame);
		else if (lastanimhack == 5)
			logoanimsounds(frame);
		else if (lastanimhack < 4)
			endanimsounds(frame);

		return mvfil.getFrame(frame);
	}

	@Override
	protected BuildFont GetFont() {
		return game.getFont(0);
	}

	@Override
	protected void callback() {
		if (!checkAnm()) {
			super.callback();
		}
	}

	@Override
	protected void DrawEscText(BuildFont font, int pal) {
		int shade = 16 + mulscale(16, sintable[(20 * totalclock) & 2047], 16);
		font.drawText(160, 5, "Press ESC to skip", shade, pal, TextAlign.Center, 2, true);
	}

	private boolean checkAnm() {
		if (name.equals("vol41a.anm")) {
			close();
			init("vol42a.anm", 7);
			return true;
		}

		if (name.equals("vol42a.anm")) {
			close();
			init("vol43a.anm", 9);
			return true;
		}

		if (name.equals("vol4e1.anm")) {
			close();
			init("vol4e2.anm", 10);
			return true;
		}

		if (name.equals("vol4e2.anm")) {
			close();
			init("vol4e3.anm", 11);
			return true;
		}

		if (name.equals("radlogo.anm")) {
			if (bonuscnt >= 0 && bonuscnt < scenevoices.length) {
				if (scenevoice == null || !scenevoice.isActive()) {
					scenevoice = sound(scenevoices[bonuscnt]);
					bonuscnt++;
				}
			}
			return true; // no autoskip
		}

        return name.equals("duketeam.anm"); // no autoskip
    }

	private void endanimsounds(int fr) {
		switch (ud.volume_number) {
		case 0:
			break;
		case 1:
			switch (fr) {
			case 1:
				sound(WIND_AMBIENCE);
				break;
			case 26:
				sound(ENDSEQVOL2SND1);
				break;
			case 36:
				sound(ENDSEQVOL2SND2);
				break;
			case 54:
				sound(THUD);
				break;
			case 62:
				sound(ENDSEQVOL2SND3);
				break;
			case 75:
				sound(ENDSEQVOL2SND4);
				break;
			case 81:
				sound(ENDSEQVOL2SND5);
				break;
			case 115:
				sound(ENDSEQVOL2SND6);
				break;
			case 124:
				sound(ENDSEQVOL2SND7);
				break;
			}
			break;
		case 2:
			switch (fr) {
			case 1:
				sound(WIND_REPEAT);
				break;
			case 98:
				sound(DUKE_GRUNT);
				break;
			case 82 + 20:
				sound(THUD);
				sound(SQUISHED);
				break;
			case 104 + 20:
				sound(ENDSEQVOL3SND3);
				break;
			case 114 + 20:
				sound(ENDSEQVOL3SND2);
				break;
			case 158:
				sound(PIPEBOMB_EXPLODE);
				break;
			}
			break;
		}
	}

	private void logoanimsounds(int fr) {
		switch (fr) {
		case 1:
			sound(FLY_BY);
			break;
		case 19:
			sound(PIPEBOMB_EXPLODE);
			break;
		}
	}

	private void intro4animsounds(int fr) {
		switch (fr) {
		case 1:
			sound(INTRO4_B);
			break;
		case 12:
		case 34:
			sound(SHORT_CIRCUIT);
			break;
		case 18:
			sound(INTRO4_5);
			break;
		}
	}

	private void first4animsounds(int fr) {
		switch (fr) {
		case 1:
			sound(INTRO4_1);
			break;
		case 12:
			sound(INTRO4_2);
			break;
		case 7:
			sound(INTRO4_3);
			break;
		case 26:
			sound(INTRO4_4);
			break;
		}
	}

	private void intro42animsounds(int fr) {
		switch (fr) {
		case 10:
			sound(INTRO4_6);
			break;
		}
	}

	private void endanimvol41(int fr) {
		switch (fr) {
		case 3:
			sound(DUKE_UNDERWATER);
			break;
		case 35:
			sound(VOL4ENDSND1);
			break;
		}
	}

	private void endanimvol42(int fr) {
		switch (fr) {
		case 11:
			sound(DUKE_UNDERWATER);
			break;
		case 20:
			sound(VOL4ENDSND1);
			break;
		case 39:
			sound(VOL4ENDSND2);
			break;
		case 50:
			BuildGdx.audio.getSound().stopAllSounds();
			break;
		}
	}

	private void endanimvol43(int fr) {
		switch (fr) {
		case 1:
			sound(BOSS4_DEADSPEECH);
			break;
		case 40:
			sound(VOL4ENDSND1);
			sound(DUKE_UNDERWATER);
			break;
		case 50:
			sound(BIGBANG);
			break;
		}
	}
}
