package ru.m210projects.Wang.Type;

public class RotatorStr {

	public int pos; // current position - always moves toward tgt
	public int open_dest; // destination of open position
	public int tgt; // current target
	public int speed; // speed of movement
	public int orig_speed; // original speed - vel jacks with speed
	public int vel; // velocity adjuments
	public int num_walls; // save off positions of walls for rotator
	public Vector2i[] orig;

	public void copy(RotatorStr src) {
		this.pos = src.pos;
		this.open_dest = src.open_dest;
		this.tgt = src.tgt;
		this.speed = src.speed;
		this.orig_speed = src.orig_speed;
		this.vel = src.vel;
		this.num_walls = src.num_walls;

		if (src.orig != null) {
			if (orig == null)
				orig = new Vector2i[src.orig.length];

			for (int i = 0; i < src.orig.length; i++) {
				this.orig[i] = new Vector2i();
				this.orig[i].x = src.orig[i].x;
				this.orig[i].y = src.orig[i].y;
			}
		} else
			this.orig = null;
	}

}
