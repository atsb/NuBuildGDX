package ru.m210projects.Tekwar.Types;

import ru.m210projects.Build.Types.LittleEndian;

public class XTtrailertype {
	public static final int sizeof = 34;
	public int      numXTs;     //4       
	public int      start; 		//8
	public String mapname;      //[13] 21
	public String ID;           //[13] 34

	public void load(byte[] data)
	{
		numXTs = LittleEndian.getInt(data, 0);
    	start = LittleEndian.getInt(data, 4);
    	mapname = new String(data, 8, 13);
    	ID = new String(data, 21, 13);
	}
}
