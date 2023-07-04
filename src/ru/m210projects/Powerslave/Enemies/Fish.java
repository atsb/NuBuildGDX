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

import static ru.m210projects.Build.Gameutils.*;
import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Build.Pragmas.*;
import static ru.m210projects.Powerslave.Globals.*;
import static ru.m210projects.Powerslave.Main.*;
import static ru.m210projects.Powerslave.Random.*;
import static ru.m210projects.Powerslave.RunList.*;
import static ru.m210projects.Powerslave.Seq.*;
import static ru.m210projects.Powerslave.Anim.*;
import static ru.m210projects.Powerslave.Sound.*;
import static ru.m210projects.Powerslave.Sprites.*;
import static ru.m210projects.Powerslave.Enemies.Enemy.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ru.m210projects.Powerslave.Type.FishChunk;
import ru.m210projects.Powerslave.Type.SafeLoader;
import ru.m210projects.Build.FileHandle.Resource;
import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Powerslave.Enemies.Enemy.EnemyStruct;

public class Fish {
	
	public static final int MAX_FISHS = 128;

	private static int FishCount;
//	private static int FishSprite;
	private static int nChunksFree;
	private static int nFreeChunk[] = new int[MAX_FISHS];
	private static FishChunk[] FishChunk = new FishChunk[MAX_FISHS];
	private static EnemyStruct[] FishList = new EnemyStruct[MAX_FISHS];

	private static final short[][] ActionSeq_X_5 = new short[][] { { 8, 0 }, { 8, 0 }, { 0, 0 }, { 24, 0 }, { 8, 0 },
			{ 32, 1 }, { 33, 1 }, { 34, 1 }, { 35, 1 }, { 39, 1 }, };

	public static void InitFishes() {
		FishCount = 0;
//		FishSprite = 1;
		nChunksFree = MAX_FISHS;
		for (int i = 0; i < MAX_FISHS; i++)
			nFreeChunk[i] = i;

		for (int i = 0; i < MAX_FISHS; i++) {
			if (FishChunk[i] == null)
				FishChunk[i] = new FishChunk();
		}
	}
	
	public static ByteBuffer saveFish()
	{
		ByteBuffer bb = ByteBuffer.allocate(FishCount * EnemyStruct.size + 3 + ((6 + 2) * MAX_FISHS));
		bb.order(ByteOrder.LITTLE_ENDIAN);
		
		bb.putShort((short)FishCount);
		bb.put((byte)nChunksFree);
		for(int i = 0; i < MAX_FISHS; i++) {
			bb.putShort((short)nFreeChunk[i]);
			FishChunk[i].save(bb);
		}
		
		for(int i = 0; i < FishCount; i++)
			FishList[i].save(bb);
		
		return bb;
	}
	
	public static void loadFish(SafeLoader loader, Resource bb)
	{
		if(bb != null) {
			loader.FishCount = bb.readShort();
			loader.nChunksFree = bb.readByte() & 0xFF;
			for(int i = 0; i < MAX_FISHS; i++) {
				loader.nFreeChunk[i] = bb.readShort();
				if(loader.FishChunk[i] == null)
					loader.FishChunk[i] = new FishChunk();
				loader.FishChunk[i].load(bb);
			}
			for(int i = 0; i < loader.FishCount; i++) {
				if(loader.FishList[i] == null)
					loader.FishList[i] = new EnemyStruct();
				loader.FishList[i].load(bb);
			}
		} 
		else
		{
			FishCount = loader.FishCount;
			nChunksFree = loader.nChunksFree;
			for(int i = 0; i < MAX_FISHS; i++) {
				nFreeChunk[i] = loader.nFreeChunk[i];
				if(FishChunk[i] == null)
					FishChunk[i] = new FishChunk();
				FishChunk[i].copy(loader.FishChunk[i]);
			}
			
			for(int i = 0; i < loader.FishCount; i++) {
				if(FishList[i] == null)
					FishList[i] = new EnemyStruct();
				FishList[i].copy(loader.FishList[i]);
			}
		}
	}

	public static void BuildBlood(int a1, int a2, int a3, int a4) {
		BuildAnim(-1, 19, 36, a1, a2, a3, a4, 0x4B, -128);
	}

	public static void BuildFishLimb(int a1, int a2) {
		if (nChunksFree > 0) {
			int nSprite = FishList[a1].nSprite;
			int nChunk = nFreeChunk[nChunksFree-- - 1];
			FishChunk pChunk = FishChunk[nChunk];

			int spr = engine.insertsprite(sprite[nSprite].sectnum, (short) 99);
			if (spr < 0 || spr >= MAXSPRITES) {
				game.ThrowError("spr>=0 && spr<MAXSPRITES");
				return;
			}

			pChunk.nSprite = (short) spr;
			pChunk.ActionSeq = (short) (a2 + 40);
			pChunk.nSeq = (short) (RandomSize(3) % SeqSize[SeqOffsets[19] + pChunk.ActionSeq]);

			SPRITE pSprite = sprite[spr];
			pSprite.x = sprite[nSprite].x;
			pSprite.y = sprite[nSprite].y;
			pSprite.z = sprite[nSprite].z;
			pSprite.cstat = 0;
			pSprite.shade = -12;
			pSprite.pal = 0;
			pSprite.xvel = (short) (((RandomSize(5) & 0xFFFF) - 16) << 8);
			pSprite.yvel = (short) (((RandomSize(5) & 0xFFFF) - 16) << 8);
			pSprite.xrepeat = 64;
			pSprite.yrepeat = 64;
			pSprite.xoffset = 0;
			pSprite.yoffset = 0;
			pSprite.zvel = (short) (-2 * (RandomByte() + 512));
			GetSeqPicnum(19, pChunk.ActionSeq, 0);
			pSprite.picnum = (short) a2;
			pSprite.lotag = (short) (HeadRun() + 1);
			pSprite.clipdist = 0;
			pSprite.extra = -1;
			pSprite.owner = (short) AddRunRec(pSprite.lotag - 1, 0x200000 | nChunk);
			pSprite.hitag = (short) AddRunRec(NewRun, 0x200000 | nChunk);
		}
	}

	public static void BuildFish(int spr, int x, int y, int z, int sectnum, int ang) {
		int count = FishCount++;

		if (count >= MAX_FISHS)
			return;

		if (spr == -1) {
			spr = engine.insertsprite((short) sectnum, (short) 103);

		} else {
			x = sprite[spr].x;
			y = sprite[spr].y;
			z = sprite[spr].z;
			ang = sprite[spr].ang;
			engine.changespritestat((short) spr, (short) 103);
		}

		if (spr < 0 || spr >= MAXSPRITES) {
			game.ThrowError("spr>=0 && spr<MAXSPRITES");
			return;
		}

		sprite[spr].x = x;
		sprite[spr].y = y;
		sprite[spr].z = z;
		sprite[spr].shade = -12;
		sprite[spr].cstat = 257;
		sprite[spr].clipdist = 80;
		sprite[spr].xrepeat = 40;
		sprite[spr].yrepeat = 40;
		sprite[spr].pal = sector[sprite[spr].sectnum].ceilingpal;
		sprite[spr].xoffset = 0;
		sprite[spr].yoffset = 0;
		sprite[spr].picnum = (short) GetSeqPicnum(19, ActionSeq_X_5[0][0], 0);
		sprite[spr].ang = (short) ang;
		sprite[spr].xvel = 0;
		sprite[spr].yvel = 0;
		sprite[spr].zvel = 0;

		sprite[spr].hitag = 0;
		sprite[spr].lotag = (short) (HeadRun() + 1);
		sprite[spr].extra = -1;
		
		if (FishList[count] == null)
			FishList[count] = new EnemyStruct();

		FishList[count].nState = 0;
		FishList[count].nHealth = 200;
		FishList[count].nSprite = (short) spr;
		FishList[count].nTarget = -1;
		FishList[count].field_C = 60;
		FishList[count].nSeq = 0;

		sprite[spr].owner = (short) AddRunRec(sprite[spr].lotag - 1, 0x120000 | count);
		FishList[count].nFunc = (short) AddRunRec(NewRun, 0x120000 | count);
		nCreaturesLeft++;
		nCreaturesMax++;
	}

	public static void IdleFish(int nFish, int a2) {
		int nSprite = FishList[nFish].nSprite;
		sprite[nSprite].ang += (256 - RandomSize(9)) + 1024;
		sprite[nSprite].ang &= 0x7FF;

		sprite[nSprite].xvel = (short) (sintable[(sprite[nSprite].ang + 512) & 0x7FF] >> 8);
		sprite[nSprite].yvel = (short) (sintable[sprite[nSprite].ang & 0x7FF] >> 8);

		FishList[nFish].nState = 0;
		FishList[nFish].nSeq = 0;
		sprite[nSprite].zvel = (short) RandomSize(9);

		if (a2 > 0)
			return;
		if (a2 == 0 && RandomBit() == 0)
			return;

		sprite[nSprite].zvel = (short) -sprite[nSprite].zvel;
	}

	private static short DestroyFish(int nFish) {
		DoSubRunRec(sprite[FishList[nFish].nSprite].owner);
		FreeRun(sprite[FishList[nFish].nSprite].lotag - 1);
		SubRunRec(FishList[nFish].nFunc);
		return engine.mydeletesprite(FishList[nFish].nSprite);
	}

	public static void FuncFishLimb(int a1, int a2, int RunPtr) {
		short nChunk = (short) (RunData[RunPtr].RunEvent & 0xFFFF);
		short spr = FishChunk[nChunk].nSprite;

		if (spr < 0 || spr >= MAXSPRITES) {
			game.ThrowError("spr>=0 && spr<MAXSPRITES");
			return;
		}

		SPRITE pSprite = sprite[spr];
		int v6 = SeqOffsets[19] + FishChunk[nChunk].ActionSeq;
		switch (a1 & 0x7F0000) {
		case nEventProcess:
			pSprite.picnum = (short) GetSeqPicnum2(v6, FishChunk[nChunk].nSeq);
			Gravity(spr);
			FishChunk[nChunk].nSeq++;
			if (FishChunk[nChunk].nSeq >= SeqSize[v6]) {
				FishChunk[nChunk].nSeq = 0;
				if (RandomBit() != 0)
					BuildBlood(pSprite.x, pSprite.y, pSprite.z, pSprite.sectnum);
			}

			if (sector[pSprite.sectnum].floorz > pSprite.z) {
				if (engine.movesprite(spr, pSprite.xvel << 8, pSprite.yvel << 8, pSprite.zvel, 2560, -2560, 1) != 0) {
					pSprite.xvel = pSprite.yvel = 0;
				}
			} else {
				pSprite.z += 256;
				int dz = pSprite.z - sector[pSprite.sectnum].floorz;
				if (dz <= 25600) {
					if (dz > 0)
						pSprite.zvel = 1024;
				} else {
					pSprite.zvel = 0;
					DoSubRunRec(pSprite.owner);
					FreeRun(pSprite.lotag - 1);
					SubRunRec(pSprite.hitag);
					engine.mydeletesprite(spr);
				}
			}
			return;
		case nEventView:
			PlotSequence(a1 & 0xFFFF, v6, FishChunk[nChunk].nSeq, 1);
			return;
		}
	}

	public static void FuncFish(int a1, int a2, int RunPtr) {
		short nFish = (short) (RunData[RunPtr].RunEvent & 0xFFFF);
		if (nFish < 0 || nFish >= MAX_FISHS) {
			game.ThrowError("Fish>=0 && Fish<MAX_FISHS");
			return;
		}

		int nSprite = FishList[nFish].nSprite;
		SPRITE pSprite = sprite[nSprite];
		int nState = FishList[nFish].nState;
		short plr = (short) (a1 & 0xFFFF);
		int damage = a2;
	
		switch (a1 & 0x7F0000) {
		case nEventProcess:
			if ((SectFlag[sprite[nSprite].sectnum] & 0x2000) == 0)
				Gravity(nSprite);

			int v10 = ActionSeq_X_5[nState][0] + SeqOffsets[19];
			pSprite.picnum = (short) GetSeqPicnum2(v10, FishList[nFish].nSeq);
			MoveSequence(nSprite, v10, FishList[nFish].nSeq);
			FishList[nFish].nSeq++;
			if (FishList[nFish].nSeq >= SeqSize[v10])
				FishList[nFish].nSeq = 0;

			int nTarget = FishList[nFish].nTarget;
			switch (nState) {
			case 1:
			default:
				return;
			case 4:
				if (FishList[nFish].nSeq == 0)
					IdleFish(nFish, 0);
				return;
			case 9:
				if (FishList[nFish].nSeq == 0) 
					DestroyFish(nFish);
				return;
			case 2:
			case 3:
				FishList[nFish].field_C--;
				if (FishList[nFish].field_C <= 0) {
					IdleFish(nFish, 0);
					return;
				}
				
				if(nTarget == -1)
					break;
				
				PlotCourseToSprite(nSprite, nTarget);
				int dz = sprite[nTarget].z - sprite[nSprite].z;
				if (klabs(dz) <= GetSpriteHeight(nSprite) >> 1) {
					int cos = sintable[(sprite[nSprite].ang + 512) & 0x7FF];
					int sin = sintable[sprite[nSprite].ang & 0x7FF];
					sprite[nSprite].xvel = (short) ((cos >> 5) - (cos >> 7));
					sprite[nSprite].yvel = (short) ((sin >> 5) - (sin >> 7));
				} else {
					sprite[nSprite].xvel = 0;
					sprite[nSprite].yvel = 0;
				}
				sprite[nSprite].zvel = (short) (dz >> 3);
				break;
			case 0:
				FishList[nFish].field_C--;
				if (FishList[nFish].field_C <= 0) {
					nTarget = FindPlayer(nSprite, 60);
					if (nTarget < 0)
						IdleFish(nFish, 0);
					else {
						FishList[nFish].nTarget = (short) nTarget;
						FishList[nFish].nState = 2;
						FishList[nFish].nSeq = 0;
						sprite[nSprite].zvel = (short) (sintable[engine.GetMyAngle(
								sprite[nTarget].x - sprite[nSprite].x, sprite[nTarget].z - sprite[nSprite].z)
								& 0x7FF] >> 5);
						FishList[nFish].field_C = (short) (RandomSize(6) + 90);
					}
				}
				break;
			}
			int ox = sprite[nSprite].x;
			int oy = sprite[nSprite].y;
			int oz = sprite[nSprite].z;
			short osect = sprite[nSprite].sectnum;
			int hitMove = engine.movesprite((short) nSprite, sprite[nSprite].xvel << 13, sprite[nSprite].yvel << 13,
					4 * sprite[nSprite].zvel, 0, 0, 0);
			
			int v18;
			if ((SectFlag[sprite[nSprite].sectnum] & 0x2000) != 0) {
				if (nState >= 5) 
                    return;

				if (hitMove == 0) {
					if (nState == 3) {
						FishList[nFish].nState = 2;
						FishList[nFish].nSeq = 0;
					}
					return;
				}

				if ((hitMove & 0x30000) == 0) {
					switch (hitMove & 0xC000) {
					default:
						return;
					case 0xC000:
						int nHitObject = hitMove & 0x3FFF;
						if (isValidSprite(nHitObject) && sprite[nHitObject].statnum == 100) {
							nTarget = FishList[nFish].nTarget = (short) nHitObject;
							sprite[nSprite].ang = engine.GetMyAngle(sprite[nTarget].x - sprite[nSprite].x,
									sprite[nTarget].y - sprite[nSprite].y);
							if (nState != 3) {
								FishList[nFish].nState = 3;
								FishList[nFish].nSeq = 0;
							}
							if (FishList[nFish].nSeq == 0)
								DamageEnemy(nTarget, nSprite, 2);
						}
						return;
					case 0x8000:
						IdleFish(nFish, 0);
						return;
					}
				} 
				if ((hitMove & 0x20000) != 0)
					v18 = -1;
	         	else v18 = 1;
			} else {
				engine.mychangespritesect((short) nSprite, osect);
				sprite[nSprite].x = ox;
				sprite[nSprite].y = oy;
				sprite[nSprite].z = oz;
				v18 = 0;
			}
			IdleFish(nFish, v18);
			return;
		case nEventView:
			PlotSequence(plr, ActionSeq_X_5[FishList[nFish].nState][0] + SeqOffsets[19], FishList[nFish].nSeq,
					ActionSeq_X_5[FishList[nFish].nState][1]);
//			tsprite[plr].owner = -1;
			return;
		case nEventRadialDamage:
			if (FishList[nFish].nHealth <= 0)
				return;
			damage = CheckRadialDamage(nSprite);
			if (damage != 0)
				FishList[nFish].field_C = 10;
		case nEventDamage:
			if (damage != 0) {
				if (FishList[nFish].nHealth > 0) {
					FishList[nFish].nHealth -= damage;
					if (FishList[nFish].nHealth > 0) {
						if (plr >= 0 && sprite[plr].statnum < 199)
							FishList[nFish].nTarget = (short) plr;
						FishList[nFish].nSeq = 0;
						FishList[nFish].nState = 4;
						FishList[nFish].field_C += 10;
					} else {
						FishList[nFish].nHealth = 0;
						pSprite.cstat &= 0xFEFE;
						if ((a1 & 0x7F0000) != nEventDamage) {
							FishList[nFish].nState = 9;
							FishList[nFish].nSeq = 0;
							return;
						}
						for (int i = 0; i < 3; i++)
							BuildFishLimb(nFish, i);
						PlayFXAtXYZ(StaticSound[40], sprite[nSprite].x, sprite[nSprite].y, sprite[nSprite].z,
								sprite[nSprite].sectnum);
						DestroyFish(nFish);
	
						nCreaturesLeft--;
					}
				}
			}
			return;
		}
	}
}
