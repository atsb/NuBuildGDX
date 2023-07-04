// This file is part of BloodGDX.
// Copyright (C) 2017-2019  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// BloodGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// BloodGDX is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with BloodGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.Blood.Factory;

import static ru.m210projects.Blood.Cheats.IsCheatCode;
import static ru.m210projects.Blood.Cheats.cheatCode;
import static ru.m210projects.Blood.Globals.kNetModeOff;
import static ru.m210projects.Blood.Globals.pGameInfo;
import static ru.m210projects.Blood.Main.gGameScreen;
import static ru.m210projects.Blood.Main.game;
import static ru.m210projects.Build.Engine.xdim;
import static ru.m210projects.Build.OnSceenDisplay.Console.BGCTILE;
import static ru.m210projects.Build.OnSceenDisplay.Console.BGTILE;
import static ru.m210projects.Build.OnSceenDisplay.Console.BGTILE_SIZEX;
import static ru.m210projects.Build.OnSceenDisplay.Console.BGTILE_SIZEY;
import static ru.m210projects.Build.OnSceenDisplay.Console.BITS;
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

import ru.m210projects.Build.Engine;
import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Build.OnSceenDisplay.DEFOSDFUNC;
import ru.m210projects.Build.Pattern.BuildFont.TextAlign;
import ru.m210projects.Build.Pattern.ScreenAdapters.InitScreen;
import ru.m210projects.Build.Types.Tile;

public class BloodOSDFunc extends DEFOSDFUNC {

	public BloodOSDFunc(Engine engine) {
		super(engine);

		BGTILE = 2051;
		BGCTILE = -1;
		BORDTILE = 2051;

		BITSTH = 1+32+8+16;
		BITSTL = 1+8+16;
		BITS = 8+16+64;
		SHADE = 50;
		PALETTE = 5;

		OSDTEXT_RED      = 7;
		OSDTEXT_BLUE     = 10;
		OSDTEXT_GOLD     = 9;
		OSDTEXT_BROWN    = 2;
		OSDTEXT_YELLOW   = 8;
	}

	@Override
	public void drawchar(int x, int y, char ch, int shade, int pal, int scale) {
		x = mulscale(6 * x, scale, 16);
		game.getFont(3).drawChar(x,  mulscale(y << 3, scale, 16), ch, scale, shade, pal, 0, false);
	}

	@Override
	public void drawosdstr(int x, int y, int ptr, int len, int shade, int pal, int scale) {
		short[][] fmt = Console.getFmtPtr();
		char[][] osdtext = Console.getTextPtr();

		if (ptr >= 0 && ptr < osdtext.length) {
			char[] text = osdtext[ptr];
			int pos = 0;
			x = mulscale(6 * x, scale, 16) + 3;

			while (text != null && pos < text.length && text[pos] != 0) {

				shade = ((fmt[ptr][pos]) & ~0x1F) >> 4;
				pal = ((fmt[ptr][pos]) & ~0xE0);

				x += game.getFont(3).drawChar(x, mulscale(y << 3, scale, 16), text[pos], scale, shade, pal, 0, false);

				pos++;
			}
		}
	}

	@Override
	public void drawcursor(int x, int y, int type, int lastkeypress, int scale) {

		x = mulscale(6 * x, scale, 16);
		if ((lastkeypress & 0x40L) == 0) {
			char ch = '_';
			if (type != 0) ch = '#';
			game.getFont(3).drawChar(x,  mulscale(y << 3, scale, 16), ch, scale, 0, 0, 0, false);
		}
	}

	@Override
	public void showosd(int shown) {
		// fix for TCs like Layre which don't have the BGTILE for
		// some reason
		// most of this is copied from my dummytile stuff in defs.c
		Tile pic = engine.getTile(BGTILE);

		if (pic.getWidth() == 0 || pic.getHeight() == 0)
			engine.allocatepermanenttile(BGTILE, BGTILE_SIZEX,
					BGTILE_SIZEY);

		if(game.pMenu == null) return; //not ready to show

		if (!game.pMenu.gShowMenu && !(game.getScreen() instanceof InitScreen)) {
			BuildGdx.input.setCursorCatched(shown == 0);
			game.pInput.resetMousePos();
		}
	}

	@Override
	public int getcolumnwidth(int width) {
		return width / 6;
	}

	@Override
	public int getrowheight(int height) {
		return height >> 3;
	}

	@Override
	public void drawstr(int x, int y, char[] text, int len, int shade, int pal, int scale) {
		game.getFont(3).drawText(xdim - 4, mulscale(y << 3, scale, 16), text, scale, shade, pal, TextAlign.Right, 0, false);
	}

	@Override
	public boolean textHandler(String message) {
		if ( pGameInfo.nGameType != kNetModeOff )
			return false;

		char[] lockeybuf = message.toCharArray();
		int i = 0;
		while (i < lockeybuf.length && lockeybuf[i] != 0)
			lockeybuf[i++] += 1;
		String cheat = new String(lockeybuf).toUpperCase();

		int ep = -1, lvl = -1;
		boolean wrap1 = false;
		boolean wrap2 = false;
	boolean isMario = false;

		if (cheat.startsWith(cheatCode[17])
				|| cheat.startsWith(cheatCode[18])
				|| cheat.startsWith(cheatCode[35])) {

			isMario = true;

			i = 0;
			while (i < message.length() && message.charAt(i) != 0
					&& message.charAt(i) != ' ')
				i++;
			cheat = cheat.substring(0, i);
			message = message.replaceAll("[\\s]{2,}", " ");
			int startpos = ++i;
			while (i < message.length() && message.charAt(i) != 0
					&& message.charAt(i) != ' ')
				i++;

			if (i <= message.length()) {
				String nEpisode = message.substring(startpos, i);
				nEpisode = nEpisode.replaceAll("[^0-9]", "");
				if (!nEpisode.isEmpty()) {
					try {
						ep = Integer.parseInt(nEpisode);
						wrap1 = true;
						startpos = ++i;
						while (i < message.length()
								&& message.charAt(i) != 0
								&& message.charAt(i) != ' ')
							i++;
						if (i <= message.length()) {
							String nLevel = message.substring(
									startpos, i);
							nLevel = nLevel
									.replaceAll("[^0-9]", "");
							if (!nLevel.isEmpty()) {
								lvl = Integer.parseInt(nLevel);
								wrap2 = true;
							}
						}
					} catch (Exception ignored) {
					}
				}
			}
		}

		boolean isCheat = false;
		for (String s : cheatCode)
			if (cheat.equalsIgnoreCase(s)) {
				isCheat = true;
				break;
			}

		if (!game.isCurrentScreen(gGameScreen) && isCheat) {
			Console.Println(message + ": not in a game");
			return true;
		}

		if (wrap1) {
			if (wrap2) {
                return IsCheatCode(cheat, ep, lvl);
			} else {
                return IsCheatCode(cheat, ep);
			}
		} else {
			if(isMario) {
				Console.Println("mario <level> or <episode> <level>");
				return true;
			}

			if(cheat.equalsIgnoreCase(cheatCode[36]) || cheat.equalsIgnoreCase(cheatCode[17])) {
				Console.Println(message + ": level will end");
				IsCheatCode(cheat);
				return true;
			} else return IsCheatCode(cheat);
		}
    }
}
