package ru.m210projects.Tekwar;

import static com.badlogic.gdx.graphics.GL20.GL_DST_COLOR;
import static com.badlogic.gdx.graphics.GL20.GL_ONE_MINUS_SRC_ALPHA;
import static java.lang.Math.abs;
import static ru.m210projects.Build.Engine.CEIL;
import static ru.m210projects.Build.Engine.FLOOR;
import static ru.m210projects.Build.Engine.palette;
import static ru.m210projects.Build.Engine.windowx1;
import static ru.m210projects.Build.Engine.windowx2;
import static ru.m210projects.Build.Engine.windowy1;
import static ru.m210projects.Build.Engine.windowy2;
import static ru.m210projects.Build.Engine.xdim;
import static ru.m210projects.Build.Engine.ydim;
import static ru.m210projects.Build.Gameutils.BClampAngle;
import static ru.m210projects.Build.Gameutils.BClipHigh;
import static ru.m210projects.Build.Gameutils.BClipLow;
import static ru.m210projects.Build.Net.Mmulti.myconnectindex;
import static ru.m210projects.Build.Net.Mmulti.numplayers;
import static ru.m210projects.Build.Pragmas.divscale;
import static ru.m210projects.Build.Pragmas.mulscale;
import static ru.m210projects.Build.Strhandler.Bitoa;
import static ru.m210projects.Build.Strhandler.buildString;
import static ru.m210projects.Build.Strhandler.toCharArray;
import static ru.m210projects.Tekwar.Config.TOGGLE_HEALTH;
import static ru.m210projects.Tekwar.Config.TOGGLE_INVENTORY;
import static ru.m210projects.Tekwar.Config.TOGGLE_REARVIEW;
import static ru.m210projects.Tekwar.Config.TOGGLE_SCORE;
import static ru.m210projects.Tekwar.Config.TOGGLE_TIME;
import static ru.m210projects.Tekwar.Config.TOGGLE_UPRT;
import static ru.m210projects.Tekwar.Globals.MAXAMMO;
import static ru.m210projects.Tekwar.Globals.MAXHEALTH;
import static ru.m210projects.Tekwar.Globals.MAXTOGGLES;
import static ru.m210projects.Tekwar.Globals.TICSPERFRAME;
import static ru.m210projects.Tekwar.Globals.notininventory;
import static ru.m210projects.Tekwar.Main.TEKDEMO;
import static ru.m210projects.Tekwar.Main.engine;
import static ru.m210projects.Tekwar.Main.game;
import static ru.m210projects.Tekwar.Main.hours;
import static ru.m210projects.Tekwar.Main.mUserFlag;
import static ru.m210projects.Tekwar.Main.minutes;
import static ru.m210projects.Tekwar.Main.screenpeek;
import static ru.m210projects.Tekwar.Main.seconds;
import static ru.m210projects.Tekwar.Main.tekcfg;
import static ru.m210projects.Tekwar.Names.ALPHABET;
import static ru.m210projects.Tekwar.Names.ALPHABET2;
import static ru.m210projects.Tekwar.Names.BLUELIGHTPIC;
import static ru.m210projects.Tekwar.Names.ES1_VGA;
import static ru.m210projects.Tekwar.Names.ES2_VGA;
import static ru.m210projects.Tekwar.Names.GREENLIGHTPIC;
import static ru.m210projects.Tekwar.Names.HCDEVICE;
import static ru.m210projects.Tekwar.Names.HCDEVICEON;
import static ru.m210projects.Tekwar.Names.HELPSCREEN4801;
import static ru.m210projects.Tekwar.Names.HELPSCREEN4802;
import static ru.m210projects.Tekwar.Names.HELPSCREENPIC;
import static ru.m210projects.Tekwar.Names.RVDEVICE;
import static ru.m210projects.Tekwar.Names.RVDEVICEON;
import static ru.m210projects.Tekwar.Names.SYMBOL1PIC;
import static ru.m210projects.Tekwar.Names.WPDEVICE;
import static ru.m210projects.Tekwar.Names.WPDEVICEON;
import static ru.m210projects.Tekwar.Names.YELLOWLIGHTPIC;
import static ru.m210projects.Tekwar.Player.gPlayer;
import static ru.m210projects.Tekwar.Tekchng.stun;
import static ru.m210projects.Tekwar.Tekgun.tekdrawgun;
import static ru.m210projects.Tekwar.Tekmap.symbols;
import static ru.m210projects.Tekwar.Tekmap.symbolsdeposited;
import static ru.m210projects.Tekwar.Teksnd.updatesounds;
import static ru.m210projects.Tekwar.Tekspr.analyzesprites;
import static ru.m210projects.Tekwar.Tektag.headbob;

import java.util.Arrays;

import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Build.Pattern.BuildFont.TextAlign;
import ru.m210projects.Build.Pattern.BuildGame.NetMode;
import ru.m210projects.Build.Render.GLRenderer.GLInvalidateFlag;
import ru.m210projects.Build.Render.Types.FadeEffect;
import ru.m210projects.Build.Settings.BuildSettings;
import ru.m210projects.Build.Types.Tile;
import ru.m210projects.Tekwar.Main.UserFlag;
import ru.m210projects.Tekwar.Menus.MenuInterfaceSet;

public class View {

	public static final int kView2D = 2;
	public static final int kView3D = 3;
	public static final int kView2DIcon = 4;

	public static int smoothratio;
	public static int[] zofslope = new int[2];

	public static byte gViewMode;
	public static int zoom, ozoom;

	public static char rearviewdraw;
	public static int hcmoving, rvmoving, wpmoving;
	public static short hcpos, wppos, rvpos;

	public static boolean lasttimetoggle;
	public static boolean lastscoretoggle;
	public static boolean lastinvtoggle;
	public static boolean[] otoggles = new boolean[MAXTOGGLES];

	public static int timedinv;
	public static int lastwx2;
	public static long lastinvr, lastinvb, lastinvacc;

	public static char demowon = 0;
	public static char outofsync = 0;

	public static final int HCSCALE = 100;
	public static final int AMMOSCALE = 10;

	public static int messagex;
	public static int messageon = 0;
	static long lastsec;
	static long lastscore;
	static char[] numtmp = new char[100];
	public static int redcount, whitecount;

	public static char rvonemotime;
	public static char wponemotime;
	public static char hconemotime;

	public static final int NUMWHITESHIFTS = 3;
	public static final int WHITESTEPS = 20;
	public static final int WHITETICS = 6;
	public static final int NUMREDSHIFTS = 4;
	public static final int REDSTEPS = 8;

	public static boolean palshifted;
	public static byte[][] whiteshifts = new byte[NUMREDSHIFTS][768];
	public static byte[][] redshifts = new byte[NUMREDSHIFTS][768];

	public static final int TEKTEMPBUFSIZE = 256;
	private static char[] tektempbuf = new char[TEKTEMPBUFSIZE];

	public static final int MSGBUFSIZE = 40;
	public static char[] messagebuf = new char[MSGBUFSIZE];

	public static void overwritesprite(int thex, int they, int scale, int tilenum, int shade, int stat, int dapalnum) {

		engine.rotatesprite(thex << 16, they << 16, scale, (stat & 8) << 7, tilenum, shade, dapalnum,
				(((stat & 1) ^ 1) << 4) + (stat & 2) // 16 + 2
						+ ((stat & 4) >> 2) // 1
						+ (((stat & 16) >> 2) ^ ((stat & 8) >> 1)) + ((stat & 32) >> 2) + (stat & 512) + (stat & 256),
				windowx1, windowy1, windowx2, windowy2);
	}

	public static void printext(int x, int y, char[] buffer, int tilenum, int invisiblecol) {
		int scale = tekcfg.gHUDSize;
		int textsize = mulscale(8, scale, 16);

		for (int i = 0; i < buffer.length && buffer[i] != '\0'; i++) {
			char ch = buffer[i];

			float tx = (ch % 16) / 16f;
			float ty = (ch / 16) / 16f;

			int ctx = mulscale((int) (tx * 128), scale, 16);
			int cty = mulscale((int) (ty * 128), scale, 16);

			engine.rotatesprite((x - ctx) << 16, (y - cty) << 16, scale, 0, tilenum, 0, 0, 8 + 16, x, y,
					x + textsize - 1, y + textsize - 1);

//			engine.rotatesprite((x - ctx) << 16,
//					(y - cty) << 16, scale, 0, tilenum, 0, 0,
//					2 + 8 + 16, coordsConvertXScaled(x), coordsConvertYScaled(y), coordsConvertXScaled(x+textsize-1), coordsConvertYScaled(y+textsize-1));
			x += textsize;
		}
	}

	public static void FadeInit() {
		Console.Println("Initializing fade effects", 0);

		initpaletteshifts();

		engine.registerFade("DAMAGE", new FadeEffect(GL_DST_COLOR, GL_ONE_MINUS_SRC_ALPHA) {
			private int intensive;

			@Override
			public void update(int intensive) {
				this.intensive = intensive;
				if (intensive > 0) {
					r = 3 * (intensive + 32);
					a = 2 * (intensive + 32);
				} else
					r = a = 0;
				if (r > 255)
					r = 255;
				if (a > 255)
					a = 255;
			}

			@Override
			public void draw(FadeShader shader) {
				FadeEffect.setParams(shader, r, 0, 0, a, sfactor, dfactor);
				FadeEffect.render(shader);

				int multiple = intensive / 2;
				if (multiple > 170)
					multiple = 170;
				FadeEffect.setParams(shader, r > 0 ? multiple : 0, 0, 0, 0, GL_ONE_MINUS_SRC_ALPHA,
						GL_ONE_MINUS_SRC_ALPHA);
				FadeEffect.render(shader);
			}
		});

		engine.registerFade("PICKUP", new FadeEffect(GL_DST_COLOR, GL_ONE_MINUS_SRC_ALPHA) {
			private int intensive;

			@Override
			public void update(int intensive) {
				this.intensive = intensive;
				if (intensive > 0) {
					g = r = 4 * intensive;
					a = (intensive + 32);
				} else
					g = r = a = 0;

				if (r > 255)
					r = 255;
				if (g > 255)
					g = 255;
				if (a > 255)
					a = 255;
			}

			@Override
			public void draw(FadeShader shader) {
				FadeEffect.setParams(shader, r, g, b, a, sfactor, dfactor);
				FadeEffect.render(shader);

				if (intensive > 0) {
					int multiple = intensive;
					if (multiple > 255)
						multiple = 255;

					FadeEffect.setParams(shader, multiple, multiple, 0, 0, GL_ONE_MINUS_SRC_ALPHA,
							GL_ONE_MINUS_SRC_ALPHA);
					FadeEffect.render(shader);
				}
			}
		});

		engine.registerFade("FADE", new FadeEffect(GL_DST_COLOR, GL_DST_COLOR) {
			private int intensive;

			@Override
			public void update(int intensive) {
				this.intensive = intensive;
			}

			@Override
			public void draw(FadeShader shader) {
				if (intensive > 0) {
					int multiple = intensive;
					if (multiple > 255)
						multiple = 255;

					FadeEffect.setParams(shader, 0, 0, 0, multiple, GL_ONE_MINUS_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
					FadeEffect.render(shader);
				}
			}
		});
	}

	public static void resetEffects() {
		redcount = 0;
		whitecount = 0;
		engine.updateFade("DAMAGE", 0);
		engine.updateFade("PICKUP", 0);
		engine.updateFade("FADE", 0);
	}

	public static void showmessage(String fmt) {
		System.err.println(fmt);
		if (!tekcfg.showMessages)
			return;

		Arrays.fill(messagebuf, '\0');

		System.arraycopy(fmt.toCharArray(), 0, messagebuf, 0, Math.min(fmt.length(), MSGBUFSIZE));
		messagex = (xdim >> 1) - (mulscale((fmt.length() >> 1) * 8, tekcfg.gHUDSize, 16));
		if (messagex < 0)
			messagex = 0;

		messageon = 1;
	}

	public static void showtime() {
		if (!tekcfg.toggles[TOGGLE_TIME]) {
			lasttimetoggle = tekcfg.toggles[TOGGLE_TIME];
			lastsec = 0;
		} else {

			Bitoa(hours, tektempbuf, 2);
			int offs = buildString(tektempbuf, 2, ":", minutes, 2);
			buildString(tektempbuf, offs, ":", seconds, 2);

			printext(xdim - mulscale(72, tekcfg.gHUDSize, 16), ydim - mulscale(12, tekcfg.gHUDSize, 16), tektempbuf,
					ALPHABET2, 255);
			lastsec = seconds;
			lasttimetoggle = tekcfg.toggles[TOGGLE_TIME];
		}
	}

	public static void showscore() {

		if (tekcfg.toggles[TOGGLE_SCORE]) {
			if (gPlayer[screenpeek].score == 1)
				buildString(tektempbuf, 0, "0");
			else
				Bitoa(gPlayer[screenpeek].score, tektempbuf);

			printext(xdim - mulscale(160, tekcfg.gHUDSize, 16), ydim - mulscale(12, tekcfg.gHUDSize, 16), tektempbuf,
					ALPHABET2, 255);
		} else {
			lastscoretoggle = tekcfg.toggles[TOGGLE_SCORE];
			lastscore = 0L;
		}
	}

	public static void rearview(int snum, int smooth) {
		if (!tekcfg.toggles[TOGGLE_REARVIEW]) {
			return;
		}

		int oldwx1 = windowx1, oldwx2 = windowx2;
		int oldwy1 = windowy1, oldwy2 = windowy2;

		float plrang = gPlayer[snum].ang;

		float plrhoriz = gPlayer[snum].horiz;
		engine.setview(mulscale(66, tekcfg.gHUDSize, 16), mulscale(7, tekcfg.gHUDSize, 16),
				mulscale(131, tekcfg.gHUDSize, 16), mulscale(42, tekcfg.gHUDSize, 16));

		float cang = BClampAngle(plrang + 1024);

		int cposx = gPlayer[snum].posx, cposy = gPlayer[snum].posy, cposz = gPlayer[snum].posz;
		float choriz = 200 - plrhoriz;

		engine.drawrooms(cposx, cposy, cposz, cang, choriz, gPlayer[snum].cursectnum);
		rearviewdraw = 1;
		analyzesprites(gPlayer[snum].posx, gPlayer[snum].posy, smooth);
		rearviewdraw = 0;
		engine.drawmasks();

		engine.setview(oldwx1, oldwy1, oldwx2, oldwy2);
	}

	public static void dorearviewscreen() {
		if (otoggles[TOGGLE_REARVIEW]) {
			if (rvpos < 0) {
				rvpos += (TICSPERFRAME << 2);
				if (rvpos > 0)
					rvpos = 0;
			}
		} else if (abs(rvpos) < engine.getTile(RVDEVICE).getWidth()) {
			rvpos -= (TICSPERFRAME << 2);
			if (abs(rvpos) > engine.getTile(RVDEVICE).getWidth())
				rvpos = (short) -engine.getTile(RVDEVICE).getWidth();
		}
	}

	public static void douprtscreen() {
		if (otoggles[TOGGLE_UPRT]) {
			if (wppos < 0) {
				wppos += (TICSPERFRAME << 2);
				if (wppos > 0)
					wppos = 0;
			}
		} else if (abs(wppos) < engine.getTile(WPDEVICE).getWidth()) {
			wppos -= (TICSPERFRAME << 2);
			if (abs(wppos) > engine.getTile(WPDEVICE).getWidth())
				wppos = (short) -engine.getTile(WPDEVICE).getWidth();
		}
	}

	public static void dohealthscreen() {
		if (otoggles[TOGGLE_HEALTH]) {
			if (hcpos < 0) {
				hcpos += (TICSPERFRAME << 2);
				if (hcpos > 0)
					hcpos = 0;
			}
		} else if (abs(hcpos) < engine.getTile(HCDEVICE).getWidth()) {
			hcpos -= (TICSPERFRAME << 2);
			if (abs(hcpos) > engine.getTile(HCDEVICE).getWidth())
				hcpos = (short) -engine.getTile(HCDEVICE).getWidth();
		}
	}

	public static void tekmapmode(int mode) {
		switch (mode) {
		case kView2D:
			if (tekcfg.gOverlayMap == 1)
				gViewMode = kView3D;
			else
				gViewMode = kView2DIcon;
			break;
		case kView3D:
			if (tekcfg.gOverlayMap != 0)
				gViewMode = kView2D;
			else
				gViewMode = kView2DIcon;
			break;
		default:
			gViewMode = kView3D;
			break;
		}
	}

	public static short hcpic, rvpic, wppic;

	public static int tekscreenfx(int smooth) {
		int ammo, n;

		updatesounds(screenpeek);

		// #define COMMITTEE
		// printext((xdim>>1)-25,windowy1+24,"THURS 6PM \0".toCharArray(),ALPHABET,255);

		if (TEKDEMO)
			printext((xdim >> 1) - mulscale(16, tekcfg.gHUDSize, 16), windowy1 + mulscale(24, tekcfg.gHUDSize, 16),
					"DEMO".toCharArray(), ALPHABET2, 255);

		if (mUserFlag != UserFlag.None)
			printext((xdim >> 1) - mulscale(16, tekcfg.gHUDSize, 16), (ydim - mulscale(24, tekcfg.gHUDSize, 16)),
					"USERMAP".toCharArray(), ALPHABET2, 255);

		if ((messageon == 0) && notininventory != 0) {
			showmessage("NOT IN INVENTORY");
			notininventory = 0;
		}

		Tile pic = engine.getTile(RVDEVICE);
		if (otoggles[TOGGLE_REARVIEW]) {
			if (rvpos == 0)
				rvpic = RVDEVICEON;
			if (rvpos < 0) {
				n = pic.getWidth() / (RVDEVICEON - RVDEVICE);
				n = (pic.getWidth() - abs(rvpos)) / n;
				rvpic = (short) (RVDEVICE + n);
			}
			engine.rotatesprite(mulscale(rvpos, tekcfg.gHUDSize, 16) << 16, 0 << 16, tekcfg.gHUDSize, 0, rvpic, 0, 0,
					8 + 16, 0, 0, xdim - 1, ydim - 1);
			if (rvpos == 0)
				rearview(screenpeek, smooth);
		} else if (abs(rvpos) < pic.getWidth()) {
			if (abs(rvpos) > pic.getWidth())
				rvpic = RVDEVICE;
			else {
				n = pic.getWidth() / (RVDEVICEON - RVDEVICE);
				n = (pic.getWidth() - abs(rvpos)) / n;
				rvpic = (short) (RVDEVICE + n);
			}
			engine.rotatesprite(mulscale(rvpos, tekcfg.gHUDSize, 16) << 16, 0 << 16, tekcfg.gHUDSize, 0, rvpic, 0, 0,
					8 + 16, 0, 0, xdim - 1, ydim - 1);
		}

		pic = engine.getTile(WPDEVICE);
		if (otoggles[TOGGLE_UPRT]) {
			if (wppos == 0)
				wppic = WPDEVICEON;

			if (wppos < 0) {
				n = pic.getWidth() / (WPDEVICEON - WPDEVICE);
				n = (pic.getWidth() - abs(wppos)) / n;
				wppic = (short) (WPDEVICE + n);
			}

			engine.rotatesprite((xdim - 1 - mulscale((pic.getWidth() + wppos), tekcfg.gHUDSize, 16)) << 16, 0 << 16,
					tekcfg.gHUDSize, 0, WPDEVICE, 0, 0, 8 + 16, 0, 0, xdim - 1, ydim - 1);
		} else if (abs(wppos) < pic.getWidth()) {
			if (abs(wppos) > pic.getWidth())
				wppic = WPDEVICE;
			else {
				n = pic.getWidth() / (WPDEVICEON - WPDEVICE);
				n = (pic.getWidth() - abs(wppos)) / n;
				wppic = (short) (WPDEVICE + n);
			}

			engine.rotatesprite((xdim - 1 - mulscale((pic.getWidth() + wppos), tekcfg.gHUDSize, 16)) << 16, 0 << 16,
					tekcfg.gHUDSize, 0, WPDEVICE, 0, 0, 8 + 16, 0, 0, xdim - 1, ydim - 1);
		}

		if ((wppic == WPDEVICEON) && (!game.menu.gShowMenu || game.menu.getCurrentMenu() instanceof MenuInterfaceSet)) {
			Bitoa(hours, tektempbuf, 2);
			int offs = buildString(tektempbuf, 2, ":", minutes, 2);
			buildString(tektempbuf, offs, ":", seconds, 2);

			printext(xdim - mulscale(74, tekcfg.gHUDSize, 16), mulscale(8, tekcfg.gHUDSize, 16), tektempbuf, ALPHABET,
					255);

			Bitoa(gPlayer[screenpeek].score, tektempbuf, 8);
			printext(xdim - mulscale(74, tekcfg.gHUDSize, 16), mulscale(18, tekcfg.gHUDSize, 16), tektempbuf, ALPHABET,
					255);
		}

		if ((!game.menu.gShowMenu || game.menu.getCurrentMenu() instanceof MenuInterfaceSet)) {
			showtime();
			showscore();
			showinv(screenpeek);
		}

		if (gPlayer[screenpeek].godMode)
			printext((xdim >> 1) - mulscale(16, tekcfg.gHUDSize, 16), 4, "HOLY \0".toCharArray(), ALPHABET2, 255);

		if (gPlayer[myconnectindex].health <= -160)
			game.getFont(0).drawText(160, 80, toCharArray("Press \"USE\" to restart"), 0, 4, TextAlign.Center, 2, true);

		pic = engine.getTile(HCDEVICE);
		if (otoggles[TOGGLE_HEALTH]) {
			if (hcpos == 0)
				hcpic = HCDEVICEON;

			if (hcpos < 0) {
				n = pic.getWidth() / (HCDEVICEON - HCDEVICE);
				n = (pic.getWidth() - abs(hcpos)) / n;
				hcpic = (short) (HCDEVICE + n);
			}

			engine.rotatesprite(mulscale(hcpos, tekcfg.gHUDSize, 16) << 16,
					(ydim - mulscale(engine.getTile(hcpic).getHeight(), tekcfg.gHUDSize, 16)) << 16, tekcfg.gHUDSize, 0,
					hcpic, 0, 0, 8 + 16, 0, 0, xdim - 1, ydim - 1);

			if (hcpic == HCDEVICEON) {
				for (n = 0; n < gPlayer[screenpeek].health / HCSCALE; n++) {
					engine.rotatesprite(mulscale(hcpos + 34 + (n * 5), tekcfg.gHUDSize, 16) << 16,
							(ydim - mulscale(engine.getTile(hcpic).getHeight() - 7, tekcfg.gHUDSize, 16)) << 16,
							tekcfg.gHUDSize, 0, GREENLIGHTPIC, (gPlayer[screenpeek].health / HCSCALE) - n, 0, 8 + 16, 0,
							0, xdim - 1, ydim - 1);
				}
				for (n = 0; n < stun[screenpeek] / HCSCALE; n++) {
					engine.rotatesprite(mulscale(hcpos + 34 + (n * 5), tekcfg.gHUDSize, 16) << 16,
							(ydim - mulscale(engine.getTile(hcpic).getHeight() - 13, tekcfg.gHUDSize, 16)) << 16,
							tekcfg.gHUDSize, 0, YELLOWLIGHTPIC, (stun[screenpeek] / HCSCALE) - n, 0, 8 + 16, 0, 0,
							xdim - 1, ydim - 1);
				}

				if (gPlayer[screenpeek].lastgun < 8)
					ammo = gPlayer[screenpeek].ammo[gPlayer[screenpeek].lastgun];
				else
					ammo = MAXAMMO;

				for (n = 0; n < ammo / AMMOSCALE; n++) {
					engine.rotatesprite(mulscale(hcpos + 34 + (n * 5), tekcfg.gHUDSize, 16) << 16,
							(ydim - mulscale(engine.getTile(hcpic).getHeight() - 19, tekcfg.gHUDSize, 16)) << 16,
							tekcfg.gHUDSize, 0, BLUELIGHTPIC, (ammo / AMMOSCALE) - n, 0, 8 + 16, 0, 0, xdim - 1,
							ydim - 1);
				}
			}
		} else if (abs(hcpos) < pic.getWidth()) {
			if (abs(hcpos) > pic.getWidth()) {
				hcpic = HCDEVICE;
			} else {
				n = pic.getWidth() / (HCDEVICEON - HCDEVICE);
				n = (pic.getWidth() - abs(hcpos)) / n;
				hcpic = (short) (HCDEVICE + n);
			}
			engine.rotatesprite(mulscale(hcpos, tekcfg.gHUDSize, 16) << 16,
					(ydim - mulscale(engine.getTile(hcpic).getHeight(), tekcfg.gHUDSize, 16)) << 16, tekcfg.gHUDSize, 0,
					hcpic, 0, 0, 8 + 16, 0, 0, xdim - 1, ydim - 1);
		}

		if ((!game.menu.gShowMenu) && (game.nNetMode == NetMode.Multiplayer)) {
//	          netstats(); FIXME
		}

		if ((!game.menu.gShowMenu) && messageon != 0) {
			printext(messagex, windowy2 - mulscale(32, tekcfg.gHUDSize, 16), messagebuf, ALPHABET2, 255);
		}

		if ((!game.menu.gShowMenu || game.menu.getCurrentMenu() instanceof MenuInterfaceSet)
				&& !tekcfg.toggles[TOGGLE_HEALTH] && (hcpos == -pic.getWidth())) {
			if (gPlayer[screenpeek].health < 0) {
				buildString(tektempbuf, 0, "0");
			} else if (gPlayer[screenpeek].health > MAXHEALTH) {
				buildString(tektempbuf, 0, "1000");
			} else {
				Bitoa(gPlayer[screenpeek].health, tektempbuf);
			}
			printext(windowx1 + mulscale(6, tekcfg.gHUDSize, 16), windowy2 - mulscale(10, tekcfg.gHUDSize, 16),
					tektempbuf, ALPHABET2, 255);
		}

		if (game.gPaused)
			game.getFont(0).drawText(160, 5, toCharArray("Pause"), 0, 0, TextAlign.Center, 2, true);

		return 0;
	}

	public static void tekendscreen() {
		if (demowon != 0)
			return;

		engine.setview(0, 0, xdim - 1, ydim - 1);
		engine.loadtile(ES1_VGA);
		overwritesprite(0, 0, 65536, ES1_VGA, 0, 0x02, 0);
		engine.nextpage();

		engine.loadtile(ES2_VGA);
		overwritesprite(0, 0, 65536, ES2_VGA, 0, 0x02, 0);
		engine.nextpage();
	}

	public static void drawscreen(short snum, int dasmoothratio) {
		int cposx, cposy, cposz, czoom = 768;
		short csect;

		smoothratio = dasmoothratio;

		cposx = gPlayer[snum].posx;
		cposy = gPlayer[snum].posy;
		cposz = gPlayer[snum].posz;
		float choriz = gPlayer[snum].horiz;
		czoom = zoom;
		float cang = gPlayer[snum].ang;
		csect = gPlayer[snum].cursectnum;
		if (!game.menu.gShowMenu && !Console.IsShown()) {

			int ix = gPlayer[snum].oposx;
			int iy = gPlayer[snum].oposy;
			int iz = gPlayer[snum].oposz;
			float iHoriz = gPlayer[snum].ohoriz;
			float inAngle = gPlayer[snum].oang;
			int izoom = ozoom;

			ix += mulscale(cposx - gPlayer[snum].oposx, dasmoothratio, 16);
			iy += mulscale(cposy - gPlayer[snum].oposy, dasmoothratio, 16);
			iz += mulscale(cposz - gPlayer[snum].oposz, dasmoothratio, 16);
			iHoriz += ((choriz - gPlayer[snum].ohoriz) * dasmoothratio) / 65536.0f;
			inAngle += ((BClampAngle(cang - gPlayer[snum].oang + 1024) - 1024) * dasmoothratio) / 65536.0f;
			izoom += mulscale(czoom - ozoom, dasmoothratio, 16);

			cposx = ix;
			cposy = iy;
			cposz = iz;
			czoom = izoom;

			choriz = iHoriz;
			cang = inAngle;
		}

		cposz += headbob;

		engine.getzsofslope(csect, cposx, cposy, zofslope);
		int lz = 4 << 8;
		if (cposz < zofslope[CEIL] + lz)
			cposz = zofslope[CEIL] + lz;
		if (cposz > zofslope[FLOOR] - lz)
			cposz = zofslope[FLOOR] - lz;

		if (gViewMode != kView2DIcon) {
			redrawbackfx();
			engine.drawrooms(cposx, cposy, cposz, cang, choriz, csect);
			analyzesprites(gPlayer[snum].posx, gPlayer[snum].posy, dasmoothratio);
			engine.drawmasks();
			tekdrawgun(snum);
		}

		if (gViewMode != kView3D) {
			if (gViewMode == kView2DIcon) {
				engine.clearview(0); // Clear screen to specified color
				engine.drawmapview(cposx, cposy, czoom, (int) cang);
			}
			engine.drawoverheadmap(cposx, cposy, czoom, (short) cang);
		}

//		if (getmessageleng > 0) {
//			charsperline = 40;
//			for (i = 0; i <= getmessageleng; i += charsperline) {
//				for (j = 0; j < charsperline; j++) {
//					tempbuf[j] = getmessage[i + j];
//				}
//				if (getmessageleng < i + charsperline) {
//					tempbuf[(getmessageleng - i)] = 0;
//				} else {
//					tempbuf[charsperline] = 0;
//				}
//				engine.printext256(0, ((i / charsperline) << 3) + (200 - 32 - 8)
//						- (((getmessageleng - 1) / charsperline) << 3), 151,
//						-1, tempbuf, 0);
//			}
//			if (totalclock > getmessagetimeoff) {
//				getmessageleng = 0;
//			}
//		}

		// you are looking thru an opponent plaeyer's eyes
		if ((numplayers >= 2) && (screenpeek != myconnectindex)) {
//			Bstrcpy(tempbuf, "Other");
		}

//		if (OUTOFSYNCMESSAGE) {
////			if (syncstat != 0) {
////				engine.printext256(68, 84, 31, 0, "OUT OF SYNC!\0".toCharArray(), 0);
////			}
//			if (syncstate != 0) {
//				engine.printext256(68, 92, 31, 0, "Missed Network packet!\0".toCharArray(), 0);
//			}
//		}
	}

	public static void redrawbackfx() {
		for (int i = 0; i < MAXTOGGLES; i++)
			otoggles[i] = tekcfg.toggles[i];

		if ((rvmoving != 0 || rvonemotime != 0)) {
			// overwritesprite(0,0,tekcfg.gHUDSize, RVDEVRES,0,0,0);
			if ((tekcfg.toggles[TOGGLE_REARVIEW] && rvpos == 0)
					|| (!tekcfg.toggles[TOGGLE_REARVIEW] && rvpos == -engine.getTile(RVDEVICE).getWidth())) {
				rvmoving = 0;
				rvonemotime++;
				if (rvonemotime == 4)
					rvonemotime = 0;

			}
		}

		if ((wpmoving != 0 || wponemotime != 0)) {
			// overwritesprite(xdim-tilesizx[WPDEVRES],0,tekcfg.gHUDSize,WPDEVRES,0,0,0);
			if ((tekcfg.toggles[TOGGLE_UPRT] && wppos == xdim - engine.getTile(WPDEVICE).getWidth())
					|| (!tekcfg.toggles[TOGGLE_UPRT] && wppos == xdim)) {
				wpmoving = 0;
				wponemotime++;
				if (wponemotime == 4)
					wponemotime = 0;
			}
		}
		if ((hcmoving != 0 || hconemotime != 0)) {
			// overwritesprite(0,ydim-tilesizy[HCDEVRES],tekcfg.gHUDSize,HCDEVRES,0,0,0);
			if ((tekcfg.toggles[TOGGLE_HEALTH] && hcpos == 0)
					|| (!tekcfg.toggles[TOGGLE_HEALTH] && hcpos == xdim - engine.getTile(HCDEVICE).getWidth())) {
				hcmoving = 0;
				hconemotime++;
				if (hconemotime == 4)
					hconemotime = 0;
			}
		}
	}

	public static int startwhiteflash(int bonus) {
		whitecount = BClipHigh(whitecount + (bonus << 3), 100);

		return 0;
	}

	public static int startredflash(int damage) {
		redcount = BClipHigh(redcount + (damage << 4), 100);

		return 0;
	}

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
	}

	public static void updatepaletteshifts() {
		if (engine.glrender() != null) {
			if (whitecount != 0)
				whitecount = BClipLow(whitecount - TICSPERFRAME, 0);

			if (redcount != 0)
				redcount = BClipLow(redcount - 2 * TICSPERFRAME, 0);
		} else {
			int red = 0, white = 0;

			if (whitecount != 0) {
				white = BClipHigh(whitecount / WHITETICS + 1, NUMWHITESHIFTS);
				whitecount = BClipLow(whitecount - TICSPERFRAME, 0);
			}

			if (redcount != 0) {
				red = BClipHigh(redcount / 10 + 1, NUMREDSHIFTS);
				redcount = BClipLow(redcount - 2 * TICSPERFRAME, 0);
			}

			if (red != 0) {
				engine.changepalette(redshifts[red - 1]);
				palshifted = true;
			} else if (white != 0) {
				engine.changepalette(whiteshifts[white - 1]);
				palshifted = true;
			} else if (palshifted) {
				int brightness = BuildSettings.paletteGamma.get();
				engine.setbrightness(brightness, palette, GLInvalidateFlag.All);
				palshifted = false;
			}
		}
	}

	public static void showinv(int snum) {
		char shade;
		boolean skipsyms = false;

		if (!tekcfg.toggles[TOGGLE_INVENTORY]) {
			skipsyms = true;
		}
		if (!skipsyms) {

			for (int i = 0; i < 7; i++) {
				shade = 0;
				if (symbols[i]) {
					if (symbolsdeposited[i])
						shade = 32;
					overwritesprite(windowx2 - mulscale(30 * (8 - i), tekcfg.gHUDSize, 16),
							windowy2 - mulscale(32, tekcfg.gHUDSize, 16), tekcfg.gHUDSize, SYMBOL1PIC + i, shade, 0, 0);
				}
			}
		}

		if (tekcfg.toggles[TOGGLE_INVENTORY]) {
			timedinv--;
			if (timedinv == 0)
				tekcfg.toggles[TOGGLE_INVENTORY] = false;
		}

		if ((windowx2 >= (xdim - 24)) && tekcfg.toggles[TOGGLE_INVENTORY]) {
			if (gPlayer[snum].invbluecards != 0) {
				overwritesprite(xdim - mulscale(24, tekcfg.gHUDSize, 16),
						(ydim >> 1) - mulscale(14, tekcfg.gHUDSize, 16), tekcfg.gHUDSize, 483, 0, 0, 0);
			}
			if (gPlayer[snum].invredcards != 0) {
				overwritesprite(xdim - mulscale(24, tekcfg.gHUDSize, 16),
						(ydim >> 1) - mulscale(2, tekcfg.gHUDSize, 16), tekcfg.gHUDSize, 484, 0, 0, 0);
			}
			if (gPlayer[snum].invaccutrak != 0) {
				overwritesprite(xdim - mulscale(24, tekcfg.gHUDSize, 16),
						(ydim >> 1) + mulscale(10, tekcfg.gHUDSize, 16), tekcfg.gHUDSize, 485, 0, 0, 0);
			}
			lastinvtoggle = true;
		}
	}

	public static void showhelpscreen() {
		if (xdim < 640) {
			engine.rotatesprite(160 << 16, 160 << 16, 65536, 0, HELPSCREENPIC, 0, 0, 2 + 8, 0, 0, xdim - 1, ydim - 1);
		} else {
			engine.rotatesprite(160 << 16, 50 << 16, divscale(200, engine.getTile(HELPSCREEN4801).getHeight(), 15), 0,
					HELPSCREEN4801, 0, 0, 2 + 8, 0, 0, xdim - 1, ydim - 1);
			engine.rotatesprite(160 << 16, 150 << 16, divscale(200, engine.getTile(HELPSCREEN4802).getHeight(), 15), 0,
					HELPSCREEN4802, 0, 0, 2 + 8, 0, 0, xdim - 1, ydim - 1);
		}
	}
}
