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

public class XSECTOR {
	public static int sizeof = 60;

	public int reference;
	public short state;
	public int busy;
	
	public short[]   busyTime = new short[2];
	public boolean[] waitFlag = new boolean[2];
	public short[]   waitTime = new short[2];
	public short[]   waveTime = new short[2];
	public boolean[] stopFlag = new boolean[2];
	
	public short txID;
	public short rxID;
	public int data;
	public short command;
	public boolean triggerOn;
	public boolean triggerOff;

	public boolean interruptable;
	public boolean decoupled;
	public boolean triggerOnce;
	public boolean isTriggered;
	public int 	locked;
	public boolean dudelockout;
	
	public boolean Push;
	public boolean Vector;
	public boolean Reserved;
	public boolean Enter;
	public boolean Exit;
	public boolean Wallpush;
	
	public boolean panAlways;
	public boolean panFloor;
	public boolean panCeiling;
	
	public boolean Drag;
	public boolean Underwater;
	public boolean Crush;
	
	public short Key;
	public short Depth;
	
	// lighting data
	
	public short amplitude;
	public short freq;
	public short phase;
	public short wave;
	public boolean shadeAlways;
	public boolean shadeFloor;
	public boolean shadeCeiling;
	public boolean shadeWalls;
	public int shade;
	
	public short panVel;
	public short panAngle;
	public boolean wind;
	public boolean color;
	public short floorpal;
	public short ceilpal;
	
	public int marker0, marker1;
	
	public int offCeilZ;
	public int onCeilZ;
	public int offFloorZ;
	public int onFloorZ;
	
	public int floorxpanFrac;
	public int floorypanFrac;
	public int ceilxpanFrac;
	public int ceilypanFrac;
	public int damageType;
	
	public int windVel;
	public int windAng;
	public boolean windAlways;
	
	public int bobTheta;
	public int bobZRange;
	public int bobSpeed;

	public boolean bobAlways;
	public boolean bobFloor;
	public boolean bobCeiling;
	public boolean bobRotate;
	
	public XSECTOR() { }
	
	public void init(byte[] data) {
//		reference = BitHandler.buread(data, 0, 0, 13);
		state = (short) BitHandler.buread(data, 1, 6, 6);
		busy = BitHandler.buread(data, 1, 7, 23);
		this.data = BitHandler.buread(data, 4, 0, 15);
		txID = (short) BitHandler.buread(data, 6, 0, 9);
		waveTime[1] = (short) BitHandler.buread(data, 7, 2, 3);
		waveTime[0] = (short) BitHandler.buread(data, 7, 5, 6);
		rxID = (short) BitHandler.buread(data, 8, 0, 9);
		command = (short) BitHandler.buread(data, 9, 2, 9);
		triggerOn = BitHandler.buread(data, 10, 2, 2) == 1;
		triggerOff = BitHandler.buread(data, 10, 3, 3) == 1;
		busyTime[1] = (short) BitHandler.buread(data, 10, 4, 15);
		waitTime[1] = (short) BitHandler.buread(data, 12, 0, 11);
		//BitHandler.buread(data, 13, restState, 4, 4);
		interruptable = BitHandler.buread(data, 13, 5, 5) == 1;
		amplitude = (short) BitHandler.bsread(data, 13, 6, 13);
		freq = (short) BitHandler.buread(data, 14, 6, 13);
		waitFlag[1] = BitHandler.buread(data, 15, 6, 6) == 1;
		waitFlag[0] = BitHandler.buread(data, 15, 7, 7) == 1;
		phase = (short) BitHandler.buread(data, 16, 0, 7);
		wave = (short) BitHandler.buread(data, 17, 0, 3);
		shadeAlways = BitHandler.buread(data, 17, 4, 4) == 1;
		shadeFloor = BitHandler.buread(data, 17, 5, 5) == 1;
		shadeCeiling = BitHandler.buread(data, 17, 6, 6) == 1;
		shadeWalls = BitHandler.buread(data, 17, 7, 7) == 1;
		shade = BitHandler.bsread(data, 18, 0, 7);
		panAlways = BitHandler.buread(data, 19, 0, 0) == 1;
		panFloor = BitHandler.buread(data, 19, 1, 1) == 1;
		panCeiling = BitHandler.buread(data, 19, 2, 2) == 1;
		Drag = BitHandler.buread(data, 19, 3, 3) == 1;
		Underwater = BitHandler.buread(data, 19, 4, 4) == 1;
		Depth = (short) BitHandler.buread(data, 19, 5, 7);
		panVel = (short) BitHandler.buread(data, 20, 0, 7);
		panAngle = (short) BitHandler.buread(data, 21, 0, 10);
		wind = BitHandler.buread(data, 22, 3, 3) == 1;
		decoupled = BitHandler.buread(data, 22, 4, 4) == 1;
		triggerOnce = BitHandler.buread(data, 22, 5, 5) == 1;
		isTriggered = BitHandler.buread(data, 22, 6, 6) == 1;
		Key = (short) BitHandler.buread(data, 22, 7, 9);
		Push = BitHandler.buread(data, 23, 2, 2) == 1;
		Vector = BitHandler.buread(data, 23, 3, 3) == 1;
		Reserved = BitHandler.buread(data, 23, 4, 4) == 1;
		Enter = BitHandler.buread(data, 23, 5, 5) == 1;
		Exit = BitHandler.buread(data, 23, 6, 6) == 1;
		Wallpush = BitHandler.buread(data, 23, 7, 7) == 1;
		color = BitHandler.buread(data, 24, 0, 0) == 1;
		busyTime[0] = (short) BitHandler.buread(data, 24, 2, 9);
		waitTime[0] = (short) BitHandler.buread(data, 25, 6, 13);
		ceilpal = (short) BitHandler.buread(data, 27, 4, 7);
		offCeilZ = BitHandler.bsread(data, 28, 0, 31);
		onCeilZ = BitHandler.bsread(data, 32, 0, 31);
		offFloorZ = BitHandler.bsread(data, 36, 0, 31);
		onFloorZ = BitHandler.bsread(data, 40, 0, 31);
		marker0 = BitHandler.bsread(data, 44, 0, 15);
		marker1 = BitHandler.bsread(data, 46, 0, 15);
		Crush = BitHandler.buread(data, 48, 0, 0) == 1;
		ceilxpanFrac = BitHandler.buread(data, 48, 1, 8);
		ceilypanFrac = BitHandler.buread(data, 49, 1, 8);
		floorxpanFrac = BitHandler.buread(data, 50, 1, 8);
		damageType = BitHandler.buread(data, 51, 1, 3);
		floorpal = (short) BitHandler.buread(data, 51, 4, 7);
		floorypanFrac = BitHandler.buread(data, 52, 0, 7);
		locked = BitHandler.buread(data, 53, 0, 0);
		windVel = BitHandler.buread(data, 53, 1, 10);
		windAng = BitHandler.buread(data, 54, 3, 13);
		windAlways = BitHandler.buread(data, 55, 6, 6) == 1;
		dudelockout = BitHandler.buread(data, 55, 7, 7) == 1;
		bobTheta = BitHandler.buread(data, 56, 0, 10);
		bobZRange = BitHandler.buread(data, 57, 3, 7);
		bobSpeed = BitHandler.buread(data, 58, 0, 11);
		bobAlways = BitHandler.buread(data, 59, 4, 4) == 1;
		bobFloor = BitHandler.buread(data, 59, 5, 5) == 1;
		bobCeiling = BitHandler.buread(data, 59, 6, 6) == 1;
		bobRotate = BitHandler.buread(data, 59, 7, 7) == 1;
	}
	
	public void copy(XSECTOR src)
	{
		reference = src.reference;
		state = src.state;
		data = src.data;
		busy = src.busy;
		txID = src.txID;
		rxID = src.rxID;
		command = src.command;
		waveTime[0] = src.waveTime[0];
		waveTime[1] = src.waveTime[1];
		triggerOn = src.triggerOn;
		triggerOff = src.triggerOff;
		busyTime[1] = src.busyTime[1];
		waitTime[1] = src.waitTime[1];
		waitFlag[0] = src.waitFlag[0];
		waitFlag[1] = src.waitFlag[1];
		interruptable = src.interruptable;
		decoupled = src.decoupled;
		triggerOnce = src.triggerOnce;
		isTriggered = src.isTriggered;
		locked = src. locked;
		dudelockout = src.dudelockout;
		Drag = src.Drag;
		busyTime[0] = src.busyTime[0];
		waitTime[0] = src.waitTime[0];
		Push = src.Push;
		Vector = src.Vector;
		Reserved = src.Reserved;
		Enter = src.Enter;
		Exit = src.Exit;
		Wallpush = src.Wallpush;
		Crush = src.Crush;
		Underwater = src.Underwater;
		Key = src.Key;
		Depth = src.Depth;
		amplitude = src.amplitude;
		freq = src.freq;
		phase = src.phase;
		wave = src.wave;
		wind = src.wind;
		shadeFloor  = src.shadeFloor;
		shadeCeiling  = src.shadeCeiling;
		shadeWalls  = src.shadeWalls;
		shadeAlways = src.shadeAlways;
		shade = src.shade;
		panVel = src.panVel;
		panAngle = src.panAngle;
		panAlways = src.panAlways;
		panFloor = src.panFloor;
		panCeiling = src.panCeiling;
		color = src.color;
		ceilpal  = src.ceilpal;
		floorpal  = src.floorpal;
		offCeilZ = src.offCeilZ;
		onCeilZ = src.onCeilZ;
		offFloorZ = src.offFloorZ;
		onFloorZ = src.onFloorZ;
		marker0 = src.marker0;
		marker1 = src.marker1;
		floorxpanFrac = src.floorxpanFrac;
		floorypanFrac = src.floorypanFrac;
		ceilxpanFrac = src.ceilxpanFrac;
		ceilypanFrac = src.ceilypanFrac;
		damageType = src.damageType;
		windVel = src.windVel;
		windAng = src.windAng;
		windAlways = src.windAlways;
		bobTheta = src.bobTheta;
		bobZRange = src.bobZRange;
		bobSpeed = src.bobSpeed;
		bobAlways = src.bobAlways;
		bobFloor = src.bobFloor;
		bobCeiling = src.bobCeiling;
		bobRotate = src.bobRotate;
	}
	
	public void free() {
		reference = 0;
		state = 0;
		data = 0;
		busy = 0;
		txID = 0;
		rxID = 0;
		command = 0;
		waveTime[0] = 0;
		waveTime[1] = 0;
		triggerOn = false;
		triggerOff = false;
		busyTime[1] = 0;
		waitTime[1] = 0;
		waitFlag[0] = false;
		waitFlag[1] = false;
		interruptable = false;
		decoupled = false;
		triggerOnce = false;
		isTriggered = false;
		locked =  0;
		dudelockout = false;
		Drag = false;
		busyTime[0] = 0;
		waitTime[0] = 0;
		Push = false;
		Vector = false;
		Reserved = false;
		Enter = false;
		Exit = false;
		Wallpush = false;
		Crush = false;
		Underwater = false;
		Key = 0;
		Depth = 0;
		amplitude = 0;
		freq = 0;
		phase = 0;
		wave = 0;
		wind = false;
		shadeFloor  = false;
		shadeCeiling  = false;
		shadeWalls  = false;
		shadeAlways = false;
		shade = 0;
		panVel = 0;
		panAngle = 0;
		panAlways = false;
		panFloor = false;
		panCeiling = false;
		color = false;
		ceilpal  = 0;
		floorpal  = 0;
		offCeilZ = 0;
		onCeilZ = 0;
		offFloorZ = 0;
		onFloorZ = 0;
		marker0 = 0; 
		marker1 = 0;
		floorxpanFrac = 0;
		floorypanFrac = 0;
		ceilxpanFrac = 0;
		ceilypanFrac = 0;
		damageType = 0;
		windVel = 0;
		windAng = 0;
		windAlways = false;
		bobTheta = 0;
		bobZRange = 0;
		bobSpeed = 0;
		bobAlways = false;
		bobFloor = false;
		bobCeiling = false;
		bobRotate = false;
	}
	
	
}
