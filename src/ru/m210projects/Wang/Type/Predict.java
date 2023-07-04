package ru.m210projects.Wang.Type;

public class Predict {

	public int x, y, z;
	public float horiz, ang;
	
	public void copy(Predict src)
	{
		this.x = src.x;
		this.y = src.y;
		this.z = src.z;
		this.horiz = src.horiz;
		this.ang = src.ang;
	}
	
	public void set(PlayerStr p) {
		x = p.posx;
		y = p.posy;
		z = p.posz;
		ang = p.pang;
		horiz = p.horiz;
	}

}
