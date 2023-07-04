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
// Добавил звук выстрела в структуру.
// Изначально делал для универсального культиста, но можно и для других это использовать,
// тем самым нехило сократить код, убрав вызовы функций звука для ИИ.
// Поддерживается до 2-х звуков, которые выбираются через Chance в функции sfxPlayMissileFireSound();
public class MissileType {
	public int picnum;
	public int velocity;
	public int angleOfs;
	public int xrepeat;
	public int yrepeat;
	public int shade;
	public int clipdist;
	public int[] fireSound = new int[2];
	
	public MissileType(int picnum, int velocity, int angleOfs, int xrepeat, 
			int yrepeat, int shade, int clipdist, int[] fireSound) {
		this.picnum = picnum;
		this.velocity = velocity;
		this.angleOfs = angleOfs;
		this.xrepeat = xrepeat;
		this.yrepeat = yrepeat;
		this.shade = shade;
		this.clipdist = clipdist;
		this.fireSound = fireSound;
	}
}
