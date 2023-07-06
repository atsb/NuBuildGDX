// This file is part of PowerslaveGDX.
// Copyright (C) 2019  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// PowerslaveGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// PowerslaveGDX is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with PowerslaveGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.Powerslave;

import java.nio.ByteBuffer;
import java.util.Iterator;

import static ru.m210projects.Powerslave.Globals.*;
import static ru.m210projects.Powerslave.Seq.*;
import static ru.m210projects.Powerslave.Snake.SnakeList;
import static ru.m210projects.Powerslave.Map.*;
import static ru.m210projects.Powerslave.Random.*;
import static ru.m210projects.Build.OnSceenDisplay.Console.OSDTEXT_RED;
import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Build.Gameutils.*;

import static ru.m210projects.Powerslave.Main.*;

import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.Audio.MusicSource;
import ru.m210projects.Build.Audio.Source;
import ru.m210projects.Build.Audio.SourceCallback;
import ru.m210projects.Build.Audio.BuildAudio.Driver;
import ru.m210projects.Build.Audio.BuildAudio.MusicType;
import ru.m210projects.Build.Audio.Sound.SystemType;
import ru.m210projects.Build.FileHandle.DirectoryEntry;
import ru.m210projects.Build.FileHandle.FileEntry;
import ru.m210projects.Build.FileHandle.Compat.Path;
import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Build.Script.CueScript;
import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Build.Types.WALL;
import ru.m210projects.Powerslave.Main.UserFlag;
import ru.m210projects.Powerslave.Type.VOC;

class SoundResource {
	public ByteBuffer ptr;
	public int num;
	public int bits, rate;
	public boolean loop;

	public SoundResource(byte[] data, int num) {
		VOC voc = new VOC(data);
		this.bits = voc.samplesize;
		this.rate = voc.samplerate;
		this.ptr = voc.sampledata;
		this.num = num;
		this.loop = data[26] == 6;
	}
}

public class Sound {

	public static class ActiveSound {
		public Source pHandle;
		public int rate;
		public int spr;
		public int x, y, z;
		public int oldvolume, volume;
		public int panning;
		public int oldpitch, pitch;
		public int soundnum, asound_C, clock;
		public int ambient, ambchannel;
		public int sectnum;
	}

	private static SourceCallback<Integer> callback = new SourceCallback<Integer>() {
		@Override
		public void run(Integer ch) {
			if (sActiveSound[ch].pHandle != null)
				sActiveSound[ch].pHandle.dispose();
			sActiveSound[ch].pHandle = null;
			sActiveSound[ch].rate = 0;
		}
	};

	public static String[] SoundFiles = { "spl_big", "spl_smal", "bubble_l", "grn_drop", "p_click", "grn_roll",
			"cosprite", "m_chant0", "anu_icu", "item_reg", "item_spe", "item_key", "torch_on", "jon_bnst", "jon_gasp",
			"jon_land", "jon_gags", "jon_fall", "jon_drwn", "jon_air1", "jon_glp1", "jon_bbwl", "jon_pois", "amb_ston",
			"cat_icu", "bubble_h", "set_land", "jon_hlnd", "jon_laf2", "spi_jump", "jon_scub", "item_use", "tr_arrow",
			"swi_foot", "swi_ston", "swi_wtr1", "tr_fire", "m_skull5", "spi_atak", "anu_hit", "fishdies", "scrp_icu",
			"jon_wade", "amb_watr", "tele_1", "wasp_stg", "res", "drum4", "rex_icu", "m_hits_u", "q_tail", "vatr_mov",
			"jon_hit3", "jon_t_2", "jon_t_1", "jon_t_5", "jon_t_6", "jon_t_8", "jon_t_4", "rasprit1", "jon_fdie",
			"wijaf1", "ship_1", "saw_on", "ra_on", "amb_ston", "vatr_stp", "mana1", "mana2", "ammo", "pot_pc1",
			"pot_pc2", "weapon", "alarm", "tick1", "scrp_zap", "jon_t_3", "jon_laf1", "blasted", "jon_air2" };

	public static int nWaspSound = -1;
	public static int[] StaticSound = new int[80];
	public static SoundResource[] SoundBufs = new SoundResource[200];
	public static String szSoundName[] = new String[200];
	public static int nSoundCount;
	public static int soundx, soundy, soundz, soundsect;
	public static int nLocalSectFlags;
	public static int nAmbientChannel = -1;
	public static int nLocalChan;

	public static ActiveSound sActiveSound[];
	public static int nNextFreq;
	public static int nSwirlyFrames;

	public static int currTrack = -1;
	public static MusicSource currMusic = null;

	public static final String[] cdtracks = { "", "track02.ogg", "track03.ogg", "track04.ogg", "track05.ogg",
			"track06.ogg", "track07.ogg", "track08.ogg", "track09.ogg", "track10.ogg", "track11.ogg", "track12.ogg",
			"track13.ogg", "track14.ogg", "track15.ogg", "track16.ogg", "track17.ogg", "track18.ogg", "track19.ogg" };

	public static void sndInit() {
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

		nCreepyTimer = nCreepyTime;
		InitSoundInfo(cfg.maxvoices);
	}

	private static void InitSoundInfo(int voices) {
		sActiveSound = new ActiveSound[voices];
		for (int i = 0; i < sActiveSound.length; i++) {
			sActiveSound[i] = new ActiveSound();
			sActiveSound[i].ambchannel = i;
		}
	}

	private static int pSoundVolume, pSoundPitch;

	public static void GetSpriteSoundPitch(int nSector, int pVolume, int pPitch) {
		if (!isValidSector(nSector))
			return;
		pSoundVolume = pVolume;
		pSoundPitch = pPitch;
		if (((nLocalSectFlags ^ SectFlag[nSector]) & 0x2000) != 0) {
			pSoundVolume >>= 1;
			pSoundPitch -= 1200;
		}
	}

	public static boolean midRestart() {
		StopMusic();

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
		InitSoundInfo(cfg.maxvoices);

		return true;
	}

	public static int GetFrameSound(int a1, int a2) {
		if(a1 == -1 || (a2 + SeqBase[a1]) == -1) return -1;
		return FrameSound[a2 + SeqBase[a1]];
	}

	public static void LoadFX() {
		for (int i = 0; i < 80; i++) {
			StaticSound[i] = LoadSound(SoundFiles[i]);
		}
	}

	public static void StartElevSound(short a1, int a2) {
		if ((a2 & 2) != 0) {
			D3PlayFX(StaticSound[nElevSound], a1);
		} else {
			D3PlayFX(StaticSound[nStoneSound], a1);
		}
	}

	public static int LoadSound(String name) {
		for (int i = 0; i < nSoundCount; i++) {
			if (szSoundName[i].equalsIgnoreCase(name))
				return i;
		}

		if (nSoundCount >= 200) {
			game.ThrowError("Too many sounds being loaded... increase array size");
			return -1;
		}

		byte[] data = BuildGdx.cache.getBytes(name + ".voc", 1);
		if (data == null) {
			Console.Println("Sound " + name + " not found.", Console.OSDTEXT_RED);
			return -1;
		}

		if(name.equals("WASP_WNG"))
			nWaspSound = nSoundCount;
		szSoundName[nSoundCount] = name;
		SoundBufs[nSoundCount] = new SoundResource(data, nSoundCount);
		return nSoundCount++;
	}

	public static void sndHandlePause(boolean gPaused)
	{
		if(gPaused) {
			if (currMusic != null)
				currMusic.pause();
			BuildGdx.audio.getSound().stopAllSounds();
		} else {
			if (!cfg.muteMusic && currMusic != null)
				currMusic.resume();
		}
	}

	public static void searchCDtracks()
	{
		CueScript cdTracks = null;
		String[] cds = new String[cdtracks.length];
		System.arraycopy(cdtracks, 0, cds, 0, cds.length);

		for (Iterator<FileEntry> it = BuildGdx.compat.getDirectory(Path.Game).getFiles().values().iterator(); it.hasNext();) {
			FileEntry file = it.next();
			if (file.getExtension().equals("cue")) {
				byte[] data = BuildGdx.compat.getBytes(file);
				if(data != null) {
					cdTracks = new CueScript(file.getName(), data);
					String[] newcds = cdTracks.getTracks();
					System.arraycopy(newcds, 0, cds, 0, Math.min(newcds.length, cds.length));
					break;
				}
			}
		}

		int numtracks = cds.length;
		for (int i = 0; i < cds.length; i++) {
			if (!BuildGdx.cache.contains(cds[i], 0)) {
				cds[i] = null;
				numtracks--;
			}
		}

		if (numtracks <= 0 && cdTracks == null) {
			DirectoryEntry mus = BuildGdx.compat.checkDirectory("music");
			if (mus != null) {
				System.arraycopy(cdtracks, 0, cds, 0, cds.length);
				numtracks = cds.length;
				for (int i = 0; i < cds.length; i++) {
					FileEntry fil;
					if ((fil = mus.checkFile(cds[i])) == null) {
						cds[i] = null;
						numtracks--;
					} else
						cds[i] = fil.getPath();
				}
			}
		}

		System.arraycopy(cds, 0, cdtracks, 0, cds.length);
		if (numtracks > 0)
			Console.Println(numtracks + " cd tracks found...");
		else
			Console.Println("Cd tracks not found.");
	}

	public static boolean MusicPlaying()
	{
		return currMusic != null && currMusic.isPlaying();
	}

	public static boolean playCDtrack(int nTrack, boolean loop)
	{
		if ( /*cfg.musicType == 0 ||*/ nTrack < 0)
			return false;

		if(MusicPlaying() && currTrack == nTrack)
			return true;

		MusicSource mus = null;
		if(cdtracks != null && nTrack > 0 && nTrack <= cdtracks.length && cdtracks[nTrack - 1] != null && (mus = BuildGdx.audio.newMusic(MusicType.Digital, cdtracks[nTrack - 1])) != null) {
			StopMusic();
			currMusic = mus;
			currTrack = nTrack;
			currMusic.play(loop);
			return true;
		}

		return false;
	}

	public static void sndPlayMusic()
	{
		if(!cfg.muteMusic)
			BuildGdx.audio.setVolume(Driver.Music, cfg.musicVolume);
		else BuildGdx.audio.setVolume(Driver.Music, 0);

		if (mUserFlag != UserFlag.UserMap)
		{
			int num = levelnum;
			if (levelnum != 0)
				num = levelnum - 1;
			playCDtrack(num % 8 + 11, true);
		}
		else
		{
			if(!MusicPlaying())
			{
				int num = (int) ((Math.random() * 8) + 11);
				playCDtrack(num, true);
			}
		}
	}

	public static void StopMusic()
	{
		if(currMusic != null)
			currMusic.stop();

		currTrack = -1;
		currMusic = null;
	}

	public static void StopAllSounds() {
		BuildGdx.audio.getSound().stopAllSounds();
		for (int i = 0; i < sActiveSound.length; i++) {
			if (sActiveSound[i].pHandle != null)
				sActiveSound[i].pHandle.dispose();
			sActiveSound[i].pHandle = null;
			sActiveSound[i].rate = 0;
		}
		nAmbientChannel = -1;
	}

	public static Source PlayLocalSound(int soundid, int rateoffs) {
		if (cfg.noSound || !BuildGdx.audio.IsInited(Driver.Sound) || soundid < 0 || soundid >= SoundBufs.length)
			return null;

		if (nAmbientChannel == nLocalChan)
			nAmbientChannel = -1;

		ActiveSound pSound = sActiveSound[nLocalChan];
		SoundResource res = SoundBufs[soundid];
		if (res == null)
			return null;

		int rate = res.rate;
		if (rateoffs != 0)
			rate += rateoffs;

		Source hVoice = BuildGdx.audio.newSound(res.ptr, rate, res.bits, 255);
		if (hVoice != null) {
			if(LocalSoundPlaying())
				StopLocalSound();

			if (res.loop)
				hVoice.setLooping(true, 0, -1);
//			hVoice.setCallback(callback, nLocalChan);
			hVoice.setGlobal(1);
			hVoice.play(255.0f);

			pSound.pHandle = hVoice;
			pSound.rate = rate;
			pSound.soundnum = soundid;
			SetLocalChan(0);

			return hVoice;
		}

		return null;
	}

	public static void CheckAmbience(int sect) {
		if (isValidSector(sect) && SectSound[sect] != -1) {
			short nSourceSect = SectSoundSect[sect];
			WALL pWall = wall[sector[nSourceSect].wallptr];
			if (nAmbientChannel < 0) {
				PlayFXAtXYZ(SectSound[sect] | 0x4000, pWall.x, pWall.y, sector[nSourceSect].floorz, sect);
				return;
			}
			ActiveSound pSound = sActiveSound[nAmbientChannel];
			if (sect == nSourceSect) {
				SPRITE pPlayer = sprite[PlayerList[nLocalPlayer].spriteId];
				pSound.x = pPlayer.x;
				pSound.y = pPlayer.y;
				pSound.z = pPlayer.z;
			} else {
				pSound.x = pWall.x;
				pSound.y = pWall.y;
				pSound.z = sector[nSourceSect].floorz;
			}
			return;
		}

		if (nAmbientChannel != -1) {
			if (sActiveSound[nAmbientChannel].pHandle != null)
				sActiveSound[nAmbientChannel].pHandle.dispose();
			sActiveSound[nAmbientChannel].pHandle = null;
			sActiveSound[nAmbientChannel].rate = 0;
			nAmbientChannel = -1;
		}
	}

	public static void StopLocalSound() {
		if (nLocalChan == nAmbientChannel)
			nAmbientChannel = -1;
		if (LocalSoundPlaying())
			sActiveSound[nLocalChan].pHandle.dispose();
		sActiveSound[nLocalChan].pHandle = null;
		sActiveSound[nLocalChan].rate = 0;
	}

	public static void StopSpriteSound(int nSprite) {
		if (cfg.noSound || !BuildGdx.audio.IsInited(Driver.Sound))
			return;

		int i = 0;
		while (i < sActiveSound.length) {
			if (SoundPlaying(i) && nSprite == sActiveSound[i].spr) {
				sActiveSound[i].pHandle.dispose();
				sActiveSound[i].pHandle = null;
				sActiveSound[i].rate = 0;
				break;
			}
			i++;
		}
	}

	public static void BendAmbientSound() {
		if (cfg.noSound || !BuildGdx.audio.IsInited(Driver.Sound))
			return;

		if (nAmbientChannel >= 0) {
			ActiveSound pSound = sActiveSound[nAmbientChannel];
			if (pSound.pHandle != null)
				pSound.pHandle.setPitch((nDronePitch + 11000) / (float) pSound.rate);
		}
	}

	public static int PlayFXAtXYZ(int soundid, int x, int y, int z, int sect) {
		if (cfg.noSound || !BuildGdx.audio.IsInited(Driver.Sound))
			return -1;

		soundx = x;
		soundy = y;
		soundz = z;
		soundsect = sect & (MAXSECTORS - 1);

		int num = PlayFX2(soundid, -1);
		if (num != -1) {
			if ((sect & 0x4000) != 0)
				sActiveSound[num].asound_C = 2000;
		}
		return num;
	}

	public static void D3PlayFX(int soundid, int nSprite) {
		PlayFX2(soundid, nSprite);
	}

	public static int PlayFX2(int soundid, int nSprite) {
		if (cfg.noSound || !BuildGdx.audio.IsInited(Driver.Sound))
			return -1;

		nLocalSectFlags = SectFlag[nPlayerViewSect[nLocalPlayer]];
		boolean v33 = false;
		boolean v11 = false;
		if (nSprite >= 0) {
			v33 = (nSprite & 0x2000) != 0;
			v11 = (nSprite & 0x4000) != 0;
			nSprite &= 0xFFF;
			soundx = sprite[nSprite].x;
			soundy = sprite[nSprite].y;
			soundz = sprite[nSprite].z;
			soundsect = sprite[nSprite].sectnum;
		}

		int dx = (initx - soundx) >> 8;
		int dy = (inity - soundy) >> 8;

		int dist = GetDistFromDXDY(dx, dy);
		int vol = v33 ? 255 : BClipRange(255 + 10 - (sintable[(2 * dist) & 0x7FF] >> 6), 0, 255);

		if (dist >= 255 || vol == 0) {
			if (nSprite >= 0)
				StopSpriteSound(nSprite);
			return -1;
		}

		int v37 = soundid & 0xFE00;
		boolean v39 = (soundid & 0x2000) != 0;
		boolean v38 = (soundid & 0x1000) != 0;
		boolean v35 = (soundid & 0x4000) != 0;
		int v29 = 0x7FFFFFFF;
		int v36 = (soundid & 0xE00) >> 9;
		soundid &= 0x1FF;

		int v32 = 0;
		if (v38 || v35)
			v32 = 1000;
		else if (nSprite != -1 && v11)
			v32 = 2000;

		int nFree = 0;
		int v30 = 0;
		int v26 = 0;

		if(soundid < 0 || soundid >= SoundBufs.length)
			return -1;

		for (int i = 1; i < sActiveSound.length; i++) {
			if (SoundPlaying(i)) {
				ActiveSound pSound = sActiveSound[i];
				if (v32 >= pSound.asound_C) {
					if (v29 > pSound.clock && pSound.asound_C == v32) {
						v30 = i;
						v29 = pSound.clock;
					}

					if (!v38) {
						if (soundid == pSound.soundnum) {
							if (!v39 && nSprite == pSound.spr)
								return -1;
							v26 = i;
						} else if (nSprite == pSound.spr && (v39 || soundid != pSound.soundnum)) {
							nFree = i;
							break;
						}
					}
				}
			} else
				nFree = i;
		}

		while (true) {
			if (nFree != 0) {
				ActiveSound pFree = sActiveSound[nFree];
				if (pFree.pHandle != null && pFree.pHandle.isActive()) {
					pFree.pHandle.dispose();
					if (pFree.ambchannel == nAmbientChannel)
						nAmbientChannel = -1;
					pFree.pHandle = null;
					pFree.rate = 0;
				}

				SoundResource res = SoundBufs[soundid];
				if (res == null)
					return -1;

				if(!cfg.bWaspSound && soundid == nWaspSound)
					return -1;

				int rate = res.rate;
				int pitch = 0;
				if (v36 != 0)
					pitch = -16 * (totalmoves & ((1 << v36) - 1));
				if (pitch != 0)
					rate += pitch;
				pitch += rate;

				pFree.oldpitch = pitch;
				pFree.oldvolume = vol;
				if (nSprite < 0) {
					pFree.x = soundx;
					pFree.y = soundy;
					pFree.z = soundz;
					pFree.sectnum = soundsect;
				}
				GetSpriteSoundPitch(soundsect, vol, pitch);
				pFree.pitch = pSoundPitch;
				pFree.volume = pSoundVolume;
//	                pFree.panning = v34;
				pFree.spr = nSprite;
				pFree.soundnum = soundid;
				pFree.clock = totalclock;
				pFree.asound_C = v32;
				pFree.ambient = v37;

				Source hVoice = BuildGdx.audio.newSound(res.ptr, rate, res.bits, 255);
				if (hVoice != null) {
					boolean isPlayer = nSprite == PlayerList[nLocalPlayer].spriteId;

					if (res.loop)
						hVoice.setLooping(true, 0, -1);
					if(isPlayer)
						hVoice.setGlobal(1);
					else hVoice.setPosition(soundx, soundz >> 4, soundy);
					hVoice.setCallback(callback, nFree);
					hVoice.play(vol / 255.0f);

					if (v35)
						nAmbientChannel = pFree.ambchannel;
					if (!isPlayer && isValidSprite(nSprite) && (sprite[nSprite].cstat & 0x101) != 0)
						nCreepyTimer = nCreepyTime;

					pFree.pHandle = hVoice;
					pFree.rate = rate;
					return pFree.ambchannel;
				}
				return -1;
			} else {
				if (v26 != 0) {
					nFree = v26;
					continue;
				}
				if (v30 != 0) {
					nFree = v30;
					continue;
				}
				break;
			}
		}
		return -1;

	}

	public static int GetDistFromDXDY(int dx, int dy) {
		return ((dx * dx + dy * dy) >> 3) - ((dx * dx + dy * dy) >> 5);
	}

	public static void StartSwirlies() {
		if (cfg.noSound || !BuildGdx.audio.IsInited(Driver.Sound))
			return;

		StopAllSounds();
		nNextFreq = 19000;
		nSwirlyFrames = 0;

		for (int i = 1; i <= 4; i++)
			StartSwirly(i);
	}

	private static void StartSwirly(int nSwirly) {
		ActiveSound pSound = sActiveSound[nSwirly];
		pSound.panning &= 0x7ff;
		pSound.oldpitch = nNextFreq - RandomSize(9);
		nNextFreq = Math.min(25000 - 6 * RandomSize(10), 32000);
		SoundResource res = SoundBufs[StaticSound[67]];
		if (res == null)
			return;

		Source pHandle = pSound.pHandle = BuildGdx.audio.newSound(res.ptr, pSound.oldpitch, res.bits, 255);
		if (pHandle != null) {
			pHandle.setGlobal(1);
			pHandle.setPosition(pSound.panning, 0, 0);
			pHandle.setCallback(callback, nSwirly);
			pHandle.play(Math.min(nSwirlyFrames + 1, 220) / 220.0f);
			pHandle.rate = pSound.oldpitch;
		}
	}

	public static void UpdateSwirlies() {
		if (cfg.noSound || !BuildGdx.audio.IsInited(Driver.Sound))
			return;

		nSwirlyFrames++;

		int nSound = 1;
		int nShift = 5;
		for (int i = 1; i <= 4; i++) {
			ActiveSound pSound = sActiveSound[nSound++];
			if (pSound.pHandle == null || !pSound.pHandle.isActive())
				StartSwirly(i);

			if (pSound.pHandle != null) {
				pSound.panning = (sintable[(totalclock << nShift) & 0x7FF] >> 8);
				pSound.pHandle.setPosition(pSound.panning, 0, 0);
			}
			nShift++;
		}
	}

	public static void SoundBigEntrance() {
		if (cfg.noSound || !BuildGdx.audio.IsInited(Driver.Sound))
			return;

		StopAllSounds();
		int rate = -1200;

		for (int i = 0; i < 4; i++) {
			SoundResource res = SoundBufs[12];
			if (res == null)
				return;

			ActiveSound pSound = sActiveSound[i];
			Source pHandle = pSound.pHandle = BuildGdx.audio.newSound(res.ptr, rate + 11000, res.bits, 255);
			if (pHandle == null)
				return;

			pSound.oldpitch = rate;
			pHandle.setGlobal(1);
			pHandle.setPosition(63 - 127 * (i & 1), 0, 0);
			pHandle.setCallback(callback, i);
			pHandle.play(200);
			pHandle.rate = rate + 11000;
			rate += 512;
		}
	}

	private static boolean SoundPlaying(int num) {
		return num >= 0 && num < sActiveSound.length && sActiveSound[num].pHandle != null && sActiveSound[num].pHandle.isActive();
	}

	public static boolean LocalSoundPlaying() {
		return SoundPlaying(nLocalChan);
	}

	public static int GetLocalSound() {
		if (!LocalSoundPlaying())
			return -1;

		return sActiveSound[nLocalChan].soundnum & 0x1ff;
	}

	public static void SetLocalChan(int channel) {
		if(channel >= 0 && channel < sActiveSound.length)
			nLocalChan = channel;
	}

	public static void PlayLogoSound() {
		PlayLocalSound(StaticSound[28], 7000);
	}

	public static void PlayGameOverSound() {
		PlayLocalSound(StaticSound[28], 0);
	}

	public static void UpdateSounds() {
		if (cfg.noSound || !BuildGdx.audio.IsInited(Driver.Sound) || nFreeze != 0)
			return;

		nLocalSectFlags = SectFlag[nPlayerViewSect[nLocalPlayer]];
		int earx, eary, earz, earang;
		if (nSnakeCam >= 0) {
			SPRITE pSnake = sprite[SnakeList[nSnakeCam].nSprite[0]];
			earx = pSnake.x;
			eary = pSnake.y;
			earz = pSnake.z;
			earang = pSnake.ang;
		} else {
			earx = initx;
			eary = inity;
			earz = initz;
			earang = inita;
		}

		BuildGdx.audio.getSound().setListener(earx, earz >> 4, eary, earang);

		int dx, dy;
		for (int i = 1; i < sActiveSound.length; i++) {
			if (SoundPlaying(i)) {
				ActiveSound pSound = sActiveSound[i];
				Source pHandle = pSound.pHandle;

				SPRITE spr = (pSound.spr >= 0) ? sprite[pSound.spr] : null;
				if (spr != null) {
					dx = earx - spr.x;
					dy = eary - spr.y;

					if(spr != sprite[PlayerList[nLocalPlayer].spriteId])
						pHandle.setPosition(spr.x, spr.z >> 4, spr.y);
					else pHandle.setPosition(0, 0, 0); //global position
				} else {
					dx = earx - pSound.x;
					dy = eary - pSound.y;
					pHandle.setPosition(pSound.x, pSound.z >> 4, pSound.y);
				}

				int dist = GetDistFromDXDY(dx >> 8, dy >> 8);
				if (dist >= 255) {
					pHandle.dispose();
					if ((pSound.ambient & 0x4000) != 0)
						nAmbientChannel = -1;
					return;
				}

				int vol = BClipRange(255 + 10 - (sintable[(2 * dist) & 0x7FF] >> 6), 0, 255);
				int pitch = pSound.oldpitch;
				int sectnum = pSound.sectnum;
				if (spr != null) {
					if (spr == sprite[PlayerList[nLocalPlayer].spriteId])
						sectnum = nPlayerViewSect[nLocalPlayer];
					else
						sectnum = spr.sectnum;
				}

				GetSpriteSoundPitch(sectnum, vol, pitch);
				vol = pSoundVolume;
				pitch = pSoundPitch;
				if (pSound.pitch != pitch)
					pSound.pitch = pitch;

				if (vol != pSound.volume) {
					if (pitch < 0)
						pitch = 7000;

					pHandle.setPitch(pitch / (float) pHandle.rate);
					pHandle.setVolume(vol / 255.0f);
					pSound.volume = vol;
				}
			}
		}

		if (nFreeze != 0 || levelnum == 20 || --nCreepyTimer > 0)
			return;

		if (nCreaturesLeft > 0 && (SectFlag[nPlayerViewSect[nLocalPlayer]] & 0x2000) == 0) {
			SPRITE pPlayer = sprite[PlayerList[nLocalPlayer].spriteId];
			int soundId = GetFrameSound(SeqOffsets[65], totalmoves % SeqSize[SeqOffsets[65]]);
			int sx = (totalmoves + 32) & 0x1F;
			if ((totalmoves & 1) != 0)
				sx = -sx;
			int sy = (totalmoves + 32) & 0x3F;
			if ((totalmoves & 2) != 0)
				sy = -sy;

			PlayFXAtXYZ(soundId, pPlayer.x + sx, sy + pPlayer.y, pPlayer.z, pPlayer.sectnum);
		}
		nCreepyTimer = nCreepyTime;
	}
}
