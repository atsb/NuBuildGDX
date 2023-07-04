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

public class TeslaData {
	public int xoffset;
	public int missileType;
	public int nCount;
	public int soundId;
	public int visibility;
	public int fireEffect;
	
	public TeslaData(int xoffset, int missileType, int nCount,
			int soundId, int visibility, int fireEffect)
	{
		this.xoffset = xoffset;
		this.missileType = missileType;
		this.nCount = nCount;
		this.soundId = soundId;
		this.visibility = visibility;
		this.fireEffect = fireEffect;
	}
}
