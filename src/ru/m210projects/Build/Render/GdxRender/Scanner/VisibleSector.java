package ru.m210projects.Build.Render.GdxRender.Scanner;

import com.badlogic.gdx.math.Plane;
import com.badlogic.gdx.utils.IntArray;

import ru.m210projects.Build.Render.GdxRender.Pool.Poolable;

public class VisibleSector implements Poolable {

	public IntArray walls = new IntArray();
	public IntArray skywalls = new IntArray();
	public int index;
	public IntArray wallflags = new IntArray();
	public byte secflags = 0;

	// bounds
//	public float x1, y1, x2, y2;
	public Plane[] clipPlane = { new Plane(), new Plane(), new Plane(), new Plane() };

	public VisibleSector set(int index) {
		this.index = index;
		return this;
	}

	public void setFrustum(Plane[] plane) {
		for (int i = 0; i < plane.length; i++) {
			clipPlane[i].set(plane[i]);
		}
	}

	@Override
	public String toString() {
		return "index: " + index + " walls: " + walls.size;
	}

	@Override
	public void reset() {
		walls.clear();
		wallflags.clear();
		skywalls.clear();
//		x1 = x2 = y1 = y2 = 0;
	}

}
