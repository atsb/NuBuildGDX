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

import static ru.m210projects.Build.Engine.CLIPMASK1;
import static ru.m210projects.Build.Engine.MAXSPRITES;
import static ru.m210projects.Build.Engine.pHitInfo;
import static ru.m210projects.Build.Engine.sector;
import static ru.m210projects.Build.Engine.sintable;
import static ru.m210projects.Build.Engine.sprite;
import static ru.m210projects.Build.Engine.tsprite;
import static ru.m210projects.Build.Engine.wall;
import static ru.m210projects.Build.Gameutils.*;
import static ru.m210projects.Powerslave.Anim.BuildAnim;
import static ru.m210projects.Powerslave.Anim.GetAnimSprite;
import static ru.m210projects.Powerslave.Globals.*;
import static ru.m210projects.Powerslave.Light.AddFlash;
import static ru.m210projects.Powerslave.Main.*;
import static ru.m210projects.Powerslave.Main.game;
import static ru.m210projects.Powerslave.Player.GetPlayerFromSprite;
import static ru.m210projects.Powerslave.Random.RandomBit;
import static ru.m210projects.Powerslave.Random.RandomSize;
import static ru.m210projects.Powerslave.RunList.AddRunRec;
import static ru.m210projects.Powerslave.RunList.DoSubRunRec;
import static ru.m210projects.Powerslave.RunList.HeadRun;
import static ru.m210projects.Powerslave.RunList.NewRun;
import static ru.m210projects.Powerslave.RunList.RunData;
import static ru.m210projects.Powerslave.RunList.SubRunRec;
import static ru.m210projects.Powerslave.Seq.FrameFlag;
import static ru.m210projects.Powerslave.Seq.GetSeqPicnum;
import static ru.m210projects.Powerslave.Seq.MoveSequence;
import static ru.m210projects.Powerslave.Seq.PlotArrowSequence;
import static ru.m210projects.Powerslave.Seq.PlotSequence;
import static ru.m210projects.Powerslave.Seq.SeqBase;
import static ru.m210projects.Powerslave.Seq.SeqOffsets;
import static ru.m210projects.Powerslave.Seq.SeqSize;
import static ru.m210projects.Powerslave.Sound.StopSpriteSound;
import static ru.m210projects.Powerslave.Sprites.AngleChase;
import static ru.m210projects.Powerslave.Sprites.BuildSplash;
import static ru.m210projects.Powerslave.Sprites.DamageEnemy;
import static ru.m210projects.Powerslave.Sprites.GetSpriteHeight;
import static ru.m210projects.Powerslave.Enemies.Enemy.*;
import static ru.m210projects.Powerslave.Sprites.RadialDamageEnemy;
import static ru.m210projects.Powerslave.Weapons.SetNewWeapon;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

import com.badlogic.gdx.math.Vector2;

import ru.m210projects.Build.FileHandle.Resource;
import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Powerslave.Type.BulletInfo;
import ru.m210projects.Powerslave.Type.BulletStruct;
import ru.m210projects.Powerslave.Type.SafeLoader;

public class Bullet {
	private static Vector2 tmpVec = new Vector2();
	private static BulletStruct tmpBullet = new BulletStruct();

	private static int nBulletCount;
	private static int nBulletsFree;
	private static short BulletFree[] = new short[500];
	private static short nBulletEnemy[] = new short[500];
	private static BulletStruct BulletList[] = new BulletStruct[500];

	public static int lasthitx, lasthity, lasthitz;
	public static int lasthitsect, lasthitwall, lasthitsprite;

	public static final BulletInfo BulletInfo[] = { new BulletInfo(25, 1, 20, 65535, 65535, 13, 0, 0, 65535),
			new BulletInfo(25, 65535, 65000, 65535, 31, 73, 0, 0, 65535),
			new BulletInfo(15, 65535, 60000, 65535, 31, 73, 0, 0, 65535),
			new BulletInfo(5, 15, 2000, 65535, 14, 38, 4, 5, 3),
			new BulletInfo(250, 100, 2000, 65535, 33, 34, 4, 20, 65535),
			new BulletInfo(200, 65535, 2000, 65535, 20, 23, 4, 10, 65535),
			new BulletInfo(200, 65535, 60000, 68, 68, 65535, 65535, 0, 65535),
			new BulletInfo(300, 1, 0, 65535, 65535, 65535, 0, 50, 65535),
			new BulletInfo(18, 65535, 2000, 65535, 18, 29, 4, 0, 65535),
			new BulletInfo(20, 65535, 2000, 37, 11, 30, 4, 0, 65535),
			new BulletInfo(25, 65535, 3000, 65535, 44, 36, 4, 15, 90),
			new BulletInfo(30, 65535, 1000, 65535, 52, 53, 4, 20, 48),
			new BulletInfo(20, 65535, 3500, 65535, 54, 55, 4, 30, 65535),
			new BulletInfo(10, 65535, 5000, 65535, 57, 76, 4, 0, 65535),
			new BulletInfo(40, 65535, 1500, 65535, 63, 38, 4, 10, 40),
			new BulletInfo(20, 65535, 2000, 65535, 60, 12, 0, 0, 65535),
			new BulletInfo(5, 65535, 60000, 65535, 31, 76, 0, 0, 65535) };

	public static void InitBullets() {
		nBulletCount = 0;
		nBulletsFree = 500;
		for (int i = 0; i < 500; i++) {
			BulletFree[i] = (short) i;
			BulletList[i] = new BulletStruct();
		}
		Arrays.fill(nBulletEnemy, (short) -1);
	}

	public static ByteBuffer saveBullets()
	{
		ByteBuffer bb = ByteBuffer.allocate(4 + (4 + BulletStruct.size) * 500 + 18);
		bb.order(ByteOrder.LITTLE_ENDIAN);

		bb.putShort((short)nBulletCount);
		bb.putShort((short)nBulletsFree);
		for(int i = 0; i < 500; i++) {
			bb.putShort(BulletFree[i]);
			bb.putShort(nBulletEnemy[i]);
			BulletList[i].save(bb);
		}

		bb.putInt(lasthitx);
		bb.putInt(lasthity);
		bb.putInt(lasthitz);
		bb.putShort((short)lasthitsect);
		bb.putShort((short)lasthitwall);
		bb.putShort((short)lasthitsprite);

		return bb;
	}

	public static void loadBullets(SafeLoader loader, Resource bb)
	{
		if(bb != null) {
			loader.nBulletCount = bb.readShort();
			loader.nBulletsFree = bb.readShort();
			for(int i = 0; i < 500; i++) {
				loader.BulletFree[i] = bb.readShort();
				loader.nBulletEnemy[i] = bb.readShort();
				if(loader.BulletList[i] == null)
					loader.BulletList[i] = new BulletStruct();
				loader.BulletList[i].load(bb);
			}

			loader.lasthitx = bb.readInt();
			loader.lasthity = bb.readInt();
			loader.lasthitz = bb.readInt();
			loader.lasthitsect = bb.readShort();
			loader.lasthitwall = bb.readShort();
			loader.lasthitsprite = bb.readShort();
		}
		else
		{
			nBulletCount = loader.nBulletCount;
			nBulletsFree = loader.nBulletsFree;
			System.arraycopy(loader.BulletFree, 0, BulletFree, 0, 500);
			System.arraycopy(loader.nBulletEnemy, 0, nBulletEnemy, 0, 500);

			for(int i = 0; i < 500; i++) {
				if(BulletList[i] == null)
					BulletList[i] = new BulletStruct();
				BulletList[i].copy(loader.BulletList[i]);
			}

			lasthitx = loader.lasthitx;
			lasthity = loader.lasthity;
			lasthitz = loader.lasthitz;
			lasthitsect = loader.lasthitsect;
			lasthitwall = loader.lasthitwall;
			lasthitsprite = loader.lasthitsprite;
		}
	}

	public static int GrabBullet() {
		return BulletFree[--nBulletsFree];
	}

	public static int DestroyBullet(int nBullet) {
		int nSprite = BulletList[nBullet].nSprite;
		DoSubRunRec(BulletList[nBullet].bull_6);
		DoSubRunRec(sprite[nSprite].lotag - 1);
		SubRunRec(BulletList[nBullet].bull_8);
		StopSpriteSound(nSprite);
		engine.mydeletesprite((short) nSprite);
		BulletFree[nBulletsFree] = (short) nBullet;
		nBulletsFree++;
		return nBulletsFree;
	}

	public static void IgniteSprite(int nSprite) {
		sprite[nSprite].hitag += 2;
		int v5 = GetAnimSprite(BuildAnim(-1, 38, 0, sprite[nSprite].x, sprite[nSprite].y, sprite[nSprite].z,
				sprite[nSprite].sectnum, 0x28, 20));
		sprite[v5].hitag = (short) nSprite;
		engine.changespritestat((short) v5, (short) 404);
		sprite[v5].yrepeat = (short) Math.max(32 * engine.getTile(sprite[v5].picnum).getHeight() / nFlameHeight, 1);
	}

	public static void BulletHitsSprite(BulletStruct pBullet, int nSource, int nDest, int x, int y, int z, int sectnum) {
		BulletInfo pBulletInfo = BulletInfo[pBullet.type];
		int type = pBullet.type;
		int statnum = sprite[nDest].statnum;

		switch (type) {
		case 14:
			if (statnum > 107 || statnum == 98)
				return;
		case 1:
		case 2:
		case 8:
		case 9:
		case 12:
		case 13:
		case 15:
		case 16:
			if (statnum != 0 && statnum <= 98) {
				SPRITE pDest = sprite[nDest];
				SPRITE pSprite = sprite[pBullet.nSprite];
				if (statnum == 98) {
					int v20 = (pSprite.ang + 256) - RandomSize(9);
					pDest.xvel = (short) (2 * sintable[(v20 + 512) & 0x7FF]);
					pDest.yvel = (short) (2 * sintable[v20 & 0x7FF]);
					pDest.zvel = (short) (-256 * (RandomSize(3) + 1));
				} else {
					short v15 = pDest.xvel;
					short v17 = pDest.yvel;
					pDest.xvel = (short) (sintable[(pSprite.ang + 512) & 0x7FF] >> 2);
					pDest.yvel = (short) (sintable[pSprite.ang & 0x7FF] >> 2);
					MoveCreature(nDest);
					pDest.xvel = v15;
					pDest.yvel = v17;
				}
			}
			break;
		case 3:
			if (statnum <= 107 && statnum != 98) {
				sprite[nDest].hitag++;
				if (sprite[nDest].hitag == 15)
					IgniteSprite(nDest);
				if (RandomSize(2) == 0) {
					BuildAnim(-1, pBulletInfo.inf_C, 0, x, y, z, sectnum, 40, pBulletInfo.inf_E);
					return;
				}
			}
			return;
		}

		int damage = pBulletInfo.force;
		if (pBullet.bull_13 > 1)
			damage *= 2;
		DamageEnemy(nDest, nSource, damage);

		if (statnum > 90 && statnum < 199) {
			switch (statnum) {
			case 97:
				return;
			case 98:
			case 102:
			case 141:
			case 152:
				BuildAnim(-1, 12, 0, x, y, z, sectnum, 40, 0);
				return;
			default:
				BuildAnim(-1, 39, 0, x, y, z, sectnum, 40, 0);
				if (pBullet.type <= 2)
					return;
				BuildAnim(-1, pBulletInfo.inf_C, 0, x, y, z, sectnum, 40, pBulletInfo.inf_E);
				return;
			}
		}
		BuildAnim(-1, pBulletInfo.inf_C, 0, x, y, z, sectnum, 40, pBulletInfo.inf_E);
	}

	public static Vector2 BackUpBullet(int a1, int a2, int a3) {
		a1 -= sintable[(a3 + 512) & 0x7FF] >> 11;
		a2 -= sintable[a3 & 0x7FF] >> 11;

		return tmpVec.set(a1, a2);
	}

	public static void SetBulletEnemy(short a1, int a2) {
		if (a1 >= 0)
			nBulletEnemy[a1] = (short) a2;
	}

	public static int BuildBullet(int a1, int nType, int a3, int a4, int wz, int a6, int zAngle, int a8) {
		BulletInfo pBullet = BulletInfo[nType];

		if (pBullet.inf_4 > 30000) {
			if (zAngle >= 10000) {
				int v88 = zAngle - 10000;
				SPRITE v11 = sprite[v88];
				if ((v11.cstat & 0x101) != 0 && isValidSector(v11.sectnum)) {
					short i = engine.insertsprite(sprite[a1].sectnum, (short) 200);
					sprite[i].ang = (short) a6;
					tmpBullet.nSprite = i;
					tmpBullet.type = (short) nType;
					tmpBullet.bull_13 = (byte) a8;
					BulletHitsSprite(tmpBullet, a1 & 0xFFFF, v88, v11.x, v11.y, v11.z - (GetSpriteHeight(v88) >> 1),
							v11.sectnum);
					engine.mydeletesprite(i);
					return -1;
				}
				zAngle = v11.cstat & 0x101;
			}
		}
		if (nBulletsFree == 0)
			return -1;

		short v19 = sprite[a1].sectnum;
		if (sprite[a1].statnum == 100)
			v19 = nPlayerViewSect[GetPlayerFromSprite(a1)];

		short spr = engine.insertsprite(v19, (short) 200);
		if (spr < 0 || spr >= MAXSPRITES) {
			game.ThrowError("spr>=0 && spr<MAXSPRITES");
			return -1;
		}

		int v21 = GetSpriteHeight(a1);
		if (wz == -1)
			wz = -(v21 - (v21 >> 2));

		sprite[spr].x = sprite[a1].x;
		sprite[spr].y = sprite[a1].y;
		sprite[spr].z = sprite[a1].z;

		int nBullet = GrabBullet();
		BulletStruct v35 = BulletList[nBullet];

		nBulletEnemy[nBullet] = -1;

		sprite[spr].cstat = 0;
		sprite[spr].shade = -64;
		if ((pBullet.inf_E & 4) != 0)
			sprite[spr].pal = 4;
		else
			sprite[spr].pal = 0;

		sprite[spr].clipdist = 25;
		int v31 = pBullet.inf_12;
		if ( v31 < 0 )
		    v31 = 30;

		sprite[spr].xrepeat = (short) v31;
		sprite[spr].yrepeat = (short) v31;
		sprite[spr].xoffset = 0;
		sprite[spr].yoffset = 0;
		sprite[spr].ang = (short) a6;
		sprite[spr].xvel = 0;
		sprite[spr].yvel = 0;
		sprite[spr].zvel = 0;
		sprite[spr].owner = (short) a1;

		sprite[spr].lotag = (short) (HeadRun() + 1);
		sprite[spr].extra = -1;
		sprite[spr].hitag = 0;

		v35.bull_10 = 0;
		v35.bull_E = pBullet.inf_2;
		v35.frmOffset = 0;
		if (pBullet.inf_8 == -1) {
			v35.bull_12 = 1;
			v35.baseSeq = pBullet.inf_A;
		} else {
			v35.bull_12 = 0;
			v35.baseSeq = pBullet.inf_8;
		}

		sprite[spr].picnum = GetSeqPicnum(v35.baseSeq, 0, 0);
		if (v35.baseSeq == 31)
			sprite[spr].cstat |= 0x8000;

		v35.zang = (short) zAngle;
		v35.type = (short) nType;
		v35.nSprite = spr;
		v35.bull_6 = (short) AddRunRec(sprite[spr].lotag - 1, 0xB0000 | nBullet);
		v35.bull_8 = (short) AddRunRec(NewRun, 0xB0000 | nBullet);
		v35.bull_13 = (byte) a8;
		sprite[spr].z += wz;
		short sect = sprite[spr].sectnum;

		while (true) {
			if (sprite[spr].z >= sector[sect].ceilingz)
				break;
			if (SectAbove[sect] == -1) {
				sprite[spr].z = sector[sect].ceilingz;
				break;
			}
			engine.mychangespritesect(spr, SectAbove[sect]);
			sect = SectAbove[sect];
		}

		int zvec = 0;
		if (zAngle >= 10000) {
			int v89 = zAngle - 10000;
			if (pBullet.inf_4 > 0x7530) {
				nBulletEnemy[nBullet] = (short) v89;
			} else {
				int v51 = GetSpriteHeight(v89), v53;
				if (sprite[v89].statnum == 100)
					v53 = v51 >> 2;
				else
					v53 = v51 >> 1;
				int dx, dy, dz = sprite[v89].z - (v51 - v53);
				if (a1 == -1 || sprite[a1].statnum == 100) {
					dx = sprite[v89].x - sprite[spr].x;
					dy = sprite[v89].y - sprite[spr].y;
				} else {

					int sx = sprite[v89].x;
					int sy = sprite[v89].y;
					if (sprite[v89].statnum == 100) {
						int plr = GetPlayerFromSprite(v89);
						if (plr > -1) {
							int pxvel = 15 * nPlayerDX[plr];
							int pyvel = 15 * nPlayerDY[plr];

							if(!isOriginal()) {
								pxvel = BClipRange(pxvel, -1000, 1000);
								pyvel = BClipRange(pyvel, -1000, 1000);
							}

							sx += pxvel;
							sy += pyvel;
						}
					} else {
						sx += 20 * sprite[v89].xvel >> 6;
						sy += 20 * sprite[v89].yvel >> 6;
					}

					dy = sy - sprite[spr].y;
					dx = sx - sprite[spr].x;
					a6 = engine.GetMyAngle(dx, dy);
					sprite[a1].ang = (short) a6;
				}

				int v67 = engine.ksqrt(dx * dx + dy * dy);
				if (v67 != 0)
					zvec = (dz - sprite[spr].z) * pBullet.inf_4 / v67;
				else
					zvec = 0;
			}
		} else {
			zvec = isOriginal() ? (pBullet.inf_4 * -sintable[zAngle & 0x7FF] >> 11) : -(zAngle / 32) * pBullet.inf_4;
		}

		int dist = 4 * sprite[a1].clipdist;
		v35.xvec = dist * sintable[(a6 + 512) & 0x7FF];
		v35.yvec = dist * sintable[a6 & 0x7FF];
		v35.zvec = 0;

		nBulletEnemy[nBullet] = -1;
		if (MoveBullet(nBullet)) {
			spr = -1;
		} else {
			v35.bull_10 = (short) pBullet.inf_4;
			v35.xvec = pBullet.inf_4 * (sintable[(a6 + 512) & 0x7FF] >> 3);
			v35.yvec = pBullet.inf_4 * (sintable[a6 & 0x7FF] >> 3);
			v35.zvec = zvec >> 3;
		}

		return spr | (nBullet << 16);
	}

	private static boolean MoveBullet(int nBullet) {
		BulletStruct pBullet = BulletList[nBullet];
		int type = pBullet.type;
		BulletInfo pBulletInfo = BulletInfo[type];
		int nSprite = pBullet.nSprite;
		SPRITE pSprite = sprite[pBullet.nSprite];
		if(!isValidSector(pSprite.sectnum))
			return false;

		int nSectFlag = SectFlag[pSprite.sectnum];

		int hitsec = -1, hitwall = -1, hitspr = -1;
		int hitx = 0, hity = 0, hitz = 0;
		boolean out = false;

		if ((pBullet.bull_10 & 0xFFFF) >= 30000) {
			out = true;
			if (nBulletEnemy[nBullet] == -1) {
				int zvel = isOriginal() ? -8 * sintable[pBullet.zang & 0x7FF] : -16 * pBullet.zang * 32;
				engine.hitscan(pSprite.x, pSprite.y, pSprite.z, pSprite.sectnum, sintable[(pSprite.ang + 512) & 0x7FF],
						sintable[pSprite.ang], zvel, pHitInfo, CLIPMASK1);

				hitx = pHitInfo.hitx;
				hity = pHitInfo.hity;
				hitz = pHitInfo.hitz;
				hitsec = pHitInfo.hitsect;
				hitwall = pHitInfo.hitwall;
				hitspr = pHitInfo.hitsprite;
			} else {
				int nEnemy = nBulletEnemy[nBullet];
				hitx = sprite[nEnemy].x;
				hity = sprite[nEnemy].y;
				hitz = sprite[nEnemy].z - (GetSpriteHeight(nEnemy) >> 1);
				hitsec = sprite[nEnemy].sectnum;
				hitspr = nEnemy;
			}
			lasthitx = hitx;
			lasthity = hity;
			lasthitz = hitz;
			lasthitsect = hitsec;
			lasthitwall = hitwall;
			lasthitsprite = hitspr;
		} else {
			int v9 = nBulletEnemy[nBullet];
			int moveHit = 0;
			if (v9 == -1 || (sprite[v9].cstat & 0x101) == 0) {
				nBulletEnemy[nBullet] = -1;
				if (type == 3) {
					if (pBullet.bull_E >= 8) {
						pSprite.xrepeat += 4;
						pSprite.yrepeat += 4;
					} else {
						pSprite.xrepeat--;
						pSprite.yrepeat += 8;
						pBullet.zvec -= 200;
						if (pSprite.shade < 90)
							pSprite.shade += 35;
						if (pBullet.bull_E == 3) {
							pBullet.baseSeq = 45;
							pBullet.frmOffset = 0;
							pSprite.xrepeat = 40;
							pSprite.yrepeat = 40;
							pSprite.shade = 0;
							pSprite.z += 512;
						}
					}
				}

				moveHit = engine.movesprite((short) nSprite, pBullet.xvec, pBullet.yvec, pBullet.zvec,
						pSprite.clipdist >> 1, pSprite.clipdist >> 1, 1);
			} else
				moveHit = AngleChase(nSprite, v9, (pBullet.bull_10 & 0xFFFF), 0, 16);

			out = moveHit != 0;
			if (out) {
				hitx = pSprite.x;
				hity = pSprite.y;
				hitz = pSprite.z;
				hitsec = pSprite.sectnum;

				if ((moveHit & 0x30000) != 0 || (moveHit & 0xC000) == 0x8000) {
					hitwall = moveHit & 0x3FFF;
				} else if ((moveHit & 0xC000) == 0xC000) {
					hitspr = moveHit & 0x3FFF;
				}
			}

			if (hitwall == -1 && hitspr == -1) {
				if ((((nSectFlag ^ SectFlag[pSprite.sectnum]) >> 8) & 0x20) != 0) {
					DestroyBullet(nBullet);
					out = true;
				}

				if (!out && type != 15 && type != 3) {
					AddFlash(sprite[nSprite].sectnum, sprite[nSprite].x, sprite[nSprite].y, sprite[nSprite].z, 0);
					if (sprite[nSprite].pal != 5)
						sprite[nSprite].pal = 1;
				}
				return out;
			}
		}

		if (hitspr != -1) {
			if (pSprite.pal != 5 || sprite[hitspr].statnum != 100) {
				BulletHitsSprite(pBullet, pSprite.owner, hitspr, hitx, hity, hitz, hitsec);
			} else {
				int plr = GetPlayerFromSprite(hitspr);

				if (PlayerList[plr].mummified == 0) {
					PlayerList[plr].mummified = 1;
					SetNewWeapon(plr, 7);
				}
			}
		} else if (hitwall != -1) {
			if (wall[hitwall].picnum == 3604 && wall[hitwall].nextsector > -1) {
				int v31 = BulletInfo[pBullet.type].force;
				if (pBullet.bull_13 > 1)
					v31 *= 2;

				DamageEnemy(sector[wall[hitwall].nextsector].extra, engine.GetWallNormal(hitwall) & 0x7FF, v31);
			}
		}

		if(hitsec != -1) {
			if (hitwall != -1 || hitspr != -1) {
				if (hitwall == -1) {
					pSprite.x = hitx;
					pSprite.y = hity;
					pSprite.z = hitz;
					engine.mychangespritesect((short) nSprite, (short) hitsec);
				} else {
					Vector2 vec = BackUpBullet(hitx, hity, pSprite.ang);
					hitx = (int) vec.x;
					hity = (int) vec.y;
					if (type != 3 || RandomSize(2) == 0) {
						int v38 = RandomSize(8);
						int v37 = RandomBit();
						int v39 = 8 * v38;
						if (v37 == 0)
							v39 = -v39;

						BuildAnim(-1, pBulletInfo.inf_C, 0, hitx, hity, hitz + v39, hitsec, 0x28, pBulletInfo.inf_E);
					}
				}
				if (pBulletInfo.inf_10 != 0) {
					nRadialBullet = (short) type;
					RadialDamageEnemy(nSprite, pBulletInfo.force, pBulletInfo.inf_10);
					nRadialBullet = -1;
					AddFlash(pSprite.sectnum, pSprite.x, pSprite.y, pSprite.z, 128);
				}
			} else {
				if (SectBelow[hitsec] >= 0 && (SectFlag[SectBelow[hitsec]] & 0x2000) != 0 || SectDepth[hitsec] != 0) {
					pSprite.x = hitx;
					pSprite.y = hity;
					pSprite.z = hitz;
					BuildSplash(nSprite, hitsec);
				} else {
					BuildAnim(-1, pBulletInfo.inf_C, 0, hitx, hity, hitz, hitsec, 0x28, pBulletInfo.inf_E);
				}
			}
		}
		DestroyBullet(nBullet);
		return out;
	}

	public static void FuncBullet(int a1, int a2, int RunPtr) {
		short nBullet = (short) (RunData[RunPtr].RunEvent & 0xFFFF);
		if (nBullet < 0 || nBullet >= 500) {
			game.ThrowError("nBullet>=0 && nBullet<MAX_BULLETS");
			return;
		}

		BulletStruct pBullet = BulletList[nBullet];
		int nSprite = pBullet.nSprite;
		if ((a1 & 0x7F0000) == 0x20000) {
			int v17 = SeqOffsets[pBullet.baseSeq];
			int v8 = pBullet.frmOffset;
			MoveSequence(nSprite, v17, v8);
			if ((FrameFlag[v8 + SeqBase[v17]] & 0x80) != 0)
				BuildAnim(-1, 45, 0, sprite[nSprite].x, sprite[nSprite].y, sprite[nSprite].z, sprite[nSprite].sectnum,
						sprite[nSprite].xrepeat, 0);
			pBullet.frmOffset++;
			if (pBullet.frmOffset >= SeqSize[v17]) {
				if (pBullet.bull_12 == 0) {
					pBullet.baseSeq = BulletInfo[pBullet.type].inf_A;
					pBullet.bull_12++;
				}
				pBullet.frmOffset = 0;
			}
			if (pBullet.bull_E == -1 || (--pBullet.bull_E) != 0)
				MoveBullet(nBullet);
			else
				DestroyBullet(nBullet);
		} else if ((a1 & 0x7F0000) == 0x90000) {
			int spr = a1 & 0xFFFF;
			tsprite[spr].statnum = 1000;
			if (pBullet.type == 15) {
				PlotArrowSequence(spr, SeqOffsets[pBullet.baseSeq], pBullet.frmOffset);
			} else {
				PlotSequence(spr, SeqOffsets[pBullet.baseSeq], pBullet.frmOffset, 0);
				tsprite[spr].owner = -1;
			}
		}
	}
}
