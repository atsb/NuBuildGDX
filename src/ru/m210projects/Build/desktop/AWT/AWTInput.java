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

import static ru.m210projects.Build.Input.Keymap.KEY_CAPSLOCK;
import static ru.m210projects.Build.Input.Keymap.KEY_NUMDECIMAL;
import static ru.m210projects.Build.Input.Keymap.KEY_PAUSE;
import static ru.m210projects.Build.Input.Keymap.KEY_SCROLLOCK;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.KeyListener;
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

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.utils.Pool;

import ru.m210projects.Build.Architecture.BuildFrame;
import ru.m210projects.Build.Architecture.BuildInput;

public class AWTInput implements BuildInput, KeyListener {
	static class KeyEvent {
		static final int KEY_DOWN = 0;
		static final int KEY_UP = 1;
		static final int KEY_TYPED = 2;

		long timeStamp;
		int type;
		int keyCode;
		char keyChar;
	}

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

	Pool<KeyEvent> usedKeyEvents = new Pool<KeyEvent>(16, 1000) {
		@Override
		protected KeyEvent newObject () {
			return new KeyEvent();
		}
	};

	protected BuildFrame frame;

	List<KeyEvent> keyEvents = new ArrayList<KeyEvent>();
	int keyCount = 0;
	boolean[] keys = new boolean[256];
	boolean keyJustPressed = false;
	boolean[] justPressedKeys = new boolean[256];

	InputProcessor processor;
	JCanvas canvas;

	MouseInterface mouse;
	long currentEventTimeStamp;

	protected void reset()
	{
		keyJustPressed = false;
		Arrays.fill(justPressedKeys, false);
		keyCount = 0;
		Arrays.fill(keys, false);

		mouse.reset();
	}

	@Override
	public void update() {
		if(!frame.isActive())
			reset();

		synchronized (this) {
			if (keyJustPressed) {
				keyJustPressed = false;
				Arrays.fill(justPressedKeys, false);
			}

			if (processor != null) {
				InputProcessor processor = this.processor;

				int len = keyEvents.size();
				for (int i = 0; i < len; i++) {
					KeyEvent e = keyEvents.get(i);
					currentEventTimeStamp = e.timeStamp;
					switch (e.type) {
						case KeyEvent.KEY_DOWN:
							processor.keyDown(e.keyCode);
							keyJustPressed = true;
							justPressedKeys[e.keyCode] = true;
							break;
						case KeyEvent.KEY_UP:
							processor.keyUp(e.keyCode);
							break;
						case KeyEvent.KEY_TYPED:
							processor.keyTyped(e.keyChar);
					}
					usedKeyEvents.free(e);
				}
			} else {
				int len = keyEvents.size();
				for (int i = 0; i < len; i++) {
					KeyEvent event = keyEvents.get(i);
					if (event.type == KeyEvent.KEY_DOWN) {
						keyJustPressed = true;
						justPressedKeys[event.keyCode] = true;
					}

					usedKeyEvents.free(keyEvents.get(i));
				}
			}

			keyEvents.clear();

			long out = mouse.processEvents(processor);
			if(out != -1)
				currentEventTimeStamp = out;
		}
	}

	@Override
	public void init(BuildFrame frame)
	{
		this.frame = frame;
		JDisplay display = ((AWTGraphics) frame.getGraphics()).display;
		this.mouse = new AWTMouse(display);

		this.setListeners(display.getCanvas());
	}

	public void setListeners (JCanvas canvas) {
		if (this.canvas != null)
			canvas.removeKeyListener(this);
		canvas.addKeyListener(this);
		canvas.setFocusTraversalKeysEnabled(false);
		this.canvas = canvas;
	}

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
	public void getTextInput (final TextInputListener listener, final String title, final String text, final String hint) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run () {
				JPanel panel = new JPanel(new FlowLayout());

				JPanel textPanel = new JPanel() {
					private static final long serialVersionUID = -4257888082210622881L;

					@Override
					public boolean isOptimizedDrawingEnabled () {
						return false;
					};
				};

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
						if (textField.getText().length() == 0)
							placeholderLabel.setVisible(true);
						else
							placeholderLabel.setVisible(false);
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

				dialog.setVisible(true);
				dialog.dispose();

				Object selectedValue = pane.getValue();

				if (selectedValue != null && (selectedValue instanceof Integer)
						&& ((Integer)selectedValue).intValue() == JOptionPane.OK_OPTION) {
					listener.input(textField.getText());
				} else {
					listener.canceled();
				}

			}
		});
	}

	@Override
	public int getX() {
		return mouse.getX();
	}

	@Override
	public int getX (int pointer) {
		if (pointer == 0)
			return getX();
		else
			return 0;
	}

	@Override
	public int getY() {
		return mouse.getY();
	}

	@Override
	public int getY (int pointer) {
		if (pointer == 0)
			return getY();
		else
			return 0;
	}

	@Override
	public boolean isKeyPressed (int key) {
		if (key == Input.Keys.ANY_KEY) {
			return keyCount > 0;
		}
		if (key < 0 || key > 255) {
			return false;
		}
		return keys[key];
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
	public boolean isTouched() {
		return mouse.isTouched();
	}

	@Override
	public boolean isTouched (int pointer) {
		if (pointer == 0)
			return isTouched();
		else
			return false;
	}

	@Override
	public void processEvents () { }

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
	public void setOnscreenKeyboardVisible (boolean visible) {

	}

	@Override
	public void keyPressed (java.awt.event.KeyEvent e) {
		synchronized (this) {
			KeyEvent event = usedKeyEvents.obtain();
			event.keyChar = 0;
			event.keyCode = translateKeyCode(e);
			event.type = KeyEvent.KEY_DOWN;
			event.timeStamp = System.nanoTime();
			keyEvents.add(event);
			if (!keys[event.keyCode]) {
				keyCount++;
				keys[event.keyCode] = true;
			}
		}
	}

	@Override
	public void keyReleased (java.awt.event.KeyEvent e) {
		synchronized (this) {
			KeyEvent event = usedKeyEvents.obtain();
			event.keyChar = 0;
			event.keyCode = translateKeyCode(e);
			event.type = KeyEvent.KEY_UP;
			event.timeStamp = System.nanoTime();
			keyEvents.add(event);
			if (keys[event.keyCode]) {
				keyCount--;
				keys[event.keyCode] = false;
			}
		}
	}

	@Override
	public void keyTyped (java.awt.event.KeyEvent e) {
		synchronized (this) {
			KeyEvent event = usedKeyEvents.obtain();
			event.keyChar = e.getKeyChar();
			event.keyCode = 0;
			event.type = KeyEvent.KEY_TYPED;
			event.timeStamp = System.nanoTime();
			keyEvents.add(event);
		}
	}

	protected static int translateKeyCode (java.awt.event.KeyEvent ke) {
		switch (ke.getKeyCode()) {
			case java.awt.event.KeyEvent.VK_MULTIPLY:
				return Keys.STAR;
			case java.awt.event.KeyEvent.VK_DECIMAL:
				return KEY_NUMDECIMAL;
			case java.awt.event.KeyEvent.VK_PAUSE:
				return KEY_PAUSE;
			case java.awt.event.KeyEvent.VK_CAPS_LOCK:
				return KEY_CAPSLOCK;
			case java.awt.event.KeyEvent.VK_SCROLL_LOCK:
				return KEY_SCROLLOCK;
			case java.awt.event.KeyEvent.VK_BACK_SPACE:
				return Keys.BACKSPACE;
			case java.awt.event.KeyEvent.VK_LEFT:
				return Keys.LEFT;
			case java.awt.event.KeyEvent.VK_RIGHT:
				return Keys.RIGHT;
			case java.awt.event.KeyEvent.VK_UP:
				return Keys.UP;
			case java.awt.event.KeyEvent.VK_DOWN:
				return Keys.DOWN;
			case java.awt.event.KeyEvent.VK_QUOTE:
				return Keys.APOSTROPHE;
			case java.awt.event.KeyEvent.VK_OPEN_BRACKET:
				return Keys.LEFT_BRACKET;
			case java.awt.event.KeyEvent.VK_CLOSE_BRACKET:
				return Keys.RIGHT_BRACKET;
			case java.awt.event.KeyEvent.VK_BACK_QUOTE:
				return Keys.GRAVE;
			case java.awt.event.KeyEvent.VK_NUM_LOCK:
				return Keys.NUM;
			case java.awt.event.KeyEvent.VK_EQUALS:
				return Keys.EQUALS;
			case java.awt.event.KeyEvent.VK_0:
				return Keys.NUM_0;
			case java.awt.event.KeyEvent.VK_1:
				return Keys.NUM_1;
			case java.awt.event.KeyEvent.VK_2:
				return Keys.NUM_2;
			case java.awt.event.KeyEvent.VK_3:
				return Keys.NUM_3;
			case java.awt.event.KeyEvent.VK_4:
				return Keys.NUM_4;
			case java.awt.event.KeyEvent.VK_5:
				return Keys.NUM_5;
			case java.awt.event.KeyEvent.VK_6:
				return Keys.NUM_6;
			case java.awt.event.KeyEvent.VK_7:
				return Keys.NUM_7;
			case java.awt.event.KeyEvent.VK_8:
				return Keys.NUM_8;
			case java.awt.event.KeyEvent.VK_9:
				return Keys.NUM_9;
			case java.awt.event.KeyEvent.VK_A:
				return Keys.A;
			case java.awt.event.KeyEvent.VK_B:
				return Keys.B;
			case java.awt.event.KeyEvent.VK_C:
				return Keys.C;
			case java.awt.event.KeyEvent.VK_D:
				return Keys.D;
			case java.awt.event.KeyEvent.VK_E:
				return Keys.E;
			case java.awt.event.KeyEvent.VK_F:
				return Keys.F;
			case java.awt.event.KeyEvent.VK_G:
				return Keys.G;
			case java.awt.event.KeyEvent.VK_H:
				return Keys.H;
			case java.awt.event.KeyEvent.VK_I:
				return Keys.I;
			case java.awt.event.KeyEvent.VK_J:
				return Keys.J;
			case java.awt.event.KeyEvent.VK_K:
				return Keys.K;
			case java.awt.event.KeyEvent.VK_L:
				return Keys.L;
			case java.awt.event.KeyEvent.VK_M:
				return Keys.M;
			case java.awt.event.KeyEvent.VK_N:
				return Keys.N;
			case java.awt.event.KeyEvent.VK_O:
				return Keys.O;
			case java.awt.event.KeyEvent.VK_P:
				return Keys.P;
			case java.awt.event.KeyEvent.VK_Q:
				return Keys.Q;
			case java.awt.event.KeyEvent.VK_R:
				return Keys.R;
			case java.awt.event.KeyEvent.VK_S:
				return Keys.S;
			case java.awt.event.KeyEvent.VK_T:
				return Keys.T;
			case java.awt.event.KeyEvent.VK_U:
				return Keys.U;
			case java.awt.event.KeyEvent.VK_V:
				return Keys.V;
			case java.awt.event.KeyEvent.VK_W:
				return Keys.W;
			case java.awt.event.KeyEvent.VK_X:
				return Keys.X;
			case java.awt.event.KeyEvent.VK_Y:
				return Keys.Y;
			case java.awt.event.KeyEvent.VK_Z:
				return Keys.Z;
			case java.awt.event.KeyEvent.VK_ALT:
				ke.consume();
				if(ke.getKeyLocation() == java.awt.event.KeyEvent.KEY_LOCATION_LEFT)
					return Keys.ALT_LEFT;
				return Keys.ALT_RIGHT;
			case java.awt.event.KeyEvent.VK_BACK_SLASH:
				return Keys.BACKSLASH;
			case java.awt.event.KeyEvent.VK_COMMA:
				return Keys.COMMA;
			case java.awt.event.KeyEvent.VK_DELETE:
				return Keys.FORWARD_DEL;
			case java.awt.event.KeyEvent.VK_ENTER:
				return Keys.ENTER;
			case java.awt.event.KeyEvent.VK_HOME:
				return Keys.HOME;
			case java.awt.event.KeyEvent.VK_END:
				return Keys.END;
			case java.awt.event.KeyEvent.VK_PAGE_DOWN:
				return Keys.PAGE_DOWN;
			case java.awt.event.KeyEvent.VK_PAGE_UP:
				return Keys.PAGE_UP;
			case java.awt.event.KeyEvent.VK_INSERT:
				return Keys.INSERT;
			case java.awt.event.KeyEvent.VK_SUBTRACT:
			case java.awt.event.KeyEvent.VK_MINUS:
				return Keys.MINUS;
			case java.awt.event.KeyEvent.VK_PERIOD:
				return Keys.PERIOD;
			case java.awt.event.KeyEvent.VK_ADD:
			case java.awt.event.KeyEvent.VK_PLUS:
				return Keys.PLUS;
			case java.awt.event.KeyEvent.VK_SEMICOLON:
				return Keys.SEMICOLON;
			case java.awt.event.KeyEvent.VK_SHIFT:
				if(ke.getKeyLocation() == java.awt.event.KeyEvent.KEY_LOCATION_LEFT)
					return Keys.SHIFT_LEFT;
				return Keys.SHIFT_RIGHT;
			case java.awt.event.KeyEvent.VK_SLASH:
			case java.awt.event.KeyEvent.VK_DIVIDE:
				return Keys.SLASH;
			case java.awt.event.KeyEvent.VK_SPACE:
				return Keys.SPACE;
			case java.awt.event.KeyEvent.VK_TAB:
				return Keys.TAB;
			case java.awt.event.KeyEvent.VK_CONTROL:
				if(ke.getKeyLocation() == java.awt.event.KeyEvent.KEY_LOCATION_LEFT)
					return Keys.CONTROL_LEFT;
				return Keys.CONTROL_RIGHT;
			case java.awt.event.KeyEvent.VK_ESCAPE:
				return Keys.ESCAPE;
			case java.awt.event.KeyEvent.VK_F1:
				return Keys.F1;
			case java.awt.event.KeyEvent.VK_F2:
				return Keys.F2;
			case java.awt.event.KeyEvent.VK_F3:
				return Keys.F3;
			case java.awt.event.KeyEvent.VK_F4:
				return Keys.F4;
			case java.awt.event.KeyEvent.VK_F5:
				return Keys.F5;
			case java.awt.event.KeyEvent.VK_F6:
				return Keys.F6;
			case java.awt.event.KeyEvent.VK_F7:
				return Keys.F7;
			case java.awt.event.KeyEvent.VK_F8:
				return Keys.F8;
			case java.awt.event.KeyEvent.VK_F9:
				return Keys.F9;
			case java.awt.event.KeyEvent.VK_F10:
				return Keys.F10;
			case java.awt.event.KeyEvent.VK_F11:
				return Keys.F11;
			case java.awt.event.KeyEvent.VK_F12:
				return Keys.F12;
			case java.awt.event.KeyEvent.VK_COLON:
				return Keys.COLON;
			case java.awt.event.KeyEvent.VK_NUMPAD0:
				return Keys.NUMPAD_0;
			case java.awt.event.KeyEvent.VK_NUMPAD1:
				return Keys.NUMPAD_1;
			case java.awt.event.KeyEvent.VK_NUMPAD2:
				return Keys.NUMPAD_2;
			case java.awt.event.KeyEvent.VK_NUMPAD3:
				return Keys.NUMPAD_3;
			case java.awt.event.KeyEvent.VK_NUMPAD4:
				return Keys.NUMPAD_4;
			case java.awt.event.KeyEvent.VK_NUMPAD5:
				return Keys.NUMPAD_5;
			case java.awt.event.KeyEvent.VK_NUMPAD6:
				return Keys.NUMPAD_6;
			case java.awt.event.KeyEvent.VK_NUMPAD7:
				return Keys.NUMPAD_7;
			case java.awt.event.KeyEvent.VK_NUMPAD8:
				return Keys.NUMPAD_8;
			case java.awt.event.KeyEvent.VK_NUMPAD9:
				return Keys.NUMPAD_9;
		}
		return Input.Keys.UNKNOWN;
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
		return mouse.justTouched();
	}

	@Override
	public boolean isButtonPressed (int button) {
		return mouse.isButtonPressed(button);
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
		if (peripheral == Peripheral.HardwareKeyboard) return true;
		return false;
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
		mouse.setCursorCatched(catched);
	}

	@Override
	public boolean isCursorCatched () {
		return mouse.isCursorCatched();
	}

	@Override
	public int getDeltaX () {
		return mouse.getDeltaX();
	}

	@Override
	public int getDeltaX (int pointer) {
		if (pointer == 0) return getDeltaX();
		return 0;
	}

	@Override
	public int getDeltaY () {
		return mouse.getDeltaY();
	}

	@Override
	public int getDeltaY (int pointer) {
		if (pointer == 0) return getDeltaY();
		return 0;
	}

	@Override
	public void setCursorPosition (int x, int y) {
		if(!isInsideWindow() || !frame.isActive())
			return;

		mouse.setCursorPosition(x, y);
	}

	@Override
	public long getCurrentEventTime () {
		return currentEventTimeStamp;
	}

	@Override
	public void getRotationMatrix (float[] matrix) {
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
	public void processMessages() {}

	@Override
	public boolean cursorHandler() {
		if (isInsideWindow() && frame.isActive())
			mouse.showCursor(false);
		else {
			if(frame.isActive())
				mouse.reset();
			mouse.showCursor(true);
		}

		return false;
	}

	@Override
	public int getDWheel() {
		return mouse.getDWheel();
	}

	public boolean isInsideWindow() {
		return mouse.isInsideWindow();
	}

	@Override
	public int getMaxPointers() {
		return 1;
	}

	@Override
	public float getPressure() {
		return getPressure(0);
	}

	@Override
	public float getPressure (int pointer) {
		return isTouched(pointer) ? 1 : 0;
	}

	@Override
	public boolean isButtonJustPressed(int button) {
		return mouse.isButtonJustPressed(button);
	}

	@Override
	public boolean isCatchKey(int arg0) {
		return false;
	}

	@Override
	public void setCatchKey(int arg0, boolean arg1) {
	}

	@Override
	public void dispose() {
	}
}