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
import java.nio.ByteOrder;

import ru.m210projects.Build.Pattern.BuildNet.NetInput;

public class Input implements NetInput {
	
	public int xvel, yvel;
	public float avel, horiz;
	public int bits;
	public short nTarget;
	public short nWeaponAim;
	public byte field_F;
	
	public Input(){}
	public Input(Object data)
	{
		setBytes(data);
	}
	
	public int setBytes(Object data) {
		ByteBuffer bb = null;
		if(data instanceof byte[]) {
			bb = ByteBuffer.wrap((byte[])data);
			bb.order(ByteOrder.LITTLE_ENDIAN); 
		} else if(data instanceof ByteBuffer)
			bb = (ByteBuffer) data;
	
		xvel = bb.getInt();
		yvel = bb.getInt();
		avel = bb.getShort();
		bits = bb.getShort();
		nTarget = bb.getShort();
		nWeaponAim = bb.get();
		field_F = bb.get();

		return bb.position();
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
	public void Reset() {
		this.xvel = 0;
		this.yvel = 0;
		this.avel = 0;
		this.horiz = 0;
		this.bits = 0;
		this.nTarget = 0;
		this.nWeaponAim = 0;
		this.field_F = -1;
	}

	@Override
	public NetInput Copy(NetInput netsrc) {
		Input src = (Input) netsrc;
		
		this.xvel = src.xvel;
		this.yvel = src.yvel;
		this.avel = src.avel;
		this.horiz = src.horiz;
		this.bits = src.bits;
		this.nTarget = src.nTarget;
		this.nWeaponAim = src.nWeaponAim;
		this.field_F = src.field_F;
		
		return this;
	}

}
