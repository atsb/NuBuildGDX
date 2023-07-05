// This file is part of LSPGDX.
// Copyright (C) 2020  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// LSPGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// LSPGDX is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with LSPGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.LSP;

import static ru.m210projects.Build.OnSceenDisplay.Console.OSDTEXT_YELLOW;
import static ru.m210projects.Build.Input.Keymap.KEY_CAPSLOCK;
import static ru.m210projects.Build.Input.Keymap.MOUSE_LBUTTON;
import static ru.m210projects.Build.Input.Keymap.MOUSE_RBUTTON;
import static ru.m210projects.Build.Input.Keymap.MOUSE_WHELLDN;
import static ru.m210projects.Build.Input.Keymap.MOUSE_WHELLUP;
import static ru.m210projects.Build.Gameutils.*;


import com.badlogic.gdx.Input.Keys;

import ru.m210projects.Build.FileHandle.FileResource;
import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Build.Settings.BuildConfig;

public class Config extends BuildConfig {
	
	public boolean gAutoRun;
	public int gOverlayMap;
	public boolean gShowMessages;
	public boolean gCrosshair;
	public int gCrossSize;
	public int gShowStat;
	public int gHUDSize;
	public int showMapInfo;
	public boolean bHeadBob;
	public boolean bOriginal;
	public boolean bShowExit;
	
	public enum LSPKeys implements KeyType {
		Weapon_1,
		Weapon_2,
		Weapon_3,
		Last_Used_Weapon,
		Map_Follow_Mode,
		Toggle_Crosshair,
		AutoRun,
		Quickload,
		Quicksave,
		Show_Savemenu,
		Show_Loadmenu,
		Show_Sounds,
		Show_Options,
		Quit,
		Gamma,
		Crouch_toggle,
		Make_Screenshot;

		private int num = -1;

		public int getNum() { return num; }
		
		public String getName() { return name(); }
		
		public KeyType setNum(int num) { this.num = num; return this; }
	}

	public static final char[] defclassickeys = { 
			Keys.UP, // GameKeys.Move_Forward,
			Keys.DOWN, // GameKeys.Move_Backward,
			Keys.LEFT, // GameKeys.Turn_Left,
			Keys.RIGHT, // GameKeys.Turn_Right,
			Keys.BACKSPACE, // GameKeys.Turn_Around,
			Keys.ALT_LEFT, // GameKeys.Strafe,
			Keys.COMMA, // GameKeys.Strafe_Left,
			Keys.PERIOD, // GameKeys.Strafe_Right,
			Keys.A, // GameKeys.Jump,
			Keys.Z, // GameKeys.Crouch,
			0, //LSPKeys.Crouch_toggle,
			Keys.SPACE, // GameKeys.Use
			Keys.CONTROL_LEFT,// GameKeys.Weapon_Fire,
			Keys.ESCAPE,	// GameKeys.Menu_Toggle,
			Keys.NUM_1, //LSPKeys.Weapon_1
			Keys.NUM_2, //LSPKeys.Weapon_2
			Keys.NUM_3, //LSPKeys.Weapon_3
			Keys.SEMICOLON, //GameKeys.Previous_Weapon,
			Keys.APOSTROPHE, //GameKeys.Next_Weapon,
			0, //LSPKeys.Last_Used_Weapon,
			Keys.PAGE_UP, //GameKeys.Look_Up,
			Keys.PAGE_DOWN, //GameKeys.Look_Down,
			Keys.U, //GameKeys.Mouse_Aiming,
			Keys.SHIFT_LEFT, //GameKeys.Run
			KEY_CAPSLOCK, 		//AutoRun
			Keys.TAB, //GameKeys.Map_Toggle,
			Keys.EQUALS, //GameKeys.Enlarge_Hud,
			Keys.MINUS, //GameKeys.Shrink_Hud,
			Keys.GRAVE,		//Show_Console
			Keys.F, //LSPKeys.Map_Follow_Mode,
			Keys.I, //LSPKeys.Toggle_Crosshair,
			Keys.F9, //LSPKeys.Quickload,
			Keys.F6, //LSPKeys.Quicksave,
			Keys.F2, //LSPKeys.Show_Savemenu,
			Keys.F3, //LSPKeys.Show_Loadmenu,
			Keys.F4, //LSPKeys.Show_Sounds,
			Keys.F5, //LSPKeys.Show_Options,
			Keys.F10, //LSPKeys.Quit,
			Keys.F11, //LSPKeys.Gamma,
			Keys.F12, //LSPKeys.Make_Screenshot,
	};

	public static char[] defkeys = { 
			Keys.W, // GameKeys.Move_Forward,
			Keys.S, // GameKeys.Move_Backward,
			Keys.LEFT, // GameKeys.Turn_Left,
			Keys.RIGHT, // GameKeys.Turn_Right,
			Keys.BACKSPACE, // GameKeys.Turn_Around,
			Keys.ALT_LEFT, // GameKeys.Strafe,
			Keys.A, // GameKeys.Strafe_Left,
			Keys.D, // GameKeys.Strafe_Right,
			Keys.SPACE, // GameKeys.Jump,
			Keys.CONTROL_LEFT, // GameKeys.Crouch,
			0, //LSPKeys.Crouch_toggle,
			Keys.E, // GameKeys.Use
			0,// GameKeys.Weapon_Fire,
			Keys.ESCAPE,	// GameKeys.Menu_Toggle,
			Keys.NUM_1, //LSPKeys.Weapon_1
			Keys.NUM_2, //LSPKeys.Weapon_2
			Keys.NUM_3, //LSPKeys.Weapon_3
			Keys.SEMICOLON, //GameKeys.Previous_Weapon,
			Keys.APOSTROPHE, //GameKeys.Next_Weapon,
			Keys.Q, //LSPKeys.Last_Used_Weapon,
			Keys.PAGE_UP, //GameKeys.Look_Up,
			Keys.PAGE_DOWN, //GameKeys.Look_Down,
			Keys.U, //GameKeys.Mouse_Aiming,
			Keys.SHIFT_LEFT, //GameKeys.Run
			KEY_CAPSLOCK, 		//AutoRun
			Keys.TAB, //GameKeys.Map_Toggle,
			Keys.EQUALS, //GameKeys.Enlarge_Hud,
			Keys.MINUS, //GameKeys.Shrink_Hud,
			Keys.GRAVE,		//Show_Console
			Keys.F, //LSPKeys.Map_Follow_Mode,
			Keys.I, //LSPKeys.Toggle_Crosshair,
			Keys.F9, //LSPKeys.Quickload,
			Keys.F6, //LSPKeys.Quicksave,
			Keys.F2, //LSPKeys.Show_Savemenu,
			Keys.F3, //LSPKeys.Show_Loadmenu,
			Keys.F4, //LSPKeys.Show_Sounds,
			Keys.F5, //LSPKeys.Show_Options,
			Keys.F10, //LSPKeys.Quit,
			Keys.F11, //LSPKeys.Gamma,
			Keys.F12, //LSPKeys.Make_Screenshot,
	};

	public Config(String path, String name) {
		super(path, name);
	}

	@Override
	public void SaveConfig(FileResource fil) {
		if(fil != null)
		{
			saveString(fil, "[Options]\r\n");	
			saveBoolean(fil, "AutoRun", gAutoRun);
			saveInteger(fil, "OverlayMap", gOverlayMap);
			saveBoolean(fil, "ShowMessages", gShowMessages);
			saveBoolean(fil, "Crosshair", gCrosshair);
			saveInteger(fil, "CrossSize", gCrossSize);
			saveInteger(fil, "ShowStat", gShowStat);
			saveInteger(fil, "HUDSize", gHUDSize);
			saveInteger(fil, "showMapInfo", showMapInfo);
			saveBoolean(fil, "HeadBob", bHeadBob);
			saveBoolean(fil, "ProjectileSpam", bOriginal);
			saveBoolean(fil, "ShowExits", bShowExit);
		}
	}

	@Override
	public boolean InitConfig(boolean isDefault) {
		gAutoRun = false;
		gOverlayMap = 2;
		gShowMessages = true;
		gCrosshair = true;
		gCrossSize = 65536;
		gShowStat = 0;
		gHUDSize = 65536;
		showMapInfo = 1;
		bHeadBob = true;
		bShowExit = false;
		
		if (!isDefault) {
			LoadCommon(defkeys, defclassickeys);
			if (set("Options")) {
				gAutoRun = GetKeyInt("AutoRun") == 1;
				int value = GetKeyInt("OverlayMap");
				if(value != -1) gOverlayMap = value;
				gShowMessages = GetKeyInt("ShowMessages") == 1;
				gCrosshair = GetKeyInt("Crosshair") == 1;
				value = GetKeyInt("CrossSize");
				if(value != -1) gCrossSize = BClipLow(value, 16384);
				value = GetKeyInt("ShowStat");
				if(value != -1) gShowStat = value;
				value = GetKeyInt("HUDSize");
				if(value != -1) gHUDSize = BClipLow(value, 16384);
				value = GetKeyInt("showMapInfo");
				if(value != -1) showMapInfo = value;
				bHeadBob = GetKeyInt("HeadBob") == 1;
				value = GetKeyInt("ProjectileSpam");
				if(value != -1) bOriginal = value == 1;
				value = GetKeyInt("ShowExits");
				if(value != -1) bShowExit = value == 1;
			}
			close();
		} else {
			Console.Println("Config file not found, using default settings", OSDTEXT_YELLOW);

			for (int i = 0; i < keymap.length; i++)
				primarykeys[i] = defkeys[i];

			mousekeys[GameKeys.Weapon_Fire.getNum()] = MOUSE_LBUTTON;
			mousekeys[GameKeys.Open.getNum()] = MOUSE_RBUTTON;
			mousekeys[GameKeys.Next_Weapon.getNum()] = MOUSE_WHELLUP;
			mousekeys[GameKeys.Previous_Weapon.getNum()] = MOUSE_WHELLDN;
		}

		boolean mouseset = true;
		for (int i = 0; i < mousekeys.length; i++) {
			if (mousekeys[i] != 0) {
				mouseset = false;
				break;
			}
		}
		if (mouseset) {
			mousekeys[GameKeys.Weapon_Fire.getNum()] = MOUSE_LBUTTON;
			mousekeys[GameKeys.Open.getNum()] = MOUSE_RBUTTON;
			mousekeys[GameKeys.Next_Weapon.getNum()] = MOUSE_WHELLUP;
			mousekeys[GameKeys.Previous_Weapon.getNum()] = MOUSE_WHELLDN;
		}

		musicType = BClipRange(musicType, 0, 1);
		return true;
	}

	@Override
	public KeyType[] getKeyMap() {
		KeyType[] keymap = { 
				GameKeys.Move_Forward, 
				GameKeys.Move_Backward, 
				GameKeys.Turn_Left, 
				GameKeys.Turn_Right,
				GameKeys.Turn_Around, 
				GameKeys.Strafe, 
				GameKeys.Strafe_Left, 
				GameKeys.Strafe_Right, 
				GameKeys.Jump,
				GameKeys.Crouch,
				LSPKeys.Crouch_toggle,
				GameKeys.Open, 
				GameKeys.Weapon_Fire, 
				GameKeys.Menu_Toggle, 
				LSPKeys.Weapon_1,
				LSPKeys.Weapon_2,
				LSPKeys.Weapon_3,
				GameKeys.Previous_Weapon,
				GameKeys.Next_Weapon,
				LSPKeys.Last_Used_Weapon,
				GameKeys.Look_Up,
				GameKeys.Look_Down,
				GameKeys.Mouse_Aiming,
				GameKeys.Run,
				LSPKeys.AutoRun,
				GameKeys.Map_Toggle,
				GameKeys.Enlarge_Hud,
				GameKeys.Shrink_Hud,
				GameKeys.Show_Console,
				
				LSPKeys.Map_Follow_Mode,
				LSPKeys.Toggle_Crosshair,
				LSPKeys.Quickload,
				LSPKeys.Quicksave,
				LSPKeys.Show_Savemenu,
				LSPKeys.Show_Loadmenu,
				LSPKeys.Show_Sounds,
				LSPKeys.Show_Options,
				LSPKeys.Quit,
				LSPKeys.Gamma,
				LSPKeys.Make_Screenshot,
			};
		return keymap;
	}

}
