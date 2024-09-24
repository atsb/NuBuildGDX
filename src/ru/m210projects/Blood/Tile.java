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

import static ru.m210projects.Blood.Globals.kMaxTiles;
import static ru.m210projects.Blood.Main.engine;
import static ru.m210projects.Blood.Main.gPrecacheScreen;
import static ru.m210projects.Blood.Main.game;

import java.util.Arrays;

import ru.m210projects.Blood.Types.BloodTile;
import ru.m210projects.Blood.Types.BloodTile.ViewType;
import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.FileHandle.Resource;


import ru.m210projects.Build.Types.Tile.AnimType;

public class Tile {

	public static int kSurfNone = 0;
	public static int kSurfStone = 1;
	public static int kSurfMetal = 2;
	public static int kSurfWood = 3;
	public static int kSurfFlesh = 4;
	public static int kSurfWater = 5;
	public static int kSurfDirt = 6;
	public static int kSurfClay = 7;
	public static int kSurfSnow = 8;
	public static int kSurfIce = 9;
	public static int kSurfLeaves = 10;
	public static int kSurfCloth = 11;
	public static int kSurfPlant = 12;
	public static int kSurfGoo = 13;
	public static int kSurfLava = 14;
	public static int kSurfMax = 15;

	public static int[] surfSfxLand = {
		-1,
		600,
		601,
		602,
		603,
		604,
		605,
		605,
		605,
		600,
		605,
		605,
		605,
		604,
		603,
	};

	public static int[][] surfSfxMove = {
		new int[] {-1,-1},
		new int[] {802,803},
		new int[] {804,805},
		new int[] {806,807},
		new int[] {808,809},
		new int[] {810,811},
		new int[] {812,813},
		new int[] {814,815},
		new int[] {816,817},
		new int[] {818,819},
		new int[] {820,821},
		new int[] {822,823},
		new int[] {824,825},
		new int[] {826,827},
		new int[] {828,829},
	};

	public static short[] gVoxelData = new short[kMaxTiles];
	public static byte[] shadeTable = new byte[kMaxTiles];

	public static void tileInit()
	{
		voxelsInit("VOXEL.DAT");
		shadeInit("SHADE.DAT");
	}

	public static void voxelsInit(String name) {
		Arrays.fill(gVoxelData, (byte) -1);

		Resource bb;
		if ((bb = BuildGdx.cache.open(name, 0)) != null)
		{
	    	for(int i = 0; i < bb.size() / 2; i++)
	    		gVoxelData[i] = bb.readShort();
	    	bb.close();
		}
	}

	public static void tileLoadVoxel(int nTile)
	{
		BloodTile pic;
		if(nTile < 0 || ( (pic = engine.getTile(nTile)).getView() != ViewType.kSpriteViewVoxel && pic.getView() != ViewType.kSpriteViewSpinVoxel)) return;
		int nVoxel = gVoxelData[nTile];
	}

	public static void shadeInit(String name)
	{
		Resource data = BuildGdx.cache.open(name, 0);
		if(data == null) return;

		int pos = 0;
		while(data.hasRemaining())
		{
			data.read(shadeTable, pos, Math.min(shadeTable.length, data.remaining()));
			pos += shadeTable.length;
		}
		data.close();
	}

	public static void tilePreloadTile( int nTile ) {
		if(nTile < 0 || nTile >= kMaxTiles)
			return;

		int view = 0;
		switch( engine.getTile(nTile).getView() ) {
		case kSpriteViewSingle: view = 1; break;
		case kSpriteView5Full: view = 5; break;
		case kSpriteView8Full: view = 8; break;
		case kSpriteView5Half: view = 2; break;
		case kSpriteViewVoxel:
		case kSpriteViewSpinVoxel:
			tileLoadVoxel(nTile);
			view = 1;
		    break;
		default:
		    break;
		}

		while ( view > 0 )
		{
			BloodTile pic = engine.getTile(nTile);
			if (pic.getType() != AnimType.None) {
		    	for ( int i = pic.getFrames(); i >= 0; i-- )
		    	{
		    		if (pic.getType() == AnimType.Backward)
			        	gPrecacheScreen.addTile(nTile - i);
			        else gPrecacheScreen.addTile(nTile + i);
		    	}
		    } else gPrecacheScreen.addTile(nTile);

			nTile += pic.getFrames() + 1;
		    view--;
		}
	}
}
