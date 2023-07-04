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

public class WeaponInfo {

	public short seq;
	public int[] field_2;
	public short field_1A;
	public short field_1C;
	public short field_1E;
	public int[] field_20;
	
	public WeaponInfo(int seq, int[] field_2, int field_1A, int field_1C, int field_1E, int[] field_20)
	{
		this.seq = (short) seq;
		this.field_2 = field_2;
		
		this.field_1A = (short) field_1A;
		this.field_1C = (short) field_1C;
		this.field_1E = (short) field_1E;
		this.field_20 = field_20;
	}
}
