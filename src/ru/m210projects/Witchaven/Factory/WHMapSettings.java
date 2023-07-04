
package ru.m210projects.Witchaven.Factory;

import static ru.m210projects.Build.Engine.wall;
import static ru.m210projects.Witchaven.Globals.followmode;
import static ru.m210projects.Witchaven.WHPLR.player;
import static ru.m210projects.Witchaven.WHPLR.pyrn;

import ru.m210projects.Build.Gameutils;
import ru.m210projects.Build.Render.DefaultMapSettings;
import ru.m210projects.Build.Types.WALL;

public class WHMapSettings extends DefaultMapSettings {

	@Override
	public int getWallColor(int w, int s) {
		WALL wal = wall[w];
		if (Gameutils.isValidSector(wal.nextsector)) // red wall
			return 252;
		return 24; // white wall
	}

	@Override
	public boolean isSpriteVisible(MapView view, int index) {
		return false;
	}

	@Override
	public int getPlayerPicnum(int player) {
		return -1;
	}

	@Override
	public int getSpriteColor(int s) {
		return 31;
	}

	@Override
	public boolean isShowRedWalls() {
		return true;
	}

	@Override
	public int getPlayerSprite(int i) {
		return player[i].spritenum;
	}

	@Override
	public boolean isScrollMode() {
		return followmode;
	}

	@Override
	public int getViewPlayer() {
		return pyrn;
	}
}
