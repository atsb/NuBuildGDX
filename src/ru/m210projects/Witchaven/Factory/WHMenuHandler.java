package ru.m210projects.Witchaven.Factory;

import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Build.Pragmas.*;
import static ru.m210projects.Witchaven.Globals.*;
import static ru.m210projects.Witchaven.WHSND.*;
import static ru.m210projects.Witchaven.Names.*;

import ru.m210projects.Build.Engine;
import ru.m210projects.Build.Pattern.BuildFont;
import ru.m210projects.Build.Pattern.BuildGame;
import ru.m210projects.Build.Pattern.CommonMenus.MenuRendererSettings;
import ru.m210projects.Build.Pattern.MenuItems.BuildMenu;
import ru.m210projects.Build.Pattern.MenuItems.MenuHandler;
import ru.m210projects.Build.Pattern.MenuItems.MenuItem;
import ru.m210projects.Build.Pattern.MenuItems.MenuSlider;
import ru.m210projects.Build.Types.Tile;
import ru.m210projects.Witchaven.Fonts.MenuFont;
import ru.m210projects.Witchaven.Menu.MenuInterfaceSet;
import ru.m210projects.Witchaven.Screens.MenuScreen;

public class WHMenuHandler extends MenuHandler {

	public BuildMenu[] mMenus;
	protected Engine engine;
	protected BuildGame app;

	public static final int MAIN = 0;
	public static final int GAME = 1;
	public static final int LOADGAME = 3;
	public static final int SAVEGAME = 4;
	public static final int QUIT = 6;
	public static final int HELP = 7;
	public static final int AUDIOSET = 8;
	public static final int CONTROLSET = 9;
	public static final int OPTIONS = 10;
	public static final int COLORCORR = 11;
	public static final int DIFFICULTY = 12;
	public static final int CORRUPTLOAD = 13;

	public WHMenuHandler(BuildGame app)
	{
		mMenus = new BuildMenu[15];
		this.engine = app.pEngine;
		this.app = app;
	}

	@Override
	public void mDrawMenu() {
		if(!(app.getScreen() instanceof MenuScreen) && !(app.pMenu.getCurrentMenu() instanceof MenuRendererSettings) && !(app.pMenu.getCurrentMenu() instanceof MenuInterfaceSet)) {
			int tile = MAINMENU;
			Tile pic = engine.getTile(tile);

			float kt = xdim / (float) ydim;
			float kv = pic.getWidth() / (float) pic.getHeight();
			float scale;
			if (kv >= kt)
				scale = (ydim + 1) / (float) pic.getHeight();
			else
				scale = (xdim + 1) / (float) pic.getWidth();

			engine.rotatesprite(0, 0, (int) (scale * 65536), 0, tile, 127, 0, 8 | 16 | 1, 0, 0, xdim - 1, ydim - 1);
		}
		mMenuDraw();
	}

	protected void mMenuDraw() {
		super.mDrawMenu();
	}

	@Override
	public int getShade(MenuItem item) {
		if(item != null && item.getClass().getSuperclass().equals(MenuSlider.class) && !item.isEnabled())
			return -127;

		if(item != null && item.isFocused())
			return 16 - (totalclock & 0x3F);
		return 0;
	}

	@Override
	public int getPal(BuildFont font, MenuItem item) {
		if(item != null)
		{
			if(item.isFocused()) {
				if(font != null && font.getClass().equals(MenuFont.class))
					return 20;
				return 9;
			}

			if(!item.isEnabled()) {
				if(font != null && font.getClass().equals(MenuFont.class))
					return 9;
				return 20;
			}

			if(font != null && font.getClass().equals(MenuFont.class))
				return item.pal;
			return 9;
		}

		return 0;
	}

	@Override
	public void mDrawMouse(int x, int y) {
		if(!app.pCfg.menuMouse) return;

		int zoom = scale(0x10000, ydim, 200);
		int czoom = mulscale(0x8000, mulscale(zoom, app.pCfg.gMouseCursorSize, 16), 16);
		int xoffset = 0;
		int yoffset = 0;
		int ang = 0;

		engine.rotatesprite((x + xoffset) << 16, (y + yoffset) << 16, czoom, ang, MOUSECURSOR, 0, 0, 8, 0, 0, xdim-1, ydim-1);
	}

	@Override
	public void mDrawBackButton() {
		if(!app.pCfg.menuMouse)
			return;

		int zoom = scale(BACKSCALE, ydim, 200);
		if(mCount > 1) {
			//Back button
			Tile pic = engine.getTile(BACKBUTTON);

			int shade = 4 + mulscale(16, sintable[(20 * totalclock) & 2047], 16);
			engine.rotatesprite(0, (ydim-mulscale(pic.getHeight(), zoom, 16))<<16, zoom, 0, BACKBUTTON, shade, 0, 8|16, 0, 0, mulscale(zoom, pic.getWidth()-1, 16), ydim-1);
		}
	}

	@Override
	public boolean mCheckBackButton(int mx, int my) {
		int zoom = scale(BACKSCALE, ydim, 200);
		Tile pic = engine.getTile(BACKBUTTON);
		int size = mulscale(pic.getWidth(), zoom, 16);
		int bx = 0;
		int by = ydim - mulscale(pic.getHeight(), zoom, 16);
		if(mx >= bx && mx < bx + size)
			if(my >= by && my < by + size)
				return true;
		return false;
	}

	@Override
	public void mSound(MenuItem item, MenuOpt opt) {
		switch(opt)
		{
		case Open:
		case Close:
		case UP:
		case DW:
		case LEFT:
		case RIGHT:
		case ENTER:
		case MCHANGE:
			SND_Sound( 85/*engine.rand()%60*/ );
			break;
		default:
			break;
		}
	}

	@Override
	public void mPostDraw(MenuItem item) { /* nothing */ }
}
