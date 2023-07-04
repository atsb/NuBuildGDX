package ru.m210projects.Witchaven.Types;

import java.util.ArrayList;
import java.util.List;

public class EpisodeInfo {

	public String Title;
	public String path;
	public boolean packed;
	public List<MapInfo> gMapInfo;

	public EpisodeInfo() {
		gMapInfo = new ArrayList<MapInfo>();
	}
	
	public EpisodeInfo(String title) {
		this();
		this.Title = title;
	}

	public void clear()
	{
		Title = null;
		path = null;
		packed = false;
		gMapInfo.clear();
	}
	
	public int maps() {
		return gMapInfo.size();
	}
	
	public String getMapName(int num)
	{
		MapInfo map = getMap(num);
		if(map != null)
			return map.title;
		
		return null;
	}
	
	public MapInfo getMap(int num)
	{
		return (num - 1) < gMapInfo.size() ? gMapInfo.get(num - 1) : null;
	}
}
