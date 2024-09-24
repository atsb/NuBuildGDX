// This file is part of PowerslaveGDX.
// Copyright (C) 2019  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// PowerslaveGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// PowerslaveGDX is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with PowerslaveGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.Powerslave;

import static ru.m210projects.Build.Gameutils.*;
import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Powerslave.Globals.*;
import static ru.m210projects.Powerslave.Main.*;

import ru.m210projects.Build.Architecture.BuildGdx;


import ru.m210projects.Build.Settings.BuildSettings;

public class Palette {

	public static int nPalDiff, nPalDelay;
	public static int rtint, btint, gtint;

	public static byte[] curpal = new byte[768];

	public static String[] cinpalfname = { "3454.pal", "3452.pal", "3449.pal", "3445.pal", "set.pal", "3448.pal",
			"3446.pal", "hsc1.pal", "2972.pal", "2973.pal", "2974.pal", "2975.pal", "2976.pal", "heli.pal", "2978.pal",
			"terror.pal" };
	public static byte[] cinemapal = new byte[768];

	public static void TintPalette(int r, int g, int b) {
		r = BClipRange(r, (r != 0 && r < 5) ? 5 : 0, 63);
		g = BClipRange(g, (g != 0 && g < 5) ? 5 : 0, 63);
		b = BClipRange(b, (b != 0 && b < 5) ? 5 : 0, 63);

		if (g != 0 && gtint > 8)
			return;

		gtint += g;

		if (r != 0 && rtint > 64)
			return;

		rtint += r;
		btint += b;

		int nDiff = r; //klabs(r) Is it really need?
		if (nDiff <= g)
			nDiff = g;
		if (nDiff <= b) 
			nDiff = b;
		nPalDiff += nDiff;

			for(int i = 0; i < 256; i++)
			{
				curpal[3 * i + 0] = (byte) BClipHigh((curpal[3 * i + 0] & 0xFF) + r, 63);
				curpal[3 * i + 1] = (byte) BClipHigh((curpal[3 * i + 1] & 0xFF) + g, 63);
				curpal[3 * i + 2] = (byte) BClipHigh((curpal[3 * i + 2] & 0xFF) + b, 63);
			}
		
		nPalDelay = 0;
	}

	public static void LoadCinemaPalette(int pal) {
		int num = pal - 1;
		if (num >= 0 && num < 16) {
			System.arraycopy(BuildGdx.cache.getBytes(cinpalfname[num], 1), 0, cinemapal, 0, 768);
		}
	}

	public static void FixPalette() {
		if (nPalDiff == 0)
			return;
		
		if (nPalDelay-- > 0) 
			return;

		nPalDelay = 5;

			for (int i = 0; i < 768; i++) {
				int dP = (curpal[i] & 0xFF) - (palette[i] & 0xFF);
				if (dP > 0) {
					if (dP <= 5)
						curpal[i] = palette[i];
					else curpal[i] -= 5;
				}
			engine.setbrightness(BuildSettings.paletteGamma.get(), curpal, true);
		}
		
		nPalDiff = BClipLow(nPalDiff - 5, 0);
		rtint = BClipLow(rtint - 5, 0);
		gtint = BClipLow(gtint - 5, 0);
		btint = BClipLow(btint - 5, 0);
	}

	public static boolean bGreenPalette;
	public static void SetGreenPal() {
		if(bGreenPalette)
			return;
		
		for (int i = 0; i < 12; i++)
			palookup[i] = palookup[6];
		palookup[5] = origpalookup[5];
		bGreenPalette = true;
		
		System.err.println("SetGreen");
	}

	public static void RestoreGreenPal() {
		if(!bGreenPalette)
			return;
		
		for (int i = 0; i < 12; i++)
			palookup[i] = origpalookup[i];
		bGreenPalette = false;
		
		System.err.println("RestoreGreenPal");
	}

	public static void GrabPalette() {
		System.arraycopy(palette, 0, curpal, 0, 768);
		engine.setbrightness(BuildSettings.paletteGamma.get(), curpal, true);

		nPalDiff = 0;
		nPalDelay = 0;
		btint = 0;
		gtint = 0;
		rtint = 0;
	}
}
