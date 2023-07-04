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

import static ru.m210projects.Powerslave.Globals.*;

import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.FileHandle.Resource;

public class DemoFile {

	public int record_index, record_limit;
	public byte record_mode;
	public byte[] record_buffer;
	public Resource record_file;

	public int level;
	public short weapons;
	public short clip, items;
	public short lives;
	public PlayerStruct player = new PlayerStruct();
	
	public DemoFile(String name) {
		record_file = BuildGdx.cache.open(name, 0);
		level = record_file.readByte();
		weapons = record_file.readShort();
		record_file.readShort(); //currentWeapon
		clip = record_file.readShort();
		items = record_file.readShort();
		player.setBytes(record_file, false);
		lives = record_file.readShort();
	}
	
	public Input ReadPlaybackInput()
	{
		Integer var = null;
		if (record_file != null && (var = record_file.readInt()) != null) {
			moveframes = var;
			byte[] data = new byte[32];
			record_file.read(data);
			
			return new Input(data);
		}
		
		if(record_file != null)
			record_file.close();
		record_file = null;
		return null;
	}
}
