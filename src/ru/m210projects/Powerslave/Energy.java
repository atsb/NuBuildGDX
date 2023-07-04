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

import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Build.Pragmas.*;
import static ru.m210projects.Powerslave.Globals.*;
import static ru.m210projects.Powerslave.Random.*;
import static ru.m210projects.Powerslave.Main.*;

import java.util.Arrays;

import ru.m210projects.Build.Types.Tile;

public class Energy {

	public static byte[] energytile = new byte[66 * 66];

	public static void InitEnergyTile() {
		Arrays.fill(energytile, (byte) 96);
		Arrays.fill(engine.loadtile(3605), (byte) 255);

		nSmokeSparks = 1; // force to update sparks
		DoEnergyTile();
		nSmokeSparks = 0;
	}

	public static void DoEnergyTile() {
		Tile pic = engine.getTile(3604);

		if (!pic.isLoaded())
			engine.loadtile(3604);
		byte[] pEnergyTile = pic.data;

		int ptr1 = 1984;
		int ptr2 = 2048;
		nButtonColor += nButtonColor < 0 ? 8 : 0;
		byte col = (byte) (nButtonColor + 161);

		for (int i = 0; i < 32; i++) {
			Arrays.fill(pEnergyTile, ptr1, ptr1 + 64, col);
			Arrays.fill(pEnergyTile, ptr2, ptr2 + 64, col);
			ptr1 -= 64;
			ptr2 += 64;
			if (++col >= (byte) 168)
				col = (byte) 160;
		}
		engine.getrender().invalidatetile(3604, -1, -1);

		if (nSmokeSparks != 0) {
			int ptr = 67;
			int data = 0;
			byte[] tex = engine.loadtile(3605);

			if (tex == null || tex.length < 64 * 64)
				return;

			for (int k = 0; k < 64; k++) {
				for (int i = 0; i < 64; i++, ptr++, data++) {
					if (energytile[ptr] == 96) {
						if (RandomBit2() != 0) {
							tex[data] = (byte) 96;
							continue;
						}

						int index = energytile[ptr + 1] & 0xFF;
						if (index <= (energytile[ptr - 1] & 0xFF))
							index = energytile[ptr - 1] & 0xFF;
						if (index <= (energytile[ptr - 66] & 0xFF))
							index = energytile[ptr - 66] & 0xFF;
						if (index <= (energytile[ptr + 66] & 0xFF))
							index = energytile[ptr + 66] & 0xFF;
						if (index <= (energytile[ptr + 66] & 0xFF))
							index = energytile[ptr + 66] & 0xFF;
						if (index <= (energytile[ptr + 66] & 0xFF))
							index = energytile[ptr + 66] & 0xFF;
						if (index <= (energytile[ptr - 65] & 0xFF))
							index = energytile[ptr - 65] & 0xFF;
						if (index <= (energytile[ptr - 67] & 0xFF))
							index = energytile[ptr - 67] & 0xFF;

						if (index > 159) {
							if (RandomBit2() == 0)
								tex[data] = (byte) (index - 1);
							else
								tex[data] = (byte) index;
						} else
							tex[data] = (byte) 96;
					} else {
						if ((energytile[ptr] & 0xFF) > 158)
							tex[data] = (byte) ((energytile[ptr] & 0xFF) - 1);
						else
							tex[data] = (byte) 96;
					}
				}
				ptr += 2;
			}

			ptr = 67;
			data = 0;
			for (int i = 0; i < 64; i++) {
				System.arraycopy(tex, data, energytile, ptr, 64);
				ptr += 66;
				data += 64;
			}

			for (int i = 0; i < 4096; i++) {
				if (tex[i] == 96)
					tex[i] = (byte) 255;
			}

			int x = (RandomSize(5) & 0x1F) + 16;
			int y = (RandomSize(5) & 0x1F) + 16;
			energytile[66 * x + y] = (byte) 175;

			engine.getrender().invalidatetile(3605, 1, -1);
		}
	}

	private static int nRandomBit = 31;
	private static int nRandom = 1103500926;

	public static byte RandomBit2() {
		long result = nRandom & 1;
		if (--nRandomBit > 0) {
			nRandom = (int) ((result << 31) | (nRandom >>> 1));
		} else {
			nRandomBit = 31;
			nRandom ^= nRandom >>> 4;
		}
		return (byte) result;
	}

	public static long RandomLong2() {
		long result = 0;
		for (int i = 0; i < 32; i++)
			result = (RandomBit2() | (result << 1));
		return result & 0xFFFFFFFFL;
	}

//	private static byte[] SmokeBuffer = new byte[320 * 80];
	private static byte[][] SmokeBuffer = new byte[2][320 * 80];
	private static int currentBuffer = 0;
	private static int[] sx = new int[5];
	private static int[] dword_1BF71D = new int[5];
	private static int[] dword_1BF709 = new int[5];

	private static int nSmokeTexture = 4092;
	public static boolean lockbyte4092 = false;

	public static int nSmokeLeft;
	public static int nSmokeRight;
	public static int nSmokeTop;
	public static int nSmokeBottom;

	public static long time;

	public static void DoPlasma(int x, int y, int nScale) {
		Tile pic = engine.getTile(LOGO);

		if (!lockbyte4092) {
			engine.allocatepermanenttile(4092, 320, 80);
			Arrays.fill(SmokeBuffer[0], (byte) 96);
			Arrays.fill(SmokeBuffer[1], (byte) 96);

			if (!pic.hasSize())
				return;

			nSmokeLeft = 160 - pic.getWidth() / 2;
			nSmokeRight = nSmokeLeft + pic.getWidth();
			nSmokeTop = 40 - pic.getHeight() / 2;
			nSmokeBottom = nSmokeTop + pic.getHeight() - 1;

			for (int i = 0; i < 5; i++) {
				sx[i] = (nSmokeLeft + engine.rand() % pic.getWidth()) << 16;
				dword_1BF71D[i] = 0x10000 + (int) (RandomLong2() % 0x50000);
				if (RandomBit2() != 0)
					dword_1BF71D[i] = -dword_1BF71D[i];
				dword_1BF709[i] = RandomBit2();
			}
			time = System.currentTimeMillis() - 100;
			lockbyte4092 = true;
		}

		int ptr1 = 81;
		int ptr2 = 81;
		byte[] tex = SmokeBuffer[currentBuffer];
		byte[] src = SmokeBuffer[currentBuffer ^ 1];

		if ((System.currentTimeMillis() - time) >= 30) {
			for (int k = 1; k < 318; k++) {
				for (int i = 1; i < 79; i++, ptr1++, ptr2++) {
					if (src[ptr1] == 96) {
						if (RandomBit2() != 0) {
							tex[ptr2] = (byte) 96;
							continue;
						}

						int index = src[ptr1 + 1] & 0xFF;
						if (index <= (src[ptr1 - 1] & 0xFF))
							index = src[ptr1 - 1] & 0xFF;
						if (index <= (src[ptr1 - 80] & 0xFF))
							index = src[ptr1 - 80] & 0xFF;
						if (index <= (src[ptr1 + 80] & 0xFF))
							index = src[ptr1 + 80] & 0xFF;
						if (index <= (src[ptr1 + 80] & 0xFF))
							index = src[ptr1 + 80] & 0xFF;
						if (index <= (src[ptr1 + 80] & 0xFF))
							index = src[ptr1 + 80] & 0xFF;
						if (index <= (src[ptr1 - 79] & 0xFF))
							index = src[ptr1 - 79] & 0xFF;
						if (index <= (src[ptr1 - 81] & 0xFF))
							index = src[ptr1 - 81] & 0xFF;

						if (index > 159) {
							if (RandomBit2() == 0)
								tex[ptr2] = (byte) (index - 1);
							else
								tex[ptr2] = (byte) index;
						} else
							tex[ptr2] = (byte) 96;
					} else {
						if ((src[ptr1] & 0xFF) > 158)
							tex[ptr2] = (byte) ((src[ptr1] & 0xFF) - 1);
						else
							tex[ptr2] = (byte) 96;
					}
				}
				ptr1 += 2;
				ptr2 += 2;
			}

			if (!pic.isLoaded())
				engine.loadtile(LOGO);
			byte[] pLogo = pic.data;

			for (int i = 0; i < 5; i++) {
				ptr1 = ((sx[i] >> 16) - nSmokeLeft) * pic.getHeight();

				sx[i] += dword_1BF71D[i];
				if (dword_1BF71D[i] > 0 && (sx[i] >> 16) >= nSmokeRight
						|| dword_1BF71D[i] < 0 && (sx[i] >> 16) <= nSmokeLeft) {
					dword_1BF71D[i] = -dword_1BF71D[i];
					dword_1BF709[i] ^= 1;
				}

				int sy = 0;
				if (dword_1BF709[i] != 0) {
					for (sy = nSmokeTop; ptr1 >= 0 && ptr1 < pLogo.length
							&& (pLogo[ptr1] == -1 || pLogo[ptr1] == 96); sy++)
						ptr1++;
				} else {
					ptr1 += pic.getHeight() - 1;
					for (sy = nSmokeBottom; sy > nSmokeTop
							&& (ptr1 >= 0 && ptr1 < pLogo.length && (pLogo[ptr1] == -1 || pLogo[ptr1] == 96)); sy--)
						ptr1--;
				}

				tex[80 * (sx[i] >> 16) + sy] = (byte) 175;
			}

			for (int i = 0; i < tex.length; i++) {
				if (tex[i] != 96)
					engine.getTile(nSmokeTexture).data[i] = tex[i];
				else
					engine.getTile(nSmokeTexture).data[i] = (byte) 255;
			}

			engine.getrender().invalidatetile(nSmokeTexture, 0, -1);
			currentBuffer ^= 1;
			time = System.currentTimeMillis();
		}

		engine.rotatesprite((x - mulscale(160, nScale, 16)) << 16, (y - mulscale(40, nScale, 16)) << 16, nScale, 0,
				nSmokeTexture, 0, 0, 10 | 16, 0, 0, xdim - 1, ydim - 1);
		engine.rotatesprite(x << 16, y << 16, nScale, 0, LOGO, 0, 0, 10, 0, 0, xdim - 1, ydim - 1);
	}
}
