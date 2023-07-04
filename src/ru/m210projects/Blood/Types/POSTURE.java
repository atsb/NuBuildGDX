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

public class POSTURE {
	public int frontAccel;
	public int sideAccel;
	public int	backAccel;
	
	public int[] pace;
	public int bobV;
	public int bobH;
	public int	swayV;
	public int	swayH;
	public int viewSpeed;
	public int weapSpeed;
	public int xoffset;
	public int zoffset;
	
	public POSTURE(int frontAccel, int sideAccel, int backAccel, int[] pace, int bobV, int bobH, int swayV, int swayH, int viewSpeed, int weapSpeed, int zoffset, int xoffset) {
		this.frontAccel = frontAccel;
		this.sideAccel = sideAccel;
		this.backAccel = backAccel;
		
		this.pace = pace;
		this.bobV = bobV;
		this.bobH = bobH;
		this.swayV = swayV;
		this.swayH = swayH;
		this.viewSpeed = viewSpeed;
		this.weapSpeed = weapSpeed;
		this.xoffset = xoffset;
		this.zoffset = zoffset;
	}
}
