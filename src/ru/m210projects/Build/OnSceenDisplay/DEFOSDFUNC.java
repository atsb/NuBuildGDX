// On-screen Display function
// for the Build Engine
// by Jonathon Fowler (jf@jonof.id.au)
//
// This file has been modified by Alexander Makarov-[M210] (m210-2007@mail.ru)

package ru.m210projects.Build.OnSceenDisplay;

import static ru.m210projects.Build.Engine.palette;
import static ru.m210projects.Build.Engine.xdim;
import static ru.m210projects.Build.OnSceenDisplay.Console.BGCTILE;
import static ru.m210projects.Build.OnSceenDisplay.Console.BGTILE;
import static ru.m210projects.Build.OnSceenDisplay.Console.BGTILE_SIZEX;
import static ru.m210projects.Build.OnSceenDisplay.Console.BGTILE_SIZEY;
import static ru.m210projects.Build.OnSceenDisplay.Console.BITS;
import static ru.m210projects.Build.OnSceenDisplay.Console.BITSTH;
import static ru.m210projects.Build.OnSceenDisplay.Console.BITSTL;
import static ru.m210projects.Build.OnSceenDisplay.Console.BORDERANG;
import static ru.m210projects.Build.OnSceenDisplay.Console.BORDTILE;
import static ru.m210projects.Build.OnSceenDisplay.Console.PALETTE;
import static ru.m210projects.Build.OnSceenDisplay.Console.SHADE;
import static ru.m210projects.Build.Pragmas.mulscale;

import ru.m210projects.Build.Engine;
import ru.m210projects.Build.Types.Tile;

public class DEFOSDFUNC implements OSDFunc {

	protected Engine engine;
	protected int white = -1;
	protected char[] charbuf = new char[1];
	public DEFOSDFUNC(Engine engine){
		this.engine = engine;
	}

	protected int getwhite() {
		if(white == -1) {
			// find the palette index closest to white
	        int k = 0;
	        for (int i = 0; i < 256; i++)
	        {
	            int j = (palette[3*i]&0xFF)+(palette[3*i+1]&0xFF)+(palette[3*i+2]&0xFF);
	            if (j > k) { k = j; white = i; }
	        }
		}

        return white;
	}

	@Override
	public void drawchar(int x, int y, char ch, int shade, int pal, int scale) {
		x = mulscale(4 + (x << 3), scale, 16);
		y = mulscale((y << 3), scale, 16);


		charbuf[0] = ch;
		engine.printext256(x, y, getwhite(), -1, charbuf, 0, scale / 65536.0f);
	}

	@Override
	public void drawosdstr(int x, int y, int ptr, int len, int shade, int pal, int scale) {
		char[][] osdtext = Console.getTextPtr();
		if (ptr >= 0 && ptr < osdtext.length) {
			char[] text = osdtext[ptr];
			engine.printext256(mulscale(4+(x<<3), scale, 16),mulscale(4+(y<<3), scale, 16), getwhite(), -1, text, 0, scale / 65536.0f);
		}
	}

	@Override
	public void drawstr(int x, int y, char[] text, int len, int shade, int pal, int scale) {
		engine.printext256(mulscale(4+(x<<3), scale, 16),mulscale((y<<3), scale, 16), getwhite(), -1, text, 0, scale / 65536.0f);
	}

	@Override
	public void drawcursor(int x, int y, int type, int lastkeypress, int scale) {
		char ch = '_';
		if(type != 0)
			ch = '#';

		if ((lastkeypress & 0x40l) == 0) {
			charbuf[0] = ch;
			engine.printext256(mulscale(4+(x<<3), scale, 16), mulscale((y<<3) + 2, scale, 16), getwhite(), -1, charbuf, 0, scale / 65536.0f);
		}
	}

	@Override
	public int gettime() {
		return Engine.totalclock;
	}

	@Override
	public long getticksfunc() {
		return engine.getticks();
	}

	@Override
	public void clearbg(int col, int row) {
		int x, y, xsiz, ysiz, tx2, ty2;
		int daydim, bits;

		bits = BITSTH;

		daydim = (row << 3) + 3;

		Tile pic = engine.getTile(BGTILE);

		xsiz = pic.getWidth();
		ysiz = pic.getHeight();

		if (!pic.hasSize())
			return;

		tx2 = xdim / xsiz;
		ty2 = daydim / ysiz;

		for (x = tx2; x >= 0; x--)
			for (y = ty2; y >= 0; y--)
				engine.rotatesprite(x * xsiz << 16, y * ysiz << 16,
						65536, 0, BGTILE, SHADE, PALETTE, bits, 0,
						0, xdim, daydim);

		drawlogo(daydim);

		if(BORDTILE != -1) {
			xsiz = engine.getTile(BORDTILE).getHeight();
			if (xsiz > 0)
			{
				tx2 = xdim / xsiz;

				for (x = tx2; x >= 0; x--)
					engine.rotatesprite(x * xsiz << 16, (daydim - 1) << 16,
							65536, BORDERANG, BORDTILE, SHADE + 12, PALETTE, BITS,
							0, 0, xdim, daydim + 1);
			}
		}
	}

	@Override
	public void showosd(int shown) {
		// fix for TCs like Layre which don't have the BGTILE for
		// some reason
		// most of this is copied from my dummytile stuff in defs.c

		Tile pic = engine.getTile(BGTILE);
		if (pic.getWidth() == 0 || pic.getHeight() == 0)
			engine.allocatepermanenttile(BGTILE, BGTILE_SIZEX, BGTILE_SIZEY);
	}

	@Override
	public int getcolumnwidth(int width) {
		return width/8 - 3;
	}

	@Override
	public int getrowheight(int height) {
		return height/8;
	}

	@Override
	public boolean textHandler(String text) {
		return false;
	}

	@Override
	public void drawlogo(int daydim) {
		if(BGCTILE != -1) {
			Tile pic = engine.getTile(BGCTILE);

			int xsiz = pic.getWidth();
			int ysiz = pic.getHeight();

			if (pic.hasSize())
			{
				engine.rotatesprite((xdim - xsiz) << 15,
						(daydim - ysiz) << 16, 65536, 0, BGCTILE,
						SHADE - 32, PALETTE, BITSTL, 0, 0, xdim, daydim);
			}
		}
	}
}
