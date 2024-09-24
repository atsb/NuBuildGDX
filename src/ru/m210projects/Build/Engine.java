// "Build Engine & Tools" Copyright (c) 1993-1997 Ken Silverman
// Ken Silverman's official web site: "http://www.advsys.net/ken"
// See the included license file "BUILDLIC.TXT" for license info.
//
// This file has been modified from Ken Silverman's original release
// by Jonathon Fowler (jf@jonof.id.au)
// by the EDuke32 team (development@voidpoint.com)
// by Alexander Makarov-[M210] (m210-2007@mail.ru)

package ru.m210projects.Build;

import static java.lang.Math.abs;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;
import static ru.m210projects.Build.Gameutils.BClampAngle;
import static ru.m210projects.Build.Gameutils.BClipRange;
import static ru.m210projects.Build.Gameutils.BCosAngle;
import static ru.m210projects.Build.Gameutils.BSinAngle;
import static ru.m210projects.Build.Gameutils.isCorruptWall;
import static ru.m210projects.Build.Gameutils.isValidSector;
import static ru.m210projects.Build.Gameutils.isValidWall;
import static ru.m210projects.Build.Net.Mmulti.uninitmultiplayer;
import static ru.m210projects.Build.OnSceenDisplay.Console.OSDTEXT_YELLOW;
import static ru.m210projects.Build.Pragmas.divscale;
import static ru.m210projects.Build.Pragmas.dmulscale;
import static ru.m210projects.Build.Pragmas.klabs;
import static ru.m210projects.Build.Pragmas.ksgn;
import static ru.m210projects.Build.Pragmas.mulscale;
import static ru.m210projects.Build.Pragmas.scale;
import static ru.m210projects.Build.Strhandler.buildString;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Graphics.DisplayMode;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.PixmapIO;

import ru.m210projects.Build.Architecture.BuildFrame.FrameType;
import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.FileHandle.Compat.Path;
import ru.m210projects.Build.FileHandle.DirectoryEntry;
import ru.m210projects.Build.FileHandle.FileResource;
import ru.m210projects.Build.FileHandle.Resource;
import ru.m210projects.Build.FileHandle.Resource.Whence;
import ru.m210projects.Build.Input.KeyInput;
import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Build.OnSceenDisplay.DEFOSDFUNC;


import ru.m210projects.Build.Render.Renderer;
import ru.m210projects.Build.Render.Renderer.RenderType;
import ru.m210projects.Build.Render.Software.Software;
import ru.m210projects.Build.Render.TextureHandle.TileData.PixelFormat;


import ru.m210projects.Build.Script.DefScript;
import ru.m210projects.Build.Settings.BuildSettings;
import ru.m210projects.Build.Types.BuildPos;
import ru.m210projects.Build.Types.Hitscan;
import ru.m210projects.Build.Types.InvalidVersionException;
import ru.m210projects.Build.Types.Neartag;
import ru.m210projects.Build.Types.Palette;
import ru.m210projects.Build.Types.SECTOR;
import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Build.Types.SmallTextFont;
import ru.m210projects.Build.Types.TextFont;
import ru.m210projects.Build.Types.Tile;
import ru.m210projects.Build.Types.TileFont;
import ru.m210projects.Build.Types.WALL;

public abstract class Engine {

	/*
	 * TODO:
	 * Add try/catch to findsaves, load, save in each game
	 * Software renderer: and the draw distance for voxel detail is really low
	 * Software renderer: You might want to look at wall sprites. I noticed a lot of them clipping through geometry in classic render
	 * Software renderer: Voxel is not clipped by ceiling geometry
	 *
	 * osdrows в сохранения конфига
	 * Туман зависит от разрешения экрана (Polymost)
	 * History list for last used IP's (client could be better for multiplayer) or copy paste IP if possible.
	 * brz фильтр
	 * broadcast
	 * Some sort of anti-aliasing option. The NVidia control panel's anti-aliasing works, but it introduces artifacting on voxels.
	 * бинд в консоль
	 * плохой перенос строк в консоли если строк больше 2х
	 * оптимизировать Bsprintf
	 * сохранения в папку (почему то не находит файл)
	 * 2д карта подглюкивает вылазиют
	 * полигоны за скайбокс потолок
	 * floor-alignment voxels for maphack
	 *
	 * Architecture:
	 * 	Engine
	 * 		messages
	 * 		filecache
	 * 		filegroup		-> 		filecache
	 * 		(net) mmulti
	 * 		audiomanger (BAudio)
	 * 			midimodule
	 * 			openal
	 * 		renderer
	 * 			polymost	->    texturecache
	 * 			software
	 * 		input
	 * 			keyboard -> input()
	 * 			gamepad	-> gpmanager
	 *
	 * 		OnScreenDisplay
	 * 			Console
	 * 		loader
	 * 			md2
	 * 			md3
	 * 			vox
	 * 			wav
	 *
	 *
	 *  Utils
	 *  	string handler
	 *  	def loader + common + scriptfile	-> texturecache / mdloader
	 *  	pragmas
	 *  	bithandler
	 */

	public static final String version = "23.071"; // XX. - year, XX - month, X - build

	public static final byte CEIL = 0;
	public static final byte FLOOR = 1;

	protected static class Line {
		public int x1, y1, x2, y2;
	}

	protected static class Clip {
		private int x, y, z;
		private short num;

		public int getX() {
			return x;
		}

		public int getY() {
			return y;
		}

		public int getZ() {
			return z;
		}

		public short getNum() {
			return num;
		}

		public Clip set(int x, int y, int z, short num) {
			this.x = x;
			this.y = y;
			this.z = z;
			this.num = num;
			return this;
		}
	}

	public static class Point {
		private int x, y, z;

		public int getX() {
			return x;
		}

		public int getY() {
			return y;
		}

		public int getZ() {
			return z;
		}

		public Point set(int x, int y, int z) {
			this.x = x;
			this.y = y;
			this.z = z;

			return this;
		}

		public Point set(int x, int y) {
			this.x = x;
			this.y = y;
			this.z = 0;

			return this;
		}

		public boolean equals(int x, int y) {
			return this.x == x && this.y == y;
		}

		public boolean equals(int x, int y, int z) {
			return this.x == x && this.y == y && this.z == z;
		}
	}

	public String tilesPath = "tilesXXX.art";
	public int fpscol = 31;

	public Renderer render;

	public static Object lock = new Object();
	private static KeyInput input;

	public static TileFont pTextfont, pSmallTextfont;

	public static boolean offscreenrendering;

	public static float TRANSLUSCENT1 = 0.66f;
	public static float TRANSLUSCENT2 = 0.33f;
	public static float MAXDRUNKANGLE = 2.5f;

	public static int setviewcnt = 0; // interface layers use this now
	public static int[] bakwindowx1, bakwindowy1;
	public static int[] bakwindowx2, bakwindowy2;
	public static int baktile;

	public static final int CLIPMASK0 = (((1) << 16) + 1);
	public static final int CLIPMASK1 = (((256) << 16) + 64);
	public static final int MAXPSKYTILES = 256;
	public static final int MAXPALOOKUPS = 256;
	public static int USERTILES = 256;
	public static int MAXTILES = 9216 + USERTILES;
	public static final int MAXSTATUS = 1024;
	public static final int DETAILPAL = (MAXPALOOKUPS - 1);
	public static final int GLOWPAL = (MAXPALOOKUPS - 2);
	public static final int SPECULARPAL = (MAXPALOOKUPS - 3);
	public static final int NORMALPAL = (MAXPALOOKUPS - 4);
	public static final int RESERVEDPALS = 4; // don't forget to increment this when adding reserved pals
	public static final int MAXSECTORSV8 = 4096;
	public static final int MAXWALLSV8 = 16384;
	public static final int MAXSPRITESV8 = 16384;
	public static final int MAXSECTORSV7 = 1024;
	public static final int MAXWALLSV7 = 8192;
	public static final int MAXSPRITESV7 = 4096;
	public static int MAXSECTORS = MAXSECTORSV7;
	public static int MAXWALLS = MAXWALLSV7;
	public static int MAXSPRITES = MAXSPRITESV7;
	public static final int MAXSPRITESONSCREEN = 1024;
	public static final int MAXVOXELS = MAXSPRITES;
//	public static final int EXTRATILES = (MAXTILES / 8);
	public static final int MAXUNIQHUDID = 256; // Extra slots so HUD models can store animation state without messing
												// game sprites
	public static final int MAXPSKYMULTIS = 8;
	public static final int MAXPLAYERS = 16;
	public static final int MAXXDIM = 4096;
	public static final int MAXYDIM = 3072;
	public static short numshades;
	public static byte[] palette;
	public static short numsectors, numwalls, numsprites;
	public static int totalclock;
	public static short[] pskyoff;
	public static short[] zeropskyoff;
	public static short pskybits;
//	public static Spriteext[] spriteext;
	public static byte parallaxtype;
	public static boolean showinvisibility;
	public static int visibility, parallaxvisibility;
	public static int parallaxyoffs, parallaxyscale;
	public static byte[][] palookup;
	public static byte[][] palookupfog;
	public static int timerticspersec;
	public static short[] sintable;
	public static byte automapping;
	public static int numtiles;
	public static byte[] show2dsector;
	public static byte[] show2dwall;
	public static byte[] show2dsprite;

	public static SECTOR[] sector;
	public static WALL[] wall;
	public static SPRITE[] sprite;
	public static SPRITE[] tsprite;
	protected Tile[] tiles;

	public static short[] headspritesect, headspritestat;
	public static short[] prevspritesect, prevspritestat;
	public static short[] nextspritesect, nextspritestat;
	private final char[] fpsbuffer = new char[32];
	private long fpstime = 0;
	private int fpsx, fpsy;

	public static byte[] gotpic;
	public static byte[] gotsector;
	public static int spritesortcnt;
	public static int windowx1, windowy1, windowx2, windowy2;
	public static int xdim, ydim;
	public static int yxaspect, viewingrange;

	// OUTPUT VALUES
	public static int mirrorx, mirrory;
	public static float mirrorang;

	public static Point intersect;
	public static Point keep;
	public static Clip ray;

	protected static int[] zofslope;
	private float fovFactor = 1.0f;

	public static int rayx = 0;
	public static int rayy = 0;
	public static Hitscan pHitInfo;
	public static Neartag neartag;

	public static int paletteloaded = 0;
	public static int tablesloaded = 0;
	protected static byte[][] britable; // JBF 20040207: full 8bit precision
	public static int curbrightness = 0;
	public static int[] picsiz;
	public static int xdimen = -1, halfxdimen, xdimenscale, xdimscale;
	public static int wx1, wy1, wx2, wy2, ydimen;
	public static final short[] pow2char = { 1, 2, 4, 8, 16, 32, 64, 128 };
	public static final int[] pow2long = { 1, 2, 4, 8, 16, 32, 64, 128, 256, 512, 1024, 2048, 4096, 8192, 16384, 32768,
			65536, 131072, 262144, 524288, 1048576, 2097152, 4194304, 8388608, 16777216, 33554432, 67108864, 134217728,
			268435456, 536870912, 1073741824, 2147483647, };

	public static Palette curpalette;

	public static int clipmoveboxtracenum = 3;
	public static int hitscangoalx = (1 << 29) - 1, hitscangoaly = (1 << 29) - 1;
	public static int globalposx, globalposy, globalposz; // polymost
	public static float globalhoriz, globalang;
	public static float pitch;
	public static short globalcursectnum;
	public static int globalvisibility;
	public static int globalshade, globalpal, cosglobalang, singlobalang;
	public static int cosviewingrangeglobalang, sinviewingrangeglobalang;
	public static int beforedrawrooms = 1;
	public static int xyaspect, viewingrangerecip;
	public static boolean inpreparemirror = false;
	public static byte[] textfont;
	public static byte[] smalltextfont;

	private byte[] sectbitmap;
	protected int timerfreq;
	protected long timerlastsample;

	private final int newaspect_enable = 1;
	private int setaspect_new_use_dimen;

	private final char[] artfilename = new char[12];
//	private int artsize = 0;
	public int numtilefiles;
	public static Resource artfil = null;
	public int artfilnum;
	public int artfilplc;
	protected int[] tilefilenum;
	protected int[] tilefileoffs;
	protected int artversion;
	protected int mapversion;
	protected int totalclocklock;
	protected short[] sqrtable;
	protected short[] shlookup;
	private final int hitallsprites = 0;
	private final int MAXCLIPNUM = 1024;
	protected final int MAXCLIPDIST = 1024;

//	private int[] lookups;
	protected short clipnum;

	protected int[] rxi;
	protected int[] ryi;
	protected short[] hitwalls;

	protected Line[] clipit;
	protected short[] clipsectorlist;
	protected short clipsectnum;
	protected int[] clipobjectval;

	private int[] rdist, gdist, bdist;
	private final int FASTPALGRIDSIZ = 8;

	private final byte[] shortbuf = new byte[2];

	private byte[] colhere;
	private byte[] colhead;
	private short[] colnext;
	private final byte[] coldist = { 0, 1, 2, 3, 4, 3, 2, 1 };
	private int[] colscan;
	protected int randomseed;

	public static short[] radarang;
	public static byte[] transluc;

	// Engine.c

	public int getpalookup(int davis, int dashade) // jfBuild
	{
		return (min(max(dashade + (davis >> 8), 0), numshades - 1));
	}

	public int animateoffs(int tilenum, int nInfo) { // jfBuild + gdxBuild
		int clock, index = 0;

		int speed = getTile(tilenum).getSpeed();
		if ((nInfo & 0xC000) == 0x8000) { // sprite
			// hash sprite frame by info variable

			shortbuf[0] = (byte) ((nInfo) & 0xFF);
			shortbuf[1] = (byte) ((nInfo >>> 8) & 0xFF);

			clock = (int) ((totalclocklock + CRC32.getChecksum(shortbuf)) >> speed);
		} else
			clock = totalclocklock >> speed;

		int frames = getTile(tilenum).getFrames();

		if (frames > 0) {
			switch (getTile(tilenum).getType()) {
			case Oscil:
				index = clock % (frames * 2);
				if (index >= frames)
					index = frames * 2 - index;
				break;
			case Forward:
				index = clock % (frames + 1);
				break;
			case Backward:
				index = -(clock % (frames + 1));
				break;
			default: // None
				break;
			}
		}
		return index;
	}

	public void initksqrt() { // jfBuild

		sqrtable = new short[4096];
		shlookup = new short[4096 + 256];

		int i, j = 1, k = 0;
		for (i = 0; i < 4096; i++) {
			if (i >= j) {
				j <<= 2;
				k++;
			}

			sqrtable[i] = (short) ((int) sqrt(((i << 18) + 131072)) << 1);
			shlookup[i] = (short) ((k << 1) + ((10 - k) << 8));
			if (i < 256)
				shlookup[i + 4096] = (short) (((k + 6) << 1) + ((10 - (k + 6)) << 8));
		}
	}

	protected void calcbritable() { // jfBuild
		britable = new byte[16][256];

		float a, b;
		for (int i = 0, j; i < 16; i++) {
			a = 8.0f / (i + 8);
			b = (float) (255.0f / pow(255.0f, a));
			for (j = 0; j < 256; j++) {// JBF 20040207: full 8bit precision
				britable[i][j] = (byte) (pow(j, a) * b);
			}
		}
	}

	public void loadtables() throws Exception { // jfBuild
		if (tablesloaded == 0) {
			initksqrt();

			sintable = new short[2048];
			textfont = new byte[2048];
			smalltextfont = new byte[2048];
			radarang = new short[1280]; // 1024

			Resource res = BuildGdx.cache.open("tables.dat", 0);
			if (res != null) {
				byte[] buf = new byte[2048 * 2];

				res.read(buf);
				ByteBuffer.wrap(buf).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(sintable);

				buf = new byte[640 * 2];
				res.read(buf);
				ByteBuffer.wrap(buf).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(radarang, 0, 640);
				for (int i = 0; i < 640; i++)
					radarang[1279 - i] = (short) -radarang[i];

				res.read(textfont, 0, 1024);
				res.read(smalltextfont, 0, 1024);

				pTextfont = new TextFont();
				pSmallTextfont = new SmallTextFont();

				/* kread(fil, britable, 1024); */

				calcbritable();
				res.close();
			} else
				throw new Exception("ERROR: Failed to load TABLES.DAT!");

			tablesloaded = 1;
		}
	}

	public void initfastcolorlookup(int rscale, int gscale, int bscale) { // jfBuild
		int i, x, y, z;

		int j = 0;
		for (i = 64; i >= 0; i--) {
			rdist[i] = rdist[128 - i] = j * rscale;
			gdist[i] = gdist[128 - i] = j * gscale;
			bdist[i] = bdist[128 - i] = j * bscale;
			j += 129 - (i << 1);
		}

		Arrays.fill(colhere, (byte) 0);
		Arrays.fill(colhead, (byte) 0);

		int pal1 = 768 - 3;
		for (i = 255; i >= 0; i--, pal1 -= 3) {
			int r = palette[pal1] & 0xFF;
			int g = palette[pal1 + 1] & 0xFF;
			int b = palette[pal1 + 2] & 0xFF;
			j = (r >> 3) * FASTPALGRIDSIZ * FASTPALGRIDSIZ + (g >> 3) * FASTPALGRIDSIZ + (b >> 3)
					+ FASTPALGRIDSIZ * FASTPALGRIDSIZ + FASTPALGRIDSIZ + 1;
			if ((colhere[j >> 3] & pow2char[j & 7]) != 0)
				colnext[i] = (short) (colhead[j] & 0xFF);
			else
				colnext[i] = -1;

			colhead[j] = (byte) i;
			colhere[j >> 3] |= pow2char[j & 7];
		}

		i = 0;
		for (x = -FASTPALGRIDSIZ * FASTPALGRIDSIZ; x <= FASTPALGRIDSIZ * FASTPALGRIDSIZ; x += FASTPALGRIDSIZ
				* FASTPALGRIDSIZ)
			for (y = -FASTPALGRIDSIZ; y <= FASTPALGRIDSIZ; y += FASTPALGRIDSIZ)
				for (z = -1; z <= 1; z++)
					colscan[i++] = x + y + z;
		i = colscan[13];
		colscan[13] = colscan[26];
		colscan[26] = i;
	}

	public void loadpalette() throws Exception // jfBuild
	{
		Resource fil;
		if (paletteloaded != 0)
			return;

		palette = new byte[768];
		curpalette = new Palette();
		palookup = new byte[MAXPALOOKUPS][];

		Console.Println("Loading palettes");
		if ((fil = BuildGdx.cache.open("palette.dat", 0)) == null)
			throw new Exception("Failed to load \"palette.dat\"!");

		fil.read(palette);

		numshades = fil.readShort();

		if (palookup[0] == null)
			palookup[0] = new byte[numshades << 8];
		if (transluc == null)
			transluc = new byte[65536];

		globalpal = 0;
		Console.Println("Loading gamma correction tables");
		fil.read(palookup[globalpal], 0, numshades << 8);
		Console.Println("Loading translucency table");

		fil.read(transluc);

		fil.close();

		initfastcolorlookup(30, 59, 11);

		paletteloaded = 1;
	}

	protected Byte[] palcache = new Byte[0x40000]; // buffer 256kb

	public byte getclosestcol(byte[] palette, int r, int g, int b) { // jfBuild
		int i, k, dist;
		byte retcol;
		int pal1;

		int j = (r >> 3) * FASTPALGRIDSIZ * FASTPALGRIDSIZ + (g >> 3) * FASTPALGRIDSIZ + (b >> 3)
				+ FASTPALGRIDSIZ * FASTPALGRIDSIZ + FASTPALGRIDSIZ + 1;

		int rgb = ((r << 12) | (g << 6) | b);

		int mindist = min(rdist[(coldist[r & 7] & 0xFF) + 64 + 8], gdist[(coldist[g & 7] & 0xFF) + 64 + 8]);
		mindist = min(mindist, bdist[(coldist[b & 7] & 0xFF) + 64 + 8]);
		mindist++;

		Byte out = palcache[rgb & (palcache.length - 1)];
		if (out != null)
			return out;

		r = 64 - r;
		g = 64 - g;
		b = 64 - b;

		retcol = -1;
		for (k = 26; k >= 0; k--) {
			i = colscan[k] + j;
			if ((colhere[i >> 3] & pow2char[i & 7]) == 0)
				continue;

			i = colhead[i] & 0xFF;
			do {
				pal1 = i * 3;
				dist = gdist[(palette[pal1 + 1] & 0xFF) + g];
				if (dist < mindist) {
					dist += rdist[(palette[pal1] & 0xFF) + r];
					if (dist < mindist) {
						dist += bdist[(palette[pal1 + 2] & 0xFF) + b];
						if (dist < mindist) {
							mindist = dist;
							retcol = (byte) i;
						}
					}
				}
				i = colnext[i];
			} while (i >= 0);
		}
		if (retcol >= 0) {
			palcache[rgb & (palcache.length - 1)] = retcol;
			return retcol;
		}

		mindist = 0x7fffffff;
		for (i = 255; i >= 0; i--) {
			pal1 = i * 3;
			dist = gdist[(palette[pal1 + 1] & 0xFF) + g];
			if (dist >= mindist)
				continue;

			dist += rdist[(palette[pal1] & 0xFF) + r];
			if (dist >= mindist)
				continue;

			dist += bdist[(palette[pal1 + 2] & 0xFF) + b];
			if (dist >= mindist)
				continue;

			mindist = dist;
			retcol = (byte) i;
		}

		palcache[rgb & (palcache.length - 1)] = retcol;
		return retcol;
	}

	////////// SPRITE LIST MANIPULATION FUNCTIONS //////////

	public short insertspritesect(int sectnum) // jfBuild
	{
		if ((sectnum >= MAXSECTORS) || (headspritesect[MAXSECTORS] == -1))
			return (-1); // list full

		short blanktouse = headspritesect[MAXSECTORS];

		headspritesect[MAXSECTORS] = nextspritesect[blanktouse];
		if (headspritesect[MAXSECTORS] >= 0)
			prevspritesect[headspritesect[MAXSECTORS]] = -1;

		prevspritesect[blanktouse] = -1;
		nextspritesect[blanktouse] = headspritesect[sectnum];
		if (headspritesect[sectnum] >= 0)
			prevspritesect[headspritesect[sectnum]] = blanktouse;
		headspritesect[sectnum] = blanktouse;

		sprite[blanktouse].sectnum = (short) sectnum;

		return (blanktouse);
	}

	public short insertspritestat(int newstatnum) // jfBuild
	{
		if ((newstatnum >= MAXSTATUS) || (headspritestat[MAXSTATUS] == -1))
			return (-1); // list full

		short blanktouse = headspritestat[MAXSTATUS];

		headspritestat[MAXSTATUS] = nextspritestat[blanktouse];
		if (headspritestat[MAXSTATUS] >= 0)
			prevspritestat[headspritestat[MAXSTATUS]] = -1;

		prevspritestat[blanktouse] = -1;
		nextspritestat[blanktouse] = headspritestat[newstatnum];
		if (headspritestat[newstatnum] >= 0)
			prevspritestat[headspritestat[newstatnum]] = blanktouse;
		headspritestat[newstatnum] = blanktouse;

		sprite[blanktouse].statnum = (short) newstatnum;

		return (blanktouse);
	}

	public short insertsprite(int sectnum, int statnum) // jfBuild
	{
		insertspritestat(statnum);
		return (insertspritesect(sectnum));
	}

	public short deletesprite(int spritenum) // jfBuild
	{
		deletespritestat(spritenum);
		return (deletespritesect(spritenum));
	}

	public short changespritesect(int spritenum, int newsectnum) // jfBuild
	{
		if ((newsectnum < 0) || (newsectnum > MAXSECTORS))
			return (-1);
		if (sprite[spritenum].sectnum == newsectnum)
			return (0);
		if (sprite[spritenum].sectnum == MAXSECTORS)
			return (-1);
		if (deletespritesect((short) spritenum) < 0)
			return (-1);
		insertspritesect(newsectnum);
		return (0);
	}

	public short changespritestat(int spritenum, int newstatnum) // jfBuild
	{
		if ((newstatnum < 0) || (newstatnum > MAXSTATUS))
			return (-1);
		if (sprite[spritenum].statnum == newstatnum)
			return (0);
		if (sprite[spritenum].statnum == MAXSTATUS)
			return (-1);
		if (deletespritestat((short) spritenum) < 0)
			return (-1);
		insertspritestat(newstatnum);
		return (0);
	}

	public short deletespritesect(int spritenum) // jfBuild
	{
		if (sprite[spritenum].sectnum == MAXSECTORS)
			return (-1);

		if (headspritesect[sprite[spritenum].sectnum] == spritenum)
			headspritesect[sprite[spritenum].sectnum] = nextspritesect[spritenum];

		if (prevspritesect[spritenum] >= 0)
			nextspritesect[prevspritesect[spritenum]] = nextspritesect[spritenum];
		if (nextspritesect[spritenum] >= 0)
			prevspritesect[nextspritesect[spritenum]] = prevspritesect[spritenum];

		if (headspritesect[MAXSECTORS] >= 0)
			prevspritesect[headspritesect[MAXSECTORS]] = (short) spritenum;
		prevspritesect[spritenum] = -1;
		nextspritesect[spritenum] = headspritesect[MAXSECTORS];
		headspritesect[MAXSECTORS] = (short) spritenum;

		sprite[spritenum].sectnum = (short) MAXSECTORS;
		return (0);
	}

	public short deletespritestat(int spritenum) // jfBuild
	{
		if (sprite[spritenum].statnum == MAXSTATUS)
			return (-1);

		if (headspritestat[sprite[spritenum].statnum] == spritenum)
			headspritestat[sprite[spritenum].statnum] = nextspritestat[spritenum];

		if (prevspritestat[spritenum] >= 0)
			nextspritestat[prevspritestat[spritenum]] = nextspritestat[spritenum];
		if (nextspritestat[spritenum] >= 0)
			prevspritestat[nextspritestat[spritenum]] = prevspritestat[spritenum];

		if (headspritestat[MAXSTATUS] >= 0)
			prevspritestat[headspritestat[MAXSTATUS]] = (short) spritenum;
		prevspritestat[spritenum] = -1;
		nextspritestat[spritenum] = headspritestat[MAXSTATUS];
		headspritestat[MAXSTATUS] = (short) spritenum;

		sprite[spritenum].statnum = MAXSTATUS;
		return (0);
	}

	public Point lintersect(int x1, int y1, int z1, int x2, int y2, int z2, int x3, // jfBuild
			int y3, int x4, int y4) {

		// p1 to p2 is a line segment
		int x21 = x2 - x1, x34 = x3 - x4;
		int y21 = y2 - y1, y34 = y3 - y4;
		int bot = x21 * y34 - y21 * x34;

		if (bot == 0)
			return null;

		int x31 = x3 - x1, y31 = y3 - y1;
		int topt = x31 * y34 - y31 * x34;

		if (bot > 0) {
			if ((topt < 0) || (topt >= bot))
				return null;
			int topu = x21 * y31 - y21 * x31;
			if ((topu < 0) || (topu >= bot))
				return null;
		} else {
			if ((topt > 0) || (topt <= bot))
				return null;
			int topu = x21 * y31 - y21 * x31;
			if ((topu > 0) || (topu <= bot))
				return null;
		}
		long t = divscale(topt, bot, 24);

		intersect.x = x1 + mulscale(x21, t, 24);
		intersect.y = y1 + mulscale(y21, t, 24);
		intersect.z = z1 + mulscale(z2 - z1, t, 24);

		return intersect;
	}

	protected Point rintersect(int x1, int y1, int z1, int vx, int vy, int vz, int x3, // jfBuild
			int y3, int x4, int y4) { // p1 towards p2 is a ray
		int x34, y34, x31, y31, bot, topt, topu;

		x34 = x3 - x4;
		y34 = y3 - y4;
		bot = vx * y34 - vy * x34;
		if (bot == 0)
			return null;

		if (bot > 0) {

			x31 = x3 - x1;
			y31 = y3 - y1;
			topt = x31 * y34 - y31 * x34;
			if (topt < 0)
				return null;
			topu = vx * y31 - vy * x31;
			if ((topu < 0) || (topu >= bot))
				return null;
		} else {
			x31 = x3 - x1;
			y31 = y3 - y1;
			topt = x31 * y34 - y31 * x34;
			if (topt > 0)
				return null;
			topu = vx * y31 - vy * x31;
			if ((topu > 0) || (topu <= bot))
				return null;
		}

		long t = divscale(topt, bot, 16);
		intersect.x = x1 + mulscale(vx, t, 16);
		intersect.y = y1 + mulscale(vy, t, 16);
		intersect.z = z1 + mulscale(vz, t, 16);

		return intersect;
	}

	protected Point keepaway(int x, int y, int w) {
		int x1 = clipit[w].x1;
		int dx = clipit[w].x2 - x1;
		int y1 = clipit[w].y1;
		int dy = clipit[w].y2 - y1;
		int ox = ksgn(-dy);
		int oy = ksgn(dx);
		int first = (klabs(dx) <= klabs(dy) ? 1 : 0);

		while (true) {
			if (dx * (y - y1) > (x - x1) * dy)
				return keep.set(x, y, 0);
			if (first == 0)
				x += ox;
			else
				y += oy;
			first ^= 1;
		}
	}

	protected Clip raytrace(int x3, int y3, int x4, int y4) { // jfBuild
		int x1, y1, x2, y2, nintx, ninty, cnt;
		int x21, y21, x43, y43;
		long topu, bot;

		int rayx = x4;
		int rayy = y4;
		short hitwall = -1;

		for (short z = (short) (clipnum - 1); z >= 0; z--) {
			x1 = clipit[z].x1;
			x2 = clipit[z].x2;
			x21 = x2 - x1;
			y1 = clipit[z].y1;
			y2 = clipit[z].y2;
			y21 = y2 - y1;

			topu = x21 * (y3 - y1) - (x3 - x1) * y21;
			if (topu <= 0)
				continue;
			if (x21 * (rayy - y1) > (rayx - x1) * y21)
				continue;
			x43 = rayx - x3;
			y43 = rayy - y3;
			if (x43 * (y1 - y3) > (x1 - x3) * y43)
				continue;
			if (x43 * (y2 - y3) <= (x2 - x3) * y43)
				continue;
			bot = x43 * y21 - x21 * y43;
			if (bot == 0)
				continue;

			cnt = 256;
			do {
				cnt--;
				if (cnt < 0) {
					rayx = x3;
					rayy = y3;
					return ray.set(rayx, rayy, 0, z);
				}
				nintx = x3 + scale(x43, topu, bot);
				ninty = y3 + scale(y43, topu, bot);
				topu--;
			} while (x21 * (ninty - y1) <= (nintx - x1) * y21);

			if (klabs(x3 - nintx) + klabs(y3 - ninty) < klabs(x3 - rayx) + klabs(y3 - rayy)) {
				rayx = nintx;
				rayy = ninty;
				hitwall = z;
				ray.set(rayx, rayy, 0, hitwall);
			}
		}
		return ray.set(rayx, rayy, 0, hitwall);
	}

	//
	// Exported Engine Functions
	//

	public void InitArrays() // gdxBuild
	{
		intersect = new Point();
		keep = new Point();
		ray = new Clip();

		zofslope = new int[2];
		sectbitmap = new byte[MAXSECTORS >> 3];
		palookupfog = new byte[MAXPALOOKUPS][3];
		pskyoff = new short[MAXPSKYTILES];
		zeropskyoff = new short[MAXPSKYTILES];

		tiles = new Tile[MAXTILES];

		show2dsector = new byte[(MAXSECTORS + 7) >> 3];
		show2dwall = new byte[(MAXWALLS + 7) >> 3];
		show2dsprite = new byte[(MAXSPRITES + 7) >> 3];
		sector = new SECTOR[MAXSECTORS];
		wall = new WALL[MAXWALLS];
		sprite = new SPRITE[MAXSPRITES];
		tsprite = new SPRITE[MAXSPRITESONSCREEN + 1];
		headspritesect = new short[MAXSECTORS + 1];
		headspritestat = new short[MAXSTATUS + 1];
		prevspritesect = new short[MAXSPRITES];
		prevspritestat = new short[MAXSPRITES];
		nextspritesect = new short[MAXSPRITES];
		nextspritestat = new short[MAXSPRITES];
		gotpic = new byte[(MAXTILES + 7) >> 3];
		gotsector = new byte[(MAXSECTORS + 7) >> 3];

		pHitInfo = new Hitscan();
		neartag = new Neartag();
		picsiz = new int[MAXTILES];
		tilefilenum = new int[MAXTILES];
		tilefileoffs = new int[MAXTILES];

		rxi = new int[4];
		ryi = new int[4];
		hitwalls = new short[clipmoveboxtracenum + 1];
		clipit = new Line[MAXCLIPNUM];
		clipsectorlist = new short[MAXCLIPNUM];
		clipobjectval = new int[MAXCLIPNUM];
		rdist = new int[129];
		gdist = new int[129];
		bdist = new int[129];
		colhere = new byte[((FASTPALGRIDSIZ + 2) * (FASTPALGRIDSIZ + 2) * (FASTPALGRIDSIZ + 2)) >> 3];
		colhead = new byte[(FASTPALGRIDSIZ + 2) * (FASTPALGRIDSIZ + 2) * (FASTPALGRIDSIZ + 2)];
		colnext = new short[256];
		colscan = new int[27];

		Arrays.fill(show2dsector, (byte) 0);
		Arrays.fill(show2dsprite, (byte) 0);
		Arrays.fill(show2dwall, (byte) 0);

		bakwindowx1 = new int[4];
		bakwindowy1 = new int[4];
		bakwindowx2 = new int[4];
		bakwindowy2 = new int[4];
	}

	public Engine() throws Exception { // gdxBuild
		InitArrays();

		loadtables();

		parallaxtype = 2;
		parallaxyoffs = 0;
		parallaxyscale = 65536;
		showinvisibility = false;

		pskybits = 0;
		paletteloaded = 0;
		automapping = 0;
		totalclock = 0;
		visibility = 512;
		parallaxvisibility = 512;

		loadpalette();

		initkeys();

		Console.setFunction(new DEFOSDFUNC(this));
		randomseed = 1; // random for voxels
	}

	public void uninit() // gdxBuild
	{
		Iterator<TileFont> it;
		while ((it = TileFont.managedFont.iterator()).hasNext()) {
			TileFont font = it.next();
			font.dispose();
		}

		try {
			if (render != null && render.isInited())
				render.uninit();
		} catch (Exception e) {

		}

		if (artfil != null)
			artfil.close();

		for (int i = 0; i < MAXPALOOKUPS; i++)
			if (palookup[i] != null)
				palookup[i] = null;

		uninitmultiplayer();

		BuildGdx.audio.dispose();
		BuildGdx.message.dispose();
	}

	public void initspritelists() // jfBuild
	{
		for (int i = 0; i < MAXSECTORS; i++) // Init doubly-linked sprite sector lists
			headspritesect[i] = -1;
		headspritesect[MAXSECTORS] = 0;

		for (int i = 0; i < MAXSPRITES; i++) {
			if (sprite[i] == null)
				sprite[i] = new SPRITE();
			else
				sprite[i].reset();
			prevspritesect[i] = (short) (i - 1);
			nextspritesect[i] = (short) (i + 1);
			sprite[i].sectnum = (short) MAXSECTORS;
		}
		prevspritesect[0] = -1;
		nextspritesect[MAXSPRITES - 1] = -1;

		for (int i = 0; i < MAXSTATUS; i++) // Init doubly-linked sprite status lists
			headspritestat[i] = -1;
		headspritestat[MAXSTATUS] = 0;
		for (int i = 0; i < MAXSPRITES; i++) {
			prevspritestat[i] = (short) (i - 1);
			nextspritestat[i] = (short) (i + 1);
			sprite[i].statnum = (short) MAXSTATUS;
		}
		prevspritestat[0] = -1;
		nextspritestat[MAXSPRITES - 1] = -1;

		for (int i = 0; i < MAXSPRITESONSCREEN; i++) {
			if (tsprite[i] == null)
				tsprite[i] = new SPRITE();
			else
				tsprite[i].reset();
		}
	}

	public int drawrooms(float daposx, float daposy, float daposz, float daang, float dahoriz, short dacursectnum) { // eDuke32
		beforedrawrooms = 0;

		globalposx = (int) daposx;
		globalposy = (int) daposy;
		globalposz = (int) daposz;

		globalang = BClampAngle(daang);
		globalhoriz = (dahoriz - 100);
		pitch = (-getangle(160, (int) (dahoriz - 100))) / (2048.0f / 360.0f);

		globalcursectnum = dacursectnum;
		totalclocklock = totalclock;

		cosglobalang = (int) BCosAngle(globalang);
		singlobalang = (int) BSinAngle(globalang);

		cosviewingrangeglobalang = mulscale(cosglobalang, viewingrange, 16);
		sinviewingrangeglobalang = mulscale(singlobalang, viewingrange, 16);

		Arrays.fill(gotpic, (byte) 0);
		Arrays.fill(gotsector, (byte) 0);

		render.drawrooms();
		return 0;
	}

	public void drawmasks() { // gdxBuild
		render.drawmasks();
	}

	public void drawmapview(int dax, int day, int zoome, int ang) { // gdxBuild
		render.drawmapview(dax, day, zoome, ang);
	}

	public void drawoverheadmap(int cposx, int cposy, int czoom, short cang) { // gdxBuild
		render.drawoverheadmap(cposx, cposy, czoom, cang);
	}

	public BuildPos loadboard(String filename) throws InvalidVersionException, FileNotFoundException, RuntimeException {
		Resource fil;
		if ((fil = BuildGdx.cache.open(filename, 0)) == null) {
			mapversion = 7;
			throw new FileNotFoundException("Map " + filename + " not found!");
		}

		mapversion = fil.readInt();
		switch (mapversion) {
		case 6:
			return loadoldboard(fil);
		case 7:
			break;
		case 8:
			if (MAXSECTORS == MAXSECTORSV8)
				break;
		default:
			fil.close();
			throw new InvalidVersionException(filename + ": invalid map version( v" + mapversion + " )!");
		}

		BuildPos pos = new BuildPos();

		initspritelists();

		Arrays.fill(show2dsector, (byte) 0);
		Arrays.fill(show2dsprite, (byte) 0);
		Arrays.fill(show2dwall, (byte) 0);

		pos.x = fil.readInt();
		pos.y = fil.readInt();
		pos.z = fil.readInt();
		pos.ang = fil.readShort();
		pos.sectnum = fil.readShort();

		numsectors = fil.readShort();
		for (int i = 0; i < numsectors; i++)
			sector[i] = new SECTOR(fil);

		numwalls = fil.readShort();
		for (int w = 0; w < numwalls; w++)
			wall[w] = new WALL(fil);

		numsprites = fil.readShort();
		for (int s = 0; s < numsprites; s++)
			sprite[s].buildSprite(fil);

		for (int i = 0; i < numsprites; i++)
			insertsprite(sprite[i].sectnum, sprite[i].statnum);

		// Must be after loading sectors, etc!
		pos.sectnum = updatesector(pos.x, pos.y, pos.sectnum);

		fil.close();

		if (inside(pos.x, pos.y, pos.sectnum) == -1)
			throw new RuntimeException("Player should be in a sector!");

		return pos;
	}

	public BuildPos loadoldboard(Resource fil) throws RuntimeException {
		BuildPos pos = new BuildPos();

		initspritelists();

		Arrays.fill(show2dsector, (byte) 0);
		Arrays.fill(show2dsprite, (byte) 0);
		Arrays.fill(show2dwall, (byte) 0);

		pos.x = fil.readInt();
		pos.y = fil.readInt();
		pos.z = fil.readInt();
		pos.ang = fil.readShort();
		pos.sectnum = fil.readShort();

		numsectors = fil.readShort();
		for (int i = 0; i < numsectors; i++) {
			SECTOR sec = new SECTOR();

			sec.wallptr = fil.readShort();
			sec.wallnum = fil.readShort();
			sec.ceilingpicnum = fil.readShort();
			sec.floorpicnum = fil.readShort();
			int ceilingheinum = fil.readShort();
			sec.ceilingheinum = (short) max(min(ceilingheinum << 5, 32767), -32768);
			int floorheinum = fil.readShort();
			sec.floorheinum = (short) max(min(floorheinum << 5, 32767), -32768);
			sec.ceilingz = fil.readInt();
			sec.floorz = fil.readInt();
			sec.ceilingshade = fil.readByte();
			sec.floorshade = fil.readByte();
			sec.ceilingxpanning = (short) (fil.readByte() & 0xFF);
			sec.floorxpanning = (short) (fil.readByte() & 0xFF);
			sec.ceilingypanning = (short) (fil.readByte() & 0xFF);
			sec.floorypanning = (short) (fil.readByte() & 0xFF);
			sec.ceilingstat = fil.readByte();
			if ((sec.ceilingstat & 2) == 0)
				sec.ceilingheinum = 0;
			sec.floorstat = fil.readByte();
			if ((sec.floorstat & 2) == 0)
				sec.floorheinum = 0;
			sec.ceilingpal = fil.readByte();
			sec.floorpal = fil.readByte();
			sec.visibility = fil.readByte();
			sec.lotag = fil.readShort();
			sec.hitag = fil.readShort();
			sec.extra = fil.readShort();

			sector[i] = sec;
		}

		numwalls = fil.readShort();
		for (int w = 0; w < numwalls; w++) {
			WALL wal = new WALL();

			wal.x = fil.readInt();
			wal.y = fil.readInt();
			wal.point2 = fil.readShort();
			wal.nextsector = fil.readShort();
			wal.nextwall = fil.readShort();
			wal.picnum = fil.readShort();
			wal.overpicnum = fil.readShort();
			wal.shade = fil.readByte();
			wal.pal = (short) (fil.readByte() & 0xFF);
			wal.cstat = fil.readShort();
			wal.xrepeat = (short) (fil.readByte() & 0xFF);
			wal.yrepeat = (short) (fil.readByte() & 0xFF);
			wal.xpanning = (short) (fil.readByte() & 0xFF);
			wal.ypanning = (short) (fil.readByte() & 0xFF);
			wal.lotag = fil.readShort();
			wal.hitag = fil.readShort();
			wal.extra = fil.readShort();

			wall[w] = wal;
		}

		numsprites = fil.readShort();
		for (int s = 0; s < numsprites; s++) {
			SPRITE spr = sprite[s];

			spr.x = fil.readInt();
			spr.y = fil.readInt();
			spr.z = fil.readInt();
			spr.cstat = fil.readShort();
			spr.shade = fil.readByte();
			spr.pal = fil.readByte();
			spr.clipdist = fil.readByte();
			spr.xrepeat = (short) (fil.readByte() & 0xFF);
			spr.yrepeat = (short) (fil.readByte() & 0xFF);
			spr.xoffset = (short) (fil.readByte() & 0xFF);
			spr.yoffset = (short) (fil.readByte() & 0xFF);
			spr.picnum = fil.readShort();
			spr.ang = fil.readShort();
			spr.xvel = fil.readShort();
			spr.yvel = fil.readShort();
			spr.zvel = fil.readShort();
			spr.owner = fil.readShort();
			spr.sectnum = fil.readShort();
			spr.statnum = fil.readShort();
			spr.lotag = fil.readShort();
			spr.hitag = fil.readShort();
			spr.extra = fil.readShort();
		}

		for (int i = 0; i < numsprites; i++)
			insertsprite(sprite[i].sectnum, sprite[i].statnum);

		// Must be after loading sectors, etc!
		pos.sectnum = updatesector(pos.x, pos.y, pos.sectnum);

		fil.close();

		if (inside(pos.x, pos.y, pos.sectnum) == -1)
			throw new RuntimeException("Player should be in a sector!");

		return pos;
	}

	public void saveboard(FileResource fil, int daposx, int daposy, int daposz, int daang, int dacursectnum) {
		fil.writeInt(7); // mapversion

		fil.writeInt(daposx);
		fil.writeInt(daposy);
		fil.writeInt(daposz);
		fil.writeShort(daang);
		fil.writeShort(dacursectnum);

		fil.writeShort(numsectors);
		for (int s = 0; s < numsectors; s++)
			fil.writeBytes(sector[s].getBytes());

		fil.writeShort(numwalls);
		for (int s = 0; s < numwalls; s++)
			fil.writeBytes(wall[s].getBytes());

		fil.writeShort(numsprites);
		for (int s = 0; s < numsprites; s++)
			fil.writeBytes(sprite[s].getBytes());
	}

	// JBF: davidoption now functions as a windowed-mode flag (0 == windowed, 1 ==
	// fullscreen)
	public boolean setgamemode(int davidoption, int daxdim, int daydim) { // jfBuild + gdxBuild
		if (BuildGdx.app.getType() == ApplicationType.Android) {
			daxdim = BuildGdx.graphics.getWidth();
			daydim = BuildGdx.graphics.getHeight();
			davidoption = 0;
		}

		daxdim = max(320, daxdim);
		daydim = max(200, daydim);

		if (render.isInited()
				&& ((davidoption == (BuildGdx.graphics.isFullscreen() ? 1 : 0))
						&& (BuildGdx.graphics.getWidth() == daxdim) && (BuildGdx.graphics.getHeight() == daydim))
				&& xdim == daxdim && ydim == daydim)
			return true;

		xdim = daxdim;
		ydim = daydim;

		setview(0, 0, xdim - 1, ydim - 1);
		setbrightness(curbrightness, palette, true);

		Console.ResizeDisplay(daxdim, daydim);

		if (render instanceof Software) {
			// Software renderer must be reinitialize when resolution is changed
			if (render.isInited())
				render.uninit();
			render.init();
		}

		if (davidoption == 1) {
			DisplayMode m = null;
			for (DisplayMode mode : BuildGdx.graphics.getDisplayModes()) {
				if (mode.width == daxdim && mode.height == daydim)
					if (m == null || m.refreshRate < mode.refreshRate) {
						m = mode;
					}
			}

			if (m == null) {
				Console.Println("Warning: " + daxdim + "x" + daydim + " fullscreen not supported", OSDTEXT_YELLOW);
				BuildGdx.graphics.setWindowedMode(daxdim, daydim);
				return false;
			} else
				BuildGdx.graphics.setFullscreenMode(m);
		} else
			BuildGdx.graphics.setWindowedMode(daxdim, daydim);

		return true;
	}

	public void inittimer(int tickspersecond) { // jfBuild
		if (timerfreq != 0)
			return; // already installed

		timerfreq = 1000;
		timerticspersec = tickspersecond;
		timerlastsample = System.nanoTime() * timerticspersec / (timerfreq * 1000000L);
	}

	public void sampletimer() { // jfBuild
		if (timerfreq == 0)
			return;

		long n = (System.nanoTime() * timerticspersec / (timerfreq * 1000000)) - timerlastsample;
		if (n > 0) {
			totalclock += n;
			timerlastsample += n;
		}
	}

	public long getticks() { // gdxBuild
		return System.currentTimeMillis();
	}

	public void updateFade(String fadename, int intensive) // gdxBuild
	{

	}

	public void showfade() { // gdxBuild
	}

	public synchronized void loadpic(String filename) // gdxBuild
	{
		Resource fil;
		if ((fil = BuildGdx.cache.open(filename, 0)) != null) {
			artversion = fil.readInt();
			if (artversion != 1)
				return;
			numtiles = fil.readInt();
			int localtilestart = fil.readInt();
			int localtileend = fil.readInt();

			for (int i = localtilestart; i <= localtileend; i++) {
				getTile(i).setWidth(fil.readShort());
			}
			for (int i = localtilestart; i <= localtileend; i++) {
				getTile(i).setHeight(fil.readShort());
			}
			for (int i = localtilestart; i <= localtileend; i++) {
				getTile(i).anm = fil.readInt();
			}

			for (int i = localtilestart; i <= localtileend; i++) {
				int dasiz = getTile(i).getSize();
				if (dasiz > 0) {
					Tile pic = getTile(i);
					pic.data = new byte[dasiz];
					fil.read(pic.data);
				}
				setpicsiz(i);
			}
			fil.close();
		}
	}

	public void setpicsiz(int tilenum) // jfBuild
	{
		Tile pic = getTile(tilenum);
		int j = 15;
		while ((j > 1) && (pow2long[j] > pic.getWidth()))
			j--;
		picsiz[tilenum] = j;
		j = 15;
		while ((j > 1) && (pow2long[j] > pic.getHeight()))
			j--;
		picsiz[tilenum] += (j << 4);
	}

	public synchronized int loadpics() { // jfBuild
		int offscount, localtilestart, localtileend, dasiz;
		int i, k;

		buildString(artfilename, 0, tilesPath);

		numtilefiles = 0;
		Resource fil;
		do {
			k = numtilefiles;

			artfilename[7] = (char) ((k % 10) + 48);
			artfilename[6] = (char) (((k / 10) % 10) + 48);
			artfilename[5] = (char) (((k / 100) % 10) + 48);
			String name = String.copyValueOf(artfilename);

			if ((fil = BuildGdx.cache.open(name, 0)) != null) {
				artversion = fil.readInt();
				if (artversion != 1)
					return (-1);

				numtiles = fil.readInt();
				localtilestart = fil.readInt();
				localtileend = fil.readInt();

				for (i = localtilestart; i <= localtileend; i++) {
					getTile(i).setWidth(fil.readShort());
				}
				for (i = localtilestart; i <= localtileend; i++) {
					getTile(i).setHeight(fil.readShort());
				}
				for (i = localtilestart; i <= localtileend; i++) {
					getTile(i).anm = fil.readInt();
				}
				offscount = 4 + 4 + 4 + 4 + ((localtileend - localtilestart + 1) << 3);
				for (i = localtilestart; i <= localtileend; i++) {
					tilefilenum[i] = k;
					tilefileoffs[i] = offscount;
					dasiz = getTile(i).getSize();
					offscount += dasiz;
				}

				numtilefiles++;
				fil.close();
			}
		} while (k != numtilefiles);

		for (i = 0; i < MAXTILES; i++)
			setpicsiz(i);

		if (artfil != null)
			artfil.close();
		artfil = null;
		artfilnum = -1;
		artfilplc = 0;

		return (numtilefiles);
	}

	public synchronized byte[] loadtile(int tilenume) { // jfBuild
		if (tilenume >= MAXTILES)
			return null;

		Tile pic = getTile(tilenume);
		int dasiz = pic.getSize();

		if (dasiz <= 0)
			return null;

		int i = tilefilenum[tilenume];

		if (i != artfilnum) {
			if (artfil != null)
				artfil.close();
			artfilnum = i;
			artfilplc = 0;

			artfilename[7] = (char) ((i % 10) + 48);
			artfilename[6] = (char) (((i / 10) % 10) + 48);
			artfilename[5] = (char) (((i / 100) % 10) + 48);

			artfil = BuildGdx.cache.open(new String(artfilename), 0);

			faketimerhandler();
		}

		if (artfil == null)
			return null;

		if (pic.data == null)
			pic.data = new byte[dasiz];

		if (artfilplc != tilefileoffs[tilenume]) {
			artfil.seek(tilefileoffs[tilenume] - artfilplc, Whence.Current);
			faketimerhandler();
		}

		if (artfil.read(pic.data) == -1)
			return null;

		faketimerhandler();
		artfilplc = tilefileoffs[tilenume] + dasiz;

		return pic.data;
	}

	public byte[] allocatepermanenttile(int tilenume, int xsiz, int ysiz) { // jfBuild
		if ((xsiz <= 0) || (ysiz <= 0) || (tilenume >= MAXTILES))
			return null;

		Tile pic = getTile(tilenume);
		pic.allocate(xsiz, ysiz);
		setpicsiz(tilenume);

		return (pic.data);
	}

	public int clipinsidebox(int x, int y, int wallnum, int walldist) { // jfBuild
		int r = (walldist << 1);
		if (isCorruptWall(wallnum))
			return 0;

		WALL wal = wall[wallnum];
		int x1 = wal.x + walldist - x;
		int y1 = wal.y + walldist - y;
		wal = wall[wal.point2];
		int x2 = wal.x + walldist - x;
		int y2 = wal.y + walldist - y;

		if ((x1 < 0) && (x2 < 0))
			return (0);
		if ((y1 < 0) && (y2 < 0))
			return (0);
		if ((x1 >= r) && (x2 >= r))
			return (0);
		if ((y1 >= r) && (y2 >= r))
			return (0);

		x2 -= x1;
		y2 -= y1;
		if (x2 * (walldist - y1) >= y2 * (walldist - x1)) // Front
		{
			if (x2 > 0)
				x2 *= (0 - y1);
			else
				x2 *= (r - y1);
			if (y2 > 0)
				y2 *= (r - x1);
			else
				y2 *= (0 - x1);
			return (x2 < y2 ? 1 : 0);
		}
		if (x2 > 0)
			x2 *= (r - y1);
		else
			x2 *= (0 - y1);
		if (y2 > 0)
			y2 *= (0 - x1);
		else
			y2 *= (r - x1);
		return ((x2 >= y2 ? 1 : 0) << 1);
	}

	public int clipinsideboxline(int x, int y, int x1, int y1, int x2, int y2, int walldist) { // jfBuild
		int r = walldist << 1;

		x1 += walldist - x;
		x2 += walldist - x;

		if (((x1 < 0) && (x2 < 0)) || ((x1 >= r) && (x2 >= r)))
			return 0;

		y1 += walldist - y;
		y2 += walldist - y;

		if (((y1 < 0) && (y2 < 0)) || ((y1 >= r) && (y2 >= r)))
			return 0;

		x2 -= x1;
		y2 -= y1;

		if (x2 * (walldist - y1) >= y2 * (walldist - x1)) // Front
		{
			x2 *= ((x2 > 0) ? (0 - y1) : (r - y1));
			y2 *= ((y2 > 0) ? (r - x1) : (0 - x1));
			return x2 < y2 ? 1 : 0;
		}

		x2 *= ((x2 > 0) ? (r - y1) : (0 - y1));
		y2 *= ((y2 > 0) ? (0 - x1) : (r - x1));
		return (x2 >= y2 ? 1 : 0) << 1;
	}

	public int inside(int x, int y, int sectnum) { // jfBuild
		if (!isValidSector(sectnum))
			return (-1);

		int cnt = 0;
		short wallid = sector[sectnum].wallptr;
		int i = sector[sectnum].wallnum;

		if (wallid < 0)
			return -1;
		do {
			if (isCorruptWall(wallid))
				return -1;

			WALL wal = wall[wallid];
			int y1 = wal.y - y;
			int y2 = wall[wal.point2].y - y;

			if ((y1 ^ y2) < 0) {
				int x1 = wal.x - x;
				int x2 = wall[wal.point2].x - x;
				if ((x1 ^ x2) >= 0)
					cnt ^= x1;
				else
					cnt ^= (x1 * y2 - x2 * y1) ^ y2;

			}
			wallid++;
			i--;
		} while (i != 0);

		return (cnt >>> 31);
	}

	public short getangle(int xvect, int yvect) { // jfBuild
		if ((xvect | yvect) == 0)
			return (0);
		if (xvect == 0)
			return (short) (512 + ((yvect < 0 ? 1 : 0) << 10));
		if (yvect == 0)
			return (short) ((xvect < 0 ? 1 : 0) << 10);
		if (xvect == yvect)
			return (short) (256 + ((xvect < 0 ? 1 : 0) << 10));
		if (xvect == -yvect)
			return (short) (768 + ((xvect > 0 ? 1 : 0) << 10));

		if (Math.abs((long) xvect) > Math.abs((long) yvect)) // GDX 26.11.2021 Integer.MIN issue fix
			return (short) (((radarang[640 + scale(160, yvect, xvect)] >> 6) + ((xvect < 0 ? 1 : 0) << 10)) & 2047);
		return (short) (((radarang[640 - scale(160, xvect, yvect)] >> 6) + 512 + ((yvect < 0 ? 1 : 0) << 10)) & 2047);
	}

	public int ksqrt(int a) { // jfBuild
		long out = a & 0xFFFFFFFFL;
		int value;
		if ((out & 0xFF000000) != 0)
			value = shlookup[(int) ((out >> 24) + 4096)] & 0xFFFF;
		else
			value = shlookup[(int) (out >> 12)] & 0xFFFF;

		out >>= value & 0xff;
		out = (out & 0xffff0000) | (sqrtable[(int) out] & 0xFFFF);
		out >>= ((value & 0xff00) >> 8);

		return (int) out;
	}

	protected static int SETSPRITEZ = 0;

	public short setsprite(int spritenum, int newx, int newy, int newz) // jfBuild
	{
		sprite[spritenum].x = newx;
		sprite[spritenum].y = newy;
		sprite[spritenum].z = newz;

		short tempsectnum = sprite[spritenum].sectnum;
		if (SETSPRITEZ == 1)
			tempsectnum = updatesectorz(newx, newy, newz, tempsectnum);
		else
			tempsectnum = updatesector(newx, newy, tempsectnum);
		if (tempsectnum < 0)
			return (-1);
		if (tempsectnum != sprite[spritenum].sectnum)
			changespritesect((short) spritenum, tempsectnum);

		return (0);
	}

	public int nextsectorneighborz(int sectnum, int thez, int topbottom, int direction) { // jfBuild
		int nextz = 0x80000000;
		if (direction == 1)
			nextz = 0x7fffffff;

		short sectortouse = -1;

		short wallid = sector[sectnum].wallptr;
		int i = sector[sectnum].wallnum, testz;
		do {
			WALL wal = wall[wallid];
			if (wal.nextsector >= 0) {
				if (topbottom == 1) {
					testz = sector[wal.nextsector].floorz;
				} else {
					testz = sector[wal.nextsector].ceilingz;
				}
				if (direction == 1) {
					if ((testz > thez) && (testz < nextz)) {
						nextz = testz;
						sectortouse = wal.nextsector;
					}
				} else {
					if ((testz < thez) && (testz > nextz)) {
						nextz = testz;
						sectortouse = wal.nextsector;
					}
				}
			}
			wallid++;
			i--;
		} while (i != 0);

		return (sectortouse);
	}

	public boolean cansee(int x1, int y1, int z1, int sect1, int x2, int y2, int z2, int sect2) { // eduke32
																									// sectbitmap

		Arrays.fill(sectbitmap, (byte) 0);

		if (!isValidSector(sect1))
			return false;
		if (!isValidSector(sect2))
			return false;

		if ((x1 == x2) && (y1 == y2))
			return (sect1 == sect2);

		int x21 = x2 - x1;
		int y21 = y2 - y1;
		int z21 = z2 - z1;

		sectbitmap[sect1 >> 3] |= (1 << (sect1 & 7));
		clipsectorlist[0] = (short) sect1;
		int danum = 1;

		for (int dacnt = 0; dacnt < danum; dacnt++) {
			short dasectnum = clipsectorlist[dacnt];
			if (!isValidSector(dasectnum))
				continue;

			SECTOR sec = sector[dasectnum];
			short startwall = sec.wallptr;
			int endwall = startwall + sec.wallnum - 1;
			if (startwall < 0 || endwall < 0)
				continue;

			for (int w = startwall; w <= endwall; w++) {
				if (isCorruptWall(w))
					continue;

				WALL wal = wall[w];
				WALL wal2 = wall[wal.point2];
				int x31 = wal.x - x1;
				int x34 = wal.x - wal2.x;
				int y31 = wal.y - y1;
				int y34 = wal.y - wal2.y;

				int bot = y21 * x34 - x21 * y34;
				if (bot <= 0)
					continue;
				long t = y21 * x31 - x21 * y31;
				if ((t & 0xFFFFFFFFL) >= (bot & 0xFFFFFFFFL))
					continue;
				t = y31 * x34 - x31 * y34;
				if ((t & 0xFFFFFFFFL) >= (bot & 0xFFFFFFFFL))
					continue;

				short nexts = wal.nextsector;
				if ((nexts < 0) || ((wal.cstat & 32) != 0))
					return false;

				t = divscale(t, bot, 24);
				int x = x1 + mulscale(x21, t, 24);
				int y = y1 + mulscale(y21, t, 24);
				int z = z1 + mulscale(z21, t, 24);

				getzsofslope(dasectnum, x, y, zofslope);
				if ((z <= zofslope[CEIL]) || (z >= zofslope[FLOOR]))
					return false;
				getzsofslope(nexts, x, y, zofslope);
				if ((z <= zofslope[CEIL]) || (z >= zofslope[FLOOR]))
					return false;

                if ((sectbitmap[nexts >> 3] & (1 << (nexts & 7))) == 0) {
					sectbitmap[nexts >> 3] |= (1 << (nexts & 7));
					clipsectorlist[danum++] = nexts;
				}
			}
		}
		// for(i=danum-1;i>=0;i--) if (clipsectorlist[i] == sect2) return(1);
		return (sectbitmap[sect2 >> 3] & (1 << (sect2 & 7))) != 0;
	}

	public int hitscan(int xs, int ys, int zs, int sectnum, int vx, int vy, int vz, // jfBuild
			Hitscan hit, int cliptype) {

		int zz, x1, y1 = 0, z1 = 0, x2, y2, x3, y3, x4, y4;
		int intx, inty, intz, endwall;
		int topt, topu, bot, dist, offx, offy, cstat;
		int i, j, k, l, xoff, yoff, dax, day;
		int ang, cosang, sinang, xspan, yspan, xrepeat, yrepeat;

		short dasector, startwall;
		short nextsector, z;
		int clipyou;

		hit.hitsect = -1;
		hit.hitwall = -1;
		hit.hitsprite = -1;
		if (sectnum < 0 || sectnum >= MAXSECTORS)
			return (-1);

		hit.hitx = hitscangoalx;
		hit.hity = hitscangoaly;

		int dawalclipmask = (cliptype & 65535);
		int dasprclipmask = (cliptype >> 16);

		clipsectorlist[0] = (short) sectnum;
		short tempshortcnt = 0;
		short tempshortnum = 1;
		do {
			dasector = clipsectorlist[tempshortcnt];
			if (dasector < 0) {
				tempshortcnt++;
				continue;
			}
			SECTOR sec = sector[dasector];
			if (sec == null) {
				tempshortcnt++;
				continue;
			}
			x1 = 0x7fffffff;
			if ((sec.ceilingstat & 2) != 0) {
				WALL wal = wall[sec.wallptr];
				WALL wal2 = wall[wal.point2];
				dax = wal2.x - wal.x;
				day = wal2.y - wal.y;
				i = ksqrt(dax * dax + day * day);
				if (i == 0)
					continue;
				i = divscale(sec.ceilingheinum, i, 15);
				dax *= i;
				day *= i;

				j = (vz << 8) - dmulscale(dax, vy, -day, vx, 15);
				if (j != 0) {
					i = ((sec.ceilingz - zs) << 8) + dmulscale(dax, ys - wal.y, -day, xs - wal.x, 15);
					if (((i ^ j) >= 0) && ((klabs(i) >> 1) < klabs(j))) {
						i = divscale(i, j, 30);
						x1 = xs + mulscale(vx, i, 30);
						y1 = ys + mulscale(vy, i, 30);
						z1 = zs + mulscale(vz, i, 30);
					}
				}
			} else if ((vz < 0) && (zs >= sec.ceilingz)) {
				z1 = sec.ceilingz;
				i = z1 - zs;
				if ((klabs(i) >> 1) < -vz) {
					i = divscale(i, vz, 30);
					x1 = xs + mulscale(vx, i, 30);
					y1 = ys + mulscale(vy, i, 30);
				}
			}
			if ((x1 != 0x7fffffff)
					&& (klabs(x1 - xs) + klabs(y1 - ys) < klabs((hit.hitx) - xs) + klabs((hit.hity) - ys)))
				if (inside(x1, y1, dasector) != 0) {
					hit.hitsect = dasector;
					hit.hitwall = -1;
					hit.hitsprite = -1;
					hit.hitx = x1;
					hit.hity = y1;
					hit.hitz = z1;
				}

			x1 = 0x7fffffff;
			if ((sec.floorstat & 2) != 0) {
				WALL wal = wall[sec.wallptr];
				WALL wal2 = wall[wal.point2];
				dax = wal2.x - wal.x;
				day = wal2.y - wal.y;
				i = ksqrt(dax * dax + day * day);
				if (i == 0)
					continue;
				i = divscale(sec.floorheinum, i, 15);
				dax *= i;
				day *= i;

				j = (vz << 8) - dmulscale(dax, vy, -day, vx, 15);
				if (j != 0) {
					i = ((sec.floorz - zs) << 8) + dmulscale(dax, ys - wal.y, -day, xs - wal.x, 15);
					if (((i ^ j) >= 0) && ((klabs(i) >> 1) < klabs(j))) {
						i = divscale(i, j, 30);
						x1 = xs + mulscale(vx, i, 30);
						y1 = ys + mulscale(vy, i, 30);
						z1 = zs + mulscale(vz, i, 30);
					}
				}
			} else if ((vz > 0) && (zs <= sec.floorz)) {
				z1 = sec.floorz;
				i = z1 - zs;
				if ((klabs(i) >> 1) < vz) {
					i = divscale(i, vz, 30);
					x1 = xs + mulscale(vx, i, 30);
					y1 = ys + mulscale(vy, i, 30);
				}
			}
			if ((x1 != 0x7fffffff)
					&& (klabs(x1 - xs) + klabs(y1 - ys) < klabs((hit.hitx) - xs) + klabs((hit.hity) - ys)))
				if (inside(x1, y1, dasector) != 0) {
					hit.hitsect = dasector;
					hit.hitwall = -1;
					hit.hitsprite = -1;
					hit.hitx = x1;
					hit.hity = y1;
					hit.hitz = z1;
				}

			startwall = sec.wallptr;
			endwall = (startwall + sec.wallnum);

			if (startwall < 0 || endwall < 0) {
				tempshortcnt++;
				continue;
			}
			Point out;
			for (z = startwall; z < endwall; z++) {
				if (isCorruptWall(z))
					continue;

				WALL wal = wall[z];
				WALL wal2 = wall[wal.point2];
				x1 = wal.x;
				y1 = wal.y;
				x2 = wal2.x;
				y2 = wal2.y;

				if ((x1 - xs) * (y2 - ys) < (x2 - xs) * (y1 - ys))
					continue;

				if ((out = rintersect(xs, ys, zs, vx, vy, vz, x1, y1, x2, y2)) == null)
					continue;

				intx = out.getX();
				inty = out.getY();
				intz = out.getZ();

				if (klabs(intx - xs) + klabs(inty - ys) >= klabs((hit.hitx) - xs) + klabs((hit.hity) - ys))
					continue;

				nextsector = wal.nextsector;
				if ((nextsector < 0) || ((wal.cstat & dawalclipmask) != 0)) {
					hit.hitsect = dasector;
					hit.hitwall = z;
					hit.hitsprite = -1;
					hit.hitx = intx;
					hit.hity = inty;
					hit.hitz = intz;
					continue;
				}
				getzsofslope(nextsector, intx, inty, zofslope);
				if ((intz <= zofslope[CEIL]) || (intz >= zofslope[FLOOR])) {
					hit.hitsect = dasector;
					hit.hitwall = z;
					hit.hitsprite = -1;
					hit.hitx = intx;
					hit.hity = inty;
					hit.hitz = intz;
					continue;
				}

				for (zz = tempshortnum - 1; zz >= 0; zz--)
					if (clipsectorlist[zz] == nextsector)
						break;
				if (zz < 0)
					clipsectorlist[tempshortnum++] = nextsector;
			}

			for (z = headspritesect[dasector]; z >= 0; z = nextspritesect[z]) {
				SPRITE spr = sprite[z];
				cstat = spr.cstat;

				if (hitallsprites == 0)

					if ((cstat & dasprclipmask) == 0)
						continue;

				x1 = spr.x;
				y1 = spr.y;
				z1 = spr.z;
				Tile pic = getTile(spr.picnum);

				switch (cstat & 48) {
				case 0:
					topt = vx * (x1 - xs) + vy * (y1 - ys);
					if (topt <= 0)
						continue;
					bot = vx * vx + vy * vy;
					if (bot == 0)
						continue;

					intz = zs + scale(vz, topt, bot);

					i = (pic.getHeight() * spr.yrepeat << 2);
					if ((cstat & 128) != 0)
						z1 += (i >> 1);
					if ((pic.anm & 0x00ff0000) != 0)
						z1 -= (pic.getOffsetY() * spr.yrepeat << 2);
					if ((intz > z1) || (intz < z1 - i))
						continue;
					topu = vx * (y1 - ys) - vy * (x1 - xs);

					offx = scale(vx, topu, bot);
					offy = scale(vy, topu, bot);
					dist = offx * offx + offy * offy;
					i = pic.getWidth() * spr.xrepeat;
					i *= i;
					if (dist > (i >> 7))
						continue;
					intx = xs + scale(vx, topt, bot);
					inty = ys + scale(vy, topt, bot);

					if (klabs(intx - xs) + klabs(inty - ys) > klabs((hit.hitx) - xs) + klabs((hit.hity) - ys))
						continue;

					hit.hitsect = dasector;
					hit.hitwall = -1;
					hit.hitsprite = z;
					hit.hitx = intx;
					hit.hity = inty;
					hit.hitz = intz;
					break;
				case 16:
					// These lines get the 2 points of the rotated sprite
					// Given: (x1, y1) starts out as the center point
					xoff = (byte) (pic.getOffsetX() + (spr.xoffset));
					if ((cstat & 4) > 0)
						xoff = -xoff;
					k = spr.ang;
					l = spr.xrepeat;
					dax = sintable[k & 2047] * l;
					day = sintable[(k + 1536) & 2047] * l;
					l = pic.getWidth();
					k = (l >> 1) + xoff;
					x1 -= mulscale(dax, k, 16);
					x2 = x1 + mulscale(dax, l, 16);
					y1 -= mulscale(day, k, 16);
					y2 = y1 + mulscale(day, l, 16);

					if ((cstat & 64) != 0) // back side of 1-way sprite
						if ((x1 - xs) * (y2 - ys) < (x2 - xs) * (y1 - ys))
							continue;

					if ((out = rintersect(xs, ys, zs, vx, vy, vz, x1, y1, x2, y2)) == null)
						continue;

					intx = out.getX();
					inty = out.getY();
					intz = out.getZ();

					if (klabs(intx - xs) + klabs(inty - ys) > klabs((hit.hitx) - xs) + klabs((hit.hity) - ys))
						continue;

					k = ((pic.getHeight() * spr.yrepeat) << 2);
					if ((cstat & 128) != 0)
						zofslope[CEIL] = spr.z + (k >> 1);
					else
						zofslope[CEIL] = spr.z;
					if ((pic.anm & 0x00ff0000) != 0)
						zofslope[CEIL] -= (pic.getOffsetY() * spr.yrepeat << 2);
					if ((intz < zofslope[CEIL]) && (intz > zofslope[CEIL] - k)) {
						hit.hitsect = dasector;
						hit.hitwall = -1;
						hit.hitsprite = z;
						hit.hitx = intx;
						hit.hity = inty;
						hit.hitz = intz;
					}
					break;
				case 32:
					if (vz == 0)
						continue;
					intz = z1;
					if (((intz - zs) ^ vz) < 0)
						continue;
					if ((cstat & 64) != 0)
						if ((zs > intz) == ((cstat & 8) == 0))
							continue;

					intx = xs + scale(intz - zs, vx, vz);
					inty = ys + scale(intz - zs, vy, vz);

					if (klabs(intx - xs) + klabs(inty - ys) > klabs((hit.hitx) - xs) + klabs((hit.hity) - ys))
						continue;

					xoff = (byte) (pic.getOffsetX() + spr.xoffset);
					yoff = (byte) (pic.getOffsetY() + spr.yoffset);
					if ((cstat & 4) > 0)
						xoff = -xoff;
					if ((cstat & 8) > 0)
						yoff = -yoff;

					ang = spr.ang;
					cosang = sintable[(ang + 512) & 2047];
					sinang = sintable[ang & 2047];
					xspan = pic.getWidth();
					xrepeat = spr.xrepeat;
					yspan = pic.getHeight();
					yrepeat = spr.yrepeat;

					dax = ((xspan >> 1) + xoff) * xrepeat;
					day = ((yspan >> 1) + yoff) * yrepeat;
					x1 += dmulscale(sinang, dax, cosang, day, 16) - intx;
					y1 += dmulscale(sinang, day, -cosang, dax, 16) - inty;
					l = xspan * xrepeat;
					x2 = x1 - mulscale(sinang, l, 16);
					y2 = y1 + mulscale(cosang, l, 16);
					l = yspan * yrepeat;
					k = -mulscale(cosang, l, 16);
					x3 = x2 + k;
					x4 = x1 + k;
					k = -mulscale(sinang, l, 16);
					y3 = y2 + k;
					y4 = y1 + k;

					clipyou = 0;
					if ((y1 ^ y2) < 0) {
						if ((x1 ^ x2) < 0)
							clipyou ^= (x1 * y2 < x2 * y1 ? 1 : 0) ^ (y1 < y2 ? 1 : 0);
						else if (x1 >= 0)
							clipyou ^= 1;
					}
					if ((y2 ^ y3) < 0) {
						if ((x2 ^ x3) < 0)
							clipyou ^= (x2 * y3 < x3 * y2 ? 1 : 0) ^ (y2 < y3 ? 1 : 0);
						else if (x2 >= 0)
							clipyou ^= 1;
					}
					if ((y3 ^ y4) < 0) {
						if ((x3 ^ x4) < 0)
							clipyou ^= (x3 * y4 < x4 * y3 ? 1 : 0) ^ (y3 < y4 ? 1 : 0);
						else if (x3 >= 0)
							clipyou ^= 1;
					}
					if ((y4 ^ y1) < 0) {
						if ((x4 ^ x1) < 0)
							clipyou ^= (x4 * y1 < x1 * y4 ? 1 : 0) ^ (y4 < y1 ? 1 : 0);
						else if (x4 >= 0)
							clipyou ^= 1;
					}

					if (clipyou != 0) {
						hit.hitsect = dasector;
						hit.hitwall = -1;
						hit.hitsprite = z;
						hit.hitx = intx;
						hit.hity = inty;
						hit.hitz = intz;
					}
					break;
				}
			}
			tempshortcnt++;
		} while (tempshortcnt < tempshortnum);
		return (0);
	}

	public void nextpage() { // gdxBuild
		faketimerhandler();
		Console.draw();
		render.nextpage();
		BuildGdx.audio.update();
		totalclocklock = totalclock;
	}

	public int neartag(int xs, int ys, int zs, int sectnum, int ange, Neartag near, int neartagrange, int tagsearch) { // jfBuild

		int i, zz, x1, y1, z1, x2, y2, endwall;
		int topt, topu, bot, dist, offx, offy;
		short dasector, startwall;
		short nextsector, good, z;

		near.tagsector = -1;
		near.tagwall = -1;
		near.tagsprite = -1;
		near.taghitdist = 0;

		if (sectnum < 0 || sectnum >= MAXSECTORS || (tagsearch & 3) == 0)
			return 0;

		int vx = mulscale(sintable[(ange + 2560) & 2047], neartagrange, 14);
		int xe = xs + vx;
		int vy = mulscale(sintable[(ange + 2048) & 2047], neartagrange, 14);
		int ye = ys + vy;
		int vz = 0;
		int ze = 0;

		clipsectorlist[0] = (short) sectnum;
		short tempshortcnt = 0;
		short tempshortnum = 1;

		Point out;
		do {
			dasector = clipsectorlist[tempshortcnt];
			if (dasector < 0) {
				tempshortcnt++;
				continue;
			}

			startwall = sector[dasector].wallptr;
			endwall = (startwall + sector[dasector].wallnum - 1);
			if (startwall < 0 || endwall < 0) {
				tempshortcnt++;
				continue;
			}
			for (z = startwall; z <= endwall; z++) {
				WALL wal = wall[z];
				WALL wal2 = wall[wal.point2];
				x1 = wal.x;
				y1 = wal.y;
				x2 = wal2.x;
				y2 = wal2.y;

				nextsector = wal.nextsector;

				good = 0;
				if (nextsector >= 0) {
					if (((tagsearch & 1) != 0) && sector[nextsector].lotag != 0)
						good |= 1;
					if (((tagsearch & 2) != 0) && sector[nextsector].hitag != 0)
						good |= 1;
				}
				if (((tagsearch & 1) != 0) && wal.lotag != 0)
					good |= 2;
				if (((tagsearch & 2) != 0) && wal.hitag != 0)
					good |= 2;

				if ((good == 0) && (nextsector < 0))
					continue;
				if ((x1 - xs) * (y2 - ys) < (x2 - xs) * (y1 - ys))
					continue;

				if ((out = lintersect(xs, ys, zs, xe, ye, ze, x1, y1, x2, y2)) != null) {
					if (good != 0) {
						if ((good & 1) != 0)
							near.tagsector = nextsector;
						if ((good & 2) != 0)
							near.tagwall = z;
						near.taghitdist = dmulscale(out.getX() - xs, sintable[(ange + 2560) & 2047], out.getY() - ys,
								sintable[(ange + 2048) & 2047], 14);
						xe = out.getX();
						ye = out.getY();
						ze = out.getZ();
					}
					if (nextsector >= 0) {
						for (zz = tempshortnum - 1; zz >= 0; zz--)
							if (clipsectorlist[zz] == nextsector)
								break;
						if (zz < 0)
							clipsectorlist[tempshortnum++] = nextsector;
					}
				}
			}

			for (z = headspritesect[dasector]; z >= 0; z = nextspritesect[z]) {
				SPRITE spr = sprite[z];

				good = 0;
				if (((tagsearch & 1) != 0) && spr.lotag != 0)
					good |= 1;
				if (((tagsearch & 2) != 0) && spr.hitag != 0)
					good |= 1;
				if (good != 0) {
					x1 = spr.x;
					y1 = spr.y;
					z1 = spr.z;

					topt = vx * (x1 - xs) + vy * (y1 - ys);
					if (topt > 0) {
						bot = vx * vx + vy * vy;
						if (bot != 0) {
							int intz = zs + scale(vz, topt, bot);
							Tile pic = getTile(spr.picnum);
							i = pic.getHeight() * spr.yrepeat;
							if ((spr.cstat & 128) != 0)
								z1 += (i << 1);
							if ((pic.anm & 0x00ff0000) != 0)
								z1 -= (pic.getOffsetY() * spr.yrepeat << 2);
							if ((intz <= z1) && (intz >= z1 - (i << 2))) {
								topu = vx * (y1 - ys) - vy * (x1 - xs);
								offx = scale(vx, topu, bot);
								offy = scale(vy, topu, bot);
								dist = offx * offx + offy * offy;
								i = (pic.getWidth() * spr.xrepeat);
								i *= i;
								if (dist <= (i >> 7)) {
									int intx = xs + scale(vx, topt, bot);
									int inty = ys + scale(vy, topt, bot);
									if (klabs(intx - xs) + klabs(inty - ys) < klabs(xe - xs) + klabs(ye - ys)) {
										near.tagsprite = z;
										near.taghitdist = dmulscale(intx - xs, sintable[(ange + 2560) & 2047],
												inty - ys, sintable[(ange + 2048) & 2047], 14);
										xe = intx;
										ye = inty;
										ze = intz;
									}
								}
							}
						}
					}
				}
			}

			tempshortcnt++;
		} while (tempshortcnt < tempshortnum);
		return (0);
	}

	public long qdist(long dx, long dy) { // gdxBuild
		dx = abs(dx);
		dy = abs(dy);

		if (dx > dy)
			dy = (3 * dy) >> 3;
		else
			dx = (3 * dx) >> 3;

		return dx + dy;
	}

	public void dragpoint(int pointhighlight, int dax, int day) { // jfBuild
		wall[pointhighlight].x = dax;
		wall[pointhighlight].y = day;

		int cnt = MAXWALLS;
		int tempshort = pointhighlight; // search points CCW
		do {
			if (wall[tempshort].nextwall >= 0) {
				tempshort = wall[wall[tempshort].nextwall].point2;
				wall[tempshort].x = dax;
				wall[tempshort].y = day;
			} else {
				tempshort = pointhighlight; // search points CW if not searched all the way around
				do {
					if (wall[lastwall(tempshort)].nextwall >= 0) {
						tempshort = wall[lastwall(tempshort)].nextwall;
						wall[tempshort].x = dax;
						wall[tempshort].y = day;
					} else {
						break;
					}
					cnt--;
				} while ((tempshort != pointhighlight) && (cnt > 0));
				break;
			}
			cnt--;
		} while ((tempshort != pointhighlight) && (cnt > 0));
	}

	public int lastwall(int point) { // jfBuild
		if ((point > 0) && (wall[point - 1].point2 == point))
			return (point - 1);

		int i = point, j;
		int cnt = MAXWALLS;
		do {
			j = wall[i].point2;
			if (j == point)
				return (i);
			i = j;
			cnt--;
		} while (cnt > 0);
		return (point);
	}

	protected void addclipline(int dax1, int day1, int dax2, int day2, int daoval) { // jfBuild
		if (clipnum < MAXCLIPNUM) {
			if (clipit[clipnum] == null)
				clipit[clipnum] = new Line();
			clipit[clipnum].x1 = dax1;
			clipit[clipnum].y1 = day1;
			clipit[clipnum].x2 = dax2;
			clipit[clipnum].y2 = day2;
			clipobjectval[clipnum] = daoval;
			clipnum++;
		}
	}

	public static int clipmove_x, clipmove_y, clipmove_z;
	public static short clipmove_sectnum;

	public int clipmove(int x, int y, int z, int sectnum, // jfBuild
			long xvect, long yvect, int walldist, int ceildist, int flordist, int cliptype) {
		clipmove_x = x;
		clipmove_y = y;
		clipmove_z = z;
		clipmove_sectnum = (short) sectnum;
		WALL wal, wal2;
		SPRITE spr;
		SECTOR sec, sec2;
		int i, templong1, templong2;
		short j, startwall, hitwall;
		long oxvect, oyvect;
		int lx, ly, retval;
		int intx, inty, goalx, goaly;

		int k, l, clipsectcnt, endwall, cstat, dasect;
		int x1, y1, x2, y2, cx, cy, rad, xmin, ymin, xmax, ymax;
		int bsz, xoff, yoff, xspan, yspan, cosang, sinang;
		int xrepeat, yrepeat, gx, gy, dx, dy, dasprclipmask, dawalclipmask;
		int cnt, clipyou;

		int dax, day, daz, daz2;

		if (((xvect | yvect) == 0) || (clipmove_sectnum < 0) || clipmove_sectnum >= MAXSECTORS)
			return (0);
		retval = 0;

		oxvect = xvect;
		oyvect = yvect;

		goalx = clipmove_x + (int) (xvect >> 14);
		goaly = clipmove_y + (int) (yvect >> 14);

		clipnum = 0;

		cx = (clipmove_x + goalx) >> 1;
		cy = (clipmove_y + goaly) >> 1;
		// Extra walldist for sprites on sector lines
		gx = goalx - clipmove_x;
		gy = goaly - clipmove_y;
		rad = (ksqrt(gx * gx + gy * gy) + MAXCLIPDIST + walldist + 8);
		xmin = cx - rad;
		ymin = cy - rad;
		xmax = cx + rad;
		ymax = cy + rad;

		dawalclipmask = (cliptype & 65535); // CLIPMASK0 = 0x00010001
		dasprclipmask = (cliptype >> 16); // CLIPMASK1 = 0x01000040

		clipsectorlist[0] = clipmove_sectnum;
		clipsectcnt = 0;
		clipsectnum = 1;
		do {
			dasect = clipsectorlist[clipsectcnt++];
			if (!isValidSector(dasect))
				continue;

			sec = sector[dasect];
			startwall = sec.wallptr;
			endwall = startwall + sec.wallnum;
			if (startwall < 0 || endwall < 0)
				continue;
			for (j = startwall; j < endwall; j++) {
				if (isCorruptWall(j))
					continue;

				wal = wall[j];
				wal2 = wall[wal.point2];
				if ((wal.x < xmin) && (wal2.x < xmin))
					continue;
				if ((wal.x > xmax) && (wal2.x > xmax))
					continue;
				if ((wal.y < ymin) && (wal2.y < ymin))
					continue;
				if ((wal.y > ymax) && (wal2.y > ymax))
					continue;

				x1 = wal.x;
				y1 = wal.y;
				x2 = wal2.x;
				y2 = wal2.y;

				dx = x2 - x1;
				dy = y2 - y1;
				if (dx * ((clipmove_y) - y1) < ((clipmove_x) - x1) * dy)
					continue; // If wall's not facing you

				if (dx > 0)
					dax = dx * (ymin - y1);
				else
					dax = dx * (ymax - y1);
				if (dy > 0)
					day = dy * (xmax - x1);
				else
					day = dy * (xmin - x1);
				if (dax >= day)
					continue;

				clipyou = 0;
				if ((wal.nextsector < 0) || ((wal.cstat & dawalclipmask) != 0)) {
					clipyou = 1;
				} else {
					Point out = rintersect(clipmove_x, clipmove_y, 0, gx, gy, 0, x1, y1, x2, y2);
					if (out == null) {
						dax = clipmove_x;
						day = clipmove_y;
					} else {
						dax = out.getX();
						day = out.getY();
					}

					daz = getflorzofslope((short) dasect, dax, day);
					daz2 = getflorzofslope(wal.nextsector, dax, day);

					sec2 = sector[wal.nextsector];
					if (sec2 == null)
						continue;
					if (daz2 < daz - (1 << 8))
						if ((sec2.floorstat & 1) == 0)
							if ((clipmove_z) >= daz2 - (flordist - 1))
								clipyou = 1;
					if (clipyou == 0) {
						daz = getceilzofslope((short) dasect, dax, day);
						daz2 = getceilzofslope(wal.nextsector, dax, day);
						if (daz2 > daz + (1 << 8))
							if ((sec2.ceilingstat & 1) == 0)
								if ((clipmove_z) <= daz2 + (ceildist - 1))
									clipyou = 1;
					}
				}

				if (clipyou == 1) {
					// Add 2 boxes at endpoints
					bsz = walldist;
					if (gx < 0)
						bsz = -bsz;
					addclipline(x1 - bsz, y1 - bsz, x1 - bsz, y1 + bsz, j + 32768);
					addclipline(x2 - bsz, y2 - bsz, x2 - bsz, y2 + bsz, j + 32768);
					bsz = walldist;
					if (gy < 0)
						bsz = -bsz;
					addclipline(x1 + bsz, y1 - bsz, x1 - bsz, y1 - bsz, j + 32768);
					addclipline(x2 + bsz, y2 - bsz, x2 - bsz, y2 - bsz, j + 32768);

					dax = walldist;
					if (dy > 0)
						dax = -dax;
					day = walldist;
					if (dx < 0)
						day = -day;
					addclipline(x1 + dax, y1 + day, x2 + dax, y2 + day, j + 32768);
				} else {
					for (i = clipsectnum - 1; i >= 0; i--)
						if (wal.nextsector == clipsectorlist[i])
							break;
					if (i < 0)
						clipsectorlist[clipsectnum++] = wal.nextsector;
				}
			}

			for (j = headspritesect[dasect]; j >= 0; j = nextspritesect[j]) {
				spr = sprite[j];

				cstat = spr.cstat;

				if ((cstat & dasprclipmask) == 0)
					continue;

				Tile pic = getTile(spr.picnum);
				x1 = spr.x;
				y1 = spr.y;
				switch (cstat & 48) {
				case 0:

					if ((x1 >= xmin) && (x1 <= xmax) && (y1 >= ymin) && (y1 <= ymax)) {
						k = ((pic.getHeight() * spr.yrepeat) << 2);
						if ((cstat & 128) != 0)
							daz = spr.z + (k >> 1);
						else
							daz = spr.z;
						if ((pic.anm & 0x00ff0000) != 0)
							daz -= (pic.getOffsetY() * spr.yrepeat << 2);

						if ((clipmove_z < (daz + ceildist)) && (clipmove_z > (daz - k - flordist))) {
							bsz = (spr.clipdist << 2) + walldist;
							if (gx < 0)
								bsz = -bsz;
							addclipline(x1 - bsz, y1 - bsz, x1 - bsz, y1 + bsz, j + 49152);
							bsz = (spr.clipdist << 2) + walldist;
							if (gy < 0)
								bsz = -bsz;
							addclipline(x1 + bsz, y1 - bsz, x1 - bsz, y1 - bsz, j + 49152);
						}
					}
					break;
				case 16:
					k = ((pic.getHeight() * spr.yrepeat) << 2);
					if ((cstat & 128) != 0)
						daz = spr.z + (k >> 1);
					else
						daz = spr.z;
					if ((pic.anm & 0x00ff0000) != 0)
						daz -= (pic.getOffsetY() * spr.yrepeat << 2);
					daz2 = daz - k;
					daz += ceildist;
					daz2 -= flordist;
					if (((clipmove_z) < daz) && ((clipmove_z) > daz2)) {
						// These lines get the 2 points of the rotated sprite
						// Given: (x1, y1) starts out as the center point
						xoff = (byte) (pic.getOffsetX() + spr.xoffset);
						if ((cstat & 4) > 0)
							xoff = -xoff;
						k = spr.ang;
						l = spr.xrepeat;
						dax = sintable[k & 2047] * l;
						day = sintable[(k + 1536) & 2047] * l;
						l = pic.getWidth();
						k = (l >> 1) + xoff;
						x1 -= mulscale(dax, k, 16);
						x2 = x1 + mulscale(dax, l, 16);
						y1 -= mulscale(day, k, 16);
						y2 = y1 + mulscale(day, l, 16);

						if (clipinsideboxline(cx, cy, x1, y1, x2, y2, rad) != 0) {
							dax = mulscale(sintable[(spr.ang + 256 + 512) & 2047], walldist, 14);
							day = mulscale(sintable[(spr.ang + 256) & 2047], walldist, 14);

							if ((x1 - (clipmove_x)) * (y2 - (clipmove_y)) >= (x2 - (clipmove_x)) * (y1 - (clipmove_y))) // Front
							{
								addclipline(x1 + dax, y1 + day, x2 + day, y2 - dax, j + 49152);
							} else {
								if ((cstat & 64) != 0)
									continue;
								addclipline(x2 - dax, y2 - day, x1 - day, y1 + dax, j + 49152);
							}

							// Side blocker
							if ((x2 - x1) * ((clipmove_x) - x1) + (y2 - y1) * ((clipmove_y) - y1) < 0) {
								addclipline(x1 - day, y1 + dax, x1 + dax, y1 + day, j + 49152);
							} else if ((x1 - x2) * ((clipmove_x) - x2) + (y1 - y2) * ((clipmove_y) - y2) < 0) {
								addclipline(x2 + day, y2 - dax, x2 - dax, y2 - day, j + 49152);
							}
						}
					}

					break;
				case 32:
					daz = spr.z + ceildist;
					daz2 = spr.z - flordist;
					if (((clipmove_z) < daz) && ((clipmove_z) > daz2)) {
						if ((cstat & 64) != 0)
							if (((clipmove_z) > spr.z) == ((cstat & 8) == 0))
								continue;

						xoff = (byte) (pic.getOffsetX() + (spr.xoffset));
						yoff = (byte) (pic.getOffsetY() + (spr.yoffset));
						if ((cstat & 4) > 0)
							xoff = -xoff;
						if ((cstat & 8) > 0)
							yoff = -yoff;

						k = spr.ang;
						cosang = sintable[(k + 512) & 2047];
						sinang = sintable[k & 2047];
						xspan = pic.getWidth();
						xrepeat = spr.xrepeat;
						yspan = pic.getHeight();
						yrepeat = spr.yrepeat;

						dax = ((xspan >> 1) + xoff) * xrepeat;
						day = ((yspan >> 1) + yoff) * yrepeat;
						rxi[0] = x1 + dmulscale(sinang, dax, cosang, day, 16);
						ryi[0] = y1 + dmulscale(sinang, day, -cosang, dax, 16);
						l = xspan * xrepeat;
						rxi[1] = rxi[0] - mulscale(sinang, l, 16);
						ryi[1] = ryi[0] + mulscale(cosang, l, 16);
						l = yspan * yrepeat;
						k = -mulscale(cosang, l, 16);
						rxi[2] = rxi[1] + k;
						rxi[3] = rxi[0] + k;
						k = -mulscale(sinang, l, 16);
						ryi[2] = ryi[1] + k;
						ryi[3] = ryi[0] + k;

						dax = mulscale(sintable[(spr.ang - 256 + 512) & 2047], walldist, 14);
						day = mulscale(sintable[(spr.ang - 256) & 2047], walldist, 14);

						if ((rxi[0] - (clipmove_x)) * (ryi[1] - (clipmove_y)) < (rxi[1] - (clipmove_x))
								* (ryi[0] - (clipmove_y))) {
							if (clipinsideboxline(cx, cy, rxi[1], ryi[1], rxi[0], ryi[0], rad) != 0)
								addclipline(rxi[1] - day, ryi[1] + dax, rxi[0] + dax, ryi[0] + day, j + 49152);
						} else if ((rxi[2] - (clipmove_x)) * (ryi[3] - (clipmove_y)) < (rxi[3] - (clipmove_x))
								* (ryi[2] - (clipmove_y))) {
							if (clipinsideboxline(cx, cy, rxi[3], ryi[3], rxi[2], ryi[2], rad) != 0)
								addclipline(rxi[3] + day, ryi[3] - dax, rxi[2] - dax, ryi[2] - day, j + 49152);
						}

						if ((rxi[1] - (clipmove_x)) * (ryi[2] - (clipmove_y)) < (rxi[2] - (clipmove_x))
								* (ryi[1] - (clipmove_y))) {
							if (clipinsideboxline(cx, cy, rxi[2], ryi[2], rxi[1], ryi[1], rad) != 0)
								addclipline(rxi[2] - dax, ryi[2] - day, rxi[1] - day, ryi[1] + dax, j + 49152);
						} else if ((rxi[3] - (clipmove_x)) * (ryi[0] - (clipmove_y)) < (rxi[0] - (clipmove_x))
								* (ryi[3] - (clipmove_y))) {
							if (clipinsideboxline(cx, cy, rxi[0], ryi[0], rxi[3], ryi[3], rad) != 0)
								addclipline(rxi[0] + dax, ryi[0] + day, rxi[3] + day, ryi[3] - dax, j + 49152);
						}
					}
					break;
				}
			}
		} while (clipsectcnt < clipsectnum);

		cnt = clipmoveboxtracenum;
		do {
			Clip out = raytrace(clipmove_x, clipmove_y, goalx, goaly);
			intx = out.getX();
			inty = out.getY();
			hitwall = out.getNum();
			if (hitwall >= 0) {
				lx = clipit[hitwall].x2 - clipit[hitwall].x1;
				ly = clipit[hitwall].y2 - clipit[hitwall].y1;
				templong2 = lx * lx + ly * ly;
				if (templong2 > 0) {
					templong1 = (goalx - intx) * lx + (goaly - inty) * ly;

					if ((klabs(templong1) >> 11) < templong2)
						i = divscale(templong1, templong2, 20);
					else
						i = 0;
					goalx = mulscale(lx, i, 20) + intx;
					goaly = mulscale(ly, i, 20) + inty;
				}

				templong1 = dmulscale(lx, oxvect, ly, oyvect, 6);
				for (i = cnt + 1; i <= clipmoveboxtracenum; i++) {
					j = hitwalls[i];
					templong2 = dmulscale(clipit[j].x2 - clipit[j].x1, oxvect, clipit[j].y2 - clipit[j].y1, oyvect, 6);
					if ((templong1 ^ templong2) < 0) {
						clipmove_sectnum = updatesector(clipmove_x, clipmove_y, clipmove_sectnum);
						return (retval);
					}
				}

				Point goal = keepaway(goalx, goaly, hitwall);
				goalx = goal.getX();
				goaly = goal.getY();

				xvect = ((goalx - intx) << 14);
				yvect = ((goaly - inty) << 14);

				if (cnt == clipmoveboxtracenum)
					retval = clipobjectval[hitwall];
				hitwalls[cnt] = hitwall;
			}
			cnt--;

			clipmove_x = intx;
			clipmove_y = inty;
		} while (((xvect | yvect) != 0) && (hitwall >= 0) && (cnt > 0));

		for (j = 0; j < clipsectnum; j++)
			if (inside(clipmove_x, clipmove_y, clipsectorlist[j]) == 1) {
				clipmove_sectnum = clipsectorlist[j];
				return (retval);
			}

		clipmove_sectnum = -1;
		templong1 = 0x7fffffff;
		for (j = (short) (numsectors - 1); j >= 0; j--)
			if (inside(clipmove_x, clipmove_y, j) == 1) {
				if ((sector[j].ceilingstat & 2) != 0)
					templong2 = getceilzofslope(j, clipmove_x, clipmove_y) - (clipmove_z);
				else
					templong2 = (sector[j].ceilingz - (clipmove_z));

				if (templong2 <= 0) {
					if ((sector[j].floorstat & 2) != 0)
						templong2 = (clipmove_z) - getflorzofslope(j, clipmove_x, clipmove_y);
					else
						templong2 = ((clipmove_z) - sector[j].floorz);

					if (templong2 <= 0) {
						clipmove_sectnum = j;
						return (retval);
					}
				}
				if (templong2 < templong1) {
					clipmove_sectnum = j;
					templong1 = templong2;
				}
			}

		return (retval);
	}

	public static int pushmove_x, pushmove_y, pushmove_z;
	public static short pushmove_sectnum;

	public int pushmove(int x, int y, int z, int sectnum, // jfBuild
			int walldist, int ceildist, int flordist, int cliptype) {
		pushmove_x = x;
		pushmove_y = y;
		pushmove_z = z;
		pushmove_sectnum = (short) sectnum;

		SECTOR sec, sec2;
		WALL wal;
		int i, j, k, t, dx, dy, dax, day, daz, daz2, bad, dir;
		int dawalclipmask;
		short startwall, endwall, clipsectcnt;
		int bad2;

		if (pushmove_sectnum < 0 || pushmove_sectnum >= MAXSECTORS)
			return (-1);

		dawalclipmask = (cliptype & 65535);

		k = 32;
		dir = 1;
		do {
			bad = 0;
			clipsectorlist[0] = pushmove_sectnum;
			clipsectcnt = 0;
			clipsectnum = 1;
			do {
				if (clipsectorlist[clipsectcnt] == -1) {
					clipsectcnt++;
					continue;
				}

				sec = sector[clipsectorlist[clipsectcnt]];
				if (dir > 0) {
					startwall = sec.wallptr;
					endwall = (short) (startwall + sec.wallnum);
				} else {
					endwall = sec.wallptr;
					startwall = (short) (endwall + sec.wallnum);
				}

				if (startwall < 0 || endwall < 0) {
					clipsectcnt++;
					continue;
				}
				for (i = startwall; i != endwall; i += dir) {
					if (i >= MAXWALLS)
						break;
					wal = wall[i];
					if (clipinsidebox(pushmove_x, pushmove_y, (short) i, walldist - 4) == 1) {
						j = 0;
						if (wal.nextsector < 0)
							j = 1;
						if ((wal.cstat & dawalclipmask) != 0)
							j = 1;
						if (j == 0) {
							sec2 = sector[wal.nextsector];

							// Find closest point on wall (dax, day) to (*x, *y)
							dax = wall[wal.point2].x - wal.x;
							day = wall[wal.point2].y - wal.y;
							daz = dax * ((pushmove_x) - wal.x) + day * ((pushmove_y) - wal.y);
							if (daz <= 0)
								t = 0;
							else {
								daz2 = dax * dax + day * day;
								if (daz >= daz2)
									t = (1 << 30);
								else
									t = divscale(daz, daz2, 30);
							}
							dax = wal.x + mulscale(dax, t, 30);
							day = wal.y + mulscale(day, t, 30);

							daz = getflorzofslope(clipsectorlist[clipsectcnt], dax, day);
							daz2 = getflorzofslope(wal.nextsector, dax, day);
							if (sec2 == null)
								continue;
							if ((daz2 < daz - (1 << 8)) && ((sec2.floorstat & 1) == 0))
								if (pushmove_z >= daz2 - (flordist - 1))
									j = 1;

							daz = getceilzofslope(clipsectorlist[clipsectcnt], dax, day);
							daz2 = getceilzofslope(wal.nextsector, dax, day);
							if ((daz2 > daz + (1 << 8)) && ((sec2.ceilingstat & 1) == 0))
								if (pushmove_z <= daz2 + (ceildist - 1))
									j = 1;
						}
						if (j != 0) {
							j = getangle(wall[wal.point2].x - wal.x, wall[wal.point2].y - wal.y);
							dx = (sintable[(j + 1024) & 2047] >> 11);
							dy = (sintable[(j + 512) & 2047] >> 11);
							bad2 = 16;
							do {
								pushmove_x += dx;
								pushmove_y += dy;
								bad2--;
								if (bad2 == 0)
									break;
							} while (clipinsidebox(pushmove_x, pushmove_y, (short) i, walldist - 4) != 0);
							bad = -1;
							k--;
							if (k <= 0)
								return (bad);
							pushmove_sectnum = updatesector(pushmove_x, pushmove_y, pushmove_sectnum);
							if (pushmove_sectnum < 0)
								return -1;
						} else {
							for (j = clipsectnum - 1; j >= 0; j--)
								if (wal.nextsector == clipsectorlist[j])
									break;
							if (j < 0)
								clipsectorlist[clipsectnum++] = wal.nextsector;
						}
					}
				}

				clipsectcnt++;
			} while (clipsectcnt < clipsectnum);
			dir = -dir;
		} while (bad != 0);

		return (bad);
	}

	public short updatesector(int x, int y, int sectnum) { // jfBuild
		if (inside(x, y, sectnum) == 1)
			return (short) sectnum;

		short i;
		if (isValidSector(sectnum)) {
			short wallid = sector[sectnum].wallptr;
			int j = sector[sectnum].wallnum;
			do {
				if (!isValidWall(wallid))
					break;

				WALL wal = wall[wallid];
				i = wal.nextsector;
				if (i >= 0)
					if (inside(x, y, i) == 1) {
						return i;
					}
				wallid++;
				j--;
			} while (j != 0);
		}

		for (i = (short) (numsectors - 1); i >= 0; i--)
			if (inside(x, y, i) == 1) {
				return i;
			}

		return -1;
	}

	public short updatesectorz(int x, int y, int z, int sectnum) { // jfBuild
		getzsofslope(sectnum, x, y, zofslope);
		if ((z >= zofslope[CEIL]) && (z <= zofslope[FLOOR]))
			if (inside(x, y, sectnum) != 0)
				return (short) sectnum;

		short i;
		if (isValidSector(sectnum)) {
			short wallid = sector[sectnum].wallptr;
			int j = sector[sectnum].wallnum;
			do {
				if (!isValidWall(wallid))
					break;

				WALL wal = wall[wallid];
				i = wal.nextsector;
				if (i >= 0) {
					getzsofslope(i, x, y, zofslope);
					if ((z >= zofslope[CEIL]) && (z <= zofslope[FLOOR]))
						if (inside(x, y, i) == 1) {
							return i;
						}
				}
				wallid++;
				j--;
			} while (j != 0);
		}

		for (i = (short) (numsectors - 1); i >= 0; i--) {
			getzsofslope(i, x, y, zofslope);
			if ((z >= zofslope[CEIL]) && (z <= zofslope[FLOOR]))
				if (inside(x, y, i) == 1) {
					return i;
				}
		}

		return -1;
	}

	protected Point rotatepoint = new Point();

	public Point rotatepoint(int xpivot, int ypivot, int x, int y, int daang) { // jfBuild
		int dacos = sintable[(daang + 2560) & 2047];
		int dasin = sintable[(daang + 2048) & 2047];
		x -= xpivot;
		y -= ypivot;
		rotatepoint.x = dmulscale(x, dacos, -y, dasin, 14) + xpivot;
		rotatepoint.y = dmulscale(y, dacos, x, dasin, 14) + ypivot;

		return rotatepoint;
	}

	public void srand(int seed) // gdxBuild
	{
		randomseed = seed;
	}

	public int getrand() // gdxBuild
	{
		return randomseed;
	}

	public int krand() { // jfBuild
		randomseed = (randomseed * 27584621) + 1;
		return (int) ((randomseed & 0xFFFFFFFFL) >> 16);
	}

	public int rand() // gdxBuild
	{
		return (int) (Math.random() * 32767);
	}

	public static int zr_ceilz, zr_ceilhit, zr_florz, zr_florhit;

	public void getzrange(int x, int y, int z, int sectnum, // jfBuild
			int walldist, int cliptype) {
		SECTOR sec;
		WALL wal, wal2;
		SPRITE spr;
		int clipsectcnt, startwall, endwall, xoff, yoff, dax, day;
		int xmin, ymin, xmax, ymax, i, j, k, l, dx, dy;
		int x1, y1, x2, y2, x3, y3, x4, y4, ang, cosang, sinang;
		int xspan, yspan, xrepeat, yrepeat, dasprclipmask, dawalclipmask;

		short cstat;
		int clipyou;

		if (!isValidSector(sectnum)) {
			zr_ceilz = 0x80000000;
			zr_ceilhit = -1;
			zr_florz = 0x7fffffff;
			zr_florhit = -1;
			return;
		}

		// Extra walldist for sprites on sector lines
		i = walldist + MAXCLIPDIST + 1;
		xmin = x - i;
		ymin = y - i;
		xmax = x + i;
		ymax = y + i;

		getzsofslope(sectnum, x, y, zofslope);
		zr_ceilz = zofslope[CEIL];
		zr_florz = zofslope[FLOOR];

		zr_ceilhit = sectnum + 16384;
		zr_florhit = sectnum + 16384;

		dawalclipmask = (cliptype & 65535);
		dasprclipmask = (cliptype >> 16);

		clipsectorlist[0] = (short) sectnum;
		clipsectcnt = 0;
		clipsectnum = 1;

		do // Collect sectors inside your square first
		{
			if (clipsectorlist[clipsectcnt] < 0) {
				clipsectcnt++;
				continue;
			}
			sec = sector[clipsectorlist[clipsectcnt]];

			startwall = sec.wallptr;
			endwall = startwall + sec.wallnum;
			if (startwall < 0 || endwall < 0) {
				clipsectcnt++;
				continue;
			}
			for (j = startwall; j < endwall; j++) {
				if (isCorruptWall(j))
					continue;

				wal = wall[j];
				k = wal.nextsector;
				if (k >= 0) {
					wal2 = wall[wal.point2];
					x1 = wal.x;
					x2 = wal2.x;
					if ((x1 < xmin) && (x2 < xmin))
						continue;
					if ((x1 > xmax) && (x2 > xmax))
						continue;
					y1 = wal.y;
					y2 = wal2.y;
					if ((y1 < ymin) && (y2 < ymin))
						continue;
					if ((y1 > ymax) && (y2 > ymax))
						continue;

					dx = x2 - x1;
					dy = y2 - y1;
					if (dx * (y - y1) < (x - x1) * dy)
						continue; // back
					if (dx > 0)
						dax = dx * (ymin - y1);
					else
						dax = dx * (ymax - y1);
					if (dy > 0)
						day = dy * (xmax - x1);
					else
						day = dy * (xmin - x1);
					if (dax >= day)
						continue;

					if ((wal.cstat & dawalclipmask) != 0)
						continue;
					sec = sector[k];
					if (sec == null)
						continue;

					if (((sec.ceilingstat & 1) == 0) && (z <= sec.ceilingz + (3 << 8)))
						continue;
					if (((sec.floorstat & 1) == 0) && (z >= sec.floorz - (3 << 8)))
						continue;

					for (i = clipsectnum - 1; i >= 0; i--)
						if (clipsectorlist[i] == k)
							break;
					if (i < 0)
						clipsectorlist[clipsectnum++] = (short) k;

					if ((x1 < xmin + MAXCLIPDIST) && (x2 < xmin + MAXCLIPDIST))
						continue;
					if ((x1 > xmax - MAXCLIPDIST) && (x2 > xmax - MAXCLIPDIST))
						continue;
					if ((y1 < ymin + MAXCLIPDIST) && (y2 < ymin + MAXCLIPDIST))
						continue;
					if ((y1 > ymax - MAXCLIPDIST) && (y2 > ymax - MAXCLIPDIST))
						continue;
					if (dx > 0)
						dax += dx * MAXCLIPDIST;
					else
						dax -= dx * MAXCLIPDIST;
					if (dy > 0)
						day -= dy * MAXCLIPDIST;
					else
						day += dy * MAXCLIPDIST;
					if (dax >= day)
						continue;

					// It actually got here, through all the continue's!!!
					getzsofslope((short) k, x, y, zofslope);

					if (zofslope[CEIL] > zr_ceilz) {
						zr_ceilz = zofslope[CEIL];
						zr_ceilhit = k + 16384;
					}
					if (zofslope[FLOOR] < zr_florz) {
						zr_florz = zofslope[FLOOR];
						zr_florhit = k + 16384;
					}
				}
			}
			clipsectcnt++;
		} while (clipsectcnt < clipsectnum);

		for (i = 0; i < clipsectnum; i++) {
			for (j = headspritesect[clipsectorlist[i]]; j >= 0; j = nextspritesect[j]) {
				spr = sprite[j];
				cstat = spr.cstat;
				if ((cstat & dasprclipmask) != 0) {
					Tile pic = getTile(spr.picnum);
					x1 = spr.x;
					y1 = spr.y;

					clipyou = 0;
					switch (cstat & 48) {
					case 0:
						k = walldist + (spr.clipdist << 2) + 1;
						if ((klabs(x1 - x) <= k) && (klabs(y1 - y) <= k)) {
							zofslope[CEIL] = spr.z;
							k = ((pic.getHeight() * spr.yrepeat) << 1);
							if ((cstat & 128) != 0)
								zofslope[CEIL] += k;
							if ((pic.anm & 0x00ff0000) != 0)
								zofslope[CEIL] -= (pic.getOffsetY() * spr.yrepeat << 2);
							zofslope[FLOOR] = zofslope[CEIL] - (k << 1);
							clipyou = 1;
						}
						break;
					case 16:
						xoff = (byte) (pic.getOffsetX() + (spr.xoffset));
						if ((cstat & 4) > 0)
							xoff = -xoff;
						k = spr.ang;
						l = spr.xrepeat;
						dax = sintable[k & 2047] * l;
						day = sintable[(k + 1536) & 2047] * l;
						l = pic.getWidth();
						k = (l >> 1) + xoff;
						x1 -= mulscale(dax, k, 16);
						x2 = x1 + mulscale(dax, l, 16);
						y1 -= mulscale(day, k, 16);
						y2 = y1 + mulscale(day, l, 16);
						if (clipinsideboxline(x, y, x1, y1, x2, y2, walldist + 1) != 0) {
							zofslope[CEIL] = spr.z;
							k = ((pic.getHeight() * spr.yrepeat) << 1);
							if ((cstat & 128) != 0)
								zofslope[CEIL] += k;
							if ((pic.anm & 0x00ff0000) != 0)
								zofslope[CEIL] -= (pic.getOffsetY() * spr.yrepeat << 2);
							zofslope[FLOOR] = zofslope[CEIL] - (k << 1);
							clipyou = 1;
						}
						break;
					case 32:
						zofslope[CEIL] = spr.z;
						zofslope[FLOOR] = zofslope[CEIL];

						if ((cstat & 64) != 0)
							if ((z > zofslope[CEIL]) == ((cstat & 8) == 0))
								continue;

						xoff = (byte) (pic.getOffsetX() + (spr.xoffset));
						yoff = (byte) (pic.getOffsetY() + (spr.yoffset));
						if ((cstat & 4) > 0)
							xoff = -xoff;
						if ((cstat & 8) > 0)
							yoff = -yoff;

						ang = spr.ang;
						cosang = sintable[(ang + 512) & 2047];
						sinang = sintable[ang & 2047];
						xspan = pic.getWidth();
						xrepeat = spr.xrepeat;
						yspan = pic.getHeight();
						yrepeat = spr.yrepeat;

						dax = ((xspan >> 1) + xoff) * xrepeat;
						day = ((yspan >> 1) + yoff) * yrepeat;
						x1 += dmulscale(sinang, dax, cosang, day, 16) - x;
						y1 += dmulscale(sinang, day, -cosang, dax, 16) - y;
						l = xspan * xrepeat;
						x2 = x1 - mulscale(sinang, l, 16);
						y2 = y1 + mulscale(cosang, l, 16);
						l = yspan * yrepeat;
						k = -mulscale(cosang, l, 16);
						x3 = x2 + k;
						x4 = x1 + k;
						k = -mulscale(sinang, l, 16);
						y3 = y2 + k;
						y4 = y1 + k;

						dax = mulscale(sintable[(spr.ang - 256 + 512) & 2047], walldist + 4, 14);
						day = mulscale(sintable[(spr.ang - 256) & 2047], walldist + 4, 14);
						x1 += dax;
						x2 -= day;
						x3 -= dax;
						x4 += day;
						y1 += day;
						y2 += dax;
						y3 -= day;
						y4 -= dax;

						if ((y1 ^ y2) < 0) {
							if ((x1 ^ x2) < 0)
								clipyou ^= (x1 * y2 < x2 * y1 ? 1 : 0) ^ (y1 < y2 ? 1 : 0);
							else if (x1 >= 0)
								clipyou ^= 1;
						}
						if ((y2 ^ y3) < 0) {
							if ((x2 ^ x3) < 0)
								clipyou ^= (x2 * y3 < x3 * y2 ? 1 : 0) ^ (y2 < y3 ? 1 : 0);
							else if (x2 >= 0)
								clipyou ^= 1;
						}
						if ((y3 ^ y4) < 0) {
							if ((x3 ^ x4) < 0)
								clipyou ^= (x3 * y4 < x4 * y3 ? 1 : 0) ^ (y3 < y4 ? 1 : 0);
							else if (x3 >= 0)
								clipyou ^= 1;
						}
						if ((y4 ^ y1) < 0) {
							if ((x4 ^ x1) < 0)
								clipyou ^= (x4 * y1 < x1 * y4 ? 1 : 0) ^ (y4 < y1 ? 1 : 0);
							else if (x4 >= 0)
								clipyou ^= 1;
						}
						break;
					}

					if (clipyou != 0) {
						if ((z > zofslope[CEIL]) && (zofslope[CEIL] > zr_ceilz)) {
							zr_ceilz = zofslope[CEIL];
							zr_ceilhit = j + 49152;
						}
						if ((z < zofslope[FLOOR]) && (zofslope[FLOOR] < zr_florz)) {
							zr_florz = zofslope[FLOOR];
							zr_florhit = j + 49152;
						}
					}
				}
			}
		}
	}

	public void setaspect_new() { // eduke32 aspect
		if (BuildSettings.usenewaspect.get() && newaspect_enable != 0 && (4 * xdim / 5) != ydim) {
			// the correction factor 100/107 has been found
			// out experimentally. squares ftw!
			int yx = (65536 * 4 * 100) / (3 * 107);
			int xd = setaspect_new_use_dimen != 0 ? xdimen : xdim;
			int yd = setaspect_new_use_dimen != 0 ? ydimen : ydim;

			int vr = divscale(xd * 3, yd * 4, 16);

			setaspect(vr, yx);
		} else
			setaspect(65536, divscale(ydim * 320, xdim * 200, 16));
	}

	public void setview(int x1, int y1, int x2, int y2) { // jfBuild
		windowx1 = x1;
		wx1 = (x1 << 12);
		windowy1 = y1;
		wy1 = (y1 << 12);
		windowx2 = x2;
		wx2 = ((x2 + 1) << 12);
		windowy2 = y2;
		wy2 = ((y2 + 1) << 12);

		xdimen = (x2 - x1) + 1;
		halfxdimen = (xdimen >> 1);
		ydimen = (y2 - y1) + 1;

		setaspect_new();

		render.setview(x1, y1, x2, y2);
	}

	public void setaspect(int daxrange, int daaspect) { // jfBuild
		viewingrange = offscreenrendering ? daxrange : (int) (daxrange * fovFactor);
		viewingrangerecip = divscale(1, viewingrange, 32);

		yxaspect = daaspect;
		xyaspect = divscale(1, yxaspect, 32);
		xdimenscale = scale(xdimen, yxaspect, 320);
		xdimscale = scale(320, xyaspect, xdimen);
	}

	public void setFov(int fov) {
		this.fovFactor = (float) Math.tan(fov * Math.PI / 360.0);
		setaspect_new();
	}

	// dastat&1 :translucence
	// dastat&2 :auto-scale mode (use 320*200 coordinates)
	// dastat&4 :y-flip
	// dastat&8 :don't clip to startumost/startdmost
	// dastat&16 :force point passed to be top-left corner, 0:Editart center
	// dastat&32 :reverse translucence
	// dastat&64 :non-masked, 0:masked
	// dastat&128 :draw all pages (permanent)
	// dastat&256 :align to the left (widescreen support)
	// dastat&512 :align to the right (widescreen support)
	// dastat&1024 :stretch to screen resolution (distorts aspect ration)

	public void rotatesprite(int sx, int sy, int z, int a, int picnum, // gdxBuild
			int dashade, int dapalnum, int dastat, int cx1, int cy1, int cx2, int cy2) {
		render.rotatesprite(sx, sy, z, a, picnum, dashade, dapalnum, dastat, cx1, cy1, cx2, cy2);
	}

	public void makepalookup(final int palnum, byte[] remapbuf, int r, int g, int b, int dastat) // jfBuild
	{
		if (paletteloaded == 0)
			return;

		// Allocate palookup buffer
		if (palookup[palnum] == null)
			palookup[palnum] = new byte[numshades << 8];

		if (dastat == 0)
			return;
		if ((r | g | b | 63) != 63)
			return;

		if ((r | g | b) == 0) {
			for (int i = 0; i < 256; i++) {
				for (int j = 0; j < numshades; j++) {
					palookup[palnum][i + j * 256] = palookup[0][(remapbuf[i] & 0xFF) + j * 256];
				}
			}
			palookupfog[palnum][0] = 0;
			palookupfog[palnum][1] = 0;
			palookupfog[palnum][2] = 0;
		} else {
			byte[] pal = new byte[768];
			System.arraycopy(curpalette.getBytes(), 0, pal, 0, 768);
			for (int j = 0; j < 768; j++)
				pal[j] = (byte) ((pal[j] & 0xFF) >> 2);

			for (int i = 0; i < numshades; i++) {
				long palscale = divscale(i, numshades, 16);
				for (int j = 0; j < 256; j++) {
					int rptr = pal[3 * (remapbuf[j] & 0xFF)] & 0xFF;
					int gptr = pal[3 * (remapbuf[j] & 0xFF) + 1] & 0xFF;
					int bptr = pal[3 * (remapbuf[j] & 0xFF) + 2] & 0xFF;

					palookup[palnum][j + i * 256] = getclosestcol(pal, rptr + mulscale(r - rptr, palscale, 16),
							gptr + mulscale(g - gptr, palscale, 16), bptr + mulscale(b - bptr, palscale, 16));
				}
			}
			palookupfog[palnum][0] = (byte) r;
			palookupfog[palnum][1] = (byte) g;
			palookupfog[palnum][2] = (byte) b;
		}

		BuildGdx.app.postRunnable(new Runnable() {
			@Override
			public void run() {
			}
		});
	}

	public void setbrightness(int dabrightness, byte[] dapal, boolean flags) {
		curbrightness = BClipRange(dabrightness, 0, 15);

		if (curbrightness != 0) {
			for (int i = 0; i < dapal.length; i++)
				temppal[i] = britable[curbrightness][(dapal[i] & 0xFF) << 2];
		} else {
//			if (gl.getTexFormat() == PixelFormat.Rgb) { // Polymost
			System.arraycopy(dapal, 0, temppal, 0, dapal.length);
			for (int i = 0; i < dapal.length; i++)
				temppal[i] <<= 2;
//			} else {
//				for (int i = 0; i < dapal.length; i++)
//					temppal[i] = britable[curbrightness][(dapal[i] & 0xFF) << 2];
//			}
		}
	}

	public boolean changepalette(final byte[] palette) {
		if (render.getType() != RenderType.Software && CRC32.getChecksum(palette) == curpalette.getCrc32())
			return false;

		curpalette.update(palette);
		Arrays.fill(palcache, null);

		BuildGdx.app.postRunnable(new Runnable() {
			@Override
			public void run() {
				render.changepalette(palette);
			}
		});
		return true;
	}

	protected byte[] temppal = new byte[768];

	public void clearview(int dacol) { // gdxBuild
		render.clearview(dacol);
	}

	public void setviewtotile(int tilenume, int xsiz, int ysiz) // jfBuild
	{
		Tile pic = getTile(tilenume);
		if (render.getType() == RenderType.Software) {
			((Software) render).setviewtotile(tilenume, pic.getWidth(), pic.getHeight());
			return;
		}

		// DRAWROOMS TO TILE BACKUP&SET CODE
		pic.setWidth(xsiz);
		pic.setHeight(ysiz);
		bakwindowx1[setviewcnt] = windowx1;
		bakwindowy1[setviewcnt] = windowy1;
		bakwindowx2[setviewcnt] = windowx2;
		bakwindowy2[setviewcnt] = windowy2;

		if (setviewcnt == 0)
			baktile = tilenume;

		offscreenrendering = true;

		setviewcnt++;
		setview(0, 0, ysiz - 1, xsiz - 1);
		setaspect(65536, 65536);
	}

	public void setviewback() // jfBuild
	{
		if (setviewcnt <= 0)
			return;
		setviewcnt--;

		offscreenrendering = (setviewcnt > 0);

		if (setviewcnt == 0 && render.getType() != RenderType.Software) {
			getTile(baktile).data = setviewbuf();
			render.invalidatetile(baktile, -1, -1);
		}

		setview(bakwindowx1[setviewcnt], bakwindowy1[setviewcnt], bakwindowx2[setviewcnt], bakwindowy2[setviewcnt]);

		if (render.getType() == RenderType.Software)
			((Software) render).setviewback();
	}

	public void squarerotatetile(int tilenume) {
		Tile pic = getTile(tilenume);
		int xsiz = pic.getWidth();
		int ysiz = pic.getHeight();

		// supports square tiles only for rotation part
		if (xsiz == ysiz) {
			int k = (xsiz << 1);
			int ptr1, ptr2;
			for (int i = xsiz - 1, j; i >= 0; i--) {
				ptr1 = i * (xsiz + 1);
				ptr2 = ptr1;
				if ((i & 1) != 0) {
					ptr1--;
					ptr2 -= xsiz;
					squarerotatetileswap(tilenume, ptr1, ptr2);
				}
				for (j = (i >> 1) - 1; j >= 0; j--) {
					ptr1 -= 2;
					ptr2 -= k;
					squarerotatetileswap(tilenume, ptr1, ptr2);
					squarerotatetileswap(tilenume, ptr1 + 1, ptr2 + xsiz);
				}
			}
		}
	}

	private void squarerotatetileswap(int tilenume, int p1, int p2) {
		Tile pic = getTile(tilenume);

		byte tmp = pic.data[p1];
		pic.data[p1] = pic.data[p2];
		pic.data[p2] = tmp;
	}

	public void preparemirror(int dax, int day, int daz, float daang, float dahoriz, int dawall, int dasector) { // jfBuild
		int x = wall[dawall].x;
		int dx = wall[wall[dawall].point2].x - x;
		int y = wall[dawall].y;
		int dy = wall[wall[dawall].point2].y - y;
		int j = dx * dx + dy * dy;
		if (j == 0)
			return;
		int i = (((dax - x) * dx + (day - y) * dy) << 1);
		mirrorx = (x << 1) + scale(dx, i, j) - dax;
		mirrory = (y << 1) + scale(dy, i, j) - day;
		mirrorang = BClampAngle((getangle(dx, dy) << 1) - daang);

		inpreparemirror = true;
	}

	public void completemirror() {
		render.completemirror();
	}

	public short sectorofwall(int theline) { // jfBuild
		if ((theline < 0) || (theline >= numwalls))
			return (-1);
		short i = wall[theline].nextwall;
		if (i >= 0)
			return (wall[i].nextsector);

		int gap = (numsectors >> 1);
		i = (short) gap;
		while (gap > 1) {
			gap >>= 1;
			if (sector[i].wallptr < theline)
				i += gap;
			else
				i -= gap;
		}
		while (sector[i].wallptr > theline)
			i--;
		while (sector[i].wallptr + sector[i].wallnum <= theline)
			i++;
		return (i);
	}

	public int getceilzofslope(int sectnum, int dax, int day) { // jfBuild
		if (sectnum < 0 || sectnum >= MAXSECTORS || sector[sectnum] == null)
			return 0;
		if ((sector[sectnum].ceilingstat & 2) == 0)
			return (sector[sectnum].ceilingz);

		WALL wal = wall[sector[sectnum].wallptr];
		int dx = wall[wal.point2].x - wal.x;
		int dy = wall[wal.point2].y - wal.y;
		int i = (ksqrt(dx * dx + dy * dy) << 5);
		if (i == 0)
			return (sector[sectnum].ceilingz);
		long j = dmulscale(dx, day - wal.y, -dy, dax - wal.x, 3);

		return sector[sectnum].ceilingz + (scale(sector[sectnum].ceilingheinum, j, i));
	}

	public int getflorzofslope(int sectnum, int dax, int day) { // jfBuild
		if (sectnum < 0 || sectnum >= MAXSECTORS || sector[sectnum] == null)
			return 0;
		if ((sector[sectnum].floorstat & 2) == 0)
			return (sector[sectnum].floorz);

		WALL wal = wall[sector[sectnum].wallptr];
		int dx = wall[wal.point2].x - wal.x;
		int dy = wall[wal.point2].y - wal.y;
		int i = (ksqrt(dx * dx + dy * dy) << 5);
		if (i == 0)
			return (sector[sectnum].floorz);
		long j = dmulscale(dx, day - wal.y, -dy, dax - wal.x, 3);
		return sector[sectnum].floorz + (scale(sector[sectnum].floorheinum, j, i));
	}

	public void getzsofslope(int sectnum, int dax, int day, int[] outz) {
		if (sectnum < 0 || sectnum >= MAXSECTORS || sector[sectnum] == null)
			return;

		SECTOR sec = sector[sectnum];
		if (sec == null)
			return;
		outz[CEIL] = sec.ceilingz;
		outz[FLOOR] = sec.floorz;
		if (((sec.ceilingstat | sec.floorstat) & 2) != 0) {
			WALL wal = wall[sec.wallptr];
			WALL wal2 = wall[wal.point2];
			int dx = wal2.x - wal.x;
			int dy = wal2.y - wal.y;
			int i = (ksqrt(dx * dx + dy * dy) << 5);
			if (i == 0)
				return;
			long j = dmulscale(dx, day - wal.y, -dy, dax - wal.x, 3);

			if ((sec.ceilingstat & 2) != 0)
				outz[CEIL] += scale(sec.ceilingheinum, j, i);
			if ((sec.floorstat & 2) != 0)
				outz[FLOOR] += scale(sec.floorheinum, j, i);
		}
	}

	public void alignceilslope(int dasect, int x, int y, int z) { // jfBuild
		WALL wal = wall[sector[dasect].wallptr];
		int dax = wall[wal.point2].x - wal.x;
		int day = wall[wal.point2].y - wal.y;

		int i = (y - wal.y) * dax - (x - wal.x) * day;
		if (i == 0)
			return;
		sector[dasect].ceilingheinum = (short) scale((z - sector[dasect].ceilingz) << 8, ksqrt(dax * dax + day * day),
				i);

		if (sector[dasect].ceilingheinum == 0)
			sector[dasect].ceilingstat &= ~2;
		else
			sector[dasect].ceilingstat |= 2;
	}

	public void alignflorslope(int dasect, int x, int y, int z) { // jfBuild
		WALL wal = wall[sector[dasect].wallptr];
		int dax = wall[wal.point2].x - wal.x;
		int day = wall[wal.point2].y - wal.y;

		int i = (y - wal.y) * dax - (x - wal.x) * day;
		if (i == 0)
			return;
		sector[dasect].floorheinum = (short) scale((z - sector[dasect].floorz) << 8, ksqrt(dax * dax + day * day), i);

		if (sector[dasect].floorheinum == 0)
			sector[dasect].floorstat &= ~2;
		else
			sector[dasect].floorstat |= 2;
	}

	public int loopnumofsector(int sectnum, int wallnum) { // jfBuild
		int numloops = 0;
		int startwall = sector[sectnum].wallptr;
		int endwall = startwall + sector[sectnum].wallnum;
		for (int i = startwall; i < endwall; i++) {
			if (i == wallnum)
				return (numloops);
			if (wall[i].point2 < i)
				numloops++;
		}
		return (-1);
	}

	public void printext256(int xpos, int ypos, int col, int backcol, char[] name, int fontsize, float scale) { // gdxBuild
		render.printext(xpos, ypos, col, backcol, name, fontsize, scale);
	}

	public String screencapture(String fn) { // jfBuild + gdxBuild (screenshot)
		int a, b, c, d;
		fn = fn.replaceAll("[^a-zA-Z0-9_. \\[\\]-]", "");

		fn = fn.substring(0, fn.lastIndexOf('.') - 4);

		DirectoryEntry userdir = BuildGdx.compat.getDirectory(Path.User);

		int capturecount = 0;
		do { // JBF 2004022: So we don't overwrite existing screenshots
			if (capturecount > 9999)
				return null;

			a = ((capturecount / 1000) % 10);
			b = ((capturecount / 100) % 10);
			c = ((capturecount / 10) % 10);
			d = (capturecount % 10);

			if (userdir.checkFile(fn + a + b + c + d + ".png") == null)
				break;
			capturecount++;
		} while (true);

		int w = xdim, h = ydim;
		Pixmap capture = null;
		try {
			capture = new Pixmap(w, h, Format.RGB888);
			ByteBuffer pixels = capture.getPixels();
			pixels.put(render.getFrame(PixelFormat.Rgb, xdim, -ydim));

			File pci = new File(userdir.getAbsolutePath() + fn + a + b + c + d + ".png");
			PixmapIO.writePNG(new FileHandle(pci), capture);
			userdir.addFile(pci);
			capture.dispose();
			return fn + a + b + c + d + ".png";
		} catch (Throwable e) {
			if (capture != null)
				capture.dispose();
			return null;
		}
	}

	protected byte[] setviewbuf() { // gdxBuild
		Tile pic = getTile(baktile);

		int width = pic.getWidth();
		int heigth = pic.getHeight();
		byte[] data = pic.data;
		if (data == null || data.length < width * heigth)
			data = new byte[width * heigth];

		ByteBuffer frame = render.getFrame(PixelFormat.Pal8, width, heigth);

		int dptr;
		int sptr = 0;
		for (int i = width - 1, j; i >= 0; i--) {
			dptr = i;
			for (j = 0; j < heigth; j++) {
				data[dptr] = frame.get(sptr++);
				dptr += width;
			}
		}

		return data;
	}

	public boolean setrendermode(Renderer render) { // gdxBuild
		this.render = render;
		render.setDefs(getDefs());
		render.init();
		return render.isInited();
	}

	public Renderer getrender() // gdxBuild
	{
		return render;
	}

	public void copytilepiece(int tilenume1, int sx1, int sy1, int xsiz, int ysiz, // jfBuild
			int tilenume2, int sx2, int sy2) {

		Tile pic1 = getTile(tilenume1);
		Tile pic2 = getTile(tilenume2);

		int xsiz1 = pic1.getWidth();
		int ysiz1 = pic1.getHeight();
		int xsiz2 = pic2.getWidth();
		int ysiz2 = pic2.getHeight();
		if ((xsiz1 > 0) && (ysiz1 > 0) && (xsiz2 > 0) && (ysiz2 > 0)) {
			if (pic1.data == null)
				loadtile(tilenume1);
			if (pic2.data == null)
				loadtile(tilenume2);

			int x1 = sx1;
			for (int i = 0; i < xsiz; i++) {
				int y1 = sy1;
				for (int j = 0; j < ysiz; j++) {
					int x2 = sx2 + i;
					int y2 = sy2 + j;
					if ((x2 >= 0) && (y2 >= 0) && (x2 < xsiz2) && (y2 < ysiz2)) {
						byte ptr = pic1.data[x1 * ysiz1 + y1];
						if (ptr != 255)
							pic2.data[x2 * ysiz2 + y2] = ptr;
					}

					y1++;
					if (y1 >= ysiz1)
						y1 = 0;
				}
				x1++;
				if (x1 >= xsiz1)
					x1 = 0;
			}
		}
	}

	public abstract void faketimerhandler();

	public void setgotpic(int tilenume) { // jfBuild
		gotpic[tilenume >> 3] |= pow2char[tilenume & 7];
	}

	public enum Clockdir {
		CW(0), CCW(1);

		private final int value;

		Clockdir(int val) {
			this.value = val;
		}

		public int getValue() {
			return value;
		}
	}

	public Clockdir clockdir(int wallstart) // Returns: 0 is CW, 1 is CCW
	{
		int minx = 0x7fffffff;
		int themin = -1;
		int i = wallstart - 1;
		do {
			if (!Gameutils.isValidWall(++i))
				break;

			if (wall[wall[i].point2].x < minx) {
				minx = wall[wall[i].point2].x;
				themin = i;
			}
		} while (wall[i].point2 != wallstart);

		int x0 = wall[themin].x;
		int y0 = wall[themin].y;
		int x1 = wall[wall[themin].point2].x;
		int y1 = wall[wall[themin].point2].y;
		int x2 = wall[wall[wall[themin].point2].point2].x;
		int y2 = wall[wall[wall[themin].point2].point2].y;

		if ((y1 >= y2) && (y1 <= y0))
			return Clockdir.CW;
		if ((y1 >= y0) && (y1 <= y2))
			return Clockdir.CCW;

		int templong = (x0 - x1) * (y2 - y1) - (x2 - x1) * (y0 - y1);
		if (templong < 0)
			return Clockdir.CW;
		else
			return Clockdir.CCW;
	}

	public int loopinside(int x, int y, int startwall) {
		int cnt = clockdir(startwall).getValue();
		int i = startwall;

		int x1, x2, y1, y2, templong;
		do {
			x1 = wall[i].x;
			x2 = wall[wall[i].point2].x;
			if ((x1 >= x) || (x2 >= x)) {
				y1 = wall[i].y;
				y2 = wall[wall[i].point2].y;
				if (y1 > y2) {
					templong = x1;
					x1 = x2;
					x2 = templong;
					templong = y1;
					y1 = y2;
					y2 = templong;
				}
				if ((y1 <= y) && (y2 > y))
					if (x1 * (y - y2) + x2 * (y1 - y) <= x * (y1 - y2))
						cnt ^= 1;
			}
			i = wall[i].point2;
		} while (i != startwall);
		return (cnt);
	}

	public void flipwalls(int numwalls, int newnumwalls) {
		int j, tempint;
		int nume = newnumwalls - numwalls;

		for (int i = numwalls; i < numwalls + (nume >> 1); i++) {
			j = numwalls + newnumwalls - i - 1;
			tempint = wall[i].x;
			wall[i].x = wall[j].x;
			wall[j].x = tempint;
			tempint = wall[i].y;
			wall[i].y = wall[j].y;
			wall[j].y = tempint;
		}
	}

	public static KeyInput getInput() // gdxBuild
	{
		return input;
	}

	public void handleevents() { // gdxBuild
		input.handleevents();
		Console.HandleScanCode();

		sampletimer();
	}

	public void initkeys() { // gdxBuild
		input = new KeyInput();
	}

	public void printfps(float scale) {
		if (System.currentTimeMillis() - fpstime >= 1000) {
			int fps = BuildGdx.graphics.getFramesPerSecond();
			float rate = BuildGdx.graphics.getDeltaTime() * 1000;
			if (fps <= 9999 && rate <= 9999) {
				int chars = buildString(fpsbuffer, 0, Double.toString(Math.round(rate * 100) / 100.0));
//				int chars = Bitoa((int) rate, fpsbuffer);
				chars = buildString(fpsbuffer, chars, "ms ", fps);
				chars = buildString(fpsbuffer, chars, "fps");
				fpsx = windowx2 - (int) ((chars << 3) * scale);
				fpsy = windowy1 + 1;
			}
			fpstime = System.currentTimeMillis();
		}
		render.printext(fpsx, fpsy, fpscol, -1, fpsbuffer, 0, scale);
	}

	public Tile getTile(int tilenum) {
		if (tiles[tilenum] == null)
			tiles[tilenum] = new Tile();
		return tiles[tilenum];
	}

	private DefScript defs;

	public void setDefs(DefScript defs) {
		this.defs = defs;
		if (getrender() == null)
			throw new NullPointerException("Renderer is not initialized!");
		getrender().setDefs(defs);
	}

	public DefScript getDefs() {
		return defs;
	}
}
