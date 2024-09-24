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

package ru.m210projects.Build.Architecture;

import java.awt.Toolkit;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Graphics.BufferFormat;
import com.badlogic.gdx.Graphics.DisplayMode;
import com.badlogic.gdx.Graphics.Monitor;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Cursor.SystemCursor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.glutils.GLVersion;

import ru.m210projects.Build.Architecture.BuildFrame.FrameType;


public abstract class BuildGraphics implements Graphics {
	
	public enum Option { SWChangePalette, SWGetFrame, GLSetConfiguration, GLDefConfiguration }

	protected GL20 gl20;
	protected GL30 gl30;
	
	protected long frameId = -1;
	protected float deltaTime = 0;
	protected long frameStart = 0;
	protected int frames = 0;
	protected int fps;
	protected long lastTime = System.nanoTime();

	protected boolean vsync = false;
	protected boolean resize = false;
	
	protected volatile boolean isContinuous = true;
	protected volatile boolean requestRendering = false;
	
	protected void updateTime() {
		long time = System.nanoTime();
		deltaTime = (time - lastTime) / 1000000000.0f;
		lastTime = time;

		if (time - frameStart >= 1000000000) {
			fps = frames;
			frames = 0;
			frameStart = time;
		}
		frames++;
	}
	
	/** Sets whether to render continuously. In case rendering is performed non-continuously, the following events will trigger a
	 * redraw:
	 *
	 * <ul>
	 * <li>A call to {@link #requestRendering()}</li>
	 * <li>Input events from the touch screen/mouse or keyboard</li>
	 * <li>A {@link Runnable} is posted to the rendering thread via {@link Application#postRunnable(Runnable)}. In the case
	 * of a multi-window app, all windows will request rendering if a runnable is posted to the application. To avoid this, 
	 * post a runnable to the window instead. </li>
	 * </ul>
	 *
	 * Life-cycle events will also be reported as usual, see {@link ApplicationListener}. This method can be called from any
	 * thread.
	 *
	 * @param isContinuous whether the rendering should be continuous or not. */
	public void setContinuousRendering (boolean isContinuous) {
		this.isContinuous = isContinuous;
	}

	/** @return whether rendering is continuous. */
	public boolean isContinuousRendering () {
		return isContinuous;
	}

	/** Requests a new frame to be rendered if the rendering mode is non-continuous. This method can be called from any thread. */
	public void requestRendering () {
		synchronized (this) {
			requestRendering = true;
		}
	}
	
	/** Returns the id of the current frame. The general contract of this method is that the id is incremented only when the
	 * application is in the running state right before calling the {@link ApplicationListener#render()} method. Also, the id of
	 * the first frame is 0; the id of subsequent frames is guaranteed to take increasing values for 2<sup>63</sup>-1 rendering
	 * cycles.
	 * @return the id of the current frame */
	public long getFrameId () {
		return frameId;
	}

	/** @return the time span between the current frame and the last frame in seconds. Might be smoothed over n frames. */
	public float getDeltaTime () {
		return deltaTime;
	}

	/** @return the time span between the current frame and the last frame in seconds, without smoothing **/
	public float getRawDeltaTime () {
		return deltaTime;
	}
	
	protected abstract int getRefreshRate();
	
	protected abstract boolean isDirty();
	
	protected boolean shouldRender() {
		synchronized (this) {
			boolean rq = requestRendering;
			requestRendering = false;
			return rq || isContinuous || isDirty();
		}
	}
	
	protected abstract void sync(int fps);
	
	protected abstract void init() throws Exception;
	
	protected abstract void update();
	
	protected abstract void updateSize(int width, int height);
	
	protected abstract boolean wasResized();
	
	protected abstract int getX();

	protected abstract int getY();
	
	protected abstract boolean isCloseRequested();

	protected abstract void dispose();
	
	protected abstract boolean isActive();

	/** @return the {@link GL20} instance */
	public GL20 getGL20() {
		return gl20;
	}

	public boolean isGL30Available() {
		return gl30 != null;
	}
	
	/** @return the {@link GL30} instance or null if not supported */
	public GL30 getGL30() {
		return gl30;
	}

	/** Set the GL20 instance **/
	public void setGL20 (GL20 gl20) {
		this.gl20 = gl20;
		if (gl30 == null) {
			BuildGdx.gl20 = gl20;
		}
	}

	/** Set the GL30 instance **/
	public void setGL30(GL30 gl30) {
		this.gl30 = gl30;
		if (gl30 != null) {
			this.gl20 = gl30;
			BuildGdx.gl20 = gl20;
			BuildGdx.gl30 = gl30;
		}
	}

	/** @return the width of the client area in logical pixels. */
	public abstract int getWidth ();

	/** @return the height of the client area in logical pixels */
	public abstract int getHeight ();

	/** @return the average number of frames per second */
	public int getFramesPerSecond () {
		return fps;
	}
	
	public abstract void setFramesPerSecond(int fps);

	/** @return the {@link FrameType} of this Graphics instance */
	public abstract FrameType getFrameType();

	/** @return the {@link GLVersion} of this Graphics instance */
	public abstract GLVersion getGLVersion();

	/** Whether the given backend supports a display mode change via calling {@link Graphics#setFullscreenMode(DisplayMode)}
	 *
	 * @return whether display mode changes are supported or not. */
	public abstract boolean supportsDisplayModeChange();

	/** @return the primary monitor **/
	public abstract Monitor getPrimaryMonitor();

	/** @return the monitor the application's window is located on */
	public abstract Monitor getMonitor();

	/** @return the currently connected {@link Monitor}s */
	public abstract Monitor[] getMonitors();

	/** @return the supported fullscreen {@link DisplayMode}(s) of the monitor the window is on */
	public abstract DisplayMode[] getDisplayModes ();
	
	/** @return the supported fullscreen {@link DisplayMode}s of the given {@link Monitor} */
	public abstract DisplayMode[] getDisplayModes(Monitor monitor);

	/** @return the current {@link DisplayMode} of the monitor the window is on. */
	public abstract DisplayMode getDisplayMode ();
	
	/** @return the desktop {@link DisplayMode} of the monitor the window is on */
	public abstract DisplayMode getDesktopDisplayMode();

	/** @return the current {@link DisplayMode} of the given {@link Monitor} */
	public abstract DisplayMode getDisplayMode (Monitor monitor);

	/** Sets the window to full-screen mode.
	 *
	 * @param displayMode the display mode.
	 * @return whether the operation succeeded. */
	public abstract boolean setFullscreenMode (DisplayMode displayMode);

	/** Sets the window to windowed mode.
	 *
	 * @param width the width in pixels
	 * @param height the height in pixels
	 * @return whether the operation succeeded*/
	public abstract boolean setWindowedMode (int width, int height);

	/** Sets the title of the window. Ignored on Android.
	 *
	 * @param title the title. */
	public abstract void setTitle (String title);

	/** Sets the window decoration as enabled or disabled. On Android, this will enable/disable
	 *  the menu bar.
	 *
	 *  Note that immediate behavior of this method may vary depending on the implementation. It
	 *  may be necessary for the window to be recreated in order for the changes to take effect.
	 *  Consult the documentation for the backend in use for more information.
	 *
	 *  Supported on all GDX desktop backends and on Android (to disable the menu bar).
	 *
	 * @param undecorated true if the window border or status bar should be hidden. false otherwise.
	 */
	public abstract void setUndecorated (boolean undecorated);

	/** Sets whether or not the window should be resizable. Ignored on Android.
	 *
	 *  Note that immediate behavior of this method may vary depending on the implementation. It
	 *  may be necessary for the window to be recreated in order for the changes to take effect.
	 *  Consult the documentation for the backend in use for more information.
	 *
	 *  Supported on all GDX desktop backends.
	 *
	 * @param resizable
	 */
	public abstract void setResizable (boolean resizable);

	/** Enable/Disable vsynching. This is a best-effort attempt which might not work on all platforms.
	 *
	 * @param vsync vsync enabled or not. */
	public abstract void setVSync (boolean vsync);

	/** @return the format of the color, depth and stencil buffer in a {@link BufferFormat} instance */
	public abstract BufferFormat getBufferFormat ();

	/** @param extension the extension name
	 * @return whether the extension is supported */
	public abstract boolean supportsExtension (String extension);

	/** Whether the app is fullscreen or not */
	public abstract boolean isFullscreen ();

	/** Create a new cursor represented by the {@link com.badlogic.gdx.graphics.Pixmap}. The Pixmap must be in RGBA8888 format,
	 * width & height must be powers-of-two greater than zero (not necessarily equal) and of a certain minimum size (32x32 is a safe bet),
	 * and alpha transparency must be single-bit (i.e., 0x00 or 0xFF only). This function returns a Cursor object that can be set as the 
	 * system cursor by calling {@link #setCursor(Cursor)} .
	 *
	 * @param pixmap the mouse cursor image as a {@link com.badlogic.gdx.graphics.Pixmap}
	 * @param xHotspot the x location of the hotspot pixel within the cursor image (origin top-left corner)
	 * @param yHotspot the y location of the hotspot pixel within the cursor image (origin top-left corner)
	 * @return a cursor object that can be used by calling {@link #setCursor(Cursor)} or null if not supported */
	public abstract Cursor newCursor(Pixmap pixmap, int xHotspot, int yHotspot);

	/** Only viable on the lwjgl-backend and on the gwt-backend. Browsers that support cursor:url() and support the png format (the
	 * pixmap is converted to a data-url of type image/png) should also support custom cursors. Will set the mouse cursor image to
	 * the image represented by the {@link com.badlogic.gdx.graphics.Cursor}. It is recommended to call this function in the main render thread, and maximum one time per frame.
	 *
	 * @param cursor the mouse cursor as a {@link com.badlogic.gdx.graphics.Cursor} */
	public abstract void setCursor (Cursor cursor);

	/**
	 * Sets one of the predefined {@link SystemCursor}s
	 */
	public abstract void setSystemCursor(SystemCursor systemCursor);
	
	public abstract Object extra(Option opt, Object... obj);
	
	public int getLogicalWidth() {
		return getWidth();
	}
	
	public int getLogicalHeight() {
		return getHeight();
	}
			
	@Override
	public int getBackBufferWidth() {
		return getWidth();
	}
	
	@Override
	public int getBackBufferHeight() {
		return getHeight();
	}

	@Override
	public float getDensity() {
		return getPpiX() / 160f;
	}

	@Override
	public float getPpiX () {
		return Toolkit.getDefaultToolkit().getScreenResolution();
	}

	@Override
	public float getPpiY () {
		return Toolkit.getDefaultToolkit().getScreenResolution();
	}

	@Override
	public float getPpcX () {
		return (Toolkit.getDefaultToolkit().getScreenResolution() / 2.54f);
	}

	@Override
	public float getPpcY () {
		return (Toolkit.getDefaultToolkit().getScreenResolution() / 2.54f);
	}
}
