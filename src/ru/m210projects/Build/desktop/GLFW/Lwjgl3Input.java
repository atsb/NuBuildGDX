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

package ru.m210projects.Build.desktop.GLFW;

import static ru.m210projects.Build.Input.Keymap.KEY_CAPSLOCK;
import static ru.m210projects.Build.Input.Keymap.KEY_PAUSE;
import static ru.m210projects.Build.Input.Keymap.KEY_SCROLLOCK;
import static ru.m210projects.Build.Input.Keymap.KEY_NUMDECIMAL;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.util.Arrays;

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

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCharCallback;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWScrollCallback;

import com.badlogic.gdx.graphics.glutils.HdpiMode;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputEventQueue;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.utils.TimeUtils;

import ru.m210projects.Build.Architecture.BuildFrame;
import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.Architecture.BuildInput;

public class Lwjgl3Input implements BuildInput {

	private InputProcessor inputProcessor;
	private final InputEventQueue eventQueue = new InputEventQueue();
	private BuildFrame frame;
	private long windowHandle;

	private int mouseX, mouseY;
	private int mousePressed;
	private int deltaX, deltaY;
	private boolean justTouched;
	private int pressedKeys;
	private boolean keyJustPressed;
	private boolean[] justPressedKeys = new boolean[256];
	private boolean[] justPressedButtons = new boolean[5];
	private char lastCharacter;

	private GLFWKeyCallback keyCallback = new GLFWKeyCallback() {
		@Override
		public void invoke(long window, int key, int scancode, int action, int mods) {
			switch (action) {
			case GLFW.GLFW_PRESS:
				key = getGdxKeyCode(key);
				eventQueue.keyDown(key);
				pressedKeys++;
				keyJustPressed = true;
				justPressedKeys[key] = true;
				frame.getGraphics().requestRendering();
				lastCharacter = 0;
				char character = characterForKeyCode(key);
				if (character != 0) charCallback.invoke(window, character);
				break;
			case GLFW.GLFW_RELEASE:
				pressedKeys--;
				frame.getGraphics().requestRendering();
				eventQueue.keyUp(getGdxKeyCode(key));
				break;

			case GLFW.GLFW_REPEAT:
				if (lastCharacter != 0) {
					frame.getGraphics().requestRendering();
					eventQueue.keyTyped(lastCharacter);
				}
				break;
			}
		}
	};

	private GLFWCharCallback charCallback = new GLFWCharCallback() {
		@Override
		public void invoke(long window, int codepoint) {
			if ((codepoint & 0xff00) == 0xf700) return;
			lastCharacter = (char)codepoint;
			frame.getGraphics().requestRendering();
			eventQueue.keyTyped((char)codepoint);
		}
	};

	private GLFWScrollCallback scrollCallback = new GLFWScrollCallback() {
		private long pauseTime = 250000000L; //250ms
		private float scrollYRemainder;
		private long lastScrollEventTime;
		@Override
		public void invoke(long window, double scrollX, double scrollY) {
			frame.getGraphics().requestRendering();
			if (scrollYRemainder > 0 && scrollY < 0 || scrollYRemainder < 0 && scrollY > 0 ||
				TimeUtils.nanoTime() - lastScrollEventTime > pauseTime ) {
				// fire a scroll event immediately:
				//  - if the scroll direction changes;
				//  - if the user did not move the wheel for more than 250ms
				scrollYRemainder = 0;
				int scrollAmount = (int)-Math.signum(scrollY);
				eventQueue.scrolled(scrollAmount);
				lastScrollEventTime = TimeUtils.nanoTime();
			}
			else {
				scrollYRemainder += scrollY;
				while (Math.abs(scrollYRemainder) >= 1) {
					int scrollAmount = (int)-Math.signum(scrollY);
					eventQueue.scrolled(scrollAmount);
					lastScrollEventTime = TimeUtils.nanoTime();
					scrollYRemainder += scrollAmount;
				}
			}
		}
	};

	private GLFWCursorPosCallback cursorPosCallback = new GLFWCursorPosCallback() {
		private int logicalMouseY;
		private int logicalMouseX;

		@Override
		public void invoke(long windowHandle, double x, double y) {
			deltaX = (int)x - logicalMouseX;
			deltaY = (int)y - logicalMouseY;
			mouseX = logicalMouseX = (int)x;
			mouseY = logicalMouseY = (int)y;

			if(frame.getConfig().hdpiMode == HdpiMode.Pixels) {
				float xScale = frame.getGraphics().getBackBufferWidth() / (float)frame.getGraphics().getLogicalWidth();
				float yScale = frame.getGraphics().getBackBufferHeight() / (float)frame.getGraphics().getLogicalHeight();
				deltaX = (int)(deltaX * xScale);
				deltaY = (int)(deltaY * yScale);
				mouseX = (int)(mouseX * xScale);
				mouseY = (int)(mouseY * yScale);
			}

			frame.getGraphics().requestRendering();
			if (mousePressed > 0) {
				eventQueue.touchDragged(mouseX, mouseY, 0);
			} else {
				eventQueue.mouseMoved(mouseX, mouseY);
			}
		}
	};

	private GLFWMouseButtonCallback mouseButtonCallback = new GLFWMouseButtonCallback() {
		@Override
		public void invoke(long window, int button, int action, int mods) {
			int gdxButton = toGdxButton(button);
			if (button != -1 && gdxButton == -1) return;

			if (action == GLFW.GLFW_PRESS) {
				mousePressed++;
				justTouched = true;
				justPressedButtons[gdxButton] = true;
				frame.getGraphics().requestRendering();
				eventQueue.touchDown(mouseX, mouseY, 0, gdxButton);
			} else {
				mousePressed = Math.max(0, mousePressed - 1);
				frame.getGraphics().requestRendering();
				eventQueue.touchUp(mouseX, mouseY, 0, gdxButton);
			}
		}

		private int toGdxButton (int button) {
			if (button == 0) return Buttons.LEFT;
			if (button == 1) return Buttons.RIGHT;
			if (button == 2) return Buttons.MIDDLE;
			if (button == 3) return Buttons.BACK;
			if (button == 4) return Buttons.FORWARD;
			return -1;
		}
	};

	@Override
	public void init(BuildFrame frame) {
		this.frame = frame;
		Lwjgl3Graphics graphics = (Lwjgl3Graphics)frame.getGraphics();
		this.windowHandle = graphics.windowHandle;
		graphics.setPollingStatesCallback(new Runnable() {
			@Override
			public void run() {
				resetPollingStates();
			}
		});
		windowHandleChanged(windowHandle);
	}

	public void resetPollingStates() {
		justTouched = false;
		keyJustPressed = false;
		Arrays.fill(justPressedKeys, false);
		Arrays.fill(justPressedButtons, false);
		eventQueue.setProcessor(null);
		eventQueue.drain();
	}

	public void windowHandleChanged(long windowHandle) {
		resetPollingStates();
		GLFW.glfwSetKeyCallback(windowHandle, keyCallback);
		GLFW.glfwSetCharCallback(windowHandle, charCallback);
		GLFW.glfwSetScrollCallback(windowHandle, scrollCallback);
		GLFW.glfwSetCursorPosCallback(windowHandle, cursorPosCallback);
		GLFW.glfwSetMouseButtonCallback(windowHandle, mouseButtonCallback);
	}

	public void update() {
		eventQueue.setProcessor(inputProcessor);
		eventQueue.drain();
	}

	private boolean postRunnable = false;
	private Runnable prepareNext = new Runnable() {
		@Override
		public void run() {
			if(justTouched) {
				justTouched = false;
				Arrays.fill(justPressedButtons, false);
			}

			if (keyJustPressed) {
				keyJustPressed = false;
				Arrays.fill(justPressedKeys, false);
			}

			deltaX = 0;
			deltaY = 0;

			postRunnable = false;
		}
	};

	private void prepareNext()
	{
		if(postRunnable) return;

		BuildGdx.app.postRunnable(prepareNext);
		postRunnable = true;
	}

	@Override
	public int getMaxPointers () {
		return 1;
	}

	@Override
	public int getX() {
		return mouseX;
	}

	@Override
	public int getX(int pointer) {
		return pointer == 0 ? mouseX : 0;
	}

	@Override
	public int getDeltaX() {
		if(deltaX != 0)
			prepareNext();
		return deltaX;
	}

	@Override
	public int getDeltaX(int pointer) {
		return pointer == 0 ? getDeltaX() : 0;
	}

	@Override
	public int getY() {
		return mouseY;
	}

	@Override
	public int getY(int pointer) {
		return pointer == 0? mouseY: 0;
	}

	@Override
	public int getDeltaY() {
		if(deltaY != 0)
			prepareNext();
		return deltaY;
	}

	@Override
	public int getDeltaY(int pointer) {
		return pointer == 0 ? getDeltaY() : 0;
	}

	@Override
	public boolean isTouched() {
		return GLFW.glfwGetMouseButton(windowHandle, GLFW.GLFW_MOUSE_BUTTON_1) == GLFW.GLFW_PRESS ||
				GLFW.glfwGetMouseButton(windowHandle, GLFW.GLFW_MOUSE_BUTTON_2) == GLFW.GLFW_PRESS ||
				GLFW.glfwGetMouseButton(windowHandle, GLFW.GLFW_MOUSE_BUTTON_3) == GLFW.GLFW_PRESS ||
				GLFW.glfwGetMouseButton(windowHandle, GLFW.GLFW_MOUSE_BUTTON_4) == GLFW.GLFW_PRESS ||
				GLFW.glfwGetMouseButton(windowHandle, GLFW.GLFW_MOUSE_BUTTON_5) == GLFW.GLFW_PRESS;
	}

	@Override
	public boolean justTouched() {
		boolean out = justTouched;
		if(out)
			prepareNext();
		return out;
	}

	@Override
	public boolean isTouched(int pointer) {
		return pointer == 0 ? isTouched() : false;
	}

	@Override
	public float getPressure () {
		return getPressure(0);
	}

	@Override
	public float getPressure (int pointer) {
		return isTouched(pointer) ? 1 : 0;
	}

	@Override
	public boolean isButtonPressed(int button) {
		if(button >= 8) return false;
		return GLFW.glfwGetMouseButton(windowHandle, button) == GLFW.GLFW_PRESS;
	}

	@Override
	public boolean isButtonJustPressed(int button) {
		if(button < 0 || button >= justPressedButtons.length) {
			return false;
		}

		boolean out = justPressedButtons[button];
		if(out)
			prepareNext();
		return out;
	}

	@Override
	public boolean isKeyPressed(int key) {
		if (key == Input.Keys.ANY_KEY)
			return pressedKeys > 0;

		int code;
		if((code = getGlfwKeyCode(key)) == 0) return false;

		return GLFW.glfwGetKey(windowHandle, code) == GLFW.GLFW_PRESS;
	}

	@Override
	public boolean isKeyJustPressed(int key) {
		if (key < 0 || key > 256) {
			return false;
		}

		boolean out = false;
		if (key == Input.Keys.ANY_KEY) {
			out = keyJustPressed;
		} else out = justPressedKeys[key];
		if(out)
			prepareNext();
		return out;
	}

	@Override
	public void getTextInput(final TextInputListener listener, final String title, final String text, final String hint) {
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

				dialog.setModal(true);
				dialog.setAlwaysOnTop(true);
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
	public long getCurrentEventTime() {
		// queue sets its event time for each event dequeued/processed
		return eventQueue.getCurrentEventTime();
	}

	@Override
	public void setInputProcessor(InputProcessor processor) {
		this.inputProcessor = processor;
	}

	@Override
	public InputProcessor getInputProcessor() {
		return inputProcessor;
	}

	@Override
	public void setCursorCatched(boolean catched) {
		GLFW.glfwSetInputMode(windowHandle, GLFW.GLFW_CURSOR, catched ? GLFW.GLFW_CURSOR_DISABLED : GLFW.GLFW_CURSOR_NORMAL);
	}

	@Override
	public boolean isCursorCatched() {
		return GLFW.glfwGetInputMode(windowHandle, GLFW.GLFW_CURSOR) == GLFW.GLFW_CURSOR_DISABLED;
	}

	@Override
	public void setCursorPosition(int x, int y) {
		if(frame.getConfig().hdpiMode == HdpiMode.Pixels) {
			float xScale = frame.getGraphics().getLogicalWidth() / (float)frame.getGraphics().getBackBufferWidth();
			float yScale = frame.getGraphics().getLogicalHeight() / (float)frame.getGraphics().getBackBufferHeight();
			x = (int)(x * xScale);
			y = (int)(y * yScale);
		}
		mouseX = x;
		mouseY = y;
		GLFW.glfwSetCursorPos(windowHandle, x, y);
	}

	private char characterForKeyCode (int key) {
		// Map certain key codes to character codes.
		switch (key) {
		case Keys.BACKSPACE:
			return 8;
		case Keys.TAB:
			return '\t';
		case Keys.FORWARD_DEL:
			return 127;
		case Keys.ENTER:
			return '\n';
		}
		return 0;
	}

	protected int getGdxKeyCode (int lwjglKeyCode) {
		switch (lwjglKeyCode) {
            default:
			return com.badlogic.gdx.backends.lwjgl3.Lwjgl3Input.getGdxKeyCode(lwjglKeyCode);
		}
	}

	protected int getGlfwKeyCode (int gdxKeyCode) {
		switch (gdxKeyCode) {
		case KEY_PAUSE:
			return 0; //Keyboard.KEY_PAUSE;
		case KEY_CAPSLOCK:
			return 0; //return Keyboard.KEY_CAPITAL;
		case KEY_SCROLLOCK:
			return 0; //return Keyboard.KEY_SCROLL;
		case KEY_NUMDECIMAL:
			return 0; //return Keyboard.KEY_DECIMAL;
		default:
			return com.badlogic.gdx.backends.lwjgl3.Lwjgl3Input.getGlfwKeyCode(gdxKeyCode);
		}
	}

	@Override
	public void processEvents() {
	}

	@Override
	public void processMessages() {
	}

	@Override
	public boolean cursorHandler() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getDWheel() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void dispose() {
		keyCallback.free();
		charCallback.free();
		scrollCallback.free();
		cursorPosCallback.free();
		mouseButtonCallback.free();
	}

	// --------------------------------------------------------------------------
	// -------------------------- Nothing to see below this line except for stubs
	// --------------------------------------------------------------------------
	@Override
	public void setCatchBackKey(boolean catchBack) {
	}

	@Override
	public boolean isCatchBackKey() {
		return false;
	}

	@Override
	public void setCatchMenuKey(boolean catchMenu) {
	}

	@Override
	public boolean isCatchMenuKey() {
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
	public float getAccelerometerX() {
		return 0;
	}

	@Override
	public float getAccelerometerY() {
		return 0;
	}

	@Override
	public float getAccelerometerZ() {
		return 0;
	}

	@Override
	public boolean isPeripheralAvailable(Peripheral peripheral) {
		return peripheral == Peripheral.HardwareKeyboard;
	}

	@Override
	public int getRotation() {
		return 0;
	}

	@Override
	public Orientation getNativeOrientation() {
		return Orientation.Landscape;
	}

	@Override
	public void setOnscreenKeyboardVisible(boolean visible) {
	}

	@Override
	public void vibrate(int milliseconds) {
	}

	@Override
	public void vibrate(long[] pattern, int repeat) {
	}

	@Override
	public void cancelVibrate() {
	}

	@Override
	public float getAzimuth() {
		return 0;
	}

	@Override
	public float getPitch() {
		return 0;
	}

	@Override
	public float getRoll() {
		return 0;
	}

	@Override
	public void getRotationMatrix(float[] matrix) {
	}

	@Override
	public float getGyroscopeX() {
		return 0;
	}

	@Override
	public float getGyroscopeY() {
		return 0;
	}

	@Override
	public float getGyroscopeZ() {
		return 0;
	}
}
