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

package ru.m210projects.Blood;

public class PROFILE {
	public static final int sizeof = 18;

	public boolean autoaim;
	public boolean slopetilt;
	public byte skill;
	public int team;
	public String name;
	
	public PROFILE(String name, boolean autoaim, boolean slopetilt)
	{
		this.name = name;
		this.skill = 2;
		this.autoaim = autoaim;
		this.slopetilt = slopetilt;
		this.team = 0;
	}
}
