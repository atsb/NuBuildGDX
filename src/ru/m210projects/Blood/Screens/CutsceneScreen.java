// This file is part of BloodGDX.
// Copyright (C) 2017-2020  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// BloodGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// BloodGDX is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with BloodGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.Blood.Screens;

import static ru.m210projects.Blood.Globals.kAngleMask;
import static ru.m210projects.Blood.Globals.kPalNormal;
import static ru.m210projects.Blood.Main.cfg;
import static ru.m210projects.Blood.SOUND.sndStopAllSounds;
import static ru.m210projects.Blood.Screen.curPalette;
import static ru.m210projects.Blood.Screen.scrGLSetDac;
import static ru.m210projects.Blood.Screen.scrReset;
import static ru.m210projects.Blood.Screen.scrSetPalette;
import static ru.m210projects.Blood.Strings.cutskip;
import static ru.m210projects.Blood.Trig.Sin;
import static ru.m210projects.Build.Engine.MAXTILES;
import static ru.m210projects.Build.Engine.totalclock;
import static ru.m210projects.Build.OnSceenDisplay.Console.OSDTEXT_RED;
import static ru.m210projects.Build.Pragmas.mulscale;

import java.io.FileNotFoundException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.Audio.BuildAudio.Driver;
import ru.m210projects.Build.Audio.Source;
import ru.m210projects.Build.Loader.WAVLoader;
import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Build.Pattern.BuildFont;
import ru.m210projects.Build.Pattern.BuildFont.TextAlign;
import ru.m210projects.Build.Pattern.BuildGame;
import ru.m210projects.Build.Pattern.ScreenAdapters.MovieScreen;
import ru.m210projects.BuildSmacker.SMKFile;
import ru.m210projects.BuildSmacker.SMKFile.Track;

public class CutsceneScreen extends MovieScreen {
	
	private Source smkSource;
	
	protected static class SMKMovieFile implements MovieFile {
		protected SMKFile smkfil;
		protected boolean paletteChanged;
		
		public SMKMovieFile(String file) throws Exception
		{
			byte[] smkbuf = BuildGdx.cache.getBytes(file, 0);
			if (smkbuf == null)
				throw new FileNotFoundException();
			
			ByteBuffer bb = ByteBuffer.wrap(smkbuf);
			bb.order(ByteOrder.LITTLE_ENDIAN);
			
			smkfil = new SMKFile(bb);
			smkfil.setEnable(Track.All, Track.Video.mask());
		}
		
		@Override
		public int getFrames() {
			return smkfil.getFrames();
		}

		@Override
		public float getRate() {
			return smkfil.getRate() / 1000f;
		}

		@Override
		public byte[] getFrame(int num) {
			paletteChanged = smkfil.setFrame(num) != 0;
			return smkfil.getVideoBuffer().array();
		}

		@Override
		public byte[] getPalette() {
			return smkfil.getPalette();
		}

		@Override
		public short getWidth() {
			return (short) smkfil.getHeight();
		}

		@Override
		public short getHeight() {
			return (short) smkfil.getWidth();
		}
		
		public boolean paletteChanged(int frame) {
			return smkfil.setFrame(frame) != 0;
		}

		@Override
		public void close() {
			/* nothing */
		}

		@Override
		public void playAudio() {
			/* nothing */
		}
	}
	
	public CutsceneScreen(BuildGame game) {
		super(game, MAXTILES - 3);
		
		this.nFlags |= 4;
	}

	public boolean init(String path, String sndPath) {
		if (!cfg.showCutscenes || isInited())
			return false;

		if(!open(path))
			return false;

		smkStartWAV(sndPath);
		return true;
	}
	
	@Override
	public void show() {
		super.show();
		
		if (smkSource != null) {
			smkSource.setGlobal(1);
			smkSource.play(1.0f);
		}
	}
	
	@Override
	public void hide () {
		curPalette = -1;
		scrSetPalette(kPalNormal);
		scrReset();
		scrGLSetDac(0);
	}
	
	@Override
	protected MovieFile GetFile(String file) {
		try {
			return new SMKMovieFile(file);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	protected void StopAllSounds() {
		sndStopAllSounds();
	}

	@Override
	protected byte[] DoDrawFrame(int num) {
		byte[] pic = mvfil.getFrame(num);
		if (((SMKMovieFile) mvfil).paletteChanged) 
			changepalette(mvfil.getPalette());
		return pic;
	}

	@Override
	protected BuildFont GetFont() {
		return game.getFont(4);
	}

	@Override
	protected void DrawEscText(BuildFont font, int pal) {
		int shade = 32 + mulscale(32, Sin((20 * totalclock) & kAngleMask), 30);
		font.drawText(160, 5, cutskip, shade, pal, TextAlign.Center, 2, false);
	}

	private void smkStartWAV(String sampleName) {
		if (!BuildGdx.audio.IsInited(Driver.Sound))
			return;

		if (cfg.noSound || sampleName == null || sampleName.length() == 0)
			return;

		byte[] buf = BuildGdx.cache.getBytes(sampleName, 0);
		if (buf == null) {
			Console.Println("Could not load wav file: " + sampleName, OSDTEXT_RED);
			return;
		}

		int sampleSize = buf.length;
		if (sampleSize <= 0)
			return;

		try {
			WAVLoader wav = new WAVLoader(buf);
			BuildGdx.audio.getSound().resetListener();
			smkSource = BuildGdx.audio.newSound(wav.data, wav.rate, wav.bits, wav.channels, 255);
		} catch (Exception e) {
			Console.Println(e.getMessage() + " in " + sampleName, OSDTEXT_RED);
			return;
		}
	}
	
	@Override
	protected void close() {
		super.close();

		if (smkSource != null)
			smkSource.dispose();
		smkSource = null;
	}
}
