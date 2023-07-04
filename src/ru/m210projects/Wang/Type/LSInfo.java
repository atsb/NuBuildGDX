package ru.m210projects.Wang.Type;

import ru.m210projects.Build.FileHandle.Resource;

public class LSInfo {
	public int skill;
	public int level;
	
	public String info;
	public String date;
	public String addonfile;
	
	public void read(Resource bb)
	{
		level = bb.readInt();
		skill = bb.readInt() + 1;
		update();
	}
	
	public void update()
	{
		info = "Level:" + level + " / Skill:" + skill;
	}
	
	public void clear()
	{
		skill = 0;
		level = 0;
		info = "Empty slot";
		date = null;
		addonfile = null;
	}
}
