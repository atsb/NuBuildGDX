package ru.m210projects.Witchaven.AI;

import static ru.m210projects.Build.Engine.CLIPMASK0;
import static ru.m210projects.Build.Engine.sector;
import static ru.m210projects.Build.Engine.sintable;
import static ru.m210projects.Build.Engine.sprite;
import static ru.m210projects.Build.Engine.totalclock;
import static ru.m210projects.Build.Engine.wall;
import static ru.m210projects.Build.Engine.zr_florhit;
import static ru.m210projects.Build.Gameutils.isValidSector;
import static ru.m210projects.Witchaven.AI.Ai.GUARDIANTYPE;
import static ru.m210projects.Witchaven.AI.Ai.aifly;
import static ru.m210projects.Witchaven.AI.Ai.aisearch;
import static ru.m210projects.Witchaven.AI.Ai.castspell;
import static ru.m210projects.Witchaven.AI.Ai.checkdist;
import static ru.m210projects.Witchaven.AI.Ai.checkmove;
import static ru.m210projects.Witchaven.AI.Ai.checksector6;
import static ru.m210projects.Witchaven.AI.Ai.checksight;
import static ru.m210projects.Witchaven.AI.Ai.enemy;
import static ru.m210projects.Witchaven.AI.Ai.processfluid;
import static ru.m210projects.Witchaven.Globals.ATTACK;
import static ru.m210projects.Witchaven.Globals.CAST;
import static ru.m210projects.Witchaven.Globals.CHASE;
import static ru.m210projects.Witchaven.Globals.DIE;
import static ru.m210projects.Witchaven.Globals.FACE;
import static ru.m210projects.Witchaven.Globals.FINDME;
import static ru.m210projects.Witchaven.Globals.FLEE;
import static ru.m210projects.Witchaven.Globals.KILLSECTOR;
import static ru.m210projects.Witchaven.Globals.TICSPERFRAME;
import static ru.m210projects.Witchaven.Globals.kHitIndexMask;
import static ru.m210projects.Witchaven.Globals.kHitSector;
import static ru.m210projects.Witchaven.Globals.kHitTypeMask;
import static ru.m210projects.Witchaven.Globals.kHitWall;
import static ru.m210projects.Witchaven.Main.engine;
import static ru.m210projects.Witchaven.Main.game;
import static ru.m210projects.Witchaven.Names.ANILAVA;
import static ru.m210projects.Witchaven.Names.GUARDIAN;
import static ru.m210projects.Witchaven.Names.GUARDIANATTACK;
import static ru.m210projects.Witchaven.Names.LAVA;
import static ru.m210projects.Witchaven.Names.LAVA1;
import static ru.m210projects.Witchaven.WH1Names.GUARDIANCHAR;
import static ru.m210projects.Witchaven.WHFX.warpsprite;
import static ru.m210projects.Witchaven.WHOBJ.newstatus;
import static ru.m210projects.Witchaven.WHOBJ.trailingsmoke;
import static ru.m210projects.Witchaven.WHPLR.chunksofmeat;
import static ru.m210projects.Witchaven.WHSND.S_FIREBALL;
import static ru.m210projects.Witchaven.WHSND.playsound_loc;

import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Witchaven.Types.PLAYER;

public class AIGuardian {

	public static void create() {
		enemy[GUARDIANTYPE] = new Enemy();
		enemy[GUARDIANTYPE].info = new EnemyInfo(game.WH2 ? 35 : 32, game.WH2 ? 35 : 32, 4096, 120, 0, 64, true, game.WH2 ? 100 : 200, 0);
		enemy[GUARDIANTYPE].chase = new AIState() {
			@Override
			public void process(PLAYER plr, short i) {
				SPRITE spr = sprite[i];
				spr.lotag -= TICSPERFRAME;
				if (spr.lotag < 0)
					spr.lotag = 250;

				short osectnum = spr.sectnum;
				if (plr.z < spr.z)
					spr.z -= TICSPERFRAME << 8;
				if (plr.z > spr.z)
					spr.z += TICSPERFRAME << 8;

				if (engine.krand() % 63 == 0) {
					if (engine.cansee(plr.x, plr.y, plr.z, plr.sector, sprite[i].x, sprite[i].y,
							sprite[i].z - (engine.getTile(sprite[i].picnum).getHeight() << 7), sprite[i].sectnum) && plr.invisibletime < 0)
						newstatus(i, ATTACK);
					return;
				} else {
					int dax = (sintable[(sprite[i].ang + 512) & 2047] * TICSPERFRAME) << 3;
					int day = (sintable[sprite[i].ang & 2047] * TICSPERFRAME) << 3;
					checksight(plr, i);

					if(totalclock%100 > 70)
						trailingsmoke(i, true);

					if (!checkdist(plr, i)) {
						checkmove(i, dax, day);
					} else {
						if (engine.krand() % 8 == 0) // NEW
							newstatus(i, ATTACK); // NEW
						else { // NEW
							sprite[i].ang = (short) (((engine.krand() & 512 - 256) + sprite[i].ang + 1024) & 2047); // NEW
							newstatus(i, CHASE); // NEW
						}
					}
				}

				engine.getzrange(spr.x, spr.y, spr.z - 1, spr.sectnum, (spr.clipdist) << 2, CLIPMASK0);
				if ((spr.sectnum != osectnum) && (sector[spr.sectnum].lotag == 10))
					warpsprite(i);

				if (checksector6(i))
					return;

				processfluid(i, zr_florhit, true);

				if (sector[osectnum].lotag == KILLSECTOR) {
					spr.hitag--;
					if (spr.hitag < 0)
						newstatus(i, DIE);
				}

				engine.setsprite(i, spr.x, spr.y, spr.z);

				if(!isValidSector(spr.sectnum))
					return;

				if ((zr_florhit & kHitTypeMask) == kHitSector && (sector[spr.sectnum].floorpicnum == LAVA
						|| sector[spr.sectnum].floorpicnum == LAVA1 || sector[spr.sectnum].floorpicnum == ANILAVA)) {
					spr.hitag--;
					if (spr.hitag < 0)
						newstatus(i, DIE);
				}
			}
		};

		enemy[GUARDIANTYPE].nuked = new AIState() {
			@Override
			public void process(PLAYER plr, short i) {
				SPRITE spr = sprite[i];

				if(game.WH2) {
					chunksofmeat(plr, i, spr.x, spr.y, spr.z, spr.sectnum, spr.ang);
					trailingsmoke(i, false);
					newstatus(i, DIE);
					return;
				}

				spr.lotag -= TICSPERFRAME;
				if (spr.lotag < 0) {
					spr.picnum++;
					spr.lotag = 24;
					if (spr.picnum == GUARDIANCHAR + 4) {
						trailingsmoke(i, false);
						engine.deletesprite(i);
					}
				}
			}
		};

		enemy[GUARDIANTYPE].attack = new AIState() {
			@Override
			public void process(PLAYER plr, short i) {
				SPRITE spr = sprite[i];

				if (plr.z < spr.z) {
					spr.z -= TICSPERFRAME << 8;
				}
				if (plr.z > spr.z) {
					spr.z += TICSPERFRAME << 8;
				}

				spr.lotag -= TICSPERFRAME;
				if (spr.lotag < 0) {
					if (engine.cansee(plr.x, plr.y, plr.z, plr.sector, spr.x, spr.y, spr.z - (engine.getTile(spr.picnum).getHeight() << 7),
							spr.sectnum))
						newstatus(i, CAST);
					else
						newstatus(i, CHASE);
				} else
					spr.ang = engine.getangle(plr.x - spr.x, plr.y - spr.y);
			}
		};

		enemy[GUARDIANTYPE].face = new AIState() {
			@Override
			public void process(PLAYER plr, short i) {
				SPRITE spr = sprite[i];


				boolean cansee = engine.cansee(plr.x, plr.y, plr.z, plr.sector, spr.x, spr.y, spr.z - (engine.getTile(spr.picnum).getHeight() << 7),
						spr.sectnum);

				if (cansee && plr.invisibletime < 0) {
					spr.ang = (short) (engine.getangle(plr.x - spr.x, plr.y - spr.y) & 2047);

					if (plr.shadowtime > 0) {
						spr.ang = (short) (((engine.krand() & 512 - 256) + spr.ang + 1024) & 2047);
						newstatus(i, FLEE);
					} else {
						spr.owner = plr.spritenum;
						newstatus(i, CHASE);
					}
				} else { // get off the wall
					if (spr.owner == plr.spritenum) {
						spr.ang = (short) (((engine.krand() & 512 - 256) + spr.ang) & 2047);
						newstatus(i, FINDME);
					} else if(cansee) newstatus(i, FLEE);
				}

				if (checkdist(plr, i))
					newstatus(i, ATTACK);
			}
		};

		enemy[GUARDIANTYPE].search = new AIState() {
			@Override
			public void process(PLAYER plr, short i) {
				aisearch(plr, i, true);
				checksector6(i);
			}
		};

		enemy[GUARDIANTYPE].flee = new AIState() {
			@Override
			public void process(PLAYER plr, short i) {
				SPRITE spr = sprite[i];
				spr.lotag -= TICSPERFRAME;
				short osectnum = spr.sectnum;

				if (totalclock % 100 > 70)
					trailingsmoke(i, true);

				int movestat = aifly(i);

				if (movestat != 0) {
					if ((movestat & kHitTypeMask)== kHitWall) {
						int nWall = movestat & kHitIndexMask;
						int nx = -(wall[wall[nWall].point2].y - wall[nWall].y) >> 4;
						int ny = (wall[wall[nWall].point2].x - wall[nWall].x) >> 4;
						spr.ang = engine.getangle( nx, ny );
					} else {
						spr.ang = engine.getangle(plr.x - spr.x, plr.y - spr.y);
						newstatus(i, FACE);
					}
				}
				if (spr.lotag < 0)
					newstatus(i, FACE);

				if ((spr.sectnum != osectnum) && (sector[spr.sectnum].lotag == 10))
					warpsprite(i);

				if (checksector6(i))
					return;

				processfluid(i, zr_florhit, true);

				engine.setsprite(i, spr.x, spr.y, spr.z);
			}
		};

		enemy[GUARDIANTYPE].pain = new AIState() {
			@Override
			public void process(PLAYER plr, short i) {
				SPRITE spr = sprite[i];
				spr.lotag -= TICSPERFRAME;
				if (spr.lotag < 0) {
					spr.picnum = GUARDIAN;
					spr.ang = (short) plr.ang;
					newstatus(i, FLEE);
				}

			}
		};

		enemy[GUARDIANTYPE].cast = new AIState() {
			@Override
			public void process(PLAYER plr, short i) {
				SPRITE spr = sprite[i];

				spr.lotag -= TICSPERFRAME;

				if (plr.z < spr.z) {
					spr.z -= TICSPERFRAME << 8;
				}
				if (plr.z > spr.z) {
					spr.z += TICSPERFRAME << 8;
				}

				if (spr.lotag < 0) {
					spr.picnum++;
					spr.lotag = 12;
				}

				if (spr.picnum == GUARDIANATTACK + 6) {
					spr.picnum = GUARDIAN;
					playsound_loc(S_FIREBALL, sprite[i].x, sprite[i].y);
					castspell(plr, i);
					newstatus(i, CHASE);
				}
				checksector6(i);
			}
		};
	}

	public static void premap(short i) {
		SPRITE spr = sprite[i];

		spr.detail = GUARDIANTYPE;
		enemy[GUARDIANTYPE].info.set(spr);
		engine.changespritestat(i, FACE);
	}
}
