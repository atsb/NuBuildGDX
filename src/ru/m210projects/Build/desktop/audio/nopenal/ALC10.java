package ru.m210projects.Build.desktop.audio.nopenal;

import static ru.m210projects.Build.desktop.audio.nopenal.AL.*;

/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */

import java.nio.*;

/** Native bindings to ALC 1.0 functionality. */
public class ALC10 {

    /** General tokens. */
    public static final int
        ALC_INVALID = 0xFFFFFFFF,
        ALC_FALSE   = 0x0,
        ALC_TRUE    = 0x1;

    /** Context creation attributes. */
    public static final int
        ALC_FREQUENCY = 0x1007,
        ALC_REFRESH   = 0x1008,
        ALC_SYNC      = 0x1009;

    /** Error conditions. */
    public static final int
        ALC_NO_ERROR        = 0x0,
        ALC_INVALID_DEVICE  = 0xA001,
        ALC_INVALID_CONTEXT = 0xA002,
        ALC_INVALID_ENUM    = 0xA003,
        ALC_INVALID_VALUE   = 0xA004,
        ALC_OUT_OF_MEMORY   = 0xA005;

    /** String queries. */
    public static final int
        ALC_DEFAULT_DEVICE_SPECIFIER = 0x1004,
        ALC_DEVICE_SPECIFIER         = 0x1005,
        ALC_EXTENSIONS               = 0x1006;

    /** Integer queries. */
    public static final int
        ALC_MAJOR_VERSION   = 0x1000,
        ALC_MINOR_VERSION   = 0x1001,
        ALC_ATTRIBUTES_SIZE = 0x1002,
        ALC_ALL_ATTRIBUTES  = 0x1003;

    protected ALC10() {
        throw new UnsupportedOperationException();
    }

    public static ALCdevice alcOpenDevice(String deviceSpecifier) {
        return getNative().alcOpenDevice(deviceSpecifier);
    }

    public static boolean alcCloseDevice(ALCdevice deviceHandle) {
        return getNative().alcCloseDevice(deviceHandle);
    }

    public static ALCcontext alcCreateContext(ALCdevice deviceHandle, IntBuffer attrList) {
        return getNative().alcCreateContext(deviceHandle, attrList);
    }

    public static boolean alcMakeContextCurrent(ALCcontext context) {
        return getNative().alcMakeContextCurrent(context);
    }

    public static void alcProcessContext(ALCcontext context) {
    	getNative().alcProcessContext(context);
    }

    public static void alcSuspendContext(ALCcontext context) {
    	getNative().alcSuspendContext(context);
    }

    public static void alcDestroyContext(ALCcontext context) {
    	getNative().alcDestroyContext(context);
    }

    public static ALCcontext alcGetCurrentContext() {
    	return getNative().alcGetCurrentContext();
    }

    public static ALCdevice alcGetContextsDevice(ALCcontext context) {
    	return getNative().alcGetContextsDevice(context);
    }

    public static boolean alcIsExtensionPresent(ALCdevice deviceHandle, String extName) {
    	return getNative().alcIsExtensionPresent(deviceHandle, extName);
    }

    public static long alcGetProcAddress(ALCdevice deviceHandle, String funcName) {
    	return getNative().alcGetProcAddress(deviceHandle, funcName).getLong(0);
    }

    public static int alcGetEnumValue(ALCdevice deviceHandle, String enumName) {
        return getNative().alcGetEnumValue(deviceHandle, enumName);
    }

    public static int alcGetError(ALCdevice deviceHandle) {
		return getNative().alcGetError(deviceHandle);
    }

    public static String alcGetString(ALCdevice deviceHandle, int token) {
    	return getNative().alcGetString(deviceHandle, token);
    }

    public static void alcGetIntegerv(ALCdevice deviceHandle, int token, IntBuffer dest) {
    	getNative().alcGetIntegerv(deviceHandle, token, 4, dest);
    }

    public static int alcGetInteger(ALCdevice deviceHandle, int token) {
    	int[] dest = new int[1];
    	getNative().alcGetIntegerv(deviceHandle, token, 4, dest);
    	return dest[0];
    }

    public static ALCcontext alcCreateContext(ALCdevice deviceHandle, int[] attrList) {
		return getNative().alcCreateContext(deviceHandle, attrList);
    }

    public static void alcGetIntegerv(ALCdevice deviceHandle, int token, int[] dest) {
    	getNative().alcGetIntegerv(deviceHandle, token, 4, dest);
    }
}