package ru.m210projects.Wang.Menus.Network;

import static ru.m210projects.Wang.Main.*;

import ru.m210projects.Build.Pattern.BuildGame;
import ru.m210projects.Build.Pattern.MenuItems.MenuConteiner;
import ru.m210projects.Build.Pattern.MenuItems.MenuHandler;
import ru.m210projects.Build.Pattern.MenuItems.MenuItem;
import ru.m210projects.Build.Pattern.MenuItems.MenuProc;
import ru.m210projects.Build.Pattern.MenuItems.MenuTitle;
import ru.m210projects.Build.Pattern.ScreenAdapters.ConnectAdapter.NetFlag;
import ru.m210projects.Wang.Menus.WangTitle;

public class MenuJoin extends ru.m210projects.Build.Pattern.CommonMenus.MenuJoin {

	public MenuJoin(BuildGame app) {
		super(app, 46, 35, 10, 240, app.getFont(1));
		
		MenuConteiner mColor = new MenuConteiner("Player color", app.getFont(1), 46, mIPAddress.y, 240, null, 0, new MenuProc() {
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
		
		removeItem(mIPAddress);
		removeItem(mConnect);
		mIPAddress.y += 10;
		addItem(mColor, false);
		addItem(mIPAddress, false);
		
		mConnect.x = 46;
		mConnect.y += 10;
		mConnect.align = 0;
		addItem(mConnect, false);
	}

	@Override
	public MenuTitle getTitle(BuildGame app, String text) {
		return new WangTitle(text);
	}

	@Override
	public void joinGame(String[] param) {
		game.changeScreen(gNetScreen.setFlag(NetFlag.Connect, param));
	}
	
}
