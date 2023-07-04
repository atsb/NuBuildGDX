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

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public interface ALAudio {
	
	int AL_NONE = 0x0;
	int AL_NO_ERROR = 0x0;
	int AL_FALSE = 0x0;
	int AL_TRUE = 0x1;
	int AL_INVALID_VALUE = 0xA003;

	int
	    AL_FORMAT_MONO8    = 0x1100,
	    AL_FORMAT_MONO16   = 0x1101,
	    AL_FORMAT_STEREO8  = 0x1102,
	    AL_FORMAT_STEREO16 = 0x1103;
	
	
	int AL_BUFFER = 0x1009;
	int AL_BUFFERS_PROCESSED = 0x1016;
	int AL_BUFFERS_QUEUED = 0x1015;
	int AL_SEC_OFFSET = 0x1024;
	
	int AL_SOURCE_RELATIVE = 0x202;
	int AL_SOURCE_STATE = 0x1010;
	int AL_LOOPING = 0x1007;
	int AL_PITCH = 0x1003;
	int
	    AL_PLAYING = 0x1012,
	    AL_PAUSED  = 0x1013,
	    AL_STOPPED = 0x1014;
    
    int
	    AL_POSITION = 0x1004,
	    AL_VELOCITY = 0x1006,
	    AL_GAIN     = 0x100A;
    
    int AL_ORIENTATION = 0x100F;

	String getName();
	
	String getVersion();
	
	boolean alIsEFXSupport();
	
	boolean alIsSoftResamplerSupport();
	
	String alGetSoftResamplerName(int num);
	
	int alGetNumResamplers();
	
	void dispose();
	
	int alGetError();
	
	int alGenBuffers();
	
	void setSourceSoftResampler(int sourceId, int value);
	
	void setSourceReverb(int sourceId, boolean enable, float delay);
	
	void alDistanceModel(int modelName);
	
	int alGetSourcei(int source, int param);
	
	void alGetSourcefv(int source, int param, FloatBuffer values);
	
	void alSourcei(int source, int param, int value);
	
	void alSourcef(int source, int param, float value);
	
	void alSource3i(int source, int paramName, int value1, int value2, int value3);
	
	void alSource3f(int source, int param, float v1, float v2, float v3);
	
	void alSourceQueueBuffers(int sourceName, int bufferName);
	
	void alSourcePlay(int source);
		
	void alSourcePause(int source);
		
	void alSourceStop(int source);

	void alDeleteBuffers(IntBuffer bufferNames);

	void alGenBuffers(IntBuffer bufferNames);

	float alGetSourcef(int source, int param);

	int alSourceUnqueueBuffers(int sourceName);
	
	void alBufferData(int bufferName, int format, ByteBuffer data, int frequency);

	void alListener(int paramName, FloatBuffer value) ;

	void alListener3f(int paramName, float value1, float value2, float value3);

	float alGetListenerf(int paramName);
	
	int alGetListeneri(int paramName);
	
	void alGetListenerfv(int param, FloatBuffer values);

	int alGenSources();

	void alSource(int source, int param, FloatBuffer values);

	void alDeleteSources(int sourceId);

}
