// This file is part of BuildSmacker.
// Copyright (C) 2020  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// BuildSmacker is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// BuildSmacker is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with BuildSmacker.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.BuildSmacker;

import java.nio.ByteBuffer;

public class BitBuffer {

	private static final short pow2[] = { 1, 2, 4, 8, 16, 32, 64, 128 };

	private ByteBuffer data;
	private int pos;
	private int offset;

	public void wrap(ByteBuffer data) {
		this.data = data;
		this.pos = 0;
		this.offset = data.position();
	}

	public byte getBit() {
		return (((data.get(offset + (pos >> 3))) & pow2[pos++ & 7]) != 0) ? (byte) 1 : 0;
	}

	public short getByte() {
		return (short) ((getBit() << 0) | (getBit() << 1) | (getBit() << 2) | (getBit() << 3) | (getBit() << 4)
				| (getBit() << 5) | (getBit() << 6) | (getBit() << 7));
	}
}
