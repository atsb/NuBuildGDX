package ru.m210projects.Wang;

import static ru.m210projects.Build.Gameutils.BClipLow;
import static ru.m210projects.Build.Gameutils.BClipRange;
import static ru.m210projects.Build.Input.Keymap.KEY_CAPSLOCK;
import static ru.m210projects.Build.Input.Keymap.KEY_SCROLLOCK;
import static ru.m210projects.Build.Input.Keymap.MOUSE_LBUTTON;
import static ru.m210projects.Build.Input.Keymap.*;
import static ru.m210projects.Build.Input.Keymap.MOUSE_WHELLDN;
import static ru.m210projects.Build.Input.Keymap.MOUSE_WHELLUP;
import static ru.m210projects.Build.OnSceenDisplay.Console.OSDTEXT_YELLOW;

import com.badlogic.gdx.Input.Keys;

import ru.m210projects.Build.FileHandle.FileResource;
import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Build.Settings.BuildConfig;
import ru.m210projects.Build.Settings.BuildSettings;

public class Config extends BuildConfig {

	public byte NetColor = 0;
	public boolean AutoAim = false;
	public boolean Ambient = true;
	public int BorderNum = 1;
	public boolean Messages = true;
	public boolean Crosshair = true;
	public int CrosshairSize = 65536;
	public boolean Shadows = true;
	public boolean ParentalLock;
	public boolean AutoRun = true;
	public int brightness;
	public int Stats = 1;
	public int gStatSize = 65536;
	public boolean SlopeTilting = false;
	public int showMapInfo = 1;
	public int gDemoSeq = 0;
	public boolean WeaponAutoSwitch = true;
	public boolean UseDarts = false;
	public boolean DisableHornets = false;
	public boolean ShowWeapon = false;
	
	public String[] WangBangMacro = { "Burn baby burn...", "You make another stupid move.",
			"Blocking with your head again?", "You not fight well with hands!", "You so stupid!",
			"Quit jerking off. Come fight me!", "What the matter you scaredy cat?", "Did I break your concentration?",
			"Hope you were paying attention.", "ITTAIIIUUU!!!", };

	public enum SwKeys implements KeyType {
		AutoRun, Aim_Up, Aim_Down, Aim_Center, Tilt_Left, Tilt_Right, Weapon_1, Weapon_2, Weapon_3, Weapon_4, Weapon_5,
		Weapon_6, Weapon_7, Weapon_8, Weapon_9, Weapon_10, Inventory_Use, Inventory_Left, Inventory_Right,
		Map_Follow_Mode, See_Coop_View, See_Chase_View, Toggle_Crosshair, Holster_Weapon, Show_Opp_Weapon, NightVision,
		SmokeBomb, GasBomb, MedKit, FlashBomb, Caltrops, Show_Help, Show_Savemenu, Show_Loadmenu, Show_Sounds,
		Show_Options, Quicksave, Messages, Quickload, Quit, Gamma, Screenshot, Last_Weap_Switch, Crouch_toggle, Special_Fire;

		private int num = -1;

		public int getNum() {
			return num;
		}

		public String getName() {
			return name();
		}

		public KeyType setNum(int num) {
			this.num = num;
			return this;
		}
	}

	public static final char[] defkeys = { Keys.W, // Move_Forward 0
			Keys.S, // Move_Backward 1
			Keys.LEFT, // Turn_Left 2
			Keys.RIGHT, // Turn_Right 3
			Keys.BACKSPACE, // Turn_Around 4
			Keys.ALT_LEFT, // Strafe 5
			Keys.A, // Strafe_Left 6
			Keys.D, // Strafe_Right 7
			Keys.SPACE, // Jump 8
			Keys.CONTROL_LEFT, // Crouch 9
			Keys.SHIFT_LEFT, // Run 10
			KEY_CAPSLOCK, // AutoRun 11
			Keys.E, // Open 12
			Keys.CONTROL_RIGHT, // Weapon_Fire 13
			0, //Special_Fire
			Keys.HOME, // Aim_Up 15
			Keys.END, // Aim_Down 16
			Keys.NUMPAD_5, // Aim_Center 17
			Keys.PAGE_UP, // Look_Up 18
			Keys.PAGE_DOWN, // Look_Down 19
			Keys.INSERT, // Tilt_Left 20
			Keys.FORWARD_DEL, // Tilt_Right 21
			Keys.NUM_1, // Weapon_1 22
			Keys.NUM_2, // Weapon_2 23
			Keys.NUM_3, // Weapon_3 24
			Keys.NUM_4, // Weapon_4 25
			Keys.NUM_5, // Weapon_5 26
			Keys.NUM_6, // Weapon_6 27
			Keys.NUM_7, // Weapon_7 28
			Keys.NUM_8, // Weapon_8 29
			Keys.NUM_9, // Weapon_9 30
			Keys.NUM_0, // Weapon_10 31
			Keys.ENTER, // Inventory_Use 32
			Keys.LEFT_BRACKET, // Inventory_Left 33
			Keys.RIGHT_BRACKET, // Inventory_Right 34
			Keys.TAB, // Map_Toggle 35
			Keys.F, // Map_Follow_Mode 36
			Keys.MINUS, // Shrink_Screen 37
			Keys.EQUALS, // Enlarge_Screen 38
			Keys.T, // Send_Message 39
			Keys.K, // See_Coop_View 40
			Keys.F7, // See_Chase_View 41
			Keys.U, // Mouse_Aiming 42
			Keys.I, // Toggle_Crosshair 43
			Keys.APOSTROPHE, // Next_Weapon 44
			Keys.SEMICOLON, // Previous_Weapon 45
			Keys.BACKSLASH, // Holster_Weapon 46
			Keys.Y, // Show_Opponents_Weapon 47
			Keys.N, // NightVision 48
			Keys.H, // SmokeBomb 49
			Keys.G, // GasBomb 50
			Keys.M, // MedKit 51
			Keys.B, // FlashBomb 52
			Keys.C, // Caltrops
			Keys.ESCAPE, // Open_menu 53
			Keys.GRAVE, // Show_Console 54
			Keys.F1, // Show_HelpScreen 55
			Keys.F2, // Show_Save 56
			Keys.F3, // Show_Load 57
			Keys.F4, // Show_Sounds 58
			Keys.F5, // Show_Options 59
			Keys.F6, // QuickSave 60
			Keys.F8, // ToggleMessages 61
			Keys.F9, // QuickLoad 62
			Keys.F10, // Quit 63
			Keys.F11, // Gamma 64
			Keys.F12, // MakeScreenshot 65
			0, // Last_Weapon_Switch
			0, // Crouch_toggle
	};

	public static final char[] defclassickeys = { Keys.UP, // Move_Forward 0
			Keys.DOWN, // Move_Backward 1
			Keys.LEFT, // Turn_Left 2
			Keys.RIGHT, // Turn_Right 3
			Keys.BACKSPACE, // Turn_Around 4
			Keys.ALT_LEFT, // Strafe 5
			Keys.COMMA, // Strafe_Left 6
			Keys.PERIOD, // Strafe_Right 7
			Keys.A, // Jump 8
			Keys.Z, // Crouch 9
			Keys.SHIFT_LEFT, // Run 10
			KEY_CAPSLOCK, // AutoRun 11
			Keys.SPACE, // Open 12
			Keys.CONTROL_LEFT, // Weapon_Fire 13
			0, //Special_Fire
			Keys.HOME, // Aim_Up 15
			Keys.END, // Aim_Down 16
			Keys.NUMPAD_5, // Aim_Center 17
			Keys.PAGE_UP, // Look_Up 18
			Keys.PAGE_DOWN, // Look_Down 19
			Keys.INSERT, // Tilt_Left 20
			Keys.FORWARD_DEL, // Tilt_Right 21
			Keys.NUM_1, // Weapon_1 22
			Keys.NUM_2, // Weapon_2 23
			Keys.NUM_3, // Weapon_3 24
			Keys.NUM_4, // Weapon_4 25
			Keys.NUM_5, // Weapon_5 26
			Keys.NUM_6, // Weapon_6 27
			Keys.NUM_7, // Weapon_7 28
			Keys.NUM_8, // Weapon_8 29
			Keys.NUM_9, // Weapon_9 30
			Keys.NUM_0, // Weapon_10 31
			Keys.ENTER, // Inventory_Use 32
			Keys.LEFT_BRACKET, // Inventory_Left 33
			Keys.RIGHT_BRACKET, // Inventory_Right 34
			Keys.TAB, // Map_Toggle 35
			Keys.F, // Map_Follow_Mode 36
			Keys.MINUS, // Shrink_Screen 37
			Keys.EQUALS, // Enlarge_Screen 38
			Keys.T, // Send_Message 39
			Keys.K, // See_Coop_View 40
			Keys.F7, // See_Chase_View 41
			Keys.U, // Mouse_Aiming 42
			Keys.I, // Toggle_Crosshair 43
			Keys.APOSTROPHE, // Next_Weapon 44
			Keys.SEMICOLON, // Previous_Weapon 45
			KEY_SCROLLOCK, // Holster_Weapon 46
			Keys.W, // Show_Opponents_Weapon 47
			Keys.N, // NightVision 48
			Keys.S, // SmokeBomb 49
			Keys.G, // GasBomb 50
			Keys.M, // MedKit 51
			Keys.B, // FlashBomb 52 //F original
			Keys.C, // Caltrops
			Keys.ESCAPE, // Open_menu 53
			Keys.GRAVE, // Show_Console 54
			Keys.F1, // Show_HelpScreen 55
			Keys.F2, // Show_Save 56
			Keys.F3, // Show_Load 57
			Keys.F4, // Show_Sounds 58
			Keys.F5, // Show_Options 59
			Keys.F6, // QuickSave 60
			Keys.F8, // ToggleMessages 61
			Keys.F9, // QuickLoad 62
			Keys.F10, // Quit 63
			Keys.F11, // Gamma 64
			Keys.F12, // MakeScreenshot 65
			0, // Last_Weapon_Switch
			0, // Crouch_toggle
	};

	public Config(String path, String name) {
		super(path, name);
	}

	@Override
	public void SaveConfig(FileResource fil) {
		if(fil != null)
		{
			saveString(fil, "[Options]\r\n");	
				//Options
			saveInteger(fil, "NetColor", NetColor);
			saveBoolean(fil, "AutoAim", AutoAim);
			saveBoolean(fil, "Crosshair", Crosshair);
			saveInteger(fil, "CrossSize", CrosshairSize);
			saveBoolean(fil, "MessageState", Messages);
			saveBoolean(fil, "Shadows", Shadows);
			saveBoolean(fil, "Ambient", Ambient);
			saveBoolean(fil, "Tilt", SlopeTilting);
			saveInteger(fil, "ShowStat", Stats);
			saveInteger(fil, "StatSize", gStatSize);
			saveInteger(fil, "showMapInfo", showMapInfo);
			saveBoolean(fil, "AutoRun", AutoRun);
			saveInteger(fil, "Size", BorderNum);
			saveBoolean(fil, "WeaponAutoSwitch", WeaponAutoSwitch);
			saveBoolean(fil, "UseDarts", UseDarts);
			saveBoolean(fil, "DisableHornets", DisableHornets);
			saveBoolean(fil, "ShowOpponentsWeapon", ShowWeapon);
		}
	}

	@Override
	public boolean InitConfig(boolean isDefault) {
		NetColor = 0;
		AutoAim = false;
		Ambient = true;
		BorderNum = 1;
		Messages = true;
		Crosshair = true;
		CrosshairSize = 65536;
		Shadows = true;
		ParentalLock = false;
		AutoRun = true;
		brightness = 0;
		Stats = 1;
		gStatSize = 65536;
		SlopeTilting = false;
		showMapInfo = 1;
		pName ="Kato";
		WeaponAutoSwitch = true;
		UseDarts = false;
		DisableHornets = false;
		ShowWeapon = false;
		
		if (!isDefault) {
			LoadCommon(defkeys, defclassickeys);
			if (set("Options")) {
				int value = GetKeyInt("NetColor");
				if(value != -1) NetColor = (byte) value;
				value = GetKeyInt("AutoAim");
				if(value != -1) AutoAim = value == 1;
				value = GetKeyInt("Crosshair");
				if(value != -1) Crosshair = value == 1;
				value = GetKeyInt("CrossSize");
				if(value != -1) CrosshairSize = BClipLow(value, 16384);
				value = GetKeyInt("MessageState");
				if(value != -1) Messages = value == 1;
				value = GetKeyInt("Shadows");
				if(value != -1) Shadows = value == 1;
				value = GetKeyInt("Ambient");
				if(value != -1) Ambient = value == 1;
				value = GetKeyInt("Tilt");
				if(value != -1) SlopeTilting = value == 1;
				value = GetKeyInt("ShowStat");
				if(value != -1) Stats = value;
				value = GetKeyInt("StatSize");
				if(value != -1) gStatSize = BClipLow(value, 16384);
				value = GetKeyInt("showMapInfo");
				if(value != -1) showMapInfo = value;
				value = GetKeyInt("AutoRun");
				if(value != -1) AutoRun = value == 1;
				value = GetKeyInt("Size");
				if(value != -1) BorderNum = BClipRange(value, 0, 2);
				value = GetKeyInt("WeaponAutoSwitch");
				if(value != -1) WeaponAutoSwitch = value == 1;
				value = GetKeyInt("UseDarts");
				if(value != -1) UseDarts = value == 1;
				value = GetKeyInt("DisableHornets");
				if(value != -1) DisableHornets = value == 1;
				value = GetKeyInt("ShowOpponentsWeapon");
				if(value != -1) ShowWeapon = value == 1;
			}
			close();
		} else {
			Console.Println("Config file not found, using default settings", OSDTEXT_YELLOW);

			for (int i = 0; i < keymap.length; i++)
				primarykeys[i] = defkeys[i];

			mousekeys[GameKeys.Weapon_Fire.getNum()] = MOUSE_LBUTTON;
			mousekeys[SwKeys.Special_Fire.getNum()] = MOUSE_RBUTTON;
			mousekeys[GameKeys.Open.getNum()] = MOUSE_MBUTTON;
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
			mousekeys[SwKeys.Special_Fire.getNum()] = MOUSE_RBUTTON;
			mousekeys[GameKeys.Open.getNum()] = MOUSE_MBUTTON;
			mousekeys[GameKeys.Next_Weapon.getNum()] = MOUSE_WHELLUP;
			mousekeys[GameKeys.Previous_Weapon.getNum()] = MOUSE_WHELLDN;
		}
		
		
		brightness = BuildSettings.paletteGamma.get() << 2;
		musicType = BClipRange(musicType, 0, 1);
		middrv = 0; //Dummy driver
		return true;
	}

	@Override
	public KeyType[] getKeyMap() {
		KeyType[] keymap = { GameKeys.Move_Forward, GameKeys.Move_Backward, GameKeys.Turn_Left, GameKeys.Turn_Right,
				GameKeys.Turn_Around, GameKeys.Strafe, GameKeys.Strafe_Left, GameKeys.Strafe_Right, GameKeys.Jump,
				GameKeys.Crouch, GameKeys.Run, SwKeys.AutoRun, GameKeys.Open, GameKeys.Weapon_Fire, SwKeys.Special_Fire, SwKeys.Aim_Up,
				SwKeys.Aim_Down, SwKeys.Aim_Center, GameKeys.Look_Up, GameKeys.Look_Down, SwKeys.Tilt_Left,
				SwKeys.Tilt_Right, SwKeys.Weapon_1, SwKeys.Weapon_2, SwKeys.Weapon_3, SwKeys.Weapon_4, SwKeys.Weapon_5,
				SwKeys.Weapon_6, SwKeys.Weapon_7, SwKeys.Weapon_8, SwKeys.Weapon_9, SwKeys.Weapon_10,
				SwKeys.Inventory_Use, SwKeys.Inventory_Left, SwKeys.Inventory_Right, GameKeys.Map_Toggle,
				SwKeys.Map_Follow_Mode, GameKeys.Shrink_Screen, GameKeys.Enlarge_Screen, GameKeys.Send_Message,
				SwKeys.See_Coop_View, SwKeys.See_Chase_View, GameKeys.Mouse_Aiming, SwKeys.Toggle_Crosshair,
				GameKeys.Next_Weapon, GameKeys.Previous_Weapon, SwKeys.Holster_Weapon, SwKeys.Show_Opp_Weapon,
				SwKeys.NightVision, SwKeys.SmokeBomb, SwKeys.GasBomb, SwKeys.MedKit, SwKeys.FlashBomb, SwKeys.Caltrops,
				GameKeys.Menu_Toggle, GameKeys.Show_Console, SwKeys.Show_Help, SwKeys.Show_Savemenu,
				SwKeys.Show_Loadmenu, SwKeys.Show_Sounds, SwKeys.Show_Options, SwKeys.Quicksave, SwKeys.Messages,
				SwKeys.Quickload, SwKeys.Quit, SwKeys.Gamma, SwKeys.Screenshot, SwKeys.Last_Weap_Switch,
				SwKeys.Crouch_toggle };
		return keymap;
	}

}
