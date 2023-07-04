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

import static ru.m210projects.Build.Engine.sintable;
import static ru.m210projects.Build.Engine.totalclock;
import static ru.m210projects.Build.Engine.xdim;
import static ru.m210projects.Build.Engine.ydim;
import static ru.m210projects.Powerslave.LoadSave.*;
import static ru.m210projects.Powerslave.Cinema.*;
import static ru.m210projects.Powerslave.Main.engine;
import static ru.m210projects.Powerslave.Main.gGameScreen;
import static ru.m210projects.Powerslave.Sound.*;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.ScreenAdapter;

import ru.m210projects.Build.Pattern.BuildGame;
import ru.m210projects.Build.Settings.BuildConfig.GameKeys;
import ru.m210projects.Build.Settings.BuildConfig.MenuKeys;
import ru.m210projects.Powerslave.Main;

public class MapScreen extends ScreenAdapter {

	protected BuildGame game;

	class TileInfo {
		public short nTile, x, y;

		public TileInfo(int nTile, int x, int y) {
			this.nTile = (short) nTile;
			this.x = (short) x;
			this.y = (short) y;
		}
	}

	class NameInfo {
		public short x, y;
		public TileInfo[] Back = new TileInfo[2];
		public TileInfo Name;

		public NameInfo(int x, int y, TileInfo[] Back, TileInfo Name) {
			this.x = (short) x;
			this.y = (short) y;
			this.Back = Back;
			this.Name = Name;
		}
	}

	class FireAnim {
		public TileInfo[] Frame = new TileInfo[4];

		public FireAnim(TileInfo[] Frame) {
			this.Frame = Frame;
		}
	}

	class FireInfo {
		public short nFire;
		public TileInfo[] Fire;

		public FireInfo(int nFire, TileInfo[] Fire) {
			this.nFire = (short) nFire;
			this.Fire = Fire;
		}
	}

	class WorldStruct {
		public byte[] Shifts =
				{ 0, 50, 10, 20, 0, 45, (byte) 236, 20, 5, 0, (byte) 246, 10, 30, (byte) 236, 0, 20, 0, 0, 0, 0 };
		public NameInfo[] Titles = new NameInfo[20];
		public FireAnim[] FAnims = new FireAnim[3];
		public FireInfo[] Fires = new FireInfo[20];

		public WorldStruct() {
			Titles[0] = new NameInfo(100, 170, new TileInfo[] { new TileInfo(3376, 0, 0), new TileInfo(3377, 0, 0) },
					new TileInfo(3411, 18, 6));
			Titles[1] = new NameInfo(230, 10, new TileInfo[] { new TileInfo(3378, 0, 0), new TileInfo(3379, 0, 0) },
					new TileInfo(3414, 18, 6));
			Titles[2] = new NameInfo(180, 125, new TileInfo[] { new TileInfo(3380, 0, 0), new TileInfo(3381, 0, 0) },
					new TileInfo(3417, 18, 6));
			Titles[3] = new NameInfo(10, 95, new TileInfo[] { new TileInfo(3382, 0, 0), new TileInfo(3383, 0, 0) },
					new TileInfo(3420, 18, 6));
			Titles[4] = new NameInfo(210, 160, new TileInfo[] { new TileInfo(3384, 0, 0), new TileInfo(3385, 0, 0) },
					new TileInfo(3423, 18, 6));
			Titles[5] = new NameInfo(10, 110, new TileInfo[] { new TileInfo(3371, 0, 0), new TileInfo(3386, 0, 0) },
					new TileInfo(3426, 18, 6));
			Titles[6] = new NameInfo(10, 50, new TileInfo[] { new TileInfo(3387, 0, 0), new TileInfo(3388, 0, 0) },
					new TileInfo(3429, 18, 6));
			Titles[7] = new NameInfo(140, 0, new TileInfo[] { new TileInfo(3389, 0, 0), new TileInfo(3390, 0, 0) },
					new TileInfo(3432, 18, 6));
			Titles[8] = new NameInfo(30, 20, new TileInfo[] { new TileInfo(3391, 0, 0), new TileInfo(3392, 0, 0) },
					new TileInfo(3435, 18, 6));
			Titles[9] = new NameInfo(200, 150, new TileInfo[] { new TileInfo(3409, 0, 0), new TileInfo(3410, 0, 0) },
					new TileInfo(3418, 20, 4));
			Titles[10] = new NameInfo(145, 170, new TileInfo[] { new TileInfo(3393, 0, 0), new TileInfo(3394, 0, 0) },
					new TileInfo(3438, 18, 6));
			Titles[11] = new NameInfo(80, 80, new TileInfo[] { new TileInfo(3395, 0, 0), new TileInfo(3396, 0, 0) },
					new TileInfo(3441, 18, 6));
			Titles[12] = new NameInfo(15, 0, new TileInfo[] { new TileInfo(3397, 0, 0), new TileInfo(3398, 0, 0) },
					new TileInfo(3444, 18, 5));
			Titles[13] = new NameInfo(220, 35, new TileInfo[] { new TileInfo(3399, 0, 0), new TileInfo(3400, 0, 0) },
					new TileInfo(3447, 18, 6));
			Titles[14] = new NameInfo(190, 40, new TileInfo[] { new TileInfo(3401, 0, 0), new TileInfo(3402, 0, 0) },
					new TileInfo(3450, 18, 6));
			Titles[15] = new NameInfo(20, 130, new TileInfo[] { new TileInfo(3403, 0, 0), new TileInfo(3404, 0, 0) },
					new TileInfo(3453, 19, 6));
			Titles[16] = new NameInfo(220, 160, new TileInfo[] { new TileInfo(3405, 0, 0), new TileInfo(3406, 0, 0) },
					new TileInfo(3456, 18, 6));
			Titles[17] = new NameInfo(20, 10, new TileInfo[] { new TileInfo(3407, 0, 0), new TileInfo(3408, 0, 0) },
					new TileInfo(3459, 18, 6));
			Titles[18] = new NameInfo(200, 10, new TileInfo[] { new TileInfo(3412, 0, 0), new TileInfo(3413, 0, 0) },
					new TileInfo(3419, 18, 5));
			Titles[19] = new NameInfo(20, 10, new TileInfo[] { new TileInfo(3415, 0, 0), new TileInfo(3416, 0, 0) },
					new TileInfo(3421, 19, 4));

			FAnims[0] = new FireAnim(new TileInfo[] { new TileInfo(3484, 0, 3), new TileInfo(3485, 0, 0),
					new TileInfo(3486, 0, 3), new TileInfo(3487, 0, 0) });
			FAnims[1] = new FireAnim(new TileInfo[] { new TileInfo(3488, 1, 0), new TileInfo(3489, 1, 0),
					new TileInfo(3490, 0, 1), new TileInfo(3491, 1, 1) });
			FAnims[2] = new FireAnim(new TileInfo[] { new TileInfo(3492, 1, 2), new TileInfo(3493, 1, 0),
					new TileInfo(3494, 1, 2), new TileInfo(3495, 1, 0) });

			Fires[0] = new FireInfo(3,
					new TileInfo[] { new TileInfo(0, 107, 95), new TileInfo(1, 58, 140), new TileInfo(2, 28, 38) });
			Fires[1] = new FireInfo(3,
					new TileInfo[] { new TileInfo(2, 240, 0), new TileInfo(0, 237, 32), new TileInfo(1, 200, 30) });
			Fires[2] = new FireInfo(2,
					new TileInfo[] { new TileInfo(2, 250, 57), new TileInfo(0, 250, 43), new TileInfo(2, 200, 70) });
			Fires[3] = new FireInfo(2,
					new TileInfo[] { new TileInfo(1, 82, 59), new TileInfo(2, 84, 16), new TileInfo(0, 10, 95) });
			Fires[4] = new FireInfo(2,
					new TileInfo[] { new TileInfo(2, 237, 50), new TileInfo(1, 215, 42), new TileInfo(1, 210, 50) });
			Fires[5] = new FireInfo(3,
					new TileInfo[] { new TileInfo(0, 40, 7), new TileInfo(1, 75, 6), new TileInfo(2, 100, 10) });
			Fires[6] = new FireInfo(3,
					new TileInfo[] { new TileInfo(0, 58, 61), new TileInfo(1, 85, 80), new TileInfo(2, 111, 63) });
			Fires[7] = new FireInfo(3,
					new TileInfo[] { new TileInfo(0, 260, 65), new TileInfo(1, 228, 0), new TileInfo(2, 259, 15) });
			Fires[8] = new FireInfo(2,
					new TileInfo[] { new TileInfo(0, 81, 38), new TileInfo(2, 58, 38), new TileInfo(2, 30, 20) });
			Fires[9] = new FireInfo(3,
					new TileInfo[] { new TileInfo(0, 259, 49), new TileInfo(1, 248, 76), new TileInfo(2, 290, 65) });
			Fires[10] = new FireInfo(3,
					new TileInfo[] { new TileInfo(2, 227, 66), new TileInfo(0, 224, 98), new TileInfo(1, 277, 30) });
			Fires[11] = new FireInfo(2,
					new TileInfo[] { new TileInfo(0, 100, 10), new TileInfo(2, 48, 76), new TileInfo(2, 80, 80) });
			Fires[12] = new FireInfo(3,
					new TileInfo[] { new TileInfo(0, 17, 2), new TileInfo(1, 29, 49), new TileInfo(2, 53, 28) });
			Fires[13] = new FireInfo(3,
					new TileInfo[] { new TileInfo(0, 266, 42), new TileInfo(1, 283, 99), new TileInfo(2, 243, 108) });
			Fires[14] = new FireInfo(2,
					new TileInfo[] { new TileInfo(0, 238, 19), new TileInfo(2, 240, 92), new TileInfo(2, 190, 40) });
			Fires[15] = new FireInfo(2,
					new TileInfo[] { new TileInfo(0, 27, 0), new TileInfo(1, 70, 40), new TileInfo(0, 20, 130) });
			Fires[16] = new FireInfo(3,
					new TileInfo[] { new TileInfo(0, 275, 65), new TileInfo(1, 235, 8), new TileInfo(2, 274, 6) });
			Fires[17] = new FireInfo(3,
					new TileInfo[] { new TileInfo(0, 75, 45), new TileInfo(1, 152, 105), new TileInfo(2, 24, 68) });
			Fires[18] = new FireInfo(3,
					new TileInfo[] { new TileInfo(0, 290, 25), new TileInfo(1, 225, 63), new TileInfo(2, 260, 110) });
			Fires[19] = new FireInfo(0,
					new TileInfo[] { new TileInfo(1, 20, 10), new TileInfo(1, 20, 10), new TileInfo(1, 20, 10) });
		}
	}

	private WorldStruct WMap = new WorldStruct();
	private int nLast, nNext, nComplete;
	private float nCurrentPos;
	private final int nSize = 200;

	public MapScreen(BuildGame game) {
		this.game = game;
	}

	@Override
	public void render(float delta) {
		engine.clearview(96);

		if (nNext != -1) 
			DrawTheMap(delta);

		engine.sampletimer();
		engine.nextpage();
	}
	
	private Runnable show = new Runnable() {
		@Override
		public void run() {
			StopAllSounds();
			playCDtrack(19, true);
			game.changeScreen(MapScreen.this);
		}
	};
			

	public void showMap(int nLevel, int nNewLevel, int nBest) {
		this.nLast = Math.max(nLevel - 1, 0);
		this.nNext = Math.max(nNewLevel - 1, 0);
		this.nComplete = nBest;
		this.nCurrentPos = (WMap.Shifts[nLast]) + nSize * (nLast / 2);
		
		if (nNewLevel == 11 || !CheckBeforeScene(nNewLevel, show))
			show.run();
	}

	private void DrawTheMap(float delta) {
		int lastmap = nLast - 1;
		if (lastmap < 20 && nNext < 20) {
			if (lastmap < 0)
				lastmap = 0;
			if (nNext < 0)
				nNext = lastmap;
			int nNextPosition = WMap.Shifts[nNext] + nSize * (nNext / 2);

			int pos = (int) nCurrentPos;
			for (int i = 0; i < 10; i++) {
				engine.rotatesprite(0, pos << 16, 65536, 0, 3353 + i, 0, 0, 2 | 8 | 16, 0, 0, xdim - 1, ydim - 1);
				pos -= nSize;
			}

			int velocity = 0;
			if (nCurrentPos < nNextPosition)
				velocity = 2;
			if (nCurrentPos > nNextPosition)
				velocity = -2;

			for (int i = 0; i < 20; i++) {
				int yoffs = (int) nCurrentPos - nSize * (i >> 1);
				if (nComplete > i) {
					FireInfo pFire = WMap.Fires[i];
					for (int j = 0; j < pFire.nFire; j++) {
						FireAnim pAnim = WMap.FAnims[pFire.Fire[j].nTile];
						int tile = pAnim.Frame[(totalclock >> 4) & 3].nTile;
						int x = pFire.Fire[j].x + pAnim.Frame[(totalclock >> 4) & 3].x;
						int y = pFire.Fire[j].y + pAnim.Frame[(totalclock >> 4) & 3].y + yoffs;

						engine.rotatesprite(x << 16, y << 16, 65536, 0, tile, 0, 0, 2 | 8 | 16, 0, 0, xdim - 1,
								ydim - 1);
					}
				}

				NameInfo pInfo = WMap.Titles[i];
				TileInfo pTile = pInfo.Back[(totalclock & 0x10) >> 4];

				engine.rotatesprite((pTile.x + pInfo.x) << 16, (pTile.y + pInfo.y + yoffs) << 16, 65536, 0, pTile.nTile,
						0, 0, 2 | 8 | 16, 0, 0, xdim - 1, ydim - 1);

				byte shade = 96;
				if (nNext == i)
					shade = (byte) ((sintable[16 * (totalclock & 0x7F)] + 31) >> 8);
				else if (nComplete > i)
					shade = 31;

				engine.rotatesprite((pInfo.Name.x + pInfo.x) << 16, (pInfo.Name.y + pInfo.y + yoffs) << 16, 65536, 0,
						pInfo.Name.nTile, shade, 0, 2 | 8 | 16, 0, 0, xdim - 1, ydim - 1);
			}

			if (nCurrentPos == nNextPosition) {
				if (game.pInput.ctrlGetInputKey(GameKeys.Move_Forward, true) || game.pInput.ctrlKeyStatusOnce(Keys.UP) || game.pInput.ctrlPadStatusOnce(MenuKeys.Menu_Up)) {
					if (nNext < nComplete) {
						nNext++;
					}
				}

				if (game.pInput.ctrlGetInputKey(GameKeys.Move_Backward, true) || game.pInput.ctrlKeyStatusOnce(Keys.DOWN) || game.pInput.ctrlPadStatusOnce(MenuKeys.Menu_Down)) {
					if (nNext > 0) {
						nNext--;
					}
				}

				if (game.pInput.ctrlGetInputKey(GameKeys.Open, true) || game.pInput.ctrlKeyStatusOnce(Keys.ENTER) || game.pInput.ctrlKeyStatusOnce(Keys.SPACE)) {
					final int nLevel = nNext + 1;
					if (nLevel == 11)
						CheckBeforeScene(11, new Runnable() {
							@Override
							public void run() {
								gAutosaveRequest = true;
								gGameScreen.changemap(nLevel, null);
							}
						});
					else {
//						if(PlayerCount == 0)
//							gGameScreen.newgame(nLevel);
//						else {
						if(nLevel > 1 || gClassicMode)
							gAutosaveRequest = true;
						gGameScreen.changemap(nLevel, null);
//						}
					}
					nNext = -1;
				}
			} else {
				nCurrentPos += velocity * delta * 64;
				if (nCurrentPos > nNextPosition && velocity > 0)
					nCurrentPos = nNextPosition;
				if (nCurrentPos < nNextPosition && velocity < 0)
					nCurrentPos = nNextPosition;
			}
		} else {
			((Main) game).EndGame();
		}
	}
}
