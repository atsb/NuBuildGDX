// This file is part of DukeGDX.
// Copyright (C) 2019  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// DukeGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// DukeGDX is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with DukeGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.Duke3D.Types;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ru.m210projects.Build.Types.LittleEndian;

public class NetInfo {

	public final int sizeof = 10;
	private final ByteBuffer buffer = ByteBuffer.allocate(sizeof).order( ByteOrder.LITTLE_ENDIAN);
	
	public int nGameType;
	public int nEpisode;
	public int nLevel;
	public int nDifficulty;
	public int nMonsters;
	
	public int nRespawnMonsters;
	public int nRespawnItem;
	public int nRespawnInventory;
	
	public int nMarkers;
	public int nFriendlyFire;

	public void set(byte[] p, int ptr)
	{
		nGameType = LittleEndian.getUByte(p, ptr); ptr++;
		nEpisode = LittleEndian.getUByte(p, ptr); ptr++;
		nLevel = LittleEndian.getUByte(p, ptr); ptr++;
		nDifficulty = (byte) LittleEndian.getUByte(p, ptr); ptr++;
		nMonsters = LittleEndian.getUByte(p, ptr); ptr++;
		nRespawnMonsters = LittleEndian.getUByte(p, ptr); ptr++;
		nRespawnItem = LittleEndian.getUByte(p, ptr); ptr++;
		nRespawnInventory = LittleEndian.getUByte(p, ptr); ptr++;
		nMarkers = LittleEndian.getUByte(p, ptr); ptr++;
		nFriendlyFire = LittleEndian.getUByte(p, ptr); ptr++;
	}
	
	public byte[] getBytes()
	{
		buffer.clear();
		
		buffer.put((byte)nGameType);
		buffer.put((byte)nEpisode);
		buffer.put((byte)nLevel);
		buffer.put((byte)nDifficulty);
		buffer.put((byte)nMonsters);
		buffer.put((byte)nRespawnMonsters);
		buffer.put((byte)nRespawnItem);
		buffer.put((byte)nRespawnInventory);
		buffer.put((byte)nMarkers);
		buffer.put((byte)nFriendlyFire);
		
		return buffer.array();
	}
	
}
