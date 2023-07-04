
package ru.m210projects.Tekwar.Factory;

import static ru.m210projects.Build.Engine.sector;
import static ru.m210projects.Build.Engine.show2dwall;
import static ru.m210projects.Build.Engine.sprite;
import static ru.m210projects.Build.Engine.wall;
import static ru.m210projects.Tekwar.Main.screenpeek;
import static ru.m210projects.Tekwar.Player.gPlayer;
import static ru.m210projects.Tekwar.View.gViewMode;
import static ru.m210projects.Tekwar.View.kView2D;
import static ru.m210projects.Tekwar.View.kView2DIcon;

import ru.m210projects.Build.Gameutils;
import ru.m210projects.Build.Render.DefaultMapSettings;
import ru.m210projects.Build.Types.SECTOR;
import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Build.Types.WALL;

public class TekMapSettings extends DefaultMapSettings {

	@Override
	public int getWallColor(int w, int s) {
		WALL wal = wall[w];
		if (Gameutils.isValidSector(wal.nextsector)) // red wall
			return 232;
		return 239; // white wall
	}

	@Override
	public boolean isWallVisible(int w, int s) {
		WALL wal = wall[w];
		SECTOR sec = sector[s];
		if (gViewMode == kView2DIcon) {
			if (wal.nextsector != 0) {
				int k = wal.nextwall;
				if (k < 0)
					return false;
				if ((show2dwall[w >> 3] & (1 << (w & 7))) == 0)
					return false;
				if ((k > w) && ((show2dwall[k >> 3] & (1 << (k & 7))) > 0))
					return false;

				if (sector[wal.nextsector].ceilingz == sec.ceilingz)
					if (sector[wal.nextsector].floorz == sec.floorz)
						if (((wal.cstat | wall[wal.nextwall].cstat) & (16 + 32)) == 0)
							return false;

				if (gViewMode == kView2DIcon) {
					if (sec.floorz != sec.ceilingz)
						if (sector[wal.nextsector].floorz != sector[wal.nextsector].ceilingz)
							if (((wal.cstat | wall[wal.nextwall].cstat) & (16 + 32)) == 0)
								if (sec.floorz == sector[wal.nextsector].floorz)
									return false;
					if (sec.floorpicnum != sector[wal.nextsector].floorpicnum)
						return false;
					if (sec.floorshade != sector[wal.nextsector].floorshade)
						return false;
				}
			}
		}
		return true;
	}

	@Override
	public boolean isSpriteVisible(MapView view, int index) {
		if (view == MapView.Polygons)
			return false;

		switch (sprite[index].cstat & 48) {
		case 0:
			return true;
		case 16: // wall sprites
			return true;
		case 32: // floor sprites
			return gViewMode == kView2D;
		}
		return true;
	}

	@Override
	public int getPlayerPicnum(int player) {
		if (gViewMode == kView2D)
			return -1;

		int spr = getPlayerSprite(player);
		return spr != -1 ? sprite[spr].picnum : -1;
	}

	@Override
	public int getSpriteColor(int s) {
		SPRITE spr = sprite[s];
		if (s == gPlayer[screenpeek].playersprite)
			return 31;

		if ((spr.cstat & 1) != 0)
			return 248;
		return 56;
	}

	@Override
	public boolean isShowRedWalls() {
		return true;
	}

	@Override
	public int getPlayerSprite(int player) {
		return gPlayer[player].playersprite;
	}

	@Override
	public boolean isScrollMode() {
		return false;
	}

	@Override
	public int getViewPlayer() {
		return screenpeek;
	}
}
