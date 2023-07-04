// This file is part of DukeGDX.
// Copyright (C) 2019-2021  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// DukeGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// DukeGDX is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with DukeGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.Redneck.Factory;

import static ru.m210projects.Build.Engine.sector;
import static ru.m210projects.Build.Engine.show2dsector;
import static ru.m210projects.Build.Engine.sprite;
import static ru.m210projects.Build.Engine.totalclock;
import static ru.m210projects.Build.Engine.wall;
import static ru.m210projects.Build.Engine.yxaspect;
import static ru.m210projects.Build.Pragmas.klabs;
import static ru.m210projects.Build.Pragmas.mulscale;
import static ru.m210projects.Redneck.Globals.ps;
import static ru.m210projects.Redneck.Globals.screenpeek;
import static ru.m210projects.Redneck.Globals.ud;
import static ru.m210projects.Redneck.Names.APLAYERTOP;

import ru.m210projects.Build.Gameutils;
import ru.m210projects.Build.Render.IOverheadMapSettings;
import ru.m210projects.Build.Types.SECTOR;
import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Build.Types.WALL;

public class RRMapSettings implements IOverheadMapSettings {

	@Override
	public boolean isShowSprites(MapView view) {
		if (view == MapView.Polygons)
			return false;
		return true;
	}

	@Override
	public boolean isShowFloorSprites() {
		return false;
	}

	@Override
	public boolean isShowRedWalls() {
		return true;
	}

	@Override
	public boolean isShowAllPlayers() {
		return ud.coop == 1;
	}

	@Override
	public boolean isSpriteVisible(MapView view, int index) {
		if (view == MapView.Polygons)
			return true;

		SPRITE spr = sprite[index];
		if (spr.cstat == 257 || ps[screenpeek].i == index)
			return false;

		return (spr.cstat & 257) != 0;
	}

	@Override
	public boolean isWallVisible(int w, int s) {
		WALL wal = wall[w];
		SECTOR sec = sector[s];
		if (wal.nextsector != 0) // red wall
			return (((sector[wal.nextsector].ceilingz != sec.ceilingz //
					|| sector[wal.nextsector].floorz != sec.floorz //
					|| ((wal.cstat | wall[wal.nextwall].cstat) & (16 + 32)) != 0)
					&& (!isFullMap() && (show2dsector[wal.nextsector >> 3] & 1 << (wal.nextsector & 7)) == 0)));
		return true;
	}

	@Override
	public int getWallColor(int w, int s) {
		WALL wal = wall[w];
		if (Gameutils.isValidSector(wal.nextsector)) {// red wall
			int col = 139; // red
			if (Gameutils.isValidWall(wal.nextwall) && ((wal.cstat | wall[wal.nextwall].cstat) & 1) != 0)
				col = 234; // magenta
			if ((show2dsector[wal.nextsector >> 3] & (1 << (wal.nextsector & 7))) == 0)
				col = 24;
			else
				col = -1;
			return col;
		}

		return 24; // white wall
	}

	@Override
	public int getSpriteColor(int s) {
		SPRITE spr = sprite[s];
		int col = 71; // cyan;
		if ((spr.cstat & 1) != 0)
			col = 234; // magenta

		return col;
	}

	@Override
	public int getPlayerSprite(int player) {
		if ((ud.scrollmode && player == screenpeek))
			return -1;

		return ps[player].i;
	}

	@Override
	public int getPlayerZoom(int player, int czoom) {
		int j = klabs(ps[player].truefz - ps[player].posz) >> 8;
		j = mulscale(czoom * (sprite[ps[player].i].yrepeat + j), yxaspect, 16);

		if (j < 22000)
			j = 22000;
		else if (j > (65536 << 1))
			j = (65536 << 1);
		return j;
	}

	@Override
	public int getPlayerPicnum(int p) {
		int i = APLAYERTOP;
		if (ps[p].OnMotorcycle)
			i = 7169;
		else if (ps[p].OnBoat)
			i = 7191;
		else {
			if (sprite[ps[p].i].xvel > 16 && ps[p].on_ground)
				i = APLAYERTOP + ((totalclock >> 4) & 3);
		}
		return i;
	}

	@Override
	public boolean isFullMap() {
		return false;
	}

	@Override
	public boolean isScrollMode() {
		return ud.scrollmode;
	}

	@Override
	public int getViewPlayer() {
		return screenpeek;
	}

	@Override
	public int getWallX(int w) {
		return wall[w].x;
	}

	@Override
	public int getWallY(int w) {
		return wall[w].y;
	}

	@Override
	public int getSpriteX(int spr) {
		return sprite[spr].x;
	}

	@Override
	public int getSpriteY(int spr) {
		return sprite[spr].y;
	}

	@Override
	public int getSpritePicnum(int spr) {
		return sprite[spr].picnum;
	}
}
