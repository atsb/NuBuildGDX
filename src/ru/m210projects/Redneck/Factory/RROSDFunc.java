// This file is part of RedneckGDX.
// Copyright (C) 2017-2019  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// RedneckGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// RedneckGDX is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with RedneckGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.Redneck.Factory;

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
import static ru.m210projects.Redneck.Cheats.IsCheatCode;
import static ru.m210projects.Redneck.Cheats.cheatCode;
import static ru.m210projects.Redneck.Main.gGameScreen;
import static ru.m210projects.Redneck.Main.game;
import static ru.m210projects.Redneck.Names.BACKGROUND;
import static ru.m210projects.Redneck.Names.INGAMELNRDTHREEDEE;
import static ru.m210projects.Redneck.Names.VIEWBORDER;

import ru.m210projects.Build.Engine;
import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Build.OnSceenDisplay.DEFOSDFUNC;
import ru.m210projects.Build.Pattern.ScreenAdapters.InitScreen;

public class RROSDFunc extends DEFOSDFUNC {

	public RROSDFunc(Engine engine) {
		super(engine);

		BGTILE = BACKGROUND;
		BGCTILE = INGAMELNRDTHREEDEE;
		BORDTILE = VIEWBORDER;

		BITSTH = 1+8+16;
		BITSTL = 1+8+16+32;
		BITS = 8+16+64+4;
		BORDERANG = 512;
		SHADE = 20;
		PALETTE = 0;

		OSDTEXT_RED      = 1;
		OSDTEXT_BLUE     = 2;
		OSDTEXT_GOLD     = 3;
		OSDTEXT_WHITE 	 = 4;
		OSDTEXT_BROWN    = 5;
		OSDTEXT_YELLOW   = 6;
		OSDTEXT_GREEN	 = 7;
		OSDTEXT_GREY     = 8;
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
				return 143;
			case 2: //OSDTEXT_BLUE
				return 70;
			case 3: //OSDTEXT_GOLD
				return 155;
			case 4: //OSDTEXT_BROWN
				return 50;
			case 5: //OSDTEXT_YELLOW
				return 155;
			case 6: //OSDTEXT_GREEN
				return 127;
			case 7: //OSDTEXT_GREY
				return 10;
		}
		return 30; //WHITE
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

		boolean IsSkillCheat = cheat.startsWith(cheatCode[7]);
		boolean IsSkipMapCheat = cheat.startsWith(cheatCode[10]);

		if (IsSkillCheat || IsSkipMapCheat) {
			boolean bad = false;
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

			if (i <= message.length())
			{
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
						} else if(IsSkipMapCheat) bad = true;
					} catch (Exception e) {
					}
				} else bad = true;
			} else bad = true;


			if(bad)
			{
				if(IsSkipMapCheat)
					Console.Println("rdmeadow [episode] [level]");
				else if(IsSkillCheat)
					Console.Println("rdskill [skill]");
				return true;
			}
		}

		boolean isCheat = false;
		for (int nCheatCode = 0; nCheatCode < cheatCode.length; nCheatCode++)
			if (cheat.equalsIgnoreCase(cheatCode[nCheatCode])) {
				isCheat = true;
				break;
			}

		if (!game.isCurrentScreen(gGameScreen) && isCheat) {
			Console.Println(message + ": not in a game");
			return true;
		}

		if (wrap1) {
			if (wrap2) {
				if (!IsCheatCode(cheat, ep, lvl))
					return false;
			} else {
				if (!IsCheatCode(cheat, ep))
					return false;
			}
		} else {
			if (!IsCheatCode(cheat))
				return false;
		}
		return true;
	}
}
