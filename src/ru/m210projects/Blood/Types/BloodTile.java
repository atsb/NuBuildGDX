// This file is part of BloodGDX.
// Copyright (C) 2017-2020  Alexander Makarov-[M210] (m210-2007@mail.ru)
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

import ru.m210projects.Build.Types.Tile;

public class BloodTile extends Tile {

	public enum ViewType {
		kSpriteViewSingle,
		kSpriteView5Full,
		kSpriteView8Full,
		kSpriteView5Half,
		kSpriteViewVoxel,
		kSpriteViewSpinVoxel
    }

	@Override
	public int getFrames() {
		return anm & 0x1F;
	}

	public int getUpdate() {
		return anm & 0x20;
	}

	public ViewType getView() {
		switch((anm >> 28) & 0x07) {
		case 1:
			return ViewType.kSpriteView5Full;
		case 2:
			return ViewType.kSpriteView8Full;
		case 3:
			return ViewType.kSpriteView5Half;
//		case 4:
//			return ViewType.kSpriteView3Flat;
//		case 5:
//			return ViewType.kSpriteView4Flat;
		case 6:
			return ViewType.kSpriteViewVoxel;
		case 7:
			return ViewType.kSpriteViewSpinVoxel;
		}

		return ViewType.kSpriteViewSingle;
	}
}
