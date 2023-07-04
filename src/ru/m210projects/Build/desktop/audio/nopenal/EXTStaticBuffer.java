/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 * MACHINE GENERATED FILE, DO NOT EDIT
 */
package ru.m210projects.Build.desktop.audio.nopenal;

import static ru.m210projects.Build.desktop.audio.nopenal.AL.*;

import java.nio.*;
/**
 * Native bindings to the {@code AL_EXT_static_buffer} extension.
 * 
 * <p>This extension provides a means for the caller to avoid the overhead associated with the {@link AL10#alBufferData BufferData} call which performs a physical copy of the
 * data provided by the caller to internal buffers. When using the {@code AL_EXT_static_buffer} extension, OpenAL's internal buffers use the data pointer provided by
 * the caller for all data access.</p>
 */
public class EXTStaticBuffer {

    protected EXTStaticBuffer() {
        throw new UnsupportedOperationException();
    }

    // --- [ alBufferDataStatic ] ---

    /**
     * Sets the sample data of the specified buffer.
     *
     * @param buffer the buffer handle
     * @param format the data format
     * @param data   the sample data
     * @param freq   the data frequency
     */
    
    public static void alBufferDataStatic(int buffer, int format, ByteBuffer data, int freq) {
    	getNative().alBufferData(buffer, format, data, data.remaining(), freq);
    }

    /**
     * Sets the sample data of the specified buffer.
     *
     * @param buffer the buffer handle
     * @param format the data format
     * @param data   the sample data
     * @param freq   the data frequency
     */
    
    public static void alBufferDataStatic(int buffer, int format, ShortBuffer data, int freq) {
    	getNative().alBufferData(buffer, format, data, data.remaining() << 1, freq);
    }

    /**
     * Sets the sample data of the specified buffer.
     *
     * @param buffer the buffer handle
     * @param format the data format
     * @param data   the sample data
     * @param freq   the data frequency
     */
    
    public static void alBufferDataStatic(int buffer, int format, IntBuffer data, int freq) {
    	getNative().alBufferData(buffer, format, data, data.remaining() << 2, freq);
    }

    /**
     * Sets the sample data of the specified buffer.
     *
     * @param buffer the buffer handle
     * @param format the data format
     * @param data   the sample data
     * @param freq   the data frequency
     */
    
    public static void alBufferDataStatic(int buffer, int format, FloatBuffer data, int freq) {
    	getNative().alBufferData(buffer, format, data, data.remaining() << 2, freq);
    }

    /** Array version of: {@link #alBufferDataStatic BufferDataStatic} */
    
    public static void alBufferDataStatic(int buffer, int format, short[] data, int freq) {
    	getNative().alBufferData(buffer, format, data, data.length << 1, freq);
    }

    /** Array version of: {@link #alBufferDataStatic BufferDataStatic} */
    
    public static void alBufferDataStatic(int buffer, int format, int[] data, int freq) {
    	getNative().alBufferData(buffer, format, data, data.length << 2, freq);
    }

    /** Array version of: {@link #alBufferDataStatic BufferDataStatic} */
    
    public static void alBufferDataStatic(int buffer, int format, float[] data, int freq) {
    	getNative().alBufferData(buffer, format, data, data.length << 2, freq);
    }

}