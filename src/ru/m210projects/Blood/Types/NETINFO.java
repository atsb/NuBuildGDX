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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ru.m210projects.Build.Types.LittleEndian;

public class NETINFO {
	
	public final int sizeof = 14;
	private final ByteBuffer buffer = ByteBuffer.allocate(sizeof).order( ByteOrder.LITTLE_ENDIAN);

	public int nGameType = 2;
	public int nEpisode;
	public int nLevel;
	public int nDifficulty;
	public int nMonsterSettings;
	public int nWeaponSettings;
	public int nItemSettings;
	public short nFriendlyFire;
	public boolean nReviveMode;
	public int nFragLimit;

	public void set(byte[] p, int ptr)
	{
		nGameType = LittleEndian.getUByte(p, ptr); ptr++;
		nEpisode = LittleEndian.getUByte(p, ptr); ptr++;
		nLevel = LittleEndian.getUByte(p, ptr); ptr++;
		nDifficulty = LittleEndian.getUByte(p, ptr); ptr++;
		nMonsterSettings = LittleEndian.getUByte(p, ptr); ptr++;
		nWeaponSettings = LittleEndian.getUByte(p, ptr); ptr++;
		nItemSettings = LittleEndian.getUByte(p, ptr); ptr++;
		nFriendlyFire = LittleEndian.getShort(p, ptr); ptr += 2;
		nReviveMode = LittleEndian.getUByte(p, ptr) == 1; ptr++;
		nFragLimit = LittleEndian.getInt(p, ptr); ptr += 4;
	}
	
	public byte[] getBytes()
	{
		buffer.clear();
		
		buffer.put((byte)nGameType);
		buffer.put((byte)nEpisode);
		buffer.put((byte)nLevel);
		buffer.put((byte)nDifficulty);
		buffer.put((byte)nMonsterSettings);
		buffer.put((byte)nWeaponSettings);
		buffer.put((byte)nItemSettings);
		buffer.putShort(nFriendlyFire);
		buffer.put(nReviveMode ? (byte) 1 : 0);
		buffer.putInt(nFragLimit);
		
		return buffer.array();
	}
	
}
