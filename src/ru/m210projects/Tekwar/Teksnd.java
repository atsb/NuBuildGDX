package ru.m210projects.Tekwar;

import static ru.m210projects.Build.Audio.HMIMIDIP.*;
import static ru.m210projects.Build.Gameutils.*;
import static ru.m210projects.Build.OnSceenDisplay.Console.OSDTEXT_RED;
import static ru.m210projects.Tekwar.Globals.*;
import static ru.m210projects.Tekwar.Main.*;
import static ru.m210projects.Tekwar.Tekprep.subwaysound;
import static ru.m210projects.Tekwar.Tektag.*;
import static ru.m210projects.Tekwar.Player.*;
import static java.lang.Math.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.badlogic.gdx.utils.BufferUtils;

import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.Audio.BuildAudio.Driver;
import ru.m210projects.Build.Audio.BuildAudio.MusicType;
import ru.m210projects.Build.Audio.Sound.SystemType;
import ru.m210projects.Build.FileHandle.FileEntry;
import ru.m210projects.Build.FileHandle.Resource;
import ru.m210projects.Build.FileHandle.Compat.Path;
import ru.m210projects.Build.FileHandle.Resource.Whence;
import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Build.Script.CueScript;
import ru.m210projects.Tekwar.Types.Songtype;
import ru.m210projects.Tekwar.Types.Soundtype;

public class Teksnd {

	public static final int MAXSOUNDS = 256;
	public static final int TOTALSOUNDS = 208;

	public static final int MAXBASESONGLENGTH = 44136;
	public static final int AVAILMODES = 3;
	public static final int SONGSPERLEVEL = 3;
	public static final int NUMLEVELS = 7;

	public static Resource fhsongs = null;
	public static Songtype songptr;
	public static int[] songlist = new int[4096];
	public static int totalsongsperlevel;

	public static Resource fhsounds = null;
	public static final int AMBUPDATEDIST = 4000;
	public static ByteBuffer[] pSfx = new ByteBuffer[TOTALSOUNDS];
	public static Soundtype[] dsoundptr = new Soundtype[MAXSOUNDS];
	public static int digilist[] = new int[1024];
	
	public static int currTrack = -1;
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
		
		Console.Println("Cd tracks not found.");
	}

	public static void sndInit() {
		if (tekcfg.maxvoices < 4)
			tekcfg.maxvoices = 4;

		if (BuildGdx.audio.getSound().init(SystemType.Stereo, tekcfg.maxvoices, tekcfg.resampler_num)) {
			BuildGdx.audio.setVolume(Driver.Sound, tekcfg.soundVolume);
		} else {
			Console.Println(BuildGdx.audio.getName(Driver.Sound) + " initialization failed", OSDTEXT_RED);
		}

		if (!BuildGdx.audio.getMusic().init())
			Console.Println(BuildGdx.audio.getName(Driver.Music) + " initialization failed", OSDTEXT_RED);

		if (fhsongs == null)
			setupmidi();
		if (fhsounds == null)
			setupdigi();
	}

	public static boolean midRestart() {
		sndStopMusic();

		if (!BuildGdx.audio.getMusic().init()) {
			Console.Println(BuildGdx.audio.getName(Driver.Music) + " initialization failed", OSDTEXT_RED);
			return false;
		}

		return true;
	}
	
	public static void sndHandlePause(boolean gPaused)
	{
		if(gPaused) {
			if (songptr.handle != null)
				songptr.handle.pause();
			BuildGdx.audio.getSound().stopAllSounds();
		} else {
			if (!tekcfg.muteMusic && songptr.handle != null)
				songptr.handle.resume();
		}
	}

	public static void sndStopMusic() {
		if (songptr.handle != null)
			songptr.handle.stop();
		songptr.buffer = null;
		currTrack = -1;
	}

	public static void setupmidi() {
		if ((fhsongs = BuildGdx.cache.open("SONGS", 0)) == null)
			game.ThrowError("setupmidi: cant open songs");

		fhsongs.seek(-4096, Whence.End);
		for (int i = 0; i < 1024; i++)
			songlist[i] = fhsongs.readInt();

		songptr = new Songtype();
		totalsongsperlevel = SONGSPERLEVEL * AVAILMODES;
	}

	public static void menusong(int insubway) {
		int index = NUMLEVELS * (AVAILMODES * SONGSPERLEVEL);
		if (insubway != 0)
			index = (NUMLEVELS * (AVAILMODES * SONGSPERLEVEL) + 3);
		
		index += 2; //MPU-401

		songptr.offset = songlist[index * 3] * 4096;
		songptr.length = songlist[(index * 3) + 1];
		if (songptr.length >= MAXBASESONGLENGTH)
			game.ThrowError("prepsongs: basesong exceeded max length");

		playsong();
	}

	public static boolean playCDtrack(int nTrack)
	{
		if (game.isCurrentScreen(gMissionScreen) || tekcfg.musicType == 0 || nTrack < 0)
			return false;
		
		nTrack = BClipRange(nTrack, 1, cdtracks.length);
		if (songptr.handle != null && songptr.handle.isPlaying() && currTrack == nTrack)
			return true;
		
		if(cdtracks != null && cdtracks[nTrack - 1] != null && (songptr.handle = BuildGdx.audio.newMusic(MusicType.Digital, cdtracks[nTrack - 1])) != null) {
			sndStopMusic();
			currTrack = nTrack;
			songptr.handle.play(true);
			return true;
		}
		
		return false;
	}

	public static void startmusic(int level) {
		if (level > 6)
			return;

		int index = totalsongsperlevel * (level);

		songptr.offset = songlist[(index * 3)] * 4096;
		songptr.length = songlist[(index * 3) + 1];
		if (songptr.length >= MAXBASESONGLENGTH)
			game.ThrowError("prepsongs: basesong exceeded max length");

		playsong();
	}

	public static int playsong() {
		if (!tekcfg.muteMusic)
			BuildGdx.audio.setVolume(Driver.Music, tekcfg.musicVolume);
		else
			BuildGdx.audio.setVolume(Driver.Music, 0);

		sndStopMusic();

		if (cdtracks != null && playCDtrack((int) (Math.random() * cdtracks.length)))
			return 1;

		if (songptr.length == 0)
			return 0;

		if (songptr.buffer == null) {
			fhsongs.seek(songptr.offset, Whence.Set);
			byte[] buffer = new byte[songptr.length];
			int rv = fhsongs.read(buffer);

			songptr.buffer = hmpinit(buffer);

			if (songptr.buffer == null || rv != songptr.length)
				game.ThrowError("playsong: bad read");
		}

		songptr.handle = BuildGdx.audio.newMusic(MusicType.Midi, songptr.buffer);
		if (songptr.handle != null) {
			songptr.handle.play(true);
			return 1;
		}

		return 0;
	}

	public static void setupdigi() {
		if ((fhsounds = BuildGdx.cache.open("sounds", 0)) == null)
			game.ThrowError("setupdigi: cant open sounds");

		fhsounds.seek(-4096, Whence.End);
		for (int i = 0; i < 1024; i++) {
			Integer var = fhsounds.readInt();
			if(var == null) {
				game.ThrowError("setupdigi: bad read of digilist");
				return;
			}
			digilist[i] = var;
		}

		for (int i = 0; i < MAXSOUNDS; i++) {
			dsoundptr[i] = new Soundtype();
			dsoundptr[i].type = ST_UPDATE;
			dsoundptr[i].sndnum = -1;
		}
	}

	public static void updatesounds(int snum) {
		if (tekcfg.noSound || !BuildGdx.audio.IsInited(Driver.Sound))
			return;

		BuildGdx.audio.getSound().setListener(gPlayer[snum].posx, gPlayer[snum].posz >> 4, gPlayer[snum].posy,
				(int) gPlayer[snum].ang);

		for (int i = 0; i < MAXSOUNDS; i++) {
			if (dsoundptr[i].hVoice == null)
				continue;

			if ((dsoundptr[i].type & (ST_IMMEDIATE | ST_NOUPDATE | ST_VEHUPDATE)) != 0)
				continue;

			if (dsoundptr[i].hVoice.isLooping()) {
				if (dsoundptr[i].loop > 0) {
					if (dsoundptr[i].loop >= BuildGdx.graphics.getDeltaTime())
						dsoundptr[i].loop -= BuildGdx.graphics.getDeltaTime();
					else
						dsoundptr[i].loop = 0;
				}

				if (dsoundptr[i].loop == 0) {
					dsoundptr[i].hVoice.setLooping(false, 0, 0);
					stopsound(i);
					return;
				}
			}

			int dist = abs(gPlayer[snum].posx - dsoundptr[i].x) + abs(gPlayer[snum].posy - dsoundptr[i].y);
			int vol = 39000 - (dist << 2);
			if (dsoundptr[i].type == ST_AMBUPDATE) {
				if (dist < AMBUPDATEDIST)
					vol = (AMBUPDATEDIST << 3) - (dist << 3);
				else
					vol = 0;
			} else {
				if (dist < 1500)
					vol = 0x7fff;
				else if (dist > 8500)
					vol = 0x1f00;
			}

			vol = BClipRange((int) ((vol / 32767f) * 255), 0, 255);
			dsoundptr[i].hVoice.setVolume(vol / 255.0f);
		}
	}

	public static int playsound(int sn, int sndx, int sndy, int loop, int type) {
		if (!BuildGdx.audio.IsInited(Driver.Sound) || tekcfg.noSound || (sn < 0) || (sn >= TOTALSOUNDS))
			return -1;

		for (int i = 0; i < MAXSOUNDS; i++) {
			if (dsoundptr[i].hVoice != null && !dsoundptr[i].hVoice.isActive()) {
				stopsound(i);
			}
		}

		if ((type & (ST_UNIQUE | ST_AMBUPDATE | ST_TOGGLE)) != 0) {
			for (int i = 0; i < MAXSOUNDS; i++) {
				if (dsoundptr[i].hVoice == null || (dsoundptr[i].hVoice != null && !dsoundptr[i].hVoice.isActive()))
					continue;
				else if (dsoundptr[i].sndnum == sn) {
					if ((type & ST_TOGGLE) != 0)
						stopsound(i);
					return -1;
				}
			}
		}

		int sound = 0;
		while (dsoundptr[sound].hVoice != null && dsoundptr[sound].hVoice.isActive()) {
			sound++;
			if (sound == MAXSOUNDS)
				return (-1);
		}

		dsoundptr[sound].type = (short) type;
		dsoundptr[sound].x = sndx;
		dsoundptr[sound].y = sndy;

		if (pSfx[sn] == null) { // no longer in cache
			int srclen = digilist[sn * 3 + 1];
			int srcoffset = digilist[sn * 3] * 4096;
			
			fhsounds.seek(srcoffset, Whence.Set);
			ByteBuffer data = BufferUtils.newByteBuffer(srclen);
			data.order(ByteOrder.LITTLE_ENDIAN);
			
			byte[] buf = new byte[srclen];
			fhsounds.read(buf);
			data.put(buf);
			pSfx[sn] = data;
		}
		pSfx[sn].rewind();

		int vol = 0x7fff;
		if ((type & ST_IMMEDIATE) == 0) {
			int dist = abs(gPlayer[screenpeek].posx - sndx) + abs(gPlayer[screenpeek].posy - sndy);
			if ((type & ST_AMBUPDATE) != 0 || (type & ST_VEHUPDATE) != 0) {
				if (dist < AMBUPDATEDIST)
					vol = (AMBUPDATEDIST << 3) - (dist << 3);
				else
					vol = 0;
			} else if ((type & ST_IMMEDIATE) == 0) {
				vol = 39000 - (dist << 2);

				if (dist < 1500)
					vol = 0x7fff;
				else if (dist > 8500) {
					if (sn >= 151) // S_MALE_COMEONYOU
						vol = 0x0000;
					else
						vol = 0x1f00;
				}
			}
		}

		vol = BClipRange((int) ((vol / 32767f) * 255), 1, 255);
		dsoundptr[sound].loop = loop;
		if (loop != 0 || (type & ST_AMBUPDATE) != 0 || (type & ST_VEHUPDATE) != 0) {
			if ((dsoundptr[sound].hVoice = BuildGdx.audio.newSound(pSfx[sn], 11025, 8, Integer.MAX_VALUE)) != null)
				dsoundptr[sound].hVoice.setLooping(true, 0, -1);
		} else {
			dsoundptr[sound].hVoice = BuildGdx.audio.newSound(pSfx[sn], 11025, 8, 80 * (vol + 1));
		}

		if (dsoundptr[sound].hVoice != null) {
			dsoundptr[sound].hVoice.setPosition(sndx, 0, sndy);
			if ((type & ST_IMMEDIATE) != 0)
				dsoundptr[sound].hVoice.setGlobal(1);
			dsoundptr[sound].hVoice.play(vol / 255.0f);
		}
		dsoundptr[sound].sndnum = sn;
		return sound;
	}

	public static void stopallsounds() {
		if (!BuildGdx.audio.IsInited(Driver.Sound))
			return;

		for (int i = 0; i < MAXSOUNDS; i++)
			stopsound(i);
		
		BuildGdx.audio.getSound().stopAllSounds();

		// clear variables that track looping sounds
		loopinsound = -1;
		baydoorloop = -1;
		ambsubloop = -1;
	}

	public static void stopsound(int i) {
		if (dsoundptr[i].hVoice == null || !BuildGdx.audio.IsInited(Driver.Sound) || (i < 0) || (i >= TOTALSOUNDS))
			return;

		if (dsoundptr[i].loop != 0) {
			dsoundptr[i].hVoice.setLooping(false, 0, 0);
			dsoundptr[i].loop = 0;
		}
		dsoundptr[i].hVoice.stop();
		dsoundptr[i].hVoice = null;
		dsoundptr[i].type = ST_UPDATE;
		dsoundptr[i].sndnum = -1;
	}

	public static void updatevehiclesnds(int i, int sndx, int sndy) {
		if ((i < 0) || (i > TOTALSOUNDS))
			return;

		dsoundptr[i].x = sndx;
		dsoundptr[i].y = sndy;

		int dist = abs(gPlayer[screenpeek].posx - sndx) + abs(gPlayer[screenpeek].posy - sndy);
		int vol = 32767 - (dist << 2);
		if (dist < 1000)
			vol = 32767;
		else if (dist > 9000)
			vol = 0;

		vol = BClipRange((int) ((vol / 32767f) * 255), 0, 255);
		if (dsoundptr[i].hVoice != null) {
			dsoundptr[i].hVoice.setPosition(sndx, 0, sndy);
			dsoundptr[i].hVoice.setVolume(vol / 255.0f);
		}
	}

	public static boolean sndRestart(int nvoices, int resampler) {
		stopallsounds();
		BuildGdx.audio.getSound().uninit();
		tekcfg.maxvoices = nvoices;

		ambsubloop = -1;
		for (int i = 0; i < MAXSECTORVEHICLES; i++)
			sectorvehicle[i].soundindex = -1;
		for (int i = 0; i < subwaytrackcnt; i++)
			subwaysound[i] = -1;
		for (int i = 0; i < totalmapsndfx; i++)
			mapsndfx[i].id = -1;

		Console.Println("Sound restarting...");

		if (BuildGdx.audio.getSound().init(SystemType.Stereo, nvoices, resampler)) {
			BuildGdx.audio.setVolume(Driver.Sound, tekcfg.soundVolume);
		} else {
			Console.Println(BuildGdx.audio.getName(Driver.Sound) + " initialization failed", OSDTEXT_RED);
			return false;
		}

		return true;
	}

}