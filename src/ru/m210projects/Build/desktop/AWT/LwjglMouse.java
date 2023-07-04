// This file is part of BuildGDX.
// Copyright (C) 2017-2019  Alexander Makarov-[M210] (m210-2007@mail.ru)
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

import java.awt.Canvas;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.lwjgl.opengl.Display;

import com.badlogic.gdx.backends.lwjgl.LwjglNativesLoader;

import ru.m210projects.Build.Architecture.BuildApplication.Platform;
import ru.m210projects.Build.Architecture.BuildGdx;

public class LwjglMouse extends AWTMouse {

	private final Method moveMouse;
	private final Method grabMouse;
	private final Method ungrabMouse;
	
	private final Object displayImpl;
	private long handle;
	public static final boolean IS_WINDOWS = BuildGdx.app.getPlatform() == Platform.Windows;

	public LwjglMouse(JDisplay display) throws Exception {
		super(display);
		this.robot = null;

		Method getImplementation = Display.class.getDeclaredMethod("getImplementation");
		getImplementation.setAccessible(true);
		
		LwjglNativesLoader.load(); //This also needs for Pixmap working
		displayImpl = getImplementation.invoke(null, (Object[])null);
		setWindowHandle();
		if(handle == -1 || !IS_WINDOWS) 
			throw new UnsupportedOperationException("not implemented"); 
		
		display.setRebuildCallback(new Runnable() {
			@Override
			public void run() {
				setWindowHandle();
			}
		});

		moveMouse = displayImpl.getClass().getDeclaredMethod("setCursorPosition", int.class, int.class);
		moveMouse.setAccessible(true);
		
		grabMouse = displayImpl.getClass().getDeclaredMethod("setupCursorClipping", long.class); //WindowsDisplay
		grabMouse.setAccessible(true);
		
		ungrabMouse = displayImpl.getClass().getDeclaredMethod("resetCursorClipping"); //WindowsDisplay
		ungrabMouse.setAccessible(true);
	}
	
	private void setImplementVariable(String name, Object value) throws Exception
	{
		Field field = displayImpl.getClass().getDeclaredField(name);
		field.setAccessible(true);
		field.set(displayImpl, value);
	}

	@Override
	public void setWindowHandle()
	{
		try {
			handle = getWindowHandle(display.getCanvas());
			setImplementVariable(IS_WINDOWS ? "hwnd" : "parent_window", handle);
		} catch (Exception e) { e.printStackTrace(); }
	}
	
	private long getWindowHandle(Canvas canvas) throws Exception {
		boolean IS_MAC = System.getProperty("os.name").toLowerCase().contains("mac");
		
		if (IS_MAC)
			return 0;

		Method gethwnd = displayImpl.getClass().getDeclaredMethod(IS_WINDOWS ? "getHwnd" : "getHandle", Canvas.class);
		gethwnd.setAccessible(true);

		return (Long) gethwnd.invoke(displayImpl, canvas);
	}

	@Override
	public void showCursor (boolean visible) {
		super.showCursor(visible);
		try {
			if (!visible && catched && display.isActive()) {
				grabMouse.invoke(displayImpl, handle);
			} else {
				ungrabMouse.invoke(displayImpl);
			}
		} catch (Exception e) { e.printStackTrace(); }
	}

	@Override
	public void setCursorPosition(int x, int y)
	{
		try {
			this.x = x;
			this.y = y;

			moveMouse.invoke(displayImpl, x, y - 1);
		} catch (Exception e) { e.printStackTrace(); }
	}
}
