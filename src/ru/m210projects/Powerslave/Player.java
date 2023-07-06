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

import static ru.m210projects.Build.Gameutils.*;
import static ru.m210projects.Build.Pragmas.*;
import static ru.m210projects.Powerslave.Globals.*;
import static ru.m210projects.Powerslave.Random.*;
import static ru.m210projects.Powerslave.Seq.*;
import static ru.m210projects.Powerslave.Anim.*;
import static ru.m210projects.Powerslave.Enemies.Ra.*;
import static ru.m210projects.Powerslave.RunList.*;
import static ru.m210projects.Powerslave.Object.*;
import static ru.m210projects.Powerslave.Main.*;
import static ru.m210projects.Powerslave.LoadSave.*;
import static ru.m210projects.Powerslave.View.*;
import static ru.m210projects.Powerslave.Sound.*;
import static ru.m210projects.Powerslave.SpiritHead.*;
import static ru.m210projects.Powerslave.Palette.*;
import static ru.m210projects.Powerslave.Cinema.*;
import static ru.m210projects.Powerslave.Weapons.*;
import static ru.m210projects.Powerslave.Factory.PSInput.*;
import static ru.m210projects.Powerslave.Sprites.*;
import static ru.m210projects.Powerslave.Sector.*;
import static ru.m210projects.Powerslave.Map.*;
import static ru.m210projects.Powerslave.Light.*;
import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Powerslave.Type.StatusAnim.*;
import static ru.m210projects.Build.Net.Mmulti.numplayers;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Vector2;

import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.FileHandle.Compat.Path;
import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Powerslave.Main.UserFlag;
import ru.m210projects.Powerslave.Type.PlayerSave;

public class Player {
	
	public static final String deathMessage1 = "or \"enter\" to load last saved game";
	public static final String deathMessage2 = "Press \"use\" to restart the level";
	
	public static final int MAXHORIZ = 300;
	public static final int MAXOHORIZ = 180;
	public static final int MINHORIZ = -130;
	public static final int MINOHORIZ = 0;

	public static class Action {
		public short seq;
		boolean a2;

		public Action(int seq, boolean a2) {
			this.seq = (short) seq;
			this.a2 = a2;
		}
	}

	public static Action[] ActionSeq = new Action[] { new Action(18, false), new Action(0, false),
			new Action(9, false), new Action(27, false), new Action(63, false), new Action(72, false),
			new Action(54, false), new Action(45, false), new Action(54, false), new Action(81, false),
			new Action(90, false), new Action(99, false), new Action(108, false), new Action(8, false),
			new Action(0, false), new Action(139, false), new Action(117, true), new Action(119, true),
			new Action(120, true), new Action(121, true), new Action(122, true) };

	public static short nActionEyeLevel[] = new short[] { -14080, -14080, -14080, -14080, -14080, -14080, -8320, -8320,
			-8320, -8320, -8320, -8320, -8320, -14080, -14080, -14080, -14080, -14080, -14080, -14080, -14080 };
	public static int nHeightTemplate[] = new int[] { 0, 0, 0, 0, 0, 0, 7, 7, 7, 9, 9, 9, 9, 0, 0, 0, 0, 0, 0, 0, 0 };

	public static short nLocalEyeSect;
	public static PlayerSave[] sPlayerSave = new PlayerSave[8];

	public static short GetPlayerFromSprite(int num) {
		return (short) (RunData[sprite[num].owner].RunEvent & 0xFFFF);
	}

	public static void InitPlayerKeys(int num) {
		PlayerList[num].KeysBitMask = 0;
	}

	public static void RestartPlayer(int num) {
		short nSprite = PlayerList[num].spriteId;
		short nDopple = nDoppleSprite[num];

		if (nSprite > -1) {
			DoSubRunRec(sprite[nSprite].owner);
			FreeRun(sprite[nSprite].lotag - 1);
			engine.changespritestat(nSprite, (short) 0);
			PlayerList[num].spriteId = -1;
			if (nPlayerFloorSprite[num] > -1)
				engine.mydeletesprite(nPlayerFloorSprite[num]);
			if (nDopple > -1) {
				DoSubRunRec(sprite[nDopple].owner);
				FreeRun(sprite[nDopple].lotag - 1);
				engine.mydeletesprite(nDopple);
			}
		}

		short spr = GrabBody();
		PlayerSave pSave = sPlayerSave[num];

		engine.changespritesect(spr, pSave.sect);
		engine.changespritestat(spr, (short) 100);
		if (spr < 0 || spr >= MAXSPRITES) // 4096
			game.ThrowError("spr>=0 && spr<MAXSPRITES");

		short dspr = engine.insertsprite(sprite[spr].sectnum, (short) 100);
		nDoppleSprite[num] = dspr;

		if (dspr < 0 || dspr >= MAXSPRITES)
			game.ThrowError("(dspr>=0) && (dspr<MAXSPRITES)");

		SPRITE pSprite = sprite[spr];
		SPRITE pDSprite = sprite[dspr];

		int floorspr;
		if (numplayers <= 1) {
			pSprite.x = pSave.x;
			pSprite.y = pSave.y;
			pSprite.z = sector[pSave.sect].floorz;
			PlayerList[num].ang = pSprite.ang = pSave.ang;
			floorspr = -1;
		} else {
			int nNetStart = nNetStartSprite[nCurStartSprite++];
			SPRITE pNetStart = sprite[nNetStart];
			if (nCurStartSprite >= nNetStartSprites)
				nCurStartSprite = 0;

			pSprite.x = pNetStart.x;
			pSprite.y = pNetStart.y;
			pSprite.z = pNetStart.z;
			engine.mychangespritesect(spr, pNetStart.sectnum);
			PlayerList[num].ang = pSprite.ang = pNetStart.ang;
			floorspr = engine.insertsprite(pSprite.sectnum, (short) 0);

			if (floorspr < 0 || floorspr >= MAXSPRITES)
				game.ThrowError("floorspr>=0 && floorspr<MAXSPRITES");
			sprite[floorspr].x = pSprite.x;
			sprite[floorspr].y = pSprite.y;
			sprite[floorspr].z = pSprite.z;
			sprite[floorspr].yrepeat = 64;
			sprite[floorspr].xrepeat = 64;
			sprite[floorspr].cstat = 32;
			sprite[floorspr].picnum = (short) (num + 3571);
		}

		nPlayerFloorSprite[num] = (short) floorspr;
		pSprite.cstat = 257;
		pSprite.shade = -12;
		pSprite.clipdist = 58;
		pSprite.pal = 0;
		pSprite.xrepeat = 40;
		pSprite.yrepeat = 40;
		pSprite.xoffset = 0;
		pSprite.yoffset = 0;
		pSprite.picnum = (short) GetSeqPicnum(25, 18, 0);
		nStandHeight = GetSpriteHeight(spr);
		pSprite.xvel = 0;
		pSprite.yvel = 0;
		pSprite.zvel = 0;
		pSprite.lotag = (short) (HeadRun() + 1);
		pSprite.hitag = 0;
		pSprite.extra = -1;

		pDSprite.x = pSprite.x;
		pDSprite.y = pSprite.y;
		pDSprite.z = pSprite.z;
		pDSprite.xrepeat = pSprite.xrepeat;
		pDSprite.yrepeat = pSprite.yrepeat;
		pDSprite.yoffset = 0;
		pDSprite.xoffset = 0;
		pDSprite.shade = pSprite.shade;
		pDSprite.ang = pSprite.ang;
		pDSprite.cstat = pSprite.cstat;
		pDSprite.lotag = (short) (HeadRun() + 1);
		
		PlayerList[num].anim_ = 0;
		PlayerList[num].animCount = 0;
		PlayerList[num].spriteId = spr;
		PlayerList[num].invisibility = 0;
		PlayerList[num].mummified = 0;
		SetTorch(num, 0);
		nPlayerInvisible[num] = 0;
		PlayerList[num].weaponFire = 0;
		PlayerList[num].seqOffset = 0;
		nPlayerViewSect[num] = pSave.sect;
		PlayerList[num].weaponState = 0;
		nPlayerDouble[num] = 0;
		PlayerList[num].seq = 25;
		nPlayerPushSound[num] = -1;
		PlayerList[num].newWeapon = -1;
		PlayerList[num].lastWeapon = 0;
		PlayerList[num].AirAmount = 100;
		nPlayerGrenade[num] = -1;
		nPlayerTorch[num] = 0;
		PlayerList[num].AirMaskAmount = 0;
		
		if(gClassicMode) {
			PlayerList[num].HealthAmount = 800;
			if (nNetPlayerCount != 0)
				PlayerList[num].HealthAmount = 1600;
			if (PlayerList[num].currentWeapon == 7)
				PlayerList[num].currentWeapon = PlayerList[num].lastWeapon;
		}
		
		if (levelnum <= 20) {
			if(!gClassicMode)
				RestoreMinAmmo(num);
			else CheckClip(num);
		}
		else {
			ResetPlayerWeapons(num);
			PlayerList[num].MagicAmount = 0;
		}
		
		PlayerList[num].eyelevel = -14080;
		dVertPan[num] = 0;
		nTemperature[num] = 0;
		nYDamage[num] = 0;
		nXDamage[num] = 0;
		PlayerList[num].horiz = 92;
		nDestVertPan[num] = 92;
		nBreathTimer[num] = 90;
		nTauntTimer[num] = (short) (RandomSize(3) + 3);

		pDSprite.owner = (short) AddRunRec(pDSprite.lotag - 1, 0xA0000 | num);
		pSprite.owner = (short) AddRunRec(pSprite.lotag - 1, 0xA0000 | num);

		if (PlayerList[num].RunFunc < 0)
			PlayerList[num].RunFunc = (short) AddRunRec(NewRun, 0xA0000 | num);

		BuildRa(num);
		if (num == nLocalPlayer) {
//			nLocalSpr = spr;
			SetMagicFrame();
			RestoreGreenPal();
			bPlayerPan = false;
			bLockPan = false;
		}

		// name
		totalvel[num] = 0;
		nDeathType[num] = 0;
		nQuake[num] = 0;
		if (num == nLocalPlayer)
			SetHealthFrame(0);
	}

	public static void RestoreSavePoint(int num) {
		PlayerSave ps = sPlayerSave[num];

		initx = ps.x;
		inity = ps.y;
		initz = ps.z;
		initsect = ps.sect;
		inita = ps.ang;
		PlayerList[num].ang = inita;
	}

	public static PlayerSave SetSavePoint(int num, int x, int y, int z, int sect, int ang) {
		PlayerSave ps = sPlayerSave[num];
		ps.x = x;
		ps.y = y;
		ps.z = z;
		ps.sect = (short) sect;
		ps.ang = (short) ang;
		return ps;
	}

	public static int GrabPlayer() {
		if (PlayerCount >= 8)
			return -1;
		return PlayerCount++;
	}

	public static void InitPlayerInventory(int nPlayer) {
		PlayerList[nPlayer].reset();
		PlayerList[nPlayer].nPlayer = nPlayer;
		nPlayerItem[nPlayer] = -1;
		nPlayerSwear[nPlayer] = 4;
		ResetPlayerWeapons(nPlayer);
		if(gClassicMode)
			nPlayerLives[nPlayer] = 3;
		else nPlayerLives[nPlayer] = 0;
		PlayerList[nPlayer].spriteId = -1;
		PlayerList[nPlayer].RunFunc = -1;
		nPistolClip[nPlayer] = 6;
		nPlayerClip[nPlayer] = 0;
		PlayerList[nPlayer].currentWeapon = 0;
		if (nPlayer == nLocalPlayer)
			bMapMode = false;
		nPlayerScore[nPlayer] = 0;
		engine.loadtile(nPlayer + 3571);
//		  nPlayerColor[v9] = *(_BYTE *)(waloff[v10 + 3571] + tilesizx[v10 + 3571] * tilesizy[v10 + 3571] / 2); XXX
	}

	public static void SetItemSeq() {
		int item = nPlayerItem[nLocalPlayer];
		if (item < 0)
			nItemSeq = -1;
		else {
			if (nItemMagic[nPlayerItem[nLocalPlayer]] <= PlayerList[nLocalPlayer].MagicAmount)
				nItemAltSeq = 0;
			else
				nItemAltSeq = 2;
			nItemFrame = 0;
			nItemSeq = nItemAltSeq + nItemSeqOffset[item];
			nItemFrames = SeqSize[nStatusSeqOffset + nItemSeq];
		}
	}

	public static void SetPlayerItem(int nPlayer, int nItem) {
		nPlayerItem[nPlayer] = (short) nItem;
		if (nPlayer == nLocalPlayer) {
			SetItemSeq();
			if (nItem >= 0)
				BuildStatusAnim(2 * PlayerList[nPlayer].ItemsAmount[nItem] + 156, 0);
		}
	}

	public static void FuncPlayer(int a1, int a2, int RunPtr) {
		short player = (short) (RunData[RunPtr].RunEvent & 0xFFFF);
		if (player < 0 || player >= 8)
			game.ThrowError("player>=0 && player<MAXPLAYER");
		if (PlayerList[player].network == -1)
			return;

		int v233 = 0;
		short spr = PlayerList[player].spriteId;
		int dspr = nDoppleSprite[player];
		SPRITE pDSprite = sprite[dspr];
		SPRITE pSprite = sprite[spr];
		int anim = PlayerList[player].anim_;

		short v229 = -1;
		switch (a1 & 0x7F0000) {
		case nEventProcess:
			if ((sPlayerInput[player].bits & nItemUse) != 0) 
				UseCurItem(player, nPlayerItem[player]);
			if ((sPlayerInput[player].bits & nItemLeft) != 0) 
				SetPrevItem(player);
			if ((sPlayerInput[player].bits & nItemRight) != 0)
				SetNextItem(player);
			
			if ((sPlayerInput[player].bits & nItemHand) != 0) 
				UseCurItem(player, 3);
			if ((sPlayerInput[player].bits & nItemEye) != 0) 
				UseCurItem(player, 4);
			if ((sPlayerInput[player].bits & nItemMask) != 0) 
				UseCurItem(player, 5);
			if ((sPlayerInput[player].bits & nItemHeart) != 0) 
				UseCurItem(player, 0);
			if ((sPlayerInput[player].bits & nItemTorch) != 0) 
				UseCurItem(player, 2);
			if ((sPlayerInput[player].bits & nItemScarab) != 0) 
				UseCurItem(player, 1);
			
			int next_anim = PlayerList[player].anim_;

			pSprite.xvel = (short) (sPlayerInput[player].xvel >> 14);
			pSprite.yvel = (short) (sPlayerInput[player].yvel >> 14);
			if (sPlayerInput[player].field_F > -1) {
				UseItem(player, sPlayerInput[player].field_F);
				sPlayerInput[player].field_F = -1;
			}

			pDSprite.picnum = pSprite.picnum = (short) GetSeqPicnum(PlayerList[player].seq,
					ActionSeq[nHeightTemplate[anim]].seq, PlayerList[player].animCount);

			int torch = nPlayerTorch[player];
			if (torch > 0) {
				nPlayerTorch[player] = (short) (torch - 1);
				if (torch == 1) {
					SetTorch(player, 0);
				} else if (player != nLocalPlayer) {
					nFlashDepth = 5;
					AddFlash(pSprite.sectnum, pSprite.x, pSprite.y, pSprite.z, 0);
					nFlashDepth = 2;
				}
			} else if(player == nLocalPlayer)
				bTorch = 0;

			short dble = nPlayerDouble[player];
			if (dble > 0) {
				nPlayerDouble[player] = (short) (dble - 1);
				if (nPlayerDouble[player] == 150 && player == nLocalPlayer)
					PlayAlert("WEAPON POWER IS ABOUT TO EXPIRE", player);
			}

			int invis = nPlayerInvisible[player];
			if (invis > 0) {
				nPlayerInvisible[player] = (short) (invis - 1);
				if (invis == 1) {
					pSprite.cstat &= 0x7FFF;
					int fl = nPlayerFloorSprite[player];
					if (fl > -1)
						sprite[player].cstat &= 0x7FFF;
				} else if (nPlayerInvisible[player] == 150 && player == nLocalPlayer) {
					PlayAlert("INVISIBILITY IS ABOUT TO EXPIRE", player);
				}
			}

			int god = PlayerList[player].invisibility;
			if (god > 0) {
				PlayerList[player].invisibility = (short) (god - 1);
				if (PlayerList[player].invisibility == 150 && player == nLocalPlayer)
					PlayAlert("INVINCIBILITY IS ABOUT TO EXPIRE", player);
			}

			int quake = nQuake[player];
			if (quake != 0) {
				nQuake[player] = (short) -quake;
				if (nQuake[player] >= 0 && nQuake[player] != 0) {
					nQuake[player] = (short) (nQuake[player] - 512);
					if ((-quake) - 512 < 0)
						nQuake[player] = 0;
				}
			}

			PlayerList[player].ang = BClampAngle(PlayerList[player].ang + (4 * sPlayerInput[player].avel));
			pSprite.ang = (short) PlayerList[player].ang;

			short ozvel = pSprite.zvel;
			Gravity(spr);

			if (pSprite.zvel >= 6500 && ozvel < 6500) 
				D3PlayFX(StaticSound[17], spr);

			short osectnum = pSprite.sectnum;
			boolean oUnderwater = (SectFlag[nPlayerViewSect[player]] & 0x2000) != 0;
			 
			int sx = pSprite.x;
			int sy = pSprite.y;
			int sz = pSprite.z;

			int xvel = 4 * sPlayerInput[player].xvel >> 2;
			int yvel = 4 * sPlayerInput[player].yvel >> 2;
			int zvel = 4 * pSprite.zvel >> 2;

			if (pSprite.zvel > 0x2000)
				pSprite.zvel = 0x2000;
			ozvel = pSprite.zvel;

			if (PlayerList[player].mummified != 0) {
				xvel /= 2;
				yvel /= 2;
			}

			int moveHit = 0;
			if (bSlipMode) {
				pSprite.x += (xvel >> 14);
				pSprite.y += (yvel >> 14);
				engine.setsprite(spr, pSprite.x, pSprite.y, pSprite.z);
				pSprite.z = sector[pSprite.sectnum].floorz;
			} else {
				moveHit = engine.movesprite(spr, xvel, yvel, zvel, 5120, -5120, 0);
				engine.pushmove(pSprite.x, pSprite.y, pSprite.z, pSprite.sectnum, 4 * pSprite.clipdist, 5120, -5120, 0);

				pSprite.x = pushmove_x;
				pSprite.y = pushmove_y;
				pSprite.z = pushmove_z;
				
				if (isValidSector(pushmove_sectnum) && pSprite.sectnum != pushmove_sectnum)
					engine.mychangespritesect(spr, pushmove_sectnum);

				if (engine.inside(pSprite.x, pSprite.y, pSprite.sectnum) == 0) {
					engine.mychangespritesect(spr, osectnum);
					pSprite.x = sx;
					pSprite.y = sy;
					if (ozvel < pSprite.zvel)
						pSprite.zvel = ozvel;
				}
			}

			boolean bUnderwater = (SectFlag[pSprite.sectnum] & 0x2000) != 0;

			if (bUnderwater) {
				int v48 = nXDamage[player]; // XXX WTF?
				nXDamage[player] = (v48 - (v48 & 0xFF00)) >> 1;
				int v49 = nYDamage[player];
				nYDamage[player] = (v49 - (v49 & 0xFF00)) >> 1;
			}

			if ((SectFlag[pSprite.sectnum] & 0x8000) != 0 && bTouchFloor[0]) {
				if (numplayers <= 1) {
					PlayerList[player].ang = pSprite.ang = GetAngleToSprite(spr, nSpiritSprite);
					pSprite.zvel = 0;
					pSprite.yvel = 0;
					pSprite.xvel = pSprite.yvel;
					if (nFreeze < 1) {
						nFreeze = 1;
						StopAllSounds();
						StopLocalSound();
						InitSpiritHead();
						nDestVertPan[player] = 92;
						if (levelnum == 11)
							nDestVertPan[player] = 138;
						else
							nDestVertPan[player] = 103;
					}
				} else
					FinishLevel();
				return;
			}

			if ((moveHit & 0x3C000) != 0) {
				if (bTouchFloor[0]) {
					int v53 = nXDamage[player]; // XXX
					nXDamage[player] = (v53 - (v53 & 0xFF00)) >> 1;
					int v54 = nYDamage[player];
					nYDamage[player] = (v54 - (v54 & 0xFF00)) >> 1;
					if (player == nLocalPlayer) {
						if (!cfg.gMouseAim && klabs(ozvel) > 512)
							nDestVertPan[player] = 92;
					}

					if (ozvel >= 6500) {
						pSprite.xvel >>= 2;
						pSprite.yvel >>= 2;
						DamageEnemy(spr, -1, ((ozvel - 6500) >> 7) + 10);
						if (PlayerList[player].HealthAmount > 0) {
							D3PlayFX(StaticSound[27] | 0x2000, spr);
						} else {
							pSprite.xvel = pSprite.yvel = 0;
							StopSpriteSound(spr);
							PlayFXAtXYZ(StaticSound[60], pSprite.x, pSprite.y, pSprite.z, pSprite.sectnum | 0x4000);
						}
					}
				}

				int hitobject = -1;
				switch (moveHit & 0xC000) {
				case 0x4000: // hit a ceiling or floor
					hitobject = moveHit & 0x3FFF;
					break;
				case 0x8000: // hit a wall
					hitobject = wall[moveHit & 0x3FFF].nextsector;
					break;
				}

				if (hitobject >= 0 && sector[hitobject].hitag == 45 && bTouchFloor[0] && klabs(
						AngleDiff(engine.GetWallNormal(moveHit & 0x3FFF), (pSprite.ang + 1024) & 0x7FF)) <= 256) {
					nPlayerPushSect[player] = (short) hitobject;
					int xv = sPlayerInput[player].xvel;
					int yv = sPlayerInput[player].yvel;
					Vector2 out = MoveSector(hitobject, engine.GetMyAngle(xv, yv), xv, yv);
					xv = (int) out.x;
					yv = (int) out.y;

					if (nPlayerPushSound[player] == -1) {
						nPlayerPushSound[player] = sector[hitobject].extra;
						D3PlayFX(StaticSound[23], sBlockInfo[sector[hitobject].extra].sprite | 0x4000);
					} else {
						pSprite.x = sx;
						pSprite.y = sy;
						pSprite.z = sz;
						engine.mychangespritesect(spr, osectnum);
					}
					engine.movesprite(spr, xv, yv, zvel, 5120, -5120, 0);
				} else {
					if (nPlayerPushSound[player] != -1) {
						if (nPlayerPushSect[player] != -1) {
							StopSpriteSound(sBlockInfo[nPlayerPushSound[player]].sprite);
						}
						nPlayerPushSound[player] = -1;
					}
				}
			}

			if (!bPlayerPan && !bLockPan) {
				int v74 = ((sz - pSprite.z) / 32) + 92;
				nDestVertPan[player] = (short) BClipRange(v74, 0, 183);
			}

			int dx = sx - pSprite.x;
			int dy = sy - pSprite.y;
			totalvel[player] = engine.ksqrt(dx * dx + dy * dy);

			short sectnum = pSprite.sectnum;
			int z = nQuake[player] + pSprite.z + PlayerList[player].eyelevel;
			if (z < sector[sectnum].ceilingz && SectAbove[sectnum] > -1)
				sectnum = SectAbove[sectnum];
			
			if (bUnderwater) {
				short osect = pSprite.sectnum;
				if (sectnum != osect && (moveHit & 0xC000) == 0x8000) // if hit wall
				{
					int ox = pSprite.x;
					int oy = pSprite.y;
					int oz = pSprite.z;
					engine.mychangespritesect(spr, sectnum);
					pSprite.x = sx;
					pSprite.y = sy;
					int fz = pSprite.z = -5120 + sector[sectnum].floorz;

					if ((engine.movesprite(spr, xvel, yvel, 0, 5120, 0, 0) & 0xC000) == 0x8000) {
						engine.mychangespritesect(spr, osect);
						pSprite.x = ox;
						pSprite.y = oy;
						pSprite.z = oz;
					} else {
						pSprite.z = fz - 256;
						D3PlayFX(StaticSound[42], spr);
					}
				}
			}
	
			nPlayerViewSect[player] = sectnum;
			nPlayerDX[player] = pSprite.x - sx;
			nPlayerDY[player] = pSprite.y - sy;
			
			boolean bViewUnderwater = (SectFlag[sectnum] & 0x2000) != 0;
			if (PlayerList[player].HealthAmount <= 0) {
				
				if(!gClassicMode && anim >= 16)
				{
					if(message_timer <= 0) {
						if(game.isCurrentScreen(gGameScreen) && lastload != null && !lastload.isEmpty() && BuildGdx.compat.checkFile(lastload, Path.User) != null)
						{
							StatusMessage(5000, deathMessage1, player);
						} else StatusMessage(5000, deathMessage2 , player);
					}
					
					if(game.isCurrentScreen(gGameScreen) && lastload != null && !lastload.isEmpty())
					{
						if (game.pInput.ctrlKeyStatusOnce(Keys.ENTER)) {
							game.changeScreen(gLoadingScreen.setTitle(lastload));
							gLoadingScreen.init(new Runnable() {
								public void run() {
									if(!loadgame(lastload)) {
										game.GameMessage("Can't load game!");
										game.show();
									}
								}
							});
						}
					}
				}
				
				if ((sPlayerInput[player].bits & nOpen) != 0) {
					if (anim >= 16) {
						if(!gClassicMode)
						{
							BuildGdx.app.postRunnable(new Runnable() {
								@Override
								public void run() {
									gGameScreen.newgame(mUserFlag == UserFlag.UserMap ? BuildGdx.compat.checkFile(boardfilename) : gCurrentEpisode, levelnum, false);
								}
							});
							return;
						}
						else
						{
							if (player == nLocalPlayer) {
								StopAllSounds();
								StopLocalSound();
								GrabPalette();
							}
							PlayerList[player].currentWeapon = nPlayerOldWeapon[player];
							if (nPlayerLives[player] != 0 && nNetTime != 0) {
								if (anim != 20) {
									pSprite.picnum = (short) GetSeqPicnum(25, 120, 0);
									pSprite.cstat = 0;
									pSprite.z = sector[pSprite.sectnum].floorz;
								}
								RestartPlayer(player);
								dspr = nDoppleSprite[player];
								pSprite = sprite[PlayerList[player].spriteId];
								pDSprite = sprite[nDoppleSprite[player]];
							} else {
								if (levelnum == 20)
									DoFailedFinalScene();
								else
									DoGameOverScene();
							}
						}
					}
				}
			} else {

				if ((sPlayerInput[player].bits & nTurnAround) != 0) {
					if (PlayerList[player].turnAround == 0)
						PlayerList[player].turnAround = -1024;
				}

				if (PlayerList[player].turnAround < 0) {
					PlayerList[player].turnAround = (short) BClipHigh(PlayerList[player].turnAround + 128, 0);
					PlayerList[player].ang = BClampAngle(PlayerList[player].ang + 128);
					pSprite.ang = (short) PlayerList[player].ang;
				}

				if (PlayerList[player].AirMaskAmount > 0) {
					PlayerList[player].AirMaskAmount--;
					if (PlayerList[player].AirMaskAmount == 150 && player == nLocalPlayer)
						PlayAlert("MASK IS ABOUT TO EXPIRE", player);
				}

				if (PlayerList[player].invisibility == 0) {
					nBreathTimer[player]--;
					if (nBreathTimer[player] <= 0) {
						nBreathTimer[player] = 90;
						if (bViewUnderwater) {
							if (PlayerList[player].AirMaskAmount <= 0) {
								PlayerList[player].AirAmount -= 25;
								if (PlayerList[player].AirAmount <= 0) {
									PlayerList[player].HealthAmount += 4 * PlayerList[player].AirAmount;
									if (PlayerList[player].HealthAmount <= 0) {
										PlayerList[player].HealthAmount = 0;
										StartDeathSeq(player, 0);
									}
									if (player == nLocalPlayer)
										SetHealthFrame(-1);
									PlayerList[player].AirAmount = 0;
									if (PlayerList[player].HealthAmount >= 300) {
										D3PlayFX(StaticSound[19], spr);
									} else {
										D3PlayFX(StaticSound[79], spr);
									}
								} else
									D3PlayFX(StaticSound[25], spr);
							} else {
								if (player == nLocalPlayer)
									BuildStatusAnim(132, 0);
								D3PlayFX(StaticSound[30] | 0x1000, spr);
								PlayerList[player].AirAmount = 100;
							}
							DoBubbles(player);
							SetAirFrame();
						} else {
							if (player == nLocalPlayer)
								BuildStatusAnim(132, 0);
						}
					}
				}

				if (bViewUnderwater) {
					if (nPlayerTorch[player] > 0) {
						nPlayerTorch[player] = 0;
						SetTorch(player, 0);
					}
				} else {
					if (totalvel[player] > 25) {
						if (pSprite.z > sector[pSprite.sectnum].floorz && SectDepth[pSprite.sectnum] != 0
								&& SectSpeed[pSprite.sectnum] == 0 && SectDamage[pSprite.sectnum] == 0)
							D3PlayFX(StaticSound[42], spr);
					}
					
					if (oUnderwater) {
						if (PlayerList[player].AirAmount < 50)
							D3PlayFX(StaticSound[14], spr);
						nBreathTimer[player] = 1;
					}
					nBreathTimer[player]--;
					if (nBreathTimer[player] <= 0) {
						nBreathTimer[player] = 90;
						if (player == nLocalPlayer)
							BuildStatusAnim(132, nLocalPlayer ^ player);
					}
					if (PlayerList[player].AirAmount < 100) {
						PlayerList[player].AirAmount = 100;
						SetAirFrame();
					}
				}
				if (numplayers > 1) {
					SPRITE fspr = sprite[nPlayerFloorSprite[player]];
					fspr.x = pSprite.x;
					fspr.y = pSprite.y;
					if (fspr.sectnum != pSprite.sectnum)
						engine.mychangespritesect(nPlayerFloorSprite[player], pSprite.sectnum);
					fspr.z = sector[pSprite.sectnum].floorz;
				}

				int flags = 0;
				if (PlayerList[player].HealthAmount >= 800)
					flags = 2;
				if (PlayerList[player].MagicAmount >= 1000)
					flags |= 1;

				engine.neartag(pSprite.x, pSprite.y, pSprite.z, pSprite.sectnum, pSprite.ang, neartag, 1024, 2);
				int v231 = feebtag(pSprite.x, pSprite.y, pSprite.z, pSprite.sectnum, flags, 768);

				if (v231 >= 0) {
					int nSnd = 9;
					int tintGreen = 16;
					int tintRed = 0;
					int pickFlag = 0;
					boolean magicPickuped = false;
					int healthPickuped = -65536;

					int statnum = sprite[v231].statnum;
					if (statnum >= 900) {
						int statBase = sprite[v231].statnum - 900;
						switch (statnum - 906) {
						case 0:
							if (!AddAmmo(player, 1, sprite[v231].hitag))
								break;
							nSnd = StaticSound[69];
							pickFlag = 3;
							break;
						case 1:
							if (!AddAmmo(player, 3, sprite[v231].hitag))
								break;
							nSnd = StaticSound[69];
							pickFlag = 3;
							break;
						case 2:
							if (!AddAmmo(player, 2, sprite[v231].hitag))
								break;
							nSnd = StaticSound[69];
							pickFlag = 3;
							break;
						case 4:
						case 9:
						case 10:
						case 18:
						case 25:
						case 28:
						case 29:
						case 30:
						case 33:
						case 34:
						case 35:
						case 36:
						case 37:
						case 38:
						case 45:
						case 52:
							pickFlag = 3;
							break;
						case 3:
						case 21:
						case 49:
							if (!AddAmmo(player, 4, 1))
								break;

							nSnd = StaticSound[69];
							if ((nPlayerWeapons[player] & 0x10) == 0) {
								nPlayerWeapons[player] |= 0x10;
								SetNewWeaponIfBetter(player, 4);
							}
							if (statBase != 55) {
								pickFlag = 3;
								break;
							}

							sprite[v231].cstat = (short) 32768;
							DestroyItemAnim(v231);
							pickFlag = 2;
							break;
						case 5:
							GrabMap();
							pickFlag = 3;
							break;
						case 6:
						case 7:
						case 8:
							if (sprite[v231].hitag == 0)
								break;

							nSnd = 20;
							int v123 = 40;
							int v115 = 1;
							switch (statnum - 906) {
							case 7:
								v123 = 160;
								v115 = 1;
								break;
							case 8:
								v115 = -1;
								v123 = -200;
								break;
							}
							if (v123 > 0 && (flags & 2) != 0)
								break;

							if (PlayerList[player].invisibility == 0 || v123 > 0) {
								PlayerList[player].HealthAmount += v123;
								healthPickuped = v123;
								if (PlayerList[player].HealthAmount <= 800) {
									if (PlayerList[player].HealthAmount <= 0) {
										nSnd = -1;
										StartDeathSeq(player, 0);
									}
								} else {
									PlayerList[player].HealthAmount = 800;
								}
							}
							if (player == nLocalPlayer)
								SetHealthFrame(v115);
							if (statBase == 12) {
								sprite[v231].hitag = 0;
								sprite[v231].picnum++;
								engine.changespritestat((short) v231, (short) 0);
								pickFlag = 2;
								break;
							}
							if (statBase == 14) {
								tintRed = tintGreen;
								tintGreen = 0;
								nSnd = 22;
							} else
								nSnd = 21;
							pickFlag = 3;
							break;
						case 11:
							PlayerList[player].AirAmount = (short) BClipHigh(PlayerList[player].AirAmount + 10, 100);
							SetAirFrame();
							if (nBreathTimer[player] < 89)
								D3PlayFX(StaticSound[13], spr);
							nBreathTimer[player] = 90;
							break;
						case 12:
						case 13:
						case 14:
						case 15:
						case 16:
						case 17:
							int v130 = 0;
							switch (statnum - 906) {
							case 13:
								v130 = 1;
								break;
							case 14:
								v130 = 3;
								break;
							case 15:
								v130 = 4;
								break;
							case 16:
								v130 = 2;
								break;
							case 17:
								v130 = 5;
								break;
							}
							if (GrabItem(player, v130))
								pickFlag = 3;
							break;
						case 19:
							nSnd = -1;
							if (nPlayerLives[player] >= 5)
								break;
							nPlayerLives[player]++;
							if (player == nLocalPlayer)
								BuildStatusAnim(2 * (nPlayerLives[player] - 1) + 146, 0);
							tintGreen = 32;
							tintRed = 32;
							pickFlag = 3;
							break;
						case 20:
						case 22:
						case 46:
						case 23:
						case 47:
						case 24:
						case 48:
						case 26:
						case 50:
						case 27:
						case 51:
							int v251 = 1;
							int v136 = 0;
							switch (statnum - 906) {
							case 20:
								v136 = 0;
								v251 = 0;
								break;
							case 22:
							case 46:
								v251 = 1;
								v136 = 6;
								break;
							case 23:
							case 47:
								v136 = 24;
								v251 = 2;
								break;
							case 24:
							case 48:
								v136 = 100;
								v251 = 3;
								break;
							case 26:
							case 50:

								v136 = 20;
								v251 = 5;
								break;
							case 27:
							case 51:
								v251 = 6;
								v136 = 2;
								break;
							}

							int v261 = 1 << v251;
							if ((nPlayerWeapons[player] & (1 << v251)) != 0) {
								if (levelnum > 20)
									AddAmmo(player, weaponInfo[1].field_1A, v136);
							} else {
								SetNewWeaponIfBetter(player, v251);
								nPlayerWeapons[player] |= v261;
								AddAmmo(player, weaponInfo[v251].field_1A, v136);
								nSnd = StaticSound[72];
							}

							if (v251 == 2)
								CheckClip(player);

							if (statBase <= 50) {
								pickFlag = 3;
								break;
							}

							sprite[v231].cstat = (short) 32768;
							DestroyItemAnim(v231);
							pickFlag = 2;
							break;
						case 31:
							if (!AddAmmo(player, 5, 1))
								break;
							nSnd = StaticSound[69];
							pickFlag = 3;
							break;
						case 32:
							if (!AddAmmo(player, 6, sprite[v231].hitag))
								break;
							nSnd = StaticSound[69];
							pickFlag = 3;
							break;
						case 39:
						case 40:
						case 41:
						case 42:
							int v140 = 0;
							switch (statnum - 906) {
							case 39:
								v140 = 0;
								break;
							case 40:
								v140 = 1;
								break;
							case 41:
								v140 = 2;
								break;
							case 42:
								v140 = 3;
								break;
							}
							nSnd = -1;
							if (v140 != 0)
								v115 = 4096 << v140;
							else
								v115 = 4096;

							if ((PlayerList[player].KeysBitMask & v115) != 0)
								break;
							if (player == nLocalPlayer)
								BuildStatusAnim(2 * v140 + 36, nLocalPlayer ^ player);
							PlayerList[player].KeysBitMask |= v115;
							if (numplayers <= 1) {
								pickFlag = 3;
								break;
							}

							pickFlag = 2;
							break;
						case 43:
						case 44:
							if (PlayerList[player].MagicAmount >= 1000)
								break;

							nSnd = StaticSound[67];
							PlayerList[player].MagicAmount = (short) BClipHigh(PlayerList[player].MagicAmount + 100,
									1000);
							if (player == nLocalPlayer)
								SetMagicFrame();
							pickFlag = 3;
							magicPickuped = true;
							break;
						case 53:
							if (player == nLocalPlayer) {
								int v144 = sprite[v231].owner;
								AnimList[v144].nAction++;
								AnimFlags[v144] &= 0xEF;
								AnimList[v144].nSeq = 0;
								engine.changespritestat((short) v231, (short) 899);
							}

							SetSavePoint(player, pSprite.x, pSprite.y, pSprite.z, pSprite.sectnum, pSprite.ang);
							break;
						case 54:
							if (bInDemo) {
//								KB_Addch(32);
							} else
								FinishLevel();
							DestroyItemAnim(v231);
							engine.mydeletesprite((short) v231);
							break;
						}

						if ((pickFlag & 1) != 0) {
							if (levelnum <= 20 || statBase == 25 || statBase == 50) {
								DestroyItemAnim(v231);
								engine.mydeletesprite((short) v231);
							} else
								StartRegenerate(v231);
						}

						if ((pickFlag & 2) != 0) {
							if (player == nLocalPlayer) {
								if (nItemText[statBase] > -1/* && numplayers == 1*/) {
									String message = gString[nItemText[statBase] + nItemTextIndex];
									if(magicPickuped) 
										message += " +10%";
									else if(healthPickuped != -65536) {
										if(healthPickuped > 0)
											message += " +";
										else message += " ";
										message += (((healthPickuped * 100) / 800)) + "%";
									}
									
									StatusMessage(400, message, player);
								}
								TintPalette(tintRed, tintGreen, 0);
								if (nSnd > -1)
									PlayLocalSound(nSnd, 0);
							}
						}
					}
				}

				if (bTouchFloor[0]) {
					if (sector[pSprite.sectnum].lotag > 0)
						SignalRun(sector[pSprite.sectnum].lotag - 1, 0x50000 | player);
				}

				if (osectnum != pSprite.sectnum) {
					if (sector[osectnum].lotag > 0)
						SignalRun(sector[osectnum].lotag - 1, nEvent6 | player);
					if (sector[pSprite.sectnum].lotag > 0)
						SignalRun(sector[pSprite.sectnum].lotag - 1, 0x60000 | player);
				}

				if (PlayerList[player].mummified != 0) {
					if ((sPlayerInput[player].bits & nFire) != 0)
						FireWeapon(player);
					if (anim != 15) {
						if (totalvel[player] <= 1)
							next_anim = 13;
						else
							next_anim = 14;
					}
				} else {
					if ((sPlayerInput[player].bits & nOpen) != 0) {
						if (neartag.tagwall >= 0) {
							if (wall[neartag.tagwall].lotag > 0)
								SignalRun(wall[neartag.tagwall].lotag - 1, 0x40000 | player);
						}
						if (neartag.tagsector >= 0) {
							if (sector[neartag.tagsector].lotag > 0)
								SignalRun(sector[neartag.tagsector].lotag - 1, 0x40000 | player);
						}
					}

					if ((sPlayerInput[player].bits & nFire) != 0)
						FireWeapon(player);
					else
						StopFiringWeapon(player);

					if (nStandHeight > sector[pSprite.sectnum].floorz - sector[pSprite.sectnum].ceilingz)
						v233 = 1;

					if ((sPlayerInput[player].bits & nJump) != 0) {
						PlayerList[player].crouch_toggle = false;
						if (bUnderwater) {
							pSprite.zvel = -2048;
							next_anim = 10;
						} else if (bTouchFloor[0] && (anim < 6 || anim > 8)) {
							pSprite.zvel = -3584;
							next_anim = 3;
						}
					} else if ((sPlayerInput[player].bits & nCrouch) != 0) {
						if (bUnderwater) {
							pSprite.zvel = 2048;
							next_anim = 10;
						} else {
							int elev = PlayerList[player].eyelevel;
							if (elev < -8320)
								PlayerList[player].eyelevel = (short) (((-8320 - elev) >> 1) + elev);
							if (totalvel[player] >= 1)
								next_anim = 7;
							else
								next_anim = 6;
						}
					} else {
						if (PlayerList[player].HealthAmount > 0) {
							PlayerList[player].eyelevel += (nActionEyeLevel[anim] - PlayerList[player].eyelevel) >> 1;
							if (bUnderwater) {
								if (totalvel[player] <= 1)
									next_anim = 9;
								else
									next_anim = 10;
							} else {
								if (v233 != 0) {
									if (totalvel[player] >= 1)
										next_anim = 7;
									else
										next_anim = 6;
								} else {
									if (totalvel[player] <= 1) {
										next_anim = bUnderwater ? 1 : 0;
									} else if (totalvel[player] <= 30) {
										next_anim = 2;
									} else {
										next_anim = 1;
									}
								}
							}
						}

						if (v233 == 0 && (sPlayerInput[player].bits & nFire) != 0) {
							if (bUnderwater) {
								next_anim = 11;
							} else if (next_anim != 2 && next_anim != 1) {
								next_anim = 5;
							}
						}
					}

					int newWeapon = ((sPlayerInput[player].bits >> 13) & 0xF) - 1;
					if (newWeapon != -1) {

						switch (newWeapon) {
						case 9: // last weapon
							if (((1 << PlayerList[player].lastUsedWeapon) & nPlayerWeapons[player]) != 0)
								SetNewWeapon(player, PlayerList[player].lastUsedWeapon);
							break;
						case 7: // prev
						case 8: // next
							int weap = WeaponChange(player, newWeapon == 8);
							SetNewWeapon(player, weap);
							break;
						default:
							if (((1 << newWeapon) & nPlayerWeapons[player]) != 0)
								SetNewWeapon(player, newWeapon);
							break;
						}
					}
				}

				if (next_anim != anim && anim != 4) {
					anim = next_anim;
					PlayerList[player].anim_ = (short) next_anim;
					PlayerList[player].animCount = 0;
				}

				if (player == nLocalPlayer) {
					if ((sPlayerInput[player].bits & nLookUp) != 0) {
						bLockPan = true;
						if (PlayerList[player].horiz >= (isOriginal() ? MAXOHORIZ : MAXHORIZ)) {
							nDestVertPan[player] = PlayerList[player].horiz;
						} else {
							PlayerList[player].horiz += 4;
							nDestVertPan[player] = PlayerList[player].horiz;
						}
					} else if ((sPlayerInput[player].bits & nLookDown) != 0) {
						bLockPan = true;
						bPlayerPan = true;
						if (PlayerList[player].horiz > ((isOriginal() ? MINOHORIZ : MINHORIZ) + 4))
							PlayerList[player].horiz -= 4;
						nDestVertPan[player] = PlayerList[player].horiz;
					} else if ((sPlayerInput[player].bits & nAimDown) != 0) {
						bLockPan = false;
						bPlayerPan = false;
						if (PlayerList[player].horiz > ((isOriginal() ? MINOHORIZ : MINHORIZ) + 4))
							PlayerList[player].horiz -= 4;
						nDestVertPan[player] = PlayerList[player].horiz;
					} else if ((sPlayerInput[player].bits & nAimUp) != 0) {
						bLockPan = false;
						bPlayerPan = false;
						if (PlayerList[player].horiz >= (isOriginal() ? MAXOHORIZ : MAXHORIZ)) {
							nDestVertPan[player] = PlayerList[player].horiz;
						} else {
							PlayerList[player].horiz += 4;
							nDestVertPan[player] = PlayerList[player].horiz;
						}
					} else if ((sPlayerInput[player].bits & nAimCenter) != 0) {
						bLockPan = false;
						bPlayerPan = false;
						nDestVertPan[player] = 92;
					}

					if (sPlayerInput[player].horiz != 0) {
						bLockPan = true;
						bPlayerPan = true;
						if(isOriginal())
							PlayerList[player].horiz = BClipRange(PlayerList[player].horiz + sPlayerInput[player].horiz, MINOHORIZ, MAXOHORIZ);
						else PlayerList[player].horiz = BClipRange(PlayerList[player].horiz + sPlayerInput[player].horiz, MINHORIZ, MAXHORIZ);
						nDestVertPan[player] = PlayerList[player].horiz;
					}
					sPlayerInput[player].nWeaponAim = (short) PlayerList[player].horiz;

					if (totalvel[player] > 20)
						bPlayerPan = false;

					int dVertPan = (int) (nDestVertPan[player] - PlayerList[player].horiz);
					if (dVertPan != 0) {
						int val = dVertPan / 4;
						if (klabs(val) >= 4) {
							if (val >= 4)
								PlayerList[player].horiz += 4;
							else if (val <= -4)
								PlayerList[player].horiz -= 4;
						} else
							PlayerList[player].horiz += dVertPan / 2.0f;
					}
				}
			}

			if (player == nLocalPlayer) {
				nLocalEyeSect = nPlayerViewSect[nLocalPlayer];
				CheckAmbience(nLocalEyeSect);
			}
			int seqBase = (ActionSeq[anim].seq + SeqOffsets[PlayerList[player].seq]);
			MoveSequence(spr, seqBase, PlayerList[player].animCount);
			PlayerList[player].animCount++;
			if (PlayerList[player].animCount >= SeqSize[seqBase]) {
				PlayerList[player].animCount = 0;
				switch (PlayerList[player].anim_) {
				case 3:
					PlayerList[player].animCount = (short) (SeqSize[seqBase] - 1);
					break;
				case 4:
					PlayerList[player].anim_ = 0;
					break;
				case 16:
					PlayerList[player].animCount = (short) (SeqSize[seqBase] - 1);
					if (pSprite.z < sector[pSprite.sectnum].floorz)
						pSprite.z += 256;
					if (RandomSize(5) == 0) {
						int[] out = WheresMyMouth(player);
						BuildAnim(-1, 71, 0, out[0], out[1], pSprite.z + 3840, out[3], 75, -128);
					}
					break;
				case 17:
					PlayerList[player].anim_ = 18;
					break;
				case 19:
					pSprite.cstat |= 0x8000;
					PlayerList[player].anim_ = 20;
					break;
				}
			}

			if (player == nLocalPlayer) {
				initx = pSprite.x;
				inity = pSprite.y;
				initz = pSprite.z;
				initsect = pSprite.sectnum;
				inita = pSprite.ang;
			}

			if (PlayerList[player].HealthAmount == 0) {
				nYDamage[player] = 0;
				nXDamage[player] = 0;
				if (PlayerList[player].eyelevel >= -2816) {
					PlayerList[player].eyelevel = -2816;
					dVertPan[player] = 0;
				} else {
					if (PlayerList[player].horiz >= 92) {
						PlayerList[player].horiz += dVertPan[player];
						if (PlayerList[player].horiz < 200) {
							if (PlayerList[player].horiz <= 92) {
								if ((SectFlag[pSprite.sectnum] & 0x2000) == 0)
									SetNewWeapon(player, nDeathType[player] + 8);
							}
						} else {
							PlayerList[player].horiz = 199;
						}
						--dVertPan[player];
					} else {
						PlayerList[player].horiz = 91;
						PlayerList[player].eyelevel -= dVertPan[player] << 8;
					}
				}
			}
			pDSprite.x = pSprite.x;
			pDSprite.y = pSprite.y;
			pDSprite.z = pSprite.z;
			if (SectAbove[pSprite.sectnum] <= -1) {
				pDSprite.cstat = (short) 32768;
			} else {
				pDSprite.ang = pSprite.ang;
				engine.mychangespritesect((short) dspr, SectAbove[pSprite.sectnum]);
				pDSprite.cstat = 257;
			}
			MoveWeapons(player);
			return;
		case nEventView:
			PlotSequence((short) (a1 & 0xFFFF), ActionSeq[anim].seq + SeqOffsets[PlayerList[player].seq],
					PlayerList[player].animCount, ActionSeq[anim].a2 ? 1 : 0);
			return;

		case nEventDamage:
			if (a2 == 0)
				return;
			v229 = (short) (a1 & 0xFFFF);
			break;
		case nEventRadialDamage:
			if (PlayerList[player].HealthAmount > 0) {
				if ((a2 = CheckRadialDamage(spr)) == 0)
					return;
				v229 = (short) nRadialOwner;
			}
			break;
		default:
			return;
		}

		if (PlayerList[player].HealthAmount != 0) {
			if (PlayerList[player].invisibility == 0) {
				PlayerList[player].HealthAmount -= a2;
				if (player == nLocalPlayer) {
					TintPalette(a2 >> 2, 0, 0);
					SetHealthFrame(-1);
				}
			}

			if (PlayerList[player].HealthAmount > 0) {
				if (a2 > 40 || (totalmoves & 0xF) < 2) {
					if (PlayerList[player].invisibility == 0) {
						if ((SectFlag[pSprite.sectnum] & 0x2000) != 0) {
							if (anim != 12) {
								PlayerList[player].animCount = 0;
								PlayerList[player].anim_ = 12;
							}
						} else if (anim != 4) {
							PlayerList[player].animCount = 0;
							PlayerList[player].anim_ = 4;
							if (v229 > -1) {
								nPlayerSwear[player]--;
								if (nPlayerSwear[player] <= 0) {
									D3PlayFX(StaticSound[52], dspr);
									nPlayerSwear[player] = (short) (RandomSize(3) + 4);
								}
							}
						}
					}
				}
			} else {
				if (v229 < 0)
					nPlayerScore[player]--;
//				else  XXX Crash v229 == 20811

				int deathType;
				if ((a1 & 0x7F0000) == 0xA0000) {
					for (int i = 122; i <= 131; i++)
						BuildCreatureChunk(spr, GetSeqPicnum(25, i, 0));
					deathType = 1;
				} else
					deathType = 0;
				StartDeathSeq(player, deathType);
			}
		}
	}

	public static void RestorePlayer(final short player) {
		BuildGdx.app.postRunnable(new Runnable() {
			@Override
			public void run() {
				if (nNetPlayerCount == 0 && lastlevel == levelnum) {
					RestoreSavePoint(player);
					RestartPlayer(player);
					InitPlayerKeys(player);
				}
			}
		});
	}

	public static void SetCounter(int value) {
		if (value <= 999) {
			if (value < 0)
				value = 0;
		} else
			value = 999;

		nCounterDest = value;
	}

	public static void SetPlayerMummified(int nPlayer, int mummified) {
		PlayerList[nPlayer].mummified = (short) mummified;
		int nSprite = PlayerList[nPlayer].spriteId;
		if (nSprite != -1 && sprite[nSprite] != null) {
			sprite[nSprite].xvel = 0;
			sprite[nSprite].yvel = 0;
		}
		if (mummified != 0) {
			PlayerList[nPlayer].anim_ = 13;
			PlayerList[nPlayer].seq = 10;
		} else {
			PlayerList[nPlayer].anim_ = 0;
			PlayerList[nPlayer].seq = 25;
		}

		PlayerList[nPlayer].animCount = 0;
	}

	public static void SetCounterImmediate(int a1) {
		SetCounter(a1);
		nCounter = nCounterDest;
		SetCounterDigits();
	}

	public static void SetCounterDigits() {
		nDigit[2] = 3 * (nCounter / 100 % 10);
		nDigit[1] = 3 * (nCounter / 10 % 10);
		nDigit[0] = 3 * (nCounter % 10);
	}

	public static void UseItem(int nPlayer, int nItem) {
		switch (nItem) {
		case 4:
			UseEye(nPlayer);
			break;
		case 5:
			UseMask(nPlayer);
			break;
		case 0:
			UseHeart(nPlayer);
			break;
		case 2:
			UseTorch(nPlayer);
			break;
		case 1:
			UseScarab(nPlayer);
			break;
		case 3:
			UseHand(nPlayer);
			break;
		}

		int amount = --PlayerList[nPlayer].ItemsAmount[nItem];
		if (nPlayer == nLocalPlayer)
			BuildStatusAnim(2 * amount + 156, nLocalPlayer ^ nPlayer);
		int i = nItem;
		if (amount == 0) {
			for (i = 0; i != 6 && PlayerList[nPlayer].ItemsAmount[i] <= 0; i++)
				;
			if (i == 6)
				i = -1;
		}
		PlayerList[nPlayer].MagicAmount -= nItemMagic[nItem];
		SetPlayerItem(nPlayer, i);
		if (nPlayer == nLocalPlayer)
			SetMagicFrame();
	}

	public static void UseEye(int nPlayer) {
		int nSprite = PlayerList[nPlayer].spriteId;
		if (nPlayerInvisible[nPlayer] >= 0)
			nPlayerInvisible[nPlayer] = 900;
		sprite[nSprite].cstat |= 0x8000;
		int nFloorSpr = nPlayerFloorSprite[nPlayer];
		if (nFloorSpr > -1)
			sprite[nFloorSpr].cstat |= 0x8000;

		if (nPlayer == nLocalPlayer) {
			ItemFlash();
			D3PlayFX(StaticSound[31], nSprite);
		}
	}

	public static void UseMask(int nPlayer) {
		PlayerList[nPlayer].AirMaskAmount = 1350;
		PlayerList[nPlayer].AirAmount = 100;
		if (nPlayer == nLocalPlayer) {
			SetAirFrame();
			D3PlayFX(StaticSound[31], PlayerList[nPlayer].spriteId);
		}
	}

	public static void UseHeart(int nPlayer) {
		if (PlayerList[nPlayer].HealthAmount < 800)
			PlayerList[nPlayer].HealthAmount = 800;
		if (nPlayer == nLocalPlayer) {
			ItemFlash();
			SetHealthFrame(1);
			D3PlayFX(StaticSound[31], PlayerList[nPlayer].spriteId);
		}
	}

	public static void UseScarab(int nPlayer) {
		if (PlayerList[nPlayer].invisibility < 900)
			PlayerList[nPlayer].invisibility = 900;
		if (nPlayer == nLocalPlayer) {
			ItemFlash();
			D3PlayFX(StaticSound[31], PlayerList[nPlayer].spriteId);
		}
	}

	public static void UseHand(int nPlayer) {
		nPlayerDouble[nPlayer] = 1350;
		if (nPlayer == nLocalPlayer) {
			ItemFlash();
			D3PlayFX(StaticSound[31], PlayerList[nPlayer].spriteId);
		}
	}

	public static void UseCurItem(int nPlayer, int nItem) {
		if (nItem >= 0 && PlayerList[nPlayer].ItemsAmount[nItem] > 0
				&& nItemMagic[nItem] <= PlayerList[nPlayer].MagicAmount) {
			sPlayerInput[nPlayer].field_F = (byte) nItem;

			System.err.println("Use item " + nItem);
		}
	}

	public static void SetNextItem(int a1) {
		int nItem = nPlayerItem[a1];
		int nNextItem = 6;
		for (; nNextItem > 0; nNextItem--) {
			if (++nItem == 6)
				nItem = 0;
			if (PlayerList[a1].ItemsAmount[nItem] != 0)
				break;
		}
		if (nNextItem > 0)
			SetPlayerItem(a1, nItem);
	}

	public static void SetPrevItem(int a1) {
		int nItem = nPlayerItem[a1];
		if (nItem == -1)
			return;

		int nNextItem = 6;
		for (; nNextItem > 0; nNextItem--) {
			if (--nItem < 0)
				nItem = 5;
			if (PlayerList[a1].ItemsAmount[nItem] != 0)
				break;
		}
		if (nNextItem > 0)
			SetPlayerItem(a1, nItem);
	}

	public static void ItemFlash() {
		TintPalette(4, 4, 4);
	}

	public static void FillItems(int nPlayer) {
		for (int i = 0; i < 6; i++)
			PlayerList[nPlayer].ItemsAmount[i] = 5;
		PlayerList[nPlayer].MagicAmount = 1000;
		if (nPlayer == nLocalPlayer) {
			ItemFlash();
			SetMagicFrame();
		}

		if (nPlayerItem[nPlayer] == -1)
			SetPlayerItem(nPlayer, 0);

		StatusMessage(750, "All items loaded for player " + nPlayer, nLocalPlayer);
	}

	public static void PlayAlert(String message, int nPlayer) {
		StatusMessage(300, message, nPlayer);
		PlayLocalSound(StaticSound[63], 0);
	}
}
