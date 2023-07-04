package ru.m210projects.Witchaven.Screens;

import static ru.m210projects.Build.Pragmas.*;
import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Witchaven.Factory.WHMenuHandler.MAIN;
import static ru.m210projects.Witchaven.Factory.WHMenuHandler.QUIT;
import static ru.m210projects.Witchaven.Names.*;
import static ru.m210projects.Witchaven.WH1Names.*;
import static ru.m210projects.Witchaven.WH2Names.*;
import static ru.m210projects.Witchaven.WHSND.SND_CheckLoops;
import static ru.m210projects.Witchaven.WHSND.SND_MenuMusic;
import static ru.m210projects.Witchaven.WHSND.sndStopMusic;

import ru.m210projects.Build.Architecture.BuildFrame.FrameType;
import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.Architecture.BuildGraphics.Option;
import ru.m210projects.Build.Pattern.ScreenAdapters.MenuAdapter;
import ru.m210projects.Witchaven.Main;
import ru.m210projects.Witchaven.Factory.WHMenuHandler;

public class MenuScreen extends MenuAdapter {

	private WHMenuHandler menu;
	private Main game;
	public MenuScreen(Main game) {
		super(game, game.menu.mMenus[MAIN]);
		this.menu = game.menu;
		this.game = game;
	}

	@Override
	public void draw(float delta) {
		if(game.WH2) {
			int scale = divscale(ydim, engine.getTile(VMAINBLANK).getHeight(), 16);
			engine.rotatesprite(xdim<<15,ydim<<15, scale,0,VMAINBLANK,0,0,8,0,0,xdim-1,ydim-1);
		} else {
			engine.rotatesprite(0,0, 32768,0,SVGAMENU,0,0,2+8+16,0,0,xdim-1,ydim-1);
			engine.rotatesprite(0,120<<16, 32768,0,SVGAMENU2,0,0,2+8+16,0,0,xdim-1,ydim-1);

			if(menu.isOpened(menu.mMenus[MAIN]) || menu.isOpened(menu.mMenus[QUIT]))
				engine.rotatesprite(1<<16,1<<16,32768,0,MAINMENU,0,0, 2+8+16, 0,0,xdim-1,ydim-1);
			else engine.rotatesprite(1<<16,1<<16,32768,0,TITLEPIC,0,0, 2+8+16, 0,0,xdim-1,ydim-1);
		}
	}

	@Override
	public void show() {
		menu.mOpen(mainMenu, -1);
		game.pInput.ctrlResetKeyStatus();
		SND_CheckLoops();
		SND_MenuMusic();
	}

	@Override
	public void pause () {
		sndStopMusic();
		if (BuildGdx.graphics.getFrameType() == FrameType.GL)
			BuildGdx.graphics.extra(Option.GLDefConfiguration);
	}

	@Override
	public void resume () {
		SND_MenuMusic();
		game.updateColorCorrection();
	}

}
