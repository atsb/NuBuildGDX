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

package ru.m210projects.Powerslave.Menus;

import static ru.m210projects.Build.Input.Keymap.MOUSE_LBUTTON;
import static ru.m210projects.Build.Input.Keymap.MOUSE_RBUTTON;
import static ru.m210projects.Build.Input.Keymap.MOUSE_WHELLDN;
import static ru.m210projects.Build.Input.Keymap.MOUSE_WHELLUP;

import java.util.Arrays;

import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Build.Pattern.MenuItems.BuildMenu;
import ru.m210projects.Build.Pattern.MenuItems.MenuButton;
import ru.m210projects.Build.Pattern.MenuItems.MenuHandler;
import ru.m210projects.Build.Pattern.MenuItems.MenuText;
import ru.m210projects.Build.Pattern.MenuItems.MenuTitle;
import ru.m210projects.Build.Pattern.MenuItems.MenuVariants;
import ru.m210projects.Build.Settings.BuildConfig;
import ru.m210projects.Build.Settings.BuildConfig.GameKeys;
import ru.m210projects.Powerslave.Config;
import ru.m210projects.Powerslave.Main;

public class MenuKeyboardSet extends BuildMenu {

	public MenuKeyboardSet(Main app)
	{
		MenuTitle mTitle = new PSTitle("Keyboard Setup", 160, 15, 0);

		addItem(mTitle, false);
		int pos = 60;

		MenuButton mKeySet = new PSButton("Configure Keys", 0, pos, 320, 1, 0, new PSMenuMenuKeyboard(app), -1, null, 0);
		MenuButton mKeyReset = new MenuButton("Reset Keys to default", app.getFont(0), 0, pos += 30, 320, 1, 0, getResetDefaultMenu(app), -1, null, 0);
		mKeyReset.fontShadow = true;
		MenuButton mKeyReset2 = new MenuButton("Reset Keys to classic", app.getFont(0), 0, pos += 15, 320, 1, 0, getResetClassicMenu(app), -1, null, 0);
		mKeyReset2.fontShadow = true;
		
		addItem(mKeySet, true);
		addItem(mKeyReset, false);
		addItem(mKeyReset2, false);
	}
	
	private void mResetDefault(BuildConfig cfg, MenuHandler menu) {
		for (int i = 0; i < cfg.keymap.length; i++)
			cfg.primarykeys[i] = Config.defkeys[i];

		Arrays.fill(cfg.secondkeys, 0);
		Arrays.fill(cfg.mousekeys, 0);

		cfg.mousekeys[GameKeys.Weapon_Fire.getNum()] = MOUSE_LBUTTON;
		cfg.mousekeys[GameKeys.Open.getNum()] = MOUSE_RBUTTON;
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
		cfg.mousekeys[GameKeys.Open.getNum()] = MOUSE_RBUTTON;
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
		MenuText QuitQuestion = new MenuText("Do you really", app.getFont(0), 160, pos, 1);
		QuitQuestion.fontShadow = true;
		MenuText QuitQuestion2 = new MenuText("want to reset keys?", app.getFont(0), 160, pos += 10, 1);
		QuitQuestion2.fontShadow = true;
		MenuVariants QuitVariants = new MenuVariants(app.pEngine, "[Y/N]", app.getFont(0), 160, pos += 20) {
			@Override
			public void positive(MenuHandler menu) {
				mResetDefault(app.pCfg, menu);
			}
		};
		QuitVariants.fontShadow = true;
		
		menu.addItem(QuitQuestion, false);
		menu.addItem(QuitQuestion2, false);
		menu.addItem(QuitVariants, true);

		return menu;
	}
	
	private BuildMenu getResetClassicMenu(final Main app)
	{
		BuildMenu menu = new BuildMenu();
		
		int pos = 90;
		MenuText QuitQuestion = new MenuText("Do you really want", app.getFont(0), 160, pos, 1);
		QuitQuestion.fontShadow = true;
		MenuText QuitQuestion2 = new MenuText("reset to classic?", app.getFont(0), 160, pos += 10, 1);
		QuitQuestion2.fontShadow = true;
		MenuVariants QuitVariants = new MenuVariants(app.pEngine, "[Y/N]", app.getFont(0), 160, pos += 20) {
			@Override
			public void positive(MenuHandler menu) {
				mResetClassic(app.pCfg, menu);
			}
		};
		QuitVariants.fontShadow = true;
		
		menu.addItem(QuitQuestion, false);
		menu.addItem(QuitQuestion2, false);
		menu.addItem(QuitVariants, true);
		
		return menu;
	}
}
