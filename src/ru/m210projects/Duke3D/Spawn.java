package ru.m210projects.Duke3D;

import static ru.m210projects.Build.Engine.CLIPMASK0;
import static ru.m210projects.Build.Engine.MAXSPRITES;
import static ru.m210projects.Build.Engine.MAXSTATUS;
import static ru.m210projects.Build.Engine.headspritestat;
import static ru.m210projects.Build.Engine.nextspritestat;
import static ru.m210projects.Build.Engine.sector;
import static ru.m210projects.Build.Engine.show2dsector;
import static ru.m210projects.Build.Engine.show2dsprite;
import static ru.m210projects.Build.Engine.sintable;
import static ru.m210projects.Build.Engine.sprite;
import static ru.m210projects.Build.Engine.wall;
import static ru.m210projects.Build.Gameutils.isValidSector;
import static ru.m210projects.Build.Net.Mmulti.connecthead;
import static ru.m210projects.Build.Net.Mmulti.numplayers;
import static ru.m210projects.Build.Pragmas.klabs;
import static ru.m210projects.Build.Pragmas.ksgn;
import static ru.m210projects.Duke3D.Actors.*;
import static ru.m210projects.Duke3D.Premap.*;
import static ru.m210projects.Duke3D.Gamedef.*;
import static ru.m210projects.Duke3D.Gameutils.FindDistance2D;
import static ru.m210projects.Duke3D.Globals.*;
import static ru.m210projects.Duke3D.Main.*;
import static ru.m210projects.Duke3D.Names.*;
import static ru.m210projects.Duke3D.Sector.*;
import static ru.m210projects.Duke3D.SoundDefs.SUBWAY;
import static ru.m210projects.Duke3D.Sounds.check_fta_sounds;
import static ru.m210projects.Build.OnSceenDisplay.Console.*;

import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Build.Types.SPRITE;

public class Spawn {

	public static void vglass(int x, int y, int a, int wn, int n) {
		int z, zincs;
		short sect;

		sect = wall[wn].nextsector;
		if (sect == -1)
			return;
		zincs = (sector[sect].floorz - sector[sect].ceilingz) / n;

		for (z = sector[sect].ceilingz; z < sector[sect].floorz; z += zincs) {
			int ve = 16 + (engine.krand() & 31);
			int va = a + 128 - (engine.krand() & 255);
			int pn = GLASSPIECES + (z & (engine.krand() % 3));
			EGS(sect, x, y, z - (engine.krand() & 8191), pn, -32, 36, 36, va, ve, 0, -1, (short) 5);
		}
	}

	public static void lotsofglass(int i, int wallnum, int n) {
		int j, xv, yv, z, x1, y1;
		short sect, a;

		sect = -1;

		if (wallnum < 0) {
			for (j = n - 1; j >= 0; j--) {
				a = (short) (sprite[i].ang - 256 + (engine.krand() & 511) + 1024);
				int vz = 1024 - (engine.krand() & 1023);
				EGS(sprite[i].sectnum, sprite[i].x, sprite[i].y, sprite[i].z, GLASSPIECES + (j % 3), -32, 36, 36, a,
						32 + (engine.krand() & 63), vz, i, (short) 5);
			}
			return;
		}

		j = n + 1;

		x1 = wall[wallnum].x;
		y1 = wall[wallnum].y;

		xv = wall[wall[wallnum].point2].x - x1;
		yv = wall[wall[wallnum].point2].y - y1;

		x1 -= ksgn(yv);
		y1 += ksgn(xv);

		xv /= j;
		yv /= j;

		for (j = n; j > 0; j--) {
			x1 += xv;
			y1 += yv;

			sect = engine.updatesector(x1, y1, sect);
			if (sect >= 0) {
				z = sector[sect].floorz
						- (engine.krand() & (klabs(sector[sect].ceilingz - sector[sect].floorz)));
				if (z < -(32 << 8) || z > (32 << 8))
					z = sprite[i].z - (32 << 8) + (engine.krand() & ((64 << 8) - 1));
				a = (short) (sprite[i].ang - 1024);
				int vz = -(engine.krand() & 1023);
				EGS(sprite[i].sectnum, x1, y1, z, GLASSPIECES + (j % 3), -32, 36, 36, a, 32 + (engine.krand() & 63), vz,
						i, (short) 5);
			}
		}
	}

	public static void spriteglass(int i, int n) {
		int j, k, a, z;

		for (j = n; j > 0; j--) {
			a = engine.krand() & 2047;
			z = sprite[i].z - ((engine.krand() & 16) << 8);
			int vz = -512 - (engine.krand() & 2047);
			int ve = 32 + (engine.krand() & 63);
			k = EGS(sprite[i].sectnum, sprite[i].x, sprite[i].y, z, GLASSPIECES + (j % 3), engine.krand() & 15, 36, 36,
					a, ve, vz, i, (short) 5);
			sprite[k].pal = sprite[i].pal;
		}
	}

	public static void ceilingglass(int i, short sectnum, int n) {
		int j, xv, yv, z, x1, y1;
		short a, s, startwall, endwall;

		startwall = sector[sectnum].wallptr;
		endwall = (short) (startwall + sector[sectnum].wallnum);

		for (s = startwall; s < (endwall - 1); s++) {
			x1 = wall[s].x;
			y1 = wall[s].y;

			xv = (wall[s + 1].x - x1) / (n + 1);
			yv = (wall[s + 1].y - y1) / (n + 1);

			for (j = n; j > 0; j--) {
				x1 += xv;
				y1 += yv;
				a = (short) (engine.krand() & 2047);
				z = sector[sectnum].ceilingz + ((engine.krand() & 15) << 8);
				EGS(sectnum, x1, y1, z, GLASSPIECES + (j % 3), -32, 36, 36, a, (engine.krand() & 31), 0, i, (short) 5);
			}
		}
	}

	public static void lotsofcolourglass(int i, int wallnum, int n) {
		int j, xv, yv, zv, z, x1, y1;
		short sect = -1, a, k;

		if (wallnum < 0) {
			for (j = n - 1; j >= 0; j--) {
				a = (short) (engine.krand() & 2047);
				int vz = 1024 - (engine.krand() & 2047);
				int ve = 32 + (engine.krand() & 63);
				k = EGS(sprite[i].sectnum, sprite[i].x, sprite[i].y, sprite[i].z - (engine.krand() & (63 << 8)),
						GLASSPIECES + (j % 3), -32, 36, 36, a, ve, vz, i, (short) 5);
				sprite[k].pal = (short) (engine.krand() & 15);
			}
			return;
		}

		j = n + 1;
		x1 = wall[wallnum].x;
		y1 = wall[wallnum].y;

		xv = (wall[wall[wallnum].point2].x - wall[wallnum].x) / j;
		yv = (wall[wall[wallnum].point2].y - wall[wallnum].y) / j;

		for (j = n; j > 0; j--) {
			x1 += xv;
			y1 += yv;

			sect = engine.updatesector(x1, y1, sect);
			z = sector[sect].floorz - (engine.krand() & (klabs(sector[sect].ceilingz - sector[sect].floorz)));
			if (z < -(32 << 8) || z > (32 << 8))
				z = sprite[i].z - (32 << 8) + (engine.krand() & ((64 << 8) - 1));
			a = (short) (sprite[i].ang - 1024);
			zv = -(engine.krand() & 2047);
			k = EGS(sprite[i].sectnum, x1, y1, z, GLASSPIECES + (j % 3), -32, 36, 36, a, 32 + (engine.krand() & 63), zv,
					i, (short) 5);
			sprite[k].pal = (short) (engine.krand() & 7);
		}
	}

	public static short EGS(short whatsect, int s_x, int s_y, int s_z, int s_pn, int s_s, int s_xr, int s_yr, int s_a,
			int s_ve, int s_zv, int s_ow, short s_ss) {
		short i = engine.insertsprite(whatsect, s_ss);

		if (i < 0) {
			game.GameMessage(" Too many sprites spawned.");
			game.show();
			return 0;
		}

		SPRITE s = sprite[i];

		s.x = s_x;
		s.y = s_y;
		s.z = s_z;
		s.cstat = 0;
		s.picnum = (short) s_pn;

		s.shade = (byte) s_s;
		s.xrepeat = (short) s_xr;
		s.yrepeat = (short) s_yr;
		s.pal = 0;

		s.ang = (short) s_a;
		s.xvel = (short) s_ve;
		s.zvel = (short) s_zv;
		s.owner = (short) s_ow;
		s.xoffset = 0;
		s.yoffset = 0;
		s.yvel = 0;
		s.clipdist = 0;
		s.pal = 0;
		s.lotag = 0;

		if (s_ow != -1)
			hittype[i].picnum = sprite[s_ow].picnum;

		hittype[i].lastvx = 0;
		hittype[i].lastvy = 0;

		hittype[i].timetosleep = 0;
		hittype[i].actorstayput = -1;
		hittype[i].extra = -1;
		hittype[i].owner = s_ow;
		hittype[i].cgg = 0;
		hittype[i].movflag = 0;
		hittype[i].tempang = 0;
		hittype[i].dispicnum = 0;
		if (s_ow != -1) {
			hittype[i].floorz = hittype[s_ow].floorz;
			hittype[i].ceilingz = hittype[s_ow].ceilingz;
		}

		hittype[i].temp_data[0] = hittype[i].temp_data[2] = hittype[i].temp_data[3] = hittype[i].temp_data[5] = 0;
		if (currentGame.getCON().actorscrptr[s_pn] != 0) {
			s.extra = (short) currentGame.getCON().script[currentGame.getCON().actorscrptr[s_pn]];
			hittype[i].temp_data[4] = currentGame.getCON().script[currentGame.getCON().actorscrptr[s_pn] + 1];
			hittype[i].temp_data[1] = currentGame.getCON().script[currentGame.getCON().actorscrptr[s_pn] + 2];
			s.hitag = (short) currentGame.getCON().script[currentGame.getCON().actorscrptr[s_pn] + 3];
		} else {
			hittype[i].temp_data[1] = hittype[i].temp_data[4] = 0;
			s.extra = 0;
			s.hitag = 0;
		}

		if ((show2dsector[s.sectnum >> 3] & (1 << (s.sectnum & 7))) != 0)
			show2dsprite[i >> 3] |= (1 << (i & 7));
		else
			show2dsprite[i >> 3] &= ~(1 << (i & 7));

		game.pInt.clearspriteinterpolate(i);
		game.pInt.setsprinterpolate(i, s);

		return (i);
	}

	public static int tempwallptr;

	public static int spawn(int j, int pn) {
		short i, s, startwall, sect, clostest = 0;
		int x, y, d, endwall;
		SPRITE sp;

		if (j >= 0) {
			i = EGS(sprite[j].sectnum, sprite[j].x, sprite[j].y, sprite[j].z, pn, 0, 0, 0, 0, 0, 0, j, (short) 0);
			hittype[i].picnum = sprite[j].picnum;
		} else {
			i = (short) pn;

			hittype[i].picnum = sprite[i].picnum;
			hittype[i].timetosleep = 0;
			hittype[i].extra = -1;
	
			sprite[i].owner = (short) (hittype[i].owner = i);
			hittype[i].cgg = 0;
			hittype[i].movflag = 0;
			hittype[i].tempang = 0;
			hittype[i].dispicnum = 0;
			hittype[i].floorz = sector[sprite[i].sectnum].floorz;
			hittype[i].ceilingz = sector[sprite[i].sectnum].ceilingz;

			hittype[i].lastvx = 0;
			hittype[i].lastvy = 0;
			hittype[i].actorstayput = -1;

			hittype[i].temp_data[0] = hittype[i].temp_data[1] = hittype[i].temp_data[2] = hittype[i].temp_data[3] = hittype[i].temp_data[4] = hittype[i].temp_data[5] = 0;

			if (sprite[i].picnum != SPEAKER && sprite[i].picnum != LETTER && sprite[i].picnum != DUCK
					&& sprite[i].picnum != TARGET && sprite[i].picnum != TRIPBOMB && sprite[i].picnum != VIEWSCREEN
					&& sprite[i].picnum != VIEWSCREEN2 && (sprite[i].cstat & 48) != 0)
				if (!(sprite[i].picnum >= CRACK1 && sprite[i].picnum <= CRACK4)) {
					game.pInt.setsprinterpolate(i, sprite[i]);
					if (sprite[i].shade == 127)
						return i;
					if (wallswitchcheck(i) && (sprite[i].cstat & 16) != 0) {
						if (sprite[i].picnum != ACCESSSWITCH && sprite[i].picnum != ACCESSSWITCH2
								&& sprite[i].pal != 0) {
							if ((ud.multimode < 2) || (ud.multimode > 1 && ud.coop == 1)) {
								sprite[i].xrepeat = sprite[i].yrepeat = 0;
								sprite[i].cstat = sprite[i].lotag = sprite[i].hitag = 0;
								return i;
							}
						}
						sprite[i].cstat |= 257;
						if (sprite[i].pal != 0 && sprite[i].picnum != ACCESSSWITCH && sprite[i].picnum != ACCESSSWITCH2)
							sprite[i].pal = 0;
						return i;
					}

					if (sprite[i].hitag != 0) {
						engine.changespritestat(i, (short) 12);
						sprite[i].cstat |= 257;
						sprite[i].extra = (short) currentGame.getCON().impact_damage;
						return i;
					}
				}

			s = sprite[i].picnum;

			if ((sprite[i].cstat & 1) != 0)
				sprite[i].cstat |= 256;

			if (currentGame.getCON().actorscrptr[s] != 0) {
				sprite[i].extra = (short) currentGame.getCON().script[currentGame.getCON().actorscrptr[s]];
				hittype[i].temp_data[4] = currentGame.getCON().script[currentGame.getCON().actorscrptr[s] + 1];
				hittype[i].temp_data[1] = currentGame.getCON().script[currentGame.getCON().actorscrptr[s] + 2];
				if (currentGame.getCON().script[currentGame.getCON().actorscrptr[s] + 3] != 0 && sprite[i].hitag == 0)
					sprite[i].hitag = (short) currentGame.getCON().script[currentGame.getCON().actorscrptr[s] + 3];
			} else
				hittype[i].temp_data[1] = hittype[i].temp_data[4] = 0;
		}

		game.pInt.clearspriteinterpolate(i);

		sp = sprite[i];
		sect = sp.sectnum;

		if (currentGame.getCON().type == 20) { // Twentieth Anniversary World Tour
			switch (sp.picnum) {
			case BOSS5STAYPUT:
				hittype[i].actorstayput = sp.sectnum;
			case FIREFLY:
			case BOSS5:
				if (sp.picnum == BOSS5 || sp.picnum == BOSS5STAYPUT) {
					if (j >= 0 && sprite[j].picnum == RESPAWN)
						sp.pal = sprite[j].pal;
					if (sp.pal != 0) {
						sp.clipdist = 80;
						sp.xrepeat = 40;
						sp.yrepeat = 40;
					} else {
						sp.xrepeat = 80;
						sp.yrepeat = 80;
						sp.clipdist = 164;
					}
				} else {
					sp.xrepeat = 40;
					sp.yrepeat = 40;
					sp.clipdist = 80;
				}

				if (j >= 0)
					sp.lotag = 0;

				if ((sp.lotag > ud.player_skill) || ud.monsters_off) {
					sp.xrepeat = sp.yrepeat = 0;
					engine.changespritestat(i, (short) 5);
					return i;
				} else {
					makeitfall(currentGame.getCON(), i);

					sp.cstat |= 257;
					ps[connecthead].max_actors_killed++;

					if (j >= 0) {
						hittype[i].timetosleep = 0;
						check_fta_sounds(i);
						engine.changespritestat(i, (short) 1);
					} else
						engine.changespritestat(i, (short) 2);
				}
				game.pInt.setsprinterpolate(i, sprite[i]);
				return i;
			case FIREFLYFLYINGEFFECT:
				sp.owner = (short) j;
				engine.changespritestat(i, (short) 5);
				sp.xrepeat = 16;
				sp.yrepeat = 16;
				game.pInt.setsprinterpolate(i, sprite[i]);
				return i;
			case LAVAPOOLBUBBLE:
				game.pInt.setsprinterpolate(i, sprite[i]);
				if (sprite[j].xrepeat < 30)
					return i;
				sp.owner = (short) j;
				engine.changespritestat(i, (short) 5);
				sp.x += engine.krand() % 512 - 256;
				sp.y += engine.krand() % 512 - 256;
				sp.xrepeat = 16;
				sp.yrepeat = 16;
				return i;
			case WHISPYSMOKE:
				engine.changespritestat(i, (short) 5);
				sp.x += engine.krand() % 256 - 128;
				sp.y += engine.krand() % 256 - 128;
				sp.xrepeat = 20;
				sp.yrepeat = 20;
				game.pInt.setsprinterpolate(i, sprite[i]);
				return i;
			case 5846: // SERIOUSSAM
				engine.changespritestat(i, (short) 2);
				sp.cstat = 257;
				sp.extra = 150;
				game.pInt.setsprinterpolate(i, sprite[i]);
				return i;
			}
		}

		switch (sp.picnum) {
		default:
			if (currentGame.getCON().actorscrptr[sp.picnum] != 0) {
				if (j == -1 && sp.lotag > ud.player_skill) {
					sp.xrepeat = sp.yrepeat = 0;
					engine.changespritestat(i, (short) 5);
					break;
				}

				// Init the size
				if (sp.xrepeat == 0 || sp.yrepeat == 0)
					sp.xrepeat = sp.yrepeat = 1;

				if ((currentGame.getCON().actortype[sp.picnum] & 3) != 0) {
					if (ud.monsters_off) {
						sp.xrepeat = sp.yrepeat = 0;
						engine.changespritestat(i, (short) 5);
						break;
					}

					makeitfall(currentGame.getCON(), i);

					if ((currentGame.getCON().actortype[sp.picnum] & 2) != 0)
						hittype[i].actorstayput = sp.sectnum;

					ps[connecthead].max_actors_killed++;
					sp.clipdist = 80;
					if (j >= 0) {
						if (sprite[j].picnum == RESPAWN)
							hittype[i].tempang = sprite[i].pal = sprite[j].pal;
						engine.changespritestat(i, (short) 1);
					} else
						engine.changespritestat(i, (short) 2);
				} else {
					sp.clipdist = 40;
					sp.owner = i;
					engine.changespritestat(i, (short) 1);
				}

				hittype[i].timetosleep = 0;

				if (j >= 0)
					sp.ang = sprite[j].ang;
			}
			break;
		case FOF:
			sp.xrepeat = sp.yrepeat = 0;
			engine.changespritestat(i, (short) 5);
			break;
		case WATERSPLASH2:
			if (j >= 0) {
				engine.setsprite(i, sprite[j].x, sprite[j].y, sprite[j].z);
				sp.xrepeat = sp.yrepeat = (short) (8 + (engine.krand() & 7));
			} else
				sp.xrepeat = sp.yrepeat = (short) (16 + (engine.krand() & 15));

			sp.shade = -16;
			sp.cstat |= 128;
			if (j >= 0) {
				if (sector[sprite[j].sectnum].lotag == 2) {
					sp.z = engine.getceilzofslope(sprite[i].sectnum, sprite[i].x, sprite[i].y) + (16 << 8);
					sp.cstat |= 8;
				} else if (sector[sprite[j].sectnum].lotag == 1)
					sp.z = engine.getflorzofslope(sprite[i].sectnum, sprite[i].x, sprite[i].y);
			}

			if (sector[sect].floorpicnum == FLOORSLIME || sector[sect].ceilingpicnum == FLOORSLIME)
				sp.pal = 7;
		case NEON1:
		case NEON2:
		case NEON3:
		case NEON4:
		case NEON5:
		case NEON6:
		case DOMELITE:
			if (sp.picnum != WATERSPLASH2)
				sp.cstat |= 257;
		case NUKEBUTTON:
			if (sp.picnum == DOMELITE)
				sp.cstat |= 257;
		case JIBS1:
		case JIBS2:
		case JIBS3:
		case JIBS4:
		case JIBS5:
		case JIBS6:
		case HEADJIB1:
		case ARMJIB1:
		case LEGJIB1:
		case LIZMANHEAD1:
		case LIZMANARM1:
		case LIZMANLEG1:
		case DUKETORSO:
		case DUKEGUN:
		case DUKELEG:
			engine.changespritestat(i, (short) 5);
			break;
		case TONGUE:
			if (j >= 0)
				sp.ang = sprite[j].ang;
			sp.z -= 38 << 8;
			sp.zvel = (short) (256 - (engine.krand() & 511));
			sp.xvel = (short) (64 - (engine.krand() & 127));
			engine.changespritestat(i, (short) 4);
			break;
		case NATURALLIGHTNING:
			sp.cstat &= ~257;
			sp.cstat |= 32768;
			break;
		case TRANSPORTERSTAR:
		case TRANSPORTERBEAM:
			if (j == -1)
				break;
			if (sp.picnum == TRANSPORTERBEAM) {
				sp.xrepeat = 31;
				sp.yrepeat = 1;
				sp.z = sector[sprite[j].sectnum].floorz - (40 << 8);
			} else {
				if (sprite[j].statnum == 4) {
					sp.xrepeat = 8;
					sp.yrepeat = 8;
				} else {
					sp.xrepeat = 48;
					sp.yrepeat = 64;
					if (sprite[j].statnum == 10 || badguy(sprite[j]))
						sp.z -= (32 << 8);
				}
			}

			sp.shade = -127;
			sp.cstat = 128 | 2;
			sp.ang = sprite[j].ang;

			sp.xvel = 128;
			engine.changespritestat(i, (short) 5);
			ssp(i, CLIPMASK0);
			engine.setsprite(i, sp.x, sp.y, sp.z);
			break;

		case FRAMEEFFECT1:
			if (j >= 0) {
				sp.xrepeat = sprite[j].xrepeat;
				sp.yrepeat = sprite[j].yrepeat;
				hittype[i].temp_data[1] = sprite[j].picnum;
			} else
				sp.xrepeat = sp.yrepeat = 0;

			engine.changespritestat(i, (short) 5);

			break;

		case LASERLINE:
			sp.yrepeat = 6;
			sp.xrepeat = 32;

			if (currentGame.getCON().lasermode == 1)
				sp.cstat = 16 + 2;
			else if (currentGame.getCON().lasermode == 0 || currentGame.getCON().lasermode == 2)
				sp.cstat = 16;
			else {
				sp.xrepeat = 0;
				sp.yrepeat = 0;
			}

			if (j >= 0)
				sp.ang = (short) (hittype[j].temp_data[5] + 512);
			engine.changespritestat(i, (short) 5);
			break;

		case FORCESPHERE:
			if (j == -1) {
				sp.cstat = (short) 32768;
				engine.changespritestat(i, (short) 2);
			} else {
				sp.xrepeat = sp.yrepeat = 1;
				engine.changespritestat(i, (short) 5);
			}
			break;

		case BLOOD:
			sp.xrepeat = sp.yrepeat = 16;
			sp.z -= (26 << 8);
			if (j >= 0 && sprite[j].pal == 6)
				sp.pal = 6;
			engine.changespritestat(i, (short) 5);
			break;
		case BLOODPOOL:
		case PUKE:
		case LAVAPOOL:
			if (sp.picnum == LAVAPOOL && currentGame.getCON().type != 20) // Twentieth Anniversary World Tour
				return i;

		{
			short s1 = engine.updatesector(sp.x + 108, sp.y + 108, sp.sectnum);
			if (s1 >= 0 && sector[s1].floorz == sector[sp.sectnum].floorz) {
				s1 = engine.updatesector(sp.x - 108, sp.y - 108, s1);
				if (s1 >= 0 && sector[s1].floorz == sector[sp.sectnum].floorz) {
					s1 = engine.updatesector(sp.x + 108, sp.y - 108, s1);
					if (s1 >= 0 && sector[s1].floorz == sector[sp.sectnum].floorz) {
						s1 = engine.updatesector(sp.x - 108, sp.y + 108, s1);
						if (s1 >= 0 && sector[s1].floorz != sector[sp.sectnum].floorz) {
							sp.xrepeat = sp.yrepeat = 0;
							engine.changespritestat(i, (short) 5);
							break;
						}
					} else {
						sp.xrepeat = sp.yrepeat = 0;
						engine.changespritestat(i, (short) 5);
						break;
					}
				} else {
					sp.xrepeat = sp.yrepeat = 0;
					engine.changespritestat(i, (short) 5);
					break;
				}
			} else {
				sp.xrepeat = sp.yrepeat = 0;
				engine.changespritestat(i, (short) 5);
				break;
			}
		}

			if (sector[sprite[i].sectnum].lotag == 1) {
				engine.changespritestat(i, (short) 5);
				break;
			}

			if (j >= 0 && sp.picnum != PUKE) {
				if (sprite[j].pal == 1)
					sp.pal = 1;
				else if (sprite[j].pal != 6 && sprite[j].picnum != NUKEBARREL && sprite[j].picnum != TIRE) {
					if (sprite[j].picnum == FECES)
						sp.pal = 7; // Brown
					else
						sp.pal = 2; // Red
				} else
					sp.pal = 0; // green

				if (sprite[j].picnum == TIRE)
					sp.shade = 127;
			}
			sp.cstat |= 32;
			if (sp.picnum == LAVAPOOL) { // Twentieth Anniversary World Tour
				int fz = engine.getflorzofslope(sp.sectnum, sp.x, sp.y);
				if (fz != sp.z)
					sp.z = fz;
				sp.z -= 200;
			}
		case FECES:
			if (j >= 0)
				sp.xrepeat = sp.yrepeat = 1;
			engine.changespritestat(i, (short) 5);
			break;

		case BLOODSPLAT1:
		case BLOODSPLAT2:
		case BLOODSPLAT3:
		case BLOODSPLAT4:
			sp.cstat |= 16;
			sp.xrepeat = (short) (7 + (engine.krand() & 7));
			sp.yrepeat = (short) (7 + (engine.krand() & 7));
			sp.z -= (16 << 8);
			if (j >= 0 && sprite[j].pal == 6)
				sp.pal = 6;
			insertspriteq(i);
			engine.changespritestat(i, (short) 5);
			break;

		case TRIPBOMB:
			if (sp.lotag > ud.player_skill) {
				sp.xrepeat = sp.yrepeat = 0;
				engine.changespritestat(i, (short) 5);
				break;
			}

			sp.xrepeat = 4;
			sp.yrepeat = 5;

			sp.owner = i;
			sp.hitag = i;

			sp.xvel = 16;
			ssp(i, CLIPMASK0);
			hittype[i].temp_data[0] = 17;
			hittype[i].temp_data[2] = 0;
			hittype[i].temp_data[5] = sp.ang;

		case SPACEMARINE:
			if (sp.picnum == SPACEMARINE) {
				sp.extra = 20;
				sp.cstat |= 257;
			}
			engine.changespritestat(i, (short) 2);
			break;

		case HYDRENT:
		case PANNEL1:
		case PANNEL2:
		case SATELITE:
		case FUELPOD:
		case SOLARPANNEL:
		case ANTENNA:
		case GRATE1:
		case CHAIR1:
		case CHAIR2:
		case CHAIR3:
		case BOTTLE1:
		case BOTTLE2:
		case BOTTLE3:
		case BOTTLE4:
		case BOTTLE5:
		case BOTTLE6:
		case BOTTLE7:
		case BOTTLE8:
		case BOTTLE10:
		case BOTTLE11:
		case BOTTLE12:
		case BOTTLE13:
		case BOTTLE14:
		case BOTTLE15:
		case BOTTLE16:
		case BOTTLE17:
		case BOTTLE18:
		case BOTTLE19:
		case OCEANSPRITE1:
		case OCEANSPRITE2:
		case OCEANSPRITE3:
		case OCEANSPRITE5:
		case MONK:
		case INDY:
		case LUKE:
		case JURYGUY:
		case SCALE:
		case VACUUM:
		case FANSPRITE:
		case CACTUS:
		case CACTUSBROKE:
		case HANGLIGHT:
		case FETUS:
		case FETUSBROKE:
		case CAMERALIGHT:
		case MOVIECAMERA:
		case IVUNIT:
		case POT1:
		case POT2:
		case POT3:
		case TRIPODCAMERA:
		case SUSHIPLATE1:
		case SUSHIPLATE2:
		case SUSHIPLATE3:
		case SUSHIPLATE4:
		case SUSHIPLATE5:
		case WAITTOBESEATED:
		case VASE:
		case PIPE1:
		case PIPE2:
		case PIPE3:
		case PIPE4:
		case PIPE5:
		case PIPE6:
			sp.clipdist = 32;
			sp.cstat |= 257;
		case OCEANSPRITE4:
			engine.changespritestat(i, (short) 0);
			break;
		case FEMMAG1:
		case FEMMAG2:
			sp.cstat &= ~257;
			engine.changespritestat(i, (short) 0);
			break;
		case DUKETAG:
		case SIGN1:
		case SIGN2:
			if (ud.multimode < 2 && sp.pal != 0) {
				sp.xrepeat = sp.yrepeat = 0;
				engine.changespritestat(i, (short) 5);
			} else
				sp.pal = 0;
			break;
		case MASKWALL1:
		case MASKWALL2:
		case MASKWALL3:
		case MASKWALL4:
		case MASKWALL5:
		case MASKWALL6:
		case MASKWALL7:
		case MASKWALL8:
		case MASKWALL9:
		case MASKWALL10:
		case MASKWALL11:
		case MASKWALL12:
		case MASKWALL13:
		case MASKWALL14:
		case MASKWALL15:
			j = sp.cstat & 60;
			sp.cstat = (short) (j | 1);
			engine.changespritestat(i, (short) 0);
			break;
		case FOOTPRINTS:
		case FOOTPRINTS2:
		case FOOTPRINTS3:
		case FOOTPRINTS4:
			if (j >= 0) {
				short s1 = engine.updatesector(sp.x + 84, sp.y + 84, sp.sectnum);
				if (s1 >= 0 && sector[s1].floorz == sector[sp.sectnum].floorz) {
					s1 = engine.updatesector(sp.x - 84, sp.y - 84, s1);
					if (s1 >= 0 && sector[s1].floorz == sector[sp.sectnum].floorz) {
						s1 = engine.updatesector(sp.x + 84, sp.y - 84, s1);
						if (s1 >= 0 && sector[s1].floorz == sector[sp.sectnum].floorz) {
							s1 = engine.updatesector(sp.x - 84, sp.y + 84, s1);
							if (s1 >= 0 && sector[s1].floorz != sector[sp.sectnum].floorz) {
								sp.xrepeat = sp.yrepeat = 0;
								engine.changespritestat(i, (short) 5);
								break;
							}
						} else {
							sp.xrepeat = sp.yrepeat = 0;
							break;
						}
					} else {
						sp.xrepeat = sp.yrepeat = 0;
						break;
					}
				} else {
					sp.xrepeat = sp.yrepeat = 0;
					break;
				}

				sp.cstat = (short) (32 + ((ps[sprite[j].yvel].footprintcount & 1) << 2));
				sp.ang = sprite[j].ang;
			}

			sp.z = sector[sect].floorz;
			if (sector[sect].lotag != 1 && sector[sect].lotag != 2)
				sp.xrepeat = sp.yrepeat = 32;

			insertspriteq(i);
			engine.changespritestat(i, (short) 5);
			break;

		case FEM1:
		case FEM2:
		case FEM3:
		case FEM4:
		case FEM5:
		case FEM6:
		case FEM7:
		case FEM8:
		case FEM9:
		case FEM10:
		case PODFEM1:
		case NAKED1:
		case STATUE:
		case TOUGHGAL:
			sp.yvel = sp.hitag;
			sp.hitag = -1;
			if (sp.picnum == PODFEM1)
				sp.extra <<= 1;
		case BLOODYPOLE:

		case QUEBALL:
		case STRIPEBALL:

			if (sp.picnum == QUEBALL || sp.picnum == STRIPEBALL) {
				sp.cstat = 256;
				sp.clipdist = 8;
			} else {
				sp.cstat |= 257;
				sp.clipdist = 32;
			}

			engine.changespritestat(i, (short) 2);
			break;

		case DUKELYINGDEAD:
			if (j >= 0 && sprite[j].picnum == APLAYER) {
				sp.xrepeat = sprite[j].xrepeat;
				sp.yrepeat = sprite[j].yrepeat;
				sp.shade = sprite[j].shade;
				sp.pal = ps[sprite[j].yvel].palookup;
			}
		case DUKECAR:
		case HELECOPT:
//	                if(sp.picnum == HELECOPT || sp.picnum == DUKECAR) sp.xvel = 1024;
			sp.cstat = 0;
			sp.extra = 1;
			sp.xvel = 292;
			sp.zvel = 360;
		case RESPAWNMARKERRED:
		case BLIMP:

			if (sp.picnum == RESPAWNMARKERRED) {
				sp.xrepeat = sp.yrepeat = 24;
				if (j >= 0)
					sp.z = hittype[j].floorz; // -(1<<4);
			} else {
				sp.cstat |= 257;
				sp.clipdist = 128;
			}
		case MIKE:
			if (sp.picnum == MIKE)
				sp.yvel = sp.hitag;
		case WEATHERWARN:
			engine.changespritestat(i, (short) 1);
			break;

		case SPOTLITE:
			hittype[i].temp_data[0] = sp.x;
			hittype[i].temp_data[1] = sp.y;
			break;
		case BULLETHOLE:
			sp.xrepeat = sp.yrepeat = 3;
			sp.cstat = (short) (16 + (engine.krand() & 12));
			insertspriteq(i);
		case MONEY:
		case MAIL:
		case PAPER:
			if (sp.picnum == MONEY || sp.picnum == MAIL || sp.picnum == PAPER) {
				hittype[i].temp_data[0] = engine.krand() & 2047;
				sp.cstat = (short) (engine.krand() & 12);
				sp.xrepeat = sp.yrepeat = 8;
				sp.ang = (short) (engine.krand() & 2047);
			}
			engine.changespritestat(i, (short) 5);
			break;

		case VIEWSCREEN:
		case VIEWSCREEN2:
			sp.owner = i;
			sp.lotag = 1;
			sp.extra = 1;
			engine.changespritestat(i, (short) 6);
			break;

		case SHELL: // From the player
		case SHOTGUNSHELL:
			if (j >= 0) {
				short snum, a;

				if (sprite[j].picnum == APLAYER) {
					snum = sprite[j].yvel;
					a = (short) (ps[snum].ang - (engine.krand() & 63) + 8); // Fine tune

					hittype[i].temp_data[0] = engine.krand() & 1;
					if (sp.picnum == SHOTGUNSHELL)
						sp.z = (6 << 8) + ps[snum].pyoff + ps[snum].posz
								- ((ps[snum].horizoff + (int) ps[snum].horiz - 100) << 4);
					else
						sp.z = (3 << 8) + ps[snum].pyoff + ps[snum].posz
								- ((ps[snum].horizoff + (int) ps[snum].horiz - 100) << 4);
					sp.zvel = (short) -(engine.krand() & 255);
				} else {
					a = sp.ang;
					sp.z = sprite[j].z - PHEIGHT + (3 << 8);
				}

				sp.x = sprite[j].x + (sintable[(a + 512) & 2047] >> 7);
				sp.y = sprite[j].y + (sintable[a & 2047] >> 7);

				sp.shade = -8;

				sp.ang = (short) (a - 512);
				sp.xvel = 20;

				sp.xrepeat = sp.yrepeat = 4;

				engine.changespritestat(i, (short) 5);
			}
			break;

		case RESPAWN:
			sp.extra = 66 - 13;
		case MUSICANDSFX:
			if (ud.multimode < 2 && sp.pal == 1) {
				sp.xrepeat = sp.yrepeat = 0;
				engine.changespritestat(i, (short) 5);
				break;
			}
			sp.cstat = (short) 32768;
			engine.changespritestat(i, (short) 11);
			break;

		case EXPLOSION2:
		case EXPLOSION2BOT:
		case BURNING:
		case BURNING2:
		case SMALLSMOKE:
		case SHRINKEREXPLOSION:
		case COOLEXPLOSION1:
		case ONFIRE:
			// Twentieth Anniversary World Tour
			if (sp.picnum == ONFIRE && currentGame.getCON().type != 20)
				break;

			if (j >= 0) {
				sp.ang = sprite[j].ang;
				sp.shade = -64;
				sp.cstat = (short) (128 | (engine.krand() & 4));
			}

			if (sp.picnum == EXPLOSION2 || sp.picnum == EXPLOSION2BOT) {
				sp.xrepeat = 48;
				sp.yrepeat = 48;
				sp.shade = -127;
				sp.cstat |= 128;
			} else if (sp.picnum == SHRINKEREXPLOSION) {
				sp.xrepeat = 32;
				sp.yrepeat = 32;
			} else if (sp.picnum == SMALLSMOKE || sp.picnum == ONFIRE) {
				// 64 "money"
				sp.xrepeat = 24;
				sp.yrepeat = 24;
			} else if (sp.picnum == BURNING || sp.picnum == BURNING2) {
				sp.xrepeat = 4;
				sp.yrepeat = 4;
			}

			if (j >= 0) {
				x = engine.getflorzofslope(sp.sectnum, sp.x, sp.y);
				if (sp.z > x - (12 << 8))
					sp.z = x - (12 << 8);
			}

			if (sp.picnum == ONFIRE) {
				sp.x += engine.krand() % 256 - 128;
				sp.y += engine.krand() % 256 - 128;
				sp.z -= engine.krand() % 10240;
				sp.cstat |= 0x80;
			}

			engine.changespritestat(i, (short) 5);

			break;

		case PLAYERONWATER:
			if (j >= 0) {
				sp.xrepeat = sprite[j].xrepeat;
				sp.yrepeat = sprite[j].yrepeat;
				sp.zvel = 128;
				if (sector[sp.sectnum].lotag != 2)
					sp.cstat |= 32768;
			}
			engine.changespritestat(i, (short) 13);
			break;

		case APLAYER:
			sp.xrepeat = sp.yrepeat = 0;
			j = ud.coop;
			if (j == 2)
				j = 0;

			if (ud.multimode < 2 || (ud.multimode > 1 && j != sp.lotag))
				engine.changespritestat(i, (short) 5);
			else
				engine.changespritestat(i, (short) 10);
			break;
		case WATERBUBBLE:
			if (j >= 0 && sprite[j].picnum == APLAYER)
				sp.z -= (16 << 8);
			if (sp.picnum == WATERBUBBLE) {
				if (j >= 0)
					sp.ang = sprite[j].ang;
				sp.xrepeat = sp.yrepeat = 4;
			} else
				sp.xrepeat = sp.yrepeat = 32;

			engine.changespritestat(i, (short) 5);
			break;

		case CRANE:

			sp.cstat |= 64 | 257;

			sp.picnum += 2;
			sp.z = sector[sect].ceilingz + (48 << 8);
			hittype[i].temp_data[4] = tempwallptr;

			msx[tempwallptr] = sp.x;
			msy[tempwallptr] = sp.y;
			msx[tempwallptr + 2] = sp.z;

			s = headspritestat[0];
			while (s >= 0) {
				if (sprite[s].picnum == CRANEPOLE && sprite[i].hitag == (sprite[s].hitag)) {
					msy[tempwallptr + 2] = s;

					hittype[i].temp_data[1] = sprite[s].sectnum;

					sprite[s].xrepeat = 48;
					sprite[s].yrepeat = 128;

					msx[tempwallptr + 1] = sprite[s].x;
					msy[tempwallptr + 1] = sprite[s].y;

					sprite[s].x = sp.x;
					sprite[s].y = sp.y;
					sprite[s].z = sp.z;
					sprite[s].shade = sp.shade;

					engine.setsprite(s, sprite[s].x, sprite[s].y, sprite[s].z);
					break;
				}
				s = nextspritestat[s];
			}

			tempwallptr += 3;
			sp.owner = -1;
			sp.extra = 8;
			engine.changespritestat(i, (short) 6);
			break;

		case WATERDRIP:
			if (j >= 0 && (sprite[j].statnum == 10 || sprite[j].statnum == 1)) {
				sp.shade = 32;
				if (sprite[j].pal != 1) {
					sp.pal = 2;
					sp.z -= (18 << 8);
				} else
					sp.z -= (13 << 8);
				sp.ang = engine.getangle(ps[connecthead].posx - sp.x, ps[connecthead].posy - sp.y);
				sp.xvel = (short) (48 - (engine.krand() & 31));
				ssp(i, CLIPMASK0);
			} else if (j == -1) {
				sp.z += (4 << 8);
				hittype[i].temp_data[0] = sp.z;
				hittype[i].temp_data[1] = engine.krand() & 127;
			}
		case TRASH:

			if (sp.picnum != WATERDRIP)
				sp.ang = (short) (engine.krand() & 2047);

		case WATERDRIPSPLASH:

			sp.xrepeat = 24;
			sp.yrepeat = 24;

			engine.changespritestat(i, (short) 6);
			break;

		case PLUG:
			sp.lotag = 9999;
			engine.changespritestat(i, (short) 6);
			break;
		case TOUCHPLATE:
			hittype[i].temp_data[2] = sector[sect].floorz;
			if (sector[sect].lotag != 1 && sector[sect].lotag != 2)
				sector[sect].floorz = sp.z;

			if (currentGame.getCON().type != 20) {
				if (sp.pal != 0 && ud.multimode > 1) {
					sp.xrepeat = sp.yrepeat = 0;
					engine.changespritestat(i, (short) 5);
					break;
				}
			} else { // Twentieth Anniversary World Tour addition
				if ((sp.pal == 1 && ud.multimode > 1) // Single-game Only
						|| (sp.pal == 2 && (ud.multimode == 1 || ud.multimode > 1 && ud.coop != 1)) // Co-op Only
						|| (sp.pal == 3 && (ud.multimode == 1 || ud.multimode > 1 && ud.coop == 1))) // Dukematch Only
				{
					sp.xrepeat = sp.yrepeat = 0;
					engine.changespritestat(i, (short) 5);
					break;
				}
			}
		case WATERBUBBLEMAKER:
			sp.cstat |= 32768;
			engine.changespritestat(i, (short) 6);
			break;
		case BOLT1:
		case BOLT1 + 1:
		case BOLT1 + 2:
		case BOLT1 + 3:
		case SIDEBOLT1:
		case SIDEBOLT1 + 1:
		case SIDEBOLT1 + 2:
		case SIDEBOLT1 + 3:
			hittype[i].temp_data[0] = sp.xrepeat;
			hittype[i].temp_data[1] = sp.yrepeat;
		case MASTERSWITCH:
			if (sp.picnum == MASTERSWITCH)
				sp.cstat |= 32768;
			sp.yvel = 0;
			engine.changespritestat(i, (short) 6);
			break;
		case TARGET:
		case DUCK:
		case LETTER:
			sp.extra = 1;
			sp.cstat |= 257;
			engine.changespritestat(i, (short) 1);
			break;
		case OCTABRAINSTAYPUT:
		case LIZTROOPSTAYPUT:
		case PIGCOPSTAYPUT:
		case LIZMANSTAYPUT:
		case BOSS1STAYPUT:
		case BOSS2STAYPUT:
		case BOSS3STAYPUT:
		case BOSS4STAYPUT:
		case PIGCOPDIVE:
		case COMMANDERSTAYPUT:
			hittype[i].actorstayput = sp.sectnum;
		case BOSS1:
		case BOSS2:
		case BOSS3:
		case BOSS4:
		case ROTATEGUN:
		case GREENSLIME:
			if (sp.picnum == GREENSLIME)
				sp.extra = 1;
		case DRONE:
		case LIZTROOPONTOILET:
		case LIZTROOPJUSTSIT:
		case LIZTROOPSHOOT:
		case LIZTROOPJETPACK:
		case LIZTROOPDUCKING:
		case LIZTROOPRUNNING:
		case LIZTROOP:
		case OCTABRAIN:
		case COMMANDER:
		case PIGCOP:
		case LIZMAN:
		case LIZMANSPITTING:
		case LIZMANFEEDING:
		case LIZMANJUMP:
		case ORGANTIC:
		case RAT:
		case SHARK:

			if (sp.pal == 0) {
				switch (sp.picnum) {
				case LIZTROOPONTOILET:
				case LIZTROOPSHOOT:
				case LIZTROOPJETPACK:
				case LIZTROOPDUCKING:
				case LIZTROOPRUNNING:
				case LIZTROOPSTAYPUT:
				case LIZTROOPJUSTSIT:
				case LIZTROOP:
					sp.pal = 22;
					break;
				}
			}

			if (bossguy(sp.picnum)) {
				if (j >= 0 && sprite[j].picnum == RESPAWN)
					sp.pal = sprite[j].pal;
				if (sp.pal != 0) {
					sp.clipdist = 80;
					sp.xrepeat = 40;
					sp.yrepeat = 40;
				} else {
					sp.xrepeat = 80;
					sp.yrepeat = 80;
					sp.clipdist = 164;
				}
			} else {
				if (sp.picnum != SHARK) {
					sp.xrepeat = 40;
					sp.yrepeat = 40;
					sp.clipdist = 80;
				} else {
					sp.xrepeat = 60;
					sp.yrepeat = 60;
					sp.clipdist = 40;
				}
			}

			if (j >= 0)
				sp.lotag = 0;

			if ((sp.lotag > ud.player_skill) || ud.monsters_off) {
				sp.xrepeat = sp.yrepeat = 0;
				engine.changespritestat(i, (short) 5);
				break;
			} else {
				makeitfall(currentGame.getCON(), i);

				if (sp.picnum == RAT) {
					sp.ang = (short) (engine.krand() & 2047);
					sp.xrepeat = sp.yrepeat = 48;
					sp.cstat = 0;
				} else {
					sp.cstat |= 257;

					if (sp.picnum != SHARK)
						ps[connecthead].max_actors_killed++;
				}

				if (sp.picnum == ORGANTIC)
					sp.cstat |= 128;

				if (j >= 0) {
					hittype[i].timetosleep = 0;
					check_fta_sounds(i);
					engine.changespritestat(i, (short) 1);
				} else
					engine.changespritestat(i, (short) 2);
			}

			if (sp.picnum == ROTATEGUN)
				sp.zvel = 0;

			break;

		case LOCATORS:
			sp.cstat |= 32768;
			engine.changespritestat(i, (short) 7);
			break;

		case ACTIVATORLOCKED:
		case ACTIVATOR:
			sp.cstat = (short) 32768;
			if (sp.picnum == ACTIVATORLOCKED)
				sector[sp.sectnum].lotag |= 16384;
			engine.changespritestat(i, (short) 8);
			break;

		case DOORSHOCK:
			sp.cstat |= 1 + 256;
			sp.shade = -12;
			engine.changespritestat(i, (short) 6);
			break;

		case OOZ:
		case OOZ2:
			sp.shade = -12;

			if (j >= 0) {
				if (sprite[j].picnum == NUKEBARREL)
					sp.pal = 8;
				insertspriteq(i);
			}

			engine.changespritestat(i, (short) 1);

			getglobalz(i);

			j = (hittype[i].floorz - hittype[i].ceilingz) >> 9;

			sp.yrepeat = (short) j;
			sp.xrepeat = (short) (25 - (j >> 1));
			sp.cstat |= (engine.krand() & 4);

			break;

		case HEAVYHBOMB:
			if (j >= 0)
				sp.owner = (short) j;
			else
				sp.owner = i;
			sp.xrepeat = sp.yrepeat = 9;
			sp.yvel = 4;
		case REACTOR2:
		case REACTOR:
		case RECON:
			if (sp.picnum == RECON) {
				if (sp.lotag > ud.player_skill) {
					sp.xrepeat = sp.yrepeat = 0;
					engine.changespritestat(i, (short) 5);
					game.pInt.setsprinterpolate(i, sprite[i]);
					return i;
				}
				ps[connecthead].max_actors_killed++;
				hittype[i].temp_data[5] = 0;
				if (ud.monsters_off) {
					sp.xrepeat = sp.yrepeat = 0;
					engine.changespritestat(i, (short) 5);
					break;
				}
				sp.extra = 130;
			}

			if (sp.picnum == REACTOR || sp.picnum == REACTOR2)
				sp.extra = (short) currentGame.getCON().impact_damage;

			sprite[i].cstat |= 257; // Make it hitable

			if (ud.multimode < 2 && sp.pal != 0) {
				sp.xrepeat = sp.yrepeat = 0;
				engine.changespritestat(i, (short) 5);
				break;
			}
			sp.pal = 0;
			sprite[i].shade = -17;

			engine.changespritestat(i, (short) 2);
			break;

		case FLAMETHROWERSPRITE:
		case FLAMETHROWERAMMO: // Twentieth Anniversary World Tour
			if (currentGame.getCON().type != 20)
				break;

		case ATOMICHEALTH:
		case STEROIDS:
		case HEATSENSOR:
		case SHIELD:
		case AIRTANK:
		case TRIPBOMBSPRITE:
		case JETPACK:
		case HOLODUKE:

		case FIRSTGUNSPRITE:
		case CHAINGUNSPRITE:
		case SHOTGUNSPRITE:
		case RPGSPRITE:
		case SHRINKERSPRITE:
		case FREEZESPRITE:
		case DEVISTATORSPRITE:

		case SHOTGUNAMMO:
		case FREEZEAMMO:
		case HBOMBAMMO:
		case CRYSTALAMMO:
		case GROWAMMO:
		case BATTERYAMMO:
		case DEVISTATORAMMO:
		case RPGAMMO:
		case BOOTS:
		case AMMO:
		case AMMOLOTS:
		case COLA:
		case FIRSTAID:
		case SIXPAK:

			if (j >= 0) {
				sp.lotag = 0;
				sp.z -= (32 << 8);
				sp.zvel = -1024;
				ssp(i, CLIPMASK0);
				sp.cstat = (short) (engine.krand() & 4);
			} else {
				sp.owner = i;
				sp.cstat = 0;
			}

			if ((ud.multimode < 2 && sp.pal != 0) || (sp.lotag > ud.player_skill)) {
				sp.xrepeat = sp.yrepeat = 0;
				engine.changespritestat(i, (short) 5);
				break;
			}

			sp.pal = 0;

		case ACCESSCARD:

			if (sp.picnum == ATOMICHEALTH)
				sp.cstat |= 128;

			if (ud.multimode > 1 && ud.coop != 1 && sp.picnum == ACCESSCARD) {
				sp.xrepeat = sp.yrepeat = 0;
				engine.changespritestat(i, (short) 5);
				break;
			} else {
				if (sp.picnum == AMMO)
					sp.xrepeat = sp.yrepeat = 16;
				else
					sp.xrepeat = sp.yrepeat = 32;
			}

			sp.shade = -17;

			if (j >= 0)
				engine.changespritestat(i, (short) 1);
			else {
				engine.changespritestat(i, (short) 2);
				makeitfall(currentGame.getCON(), i);
			}
			break;

		case WATERFOUNTAIN:
			sprite[i].lotag = 1;

		case TREE1:
		case TREE2:
		case TIRE:
		case CONE:
		case BOX:
			sprite[i].cstat = 257; // Make it hitable
			sprite[i].extra = 1;
			engine.changespritestat(i, (short) 6);
			break;

		case FLOORFLAME:
			sp.shade = -127;
			engine.changespritestat(i, (short) 6);
			break;

		case BOUNCEMINE:
			sp.owner = i;
			sp.cstat |= 1 + 256; // Make it hitable
			sp.xrepeat = sp.yrepeat = 24;
			sp.shade = -127;
			sp.extra = (short) (currentGame.getCON().impact_damage << 2);
			engine.changespritestat(i, (short) 2);
			break;

		case CAMERA1:
		case CAMERA1 + 1:
		case CAMERA1 + 2:
		case CAMERA1 + 3:
		case CAMERA1 + 4:
		case CAMERAPOLE:
			sp.extra = 1;

			if (currentGame.getCON().camerashitable != 0)
				sp.cstat = 257;
			else
				sp.cstat = 0;

		case GENERICPOLE:

			if (ud.multimode < 2 && sp.pal != 0) {
				sp.xrepeat = sp.yrepeat = 0;
				engine.changespritestat(i, (short) 5);
				break;
			} else
				sp.pal = 0;
			if (sp.picnum == CAMERAPOLE || sp.picnum == GENERICPOLE)
				break;
			sp.picnum = CAMERA1;
			engine.changespritestat(i, (short) 1);
			break;
		case STEAM:
			if (j >= 0) {
				sp.ang = sprite[j].ang;
				sp.cstat = 16 + 128 + 2;
				sp.xrepeat = sp.yrepeat = 1;
				sp.xvel = -8;
				ssp(i, CLIPMASK0);
			}
		case CEILINGSTEAM:
			engine.changespritestat(i, (short) 6);
			break;

		case SECTOREFFECTOR:
			sp.yvel = sector[sect].extra;
			sp.cstat |= 32768;
			sp.xrepeat = sp.yrepeat = 0;

			switch (sp.lotag) {
			case 28:
				hittype[i].temp_data[5] = 65;// Delay for lightning
				break;
			case 7: // Transporters!!!!
			case 23:// XPTR END
				if (sp.lotag != 23) {
					for (j = 0; j < MAXSPRITES; j++)
						if (sprite[j].statnum < MAXSTATUS && sprite[j].picnum == SECTOREFFECTOR
								&& (sprite[j].lotag == 7 || sprite[j].lotag == 23) && i != j
								&& sprite[j].hitag == sprite[i].hitag) {
							sprite[i].owner = (short) j;
							break;
						}
				} else
					sprite[i].owner = i;

				hittype[i].temp_data[4] = (sector[sect].floorz == sprite[i].z) ? 1 : 0;
				sp.cstat = 0;
				engine.changespritestat(i, (short) 9);
				game.pInt.setsprinterpolate(i, sprite[i]);
				return i;
			case 1:
				sp.owner = -1;
				hittype[i].temp_data[0] = 1;
				break;
			case 18:

				if (sp.ang == 512) {
					hittype[i].temp_data[1] = sector[sect].ceilingz;
					if (sp.pal != 0)
						sector[sect].ceilingz = sp.z;
				} else {
					hittype[i].temp_data[1] = sector[sect].floorz;
					if (sp.pal != 0)
						sector[sect].floorz = sp.z;
				}

				sp.hitag <<= 2;
				break;

			case 19:
				sp.owner = -1;
				break;
			case 25: // Pistons
				hittype[i].temp_data[3] = sector[sect].ceilingz;
				hittype[i].temp_data[4] = 1;
				sector[sect].ceilingz = sp.z;
				game.pInt.setceilinterpolate(sect, sector[sect]); // ceilinz
				break;
			case 35:
				sector[sect].ceilingz = sp.z;
				break;
			case 27:
				if (ud.recstat == 1) {
					sp.xrepeat = sp.yrepeat = 64;
					sp.cstat &= 32767;
				}
				break;
			case 12:

				hittype[i].temp_data[1] = sector[sect].floorshade;
				hittype[i].temp_data[2] = sector[sect].ceilingshade;
				break;

			case 13:

				hittype[i].temp_data[0] = sector[sect].ceilingz;
				hittype[i].temp_data[1] = sector[sect].floorz;

				if (klabs(hittype[i].temp_data[0] - sp.z) < klabs(hittype[i].temp_data[1] - sp.z))
					sp.owner = 1;
				else
					sp.owner = 0;

				if (sp.ang == 512) {
					if (sp.owner != 0)
						sector[sect].ceilingz = sp.z;
					else
						sector[sect].floorz = sp.z;
				} else
					sector[sect].ceilingz = sector[sect].floorz = sp.z;

				if ((sector[sect].ceilingstat & 1) != 0) {
					sector[sect].ceilingstat ^= 1;
					hittype[i].temp_data[3] = 1;

					if (sp.owner == 0 && sp.ang == 512) {
						sector[sect].ceilingstat ^= 1;
						hittype[i].temp_data[3] = 0;
					}

					sector[sect].ceilingshade = sector[sect].floorshade;

					if (sp.ang == 512) {
						startwall = sector[sect].wallptr;
						endwall = startwall + sector[sect].wallnum;
						for (j = startwall; j < endwall; j++) {
							x = wall[j].nextsector;
							if (x >= 0)
								if ((sector[x].ceilingstat & 1) == 0) {
									sector[sect].ceilingpicnum = sector[x].ceilingpicnum;
									sector[sect].ceilingshade = sector[x].ceilingshade;
									break; // Leave earily
								}
						}
					}
				}

				break;

			case 17:

				hittype[i].temp_data[2] = sector[sect].floorz; // Stopping loc

				j = engine.nextsectorneighborz(sect, sector[sect].floorz, -1, -1);
				hittype[i].temp_data[3] = sector[j].ceilingz;

				j = engine.nextsectorneighborz(sect, sector[sect].ceilingz, 1, 1);
				hittype[i].temp_data[4] = sector[j].floorz;

				game.pInt.setceilinterpolate(sect, sector[sect]);
				game.pInt.setfloorinterpolate(sect, sector[sect]);

				break;

			case 24:
				sp.yvel <<= 1;
			case 36:
				break;

			case 20: {
				long q;

				startwall = sector[sect].wallptr;
				endwall = startwall + sector[sect].wallnum;

				// find the two most clostest wall x's and y's
				q = 0x7fffffff;

				for (s = startwall; s < endwall; s++) {
					x = wall[s].x;
					y = wall[s].y;

					d = FindDistance2D(sp.x - x, sp.y - y);
					if (d < q) {
						q = d;
						clostest = s;
					}
				}

				hittype[i].temp_data[1] = clostest;

				q = 0x7fffffff;

				for (s = startwall; s < endwall; s++) {
					x = wall[s].x;
					y = wall[s].y;
					d = FindDistance2D(sp.x - x, sp.y - y);

					if (d < q && s != hittype[i].temp_data[1]) {
						q = d;
						clostest = s;
					}
				}

				hittype[i].temp_data[2] = clostest;
			}

				break;

			case 3:

				hittype[i].temp_data[3] = sector[sect].floorshade;

				sector[sect].floorshade = sp.shade;
				sector[sect].ceilingshade = sp.shade;

				sp.owner = (short) (sector[sect].ceilingpal << 8);
				sp.owner |= sector[sect].floorpal;

				// fix all the walls;

				startwall = sector[sect].wallptr;
				endwall = startwall + sector[sect].wallnum;

				for (s = startwall; s < endwall; s++) {
					if ((wall[s].hitag & 1) == 0)
						wall[s].shade = sp.shade;
					if ((wall[s].cstat & 2) != 0 && wall[s].nextwall >= 0)
						wall[wall[s].nextwall].shade = sp.shade;
				}
				break;

			case 31:
				hittype[i].temp_data[1] = sector[sect].floorz;
				// hittype[i].temp_data[2] = sp.hitag;
				if (sp.ang != 1536)
					sector[sect].floorz = sp.z;

				startwall = sector[sect].wallptr;
				endwall = startwall + sector[sect].wallnum;

				for (s = startwall; s < endwall; s++)
					if (wall[s].hitag == 0)
						wall[s].hitag = 9999;

				game.pInt.setfloorinterpolate(sect, sector[sect]); // floorz

				break;
			case 32:
				hittype[i].temp_data[1] = sector[sect].ceilingz;
				hittype[i].temp_data[2] = sp.hitag;
				if (sp.ang != 1536)
					sector[sect].ceilingz = sp.z;

				startwall = sector[sect].wallptr;
				endwall = startwall + sector[sect].wallnum;

				for (s = startwall; s < endwall; s++)
					if (wall[s].hitag == 0)
						wall[s].hitag = 9999;

				game.pInt.setceilinterpolate(sect, sector[sect]); // ceiling

				break;

			case 4: // Flashing lights

				hittype[i].temp_data[2] = sector[sect].floorshade;

				startwall = sector[sect].wallptr;
				endwall = startwall + sector[sect].wallnum;

				sp.owner = (short) (sector[sect].ceilingpal << 8);
				sp.owner |= sector[sect].floorpal;

				for (s = startwall; s < endwall; s++)
					if (wall[s].shade > hittype[i].temp_data[3])
						hittype[i].temp_data[3] = wall[s].shade;

				break;

			case 9:
				if (sector[sect].lotag != 0 && klabs(sector[sect].ceilingz - sp.z) > 1024)
					sector[sect].lotag |= 32768; // If its open
			case 8:
				// First, get the ceiling-floor shade

				hittype[i].temp_data[0] = sector[sect].floorshade;
				hittype[i].temp_data[1] = sector[sect].ceilingshade;

				startwall = sector[sect].wallptr;
				endwall = startwall + sector[sect].wallnum;

				for (s = startwall; s < endwall; s++)
					if (wall[s].shade > hittype[i].temp_data[2])
						hittype[i].temp_data[2] = wall[s].shade;

				hittype[i].temp_data[3] = 1; // Take Out;

				break;

			case 11:// Pivitor rotater
				if (sp.ang > 1024)
					hittype[i].temp_data[3] = 2;
				else
					hittype[i].temp_data[3] = -2;
			case 0:
			case 2:// Earthquakemakers
			case 5:// Boss Creature
			case 6:// Subway
			case 14:// Caboos
			case 15:// Subwaytype sliding door
			case 16:// That rotating blocker reactor thing
			case 26:// ESCELATOR
			case 30:// No rotational subways

				if (sp.lotag == 0) {
					if (sector[sect].lotag == 30) {
						if (sp.pal != 0)
							sprite[i].clipdist = 1;
						else
							sprite[i].clipdist = 0;
						hittype[i].temp_data[3] = sector[sect].floorz;
						sector[sect].hitag = i;
					}

					for (j = 0; j < MAXSPRITES; j++) {
						if (sprite[j].statnum < MAXSTATUS)
							if (sprite[j].picnum == SECTOREFFECTOR && sprite[j].lotag == 1
									&& sprite[j].hitag == sp.hitag) {
								if (sp.ang == 512) {
									sp.x = sprite[j].x;
									sp.y = sprite[j].y;
								}
								break;
							}
					}
					if (j == MAXSPRITES) {
						Console.Println("Found lonely Sector Effector (lotag 0) at (" + sp.x + "," + sp.y + ")",
								OSDTEXT_RED);
						break;
					}
					sp.owner = (short) j;
				}

				startwall = sector[sect].wallptr;
				endwall = startwall + sector[sect].wallnum;

				hittype[i].temp_data[1] = tempwallptr;
				for (s = startwall; s < endwall; s++) {
					msx[tempwallptr] = wall[s].x - sp.x;
					msy[tempwallptr] = wall[s].y - sp.y;
					tempwallptr++;
					if (tempwallptr > 2047) {
						Console.Println("Too many moving sectors at (" + wall[s].x + "," + wall[s].y + ")",
								OSDTEXT_RED);
						break;
					}
				}
				if (sp.lotag == 30 || sp.lotag == 6 || sp.lotag == 14 || sp.lotag == 5) {

					startwall = sector[sect].wallptr;
					endwall = startwall + sector[sect].wallnum;

					if (sector[sect].hitag == -1)
						sp.extra = 0;
					else
						sp.extra = 1;

					sector[sect].hitag = i;

					j = 0;

					for (s = startwall; s < endwall; s++) {
						if (wall[s].nextsector >= 0 && sector[wall[s].nextsector].hitag == 0
								&& sector[wall[s].nextsector].lotag < 3) {
							s = wall[s].nextsector;
							j = 1;
							break;
						}
					}

					if (j == 0) {
						Console.Println("Subway found no zero'd sectors with locators\nat (" + sp.x + "," + sp.y + ")",
								OSDTEXT_RED);
						break;
					}

					sp.owner = -1;
					hittype[i].temp_data[0] = s;

					if (sp.lotag != 30)
						hittype[i].temp_data[3] = sp.hitag;
				}

				else if (sp.lotag == 16)
					hittype[i].temp_data[3] = sector[sect].ceilingz;

				else if (sp.lotag == 26) {
					hittype[i].temp_data[3] = sp.x;
					hittype[i].temp_data[4] = sp.y;
					if (sp.shade == sector[sect].floorshade) // UP
						sp.zvel = -256;
					else
						sp.zvel = 256;

					sp.shade = 0;
				} else if (sp.lotag == 2) {
					hittype[i].temp_data[5] = sector[sp.sectnum].floorheinum;
					sector[sp.sectnum].floorheinum = 0;
				}
			}

			switch (sp.lotag) {
			case 6:
			case 14:
				j = callsound(sect, i);
				if (j == -1)
					j = SUBWAY;
				hittype[i].lastvx = j;
			case 30:
				if (numplayers > 1 || mFakeMultiplayer)
					break;
			case 0:
			case 1:
			case 5:
			case 11:
			case 15:
			case 16:
			case 26:
				setsectinterpolate(i);
				break;
			}

			switch (sprite[i].lotag) {
			case 40:
			case 41:
				engine.changespritestat(i, (short) 15);
				if (rorcnt < 16) {
					if (sprite[i].lotag == 41)
						rortype[rorcnt] = 1; // ceiling
					if (sprite[i].lotag == 40)
						rortype[rorcnt] = 2; // floor
					rorsector[rorcnt++] = sprite[i].sectnum;
				}
				break;
			default:
				engine.changespritestat(i, (short) 3);
				break;
			}

			break;

		case SEENINE:
		case OOZFILTER:

			sp.shade = -16;
			if (sp.xrepeat <= 8) {
				sp.cstat = (short) 32768;
				sp.xrepeat = sp.yrepeat = 0;
			} else
				sp.cstat = 1 + 256;
			sp.extra = (short) (currentGame.getCON().impact_damage << 2);
			sp.owner = i;

			engine.changespritestat(i, (short) 6);
			break;

		case CRACK1:
		case CRACK2:
		case CRACK3:
		case CRACK4:
		case FIREEXT:
			if (sp.picnum == FIREEXT) {
				sp.cstat = 257;
				sp.extra = (short) (currentGame.getCON().impact_damage << 2);
			} else {
				sp.cstat |= 17;
				sp.extra = 1;
			}

			if (ud.multimode < 2 && sp.pal != 0) {
				sp.xrepeat = sp.yrepeat = 0;
				engine.changespritestat(i, (short) 5);
				break;
			}

			sp.pal = 0;
			sp.owner = i;
			engine.changespritestat(i, (short) 6);
			sp.xvel = 8;
			ssp(i, CLIPMASK0);
			break;

		case TOILET:
		case STALL:
			sp.lotag = 1;
			sp.cstat |= 257;
			sp.clipdist = 8;
			sp.owner = i;
			break;
		case CANWITHSOMETHING:
		case CANWITHSOMETHING2:
		case CANWITHSOMETHING3:
		case CANWITHSOMETHING4:
		case RUBBERCAN:
			sp.extra = 0;
		case EXPLODINGBARREL:
		case HORSEONSIDE:
		case FIREBARREL:
		case NUKEBARREL:
		case FIREVASE:
		case NUKEBARRELDENTED:
		case NUKEBARRELLEAKED:
		case WOODENHORSE:

			if (j >= 0)
				sp.xrepeat = sp.yrepeat = 32;
			sp.clipdist = 72;
			makeitfall(currentGame.getCON(), i);
			if (j >= 0)
				sp.owner = (short) j;
			else
				sp.owner = i;
		case EGG:
			if (ud.monsters_off && sp.picnum == EGG) {
				sp.xrepeat = sp.yrepeat = 0;
				engine.changespritestat(i, (short) 5);
			} else {
				if (sp.picnum == EGG) {
					sp.clipdist = 24;
					ps[connecthead].max_actors_killed++;
				}
				sp.cstat = (short) (257 | (engine.krand() & 4));
				engine.changespritestat(i, (short) 2);
			}
			break;
		case TOILETWATER:
			sp.shade = -16;
			engine.changespritestat(i, (short) 6);
			break;
		}

		game.pInt.setsprinterpolate(i, sprite[i]);
		return i;
	}

	public static void lotsofmoney(SPRITE s, int n) {
		for (int i = n; i > 0; i--) {
			int ang = engine.krand() & 2047;
			int sz = s.z - (engine.krand() % (47 << 8));
			int j = EGS(s.sectnum, s.x, s.y, sz, MONEY, -32, 8, 8, ang, 0, 0, 0, (short) 5);
			sprite[j].cstat = (short) (engine.krand() & 12);
		}
	}

	public static void lotsofmail(SPRITE s, int n) {
		for (int i = n; i > 0; i--) {
			int ang = engine.krand() & 2047;
			int sz = s.z - (engine.krand() % (47 << 8));
			int j = EGS(s.sectnum, s.x, s.y, sz, MAIL, -32, 8, 8, ang, 0, 0, 0, (short) 5);
			sprite[j].cstat = (short) (engine.krand() & 12);
		}
	}

	public static void lotsofpaper(SPRITE s, int n) {
		for (int i = n; i > 0; i--) {
			int ang = engine.krand() & 2047;
			int sz = s.z - (engine.krand() % (47 << 8));
			int j = EGS(s.sectnum, s.x, s.y, sz, PAPER, -32, 8, 8, ang, 0, 0, 0, (short) 5);
			sprite[j].cstat = (short) (engine.krand() & 12);
		}
	}

	public static void guts(SPRITE s, int gtype, int n, int p) {
		if (!isValidSector(s.sectnum))
			return;

		int gutz, floorz;
		int i;
		char sx, sy;
		byte pal;

		if (badguy(s) && s.xrepeat < 16)
			sx = sy = 8;
		else
			sx = sy = 32;

		gutz = s.z - (8 << 8);
		floorz = engine.getflorzofslope(s.sectnum, s.x, s.y);

		if (gutz > (floorz - (8 << 8)))
			gutz = floorz - (8 << 8);

		if (s.picnum == COMMANDER)
			gutz -= (24 << 8);

		if (badguy(s) && s.pal == 6)
			pal = 6;
		else
			pal = 0;

		for (int j = 0; j < n; j++) {
			int a = engine.krand() & 2047;

			int zv = -512 - (engine.krand() & 2047);
			int ve = 48 + (engine.krand() & 31);

			int esz = gutz - (engine.krand() & 8191);
			int esy = s.y + (engine.krand() & 255) - 128;
			int esx = s.x + (engine.krand() & 255) - 128;
			i = EGS(s.sectnum, esx, esy, esz, gtype, -32, sx, sy, a, ve, zv, ps[p].i, (short) 5);
			if (sprite[i].picnum == JIBS2) {
				sprite[i].xrepeat >>= 2;
				sprite[i].yrepeat >>= 2;
			}
			if (pal == 6)
				sprite[i].pal = 6;
		}
	}

	public static void gutsdir(SPRITE s, int gtype, int n, int p) {
		int gutz, floorz;
		char sx, sy;

		if (badguy(s) && s.xrepeat < 16)
			sx = sy = 8;
		else
			sx = sy = 32;

		gutz = s.z - (8 << 8);
		floorz = engine.getflorzofslope(s.sectnum, s.x, s.y);

		if (gutz > (floorz - (8 << 8)))
			gutz = floorz - (8 << 8);

		if (s.picnum == COMMANDER)
			gutz -= (24 << 8);

		for (int j = 0; j < n; j++) {
			int a = engine.krand() & 2047;
			int zv = -512 - (engine.krand() & 2047);
			int ve = 256 + (engine.krand() & 127);
			EGS(s.sectnum, s.x, s.y, gutz, gtype, -32, sx, sy, a, ve, zv, ps[p].i, (short) 5);
		}
	}

	public static boolean bossguy(int pn) {
		if (pn == BOSS4STAYPUT || pn == BOSS1 || pn == BOSS1STAYPUT || pn == BOSS2 || pn == BOSS2STAYPUT || pn == BOSS3
				|| pn == BOSS3STAYPUT || pn == BOSS4)
			return true;

		// Twentieth Anniversary World Tour
		// BOSS5
		return currentGame.getCON().type == 20 && (pn == 5310 || pn == 5311);
	}
}
