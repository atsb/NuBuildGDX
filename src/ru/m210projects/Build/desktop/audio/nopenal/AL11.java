/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 * MACHINE GENERATED FILE, DO NOT EDIT
 */
package ru.m210projects.Build.desktop.audio.nopenal;

import static ru.m210projects.Build.desktop.audio.nopenal.AL.*;

import java.nio.*;

/** Native bindings to AL 1.1 functionality. */
public class AL11 extends AL10 {

    /** General tokens. */
    public static final int
        AL_SEC_OFFSET                = 0x1024,
        AL_SAMPLE_OFFSET             = 0x1025,
        AL_BYTE_OFFSET               = 0x1026,
        AL_STATIC                    = 0x1028,
        AL_STREAMING                 = 0x1029,
        AL_UNDETERMINED              = 0x1030,
        AL_ILLEGAL_COMMAND           = 0xA004,
        AL_SPEED_OF_SOUND            = 0xC003,
        AL_LINEAR_DISTANCE           = 0xD003,
        AL_LINEAR_DISTANCE_CLAMPED   = 0xD004,
        AL_EXPONENT_DISTANCE         = 0xD005,
        AL_EXPONENT_DISTANCE_CLAMPED = 0xD006;

    protected AL11() {
        throw new UnsupportedOperationException();
    }

    // --- [ alListener3i ] ---

    /**
     * Sets the 3 dimensional integer values of a listener parameter.
     *
     * @param paramName the parameter to modify
     * @param value1    the first value
     * @param value2    the second value
     * @param value3    the third value
     */
    
    public static void alListener3i(int paramName, float value1, float value2, float value3) {
    	getNative().alListener3i(paramName, value1, value2, value3);
    }

    // --- [ alGetListeneriv ] ---

    /**
     * Returns the integer values of the specified listener parameter.
     *
     * @param param  the parameter to query
     * @param values the parameter values
     */
    
    public static void alGetListeneriv(int param, IntBuffer values) {
        getNative().alGetListeneriv(param, values);
    }

    // --- [ alSource3i ] ---

    /**
     * Sets the 3 dimensional integer values of a source parameter.
     *
     * @param source    the source to modify
     * @param paramName the parameter to modify
     * @param value1    the first value
     * @param value2    the second value
     * @param value3    the third value
     */
    
    public static void alSource3i(int source, int paramName, int value1, int value2, int value3) {
    	getNative().alSource3i(source, paramName, value1, value2, value3);
    }

    // --- [ alListeneriv ] ---

    /**
     * Pointer version.
     *
     * @param listener the parameter to modify
     * @param value    the parameter values
     */
    
    public static void alListeneriv(int listener, IntBuffer value) {
        getNative().alListeneriv(listener, value);
    }

    // --- [ alSourceiv ] ---

    /**
     * Pointer version.
     *
     * @param source    the source to modify
     * @param paramName the parameter to modify
     * @param value     the parameter values
     */
    
    public static void alSourceiv(int source, int paramName, IntBuffer value) {
        getNative().alSourceiv(source, paramName, value);
    }

    // --- [ alBufferf ] ---

    /**
     * Sets the float value of a buffer parameter.
     *
     * @param buffer    the buffer to modify
     * @param paramName the parameter to modify
     * @param value     the value
     */
    
    public static void alBufferf(int buffer, int paramName, float value) {
    	getNative().alBufferf(buffer, paramName, value);
    }

    // --- [ alBuffer3f ] ---

    /**
     * Sets the dimensional value of a buffer parameter.
     *
     * @param buffer    the buffer to modify
     * @param paramName the parameter to modify
     * @param value1    the first value
     * @param value2    the second value
     * @param value3    the third value
     */
    
    public static void alBuffer3f(int buffer, int paramName, float value1, float value2, float value3) {
    	getNative().alBuffer3f(buffer, paramName, value1, value2, value3);
    }

    // --- [ alBufferfv ] ---

    /**
     * the pointer version of {@link #alBufferf Bufferf}
     *
     * @param buffer    the buffer to modify
     * @param paramName the parameter to modify
     * @param value     the parameter values
     */
    
    public static void alBufferfv(int buffer, int paramName, FloatBuffer value) {
        getNative().alBufferfv(buffer, paramName, value);
    }

    // --- [ alBufferi ] ---

    /**
     * Sets the integer value of a buffer parameter.
     *
     * @param buffer    the buffer to modify
     * @param paramName the parameter to modify
     * @param value     the value
     */
    
    public static void alBufferi(int buffer, int paramName, int value) {
    	getNative().alBufferi(buffer, paramName, value);
    }

    // --- [ alBuffer3i ] ---

    /**
     * Sets the integer 3 dimensional value of a buffer parameter.
     *
     * @param buffer    the buffer to modify
     * @param paramName the parameter to modify
     * @param value1    the first value
     * @param value2    the second value
     * @param value3    the third value
     */
    
    public static void alBuffer3i(int buffer, int paramName, int value1, int value2, int value3) {
    	getNative().alBuffer3i(buffer, paramName, value1, value2, value3);
    }

    // --- [ alBufferiv ] ---

    /**
     * the pointer version of {@link #alBufferi Bufferi}
     *
     * @param buffer    the buffer to modify
     * @param paramName the parameter to modify
     * @param value     the parameter values
     */
    
    public static void alBufferiv(int buffer, int paramName, IntBuffer value) {
        getNative().alBufferiv(buffer, paramName, value);
    }

    // --- [ alGetBufferiv ] ---

    /**
     * Returns the integer values of the specified buffer parameter.
     *
     * @param buffer the buffer to query
     * @param param  the parameter to query
     * @param values the parameter values
     */
    
    public static void alGetBufferiv(int buffer, int param, IntBuffer values) {
        getNative().alGetBufferiv(buffer, param, values);
    }

    // --- [ alGetBufferfv ] ---

    /**
     * Returns the float values of the specified buffer parameter.
     *
     * @param buffer the buffer to query
     * @param param  the parameter to query
     * @param values the parameter values
     */
    
    public static void alGetBufferfv(int buffer, int param, FloatBuffer values) {
        getNative().alGetBufferfv(buffer, param, values);
    }

    // --- [ alSpeedOfSound ] ---

    /**
     * Sets the speed of sound.
     *
     * @param value the speed of sound
     */
    
    public static void alSpeedOfSound(float value) {
    	getNative().alSpeedOfSound(value);
    }

    /** Array version of: {@link #alGetListeneriv GetListeneriv} */
    
    public static void alGetListeneriv(int param, int[] values) {
    	getNative().alGetListeneriv(param, values);
    }

    /** Array version of: {@link #alListeneriv Listeneriv} */
    
    public static void alListeneriv(int listener, int[] value) {
    	getNative().alListeneriv(listener, value);
    }

    /** Array version of: {@link #alSourceiv Sourceiv} */
    
    public static void alSourceiv(int source, int paramName, int[] value) {
    	getNative().alSourceiv(source, paramName, value);
    }

    /** Array version of: {@link #alBufferfv Bufferfv} */
    
    public static void alBufferfv(int buffer, int paramName, float[] value) {
    	getNative().alBufferfv(buffer, paramName, value);
    }

    /** Array version of: {@link #alBufferiv Bufferiv} */
    
    public static void alBufferiv(int buffer, int paramName, int[] value) {
    	getNative().alBufferiv(buffer, paramName, value);
    }

    /** Array version of: {@link #alGetBufferiv GetBufferiv} */
    
    public static void alGetBufferiv(int buffer, int param, int[] values) {
    	getNative().alGetBufferiv(buffer, param, values);
    }

    /** Array version of: {@link #alGetBufferfv GetBufferfv} */
    
    public static void alGetBufferfv(int buffer, int param, float[] values) {
    	getNative().alGetBufferfv(buffer, param, values);
    }

}