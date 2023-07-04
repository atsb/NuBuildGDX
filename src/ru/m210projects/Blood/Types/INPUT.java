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

import static ru.m210projects.Blood.LOADSAVE.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ru.m210projects.Build.Pattern.BuildNet.NetInput;
import ru.m210projects.Build.Types.LittleEndian;

public class INPUT implements NetInput {

	private static final int sizeof = 22;
	private static final int gdxsizeof = 27;

	public int syncFlags;
	public int Forward;
	public float Turn;
	public int Strafe;
	public float mlook;

	public boolean Run;
	public boolean Jump;
	public boolean Crouch;
	public boolean Shoot;
	public boolean AltShoot;
	public boolean Lookup;
	public boolean Lookdown;

	public boolean Use;
	public boolean InventoryLeft;
	public boolean InventoryRight;
	public boolean InventoryUse;
	public boolean PrevWeapon;
	public boolean NextWeapon;
	public boolean HolsterWeapon;
	public boolean LookCenter;
	public boolean LookLeft;
	public boolean LookRight;
	public boolean TurnAround;
	public boolean Pause;
	public boolean Quit;
	public boolean Restart;
	public boolean CrouchMode;
	public boolean LastWeapon;

	public boolean UseBeastVision;
	public boolean UseCrystalBall;
	public boolean UseJumpBoots;
	public boolean UseMedKit;

	public int newWeapon;

	public INPUT() {}

	public INPUT(byte[] data, int nVersion) {
    	setBytes(data, 0, nVersion);
	}

	private int bufferVersion;
	private ByteBuffer InputBuffer;
	public byte[] getBytes(int nVersion)
	{
		if(InputBuffer == null || nVersion != bufferVersion)
			InputBuffer = ByteBuffer.allocate(sizeof(nVersion));
		else InputBuffer.clear();
		InputBuffer.order(ByteOrder.LITTLE_ENDIAN);

		int SYNCFLAGS = this.syncFlags;
		if(nVersion >= 277)
			SYNCFLAGS |= this.Run ? 32 : 0;

		InputBuffer.putInt(SYNCFLAGS);
		InputBuffer.put((byte)this.Forward);
		if(nVersion < 300)
			InputBuffer.putShort((short)this.Turn);
		else InputBuffer.putFloat(this.Turn);
		InputBuffer.put((byte)this.Strafe);
		InputBuffer.putInt(getButtonFlag(nVersion));
		InputBuffer.putInt(getKeyFlag(nVersion));
		InputBuffer.putInt(getUseFlag());
		InputBuffer.put((byte)this.newWeapon);
		if(nVersion < 300)
			InputBuffer.put((byte)this.mlook);
		else InputBuffer.putFloat(this.mlook);

		return InputBuffer.array();
	}

	@Override
	public NetInput Copy(NetInput netsrc) {
		INPUT src = (INPUT) netsrc;

		this.syncFlags = src.syncFlags;
		this.Forward = src.Forward;
		this.Turn = src.Turn;
		this.Strafe = src.Strafe;
		this.mlook = src.mlook;

		this.Run = src.Run;
		this.Jump = src.Jump;
		this.Crouch = src.Crouch;
		this.Shoot = src.Shoot;
		this.AltShoot = src.AltShoot;
		this.Lookup = src.Lookup;
		this.Lookdown = src.Lookdown;
		this.TurnAround = src.TurnAround;

		this.Use = src.Use;
		this.InventoryLeft = src.InventoryLeft;
		this.InventoryRight = src.InventoryRight;
		this.InventoryUse = src.InventoryUse;
		this.PrevWeapon = src.PrevWeapon;
		this.NextWeapon = src.NextWeapon;
		this.HolsterWeapon = src.HolsterWeapon;
		this.LookCenter = src.LookCenter;
		this.LookLeft = src.LookLeft;
		this.LookRight = src.LookRight;
		this.Pause = src.Pause;
		this.Quit = src.Quit;
		this.Restart = src.Restart;
		this.CrouchMode = src.CrouchMode;
		this.LastWeapon = src.LastWeapon;

		this.UseBeastVision = src.UseBeastVision;
		this.UseCrystalBall = src.UseCrystalBall;
		this.UseJumpBoots = src.UseJumpBoots;
		this.UseMedKit = src.UseMedKit;

		this.newWeapon = src.newWeapon;

		return this;
	}

	@Override
	public void Reset() {
		this.syncFlags = 0;
		this.Forward = 0;
		this.Turn = 0;
		this.Strafe = 0;
		this.mlook = 0;

		this.Run = false;
		this.Jump = false;
		this.Crouch = false;
		this.Shoot = false;
		this.AltShoot = false;
		this.Lookup = false;
		this.Lookdown = false;
		this.TurnAround = false;

		this.Use = false;
		this.InventoryLeft = false;
		this.InventoryRight = false;
		this.InventoryUse = false;
		this.PrevWeapon = false;
		this.NextWeapon = false;
		this.HolsterWeapon = false;
		this.LookCenter = false;
		this.LookLeft = false;
		this.LookRight = false;
		this.Pause = false;
		this.Quit = false;
		this.Restart = false;
		this.CrouchMode = false;
		this.LastWeapon = false;

		this.newWeapon = 0;

		this.UseBeastVision = false;
		this.UseCrystalBall = false;
		this.UseJumpBoots = false;
		this.UseMedKit = false;
	}

	public int setBytes(byte[] data, int offset, int nVersion) {
		ByteBuffer bb = ByteBuffer.wrap(data);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		bb.position(offset);

    	syncFlags = bb.getInt();
		Forward = bb.get();
		if(nVersion < 300)
			Turn = bb.getShort();
		else Turn = bb.getFloat();
		Strafe = bb.get();

		if(nVersion == 256)
		{
			int BUTTONFLAGS = bb.getInt();
				Run = (BUTTONFLAGS & 1) == 1;
				Jump = ((BUTTONFLAGS & 2) >> 1) == 1;
				Crouch = ((BUTTONFLAGS & 4) >> 2) == 1;
				Shoot = ((BUTTONFLAGS & 8) >> 3) == 1;
				AltShoot = ((BUTTONFLAGS & 16) >> 4) == 1;
				Lookup = ((BUTTONFLAGS & 32) >> 5) == 1;
				Lookdown = ((BUTTONFLAGS & 64) >> 6) == 1;
		}

		if(nVersion >= 277)
		{
			Run = ((syncFlags & 32) >> 5) == 1;
			int BUTTONFLAGS = bb.getInt();
				Jump = (BUTTONFLAGS & 1) == 1;
				Crouch = ((BUTTONFLAGS & 2) >> 1) == 1;
				Shoot = ((BUTTONFLAGS & 4) >> 2) == 1;
				AltShoot = ((BUTTONFLAGS & 8) >> 3) == 1;
				Lookup = ((BUTTONFLAGS &  16) >> 4) == 1;
				Lookdown = ((BUTTONFLAGS & 32) >> 5) == 1;
		}

		int KEYFLAGS = bb.getInt();
		Use = (KEYFLAGS & 1) == 1;
//		Jab = ((KEYFLAGS & 2) >> 1) == 1;
		InventoryLeft = ((KEYFLAGS & 4) >> 2) == 1;
		InventoryRight = ((KEYFLAGS & 8) >> 3) == 1;
		InventoryUse = ((KEYFLAGS & 16) >> 4) == 1;
		PrevWeapon = ((KEYFLAGS & 32) >> 5) == 1;
		NextWeapon = ((KEYFLAGS & 64) >> 6) == 1;
		HolsterWeapon = ((KEYFLAGS & 128) >> 7) == 1;
		LookCenter = ((KEYFLAGS & 256) >> 8) == 1; //1
		LookLeft = ((KEYFLAGS & 512) >> 9) == 1; //2
		LookRight = ((KEYFLAGS & 1024) >> 10) == 1; //4
		TurnAround = ((KEYFLAGS & 2048) >> 11) == 1; //8
		Pause = ((KEYFLAGS & 4096) >> 12) == 1; //16
		Quit = ((KEYFLAGS & 8192) >> 13) == 1; //quit 32
		Restart = ((KEYFLAGS & 16384) >> 14) == 1; //64
		if(nVersion >= 300) {
			CrouchMode = ((KEYFLAGS & 32768) >> 15) == 1;
			LastWeapon = ((KEYFLAGS & 65536) >> 16) == 1;
		}
	int USEFLAGS = bb.getInt();
		UseBeastVision = (USEFLAGS & 1) == 1;
		UseCrystalBall = ((USEFLAGS & 2) >> 1) == 1;
		UseJumpBoots = ((USEFLAGS & 4) >> 2) == 1;
		UseMedKit = ((USEFLAGS & 8) >> 3) == 1;

		newWeapon = bb.get() & 0xFF;

		if(nVersion < 300) {
			mlook = bb.get();
			if(nVersion == 256 || nVersion == 277) {
				if(mlook >= 0)
					mlook = (int) mlook >> 2;
				else mlook = ((int) mlook + 3) >> 2;
			}
		} else mlook = bb.getFloat();

		return bb.position();
	}

	public byte getButtonFlag(int nVersion)
	{
		byte BUTTONFLAGS = 0;
		if(nVersion == 256)
		{
			BUTTONFLAGS |= this.Run?1:0;
			BUTTONFLAGS |= this.Jump?2:0;
			BUTTONFLAGS |= this.Crouch?4:0;
			BUTTONFLAGS |= this.Shoot?8:0;
			BUTTONFLAGS |= this.AltShoot?16:0;
			BUTTONFLAGS |= this.Lookup?32:0;
			BUTTONFLAGS |= this.Lookdown?64:0;
		}

		if(nVersion >= 277)
		{
			BUTTONFLAGS |= this.Jump?1:0;
			BUTTONFLAGS |= this.Crouch?2:0;
			BUTTONFLAGS |= this.Shoot?4:0;
			BUTTONFLAGS |= this.AltShoot?8:0;
			BUTTONFLAGS |= this.Lookup?16:0;
			BUTTONFLAGS |= this.Lookdown?32:0;
		}

		return BUTTONFLAGS;
	}

	public int getKeyFlag(int nVersion)
	{
		int KEYFLAGS = 0;
		KEYFLAGS |= this.Use?(1):0;
		KEYFLAGS |= this.InventoryLeft?(1<<2):0;
		KEYFLAGS |= this.InventoryRight?(1<<3):0;
		KEYFLAGS |= this.InventoryUse?(1<<4):0;
		KEYFLAGS |= this.PrevWeapon?(1<<5):0;
		KEYFLAGS |= this.NextWeapon?(1<<6):0;
		KEYFLAGS |= this.HolsterWeapon?(1<<7):0;
		KEYFLAGS |= this.LookCenter?(1<<8):0;
		KEYFLAGS |= this.LookLeft?(1<<9):0;
		KEYFLAGS |= this.LookRight?(1<<10):0;
		KEYFLAGS |= this.TurnAround?(1<<11):0;
		KEYFLAGS |= this.Pause?(1<<12):0;
		KEYFLAGS |= this.Quit?(1<<13):0;
		KEYFLAGS |= this.Restart?(1<<14):0;
		if(nVersion >= 300) {
			KEYFLAGS |= this.CrouchMode?(1<<15):0;
			KEYFLAGS |= this.LastWeapon?(1<<16):0;
			KEYFLAGS |= this.Run?(1<<17):0;
		}

		return KEYFLAGS;
	}

	public byte getUseFlag()
	{
		byte USEFLAGS = 0;
		USEFLAGS |= this.UseBeastVision?1:0;
		USEFLAGS |= this.UseCrystalBall?2:0;
		USEFLAGS |= this.UseJumpBoots?4:0;
		USEFLAGS |= this.UseMedKit?8:0;

		return USEFLAGS;
	}

 	@Override
	public String toString()
	{
		String out = "";
		if(syncFlags != 0)
		out += "syncFlags " + syncFlags + " \r\n";
		if(Forward != 0)
		out += "Forward " + Forward + " \r\n";
		if(Turn != 0)
		out += "Turn " + Turn + " \r\n";
		if(Strafe != 0)
		out += "Strafe " + Strafe + " \r\n";
		if(Run)
		out += "Run " + Run + " \r\n";
		if(Jump)
		out += "Jump " + Jump + " \r\n";
		if(Crouch)
		out += "Crouch " + Crouch + " \r\n";
		if(Shoot)
		out += "Shoot " + Shoot + " \r\n";
		if(AltShoot)
		out += "AltShoot " + AltShoot + " \r\n";
		if(Lookup)
		out += "Lookup " + Lookup + " \r\n";
		if(Lookdown)
		out += "Lookdown " + Lookdown + " \r\n";
		if(TurnAround)
		out += "TurnAround " + TurnAround + " \r\n";
		if(Use)
		out += "Use " + Use + " \r\n";
		if(InventoryLeft)
		out += "InventoryLeft " + InventoryLeft + " \r\n";
		if(InventoryRight)
		out += "InventoryRight " + InventoryRight + " \r\n";
		if(InventoryUse)
		out += "InventoryUse " + InventoryUse + " \r\n";
		if(PrevWeapon)
		out += "PrevWeapon " + PrevWeapon + " \r\n";
		if(NextWeapon)
		out += "NextWeapon " + NextWeapon + " \r\n";
		if(HolsterWeapon)
		out += "HolsterWeapon " + HolsterWeapon + " \r\n";
		if(LookCenter)
		out += "LookCenter " + LookCenter + " \r\n";
		if(LookLeft)
		out += "LookLeft " + LookLeft + " \r\n";
		if(LookRight)
		out += "LookRight " + LookRight + " \r\n";
		if(Pause)
		out += "Pause " + Pause + " \r\n";
		if(Quit)
		out += "Master " + Quit + " \r\n";
		if(Restart)
		out += "Restart " + Restart + " \r\n";
		if(CrouchMode)
		out += "CrouchMode " + CrouchMode + " \r\n";
		if(LastWeapon)
		out += "LastWeapon " + LastWeapon + " \r\n";
		if(UseBeastVision)
		out += "UseBeastVision " + UseBeastVision + " \r\n";
		if(UseCrystalBall)
		out += "UseCrystalBall " + UseCrystalBall + " \r\n";
		if(UseJumpBoots)
		out += "UseJumpBoots " + UseJumpBoots + " \r\n";
		if(UseMedKit)
		out += "UseMedKit " + UseMedKit + " \r\n";
		if(newWeapon != 0)
		out += "newWeapon " + newWeapon + " \r\n";
		if(mlook != 0)
		out += "mlook " + mlook + " \r\n";

		return out;
	}

	public static int sizeof(int nVersion)
	{
		int size = sizeof;
		if(nVersion >= 300) size = gdxsizeof;
		return size;
	}

	@Override
	public int GetInput(byte[] p, int offset, NetInput old) {
		ByteBuffer bb = ByteBuffer.wrap(p);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		bb.position(offset);

		syncFlags = bb.get();
		if ( (syncFlags & 32) != 0)
			Forward = bb.get();
		if ( (syncFlags & 64) != 0)
			Turn = bb.getFloat();
		if ( (syncFlags & 128) != 0)
			Strafe = bb.get();

		if((syncFlags & 1) != 0) {
			int BUTTONFLAGS = bb.get();
			Jump = (BUTTONFLAGS & 1) == 1;
			Crouch = ((BUTTONFLAGS & 2) >> 1) == 1;
			Shoot = ((BUTTONFLAGS & 4) >> 2) == 1;
			AltShoot = ((BUTTONFLAGS & 8) >> 3) == 1;
			Lookup = ((BUTTONFLAGS &  16) >> 4) == 1;
			Lookdown = ((BUTTONFLAGS & 32) >> 5) == 1;
		}
		if((syncFlags & 2) != 0) {
			int KEYFLAGS = bb.getInt();
			Use = (KEYFLAGS & 1) == 1;
			InventoryLeft = ((KEYFLAGS & 4) >> 2) == 1;
			InventoryRight = ((KEYFLAGS & 8) >> 3) == 1;
			InventoryUse = ((KEYFLAGS & 16) >> 4) == 1;
			PrevWeapon = ((KEYFLAGS & 32) >> 5) == 1;
			NextWeapon = ((KEYFLAGS & 64) >> 6) == 1;
			HolsterWeapon = ((KEYFLAGS & 128) >> 7) == 1;
			LookCenter = ((KEYFLAGS & 256) >> 8) == 1;
			LookLeft = ((KEYFLAGS & 512) >> 9) == 1;
			LookRight = ((KEYFLAGS & 1024) >> 10) == 1;
			TurnAround = ((KEYFLAGS & 2048) >> 11) == 1;
			Pause = ((KEYFLAGS & 4096) >> 12) == 1;
			Quit = ((KEYFLAGS & 8192) >> 13) == 1;
			Restart = ((KEYFLAGS & 16384) >> 14) == 1;
			CrouchMode = ((KEYFLAGS & 32768) >> 15) == 1;
			LastWeapon = ((KEYFLAGS & 65536) >> 16) == 1;
			Run = ((KEYFLAGS & 131072) >> 17) == 1;
		}
		if((syncFlags & 4) != 0) {
			int USEFLAGS = bb.get();
			UseBeastVision = (USEFLAGS & 1) == 1;
			UseCrystalBall = ((USEFLAGS & 2) >> 1) == 1;
			UseJumpBoots = ((USEFLAGS & 4) >> 2) == 1;
			UseMedKit = ((USEFLAGS & 8) >> 3) == 1;
		}
		if((syncFlags & 8) != 0)
			newWeapon = bb.get() & 0xFF;
		if((syncFlags & 16) != 0) {
			mlook = bb.getFloat();
		}

		return bb.position();
	}

	@Override
	public int PutInput(byte[] p, int offset, NetInput old) {
		byte ButtonFlag = getButtonFlag(gdxSave);
		int KeyFlag = getKeyFlag(gdxSave);
		byte UseFlag = getUseFlag();

		if(ButtonFlag != 0)
			syncFlags |= 1;
		if(KeyFlag != 0)
			syncFlags |= 2;
		if(UseFlag != 0)
			syncFlags |= 4;
		if(newWeapon != 0)
			syncFlags |= 8;
		if(mlook != 0)
			syncFlags |= 16;
		if(Forward != 0)
			syncFlags |= 32;
		if(Turn != 0)
			syncFlags |= 64;
		if(Strafe != 0)
			syncFlags |= 128;

		int ptr = putByte(p, offset, syncFlags);
		if ( (syncFlags & 32) != 0)
			ptr = putByte(p, ptr, Forward);
		if ( (syncFlags & 64) != 0) {
			LittleEndian.putFloat(p, ptr, Turn); ptr += 4;
		}
		if ( (syncFlags & 128) != 0)
			ptr = putByte(p, ptr, Strafe);
        if ( (syncFlags & 1) != 0)
        	ptr = putByte(p, ptr, ButtonFlag);
        if ( (syncFlags & 2) != 0 )
        {
        	LittleEndian.putInt(p, ptr, KeyFlag);
        	ptr += 4;
        }
        if ( (syncFlags & 4) != 0)
        	ptr = putByte(p, ptr, UseFlag);
        if ( (syncFlags & 8) != 0)
        	ptr = putByte(p, ptr, newWeapon);
        if ( (syncFlags & 16) != 0) {
	        LittleEndian.putFloat(p, ptr, mlook);
	        ptr += 4;
        }

        return ptr;
	}

	private int putByte(byte[] p, int ptr, int value)
	{
		p[ptr] = (byte) value;
		return ptr += 1;
	}
}
