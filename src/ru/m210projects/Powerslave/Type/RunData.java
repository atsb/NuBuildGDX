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

public class RunData {
	
	public static final int size = 8;
	
	public int RunEvent;
	public short RunPtr;
	public short RunNum;
	
	public void save(ByteBuffer bb)
	{
		bb.putInt(RunEvent);
		bb.putShort(RunPtr);
		bb.putShort(RunNum);
	}
	
	public void load(Resource bb)
	{
		RunEvent = bb.readInt();
		RunPtr = bb.readShort();
		RunNum = bb.readShort();
	}
	
	@Override
	public String toString()
	{
		String text = "RunEvent " + RunEvent + "\r\n";;
		text += "RunPtr " + RunPtr + "\r\n";
		text += "RunNum " + RunNum + "\r\n";
		return text;
	}

	public void copy(RunData src) {
		RunEvent = src.RunEvent;
		RunPtr = src.RunPtr;
		RunNum = src.RunNum;
	}
}
