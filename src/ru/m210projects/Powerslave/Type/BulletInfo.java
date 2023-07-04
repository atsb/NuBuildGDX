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

public class BulletInfo {
	public short force;
	public short inf_2;
	public int inf_4;
	public short inf_6;
	public short inf_8;
	public short inf_A;
	public short inf_C;
	public short inf_E;
	public short inf_10;
	public short inf_12;
	public int inf_14;
	public int inf_18;
	public int inf_1C;
	
	public BulletInfo(int inf_0, int inf_2, int inf_4, int inf_8, int inf_A, int inf_C, int inf_E, int inf_10, int inf_12)
	{
		this.force = (short)inf_0;
		this.inf_2 = (short) inf_2;
		this.inf_4 = inf_4;
		this.inf_8 = (short) inf_8;
		this.inf_A = (short) inf_A;
		this.inf_C = (short) inf_C;
		this.inf_E = (short) inf_E;
		this.inf_10 = (short) inf_10;
		this.inf_12 = (short) inf_12;
	}
}
