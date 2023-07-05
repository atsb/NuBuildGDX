package ru.m210projects.Build.desktop.audio.nopenal;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;

import com.sun.jna.Library;
import com.sun.jna.Pointer;

public interface SharedLibraryAL extends Library
{
	public int alGetInteger(int paramName);

    public float alGetFloat(int paramName);

    public double alGetDouble(int paramName);

    public void alGetBooleanv(int paramName, ByteBuffer dest);

    public void alGetIntegerv(int paramName, IntBuffer dest);

    public void alGetFloatv(int paramName, FloatBuffer dest);

    public void alGetDoublev(int paramName, DoubleBuffer dest);

    public String alGetString(int paramName);

    public void alDistanceModel(int modelName);

    public void alDopplerFactor( float dopplerFactor);

    public void alDopplerVelocity( float dopplerVelocity);

    public void alListenerf(int paramName, float value);

    public void alListeneri(int paramName, int values);

    public void alListener3f(int paramName,  float value1,  float value2,  float value3);

    public void alListenerfv(int paramName, FloatBuffer values);

    public void alGetListenerf(int paramName, FloatBuffer value);

    public float alGetListenerf(int paramName);

    public void alGetListeneri(int paramName, IntBuffer value);

    public int alGetListeneri(int paramName);

    public void alGetListener3f(int paramName, FloatBuffer value1, FloatBuffer value2, FloatBuffer value3);

    public void alGetListenerfv(int paramName, FloatBuffer values);

    public void alGenSources(int size, IntBuffer srcNames);

    public int alGenSources();

    public void alDeleteSources(int size, IntBuffer sources);

    public void alDeleteSources(int source);

    public boolean alIsSource(int sourceName);

    public void alSourcef(int source, int param,  float value);

    public void alSource3f(int source, int param,  float v1,  float v2,  float v3);

    public void alSourcefv(int source, int param, FloatBuffer values);

    public void alSourcei(int source, int param,  int value);

    public void alGetSourcef(int source, int param, FloatBuffer value);

    public float alGetSourcef(int source, int param);
        
    public void alGetSource3f(int source, int param, FloatBuffer v1, FloatBuffer v2, FloatBuffer v3);

    public void alGetSourcefv(int source, int param, FloatBuffer values);

    public void alGetSourcei(int source, int param, IntBuffer value);

    public int alGetSourcei(int source, int param);

    public void alGetSourceiv(int source, int param, IntBuffer values);

    public void alSourceQueueBuffers(int sourceName, int size, IntBuffer bufferNames);

    public void alSourceQueueBuffers(int sourceName, int bufferName);

    public void alSourceUnqueueBuffers(int sourceName, int size, IntBuffer bufferNames);

    public int alSourceUnqueueBuffers(int sourceName);

    public void alSourcePlay(int source);
	
    public void alSourcePause(int source);
	
    public void alSourceStop(int source);
    
    public void alSourceRewind(int source);
	
    public void alSourcePlayv(int size, IntBuffer sources);

    public void alSourcePausev(int size, IntBuffer sources);

    public void alSourceStopv(int size, IntBuffer sources);

    public void alSourceRewindv(int size, IntBuffer sources);

    public void alGenBuffers(int size, IntBuffer bufferNames);

    public int alGenBuffers();

    public void alDeleteBuffers(int size, IntBuffer bufferNames);

    public void alDeleteBuffers(int bufferName);

    public boolean alIsBuffer(int bufferName);

    public void alGetBufferf(int bufferName, int paramName, FloatBuffer value);

    public float alGetBufferf(int bufferName, int paramName);

    public void alGetBufferi(int bufferName, int paramName, IntBuffer value);

    public int alGetBufferi(int bufferName, int paramName);

    public void alBufferData(int bufferName, int format, ByteBuffer data, int frequency);

    public void alBufferData(int bufferName, int format, ShortBuffer data, int frequency);

    public void alBufferData(int bufferName, int format, IntBuffer data, int frequency);

    public void alBufferData(int bufferName, int format, FloatBuffer data, int frequency);

    public int alGetEnumValue(String enumName);;    

    public long alGetProcAddress(String funcName);
       
    public boolean alIsExtensionPresent(String extName);
       
    public void alGetIntegerv(int paramName, int[] dest);
    	
    public void alGetFloatv(int paramName, float[] dest);
    	
    public void alGetDoublev(int paramName, double[] dest);
    	
    public void alListenerfv(int paramName, float[] values);
    	
    public void alGetListenerf(int paramName, float[] value);
    	
    public void alGetListeneri(int paramName, int[] value);
    	
    public void alGetListener3f(int paramName, float[] value1, float[] value2, float[] value3);
    	
    public void alGetListenerfv(int paramName, float[] values);
    	  
    public void alGenSources(int size, int[] srcNames);
    	 
    public void alDeleteSources(int size, int[] sources);
  
    public void alSourcefv(int source, int param, float[] values);
    
    public void alGetSourcef(int source, int param, float[] value);
    
    public void alGetSource3f(int source, int param, float[] v1, float[] v2, float[] v3);
    
    public void alGetSourcefv(int source, int param, float[] values);
  
    public void alGetSourcei(int source, int param, int[] value);
  
    public void alGetSourceiv(int source, int param, int[] values);

    public void alSourceQueueBuffers(int sourceName, int size, int[] bufferNames);

    public void alSourceUnqueueBuffers(int sourceName, int size, int[] bufferNames);

    public void alSourcePlayv(int size, int[] sources);

    public void alSourcePausev(int size, int[] sources);

    public void alSourceStopv(int size, int[] sources);

    public void alSourceRewindv(int size, int[] sources);

    public void alGenBuffers(int size, int[] bufferNames);

    public void alDeleteBuffers(int size, int[] bufferNames);

    public void alGetBufferf(int bufferName, int paramName, float[] value);

    public void alGetBufferi(int bufferName, int paramName, int[] value);

    public void alBufferData(int bufferName, int format, short[] data, int frequency);

    public void alBufferData(int bufferName, int format, int[] data, int frequency);

    public void alBufferData(int bufferName, int format, float[] data, int frequency);

	
	public String alGetStringiSOFT(int pname, int index);
	
	public void alListener3i(int paramName, float value1, float value2, float value3);
	
	public void alGetListeneriv(int param, IntBuffer values);
	
	public void alSource3i(int source, int paramName, int value1, int value2, int value3);
	
	public void alListeneriv(int listener, IntBuffer value);
	
	public void alSourceiv(int source, int paramName, IntBuffer value);
	
	public void alBufferf(int buffer, int paramName, float value);
	
	public void alBuffer3f(int buffer, int paramName, float value1, float value2, float value3);
	
	public void alBufferfv(int buffer, int paramName, FloatBuffer value);
	
	public void alBufferi(int buffer, int paramName, int value);
	
	public void alBuffer3i(int buffer, int paramName, int value1, int value2, int value3);
	
	public void alBufferiv(int buffer, int paramName, IntBuffer value);
	
	public void alGetBufferiv(int buffer, int param, IntBuffer values);
	
	public void alGetBufferfv(int buffer, int param, FloatBuffer values);
	
	public void alSpeedOfSound(float value);
	
	public void alGetListeneriv(int param, int[] values);
	
	public void alListeneriv(int listener, int[] value);
	
	public void alSourceiv(int source, int paramName, int[] value);
	
	public void alBufferfv(int buffer, int paramName, float[] value);
	
	public void alBufferiv(int buffer, int paramName, int[] value);
	
	public void alGetBufferiv(int buffer, int param, int[] values);
	
	public void alGetBufferfv(int buffer, int param, float[] values);
	
	public int alGetError();
	
	public void alEnable(int target);
	
	public void alDisable(int target);
	
	public boolean alIsEnabled(int target);

	public boolean alGetBoolean(int paramName);
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public boolean alcCaptureCloseDevice(ALCdevice device);
	
	public ALCdevice alcCaptureOpenDevice(String deviceName, int frequency, int format, int samples);
	
	public void alcCaptureSamples(ALCdevice device, ByteBuffer buffer, int samples);
	
	public void alcCaptureSamples(ALCdevice device, ShortBuffer buffer, int samples);
	
	public void alcCaptureSamples(ALCdevice device, IntBuffer buffer, int samples);
	
	public void alcCaptureSamples(ALCdevice device, FloatBuffer buffer, int samples);
	
	public void alcCaptureSamples(ALCdevice device, short[] buffer, int samples);
	
	public void alcCaptureSamples(ALCdevice device, int[] buffer, int samples);
	
	public void alcCaptureSamples(ALCdevice device, float[] buffer, int samples);
	
	public void alBufferData(int buffer, int format, ByteBuffer data, int length, int freq);
	
	public void alBufferData(int buffer, int format, ShortBuffer data, int length, int freq);
	
	public void alBufferData(int buffer, int format, IntBuffer data, int length, int freq);
	
	public void alBufferData(int buffer, int format, FloatBuffer data, int length, int freq);
	
	public void alBufferData(int buffer, int format, short[] data, int length, int freq);
	
	public void alBufferData(int buffer, int format, int[] data, int length, int freq);
	
	public void alBufferData(int buffer, int format, float[] data, int length, int freq);
	
	public void alcCaptureStart(ALCdevice device);
	
	public void alcCaptureStop(ALCdevice device);
	
	public boolean alcCloseDevice(ALCdevice deviceHandle);
	
	public ALCcontext alcCreateContext(ALCdevice deviceHandle, int[] attrList);
	
	public ALCcontext alcCreateContext(ALCdevice deviceHandle, IntBuffer attrList);
	
	public void alcDestroyContext(ALCcontext context);
	
	public ALCdevice alcGetContextsDevice(ALCcontext context);
	
	public ALCcontext alcGetCurrentContext();
	
	public int alcGetEnumValue(ALCdevice deviceHandle, String enumName);
	
	public int alcGetError(ALCdevice deviceHandle);
	
	public void alcProcessContext(ALCcontext context);
	
	public void alcSuspendContext(ALCcontext context);
	
	public void alcGetIntegerv(ALCdevice deviceHandle, int token, int sizeof, IntBuffer dest);
	
	public void alcGetIntegerv(ALCdevice deviceHandle, int token, int sizeof, int[] dest);
	
	public Pointer alcGetProcAddress(ALCdevice deviceHandle, String funcName);
	
	public String alcGetString(ALCdevice deviceHandle, int token);
	
	public ALCcontext alcGetThreadContext();
	
	public boolean alcIsExtensionPresent(ALCdevice deviceHandle, String extName);
	
	public boolean alcIsRenderFormatSupportedSOFT(ALCdevice deviceHandle, int frequency, int channels, int type);
	
	public ALCdevice alcLoopbackOpenDeviceSOFT(String devicename);
	
	public boolean alcMakeContextCurrent(ALCcontext context);
	
	public ALCdevice alcOpenDevice(String deviceSpecifier);
	
	public void alcRenderSamplesSOFT(ALCdevice device, ByteBuffer buffer, int samples);
	
	public void alcRenderSamplesSOFT(ALCdevice device, ShortBuffer buffer, int samples);
	
	public void alcRenderSamplesSOFT(ALCdevice device, IntBuffer buffer, int samples);
	
	public void alcRenderSamplesSOFT(ALCdevice device, FloatBuffer buffer, int samples);
	
	public void alcRenderSamplesSOFT(ALCdevice device, short[] buffer, int samples);
	
	public void alcRenderSamplesSOFT(ALCdevice device, int[] buffer, int samples);
	
	public void alcRenderSamplesSOFT(ALCdevice device, float[] buffer, int samples);
	
	public boolean alcSetThreadContext(ALCcontext context); //XXX false
	
	public void alcSuspendContext();
	
	public boolean alcResetDeviceSOFT(ALCdevice device, IntBuffer attrList);
	
	public boolean alcResetDeviceSOFT(ALCdevice device, int[] attrList);
	
	public String alcGetStringiSOFT(ALCdevice device, int paramName, int index);
	
	public void alcDevicePauseSOFT(ALCdevice device);
	
	public void alcDeviceResumeSOFT(ALCdevice device);
	
	public void alProcessUpdatesSOFT();
	
	public void alDeferUpdatesSOFT();
	
	public void alSourcedSOFT(int source, int param, double value);
	
	public void alSource3dSOFT(int source, int param, double value1, double value2, double value3);
	
	public void alSourcedvSOFT(int source, int param, DoubleBuffer value);
	
	public void alGetSourcedSOFT(int source, int param, DoubleBuffer value);
	
	public void alGetSource3dSOFT(int source, int param, DoubleBuffer value1, DoubleBuffer value2, DoubleBuffer value3);
	
	public void alGetSourcedvSOFT(int source, int param, DoubleBuffer values);
	
	public void alSourcei64SOFT(int source, int param, long value);
	
	public void alSource3i64SOFT(int source, int param, long value1, long value2, long value3);
	
	public void alSourcei64vSOFT(int source, int param, LongBuffer values);
	
	public void alGetSourcei64SOFT(int source, int param, LongBuffer value);
	
	public void alGetSource3i64SOFT(int source, int param, LongBuffer value1, LongBuffer value2, LongBuffer value3);
	
	public void alGetSourcei64vSOFT(int source, int param, LongBuffer values);
	
	public void alSourcedvSOFT(int source, int param, double[] value);
	
	public void alGetSourcedSOFT(int source, int param, double[] value);
	
	public void alGetSource3dSOFT(int source, int param, double[] value1, double[] value2, double[] value3);

	public void alGetSourcedvSOFT(int source, int param, double[] values);
	
	public void alSourcei64vSOFT(int source, int param, long[] values);

	public void alGetSourcei64SOFT(int source, int param, long[] value);
	
	public void alGetSource3i64SOFT(int source, int param, long[] value1, long[] value2, long[] value3);

	public void alGetSourcei64vSOFT(int source, int param, long[] values);
	
	public void alGenEffects(int size, IntBuffer effects);
	
	public void alDeleteEffects(int size, IntBuffer effects);
	
	public boolean alIsEffect(int effect);
	
	public void alEffecti(int effect, int param, int value);
	
	public void alEffectiv(int effect, int param, IntBuffer values);
	
	public void alEffectf(int effect, int param, float value);
	
	public void alEffectfv(int effect, int param, FloatBuffer values);
	
	public void alGetEffecti(int effect, int param, IntBuffer value);
	
	public void alGetEffectiv(int effect, int param, IntBuffer values);
	
	public void alGetEffectf(int effect, int param, FloatBuffer value);
	
	public void alGetEffectfv(int effect, int param, FloatBuffer values);
	
	public void alGenFilters(int size, IntBuffer filters);
	
	public void alDeleteFilters(int size, IntBuffer filters);
	
	public boolean alIsFilter(int filter);
	
	public void alFilteri(int filter, int param, int value);
	
	public void alFilteriv(int filter, int param, IntBuffer values);
	
	public void alFilterf(int filter, int param, float value);
	
	public void alFilterfv(int filter, int param, FloatBuffer values);
	
	public void alGetFilteri(int filter, int param, IntBuffer value);
	
	public void alGetFilteriv(int filter, int param, IntBuffer values);
	
	public void alGetFilterf(int filter, int param, FloatBuffer value);
	
	public void alGetFilterfv(int filter, int param, FloatBuffer values);
	
	public void alGenAuxiliaryEffectSlots(int size, IntBuffer effectSlots);
	
	public void alDeleteAuxiliaryEffectSlots(int size, IntBuffer effectSlots);
	
	public boolean alIsAuxiliaryEffectSlot(int effectSlot);
	
	public void alAuxiliaryEffectSloti(int effectSlot, int param, int value);
	
	public void alAuxiliaryEffectSlotiv(int effectSlot, int param, IntBuffer values);
	
	public void alAuxiliaryEffectSlotf(int effectSlot, int param, float value);
	
	public void alAuxiliaryEffectSlotfv(int effectSlot, int param, FloatBuffer values);
	
	public void alGetAuxiliaryEffectSloti(int effectSlot, int param, IntBuffer value);
	
	public void alGetAuxiliaryEffectSlotiv(int effectSlot, int param, IntBuffer values);
	
	public void alGetAuxiliaryEffectSlotf(int effectSlot, int param, FloatBuffer value);
	
	public void alGetAuxiliaryEffectSlotfv(int effectSlot, int param, FloatBuffer values);
	
	public void alGenEffects(int size, int[] effects);
	
	public void alDeleteEffects(int size, int[] effects);
	
	public void alEffectiv(int effect, int param, int[] values);
	
	public void alEffectfv(int effect, int param, float[] values);
	
	public void alGetEffecti(int effect, int param, int[] value);
	
	public void alGetEffectiv(int effect, int param, int[] values);
	
	public void alGetEffectf(int effect, int param, float[] value);
	
	public void alGetEffectfv(int effect, int param, float[] values);
	
	public void alGenFilters(int size, int[] filters);
	
	public void alDeleteFilters(int size, int[] filters);
	
	public void alFilteriv(int filter, int param, int[] values);
	
	public void alFilterfv(int filter, int param, float[] values);
	
	public void alGetFilteri(int filter, int param, int[] value);
    
	public void alGetFilteriv(int filter, int param, int[] values);
	
	public void alGetFilterf(int filter, int param, float[] value);
	
	public void alGetFilterfv(int filter, int param, float[] values);
	
	public void alGenAuxiliaryEffectSlots(int size, int[] effectSlots);
	
	public void alDeleteAuxiliaryEffectSlots(int size, int[] effectSlots);
	
	public void alAuxiliaryEffectSlotiv(int effectSlot, int param, int[] values);
	
	public void alAuxiliaryEffectSlotfv(int effectSlot, int param, float[] values);
	
	public void alGetAuxiliaryEffectSloti(int effectSlot, int param, int[] value);
	
	public void alGetAuxiliaryEffectSlotiv(int effectSlot, int param, int[] values);
	
	public void alGetAuxiliaryEffectSlotf(int effectSlot, int param, float[] value);
	
	public void alGetAuxiliaryEffectSlotfv(int effectSlot, int param, float[] values);
}
