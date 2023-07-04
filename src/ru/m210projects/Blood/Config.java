// This file is part of BloodGDX.
// Copyright (C) 2017-2019  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// BloodGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// BloodGDX is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with BloodGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.Blood;

import static ru.m210projects.Build.Net.Mmulti.*;
import static ru.m210projects.Build.Gameutils.*;
import static ru.m210projects.Build.Input.Keymap.KEY_CAPSLOCK;
import static ru.m210projects.Build.Input.Keymap.MOUSE_LBUTTON;
import static ru.m210projects.Build.Input.Keymap.MOUSE_MBUTTON;
import static ru.m210projects.Build.Input.Keymap.MOUSE_RBUTTON;
import static ru.m210projects.Build.Input.Keymap.MOUSE_WHELLDN;
import static ru.m210projects.Build.Input.Keymap.MOUSE_WHELLUP;

import ru.m210projects.Build.FileHandle.FileResource;
import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Build.Settings.BuildConfig;

import com.badlogic.gdx.Input.Keys;

import static ru.m210projects.Build.OnSceenDisplay.Console.*;

public class Config extends BuildConfig {

	public enum BloodKeys implements KeyType {
		
		Crouch_toggle,
		AutoRun,
		Weapon_Special_Fire,
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
		Last_Used_Weapon,
		Holster_Weapon,
		Show_Opponents_Weapon,
		BeastVision,
		CrystalBall,
		JumpBoots,
		MedKit,
		ProximityBombs,
		RemoteBombs,
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
		Keys.UP, 	//Move_Forward 0
		Keys.DOWN,	//Move_Backward 1
		Keys.LEFT,	//Turn_Left 2
		Keys.RIGHT,	//Turn_Right 3
		Keys.BACKSPACE, //Turn_Around 4
		Keys.ALT_LEFT, 	//Strafe 5
		Keys.COMMA,		//Strafe_Left 6
		Keys.PERIOD, //Strafe_Right 7
		Keys.A, 		//Jump 8
		Keys.Z, 			//Crouch 9
		0,				//Crouch_toggle 10
		Keys.SHIFT_LEFT, 	//Run 11
		KEY_CAPSLOCK, 		//AutoRun 12
		Keys.SPACE,		 	//Open 13
		Keys.CONTROL_LEFT, //Weapon_Fire 14
		Keys.X, 			//Weapon_Special_Fire 15
		Keys.HOME, 			//Aim_Up 16
		Keys.END,			//Aim_Down 17
		Keys.NUMPAD_5,		//Aim_Center 18
		Keys.PAGE_UP, 		//Look_Up 19
		Keys.PAGE_DOWN, 	//Look_Down 20
		Keys.INSERT,		//Tilt_Left 21
		Keys.FORWARD_DEL, //Tilt_Right 22
		Keys.NUM_1, 	//Weapon_1 23
		Keys.NUM_2,		//Weapon_2 24
		Keys.NUM_3,		//Weapon_3 25
		Keys.NUM_4,		//Weapon_4 26
		Keys.NUM_5,		//Weapon_5 27
		Keys.NUM_6,		//Weapon_6 28
		Keys.NUM_7,		//Weapon_7 29
		Keys.NUM_8,		//Weapon_8 30
		Keys.NUM_9,		//Weapon_9 31
		Keys.NUM_0,		//Weapon_10 32
		Keys.ENTER, 	//Inventory_Use 33
		Keys.LEFT_BRACKET, //Inventory_Left 34
		Keys.RIGHT_BRACKET, //Inventory_Right 35
		Keys.TAB,			//Map_Toggle 36
		Keys.F,			//Map_Follow_Mode 37
		Keys.MINUS, 	//Shrink_Screen 38
		Keys.EQUALS,	//Enlarge_Screen 39
		Keys.T, 		//Send_Message 40
		Keys.K,			//See_Coop_View 41
		Keys.F7,		//See_Chase_View 42
		Keys.U,			//Mouse_Aiming 43
		Keys.I,			//Toggle_Crosshair 44
		0,				//Last_Weapon_Switch 45
		Keys.APOSTROPHE,//Next_Weapon 46
		Keys.SEMICOLON,	//Previous_Weapon 47
		Keys.BACKSLASH, //Holster_Weapon 48
		Keys.W, 		//Show_Opponents_Weapon 49
		Keys.B,			//BeastVision 50
		Keys.C,			//CrystalBall 51
		Keys.J,			//JumpBoots 52
		Keys.M,			//MedKit 53
		Keys.P,			//ProximityBombs 54
		Keys.R,			//RemoteBombs 55
		Keys.ESCAPE,	//Open_menu 56
		Keys.GRAVE,		//Show_Console 57
		Keys.F1,		//Show_HelpScreen 58
		Keys.F2,		//Show_Save	59
		Keys.F3,		//Show_Load 60
		Keys.F4,		//Show_Sounds 61
		Keys.F5,		//Show_Options 62
		Keys.F6,		//QuickSave	63
		Keys.F8,		//ToggleMessages 64
		Keys.F9,		//QuickLoad 65
		Keys.F10,		//Quit 66
		Keys.F11,		//Gamma 67
		Keys.F12,		//MakeScreenshot 68
	};
	
	public static char[] defkeys = {
			Keys.W, 	//Move_Forward 0
			Keys.S,	//Move_Backward 1
			Keys.LEFT,	//Turn_Left 2
			Keys.RIGHT,	//Turn_Right 3
			Keys.BACKSPACE, //Turn_Around 4
			Keys.ALT_LEFT, 	//Strafe 5
			Keys.A,		//Strafe_Left 6
			Keys.D, //Strafe_Right 7
			Keys.SPACE, 		//Jump 8
			Keys.CONTROL_LEFT, 	//Crouch 9
			0,					//Crouch_toggle 10
			Keys.SHIFT_LEFT, 	//Run 11
			KEY_CAPSLOCK, 		//AutoRun 12
			Keys.E,		 	//Open 13
			0, 				//Weapon_Fire 14
			Keys.X, 			//Weapon_Special_Fire 15
			Keys.HOME, 			//Aim_Up 16
			Keys.END,			//Aim_Down 17
			Keys.NUMPAD_5,		//Aim_Center 18
			Keys.PAGE_UP, 		//Look_Up 19
			Keys.PAGE_DOWN, 	//Look_Down 20
			Keys.INSERT,		//Tilt_Left 21
			Keys.FORWARD_DEL, //Tilt_Right 22
			Keys.NUM_1, 	//Weapon_1 23
			Keys.NUM_2,		//Weapon_2 24
			Keys.NUM_3,		//Weapon_3 25
			Keys.NUM_4,		//Weapon_4 26
			Keys.NUM_5,		//Weapon_5 27
			Keys.NUM_6,		//Weapon_6 28
			Keys.NUM_7,		//Weapon_7 29
			Keys.NUM_8,		//Weapon_8 30
			Keys.NUM_9,		//Weapon_9 31
			Keys.NUM_0,		//Weapon_10 32
			Keys.ENTER, 	//Inventory_Use 33
			Keys.LEFT_BRACKET, //Inventory_Left 34
			Keys.RIGHT_BRACKET, //Inventory_Right 35
			Keys.TAB,			//Map_Toggle 36
			Keys.F,			//Map_Follow_Mode 37
			Keys.MINUS, 	//Shrink_Screen 38
			Keys.EQUALS,	//Enlarge_Screen 39
			Keys.T, 		//Send_Message 40
			Keys.K,			//See_Coop_View 41
			Keys.F7,		//See_Chase_View 42
			Keys.U,			//Mouse_Aiming 43
			Keys.I,			//Toggle_Crosshair 44
			Keys.Q,				//Last_Weapon_Switch 45
			Keys.APOSTROPHE,//Next_Weapon 46
			Keys.SEMICOLON,	//Previous_Weapon 47
			Keys.BACKSLASH, //Holster_Weapon 48
			0, 				//Show_Opponents_Weapon 49
			Keys.B,			//BeastVision 50
			Keys.C,			//CrystalBall 51
			Keys.J,			//JumpBoots 52
			Keys.M,			//MedKit 53
			Keys.P,			//ProximityBombs 54
			Keys.R,			//RemoteBombs 55
			Keys.ESCAPE,	//Open_menu 56
			Keys.GRAVE,		//Show_Console 57
			Keys.F1,		//Show_HelpScreen 58
			Keys.F2,		//Show_Save	59
			Keys.F3,		//Show_Load 60
			Keys.F4,		//Show_Sounds 61
			Keys.F5,		//Show_Options 62
			Keys.F6,		//QuickSave	63
			Keys.F8,		//ToggleMessages 64
			Keys.F9,		//QuickLoad 65
			Keys.F10,		//Quit 66
			Keys.F11,		//Gamma 67
			Keys.F12,		//MakeScreenshot 68
		};

	public String[] macros = {
		"I love the smell of napalm",
		"Is that gasoline I smell?",
		"Ta da!",
		"Who wants some, huh? Who's next?",
		"I have something for you.",
		"You just gonna stand there...",
		"That'll teach ya!",
		"Ohh, that wasn't a bit nice.",
		"Amateurs!",
		"Fool! You are already dead."
	};
	
	public boolean gParentalLock;
	public String AdultPassword;

	public int gTurnSpeed;
	public boolean gAutoAim, gCrosshair, gBobWidth, gBobHeight, gSlopeTilt, gAutoRun, 
		gShowWeapon, MessageState, gVanilla, useautosecretcount, showCutscenes;

	public int gDetail, gInterpolation, gOverlayMap, MessageFont, showQuotes, quoteTime;
	public int gViewSize, gStatSize, gCrossSize, gShowStat, gDemoSeq, showMapInfo, gHudSize;

	public Config(String path, String cfgname)
	{
		super(path, cfgname);
	}
	
	@Override
	public boolean InitConfig(boolean isDefault) {
		
		gViewSize = 3;
		gTurnSpeed = 92;
		gAutoAim	= true;
		gCrosshair = true;
		gBobWidth = true;
		gBobHeight = true;
		gSlopeTilt = true;
		gAutoRun = true;
		gShowWeapon = true;
	
		gDetail = 4;
		gInterpolation = 1;
		gParentalLock = false;
		AdultPassword = "";
		MessageFont = 3;
		showQuotes = 4;
		quoteTime = 5; //sec
		MessageState = true;
		gOverlayMap = 0;
		gVanilla = false;
		
		useautosecretcount = false;
		gDemoSeq = 1;
		pName = "Caleb";
		mAddress = "localhost";
		mPort = NETPORT;

		gStatSize = 65536;
		gCrossSize = 65536;
		gHudSize = 65536;
		gShowStat = 1;
		showCutscenes = true;
		showMapInfo = 1;

		if(!isDefault)
		{
			LoadCommon(defkeys, defclassickeys);
			if(set("Options")) {
				int value = GetKeyInt("KeyboardTurnSpeed");
				if(value != -1) gTurnSpeed = value;
				value = GetKeyInt("ViewSize");
				if(value != -1) gViewSize = value;
				gCrosshair = GetKeyInt("Crosshair") == 1;
				MessageState = GetKeyInt("MessageState") == 1;
				value = GetKeyInt("Detail");
				if(value != -1) gDetail = value;
				gAutoRun = GetKeyInt("AutoRun") == 1;
				value = GetKeyInt("Interpolation");
				if(value != -1) gInterpolation = value;
				gBobWidth = GetKeyInt("ViewHBobbing") == 1;
				gBobHeight = GetKeyInt("ViewVBobbing") == 1;
				value = GetKeyInt("OverlayMap");
				if(value != -1) gOverlayMap = value;
				gVanilla = GetKeyInt("VanillaMode") == 1;
				gShowWeapon = GetKeyInt("ShowOpponentsWeapon") == 1;
				gAutoAim = GetKeyInt("AutoAim") == 1;
				gSlopeTilt = GetKeyInt("SlopeTilting") == 1;
				MessageState = GetKeyInt("MessageState") == 1;
				value = GetKeyInt("MessageCount");
				if(value != -1) showQuotes = value;
				value = GetKeyInt("MessageTime");
				if(value != -1) quoteTime = value;
				value = GetKeyInt("MessageFont");
				if(value != -1) MessageFont = BClipRange(value, 0, 4);
				gParentalLock = GetKeyInt("AdultContent") == 1;
				AdultPassword = GetKeyString("AdultPassword");
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
				value = GetKeyInt("HudSize");
				if(value != -1) {
					gHudSize = value;
					if(gHudSize < 16384) gHudSize = 16384;
				}
				value = GetKeyInt("ShowStat");
				if(value != -1) gShowStat = value;
				showCutscenes = GetKeyInt("showCutscenes") == 1;
				value = GetKeyInt("showMapInfo");
				if(value != -1) showMapInfo = value;
				useautosecretcount = GetKeyInt("UseAutoSecretCount") == 1;
				value = GetKeyInt("DemoSequence");
				if(value != -1) gDemoSeq = value;
			}
			close();
		} 
		else
		{
			Console.Println("Config file not found, using default settings", OSDTEXT_YELLOW);
			
			for(int i = 0; i < keymap.length; i++)
				primarykeys[i] = defkeys[i];
			
			mousekeys[GameKeys.Weapon_Fire.getNum()] = MOUSE_LBUTTON;
			mousekeys[BloodKeys.Weapon_Special_Fire.getNum()] = MOUSE_RBUTTON;
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
			mousekeys[BloodKeys.Weapon_Special_Fire.getNum()] = MOUSE_RBUTTON;
			mousekeys[GameKeys.Open.getNum()] = MOUSE_MBUTTON;
			mousekeys[GameKeys.Next_Weapon.getNum()] = MOUSE_WHELLUP;
			mousekeys[GameKeys.Previous_Weapon.getNum()] = MOUSE_WHELLDN;
		}

		return true;
	}

	@Override
	public void SaveConfig(FileResource fil) {
		if(fil != null)
		{
			saveString(fil, "[Options]\r\n");	
				//Options
			saveInteger(fil, "KeyboardTurnSpeed", gTurnSpeed);
			saveInteger(fil, "ViewSize", gViewSize);
			saveBoolean(fil, "Crosshair", gCrosshair);
			saveBoolean(fil, "MessageState", MessageState);
			saveInteger(fil, "Detail", gDetail);
			saveBoolean(fil, "ShowOpponentsWeapon", gShowWeapon);
			saveBoolean(fil, "AutoRun", gAutoRun);
			saveInteger(fil, "Interpolation", gInterpolation);
			saveBoolean(fil, "ViewHBobbing", gBobWidth);
			saveBoolean(fil, "ViewVBobbing", gBobHeight);
			saveInteger(fil, "OverlayMap", gOverlayMap);
			saveBoolean(fil, "VanillaMode", gVanilla);
			saveBoolean(fil, "SlopeTilting", gSlopeTilt);
			saveBoolean(fil, "AutoAim", gAutoAim);
			saveBoolean(fil, "MessageState", MessageState);
			saveInteger(fil, "MessageCount", showQuotes);
			saveInteger(fil, "MessageTime", quoteTime);
			saveInteger(fil, "MessageFont", MessageFont);
			saveBoolean(fil, "AdultContent", gParentalLock);
			saveString(fil,  "AdultPassword = " + "\"\"" +"\r\n");	
			saveInteger(fil, "StatSize", gStatSize);
			saveInteger(fil, "CrossSize", gCrossSize);
			saveInteger(fil, "HudSize", gHudSize);
			saveInteger(fil, "ShowStat", gShowStat);
			saveBoolean(fil, "showCutscenes", showCutscenes);
			saveInteger(fil, "showMapInfo", showMapInfo);
			saveBoolean(fil, "UseAutoSecretCount", useautosecretcount);
			saveInteger(fil, "DemoSequence", gDemoSeq);
		}
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
			BloodKeys.Crouch_toggle,
			GameKeys.Run,
			BloodKeys.AutoRun,
			GameKeys.Open,
			GameKeys.Weapon_Fire,
			BloodKeys.Weapon_Special_Fire,
			BloodKeys.Aim_Up,
			BloodKeys.Aim_Down,
			BloodKeys.Aim_Center,
			GameKeys.Look_Up,
			GameKeys.Look_Down,
			BloodKeys.Tilt_Left,
			BloodKeys.Tilt_Right,
			BloodKeys.Weapon_1,
			BloodKeys.Weapon_2,
			BloodKeys.Weapon_3,
			BloodKeys.Weapon_4,
			BloodKeys.Weapon_5,
			BloodKeys.Weapon_6,
			BloodKeys.Weapon_7,
			BloodKeys.Weapon_8,
			BloodKeys.Weapon_9,
			BloodKeys.Weapon_10,
			BloodKeys.Inventory_Use,
			BloodKeys.Inventory_Left,
			BloodKeys.Inventory_Right,
			GameKeys.Map_Toggle,
			BloodKeys.Map_Follow_Mode,
			GameKeys.Shrink_Screen,
			GameKeys.Enlarge_Screen,
			GameKeys.Send_Message,
			BloodKeys.See_Coop_View,
			BloodKeys.See_Chase_View,
			GameKeys.Mouse_Aiming,
			BloodKeys.Toggle_Crosshair,
			BloodKeys.Last_Used_Weapon,
			GameKeys.Next_Weapon,
			GameKeys.Previous_Weapon,
			BloodKeys.Holster_Weapon,
			BloodKeys.Show_Opponents_Weapon,
			BloodKeys.BeastVision,
			BloodKeys.CrystalBall,
			BloodKeys.JumpBoots,
			BloodKeys.MedKit,
			BloodKeys.ProximityBombs,
			BloodKeys.RemoteBombs,
			GameKeys.Menu_Toggle,
			GameKeys.Show_Console,
			BloodKeys.Show_HelpScreen,
			BloodKeys.Show_SaveMenu,
			BloodKeys.Show_LoadMenu,
			BloodKeys.Show_SoundSetup,
			BloodKeys.Show_Options,
			BloodKeys.Quicksave,
			BloodKeys.Toggle_messages,
			BloodKeys.Quickload,
			BloodKeys.Quit,
			BloodKeys.Gamma,
			BloodKeys.Make_Screenshot,
		};
		return keymap;
	}
}
