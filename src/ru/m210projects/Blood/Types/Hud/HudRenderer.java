// This file is part of BloodGDX.
// Copyright (C) 2017-2020  Alexander Makarov-[M210] (m210-2007@mail.ru)
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

package ru.m210projects.Blood.Types.Hud;

import static ru.m210projects.Blood.Globals.kAngle180;
import static ru.m210projects.Blood.Globals.kAngleMask;
import static ru.m210projects.Blood.Globals.kNetModeBloodBath;
import static ru.m210projects.Blood.Globals.kNetModeTeams;
import static ru.m210projects.Blood.Globals.kRotateCorner;
import static ru.m210projects.Blood.Globals.kRotateStatus;
import static ru.m210projects.Blood.Globals.pGameInfo;
import static ru.m210projects.Blood.LEVELS.foundSecret;
import static ru.m210projects.Blood.LEVELS.kills;
import static ru.m210projects.Blood.LEVELS.totalKills;
import static ru.m210projects.Blood.LEVELS.totalSecrets;
import static ru.m210projects.Blood.Main.cfg;
import static ru.m210projects.Blood.Main.engine;
import static ru.m210projects.Blood.Main.game;
import static ru.m210projects.Blood.PLAYER.kInventoryMax;
import static ru.m210projects.Blood.Strings.killsstat2;
import static ru.m210projects.Blood.Strings.secretsstat;
import static ru.m210projects.Blood.View.gViewMode;
import static ru.m210projects.Blood.View.kView2DIcon;
import static ru.m210projects.Build.Engine.xdim;
import static ru.m210projects.Build.Engine.ydim;
import static ru.m210projects.Build.Gameutils.coordsConvertXScaled;
import static ru.m210projects.Build.Pragmas.mulscale;
import static ru.m210projects.Build.Pragmas.scale;
import static ru.m210projects.Build.Strhandler.Bitoa;
import static ru.m210projects.Build.Strhandler.Bsprintf;

import ru.m210projects.Blood.PLAYER;
import ru.m210projects.Build.Gameutils.ConvertType;
import ru.m210projects.Build.Pattern.BuildFont.TextAlign;
import ru.m210projects.Build.Types.Tile;

public abstract class HudRenderer {

	protected static final char[] number_buffer = new char[256];
	public static final int[] viewInventoryTile = { 2569, 2564, 2566, 2568, 2560, 2563, 2567 };
	public final int kBigRed = 2190;
	public final int kBigBlue = 2240;
	public final int kLittleWhite = 2230;
	public final int kLittleRed = 2250;

	// engine.rotatesprite compatible flags
	public final int kDrawNormal = 0;
	public final int kDrawTranslucent = 0x0001;
	public final int kDrawScale = 0x0002;
	public final int kDrawYFlip = 0x0004;
	public final int kDrawUnclipped = 0x0008;
	public final int kDrawStatus = 0x000A;
	public final int kDrawCorner = 0x0010;
	public final int kDrawTranslucentR = 0x0020;
	public final int kDrawNonMasked = 0x0040;
	public final int kDrawMultiPage = 0x0080;

	// viewDrawSprite specific flags
	public final int kDrawXFlip = 0x0800;

	public abstract void draw(PLAYER gView, int x, int y);

	protected int getPalette(PLAYER gView) {
		int pal = 0;
		if (pGameInfo.nGameType == kNetModeTeams) {
			if ((gView.teamID & 1) != 0)
				pal = 7;
			else
				pal = 10;
		}

		return pal;
	}

	public void DrawStatNumber(int slen, int n, int nTile, int x, int y, int nShade, int nPLU, int nFlags) {
		Bsprintf(number_buffer, slen, n, 1);
		Tile pic = engine.getTile(nTile);
		for (int i = 0; i < slen; i++) {
			if (number_buffer[i] != ' ')
				DrawStatMaskedSprite(nTile + number_buffer[i] - '0', x, y, nShade, nPLU, nFlags);
			x += pic.getWidth() + 1;
		}
	}

	public void DrawStatNumber(int slen, int n, int nTile, int x, int y, int nShade, int nPLU, int nFlags,
			int nScale) {
		Bsprintf(number_buffer, slen, n, 1);
		Tile pic = engine.getTile(nTile);
		for (int i = 0; i < slen; i++) {
			if (number_buffer[i] != ' ')
				DrawStatSprite(nTile + number_buffer[i] - '0', x, y, nShade, nPLU, nFlags, nScale);
			x += mulscale(pic.getWidth() + 1, nScale, 16);
		}
	}

	public void DrawStatSprite(int nTile, int x, int y, int nShade, int nPLU, int nFlag, int nScale) {
		engine.rotatesprite(x << 16, y << 16, nScale, 0, nTile, nShade, nPLU, nFlag | kRotateStatus, 0, 0,
				xdim - 1, ydim - 1);
	}

	public void DrawStatMaskedSprite(int nTile, int x, int y, int nShade, int nPLU, int nFlags) {
		// convert x-flipping
		int nAngle = 0;
		if ((nFlags & kDrawXFlip) != 0) {
			nAngle = (nAngle + kAngle180) & kAngleMask;
			nFlags ^= kDrawYFlip;
		}

		engine.rotatesprite(x << 16, y << 16, 0x10000, nAngle, (short) nTile, nShade, nPLU, nFlags | kRotateStatus,
				0, 0, xdim - 1, ydim - 1);
	}

	public void DrawStatMaskedSprite(int nTile, int x, int y, int nShade, int nPLU, int nFlags, float cx1, float cy1, float cx2, float cy2) {
		// convert x-flipping
		int nAngle = 0;
		if ((nFlags & kDrawXFlip) != 0) {
			nAngle = (nAngle + kAngle180) & kAngleMask;
			nFlags ^= kDrawYFlip;
		}

		engine.rotatesprite(x << 16, y << 16, 0x10000, nAngle, (short) nTile, nShade, nPLU, nFlags | kRotateStatus,
				(int) cx1, (int)cy1, (int)cx2, (int)cy2);
	}

	public void showInventoryItems(PLAYER pPlayer, int sx, int sy, int fx, int fy, int nFlags) {
		if (pPlayer.choosedInven >= 0) {
			DrawStatMaskedSprite(viewInventoryTile[pPlayer.choosedInven], sx, sy, 0, 0, nFlags);
			DrawStatNumber(3, pPlayer.Inventory[pPlayer.choosedInven].amount, kLittleRed, fx, fy, 4, 0, nFlags);
		}
	}

	private final int[] avaibleInventory = new int[kInventoryMax];

	public void viewShowInventory(PLAYER gView, int x, int y, int nFlags) {
		for (int i = 0; i < kInventoryMax; i++)
			avaibleInventory[i] = 0;

		int shade = 24;

		if (gView.showInventory != 0) {
			int wx = 0;
			int invcount = 0;
			for (int i = 0; i < kInventoryMax; i++) {
				if (gView.Inventory[i].amount != 0) {
					avaibleInventory[invcount++] = i;
					wx += engine.getTile(viewInventoryTile[i]).getWidth() + 1;
				}
			}
			x -= (wx / 2);
			for (int i = 0; i < invcount; i++) {
				int xoffset = 1;
				int yoffset = 8;
				DrawStatMaskedSprite(2568, (x + xoffset), y - yoffset, 0, 0, nFlags);
				yoffset = 6;
//				if (nScale != 65536)
//					yoffset = mulscale(yoffset, nScale, 16);
				DrawStatMaskedSprite(2568, x + xoffset, y - yoffset, 0, 0, nFlags);
				yoffset = 1;
//				if (nScale != 65536)
//					yoffset = mulscale(yoffset, nScale, 16);
				DrawStatMaskedSprite(viewInventoryTile[avaibleInventory[i]], x + xoffset, y + yoffset, 0, 0, nFlags);
				if (avaibleInventory[i] == gView.choosedInven)
					DrawStatMaskedSprite(2559, x + xoffset, y + yoffset, 0, 0, nFlags);// ramka inventory

				if (gView.Inventory[avaibleInventory[i]].activated)
					shade = 4;
				else shade = 24;

				yoffset = 13;
				xoffset = 4;
				DrawStatNumber(3, gView.Inventory[avaibleInventory[i]].amount, 2250, (x - xoffset), y - yoffset, shade, 0, nFlags);

				xoffset = engine.getTile(viewInventoryTile[avaibleInventory[i]]).getWidth() + 1;
				x += xoffset;
			}
		}
	}

	public void TileHGauge(int nTile, int x, int y, int n, int total, int nFlags) {
		int nGauge = n * engine.getTile(nTile).getWidth() / total;
		ConvertType type = ConvertType.Normal;
		if((nFlags & 256) != 0)
			type = ConvertType.AlignLeft;
		if((nFlags & 512) != 0)
			type = ConvertType.AlignRight;
		if((nFlags & 1024) != 0)
			type = ConvertType.Stretch;


		engine.rotatesprite(x << 16, y << 16, 0x10000, 0, nTile, 0, 0, kRotateStatus | kRotateCorner | nFlags, 0,
				0, coordsConvertXScaled(x + nGauge, type), ydim - 1);
	}

	protected void viewDrawStats(int x, int y, int zoom) {
		if (cfg.gShowStat == 0 || pGameInfo.nGameType >= kNetModeBloodBath || (cfg.gShowStat == 2 && gViewMode != kView2DIcon))
			return;

		float viewzoom = (zoom / 65536.0f);

		Bitoa(kills, number_buffer);
		int alignx = game.getFont(3).getWidth(number_buffer);
		int yoffset = (int) (2 * (game.getFont(3).getHeight() - 1) * viewzoom);
		y -= yoffset;

		int statx = x;
		int staty = y;
		game.getFont(3).drawText(statx, staty, killsstat2, zoom, 32, 7, TextAlign.Left, 256 | 2, false);
		game.getFont(3).drawText(statx += 9 * viewzoom, staty, number_buffer, zoom, 32, 0, TextAlign.Left, 256 | 2, false);
		game.getFont(3).drawChar(statx += (alignx + 2) * viewzoom, staty, '/', zoom, 32, 0, 256 | 2, false);
		Bitoa(totalKills, number_buffer);
		game.getFont(3).drawText(statx += 8 * viewzoom, staty, number_buffer, zoom, 32, 0, TextAlign.Left, 256 | 2, false);

		statx = x;
		staty = y + (int) (8 * viewzoom);
		Bitoa(foundSecret, number_buffer);
		alignx = game.getFont(3).getWidth(number_buffer);
		game.getFont(3).drawText(statx, staty, secretsstat, zoom, 32, 7, TextAlign.Left, 256 | 2, false);
		game.getFont(3).drawText(statx += 9 * viewzoom, staty, number_buffer, zoom, 32, 0, TextAlign.Left, 256 | 2,
				false);
		game.getFont(3).drawChar(statx += (alignx + 2) * viewzoom, staty, '/', zoom, 32, 0, 256 | 2, false);
		Bitoa(totalSecrets, number_buffer);
		game.getFont(3).drawText(statx += 8 * viewzoom, staty, number_buffer, zoom, 32, 0, TextAlign.Left, 256 | 2, false);
	}

	protected void viewDrawStats(int yoffs) {
//		if (gViewMode != kView2DIcon)
			viewDrawStats(5, 160 + yoffs, cfg.gStatSize);
//		else viewDrawStats(mulscale(5, cfg.gStatSize, 16), mulscale(40, cfg.gStatSize, 16), cfg.gStatSize);
	}
}
