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

//	public boolean expand(Vector3[] bounds) {
//		float minx = this.x1;
//		float maxx = this.x2;
//		float miny = this.y1;
//		float maxy = this.y2;
//
//		if (bounds[0].x < minx)
//			minx = bounds[0].x;
//		if (bounds[1].x > maxx)
//			maxx = bounds[1].x;
//		if (bounds[0].y < miny)
//			miny = bounds[0].y;
//		if (bounds[1].y > maxy)
//			maxy = bounds[1].y;
//
//		if (minx < x1 || maxx > x2 || miny < y1 || maxy > y2) {
//			this.x1 = minx;
//			this.x2 = maxx;
//			this.y1 = miny;
//			this.y2 = maxy;
//
//			return true;
//		}
//
//		return false;
//	}

//	public VisibleSector setBounds(Vector3[] bounds) {
//		this.x1 = bounds[0].x;
//		this.y1 = bounds[0].y;
//		this.x2 = bounds[1].x;
//		this.y2 = bounds[1].y;
//
//		return this;
//	}
}
