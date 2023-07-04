package ru.m210projects.Wang.Type;

public class Vector3i {
	
	public int x, y, z;
	
	public Vector3i()
	{
		this(0, 0, 0);
	}
	
	public Vector3i(int x, int y, int z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Vector3i set(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
		
		return this;
	}
}
