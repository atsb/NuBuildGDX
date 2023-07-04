package ru.m210projects.Wang.Factory;

import static ru.m210projects.Build.Engine.xdim;
import static ru.m210projects.Build.Net.Mmulti.numplayers;
import static ru.m210projects.Build.OnSceenDisplay.Console.BGCTILE;
import static ru.m210projects.Build.OnSceenDisplay.Console.BGTILE;
import static ru.m210projects.Build.OnSceenDisplay.Console.BGTILE_SIZEX;
import static ru.m210projects.Build.OnSceenDisplay.Console.BGTILE_SIZEY;
import static ru.m210projects.Build.OnSceenDisplay.Console.BITSTH;
import static ru.m210projects.Build.OnSceenDisplay.Console.BORDTILE;
import static ru.m210projects.Build.OnSceenDisplay.Console.OSDTEXT_BLUE;
import static ru.m210projects.Build.OnSceenDisplay.Console.OSDTEXT_BROWN;
import static ru.m210projects.Build.OnSceenDisplay.Console.OSDTEXT_GOLD;
import static ru.m210projects.Build.OnSceenDisplay.Console.OSDTEXT_GREEN;
import static ru.m210projects.Build.OnSceenDisplay.Console.OSDTEXT_GREY;
import static ru.m210projects.Build.OnSceenDisplay.Console.OSDTEXT_RED;
import static ru.m210projects.Build.OnSceenDisplay.Console.OSDTEXT_WHITE;
import static ru.m210projects.Build.OnSceenDisplay.Console.OSDTEXT_YELLOW;
import static ru.m210projects.Build.OnSceenDisplay.Console.PALETTE;
import static ru.m210projects.Build.Pragmas.mulscale;
import static ru.m210projects.Wang.Cheats.handleCheat;

import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Build.OnSceenDisplay.DEFOSDFUNC;
import ru.m210projects.Build.Pattern.BuildFont.TextAlign;
import ru.m210projects.Build.Pattern.BuildGame;
import ru.m210projects.Build.Pattern.ScreenAdapters.InitScreen;
import ru.m210projects.Build.Types.Tile;

public class WangOSDFunc extends DEFOSDFUNC {

	protected BuildGame game;

	public WangOSDFunc(BuildGame game) {
		super(game.pEngine);
		this.game = game;

		BGTILE = 220;
		BGCTILE = 2366;
		BORDTILE = 0;

		BITSTH = 1 + 8 + 16;
		PALETTE = 4;

		OSDTEXT_WHITE = 4;
		OSDTEXT_RED = 14;
		OSDTEXT_YELLOW = 20;
		OSDTEXT_GOLD = 30;
		OSDTEXT_BROWN = 21;
		OSDTEXT_GREY = 26;
		OSDTEXT_BLUE = 28;
		OSDTEXT_GREEN = 27;
	}

	@Override
	public void showosd(int shown) {
		// fix for TCs like Layre which don't have the BGTILE for
		// some reason
		// most of this is copied from my dummytile stuff in defs.c
		Tile pic = engine.getTile(BGTILE);
		if (!pic.hasSize())
			engine.allocatepermanenttile(BGTILE, BGTILE_SIZEX, BGTILE_SIZEY);

		if (!(game.getScreen() instanceof InitScreen) && !game.pMenu.gShowMenu) {
			BuildGdx.input.setCursorCatched(shown == 0);
			game.pInput.resetMousePos();
		}
	}

	@Override
	public int getcolumnwidth(int width) {
		return (int) (width / 9.1f);
	}

	@Override
	public int getrowheight(int height) {
		return height >> 3;
	}

	@Override
	public void drawcursor(int x, int y, int type, int lastkeypress, int scale) {
		x = mulscale(9 * x, scale, 16);
		if ((lastkeypress & 0x40l) == 0) {
			char ch = '_';
			if (type != 0)
				ch = '#';
			game.getFont(1).drawChar(x, mulscale(y << 3, scale, 16), ch, scale, 0, 0, 0, false);
		}
	}

	@Override
	public void drawchar(int x, int y, char ch, int shade, int pal, int scale) {
		x = mulscale(9 * x, scale, 16);
		game.getFont(1).drawChar(x, mulscale(y << 3, scale, 16), ch, scale, shade, pal, 0, false);
	}

	@Override
	public void drawosdstr(int x, int y, int ptr, int len, int shade, int pal, int scale) {
		short[][] fmt = Console.getFmtPtr();
		char[][] osdtext = Console.getTextPtr();

		if (ptr >= 0 && ptr < osdtext.length) {
			char[] text = osdtext[ptr];
			int pos = 0;
			x = mulscale(9 * x, scale, 16) + 3;

			while (text != null && pos < text.length && text[pos] != 0) {

				shade = ((fmt[ptr][pos]) & ~0x1F) >> 4;
				pal = ((fmt[ptr][pos]) & ~0xE0);

				x += game.getFont(1).drawChar(x, mulscale((y << 3) + 3, scale, 16), Character.toUpperCase(text[pos]),
						scale, shade, pal, 0, false);

				pos++;
			}
		}
	}

	@Override
	public void drawstr(int x, int y, char[] text, int len, int shade, int pal, int scale) {
		for (int i = 0; i < text.length && text[i] != 0; i++)
			text[i] = Character.toUpperCase(text[i]);
		game.getFont(1).drawText(xdim - 4, mulscale(y << 3, scale, 16), text, scale, shade, pal, TextAlign.Right, 0,
				false);
	}

	@Override
	public boolean textHandler(String message) {
		if (numplayers > 1)
			return false;
		return handleCheat(message);
	}
}
