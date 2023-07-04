// This file is part of LSPGDX.
// Copyright (C) 2021  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// LSPGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// LSPGDX is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with LSPGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.LSP.Factory;

import static ru.m210projects.Build.Engine.sector;
import static ru.m210projects.Build.Engine.totalclock;
import static ru.m210projects.Build.Engine.wall;
import static ru.m210projects.Build.Net.Mmulti.myconnectindex;
import static ru.m210projects.LSP.Globals.followmode;
import static ru.m210projects.LSP.Globals.gPlayer;
import static ru.m210projects.LSP.Main.cfg;

import ru.m210projects.Build.Gameutils;
import ru.m210projects.Build.Render.DefaultMapSettings;
import ru.m210projects.Build.Types.WALL;

public class LSPMapSettings extends DefaultMapSettings {

	@Override
	public int getWallColor(int w, int i) {
		WALL wal = wall[w];
		if (Gameutils.isValidSector(wal.nextsector)) // red wall
			return 175;

		int walcol = 4;
		switch (sector[i].lotag) {
		case 97:
		case 98:
		case 99:
			if (!cfg.bShowExit)
				return -1;

			walcol = 120;
			if (sector[i].lotag == 99) // right exit
				walcol += (totalclock & 10);
			break;
		}
		return walcol; // white wall
	}

	@Override
	public boolean isShowRedWalls() {
		return true;
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
	public int getPlayerSprite(int player) {
		return gPlayer[player].nSprite;
	}

	@Override
	public boolean isFullMap() {
		return false;
	}

	@Override
	public boolean isScrollMode() {
		return followmode;
	}

	@Override
	public int getViewPlayer() {
		return myconnectindex;
	}
}
