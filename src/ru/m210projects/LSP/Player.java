// This file is part of LSPGDX.
// Copyright (C) 2020  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// LSPGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// LSPGDX is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with LSPGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.LSP;

import static ru.m210projects.Build.Engine.CLIPMASK0;
import static ru.m210projects.Build.Engine.MAXPLAYERS;
import static ru.m210projects.Build.Engine.clipmove_sectnum;
import static ru.m210projects.Build.Engine.clipmove_x;
import static ru.m210projects.Build.Engine.clipmove_y;
import static ru.m210projects.Build.Engine.clipmove_z;
import static ru.m210projects.Build.Engine.getInput;
import static ru.m210projects.Build.Engine.globalvisibility;
import static ru.m210projects.Build.Engine.headspritestat;
import static ru.m210projects.Build.Engine.neartag;
import static ru.m210projects.Build.Engine.nextspritestat;
import static ru.m210projects.Build.Engine.numsectors;
import static ru.m210projects.Build.Engine.pushmove_sectnum;
import static ru.m210projects.Build.Engine.pushmove_x;
import static ru.m210projects.Build.Engine.pushmove_y;
import static ru.m210projects.Build.Engine.pushmove_z;
import static ru.m210projects.Build.Engine.sector;
import static ru.m210projects.Build.Engine.sintable;
import static ru.m210projects.Build.Engine.sprite;
import static ru.m210projects.Build.Engine.wall;
import static ru.m210projects.Build.Engine.zr_ceilz;
import static ru.m210projects.Build.Engine.zr_florhit;
import static ru.m210projects.Build.Engine.zr_florz;
import static ru.m210projects.Build.Gameutils.BClampAngle;
import static ru.m210projects.Build.Gameutils.BClipRange;
import static ru.m210projects.Build.Gameutils.isValidSector;
import static ru.m210projects.Build.Pragmas.klabs;
import static ru.m210projects.Build.Pragmas.mulscale;
import static ru.m210projects.LSP.Factory.LSPMenuHandler.AUDIOSET;
import static ru.m210projects.LSP.Factory.LSPMenuHandler.LASTSAVE;
import static ru.m210projects.LSP.Globals.INANIMATE;
import static ru.m210projects.LSP.Globals.PLAYER;
import static ru.m210projects.LSP.Globals.TICSPERFRAME;
import static ru.m210projects.LSP.Globals.gPlayer;
import static ru.m210projects.LSP.Globals.lockclock;
import static ru.m210projects.LSP.Globals.mapnum;
import static ru.m210projects.LSP.Globals.nDiffDoor;
import static ru.m210projects.LSP.Globals.nDiffDoorBack;
import static ru.m210projects.LSP.Globals.nDifficult;
import static ru.m210projects.LSP.Globals.nKickClock;
import static ru.m210projects.LSP.Globals.nKickSprite;
import static ru.m210projects.LSP.Globals.nPlayerFirstWeapon;
import static ru.m210projects.LSP.Globals.oldchoose;
import static ru.m210projects.LSP.Globals.oldpic;
import static ru.m210projects.LSP.Globals.waterfountaincnt;
import static ru.m210projects.LSP.Globals.waterfountainwall;
import static ru.m210projects.LSP.LoadSave.lastload;
import static ru.m210projects.LSP.Main.cfg;
import static ru.m210projects.LSP.Main.engine;
import static ru.m210projects.LSP.Main.gGameScreen;
import static ru.m210projects.LSP.Main.game;
import static ru.m210projects.LSP.Sectors.operatesector;
import static ru.m210projects.LSP.Sounds.playsound;
import static ru.m210projects.LSP.Sprites.changehealth;
import static ru.m210projects.LSP.Sprites.operatesprite;
import static ru.m210projects.LSP.Weapons.moveweapons;
import static ru.m210projects.LSP.Weapons.nextweapon;
import static ru.m210projects.LSP.Weapons.prevweapon;
import static ru.m210projects.LSP.Weapons.switchweapgroup;
import static ru.m210projects.LSP.Weapons.switchweapon;
import static ru.m210projects.LSP.Weapons.weaponbobbing;
import static ru.m210projects.LSP.Weapons.weaponfire;

import com.badlogic.gdx.Input.Keys;

import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.FileHandle.Compat.Path;
import ru.m210projects.Build.Types.BuildPos;
import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Build.Types.Tile;
import ru.m210projects.Build.Types.WALL;

public class Player {

	private static int word_5174F;
	private static int byte_58A94[] = new int[MAXPLAYERS];

	public static void InitPlayer(int plr, BuildPos startPos) {
		short spr = engine.insertsprite(startPos.sectnum, INANIMATE);
		gPlayer[plr].nSprite = spr;

		SPRITE pSprite = sprite[spr];

		pSprite.x = startPos.x;
		pSprite.y = startPos.y;
		pSprite.z = startPos.z + 0x2000;
		pSprite.cstat = 1;
		pSprite.picnum = PLAYER;
		pSprite.shade = 0;
		pSprite.xrepeat = 64;
		pSprite.yrepeat = 64;
		pSprite.ang = startPos.ang;
		pSprite.xvel = 0;
		pSprite.yvel = 0;
		pSprite.zvel = 0;
		pSprite.owner = (short) (plr * 4096);
		pSprite.lotag = 0;
		gPlayer[plr].nNewWeapon = 999;
	}

	public static boolean isonwater(int snum) {
		return gPlayer[snum].sectnum != -1
				&& (sector[gPlayer[snum].sectnum].lotag == 4 || sector[gPlayer[snum].sectnum].lotag == 5)
				&& klabs(gPlayer[snum].z - sector[gPlayer[snum].sectnum].floorz) <= 8192;
	}

	public static void processinput(int snum) {
		int i, nexti, xvect, yvect, goalz;

		if (gPlayer[snum].nHealth <= 0) {
			if (gPlayer[snum].horiz < 300)
				gPlayer[snum].horiz += 4;

			if ((sector[gPlayer[snum].sectnum].floorz - 1024) > gPlayer[snum].z)
				gPlayer[snum].z += 256;

			if (gPlayer[snum].horiz >= 200 && gPlayer[snum].nPlayerStatus != 1) {
				gPlayer[snum].nPlayerStatus = 1;
				gPlayer[snum].nWeaponState = 0;

				if (lastload != null && !lastload.isEmpty() && BuildGdx.compat.checkFile(lastload, Path.User) != null)
					game.menu.mOpen(game.menu.mMenus[LASTSAVE], -1);
				playsound(91);
			}

			if (game.isCurrentScreen(gGameScreen)) {
				if (getInput().keyPressed(Keys.ENTER) || (gPlayer[snum].pInput.bits & 0x400) != 0) {
					gPlayer[snum].nWeapon = nPlayerFirstWeapon;
					gPlayer[snum].nLastChoosedWeapon = 6;
					gPlayer[snum].nLastManaWeapon = 13;
					gPlayer[snum].nLastWeapon = 1;
					gGameScreen.newgame(mapnum);
				}
			}

			return;
		}

		if(nKickSprite != -1) {
			if (lockclock - nKickClock > 300) {
				playsound(93, nKickSprite);
				nKickClock = lockclock;
			}
		}

//		if (gPlayer[snum].nWeaponImpact != 0) {
//			int force = 3 * TICSPERFRAME * gPlayer[snum].nWeaponImpact;
//			xvect = mulscale(sintable[((int) gPlayer[snum].ang + 512) & 0x7FF], force, 4);
//			yvect = mulscale(sintable[((int) gPlayer[snum].ang) & 0x7FF], force, 4);
//
//			engine.clipmove(gPlayer[snum].x, gPlayer[snum].y, gPlayer[snum].z, gPlayer[snum].sectnum, xvect, yvect, 128,
//					1024, 1024, CLIPMASK0);
//			if (clipmove_sectnum != -1) {
//				gPlayer[snum].x = clipmove_x;
//				gPlayer[snum].y = clipmove_y;
//				gPlayer[snum].z = clipmove_z;
//				gPlayer[snum].sectnum = clipmove_sectnum;
//			}
//			gPlayer[snum].nWeaponImpact = 0;
//		} else

		if ((gPlayer[snum].pInput.xvel | gPlayer[snum].pInput.yvel | gPlayer[snum].nWeaponImpact) != 0) {
			xvect = gPlayer[snum].pInput.xvel;
			yvect = gPlayer[snum].pInput.yvel;

			if (gPlayer[snum].nWeaponImpact != 0) {
				int force = 3 * TICSPERFRAME * gPlayer[snum].nWeaponImpact;
				xvect += mulscale(sintable[(gPlayer[snum].nWeaponImpactAngle + 512) & 0x7FF], force, 4);
				yvect += mulscale(sintable[gPlayer[snum].nWeaponImpactAngle & 0x7FF], force, 4);
				gPlayer[snum].nWeaponImpact = 0;
			}

            if (gPlayer[snum].noclip) {
				gPlayer[snum].x += xvect >> 14;
				gPlayer[snum].y += yvect >> 14;
				short sect = engine.updatesector(gPlayer[snum].x, gPlayer[snum].y, gPlayer[snum].sectnum);
				if (sect != -1)
					gPlayer[snum].sectnum = sect;
				engine.changespritesect(gPlayer[snum].nSprite, sprite[gPlayer[snum].nSprite].sectnum);
			} else {
				engine.clipmove(gPlayer[snum].x, gPlayer[snum].y, gPlayer[snum].z, gPlayer[snum].sectnum, xvect, yvect,
						128, 1024, 1024, CLIPMASK0);
				if (clipmove_sectnum != -1) {
					gPlayer[snum].x = clipmove_x;
					gPlayer[snum].y = clipmove_y;
					gPlayer[snum].z = clipmove_z;
					gPlayer[snum].sectnum = clipmove_sectnum;
				}
			}
		}

		if (!gPlayer[snum].noclip) {
			short sect = gPlayer[snum].sectnum;
			engine.pushmove(gPlayer[snum].x, gPlayer[snum].y, gPlayer[snum].z, sect, 128, 1024, 1024, CLIPMASK0);
			gPlayer[snum].x = pushmove_x;
			gPlayer[snum].y = pushmove_y;
			gPlayer[snum].z = pushmove_z;
			if (sect != pushmove_sectnum && pushmove_sectnum != -1) {
				gPlayer[snum].sectnum = pushmove_sectnum;
				engine.changespritesect(gPlayer[snum].nSprite, pushmove_sectnum);
			}
		}

		if (gPlayer[snum].pInput.angvel != 0) {
			gPlayer[snum].ang += gPlayer[snum].pInput.angvel * TICSPERFRAME / 16.0f;
			gPlayer[snum].ang = BClampAngle(gPlayer[snum].ang);
		}

		short ocstat = sprite[gPlayer[snum].nSprite].cstat;
		sprite[gPlayer[snum].nSprite].cstat &= ~1;
		engine.getzrange(gPlayer[snum].x, gPlayer[snum].y, gPlayer[snum].z, gPlayer[snum].sectnum, 128, CLIPMASK0);
		int globhiz = zr_ceilz;
		int globloz = zr_florz;
		int globlohit = zr_florhit;
		sprite[gPlayer[snum].nSprite].cstat = ocstat;

		if (((gPlayer[snum].pInput.bits & 8) > 0) && (gPlayer[snum].horiz > 0))
			gPlayer[snum].horiz -= 8; // -
		if (((gPlayer[snum].pInput.bits & 4) > 0) && (gPlayer[snum].horiz < 200))
			gPlayer[snum].horiz += 8; // +
		gPlayer[snum].horiz = BClipRange(gPlayer[snum].horiz + gPlayer[snum].pInput.horiz, 0, 200);

		int word_51759 = 0;

		goalz = globloz - 0x2000;
		// kens slime sector
		if (gPlayer[snum].sectnum > 0
				&& (sector[gPlayer[snum].sectnum].lotag == 4 || sector[gPlayer[snum].sectnum].lotag == 5)) {
			int v23 = lockclock - gPlayer[snum].nSlimeDamageCount;
			if (v23 > 120) {
				if (sector[gPlayer[snum].sectnum].lotag == 4 && gPlayer[snum].nHealth > 0)
					changehealth(snum, -3);
				gPlayer[snum].nSlimeDamageCount = lockclock;
			}
			int dword_51755 = v23 >> 5;
			if (dword_51755 > 2)
				dword_51755 = 1;
			word_51759 = 1;

			// if not on a sprite
			if ((globlohit & 0xc000) != 49152) {
				goalz = globloz - ((dword_51755 + 8) << 8);
				if (gPlayer[snum].z >= goalz - 512) {
					engine.clipmove(gPlayer[snum].x, gPlayer[snum].y, gPlayer[snum].z, gPlayer[snum].sectnum,
							-TICSPERFRAME << 14, -TICSPERFRAME << 14, 128, 4 << 8, 4 << 8, CLIPMASK0);
					if (clipmove_sectnum != -1) {
						gPlayer[snum].x = clipmove_x;
						gPlayer[snum].y = clipmove_y;
						gPlayer[snum].z = clipmove_z;
						gPlayer[snum].sectnum = clipmove_sectnum;
					}
				}
			}
		} else
			gPlayer[snum].nSlimeDamageCount = lockclock;

		if (goalz < globhiz + 4096)
			goalz = (globloz + globhiz) >> 1;

		if ((gPlayer[snum].pInput.bits & 1) != 0) { // jump
			if (gPlayer[snum].z >= globloz - 8192)
				goalz -= (48 << 8);
		} else if ((gPlayer[snum].pInput.bits & 2) != 0) { // crouch
			if (gPlayer[snum].z < (sector[gPlayer[snum].sectnum].floorz - 2048)) {
				goalz += (12 << 8);
			}
		}

		// player is on a groudraw area
		if (gPlayer[snum].sectnum > 0 && (sector[gPlayer[snum].sectnum].floorstat & 2) > 0) {
			Tile pic = engine.getTile(sector[gPlayer[snum].sectnum].floorheinum);
			if (pic.data == null) {
				engine.loadtile(sector[gPlayer[snum].sectnum].floorheinum);
			}
			goalz -= ((pic.data[0] + (((gPlayer[snum].x >> 4) & 63) << 6)
					+ ((gPlayer[snum].y >> 4) & 63)) << 8);
		}

		if (goalz != gPlayer[snum].z) {
//			if (cfgParameter1[3] != 0) {
			if (goalz > gPlayer[snum].z)
				gPlayer[snum].hvel += TICSPERFRAME << 6;

			if (word_51759 != 0) {
				if (goalz < gPlayer[snum].z)
					gPlayer[snum].hvel += TICSPERFRAME * (goalz - gPlayer[snum].z) >> 5;
			} else {
				if (goalz < gPlayer[snum].z)
					gPlayer[snum].hvel = TICSPERFRAME * (goalz - gPlayer[snum].z) >> 5;
			}

			gPlayer[snum].z += gPlayer[snum].hvel;
			if (globloz - 1024 < gPlayer[snum].z) {
				gPlayer[snum].z = globloz - 1024;
				gPlayer[snum].hvel = 0;
			}
			if (globhiz + 1024 > gPlayer[snum].z) {
				gPlayer[snum].z = globhiz + 1024;
				gPlayer[snum].hvel = 0;
			}
//			} else
//				gPlayer[snum].z = goalz;
		}

		if (gPlayer[snum].gViewMode == 2) {
			if ((gPlayer[snum].pInput.bits & 0x20) > 0 && gPlayer[snum].zoom > 48)
				gPlayer[snum].zoom -= gPlayer[snum].zoom >> 4;

			if ((gPlayer[snum].pInput.bits & 0x10) > 0 && gPlayer[snum].zoom < 4096)
				gPlayer[snum].zoom += gPlayer[snum].zoom >> 4;
		}

		engine.setsprite(gPlayer[snum].nSprite, gPlayer[snum].x, gPlayer[snum].y, gPlayer[snum].z + 0x2000);
		sprite[gPlayer[snum].nSprite].ang = (short) gPlayer[snum].ang;

		if (!isValidSector(gPlayer[snum].sectnum) || globhiz + 2048 > globloz) {
//			sndPlaySound("ouch.wav", sub_1D021(gPlayer[snum].x, gPlayer[snum].y));
			changehealth(snum, -TICSPERFRAME);
		}

		if ((gPlayer[snum].pInput.bits & 0x400) != 0) { // USE
			short sect = gPlayer[snum].sectnum;

			if (sector[sect].hitag == 0 && (sector[sect].lotag == 20 || sector[sect].lotag == 21))
				operatesector(sect);

			engine.neartag(gPlayer[snum].x, gPlayer[snum].y, (gPlayer[snum].z + (8 << 8)), gPlayer[snum].sectnum,
					(short) gPlayer[snum].ang, neartag, 1024, 3);

			// kens water fountain
			if (neartag.tagwall >= 0) {
				if (mapnum == 0) {
					WALL pWall = wall[neartag.tagwall];
					if (pWall.lotag == 102) // sound setup
						game.menu.mOpen(game.menu.mMenus[AUDIOSET], -1);
					if (pWall.lotag == 103) // music setup
						game.menu.mOpen(game.menu.mMenus[AUDIOSET], -1);

					if (pWall.lotag == 101) {
						if (pWall.picnum >= 830 && pWall.picnum <= 837) // choose player
						{
							if (oldchoose != 0) {
								wall[oldchoose].shade = 0;
								wall[oldchoose].picnum = oldpic;
							}

							pWall.shade = -10;
							oldchoose = neartag.tagwall;
							oldpic = pWall.picnum;
							pWall.picnum += 710;
							wall[nDiffDoor].cstat = 0;
							wall[nDiffDoorBack].cstat = 0;

							playsound(38);
						}

						if (pWall.picnum >= 805 && pWall.picnum <= 807) // choose difficult
						{
							if (pWall.picnum == 805)
								nDifficult = 1;
							if (pWall.picnum == 806)
								nDifficult = 2;
							if (pWall.picnum == 807)
								nDifficult = 3;
							playsound(41);

							BuildGdx.app.postRunnable(new Runnable() {
								@Override
								public void run() {
									int nPlayersWeapon = 17;

									switch (oldpic) {
									case 830:
										nPlayersWeapon = 17;
										break;
									case 831:
										nPlayersWeapon = 18;
										break;
									case 832:
										nPlayersWeapon = 19;
										break;
									case 833:
										nPlayersWeapon = 20;
										break;
									case 834:
										nPlayersWeapon = 21;
										break;
									case 835:
										nPlayersWeapon = 22;
										break;
									case 836:
										nPlayersWeapon = 23;
										break;
									case 837:
										nPlayersWeapon = 24;
										break;
									default:
										break;
									}

									globalvisibility = 15;
									nPlayerFirstWeapon = (short) nPlayersWeapon;
									gGameScreen.newgame(1);
								}
							});
						}
					}
				} else {
					if (wall[neartag.tagwall].overpicnum == 54) {
						wall[neartag.tagwall].overpicnum = 53;
						waterfountainwall[snum] = neartag.tagwall;
					} else if (wall[neartag.tagwall].picnum == 54) {
						wall[neartag.tagwall].picnum = 53;
						waterfountainwall[snum] = neartag.tagwall;
					}
					if (waterfountainwall[snum] >= 0) {
						waterfountaincnt[snum] -= TICSPERFRAME;
						while (waterfountaincnt[snum] < 0) {
							waterfountaincnt[snum] += 120;
							changehealth(snum, 2);
						}
					}
				}
			}

			// 1-time triggers
			if (neartag.tagsector >= 0) {
				if (sector[neartag.tagsector].hitag == 0)
					operatesector(neartag.tagsector);
			}

			if (neartag.tagwall >= 0) {
				if (wall[neartag.tagwall].lotag == 2) {
					for (i = 0; i < numsectors; i++) {
						if (sector[i].hitag == wall[neartag.tagwall].hitag) {
							if (sector[i].lotag != 1) {
								operatesector(i);
							}
						}
					}
					i = headspritestat[0];
					while (i != -1) {
						nexti = nextspritestat[i];
						if (sprite[i].hitag == wall[neartag.tagwall].hitag) {
							operatesprite(i);
						}
						i = nexti;
					}

					switch (wall[neartag.tagwall].overpicnum) {
					case 730:
						wall[neartag.tagwall].overpicnum = 731;
						wall[neartag.tagwall].lotag = 0;
						wall[neartag.tagwall].hitag = 0;
						playsound(20);
						break;
					case 732:
						wall[neartag.tagwall].overpicnum = 733;
						wall[neartag.tagwall].lotag = 0;
						wall[neartag.tagwall].hitag = 0;
						playsound(25);
						break;
					case 734:
						wall[neartag.tagwall].overpicnum = 735;
						playsound(87);
						break;
					case 735:
						wall[neartag.tagwall].overpicnum = 734;
						playsound(87);
						break;
					case 736:
						wall[neartag.tagwall].overpicnum = 737;
						playsound(85);
						break;
					case 737:
						wall[neartag.tagwall].overpicnum = 736;
						playsound(85);
						break;
					}

				}
			}
		}

		if (mapnum > 0) {
			if ((gPlayer[snum].pInput.bits & 0x800) != 0)
				weaponfire(snum);

			moveweapons(snum);

			switch (gPlayer[snum].pInput.bits >> 14) {
			case 1:
			case 2:
			case 3:
				switchweapgroup(snum, (gPlayer[snum].pInput.bits >> 14) & 3);
				break;
			case 4:
				prevweapon(snum);
				break;
			case 5:
				nextweapon(snum);
				break;
			case 6:
				switchweapon(snum, gPlayer[snum].nLastWeapon);
				break;
			}
		}

		if (word_5174F != 0
				|| (klabs(gPlayer[snum].pInput.xvel) >= 65536 || klabs(gPlayer[snum].pInput.yvel) >= 65536)) {
			if (byte_58A94[snum] >= 4) {
				gPlayer[snum].nBobCount = (short) ((gPlayer[snum].nBobCount + 1) & 0xF);
				weaponbobbing(snum);
				if (cfg.bHeadBob)
					gPlayer[snum].nBobbing = (8 - Math.abs(8 - gPlayer[snum].nBobCount));
				word_5174F = 1;
			}
			word_5174F = 0;
			byte_58A94[snum]++;
		} else {
			byte_58A94[snum] = 0;
			gPlayer[snum].nBobCount = 0;
			gPlayer[snum].nBobbing = 0;
		}
	}

}
