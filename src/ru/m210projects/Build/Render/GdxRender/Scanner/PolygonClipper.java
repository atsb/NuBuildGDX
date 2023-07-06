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

}
