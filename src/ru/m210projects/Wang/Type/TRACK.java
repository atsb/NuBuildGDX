package ru.m210projects.Wang.Type;

public class TRACK {
	public TRACK_POINT[] TrackPoint;
	public int ttflags;
	public short flags;
	public short NumPoints;

	public void reset()
	{
		TrackPoint = null;
		ttflags = 0;
		flags = 0;
		NumPoints = 0;
	}

	public void copy(TRACK src) {
		for(int i = 0; src.TrackPoint != null && i < src.TrackPoint.length; i++)
		{
			if(TrackPoint == null)
				TrackPoint = new TRACK_POINT[src.TrackPoint.length];
			if(src.TrackPoint[i] != null) {
				if(TrackPoint[i] == null)
					TrackPoint[i] = new TRACK_POINT();
				TrackPoint[i].copy(src.TrackPoint[i]);
			}
		}
		this.ttflags = src.ttflags;
		this.flags = src.flags;
		this.NumPoints = src.NumPoints;
	}
}
