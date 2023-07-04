package ru.m210projects.Witchaven;

import static ru.m210projects.Build.Gameutils.BClipRange;
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
	
	public enum WhKeys implements KeyType {
		
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
		Weapon_8,
		Weapon_9,
		Weapon_10,
		
		Cast_spell,
		Scare_Spell,
		Nightvision_Spell,
		Freeze_Spell,
		Arrow_Spell,
		Opendoor_Spell,
		Fly_Spell,
		Fire_Spell,
		Nuke_Spell,
		Spell_next,
		Spell_prev,
		Inventory_Use,
		Inventory_Left,
		Inventory_Right,
		Fly_up,
		Fly_down,
		End_flying,
		
		Health_potion,
		Strength_potion,
		Curepoison_potion,
		Fireresist_potion,
		Invisibility_potion,
		
		Map_Follow_Mode,
		Toggle_Crosshair,
		See_Coop_View,
		Quickload,
		Quicksave,
		Show_Savemenu,
		Show_Loadmenu,
		Show_Sounds,
		Show_Options,
		Quit,
		Gamma,
		Make_Screenshot;

		private int num = -1;

		public int getNum() { return num; }
		
		public String getName() { return name(); }
		
		public KeyType setNum(int num) { this.num = num; return this; }
	}

	public static char[] defkeys = {
		Keys.W, 	//Move_Forward
		Keys.S,	//Move_Backward 
		Keys.LEFT,	//Turn_Left 
		Keys.RIGHT,	//Turn_Right 
		Keys.BACKSPACE, //Turn_Around
		Keys.ALT_LEFT, 	//Strafe 
		Keys.A,		//Strafe_Left 
		Keys.D, 	//Strafe_Right 
		Keys.SPACE, 		//Jump 
		Keys.CONTROL_LEFT, 	//Crouch 
		Keys.SHIFT_LEFT, 	//Run 
		KEY_CAPSLOCK, 		//AutoRun 
		Keys.E,		 	//Open 
		0, 				//Weapon_Fire 
		Keys.Q, 			//Cast_spell 
		Keys.PAGE_UP, 		//Aim_Up 
		Keys.PAGE_DOWN,		//Aim_Down 
		Keys.HOME,		//Aim_Center 
		Keys.NUM_1, 	//Weapon_1 
		Keys.NUM_2,		//Weapon_2 
		Keys.NUM_3,		//Weapon_3 
		Keys.NUM_4,		//Weapon_4 
		Keys.NUM_5,		//Weapon_5
		Keys.NUM_6,		//Weapon_6
		Keys.NUM_7,		//Weapon_7
		Keys.NUM_8,		//Weapon_8
		Keys.NUM_9,		//Weapon_9
		Keys.NUM_0,		//Weapon_10
		0,				//Next_Weapon 
		0,				//Previous_Weapon 
		Keys.NUMPAD_1,		//Scare_Spell
		Keys.NUMPAD_2,		//Nightvision_Spell
		Keys.NUMPAD_3,		//Freeze_Spell
		Keys.NUMPAD_4,		//Arrow_Spell
		Keys.NUMPAD_5,		//Opendoor_Spell
		Keys.NUMPAD_6,		//Fly_Spell
		Keys.NUMPAD_7,		//Fire_Spell
		Keys.NUMPAD_8,		//Nuke_Spell
		Keys.APOSTROPHE,	//Spell_next
		Keys.SEMICOLON,	 	//Spell_prev
		0,					//Health_potion
		0,					//Strength_potion
		0,					//Curepoison_potion
		0,					//Fireresist_potion
		0,					//Invisibility_potion
		Keys.ENTER, 		//Inventory_Use
		Keys.LEFT_BRACKET, //Inventory_Left
		Keys.RIGHT_BRACKET, //Inventory_Right
		Keys.TAB,			//Map_Toggle
		Keys.F,			//Map_Follow_Mode
		Keys.MINUS, 	//Shrink_Screen
		Keys.EQUALS,	//Enlarge_Screen
		Keys.U,			//Mouse_Aiming
		Keys.I,			//Toggle_Crosshair
		Keys.INSERT,	 //Fly_up
		Keys.FORWARD_DEL, //Fly_down
		Keys.END,		//End_flying
		Keys.T,				//Send_message
		Keys.K,				//See_coop
		Keys.GRAVE,		 //Show_Console
		Keys.F9,		//Quickload
		Keys.F6,		//Quicksave
		Keys.F2,	//Show_SaveMenu
		Keys.F3,	//Show_LoadMenu
		Keys.F4,	//Show_SoundSetup
		Keys.F5,	//Show_Options
		Keys.F10,	//Quit
		Keys.F11,	//Gamma
		Keys.F12,	//Make_Screenshot
		Keys.ESCAPE, 	//Open/Close_menu
	};
	
	public static char[] defclassickeys = {
		Keys.UP, 	//Move_Forward
		Keys.DOWN,	//Move_Backward
		Keys.LEFT,	//Turn_Left
		Keys.RIGHT,	//Turn_Right
		0, 			//Turn_Around
		Keys.ALT_LEFT, 	//Strafe
		Keys.COMMA,		//Strafe_Lef
		Keys.PERIOD, 	//Strafe_Right
		Keys.X, 		//Jump
		Keys.C, 		//Crouch
		Keys.SHIFT_LEFT, 	//Run
		0, 				//AutoRun
		Keys.SPACE,		 	//Open
		Keys.CONTROL_LEFT, //Weapon_Fire
		Keys.GRAVE, 		//Cast_spell 
		Keys.PAGE_UP, 	//Aim_Up
		Keys.PAGE_DOWN,	//Aim_Down 
		Keys.HOME,		//Aim_Center 
		Keys.NUM_1, 	//Weapon_1 
		Keys.NUM_2,		//Weapon_2 
		Keys.NUM_3,		//Weapon_3 
		Keys.NUM_4,		//Weapon_4 
		Keys.NUM_5,		//Weapon_5 
		Keys.NUM_6,		//Weapon_6 
		Keys.NUM_7,		//Weapon_7 
		Keys.NUM_8,		//Weapon_8 
		Keys.NUM_9,		//Weapon_9 
		Keys.NUM_0,		//Weapon_10
		0,				//Next_Weapon
		0,				//Previous_Weapon
		Keys.F1,		//Scare_Spell 
		Keys.F2,		//Nightvision_Spell 
		Keys.F3,		//Freeze_Spell 
		Keys.F4,		//Arrow_Spell 
		Keys.F5,		//Opendoor_Spell 
		Keys.F6,		//Fly_Spell 
		Keys.F7,		//Fire_Spell 
		Keys.F8,		//Nuke_Spell 
		0,				//Spell_next
		0,				//Spell_prev
		0,					//Health_potion
		0,					//Strength_potion
		0,					//Curepoison_potion
		0,					//Fireresist_potion
		0,					//Invisibility_potion
		Keys.ENTER, 	//Inventory_Use 
		Keys.LEFT_BRACKET, //Inventory_Left 
		Keys.RIGHT_BRACKET, //Inventory_Right 
		Keys.TAB,			//Map_Toggle 
		Keys.F,			//Map_Follow_Mode 
		Keys.MINUS, 	//Shrink_Screen 
		Keys.EQUALS,	//Enlarge_Screen 
		0,				//Mouse_Aiming 
		0,				//Toggle_Crosshair 
		Keys.INSERT,	 //Fly_up 
		Keys.FORWARD_DEL, //Fly_down 
		Keys.END,		//End_flying 
		0,				//Send_message
		0,				//See_coop
		0,			 //Show_Console 
		0,			//Quickload 
		0,			//Quicksave 
		0,		//Show_SaveMenu
		0,		//Show_LoadMenu
		0,		//Show_SoundSetup
		0,		//Show_Options
		0,		//Quit
		0,		//Gamma
		0,		//Make_Screenshot
		Keys.ESCAPE, //Open/Close_menu
	};

	public int gViewSize;
	public boolean MessageState;
	public boolean gCrosshair;
	public boolean gAutoRun;
	public int gStatSize;
	public int gCrossSize;
	public int gShowStat;
	public int gHudScale;
	public boolean showCutscenes;
	public boolean gGameGore;
	public boolean showMapInfo;
	
	public Config(String path, String cfgname)
	{
		super(path, cfgname);
	}
	
	public KeyType[] getKeyMap()
	{
		return new KeyType[] {
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
			WhKeys.AutoRun,
			GameKeys.Open,
			GameKeys.Weapon_Fire,
			WhKeys.Cast_spell,
			WhKeys.Aim_Up,
			WhKeys.Aim_Down,
			WhKeys.Aim_Center,
			WhKeys.Weapon_1,
			WhKeys.Weapon_2,
			WhKeys.Weapon_3,
			WhKeys.Weapon_4,
			WhKeys.Weapon_5,
			WhKeys.Weapon_6,
			WhKeys.Weapon_7,
			WhKeys.Weapon_8,
			WhKeys.Weapon_9,
			WhKeys.Weapon_10,
			GameKeys.Next_Weapon,
			GameKeys.Previous_Weapon,
			WhKeys.Scare_Spell,
			WhKeys.Nightvision_Spell,
			WhKeys.Freeze_Spell,
			WhKeys.Arrow_Spell,
			WhKeys.Opendoor_Spell,
			WhKeys.Fly_Spell,
			WhKeys.Fire_Spell,
			WhKeys.Nuke_Spell,
			WhKeys.Spell_next,
			WhKeys.Spell_prev,
			WhKeys.Health_potion,
			WhKeys.Strength_potion,
			WhKeys.Curepoison_potion,
			WhKeys.Fireresist_potion,
			WhKeys.Invisibility_potion,
			WhKeys.Inventory_Use,
			WhKeys.Inventory_Left,
			WhKeys.Inventory_Right,
			GameKeys.Map_Toggle,
			WhKeys.Map_Follow_Mode,
			GameKeys.Shrink_Screen,
			GameKeys.Enlarge_Screen,
			GameKeys.Mouse_Aiming,
			WhKeys.Toggle_Crosshair,
			WhKeys.Fly_up,
			WhKeys.Fly_down,
			WhKeys.End_flying,
			GameKeys.Send_Message,
			WhKeys.See_Coop_View,
			GameKeys.Show_Console,
			WhKeys.Quickload,
			WhKeys.Quicksave,
			WhKeys.Show_Savemenu,
			WhKeys.Show_Loadmenu,
			WhKeys.Show_Sounds,
			WhKeys.Show_Options,
			WhKeys.Quit,
			WhKeys.Gamma,
			WhKeys.Make_Screenshot,
			GameKeys.Menu_Toggle	
		};
	}
	
	@Override
	public boolean InitConfig(boolean isDefault) {
		gViewSize = 1;
		MessageState = true;
		gCrosshair = true;
		gAutoRun = false;
		gStatSize = 32768;
		gCrossSize = 65536;
		gHudScale = 65536;
		gShowStat = 1;
		showCutscenes = true;
		gGameGore = true;
		showMapInfo = true;
		
		if(!isDefault)
		{
			LoadCommon(defkeys, defclassickeys);
			if(set("Options")) {
				int value = GetKeyInt("ViewSize");
				if(value != -1 ) gViewSize = value;
				gCrosshair = GetKeyInt("Crosshair") == 1;
				MessageState = GetKeyInt("MessageState") == 1;
				gAutoRun = GetKeyInt("AutoRun") == 1;
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
				value = GetKeyInt("HUDScale");
				if(value != -1) {
					gHudScale = value;
					if(gHudScale < 32768) gHudScale = 32768;
				}
				value = GetKeyInt("ShowStat");
				if(value != -1 ) gShowStat = value;
				showCutscenes = GetKeyInt("showCutscenes") == 1;
				gGameGore = GetKeyInt("Gore") == 1;
				showMapInfo = GetKeyInt("ShowMapInfo") == 1;
			}
			close();
		} 
		else
		{
			Console.Println("Config file not found, using default settings", OSDTEXT_YELLOW);
			
			for(int i = 0; i < keymap.length; i++)
				primarykeys[i] = defkeys[i];
	
			mousekeys[GameKeys.Weapon_Fire.getNum()] = MOUSE_LBUTTON;
			mousekeys[WhKeys.Cast_spell.getNum()] = MOUSE_RBUTTON;
			mousekeys[GameKeys.Open.getNum()] = MOUSE_MBUTTON;
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
			mousekeys[WhKeys.Cast_spell.getNum()] = MOUSE_RBUTTON;
			mousekeys[GameKeys.Open.getNum()] = MOUSE_MBUTTON;
			mousekeys[GameKeys.Next_Weapon.getNum()] = MOUSE_WHELLUP;
			mousekeys[GameKeys.Previous_Weapon.getNum()] = MOUSE_WHELLDN;
		}
		
		musicType = BClipRange(musicType, 0, 1);
		
		return true;
	}
	
	@Override
	public void SaveConfig(FileResource fil)
	{
		if(fil != null)
		{
			saveString(fil, "[Options]\r\n");	
				//Options
			saveInteger(fil, "ViewSize", gViewSize);
			saveBoolean(fil, "Crosshair", gCrosshair);
			saveBoolean(fil, "MessageState", MessageState);
			
			saveBoolean(fil, "AutoRun", gAutoRun);
			saveInteger(fil, "StatSize", gStatSize);
			saveInteger(fil, "CrossSize", gCrossSize);
			saveInteger(fil, "HUDScale", gHudScale);
			saveInteger(fil, "ShowStat", gShowStat);
			saveBoolean(fil, "showCutscenes", showCutscenes);
			saveBoolean(fil, "Gore", gGameGore);
			saveBoolean(fil, "ShowMapInfo", showMapInfo);
		}
	}
}
