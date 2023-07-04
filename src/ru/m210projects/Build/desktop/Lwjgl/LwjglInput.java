/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package ru.m210projects.Build.desktop.Lwjgl;

import static ru.m210projects.Build.Input.Keymap.KEY_CAPSLOCK;
import static ru.m210projects.Build.Input.Keymap.KEY_PAUSE;
import static ru.m210projects.Build.Input.Keymap.KEY_SCROLLOCK;
import static ru.m210projects.Build.Input.Keymap.KEY_NUMDECIMAL;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.OverlayLayout;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.badlogic.gdx.utils.IntSet;

import org.lwjgl.BufferUtils;
import org.lwjgl.input.Cursor;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.utils.Pool;

import ru.m210projects.Build.Architecture.BuildFrame;
import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.Architecture.BuildInput;

/** An implementation of the {@link Input} interface hooking a LWJGL panel for input.
 *
 * @author mzechner */
public class LwjglInput implements BuildInput {

	protected BuildFrame frame;
	protected Cursor emptyCursor;
	protected Cursor defCursor = Mouse.getNativeCursor();

	static public float keyRepeatInitialTime = 0.4f;
	static public float keyRepeatTime = 0.1f;

	List<KeyEvent> keyEvents = new ArrayList<KeyEvent>();
	List<TouchEvent> touchEvents = new ArrayList<TouchEvent>();
	boolean mousePressed = false;
	int mouseX, mouseY;
	int deltaX, deltaY;
	int pressedKeys = 0;
	boolean keyJustPressed = false;
	boolean[] justPressedKeys = new boolean[256];
	boolean[] justPressedButtons = new boolean[5];
	boolean justTouched = false;
	IntSet pressedButtons = new IntSet();
	InputProcessor processor;
	char lastKeyCharPressed;
	float keyRepeatTimer;
	long currentEventTimeStamp;
	float deltaTime;
	long lastTime;

	Pool<KeyEvent> usedKeyEvents = new Pool<KeyEvent>(16, 1000) {
		@Override
		protected KeyEvent newObject () {
			return new KeyEvent();
		}
	};

	Pool<TouchEvent> usedTouchEvents = new Pool<TouchEvent>(16, 1000) {
		@Override
		protected TouchEvent newObject () {
			return new TouchEvent();
		}
	};

	public LwjglInput() {
		Keyboard.enableRepeatEvents(false);
		Mouse.setClipMouseCoordinatesToWindow(false);
	}

	@Override
	public void init(BuildFrame frame) { this.frame = frame; }

	@Override
	public float getAccelerometerX () {
		return 0;
	}

	@Override
	public float getAccelerometerY () {
		return 0;
	}

	@Override
	public float getAccelerometerZ () {
		return 0;
	}

	@Override
	public float getGyroscopeX () {
		return 0;
	}

	@Override
	public float getGyroscopeY () {
		return 0;
	}

	@Override
	public float getGyroscopeZ () {
		return 0;
	}

	@Override
	public void getTextInput (final TextInputListener listener, final String title, final String text, final String hint) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run () {
				JPanel panel = new JPanel(new FlowLayout());

				JPanel textPanel = new JPanel();
				textPanel.setLayout(new OverlayLayout(textPanel));
				panel.add(textPanel);

				final JTextField textField = new JTextField(20);
				textField.setText(text);
				textField.setAlignmentX(0.0f);
				textPanel.add(textField);

				final JLabel placeholderLabel = new JLabel(hint);
				placeholderLabel.setForeground(Color.GRAY);
				placeholderLabel.setAlignmentX(0.0f);
				textPanel.add(placeholderLabel, 0);

				textField.getDocument().addDocumentListener(new DocumentListener() {

					@Override
					public void removeUpdate (DocumentEvent arg0) {
						this.updated();
					}

					@Override
					public void insertUpdate (DocumentEvent arg0) {
						this.updated();
					}

					@Override
					public void changedUpdate (DocumentEvent arg0) {
						this.updated();
					}

					private void updated () {
						placeholderLabel.setVisible(textField.getText().length() == 0);
					}
				});

				JOptionPane pane = new JOptionPane(panel, JOptionPane.QUESTION_MESSAGE, JOptionPane.OK_CANCEL_OPTION, null, null,
					null);

				pane.setInitialValue(null);
				pane.setComponentOrientation(JOptionPane.getRootFrame().getComponentOrientation());

				Border border = textField.getBorder();
				placeholderLabel.setBorder(new EmptyBorder(border.getBorderInsets(textField)));

				JDialog dialog = pane.createDialog(null, title);
				pane.selectInitialValue();

				dialog.addWindowFocusListener(new WindowFocusListener() {

					@Override
					public void windowLostFocus (WindowEvent arg0) {
					}

					@Override
					public void windowGainedFocus (WindowEvent arg0) {
						textField.requestFocusInWindow();
					}
				});

				dialog.setModal(true);
				dialog.setAlwaysOnTop(true);
				dialog.setVisible(true);
				dialog.dispose();

				Object selectedValue = pane.getValue();

				if ((selectedValue instanceof Integer)
						&& ((Integer) selectedValue).intValue() == JOptionPane.OK_OPTION) {
					listener.input(textField.getText());
				} else {
					listener.canceled();
				}

			}
		});
	}

	@Override
	public int getMaxPointers () {
		return 1;
	}

	@Override
	public int getX () {
		return (int)(Mouse.getX() * Display.getPixelScaleFactor());
	}

	@Override
	public int getY () {
		return BuildGdx.graphics.getHeight() - 1 - (int)(Mouse.getY() * Display.getPixelScaleFactor());
	}

	public boolean isAccelerometerAvailable () {
		return false;
	}

	public boolean isGyroscopeAvailable () {
		return false;
	}

	@Override
	public boolean isKeyPressed (int key) {
		if (key == Input.Keys.ANY_KEY)
			return pressedKeys > 0;

		int code;
		if((code = getLwjglKeyCode(key)) == 0) return false;

		return Keyboard.isCreated() && Keyboard.isKeyDown(code);
	}

	@Override
	public boolean isKeyJustPressed (int key) {
		if (key == Input.Keys.ANY_KEY) {
			return keyJustPressed;
		}
		if (key < 0 || key > 255) {
			return false;
		}
		return justPressedKeys[key];
	}

	@Override
	public boolean isTouched () {
		return Mouse.isButtonDown(0) || Mouse.isButtonDown(1) || Mouse.isButtonDown(2);
	}

	@Override
	public int getX (int pointer) {
		if (pointer > 0)
			return 0;
		else
			return getX();
	}

	@Override
	public int getY (int pointer) {
		if (pointer > 0)
			return 0;
		else
			return getY();
	}

	@Override
	public boolean isTouched (int pointer) {
		if (pointer > 0)
			return false;
		else
			return isTouched();
	}

	@Override
	public float getPressure () {
		return getPressure(0);
	}

	@Override
	public float getPressure (int pointer) {
		return isTouched(pointer) ? 1 : 0;
	}

	public boolean supportsMultitouch () {
		return false;
	}

	@Override
	public void setOnscreenKeyboardVisible (boolean visible) {

	}

	@Override
	public void setCatchBackKey (boolean catchBack) {

	}

	@Override
	public boolean isCatchBackKey () {
		return false;
	}

	@Override
	public void setCatchMenuKey (boolean catchMenu) {

	}

	@Override
	public boolean isCatchMenuKey () {
		return false;
	}

	@Override
	public void setCatchKey (int keycode, boolean catchKey) {

	}

	@Override
	public boolean isCatchKey (int keycode) {
		return false;
	}

	@Override
	public void processEvents () {
		synchronized (this) {
			if (processor != null) {
				InputProcessor processor = this.processor;
				int len = keyEvents.size();
				for (int i = 0; i < len; i++) {
					KeyEvent e = keyEvents.get(i);
					currentEventTimeStamp = e.timeStamp;
					switch (e.type) {
					case KeyEvent.KEY_DOWN:
						processor.keyDown(e.keyCode);
						break;
					case KeyEvent.KEY_UP:
						processor.keyUp(e.keyCode);
						break;
					case KeyEvent.KEY_TYPED:
						processor.keyTyped(e.keyChar);
					}
					usedKeyEvents.free(e);
				}

				len = touchEvents.size();
				for (int i = 0; i < len; i++) {
					TouchEvent e = touchEvents.get(i);
					currentEventTimeStamp = e.timeStamp;
					switch (e.type) {
					case TouchEvent.TOUCH_DOWN:
						processor.touchDown(e.x, e.y, e.pointer, e.button);
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
					}
					usedTouchEvents.free(e);
				}
			} else {
				int len = touchEvents.size();
				for (int i = 0; i < len; i++) {
					usedTouchEvents.free(touchEvents.get(i));
				}

				len = keyEvents.size();
				for (int i = 0; i < len; i++) {
					usedKeyEvents.free(keyEvents.get(i));
				}
			}

			keyEvents.clear();
			touchEvents.clear();
		}
	}

	protected int getGdxKeyCode(int eventKey) {
		switch (eventKey) {
		case Keyboard.KEY_PAUSE:
			return KEY_PAUSE;
		case Keyboard.KEY_CAPITAL:
			return KEY_CAPSLOCK;
		case Keyboard.KEY_SCROLL:
			return KEY_SCROLLOCK;
		case Keyboard.KEY_DECIMAL:
			return KEY_NUMDECIMAL;
		default:
			return com.badlogic.gdx.backends.lwjgl.LwjglInput.getGdxKeyCode(eventKey);
		}
	}

	protected int getLwjglKeyCode (int gdxKeyCode) {
		switch (gdxKeyCode) {
		case KEY_PAUSE:
			return Keyboard.KEY_PAUSE;
		case KEY_CAPSLOCK:
			return Keyboard.KEY_CAPITAL;
		case KEY_SCROLLOCK:
			return Keyboard.KEY_SCROLL;
		case KEY_NUMDECIMAL:
			return Keyboard.KEY_DECIMAL;
		default:
			return com.badlogic.gdx.backends.lwjgl.LwjglInput.getLwjglKeyCode(gdxKeyCode);
		}
	}

	@Override
	public void update () {
		updateTime();
		updateMouse();
		updateKeyboard();
	}

	private int toGdxButton (int button) {
		if (button == 0) return Buttons.LEFT;
		if (button == 1) return Buttons.RIGHT;
		if (button == 2) return Buttons.MIDDLE;
		if (button == 3) return Buttons.BACK;
		if (button == 4) return Buttons.FORWARD;
		return -1;
	}

	void updateTime () {
		long thisTime = System.nanoTime();
		deltaTime = (thisTime - lastTime) / 1000000000.0f;
		lastTime = thisTime;
	}

	void updateMouse () {
		if (justTouched) {
			justTouched = false;
			Arrays.fill(justPressedButtons, false);
		}
		if (Mouse.isCreated()) {
			int events = 0;
			while (Mouse.next()) {
				events++;
				int x = (int)(Mouse.getEventX() * Display.getPixelScaleFactor());
				int y = BuildGdx.graphics.getHeight() - (int)(Mouse.getEventY() * Display.getPixelScaleFactor()) - 1;
				int button = Mouse.getEventButton();
				int gdxButton = toGdxButton(button);
				if (button != -1 && gdxButton == -1) continue; // Ignore unknown button.

				TouchEvent event = usedTouchEvents.obtain();
				event.x = x;
				event.y = y;
				event.button = gdxButton;
				event.pointer = 0;
				event.timeStamp = Mouse.getEventNanoseconds();

				// could be drag, scroll or move
				if (button == -1) {
					if (Mouse.getEventDWheel() != 0) {
						event.type = TouchEvent.TOUCH_SCROLLED;
						event.scrollAmount = (int)-Math.signum(Mouse.getEventDWheel());
					} else if (pressedButtons.size > 0) {
						event.type = TouchEvent.TOUCH_DRAGGED;
					} else {
						event.type = TouchEvent.TOUCH_MOVED;
					}
				} else {
					// nope, it's a down or up event.
					if (Mouse.getEventButtonState()) {
						event.type = TouchEvent.TOUCH_DOWN;
						pressedButtons.add(event.button);
						justPressedButtons[event.button] = true;
						justTouched = true;
					} else {
						event.type = TouchEvent.TOUCH_UP;
						pressedButtons.remove(event.button);
					}
				}

				touchEvents.add(event);
				mouseX = event.x;
				mouseY = event.y;
				deltaX += (int)(Mouse.getEventDX() * Display.getPixelScaleFactor());
				deltaY += (int)(Mouse.getEventDY() * Display.getPixelScaleFactor());
			}

			if (events == 0) {
//				deltaX = 0;
//				deltaY = 0;
			} else {
				BuildGdx.graphics.requestRendering();
			}
		}
	}

	void updateKeyboard () {
		if (keyJustPressed) {
			keyJustPressed = false;
			Arrays.fill(justPressedKeys, false);
		}

		if (lastKeyCharPressed != 0) {
			keyRepeatTimer -= deltaTime;
			if (keyRepeatTimer < 0) {
				keyRepeatTimer = keyRepeatTime;

				KeyEvent event = usedKeyEvents.obtain();
				event.keyCode = 0;
				event.keyChar = lastKeyCharPressed;
				event.type = KeyEvent.KEY_TYPED;
				event.timeStamp = System.nanoTime();
				keyEvents.add(event);
				BuildGdx.graphics.requestRendering();
			}
		}

		if (Keyboard.isCreated()) {
			while (Keyboard.next()) {
				int keyCode = getGdxKeyCode(Keyboard.getEventKey());
				char keyChar = Keyboard.getEventCharacter();
				if (Keyboard.getEventKeyState() || (keyCode == 0 && keyChar != 0 && Character.isDefined(keyChar))) {
					long timeStamp = Keyboard.getEventNanoseconds();

					switch (keyCode) {
					case Keys.DEL:
						keyChar = 8;
						break;
					case Keys.FORWARD_DEL:
						keyChar = 127;
						break;
					}

					if (keyCode != 0) {
						KeyEvent event = usedKeyEvents.obtain();
						event.keyCode = keyCode;
						event.keyChar = 0;
						event.type = KeyEvent.KEY_DOWN;
						event.timeStamp = timeStamp;
						keyEvents.add(event);

						pressedKeys++;
						keyJustPressed = true;
						justPressedKeys[keyCode] = true;
						lastKeyCharPressed = keyChar;
						keyRepeatTimer = keyRepeatInitialTime;
					}

					KeyEvent event = usedKeyEvents.obtain();
					event.keyCode = 0;
					event.keyChar = keyChar;
					event.type = KeyEvent.KEY_TYPED;
					event.timeStamp = timeStamp;
					keyEvents.add(event);
				} else {
					KeyEvent event = usedKeyEvents.obtain();
					event.keyCode = keyCode;
					event.keyChar = 0;
					event.type = KeyEvent.KEY_UP;
					event.timeStamp = Keyboard.getEventNanoseconds();
					keyEvents.add(event);

					pressedKeys--;
					if(pressedKeys < 0)
						pressedKeys = 0;
					lastKeyCharPressed = 0;
				}
				BuildGdx.graphics.requestRendering();
			}
		}
	}

	@Override
	public void setInputProcessor (InputProcessor processor) {
		this.processor = processor;
	}

	@Override
	public InputProcessor getInputProcessor () {
		return this.processor;
	}

	@Override
	public void vibrate (int milliseconds) {
	}

	@Override
	public boolean justTouched () {
		return justTouched;
	}

	@Override
	public boolean isButtonPressed (int button) {
		if(button >= 5) return false;

		return Mouse.isButtonDown(button);
	}

	@Override
	public boolean isButtonJustPressed(int button) {
		if(button < 0 || button >= justPressedButtons.length) return false;
		return justPressedButtons[button];
	}

	@Override
	public void vibrate (long[] pattern, int repeat) {
	}

	@Override
	public void cancelVibrate () {
	}

	@Override
	public float getAzimuth () {
		return 0;
	}

	@Override
	public float getPitch () {
		return 0;
	}

	@Override
	public float getRoll () {
		return 0;
	}

	@Override
	public boolean isPeripheralAvailable (Peripheral peripheral) {
		return peripheral == Peripheral.HardwareKeyboard;
	}

	@Override
	public int getRotation () {
		return 0;
	}

	@Override
	public Orientation getNativeOrientation () {
		return Orientation.Landscape;
	}

	@Override
	public void setCursorCatched (boolean catched) {
		Mouse.setGrabbed(catched);
	}

	@Override
	public boolean isCursorCatched () {
		return Mouse.isGrabbed();
	}

	@Override
	public int getDeltaX() {
		int out = deltaX;
		deltaX = 0;
		return out;
	}

	@Override
	public int getDeltaX (int pointer) {
		if (pointer == 0)
			return getDeltaX();
		else
			return 0;
	}

	@Override
	public int getDeltaY() {
		int out = -deltaY;
		deltaY = 0;
		return out;
	}

	@Override
	public int getDeltaY (int pointer) {
		if (pointer == 0)
			return getDeltaY();
		else
			return 0;
	}

	@Override
	public void setCursorPosition (int x, int y) {
		Mouse.setCursorPosition(x, BuildGdx.graphics.getHeight() - 1 - y);
	}

	@Override
	public long getCurrentEventTime () {
		return currentEventTimeStamp;
	}

	@Override
	public void getRotationMatrix (float[] matrix) {}

	@Override
	public void processMessages() {
		Display.processMessages();
	}

	@Override
	public boolean cursorHandler() {
		try {
			if(emptyCursor == null)
				emptyCursor = new Cursor(1, 1, 0, 0, 1, BufferUtils.createIntBuffer(1), null);

			if (emptyCursor != null && Mouse.isInsideWindow() && Display.isActive())
				Mouse.setNativeCursor(emptyCursor);
			else Mouse.setNativeCursor(defCursor);
		} catch (Exception e) {}

		return false;
	}

	@Override
	public int getDWheel() {
		return Mouse.getDWheel();
	}

	class KeyEvent {
		static final int KEY_DOWN = 0;
		static final int KEY_UP = 1;
		static final int KEY_TYPED = 2;

		long timeStamp;
		int type;
		int keyCode;
		char keyChar;
	}

	class TouchEvent {
		static final int TOUCH_DOWN = 0;
		static final int TOUCH_UP = 1;
		static final int TOUCH_DRAGGED = 2;
		static final int TOUCH_SCROLLED = 3;
		static final int TOUCH_MOVED = 4;

		long timeStamp;
		int type;
		int x;
		int y;
		int scrollAmount;
		int button;
		int pointer;
	}

	@Override
	public void dispose() {
	}
}
