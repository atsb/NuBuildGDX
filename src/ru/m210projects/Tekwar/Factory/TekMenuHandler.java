package ru.m210projects.Tekwar.Factory;

import static ru.m210projects.Build.Engine.sintable;
import static ru.m210projects.Build.Engine.totalclock;
import static ru.m210projects.Build.Engine.xdim;
import static ru.m210projects.Build.Engine.ydim;
import static ru.m210projects.Build.Pragmas.mulscale;
import static ru.m210projects.Build.Pragmas.scale;
import static ru.m210projects.Tekwar.Globals.BACKBUTTON;
import static ru.m210projects.Tekwar.Globals.BACKSCALE;
import static ru.m210projects.Tekwar.Globals.MOUSECURSOR;
import static ru.m210projects.Tekwar.Globals.ST_IMMEDIATE;
import static ru.m210projects.Tekwar.Names.MENUPANEL4801;
import static ru.m210projects.Tekwar.Names.MENUPANEL4802;
import static ru.m210projects.Tekwar.Names.S_BEEP;
import static ru.m210projects.Tekwar.Names.S_BOOP;
import static ru.m210projects.Tekwar.Names.S_MENUSOUND1;
import static ru.m210projects.Tekwar.Names.S_MENUSOUND2;
import static ru.m210projects.Tekwar.Teksnd.playsound;

import ru.m210projects.Build.Engine;
import ru.m210projects.Build.Pattern.BuildFont;
import ru.m210projects.Build.Pattern.BuildGame;
import ru.m210projects.Build.Pattern.CommonMenus.MenuRendererSettings;
import ru.m210projects.Build.Pattern.CommonMenus.MenuVideoMode;
import ru.m210projects.Build.Pattern.MenuItems.BuildMenu;
import ru.m210projects.Build.Pattern.MenuItems.MenuConteiner;
import ru.m210projects.Build.Pattern.MenuItems.MenuHandler;
import ru.m210projects.Build.Pattern.MenuItems.MenuItem;
import ru.m210projects.Build.Pattern.MenuItems.MenuSlider;
import ru.m210projects.Build.Pattern.MenuItems.MenuSwitch;
import ru.m210projects.Build.Types.Tile;
import ru.m210projects.Tekwar.Menus.MenuInterfaceSet;

public class TekMenuHandler extends MenuHandler {

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
	public static final int LASTSAVE = 12;
	public static final int CORRUPTLOAD = 13;

	public BuildMenu[] mMenus;

	private Engine engine;

	private BuildGame app;

	public TekMenuHandler(BuildGame app, int nMaxMenus) {
		mMenus = new BuildMenu[nMaxMenus];
		this.engine = app.pEngine;
		this.app = app;
	}

	@Override
	public void mDrawMenu() {
		if(!(app.pMenu.getCurrentMenu() instanceof MenuRendererSettings) && !(app.pMenu.getCurrentMenu() instanceof MenuInterfaceSet)) {
			int tile = 321;
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

		if(!(app.pMenu.getCurrentMenu() instanceof MenuInterfaceSet)) {
			engine.rotatesprite(160<<16,60<<16, 32768, 0, MENUPANEL4801, 0, 0, 2|8|1024, 0, 0, xdim-1, ydim-1);
        	engine.rotatesprite(160<<16,180<<16, 32768, 0, MENUPANEL4802, 0, 0, 2|8|1024, 0, 0, xdim-1, ydim-1);
		}

		super.mDrawMenu();
	}

	@Override
	public int getShade(MenuItem item) {
		if(item != null && item.isFocused())
			return 16 - (totalclock & 0x3F);
		return 0;
	}

	@Override
	public int getPal(BuildFont font, MenuItem item) {
		if(item != null)
		{
			if(!item.isEnabled())
				return 2;
			if(item.isFocused())
				return 4;
			return item.pal;
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
	public void mPostDraw(MenuItem item) {
    }

	@Override
	public void mSound(MenuItem item, MenuOpt opt) {
		switch(opt)
		{
		case Open:
			if(mCount == 1)
		    	playsound( S_MENUSOUND1 ,0,0,0,ST_IMMEDIATE);
		    else playsound( S_MENUSOUND2 ,0,0,0,ST_IMMEDIATE);
			break;
		case Close:
			if(mCount == 1)
				playsound( S_MENUSOUND2 ,0,0,0,ST_IMMEDIATE);
			else playsound( S_BEEP ,0,0,0,ST_IMMEDIATE);
			break;
		case UP:
		case DW:
			playsound( S_BOOP ,0,0,0,ST_IMMEDIATE);
			break;
		case LEFT:
		case RIGHT:
		case ENTER:
			if(opt == MenuOpt.ENTER && item instanceof MenuConteiner
				&& item.getClass().getEnclosingClass() == MenuVideoMode.class) {
					break;
			}

			if(item instanceof MenuSlider || item instanceof MenuSwitch || item instanceof MenuConteiner)
				playsound( S_MENUSOUND1 ,0,0,0,ST_IMMEDIATE);
			break;
		default:
			break;
		}
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
}
