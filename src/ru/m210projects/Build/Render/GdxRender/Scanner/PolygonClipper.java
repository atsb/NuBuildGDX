package ru.m210projects.Build.Render.GdxRender.Scanner;

import java.util.ArrayList;

import com.badlogic.gdx.math.Frustum;
import com.badlogic.gdx.math.Plane;

import ru.m210projects.Build.Render.GdxRender.Pool;
import ru.m210projects.Build.Render.GdxRender.Tesselator.Vertex;

public class PolygonClipper {

	private final Pool<Vertex> vecs = new Pool<Vertex>() {
		@Override
		protected Vertex newObject() {
			return new Vertex(0, 0);
		}
	};
	ArrayList<Vertex> list = new ArrayList<Vertex>();

//	private Vector3[] dst = new Vector3[8];
//	private Vector3[] swap = new Vector3[8];
//	private int size;

//	public PolygonClipper() {
//		for (int i = 0; i < 8; i++) {
//			dst[i] = new Vector3();
//			swap[i] = new Vector3();
//		}
//	}

//	private int ClipPlane(Vector3[] dst, Vector3[] src, int num_verts, final Plane p) {
//		int num = 0;
//		for (int i = 0; i < num_verts; i++) {
//			int j = i + 1;
//			if (j >= num_verts)
//				j = 0;
//
//			Vector3 v1 = src[i];
//			Vector3 v2 = src[j];
//
//			float t1 = p.distance(v1);
//			float t2 = p.distance(v2);
//
//			if (t1 >= 0.0f)
//				dst[num++].set(v1);
//
//			if ((t1 >= 0.0f) != (t2 >= 0.0f)) {
//				float r = t1 / (t1 - t2);
//				dst[num++].set(v2).sub(v1).scl(r).add(v1);
//			}
//		}
//		return num;
//	}

	private void ClipPlane(final Plane p, ArrayList<Vertex> src, ArrayList<Vertex> dst) {
		dst.clear();
		for (int i = 0; i < src.size(); i++) {
			int j = i + 1;
			if (j >= src.size())
				j = 0;

			Vertex v1 = src.get(i);
			Vertex v2 = src.get(j);

			float t1 = p.distance(v1);
			float t2 = p.distance(v2);

			if (t1 >= 0.0f)
				dst.add(v1);

			if ((t1 >= 0.0f) != (t2 >= 0.0f)) {
				float r = t1 / (t1 - t2);
				dst.add((Vertex) vecs.obtain().set(v2).sub(v1).scl(r).add(v1));
			}
		}
	}

//	public Vector3[] ClipPolygon(Frustum frustum, Vector3[] src) {
//		Plane[] planes = frustum.planes;
//		size = ClipPlane(dst, src, src.length, planes[2]);
//		for (int i = 3; i < 6; i++) {
//			if ((i % 2) != 0) {
//				size = ClipPlane(swap, dst, size, planes[i]);
//			} else
//				size = ClipPlane(dst, swap, size, planes[i]);
//		}
//		return swap;
//	}

	public ArrayList<Vertex> ClipPolygon(Frustum frustum, ArrayList<Vertex> src) {
		vecs.reset();
		final Plane[] planes = frustum.planes;

		ClipPlane(planes[2], src, list);
		for (int i = 3; i < 6; i++) {
			if ((i % 2) != 0) {
				ClipPlane(planes[i], list, src);
			} else
				ClipPlane(planes[i], src, list);
		}
		return src;
	}

//	public Vector3[] ClipPolygon(WallFrustum3d frustum, Vector3[] src, int len) {
//		Plane[] planes = frustum.planes;
//		size = ClipPlane(dst, src, len, planes[0]);
//		for (int i = 1; i < planes.length; i++) {
//			if ((i % 2) != 0) {
//				size = ClipPlane(swap, dst, size, planes[i]);
//			} else
//				size = ClipPlane(dst, swap, size, planes[i]);
//		}
//		return swap;
//	}
//
//	public int getSize() {
//		return size;
//	}

}
