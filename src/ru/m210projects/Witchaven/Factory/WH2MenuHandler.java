package ru.m210projects.Witchaven.Factory;

import static ru.m210projects.Build.Engine.sintable;
import static ru.m210projects.Build.Engine.totalclock;
import static ru.m210projects.Build.Engine.xdim;
import static ru.m210projects.Build.Engine.ydim;
import static ru.m210projects.Build.Pragmas.mulscale;
import static ru.m210projects.Build.Pragmas.scale;
import static ru.m210projects.Witchaven.Globals.BACKBUTTON;
import static ru.m210projects.Witchaven.Globals.BACKSCALE;
import static ru.m210projects.Witchaven.Globals.MOUSECURSOR;
import static ru.m210projects.Witchaven.Globals.WH2FONTBACKGROUND;
import static ru.m210projects.Witchaven.Names.HELMET;
import static ru.m210projects.Witchaven.Names.MAINMENU;
import static ru.m210projects.Witchaven.WHSND.SND_Sound;

import ru.m210projects.Build.Pattern.BuildFont;
import ru.m210projects.Build.Pattern.BuildGame;
import ru.m210projects.Build.Pattern.CommonMenus.MenuRendererSettings;
import ru.m210projects.Build.Pattern.MenuItems.MenuButton;
import ru.m210projects.Build.Pattern.MenuItems.MenuConteiner;
import ru.m210projects.Build.Pattern.MenuItems.MenuItem;
import ru.m210projects.Build.Pattern.MenuItems.MenuJoyList;
import ru.m210projects.Build.Pattern.MenuItems.MenuKeyboardList;
import ru.m210projects.Build.Pattern.MenuItems.MenuList;
import ru.m210projects.Build.Pattern.MenuItems.MenuResolutionList;
import ru.m210projects.Build.Pattern.MenuItems.MenuSlider;
import ru.m210projects.Build.Pattern.MenuItems.MenuSwitch;
import ru.m210projects.Build.Pattern.MenuItems.MenuTextField;
import ru.m210projects.Build.Pattern.MenuItems.MenuVariants;
import ru.m210projects.Build.Types.Tile;
import ru.m210projects.Witchaven.Fonts.MenuFont;
import ru.m210projects.Witchaven.Menu.MenuInterfaceSet;
import ru.m210projects.Witchaven.Screens.MenuScreen;

public class WH2MenuHandler extends WHMenuHandler {

	public WH2MenuHandler(BuildGame app) {
		super(app);
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

		if(mMenuHistory[0] != null) {
			MenuItem item = mMenuHistory[0].m_pItems[mMenuHistory[0].m_nFocus];
			if(item instanceof MenuButton)
			{
				if(item.align == 1) {
					int scale = 32768;
			    	int py = item.y;
			    	int yoff = 5;
			    	int posx = (320>>1);
			    	engine.rotatesprite(posx<<16,(py + yoff)<<16,scale,0,WH2FONTBACKGROUND,0,0,10,0,0,xdim-1,ydim-1);

			    	//	    	engine.rotatesprite((px+xoff)<<16,(py + yoff)<<16,scale,0,9310,0,0,11,0,0,xdim-1,ydim-1);
			    	//
			    	//	    	int width = item.font.getWidth(item.text);
			    	//	    	int x = 0;
			    	//	    	while(x < width)
			    	//	    	{
			    	//	    		x += tilesizx[9311] / 4;
			    	//	    		engine.rotatesprite((px+xoff+x)<<16,(py + yoff)<<16,scale,0,9311,0,0,11,0,0,xdim-1,ydim-1);
			    	//	    	}
			    	//	       	engine.rotatesprite((px+xoff+x)<<16,(py + yoff)<<16,scale,0,9312,0,0,11,0,0,xdim-1,ydim-1);
				} else {
					int scale = 0x2000;
			    	int px = item.x;
			    	int py = item.y;
			    	int xoff = -10, yoff = 3;

			    	engine.rotatesprite((px + xoff)<<16,(py + yoff)<<16,scale,0,HELMET,0,0,10,0,0,xdim-1,ydim-1);
				}
			}
			else if(item instanceof MenuSwitch || item instanceof MenuSlider || item instanceof MenuConteiner || item instanceof MenuTextField)
			{
				int scale = 0x2000;
		    	int px = item.x;
		    	int py = item.y;
		    	int xoff = -10, yoff = 3;

		    	engine.rotatesprite((px + xoff)<<16,(py + yoff)<<16,scale,0,HELMET,0,0,10,0,0,xdim-1,ydim-1);
			}
			else if(item instanceof MenuVariants)
			{
				int scale = 0x4000;
		    	int px = item.x;
		    	int py = item.y;
		    	int xoff = 0, yoff = 16;

		    	engine.rotatesprite((px + xoff)<<16,(py + yoff)<<16,scale,0,HELMET,0,0,10,0,0,xdim-1,ydim-1);
			} else if(item instanceof MenuList)
				{
					if(item instanceof MenuJoyList)
					{
						MenuList list = (MenuList) item;

						int px = list.x;

						int focus = list.l_nFocus;
						if(list.l_nMin < app.pCfg.joymap.length && focus >= app.pCfg.joymap.length)
							focus++;

						if(focus != -1 && focus >= list.l_nMin && focus < list.l_nMin + list.nListItems) {
							int py = list.y + (focus - list.l_nMin) * (list.mFontOffset());

							int scale = 0x2000;
					    	int xoff = -11, yoff = 2;

					    	engine.rotatesprite((px + xoff)<<16,(py + yoff)<<16,scale,0,HELMET,0,0,10,0,0,xdim-1,ydim-1);
						}
					}
					else if(item instanceof MenuKeyboardList)
					{
						MenuList list = (MenuList) item;

						int px = list.x;

						int focus = list.l_nFocus;
						if(focus != -1 && focus >= list.l_nMin && focus < list.l_nMin + list.nListItems)
						{
							int py = list.y + (focus - list.l_nMin) * (list.mFontOffset());

							int scale = 0x2000;
					    	int xoff = -11, yoff = 2;

					    	engine.rotatesprite((px + xoff)<<16,(py + yoff)<<16,scale,0,HELMET,0,0,10,0,0,xdim-1,ydim-1);
						}
					}
					else if(item instanceof MenuResolutionList)
					{
						MenuList list = (MenuList) item;

						int px = list.x;

						int focus = list.l_nFocus;
						if(focus != -1 && focus >= list.l_nMin && focus < list.l_nMin + list.nListItems) {
							int py = list.y + (focus - list.l_nMin) * (list.mFontOffset());

							int scale = 0x2000;
					    	int xoff = 0, yoff = 2;

					    	engine.rotatesprite((px + xoff)<<16,(py + yoff)<<16,scale,0,HELMET,0,0,10,0,0,xdim-1,ydim-1);
						}
					}
		    }
		}


		mMenuDraw();
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
				if(item instanceof MenuButton && item.align == 1)
					{
					if(!item.isEnabled())
						return 6;
					else return 8;
					}
				else return 7;
			}

			if(!item.isEnabled())
				return 6;

			if(font != null && font.getClass().equals(MenuFont.class))
				return item.pal;
			return 0;
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
//		case MWUP:
//		case MWDW:
		case MCHANGE:
			SND_Sound(59);
			break;
		default:
			break;
		}
	}

	@Override
	public void mPostDraw(MenuItem item) { /*nothing*/ }
}
