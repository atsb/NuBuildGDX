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

package ru.m210projects.Build.Pattern.CommonMenus;

import ru.m210projects.Build.Pattern.BuildFont;
import ru.m210projects.Build.Pattern.BuildGame;
import ru.m210projects.Build.Pattern.MenuItems.BuildMenu;
import ru.m210projects.Build.Pattern.MenuItems.MenuButton;
import ru.m210projects.Build.Pattern.MenuItems.MenuHandler;
import ru.m210projects.Build.Pattern.MenuItems.MenuText;
import ru.m210projects.Build.Pattern.MenuItems.MenuTitle;
import ru.m210projects.Build.Pattern.MenuItems.MenuVariants;
import ru.m210projects.Build.Settings.BuildConfig;

public abstract class MenuControls extends BuildMenu {
	
	protected BuildMenu ResetDefaultMenu;
	
	protected BuildMenu ResetClassicMenu;

	public abstract MenuTitle getTitle(BuildGame app, String text);
	
	public abstract BuildMenu getMouseMenu(BuildGame app);
	
	public abstract BuildMenu getJoystickMenu(BuildGame app);
	
	public abstract BuildMenu getKeyboardMenu(BuildGame app);
	
	public abstract void mResetDefault(BuildConfig cfg, MenuHandler menu);
	
	public abstract void mResetClassic(BuildConfig cfg, MenuHandler menu);
	
	public MenuControls(BuildGame app, int posy, int questionPos, int menuHeight, int separatorHeight, BuildFont style, int pal, int questionPal)
	{
		addItem(getTitle(app, "Controls setup"), false);

		MenuButton mMouse = new MenuButton("Mouse setup", style, 0, posy += menuHeight, 320, 1, pal, getMouseMenu(app), -1, null, 0);
		MenuButton mGamepad = new MenuButton("Joystick setup", style, 0, posy += menuHeight, 320, 1, pal, getJoystickMenu(app), -1, null, 0);
		MenuButton mKeyboard = new MenuButton("Keyboard setup", style, 0, posy += menuHeight, 320, 1, pal, getKeyboardMenu(app), -1, null, 0);
		
		posy += separatorHeight;
		
		ResetDefaultMenu = getResetDefaultMenu(app, style, questionPos, questionPal);
		ResetClassicMenu = getResetClassicMenu(app, style, questionPos, questionPal);
		
		MenuButton mKeyReset = new MenuButton("Reset Keys to default", style, 0, posy += menuHeight, 320, 1, pal, ResetDefaultMenu, -1, null, 0);
		MenuButton mKeyReset2 = new MenuButton("Reset Keys to classic", style, 0, posy + menuHeight, 320, 1, pal, ResetClassicMenu, -1, null, 0);
	
		addItem(mMouse, true);
		addItem(mGamepad, false);
		addItem(mKeyboard, false);
		addItem(mKeyReset, false);
		addItem(mKeyReset2, false);
	}
	
	protected BuildMenu getResetDefaultMenu(final BuildGame app, BuildFont style, int posy, int pal)
	{
		BuildMenu menu = new BuildMenu();
		
		MenuText QuitQuestion = new MenuText("Do you really want to reset keys?", style, 160, posy, 1);
		QuitQuestion.pal = pal;
		MenuVariants QuitVariants = new MenuVariants(app.pEngine, "[Y/N]", style, 160, posy += 2 * style.getHeight()) {
			@Override
			public void positive(MenuHandler menu) {
				mResetDefault(app.pCfg, menu);
			}
		};
		QuitVariants.pal = pal;
		
		menu.addItem(QuitQuestion, false);
		menu.addItem(QuitVariants, true);
		
		return menu;
	}
	
	protected BuildMenu getResetClassicMenu(final BuildGame app, BuildFont style, int posy, int pal)
	{
		BuildMenu menu = new BuildMenu();
		
		MenuText QuitQuestion = new MenuText("Do you really want reset to classic keys?", style, 160, posy, 1);
		QuitQuestion.pal = pal;
		MenuVariants QuitVariants = new MenuVariants(app.pEngine, "[Y/N]", style, 160, posy += 2 * style.getHeight()) {
			@Override
			public void positive(MenuHandler menu) {
				mResetClassic(app.pCfg, menu);
			}
		};
		QuitVariants.pal = pal;

		menu.addItem(QuitQuestion, false);
		menu.addItem(QuitVariants, true);
		
		return menu;
	}
}
