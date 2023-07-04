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

import static ru.m210projects.Build.Engine.xdim;
import static ru.m210projects.Build.Engine.ydim;
import static ru.m210projects.Build.Net.Mmulti.numplayers;
import static ru.m210projects.Build.OnSceenDisplay.Console.BGCTILE;
import static ru.m210projects.Build.OnSceenDisplay.Console.BGTILE;
import static ru.m210projects.Build.OnSceenDisplay.Console.BGTILE_SIZEX;
import static ru.m210projects.Build.OnSceenDisplay.Console.BGTILE_SIZEY;
import static ru.m210projects.Build.OnSceenDisplay.Console.BITS;
import static ru.m210projects.Build.OnSceenDisplay.Console.BITSTH;
import static ru.m210projects.Build.OnSceenDisplay.Console.BITSTL;
import static ru.m210projects.Build.OnSceenDisplay.Console.BORDERANG;
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
import static ru.m210projects.Build.OnSceenDisplay.Console.SHADE;
import static ru.m210projects.Build.Pragmas.mulscale;
import static ru.m210projects.Duke3D.Cheats.IsCheatCode;
import static ru.m210projects.Duke3D.Cheats.cheatCode;
import static ru.m210projects.Duke3D.Main.gGameScreen;
import static ru.m210projects.Duke3D.Main.game;
import static ru.m210projects.Duke3D.Names.BIGHOLE;
import static ru.m210projects.Duke3D.Names.ENDALPHANUM;
import static ru.m210projects.Duke3D.Names.INGAMEDUKETHREEDEE;
import static ru.m210projects.Duke3D.Names.STARTALPHANUM;
import static ru.m210projects.Duke3D.Names.VIEWBORDER;

import ru.m210projects.Build.Engine;
import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Build.OnSceenDisplay.DEFOSDFUNC;
import ru.m210projects.Build.Pattern.BuildFont.TextAlign;
import ru.m210projects.Build.Pattern.ScreenAdapters.InitScreen;

public class DukeOSDFunc extends DEFOSDFUNC {

	public DukeOSDFunc(Engine engine) {
		super(engine);

		BGTILE = BIGHOLE;
		BGCTILE = INGAMEDUKETHREEDEE;
		BORDTILE = VIEWBORDER;

		BITSTH = 1+8+16;
		BITSTL = 1+8+16;
		BITS = 8+16+64+4;
		BORDERANG = 512;
		SHADE = 10;
		PALETTE = 0;

		OSDTEXT_RED      = 10;
		OSDTEXT_BLUE     = 0;
		OSDTEXT_GOLD     = 7;
		OSDTEXT_WHITE 	 = 12;
		OSDTEXT_GREEN	 = 8;
		OSDTEXT_BROWN    = 15;
		OSDTEXT_YELLOW   = 23;
		OSDTEXT_GREY     = 13;
	}

	@Override
	public void drawchar(int x, int y, char ch, int shade, int pal, int scale) {
		if(ch == 32) return;

		int ac = ch - '!' + STARTALPHANUM;

        if( ac < STARTALPHANUM || ac > ENDALPHANUM )
        	return;

        x = mulscale(9 * x, scale, 16);
        engine.rotatesprite(x<<16,mulscale(y << 3, scale, 16) << 16,scale,0,ac,shade,pal,8 | 16,0,0,xdim-1,ydim-1);
	}

	@Override
	public void drawosdstr(int x, int y, int ptr, int len,
		int shade, int pal, int scale) {

		short[][] fmt = Console.getFmtPtr();
		char[][] osdtext = Console.getTextPtr();

		int ac;
		if (ptr >= 0 && ptr < osdtext.length) {
			char[] text = osdtext[ptr];
			int pos = 0;
			x = mulscale(9 * x, scale, 16) + 3;

			while (text != null && pos < text.length && text[pos] != 0)
		    {
		        if(text[pos] == 32) {x+=3;pos++;continue;}
		        else ac = text[pos] - '!' + STARTALPHANUM;

		        if( ac < STARTALPHANUM || ac > ENDALPHANUM )
		        {x+=3;pos++;continue;}

		        shade = ((fmt[ptr][pos]) & ~0x1F) >> 4;
				pal = ((fmt[ptr][pos]) & ~0xE0);

		        engine.rotatesprite(x<<16,mulscale((y << 3) + 3, scale, 16) << 16,scale,0,ac,shade,pal,8 | 16,0,0,xdim-1,ydim-1);

		        if(text[pos] >= '0' && text[pos] <= '9')
		            x += mulscale(8, scale, 16);
		        else x += mulscale(engine.getTile(ac).getWidth(), scale, 16);

		        pos++;
		    }
		}
	}


	@Override
	public void drawcursor(int x, int y, int type, int lastkeypress, int scale) {
		int nTile = STARTALPHANUM + (('_' - '!') & 0x7F);
		if (type != 0)
			nTile = STARTALPHANUM + (('#' - '!') & 0x7F);

		x = mulscale(9 * x, scale, 16);
		if ((lastkeypress & 0x40L) == 0)
			engine.rotatesprite(x<<16,(mulscale(y << 3, scale, 16) + (type != 0 ? -1
					: 2)) << 16,scale,0,nTile,0,8,8 | 16,0,0,xdim-1,ydim-1);
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

		if (!game.pMenu.gShowMenu && !(game.getScreen() instanceof InitScreen)) {
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
	public void drawstr(int x, int y, char[] text, int len,
			int shade, int pal, int scale) {
		game.getFont(1).drawText(xdim - 4, mulscale(y << 3, scale, 16), text, scale, shade, pal, TextAlign.Right, 0, false);
	}

	@Override
	public boolean textHandler(String message) {
		if ( numplayers > 1 )
			return false;

		char[] lockeybuf = message.toCharArray();
		int i = 0;
		while (i < lockeybuf.length && lockeybuf[i] != 0)
			lockeybuf[i++] += 1;
		String cheat = new String(lockeybuf).toUpperCase();

		int ep = -1, lvl = -1;
		boolean wrap1 = false;
		boolean wrap2 = false;

//		System.err.println("/*" + cheatnum++ + "*/" + "\"" + cheat + "\", // " + message);

		boolean isScotty = false;
		if (cheat.startsWith(cheatCode[2]) || cheat.startsWith(cheatCode[9])) {
			isScotty = true;

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
				if (!IsCheatCode(cheat, ep)) {
					if(isScotty) {
						Console.Println("dnscotty <episode> <level>");
						return true;
					}

					return false;
				}
			}
		} else {
			if(isScotty) {
				Console.Println("dnscotty <episode> <level>");
				return true;
			}

			return IsCheatCode(cheat);
		}
		return true;
	}
}
