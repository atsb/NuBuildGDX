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

public class EXPLODE {

	public short size;  //40 unsigned
	public short used1; //10 unsigned
	public short used2; //10 unsigned
	public int radius;   //75
	public int damageType; //450
	public int burnCount;   //0
	
	public int liveCount;  //60 data1
	public int quake;  //80 data2
	public int used3;  //40 data3
	
	public EXPLODE(int size, int used1, int used2, int radius, int damageType, int burnCount, int liveCount, int quake, int used3) {
		this.size = (short)size;
		this.used1 = (short)used1;
		this.used2 = (short)used2;
		this.damageType = damageType;
		this.radius = radius;
		this.burnCount = burnCount;
		this.liveCount = liveCount;
		this.quake = quake;
		this.used3 = used3;
	}
}
