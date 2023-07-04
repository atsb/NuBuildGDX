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
import static ru.m210projects.Powerslave.Bullet.*;
import static ru.m210projects.Powerslave.Random.*;
import static ru.m210projects.Powerslave.Enemies.Enemy.*;
import static ru.m210projects.Powerslave.Enemies.Spider.*;
import static ru.m210projects.Powerslave.RunList.*;
import static ru.m210projects.Powerslave.Seq.*;
import static ru.m210projects.Powerslave.Sound.*;
import static ru.m210projects.Powerslave.Sprites.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ru.m210projects.Build.FileHandle.Resource;
import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Powerslave.Enemies.Enemy.EnemyStruct;
import ru.m210projects.Powerslave.Type.SafeLoader;

public class Scorp {
	
	public static class ScorpStruct extends EnemyStruct {
		public static final int size = 18;
		public short field_B;
		
		@Override
		public void save(ByteBuffer bb)
		{
			super.save(bb);
			bb.putShort(field_B);
		}
		
		@Override
		public void load(Resource bb)
		{
			super.load(bb);
			field_B = bb.readShort();
		}
		
		public void copy(ScorpStruct src)
		{
			super.copy(src);
			field_B = src.field_B;
		}
	}
	
	public static final int MAXSCORP = 5;

	private static int ScorpCount;
	private static ScorpStruct[] ScorpList = new ScorpStruct[MAXSCORP];
	private static short[] ScorpChan = new short[MAXSCORP];

	private static final short[][] ActionSeq_X_7 = new short[][] { { 0, 0 }, { 8, 0 }, { 29, 0 }, { 19, 0 }, { 45, 1 },
			{ 46, 1 }, { 47, 1 }, { 48, 1 }, { 50, 1 }, { 53, 1 } };

	public static void InitScorp() {
		ScorpCount = MAXSCORP;
	}
	
	public static ByteBuffer saveScorp()
	{
		ByteBuffer bb = ByteBuffer.allocate((MAXSCORP - ScorpCount) * ScorpStruct.size + 2 + (2 * MAXSCORP));
		bb.order(ByteOrder.LITTLE_ENDIAN);
		
		bb.putShort((short)ScorpCount);
		for(int i = ScorpCount; i < MAXSCORP; i++)
			ScorpList[i].save(bb);
		for(int i = 0; i < MAXSCORP; i++)
			bb.putShort(ScorpChan[i]);
		
		return bb;
	}
	
	public static void loadScorp(SafeLoader loader, Resource bb)
	{
		if(bb != null) {
			loader.ScorpCount = bb.readShort();
			for(int i = loader.ScorpCount; i < MAXSCORP; i++) {
				if(loader.ScorpList[i] == null)
					loader.ScorpList[i] = new ScorpStruct();
				loader.ScorpList[i].load(bb);
			}
			for(int i = 0; i < MAXSCORP; i++)
				loader.ScorpChan[i] = bb.readShort();
		}
		else
		{
			ScorpCount = loader.ScorpCount;
			for(int i = loader.ScorpCount; i < MAXSCORP; i++) {
				if (ScorpList[i] == null)
					ScorpList[i] = new ScorpStruct();
				ScorpList[i].copy(loader.ScorpList[i]);
			}
			System.arraycopy(loader.ScorpChan, 0, ScorpChan, 0, MAXSCORP);
		}
	}

	public static void BuildScorp(int spr, int x, int y, int z, int sectnum, int ang, int channel) {
		int count = --ScorpCount;

		if (count < 0)
			return;

		if (spr == -1) {
			spr = engine.insertsprite((short) sectnum, (short) 122);

		} else {
			x = sprite[spr].x;
			y = sprite[spr].y;
			z = sector[sprite[spr].sectnum].floorz;
			ang = sprite[spr].ang;
			engine.changespritestat((short) spr, (short) 122);
		}

		if (spr < 0 || spr >= MAXSPRITES) {
			game.ThrowError("spr>=0 && spr<MAXSPRITES");
			return;
		}

		sprite[spr].x = x;
		sprite[spr].y = y;
		sprite[spr].z = z;
		sprite[spr].cstat = 257;
		sprite[spr].xoffset = 0;
		sprite[spr].shade = -12;
		sprite[spr].yoffset = 0;
		sprite[spr].picnum = 1;
		sprite[spr].pal = sector[sprite[spr].sectnum].ceilingpal;
		sprite[spr].clipdist = 70;
		sprite[spr].ang = (short) ang;
		sprite[spr].xrepeat = 80;
		sprite[spr].yrepeat = 80;
		sprite[spr].xvel = 0;
		sprite[spr].yvel = 0;
		sprite[spr].zvel = 0;
		sprite[spr].hitag = 0;
		sprite[spr].lotag = (short) (HeadRun() + 1);
		sprite[spr].extra = -1;
		
		if (ScorpList[count] == null)
			ScorpList[count] = new ScorpStruct();

		ScorpList[count].nState = 0;
		ScorpList[count].nHealth = 20000;
		ScorpList[count].nSeq = 0;
		ScorpList[count].nSprite = (short) spr;
		ScorpList[count].nTarget = -1;
		ScorpList[count].field_A = 1;
		ScorpList[count].field_C = 0;
		ScorpChan[count] = (short) channel;
		sprite[spr].owner = (short) AddRunRec(sprite[spr].lotag - 1, 0x220000 | count);
		ScorpList[count].nFunc = (short) AddRunRec(NewRun, 0x220000 | count);
		nCreaturesLeft++;
		nCreaturesMax++;
	}

	public static void FuncScorp(int a1, int nDamage, int RunPtr) {
		short nScorp = (short) (RunData[RunPtr].RunEvent & 0xFFFF);
		if (nScorp < 0 || nScorp >= 5) {
			game.ThrowError("scorp>=0 && scorp<MAXSCORP");
			return;
		}

		ScorpStruct pScorp = ScorpList[nScorp];
		int nSprite = pScorp.nSprite;
		SPRITE pSprite = sprite[nSprite];

		int nState = pScorp.nState;
		int nTarget = pScorp.nTarget;

		short nObject = (short) (a1 & 0xFFFF);

		int v5 = 0;
		switch (a1 & 0x7F0000) {
		case nEventProcess:
			if (pScorp.nHealth != 0)
				Gravity(nSprite);

			int nSeq = ActionSeq_X_7[nState][0] + SeqOffsets[24];
			pSprite.picnum = (short) GetSeqPicnum2(nSeq, pScorp.nSeq);
			MoveSequence(nSprite, nSeq, pScorp.nSeq);
			if (++pScorp.nSeq >= SeqSize[nSeq]) {
				pScorp.nSeq = 0;
				v5 = 1;
			}
			int nFlags = FrameFlag[pScorp.nSeq + SeqBase[nSeq]];
			switch (nState) {
			case 0:
				if (pScorp.field_C <= 0) {
					if ((nScorp & 0x1F) == (totalmoves & 0x1F) && nTarget < 0) {
						int nNewTarget = FindPlayer(nSprite, 500);
						if (nNewTarget >= 0) {
							D3PlayFX(StaticSound[41], nSprite);
							pScorp.nSeq = 0;
							pSprite.xvel = sintable[(pSprite.ang + 512) & 0x7FF];
							pScorp.nState = 1;
							pScorp.nTarget = (short) nNewTarget;
							pSprite.yvel = sintable[pSprite.ang & 0x7FF];
						}
					}
				} else
					pScorp.field_C--;
				return;
			case 1:
				if (--pScorp.field_A <= 0) {
					pScorp.field_A = (short) RandomSize(5);
					PlotCourseToSprite(nSprite, nTarget);
					pSprite.ang += RandomSize(7) - 63;
					pSprite.xvel = sintable[(pSprite.ang + 512) & 0x7FF];
					pSprite.yvel = sintable[pSprite.ang & 0x7FF];
				} else {
					int hitMove = MoveCreatureWithCaution(nSprite);
					switch (hitMove & 0xC000) {
					case 0x8000:
					case 0xC000:
						if ((hitMove & 0x3FFF) == nTarget) {
							if (AngleDiff(pSprite.ang, engine.getangle(sprite[nTarget].x - pSprite.x,
									sprite[nTarget].y - pSprite.y)) < 64) {
								pScorp.nState = 2;
								pScorp.nSeq = 0;
								break;
							}
						}
						PlotCourseToSprite(nSprite, nTarget);
						pSprite.ang += RandomSize(7) - 63;
						pSprite.xvel = sintable[(pSprite.ang + 512) & 0x7FF];
						pSprite.yvel = sintable[pSprite.ang & 0x7FF];
						break;
					}
				}
				break;
			case 2:
				if (nTarget == -1) {
					pScorp.nState = 0;
					pScorp.field_C = 5;
				} else if (PlotCourseToSprite(nSprite, nTarget) >= 768)
					pScorp.nState = 1;
				else if ((nFlags & 0x80) != 0)
					DamageEnemy(nTarget, nSprite, 7);
				break;
			case 3:
				if (v5 != 0 && --pScorp.field_B <= 0) {
					pScorp.nState = 1;
					pScorp.nSeq = 0;
					pSprite.xvel = sintable[(pSprite.ang + 512) & 0x7FF];
					pSprite.yvel = sintable[pSprite.ang & 0x7FF];
				} else if ((nFlags & 0x80) != 0) {
					int nBullet = BuildBullet(nSprite, 16, 0, 0, -1, pSprite.ang, nTarget + 10000, 1);
					if (nBullet > -1)
						PlotCourseToSprite((short)(nBullet & 0xFFFF), nTarget);
				}
				return;
			case 8:
				if (v5 != 0) {
					pScorp.nState++;
					pScorp.nSeq = 0;
					ChangeChannel(ScorpChan[nScorp], 1);
				} else {
					int nSpider = BuildSpider(-1, pSprite.x, pSprite.y, pSprite.z,
							pSprite.sectnum, pSprite.ang);
					if (nSpider != -1) {
						SPRITE pSpider = sprite[nSpider];
						pSpider.ang = (short) (RandomSize(11) & 0x7FF);
						int vel = RandomSize(5) + 1;
						pSpider.xvel = (short) (vel * (sintable[(pSpider.ang + 512) & 0x7FF] >> 8));
						pSpider.yvel = (short) (vel * (sintable[pSpider.ang] >> 8));
						pSpider.zvel = (short) (-256 * (RandomSize(5) + 3));
					}
				}
				return;
			case 4:
			case 5:
			case 6:
			case 7:
				if (v5 != 0) {
					if (pScorp.nHealth > 0) {
						pScorp.nState = 1;
						pScorp.nSeq = 0;
						pScorp.field_C = 0;
					} else {
						if (--pScorp.field_C > 0)
							pScorp.nState = (short) (RandomBit() + 6);
						else
							pScorp.nState = 8;
					}
				}
				return;
			case 9:
				pSprite.cstat &= ~0x101;
				if (v5 != 0) {
					SubRunRec(pScorp.nFunc);
					DoSubRunRec(pSprite.owner);
					FreeRun(pSprite.lotag - 1);
					engine.mydeletesprite((short) nSprite);
				}
				return;
			default:
				return;
			}
			break;
		case nEventView:
			PlotSequence(nObject, ActionSeq_X_7[nState][0] + SeqOffsets[24], pScorp.nSeq, ActionSeq_X_7[nState][1]);
			return;
		case nEventRadialDamage:
			nDamage = CheckRadialDamage(nSprite);
		case nEventDamage:
			if (nDamage != 0) {
				if (pScorp.nHealth > 0) {
					pScorp.nHealth -= nDamage;
					if (pScorp.nHealth > 0) {
						if (nObject >= 0) {
							if (sprite[nObject].statnum == 100 || sprite[nObject].statnum < 199 && RandomSize(5) == 0)
								pScorp.nTarget = nObject;
						}
						if (RandomSize(5) == 0) {
							pScorp.nState = (short) (RandomSize(2) + 4);
							pScorp.nSeq = 0;
							return;
						}

						if (RandomSize(2) == 0) {
							D3PlayFX(StaticSound[41], nSprite);
							PlotCourseToSprite(nSprite, nTarget);
							pSprite.ang += RandomSize(7) - 63;
							pSprite.xvel = sintable[(pSprite.ang + 512) & 0x7FF];
							pSprite.yvel = sintable[pSprite.ang & 0x7FF];
							break;
						}
					} else {
						pSprite.zvel = 0;
						pScorp.nHealth = 0;
						pSprite.yvel = 0;
						pScorp.nState = 4;
						pSprite.xvel = 0;
						pScorp.nSeq = 0;
						pSprite.cstat &= ~0x101;
						pScorp.field_C = 10;
						nCreaturesLeft--;
					}
				}
			}
			return;
		}

		if (nState != 2) {
			if (pScorp.field_C != 0) {
				pScorp.field_C--;
			} else {
				pScorp.field_C = 45;
				if (nTarget != -1 && engine.cansee(pSprite.x, pSprite.y, pSprite.z - GetSpriteHeight(nSprite), pSprite.sectnum,
						sprite[nTarget].x, sprite[nTarget].y, sprite[nTarget].z - GetSpriteHeight(nTarget),
						sprite[nTarget].sectnum)) {
					pSprite.yvel = 0;
					pSprite.xvel = 0;
					pSprite.ang = engine.GetMyAngle(sprite[nTarget].x - pSprite.x, sprite[nTarget].y - pSprite.y);
					int v41 = RandomSize(3);
					pScorp.field_B = (short) (v41 + RandomSize(2));
					if (pScorp.field_B != 0) {
						pScorp.nState = 3;
						pScorp.nSeq = 0;
					} else
						pScorp.field_C = (short) RandomSize(5);
				}
			}
		}

		if (nState != 0 && nTarget != -1 && (sprite[nTarget].cstat & 0x101) == 0) {
			pScorp.nState = 0;
			pScorp.nSeq = 0;
			pScorp.field_C = 30;
			pScorp.nTarget = -1;
			pSprite.xvel = 0;
			pSprite.yvel = 0;
		}
	}

}
