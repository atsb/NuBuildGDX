package ru.m210projects.Build.desktop.audio;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import ru.m210projects.Build.desktop.audio.nopenal.AL;
import ru.m210projects.Build.desktop.audio.nopenal.AL10;
import ru.m210projects.Build.desktop.audio.nopenal.AL11;
import ru.m210projects.Build.desktop.audio.nopenal.ALC10;
import ru.m210projects.Build.desktop.audio.nopenal.ALCdevice;
import ru.m210projects.Build.desktop.audio.nopenal.EXTEfx;
import ru.m210projects.Build.desktop.audio.nopenal.EXTThreadLocalContext;
import ru.m210projects.Build.desktop.audio.nopenal.SOFTSourceResampler;

public class GdxAL implements ALAudio {
	
	private String name;
	private String version;

	//SOFT Resampler
	private boolean alResamplerSupport;
	private int alNumResamplers;
	private String[] alResamplerNames;
	
	//EFX Effects
	private boolean alEfxSupport;
	private int alEffectSlot = -1;
	private int alEffect = -1;
	
	public GdxAL() throws Throwable
	{
		if(!init())
			throw new Throwable();
	}
	
	public boolean init()
	{
		try {
			AL.create();
			ALCdevice device = AL.getDevice();
			name = ALC10.alcGetString(device, ALC10.ALC_DEVICE_SPECIFIER);
			version = AL10.alGetString(AL10.AL_VERSION);

	        boolean makeCurrentFailed = EXTThreadLocalContext.alcSetThreadContext(AL.getContext());
	        if (!makeCurrentFailed) 
	        	return false;
	        
	        if(alResamplerSupport = AL10.alIsExtensionPresent("AL_SOFT_source_resampler"))
	        {
		    	alNumResamplers = AL10.alGetInteger(SOFTSourceResampler.AL_NUM_RESAMPLERS_SOFT);
		    	alResamplerNames = new String[alNumResamplers];
		    	for(int i = 0; i < alNumResamplers; i++) 
		    		alResamplerNames[i] = SOFTSourceResampler.alGetStringiSOFT(SOFTSourceResampler.AL_RESAMPLER_NAME_SOFT, i);
	        }
	
	        if (alEfxSupport = ALC10.alcIsExtensionPresent(device, "ALC_EXT_EFX")) 
	        {
				alEffectSlot = EXTEfx.alGenAuxiliaryEffectSlots();
				alEffect = EXTEfx.alGenEffects();
				EXTEfx.alEffecti(alEffect, EXTEfx.AL_EFFECT_TYPE, EXTEfx.AL_EFFECT_CHORUS);	//AL_EFFECT_REVERB
			}
	        
	        return true;
		}
        catch (Throwable e)
		{
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public int alGetError() {
		return AL10.alGetError();
	}

	@Override
	public int alGetSourcei(int source, int param) {
		return AL10.alGetSourcei(source, param);
	}

	@Override
	public void alSourcei(int source, int param, int value) {
		AL10.alSourcei(source, param, value);
	}

	@Override
	public void alSourcef(int source, int param, float value) {
		AL10.alSourcef(source, param, value);
	}
	
	@Override
	public void alSource3i(int source, int paramName, int value1, int value2, int value3) {
		AL11.alSource3i(source, paramName, value1, value2, value3);
	}

	@Override
	public void alSource3f(int source, int param, float v1, float v2, float v3) {
		AL10.alSource3f(source, param, v1, v2, v3);
	}

	@Override
	public void alSourceQueueBuffers(int sourceName, int bufferName) {
		AL10.alSourceQueueBuffers(sourceName, bufferName);
	}

	@Override
	public void alSourcePlay(int source) {
		AL10.alSourcePlay(source);
	}

	@Override
	public void alSourcePause(int source) {
		AL10.alSourcePause(source);
	}

	@Override
	public void alSourceStop(int source) {
		AL10.alSourceStop(source);
	}
	
	@Override
	public void setSourceReverb(int sourceId, boolean enable, float delay) {
		if(!alIsEFXSupport()) return;
		
		if(enable)
		{
//			EXTEfx.alEffecti(alEffect, EXTEfx.AL_CHORUS_WAVEFORM, 0);
			EXTEfx.alEffectf(alEffect, EXTEfx.AL_CHORUS_RATE, 0);
			EXTEfx.alEffectf(alEffect, EXTEfx.AL_CHORUS_FEEDBACK, delay);
			EXTEfx.alEffectf(alEffect, EXTEfx.AL_CHORUS_DEPTH, 0.0f);
			EXTEfx.alEffectf(alEffect, EXTEfx.AL_CHORUS_DELAY, 0.012f);
			EXTEfx.alAuxiliaryEffectSloti(alEffectSlot, EXTEfx.AL_EFFECTSLOT_EFFECT, alEffect);
		    alSource3i(sourceId, EXTEfx.AL_AUXILIARY_SEND_FILTER, alEffectSlot, 0, EXTEfx.AL_FILTER_NULL);
		} 
		else
		{
			EXTEfx.alAuxiliaryEffectSloti(alEffectSlot, EXTEfx.AL_EFFECTSLOT_EFFECT, EXTEfx.AL_EFFECT_NULL);
			alSource3i(sourceId, EXTEfx.AL_AUXILIARY_SEND_FILTER, EXTEfx.AL_EFFECTSLOT_NULL, 0, EXTEfx.AL_FILTER_NULL);
		}
	}

	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public void dispose() {
		if(alIsEFXSupport()) {
			EXTEfx.alDeleteEffects(alEffect);
			EXTEfx.alDeleteAuxiliaryEffectSlots(alEffectSlot);
		}
		
		AL.destroy();
		while (AL.isCreated()) {
			try {
				Thread.sleep(10);
				} catch (InterruptedException e) {
			}
		}
	}

	@Override
	public String getVersion() {
		return version;
	}

	@Override
	public int alGenBuffers() {
		return AL10.alGenBuffers();
	}

	@Override
	public void alDistanceModel(int modelName) {
		AL10.alDistanceModel(modelName);
	}

	@Override
	public boolean alIsEFXSupport() {
		return alEfxSupport && alEffect != -1;
	}

	@Override
	public boolean alIsSoftResamplerSupport() {
		return alResamplerSupport && alNumResamplers > 0;
	}

	@Override
	public String alGetSoftResamplerName(int num) {
		return alResamplerNames[num];
	}

	@Override
	public void setSourceSoftResampler(int sourceId, int value) {
		if(!alIsSoftResamplerSupport() || value < 0 || value > alNumResamplers) return;
		alSourcei(sourceId, SOFTSourceResampler.AL_SOURCE_RESAMPLER_SOFT, value);
	}

	@Override
	public void alDeleteBuffers(IntBuffer bufferNames) {
		AL10.alDeleteBuffers(bufferNames);
	}

	@Override
	public void alGenBuffers(IntBuffer bufferNames) {
		AL10.alGenBuffers(bufferNames);
	}

	@Override
	public float alGetSourcef(int source, int param) {
		return AL10.alGetSourcef(source, param);
	}

	@Override
	public int alSourceUnqueueBuffers(int sourceName) {
		return AL10.alSourceUnqueueBuffers(sourceName);
	}

	@Override
	public void alBufferData(int bufferName, int format, ByteBuffer data, int frequency) {
		AL10.alBufferData(bufferName, format, data, frequency);
	}

	@Override
	public void alListener(int paramName, FloatBuffer value) {
		AL10.alListenerfv(paramName, value);
	}

	@Override
	public void alListener3f(int paramName, float v1, float v2, float v3) {
		AL10.alListener3f(paramName, v1, v2, v3);
	}

	@Override
	public int alGenSources() {
		return AL10.alGenSources();
	}

	@Override
	public void alSource(int source, int param, FloatBuffer values) {
		AL10.alSourcefv(source, param, values);
	}

	@Override
	public void alDeleteSources(int sourceId) {
		AL10.alDeleteSources(sourceId);
	}

	@Override
	public int alGetNumResamplers() {
		return alNumResamplers;
	}

	@Override
	public void alGetSourcefv(int source, int param, FloatBuffer values) {
		AL10.alGetSourcefv(source, param, values);
	}

	@Override
	public float alGetListenerf(int paramName) {
		return AL10.alGetListenerf(paramName);
	}

	@Override
	public int alGetListeneri(int paramName) {
		return AL10.alGetListeneri(paramName);
	}

	@Override
	public void alGetListenerfv(int param, FloatBuffer values) {
		AL10.alGetListenerfv(param, values);
	}
}
