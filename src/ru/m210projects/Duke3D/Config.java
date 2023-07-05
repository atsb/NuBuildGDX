 // This file is part of DukeGDX.
// Copyright (C) 2019  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// DukeGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// DukeGDX is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with DukeGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.Duke3D;

import static ru.m210projects.Build.Input.Keymap.*;

import ru.m210projects.Build.FileHandle.FileResource;
import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Build.Settings.BuildConfig;

import com.badlogic.gdx.Input.Keys;

import static ru.m210projects.Build.OnSceenDisplay.Console.*;
import static ru.m210projects.Duke3D.Globals.ud;

public class Config extends BuildConfig {

	public Config(String path, String name) {
		super(path, name);
	}

	public enum DukeKeys implements KeyType {
		AutoRun,
		Quick_Kick,
		Aim_Up,
		Aim_Down,
		Aim_Center,
		Tilt_Left,
		Tilt_Right,
		Weapon_1,
		Weapon_2,
		Weapon_3,
		Weapon_4,
		Weapon_5,
		Weapon_6,
		Weapon_7,
		Weapon_8,
		Weapon_9,
		Weapon_10,
		Inventory_Use,
		Inventory_Left,
		Inventory_Right,
		Map_Follow_Mode,
		See_Coop_View,
		See_Chase_View,
		Toggle_Crosshair,
		Holster_Weapon,
		Show_Opp_Weapon,
		NightVision,
		Holo_Duke,
		Jetpack,
		MedKit,
		Steroids,
		Show_Help,
		Show_Savemenu,
		Show_Loadmenu,
		Show_Sounds,
		Show_Options,
		Quicksave,
		Messages,
		Quickload,
		Quit,
		Gamma,
		Screenshot,
		Last_Weap_Switch,
		Crouch_toggle;

		private int num = -1;

		public int getNum() { return num; }
		
		public String getName() { return name(); }
		
		public KeyType setNum(int num) { this.num = num; return this; }
	}


	public static final char[] defkeys = {
		Keys.W, 			//Move_Forward 0
		Keys.S,				//Move_Backward 1
		Keys.LEFT,			//Turn_Left 2
		Keys.RIGHT,			//Turn_Right 3
		Keys.BACKSPACE, 	//Turn_Around 4
		Keys.ALT_LEFT, 		//Strafe 5
		Keys.A,				//Strafe_Left 6
		Keys.D, 			//Strafe_Right 7
		Keys.SPACE, 		//Jump 8
		Keys.CONTROL_LEFT, 	//Crouch 9
		Keys.SHIFT_LEFT, 	//Run 10
		KEY_CAPSLOCK, 		//AutoRun 11
		Keys.E,		 		//Open 12
		Keys.CONTROL_RIGHT, //Weapon_Fire 13
		Keys.Q, 			//Quick_Kick 14
		Keys.HOME, 			//Aim_Up 15
		Keys.END,			//Aim_Down 16
		Keys.NUMPAD_5,		//Aim_Center 17
		Keys.PAGE_UP, 		//Look_Up 18
		Keys.PAGE_DOWN, 	//Look_Down 19
		Keys.INSERT,		//Tilt_Left 20
		Keys.FORWARD_DEL, //Tilt_Right 21
		Keys.NUM_1, 	//Weapon_1 22
		Keys.NUM_2,		//Weapon_2 23
		Keys.NUM_3,		//Weapon_3 24
		Keys.NUM_4,		//Weapon_4 25
		Keys.NUM_5,		//Weapon_5 26
		Keys.NUM_6,		//Weapon_6 27
		Keys.NUM_7,		//Weapon_7 28
		Keys.NUM_8,		//Weapon_8 29
		Keys.NUM_9,		//Weapon_9 30
		Keys.NUM_0,		//Weapon_10 31
		Keys.ENTER, 	//Inventory_Use 32
		Keys.LEFT_BRACKET, //Inventory_Left 33
		Keys.RIGHT_BRACKET, //Inventory_Right 34
		Keys.TAB,			//Map_Toggle 35
		Keys.F,			//Map_Follow_Mode 36
		Keys.MINUS, 	//Shrink_Hud 37
		Keys.EQUALS,	//Enlarge_Hud 38
		Keys.T, 		//Send_Message 39
		Keys.K,			//See_Coop_View 40
		Keys.F7,		//See_Chase_View 41
		Keys.U,			//Mouse_Aiming 42
		Keys.I,			//Toggle_Crosshair 43
		Keys.APOSTROPHE,//Next_Weapon 44
		Keys.SEMICOLON,	//Previous_Weapon 45
		Keys.BACKSLASH, //Holster_Weapon 46
		Keys.Y, 		//Show_Opponents_Weapon 47
		Keys.N,			//NightVision 48
		Keys.H,			//Holo_Duke 49
		Keys.J,			//Jetpack 50
		Keys.M,			//MedKit 51
		Keys.R,			//Steroids 52
		Keys.ESCAPE,	//Open_menu 53
		Keys.GRAVE,		//Show_Console 54
		Keys.F1,		//Show_HelpScreen 55
		Keys.F2,		//Show_Save	56
		Keys.F3,		//Show_Load 57
		Keys.F4,		//Show_Sounds 58
		Keys.F5,		//Show_Options 59
		Keys.F6,		//QuickSave	60
		Keys.F8,		//ToggleMessages 61
		Keys.F9,		//QuickLoad 62
		Keys.F10,		//Quit 63
		Keys.F11,		//Gamma 64
		Keys.F12,		//MakeScreenshot 65
		0,				//Last_Weapon_Switch
		0,				//Crouch_toggle
	};
	
	
	public static final char[] defclassickeys = {
			Keys.UP, 			//Move_Forward 0
			Keys.DOWN,			//Move_Backward 1
			Keys.LEFT,			//Turn_Left 2
			Keys.RIGHT,			//Turn_Right 3
			Keys.BACKSPACE, 	//Turn_Around 4
			Keys.ALT_LEFT, 		//Strafe 5
			Keys.COMMA,			//Strafe_Left 6
			Keys.PERIOD, 		//Strafe_Right 7
			Keys.A, 			//Jump 8
			Keys.Z, 			//Crouch 9
			Keys.SHIFT_LEFT, 	//Run 10
			KEY_CAPSLOCK, 		//AutoRun 11
			Keys.SPACE,		 		//Open 12
			Keys.CONTROL_LEFT, //Weapon_Fire 13
			Keys.Q, 			//Quick_Kick 14
			Keys.HOME, 			//Aim_Up 15
			Keys.END,			//Aim_Down 16
			Keys.NUMPAD_5,		//Aim_Center 17
			Keys.PAGE_UP, 		//Look_Up 18
			Keys.PAGE_DOWN, 	//Look_Down 19
			Keys.INSERT,		//Tilt_Left 20
			Keys.FORWARD_DEL, //Tilt_Right 21
			Keys.NUM_1, 	//Weapon_1 22
			Keys.NUM_2,		//Weapon_2 23
			Keys.NUM_3,		//Weapon_3 24
			Keys.NUM_4,		//Weapon_4 25
			Keys.NUM_5,		//Weapon_5 26
			Keys.NUM_6,		//Weapon_6 27
			Keys.NUM_7,		//Weapon_7 28
			Keys.NUM_8,		//Weapon_8 29
			Keys.NUM_9,		//Weapon_9 30
			Keys.NUM_0,		//Weapon_10 31
			Keys.ENTER, 	//Inventory_Use 32
			Keys.LEFT_BRACKET, //Inventory_Left 33
			Keys.RIGHT_BRACKET, //Inventory_Right 34
			Keys.TAB,			//Map_Toggle 35
			Keys.F,			//Map_Follow_Mode 36
			Keys.MINUS, 	//Shrink_Hud 37
			Keys.EQUALS,	//Enlarge_Hud 38
			Keys.T, 		//Send_Message 39
			Keys.K,			//See_Coop_View 40
			Keys.F7,		//See_Chase_View 41
			Keys.U,			//Mouse_Aiming 42
			Keys.I,			//Toggle_Crosshair 43
			Keys.APOSTROPHE,//Next_Weapon 44
			Keys.SEMICOLON,	//Previous_Weapon 45
			KEY_SCROLLOCK,  //Holster_Weapon 46
			Keys.W, 		//Show_Opponents_Weapon 47
			Keys.N,			//NightVision 48
			Keys.H,			//Holo_Duke 49
			Keys.J,			//Jetpack 50
			Keys.M,			//MedKit 51
			Keys.R,			//Steroids 52
			Keys.ESCAPE,	//Open_menu 53
			Keys.GRAVE,		//Show_Console 54
			Keys.F1,		//Show_HelpScreen 55
			Keys.F2,		//Show_Save	56
			Keys.F3,		//Show_Load 57
			Keys.F4,		//Show_Sounds 58
			Keys.F5,		//Show_Options 59
			Keys.F6,		//QuickSave	60
			Keys.F8,		//ToggleMessages 61
			Keys.F9,		//QuickLoad 62
			Keys.F10,		//Quit 63
			Keys.F11,		//Gamma 64
			Keys.F12,		//MakeScreenshot 65
			0,				//Last_Weapon_Switch
			0,				//Crouch_toggle	
	};
	
	public int gStatSize = 65536;
	public int gCrossSize = 65536;
	public int gShowStat = 1;
	public int showMapInfo = 1;
	public boolean AmbienceToggle = true;
	public boolean VoiceToggle = true;
	public int gDemoSeq = 0;
	public boolean gAutoAim	= true;
	public int screen_size = 2;
	public int crosshair = 1;
	public int screen_tilting = 1;
	public int auto_run = 1;
	public int fta_on = 1;
	public boolean bDevCommentry;
	public boolean bLegacyDukeTalk;
	
	@Override
	public void SaveConfig(FileResource fil) {
		if(fil != null)
		{
			saveString(fil, "[Options]\r\n");	
				//Options
			saveInteger(fil, "Size", ud.screen_size);
			saveInteger(fil, "Crosshair", ud.crosshair);
			saveInteger(fil, "MessageState", ud.fta_on);
			saveBoolean(fil, "Autoaim", gAutoAim);
			saveInteger(fil, "Tilt", ud.screen_tilting);
			saveInteger(fil, "AutoRun", ud.auto_run);
			saveInteger(fil, "MessageState", ud.fta_on);	
			saveInteger(fil, "StatSize", gStatSize);	
			saveInteger(fil, "CrossSize", gCrossSize);
			saveInteger(fil, "ShowStat", gShowStat);
			saveInteger(fil, "showMapInfo", showMapInfo);
			saveInteger(fil, "DemoSequence", gDemoSeq);
			
			saveString(fil, "//Twentieth Anniversary World Tour options\r\n");	
			saveBoolean(fil, "DeveloperCommentary", bDevCommentry);
			saveBoolean(fil, "LegacyDukeTalk", bLegacyDukeTalk);
		}
	}

	@Override
	public boolean InitConfig(boolean isDefault) {
		gStatSize = 65536;
		gCrossSize = 65536;
		gShowStat = 1;
		showMapInfo = 1;
		AmbienceToggle = true;
		VoiceToggle = true;
		gDemoSeq = 1;
		gAutoAim	= true;
		screen_size = 2;
		crosshair = 1;
		screen_tilting = 1;
		auto_run = 1;
		fta_on = 1;
		pName = "Duke";
		bDevCommentry = true;
		bLegacyDukeTalk = false;

		if(!isDefault)
		{
			LoadCommon(defkeys, defclassickeys);
			if(set("Options")) {
				int value = GetKeyInt("Size");
				if(value != -1) screen_size = value;
				value = GetKeyInt("Crosshair");
				if(value != -1) crosshair = value;
				value = GetKeyInt("MessageState");
				if(value != -1) fta_on = value;
				gAutoAim = GetKeyInt("Autoaim") == 1;
				value = GetKeyInt("Tilt");
				if(value != -1) screen_tilting = value;
				value = GetKeyInt("AutoRun");
				if(value != -1) auto_run = value;
				value = GetKeyInt("StatSize");
				if(value != -1) {
					gStatSize = value;
					if(gStatSize < 16384) gStatSize = 16384;
				}
				value = GetKeyInt("CrossSize");
				if(value != -1) {
					gCrossSize = value;
					if(gCrossSize < 16384) gCrossSize = 16384;
				}
				value = GetKeyInt("ShowStat");
				if(value != -1) gShowStat = value;
				value = GetKeyInt("showMapInfo");
				if(value != -1) showMapInfo = value;
				value = GetKeyInt("DemoSequence");
				if(value != -1) gDemoSeq = value;
				value = GetKeyInt("DeveloperCommentary");
				if(value != -1) bDevCommentry = value == 1;
				value = GetKeyInt("LegacyDukeTalk");
				if(value != -1) bLegacyDukeTalk = value == 1;
			}
			close();
		} 
		else
		{
			Console.Println("Config file not found, using default settings", OSDTEXT_YELLOW);
			
			for(int i = 0; i < keymap.length; i++)
				primarykeys[i] = defkeys[i];
	
			mousekeys[GameKeys.Weapon_Fire.getNum()] = MOUSE_LBUTTON;
			mousekeys[DukeKeys.Last_Weap_Switch.getNum()] = MOUSE_RBUTTON;
			mousekeys[GameKeys.Open.getNum()] = MOUSE_MBUTTON;
			mousekeys[GameKeys.Next_Weapon.getNum()] = MOUSE_WHELLUP;
			mousekeys[GameKeys.Previous_Weapon.getNum()] = MOUSE_WHELLDN;
		}
		
		boolean mouseset = true;
		for (int mousekey : mousekeys) {
			if (mousekey != 0) {
				mouseset = false;
				break;
			}
		}
		if(mouseset)
		{
			mousekeys[GameKeys.Weapon_Fire.getNum()] = MOUSE_LBUTTON;
			mousekeys[DukeKeys.Last_Weap_Switch.getNum()] = MOUSE_RBUTTON;
			mousekeys[GameKeys.Open.getNum()] = MOUSE_MBUTTON;
			mousekeys[GameKeys.Next_Weapon.getNum()] = MOUSE_WHELLUP;
			mousekeys[GameKeys.Previous_Weapon.getNum()] = MOUSE_WHELLDN;
		}
		
		return true;
	}

	@Override
	public KeyType[] getKeyMap() {
		return new KeyType[]{
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
			GameKeys.Run,
			DukeKeys.AutoRun,
			GameKeys.Open,
			GameKeys.Weapon_Fire,
			DukeKeys.Quick_Kick,
			DukeKeys.Aim_Up,
			DukeKeys.Aim_Down,
			DukeKeys.Aim_Center,
			GameKeys.Look_Up,
			GameKeys.Look_Down,
			DukeKeys.Tilt_Left,
			DukeKeys.Tilt_Right,
			DukeKeys.Weapon_1,
			DukeKeys.Weapon_2,
			DukeKeys.Weapon_3,
			DukeKeys.Weapon_4,
			DukeKeys.Weapon_5,
			DukeKeys.Weapon_6,
			DukeKeys.Weapon_7,
			DukeKeys.Weapon_8,
			DukeKeys.Weapon_9,
			DukeKeys.Weapon_10,
			DukeKeys.Inventory_Use,
			DukeKeys.Inventory_Left,
			DukeKeys.Inventory_Right,
			GameKeys.Map_Toggle,
			DukeKeys.Map_Follow_Mode,
			GameKeys.Shrink_Hud,
			GameKeys.Enlarge_Hud,
			GameKeys.Send_Message,
			DukeKeys.See_Coop_View,
			DukeKeys.See_Chase_View,
			GameKeys.Mouse_Aiming,
			DukeKeys.Toggle_Crosshair,
			GameKeys.Next_Weapon,
			GameKeys.Previous_Weapon,
			DukeKeys.Holster_Weapon,
			DukeKeys.Show_Opp_Weapon,
			DukeKeys.NightVision,
			DukeKeys.Holo_Duke,
			DukeKeys.Jetpack,
			DukeKeys.MedKit,
			DukeKeys.Steroids,
			GameKeys.Menu_Toggle,
			GameKeys.Show_Console,
			DukeKeys.Show_Help,
			DukeKeys.Show_Savemenu,
			DukeKeys.Show_Loadmenu,
			DukeKeys.Show_Sounds,
			DukeKeys.Show_Options,
			DukeKeys.Quicksave,
			DukeKeys.Messages,
			DukeKeys.Quickload,
			DukeKeys.Quit,
			DukeKeys.Gamma,
			DukeKeys.Screenshot,
			DukeKeys.Last_Weap_Switch,
			DukeKeys.Crouch_toggle
		};
	}

}
