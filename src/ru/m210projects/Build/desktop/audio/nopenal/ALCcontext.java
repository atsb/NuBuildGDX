package ru.m210projects.Build.desktop.audio.nopenal;

import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;

import com.sun.jna.PointerType;

public class ALCcontext extends PointerType {
	
	/**
	 * Creates an attribute list in a ByteBuffer
	 * @param contextFrequency Frequency to add
	 * @param contextRefresh Refresh rate to add
	 * @param contextSynchronized Whether to synchronize the context
	 * @return attribute list
	 */
	static IntBuffer createAttributeList(int contextFrequency, int contextRefresh, int contextSynchronized) {
		IntBuffer attribList = BufferUtils.createIntBuffer(7);

		attribList.put(ALC10.ALC_FREQUENCY);
		attribList.put(contextFrequency);
		attribList.put(ALC10.ALC_REFRESH);
		attribList.put(contextRefresh);
		attribList.put(ALC10.ALC_SYNC);
		attribList.put(contextSynchronized);
		attribList.put(0); //terminating int

		return attribList;
	}
}