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

import static ru.m210projects.Build.Engine.sprite;
import static ru.m210projects.LSP.Main.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ru.m210projects.Build.FileHandle.Resource;
import ru.m210projects.Build.Pattern.Tools.Interpolation.ILoc;

public class PlayerStruct {
	public final static int sizeof = 164;

	public boolean noclip = false;
	public Input pInput = new Input();

	public int x, y, z, ox, oy, oz;
	public float oang, ang, ohoriz, horiz, nBobbing;
	public short sectnum, osectnum;

	public byte gViewMode;
	public int ozoom, zoom;
	public short nSprite;

	public int hvel;
	public byte nWeaponImpact;
	public short nWeaponImpactAngle;
	public short nHealth;
	public short nWeaponState;
	public short nBobCount;
	public short nWeaponSeq;
	public short nMana;
	public short[] nAmmo = { 0, 100, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0 };
	public short nLastWeapon;

	public short nWeapon;
	public short nLastChoosedWeapon;
	public short nLastManaWeapon;

	public short word_586FC; // sector
	public short word_58794; // weapon sound
	public short word_5878C; // weapon
	public int nWeaponShootCount;
	public int nSlimeDamageCount;

	public short nPlayerStatus; // live = 0, dead = 1, god = 5
	public int nFirstWeaponDamage;
	public int nSecondWeaponDamage;
	public short nRandDamage1;
	public short nRandDamage2;

	public int nWeaponClock;
	public int isWeaponFire;
	public int nNewWeapon;
	public int isWeaponsSwitching;
	public int nSwitchingClock;
	
	private short invWeapon, invChoosedWeapon, invMannaWeapon, invLastWeapon, invRand1, invRand2;
	
	public void savePlayersInventory()
	{
		invWeapon = nWeapon;
		invChoosedWeapon = nLastChoosedWeapon;
		invMannaWeapon = nLastManaWeapon;
		invLastWeapon = nLastWeapon;
		invRand1 = nRandDamage1;
		invRand2 = nRandDamage2;
	}
	
	public void loadPlayersInventory()
	{
		nWeapon = invWeapon;
		nLastChoosedWeapon = invChoosedWeapon;
		nLastManaWeapon = invMannaWeapon;
		nLastWeapon = invLastWeapon;
		nRandDamage1 = invRand1;
		nRandDamage2 = invRand2;
	}

	public void UpdatePlayerLoc() {
		ILoc oldLoc = game.pInt.getsprinterpolate(nSprite);
		if (oldLoc != null) {
			oldLoc.x = sprite[nSprite].x;
			oldLoc.y = sprite[nSprite].y;
			oldLoc.z = sprite[nSprite].z;
		} else
			game.pInt.setsprinterpolate(nSprite, sprite[nSprite]);

		ox = x;
		oy = y;
		oz = z;
		ozoom = zoom;
		ohoriz = horiz + nBobbing;
		oang = ang;
	}

	public void calcRandomVariables() {
		int v24 = engine.krand();

		nRandDamage1 = (short) (v24 % 9 + 7);
		nRandDamage2 = (short) ((v24 >> 5) % 5 + 6);
	}

	private static final ByteBuffer buffer = ByteBuffer.allocate(sizeof).order(ByteOrder.LITTLE_ENDIAN);

	public byte[] getBytes() {
		buffer.clear();

		buffer.putInt(x);
		buffer.putInt(y);
		buffer.putInt(z);
		buffer.putInt(ox);
		buffer.putInt(oy);
		buffer.putInt(oz);
		buffer.putFloat(ang);
		buffer.putFloat(oang);
		buffer.putFloat(horiz);
		buffer.putFloat(ohoriz);
		buffer.putFloat(nBobbing);
		buffer.putShort(sectnum);
		buffer.putShort(osectnum);
		buffer.put(gViewMode);
		buffer.putInt(zoom);
		buffer.putInt(ozoom);
		buffer.putShort(nSprite);
		buffer.putInt(hvel);
		buffer.put(nWeaponImpact);
		buffer.putShort(nWeaponImpactAngle);
		buffer.putShort(nHealth);
		buffer.putShort(nWeaponState);
		buffer.putShort(nBobCount);
		buffer.putShort(nWeaponSeq);
		buffer.putShort(nMana);
		for (int i = 0; i < nAmmo.length; i++)
			buffer.putShort(nAmmo[i]);
		buffer.putShort(nLastWeapon);
		buffer.putShort(nWeapon);
		buffer.putShort(nLastChoosedWeapon);
		buffer.putShort(nLastManaWeapon);
		buffer.putShort(word_586FC);
		buffer.putShort(word_58794);
		buffer.putShort(word_5878C);

		buffer.putInt(nWeaponShootCount);
		buffer.putInt(nSlimeDamageCount);

		buffer.putShort(nPlayerStatus);
		buffer.putInt(nFirstWeaponDamage);
		buffer.putInt(nSecondWeaponDamage);
		buffer.putShort(nRandDamage1);
		buffer.putShort(nRandDamage2);

		buffer.putInt(nWeaponClock);
		buffer.putInt(isWeaponFire);
		buffer.putInt(nNewWeapon);
		buffer.putInt(isWeaponsSwitching);
		buffer.putInt(nSwitchingClock);

		return buffer.array();
	}

	public void copy(PlayerStruct src) {
		pInput.Reset();

		x = src.x;
		y = src.y;
		z = src.z;
		ox = src.ox;
		oy = src.oy;
		oz = src.oz;
		ang = src.ang;
		oang = src.oang;
		horiz = src.horiz;
		ohoriz = src.ohoriz;
		nBobbing = src.nBobbing;
		sectnum = src.sectnum;
		osectnum = src.osectnum;
		gViewMode = src.gViewMode;
		zoom = src.zoom;
		ozoom = src.ozoom;
		nSprite = src.nSprite;
		hvel = src.hvel;
		nWeaponImpact = src.nWeaponImpact;
		nWeaponImpactAngle = src.nWeaponImpactAngle;
		nHealth = src.nHealth;
		nWeaponState = src.nWeaponState;
		nBobCount = src.nBobCount;
		nWeaponSeq = src.nWeaponSeq;
		nMana = src.nMana;
		for (int i = 0; i < nAmmo.length; i++)
			nAmmo[i] = src.nAmmo[i];
		nLastWeapon = src.nLastWeapon;
		nWeapon = src.nWeapon;
		nLastChoosedWeapon = src.nLastChoosedWeapon;
		nLastManaWeapon = src.nLastManaWeapon;
		word_586FC = src.word_586FC;
		word_58794 = src.word_58794;
		word_5878C = src.word_5878C;
		nWeaponShootCount = src.nWeaponShootCount;
		nSlimeDamageCount = src.nSlimeDamageCount;
		nPlayerStatus = src.nPlayerStatus;
		nFirstWeaponDamage = src.nFirstWeaponDamage;
		nSecondWeaponDamage = src.nSecondWeaponDamage;
		nRandDamage1 = src.nRandDamage1;
		nRandDamage2 = src.nRandDamage2;
		nWeaponClock = src.nWeaponClock;
		isWeaponFire = src.isWeaponFire;
		nNewWeapon = src.nNewWeapon;
		isWeaponsSwitching = src.isWeaponsSwitching;
		nSwitchingClock = src.nSwitchingClock;
	}

	public void set(Resource bb) {
		pInput.Reset();

		x = bb.readInt();
		y = bb.readInt();
		z = bb.readInt();
		ox = bb.readInt();
		oy = bb.readInt();
		oz = bb.readInt();
		ang = bb.readFloat();
		oang = bb.readFloat();
		horiz = bb.readFloat();
		ohoriz = bb.readFloat();
		nBobbing = bb.readFloat();
		sectnum = bb.readShort();
		osectnum = bb.readShort();
		gViewMode = bb.readByte();
		zoom = bb.readInt();
		ozoom = bb.readInt();
		nSprite = bb.readShort();
		hvel = bb.readInt();
		nWeaponImpact = bb.readByte();
		nWeaponImpactAngle = bb.readShort();
		nHealth = bb.readShort();
		nWeaponState = bb.readShort();
		nBobCount = bb.readShort();
		nWeaponSeq = bb.readShort();
		nMana = bb.readShort();
		for (int i = 0; i < nAmmo.length; i++)
			nAmmo[i] = bb.readShort();
		nLastWeapon = bb.readShort();
		nWeapon = bb.readShort();
		nLastChoosedWeapon = bb.readShort();
		nLastManaWeapon = bb.readShort();
		word_586FC = bb.readShort();
		word_58794 = bb.readShort();
		word_5878C = bb.readShort();
		nWeaponShootCount = bb.readInt();
		nSlimeDamageCount = bb.readInt();
		nPlayerStatus = bb.readShort();
		nFirstWeaponDamage = bb.readInt();
		nSecondWeaponDamage = bb.readInt();
		nRandDamage1 = bb.readShort();
		nRandDamage2 = bb.readShort();
		nWeaponClock = bb.readInt();
		isWeaponFire = bb.readInt();
		nNewWeapon = bb.readInt();
		isWeaponsSwitching = bb.readInt();
		nSwitchingClock = bb.readInt();
	}

}
