package ru.m210projects.Tekwar.Factory;

import static ru.m210projects.Build.OnSceenDisplay.Console.*;
import static ru.m210projects.Build.Pragmas.mulscale;
import static ru.m210projects.Tekwar.Names.*;
import static ru.m210projects.Tekwar.Main.*;

import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Build.OnSceenDisplay.DEFOSDFUNC;
import ru.m210projects.Build.Pattern.BuildGame;
import ru.m210projects.Build.Pattern.ScreenAdapters.InitScreen;

public class TekwarOSDFunc extends DEFOSDFUNC {

	protected BuildGame game;
	public TekwarOSDFunc(BuildGame game) {
		super(game.pEngine);
		this.game = game;

		BGTILE = BACKGROUND;
		BGCTILE = DEMOSIGN;
		BORDTILE = BOUNCYMAT;

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

		if(game.pMenu == null) return; //not ready to show
		if (!game.pMenu.gShowMenu && !game.getScreen().equals(gMissionScreen) && !(game.getScreen() instanceof InitScreen)) {
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
//				engine.getrender().printext(Engine.pTextfont, x, (y << 3) + 3, charbuf, colorswap(pal), shade, Transparent.None, scale / 65536.0f);

				engine.printext256(x, mulscale((y<<3) + 3, scale, 16), colorswap(pal), -1, charbuf, 0, scale / 65536.0f);
				x += mulscale(8, scale, 16);
			}
		}
	}

	@Override
	public void drawstr(int x, int y, char[] text, int len, int shade, int pal, int scale) {
		engine.printext256(mulscale(4+(x<<3), scale, 16),mulscale(y<<3, scale, 16), colorswap(pal), -1, text, 0, scale / 65536.0f);

//		engine.getrender().printext(Engine.pTextfont, 4+(x<<3),(y<<3), text, colorswap(pal), shade, Transparent.None, scale / 65536.0f);
	}

	private int colorswap(int col)
	{
		switch(col) {
			case 1: //OSDTEXT_RED
				return 80;
			case 2: //OSDTEXT_BLUE
				return 112;
			case 3: //OSDTEXT_GOLD
				return 144;
			case 4: //OSDTEXT_BROWN
				return 231;
			case 5: //OSDTEXT_YELLOW
				return 202;
			case 6: //GREEN
				return 175;
		}
		return 30; //WHITE
	}
}
