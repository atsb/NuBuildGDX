/*
 * Copyright (c) 2002-2008 LWJGL Project
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'LWJGL' nor the names of
 *   its contributors may be used to endorse or promote products derived
 *   from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package ru.m210projects.Build.desktop.audio.nopenal;

import java.io.File;
import java.nio.IntBuffer;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.SharedLibraryLoader;
import com.sun.jna.Native;

import static com.badlogic.gdx.utils.SharedLibraryLoader.*;

/**
 * <p>
 * The AL class implements the actual creation code for linking to the native library
 * OpenAL.
 * </p>
 *
 * @author Brian Matzon <brian@matzon.dk>
 * @version $Revision$
 * $Id$
 */
public final class AL {
	/** ALCdevice instance. */
	static ALCdevice device;

	/** Current ALCcontext. */
	static ALCcontext context;

	/** Have we been created? */
	private static boolean created;
	private static SharedLibraryAL alnative;

	private AL() {
	}
	
	/**
	 * Method to create AL instance
	 *
	 * @param oalPath Path to search for OpenAL library
	 */
	private static void nCreate(String oalPath) throws Exception
	{
		if(alnative != null) return;
		try {
			alnative = (SharedLibraryAL) Native.load(oalPath, SharedLibraryAL.class);
		} catch (Throwable t) {
			throw new Exception(t.getMessage());
		}
	}

	/**
	 * @return true if AL has been created
	 */
	public static boolean isCreated() {
		return created;
	}

	/**
	 * Creates an OpenAL instance. Using this constructor will cause OpenAL to
	 * open the device using supplied device argument, and create a context using the context values
	 * supplied.
	 *
	 * @param deviceArguments Arguments supplied to native device
	 * @param contextFrequency Frequency for mixing output buffer, in units of Hz (Common values include 11025, 22050, and 44100).
	 * @param contextRefresh Refresh intervalls, in units of Hz.
	 * @param contextSynchronized Flag, indicating a synchronous context.*
	 */
	public static void create(String deviceArguments, int contextFrequency, int contextRefresh, boolean contextSynchronized)
		throws Exception {
		create(deviceArguments, contextFrequency, contextRefresh, contextSynchronized, true);
	}

	/**
	 * @param openDevice Whether to automatically open the device
	 * @see #create(String, int, int, boolean)
	 */
	public static void create(String deviceArguments, int contextFrequency, int contextRefresh, boolean contextSynchronized, boolean openDevice)
		throws Exception {

		if (created)
			throw new IllegalStateException("Only one OpenAL context may be instantiated at any one time.");

		SharedLibraryLoader loader = new SharedLibraryLoader();
		File library = null;
		try {
			if (isWindows)
				library = loader.extractFile(isARM ? "soft_oalarm64.dll" : "soft_oal.dll", null);
			else if (isMac)
				library = loader.extractFile(isARM ? "openalarm64.dylib" : "openal.dylib", null);
			else if (isLinux)
				library = loader.extractFile(isARM ? "libopenalarm64.so" : "libopenal64.so", null);
		} catch (Exception ex) {
			throw new GdxRuntimeException("Unable to extract OAL natives.", ex);
		}

		try {
			nCreate(library.getAbsolutePath());
			created = true;
			init(deviceArguments, contextFrequency, contextRefresh, contextSynchronized, openDevice);
		} catch (Exception e) {
			System.err.println("Failed to load " + library.getName() + ": " + e.getMessage());
		}

		if (!created)
			throw new Exception("Could not locate OpenAL library.");
	}

	private static void init(String deviceArguments, int contextFrequency, int contextRefresh, boolean contextSynchronized, boolean openDevice) throws Exception {
		try {
			if(openDevice) {
				device = ALC10.alcOpenDevice(deviceArguments);
				if (device == null) {
					throw new Exception("Could not open ALC device");
				}

				if (contextFrequency == -1) {
					context = ALC10.alcCreateContext(device, (IntBuffer)null);
				} else {
					context = ALC10.alcCreateContext(device,
							ALCcontext.createAttributeList(contextFrequency, contextRefresh,
								contextSynchronized ? ALC10.ALC_TRUE : ALC10.ALC_FALSE));
				}
				ALC10.alcMakeContextCurrent(context);
			}
		} catch (Exception e) {
			destroy();
			throw e;
		}
	}

	/**
	 * Creates an OpenAL instance. The empty create will cause OpenAL to
	 * open the default device, and create a context using default values.
	 * This method used to use default values that the OpenAL implementation
	 * chose but this produces unexpected results on some systems; so now
	 * it defaults to 44100Hz mixing @ 60Hz refresh.
	 */
	public static void create() throws Exception {
		create(null, 44100, 60, false);
	}

	/**
	 * Exit cleanly by calling destroy.
	 */
	public static void destroy() {
		if (context != null) {
			ALC10.alcMakeContextCurrent(null);
			ALC10.alcDestroyContext(context);
			context = null;
		}
		if (device != null) {
			ALC10.alcCloseDevice(device);
			device = null;
		}

		created = false;
	}

	/**
	 * @return handle to the default AL context.
	 */
	public static ALCcontext getContext() {
		return context;
	}

	/**
	 * @return handle to the default AL device.
	 */
	public static ALCdevice getDevice() {
		return device;
	}
	
	/**
	 * @return handle to the AL library.
	 */
	protected static SharedLibraryAL getNative()
	{
		return alnative;
	}
}
