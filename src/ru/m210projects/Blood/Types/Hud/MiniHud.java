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

import static ru.m210projects.Blood.PLAYER.BLUEARMOR;
import static ru.m210projects.Blood.PLAYER.GREENARMOR;
import static ru.m210projects.Blood.PLAYER.REDARMOR;
import static ru.m210projects.Blood.Main.*;
import static ru.m210projects.Build.Engine.*;

import ru.m210projects.Blood.PLAYER;
import ru.m210projects.Blood.Types.XSPRITE;

public class MiniHud extends HudRenderer {

	@Override
	public void draw(PLAYER gView, int x, int y) {
		int pal = getPalette(gView);
		XSPRITE pXplayer = gView.pXsprite;

		DrawStatMaskedSprite(2201, 34, 187, 16, pal, 256); // health and ammo window
		if ((pXplayer.health) > 0 || (totalclock & 0x10) != 0)
			DrawStatNumber(3, pXplayer.health >> 4, kBigRed, 8, 183, 0, 0, 256);// health

		if (gView.currentWeapon != 0) {
			if (gView.weaponAmmo != -1) {
				int ammo = gView.ammoCount[gView.weaponAmmo];
				if (gView.weaponAmmo == 6)
					ammo /= 10;
				DrawStatNumber(3, ammo, kBigBlue, 42, 183, 0, 0, 256);
			}
		}
		DrawStatMaskedSprite(2173, 284, 187, 16, pal, 512);// armor and inventory window

		if (gView.ArmorAmount[REDARMOR] != 0) {
			TileHGauge(2207, 250, 175, gView.ArmorAmount[REDARMOR], 3200, 512); // red armor
			DrawStatNumber(3, (gView.ArmorAmount[REDARMOR] >> 4), kLittleWhite, 255, 178, 0, 0, 512);
		}
		if (gView.ArmorAmount[BLUEARMOR] != 0) {
			TileHGauge(2209, 250, 183, gView.ArmorAmount[BLUEARMOR], 3200, 512); // blue armor
			DrawStatNumber(3, (gView.ArmorAmount[BLUEARMOR] >> 4), kLittleWhite, 255, 186, 0, 0, 512);
		}
		if (gView.ArmorAmount[GREENARMOR] != 0) {
			TileHGauge(2208, 250, 191, gView.ArmorAmount[GREENARMOR], 3200, 512); // green armor
			DrawStatNumber(3, (gView.ArmorAmount[GREENARMOR] >> 4), kLittleWhite, 255, 194, 0, 0, 512);
		}
		showInventoryItems(gView, 286, 186, 302, 183, 512);

		if (gView.hasKey[1]) {
			DrawStatMaskedSprite(2220, 75, 193, 0, 0, 256);
//			DrawStatMaskedSprite(2552, 75, 219, 0, 5, 256, 0, 0, 320, 195);
		}
		if (gView.hasKey[3]) {
			DrawStatMaskedSprite(2222, 87, 193, 0, 0, 256);
//			DrawStatMaskedSprite(2554, 88, 219, 0, 5, 256, 0, 0, 320, 195);
		}
		if (gView.hasKey[5]) {
			DrawStatMaskedSprite(2224, 99, 193, 0, 0, 256);
//			DrawStatMaskedSprite(2556, 101, 219, 0, 5, 256, 0, 0, 320, 195);
		}

		if (gView.hasKey[2]) {
			DrawStatMaskedSprite(2221, 216, 193, 0, 0, 512);
//			DrawStatMaskedSprite(2553, 214, 219, 0, 5, 512, 0, 0, 320, 195);
		}
		if (gView.hasKey[4]) {
			DrawStatMaskedSprite(2223, 228, 193, 0, 0, 512);
//			DrawStatMaskedSprite(2555, 227, 219, 0, 5, 512, 0, 0, 320, 195);
		}
		if (gView.hasKey[6]) {
			DrawStatMaskedSprite(2225, 240, 193, 0, 0, 512);
//			DrawStatMaskedSprite(2557, 240, 219, 0, 5, 512, 0, 0, 320, 195);
		}

		viewDrawStats(10);

		if (gView.throwTime != 0)
			TileHGauge(2260, 124, 175, gView.throwTime, 65536, 0);// red line
		else
			viewShowInventory(gView, 166, 200 - engine.getTile(2201).getHeight() / 2, 0);
	}

}
