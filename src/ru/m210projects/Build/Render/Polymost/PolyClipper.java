/*
 * "POLYMOST" code originally written by Ken Silverman
 * Ken Silverman's official web site: "http://www.advsys.net/ken"
 * See the included license file "BUILDLIC.TXT" for license info.
 *
 * This file has been modified from Ken Silverman's original release
 * by Jonathon Fowler (jf@jonof.id.au)
 * by Alexander Makarov-[M210] (m210-2007@mail.ru)
 */

package ru.m210projects.Build.Render.Polymost;

import static java.lang.Math.abs;

public class PolyClipper {

	class vsptyp {
		double x;
		double[] cy = new double[2];
		double[] fy = new double[2];
		int n, p, tag, ctag, ftag;

		public void set(vsptyp src) {
			this.x = src.x;
			System.arraycopy(src.cy, 0, this.cy, 0, 2);
			System.arraycopy(src.fy, 0, this.fy, 0, 2);
			this.n = src.n;
			this.p = src.p;
			this.tag = src.tag;
			this.ctag = src.ctag;
			this.ftag = src.ftag;
		}
	}

	private int domostpolymethod = 0;
	private final float DOMOST_OFFSET = 0.01f;
	private int vcnt, gtag;
	private final int VSPMAX = 4096; // <- careful!
	private final vsptyp[] vsp = new vsptyp[VSPMAX];
	private final Surface[] domost = new Surface[4];
	private final double[] domost_cy = new double[2], domost_cv = new double[2];
	private final Polymost r;

	public PolyClipper(Polymost render)
	{
		for(int i = 0; i < 4; i++)
			domost[i] = new Surface();
		for (int i = 0; i < VSPMAX; i++)
			vsp[i] = new vsptyp();
		this.r = render;
	}

	public void setMethod(int method)
	{
		domostpolymethod = method;
	}

	/* Init viewport boundary (must be 4 point convex loop): // (px[0],py[0]).----.(px[1],py[1]) // / \ // / \ //(px[3],py[3]).--------------.(px[2],py[2]) */
	public void initmosts(double[] px, double[] py, int n) {
		int i, j, k, imin;

		vcnt = 1; // 0 is dummy solid node
		if (n < 3)
			return;
		imin = (px[1] < px[0]) ? 1 : 0;
		for (i = n - 1; i >= 2; i--)
			if (px[i] < px[imin])
				imin = i;

		vsp[vcnt].x = px[imin];
		vsp[vcnt].cy[0] = vsp[vcnt].fy[0] = py[imin];
		vcnt++;

		i = imin + 1;
		if (i >= n) i = 0;
		j = imin - 1;
		if (j < 0) j = n - 1;

		do {
			if (px[i] < px[j]) {
				if ((vcnt > 1) && (px[i] <= vsp[vcnt - 1].x))
					vcnt--;
				vsp[vcnt].x = px[i];
				vsp[vcnt].cy[0] = py[i];
				k = j + 1;
				if (k >= n)
					k = 0;
				vsp[vcnt].fy[0] = ((px[i] - px[k]) * (py[j] - py[k])
						/ (px[j] - px[k]) + py[k]);
				vcnt++;
				i++;
				if (i >= n)
					i = 0;
			} else if (px[j] < px[i]) {
				if ((vcnt > 1) && (px[j] <= vsp[vcnt - 1].x))
					vcnt--;
				vsp[vcnt].x = px[j];
				vsp[vcnt].fy[0] = py[j];
				k = i - 1;
				if (k < 0)
					k = n - 1;
				// (px[k],py[k])
				// (px[j],?)
				// (px[i],py[i])
				vsp[vcnt].cy[0] = ((px[j] - px[k]) * (py[i] - py[k])
						/ (px[i] - px[k]) + py[k]);
				vcnt++;
				j--;
				if (j < 0)
					j = n - 1;
			} else {
				if ((vcnt > 1) && (px[i] <= vsp[vcnt - 1].x))
					vcnt--;
				vsp[vcnt].x = px[i];
				vsp[vcnt].cy[0] = py[i];
				vsp[vcnt].fy[0] = py[j];
				vcnt++;
				i++;
				if (i >= n)
					i = 0;
				if (i == j)
					break;
				j--;
				if (j < 0)
					j = n - 1;
			}
		} while (i != j);

		if (px[i] > vsp[vcnt - 1].x) {
			vsp[vcnt].x = px[i];
			vsp[vcnt].cy[0] = vsp[vcnt].fy[0] = py[i];
			vcnt++;
		}

		for (i = 0; i < vcnt; i++) {
			vsp[i].cy[1] = vsp[i + 1].cy[0];
			vsp[i].ctag = i;
			vsp[i].fy[1] = vsp[i + 1].fy[0];
			vsp[i].ftag = i;
			vsp[i].n = i + 1;
			vsp[i].p = i - 1;
		}
		vsp[vcnt - 1].n = 0;
		vsp[0].p = vcnt - 1;
		gtag = vcnt;

		// VSPMAX-1 is dummy empty node
		for (i = vcnt; i < VSPMAX; i++) {
			vsp[i].n = i + 1;
			vsp[i].p = i - 1;
		}

		vsp[VSPMAX - 1].n = vcnt;
		vsp[vcnt].p = VSPMAX - 1;
	}

	public void domost(double x0, double y0, double x1, double y1) {
		double d, f, n, t, slop, dx, dx0, dx1, nx, nx0, ny0, nx1, ny1;
		int i, j, k, z, ni, vcnt = 0, scnt, newi;

		boolean dir = (x0 < x1);

		if (dir) //clip dmost (floor)
	    {
	        y0 -= DOMOST_OFFSET;
	        y1 -= DOMOST_OFFSET;
	    }
		else //clip umost (ceiling)
		{
	        if (x0 == x1) return;
	        f = x0;
			x0 = x1;
			x1 = f;
			f = y0;
			y0 = y1;
			y1 = f;

	        y0 += DOMOST_OFFSET;
	        y1 += DOMOST_OFFSET; //necessary?
		}

		slop = (y1 - y0) / (x1 - x0);
		for (i = vsp[0].n; i != 0; i = newi) {
			newi = vsp[i].n;
			nx0 = vsp[i].x;
			nx1 = vsp[newi].x;
			if ((x0 >= nx1) || (nx0 >= x1) || (vsp[i].ctag <= 0))
				continue;
			dx = nx1 - nx0;
			domost_cy[0] = vsp[i].cy[0];
			domost_cv[0] = vsp[i].cy[1] - domost_cy[0];
			domost_cy[1] = vsp[i].fy[0];
			domost_cv[1] = vsp[i].fy[1] - domost_cy[1];

			scnt = 0;

			// Test if left edge requires split (x0,y0) (nx0,cy(0)),<dx,cv(0)>
			if ((x0 > nx0) && (x0 < nx1)) {
				t = (x0 - nx0) * domost_cv[dir?1:0] - (y0 - domost_cy[dir?1:0]) * dx;
				if (((!dir) && (t < 0)) || ((dir) && (t > 0))) {
					domost[scnt].spx = (float) x0;
					domost[scnt].spt = -1;
					scnt++;
				}
			}

			// Test for intersection on umost (j == 0) and dmost (j == 1)
			for (j = 0; j < 2; j++) {
				d = (y0 - y1) * dx - (x0 - x1) * domost_cv[j];
				n = (y0 - domost_cy[j]) * dx - (x0 - nx0) * domost_cv[j];
				if ((abs(n) <= abs(d)) && (d * n >= 0) && (d != 0)) {
					t = n / d;
					nx = (x1 - x0) * t + x0;
					if ((nx > nx0) && (nx < nx1)) {
						domost[scnt].spx = (float) nx;
						domost[scnt].spt = j;
						scnt++;
					}
				}
			}

			// Nice hack to avoid full sort later :)
			if ((scnt >= 2) && (domost[scnt - 1].spx < domost[scnt - 2].spx)) {
				f = domost[scnt - 1].spx;
				domost[scnt - 1].spx = domost[scnt - 2].spx;
				domost[scnt - 2].spx = (float) f;
				j = domost[scnt - 1].spt;
				domost[scnt - 1].spt = domost[scnt - 2].spt;
				domost[scnt - 2].spt = j;
			}

			// Test if right edge requires split
			if ((x1 > nx0) && (x1 < nx1)) {
				t = (x1 - nx0) * domost_cv[dir?1:0] - (y1 - domost_cy[dir?1:0]) * dx;
				if (((!dir) && (t < 0)) || ((dir) && (t > 0))) {
					domost[scnt].spx = (float) x1;
					domost[scnt].spt = -1;
					scnt++;
				}
			}

			vsp[i].tag = vsp[newi].tag = -1;
			for (z = 0; z <= scnt; z++, i = vcnt) {
				if (z < scnt) {
					vcnt = vsinsaft(i);
					t = (domost[z].spx - nx0) / dx;
					vsp[i].cy[1] = t * domost_cv[0] + domost_cy[0];
					vsp[i].fy[1] = t * domost_cv[1] + domost_cy[1];
					vsp[vcnt].x = domost[z].spx;
					vsp[vcnt].cy[0] = vsp[i].cy[1];
					vsp[vcnt].fy[0] = vsp[i].fy[1];
					vsp[vcnt].tag = domost[z].spt;
				}

				ni = vsp[i].n;
				if (ni == 0)
					continue; // this 'if' fixes many bugs!
				dx0 = vsp[i].x;
				if (x0 > dx0)
					continue;
				dx1 = vsp[ni].x;
				if (x1 < dx1)
					continue;
				ny0 = (dx0 - x0) * slop + y0;
				ny1 = (dx1 - x0) * slop + y0;

				// dx0 dx1
				// ~ ~
				// ----------------------------
				// t0+=0 t1+=0
				// vsp[i].cy[0] vsp[i].cy[1]
				// ============================
				// t0+=1 t1+=3
				// ============================
				// vsp[i].fy[0] vsp[i].fy[1]
				// t0+=2 t1+=6
				//
				// ny0 ? ny1 ?

				k = 4;
				if ((vsp[i].tag == 0) || (ny0 <= vsp[i].cy[0]+DOMOST_OFFSET)) k--;
	            if ((vsp[i].tag == 1) || (ny0 >= vsp[i].fy[0]-DOMOST_OFFSET)) k++;
	            if ((vsp[ni].tag == 0) || (ny1 <= vsp[i].cy[1]+DOMOST_OFFSET)) k -= 3;
	            if ((vsp[ni].tag == 1) || (ny1 >= vsp[i].fy[1]-DOMOST_OFFSET)) k += 3;

				if (!dir) {
					switch (k) {
					case 1:
					case 2:
						domost[0].px = dx0;
						domost[0].py = vsp[i].cy[0];
						domost[1].px = dx1;
						domost[1].py = vsp[i].cy[1];
						domost[2].px = dx0;
						domost[2].py = ny0;
						if(domostpolymethod != -1) {
							vsp[i].cy[0] = ny0;
							vsp[i].ctag = gtag;
							r.drawpoly(domost, 3, domostpolymethod);
						}
						break;
					case 3:
					case 6:
						domost[0].px = dx0;
						domost[0].py = vsp[i].cy[0];
						domost[1].px = dx1;
						domost[1].py = vsp[i].cy[1];
						domost[2].px = dx1;
						domost[2].py = ny1;
						if(domostpolymethod != -1) {
							r.drawpoly(domost, 3, domostpolymethod);
							vsp[i].cy[1] = ny1;
							vsp[i].ctag = gtag;
						}
						break;
					case 4:
					case 5:
					case 7:
						domost[0].px = dx0;
						domost[0].py = vsp[i].cy[0];
						domost[1].px = dx1;
						domost[1].py = vsp[i].cy[1];
						domost[2].px = dx1;
						domost[2].py = ny1;
						domost[3].px = dx0;
						domost[3].py = ny0;
						if(domostpolymethod != -1) {
							vsp[i].cy[0] = ny0;
							vsp[i].cy[1] = ny1;
							vsp[i].ctag = gtag;
							r.drawpoly(domost, 4, domostpolymethod);
						}
						break;
					case 8:
						domost[0].px = dx0;
						domost[0].py = vsp[i].cy[0];
						domost[1].px = dx1;
						domost[1].py = vsp[i].cy[1];
						domost[2].px = dx1;
						domost[2].py = vsp[i].fy[1];
						domost[3].px = dx0;
						domost[3].py = vsp[i].fy[0];
						if(domostpolymethod != -1) {
							vsp[i].ctag = vsp[i].ftag = -1;
							r.drawpoly(domost, 4, domostpolymethod);
						}
						break;
					default:
						break;
					}
				} else {
					switch (k) {
					case 7:
					case 6:
						domost[0].px = dx0;
						domost[0].py = ny0;
						domost[1].px = dx1;
						domost[1].py = vsp[i].fy[1];
						domost[2].px = dx0;
						domost[2].py = vsp[i].fy[0];
						if(domostpolymethod != -1) {
							vsp[i].fy[0] = ny0;
							vsp[i].ftag = gtag;
							r.drawpoly(domost, 3, domostpolymethod);
						}
						break;
					case 5:
					case 2:
						domost[0].px = dx0;
						domost[0].py = vsp[i].fy[0];
						domost[1].px = dx1;
						domost[1].py = ny1;
						domost[2].px = dx1;
						domost[2].py = vsp[i].fy[1];
						if(domostpolymethod != -1) {
							vsp[i].fy[1] = ny1;
							vsp[i].ftag = gtag;
							r.drawpoly(domost, 3, domostpolymethod);
						}
						break;
					case 4:
					case 3:
					case 1:
						domost[0].px = dx0;
						domost[0].py = ny0;
						domost[1].px = dx1;
						domost[1].py = ny1;
						domost[2].px = dx1;
						domost[2].py = vsp[i].fy[1];
						domost[3].px = dx0;
						domost[3].py = vsp[i].fy[0];
						if(domostpolymethod != -1) {
							vsp[i].fy[0] = ny0;
							vsp[i].fy[1] = ny1;
							vsp[i].ftag = gtag;
							r.drawpoly(domost, 4, domostpolymethod);
						}
						break;
					case 0:
						domost[0].px = dx0;
						domost[0].py = vsp[i].cy[0];
						domost[1].px = dx1;
						domost[1].py = vsp[i].cy[1];
						domost[2].px = dx1;
						domost[2].py = vsp[i].fy[1];
						domost[3].px = dx0;
						domost[3].py = vsp[i].fy[0];
						if(domostpolymethod != -1) {
							vsp[i].ctag = vsp[i].ftag = -1;
							r.drawpoly(domost, 4, domostpolymethod);
						}
						break;
					default:
						break;
					}
				}
			}
		}

		gtag++;

		// Combine neighboring vertical strips with matching collinear
		// top&bottom edges
		// This prevents x-splits from propagating through the entire scan

		i = vsp[0].n;
		while (i != 0) {
			ni = vsp[i].n;
			if ((vsp[i].cy[0] >= vsp[i].fy[0])
					&& (vsp[i].cy[1] >= vsp[i].fy[1])) {
				vsp[i].ctag = vsp[i].ftag = -1;
			}
			if ((vsp[i].ctag == vsp[ni].ctag) && (vsp[i].ftag == vsp[ni].ftag)) {
				vsp[i].cy[1] = vsp[ni].cy[1];
				vsp[i].fy[1] = vsp[ni].fy[1];
				vsdel(ni);
			} else
				i = ni;
		}
	}

	public int testvisiblemost(double x0, double x1) {
		int i, newi;
		for (i = vsp[0].n; i != 0; i = newi) {
			newi = vsp[i].n;
			if ((x0 < vsp[newi].x) && (vsp[i].x < x1) && (vsp[i].ctag >= 0))
				return (1);
		}
		return (0);
	}

	private void vsdel(int i) {
		int pi, ni;
		// Delete i
		pi = vsp[i].p;
		ni = vsp[i].n;
		vsp[ni].p = pi;
		vsp[pi].n = ni;

		// Add i to empty list
		vsp[i].n = vsp[VSPMAX - 1].n;
		vsp[i].p = VSPMAX - 1;
		vsp[vsp[VSPMAX - 1].n].p = i;
		vsp[VSPMAX - 1].n = i;
	}

	private int vsinsaft(int i) {
		int r;
		// i = next element from empty list
		r = vsp[VSPMAX - 1].n;
		vsp[vsp[r].n].p = VSPMAX - 1;
		vsp[VSPMAX - 1].n = vsp[r].n;

		vsp[r].set(vsp[i]); // copy i to r

		// insert r after i
		vsp[r].p = i;
		vsp[r].n = vsp[i].n;
		vsp[vsp[i].n].p = r;
		vsp[i].n = r;

		return (r);
	}

}
