// This file is part of DukeGDX.
// Copyright (C) 2019  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// DukeGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// DukeGDX is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with DukeGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.Duke3D;

import static ru.m210projects.Duke3D.Main.*;
import static ru.m210projects.Duke3D.Globals.*;
import static ru.m210projects.Duke3D.Names.*;
import static ru.m210projects.Duke3D.Spawn.*;

import static ru.m210projects.Build.Engine.neartag;
import static ru.m210projects.Build.Pragmas.*;
import static ru.m210projects.Build.Strhandler.buildString;

import ru.m210projects.Build.Types.SPRITE;


public class Gameutils {

	public static int neartag(int xs, int ys, int zs, int sectnum, int ange, int neartagrange, int tagsearch)
	{
		int out = engine.neartag(xs,ys,zs, (short)sectnum, (short)ange,neartag,neartagrange,tagsearch);
    	neartagsprite = neartag.tagsprite;
    	neartagwall = neartag.tagwall;
    	neartagsector = neartag.tagsector;
    	neartaghitdist = neartag.taghitdist;
    	return out;
	}

	public static int FindDistance2D(int dx, int dy)
	{
		dx = klabs(dx);
		dy = klabs(dy);
		if (dx == 0) return(dy);
		if (dy == 0) return(dx);
		if (dy < dx) { int i = dx; dx = dy; dy = i; } //swap x, y
		dx += (dx>>1);
		return ((dx>>6)+(dx>>2)+dy-(dy>>5)-(dy>>7)); //handle 1 octant
		//return engine.ksqrt(dx*dx + dy*dy);
	}
	
	public static int FindDistance3D(int dx, int dy, int dz)
	{
		dx = klabs(dx);
		dy = klabs(dy);
		dz = klabs(dz);

		if (dx < dy) { int i = dx; dx = dy; dy = i; } //swap x, y
		if (dx < dz) { int i = dx; dx = dz; dz = i; } //swap x, z

		int t = dy + dz;

		return (dx - (dx>>4) + (t>>2) + (t>>3));
		//return engine.ksqrt(dx*dx + dy*dy + dz*dz);
	}
	
	public static boolean rnd(int X)
	{
		return (engine.krand()>>8)>=(255-(X));
	}
	
	public static void RANDOMSCRAP(SPRITE s, int i) {
		int vz = -512-(engine.krand()&2047);
		int ve = (engine.krand()&63)+64;
		int va = engine.krand()&2047;
		int pn = SCRAP6+(engine.krand()&15);
		int sz = s.z-(8<<8)-(engine.krand()&8191);
		int sy = s.y+(engine.krand()&255)-128;
		int sx = s.x+(engine.krand()&255)-128;
		EGS(s.sectnum,sx,sy,sz,pn,-8,48,48,va,ve,vz,i,(short)5);
	}
	
	public static boolean IFWITHIN(SPRITE s, int B, int E) {
		return (s.picnum)>=(B) && (s.picnum)<=(E);
	}
	
	public static boolean AFLAMABLE(int X) {
		return (X==BOX||X==TREE1||X==TREE2||X==TIRE||X==CONE);
	}
	
	public static int sgn(int val)
	{
		return ((val > 0)?1:0) - ((val < 0)?1:0);
	}
	
	public static int ClipRange(int value, int min, int max) {
		if(value < min)
			value = min;
		if(value > max)
			value = max;
		
		return value;
	}
	
	public static int ClipLow(int value, int min) {
		if(value < min)
			value = min;
		
		return value;
	}
	
	public static int ClipHigh(int value, int max) {
		if(value > max)
			value = max;
		
		return value;
	}

	
	public static float ClipLow(float value, int min) {
		if(value < min)
			value = min;
		
		return value;
	}
	
	public static float ClipHigh(float value, int max) {
		if(value > max)
			value = max;
		
		return value;
	}
	
	public static char[] toCharArray(String... text)
	{
		buildString(buf, 0, text);
		
		return buf;
	}
	
	public static char[] toCharArray(String text, int num)
	{
		buildString(buf, 0, text, num);
		
		return buf;
	}

}
