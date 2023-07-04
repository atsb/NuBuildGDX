/*
 * 3D models support for Polymost
 * by Jonathon Fowler
 * See the included license file "BUILDLIC.TXT" for license info.
 */

package ru.m210projects.Build.Render.Types;

public class Spriteext {

	public static final int SPREXT_NOTMD = 1;
	public static final int SPREXT_NOMDANIM = 2;
	public static final int SPREXT_AWAY1 = 4;
	public static final int SPREXT_AWAY2 = 8;

	public short angoff, pitch, roll;
	public int xoff, yoff, zoff;
	public short flags;
	public short xpanning, ypanning;
	public float alpha;

	public Spriteext() {
		clear();
	}

	public Spriteext(Spriteext src) {
		this.angoff = src.angoff;
		this.pitch = src.pitch;
		this.roll = src.roll;
		this.xoff = src.xoff;
		this.yoff = src.yoff;
		this.zoff = src.zoff;
		this.flags = src.flags;
		this.xpanning = src.xpanning;
		this.ypanning = src.ypanning;
		this.alpha = src.alpha;
	}

	public void clear() {
		angoff = 0;
		pitch = 0;
		roll = 0;
		xoff = 0;
		yoff = 0;
		zoff = 0;
		flags = 0;
		xpanning = 0;
		ypanning = 0;
		alpha = 0;
	}

	public boolean isNotModel() {
		return (this.flags & SPREXT_NOTMD) != 0;
	}

	public boolean isAnimationDisabled() {
		return (this.flags & SPREXT_NOMDANIM) != 0;
	}
}
