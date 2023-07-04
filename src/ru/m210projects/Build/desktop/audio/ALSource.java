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

package ru.m210projects.Build.desktop.audio;

import static ru.m210projects.Build.OnSceenDisplay.Console.OSDTEXT_RED;
import static ru.m210projects.Build.desktop.audio.ALAudio.AL_BUFFER;
import static ru.m210projects.Build.desktop.audio.ALAudio.AL_FALSE;
import static ru.m210projects.Build.desktop.audio.ALAudio.AL_GAIN;
import static ru.m210projects.Build.desktop.audio.ALAudio.AL_LOOPING;
import static ru.m210projects.Build.desktop.audio.ALAudio.AL_NO_ERROR;
import static ru.m210projects.Build.desktop.audio.ALAudio.AL_PAUSED;
import static ru.m210projects.Build.desktop.audio.ALAudio.AL_PITCH;
import static ru.m210projects.Build.desktop.audio.ALAudio.AL_PLAYING;
import static ru.m210projects.Build.desktop.audio.ALAudio.AL_POSITION;
import static ru.m210projects.Build.desktop.audio.ALAudio.AL_SOURCE_RELATIVE;
import static ru.m210projects.Build.desktop.audio.ALAudio.AL_SOURCE_STATE;
import static ru.m210projects.Build.desktop.audio.ALAudio.AL_TRUE;

import ru.m210projects.Build.Audio.Source;
import ru.m210projects.Build.Audio.SourceCallback;
import ru.m210projects.Build.OnSceenDisplay.Console;

public class ALSource extends Source {

	private final ALSoundDrv drv;
	private final ALAudio al;
	public ALSource(ALSoundDrv drv, int bufferId, int sourceId) {
		super(bufferId, sourceId, 0);
		this.drv = drv;
		this.al = drv.getALAudio();
	}

	@Override
	public int dispose() {
		if(!drv.isInited()) return -1;
		drv.sourceManager.stopSound(this);
		return sourceId;
	}

	@Override
	public boolean isLooping() {
		if(!drv.isInited()) return false;
		return al.alGetSourcei(sourceId, AL_LOOPING) == AL_TRUE;
	}

	@Override
	public boolean isPlaying() {
		if(!drv.isInited()) return false;
		return al.alGetSourcei(sourceId, AL_SOURCE_STATE) == AL_PLAYING;
	}

	@Override
	public void setPosition(float x, float y, float z) {
		if(!drv.isInited()) return;
		al.alSource3f(sourceId, AL_POSITION, x, y, z);
		
		int error = al.alGetError();
		if(error != AL_NO_ERROR) 
			Console.Println("OpenAL Error setPosition " + error + ", values: ["+ x + ", " + y + ", " + z + "]", OSDTEXT_RED);
	}

	@Override
	public void setVolume(float volume) {
		if(!drv.isInited()) return;
		volume = Math.min(Math.max(volume, 0.0f), 1.0f);
		volume *= drv.getVolume();
		al.alSourcef(sourceId, AL_GAIN, volume);
		
		int error = al.alGetError();
		if(error != AL_NO_ERROR) 
			Console.Println("OpenAL Error setVolume " + error + ", value is " + volume, OSDTEXT_RED);
	}
	
	@Override
	public float getVolume() {
		if(!drv.isInited()) return 0;
		return al.alGetSourcef(sourceId, AL_GAIN);
	}
	
	@Override
	public float getPitch() {
		if(!drv.isInited()) return 0;
		return al.alGetSourcef(sourceId, AL_PITCH);
	}

	@Override
	public void setPitch(float pitch) {
		if(!drv.isInited()) return;
		pitch = Math.min(Math.max(pitch, 0.0f), 2.0f);
		al.alSourcef(sourceId, AL_PITCH, pitch);
		
		int error = al.alGetError();
		if(error != AL_NO_ERROR) 
			Console.Println("OpenAL Error setPitch " + error + ", value is " + pitch, OSDTEXT_RED);
	}

	@Override
	public void setGlobal(int num) {
		if(!drv.isInited()) return;
		al.alSourcei(sourceId, AL_SOURCE_RELATIVE,  num);

		int error = al.alGetError();
		if(error != AL_NO_ERROR) 
			Console.Println("OpenAL Error setGlobal " + error + ", value is " + num, OSDTEXT_RED);
	}
	
	@Override
	public boolean isGlobal() {
		if(!drv.isInited()) return false;
		return al.alGetSourcei(sourceId, AL_SOURCE_RELATIVE) == 1;
	}

	@Override
	public void setPriority(int priority) {
		this.priority = priority;
	}

	@Override
	public boolean isActive() {
		if(!drv.isInited()) return false;
		return (isPlaying() || al.alGetSourcei(sourceId, AL_SOURCE_STATE) == AL_PAUSED) && priority != 0 && !free;
	}

	@Override
	public void setLooping(boolean loop, int loopstart, int loopend) {
		if(!drv.isInited()) return;
		if(!loop)
		{
			al.alSourcei(sourceId, AL_LOOPING, AL_FALSE);
			loopInfo.clear();
		} else {
			int start = 0, end = data.capacity();
			if(loopstart >= 0 && loopstart < data.capacity()) 
				start = loopstart;
			if(loopend < data.capacity()) 
				end = loopend;
			
			int bufferID = drv.buffers.get(bufferId);
			if(start > 0) {
				al.alSourcei(sourceId, AL_BUFFER, 0);
				al.alSourcei(sourceId, AL_LOOPING, AL_FALSE);
				al.alSourceQueueBuffers(sourceId, bufferID);
				drv.loopedSource.add(this);
				loopInfo.set(data, start, end, format, rate);
			} else {
				if(end > 0) data.limit(end);
				al.alSourcei(sourceId, AL_LOOPING, AL_TRUE);
				al.alSourcei(sourceId, AL_BUFFER,   bufferID );
			}
		}
		
		int error = al.alGetError();
		if(error != AL_NO_ERROR) 
			Console.Println("OpenAL Error setLooping " + error, OSDTEXT_RED);
	}

	@Override
	public void play(float volume) {
		if(!drv.isInited()) return;
		setVolume(volume);
		al.alSourcePlay(sourceId);
		
		int error = al.alGetError();
		if(error != AL_NO_ERROR) 
			Console.Println("OpenAL Error play " + error, OSDTEXT_RED);
	}

	@Override
	public void stop() {
		if(!drv.isInited()) return;
		al.alSourceStop(sourceId);
	}

	@Override
	public void pause() {
		if(!drv.isInited()) return;
		al.alSourcePause(sourceId);
	}

	@Override
	public void resume() {
		if(!drv.isInited()) return;
		if(al.alGetSourcei(sourceId, AL_SOURCE_STATE) == AL_PAUSED)
			al.alSourcePlay(sourceId);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> void setCallback(SourceCallback<T> callback, T num) {
		this.callback = (SourceCallback<Object>) callback;
		this.channel = num;
	}
}
