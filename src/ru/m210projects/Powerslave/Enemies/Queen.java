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


package ru.m210projects.Powerslave.Enemies;

import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Build.Pragmas.klabs;
import static ru.m210projects.Powerslave.Globals.*;
import static ru.m210projects.Powerslave.Main.*;
import static ru.m210projects.Powerslave.Random.*;
import static ru.m210projects.Powerslave.Enemies.Enemy.*;
import static ru.m210projects.Powerslave.Enemies.LavaDude.*;
import static ru.m210projects.Powerslave.Enemies.Wasp.*;
import static ru.m210projects.Powerslave.RunList.*;
import static ru.m210projects.Powerslave.Bullet.*;
import static ru.m210projects.Powerslave.Seq.*;
import static ru.m210projects.Powerslave.Sound.*;
import static ru.m210projects.Powerslave.Anim.*;
import static ru.m210projects.Powerslave.Sprites.*;
import static ru.m210projects.Powerslave.Weapons.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ru.m210projects.Build.FileHandle.Resource;
import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Powerslave.Enemies.Enemy.EnemyStruct;
import ru.m210projects.Powerslave.Type.SafeLoader;

public class Queen {

	public static class QueenStruct extends EnemyStruct {
		public static final int size = EnemyStruct.size + 2;

		public short field_10;

		@Override
		public void save(ByteBuffer bb)
		{
			super.save(bb);
			bb.putShort(field_10);
		}

		@Override
		public void load(Resource bb)
		{
			super.load(bb);
			field_10 = bb.readShort();
		}


		public void copy(QueenStruct src)
		{
			super.copy(src);
			field_10 = src.field_10;
		}
	}

	public static final int MAX_EGGS = 10;

	private static int QueenCount;
	private static short[] nEggFree = new short[MAX_EGGS];
	private static int nEggsFree;
	private static EnemyStruct[] QueenEgg = new EnemyStruct[MAX_EGGS];
	private static int nVelShift;
	private static EnemyStruct QueenHead = new EnemyStruct();
	private static int nHeadVel;
	private static QueenStruct QueenList = new QueenStruct();
	private static int QueenChan;
	private static short[] tailspr = new short[7];
	private static int nQHead;
	private static int[] MoveQX = new int[25];
	private static int[] MoveQY = new int[25];
	private static int[] MoveQZ = new int[25];
	private static short[] MoveQA = new short[25];
	private static short[] MoveQS = new short[25];

	private static final short[][] HeadSeq = new short[][] { { 56, 1 }, { 65, 0 }, { 65, 0 }, { 65, 0 }, { 65, 0 },
			{ 65, 0 }, { 74, 0 }, { 82, 0 }, { 90, 0 } };

	private static final short[][] EggSeq = new short[][] { { 19, 1 }, { 18, 1 }, { 0, 0 }, { 9, 0 }, { 23, 1 }, };

	private static final short[][] ActionSeq_X_10 = new short[][] { { 0, 0 }, { 0, 0 }, { 9, 0 }, { 36, 0 }, { 18, 0 },
			{ 27, 0 }, { 45, 0 }, { 45, 0 }, { 54, 1 }, { 53, 1 }, { 55, 1 }, };

	public static void InitQueens() {
		QueenCount = 1;
		for (int i = 0; i < MAX_EGGS; i++) {
			if (QueenEgg[i] == null)
				QueenEgg[i] = new EnemyStruct();
			nEggFree[i] = (short) i;
			QueenEgg[i].nFunc = -1;
		}
		nEggsFree = MAX_EGGS;
	}

	public static ByteBuffer saveQueen()
	{
		ByteBuffer bb = ByteBuffer.allocate(QueenStruct.size + 2 + EnemyStruct.size + 2 + (2 + EnemyStruct.size) * MAX_EGGS + 428);
		bb.order(ByteOrder.LITTLE_ENDIAN);

		bb.putShort((short)QueenCount);
		QueenList.save(bb);
		QueenHead.save(bb);

		bb.putShort((short)nEggsFree);
		for (int i = 0; i < MAX_EGGS; i++) {
			bb.putShort(nEggFree[i]);
			QueenEgg[i].save(bb);
		}

		bb.putInt(nVelShift);
		bb.putInt(nHeadVel);
		bb.putShort((short)QueenChan);
		for(int i = 0; i < 7; i++)
			bb.putShort(tailspr[i]);
		bb.putInt(nQHead);
		for(int i = 0; i < 25; i++)
		{
			bb.putInt(MoveQX[i]);
			bb.putInt(MoveQY[i]);
			bb.putInt(MoveQZ[i]);
			bb.putShort(MoveQA[i]);
			bb.putShort(MoveQS[i]);
		}

		return bb;
	}

	public static void loadQueen(SafeLoader loader, Resource bb)
	{
		if(bb != null) {
			loader.QueenCount = bb.readShort();
			loader.QueenList.load(bb);
			loader.QueenHead.load(bb);

			loader.nEggsFree = bb.readShort();
			for (int i = 0; i < MAX_EGGS; i++) {
				loader.nEggFree[i] = bb.readShort();
				if(loader.QueenEgg[i] == null)
					loader.QueenEgg[i] = new EnemyStruct();
				loader.QueenEgg[i].load(bb);
			}

			loader.nVelShift = bb.readInt();
			loader.nHeadVel = bb.readInt();
			loader.QueenChan = bb.readShort();
			for(int i = 0; i < 7; i++)
				loader.tailspr[i] = bb.readShort();
			loader.nQHead = bb.readInt();
			for(int i = 0; i < 25; i++)
			{
				loader.MoveQX[i] = bb.readInt();
				loader.MoveQY[i] = bb.readInt();
				loader.MoveQZ[i] = bb.readInt();
				loader.MoveQA[i] = bb.readShort();
				loader.MoveQS[i] = bb.readShort();
			}
		}
		else
		{
			QueenCount = loader.QueenCount;
			QueenList.copy(loader.QueenList);
			QueenHead.copy(loader.QueenHead);

			nEggsFree = loader.nEggsFree;
			System.arraycopy(loader.nEggFree, 0, nEggFree, 0, MAX_EGGS);
			for (int i = 0; i < MAX_EGGS; i++) {
				if (QueenEgg[i] == null)
					QueenEgg[i] = new EnemyStruct();
				QueenEgg[i].copy(loader.QueenEgg[i]);
			}

			nVelShift = loader.nVelShift;
			nHeadVel = loader.nHeadVel;
			QueenChan = loader.QueenChan;

			nQHead = loader.nQHead;
			System.arraycopy(loader.tailspr, 0, tailspr, 0, 7);
			System.arraycopy(loader.MoveQX, 0, MoveQX, 0, 25);
			System.arraycopy(loader.MoveQY, 0, MoveQY, 0, 25);
			System.arraycopy(loader.MoveQZ, 0, MoveQZ, 0, 25);
			System.arraycopy(loader.MoveQA, 0, MoveQA, 0, 25);
			System.arraycopy(loader.MoveQS, 0, MoveQS, 0, 25);
		}
	}

	public static int GrabEgg() {
		if (nEggsFree != 0)
			return nEggFree[--nEggsFree];
		return -1;
	}

	public static void BlowChunks(int a1) {
		int v2 = 41;
		for (int i = 0; i < 4; i++) {
			BuildCreatureChunk(a1, GetSeqPicnum(16, v2++, 0));
		}
	}

	public static void DestroyEgg(int nEgg) {
		int nSprite = QueenEgg[nEgg].nSprite;
		SPRITE pSprite = sprite[nSprite];
		if (QueenEgg[nEgg].nState == 4) {
			for (int i = 0; i < 4; i++)
				BuildCreatureChunk(nSprite, GetSeqPicnum(56, i % 2 + 24, 0));
		} else
			BuildAnim(-1, 34, 0, pSprite.x, pSprite.y, pSprite.z, pSprite.sectnum, pSprite.xrepeat, 4);
		DoSubRunRec(pSprite.owner);
		DoSubRunRec(pSprite.lotag - 1);
		SubRunRec(QueenEgg[nEgg].nFunc);
		QueenEgg[nEgg].nFunc = -1;
		engine.mydeletesprite((short) nSprite);
		nEggFree[nEggsFree++] = (short) nEgg;
	}

	public static void DestroyAllEggs() {
		for (int i = 0; i < MAX_EGGS; i++)
			if (QueenEgg[i].nFunc > -1)
				DestroyEgg(i);
	}

	public static void SetHeadVel(int nSprite) {
		SPRITE pSprite = sprite[nSprite];
		if (nVelShift < 0) {
			pSprite.xvel = (short) (sintable[pSprite.ang + 512 & 0x7FF] << (-nVelShift));
			pSprite.yvel = (short) (sintable[pSprite.ang & 0x7FF] << (-nVelShift));
		} else {
			pSprite.xvel = (short) (sintable[(pSprite.ang + 512) & 0x7FF] >> nVelShift);
			pSprite.yvel = (short) (sintable[pSprite.ang & 0x7FF] >> nVelShift);
		}
	}

	public static void BuildExplosion(int a1) {
		SPRITE pSprite = sprite[a1];
		short nSector = sprite[a1].sectnum;
		if ((SectFlag[nSector] & 0x2000) != 0)
			BuildAnim(-1, 75, 0, pSprite.x, pSprite.y, pSprite.z, pSprite.sectnum, pSprite.xrepeat, 4);
		else if (sprite[a1].z == sector[nSector].floorz)
			BuildAnim(-1, 34, 0, pSprite.x, pSprite.y, pSprite.z, pSprite.sectnum, pSprite.xrepeat, 4);
		else
			BuildAnim(-1, 36, 0, pSprite.x, pSprite.y, pSprite.z, pSprite.sectnum, pSprite.xrepeat, 4);
	}

	public static boolean DestroyTailPart() {
		if (QueenHead.field_C == 0)
			return false;

		short nPart = --QueenHead.field_C;
		short spr = tailspr[nPart];
		BlowChunks(spr);
		BuildExplosion(spr);
		for (int i = 0; i < 5; i++)
			BuildLavaLimb(spr, i, GetSpriteHeight(nPart));
		engine.mydeletesprite(spr);
		return true;
	}

	public static void SetQueenSpeed(int nSprite, int shift) {
		sprite[nSprite].xvel = (short) (sintable[(sprite[nSprite].ang + 512) & 0x7FF] >> (2 - shift));
		sprite[nSprite].yvel = (short) (sintable[sprite[nSprite].ang] >> (2 - shift));
	}

	public static int QueenAngleChase(int nSprite, int nTarget, int nVel, int a4) {
		SPRITE pSprite = sprite[nSprite];
		short nNewAngle = pSprite.ang;
		if (nTarget >= 0) {
			SPRITE pTarget = sprite[nTarget];
			int zTop = 2 * pTarget.yrepeat * engine.getTile(pTarget.picnum).getHeight();

			int dx = pTarget.x - pSprite.x;
			int dy = pTarget.y - pSprite.y;
			int dz = pTarget.z - pSprite.z;

			int nGoalAngle = AngleDelta(pSprite.ang, engine.GetMyAngle(dx, dy), 1024);
			if (klabs(nGoalAngle) > 127) {
				nVel /= klabs(nGoalAngle >> 7);
				if (nVel < 256)
					nVel = 256;
			}
			if (klabs(nGoalAngle) > a4) {
				if (nGoalAngle >= 0)
					nGoalAngle = a4;
				else
					nGoalAngle = -a4;
			}

			nNewAngle = (short) ((pSprite.ang + nGoalAngle) & 0x7FF);
			pSprite.zvel = (short) (pSprite.zvel + (AngleDelta(pSprite.zvel,
					engine.GetMyAngle(engine.ksqrt(dx * dx + dy * dy), (dz - zTop) >> 8), 24)) & 0x7FF);
		} else
			pSprite.zvel = 0;
		pSprite.ang = nNewAngle;

		int v28 = klabs(sintable[(pSprite.zvel + 512) & 0x7FF]);
		int xvel = v28 * (nVel * sintable[(pSprite.ang + 512) & 0x7FF] >> 14);
		int yvel = v28 * (nVel * sintable[pSprite.ang & 0x7FF] >> 14);
		int v31 = engine.ksqrt((xvel >> 8) * (xvel >> 8) + (yvel >> 8) * (yvel >> 8));

		return engine.movesprite((short) nSprite, xvel >> 2, yvel >> 2,
				(sintable[bobangle & 0x7FF] >> 5) + (v31 * sintable[pSprite.zvel & 0x7FF] >> 13), 0, 0, 1);
	}

	public static void BuildTail() {
		int nSprite = QueenHead.nSprite;
		int x = sprite[nSprite].x;
		int y = sprite[nSprite].y;
		int z = sprite[nSprite].z;
		short nSector = sprite[nSprite].sectnum;

		for (int i = 0; i < 7; i++) {
			short spr = engine.insertsprite(nSector, (short) 121);
			tailspr[i] = spr;
			if (spr < 0) {
				game.ThrowError("Can't create queen's tail!");
				return;
			}

			sprite[spr].x = x;
			sprite[spr].y = y;
			sprite[spr].z = z;
			sprite[spr].cstat = 0;
			sprite[spr].xoffset = 0;
			sprite[spr].yoffset = 0;
			sprite[spr].shade = -12;
			sprite[spr].picnum = 1;
			sprite[spr].pal = sector[nSector].ceilingpal;
			sprite[spr].clipdist = 100;
			sprite[spr].xrepeat = 80;
			sprite[spr].yrepeat = 80;
			sprite[spr].xvel = 0;
			sprite[spr].yvel = 0;
			sprite[spr].zvel = 0;
			sprite[spr].hitag = 0;
			sprite[spr].lotag = (short) (HeadRun() + 1);
			sprite[spr].extra = -1;
			sprite[spr].owner = (short) AddRunRec(sprite[spr].lotag - 1, 0x1B0000 | i);
		}

		for (int i = 0; i < 25; i++) {
			MoveQX[i] = x;
			MoveQY[i] = y;
			MoveQZ[i] = z;
			MoveQS[i] = nSector;
		}

		nQHead = 0;
		QueenHead.field_C = 7;
	}

	public static void BuildQueenEgg(int nQueen, int nState) {
		int nEgg = GrabEgg();
		if (nEgg >= 0) {
			SPRITE pQueen = sprite[QueenList.nSprite];
			int x = pQueen.x;
			int y = pQueen.y;
			short ang = pQueen.ang;
			int z = sector[pQueen.sectnum].floorz;
			int spr = engine.insertsprite(pQueen.sectnum, (short) 121);
			if (spr < 0 || spr >= MAXSPRITES) {
				game.ThrowError("spr>=0 && spr<MAXSPRITES");
				return;
			}
			sprite[spr].x = x;
			sprite[spr].y = y;
			sprite[spr].pal = 0;
			sprite[spr].z = z;
			sprite[spr].clipdist = 50;
			sprite[spr].xoffset = 0;
			sprite[spr].yoffset = 0;
			sprite[spr].shade = -12;
			sprite[spr].picnum = 1;
			sprite[spr].ang = (short) ((ang - 256 + RandomSize(9)) & 0x7FF);
			if (nState != 0) {
				sprite[spr].yrepeat = 60;
				sprite[spr].xrepeat = 60;
				sprite[spr].xvel = 0;
				sprite[spr].yvel = 0;
				sprite[spr].zvel = -2000;
				sprite[spr].cstat = 257;
			} else {
				sprite[spr].xrepeat = 30;
				sprite[spr].yrepeat = 30;
				sprite[spr].cstat = 0;
				sprite[spr].xvel = sintable[(sprite[spr].ang + 512) & 0x7FF];
				sprite[spr].yvel = sintable[sprite[spr].ang & 0x7FF];
				sprite[spr].zvel = -6000;
			}

			sprite[spr].lotag = (short) (HeadRun() + 1);
			sprite[spr].extra = -1;
			sprite[spr].hitag = 0;

			QueenEgg[nEgg].nHealth = 200;
			QueenEgg[nEgg].nSeq = 0;
			QueenEgg[nEgg].nSprite = (short) spr;
			QueenEgg[nEgg].field_C = (short) nState;
			QueenEgg[nEgg].nTarget = QueenList.nTarget;
			if (nState != 0) {
				nState = 4;
				QueenEgg[nEgg].field_A = 200;
			}
			QueenEgg[nEgg].nState = (short) nState;
			sprite[spr].owner = (short) AddRunRec(sprite[spr].lotag - 1, 0x1D0000 | nEgg);
			QueenEgg[nEgg].nFunc = (short) AddRunRec(NewRun, 0x1D0000 | nEgg);
		}
	}

	public static void BuildQueenHead(int nQueen) {
		SPRITE pQueen = sprite[QueenList.nSprite];
		int x = pQueen.x;
		int y = pQueen.y;
		short ang = pQueen.ang;
		int z = sector[pQueen.sectnum].floorz;
		int spr = engine.insertsprite(pQueen.sectnum, (short) 121);
		if (spr < 0 || spr >= MAXSPRITES) {
			game.ThrowError("spr>=0 && spr<MAXSPRITES");
			return;
		}

		sprite[spr].x = x;
		sprite[spr].y = y;
		sprite[spr].z = z;
		sprite[spr].pal = 0;
		sprite[spr].clipdist = 70;
		sprite[spr].yrepeat = 80;
		sprite[spr].xrepeat = 80;
		sprite[spr].cstat = 0;
		sprite[spr].picnum = 1;
		sprite[spr].shade = -12;
		sprite[spr].xoffset = 0;
		sprite[spr].yoffset = 0;
		sprite[spr].ang = ang;
		nVelShift = 2;
		SetHeadVel(spr);
		sprite[spr].zvel = -8192;
		sprite[spr].lotag = (short) (HeadRun() + 1);
		sprite[spr].hitag = 0;
		sprite[spr].extra = -1;

		QueenHead.nHealth = 800;
		QueenHead.nState = 0;
		QueenHead.nTarget = QueenList.nTarget;
		QueenHead.nSeq = 0;
		QueenHead.nSprite = (short) spr;
		QueenHead.field_A = 0;
		sprite[spr].owner = (short) AddRunRec(sprite[spr].lotag - 1, 0x1B0000);
		QueenHead.nFunc = (short) AddRunRec(NewRun, 0x1B0000);
		QueenHead.field_C = 0;

		nCreaturesLeft++;
		nCreaturesMax++;
	}

	public static void BuildQueen(int spr, int x, int y, int z, int sectnum, int ang, int channel) {
		int count = --QueenCount;

		if (count < 0)
			return;

		if (spr == -1) {
			spr = engine.insertsprite((short) sectnum, (short) 121);
		} else {
			x = sprite[spr].x;
			y = sprite[spr].y;
			z = sector[sprite[spr].sectnum].floorz;
			ang = sprite[spr].ang;
			engine.changespritestat((short) spr, (short) 121);
		}

		if (spr < 0 || spr >= MAXSPRITES) {
			game.ThrowError("spr>=0 && spr<MAXSPRITES");
			return;
		}

		sprite[spr].x = x;
		sprite[spr].y = y;
		sprite[spr].z = z;
		sprite[spr].cstat = 257;
		sprite[spr].pal = 0;
		sprite[spr].shade = -12;
		sprite[spr].clipdist = 100;
		sprite[spr].xrepeat = 80;
		sprite[spr].yrepeat = 80;
		sprite[spr].xoffset = 0;
		sprite[spr].yoffset = 0;
		sprite[spr].picnum = 1;
		sprite[spr].ang = (short) ang;
		sprite[spr].xvel = 0;
		sprite[spr].yvel = 0;
		sprite[spr].zvel = 0;
		sprite[spr].lotag = (short) (HeadRun() + 1);
		sprite[spr].extra = -1;
		sprite[spr].hitag = 0;

		QueenList.nState = 0;
		QueenList.nHealth = 4000;
		QueenList.nSeq = 0;
		QueenList.nSprite = (short) spr;
		QueenList.nTarget = -1;
		QueenList.field_A = 0;
		QueenList.field_10 = 5;
		QueenList.field_C = 0;
		QueenChan = channel;
		nHeadVel = 800;
		sprite[spr].owner = (short) AddRunRec(sprite[spr].lotag - 1, 0x1A0000 | count);
		AddRunRec(NewRun, 0x1A0000 | count);
		nCreaturesLeft++;
		nCreaturesMax++;
	}

	public static void FuncQueen(int a1, int nDamage, int RunPtr) {
		short nQueen = (short) (RunData[RunPtr].RunEvent & 0xFFFF);
		if (nQueen < 0 || nQueen >= 1) {
			game.ThrowError("queen>=0 && queen<MAX_QUEEN");
			return;
		}
		QueenStruct pQueen = QueenList;
		int nSprite = pQueen.nSprite;
		SPRITE pSprite = sprite[nSprite];

		int nState = pQueen.nState;
		int field_A = pQueen.field_A;
		int v59 = 0;

		switch (a1 & 0x7F0000) {
		case nEventProcess:
			if (field_A < 3)
				Gravity(nSprite);

			int nSeq = ActionSeq_X_10[nState][0] + SeqOffsets[49];
			pSprite.picnum = (short) GetSeqPicnum2(nSeq, pQueen.nSeq);

			MoveSequence(nSprite, nSeq, pQueen.nSeq);
			if (++pQueen.nSeq >= SeqSize[nSeq]) {
				pQueen.nSeq = 0;
				v59 = 1;
			}

			int nFlags = FrameFlag[pQueen.nSeq + SeqBase[nSeq]];
			int nTarget = pQueen.nTarget;
			if (nTarget > -1 && nState < 7 && (sprite[nTarget].cstat & 0x101) == 0) {
				nTarget = pQueen.nTarget = -1;
				pQueen.nState = 0;
			}

			switch (nState) {
			case 0:
				if (nTarget < 0)
					nTarget = FindPlayer(nSprite, 60);

				if (nTarget >= 0) {
					pQueen.nState = (short) (pQueen.field_A + 1);
					pQueen.nSeq = 0;
					pQueen.nTarget = (short) nTarget;
					pQueen.field_C = (short) RandomSize(7);
					SetQueenSpeed(nSprite, field_A);
				}
				return;
			case 6:
				if (v59 != 0) {
					BuildQueenEgg(nQueen, 1);
					pQueen.nState = 3;
					pQueen.field_C = (short) (RandomSize(6) + 60);
				}
				return;
			case 1:
			case 2:
			case 3:
				pQueen.field_C--;
				if ((totalmoves & 0x1F) == (nQueen & 0x1F)) {
					if (field_A >= 2) {
						if (pQueen.field_C <= 0) {
							if (nWaspCount < MAX_WASPS) {
								pQueen.nState = 6;
								pQueen.nSeq = 0;
								return;
							}
							pQueen.field_C = 30000;
						}
					} else {
						if (pQueen.field_C <= 0) {
							pQueen.nSeq = 0;
							sprite[nSprite].yvel = 0;
							sprite[nSprite].xvel = 0;
							pQueen.nState = (short) (field_A + 4);
							pQueen.field_C = (short) (RandomSize(6) + 30);
							return;
						}
						if (pQueen.field_10 < 5)
							++pQueen.field_10;
					}
					PlotCourseToSprite(nSprite, nTarget);
					SetQueenSpeed(nSprite, field_A);
				}

				int nHit = MoveCreatureWithCaution(nSprite);
				switch (nHit & 0xC000) {
				case 0xC000:
					if (field_A == 2 && (nHit & 0x3FFF) == nTarget) {
						DamageEnemy(nTarget, nSprite, 5);
						break;
					}
				case 0x8000:
					sprite[nSprite].ang = (short) ((sprite[nSprite].ang + 0x100) & 0x7FF);
					SetQueenSpeed(nSprite, field_A);
					break;

				}
				if (nState != 0 && nTarget != -1 && (sprite[nTarget].cstat & 0x101) == 0) {
					pQueen.nState = 0;
					pQueen.nSeq = 0;
					pQueen.field_C = 100;
					pQueen.nTarget = -1;
					sprite[nSprite].yvel = 0;
					sprite[nSprite].xvel = 0;
				}
				return;
			case 4:
			case 5:
				if (v59 != 0 && pQueen.field_10 <= 0) {
					pQueen.nState = 0;
					pQueen.field_C = 15;
				} else if ((nFlags & 0x80) != 0) {
					pQueen.field_10--;
					PlotCourseToSprite(nSprite, nTarget);
					if (field_A != 0)
						BuildQueenEgg(nQueen, 0);
					else
						BuildBullet(nSprite, 12, 0, 0, -1, sprite[nSprite].ang, nTarget + 10000, 1);
				}
				return;
			case 7:
				if (v59 != 0) {
					pQueen.nState = 0;
					pQueen.nSeq = 0;
				}
				return;
			case 8:
			case 9:
				if (v59 == 0)
					return;

				if (nState == 9) {
					if (--pQueen.field_C > 0)
						return;

					sprite[nSprite].cstat = 0;
					for (int i = 0; i < 20; i++) {
						int spr = BuildCreatureChunk(nSprite, GetSeqPicnum(49, 57, 0));
						sprite[spr].picnum = (short) (i % 3 + 3117);
						sprite[spr].yrepeat = 100;
						sprite[spr].xrepeat = 100;
					}

					int spr = BuildCreatureChunk(nSprite, GetSeqPicnum(49, 57, 0));
					sprite[spr].picnum = 3126;
					sprite[spr].yrepeat = 100;
					sprite[spr].xrepeat = 100;
					PlayFXAtXYZ(StaticSound[40], sprite[nSprite].x, sprite[nSprite].y, sprite[nSprite].z,
							sprite[nSprite].sectnum);
					BuildQueenHead(nQueen);
				}
				pQueen.nState++;
				return;
			case 10:
				sprite[nSprite].cstat &= ~0x101;
				return;
			}
			return;
		case nEventView:
			PlotSequence((short) (a1 & 0xFFFF), ActionSeq_X_10[nState][0] + SeqOffsets[49], pQueen.nSeq,
					ActionSeq_X_10[nState][1]);
			return;
		case nEventRadialDamage:
			if (sprite[nRadialSpr].statnum != 121)
				if ((pSprite.cstat & 0x101) != 0)
					nDamage = CheckRadialDamage(nSprite);
		case nEventDamage:
			if (nDamage != 0) {
				if (pQueen.nHealth > 0) {
					pQueen.nHealth -= nDamage;
					if (pQueen.nHealth <= 0) {
						pSprite.xvel = 0;
						pSprite.yvel = 0;
						pSprite.zvel = 0;
						switch (++pQueen.field_A) {
						case 1:
							pQueen.nHealth = 4000;
							pQueen.nState = 7;
							BuildAnim(-1, 36, 0, pSprite.x, pSprite.y, pSprite.z - 7680, pSprite.sectnum,
									pSprite.xrepeat, 4);
							break;
						case 2:
							pQueen.nHealth = 4000;
							pQueen.nState = 7;
							DestroyAllEggs();
							break;
						case 3:
							pQueen.nHealth = 0;
							pQueen.nState = 8;
							pQueen.field_C = 5;
							nCreaturesLeft--;
							break;
						}
						pQueen.nSeq = 0;
					} else {
						if (field_A > 0 && RandomSize(4) == 0) {
							pQueen.nState = 7;
							pQueen.nSeq = 0;
						}
					}
				}
			}
			return;
		}
	}

	public static void FuncQueenHead(int a1, int nDamage, int RunPtr) {
		EnemyStruct pHead = QueenHead;
		int nSprite = pHead.nSprite;
		SPRITE pSprite = sprite[nSprite];
		int nState = pHead.nState;
		short nTarget = pHead.nTarget;
		int v73 = 0;
		switch (a1 & 0x7F0000) {
		case nEventProcess:
			if (pHead.nState == 0)
				Gravity(QueenHead.nSprite);

			int nSeq = HeadSeq[nState][0] + SeqOffsets[49];
			pSprite.picnum = (short) GetSeqPicnum2(nSeq, pHead.nSeq);
			MoveSequence(nSprite, nSeq, pHead.nSeq);
			if (++pHead.nSeq >= SeqSize[nSeq]) {
				pHead.nSeq = 0;
				v73 = 1;
			}

			if (nTarget == -1)
				nTarget = (short) FindPlayer(nSprite, 1000);
			else if ((sprite[pHead.nTarget].cstat & 0x101) == 0)
				nTarget = -1;
			pHead.nTarget = nTarget;

			switch (nState) {
			case 0:
				int v13 = pHead.field_A;
				if (v13 > 0) {
					pHead.field_A--;
					if (v13 == 1) {
						BuildTail();
						pHead.nState = 6;
						nHeadVel = 800;
						pSprite.cstat = 257;
					} else if ((v13 - 1) < 60) {
						pSprite.shade--;
					}
					return;
				}
				int hit = MoveCreature(nSprite);
				short nNewAngle = 0;
				switch (hit & 0xFC000) {
				case 0x8000:
					nNewAngle = engine.GetWallNormal(hit & 0x3FFF);
					break;
				case 0xC000:
					nNewAngle = sprite[hit & 0x3FFF].ang;
					break;
				case 0x20000:
					pSprite.zvel = (short) -(pSprite.zvel >> 1);
					if (pSprite.zvel > -256) {
						nVelShift = 100;
						pSprite.zvel = 0;
					}
					break;
				default:
					return;
				}

				pSprite.ang = nNewAngle;
				if (++nVelShift >= 5) {
					pSprite.xvel = 0;
					pSprite.yvel = 0;
					if (pSprite.zvel == 0)
						pHead.field_A = 120;
				} else {
					SetHeadVel(nSprite);
				}

				return;
			case 6:
				if (v73 != 0) {
					pHead.nState = 1;
					pHead.nSeq = 0;
					return;
				}
			case 1:
				if (nTarget != -1 && sprite[nTarget].z - 51200 <= pSprite.z) {
					pSprite.z -= 2048;
					break;
				} else {
					pHead.nState = 4;
					pHead.nSeq = 0;
				}
				return;
			case 4:
			case 7:
			case 8:
				if (v73 != 0) {
					switch (RandomSize(2)) {
					case 0:
						pHead.nState = 4;
						break;
					case 1:
						pHead.nState = 7;
						break;
					default:
						pHead.nState = 8;
						break;
					}

				}
				if (nTarget > -1) {
					int hitMove = QueenAngleChase(nSprite, nTarget, nHeadVel, 64);
					switch (hitMove & 0xC000) {
					case 0xC000:
						if ((hitMove & 0x3FFF) == nTarget) {
							DamageEnemy(nTarget, nSprite, 10);
							D3PlayFX(StaticSound[50] | 0x2000, nSprite);
							pSprite.ang += (RandomSize(9) + 0x300);
							pSprite.ang &= 0x7FF;
							pSprite.zvel = (short) (-20 - RandomSize(6));
							SetHeadVel(nSprite);
						}
						break;
					}
				}
				break;
			case 5:
				if (--pHead.field_A <= 0) {
					pHead.field_A = 3;
					if (pHead.field_C-- != 0) {
						if (pHead.field_C >= 15 || pHead.field_C < 10) {
							int ox = pSprite.x;
							int oy = pSprite.y;
							int oz = pSprite.z;
							short sect = pSprite.sectnum;
							int nAngle = RandomSize(11) & 0x7FF;
							pSprite.xrepeat = pSprite.yrepeat = (short) (127 - pHead.field_C);
							pSprite.cstat = (short) 32768;
							int v54 = RandomSize(5);
							int v53 = RandomSize(5);
							engine.movesprite((short) nSprite, sintable[nAngle & 0x7FF] << 10,
									sintable[(nAngle + 512) & 0x7FF] << 10, (v54 - v53) << 7, 0, 0, 1);
							BlowChunks(nSprite);
							BuildExplosion(nSprite);
							engine.mychangespritesect((short) nSprite, sect);
							pSprite.x = ox;
							pSprite.y = oy;
							pSprite.z = oz;
							if (pHead.field_C < 10) {
								for (int i = 2 * (10 - pHead.field_C); i > 0; i--)
									BuildLavaLimb(nSprite, i, GetSpriteHeight(nSprite));
							}
						}
					} else {
						BuildExplosion(nSprite);
						for (int i = 0; i < 10; i++)
							BlowChunks(nSprite);
						for (int i = 0; i < 20; i++)
							BuildLavaLimb(nSprite, i, GetSpriteHeight(nSprite));
						SubRunRec(pSprite.owner);
						SubRunRec(pHead.nFunc);
						engine.mydeletesprite((short) nSprite);
						ChangeChannel(QueenChan, 1);
					}
				}
				return;
			}

			MoveQX[nQHead] = pSprite.x;
			MoveQY[nQHead] = pSprite.y;
			MoveQZ[nQHead] = pSprite.z;
			MoveQS[nQHead] = pSprite.sectnum;
			MoveQA[nQHead] = pSprite.ang;

			int nQ = nQHead;
			for (int i = 0; i < pHead.field_C; i++) {
				nQ -= 3;
				if (nQ < 0)
					nQ += 25;

				short spr = tailspr[i];
				if (MoveQS[nQ] != sprite[spr].sectnum)
					engine.mychangespritesect(spr, MoveQS[nQ]);

				sprite[spr].x = MoveQX[nQ];
				sprite[spr].y = MoveQY[nQ];
				sprite[spr].z = MoveQZ[nQ];
				sprite[spr].ang = MoveQA[nQ];
			}

			if (++nQHead >= 25)
				nQHead = 0;

			return;
		case nEventView:
			PlotSequence((short) (a1 & 0xFFFF), HeadSeq[nState][0] + SeqOffsets[49], pHead.nSeq, HeadSeq[nState][1]);
			return;
		case nEventRadialDamage:
			if (sprite[nRadialSpr].statnum != 121) {
				if ((sprite[pHead.nSprite].cstat & 0x101) != 0) {
					nDamage = CheckRadialDamage(nSprite);
				}
			}
		case nEventDamage:
			if (nDamage != 0) {
				if (pHead.nHealth > 0) {
					pHead.nHealth -= nDamage;
					if (RandomSize(4) == 0) {
						pHead.nTarget = (short) (a1 & 0xFFFF);
						pHead.nState = 7;
						pHead.nSeq = 0;
					}
					if (pHead.nHealth <= 0) {
						if (DestroyTailPart()) {
							pHead.nHealth = 200;
							nHeadVel += 100;
						} else {
							nCreaturesLeft--;
							pHead.nState = 5;
							pHead.nSeq = 0;
							pHead.field_A = 0;
							pHead.field_C = 80;
							pSprite.cstat = 0;
						}
					}
				}
			}
			return;
		}
	}

	public static void FuncQueenEgg(int a1, int nDamage, int RunPtr) {
		short nEgg = (short) (RunData[RunPtr].RunEvent & 0xFFFF);
		EnemyStruct pEgg = QueenEgg[nEgg];
		int nSprite = pEgg.nSprite;
		SPRITE pSprite = sprite[nSprite];

		int nState = pEgg.nState;

		int nTarget = -1;
		int v44 = 0;

		switch (a1 & 0x7F0000) {
		case nEventProcess:
			if (pEgg.nHealth > 0) {
				if (nState == 0 || nState == 4)
					Gravity(nSprite);

				int nSeq = EggSeq[nState][0] + SeqOffsets[56];
				pSprite.picnum = (short) GetSeqPicnum2(nSeq, pEgg.nSeq);
				if (nState != 4) {
					MoveSequence(nSprite, nSeq, pEgg.nSeq);
					if (++pEgg.nSeq >= SeqSize[nSeq]) {
						pEgg.nSeq = 0;
						v44 = 1;
					}

					nTarget = UpdateEnemy(pEgg.nTarget);
					pEgg.nTarget = (short) nTarget;
					if (nTarget < 0 || (sprite[nTarget].cstat & 0x101) != 0) {
						pEgg.nTarget = (short) FindPlayer(-nSprite, 1000);
					} else {
						pEgg.nTarget = -1;
						pEgg.nState = 0;
					}
				}

				switch (nState) {
				case 0:
					int hitMove = MoveCreature(nSprite);
					short nAngle = 0;
					switch (hitMove & 0xFC000) {
					case 0x20000:
						if (RandomSize(1) != 0) {
							DestroyEgg(nEgg);
							return;
						}
						pEgg.nState = 1;
						pEgg.nSeq = 0;
						return;
					case 0x8000:
						nAngle = engine.GetWallNormal(hitMove & 0x3FFF);
					case 0xC000:
						nAngle = sprite[hitMove & 0x3FFF].ang;
						break;
					default:
						return;
					}
					pSprite.ang = nAngle;
					pSprite.xvel = (short) (sintable[(pSprite.ang + 512) & 0x7FF] >> 1);
					pSprite.yvel = (short) (sintable[pSprite.ang & 0x7FF] >> 1);
					return;
				case 1:
					if (v44 != 0) {
						pEgg.nState = 3;
						pSprite.cstat = 257;
					}
					return;
				case 2:
				case 3:
					int nHit = QueenAngleChase(nSprite, nTarget, nHeadVel, 64);
					switch (nHit & 0xC000) {
					case 0xC000:
						if (sprite[nHit & 0x3FFF].statnum != 121)
							DamageEnemy(nHit & 0x3FFF, nSprite, 5);
					case 0x8000:
						pSprite.ang += RandomSize(9) + 0x300;
						pSprite.ang &= 0x7FF;
						pSprite.xvel = (short) (sintable[(pSprite.ang + 512) & 0x7FF] >> 3);
						pSprite.yvel = (short) (sintable[pSprite.ang & 0x7FF] >> 3);
						pSprite.zvel = (short) -RandomSize(5);
						return;
					}
					return;
				case 4:
					if ((MoveCreature(nSprite) & 0x20000) != 0) {
						pSprite.zvel = (short) -(pSprite.zvel - 256);
						if (pSprite.zvel < -512)
							pSprite.zvel = 0;
					}

					if (--pEgg.field_A <= 0) {
						int spr = BuildWasp(-2, pSprite.x, pSprite.y, pSprite.z, pSprite.sectnum, pSprite.ang);
						pSprite.z = sprite[spr].z;
						DestroyEgg(nEgg);
						return;
					}
				}
				return;
			}
			DestroyEgg(nEgg);
			return;
		case nEventView:
			PlotSequence((short) (a1 & 0xFFFF), EggSeq[nState][0] + SeqOffsets[56], pEgg.nSeq, EggSeq[nState][1]);
			return;
		case nEventRadialDamage:
			if (sprite[nRadialSpr].statnum != 121)
				if ((pSprite.cstat & 0x101) != 0)
					nDamage = CheckRadialDamage(nSprite);
		case nEventDamage:
			if (nDamage != 0) {
				if (pEgg.nHealth > 0) {
					pEgg.nHealth -= nDamage;
					if (pEgg.nHealth <= 0)
						DestroyEgg(nEgg);
				}
			}
			return;
		}
	}
}
