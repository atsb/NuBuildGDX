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
import static ru.m210projects.Build.Pragmas.*;
import static ru.m210projects.Powerslave.Globals.*;
import static ru.m210projects.Powerslave.Main.engine;
import static ru.m210projects.Powerslave.Random.*;
import static ru.m210projects.Powerslave.View.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static ru.m210projects.Powerslave.Sound.*;
import static ru.m210projects.Build.Engine.*;

import ru.m210projects.Build.FileHandle.Resource;
import ru.m210projects.Build.Render.GLRenderer;
import ru.m210projects.Build.Types.SECTOR;
import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Build.Types.WALL;
import ru.m210projects.Powerslave.Type.FlashStruct;
import ru.m210projects.Powerslave.Type.FlickerStruct;
import ru.m210projects.Powerslave.Type.FlowStruct;
import ru.m210projects.Powerslave.Type.GrowStruct;
import ru.m210projects.Powerslave.Type.SafeLoader;

public class Light {

	public static int bDoFlicks;
	public static int bDoGlows;
	private static int nFlickerCount;
	private static int nGlowCount;
	private static int nFlowCount;
	private static int nFlashes;
	private static int nFirstFlash = -1;
	private static int nLastFlash = -1;
	private static short nFreeFlash[] = new short[2000];
	private static short nNextFlash[] = new short[2000];

	private static FlashStruct[] sFlash = new FlashStruct[2000];
	private static GrowStruct[] sGlow = new GrowStruct[50];
	private static FlickerStruct[] sFlicker = new FlickerStruct[100];
	private static FlowStruct[] sFlowInfo = new FlowStruct[375];

	public static int flickermask[] = new int[25];
	public static int nFlashDepth = 2;

	public static FlashStruct GrabFlash() {
		if (nFlashes < 2000) {
			short nFlash = nFreeFlash[nFlashes++];
			nNextFlash[nFlash] = -1;
			if (nLastFlash <= -1)
				nFirstFlash = nFlash;
			else
				nNextFlash[nLastFlash] = nFlash;
			nLastFlash = nFlash;
			if (sFlash[nFlash] == null)
				sFlash[nFlash] = new FlashStruct();

			return sFlash[nFlash];
		}

		return null;
	}

	public static ByteBuffer saveLights() {
		ByteBuffer bb = ByteBuffer
				.allocate(6 + (4 * 2000) + (FlashStruct.size * nFlashes) + (GrowStruct.size * nGlowCount) + 2
						+ (FlowStruct.size * nFlowCount) + 2 + (FlickerStruct.size * nFlickerCount) + 2);
		bb.order(ByteOrder.LITTLE_ENDIAN);

		saveFlashes(bb);
		saveGlows(bb);
		saveFlows(bb);
		saveFlickers(bb);

		return bb;
	}

	public static void loadLights(SafeLoader loader, Resource bb) {
		loadFlashes(loader, bb);
		loadGlows(loader, bb);
		loadFlows(loader, bb);
		loadFlickers(loader, bb);

		if (bb != null)
			return;

		for (int i = 0; i < 25; i++)
			flickermask[i] = 2 * RandomSize(31);
		bDoFlicks = 0;
		bDoGlows = 0;
	}

	private static void saveFlashes(ByteBuffer bb) {
		bb.putShort((short) nFlashes);
		bb.putShort((short) nFirstFlash);
		bb.putShort((short) nLastFlash);
		for (int i = 0; i < 2000; i++) {
			bb.putShort(nFreeFlash[i]);
			bb.putShort(nNextFlash[i]);
		}

		if (nFlashes != 0) {
			for (int i = nFirstFlash; i >= 0; i = nNextFlash[i])
				sFlash[i].save(bb);
		}
	}

	private static void loadFlashes(SafeLoader loader, Resource bb) {
		if (bb != null) {
			loader.nFlashes = bb.readShort();
			loader.nFirstFlash = bb.readShort();
			loader.nLastFlash = bb.readShort();
			for (int i = 0; i < 2000; i++) {
				loader.nFreeFlash[i] = bb.readShort();
				loader.nNextFlash[i] = bb.readShort();
			}

			if (loader.nFlashes != 0) {
				for (int i = loader.nFirstFlash; i >= 0; i = loader.nNextFlash[i]) {
					if (loader.sFlash[i] == null)
						loader.sFlash[i] = new FlashStruct();
					loader.sFlash[i].load(bb);
				}
			}
		} else {
			nFlashes = loader.nFlashes;
			nFirstFlash = loader.nFirstFlash;
			nLastFlash = loader.nLastFlash;

			System.arraycopy(loader.nFreeFlash, 0, nFreeFlash, 0, 2000);
			System.arraycopy(loader.nNextFlash, 0, nNextFlash, 0, 2000);

			if (nFlashes != 0) {
				for (int i = nFirstFlash; i >= 0; i = nNextFlash[i]) {
					if (sFlash[i] == null)
						sFlash[i] = new FlashStruct();
					sFlash[i].copy(loader.sFlash[i]);
				}
			}
		}
	}

	private static void saveGlows(ByteBuffer bb) {
		bb.putShort((short) nGlowCount);
		for (int i = 0; i < nGlowCount; i++)
			sGlow[i].save(bb);
	}

	private static void loadGlows(SafeLoader loader, Resource bb) {
		if (bb != null) {
			loader.nGlowCount = bb.readShort();
			for (int i = 0; i < loader.nGlowCount; i++) {
				if (loader.sGlow[i] == null)
					loader.sGlow[i] = new GrowStruct();
				loader.sGlow[i].load(bb);
			}
		} else {
			nGlowCount = loader.nGlowCount;
			for (int i = 0; i < loader.nGlowCount; i++) {
				if (sGlow[i] == null)
					sGlow[i] = new GrowStruct();
				sGlow[i].copy(loader.sGlow[i]);
			}
		}
	}

	private static void saveFlows(ByteBuffer bb) {
		bb.putShort((short) nFlowCount);
		for (int i = 0; i < nFlowCount; i++)
			sFlowInfo[i].save(bb);
	}

	private static void loadFlows(SafeLoader loader, Resource bb) {
		if (bb != null) {
			loader.nFlowCount = bb.readShort();
			for (int i = 0; i < loader.nFlowCount; i++) {
				if (loader.sFlowInfo[i] == null)
					loader.sFlowInfo[i] = new FlowStruct();
				loader.sFlowInfo[i].load(bb);
			}
		} else {
			nFlowCount = loader.nFlowCount;
			for (int i = 0; i < loader.nFlowCount; i++) {
				if (sFlowInfo[i] == null)
					sFlowInfo[i] = new FlowStruct();
				sFlowInfo[i].copy(loader.sFlowInfo[i]);
			}
		}
	}

	private static void saveFlickers(ByteBuffer bb) {
		bb.putShort((short) nFlickerCount);
		for (int i = 0; i < nFlickerCount; i++)
			sFlicker[i].save(bb);
	}

	private static void loadFlickers(SafeLoader loader, Resource bb) {
		if (bb != null) {
			loader.nFlickerCount = bb.readShort();
			for (int i = 0; i < loader.nFlickerCount; i++) {
				if (loader.sFlicker[i] == null)
					loader.sFlicker[i] = new FlickerStruct();
				loader.sFlicker[i].load(bb);
			}
		} else {
			nFlickerCount = loader.nFlickerCount;
			for (int i = 0; i < loader.nFlickerCount; i++) {
				if (sFlicker[i] == null)
					sFlicker[i] = new FlickerStruct();
				sFlicker[i].copy(loader.sFlicker[i]);
			}
		}
	}

	public static void InitLights() {
		nFlickerCount = 0;
		for (int i = 0; i < 25; i++)
			flickermask[i] = 2 * RandomSize(31);

		nGlowCount = 0;
		nFlowCount = 0;
		nFlashes = 0;
		bDoFlicks = 0;
		bDoGlows = 0;

		for (int i = 0; i < 2000; i++) {
			nFreeFlash[i] = (short) i;
			nNextFlash[i] = -1;
		}

		nFirstFlash = -1;
		nLastFlash = -1;
	}

	public static void AddFlash(int sectnum, int x, int y, int z, int a5) {
		int nDepth = a5 >> 8;
		int nShade = 0;
		if (nDepth < nFlashDepth) {
			int v37 = a5 & 0x80;
			int v42 = ((nDepth + 1) << 8) | (a5 & 0xFF);

			SECTOR pSector = sector[sectnum];

			int v40 = 0;
			int i = pSector.wallptr;
			for (int n = 0; n < pSector.wallnum; n++, i++) {
				WALL pWall = wall[i];
				int cx = (wall[pWall.point2].x + pWall.x) / 2;
				int cy = (wall[pWall.point2].y + pWall.y) / 2;
				int nNextSector = pWall.nextsector;
				SECTOR pNextSector;
				if (nNextSector <= -1)
					pNextSector = null;
				else
					pNextSector = sector[nNextSector];

				int v14 = -255;
				if ((a5 & 0x40) == 0)
					v14 = ((klabs(x - cx) + klabs(y - cy)) >> 4) - 255;

				if (v14 < 0) {
					v40++;
					nShade += v14;
					if (pWall.pal < 5 && (pNextSector == null || pNextSector.floorz < pSector.floorz)) {
						FlashStruct pFlash = GrabFlash();
						if (pFlash == null)
							return;

						pFlash.field_0 = (byte) (v37 | 2);
						pFlash.nShade = pWall.shade;
						pFlash.nObject = (short) i;
						pWall.shade = (byte) BClipLow(v14 + pWall.shade, -127);
						pWall.pal += 7;
						if (nDepth == 0 && pWall.overpicnum == 0 && pNextSector != null)
							AddFlash(pWall.nextsector, x, y, z, v42);
					}
				}
			}

			if (v40 == 0 || pSector.floorpal >= 4)
				return;

			FlashStruct v21 = GrabFlash();
			if (v21 == null)
				return;

			v21.field_0 = (byte) (v37 | 1);
			v21.nShade = pSector.floorshade;
			v21.nObject = (short) sectnum;
			pSector.floorshade = (byte) BClipLow(nShade + pSector.floorshade, -127);
			pSector.floorpal += 7;

			if ((pSector.ceilingstat & 1) == 0) {
				if (pSector.ceilingpal < 4) {
					FlashStruct v23 = GrabFlash();
					if (v23 != null) {
						v23.field_0 = (byte) (v37 | 3);
						v23.nShade = pSector.ceilingshade;
						v23.nObject = (short) sectnum;
						pSector.ceilingshade = (byte) BClipLow(nShade + pSector.ceilingshade, -127);
						pSector.ceilingpal += 7;
					}
				}
			}

			for (short j = headspritesect[sectnum]; j != -1; j = nextspritesect[j]) {
				SPRITE pSprite = sprite[j];
				if (pSprite.pal < 4) {
					FlashStruct pFlash = GrabFlash();
					if (pFlash != null) {
						pFlash.field_0 = (byte) (v37 | 4);
						pFlash.nShade = pSprite.shade;
						pFlash.nObject = j;
						pSprite.pal += 7;

						int v14 = -255;
						if ((a5 & 0x40) == 0)
							v14 = ((klabs(x - pSprite.x) + klabs(y - pSprite.y)) >> 4) - 255;

						if (v14 < 0)
							pSprite.shade = (byte) BClipLow(pSprite.shade + v14, -127);
					}
				}
			}
		}
	}

	public static void UndoFlashes() {
		int nNext = -1;
		if (nFlashes != 0) {
			for (int i = nFirstFlash; i >= 0; i = nNextFlash[i]) {
				FlashStruct v3 = sFlash[i];
				int nObject = v3.nObject;

				switch ((v3.field_0 & 0x3F) - 1) {
				case 0:
					if ((v3.field_0 & 0x80) != 0 && (sector[nObject].floorshade + 6) < v3.nShade) {
						nNext = i;
						sector[nObject].floorshade += 6;
						continue;
					} else {
						sector[nObject].floorpal -= 7;
						sector[nObject].floorshade = v3.nShade;
					}
					break;
				case 1:
					if ((v3.field_0 & 0x80) != 0 && (wall[nObject].shade + 6) < v3.nShade) {
						nNext = i;
						wall[nObject].shade += 6;
						continue;
					} else {
						wall[nObject].pal -= 7;
						wall[nObject].shade = v3.nShade;
					}
					break;
				case 2:
					if ((v3.field_0 & 0x80) != 0 && (sector[nObject].ceilingshade + 6) < v3.nShade) {
						nNext = i;
						sector[nObject].ceilingshade += 6;
						continue;
					} else {
						sector[nObject].ceilingpal -= 7;
						sector[nObject].ceilingshade = v3.nShade;
					}
					break;
				case 3:
					if (sprite[nObject].pal < 7)
						break;

					if ((v3.field_0 & 0x80) != 0 && (sprite[nObject].shade + 6) < v3.nShade) {
						nNext = i;
						sprite[nObject].shade += 6;
						continue;
					} else {
						sprite[nObject].pal -= 7;
						sprite[nObject].shade = v3.nShade;
					}
					break;
				}

				nFreeFlash[--nFlashes] = (short) i;
				if (nNext != -1)
					nNextFlash[nNext] = nNextFlash[i];
				if (i == nFirstFlash)
					nFirstFlash = nNextFlash[nFirstFlash];
				if (i == nLastFlash)
					nLastFlash = nNext;
			}
		}
	}

	public static void AddGlow(int a1, int a2) {
		if (nGlowCount < 50) {
			if (sGlow[nGlowCount] == null)
				sGlow[nGlowCount] = new GrowStruct();
			sGlow[nGlowCount].field_6 = (short) a2;
			sGlow[nGlowCount].field_4 = (short) a1;
			sGlow[nGlowCount].field_0 = -1;
			sGlow[nGlowCount].field_2 = 0;
			nGlowCount++;
		}
	}

	public static void AddFlicker(int a1, int a2) {
		if (nFlickerCount < 100) {
			if (sFlicker[nFlickerCount] == null)
				sFlicker[nFlickerCount] = new FlickerStruct();
			sFlicker[nFlickerCount].field_0 = (short) a2;
			sFlicker[nFlickerCount].field_2 = (short) a1;
			if (a2 >= 25)
				a2 = 24;
			sFlicker[nFlickerCount++].field_4 = flickermask[a2];
		}
	}

	public static void DoGlows() {
		if (++bDoGlows < 3)
			return;

		int v0 = 0;
		int v1 = 0;
		bDoGlows = 0;
		while (v0 < nGlowCount) {
			int v2 = sGlow[v1].field_2 + 1;
			int v3 = sGlow[v1].field_4;
			sGlow[v1].field_2 = (short) v2;
			int v4 = sGlow[v1].field_0;
			if (v2 >= sGlow[v1].field_6) {
				sGlow[v1].field_2 = 0;
				sGlow[v1].field_0 = (short) -sGlow[v1].field_0;
			}
			int v5 = v3;
			sector[v5].ceilingshade += v4;
			sector[v5].floorshade += v4;
			int v6 = sector[v5].wallptr;
			int v8 = v6 + sector[v5].wallnum - 1;
			for (int i = v8; i >= v6; i--)
				wall[i].shade += v4;

			++v1;
			++v0;
		}
	}

	public static void DoFlickers() {
		bDoFlicks = bDoFlicks ^ 1;
		if (bDoFlicks == 0)
			return;
		int v0 = 0;
		int v1 = 0;
		while (v0 < nFlickerCount) {
			int v2 = sFlicker[v1].field_4 & 1;
			int v3 = sFlicker[v1].field_2;
			int v4 = (sFlicker[v1].field_4 >>> 1) & 1;
			sFlicker[v1].field_4 = (sFlicker[v1].field_4 << 31) | (sFlicker[v1].field_4 >>> 1);

			if ((v2 ^ v4) != 0) {
				int v5 = sFlicker[v1].field_0;
				if (v2 == 0)
					v5 = -sFlicker[v1].field_0;

				sector[v3].ceilingshade += v5;
				sector[v3].floorshade += v5;
				for (int i = sector[v3].wallptr + sector[v3].wallnum - 1; i >= sector[v3].wallptr; i--)
					wall[i].shade += v5;
			}
			++v1;
			++v0;
		}
	}

	public static void AddFlow(short nObject, int nVelocity, int nState) {
		if (nFlowCount < 375) {
			if (sFlowInfo[nFlowCount] == null)
				sFlowInfo[nFlowCount] = new FlowStruct();

			FlowStruct pFlow = sFlowInfo[nFlowCount];
			nFlowCount++;

			switch (nState) {
			case 0:
			case 1:
				SPRITE pSprite = sprite[nObject];
				nObject = pSprite.sectnum;
				SECTOR pSector = sector[nObject];

				pFlow.xmax = (engine.getTile(pSector.floorpicnum).getWidth() << 14) - 1;
				pFlow.ymax = (engine.getTile(pSector.floorpicnum).getHeight() << 14) - 1;
				pFlow.xvel = nVelocity * -sintable[(pSprite.ang + 512) & 0x7FF];
				pFlow.yvel = nVelocity * sintable[pSprite.ang & 0x7FF];
				break;
			case 2:
			case 3:
				int nDirection = 1536;
				if (nState == 2)
					nDirection = 512;

				WALL pWall = wall[nObject];
				pFlow.xmax = pWall.xrepeat * engine.getTile(pWall.picnum).getWidth() << 8;
				pFlow.ymax = pWall.yrepeat * engine.getTile(pWall.picnum).getHeight() << 8;
				pFlow.xvel = nVelocity * -sintable[(nDirection + 512) & 0x7FF];
				pFlow.yvel = nVelocity * sintable[nDirection & 0x7FF];
				break;
			}

			pFlow.xpanning = pFlow.ypanning = 0;
			pFlow.nObject = nObject;
			pFlow.nState = (short) nState;
		}
	}

	public static void DoFlows() {
		for (int i = 0; i < nFlowCount; i++) {
			FlowStruct pFlow = sFlowInfo[i];
			pFlow.xpanning += pFlow.xvel;
			pFlow.ypanning += pFlow.yvel;

			switch (pFlow.nState) {
			case 0:
				pFlow.xpanning &= pFlow.xmax;
				pFlow.ypanning &= pFlow.ymax;
				sector[pFlow.nObject].floorxpanning = (short) (pFlow.xpanning >> 14);
				sector[pFlow.nObject].floorypanning = (short) (pFlow.ypanning >> 14);
				break;
			case 1:
				sector[pFlow.nObject].ceilingxpanning = (short) (pFlow.xpanning >> 14);
				sector[pFlow.nObject].ceilingypanning = (short) (pFlow.ypanning >> 14);
				pFlow.xpanning &= pFlow.xmax;
				pFlow.ypanning &= pFlow.ymax;
				break;
			case 2:
				wall[pFlow.nObject].xpanning = (short) (pFlow.xpanning >> 14);
				wall[pFlow.nObject].ypanning = (short) (pFlow.ypanning >> 14);
				if (pFlow.xpanning < 0)
					pFlow.xpanning += pFlow.xmax;
				if (pFlow.ypanning < 0)
					pFlow.ypanning += pFlow.ymax;
				break;
			case 3:
				wall[pFlow.nObject].xpanning = (short) (pFlow.xpanning >> 14);
				wall[pFlow.nObject].ypanning = (short) (pFlow.ypanning >> 14);
				if (pFlow.xpanning >= pFlow.xmax)
					pFlow.xpanning -= pFlow.xmax;
				if (pFlow.ypanning >= pFlow.ymax)
					pFlow.ypanning -= pFlow.ymax;
				break;
			}
		}
	}

	public static void DoLights() {
		DoFlickers();
		DoGlows();
		DoFlows();
	}

	public static void BuildFlash(int player, int sectnum, int value) {
		if (player == nLocalPlayer) {
			flash = -value;
		}
	}

	public static int bTorch;

	public static void SetTorch(int nPlayer, int nTorch) {
		if (nTorch != bTorch) {
			if (nPlayer == nLocalPlayer) {
				// 2, 9 torch.rmp
				// 3, 10 notorch.rmp
				SwapPalookup(palookup, 2, 3);
				SwapPalookup(palookup, 9, 10);

				if (nTorch == 2)
					bTorch = (bTorch == 0) ? 1 : 0;
				else
					bTorch = nTorch;
				if (bTorch != 0)
					PlayLocalSound(12, 0);

				final GLRenderer gl = engine.glrender();
				if (gl != null && gl.getTextureManager() != null) {
					gl.getTextureManager().invalidatepalookup(2);
					gl.getTextureManager().invalidatepalookup(3);
					gl.getTextureManager().invalidatepalookup(9);
					gl.getTextureManager().invalidatepalookup(10);
				}

				StatusMessage(150, "TORCH IS " + ((bTorch != 0) ? "LIT" : "OUT"), nPlayer);
			}
		}
	}

	public static void LoadTorch(int nTorch) {
		palookup[2] = origpalookup[2];
		palookup[3] = origpalookup[3];
		palookup[9] = origpalookup[9];
		palookup[10] = origpalookup[10];

		if (nTorch != 0) {
			SwapPalookup(palookup, 2, 3);
			SwapPalookup(palookup, 9, 10);
		}

		final GLRenderer gl = engine.glrender();
		if (gl != null && gl.getTextureManager() != null) {
			gl.getTextureManager().invalidatepalookup(2);
			gl.getTextureManager().invalidatepalookup(3);
			gl.getTextureManager().invalidatepalookup(9);
			gl.getTextureManager().invalidatepalookup(10);
		}
	}

	private static void SwapPalookup(byte[][] palookup, int num1, int num2) {
		byte[] tmp = palookup[num1];
		palookup[num1] = palookup[num2];
		palookup[num2] = tmp;
	}

	public static void UseTorch(int nPlayer) {
		if (nPlayerTorch[nPlayer] == 0)
			SetTorch(nPlayer, 1);
		nPlayerTorch[nPlayer] = 900;
	}
}
