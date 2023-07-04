package ru.m210projects.Build.Render.GdxRender.Scanner;

import static ru.m210projects.Build.Engine.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.Plane;

import ru.m210projects.Build.Gameutils;
import ru.m210projects.Build.Render.GdxRender.Pool;
import ru.m210projects.Build.Render.GdxRender.Pool.Poolable;
import ru.m210projects.Build.Types.LinkedList;
import ru.m210projects.Build.Types.LinkedList.Node;
import ru.m210projects.Build.Types.RuntimeArray;
import ru.m210projects.Build.Types.WALL;

public class RayCaster {

	protected List<Segment> segments = new ArrayList<Segment>(); // WALLS
	protected RuntimeArray<EndPoint> endpoints = new RuntimeArray<EndPoint>(); // wall points (coordinates)
	protected LinkedList<Segment> open = new LinkedList<Segment>();
	protected byte[] gotwall = new byte[MAXWALLS >> 3];
	protected byte[] handled = new byte[MAXWALLS >> 3];
	protected boolean globalcheck;

	private final Pool<Segment> pSegmentPool = new Pool<Segment>() {
		@Override
		protected Segment newObject() {
			return new Segment();
		}
	};

	private final Pool<EndPoint> pEndpointPool = new Pool<EndPoint>() {
		@Override
		protected EndPoint newObject() {
			return new EndPoint();
		}
	};

	public class EndPoint {
		public float x, y;
		public boolean begin;
		public Segment segment;
		public float angle;
	}

	public class Segment extends Node<Segment> implements Poolable { // WALL
		public EndPoint p1, p2;
		public int wallid;
		public int index;

		@Override
		public Segment getValue() {
			return this;
		}

		public boolean isPortal() {
			return wall[wallid].nextsector != -1;
		}

		@Override
		public void reset() {
			p1 = p2 = null;
			next = prev = null;
			wallid = index = 0;
		}
	}

	protected Comparator<EndPoint> comparator = new Comparator<EndPoint>() {
		@Override
		public int compare(EndPoint a, EndPoint b) {
			// XXX Comparison method violates its general contract!
			if (a.angle != b.angle)
				return a.angle > b.angle ? 1 : -1;
			if (a.begin != b.begin)
				return !a.begin && b.begin ? 1 : -1;
			return 0;
		}
	};

	protected Comparator<Segment> wallfront = new Comparator<Segment>() {
		@Override
		public int compare(Segment o1, Segment o2) {
			if (!wallfront(o1, o2))
				return -1;
			return 0;
		}
	};

	public void init(boolean globalcheck) {
		this.segments.clear();
		this.endpoints.clear();
		Gameutils.fill(gotwall, (byte) 0);
		Gameutils.fill(handled, (byte) 0);
		this.globalcheck = globalcheck;

		this.pSegmentPool.reset();
		this.pEndpointPool.reset();
	}

	public boolean wallfront(Segment a, Segment b) {
		float x11 = a.p1.x;
		float y11 = a.p1.y;
		float x21 = a.p2.x;
		float y21 = a.p2.y;

		float x12 = b.p1.x;
		float y12 = b.p1.y;
		float x22 = b.p2.x;
		float y22 = b.p2.y;

		float dx = x21 - x11;
		float dy = y21 - y11;

		final double f = 0.001;
		final double invf = 1.0 - f;
		double px = (x12 * invf) + (x22 * f);
		double py = (y12 * invf) + (y22 * f);

		double cross = dx * (py - y11) - dy * (px - x11);
//		double cross = dx * (y12 - y11) - dy * (x12 - x11);
		boolean t1 = (cross < 0.00001); // p1(l2) vs. l1

		px = (x22 * invf) + (x12 * f);
		py = (y22 * invf) + (y12 * f);
		double cross1 = dx * (py - y11) - dy * (px - x11);
//		double cross1 = dx * (y22 - y11) - dy * (x22 - x11);
		boolean t2 = (cross1 < 0.00001); // p2(l2) vs. l1

		if (t1 == t2) {
			t1 = (dx * (globalposy - y11) - dy * (globalposx - x11) < 0.00001); // pos vs. l1
			if (t2 == t1) {
				return true;
			}
		}

		dx = x22 - x12;
		dy = y22 - y12;

		px = (x11 * invf) + (x21 * f);
		py = (y11 * invf) + (y21 * f);

		double cross3 = dx * (py - y12) - dy * (px - x12);
//		double cross3 = dx * (y11 - y12) - dy * (x11 - x12);
		t1 = (cross3 < 0.00001); // p1(l1) vs. l2

		px = (x21 * invf) + (x11 * f);
		py = (y21 * invf) + (y11 * f);
		double cross4 = dx * (py - y12) - dy * (px - x12);
//		double cross4 = dx * (y21 - y12) - dy * (x21 - x12);
		t2 = (cross4 < 0.00001); // p2(l1) vs. l2

		if (t1 == t2) {
			if (globalcheck) {
				if (Math.abs(cross) < 0.00001 && Math.abs(cross1) < 0.00001 && Math.abs(cross3) < 0.00001
						&& Math.abs(cross4) < 0.00001) {
					if (a.isPortal() && !b.isPortal()) // e2l8
						return false;
				}
			}

			t1 = (dx * (globalposy - y12) - dy * (globalposx - x12) < 0.00001); // pos vs. l2
			return t2 != t1;
		}

		return false;
	}

	public Segment addSegment(int id, float x1, float y1, float x2, float y2) {
		EndPoint p1 = pEndpointPool.obtain();
		EndPoint p2 = pEndpointPool.obtain();
		Segment segment = pSegmentPool.obtain();

		p1.x = x1;
		p1.y = y1;
		p2.x = x2;
		p2.y = y2;
		p1.segment = segment;
		p2.segment = segment;
		segment.p1 = p1;
		segment.p2 = p2;
		segment.wallid = id;
		segment.index = segments.size();

		this.segments.add(segment);
		this.endpoints.add(p1);
		this.endpoints.add(p2);

		handled[id >> 3] |= pow2char[id & 7];

		return segment;
	}

	public void update() {
		for (int i = 0; i < segments.size(); i++) {
			Segment segment = segments.get(i);

			segment.p1.angle = atan2((segment.p1.y - globalposy), (segment.p1.x - globalposx));
			segment.p2.angle = atan2((segment.p2.y - globalposy), (segment.p2.x - globalposx));

			float dAngle = (segment.p2.angle - segment.p1.angle);
			if ((dAngle <= -2))
				dAngle += 4;
			if ((dAngle > 2))
				dAngle -= 4;

			segment.p1.begin = (dAngle > 0.0);
			segment.p2.begin = !(segment.p1.begin);
		}

		sweep();
	}

	public float atan2(float dx, float dy) {
		float len = (float) Math.sqrt(dx * dx + dy * dy);
		if(len == 0)
			return 0;
		dx /= len;
		if (dy >= 0)
			return dx;
		if (dx >= 0)
			return 2 - dx;
		return -(2 + dx);
	}

	public void add(int z, WallFrustum2d frust) {
		WALL wal = wall[z];
		WALL wal2 = wall[wal.point2];

		if (frust == null || frust.sectnum == globalcursectnum) {
			addSegment(z, wal.x, wal.y, wal2.x, wal2.y);
		} else {
			WallFrustum2d f = frust;
			do {
				if (f.isGreater180())
					addSegment(z, wal.x, wal.y, wal2.x, wal2.y);
				else
					addClippedSegment(f, z);
				f = f.next;
			} while (f != null);
		}

//		addSegment(z, wal.x, wal.y, wal2.x, wal2.y);
	}

	public void addClippedSegment(WallFrustum2d frustum, int z) {
		Plane[] planes = frustum.planes;
		WALL p1 = wall[z];
		WALL p2 = wall[p1.point2];

		float p1x = p1.x;
		float p1y = p1.y;
		float p2x = p2.x;
		float p2y = p2.y;

		for (int i = 0; i < 2; i++) {
			Vector3 p = planes[i].normal;

			float t1 = p.dot(p1x - globalposx, p1y - globalposy, 0);
			float t2 = p.dot(p2x - globalposx, p2y - globalposy, 0);

			if (t1 < 0.0001f && t2 < 0.0001f) {
				float angle1 = atan2((p1y - globalposy), (p1x - globalposx));
				float angle2 = atan2((p2y - globalposy), (p2x - globalposx));

				float dAngle = (angle2 - angle1);
				if (dAngle >= 0.01f) // XXX E3L2 line wall bug fix
					handled[z >> 3] |= pow2char[z & 7];
				return;
			}

			if ((t1 >= -0.0001f) != (t2 >= -0.0001f)) {
				float r = t1 / (t1 - t2);
				float dx = (p2x - p1x);
				float dy = (p2y - p1y);

				if (t1 >= -0.0001f) {
					p2x = (float) Math.ceil(dx * r + p1x);
					p2y = (float) Math.ceil(dy * r + p1y);
				} else {
					p1x = (float) Math.ceil(dx * r + p1x);
					p1y = (float) Math.ceil(dy * r + p1y);
				}
			}
		}

		addSegment(z, p1x, p1y, p2x, p2y);
	}

	public boolean check(int z) {
		if ((handled[z >> 3] & pow2char[z & 7]) == 0)
			return true;

		return (gotwall[z >> 3] & pow2char[z & 7]) != 0;
	}

	protected void sweep() {
		endpoints.sort(comparator);

		open.clear();

		for (int i = 0; i < 2; i++) {

			for (int pi = 0; pi < endpoints.size(); pi++) {
				EndPoint p = endpoints.get(pi);
				Segment current_old = open.getFirst();

				while (current_old != null && current_old.isPortal()) {
					if (i == 1)
						gotwall[current_old.wallid >> 3] |= pow2char[current_old.wallid & 7];
					open.remove(current_old);
					current_old = open.getFirst();
				}

				if (p.begin) {
					open.add(p.segment, wallfront);
				} else {
					open.remove(p.segment);
				}

				if (current_old != open.getFirst()) {
					if (i == 1) {
						if (current_old != null) {
							int z = current_old.wallid;
							gotwall[z >> 3] |= pow2char[z & 7];
						}
					}
				}
			}
		}
	}
}
