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
import static ru.m210projects.Powerslave.Globals.*;
import static ru.m210projects.Powerslave.Main.*;
import static ru.m210projects.Powerslave.Random.*;
import static ru.m210projects.Powerslave.Enemies.Enemy.*;
import static ru.m210projects.Powerslave.RunList.*;
import static ru.m210projects.Powerslave.Seq.*;
import static ru.m210projects.Powerslave.Sprites.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ru.m210projects.Build.FileHandle.Resource;
import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Powerslave.Enemies.Enemy.EnemyStruct;
import ru.m210projects.Powerslave.Type.SafeLoader;

public class Wasp {
	
	public static class WaspStruct extends EnemyStruct {
		public static final int size = EnemyStruct.size + 8;
		
		public short nAttackTime;
		public short dTime;
		public short nVelocity;
		public short nDamage;
		
		@Override
		public void save(ByteBuffer bb)
		{
			super.save(bb);
			bb.putShort(nAttackTime);
			bb.putShort(dTime);
			bb.putShort(nVelocity);
			bb.putShort(nDamage);
		}
		
		@Override
		public void load(Resource bb)
		{
			super.load(bb);
			nAttackTime = bb.readShort();
			dTime = bb.readShort();
			nVelocity = bb.readShort();
			nDamage = bb.readShort();
		}
		
		public void copy(WaspStruct src)
		{
			super.copy(src);
			nAttackTime = src.nAttackTime;
			dTime = src.dTime;
			nVelocity = src.nVelocity;
			nDamage = src.nDamage;
		}
	}
	
	public static final int MAX_WASPS = 100;

	public static int nWaspCount;
	private static int nVelShift_X_1;
	private static WaspStruct[] WaspList = new WaspStruct[MAX_WASPS];

	private static final short[][] ActionSeq_X_12 = new short[][] { { 0, 0 }, { 0, 0 }, { 9, 0 }, { 18, 0 }, { 27, 1 },
			{ 28, 1 }, { 29, 1 } };

	public static void InitWasps() {
		nWaspCount = 0;
	}
	
	public static ByteBuffer saveWasp()
	{
		ByteBuffer bb = ByteBuffer.allocate(nWaspCount * WaspStruct.size + 6);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		
		bb.putShort((short)nWaspCount);
		for(int i = 0; i < nWaspCount; i++)
			WaspList[i].save(bb);
		
		bb.putInt(nVelShift_X_1);
		
		return bb;
	}
	
	public static void loadWasp(SafeLoader loader, Resource bb)
	{
		if(bb != null) {
			loader.nWaspCount = bb.readShort();
			for(int i = 0; i < loader.nWaspCount; i++) {
				if(loader.WaspList[i] == null)
					loader.WaspList[i] = new WaspStruct();
				loader.WaspList[i].load(bb);
			}
			loader.nVelShift_X_1 = bb.readInt();
		}
		else
		{
			nWaspCount = loader.nWaspCount;
			for(int i = 0; i < loader.nWaspCount; i++) {
				if (WaspList[i] == null)
					WaspList[i] = new WaspStruct();
				WaspList[i].copy(loader.WaspList[i]);
			}
			nVelShift_X_1 = loader.nVelShift_X_1;
		}
	}

	public static void SetWaspVel(int nSprite) {
		if (nVelShift_X_1 < 0) {
			sprite[nSprite].yvel = (short) (sintable[sprite[nSprite].ang & 0x7FF] << (-nVelShift_X_1));
			sprite[nSprite].xvel = (short) (sintable[sprite[nSprite].ang + 512 & 0x7FF] << (-nVelShift_X_1));
		} else {
			sprite[nSprite].xvel = (short) (sintable[(sprite[nSprite].ang + 512) & 0x7FF] >> nVelShift_X_1);
			sprite[nSprite].yvel = (short) (sintable[sprite[nSprite].ang & 0x7FF] >> nVelShift_X_1);
		}
	}

	public static int BuildWasp(int spr, int x, int y, int z, int sectnum, int ang) {
		int count = nWaspCount++;

		if (nWaspCount >= MAX_WASPS)
			return -1;

		boolean opt = spr == -2;
		if (spr < 0) {
			spr = engine.insertsprite((short) sectnum, (short) 107);
			sprite[spr].x = x;
			sprite[spr].y = y;
			sprite[spr].z = z;
		} else {
			ang = sprite[spr].ang;
			engine.changespritestat((short) spr, (short) 107);
		}

		if (spr < 0 || spr >= MAXSPRITES) {
			game.ThrowError("spr>=0 && spr<MAXSPRITES");
			return -1;
		}

		sprite[spr].cstat = 257;
		sprite[spr].shade = -12;
		sprite[spr].xoffset = 0;
		sprite[spr].yoffset = 0;
		sprite[spr].ang = (short) ang;
		if (opt) {
			sprite[spr].yrepeat = 20;
			sprite[spr].xrepeat = 20;
		} else {
			sprite[spr].xrepeat = 50;
			sprite[spr].yrepeat = 50;
		}
		sprite[spr].picnum = 1;
		sprite[spr].pal = sector[sprite[spr].sectnum].ceilingpal;
		sprite[spr].clipdist = 70;
		sprite[spr].xvel = 0;
		sprite[spr].yvel = 0;
		sprite[spr].zvel = 0;
		sprite[spr].hitag = 0;
		sprite[spr].lotag = (short) (HeadRun() + 1);
		sprite[spr].extra = -1;
		
		if(WaspList[count] == null)
			WaspList[count] = new WaspStruct();

		WaspList[count].nState = 0;
		WaspList[count].nSeq = 0;
		WaspList[count].nSprite = (short) spr;
		WaspList[count].nTarget = -1;
		WaspList[count].nHealth = 800;
		WaspList[count].nDamage = 10;
		if (opt) {
			WaspList[count].field_C = 60;
			WaspList[count].nDamage /= 2;
		} else {
			WaspList[count].field_C = (short) RandomSize(5);
		}
		WaspList[count].nAttackTime = 0;
		WaspList[count].nVelocity = 0;
		WaspList[count].dTime = (short) (RandomSize(7) + 127);
		sprite[spr].owner = (short) AddRunRec(sprite[spr].lotag - 1, 0x1E0000 | count);
		WaspList[count].nFunc = (short) AddRunRec(NewRun, 0x1E0000 | count);
		nCreaturesLeft++;
		nCreaturesMax++;
		
		return spr;
	}

	public static void FuncWasp(int a1, int nDamage, int RunPtr) {
		short nWasp = (short) (RunData[RunPtr].RunEvent & 0xFFFF);
		WaspStruct pWasp = WaspList[nWasp];
		int nSprite = pWasp.nSprite;
		SPRITE pSprite = sprite[nSprite];

		int nState = pWasp.nState;

		int v48 = 0;
		short nObject = (short) (a1 & 0xFFFF);
		switch (a1 & 0x7F0000) {
		case nEventProcess:
			int nSeq = ActionSeq_X_12[nState][0] + SeqOffsets[22];
			pSprite.picnum = (short) GetSeqPicnum2(nSeq, pWasp.nSeq);
			MoveSequence(nSprite, nSeq, pWasp.nSeq);
			if (++pWasp.nSeq >= SeqSize[nSeq]) {
				pWasp.nSeq = 0;
				v48 = 1;
			}

			int nTarget = pWasp.nTarget;
			if (pWasp.nHealth <= 0 || nTarget <= -1
					|| ((sprite[nTarget].cstat & 0x101) != 0 && (SectFlag[sprite[nTarget].sectnum] & 0x2000) == 0)) {
				switch (nState) {
				case 0:
					pSprite.zvel = (short) (sintable[pWasp.nAttackTime & 0x7FF] >> 4);
					pWasp.nAttackTime = (short) ((pWasp.nAttackTime + pWasp.dTime) & 0x7FF);
					MoveCreature(nSprite);
					if (nTarget < 0) {
						if ((nWasp & 0x1F) == (totalmoves & 0x1F))
							pWasp.nTarget = (short) FindPlayer(nSprite, 60);
					} else {
						if (--pWasp.field_C > 0) {
							PlotCourseToSprite(nSprite, nTarget);
						} else {
							pWasp.nState = 1;
							pSprite.zvel = 0;
							pWasp.nSeq = 0;
							pWasp.nVelocity = 1500;
							pWasp.field_C = (short) (RandomSize(5) + 60);
						}
					}
					return;
				case 2:
				case 3:
					if (v48 != 0)
						break;
					return;
				case 1:
					if (--pWasp.field_C > 0) {
						int moveHit = AngleChase(nSprite, nTarget, pWasp.nVelocity, 0, 16);
						if ((moveHit & 0xC000) == 0xC000) {
							if ((moveHit & 0x3FFF) == nTarget) {
								pSprite.xvel = 0;
								pSprite.yvel = 0;
								DamageEnemy(nTarget, nSprite, pWasp.nDamage);
								pWasp.nState = 2;
								pWasp.nSeq = 0;
							}
						}
					} else {
						pWasp.nState = 0;
						pWasp.field_C = (short) RandomSize(6);
					}
					return;
				case 4:
					int v34 = MoveCreature(nSprite) & 0x8000;
					v34 |= 0xC000;
					if (v34 != 0) {
						pWasp.nState = 5;
						pWasp.nSeq = 0;
						pSprite.xvel = 0;
						pSprite.yvel = 0;
						pSprite.zvel = 1024;
					}
					return;
				case 5:
					pSprite.z += pSprite.zvel;
					if (pSprite.z >= sector[pSprite.sectnum].floorz) {
						if (SectBelow[pSprite.sectnum] > -1) {
							BuildSplash(nSprite, pSprite.sectnum);
							pSprite.cstat |= 0x8000;
						}
						pSprite.xvel = 0;
						pSprite.yvel = 0;
						pSprite.zvel = 0;
						pWasp.nState = 6;
						pWasp.nSeq = 0;
						SubRunRec(pWasp.nFunc);
					}
					return;
				default:
					return;
				}
			} else {
				pWasp.nTarget = -1;
				pWasp.nState = 0;
				pWasp.field_C = (short) RandomSize(6);
				return;
			}

			break;
		case nEventView:
			PlotSequence(nObject, ActionSeq_X_12[nState][0] + SeqOffsets[22], pWasp.nSeq, ActionSeq_X_12[nState][1]);
			return;
		case nEventRadialDamage:
			if ((pSprite.cstat & 0x101) == 0)
				return;
			nDamage = CheckRadialDamage(nSprite);
		case nEventDamage:
			if (nDamage != 0) {
				if (pWasp.nHealth > 0) {
					pWasp.nHealth -= nDamage;
					if (pWasp.nHealth > 0) {
						if (RandomSize(4) == 0) {
							pWasp.nState = 3;
							pWasp.nSeq = 0;
						}
						break;
					} else {
						pWasp.nState = 4;
						nVelShift_X_1 = 0;
						pWasp.nSeq = 0;
						pSprite.cstat = 0;
						pSprite.ang = (short) ((pSprite.ang + 1024) & 0x7FF);
						SetWaspVel(nSprite);
						pSprite.zvel = 512;
						nCreaturesLeft--;
					}
				}
			}
			return;
		}

		pWasp.nState = 1;
		pSprite.ang += (RandomSize(9) + 0x300) & 0x7FF;
		pWasp.nVelocity = 3000;
		pSprite.zvel = (short) (-20 - RandomSize(6));
	}
}
