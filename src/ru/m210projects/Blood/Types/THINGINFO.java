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

public class THINGINFO {
	public int startHealth;
	public int mass;
	public int clipdist;
	public int flags;
	public int nFraction;
	public int fallDamage;
	public int cstat;
	public int picnum;
	public int shade;
	public int pal;
	public int xrepeat;
	public int yrepeat;
	public int[] damageShift;
	public int allowThrow; // indicates if custom dude can throw it
	
	public THINGINFO(int startHealth, int mass, int clipdist, int flags, int nFraction, int fallDamage, int cstat,
	int picnum, int shade, int pal, int xrepeat, int yrepeat, int[] damageShift,int allowThrow) {
		this.startHealth = startHealth;
		this.mass = mass;
		this.clipdist = clipdist;
		this.flags = flags;
		this.nFraction = nFraction;
		this.fallDamage = fallDamage;
		this.cstat = cstat;
		this.picnum = picnum;
		this.shade = shade;
		this.pal = pal;
		this.xrepeat = xrepeat;
		this.yrepeat = yrepeat;
		this.damageShift = damageShift;
		this.allowThrow = allowThrow;
	}
}
