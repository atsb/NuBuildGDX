package ru.m210projects.Witchaven.Types;

import ru.m210projects.Build.FileHandle.Resource;

public class LSInfo {
	public int level;
	public int skill;
	public String info;
	public String date;
	public String addonfile;

	public void read(Resource bb)
	{
		level = bb.readInt();
		skill = bb.readInt();
		update();
	}
	
	public void update()
	{
		info = "Map:" + level + " / Skill:" + skill;
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
