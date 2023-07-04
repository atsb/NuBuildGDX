package ru.m210projects.Blood.Types.Hud;

import static ru.m210projects.Blood.PLAYER.BLUEARMOR;
import static ru.m210projects.Blood.PLAYER.GREENARMOR;
import static ru.m210projects.Blood.PLAYER.REDARMOR;
import static ru.m210projects.Build.Engine.totalclock;

import ru.m210projects.Blood.PLAYER;
import ru.m210projects.Blood.Types.XSPRITE;

public class SplitHud extends HudRenderer {

	@Override
	public void draw(PLAYER gView, int x, int y) {
		x = 17;
		y = 94;
		if (gView.nPlayer == 1)
			y += 100;
		XSPRITE pXplayer = gView.pXsprite;

		DrawStatSprite(2201, x, y, 0, 0, 256, 32768);
		x = 302;
		DrawStatSprite(2173, x, y, 16, 0, 512, 32768);

		x = 5;
		y = 92;
		if (gView.nPlayer == 1)
			y += 100;
		if ((pXplayer.health) > 0 || (totalclock & 0x10) != 0)
			DrawStatNumber(3, pXplayer.health >> 4, kBigRed, x, y, 0, 0, 256, 32768);

		if (gView.currentWeapon != 0) {
			if (gView.weaponAmmo != -1) {
				int ammo = gView.ammoCount[gView.weaponAmmo];
				if (gView.weaponAmmo == 6)
					ammo /= 10;
				DrawStatNumber(3, ammo, kBigBlue, x + 17, y, 0, 0, 256, 32768);
			}
		}
		y = 90;
		if (gView.nPlayer == 1)
			y += 100;

		if (gView.ArmorAmount[REDARMOR] != 0) {
			DrawStatNumber(3, (gView.ArmorAmount[REDARMOR] >> 4), kLittleWhite, 288, y, 0, 0, 512, 32768);
		}
		if (gView.ArmorAmount[BLUEARMOR] != 0) {
			DrawStatNumber(3, (gView.ArmorAmount[BLUEARMOR] >> 4), kLittleWhite, 288, y + 4, 0, 0, 512, 32768);
		}
		if (gView.ArmorAmount[GREENARMOR] != 0) {
			DrawStatNumber(3, (gView.ArmorAmount[GREENARMOR] >> 4), kLittleWhite, 288, y + 8, 0, 0, 512, 32768);
		}

		if (gView.choosedInven >= 0) {
			y = 94;
			if (gView.nPlayer == 1)
				y += 100;
			DrawStatSprite(viewInventoryTile[gView.choosedInven], 303, y, 0, 0, 512, 32768);
			DrawStatNumber(3, gView.Inventory[gView.choosedInven].amount, kLittleRed, 313, y - 2, 4, 0, 512, 32768);
		}
		viewShowInventory(gView, 160, y, 0);
	}

}
