// This file is part of RedneckGDX.
// Copyright (C) 2017-2019  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// RedneckGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// RedneckGDX is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with RedneckGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.Redneck.Menus;

import static ru.m210projects.Build.Net.Mmulti.connecthead;
import static ru.m210projects.Build.Net.Mmulti.connectpoint2;
import static ru.m210projects.Build.Net.Mmulti.myconnectindex;
import static ru.m210projects.Build.Net.Mmulti.numplayers;
import static ru.m210projects.Build.Net.Mmulti.sendpacket;
import static ru.m210projects.Build.OnSceenDisplay.Console.OSDTEXT_RED;
import static ru.m210projects.Build.Pattern.BuildNet.kPacketLevelStart;
import static ru.m210projects.Build.Strhandler.toCharArray;
import static ru.m210projects.Redneck.Main.*;
import static ru.m210projects.Redneck.Sounds.*;
import static ru.m210projects.Redneck.Factory.RRMenuHandler.*;
import static ru.m210projects.Redneck.Globals.*;

import java.util.Arrays;

import ru.m210projects.Build.FileHandle.FileEntry;
import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Build.Pattern.MenuItems.BuildMenu;
import ru.m210projects.Build.Pattern.MenuItems.MenuButton;
import ru.m210projects.Build.Pattern.MenuItems.MenuConteiner;
import ru.m210projects.Build.Pattern.MenuItems.MenuHandler;
import ru.m210projects.Build.Pattern.MenuItems.MenuItem;
import ru.m210projects.Build.Pattern.MenuItems.MenuProc;
import ru.m210projects.Build.Pattern.MenuItems.MenuSwitch;
import ru.m210projects.Build.Pattern.MenuItems.MenuHandler.MenuOpt;
import ru.m210projects.Build.Types.LittleEndian;
import ru.m210projects.Redneck.Main;
import ru.m210projects.Redneck.Factory.RRMenuHandler;
import ru.m210projects.Redneck.Types.GameInfo;

public class NetworkMenu extends BuildMenu {
	
	private Main app;
	
	private int mGameType = 1;
	private int mEpisodeId = 0;
	private int mLevelId = 0;
	private int mDifficulty = 2;
	private int mMonsters = 0;
	private int mFFire = 0;
	private int mMarkers = 1;
	private String mContent;
	private Object currentFile;

	private int[] netEpisodeInfo = new int[nMaxEpisodes];

	private MenuProc mLevelsUpdate;
	private MenuConteiner mMenuLevel;
	private MenuConteiner mMenuEpisode;
	private MenuConteiner mMenuDifficulty;
	
	public NetworkMenu(final Main app)
	{
		this.app = app;
		final RRMenuHandler menu = app.menu;
		addItem(new RRTitle("NETWORK GAME"), false);

		final MenuConteiner pItem = new MenuConteiner("Content", app.getFont(2), 20,
				45, 280, new String[] { "" }, 0, null) {
			
			@Override
			public boolean callback(MenuHandler handler, MenuOpt opt) {
				switch(opt)
				{
				case ENTER:
				case LMB:
					RUserContent usercont = (RUserContent) menu.mMenus[USERCONTENT];
					if(!usercont.showmain) 
						usercont.setShowMain(true);
					StopAllSounds();
					handler.mOpen(usercont, -1);
					return false;
				default:
					return m_pMenu.mNavigation(opt);
				}
			}
			
			@Override
			public void open() {
				if(!app.isCurrentScreen(gGameScreen))
					setEpisode(defGame);
			}

			@Override
			public void draw(MenuHandler handler) {
				this.list[0] = toCharArray(mContent);
				super.draw(handler);
			}
		};
		pItem.listFont = app.getFont(1);
		
		MenuConteiner mMenuGame = new MenuConteiner("GAME TYPE", app.getFont(1), 20, 70, 280, null, 0, new MenuProc() {
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
					this.list[0] = "DUKEMATCH (SPAWN)".toCharArray();
					this.list[1] = "COOPERATIVE PLAY".toCharArray();
					this.list[2] = "DUKEMATCH (NO SPAWN)".toCharArray();
				}
				num = mGameType;
			}
		};
		
		mLevelsUpdate = new MenuProc() {
			@Override
			public void run( MenuHandler handler, MenuItem pItem ) {
				MenuConteiner item = (MenuConteiner) pItem;
				if(currentFile instanceof GameInfo)
				{
					GameInfo mGameInfo = (GameInfo) currentFile;
					int size = mGameInfo.episodes[netEpisodeInfo[mEpisodeId]].nMaps;
					if (item.list == null || item.list.length != size)
						item.list = new char[size][];
					for (int i = 0; i < size; i++)
						item.list[i] =  mGameInfo.episodes[netEpisodeInfo[mEpisodeId]].gMapInfo[i].title.toCharArray();
				} else {
					item.list = new char[1][];
					item.list[0] = "None".toCharArray();
				}

				mLevelId = item.num = 0;
			}
		};
		
		mMenuLevel = new MenuConteiner("LEVEL", app.getFont(1), 20, 90, 280, null, 0, new MenuProc() {
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
				mCheckEnableItem(currentFile instanceof GameInfo);
				super.draw(handler);
			}
		};
		
		mMenuEpisode = new MenuConteiner("EPISODE", app.getFont(1), 20, 80, 280, null, 0, new MenuProc() {
			@Override
			public void run( MenuHandler handler, MenuItem pItem ) {
				MenuConteiner item = (MenuConteiner) pItem;
				mEpisodeId = item.num;
				mLevelsUpdate.run(menu, mMenuLevel);
			}
		}) {

			@Override
			public void open() {
				num = mEpisodeId;
			}

			@Override
			public void draw(MenuHandler handler) {
				mCheckEnableItem(currentFile instanceof GameInfo);
				super.draw(handler);
			}
		};
		
		int pos = 90;
		mMenuDifficulty = new MenuConteiner("MONSTERS", app.getFont(1), 20, pos += 12, 280, null, 0, new MenuProc() {
			@Override
			public void run( MenuHandler handler, MenuItem pItem ) {
				MenuConteiner item = (MenuConteiner) pItem;
				if(item.num == 0)
					mMonsters = 1;
				else mMonsters = 0;
				
				mDifficulty = item.num-1;
			}
		}) {
			@Override
			public void open() {
				num = mDifficulty+1;
			}
			
			@Override
			public void draw(MenuHandler handler) {
				mCheckEnableItem(currentFile instanceof GameInfo);
				super.draw(handler);
			}
		};
		
		MenuSwitch mMenuMarkers = new MenuSwitch("MARKERS", app.getFont(1), 20, pos += 12, 280, mMarkers==1, new MenuProc() {
			@Override
			public void run( MenuHandler handler, MenuItem pItem ) {
				MenuSwitch sw = (MenuSwitch) pItem;
				mMarkers = sw.value?1:0;
			}
		}, "Yes", "No");
		
		MenuSwitch mMenuFFire = new MenuSwitch("FRIENDLY FIRE", app.getFont(1), 20, pos += 12, 280, mFFire==1, new MenuProc() {
			@Override
			public void run( MenuHandler handler, MenuItem pItem ) {
				MenuSwitch sw = (MenuSwitch) pItem;
				mFFire = sw.value?1:0;
			}
		}, "Yes", "No") {
			
			@Override
			public void draw(MenuHandler handler) {
				mCheckEnableItem(mGameType == 1);
				super.draw(handler);
			}
		};
		
		final MenuProc mNetStart = new MenuProc() {
			@Override
			public void run( MenuHandler handler, MenuItem pItem ) {
				pNetInfo.nGameType = mGameType;
				if(currentFile instanceof FileEntry)
					pNetInfo.nEpisode = mEpisodeId;
				else pNetInfo.nEpisode = netEpisodeInfo[mEpisodeId];
				pNetInfo.nLevel = mLevelId;
				pNetInfo.nDifficulty = mDifficulty;
				pNetInfo.nMonsters = mMonsters;
				pNetInfo.nRespawnMonsters = 0;
				pNetInfo.nRespawnInventory = 0;
				pNetInfo.nRespawnItem = 0;
				pNetInfo.nMarkers = mMarkers;
				pNetInfo.nFriendlyFire = mFFire;

				System.err.println("nNetType " + pNetInfo.nGameType);
				System.err.println("nNetDifficulty " + pNetInfo.nDifficulty);
				System.err.println("nNetMonsters " + pNetInfo.nMonsters);
				System.err.println("nNetMarkers " + pNetInfo.nMarkers);
				System.err.println("nNetFFire " + pNetInfo.nFriendlyFire);
				
				if (numplayers >= 2) {
					byte[] packbuf = app.net.packbuf;
					packbuf[0] = kPacketLevelStart;
					int ptr = 1;

					LittleEndian.putInt(packbuf, ptr, myconnectindex); ptr += 4;
					LittleEndian.putInt(packbuf, ptr, app.net.nNetVersion); ptr += 4;

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
					gGameScreen.newgame(true, currentFile, netEpisodeInfo[mEpisodeId], mLevelId, mDifficulty);
			}
		};
		
		MenuButton mStart = new MenuButton("START GAME", app.getFont(2), 20, pos += 15, 280, 1, 0, null, -1, mNetStart, 0) {
			@Override
			public void draw(MenuHandler handler) {
				mCheckEnableItem(myconnectindex == connecthead && currentFile != null);
				super.draw(handler);
			}
		};
		
		addItem(pItem, true);
		addItem(mMenuGame, false);
		addItem(mMenuEpisode, false);
		addItem(mMenuLevel, false);
		addItem(mMenuDifficulty, false);
		addItem(mMenuMarkers, false);
		addItem(mMenuFFire, false);
		addItem(mStart, false);
	}
	
	private void updateUserEpisodeList(GameInfo gInfo) {
		Arrays.fill(netEpisodeInfo, -1);
		int nEpisodes = 0;
		for (int i = 0; i < nMaxEpisodes; i++) {
			if(gInfo.episodes[i] != null && gInfo.episodes[i].nMaps != 0) 
				netEpisodeInfo[nEpisodes++] = i;
		}
		if (mMenuEpisode.list == null || mMenuEpisode.list.length != nEpisodes)
			mMenuEpisode.list = new char[nEpisodes][];

		for (int i = 0; i < nEpisodes; i++) 
			mMenuEpisode.list[i] = gInfo.episodes[netEpisodeInfo[i]].Title.toCharArray();
		
		if(mMenuDifficulty.list == null)
			mMenuDifficulty.list = new char[5][];
		mMenuDifficulty.list[0] = "NONE".toCharArray();
		for(int i = 0; i < 4; i++)
			mMenuDifficulty.list[1 + i] = gInfo.skillnames[i].toCharArray();
	}
	
	public Object getFile()
	{
		return currentFile;
	}

	public void setEpisode(GameInfo ini)
	{
		if(ini == null || currentFile == ini)
			return;
		
		String path = ini.getFile().getPath();
		if(ini.isPackage())
			path += ":" + ini.ConName;
		
		long crc32 = 0;
		if(numplayers > 1)
			crc32 = ini.getFile().getChecksum();

		if(myconnectindex == connecthead && !app.net.WaitForContentCheck(path, crc32, 0))
		{
			String msg = "";
			for(int i=connecthead;i>=0;i=connectpoint2[i])
			{
				if(app.net.gContentFound[i] != 1) {
					if(app.net.gContentFound[i] == 2)
						msg += ud.user_name[i] + "(wrong checksum)" + ", ";
					else msg += ud.user_name[i] + ", ";
				}
			}
			
			msg = msg.substring(0, msg.length() - 2);
			msg += "  is missing content: " + ini.Title;
			
			Console.Println(msg, OSDTEXT_RED);
			if(!Console.IsShown())
				Console.toggle();
			
			currentFile = null;
			return;
		}
		
		currentFile = ini;
		mContent = ini.Title;
		mEpisodeId = 0;
		mMenuEpisode.num = 0;
		
		updateUserEpisodeList(ini);
		mLevelsUpdate.run(app.pMenu, mMenuLevel);
	}
	
	public void setMap(FileEntry map)
	{
		if(map == null || currentFile == map)
			return;
		
		long crc32 = 0;
		if(numplayers > 1)
			crc32 = map.getChecksum();
		
		if(myconnectindex == connecthead && !app.net.WaitForContentCheck(map.getPath(), crc32, 0))
		{
			String msg = "";
			for(int i=connecthead;i>=0;i=connectpoint2[i])
			{
				if(app.net.gContentFound[i] != 1) {
					if(app.net.gContentFound[i] == 2)
						msg += ud.user_name[i] + "(wrong checksum)" + ", ";
					else msg += ud.user_name[i] + ", ";
				}
			}
			
			msg = msg.substring(0, msg.length() - 2);
			msg += " haven't content: " + map.getName();
			
			Console.Println(msg, OSDTEXT_RED);
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
		
		if(mMenuDifficulty.list == null)
			mMenuDifficulty.list = new char[5][];
		mMenuDifficulty.list[0] = "NONE".toCharArray();
		for(int i = 0; i < 4; i++)
			mMenuDifficulty.list[1 + i] = defGame.skillnames[i].toCharArray();
	}

}
