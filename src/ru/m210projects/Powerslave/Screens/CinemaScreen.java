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

package ru.m210projects.Powerslave.Screens;

import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Build.Input.Keymap.ANYKEY;
import static ru.m210projects.Powerslave.Palette.*;
import static ru.m210projects.Powerslave.Sound.*;
import static ru.m210projects.Powerslave.Random.*;
import static ru.m210projects.Powerslave.Seq.*;
import static ru.m210projects.Powerslave.Screens.GameScreen.*;
import static ru.m210projects.Powerslave.Cinema.*;
import static ru.m210projects.Powerslave.Globals.*;

import ru.m210projects.Build.Pattern.BuildFont.TextAlign;
import ru.m210projects.Build.Pattern.ScreenAdapters.SkippableAdapter;

import ru.m210projects.Build.Settings.BuildSettings;
import ru.m210projects.Powerslave.Main;

public class CinemaScreen extends SkippableAdapter {

	private Main game;
	private int num;
	private int cinematile;
	private int backgroundCol;
	private int cinematext;
	private long time;

	private int lastScene;
	private int nLastSceneTextIndex;
	private int nCurrentSceneTextIndex;
	private sceneStatus lastStatus;
	private int openClock;
	private int lx, ly;
	private char[] gMessage;
	private int symPosX, symPosY, symCur;
	private int creditsShade;
	private long creditsTime;

	private enum sceneStatus {
		Open, Close, Screen
	};

	public CinemaScreen(Main game) {
		super(game);
		this.game = game;
	}

	@Override
	public void show() {
		lastScene = 0;
		cinematext = -1;
		switch (num) {
		case 1:
			cinematile = 3454;
			cinematext = 4;
			break;
		case 2:
			cinematile = 3452;
			cinematext = 0;
			break;
		case 3:
			cinematile = 3449;
			cinematext = 2;
			break;
		case 4:
			cinematile = 3445;
			cinematext = 7;
			break;
		case 5:
			cinematile = 3451;
			cinematext = 3;
			break;
		case 6:
			cinematile = 3448;
			cinematext = 8;
			break;
		case 7:
			cinematile = 3446;
			cinematext = 6;
			break;
		case 8:
			lastScene = 1;
			engine.setbrightness(BuildSettings.paletteGamma.get(), palette, true);
			backgroundCol = 96;
			break;
		case 9:
			lastScene = 2;
			engine.setbrightness(BuildSettings.paletteGamma.get(), palette, true);
			backgroundCol = 96;
			break;
		}

		StopAllSounds();
		if (lastScene == 0) {
			if (cinematext != -1)
				playCDtrack(cinematext + 2, false);
			LoadCinemaPalette(num);
			engine.setbrightness(BuildSettings.paletteGamma.get(), cinemapal, true);

			backgroundCol = 0;
			int k = 255;
			for (int i = 0; i < 256; i += 3) {
				int j = (cinemapal[3 * i] & 0xFF) + (cinemapal[3 * i + 1] & 0xFF) + (cinemapal[3 * i + 2] & 0xFF);
				if (j < k) {
					k = j;
					backgroundCol = i;
				}
			}

			ReadyCinemaText(cinematext);
			time = System.currentTimeMillis();
		} else if (lastScene == 1) {
			playCDtrack(19, true);

			nCurrentSceneTextIndex = nLastSceneTextIndex = game.FindGString("LASTLEVEL");

			PlayLocalSound(StaticSound[75], 0);

			engine.loadtile(3623);
			engine.getrender().invalidatetile(3623, 0, -1);
			lastStatus = sceneStatus.Open;
			openClock = totalclock + 240;
			lx = 12;
			ly = 16;

			time = System.currentTimeMillis();
		} else if (lastScene == 2) {
			playCDtrack(19, false);

			nCurrentSceneTextIndex = nLastSceneTextIndex = game.FindGString("CREDITS");
			NextCredit();
		}
	}

	@Override
	public void hide() {
		StopAllSounds();
		StopMusic();
		engine.setbrightness(BuildSettings.paletteGamma.get(), palette, true);
	}

	public CinemaScreen setNum(int num) {
		this.num = num;
		return this;
	}

	@Override
	public void skip() {
		if (lastScene != 1)
			super.skip();
		else {
			if (lastStatus != sceneStatus.Close) {
				PlayLocalSound(StaticSound[75], 0);
				lastStatus = sceneStatus.Close;
			} else
				super.skip();
		}
	}

	@Override
	public void draw(float delta) {
		engine.clearview(backgroundCol);
		if (lastScene == 0) {
			engine.rotatesprite(160 << 16, 100 << 16, 65536, 0, cinematile, 0, 0, 2 | 8, 0, 0, xdim - 1, ydim - 1);
			if (cinematext != -1) {
				if (nHeight + nCrawlY > 0 && System.currentTimeMillis() - time >= 120) {
					nCrawlY--;
					time = System.currentTimeMillis();
				}
				AdvanceCinemaText();

				if (nHeight + nCrawlY <= 0 && !MusicPlaying())
					skip();
			}
		} else if (lastScene == 1) {
			switch (lastStatus) {
			case Open:
				if (engine.loadtile(3623) == null) {
					skip();
					return;
				}

				if (totalclock >= openClock) {
					lastStatus = sceneStatus.Screen;
					engine.loadtile(3623);
					engine.getrender().invalidatetile(3623, 0, -1);
					NextScreen();
					break;
				}

				if (System.currentTimeMillis() - time >= 15) {
					if (ly < 116)
						ly += 20;
					else if (lx < 192)
						lx += 20;
					time = System.currentTimeMillis();
				}

				DoStatic(lx, ly);
				break;
			case Close:
				if (lx == 12 && ly == 16) {
					skip();
					break;
				}

				if (System.currentTimeMillis() - time >= 15) {
					if (lx > 20)
						lx -= 20;
					else if (ly > 20)
						ly -= 20;
					time = System.currentTimeMillis();
				}

				DoStatic(lx, ly);
				break;
			case Screen:
				if (System.currentTimeMillis() - time >= 40) {
					if (symCur < gMessage.length) {
						if (game.pInput.ctrlKeyStatusOnce(ANYKEY)) {
							while (nLastSceneTextIndex - nCurrentSceneTextIndex >= 1) {
								while (symCur < gMessage.length)
									symPosX += CopyCharToBitmap(gMessage[symCur++], 3623, symPosX, symPosY);
								NextLine();
							}
							nCurrentSceneTextIndex -= 1; // to avoid skip method
						} else {
							char sym = gMessage[symCur++];
							if (sym != ' ')
								PlayLocalSound(StaticSound[71], 0);
							symPosX += CopyCharToBitmap(sym, 3623, symPosX, symPosY);
						}

						engine.getrender().invalidatetile(3623, 0, -1);
						time = System.currentTimeMillis();
					} else {
						if (nLastSceneTextIndex - nCurrentSceneTextIndex <= 1) {
							if (System.currentTimeMillis() - time >= 10000 || game.pInput.ctrlKeyStatusOnce(ANYKEY)) {
								engine.loadtile(3623);
								engine.getrender().invalidatetile(3623, 0, -1);
								NextScreen();
							}
							if (nCurrentSceneTextIndex == nLastSceneTextIndex) {
								skip();
								break;
							}
						} else
							NextLine();
					}
				}
				engine.rotatesprite(160 << 16, 100 << 16, 65536, 0, 3623, 0, 0, 2 | 8, 0, 0, xdim - 1, ydim - 1);
				break;
			}
		} else // Credits
		{
			if (System.currentTimeMillis() - time >= 30) {
				if (System.currentTimeMillis() - creditsTime <= 6000) {
					if (creditsShade >= 0)
						creditsShade--;
				} else {
					if (creditsShade < 64)
						creditsShade++;
					else if (!NextCredit()) {
						if (!MusicPlaying())
							skip();
					}
				}
				time = System.currentTimeMillis();
			}

			int y = symPosY;
			for (int i = nCurrentSceneTextIndex; i < nLastSceneTextIndex; i++) {
				game.getFont(0).drawText(symPosX, y, gString[i], creditsShade, 0, TextAlign.Center, 2 | 16, false);
				y += (game.getFont(0).getHeight() + 1);
			}
		}
	}

	private boolean NextCredit() {
		int index = nLastSceneTextIndex + 1;
		if (gString[index].equals("END"))
			return false;

		while (gString[index].length() != 0)
			index++;

		creditsShade = 64;
		symPosX = 160;
		symPosY = 100 - ((game.getFont(0).getHeight() + 1) * (index - nLastSceneTextIndex - 1)) / 2;

		nCurrentSceneTextIndex = nLastSceneTextIndex;
		nLastSceneTextIndex = index;
		creditsTime = time = System.currentTimeMillis();
		return true;
	}

	private void NextScreen() {
		int index = nLastSceneTextIndex;
		while (gString[index].length() != 0)
			index++;

		symCur = 0;
		symPosX = 70;
		symPosY = 81 - 4 * (index - nLastSceneTextIndex);
		gMessage = gString[nLastSceneTextIndex].toCharArray();
		nCurrentSceneTextIndex = nLastSceneTextIndex + 1;
		nLastSceneTextIndex = index + 1;
		time = System.currentTimeMillis();
	}

	private void NextLine() {
		symCur = 0;
		symPosX = 70;
		symPosY += 8;
		gMessage = gString[nCurrentSceneTextIndex].toCharArray();
		nCurrentSceneTextIndex++;
		time = System.currentTimeMillis();
	}

	private void DoStatic(int x, int y) {
		RandomLong();

		if (engine.loadtile(3623) == null)
			return;

		int x1 = 160 - x / 2;
		int y1 = 81 - y / 2;
		int x2 = x1 + x;
		int y2 = y1 + y;

		int ptr = 200 * x1 + y1;
		byte[] data = engine.getTile(3623).data;
		for (int i = x1; i < x2; i++) {
			int wptr = ptr;
			for (int j = y1; j < y2; j++)
				data[wptr++] = (byte) (16 * RandomBit());
			ptr += 200;
		}

		engine.getrender().invalidatetile(3623, 0, -1);
		engine.rotatesprite(160 << 16, 100 << 16, 65536, 0, 3623, 0, 0, 2 | 8, 0, 0, xdim - 1, ydim - 1);
	}

	private int CopyCharToBitmap(char ch, int dest, int x, int y) {
		if (ch == ' ')
			return 4;

		int pic = GetSeqPicnum(69, 0, Character.toUpperCase(ch) - ' ') + 102;
		CopyTileToBitmap(pic, dest, x, y);
		return engine.getTile(pic).getWidth() + 1;
	}
}
