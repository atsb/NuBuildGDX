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
import static ru.m210projects.Powerslave.Bullet.*;
import static ru.m210projects.Powerslave.Globals.*;
import static ru.m210projects.Powerslave.Main.*;
import static ru.m210projects.Powerslave.Random.*;
import static ru.m210projects.Powerslave.Weapons.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static ru.m210projects.Powerslave.Light.*;
import static ru.m210projects.Powerslave.RunList.*;
import static ru.m210projects.Powerslave.Seq.*;
import static ru.m210projects.Powerslave.Anim.*;
import static ru.m210projects.Powerslave.Sound.*;
import static ru.m210projects.Powerslave.Sprites.*;

import ru.m210projects.Build.FileHandle.Resource;
import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Powerslave.Type.GrenadeStruct;
import ru.m210projects.Powerslave.Type.SafeLoader;

public class Grenade {
	
	public static final int MAX_GRENADES = 50;

	private static int nGrenadeCount;
	private static int nGrenadesFree;
	
	public static short GrenadeFree[] = new short[MAX_GRENADES];
	public static short[] nGrenadePlayer = new short[MAX_GRENADES];
	public static GrenadeStruct[] GrenadeList = new GrenadeStruct[MAX_GRENADES];

	public static void InitGrenades() {
		nGrenadeCount = 0;
		for (short i = 0; i < MAX_GRENADES; i++) {
			GrenadeFree[i] = i;
			if (GrenadeList[i] == null)
				GrenadeList[i] = new GrenadeStruct();
		}
		nGrenadesFree = MAX_GRENADES;
	}
	
	public static ByteBuffer saveGrenades()
	{
		ByteBuffer bb = ByteBuffer.allocate(4 + (4 + GrenadeStruct.size) * MAX_GRENADES);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		bb.putShort((short)nGrenadeCount);
		bb.putShort((short)nGrenadesFree);
		
		for(int i = 0; i < MAX_GRENADES; i++) {
			bb.putShort(GrenadeFree[i]);
			bb.putShort(nGrenadePlayer[i]);
			GrenadeList[i].save(bb);
		}

		return bb;
	}
	
	public static void loadGrenades(SafeLoader loader, Resource bb)
	{
		if(bb != null) {
			loader.nGrenadeCount = bb.readShort();
			loader.nGrenadesFree = bb.readShort();
			
			for(int i = 0; i < MAX_GRENADES; i++) {
				loader.GrenadeFree[i] = bb.readShort();
				loader.nGrenadePlayer[i] = bb.readShort();
				if(loader.GrenadeList[i] == null)
					loader.GrenadeList[i] = new GrenadeStruct();
				loader.GrenadeList[i].load(bb);
			}
		}
		else
		{
			nGrenadeCount = loader.nGrenadeCount;
			nGrenadesFree = loader.nGrenadesFree;
			System.arraycopy(loader.GrenadeFree, 0, GrenadeFree, 0, MAX_GRENADES);
			System.arraycopy(loader.nGrenadePlayer, 0, nGrenadePlayer, 0, MAX_GRENADES);

			for(int i = 0; i < MAX_GRENADES; i++) {
				if(GrenadeList[i] == null)
					GrenadeList[i] = new GrenadeStruct();
				GrenadeList[i].copy(loader.GrenadeList[i]);
			}
		}
	}

	public static int GrabGrenade() {
		return GrenadeFree[--nGrenadesFree];
	}

	public static void DestroyGrenade(int a1) {
		DoSubRunRec(GrenadeList[a1].field_6);
		SubRunRec(GrenadeList[a1].field_8);
		DoSubRunRec(sprite[GrenadeList[a1].nSprite].lotag - 1);
		engine.mydeletesprite(GrenadeList[a1].nSprite);
		GrenadeFree[nGrenadesFree] = (short) a1;
		nGrenadesFree++;
	}

	public static void BounceGrenade(int a1, int a2) {
		GrenadeList[a1].field_10 >>= 1;
		GrenadeList[a1].xvel = GrenadeList[a1].field_10 * (sintable[(a2 + 512) & 0x7FF] >> 5);
		GrenadeList[a1].yvel = GrenadeList[a1].field_10 * (sintable[a2 & 0x7FF] >> 5);
		D3PlayFX(StaticSound[3], GrenadeList[a1].nSprite);
	}

	public static void ThrowGrenade(int a1, int a2, int a3, int a4, int a5) {
		int v7 = nPlayerGrenade[a1];
		if (v7 >= 0) {
			short nPlayer = PlayerList[a1].spriteId;
			short nGrenade = GrenadeList[v7].nSprite;
			engine.mychangespritesect(nGrenade, nPlayerViewSect[a1]);

			sprite[nGrenade].x = sprite[nPlayer].x;
			sprite[nGrenade].y = sprite[nPlayer].y;
			sprite[nGrenade].z = sprite[nPlayer].z;
			sprite[nGrenade].ang = sprite[nPlayer].ang;

			sprite[nGrenade].cstat &= 0x7FF;

			if (a5 >= -3000) {
				GrenadeList[v7].field_10 = 32 * ((cfg.bGrenadeFix && !isOriginal()) ? (90 - GrenadeList[v7].field_E) : totalvel[a1])
						+ (90 - GrenadeList[v7].field_E) * (90 - GrenadeList[v7].field_E);
	
				sprite[nGrenade].zvel = (short) (-64 * a5 - 4352);
				int dist = 8 * sprite[nPlayer].clipdist;
				int hitMove = engine.movesprite(nGrenade, dist * sintable[(sprite[nPlayer].ang + 512) & 0x7FF],
						sintable[sprite[nPlayer].ang & 0x7FF] * dist, a4, 0, 0, 1);
				if ((hitMove & 0x8000) != 0) {
					BounceGrenade(v7, engine.GetWallNormal(hitMove & 0x3FFF));
				}
			} else {
				GrenadeList[v7].field_10 = 0;
				sprite[nGrenade].zvel = sprite[nPlayer].zvel;
			}

			GrenadeList[v7].xvel = GrenadeList[v7].field_10 * (sintable[(sprite[nPlayer].ang + 512) & 0x7FF] >> 4);
			GrenadeList[v7].yvel = GrenadeList[v7].field_10 * (sintable[sprite[nPlayer].ang & 0x7FF] >> 4);
			nPlayerGrenade[a1] = -1;
		}
	}

	public static int BuildGrenade(int a1) {
		if (nGrenadesFree != 0) {
			int nGrenade = GrabGrenade();
			short nPlayer = PlayerList[a1].spriteId;
			int spr = engine.insertsprite(nPlayerViewSect[a1], (short) 201);

			if (spr < 0 || spr >= MAXSPRITES) {
				game.ThrowError("spr>=0 && spr<MAXSPRITES");
				return -1;
			}

			SPRITE pSprite = sprite[spr];
			pSprite.x = sprite[nPlayer].x;
			pSprite.y = sprite[nPlayer].y;
			pSprite.z = sprite[nPlayer].z - 3840;
			pSprite.shade = -64;
			pSprite.xrepeat = 20;
			pSprite.yrepeat = 20;
			pSprite.cstat = -32768;
			pSprite.picnum = 1;
			pSprite.pal = 0;
			pSprite.clipdist = 30;
			pSprite.xoffset = 0;
			pSprite.yoffset = 0;
			pSprite.ang = sprite[nPlayer].ang;
			pSprite.yvel = 0;
			pSprite.owner = nPlayer;
			pSprite.xvel = 0;
			pSprite.zvel = 0;
			pSprite.hitag = 0;
			pSprite.lotag = (short) (HeadRun() + 1);
			pSprite.extra = -1;

			GrenadeList[nGrenade].field_E = 90;
			GrenadeList[nGrenade].field_2 = 0;
			GrenadeList[nGrenade].field_0 = 16;
			GrenadeList[nGrenade].field_10 = -1;
			GrenadeList[nGrenade].nSprite = (short) spr;
			GrenadeList[nGrenade].ActionSeq = 0;
			GrenadeList[nGrenade].field_C = 0;
			GrenadeList[nGrenade].field_6 = (short) AddRunRec(pSprite.lotag - 1, 0xF0000 | nGrenade);
			GrenadeList[nGrenade].field_8 = (short) AddRunRec(NewRun, 0xF0000 | nGrenade);
			nGrenadePlayer[nGrenade] = (short) a1;
			nPlayerGrenade[a1] = (short) nGrenade;
		}

		return -1;
	}

	public static void ExplodeGrenade(int a1) {
		int nPlayer = nGrenadePlayer[a1];
		short spr = GrenadeList[a1].nSprite;
		GrenadeList[a1].field_C = 1;
		SPRITE pSprite = sprite[spr];

		int v16, v18;
		short sectnum = pSprite.sectnum;
		if ((SectFlag[sectnum] & 0x2000) != 0) {
			v16 = 75;
			v18 = 60;
		} else if (pSprite.z >= sector[sectnum].floorz) {
//			MonoOut(v14, v15); "GRENBOOM\n"
			v16 = 34;
			v18 = 150;
		} else {
//			MonoOut(v14, 36); "GRENPOW\n"
			v16 = 36;
			v18 = 200;
		}

		if (GrenadeList[a1].field_10 < 0) {
			int v9 = PlayerList[nPlayer].spriteId;
			pSprite.z = sprite[v9].z;
			pSprite.x = sprite[v9].x + (sintable[(sprite[v9].ang + 512) & 0x7FF] >> 5);
			pSprite.y = sprite[v9].y + (sintable[sprite[v9].ang & 0x7FF] >> 5);
			engine.changespritesect(spr, sprite[v9].sectnum);
			if (PlayerList[nPlayer].invisibility == 0)
				PlayerList[nPlayer].HealthAmount = 1;
		}

		int force = BulletInfo[4].force;
		if (nPlayerDouble[nPlayer] > 0)
			force = 2 * BulletInfo[4].force;
		RadialDamageEnemy(spr, force, BulletInfo[4].inf_10);
		BuildAnim(-1, v16, 0, pSprite.x, pSprite.y, pSprite.z, pSprite.sectnum, v18, 4);
		AddFlash(pSprite.sectnum, pSprite.x, pSprite.y, pSprite.z, 128);
		nGrenadePlayer[a1] = -1;
		DestroyGrenade(a1);
	}
	

	public static void FuncGrenade(int a1, int a2, int RunPtr) {
		short nGrenade = (short) (RunData[RunPtr].RunEvent & 0xFFFF);

		if (nGrenade < 0 || nGrenade >= MAX_GRENADES) {
			game.ThrowError("nGrenade>=0 && nGrenade<MAX_GRENADES");
			return;
		}

		int nSeq;
		short nSprite = GrenadeList[nGrenade].nSprite;
		if (GrenadeList[nGrenade].field_C != 0)
			nSeq = SeqOffsets[34];
		else
			nSeq = GrenadeList[nGrenade].ActionSeq + SeqOffsets[33];

		switch (a1 & 0x7F0000) {
		case nEventProcess:
			int v10 = GrenadeList[nGrenade].field_2 >> 8;
			MoveSequence(nSprite, nSeq, v10);
			sprite[nSprite].picnum = (short) GetSeqPicnum2(nSeq, v10);
			GrenadeList[nGrenade].field_E--;
			if (GrenadeList[nGrenade].field_E == 0) {
				int nPlayer = nGrenadePlayer[nGrenade];
				if (GrenadeList[nGrenade].field_10 < 0) {
					PlayerList[nPlayer].weaponState = 0;
					PlayerList[nPlayer].seqOffset = 0;
					if (PlayerList[nPlayer].AmmosAmount[4] != 0) {
						PlayerList[nPlayer].weaponFire = 0;
					} else {
						SelectNewWeapon(nPlayer);
						PlayerList[nPlayer].currentWeapon = PlayerList[nPlayer].newWeapon;
						PlayerList[nPlayer].newWeapon = -1;
					}
				}
				ExplodeGrenade(nGrenade);
				return;
			}
			if (GrenadeList[nGrenade].field_10 < 0)
				return;

			GrenadeList[nGrenade].field_2 += GrenadeList[nGrenade].field_0;
			int v19 = GrenadeList[nGrenade].field_2 >> 8;
			if (v19 < 0) {
				GrenadeList[nGrenade].field_2 += (SeqSize[nSeq] << 8);
			} else if (v19 >= SeqSize[nSeq]) {
				if (GrenadeList[nGrenade].field_C != 0) {
					DestroyGrenade(nGrenade);
					return;
				}
				GrenadeList[nGrenade].field_2 = 0;
			}

			if (GrenadeList[nGrenade].field_C != 0)
				return;
			
			int zvel = sprite[nSprite].zvel;
			
			Gravity(nSprite);

			int hitMove = engine.movesprite(nSprite, GrenadeList[nGrenade].xvel, GrenadeList[nGrenade].yvel, sprite[nSprite].zvel,
					sprite[nSprite].clipdist >> 1, sprite[nSprite].clipdist >> 1, 1);
			
			if (hitMove == 0)
				return;

			if ((hitMove & 0x20000) != 0) {
				if (zvel != 0) {
					if (SectDamage[sprite[nSprite].sectnum] > 0) {
						ExplodeGrenade(nGrenade);
						return;
					}
					GrenadeList[nGrenade].field_0 = (short) totalmoves;
					D3PlayFX(StaticSound[3], nSprite);
					sprite[nSprite].zvel = (short) -(zvel >> 1);
					if (sprite[nSprite].zvel > -1280) {
						D3PlayFX(StaticSound[5], nSprite);
						GrenadeList[nGrenade].field_0 = 0;
						GrenadeList[nGrenade].field_2 = 0;
						sprite[nSprite].zvel = 0;
						GrenadeList[nGrenade].ActionSeq = 1;
					}
				}
			      GrenadeList[nGrenade].field_0 = (short) (255 - 2 * RandomByte());
			      GrenadeList[nGrenade].xvel = GrenadeList[nGrenade].xvel - (GrenadeList[nGrenade].xvel >> 4);
			      GrenadeList[nGrenade].yvel = GrenadeList[nGrenade].yvel - (GrenadeList[nGrenade].yvel >> 4);
			}
			
			switch(hitMove & 0xC000)
			{
				case 0x8000:
					BounceGrenade(nGrenade, engine.GetWallNormal(hitMove & 0x3FFF));
					GrenadeList[nGrenade].field_2 = 0;
				    return;
				case 0xC000:
					BounceGrenade(nGrenade, sprite[hitMove & 0x3FFF].ang);
					GrenadeList[nGrenade].field_2 = 0;
				    return;
			}
			return;
		case nEventView:
			PlotSequence((short) (a1 & 0xFFFF), nSeq, GrenadeList[nGrenade].field_2 >> 8, 1);
			return;
		case nEventRadialDamage:
			if (nSprite != nRadialSpr && GrenadeList[nGrenade].field_C == 0) {
				if (CheckRadialDamage(nSprite) > 280) {
					GrenadeList[nGrenade].field_E = (short) (RandomSize(4) + 1);
				}
			}
			return;
		}
	}
}
