/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 * MACHINE GENERATED FILE, DO NOT EDIT
 */
package ru.m210projects.Build.desktop.audio.nopenal;

import static ru.m210projects.Build.desktop.audio.nopenal.AL.*;

/**
 * Native bindings to the <a target="_blank" href="http://kcat.strangesoft.net/openal-extensions/EXT_thread_local_context.txt">EXT_thread_local_context</a> extension.
 * 
 * <p>This extension introduces the concept of a current thread-local context, with each thread able to have its own current context. The current context is
 * what the al- functions work on, effectively allowing multiple threads to independently drive separate OpenAL playback contexts.</p>
 */
public class EXTThreadLocalContext {

    protected EXTThreadLocalContext() {
        throw new UnsupportedOperationException();
    }

    // --- [ alcSetThreadContext ] ---

    /**
     * Makes a context current with respect to OpenAL operation on the current thread. The context parameter can be {@code NULL} or a valid context pointer. Using
     * {@code NULL} results in no thread-specific context being current in the calling thread, which is useful when shutting OpenAL down.
     *
     * @param context the context to make current
     */

    public static boolean alcSetThreadContext(ALCcontext context) {
		return getNative().alcSetThreadContext(context);
    }

    // --- [ alcGetThreadContext ] ---

    /** Retrieves a handle to the thread-specific context of the calling thread. This function will return {@code NULL} if no thread-specific context is set. */
    public static ALCcontext alcGetThreadContext() {
		return getNative().alcGetThreadContext();
    }
}