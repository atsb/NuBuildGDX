package ru.m210projects.Tekwar;

import static ru.m210projects.Build.Gameutils.*;
import static ru.m210projects.Build.Input.Keymap.MOUSE_LBUTTON;
import static ru.m210projects.Build.Input.Keymap.MOUSE_MBUTTON;
import static ru.m210projects.Build.Input.Keymap.MOUSE_RBUTTON;
import static ru.m210projects.Build.Input.Keymap.MOUSE_WHELLDN;
import static ru.m210projects.Build.Input.Keymap.MOUSE_WHELLUP;
import static ru.m210projects.Build.OnSceenDisplay.Console.*;
import static ru.m210projects.Tekwar.View.*;

import ru.m210projects.Build.FileHandle.FileResource;
import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Build.Settings.BuildConfig;

import com.badlogic.gdx.Input.Keys;

public class Config extends BuildConfig {

	public enum TekKeys implements KeyType {
		
		Throw_Item,
		Aim_Center,
		Rearview,
		Prepared_Item,
		Health_Meter,
		Toggle_Crosshair,
		Elapsed_Time,
		Score,
		Inventory,
		Holster_Weapon,
		Zoom_In,
		Zoom_Out,
		
		Show_HelpScreen,
		Show_SaveMenu,
		Show_LoadMenu,
		Show_SoundSetup,
		Show_Options,
		Quicksave,
		Quickload,
		Quit,
		Gamma,
		Make_Screenshot,
		
//		AutoRun,
//		Aim_Up,
//		Aim_Down,
//		Tilt_Left,
//		Tilt_Right,
		Weapon_1,
		Weapon_2,
		Weapon_3,
		Weapon_4,
		Weapon_5,
		Weapon_6,
		Weapon_7,
		Weapon_8,
//		Map_Follow_Mode
		;

		private int num = -1;

		public int getNum() { return num; }
		
		public String getName() { return name(); }
		
		public KeyType setNum(int num) { this.num = num; return this; }
	}

	public static final char[] defclassickeys = {
		Keys.UP, 	//Move_Forward
		Keys.DOWN,	//Move_Backward
		Keys.LEFT,	//Turn_Left
		Keys.RIGHT,	//Turn_Right
		0, 			//Turn_Around
		Keys.SPACE, //Open
		Keys.SHIFT_LEFT, //Run
		Keys.ALT_LEFT, 	//Strafe
		Keys.CONTROL_LEFT,	//Weapon_Fire
		Keys.X, 		//Jump
		Keys.C, 		//Crouch
		Keys.PAGE_UP, 	//Look_Up
		Keys.PAGE_DOWN, //Look_Down
		Keys.COMMA,		//Strafe_Left
		Keys.SEMICOLON, //Strafe_Right
		Keys.TAB, 		//Map_Toggle
		Keys.ENTER, 	//Throw_Item
		Keys.EQUALS,	//Enlarge_Screen
		Keys.MINUS,		//Shrink_Screen
		Keys.M, 		//Send_Message
		Keys.HOME, 		//Aim_Center
		Keys.R,		//Rearview
		Keys.E, 	//Prepared_Item
		Keys.H, 	//Health_Meter
		Keys.G,		//Toggle_Crosshair
		Keys.T,		//Elapsed_Time
		Keys.S,		//Score
		Keys.I,		//Inventory
		Keys.NUM_1,
		Keys.NUM_2,
		Keys.NUM_3,
		Keys.NUM_4,
		Keys.NUM_5,
		Keys.NUM_6,
		Keys.NUM_7,
		Keys.NUM_8,
		Keys.APOSTROPHE,//Next_Weapon
		Keys.SEMICOLON,	//Previous_Weapon
		Keys.SLASH,	//Holster_Weapon
		Keys.U,		//Mouse_Aiming
		Keys.GRAVE,	//Console
		Keys.F1, //Show_HelpScreen
		Keys.F2, //Show_SaveMenu
		Keys.F3, //Show_LoadMenu
		Keys.F4, //Show_SoundSetup
		Keys.F5, //Show_Options
		Keys.F6, //Quicksave
		Keys.F9, //Quickload
		Keys.F10,//Quit
		Keys.F11,//Gamma
		Keys.F12,//Make_Screenshot
		Keys.ESCAPE,//Menu_open
	};
	
	public static char[] defkeys = {
		Keys.W, 	//Move_Forward
		Keys.S,	//Move_Backward
		Keys.LEFT,	//Turn_Left
		Keys.RIGHT,	//Turn_Right
		Keys.BACKSPACE, 			//Turn_Around
		Keys.E, //Open
		Keys.SHIFT_LEFT, //Run
		Keys.ALT_LEFT, 	//Strafe
		0,	//Weapon_Fire
		Keys.SPACE, 		//Jump
		Keys.CONTROL_LEFT, 		//Crouch
		Keys.PAGE_UP, 	//Look_Up
		Keys.PAGE_DOWN, //Look_Down
		Keys.A,		//Strafe_Left
		Keys.D, //Strafe_Right
		Keys.TAB, 		//Map_Toggle
		Keys.ENTER, 	//Throw_Item
		Keys.EQUALS,	//Enlarge_Screen
		Keys.MINUS,		//Shrink_Screen
		Keys.M, 		//Send_Message
		Keys.HOME, 		//Aim_Center
		Keys.R,		//Rearview
		0, 			//Prepared_Item
		Keys.H, 	//Health_Meter
		Keys.G,		//Toggle_Crosshair
		Keys.T,		//Elapsed_Time
		0,			//Score
		Keys.I,		//Inventory
		Keys.NUM_1,
		Keys.NUM_2,
		Keys.NUM_3,
		Keys.NUM_4,
		Keys.NUM_5,
		Keys.NUM_6,
		Keys.NUM_7,
		Keys.NUM_8,
		Keys.APOSTROPHE,//Next_Weapon
		Keys.SEMICOLON,	//Previous_Weapon
		Keys.SLASH,	//Holster_Weapon
		Keys.U,		//Mouse_Aiming
		Keys.GRAVE,	//Console
		Keys.F1, //Show_HelpScreen
		Keys.F2, //Show_SaveMenu
		Keys.F3, //Show_LoadMenu
		Keys.F4, //Show_SoundSetup
		Keys.F5, //Show_Options
		Keys.F6, //Quicksave
		Keys.F9, //Quickload
		Keys.F10,//Quit
		Keys.F11,//Gamma
		Keys.F12,//Make_Screenshot
		Keys.ESCAPE,//Menu_open	
	};

	public static final int TOGGLE_TIME      =   0;
	public static final int TOGGLE_SCORE     =   1;
	public static final int TOGGLE_REARVIEW  =   2;
	public static final int TOGGLE_UPRT      =   3;
	public static final int TOGGLE_HEALTH    =   4;
	public static final int TOGGLE_INVENTORY =   5;

	public boolean[] toggles;
	public boolean gCrosshair, gHeadBob, gAutoRun;
	public int gHUDSize, gStatSize, gCrossSize, gShowStat, showMapInfo;
	public boolean showMessages, showCutscenes, borderless;
	public int gOverlayMap = 2;
	public boolean gSaveWeapons = false;

	@Override
	public boolean InitConfig(boolean isDefault) {

		gCrosshair = true;
		gHeadBob = true;
		gAutoRun = true;
		showMessages = false;
		gHUDSize = 65536;
		gStatSize = 65536;
		gCrossSize = 65536;
		gShowStat = 1;
		showCutscenes = true;
		showMapInfo = 1;
		gSaveWeapons = false;
		
		toggles = new boolean[6];
		toggles[TOGGLE_TIME] = false;
		toggles[TOGGLE_SCORE] = false;
		toggles[TOGGLE_REARVIEW] = false;
		toggles[TOGGLE_UPRT] = true;
		toggles[TOGGLE_HEALTH] = true;
		toggles[TOGGLE_INVENTORY] = true;
		
		gOverlayMap = 2;
		
		if(!isDefault)
		{
			LoadCommon(defkeys, defclassickeys);
			if(set("Options")) {
				int value = GetKeyInt("HUDSize");
				if(value != -1) gHUDSize = value;
				gCrosshair = GetKeyInt("Crosshair") == 1;
				showMessages = GetKeyInt("Show_Messages") == 1;
				gAutoRun = GetKeyInt("AutoRun") == 1;
				gHeadBob = GetKeyInt("HeadBobbing") == 1;
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
				if(value != -1 ) gShowStat = value;
				showCutscenes = GetKeyInt("showCutscenes") == 1;
				value = GetKeyInt("showMapInfo");
				if(value != -1 ) showMapInfo = value;
				value = GetKeyInt("OverlayMap");
				if(value != -1) gOverlayMap = value;
				gSaveWeapons = GetKeyInt("Save_weapons") == 1;
				
				int rearv = GetKeyInt("Rearview");
				if(rearv != -1) {
					toggles[TOGGLE_REARVIEW] = (rearv == 1);
					rvmoving=rearv;
				}
				int uprt = GetKeyInt("Prepared_Item");
				if(uprt != -1) {
					toggles[TOGGLE_UPRT] = (uprt == 1);
					wpmoving = uprt;
				}
				int health = GetKeyInt("Health_Meter");
				if(health != -1) {
					toggles[TOGGLE_HEALTH] = (health == 1);
					hcmoving = health;
				}
				toggles[TOGGLE_TIME] = (GetKeyInt("Elapsed_Time") == 1);
				toggles[TOGGLE_SCORE] = (GetKeyInt("Score") == 1);
				toggles[TOGGLE_INVENTORY] = (GetKeyInt("Inventory") == 1);
			}
			close();
		} 
		else
		{
			Console.Println("Config file not found, using default settings", OSDTEXT_YELLOW);
			
			for(int i = 0; i < keymap.length; i++)
				primarykeys[i] = defkeys[i];
	
			mousekeys[GameKeys.Weapon_Fire.getNum()] = MOUSE_LBUTTON;
			mousekeys[TekKeys.Holster_Weapon.getNum()] = MOUSE_RBUTTON;
			mousekeys[GameKeys.Jump.getNum()] = MOUSE_MBUTTON;
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
			mousekeys[TekKeys.Holster_Weapon.getNum()] = MOUSE_RBUTTON;
			mousekeys[GameKeys.Jump.getNum()] = MOUSE_MBUTTON;
			mousekeys[GameKeys.Next_Weapon.getNum()] = MOUSE_WHELLUP;
			mousekeys[GameKeys.Previous_Weapon.getNum()] = MOUSE_WHELLDN;
		}
		
		musicType = BClipRange(musicType, 0, 1);
		return true;
	}
	
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
			GameKeys.Open,
			GameKeys.Run,
			GameKeys.Strafe,
			GameKeys.Weapon_Fire,
			GameKeys.Jump,
			GameKeys.Crouch,
			GameKeys.Look_Up,
			GameKeys.Look_Down,
			GameKeys.Strafe_Left,
			GameKeys.Strafe_Right,
			GameKeys.Map_Toggle,
			TekKeys.Throw_Item,
			TekKeys.Zoom_In,
			TekKeys.Zoom_Out,
			GameKeys.Send_Message,
			TekKeys.Aim_Center,
			TekKeys.Rearview,
			TekKeys.Prepared_Item,
			TekKeys.Health_Meter,
			TekKeys.Toggle_Crosshair,
			TekKeys.Elapsed_Time,
			TekKeys.Score,
			TekKeys.Inventory,
			TekKeys.Weapon_1,
			TekKeys.Weapon_2,
			TekKeys.Weapon_3,
			TekKeys.Weapon_4,
			TekKeys.Weapon_5,
			TekKeys.Weapon_6,
			TekKeys.Weapon_7,
			TekKeys.Weapon_8,
			GameKeys.Next_Weapon,
			GameKeys.Previous_Weapon,
			TekKeys.Holster_Weapon,
			GameKeys.Mouse_Aiming,
			GameKeys.Show_Console,
			TekKeys.Show_HelpScreen,
			TekKeys.Show_SaveMenu,
			TekKeys.Show_LoadMenu,
			TekKeys.Show_SoundSetup,
			TekKeys.Show_Options,
			TekKeys.Quicksave,
			TekKeys.Quickload,
			TekKeys.Quit,
			TekKeys.Gamma,
			TekKeys.Make_Screenshot,
			GameKeys.Menu_Toggle	
		};
	}

	@Override
	public void SaveConfig(FileResource fil)
	{
		if(fil != null)
		{
			saveString(fil, "[Options]\r\n");	
				//Options

			saveInteger(fil, "HUDSize", gHUDSize);
			saveBoolean(fil, "Crosshair", gCrosshair);
			saveBoolean(fil, "Show_Messages", showMessages);
			saveBoolean(fil, "AutoRun", gAutoRun);
			saveBoolean(fil, "HeadBobbing", gHeadBob);
			saveString(fil, "AdultPassword = " + "\"\"" +"\r\n");	
			saveInteger(fil, "StatSize", gStatSize);
			saveInteger(fil, "CrossSize", gCrossSize);
			saveInteger(fil, "ShowStat", gShowStat);
			saveBoolean(fil, "showCutscenes", showCutscenes);
			saveInteger(fil, "showMapInfo", showMapInfo);
			saveInteger(fil, "OverlayMap", gOverlayMap);
			saveBoolean(fil, "Save_weapons", gSaveWeapons);
			saveBoolean(fil, "Rearview", toggles[TOGGLE_REARVIEW]);
			saveBoolean(fil, "Prepared_Item", toggles[TOGGLE_UPRT]);
			saveBoolean(fil, "Health_Meter", toggles[TOGGLE_HEALTH]);
			saveBoolean(fil, "Elapsed_Time", toggles[TOGGLE_TIME]);
			saveBoolean(fil, "Score", toggles[TOGGLE_SCORE]);
			saveBoolean(fil, "Inventory", toggles[TOGGLE_INVENTORY]);
		}
	}

}
