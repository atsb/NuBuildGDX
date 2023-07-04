package ru.m210projects.Witchaven.Menu;

import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Build.Gameutils.*;
import static ru.m210projects.Witchaven.Names.*;

import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.Pattern.BuildFont;
import ru.m210projects.Build.Pattern.BuildFont.TextAlign;
import ru.m210projects.Build.Pattern.MenuItems.MenuHandler;
import ru.m210projects.Build.Pattern.MenuItems.MenuItem;
import ru.m210projects.Build.Pattern.MenuItems.MenuProc;
import ru.m210projects.Build.Pattern.MenuItems.MenuHandler.MenuOpt;

import static ru.m210projects.Witchaven.Main.engine;

public class WHSwitch extends MenuItem {

	public int nTile;
	public int nCount;
	public int nValue;
	public MenuProc callback;

	private boolean isTouched;
	private static WHSwitch touchedObj;

	public WHSwitch(Object text, BuildFont font, int x, int y, int nSelect, int nCount, MenuProc callback)
	{
		super(text, font);
		this.x = x;
		this.y = y;
		this.flags = 3 | 4;
		this.width = 320;

		this.nTile = HORNYSKULL4;

		engine.getTile(nTile).anm = 0;
		engine.getTile(nTile + 2).anm = 0;
		this.nCount = nCount;
		this.nValue = nSelect;
		this.callback = callback;
	}

	private final int[][] pics = {
		{ HORNYSKULL1, 20 },
		{ HORNYSKULL2, 20 },
		{ HORNYSKULL3, 25 },
		{ HORNYSKULL4, 25 }
	};

	@Override
	public void draw(MenuHandler handler) {
		int px = x;
		int shade = 0;
		int pal = this.pal;
	    if ( m_pMenu.mGetFocusedItem(this) ) {
	     	shade = 16 - (totalclock & 0x3F);
	     	pal = 20;
	    }

		int stat = 2 | 8 | 16;

		if ( text != null ) {
	        px = width / 2 + x - font.getWidth(text) / 2;

	        font.drawText(px, y, text, shade, pal, TextAlign.Left, 2, false);
		}

		int posX = width / 2 + x - (nCount - 1) * (pics[nCount - 1][1]) / 2;
		int varY = y + 25;
		for(int i = 0; i < nCount; i++) {
			stat = 2 | 8;
			if(i != nValue - 1)
				stat |= 33;

			engine.rotatesprite(posX<<16,varY<<16,32768,0,pics[i][0],0,0, stat, 0,0,xdim-1,ydim-1);
			posX += pics[i][1];
		}
	}

	@Override
	public boolean callback(MenuHandler handler, MenuOpt opt) {
		switch(opt)
		{
			case LEFT:
				if ( (flags & 4) == 0 ) return false;
				nValue = BClipLow(nValue-1, 1);
				break;
			case RIGHT:
				if ( (flags & 4) == 0 ) return false;
				nValue = BClipHigh(nValue+1, nCount);
				break;
			case LMB:
				if ( (flags & 4) == 0 ) return false;

				if(touchedObj == this)
				{
					if(callback != null)
						callback.run(handler, this);
				}
			default:
				return m_pMenu.mNavigation(opt);
		}
		if(callback != null)
			callback.run(handler, this);
		return false;
	}

	@Override
	public boolean mouseAction(int mx, int my) {
		int posY = y + 25;

		isTouched = false;
		if(!BuildGdx.input.isTouched() && touchedObj != this)
			touchedObj = null;

		if(my > posY - engine.getTile(HORNYSKULL1).getHeight() / 2 && my < posY + engine.getTile(HORNYSKULL1).getHeight() / 2)
		{
			int posX = width / 2 + x - (nCount - 1) * (pics[nCount - 1][1]) / 2;
			for(int i = 0; i < nCount; i++) {
				if(mx > posX - engine.getTile(pics[i][0]).getWidth() / 2 && mx < posX + engine.getTile(pics[i][0]).getWidth() / 2)
				{
					if(BuildGdx.input.isTouched()) {
						nValue = i + 1;
						isTouched = true;
						if(BuildGdx.input.isTouched())
							touchedObj = this;
					}
				}

				posX += pics[i][1];
			}
		}

		return isTouched;
	}

	@Override
	public void open() {

	}

	@Override
	public void close() {

	}

}

