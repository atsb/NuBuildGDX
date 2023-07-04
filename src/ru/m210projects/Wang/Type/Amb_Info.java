package ru.m210projects.Wang.Type;

public class Amb_Info {
	public int name;
	public int diginame;
	public int ambient_flags;
	public int maxtics; // When tics reaches this number next
	
	public Amb_Info(int name, int diginame, int ambient_flags, int maxtics)
	{
		this.name = name;
		this.diginame = diginame;
		this.ambient_flags = ambient_flags;
		this.maxtics = maxtics;
	}
}
