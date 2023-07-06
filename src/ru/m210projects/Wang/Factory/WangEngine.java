package ru.m210projects.Wang.Factory;

import static ru.m210projects.Build.Gameutils.isValidSector;
import static ru.m210projects.Build.Pragmas.dmulscale;
import static ru.m210projects.Build.Pragmas.mulscale;
import static ru.m210projects.Wang.Gameutils.*;
import static ru.m210projects.Wang.Main.*;
import static ru.m210projects.Wang.Game.*;

import com.badlogic.gdx.Screen;

import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Build.Pattern.BuildEngine;
import ru.m210projects.Build.Pattern.BuildGame;
import ru.m210projects.Build.Pattern.ScreenAdapters.GameAdapter;
import ru.m210projects.Build.Pattern.Tools.Interpolation;
import ru.m210projects.Build.Render.Renderer;
import ru.m210projects.Build.Render.Renderer.RenderType;
import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Build.Types.Tile;
import ru.m210projects.Wang.Type.LONGp;

public class WangEngine extends BuildEngine {

	public WangEngine(BuildGame game) throws Exception {
		super(game, synctics);
		fpscol = 4;
	}

	@Override
	public void updatesmoothticks() {
		super.updatesmoothticks();
		game.pIntSkip2.requestUpdating();
		game.pIntSkip4.requestUpdating();
	}

	@Override
	public void nextpage() {
		super.nextpage();
		game.pIntSkip2.clearinterpolations();
		game.pIntSkip4.clearinterpolations();
	}

	@Override
	public int getsmoothratio() {
		if (nInterpolation == 0 || nInterpolation == 3)
			return 65536;

		return super.getsmoothratio();
	}

	@Override
	public void sampletimer() {
		if (timerfreq == 0)
			return;

        long n = (System.nanoTime() * timerticspersec / (timerfreq * 1000000)) - timerlastsample;
		if (n > 0) {
			totalclock += n;
			timerlastsample += n;
		}
	}

	public Interpolation getInterpolation(int type) {
		if(nInterpolation >= 2) {
			switch (type) {
			case 2:
				return game.pIntSkip2;
			case 4:
				return game.pIntSkip4;
			}
		}
		return game.pInt;
	}

	public void dragpoint(int moveskip, short pointhighlight, int dax, int day) {
		if (nInterpolation == 1) {
			super.dragpoint(pointhighlight, dax, day);
			return;
		}

		// Wang Interpolation
		Interpolation pInt = getInterpolation(moveskip);
		pInt.setwallinterpolate(pointhighlight, wall[pointhighlight]);
		wall[pointhighlight].x = dax;
		wall[pointhighlight].y = day;

		int cnt = MAXWALLS;
		short tempshort = pointhighlight; // search points CCW
		do {
			if (wall[tempshort].nextwall >= 0) {
				tempshort = wall[wall[tempshort].nextwall].point2;
				pInt.setwallinterpolate(tempshort, wall[tempshort]);
				wall[tempshort].x = dax;
				wall[tempshort].y = day;
			} else {
				tempshort = pointhighlight; // search points CW if not searched all the way around
				do {
					if (wall[lastwall(tempshort)].nextwall >= 0) {
						tempshort = wall[lastwall(tempshort)].nextwall;
						pInt.setwallinterpolate(tempshort, wall[tempshort]);
						wall[tempshort].x = dax;
						wall[tempshort].y = day;
					} else
						break;

					cnt--;
				} while ((tempshort != pointhighlight) && (cnt > 0));
				break;
			}
			cnt--;
		} while ((tempshort != pointhighlight) && (cnt > 0));
	}

	@Override
	public boolean setrendermode(Renderer render) {
		if (this.render != null && this.render != render) {
			if (render.getType() != RenderType.Software) {
				final Screen screen = game.getScreen();
				if (screen instanceof GameAdapter) {
					gPrecacheScreen.init(true, new Runnable() {
						@Override
						public void run() {
							game.changeScreen(screen);
							if (game.isCurrentScreen(gGameScreen))
								game.net.ready2send = true;
						}
					});
					game.changeScreen(gPrecacheScreen);
				}
			}
		}

		return super.setrendermode(render);
	}

	@Override
	public int krand() {
		randomseed = ((randomseed * 21 + 1) & 65535);
		return (randomseed);
	}

	public void getzrange(int x, int y, int z, short sectnum, LONGp hiz, LONGp ceilhit, LONGp loz, LONGp florhit,
			int walldist, int cliptype) {
		super.getzrange(x, y, z, sectnum, walldist, cliptype);
		if (hiz != null)
			hiz.value = zr_ceilz;
		if (ceilhit != null)
			ceilhit.value = zr_ceilhit;
		if (loz != null)
			loz.value = zr_florz;
		if (florhit != null)
			florhit.value = zr_florhit;
	}

	public short setspritez(int spritenum, int newx, int newy, int newz)
	{
		sprite[spritenum].x = newx;
		sprite[spritenum].y = newy;
		sprite[spritenum].z = newz;

		short tempsectnum = updatesectorz(newx, newy, newz, sprite[spritenum].sectnum);

		if (tempsectnum < 0)
			return (-1);
		if (tempsectnum != sprite[spritenum].sectnum)
			changespritesect((short) spritenum, tempsectnum);

		return (0);
	}

	public void getzrangepoint(int x, int y, int z, short sectnum, LONGp ceilz, LONGp ceilhit, LONGp florz,
			LONGp florhit) {
		SPRITE spr;
		int j, k, l, dax, day, daz, xspan, yspan, xoff, yoff;
		int x1, y1, x2, y2, x3, y3, x4, y4, cosang, sinang, tilenum;
		short cstat;
		int clipyou;

		if (!isValidSector(sectnum)) {
			if (ceilz != null)
				ceilz.value = 0x80000000;
			if (ceilhit != null)
				ceilhit.value = -1;
			if (florz != null)
				florz.value = 0x7fffffff;
			if (florhit != null)
				florhit.value = -1;
			return;
		}

		// Initialize z's and hits to the current sector's top&bottom
		getzsofslope(sectnum, x, y, zofslope);
		if (ceilz != null)
			ceilz.value = zofslope[CEIL];
		if (florz != null)
			florz.value = zofslope[FLOOR];
		if (ceilhit != null)
			ceilhit.value = sectnum + 16384;
		if (florhit != null)
			florhit.value = sectnum + 16384;

		// Go through sprites of only the current sector
		for (j = headspritesect[sectnum]; j >= 0; j = nextspritesect[j]) {
			spr = sprite[j];
			cstat = spr.cstat;
			if ((cstat & 49) != 33)
				continue; // Only check blocking floor sprites

			daz = spr.z;

			// Only check if sprite's 2-sided or your on the 1-sided side
			if (((cstat & 64) != 0) && ((z > daz) == ((cstat & 8) == 0)))
				continue;

			// Calculate and store centering offset information into xoff&yoff
			tilenum = spr.picnum;
			Tile pic = getTile(tilenum);
			xoff = (byte)(pic.getOffsetX() + (spr.xoffset));
			yoff = (byte)(pic.getOffsetY() + (spr.yoffset));
			if ((cstat & 4) != 0)
				xoff = -xoff;
			if ((cstat & 8) != 0)
				yoff = -yoff;

			// Calculate all 4 points of the floor sprite.
			// (x1,y1),(x2,y2),(x3,y3),(x4,y4)
			// These points will already have (x,y) subtracted from them
			cosang = sintable[NORM_ANGLE(spr.ang + 512)];
			sinang = sintable[NORM_ANGLE(spr.ang)];
			xspan = pic.getWidth();
			dax = ((xspan >> 1) + xoff) * spr.xrepeat;
			yspan = pic.getHeight();
			day = ((yspan >> 1) + yoff) * spr.yrepeat;
			x1 = spr.x + dmulscale(sinang, dax, cosang, day, 16) - x;
			y1 = spr.y + dmulscale(sinang, day, -cosang, dax, 16) - y;
			l = xspan * spr.xrepeat;
			x2 = x1 - mulscale(sinang, l, 16);
			y2 = y1 + mulscale(cosang, l, 16);
			l = yspan * spr.yrepeat;
			k = -mulscale(cosang, l, 16);
			x3 = x2 + k;
			x4 = x1 + k;
			k = -mulscale(sinang, l, 16);
			y3 = y2 + k;
			y4 = y1 + k;

			// Check to see if point (0,0) is inside the 4 points by seeing if
			// the number of lines crossed as a line is shot outward is odd
			clipyou = 0;
			if ((y1 ^ y2) < 0) // If y1 and y2 have different signs
			// (- / +)
			{
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
			if (clipyou == 0)
				continue; // Point is not inside, don't clip

			// Clipping time!
			if (z > daz) {
				if (ceilz != null && daz > ceilz.value) {
					ceilz.value = daz;
					if (ceilhit != null)
						ceilhit.value = j + 49152;
				}
			} else {
				if (florz != null && daz < florz.value) {
					florz.value = daz;
					if (florhit != null)
						florhit.value = j + 49152;
				}
			}
		}
	}

	public void compare(SPRITE src, SPRITE dst) {
		Console.Println("Comparing...", Console.OSDTEXT_GREEN);
		if (dst.x != src.x)
			Console.Println("Not match x: " + dst.x + " != " + src.x, Console.OSDTEXT_RED);
		if (dst.y != src.y)
			Console.Println("Not match y: " + dst.y + " != " + src.y, Console.OSDTEXT_RED);
		if (dst.z != src.z)
			Console.Println("Not match z: " + dst.z + " != " + src.z, Console.OSDTEXT_RED);
		if (dst.cstat != src.cstat)
			Console.Println("Not match cstat: " + dst.cstat + " != " + src.cstat, Console.OSDTEXT_RED);
		if (dst.picnum != src.picnum)
			Console.Println("Not match picnum: " + dst.picnum + " != " + src.picnum, Console.OSDTEXT_RED);
		if (dst.shade != src.shade)
			Console.Println("Not match shade: " + dst.shade + " != " + src.shade, Console.OSDTEXT_RED);
		if (dst.pal != src.pal)
			Console.Println("Not match pal: " + dst.pal + " != " + src.pal, Console.OSDTEXT_RED);
		if (dst.clipdist != src.clipdist)
			Console.Println("Not match clipdist: " + dst.clipdist + " != " + src.clipdist, Console.OSDTEXT_RED);
		if (dst.detail != src.detail)
			Console.Println("Not match detail: " + dst.detail + " != " + src.detail, Console.OSDTEXT_RED);
		if (dst.xrepeat != src.xrepeat)
			Console.Println("Not match xrepeat: " + dst.xrepeat + " != " + src.xrepeat, Console.OSDTEXT_RED);
		if (dst.yrepeat != src.yrepeat)
			Console.Println("Not match xrepeat: " + dst.yrepeat + " != " + src.yrepeat, Console.OSDTEXT_RED);
		if (dst.xoffset != src.xoffset)
			Console.Println("Not match xoffset: " + dst.xoffset + " != " + src.xoffset, Console.OSDTEXT_RED);
		if (dst.yoffset != src.yoffset)
			Console.Println("Not match yoffset: " + dst.yoffset + " != " + src.yoffset, Console.OSDTEXT_RED);
		if (dst.sectnum != src.sectnum)
			Console.Println("Not match sectnum: " + dst.sectnum + " != " + src.sectnum, Console.OSDTEXT_RED);
		if (dst.statnum != src.statnum)
			Console.Println("Not match statnum: " + dst.statnum + " != " + src.statnum, Console.OSDTEXT_RED);
		if (dst.ang != src.ang)
			Console.Println("Not match ang: " + dst.ang + " != " + src.ang, Console.OSDTEXT_RED);
		if (dst.owner != src.owner)
			Console.Println("Not match owner: " + dst.owner + " != " + src.owner, Console.OSDTEXT_RED);
		if (dst.xvel != src.xvel)
			Console.Println("Not match xvel: " + dst.xvel + " != " + src.xvel, Console.OSDTEXT_RED);
		if (dst.yvel != src.yvel)
			Console.Println("Not match yvel: " + dst.yvel + " != " + src.yvel, Console.OSDTEXT_RED);
		if (dst.zvel != src.zvel)
			Console.Println("Not match zvel: " + dst.zvel + " != " + src.zvel, Console.OSDTEXT_RED);
		if (dst.lotag != src.lotag)
			Console.Println("Not match lotag: " + dst.lotag + " != " + src.lotag, Console.OSDTEXT_RED);
		if (dst.hitag != src.hitag)
			Console.Println("Not match hitag: " + dst.hitag + " != " + src.hitag, Console.OSDTEXT_RED);
		if (dst.extra != src.extra)
			Console.Println("Not match extra: " + dst.extra + " != " + src.extra, Console.OSDTEXT_RED);
		Console.Println("Compare completed", Console.OSDTEXT_GREEN);
	}

}
