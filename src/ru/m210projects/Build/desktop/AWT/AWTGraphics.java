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

package ru.m210projects.Build.desktop.AWT;

import java.awt.Image;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Cursor.SystemCursor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.glutils.GLVersion;
import com.badlogic.gdx.utils.Array;

import ru.m210projects.Build.Architecture.BuildConfiguration;
import ru.m210projects.Build.Architecture.BuildFrame.FrameType;
import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.Architecture.BuildGraphics;
import ru.m210projects.Build.Render.Types.GL10;

public class AWTGraphics extends BuildGraphics {

	protected JDisplay display;
	private final BuildConfiguration config;
	
	public AWTGraphics(BuildConfiguration config)
	{
		this.config = config;
	}
	
	@Override
	public GraphicsType getType() {
		return null;
	}

	@Override
	protected int getRefreshRate() {
		return display.getDesktopDisplayMode().getRefreshRate();
	}

	@Override
	protected boolean isDirty() {
		return false;
	}

	@Override
	protected void sync(int fps) {
		Sync.sync(fps);
	}

	@Override
	protected void init() throws Exception {
		display = new JDisplay(config.width, config.height, config.borderless);

		try {
			Array<String> iconPaths = config.getIconPaths();
			if (iconPaths.size > 0) {
				List<Image> icons = new ArrayList<Image>();
				for (int i = 0, n = iconPaths.size; i < n; i++) {
					FileHandle file = BuildGdx.files.getFileHandle(iconPaths.get(i), config.getIconFileTypes().get(i));
					ImageIcon icon = new ImageIcon(file.readBytes());
					icons.add(icon.getImage());
				}
				display.setIcon(icons);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		display.setTitle(config.title);
		display.setResizable(config.resizable);
		display.setLocation(config.x, config.y);
		
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				if(config.fullscreen) {
					DisplayMode bestMode = null;
					for(DisplayMode mode: getDisplayModes()) {
						if(mode.width == config.width && mode.height == config.height) {
							if(bestMode == null || bestMode.refreshRate < mode.refreshRate) {
								bestMode = mode;
							}
						}
					}
					if(bestMode == null) {
						bestMode = AWTGraphics.this.getDesktopDisplayMode();
					}
					setFullscreenMode(bestMode);
				}
				
				display.m_frame.setVisible(true);
				display.getCanvas().setFocusable(true);
				display.getCanvas().requestFocus();
			}
		});
	}

	@Override
	protected void update() {
		display.update();
	}

	@Override
	protected void updateSize(int width, int height) {
		display.updateSize(width, height);
	}

	@Override
	protected boolean wasResized() {
		return display.wasResized();
	}

	@Override
	protected int getX() {
		return display.getX();
	}

	@Override
	protected int getY() {
		return display.getY();
	}

	@Override
	protected boolean isActive() {
		return display.isActive();
	}

	@Override
	protected boolean isCloseRequested() {
		return display.isCloseRequested();
	}

	@Override
	protected void dispose() {
		display.dispose();
	}

	@Override
	public int getWidth() {
		return display.getWidth(); //* scalefactor
	}

	@Override
	public int getHeight() {
		return display.getHeight(); //* scalefactor
	}

	@Override
	public void setFramesPerSecond(int fps) {
		config.foregroundFPS = fps;
		config.backgroundFPS = fps;
	}

	@Override
	public FrameType getFrameType() {
		return FrameType.Canvas;
	}

	@Override
	public boolean supportsDisplayModeChange() {
		return true;
	}

	@Override
	public DisplayMode[] getDisplayModes() {
		java.awt.DisplayMode[] availableDisplayModes = display.getDisplayModes();
		DisplayMode[] modes = new DisplayMode[availableDisplayModes.length];

		int idx = 0;
		for (java.awt.DisplayMode mode : availableDisplayModes) {
			modes[idx++] = new SoftDisplayMode(mode.getWidth(), mode.getHeight(), mode.getRefreshRate(), mode.getBitDepth(), mode);
		}

		return modes;
	}

	@Override
	public DisplayMode[] getDisplayModes(Monitor monitor) {
		return getDisplayModes();
	}

	@Override
	public DisplayMode getDisplayMode() {
		return getDesktopDisplayMode();
	}

	@Override
	public DisplayMode getDisplayMode(Monitor monitor) {
		return getDisplayMode();
	}

	@Override
	public DisplayMode getDesktopDisplayMode() {
		java.awt.DisplayMode mode = display.getDesktopDisplayMode();
		return new SoftDisplayMode(mode.getWidth(), mode.getHeight(), mode.getRefreshRate(), mode.getBitDepth(), mode);
	}

	private class SoftDisplayMode extends DisplayMode {
		java.awt.DisplayMode mode;
		
		public SoftDisplayMode (int width, int height, int refreshRate, int bitsPerPixel, java.awt.DisplayMode mode) {
			super(width, height, refreshRate, bitsPerPixel);
			this.mode = mode;
		}
	}

	@Override
	public boolean setFullscreenMode(DisplayMode displayMode) {
		java.awt.DisplayMode mode = ((SoftDisplayMode)displayMode).mode;
		
		if(display.setFullscreenMode(mode))
		{
			config.width = mode.getWidth(); // * scalefactor
			config.height = mode.getHeight(); // * scalefactor
			resize = true;
			
			return true;
		}

		return false;
	}

	@Override
	public boolean setWindowedMode(int width, int height) {
		if (getWidth() == width && getHeight() == height && !display.isFullscreen()) 
			return true;

		java.awt.DisplayMode targetDisplayMode = new java.awt.DisplayMode(width, height, 
				display.getDesktopDisplayMode().getRefreshRate(), display.getDesktopDisplayMode().getBitDepth());

		display.setWindowedMode(targetDisplayMode);
		display.setResizable(config.resizable);

		config.width = targetDisplayMode.getWidth(); // * scalefactor
		config.height = targetDisplayMode.getHeight(); // * scalefactor
		resize = true;
		return true;
	}

	@Override
	public void setTitle(String title) {
		display.setTitle(title);
	}

	@Override
	public void setUndecorated(boolean undecorated) {
		display.setUndecorated(undecorated);
	}

	@Override
	public void setResizable(boolean resizable) {
		display.setResizable(resizable);
	}

	@Override
	public void setVSync(boolean vsync) {
		this.vsync = vsync;
	}
	
	@Override
	public boolean isFullscreen() {
		return display.isFullscreen();
	}

	// unsupported
	
	@Override
	public Cursor newCursor(Pixmap pixmap, int xHotspot, int yHotspot) {
		return null;
	}

	@Override
	public void setCursor(Cursor cursor) {
		
	}

	@Override
	public void setSystemCursor(SystemCursor systemCursor) {
		
	}

	@Override
	public boolean isGL30Available() {
		return false;
	}

	@Override
	public GL20 getGL20() {
		return null;
	}

	@Override
	public GL30 getGL30() {
		return null;
	}

	@Override
	public void setGL20(GL20 gl20) {
	}

	@Override
	public void setGL30(GL30 gl30) {
	}

	@Override
	public GLVersion getGLVersion() {
		return null;
	}

	@Override
	public Monitor getPrimaryMonitor() {
		return null;
	}

	@Override
	public Monitor getMonitor() {
		return null;
	}

	@Override
	public Monitor[] getMonitors() {
		return null;
	}
	
	@Override
	public boolean supportsExtension(String extension) {
		return false;
	}

	@Override
	public GL10 getGL10() {
		return null;
	}
	
	@Override
	public BufferFormat getBufferFormat() {
		return null;
	}

	/**
	 * Software
	 */
	@Override
	public Object extra(Option opt, Object... obj) {
		switch(opt)
		{
		case SWGetFrame:
			return display.getCanvas().getFrame();
		case SWChangePalette:
			byte[] palette = (byte[]) obj[0];
			if(palette.length != 768)
				throw new UnsupportedOperationException("The array should be 256 bytes!"); 
			
			display.getCanvas().changepalette(palette);
			break;
		default:
			throw new UnsupportedOperationException("not implemented"); 
		}
		
		return null;
	}
}
