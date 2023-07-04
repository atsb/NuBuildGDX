// This file is part of BloodGDX.
// Copyright (C) 2017-2019  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// BloodGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// BloodGDX is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with BloodGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.Blood;

import static ru.m210projects.Blood.Gameutils.*;
import static ru.m210projects.Blood.Globals.*;
import static ru.m210projects.Blood.Main.*;
import static ru.m210projects.Blood.Strings.pal;
import static ru.m210projects.Blood.Strings.plu;
import static ru.m210projects.Blood.Types.ScreenEffect.*;
import static ru.m210projects.Blood.View.*;
import static ru.m210projects.Build.Engine.*;

import ru.m210projects.Build.Engine;
import ru.m210projects.Build.Architecture.BuildGdx;

import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Build.Render.GLRenderer.GLInvalidateFlag;
import ru.m210projects.Build.Script.TextureHDInfo;
import ru.m210projects.Build.Settings.BuildSettings;
import ru.m210projects.Build.Types.Palette;

import static ru.m210projects.Build.OnSceenDisplay.Console.*;
import static ru.m210projects.Build.Pragmas.scale;

public class Screen {

	public static byte[] tmpDAC = new byte[768];
	public static byte[][] palTable = new byte[kMaxPalettes][];
	public static int curPalette;
	public static int gGammaLevels;
	public static byte[][] gammaTable = new byte[16][256];
	private static boolean restorepalette;

	public static void scrCreateStdColors()
	{
		for (int i = 0; i < 32; i++)
			gStdColor[i] = scrFindClosestColor(palette, StdPal[i][0], StdPal[i][1], StdPal[i][2]);
	}

	public static byte scrFindClosestColor( byte[] palette, int r, int g, int b )
	{
		int i;
		int dr, dg, db, dist, matchDist, match = 0;

		matchDist = 0x7FFFFFFF;

		for ( i = 0; i < 256; i++ )
		{
			dist = 0;

			dg = (palette[3 * i + 1] & 0xFF) - g;
			dist += kWeightG * dg * dg;
			if ( dist >= matchDist )
				continue;

			dr = (palette[3 * i] & 0xFF) - r;
			dist += kWeightR * dr * dr;
			if ( dist >= matchDist )
				continue;

			db = (palette[3 * i + 2] & 0xFF) - b;
			dist += kWeightB * db * db;
			if ( dist >= matchDist )
				continue;

			matchDist = dist;
			match = i;

			if (dist == 0)
				break;
		}

		return (byte) match;
	}

	public static void scrLoadPLUs()
	{
		int i;

		if ( gFogMode )
		{
			byte[] buf = BuildGdx.cache.getBytes("FOG.FLU", 0);
			if (buf == null) {
				Console.Println("FOG.FLU not found", OSDTEXT_RED);
				return;
			}

			for ( i = 0; i < kMaxPLU; i++ )
			{
				System.arraycopy(buf, 768 * i, palookup[PLU[i].index], 0, 768);
			}

			parallaxvisibility = 3072;
		}
		else
		{
			for ( i = 0; i < kMaxPLU; i++ )
			{
				byte[] buf = BuildGdx.cache.getBytes(PLU[i].name + "." + plu, 0);
				if (buf == null)
				{
					Console.Println(PLU[i].name + ".PLU not found", OSDTEXT_RED);
					return;
				}

				if ( buf.length / 256 != kPalLookups ) {
					Console.Println("Incorrect PLU size", OSDTEXT_RED);
					return;
				}

				//[index][color + 256 * shade]
				palookup[PLU[i].index] = buf;
				int colnum = 0;
				int colindex = palookup[PLU[i].index][colnum + 256 * 63] & 0xFF;
				palookupfog[PLU[i].index][0] = palette[colindex];
				palookupfog[PLU[i].index][1] = palette[colindex + 1];
				palookupfog[PLU[i].index][2] = palette[colindex + 2];
			}
		}
	}

	public static void scrLoadPalette()
	{
		int i;
		Console.Println("Loading palettes");

		palette = new byte[768];
		curpalette = new Palette();
		palookup = new byte[MAXPALOOKUPS][];

		for ( i = 0; i < PAL.length; i++ )
		{
			byte[] buf = BuildGdx.cache.getBytes(PAL[i].name + "." + pal, 0);
			if (buf == null)
			{
				Console.Println(PAL[i].name + ".PAL not found", OSDTEXT_RED);
				return;
			}
			palTable[PAL[i].index] = buf;
		}

		// copy the default palette into ken's variable so Std color stuff still works
		System.arraycopy(palTable[kPalNormal], 0, palette, 0, 768);

		paletteloaded = 1;
		numshades = kPalLookups;

		scrLoadPLUs();

		Console.Println("Loading translucency table", 0);

		transluc = BuildGdx.cache.getBytes("trans.tlu", 0);

		if (transluc == null) {
			Console.Println("TRANS.TLU not found", OSDTEXT_RED);
			return;
		}
	}

	public static boolean scrSetGameMode(int davidoption, int daxdim, int daydim)
	{
		if(!engine.setgamemode(davidoption, daxdim, daydim))
			cfg.fullscreen = 0;

		cfg.ScreenWidth = daxdim;
		cfg.ScreenHeight = daydim;

		scrSetPalette(curPalette);
		return true;
	}

	public static void scrSetView(int mode)
	{
		switch(mode)
		{
		case kView2D:
			if(cfg.gOverlayMap == 1)
				gViewMode = kView3D;
			else gViewMode = kView2DIcon;
			break;
		case kView3D:
			if(cfg.gOverlayMap != 0)
				gViewMode = kView2D;
			else gViewMode = kView2DIcon;
			break;
		default:
			gViewMode = kView3D;
			viewResizeView(cfg.gViewSize);
			break;
		}
	}

	public static void scrReset()
	{
		gMe.pickupEffect = 0;
		gMe.hitEffect = 0;
		gMe.blindEffect = 0;
		gMe.drownEffect = 0;
		resetEffects();
	}

	public static void scrSetPalette( int nPalette )
	{
		if(curPalette == nPalette)
			return;

		System.err.println("Palette has been changed to " + nPalette);
		curPalette = nPalette;
		engine.setbrightness(BuildSettings.paletteGamma.get(), palTable[curPalette], GLInvalidateFlag.All); // reset baseline colors

		if(game.currentDef != null) {
			TextureHDInfo hdInfo = game.currentDef.texInfo;
			if (nPalette == kPalWater)
				hdInfo.setPaletteTint(MAXPALOOKUPS-1, 100, 160, 255, 0);
			else if (nPalette == kPalBeast)
				hdInfo.setPaletteTint(MAXPALOOKUPS-1, 255, 120, 120, 0);
	        else if (nPalette == kPalSewer)
	        	hdInfo.setPaletteTint(MAXPALOOKUPS-1, 180, 200, 50, 0);
	        else
	        	hdInfo.setPaletteTint(MAXPALOOKUPS-1, 255, 255, 255, 0);
		}
	}

	public static int coordsConvertXScaled(int coord, int bits)
	{
		int oxdim = xdim;
		int xdim = (4 * ydim) / 3;
		int offset = oxdim - xdim;

		int normxofs = coord - (320 << 15);
		int wx = (xdim << 15) + scale(normxofs, xdim, 320);
		wx += (oxdim - xdim) / 2;

		if((bits & 256) == 256)
			return wx - offset / 2 - 1;
		if((bits & 512) == 512)
			return wx + offset / 2 - 1;

		return wx - 1;
	}

	public static int coordsConvertYScaled(int coord)
	{
		int ydim = (3 * xdim) / 4;
		int buildim = 200 * ydim / Engine.ydim;
		int normxofs = coord - (buildim << 15);
		int wy = (ydim << 15) + scale(normxofs, ydim, buildim);

		return wy;
	}

	public static void scrSetDac( int nTicks )
	{
		int pickupEffect = gPlayer[gViewIndex].pickupEffect;
		int hitEffect = ClipHigh(gPlayer[gViewIndex].hitEffect, 85);
		int blindEffect = gPlayer[gViewIndex].blindEffect;
		int drownEffect = gPlayer[gViewIndex].drownEffect;

		int scrR = pickupEffect + 2 * hitEffect - blindEffect - (drownEffect >> 6);
		int scrG = pickupEffect - 3 * hitEffect - blindEffect - (drownEffect >> 5);
		int scrB = pickupEffect + 3 * hitEffect + blindEffect + (drownEffect >> 6);

		boolean update = false;
		if((scrR|scrG|scrB) != 0)
		{
			update = true;
			restorepalette = true;
		} else if(restorepalette)
		{
			update = true;
			restorepalette = false;
		}

		if(!update) return;

		for (int i = 0; i < 256; i++)
		{
			tmpDAC[3 * i] = (byte)ClipRange(curpalette.getRed(i) + scrR, 0, 255);
			tmpDAC[3 * i + 1] = (byte)ClipRange(curpalette.getGreen(i) + scrG, 0, 255);
			tmpDAC[3 * i + 2] = (byte)ClipRange(curpalette.getBlue(i) - scrB, 0, 255);
		}

		engine.getrender().changepalette(tmpDAC);
	}

	public static void scrGLSetDac( int nTicks )
	{
		if(game.menu == null) return; //Not initialized yet

		if(game.menu.gShowMenu)
		{
			engine.updateFade(damagefade, 0);
			engine.updateFade(drownfade, 0);
			engine.updateFade(blindfade, 0);
			engine.updateFade(pickupfade, 0);
			return;
		}

		int pickup = gPlayer[gViewIndex].pickupEffect;
		int damage = gPlayer[gViewIndex].hitEffect;
		int choke = gPlayer[gViewIndex].blindEffect;
		int drown = gPlayer[gViewIndex].drownEffect;

		engine.updateFade(damagefade, 3 * damage);
		engine.updateFade(drownfade, drown);
		engine.updateFade(blindfade, choke);
		engine.updateFade(pickupfade, pickup);

		if((pickup|damage|choke|drown) != 0)
			engine.showfade();
	}
}
