// This file has been modified from LibGDX's original release
// by Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// BuildGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// BuildGDX is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with BuildGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.Build.desktop.Lwjgl;

import java.lang.reflect.Constructor;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.PixelFormat;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.backends.lwjgl.LwjglNativesLoader;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Cursor.SystemCursor;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.glutils.GLVersion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.SharedLibraryLoader;

import ru.m210projects.Build.Architecture.BuildConfiguration;
import ru.m210projects.Build.Architecture.BuildFrame.FrameType;
import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.Architecture.BuildGraphics;

public class LwjglGraphics extends BuildGraphics {

	/** The suppored OpenGL extensions */
	private static Array<String> extensions;
	private static GLVersion glVersion;
	private final BuildConfiguration config;
	private BufferFormat bufferFormat = new BufferFormat(8, 8, 8, 8, 16, 8, 0, false);
	private boolean usingGL30;
	private int rate;

	public LwjglGraphics(BuildConfiguration config)
	{
		LwjglNativesLoader.load();
		this.config = config;
	}

	@Override
	protected int getRefreshRate() {
		return rate;
	}

	@Override
	protected boolean isDirty() {
		return Display.isDirty();
	}

	@Override
	protected void sync(int fps) {
		Display.sync(fps);
	}

	@Override
	protected void init() throws Exception {
		if (config.useHDPI) {
			System.setProperty("org.lwjgl.opengl.Display.enableHighDPI", "true");
		}

		boolean displayCreated;
		if(!config.fullscreen) {
			displayCreated = setWindowedMode(config.width, config.height);
		} else {
			DisplayMode bestMode = null;
			for(DisplayMode mode: getDisplayModes()) {
				if(mode.width == config.width && mode.height == config.height) {
					if(bestMode == null || bestMode.refreshRate < mode.refreshRate) {
						bestMode = mode;
					}
				}
			}

			if(bestMode == null) {
				bestMode = this.getDesktopDisplayMode();
			}
			displayCreated = setFullscreenMode(bestMode);
		}
		if (!displayCreated) {
			throw new GdxRuntimeException("Couldn't set display mode " + config.width + "x" + config.height + ", fullscreen: " + config.fullscreen);
		}

		Array<String> iconPaths = config.getIconPaths();
		if (iconPaths.size > 0) {
			ByteBuffer[] icons = new ByteBuffer[iconPaths.size];
			for (int i = 0, n = iconPaths.size; i < n; i++) {
				Pixmap pixmap = new Pixmap(BuildGdx.files.getFileHandle(iconPaths.get(i), config.getIconFileTypes().get(i)));
				if (pixmap.getFormat() != Format.RGBA8888) {
					Pixmap rgba = new Pixmap(pixmap.getWidth(), pixmap.getHeight(), Format.RGBA8888);
					rgba.drawPixmap(pixmap, 0, 0);
					pixmap.dispose();
					pixmap = rgba;
				}
				icons[i] = ByteBuffer.allocateDirect(pixmap.getPixels().limit());
				icons[i].put(pixmap.getPixels()).flip();
				pixmap.dispose();
			}
			Display.setIcon(icons);
		}

		Display.setTitle(config.title);
		Display.setResizable(config.resizable);
		Display.setInitialBackground(0, 0, 0);
		Display.setLocation(config.x, config.y);
		setUndecorated(config.borderless);

		int gles30ContextMajorVersion = 3;
		int gles30ContextMinorVersion = 2;

		createDisplayPixelFormat(config.useGL30, gles30ContextMajorVersion, gles30ContextMinorVersion);
		initiateGL();
	}

	private void initiateGL() throws Exception {
		extractVersion();
		extractExtensions();

		gl10 = new LwjglGL10();
		if (usingGL30) {
			Class<?> GL30 = Class.forName("com.badlogic.gdx.backends.lwjgl.LwjglGL30");
			Constructor<?> LwjglGL30 = GL30.getDeclaredConstructor();
			LwjglGL30.setAccessible(true);
			gl30 = (GL30) LwjglGL30.newInstance();
			gl20 = gl30;
		} else {
			Class<?> GL20Class = Class.forName("com.badlogic.gdx.backends.lwjgl.LwjglGL20");
			Constructor<?> LwjglGL20 = GL20Class.getDeclaredConstructor();
			LwjglGL20.setAccessible(true);
			gl20 = (GL20) LwjglGL20.newInstance();
		}
	}

	private void extractVersion () {
		String versionString = org.lwjgl.opengl.GL11.glGetString(GL11.GL_VERSION);
		String vendorString = org.lwjgl.opengl.GL11.glGetString(GL11.GL_VENDOR);
		String rendererString = org.lwjgl.opengl.GL11.glGetString(GL11.GL_RENDERER);
		glVersion = new GLVersion(Application.ApplicationType.Desktop, versionString, vendorString, rendererString);
	}

	private void extractExtensions () {
		extensions = new Array<String>();
		if (glVersion.isVersionEqualToOrHigher(3, 2)) {
			int numExtensions = GL11.glGetInteger(GL30.GL_NUM_EXTENSIONS);
			for (int i = 0; i < numExtensions; ++i)
				extensions.add(org.lwjgl.opengl.GL30.glGetStringi(GL20.GL_EXTENSIONS, i));
		} else {
			extensions.addAll(org.lwjgl.opengl.GL11.glGetString(GL20.GL_EXTENSIONS).split(" "));
		}
	}

	private void createDisplayPixelFormat (boolean useGL30, int gles30ContextMajor, int gles30ContextMinor) {
		try {
			int samples = 0;
			int r = 8, g = 8, b = 8, a = 8; //32bit
			if (useGL30) {
				ContextAttribs context = new ContextAttribs(gles30ContextMajor, gles30ContextMinor).withForwardCompatible(false).withProfileCore(true);
				try {
					Display.create(new PixelFormat(r + g + b, a, config.depth, config.stencil, samples), context);
				} catch (Exception e) {
					System.out.println("GLGraphics: OpenGL " + gles30ContextMajor + "." + gles30ContextMinor + "+ core profile (GLES 3.0) not supported.");
					createDisplayPixelFormat(false, gles30ContextMajor, gles30ContextMinor);
					return;
				}
				System.out.println("GLGraphics: created OpenGL " + gles30ContextMajor + "." + gles30ContextMinor + "+ core profile (GLES 3.0) context. This is experimental!");
				usingGL30 = true;
			} else {
				Display.create(new PixelFormat(r + g + b, a, config.depth, config.stencil, samples));
				usingGL30 = false;
			}
			bufferFormat = new BufferFormat(r, g, b, a, config.depth, config.stencil, samples, false);
		} catch (Exception ex) {
			Display.destroy();
			try {
				Thread.sleep(200);
			} catch (InterruptedException ignored) {
			}
			try {
				Display.create(new PixelFormat(0, 16, 8));
				if (getDesktopDisplayMode().bitsPerPixel == 16) {
					bufferFormat = new BufferFormat(5, 6, 5, 0, 16, 8, 0, false);
				}
				if (getDesktopDisplayMode().bitsPerPixel == 24) {
					bufferFormat = new BufferFormat(8, 8, 8, 0, 16, 8, 0, false);
				}
				if (getDesktopDisplayMode().bitsPerPixel == 32) {
					bufferFormat = new BufferFormat(8, 8, 8, 8, 16, 8, 0, false);
				}
			} catch (Exception ex2) {
				Display.destroy();
				try {
					Thread.sleep(200);
				} catch (InterruptedException ignored) {
				}
				try {
					Display.create(new PixelFormat());
				} catch (Exception ex3) {
					throw new GdxRuntimeException("OpenGL is not supported by the video driver.", ex3);
				}
				if (getDesktopDisplayMode().bitsPerPixel == 16) {
					bufferFormat = new BufferFormat(5, 6, 5, 0, 8, 0, 0, false);
				}
				if (getDesktopDisplayMode().bitsPerPixel == 24) {
					bufferFormat = new BufferFormat(8, 8, 8, 0, 8, 0, 0, false);
				}
				if (getDesktopDisplayMode().bitsPerPixel == 32) {
					bufferFormat = new BufferFormat(8, 8, 8, 8, 8, 0, 0, false);
				}
			}
		}
	}

	@Override
	protected void update() {
		Display.update(false);
	}

	@Override
	protected void updateSize(int width, int height) {
		if (BuildGdx.gl != null)
			BuildGdx.gl.glViewport(0, 0, width, height);
	}

	@Override
	protected boolean wasResized() {
		return Display.wasResized();
	}

	@Override
	protected int getX() {
		return Display.getX();
	}

	@Override
	protected int getY() {
		return Display.getY();
	}

	@Override
	protected boolean isActive() {
		return Display.isActive();
	}

	@Override
	protected boolean isCloseRequested() {
		return Display.isCloseRequested();
	}

	@Override
	protected void dispose() {
		// Workaround for bug in LWJGL whereby resizable state is lost on DisplayMode change
		if(Display.isCreated()) {
			Display.setResizable(false);
			Display.destroy();
		}
	}

	@Override
	public int getWidth() {
		return (int)(Display.getWidth() * Display.getPixelScaleFactor());
	}

	@Override
	public int getHeight() {
		return (int)(Display.getHeight() * Display.getPixelScaleFactor());
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

	private class LwjglMonitor extends Monitor {
		protected LwjglMonitor (int virtualX, int virtualY, String name) {
			super(virtualX, virtualY, name);
		}
	}

	@Override
	public Monitor getPrimaryMonitor() {
		return new LwjglMonitor(0, 0, "Primary Monitor");
	}

	@Override
	public Monitor getMonitor() {
		return getPrimaryMonitor();
	}

	@Override
	public Monitor[] getMonitors() {
		return new Monitor[] { getPrimaryMonitor() };
	}

	@Override
	public DisplayMode[] getDisplayModes() {
		try {
			org.lwjgl.opengl.DisplayMode[] availableDisplayModes = Display.getAvailableDisplayModes();
			DisplayMode[] modes = new DisplayMode[availableDisplayModes.length];

			int idx = 0;
			for (org.lwjgl.opengl.DisplayMode mode : availableDisplayModes) {
				if (mode.isFullscreenCapable()) {
					modes[idx++] = new LwjglDisplayMode(mode.getWidth(), mode.getHeight(), mode.getFrequency(),
						mode.getBitsPerPixel(), mode);
				}
			}

			return modes;
		} catch (LWJGLException e) {
			throw new GdxRuntimeException("Couldn't fetch available display modes", e);
		}
	}

	@Override
	public DisplayMode[] getDisplayModes(Monitor monitor) {
		return getDisplayModes();
	}

	@Override
	public DisplayMode getDisplayMode() {
		org.lwjgl.opengl.DisplayMode mode = Display.getDisplayMode();
		return new LwjglDisplayMode(mode.getWidth(), mode.getHeight(), mode.getFrequency(), mode.getBitsPerPixel(), mode);
	}

	@Override
	public DisplayMode getDesktopDisplayMode() {
		org.lwjgl.opengl.DisplayMode mode = Display.getDesktopDisplayMode();
		return new LwjglDisplayMode(mode.getWidth(), mode.getHeight(), mode.getFrequency(), mode.getBitsPerPixel(), mode);
	}

	@Override
	public DisplayMode getDisplayMode(Monitor monitor) {
		return getDisplayMode();
	}

	private class LwjglDisplayMode extends DisplayMode {
		org.lwjgl.opengl.DisplayMode mode;

		public LwjglDisplayMode (int width, int height, int refreshRate, int bitsPerPixel, org.lwjgl.opengl.DisplayMode mode) {
			super(width, height, refreshRate, bitsPerPixel);
			this.mode = mode;
		}
	}

	@Override
	public boolean setFullscreenMode(DisplayMode displayMode) {
		org.lwjgl.opengl.DisplayMode mode = ((LwjglDisplayMode)displayMode).mode;
		try {
			if (!mode.isFullscreenCapable()) {
				Display.setDisplayMode(mode);
			} else {
				Display.setDisplayModeAndFullscreen(mode);
			}
			float scaleFactor = Display.getPixelScaleFactor();
			config.width = (int)(mode.getWidth() * scaleFactor);
			config.height = (int)(mode.getHeight() * scaleFactor);
			if (BuildGdx.gl != null) BuildGdx.gl.glViewport(0, 0, config.width, config.height);
			resize = true;

			rate = mode.getFrequency();
			return true;
		} catch (LWJGLException e) {
			return false;
		}
	}

	@Override
	public boolean setWindowedMode(int width, int height) {
		if (getWidth() == width && getHeight() == height && !Display.isFullscreen()) {
			return true;
		}

		try {
			boolean fullscreen = false;
			org.lwjgl.opengl.DisplayMode targetDisplayMode = new org.lwjgl.opengl.DisplayMode(width, height);
			boolean resizable = !fullscreen && config.resizable;

			Display.setDisplayMode(targetDisplayMode);
			Display.setFullscreen(fullscreen);
			// Workaround for bug in LWJGL whereby resizable state is lost on DisplayMode change
			if (resizable == Display.isResizable()) {
				Display.setResizable(!resizable);
			}
			Display.setResizable(resizable);

			float scaleFactor = Display.getPixelScaleFactor();
			config.width = (int)(targetDisplayMode.getWidth() * scaleFactor);
			config.height = (int)(targetDisplayMode.getHeight() * scaleFactor);
			if (BuildGdx.gl != null) BuildGdx.gl.glViewport(0, 0, config.width, config.height);
			resize = true;

			rate = Display.getDesktopDisplayMode().getFrequency();
			return true;
		} catch (LWJGLException e) {
			return false;
		}
	}

	@Override
	public void setTitle(String title) {
		if (title == null)
			title = "";
		Display.setTitle(title);
	}

	@Override
	public void setUndecorated(boolean undecorated) {
		System.setProperty("org.lwjgl.opengl.Window.undecorated", undecorated ? "true" : "false");
	}

	@Override
	public void setResizable(boolean resizable) {
		this.config.resizable = resizable;
		Display.setResizable(resizable);
	}

	@Override
	public void setVSync(boolean vsync) {
		this.vsync = vsync;
		Display.setVSyncEnabled(vsync);
	}

	@Override
	public BufferFormat getBufferFormat() {
		return bufferFormat;
	}

	@Override
	public boolean supportsExtension(String extension) {
		return extensions.contains(extension, false);
	}

	@Override
	public boolean isFullscreen() {
		return Display.isFullscreen();
	}

	@Override
	public Cursor newCursor (Pixmap pixmap, int xHotspot, int yHotspot) {
		return new LwjglCursor(pixmap, xHotspot, yHotspot);
	}

	@Override
	public void setCursor(Cursor cursor) {
		if (SharedLibraryLoader.isMac)
			return;

		try {
			Mouse.setNativeCursor(((LwjglCursor)cursor).lwjglCursor);
		} catch (Exception e) {
			throw new GdxRuntimeException("Could not set cursor image.", e);
		}
	}

	@Override
	public void setSystemCursor(SystemCursor systemCursor) {
		if (SharedLibraryLoader.isMac)
			return;

		try {
			Mouse.setNativeCursor(null);
		} catch (LWJGLException e) {
			throw new GdxRuntimeException("Couldn't set system cursor");
		}
	}

	@Override
	public GraphicsType getType() {
		return GraphicsType.LWJGL;
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
		if( !Display.isCreated() || !Mouse.isCreated() || !Keyboard.isCreated()) return false;
		try {
			Display.setDisplayConfiguration(gamma, brightness, contrast);
		} catch (Exception e) { e.printStackTrace(); return false; }

		return true;
	}

	private class LwjglCursor implements Cursor {
		org.lwjgl.input.Cursor lwjglCursor = null;

		public LwjglCursor (Pixmap pixmap, int xHotspot, int yHotspot) {
			if (SharedLibraryLoader.isMac)
				return;

			try {
				if (pixmap == null) {
					lwjglCursor = null;
					return;
				}

				if (pixmap.getFormat() != Pixmap.Format.RGBA8888) {
					throw new GdxRuntimeException("Cursor image pixmap is not in RGBA8888 format.");
				}

				if ((pixmap.getWidth() & (pixmap.getWidth() - 1)) != 0) {
					throw new GdxRuntimeException("Cursor image pixmap width of " + pixmap.getWidth()
						+ " is not a power-of-two greater than zero.");
				}

				if ((pixmap.getHeight() & (pixmap.getHeight() - 1)) != 0) {
					throw new GdxRuntimeException("Cursor image pixmap height of " + pixmap.getHeight()
						+ " is not a power-of-two greater than zero.");
				}

				if (xHotspot < 0 || xHotspot >= pixmap.getWidth()) {
					throw new GdxRuntimeException("xHotspot coordinate of " + xHotspot + " is not within image width bounds: [0, "
						+ pixmap.getWidth() + ").");
				}

				if (yHotspot < 0 || yHotspot >= pixmap.getHeight()) {
					throw new GdxRuntimeException("yHotspot coordinate of " + yHotspot + " is not within image height bounds: [0, "
						+ pixmap.getHeight() + ").");
				}

				// Convert from RGBA8888 to ARGB8888 and flip vertically
				IntBuffer pixelBuffer = pixmap.getPixels().asIntBuffer();
				int[] pixelsRGBA = new int[pixelBuffer.capacity()];
				pixelBuffer.get(pixelsRGBA);
				int[] pixelsARGBflipped = new int[pixelBuffer.capacity()];
				int pixel;
				if (pixelBuffer.order() == ByteOrder.BIG_ENDIAN) {
					for (int y = 0; y < pixmap.getHeight(); ++y) {
						for (int x = 0; x < pixmap.getWidth(); ++x) {
							pixel = pixelsRGBA[x + (y * pixmap.getWidth())];
							pixelsARGBflipped[x + ((pixmap.getHeight() - 1 - y) * pixmap.getWidth())] = ((pixel >> 8) & 0x00FFFFFF)
								| ((pixel << 24) & 0xFF000000);
						}
					}
				} else {
					for (int y = 0; y < pixmap.getHeight(); ++y) {
						for (int x = 0; x < pixmap.getWidth(); ++x) {
							pixel = pixelsRGBA[x + (y * pixmap.getWidth())];
							pixelsARGBflipped[x + ((pixmap.getHeight() - 1 - y) * pixmap.getWidth())] = ((pixel & 0xFF) << 16)
								| ((pixel & 0xFF0000) >> 16) | (pixel & 0xFF00FF00);
						}
					}
				}

				lwjglCursor = new org.lwjgl.input.Cursor(pixmap.getWidth(), pixmap.getHeight(), xHotspot, pixmap.getHeight() - yHotspot
					- 1, 1, IntBuffer.wrap(pixelsARGBflipped), null);
			} catch (LWJGLException e) {
				throw new GdxRuntimeException("Could not create cursor image.", e);
			}
		}

		@Override
		public void dispose () {
		}
	}

}
