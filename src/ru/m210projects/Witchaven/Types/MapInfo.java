package ru.m210projects.Witchaven.Types;

public class MapInfo {
	
	public String path;
	public String title;

	public MapInfo(String path, String title) {
		this.path = path;
		this.title = title;
	}
	
	public void clear()
	{
		path = null;
		title = null;
	}
	
	public String toString()
	{
		String text = "path: " + path + ", ";
		text += "title: " + title;
		
		return text;
	}
}
