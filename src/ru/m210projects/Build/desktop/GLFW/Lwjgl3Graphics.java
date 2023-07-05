package ru.m210projects.Build.desktop.GLFW;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWFramebufferSizeCallback;
import org.lwjgl.glfw.GLFWGammaRamp;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWVidMode.Buffer;
import org.lwjgl.glfw.GLFWWindowFocusCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Cursor;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3NativesLoader;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Window;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Cursor.SystemCursor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.glutils.GLVersion;
import com.badlogic.gdx.graphics.glutils.HdpiMode;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.SharedLibraryLoader;

import ru.m210projects.Build.Architecture.BuildConfiguration;
import ru.m210projects.Build.Architecture.BuildFrame.FrameType;
import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.Architecture.BuildGraphics;

public class Lwjgl3Graphics extends BuildGraphics {

	private int samples = 0;
	private int r = 8, g = 8, b = 8, a = 8; // 32bit

	private static GLVersion glVersion;
	private BufferFormat bufferFormat;
	protected long windowHandle;

	private volatile int backBufferWidth;
	private volatile int backBufferHeight;
	private volatile int logicalWidth;
	private volatile int logicalHeight;
	private int windowPosXBeforeFullscreen;
	private int windowPosYBeforeFullscreen;
	private DisplayMode displayModeBeforeFullscreen = null;

	private IntBuffer tmpBuffer = BufferUtils.createIntBuffer(1);
	private IntBuffer tmpBuffer2 = BufferUtils.createIntBuffer(1);

	private GLFWErrorCallback errorCallback;

	private BuildConfiguration config;
	private int rate;
	private boolean wasResized = false;
	private boolean isActive = false;

	public Lwjgl3Graphics(BuildConfiguration config) {
		Lwjgl3NativesLoader.load();
		this.config = config;
	}

	private final GLFWWindowFocusCallback focusCallback = new GLFWWindowFocusCallback() {
		@Override
		public void invoke(long windowHandle, final boolean focused) {
			BuildGdx.app.postRunnable(new Runnable() {
				@Override
				public void run() {
					isActive = focused;
				}
			});
		}
	};
	
	private Runnable resetPollingStates;
	protected void setPollingStatesCallback(Runnable handleCallback)
	{
		this.resetPollingStates = handleCallback;
	}

	private GLFWFramebufferSizeCallback resizeCallback = new GLFWFramebufferSizeCallback() {
		@Override
		public void invoke(long windowHandle, final int width, final int height) {
			updateFramebufferInfo();
			wasResized = true;
		}
	};

	private long createGlfwWindow(BuildConfiguration config) {
		GLFW.glfwDefaultWindowHints();
		GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
		GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, config.resizable ? GLFW.GLFW_TRUE : GLFW.GLFW_FALSE);
		GLFW.glfwWindowHint(GLFW.GLFW_MAXIMIZED, GLFW.GLFW_FALSE);

		GLFW.glfwWindowHint(GLFW.GLFW_RED_BITS, r);
		GLFW.glfwWindowHint(GLFW.GLFW_GREEN_BITS, g);
		GLFW.glfwWindowHint(GLFW.GLFW_BLUE_BITS, b);
		GLFW.glfwWindowHint(GLFW.GLFW_ALPHA_BITS, a);
		GLFW.glfwWindowHint(GLFW.GLFW_STENCIL_BITS, config.stencil);
		GLFW.glfwWindowHint(GLFW.GLFW_DEPTH_BITS, config.depth);
		GLFW.glfwWindowHint(GLFW.GLFW_SAMPLES, samples);

		if (config.useGL30) {
			int gles30ContextMajorVersion = 3;
			int gles30ContextMinorVersion = 2;

			GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, gles30ContextMajorVersion);
			GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, gles30ContextMinorVersion);
			if (SharedLibraryLoader.isMac) {
				// hints mandatory on OS X for GL 3.2+ context creation, but fail on Windows if
				// the
				// WGL_ARB_create_context extension is not available
				// see: http://www.glfw.org/docs/latest/compat.html
				GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GLFW.GLFW_TRUE);
				GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
			}
		}

		long windowHandle = 0;
		if (!config.fullscreen) {
			GLFW.glfwWindowHint(GLFW.GLFW_DECORATED, config.undecorated ? GLFW.GLFW_FALSE : GLFW.GLFW_TRUE);
			windowHandle = GLFW.glfwCreateWindow(config.width, config.height, config.title, 0, 0);
			
			rate = getDesktopDisplayMode().refreshRate;
		} else {
			DisplayMode bestMode = null;
			for (DisplayMode mode : getDisplayLwjglModes()) {
				if (mode.width == config.width && mode.height == config.height) {
					if (bestMode == null || bestMode.refreshRate < mode.refreshRate) {
						bestMode = mode;
					}
				}
			}

			if (bestMode == null)
				bestMode = this.getDesktopDisplayMode();

			GLFW.glfwWindowHint(GLFW.GLFW_REFRESH_RATE, bestMode.refreshRate);
			windowHandle = GLFW.glfwCreateWindow(bestMode.width, bestMode.height, config.title,
					((Lwjgl3DisplayMode) bestMode).getMonitor(), 0);
			rate = bestMode.refreshRate;
		}
		if (windowHandle == 0)
			throw new GdxRuntimeException("Couldn't create window");
		
		if (!config.fullscreen) {
			if (config.x == -1 && config.y == -1) {
				GLFWVidMode vidMode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
				GLFW.glfwSetWindowPos(windowHandle, vidMode.width() / 2 - config.width / 2,
						vidMode.height() / 2 - config.height / 2);
			} else {
				GLFW.glfwSetWindowPos(windowHandle, config.x, config.y);
			}
		}

		Array<String> iconPaths = config.getIconPaths();
		if (iconPaths.size > 0) {
			GLFWImage.Buffer buffer = GLFWImage.malloc(iconPaths.size);

			for (int i = 0, n = iconPaths.size; i < n; i++) {
				Pixmap pixmap = new Pixmap(
						BuildGdx.files.getFileHandle(iconPaths.get(i), config.getIconFileTypes().get(i)));
				if (pixmap.getFormat() != Format.RGBA8888) {
					Pixmap rgba = new Pixmap(pixmap.getWidth(), pixmap.getHeight(), Format.RGBA8888);
					rgba.setBlending(Pixmap.Blending.None);
					rgba.drawPixmap(pixmap, 0, 0);
					pixmap.dispose();
					pixmap = rgba;
				}

				GLFWImage icon = GLFWImage.malloc();
				icon.set(pixmap.getWidth(), pixmap.getHeight(), pixmap.getPixels());
				buffer.put(icon);

				icon.free();
				pixmap.dispose();
			}

			buffer.position(0);
			GLFW.glfwSetWindowIcon(windowHandle, buffer);
			buffer.free();
		}

		GLFW.glfwSetWindowFocusCallback(windowHandle, focusCallback);
		GLFW.glfwSetFramebufferSizeCallback(windowHandle, resizeCallback);

		GLFW.glfwMakeContextCurrent(windowHandle);
		GL.createCapabilities();

		initiateGL();
		return windowHandle;
	}

	private void initiateGL() {
		String versionString = GL11.glGetString(GL11.GL_VERSION);
		String vendorString = GL11.glGetString(GL11.GL_VENDOR);
		String rendererString = GL11.glGetString(GL11.GL_RENDERER);
		glVersion = new GLVersion(Application.ApplicationType.Desktop, versionString, vendorString, rendererString);
	}

	private void updateFramebufferInfo() {
		GLFW.glfwGetFramebufferSize(windowHandle, tmpBuffer, tmpBuffer2);
		this.backBufferWidth = tmpBuffer.get(0);
		this.backBufferHeight = tmpBuffer2.get(0);
		GLFW.glfwGetWindowSize(windowHandle, tmpBuffer, tmpBuffer2);
		Lwjgl3Graphics.this.logicalWidth = tmpBuffer.get(0);
		Lwjgl3Graphics.this.logicalHeight = tmpBuffer2.get(0);
		bufferFormat = new BufferFormat(r, g, b, a, config.depth, config.stencil, samples, false);
	}

	@Override
	protected void init() throws Exception {
		if (!GLFW.glfwInit()) 
			throw new GdxRuntimeException("Unable to initialize GLFW");

		errorCallback = GLFWErrorCallback.createPrint(System.err);
		GLFW.glfwSetErrorCallback(errorCallback);
		GLFW.glfwInitHint(GLFW.GLFW_JOYSTICK_HAT_BUTTONS, GLFW.GLFW_FALSE);

		windowHandle = createGlfwWindow(config);

		gl10 = new Lwjgl3GL10();
		if (config.useGL30) {
			Class<?> GL30 = Class.forName("com.badlogic.gdx.backends.lwjgl3.Lwjgl3GL30");
			Constructor<?> LwjglGL30 = GL30.getDeclaredConstructor();
			LwjglGL30.setAccessible(true);
			gl30 = (GL30) LwjglGL30.newInstance();
			this.gl20 = this.gl30;
		} else {
			Class<?> GL20Class = Class.forName("com.badlogic.gdx.backends.lwjgl3.Lwjgl3GL20");
			Constructor<?> LwjglGL20 = GL20Class.getDeclaredConstructor();
			LwjglGL20.setAccessible(true);
			gl20 = (GL20) LwjglGL20.newInstance();
			this.gl30 = null;
		}
		updateFramebufferInfo();
		
		setUndecorated(config.borderless);
		GLFW.glfwShowWindow(windowHandle);
	}

	@Override
	public GraphicsType getType() {
		return GraphicsType.LWJGL3;
	}

	@Override
	protected int getRefreshRate() {
		return rate;
	}

	@Override
	protected boolean isDirty() {
		return false;
	}

	@Override
	protected void sync(int fps) {
		GLFWSync.sync(fps);
	}

	private static final long NANOS_IN_SECOND = 1000L * 1000L;
	public void sleepAtLeast(long millis) throws InterruptedException {
		double t0 = GLFW.glfwGetTime();
		long millisLeft = millis;
		while (millisLeft > 0) {
			Thread.sleep(millisLeft);
			millisLeft -= ((GLFW.glfwGetTime() - t0) * NANOS_IN_SECOND);
		}
	}

//	public void sleepAtLeast(long millis) throws InterruptedException {
//		long t0 = System.currentTimeMillis();
//		long millisLeft = millis;
//		while (millisLeft > 0) {
//			Thread.sleep(millisLeft);
//			long t1 = System.currentTimeMillis();
//			millisLeft = millis - (t1 - t0);
//		}
//	}
//		}
//	};

	@Override
	protected void update() {
		GLFW.glfwSwapBuffers(windowHandle);
		GLFW.glfwPollEvents();
	}

	@Override
	protected void updateSize(int width, int height) {
		if (BuildGdx.gl != null)
			BuildGdx.gl.glViewport(0, 0, width, height);
	}

	@Override
	protected boolean wasResized() {
		boolean out = wasResized;
		wasResized = false;
		return out;
	}

	@Override
	protected int getX() {
		GLFW.glfwGetWindowPos(windowHandle, tmpBuffer, tmpBuffer2);
		return tmpBuffer.get(0);
	}

	@Override
	protected int getY() {
		GLFW.glfwGetWindowPos(windowHandle, tmpBuffer, tmpBuffer2);
		return tmpBuffer2.get(0);
	}

	@Override
	protected boolean isActive() {
		return isActive;
	}

	@Override
	protected boolean isCloseRequested() {
		return GLFW.glfwWindowShouldClose(windowHandle);
	}

	@Override
	protected void dispose() {
		GLFW.glfwSetErrorCallback(null);
		GLFW.glfwSetWindowFocusCallback(windowHandle, null);
		GLFW.glfwSetFramebufferSizeCallback(windowHandle, null);
		GLFW.glfwDestroyWindow(windowHandle);

		this.focusCallback.free();
		this.resizeCallback.free();
		this.errorCallback.free();
		
		try {
			Method disposeCursor = Lwjgl3Cursor.class.getDeclaredMethod("disposeSystemCursors");
			disposeCursor.setAccessible(true);
			disposeCursor.invoke(disposeCursor);
		} catch(Exception e) {
			e.printStackTrace();
		}

		GLFW.glfwTerminate();
	}

	@Override
	public int getWidth() {
		if (config.hdpiMode == HdpiMode.Pixels) {
			return backBufferWidth;
		} else {
			return logicalWidth;
		}
	}

	@Override
	public int getHeight() {
		if (config.hdpiMode == HdpiMode.Pixels) {
			return backBufferHeight;
		} else {
			return logicalHeight;
		}
	}

	@Override
	public void setFramesPerSecond(int fps) {
		config.foregroundFPS = fps;
		config.backgroundFPS = fps;
	}

	@Override
	public FrameType getFrameType() {
		return FrameType.GL;
	}

	@Override
	public GLVersion getGLVersion() {
		return glVersion;
	}

	@Override
	public boolean supportsDisplayModeChange() {
		return true;
	}

	@Override
	public Monitor getPrimaryMonitor() {
		return toLwjgl3Monitor(GLFW.glfwGetPrimaryMonitor());
	}

	@Override
	public Monitor getMonitor() {
		Monitor[] monitors = getMonitors();
		Monitor result = monitors[0];

		GLFW.glfwGetWindowPos(windowHandle, tmpBuffer, tmpBuffer2);
		int windowX = tmpBuffer.get(0);
		int windowY = tmpBuffer2.get(0);
		GLFW.glfwGetWindowSize(windowHandle, tmpBuffer, tmpBuffer2);
		int windowWidth = tmpBuffer.get(0);
		int windowHeight = tmpBuffer2.get(0);
		int overlap;
		int bestOverlap = 0;

		for (Monitor monitor : monitors) {
			DisplayMode mode = getDisplayMode(monitor);

			overlap = Math.max(0,
					Math.min(windowX + windowWidth, monitor.virtualX + mode.width)
							- Math.max(windowX, monitor.virtualX))
					* Math.max(0, Math.min(windowY + windowHeight, monitor.virtualY + mode.height)
							- Math.max(windowY, monitor.virtualY));

			if (bestOverlap < overlap) {
				bestOverlap = overlap;
				result = monitor;
			}
		}
		return result;
	}

	@Override
	public Monitor[] getMonitors() {
		PointerBuffer glfwMonitors = GLFW.glfwGetMonitors();
		Monitor[] monitors = new Monitor[glfwMonitors.limit()];
		for (int i = 0; i < glfwMonitors.limit(); i++) {
			monitors[i] = toLwjgl3Monitor(glfwMonitors.get(i));
		}
		return monitors;
	}

	@Override
	public DisplayMode[] getDisplayModes() {
		return getDisplayLwjglModes(getMonitor());
	}

	@Override
	public DisplayMode[] getDisplayModes(Monitor monitor) {
		return getDisplayLwjglModes(monitor);
	}

	@Override
	public DisplayMode getDisplayMode() {
		return getDisplayMode(getMonitor());
	}

	@Override
	public DisplayMode getDesktopDisplayMode() {
		return getDisplayLwjglMode();
	}

	@Override
	public DisplayMode getDisplayMode(Monitor monitor) {
		return getDisplayLwjglMode(monitor);
	}

	@Override
	public boolean setFullscreenMode(DisplayMode displayMode) {
		if(resetPollingStates != null)
			resetPollingStates.run();
		Lwjgl3DisplayMode newMode = (Lwjgl3DisplayMode) displayMode;
		if (isFullscreen()) {
			Lwjgl3DisplayMode currentMode = (Lwjgl3DisplayMode) getDisplayMode();
			if (currentMode.getMonitor() == newMode.getMonitor() && currentMode.refreshRate == newMode.refreshRate) {
				// same monitor and refresh rate
				GLFW.glfwSetWindowSize(windowHandle, newMode.width, newMode.height);
			} else {
				// different monitor and/or refresh rate
				GLFW.glfwSetWindowMonitor(windowHandle, newMode.getMonitor(), 0, 0, newMode.width, newMode.height,
						newMode.refreshRate);
			}
		} else {
			// store window position so we can restore it when switching from fullscreen to
			// windowed later
			storeCurrentWindowPositionAndDisplayMode();

			// switch from windowed to fullscreen
			GLFW.glfwSetWindowMonitor(windowHandle, newMode.getMonitor(), 0, 0, newMode.width, newMode.height,
					newMode.refreshRate);
		}
		
		rate = newMode.refreshRate;
		updateFramebufferInfo();
		return true;
	}

	@Override
	public boolean setWindowedMode(int width, int height) {
		if(resetPollingStates != null)
			resetPollingStates.run();
		if (!isFullscreen()) {
			GLFW.glfwSetWindowSize(windowHandle, width, height);
		} else {
			if (displayModeBeforeFullscreen == null) {
				storeCurrentWindowPositionAndDisplayMode();
			}

			GLFW.glfwSetWindowMonitor(windowHandle, 0, windowPosXBeforeFullscreen, windowPosYBeforeFullscreen, width,
					height, displayModeBeforeFullscreen.refreshRate);
		}
		
		rate = getDesktopDisplayMode().refreshRate;
		updateFramebufferInfo();
		return true;
	}

	@Override
	public void setTitle(String title) {
		if (title == null)
			title = "";
		GLFW.glfwSetWindowTitle(windowHandle, title);
	}

	@Override
	public void setUndecorated(boolean undecorated) {
		this.config.undecorated = undecorated;
		GLFW.glfwSetWindowAttrib(windowHandle, GLFW.GLFW_DECORATED, undecorated ? GLFW.GLFW_FALSE : GLFW.GLFW_TRUE);
	}

	@Override
	public void setResizable(boolean resizable) {
		this.config.resizable = resizable;
		GLFW.glfwSetWindowAttrib(windowHandle, GLFW.GLFW_RESIZABLE, resizable ? GLFW.GLFW_TRUE : GLFW.GLFW_FALSE);
	}

	@Override
	public void setVSync(boolean vsync) {
		GLFW.glfwSwapInterval(vsync ? 1 : 0);
	}

	@Override
	public BufferFormat getBufferFormat() {
		return bufferFormat;
	}

	@Override
	public boolean supportsExtension(String extension) {
		return GLFW.glfwExtensionSupported(extension);
	}

	@Override
	public boolean isFullscreen() {
		return GLFW.glfwGetWindowMonitor(windowHandle) != 0;
	}

	@Override
	public Cursor newCursor(Pixmap pixmap, int xHotspot, int yHotspot) {
		try {
			Constructor<?> Lwjgl3Cursor = Class.forName("com.badlogic.gdx.backends.lwjgl3.Lwjgl3Cursor").getDeclaredConstructor(Lwjgl3Window.class, Pixmap.class, int.class, int.class);
			Lwjgl3Cursor.setAccessible(true);
			return (Cursor) Lwjgl3Cursor.newInstance(null, pixmap, xHotspot, yHotspot);
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}

	@Override
	public void setCursor(Cursor cursor) {
		try {
			Field glfwCursor = Lwjgl3Cursor.class.getDeclaredField("glfwCursor");
			glfwCursor.setAccessible(true);
			GLFW.glfwSetCursor(windowHandle, (Long) glfwCursor.get(cursor));
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setSystemCursor(SystemCursor systemCursor) {
		try {
			Method setSystemCursor = Lwjgl3Cursor.class.getDeclaredMethod("setSystemCursor", long.class, SystemCursor.class);
			setSystemCursor.setAccessible(true);
			setSystemCursor.invoke(null, windowHandle, systemCursor);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public Object extra(Option opt, Object... obj) {
		switch(opt)
		{
		case GLSetConfiguration:
			if(obj.length < 3) 
				return false;
			float gamma = (Float) obj[0];
			float brightness = (Float) obj[1];
			float contrast = (Float) obj[2];
			
			return setDisplayConfiguration(gamma, brightness, contrast);
		case GLDefConfiguration:
			return setDisplayConfiguration(1.0f, 0.0f, 1.0f);
		default:
			throw new UnsupportedOperationException("not implemented"); 
		}
	}

	private boolean setDisplayConfiguration(float gamma, float brightness, float contrast) {
		try {
			if ( brightness < -1.0f || brightness > 1.0f )
				throw new IllegalArgumentException("Invalid brightness value");
			if ( contrast < 0.0f )
				throw new IllegalArgumentException("Invalid contrast value");
			
			long monitorHandle = ((Lwjgl3Monitor) getMonitor()).monitorHandle;
			GLFWGammaRamp ramp = GLFW.glfwGetGammaRamp(monitorHandle);
			int rampSize = ramp.size();
			if ( rampSize == 0 ) 
				throw new Exception("Display configuration not supported");

			for ( int i = 0; i < rampSize; i++ ) {
				float intensity = (float) i / (rampSize - 1);
				// apply gamma
				float rampEntry = (float) Math.pow(intensity, gamma);
				// apply brightness
				rampEntry += brightness;
				// apply contrast
				rampEntry = (rampEntry - 0.5f) * contrast + 0.5f;
				// Clamp entry to [0, 1]
				if ( rampEntry > 1.0f )
					rampEntry = 1.0f;
				else if ( rampEntry < 0.0f )
					rampEntry = 0.0f;
				
				short value = (short) (rampEntry * 65535);
				
				ramp.red().put(i, value);
				ramp.green().put(i, value);
				ramp.blue().put(i, value);
			}
			GLFW.glfwSetGammaRamp(monitorHandle, ramp);
		} catch (Exception e) { e.printStackTrace(); return false; }
		
		return true;
	}

	@Override
	public float getPpiX() {
		return getPpcX() / 0.393701f;
	}

	@Override
	public float getPpiY() {
		return getPpcY() / 0.393701f;
	}

	@Override
	public float getPpcX() {
		Lwjgl3Monitor monitor = (Lwjgl3Monitor) getMonitor();
		GLFW.glfwGetMonitorPhysicalSize(monitor.monitorHandle, tmpBuffer, tmpBuffer2);
		int sizeX = tmpBuffer.get(0);
		DisplayMode mode = getDisplayMode();
		return mode.width / (float) sizeX * 10;
	}

	@Override
	public float getPpcY() {
		Lwjgl3Monitor monitor = (Lwjgl3Monitor) getMonitor();
		GLFW.glfwGetMonitorPhysicalSize(monitor.monitorHandle, tmpBuffer, tmpBuffer2);
		int sizeY = tmpBuffer2.get(0);
		DisplayMode mode = getDisplayMode();
		return mode.height / (float) sizeY * 10;
	}

	private void storeCurrentWindowPositionAndDisplayMode() {
		windowPosXBeforeFullscreen = getX();
		windowPosYBeforeFullscreen = getY();
		displayModeBeforeFullscreen = getDisplayMode();
	}

	public static class Lwjgl3DisplayMode extends DisplayMode {
		final long monitorHandle;

		Lwjgl3DisplayMode(long monitor, int width, int height, int refreshRate, int bitsPerPixel) {
			super(width, height, refreshRate, bitsPerPixel);
			this.monitorHandle = monitor;
		}

		public long getMonitor() {
			return monitorHandle;
		}
	}

	public static class Lwjgl3Monitor extends Monitor {
		final long monitorHandle;

		Lwjgl3Monitor(long monitor, int virtualX, int virtualY, String name) {
			super(virtualX, virtualY, name);
			this.monitorHandle = monitor;
		}

		public long getMonitorHandle() {
			return monitorHandle;
		}
	}

	private Lwjgl3Monitor toLwjgl3Monitor(long glfwMonitor) {
		GLFW.glfwGetMonitorPos(glfwMonitor, tmpBuffer, tmpBuffer2);
		int virtualX = tmpBuffer.get(0);
		int virtualY = tmpBuffer2.get(0);
		String name = GLFW.glfwGetMonitorName(glfwMonitor);
		return new Lwjgl3Monitor(glfwMonitor, virtualX, virtualY, name);
	}

	/**
	 * @return the currently active {@link DisplayMode} of the primary monitor
	 */
	private DisplayMode getDisplayLwjglMode() {
		GLFWVidMode videoMode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
		return new Lwjgl3Graphics.Lwjgl3DisplayMode(GLFW.glfwGetPrimaryMonitor(), videoMode.width(), videoMode.height(),
				videoMode.refreshRate(), videoMode.redBits() + videoMode.greenBits() + videoMode.blueBits());
	}

	/**
	 * @return the currently active {@link DisplayMode} of the given monitor
	 */
	private DisplayMode getDisplayLwjglMode(Monitor monitor) {
		GLFWVidMode videoMode = GLFW.glfwGetVideoMode(((Lwjgl3Monitor) monitor).monitorHandle);
		return new Lwjgl3Graphics.Lwjgl3DisplayMode(((Lwjgl3Monitor) monitor).monitorHandle, videoMode.width(),
				videoMode.height(), videoMode.refreshRate(),
				videoMode.redBits() + videoMode.greenBits() + videoMode.blueBits());
	}

	/**
	 * @return the available {@link DisplayMode}s of the primary monitor
	 */
	private DisplayMode[] getDisplayLwjglModes() { 
		Buffer videoModes = GLFW.glfwGetVideoModes(GLFW.glfwGetPrimaryMonitor());
		DisplayMode[] result = new DisplayMode[videoModes.limit()];
		for (int i = 0; i < result.length; i++) {
			GLFWVidMode videoMode = videoModes.get(i);
			result[i] = new Lwjgl3Graphics.Lwjgl3DisplayMode(GLFW.glfwGetPrimaryMonitor(), videoMode.width(), videoMode.height(),
					videoMode.refreshRate(), videoMode.redBits() + videoMode.greenBits() + videoMode.blueBits());
		}
		return result;
	}
	
	/**
	 * @return the available {@link DisplayMode}s of the given {@link Monitor}
	 */
	private DisplayMode[] getDisplayLwjglModes(Monitor monitor) {
		Buffer videoModes = GLFW.glfwGetVideoModes(((Lwjgl3Monitor) monitor).monitorHandle);
		DisplayMode[] result = new DisplayMode[videoModes.limit()];
		for (int i = 0; i < result.length; i++) {
			GLFWVidMode videoMode = videoModes.get(i);
			result[i] = new Lwjgl3Graphics.Lwjgl3DisplayMode(((Lwjgl3Monitor) monitor).monitorHandle, videoMode.width(),
					videoMode.height(), videoMode.refreshRate(),
					videoMode.redBits() + videoMode.greenBits() + videoMode.blueBits());
		}
		return result;
	}

}
