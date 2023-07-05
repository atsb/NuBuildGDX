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
	
	public static final int AL_NONE = 0x0;
	public static final int AL_NO_ERROR = 0x0;
	public static final int AL_FALSE = 0x0;
	public static final int AL_TRUE = 0x1;
	public static final int AL_INVALID_VALUE = 0xA003;

	public static final int
	    AL_FORMAT_MONO8    = 0x1100,
	    AL_FORMAT_MONO16   = 0x1101,
	    AL_FORMAT_STEREO8  = 0x1102,
	    AL_FORMAT_STEREO16 = 0x1103;
	
	
	public static final int AL_BUFFER = 0x1009;
	public static final int AL_BUFFERS_PROCESSED = 0x1016;
	public static final int AL_BUFFERS_QUEUED = 0x1015;
	public static final int AL_SEC_OFFSET = 0x1024;
	
	public static final int AL_SOURCE_RELATIVE = 0x202;
	public static final int AL_SOURCE_STATE = 0x1010;
	public static final int AL_LOOPING = 0x1007;
	public static final int AL_PITCH = 0x1003;
	public static final int
	    AL_PLAYING = 0x1012,
	    AL_PAUSED  = 0x1013,
	    AL_STOPPED = 0x1014;
    
    public static final int
	    AL_POSITION = 0x1004,
	    AL_VELOCITY = 0x1006,
	    AL_GAIN     = 0x100A;
    
    public static final int AL_ORIENTATION = 0x100F;

	public String getName();
	
	public String getVersion();
	
	public boolean alIsEFXSupport();
	
	public boolean alIsSoftResamplerSupport();
	
	public String alGetSoftResamplerName(int num);
	
	public int alGetNumResamplers();
	
	public void dispose();
	
	public int alGetError();
	
	public int alGenBuffers();
	
	public void setSourceSoftResampler(int sourceId, int value);
	
	public void setSourceReverb(int sourceId, boolean enable, float delay);
	
	public void alDistanceModel(int modelName);
	
	public int alGetSourcei(int source, int param);
	
	public void alGetSourcefv(int source, int param, FloatBuffer values);
	
	public void alSourcei(int source, int param,  int value);
	
	public void alSourcef(int source, int param,  float value);
	
	public void alSource3i(int source, int paramName, int value1, int value2, int value3);
	
	public void alSource3f(int source, int param,  float v1,  float v2,  float v3);
	
	public void alSourceQueueBuffers(int sourceName, int bufferName);
	
	public void alSourcePlay(int source);
		
	public void alSourcePause(int source);
		
	public void alSourceStop(int source);

	public void alDeleteBuffers(IntBuffer bufferNames);

	public void alGenBuffers(IntBuffer bufferNames);

	public float alGetSourcef(int source, int param);

	public int alSourceUnqueueBuffers(int sourceName);
	
	public void alBufferData(int bufferName, int format, ByteBuffer data, int frequency);

	public void alListener(int paramName, FloatBuffer value) ;

	public void alListener3f(int paramName,  float value1,  float value2,  float value3);

	public float alGetListenerf(int paramName);
	
	public int alGetListeneri(int paramName);
	
	public void alGetListenerfv(int param, FloatBuffer values);

	public int alGenSources();

	public void alSource(int source, int param, FloatBuffer values);

	public void alDeleteSources(int sourceId);

}
