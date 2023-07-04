// This file is part of BloodGDX.
// Copyright (C) 2017-2019  Alexander Makarov-[M210] (m210-2007@mail.ru)
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

package ru.m210projects.Blood.AI;

import ru.m210projects.Blood.Types.CALLPROC;
import ru.m210projects.Blood.Types.XSPRITE;
import ru.m210projects.Build.Types.SPRITE;

public class AISTATE implements AISTATEFUNC {
	final int	seqId, ticks;
	final boolean enter, move, think;
	AISTATE next;
	final CALLPROC callback;
	final Type type;

	public AISTATE(Type aistate, int seqId, CALLPROC callback, int ticks, boolean enter, boolean move, boolean think, AISTATE next) {
		this.type = aistate;
		this.seqId = seqId;
		this.callback = callback;
		this.ticks = ticks;
		this.enter = enter;
		this.move = move;
		this.think = think;
		this.next = next;
	}
	
	public Type getType() {
		return type;
	}

	@Override
	public void enter(SPRITE sprite, XSPRITE xsprite) {
	}

	@Override
	public void move(SPRITE sprite, XSPRITE xsprite) {	
	}

	@Override
	public void think(SPRITE sprite, XSPRITE xsprite) {	
	}
	
	@Override
	public void callback(int nXSprite) {	
	}
}
