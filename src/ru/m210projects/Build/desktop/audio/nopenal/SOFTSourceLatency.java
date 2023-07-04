/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 * MACHINE GENERATED FILE, DO NOT EDIT
 */
package ru.m210projects.Build.desktop.audio.nopenal;

import static ru.m210projects.Build.desktop.audio.nopenal.AL.*;

import java.nio.*;

import org.lwjgl.BufferUtils;

/**
 * Native bindings to the <a target="_blank" href="http://kcat.strangesoft.net/opegetNative().al-extensions/SOFT_source_latency.txt">SOFT_source_latency</a> extension.
 * 
 * <p>This extension provides a method for applications to more accurately measure the playback latency of sources. Unextended OpegetNative().al allows apps to retrieve
 * a source's playback offset in bytes, samples, or seconds, but this is (typically) where the AL is processing the audio data.</p>
 * 
 * <p>Often, more processing is done outside of the AL. Audio servers are common and they can introduce a bit of latency, increasing the time between when the
 * AL is done with a piece of audio data until it gets heard by the user. If the OpegetNative().al implementation uses its own mixer, that can also add to the
 * latency. This can ultimately cause a not-insignificant delay between where the AL is processing and what is actually being heard.</p>
 * 
 * <p>Although this delay may not be very noticeable for general gaming, if the app is trying to keep a video or animation syncronized with the playback of an
 * OpegetNative().al source this extra delay can cause the audio and video to appear of out sync.</p>
 * 
 * <p>Luckily, most audio systems have a way of measuring the latency it takes for sound to actually get to the physical output device (the DAC or speakers).
 * By providing this information through the AL, an application can more accurately tell what a user is hearing and thus synchronize better with the audio
 * output.</p>
 */
public class SOFTSourceLatency {

    /**
     * The playback position, expressed in fixed-point samples, along with the playback latency, expressed in nanoseconds (1/1000000000ths of a second). This
     * attribute is read-only.
     * 
     * <p>The first value in the returned vector is the sample offset, which is a 32.32 fixed-point value. The whole number is stored in the upper 32 bits and
     * the fractiogetNative().al component is in the lower 32 bits. The value is similar to that returned by {@link AL11#AL_SAMPLE_OFFSET SAMPLE_OFFSET}, just with more precision.</p>
     * 
     * <p>The second value is the latency, in nanoseconds. It represents the length of time it will take for the audio at the current offset to actually reach
     * the speakers or DAC. This value should be considered volatile, as it may change very often during playback (it can depend on a number of factors,
     * including how full the mixing buffer OpegetNative().al may be using is timer jitter, or other changes deeper in the audio pipeline).</p>
     * 
     * <p>The retrieved offset and latency should be considered atomic, with respect to one another. This means the returned latency was measured exactly when
     * the source was at the returned offset.</p>
     */
    public static final int AL_SAMPLE_OFFSET_LATENCY_SOFT = 0x1200;

    /**
     * The playback position, along with the playback latency, both expressed in seconds. This attribute is read-only.
     * 
     * <p>The first value in the returned vector is the offset in seconds. The value is similar to that returned by {@link AL11#AL_SEC_OFFSET SEC_OFFSET}, just with more precision.</p>
     * 
     * <p>The second value is the latency, in seconds. It represents the length of time it will take for the audio at the current offset to actually reach the
     * speakers or DAC. This value should be considered volatile, as it may change very often during playback (it can depend on a number of factors, including
     * how full the mixing buffer OpegetNative().al may be using is, timer jitter, or other changes deeper in the audio pipeline).</p>
     * 
     * <p>The retrieved offset and latency should be considered atomic with respect to one another. This means the returned latency was measured exactly when the
     * source was at the returned offset.</p>
     */
    public static final int AL_SEC_OFFSET_LATENCY_SOFT = 0x1201;

    protected SOFTSourceLatency() {
        throw new UnsupportedOperationException();
    }

    // --- [ alSourcedSOFT ] ---

    /**
     * Sets the double value of a source parameter.
     *
     * @param source the source to modify
     * @param param  the parameter to modify
     * @param value  the parameter value
     */
    
    public static void alSourcedSOFT(int source, int param, double value) {
        getNative().alSourcedSOFT(source, param, value);
    }

    // --- [ alSource3dSOFT ] ---

    /**
     * Sets the 3 dimensiogetNative().al double values of a source parameter.
     *
     * @param source the source to modify
     * @param param  the parameter to modify
     * @param value1 the first value
     * @param value2 the second value
     * @param value3 the third value
     */
    
    public static void alSource3dSOFT(int source, int param, double value1, double value2, double value3) {
        getNative().alSource3dSOFT(source, param, value1, value2, value3);
    }

    // --- [ alSourcedvSOFT ] ---

    /**
     * pointer version of {@link #alSourcedSOFT SourcedSOFT}
     *
     * @param source the source to modify
     * @param param  the parameter to modify
     * @param value  the parameter values
     */
    
    public static void alSourcedvSOFT(int source, int param, DoubleBuffer value) {
        getNative().alSourcedvSOFT(source, param, value);
    }

    // --- [ alGetSourcedSOFT ] ---

    /**
     * Gets the double value of a source parameter.
     *
     * @param source the source to query
     * @param param  the parameter to query
     * @param value  the parameter values
     */
    
    public static void alGetSourcedSOFT(int source, int param, DoubleBuffer value) {
        getNative().alGetSourcedSOFT(source, param, value);
    }

    /**
     * Gets the double value of a source parameter.
     *
     * @param source the source to query
     * @param param  the parameter to query
     */
    
    public static double alGetSourcedSOFT(int source, int param) {
        DoubleBuffer value = BufferUtils.createDoubleBuffer(1);
        getNative().alGetSourcedSOFT(source, param, value);
        return value.get(0);
    }

    // --- [ alGetSource3dSOFT ] ---

    /**
     * Gets the 3 dimensiogetNative().al double values of a source parameter.
     *
     * @param source the source to query
     * @param param  the parameter to query
     * @param value1 the first value
     * @param value2 the second value
     * @param value3 the third value
     */
    
    public static void alGetSource3dSOFT(int source, int param, DoubleBuffer value1, DoubleBuffer value2, DoubleBuffer value3) {
        getNative().alGetSource3dSOFT(source, param, value1, value2, value3);
    }

    // --- [ alGetSourcedvSOFT ] ---

    /**
     * Array version of {@link #alGetSourcedSOFT GetSourcedSOFT}
     *
     * @param source the source to query
     * @param param  the parameter to query
     * @param values the parameter values
     */
    
    public static void alGetSourcedvSOFT(int source, int param, DoubleBuffer values) {
        getNative().alGetSourcedvSOFT(source, param, values);
    }

    // --- [ alSourcei64SOFT ] ---

    /**
     * Sets the 64 bit integer value of a source parameter.
     *
     * @param source the source to modify
     * @param param  the parameter to modify
     * @param value  the parameter values
     */
    
    public static void alSourcei64SOFT(int source, int param, long value) {
    	getNative().alSourcei64SOFT(source, param, value);
    }

    // --- [ alSource3i64SOFT ] ---

    /**
     * Sets the 3 dimensiogetNative().al 64 bit integer values of a source parameter.
     *
     * @param source the source to modify
     * @param param  the parameter to modify
     * @param value1 the first value
     * @param value2 the second value
     * @param value3 the third value
     */
    
    public static void alSource3i64SOFT(int source, int param, long value1, long value2, long value3) {
    	getNative().alSource3i64SOFT(source, param, value1, value2, value3);
    }

    // --- [ alSourcei64vSOFT ] ---

    /**
     * Array version of {@link #alSourcei64SOFT Sourcei64SOFT}
     *
     * @param source the source to modify
     * @param param  the parameter to modify
     * @param values the parameter values
     */
    
    public static void alSourcei64vSOFT(int source, int param, LongBuffer values) {
        getNative().alSourcei64vSOFT(source, param, values);
    }

    // --- [ alGetSourcei64SOFT ] ---

    /**
     * Gets the 64 bit integer value of a source parameter.
     *
     * @param source the source to query
     * @param param  the parameter to query
     * @param value  the parameter values
     */
    
    public static void alGetSourcei64SOFT(int source, int param, LongBuffer value) {
        getNative().alGetSourcei64SOFT(source, param, value);
    }

    /**
     * Gets the 64 bit integer value of a source parameter.
     *
     * @param source the source to query
     * @param param  the parameter to query
     */
    
    public static long alGetSourcei64SOFT(int source, int param) {
        LongBuffer value = BufferUtils.createLongBuffer(1);
        getNative().alGetSourcei64SOFT(source, param, value);
        return value.get(0);
    }

    // --- [ alGetSource3i64SOFT ] ---

    /**
     * Gets the 3 dimensiogetNative().al 64 bit integer values of a source parameter.
     *
     * @param source the source to query
     * @param param  the parameter to query
     * @param value1 the first value
     * @param value2 the second value
     * @param value3 the third value
     */
    
    public static void alGetSource3i64SOFT(int source, int param, LongBuffer value1, LongBuffer value2, LongBuffer value3) {
        getNative().alGetSource3i64SOFT(source, param, value1, value2, value3);
    }

    // --- [ alGetSourcei64vSOFT ] ---

    /**
     * Array version of {@link #alGetSourcei64SOFT GetSourcei64SOFT}
     *
     * @param source the source to query
     * @param param  the parameter to query
     * @param values the parameter values
     */
    
    public static void alGetSourcei64vSOFT(int source, int param, LongBuffer values) {
        getNative().alGetSourcei64vSOFT(source, param, values);
    }

    /** Array version of: {@link #alSourcedvSOFT SourcedvSOFT} */
    
    public static void alSourcedvSOFT(int source, int param, double[] value) {
    	getNative().alSourcedvSOFT(source, param, value);
    }

    /** Array version of: {@link #alGetSourcedSOFT GetSourcedSOFT} */
    
    public static void alGetSourcedSOFT(int source, int param, double[] value) {
        getNative().alGetSourcedSOFT(source, param, value);
    }

    /** Array version of: {@link #alGetSource3dSOFT GetSource3dSOFT} */
    
    public static void alGetSource3dSOFT(int source, int param, double[] value1, double[] value2, double[] value3) {
        getNative().alGetSource3dSOFT(source, param, value1, value2, value3);
    }

    /** Array version of: {@link #alGetSourcedvSOFT GetSourcedvSOFT} */
    
    public static void alGetSourcedvSOFT(int source, int param, double[] values) {
        getNative().alGetSourcedvSOFT(source, param, values);
    }

    /** Array version of: {@link #alSourcei64vSOFT Sourcei64vSOFT} */
    
    public static void alSourcei64vSOFT(int source, int param, long[] values) {
        getNative().alSourcei64vSOFT(source, param, values);
    }

    /** Array version of: {@link #alGetSourcei64SOFT GetSourcei64SOFT} */
    
    public static void alGetSourcei64SOFT(int source, int param, long[] value) {
        getNative().alGetSourcei64SOFT(source, param, value);
    }

    /** Array version of: {@link #alGetSource3i64SOFT GetSource3i64SOFT} */
    
    public static void alGetSource3i64SOFT(int source, int param, long[] value1, long[] value2, long[] value3) {
        getNative().alGetSource3i64SOFT(source, param, value1, value2, value3);
    }

    /** Array version of: {@link #alGetSourcei64vSOFT GetSourcei64vSOFT} */
    
    public static void alGetSourcei64vSOFT(int source, int param, long[] values) {
        getNative().alGetSourcei64vSOFT(source, param, values);
    }

}