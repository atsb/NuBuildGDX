//This file is part of BuildGDX.
//Copyright (C) 2017-2018  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
//BuildGDX is free software: you can redistribute it and/or modify
//it under the terms of the GNU General Public License as published by
//the Free Software Foundation, either version 3 of the License, or
//(at your option) any later version.
//
//BuildGDX is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU General Public License for more details.
//
//You should have received a copy of the GNU General Public License
//along with BuildGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.Build.Settings;

import static ru.m210projects.Build.Strhandler.toLowerCase;
import static ru.m210projects.Build.Net.Mmulti.NETPORT;
import static ru.m210projects.Build.OnSceenDisplay.Console.OSDTEXT_YELLOW;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;

import ru.m210projects.Build.FileHandle.Compat;
import ru.m210projects.Build.FileHandle.Compat.Path;
import ru.m210projects.Build.FileHandle.FileResource;
import ru.m210projects.Build.FileHandle.FileResource.Mode;
import ru.m210projects.Build.Input.ButtonMap;
import ru.m210projects.Build.Input.Keymap;
import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Build.Pattern.Tools.IniFile;
import ru.m210projects.Build.Render.Renderer.RenderType;

public abstract class BuildConfig extends IniFile {

	public static final int MINFOV = 60;
	public static final int MAXFOV = 140;

	public final int cfgVersion = 1901; // year XX, num XX
	public boolean isInited;

	public interface KeyType {

		int getNum();

		KeyType setNum(int num);

		String getName();

	}

	public KeyType[] joymap = { MenuKeys.Menu_Enter.setJoyNum(0), MenuKeys.Menu_Cancel.setJoyNum(1),
			MenuKeys.Menu_Up.setJoyNum(2), MenuKeys.Menu_Down.setJoyNum(3), MenuKeys.Menu_Left.setJoyNum(4),
			MenuKeys.Menu_Right.setJoyNum(5), };

	public enum MenuKeys implements KeyType {
		Menu_Enter, Menu_Cancel, Menu_Up, Menu_Down, Menu_Left, Menu_Right;

		private int num = -1;
		private int joynum = -1;

		@Override
		public int getNum() {
			return num;
		}

		@Override
		public String getName() {
			return name();
		}

		@Override
		public KeyType setNum(int num) {
			this.num = num;
			return this;
		}

		public int getJoyNum() {
			return joynum;
		}

		public KeyType setJoyNum(int num) {
			this.joynum = num;
			return this;
		}
	}

	public enum GameKeys implements KeyType {

		Move_Forward, Move_Backward, Turn_Left, Turn_Right, Turn_Around, Strafe, Strafe_Left, Strafe_Right, Jump,
		Crouch, Run, Open, Weapon_Fire, Next_Weapon, Previous_Weapon, Look_Up, Look_Down, Map_Toggle, Enlarge_Hud,
		Shrink_Hud, Send_Message, Mouse_Aiming, Menu_Toggle, Show_Console;

		private int num = -1;
		private String name;

		@Override
		public int getNum() {
			return num;
		}

		public void setName(String name) {
			this.name = name;
		}

		@Override
		public String getName() {
			return (name != null) ? name : name();
		}

		@Override
		public KeyType setNum(int num) {
			this.num = num;
			return this;
		}
	}

	public String path;
	public String cfgPath;
	public File soundBank;
	public int version;
	public boolean startup = true;
	public boolean autoloadFolder = true;
	public boolean userfolder = false;
	public boolean checkVersion = true;

	public int fullscreen = 0;
	public int ScreenWidth = 640;
	public int ScreenHeight = 400;
	public boolean gVSync = false;
	public int fpslimit = 0;
	public boolean borderless = false;

	public int snddrv = 1;
	public int middrv = 1;
	public int midiSynth = 0;

	protected int paletteGamma = 0;
	protected float fgamma = 1;
public float gFpsScale = 1.0f;

	public KeyType[] keymap;
	public int[] primarykeys;
	public int[] secondkeys;
	public int[] mousekeys;
	public int[] gpadkeys;

	public int gJoyMoveAxis = 0; // Stick1Y
	public int gJoyStrafeAxis = 1; // Stick1X
	public int gJoyLookAxis = 2; // Stick2Y
	public int gJoyTurnAxis = 3; // Stick2X
	public int gJoyTurnSpeed = 65536;
	public int gJoyLookSpeed = 65536;
	public int gJoyDeadZone = 6144;
	public boolean gJoyInvert = false;
	public int gJoyDevice = -1;
	public int[] gJoyMenukeys = new int[joymap.length];

	public boolean useMouse = true;
	public boolean menuMouse = true;
	public int gSensitivity = 69632;
	public int gMouseTurnSpeed = 65536;
	public int gMouseLookSpeed = 65536;
	public int gMouseMoveSpeed = 65536;
	public int gMouseStrafeSpeed = 131072;
	public int gMouseCursor = 0;
	public int gMouseCursorSize = 65536;
	public boolean gMouseAim = true;
	public boolean gInvertmouse = false;

	public static final int AXISLEFT = 0;
	public static final int AXISRIGHT = 1;
	public static final int AXISUP = 2;
	public static final int AXISDOWN = 3;
	public int[] mouseaxis = new int[4];

	public float soundVolume = 1.00f;
	public float musicVolume = 1.00f;
	public boolean noSound = false;
	public boolean muteMusic = false;
	public int resampler_num = 0;
	public int maxvoices = 32;
	public int musicType = 0;

	protected boolean paletteEmulation = true;
	protected int glanisotropy = 0;
	public int widescreen = 1;
	protected int glfilter = 0;
	public boolean gShowFPS = true;
	public int gFov = 90;

	public String pName;
	public String mAddress = "localhost";
	public int mPort = NETPORT;

	public RenderType renderType = RenderType.Software;
	public boolean gPrecache = true;

	public BuildConfig(String path, String name) {
		super();

		this.cfgPath = path;
		this.keymap = getKeyMap();
		this.name = toLowerCase(name);
		byte[] data = null;

		try {
			RandomAccessFile raf = new RandomAccessFile(path + this.name, "r");
			data = new byte[(int) raf.length()];
			raf.read(data);
			init(data);
			raf.close();
		} catch (FileNotFoundException e) {
			Console.Println("File not found: " + path + this.name, OSDTEXT_YELLOW);
		} catch (IOException e) {
			Console.Println("Read file error: " + e.getMessage(), OSDTEXT_YELLOW);
		}

		this.primarykeys = new int[keymap.length];
		this.secondkeys = new int[keymap.length];
		this.mousekeys = new int[keymap.length];
		this.gpadkeys = new int[keymap.length];

		Arrays.fill(mouseaxis, -1);
		Arrays.fill(gpadkeys, -1);
		Arrays.fill(gJoyMenukeys, -1);

		LoadMain();

		if (version != cfgVersion && data != null) {
			try {
				int index = name.lastIndexOf(".");
				String cfgname = name.substring(0, index);
				RandomAccessFile raf = new RandomAccessFile(path + cfgname + ".old", "rw");
				raf.write(data);
				raf.close();
			} catch (Exception e) {
				Console.Println("Old config file error: " + e.getMessage(), OSDTEXT_YELLOW);
			}
		}
	}

	public boolean isExist() {
		return data != null && version == cfgVersion;
	}

	public void LoadMain() {
		if (data != null) {
			if (set("Main")) {
				version = GetKeyInt("ConfigVersion");
				startup = GetKeyInt("Startup") == 1;
				int check = GetKeyInt("CheckNewVersion");
				if (check != -1)
					checkVersion = (check == 1);
				int afolder = GetKeyInt("AutoloadFolder");
				if (afolder != -1)
					autoloadFolder = (afolder == 1);
				userfolder = GetKeyInt("UseHomeFolder") == 1;
				String respath = GetKeyString("Path");
				if (respath != null && !respath.isEmpty())
					this.path = respath;
				String bank = GetKeyString("SoundBank");
				if (bank != null && !bank.isEmpty()) {
					File fbank = new File(bank);
					if (fbank.exists())
						this.soundBank = fbank;
				}
			}

			if (version != cfgVersion)
				return;

			if (set("ScreenSetup")) {
				String render = GetKeyString("Render");
				if (render != null) {
					if (render.equals("Classic"))
						renderType = RenderType.Software;
					else {
						RenderType ren = RenderType.valueOf(render);
						if (ren != null)
							renderType = ren;
					}
				}

				int value = GetKeyInt("Fullscreen");
				if (value != -1)
					fullscreen = value;
				value = GetKeyInt("ScreenWidth");
				if (value >= 640)
					ScreenWidth = value;
				value = GetKeyInt("ScreenHeight");
				if (value >= 400)
					ScreenHeight = value;
				borderless = GetKeyInt("BorderlessMode") == 1;
				gVSync = GetKeyInt("VSync") == 1;
				value = GetKeyInt("FPSLimit");
				if (value != -1)
					fpslimit = value;
				value = GetKeyInt("GLFilterMode");
				if (value != -1)
					glfilter = value;
				value = GetKeyInt("GLAnisotropy");
				if (value != -1)
					glanisotropy = value;
				value = GetKeyInt("WideScreen");
				if (value != -1)
					widescreen = value;
				value = GetKeyInt("FieldOfView");
				if (value != -1 && value >= MINFOV && value <= MAXFOV)
					gFov = value;
				value = GetKeyInt("Palette_Emulation");
				if (value != -1)
					paletteEmulation = value == 1;
			}

			if (set("SoundSetup")) {
				noSound = GetKeyInt("NoSound") == 1;
				muteMusic = GetKeyInt("NoMusic") == 1;
				int snd = GetKeyInt("SoundDriver");
				if (snd != -1)
					snddrv = snd;
				int mid = GetKeyInt("MidiDriver");
				if (mid != -1)
					middrv = mid;
				int mids = GetKeyInt("MidiSynth");
				if (mids != -1)
					midiSynth = mids;
				int type = GetKeyInt("MusicType");
				if (type != -1)
					musicType = type;
			}
		}

		isInited = false;
	}

	public void LoadCommon(char[] defkeys, char[] defclassickeys) {
		if (set("Main")) {
			int value = GetKeyInt("OSDTextScale");
			if (value != -1)
				Console.setTextScale(value);

			value = GetKeyInt("UseVoxels");
			if (value != -1)
				BuildSettings.useVoxels.set(value == 1);
			value = GetKeyInt("UseModels");
			if (value != -1)
				GLSettings.useModels.set(value == 1);
			value = GetKeyInt("UseHightiles");
			if (value != -1)
				GLSettings.useHighTile.set(value == 1);
			value = GetKeyInt("UsePrecache");
			if (value != -1)
				gPrecache = (value == 1);

			String name = GetKeyString("Player_name");
			if (name != null)
				pName = name;

			String ip = GetKeyString("IP_Address");
			if (ip != null)
				mAddress = ip;
			value = GetKeyInt("Port");
			if (value != -1)
				mPort = value;
		}

		if (set("ScreenSetup")) {
			gVSync = GetKeyInt("VSync") == 1;
			int value = GetKeyInt("FpsScale");
			if (value != -1)
				gFpsScale = value / 65536.0f;

			gShowFPS = GetKeyInt("ShowFPS") == 1;
		}

		if (set("SoundSetup")) {
			float value = GetKeyInt("SoundVolume") / 256.0f;
			if (value >= 0 && value < 1.0)
				soundVolume = value;
			int voices = GetKeyInt("MaxVoices");
			if (voices >= 0 && voices <= 256)
				maxvoices = voices;
			int resampler = GetKeyInt("Resampler");
			if (resampler != -1)
				resampler_num = resampler;
			value = GetKeyInt("MusicVolume") / 256.0f;
			if (value >= 0 && value < 1.0)
				musicVolume = value;
		}

		if (set("KeyDefinitions")) {
			for (int i = 0; i < keymap.length; i++) {
				primarykeys[i] = defkeys[i];
				secondkeys[i] = 0;
				mousekeys[i] = 0;
				gpadkeys[i] = -1;

				String primary = GetKeyString(keymap[i].getName(), 0);
				String secondary = GetKeyString(keymap[i].getName(), 1);
				String mouse = GetKeyString(keymap[i].getName(), 2);
				String joystick = GetKeyString(keymap[i].getName(), 3);

				if (primary != null)
					primarykeys[i] = Keymap.valueOf(primary);
				if (secondary != null)
					secondkeys[i] = Keymap.valueOf(secondary);
				if (mouse != null)
					mousekeys[i] = Keymap.valueOf(mouse);
				if (joystick != null)
					gpadkeys[i] = ButtonMap.valueOf(joystick);
			}
			if (primarykeys[GameKeys.Menu_Toggle.getNum()] == 0)
				primarykeys[GameKeys.Menu_Toggle.getNum()] = defclassickeys[GameKeys.Menu_Toggle.getNum()];

			String left = GetKeyString("MouseDigitalAxes0_0");
			if (left != null)
				mouseaxis[AXISLEFT] = getKeyIndex(left);
			String right = GetKeyString("MouseDigitalAxes0_1");
			if (right != null)
				mouseaxis[AXISRIGHT] = getKeyIndex(right);
			String up = GetKeyString("MouseDigitalAxes1_0");
			if (up != null)
				mouseaxis[AXISUP] = getKeyIndex(up);
			String down = GetKeyString("MouseDigitalAxes1_1");
			if (down != null)
				mouseaxis[AXISDOWN] = getKeyIndex(down);
		}

		if (set("JoyDefinitions")) {
			for (int i = 0; i < joymap.length; i++) {
				gJoyMenukeys[((MenuKeys) joymap[i]).getJoyNum()] = -1;
				String joymenu = GetKeyString(joymap[i].getName(), 0);
				if (joymenu != null)
					gJoyMenukeys[((MenuKeys) joymap[i]).getJoyNum()] = ButtonMap.valueOf(joymenu);
			}
		}

		if (set("Controls")) {
			int value = GetKeyInt("UseMouse");
			if (value != -1)
				useMouse = value == 1;
			value = GetKeyInt("UseMouseInMenu");
			if (value != -1)
				menuMouse = value == 1;
			value = GetKeyInt("MouseSensitivity");
			if (value != -1)
				gSensitivity = value;
			value = GetKeyInt("MouseAiming");
			if (value != -1)
				gMouseAim = value == 1;
			value = GetKeyInt("MouseAimingFlipped");
			if (value != -1)
				gInvertmouse = value == 1;
			value = GetKeyInt("MouseTurnSpeed");
			if (value != -1)
				gMouseTurnSpeed = value;
			value = GetKeyInt("MouseLookSpeed");
			if (value != -1)
				gMouseLookSpeed = value;
			value = GetKeyInt("MouseMoveSpeed");
			if (value != -1)
				gMouseMoveSpeed = value;
			value = GetKeyInt("MouseStrafeSpeed");
			if (value != -1)
				gMouseStrafeSpeed = value;
			value = GetKeyInt("MouseCursor");
			if (value != -1)
				gMouseCursor = value;
			value = GetKeyInt("MouseCursorSize");
			if (value != -1)
				gMouseCursorSize = value;
			value = GetKeyInt("JoyDevice");
			if (value != -1)
				gJoyDevice = value;
			value = GetKeyInt("JoyTurnAxis");
			if (value != -1)
				gJoyTurnAxis = value;
			value = GetKeyInt("JoyMoveAxis");
			if (value != -1)
				gJoyMoveAxis = value;
			value = GetKeyInt("JoyStrafeAxis");
			if (value != -1)
				gJoyStrafeAxis = value;
			value = GetKeyInt("JoyLookAxis");
			if (value != -1)
				gJoyLookAxis = value;
			value = GetKeyInt("JoyTurnSpeed");
			if (value != -1)
				gJoyTurnSpeed = value;
			value = GetKeyInt("JoyLookSpeed");
			if (value != -1)
				gJoyLookSpeed = value;
			value = GetKeyInt("JoyInvertLook");
			if (value != -1)
				gJoyInvert = value == 1;
			value = GetKeyInt("JoyDeadZone");
			if (value != -1)
				gJoyDeadZone = value;
		}
	}

	public void saveConfig(Compat compat, String path) {
		if (!isInited) { // has saving from launcher
			FileResource fil = compat.open(cfgPath + name, Path.Absolute, Mode.Write);
			if (fil != null) {
				SaveUninited(fil);
				fil.close();
			}
			return;
		}

		FileResource fil = compat.open(cfgPath + name, Path.Absolute, Mode.Write);
		if (fil != null) {
			SaveMain(fil, path);
			SaveCommon(fil);
			SaveConfig(fil);
			fil.close();
		}
	}

	public void SaveCommon(FileResource fil) {
		saveString(fil, "[KeyDefinitions]\r\n");
		for (int i = 0; i < keymap.length; i++) {
			String line = keymap[i] + " = \"" + Keymap.toString(primarykeys[i]) + "\", \""
					+ Keymap.toString(secondkeys[i]) + "\", \"" + Keymap.toString(mousekeys[i]) + "\", \""
					+ ButtonMap.buttonName(gpadkeys[i]) + "\"\r\n";
			saveString(fil, line);
		}
		saveString(fil, ";\r\n");
		saveString(fil, "MouseDigitalAxes0_0 "
				+ ((mouseaxis[AXISLEFT] != -1) ? ("= " + keymap[mouseaxis[AXISLEFT]]) : "= \"N/A\"") + "\r\n");
		saveString(fil, "MouseDigitalAxes0_1 "
				+ ((mouseaxis[AXISRIGHT] != -1) ? ("= " + keymap[mouseaxis[AXISRIGHT]]) : "= \"N/A\"") + "\r\n");
		saveString(fil, "MouseDigitalAxes1_0 "
				+ ((mouseaxis[AXISUP] != -1) ? ("= " + keymap[mouseaxis[AXISUP]]) : "= \"N/A\"") + "\r\n");
		saveString(fil, "MouseDigitalAxes1_1 "
				+ ((mouseaxis[AXISDOWN] != -1) ? ("= " + keymap[mouseaxis[AXISDOWN]]) : "= \"N/A\"") + "\r\n");
		saveString(fil, ";\r\n");
		saveString(fil, "[JoyDefinitions]\r\n");
		for (int i = 0; i < joymap.length; i++) {
			String line = joymap[i] + " = \"" + ButtonMap.buttonName(gJoyMenukeys[((MenuKeys) joymap[i]).getJoyNum()])
					+ "\"\r\n";
			saveString(fil, line);
		}
		saveString(fil, ";\r\n;\r\n");

		saveString(fil, "[Controls]\r\n");
		// Controls

		saveBoolean(fil, "UseMouse", useMouse);
		saveBoolean(fil, "UseMouseInMenu", menuMouse);
		saveInteger(fil, "MouseSensitivity", gSensitivity);
		saveBoolean(fil, "MouseAiming", gMouseAim);
		saveBoolean(fil, "MouseAimingFlipped", gInvertmouse);
		saveInteger(fil, "MouseTurnSpeed", gMouseTurnSpeed);
		saveInteger(fil, "MouseLookSpeed", gMouseLookSpeed);
		saveInteger(fil, "MouseMoveSpeed", gMouseMoveSpeed);
		saveInteger(fil, "MouseStrafeSpeed", gMouseStrafeSpeed);
		saveInteger(fil, "MouseCursor", gMouseCursor);
		saveInteger(fil, "MouseCursorSize", gMouseCursorSize);
		saveInteger(fil, "JoyDevice", gJoyDevice);
		saveInteger(fil, "JoyTurnAxis", gJoyTurnAxis);
		saveInteger(fil, "JoyMoveAxis", gJoyMoveAxis);
		saveInteger(fil, "JoyStrafeAxis", gJoyStrafeAxis);
		saveInteger(fil, "JoyLookAxis", gJoyLookAxis);
		saveInteger(fil, "JoyTurnSpeed", gJoyTurnSpeed);
		saveInteger(fil, "JoyLookSpeed", gJoyLookSpeed);
		saveBoolean(fil, "JoyInvertLook", gJoyInvert);
		saveInteger(fil, "JoyDeadZone", gJoyDeadZone);
		saveString(fil, ";\r\n;\r\n");
	}

	public void SaveMain(FileResource fil, String path) {
		saveString(fil, "[Main]\r\n");
		saveInteger(fil, "ConfigVersion", cfgVersion);
		saveBoolean(fil, "Startup", startup);
		saveBoolean(fil, "CheckNewVersion", checkVersion);
		saveBoolean(fil, "AutoloadFolder", autoloadFolder);
		saveBoolean(fil, "UseHomeFolder", userfolder);
		saveString(fil, "Path = ");
		byte[] buf = path.getBytes(); // without this path can be distorted
		fil.writeBytes(buf, buf.length);

		if (soundBank != null)
			saveString(fil, "\r\nSoundBank = " + soundBank.getAbsolutePath() + "\r\n");
		else
			saveString(fil, "\r\nSoundBank = \r\n");
		saveInteger(fil, "OSDTextScale", Console.getTextScale());
		saveBoolean(fil, "UseVoxels", BuildSettings.useVoxels.get());
		saveBoolean(fil, "UseModels", GLSettings.useModels.get());
		saveBoolean(fil, "UseHightiles", GLSettings.useHighTile.get());
		saveBoolean(fil, "UsePrecache", gPrecache);
		saveString(fil, "Player_name", pName);
		saveString(fil, "IP_Address", mAddress);
		saveInteger(fil, "Port", mPort);
		saveString(fil, ";\r\n;\r\n");
		saveString(fil, "[ScreenSetup]\r\n");
		// Screen Setup
		saveString(fil, "Render", renderType.getName());
		saveInteger(fil, "Fullscreen", fullscreen);
		saveInteger(fil, "ScreenWidth", ScreenWidth);
		saveInteger(fil, "ScreenHeight", ScreenHeight);
		saveBoolean(fil, "BorderlessMode", borderless);
		saveBoolean(fil, "VSync", gVSync);
		saveInteger(fil, "FPSLimit", fpslimit);
		saveInteger(fil, "GLFilterMode", glfilter);
		saveInteger(fil, "GLAnisotropy", glanisotropy);
		saveInteger(fil, "WideScreen", widescreen);
		saveInteger(fil, "FieldOfView", gFov);
		saveInteger(fil, "FpsScale", (int) (gFpsScale * 65536.0f));
		saveBoolean(fil, "Palette_Emulation", paletteEmulation);

		saveInteger(fil, "GLGamma", GLSettings.gamma.get());
		saveInteger(fil, "PaletteGamma", BuildSettings.paletteGamma.get());
		saveBoolean(fil, "ShowFPS", gShowFPS);
		saveString(fil, ";\r\n;\r\n");

		saveString(fil, "[SoundSetup]\r\n");
		// Sound Setup

		saveBoolean(fil, "NoSound", noSound);
		saveBoolean(fil, "NoMusic", muteMusic);
		saveInteger(fil, "SoundDriver", snddrv);

		saveInteger(fil, "MidiDriver", middrv);
		saveInteger(fil, "MidiSynth", midiSynth);
		saveInteger(fil, "MusicType", musicType);
		saveInteger(fil, "SoundVolume", (int) (soundVolume * 256.0f));
		saveInteger(fil, "MaxVoices", maxvoices);
		saveInteger(fil, "Resampler", resampler_num);
		saveInteger(fil, "MusicVolume", (int) (musicVolume * 256.0f));
		saveString(fil, ";\r\n;\r\n");
	}

	public abstract void SaveConfig(FileResource fil);

	public abstract boolean InitConfig(boolean isDefault);

	public void InitKeymap() {
		for (int i = 0; i < keymap.length; i++)
			keymap[i].setNum(i);
	}

	public abstract KeyType[] getKeyMap();

	public void setKey(int index, int keyId) {
		if (primarykeys[index] == 0 && secondkeys[index] == 0)
			primarykeys[index] = keyId;
		else if (primarykeys[index] != 0 && secondkeys[index] == 0) {
			if (keyId != primarykeys[index]) {
				secondkeys[index] = primarykeys[index];
				primarykeys[index] = keyId;
			} else
				secondkeys[index] = 0;
		} else {
			if (keyId == primarykeys[index] || keyId == secondkeys[index]) {
				primarykeys[index] = keyId;
				secondkeys[index] = 0;
			} else {
				secondkeys[index] = primarykeys[index];
				primarykeys[index] = keyId;
			}
		}

		for (int i = 0; i < primarykeys.length; i++) {
			if (i != index && keyId == primarykeys[i]) {
				if (primarykeys[i] != 0 && secondkeys[i] != 0) {
					primarykeys[i] = secondkeys[i];
					secondkeys[i] = 0;
				} else
					primarykeys[i] = 0;
			}
		}

		for (int i = 0; i < secondkeys.length; i++) {
			if (i != index && keyId == secondkeys[i]) {
				secondkeys[i] = 0;
			}
		}
	}

	public void setButton(KeyType key, int button) {
		if (key instanceof MenuKeys) {
			MenuKeys mk = (MenuKeys) key;
			gJoyMenukeys[mk.getJoyNum()] = button;
		} else
			gpadkeys[key.getNum()] = button;
	}

	public int getKeyIndex(String keyname) {
		keyname = keyname.replaceAll("[^a-zA-Z_-]", "");
		for (int i = 0; i < keymap.length; i++) {
			if (keyname.equals(keymap[i].getName()))
				return i;
		}
		return -1;
	}

	public void saveBoolean(FileResource fil, String name, boolean var) {
		String line = name + " = " + (var ? 1 : 0) + "\r\n";
		fil.writeBytes(line.toCharArray(), line.length());
	}

	public void saveInteger(FileResource fil, String name, int var) {
		String line = name + " = " + var + "\r\n";
		fil.writeBytes(line.toCharArray(), line.length());
	}

	public void saveString(FileResource fil, String text) {
		fil.writeBytes(text.toCharArray(), text.length());
	}

	public void saveString(FileResource fil, String name, String var) {
		String line = name + " = " + var + "\r\n";
		fil.writeBytes(line.toCharArray(), line.length());
	}

	public void SaveUninited(FileResource fil) {
		if (data == null)
			return;

		set("Main");
		saveString(fil, "[Main]\r\n");
		saveInteger(fil, "ConfigVersion", cfgVersion);
		saveBoolean(fil, "Startup", startup);
		saveBoolean(fil, "CheckNewVersion", checkVersion);
		saveBoolean(fil, "AutoloadFolder", autoloadFolder);
		saveBoolean(fil, "UseHomeFolder", userfolder);
		saveString(fil, "Path = ");
		if (path != null) {
			byte[] buf = path.getBytes(); // without this path can be distorted
			fil.writeBytes(buf, buf.length);
		}

		if (soundBank != null)
			saveString(fil, "\r\nSoundBank = " + soundBank.getAbsolutePath() + "\r\n");
		else
			saveString(fil, "\r\nSoundBank = \r\n");

		saveInteger(fil, "OSDTextScale", GetKeyInt("OSDTextScale"));
		saveBoolean(fil, "UseVoxels", GetKeyInt("UseVoxels") == 1);
		saveBoolean(fil, "UseModels", GetKeyInt("UseModels") == 1);
		saveBoolean(fil, "UseHightiles", GetKeyInt("UseHightiles") == 1);

		saveString(fil, "Player_name", GetKeyString("Player_name"));
		saveString(fil, "IP_Address", GetKeyString("IP_Address"));
		saveInteger(fil, "Port", GetKeyInt("Port"));
		saveString(fil, ";\r\n;\r\n");

		set("ScreenSetup");
		saveString(fil, "[ScreenSetup]\r\n");
		// Screen Setup
		saveString(fil, "Render", renderType.getName());
		saveInteger(fil, "Fullscreen", fullscreen);
		saveInteger(fil, "ScreenWidth", ScreenWidth);
		saveInteger(fil, "ScreenHeight", ScreenHeight);
		saveBoolean(fil, "BorderlessMode", borderless);
		saveBoolean(fil, "VSync", GetKeyInt("VSync") == 1);
		saveInteger(fil, "FPSLimit", GetKeyInt("FPSLimit"));
		saveInteger(fil, "GLFilterMode", GetKeyInt("GLFilterMode"));
		saveInteger(fil, "GLAnisotropy", GetKeyInt("GLAnisotropy"));
		saveInteger(fil, "WideScreen", GetKeyInt("WideScreen"));
		saveInteger(fil, "FieldOfView", GetKeyInt("FieldOfView"));
		saveInteger(fil, "FpsScale", GetKeyInt("FpsScale"));
		saveBoolean(fil, "Palette_Emulation", GetKeyInt("Palette_Emulation") == 1);
		saveInteger(fil, "PaletteGamma", GetKeyInt("PaletteGamma"));
		saveBoolean(fil, "ShowFPS", GetKeyInt("ShowFPS") == 1);
		saveString(fil, ";\r\n;\r\n");

		set("SoundSetup");
		saveString(fil, "[SoundSetup]\r\n");
		// Sound Setup

		saveBoolean(fil, "NoSound", GetKeyInt("NoSound") == 1);
		saveBoolean(fil, "NoMusic", GetKeyInt("NoMusic") == 1);
		saveInteger(fil, "SoundDriver", snddrv);

		saveInteger(fil, "MidiDriver", middrv);
		saveInteger(fil, "MidiSynth", midiSynth);
		saveInteger(fil, "MusicType", GetKeyInt("MusicType"));
		saveInteger(fil, "SoundVolume", GetKeyInt("SoundVolume"));
		saveInteger(fil, "MaxVoices", GetKeyInt("MaxVoices"));
		saveInteger(fil, "Resampler", GetKeyInt("Resampler"));
		saveInteger(fil, "MusicVolume", GetKeyInt("MusicVolume"));
		saveString(fil, ";\r\n;\r\n");

		String gamePart = new String(data);
		int index = gamePart.indexOf("[KeyDefinitions]");
		if (index != -1) {
			gamePart = gamePart.substring(index);
			fil.writeBytes(gamePart.toCharArray(), gamePart.length());
		}
	}
}
