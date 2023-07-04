package ru.m210projects.Wang.Menus.Network;

import static ru.m210projects.Build.Net.Mmulti.connecthead;
import static ru.m210projects.Build.Net.Mmulti.connectpoint2;
import static ru.m210projects.Build.Net.Mmulti.myconnectindex;
import static ru.m210projects.Build.Net.Mmulti.numplayers;
import static ru.m210projects.Build.OnSceenDisplay.Console.OSDTEXT_RED;
import static ru.m210projects.Build.Strhandler.toCharArray;
import static ru.m210projects.Wang.Factory.WangMenuHandler.USERCONTENT;
import static ru.m210projects.Wang.Factory.WangNetwork.TimeLimitTable;
import static ru.m210projects.Wang.Game.Player;
import static ru.m210projects.Wang.Game.defGame;
import static ru.m210projects.Wang.Game.pNetInfo;
import static ru.m210projects.Wang.Main.gGameScreen;
import static ru.m210projects.Wang.Sound.StopSound;

import java.util.Arrays;

import ru.m210projects.Build.FileHandle.FileEntry;
import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Build.Pattern.MenuItems.BuildMenu;
import ru.m210projects.Build.Pattern.MenuItems.MenuButton;
import ru.m210projects.Build.Pattern.MenuItems.MenuConteiner;
import ru.m210projects.Build.Pattern.MenuItems.MenuHandler;
import ru.m210projects.Build.Pattern.MenuItems.MenuHandler.MenuOpt;
import ru.m210projects.Build.Pattern.MenuItems.MenuItem;
import ru.m210projects.Build.Pattern.MenuItems.MenuProc;
import ru.m210projects.Build.Pattern.MenuItems.MenuSwitch;
import ru.m210projects.Wang.Main;
import ru.m210projects.Wang.Factory.WangMenuHandler;
import ru.m210projects.Wang.Factory.WangNetwork.PacketType;
import ru.m210projects.Wang.Menus.MenuUserContent;
import ru.m210projects.Wang.Menus.WangSwitch;
import ru.m210projects.Wang.Menus.WangTitle;
import ru.m210projects.Wang.Type.GameInfo;

public class MenuNetwork extends BuildMenu {

	private Main app;

	private int mGameType = 1;
	private int mEpisodeId = 0;
	private int mLevelId = 0;
	private int mDifficulty = -1;
	private int mMonsters = 0;
	private int mFFire = 0;
	private int mMarkers = 1;
	private boolean TeamPlay = false;
	private boolean NetNuke = true;
	private int NetTimeLimit = 0;
	private int NetKillLimit = 0;
	private String mContent;
	private Object currentFile;

	private int[] netEpisodeInfo = new int[3];

	private MenuProc mLevelsUpdate;
	private MenuConteiner mMenuLevel;
	private MenuConteiner mMenuEpisode;
	private MenuConteiner mMenuDifficulty;

	public MenuNetwork(final Main app) {
		this.app = app;
		final WangMenuHandler menu = app.menu;
		addItem(new WangTitle("NETWORK GAME"), false);

		final MenuConteiner pItem = new MenuConteiner("Content", app.getFont(2), 35, 45, 240, new String[] { "" }, 0,
				null) {

			@Override
			public boolean callback(MenuHandler handler, MenuOpt opt) {
				switch (opt) {
				case ENTER:
				case LMB:
					MenuUserContent usercont = (MenuUserContent) menu.mMenus[USERCONTENT];
					if (!usercont.showmain)
						usercont.setShowMain(true);
					StopSound();
					handler.mOpen(usercont, -1);
					return false;
				default:
					return m_pMenu.mNavigation(opt);
				}
			}

			@Override
			public void open() {
				if (!app.isCurrentScreen(gGameScreen))
					setEpisode(defGame);
			}

			@Override
			public void draw(MenuHandler handler) {
				this.list[0] = toCharArray(mContent);
				super.draw(handler);
			}
		};
		pItem.listFont = app.getFont(1);

		MenuConteiner mMenuGame = new MenuConteiner("Game Type", app.getFont(1), 35, 70, 240, null, 0, new MenuProc() {
			@Override
			public void run(MenuHandler handler, MenuItem pItem) {
				MenuConteiner item = (MenuConteiner) pItem;
				mGameType = item.num;
			}
		}) {

			@Override
			public void open() {
				if (this.list == null) {
					this.list = new char[][] { "WangBang (spawn)".toCharArray(), "WangBang (no spawn)".toCharArray(),
							"Cooperative".toCharArray() };
				}
				num = mGameType;
			}
		};

		mLevelsUpdate = new MenuProc() {
			@Override
			public void run(MenuHandler handler, MenuItem pItem) {
				MenuConteiner item = (MenuConteiner) pItem;
				if (currentFile instanceof GameInfo) {
					GameInfo mGameInfo = (GameInfo) currentFile;
					int size = mGameInfo.episode[netEpisodeInfo[mEpisodeId]].nMaps;
					if (item.list == null || item.list.length != size)
						item.list = new char[size][];
					for (int i = 0; i < size; i++) {
						if( mGameInfo.episode[netEpisodeInfo[mEpisodeId]].gMapInfo[i] != null)
							item.list[i] = mGameInfo.episode[netEpisodeInfo[mEpisodeId]].gMapInfo[i].Description.toCharArray();
						else item.list[i] = ("Null[" + i + "]").toCharArray();
					}
				} else {
					item.list = new char[1][];
					item.list[0] = "None".toCharArray();
				}
				mLevelId = item.num = 0;
			}
		};

		mMenuLevel = new MenuConteiner("Level", app.getFont(1), 35, 90, 240, null, 0, new MenuProc() {
			@Override
			public void run(MenuHandler handler, MenuItem pItem) {
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

		mMenuEpisode = new MenuConteiner("Episode", app.getFont(1), 35, 80, 240, null, 0, new MenuProc() {
			@Override
			public void run(MenuHandler handler, MenuItem pItem) {
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
		mMenuDifficulty = new MenuConteiner("Monsters", app.getFont(1), 35, pos += 10, 240, null, 0, new MenuProc() {
			@Override
			public void run(MenuHandler handler, MenuItem pItem) {
				MenuConteiner item = (MenuConteiner) pItem;
				if (item.num == 0)
					mMonsters = 1;
				else
					mMonsters = 0;

				mDifficulty = item.num - 1;
			}
		}) {
			@Override
			public void open() {
				num = mDifficulty + 1;
			}
		};

		WangSwitch mMenuMarkers = new WangSwitch("Markers", app.getFont(1), 35, pos += 10, 240, mMarkers == 1,
				new MenuProc() {
					@Override
					public void run(MenuHandler handler, MenuItem pItem) {
						MenuSwitch sw = (MenuSwitch) pItem;
						mMarkers = sw.value ? 1 : 0;
					}
				});

		MenuConteiner mMenuKills = new MenuConteiner("Kill Limit", app.getFont(1), 35, pos += 10, 240, null,
				NetKillLimit, new MenuProc() {
					@Override
					public void run(MenuHandler handler, MenuItem pItem) {
						MenuConteiner item = (MenuConteiner) pItem;
						NetKillLimit = item.num;
					}
				}) {

			@Override
			public void open() {
				if (this.list == null) {
					this.list = new char[TimeLimitTable.length][];
					this.list[0] = "Infinite".toCharArray();
					for (int i = 1; i < list.length; i++)
						list[i] = Integer.toString(i * 10).toCharArray();
				}
				num = NetKillLimit;
			}
		};

		MenuConteiner mMenuTime = new MenuConteiner("Time Limit", app.getFont(1), 35, pos += 10, 240, null,
				NetTimeLimit, new MenuProc() {
					@Override
					public void run(MenuHandler handler, MenuItem pItem) {
						MenuConteiner item = (MenuConteiner) pItem;
						NetTimeLimit = item.num;
					}
				}) {

			@Override
			public void open() {
				if (this.list == null) {
					this.list = new char[TimeLimitTable.length][];
					this.list[0] = "Infinite".toCharArray();
					for (int i = 1; i < list.length; i++)
						list[i] = (TimeLimitTable[i] + " minutes").toCharArray();
				}
				num = NetTimeLimit;
			}
		};

		WangSwitch mMenuTeam = new WangSwitch("TeamPlay", app.getFont(1), 35, pos += 10, 240, TeamPlay, new MenuProc() {
			@Override
			public void run(MenuHandler handler, MenuItem pItem) {
				MenuSwitch sw = (MenuSwitch) pItem;
				TeamPlay = sw.value;
			}
		});

		WangSwitch mMenuFFire = new WangSwitch("Hurt teammate", app.getFont(1), 35, pos += 10, 240, mFFire == 1,
				new MenuProc() {
					@Override
					public void run(MenuHandler handler, MenuItem pItem) {
						MenuSwitch sw = (MenuSwitch) pItem;
						mFFire = sw.value ? 1 : 0;
					}
				}) {

			@Override
			public void draw(MenuHandler handler) {
				mCheckEnableItem(mGameType == 2 || TeamPlay);
				super.draw(handler);
			}
		};

		WangSwitch mMenuNuke = new WangSwitch("Play with Nuke", app.getFont(1), 35, pos += 10, 240, NetNuke,
				new MenuProc() {
					@Override
					public void run(MenuHandler handler, MenuItem pItem) {
						MenuSwitch sw = (MenuSwitch) pItem;
						NetNuke = sw.value;
					}
				});

		final MenuProc mNetStart = new MenuProc() {
			@Override
			public void run(MenuHandler handler, MenuItem pItem) {
				pNetInfo.nGameType = mGameType;
				if (currentFile instanceof FileEntry)
					pNetInfo.nEpisode = mEpisodeId;
				else
					pNetInfo.nEpisode = netEpisodeInfo[mEpisodeId];
				pNetInfo.nLevel = mLevelId;
				pNetInfo.nDifficulty = mDifficulty;
				pNetInfo.nMonsters = mMonsters;
				pNetInfo.nFriendlyFire = mFFire;
				pNetInfo.SpawnMarkers = mMarkers == 1;

				pNetInfo.TeamPlay = TeamPlay;
				pNetInfo.NetNuke = NetNuke;
				pNetInfo.TimeLimit = NetTimeLimit;
				pNetInfo.KillLimit = NetKillLimit;

				if (numplayers >= 2) {
					byte[] packbuf = app.net.packbuf;
					int len = PacketType.LevelStart.Send(packbuf);

					app.net.sendtoall(packbuf, len);
				}

				if (app.net.WaitForAllPlayers(0))
					gGameScreen.newgame(true, currentFile, netEpisodeInfo[mEpisodeId], mLevelId, mDifficulty);
			}
		};

		MenuButton mStart = new MenuButton("Start Game", app.getFont(1), 35, pos += 10, 240, 0, 0, null, -1, mNetStart,
				0) {
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
		addItem(mMenuKills, false);
		addItem(mMenuTime, false);
		addItem(mMenuTeam, false);
		addItem(mMenuFFire, false);
		addItem(mMenuNuke, false);
		addItem(mStart, false);
	}

	private void updateUserEpisodeList(GameInfo gInfo) {
		Arrays.fill(netEpisodeInfo, -1);
		int nEpisodes = 0;
		for (int i = 0; i < gInfo.episode.length; i++) {
			if (gInfo.episode[i] != null && gInfo.episode[i].nMaps != 0)
				netEpisodeInfo[nEpisodes++] = i;
		}
		if (mMenuEpisode.list == null || mMenuEpisode.list.length != nEpisodes)
			mMenuEpisode.list = new char[nEpisodes][];

		for (int i = 0; i < nEpisodes; i++)
			mMenuEpisode.list[i] = gInfo.episode[netEpisodeInfo[i]].Title.toCharArray();

		if (mMenuDifficulty.list == null)
			mMenuDifficulty.list = new char[5][];
		mMenuDifficulty.list[0] = "None".toCharArray();

		for (int i = 0; i < 4; i++)
			mMenuDifficulty.list[1 + i] = gInfo.skills[i].toCharArray();
	}

	public Object getFile() {
		return currentFile;
	}

	public void setEpisode(GameInfo ini) {
		if (ini == null || currentFile == ini)
			return;
		String path = null;
		if(ini.isPackage())
			path = ini.getFile().getPath();
		else path = "<d>" + ini.getDirectory().getRelativePath();

		long crc32 = 0;
		if (numplayers > 1)
			crc32 = ini.getChecksum();

		if(myconnectindex == connecthead && !app.net.WaitForContentCheck(path, crc32, 0))
		{
			String msg = "";
			for(int i=connecthead;i>=0;i=connectpoint2[i])
			{
				if(app.net.gContentFound[i] != 1) {
					if(app.net.gContentFound[i] == 2)
						msg += Player[i].getName() + "(wrong checksum)" + ", ";
					else msg += Player[i].getName() + ", ";
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

	public void setMap(FileEntry map) {
		if (map == null || currentFile == map)
			return;

		long crc32 = 0;
		if (numplayers > 1)
			crc32 = map.getChecksum();

		if(myconnectindex == connecthead && !app.net.WaitForContentCheck(map.getPath(), crc32, 0))
		{
			String msg = "";
			for(int i=connecthead;i>=0;i=connectpoint2[i])
			{
				if(app.net.gContentFound[i] != 1) {
					if(app.net.gContentFound[i] == 2)
						msg += Player[i].getName() + "(wrong checksum)" + ", ";
					else msg += Player[i].getName() + ", ";
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

		if (mMenuEpisode.list != null)
			mMenuEpisode.list[0] = "None".toCharArray();
		mEpisodeId = mMenuEpisode.num = 0;
		if (mMenuLevel.list != null)
			mMenuLevel.list[0] = "Usermap".toCharArray();
		mLevelId = mMenuLevel.num = 0;

		if (mMenuDifficulty.list == null)
			mMenuDifficulty.list = new char[5][];
		mMenuDifficulty.list[0] = "None".toCharArray();
		for (int i = 0; i < 4; i++)
			mMenuDifficulty.list[1 + i] = defGame.skills[i].toCharArray();
	}

}
