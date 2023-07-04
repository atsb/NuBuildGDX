// This file is part of BloodGDX.
// Copyright (C) 2017-2018  Alexander Makarov-[M210] (m210-2007@mail.ru)
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

import ru.m210projects.Build.Types.LittleEndian;

public class BitHandler {

	public static final byte[] loMask = { (byte) 0xFF, (byte)0x01, (byte)0x03, (byte)0x07, (byte)0x0F, (byte)0x1F, (byte)0x3F, (byte)0x7F, (byte)0xFF };
	public static final byte[] hiMask = { (byte) 0xFF, (byte)0xFE, (byte)0xFC, (byte)0xF8, (byte)0xF0, (byte)0xE0, (byte)0xC0, (byte)0x80, (byte)0x00 };
	public static final byte[] bitMask = new byte[4];

	public static int buread(byte[] buffer, int bufferPos, int startBit, int endBit)
	{
		resetBitMask();
		byte sMask = hiMask[startBit];
		byte eMask = loMask[(endBit + 1) % 8];
		int nBytes = (endBit / 8);
		bitMask[0] = sMask;
		bitMask[nBytes] = eMask;
		int mask = 0, value = 0;
		switch(nBytes)
		{
			case 0:
				mask = sMask & eMask;
				value = buffer[bufferPos] & 0xFF;
				break;
			case 1:
				mask = LittleEndian.getShort(bitMask, 0);
				value = LittleEndian.getUShort(buffer, bufferPos);
				break;
			case 2:
			    int bm0 = bitMask[0] & 0xFF;
			    int bm1 = bitMask[1] & 0xFF;
			    int bm2 = bitMask[2] & 0xFF;
			    mask = ( bm2 << 16 ) + ( bm1 << 8 ) + (bm0);
			    value = (int) LittleEndian.getUInt(buffer, bufferPos);
				break;
			case 3:
				mask = LittleEndian.getInt(bitMask, 0);
				value = (int) LittleEndian.getUInt(buffer, bufferPos);
				break;
		}


		//System.out.println("BitReader: value & 0x" + Integer.toHexString(mask) + " >> " + startBit);
		return (value & mask) >> startBit;
	}

	public static int bsread(byte[] buffer, int bufferPos, int startBit, int endBit)
	{
		resetBitMask();
		byte sMask = hiMask[startBit];
		byte eMask = loMask[(endBit + 1) % 8];
		int nBytes = (endBit / 8);
		bitMask[0] = sMask;
		bitMask[nBytes] = eMask;
		int mask = 0, value = 0;
		switch(nBytes)
		{
			case 0:
				mask = sMask & eMask;
				value = buffer[bufferPos] & 0xFF;
				break;
			case 1:
				mask = LittleEndian.getShort(bitMask, 0);
				value = LittleEndian.getUShort(buffer, bufferPos);
				break;
			case 2:
			    int bm0 = bitMask[0] & 0xFF;
			    int bm1 = bitMask[1] & 0xFF;
			    int bm2 = bitMask[2] & 0xFF;
			    mask = ( bm2 << 16 ) + ( bm1 << 8 ) + (bm0);
			    value = (int) LittleEndian.getUInt(buffer, bufferPos);
				break;
			case 3:
				mask = LittleEndian.getInt(bitMask, 0);
				value = (int) LittleEndian.getUInt(buffer, bufferPos);
				break;
		}

		int out = (value & mask);
		if((out & (1 << endBit)) != 0) {
			out ^= (1 << endBit);
			out -= (1 << endBit);
		}

		return (out) >> startBit;
	}

	public static void bput(byte[] data, int bufferPos, int value, int startBit, int endBit)
	{
		resetBitMask();
		byte eMask = loMask[(endBit + 1) % 8];
		int nBytes = (endBit / 8);
		bitMask[nBytes] = eMask;
		int mask = 0, val;
		switch(nBytes)
		{
			case 0:
				mask = (hiMask[startBit] & eMask) >> startBit;
				data[bufferPos] |= ((value & mask) << startBit);
				break;
			case 1:
				mask = LittleEndian.getShort(bitMask, 0);
				val = (value << startBit) & mask;
				data[bufferPos++] |= (byte)(val & 0xFF);
				data[bufferPos++] |= (byte)((val >> 8) & 0xFF);
				break;
			case 2:
			    int bm0 = bitMask[0] & 0xFF;
			    int bm1 = bitMask[1] & 0xFF;
			    int bm2 = bitMask[2] & 0xFF;
			    mask = ( bm2 << 16 ) + ( bm1 << 8 ) + (bm0);
			    val = (value << startBit) & mask;
			    data[bufferPos++] |= (byte)(val & 0xFF);
			    data[bufferPos++] |= (byte)((val >> 8) & 0xFF);
			    data[bufferPos++] |= (byte)((val >> 16) & 0xFF);
				break;
			case 3:
				mask = LittleEndian.getInt(bitMask, 0);
				val = (value << startBit) & mask;
				data[bufferPos++] |= (byte)(val & 0xFF);
				data[bufferPos++] |= (byte)((val >> 8) & 0xFF);
				data[bufferPos++] |= (byte)((val >> 16) & 0xFF);
				data[bufferPos++] |= (byte)((val >> 24) & 0xFF);
				break;
		}
	}

	public static void resetBitMask()
	{
		for(int i = 0; i < 4; i++)
			bitMask[i] = (byte) 0xFF;
	}
}


