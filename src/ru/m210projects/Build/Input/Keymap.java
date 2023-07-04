// This file is part of BuildGDX.
// Copyright (C) 2017-2018  Alexander Makarov-[M210] (m210-2007@mail.ru)
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

package ru.m210projects.Build.Input;

import static com.badlogic.gdx.Input.Keys.*;

import com.badlogic.gdx.utils.ObjectIntMap;

public class Keymap {
	
	public static final int MOUSE_WHELLUP = 0xE0;
	public static final int MOUSE_WHELLDN = 0xE1;
	public static final int MOUSE_LBUTTON = 0xE2;
	public static final int MOUSE_RBUTTON = 0xE3;
	public static final int MOUSE_MBUTTON = 0xE4;
	public static final int MOUSE_BUTTON4 = 0xE5;
	public static final int MOUSE_BUTTON5 = 0xE6;
	public static final int MOUSE_BUTTON6 = 0xE7;
	public static final int MOUSE_BUTTON7 = 0xE8;
	public static final int MOUSE_BUTTON8 = 0xE9;
	public static final int MOUSE_BUTTON9 = 0xEA;
	public static final int MOUSE_BUTTON10 = 0xEB;
	public static final int MOUSE_BUTTON11 = 0xEC;
	public static final short ANYKEY = 0x100;
	
	public static final int KEY_PAUSE = 0xED;
	public static final int KEY_CAPSLOCK = 0xEE;
	public static final int KEY_SCROLLOCK = 0xEF;
	public static final int KEY_NUMDECIMAL = 0xF0;

	public static String toString (int libgdx_keycode) {
		if(libgdx_keycode < 0 || libgdx_keycode > 255)
			libgdx_keycode = 0;
		
		switch (libgdx_keycode) {
		case UNKNOWN:
			return "N/A";
		case KEY_CAPSLOCK:
			return "Caps Lock";
		case KEY_SCROLLOCK:
			return "Scroll Lock";
		case KEY_NUMDECIMAL:
			return "Numpad decimal";
		case SOFT_LEFT:
			return "Soft Left";
		case SOFT_RIGHT:
			return "Soft Right";
		case HOME:
			return "Home";
		case BACK:
			return "Back";
		case CALL:
			return "Call";
		case ENDCALL:
			return "End Call";
		case NUM_0:
			return "0";
		case NUM_1:
			return "1";
		case NUM_2:
			return "2";
		case NUM_3:
			return "3";
		case NUM_4:
			return "4";
		case NUM_5:
			return "5";
		case NUM_6:
			return "6";
		case NUM_7:
			return "7";
		case NUM_8:
			return "8";
		case NUM_9:
			return "9";
		case STAR:
			return "*";
		case POUND:
			return "#";
		case UP:
			return "Up";
		case DOWN:
			return "Down";
		case LEFT:
			return "Left";
		case RIGHT:
			return "Right";
		case CENTER:
			return "Center";
		case VOLUME_UP:
			return "Volume Up";
		case VOLUME_DOWN:
			return "Volume Down";
		case POWER:
			return "Power";
		case CAMERA:
			return "Camera";
		case CLEAR:
			return "Clear";
		case A:
			return "A";
		case B:
			return "B";
		case C:
			return "C";
		case D:
			return "D";
		case E:
			return "E";
		case F:
			return "F";
		case G:
			return "G";
		case H:
			return "H";
		case I:
			return "I";
		case J:
			return "J";
		case K:
			return "K";
		case L:
			return "L";
		case M:
			return "M";
		case N:
			return "N";
		case O:
			return "O";
		case P:
			return "P";
		case Q:
			return "Q";
		case R:
			return "R";
		case S:
			return "S";
		case T:
			return "T";
		case U:
			return "U";
		case V:
			return "V";
		case W:
			return "W";
		case X:
			return "X";
		case Y:
			return "Y";
		case Z:
			return "Z";
		case COMMA:
			return ",";
		case PERIOD:
			return ".";
		case ALT_LEFT:
			return "L-Alt";
		case ALT_RIGHT:
			return "R-Alt";
		case SHIFT_LEFT:
			return "L-Shift";
		case SHIFT_RIGHT:
			return "R-Shift";
		case TAB:
			return "Tab";
		case SPACE:
			return "Space";
		case SYM:
			return "SYM";
		case EXPLORER:
			return "Explorer";
		case ENVELOPE:
			return "Envelope";
		case ENTER:
			return "Enter";
		case DEL:
			return "Backspace";
		case GRAVE:
			return "GRAVE";
		case MINUS:
			return "-";
		case EQUALS:
			return "=";
		case LEFT_BRACKET:
			return "[";
		case RIGHT_BRACKET:
			return "]";
		case BACKSLASH:
			return "\\";
		case SEMICOLON:
			return ";";
		case APOSTROPHE:
			return "'";
		case SLASH:
			return "/";
		case AT:
			return "@";
		case NUM:
			return "Num";
		case HEADSETHOOK:
			return "Headset Hook";
		case FOCUS:
			return "Focus";
		case PLUS:
			return "Plus";
		case MENU:
			return "Menu";
		case NOTIFICATION:
			return "Notification";
		case SEARCH:
			return "Search";
		case MEDIA_PLAY_PAUSE:
			return "Play/Pause";
		case MEDIA_STOP:
			return "Stop Media";
		case MEDIA_NEXT:
			return "Next Media";
		case MEDIA_PREVIOUS:
			return "Prev Media";
		case MEDIA_REWIND:
			return "Rewind";
		case MEDIA_FAST_FORWARD:
			return "Fast Forward";
		case MUTE:
			return "Mute";
		case PAGE_UP:
			return "Page Up";
		case PAGE_DOWN:
			return "Page Down";
		case PICTSYMBOLS:
			return "PICTSYMBOLS";
		case SWITCH_CHARSET:
			return "SWITCH_CHARSET";
		case BUTTON_A:
			return "A Button";
		case BUTTON_B:
			return "B Button";
		case BUTTON_C:
			return "C Button";
		case BUTTON_X:
			return "X Button";
		case BUTTON_Y:
			return "Y Button";
		case BUTTON_Z:
			return "Z Button";
		case BUTTON_L1:
			return "L1 Button";
		case BUTTON_R1:
			return "R1 Button";
		case BUTTON_L2:
			return "L2 Button";
		case BUTTON_R2:
			return "R2 Button";
		case BUTTON_THUMBL:
			return "Left Thumb";
		case BUTTON_THUMBR:
			return "Right Thumb";
		case BUTTON_START:
			return "Start";
		case BUTTON_SELECT:
			return "Select";
		case BUTTON_MODE:
			return "Button Mode";
		case FORWARD_DEL:
			return "Delete";
		case CONTROL_LEFT:
			return "L-Ctrl";
		case CONTROL_RIGHT:
			return "R-Ctrl";
		case ESCAPE:
			return "Escape";
		case END:
			return "End";
		case INSERT:
			return "Insert";
		case NUMPAD_0:
			return "Numpad 0";
		case NUMPAD_1:
			return "Numpad 1";
		case NUMPAD_2:
			return "Numpad 2";
		case NUMPAD_3:
			return "Numpad 3";
		case NUMPAD_4:
			return "Numpad 4";
		case NUMPAD_5:
			return "Numpad 5";
		case NUMPAD_6:
			return "Numpad 6";
		case NUMPAD_7:
			return "Numpad 7";
		case NUMPAD_8:
			return "Numpad 8";
		case NUMPAD_9:
			return "Numpad 9";
		case COLON:
			return ":";
		case F1:
			return "F1";
		case F2:
			return "F2";
		case F3:
			return "F3";
		case F4:
			return "F4";
		case F5:
			return "F5";
		case F6:
			return "F6";
		case F7:
			return "F7";
		case F8:
			return "F8";
		case F9:
			return "F9";
		case F10:
			return "F10";
		case F11:
			return "F11";
		case F12:
			return "F12";
			// BUTTON_CIRCLE unhandled, as it conflicts with the more likely to be pressed F12
		case MOUSE_LBUTTON:
			return "MOUSE1";	
		case MOUSE_RBUTTON:
			return "MOUSE2";	
		case MOUSE_MBUTTON:
			return "MOUSE3";	
		case MOUSE_WHELLUP:
			return "MWHEELUP";	
		case MOUSE_WHELLDN:
			return "MWHEELDN";	
		case MOUSE_BUTTON4:
			return "MOUSE4";
		case MOUSE_BUTTON5:
			return "MOUSE5";	
		case MOUSE_BUTTON6:
			return "MOUSE6";	
		case MOUSE_BUTTON7:
			return "MOUSE7";	
		case MOUSE_BUTTON8:
			return "MOUSE8";	
		case MOUSE_BUTTON9:
			return "MOUSE9";	
		case MOUSE_BUTTON10:
			return "MOUSE10";	
		case MOUSE_BUTTON11:
			return "MOUSE11";	

		default:
			// key name not found
			return Integer.toString(libgdx_keycode);
		}
	}
	
	private static ObjectIntMap<String> keyNames;

	public static int valueOf (String keyname) {
		if (keyNames == null) initializeKeyNames();
		return keyNames.get(keyname, -1);
	}

	private static void initializeKeyNames () {
		keyNames = new ObjectIntMap<String>();
		for (int i = 0; i < 256; i++) {
			String name = toString(i);
			if (name != null) keyNames.put(name, i);
		}
	}
}
