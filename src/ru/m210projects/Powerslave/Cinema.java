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

package ru.m210projects.Powerslave;

import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Powerslave.Main.*;
import static ru.m210projects.Powerslave.Palette.*;
import static ru.m210projects.Powerslave.Globals.*;
import static ru.m210projects.Powerslave.Sound.*;

import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.Pattern.BuildFont.TextAlign;

import ru.m210projects.Build.Settings.BuildSettings;

public class Cinema {

	private static int line, linecount;
	private static final short[] nBeforeScene = { 0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0 };
	private static final short[] nAfterScene  = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 5, 0, 0, 0, 0, 7, 0, 0, 0, 0, 6 };
	public static int nCrawlY, nHeight, nextclock;
	private static short[] nLeft = new short[50];

	public static void ReadyCinemaText(int a1) {
		line = game.FindGString("CINEMAS");
		if (line >= 0) {
			while (a1 != 0) {
				while (!gString[line].equals("END"))
					++line;
				++line;
				a1--;
			}
			ComputeCinemaText(line);
		}
	}

	public static boolean CheckBeforeScene(int nNewLevel, Runnable run) {
		if (nNewLevel == 20) {
			return DoLastLevelCinema(run);
		} else {
			int num = nBeforeScene[Math.max(nNewLevel - 1, 0)];
			if (num != 0) {
				return GoToTheCinema(num, run);
			}
		}
		
		return false;
	}

	public static boolean DoAfterCinemaScene(int nLevel, Runnable run) {
		int num = nAfterScene[Math.max(nLevel - 1, 0)];
		if (num != 0) {
			return GoToTheCinema(num, run);
		}
		return false;
	}

	public static boolean DoLastLevelCinema(Runnable run) {
		gCinemaScreen.setNum(8).setSkipping(run).escSkipping(true);
		game.changeScreen(gCinemaScreen);
		return true;
	}

	public static boolean GoToTheCinema(int num, Runnable run) {
		gCinemaScreen.setNum(num).setSkipping(run).escSkipping(true);
		game.changeScreen(gCinemaScreen);
		return true;
	}

	public static void ComputeCinemaText(int line) {
		for (linecount = 0;; linecount++) {
			if (gString[line + linecount].equals("END"))
				break;

			nLeft[linecount] = (short) (160 - game.getFont(0).getWidth(gString[line + linecount]) / 2);
		}
		nCrawlY = 199;
		nHeight = (game.getFont(0).getHeight() + 4) * linecount;
		nextclock = totalclock;
	}

	public static boolean AdvanceCinemaText() {
		int height = game.getFont(0).getHeight() + 4;
		for(int i = 0, y = nCrawlY; i < linecount && y <= 199; i++, y += height)
			if (y >= -height) {
				myprintext(nLeft[i], y, gString[i + line], 0);
			}
		return nHeight + nCrawlY > 0;
	}

	public static int myprintext(int x, int y, String text, int shade) {
		if (y < -15 || y >= 200)
	        return 0;
		
		return game.getFont(0).drawText(x, y, text, 65536, 0, 0, TextAlign.Left, 2 | 16, true);
	}
	
	public static void DoFailedFinalScene()
	{
		BuildGdx.app.postRunnable(new Runnable() {
			@Override
			public void run() {
				GoToTheCinema(4, game.rMenu);
			}
		});
	}
	
	public static void DoGameOverScene() {
		BuildGdx.app.postRunnable(new Runnable() {
			@Override
			public void run() {
				LoadCinemaPalette(16);
				PlayGameOverSound();
				engine.setbrightness(BuildSettings.paletteGamma.get(), cinemapal, true);
				game.changeScreen(gLogoScreen.setTime(10.0f).setTile(3591).setCallback(game.rMenu).setSkipping(game.rMenu));
			}
		});
	}
}
