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

package ru.m210projects.Blood.Menus;

import static ru.m210projects.Blood.Main.*;

import ru.m210projects.Blood.Main;
import ru.m210projects.Blood.Types.BloodIniFile;
import ru.m210projects.Build.FileHandle.FileEntry;
import ru.m210projects.Build.Pattern.MenuItems.BuildMenu;
import ru.m210projects.Build.Pattern.MenuItems.MenuButton;
import ru.m210projects.Build.Pattern.MenuItems.MenuHandler;
import ru.m210projects.Build.Pattern.MenuItems.MenuItem;
import ru.m210projects.Build.Pattern.MenuItems.MenuProc;
import ru.m210projects.Build.Pattern.MenuItems.MenuSlider;
import ru.m210projects.Build.Pattern.MenuItems.MenuSwitch;
import ru.m210projects.Build.Pattern.MenuItems.MenuTitle;

public class MenuCustom extends BuildMenu {

	private BloodIniFile ini;
	private FileEntry map;
	private int episodeNum;
	private int nEnemyQuantity = 2, nGlobalDifficulty = 2, nEnemyDamage = 2;
	private boolean nPitchforkOnly = false;

	public MenuCustom(Main app)
	{
		MenuTitle mTitle = new MenuTitle(app.pEngine, "Custom", app.getFont(1), 160, 20, 2038);

		int pos = 40;
		MenuSlider enemyQ = new MenuSlider(app.pSlider, "Enemies quantity:", app.getFont(3), 66, pos += 10, 180, nEnemyQuantity, 0, 4, 1,
				new MenuProc() {
					@Override
					public void run( MenuHandler handler, MenuItem pItem ) {
						MenuSlider slider = (MenuSlider) pItem;
						nEnemyQuantity = slider.value;
					}
				}, false);
		MenuSlider enemyDamage = new MenuSlider(app.pSlider,"Enemies health:", app.getFont(3), 66, pos += 10, 180, nEnemyDamage, 0, 4, 1,
				new MenuProc() {
					@Override
					public void run( MenuHandler handler, MenuItem pItem ) {
						MenuSlider slider = (MenuSlider) pItem;
						nEnemyDamage = slider.value;
					}
				}, false);

		MenuSlider playerDamage = new MenuSlider(app.pSlider,"Enemies damage:", app.getFont(3), 66, pos += 10, 180, nGlobalDifficulty, 0,
				4, 1, new MenuProc() {
					@Override
					public void run( MenuHandler handler, MenuItem pItem ) {
						MenuSlider slider = (MenuSlider) pItem;
						nGlobalDifficulty = slider.value;
					}
				}, false);

		MenuSwitch nWeapon = new MenuSwitch("Pitchfork start:", app.getFont(3), 66, pos += 10, 180, nPitchforkOnly,
				new MenuProc() {
					@Override
					public void run( MenuHandler handler, MenuItem pItem ) {
						MenuSwitch sw = (MenuSwitch) pItem;
						nPitchforkOnly = sw.value;
					}
				}, null, null);

		MenuProc newGameProc = new MenuProc() {
			@Override
			public void run( MenuHandler handler, MenuItem pItem ) {
				gGameScreen.newgame(false, map != null ? map : ini, episodeNum, 0, nGlobalDifficulty, nEnemyQuantity, nEnemyDamage, nPitchforkOnly);
			}
		};

		pos += 10;
		MenuButton mStart = new MenuButton("Start game", app.getFont(1), 66, pos += 10, 180, 1, 0, null, -1, newGameProc, 0);
		mStart.fontShadow = true;
		
		addItem(mTitle, false);
		addItem(enemyQ, true);
		addItem(enemyDamage, false);
		addItem(playerDamage, false);
		addItem(nWeapon, false);
		addItem(mStart, false);
		addItem(app.menu.addMenuBlood(), false);
	}
	
	public void setEpisode(BloodIniFile ini, int episodeNum)
	{
		this.map = null;
		this.ini = ini;
		this.episodeNum = episodeNum; 
	}
	
	public void setMap(FileEntry map)
	{
		this.map = map;
		this.ini = null;
		this.episodeNum = -1; 
	}
}
