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

package ru.m210projects.Blood.Types;

public class EFFECT {
	
	public int funcID;
	public int detail;
	public int seqId;
	public int flags;
	public int gravity;
	public int velocity;
	public int count;
	public int picnum;
	public int xrepeat;
	public int yrepeat;
	public int cstat;
	public int shade;
	public int pal;
		
	public EFFECT(int funcID, int detail, int seqId, int flags, int gravity, int velocity, int count, int picnum,
			int xrepeat, int yrepeat, int cstat, int shade, int pal) {
		
		this.funcID = funcID;
		this.detail = detail;
		this.seqId = seqId;
		this.flags = flags;
		this.gravity = gravity;
		this.velocity = velocity;
		this.count = count;
		this.picnum = picnum;
		this.xrepeat = xrepeat;
		this.yrepeat = yrepeat;
		this.cstat = cstat;
		this.shade = shade;
		this.pal = pal;
	}
}