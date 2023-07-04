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
import static ru.m210projects.Blood.Globals.kHUDLeft2;
import static ru.m210projects.Blood.Globals.kHUDRight2;
import static ru.m210projects.Blood.Main.engine;
import static ru.m210projects.Blood.PLAYER.BLUEARMOR;
import static ru.m210projects.Blood.PLAYER.GREENARMOR;
import static ru.m210projects.Blood.PLAYER.REDARMOR;
import static ru.m210projects.Blood.PLAYER.powerupCheck;
import static ru.m210projects.Build.Engine.totalclock;

import ru.m210projects.Blood.PLAYER;
import ru.m210projects.Blood.Types.XSPRITE;

public class AltHud extends HudRenderer {

	@Override
	public void draw(PLAYER gView, int x, int y) {
		int pal = getPalette(gView);
		XSPRITE pXplayer = gView.pXsprite;

		DrawStatMaskedSprite(kHUDLeft2, 64, 177, 16, pal, 256); // health window
		if ((pXplayer.health) > 0 || (totalclock & 0x10) != 0)
			DrawStatNumber(3, pXplayer.health >> 4, kBigRed, 100, 183, 0, 0, 256);// health

		DrawStatMaskedSprite(kHUDRight2, 254, 177, 16, pal, 512);// armor and inventory window

		if (gView.currentWeapon != 0) {
			if (gView.weaponAmmo != -1) {
				int ammo = gView.ammoCount[gView.weaponAmmo];
				if (gView.weaponAmmo == 6)
					ammo /= 10;
				DrawStatNumber(3, ammo, kBigBlue, 201, 183, 0, 0, 512);
			}
		}
		showInventoryItems(gView, 290, 184, 285, 170, 512);

		if (gView.ArmorAmount[REDARMOR] != 0) {
			TileHGauge(2207, 19, 173, gView.ArmorAmount[REDARMOR], 3200, 256); // red armor
			DrawStatNumber(3, (gView.ArmorAmount[REDARMOR] >> 4), kLittleWhite, 24, 176, 0, 0, 256);
		}
		if (gView.ArmorAmount[BLUEARMOR] != 0) {
			TileHGauge(2209, 19, 181, gView.ArmorAmount[BLUEARMOR], 3200, 256); // blue armor
			DrawStatNumber(3, (gView.ArmorAmount[BLUEARMOR] >> 4), kLittleWhite, 24, 184, 0, 0, 256);
		}
		if (gView.ArmorAmount[GREENARMOR] != 0) {
			TileHGauge(2208, 19, 189, gView.ArmorAmount[GREENARMOR], 3200, 256); // green armor
			DrawStatNumber(3, (gView.ArmorAmount[GREENARMOR] >> 4), kLittleWhite, 24, 192, 0, 0, 256);
		}

		if (gView.hasKey[1])
			DrawStatMaskedSprite(2220, 7, 170, 0, 0, 256);
		if (gView.hasKey[3])
			DrawStatMaskedSprite(2222, 7, 181, 0, 0, 256);
		if (gView.hasKey[5])
			DrawStatMaskedSprite(2224, 7, 192, 0, 0, 256);

		if (gView.hasKey[2])
			DrawStatMaskedSprite(2221, 311, 170, 0, 0, 512);
		if (gView.hasKey[4])
			DrawStatMaskedSprite(2223, 311, 181, 0, 0, 512);
		if (gView.hasKey[6])
			DrawStatMaskedSprite(2225, 311, 192, 0, 0, 512);

		if (gView.godMode || powerupCheck(gView, kItemInvulnerability - kItemBase) > 0) {
			DrawStatMaskedSprite(kHUDEye, 78, 175, 16, 2,
					kDrawXFlip | 256 | kDrawTranslucentR | kDrawTranslucent);
			DrawStatMaskedSprite(kHUDEye, 241, 175, 16, 2, 512 | kDrawTranslucentR | kDrawTranslucent);
		}

		viewDrawStats(0);

		if (gView.throwTime != 0)
			TileHGauge(2260, 124, 175, gView.throwTime, 65536, 0);// red line
		else
			viewShowInventory(gView, 166, 200 - engine.getTile(2201).getHeight() / 2, 0);
	}

}
