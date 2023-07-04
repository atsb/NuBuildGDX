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

import java.nio.ByteBuffer;

import ru.m210projects.Build.FileHandle.Resource;

public class ElevStruct {
	
	public static final int size = 56;
	
	public short field_0;
	public short channel;
	public short sectnum;
	public int field_6;
	public int field_A;
	public short field_E;
	public short field_10;
	
	public int field_12[] = new int[8];
	public short field_32;
	public short field_34;
	public short field_36;
	
	public void save(ByteBuffer bb)
	{
		bb.putShort(field_0);
		bb.putShort(channel);
		bb.putShort(sectnum);
		bb.putInt(field_6);
		bb.putInt(field_A);
		bb.putShort(field_E);
		bb.putShort(field_10);
		
		for(int i = 0; i < 8; i++)
			bb.putInt(field_12[i]);
		bb.putShort(field_32);
		bb.putShort(field_34);
		bb.putShort(field_36);
	}
	
	public void load(Resource bb)
	{
		field_0 = bb.readShort();
		channel = bb.readShort();
		sectnum = bb.readShort();
		field_6 = bb.readInt();
		field_A = bb.readInt();
		field_E = bb.readShort();
		field_10 = bb.readShort();
		
		for(int i = 0; i < 8; i++)
			field_12[i] = bb.readInt();
		field_32 = bb.readShort();
		field_34 = bb.readShort();
		field_36 = bb.readShort();
	}
	
	public String toString()
	{
		String text = "field_0 " + field_0 + "\r\n";
		text += "channel " + channel + "\r\n";
		text += "sectnum " + sectnum + "\r\n";
		text += "field_6 " + field_6 + "\r\n";
		text += "field_A " + field_A + "\r\n";
		text += "field_E " + field_E + "\r\n";
		text += "field_10 " + field_10 + "\r\n";
		for(int i = 0; i < 8; i++)
			text += "field_12[" + i + "] " + field_12[i] + "\r\n";
		text += "field_32 " + field_32 + "\r\n";
		text += "field_34 " + field_34 + "\r\n";
		text += "field_36 " + field_36 + "\r\n";
		
		return text;
	}

	public void copy(ElevStruct bb) {
		field_0 = bb.field_0;
		channel = bb.channel;
		sectnum = bb.sectnum;
		field_6 = bb.field_6;
		field_A = bb.field_A;
		field_E = bb.field_E;
		field_10 = bb.field_10;
		System.arraycopy(bb.field_12, 0, field_12, 0, 8);
		field_32 = bb.field_32;
		field_34 = bb.field_34;
		field_36 = bb.field_36;
	}
}
