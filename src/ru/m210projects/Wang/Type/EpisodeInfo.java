package ru.m210projects.Wang.Type;

import static ru.m210projects.Wang.Gameutils.MAX_LEVELS;

public class EpisodeInfo {

	public String Title;
	public String Description;
	public int nMaps;
	public LevelInfo[] gMapInfo;

	public EpisodeInfo() {
		gMapInfo = new LevelInfo[MAX_LEVELS + 1];
	}
	
	public EpisodeInfo(String title, String Description, LevelInfo[] maps) {
		this.Title = title;
		this.Description = Description;
		this.gMapInfo = new LevelInfo[MAX_LEVELS + 1];
		for(int i = 0; i < maps.length; i++)
			gMapInfo[i] = maps[i];
		nMaps = maps.length;
	}

	public void clear()
	{
		Title = null;
		Description = null;
		nMaps = 0;

		for(int i = 0; i <= MAX_LEVELS; i++)
			if(gMapInfo[i] != null) 
				gMapInfo[i].clear();
	}
	
	public String toString()
	{
		String txt = "\r\nTitle: " + Title + "\r\n";
		txt += "Description: " + Description + "\r\n";
		txt += "nMaps: " + nMaps + "\r\n";
		txt += "[ \r\n";
		for(int i = 0; i <= nMaps; i++) {
			txt += "Map " + i + ":\r\n";
			if(gMapInfo[i] != null)
				txt += gMapInfo[i].toString();
			txt += "\r\n";
		}
		txt += "] \r\n";
		return txt;
	}
}
