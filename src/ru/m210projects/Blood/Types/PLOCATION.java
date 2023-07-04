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

public class PLOCATION {
	public int bobAmp;
	public int bobPhase;
	public int bobHeight;
	public int bobWidth;
	public int swayAmp;
	public int swayPhase;
	public int swayHeight;
	public int swayWidth;
	public float look;  
	public float horiz; 
	public int slope; 
	public float horizOff;
	public float ang;   
	public int weapOffZ;
	public int viewOffZ;
	public int viewOffdZ;
	public int weaponAboveZ; 
	public int weapOffdZ;
	public int moveState;
	public int Turn_Around;
	public int x;     
	public int y;     
	public int z;     
	public long xvel;  
	public long yvel;  
	public long zvel;  
	public short sectnum; 
	public int height;
	public boolean center;
	public int fNoJump; 
	public boolean Run;   
	public boolean jump;  
	public boolean underwater;
	public short flags; 
	public int moveHit; 
	public int ceilHit; 
	public int floorHit;
	
	public void copy(PLOCATION src)
	{
		this.bobAmp = src.bobAmp;
		this.bobPhase = src.bobPhase;
		this.bobHeight = src.bobHeight;
		this.bobWidth = src.bobWidth;
		this.swayAmp = src.swayAmp;
		this.swayPhase = src.swayPhase;
		this.swayHeight = src.swayHeight;
		this.swayWidth = src.swayWidth;
		this.look = src.look;
		this.horiz = src.horiz;
		this.slope = src.slope;
		this.horizOff = src.horizOff;
		this.ang = src.ang;
		this.weapOffZ = src.weapOffZ;
		this.viewOffZ = src.viewOffZ;
		this.viewOffdZ = src.viewOffdZ;
		this.weaponAboveZ = src.weaponAboveZ;
		this.weapOffdZ = src.weapOffdZ;
		this.moveState = src.moveState;
		this.Turn_Around = src.Turn_Around;
		this.x = src.x;
		this.y = src.y;
		this.z = src.z;
		this.xvel = src.xvel;
		this.yvel = src.yvel;
		this.zvel = src.zvel;
		this.sectnum = src.sectnum;
		this.height = src.height;
		this.center = src.center;
		this.fNoJump = src.fNoJump;
		this.Run = src.Run;
		this.jump = src.jump;
		this.underwater = src.underwater;
		this.flags = src.flags;
		this.moveHit = src.moveHit;
		this.ceilHit = src.ceilHit;
		this.floorHit = src.floorHit;
	}
}
