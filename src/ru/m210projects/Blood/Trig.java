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

package ru.m210projects.Blood;

import static ru.m210projects.Blood.Globals.kAngleMask;
import static ru.m210projects.Build.Pragmas.dmulscaler;
import static ru.m210projects.Build.Pragmas.klabs;

import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.FileHandle.Resource;

import com.badlogic.gdx.math.Vector2;

public class Trig {
	public static int[] costable = new int[2048];
	
	public static void trigInit() {
		
		Resource buf = BuildGdx.cache.open("cosine.dat", 0);
		if (buf != null)
        {
			for(int i = 0; i < 512; i++) {
				costable[i] = buf.readInt();
			}
			for(int i = 513; i <= 1024; i++) costable[i] = -costable[1024 - i];
			for(int i = 1025; i < 2048; i++) costable[i] = costable[2048 - i]; 

			buf.close();
	    } else {
	    	System.err.println("cosine file not found");
	    }
	}
	
	public static int Cos(int angle) {
		return costable[angle&kAngleMask];
	}
	
	public static int Sin(int angle) {
		return costable[(angle-512)&kAngleMask];
	}
	
	
	static Vector2 rotated = new Vector2(0, 0);
	public static Vector2 RotateVector(long x, long y, int nAngle) {
	
		rotated.x = dmulscaler(Cos(nAngle), x, -Sin(nAngle), y, 30);
		rotated.y = dmulscaler(Sin(nAngle), x, Cos(nAngle), y, 30);

		return rotated;
	}
	
	static int[] octanTable = { 5, 6, 2, 1, 4, 7, 3, 0 };
	public static int GetOctant(int x, int y) {
		long dx = klabs(x) - klabs(y);
		return octanTable[7 - 2 * ((y < 0)?1:0) - ((x < 0)?1:0) - 4 * ((dx > 2 *dx)?1:0)];
	}
}