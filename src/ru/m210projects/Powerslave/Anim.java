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

import static ru.m210projects.Powerslave.Globals.*;
import static ru.m210projects.Powerslave.Seq.*;
import static ru.m210projects.Powerslave.RunList.*;
import static ru.m210projects.Powerslave.Main.*;
import static ru.m210projects.Powerslave.Sound.*;
import static ru.m210projects.Powerslave.Sprites.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ru.m210projects.Build.FileHandle.Resource;
import ru.m210projects.Build.Types.SPRITE;

import static ru.m210projects.Build.Engine.*;

import ru.m210projects.Powerslave.Type.AnimStruct;
import ru.m210projects.Powerslave.Type.SafeLoader;

public class Anim {
	
	public static final int MAX_LIL_ANIM = 400;
	
	public static int nAnimsFree;
	private static short AnimsFree[] = new short[MAX_LIL_ANIM];
	private static int AnimRunRec[] = new int[MAX_LIL_ANIM];
	
	public static byte AnimFlags[] = new byte[MAX_LIL_ANIM];
	public static AnimStruct AnimList[] = new AnimStruct[MAX_LIL_ANIM];

	public static void InitAnims() {

		for (short i = 0; i < MAX_LIL_ANIM; i++) {
			AnimsFree[i] = i;
			if(AnimList[i] != null)
				AnimList[i].nSprite = -1;
		}
		
		nAnimsFree = MAX_LIL_ANIM;
		nMagicSeq = SeqOffsets[41] + 21;
		nPreMagicSeq = SeqOffsets[64];
		nSavePointSeq = SeqOffsets[41] + 12;
	}
	
	public static ByteBuffer saveAnm()
	{
		int nAnims = 0;
		for (int i = 0; i < MAX_LIL_ANIM; i++) {
			if(AnimList[i] != null && AnimList[i].nSprite != -1) 
				nAnims++;
		}
		
		ByteBuffer bb = ByteBuffer.allocate(nAnims * (AnimStruct.size + 2) + 5 * MAX_LIL_ANIM + 2);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		bb.putShort((short)nAnimsFree);
		
		for (int i = 0; i < MAX_LIL_ANIM; i++) {
			bb.putShort(AnimsFree[i]);
			bb.putShort((short)AnimRunRec[i]);
			bb.put(AnimFlags[i]);
		}
		
		if(nAnims != 0)
		{
			for (int i = 0; i < MAX_LIL_ANIM; i++) {
				if(AnimList[i] != null && AnimList[i].nSprite != -1) {
					bb.putShort((short)i);
					AnimList[i].save(bb);
				}
			}
		}

		return bb;
	}
	
	public static void loadAnm(SafeLoader loader, Resource bb)
	{
		if(bb != null) {
			for (int i = 0; i < MAX_LIL_ANIM; i++) 
				if(loader.AnimList[i] != null)
					loader.AnimList[i].nSprite = -1;

			loader.nAnimsFree = bb.readShort();
			for (int i = 0; i < MAX_LIL_ANIM; i++) {
				loader.AnimsFree[i] = bb.readShort();
				loader.AnimRunRec[i] = bb.readShort();
				loader.AnimFlags[i] = bb.readByte();
			}
			
			int nAnims = (MAX_LIL_ANIM - loader.nAnimsFree);
			while(nAnims > 0)
			{
				short i = bb.readShort();
				if(loader.AnimList[i] == null)
					loader.AnimList[i] = new AnimStruct();
				loader.AnimList[i].load(bb);
				nAnims--;
			}
		}
		else
		{
			nAnimsFree = loader.nAnimsFree;
			
			System.arraycopy(loader.AnimsFree, 0, AnimsFree, 0, MAX_LIL_ANIM);
			System.arraycopy(loader.AnimRunRec, 0, AnimRunRec, 0, MAX_LIL_ANIM);
			System.arraycopy(loader.AnimFlags, 0, AnimFlags, 0, MAX_LIL_ANIM);

			for (int i = 0; i < MAX_LIL_ANIM; i++) {
				if(AnimList[i] != null)
					AnimList[i].nSprite = -1;
				
				if(loader.AnimList[i] != null && loader.AnimList[i].nSprite != -1) {
					if(AnimList[i] == null)
						AnimList[i] = new AnimStruct();
					AnimList[i].copy(loader.AnimList[i]);
				}
			}

			nMagicSeq = SeqOffsets[41] + 21;
			nPreMagicSeq = SeqOffsets[64];
			nSavePointSeq = SeqOffsets[41] + 12;
		}
	}

	public static int BuildAnim(int spr, int a2, int a3, int x, int y, int z, int sectnum, int size, int flags) {
		int v20 = a2;
		int v21 = a3;
		if (--nAnimsFree != 0) {
			int nAnim = AnimsFree[nAnimsFree];
			if (spr == -1) 
				spr = engine.insertsprite((short) sectnum, (short) 500);

			sprite[spr].y = y;
			sprite[spr].z = z;
			sprite[spr].x = x;
			sprite[spr].cstat = 0;
			if ((flags & 4) != 0) {
				sprite[spr].pal = 4;
				sprite[spr].shade = -64;
			} else {
				sprite[spr].pal = 0;
				sprite[spr].shade = -12;
			}
			sprite[spr].clipdist = 10;
			sprite[spr].xrepeat = (short) size;
			sprite[spr].yrepeat = (short) size;
			sprite[spr].picnum = 1;
			sprite[spr].ang = 0;
			sprite[spr].xoffset = 0;
			sprite[spr].yoffset = 0;
			sprite[spr].xvel = 0;
			sprite[spr].zvel = 0;
			sprite[spr].yvel = 0;
			if (sprite[spr].statnum < 900)
				sprite[spr].hitag = -1;
			sprite[spr].lotag = (short) (HeadRun() + 1);
			sprite[spr].owner = -1;
			sprite[spr].extra = (short) AddRunRec(sprite[spr].lotag - 1, 0x100000 | nAnim);
			AnimRunRec[nAnim] = AddRunRec(NewRun, 0x100000 | nAnim);
			AnimFlags[nAnim] = (byte) flags;
			if ((flags & 0x80) != 0)
				sprite[spr].cstat |= 2;
			
			if(AnimList[nAnim] == null)
				AnimList[nAnim] = new AnimStruct();
			AnimList[nAnim].nAction = (short) (v21 + SeqOffsets[v20]);
			AnimList[nAnim].nSeq = 0;
//			AnimList[nAnim].field_4 = 256;
			AnimList[nAnim].nSprite = (short) spr;
			
			return nAnim;
		}
		nAnimsFree = 0;
		return -1;
	}

	public static int GetAnimSprite(int a1) {
		return AnimList[a1].nSprite;
	}

	public static void DestroyAnim(int a1) {
		int nSprite = AnimList[a1].nSprite;
		if (nSprite >= 0) {
			StopSpriteSound(nSprite);
			SubRunRec(AnimRunRec[a1]);
			DoSubRunRec(sprite[nSprite].extra);
			FreeRun(sprite[nSprite].lotag - 1);
		}
		AnimList[a1].nSprite = -1;
		if(nAnimsFree == MAX_LIL_ANIM) return;
		AnimsFree[nAnimsFree] = (short) a1;
		nAnimsFree++;
	}

	public static void FuncAnim(int a1, int a2, int a3) {
		short nAnim = (short) (RunData[a3].RunEvent & 0xFFFF);
		if (nAnim < 0 || nAnim >= MAX_LIL_ANIM) {
			game.ThrowError("nAnim>=0 && nAnim<MAX_LIL_ANIM");
			return;
		}

		int nSprite = AnimList[nAnim].nSprite;
		int nSeq = AnimList[nAnim].nAction;

		switch (a1 & 0x7F0000) {
		case 0x20000:
			if ((sprite[nSprite].cstat & 0x8000) == 0)
				MoveSequence(nSprite, nSeq, AnimList[nAnim].nSeq);
	
			if (sprite[nSprite].statnum != 404)
				break;

			int nSource = sprite[nSprite].hitag;
			if (nSource <= -1)
				break;
			
			SPRITE pSource = sprite[nSource];

			sprite[nSprite].x = pSource.x;
			sprite[nSprite].y = pSource.y;
			sprite[nSprite].z = pSource.z;
			int v11 = pSource.sectnum;
			if (v11 != sprite[nSprite].sectnum) {
				if (v11 < 0 || v11 >= 1024) {
					DestroyAnim(nAnim);
					engine.mydeletesprite((short) nSprite);
					return;
				}
				engine.mychangespritesect((short) nSprite, (short) v11);
			}
			if (AnimList[nAnim].nSeq != 0)
				break;

			int v13 = pSource.hitag;
			if (pSource.cstat != (short)32768)
				pSource.hitag--;

			if (pSource.cstat == (short)32768 || (v13 < 15)) {
				pSource.hitag = 1;
				DestroyAnim(nAnim);
				engine.mydeletesprite((short) nSprite);
				break;
			}

			DamageEnemy(nSource, -1, 2 * (pSource.hitag - 14));
			if (pSource.shade < 100) {
				pSource.pal = 0;
				pSource.shade++;
			}
			if ((pSource.cstat & 0x101) != 0)
				break;

			DestroyAnim(nAnim);
			engine.mydeletesprite((short) nSprite);
			return;
		case 0x90000:
			PlotSequence((short)(a1 & 0xFFFF), nSeq, AnimList[nAnim].nSeq, 257);
			return;
		default:
			return;
		}

		if (++AnimList[nAnim].nSeq < SeqSize[nSeq])
			return;
		if ((AnimFlags[nAnim] & 0x10) != 0) {
			AnimList[nAnim].nSeq = 0;
			return;
		}
		if (nSeq == nPreMagicSeq) {
			AnimList[nAnim].nSeq = 0;
			AnimList[nAnim].nAction = (short) nMagicSeq;
			AnimFlags[nAnim] |= 0x10;
			sprite[AnimList[nAnim].nSprite].cstat |= 2;
			return;
		}
		if (nSeq == nSavePointSeq) {
			AnimList[nAnim].nSeq = (short) (nSavePointSeq ^ nSeq);
			AnimList[nAnim].nAction++;
			AnimFlags[nAnim] |= 0x10;
			return;
		}

		DestroyAnim(nAnim);
		engine.mydeletesprite((short) nSprite);
	}

}
