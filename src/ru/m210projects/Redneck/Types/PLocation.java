// This file is part of RedneckGDX.
// Copyright (C) 2017-2019  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// RedneckGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// RedneckGDX is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with RedneckGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.Redneck.Types;

import static ru.m210projects.Build.Net.Mmulti.myconnectindex;
import static ru.m210projects.Redneck.Globals.ps;
import static ru.m210projects.Redneck.Main.*;

public class PLocation {
	
	public int x, y, z;
	public int xvel, yvel, zvel;
	public float horiz, ang;
	public short sectnum, horizoff; 
	public short lookang;
	public short rotscrnang;
	
	public short jumpingcounter;
	public byte jumpingtoggle, hardlanding, returntocenter;
	public boolean onground;
	
	public void copy(PLocation src)
	{
		this.x = src.x;
		this.y = src.y;
		this.z = src.z;
		this.xvel = src.xvel;
		this.yvel = src.yvel;
		this.zvel = src.zvel;
		this.horiz = src.horiz;
		this.horizoff = src.horizoff;
		this.ang = src.ang;
		this.sectnum = src.sectnum;
		this.lookang = src.lookang;
		this.rotscrnang = src.rotscrnang;
		this.jumpingcounter = src.jumpingcounter;
		this.jumpingtoggle = src.jumpingtoggle;
		this.hardlanding = src.hardlanding;
		this.returntocenter = src.returntocenter;
		this.onground = src.onground;
	}
	
	public void reset() {
		PlayerStruct p = ps[myconnectindex];
		
		x = p.posx;
		xvel = p.posxv;
		y = p.posy;
		yvel = p.posyv;
		z = p.posz;
		zvel = p.poszv;
		ang = p.ang;
		sectnum = p.cursectnum;
		lookang = p.look_ang;
		rotscrnang = p.rotscrnang;
		horiz = p.horiz;
		horizoff = p.horizoff;
		jumpingcounter = p.jumping_counter;
		jumpingtoggle = (byte) p.jumping_toggle;
		onground = p.on_ground;
		hardlanding = (byte) p.hard_landing;
		returntocenter = (byte) p.return_to_center;
		
		game.net.predictOld.copy(this);
	}

}
