// This file is part of RedneckGDX.
// Copyright (C) 2017-2018  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// RedneckGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// RedneckGDX is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with RedneckGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.Redneck;

import static ru.m210projects.Build.Engine.MAXSECTORS;
import static ru.m210projects.Build.Engine.sector;
import static ru.m210projects.Build.Engine.sprite;
import static ru.m210projects.Build.Net.Mmulti.myconnectindex;
import static ru.m210projects.Build.OnSceenDisplay.Console.OSDTEXT_RED;
import static ru.m210projects.Build.Pragmas.divscale;
import static ru.m210projects.Build.Pragmas.klabs;
import static ru.m210projects.Build.Pragmas.mulscale;
import static ru.m210projects.Build.Gameutils.*;
import static ru.m210projects.Redneck.Actors.badguy;
import static ru.m210projects.Redneck.Gameutils.FindDistance3D;
import static ru.m210projects.Redneck.Globals.RRRA;
import static ru.m210projects.Redneck.Globals.Sound;
import static ru.m210projects.Redneck.Globals.SoundOwner;
import static ru.m210projects.Redneck.Globals.currentGame;
import static ru.m210projects.Redneck.Globals.hittype;
import static ru.m210projects.Redneck.Globals.loadfromgrouponly;
import static ru.m210projects.Redneck.Globals.ps;
import static ru.m210projects.Redneck.Globals.screenpeek;
import static ru.m210projects.Redneck.Globals.soundsiz;
import static ru.m210projects.Redneck.Globals.ud;
import static ru.m210projects.Redneck.Main.cfg;
import static ru.m210projects.Redneck.Main.engine;
import static ru.m210projects.Redneck.Main.game;
import static ru.m210projects.Redneck.Names.APLAYER;
import static ru.m210projects.Redneck.Names.BILLYCOCK;
import static ru.m210projects.Redneck.Names.BILLYRAY;
import static ru.m210projects.Redneck.Names.COOT;
import static ru.m210projects.Redneck.Names.MUSICANDSFX;
import static ru.m210projects.Redneck.SoundDefs.LASERTRIP_EXPLODE;
import static ru.m210projects.Redneck.SoundDefs.PIPEBOMB_EXPLODE;
import static ru.m210projects.Redneck.SoundDefs.RPG_EXPLODE;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.Audio.BuildAudio.Driver;
import ru.m210projects.Build.Audio.BuildAudio.MusicType;
import ru.m210projects.Build.Audio.MusicSource;
import ru.m210projects.Build.Audio.SoundData;
import ru.m210projects.Build.Audio.Sound.SystemType;
import ru.m210projects.Build.Audio.Source;
import ru.m210projects.Build.Audio.SourceCallback;
import ru.m210projects.Build.FileHandle.FileEntry;
import ru.m210projects.Build.FileHandle.Resource;
import ru.m210projects.Build.FileHandle.Compat.Path;
import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Build.Script.CueScript;
import ru.m210projects.Redneck.Types.Sample;
import ru.m210projects.Redneck.Types.SoundOwner;
import ru.m210projects.Redneck.Types.VOC;

public class Sounds {

	public static final int LOUDESTVOLUME = 150;
	public static final int NUM_SOUNDS = 500;
	public static int backflag, numenvsnds;

	public static FileEntry userMusic;
	public static int currTrack = -1;
	public static MusicSource currMusic;
	public static String currSong;

	public static String[] cdtracks;
	public static void searchCDtracks()
	{
		for (Iterator<FileEntry> it = BuildGdx.compat.getDirectory(Path.Game).getFiles().values().iterator(); it.hasNext();) {
			FileEntry file = it.next();
			if(file.getExtension().equals("cue")) {
				byte[] data = BuildGdx.compat.getBytes(file);
				if(data != null) {
					CueScript cdTracks = new CueScript(file.getName(), data);
					cdtracks = cdTracks.getTracks();

					int numtracks = cdtracks.length;
					for(int i = 0; i < cdtracks.length; i++) {
						if(!BuildGdx.cache.contains(cdtracks[i], 0)) {
							cdtracks[i] = null;
							numtracks--;
						}
					}

					if(numtracks > 0) {
						Console.Println(numtracks + " cd tracks found...");
						return;
					}
				}
			}
		}

		List<FileEntry> tracks = new ArrayList<FileEntry>();
		for (Iterator<FileEntry> it = BuildGdx.compat.getDirectory(Path.Game).getFiles().values().iterator(); it.hasNext();) {
			FileEntry file = it.next();
			if(file.getExtension().equals("ogg"))
				tracks.add(file);
		}

		if(tracks.size() != 0)
		{
			int numtracks = tracks.size();
			cdtracks = new String[numtracks];
			for(int i = 0; i < numtracks; i++) {
				cdtracks[i] = tracks.get(i).getPath();
			}
			Console.Println(numtracks + " cd tracks found...");
			return;
		}

		cdtracks = new String[0];
		Console.Println("Cd tracks not found.");
	}

	private static SourceCallback<Integer> callback = new SourceCallback<Integer>() {
		@Override
		public void run(Integer num) {
			if (num < 0) {
				return;
			}

			int tempk = Sound[num].num;

			if (tempk > 0) {
				if ((currentGame.getCON().soundm[num] & 16) == 0)
					for (int tempj = 0; tempj < tempk; tempj++) {
						int tempi = SoundOwner[num][tempj].i;
						if (sprite[tempi].picnum == MUSICANDSFX && isValidSector(sprite[tempi].sectnum)
								&& sector[sprite[tempi].sectnum].lotag < 3 && sprite[tempi].lotag < 999) {
							hittype[tempi].temp_data[0] = 0;
							if ((tempj + 1) < tempk) {
								SoundOwner[num][tempj].voice = SoundOwner[num][tempk - 1].voice;
								SoundOwner[num][tempj].i = SoundOwner[num][tempk - 1].i;
							}
							break;
						}
					}

				Sound[num].num--;
				SoundOwner[num][tempk - 1].i = -1;
			}

			Sound[num].lock--;
		}
	};

	public static void check_fta_sounds(int i) {
		if (sprite[i].extra > 0)
			switch (sprite[i].picnum) {
			case BILLYCOCK:
			case BILLYRAY:
			case 4249:
				spritesound(121, i);
				break;
			case COOT:
				if (currentGame.getCON().type != RRRA || (engine.krand() & 3) == 2)
					spritesound(111, i);
				break;
			}
	}

	public static boolean midRestart() {
		sndStopMusic();

		if (!BuildGdx.audio.getMusic().init()) {
			Console.Println(BuildGdx.audio.getName(Driver.Music) + " initialization failed", OSDTEXT_RED);
			return false;
		}

		return true;
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

	public static void sndStopMusic() {
		if (currMusic != null)
			currMusic.stop();

		currMusic = null;
		currTrack++;
		if (currTrack >= cdtracks.length)
			currTrack = 0;
		currSong = null;
	}

	public static FileEntry sndCheckMusic(FileEntry map) { //usermap music
		if (map != null) {
			String musName = map.getName().substring(0, map.getName().indexOf(map.getExtension()) - 1) + ".ogg";
			userMusic = map.getParent().checkFile(musName);
		}

		return userMusic;
	}

	public static void sndPlayMusic(String name) {
		if (!cfg.muteMusic)
			BuildGdx.audio.setVolume(Driver.Music, cfg.musicVolume);
		else
			BuildGdx.audio.setVolume(Driver.Music, 0);

		if (userMusic != null) {
			if (currMusic != null && currMusic.isPlaying() && currSong == userMusic.getPath())
				return;

			sndStopMusic();
			if ((currMusic = BuildGdx.audio.newMusic(MusicType.Digital, userMusic.getPath())) != null) {
				currSong = userMusic.getPath();
				currMusic.play(true);
				return;
			}
		}

		if(cfg.gShuffleMusic)
			currTrack = (int) (Math.random() * (cdtracks.length - 1));
		sndPlayTrack(currTrack);

	}

	public static void checkTrack() {
		if(cfg.muteMusic) return;

		if (currMusic != null && !currMusic.isPlaying()) {
			if(cfg.gShuffleMusic)
				currTrack = (int) (Math.random() * (cdtracks.length - 1));
			else currTrack++;

			if (currTrack < 0 || currTrack >= cdtracks.length)
				currTrack = 0;

			System.err.println("Change music to " + currTrack);
			sndPlayTrack(currTrack);
		} else if (currMusic == null) {
			int i = 0;
			if(cfg.gShuffleMusic)
				i = (int) (Math.random() * (cdtracks.length - 1));

			for (; i < cdtracks.length; i++)
				if (sndPlayTrack(i)) {
					System.err.println("Start music " + i);
					return;
				}
			Console.Println("Music tracks not found!");
			cfg.muteMusic = true;
		}
	}

	public static boolean sndPlayTrack(int nTrack) {
		if (currMusic != null && currMusic.isPlaying() && currTrack == nTrack)
			return true;

		sndStopMusic();
		if (nTrack >= 0 && nTrack < cdtracks.length && BuildGdx.cache.contains(cdtracks[nTrack], 0)
				&& (currMusic = BuildGdx.audio.newMusic(MusicType.Digital, cdtracks[nTrack])) != null) {

			System.err.println("Play track " + nTrack);
			currTrack = nTrack;
			currMusic.play(false);
			return true;
		}

		return false;
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

		return true;
	}

	public static void SoundStartup() {
		for (int i = 0; i < NUM_SOUNDS; i++) {
			Sound[i] = new Sample();
			for (int j = 0; j < 4; j++)
				SoundOwner[i][j] = new SoundOwner();
		}

		if (BuildGdx.audio.getSound().init(SystemType.Stereo, cfg.maxvoices, cfg.resampler_num)) {
			BuildGdx.audio.setVolume(Driver.Sound, cfg.soundVolume);
		} else {
			Console.Println(BuildGdx.audio.getName(Driver.Sound) + " initialization failed", OSDTEXT_RED);
		}
	}

	public static void MusicStartup() {
		if (!BuildGdx.audio.getMusic().init())
			Console.Println(BuildGdx.audio.getName(Driver.Music) + " initialization failed", OSDTEXT_RED);

		if (!cfg.muteMusic)
			BuildGdx.audio.setVolume(Driver.Music, cfg.musicVolume);
		else
			BuildGdx.audio.setVolume(Driver.Music, 0);
	}

	public static void MusicUpdate() {
		currMusic.update();
	}

	public static int loadsound(int num) {

		if (num < 0 || num >= NUM_SOUNDS || cfg.noSound)
			return 0;
		if (!BuildGdx.audio.IsInited(Driver.Sound))
			return 0;

		Resource fp = null;
		if (currentGame.getCON().sounds[num] != null)
			fp = BuildGdx.cache.open(currentGame.getCON().sounds[num], loadfromgrouponly);
		if (fp == null) {

			Console.Println("Sound " + "(" + num + ") not found.");
			return 0;
		}

		soundsiz[num] = fp.size();
		Sound[num].lock = 2;

		byte[] tmp = fp.getBytes();
		loadSample(tmp, num);

		fp.close();
		return 1;
	}

	public static Source xyzsound(int num, int i, int x, int y, int z) {
		Source voice;
		int pitch;

		if (num < 0 || num >= NUM_SOUNDS || !BuildGdx.audio.IsInited(Driver.Sound)
				|| ((currentGame.getCON().soundm[num] & 8) != 0 && ud.lockout != 0) || cfg.noSound || Sound[num].num > 3
				|| !BuildGdx.audio.getSound().isAvailable(currentGame.getCON().soundpr[num])
				|| (ps[myconnectindex].timebeforeexit > 0 && ps[myconnectindex].timebeforeexit <= 26 * 3)
				|| game.menu.gShowMenu)
			return null;

		if ((currentGame.getCON().soundm[num] & 128) != 0) {
			sound(num);
			return null;
		}

		if ((currentGame.getCON().soundm[num] & 4) != 0) {
			if (!cfg.VoiceToggle || (ud.multimode > 1 && sprite[i].picnum == APLAYER && sprite[i].yvel != screenpeek
					&& ud.coop != 1))
				return null;

			for (int j = 0; j < NUM_SOUNDS; j++)
				for (int k = 0; k < Sound[j].num; k++)
					if ((Sound[j].num > 0) && (currentGame.getCON().soundm[j] & 4) != 0)
						return null;
		}

		int cx = ps[screenpeek].oposx;
		int cy = ps[screenpeek].oposy;
		int cz = ps[screenpeek].oposz;
		short cs = ps[screenpeek].cursectnum;

		int sndist = FindDistance3D((cx - x), (cy - y), (cz - z) >> 4);

		if (i >= 0 && isValidSector(sprite[i].sectnum) && (currentGame.getCON().soundm[num] & 16) == 0 && sprite[i].picnum == MUSICANDSFX
				&& sprite[i].lotag < 999 && sector[sprite[i].sectnum].lotag < 9)
			sndist = divscale(sndist, (sprite[i].hitag + 1), 14);

		int pitchs = currentGame.getCON().soundps[num];
		int pitche = currentGame.getCON().soundpe[num];
		cx = klabs(pitche - pitchs);

		if (cx != 0) {
			if (pitchs < pitche)
				pitch = pitchs + (engine.rand() % cx);
			else
				pitch = pitche + (engine.rand() % cx);
		} else
			pitch = pitchs;

		sndist += currentGame.getCON().soundvo[num];
		if (sndist < 0)
			sndist = 0;
		if (sndist != 0 && sprite[i].picnum != MUSICANDSFX && !engine.cansee(cx, cy, cz - (24 << 8), cs, sprite[i].x,
				sprite[i].y, sprite[i].z - (24 << 8), sprite[i].sectnum))
			sndist += sndist >> 2;

		switch (num) {
		case PIPEBOMB_EXPLODE:
		case LASERTRIP_EXPLODE:
		case RPG_EXPLODE:
			if (sndist > (6144))
				sndist = 6144;
			if (isValidSector(cs) && sector[cs].lotag == 2)
				pitch -= 1024;
			break;
		default:
			if (isValidSector(cs) && sector[cs].lotag == 2 && (currentGame.getCON().soundm[num] & 4) == 0)
				pitch = -768;
			if (sndist > 31444 && sprite[i].picnum != MUSICANDSFX)
				return null;
			break;
		}

		if (Sound[num].num > 0 && sprite[i].picnum != MUSICANDSFX) {
			if (SoundOwner[num][0].i == i)
				stopsound(num);
			else if (Sound[num].num > 1)
				stopsound(num);
			else if (badguy(sprite[i]) && sprite[i].extra <= 0)
				stopsound(num);
		}

		if (sprite[i].picnum == APLAYER && sprite[i].yvel == screenpeek) {
			sndist = 0;
		}

		if (Sound[num].ptr == null) {
			if (loadsound(num) == 0)
				return null;
		} else {
			if (Sound[num].lock < 200)
				Sound[num].lock = 200;
			else
				Sound[num].lock++;

			Sound[num].ptr.rewind();
		}

		if ((currentGame.getCON().soundm[num] & 16) != 0)
			sndist = 0;

		if (sndist < ((255 - LOUDESTVOLUME) << 6))
			sndist = ((255 - LOUDESTVOLUME) << 6);

		if ((currentGame.getCON().soundm[num] & 1) != 0) {
			if (Sound[num].num > 0)
				return null;
			voice = BuildGdx.audio.newSound(Sound[num].ptr, mulscale(Sound[num].rate, PITCH_GetScale(pitch), 16),
					Sound[num].bits, currentGame.getCON().soundpr[num]);
			if (voice != null)
				voice.setLooping(true, 0, -1);
		} else
			voice = BuildGdx.audio.newSound(Sound[num].ptr, mulscale(Sound[num].rate, PITCH_GetScale(pitch), 16),
					Sound[num].bits, currentGame.getCON().soundpr[num]);

		if (voice != null) {
			SoundOwner[num][Sound[num].num].i = i;
			SoundOwner[num][Sound[num].num].voice = voice;
			voice.setCallback(callback, num);
			voice.setPosition(x, z >> 4, y);
			voice.play(calcVolume(sndist));
			Sound[num].num++;
		} else
			Sound[num].lock--;
		return (voice);
	}

	public static Source sound(int num) {
		Source voice;
		if (!BuildGdx.audio.IsInited(Driver.Sound))
			return null;
		if (cfg.noSound)
			return null;

		if(num < 0 || num >= NUM_SOUNDS)
			return null;

		if (!cfg.VoiceToggle && (currentGame.getCON().soundm[num] & 4) != 0)
			return null;
		if ((currentGame.getCON().soundm[num] & 8) != 0 && ud.lockout != 0)
			return null;
		if (!BuildGdx.audio.getSound().isAvailable(currentGame.getCON().soundpr[num]))
			return null;

		int pitch;
		int pitchs = currentGame.getCON().soundps[num];
		int pitche = currentGame.getCON().soundpe[num];
		int cx = klabs(pitche - pitchs);

		if (cx != 0) {
			if (pitchs < pitche)
				pitch = pitchs + (engine.rand() % cx);
			else
				pitch = pitche + (engine.rand() % cx);
		} else
			pitch = pitchs;

		if (Sound[num].ptr == null) {
			if (loadsound(num) == 0)
				return null;
		} else {
			if (Sound[num].lock < 200)
				Sound[num].lock = 200;
			else
				Sound[num].lock++;

			Sound[num].ptr.rewind();
		}

		if ((currentGame.getCON().soundm[num] & 1) != 0) {
			voice = BuildGdx.audio.newSound(Sound[num].ptr, mulscale(Sound[num].rate, PITCH_GetScale(pitch), 16),
					Sound[num].bits, currentGame.getCON().soundpr[num]);
			if (voice != null) {
				voice.setLooping(true, 0, -1);
				voice.setCallback(callback, num);
				voice.setGlobal(1);
				voice.play(calcVolume(LOUDESTVOLUME));
				return voice;
			}

		} else {
			voice = BuildGdx.audio.newSound(Sound[num].ptr, mulscale(Sound[num].rate, PITCH_GetScale(pitch), 16),
					Sound[num].bits, currentGame.getCON().soundpr[num]);
			if (voice != null) {
				voice.setGlobal(1);
				voice.setCallback(callback, num);
				voice.play(calcVolume(255 - LOUDESTVOLUME));
				return voice;
			}
		}
		Sound[num].lock--;

		return null;
	}

	public static void loadSample(byte[] data, int num) {
		if (data[0] == 'C') {
			VOC voc = new VOC(data);
			Sound[num].bits = voc.samplesize;
			Sound[num].rate = voc.samplerate;
			Sound[num].ptr = voc.sampledata;
		} else {
			SoundData snd = BuildGdx.audio.decodeSound(data);
			if (snd != null) {
				Sound[num].bits = snd.bits;
				Sound[num].rate = snd.rate;
				Sound[num].ptr = snd.data;
				return;
			} else
				Console.Println("Can't load sound[" + num + "]", OSDTEXT_RED);

			Sound[num].ptr = ByteBuffer.allocateDirect(0); // to avoid of load cycle
			Sound[num].rate = 0;
			Sound[num].bits = 8;
		}
	}

	public static Source spritesound(int num, int i) {
		if (num < 0 || num >= NUM_SOUNDS)
			return null;
		return xyzsound(num, i, sprite[i].x, sprite[i].y, sprite[i].z);
	}

	public static void stopsound(int num, int i) {
		if(num < 0 || num >= NUM_SOUNDS)
			return;

		if (Sound[num].num > 0 && (i == -1 || i == SoundOwner[num][Sound[num].num - 1].i)) {
			SoundOwner[num][Sound[num].num - 1].voice.dispose();
		}
	}

	public static void stopsound(int num) {
		if(num < 0 || num >= NUM_SOUNDS)
			return;

		if (Sound[num].num > 0) {
			SoundOwner[num][Sound[num].num - 1].voice.dispose();
		}
	}

	public static void StopAllSounds() {
		for (int i = 0; i < NUM_SOUNDS; i++)
			stopsound(i);
		BuildGdx.audio.getSound().stopAllSounds();
	}

	public static void stopenvsound(int num, int i) {
		if(num < 0 || num >= NUM_SOUNDS)
			return;

		if (Sound[num].num > 0) {
			int k = Sound[num].num;
			for (int j = 0; j < k; j++)
				if (SoundOwner[num][j].i == i) {
					SoundOwner[num][j].voice.dispose();
					break;
				}
		}
	}

	public static void pan3dsound() {
		int sndist, sx, sy, sz, cx, cy, cz;
		short cs, ca;

		numenvsnds = 0;

		if (ud.camerasprite == -1) {
			cx = ps[screenpeek].oposx;
			cy = ps[screenpeek].oposy;
			cz = ps[screenpeek].oposz;
			cs = ps[screenpeek].cursectnum;
			ca = (short) (ps[screenpeek].ang + ps[screenpeek].look_ang);
		} else {
			cx = sprite[ud.camerasprite].x;
			cy = sprite[ud.camerasprite].y;
			cz = sprite[ud.camerasprite].z;
			cs = sprite[ud.camerasprite].sectnum;
			ca = sprite[ud.camerasprite].ang;
		}

		BuildGdx.audio.getSound().setListener(cx, cz >> 4, cy, ca);

		for (int j = 0; j < NUM_SOUNDS; j++)
			for (int k = 0; k < Sound[j].num; k++) {
				int i = SoundOwner[j][k].i;

				sx = sprite[i].x;
				sy = sprite[i].y;
				sz = sprite[i].z;

				if (sprite[i].picnum == APLAYER && sprite[i].yvel == screenpeek) {
					sndist = 0;
				} else {
					sndist = FindDistance3D((cx - sx), (cy - sy), (cz - sz) >> 4);
					if (sprite[i].sectnum >= 0 && sprite[i].sectnum < MAXSECTORS) { // 0.751
						if (i >= 0 && (currentGame.getCON().soundm[j] & 16) == 0 && sprite[i].picnum == MUSICANDSFX
								&& sprite[i].lotag < 999 && (sector[sprite[i].sectnum].lotag & 0xff) < 9)
							sndist = divscale(sndist, (sprite[i].hitag + 1), 14);
					}
				}

				sndist += currentGame.getCON().soundvo[j];
				if (sndist < 0)
					sndist = 0;

				if (sndist != 0 && sprite[i].picnum != MUSICANDSFX
						&& !engine.cansee(cx, cy, cz - (24 << 8), cs, sx, sy, sz - (24 << 8), sprite[i].sectnum))
					sndist += sndist >> 5;

				if (sprite[i].picnum == MUSICANDSFX && sprite[i].lotag < 999)
					numenvsnds++;

				switch (j) {
				case PIPEBOMB_EXPLODE:
				case LASERTRIP_EXPLODE:
				case RPG_EXPLODE:
					if (sndist > (6144))
						sndist = (6144);
					break;
				default:
					if (sndist > 31444 && sprite[i].picnum != MUSICANDSFX) {
						stopsound(j);
						continue;
					}
				}

				if (Sound[j].ptr == null && loadsound(j) == 0)
					continue;
				if ((currentGame.getCON().soundm[j] & 16) != 0)
					sndist = 0;

				if (sndist < ((255 - LOUDESTVOLUME) << 6))
					sndist = ((255 - LOUDESTVOLUME) << 6);

				SoundOwner[j][k].voice.setPosition(sx, sz >> 4, sy);
				SoundOwner[j][k].voice.setVolume(calcVolume(sndist));
			}
	}

	private static float calcVolume(int dist) {
		float vol = (dist >> 6) / 255.0f;
		vol = Math.min(Math.max(vol, 0.0f), 1.0f);
		return 1.0f - vol;
	}

	public static void clearsoundlocks() {
		for (int i = 0; i < NUM_SOUNDS; i++)
			if (Sound[i].lock >= 200)
				Sound[i].lock = 199;

	}

	private static final int PitchTable[][] = {
			{ 0x10000, 0x10097, 0x1012f, 0x101c7, 0x10260, 0x102f9, 0x10392, 0x1042c,
				0x104c6, 0x10561, 0x105fb, 0x10696, 0x10732, 0x107ce, 0x1086a, 0x10907,
				0x109a4, 0x10a41, 0x10adf, 0x10b7d, 0x10c1b, 0x10cba, 0x10d59, 0x10df8,
				0x10e98 },
			{ 0x10f38, 0x10fd9, 0x1107a, 0x1111b, 0x111bd, 0x1125f, 0x11302, 0x113a5,
				0x11448, 0x114eb, 0x1158f, 0x11634, 0x116d8, 0x1177e, 0x11823, 0x118c9,
				0x1196f, 0x11a16, 0x11abd, 0x11b64, 0x11c0c, 0x11cb4, 0x11d5d, 0x11e06,
				0x11eaf },
			{ 0x11f59, 0x12003, 0x120ae, 0x12159, 0x12204, 0x122b0, 0x1235c, 0x12409,
				0x124b6, 0x12563, 0x12611, 0x126bf, 0x1276d, 0x1281c, 0x128cc, 0x1297b,
				0x12a2b, 0x12adc, 0x12b8d, 0x12c3e, 0x12cf0, 0x12da2, 0x12e55, 0x12f08,
				0x12fbc },
			{ 0x1306f, 0x13124, 0x131d8, 0x1328d, 0x13343, 0x133f9, 0x134af, 0x13566,
				0x1361d, 0x136d5, 0x1378d, 0x13846, 0x138fe, 0x139b8, 0x13a72, 0x13b2c,
				0x13be6, 0x13ca1, 0x13d5d, 0x13e19, 0x13ed5, 0x13f92, 0x1404f, 0x1410d,
				0x141cb },
			{ 0x1428a, 0x14349, 0x14408, 0x144c8, 0x14588, 0x14649, 0x1470a, 0x147cc,
				0x1488e, 0x14951, 0x14a14, 0x14ad7, 0x14b9b, 0x14c5f, 0x14d24, 0x14dea,
				0x14eaf, 0x14f75, 0x1503c, 0x15103, 0x151cb, 0x15293, 0x1535b, 0x15424,
				0x154ee },
			{ 0x155b8, 0x15682, 0x1574d, 0x15818, 0x158e4, 0x159b0, 0x15a7d, 0x15b4a,
				0x15c18, 0x15ce6, 0x15db4, 0x15e83, 0x15f53, 0x16023, 0x160f4, 0x161c5,
				0x16296, 0x16368, 0x1643a, 0x1650d, 0x165e1, 0x166b5, 0x16789, 0x1685e,
				0x16934 },
			{ 0x16a09, 0x16ae0, 0x16bb7, 0x16c8e, 0x16d66, 0x16e3e, 0x16f17, 0x16ff1,
				0x170ca, 0x171a5, 0x17280, 0x1735b, 0x17437, 0x17513, 0x175f0, 0x176ce,
				0x177ac, 0x1788a, 0x17969, 0x17a49, 0x17b29, 0x17c09, 0x17cea, 0x17dcc,
				0x17eae },
			{ 0x17f91, 0x18074, 0x18157, 0x1823c, 0x18320, 0x18406, 0x184eb, 0x185d2,
				0x186b8, 0x187a0, 0x18888, 0x18970, 0x18a59, 0x18b43, 0x18c2d, 0x18d17,
				0x18e02, 0x18eee, 0x18fda, 0x190c7, 0x191b5, 0x192a2, 0x19391, 0x19480,
				0x1956f },
			{ 0x1965f, 0x19750, 0x19841, 0x19933, 0x19a25, 0x19b18, 0x19c0c, 0x19d00,
				0x19df4, 0x19ee9, 0x19fdf, 0x1a0d5, 0x1a1cc, 0x1a2c4, 0x1a3bc, 0x1a4b4,
				0x1a5ad, 0x1a6a7, 0x1a7a1, 0x1a89c, 0x1a998, 0x1aa94, 0x1ab90, 0x1ac8d,
				0x1ad8b },
			{ 0x1ae89, 0x1af88, 0x1b088, 0x1b188, 0x1b289, 0x1b38a, 0x1b48c, 0x1b58f,
				0x1b692, 0x1b795, 0x1b89a, 0x1b99f, 0x1baa4, 0x1bbaa, 0x1bcb1, 0x1bdb8,
				0x1bec0, 0x1bfc9, 0x1c0d2, 0x1c1dc, 0x1c2e6, 0x1c3f1, 0x1c4fd, 0x1c609,
				0x1c716 },
			{ 0x1c823, 0x1c931, 0x1ca40, 0x1cb50, 0x1cc60, 0x1cd70, 0x1ce81, 0x1cf93,
				0x1d0a6, 0x1d1b9, 0x1d2cd, 0x1d3e1, 0x1d4f6, 0x1d60c, 0x1d722, 0x1d839,
				0x1d951, 0x1da69, 0x1db82, 0x1dc9c, 0x1ddb6, 0x1ded1, 0x1dfec, 0x1e109,
				0x1e225 },
			{ 0x1e343, 0x1e461, 0x1e580, 0x1e6a0, 0x1e7c0, 0x1e8e0, 0x1ea02, 0x1eb24,
				0x1ec47, 0x1ed6b, 0x1ee8f, 0x1efb4, 0x1f0d9, 0x1f1ff, 0x1f326, 0x1f44e,
				0x1f576, 0x1f69f, 0x1f7c9, 0x1f8f3, 0x1fa1e, 0x1fb4a, 0x1fc76, 0x1fda3,
				0x1fed1 }
		};

	private static final int MAXDETUNE = 25;

	private static int PITCH_GetScale(int pitchoffset) {
		int scale;
		int octaveshift;
		int noteshift;
		int note;
		int detune;

		if (pitchoffset == 0)
			return (PitchTable[0][0]);

		noteshift = pitchoffset % 1200;
		if (noteshift < 0)
			noteshift += 1200;

		note = noteshift / 100;
		detune = (noteshift % 100) / (100 / MAXDETUNE);
		octaveshift = (pitchoffset - noteshift) / 1200;

		if (detune < 0) {
			detune += (100 / MAXDETUNE);
			note--;
			if (note < 0) {
				note += 12;
				octaveshift--;
			}
		}

		scale = PitchTable[note][detune];

		if (octaveshift < 0)
			scale >>= -octaveshift;
		else
			scale <<= octaveshift;

		return scale;
	}
}
