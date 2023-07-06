package ru.m210projects.Witchaven;

import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Witchaven.AI.Ai.*;
import static ru.m210projects.Build.Pragmas.*;
import static ru.m210projects.Build.Strhandler.Bitoa;
import static ru.m210projects.Build.Strhandler.buildString;
import static ru.m210projects.Build.Strhandler.toCharArray;
import static ru.m210projects.Build.Gameutils.*;
import static ru.m210projects.Witchaven.Globals.*;
import static ru.m210projects.Witchaven.Main.*;
import static ru.m210projects.Witchaven.Names.*;
import static ru.m210projects.Witchaven.WH1Names.*;
import static ru.m210projects.Witchaven.WH2Names.*;
import static ru.m210projects.Witchaven.Potions.*;
import static ru.m210projects.Witchaven.Spellbooks.*;
import static ru.m210projects.Witchaven.WHFX.*;
import static ru.m210projects.Witchaven.WHOBJ.*;
import static ru.m210projects.Witchaven.WHPLR.*;
import static ru.m210projects.Witchaven.Weapons.*;
import static ru.m210projects.Witchaven.Items.*;

import com.badlogic.gdx.math.Vector3;

import ru.m210projects.Build.Engine;
import ru.m210projects.Build.Gameutils;
import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Build.Pattern.BuildFont;
import ru.m210projects.Build.Pattern.BuildFont.TextAlign;
import ru.m210projects.Build.Pattern.Tools.Interpolation.ILoc;
import ru.m210projects.Build.Render.GLRenderer.GLInvalidateFlag;
import ru.m210projects.Build.Settings.BuildSettings;
import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Witchaven.Factory.WHSoftware;
import ru.m210projects.Witchaven.Types.PLAYER;

public class WHScreen {

//	public static int jumphoriz;

	private static int[] zofslope = new int[2];

	public static char[] armorbuf = new char[50];
	public static char[] scorebuf = new char[50];
	public static char[] healthbuf = new char[50];

	private static final char[] buffer = new char[256];
	public static boolean drawfloormirror = false;

	public static int zoom = 256;

	public static final Vector3 sflag[] = { new Vector3(260, 387, 0), new Vector3(286, 387, 10),
			new Vector3(260, 417, 11), new Vector3(286, 417, 12), };

	public static char[] displaybuf = new char[128];

	public static int redcount, whitecount, greencount, bluecount;

	public static final int NUMWHITESHIFTS = 3;
	public static final int WHITESTEPS = 20;
	public static final int WHITETICS = 6;

	public static final int NUMREDSHIFTS = 4;
	public static final int REDSTEPS = 8;

	public static final int NUMGREENSHIFTS = 4;
	public static final int GREENSTEPS = 8;

	public static final int NUMBLUESHIFTS = 4;
	public static final int BLUESTEPS = 8;

	public static final byte[][] whiteshifts = new byte[NUMREDSHIFTS][768];
	public static final byte[][] redshifts = new byte[NUMREDSHIFTS][768];
	public static final byte[][] greenshifts = new byte[NUMGREENSHIFTS][768];
	public static final byte[][] blueshifts = new byte[NUMBLUESHIFTS][768];

	public static boolean palshifted;

	public static SPRITE tspritelist[] = new SPRITE[MAXSPRITESONSCREEN + 1];
	public static int tspritelistcnt;

	public static void initpaletteshifts() {
		int delta, workptr, baseptr;

		for (int i = 1; i <= NUMREDSHIFTS; i++) {
			workptr = 0;
			baseptr = 0;
			for (int j = 0; j <= 255; j++) {
				delta = 64 - (palette[baseptr] & 0xFF);
				redshifts[i - 1][workptr++] = (byte) (((palette[baseptr++] & 0xFF) + delta * i / REDSTEPS) << 2);
				delta = -(palette[baseptr] & 0xFF);
				redshifts[i - 1][workptr++] = (byte) (((palette[baseptr++] & 0xFF) + delta * i / REDSTEPS) << 2);
				delta = -(palette[baseptr] & 0xFF);
				redshifts[i - 1][workptr++] = (byte) (((palette[baseptr++] & 0xFF) + delta * i / REDSTEPS) << 2);
			}
		}

		for (int i = 1; i <= NUMWHITESHIFTS; i++) {
			workptr = 0;
			baseptr = 0;
			for (int j = 0; j <= 255; j++) {
				delta = 64 - (palette[baseptr] & 0xFF);
				whiteshifts[i - 1][workptr++] = (byte) (((palette[baseptr++] & 0xFF) + delta * i / WHITESTEPS) << 2);
				delta = 62 - (palette[baseptr] & 0xFF);
				whiteshifts[i - 1][workptr++] = (byte) (((palette[baseptr++] & 0xFF) + delta * i / WHITESTEPS) << 2);
				delta = 0 - (palette[baseptr] & 0xFF);
				whiteshifts[i - 1][workptr++] = (byte) (((palette[baseptr++] & 0xFF) + delta * i / WHITESTEPS) << 2);
			}
		}

		for (int i = 1; i <= NUMGREENSHIFTS; i++) {
			workptr = 0;
			baseptr = 0;
			for (int j = 0; j <= 255; j++) {
				delta = -(palette[baseptr] & 0xFF);
				greenshifts[i - 1][workptr++] = (byte) (((palette[baseptr++] & 0xFF) + delta * i / GREENSTEPS) << 2);
				delta = 64 - (palette[baseptr] & 0xFF);
				greenshifts[i - 1][workptr++] = (byte) (((palette[baseptr++] & 0xFF) + delta * i / GREENSTEPS) << 2);
				delta = -(palette[baseptr] & 0xFF);
				greenshifts[i - 1][workptr++] = (byte) (((palette[baseptr++] & 0xFF) + delta * i / GREENSTEPS) << 2);
			}
		}

		for (int i = 1; i <= NUMBLUESHIFTS; i++) {
			workptr = 0;
			baseptr = 0;
			for (int j = 0; j <= 255; j++) {
				delta = -(palette[baseptr] & 0xFF);
				blueshifts[i - 1][workptr++] = (byte) (((palette[baseptr++] & 0xFF) + delta * i / BLUESTEPS) << 2);
				delta = -(palette[baseptr] & 0xFF);
				blueshifts[i - 1][workptr++] = (byte) (((palette[baseptr++] & 0xFF) + delta * i / BLUESTEPS) << 2);
				delta = 64 - (palette[baseptr] & 0xFF);
				blueshifts[i - 1][workptr++] = (byte) (((palette[baseptr++] & 0xFF) + delta * i / BLUESTEPS) << 2);
			}
		}
	}

	public static void updatepaletteshifts() {
		if (engine.glrender() != null) {
			if (whitecount != 0)
				whitecount = BClipLow(whitecount - TICSPERFRAME, 0);

			if (redcount != 0)
				redcount = BClipLow(redcount - TICSPERFRAME, 0);

			if (bluecount != 0)
				bluecount = BClipLow(bluecount - TICSPERFRAME, 0);

			if (greencount != 0)
				greencount = BClipLow(greencount - TICSPERFRAME, 0);
		} else {

			int red = 0, white = 0, green = 0, blue = 0;

			if (whitecount != 0) {
				white = BClipHigh(whitecount / WHITETICS + 1, NUMWHITESHIFTS);
				whitecount = BClipLow(whitecount - TICSPERFRAME, 0);
			}

			if (redcount != 0) {
				red = BClipHigh(redcount / 10 + 1, NUMREDSHIFTS);
				redcount = BClipLow(redcount - TICSPERFRAME, 0);
			}

			if (greencount != 0) {
				green = BClipHigh(greencount / 10 + 1, NUMGREENSHIFTS);
				greencount = BClipLow(greencount - TICSPERFRAME, 0);
			}

			if (bluecount != 0) {
				blue = BClipHigh(bluecount / 10 + 1, NUMBLUESHIFTS);
				bluecount = BClipLow(bluecount - TICSPERFRAME, 0);
			}

			if (red != 0) {
				engine.changepalette(redshifts[red - 1]);
				palshifted = true;
			} else if (white != 0) {
				engine.changepalette(whiteshifts[white - 1]);
				palshifted = true;
			} else if (green != 0) {
				engine.changepalette(greenshifts[green - 1]);
				palshifted = true;
			} else if (blue != 0) {
				engine.changepalette(blueshifts[blue - 1]);
				palshifted = true;
			} else if (palshifted) {
				int brightness = BuildSettings.paletteGamma.get();
				engine.setbrightness(brightness, palette, GLInvalidateFlag.All);
				palshifted = false;
			}
		}
	}

	public static void startredflash(int damage) {
		redcount = BClipHigh(redcount + 3 * damage, 100);
	}

	public static void startblueflash(int bluetime) {
		bluecount = BClipHigh(bluecount + 3 * bluetime, 100);
	}

	public static void startwhiteflash(int whitetime) {
		whitecount = BClipHigh(whitecount + 3 * whitetime, 100);
	}

	public static void startgreenflash(int greentime) {
		greencount = BClipHigh(greencount + 3 * greentime, 100);
	}

	public static void resetflash() {
		redcount = 0;
		whitecount = 0;
		greencount = 0;
		bluecount = 0;

		engine.updateFade("RED", 0);
		engine.updateFade("WHITE", 0);
		engine.updateFade("GREEN", 0);
		engine.updateFade("BLUE", 0);
	}

	public static int displaytime;
	public static String lastmessage;

	public static void showmessage(String message, int time) {
		buildString(displaybuf, 0, message);
		displaytime = time;
		if (!message.equals(lastmessage))
			Console.Println(message);
		lastmessage = message;
	}

	public static void setup3dscreen(int w, int h) {
		if (!engine.setgamemode(whcfg.fullscreen, w, h))
			whcfg.fullscreen = 0;
	
		whcfg.ScreenWidth = BuildGdx.graphics.getWidth();
		whcfg.ScreenHeight = BuildGdx.graphics.getHeight();
	}

	public static void spikeanimation(PLAYER plr) {
		if (plr.spiketics < 0) {
			plr.currspikeframe++;
			if (plr.currspikeframe > 4)
				plr.currspikeframe = 4;
			plr.spiketics = spikeanimtics[plr.currspikeframe].daweapontics;
			plr.spikeframe = spikeanimtics[plr.currspikeframe].daweaponframe;
		}
	}

	public static void drawscreen(int num, int dasmoothratio) {

		PLAYER plr = player[num];

		int cposx = plr.x;
		int cposy = plr.y;
		int cposz = plr.z;
		float cang = plr.ang;
		float choriz = plr.horiz + plr.jumphoriz;

		if (!game.menu.gShowMenu && !Console.IsShown()) {

			int ix = gPrevPlayerLoc[num].x;
			int iy = gPrevPlayerLoc[num].y;
			int iz = gPrevPlayerLoc[num].z;
			float iHoriz = gPrevPlayerLoc[num].horiz;
			float inAngle = gPrevPlayerLoc[num].ang;

			ix += mulscale(cposx - gPrevPlayerLoc[num].x, dasmoothratio, 16);
			iy += mulscale(cposy - gPrevPlayerLoc[num].y, dasmoothratio, 16);
			iz += mulscale(cposz - gPrevPlayerLoc[num].z, dasmoothratio, 16);
			iHoriz += ((choriz - gPrevPlayerLoc[num].horiz) * dasmoothratio) / 65536.0f;
			inAngle += ((BClampAngle(cang - gPrevPlayerLoc[num].ang + 1024) - 1024) * dasmoothratio) / 65536.0f;

			cposx = ix;
			cposy = iy;
			cposz = iz;

			choriz = iHoriz;
			cang = inAngle;
		}

		// wango
        if ((gotpic[FLOORMIRROR >> 3] & (1 << (FLOORMIRROR & 7))) != 0) {
              int dist = 0x7fffffff;
              int i = 0, j;
              for (int k = floormirrorcnt - 1; k >= 0; k--) {
            	  int sect = floormirrorsector[k];
            	  if((gotsector[sect >> 3] & (1 << (sect & 7))) == 0) continue;
                   j = klabs(wall[sector[sect].wallptr].x - plr.x);
                   j += klabs(wall[sector[sect].wallptr].y - plr.y);
                   if (j < dist) {
                        dist = j; i = k;
                   }
              }

              if(engine.glrender() != null) {
            	  drawfloormirror = inpreparemirror = true;
            	  engine.glrender().settiltang(1024);
            	  engine.drawrooms(plr.x, plr.y,  plr.z, plr.ang, 201 - plr.horiz, floormirrorsector[i]);
            	  analizesprites(plr, dasmoothratio);
            	  engine.drawmasks();
            	  engine.glrender().settiltang(0);
            	  drawfloormirror = inpreparemirror = false;
              } else {
            	  engine.drawrooms(plr.x, plr.y, (sector[floormirrorsector[i]].floorz << 1) - plr.z, plr.ang, 201 - plr.horiz, floormirrorsector[i]);
                  analizesprites(plr, dasmoothratio);
                  engine.drawmasks();

                  // Temp horizon
                  WHSoftware rend = (WHSoftware) engine.getrender();
                  rend.TempHorizon((int) plr.horiz);
              }

              gotpic[FLOORMIRROR >> 3] &= ~(1 << (FLOORMIRROR & 7));
        }

		engine.getzsofslope(plr.sector, cposx, cposy, zofslope);
		int lz = 4 << 8;
		if (cposz < zofslope[CEIL] + lz)
			cposz = zofslope[CEIL] + lz;
		if (cposz > zofslope[FLOOR] - lz)
			cposz = zofslope[FLOOR] - lz;

		engine.drawrooms(cposx, cposy, cposz, cang, choriz, plr.sector);
		analizesprites(plr, dasmoothratio);
		engine.drawmasks();

		if (dimension == 2) {

			if (followmode) {
				cposx = followx;
				cposy = followy;
				cang = followa;

				game.getFont(1).drawText(5, 25, toCharArray("follow mode"), 0, 7, TextAlign.Left, 2 | 256, false);
			}

			if (mUserFlag == UserFlag.UserMap)
				game.getFont(1).drawText(5, 15, toCharArray("user map: " + boardfilename), 0, 7, TextAlign.Left,
						2 | 256, false);
			else {
				if(gCurrentEpisode != null && gCurrentEpisode.getMap(mapon) != null)
					game.getFont(1).drawText(5, 15, gCurrentEpisode.getMap(mapon).title, 0, 7, TextAlign.Left, 2 | 256, false);
				else game.getFont(1).drawText(5, 15, "Map " + mapon, 0, 7, TextAlign.Left, 2 | 256, false);
			}

			engine.drawoverheadmap(cposx, cposy, zoom, (short) cang);
		}
	}

	public static void drawscary() {
		if (scarytime > 140 && scarytime < 180)
			engine.rotatesprite(320 << 15, 200 << 15, scarysize << 9, 0, SCARY, 0, 0, 1 + 2, 0, 0, xdim, ydim);
		if (scarytime > 120 && scarytime < 139)
			engine.rotatesprite(320 << 15, 200 << 15, scarysize << 9, 0, SCARY + 1, 0, 0, 1 + 2, 0, 0, xdim, ydim);
		if (scarytime > 100 && scarytime < 119)
			engine.rotatesprite(320 << 15, 200 << 15, scarysize << 9, 0, SCARY + 2, 0, 0, 1 + 2, 0, 0, xdim, ydim);
		if (scarytime > 0 && scarytime < 99)
			engine.rotatesprite(320 << 15, 200 << 15, scarysize << 9, 0, SCARY + 3, 0, 0, 1 + 2, 0, 0, xdim, ydim);
	}

	public static void overwritesprite(int thex, int they, int tilenum, int shade, int stat, int dapalnum) {

		engine.rotatesprite(thex << 16, they << 16, 65536, (stat & 8) << 7, tilenum, shade, dapalnum,
				(((stat & 1) ^ 1) << 4) + (stat & 2) + ((stat & 4) >> 2) + (((stat & 16) >> 2) ^ ((stat & 8) >> 1)) + 8
						+ (stat & 256) + (stat & 512),
				windowx1, windowy1, windowx2, windowy2);
	}

	public static void spikeheart(PLAYER plr) {
		int dax = spikeanimtics[plr.currspikeframe].currx;
		int day = spikeanimtics[plr.currspikeframe].curry;

		overwritesprite(dax, day, plr.spikeframe, sector[plr.sector].ceilingshade, 0x02, 0);
		startredflash(10);
	}

	public static void levelpic(PLAYER plr, int x, int y, int scale) {
		if (plr.selectedgun == 6) {
			Bitoa(plr.ammo[6], tempchar);

			if(game.WH2) {
				int px = (x - mulscale(314, scale, 16));
				int py = (y - mulscale(43, scale, 16));
				engine.rotatesprite(px << 16, py << 16, 2*scale, 0,
						1916, 0, 0, 8 | 16, px + mulscale(4, scale, 16), py + mulscale(4, scale, 16), xdim, py + mulscale(20, scale, 16));
			} else
			engine.rotatesprite(x - mulscale(313, scale, 16) << 16, y - mulscale(43, scale, 16) << 16, scale, 0,
					SARROWS, 0, 0, 8 | 16, 0, 0, xdim, ydim - 1);
			game.getFont(4).drawText(x - mulscale(235, scale, 16), y - mulscale(40, scale, 16), tempchar, scale, 0, 0,
					TextAlign.Left, 0, false);
		} else if (plr.selectedgun == 7 && plr.weapon[7] == 2) {
			Bitoa(plr.ammo[7], tempchar);
			engine.rotatesprite(x - mulscale(game.WH2 ? 314 : 313, scale, 16) << 16, y - mulscale(46, scale, 16) << 16, scale, 0, SPIKES,
					0, 0, 8 | 16, 0, 0, xdim, ydim - 1);
			game.getFont(4).drawText(x - mulscale(235, scale, 16), y - mulscale(40, scale, 16), tempchar, scale, 0, 0,
					TextAlign.Left, 0, false);
		} else {
			if(game.WH2) {
				int tilenum = 1917 + (plr.lvl - 1);
				int px = (x - mulscale(314, scale, 16));
				int py = (y - mulscale(43, scale, 16));
				engine.rotatesprite(px << 16, py << 16, 2*scale, 0,
						tilenum, 0, 0, 8 | 16, px + mulscale(4, scale, 16), py + mulscale(4, scale, 16), xdim, py + mulscale(20, scale, 16));
			} else {
				int tilenum = SPLAYERLVL + (plr.lvl - 1);
				engine.rotatesprite((x - mulscale(313, scale, 16)) << 16, (y - mulscale(43, scale, 16)) << 16, scale, 0,
						tilenum, 0, 0, 8 | 16, 0, 0, xdim, ydim - 1);
			}
		}
	}

	public static void drawscore(PLAYER plr, int x, int y, int scale) {
		Bitoa(plr.score, scorebuf);
		engine.rotatesprite((x - mulscale(game.WH2 ? 314 : 313, scale, 16)) << 16, (y - mulscale(85, scale, 16)) << 16, scale, 0,
				SSCOREBACKPIC, 0, 0, 8 | 16, 0, 0, xdim, ydim - 1);
		game.getFont(4).drawText(x - mulscale(259, scale, 16), y - mulscale(81, scale, 16), scorebuf, scale, 0, 0,
				TextAlign.Left, 0, false);
	}

	public static void updatepics(PLAYER plr, int x, int y, int scale) {
		drawscore(plr, x, y, scale);
		if (netgame) {
			if (game.nNetMode == NetMode.Multiplayer)
				captureflagpic(scale);
			else
				fragspic(plr, scale);
		} else
			potionpic(plr, plr.currentpotion, x, y, scale);

		levelpic(plr, x, y, scale);
		drawhealth(plr, x, y, scale);
		drawarmor(plr, x, y, scale);
		keyspic(plr, x, y, scale);
	}

	public static void captureflagpic(int scale) {

		int i;
		overwritesprite(260 << 1, 387, SPOTIONBACKPIC, 0, 0, 0);

		for (i = 0; i < 4; i++) {
//			if( teaminplay[i] ) { XXX
			overwritesprite(((int) sflag[i].x << 1) + 6, (int) sflag[i].y + 8, STHEFLAG, 0, 0, (int) sflag[i].z);
//				 Bitoa(teamscore[i],tempchar);
//			fancyfont(((int) sflag[i].x << 1) + 16, (int) sflag[i].y + 16, SPOTIONFONT - 26, tempchar, 0);
//			}
		}
	}

	public static void fragspic(PLAYER plr, int scale) {
		if (whcfg.gViewSize == 320) {

			int x = windowx2 / 2 + 200;
			int y = windowy2 - 94;
			overwritesprite(x, y, SPOTIONBACKPIC, 0, 0, 0);

//			Bitoa(teamscore[pyrn],tempchar); XXX
			game.getFont(2).drawText(x + 10, y + 10, tempchar, 0, 0, TextAlign.Left, 0, false);
		}
	}

	public static void keyspic(PLAYER plr, int x, int y, int scale) {
		y -= mulscale(85, scale, 16);
		if (plr.treasure[TBRASSKEY] == 1)
			engine.rotatesprite((x + mulscale(180, scale, 16)) << 16, y << 16, scale, 0, SKEYBRASS, 0, 0, 8, 0, 0, xdim,
					ydim - 1);
		else
			engine.rotatesprite((x + mulscale(180, scale, 16)) << 16, y << 16, scale, 0, SKEYBLANK, 0, 0, 8, 0, 0, xdim,
					ydim - 1);

		y += mulscale(22, scale, 16);
		if (plr.treasure[TBLACKKEY] == 1)
			engine.rotatesprite((x + mulscale(180, scale, 16)) << 16, y << 16, scale, 0, SKEYBLACK, 0, 0, 8, 0, 0, xdim,
					ydim - 1);
		else
			engine.rotatesprite((x + mulscale(180, scale, 16)) << 16, y << 16, scale, 0, SKEYBLANK, 0, 0, 8, 0, 0, xdim,
					ydim - 1);

		y += mulscale(22, scale, 16);
		if (plr.treasure[TGLASSKEY] == 1)
			engine.rotatesprite((x + mulscale(180, scale, 16)) << 16, y << 16, scale, 0, SKEYGLASS, 0, 0, 8, 0, 0, xdim,
					ydim - 1);
		else
			engine.rotatesprite((x + mulscale(180, scale, 16)) << 16, y << 16, scale, 0, SKEYBLANK, 0, 0, 8, 0, 0, xdim,
					ydim - 1);

		y += mulscale(22, scale, 16);
		if (plr.treasure[TIVORYKEY] == 1)
			engine.rotatesprite((x + mulscale(180, scale, 16)) << 16, y << 16, scale, 0, SKEYIVORY, 0, 0, 8, 0, 0, xdim,
					ydim - 1);
		else
			engine.rotatesprite((x + mulscale(180, scale, 16)) << 16, y << 16, scale, 0, SKEYBLANK, 0, 0, 8, 0, 0, xdim,
					ydim - 1);
	}

	public static void drawhealth(PLAYER plr, int x, int y, int scale) {
		Bitoa(plr.health, healthbuf);
		if (plr.poisoned == 1) {
			int flag = 0;
			switch (sintable[(10 * totalclock) & 2047] / 4096) {
			case 0:
				flag = 0;
				break;
			case 1:
			case -1:
				flag = 1;
				break;
			case -2:
			case 2:
				flag = 33;
				break;
			default:
				game.getFont(2).drawText(x - mulscale(167, scale, 16), y - mulscale(70, scale, 16), healthbuf, scale, 0,
						0, TextAlign.Left, 0, false);
				return;
			}

			engine.rotatesprite((x - mulscale(171, scale, 16)) << 16, (y - mulscale(75, scale, 16)) << 16, scale, 0,
					SHEALTHBACK, 0, 6, 8 | 16 | flag, windowx1, windowy1, windowx2, windowy2);
			game.getFont(2).drawText(x - mulscale(167, scale, 16), y - mulscale(70, scale, 16), healthbuf, scale, 0, 0,
					TextAlign.Left, 0, false);
		} else {
			engine.rotatesprite((x - mulscale(171, scale, 16)) << 16, (y - mulscale(75, scale, 16)) << 16, scale, 0,
					SHEALTHBACK, 0, 0, 8 | 16, 0, 0, xdim, ydim - 1);
			game.getFont(2).drawText(x - mulscale(167, scale, 16), y - mulscale(70, scale, 16), healthbuf, scale, 0, 0,
					TextAlign.Left, 0, false);
		}
	}

	public static void drawarmor(PLAYER plr, int x, int y, int scale) {
		Bitoa(plr.armor, armorbuf);

		engine.rotatesprite((x + mulscale(81, scale, 16)) << 16, (y - mulscale(75, scale, 16)) << 16, scale, 0,
				SHEALTHBACK, 0, 0, 8 | 16, 0, 0, xdim, ydim - 1);
		game.getFont(2).drawText(x + mulscale(89, scale, 16), y - mulscale(70, scale, 16), armorbuf, scale, 0, 0,
				TextAlign.Left, 0, false);
	}

	public static void drawweapons(int snum) {

		int dax, day;
		char dabits;
		byte dashade;
		char dapalnum;

		PLAYER plr = player[snum];

		if (plr.shadowtime > 0 || plr.sector == -1) {
			dashade = 31;
			dapalnum = 0;
		} else {
			dashade = sector[plr.sector].ceilingshade;
			dapalnum = 0;
		}

		if (plr.invisibletime > 0)
			dabits = 0x06;
		else
			dabits = 0x02;

		if (plr.currweaponflip == 1)
			dabits |= 0x08;

		switch (plr.currweapon) {
		case 1:
		case 2:
		case 3:
		case 4:
		case 6:
		case 9:
			dabits |= 512;
			break;
		}

		switch (plr.currweaponfired) {
		case 6:
			if (game.WH2) {
				if (plr.weapon[plr.currweapon] == 1 || plr.weapon[7] == 2) {
					{
						dax = lefthandanimtics[plr.currweapon][plr.currweaponanim].currx;
						day = lefthandanimtics[plr.currweapon][plr.currweaponanim].curry + 8;
					}
				} else {
					dax = zlefthandanimtics[plr.currweapon][plr.currweaponanim].currx;
					day = zlefthandanimtics[plr.currweapon][plr.currweaponanim].curry + 8;
				}
			} else {
				dax = lefthandanimtics[plr.currweapon][plr.currweaponanim].currx;
				day = lefthandanimtics[plr.currweapon][plr.currweaponanim].curry + 8;
			}

			dabits &= ~512;
			if (plr.currweapon == 0 && plr.currweaponframe != 0) {
				if (dahand == 1)
					overwritesprite(dax, day, plr.currweaponframe, dashade, dabits, dapalnum);
				else if (dahand == 2) {
					dax = lefthandanimtics[0][plr.currweaponanim].currx;
					day = lefthandanimtics[0][plr.currweaponanim].curry + 8;
					overwritesprite(dax, day + 5, plr.currweaponframe + 6, dashade, dabits, dapalnum);
				}
			} else {
				if (plr.currweaponframe != 0) {
					if (game.WH2) {
						if (plr.weapon[plr.currweapon] == 1)
							dax = lefthandanimtics[plr.currweapon][plr.currweaponanim].currx;
						else
							dax = zlefthandanimtics[plr.currweapon][plr.currweaponanim].currx;
					} else
						dax = lefthandanimtics[plr.currweapon][plr.currweaponanim].currx;
					overwritesprite(dax, day, plr.currweaponframe, dashade, dabits, dapalnum);
				}
			}
			break;
		case 1: // fire
			if (plr.currweaponattackstyle == 0) {
				if (game.WH2) {
					if (plr.weapon[plr.currweapon] == 1 || plr.weapon[7] == 2) {
						dax = weaponanimtics[plr.currweapon][plr.currweaponanim].currx + 8;
						day = weaponanimtics[plr.currweapon][plr.currweaponanim].curry;
					} else {
						dax = zweaponanimtics[plr.currweapon][plr.currweaponanim].currx + 8;
						day = zweaponanimtics[plr.currweapon][plr.currweaponanim].curry;
					}
				} else {
					dax = weaponanimtics[plr.currweapon][plr.currweaponanim].currx;
					day = weaponanimtics[plr.currweapon][plr.currweaponanim].curry + 4;
				}
			} else {
				if (game.WH2) {
					if (plr.weapon[plr.currweapon] == 1 || plr.weapon[7] == 2) {
						dax = weaponanimtics2[plr.currweapon][plr.currweaponanim].currx + 8;
						day = weaponanimtics2[plr.currweapon][plr.currweaponanim].curry;
					} else {
						dax = zweaponanimtics2[plr.currweapon][plr.currweaponanim].currx + 8;
						day = zweaponanimtics2[plr.currweapon][plr.currweaponanim].curry;
					}
				} else {
					dax = weaponanimtics2[plr.currweapon][plr.currweaponanim].currx;
					day = weaponanimtics2[plr.currweapon][plr.currweaponanim].curry + 4;
				}
			}

			if (plr.currweapon == 0 && plr.currweaponframe != 0) {
				if (dahand == 1) {
					overwritesprite(dax, day, plr.currweaponframe, dashade, dabits, dapalnum);
				} else if (dahand == 2) {
					dax = lefthandanimtics[0][plr.currweaponanim].currx;
					day = lefthandanimtics[0][plr.currweaponanim].curry + 8;
					overwritesprite(dax, day + 5, plr.currweaponframe + 6, dashade, dabits, dapalnum);
				}
			} else {
				if (plr.currweaponframe != 0) {
					if (plr.currweaponattackstyle == 0) {
						if (game.WH2) {
							// flip
							if (plr.weapon[plr.currweapon] == 1 || plr.weapon[7] == 2)
								dax = weaponanimtics[plr.currweapon][plr.currweaponanim].currx;
							else
								dax = zweaponanimtics[plr.currweapon][plr.currweaponanim].currx;
						} else
							dax = weaponanimtics[plr.currweapon][plr.currweaponanim].currx;
					} else {
						if (game.WH2) {
							if (plr.weapon[plr.currweapon] == 1 || plr.weapon[7] == 2)
								dax = weaponanimtics2[plr.currweapon][plr.currweaponanim].currx;
							else
								dax = zweaponanimtics2[plr.currweapon][plr.currweaponanim].currx;
						} else
							dax = weaponanimtics2[plr.currweapon][plr.currweaponanim].currx;
					}
					overwritesprite(dax, day, plr.currweaponframe, dashade, dabits, dapalnum);
				}
			}
			break;

		case 0: // walking
			if ((plr.pInput.fvel|plr.pInput.svel) != 0) {
				if (plr.currweaponframe == BOWREADYEND) {
					if (game.WH2) {
						if (plr.weapon[plr.currweapon] == 1) {
							day = readyanimtics[plr.currweapon][6].curry + snakey + 8;
							dax = readyanimtics[plr.currweapon][6].currx + snakex + 8;
						} else {
							day = zreadyanimtics[plr.currweapon][6].curry + snakey + 8;
							dax = zreadyanimtics[plr.currweapon][6].currx + snakex + 8;
						}
					} else {
						day = readyanimtics[plr.currweapon][6].curry + snakey + 8;
						dax = readyanimtics[plr.currweapon][6].currx + snakex + 8;
					}
				} else {
					if (game.WH2) {
						if (plr.weapon[plr.currweapon] == 1 || plr.weapon[7] == 2) {
							day = weaponanimtics[plr.currweapon][0].curry + snakey + 8;
							dax = weaponanimtics[plr.currweapon][0].currx + snakex + 8;
						} else {
							day = zweaponanimtics[plr.currweapon][0].curry + snakey + 8;
							dax = zweaponanimtics[plr.currweapon][0].currx + snakex + 8;
						}
					} else {
						day = weaponanimtics[plr.currweapon][0].curry + snakey + 8;
						dax = weaponanimtics[plr.currweapon][0].currx + snakex + 8;
					}
				}
			} else {
				if (game.WH2) {
					if (plr.currweaponframe == BOWREADYEND) {
						if (plr.weapon[plr.currweapon] == 1) {
							day = readyanimtics[plr.currweapon][6].curry + 3;
							dax = readyanimtics[plr.currweapon][6].currx + 3;
						} else {
							day = zreadyanimtics[plr.currweapon][6].curry + 3;
							dax = zreadyanimtics[plr.currweapon][6].currx + 3;
						}
					} else {
						if (plr.weapon[plr.currweapon] == 1 || plr.weapon[7] == 2) {
							dax = weaponanimtics[plr.currweapon][0].currx + 3;
							day = weaponanimtics[plr.currweapon][0].curry + 3;
						} else {
							dax = zweaponanimtics[plr.currweapon][0].currx + 3;
							day = zweaponanimtics[plr.currweapon][0].curry + 3;
						}
					}
				} else {
					if (plr.currweaponframe == BOWREADYEND) {

						day = readyanimtics[plr.currweapon][6].curry + 3;
						dax = readyanimtics[plr.currweapon][6].currx + 3;
					} else {
						dax = weaponanimtics[plr.currweapon][0].currx + 3;
						day = weaponanimtics[plr.currweapon][0].curry + 3;
					}
				}
			}

			if (plr.currweapon == 0 && plr.currweaponframe != 0) {
				overwritesprite(dax, day, plr.currweaponframe, dashade, dabits, dapalnum);
				overwritesprite(0, day + 8, plr.currweaponframe + 6, dashade, dabits, dapalnum);
			} else if (plr.currweaponframe != 0)
				overwritesprite(dax + snakex, day, plr.currweaponframe, dashade, dabits, dapalnum);
			break;
		case 2: // unready
			if (game.WH2) {
				if (plr.currweaponframe == BOWREADYEND) {
					day = readyanimtics[plr.currweapon][6].curry + (weapondrop);
					dax = readyanimtics[plr.currweapon][6].currx;
				} else if (plr.currweaponframe == ZBOWWALK) {
					day = zreadyanimtics[plr.currweapon][6].curry + (weapondrop);
					dax = zreadyanimtics[plr.currweapon][6].currx;
				} else {
					if (plr.weapon[plr.currweapon] == 1 || plr.weapon[7] == 2) {
						dax = weaponanimtics[plr.currweapon][0].currx;
						day = weaponanimtics[plr.currweapon][0].curry + (weapondrop);
					} else {
						dax = zweaponanimtics[plr.currweapon][0].currx;
						day = zweaponanimtics[plr.currweapon][0].curry + (weapondrop);
					}
				}
			} else {
				if (plr.currweaponframe == BOWREADYEND) {
					day = readyanimtics[plr.currweapon][6].curry + (weapondrop);
					dax = readyanimtics[plr.currweapon][6].currx;
				} else {
					dax = weaponanimtics[plr.currweapon][0].currx;
					day = weaponanimtics[plr.currweapon][0].curry + (weapondrop);
				}
			}

			if (plr.currweapon == 0 && plr.currweaponframe != 0) {
				overwritesprite(dax, day, plr.currweaponframe, dashade, dabits, dapalnum);
				overwritesprite(0, day, plr.currweaponframe + 6, dashade, dabits, dapalnum);
			} else if (plr.currweaponframe != 0) {
				dax = weaponanimtics[plr.currweapon][0].currx;
				overwritesprite(dax, day, plr.currweaponframe, dashade, dabits, dapalnum);
			}
			break;
		case 3: // ready
			if (game.WH2) {
				if (plr.weapon[plr.currweapon] == 1 || plr.weapon[7] == 2) {
					dax = readyanimtics[plr.currweapon][plr.currweaponanim].currx;
					day = readyanimtics[plr.currweapon][plr.currweaponanim].curry + 8;
				} else {
					dax = zreadyanimtics[plr.currweapon][plr.currweaponanim].currx;
					day = zreadyanimtics[plr.currweapon][plr.currweaponanim].curry + 8;
				}
			} else {
				dax = readyanimtics[plr.currweapon][plr.currweaponanim].currx;
				day = readyanimtics[plr.currweapon][plr.currweaponanim].curry + 8;
			}

			if (plr.currweapon == 0 && plr.currweaponframe != 0) {
				overwritesprite(dax, day, plr.currweaponframe, dashade, dabits, dapalnum);
				overwritesprite(0, day, plr.currweaponframe + 6, dashade, dabits, dapalnum);
			} else if (plr.currweaponframe != 0)
				overwritesprite(dax, day, plr.currweaponframe, dashade, dabits, dapalnum);
			break;
		case 5: // cock
			if (game.WH2) {
				if (plr.weapon[plr.currweapon] == 1 || plr.weapon[7] == 2) {
					dax = cockanimtics[plr.currweaponanim].currx;
					day = cockanimtics[plr.currweaponanim].curry + 8;
				} else {
					if (plr.weapon[plr.currweapon] == 3) {
						dax = zcockanimtics[plr.currweaponanim].currx;
						day = zcockanimtics[plr.currweaponanim].curry + 8;
					} else {
						dax = zcockanimtics[plr.currweaponanim].currx;
						day = zcockanimtics[plr.currweaponanim].curry + 8;
					}
				}
			} else {
				dax = cockanimtics[plr.currweaponanim].currx;
				day = cockanimtics[plr.currweaponanim].curry + 8;
			}
			if (plr.currweaponframe != 0)
				overwritesprite(dax, day, plr.currweaponframe, dashade, dabits, dapalnum);
			break;
		case 4: // throw the orb
			if (game.WH2) {
				dax = wh2throwanimtics[plr.currentorb][plr.currweaponanim].currx;
				day = wh2throwanimtics[plr.currentorb][plr.currweaponanim].curry + 8;
			} else {
				dax = throwanimtics[plr.currentorb][plr.currweaponanim].currx;
				day = throwanimtics[plr.currentorb][plr.currweaponanim].curry + 8;
			}
			if (plr.currweaponframe != 0)
				overwritesprite(dax, day, plr.currweaponframe, dashade, dabits, dapalnum);
			break;
		}

		// shield stuff

		if (plr.shieldpoints > 0 && (plr.currweaponfired == 0 || plr.currweaponfired == 1) && plr.selectedgun > 0
				&& plr.selectedgun < 5) {
			if (plr.shieldtype == 1) {
				if (plr.shieldpoints > 75) {
					overwritesprite(-40 + snakex, 100 + snakey, GRONSHIELD, dashade, dabits, dapalnum);
				} else if (plr.shieldpoints > 50 && plr.shieldpoints < 76) {
					overwritesprite(-40 + snakex, 100 + snakey, GRONSHIELD + 1, dashade, dabits, dapalnum);
				} else if (plr.shieldpoints > 25 && plr.shieldpoints < 51) {
					overwritesprite(-40 + snakex, 100 + snakey, GRONSHIELD + 2, dashade, dabits, dapalnum);
				} else {
					overwritesprite(-40 + snakex, 100 + snakey, GRONSHIELD + 3, dashade, dabits, dapalnum);
				}
			} else {
				if (plr.shieldpoints > 150) {
					overwritesprite(-40 + snakex, 100 + snakey, ROUNDSHIELD, dashade, dabits, dapalnum);
				} else if (plr.shieldpoints > 100 && plr.shieldpoints < 151) {
					overwritesprite(-40 + snakex, 100 + snakey, ROUNDSHIELD + 1, dashade, dabits, dapalnum);
				} else if (plr.shieldpoints > 50 && plr.shieldpoints < 101) {
					overwritesprite(-40 + snakex, 100 + snakey, ROUNDSHIELD + 2, dashade, dabits, dapalnum);
				} else {
					overwritesprite(-40 + snakex, 100 + snakey, ROUNDSHIELD + 3, dashade, dabits, dapalnum);
				}
			}
		}
	}

	public static void analizesprites(PLAYER plr, int dasmoothratio) {
		int k;
		tspritelistcnt = spritesortcnt;
		for (int i = spritesortcnt - 1; i >= 0; i--) {
			SPRITE tspr = tsprite[i];
			if(tspritelist[i] == null)
				tspritelist[i] = new SPRITE();
			tspritelist[i].set(tspr);

			if (plr.nightglowtime <= 0 && tspr.detail != 0 && tspr.detail < MAXTYPES) { //enemies' detail only
				if(((tspr.detail != GONZOTYPE && tspr.picnum != GONZOGSHDEAD) || tspr.shade != 31) && tspr.statnum != EVILSPIRIT)
					tspr.shade = sector[tspr.sectnum].floorshade;
			}

			ILoc oldLoc = game.pInt.getsprinterpolate(tspr.owner);
			// only interpolate certain moving things
			if (oldLoc != null && (tspr.hitag & 0x0200) == 0) {
				int x = oldLoc.x;
				int y = oldLoc.y;
				int z = oldLoc.z;
				short nAngle = oldLoc.ang;

				// interpolate sprite position
				x += mulscale(tspr.x - oldLoc.x, dasmoothratio, 16);
				y += mulscale(tspr.y - oldLoc.y, dasmoothratio, 16);
				z += mulscale(tspr.z - oldLoc.z, dasmoothratio, 16);
				nAngle += mulscale(((tspr.ang - oldLoc.ang + 1024) & 2047) - 1024, dasmoothratio, 16);

				tspr.x = x;
				tspr.y = y;
				tspr.z = z;
				tspr.ang = nAngle;
			}

			switch (sprite[tspr.owner].detail) {
			case GRONTYPE:
				if (tspr.picnum == GRONHAL || tspr.picnum == GRONSW || tspr.picnum == GRONSWATTACK
						|| tspr.picnum == GRONMU) {
					k = engine.getangle(tspr.x - plr.x, tspr.y - plr.y);
					k = (((tspr.ang + 3072 + 128 - k) & 2047) >> 8) & 7;
					if (k <= 4) {
						tspr.picnum += (k << 2);
						tspr.cstat &= ~4; // clear x-flipping bit
					} else {
						tspr.picnum += ((8 - k) << 2);
						tspr.cstat |= 4; // set x-flipping bit
					}
				} else {
					switch (tspr.picnum) {
					case WH1GRONMUATTACK:
					case WH2GRONMUATTACK:
						if (tspr.picnum != GRONMUATTACK)
							break;
						k = engine.getangle(tspr.x - plr.x, tspr.y - plr.y);
						k = (((tspr.ang + 3072 + 128 - k) & 2047) >> 8) & 7;
						if (k <= 4) {
							tspr.picnum += (k * 6);
							tspr.cstat &= ~4; // clear x-flipping bit
						} else {
							tspr.picnum += ((8 - k) * 6);
							tspr.cstat |= 4; // set x-flipping bit
						}
						break;
					case WH1GRONHALATTACK:
					case WH2GRONHALATTACK:
						if (tspr.picnum != GRONHALATTACK)
							break;
						k = engine.getangle(tspr.x - plr.x, tspr.y - plr.y);
						k = (((tspr.ang + 3072 + 128 - k) & 2047) >> 8) & 7;
						if (k <= 4) {
							tspr.picnum += (k * 7);
							tspr.cstat &= ~4; // clear x-flipping bit
						} else {
							tspr.picnum += ((8 - k) * 7);
							tspr.cstat |= 4; // set x-flipping bit
						}
						break;
					}
				}

				continue;
			case GOBLINTYPE:
				if (game.WH2)
					continue;

				switch (tspr.picnum) {
				case GOBLINSTAND:
				case GOBLIN:
				case GOBLINATTACK:
					k = engine.getangle(tspr.x - plr.x, tspr.y - plr.y);
					k = (((tspr.ang + 3072 + 128 - k) & 2047) >> 8) & 7;
					if (k <= 4) {
						tspr.picnum += (k << 2);
						tspr.cstat &= ~4; // clear x-flipping bit
					} else {
						tspr.picnum += ((8 - k) << 2);
						tspr.cstat |= 4; // set x-flipping bit
					}
					break;
				}
				continue;
			case IMPTYPE:
				if (!game.WH2)
					continue;

				k = engine.getangle(tspr.x - plr.x, tspr.y - plr.y);
				k = (((tspr.ang + 3072 + 128 - k) & 2047) >> 8) & 7;
				if (tspr.picnum == IMP) {
					if (k <= 4) {
						tspr.picnum += (k * 6);
						tspr.cstat &= ~4; // clear x-flipping bit
					} else {
						tspr.picnum += ((8 - k) * 6);
						tspr.cstat |= 4; // set x-flipping bit
					}
				}
				continue;
			}

			switch (tspr.picnum) {
			case MONSTERBALL:
			case EXPLOSION:
			case PLASMA:
			case ICECUBE:
			case BULLET:
			case DISTORTIONBLAST:
			case WH1WILLOW:
			case WH2WILLOW:
				if ((tspr.picnum == WH1WILLOW && game.WH2) || (tspr.picnum == WH2WILLOW && !game.WH2))
					break;
				tspr.shade = -128;
				break;
			case DEVILSTAND:
			case DEVIL:
			case DEVILATTACK:
			case SKULLY:
			case FATWITCH:
			case JUDY:
			case FREDSTAND:
			case FRED:
			case FREDATTACK:
			case MINOTAUR:
			case MINOTAURATTACK:
				k = engine.getangle(tspr.x - plr.x, tspr.y - plr.y);
				k = (((tspr.ang + 3072 + 128 - k) & 2047) >> 8) & 7;
				if (k <= 4) {
					tspr.picnum += (k << 2);
					tspr.cstat &= ~4; // clear x-flipping bit
				} else {
					tspr.picnum += ((8 - k) << 2);
					tspr.cstat |= 4; // set x-flipping bit
				}
				break;
			case WH2SKELETON:
			case WH2SKELETONATTACK:
				if (game.WH2) {
					k = engine.getangle(tspr.x - plr.x, tspr.y - plr.y);
					k = (((tspr.ang + 3072 + 128 - k) & 2047) >> 8) & 7;
					if (k <= 4) {
						tspr.picnum += (k * 6);
						tspr.cstat &= ~4; // clear x-flipping bit
					} else {
						tspr.picnum += ((8 - k) * 6);
						tspr.cstat |= 4; // set x-flipping bit
					}
				}
				break;
			case WH1SKELETON:
			case WH1SKELETONATTACK:
			case KOBOLD:
			case KURTAT:
				k = engine.getangle(tspr.x - plr.x, tspr.y - plr.y);
				k = (((tspr.ang + 3072 + 128 - k) & 2047) >> 8) & 7;
				if (k <= 4) {
					tspr.picnum += (k * 5);
					tspr.cstat &= ~4; // clear x-flipping bit
				} else {
					tspr.picnum += ((8 - k) * 5);
					tspr.cstat |= 4; // set x-flipping bit
				}
				break;

			case KATIE:
			case KATIEAT:

				k = engine.getangle(tspr.x - plr.x, tspr.y - plr.y);
				k = (((tspr.ang + 3072 + 128 - k) & 2047) >> 8) & 7;
				if (k <= 4) {
					tspr.picnum += (k * 5);
					// tspr.cstat &= ~4; //clear x-flipping bit
					tspr.cstat |= 4; // set x-flipping bit
				} else {
					tspr.picnum += ((8 - k) * 5);
					// tspr.cstat |= 4; //set x-flipping bit
					tspr.cstat &= ~4; // clear x-flipping bit
				}
				break;

			case NEWGUY:
			case NEWGUYBOW:
			case NEWGUYMACE:

			case GONZOCSW:
			case GONZOCSWAT:
				k = engine.getangle(tspr.x - plr.x, tspr.y - plr.y);
				k = (((tspr.ang + 3072 + 128 - k) & 2047) >> 8) & 7;
				if (k <= 4) {
					tspr.picnum += (k * 6);
					tspr.cstat &= ~4; // clear x-flipping bit
				} else {
					tspr.picnum += ((8 - k) * 6);
					tspr.cstat |= 4; // set x-flipping bit
				}
				break;

			case GONZOGSW:
			case GONZOGHM:
				k = engine.getangle(tspr.x - plr.x, tspr.y - plr.y);
				k = (((tspr.ang + 3072 + 128 - k) & 2047) >> 8) & 7;
				if (k <= 4) {
					tspr.picnum += (k * 6);
					// tspr.cstat &= ~4; //clear x-flipping bit
					tspr.cstat |= 4; // set x-flipping bit
				} else {
					tspr.picnum += ((8 - k) * 6);
					// tspr.cstat |= 4; //set x-flipping bit
					tspr.cstat &= ~4; // clear x-flipping bit

				}
				break;

			case GONZOGSH:
				k = engine.getangle(tspr.x - plr.x, tspr.y - plr.y);
				k = (((tspr.ang + 3072 + 128 - k) & 2047) >> 8) & 7;

				tspr.picnum += (k * 6);
				tspr.cstat |= 4; // set x-flipping bit
				break;

			case NEWGUYCAST:
			case NEWGUYPUNCH:
			case KURTPUNCH:
				k = engine.getangle(tspr.x - plr.x, tspr.y - plr.y);
				k = (((tspr.ang + 3072 + 128 - k) & 2047) >> 8) & 7;
				if (k <= 4) {
					tspr.picnum += (k * 3);
					tspr.cstat &= ~4; // clear x-flipping bit
				} else {
					tspr.picnum += ((8 - k) * 3);
					tspr.cstat |= 4; // set x-flipping bit
				}
				break;

			case WH1RAT:
			case WH2RAT:
				if ((tspr.picnum == WH1RAT && game.WH2) || (tspr.picnum == WH2RAT && !game.WH2))
					break;
				k = engine.getangle(tspr.x - plr.x, tspr.y - plr.y);
				k = (((tspr.ang + 3072 + 128 - k) & 2047) >> 8) & 7;
				if (k <= 4) {
					tspr.picnum += (k * 2);
					tspr.cstat &= ~4; // clear x-flipping bit
				} else {
					tspr.picnum += ((8 - k) * 2);
					tspr.cstat |= 4; // set x-flipping bit
				}
				break;
			case SPIDER:
				k = engine.getangle(tspr.x - plr.x, tspr.y - plr.y);
				k = (((tspr.ang + 3072 + 128 - k) & 2047) >> 8) & 7;
				if (k <= 4) {
					tspr.picnum += (k << 3);
					tspr.cstat &= ~4; // clear x-flipping bit
				} else {
					tspr.picnum += ((8 - k) << 3);
					tspr.cstat |= 4; // set x-flipping bit
				}
				break;
			case NEWGUYSTAND:
			case NEWGUYKNEE:
			case KURTSTAND:
			case KURTKNEE:
			case WH1GUARDIAN:
			case WH2GUARDIAN:
				if ((tspr.picnum == WH1GUARDIAN && game.WH2) || (tspr.picnum == WH2GUARDIAN && !game.WH2))
					break;

				k = engine.getangle(tspr.x - plr.x, tspr.y - plr.y);
				k = (((tspr.ang + 3072 + 128 - k) & 2047) >> 8) & 7;
				if (k <= 4) {
					tspr.picnum += k;
					tspr.cstat &= ~4; // clear x-flipping bit
				} else {
					tspr.picnum += (8 - k);
					tspr.cstat |= 4; // set x-flipping bit
				}
				break;

			// ITEMS

			case SILVERBAG:
			case SILVERCOINS:
			case GOLDBAG:
			case GOLDBAG2:
			case GOLDCOINS:
			case GOLDCOINS2:
			case GIFTBOX:
			case FLASKBLUE:
			case FLASKRED:
			case FLASKGREEN:
			case FLASKOCHRE:
			case FLASKTAN:
			case DIAMONDRING:
			case SHADOWAMULET:
			case GLASSSKULL:
			case AHNK:
			case BLUESCEPTER:
			case YELLOWSCEPTER:
			case ADAMANTINERING:
			case ONYXRING:
			case SAPHIRERING:
			case WALLBOW:
			case WALLSWORD:
			case WEAPON3A:
			case WEAPON3:
			case WALLAXE:
			case GONZOBSHIELD:
			case GONZOCSHIELD:
			case GONZOGSHIELD:
			case WALLPIKE:
				tspr.shade -= 16;
				break;
			default:
				int p = tspr.picnum;
				if (p == CRYSTALSTAFF || p == AMULETOFTHEMIST || p == HORNEDSKULL || p == THEHORN || p == HELMET
						|| p == PLATEARMOR || p == CHAINMAIL || p == LEATHERARMOR || p == BRASSKEY || p == BLACKKEY
						|| p == GLASSKEY || p == IVORYKEY || p == SCROLLSCARE || p == SCROLLNIGHT || p == SCROLLFREEZE
						|| p == SCROLLMAGIC || p == SCROLLOPEN || p == SCROLLFLY || p == SCROLLFIREBALL
						|| p == SCROLLNUKE || p == QUIVER || p == BOW || p == WEAPON1 || p == WEAPON1A || p == GOBWEAPON
						|| p == WEAPON2 || p == WEAPON4 || p == THROWHALBERD || p == WEAPON5 || p == SHIELD
						|| p == WEAPON5B || p == THROWPIKE || p == WEAPON6 || p == WEAPON7 || p == PENTAGRAM) {
					tspr.shade -= 16;
					if (p == PENTAGRAM) {
						if (sector[tspr.sectnum].lotag == 4002 && plr.treasure[TPENTAGRAM] == 0)
							tspr.cstat |= 514;
					}
				}
				break;
			}

			if(tspr.detail != 0 && tspr.statnum != 99 && (tspr.detail & 0xFF) != SPIKEBLADETYPE) {
				if (spritesortcnt < (MAXSPRITESONSCREEN - 2)) {
					int fz = engine.getflorzofslope(tspr.sectnum, tspr.x, tspr.y);
					if (fz > plr.z) {
						short siz = (short) Math.max((tspr.xrepeat - ((fz - tspr.z) >> 10)), 1);
						if(siz > 4) {
							if (tsprite[spritesortcnt] == null)
								tsprite[spritesortcnt] = new SPRITE();
							SPRITE tshadow = tsprite[spritesortcnt];
							tshadow.set(tspr);
							int camangle = engine.getangle(plr.x - tshadow.x, plr.y - tshadow.y);
							tshadow.x -= mulscale(sintable[(camangle + 512) & 2047], 100, 16);
							tshadow.y += mulscale(sintable[(camangle + 1024) & 2047], 100, 16);
							tshadow.z = fz + 1;
							tshadow.statnum = 99;

							tshadow.xrepeat = siz;
							tshadow.yrepeat = (short) (tspr.yrepeat >> 3);
							if (tshadow.yrepeat < 4)
								tshadow.yrepeat = 4;

							tshadow.shade = 127;
							tshadow.cstat |= 2;
							spritesortcnt++;
						}
					}
				}
			}
		}
	}

	public static void drawInterface(PLAYER plr) {
		int hudscale = whcfg.gHudScale;
		if (whcfg.gViewSize == 1)
			drawhud(plr, windowx2 / 2, windowy2 + 1, hudscale);
		if (plr.potion[0] == 0 && plr.health > 0 && plr.health < 21)
			game.getFont(1).drawText(160, 5, toCharArray("health critical"), 0, 7, TextAlign.Center, 2, false);

		if (justwarpedfx > 0)
			engine.rotatesprite(320 << 15, 200 << 15, justwarpedcnt << 9, 0, ANNIHILATE, 0, 0, 1 + 2, 0, 0, xdim - 1,
					ydim - 1);

		int pwpos = 0;

		if (plr.helmettime > 0)
		{
			engine.rotatesprite(300 << 16, (pwpos += engine.getTile(HELMET).getHeight() >> 2) << 16, 16384, 0, HELMET, 0, 0, 2 + 8 + 512, 0, 0,
					xdim - 1, ydim - 1);
			pwpos += 10;
		}

		if(plr.vampiretime > 0)
		{
			engine.rotatesprite(300 << 16, (pwpos += engine.getTile(THEHORN).getHeight() / 6) << 16, 12000, 0, THEHORN, 0, 0, 2 + 8 + 512, 0, 0,
					xdim - 1, ydim - 1);
			pwpos += 10;
		}

		if(plr.orbactive[5] > 0)
		{
			engine.rotatesprite(300 << 16, (pwpos += engine.getTile(SCROLLFLY).getHeight() >> 2) << 16, 16384, 0, SCROLLFLY, 0, 0, 2 + 8 + 512, 0, 0,
					xdim - 1, ydim - 1);
			pwpos += 10;
		}

		if(plr.shadowtime > 0)
		{
			engine.rotatesprite(300 << 16, (pwpos += engine.getTile(SCROLLSCARE).getHeight() >> 2) << 16, 16384, 0, SCROLLSCARE, 0, 0, 2 + 8 + 512, 0, 0,
					xdim - 1, ydim - 1);
			pwpos += 10;
		}

		if(plr.nightglowtime > 0)
		{
			engine.rotatesprite(300 << 16, (pwpos += engine.getTile(SCROLLNIGHT).getHeight() >> 2) << 16, 16384, 0, SCROLLNIGHT, 0, 0, 2 + 8 + 512, 0, 0,
					xdim - 1, ydim - 1);
			pwpos += 10;
		}

		boolean message = whcfg.MessageState && displaytime > 0;

		if (message)
			game.getFont(1).drawText(5, 5, displaybuf, 0, 7, TextAlign.Left, 2 | 256, false);

		int amposx = 10;
		int amposy = 7;
		if(message) {
			amposy += 15;
			if(dimension == 2)
				amposy += 10;
			if(dimension == 2 && followmode)
				amposy += 10;
		}

		if(plr.treasure[TONYXRING] != 0)
		{
			engine.rotatesprite(amposx << 16, amposy << 16, 16384, 0, ONYXRING, 0, 0, 2 | 8 | 256, 0, 0,
					xdim - 1, ydim - 1);
			amposx += 20;
		}

		if(plr.treasure[TAMULETOFTHEMIST] != 0 && plr.invisibletime > 0)
		{
			engine.rotatesprite(amposx << 16, amposy << 16, 16384, 0, AMULETOFTHEMIST, 0, 0, 2 | 8 | 256, 0, 0,
					xdim - 1, ydim - 1);
			amposx += 20;
		}

		if(plr.treasure[TADAMANTINERING] != 0)
		{
			engine.rotatesprite(amposx << 16, amposy << 16, 16384, 0, ADAMANTINERING, 0, 0, 2 | 8 | 256, 0, 0,
					xdim - 1, ydim - 1);
			amposx += 20;
		}

		if(plr.treasure[TBLUESCEPTER] != 0)
		{
			engine.rotatesprite(amposx << 16, (amposy + 9) << 16, 16384, 0, BLUESCEPTER, 0, 0, 2 | 8 | 256, 0, 0,
					xdim - 1, Gameutils.coordsConvertYScaled(amposy + 4));
			amposx += 20;
		}

		if(plr.treasure[TYELLOWSCEPTER] != 0)
		{
			engine.rotatesprite(amposx << 16, (amposy + 9) << 16, 16384, 0, YELLOWSCEPTER
					, 0, 0, 2 | 8 | 256, 0, 0,
					xdim - 1, Gameutils.coordsConvertYScaled(amposy + 4));
			amposx += 20;
		}

		if (whcfg.gCrosshair) {
			int col = 17;
			engine.getrender().drawline256((xdim - mulscale(whcfg.gCrossSize, 16, 16)) << 11, ydim << 11,
					(xdim - mulscale(whcfg.gCrossSize, 4, 16)) << 11, ydim << 11, col);
			engine.getrender().drawline256((xdim + mulscale(whcfg.gCrossSize, 4, 16)) << 11, ydim << 11,
					(xdim + mulscale(whcfg.gCrossSize, 16, 16)) << 11, ydim << 11, col);
			engine.getrender().drawline256(xdim << 11, (ydim - mulscale(whcfg.gCrossSize, 16, 16)) << 11, xdim << 11,
					(ydim - mulscale(whcfg.gCrossSize, 4, 16)) << 11, col);
			engine.getrender().drawline256(xdim << 11, (ydim + mulscale(whcfg.gCrossSize, 4, 16)) << 11, xdim << 11,
					(ydim + mulscale(whcfg.gCrossSize, 16, 16)) << 11, col);
		}

		if (plr.spiked != 0)
			spikeanimation(plr);

		int y = windowy2 - 20;
		if (whcfg.gViewSize == 1)
			y -= mulscale(engine.getTile(SSTATUSBAR).getHeight(), hudscale, 16);

		if (whcfg.gShowStat == 1 || (whcfg.gShowStat == 2 && dimension == 2))
			drawStatistics(10, y, whcfg.gStatSize);

		if (game.gPaused)
			game.getFont(0).drawText(160, 5, toCharArray("Pause"), 0, 0, TextAlign.Center, 2, true);
	}

	public static void drawStatistics(int x, int y, int zoom) {
		float viewzoom = (zoom / 65536.0f);
		BuildFont font = game.getFont(1);

		buildString(buffer, 0, "kills: ");

		int yoffset = (int) (4 * (font.getHeight()) * viewzoom);
		y -= yoffset;

		int statx = x;
		int staty = y;

		font.drawText(statx, staty, buffer, zoom, 0, 7, TextAlign.Left, 256, false);

		int alignx = font.getWidth(buffer, zoom);

		int offs = Bitoa(kills, buffer);
		offs = buildString(buffer, offs, " / ", killcnt);
		font.drawText(statx += (alignx + 2), staty, buffer, zoom, 0, 0, TextAlign.Left, 256, false);

		statx = x;
		staty = y + (int) (15 * viewzoom);

		buildString(buffer, 0, "treasures: ");

		font.drawText(statx, staty, buffer, zoom, 0, 7, TextAlign.Left, 256, false);

		alignx = font.getWidth(buffer, zoom);

		offs = Bitoa(treasuresfound, buffer, 2);
		offs = buildString(buffer, offs, " / ", treasurescnt, 2);

		font.drawText(statx += (alignx + 2), staty, buffer, zoom, 0, 0, TextAlign.Left, 256, false);

		statx = x;
		staty = y + (int) (30 * viewzoom);

		buildString(buffer, 0, "time: ");

		font.drawText(statx, staty, buffer, zoom, 0, 7, TextAlign.Left, 256, false);
		alignx = font.getWidth(buffer, zoom);

		offs = Bitoa(minutes, buffer, 2);
		offs = buildString(buffer, offs, " : ", seconds, 2);

		font.drawText(statx += (alignx + 2), staty, buffer, zoom, 0, 0, TextAlign.Left, 256, false);
	}

	public static void drawhud(PLAYER plr, int x, int y, int scale) {

		engine.rotatesprite(x << 16, (y << 16) - engine.getTile(SSTATUSBAR).getHeight() * scale / 2, scale, 0, SSTATUSBAR, 0, 0, 8, 0, 0,
				xdim, ydim - 1);
		updatepics(plr, x, y, scale);

		int bookpic = plr.spellbook;
		if (plr.spellbookflip == 0)
			bookpic = 8;

		if (bookpic < sspellbookanim[plr.currentorb].length && (plr.orbammo[plr.currentorb] > 0 || plr.currweaponfired == 4)) {
			plr.spellbookframe = sspellbookanim[plr.currentorb][bookpic].daweaponframe;
			int dax = x + mulscale(sspellbookanim[plr.currentorb][bookpic].currx, scale, 16);
			int day = y + mulscale(sspellbookanim[plr.currentorb][bookpic].curry, scale, 16);

			engine.rotatesprite(dax << 16, day << 16, scale, 0, plr.spellbookframe, 0, 0, 8 | 16, 0, 0, xdim, ydim - 1);
			Bitoa(plr.orbammo[plr.currentorb], tempchar);
			game.getFont(4).drawText(x - mulscale(67, scale, 16), y - mulscale(39, scale, 16), tempchar, scale, 0, 0,
					TextAlign.Left, 0, false);
		}
	}

	public static int coordsConvertXScaled(int coord, int bits) {
		int oxdim = xdim;
		int xdim = (4 * ydim) / 3;
		int offset = oxdim - xdim;

		int normxofs = coord - (320 << 15);
		int wx = (xdim << 15) + scale(normxofs, xdim, 320);
		wx += (oxdim - xdim) / 2;

		if ((bits & 256) == 256)
			return wx - offset / 2 - 1;
		if ((bits & 512) == 512)
			return wx + offset / 2 - 1;

		return wx - 1;
	}

	public static int coordsConvertYScaled(int coord) {
		int ydim = (3 * xdim) / 4;
		int buildim = 200 * ydim / Engine.ydim;
		int normxofs = coord - (buildim << 15);
		int wy = (ydim << 15) + scale(normxofs, ydim, buildim);

		return wy;
	}
}
