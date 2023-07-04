// This file is part of LSPGDX.
// Copyright (C) 2020  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// LSPGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// LSPGDX is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with LSPGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.LSP;

import static ru.m210projects.LSP.Main.*;
import static ru.m210projects.LSP.Globals.*;
import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Build.Gameutils.*;
import static ru.m210projects.Build.Pragmas.*;

import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Build.Pattern.BuildFont.TextAlign;

public class Quotes {
	
	public static class QUOTE {
		public String messageText;
		public int messageTime;
	}
	
	public static int showQuotes = 4;
	public static int quoteTime = 500;
	
	public static int nextTime;
	public static int nextY;
	public static int kMaxQuotes = 16;
	public static int numQuotes;
	public static int hideQuotes;
	public static int totalQuotes;
	public static int QuotesY = 1;
	public static QUOTE[] quotes = new QUOTE[kMaxQuotes];
	public static int yOffset = 11;
	
	public static void InitQuotes() {
		for (int i = 0; i < kMaxQuotes; i++) {
			quotes[i] = new QUOTE();
		}
	}
	
	public static void resetQuotes() {
		numQuotes = 0;
		totalQuotes = 0;
		hideQuotes = 0;
	}
	
	public static QUOTE viewSetMessage(String message) {
		QUOTE quote = quotes[totalQuotes];
		quote.messageText = message;
	
		Console.Println(message);

		quote.messageTime = 4 * quoteTime + lockclock;

		totalQuotes += 1;
		totalQuotes %= kMaxQuotes;
		numQuotes += 1;
		if (numQuotes > showQuotes) {
			hideQuotes += 1;
			hideQuotes %= kMaxQuotes;
			nextTime = 0;
			numQuotes = showQuotes;
			nextY = yOffset;
		}
		return quote;
	}
	
	public static void viewDisplayMessage() {
		if (!cfg.gShowMessages || game.menu.gShowMenu)
			return;

		int x = scale(10, cfg.gHUDSize, 65536), y = scale(10, cfg.gHUDSize, 65536);
		int nShade = BClipHigh(numQuotes << 3, 48);
		y += scale(nextY, cfg.gHUDSize, 65536);

		for (int i = 0; i < numQuotes; i++) {
			QUOTE quote = quotes[(i + hideQuotes) % kMaxQuotes];
			if (lockclock < quote.messageTime) {
				int col = (Math.max(16 + (32 - nShade), 0) * 255 / 48); //48 - max shade (black)
				byte palcol = engine.getclosestcol(curpalette.getBytes(), col, col, col);

				game.getFont(1).drawText(x + 1, y, quote.messageText, cfg.gHUDSize, nShade, palcol, TextAlign.Left, 8 | 256, true);
				y += scale(yOffset, cfg.gHUDSize, 65536);
				nShade = BClipLow(nShade - 64 / numQuotes, -128);
			} else {
				numQuotes--;
				hideQuotes += 1;
				hideQuotes %= kMaxQuotes;
			}
		}
		if (nextY != 0) 
			nextY = nextTime * yOffset / 4;
	}

}
