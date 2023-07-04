// This file is part of PowerslaveGDX.
// Copyright (C) 2019  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// PowerslaveGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// PowerslaveGDX is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with PowerslaveGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.Powerslave.Factory;

import static ru.m210projects.Build.Net.Mmulti.numplayers;
import static ru.m210projects.Build.OnSceenDisplay.Console.*;
import static ru.m210projects.Build.Pragmas.mulscale;
import static ru.m210projects.Powerslave.Cheats.*;
import static ru.m210projects.Powerslave.Main.*;
import static ru.m210projects.Powerslave.Globals.*;

import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Build.OnSceenDisplay.DEFOSDFUNC;
import ru.m210projects.Build.Pattern.BuildGame;
import ru.m210projects.Build.Pattern.ScreenAdapters.InitScreen;
import ru.m210projects.Build.Types.Tile;

public class PSOSDFunc extends DEFOSDFUNC {

	protected BuildGame game;
	public PSOSDFunc(BuildGame game) {
		super(game.pEngine);
		this.game = game;

		BGTILE = 1551; //3669;
		BGCTILE = LOGO;
		BORDTILE = 3469;
		PALETTE = 0;

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
		Tile pic = engine.getTile(BGTILE);

		if (!pic.hasSize())
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

	private int colorswap(int col)
	{
		switch(col) {
			case 1: //OSDTEXT_RED
				return 166;
			case 2: //OSDTEXT_BLUE
				return 20;
			case 3: //OSDTEXT_GOLD
				return 180;
			case 4: //OSDTEXT_BROWN
				return 37;
			case 5: //OSDTEXT_YELLOW
				return 175;
			case 6: //GREEN
				return 233;
		}
		return 0; //WHITE
	}

	@Override
	public boolean textHandler(String message) {
		if ( numplayers > 1 )
			return false;

		boolean isCheat = false;
		for (int nCheatCode = 0; nCheatCode < cheatCode.length; nCheatCode++)
			if (message.equalsIgnoreCase(cheatCode[nCheatCode])) {
				isCheat = true;
				break;
			}

		if (!game.isCurrentScreen(gGameScreen) && isCheat) {
			Console.Println(message + ": not in a game");
			return true;
		}

		if (!IsCheatCode(message))
			return false;

		return true;
	}
}
