package ru.m210projects.Wang;

import static com.badlogic.gdx.graphics.GL20.GL_DST_COLOR;
import static com.badlogic.gdx.graphics.GL20.GL_ONE_MINUS_SRC_ALPHA;
import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Build.Gameutils.BClipRange;
import static ru.m210projects.Build.Pragmas.klabs;
import static ru.m210projects.Wang.Game.*;
import static ru.m210projects.Wang.Game.screenpeek;
import static ru.m210projects.Wang.Gameutils.PF_DIVING;
import static ru.m210projects.Wang.Gameutils.PF_DIVING_IN_LAVA;
import static ru.m210projects.Wang.Gameutils.PLAYER_DEATH_DROWN;
import static ru.m210projects.Wang.Gameutils.synctics;
import static ru.m210projects.Wang.Inv.INVENTORY_NIGHT_VISION;
import static ru.m210projects.Wang.Main.engine;
import static ru.m210projects.Wang.Main.*;
import static ru.m210projects.Wang.Type.MyTypes.TEST;



import ru.m210projects.Build.Script.TextureHDInfo;
import ru.m210projects.Wang.Type.PlayerStr;

public class Palette {

	public static int fader, fadeg, fadeb;

//	public static byte[] palette_data = new byte[768];
	public static byte[] tempbuf = new byte[256];
//	public static byte[][] DefaultPalette = new byte[MAXPALOOKUPS][32];
	public static final int MAXFADETICS = 5;

	// Fades from 100% to 62.5% somewhat quickly,
	// then from 62.5% to 37.5% slowly,
	// then from 37.5% to 0% quickly.
	// This seems to capture the pain caused by enemy shots, plus the extreme
	// fade caused by being blinded or intense pain.
	// Perhaps the next step would be to apply a gentle smoothing to the
	// intersections of these lines.
	public static int faderamp[] = { 64, 60, 56, 52, 48, 44, // y=64-4x

			40, 39, 38, 38, 37, // y=44.8-(16/20)x
			36, 35, 34, 34, 33, 32, 31, 30, 30, 29, 28, 27, 26, 26, 25,

			24, 20, 16, 12, 8, 4 // y=128-4x
	};

	public static final int COLOR_PAIN = 128; // Light red range
	public static final int FADE_DAMAGE_FACTOR = 3; // 100 health / 32 shade cycles = 3.125
	public static final int FORCERESET = 256;

	public static final int LT_GREY = (16 * 0 + 1);
	public static final int DK_GREY = (16 * 1);
	public static final int LT_BROWN = (16 * 2);
	public static final int DK_BROWN = (16 * 3);
	public static final int LT_TAN = (16 * 4);
	public static final int DK_TAN = (16 * 5);
	public static final int RUST_RED = (16 * 6);
	public static final int RED = (16 * 7);
	public static final int YELLOW = (16 * 8);
	public static final int BRIGHT_GREEN = (16 * 9);
	public static final int DK_GREEN = (16 * 10);
	public static final int GREEN = (16 * 11);
	public static final int LT_BLUE = (16 * 12);
	public static final int DK_BLUE = (16 * 13);
	public static final int PURPLE = (16 * 14);
	public static final int FIRE = (16 * 15);

	public static final int PALETTE_DEFAULT = 0;
	public static final int PALETTE_FOG = 1;
	// blue sword blade test
	public static final int PALETTE_MENU_HIGHLIGHT = 2;
	// used for the elector gore pieces
	public static final int PALETTE_ELECTRO_GORE = 3;
	// turns ninjas belt and headband red
	public static final int PALETTE_BASIC_NINJA = 4;
	// diving in lava
	public static final int PALETTE_DIVE_LAVA = 5;
	// turns ninjas belt and headband red
	public static final int PALETTE_RED_NINJA = 6;
	// used for the mother ripper - she is bigger/stronger/brown
	public static final int PALETTE_BROWN_RIPPER = 7;
	// turns ninjas belt and headband red
	public static final int PALETTE_GREEN_NINJA = 8;
	// reserved diving palette this is copied over the default palette
	// when needed - NOTE: could move this to a normal memory buffer if palette
	// slot is needed.
	public static final int PALETTE_DIVE = 9;
	public static final int PALETTE_SKEL_GORE = 10;
	// turns ALL colors to shades of GREEN/BLUE/RED
	public static final int PALETTE_GREEN_LIGHTING = 11;
	public static final int PALETTE_BLUE_LIGHTING = 13;
	public static final int PALETTE_RED_LIGHTING = 14;

	// for brown bubbling sludge
	public static final int PALETTE_SLUDGE = 15;

	// Player 0 uses default palette - others use these
	// turns ninja's vests (when we get them) into different color ranges
	public static final int PALETTE_PLAYER0 = 16;
	public static final int PAL_XLAT_BROWN = 16;
	public static final int PALETTE_PLAYER1 = 17;
	public static final int PAL_XLAT_LT_GREY = 17;
	public static final int PALETTE_PLAYER2 = 18;
	public static final int PAL_XLAT_PURPLE = 18;
	public static final int PALETTE_PLAYER3 = 19;
	public static final int PAL_XLAT_RUST_RED = 19;
	public static final int PALETTE_PLAYER4 = 20;
	public static final int PAL_XLAT_YELLOW = 20;
	public static final int PALETTE_PLAYER5 = 21;
	public static final int PAL_XLAT_DK_GREEN = 21;
	public static final int PALETTE_PLAYER6 = 22;
	public static final int PAL_XLAT_GREEN = 22;
	public static final int PALETTE_PLAYER7 = 23;
	public static final int PAL_XLAT_LT_BLUE = 23;
	public static final int PALETTE_PLAYER8 = 24;
	public static final int PAL_XLAT_LT_TAN = 24;
	public static final int PALETTE_PLAYER9 = 25;
	public static final int PAL_XLAT_RED = 25;
	public static final int PALETTE_PLAYER10 = 26;
	public static final int PAL_XLAT_DK_GREY = 26;
	public static final int PALETTE_PLAYER11 = 27;
	public static final int PAL_XLAT_BRIGHT_GREEN = 27;
	public static final int PALETTE_PLAYER12 = 28;
	public static final int PAL_XLAT_DK_BLUE = 28;
	public static final int PALETTE_PLAYER13 = 29;
	public static final int PAL_XLAT_FIRE = 29;
	public static final int PALETTE_PLAYER14 = 30;
	public static final int PALETTE_PLAYER15 = 31;
	public static final int PALETTE_ILLUMINATE = 32; // Used to make sprites bright green in night vision

	public static final int PLAYER_COLOR_MAPS = 15;

	private static class COLOR_MAP {
		public int FromRange, ToRange, FromColor, ToColor;

		public COLOR_MAP(int FromRange, int ToRange, int FromColor, int ToColor) {
			this.FromRange = FromRange;
			this.ToRange = ToRange;
			this.FromColor = FromColor;
			this.ToColor = ToColor;
		}
	}

	private static void MapColors(int num, COLOR_MAP cm, int create) {
		int i;
		float inc;

		if (create != 0) {
			for (i = 0; i < 256; i++)
				tempbuf[i] = (byte) i;
		}

		if (cm.FromRange == 0 || num <= 0 || num >= 256) {
			return;
		}

		inc = cm.ToRange / ((float) cm.FromRange);

		for (i = 0; i < cm.FromRange; i++)
			tempbuf[i + cm.FromColor] = (byte) ((i * inc) + cm.ToColor);
	}

	public static COLOR_MAP BrownRipper = new COLOR_MAP(31, 32, LT_GREY, LT_TAN);

	public static COLOR_MAP SkelGore = new COLOR_MAP(16, 16, RED, BRIGHT_GREEN);
	public static COLOR_MAP ElectroGore = new COLOR_MAP(16, 16, RED, DK_BLUE);

	public static COLOR_MAP MenuHighlight = new COLOR_MAP(16, 16, RED, FIRE);

	public static COLOR_MAP PlayerColorMap[][] = { { new COLOR_MAP(32, 32, LT_BLUE, LT_BROWN), },
			{ new COLOR_MAP(32, 31, LT_BLUE, LT_GREY), }, { new COLOR_MAP(32, 16, LT_BLUE, PURPLE), },
			{ new COLOR_MAP(32, 16, LT_BLUE, RUST_RED), }, { new COLOR_MAP(32, 16, LT_BLUE, YELLOW), },
			{ new COLOR_MAP(32, 16, LT_BLUE, DK_GREEN), }, { new COLOR_MAP(32, 16, LT_BLUE, GREEN), },
			{ new COLOR_MAP(32, 32, LT_BLUE, LT_BLUE), // Redundant, but has to be here for position
			}, { new COLOR_MAP(32, 32, LT_BLUE, LT_TAN), }, { new COLOR_MAP(32, 16, LT_BLUE, RED), },
			{ new COLOR_MAP(32, 16, LT_BLUE, DK_GREY), }, { new COLOR_MAP(32, 16, LT_BLUE, BRIGHT_GREEN), },
			{ new COLOR_MAP(32, 16, LT_BLUE, DK_BLUE), }, { new COLOR_MAP(32, 16, LT_BLUE, FIRE), },
			{ new COLOR_MAP(32, 16, LT_BLUE, FIRE), } };

	public static COLOR_MAP AllToRed[] = { new COLOR_MAP(31, 16, LT_GREY, RED), new COLOR_MAP(32, 16, LT_BROWN, RED),
			new COLOR_MAP(32, 16, LT_TAN, RED), new COLOR_MAP(16, 16, RUST_RED, RED),
			new COLOR_MAP(16, 16, YELLOW, RED), new COLOR_MAP(16, 16, BRIGHT_GREEN, RED),
			new COLOR_MAP(16, 16, DK_GREEN, RED), new COLOR_MAP(16, 16, GREEN, RED),
			new COLOR_MAP(32, 16, LT_BLUE, RED), new COLOR_MAP(16, 16, PURPLE, RED), new COLOR_MAP(16, 16, FIRE, RED) };

	public static COLOR_MAP AllToBlue[] = { new COLOR_MAP(31, 32, LT_GREY, LT_BLUE),
			new COLOR_MAP(32, 32, LT_BROWN, LT_BLUE), new COLOR_MAP(32, 32, LT_TAN, LT_BLUE),
			new COLOR_MAP(16, 32, RUST_RED, LT_BLUE), new COLOR_MAP(16, 32, YELLOW, LT_BLUE),
			new COLOR_MAP(16, 32, BRIGHT_GREEN, LT_BLUE), new COLOR_MAP(16, 32, DK_GREEN, LT_BLUE),
			new COLOR_MAP(16, 32, GREEN, LT_BLUE), new COLOR_MAP(16, 32, RED, LT_BLUE),
			new COLOR_MAP(16, 32, PURPLE, LT_BLUE), new COLOR_MAP(16, 32, FIRE, LT_BLUE) };

	public static COLOR_MAP AllToGreen[] = { new COLOR_MAP(31, 16, LT_GREY, GREEN),
			new COLOR_MAP(32, 16, LT_BROWN, GREEN), new COLOR_MAP(32, 16, LT_TAN, GREEN),
			new COLOR_MAP(16, 16, RUST_RED, GREEN), new COLOR_MAP(16, 16, YELLOW, GREEN),
			new COLOR_MAP(16, 16, BRIGHT_GREEN, GREEN), new COLOR_MAP(16, 16, DK_GREEN, GREEN),
			new COLOR_MAP(16, 16, GREEN, GREEN), new COLOR_MAP(32, 16, LT_BLUE, GREEN),
			new COLOR_MAP(16, 16, RED, GREEN), new COLOR_MAP(16, 16, PURPLE, GREEN),
			new COLOR_MAP(16, 16, FIRE, GREEN) };

	public static COLOR_MAP NinjaBasic[] = { new COLOR_MAP(32, 16, LT_TAN, DK_GREY),
			new COLOR_MAP(32, 16, LT_BROWN, DK_GREY), new COLOR_MAP(32, 31, LT_BLUE, LT_GREY),
			new COLOR_MAP(16, 16, DK_GREEN, DK_GREY), new COLOR_MAP(16, 16, GREEN, DK_GREY),
			new COLOR_MAP(16, 16, YELLOW, DK_GREY) };

	public static COLOR_MAP NinjaRed[] = { new COLOR_MAP(16, 16, DK_TAN, DK_GREY), new COLOR_MAP(16, 16, GREEN, DK_TAN),
			new COLOR_MAP(16, 8, DK_BROWN, RED + 8), new COLOR_MAP(32, 16, LT_BLUE, RED) };

	public static COLOR_MAP NinjaGreen[] = { new COLOR_MAP(16, 16, DK_TAN, DK_GREY),
			new COLOR_MAP(16, 16, GREEN, DK_TAN), new COLOR_MAP(16, 8, DK_BROWN, GREEN + 6),

			new COLOR_MAP(32, 16, LT_BLUE, GREEN) };

	public static COLOR_MAP Illuminate[] = { new COLOR_MAP(16, 8, LT_GREY, BRIGHT_GREEN),
			new COLOR_MAP(16, 8, DK_GREY, BRIGHT_GREEN), new COLOR_MAP(16, 8, LT_BROWN, BRIGHT_GREEN),
			new COLOR_MAP(16, 8, DK_BROWN, BRIGHT_GREEN), new COLOR_MAP(16, 8, LT_TAN, BRIGHT_GREEN),
			new COLOR_MAP(16, 8, DK_TAN, BRIGHT_GREEN), new COLOR_MAP(16, 8, RUST_RED, BRIGHT_GREEN),
			new COLOR_MAP(16, 8, YELLOW, BRIGHT_GREEN), new COLOR_MAP(16, 8, DK_GREEN, BRIGHT_GREEN),
			new COLOR_MAP(16, 8, GREEN, BRIGHT_GREEN), new COLOR_MAP(32, 8, LT_BLUE, BRIGHT_GREEN),
			new COLOR_MAP(16, 8, RED, BRIGHT_GREEN), new COLOR_MAP(16, 8, PURPLE, BRIGHT_GREEN),
			new COLOR_MAP(16, 8, FIRE, BRIGHT_GREEN) };

	public static void InitPalette() {
		engine.initfastcolorlookup(1, 1, 1);

//		for (int i = 0; i < MAXPALOOKUPS; i++) {
//			if (palookup[i] != null)
//				System.arraycopy(palookup[i], 0, DefaultPalette[i], 0, 32);
//		}

		//
		// Dive palettes
		//

		for (int i = 0; i < 256; i++)
			tempbuf[i] = (byte) i;

		// palette for underwater
		engine.makepalookup(PALETTE_DIVE, tempbuf, 0, 0, 15, 1);

		int FOG_AMT = 15;
		engine.makepalookup(PALETTE_FOG, tempbuf, FOG_AMT, FOG_AMT, FOG_AMT, 1);

		engine.makepalookup(PALETTE_DIVE_LAVA, tempbuf, 11, 0, 0, 1);

		//
		// 1 Range changes
		//

		MapColors(PALETTE_BROWN_RIPPER, BrownRipper, 1);
		engine.makepalookup(PALETTE_BROWN_RIPPER, tempbuf, 0, 0, 0, 1);

		MapColors(PALETTE_SKEL_GORE, SkelGore, 1);
		engine.makepalookup(PALETTE_SKEL_GORE, tempbuf, 0, 0, 0, 1);

		MapColors(PALETTE_ELECTRO_GORE, ElectroGore, 1);
		engine.makepalookup(PALETTE_ELECTRO_GORE, tempbuf, 0, 0, 0, 1);

		MapColors(PALETTE_MENU_HIGHLIGHT, MenuHighlight, 1);
		engine.makepalookup(PALETTE_MENU_HIGHLIGHT, tempbuf, 0, 0, 0, 1);

		//
		// Multiple range changes
		//

		MapColors(PALETTE_BASIC_NINJA, NinjaBasic[0], 1);
		for (int i = 1; i < NinjaBasic.length; i++)
			MapColors(PALETTE_BASIC_NINJA, NinjaBasic[i], 0);
		engine.makepalookup(PALETTE_BASIC_NINJA, tempbuf, 0, 0, 0, 1);

		MapColors(PALETTE_RED_NINJA, NinjaRed[0], 1);
		for (int i = 1; i < NinjaRed.length; i++)
			MapColors(PALETTE_RED_NINJA, NinjaRed[i], 0);
		engine.makepalookup(PALETTE_RED_NINJA, tempbuf, 0, 0, 0, 1);

		MapColors(PALETTE_GREEN_NINJA, NinjaGreen[0], 1);
		for (int i = 1; i < NinjaGreen.length; i++)
			MapColors(PALETTE_GREEN_NINJA, NinjaGreen[i], 0);
		engine.makepalookup(PALETTE_GREEN_NINJA, tempbuf, 0, 0, 0, 1);

		MapColors(PALETTE_GREEN_LIGHTING, AllToGreen[0], 1);
		for (int i = 1; i < AllToGreen.length; i++)
			MapColors(PALETTE_GREEN_LIGHTING, AllToGreen[i], 0);
		engine.makepalookup(PALETTE_GREEN_LIGHTING, tempbuf, 0, 0, 0, 1);

		MapColors(PALETTE_RED_LIGHTING, AllToRed[0], 1);
		for (int i = 1; i < AllToRed.length; i++)
			MapColors(PALETTE_RED_LIGHTING, AllToRed[i], 0);
		engine.makepalookup(PALETTE_RED_LIGHTING, tempbuf, 0, 0, 0, 1);

		MapColors(PALETTE_BLUE_LIGHTING, AllToBlue[0], 1);
		for (int i = 1; i < AllToBlue.length; i++)
			MapColors(PALETTE_BLUE_LIGHTING, AllToBlue[i], 0);
		engine.makepalookup(PALETTE_BLUE_LIGHTING, tempbuf, 0, 0, 0, 1);

		MapColors(PALETTE_ILLUMINATE, Illuminate[0], 1);
		for (int i = 1; i < Illuminate.length; i++)
			MapColors(PALETTE_ILLUMINATE, Illuminate[i], 0);
		engine.makepalookup(PALETTE_ILLUMINATE, tempbuf, 0, 0, 0, 1);

		// PLAYER COLORS - ALSO USED FOR OTHER THINGS
		for (int play = 0; play < PLAYER_COLOR_MAPS; play++) {
			MapColors(PALETTE_PLAYER0 + play, PlayerColorMap[play][0], 1);
			MapColors(PALETTE_PLAYER0 + play, PlayerColorMap[play][0], 0);
			engine.makepalookup(PALETTE_PLAYER0 + play, tempbuf, 0, 0, 0, 1);
		}

		//
		// Special Brown sludge
		//

		for (int i = 0; i < 256; i++)
			tempbuf[i] = (byte) i;

		// invert the brown palette
		for (int i = 0; i < 32; i++)
			tempbuf[LT_BROWN + i] = (byte) ((LT_BROWN + 32) - i);
		engine.makepalookup(PALETTE_SLUDGE, tempbuf, 0, 0, 0, 1);
	}

	private static boolean CheckGLPalette(int color) {
		if ((color == 210 || color == 148 || color == FORCERESET)) // dive, night palette
																								// or forcereset palette
			return true;
		return false;
	}

	//////////////////////////////////////////
	// Set the amount of redness for damage
	// the player just took
	//////////////////////////////////////////
	public static void SetFadeAmt(PlayerStr pp, int damage, int startcolor) {
		int palreg, usereg = 0, tmpreg1 = 0, tmpreg2 = 0;
		int fadedamage = 0;

		if (klabs(pp.FadeAmt) > 0 && startcolor == (pp.StartColor & 0xFF))
			return;

		// Don't ever over ride flash bomb
		if (pp.StartColor == 1 && klabs(pp.FadeAmt) > 0)
			return;

		// Reset the palette
		if (pp == Player[screenpeek]) {
			if (!CheckGLPalette(startcolor))
				ResetPalette(pp, startcolor);
			else
				System.arraycopy(palette, 0, pp.temp_pal, 0, 768);
		}

		if (damage < -150 && damage > -1000)
			fadedamage = 150;
		else if (damage < -1000) // Underwater
			fadedamage = klabs(damage + 1000);
		else
			fadedamage = klabs(damage);

		if (damage >= -5 && damage < 0)
			fadedamage += 10;

		// Don't let red to TOO red
		if (startcolor == COLOR_PAIN && fadedamage > 100)
			fadedamage = 100;

		pp.FadeAmt = (short) (fadedamage / FADE_DAMAGE_FACTOR);

		if (pp.FadeAmt <= 0) {
			pp.FadeAmt = 0;
			return;
		}

		// It's a health item, just do a preset flash amount
		if (damage > 0)
			pp.FadeAmt = 3;

		pp.StartColor = (byte) startcolor;
		pp.FadeTics = 0;

		int red = palette[3 * startcolor] & 0xFF;
		int green = palette[3 * startcolor + 1] & 0xFF;
		int blue = palette[3 * startcolor + 2] & 0xFF;

		fader = red;
		fadeg = green;
		fadeb = blue;

		for (palreg = 0; palreg < 768; palreg++) {
			tmpreg1 = (pp.temp_pal[palreg] & 0xFF) + ((2 * pp.FadeAmt) + 4);
			tmpreg2 = (pp.temp_pal[palreg] & 0xFF) - ((2 * pp.FadeAmt) + 4);
			if (tmpreg1 > 255)
				tmpreg1 = 255;
			if (tmpreg2 < 0)
				tmpreg2 = 0;

			if (usereg == 0) {
				if ((pp.temp_pal[palreg] & 0xFF) < red) {
					if (((pp.temp_pal[palreg] = (byte) tmpreg1) & 0xFF) > red)
						pp.temp_pal[palreg] = (byte) red;
				} else if ((pp.temp_pal[palreg] & 0xFF) > red)
					if (((pp.temp_pal[palreg] = (byte) tmpreg2) & 0xFF) < red)
						pp.temp_pal[palreg] = (byte) red;
			} else if (usereg == 1) {
				if ((pp.temp_pal[palreg] & 0xFF) < green) {
					if (((pp.temp_pal[palreg] = (byte) tmpreg1) & 0xFF) > green)
						pp.temp_pal[palreg] = (byte) green;
				} else if ((pp.temp_pal[palreg] & 0xFF) > green)
					if (((pp.temp_pal[palreg] = (byte) tmpreg2) & 0xFF) < green)
						pp.temp_pal[palreg] = (byte) green;
			} else if (usereg == 2) {
				if ((pp.temp_pal[palreg] & 0xFF) < blue) {
					if (((pp.temp_pal[palreg] = (byte) tmpreg1) & 0xFF) > blue)
						pp.temp_pal[palreg] = (byte) blue;
				} else if ((pp.temp_pal[palreg] & 0xFF) > blue)
					if (((pp.temp_pal[palreg] = (byte) tmpreg2) & 0xFF) < blue)
						pp.temp_pal[palreg] = (byte) blue;
			}

			if (++usereg > 2)
				usereg = 0;
		}

		// Do initial palette set
		if (pp == Player[screenpeek]) {
			engine.setbrightness(gs.brightness, pp.temp_pal, true);

				if (game.currentDef != null) {
					TextureHDInfo hdInfo = game.currentDef.texInfo;

					if (startcolor == 210) // underwater
						hdInfo.setPaletteTint(MAXPALOOKUPS - 1, 200, 240, 255, 0);
					else if (startcolor == 148) // nightvision
						hdInfo.setPaletteTint(MAXPALOOKUPS - 1, 230, 255, 150, 0);
					else
						hdInfo.setPaletteTint(MAXPALOOKUPS - 1, 255, 255, 255, 0);
				}

			if (damage < -1000)
				pp.FadeAmt = 1000; // Don't call DoPaletteFlash for underwater stuff
		}
	}

	public static void DoPaletteFlash(PlayerStr pp) {
		int palreg, tmpreg1 = 0, tmpreg2 = 0;

		if (pp.FadeAmt <= 1) {
			pp.FadeAmt = 0;
			pp.StartColor = 0;
			if (pp == Player[screenpeek]) {
				ResetPalette(pp, FORCERESET);
				DoPlayerDivePalette(pp); // Check Dive again
				DoPlayerNightVisionPalette(pp); // Check Night Vision again
			}
			return;
		}

		pp.FadeTics += synctics; // Add this frame's tic amount to
									// counter

		if (pp.FadeTics >= MAXFADETICS) {
			while (pp.FadeTics >= MAXFADETICS) {
				pp.FadeTics -= MAXFADETICS;

				pp.FadeAmt--; // Decrement FadeAmt till it gets to
								// 0 again.
			}
		} else
			return; // Return if they were not >
					// MAXFADETICS

		if (pp.FadeAmt > 32)
			return;

		if (pp.FadeAmt <= 1) {
			pp.FadeAmt = 0;
			pp.StartColor = 0;
			if (pp == Player[screenpeek]) {
				ResetPalette(pp, FORCERESET);
				DoPlayerDivePalette(pp); // Check Dive again
				DoPlayerNightVisionPalette(pp); // Check Night Vision again
			}
			return;
		} else {
			for (palreg = 0; palreg < 768; palreg++) {
				tmpreg1 = (pp.temp_pal[palreg]) + 2;
				tmpreg2 = (pp.temp_pal[palreg]) - 2;
				if (tmpreg1 > 255)
					tmpreg1 = 255;
				if (tmpreg2 < 0)
					tmpreg2 = 0;

				if ((pp.temp_pal[palreg] & 0xFF) < (palette[palreg] & 0xFF)) {
					if (((pp.temp_pal[palreg] = (byte) tmpreg1) & 0xFF) > (palette[palreg] & 0xFF))
						pp.temp_pal[palreg] = palette[palreg];
				} else if ((pp.temp_pal[palreg] & 0xFF) > (palette[palreg] & 0xFF))
					if (((pp.temp_pal[palreg] = (byte) tmpreg2) & 0xFF) < (palette[palreg] & 0xFF))
						pp.temp_pal[palreg] = palette[palreg];

			}

			// Only hard set the palette if this is currently the player's view
			if (pp == Player[screenpeek]) {
				engine.setbrightness(gs.brightness, pp.temp_pal, true);
			}
		}
	}

	public static void DoPlayerDivePalette(PlayerStr pp) {
		if (pp != Player[screenpeek])
			return;

		if ((pp.DeathType == PLAYER_DEATH_DROWN || TEST((Player[screenpeek]).Flags, PF_DIVING))
				&& !TEST(pp.Flags, PF_DIVING_IN_LAVA)) {
			SetFadeAmt(pp, -1005, 210); // Dive color , org color 208
		} else {
			// Put it all back to normal
			if ((pp.StartColor & 0xFF) == 210) {
				ResetPalette(pp, FORCERESET); // Back to normal
				pp.FadeAmt = 0;
			}
		}
	}

	public static void DoPlayerNightVisionPalette(PlayerStr pp) {
		if (pp != Player[screenpeek])
			return;

		if (pp.InventoryActive[INVENTORY_NIGHT_VISION]) {
			SetFadeAmt(pp, -1005, 148); // Night vision green tint
			pp.NightVision = true;
		} else {
			// Put it all back to normal
			if ((pp.StartColor & 0xFF) == 148) {
				ResetPalette(pp, FORCERESET); // Back to normal
				pp.FadeAmt = 0;
			}
			pp.NightVision = false;
		}
	}

	public static void ResetPalette(PlayerStr pp, int color) {
		System.arraycopy(palette, 0, pp.temp_pal, 0, 768);
		pp.StartColor = 0;
		if (game.currentDef != null) {
			TextureHDInfo hdInfo = game.currentDef.texInfo;
			hdInfo.setPaletteTint(MAXPALOOKUPS - 1, 255, 255, 255, 0);
		}
		if (!CheckGLPalette(color))
			return;

		engine.setbrightness(gs.brightness, palette, true);
	}

}
