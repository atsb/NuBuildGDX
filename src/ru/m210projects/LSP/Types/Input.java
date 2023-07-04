// This file is part of LSPGDX.
// Copyright (C) 2020  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// LSPGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// LSPGDX is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with LSPGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.LSP.Types;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ru.m210projects.Build.FileHandle.Resource;
import ru.m210projects.Build.Pattern.BuildNet.NetInput;

public class Input implements NetInput {

	public int xvel, yvel;
	public float angvel;
	public int bits;
	public float horiz;
	
	public Input(){}
	
	public Input(Resource bb)
	{
		xvel = bb.readInt();
		yvel = bb.readInt();
		angvel = bb.readFloat();
		bits = bb.readInt();
		horiz = bb.readFloat();
	}
	
	@Override
	public int GetInput(byte[] p, int offset, NetInput oldInput) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int PutInput(byte[] p, int offset, NetInput oldInput) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void Reset()
	{
		xvel = 0;
		yvel = 0;
		angvel = 0;
		horiz = 0;
		bits = 0;
	}

	@Override
	public NetInput Copy(NetInput src) {
		Input i = (Input) src;
		
		this.xvel = i.xvel;
		this.yvel = i.yvel;
		this.angvel = i.angvel;
		this.horiz = i.horiz;
		this.bits = i.bits;
		
		return this;
	}

	public static int sizeof(int version) {
		return 20;
	}
	
	private static ByteBuffer InputBuffer = ByteBuffer.allocate(sizeof(100)).order(ByteOrder.LITTLE_ENDIAN); 
	public byte[] getBytes()
	{
		InputBuffer.clear();
		InputBuffer.putInt(xvel);
		InputBuffer.putInt(yvel);
		InputBuffer.putFloat(angvel);
		InputBuffer.putInt(bits);
		InputBuffer.putFloat(horiz);

		return InputBuffer.array();
	}

}
