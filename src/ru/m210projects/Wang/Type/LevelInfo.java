package ru.m210projects.Wang.Type;

public class LevelInfo {
	public String LevelName;
	public String SongName;
	public String Description;
	public String BestTime;
	public String ParTime;
	public int CDtrack = -1;

	public LevelInfo(String LevelName, String SongName, String Description, String BestTime, String ParTime) {
		this.LevelName = LevelName;
		this.SongName = SongName;
		this.Description = Description;
		this.BestTime = BestTime;
		this.ParTime = ParTime;
	}

	public void clear() {
		this.LevelName = null;
		this.SongName = null;
		this.Description = null;
		this.BestTime = null;
		this.ParTime = null;
	}
	
	public String toString()
	{
		String txt = "Description: " + Description + "\r\n";
		txt += "LevelName: " + LevelName + "\r\n";
		txt += "SongName: " + SongName + "\r\n";
		txt += "CDtrack: " + CDtrack + "\r\n";
		txt += "BestTime: " + BestTime + "\r\n";
		txt += "ParTime: " + ParTime + "\r\n";
		
		return txt;
	}
}
