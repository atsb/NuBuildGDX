package ru.m210projects.Witchaven.Factory;

import static ru.m210projects.Build.Engine.xdim;
import static ru.m210projects.Build.OnSceenDisplay.Console.BGCTILE;
import static ru.m210projects.Build.OnSceenDisplay.Console.BGTILE;
import static ru.m210projects.Build.OnSceenDisplay.Console.BGTILE_SIZEX;
import static ru.m210projects.Build.OnSceenDisplay.Console.BGTILE_SIZEY;
import static ru.m210projects.Build.OnSceenDisplay.Console.BITSTH;
import static ru.m210projects.Build.OnSceenDisplay.Console.BITSTL;
import static ru.m210projects.Build.OnSceenDisplay.Console.BORDTILE;
import static ru.m210projects.Build.OnSceenDisplay.Console.OSDTEXT_BLUE;
import static ru.m210projects.Build.OnSceenDisplay.Console.OSDTEXT_BROWN;
import static ru.m210projects.Build.OnSceenDisplay.Console.OSDTEXT_GOLD;
import static ru.m210projects.Build.OnSceenDisplay.Console.OSDTEXT_RED;
import static ru.m210projects.Build.OnSceenDisplay.Console.OSDTEXT_YELLOW;
import static ru.m210projects.Build.OnSceenDisplay.Console.PALETTE;
import static ru.m210projects.Build.OnSceenDisplay.Console.SHADE;
import static ru.m210projects.Build.Pragmas.mulscale;
import static ru.m210projects.Witchaven.Globals.WH2LOGO;
import static ru.m210projects.Witchaven.Globals.WHLOGO;
import static ru.m210projects.Witchaven.Names.BACKGROUND;

import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Build.OnSceenDisplay.DEFOSDFUNC;
import ru.m210projects.Build.Pattern.ScreenAdapters.InitScreen;
import ru.m210projects.Build.Types.Tile;
import ru.m210projects.Witchaven.Main;

public class WHOSDFunc extends DEFOSDFUNC {

	protected Main game;
	public WHOSDFunc(Main game) {
		super(game.pEngine);
		this.game = game;

		BGTILE = BACKGROUND;
		BGCTILE = game.WH2 ? WH2LOGO : WHLOGO; //game.WH2 ? 500 : 679;
		BORDTILE = BACKGROUND;

		BITSTH = 1+8+16;

		OSDTEXT_RED      = 1;
		OSDTEXT_BLUE     = 2;
		OSDTEXT_GOLD     = 3;
		OSDTEXT_BROWN    = 4;
		OSDTEXT_YELLOW   = 5;
	}

	@Override
	public void showosd(int shown) {
		// fix for TCs like Layre which don't have the BGTILE for
		// some reason
		// most of this is copied from my dummytile stuff in defs.c
		if (!engine.getTile(BGTILE).hasSize())
			engine.allocatepermanenttile(BGTILE, BGTILE_SIZEX,
					BGTILE_SIZEY);

		if (!(game.getScreen() instanceof InitScreen) && !game.pMenu.gShowMenu) {
			BuildGdx.input.setCursorCatched(shown == 0);
			game.pInput.resetMousePos();
		}
	}

	@Override
	public void drawosdstr(int x, int y, int ptr, int len, int shade, int pal, int scale) {
		char[][] osdtext = Console.getTextPtr();
		short[][] fmt = Console.getFmtPtr();
		if (ptr >= 0 && ptr < osdtext.length) {
			char[] text = osdtext[ptr];
			int pos = 0;
			x += mulscale(3, scale, 16);
			while (text != null && pos < text.length && text[pos] != 0) {
				pal = ((fmt[ptr][pos]) & ~0xE0);
				charbuf[0] = text[pos++];
				engine.printext256(x, mulscale((y<<3) + 3, scale, 16), colorswap(pal), -1, charbuf, 0, scale / 65536.0f);
				x += mulscale(8, scale, 16);
			}
		}
	}

	@Override
	public void drawstr(int x, int y, char[] text, int len, int shade, int pal, int scale) {
		engine.printext256(mulscale(4+(x<<3), scale, 16),mulscale(y<<3, scale, 16), colorswap(pal), -1, text, 0, scale / 65536.0f);
	}

	@Override
	public void drawlogo(int daydim) {
		if(BGCTILE != -1) {
			Tile pic = engine.getTile(BGCTILE);

			int xsiz = pic.getWidth() / 2;
			int ysiz = pic.getHeight() / 2;

			if (xsiz > 0 && ysiz > 0)
			{
				engine.rotatesprite((xdim - xsiz) << 15,
						(daydim - ysiz) << 16, 32768, 0, BGCTILE,
						SHADE - 32, PALETTE, BITSTL, 0, 0, xdim, daydim);

			}
		}
	}

	private int colorswap(int col)
	{
		switch(col) {
			case 1: //OSDTEXT_RED
				return 170;
			case 2: //OSDTEXT_BLUE
				return 233;
			case 3: //OSDTEXT_GOLD
				return 40;
			case 4: //OSDTEXT_BROWN
				return 37;
			case 5: //OSDTEXT_YELLOW
				return 143;
			case 6: //GREEN
				return 125;
		}
		return 30; //WHITE
	}
}
