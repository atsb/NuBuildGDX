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

public class ITEMDATA {
	public int nInventory;
	public int picnum;   
	public int shade;           
	public int pal;       
	public int xrepeat;        
	public int yrepeat;  
	
	public ITEMDATA(int nInventory, int picnum, int shade, int pal, int xrepeat, int yrepeat) {
		this.nInventory = nInventory;
		this.picnum = picnum;
		this.shade = shade;
		this.pal = pal;
		this.xrepeat = xrepeat; 
		this.yrepeat = yrepeat;
	}
}
