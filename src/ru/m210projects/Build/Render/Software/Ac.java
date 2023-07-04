// A.ASM replacement using C
// Mainly by Ken Silverman, with things melded with my port by
// Jonathon Fowler (jonof@edgenetwork.org)
//
// "Build Engine & Tools" Copyright (c) 1993-1997 Ken Silverman
// Ken Silverman's official web site: "http://www.advsys.net/ken"
// See the included license file "BUILDLIC.TXT" for license info.
//
// This file has been modified from Ken Silverman's original release
// by Alexander Makarov-[M210] (m210-2007@mail.ru)

package ru.m210projects.Build.Render.Software;

import ru.m210projects.Build.Gameutils;

public class Ac implements A {

	private int transmode = 0;
	private int gbxinc, gbyinc, glogx, glogy;
	private int gshade;
	private int bpl, gpinc;
	private byte[] gtrans, gbuf, frameplace, gpal, ghlinepal, hlinepal;
	private final int[] reciptable;

	private int bzinc;
	private int asm1, asm2;
	private int hlineshade;

	public Ac(int xdim, int ydim, int[] reciptable) {
		this.reciptable = reciptable;

		this.frameplace = new byte[xdim * ydim];
	}

	// Global variable functions
	@Override
	public void setvlinebpl(int dabpl) {
		bpl = dabpl;
	}

	@Override
	public void fixtransluscence(byte[] datrans) {
		gtrans = datrans;
	}

	@Override
	public void settransnormal() {
		transmode = 0;
	}

	@Override
	public void settransreverse() {
		transmode = 1;
	}

	// Ceiling/floor horizontal line functions
	@Override
	public void sethlinesizes(int logx, int logy, byte[] bufplc) {
		glogx = logx;
		glogy = logy;
		gbuf = bufplc;
	}

	@Override
	public void setpalookupaddress(byte[] paladdr) {
		ghlinepal = paladdr;
	}

	@Override
	public void setuphlineasm4(int bxinc, int byinc) {
		gbxinc = bxinc;
		gbyinc = byinc;
	}

	@Override
	public void hlineasm4(int cnt, int skiploadincs, int paloffs, int by, int bx, int p) {
		if (skiploadincs == 0) {
			gbxinc = asm1;
			gbyinc = asm2;
		}

		byte[] remap = ghlinepal;
		int shiftx = 32 - glogx;
		int shifty = 32 - glogy;

		int xinc = gbxinc; //it affects on fps
		int yinc = gbyinc;

		try {
			for (; cnt >= 0; cnt--) {
				int index = ((bx >>> shiftx) << glogy) + (by >>> shifty);

				drawpixel(p, remap[(gbuf[index] & 0xFF) + paloffs]); //XXX
				bx -= xinc;
				by -= yinc;
				p--;
			}
		} catch (Throwable e) {
		}
	}

	// Sloped ceiling/floor vertical line functions
	@Override
	public void setupslopevlin(int logylogx, byte[] bufplc, int pinc, int bzinc) {
		glogx = (logylogx & 255);
		glogy = (logylogx >> 8);
		gbuf = bufplc;
		gpinc = pinc;
		this.bzinc = (bzinc >> 3);
	}

	@Override
	public void slopevlin(int p, byte[] pal, int slopaloffs, int cnt, int bx, int by, int x3, int y3, int[] slopalookup,
			int bz) {

		int u, v, i, index;
		int shiftx = 32 - glogx;
		int shifty = 32 - glogy;
		int inc = gpinc; //it affects on fps

		try {
			for (; cnt > 0; cnt--) {
				i = krecipasm(bz >> 6);
				bz += bzinc;
				u = bx + x3 * i;
				v = by + y3 * i;

				index = ((u >>> shiftx) << glogy) + (v >>> shifty);
				drawpixel(p, pal[(gbuf[index] & 0xFF) + slopalookup[slopaloffs]]);
				slopaloffs--;
				p += inc;
			}
		} catch (Throwable e) {
		}
	}

	private int krecipasm(int i) { // Copied from software renderer
		i = Float.floatToRawIntBits(i);
		return (reciptable[(i >> 12) & 2047] >> (((i - 0x3f800000) >> 23) & 31)) ^ (i >> 31);
	}

	// Wall,face sprite/wall sprite vertical line functions
	@Override
	public void setupvlineasm(int neglogy) {
		glogy = neglogy;
	}

	@Override
	public void vlineasm1(int vinc, byte[] pal, int shade, int cnt, int vplc, byte[] bufplc, int bufoffs, int p) {
		int pl = bpl; //it affects on fps

		try {
			for (; cnt >= 0; cnt--) {
				int index = bufoffs + (vplc >>> glogy);
				if(index >= bufplc.length)
					return;

				int ch = (bufplc[index] & 0xFF) + shade;
				drawpixel(p, pal[ch]);
				p += pl;
				vplc += vinc;
			}
		} catch (Throwable e) {
		}
	}

	@Override
	public void setupmvlineasm(int neglogy) {
		glogy = neglogy;
	}

	@Override
	public void mvlineasm1(int vinc, byte[] pal, int shade, int cnt, int vplc, byte[] bufplc, int bufoffs, int p) {
		int pl = bpl; //it affects on fps

		try {
			for (; cnt >= 0; cnt--) {
				int index = bufoffs + (vplc >>> glogy);
				if(index >= bufplc.length)
					return;

				int ch = bufplc[index] & 0xFF;
				if (ch != 255)
					drawpixel(p, pal[ch + shade]);
				p += pl;
				vplc += vinc;
			}
		} catch (Throwable e) {
		}
	}

	@Override
	public void setuptvlineasm(int neglogy) {
		glogy = neglogy;
	}

	@Override
	public void tvlineasm1(int vinc, byte[] pal, int shade, int cnt, int vplc, byte[] bufplc, int bufoffs, int p) {
		int pl = bpl; //it affects on fps

		try {
			if (transmode != 0) {
				for (; cnt >= 0; cnt--) {
					int index = bufoffs + (vplc >>> glogy);
					int ch = bufplc[index] & 0xFF;
					if (ch != 255) {
						int dacol = pal[ch + shade] & 0xFF;
						drawpixel(p, gtrans[(frameplace[p] & 0xFF) + (dacol << 8)]);
					}
					p += pl;
					vplc += vinc;
				}
			} else {
				for (; cnt >= 0; cnt--) {
					int index = bufoffs + (vplc >>> glogy);
					int ch = bufplc[index] & 0xFF;
					if (ch != 255) {
						int dacol = pal[ch + shade] & 0xFF;
						drawpixel(p, gtrans[((frameplace[p] & 0xFF) << 8) + dacol]);
					}
					p += pl;
					vplc += vinc;
				}
			}
		} catch (Throwable e) {
		}
	}

	// Floor sprite horizontal line functions

	@Override
	public void sethlineincs(int x, int y) {
		asm1 = x;
		asm2 = y;
	}

	@Override
	public void setuphline(byte[] pal, int shade) {
		hlinepal = pal;
		hlineshade = shade;
	}

	@Override
	public void msethlineshift(int logx, int logy) {
		glogx = logx;
		glogy = logy;
	}

	@Override
	public void mhline(byte[] bufplc, int bx, int cntup16, int junk, int by, int p) {
		byte[] remap = hlinepal;
		int shiftx = 32 - glogx;
		int shifty = 32 - glogy;

		int xinc = asm1; //it affects on fps
		int yinc = asm2;
		int shade = hlineshade;

		try {
			for (cntup16 >>= 16; cntup16 > 0; cntup16--) {
				int index = ((bx >>> shiftx) << glogy) + (by >>> shifty);
				int ch = bufplc[index] & 0xFF;
				if (ch != 255)
					drawpixel(p, remap[ch + shade]);

				bx += xinc;
				by += yinc;
				p++;
			}
		} catch (Throwable e) {
		}
	}

	@Override
	public void tsethlineshift(int logx, int logy) {
		glogx = logx;
		glogy = logy;
	}

	@Override
	public void thline(byte[] bufplc, int bx, int cntup16, int junk, int by, int p) {
		int dacol;
		byte[] remap = hlinepal;
		int shiftx = 32 - glogx;
		int shifty = 32 - glogy;

		int xinc = asm1; //it affects on fps
		int yinc = asm2;
		int shade = hlineshade;

		try {
			if (transmode != 0) {
				for (cntup16 >>= 16; cntup16 > 0; cntup16--) {
					int index = ((bx >>> shiftx) << glogy) + (by >>> shifty);
					int ch = bufplc[index] & 0xFF;
					if (ch != 255) {
						dacol = remap[ch + shade] & 0xFF;
						drawpixel(p, gtrans[(frameplace[p] & 0xFF) + (dacol << 8)]);
					}
					bx += xinc;
					by += yinc;
					p++;
				}
			} else {
				for (cntup16 >>= 16; cntup16 > 0; cntup16--) {
					int index = ((bx >>> shiftx) << glogy) + (by >>> shifty);
					int ch = bufplc[index] & 0xFF;
					if (ch != 255) {
						dacol = remap[ch + shade] & 0xFF;
						drawpixel(p, gtrans[((frameplace[p] & 0xFF) << 8) + dacol]);
					}
					bx += xinc;
					by += yinc;
					p++;
				}
			}
		} catch (Throwable e) {
		}
	}

	// Rotatesprite vertical line functions
	@Override
	public void setupspritevline(byte[] pal, int shade, int bxinc, int byinc, int ysiz) {
		gpal = pal;
		gshade = shade;
		gbxinc = bxinc;
		gbyinc = byinc;
		glogy = ysiz;
	}

	@Override
	public void spritevline(int bx, int by, int cnt, byte[] bufplc, int bufoffs, int p) {
		byte[] remap = gpal;
		int xinc = gbxinc; //it affects on fps
		int yinc = gbyinc;
		int pl = bpl;
		int shade = gshade;

		try {
			for (; cnt > 1; cnt--) {
				int index = bufoffs + (bx >> 16) * glogy + (by >> 16);
				drawpixel(p, remap[(bufplc[index] & 0xFF) + shade]);

				bx += xinc;
				by += yinc;
				p += pl;
			}
		} catch (Throwable e) {
		}
	}

	// Rotatesprite vertical line functions
	@Override
	public void msetupspritevline(byte[] pal, int shade, int bxinc, int byinc, int ysiz) {
		gpal = pal;
		gshade = shade;
		gbxinc = bxinc;
		gbyinc = byinc;
		glogy = ysiz;
	}

	@Override
	public void mspritevline(int bx, int by, int cnt, byte[] bufplc, int bufoffs, int p) {
		byte[] remap = gpal;
		int xinc = gbxinc; //it affects on fps
		int yinc = gbyinc;
		int pl = bpl;
		int shade = gshade;

		try {
			for (; cnt > 1; cnt--) {
				int index = bufoffs + (bx >> 16) * glogy + (by >> 16);

				int ch = bufplc[index] & 0xFF;
				if (ch != 255)
					drawpixel(p, remap[ch + shade]);

				bx += xinc;
				by += yinc;
				p += pl;
			}
		} catch (Throwable e) {
		}

	}

	@Override
	public void tsetupspritevline(byte[] pal, int shade, int bxinc, int byinc, int ysiz) {
		gpal = pal;
		gshade = shade;
		gbxinc = bxinc;
		gbyinc = byinc;
		glogy = ysiz;
	}

	@Override
	public void tspritevline(int bx, int by, int cnt, byte[] bufplc, int bufoffs, int p) {
		int dacol;
		byte[] remap = gpal;
		int xinc = gbxinc; //it affects on fps
		int yinc = gbyinc;
		int pl = bpl;
		int shade = gshade;
		try {
			if (transmode != 0) {
				for (; cnt > 1; cnt--) {
					int index = bufoffs + (bx >> 16) * glogy + (by >> 16);
					int ch = bufplc[index] & 0xFF;
					if (ch != 255) {
						dacol = remap[ch + shade] & 0xFF;
						drawpixel(p, gtrans[(frameplace[p] & 0xFF) + (dacol << 8)]);
					}
					bx += xinc;
					by += yinc;
					p += pl;
				}
			} else {
				for (; cnt > 1; cnt--) {
					int index = bufoffs + (bx >> 16) * glogy + (by >> 16);
					int ch = bufplc[index] & 0xFF;
					if (ch != 255) {
						dacol = remap[ch + shade] & 0xFF;
						drawpixel(p, gtrans[((frameplace[p] & 0xFF) << 8) + dacol]);
					}
					bx += xinc;
					by += yinc;
					p += pl;
				}
			}
		} catch (Throwable e) {
		}
	}

	@Override
	public void setupdrawslab(int dabpl, byte[] pal, int shade, int trans) {
		bpl = dabpl;
		gpal = pal;
		gshade = shade;
		transmode = trans;
	}

	@Override
	public void drawslab(int dx, int v, int dy, int vi, byte[] data, int vptr, int p) {
		int x;
		int dacol;
		byte[] remap = gpal;
		int pl = bpl;
		int shade = gshade;

		switch (transmode) {
		case 0:
			while (dy > 0) {
				for (x = 0; x < dx; x++)
					drawpixel(p + x, remap[(data[(v >>> 16) + vptr] & 0xFF) + shade]);
				p += pl;
				v += vi;
				dy--;
			}
			break;
		case 1:
			while (dy > 0) {
				for (x = 0; x < dx; x++) {
					dacol = remap[(data[(v >>> 16) + vptr] & 0xFF) + shade] & 0xFF;
					drawpixel(p + x, gtrans[(frameplace[p + x] & 0xFF) + (dacol << 8)]);
				}
				p += pl;
				v += vi;
				dy--;
			}
			break;
		case 2:
			while (dy > 0) {
				for (x = 0; x < dx; x++) {
					dacol = remap[(data[(v >>> 16) + vptr] & 0xFF) + shade] & 0xFF;
					drawpixel(p + x, gtrans[((frameplace[p + x] & 0xFF) << 8) + dacol]);
				}
				p += pl;
				v += vi;
				dy--;
			}
			break;
		}
	}

	@Override
	public void drawpixel(int ptr, byte col) {
		try { //still has crashes
			frameplace[ptr] = col;
		} catch(Exception e) {}
	}

	@Override
	public void setframeplace(byte[] newframeplace) {
		this.frameplace = newframeplace;
	}

	@Override
	public byte[] getframeplace() {
		return frameplace;
	}

	@Override
	public void clearframe(byte dacol) {
		Gameutils.fill(frameplace, dacol);
	}
}
