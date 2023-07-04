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

public class XWALL {
	public static int sizeof = 24;

	public int reference;
	public short state;
	public int busy;

	// trigger data
	public int data;
	public short txID;
	public short rxID;
	public int command;
	public boolean triggerOn;
	public boolean triggerOff;
	public short busyTime; // time to reach next state
	public short waitTime; // delay before callback
	public int restState; // state to return to on callback

	// panning data
	public boolean panAlways;
	public byte panXVel; // signed
	public byte panYVel; // signed
	public int key;

	// Trigger On
	public boolean triggerPush;
	public boolean triggerVector;
	public boolean triggerReserved;
	public boolean dudeLockout;

	public int xpanFrac;
	public int ypanFrac;

	// Trigger Flags
	public boolean decoupled;
	public boolean triggerOnce;
	public int locked;
	public boolean interruptable;
	public boolean isTriggered;

	public XWALL() {
	}

	public void init(byte[] data) {
//    	reference = BitHandler.bsread(data, 0, 0, 13);
		state = (short) BitHandler.buread(data, 1, 6, 6);
		busy = BitHandler.buread(data, 1, 7, 23);
		this.data = BitHandler.buread(data, 4, 0, 15);
		txID = (short) BitHandler.buread(data, 6, 0, 9);
		rxID = (short) BitHandler.buread(data, 8, 0, 9);
		command = BitHandler.buread(data, 9, 2, 9);
		triggerOn = BitHandler.buread(data, 10, 2, 2) == 1;
		triggerOff = BitHandler.buread(data, 10, 3, 3) == 1;
		busyTime = (short) BitHandler.buread(data, 10, 4, 15);
		waitTime = (short) BitHandler.buread(data, 12, 0, 11);
		restState = BitHandler.buread(data, 13, 4, 4);
		interruptable = BitHandler.buread(data, 13, 5, 5) == 1;
		panAlways = BitHandler.buread(data, 13, 6, 6) == 1;
		panXVel = (byte) BitHandler.bsread(data, 13, 7, 14);
		panYVel = (byte) BitHandler.bsread(data, 14, 7, 14);
		decoupled = BitHandler.buread(data, 15, 7, 7) == 1;
		triggerOnce = BitHandler.buread(data, 16, 0, 0) == 1;
		isTriggered = BitHandler.buread(data, 16, 1, 1) == 1;
		key = BitHandler.buread(data, 16, 2, 4);
		triggerPush = BitHandler.buread(data, 16, 5, 5) == 1;
		triggerVector = BitHandler.buread(data, 16, 6, 6) == 1;
		triggerReserved = BitHandler.buread(data, 16, 7, 7) == 1;
		xpanFrac = BitHandler.buread(data, 17, 0, 7);
		ypanFrac = BitHandler.buread(data, 18, 0, 7);
		locked = BitHandler.buread(data, 19, 2, 2);
		dudeLockout = BitHandler.buread(data, 19, 3, 3) == 1;
	}

	public String toString() {
		String out = "reference " + reference + " \r\n";
		out += "state " + state + " \r\n";
		out += "busy " + busy + " \r\n";
		out += "data " + data + " \r\n";
		out += "txID " + txID + " \r\n";
		out += "rxID " + rxID + " \r\n";
		out += "command " + command + " \r\n";
		out += "triggerOn " + triggerOn + " \r\n";
		out += "triggerOff " + triggerOff + " \r\n";
		out += "busyTime " + busyTime + " \r\n";
		out += "waitTime " + waitTime + " \r\n";
		out += "restState " + restState + " \r\n";
		out += "Interrutable " + interruptable + " \r\n";
		out += "panAlways " + panAlways + " \r\n";
		out += "panXVel " + panXVel + " \r\n";
		out += "panYVel " + panYVel + " \r\n";
		out += "decoupled " + decoupled + " \r\n";
		out += "triggerOnce " + triggerOnce + " \r\n";
		out += "isTriggered " + isTriggered + " \r\n";
		out += "key " + key + " \r\n";
		out += "triggerPush " + triggerPush + " \r\n";
		out += "triggerVector " + triggerVector + " \r\n";
		out += "triggerReserved " + triggerReserved + " \r\n";
		out += "xpanFrac " + xpanFrac + " \r\n";
		out += "ypanFrac " + ypanFrac + " \r\n";
		out += "locked " + locked + " \r\n";
		out += "dudeLockout " + dudeLockout + " \r\n";
		return out;
	}

	public void free() {
		reference = 0;
		state = 0;
		busy = 0;
		data = 0;
		txID = 0;
		rxID = 0;
		command = 0;
		triggerOn = false;
		triggerOff = false;
		busyTime = 0;
		waitTime = 0;
		restState = 0;
		interruptable = false;
		panAlways = false;
		panXVel = 0;
		panYVel = 0;
		decoupled = false;
		triggerOnce = false;
		isTriggered = false;
		key = 0;
		triggerPush = false;
		triggerVector = false;
		triggerReserved = false;
		xpanFrac = 0;
		ypanFrac = 0;
		locked = 0;
		dudeLockout = false;
	}

	public void copy(XWALL src) {
		reference = src.reference;
		state = src.state;
		busy = src.busy;
		data = src.data;
		txID = src.txID;
		rxID = src.rxID;
		command = src.command;
		triggerOn = src.triggerOn;
		triggerOff = src.triggerOff;
		busyTime = src.busyTime;
		waitTime = src.waitTime;
		restState = src.restState;
		interruptable = src.interruptable;
		panAlways = src.panAlways;
		panXVel = src.panXVel;
		panYVel = src.panYVel;
		decoupled = src.decoupled;
		triggerOnce = src.triggerOnce;
		isTriggered = src.isTriggered;
		key = src.key;
		triggerPush = src.triggerPush;
		triggerVector = src.triggerVector;
		triggerReserved = src.triggerReserved;
		xpanFrac = src.xpanFrac;
		ypanFrac = src.ypanFrac;
		locked = src.locked;
		dudeLockout = src.dudeLockout;
	}
}
