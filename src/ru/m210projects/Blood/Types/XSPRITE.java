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

import java.util.Arrays;

import ru.m210projects.Blood.AI.AISTATE;
import ru.m210projects.Build.Types.LittleEndian;

/**
 * This class is a part of Blood's XSystem. {@link XSPRITE} types are:
 * <ul>
 * <li>Markers (not implemented)</li>
 * <li>Switches</li>
 * <li>Weapons</li>
 * </ul>
 */

public class XSPRITE {
	public static final int sizeof = 56;
	private static final byte[] xdata = new byte[XSPRITE.sizeof];

	/**
	 * An index to {@link ru.m210projects.Build.Engine#sprite} array linking this
	 * {@link XSPRITE} with its corresponding {@link SPRITE}.
	 */

	public int reference;
	public short state;

	public int busy;

	public short txID;
	public short rxID;

	public short command;
	public boolean triggerOn;
	public boolean triggerOff;
	public short busyTime;
	public short waitTime;
	public short restState;

	public boolean Push;
	public boolean Vector;
	public boolean Impact;
	public boolean Pickup;
	public boolean Touch;
	public boolean Sight;
	public boolean Proximity;
	public boolean DudeLockout;

	public boolean triggerOnce;

	public int l1;
	public int l2;
	public int l3;
	public int l4;
	public int l5;
	public int lSkill;
	public boolean lS;
	public boolean lB;
	public boolean lC;
	public boolean lT;

	/**
	 * Storage area for miscellaneous data. Use is {@link XSPRITE} type dependant.
	 */
	public short data1;
	public short data2;
	public short data3;
	public int data4;
	public short key;
	public short wave;

	public short lockMsg;
	public short dropMsg;

	public boolean Decoupled;
	public boolean isTriggered;
	public boolean Interrutable;
	public int respawnPending; // 0:not, 1: red, 2:yellow, 3:green

	public int goalAng; // dudes
	public int dodgeDir; // dudes
	public int Locked;
	public int palette;

	public int respawn; // 0=optional never, 1=optional always, 2=always, 3=never

	// this stuff needed for dudes
	public int moveState; // same as player move states

	public int health;
	public boolean dudeDeaf; // can't hear players
	public boolean dudeAmbush; // must be triggered manually
	public boolean dudeGuard; // won't leave sector
	public boolean dudeFlag4;

	public int target;
	public int targetX;
	public int targetY;
	public int targetZ;

	public int burnTime;
	public int burnSource;
	public int height;

	public int stateTimer;
	public int floorZ;

	public AISTATE aiState;

	public XSPRITE() {
	}

	public void init(byte[] data) {
//			int breference = BitReader.bread(data, 0, 0, 13);
		state = (short) BitHandler.buread(data, 1, 6, 6);
		busy = BitHandler.buread(data, 1, 7, 23);
		txID = (short) BitHandler.buread(data, 4, 0, 9);
		rxID = (short) BitHandler.buread(data, 5, 2, 11);
		command = (short) BitHandler.buread(data, 6, 4, 11);
		triggerOn = BitHandler.buread(data, 7, 4, 4) != 0;
		triggerOff = BitHandler.buread(data, 7, 5, 5) != 0;
		wave = (short) BitHandler.buread(data, 7, 6, 7);
		busyTime = (short) BitHandler.buread(data, 8, 0, 11);
		waitTime = (short) BitHandler.buread(data, 9, 4, 15);
		restState = (short) BitHandler.buread(data, 11, 0, 0);
		Interrutable = BitHandler.buread(data, 11, 1, 1) != 0;
//			boolean difficulty = BitReader.bread(data, 11, 2, 3) != 0;
		respawnPending = BitHandler.buread(data, 11, 4, 6);
		lT = BitHandler.buread(data, 11, 7, 7) != 0;
		dropMsg = LittleEndian.getUByte(data, 12);
		Decoupled = BitHandler.buread(data, 13, 0, 0) != 0;
		triggerOnce = BitHandler.buread(data, 13, 1, 1) != 0;
		isTriggered = BitHandler.buread(data, 13, 2, 2) != 0;
		key = (short) BitHandler.buread(data, 13, 3, 5);
		Push = BitHandler.buread(data, 13, 6, 6) != 0;
		Vector = BitHandler.buread(data, 13, 7, 7) != 0;
		Impact = BitHandler.buread(data, 14, 0, 0) != 0;
		Pickup = BitHandler.buread(data, 14, 1, 1) != 0;
		Touch = BitHandler.buread(data, 14, 2, 2) != 0;
		Sight = BitHandler.buread(data, 14, 3, 3) != 0;
		Proximity = BitHandler.buread(data, 14, 4, 4) != 0;
//			int breserved1 = BitReader.bread(data, 14, 5, 5);
//			int breserved2 = BitReader.bread(data, 14, 6, 6);
		l1 = BitHandler.buread(data, 14, 7, 7);
		l2 = BitHandler.buread(data, 15, 0, 0);
		l3 = BitHandler.buread(data, 15, 1, 1);
		l4 = BitHandler.buread(data, 15, 2, 2);
		l5 = BitHandler.buread(data, 15, 3, 3);
		lS = BitHandler.buread(data, 15, 4, 4) != 0;
		lB = BitHandler.buread(data, 15, 5, 5) != 0;
		lC = BitHandler.buread(data, 15, 6, 6) != 0;

		lSkill |= (l1 | (l2 << 1) | (l3 << 2) | (l4 << 3) | (l5 << 4));

		DudeLockout = BitHandler.buread(data, 15, 7, 7) != 0;
		data1 = LittleEndian.getShort(data, 16);
		data2 = LittleEndian.getShort(data, 18);
		data3 = LittleEndian.getShort(data, 20);
		goalAng = BitHandler.buread(data, 22, 0, 10);
		dodgeDir = BitHandler.bsread(data, 23, 3, 4);
		Locked = BitHandler.buread(data, 23, 5, 5);
		palette = BitHandler.buread(data, 23, 6, 7);
		respawn = BitHandler.buread(data, 24, 0, 1);
		data4 = BitHandler.buread(data, 24, 2, 17);
		moveState = BitHandler.buread(data, 26, 2, 7);
		lockMsg = LittleEndian.getUByte(data, 27);
		health = BitHandler.buread(data, 28, 0, 11);
		dudeDeaf = BitHandler.buread(data, 29, 4, 4) != 0;
		dudeAmbush = BitHandler.buread(data, 29, 5, 5) != 0;
		dudeGuard = BitHandler.buread(data, 29, 6, 6) != 0;
		dudeFlag4 = BitHandler.buread(data, 29, 7, 7) != 0;
		target = LittleEndian.getShort(data, 30);
		targetX = LittleEndian.getInt(data, 32);
		targetY = LittleEndian.getInt(data, 36);
		targetZ = LittleEndian.getInt(data, 40);
		burnTime = LittleEndian.getShort(data, 44);
		burnSource = LittleEndian.getShort(data, 46);
		height = LittleEndian.getShort(data, 48);
		stateTimer = LittleEndian.getShort(data, 50);
//			int aiState = LittleEndian.getInt(data, 52);
	}

	public String toString() {
		String out = "reference " + reference + " \r\n";
		out += "state " + state + " \r\n";
		out += "busy " + busy + " \r\n";
		out += "txID " + txID + " \r\n";
		out += "rxID " + rxID + " \r\n";
		out += "command " + command + " \r\n";
		out += "triggerOn " + triggerOn + " \r\n";
		out += "triggerOff " + triggerOff + " \r\n";
		out += "wave " + wave + " \r\n";
		out += "busyTime " + busyTime + " \r\n";
		out += "waitTime " + waitTime + " \r\n";
		out += "restState " + restState + " \r\n";
		out += "Interrutable " + Interrutable + " \r\n";
		out += "respawnPending " + respawnPending + " \r\n";
		out += "lT " + lT + " \r\n";
		out += "dropMsg " + dropMsg + " \r\n";
		out += "Decoupled " + Decoupled + " \r\n";
		out += "triggerOnce " + triggerOnce + " \r\n";
		out += "isTriggered " + isTriggered + " \r\n";
		out += "key " + key + " \r\n";
		out += "Push " + Push + " \r\n";
		out += "Vector " + Vector + " \r\n";
		out += "Impact " + Impact + " \r\n";
		out += "Pickup " + Pickup + " \r\n";
		out += "Touch " + Touch + " \r\n";
		out += "Sight " + Sight + " \r\n";
		out += "Proximity " + Proximity + " \r\n";
		out += "l1 " + l1 + " \r\n";
		out += "l2 " + l2 + " \r\n";
		out += "l3 " + l3 + " \r\n";
		out += "l4 " + l4 + " \r\n";
		out += "l5 " + l5 + " \r\n";
		out += "lS " + lS + " \r\n";
		out += "lB " + lB + " \r\n";
		out += "lC " + lC + " \r\n";
		out += "DudeLockout " + DudeLockout + " \r\n";
		out += "data1 " + data1 + " \r\n";
		out += "data2 " + data2 + " \r\n";
		out += "data3 " + data3 + " \r\n";
		out += "goalAng " + goalAng + " \r\n";
		out += "dodgeDir " + dodgeDir + " \r\n";
		out += "Locked " + Locked + " \r\n";
		out += "palette " + palette + " \r\n";
		out += "respawn " + respawn + " \r\n";
		out += "data4 " + data4 + " \r\n";
		out += "moveState " + moveState + " \r\n";
		out += "lockMsg " + lockMsg + " \r\n";
		out += "health " + health + " \r\n";
		out += "dudeDeaf " + dudeDeaf + " \r\n";
		out += "dudeAmbush " + dudeAmbush + " \r\n";
		out += "dudeGuard " + dudeGuard + " \r\n";
		out += "dudeFlag4 " + dudeFlag4 + " \r\n";
		out += "target " + target + " \r\n";
		out += "targetX " + targetX + " \r\n";
		out += "targetY " + targetY + " \r\n";
		out += "targetZ " + targetZ + " \r\n";
		out += "burnTime " + burnTime + " \r\n";
		out += "burnSource " + burnSource + " \r\n";
		out += "height " + height + " \r\n";
		out += "stateTimer " + stateTimer + " \r\n";
		out += "aiState " + aiState + " \r\n";
//		if (aiState != null)
//			out += "aiState " + aiState.name + " \r\n";

		return out;
	}

	public void free() {
		reference = 0;
		state = 0;
		busy = 0;
		txID = 0;
		rxID = 0;

		command = 0;
		triggerOn = false;
		triggerOff = false;
		busyTime = 0;
		waitTime = 0;
		restState = 0;

		Push = false;
		Vector = false;
		Impact = false;
		Pickup = false;
		Touch = false;
		Sight = false;
		Proximity = false;
		DudeLockout = false;
		triggerOnce = false;

		l1 = 0;
		l2 = 0;
		l3 = 0;
		l4 = 0;
		l5 = 0;
		lSkill = 0;
		lS = false;
		lB = false;
		lC = false;
		lT = false;

		data1 = 0;
		data2 = 0;
		data3 = 0;
		data4 = 0;
		key = 0;
		wave = 0;

		lockMsg = 0;
		dropMsg = 0;

		Decoupled = false;
		isTriggered = false;
		Interrutable = false;

		goalAng = 0; // dudes
		dodgeDir = 0; // dudes
		Locked = 0;
		palette = 0;

		respawn = 0; // 0=optional never, 1=optional always, 2=always, 3=never
		respawnPending = 0;

		// this stuff needed for dudes
		moveState = 0; // same as player move states

		health = 0;
		dudeDeaf = false; // can't hear players
		dudeAmbush = false; // must be triggered manually
		dudeGuard = false; // won't leave sector
		dudeFlag4 = false;

		target = 0;
		targetX = 0;
		targetY = 0;
		targetZ = 0;

		burnTime = 0;
		burnSource = 0;
		height = 0;

		stateTimer = 0;
		floorZ = 0;

		aiState = null;
	}

	public void copy(XSPRITE src) {
		reference = src.reference;
		state = src.state;
		busy = src.busy;
		txID = src.txID;
		rxID = src.rxID;

		command = src.command;
		triggerOn = src.triggerOn;
		triggerOff = src.triggerOff;
		busyTime = src.busyTime;
		waitTime = src.waitTime;
		restState = src.restState;

		Push = src.Push;
		Vector = src.Vector;
		Impact = src.Impact;
		Pickup = src.Pickup;
		Touch = src.Touch;
		Sight = src.Sight;
		Proximity = src.Proximity;
		DudeLockout = src.DudeLockout;
		triggerOnce = src.triggerOnce;

		l1 = src.l1;
		l2 = src.l2;
		l3 = src.l3;
		l4 = src.l4;
		l5 = src.l5;
		lSkill = src.lSkill;
		lS = src.lS;
		lB = src.lB;
		lC = src.lC;
		lT = src.lT;

		data1 = src.data1;
		data2 = src.data2;
		data3 = src.data3;
		data4 = src.data4;
		key = src.key;
		wave = src.wave;

		lockMsg = src.lockMsg;
		dropMsg = src.dropMsg;

		Decoupled = src.Decoupled;
		isTriggered = src.isTriggered;
		Interrutable = src.Interrutable;

		goalAng = src.goalAng;
		dodgeDir = src.dodgeDir;
		Locked = src.Locked;
		palette = src.palette;

		respawn = src.respawn;
		respawnPending = src.respawnPending;

		moveState = src.moveState;

		health = src.health;
		dudeDeaf = src.dudeDeaf;
		dudeAmbush = src.dudeAmbush;
		dudeGuard = src.dudeGuard;
		dudeFlag4 = src.dudeFlag4;

		target = src.target;
		targetX = src.targetX;
		targetY = src.targetY;
		targetZ = src.targetZ;

		burnTime = src.burnTime;
		burnSource = src.burnSource;
		height = src.height;

		stateTimer = src.stateTimer;
		floorZ = src.floorZ;

		aiState = src.aiState;
	}

	public byte[] getBytes() {
		Arrays.fill(xdata, (byte) 0);
		BitHandler.bput(xdata, 0, reference, 0, 13);
		BitHandler.bput(xdata, 1, state, 6, 6);
		BitHandler.bput(xdata, 1, busy, 7, 23);
		BitHandler.bput(xdata, 4, txID, 0, 9);
		BitHandler.bput(xdata, 5, rxID, 2, 11);
		BitHandler.bput(xdata, 6, command, 4, 11);
		BitHandler.bput(xdata, 7, triggerOn ? 1 : 0, 4, 4);
		BitHandler.bput(xdata, 7, triggerOff ? 1 : 0, 5, 5);
		BitHandler.bput(xdata, 7, wave, 6, 7);
		BitHandler.bput(xdata, 8, busyTime, 0, 11);
		BitHandler.bput(xdata, 9, waitTime, 4, 15);
		BitHandler.bput(xdata, 11, restState, 0, 0);
		BitHandler.bput(xdata, 11, Interrutable ? 1 : 0, 1, 1);
		BitHandler.bput(xdata, 11, respawnPending, 4, 6);
		BitHandler.bput(xdata, 11, lT ? 1 : 0, 7, 7);
		BitHandler.bput(xdata, 12, dropMsg, 0, 7);
		BitHandler.bput(xdata, 13, Decoupled ? 1 : 0, 0, 0);
		BitHandler.bput(xdata, 13, triggerOnce ? 1 : 0, 1, 1);
		BitHandler.bput(xdata, 13, isTriggered ? 1 : 0, 2, 2);
		BitHandler.bput(xdata, 13, key, 3, 5);
		BitHandler.bput(xdata, 13, Push ? 1 : 0, 6, 6);
		BitHandler.bput(xdata, 13, Vector ? 1 : 0, 7, 7);
		BitHandler.bput(xdata, 14, Impact ? 1 : 0, 0, 0);
		BitHandler.bput(xdata, 14, Pickup ? 1 : 0, 1, 1);
		BitHandler.bput(xdata, 14, Touch ? 1 : 0, 2, 2);
		BitHandler.bput(xdata, 14, Sight ? 1 : 0, 3, 3);
		BitHandler.bput(xdata, 14, Proximity ? 1 : 0, 4, 4);
		BitHandler.bput(xdata, 14, l1, 7, 7);
		BitHandler.bput(xdata, 15, l2, 0, 0);
		BitHandler.bput(xdata, 15, l3, 1, 1);
		BitHandler.bput(xdata, 15, l4, 2, 2);
		BitHandler.bput(xdata, 15, l5, 3, 3);
		BitHandler.bput(xdata, 15, lS ? 1 : 0, 4, 4);
		BitHandler.bput(xdata, 15, lB ? 1 : 0, 5, 5);
		BitHandler.bput(xdata, 15, lC ? 1 : 0, 6, 6);
		BitHandler.bput(xdata, 15, DudeLockout ? 1 : 0, 7, 7);
		BitHandler.bput(xdata, 16, data1, 0, 15);
		BitHandler.bput(xdata, 18, data2, 0, 15);
		BitHandler.bput(xdata, 20, data3, 0, 15);
		BitHandler.bput(xdata, 22, goalAng, 0, 10);
		BitHandler.bput(xdata, 23, dodgeDir, 3, 4);
		BitHandler.bput(xdata, 23, Locked, 5, 5);
		BitHandler.bput(xdata, 23, palette, 6, 7);
		BitHandler.bput(xdata, 24, respawn, 0, 1);
		BitHandler.bput(xdata, 24, data4, 2, 17);
		BitHandler.bput(xdata, 26, moveState, 2, 7);
		BitHandler.bput(xdata, 27, lockMsg, 0, 7);
		BitHandler.bput(xdata, 28, health, 0, 11);
		BitHandler.bput(xdata, 29, dudeDeaf ? 1 : 0, 4, 4);
		BitHandler.bput(xdata, 29, dudeAmbush ? 1 : 0, 5, 5);
		BitHandler.bput(xdata, 29, dudeGuard ? 1 : 0, 6, 6);
		BitHandler.bput(xdata, 29, dudeFlag4 ? 1 : 0, 7, 7);
		BitHandler.bput(xdata, 30, target, 0, 15);
		BitHandler.bput(xdata, 32, targetX, 0, 31);
		BitHandler.bput(xdata, 36, targetY, 0, 31);
		BitHandler.bput(xdata, 40, targetZ, 0, 31);
		BitHandler.bput(xdata, 44, burnTime, 0, 15);
		BitHandler.bput(xdata, 46, burnSource, 0, 15);
		BitHandler.bput(xdata, 48, height, 0, 15);
		BitHandler.bput(xdata, 50, stateTimer, 0, 15);
		if (aiState != null)
			BitHandler.bput(xdata, 52, aiState.hashCode(), 0, 31);
		else
			BitHandler.bput(xdata, 52, 0, 0, 31);

		return xdata;
	}
}
