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

package ru.m210projects.Powerslave;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ru.m210projects.Build.FileHandle.Resource;
import ru.m210projects.Powerslave.Type.SafeLoader;

public class Random {

	private static int randA;
	private static int randB;
	private static int randC;

	public static void InitRandom() {
		randA = 0;
		randB = 286331153;
		randC = 16843009;
	}

	public static ByteBuffer saveRandom()
	{
		ByteBuffer bb = ByteBuffer.allocate(3 * 4);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		
		bb.putInt(randA);
		bb.putInt(randB);
		bb.putInt(randC);
		
		return bb;
	}
	
	public static void loadRandom(SafeLoader loader, Resource bb)
	{
		if(bb != null) {
			loader.randA = bb.readInt();
			loader.randB = bb.readInt();
			loader.randC = bb.readInt();
		}
		else
		{
			randA = loader.randA;
			randB = loader.randB;
			randC = loader.randC;
		}
	}

	public static int RandomBit() {
		randA = (randA >> 1) | ((((randA & 0xFF) ^ (((randA >> 1) ^ (randA >> 2) ^ (randA >> 31) ^ (randA >> 6) ^ (randA >> 4)) & 0xFF)) & 1) << 31);
		randB = (randB >> 1) | (((((randB >> 2) & 0xFF) ^ (randB >> 30)) & 1) << 30);
		randC = (randC >> 1) | (((((randC >> 1) & 0xFF) ^ (randC >> 28)) & 1) << 28);
		return (((randA == 0) ? 1 : 0) & (randC & 0xFF) | (randB & randA) & 0xFF) & 1;
	}

	public static int RandomByte() {
		return (RandomBit() << 7) | (RandomBit() << 6) | (RandomBit() << 5) | (RandomBit() << 4) | (RandomBit() << 3)
				| (RandomBit() << 2) | (RandomBit() << 1) | RandomBit();
	}

	public static int RandomWord() {
		return (RandomByte() << 8) | RandomByte();
	}

	public static int RandomLong() {
		return (RandomWord() << 16) | RandomWord();
	}

	public static int RandomSize(int len) {
		int result = 0;
		while (len-- > 0)
			result = 2 * result | RandomBit();
		return result;
	}
}
