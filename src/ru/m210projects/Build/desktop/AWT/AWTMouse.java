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

import java.awt.Canvas;
import java.awt.Cursor;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Point;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.utils.IntSet;
import com.badlogic.gdx.utils.Pool;

public class AWTMouse implements MouseMotionListener, MouseListener, MouseWheelListener, MouseInterface {

	static class TouchEvent {
		static final int TOUCH_DOWN = 0;
		static final int TOUCH_UP = 1;
		static final int TOUCH_DRAGGED = 2;
		static final int TOUCH_MOVED = 3;
		static final int TOUCH_SCROLLED = 4;

		long timeStamp;
		int type;
		int x;
		int y;
		int pointer;
		int button;
		int scrollAmount;
	}

	Pool<TouchEvent> usedTouchEvents = new Pool<TouchEvent>(16, 1000) {
		@Override
		protected TouchEvent newObject () {
			return new TouchEvent();
		}
	};

	List<TouchEvent> touchEvents = new ArrayList<TouchEvent>();
	int x = 0;
	int y = 0;
	int deltaX = 0;
	int deltaY = 0;
	int wheel = 0;
	boolean touchDown = false;
	boolean justTouched = false;
	boolean catched = false;

	Cursor noCursor;
	Cursor defCursor = Cursor.getDefaultCursor();
	boolean mouseInside;
	boolean[] justPressedButtons = new boolean[5];
	IntSet pressedButtons = new IntSet();

	protected Robot robot;
	protected JDisplay display;

	public AWTMouse(JDisplay display)
	{
		try {
			robot = new Robot(GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice());
		} catch (Exception ignored) {}
		this.display = display;

		Canvas canvas = display.getCanvas();
		if (canvas != null) {
			canvas.removeMouseListener(this);
			canvas.removeMouseMotionListener(this);
			canvas.removeMouseWheelListener(this);
		}
		assert canvas != null;
		canvas.addMouseListener(this);
		canvas.addMouseMotionListener(this);
		canvas.addMouseWheelListener(this);
	}

	@Override
	public void setWindowHandle() { /* nothing */ }

	private void setMousePosition(int x, int y)
	{
		if(robot != null)
			robot.mouseMove(x, y);
	}

	@Override
	public void setCursorPosition(int x, int y)
	{
		this.x = x;
		this.y = y;

		Canvas canvas = display.getCanvas();
		x += canvas.getLocationOnScreen().x;
		y += canvas.getLocationOnScreen().y;

		setMousePosition(x, y);
	}

	@Override
	public void reset()
	{
		Canvas canvas = display.getCanvas();

		touchDown = false;
		x = canvas.getWidth() / 2;
		y = canvas.getHeight() / 2;
//		deltaX = 0;
//		deltaY = 0;
		wheel = 0;
		justTouched = false;
		pressedButtons.clear();
		Arrays.fill(justPressedButtons, false);
	}

	@Override
	public long processEvents (InputProcessor processor) {
		synchronized (this) {
			if (justTouched) {
				justTouched = false;
				Arrays.fill(justPressedButtons, false);
			}

			long currentEventTimeStamp = -1;
			if (processor != null) {
				int len = touchEvents.size();
				for (int i = 0; i < len; i++) {
					TouchEvent e = touchEvents.get(i);
					currentEventTimeStamp = e.timeStamp;
					switch (e.type) {
						case TouchEvent.TOUCH_DOWN:
							processor.touchDown(e.x, e.y, e.pointer, e.button);
							justPressedButtons[e.button] = true;
							justTouched = true;
							break;
						case TouchEvent.TOUCH_UP:
							processor.touchUp(e.x, e.y, e.pointer, e.button);
							break;
						case TouchEvent.TOUCH_DRAGGED:
							processor.touchDragged(e.x, e.y, e.pointer);
							break;
						case TouchEvent.TOUCH_MOVED:
							processor.mouseMoved(e.x, e.y);
							break;
						case TouchEvent.TOUCH_SCROLLED:
							processor.scrolled(e.scrollAmount);
							break;
					}
					usedTouchEvents.free(e);
				}
			}  else {
				int len = touchEvents.size();
				for (int i = 0; i < len; i++) {
					TouchEvent event = touchEvents.get(i);
					if (event.type == TouchEvent.TOUCH_DOWN && event.button != -1) {
						justPressedButtons[event.button] = true;
						justTouched = true;
					}
					usedTouchEvents.free(event);
				}
			}

			if (touchEvents.isEmpty()) {
//				deltaX = 0;
//				deltaY = 0;
				wheel = 0;
			}

			touchEvents.clear();

			return currentEventTimeStamp;
		}
	}

	@Override
	public void mouseDragged (MouseEvent e) {
		synchronized (this) {
			TouchEvent event = usedTouchEvents.obtain();
			event.pointer = 0;
			event.x = e.getX();
			event.y = e.getY();
			event.type = TouchEvent.TOUCH_DRAGGED;
			event.timeStamp = System.nanoTime();
			touchEvents.add(event);

			deltaX += (event.x - x);
			deltaY += (event.y - y);
			x = event.x;
			y = event.y;
			checkCatched(e);
		}
	}

	@Override
	public void mouseMoved (MouseEvent e) {
		synchronized (this) {
			TouchEvent event = usedTouchEvents.obtain();
			event.pointer = 0;
			event.x = e.getX();
			event.y = e.getY();
			event.type = TouchEvent.TOUCH_MOVED;
			event.timeStamp = System.nanoTime();
			touchEvents.add(event);

			deltaX += (event.x - x);
			deltaY += (event.y - y);
			x = event.x;
			y = event.y;
			checkCatched(e);
		}
	}

	@Override
	public void mouseClicked (MouseEvent arg0) {
		mouseInside = true;
	}

	@Override
	public void mouseEntered (MouseEvent e) {
		x = e.getX();
		y = e.getY();
		mouseInside = true;
		checkCatched(e);
	}

	@Override
	public void mouseExited (MouseEvent e) {
		mouseInside = false;
		checkCatched(e);
	}

	private void checkCatched (MouseEvent e) {
		if(!display.isActive())
			return;

		Canvas canvas = display.getCanvas();
		if (catched && canvas.isShowing()) {
			if (e.getX() < 0 || e.getX() >= canvas.getWidth() || e.getY() < 0 || e.getY() >= canvas.getHeight()) {
				setCursorPosition(canvas.getWidth() / 2, canvas.getHeight() / 2);
				showCursor(false);
			}
		}
	}

	@Override
	public void showCursor (boolean visible) {
		if(display == null) return;

		if (!visible) {
			if(noCursor == null) {
				Toolkit t = Toolkit.getDefaultToolkit();
				Image i = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
				noCursor = t.createCustomCursor(i, new Point(0, 0), "none");
			}

			if(display.m_frame.getContentPane().getCursor() != noCursor) {
				display.m_frame.getContentPane().getInputContext().selectInputMethod(Locale.US);
				display.m_frame.getContentPane().setCursor(noCursor);
			}
		} else {
			if(display.m_frame.getContentPane().getCursor() != defCursor)
				display.m_frame.getContentPane().setCursor(defCursor);
		}
	}

	@Override
	public void mousePressed (MouseEvent e) {
		synchronized (this) {
			TouchEvent event = usedTouchEvents.obtain();
			event.pointer = 0;
			event.x = e.getX();
			event.y = e.getY();
			event.type = TouchEvent.TOUCH_DOWN;
			event.button = toGdxButton(e.getButton());
			event.timeStamp = System.nanoTime();
			touchEvents.add(event);

			deltaX += (event.x - x);
			deltaY += (event.y - y);
			x = event.x;
			y = event.y;
			touchDown = true;
			pressedButtons.add(event.button);
		}
	}

	private int toGdxButton (int button) {
		if (button == 1) return Buttons.LEFT;
		if (button == 2) return Buttons.MIDDLE;
		if (button == 3) return Buttons.RIGHT;
		if (button == 4) return Buttons.BACK;
		if (button == 5) return Buttons.FORWARD;
		return -1;
	}

	@Override
	public void mouseReleased (MouseEvent e) {
		synchronized (this) {
			TouchEvent event = usedTouchEvents.obtain();
			event.pointer = 0;
			event.x = e.getX();
			event.y = e.getY();
			event.button = toGdxButton(e.getButton());
			event.type = TouchEvent.TOUCH_UP;
			event.timeStamp = System.nanoTime();
			touchEvents.add(event);

			deltaX += (event.x - x);
			deltaY += (event.y - y);
			x = event.x;
			y = event.y;
			pressedButtons.remove(event.button);
			if (pressedButtons.size == 0) touchDown = false;
		}
	}

	@Override
	public void mouseWheelMoved (MouseWheelEvent e) {
		synchronized (this) {
			TouchEvent event = usedTouchEvents.obtain();
			event.pointer = 0;
			event.type = TouchEvent.TOUCH_SCROLLED;
			event.scrollAmount = e.getWheelRotation();
			event.timeStamp = System.nanoTime();
			touchEvents.add(event);

			wheel = -event.scrollAmount;
		}
	}

	@Override
	public void setCursorCatched (boolean catched) {
		this.catched = catched;
		showCursor(!catched);

		Canvas canvas = display.getCanvas();
		if(catched)
			setCursorPosition(canvas.getWidth() / 2, canvas.getHeight() / 2);
	}

	@Override
	public int getX() {
		return x;
	}

	@Override
	public int getY() {
		return y;
	}

	@Override
	public int getDeltaX() {
		int out = deltaX;
		deltaX = 0;
		return out;
	}

	@Override
	public int getDeltaY() {
		int out = deltaY;
		deltaY = 0;
		return out;
	}

	@Override
	public boolean isTouched() {
		return touchDown;
	}

	@Override
	public boolean justTouched() {
		return justTouched;
	}

	@Override
	public boolean isButtonPressed(int button) {
		return pressedButtons.contains(button);
	}

	@Override
	public boolean isButtonJustPressed(int button) {
		if(button < 0 || button >= justPressedButtons.length) return false;
		return justPressedButtons[button];
	}

	@Override
	public int getDWheel() {
		return wheel;
	}

	@Override
	public boolean isCursorCatched() {
		return catched;
	}

	@Override
	public boolean isInsideWindow() {
		return mouseInside;
	}
}
