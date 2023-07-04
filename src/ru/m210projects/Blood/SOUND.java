// This file is part of BloodGDX.
// Copyright (C) 2017-2019  Alexander Makarov-[M210] (m210-2007@mail.ru)
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

package ru.m210projects.Blood;

import static ru.m210projects.Blood.DB.kMaxXSprites;
import static ru.m210projects.Blood.DB.xsprite;
import static ru.m210projects.Blood.Gameutils.ClipLow;
import static ru.m210projects.Blood.Gameutils.ClipRange;
import static ru.m210projects.Blood.Gameutils.Dist3d;
import static ru.m210projects.Blood.Gameutils.FindSector;
import static ru.m210projects.Blood.Gameutils.M2X;
import static ru.m210projects.Blood.Gameutils.foundSector;
import static ru.m210projects.Blood.Globals.*;
import static ru.m210projects.Blood.Main.*;
import static ru.m210projects.Blood.View.*;
import static ru.m210projects.Blood.LEVELS.*;
import static ru.m210projects.Blood.Strings.mid;
import static ru.m210projects.Blood.Strings.raw;
import static ru.m210projects.Blood.Strings.sfx;
import static ru.m210projects.Blood.Trig.Cos;
import static ru.m210projects.Blood.Trig.Sin;
import static ru.m210projects.Build.Engine.headspritestat;
import static ru.m210projects.Build.Engine.nextspritestat;
import static ru.m210projects.Build.Engine.numsectors;
import static ru.m210projects.Build.Engine.sprite;
import static ru.m210projects.Build.Pragmas.*;
import static ru.m210projects.Build.OnSceenDisplay.Console.*;

import java.nio.ByteBuffer;

import ru.m210projects.Blood.Main.UserFlag;
import ru.m210projects.Blood.Types.AMBIENT;
import ru.m210projects.Blood.Types.SAMPLE2D;
import ru.m210projects.Blood.Types.SFX;
import ru.m210projects.Blood.Types.SOUNDEFFECT;
import ru.m210projects.Blood.Types.XSPRITE;
import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.Audio.MusicSource;
import ru.m210projects.Build.Audio.BuildAudio.Driver;
import ru.m210projects.Build.Audio.BuildAudio.MusicType;
import ru.m210projects.Build.Audio.Sound.SystemType;
import ru.m210projects.Build.FileHandle.Group;
import ru.m210projects.Build.FileHandle.GroupResource;
import ru.m210projects.Build.FileHandle.Resource;
import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Build.Types.LittleEndian;
import ru.m210projects.Build.Types.SPRITE;

import com.badlogic.gdx.utils.BufferUtils;

public class SOUND {
	
	public static final int BLUDSPLT = 385;
	
	public static final String[] track = {
		"blood00.ogg",
		"blood01.ogg",
		"blood02.ogg",
		"blood03.ogg",
		"blood04.ogg",
		"blood05.ogg",
		"blood06.ogg",
		"blood07.ogg",
		"blood08.ogg",
		"blood09.ogg"
	};
	
	public static final int MAXUSERTRACKS = 64;
	public static String[] usertrack = new String[MAXUSERTRACKS];

	public static SFX[] pSFXs = new SFX[65536];

	// sound of sound in XY units / sec
	public static final int kSoundVel = M2X(280.0);
	// Intra-aural delay in seconds
	public static final float kIntraAuralTime	= 0.0006f;
	public static final int kIntraAuralDist	= (int) (kIntraAuralTime / 2 * kSoundVel);
	// volume different between sound in front and behind pinna focus
	public static final int kBackFilter = 0x4000;
	
	public static int nSndEffect;
	public static SOUNDEFFECT[] soundEffect;

	public static final int kVolScale = 80;
	public static final int kMaxAmbients = 16;
	public static final int kMaxChannels = 32;
	public static final int kMaxSources = 256;
	public static final int[] sndRate =
	{
		11025, 11025, 11025, 11025, 11025,
		22050, 22050, 22050, 22050, 
		44100, 44100, 44100, 44100
	};
	
	public static SAMPLE2D[] Channel;
	
	public static int earX0;
	public static int earX;
	public static int earY0;
	public static int earY;
	public static int earZ0;
	public static int earZ;
	public static int earVX;
	public static int earVY;
	public static int earVZ;
	public static int earVolume;
	public static int earVelocity;
	
	public static AMBIENT[] ambient;
	public static int numambients;
	
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
	
	public static boolean sndLoadSound(int soundId) {
		if(soundId < 0) return false;
		
		if(pSFXs[soundId] == null) {
			Resource dat = BuildGdx.cache.open(soundId, sfx);
			if(dat != null) {
				pSFXs[soundId] = new SFX(dat);
				dat.close();
			}
			else return false;
		}
		
		if(pSFXs[soundId].hResource == null)
		{
			String rawName = pSFXs[soundId].rawName + "." + raw;
			Resource fp = BuildGdx.cache.open(rawName, 0);

			if(fp != null) {
				int size = fp.size();
				pSFXs[soundId].hResource = ByteBuffer.allocateDirect(size);
				pSFXs[soundId].hResource.put(fp.getBytes());
				pSFXs[soundId].hResource.rewind();
				pSFXs[soundId].size = size;
				fp.close();
			}
			else {
				return false; //can't load resource
			}
		}
		
		return true;
	}

	public static void sfxStart3DSound( SPRITE pSprite, int soundId, int nChannel, int flags ) {
		if(cfg.noSound || !BuildGdx.audio.IsInited(Driver.Sound))
			return;

		if(pSprite != null && soundId >= 0)
		{
			if(!sndLoadSound(soundId))
				return;
			
			SFX pEffect = pSFXs[soundId];

			if(pEffect.size <= 0)
				return;
			
			int nPitch = mulscale(sndGetSampleRate(pEffect.format), pEffect.pitch, 16);
			SOUNDEFFECT pSource = null;
			if ( nChannel < 0 )
            {
            	if ( nSndEffect >= kMaxSources )
            		return;

            	pSource = soundEffect[nSndEffect++];
            	pSource.pSprite = null;
            }
			else
			{
				int i;
				for ( i = 0; i < nSndEffect; i++ )
                {
					pSource = soundEffect[i];
					
					if ( nChannel == pSource.channel && (pSprite == pSource.pSprite || (flags & 1) != 0) )
	                {
	                    if ( pSource.hVoice != null 
	                    		&& pSource.hVoice.data == pSource.hResource 
	                    		&& ((flags & 4) != 0 
	                    		&& nChannel == pSource.channel 
	                    		|| ((flags & 2) != 0 && soundId == pSource.soundId)) )
	                    	return;

	                    if ( pSource.hVoice != null) 
	                    	pSource.hVoice.dispose();

	                    if ( pSource.hResource != null )
	                    	pSource.hResource = null;
	                    break;
	                }
                }

				if ( i == nSndEffect )
                {
					if ( nSndEffect >= kMaxSources )
	            		return;
					pSource = soundEffect[nSndEffect++];
                }
				pSource.pSprite = pSprite;
				pSource.channel = nChannel;
			}

			pSource.x = pSprite.x;
            pSource.y = pSprite.y;
            pSource.z = pSprite.z;
            pSource.nSector = pSprite.sectnum;
            pSource.oldX = pSource.x;
            pSource.oldY = pSource.y;
            pSource.oldZ = pSource.z;
            pSource.soundId = soundId;
            pSource.hResource = pEffect.hResource;
            pSource.relVol = pEffect.relVol;
            pSource.nPitch = nPitch;

            Calc3DValues(pSource);
            int nPriority = 1;

            if(nPriority < earVolume)
            	nPriority = earVolume;
            
            int loopStart = pEffect.loopStart;
            if(nChannel < 0)
            	loopStart = -1;

            ByteBuffer pRaw = pEffect.hResource;
            pRaw.rewind();

            boolean global = pSprite == gPlayer[gViewIndex].pSprite;
            pSource.hVoice = BuildGdx.audio.newSound(pRaw, global?pSource.nPitch:earVelocity, 8, nPriority);
            if(pSource.hVoice != null) {
	            if(loopStart >= 0) 
	            	pSource.hVoice.setLooping(true, loopStart, ClipLow(pEffect.size - 1, 0));
	            if(global) {
	            	pSource.hVoice.setGlobal(1);
	            	pSource.pSprite = gPlayer[gViewIndex].pSprite;
	            } else setPosition(pSource);
	            pSource.hVoice.play(earVolume / 255.0f);
            }
		}
	}
	
	// same as previous, but allows to set custom pitch and volume
	public static void sfxStart3DSoundCP( SPRITE pSprite, int soundId, int nChannel, int flags, long pitch, int volume ) {
		if(cfg.noSound || !BuildGdx.audio.IsInited(Driver.Sound))
			return;

		if(pSprite != null && soundId >= 0)
		{
			if(!sndLoadSound(soundId))
				return;
			
			SFX pEffect = pSFXs[soundId];
			
			if (pitch <= 0) pitch = pEffect.pitch;
			else pitch-=ru.m210projects.Blood.Gameutils.BiRandom(pEffect.pitchrange);
			
			if(pEffect.size <= 0)
				return;
			
			int nPitch = mulscale(sndGetSampleRate(pEffect.format), pitch, 16);
			SOUNDEFFECT pSource = null;
			if ( nChannel < 0 )
            {
            	if ( nSndEffect >= kMaxSources )
            		return;

            	pSource = soundEffect[nSndEffect++];
            	pSource.pSprite = null;
            }
			else
			{
				int i;
				for ( i = 0; i < nSndEffect; i++ )
                {
					pSource = soundEffect[i];
					
					if ( nChannel == pSource.channel && (pSprite == pSource.pSprite || (flags & 1) != 0) )
	                {
	                    if ( pSource.hVoice != null 
	                    		&& pSource.hVoice.data == pSource.hResource 
	                    		&& ((flags & 4) != 0 
	                    		&& nChannel == pSource.channel 
	                    		|| ((flags & 2) != 0 && soundId == pSource.soundId)) )
	                    	return;

	                    if ( pSource.hVoice != null) 
	                    	pSource.hVoice.dispose();

	                    if ( pSource.hResource != null )
	                    	pSource.hResource = null;
	                    break;
	                }
                }

				if ( i == nSndEffect )
                {
					if ( nSndEffect >= kMaxSources )
	            		return;
					pSource = soundEffect[nSndEffect++];
                }
				pSource.pSprite = pSprite;
				pSource.channel = nChannel;
			}

			pSource.x = pSprite.x;
            pSource.y = pSprite.y;
            pSource.z = pSprite.z;
            pSource.nSector = pSprite.sectnum;
            pSource.oldX = pSource.x;
            pSource.oldY = pSource.y;
            pSource.oldZ = pSource.z;
            pSource.soundId = soundId;
            pSource.hResource = pEffect.hResource;
            pSource.relVol = ((volume == 0) ? pEffect.relVol : ((volume == -1) ? 0 : (Math.min(volume, 255))));
            pSource.nPitch = nPitch;

            Calc3DValues(pSource);
            int nPriority = 1;

            if(nPriority < earVolume)
            	nPriority = earVolume;
            
            int loopStart = pEffect.loopStart;
            if(nChannel < 0)
            	loopStart = -1;

            ByteBuffer pRaw = pEffect.hResource;
            pRaw.rewind();

            boolean global = pSprite == gPlayer[gViewIndex].pSprite;
            pSource.hVoice = BuildGdx.audio.newSound(pRaw, global?pSource.nPitch:earVelocity, 8, nPriority);
            if(pSource.hVoice != null) {
	            if(loopStart >= 0) 
	            	pSource.hVoice.setLooping(true, loopStart, ClipLow(pEffect.size - 1, 0));
	            if(global) {
	            	pSource.hVoice.setGlobal(1);
	            	pSource.pSprite = gPlayer[gViewIndex].pSprite;
	            } else setPosition(pSource);
	            pSource.hVoice.play(earVolume / 255.0f);
            }
		}
	}
	
	public static void sfxCreate3DSound( int x, int y, int z, int soundId, int nSector ) {
		if(cfg.noSound || !BuildGdx.audio.IsInited(Driver.Sound))
			return;

		if (nSector < 0 || nSector > numsectors)
			return;
		
		if ( soundId < 0 ) game.dassert("Invalid sound ID: " + soundId);

		if(!sndLoadSound(soundId))
			return;
		
		SFX pEffect = pSFXs[soundId];

		if(pEffect.size <= 0)
			return;
		
		int nPitch = mulscale(sndGetSampleRate(pEffect.format), pEffect.pitch, 16);
		
		if ( nSndEffect < kMaxSources )
	    {
	    	SOUNDEFFECT pSoundEffect = soundEffect[nSndEffect++];
	        pSoundEffect.pSprite = null;
	        pSoundEffect.x = x;
	        pSoundEffect.y = y;
	        pSoundEffect.z = z;
	        pSoundEffect.nSector = nSector;
	        FindSector(x, y, z, (short)pSoundEffect.nSector);
	        pSoundEffect.nSector = foundSector;
	        
	        pSoundEffect.oldX = pSoundEffect.x;
	        pSoundEffect.oldY = pSoundEffect.y;
	        pSoundEffect.oldZ = pSoundEffect.z;
	        pSoundEffect.soundId = soundId;
	        pSoundEffect.hResource = pEffect.hResource;
	        pSoundEffect.relVol = pEffect.relVol;
	        pSoundEffect.nPitch = nPitch;
	        pSoundEffect.format = pEffect.format;
	       
	        ByteBuffer pRaw = pEffect.hResource;
            pRaw.rewind();

	        Calc3DValues(pSoundEffect);
	        
	        int nPriority = 1;
	        if(nPriority < earVolume)
            	nPriority = earVolume;
	        
	        pSoundEffect.hVoice = BuildGdx.audio.newSound(pRaw, earVelocity, 8, nPriority);
            if(pSoundEffect.hVoice != null) {
            	setPosition(pSoundEffect);
            	pSoundEffect.hVoice.play(earVolume / 255.0f);
            }
	    }
	}
	
	public static void setPosition(SOUNDEFFECT pSoundEffect)
	{
		pSoundEffect.hVoice.setPosition(pSoundEffect.x, pSoundEffect.z >> 4, pSoundEffect.y);
	}
	
	public static void Calc3DValues(SOUNDEFFECT pSource)
	{
		if(cfg.noSound || !BuildGdx.audio.IsInited(Driver.Sound))
			return;
		
		int dx = pSource.x - gPlayer[gViewIndex].pSprite.x;
		int dy = pSource.y - gPlayer[gViewIndex].pSprite.y;
		int dz = pSource.z - gPlayer[gViewIndex].pSprite.z;
		int nAngle = engine.getangle(dx, dy);
		int dist = Dist3d(dx,dy,dz);
		dist = ClipLow((dist >> 3) + (dist >> 2), 64);
		int monoVol = muldiv(pSource.relVol, kVolScale, dist);
		
		// normal vector for listener -> source
		dx = Cos(nAngle);
		dy = Sin(nAngle);
		
		earVolume = Vol3d(nAngle - (gPlayer[gViewIndex].pSprite.ang), monoVol);
		earVelocity = pSource.nPitch;

		int div = dmulscaler(dx, pSource.x - pSource.oldX, dy, pSource.y - pSource.oldY, 30) + 5853;
		int kVel = klabs(dmulscaler(dx, earVX, dy, earVY, 30) + 5853);
		if(div != 0) earVelocity = muldiv(kVel, pSource.nPitch, div);	
	}
	
	public static int Vol3d(int nAngle, int vol)
	{
		return vol - mulscale(vol, kBackFilter / 2 - mulscale(kBackFilter / 2, Cos(nAngle), 30), 16);
	}
	
	public static void sfxResetListener()
	{
		earX0 = earX = gPlayer[gViewIndex].pSprite.x;
		earY0 = earY = gPlayer[gViewIndex].pSprite.y;
		earZ0 = earZ = gPlayer[gViewIndex].pSprite.z;
	}
	
	public static void sfxUpdate3DSounds()
	{
		if(cfg.noSound || !BuildGdx.audio.IsInited(Driver.Sound) || nSndEffect >= kMaxSources)
			return;
		
		// update listener ear positions

		earX0 = earX;
		earY0 = earY;
		earZ0 = earZ;
		earX = gPlayer[gViewIndex].pSprite.x;
		earY = gPlayer[gViewIndex].pSprite.y;
		earZ = gPlayer[gViewIndex].pSprite.z;
		earVX = earX - earX0;
		earVY = earY - earY0;
		earVZ = earZ - earZ0;

		BuildGdx.audio.getSound().setListener(earX0, earZ0 >> 4, earY0, gPlayer[gViewIndex].pSprite.ang);
	
		for ( int i = nSndEffect - 1; i >= 0; --i )
		{
			SOUNDEFFECT pSoundEffect = soundEffect[i];
			if(pSoundEffect.hVoice == null)
			{
				SoundCallback(i);
			}
		
		    if ( pSoundEffect.hVoice != null && !pSoundEffect.hVoice.isActive())
		    {
		    	pSoundEffect.hVoice.dispose();
		    	SoundCallback(i);
		    	
		    } 
		    else if(pSoundEffect.hResource != null)
		    {
		    	SPRITE pSprite = pSoundEffect.pSprite;
		        if ( pSprite != null )
		        {
		        	pSoundEffect.oldX = pSoundEffect.x;
		        	pSoundEffect.oldY = pSoundEffect.y;
		        	pSoundEffect.oldZ = pSoundEffect.z;
		        	pSoundEffect.x = pSprite.x;
		        	pSoundEffect.y = pSprite.y;
		        	pSoundEffect.z = pSprite.z;
		        	pSoundEffect.nSector = pSprite.sectnum;	
		        }
		        Calc3DValues(pSoundEffect);
		        if ( pSoundEffect.hVoice != null && pSoundEffect.hVoice.data == pSoundEffect.hResource/*&& (pSoundEffect.hVoice.channel == -1 || pSoundEffect.hVoice.channel == i)*/ )
		        {
		        	if(pSoundEffect.pSprite != gPlayer[gViewIndex].pSprite) { //not global sound
			        	pSoundEffect.hVoice.setVolume(earVolume / 255.0f);
			        	float pitch = 0; 
			        	if(pSoundEffect.nPitch > 0)
			        		pitch = (float) earVelocity / pSoundEffect.nPitch;
			        	pSoundEffect.hVoice.setPitch(pitch);
			        	setPosition(pSoundEffect);
		        	}
		        }
		    }
		}
	}
	
	public static void SoundCallback(int num)
	{
		SOUNDEFFECT pSoundEffect = soundEffect[num];
		pSoundEffect.clear();

    	int tmp = nSndEffect-1;
	    soundEffect[num] = soundEffect[tmp];
	    soundEffect[tmp] = pSoundEffect;
	    nSndEffect = tmp;
	}
	
	public static void sfxKill3DSound(SPRITE pSprite, int nChannel, int nSoundId)
	{
		if(!BuildGdx.audio.IsInited(Driver.Sound))
			return;
		
		int i = nSndEffect;
		while(--i >= 0)
		{
			SOUNDEFFECT pSoundEffect = soundEffect[i];
			if(pSprite == pSoundEffect.pSprite && (nChannel < 0 || nChannel == pSoundEffect.channel) && (nSoundId < 0 || nSoundId == pSoundEffect.soundId) )
			{
				if ( pSoundEffect.hVoice != null )
					pSoundEffect.hVoice.dispose();

				SoundCallback(i);
			}
		}
	}
	
	public static void sfxKillAll3DSounds()
	{
		int i = nSndEffect;
		while(--i >= 0)
		{
			SOUNDEFFECT pSoundEffect = soundEffect[i];
			
			if ( pSoundEffect.hVoice != null ) 
				pSoundEffect.hVoice.dispose();
			
			SoundCallback(i);
		}
	}
	
	public static void varsInit()
	{
		nSndEffect = 0;
		Channel = new SAMPLE2D[kMaxChannels];
		for(int i = 0; i < kMaxChannels; i++)
			Channel[i] = new SAMPLE2D();
		
		soundEffect = new SOUNDEFFECT[kMaxSources];
		for(int i = 0; i < kMaxSources; i++)
			soundEffect[i] = new SOUNDEFFECT();
		
		ambient = new AMBIENT[kMaxAmbients];
		for(int i = 0; i < kMaxAmbients; i++)
			ambient[i] = new AMBIENT();
		numambients = 0;
	}

	public static void sndInit()
	{
		if(cfg.maxvoices < 4)
			cfg.maxvoices = 4;

		varsInit();
		if(BuildGdx.audio.getSound().init(SystemType.Stereo, cfg.maxvoices, cfg.resampler_num)) {
			BuildGdx.audio.setVolume(Driver.Sound, cfg.soundVolume);	
		}
		else {
			Console.Println(BuildGdx.audio.getName(Driver.Sound) + " initialization failed", OSDTEXT_RED);
		}
		
		if(!BuildGdx.audio.getMusic().init()) 
			Console.Println(BuildGdx.audio.getName(Driver.Music) + " initialization failed", OSDTEXT_RED);

		
		if(!cfg.muteMusic)
			BuildGdx.audio.setVolume(Driver.Music, cfg.musicVolume);	
		else BuildGdx.audio.setVolume(Driver.Music, 0);	
	}
	
	public static int sndGetSampleRate(int SND_FMT)
	{
		if ( SND_FMT >= 13 )
			return 11025;
		else
	    	return sndRate[SND_FMT];
	}
	
	public static SAMPLE2D FindChannel()
	{
		int i;
		for (i = kMaxChannels - 1; i >= 0 && Channel[i].hResource != null; --i);
		if ( i < 0 ) return null; //ThrowError("No free channel available for sample", 21);
		
		return Channel[i];
	}
	
	public static void sndStopSound(SAMPLE2D pChannel)
	{
		if(!BuildGdx.audio.IsInited(Driver.Sound))
			return;
		
		if ( (pChannel.loop & 1) != 0 )
			pChannel.loop &= ~1;
		
		pChannel.hVoice.dispose();
	  	pChannel.hVoice = null;
	}
	
	public static void sndStopAllSounds()
	{
		sndStopAllSamples();
		sndStopMusic();
		sfxKillAll3DSounds();
    	ambStopAll();
	}
	
	public static void sndStopAllSamples()
	{
		for (int i = 0; i < kMaxChannels; i++)
		{
			SAMPLE2D pChannel = Channel[i];
			if(pChannel.hVoice != null)
				sndStopSound(pChannel);
			if(pChannel.hResource != null)
				pChannel.hResource = null;
		}
	}

	public static void sndStartSample( int soundId, int nVol, int nChannel, boolean loop )
	{
		if(cfg.noSound || !BuildGdx.audio.IsInited(Driver.Sound))
			return;
		
		if(!(nChannel >= -1 && nChannel < kMaxChannels))
			game.dassert("nChannel >= -1 && nChannel < kMaxChannels");

		
		if(!sndLoadSound(soundId))
		{
			Console.Println("Could not load sample" + ((pSFXs[soundId] != null) ? pSFXs[soundId].rawName : soundId), OSDTEXT_RED);
			return; //can't load resource
		}
		
		SFX pEffect = pSFXs[soundId];

		if(pEffect.size <= 0)
			return;

		SAMPLE2D pChannel;
		if (nChannel == -1)
			pChannel = FindChannel();
		else
			pChannel = Channel[nChannel];
		
		if(pChannel == null) return;

		// if this is a fixed channel with an active voice, kill the sound
		if ( pChannel.hVoice != null )  {
			sndStopSound(pChannel);
		}

		pChannel.hResource = pEffect.hResource;
		
		if ( nVol < 0 )
			nVol = pEffect.relVol;

		int sampleSize = pEffect.size;
		if(sampleSize > 0)
		{
			ByteBuffer pRaw = pChannel.hResource;
			pRaw.rewind();

			if(nChannel < 0) loop = false;

			pChannel.hVoice = BuildGdx.audio.newSound(pRaw, sndGetSampleRate(pEffect.format), 8, 80 * nVol);
            if(pChannel.hVoice != null) {
            	pChannel.loop &= ~1;
            	pChannel.hVoice.setGlobal(1);
            	
            	if(loop) {
	            	int loopStart = pEffect.loopStart;
					int loopEnd = ClipLow(sampleSize - 1, 0);
	            	pChannel.hVoice.setLooping(true, loopStart, loopEnd);
	            	pChannel.loop |= 1;
	            } 
	            pChannel.hVoice.play((80 * nVol) / 255.0f);
            }
		}
	}

	public static void sndPreloadSounds()
	{
		Group sounds = BuildGdx.cache.getGroup("sounds.rff");
		if(sounds != null) {
			for(GroupResource res : sounds.getList()) {
				if(res.getExtension().equals("sfx"))
					sndLoadSound(res.getIdentification());
			}
		}
	}
	
	public static boolean midRestart()
	{
		sndStopMusic();
		
		if(!BuildGdx.audio.getMusic().init()) {
			Console.Println(BuildGdx.audio.getName(Driver.Music) + " initialization failed", OSDTEXT_RED);
			return false;
		}
		
		return true;
	}
	
	public static boolean sndRestart(int nvoices, int resampler)
	{
		ambStopAll();
		BuildGdx.audio.getSound().stopAllSounds();	
		BuildGdx.audio.getSound().uninit();
		cfg.maxvoices = nvoices;
		
		Console.Println("Sound restarting...");
		
		if(BuildGdx.audio.getSound().init(SystemType.Stereo, nvoices, resampler))
		{
			BuildGdx.audio.setVolume(Driver.Sound, cfg.soundVolume);
		} 
		else
		{
			Console.Println(BuildGdx.audio.getName(Driver.Sound) + " initialization failed", OSDTEXT_RED);
			return false;
		}
		
		if(game.isCurrentScreen(gGameScreen) || game.isCurrentScreen(gDemoScreen)) 
			ambPrepare();
		
		return true;
	}
	
	public static void sndProcess()
	{
		if(cfg.noSound || !BuildGdx.audio.IsInited(Driver.Sound))
			return;
		
		for (int i = 0; i < Channel.length; i++)
		{
			if ((Channel[i].hVoice == null || !Channel[i].hVoice.isPlaying()) && Channel[i].hResource != null )
				Channel[i].hResource = null;
		}
	}
	
	public static void ambProcess()
	{
		if(cfg.noSound || !BuildGdx.audio.IsInited(Driver.Sound) || (!game.isCurrentScreen(gGameScreen) && !game.isCurrentScreen(gDemoScreen)))
			return;
		
		for ( int i = headspritestat[kStatAmbient]; i >= 0; i = nextspritestat[i] )
		{
			SPRITE pSprite = sprite[i];
			int nXSprite = pSprite.extra;
			if(nXSprite > 0 && nXSprite < kMaxXSprites && xsprite[nXSprite].state != 0 && pSprite.owner != -1)
			{
				XSPRITE pXSprite = xsprite[nXSprite];
				int dx = (pSprite.x - gPlayer[gViewIndex].pSprite.x) >> 4;
				int dy = (pSprite.y - gPlayer[gViewIndex].pSprite.y) >> 4;
				int dz = (pSprite.z - gPlayer[gViewIndex].pSprite.z) >> 8;
				
				int dist = engine.ksqrt(dx * dx + dy * dy + dz * dz);

				int volume = mulscale(pXSprite.busy, pXSprite.data4, 16);
				int radius = ClipRange(volume + (-volume * (dist - pXSprite.data1)) / (pXSprite.data2 - pXSprite.data1), 0, volume);

				ambient[pSprite.owner].volume += radius;
			}
		}

		int nAmbient = 0;
        while ( nAmbient < numambients )
        {
        	AMBIENT pAmbient = ambient[nAmbient];
        	int volume = pAmbient.volume;
        	if ( pAmbient.hVoice == null || !pAmbient.hVoice.isPlaying()) {
        		if(volume > 0) {
        			pAmbient.hVoice = BuildGdx.audio.newSound(pAmbient.pRaw, sndGetSampleRate(pAmbient.format), 8, volume);
                    if(pAmbient.hVoice != null) {
	        			pAmbient.hVoice.setGlobal(1);
	        			pAmbient.hVoice.setLooping(true, 0, pAmbient.nSize);
	        			pAmbient.hVoice.play(volume / 255.0f);
                    }
        		}
        	} else {
        		if(pAmbient.hVoice.data == pAmbient.pRaw)
        		{
        			pAmbient.hVoice.setGlobal(1);
        			pAmbient.hVoice.setVolume(volume / 255.0f);
        			pAmbient.hVoice.setPriority(volume);
        		} else pAmbient.hVoice = null;
            }
        	
        	pAmbient.volume = 0;
        	nAmbient++;
        }
	}
	
	public static void ambStopAll()
	{
		int nAmbient = 0;
		while ( nAmbient < numambients )
		{
			AMBIENT pAmbient = ambient[nAmbient];
			if ( pAmbient.hVoice != null )
				pAmbient.hVoice.dispose();
			
			pAmbient.clear();
			nAmbient++;
		}
		numambients = 0;
	}
	
	public static void ambPrepare()
	{
		ambStopAll();
		if(cfg.noSound || !BuildGdx.audio.IsInited(Driver.Sound))
			return;
		
		for ( int i = headspritestat[kStatAmbient]; i >= 0; i = nextspritestat[i] )
		{
			SPRITE pSprite = sprite[i];
			int nXSprite = pSprite.extra;
			if(nXSprite > 0 && nXSprite < kMaxXSprites)
			{
				XSPRITE pXSprite = xsprite[nXSprite];
				if ( pXSprite.data2 > pXSprite.data1 )
			    {
					int nAmbient = 0;
			        while ( nAmbient < numambients && pXSprite.data3 != ambient[nAmbient].nSoundId )
			        {
			        	nAmbient++;
			        }

			        if(nAmbient != numambients)
			        {
			        	pSprite.owner = (short) nAmbient;
			            continue;
			        }
			        
			        if ( numambients < kMaxAmbients )
			        {
			        	int soundId = pXSprite.data3;
			        	
			        	if(!sndLoadSound(soundId))
			        	{
			        		Console.Println("Missing sound #" + soundId + " used in ambient sound generator " + nAmbient, OSDTEXT_RED);
							continue; //can't load resource
			        	}

						SFX pEffect = pSFXs[soundId];

						if(pEffect.size <= 0)
							continue;

			    		int nSize = pEffect.size;
			            if ( nSize > 0)
			            {
			            	pEffect.hResource.rewind();
			            	AMBIENT pAmbient = ambient[nAmbient];
			            	pAmbient.nSize = nSize;
			            	pAmbient.nSoundId = soundId;
			            	pAmbient.nSize = nSize;
			            	pAmbient.pRaw = pEffect.hResource;
			            	pAmbient.format = pEffect.format;
			            	pSprite.owner = (short) nAmbient;
			            	numambients++;
			            }
			        }
			    }
			}
		}
	}

	public static int currTrack = -1;
	public static String currEpisode = null;
	public static String currSong = null;
	public static MusicSource currMusic = null;
	
	public static void sndPlayMenu()
	{
		String himus;
		MusicSource hisource;
		sndStopMusic();
		if(game.currentDef != null && (himus = game.currentDef.audInfo.getDigitalInfo("mainmenu")) != null)
		{
			byte[] data = BuildGdx.cache.getBytes(himus, 0);
			if(data == null) return;
			
			int sign = LittleEndian.getInt(data);
			if(sign == 1684558925 && (hisource = BuildGdx.audio.newMusic(MusicType.Midi, data)) != null) { //MThd
				currMusic = hisource;
				currMusic.play(true);
			} else if(cfg.musicType != 0 && (hisource = BuildGdx.audio.newMusic(MusicType.Digital, data)) != null) {
				currMusic = hisource;
				currMusic.play(true);
			}
		}
	}
	
	public static boolean sndPlayTrack(int nTrack)
	{
		if ( cfg.musicType != 2 )
			return false;

		if(currMusic != null && currMusic.isPlaying() && currTrack == pGameInfo.nTrackNumber
				&& (mUserFlag == UserFlag.UserMap || (mUserFlag != UserFlag.UserMap && currentEpisode.filename.equals(currEpisode))) )
			return true;

		sndStopMusic();
		if(nTrack > 0 && nTrack <= MAXUSERTRACKS && usertrack[nTrack - 1] != null && (currMusic = BuildGdx.audio.newMusic(MusicType.Digital, usertrack[nTrack - 1])) != null) {
			currTrack = nTrack;
			currEpisode = currentEpisode.filename;
			currMusic.play(true);
			return true;
		}
		
		if(nTrack >= 0 && nTrack < track.length && (currMusic = BuildGdx.audio.newMusic(MusicType.Digital, track[nTrack])) != null) {
			currTrack = nTrack;
			if(mUserFlag != UserFlag.UserMap) currEpisode = currentEpisode.filename;
			currMusic.play(true);
			return true;
		}
		
		return false;
	}
	
	public static void sndPlaySong(String name)
	{
		if ( name == null || name.length() == 0) {
			sndStopMusic();
			return;
		}

		if(currMusic != null && currMusic.isPlaying() && currSong != null && currSong.equals(pGameInfo.zLevelSong))
			return;
		
		sndStopMusic();
		byte[] pRaw = BuildGdx.cache.getBytes(name + "." + mid, 0);
		
		if(pRaw == null || pRaw.length <= 0)
			return;

		currSong = name;
		currMusic = BuildGdx.audio.newMusic(MusicType.Midi, pRaw);
		if(currMusic != null)
			currMusic.play(true);
	}

	public static void sndPlayMusic()
	{
		if(!cfg.muteMusic)
			BuildGdx.audio.setVolume(Driver.Music, cfg.musicVolume);
		else BuildGdx.audio.setVolume(Driver.Music, 0);
		
		if ( cfg.musicType == 1 && game.currentDef != null) { //music from def file
			String himus = game.currentDef.audInfo.getDigitalInfo(pGameInfo.zLevelSong);
			if(himus != null)
			{
				if(currMusic != null && currMusic.isPlaying() && currSong.equals(himus))
					return;
				
				sndStopMusic();
				if((currMusic = BuildGdx.audio.newMusic(MusicType.Digital, himus)) != null) {
					currSong = himus;
					currMusic.play(true);
					return;
				}
			} 
		}
		
		if(!sndPlayTrack(pGameInfo.nTrackNumber)) 
			sndPlaySong(pGameInfo.zLevelSong);
	}
	
	public static void sndStopMusic()
	{
		if(currMusic != null)
			currMusic.stop();
		
		currSong = null;
		currTrack = -1;
		currEpisode = null;
		currMusic = null;
	}

	private static ByteBuffer loadSample(String sampleName)
	{
		ByteBuffer buf = null;
		Resource res = BuildGdx.cache.open(sampleName, 0);
		if(res != null) {
			buf = ByteBuffer.allocateDirect(res.size());
			buf.put(res.getBytes());
			buf.rewind();
		}
		
		return buf;
	}
	
	public static void sndStartSample( String sampleName, int nVol, int nChannel )
	{
		if(cfg.noSound || !BuildGdx.audio.IsInited(Driver.Sound))
			return;

		if ( sampleName.length() == 0 )
			return;
		if(!(nChannel >= -1 && nChannel < kMaxChannels))
			game.dassert("nChannel >= -1 && nChannel < kMaxChannels");
		
		SAMPLE2D pChannel;
		if (nChannel == -1)
			pChannel = FindChannel();
		else
			pChannel = Channel[nChannel];
		
		if(pChannel == null) return;

		// if this is a fixed channel with an active voice, kill the sound
		if ( pChannel.hVoice != null )  
			sndStopSound(pChannel);
		
		if(!sampleName.contains("."))
			sampleName = sampleName + ".raw";
		
		ByteBuffer buf = loadSample(sampleName);
		if (buf == null || buf.capacity() <= 0)
		{
			System.err.println("Could not load sample " + sampleName);
			return;
		}
		
		pChannel.hResource = buf;
		pChannel.hVoice = BuildGdx.audio.newSound(buf, sndGetSampleRate(1), 8, nVol);
		if(pChannel.hVoice != null)
		{
			pChannel.hVoice.setGlobal(1);
			pChannel.loop &= ~1;
			pChannel.hVoice.play(nVol / 255.0f);
		}
	}
	
	public static void sndStartWAV( String sampleName, int nVol, int nChannel )
	{
		if(cfg.noSound || !BuildGdx.audio.IsInited(Driver.Sound))
			return;

		if ( sampleName.length() == 0 )
			return;
		if(!(nChannel >= -1 && nChannel < kMaxChannels))
			game.dassert("nChannel >= -1 && nChannel < kMaxChannels");
		SAMPLE2D pChannel;
		if (nChannel == -1)
			pChannel = FindChannel();
		else
			pChannel = Channel[nChannel];
		
		if(pChannel == null) return;

		// if this is a fixed channel with an active voice, kill the sound
		if ( pChannel.hVoice != null )  
			sndStopSound(pChannel);

		if(!BuildGdx.cache.contains(sampleName, 0)) {
			Console.Println("Could not load wav file: " + sampleName, OSDTEXT_RED);
			return;
		}
		
		byte[] buf = BuildGdx.cache.getBytes(sampleName, 0);
		ByteBuffer pRaw = BufferUtils.newByteBuffer(buf.length);
		pRaw.put(buf);
		pRaw.flip();

		pChannel.hVoice = BuildGdx.audio.newSound(pRaw, sndGetSampleRate(8), 8, nVol);
		if(pChannel.hVoice != null)
		{
			pChannel.hVoice.setGlobal(1);
			pChannel.loop &= ~1;
			pChannel.hVoice.play(nVol / 255.0f);
		}
	}
	
}
