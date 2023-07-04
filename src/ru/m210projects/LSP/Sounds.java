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

package ru.m210projects.LSP;

import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Build.Pragmas.*;
import static ru.m210projects.Build.Gameutils.*;
import static ru.m210projects.Build.OnSceenDisplay.Console.OSDTEXT_RED;
import static ru.m210projects.LSP.Main.*;
import static ru.m210projects.LSP.Globals.*;

import java.nio.ByteBuffer;

import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.Audio.MusicSource;
import ru.m210projects.Build.Audio.Source;
import ru.m210projects.Build.Audio.SourceCallback;
import ru.m210projects.Build.Audio.BuildAudio.Driver;
import ru.m210projects.Build.Audio.BuildAudio.MusicType;
import ru.m210projects.Build.Audio.Sound.SystemType;
import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.LSP.Types.VOC;

public class Sounds {

	public static class SoundResource {
		public ByteBuffer ptr;
		public int num;
		public int bits, rate;

		public SoundResource(byte[] data, int num) {
			VOC voc = new VOC(data);
			this.bits = voc.samplesize;
			this.rate = voc.samplerate;
			this.ptr = voc.sampledata;
			this.num = num;
		}
	}

	public static class ActiveSound {
		public Source pHandle;
		public int spr;
		public int soundnum;
		
		public void stop() {
			if (pHandle != null)
				pHandle.dispose();
			pHandle = null;
			spr = soundnum = -1;
		}
	}

	private static SourceCallback<Integer> callback = new SourceCallback<Integer>() {
		@Override
		public void run(Integer ch) {
			sActiveSound[ch].stop();
		}
	};

	public static SoundResource[] SoundBufs = new SoundResource[100];
	public static int currChannel;
	public static ActiveSound sActiveSound[];
	public static int currTrack = -1;
	public static MusicSource currMusic = null;
	public static int currSong = -1;

	public static void sndInit() {
		Console.Println("Initializing sound system");
		if (cfg.maxvoices < 8)
			cfg.maxvoices = 8;

		if (BuildGdx.audio.getSound().init(SystemType.Stereo, cfg.maxvoices, cfg.resampler_num)) {
			BuildGdx.audio.setVolume(Driver.Sound, cfg.soundVolume);
		} else {
			Console.Println(BuildGdx.audio.getName(Driver.Sound) + " initialization failed", OSDTEXT_RED);
		}

		if (!BuildGdx.audio.getMusic().init())
			Console.Println(BuildGdx.audio.getName(Driver.Music) + " initialization failed", OSDTEXT_RED);

		if (!cfg.muteMusic)
			BuildGdx.audio.setVolume(Driver.Music, cfg.musicVolume);
		else
			BuildGdx.audio.setVolume(Driver.Music, 0);

		sActiveSound = new ActiveSound[cfg.maxvoices];
		for (int i = 0; i < sActiveSound.length; i++)
			sActiveSound[i] = new ActiveSound();
		currChannel = 0;
	}

	public static boolean midRestart() {
		stopmusic();

		if (!BuildGdx.audio.getMusic().init()) {
			Console.Println(BuildGdx.audio.getName(Driver.Music) + " initialization failed", OSDTEXT_RED);
			return false;
		}

		return true;
	}

	public static boolean sndRestart(int nvoices, int resampler) {
		BuildGdx.audio.getSound().stopAllSounds();
		BuildGdx.audio.getSound().uninit();
		cfg.maxvoices = nvoices;

		Console.Println("Sound restarting...");

		if (BuildGdx.audio.getSound().init(SystemType.Stereo, nvoices, resampler)) {
			BuildGdx.audio.setVolume(Driver.Sound, cfg.soundVolume);
		} else {
			Console.Println(BuildGdx.audio.getName(Driver.Sound) + " initialization failed", OSDTEXT_RED);
			return false;
		}
		sActiveSound = new ActiveSound[cfg.maxvoices];
		for (int i = 0; i < sActiveSound.length; i++)
			sActiveSound[i] = new ActiveSound();
		currChannel = 0;

		return true;
	}

	public static void stopallsounds() {
		currChannel = 0;
		BuildGdx.audio.getSound().stopAllSounds();
		for (int i = 0; i < sActiveSound.length; i++) 
			sActiveSound[i].stop();
	}

	public static void stopmusic() {
		if (currMusic != null)
			currMusic.stop();

		currTrack = -1;
		currMusic = null;
	}

	public static void startmusic(int num) {
		if(!cfg.muteMusic)
			BuildGdx.audio.setVolume(Driver.Music, cfg.musicVolume);
		else BuildGdx.audio.setVolume(Driver.Music, 0);
		
		if (cfg.muteMusic || (currMusic != null && currMusic.isPlaying() && currSong != -1 && currSong == num))
			return;
		
		if ( cfg.musicType == 1 && game.currentDef != null) { //music from def file
			String himus = game.currentDef.audInfo.getDigitalInfo(Integer.toString(num));
			if(himus != null)
			{
				stopmusic();
				if((currMusic = BuildGdx.audio.newMusic(MusicType.Digital, himus)) != null) {
					currSong = num;
					System.err.println("Play ogg music " + num);
					currMusic.play(true);
					return;
				}
			} 
		}

		byte[] data = BuildGdx.cache.getBytes(num + 103, "");
		if (data == null || data.length <= 0)
			return;

		stopmusic();
		if ((currMusic = BuildGdx.audio.newMusic(MusicType.Midi, data)) != null) {
			System.err.println("Play midi music " + num);
			currSong = num;
			currMusic.play(true);
		}
	}

	private static boolean loadsound(int num) {
		byte[] data = BuildGdx.cache.getBytes(num + 3, "");
		if (data == null) {
			Console.Println("Sound " + num + " not found.", Console.OSDTEXT_RED);
			return false;
		}

		SoundBufs[num] = new SoundResource(data, num);
		return true;
	}

	private static ActiveSound preparesound(int num, int priority) {
		if (cfg.noSound || !BuildGdx.audio.IsInited(Driver.Sound) || num < 0 || num >= SoundBufs.length)
			return null;

		if (SoundBufs[num] == null && !loadsound(num))
			return null;

		int channel = (currChannel++) & (sActiveSound.length - 1);
		ActiveSound pSound = sActiveSound[channel];
		SoundResource res = SoundBufs[num];
		Source hVoice = BuildGdx.audio.newSound(res.ptr, res.rate, res.bits, priority);
		if (hVoice != null) {
			hVoice.setCallback(callback, channel);
			pSound.pHandle = hVoice;
			pSound.soundnum = channel;

			return pSound;
		}

		return null;
	}

	public static ActiveSound playsound(int num) {
		ActiveSound pSound = preparesound(num, 256);
		if (pSound != null) {
			pSound.spr = -1;
			pSound.pHandle.setGlobal(1);
			pSound.pHandle.play(1.0f);
		}
		return pSound;
	}

	public static ActiveSound playsound(int num, int spriteid) {
		int vol = calcvolume(spriteid);
		if(vol == 0) return null;
		
		ActiveSound pSound = preparesound(num, vol);
		if (pSound != null) {
			pSound.spr = spriteid;
			pSound.pHandle.play(vol / 255.0f);
		}
		return pSound;
	}
	
	public static int calcvolume(int spr)
	{
		SPRITE pSprite = sprite[spr];
		int dist = klabs(gPlayer[screenpeek].x - pSprite.x) + klabs(gPlayer[screenpeek].y - pSprite.y);
		int vol = BClipLow(65536 - (dist << 2), 0);
		if (dist < 1500)
			vol = 65536;
		return BClipRange((int) ((vol / 65536.0f) * 255), 0, 255);
	}

	public static void updatesounds() {
		if (cfg.noSound || !BuildGdx.audio.IsInited(Driver.Sound))
			return;

		BuildGdx.audio.getSound().setListener(gPlayer[screenpeek].x, gPlayer[screenpeek].z >> 4, gPlayer[screenpeek].y, (int) gPlayer[screenpeek].ang);
		for (int i = 0; i < sActiveSound.length; i++) {
			if (sActiveSound[i].pHandle == null)
				continue;
			
			if(!sActiveSound[i].pHandle.isActive()) {
				sActiveSound[i].pHandle.callback.run(i);
				continue;
			}
			ActiveSound pSound = sActiveSound[i];
			int spr = pSound.spr;
			if(spr != -1) {
				SPRITE pSprite = sprite[spr];
				pSound.pHandle.setPosition(pSprite.x, pSprite.z >> 4, pSprite.y);
				pSound.pHandle.setVolume(calcvolume(spr) / 255.0f);
			}
		}
	}

	public static void sndHandlePause(boolean gPaused) {
		if (gPaused) {
			if (currMusic != null)
				currMusic.pause();
			BuildGdx.audio.getSound().stopAllSounds();
		} else {
			if (!cfg.muteMusic && currMusic != null)
				currMusic.resume();
		}
	}

}
