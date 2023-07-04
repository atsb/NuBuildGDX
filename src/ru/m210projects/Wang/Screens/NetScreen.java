package ru.m210projects.Wang.Screens;

import static ru.m210projects.Wang.Names.*;
import static ru.m210projects.Wang.Factory.WangMenuHandler.*;
import static ru.m210projects.Wang.Game.*;
import static ru.m210projects.Wang.Main.*;
import static ru.m210projects.Wang.Sound.*;
import static ru.m210projects.Build.Net.Mmulti.*;
import static ru.m210projects.Wang.Factory.WangNetwork.*;

import ru.m210projects.Wang.Main;
import ru.m210projects.Build.Pattern.ScreenAdapters.ConnectAdapter;

public class NetScreen extends ConnectAdapter {

	private Main app;
	public NetScreen(Main game) {
		super(game, LOADSCREEN, game.getFont(1));
		this.app = game;
	}

	@Override
	public void show() {
		super.show();
		StopFX();
	}

	@Override
	public void back() {
		game.changeScreen(gMenuScreen);
	}

	@Override
	public void connect() {
		gDisconnectScreen.updateList();
		screenpeek = myconnectindex;
		CommPlayers = numplayers;
        
		gNet.InitNetPlayerProfile();

		app.changeScreen(gMenuScreen);

		app.menu.mClose();
		app.menu.mOpen(app.menu.mMenus[NETWORKGAME], -1);
	}
}
