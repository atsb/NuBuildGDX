package ru.m210projects.Witchaven.Screens;

import static ru.m210projects.Build.Engine.MAXPALOOKUPS;
import static ru.m210projects.Build.Engine.MAXTILES;
import static ru.m210projects.Build.Engine.RESERVEDPALS;
import static ru.m210projects.Build.Engine.palette;
import static ru.m210projects.Build.Engine.sintable;
import static ru.m210projects.Build.Engine.totalclock;
import static ru.m210projects.Build.Pragmas.mulscale;
import static ru.m210projects.Witchaven.Main.whcfg;
import static ru.m210projects.Witchaven.WHSND.SND_CheckLoops;
import static ru.m210projects.Witchaven.WHSND.sndStopMusic;

import java.io.FileNotFoundException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.Audio.BuildAudio.Driver;
import ru.m210projects.Build.Audio.Source;
import ru.m210projects.Build.Pattern.BuildFont;
import ru.m210projects.Build.Pattern.BuildFont.TextAlign;
import ru.m210projects.Build.Pattern.BuildGame;
import ru.m210projects.Build.Pattern.ScreenAdapters.MovieScreen;
import ru.m210projects.Build.Render.GLRenderer.GLInvalidateFlag;
import ru.m210projects.Build.Settings.BuildSettings;
import ru.m210projects.BuildSmacker.SMKAudio;
import ru.m210projects.BuildSmacker.SMKFile;
import ru.m210projects.BuildSmacker.SMKFile.Track;

public class CutsceneScreen extends MovieScreen {

	protected static class SMKMovieFile implements MovieFile {
		protected SMKFile smkfil;
		protected boolean paletteChanged;
		protected Source audio;
		
		protected int sourceRate;
		protected int sourceBits;
		protected int sourceChannels;
		protected ByteBuffer sourceBuffer;
	
		public SMKMovieFile(String file) throws Exception
		{
			byte[] smkbuf = BuildGdx.cache.getBytes(file, 0);
			if (smkbuf == null)
				throw new FileNotFoundException();
			
			ByteBuffer bb = ByteBuffer.wrap(smkbuf);
			bb.order(ByteOrder.LITTLE_ENDIAN);
			
			smkfil = new SMKFile(bb);
			smkfil.setEnable(Track.All, Track.Video.mask() | Track.Audio.mask());
			
			if(!whcfg.noSound) {
				SMKAudio aud = smkfil.getAudio(0);
				if(aud != null) {
					sourceRate = aud.getRate();
					sourceBits = aud.getBits().get();
					sourceChannels = aud.getChannels().get();
					
					if(sourceRate != 0 && sourceBits != 0) {
						sourceBuffer = smkfil.getAudioBuffer(0);
						bb.order( ByteOrder.LITTLE_ENDIAN);
						bb.rewind();
					}
			    }
			}
		}

		public void playAudio() {
			if (!BuildGdx.audio.IsInited(Driver.Sound))
				return;

			if (whcfg.noSound)
				return;
			
			if(sourceRate != 0 && sourceBits != 0 && sourceBuffer != null) {
				audio = BuildGdx.audio.newSound(sourceBuffer, sourceRate, sourceBits, sourceChannels, 255);
				if (audio != null) {
					audio.setGlobal(1);
					audio.play(1.0f);
				}
			}
		}
		
		@Override
		public int getFrames() {
			return smkfil.getFrames();
		}

		@Override
		public float getRate() {
			return (float) (smkfil.getRate() / 1000f);
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
			if (audio != null)
				audio.dispose();
		}
	}
	
	public CutsceneScreen(BuildGame game) {
		super(game, MAXTILES - 2);
		
		this.nFlags |= 4;
	}

	public boolean init(String path) {
		if (!whcfg.showCutscenes || isInited())
			return false;

		return open(path);
	}

	@Override
	public void hide () {
		engine.setbrightness(BuildSettings.paletteGamma.get(), palette, GLInvalidateFlag.All);
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
		SND_CheckLoops();
		sndStopMusic();
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
		return game.getFont(1);
	}

	@Override
	protected void DrawEscText(BuildFont font, int pal) {
		int shade = 16 + mulscale(32, sintable[(20 * totalclock) & 2047], 16);
		game.getFont(0).drawText(160, 5, "Press ESC to skip", shade, MAXPALOOKUPS - RESERVEDPALS - 1, TextAlign.Center, 2, false);
	}
}
