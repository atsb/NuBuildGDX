package ru.m210projects.Build.Render.GdxRender;

import static java.lang.Math.sqrt;
import static ru.m210projects.Build.Engine.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.NumberUtils;

import ru.m210projects.Build.Engine;
import ru.m210projects.Build.Render.GLInfo;
import ru.m210projects.Build.Render.GdxRender.WorldMesh.Heinum;
import ru.m210projects.Build.Types.*;

public class Tesselator {

	public enum Type {
		Floor, Ceiling, Wall, Sky, Quad;

		private Heinum heinum;

		public Type setHeinum(Heinum heinum) {
			this.heinum = heinum;
			return this;
		}

		public Heinum getHeinum() {
			return heinum;
		}
	}

	private final Pool<Zoid_t> pZoidsPool = new Pool<Zoid_t>() {
		@Override
		protected Zoid_t newObject() {
			return new Zoid_t();
		}
	};

	protected final VertexAttribute[] attributes;

	private final Engine engine;
	private final SurfaceInfo surf;
	private final int vertexSize;
	public final List<Zoid_t> zoids = new ArrayList<Zoid_t>();

	private int sectnum = -1;

	private final Vertex[] pol = new Vertex[] { new Vertex(0, 0), new Vertex(1, 0), new Vertex(1, 1),
			new Vertex(0, 1) };
	private final Vector3 norm = new Vector3();
	private final Vertex[] vertex = new Vertex[3];

	private final RuntimeArray<Integer> secy = new RuntimeArray<Integer>() {
		@Override
		public void sort(Comparator<? super Integer> c) {
			Arrays.sort(elementData, 0, size);
		}
	};
	private final ArrayList<Float> trapx0 = new ArrayList<Float>();
	private final ArrayList<Float> trapx1 = new ArrayList<Float>();

	private final WorldMesh mesh;

	public Tesselator(WorldMesh mesh, VertexAttribute... attributes) {
		this.mesh = mesh;
		this.engine = mesh.engine;
		this.attributes = attributes;

		int size = 0;
		for (int i = 0; i < attributes.length; i++) {
			if (!attributes[i].equals(VertexAttribute.ColorPacked()))
				size += attributes[i].numComponents;
			else
				size++;
		}

		this.vertexSize = size;
		this.surf = new SurfaceInfo();
	}

	public int getVertexSize() {
		return vertexSize;
	}

	public void setSector(int sectnum, boolean initZoids) {
		if (initZoids && this.sectnum != sectnum)
			initZoids(sectnum);
		this.sectnum = sectnum;
	}

	public int getSector() {
		return sectnum;
	}

	private void initZoids(final int sectnum) {
		final SECTOR sec = sector[sectnum];
		final int n = sec.wallnum;

		zoids.clear();
		secy.clear();
		pZoidsPool.reset();

		if (n < 3)
			return;

		float f, x0, y0, x1, y1, sy0, sy1;
		int g, s, secn, ntrap, cury;

		int startwall = sec.wallptr;
		int endwall = startwall + sec.wallnum - 1;
		int i = 0;
		for (int w = startwall; w <= endwall; w++) {
			secy.add(wall[w].y);
		}

		secy.sort(null);
		for (i = 0, secn = 0, cury = Integer.MIN_VALUE; i < n; i++) {// remove dups
			int val = secy.get(i);
			if (val > cury) {
				secy.set(secn++, (cury = val));
			}
		}

		for (s = 0; s < secn - 1; s++) {
			sy0 = secy.get(s);
			sy1 = secy.get(s + 1);

			trapx0.clear();
			trapx1.clear();
			ntrap = 0;

			startwall = sec.wallptr;
			endwall = startwall + sec.wallnum - 1;
			for (int w = startwall; w <= endwall; w++) {
				WALL wal = wall[w];
				WALL wal2 = wall[wal.point2];

				x0 = wal.x;
				y0 = wal.y;
				x1 = wal2.x;
				y1 = wal2.y;

				if (y0 > y1) {
					f = x0;
					x0 = x1;
					x1 = f;
					f = y0;
					y0 = y1;
					y1 = f;
				}
				if ((y0 >= sy1) || (y1 <= sy0))
					continue;
				if (y0 < sy0)
					x0 = (sy0 - wal.y) * (wal2.x - wal.x) / (wal2.y - wal.y) + wal.x;
				if (y1 > sy1)
					x1 = (sy1 - wal.y) * (wal2.x - wal.x) / (wal2.y - wal.y) + wal.x;

				trapx0.add(x0);
				trapx1.add(x1);
				ntrap++;
			}

			int j;
			for (g = (ntrap >> 1); g != 0; g >>= 1)
				for (i = 0; i < ntrap - g; i++)
					for (j = i; j >= 0; j -= g) {

						if (trapx0.get(j) + trapx1.get(j) <= trapx0.get(j + g) + trapx1.get(j + g))
							break;

						f = trapx0.get(j);
						trapx0.set(j, trapx0.get(j + g));
						trapx0.set(j + g, f);
						f = trapx1.get(j);
						trapx1.set(j, trapx1.get(j + g));
						trapx1.set(j + g, f);
					}

			if (ntrap < 2) {
				continue;
			}

			for (i = 0; i < ntrap; i = j + 1) {
				j = i + 1;

				if ((trapx0.get(j) <= trapx0.get(i)) && (trapx1.get(j) <= trapx1.get(i)))
					continue;
				while ((j + 2 < ntrap) && (trapx0.get(j + 1) <= trapx0.get(j)) && (trapx1.get(j + 1) <= trapx1.get(j)))
					j += 2;

				Zoid_t zoid = pZoidsPool.obtain();

				zoid.x[0] = trapx0.get(i);
				zoid.x[1] = trapx0.get(j);
				zoid.y[0] = sy0;
				zoid.x[3] = trapx1.get(i);
				zoid.x[2] = trapx1.get(j);
				zoid.y[1] = sy1;

				zoids.add(zoid);
			}
		}
	}

	public int getMaxVertices() {
		int vertices = 2 * zoids.size() * 6; // ceiling and floor
		vertices += 30 * sector[sectnum].wallnum; // (6 - top, 6 - middle, 6 bottom, 12 sky)
		return 2 * vertices;
	}

	public SurfaceInfo getSurface(Type type, int num, FloatArray vertices) {
		surf.clear();

		int count = 0;
		switch (type) {
		case Quad: {
			float SIZEX = 1.0f / 2;
			float SIZEY = 1.0f / 2;
			ArrayList<Vertex> pol = new ArrayList<Vertex>();
			pol.add((Vertex) new Vertex(1, 1).set(-SIZEX, SIZEY, 0));
			pol.add((Vertex) new Vertex(0, 1).set(SIZEX, SIZEY, 0));
			pol.add((Vertex) new Vertex(0, 0).set(SIZEX, -SIZEY, 0));
			pol.add((Vertex) new Vertex(1, 0).set(-SIZEX, -SIZEY, 0));

			for (int v = 0; v < 4; v++) {
				Vertex vx = pol.get(v);
				for (int a = 0; a < attributes.length; a++) {
					switch (attributes[a].usage) {
					case Usage.Position:
						vertices.addAll(vx.x, vx.y, vx.z);
						break;
					case Usage.TextureCoordinates:
						vertices.addAll(vx.u, vx.v);
						break;
					case Usage.ColorUnpacked:
						vertices.addAll(1, 1, 1, 1);
						break;
					case Usage.ColorPacked:
						vertices.add(NumberUtils.intToFloatColor(-1));
						break;
					case Usage.Normal:
						vertices.addAll(norm.x, norm.y, norm.z);
						break;
					}
				}
				count++;
			}
			break;
		}
		case Floor:
		case Ceiling: {
			SECTOR sec = sector[num];
			surf.picnum = type == Type.Floor ? sec.floorpicnum : sec.ceilingpicnum;
			surf.obj = sec;
//			surf.shade = type == Type.Floor ? sec.floorshade : sec.ceilingshade;
//			surf.pal = type == Type.Floor ? sec.floorpal : sec.ceilingpal;
			Tile pic = engine.getTile(surf.picnum);

			int n = 0, j = 0;
			for (int i = 0; i < zoids.size(); i++) {
				for (j = 0, n = 0; j < 4; j++) {
					pol[n].x = zoids.get(i).x[j];
					pol[n].y = zoids.get(i).y[j >> 1];

					if ((n == 0) || (pol[n].x != pol[n - 1].x) || (pol[n].y != pol[n - 1].y)) {
						pol[n].z = (type == Type.Floor)
								? engine.getflorzofslope((short) num, (int) (pol[n].x), (int) (pol[n].y))
								: engine.getceilzofslope((short) num, (int) (pol[n].x), (int) (pol[n].y));
						n++;
					}
				}

				if (n < 3)
					continue;

				vertex[0] = pol[0];
				for (j = 2; j < n; j++) {
					if (type == Type.Floor) {
						vertex[1] = pol[j - 1];
						vertex[2] = pol[j];
					} else {
						vertex[1] = pol[j];
						vertex[2] = pol[j - 1];
					}

					norm.set(vertex[2]).sub(vertex[0]);
					float dx = (vertex[1].x - vertex[0].x);
					float dy = (vertex[1].y - vertex[0].y);
					float dz = (vertex[1].z - vertex[0].z);
					norm.crs(dx, dy, dz).nor();

					for (int v = 0; v < 3; v++) {
						Vertex vx = vertex[v];
						for (int a = 0; a < attributes.length; a++) {
							switch (attributes[a].usage) {
							case Usage.Position:
								vertices.addAll(vx.x / mesh.scalexy, vx.y / mesh.scalexy, vx.z / mesh.scalez);
								break;
							case Usage.TextureCoordinates:
								float uptr = vx.x;
								float vptr = vx.y;
								if (!pic.hasSize()) {
									vertices.addAll(0.0f, 0.0f);
									break;
								}

								// Texture Relativity
								if ((type == Type.Floor) ? sec.isRelativeTexFloor() : sec.isRelativeTexCeiling()) {
									WALL wal = wall[sec.wallptr];
									WALL wal2 = wall[wal.point2];

									float vecx = wal.x - wal2.x;
									float vecy = wal.y - wal2.y;
									float len = (float) sqrt(vecx * vecx + vecy * vecy);
									vecx /= len;
									vecy /= len;

									float nuptr = uptr - wal.x;
									float nvptr = vptr - wal.y;

									uptr = -nuptr * vecx - vecy * nvptr;
									vptr = nvptr * vecx - vecy * nuptr;

									// Hack for slopes w/ relative alignment
									if ((type == Type.Floor) ? sec.isSlopedFloor() : sec.isSlopedCeiling()) {
										double r = ((type == Type.Floor) ? sec.floorheinum : sec.ceilingheinum)
												/ 4096.0;
										r = sqrt(r * r + 1);
										if ((type == Type.Floor) ? !sec.isTexSwapedFloor() : !sec.isTexSwapedCeiling())
											vptr *= r;
										else
											uptr *= r;
									}
								}

								float uCoff = 16.0f * pic.getWidth();
								float vCoff = -16.0f * pic.getHeight();

								// Texture's x & y is swapped
								if ((type == Type.Floor) ? sec.isTexSwapedFloor() : sec.isTexSwapedCeiling()) {
									float tmp = uptr;
									uptr = -vptr;
									vptr = -tmp;
								}

								// Texture Expansion
								if ((type == Type.Floor) ? sec.isTexSmooshedFloor() : sec.isTexSmooshedCeiling()) {
									uCoff /= 2;
									vCoff /= 2;
								}

								// Texture's x is flipped
								if ((type == Type.Floor) ? sec.isTexXFlippedFloor() : sec.isTexXFlippedCeiling())
									uCoff *= -1;

								// Texture's y is flipped
								if ((type == Type.Floor) ? sec.isTexYFlippedFloor() : sec.isTexYFlippedCeiling())
									vCoff *= -1;

								float uPanning = ((type == Type.Floor) ? sec.floorxpanning : sec.ceilingxpanning)
										/ 255.0f;
								float vPanning = ((type == Type.Floor) ? sec.floorypanning : sec.ceilingypanning)
										/ 255.0f;

								vertices.addAll(uptr / uCoff + uPanning, vptr / vCoff + vPanning);
								break;
							case Usage.ColorUnpacked:
								vertices.addAll(1, 1, 1, 1);
								break;
							case Usage.ColorPacked:
								vertices.add(NumberUtils.intToFloatColor(-1));
								break;
							case Usage.Normal:
								vertices.addAll(norm.x, norm.y, norm.z);
								break;
							}
						}
						count++;
					}
				}
			}
			break;
		}
		case Wall:
		case Sky: {
			WALL wal = wall[num];
			Heinum heinum = type.getHeinum();

			ArrayList<Vertex> pol;
			if ((pol = mesh.getPoints(type.getHeinum(), sectnum, num)) == null || pol.size() < 3)
				return null;

			float uCoff = 1, vCoff = 1, uPanning = 0, vPanning = 0, vOffs = 0;
			if (type != Type.Sky) {
				int k = 0;
				if (heinum == Heinum.Portal)
					k = -1;
				else if (heinum == Heinum.Lower)
					k = 1;

				WALL ptr = wal;
				if (k == 1 && wal.nextsector != -1 && wal.isSwapped())
					ptr = wall[wal.nextwall];
				vOffs = getVCoord(wal, sectnum, k);

				int picnum = (k == -1 ? ptr.overpicnum : ptr.picnum);
				surf.picnum = picnum;
				surf.obj = ptr;

				Tile pic = engine.getTile(picnum);
				if (pic.hasSize()) {
					uCoff = wal.xrepeat * 8.0f / pic.getWidth();
					vCoff = -wal.yrepeat * 4.0f / GLInfo.calcSize(pic.getHeight());
					uPanning = ptr.xpanning / (float) pic.getWidth();
					vPanning = ptr.ypanning / 256.0f;

					if (wal.isXFlip()) {
						uCoff *= -1;
						uPanning = (wal.xrepeat * 8.0f + ptr.xpanning) / pic.getWidth();
					}

					if (ptr.isYFlip()) {
						vCoff *= -1;
						vPanning *= -1;
					}
				}
			} else {
				surf.picnum = heinum == Heinum.SkyLower ? sector[sectnum].floorpicnum : sector[sectnum].ceilingpicnum;
				surf.obj = sector[sectnum];
			}

			vertex[0] = pol.get(0);
			for (int j = 2; j < pol.size(); j++) {
				vertex[1] = pol.get(j - 1);
				vertex[2] = pol.get(j);

				norm.set(vertex[2]).sub(vertex[0]);
				float dx = (vertex[1].x - vertex[0].x);
				float dy = (vertex[1].y - vertex[0].y);
				float dz = (vertex[1].z - vertex[0].z);
				norm.crs(dx, dy, dz).nor();

				for (int i = 0; i < 3; i++) {
					Vertex ver = vertex[i];
					for (int a = 0; a < attributes.length; a++) {
						switch (attributes[a].usage) {
						case Usage.Position:
							vertices.addAll(ver.x / mesh.scalexy, ver.y / mesh.scalexy, ver.z / mesh.scalez);
							break;
						case Usage.TextureCoordinates:
							if (type != Type.Sky)
								vertices.addAll(uCoff * ver.u + uPanning,
										(vCoff * (vOffs - ver.z) / mesh.scalez) + vPanning);
							else
								vertices.addAll(ver.u, ver.v);
							break;
						case Usage.ColorUnpacked:
							vertices.addAll(1, 1, 1, 1);
							break;
						case Usage.ColorPacked:
							vertices.add(NumberUtils.intToFloatColor(-1));
							break;
						case Usage.Normal:
							vertices.addAll(norm.x, norm.y, norm.z);
							break;
						}
					}
					count++;
				}
			}
			break;
		}
		// end switch
		}

		if (count == 0)
			return null;

		surf.size = count;
		if (type == Type.Wall || type == Type.Sky) {
			surf.limit = 6; // Walls are quads (3 + 3 tris)
			if (mesh.getMesh() == null) { // when initializing
				int pads = 6 - count;
				for (int i = 0; i < pads; i++) {
					for (int j = 0; j < getVertexSize(); j++)
						vertices.add(-1);
				}
			}
		} else
			surf.limit = count;

		return surf;
	}

	private int getVCoord(WALL wal, int sectnum, int k) {
		short nextsectnum = wal.nextsector;
		int s3 = sectnum;
		int align = 0;

		if (nextsectnum != -1) {
			if (k == -1) { // masked wall
				if (wal.isOneWay()) {
					if (!wal.isBottomAligned())
						s3 = nextsectnum;
				} else {
					align = wal.isBottomAligned() ? 1 : 0;
					if (wal.isBottomAligned())
						s3 = (sector[sectnum].floorz < sector[nextsectnum].floorz) ? sectnum : nextsectnum;
					else
						s3 = (sector[sectnum].ceilingz < sector[nextsectnum].ceilingz) ? nextsectnum : sectnum;
				}
			} else {
				if (k == 1) { // under
					if (wal.isSwapped())
						wal = wall[wal.nextwall];
					align = wal.isBottomAligned() ? 0 : 1;
				}

				if (!wal.isBottomAligned())
					s3 = nextsectnum;
			}
		} else
			align = wal.isBottomAligned() ? 1 : 0;

		WALL ptr = wall[sector[s3].wallptr];
		if (align == 0)
			return engine.getceilzofslope((short) s3, ptr.x, ptr.y);
		return engine.getflorzofslope((short) s3, ptr.x, ptr.y);
	}

	public static class Vertex extends Vector3 {
		private static final long serialVersionUID = 2322711328782046279L;
		protected float u, v;

		public Vertex(int u, int v) {
			this.u = u;
			this.v = v;
		}

		protected void set(WALL i, float z, float u, float v) {
			this.set(i.x, i.y, z);
			this.u = u;
			this.v = v;
		}
	}

	protected static class SurfaceInfo {
		public int picnum;
		public Object obj;
		public int size, limit;

		protected void clear() {
			picnum = -1;
			obj = null;
			size = 0;
			limit = 0;
		}

		protected int getSize() {
			return size;
		}

		protected int getLimit() {
			return limit;
		}
	}

	private static class Zoid_t {
		private final float[] x;
		private final float[] y;

		public Zoid_t() {
			x = new float[4];
			y = new float[2];
		}
	}
}
