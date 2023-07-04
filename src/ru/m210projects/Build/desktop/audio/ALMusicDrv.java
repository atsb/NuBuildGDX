//This file is part of BuildGDX.
//Copyright (C) 2017-2018  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
//BuildGDX is free software: you can redistribute it and/or modify
//it under the terms of the GNU General Public License as published by
//the Free Software Foundation, either version 3 of the License, or
//(at your option) any later version.
//
//BuildGDX is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU General Public License for more details.
//
//You should have received a copy of the GNU General Public License
//along with BuildGDX.  If not, see <http://www.gnu.org/licenses/>.
// Music part of this file has been modified from 
// Nathan Sweet's LibGDX OpenALMusic by Alexander Makarov-[M210]

package ru.m210projects.Build.desktop.audio;

import static ru.m210projects.Build.OnSceenDisplay.Console.OSDTEXT_RED;
import static ru.m210projects.Build.desktop.audio.ALAudio.AL_BUFFERS_PROCESSED;
import static ru.m210projects.Build.desktop.audio.ALAudio.AL_BUFFERS_QUEUED;
import static ru.m210projects.Build.desktop.audio.ALAudio.AL_FALSE;
import static ru.m210projects.Build.desktop.audio.ALAudio.AL_FORMAT_MONO16;
import static ru.m210projects.Build.desktop.audio.ALAudio.AL_FORMAT_STEREO16;
import static ru.m210projects.Build.desktop.audio.ALAudio.AL_GAIN;
import static ru.m210projects.Build.desktop.audio.ALAudio.AL_INVALID_VALUE;
import static ru.m210projects.Build.desktop.audio.ALAudio.AL_LOOPING;
import static ru.m210projects.Build.desktop.audio.ALAudio.AL_NO_ERROR;
import static ru.m210projects.Build.desktop.audio.ALAudio.AL_PLAYING;
import static ru.m210projects.Build.desktop.audio.ALAudio.AL_SEC_OFFSET;
import static ru.m210projects.Build.desktop.audio.ALAudio.AL_SOURCE_STATE;

import java.io.ByteArrayInputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import com.badlogic.gdx.backends.lwjgl.audio.OggInputStream;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.StreamUtils;

import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.Audio.Music;
import ru.m210projects.Build.Audio.MusicSource;
import ru.m210projects.Build.Audio.Source;
import ru.m210projects.Build.FileHandle.Resource;
import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Build.desktop.audio.ALSoundDrv.SourceManager;

public class ALMusicDrv implements Music {

	private MusicSource music;
	private float musicVolume = 1.0f;
	private boolean inited;
	
	protected IntBuffer musicBuffers;
	protected final int musicBufferCount = 3;
	
	private final ALSoundDrv drv;
	private final ALAudio al;
	public ALMusicDrv(ALSoundDrv drv) {
		this.drv = drv;
		this.al = drv.getALAudio();
	}

	@Override
	public MusicSource newMusic(byte[] data) {
		if(drv.noDevice || data == null) return null;
		if(music != null) {
			if(music.isPlaying())
				music.stop();
			music = null;
		}
		
		try {
			music = new ALMusicSource(new Ogg.Music(drv, musicBuffers, data));
		} catch (Throwable e) {
			e.printStackTrace();
			Console.Println("Can't load ogg file", OSDTEXT_RED);
			return null;
		}
		
		setVolume(musicVolume);
		return music;
	}

	@Override
	public MusicSource newMusic(String name) {
		if(drv.noDevice || !inited) return null;
		
		Resource res = BuildGdx.cache.open(name, 0);
		if(res == null) {
			Console.Println("OpenAL Music: Unable to load " + name, OSDTEXT_RED);
			return null;
		}
		
		byte[] data = res.getBytes();
		res.close();
		return newMusic(data);
	}

	@Override
	public String getName() {
		return "OpenAL Music";
	}
	
	@Override
	public void dispose() {
		if(inited)
			al.alDeleteBuffers(musicBuffers); 
	}

	@Override
	public synchronized boolean init() {
		if(!drv.isInited()) return false;
		inited = false;
		musicBuffers = BufferUtils.newIntBuffer(musicBufferCount);
		al.alGenBuffers(musicBuffers);
		
		if (al.alGetError() != AL_NO_ERROR) {
			Console.Println("OpenAL Music: Unabe to allocate audio buffers.", OSDTEXT_RED);
			return false;
		}
		
		inited = true;
		return true;
	}

	@Override
	public boolean isInited() {
		return drv.isInited() && inited;
	}

	@Override
	public void update() {
		if(music != null)
			music.update();
	}

	@Override
	public void setVolume(float volume) {
		musicVolume = volume;
		if(music != null)
			((ALMusicSource) music).setVolume(volume);
	}
}





/** @author Nathan Sweet */
abstract class OpenALMusic {
	
	static private final int bufferSize = 4096 * 10;
	static private final int bytesPerSample = 2;
	static private final byte[] tempBytes = new byte[bufferSize];
	static private final ByteBuffer tempBuffer = BufferUtils.newByteBuffer(bufferSize);

	private final SourceManager sourceManager;
	private Source source = null;
	private int format, sampleRate;
	private boolean isLooping, isPlaying;
	private float renderedSeconds, secondsPerBuffer;
	protected byte[] data;
	private float musicVolume;
	private final IntBuffer musicBuffers;
	private final ALSoundDrv drv;
	private final ALAudio al;
	
	public OpenALMusic (ALSoundDrv drv, IntBuffer ALbuffers, byte[] data) {
		this.drv = drv;
		this.al = drv.getALAudio();
		this.sourceManager = drv.sourceManager;
		this.data = data;
		this.musicBuffers = ALbuffers;
	}

	protected void setup (int channels, int sampleRate) {
		this.format = channels > 1 ? AL_FORMAT_STEREO16 : AL_FORMAT_MONO16;
		this.sampleRate = sampleRate;
		secondsPerBuffer = (float)bufferSize / bytesPerSample / channels / sampleRate;
	}

	public void play() {
		if (source == null) {
			source = sourceManager.obtainSource(Integer.MAX_VALUE);
			if (source == null) return;
			al.alSourcei(source.sourceId, AL_LOOPING, AL_FALSE);
			setVolume(musicVolume);
			source.flags |= Source.Locked;
			for (int i = 0; i < musicBuffers.capacity(); i++) {
				int bufferID = musicBuffers.get(i);
				if (!fill(bufferID)) break;
				renderedSeconds = 0;
				al.alSourceQueueBuffers(source.sourceId, bufferID);
			}
			if (al.alGetError() != AL_NO_ERROR) {
				stop();
				return;
			}
		}
		al.alSourcePlay(source.sourceId);
		isPlaying = true;
	}

	public void stop() {
		if (source == null) return;
		reset();
		source.flags &= ~Source.Locked;
		if(drv.isInited())
			sourceManager.stopSound(source);
		else if(source.flags != Source.Locked) 
			sourceManager.freeSource(source);
		source = null;
		renderedSeconds = 0;
		isPlaying = false;
	}

	public void pause() {
		if (source != null && drv.isInited()) al.alSourcePause(source.sourceId);
		isPlaying = false;
	}

	public boolean isPlaying() {
		if (source == null) return false;
		return isPlaying;
	}

	public void setLooping(boolean isLooping) {
		this.isLooping = isLooping;
	}

	public boolean isLooping() {
		return isLooping;
	}

	public void setVolume(float volume) {
		this.musicVolume = volume;
		if (source != null && drv.isInited()) 
			al.alSourcef(source.sourceId, AL_GAIN, volume);
	}

	public float getVolume() {
		return this.musicVolume;
	}

	public float getPosition() {
		if (source == null || !drv.isInited()) return 0;
		return renderedSeconds + al.alGetSourcef(source.sourceId, AL_SEC_OFFSET);
	}
	
	public int getChannels() {
		return format == AL_FORMAT_STEREO16 ? 2 : 1;
	}

	public int getRate() {
		return sampleRate;
	}

	public void update() {
		if (source == null || !drv.isInited()) return;
		boolean end = false;
		int buffers = al.alGetSourcei(source.sourceId, AL_BUFFERS_PROCESSED);
		while (buffers-- > 0) {
			int bufferID = al.alSourceUnqueueBuffers(source.sourceId);
			if (bufferID == AL_INVALID_VALUE) break;
			renderedSeconds += secondsPerBuffer;
			if (end) continue;
			if (fill(bufferID)) 
				al.alSourceQueueBuffers(source.sourceId, bufferID);
			else end = true;
		}
		if (end && al.alGetSourcei(source.sourceId, AL_BUFFERS_QUEUED) == 0) {
			stop();
		}
		
		// A buffer underflow will cause the source to stop.
		if (isPlaying && al.alGetSourcei(source.sourceId, AL_SOURCE_STATE) != AL_PLAYING) al.alSourcePlay(source.sourceId);
	}

	private boolean fill (int bufferID) {
		tempBuffer.clear();
		int length = read(tempBytes);
		if (length <= 0) {
			if (isLooping) {
				reset();
				renderedSeconds = 0;
				length = read(tempBytes);
				if (length <= 0) return false;
			} else
				return false;
		}
		tempBuffer.put(tempBytes, 0, length).flip();
		al.alBufferData(bufferID, format, tempBuffer, sampleRate);
		return true;
	}
	
	public abstract int read(byte[] buffer);
	
	public abstract void reset ();
}

class Ogg {
	static public class Music extends OpenALMusic {
		private OggInputStream input;
		public Music(ALSoundDrv drv, IntBuffer ALbuffers, byte[] data) {
			super(drv, ALbuffers, data);
			input = new OggInputStream(new ByteArrayInputStream(data, 0, data.length));
			setup(input.getChannels(), input.getSampleRate());
		}

		public int read(byte[] buffer) {
			if (input == null) {
				input = new OggInputStream(new ByteArrayInputStream(data, 0, data.length));
				setup(input.getChannels(), input.getSampleRate());
			}
			return input.read(buffer);
		}

		public void reset() {
			StreamUtils.closeQuietly(input);
			input = null;
		}
	}
}
