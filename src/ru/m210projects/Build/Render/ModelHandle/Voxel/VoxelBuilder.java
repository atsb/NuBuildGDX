package ru.m210projects.Build.Render.ModelHandle.Voxel;

import static java.lang.Math.max;
import static ru.m210projects.Build.Pragmas.klabs;

import java.util.Arrays;
import java.util.HashMap;

import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.NumberUtils;
import com.badlogic.gdx.utils.ShortArray;

import ru.m210projects.Build.Types.Tile;

public class VoxelBuilder {

	// For loading / conversion only

	public class Rectangle {
		public Vertex[] v = { new Vertex(), new Vertex(), new Vertex(), new Vertex() };

		public int getX(int num) {
			return v[num].x;
		}

		public int getY(int num) {
			return v[num].y;
		}

		public int getZ(int num) {
			return v[num].z;
		}

		public int getU(int num) {
			return v[num].u;
		}

		public int getV(int num) {
			return v[num].v;
		}
	}

	private class Vertex {
		private int x, y, z, u, v;
	}

	protected int xsiz, ysiz, zsiz, yzsiz;
	protected HashMap<Integer, Byte> vcol;
	protected Vector2[] shp;
	protected int[] shcntmal;
	protected int shcnt;
	protected int shcntp;
	protected int mytexo5;
	protected int[] zbit;
	protected int gmaxx;
	protected int gmaxy;
	protected int garea;
	protected int[] pow2m1;
	protected int[] pal;
	protected int mytexx, mytexy;
	protected byte[] mytex;

	// Output data

	protected Rectangle[] quad;
	protected int qcnt;
	protected int[] qfacind = new int[7];
	private final FloatArray vertices;
	private final ShortArray indices;
	private final Tile texture;
	private final int vertexSize;

	public VoxelBuilder(VoxelData vox, int voxmip) {
		vertices = new FloatArray();
		indices = new ShortArray();

		pal = vox.pal;
		vox2poly(vox, voxmip);

		texture = new Tile().setWidth(mytexx).setHeight(mytexy);
		texture.data = mytex;

		this.vertexSize = 6;
	}

	public Tile getTexture() {
		return texture;
	}

	public int getVertexSize() {
		return vertexSize;
	}

	public VertexAttribute[] getAttributes() {
		return new VertexAttribute[] { VertexAttribute.Position(), VertexAttribute.ColorPacked(), VertexAttribute.TexCoords(0) };
	}

	public float[] getVertices() {
		return vertices.toArray();
	}

	public short[] getIndices() {
		return indices.toArray();
	}

	private void putvox(int x, int y, int z, byte col) {
		z += x * yzsiz + y * zsiz;
		vcol.put(z, col);
	}

	private byte getvox(int x, int y, int z) {
		z += x * yzsiz + y * zsiz;
		Byte col = vcol.get(z);
		if (col == null)
			return 0; // (0x808080);
		return (byte) ((pal[col & 0xFF] & 0xFFFFFFFFL) >> 24);
	}

	// Set all bits in vbit from (x,y,z0) to (x,y,z1-1) to 1's
	private void setzrange1(int[] lptr, int z0, int z1) {
		int z, ze;
		if (((z0 ^ z1) & ~31) == 0) {
			lptr[z0 >> 5] |= ((~(-1 << (z1 & 31))) & (-1 << (z0 & 31)));
			return;
		}
		z = (z0 >> 5);
		ze = (z1 >> 5);
		lptr[z] |= (-1 << (z0 & 31));
		for (z++; z < ze; z++)
			lptr[z] = -1;
		lptr[z] |= ~(-1 << (z1 & 31));
	}

	private void setrect(int x0, int y0, int dx, int dy) {
		int i, c, m, m1, x;

		i = y0 * mytexo5 + (x0 >> 5);
		dx += x0 - 1;
		c = (dx >> 5) - (x0 >> 5);
		m = ~pow2m1[x0 & 31];
		m1 = pow2m1[(dx & 31) + 1];
		if (c == 0) {
			for (m &= m1; dy != 0; dy--, i += mytexo5)
				zbit[i] |= m;
		} else {
			for (; dy != 0; dy--, i += mytexo5) {
				zbit[i] |= m;
				for (x = 1; x < c; x++)
					zbit[i + x] = -1;
				zbit[i + x] |= m1;
			}
		}
	}

	private int isolid(int[] vbit, int x, int y, int z) {
		if ((x & 0xFFFFFFFFL) >= (xsiz & 0xFFFFFFFFL))
			return (0);
		if ((y & 0xFFFFFFFFL) >= (ysiz & 0xFFFFFFFFL))
			return (0);
		if ((z & 0xFFFFFFFFL) >= (zsiz & 0xFFFFFFFFL))
			return (0);
		z += x * yzsiz + y * zsiz;
		return (vbit[z >> 5] & (1 << (z & 31)));
	}

	private void daquad(int i, int x0, int y0, int z0, int x1, int y1, int z1, int x2, int y2, int z2, int face) {
		if (i == 0)
			cntquad(x0, y0, z0, x1, y1, z1, x2, y2, z2, face);
		else
			addquad(x0, y0, z0, x1, y1, z1, x2, y2, z2, face);
	}

	private void cntquad(int x0, int y0, int z0, int x1, int y1, int z1, int x2, int y2, int z2, int face) {
		int x, y, z;

		x = klabs(x2 - x0);
		y = klabs(y2 - y0);
		z = klabs(z2 - z0);
		if (x == 0)
			x = z;
		else if (y == 0)
			y = z;
		if (x < y) {
			z = x;
			x = y;
			y = z;
		}
		shcntmal[shcnt + y * shcntp + x]++;
		if (x > gmaxx)
			gmaxx = x;
		if (y > gmaxy)
			gmaxy = y;
		garea += x * y;
		qcnt++;
	}

	private void addquad(int x0, int y0, int z0, int x1, int y1, int z1, int x2, int y2, int z2, int face) {
		int i, j, x, y, z, xx, yy, nx = 0, ny = 0, nz = 0;
		Rectangle qptr;
		int lptr;

		x = klabs(x2 - x0);
		y = klabs(y2 - y0);
		z = klabs(z2 - z0);
		if (x == 0) {
			x = y;
			y = z;
			i = 0;
		} else if (y == 0) {
			y = z;
			i = 1;
		} else
			i = 2;
		if (x < y) {
			z = x;
			x = y;
			y = z;
			i += 3;
		}
		z = shcntmal[shcnt + y * shcntp + x]++;

		lptr = (int) (shp[z].y * mytexx + shp[z].x);

		switch (face) {
		case 0:
			ny = y1;
			x2 = x0;
			x0 = x1;
			x1 = x2;
			break;
		case 1:
			ny = y0;
			y0++;
			y1++;
			y2++;
			break;
		case 2:
			nz = z1;
			y0 = y2;
			y2 = y1;
			y1 = y0;
			z0++;
			z1++;
			z2++;
			break;
		case 3:
			nz = z0;
			break;
		case 4:
			nx = x1;
			y2 = y0;
			y0 = y1;
			y1 = y2;
			x0++;
			x1++;
			x2++;
			break;
		case 5:
			nx = x0;
			break;
		}

		for (yy = 0; yy < y; yy++, lptr += mytexx)
			for (xx = 0; xx < x; xx++) {
				switch (face) {
				case 0:
					if (i < 3) {
						nx = x1 + x - 1 - xx;
						nz = z1 + yy;
					} // back
					else {
						nx = x1 + y - 1 - yy;
						nz = z1 + xx;
					}
					break;
				case 1:
					if (i < 3) {
						nx = x0 + xx;
						nz = z0 + yy;
					} // front
					else {
						nx = x0 + yy;
						nz = z0 + xx;
					}
					break;
				case 2:
					if (i < 3) {
						nx = x1 - x + xx;
						ny = y1 - 1 - yy;
					} // bot
					else {
						nx = x1 - 1 - yy;
						ny = y1 - 1 - xx;
					}
					break;
				case 3:
					if (i < 3) {
						nx = x0 + xx;
						ny = y0 + yy;
					} // top
					else {
						nx = x0 + yy;
						ny = y0 + xx;
					}
					break;
				case 4:
					if (i < 3) {
						ny = y1 + x - 1 - xx;
						nz = z1 + yy;
					} // right
					else {
						ny = y1 + y - 1 - yy;
						nz = z1 + xx;
					}
					break;
				case 5:
					if (i < 3) {
						ny = y0 + xx;
						nz = z0 + yy;
					} // left
					else {
						ny = y0 + yy;
						nz = z0 + xx;
					}
					break;
				}

				mytex[lptr + xx] = getvox(nx, ny, nz);
			}

		qptr = quad[qcnt];
		qptr.v[0].x = x0;
		qptr.v[0].y = y0;
		qptr.v[0].z = z0;
		qptr.v[1].x = x1;
		qptr.v[1].y = y1;
		qptr.v[1].z = z1;
		qptr.v[2].x = x2;
		qptr.v[2].y = y2;
		qptr.v[2].z = z2;
		for (j = 0; j < 3; j++) {
			qptr.v[j].u = (int) shp[z].x;
			qptr.v[j].v = (int) shp[z].y;
		}
		if (i < 3)
			qptr.v[1].u += x;
		else
			qptr.v[1].v += y;
		qptr.v[2].u += x;
		qptr.v[2].v += y;

		qptr.v[3].u = qptr.v[0].u - qptr.v[1].u + qptr.v[2].u;
		qptr.v[3].v = qptr.v[0].v - qptr.v[1].v + qptr.v[2].v;
		qptr.v[3].x = qptr.v[0].x - qptr.v[1].x + qptr.v[2].x;
		qptr.v[3].y = qptr.v[0].y - qptr.v[1].y + qptr.v[2].y;
		qptr.v[3].z = qptr.v[0].z - qptr.v[1].z + qptr.v[2].z;
		if (qfacind[face] < 0)
			qfacind[face] = qcnt;

		int vertexOffset = vertices.size / 6;
		for (i = 0; i < 4; i++) {
			vertices.addAll(qptr.v[i].x / 64.0f, qptr.v[i].y / 64.0f, qptr.v[i].z / 64.0f);
			vertices.add(NumberUtils.intToFloatColor(-1));
			vertices.addAll(qptr.v[i].u / (float) mytexx, qptr.v[i].v / (float) mytexy);
		}
		indices.addAll((short) vertexOffset, (short) (vertexOffset + 1), (short) (vertexOffset + 2),
				(short) vertexOffset, (short) (vertexOffset + 2), (short) (vertexOffset + 3));
		qcnt++;
	}

	private void vox2poly(VoxelData vox, int mip) {
		int i;
		int j;
		int x;
		int y;
		int z;
		int v;
		int ov;
		int oz = 0;
		int cnt;
		int sc;
		int x0;
		int y0;
		int dx;
		int dy;
		int[] bx0;
		int[] by0;

		xsiz = vox.xsiz[mip];
		ysiz = vox.ysiz[mip];
		zsiz = vox.zsiz[mip];

		yzsiz = ysiz * zsiz;
		int[] vbit = new int[((xsiz * yzsiz + 31) >> 3) + 1]; // vbit: 1 bit per voxel: 0=air, 1=solid
		vcol = new HashMap<Integer, Byte>();
		pow2m1 = new int[33];

		int cptr = 0;
		int zleng, ztop, z1;
		for (x = 0; x < xsiz; x++) { // Set surface voxels to 1 else 0
			for (y = 0, j = x * yzsiz; y < ysiz; y++, j += zsiz) {
				int voxptr = vox.xyoffs[mip][x][y];
				int voxend = vox.xyoffs[mip][x][y + 1];

				z1 = 0;
				while (voxptr < voxend) {
					ztop = vox.data[mip][cptr] & 0xFF;
					zleng = vox.data[mip][cptr + 1] & 0xFF;
					if ((vox.data[mip][cptr + 2] & 16) == 0)
						setzrange1(vbit, j + z1, j + ztop);
					z1 = ztop + zleng;
					setzrange1(vbit, j + ztop, j + z1);
					cptr += 3; // voxel color
					for (z = ztop; z < z1; z++) {
						if (cptr >= vox.data[mip].length)
							break;
						putvox(x, y, z, vox.data[mip][cptr++]);
					}

					voxptr += zleng + 3;
				}
			}
		}

		// x is largest dimension, y is 2nd largest dimension
		x = xsiz;
		y = ysiz;
		z = zsiz;

		if ((x < y) && (x < z))
			x = z;
		else if (y < z)
			y = z;
		if (x < y) {
			z = x;
			x = y;
			y = z;
		}
		shcntp = x;
		i = x * y;
		shcntmal = new int[i];
		shcnt = -shcntp - 1;
		gmaxx = gmaxy = garea = 0;

		if (pow2m1[32] != -1) {
			for (i = 0; i < 32; i++)
				pow2m1[i] = (1 << i) - 1;
			pow2m1[32] = -1;
		}

		for (i = 0; i < 7; i++)
			qfacind[i] = -1;

		i = ((max(ysiz, zsiz) + 1) << 2);
		bx0 = new int[i << 1];
		by0 = new int[i << 1];

		for (cnt = 0; cnt < 2; cnt++) {
			qcnt = 0;

			Arrays.fill(by0, -1);
			v = 0;

			for (i = -1; i <= 1; i += 2) // add x surfaces
				for (y = 0; y < ysiz; y++)
					for (x = 0; x <= xsiz; x++)
						for (z = 0; z <= zsiz; z++) {
							ov = v;
							v = (isolid(vbit, x, y, z) != 0 && (isolid(vbit, x, y + i, z) == 0)) ? 1 : 0;
							if ((by0[z] >= 0) && ((by0[z] != oz) || (v >= ov))) {
								daquad(cnt, bx0[z], y, by0[z], x, y, by0[z], x, y, z, (i >= 0) ? 1 : 0);
								by0[z] = -1;
							}
							if (v > ov)
								oz = z;
							else if ((v < ov) && (by0[z] != oz)) {
								bx0[z] = x;
								by0[z] = oz;
							}
						}

			for (i = -1; i <= 1; i += 2) // add z surfaces
				for (z = 0; z < zsiz; z++)
					for (x = 0; x <= xsiz; x++)
						for (y = 0; y <= ysiz; y++) {
							ov = v;
							v = (isolid(vbit, x, y, z) != 0 && (isolid(vbit, x, y, z - i) == 0)) ? 1 : 0;
							if ((by0[y] >= 0) && ((by0[y] != oz) || (v >= ov))) {
								daquad(cnt, bx0[y], by0[y], z, x, by0[y], z, x, y, z, ((i >= 0) ? 1 : 0) + 2);
								by0[y] = -1;
							}
							if (v > ov)
								oz = y;
							else if ((v < ov) && (by0[y] != oz)) {
								bx0[y] = x;
								by0[y] = oz;
							}
						}

			for (i = -1; i <= 1; i += 2) // add y surfaces
				for (x = 0; x < xsiz; x++)
					for (y = 0; y <= ysiz; y++)
						for (z = 0; z <= zsiz; z++) {
							ov = v;
							v = (isolid(vbit, x, y, z) != 0 && (isolid(vbit, x - i, y, z) == 0)) ? 1 : 0;
							if ((by0[z] >= 0) && ((by0[z] != oz) || (v >= ov))) {
								daquad(cnt, x, bx0[z], by0[z], x, y, by0[z], x, y, z, ((i >= 0) ? 1 : 0) + 4);
								by0[z] = -1;
							}
							if (v > ov)
								oz = z;
							else if ((v < ov) && (by0[z] != oz)) {
								bx0[z] = y;
								by0[z] = oz;
							}
						}

			if (cnt == 0) {
				shp = new Vector2[qcnt];
				for (int vc = 0; vc < qcnt; vc++)
					shp[vc] = new Vector2();

				sc = 0;
				for (y = gmaxy; y != 0; y--)
					for (x = gmaxx; x >= y; x--) {
						i = shcntmal[shcnt + y * shcntp + x];
						shcntmal[shcnt + y * shcntp + x] = sc; // shcnt changes from counter to head index
						for (; i > 0; i--) {
							shp[sc].x = x;
							shp[sc].y = y;
							sc++;
						}
					}

				for (mytexx = 32; mytexx < gmaxx; mytexx <<= 1)
					;
				for (mytexy = 32; mytexy < gmaxy; mytexy <<= 1)
					;

				while (mytexx * mytexy * 8 < garea * 9) // This should be sufficient to fit most skins...
					if (mytexx <= mytexy)
						mytexx <<= 1;
					else
						mytexy <<= 1;

				mytexo5 = (mytexx >> 5);
				i = (((mytexx * mytexy + 31) >> 5) << 2);
				zbit = new int[i];
				v = mytexx * mytexy;

				skindidntfit: for (z = 0; z < sc; z++) {
					dx = (int) shp[z].x;
					dy = (int) shp[z].y;
					i = v;
					do {
						int a = (int) (Math.random() * 32767);
						int b = (int) (Math.random() * 32767);

						x0 = ((a * (mytexx + 1 - dx)) >> 15);
						y0 = ((b * (mytexy + 1 - dy)) >> 15);

						if (--i < 0) // Time-out! Very slow if this happens... but at least it still works :P
						{
							Arrays.fill(zbit, 0);
							// Re-generate shp[].x/y (box sizes) from shcnt (now head indices) for next pass
							// :/
							j = 0;
							for (y = gmaxy; y != 0; y--)
								for (x = gmaxx; x >= y; x--) {
									i = shcntmal[shcnt + y * shcntp + x];
									for (; j < i; j++) {
										shp[j].x = x0;
										shp[j].y = y0;
									}
									x0 = x;
									y0 = y;
								}
							for (; j < sc; j++) {
								shp[j].x = x0;
								shp[j].y = y0;
							}

							if (mytexx <= mytexy)
								mytexx <<= 1;
							else
								mytexy <<= 1;
							mytexo5 = (mytexx >> 5);
							i = (((mytexx * mytexy + 31) >> 5) << 2);
							v = mytexx * mytexy;
							z = -1;
							continue skindidntfit;
						}
					} while (isrectfree(x0, y0, dx, dy) == 0);
					while ((y0 != 0) && (isrectfree(x0, y0 - 1, dx, 1) != 0))
						y0--;
					while ((x0 != 0) && (isrectfree(x0 - 1, y0, 1, dy) != 0))
						x0--;
					setrect(x0, y0, dx, dy);
					shp[z].x = x0;
					shp[z].y = y0; // Overwrite size with top-left location
				}

				quad = new Rectangle[qcnt];
				for (int vx = 0; vx < qcnt; vx++)
					quad[vx] = new Rectangle();
				mytex = new byte[mytexx * mytexy];
			}
		}
	}

	private int isrectfree(int x0, int y0, int dx, int dy) {
		int i, c, m, m1, x;

		i = y0 * mytexo5 + (x0 >> 5);
		dx += x0 - 1;

		c = (dx >> 5) - (x0 >> 5);
		m = ~pow2m1[x0 & 31];
		m1 = pow2m1[(dx & 31) + 1];
		if (c == 0) {
			for (m &= m1; dy != 0; dy--, i += mytexo5)
				if ((zbit[i] & m) != 0)
					return (0);
		} else {
			for (; dy != 0; dy--, i += mytexo5) {
				if ((zbit[i] & m) != 0)
					return (0);
				for (x = 1; x < c; x++)
					if (zbit[i + x] != 0)
						return (0);
				if ((zbit[i + x] & m1) != 0)
					return (0);
			}
		}

		return (1);
	}
}
