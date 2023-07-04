// This file is part of DukeGDX.
// Copyright (C) 2019  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// DukeGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// DukeGDX is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with DukeGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.Duke3D.Factory;

import static ru.m210projects.Build.Engine.sintable;
import static ru.m210projects.Build.Engine.totalclock;
import static ru.m210projects.Build.Engine.xdim;
import static ru.m210projects.Build.Engine.ydim;
import static ru.m210projects.Build.Pragmas.mulscale;
import static ru.m210projects.Build.Pragmas.scale;
import static ru.m210projects.Duke3D.Globals.BACKBUTTON;
import static ru.m210projects.Duke3D.Globals.MOUSECURSOR;
import static ru.m210projects.Duke3D.Globals.ud;
import static ru.m210projects.Duke3D.Names.BIGFNTCURSOR;
import static ru.m210projects.Duke3D.Names.LOADSCREEN;
import static ru.m210projects.Duke3D.Names.SPINNINGNUKEICON;
import static ru.m210projects.Duke3D.Screen.screensize;
import static ru.m210projects.Duke3D.Screen.vscrn;
import static ru.m210projects.Duke3D.SoundDefs.BOS1_WALK;
import static ru.m210projects.Duke3D.SoundDefs.CHAINGUN_FIRE;
import static ru.m210projects.Duke3D.SoundDefs.DUKE_GRUNT;
import static ru.m210projects.Duke3D.SoundDefs.DUKE_LAND_HURT;
import static ru.m210projects.Duke3D.SoundDefs.EXITMENUSOUND;
import static ru.m210projects.Duke3D.SoundDefs.KICK_HIT;
import static ru.m210projects.Duke3D.SoundDefs.LASERTRIP_EXPLODE;
import static ru.m210projects.Duke3D.SoundDefs.NITEVISION_ONOFF;
import static ru.m210projects.Duke3D.SoundDefs.PIPEBOMB_BOUNCE;
import static ru.m210projects.Duke3D.SoundDefs.PIPEBOMB_EXPLODE;
import static ru.m210projects.Duke3D.SoundDefs.PISTOL_BODYHIT;
import static ru.m210projects.Duke3D.SoundDefs.PISTOL_FIRE;
import static ru.m210projects.Duke3D.SoundDefs.PISTOL_RICOCHET;
import static ru.m210projects.Duke3D.SoundDefs.RPG_EXPLODE;
import static ru.m210projects.Duke3D.SoundDefs.RPG_SHOOT;
import static ru.m210projects.Duke3D.SoundDefs.SELECT_WEAPON;
import static ru.m210projects.Duke3D.SoundDefs.SHOTGUN_FIRE;
import static ru.m210projects.Duke3D.SoundDefs.SQUISHED;
import static ru.m210projects.Duke3D.Sounds.sound;

import ru.m210projects.Build.Engine;
import ru.m210projects.Build.Pattern.BuildFont;
import ru.m210projects.Build.Pattern.BuildGame;
import ru.m210projects.Build.Pattern.CommonMenus.MenuRendererSettings;
import ru.m210projects.Build.Pattern.CommonMenus.MenuVideoMode;
import ru.m210projects.Build.Pattern.MenuItems.BuildMenu;
import ru.m210projects.Build.Pattern.MenuItems.MenuButton;
import ru.m210projects.Build.Pattern.MenuItems.MenuConteiner;
import ru.m210projects.Build.Pattern.MenuItems.MenuFileBrowser;
import ru.m210projects.Build.Pattern.MenuItems.MenuHandler;
import ru.m210projects.Build.Pattern.MenuItems.MenuItem;
import ru.m210projects.Build.Pattern.MenuItems.MenuJoyList;
import ru.m210projects.Build.Pattern.MenuItems.MenuKeyboardList;
import ru.m210projects.Build.Pattern.MenuItems.MenuList;
import ru.m210projects.Build.Pattern.MenuItems.MenuResolutionList;
import ru.m210projects.Build.Pattern.MenuItems.MenuSlider;
import ru.m210projects.Build.Pattern.MenuItems.MenuSlotList;
import ru.m210projects.Build.Pattern.MenuItems.MenuSwitch;
import ru.m210projects.Build.Pattern.MenuItems.MenuTextField;
import ru.m210projects.Build.Pattern.MenuItems.MenuVariants;
import ru.m210projects.Build.Types.Tile;
import ru.m210projects.Duke3D.Menus.InterfaceMenu;
import ru.m210projects.Duke3D.Screens.MenuScreen;

public class DukeMenuHandler extends MenuHandler {

	public static final int MAIN = 0;
	public static final int GAME = 1;
	public static final int NEWGAME = 2;
	public static final int SOUNDSET = 3;
	public static final int DIFFICULTY = 4;

	public static final int HELP = 6;
	public static final int LOADGAME = 7;
	public static final int SAVEGAME = 8;
	public static final int QUIT = 9;
	public static final int QUITTITLE = 10;
	public static final int NETWORKGAME = 11;
	public static final int MULTIPLAYER = 12;
	public static final int COLORCORR = 13;
	public static final int NEWADDON = 14;
	public static final int OPTIONS = 15;
	public static final int USERCONTENT = 16;
	public static final int CORRUPTLOAD = 17;

	public BuildMenu[] mMenus;
	private final Engine engine;
	private final BuildGame app;

	private int menunum=0;
	private final short[] menusnds =
    {
        LASERTRIP_EXPLODE,
        DUKE_GRUNT,
        DUKE_LAND_HURT,
        CHAINGUN_FIRE,
        SQUISHED,
        KICK_HIT,
        PISTOL_RICOCHET,
        PISTOL_BODYHIT,
        PISTOL_FIRE,
        SHOTGUN_FIRE,
        BOS1_WALK,
        RPG_EXPLODE,
        PIPEBOMB_BOUNCE,
        PIPEBOMB_EXPLODE,
        NITEVISION_ONOFF,
        RPG_SHOOT,
        SELECT_WEAPON
    };

	public DukeMenuHandler(BuildGame app)
	{
		mMenus = new BuildMenu[19];
		this.engine = app.pEngine;
		this.app = app;
	}

	@Override
	public void mDrawMenu() {
		if(screensize != 0) vscrn(0);
		if(!(app.getScreen() instanceof MenuScreen) && !(app.pMenu.getCurrentMenu() instanceof MenuRendererSettings) && !(app.pMenu.getCurrentMenu() instanceof InterfaceMenu)) {

			int tile = LOADSCREEN;
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
	public void mClose()
	{
		super.mClose();
		vscrn(ud.screen_size);
	}

	@Override
	public int getShade(MenuItem item) {
		int shade = 8;
	    if ( item != null && item.isFocused() )
	    	shade = 8 + mulscale(16, sintable[(32 * totalclock) & 2047], 16);

		return shade;
	}

	@Override
	public int getPal(BuildFont font, MenuItem item) {
		if(item instanceof MenuFileBrowser)
			return 12;

		if(item != null)
		{
			if(!item.isEnabled())
			{
				if(app.getFont(2).equals(font)
						|| (item instanceof MenuSlider && font == null))
					return 1;

				return 16;
			}
		}

		return 10;
	}

	@Override
	public void mPostDraw(MenuItem item) {
		int shade = 8 - (totalclock & 0x3F);
		if ( item.isFocused() ) {
			if(item instanceof MenuButton)
			{
				int scale = 65536;
		    	int px = item.x;
		    	int py = item.y;

		    	int xoff = 0, yoff = 0;
		    	if(item.font.equals(app.getFont(1))) {
		    		scale = 32768;
		    		xoff = -10;
		    		yoff = 3;
		    	}
		    	if(item.font.equals(app.getFont(2))) {
		    		xoff = engine.getTile(BIGFNTCURSOR).getWidth()-4;
		    		yoff = 6;
		    	}

		    	if(item.align == 1) {
		    		int centre = 320>>2;
				    engine.rotatesprite(((320>>1)+(centre>>1)+70)<<16,(py + yoff)<<16,scale,0,SPINNINGNUKEICON+6-((6+(totalclock>>3))%7),shade,0,10,0,0,xdim-1,ydim-1);
				    engine.rotatesprite(((320>>1)-(centre>>1)-70)<<16,(py + yoff)<<16,scale,0,SPINNINGNUKEICON+((totalclock>>3)%7),shade,0,10,0,0,xdim-1,ydim-1);
		    	} else
		    		engine.rotatesprite((px+xoff)<<16,(py + yoff)<<16,scale,0,SPINNINGNUKEICON+(((totalclock>>3))%7),shade,0,10,0,0,xdim-1,ydim-1);
			}

			if(item instanceof MenuVariants)
				engine.rotatesprite(160<<16,(item.y + engine.getTile(BIGFNTCURSOR).getHeight()-4)<<16,65536,0,SPINNINGNUKEICON+(((totalclock>>3))%7),8 - (totalclock & 0x3F),0,2|8,0,0,xdim-1,ydim-1);

			if(item instanceof MenuSwitch || item instanceof MenuSlider || item instanceof MenuTextField || item instanceof MenuConteiner)
			{
				int scale = 65536;
				int px = item.x;
		    	int py = item.y;

		    	int xoff = 0, yoff = 0;
		    	if(item.font.equals(app.getFont(1))) {
		    		scale = 32768;
		    		xoff = -10;
		    		yoff = 3;
		    	}
		    	if(item.font.equals(app.getFont(2))) {
		    		xoff = -20;
		    		yoff = 6;
		    	}

				engine.rotatesprite((px+xoff)<<16,(py+yoff)<<16,scale,0,SPINNINGNUKEICON+(((totalclock>>3))%7),shade,0,10,0,0,xdim-1,ydim-1);
			}


			if(item instanceof MenuList)
			{
				if(item instanceof MenuJoyList)
				{
					MenuList list = (MenuList) item;

					int px = list.x;

					int focus = list.l_nFocus;
					if(list.l_nMin < app.pCfg.joymap.length && focus >= app.pCfg.joymap.length)
						focus++;

					if(focus == -1 || focus < list.l_nMin || focus >= list.l_nMin + list.nListItems)
						return;

					int py = list.y + (focus - list.l_nMin) * (list.mFontOffset());

					engine.rotatesprite((px-10)<<16,(py+3)<<16,32768,0,SPINNINGNUKEICON+(((totalclock>>3))%7),shade,0,10,0,0,xdim-1,ydim-1);
				}
				else if(item instanceof MenuKeyboardList)
				{
					MenuList list = (MenuList) item;

					int px = list.x;

					int focus = list.l_nFocus;
					if(focus == -1 || focus < list.l_nMin || focus >= list.l_nMin + list.nListItems)
						return;

					int py = list.y + (focus - list.l_nMin) * (list.mFontOffset());

					engine.rotatesprite((px-10)<<16,(py+3)<<16,32768,0,SPINNINGNUKEICON+(((totalclock>>3))%7),shade,0,10,0,0,xdim-1,ydim-1);
				}
				else if(item instanceof MenuSlotList)
				{
					MenuSlotList list = (MenuSlotList) item;
					if(list.deleteQuestion)
						engine.rotatesprite(160<<16,130<<16,65536,0,SPINNINGNUKEICON+(((totalclock>>3))%7),shade,0,10,0,0,xdim-1,ydim-1);
					else {
						int px = list.x;
						int focus = list.l_nFocus;
						if(focus == -1 || focus < list.l_nMin || focus >= list.l_nMin + list.nListItems)
							return;

						int py = list.y + (focus - list.l_nMin) * (list.mFontOffset());
						engine.rotatesprite((px+125)<<16,(py+2)<<16,32768,0,SPINNINGNUKEICON+(((totalclock>>3))%7),shade,0,10,0,0,xdim-1,ydim-1);
					}
				}
				else if(item instanceof MenuResolutionList)
				{
					MenuList list = (MenuList) item;

					int px = list.x;

					int focus = list.l_nFocus;
					if(focus == -1 || focus < list.l_nMin || focus >= list.l_nMin + list.nListItems)
						return;

					int py = list.y + (focus - list.l_nMin) * (list.mFontOffset());

					engine.rotatesprite((px)<<16,(py+3)<<16,32768,0,SPINNINGNUKEICON+(((totalclock>>3))%7),shade,0,10,0,0,xdim-1,ydim-1);
				}
				else //select skill list
				{
					MenuList list = (MenuList) item;
					if(list.l_nFocus == -1 || list.l_nFocus < list.l_nMin || list.l_nFocus >= list.l_nMin + list.nListItems) return;

					int px = list.x;
					int py = list.y + (list.l_nFocus - list.l_nMin) * (list.mFontOffset()) + 6;

					int centre = 320>>2;
				    engine.rotatesprite((px + (320>>1)+(centre>>1)+70)<<16,(py)<<16,65536,0,SPINNINGNUKEICON+6-((6+(totalclock>>3))%7),shade,0,10,0,0,xdim-1,ydim-1);
				    engine.rotatesprite((px + (320>>1)-(centre>>1)-70)<<16,(py)<<16,65536,0,SPINNINGNUKEICON+((totalclock>>3)%7),shade,0,10,0,0,xdim-1,ydim-1);
				}
			}
	    }
	}

	@Override
	public void mDrawMouse(int x, int y) {
		if(!app.pCfg.menuMouse) return;

		int zoom = scale(0x10000, ydim, 200);
		int czoom = mulscale(0x8000, mulscale(zoom, app.pCfg.gMouseCursorSize, 16), 16);
		int xoffset = 0; //mulscale(9, czoom, 16);
		int yoffset = 0; //mulscale(9, czoom, 16);
		int ang = 0; //1800;

		engine.rotatesprite((x + xoffset) << 16, (y + yoffset) << 16, czoom, ang, MOUSECURSOR, 0, 0, 8, 0, 0, xdim-1, ydim-1);
	}

	@Override
	public void mDrawBackButton() {
		if(!app.pCfg.menuMouse)
			return;

		int zoom = scale(16384, ydim, 200);
		if(mCount > 1) {
			//Back button
			Tile pic = engine.getTile(BACKBUTTON);

			int shade = 4 + mulscale(16, sintable[(20 * totalclock) & 2047], 16);
			engine.rotatesprite(0, (ydim-mulscale(pic.getHeight(), zoom, 16))<<16, zoom, 0, BACKBUTTON, shade, 0, 8|16, 0, 0, mulscale(zoom, pic.getWidth()-1, 16), ydim-1);
		}
	}

	@Override
	public boolean mCheckBackButton(int mx, int my) {
		int zoom = scale(16384, ydim, 200);
		Tile pic = engine.getTile(BACKBUTTON);
		int size = mulscale(pic.getWidth(), zoom, 16);
		int bx = 0;
		int by = ydim - mulscale(pic.getHeight(), zoom, 16);
		if(mx >= bx && mx < bx + size)
			return my >= by && my < by + size;
		return false;
	}

	@Override
	public void mSound(MenuItem item, MenuOpt opt) {
		switch(opt)
		{
		case Open:
			if(mCount > 1)
				sound(PISTOL_BODYHIT);
		    else {
				sound(menusnds[menunum++]);
			    menunum %= 17;
			}
			break;
		case Close:
			sound(EXITMENUSOUND);
			break;
		case UP:
		case DW:
			sound(KICK_HIT);
			break;
		case LEFT:
		case RIGHT:
		case ENTER:
			if(opt == MenuOpt.ENTER && item instanceof MenuConteiner
				&& item.getClass().getEnclosingClass() == MenuVideoMode.class) {
					break;
			}

			if(item instanceof MenuSlider
					|| item instanceof MenuSwitch
					|| item instanceof MenuConteiner)
				sound(KICK_HIT);

			if(item instanceof MenuFileBrowser && opt != MenuOpt.ENTER)
				sound(KICK_HIT);

			break;
		default:
			break;
		}
	}

}
