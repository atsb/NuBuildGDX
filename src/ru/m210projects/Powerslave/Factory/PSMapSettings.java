// This file is part of PowerslaveGDX.
// Copyright (C) 2021  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// PowerslaveGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// PowerslaveGDX is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with PowerslaveGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.Powerslave.Factory;

import ru.m210projects.Build.Gameutils;
import ru.m210projects.Build.Render.DefaultMapSettings;
import ru.m210projects.Build.Types.WALL;

import static ru.m210projects.Build.Engine.sprite;
import static ru.m210projects.Build.Engine.wall;
import static ru.m210projects.Build.Net.Mmulti.myconnectindex;
import static ru.m210projects.Powerslave.Globals.PlayerList;
import static ru.m210projects.Powerslave.Globals.followmode;
import static ru.m210projects.Powerslave.Player.ActionSeq;
import static ru.m210projects.Powerslave.Player.nHeightTemplate;
import static ru.m210projects.Powerslave.Seq.*;

public class PSMapSettings extends DefaultMapSettings {

	@Override
	public int getWallColor(int w, int s) {
		WALL wal = wall[w];
		if (Gameutils.isValidSector(wal.nextsector)) // red wall
			return 187;
		return 0; // white wall
	}

	@Override
	public boolean isShowSprites(MapView view) {
		return false;
	}

	@Override
	public boolean isShowRedWalls() {
		return true;
	}

	@Override
	public int getPlayerSprite(int player) {
		return PlayerList[player].spriteId;
	}

	@Override
	public boolean isScrollMode() {
		return followmode;
	}

	@Override
	public int getViewPlayer() {
		return myconnectindex;
	}

	@Override
	public int getPlayerPicnum(int player) {
		int anim = PlayerList[player].anim_;
		int seq = (SeqBase[4 + ActionSeq[anim].seq + SeqOffsets[PlayerList[player].seq]] + PlayerList[player].animCount);
		int frm = FrameBase[seq];
		return ChunkPict[frm];
	}
}
