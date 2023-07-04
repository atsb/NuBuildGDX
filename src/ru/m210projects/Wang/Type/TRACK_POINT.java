package ru.m210projects.Wang.Type;

public class TRACK_POINT {
	public int x, y, z;
	public short ang, tag_low, tag_high;
	
	public void copy(TRACK_POINT src) {
		this.x = src.x;
		this.y = src.y;
		this.z = src.z;
		this.ang = src.ang;
		this.tag_low = src.tag_low;
		this.tag_high = src.tag_high;
	}
}
