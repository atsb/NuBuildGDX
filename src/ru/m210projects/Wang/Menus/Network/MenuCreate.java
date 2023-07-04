package ru.m210projects.Wang.Menus.Network;

import static ru.m210projects.Wang.Main.*;
import static ru.m210projects.Wang.Factory.WangMenuHandler.*;

import ru.m210projects.Wang.Main;
import ru.m210projects.Wang.Menus.WangTitle;
import ru.m210projects.Build.Pattern.BuildGame;
import ru.m210projects.Build.Pattern.MenuItems.MenuConteiner;
import ru.m210projects.Build.Pattern.MenuItems.MenuHandler;
import ru.m210projects.Build.Pattern.MenuItems.MenuItem;
import ru.m210projects.Build.Pattern.MenuItems.MenuProc;
import ru.m210projects.Build.Pattern.MenuItems.MenuTitle;
import ru.m210projects.Build.Pattern.ScreenAdapters.ConnectAdapter.NetFlag;

public class MenuCreate extends ru.m210projects.Build.Pattern.CommonMenus.MenuCreate {

	private Main app;
	public MenuCreate(Main app) {
		super(app, 46, 45, 10, 240, app.getFont(1), 8);
		this.app = (Main) app;
		
		MenuConteiner mColor = new MenuConteiner("Player color", app.getFont(1), 46, mMenuFakeMM.y, 240, null, 0, new MenuProc() {
			@Override
			public void run( MenuHandler handler, MenuItem pItem ) {
				MenuConteiner item = (MenuConteiner) pItem;
				gs.NetColor = (byte) item.num;
			}
		}) {
			@Override
			public void open() {
				if (this.list == null) {
					this.list = new char[][] {
						"Brown".toCharArray(),
						"Gray".toCharArray(),
						"Purple".toCharArray(),
						"Red".toCharArray(),
						"Yellow".toCharArray(),
						"Olive".toCharArray(),
						"Green".toCharArray(),
						"Blue".toCharArray()
					};
				}
				num = gs.NetColor;
			}
		};
		
		MenuConteiner mMenuBots = new MenuConteiner("Use bots", app.getFont(1), 46, mMenuFakeMM.y + 20, 240, null, 0, new MenuProc() {
			@Override
			public void run( MenuHandler handler, MenuItem pItem ) {
				MenuConteiner item = (MenuConteiner) pItem;
				gNet.BotSkill = (byte) (item.num - 1);
				if(gNet.BotSkill > 0)
					gNet.BotMode = true;
				else gNet.BotMode = false;
			}
		}) {
			
			@Override
			public void draw(MenuHandler handler) {
				num = (gNet.BotSkill + 1);
				mCheckEnableItem(mPlayers > 1 && mUseFakeMultiplayer);
				super.draw(handler);
			}
			
			@Override
			public void open() {
				if (this.list == null) {
					this.list = new char[][] {
						"No".toCharArray(),
						"Skill1".toCharArray(),
						"Skill2".toCharArray(),
						"Skill3".toCharArray(),
						"Skill4".toCharArray(),
					};
				}
			}
		};

		removeItem(mMenuFakeMM);
		removeItem(mCreate);
		
		addItem(mColor, false);
		mMenuFakeMM.y += 10;
		addItem(mMenuFakeMM, false);
		addItem(mMenuBots, false);

		mCreate.x = 46;
		mCreate.y += 20;
		mCreate.align = 0;
		addItem(mCreate, false);
	}

	@Override
	public MenuTitle getTitle(BuildGame app, String text) {
		return new WangTitle(text);
	}

	@Override
	public void createGame(int mPlayers, boolean mUseFakeMultiplayer, String[] param) {
		gNet.FakeMultiplayer = false;
		if (mPlayers == 1 || mUseFakeMultiplayer) {
			gNet.FakeMultiplayer = true;
			gNet.FakeMultiNumPlayers = (short) mPlayers;
			app.menu.mOpen(app.menu.mMenus[NETWORKGAME], -1);
		} else app.changeScreen(gNetScreen.setFlag(NetFlag.Create, param));
	}
	
}
