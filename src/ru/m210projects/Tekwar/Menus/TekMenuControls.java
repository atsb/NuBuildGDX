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

package ru.m210projects.Tekwar.Menus;

import static ru.m210projects.Build.Input.Keymap.MOUSE_LBUTTON;
import static ru.m210projects.Build.Input.Keymap.MOUSE_MBUTTON;
import static ru.m210projects.Build.Input.Keymap.MOUSE_RBUTTON;
import static ru.m210projects.Build.Input.Keymap.MOUSE_WHELLDN;
import static ru.m210projects.Build.Input.Keymap.MOUSE_WHELLUP;

import java.util.Arrays;

import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Build.Pattern.BuildGame;
import ru.m210projects.Build.Pattern.CommonMenus.MenuControls;
import ru.m210projects.Build.Pattern.MenuItems.BuildMenu;
import ru.m210projects.Build.Pattern.MenuItems.MenuHandler;
import ru.m210projects.Build.Pattern.MenuItems.MenuTitle;
import ru.m210projects.Build.Settings.BuildConfig;
import ru.m210projects.Build.Settings.BuildConfig.GameKeys;
import ru.m210projects.Tekwar.Config;
import ru.m210projects.Tekwar.Config.TekKeys;

public class TekMenuControls extends MenuControls {

	public TekMenuControls(BuildGame app) {
		super(app, 30, 60, app.getFont(0).getHeight() + 4, app.getFont(0).getHeight(), app.getFont(0), 2, 3);
	}

	@Override
	public MenuTitle getTitle(BuildGame app, String text) {
		MenuTitle title = new MenuTitle(app.pEngine, text, app.getFont(0), 160, 15, -1);
		title.pal = 3;
		
		return title;
	}

	@Override
	public BuildMenu getMouseMenu(BuildGame app) {
		return new TekMenuMouse(app);
	}

	@Override
	public BuildMenu getJoystickMenu(BuildGame app) {
		return new TekMenuJoystick(app);
	}

	@Override
	public BuildMenu getKeyboardMenu(BuildGame app) {
		return new TekMenuKeyboard(app);
	}

	@Override
	public void mResetDefault(BuildConfig cfg, MenuHandler menu) {
		for (int i = 0; i < cfg.keymap.length; i++)
			cfg.primarykeys[i] = Config.defkeys[i];

		Arrays.fill(cfg.secondkeys, 0);
		Arrays.fill(cfg.mousekeys, 0);

		cfg.mousekeys[GameKeys.Weapon_Fire.getNum()] = MOUSE_LBUTTON;
		cfg.mousekeys[TekKeys.Holster_Weapon.getNum()] = MOUSE_RBUTTON;
		cfg.mousekeys[GameKeys.Jump.getNum()] = MOUSE_MBUTTON;
		cfg.mousekeys[GameKeys.Next_Weapon.getNum()] = MOUSE_WHELLUP;
		cfg.mousekeys[GameKeys.Previous_Weapon.getNum()] = MOUSE_WHELLDN;
		
		Console.setCaptureKey(cfg.primarykeys[GameKeys.Show_Console.getNum()], 0);
		Console.setCaptureKey(cfg.secondkeys[GameKeys.Show_Console.getNum()], 1);
		Console.setCaptureKey(cfg.mousekeys[GameKeys.Show_Console.getNum()], 2);
		
		menu.mMenuBack();
	}

	@Override
	public void mResetClassic(BuildConfig cfg, MenuHandler menu) {
		
		for (int i = 0; i < cfg.keymap.length; i++)
			cfg.primarykeys[i] = Config.defclassickeys[i];

		Arrays.fill(cfg.secondkeys, 0);
		Arrays.fill(cfg.mousekeys, 0);

		cfg.mousekeys[GameKeys.Weapon_Fire.getNum()] = MOUSE_LBUTTON;
		cfg.mousekeys[TekKeys.Holster_Weapon.getNum()] = MOUSE_RBUTTON;
		cfg.mousekeys[GameKeys.Jump.getNum()] = MOUSE_MBUTTON;
		cfg.mousekeys[GameKeys.Next_Weapon.getNum()] = MOUSE_WHELLUP;
		cfg.mousekeys[GameKeys.Previous_Weapon.getNum()] = MOUSE_WHELLDN;
		
		Console.setCaptureKey(cfg.primarykeys[GameKeys.Show_Console.getNum()], 0);
		Console.setCaptureKey(cfg.secondkeys[GameKeys.Show_Console.getNum()], 1);
		Console.setCaptureKey(cfg.mousekeys[GameKeys.Show_Console.getNum()], 2);
		
		menu.mMenuBack();
	}

}
