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

import static ru.m210projects.Blood.DB.kItemBase;
import static ru.m210projects.Blood.DB.kItemInvulnerability;
import static ru.m210projects.Blood.Globals.kHUDEye;
import static ru.m210projects.Blood.Main.cfg;
import static ru.m210projects.Blood.Main.engine;
import static ru.m210projects.Blood.Main.game;
import static ru.m210projects.Blood.PLAYER.BLUEARMOR;
import static ru.m210projects.Blood.PLAYER.GREENARMOR;
import static ru.m210projects.Blood.PLAYER.REDARMOR;
import static ru.m210projects.Blood.PLAYER.powerupCheck;
import static ru.m210projects.Build.Engine.totalclock;

import ru.m210projects.Blood.PLAYER;
import ru.m210projects.Blood.Types.XSPRITE;
import ru.m210projects.Build.Gameutils.ConvertType;
import ru.m210projects.Build.Pattern.BuildFont.TextAlign;

public class FullHud extends HudRenderer {

	public static final int kPicStatBar = 2200;

	public FullHud() {
		engine.loadtile(kPicStatBar);
	}

	@Override
	public void draw(PLAYER gView, int x, int y) {
		int pal = getPalette(gView);
		XSPRITE pXplayer = gView.pXsprite;

		viewShowInventory(gView, 160, 200 - engine.getTile(kPicStatBar).getHeight(), 0);
		int hx = 160;
		int hy = 172;
		DrawStatMaskedSprite(kPicStatBar, hx, hy, 16, pal, 0);

		if (gView.godMode || powerupCheck(gView, kItemInvulnerability - kItemBase) > 0) {
			DrawStatMaskedSprite(kHUDEye, (hx - 134), (hy - 7), 16, 2,
					kDrawXFlip | kDrawTranslucentR | kDrawTranslucent);
			DrawStatMaskedSprite(kHUDEye, (hx + 134), (hy - 7), 16, 2,
				 kDrawTranslucentR | kDrawTranslucent);
		}

		showInventoryItems(gView, 265, 186, 260, 172, 0);

		if ((pXplayer.health) > 0 || (totalclock & 0x10) != 0)
			DrawStatNumber(3, pXplayer.health >> 4, kBigRed, 86, 183, 0, 0, 0); // health

		if (gView.currentWeapon != 0) {
			if (gView.weaponAmmo != -1) {
				int ammo = gView.ammoCount[gView.weaponAmmo];
				if (gView.weaponAmmo == 6)
					ammo /= 10;
				DrawStatNumber(3, ammo, kBigBlue, 216, 183, 0, 0, 0);
			}
		}

		for (int i = 9; i >= 1; --i) {
			int dx = 23 * ((i - 1) / 3) + 135;
			int dy = 6 * ((i - 1) % 3) + 182;
			int ammo = gView.ammoCount[i];
			if (i == 6)
				ammo /= 10;
			if (i == gView.weaponAmmo)
				DrawStatNumber(3, ammo, kLittleWhite, dx, dy, -128, 10, 0);
			else
				DrawStatNumber(3, ammo, kLittleWhite, dx, dy, 32, 10, 0);
		}

		int nShade;
		if (gView.weaponAmmo == 10)
			nShade = -128;
		else
			nShade = 32;
		DrawStatNumber(2, gView.ammoCount[10], kLittleWhite, 291, 194, nShade, 10, 0);
		if (gView.weaponAmmo == 11)
			nShade = -128;
		else
			nShade = 32;
		DrawStatNumber(2, gView.ammoCount[11], kLittleWhite, 309, 194, nShade, 10, 0);

		if (gView.ArmorAmount[REDARMOR] != 0) {
			TileHGauge(2207, 44, 174, gView.ArmorAmount[REDARMOR], 3200, 0); // red armor
			DrawStatNumber(3, (gView.ArmorAmount[REDARMOR] >> 4), kLittleWhite, 50, 177, 0, 0, 0);
		}
		if (gView.ArmorAmount[BLUEARMOR] != 0) {
			TileHGauge(2209, 44, 182, gView.ArmorAmount[BLUEARMOR], 3200, 0); // blue armor
			DrawStatNumber(3, (gView.ArmorAmount[BLUEARMOR] >> 4), kLittleWhite, 50, 185, 0, 0, 0);
		}
		if (gView.ArmorAmount[GREENARMOR] != 0) {
			TileHGauge(2208, 44, 190, gView.ArmorAmount[GREENARMOR], 3200, 0); // green armor
			DrawStatNumber(3, (gView.ArmorAmount[GREENARMOR] >> 4), kLittleWhite, 50, 193, 0, 0, 0);
		}

		DrawVersion(20, 191, game.sversion);

		for (int i = 0; i < 6; i++) {
			int dx = 173 * (i & 1) + 73;
			int dy = 11 * (i >> 1) + 171;

			int keyTile = i + 2220;
			if (gView.hasKey[i + 1])
				DrawStatMaskedSprite(keyTile, dx, dy, 0, 0, 0);
			else
				DrawStatMaskedSprite(keyTile, dx, dy, 40, 5, 0);
		}

		if ((!cfg.gAutoRun && gView.Run) || (!gView.Run && cfg.gAutoRun))
			pal = 16;
		else
			pal = 40;

		DrawStatMaskedSprite(2202, 118, 185, pal, 0, 0);
		DrawStatMaskedSprite(2202, 201, 185, pal, 0, 0);

		viewDrawStats(0);

		if (gView.throwTime != 0)
			TileHGauge(2260, 124, 175, gView.throwTime, 65536, 0);
	}

	protected void DrawVersion(int x, int y, String version) {

        game.getFont(3).drawText(x, y, game.sversion, 65536, 32, 0, TextAlign.Center, 10, false);
	}

}
