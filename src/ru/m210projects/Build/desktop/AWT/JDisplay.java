//This file is part of BuildGDX.
//Copyright (C) 2017-2018  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
//BuildGDX is free software: you can redistribute it and/or modify
//it under the terms of the GNU General Public License as published by
//the Free Software Foundation, either version 3 of the License, or
//(at your option) any later version.
//
//BuildGDX is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU General Public License for more details.
//
//You should have received a copy of the GNU General Public License
//along with BuildGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.Build.desktop.AWT;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.MouseInfo;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import javax.swing.JFrame;

public class JDisplay extends WindowAdapter
{
	protected final JFrame m_frame;
	private final JCanvas canvas;
	private boolean isCloseRequested = false;
	private boolean isFocus, isActive;
	private final GraphicsDevice device;
	private boolean isFullscreen;
	private boolean wasResized;
	private boolean undecorated;

	public JDisplay(int width, int height, boolean undecorated)
	{
		this.device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();

		this.canvas = new JCanvas(width, height);
		this.m_frame = buildFrame(undecorated);
		updateSize(width, height);
		this.isFullscreen = false;
	}
	
	private Runnable rebuildCallback;
	protected void setRebuildCallback(Runnable handleCallback)
	{
		this.rebuildCallback = handleCallback;
	}

	public DisplayMode[] getDisplayModes()
	{
		return device.getDisplayModes();
	}
	
	public JFrame buildFrame(boolean undecorated)
	{
		JFrame frame = new JFrame(MouseInfo.getPointerInfo().getDevice().getDefaultConfiguration());
		frame.setUndecorated(undecorated);
		frame.add(canvas);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.addWindowListener(this);
		frame.addWindowFocusListener(this);
		
		this.undecorated = undecorated;

		return frame;
	}
	
	public void dispose()
	{
		m_frame.setVisible(false);
		m_frame.dispose();
	}
	
	protected void update() {
		canvas.repaint();
	}
	
	public int getWidth() {
		return canvas.getWidth();
	}

	public int getHeight() {
		return canvas.getHeight();
	}
	
	public boolean isCloseRequested() {
		return isCloseRequested;
	}
	
	public boolean isActive()
	{
		return isFocus && isActive;
	}
	
	public boolean setFullscreenMode(DisplayMode mode)
	{
		if(!device.isFullScreenSupported())
			return false;

		setUndecorated(true);
		device.setFullScreenWindow(m_frame);
		
		if (device.isDisplayChangeSupported())
            device.setDisplayMode(mode);
		
		updateSize(mode.getWidth(), mode.getHeight());

		isFullscreen = true;
		return true;
	}

	public boolean setWindowedMode(DisplayMode mode)
	{
		setUndecorated(undecorated);
		
		device.setFullScreenWindow(null);
		
		if (device.isDisplayChangeSupported())
            device.setDisplayMode(mode);
		
		updateSize(mode.getWidth(), mode.getHeight());

		isFullscreen = false;
		return true;
	}
	
	public DisplayMode getDesktopDisplayMode()
	{
		return device.getDisplayMode();
	}
	
	public void updateSize(int width, int height)
	{
		Dimension size = canvas.getSize();
		if((size.width == width && size.height == height) && 
				(canvas.getWidth() == width && canvas.getHeight() == height))
			return;

		wasResized = true;
		size = new Dimension(width, height);
		canvas.setSize(width, height);
		canvas.setPreferredSize(size);
		canvas.setMinimumSize(size);
		canvas.setMaximumSize(size);
		canvas.setBackground(Color.black);

		if(!isFullscreen())
		{
			int oldX = getX();
			int oldY = getY();

			m_frame.pack();
			m_frame.setLocationRelativeTo(null);
			if(m_frame.isVisible()) {//set location if initialized only
				if(undecorated) {
					DisplayMode mode = getDesktopDisplayMode();
					oldX = (mode.getWidth() / 2) - (m_frame.getWidth() / 2);
					oldY = (mode.getHeight() / 2) - (m_frame.getHeight() / 2);
				}

				m_frame.setLocation(oldX, oldY);
			}
		}

		canvas.validate();
	}
	
	public boolean wasResized()
	{
		boolean out = wasResized;
		wasResized = false;
		return out;
	}
	
	protected JCanvas getCanvas()
	{
		return canvas;
	}

	public void setTitle(String title)
	{
		m_frame.setTitle(title);
	}
	
	public int getX()
	{
		return m_frame.getX();
	}
	
	public int getY()
	{
		return m_frame.getY();
	}

	public void setUndecorated(boolean undecorated)
	{
		if(m_frame.isUndecorated() == undecorated)
			return;
		
		m_frame.dispose();
		m_frame.setUndecorated(undecorated);
		m_frame.setVisible(true);
		
		if(rebuildCallback != null)
			rebuildCallback.run();
	}
	
	public void setResizable(boolean resizable) {
		m_frame.setResizable(resizable);
	}

	public void setLocation(int x, int y) {
		if(x == - 1 && y == -1)
			return;
		
		m_frame.setLocation(x, y);
	}
	
	public void setIcon(List<Image> icons) {
		m_frame.setIconImages(icons);
	}

	public boolean isFullscreen() {
		return isFullscreen;
	}
	
	@Override
    public void windowClosing(WindowEvent windowEvent) {
    	isCloseRequested = true;
    }
    
	@Override
    public void windowActivated(WindowEvent e) {
    	isActive = true;
    }

	@Override
    public void windowDeactivated(WindowEvent e) {
    	isActive = false;
    }

	@Override
    public void windowGainedFocus(WindowEvent e) {
    	isFocus = true;
    }

	@Override
    public void windowLostFocus(WindowEvent e) {
    	isFocus = false;
    }
}
