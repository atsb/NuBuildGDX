package ru.m210projects.Wang.Type;

public class SHRAP {
	public State[] state;
	public short id, num, zlevel, min_jspeed, max_jspeed, min_vel, max_vel, ang_range;
	public boolean random_disperse;
	
	public SHRAP() {}

	public SHRAP(State[] state, int id, int num, int zlevel, int min_jspeed, int max_jspeed, int min_vel, int max_vel,
			boolean random_disperse, int ang_range) {

		this.state = state;
		this.id = (short) id;
		this.num = (short) num;
		this.zlevel = (short) zlevel;
		this.min_jspeed = (short) min_jspeed;
		this.max_jspeed = (short) max_jspeed;
		this.min_vel = (short) min_vel;
		this.max_vel = (short) max_vel;
		this.random_disperse = random_disperse;
		this.ang_range = (short) ang_range;
	}
	
	public void copy(SHRAP src)
	{
		this.state = src.state;

		this.id = src.id;
		this.num = src.num;
		this.zlevel = src.zlevel;
		this.min_jspeed = src.min_jspeed;
		this.max_jspeed = src.max_jspeed;
		this.min_vel = src.min_vel;
		this.max_vel = src.max_vel;
		this.random_disperse = src.random_disperse;
		this.ang_range = src.ang_range;
	}
}
