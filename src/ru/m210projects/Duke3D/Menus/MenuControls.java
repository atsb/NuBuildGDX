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

package ru.m210projects.Duke3D.Menus;

import ru.m210projects.Duke3D.Config;
import ru.m210projects.Duke3D.Config.DukeKeys;
import ru.m210projects.Duke3D.Main;

import static ru.m210projects.Build.Input.Keymap.MOUSE_LBUTTON;
import static ru.m210projects.Build.Input.Keymap.MOUSE_MBUTTON;
import static ru.m210projects.Build.Input.Keymap.MOUSE_RBUTTON;
import static ru.m210projects.Build.Input.Keymap.MOUSE_WHELLDN;
import static ru.m210projects.Build.Input.Keymap.MOUSE_WHELLUP;

import java.util.Arrays;

import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Build.Pattern.MenuItems.BuildMenu;
import ru.m210projects.Build.Pattern.MenuItems.MenuButton;
import ru.m210projects.Build.Pattern.MenuItems.MenuHandler;
import ru.m210projects.Build.Pattern.MenuItems.MenuText;
import ru.m210projects.Build.Pattern.MenuItems.MenuVariants;
import ru.m210projects.Build.Settings.BuildConfig;
import ru.m210projects.Build.Settings.BuildConfig.GameKeys;

public class MenuControls extends BuildMenu {

	public MenuControls(final Main app)
	{
		addItem(new DukeTitle("Controls Setup"), false);
		int pos = 39;

		MenuButton mMouseSet = new MenuButton("Mouse setup", app.getFont(2), 0, pos += 20, 320, 1, 0, new DMenuMouse(app), -1, null, 0);
		MenuButton mJoySet = new MenuButton("Joystick setup", app.getFont(2), 0, pos += 20, 320, 1, 0, new DMenuJoystick(app), -1, null, 0);
		MenuButton mKeySet = new MenuButton("Keyboard Setup", app.getFont(2), 0, pos += 20, 320, 1, 0, new DMenuMenuKeyboard(app), -1, null, 0);
		MenuButton mKeyReset = new MenuButton("Reset to default", app.getFont(2), 0, pos += 30, 320, 1, 0, getResetDefaultMenu(app), -1, null,0);
		MenuButton mKeyClassic = new MenuButton("Reset to classic", app.getFont(2), 0, pos += 20, 320, 1, 0, getResetClassicMenu(app), -1, null,0);
		
		addItem(mMouseSet, true);
		addItem(mJoySet, false);
		addItem(mKeySet, false);
		addItem(mKeyReset, false);
		addItem(mKeyClassic, false);
	}
	
	private void mResetDefault(BuildConfig cfg, MenuHandler menu) {
		for (int i = 0; i < cfg.keymap.length; i++)
			cfg.primarykeys[i] = Config.defkeys[i];

		Arrays.fill(cfg.secondkeys, 0);
		Arrays.fill(cfg.mousekeys, 0);

		cfg.mousekeys[GameKeys.Weapon_Fire.getNum()] = MOUSE_LBUTTON;
		cfg.mousekeys[DukeKeys.Quick_Kick.getNum()] = MOUSE_RBUTTON;
		cfg.mousekeys[GameKeys.Open.getNum()] = MOUSE_MBUTTON;
		cfg.mousekeys[GameKeys.Next_Weapon.getNum()] = MOUSE_WHELLUP;
		cfg.mousekeys[GameKeys.Previous_Weapon.getNum()] = MOUSE_WHELLDN;
		
		Console.setCaptureKey(cfg.primarykeys[GameKeys.Show_Console.getNum()], 0);
		Console.setCaptureKey(cfg.secondkeys[GameKeys.Show_Console.getNum()], 1);
		Console.setCaptureKey(cfg.mousekeys[GameKeys.Show_Console.getNum()], 2);
		
		menu.mMenuBack();
	}

	private void mResetClassic(BuildConfig cfg, MenuHandler menu) {
		
		for (int i = 0; i < cfg.keymap.length; i++)
			cfg.primarykeys[i] = Config.defclassickeys[i];

		Arrays.fill(cfg.secondkeys, 0);
		Arrays.fill(cfg.mousekeys, 0);

		cfg.mousekeys[GameKeys.Weapon_Fire.getNum()] = MOUSE_LBUTTON;
		cfg.mousekeys[DukeKeys.Quick_Kick.getNum()] = MOUSE_RBUTTON;
		cfg.mousekeys[GameKeys.Open.getNum()] = MOUSE_MBUTTON;
		cfg.mousekeys[GameKeys.Next_Weapon.getNum()] = MOUSE_WHELLUP;
		cfg.mousekeys[GameKeys.Previous_Weapon.getNum()] = MOUSE_WHELLDN;
		
		Console.setCaptureKey(cfg.primarykeys[GameKeys.Show_Console.getNum()], 0);
		Console.setCaptureKey(cfg.secondkeys[GameKeys.Show_Console.getNum()], 1);
		Console.setCaptureKey(cfg.mousekeys[GameKeys.Show_Console.getNum()], 2);
		
		menu.mMenuBack();
	}
	
	private BuildMenu getResetDefaultMenu(final Main app)
	{
		BuildMenu menu = new BuildMenu();
		
		int pos = 90;
		MenuText QuitQuestion = new MenuText("Do you really", app.getFont(1), 160, pos, 1);
		MenuText QuitQuestion2 = new MenuText("want to reset keys?", app.getFont(1), 160, pos += 10, 1);
		MenuVariants QuitVariants = new MenuVariants(app.pEngine, "[Y/N]", app.getFont(1), 160, pos += 20) {
			@Override
			public void positive(MenuHandler menu) {
				mResetDefault(app.pCfg, menu);
			}
		};

		menu.addItem(QuitQuestion, false);
		menu.addItem(QuitQuestion2, false);
		menu.addItem(QuitVariants, true);

		return menu;
	}
	
	private BuildMenu getResetClassicMenu(final Main app)
	{
		BuildMenu menu = new BuildMenu();
		
		int pos = 90;
		MenuText QuitQuestion = new MenuText("Do you really want", app.getFont(1), 160, pos, 1);
		MenuText QuitQuestion2 = new MenuText("reset to classic?", app.getFont(1), 160, pos += 10, 1);
		MenuVariants QuitVariants = new MenuVariants(app.pEngine, "[Y/N]", app.getFont(1), 160, pos += 20) {
			@Override
			public void positive(MenuHandler menu) {
				mResetClassic(app.pCfg, menu);
			}
		};

		menu.addItem(QuitQuestion, false);
		menu.addItem(QuitQuestion2, false);
		menu.addItem(QuitVariants, true);

		return menu;
	}
}
