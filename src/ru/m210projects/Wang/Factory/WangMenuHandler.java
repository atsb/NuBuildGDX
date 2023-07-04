package ru.m210projects.Wang.Factory;

import static ru.m210projects.Build.Engine.sintable;
import static ru.m210projects.Build.Engine.totalclock;
import static ru.m210projects.Build.Engine.xdim;
import static ru.m210projects.Build.Engine.ydim;
import static ru.m210projects.Build.Pragmas.mulscale;
import static ru.m210projects.Build.Pragmas.scale;
import static ru.m210projects.Wang.Digi.DIGI_STAR;
import static ru.m210projects.Wang.Digi.DIGI_STARCLINK;
import static ru.m210projects.Wang.Digi.DIGI_SWORDSWOOSH;
import static ru.m210projects.Wang.Game.BACKBUTTON;
import static ru.m210projects.Wang.Game.MOUSECURSOR;
import static ru.m210projects.Wang.Gameutils.ROTATE_SPRITE_SCREEN_CLIP;
import static ru.m210projects.Wang.Names.COINCURSOR;
import static ru.m210projects.Wang.Sound.PlaySound;
import static ru.m210projects.Wang.Sound.v3df_dontpan;

import ru.m210projects.Build.Engine;
import ru.m210projects.Build.Pattern.BuildFont;
import ru.m210projects.Build.Pattern.BuildGame;
import ru.m210projects.Build.Pattern.CommonMenus.MenuRendererSettings;
import ru.m210projects.Build.Pattern.MenuItems.BuildMenu;
import ru.m210projects.Build.Pattern.MenuItems.MenuHandler;
import ru.m210projects.Build.Pattern.MenuItems.MenuItem;
import ru.m210projects.Build.Pattern.MenuItems.MenuJoyList;
import ru.m210projects.Build.Pattern.MenuItems.MenuKeyboardList;
import ru.m210projects.Build.Pattern.MenuItems.MenuList;
import ru.m210projects.Build.Pattern.MenuItems.MenuResolutionList;
import ru.m210projects.Build.Pattern.MenuItems.MenuSlotList;
import ru.m210projects.Build.Pattern.MenuItems.MenuVariants;
import ru.m210projects.Build.Types.Tile;
import ru.m210projects.Wang.Main;
import ru.m210projects.Wang.Menus.MenuCorruptGame;
import ru.m210projects.Wang.Menus.MenuDifficulty;
import ru.m210projects.Wang.Menus.MenuGame;
import ru.m210projects.Wang.Menus.MenuInterface;
import ru.m210projects.Wang.Menus.MenuLastLoad;
import ru.m210projects.Wang.Menus.MenuLoad;
import ru.m210projects.Wang.Menus.MenuMain;
import ru.m210projects.Wang.Menus.MenuNewAddon;
import ru.m210projects.Wang.Menus.MenuNewGame;
import ru.m210projects.Wang.Menus.MenuOptions;
import ru.m210projects.Wang.Menus.MenuQuit;
import ru.m210projects.Wang.Menus.MenuSave;
import ru.m210projects.Wang.Menus.MenuSound;
import ru.m210projects.Wang.Menus.Network.MenuMultiplayer;
import ru.m210projects.Wang.Menus.Network.MenuNetwork;
import ru.m210projects.Wang.Screens.MenuScreen;

public class WangMenuHandler extends MenuHandler {

	public static final int MAIN = 0;
	public static final int GAME = 1;
	public static final int NEWGAME = 2;
	public static final int SOUNDSET = 3;
	public static final int DIFFICULTY = 4;
	public static final int OPTIONS = 5;
	public static final int HELP = 6;
	public static final int LOADGAME = 7;
	public static final int SAVEGAME = 8;
	public static final int QUIT = 9;
	public static final int QUITTITLE = 10;
	public static final int NETWORKGAME = 11;
	public static final int MULTIPLAYER = 12;
	public static final int COLORCORR = 13;
	public static final int NEWADDON = 14;
	public static final int USERCONTENT = 15;
	public static final int LASTSAVE = 16;
	public static final int CORRUPTLOAD = 17;

	public static final int pic_yinyang = 2870;

	public BuildMenu[] mMenus;
	private Engine engine;
	private BuildGame app;

	public WangMenuHandler(Main app) {
		app.menu = this;
		mMenus = new BuildMenu[19];
		this.engine = app.pEngine;
		this.app = app;

		mMenus[DIFFICULTY] = new MenuDifficulty(app);
		mMenus[NEWGAME] = new MenuNewGame(app);
		mMenus[LOADGAME] = new MenuLoad(app);
		mMenus[SAVEGAME] = new MenuSave(app);
		mMenus[QUIT] = new MenuQuit(app, false);
		mMenus[QUITTITLE] = new MenuQuit(app, true);
		mMenus[SOUNDSET] = new MenuSound(app);
		mMenus[OPTIONS] = new MenuOptions(app);
		mMenus[NEWADDON] = new MenuNewAddon(app);
		mMenus[NETWORKGAME] = new MenuNetwork(app);
		mMenus[MULTIPLAYER] = new MenuMultiplayer(app);
		mMenus[LASTSAVE] = new MenuLastLoad(app);
		mMenus[CORRUPTLOAD] = new MenuCorruptGame(app);

		mMenus[MAIN] = new MenuMain(app);
		mMenus[GAME] = new MenuGame(app);
	}

	@Override
	public void mDrawMenu() {
		if (!(app.getScreen() instanceof MenuScreen) && !(app.pMenu.getCurrentMenu() instanceof MenuRendererSettings)
				&& !(app.pMenu.getCurrentMenu() instanceof MenuInterface)) {

			int tile = 2324;
			Tile pic = engine.getTile(tile);

			float kt = xdim / (float) ydim;
			float kv = pic.getWidth() / (float) pic.getHeight();
			float scale;
			if (kv >= kt)
				scale = (ydim + 1) / (float) pic.getHeight();
			else
				scale = (xdim + 1) / (float) pic.getWidth();

			engine.rotatesprite(0, 0, (int) (scale * 65536), 0, tile, 127, 4, 8 | 16 | 1, 0, 0, xdim - 1, ydim - 1);
		}

		super.mDrawMenu();
	}

	@Override
	public int getShade(MenuItem item) {
		if (item != null) {
			if (!item.isEnabled())
				return 16;

			if (item.font.equals(app.getFont(0))) {
				int shade = 8;
				if (item != null && item.isFocused())
					shade = 8 + mulscale(16, sintable[(32 * totalclock) & 2047], 16);
				return shade;
			}
		}
		return 0;
	}

	@Override
	public int getPal(BuildFont font, MenuItem item) {

		// for UserContent list
		if (item != null && item.font.equals(app.getFont(0))) // MiniFont
		{
			if (item.isFocused()) {
				return 24;
			}
		}

		return 16;
	}

	@Override
	public void mPostDraw(MenuItem item) {
		if (item.isFocused()) {
			if (item instanceof MenuSlotList) {
				MenuSlotList list = (MenuSlotList) item;
				if (list.owncursor && list.typing) {
					int px = list.x + list.width / 2 + list.nListOffset + item.font.getWidth(list.typingBuf);
					int focus = list.l_nFocus;
					if (focus == -1 || focus < list.l_nMin || focus >= list.l_nMin + list.nListItems)
						return;

					int py = list.y + (focus - list.l_nMin) * (list.mFontOffset());
					engine.rotatesprite(px + 4 << 16, py + 2 << 16, 20000, 0, COINCURSOR + ((totalclock >> 3) % 7), 0,
							0, ROTATE_SPRITE_SCREEN_CLIP, 0, 0, xdim - 1, ydim - 1);
				}
			} else if (item instanceof MenuList) {
				if (item instanceof MenuJoyList) {
					MenuList list = (MenuList) item;

					int px = list.x;

					int focus = list.l_nFocus;
					if (list.l_nMin < app.pCfg.joymap.length && focus >= app.pCfg.joymap.length)
						focus++;

					if (focus == -1 || focus < list.l_nMin || focus >= list.l_nMin + list.nListItems)
						return;

					int py = list.y + (focus - list.l_nMin) * (list.mFontOffset());

					engine.rotatesprite(px - 6 << 16, py + 3 << 16, 20000, 0, COINCURSOR + ((totalclock >> 3) % 7), 0,
							0, ROTATE_SPRITE_SCREEN_CLIP, 0, 0, xdim - 1, ydim - 1);
				} else if (item instanceof MenuKeyboardList) {
					MenuList list = (MenuList) item;

					int px = list.x;

					int focus = list.l_nFocus;
					if (focus == -1 || focus < list.l_nMin || focus >= list.l_nMin + list.nListItems)
						return;

					int py = list.y + (focus - list.l_nMin) * (list.mFontOffset());

					engine.rotatesprite(px - 6 << 16, py + 3 << 16, 20000, 0, COINCURSOR + ((totalclock >> 3) % 7), 0,
							0, ROTATE_SPRITE_SCREEN_CLIP, 0, 0, xdim - 1, ydim - 1);
				} else if (item instanceof MenuResolutionList) {
					MenuList list = (MenuList) item;

					int px = list.x;

					int focus = list.l_nFocus;
					if (focus == -1 || focus < list.l_nMin || focus >= list.l_nMin + list.nListItems)
						return;

					int py = list.y + (focus - list.l_nMin) * (list.mFontOffset());

					engine.rotatesprite(px + 26 << 16, py + 3 << 16, 20000, 0, COINCURSOR + ((totalclock >> 3) % 7), 0,
							0, ROTATE_SPRITE_SCREEN_CLIP, 0, 0, xdim - 1, ydim - 1);
				} else {
					MenuList list = (MenuList) item;

					Tile pic = engine.getTile(pic_yinyang);

					int scale = 32768;
					int px = list.x;
					int focus = list.l_nFocus;
					if (focus == -1 || focus < list.l_nMin || focus >= list.l_nMin + list.nListItems)
						return;
					int py = list.y + (focus - list.l_nMin) * (list.mFontOffset());
					int xoff = 0, yoff = 0;
					if (item.font.equals(app.getFont(1))) {
						xoff = -(pic.getWidth() / 2) + 10;
						yoff = 3;
					}

					if (item.font.equals(app.getFont(2))) {
						xoff = -(pic.getWidth() / 2) + 10;
						yoff = 8;
					}

					engine.rotatesprite((px + xoff) << 16, (py + yoff) << 16, scale, 0, pic_yinyang, 0, 0, 10, 0, 0,
							xdim - 1, ydim - 1);
				}
			} else if (!(item instanceof MenuVariants)) {
				int scale = 32768;
				int px = item.x;
				int py = item.y;
				int xoff = 0, yoff = 0;
				Tile pic = engine.getTile(pic_yinyang);

				if (item.font.equals(app.getFont(1))) {
					xoff = -(pic.getWidth() / 2) + 10;
					yoff = 3;
				}

				if (item.font.equals(app.getFont(2))) {
					xoff = -(pic.getWidth() / 2) + 10;
					yoff = 8;
				}

				engine.rotatesprite((px + xoff) << 16, (py + yoff) << 16, scale, 0, pic_yinyang, 0, 0, 10, 0, 0,
						xdim - 1, ydim - 1);
			}
		}
	}

	@Override
	public void mDrawMouse(int x, int y) {
		if (!app.pCfg.menuMouse)
			return;

		int zoom = scale(0x10000, ydim, 200);
		int czoom = mulscale(0x8000, mulscale(zoom, app.pCfg.gMouseCursorSize, 16), 16);
		int xoffset = 0;
		int yoffset = 0;
		int ang = 0;

		engine.rotatesprite((x + xoffset) << 16, (y + yoffset) << 16, czoom, ang, MOUSECURSOR, 0, 0, 8, 0, 0, xdim - 1,
				ydim - 1);
	}

	@Override
	public void mDrawBackButton() {
		if (!app.pCfg.menuMouse)
			return;

		Tile pic = engine.getTile(BACKBUTTON);

		int zoom = scale(16384, ydim, 200);
		if (mCount > 1) {
			// Back button
			int shade = 4 + mulscale(16, sintable[(20 * totalclock) & 2047], 16);
			engine.rotatesprite(0, (ydim - mulscale(pic.getHeight(), zoom, 16)) << 16, zoom, 0, BACKBUTTON, shade,
					0, 8 | 16, 0, 0, mulscale(zoom, pic.getWidth() - 1, 16), ydim - 1);
		}
	}

	@Override
	public boolean mCheckBackButton(int mx, int my) {
		int zoom = scale(16384, ydim, 200);
		Tile pic = engine.getTile(BACKBUTTON);

		int size = mulscale(pic.getWidth(), zoom, 16);
		int bx = 0;
		int by = ydim - mulscale(pic.getHeight(), zoom, 16);
		if (mx >= bx && mx < bx + size)
			if (my >= by && my < by + size)
				return true;
		return false;
	}

	@Override
	public void mSound(MenuItem item, MenuOpt opt) {
		switch (opt) {
		case Open:
			if (mCount > 1)
				PlaySound(DIGI_SWORDSWOOSH, null, v3df_dontpan);
			break;
		case ENTER:
			PlaySound(DIGI_SWORDSWOOSH, null, v3df_dontpan);
			break;
		case UP:
		case DW:
		case LEFT:
		case RIGHT:
		case PGUP:
		case PGDW:
		case HOME:
		case END:
		case MCHANGE:
			PlaySound(DIGI_STAR, null, v3df_dontpan);
			break;
		case Close:
			if (mCount == 1)
				PlaySound(DIGI_STARCLINK, null, v3df_dontpan);
			else
				PlaySound(DIGI_SWORDSWOOSH, null, v3df_dontpan);
			break;
		default:
			break;
		}
	}

}
