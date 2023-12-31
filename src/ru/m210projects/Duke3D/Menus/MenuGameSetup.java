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

import static ru.m210projects.Duke3D.Main.*;
import static ru.m210projects.Duke3D.Globals.*;
import static ru.m210projects.Duke3D.Sounds.*;
import static ru.m210projects.Build.Net.Mmulti.*;

import ru.m210projects.Build.Pattern.MenuItems.BuildMenu;
import ru.m210projects.Build.Pattern.MenuItems.MenuConteiner;
import ru.m210projects.Build.Pattern.MenuItems.MenuHandler;
import ru.m210projects.Build.Pattern.MenuItems.MenuItem;
import ru.m210projects.Build.Pattern.MenuItems.MenuProc;
import ru.m210projects.Build.Pattern.MenuItems.MenuSwitch;
import ru.m210projects.Duke3D.Main;

public class MenuGameSetup extends BuildMenu {

	public MenuGameSetup(final Main app)
	{
		DukeTitle mTitle = new DukeTitle("Game Setup");
		int pos = 40;

		MenuSwitch sSlopeTilt = new MenuSwitch("SCREEN TILTING:", app.getFont(1), 46, pos += 10, 240, ud.screen_tilting==1, new MenuProc() {
			@Override
			public void run( MenuHandler handler, MenuItem pItem ) {
				MenuSwitch sw = (MenuSwitch) pItem;
				ud.screen_tilting = sw.value?1:0;
			}
		}, null, null);

		MenuSwitch sAutoAim = new MenuSwitch("AutoAim:", app.getFont(1), 46, pos += 10, 240, cfg.gAutoAim, new MenuProc() {
			@Override
			public void run( MenuHandler handler, MenuItem pItem ) {
				MenuSwitch sw = (MenuSwitch) pItem;
				cfg.gAutoAim = sw.value;
				ps[myconnectindex].auto_aim = cfg.gAutoAim?1:0;
				if(numplayers > 1) app.net.getnames();
			}
		}, null, null);
		
		MenuSwitch sDevComments = null;
		MenuSwitch sLegacyDuke = null;
		if(currentGame.getCON().type == 20) // Twentieth Anniversary World Tour
		{
			sDevComments = new MenuSwitch("Developer commentary:", app.getFont(1), 46, pos += 10, 240, cfg.bDevCommentry, new MenuProc() {
				@Override
				public void run( MenuHandler handler, MenuItem pItem ) {
					MenuSwitch sw = (MenuSwitch) pItem;
					cfg.bDevCommentry = sw.value;
				}
			}, null, null);

			sLegacyDuke = new MenuSwitch("Legacy Duke Talk:", app.getFont(1), 46, pos += 10, 240, cfg.bLegacyDukeTalk, new MenuProc() {
				@Override
				public void run( MenuHandler handler, MenuItem pItem ) {
					MenuSwitch sw = (MenuSwitch) pItem;
					cfg.bLegacyDukeTalk = sw.value;

					for(int i = 0; i < NUM_SOUNDS; i++)
						if ((currentGame.getCON().soundm[i] & 4) != 0)
							Sound[i].ptr = null; //force to reload all duke talk
				}
			}, null, null);
		}

		MenuSwitch sStartup = new MenuSwitch("Startup window:", app.getFont(1), 46, pos += 10, 240, cfg.startup, new MenuProc() {
			@Override
			public void run( MenuHandler handler, MenuItem pItem ) {
				MenuSwitch sw = (MenuSwitch) pItem;
				cfg.startup = sw.value;
			}
		}, null, null);

		MenuConteiner mPlayingDemo = new MenuConteiner("Demos playback:", app.getFont(1), 46, pos += 10, 240, null, 0,
				new MenuProc() {
					@Override
					public void run( MenuHandler handler, MenuItem pItem ) {
						MenuConteiner item = (MenuConteiner) pItem;
						cfg.gDemoSeq = item.num;
					}
				}) {
			@Override
			public void open() {
				if (this.list == null) {
					this.list = new char[3][];
					this.list[0] = "Off".toCharArray();
					this.list[1] = "In order".toCharArray();
					this.list[2] = "Randomly".toCharArray();
				}
				num = cfg.gDemoSeq;
			}
		};
		
		MenuSwitch sRecord = new MenuSwitch("Record demo:", app.getFont(1), 46, pos += 10, 240, ud.m_recstat == 1,
		new MenuProc() {
			@Override
			public void run( MenuHandler handler, MenuItem pItem ) {
				MenuSwitch sw = (MenuSwitch) pItem;
				ud.m_recstat = sw.value?1:0;
			}
		}, null, null) {
			
			@Override
			public void open() {
				value = ud.m_recstat == 1;
				mCheckEnableItem(!app.isCurrentScreen(gGameScreen));
			}
		};

		addItem(mTitle, false);
		addItem(sSlopeTilt, true);
		addItem(sAutoAim, false);
		if(sDevComments != null) {
			addItem(sDevComments, false);
			addItem(sLegacyDuke, false);
		}
		addItem(sStartup, false);
		addItem(mPlayingDemo, false);
		addItem(sRecord, false);
	}
}
