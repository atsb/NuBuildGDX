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

import static ru.m210projects.Blood.Globals.*;
import static ru.m210projects.Blood.Globals.kNetModeBloodBath;
import static ru.m210projects.Blood.LEVELS.*;
import static ru.m210projects.Blood.LEVELS.totalSecrets;
import static ru.m210projects.Blood.Main.*;
import static ru.m210projects.Blood.Main.game;
import static ru.m210projects.Blood.Strings.killsstat2;
import static ru.m210projects.Blood.Strings.secretsstat;
import static ru.m210projects.Blood.View.gViewMode;
import static ru.m210projects.Blood.View.kView2DIcon;
import static ru.m210projects.Build.Engine.xdim;
import static ru.m210projects.Build.Engine.ydim;
import static ru.m210projects.Build.Pragmas.*;
import static ru.m210projects.Build.Strhandler.Bitoa;
import static ru.m210projects.Build.Strhandler.Bsprintf;

import ru.m210projects.Blood.PLAYER;
import ru.m210projects.Build.Gameutils.ConvertType;
import ru.m210projects.Build.Pattern.BuildFont;
import ru.m210projects.Build.Types.Tile;

public abstract class HudScaledRenderer extends HudRenderer {

	protected int nScale = 65536;
	public void setScale(int nScale) {
		this.nScale = scale(ydim, nScale, 240);
	}

	@Override
	public abstract void draw(PLAYER gView, int x, int y);

	protected float convertX(float x, int scale, ConvertType type) {
		if (type == ConvertType.AlignLeft)
			return x * scale / 65536.0f;
		if (type == ConvertType.AlignRight)
			return xdim - ((320 - x) * scale / 65536.0f);
		if (type == ConvertType.Normal)
			return (xdim / 2) -  ((160 - x) * scale / 65536.0f);

		return x;
	}

	protected float convertY(float y, int nScale) {
		return ydim - ((200 - y) * nScale / 65536.0f);
	}

	protected ConvertType getType(int nFlags) {
		ConvertType type = ConvertType.Normal;
		if ((nFlags & 256) != 0)
			type = ConvertType.AlignLeft;
		if ((nFlags & 512) != 0)
			type = ConvertType.AlignRight;
		if ((nFlags & 1024) != 0)
			type = ConvertType.Stretch;

		return type;
	}

	@Override
	public void showInventoryItems(PLAYER pPlayer, int sx, int sy, int fx, int fy, int nFlags) {
		if (pPlayer.choosedInven >= 0) {

			DrawStatSprite(viewInventoryTile[pPlayer.choosedInven], sx, sy, 0, 0, nFlags, nScale);
			DrawStatNumber(3, pPlayer.Inventory[pPlayer.choosedInven].amount, kLittleRed, fx, fy, 4, 0, nFlags, nScale);
		}
	}

	@Override
	public void DrawStatNumber(int slen, int n, int nTile, int x, int y, int nShade, int nPLU, int nFlags, int nScale) {
		Bsprintf(number_buffer, slen, n, 1);

		ConvertType type = getType(nFlags);
		y = (int) (convertY(y, nScale) * 65536.0f);
		Tile pic = engine.getTile(nTile);
		for (int i = 0; i < slen; i++) {
			if (number_buffer[i] != ' ')
				engine.rotatesprite((int) (convertX(x, nScale, type) * 65536.0f), y, nScale, 0, nTile + number_buffer[i] - '0',
						nShade, nPLU, nFlags | kRotateUnclipped, 0, 0, xdim - 1, ydim - 1);
			x += pic.getWidth() + 1;
		}
	}

	@Override
	public void DrawStatMaskedSprite(int nTile, int x, int y, int nShade, int nPLU, int nFlags) {
		// convert x-flipping
		int nAngle = 0;
		if ((nFlags & kDrawXFlip) != 0) {
			nAngle = (nAngle + kAngle180) & kAngleMask;
			nFlags ^= kDrawYFlip;
		}

		engine.rotatesprite((int) (convertX(x, nScale, getType(nFlags)) * 65536.0f), (int) (convertY(y, nScale) * 65536.0f), nScale, nAngle, (short) nTile, nShade, nPLU, nFlags | kRotateUnclipped, 0,
				0, xdim - 1, ydim - 1);
	}

	@Override
	public void DrawStatMaskedSprite(int nTile, int x, int y, int nShade, int nPLU, int nFlags, float cx1, float cy1, float cx2, float cy2) {
		// convert x-flipping
		int nAngle = 0;
		if ((nFlags & kDrawXFlip) != 0) {
			nAngle = (nAngle + kAngle180) & kAngleMask;
			nFlags ^= kDrawYFlip;
		}

		engine.rotatesprite((int) (convertX(x, nScale, getType(nFlags)) * 65536.0f), (int) (convertY(y, nScale) * 65536.0f), nScale / 2, nAngle, (short) nTile, nShade, nPLU, nFlags | kRotateUnclipped,
				(int) convertX(cx1, nScale / 2, getType(nFlags)), (int) convertY(cy1, nScale / 2), (int) convertX(cx2, nScale / 2, getType(nFlags)), (int) convertY(cy2, nScale / 2));
	}

	@Override
	public void DrawStatSprite(int nTile, int x, int y, int nShade, int nPLU, int nFlags, int nScale) {
		engine.rotatesprite((int) (convertX(x, nScale, getType(nFlags)) * 65536.0f), (int) (convertY(y, nScale) * 65536.0f), nScale, 0, nTile, nShade, nPLU, nFlags | kRotateUnclipped, 0, 0, xdim - 1,
				ydim - 1);
	}

	@Override
	public void TileHGauge(int nTile, int x, int y, int n, int total, int nFlags) {
		int nGauge = n * engine.getTile(nTile).getWidth() / total;
		engine.rotatesprite((int) (convertX(x, nScale, getType(nFlags)) * 65536.0f), (int) (convertY(y, nScale) * 65536.0f), nScale, 0, nTile, 0,
				0, kRotateUnclipped | kRotateCorner | nFlags, 0, 0, (int) convertX(x + nGauge + 1, nScale, getType(nFlags)),
				ydim - 1);
	}

	@Override
	protected void viewDrawStats(int yoffs) {
		//if (gViewMode != kView2DIcon)
			viewDrawStats((int) convertX(5, nScale, ConvertType.AlignLeft), (int) convertY(160 + yoffs, nScale), cfg.gStatSize);
		//else viewDrawStats(mulscale(5, cfg.gStatSize, 16), mulscale(40, cfg.gStatSize, 16), cfg.gStatSize);
	}

	@Override
	protected void viewDrawStats(int x, int y, int zoom) {
		if (cfg.gShowStat == 0 || pGameInfo.nGameType >= kNetModeBloodBath || (cfg.gShowStat == 2 && gViewMode != kView2DIcon))
			return;

		zoom = scale(ydim, zoom, 240);
		float viewzoom = (zoom / 65536.0f);

		Bitoa(kills, number_buffer);
		int alignx = game.getFont(3).getWidth(number_buffer);
		int yoffset = (int) (2 * (game.getFont(3).getHeight() - 1) * viewzoom);
		y -= yoffset;

		int statx = x;
		int staty = y;
		game.getFont(3).drawText(statx, staty, killsstat2, zoom, 32, 7, BuildFont.TextAlign.Left, 256, false);
		game.getFont(3).drawText(statx += 9 * viewzoom, staty, number_buffer, zoom, 32, 0, BuildFont.TextAlign.Left, 256, false);
		game.getFont(3).drawChar(statx += (alignx + 2) * viewzoom, staty, '/', zoom, 32, 0, 256, false);
		Bitoa(totalKills, number_buffer);
		game.getFont(3).drawText(statx += 8 * viewzoom, staty, number_buffer, zoom, 32, 0, BuildFont.TextAlign.Left, 256, false);

		statx = x;
		staty = y + (int) (8 * viewzoom);
		Bitoa(foundSecret, number_buffer);
		alignx = game.getFont(3).getWidth(number_buffer);
		game.getFont(3).drawText(statx, staty, secretsstat, zoom, 32, 7, BuildFont.TextAlign.Left, 256, false);
		game.getFont(3).drawText(statx += 9 * viewzoom, staty, number_buffer, zoom, 32, 0, BuildFont.TextAlign.Left, 256,
				false);
		game.getFont(3).drawChar(statx += (alignx + 2) * viewzoom, staty, '/', zoom, 32, 0, 256, false);
		Bitoa(totalSecrets, number_buffer);
		game.getFont(3).drawText(statx += 8 * viewzoom, staty, number_buffer, zoom, 32, 0, BuildFont.TextAlign.Left, 256, false);
	}
}
