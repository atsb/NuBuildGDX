// This file is part of PowerslaveGDX.
// Copyright (C) 2019  Alexander Makarov-[M210] (m210-2007@mail.ru)
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

package ru.m210projects.Powerslave.Type;

import static ru.m210projects.Build.Engine.sprite;
import static ru.m210projects.Build.Net.Mmulti.myconnectindex;
import static ru.m210projects.Powerslave.Globals.PlayerList;

import ru.m210projects.Build.Types.SPRITE;

public class PLocation {

	public int x, y, z;
	public int xvel, yvel, zvel;
	public float horiz, ang;
	
	public void copy(PLocation src)
	{
		this.x = src.x;
		this.y = src.y;
		this.z = src.z;
		this.xvel = src.xvel;
		this.yvel = src.yvel;
		this.zvel = src.zvel;
		this.horiz = src.horiz;
		this.ang = src.ang;
	}
	
	public void reset() {
		PlayerStruct p = PlayerList[myconnectindex];
		SPRITE pSprite = sprite[p.spriteId];
		
		x = pSprite.x;
		xvel = pSprite.xvel;
		y = pSprite.y;
		yvel = pSprite.yvel;
		z = pSprite.z + p.eyelevel;
		zvel = pSprite.zvel;
		ang = p.ang;
		horiz = p.horiz;
//		game.net.predictOld.copy(this);
	}
}
