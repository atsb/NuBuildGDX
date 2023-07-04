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

import static ru.m210projects.Build.Settings.BuildConfig.*;

import ru.m210projects.Build.Pattern.BuildFont;
import ru.m210projects.Build.Pattern.BuildGame;
import ru.m210projects.Build.Pattern.MenuItems.BuildMenu;
import ru.m210projects.Build.Pattern.MenuItems.MenuButton;
import ru.m210projects.Build.Pattern.MenuItems.MenuConteiner;
import ru.m210projects.Build.Pattern.MenuItems.MenuHandler;
import ru.m210projects.Build.Pattern.MenuItems.MenuItem;
import ru.m210projects.Build.Pattern.MenuItems.MenuProc;
import ru.m210projects.Build.Pattern.MenuItems.MenuSlider;
import ru.m210projects.Build.Pattern.MenuItems.MenuSwitch;
import ru.m210projects.Build.Pattern.MenuItems.MenuTitle;
import ru.m210projects.Build.Pattern.MenuItems.MenuHandler.MenuOpt;
import ru.m210projects.Build.Settings.BuildConfig;

public abstract class MenuMouse extends BuildMenu {
	
	public BuildMenu advancedMenu;
	
	public MenuSwitch mEnable;
	public MenuSwitch mMenuEnab;
	public MenuSlider mSens;
	public MenuSlider mTurn;
	public MenuSlider mLook;
	public MenuSlider mMove;
	public MenuSlider mStrafe;
	public MenuSwitch mAiming;
	public MenuSwitch mInvert;
	public MenuButton mAdvance;
	
	public MenuConteiner mAxisUp;
	public MenuConteiner mAxisDown;
	public MenuConteiner mAxisLeft;
	public MenuConteiner mAxisRight;

	public abstract MenuTitle getTitle(BuildGame app, String text);

	public MenuMouse(final BuildGame app, int posx, int posy, int width, int menuHeight, int separatorHeight, BuildFont style, int buttonPal)
	{
		addItem(getTitle(app, "Mouse setup"), false);
		
		final BuildConfig cfg = app.pCfg;
		
		advancedMenu = buildAdvancedAxisMenu(app, posx, posy, width, menuHeight, style);
		
		mEnable = new MenuSwitch("Mouse in game", style, posx, posy += menuHeight, width, cfg.useMouse, new MenuProc() {
			@Override
			public void run(MenuHandler handler, MenuItem pItem) {
				MenuSwitch sw = (MenuSwitch) pItem;
				cfg.useMouse = sw.value;
			}
		}, "Yes", "No");
		mEnable.pal = buttonPal;

		mMenuEnab = new MenuSwitch("Mouse in menu", style, posx, posy += menuHeight, width, cfg.menuMouse,
				new MenuProc() {
					@Override
					public void run(MenuHandler handler, MenuItem pItem) {
						MenuSwitch sw = (MenuSwitch) pItem;
						cfg.menuMouse = sw.value;
					}
				}, "Yes", "No");
		mMenuEnab.pal = buttonPal;
		
		posy += separatorHeight;
		mSens = new MenuSlider(app.pSlider, "Mouse Sensitivity", style, posx, posy += menuHeight, width, cfg.gSensitivity, 0x1000,
				0x28000, 4096, new MenuProc() {
					@Override
					public void run(MenuHandler handler, MenuItem pItem) {
						MenuSlider slider = (MenuSlider) pItem;
						cfg.gSensitivity = slider.value;
					}
				}, true);
		mSens.digitalMax = 65536f;
		mSens.pal = buttonPal;

		mTurn = new MenuSlider(app.pSlider, "Turning speed", style, posx, posy += menuHeight, width, cfg.gMouseTurnSpeed, 0,
				0x28000, 4096, new MenuProc() {
					@Override
					public void run(MenuHandler handler, MenuItem pItem) {
						MenuSlider slider = (MenuSlider) pItem;
						cfg.gMouseTurnSpeed = slider.value;
					}
				}, true);
		mTurn.digitalMax = 65536f;
		mTurn.pal = buttonPal;

		mLook = new MenuSlider(app.pSlider, "Aiming up/down speed", style, posx, posy += menuHeight, width, cfg.gMouseLookSpeed, 0,
				0x28000, 4096, new MenuProc() {
					@Override
					public void run(MenuHandler handler, MenuItem pItem) {
						MenuSlider slider = (MenuSlider) pItem;
						cfg.gMouseLookSpeed = slider.value;
					}
		}, true);
		mLook.digitalMax = 65536f;
		mLook.pal = buttonPal;

		mMove = new MenuSlider(app.pSlider, "Forward/Backward speed", style, posx, posy += menuHeight, width, cfg.gMouseMoveSpeed,
				0, 0x28000, 4096, new MenuProc() {
					@Override
					public void run(MenuHandler handler, MenuItem pItem) {
						MenuSlider slider = (MenuSlider) pItem;
						cfg.gMouseMoveSpeed = slider.value;
					}
		}, true);
		mMove.digitalMax = 65536f;
		mMove.pal = buttonPal;

		mStrafe = new MenuSlider(app.pSlider, "Strafing speed", style, posx, posy += menuHeight, width, cfg.gMouseStrafeSpeed, 0,
				0x28000, 4096, new MenuProc() {
					@Override
					public void run(MenuHandler handler, MenuItem pItem) {
						MenuSlider slider = (MenuSlider) pItem;
						cfg.gMouseStrafeSpeed = slider.value;
					}
		}, true);
		mStrafe.digitalMax = 65536f;
		mStrafe.pal = buttonPal;

		posy += separatorHeight;

		mAiming = new MenuSwitch("Mouse aiming", style, posx, posy += menuHeight, width, cfg.gMouseAim, new MenuProc() {
			@Override
			public void run(MenuHandler handler, MenuItem pItem) {
				MenuSwitch sw = (MenuSwitch) pItem;
				cfg.gMouseAim = sw.value;
			}
		}, null, null);
		mAiming.pal = buttonPal;
		
		mInvert = new MenuSwitch("Invert mouse aim", style, posx, posy += menuHeight, width, cfg.gInvertmouse,
				new MenuProc() {
					@Override
					public void run(MenuHandler handler, MenuItem pItem) {
						MenuSwitch sw = (MenuSwitch) pItem;
						cfg.gInvertmouse = sw.value;
					}
				}, null, null);
		mInvert.pal = buttonPal;

		posy += separatorHeight;

		mAdvance = new MenuButton("Digital axis setup", style, posx, posy + menuHeight, width, 1, buttonPal, advancedMenu, -1, null, 0);
		
		addItem(mEnable, true);
		addItem(mMenuEnab, false);
		addItem(mSens, false);

		addItem(mTurn, false);
		addItem(mLook, false);
		addItem(mMove, false);
		addItem(mStrafe, false);

		addItem(mAiming, false);
		addItem(mInvert, false);
		addItem(mAdvance, false);
	}
	
	protected BuildMenu buildAdvancedAxisMenu(BuildGame app, int posx, int posy, int width, int menuHeight, BuildFont style) {
		BuildMenu advancedMenu = new BuildMenu();
		
		advancedMenu.addItem(getTitle(app, "Digital axis"), false);
		final BuildConfig cfg = app.pCfg;

		char[][] keymaplist = new char[cfg.keymap.length + 1][];
		keymaplist[0] = "None".toCharArray();
		for (int i = 1; i < keymaplist.length; i++)
			keymaplist[i] = cfg.keymap[i - 1].getName().toCharArray();

		mAxisUp = new MenuConteiner("Digital up", style, posx, posy += 10, width, null, 0, null) {
			@Override
			public void open() {
				num = cfg.mouseaxis[AXISUP] + 1;
			}
			
			@Override
			public boolean callback(MenuHandler handler, MenuOpt opt) {
				return mAdvancedCallback(handler, cfg, this, opt, AXISUP);
			}
		};
		
		mAxisDown = new MenuConteiner("Digital down", style, posx, posy += 10, width, null, 0, null) {
			@Override
			public void open() {
				num = cfg.mouseaxis[AXISDOWN] + 1;
			}
			
			@Override
			public boolean callback(MenuHandler handler, MenuOpt opt) {
				return mAdvancedCallback(handler, cfg, this, opt, AXISDOWN);
			}
		};
		
		mAxisLeft = new MenuConteiner("Digital left", style, posx, posy += 10, width, null, 0, null) {
			@Override
			public void open() {
				num = cfg.mouseaxis[AXISLEFT] + 1;
			}
			
			@Override
			public boolean callback(MenuHandler handler, MenuOpt opt) {
				return mAdvancedCallback(handler, cfg, this, opt, AXISLEFT);
			}
		};
		
		mAxisRight = new MenuConteiner("Digital right", style, posx, posy += 10, width, null, 0, null) {
			@Override
			public void open() {
				num = cfg.mouseaxis[AXISRIGHT] + 1;
			}
			
			@Override
			public boolean callback(MenuHandler handler, MenuOpt opt) {
				return mAdvancedCallback(handler, cfg, this, opt, AXISRIGHT);
			}
		};
		
		mAxisUp.list = mAxisDown.list = mAxisLeft.list = mAxisRight.list = keymaplist;
		
		advancedMenu.addItem(mAxisUp, true);
		advancedMenu.addItem(mAxisDown, false);
		advancedMenu.addItem(mAxisLeft, false);
		advancedMenu.addItem(mAxisRight, false);
		
		return advancedMenu;
	}
	
	private boolean mAdvancedCallback(MenuHandler handler, BuildConfig cfg, MenuConteiner item, MenuOpt opt, int nAxis) {
		switch(opt)
		{
		case LEFT:
		case MWDW:
			if ( (item.flags & 4) == 0 ) return false;
			if(item.num > 0) item.num--;
			else item.num = 0;
			cfg.mouseaxis[nAxis] = item.num - 1;
			return false;
		case RIGHT:
		case MWUP:
			if ( (item.flags & 4) == 0 ) return false;
			if(item.num < item.list.length - 1) item.num++;
			else item.num = item.list.length - 1;
			cfg.mouseaxis[nAxis] = item.num - 1;
			return false;
		case ENTER:
		case LMB:
			if ( (item.flags & 4) == 0 ) return false;
			if(item.num < item.list.length - 1) {
				item.num++;
			} else item.num = 0;
			cfg.mouseaxis[nAxis] = item.num - 1;
			return false;
		default:
			return item.m_pMenu.mNavigation(opt);
		}
	}
}
