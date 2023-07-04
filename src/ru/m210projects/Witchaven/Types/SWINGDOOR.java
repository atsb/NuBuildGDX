package ru.m210projects.Witchaven.Types;

import ru.m210projects.Build.FileHandle.Resource;

public class SWINGDOOR {
	public int[] wall = new int[8];
	public int  sector;
	public int  angopen;
	public int  angclosed;
	public int  angopendir;
	public int  ang;
	public int  anginc;
	public int[] x = new int[8];
	public int[] y = new int[8];
	
	public void copy(SWINGDOOR src)
	{
		System.arraycopy(src.wall, 0, wall, 0, 8);

		sector = src.sector;
		angopen = src.angopen;
		angclosed = src.angclosed;
		angopendir = src.angopendir;
		ang = src.ang;
		anginc = src.anginc;
		
		System.arraycopy(src.x, 0, x, 0, 8);
		System.arraycopy(src.y, 0, y, 0, 8);
	}
	
	public void set(Resource bb) {
		for(int j = 0; j < 8; j++)
			wall[j] = bb.readShort();
		sector = bb.readShort();
		angopen = bb.readShort();
		angclosed = bb.readShort();
		angopendir = bb.readShort();
		ang = bb.readShort();
		anginc = bb.readShort();
		for(int j = 0; j < 8; j++)
			x[j] = bb.readInt();
		for(int j = 0; j < 8; j++)
			y[j] = bb.readInt();
	}
}
