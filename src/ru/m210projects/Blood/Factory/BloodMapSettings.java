// This file is part of BloodGDX.
// Copyright (C) 2017-2021 Alexander Makarov-[M210] (m210-2007@mail.ru)
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

package ru.m210projects.Blood.Factory;

import static ru.m210projects.Blood.Globals.gFullMap;
import static ru.m210projects.Blood.Globals.gMapScrollMode;
import static ru.m210projects.Blood.Globals.gPlayer;
import static ru.m210projects.Blood.Globals.pGameInfo;
import static ru.m210projects.Blood.View.gViewIndex;
import static ru.m210projects.Build.Engine.wall;

import ru.m210projects.Build.Gameutils;
import ru.m210projects.Build.Render.DefaultMapSettings;
import ru.m210projects.Build.Types.WALL;

public class BloodMapSettings extends DefaultMapSettings {

	@Override
	public int getWallColor(int w, int s) {
		WALL wal = wall[w];
//		if (Gameutils.isValidSector(wal.nextsector)) // red wall
//			return 24;
		return 24; // white wall
	}

	@Override
	public boolean isShowRedWalls() {
		return false;
	}

	@Override
	public boolean isSpriteVisible(MapView view, int index) {
		return false;
	}

	@Override
	public int getPlayerSprite(int player) {
		return gPlayer[player].nSprite;
	}

	@Override
	public boolean isShowAllPlayers() {
		return pGameInfo.nGameType == 1;
	}

	@Override
	public boolean isFullMap() {
		return gFullMap;
	}

	@Override
	public boolean isScrollMode() {
		return gMapScrollMode;
	}

	@Override
	public int getViewPlayer() {
		return gViewIndex;
	}
}
