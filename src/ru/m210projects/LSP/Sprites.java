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

import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Build.Net.Mmulti.connecthead;
import static ru.m210projects.Build.Net.Mmulti.connectpoint2;
import static ru.m210projects.Build.Pragmas.klabs;
import static ru.m210projects.LSP.Quotes.viewSetMessage;
import static ru.m210projects.LSP.Main.*;
import static ru.m210projects.LSP.Globals.*;
import static ru.m210projects.LSP.Sounds.*;
import static ru.m210projects.LSP.Weapons.*;
import static ru.m210projects.LSP.Enemies.*;
import static ru.m210projects.LSP.View.*;

import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.LSP.Types.PlayerStruct;

public class Sprites {

	public static void operatesprite(int dasprite) {
		SPRITE spr = sprite[dasprite];
		if (spr.lotag == 2)
			shoot(dasprite, spr.x, spr.y, spr.z, spr.ang, 100, spr.sectnum, 9);
	}

	public static int changeammo(int plr, int nWeapon, int value) {
		if (gPlayer[plr].nPlayerStatus == 5) {
			gPlayer[plr].nAmmo[nWeapon] = 160;
			return 0;
		}
		if (value <= 0) {
			gPlayer[plr].nAmmo[nWeapon] += value;
			if (gPlayer[plr].nAmmo[nWeapon] < 0)
				gPlayer[plr].nAmmo[nWeapon] = 0;
			return 0;
		}
		if (gPlayer[plr].nAmmo[nWeapon] >= 160)
			return 0;
		gPlayer[plr].nAmmo[nWeapon] += value;
		if (gPlayer[plr].nAmmo[nWeapon] > 160)
			gPlayer[plr].nAmmo[nWeapon] = 160;
		TintPalette(0, 16, 0);
		return 1;
	}

	public static int changemanna(int a1, int a2) {
		if (gPlayer[a1].nPlayerStatus == 5) {
			gPlayer[a1].nMana = 160;
			return 0;
		}
		if (a2 <= 0) {
			gPlayer[a1].nMana += a2;
			if (gPlayer[a1].nMana < 0)
				gPlayer[a1].nMana = 0;
			return 0;
		}
		if (gPlayer[a1].nMana >= 160)
			return 0;
		gPlayer[a1].nMana += a2;
		if (gPlayer[a1].nMana > 160)
			gPlayer[a1].nMana = 160;

		TintPalette(0, 4, 16);
		return 1;
	}

	public static boolean changehealth(int snum, int deltahealth) {
		if (gPlayer[snum].nPlayerStatus == 5) {
			gPlayer[snum].nHealth = 160;
			return false;
		}

		boolean out = false;
		if (deltahealth <= 0) {
			if (gPlayer[snum].nHealth > 0)
				TintPalette(16, 0, 0);
			switch (nDifficult) {
			case 1:
				gPlayer[snum].nHealth += deltahealth / 2;
				break;
			case 3:
				gPlayer[snum].nHealth += deltahealth * 2;
				break;
			default:
				gPlayer[snum].nHealth += deltahealth;
				break;
			}
		} else {
			if (gPlayer[snum].nHealth < 160) {
				gPlayer[snum].nHealth += deltahealth;
				if (gPlayer[snum].nHealth > 160)
					gPlayer[snum].nHealth = 160;
				out = true;
			}
		}

		if (gPlayer[snum].nHealth <= 0) {
			if ((engine.krand() & 1) != 0)
				playsound(9);
			else
				playsound(10);
			globalvisibility = 15;
			gPlayer[snum].nHealth = 0;
			return false;
		}

		if (gPlayer[snum].nHealth >= 25) {
			globalvisibility = 15;
			return out;
		}

		if (gPlayer[snum].nHealth < 8)
			globalvisibility = 9;
		else if (gPlayer[snum].nHealth < 18)
			globalvisibility = 11;
		else
			globalvisibility = 13;

		playsound(88);
		return out;
	}

	public static void checktouchsprite(int snum) {
		short sectnum = gPlayer[snum].sectnum;
		if ((sectnum < 0) || (sectnum >= numsectors)) {
			return;
		}

		short i = headspritesect[sectnum], nexti;
		while (i != -1) {
			nexti = nextspritesect[i];
			if ((Math.abs(gPlayer[snum].x - sprite[i].x) + Math.abs(gPlayer[snum].y - sprite[i].y) < 512)) {
				int height = (sprite[i].z >> 8) - (engine.getTile(sprite[i].picnum).getHeight() >> 1);
				if (Math.abs((gPlayer[snum].z >> 8) - height) <= 40) {
					switch (sprite[i].picnum) {
					case 601:
					case 602:
						if (changehealth(snum, sprite[i].picnum == 601 ? 10 : 30)) {
							viewSetMessage("Picked up " + (sprite[i].picnum == 601 ? "a Parasol" : "a Cookie"));
							TintPalette(16, 16, 0);
							playsound(51);
							engine.deletesprite(i);
						}
						break;
					case 603: // armor
						if (changehealth(snum, 100)) {
							viewSetMessage("Picked up an Armor");
							TintPalette(16, 16, 0);
							playsound(51);
							playsound(51);
							playsound(51);
							engine.deletesprite(i);
						}
						break;
					case 604:
						if (changeammo(snum, 1, 12) == 0)
							break;

						viewSetMessage("Picked up Shakens");
						playsound(56);
						engine.deletesprite(i);
						break;
					case 605: // balls
						if (changeammo(snum, 2, 10) == 0)
							break;

						viewSetMessage("Picked up Balls");
						playsound(26);
						engine.deletesprite(i);
						break;
					case 606:
						if (changeammo(snum, 3, 10) == 0)
							break;

						viewSetMessage("Picked up Pikes");
						playsound(27);
						engine.deletesprite(i);
						break;
					case 607:
						if (changeammo(snum, 4, 8) == 0)
							break;

						viewSetMessage("Picked up Shurikens");
						playsound(32);
						engine.deletesprite(i);
						break;
					case 608:
						if (changeammo(snum, 5, 6) == 0)
							break;

						viewSetMessage("Picked up Daggers");
						playsound(35);
						engine.deletesprite(i);
						break;
					case 609:
						if (changeammo(snum, 6, 3) == 0)
							break;

						viewSetMessage("Picked up Kunai");
						playsound(90);
						engine.deletesprite(i);
						break;
					case 700: // tree root1?
					case 701: // tree root2?
						if (changehealth(snum, sprite[i].picnum == 700 ? 50 : 32)) {
							viewSetMessage("Picked up Lucky Charm");
							TintPalette(16, 16, 0);
							playsound(51);
							engine.deletesprite(i);
						}
						break;
					case 702: // chocolate
						if (changehealth(snum, 16)) {
							viewSetMessage("Picked up Po Chai pills");
							TintPalette(16, 16, 0);
							playsound(50);
							engine.deletesprite(i);
						}
						break;
					case 703: // flowers with roots
						if (changehealth(snum, 8)) {
							viewSetMessage("Picked up Herbs");
							TintPalette(16, 16, 0);
							playsound(50);
							playsound(50);
							playsound(50);
							engine.deletesprite(i);
						}
						break;
					case 705: // flower
						if (changemanna(snum, 25) != 0) {
							viewSetMessage("Picked up a Flower");
							playsound(29);
							engine.deletesprite(i);
						}
						break;
					case 706: // mushroom
						if (changemanna(snum, 12) != 0) {
							viewSetMessage("Picked up a Mushroom");
							playsound(29);
							engine.deletesprite(i);
						}
						break;
					case 707: // manna bottle
						if (changemanna(snum, 5) != 0) {
							viewSetMessage("Picked up a Mana bottle");
							playsound(29);
							engine.deletesprite(i);
						}
						break;
					case 710: // blue book
						if (gPlayer[snum].nAmmo[8] == 0) {
							TintPalette(0, 4, 16);
							viewSetMessage("Picked up a Blue book");
							changemanna(snum, 25);
							playsound(58);
							engine.deletesprite(i);
							gPlayer[snum].nAmmo[8] = 1;
						}
						break;
					case 711: // brown book
						if (gPlayer[snum].nAmmo[9] == 0) {
							TintPalette(0, 4, 16);
							viewSetMessage("Picked up a Brown book");
							changemanna(snum, 35);
							playsound(59);
							engine.deletesprite(i);
							gPlayer[snum].nAmmo[9] = 1;
						}
						break;
					case 712: // stone
						if (gPlayer[snum].nAmmo[10] == 0) {
							TintPalette(0, 4, 16);
							viewSetMessage("Picked up a Stone plate");
							changemanna(snum, 50);
							playsound(60);
							engine.deletesprite(i);
							gPlayer[snum].nAmmo[10] = 1;
						}
						break;
					case 713: // wooden book
						if (gPlayer[snum].nAmmo[11] == 0) {
							TintPalette(0, 4, 16);
							viewSetMessage("Picked up a Wooden slip");
							changemanna(snum, 75);
							playsound(61);
							engine.deletesprite(i);
							gPlayer[snum].nAmmo[11] = 1;
						}
						break;
					case 714: // papirus
						if (gPlayer[snum].nAmmo[12] == 0) {
							TintPalette(0, 4, 16);
							viewSetMessage("Picked up a Scroll");
							changemanna(snum, 100);
							playsound(62);
							engine.deletesprite(i);
							gPlayer[snum].nAmmo[12] = 1;
						}
						break;
					case 720: // gold key
					case 721: // bronze key
					case 722: // silver key
					case 723: // grey key
					case 724: // green key
					case 725: // red key
						viewSetMessage("Picked up a Key");
						TintPalette(0, 16, 0);
						changehealth(snum, engine.krand() % 100 + 60);
						changemanna(snum, engine.krand() % 100 + 60);
						playsound(50);
						playsound(29);
						playsound(50);
						playsound(29);
						engine.deletesprite(i);
						if (sprite[i].picnum == 725) {
							for (short sec = 0; sec < numsectors; sec++) {
								for (short spr = headspritesect[sec]; spr != -1; spr = nextspritesect[spr]) {
									switch (sprite[spr].picnum) {
									case 275:
									case 339:
									case BLUEDUDE:
									case GREENDUDE:
									case REDDUDE:
									case PURPLEDUDE:
									case YELLOWDUDE:
										PlayerStruct plr = gPlayer[snum];
										SPRITE pspr = sprite[spr];

										int sin = sintable[(int) plr.ang & 2047];
										int cos = sintable[(int) (plr.ang + 512) & 2047];

										if (plr.sectnum >= 0
												&& (cos * (pspr.x - plr.x)) + (sin * (pspr.y - plr.y)) >= 0) {
											if (engine.cansee(plr.x, plr.y, plr.z, plr.sectnum, pspr.x, pspr.y,
													pspr.z - (engine.getTile(pspr.picnum).getHeight() << 7), pspr.sectnum)) {
												if (sprite[spr].picnum != 275 && sprite[spr].picnum != 339)
													enemydie(spr);
												if (spr % 5 == 1)
													playsound((engine.krand() & 7) + 12, spr);
											}
										}
										break;
									}
								}
							}
							changemanna(snum, -200);
						}
						break;
					case 727: // gold coin

						break;
					}
				}
			}
			i = nexti;
		}
	}

	private static void moveprojectiles() {
		int xvel, yvel, zvel;

		short nexti;
		for (short i = headspritestat[PROJECTILE]; i >= 0; i = nexti) {
			nexti = nextspritestat[i];

			SPRITE spr = sprite[i];
			game.pInt.setsprinterpolate(i, spr);
			if (spr.picnum != 1166 && spr.picnum != 1156 && spr.picnum != 1136 && spr.picnum != 1116
					&& spr.picnum != 1179 && spr.picnum != 1192 && spr.picnum != 1203 && spr.picnum != 1214) {
				if (spr.picnum == 1259) {
					spr.ang += 16;
					xvel = sintable[(spr.ang + 2560) & 0x7FF] >> 5;
					yvel = sintable[(spr.ang + 2048) & 0x7FF] >> 5;
				} else {
					xvel = spr.xvel;
					yvel = spr.yvel;
				}
				zvel = spr.zvel;
			} else {
				spr.z += spr.zvel;
				spr.zvel += 32 * TICSPERFRAME;
				int cz = sector[spr.sectnum].ceilingz + 512;
				if (cz > spr.z) {
					spr.z = cz;
					spr.zvel = (short) -(spr.zvel >> 1);
				}

				int fz = sector[spr.sectnum].floorz - 512;
				if (fz < spr.z) {
					spr.z = fz;
					spr.zvel = (short) -(spr.zvel >> 1);
				}
				xvel = spr.xvel;
				yvel = spr.yvel;
				zvel = 0;
			}

			clipmoveboxtracenum = 1;

			int blowspr = -1;
			int hitmove = engine.movesprite(i, xvel, yvel, zvel, spr.clipdist << 2, 512, 512, CLIPMASK0, TICSPERFRAME);

			clipmoveboxtracenum = 3;

			if (hitmove != 0) {
				switch (spr.picnum) {
				case 1203:
					blowspr = engine.insertsprite(spr.sectnum, EXPLOSION);
					sprite[blowspr].x = spr.x;
					sprite[blowspr].y = spr.y;
					sprite[blowspr].z = spr.z;
					sprite[blowspr].cstat = 128;
					sprite[blowspr].picnum = 1354;
					sprite[blowspr].shade = -24;
					sprite[blowspr].xrepeat = 72;
					sprite[blowspr].yrepeat = 72;
					sprite[blowspr].ang = 0;
					sprite[blowspr].xvel = 0;
					sprite[blowspr].yvel = 0;
					sprite[blowspr].zvel = 0;
					sprite[blowspr].owner = spr.owner;
					sprite[blowspr].lotag = 80;
					break;
				case 1214:
					blowspr = engine.insertsprite(spr.sectnum, EXPLOSION);
					sprite[blowspr].x = spr.x;
					sprite[blowspr].y = spr.y;
					sprite[blowspr].z = spr.z;
					sprite[blowspr].cstat = 128;
					sprite[blowspr].picnum = 1369;
					sprite[blowspr].shade = -24;
					sprite[blowspr].xrepeat = 64;
					sprite[blowspr].yrepeat = 64;
					sprite[blowspr].ang = spr.ang;
					sprite[blowspr].xvel = 0;
					sprite[blowspr].yvel = 0;
					sprite[blowspr].zvel = 0;
					sprite[blowspr].owner = spr.owner;
					sprite[blowspr].lotag = 100;
					break;
				case 1225:
				case 1226:
				case 1227:
				case 1228:
				case 1229:
				case 1230:
				case 1231:
				case 1232:
				case 1233:
					blowspr = engine.insertsprite(spr.sectnum, EXPLOSION);
					sprite[blowspr].x = spr.x;
					sprite[blowspr].y = spr.y;
					sprite[blowspr].z = spr.z;
					sprite[blowspr].cstat = 128;
					sprite[blowspr].picnum = (short) ((engine.krand() & 3) + 1374);
					sprite[blowspr].shade = -24;
					sprite[blowspr].xrepeat = 48;
					sprite[blowspr].yrepeat = 48;
					sprite[blowspr].ang = spr.ang;
					sprite[blowspr].xvel = 0;
					sprite[blowspr].yvel = 0;
					sprite[blowspr].zvel = 0;
					sprite[blowspr].owner = spr.owner;
					sprite[blowspr].lotag = (short) ((engine.krand() & 0x7F) + 100);
					break;
				case 1259:
					blowspr = engine.insertsprite(spr.sectnum, EXPLOSION);
					sprite[blowspr].x = spr.x;
					sprite[blowspr].y = spr.y;
					sprite[blowspr].z = spr.z;
					sprite[blowspr].cstat = 128;
					sprite[blowspr].picnum = 1328;
					sprite[blowspr].shade = -24;
					sprite[blowspr].xrepeat = 72;
					sprite[blowspr].yrepeat = 72;
					sprite[blowspr].ang = 0;
					sprite[blowspr].xvel = 0;
					sprite[blowspr].yvel = 0;
					sprite[blowspr].zvel = 0;
					sprite[blowspr].owner = spr.owner;
					sprite[blowspr].lotag = (short) ((engine.krand() & 0x3F) + 12);
					break;
				case 1322:
				case 2352:
					blowspr = engine.insertsprite(spr.sectnum, EXPLOSION);
					sprite[blowspr].x = spr.x;
					sprite[blowspr].y = spr.y;
					sprite[blowspr].z = spr.z;
					sprite[blowspr].cstat = 128;
					sprite[blowspr].picnum = 2356;
					sprite[blowspr].shade = -23;
					sprite[blowspr].xrepeat = 32;
					sprite[blowspr].yrepeat = 32;
					sprite[blowspr].ang = 0;
					sprite[blowspr].xvel = 0;
					sprite[blowspr].yvel = 0;
					sprite[blowspr].zvel = 0;
					sprite[blowspr].owner = spr.owner;
					sprite[blowspr].lotag = 63;
					break;
				case 1840:
					blowspr = engine.insertsprite(spr.sectnum, EXPLOSION);
					sprite[blowspr].x = spr.x;
					sprite[blowspr].y = spr.y;
					sprite[blowspr].z = spr.z;
					sprite[blowspr].cstat = 128;
					sprite[blowspr].picnum = 1844;
					sprite[blowspr].shade = -23;
					sprite[blowspr].xrepeat = 32;
					sprite[blowspr].yrepeat = 32;
					sprite[blowspr].ang = spr.ang;
					sprite[blowspr].xvel = 0;
					sprite[blowspr].yvel = 0;
					sprite[blowspr].zvel = 0;
					sprite[blowspr].owner = spr.owner;
					sprite[blowspr].lotag = 63;
					break;
				case 2608:
					blowspr = engine.insertsprite(spr.sectnum, EXPLOSION);
					sprite[blowspr].x = spr.x;
					sprite[blowspr].y = spr.y;
					sprite[blowspr].z = spr.z;
					sprite[blowspr].cstat = 128;
					sprite[blowspr].picnum = 2612;
					sprite[blowspr].shade = -23;
					sprite[blowspr].xrepeat = 24;
					sprite[blowspr].yrepeat = 24;
					sprite[blowspr].ang = spr.ang;
					sprite[blowspr].xvel = 0;
					sprite[blowspr].yvel = 0;
					sprite[blowspr].zvel = 0;
					sprite[blowspr].owner = spr.owner;
					sprite[blowspr].lotag = 63;
					break;
				case 2096: // gray coin
					blowspr = engine.insertsprite(spr.sectnum, EXPLOSION);
					sprite[blowspr].x = spr.x;
					sprite[blowspr].y = spr.y;
					sprite[blowspr].z = spr.z;
					sprite[blowspr].cstat = 128;
					sprite[blowspr].picnum = 1364;
					sprite[blowspr].shade = -23;
					sprite[blowspr].xrepeat = 32;
					sprite[blowspr].yrepeat = 32;
					sprite[blowspr].ang = 0;
					sprite[blowspr].xvel = 0;
					sprite[blowspr].yvel = 0;
					sprite[blowspr].zvel = 0;
					sprite[blowspr].owner = spr.owner;
					sprite[blowspr].lotag = 48;
					break;
				case 1116:
				case 1126:
				case 1136:
				case 1146:
				case 1156:
				case 1166:
					blowspr = engine.insertsprite(spr.sectnum, EXPLOSION);
					sprite[blowspr].x = spr.x;
					sprite[blowspr].y = spr.y;
					sprite[blowspr].z = spr.z;
					sprite[blowspr].cstat = 128;
					sprite[blowspr].picnum = 1364;
					sprite[blowspr].shade = -23;
					sprite[blowspr].xrepeat = 48;
					sprite[blowspr].yrepeat = 48;
					sprite[blowspr].ang = 0;
					sprite[blowspr].xvel = 0;
					sprite[blowspr].yvel = 0;
					sprite[blowspr].zvel = 0;
					sprite[blowspr].owner = spr.owner;
					sprite[blowspr].lotag = 63;
					break;
				case 1179:
					blowspr = engine.insertsprite(spr.sectnum, EXPLOSION);
					sprite[blowspr].x = spr.x;
					sprite[blowspr].y = spr.y;
					sprite[blowspr].z = spr.z;
					sprite[blowspr].cstat = 128;
					sprite[blowspr].picnum = 1358;
					sprite[blowspr].shade = -24;
					sprite[blowspr].xrepeat = 128;
					sprite[blowspr].yrepeat = 128;
					sprite[blowspr].ang = 0;
					sprite[blowspr].xvel = 0;
					sprite[blowspr].yvel = 0;
					sprite[blowspr].zvel = 0;
					sprite[blowspr].owner = spr.owner;
					sprite[blowspr].lotag = 64;
					break;
				case 1192:
					blowspr = engine.insertsprite(spr.sectnum, EXPLOSION);
					sprite[blowspr].x = spr.x;
					sprite[blowspr].y = spr.y;
					sprite[blowspr].z = spr.z;
					sprite[blowspr].cstat = 128;
					sprite[blowspr].picnum = 1345;
					sprite[blowspr].shade = -24;
					sprite[blowspr].xrepeat = 72;
					sprite[blowspr].yrepeat = 72;
					sprite[blowspr].ang = 0;
					sprite[blowspr].xvel = 0;
					sprite[blowspr].yvel = 0;
					sprite[blowspr].zvel = 0;
					sprite[blowspr].owner = spr.owner;
					sprite[blowspr].lotag = 64;
					break;
				}

				int nHitObject = hitmove & 0x0FFF;
				switch (hitmove & 0xC000) {
				case 0x4000:
				case 0x8000:
					switch (spr.picnum) {
					case 1840:
					case 1322:
					case 2352:
						playsound(39, i);
						break;
					case 1179:
					case 1192:
					case 1203:
					case 1214:
					case 1225:
					case 1226:
					case 1227:
					case 1228:
					case 1229:
					case 1230:
					case 1231:
					case 1232:
					case 1233:
					case 1259:
						playsound((engine.krand() & 7) + 37, i);
						break;
					case 1116:
					case 1126:
					case 1136:
					case 1146:
					case 1156:
					case 1166:
						playsound(49, i);
						break;
					}

					if (spr.picnum == 1259 && (hitmove & 0xC000) != 0x4000) {
						spr.ang = (short) ((spr.ang + 0x100) & 0x7FF);
						spr.zvel -= 2;
						continue;
					}

					engine.deletesprite(i);
					continue;
				case 0xC000:
					if (sprite[nHitObject].picnum >= 256 && sprite[nHitObject].picnum < 320) {
						switch (spr.picnum) {
						case 1116:
						case 1126:
						case 1136:
						case 1146:
						case 1156:
						case 1166:
							engine.deletesprite(i);
							continue;
						case 1179: // Check it
						case 1192:
						case 1203:
						case 1214:
						case 1225:
						case 1226:
						case 1227:
						case 1228:
						case 1229:
						case 1230:
						case 1231:
						case 1232:
						case 1233:
						case 1259:
							playsound((engine.krand() & 7) + 37, i);
							sprite[nHitObject].picnum += 64;
							sprite[nHitObject].cstat &= ~257;
							engine.deletesprite(i);
							continue;
						}
					}

					if (spr.owner != nHitObject) {
						if (sprite[nHitObject].picnum != REDDUDE && sprite[nHitObject].picnum != BLUEDUDE
								&& sprite[nHitObject].picnum != GREENDUDE && sprite[nHitObject].picnum != PURPLEDUDE
								&& sprite[nHitObject].picnum != YELLOWDUDE) {

							for (int p = connecthead; p >= 0; p = connectpoint2[p]) {
								if (nHitObject == gPlayer[p].nSprite) {
									// if ( dword_516B4 != 0 ) XXX
									// sub_32E4C(2000, 20, 30);

									if (spr.picnum == 2096) {
										// changepalette(palette2); XXX
										// word_58784[target] = 1;
										// dword_569DC[target] = totalclock;

										gPlayer[p].nWeaponImpact = -72;
										gPlayer[p].nWeaponImpactAngle = (short) gPlayer[p].ang;
										playsound(7);
										changehealth(p, spr.lotag);
										engine.deletesprite(i);
										continue;
									}

									switch (spr.picnum) {
									case 1840:
										sprite[blowspr].picnum = 1844;
										sprite[blowspr].shade = -18;
										sprite[blowspr].xrepeat = 24;
										sprite[blowspr].yrepeat = 24;
										sprite[blowspr].lotag = 64;
										playsound(33);
										gPlayer[p].nWeaponImpact = -100;
										gPlayer[p].nWeaponImpactAngle = (short) gPlayer[p].ang;
										break;
									case 2352:
										sprite[blowspr].picnum = 2356;
										sprite[blowspr].shade = -16;
										sprite[blowspr].xrepeat = 32;
										sprite[blowspr].yrepeat = 32;
										sprite[blowspr].lotag = 160;
										playsound(1);
										gPlayer[p].nWeaponImpact = -100;
										gPlayer[p].nWeaponImpactAngle = (short) gPlayer[p].ang;
										break;
									case 2608:
										sprite[blowspr].picnum = 2612;
										sprite[blowspr].shade = -16;
										sprite[blowspr].xrepeat = 32;
										sprite[blowspr].yrepeat = 32;
										sprite[blowspr].lotag = 160;
										playsound(5);
										gPlayer[p].nWeaponImpact = -100;
										gPlayer[p].nWeaponImpactAngle = (short) gPlayer[p].ang;
										break;
									}
									sprite[blowspr].z = spr.z + 2048;
									changehealth(p, spr.lotag);
									engine.deletesprite(i);
									continue;
								}
							}
							if (spr.picnum != 1259) // boomerang
								engine.deletesprite(i);
							continue;
						}
					}
				}

				if (spr.owner != nHitObject && spr.picnum != 1322 // skull proj
						&& spr.picnum != 1840 // greendude proj
						&& spr.picnum != 2096 // gray coin (reddude)
						&& spr.picnum != 2352 // purpdude proj
						&& spr.picnum != 2608) // yelldude proj
				{
					switch (sprite[nHitObject].picnum) {
					case BLUEDUDE: // blue enemy
					case GREENDUDE: // green enemy
					case REDDUDE: // red enemy
					case PURPLEDUDE: // purple enemy
					case YELLOWDUDE: // yellow enemy
						for (int p = connecthead; p >= 0; p = connectpoint2[p]) {
							if ((spr.owner - 4096) == p) {
								if (sprite[nHitObject].lotag > 0) {
									sprite[nHitObject].lotag -= spr.lotag + gPlayer[p].nSecondWeaponDamage;
									gEnemyClock[nHitObject] = lockclock;

									if (sprite[nHitObject].statnum == GUARD) {
										engine.changespritestat((short) nHitObject, CHASE);
									}
								}
							}
						}

						if (sprite[nHitObject].lotag > 0) {
							switch (spr.picnum) {
							case 1179:
							case 1192:
							case 1203:
							case 1214:
							case 1225:
							case 1226:
							case 1227:
							case 1228:
							case 1229:
							case 1230:
							case 1231:
							case 1232:
							case 1233:
							case 1259:
								switch (sprite[nHitObject].picnum) {
								case 1536:
								case 2560:
									playsound(12, nHitObject);
									break;
								case 1792:
									playsound(68, nHitObject);
									break;
								case 2048:
									playsound(70, nHitObject);
									break;
								case 2304:
									playsound(73, nHitObject);
									break;

								}
								break;
							case 1116:
							case 1126:
							case 1136:
							case 1146:
							case 1156:
							case 1166:
								switch (sprite[nHitObject].picnum) {
								case 1536:
									playsound(65, nHitObject);
									break;
								case 1792:
									playsound(66, nHitObject);
									break;
								case 2048:
									playsound(70, nHitObject);
									break;
								case 2304:
									playsound(72, nHitObject);
									break;
								case 2560:
									playsound(68, nHitObject);
									break;
								}
								break;
							}

//							if (sprite[nHitObject].lotag <= 12)
//								sprite[nHitObject].cstat &= 0xFD;
//							sprite[nHitObject].ang = (short) ((sprite[gPlayer[target].nSprite].ang + 0x400) & 0x7FF);
							engine.deletesprite(i);
							continue;
						}

						switch (spr.picnum) {
						case 1179:
						case 1192:
						case 1203:
						case 1214:
						case 1225:
						case 1226:
						case 1227:
						case 1228:
						case 1229:
						case 1230:
						case 1231:
						case 1232:
						case 1233:
						case 1259:
							switch (sprite[nHitObject].picnum) {
							case 1536: // blue enemy
							case 2048: // red enemy
								playsound(11, nHitObject);
								break;
							case 1792: // green enemy
							case 2304: // purple enemy
								playsound(16, nHitObject);
								break;
							case 2560: // yellow enemy
								playsound(19, nHitObject);
								break;
							}
							break;
						case 1116:
						case 1126:
						case 1136:
						case 1146:
						case 1156:
						case 1166:
							switch (sprite[nHitObject].picnum) {
							case 1536: // blue enemy
							case 2048: // red enemy
								playsound(15, nHitObject);
								break;
							case 1792: // green enemy
							case 2304: // purple enemy
								playsound(18, nHitObject);
								break;
							case 2560: // yellow enemy
								playsound(11, nHitObject);
								break;
							}
							break;
						}

						enemydie(nHitObject);
					}
				}

				engine.deletesprite(i);
				continue;
			}
		}
	}

	public static void statuslistcode() {
		moveprojectiles();
		moveenemies();

		short i, nexti;
		i = headspritestat[SHOTSPARK];
		while (i >= 0) {
			nexti = nextspritestat[i];
			game.pInt.setsprinterpolate(i, sprite[i]);

			sprite[i].z -= TICSPERFRAME << 6;
			sprite[i].lotag -= TICSPERFRAME;
			if (sprite[i].lotag < 0)
				engine.deletesprite(i);

			i = nexti;
		}

		i = headspritestat[4]; // unused?
		while (i >= 0) {
			nexti = nextspritestat[i];

			sprite[i].lotag -= TICSPERFRAME;
			sprite[i].picnum = (short) (((63 - sprite[i].lotag) >> 4) + 144);
			if (sprite[i].lotag < 0)
				engine.deletesprite(i);

			i = nexti;
		}

		i = headspritestat[EXPLOSION];
		while (i >= 0) {
			nexti = nextspritestat[i];

			short sectnum = sprite[i].sectnum;
			if (!cfg.bOriginal && sprite[i].picnum != 1364) {
				for (short spr = headspritesect[sectnum]; spr != -1; spr = nextspritesect[spr]) {
					switch (sprite[spr].picnum) {
					case PLAYER:
					case BLUEDUDE:
					case GREENDUDE:
					case REDDUDE:
					case PURPLEDUDE:
					case YELLOWDUDE:
						int dx = sprite[i].x - sprite[spr].x;
						int dy = sprite[i].y - sprite[spr].y;
						int dist = klabs(dx) + klabs(dy);
						if (dist <= sprite[i].xrepeat << 3) {

							if (engine.cansee(sprite[i].x, sprite[i].y, sprite[i].z, sectnum, sprite[spr].x,
									sprite[spr].y, sprite[spr].z - (engine.getTile(sprite[spr].picnum).getHeight() << 7),
									sprite[spr].sectnum)) {
								if (sprite[spr].picnum != PLAYER) {
									if (sprite[spr].statnum == GUARD)
										engine.changespritestat(spr, CHASE);
									sprite[spr].lotag--;
									if (sprite[spr].lotag <= 0) {
										if (sprite[spr].picnum == PURPLEDUDE || sprite[spr].picnum == BLUEDUDE)
											playsound(14, spr);
										else if (sprite[spr].picnum == YELLOWDUDE)
											playsound(18, spr);
										else
											playsound(17, spr);
										enemydie(spr);
									}
								} else {
									for (short p = connecthead; p >= 0; p = connectpoint2[p]) {
										if (spr == gPlayer[p].nSprite) {
											if(gPlayer[p].nPlayerStatus != 5) {
												gPlayer[p].nHealth--;
												TintPalette(3, 0, 0);
											}
											gPlayer[p].nWeaponImpact = -32;
											gPlayer[p].nWeaponImpactAngle = engine.getangle(dx, dy);
										}
									}
								}
							}
						}
						break;
					}
				}
			}

			sprite[i].lotag -= TICSPERFRAME;
			if (sprite[i].lotag < 0)
				engine.deletesprite(i);

			i = nexti;
		}

		i = headspritestat[7]; // unused?
		while (i >= 0) {
			nexti = nextspritestat[i];

			SPRITE spr = sprite[i];
			game.pInt.setsprinterpolate(i, spr);

			spr.x += TICSPERFRAME * spr.xvel >> 4;
			spr.y += TICSPERFRAME * spr.yvel >> 4;
			spr.z += TICSPERFRAME * spr.zvel >> 4;
			spr.zvel += TICSPERFRAME << 7;

			if ((sector[spr.sectnum].ceilingz + 1024) > spr.z) {
				spr.z = sector[spr.sectnum].ceilingz + 1024;
				spr.zvel = (short) -(spr.zvel >> 1);
			}

			if (sector[spr.sectnum].floorz - 1024 < spr.z) {
				spr.z = sector[spr.sectnum].floorz - 1024;
				spr.zvel = (short) -(spr.zvel >> 1);
			}

			spr.xrepeat = (short) (spr.lotag >> 2);
			spr.yrepeat = (short) (spr.lotag >> 2);
			spr.lotag -= TICSPERFRAME;
			if (spr.lotag < 0)
				engine.deletesprite(i);

			i = nexti;
		}

	}
}
