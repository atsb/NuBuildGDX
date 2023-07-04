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
import static ru.m210projects.Build.Engine.MAXSPRITES;
import static ru.m210projects.Build.Engine.headspritesect;
import static ru.m210projects.Build.Engine.headspritestat;
import static ru.m210projects.Build.Engine.nextspritesect;
import static ru.m210projects.Build.Engine.nextspritestat;
import static ru.m210projects.Build.Engine.numsectors;
import static ru.m210projects.Build.Engine.pushmove_sectnum;
import static ru.m210projects.Build.Engine.pushmove_x;
import static ru.m210projects.Build.Engine.pushmove_y;
import static ru.m210projects.Build.Engine.pushmove_z;
import static ru.m210projects.Build.Engine.sector;
import static ru.m210projects.Build.Engine.sintable;
import static ru.m210projects.Build.Engine.sprite;
import static ru.m210projects.Build.Gameutils.isValidSector;
import static ru.m210projects.Build.Net.Mmulti.connecthead;
import static ru.m210projects.Build.Net.Mmulti.connectpoint2;
import static ru.m210projects.Build.Pragmas.klabs;
import static ru.m210projects.LSP.Main.*;
import static ru.m210projects.LSP.Globals.*;
import static ru.m210projects.LSP.Sounds.*;
import static ru.m210projects.LSP.Sprites.*;

import java.util.Arrays;

import ru.m210projects.Build.Types.BuildPos;
import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.LSP.Types.PlayerStruct;

public class Enemies {

	public static int[] gEnemyClock = new int[MAXSPRITES];
	public static short gMoveStatus[] = new short[MAXSPRITES];
	public static short nKills[] = new short[6];
	public static short nTotalKills[] = new short[6];
	public static int nEnemyKills, nEnemyMax;

	private static interface Callback {
		public void invoke(int spriteid);
	}

	private static Object attackFrames[] = new Object[MAXSPRITES];
	private static short timeCounter[] = new short[MAXSPRITES];
	private static short frameIndex[] = new short[MAXSPRITES];

	private static class Frame {
		public short tile;
		public Callback trigger;
		public short ticksPerFrame;

		public Frame(int tile, int ticksPerFrame, Callback trigger) {
			this.tile = (short) tile;
			this.ticksPerFrame = (short) ticksPerFrame;
			this.trigger = trigger;
		}
	};

	private static void setframes(short i, Frame[] frms) {
		SPRITE spr = sprite[i];
		spr.extra = spr.picnum;

		frameIndex[i] = 0;
		attackFrames[i] = frms;

		spr.picnum = frms[0].tile;
		timeCounter[i] = frms[0].ticksPerFrame;
	}

	private static boolean frameprocess(Frame[] frms, short spr) {
		timeCounter[spr] -= TICSPERFRAME;

		if(cfg.bOriginal) {
			switch (sprite[spr].extra) {
			case GREENDUDE:
				greencallback.invoke(spr);
				break;
			case REDDUDE:
				redcallback.invoke(spr);
				break;
			case PURPLEDUDE:
				purpcallback.invoke(spr);
				break;
			case YELLOWDUDE:
				yellowcallback.invoke(spr);
				break;
			}
		}

		if (timeCounter[spr] < 0) {
			if (++frameIndex[spr] >= frms.length) {
				sprite[spr].picnum = sprite[spr].extra;
				engine.changespritestat(spr, CHASE);
				attackFrames[spr] = null;
				return true;
			}

			Frame frm = frms[frameIndex[spr]];
			timeCounter[spr] += frm.ticksPerFrame;
			if (frm.trigger != null)
				frm.trigger.invoke(spr);
			sprite[spr].picnum = frm.tile;
		}

		return false;
	}

	private static Frame[] blueattack = { new Frame(1556, 20, null), new Frame(1557, 12, new Callback() {
		@Override
		public void invoke(int i) {
			SPRITE spr = sprite[i];
			int target = spr.hitag;
			if (target != -1) {
				int dx = gPlayer[target].x - spr.x;
				int dy = gPlayer[target].y - spr.y;
				if (klabs(dx) + klabs(dy) < 1024) {
					changehealth(target, -((engine.krand() & 3) + nDifficult));
					playsound(31, i);
//						if (dword_516B4 != 0) XXX
//							sub_32E4C(500, 10, 10);
				}
			}
		}
	}), new Frame(1558, 12, null), };

	private static SPRITE buildProjectile(int i, int target, int dang) {
		SPRITE spr = sprite[i];

		int j = engine.insertsprite(spr.sectnum, PROJECTILE);
		if (j == -1)
			return null;

		SPRITE nspr = sprite[j];

		nspr.x = spr.x;
		nspr.y = spr.y;
		nspr.z = spr.z;
		nspr.cstat = 128;
		nspr.xrepeat = 64;
		nspr.yrepeat = 64;

		int dx = gPlayer[target].x - spr.x;
		int dy = gPlayer[target].y - spr.y;
		nspr.ang = (short) ((engine.getangle(dx, dy) + dang) & 0x7FF);
		nspr.xvel = (short) (sintable[(nspr.ang + 2560) & 0x7FF] >> 6);
		nspr.yvel = (short) (sintable[(nspr.ang + 2048) & 0x7FF] >> 6);
		nspr.zvel = (short) (((gPlayer[target].z + 8192 - nspr.z) << 8) / engine.ksqrt(dx * dx + dy * dy));
		nspr.owner = (short) i;
		nspr.hitag = (short) target;

		return nspr;
	}

	private static Callback greencallback = new Callback() {
		@Override
		public void invoke(int i) {
			SPRITE nspr = buildProjectile(i, sprite[i].hitag, (engine.krand() & 3) + 2032);
			nspr.z -= 2 * 3584;
			nspr.picnum = 1840;
			nspr.shade = -20;
			nspr.xrepeat = 32;
			nspr.yrepeat = 32;
			nspr.lotag = (short) (-4 - nDifficult);
			playsound(6, i);
		}
	};

	private static Frame[] greenattack = { new Frame(1812, 12, null), new Frame(1813, 12, greencallback), new Frame(1814, 12, null), };

	private static Callback redcallback = new Callback() {
		@Override
		public void invoke(int i) {
			SPRITE nspr = buildProjectile(i, sprite[i].hitag, (engine.krand() & 1) + 2032);
			nspr.z -= 2 * 2560;
			nspr.picnum = 2096;
			nspr.shade = -20;
			nspr.lotag = (short) (-8 - nDifficult);
			playsound(8, i);
		}
	};

	private static Frame[] redattack = { new Frame(2068, 8, null), new Frame(2069, 8, null), new Frame(2070, 8, null),
			new Frame(2071, 8, null), new Frame(2072, 8, null), new Frame(2073, 12, redcallback), };

	private static Callback purpcallback = new Callback() {
		@Override
		public void invoke(int i) {
			SPRITE nspr = buildProjectile(i, sprite[i].hitag, (engine.krand() & 1) + 2040);
			nspr.z -= 2 * 2560;
			nspr.picnum = 2352;
			nspr.shade = -20;
			nspr.lotag = (short) (-16 - nDifficult);
			playsound(0, i);
		}
	};

	private static Frame[] purpattack = { new Frame(2324, 8, null), new Frame(2325, 8, null), new Frame(2326, 8, null),
			new Frame(2327, 16,purpcallback) };

	private static Callback yellowcallback = new Callback() {
		@Override
		public void invoke(int i) {
			SPRITE nspr = buildProjectile(i, sprite[i].hitag, (engine.krand() & 1) + 2040);
			nspr.z -= 2 * 2560;
			nspr.picnum = 2608;
			nspr.shade = -25;
			nspr.lotag = (short) (-32 - nDifficult);
			playsound(2, i);
		}
	};

	private static Frame[] yellattack = { new Frame(2580, 20, null), new Frame(2581, 12, null),
			new Frame(2582, 12, yellowcallback), new Frame(2583, 12, null), };

	public static void inienemies() {
		Arrays.fill(gMoveStatus, (short) 1);
		Arrays.fill(nKills, (short) 0);
		Arrays.fill(nTotalKills, (short) 0);

		nEnemyKills = 0;
		nEnemyMax = 0;

		// attack animate
		engine.getTile(1556).anm = 0;
		engine.getTile(1812).anm = 0;
		engine.getTile(2068).anm = 0;
		engine.getTile(2324).anm = 0;
		engine.getTile(2580).anm = 0;

		for (short i = 0; i < 1024; i++) {
			SPRITE spr = sprite[i];
			if (spr.statnum >= 32)
				continue;

			switch (spr.picnum) {
			case 232: // budda
			case 1410: // skull
				engine.changespritestat(i, GUARD);
				spr.lotag = 9999;
				break;
			case BLUEDUDE:
				engine.changespritestat(i, GUARD);
				spr.lotag = 24;
				nTotalKills[1]++;
				nEnemyMax++;
				break;
			case GREENDUDE:
				engine.changespritestat(i, GUARD);
				spr.lotag = 48;
				nTotalKills[2]++;
				nEnemyMax++;
				break;
			case REDDUDE:
				engine.changespritestat(i, GUARD);
				spr.lotag = 72;
				nTotalKills[3]++;
				nEnemyMax++;
				break;
			case PURPLEDUDE:
				engine.changespritestat(i, GUARD);
				spr.lotag = 240;
				nTotalKills[4]++;
				nEnemyMax++;
				break;
			case YELLOWDUDE:
				engine.changespritestat(i, GUARD);
				spr.lotag = 480;
				nTotalKills[5]++;
				nEnemyMax++;
				break;
			}

			switch (spr.picnum) {
			case 50: // concrete
			case 232: // budda
			case 1410: // skull
			case 1536: // blue enemy
			case 1600: // empty
			case 1700: // empty
			case 1792: // green enemy
			case 2048: // red enemy
			case 2304: // purple enemy
			case 2560: // yellow enemy
				spr.cstat |= 257;
				break;
			}

			if (nNextMap == 6) {
				switch (spr.picnum) {
				case 2608: // delete projectiles
				case 2356:
				case 2352:
				case 2096:
				case 1844:
				case 1840:
				case 1374:
				case 1369:
				case 1364:
				case 1358:
				case 1354:
				case 1345:
				case 1341:
				case 1335:
				case 1328:
				case 1259:
				case 1225:
				case 1214:
				case 1203:
				case 1192:
				case 1179:
				case 1166:
				case 1156:
				case 1146:
				case 1136:
				case 1126:
				case 1116:
				case 249:
					engine.deletesprite(i);
					break;
				case 2612:
					break;
				}
			}
		}
	}

	public static void enemydie(int i) {
		SPRITE spr = sprite[i];
		if (spr.statnum == DYING)
			return;

		switch (spr.picnum) {
		case BLUEDUDE:
			nKills[1]++;
			nEnemyKills++;
			break;
		case GREENDUDE:
			nKills[2]++;
			nEnemyKills++;
			break;
		case REDDUDE:
			nKills[3]++;
			nEnemyKills++;
			break;
		case PURPLEDUDE:
			nKills[4]++;
			nEnemyKills++;
			break;
		case YELLOWDUDE:
			nKills[5]++;
			nEnemyKills++;
			break;
		}

		for (short sec = 0; sec < numsectors; sec++) {
			for (short j = headspritesect[sec]; j != -1; j = nextspritesect[j]) {
				switch (sprite[j].picnum) {
				case BLUEDUDE:
				case GREENDUDE:
				case REDDUDE:
				case PURPLEDUDE:
				case YELLOWDUDE:
					SPRITE pspr = sprite[j];
					if (engine.cansee(spr.x, spr.y, spr.z, spr.sectnum, pspr.x, pspr.y,
							pspr.z - (engine.getTile(pspr.picnum).getHeight() << 7), pspr.sectnum)) {
						if (pspr.statnum == GUARD) {
							engine.changespritestat(j, CHASE);
						}
					}
					break;
				}
			}
		}

		if (spr.picnum != PURPLEDUDE && spr.picnum != YELLOWDUDE)
			spr.picnum += 4 * (engine.krand() & 1) + 28;
		else
			spr.picnum += 28;
		spr.cstat = 0;
		spr.lotag = 240;
		spr.owner = spr.picnum;
		engine.changespritestat((short) i, DYING);
	}

//	private static int player_dist;
	private static int findplayer(int i) {
		int mindist = 0x7fffffff;
		int target = connecthead;

		for (int p = connecthead; p >= 0; p = connectpoint2[p]) {
			int dist = klabs(sprite[i].x - gPlayer[p].x) + klabs(sprite[i].y - gPlayer[p].y);
			if (dist < mindist) {
				mindist = dist;
				target = p;
			}
		}

//		player_dist = mindist;
		return target;
	}

	private static int ocount;
	public static void moveenemies() {
		int j;
		int dx, dy;

		short ang;
		int dang;
		boolean canAttack = false;
		int count = 0;

		short i, nexti;
		for (i = headspritestat[ATTACK]; i >= 0; i = nexti) {
			nexti = nextspritestat[i];

			SPRITE spr = sprite[i];
			switch (spr.extra) {
			case GREENDUDE:
			case REDDUDE:
			case PURPLEDUDE:
			case YELLOWDUDE:
			case BLUEDUDE:
				if(!frameprocess((Frame[]) attackFrames[i], i))
					count++;
				break;
			}
		}

		for (i = headspritestat[CHASE]; i >= 0; i = nexti) {
			nexti = nextspritestat[i];

			int target = findplayer(i);
			int v56 = engine.krand();

			SPRITE spr = sprite[i];
			switch (spr.picnum) {
			case 232:
				if (cfg.bOriginal || (totalmoves % 4) == 0) {
					if (engine.cansee(gPlayer[target].x, gPlayer[target].y, gPlayer[target].z, gPlayer[target].sectnum,
							spr.x, spr.y, spr.z - (engine.getTile(232).getHeight() << 7), spr.sectnum) && (engine.krand() & 3) == 0) {
						j = engine.insertsprite(spr.sectnum, PROJECTILE);
						sprite[j].x = spr.x;
						sprite[j].y = spr.y;
						sprite[j].z = spr.z - 2560;
						sprite[j].cstat = 128;
						sprite[j].picnum = 1179;
						sprite[j].shade = -20;
						sprite[j].xrepeat = 32;
						sprite[j].yrepeat = 32;
						sprite[j].ang = (short) (engine.krand() & 0x7FF);
						sprite[j].xvel = (short) (sintable[(sprite[j].ang + 2560) & 0x7FF] >> 6);
						sprite[j].yvel = (short) (sintable[(sprite[j].ang + 2048) & 0x7FF] >> 6);
						dx = gPlayer[target].x - sprite[j].y;
						dy = gPlayer[target].y - sprite[j].x;
						sprite[j].zvel = (short) (((gPlayer[target].z + 2048 - sprite[j].z) << 8)
								/ engine.ksqrt(dx * dx + dy * dy));
						sprite[j].owner = i;
						sprite[j].lotag = -8;
					}
				}
				break;
			case 1410:
				if (cfg.bOriginal || (totalmoves % 4) == 0) {
					if (engine.cansee(spr.x, spr.y, spr.z - (engine.getTile(spr.picnum).getHeight() << 7), spr.sectnum, gPlayer[target].x,
							gPlayer[target].y, gPlayer[target].z, gPlayer[target].sectnum)
							&& (engine.krand() & 7) == 0) {
						j = engine.insertsprite(spr.sectnum, PROJECTILE);
						sprite[j].x = spr.x;
						sprite[j].y = spr.y;
						sprite[j].z = spr.z - 2560;
						sprite[j].cstat = 128;
						sprite[j].picnum = 1322;
						sprite[j].shade = -20;
						sprite[j].xrepeat = 32;
						sprite[j].yrepeat = 32;

						dx = gPlayer[target].x - spr.x;
						dy = gPlayer[target].y - spr.y;
						sprite[j].ang = (short) (((engine.krand() & 1) + engine.getangle(dx, dy) + 2040) & 0x7FF);
						sprite[j].xvel = (short) (sintable[(sprite[j].ang + 2560) & 0x7FF] >> 6);
						sprite[j].yvel = (short) (sintable[(sprite[j].ang + 2048) & 0x7FF] >> 6);
						sprite[j].zvel = (short) (((gPlayer[target].z + 2048 - sprite[j].z) << 8)
								/ engine.ksqrt(dx * dx + dy * dy));
						sprite[j].owner = i;
						sprite[j].lotag = -16;
						playsound(0, i);
					}
				}
				break;
			case GREENDUDE:
			case REDDUDE:
			case PURPLEDUDE:
			case YELLOWDUDE:
			case BLUEDUDE:
				if (!isValidSector(spr.sectnum))
					break;

				count++;
				if (lockclock - gEnemyClock[i] < 0)
					gEnemyClock[i] = lockclock;

				game.pInt.setsprinterpolate(i, spr);

				if (spr.z < sector[spr.sectnum].floorz)
					spr.z += 18 << 6;

				int h = ((engine.getTile(spr.picnum).getHeight() * spr.yrepeat) << 2);
				int zTop = spr.z - h;
				if (spr.z >= sector[spr.sectnum].floorz && zTop < sector[spr.sectnum].floorz)
					spr.z = sector[spr.sectnum].floorz;

				if ((sector[spr.sectnum].lotag == 4 || sector[spr.sectnum].lotag == 5))
					spr.z = sector[spr.sectnum].floorz + h / 2;

				dx = gPlayer[target].x - spr.x;
				dy = gPlayer[target].y - spr.y;
				int dz = (gPlayer[target].z + 0x2000) - spr.z;

				ang = engine.getangle(dx, dy);
				dang = ((1024 + ang - spr.ang) & 0x7FF) - 1024;

				boolean cansee = engine.cansee(gPlayer[target].x, gPlayer[target].y, gPlayer[target].z,
						gPlayer[target].sectnum, spr.x, spr.y, spr.z, spr.sectnum);

				if (spr.picnum == BLUEDUDE) {
					canAttack = false;
					if (cansee && klabs(dx) + klabs(dy) < 1024 && klabs(dz) < 8192) {
						spr.ang = ang;
						canAttack = true;
					}
				} else {
					canAttack = cansee;
				}

				if ((v56 % 10) == 9 && gMoveStatus[i] != 1) {
					if (gMoveStatus[i] == 2 && lockclock - gEnemyClock[i] > 180 || gMoveStatus[i] == 0) {
						gMoveStatus[i] = 1;
						gEnemyClock[i] = lockclock;
					}
				} else if ((v56 % 10) == 0 && klabs(dang) < 256) {
					if (lockclock - gEnemyClock[i] > 60) {
						if (canAttack && target != -1 && gPlayer[target].nHealth > 0) {
							spr.hitag = (short) target;
							switch (spr.picnum) {
							case GREENDUDE:
								setframes(i, greenattack);
								break;
							case REDDUDE:
								setframes(i, redattack);
								break;
							case PURPLEDUDE:
								setframes(i, purpattack);
								break;
							case YELLOWDUDE:
								setframes(i, yellattack);
								break;
							case BLUEDUDE:
								setframes(i, blueattack);
								break;
							}
							engine.changespritestat(i, ATTACK);
							gMoveStatus[i] = 0;
							break;
						}
						gEnemyClock[i] = lockclock;
					}
				} else if (lockclock - gEnemyClock[i] > 120) {
					sprite[i].ang = ang;

					int v30 = (engine.krand() & 256) - 128;
					sprite[i].ang = (short) ((v30 + sprite[i].ang) & 0x7FF);
					gEnemyClock[i] = lockclock;
				}

				if (gMoveStatus[i] == 0)
					break;

				out.x = sintable[(spr.ang + 512) & 0x7FF] >> 9;
				out.y = sintable[spr.ang & 0x7FF] >> 9;

				if (klabs(dang) < 256)
					out = sub_16849(target, i, out.x, out.y);

				short sect = spr.sectnum;
				int move = engine.movesprite(i, out.x, out.y, 0, spr.clipdist << 2, 1024, 1024, CLIPMASK0,
						TICSPERFRAME);

				engine.pushmove(spr.x, spr.y, spr.z - h / 2, sect, spr.clipdist << 2, 1024, 1024, CLIPMASK0);
				spr.x = pushmove_x;
				spr.y = pushmove_y;
				spr.z = pushmove_z + h / 2;
				if (sect != pushmove_sectnum)
					engine.changespritesect(i, pushmove_sectnum);

				if (move == 0)
					break;

				if (spr.picnum == BLUEDUDE) {
					if ((engine.krand() & 3) == 0) {
						sprite[i].ang = engine.getangle(dx, dy);
						break;
					}
				} else {
					if (((spr.ang + 2048 - ang) & 0x7FF) >= 1024)
						spr.ang = (short) (spr.ang + 2 * TICSPERFRAME);
					else
						spr.ang = (short) ((spr.ang + 2048) - 2 * TICSPERFRAME);
					spr.ang &= 0x7FF;
				}

				break;
			}
		}

		if (count < 1 && ocount != count)
			nMusicClock = lockclock;

		if(count < 1 && (maps[mapnum].music - 1) != currSong) {
			if (lockclock - nMusicClock > 780)
				startmusic(maps[mapnum].music - 1);
		} else if (count > 1 && currSong != 7)
			startmusic(7);

		ocount = count;

		i = headspritestat[DYING];
		while (i >= 0) {
			nexti = nextspritestat[i];
			SPRITE spr = sprite[i];
			spr.lotag -= TICSPERFRAME;
			if (spr.lotag < 210 && spr.lotag > 175) {
				spr.picnum = (short) (spr.owner + 1);
			} else if (spr.lotag < 155 && spr.lotag > 120) {
				spr.picnum = (short) (spr.owner + 2);
			} else if (spr.lotag < 75 && spr.lotag > 45) {
				spr.picnum = (short) (spr.owner + 3);
			} else if (spr.lotag < 0) {
				spr.picnum = (short) (spr.owner + 8);
				engine.changespritestat(i, INANIMATE);
			}

			i = nexti;
		}

		i = headspritestat[GUARD];
		while (i >= 0) {
			nexti = nextspritestat[i];
			for (int p = connecthead; p >= 0; p = connectpoint2[p]) {
				if (isvisible(i, p)) {
//					if (word_51767 != 8)
//						sub_1F401(500); XXX
					engine.changespritestat(i, CHASE);
					switch (sprite[i].picnum) {
					case 1536: // blue enemy
						playsound(28, i);
						break;
					case 1792: // green enemy
						playsound(34, i);
						break;
					case 2048: // red enemy
						playsound(53, i);
						break;
					case 2304: // purple enemy
						playsound(52, i);
						break;
					case 2560: // yellow enemy
						playsound(63, i);
						break;
					}
				}
			}
			i = nexti;
		}
	}

	public static boolean isvisible(int i, int target) {
		PlayerStruct plr = gPlayer[target];
		SPRITE spr = sprite[i];

		int sin = sintable[spr.ang & 2047];
		int cos = sintable[(spr.ang + 512) & 2047];

		if (plr.sectnum >= 0 && (cos * (plr.x - spr.x)) + (sin * (plr.y - spr.y)) >= 0) {
			return engine.cansee(plr.x, plr.y, plr.z, plr.sectnum, spr.x, spr.y, spr.z - (engine.getTile(spr.picnum).getHeight() << 7),
					spr.sectnum);
		}
		return false;
	}

	private static BuildPos out = new BuildPos();

	private static BuildPos sub_16849(int nTarget, short nSprite, int xvel, int yvel) {
		boolean v19 = false;
		boolean v18 = false;

		int dx = gPlayer[nTarget].x - sprite[nSprite].x;
		int dy = gPlayer[nTarget].y - sprite[nSprite].y;

		out.x = xvel;
		out.y = yvel;

		if (xvel < 0 && (dx - xvel) < -350 || xvel > 0 && (dx - xvel) > 350)
			v19 = true;

		if (yvel < 0 && (dy - yvel) < -350 || yvel > 0 && (dy - yvel) > 350)
			v18 = true;

		if (!v19) {
			int vel = dx < 0 ? (dx + 350) : (dx - 350);

			if ((klabs(vel) == vel) == (klabs(xvel) == xvel))
				out.x = vel;
			else
				out.x = 0;
		}

		if (!v18) {
			int vel = dy < 0 ? (dy + 350) : (dy - 350);

			if ((klabs(vel) == vel) == (klabs(yvel) == yvel))
				out.y = vel;
			else
				out.y = 0;
		}
		return out;
	}

}
