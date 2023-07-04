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

import static ru.m210projects.Build.Net.Mmulti.numplayers;
import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Powerslave.Main.*;
import static ru.m210projects.Powerslave.Player.*;
import static ru.m210projects.Powerslave.Globals.*;
import static ru.m210projects.Powerslave.Seq.*;
import static ru.m210projects.Powerslave.Anim.*;
import static ru.m210projects.Powerslave.Bullet.BuildBullet;
import static ru.m210projects.Powerslave.Random.*;
import static ru.m210projects.Powerslave.RunList.*;
import static ru.m210projects.Powerslave.Sound.*;

import ru.m210projects.Build.FileHandle.Resource;
import ru.m210projects.Build.Types.SECTOR;
import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Powerslave.Type.BobStruct;
import ru.m210projects.Powerslave.Type.Channel;
import ru.m210projects.Powerslave.Type.DripStruct;
import ru.m210projects.Powerslave.Type.ElevStruct;
import ru.m210projects.Powerslave.Type.ObjectStruct;
import ru.m210projects.Powerslave.Type.SafeLoader;
import ru.m210projects.Powerslave.Type.TrailPointStruct;
import ru.m210projects.Powerslave.Type.TrailStruct;
import ru.m210projects.Powerslave.Type.TrapStruct;
import ru.m210projects.Powerslave.Type.WallFaceStruct;

import static ru.m210projects.Powerslave.Sprites.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static ru.m210projects.Powerslave.Map.*;
import static ru.m210projects.Powerslave.Light.*;

public class Object {

	public static int ElevCount;
	public static ElevStruct Elevator[] = new ElevStruct[1024];

	public static int WallFaceCount;
	public static WallFaceStruct WallFace[] = new WallFaceStruct[4096];

	public static void InitElev() {
		ElevCount = 1024;
		for (int i = 0; i < 1024; i++)
			Elevator[i] = new ElevStruct();
	}
	
	public static ByteBuffer saveElevs()
	{
		ByteBuffer bb = ByteBuffer.allocate((1024 - ElevCount) * ElevStruct.size + 2);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		
		bb.putShort((short)ElevCount);
		for(int i = ElevCount; i < 1024; i++)
			Elevator[i].save(bb);

		return bb;
	}
	
	public static void loadElevs(SafeLoader loader, Resource bb)
	{
		if(bb != null) {
			loader.ElevCount = bb.readShort();
			for(int i = loader.ElevCount; i < 1024; i++) {
				if(loader.Elevator[i] == null)
					loader.Elevator[i] = new ElevStruct();
				loader.Elevator[i].load(bb);
			}
		}
		else
		{
			ElevCount = loader.ElevCount;
			for(int i = loader.ElevCount; i < 1024; i++) {
				if(Elevator[i] == null)
					Elevator[i] = new ElevStruct();
				Elevator[i].copy(loader.Elevator[i]);
			}
		}
	}
	
	public static int BuildElevC(int a1, int a2, int a3, int a4, int a5, int a6, int a7, int... a8) {
		if (ElevCount <= 0) {
			game.ThrowError("ElevCount>0");
			return -1;
		}

		if (ElevCount <= 0)
			return -1;

		int i = --ElevCount;
		Elevator[i].field_0 = (short) a1;
		if ((a1 & 4) != 0)
			a5 /= 2;

		Elevator[i].field_6 = a5;
		Elevator[i].field_E = 0;
		Elevator[i].field_36 = 0;
		Elevator[i].field_10 = 0;
		Elevator[i].field_A = a6;
		Elevator[i].field_32 = -1;
		Elevator[i].channel = (short) a2;
		Elevator[i].sectnum = (short) a3;

		if (a4 < 0)
			a4 = BuildWallSprite(a3);

		Elevator[i].field_34 = (short) a4;
		for (int j = 0; j < a7; j++) {
			Elevator[i].field_12[Elevator[i].field_E] = a8[j];
			Elevator[i].field_E++;
		}

		return i;
	}

	public static int BuildElevF(int a1, int a2, int a3, int a4, int a5, int a6, int... a7) {
		if (ElevCount <= 0) {
			game.ThrowError("ElevCount>0");
			return -1;
		}

		if (ElevCount <= 0)
			return -1;

		int i = --ElevCount;
		if ((a1 & 4) != 0)
			a5 /= 2;

		Elevator[i].field_0 = 2;
		Elevator[i].field_6 = a4;
		Elevator[i].field_32 = -1;
		Elevator[i].field_A = a5;
		Elevator[i].channel = (short) a1;
		Elevator[i].sectnum = (short) a2;
		Elevator[i].field_E = 0;
		Elevator[i].field_10 = 0;
		Elevator[i].field_36 = 0;

		if (a3 < 0)
			a3 = BuildWallSprite(a2);

		Elevator[i].field_34 = (short) a3;
		for (int j = 0; j < a6; j++) {
			Elevator[i].field_12[Elevator[i].field_E] = a7[j];
			Elevator[i].field_E++;
		}

		return i;
	}

	public static void FuncElev(int a1, int a2, int RunPtr) {
		short elev = (short) (RunData[RunPtr].RunEvent & 0xFFFF);
		if (elev < 0 || elev >= 1024) {
			game.ThrowError("elev>=0 && elev<MAXELEV");
			return;
		}
		
		int nRun = a1 & 0x7F0000;
		if(nRun != 0x20000 && nRun != 0x10000) 
			return;

		int chan = Elevator[elev].channel;
		if (chan < 0 || chan >= 4096) {
			game.ThrowError("chan>=0 && chan<MAXCHAN");
			return;
		}

		Channel pChannel = channel[chan];
		int nFlags = Elevator[elev].field_0;

		switch (nRun) {
		case 0x20000:
			if ((nFlags & 2) != 0) {
				game.pInt.setfloorinterpolate(Elevator[elev].sectnum, sector[Elevator[elev].sectnum]);
				int v18 = LongSeek(sector[Elevator[elev].sectnum].floorz,
						Elevator[elev].field_12[Elevator[elev].field_10], Elevator[elev].field_6,
						Elevator[elev].field_A);
				sector[Elevator[elev].sectnum].floorz = longSeek_out;
				if (v18 == 0) {
					if ((nFlags & 0x10) != 0) {
						Elevator[elev].field_10 ^= 1;
						StartElevSound(Elevator[elev].field_34, nFlags);
					} else {
						StopSpriteSound(Elevator[elev].field_34);
						SubRunRec(RunPtr);
						Elevator[elev].field_32 = -1;
						ReadyChannel(chan);
						D3PlayFX(StaticSound[nStopSound], Elevator[elev].field_34);
					}

					int v16 = Elevator[elev].field_34;
					while (v16 != -1) {
						int v29 = v16;
						v16 = sprite[v29].owner;
						game.pInt.setsprinterpolate(v29, sprite[v29]);
						sprite[v29].z += v18;
					}
					return;
				}
				MoveSectorSprites(Elevator[elev].sectnum, v18);
				if (v18 >= 0 || !CheckSectorSprites(Elevator[elev].sectnum, 2)) {

					int v16 = Elevator[elev].field_34;
					while (v16 != -1) {
						int v29 = v16;
						v16 = sprite[v29].owner;
						game.pInt.setsprinterpolate(v29, sprite[v29]);
						sprite[v29].z += v18;
					}
					return;
				}

				ChangeChannel(chan, channel[chan].field_4 == 0 ? 1 : 0);
				return;
			}

			SECTOR v34 = sector[Elevator[elev].sectnum];
			game.pInt.setceilinterpolate(Elevator[elev].sectnum, sector[Elevator[elev].sectnum]);
			int v30 = sector[Elevator[elev].sectnum].ceilingz;
			int v27 = LongSeek(v30, Elevator[elev].field_12[Elevator[elev].field_10], Elevator[elev].field_6,
					Elevator[elev].field_A);
			v30 = longSeek_out;

			if (v27 != 0) {
				if (v27 > 0) {
					if (v30 == Elevator[elev].field_12[Elevator[elev].field_10]) {
						if ((nFlags & 4) != 0)
							SetQuake(Elevator[elev].field_34, 30);
						PlayFXAtXYZ(StaticSound[26], sprite[Elevator[elev].field_34].x,
								sprite[Elevator[elev].field_34].y, sprite[Elevator[elev].field_34].z,
								sprite[Elevator[elev].field_34].sectnum);
					}
					if ((nFlags & 4) != 0) {
						if (CheckSectorSprites(Elevator[elev].sectnum, 1))
							return;
					} else if (CheckSectorSprites(Elevator[elev].sectnum, 0)) {
						ChangeChannel(chan, channel[chan].field_4 == 0 ? 1 : 0);
						return;
					}
				}
				v34.ceilingz = v30;
				int v16 = Elevator[elev].field_34;
				while (v16 != -1) {
					int v29 = v16;
					v16 = sprite[v29].owner;
					game.pInt.setsprinterpolate(v29, sprite[v29]);
					sprite[v29].z += v27;
				}
				return;
			}

			if ((nFlags & 0x10) == 0) {
				SubRunRec(RunPtr);
				Elevator[elev].field_32 = -1;
				StopSpriteSound(Elevator[elev].field_34);
				D3PlayFX(StaticSound[nStopSound], Elevator[elev].field_34);
				ReadyChannel(chan);
				return;
			}
			Elevator[elev].field_10 ^= 1;
			StartElevSound(Elevator[elev].field_34, nFlags);
			return;
		case 0x10000:
			int a4 = 0;
			if ((nFlags & 8) == 0) {
				if ((nFlags & 0x10) != 0) {
					if (Elevator[elev].field_32 < 0) {
						Elevator[elev].field_32 = (short) AddRunRec(NewRun, RunData[RunPtr].RunEvent);
						a4 = 1;
						StartElevSound(Elevator[elev].field_34, nFlags);
					}
					if (a4 != 0) {
						if (Elevator[elev].field_32 >= 0)
							return;
						Elevator[elev].field_32 = (short) AddRunRec(NewRun, RunData[RunPtr].RunEvent);
						StartElevSound(Elevator[elev].field_34, nFlags);
						return;
					}

					if (Elevator[elev].field_32 >= 0) {
						SubRunRec(Elevator[elev].field_32);
						Elevator[elev].field_32 = -1;
					}
					return;
				}

				if (pChannel.field_4 >= 0) {
					if (pChannel.field_4 == Elevator[elev].field_10 || pChannel.field_4 >= Elevator[elev].field_E) {
						Elevator[elev].field_36 = pChannel.field_4;
					} else {
						Elevator[elev].field_10 = channel[chan].field_4;
					}
					if (Elevator[elev].field_32 >= 0)
						return;
					Elevator[elev].field_32 = (short) AddRunRec(NewRun, RunData[RunPtr].RunEvent);
					StartElevSound(Elevator[elev].field_34, nFlags);
					return;
				}

				if (Elevator[elev].field_32 >= 0) {
					SubRunRec(Elevator[elev].field_32);
					Elevator[elev].field_32 = -1;
				}
				return;
			}

			if (pChannel.field_4 == 0) {
				if (Elevator[elev].field_32 >= 0) {
					SubRunRec(Elevator[elev].field_32);
					Elevator[elev].field_32 = -1;
				}
				return;
			}

			if (Elevator[elev].field_32 >= 0)
				return;

			Elevator[elev].field_32 = (short) AddRunRec(NewRun, RunData[RunPtr].RunEvent);
			StartElevSound(Elevator[elev].field_34, nFlags);
			return;
		}
	}

	public static void BuildObject(int a1, int a2, int a3) {
		int v3 = a1;
		int v4 = a2;
		int v24 = a3;
		int v20 = ObjectCount;
		if (ObjectCount >= 128) {
			game.ThrowError("Too many objects!");
			return;
		}
		
		if (ObjectList[v20] == null)
			ObjectList[v20] = new ObjectStruct();

		int v5 = ObjectStatnum[a2];
		ObjectCount++;
		int v6 = a1;
		engine.changespritestat((short) a1, (short) v5);

		sprite[v6].cstat = (short) ((sprite[v6].cstat | 0x101) & (~(0x8000 | 2)));
		sprite[v6].xvel = 0;
		sprite[v6].yvel = 0;
		sprite[v6].zvel = 0;
		sprite[v6].extra = -1;
		sprite[v6].lotag = (short) (HeadRun() + 1);
		int v8 = v20;
		int v9 = sprite[v6].lotag;
		sprite[v6].hitag = 0;
		sprite[v6].owner = (short) AddRunRec(v9 - 1, 0x170000 | v20);

		int v12 = v8;
		if (sprite[v6].statnum == 97)
			ObjectList[v12].obj_2 = 4;
		else
			ObjectList[v12].obj_2 = 120;
		int v13 = v20;
		int v14 = NewRun;
		ObjectList[v13].field_6 = (short) v3;
		ObjectList[v13].field_4 = (short) AddRunRec(v14, 0x170000 | v20);
		int v15 = ObjectSeq[v4];
		if (v15 <= -1) {
			ObjectList[v13].field_0 = 0;
			int v23 = sprite[v3].statnum;
			ObjectList[v13].field_8 = -1;
			if (v23 == 97)
				ObjectList[v13].obj_A = -1;
			else
				ObjectList[v13].obj_A = (short) -v24;
		} else {
			int v16 = SeqOffsets[v15];
			ObjectList[v13].field_8 = (short) v16;
			if (v4 == 0) {
				int v17 = SeqSize[v16] - 1;
				int v18 = RandomSize(4);
				ObjectList[v13].field_0 = (short) (v18 % v17);
			}
			int v19 = v3;
			int v21 = engine.insertsprite(sprite[v19].sectnum, (short) 0);
			ObjectList[v20].obj_A = (short) v21;
			sprite[v21].cstat = (short) 32768;
			sprite[v21].x = sprite[v19].x;
			sprite[v21].y = sprite[v19].y;
			sprite[v21].z = sprite[v19].z;
		}
	}
	
	public static ByteBuffer saveObjects()
	{
		ByteBuffer bb = ByteBuffer.allocate(ObjectCount * ObjectStruct.size + 2);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		
		bb.putShort((short)ObjectCount);
		for (short i = 0; i < ObjectCount; i++) {
			ObjectList[i].save(bb);
		}
		
		return bb;
	}
	
	public static void loadObjects(SafeLoader loader, Resource bb)
	{
		if(bb != null)
		{
			loader.ObjectCount = bb.readShort();
			for (short i = 0; i < loader.ObjectCount; i++) {
				if(loader.ObjectList[i] == null)
					loader.ObjectList[i] = new ObjectStruct();
				loader.ObjectList[i].load(bb);
			}
		}
		else
		{
			ObjectCount = loader.ObjectCount;
			for (short i = 0; i < loader.ObjectCount; i++) {
				if(ObjectList[i] == null)
					ObjectList[i] = new ObjectStruct();
				ObjectList[i].copy(loader.ObjectList[i]);
			}
		}
	}

	public static void FuncObject(int a1, int a2, int a3) {
		ObjectStruct v5 = ObjectList[RunData[a3].RunEvent & 0xFFFF];
		int v4 = v5.field_6;

		int v6 = v4;
		int v7 = v4;
		int v45 = v4;
		int v43 = sprite[v7].statnum;
		int v8 = (a1 & 0x7F0000);
		int v9 = v5.field_8;

		switch (v8) {
		case 0x20000:
			if (v43 != 97 && (sprite[v7].cstat & 0x101) != 0) {
				if (v43 != 152)
					Gravity(v6);
				if (v9 != -1) {
					int v10 = v5.field_0 + 1;
					v5.field_0 = (short) v10;
					if (v10 >= SeqSize[v9])
						v5.field_0 = 0;
					sprite[v45].picnum = (short) GetSeqPicnum2(v9, v5.field_0);
				}

				if (v5.obj_2 >= 0 || (v5.obj_2++ != -1)) {
					if (v43 != 152) {
						int v23 = v45;
						int v24 = engine.movesprite((short) v45, sprite[v23].xvel << 6, sprite[v23].yvel << 6,
								sprite[v23].zvel, 0, 0, 0);
						if (sprite[v23].statnum == 141)
							sprite[v23].pal = 1;
						if ((v24 & 0x20000) != 0) {
							sprite[v45].xvel -= sprite[v45].xvel >> 3;
							sprite[v45].yvel -= sprite[v45].yvel >> 3;
						}
						if ((v24 & 0xC000) == 49152) {
							sprite[v45].yvel = 0;
							sprite[v45].xvel = 0;
						}
					}
				} else {
					int v44;
					if (v43 != 152 && sprite[v45].z >= sector[sprite[v45].sectnum].floorz)
						v44 = 34;
					else
						v44 = 36;
					int v13 = v45;
					AddFlash(sprite[v13].sectnum, sprite[v13].x, sprite[v13].y, sprite[v13].z, 128);
					BuildAnim(-1, v44, 0, sprite[v13].x, sprite[v13].y, sector[sprite[v13].sectnum].floorz,
							sprite[v13].sectnum, 0xF0, 4);
					int v14 = v45 | 0x4000;
					if (v43 == 141) {
						for (int i = 4; i < 8; i++)
							BuildCreatureChunk(v14, GetSeqPicnum(46, (i >> 2) + 1, 0));
						RadialDamageEnemy(v45, 200, 20);
					} else if (v43 == 152) {
						for (int i = 0; i < 8; i++)
							BuildCreatureChunk(v14, GetSeqPicnum(46, (i >> 1) + 3, 0));
					}
					if (levelnum <= 20 || v43 != 141) {
						SubRunRec(sprite[v45].owner);
						SubRunRec(v5.field_4);
						engine.mydeletesprite((short) v45);
					} else {
						StartRegenerate(v45);
						int v19 = v5.obj_A;
						v5.obj_2 = 120;
						sprite[v45].x = sprite[v19].x;
						sprite[v45].y = sprite[v19].y;
						sprite[v45].z = sprite[v19].z;
						engine.mychangespritesect((short) v45, sprite[v19].sectnum);
					}
				}
			}
			return;
		case 0x80000:
			if (v43 >= 150)
				return;
			if (v5.obj_2 <= 0)
				return;
			if (v43 == 98) {
				D3PlayFX((RandomSize(2) << 9) | StaticSound[47] | 0x2000, v5.field_6);
				return;
			}
			v5.obj_2 -= a2;
			if (v5.obj_2 > 0)
				return;

			if (v43 == 97) {
				ExplodeScreen(v6);
				return;
			}

			v5.obj_2 = (short) -(RandomSize(3) + 1);
			return;
		case 0x90000:
			if (v9 > -1)
				PlotSequence(a1 & 0xFFFF, v9, v5.field_0, 1);
			return;
		case 0xA0000:
			if (v5.obj_2 > 0 && (sprite[v7].cstat & 0x101) != 0 && (v43 != 152 || sprite[nRadialSpr].statnum == 201
					|| nRadialBullet != 3 && nRadialBullet > -1 || sprite[nRadialSpr].statnum == 141)) {
				int v28 = CheckRadialDamage(v45);
				if (v28 > 0) {
					if (sprite[v45].statnum != 98)
						v5.obj_2 -= v28;
					int v30 = v45;
					int v31 = sprite[v30].statnum;
					if (v31 == 152) {
						sprite[v30].zvel = 0;
						sprite[v30].yvel = 0;
						sprite[v30].xvel = 0;
					} else if (v31 != 98) {
						sprite[v30].xvel >>= 1;
						sprite[v30].yvel >>= 1;
						sprite[v30].zvel >>= 1;
					}
					if (v5.obj_2 <= 0) {
						int v34 = v45;
						int v35 = sprite[v45].statnum;
						if (v35 == 152) {
							int v36 = v5.obj_A;
							v5.obj_2 = -1;
							if (v36 >= 0) {
								if (ObjectList[v36].obj_2 > 0)
									ObjectList[v36].obj_2 = -1;
							}
							return;
						}
						if (v35 == 97) {
							v5.obj_2 = 0;
							ExplodeScreen(v34);
							return;
						}
						v5.obj_2 = (short) -(RandomSize(4) + 1);
					}
				}
			}
			return;
		}
	}

	public static void StartRegenerate(int a1) {
		int v2 = -1;
		SPRITE v4 = sprite[a1];
		int v5 = nFirstRegenerate;

		int v3 = 0;
		while (true) {
			if (v3 >= nRegenerates) {
				v4.xvel = v4.xrepeat;
				v4.zvel = v4.shade;
				v4.yvel = v4.pal;
				break;
			}

			if (v5 == a1) {
				if (v2 == -1)
					nFirstRegenerate = v4.ang;
				else
					sprite[v2].ang = sprite[a1].ang;
				--nRegenerates;
				break;
			}

			v2 = v5;
			v5 = sprite[v5].ang;
			v3++;
		}

		v4.extra = 1350;
		v4.ang = (short) nFirstRegenerate;
		if (levelnum <= 20)
			v4.extra /= 5;
		v4.cstat = -32768;
		v4.xrepeat = 1;
		v4.yrepeat = 1;
		v4.pal = 1;

		nFirstRegenerate = a1;
		nRegenerates++;
	}

	public static void DoRegenerates() {
		int spr = nFirstRegenerate;
		SPRITE pSprite;
		for (int i = nRegenerates; i > 0; i--, spr = pSprite.ang) {
			pSprite = sprite[spr];
			if (pSprite.extra <= 0) {
				if (pSprite.xrepeat < pSprite.xvel) {
					pSprite.xrepeat += 2;
					pSprite.yrepeat += 2;
					continue;
				}
			} else {
				pSprite.extra--;
				if (pSprite.extra > 0)
					continue;
				BuildAnim(-1, 38, 0, pSprite.x, pSprite.y, pSprite.z, pSprite.sectnum, 0x40, 4);
				D3PlayFX(StaticSound[12], spr);
			}

			pSprite.xrepeat = pSprite.xvel;
			pSprite.yrepeat = pSprite.xvel;

			pSprite.pal = pSprite.yvel;
			pSprite.xvel = 0;
			pSprite.yvel = 0;
			pSprite.zvel = 0;

			nRegenerates--;
			if (pSprite.statnum == 141)
				pSprite.cstat = 257;
			else
				pSprite.cstat = 0;
			if (nRegenerates == 0)
				nFirstRegenerate = -1;
		}
	}

	private static void MoveSectorSprites(short a1, int a2) {
		for (int i = headspritesect[a1]; i != -1; i = nextspritesect[i]) {
			if (sprite[i].statnum != 200) {
				game.pInt.setsprinterpolate(i, sprite[i]);
				sprite[i].z += a2;
			}
		}
	}

	private static boolean CheckSectorSprites(short a1, int a2) {
		int v9 = a2;
		boolean v10 = false;
		if (a2 != 0) {
			int v4 = headspritesect[a1];
			int v5 = sector[a1].floorz - sector[a1].ceilingz;
			while (v4 != -1) {
				if ((sprite[v4].cstat & 0x101) != 0 && v5 < GetSpriteHeight(v4)) {
					if (v9 != 1)
						return true;
					v10 = true;
					DamageEnemy(v4, -1, 5);
					if (sprite[v4].statnum == 100 && PlayerList[GetPlayerFromSprite(v4)].HealthAmount <= 0) {
						int v8 = sprite[v4].sectnum | 0x4000;

						PlayFXAtXYZ(StaticSound[60], sprite[v4].x, sprite[v4].y, sprite[v4].z, v8);
					}
				}
				v4 = nextspritesect[v4];
			}
			return v10;
		} else {
			for (int i = headspritesect[a1]; i != -1; i = nextspritesect[i]) {
				if ((sprite[i].cstat & 0x101) != 0)
					return true;
			}
		}
		return false;
	}

	public static void SetQuake(int a1, int a2) {
		int v2 = a1;
		int v3 = 0;
		int v4 = 0;
		int v5 = a2 << 8;
		int v11 = sprite[v2].x;
		int v10 = sprite[v2].y;
		int v6 = 0;
		while (v3 < numplayers) {
			int v7 = PlayerList[v4].spriteId;
			int v8 = engine.ksqrt(((sprite[v7].x - v11) >> 8) * ((sprite[v7].x - v11) >> 8)
					+ ((sprite[v7].y - v10) >> 8) * ((sprite[v7].y - v10) >> 8));
			int v9 = v5;
			if (v8 != 0) {
				v9 = v5 / v8;
				if (v5 / v8 >= 256) {
					if (v9 > 3840)
						v9 = 3840;
				} else {
					v9 = 0;
				}
			}
			if (v9 > nQuake[v6])
				nQuake[v6] = (short) v9;
			++v4;
			++v6;
			++v3;
		}
	}

	public static int longSeek_out;

	public static int LongSeek(int a1, int a2, int a3, int a4) {
		longSeek_out = a1;
		int v4 = a2 - longSeek_out;
		if (v4 < 0) {
			int v5 = -a3;
			if (v5 > v4)
				v4 = v5;
			longSeek_out += v4;
		}
		if (v4 > 0) {
			if (a4 < v4)
				v4 = a4;
			longSeek_out += v4;
		}
		return v4;
	}

	public static int BuildWallSprite(int a1) {
		int nWall = sector[a1].wallptr;
		int nPoint2 = sector[a1].wallptr + 1;

		int i = engine.insertsprite((short) a1, (short) 401);
		sprite[i].x = (wall[nWall].x + wall[nPoint2].x) / 2;
		sprite[i].y = (wall[nWall].y + wall[nPoint2].y) / 2;
		sprite[i].z = (sector[a1].ceilingz + sector[a1].floorz) / 2;
		sprite[i].cstat = (short) 32768;

		return i;
	}

	public static void InitWallFace() {
		WallFaceCount = 4096;

		for (int i = 0; i < 4096; i++)
			WallFace[i] = new WallFaceStruct();
	}
	
	public static ByteBuffer saveWallFaces()
	{
		ByteBuffer bb = ByteBuffer.allocate((4096 - WallFaceCount) * WallFaceStruct.size + 2);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		
		bb.putShort((short)WallFaceCount);
		for(int i = WallFaceCount; i < 4096; i++)
			WallFace[i].save(bb);

		return bb;
	}
	
	public static void loadWallFaces(SafeLoader loader, Resource bb)
	{
		if(bb != null) {
			loader.WallFaceCount = bb.readShort();
			for(int i = loader.WallFaceCount; i < 4096; i++) {
				if(loader.WallFace[i] == null)
					loader.WallFace[i] = new WallFaceStruct();
				loader.WallFace[i].load(bb);
			}
		}
		else
		{
			WallFaceCount = loader.WallFaceCount;
			for(int i = loader.WallFaceCount; i < 4096; i++) {
				if(WallFace[i] == null)
					WallFace[i] = new WallFaceStruct();
				WallFace[i].copy(loader.WallFace[i]);
			}
		}
	}

	public static int BuildWallFace(int a1, short a2, int a3, int... a4) {
		if (WallFaceCount <= 0) {
			game.ThrowError("Too many wall faces!");
			return -1;
		}
		int i = --WallFaceCount;

		WallFace[i].field_4 = 0;
		WallFace[i].field_2 = a2;
		WallFace[i].field_0 = (short) a1;

		if (a3 > 8)
			a3 = 8;
		for (int j = 0; j < a3; j++) {
			WallFace[i].field_6[WallFace[i].field_4] = (short) a4[j];
			WallFace[i].field_4++;
		}

		return i | nEvent6;
	}

	public static void FuncWallFace(int a1, int a2, int a3) {
		short ws = (short) (RunData[a3].RunEvent & 0xFFFF);
		if (ws < 0 || ws >= 4096) {
			game.ThrowError("ws>=0 && ws<MAXWALLFACE");
			return;
		}

		if ((a1 & 0x7F0000) == 0x10000) {
			int v6 = channel[WallFace[ws].field_0].field_4;
			if (v6 <= WallFace[ws].field_4 && v6 >= 0) {
				wall[WallFace[ws].field_2].picnum = WallFace[ws].field_6[v6];
			}
		}
	}

	public static int FindTrail(int a1) {
		int v2 = 0;
		int v3 = 0;
		while (v2 < nTrails) {
			if (a1 == sTrail[v3].field_2)
				return v2;
			++v3;
			++v2;
		}
		sTrail[nTrails].field_2 = (short) a1;
		sTrail[nTrails].field_0 = -1;

		return nTrails++;
	}
	
	public static ByteBuffer saveTrails()
	{
		ByteBuffer bb = ByteBuffer.allocate(4 + (TrailStruct.size * nTrails) + nTrailPoints * (TrailPointStruct.size) + 600);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		
		bb.putShort((short)nTrails);
		bb.putShort((short)nTrailPoints);
		
		for(int i = 0; i < nTrails; i++)
			sTrail[i].save(bb);
		
		for(int i = 0; i < nTrailPoints; i++)
		{
			sTrailPoint[i].save(bb);
		}
		
		for(int i = 0; i < 100; i++)
		{
			bb.putShort(nTrailPointVal[i]);
			bb.putShort(nTrailPointNext[i]);
			bb.putShort(nTrailPointPrev[i]);
		}

		return bb;
	}
	
	public static void loadTrails(SafeLoader loader, Resource bb)
	{
		if(bb != null) {
			loader.nTrails = bb.readShort();
			loader.nTrailPoints = bb.readShort();
			for(int i = 0; i < loader.nTrails; i++) {
				if(loader.sTrail[i] == null)
					loader.sTrail[i] = new TrailStruct();
				loader.sTrail[i].load(bb);
			}
			
			for(int i = 0; i < loader.nTrailPoints; i++)
			{
				if(loader.sTrailPoint[i] == null)
					loader.sTrailPoint[i] = new TrailPointStruct();
				loader.sTrailPoint[i].load(bb);
			}
			
			for(int i = 0; i < 100; i++)
			{
				loader.nTrailPointVal[i] = bb.readShort();
				loader.nTrailPointNext[i] = bb.readShort();
				loader.nTrailPointPrev[i] = bb.readShort();
			}
		}
		else
		{
			nTrails = loader.nTrails;
			nTrailPoints = loader.nTrailPoints;
			for(int i = 0; i < loader.nTrails; i++) {
				if(sTrail[i] == null)
					sTrail[i] = new TrailStruct();
				sTrail[i].copy(loader.sTrail[i]);
			}
			
			for(int i = 0; i < loader.nTrailPoints; i++)
			{
				if(sTrailPoint[i] == null)
					sTrailPoint[i] = new TrailPointStruct();
				sTrailPoint[i].copy(loader.sTrailPoint[i]);
			}
			
			System.arraycopy(loader.nTrailPointVal, 0, nTrailPointVal, 0, nTrailPoints);
			System.arraycopy(loader.nTrailPointNext, 0, nTrailPointNext, 0, 100);
			System.arraycopy(loader.nTrailPointPrev, 0, nTrailPointPrev, 0, 100);
		}
	}

	public static int BuildSpark(int a1, int a2) {
		int i = engine.insertsprite(sprite[a1].sectnum, (short) 0);
		if (i != -1) {
			SPRITE pSprite = sprite[i];
			pSprite.x = sprite[a1].x;
			pSprite.y = sprite[a1].y;
			pSprite.cstat = 0;
			pSprite.shade = -127;
			pSprite.pal = 1;
			pSprite.xrepeat = 50;
			pSprite.xoffset = 0;
			pSprite.yoffset = 0;
			pSprite.yrepeat = 50;

			if (a2 >= 2) {
				pSprite.picnum = 3605;
				nSmokeSparks++;
				if (a2 == 3) {
					pSprite.xrepeat = pSprite.yrepeat = 120;
				} else {
					pSprite.xrepeat = pSprite.yrepeat = (short) (sprite[a1].xrepeat + 15);
				}
			} else {
				int v11 = (sprite[a1].ang + 256) - RandomSize(9);
				if (a2 != 0) {
					pSprite.xvel = (short) (sintable[(v11 + 512) & 0x7FF] >> 5);
					pSprite.yvel = (short) (sintable[v11 & 0x7FF] >> 5);
				} else {
					pSprite.xvel = (short) (sintable[(v11 + 512) & 0x7FF] >> 6);
					pSprite.yvel = (short) (sintable[v11 & 0x7FF] >> 6);
				}
				pSprite.zvel = (short) (-128 * RandomSize(4));
				pSprite.picnum = (short) (a2 + 985);
			}
			pSprite.z = sprite[a1].z;
			pSprite.lotag = (short) (HeadRun() + 1);
			pSprite.clipdist = 1;
			pSprite.hitag = 0;
			pSprite.extra = -1;
			pSprite.owner = (short) AddRunRec(pSprite.lotag - 1, 0x260000 | i);
			pSprite.hitag = (short) AddRunRec(NewRun, 0x260000 | i);
		}

		return i;
	}

	public static void FuncSpark(int a1, int a2, int RunPtr) {
		short spr = (short) (RunData[RunPtr].RunEvent & 0xFFFF);
		if (spr < 0 || spr >= MAXSPRITES) {
			game.ThrowError("spr>=0 && spr<MAXSPRITES");
			return;
		}

		if ((a1 & 0x7F0000) == 0x20000) {
			sprite[spr].shade += 3;
			sprite[spr].xrepeat -= 2;
			if (sprite[spr].xrepeat >= 4 && sprite[spr].shade < 100) {
				sprite[spr].yrepeat -= 2;

				if (sprite[spr].picnum == 986 && (sprite[spr].xrepeat & 2) != 0)
					BuildSpark(spr, 2);

				if (sprite[spr].picnum >= 3000)
					return;

				sprite[spr].zvel += 128;
				int result = engine.movesprite((short) spr, sprite[spr].xvel << 12, sprite[spr].yvel << 12,
						sprite[spr].zvel, 2560, -2560, 1);
				if (result == 0 || sprite[spr].zvel <= 0)
					return;
			}

			sprite[spr].zvel = 0;
			sprite[spr].yvel = 0;
			sprite[spr].xvel = 0;
			if (sprite[spr].picnum > 3000)
				--nSmokeSparks;

			DoSubRunRec(sprite[spr].owner);
			FreeRun(sprite[spr].lotag - 1);
			SubRunRec(sprite[spr].hitag);
			engine.mydeletesprite((short) spr);
		}
	}

	public static int BuildFireBall(int a1, int a2, int a3) {
		return BuildTrap(a1, 1, a2, a3);
	}

	public static int BuildArrow(int a1, int a2) {
		return BuildTrap(a1, 0, -1, a2);
	}
	
	public static ByteBuffer saveTraps()
	{
		ByteBuffer bb = ByteBuffer.allocate(nTraps * (TrapStruct.size) + 2 + 40 * 2);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		
		bb.putShort((short)nTraps);
		for (short i = 0; i < 40; i++)
			bb.putShort(nTrapInterval[i]);
		
		for (short i = 0; i < nTraps; i++) 
			sTrap[i].save(bb);

		return bb;
	}
	
	public static void loadTraps(SafeLoader loader, Resource bb)
	{
		if(bb != null)
		{
			loader.nTraps = bb.readShort();
			for (short i = 0; i < 40; i++)
				loader.nTrapInterval[i] = bb.readShort();
			
			for (short i = 0; i < loader.nTraps; i++) {
				if(loader.sTrap[i] == null)
					loader.sTrap[i] = new TrapStruct();
				loader.sTrap[i].load(bb);
			}
		}
		else
		{
			nTraps = loader.nTraps;
			System.arraycopy(loader.nTrapInterval, 0, nTrapInterval, 0, nTraps);
			for (short i = 0; i < loader.nTraps; i++) {
				if(sTrap[i] == null)
					sTrap[i] = new TrapStruct();
				sTrap[i].copy(loader.sTrap[i]);
			}
		}
	}

	public static int BuildTrap(int a1, int a2, int a3, int a4) {

		if (nTraps >= 40) {
			game.ThrowError("Too many traps!");
			return -1;
		}
		int v5 = nTraps++;

		engine.changespritestat((short) a1, (short) 0);

		sprite[a1].cstat = (short) 32768;
		sprite[a1].xvel = 0;
		sprite[a1].yvel = 0;
		sprite[a1].zvel = 0;
		sprite[a1].extra = -1;
		sprite[a1].lotag = (short) (HeadRun() + 1);
		sprite[a1].hitag = (short) AddRunRec(NewRun, 0x1F0000 | v5);
		sprite[a1].owner = (short) AddRunRec(sprite[a1].lotag - 1, 0x1F0000 | v5);

		if (sTrap[v5] == null)
			sTrap[v5] = new TrapStruct();
		
		sTrap[v5].field_2 = (short) a1;

		sTrap[v5].field_4 = (short) (((a2 == 0) ? 1 : 0) + 14);
		sTrap[v5].field_0 = -1;

		nTrapInterval[v5] = (short) (64 - 2 * a4);
		if (nTrapInterval[v5] < 5)
			nTrapInterval[v5] = 5;

		sTrap[v5].field_C = 0;
		sTrap[v5].field_A = 0;
		if (a3 != -1) {
			sTrap[v5].nWall = -1;
			sTrap[v5].nWall2 = -1;

			SECTOR v15 = sector[sprite[a1].sectnum];
			int v16 = 0;
			int v17 = v15.wallptr;
			while (v16 < v15.wallnum) {
				if (a3 == wall[v17].hitag) {
					if (sTrap[v5].nWall != -1) {
						sTrap[v5].nWall2 = (short) v17;
						sTrap[v5].field_C = wall[v17].picnum;
						return 0x1F0000 | v5;
					}
					sTrap[v5].nWall = (short) v17;
					sTrap[v5].field_A = wall[v17].picnum;
				}
				++v16;
				++v17;
			}
		}

		return 0x1F0000 | v5;
	}

	public static void FuncTrap(int a1, int a2, int RunPtr) {
		short nTrap = (short) (RunData[RunPtr].RunEvent & 0xFFFF);
		TrapStruct pTrap = sTrap[nTrap];
		int v21 = pTrap.field_2;

		switch (a1 & 0x7F0000) {
		case 0x10000:
			if (channel[a1 & 0x3FFF].field_4 <= 0)
				pTrap.field_0 = -1;
			else
				pTrap.field_0 = 12;
			return;
		case 0x20000:
			int v8 = pTrap.field_0;
			if (pTrap.field_0 >= 0) {
				if (--pTrap.field_0 <= 10) {
					int v10 = pTrap.field_4;
					if (v8 == 1) {
						pTrap.field_0 = nTrapInterval[nTrap];
						if (v10 == 14) {
							if (pTrap.nWall > -1)
								wall[pTrap.nWall].picnum = pTrap.field_A;
							if (pTrap.nWall2 > -1)
								wall[pTrap.nWall2].picnum = pTrap.field_C;
						}
					} else if (pTrap.field_0 == 5) {
						short nBullet = (short) (BuildBullet(v21, v10, 0, 0, 0, sprite[v21].ang, 0, 1) & 0xFFFF);
						if (nBullet == -1)
							return;

						if (v10 == 15) {
							sprite[nBullet].ang = (short) ((sprite[nBullet].ang - 512) & 0x7FF);
							D3PlayFX(StaticSound[32], v21);
						} else {
							sprite[nBullet].clipdist = 50;
							if (pTrap.nWall > -1)
								wall[pTrap.nWall].picnum = (short) (pTrap.field_A + 1);

							if (pTrap.nWall2 > -1)
								wall[pTrap.nWall2].picnum = (short) (pTrap.field_C + 1);
							D3PlayFX(StaticSound[36], v21);
						}
					}
				}
			}
			return;
		}
	}

	public static ByteBuffer saveDrips()
	{
		ByteBuffer bb = ByteBuffer.allocate(nDrips * DripStruct.size + 2);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		
		bb.putShort((short)nDrips);
		for (short i = 0; i < nDrips; i++) {
			sDrip[i].save(bb);
		}
		
		return bb;
	}
	
	public static void loadDrips(SafeLoader loader, Resource bb)
	{
		if(bb != null) {
			loader.nDrips = bb.readShort();
			for (short i = 0; i < loader.nDrips; i++) {
				if(loader.sDrip[i] == null)
					loader.sDrip[i] = new DripStruct();
				loader.sDrip[i].load(bb);
			}
		}
		else
		{
			nDrips = loader.nDrips;
			for (short i = 0; i < loader.nDrips; i++) {
				if(sDrip[i] == null)
					sDrip[i] = new DripStruct();
				sDrip[i].copy(loader.sDrip[i]);
			}
		}
	}
	
	public static void BuildDrip(int a1) {
		if (nDrips >= 50) {
			game.ThrowError("Too many drips!");
			return;
		}

		if (sDrip[nDrips] == null)
			sDrip[nDrips] = new DripStruct();
		DripStruct v4 = sDrip[nDrips];
		++nDrips;
		v4.field_0 = (short) a1;
		v4.field_2 = (short) (RandomSize(8) + 90);
		sprite[a1].cstat = (short) 32768;
	}
	
	public static ByteBuffer saveBobs()
	{
		ByteBuffer bb = ByteBuffer.allocate(nBobs * BobStruct.size + 2 + 200 * 2);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		
		bb.putShort((short)nBobs);
		for (short i = 0; i < 200; i++) 
			bb.putShort(sBobID[i]);
		
		for (short i = 0; i < nBobs; i++) {
			sBob[i].save(bb);
		}
		
		return bb;
	}
	
	public static void loadBobs(SafeLoader loader, Resource bb)
	{
		if(bb != null) {
			loader.nBobs = bb.readShort();
			for (short i = 0; i < 200; i++) 
				loader.sBobID[i] = bb.readShort();
			for (short i = 0; i < loader.nBobs; i++) {
				if(loader.sBob[i] == null)
					loader.sBob[i] = new BobStruct();
				loader.sBob[i].load(bb);
			}
		}
		else
		{
			nBobs = loader.nBobs;
			System.arraycopy(loader.sBobID, 0, sBobID, 0, nBobs);
			for (short i = 0; i < loader.nBobs; i++) {
				if(sBob[i] == null)
					sBob[i] = new BobStruct();
				sBob[i].copy(loader.sBob[i]);
			}
		}
	}
	

	public static void DoDrips() {
		for (int i = 0; i < nDrips; i++) {
			if (--sDrip[i].field_2 <= 0) {
				int v3 = SeqOffsets[62];
				if ((SectFlag[sprite[sDrip[i].field_0].sectnum] & 0x4000) == 0)
					v3 = SeqOffsets[62] + 1;
				MoveSequence(sDrip[i].field_0, v3, RandomSize(2) % SeqSize[v3]);
				sDrip[i].field_2 = (short) (RandomSize(8) + 90);
			}
		}

		for (int i = 0; i < nBobs; i++) {
			sBob[i].field_2 += 4;
			int v10 = sintable[8 * sBob[i].field_2 & 0x7FF] >> 4;
			if (sBob[i].field_3 != 0) {
				game.pInt.setceilinterpolate(sBob[i].field_0, sector[sBob[i].field_0]);
				sector[sBob[i].field_0].ceilingz = sBob[i].field_4 + v10;
			} else {
				int v13 = v10 + sBob[i].field_4;
				int v14 = v13 - sector[sBob[i].field_0].floorz;
				game.pInt.setfloorinterpolate(sBob[i].field_0, sector[sBob[i].field_0]);
				sector[sBob[i].field_0].floorz = v13;
				MoveSectorSprites(sBob[i].field_0, v14);
			}
		}
	}

	private static int FinaleMoves;
	private static int FinaleClock;

	public static void DoFinale() {
		if (lFinaleStart == 0)
			return;

		if (++FinaleMoves >= 90) {
			DimLights();
			if (nDronePitch <= -2400) {
				if (nFinaleStage < 2) {
					if (nFinaleStage == 1) {
						StopLocalSound();
						PlayLocalSound(StaticSound[76], 0);
						FinaleClock = totalclock + 120;
						++nFinaleStage;
					}
				} else if (nFinaleStage <= 2) {
					if (totalclock >= FinaleClock) {
						PlayLocalSound(StaticSound[77], 0);
						++nFinaleStage;
						FinaleClock = totalclock + 360;
					}
				} else if (nFinaleStage == 3 && totalclock >= FinaleClock) {
					FinishLevel();
				}
			} else {
				nDronePitch -= 128;
				BendAmbientSound();
				nFinaleStage = 1;
			}
		} else {
			if ((FinaleMoves & 2) == 0) {
				sprite[nFinaleSpr].ang = (short) RandomSize(11);
				BuildSpark(nFinaleSpr, 1);
			}
			if (RandomSize(2) == 0) {
				PlayFX2(StaticSound[78] | 0x2000, nFinaleSpr);
				for (int i = 0; i < numplayers; i++) {
					nQuake[i] = 1280;
				}
			}
		}
	}
	
	private static boolean bLightTrig;
	private static void DimLights()
	{
	    bLightTrig = !bLightTrig;
	    if (!bLightTrig)
	        return;

	    for (int i = 0; i < numsectors; i++)
	    {
	        if (sector[i].ceilingshade < 100)
	            sector[i].ceilingshade++;
	        if (sector[i].floorshade < 100)
	            sector[i].floorshade++;
	        for(int s = 0, w = sector[i].wallptr; s < sector[i].wallnum; s++, w++)
	        {
	        	if (wall[w].shade < 100)
	                wall[w].shade++;
	        }
	    }
	}
}
