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

import static ru.m210projects.Build.Engine.sprite;
import static ru.m210projects.Powerslave.Globals.*;
import static ru.m210projects.Powerslave.Main.engine;
import static ru.m210projects.Powerslave.Object.SetQuake;
import static ru.m210projects.Powerslave.RunList.AddRunRec;
import static ru.m210projects.Powerslave.RunList.DoSubRunRec;
import static ru.m210projects.Powerslave.RunList.FreeRun;
import static ru.m210projects.Powerslave.RunList.HeadRun;
import static ru.m210projects.Powerslave.RunList.NewRun;
import static ru.m210projects.Powerslave.RunList.RunData;
import static ru.m210projects.Powerslave.RunList.SubRunRec;
import static ru.m210projects.Powerslave.Seq.GetSeqPicnum2;
import static ru.m210projects.Powerslave.Seq.MoveSequence;
import static ru.m210projects.Powerslave.Seq.PlotSequence;
import static ru.m210projects.Powerslave.Seq.SeqOffsets;
import static ru.m210projects.Powerslave.Seq.SeqSize;
import static ru.m210projects.Powerslave.Sprites.DamageEnemy;
import static ru.m210projects.Powerslave.Sprites.GetSpriteHeight;
import static ru.m210projects.Powerslave.Weapons.AddAmmo;
import static ru.m210projects.Powerslave.Weapons.SelectNewWeapon;
import static ru.m210projects.Powerslave.Weapons.weaponInfo;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ru.m210projects.Build.FileHandle.Resource;
import ru.m210projects.Powerslave.Type.RaStruct;
import ru.m210projects.Powerslave.Type.SafeLoader;

public class Ra {
//	private static int RaCount;
	private static short[] ActionSeq_X_3 = { 2, 0, 1, 2 };

	public static void InitRa() {
//		RaCount = 0;
	}
	
	public static ByteBuffer saveRa()
	{
		ByteBuffer bb = ByteBuffer.allocate(RaStruct.size);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		
		Ra[nLocalPlayer].save(bb);
		
		return bb;
	}
	
	public static void loadRa(SafeLoader loader, Resource bb)
	{
		if(bb != null) {
			if(loader.Ra[nLocalPlayer] == null)
				loader.Ra[nLocalPlayer] = new RaStruct();
			loader.Ra[nLocalPlayer].load(bb);
		}
		else
		{
			if(Ra[nLocalPlayer] == null)
				Ra[nLocalPlayer] = new RaStruct();
			Ra[nLocalPlayer].copy(loader.Ra[nLocalPlayer]);
		}
	}

	public static void BuildRa(int nPlayer) {
		short spr = engine.insertsprite(sprite[PlayerList[nPlayer].spriteId].sectnum, (short) 203);
		sprite[spr].cstat = (short) 32768;
		sprite[spr].xvel = 0;
		sprite[spr].yvel = 0;
		sprite[spr].zvel = 0;
		sprite[spr].extra = -1;
		sprite[spr].lotag = (short) (HeadRun() + 1);
		sprite[spr].hitag = 0;
		sprite[spr].owner = (short) AddRunRec(sprite[spr].lotag - 1, 0x210000 | nPlayer);
		sprite[spr].pal = 1;
		sprite[spr].xrepeat = 64;
		sprite[spr].yrepeat = 64;
		sprite[spr].x = sprite[PlayerList[nPlayer].spriteId].x;
		sprite[spr].y = sprite[PlayerList[nPlayer].spriteId].y;
		sprite[spr].z = sprite[PlayerList[nPlayer].spriteId].z;
		
		if(Ra[nPlayer] == null)
			Ra[nPlayer] = new RaStruct();

		Ra[nPlayer].nSprite = spr;
		Ra[nPlayer].nFunc = (short) AddRunRec(NewRun, 0x210000 | nPlayer);
		Ra[nPlayer].nTarget = -1;
		Ra[nPlayer].nSeq = 0;
		Ra[nPlayer].nState = 0;
		Ra[nPlayer].field_C = 0;
		Ra[nPlayer].nPlayer = (short) nPlayer;
	}

	public static void FreeRa(int a1) {
		SubRunRec(Ra[a1].nFunc);
		DoSubRunRec(sprite[Ra[a1].nSprite].owner);
		FreeRun(sprite[Ra[a1].nSprite].lotag - 1);
		engine.mydeletesprite(Ra[a1].nSprite);
	}

	public static void FuncRa(int a1, int a2, int a3) {
		short nRa = (short) (RunData[a3].RunEvent & 0xFFFF);
		RaStruct pRa = Ra[nRa];
		int v18 = 0;
		int nCurrentWeapon = PlayerList[nRa].currentWeapon;
		int nSeq = ActionSeq_X_3[pRa.nState] + SeqOffsets[68];
		
		switch (a1 & 0x7F0000) {
		case nEventProcess:
			Ra[nRa].nTarget = sPlayerInput[nRa].nTarget;
			int nSprite = pRa.nSprite;
			sprite[nSprite].picnum = (short) GetSeqPicnum2(nSeq, pRa.nSeq);
			if (pRa.nState != 0) {
				MoveSequence(nSprite, nSeq, pRa.nSeq);
				if (++pRa.nSeq >= SeqSize[nSeq]) {
					pRa.nSeq = 0;
					v18 = 1;
				}
			}

			switch (pRa.nState) {
			case 0:
				MoveRaToEnemy(nRa);
				if (pRa.field_C != 0 && pRa.nTarget > -1) {
					sprite[nSprite].cstat &= ~0x8000;
					pRa.nState = 1;
					pRa.nSeq = 0;
					break;
				}
				sprite[nSprite].cstat = (short) 32768;
				return;
			case 1:
				if (pRa.field_C != 0) {
					if (v18 != 0)
						pRa.nState = 2;
					MoveRaToEnemy(nRa);
				} else {
					pRa.nState = 3;
					pRa.nSeq = 0;
				}
				return;
			case 2:
				MoveRaToEnemy(nRa);
				if (nCurrentWeapon != 6) {
					pRa.nState = 3;
					pRa.nSeq = 0;
					break;
				}

				if (pRa.nSeq != 0 || pRa.nTarget <= -1) {
					if (v18 == 0)
						return;
					pRa.nState = 3;
					pRa.nSeq = 0;
				} else if (PlayerList[nRa].AmmosAmount[6] <= 0) {
					pRa.nState = 3;
					pRa.nSeq = 0;
					SelectNewWeapon(nRa);
				} else {
					DamageEnemy(pRa.nTarget, PlayerList[pRa.nPlayer].spriteId, 200);
					AddAmmo(nRa, 6, -weaponInfo[6].field_1E);
					SetQuake(nSprite, 100);
				}
				break;
			case 3:
				if (v18 != 0) {
					sprite[nSprite].cstat |= 0x8000;
					pRa.nState = 0;
					pRa.nSeq = 0;
					pRa.field_C = 0;
				}
				break;
			}
			return;
		case nEventView:
			PlotSequence(a1 & 0xFFFF, nSeq, pRa.nSeq, 1);
			return;
		}
	}

	public static void MoveRaToEnemy(int nRa) {
		int nTarget = Ra[nRa].nTarget;
		int nSprite = Ra[nRa].nSprite;
		int nState = Ra[nRa].nState;

		if (nTarget == -1) {
			if (nState == 1 || nState == 2) {
				Ra[nRa].nState = 3;
				Ra[nRa].nSeq = 0;
				return;
			}
			if (nState != 0)
				return;
			nTarget = PlayerList[nRa].spriteId;
			sprite[nSprite].cstat = (short) 32768;
		} else {
			if ((sprite[nTarget].cstat & 0x101) == 0 || sprite[nTarget].sectnum == 1024) {
				Ra[nRa].nTarget = -1;
				if (nState == 0 || nState == 3)
					return;

				Ra[nRa].nState = 3;
				Ra[nRa].nSeq = 0;
				return;
			}
			if (sprite[nSprite].sectnum != sprite[nTarget].sectnum)
				engine.mychangespritesect((short) nSprite, sprite[nTarget].sectnum);
		}

		sprite[nSprite].x = sprite[nTarget].x;
		sprite[nSprite].y = sprite[nTarget].y;
		sprite[nSprite].z = sprite[nTarget].z - GetSpriteHeight(nTarget);
		if (sprite[nSprite].sectnum == sprite[nTarget].sectnum)
			return;

		engine.mychangespritesect((short) nSprite, sprite[nTarget].sectnum);
	}

}
