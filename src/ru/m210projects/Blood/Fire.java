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

import static ru.m210projects.Blood.Gameutils.ClipLow;
import static ru.m210projects.Blood.Gameutils.bRandom;
import static ru.m210projects.Blood.Gameutils.vRandom;
import static ru.m210projects.Blood.Main.*;
import static ru.m210projects.Build.Engine.gotpic;
import static ru.m210projects.Build.Engine.totalclock;

import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Build.Types.Tile;

public class Fire {

	private final short kSize = 128;
	private final int kSeedLines = 3;
	private final int kPorchLines = 4;
	private final int kSeedBuffers = 16;
	private final int kPicFire = 2342;

	private final int fireSize = kSize;
	private final byte[] FrameBuffer;
	private final byte[][] SeedBuffer = new byte[kSeedBuffers][kSize];
	private final byte[] CoolTable = new byte[1024];
	private final int gDamping = 7; // use 7 for 64x64, 4 for 128x128
	private final byte[] gCLU;
	private int gFireClock;

	public Fire() throws Exception {
		Console.Println("Initializing dynamic fire");
		Tile pic = engine.getTile(kPicFire);

		if ((kSize != pic.getWidth() && pic.getWidth() != 0) || (kSize != pic.getHeight() && pic.getHeight() != 0))
			throw new Exception("Fire tile size(#2342) is wrong!");

		FrameBuffer = new byte[kSize * (kSize + kPorchLines + kSeedLines)];
		BuildCoolTable();
		InitSeedBuffers();

		gCLU = BuildGdx.cache.getBytes("RFIRE.CLU", 0);
		if (gCLU == null)
			throw new Exception("RFIRE.CLU not found");

		// run a few frame to start the fire
		for (int i = 0; i < 50; i++)
			update();
	}

	public int getPicture() {
		return kPicFire;
	}

	private void InitSeedBuffers() {
		int i, j;
		byte c;

		for (i = 0; i < kSeedBuffers; i++) {
			for (j = 0; j < fireSize; j += 2) {
				c = (byte) bRandom();
				SeedBuffer[i][j] = c;
				SeedBuffer[i][j + 1] = c;
			}
		}
	}

	public void process() {
		if (totalclock < gFireClock || ((gFireClock + 2) < totalclock)) {
			if ((gotpic[kPicFire >> 3] & (1 << kPicFire)) != 0)
				update();
			gFireClock = totalclock;
		}
	}

	private void BuildCoolTable() {
		for (int i = 0; i < 1024; i++)
			CoolTable[i] = (byte) ClipLow((i - gDamping) / 4, 0);
	}

	private void update() {
		int nSeed = vRandom() & (kSeedBuffers - 1); // we need a secondary random number generator for view stuff!

		for (int i = 0; i < kSeedLines; i++) {
			System.arraycopy(SeedBuffer[nSeed], 0, FrameBuffer, (kSize + kPorchLines + i) * 128, kSize);
		}

		CellularFrame(FrameBuffer, kSize, kSize + kPorchLines);

		byte[] FireData = engine.loadtile(kPicFire);
		if (FireData == null) {
			FireData = new byte[kSize * kSize];

			Tile pic = engine.getTile(kPicFire);
			pic.setWidth(kSize).setHeight(kSize);
		}

		UpdateTile(gCLU, FireData);
	}

	private void CellularFrame(byte[] buff, int cols, int rows) {
		int size, idx = 0, pos, o = 0, k;
		size = rows * cols;
		pos = 0;
		while (true) {
			do {
				for (k = 0; k < 3; k++) {
					o = cols + pos - 1;
					idx = (buff[o] & 0xFF) + (buff[o + 1] & 0xFF) + (buff[o + 2] & 0xFF)
							+ (buff[o + cols + 1] & 0xFF);
					if ((buff[o + cols + 1] & 0xFF) > 96) {
						break;
					}
					buff[pos] = CoolTable[idx];
					pos++;
					if (pos >= size)
						return;
				}
			} while ((buff[o + cols + 1] & 0xFF) <= 96);
			idx = (idx + (buff[o + cols] & 0xFF) + (buff[o + cols + 1] & 0xFF) + (buff[o + cols + 2] & 0xFF)
					+ (buff[(o + cols * 2) + 1] & 0xFF)) / 2;

			buff[pos] = CoolTable[idx];
			pos++;
			if (pos >= size)
				return;

			o = cols + pos - 1;

			idx = (buff[o] & 0xFF) + (buff[o + 1] & 0xFF) + (buff[o + 2] & 0xFF) + (buff[o + cols + 1] & 0xFF);

			if ((buff[o + cols + 1] & 0xFF) > 96) {
				idx = (idx + (buff[o + cols] & 0xFF) + (buff[o + cols + 1] & 0xFF) + (buff[o + cols + 2] & 0xFF)
						+ (buff[o + (cols * 2) + 1] & 0xFF)) / 2;
			}

			buff[pos] = CoolTable[idx];
			pos++;
			if (pos >= size)
				return;
		}
	}

	private void UpdateTile(byte[] pCLU, byte[] pTile) {
		int i, j, k;
		k = 0;
		// rowLoop(edx)
		for (i = 0; i < fireSize; i++) {
			// colLoop(ecx)
			for (j = 0; j < fireSize; j++) {
				pTile[i + (j * fireSize)] = pCLU[FrameBuffer[k] & 0xFF];
				k++;
			}
		}

		engine.getrender().invalidatetile(kPicFire, -1, -1);
	}
}
