// This file is part of PowerslaveGDX.
// Copyright (C) 2019  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// PowerslaveGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// PowerslaveGDX is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with PowerslaveGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.Powerslave;

import static ru.m210projects.Build.Gameutils.*;
import static ru.m210projects.Build.Input.Keymap.KEY_CAPSLOCK;
import static ru.m210projects.Build.Input.Keymap.MOUSE_LBUTTON;
import static ru.m210projects.Build.Input.Keymap.MOUSE_RBUTTON;
import static ru.m210projects.Build.Input.Keymap.MOUSE_WHELLDN;
import static ru.m210projects.Build.Input.Keymap.MOUSE_WHELLUP;
import static ru.m210projects.Build.OnSceenDisplay.Console.OSDTEXT_YELLOW;

import com.badlogic.gdx.Input.Keys;

import ru.m210projects.Build.FileHandle.FileResource;
import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Build.Settings.BuildConfig;

public class Config extends BuildConfig {
	
	public enum PsKeys implements KeyType {
		Crouch_toggle,
		AutoRun,
		Aim_Up,
		Aim_Down,
		Aim_Center,
		Weapon_1,
		Weapon_2,
		Weapon_3,
		Weapon_4,
		Weapon_5,
		Weapon_6,
		Weapon_7,
		Inventory_Use,
		Inventory_Left,
		Inventory_Right,
		Inventory_Hand,
		Inventory_Eye,
		Inventory_Mask,
		Inventory_Heart,
		Inventory_Torch,
		Inventory_Scarab,
		Third_View,
		Map_Follow_Mode,
		Toggle_Crosshair,
		Last_Used_Weapon,
		Show_HelpScreen,
		Show_SaveMenu,
		Show_LoadMenu,
		Show_SoundSetup,
		Show_Options,
		Quicksave,
		Toggle_messages,
		Quickload,
		Quit,
		Gamma,
		Make_Screenshot;

		private int num = -1;

		public int getNum() { return num; }
		
		public String getName() { return name(); }
		
		public KeyType setNum(int num) { this.num = num; return this; }
	}
	
	public static char[] defclassickeys = {
			Keys.UP,//			GameKeys.Move_Forward,
			Keys.DOWN,//			GameKeys.Move_Backward,
			Keys.LEFT,//			GameKeys.Turn_Left,
			Keys.RIGHT,//			GameKeys.Turn_Right,
			Keys.BACKSPACE,//			GameKeys.Turn_Around,
			Keys.ALT_LEFT,//			GameKeys.Strafe,
			Keys.COMMA,//			GameKeys.Strafe_Left,
			Keys.PERIOD, //			GameKeys.Strafe_Right,
			Keys.A, //			GameKeys.Jump,
			Keys.Z, //			GameKeys.Crouch,
				0, //			PsKeys.Crouch_toggle,
			Keys.SHIFT_LEFT, //			GameKeys.Run,
			KEY_CAPSLOCK, //			PsKeys.AutoRun,
			Keys.SPACE,//			GameKeys.Open,
			Keys.CONTROL_LEFT,//			GameKeys.Weapon_Fire,
			Keys.HOME,//			PsKeys.Aim_Up,
			Keys.END,//			PsKeys.Aim_Down,
			Keys.NUMPAD_5,//			PsKeys.Aim_Center,
			Keys.PAGE_UP,//			GameKeys.Look_Up,
			Keys.PAGE_DOWN,//			GameKeys.Look_Down,
			Keys.NUM_1,//			PsKeys.Weapon_1,
			Keys.NUM_2,//			PsKeys.Weapon_2,
			Keys.NUM_3,//			PsKeys.Weapon_3,
			Keys.NUM_4,//			PsKeys.Weapon_4,
			Keys.NUM_5,//			PsKeys.Weapon_5,
			Keys.NUM_6,//			PsKeys.Weapon_6,
			Keys.NUM_7,//			PsKeys.Weapon_7,
			Keys.ENTER,//			PsKeys.Inventory_Use,
			Keys.LEFT_BRACKET, //			PsKeys.Inventory_Left,
			Keys.RIGHT_BRACKET,//			PsKeys.Inventory_Right,
			0, //Inventory_Hand,
			0, //Inventory_Eye,
			0, //Inventory_Mask,
			0, //Inventory_Heart,
			0, //Inventory_Torch,
			0, //Inventory_Scarab,
			Keys.F7,			//PsKeys.Third_View,
			Keys.I,//			PsKeys.Toggle_Crosshair,
			0, 		//			PsKeys.Last_Used_Weapon,
			Keys.APOSTROPHE,	//			GameKeys.Next_Weapon,
			Keys.SEMICOLON,	//			GameKeys.Previous_Weapon,
			Keys.TAB,	//			GameKeys.Map_Toggle,
			Keys.F,	//			PsKeys.Map_Follow_Mode,
			Keys.MINUS,	//			GameKeys.Shrink_Screen,
			Keys.EQUALS,	//			GameKeys.Enlarge_Screen,
			Keys.U,	//			GameKeys.Mouse_Aiming,
			Keys.F8,	//			PsKeys.Toggle_messages,
			Keys.GRAVE,	//			GameKeys.Show_Console,
			Keys.F2,	//			PsKeys.Show_SaveMenu,
			Keys.F3,	//			PsKeys.Show_LoadMenu,
			Keys.F4,	//			PsKeys.Show_SoundSetup,
			Keys.F5,	//			PsKeys.Show_Options,
			Keys.F6,	//			PsKeys.Quicksave,
			Keys.F9,	//			PsKeys.Quickload,
			Keys.F10,	//			PsKeys.Quit,
			Keys.F11,	//			PsKeys.Gamma,
			Keys.F12,	//			PsKeys.Make_Screenshot,
			Keys.ESCAPE,	//			GameKeys.Menu_Toggle,
	};
	
	public static char[] defkeys = {
			Keys.W,//			GameKeys.Move_Forward,
			Keys.S,//			GameKeys.Move_Backward,
			Keys.LEFT,//			GameKeys.Turn_Left,
			Keys.RIGHT,//			GameKeys.Turn_Right,
			Keys.BACKSPACE,//			GameKeys.Turn_Around,
			Keys.ALT_LEFT,//			GameKeys.Strafe,
			Keys.A,//			GameKeys.Strafe_Left,
			Keys.D, //			GameKeys.Strafe_Right,
			Keys.SPACE, //			GameKeys.Jump,
			Keys.CONTROL_LEFT, //			GameKeys.Crouch,
			0, //			PsKeys.Crouch_toggle,
			Keys.SHIFT_LEFT, //			GameKeys.Run,
			KEY_CAPSLOCK, //			PsKeys.AutoRun,
			Keys.E,//			GameKeys.Open,
			0,//			GameKeys.Weapon_Fire,
			Keys.HOME,//			PsKeys.Aim_Up,
			Keys.END,//			PsKeys.Aim_Down,
			Keys.NUMPAD_5,//			PsKeys.Aim_Center,
			Keys.PAGE_UP,//			GameKeys.Look_Up,
			Keys.PAGE_DOWN,//			GameKeys.Look_Down,
			Keys.NUM_1,//			PsKeys.Weapon_1,
			Keys.NUM_2,//			PsKeys.Weapon_2,
			Keys.NUM_3,//			PsKeys.Weapon_3,
			Keys.NUM_4,//			PsKeys.Weapon_4,
			Keys.NUM_5,//			PsKeys.Weapon_5,
			Keys.NUM_6,//			PsKeys.Weapon_6,
			Keys.NUM_7,//			PsKeys.Weapon_7,
			Keys.ENTER,//			PsKeys.Inventory_Use,
			Keys.LEFT_BRACKET, //			PsKeys.Inventory_Left,
			Keys.RIGHT_BRACKET,//			PsKeys.Inventory_Right,
			0, //Inventory_Hand,
			0, //Inventory_Eye,
			0, //Inventory_Mask,
			0, //Inventory_Heart,
			0, //Inventory_Torch,
			0, //Inventory_Scarab,
			Keys.F7,			//PsKeys.Third_View,
			Keys.I,//			PsKeys.Toggle_Crosshair,
			Keys.Q, 		//			PsKeys.Last_Used_Weapon,
			Keys.APOSTROPHE,	//			GameKeys.Next_Weapon,
			Keys.SEMICOLON,	//			GameKeys.Previous_Weapon,
			Keys.TAB,	//			GameKeys.Map_Toggle,
			Keys.F,	//			PsKeys.Map_Follow_Mode,
			Keys.MINUS,	//			GameKeys.Shrink_Screen,
			Keys.EQUALS,	//			GameKeys.Enlarge_Screen,
			Keys.U,	//			GameKeys.Mouse_Aiming,
			Keys.F8,	//			PsKeys.Toggle_messages,
			Keys.GRAVE,	//			GameKeys.Show_Console,
			Keys.F2,	//			PsKeys.Show_SaveMenu,
			Keys.F3,	//			PsKeys.Show_LoadMenu,
			Keys.F4,	//			PsKeys.Show_SoundSetup,
			Keys.F5,	//			PsKeys.Show_Options,
			Keys.F6,	//			PsKeys.Quicksave,
			Keys.F9,	//			PsKeys.Quickload,
			Keys.F10,	//			PsKeys.Quit,
			Keys.F11,	//			PsKeys.Gamma,
			Keys.F12,	//			PsKeys.Make_Screenshot,
			Keys.ESCAPE,	//			GameKeys.Menu_Toggle,
	};
	
	public boolean gShowMessages;
	public boolean gCrosshair;
	public boolean gAutoRun;
	public int gCrossSize;
	public int nScreenSize;
	public int gOverlayMap;
	public int showMapInfo;
	public int gStatSize, gShowStat;
	public boolean gAutoAim;
	public boolean bNewShadows;
	public boolean bSubtitles;
	public boolean bGrenadeFix;
	public boolean bWaspSound;

	public int gDemoSeq;

	public Config(String path, String name) {
		super(path, name);
	}

	@Override
	public void SaveConfig(FileResource fil) {
		if(fil != null)
		{
			saveString(fil, "[Options]\r\n");	
			
			saveBoolean(fil, "ShowMessages", gShowMessages);
			saveBoolean(fil, "Crosshair", gCrosshair);
			saveBoolean(fil, "AutoRun", gAutoRun);
			saveInteger(fil, "CrossSize", gCrossSize);
			saveInteger(fil, "ScreenSize", nScreenSize);
			saveInteger(fil, "OverlayMap", gOverlayMap);
			saveInteger(fil, "showMapInfo", showMapInfo);
			saveInteger(fil, "StatSize", gStatSize);
			saveInteger(fil, "ShowStat", gShowStat);
			saveBoolean(fil, "AutoAim", gAutoAim);
			saveInteger(fil, "DemoSequence", gDemoSeq);
			saveBoolean(fil, "NewShadows", bNewShadows);
			saveBoolean(fil, "Subtitles", bSubtitles);
			saveBoolean(fil, "GrenadeFix", bGrenadeFix);
			saveBoolean(fil, "WaspSound", bWaspSound);
		}
	}

	@Override
	public boolean InitConfig(boolean isDefault) {
		gShowMessages = true;
		gCrosshair = true;
		gAutoRun = false;
		gCrossSize = 65536;
		nScreenSize = 2;
		gOverlayMap = 1;
		gAutoAim = true;
		showMapInfo = 1;
		gStatSize = 65536;
		gShowStat = 1;
		bNewShadows = true;
		gDemoSeq = 1;
		bSubtitles = true;
		bGrenadeFix = false;
		bWaspSound = true;

		if(!isDefault)
		{
			LoadCommon(defkeys, defclassickeys);
			if(set("Options")) {
				gShowMessages = GetKeyInt("ShowMessages") != 0;
				gCrosshair = GetKeyInt("Crosshair") != 0;
				gAutoRun = GetKeyInt("AutoRun") == 1;
				int value = GetKeyInt("CrossSize");
				if(value != -1) 
					gCrossSize = BClipLow(value, 16384);
				value = GetKeyInt("ScreenSize");
				if(value != -1) 
					nScreenSize = BClipRange(value, 0, 2);
				value = GetKeyInt("OverlayMap");
				if(value != -1) 
					gOverlayMap = BClipRange(value, 0, 2);
				value = GetKeyInt("showMapInfo");
				if(value != -1) 
					showMapInfo = value;
				value = GetKeyInt("StatSize");
				if(value != -1) 
					gStatSize = BClipLow(value, 16384);
				value = GetKeyInt("ShowStat");
				if(value != -1) gShowStat = value;
				gAutoAim = GetKeyInt("AutoAim") == 1;
				value = GetKeyInt("DemoSequence");
				if(value != -1) gDemoSeq = value;
				value = GetKeyInt("NewShadows");
				if(value != -1) bNewShadows = value == 1;
				value = GetKeyInt("Subtitles");
				if(value != -1) bSubtitles = value == 1;
				value = GetKeyInt("GrenadeFix");
				if(value != -1) bGrenadeFix = value == 1;
				value = GetKeyInt("WaspSound");
				if(value != -1) bWaspSound = value == 1;
			}
			close();
		} 
		else
		{
			Console.Println("Config file not found, using default settings", OSDTEXT_YELLOW);
			
			for(int i = 0; i < keymap.length; i++)
				primarykeys[i] = defkeys[i];
			
			mousekeys[GameKeys.Weapon_Fire.getNum()] = MOUSE_LBUTTON;
			mousekeys[GameKeys.Open.getNum()] = MOUSE_RBUTTON;
			mousekeys[GameKeys.Next_Weapon.getNum()] = MOUSE_WHELLUP;
			mousekeys[GameKeys.Previous_Weapon.getNum()] = MOUSE_WHELLDN;
		}
		
		boolean mouseset = true;
		for(int i = 0; i < mousekeys.length; i++) {
			if(mousekeys[i] != 0) {
				mouseset = false;
				break;
			}
		}
		if(mouseset)
		{
			mousekeys[GameKeys.Weapon_Fire.getNum()] = MOUSE_LBUTTON;
			mousekeys[GameKeys.Open.getNum()] = MOUSE_RBUTTON;
			mousekeys[GameKeys.Next_Weapon.getNum()] = MOUSE_WHELLUP;
			mousekeys[GameKeys.Previous_Weapon.getNum()] = MOUSE_WHELLDN;
		}

		middrv = musicType = 0;
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
			PsKeys.Crouch_toggle,
			GameKeys.Run,
			PsKeys.AutoRun,
			GameKeys.Open,
			GameKeys.Weapon_Fire,
			PsKeys.Aim_Up,
			PsKeys.Aim_Down,
			PsKeys.Aim_Center,
			GameKeys.Look_Up,
			GameKeys.Look_Down,
			PsKeys.Weapon_1,
			PsKeys.Weapon_2,
			PsKeys.Weapon_3,
			PsKeys.Weapon_4,
			PsKeys.Weapon_5,
			PsKeys.Weapon_6,
			PsKeys.Weapon_7,
			PsKeys.Inventory_Use,
			PsKeys.Inventory_Left,
			PsKeys.Inventory_Right,
			PsKeys.Inventory_Hand,
			PsKeys.Inventory_Eye,
			PsKeys.Inventory_Mask,
			PsKeys.Inventory_Heart,
			PsKeys.Inventory_Torch,
			PsKeys.Inventory_Scarab,
			PsKeys.Third_View,
			PsKeys.Toggle_Crosshair,
			PsKeys.Last_Used_Weapon,
			GameKeys.Next_Weapon,
			GameKeys.Previous_Weapon,
			GameKeys.Map_Toggle,
			PsKeys.Map_Follow_Mode,
			GameKeys.Shrink_Screen,
			GameKeys.Enlarge_Screen,
			GameKeys.Mouse_Aiming,
			PsKeys.Toggle_messages,
			GameKeys.Show_Console,
			PsKeys.Show_SaveMenu,
			PsKeys.Show_LoadMenu,
			PsKeys.Show_SoundSetup,
			PsKeys.Show_Options,
			PsKeys.Quicksave,
			PsKeys.Quickload,
			PsKeys.Quit,
			PsKeys.Gamma,
			PsKeys.Make_Screenshot,
			GameKeys.Menu_Toggle,
		};
		return keymap;
	}

}
