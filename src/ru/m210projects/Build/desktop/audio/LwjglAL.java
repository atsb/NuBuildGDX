package ru.m210projects.Build.desktop.audio;

import static org.lwjgl.openal.EFX10.AL_AUXILIARY_SEND_FILTER;
import static org.lwjgl.openal.EFX10.AL_EFFECTSLOT_EFFECT;
import static org.lwjgl.openal.EFX10.AL_EFFECTSLOT_NULL;
import static org.lwjgl.openal.EFX10.AL_EFFECT_NULL;
import static org.lwjgl.openal.EFX10.AL_EFFECT_REVERB;
import static org.lwjgl.openal.EFX10.AL_EFFECT_TYPE;
import static org.lwjgl.openal.EFX10.AL_FILTER_NULL;
import static org.lwjgl.openal.EFX10.AL_REVERB_DECAY_TIME;
import static org.lwjgl.openal.EFX10.alAuxiliaryEffectSloti;
import static org.lwjgl.openal.EFX10.alEffectf;
import static org.lwjgl.openal.EFX10.alEffecti;
import static org.lwjgl.openal.EFX10.alDeleteEffects;
import static org.lwjgl.openal.EFX10.alDeleteAuxiliaryEffectSlots;
import static org.lwjgl.openal.EFX10.alGenAuxiliaryEffectSlots;
import static org.lwjgl.openal.EFX10.alGenEffects;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
 
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.AL11;
import org.lwjgl.openal.ALC10;
import org.lwjgl.openal.ALCdevice;

import com.badlogic.gdx.backends.lwjgl.LwjglNativesLoader;

public class LwjglAL implements ALAudio {

	private final String name;
	private final String version;

	//EFX Effects
	private final boolean alEfxSupport;
	private int alEffectSlot = -1;
	private int alEffect = -1;
	
	public LwjglAL() throws Throwable
	{
		LwjglNativesLoader.load();
		AL.create();
		ALCdevice device = AL.getDevice();
		name = ALC10.alcGetString(device, ALC10.ALC_DEVICE_SPECIFIER);
		version = AL10.alGetString(AL10.AL_VERSION);

        if (alEfxSupport = ALC10.alcIsExtensionPresent(AL.getDevice(), "ALC_EXT_EFX")) 
        {
        	alEffectSlot = alGenAuxiliaryEffectSlots();
			alEffect = alGenEffects();
			alEffecti(alEffect, AL_EFFECT_TYPE, AL_EFFECT_REVERB);
		}
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getVersion() {
		return version;
	}

	@Override
	public boolean alIsEFXSupport() {
		return alEfxSupport && alEffect != -1;
	}

	@Override
	public boolean alIsSoftResamplerSupport() {
		return false; //not supported
	}

	@Override
	public String alGetSoftResamplerName(int num) {
		return "Not supported";
	}
	
	@Override
	public void dispose() {
		if(alIsEFXSupport()) {
			alDeleteEffects(alEffect);
			alDeleteAuxiliaryEffectSlots(alEffectSlot);
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
	public int alGetError() {
		return AL10.alGetError();
	}

	@Override
	public int alGenBuffers() {
		return AL10.alGenBuffers();
	}

	@Override
	public void setSourceSoftResampler(int sourceId, int value) { /* not supported */ }

	@Override
	public void setSourceReverb(int sourceId, boolean enable, float delay) {
		if(!alIsEFXSupport()) return;
		
		if(enable)
		{
			 alEffectf(alEffect, AL_REVERB_DECAY_TIME, delay);
			 alAuxiliaryEffectSloti(alEffectSlot, AL_EFFECTSLOT_EFFECT, alEffect);
		     alSource3i(sourceId, AL_AUXILIARY_SEND_FILTER, alEffectSlot, 0, AL_FILTER_NULL);
		} 
		else
		{
			alAuxiliaryEffectSloti(alEffectSlot, AL_EFFECTSLOT_EFFECT, AL_EFFECT_NULL);
			alSource3i(sourceId, AL_AUXILIARY_SEND_FILTER, AL_EFFECTSLOT_NULL, 0, AL_FILTER_NULL);
		}
	}

	@Override
	public void alDistanceModel(int modelName) {
		AL10.alDistanceModel(modelName);
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
		AL10.alListener(paramName, value);
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
		AL10.alSource(source, param, values);
	}

	@Override
	public void alDeleteSources(int sourceId) {
		AL10.alDeleteSources(sourceId);
	}

	@Override
	public int alGetNumResamplers() {
		return 1;
	}

	@Override
	public void alGetSourcefv(int source, int param, FloatBuffer values) {
		AL10.alGetSource(source, param, values);
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
		AL10.alGetListener(param, values);
	}
}
