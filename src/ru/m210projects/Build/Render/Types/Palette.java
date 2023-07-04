//This file is part of BuildGDX.
//Copyright (C) 2017-2018  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
//BuildGDX is free software: you can redistribute it and/or modify
//it under the terms of the GNU General Public License as published by
//the Free Software Foundation, either version 3 of the License, or
//(at your option) any later version.
//
//BuildGDX is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU General Public License for more details.
//
//You should have received a copy of the GNU General Public License
//along with BuildGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.Build.Render.Types;

public class Palette {
	public short r;
	public short g;
	public short b;
	public byte f;

	public Palette() {
	}

	public Palette(Palette src) {
		this.r = src.r;
		this.g = src.g;
		this.b = src.b;
		this.f = src.f;
	}

	public Palette(int r, int g, int b, int f) {
		this.r = (short) r;
		this.g = (short) g;
		this.b = (short) b;
		this.f = (byte) f;
	}

	public void update(int r, int g, int b, int f) {
		this.r = (short) r;
		this.g = (short) g;
		this.b = (short) b;
		this.f = (byte) f;
	}
}
