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

import static ru.m210projects.Powerslave.Globals.nDoppleSprite;
import static ru.m210projects.Powerslave.Main.game;
import static ru.m210projects.Build.Engine.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

import ru.m210projects.Build.FileHandle.Resource;
import ru.m210projects.Build.Pattern.Tools.Interpolation.ILoc;

public class PlayerStruct {
	
	public PLocation prevView = new PLocation();

	public int nPlayer;
	public short HealthAmount;
	public short animCount;
	public short anim_;
	public short spriteId;
	public short mummified;
	public short network;
	public short invisibility;
	public short AirAmount;
	public short seq;
	public short AirMaskAmount;
	public short KeysBitMask;
	public short MagicAmount;
	public byte ItemsAmount[] = new byte[8];
	public short AmmosAmount[] = new short[9];
	public short currentWeapon;
	public short seqOffset;
	public short weaponFire;
	public short newWeapon;
	public short weaponState;
	public short lastWeapon;
	public short RunFunc;
	
	//GDX
	public short turnAround;
	public short lastUsedWeapon;
	public float ang;
	public float horiz;
	public short eyelevel;
	public boolean crouch_toggle;

	public void UpdatePlayerLoc() {
		ILoc oldLoc = game.pInt.getsprinterpolate(spriteId);
        if(oldLoc != null)
        {
        	oldLoc.x = sprite[spriteId].x;
        	oldLoc.y = sprite[spriteId].y;
        	oldLoc.z = sprite[spriteId].z;
        } else game.pInt.setsprinterpolate(spriteId, sprite[spriteId]);
        game.pInt.setsprinterpolate(nDoppleSprite[nPlayer], sprite[nDoppleSprite[nPlayer]]);

		prevView.x = sprite[spriteId].x;
		prevView.y = sprite[spriteId].y;
		prevView.z = sprite[spriteId].z + eyelevel;
		prevView.ang = ang;
		prevView.horiz = horiz;
	}
	
	public static int size(boolean isGDX)
	{
		if(isGDX)
			return 64 + 15;
		return 64;
	}
	
	private byte[] buf = new byte[64 + 15];
	private ByteBuffer bb = ByteBuffer.wrap(buf).order(ByteOrder.LITTLE_ENDIAN);
	public byte[] getBytes(boolean isGDX) {
		bb.rewind();
		
		bb.putShort(HealthAmount);
		bb.putShort(animCount);
		bb.putShort(anim_);
		bb.putShort(spriteId);
		bb.putShort(mummified);
		bb.putShort(network);
		bb.putShort(invisibility);
		bb.putShort(AirAmount);
		bb.putShort(seq);
		bb.putShort(AirMaskAmount);
		bb.putShort(KeysBitMask);
		bb.putShort(MagicAmount);
		bb.put(ItemsAmount);
		for(int i = 0; i < 9; i++)
			bb.putShort(AmmosAmount[i]);
		bb.putShort(currentWeapon);
		bb.putShort(seqOffset);
		bb.putShort(weaponFire);
		bb.putShort(newWeapon);
		bb.putShort(weaponState);
		bb.putShort(lastWeapon);
		bb.putShort(RunFunc);
		
		if(isGDX)
		{
			bb.putShort(turnAround);
			bb.putShort(lastUsedWeapon);
			bb.putFloat(ang);
			bb.putFloat(horiz);
			bb.putShort(eyelevel);
			bb.put(crouch_toggle ? (byte) 1 : 0);
		}
		
		return buf;
	}
	
	public void setBytes(Resource bb, boolean isGDX)
	{
		HealthAmount = bb.readShort();
		animCount = bb.readShort();
		anim_ = bb.readShort();
		spriteId = bb.readShort();
		mummified = bb.readShort();
		network = bb.readShort();
		invisibility = bb.readShort();
		AirAmount = bb.readShort();
		seq = bb.readShort();
		AirMaskAmount = bb.readShort();
		KeysBitMask = bb.readShort();
		MagicAmount = bb.readShort();
		bb.read(ItemsAmount);
		for(int i = 0; i < 9; i++)
			AmmosAmount[i] = bb.readShort();
		currentWeapon = bb.readShort();
		seqOffset = bb.readShort();
		weaponFire = bb.readShort();
		newWeapon = bb.readShort();
		weaponState = bb.readShort();
		lastWeapon = bb.readShort();
		RunFunc = bb.readShort();
		
		if(isGDX)
		{
			turnAround = bb.readShort();
			lastUsedWeapon = bb.readShort();
			ang = bb.readFloat();
			horiz = bb.readFloat();
			eyelevel = bb.readShort();
			crouch_toggle = bb.readBoolean();
		}
	}
	
	public void reset() {
		HealthAmount = 0;
		animCount = 0;
		anim_ = 0;
		spriteId = 0;
		mummified = 0;
		network = 0;
		invisibility = 0;
		AirAmount = 0;
		seq = 0;
		AirMaskAmount = 0;
		KeysBitMask = 0;
		MagicAmount = 0;
		Arrays.fill(ItemsAmount, (byte) 0);
		Arrays.fill(AmmosAmount, (short) 0);
		currentWeapon = 0;
		seqOffset = 0;
		weaponFire = 0;
		newWeapon = 0;
		weaponState = 0;
		lastWeapon = 0;
		RunFunc = 0;
		
		turnAround = 0;
		lastUsedWeapon = 0;
		ang = 0;
		horiz = 92;
		eyelevel = -14080;
		crouch_toggle = false;
	}
	
	public void copy(PlayerStruct src) {
		HealthAmount = src.HealthAmount;
		animCount = src.animCount;
		anim_ = src.anim_;
		spriteId = src.spriteId;
		mummified = src.mummified;
		network = src.network;
		invisibility = src.invisibility;
		AirAmount = src.AirAmount;
		seq = src.seq;
		AirMaskAmount = src.AirMaskAmount;
		KeysBitMask = src.KeysBitMask;
		MagicAmount = src.MagicAmount;
		System.arraycopy(src.ItemsAmount, 0, ItemsAmount, 0, ItemsAmount.length);
		System.arraycopy(src.AmmosAmount, 0, AmmosAmount, 0, AmmosAmount.length);
		currentWeapon = src.currentWeapon;
		seqOffset = src.seqOffset;
		weaponFire = src.weaponFire;
		newWeapon = src.newWeapon;
		weaponState = src.weaponState;
		lastWeapon = src.lastWeapon;
		RunFunc = src.RunFunc;
		
		turnAround = src.turnAround;
		lastUsedWeapon = src.lastUsedWeapon;
		ang = src.ang;
		horiz = src.horiz;
		eyelevel = src.eyelevel;
		crouch_toggle = src.crouch_toggle;
	}
}
