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

import static ru.m210projects.Blood.Globals.*;
import static ru.m210projects.Blood.Main.*;
import static ru.m210projects.Blood.LEVELS.*;
import static ru.m210projects.Build.Strhandler.toCharArray;
import static ru.m210projects.Blood.Factory.BloodMenuHandler.*;
import static ru.m210projects.Build.Net.Mmulti.*;
import static ru.m210projects.Build.Pattern.MenuItems.MenuTextField.*;
import static ru.m210projects.Build.Pattern.BuildNet.*;
import static ru.m210projects.Blood.VERSION.*;
import static ru.m210projects.Build.OnSceenDisplay.Console.*;

import ru.m210projects.Blood.Main;
import ru.m210projects.Blood.Factory.BloodMenuHandler;
import ru.m210projects.Blood.Types.BloodIniFile;
import ru.m210projects.Blood.Types.EpisodeInfo;
import ru.m210projects.Build.FileHandle.FileEntry;
import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Build.Pattern.MenuItems.BuildMenu;
import ru.m210projects.Build.Pattern.MenuItems.MenuButton;
import ru.m210projects.Build.Pattern.MenuItems.MenuConteiner;
import ru.m210projects.Build.Pattern.MenuItems.MenuHandler;
import ru.m210projects.Build.Pattern.MenuItems.MenuItem;
import ru.m210projects.Build.Pattern.MenuItems.MenuProc;
import ru.m210projects.Build.Pattern.MenuItems.MenuSwitch;
import ru.m210projects.Build.Pattern.MenuItems.MenuTextField;
import ru.m210projects.Build.Pattern.MenuItems.MenuTitle;
import ru.m210projects.Build.Types.LittleEndian;

public class MenuNetwork extends BuildMenu {
	
	private int mGameType = 1;
	private int mEpisodeId = 0;
	private int mLevelId = 0;
	private int mDifficulty = 2;
	private int mMonsters = 0;
	private int mWeapons = 1;
	private int mItems = 1;
	private short mFFire = 0;
	private final short[] mFfireShift = { 0, 32, 64, 128, 256 };
	private boolean mRevive = true;
	private int mFragLimit = 0;
	private Object currentFile;
	private int mTeam = 0;

	private String mContent;
	private final EpisodeInfo[] netEpisodeInfo = new EpisodeInfo[kMaxEpisode];
	
	private final Main app;
	private final MenuProc mLevelsUpdate;
	private final MenuConteiner mMenuLevel;
	private final MenuConteiner mMenuEpisode;
	
	public MenuNetwork(final Main app)
	{
		this.app = app;
		final BloodMenuHandler menu = app.menu;
		addItem(new MenuTitle(app.pEngine, "NETWORK GAME", app.getFont(1), 160, 20, 2038), false);

		MenuConteiner mMenuGame = new MenuConteiner("GAME", app.getFont(1), 20, 35, 280, null, 0, new MenuProc() {
			@Override
			public void run( MenuHandler handler, MenuItem pItem ) {
				MenuConteiner item = (MenuConteiner) pItem;
				mGameType = item.num;
			}
		}) {

			@Override
			public void open() {
				if (this.list == null) {
					this.list = new char[3][];
					this.list[0] = "Cooperative".toCharArray();
					this.list[1] = "Bloodbath".toCharArray();
					this.list[2] = "Teams".toCharArray();
				}
				num = mGameType = pNetInfo.nGameType - 1;
			}
		};

		for (int i = 0; i < kMaxEpisode; i++)
			netEpisodeInfo[i] = new EpisodeInfo();

		mLevelsUpdate = new MenuProc() {
			@Override
			public void run( MenuHandler handler, MenuItem pItem ) {
				MenuConteiner item = (MenuConteiner) pItem;
				EpisodeInfo pEpisode = netEpisodeInfo[mEpisodeId];
				if (item.list == null || item.list.length != pEpisode.nMaps)
					item.list = new char[pEpisode.nMaps][];
				for (int i = 0; i < pEpisode.nMaps; i++)
					if (pEpisode.gMapInfo[i].Title != null)
						item.list[i] = pEpisode.gMapInfo[i].Title.toCharArray();
				mLevelId = item.num = 0;
			}
		};

		mMenuLevel = new MenuConteiner("LEVEL", app.getFont(3), 20, 80, 280, null, 0, new MenuProc() {
			@Override
			public void run( MenuHandler handler, MenuItem pItem ) {
				MenuConteiner item = (MenuConteiner) pItem;
				mLevelId = item.num;
			}
		}) {

			@Override
			public void open() {
				num = mLevelId;
			}

			@Override
			public void draw(MenuHandler handler) {
				mCheckEnableItem(currentFile instanceof BloodIniFile);
				super.draw(handler);
			}
		};

		mMenuEpisode = new MenuConteiner("EPISODE", app.getFont(3), 20, 70, 280, null, 0, new MenuProc() {
			@Override
			public void run( MenuHandler handler, MenuItem pItem ) {
				MenuConteiner item = (MenuConteiner) pItem;
				mEpisodeId = item.num;
				mLevelsUpdate.run(menu, mMenuLevel);
			}
		}) {

			@Override
			public void open() {
				if (this.list == null) {
					this.list = new char[nEpisodeCount][];
					for (int i = 0; i < nEpisodeCount; i++) {
						EpisodeInfo pEpisode = netEpisodeInfo[i];
						if (pEpisode.Title != null) {
							this.list[i] = pEpisode.Title.toCharArray();
						}
					}
				}
				num = mEpisodeId;
			}

			@Override
			public void draw(MenuHandler handler) {
				mCheckEnableItem(currentFile instanceof BloodIniFile);
				super.draw(handler);
			}
		};

		final MenuConteiner pItem = new MenuConteiner("Content", app.getFont(1), 20,
				50, 280, new String[] { "" }, 0, new MenuProc() {
					@Override
					public void run( MenuHandler handler, MenuItem pItem ) {
						BLUserContent usercont = (BLUserContent) menu.mMenus[USERCONTENT];
						if(!usercont.showmain) 
							usercont.setShowMain(true);
						handler.mOpen(usercont, -1);
					}
				}) {
			
			@Override
			public void open() {
				if(!app.isCurrentScreen(gGameScreen))
					setEpisode(MainINI);
			}

			@Override
			public void draw(MenuHandler handler) {
				this.list[0] = toCharArray(mContent);
				super.draw(handler);
			}
		};

		int pos = 80;

		MenuConteiner mMenuDifficulty = new MenuConteiner("DIFFICULTY", app.getFont(3), 20, pos += 10, 280, null, 0, new MenuProc() {
			@Override
			public void run( MenuHandler handler, MenuItem pItem ) {
				MenuConteiner item = (MenuConteiner) pItem;
				mDifficulty = item.num;
			}
		}) {

			@Override
			public void open() {
				if (this.list == null) {
					this.list = new char[5][];
					this.list[0] = "STILL KICKING".toCharArray();
					this.list[1] = "PINK ON THE INSIDE".toCharArray();
					this.list[2] = "LIGHTLY BROILED".toCharArray();
					this.list[3] = "WELL DONE".toCharArray();
					this.list[4] = "EXTRA CRISPY".toCharArray();
				}
				num = mDifficulty;
			}
		};

		MenuConteiner mMenuMonsters = new MenuConteiner("MONSTERS", app.getFont(3), 20, pos += 10, 280, null, 0, new MenuProc() {
			@Override
			public void run( MenuHandler handler, MenuItem pItem ) {
				MenuConteiner item = (MenuConteiner) pItem;
				mMonsters = item.num;
			}
		}) {
			@Override
			public void open() {
				if (this.list == null) {
					this.list = new char[3][];
					this.list[0] = "None".toCharArray();
					this.list[1] = "Bring'em on".toCharArray();
					this.list[2] = "Respawn".toCharArray();
				}
				num = mMonsters;
			}
		};

		MenuConteiner mMenuWeapons = new MenuConteiner("WEAPONS", app.getFont(3), 20, pos += 10, 280, null, 0, new MenuProc() {
			@Override
			public void run( MenuHandler handler, MenuItem pItem ) {
				MenuConteiner item = (MenuConteiner) pItem;
				mWeapons = item.num;
			}
		}) {
			@Override
			public void open() {
				if (this.list == null) {
					this.list = new char[4][];
					this.list[0] = "Do not respawn".toCharArray();
					this.list[1] = "Are permanent".toCharArray();
					this.list[2] = "Respawn".toCharArray();
					this.list[3] = "Respawn with markers".toCharArray();
				}
				num = mWeapons;
			}
		};

		MenuConteiner mMenuItems = new MenuConteiner("Items", app.getFont(3), 20, pos += 10, 280, null, 0, new MenuProc() {
			@Override
			public void run( MenuHandler handler, MenuItem pItem ) {
				MenuConteiner item = (MenuConteiner) pItem;
				mItems = item.num;
			}
		}) {
			@Override
			public void open() {
				if (this.list == null) {
					this.list = new char[3][];
					this.list[0] = "Do not respawn".toCharArray();
					this.list[1] = "Respawn".toCharArray();
					this.list[2] = "Respawn with markers".toCharArray();
				}
				num = mItems;
			}
		};

		MenuConteiner mMenuFFire = new MenuConteiner("Friendly Fire", app.getFont(3), 20, pos += 10, 280, null, 0, new MenuProc() {
			@Override
			public void run( MenuHandler handler, MenuItem pItem ) {
				MenuConteiner item = (MenuConteiner) pItem;
				mFFire = (short) item.num;
			}
		}) {

			@Override
			public void open() {
				if (this.list == null) {
					this.list = new char[5][];
					this.list[0] = "No damage".toCharArray();
					this.list[1] = "1/8 damage".toCharArray();
					this.list[2] = "1/4 damage".toCharArray();
					this.list[3] = "1/2 damage".toCharArray();
					this.list[4] = "Full damage".toCharArray();
				}
				num = mFFire;
			}
			
			@Override
			public void draw(MenuHandler handler) {
				mCheckEnableItem((mGameType + 1) == kNetModeCoop || (mGameType + 1) == kNetModeTeams);
				super.draw(handler);
			}
		};
		
		MenuSwitch mMenuRevive = new MenuSwitch("Revive mode:", app.getFont(3), 20, pos += 10, 280, mRevive, new MenuProc() {
			@Override
			public void run( MenuHandler handler, MenuItem pItem ) {
				MenuSwitch sw = (MenuSwitch) pItem;
				mRevive = sw.value;
			}
		}, "Yes", "No") {
			
			@Override
			public void draw(MenuHandler handler) {
				mCheckEnableItem((mGameType + 1) == kNetModeCoop);
				super.draw(handler);
			}
		};
		
		MenuConteiner mMenuTeam = new MenuConteiner("Team:", app.getFont(3), 20, pos += 10, 280, null, 0, new MenuProc() {
			@Override
			public void run( MenuHandler handler, MenuItem pItem ) {
				MenuConteiner item = (MenuConteiner) pItem;
				mTeam = (short) item.num;
				app.net.ChangeTeam(myconnectindex, mTeam);
			}
		}) {

			@Override
			public void open() {
				if (this.list == null) {
					this.list = new char[3][];
					this.list[0] = "Auto".toCharArray();
					this.list[1] = "Blue".toCharArray();
					this.list[2] = "Red".toCharArray();
				}
				num = mTeam;
			}
			
			@Override
			public void draw(MenuHandler handler) {
				mCheckEnableItem((mGameType + 1) == kNetModeTeams);
				super.draw(handler);
			}
		};
		
		MenuTextField mLimit = new MenuTextField("Frag limit:", "" + mFragLimit, app.getFont(3), 20, pos += 10, 280,
				NUMBERS, new MenuProc() {
					@Override
					public void run( MenuHandler handler, MenuItem pItem ) {
						MenuTextField item = (MenuTextField) pItem;
						if(item.typed.length() < 8) {
							mFragLimit = Integer.parseInt(item.typed);
						}
						else {
							System.arraycopy(item.otypingBuf, 0, item.typingBuf, 0, 16);
							item.inputlen = item.oinputlen;
						}
					}
				}) {
			@Override
			public void draw(MenuHandler handler) {
				mCheckEnableItem((mGameType + 1) >= kNetModeBloodBath);
				super.draw(handler);
			}
		};
		
		final MenuProc mNetStart = new MenuProc() {
			@Override
			public void run( MenuHandler handler, MenuItem pItem ) {

				pNetInfo.nGameType = mGameType + 1;
				pNetInfo.nEpisode = mEpisodeId;
				pNetInfo.nLevel = mLevelId;
				pNetInfo.nDifficulty = mDifficulty;
				pNetInfo.nMonsterSettings = mMonsters;
				pNetInfo.nWeaponSettings = mWeapons;
				pNetInfo.nItemSettings = mItems;
				pNetInfo.nFriendlyFire = mFfireShift[mFFire];
				pNetInfo.nReviveMode = mRevive;
				pNetInfo.nFragLimit = mFragLimit;

				System.err.println("nNetType " + pNetInfo.nGameType);
				System.err.println("nNetDifficulty " + pNetInfo.nDifficulty);
				System.err.println("nNetMonsters " + pNetInfo.nMonsterSettings);
				System.err.println("nNetWeapons " + pNetInfo.nWeaponSettings);
				System.err.println("nNetItems " + pNetInfo.nItemSettings);
				System.err.println("nNetFFire " + pNetInfo.nFriendlyFire);
				System.err.println("nNetRevive " + pNetInfo.nReviveMode);
				System.err.println("nNetFragLimit " + pNetInfo.nFragLimit);

				if (numplayers >= 2) {
					byte[] packbuf = app.net.packbuf;
					packbuf[0] = kPacketLevelStart;
					int ptr = 1;

					LittleEndian.putInt(packbuf, ptr, myconnectindex); ptr += 4;
					LittleEndian.putInt(packbuf, ptr, app.net.nNetVersion); ptr += 4;
					LittleEndian.putByte(packbuf, ptr, GAMEVER); ptr++;
					
					System.arraycopy( pNetInfo.getBytes(), 0, packbuf, ptr, pNetInfo.sizeof );
					ptr += pNetInfo.sizeof;

					for (int i = connecthead; i >= 0; i = connectpoint2[i]) {
						if (i != myconnectindex)
							sendpacket(i, packbuf, ptr);
						if ((myconnectindex != connecthead))
							break; // slaves in M/S mode only send to master
					}
				}

				if (app.net.WaitForAllPlayers(0)) 
					gGameScreen.newgame(true, currentFile, mEpisodeId, mLevelId, mDifficulty, mDifficulty, mDifficulty, false);
			}
		};

		MenuButton mStart = new MenuButton("START GAME", app.getFont(1), 20, pos += 15, 280, 0, 0, null, -1, mNetStart, 0) {
			@Override
			public void draw(MenuHandler handler) {
				mCheckEnableItem(myconnectindex == connecthead && currentFile != null);
				super.draw(handler);
			}
		};
		mStart.fontShadow = true;

		addItem(mMenuGame, true);
		addItem(pItem, false);
		addItem(mMenuEpisode, false);
		addItem(mMenuLevel, false);
		addItem(mMenuDifficulty, false);
		addItem(mMenuMonsters, false);
		addItem(mMenuWeapons, false);
		addItem(mMenuItems, false);
		addItem(mMenuFFire, false);
		addItem(mMenuRevive, false);
		addItem(mMenuTeam, false);
		addItem(mLimit, false);
		addItem(mStart, false);

		addItem(menu.addMenuBlood(), false);
	}
	
	public Object getFile()
	{
		return currentFile;
	}

	public void setEpisode(BloodIniFile ini)
	{
		if(ini == null || currentFile == ini)
			return;
		
		String path = ini.getFile().getPath();
		if(ini.isPackage())
			path += ":" + ini.getName();

		if(myconnectindex == connecthead && !app.net.WaitForContentCheck(path, 0))
		{
			StringBuilder msg = new StringBuilder();
			for(int i=connecthead;i>=0;i=connectpoint2[i])
			{
				if(app.net.gContentFound[i] == 0) {
					msg.append(app.net.getPlayerName(i)).append(", ");
				}
			}
			
			msg = new StringBuilder(msg.substring(0, msg.length() - 2));
			msg.append(" haven't ").append(ini.getName());
			
			Console.Println(msg.toString(), OSDTEXT_RED);
			if(!Console.IsShown())
				Console.toggle();
			
			currentFile = null;
			return;
		}
		
		currentFile = ini;
		mContent = ini.getName();
		mEpisodeId = 0;
		mMenuEpisode.num = 0;
		
		int nEpisodes = getEpisodeInfo(netEpisodeInfo, ini);
		mLevelsUpdate.run(app.pMenu, mMenuLevel);

		if (mMenuEpisode.list == null || mMenuEpisode.list.length != nEpisodes)
			mMenuEpisode.list = new char[nEpisodes][];

		for (int i = 0; i < nEpisodes; i++) {
			EpisodeInfo pEpisode = netEpisodeInfo[i];
			if (pEpisode.Title != null) {
				mMenuEpisode.list[i] = pEpisode.Title.toCharArray();
			}
		}
	}
	
	public void setMap(FileEntry map)
	{
		if(map == null || currentFile == map)
			return;
		
		if(myconnectindex == connecthead && !app.net.WaitForContentCheck(map.getPath(), 0))
		{
			StringBuilder msg = new StringBuilder();
			for(int i=connecthead;i>=0;i=connectpoint2[i])
			{
				if(app.net.gContentFound[i] == 0) {
					msg.append(app.net.getPlayerName(i)).append(", ");
				}
			}
			
			msg = new StringBuilder(msg.substring(0, msg.length() - 2));
			msg.append(" haven't ").append(map.getName());
			
			Console.Println(msg.toString(), OSDTEXT_RED);
			if(!Console.IsShown())
				Console.toggle();
			
			currentFile = null;
			return;
		}
		
		currentFile = map;
		mContent = map.getName();
		
		if(mMenuEpisode.list != null)
			mMenuEpisode.list[0] = "None".toCharArray();
		mEpisodeId = mMenuEpisode.num = 0;
		if(mMenuLevel.list != null)
			mMenuLevel.list[0] = "Usermap".toCharArray();
		mLevelId = mMenuLevel.num = 0;
	}
	
}
