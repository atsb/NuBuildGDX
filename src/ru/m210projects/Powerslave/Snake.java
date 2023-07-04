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
import static ru.m210projects.Powerslave.Globals.*;
import static ru.m210projects.Powerslave.Main.*;
import static ru.m210projects.Powerslave.Light.*;
import static ru.m210projects.Powerslave.RunList.*;
import static ru.m210projects.Powerslave.Seq.*;
import static ru.m210projects.Powerslave.Bullet.*;
import static ru.m210projects.Powerslave.Anim.*;
import static ru.m210projects.Powerslave.Sound.*;
import static ru.m210projects.Powerslave.Sprites.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

import ru.m210projects.Build.FileHandle.Resource;
import ru.m210projects.Powerslave.Type.SafeLoader;
import ru.m210projects.Powerslave.Type.SnakeStruct;

public class Snake {
	
	public static final int MAX_SNAKES = 50;

	private static int nSnakesFree;
	private static short SnakeFree[] = new short[MAX_SNAKES];
	private static short[] nSnakePlayer = new short[MAX_SNAKES];
	public static SnakeStruct[] SnakeList = new SnakeStruct[MAX_SNAKES];

	public static void InitSnakes() {
		for (short i = 0; i < MAX_SNAKES; i++)
			SnakeFree[i] = i;
		nSnakesFree = MAX_SNAKES;
		Arrays.fill(nPlayerSnake, (short) 0);
	}
	
	public static ByteBuffer saveSnakes()
	{
		int nSnakes = 0;
		for (int i = 0; i < MAX_SNAKES; i++) {
			if(SnakeList[i] != null && SnakeList[i].nFunc != -1) 
				nSnakes++;
		}
		
		ByteBuffer bb = ByteBuffer.allocate(2 + 4 * MAX_SNAKES + ((SnakeStruct.size + 2) * nSnakes));
		bb.order(ByteOrder.LITTLE_ENDIAN);
		
		bb.putShort((short)nSnakesFree);
		for(int i = 0; i < MAX_SNAKES; i++) {
			bb.putShort(SnakeFree[i]);
			bb.putShort(nSnakePlayer[i]);
		}
		
		if(nSnakes != 0)
		{
			for (int i = 0; i < MAX_SNAKES; i++) {
				if(SnakeList[i] != null && SnakeList[i].nFunc != -1) {
					bb.putShort((short)i);
					SnakeList[i].save(bb);
				}
			}
		}

		return bb;
	}
	
	public static void loadSnakes(SafeLoader loader, Resource bb)
	{
		if(bb != null)
		{
			for (int i = 0; i < MAX_SNAKES; i++) 
				if(loader.SnakeList[i] != null)
					loader.SnakeList[i].nFunc = -1;
			
			loader.nSnakesFree = bb.readShort();
			for(int i = 0; i < MAX_SNAKES; i++) {
				loader.SnakeFree[i] = bb.readShort();
				loader.nSnakePlayer[i] = bb.readShort();
			}
			
			int nSnakes = (MAX_SNAKES - loader.nSnakesFree);
			while(nSnakes > 0)
			{
				short i = bb.readShort();
				if(loader.SnakeList[i] == null)
					loader.SnakeList[i] = new SnakeStruct();
				loader.SnakeList[i].load(bb);
				nSnakes--;
			}
		}
		else
		{
			nSnakesFree = loader.nSnakesFree;
			
			System.arraycopy(loader.SnakeFree, 0, SnakeFree, 0, MAX_SNAKES);
			System.arraycopy(loader.nSnakePlayer, 0, nSnakePlayer, 0, MAX_SNAKES);

			for (int i = 0; i < MAX_SNAKES; i++) {
				if(SnakeList[i] != null)
					SnakeList[i].nFunc = -1;
				
				if(loader.SnakeList[i] != null && loader.SnakeList[i].nFunc != -1) {
					if(SnakeList[i] == null)
						SnakeList[i] = new SnakeStruct();
					SnakeList[i].copy(loader.SnakeList[i]);
				}
			}
		}
	}

	public static int GrabSnake() {
		return SnakeFree[--nSnakesFree];
	}

	public static void DestroySnake(int a1) {
		SubRunRec(SnakeList[a1].nFunc);
		for (int i = 0; i < 8; i++) {
			short spr = SnakeList[a1].nSprite[i];
			DoSubRunRec(sprite[spr].lotag - 1);
			DoSubRunRec(sprite[spr].owner);
			engine.mydeletesprite(spr);
		}
		SnakeList[a1].nFunc = -1;
		SnakeFree[nSnakesFree] = (short) a1;
		nSnakesFree++;

		if (a1 == nSnakeCam)
			nSnakeCam = -1;
	}

	public static void ExplodeSnakeSprite(int a1, int a2) {
		int v3 = BulletInfo[5].force;
		if (nPlayerDouble[a2] > 0)
			v3 = 2 * BulletInfo[5].force;

		short old = sprite[a1].owner;
		sprite[a1].owner = PlayerList[a2].spriteId;
		RadialDamageEnemy(a1, v3, BulletInfo[5].inf_10);
		sprite[a1].owner = old;
		BuildAnim(-1, 23, 0, sprite[a1].x, sprite[a1].y, sprite[a1].z, sprite[a1].sectnum, 0x28, 4);
		AddFlash(sprite[a1].sectnum, sprite[a1].x, sprite[a1].y, sprite[a1].z, 128);
		StopSpriteSound(a1);
	}

	public static void BuildSnake(int a1, int a2) {
		if (nSnakesFree != 0) {
			short pspr = PlayerList[a1].spriteId;
			short sect = nPlayerViewSect[a1];
			short picnum = GetSeqPicnum(21, 0, 0);
			int v40 = a2 - 1280;

			engine.hitscan(sprite[pspr].x, sprite[pspr].y, v40 + sprite[pspr].z - 2560, sprite[pspr].sectnum,
					sintable[(sprite[pspr].ang + 512) & 0x7FF], sintable[sprite[pspr].ang & 0x7FF], 0, pHitInfo,
					CLIPMASK1);
			if (engine.ksqrt((pHitInfo.hitx - sprite[pspr].x) * (pHitInfo.hitx - sprite[pspr].x)
					+ (pHitInfo.hity - sprite[pspr].y) * (pHitInfo.hity - sprite[pspr].y)) >= (sintable[512] >> 4)) {
				int nTarget;
				if (pHitInfo.hitsprite < 0 || sprite[pHitInfo.hitsprite].statnum < 90
						|| sprite[pHitInfo.hitsprite].statnum > 199) {
					nTarget = sPlayerInput[a1].nTarget;
				} else
					nTarget = pHitInfo.hitsprite;

				int nSnake = GrabSnake();
				if(SnakeList[nSnake] == null)
					SnakeList[nSnake] = new SnakeStruct();

				int v55 = 0;
				int peer = -1;
				for (int i = 0; i < 8; i++) {
					short spr = engine.insertsprite(sect, (short) 202);
					if (spr < 0 || spr >= MAXSPRITES) {
						game.ThrowError("spr>=0 && spr<MAXSPRITES");
						return;
					}
					
					sprite[spr].owner = pspr;
					sprite[spr].picnum = picnum;
					if (i != 0) {
						sprite[spr].x = sprite[peer].x;
						sprite[spr].y = sprite[peer].y;
						sprite[spr].z = sprite[peer].z;
						short siz = (short) (40 - (i + v55));
						sprite[spr].xrepeat = siz;
						sprite[spr].yrepeat = siz;
					} else {
						sprite[spr].x = sprite[pspr].x;
						sprite[spr].y = sprite[pspr].y;
						sprite[spr].z = v40 + sprite[pspr].z;
						sprite[spr].xrepeat = 32;
						sprite[spr].yrepeat = 32;
						peer = spr;
						sect = sprite[spr].sectnum;
					}
					sprite[spr].clipdist = 10;
					sprite[spr].cstat = 0;
					sprite[spr].shade = -64;
					sprite[spr].pal = 0;
					sprite[spr].xoffset = 0;
					sprite[spr].yoffset = 0;
					sprite[spr].ang = sprite[pspr].ang;
					sprite[spr].xvel = 0;
					sprite[spr].yvel = 0;
					sprite[spr].zvel = 0;
					sprite[spr].lotag = (short) (HeadRun() + 1);
					sprite[spr].hitag = 0;
					sprite[spr].extra = -1;

					SnakeList[nSnake].nSprite[i] = spr;
					sprite[spr].owner = (short) AddRunRec(sprite[spr].lotag - 1, 0x110000 | (i << 8) | nSnake);
					game.pInt.setsprinterpolate(spr, sprite[spr]);
					v55 += 2;
				}

				SnakeList[nSnake].nFunc = (short) AddRunRec(NewRun, 0x110000 | nSnake);
				SnakeList[nSnake].field_14[1] = 2;
				SnakeList[nSnake].field_14[2] = 4;
				SnakeList[nSnake].field_14[3] = 6;
				SnakeList[nSnake].field_14[4] = 7;
				SnakeList[nSnake].field_14[5] = 5;
				SnakeList[nSnake].field_14[6] = 6;
				SnakeList[nSnake].field_14[7] = 7;
				SnakeList[nSnake].nTarget = (short) nTarget;
				SnakeList[nSnake].field_10 = 1200;
				SnakeList[nSnake].zvec = 0;
				nSnakePlayer[nSnake] = (short) a1;
				nPlayerSnake[a1] = (short) nSnake;
				if (bSnakeCam && nSnakeCam < 0)
					nSnakeCam = nSnake;
				D3PlayFX(StaticSound[6], peer);
			} else {
				BackUpBullet(pHitInfo.hitx, pHitInfo.hity, sprite[pspr].ang);
				short i = engine.insertsprite(pHitInfo.hitsect, (short) 202);
				sprite[i].x = pHitInfo.hitx;
				sprite[i].y = pHitInfo.hity;
				sprite[i].z = pHitInfo.hitz;
				ExplodeSnakeSprite(i, a1);
				engine.mydeletesprite(i);
			}
		}
	}

	public static void FindSnakeEnemy(int nSnake) {
		short pspr = PlayerList[nSnakePlayer[nSnake]].spriteId;
		int spr = SnakeList[nSnake].nSprite[0];

		int v15 = -1;
		int v5 = 2048;
		for (int i = headspritesect[sprite[spr].sectnum]; i >= 0; i = nextspritesect[i]) {
			if (sprite[i].statnum >= 90 && sprite[i].statnum < 150) {
				if ((sprite[i].cstat & 0x101) != 0) {
					if (i != pspr && ((sprite[i].cstat >> 8) & 0x80) == 0) {
						int v9 = (sprite[spr].ang - GetAngleToSprite(spr, i)) & 0x7FF;
						if (v9 < v5) {
							v15 = i;
							v5 = v9;
						}
					}
				}
			}
		}

		if (v15 != -1) {
			SnakeList[nSnake].nTarget = (short) v15;
			return;
		}

		if (--SnakeList[nSnake].nTarget < -25)
			SnakeList[nSnake].nTarget = pspr;
	}

	public static void FuncSnake(int a1, int a2, int RunPtr) {
		short nSnake = (short) (RunData[RunPtr].RunEvent & 0xFF);
		if (nSnake < 0 || nSnake >= MAX_SNAKES) {
			game.ThrowError("nSnake>=0 && nSnake<MAX_SNAKES");
			return;
		}

		int v37 = 0, nHitMove = 0;
		short nObject = (short) (a1 & 0xFFFF);
		short nSprite = SnakeList[nSnake].nSprite[0];

		switch (a1 & 0x7F0000) {
		case nEventProcess:
			MoveSequence(nSprite, SeqOffsets[20], 0);
			int nTarget = SnakeList[nSnake].nTarget;
			if (nTarget >= 0) {
				if ((sprite[nTarget].cstat & 0x101) != 0) {
					nHitMove = AngleChase(nSprite, nTarget, 1200, SnakeList[nSnake].zvec, 32);
					v37 = sprite[nSprite].z - sprite[nSprite].z;
					break;
				}
				SnakeList[nSnake].nTarget = -1;
			}

			v37 = 0;
			nHitMove = engine.movesprite(nSprite, 600 * sintable[(sprite[nSprite].ang + 512) & 0x7FF],
					600 * sintable[sprite[nSprite].ang & 0x7FF], sintable[SnakeList[nSnake].zvec & 0x7FF] >> 5, 0,
					0, 1);
			FindSnakeEnemy(nSnake);
			break;
		case nEventView:
			if ((RunData[RunPtr].RunEvent & 0xFF00) != 0) 
				PlotSequence(nObject, SeqOffsets[21], 0, 0);
			else
				PlotSequence(nObject, SeqOffsets[20], 0, 0);
			tsprite[nObject].owner = -1;
			return;
		}

		if (nHitMove != 0) {
			ExplodeSnakeSprite(SnakeList[nSnake].nSprite[0], nSnakePlayer[nSnake]);
			nPlayerSnake[nObject] = -1;
			nSnakePlayer[nSnake] = -1;
			DestroySnake(nSnake);
		} else {
			int ang = sprite[nSprite].ang;

			int v14 = -64 * sintable[ang & 0x7FF];
			int dx = -576 * sintable[(ang + 512) & 0x7FF];

			int v36 = -64 * sintable[(ang + 512) & 0x7FF];
			int v15 = -512 * sintable[ang & 0x7FF];
			int dy = v36 + v15;

			int v39 = SnakeList[nSnake].zvec;
			SnakeList[nSnake].zvec = (short) ((v39 + 64) & 0x7FF);

			short sectnum = sprite[nSprite].sectnum;
			int sx = sprite[nSprite].x;
			int sy = sprite[nSprite].y;
			int sz = sprite[nSprite].z;
			int dz = -7 * v37;

			for (int i = 7; i > 0; i--) {
				int spr = SnakeList[nSnake].nSprite[i];
				game.pInt.setsprinterpolate(spr, sprite[spr]);
				sprite[spr].ang = (short) ang;
				sprite[spr].x = sx;
				sprite[spr].y = sy;
				sprite[spr].z = sz;
				engine.mychangespritesect((short) spr, sectnum);
				dx -= v36;
				dy -= v14;
				dz += v37;
				
				int vel = SnakeList[nSnake].field_14[i] * sintable[v39 & 0x7FF] >> 9;
				engine.movesprite((short) spr, vel * sintable[(sprite[nSprite].ang + 1024) & 0x7FF] + dx,
						sintable[(sprite[nSprite].ang + 512) & 0x7FF] * vel + dy, dz, 0, 0, 1);
				v39 = (v39 + 128) & 0x7FF;
			}
		}
	}

}
