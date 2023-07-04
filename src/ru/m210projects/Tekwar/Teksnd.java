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

//	public static Soundbuffertype[] sbufptr = new Soundbuffertype[TOTALSOUNDS];
//	public static SOS_START_SAMPLE[] sampleptr = new SOS_START_SAMPLE[MAXSOUNDS];
//	public static final int   NULL_HANDLE  =  -1;
//	public static final int   _ERR_NO_ERROR = 0;
//	public static final int   SM_NOHARDWARE  =     0;
//	public static final int   MM_NOHARDWARE  =     0;

//	public static final int  _LOOsPING         = 0x4000;
//	public static final int  _PANNING         = 0x0200;
//	public static final int  _VOLUME          = 0x0100;

//	public static final int   _CENTER_CHANNEL = 2;

//	public static final int PanArray[] = {
//		//REAR to HARD LEFT (angle = 0 - 512)   
//		0x8000,0x7000,0x6000,0x5000,0x4000,0x3000,0x2000,0x1000,0x0000,
//		//HARD LEFT to CENTER (angle = 513-1024)
//		0x1000,0x20f0,0x2000,0x3000,0x4000,0x5000,0x6000,0x7000,0x8000, 
//		//CENTER to HARD RIGHT (angle = 1025-1536)
//		0x70f0,0x8000,0x9000,0xa000,0xb000,0xc000,0xd000,0xe000,0xf000,   
//		//HARD RIGHT to REAR (angle = 1537-2047)
//		0xffff,0xf000,0xe000,0xd000,0xc000,0xb000,0xa000,0x9000,0x8000
//	};

//	public static void sosDIGISetSampleVolume(Soundtype dsoundptr, long vol) {
//		if(dsoundptr == null) {
//			System.err.println("Volume sound error: Soundtype " + dsoundptr);
//			return;
//		}
//		dsoundptr.handle.sound.setVolume((vol*soundMasterVolume)/32768f);
//	}

//	public static void sosDIGISetPanLocation(Soundtype dsoundptr, long pan, long vol) {
//		if(dsoundptr == null) {
//			System.err.println("Pan sound error: Soundtype " + dsoundptr);
//			return;
//		}
//		dsoundptr.handle.sound.setPan(pan, (vol*soundMasterVolume)/32768f);
//	}
//	
//	public static void sosDIGIStopSample(SOS_START_SAMPLE sampleptr) {
//		if(sampleptr == null) {
//			System.err.println("Stop sound error: SOS_START_SAMPLE " + sampleptr);
//			return;
//		}
//			
//		if(sampleptr != null && sampleptr.sound == null) {
//			System.err.println("Stop sound error: SOS_START_SAMPLE " + sampleptr + " sound: " + sampleptr.sound);
//			return;
//		}
//		
//		sampleptr.sound.stop();
//	}
//	
//	public static SOS_START_SAMPLE sosDIGIStartSample(SOS_START_SAMPLE sampleptr) {
//		if(sampleptr == null || sampleptr.sound == null) {
//			System.err.println("Play sound error: SOS_START_SAMPLE " + sampleptr + " sound: " + sampleptr.sound);
//			return null;
//		}
//
//		sampleptr.sound.setVolume((sampleptr.wVolume*soundMasterVolume) / 32768f);
//		sampleptr.sound.play();
//		return sampleptr;
//	}

//	public static void buildWavHeader(ByteBuffer header, int channels, int sampleRate, int bitsPerSample, int dataSize) {
//		byte[] chunkId = new byte[]{'R', 'I', 'F', 'F'};
//		int chunkSize = (dataSize+40);
//		byte[] format = new byte[]{'W', 'A', 'V', 'E'};
//		byte[] subchunk1Id = new byte[]{'f', 'm', 't', ' '};
//		int subchunk1Size = 16;
//		short audioFormat = 1; //PCM
//		short numChannels = (short) channels;
//		int byteRate = sampleRate * numChannels * bitsPerSample/8;
//		short blockAlign = (short) (numChannels * bitsPerSample/8);
//		byte[] subchunk2Id = new byte[]{'d', 'a', 't', 'a'};
//
//		header.put(chunkId); // 00 - RIFF
//		header.putInt(chunkSize); // 04 - how big is the rest of this file?
//		header.put(format); // 08 - WAVE
//		header.put(subchunk1Id); // 12 - fmt
//		header.putInt(subchunk1Size); // 16 - size of this chunk
//		header.putShort(audioFormat); // 20 - what is the audio format? 1 for PCM = Pulse Code Modulation
//		header.putShort(numChannels); // 22 - mono or stereo? 1 or 2?  (or 5 or ???)
//		header.putInt(sampleRate); // 24 - samples per second (numbers per second)
//		header.putInt(byteRate); // 28 - bytes per second
//		header.putShort(blockAlign); // 32 - # of bytes in one sample, for all channels
//		header.putShort((short) bitsPerSample); // 34 - how many bits in a sample(number)?  usually 16 or 24
//		header.put(subchunk2Id);
//		header.putInt(dataSize); // 40 - how big is this data chunk
//	}

//	public static final int   MAXLOOPS     =   2;	
//	static byte[] LoopList;
//	static Soundbuffertype[] loopbufptr = new Soundbuffertype[MAXLOOPS];	
//	static int digiloopflag = 0;
//	static Soundtype[] lsoundptr = new Soundtype[MAXLOOPS];
//	static SOS_START_SAMPLE[] loopsampleptr = new SOS_START_SAMPLE[MAXLOOPS];
//	long	 SeekIndex,SampleSize;
//	public static final int   MM_MIDIFM       =    1;
//	public static final int   MM_MIDIDIGI     =    2;
//	public static final int   MM_MIDIGEN      =    3;
//	public static final int   MM_MIDIAWE32    =    4;
//	public static final int  _ACTIVE          = 0x8000;
//	public static final int  _FIRST_TIME      = 0x2000;
//	public static final int  _PENDING_RELEASE = 0x1000;
//	public static final int  _CONTINUE_BLOCK  = 0x0800;
//	public static final int  _PITCH_SHIFT     = 0x0400;
//	public static final int  _TRANSLATE16TO8  = 0x0080;
//	public static final int  _STAGE_LOOP      = 0x0040;
//	public static final int  _TRANSLATE8TO16  = 0x0020;
//	public static final int  _STEREOTOMONO    = 0x0010;

//	public static final int  _SOS_MIDI_FADE_IN       =   0x01;  
//	public static final int  _SOS_MIDI_FADE_OUT      =   0x02;  
//	public static final int  _SOS_MIDI_FADE_OUT_STOP =   0x04; 

//	public static final int   _LEFT_CHANNEL = 0;
//	public static final int   _RIGHT_CHANNEL = 1;
//	public static final int   _INTERLEAVED = 3;

//	public static void initlooptable()
//	{
//		if(digiloopflag == 0) 
//			return;
//		
//		int hLoopFile = kOpen("LOOPS", 0);
//		if( hLoopFile == -1 ) {
//			game.ThrowError("initlooptable: cant open loops");
//		}
//		LoopList = new byte[4096]; 
//
//		klseek(hLoopFile,-4096,SEEK_END);
//		kRead(hLoopFile, LoopList, 4096);
//	}

//	public static void initaudioptrs()
//	{
//	     for( int i=0; i<MAXLOOPS; i++ ) {
//	          loopbufptr[i]=new Soundbuffertype();
//	          lsoundptr[i]=new Soundtype();
//	          lsoundptr[i].handle=null;
//	          lsoundptr[i].type=ST_UPDATE;
//	          lsoundptr[i].sndnum=-1;
//	          loopsampleptr[i]= new SOS_START_SAMPLE();
//	     }
//	}

//	public static byte[] testmenu(int insubway) {
//	int i,index = 0;
//	if(insubway != 0)
//	     index=(NUMLEVELS*(AVAILMODES*SONGSPERLEVEL)+3);
//	
//	for( i=0; i<SONGSPERLEVEL; i++ ) {
//          songptr[0].handle=NULL_HANDLE;
//          songptr[0].offset=songlist[index*3]*4096;
//          songptr[0].playing=0;
//          songptr[0].pending=0;
//          songptr[0].length=songlist[(index*3)+1];
//          if( songptr[0].length >= MAXBASESONGLENGTH ) {
//               game.ThrowError("prepsongs: basesong exceeded max length");
//          }
//     }
//
//	int sn = BASESONG;
//
//	klseek(fhsongs, songptr[sn].offset, SEEK_SET);
//
//    kRead(fhsongs, songptr[sn].buffer, songptr[sn].length);
//    
//    return songptr[sn].buffer; 
//}

//	public static void stopsong(int sn) {
//    if( songptr[sn].playing == 0 ) 
//         return;     
//    
//    if( songptr[sn].pending != 0 )     // cant stop a pending song
//         return;                       // since may be interrupted
//
//    sosMIDIStopSong(songptr[sn].handle);
//    songptr[sn].playing=0;
//}

//public static void removesong(int sn) {
//    if( songptr[sn].handle != NULL_HANDLE ) {
//         songptr[sn].pending=0;
//         sosMIDIStopSong(songptr[sn].handle);
//         sosMIDIUnInitSong(songptr[sn].handle);
//         songptr[sn].handle=NULL_HANDLE;
//         songptr[sn].playing=0;
//    }
//}

//	public static void musicoff()
//	{
//		for( int i=0; i<SONGSPERLEVEL; i++ ) {
//        	if( songptr[i].handle == NULL_HANDLE )
//            	continue;
//        	sosMIDIStopSong(songptr[i].handle);
//        	sosMIDIUnInitSong(songptr[i].handle); 
//		}
//	}
}