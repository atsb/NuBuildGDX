package ru.m210projects.Build.Render.GdxRender.Scanner;

import static ru.m210projects.Build.Engine.*;

import java.util.ArrayList;

import com.badlogic.gdx.math.Plane;
import com.badlogic.gdx.math.Plane.PlaneSide;
import com.badlogic.gdx.math.Vector3;

import ru.m210projects.Build.Render.GdxRender.BuildCamera;
import ru.m210projects.Build.Render.GdxRender.Pool;
import ru.m210projects.Build.Render.GdxRender.Pool.Poolable;

public class WallFrustum3d implements Poolable {

	public final Plane[] planes = new Plane[4];
	public int sectnum;
	public WallFrustum3d next;
	public boolean handled = false;
	private boolean rebuildRequest = false;
	private BuildCamera cam;

	// Viewport

	public Vector3[] bounds = new Vector3[2]; // XXX private
	private static final Vector3[] tmpVec = { new Vector3(), new Vector3(), new Vector3(), new Vector3() };

	public WallFrustum3d() {
		for (int i = 0; i < planes.length; i++) {
			planes[i] = new Plane();
		}

		for (int i = 0; i < bounds.length; i++) {
			bounds[i] = new Vector3();
		}
	}

	public WallFrustum3d set(BuildCamera cam, int sectnum) {
		this.sectnum = sectnum;
		this.cam = cam;
		for (int i = 0, j = 2; i < 4; i++)
			planes[i].set(cam.frustum.planes[j++]);
		setBounds(0, 0, windowx2 + 1, windowy2 + 1);
		return this;
	}

	public void rebuild() {
		if (rebuildRequest) {
			buildFrustum();
			rebuildRequest = false;
		}
	}

	public boolean wallInFrustum(Vector3[] points, int len) {
		rebuild();

		Plane: for (int i = 0; i < planes.length; i++) {
			Plane plane = planes[i];
			for (int p = 0; p < len; p++)
				if (plane.testPoint(points[p]) != PlaneSide.Back)
					continue Plane;

			return false;
		}

		return true;
	}

	public boolean wallInFrustum(ArrayList<? extends Vector3> points) {
		if (points == null)
			return false;

		rebuild();
		final int len = points.size();
		Plane: for (int i = 0; i < planes.length; i++) {
			Plane plane = planes[i];
			for (int p = 0; p < len; p++)
				if (plane.testPoint(points.get(p)) != PlaneSide.Back)
					continue Plane;

			return false;
		}

		return true;
	}

	public WallFrustum3d clone(Pool<WallFrustum3d> pool) {
		WallFrustum3d frustum = pool.obtain();
		frustum.sectnum = sectnum;
		for (int i = 0; i < planes.length; i++)
			frustum.planes[i].set(planes[i]);
		for (int i = 0; i < bounds.length; i++)
			frustum.bounds[i].set(bounds[i]);
		frustum.rebuildRequest = rebuildRequest;
		frustum.cam = cam;
		return frustum;
	}

	public WallFrustum3d build(BuildCamera cam, Pool<WallFrustum3d> pool, ArrayList<? extends Vector3> coords,
			int sectnum) {
		if (coords == null)
			return null;

		if (!sector[sectnum].isParallaxCeiling() && !sector[sectnum].isParallaxFloor()) {
			if (!wallInFrustum(coords))
				return null;
		}

		WallFrustum3d frustum = pool.obtain();
		// Calc AABB of the new frustum
		for (int i = 0; i < coords.size(); i++)
			cam.project(coords.get(i));

		frustum.sectnum = sectnum;
		frustum.calcBounds(coords);
		if (!frustum.clipBounds(this))
			return null;

		frustum.rebuildRequest = true;
		frustum.cam = cam;
		return frustum;
	}

	private WallFrustum3d buildFrustum() {
		cam.unproject(tmpVec[0].set(bounds[0].x, bounds[0].y, 1));
		cam.unproject(tmpVec[1].set(bounds[0].x, bounds[1].y, 1));
		cam.unproject(tmpVec[2].set(bounds[1].x, bounds[1].y, 1));
		cam.unproject(tmpVec[3].set(bounds[1].x, bounds[0].y, 1));

		build(planes[0], tmpVec[1], tmpVec[0]); // left
		build(planes[1], tmpVec[2], tmpVec[1]);
		build(planes[2], tmpVec[3], tmpVec[2]); // right
		build(planes[3], tmpVec[0], tmpVec[3]);
		return this;
	}

	private void build(Plane p, Vector3 p1, Vector3 p2) {
		p.normal.set(globalposx, globalposy, globalposz).sub(p1).crs(p1.x - p2.x, p1.y - p2.y, p1.z - p2.z);
//		p.normal.nor();
		p.d = -p.normal.dot(globalposx, globalposy, globalposz);
	}

	public WallFrustum3d expand(WallFrustum3d frustum) {
		float bminx = frustum.bounds[0].x;
		float bmaxx = frustum.bounds[1].x;
		float bminy = frustum.bounds[0].y;
		float bmaxy = frustum.bounds[1].y;

		WallFrustum3d n = this, prev;
		do {
			if (n.intersects(frustum.bounds)) {
				float minx = n.bounds[0].x;
				float maxx = n.bounds[1].x;
				float miny = n.bounds[0].y;
				float maxy = n.bounds[1].y;

				if (bminx < minx)
					minx = bminx;
				if (bmaxx > maxx)
					maxx = bmaxx;
				if (bminy < miny)
					miny = bminy;
				if (bmaxy > maxy)
					maxy = bmaxy;

				if (minx < n.bounds[0].x || maxx > n.bounds[1].x || miny < n.bounds[0].y || maxy > n.bounds[1].y) {
					n.handled = false;
					n.setBounds(minx, miny, maxx, maxy);
					n.rebuildRequest = true;
					return n;
				}

				return null;
			}
			prev = n;
			n = n.next;
		} while (n != null);

		prev.next = frustum;
		return frustum;
	}

	// Backend

	private WallFrustum3d setBounds(float x1, float y1, float x2, float y2) {
		bounds[0].x = x1;
		bounds[0].y = y1;

		bounds[1].x = x2;
		bounds[1].y = y2;
		return this;
	}

	public boolean intersects(Vector3[] p) {
		return p[0].x <= bounds[1].x && p[1].x >= bounds[0].x && p[0].y <= bounds[1].y && p[1].y >= bounds[0].y;
	}

	private Vector3[] calcBounds(ArrayList<? extends Vector3> points) {
		bounds[0].x = bounds[1].x = points.get(0).x;
		bounds[0].y = bounds[1].y = points.get(0).y;

		for (int i = 1; i < points.size(); i++) {
			Vector3 v = points.get(i);
			if (v.x < bounds[0].x)
				bounds[0].x = v.x;
			if (v.y < bounds[0].y)
				bounds[0].y = v.y;

			if (v.x > bounds[1].x)
				bounds[1].x = v.x;
			if (v.y > bounds[1].y)
				bounds[1].y = v.y;
		}

		bounds[0].x = (float) Math.floor(bounds[0].x);
		bounds[1].x = (float) Math.ceil(bounds[1].x);
		bounds[0].y = (float) Math.floor(bounds[0].y);
		bounds[1].y = (float) Math.ceil(bounds[1].y);

		bounds[0].x = Math.max(bounds[0].x, 0);
		bounds[1].x = Math.min(bounds[1].x, windowx2 + 1);
		bounds[0].y = Math.max(bounds[0].y, 0);
		bounds[1].y = Math.min(bounds[1].y, windowy2 + 1);

		return bounds;
	}

	public boolean clipBounds(WallFrustum3d viewport) {
		bounds[0].x = Math.max(viewport.bounds[0].x, bounds[0].x);
		bounds[1].x = Math.min(viewport.bounds[1].x, bounds[1].x);
		bounds[0].y = Math.max(viewport.bounds[0].y, bounds[0].y);
		bounds[1].y = Math.min(viewport.bounds[1].y, bounds[1].y);

		return !(bounds[1].x - bounds[0].x <= 0) && !(bounds[1].y - bounds[0].y <= 0);
	}

	public static final Plane[] clipPlanes = { new Plane(), new Plane() };

	public Plane[] getPlanes() {
		// XXX Temporaly code below:

		Vector3[] bounds = getBounds();
		float b0x = bounds[0].x;
		float b0y = bounds[0].y;
		float b1x = bounds[1].x;
		float b1y = bounds[1].y;

		cam.unproject(tmpVec[0].set(b0x, b0y, 1));
		cam.unproject(tmpVec[1].set(b0x, b1y, 1));
		cam.unproject(tmpVec[2].set(b1x, b1y, 1));
		cam.unproject(tmpVec[3].set(b1x, b0y, 1));

		build(clipPlanes[0], tmpVec[1], tmpVec[0]); // left
		build(clipPlanes[1], tmpVec[3], tmpVec[2]); // right

		return clipPlanes;
	}

	public Vector3[] getBounds() {
		float minx = this.bounds[0].x;
		float maxx = this.bounds[1].x;
		float miny = this.bounds[0].y;
		float maxy = this.bounds[1].y;

		WallFrustum3d n = this.next;
		while (n != null) {
			if (n.bounds[0].x < minx)
				minx = n.bounds[0].x;
			if (n.bounds[1].x > maxx)
				maxx = n.bounds[1].x;
			if (n.bounds[0].y < miny)
				miny = n.bounds[0].y;
			if (n.bounds[1].y > maxy)
				maxy = n.bounds[1].y;
			n = n.next;
		}

		tmpVec[0].set(minx, miny, 0);
		tmpVec[1].set(maxx, maxy, 0);
		return tmpVec;
	}

	@Override
	public void reset() {
		next = null;
		handled = false;
		rebuildRequest = false;
	}
}
