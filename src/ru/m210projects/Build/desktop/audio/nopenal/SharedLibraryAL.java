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
	int alGetInteger(int paramName);

    float alGetFloat(int paramName);

    double alGetDouble(int paramName);

    void alGetBooleanv(int paramName, ByteBuffer dest);

    void alGetIntegerv(int paramName, IntBuffer dest);

    void alGetFloatv(int paramName, FloatBuffer dest);

    void alGetDoublev(int paramName, DoubleBuffer dest);

    String alGetString(int paramName);

    void alDistanceModel(int modelName);

    void alDopplerFactor(float dopplerFactor);

    void alDopplerVelocity(float dopplerVelocity);

    void alListenerf(int paramName, float value);

    void alListeneri(int paramName, int values);

    void alListener3f(int paramName, float value1, float value2, float value3);

    void alListenerfv(int paramName, FloatBuffer values);

    void alGetListenerf(int paramName, FloatBuffer value);

    float alGetListenerf(int paramName);

    void alGetListeneri(int paramName, IntBuffer value);

    int alGetListeneri(int paramName);

    void alGetListener3f(int paramName, FloatBuffer value1, FloatBuffer value2, FloatBuffer value3);

    void alGetListenerfv(int paramName, FloatBuffer values);

    void alGenSources(int size, IntBuffer srcNames);

    int alGenSources();

    void alDeleteSources(int size, IntBuffer sources);

    void alDeleteSources(int source);

    boolean alIsSource(int sourceName);

    void alSourcef(int source, int param, float value);

    void alSource3f(int source, int param, float v1, float v2, float v3);

    void alSourcefv(int source, int param, FloatBuffer values);

    void alSourcei(int source, int param, int value);

    void alGetSourcef(int source, int param, FloatBuffer value);

    float alGetSourcef(int source, int param);
        
    void alGetSource3f(int source, int param, FloatBuffer v1, FloatBuffer v2, FloatBuffer v3);

    void alGetSourcefv(int source, int param, FloatBuffer values);

    void alGetSourcei(int source, int param, IntBuffer value);

    int alGetSourcei(int source, int param);

    void alGetSourceiv(int source, int param, IntBuffer values);

    void alSourceQueueBuffers(int sourceName, int size, IntBuffer bufferNames);

    void alSourceQueueBuffers(int sourceName, int bufferName);

    void alSourceUnqueueBuffers(int sourceName, int size, IntBuffer bufferNames);

    int alSourceUnqueueBuffers(int sourceName);

    void alSourcePlay(int source);
	
    void alSourcePause(int source);
	
    void alSourceStop(int source);
    
    void alSourceRewind(int source);
	
    void alSourcePlayv(int size, IntBuffer sources);

    void alSourcePausev(int size, IntBuffer sources);

    void alSourceStopv(int size, IntBuffer sources);

    void alSourceRewindv(int size, IntBuffer sources);

    void alGenBuffers(int size, IntBuffer bufferNames);

    int alGenBuffers();

    void alDeleteBuffers(int size, IntBuffer bufferNames);

    void alDeleteBuffers(int bufferName);

    boolean alIsBuffer(int bufferName);

    void alGetBufferf(int bufferName, int paramName, FloatBuffer value);

    float alGetBufferf(int bufferName, int paramName);

    void alGetBufferi(int bufferName, int paramName, IntBuffer value);

    int alGetBufferi(int bufferName, int paramName);

    void alBufferData(int bufferName, int format, ByteBuffer data, int frequency);

    void alBufferData(int bufferName, int format, ShortBuffer data, int frequency);

    void alBufferData(int bufferName, int format, IntBuffer data, int frequency);

    void alBufferData(int bufferName, int format, FloatBuffer data, int frequency);

    int alGetEnumValue(String enumName);

    long alGetProcAddress(String funcName);
       
    boolean alIsExtensionPresent(String extName);
       
    void alGetIntegerv(int paramName, int[] dest);
    	
    void alGetFloatv(int paramName, float[] dest);
    	
    void alGetDoublev(int paramName, double[] dest);
    	
    void alListenerfv(int paramName, float[] values);
    	
    void alGetListenerf(int paramName, float[] value);
    	
    void alGetListeneri(int paramName, int[] value);
    	
    void alGetListener3f(int paramName, float[] value1, float[] value2, float[] value3);
    	
    void alGetListenerfv(int paramName, float[] values);
    	  
    void alGenSources(int size, int[] srcNames);
    	 
    void alDeleteSources(int size, int[] sources);
  
    void alSourcefv(int source, int param, float[] values);
    
    void alGetSourcef(int source, int param, float[] value);
    
    void alGetSource3f(int source, int param, float[] v1, float[] v2, float[] v3);
    
    void alGetSourcefv(int source, int param, float[] values);
  
    void alGetSourcei(int source, int param, int[] value);
  
    void alGetSourceiv(int source, int param, int[] values);

    void alSourceQueueBuffers(int sourceName, int size, int[] bufferNames);

    void alSourceUnqueueBuffers(int sourceName, int size, int[] bufferNames);

    void alSourcePlayv(int size, int[] sources);

    void alSourcePausev(int size, int[] sources);

    void alSourceStopv(int size, int[] sources);

    void alSourceRewindv(int size, int[] sources);

    void alGenBuffers(int size, int[] bufferNames);

    void alDeleteBuffers(int size, int[] bufferNames);

    void alGetBufferf(int bufferName, int paramName, float[] value);

    void alGetBufferi(int bufferName, int paramName, int[] value);

    void alBufferData(int bufferName, int format, short[] data, int frequency);

    void alBufferData(int bufferName, int format, int[] data, int frequency);

    void alBufferData(int bufferName, int format, float[] data, int frequency);

	
	String alGetStringiSOFT(int pname, int index);
	
	void alListener3i(int paramName, float value1, float value2, float value3);
	
	void alGetListeneriv(int param, IntBuffer values);
	
	void alSource3i(int source, int paramName, int value1, int value2, int value3);
	
	void alListeneriv(int listener, IntBuffer value);
	
	void alSourceiv(int source, int paramName, IntBuffer value);
	
	void alBufferf(int buffer, int paramName, float value);
	
	void alBuffer3f(int buffer, int paramName, float value1, float value2, float value3);
	
	void alBufferfv(int buffer, int paramName, FloatBuffer value);
	
	void alBufferi(int buffer, int paramName, int value);
	
	void alBuffer3i(int buffer, int paramName, int value1, int value2, int value3);
	
	void alBufferiv(int buffer, int paramName, IntBuffer value);
	
	void alGetBufferiv(int buffer, int param, IntBuffer values);
	
	void alGetBufferfv(int buffer, int param, FloatBuffer values);
	
	void alSpeedOfSound(float value);
	
	void alGetListeneriv(int param, int[] values);
	
	void alListeneriv(int listener, int[] value);
	
	void alSourceiv(int source, int paramName, int[] value);
	
	void alBufferfv(int buffer, int paramName, float[] value);
	
	void alBufferiv(int buffer, int paramName, int[] value);
	
	void alGetBufferiv(int buffer, int param, int[] values);
	
	void alGetBufferfv(int buffer, int param, float[] values);
	
	int alGetError();
	
	void alEnable(int target);
	
	void alDisable(int target);
	
	boolean alIsEnabled(int target);

	boolean alGetBoolean(int paramName);
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	boolean alcCaptureCloseDevice(ALCdevice device);
	
	ALCdevice alcCaptureOpenDevice(String deviceName, int frequency, int format, int samples);
	
	void alcCaptureSamples(ALCdevice device, ByteBuffer buffer, int samples);
	
	void alcCaptureSamples(ALCdevice device, ShortBuffer buffer, int samples);
	
	void alcCaptureSamples(ALCdevice device, IntBuffer buffer, int samples);
	
	void alcCaptureSamples(ALCdevice device, FloatBuffer buffer, int samples);
	
	void alcCaptureSamples(ALCdevice device, short[] buffer, int samples);
	
	void alcCaptureSamples(ALCdevice device, int[] buffer, int samples);
	
	void alcCaptureSamples(ALCdevice device, float[] buffer, int samples);
	
	void alBufferData(int buffer, int format, ByteBuffer data, int length, int freq);
	
	void alBufferData(int buffer, int format, ShortBuffer data, int length, int freq);
	
	void alBufferData(int buffer, int format, IntBuffer data, int length, int freq);
	
	void alBufferData(int buffer, int format, FloatBuffer data, int length, int freq);
	
	void alBufferData(int buffer, int format, short[] data, int length, int freq);
	
	void alBufferData(int buffer, int format, int[] data, int length, int freq);
	
	void alBufferData(int buffer, int format, float[] data, int length, int freq);
	
	void alcCaptureStart(ALCdevice device);
	
	void alcCaptureStop(ALCdevice device);
	
	boolean alcCloseDevice(ALCdevice deviceHandle);
	
	ALCcontext alcCreateContext(ALCdevice deviceHandle, int[] attrList);
	
	ALCcontext alcCreateContext(ALCdevice deviceHandle, IntBuffer attrList);
	
	void alcDestroyContext(ALCcontext context);
	
	ALCdevice alcGetContextsDevice(ALCcontext context);
	
	ALCcontext alcGetCurrentContext();
	
	int alcGetEnumValue(ALCdevice deviceHandle, String enumName);
	
	int alcGetError(ALCdevice deviceHandle);
	
	void alcProcessContext(ALCcontext context);
	
	void alcSuspendContext(ALCcontext context);
	
	void alcGetIntegerv(ALCdevice deviceHandle, int token, int sizeof, IntBuffer dest);
	
	void alcGetIntegerv(ALCdevice deviceHandle, int token, int sizeof, int[] dest);
	
	Pointer alcGetProcAddress(ALCdevice deviceHandle, String funcName);
	
	String alcGetString(ALCdevice deviceHandle, int token);
	
	ALCcontext alcGetThreadContext();
	
	boolean alcIsExtensionPresent(ALCdevice deviceHandle, String extName);
	
	boolean alcIsRenderFormatSupportedSOFT(ALCdevice deviceHandle, int frequency, int channels, int type);
	
	ALCdevice alcLoopbackOpenDeviceSOFT(String devicename);
	
	boolean alcMakeContextCurrent(ALCcontext context);
	
	ALCdevice alcOpenDevice(String deviceSpecifier);
	
	void alcRenderSamplesSOFT(ALCdevice device, ByteBuffer buffer, int samples);
	
	void alcRenderSamplesSOFT(ALCdevice device, ShortBuffer buffer, int samples);
	
	void alcRenderSamplesSOFT(ALCdevice device, IntBuffer buffer, int samples);
	
	void alcRenderSamplesSOFT(ALCdevice device, FloatBuffer buffer, int samples);
	
	void alcRenderSamplesSOFT(ALCdevice device, short[] buffer, int samples);
	
	void alcRenderSamplesSOFT(ALCdevice device, int[] buffer, int samples);
	
	void alcRenderSamplesSOFT(ALCdevice device, float[] buffer, int samples);
	
	boolean alcSetThreadContext(ALCcontext context); //XXX false
	
	void alcSuspendContext();
	
	boolean alcResetDeviceSOFT(ALCdevice device, IntBuffer attrList);
	
	boolean alcResetDeviceSOFT(ALCdevice device, int[] attrList);
	
	String alcGetStringiSOFT(ALCdevice device, int paramName, int index);
	
	void alcDevicePauseSOFT(ALCdevice device);
	
	void alcDeviceResumeSOFT(ALCdevice device);
	
	void alProcessUpdatesSOFT();
	
	void alDeferUpdatesSOFT();
	
	void alSourcedSOFT(int source, int param, double value);
	
	void alSource3dSOFT(int source, int param, double value1, double value2, double value3);
	
	void alSourcedvSOFT(int source, int param, DoubleBuffer value);
	
	void alGetSourcedSOFT(int source, int param, DoubleBuffer value);
	
	void alGetSource3dSOFT(int source, int param, DoubleBuffer value1, DoubleBuffer value2, DoubleBuffer value3);
	
	void alGetSourcedvSOFT(int source, int param, DoubleBuffer values);
	
	void alSourcei64SOFT(int source, int param, long value);
	
	void alSource3i64SOFT(int source, int param, long value1, long value2, long value3);
	
	void alSourcei64vSOFT(int source, int param, LongBuffer values);
	
	void alGetSourcei64SOFT(int source, int param, LongBuffer value);
	
	void alGetSource3i64SOFT(int source, int param, LongBuffer value1, LongBuffer value2, LongBuffer value3);
	
	void alGetSourcei64vSOFT(int source, int param, LongBuffer values);
	
	void alSourcedvSOFT(int source, int param, double[] value);
	
	void alGetSourcedSOFT(int source, int param, double[] value);
	
	void alGetSource3dSOFT(int source, int param, double[] value1, double[] value2, double[] value3);

	void alGetSourcedvSOFT(int source, int param, double[] values);
	
	void alSourcei64vSOFT(int source, int param, long[] values);

	void alGetSourcei64SOFT(int source, int param, long[] value);
	
	void alGetSource3i64SOFT(int source, int param, long[] value1, long[] value2, long[] value3);

	void alGetSourcei64vSOFT(int source, int param, long[] values);
	
	void alGenEffects(int size, IntBuffer effects);
	
	void alDeleteEffects(int size, IntBuffer effects);
	
	boolean alIsEffect(int effect);
	
	void alEffecti(int effect, int param, int value);
	
	void alEffectiv(int effect, int param, IntBuffer values);
	
	void alEffectf(int effect, int param, float value);
	
	void alEffectfv(int effect, int param, FloatBuffer values);
	
	void alGetEffecti(int effect, int param, IntBuffer value);
	
	void alGetEffectiv(int effect, int param, IntBuffer values);
	
	void alGetEffectf(int effect, int param, FloatBuffer value);
	
	void alGetEffectfv(int effect, int param, FloatBuffer values);
	
	void alGenFilters(int size, IntBuffer filters);
	
	void alDeleteFilters(int size, IntBuffer filters);
	
	boolean alIsFilter(int filter);
	
	void alFilteri(int filter, int param, int value);
	
	void alFilteriv(int filter, int param, IntBuffer values);
	
	void alFilterf(int filter, int param, float value);
	
	void alFilterfv(int filter, int param, FloatBuffer values);
	
	void alGetFilteri(int filter, int param, IntBuffer value);
	
	void alGetFilteriv(int filter, int param, IntBuffer values);
	
	void alGetFilterf(int filter, int param, FloatBuffer value);
	
	void alGetFilterfv(int filter, int param, FloatBuffer values);
	
	void alGenAuxiliaryEffectSlots(int size, IntBuffer effectSlots);
	
	void alDeleteAuxiliaryEffectSlots(int size, IntBuffer effectSlots);
	
	boolean alIsAuxiliaryEffectSlot(int effectSlot);
	
	void alAuxiliaryEffectSloti(int effectSlot, int param, int value);
	
	void alAuxiliaryEffectSlotiv(int effectSlot, int param, IntBuffer values);
	
	void alAuxiliaryEffectSlotf(int effectSlot, int param, float value);
	
	void alAuxiliaryEffectSlotfv(int effectSlot, int param, FloatBuffer values);
	
	void alGetAuxiliaryEffectSloti(int effectSlot, int param, IntBuffer value);
	
	void alGetAuxiliaryEffectSlotiv(int effectSlot, int param, IntBuffer values);
	
	void alGetAuxiliaryEffectSlotf(int effectSlot, int param, FloatBuffer value);
	
	void alGetAuxiliaryEffectSlotfv(int effectSlot, int param, FloatBuffer values);
	
	void alGenEffects(int size, int[] effects);
	
	void alDeleteEffects(int size, int[] effects);
	
	void alEffectiv(int effect, int param, int[] values);
	
	void alEffectfv(int effect, int param, float[] values);
	
	void alGetEffecti(int effect, int param, int[] value);
	
	void alGetEffectiv(int effect, int param, int[] values);
	
	void alGetEffectf(int effect, int param, float[] value);
	
	void alGetEffectfv(int effect, int param, float[] values);
	
	void alGenFilters(int size, int[] filters);
	
	void alDeleteFilters(int size, int[] filters);
	
	void alFilteriv(int filter, int param, int[] values);
	
	void alFilterfv(int filter, int param, float[] values);
	
	void alGetFilteri(int filter, int param, int[] value);
    
	void alGetFilteriv(int filter, int param, int[] values);
	
	void alGetFilterf(int filter, int param, float[] value);
	
	void alGetFilterfv(int filter, int param, float[] values);
	
	void alGenAuxiliaryEffectSlots(int size, int[] effectSlots);
	
	void alDeleteAuxiliaryEffectSlots(int size, int[] effectSlots);
	
	void alAuxiliaryEffectSlotiv(int effectSlot, int param, int[] values);
	
	void alAuxiliaryEffectSlotfv(int effectSlot, int param, float[] values);
	
	void alGetAuxiliaryEffectSloti(int effectSlot, int param, int[] value);
	
	void alGetAuxiliaryEffectSlotiv(int effectSlot, int param, int[] values);
	
	void alGetAuxiliaryEffectSlotf(int effectSlot, int param, float[] value);
	
	void alGetAuxiliaryEffectSlotfv(int effectSlot, int param, float[] values);
}
