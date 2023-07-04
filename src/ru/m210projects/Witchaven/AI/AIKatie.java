package ru.m210projects.Witchaven.AI;

import static ru.m210projects.Build.Engine.CLIPMASK0;
import static ru.m210projects.Build.Engine.MAXSPRITES;
import static ru.m210projects.Build.Engine.sector;
import static ru.m210projects.Build.Engine.sprite;
import static ru.m210projects.Build.Engine.wall;
import static ru.m210projects.Build.Engine.zr_florhit;
import static ru.m210projects.Build.Engine.zr_florz;
import static ru.m210projects.Witchaven.AI.AIJudy.spawnabaddy;
import static ru.m210projects.Witchaven.AI.Ai.KATIETYPE;
import static ru.m210projects.Witchaven.AI.Ai.TYPELAVA;
import static ru.m210projects.Witchaven.AI.Ai.TYPEWATER;
import static ru.m210projects.Witchaven.AI.Ai.aimove;
import static ru.m210projects.Witchaven.AI.Ai.aisearch;
import static ru.m210projects.Witchaven.AI.Ai.castspell;
import static ru.m210projects.Witchaven.AI.Ai.checkdist;
import static ru.m210projects.Witchaven.AI.Ai.checkfluid;
import static ru.m210projects.Witchaven.AI.Ai.checksector6;
import static ru.m210projects.Witchaven.AI.Ai.checksight;
import static ru.m210projects.Witchaven.AI.Ai.enemy;
import static ru.m210projects.Witchaven.AI.Ai.processfluid;
import static ru.m210projects.Witchaven.Globals.ATTACK;
import static ru.m210projects.Witchaven.Globals.CAST;
import static ru.m210projects.Witchaven.Globals.CHASE;
import static ru.m210projects.Witchaven.Globals.DEAD;
import static ru.m210projects.Witchaven.Globals.DIE;
import static ru.m210projects.Witchaven.Globals.FACE;
import static ru.m210projects.Witchaven.Globals.FINDME;
import static ru.m210projects.Witchaven.Globals.FLEE;
import static ru.m210projects.Witchaven.Globals.KILLSECTOR;
import static ru.m210projects.Witchaven.Globals.RESURECT;
import static ru.m210projects.Witchaven.Globals.TICSPERFRAME;
import static ru.m210projects.Witchaven.Globals.difficulty;
import static ru.m210projects.Witchaven.Globals.kHitFloor;
import static ru.m210projects.Witchaven.Globals.kHitIndexMask;
import static ru.m210projects.Witchaven.Globals.kHitTypeMask;
import static ru.m210projects.Witchaven.Globals.kHitWall;
import static ru.m210projects.Witchaven.Globals.kills;
import static ru.m210projects.Witchaven.Main.engine;
import static ru.m210projects.Witchaven.Names.GRONSW;
import static ru.m210projects.Witchaven.WH2Names.GONZOGSH;
import static ru.m210projects.Witchaven.WH2Names.KATIE;
import static ru.m210projects.Witchaven.WH2Names.KATIEAT;
import static ru.m210projects.Witchaven.WH2Names.KATIEDEAD;
import static ru.m210projects.Witchaven.WHFX.warpsprite;
import static ru.m210projects.Witchaven.WHOBJ.adjusthp;
import static ru.m210projects.Witchaven.WHOBJ.newstatus;
import static ru.m210projects.Witchaven.WHSND.S_FIREBALL;
import static ru.m210projects.Witchaven.WHSND.playsound_loc;

import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Witchaven.Types.PLAYER;

public class AIKatie {

	public static void create() {
		enemy[KATIETYPE] = new Enemy();
		enemy[KATIETYPE].info = new EnemyInfo(35, 35, 2048, 120, 0, 64, false, 200, 0);
		enemy[KATIETYPE].chase = new AIState() {
			@Override
			public void process(PLAYER plr, short i) {
				SPRITE spr = sprite[i];
				spr.lotag -= TICSPERFRAME;
				if (spr.lotag < 0)
					spr.lotag = 250;

				short osectnum = spr.sectnum;

				if (engine.krand() % 63 == 0) {
					if (engine.cansee(plr.x, plr.y, plr.z, plr.sector, sprite[i].x, sprite[i].y,
							sprite[i].z - (engine.getTile(sprite[i].picnum).getHeight() << 7), sprite[i].sectnum))// && invisibletime < 0)
						newstatus(i, ATTACK);
				} else {
					checksight(plr, i);
					if (!checkdist(i, plr.x, plr.y, plr.z)) {
						int movestat = aimove(i);
						if((movestat & kHitTypeMask) == kHitFloor)
						{
							spr.ang = (short) ((spr.ang + 1024) & 2047);
							newstatus(i, FLEE);
							return;
						}
					} else {
						if (engine.krand() % 8 == 0) // NEW
							newstatus(i, ATTACK); // NEW
						else { // NEW
							sprite[i].ang = (short) (((engine.krand() & 512 - 256) + sprite[i].ang + 1024) & 2047); // NEW
							newstatus(i, FLEE); // NEW
						}
					}
				}

				engine.getzrange(spr.x, spr.y, spr.z - 1, spr.sectnum, (spr.clipdist) << 2, CLIPMASK0);
				spr.z = zr_florz;

				if ((spr.sectnum != osectnum) && (sector[spr.sectnum].lotag == 10))
					warpsprite(i);

				if (checksector6(i))
					return;

				processfluid(i, zr_florhit, false);

				if (sector[osectnum].lotag == KILLSECTOR) {
					spr.hitag--;
					if (spr.hitag < 0)
						newstatus(i, DIE);
				}

				engine.setsprite(i, spr.x, spr.y, spr.z);
			}
		};

		enemy[KATIETYPE].resurect = new AIState() {
			@Override
			public void process(PLAYER plr, short i) {
				SPRITE spr = sprite[i];

				spr.lotag -= TICSPERFRAME;
				if (spr.lotag < 0) {
					newstatus(i, FACE);
					spr.picnum = KATIE;
					spr.hitag = adjusthp(200);
					spr.lotag = 100;
					spr.cstat |= 1;
				}
			}
		};

		enemy[KATIETYPE].search = new AIState() {
			@Override
			public void process(PLAYER plr, short i) {
				aisearch(plr, i, false);
				checksector6(i);
			}
		};

		enemy[KATIETYPE].pain = new AIState() {
			@Override
			public void process(PLAYER plr, short i) {
				SPRITE spr = sprite[i];
				spr.lotag -= TICSPERFRAME;
				if (spr.lotag < 0) {
					spr.picnum = KATIE;
					spr.ang = (short) plr.ang;
					newstatus(i, FLEE);
				}

				aimove(i);
				processfluid(i, zr_florhit, false);
				engine.setsprite(i, spr.x, spr.y, spr.z);
			}
		};

		enemy[KATIETYPE].face = new AIState() {
			@Override
			public void process(PLAYER plr, short i) {
				SPRITE spr = sprite[i];

				spr.ang = (short) (engine.getangle(plr.x - spr.x, plr.y - spr.y) & 2047);

				boolean cansee = engine.cansee(plr.x, plr.y, plr.z, plr.sector, spr.x, spr.y, spr.z - (engine.getTile(spr.picnum).getHeight() << 7),
						spr.sectnum);

				if (cansee && plr.invisibletime < 0) {
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

				if (checkdist(i, plr.x, plr.y, plr.z))
					newstatus(i, ATTACK);
			}
		};

		enemy[KATIETYPE].attack = new AIState() {
			@Override
			public void process(PLAYER plr, short i) {
				SPRITE spr = sprite[i];

				engine.getzrange(spr.x, spr.y, spr.z - 1, spr.sectnum, (spr.clipdist) << 2, CLIPMASK0);
				spr.z = zr_florz;

				switch (checkfluid(i, zr_florhit)) {
				case TYPELAVA:
				case TYPEWATER:
					spr.z += engine.getTile(spr.picnum).getHeight() << 5;
					break;
				}

				engine.setsprite(i, spr.x, spr.y, spr.z);

				sprite[i].lotag -= TICSPERFRAME;
				if (sprite[i].lotag < 0) {
					if (engine.cansee(plr.x, plr.y, plr.z, plr.sector, sprite[i].x, sprite[i].y,
							sprite[i].z - (engine.getTile(sprite[i].picnum).getHeight() << 7), sprite[i].sectnum))
						newstatus(i, CAST);
					else
						newstatus(i, CHASE);
				} else
					sprite[i].ang = engine.getangle(plr.x - sprite[i].x, plr.y - sprite[i].y);
			}
		};

		enemy[KATIETYPE].flee = new AIState() {
			@Override
			public void process(PLAYER plr, short i) {
				SPRITE spr = sprite[i];
				spr.lotag -= TICSPERFRAME;
				short osectnum = spr.sectnum;

				int movestat = aimove(i);
				if ((movestat & kHitTypeMask) != kHitFloor && movestat != 0) {
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

				if (movestat != 0) {
					spr.ang = engine.getangle(plr.x - spr.x, plr.y - spr.y);
					newstatus(i, FACE);
				}
				if (spr.lotag < 0)
					newstatus(i, FACE);

				if ((spr.sectnum != osectnum) && (sector[spr.sectnum].lotag == 10))
					warpsprite(i);

				if (checksector6(i))
					return;

				processfluid(i, zr_florhit, false);

				engine.setsprite(i, spr.x, spr.y, spr.z);
			}
		};

		enemy[KATIETYPE].cast = new AIState() {
			@Override
			public void process(PLAYER plr, short i) {
				SPRITE spr = sprite[i];

				spr.lotag -= TICSPERFRAME;
				if (spr.lotag < 0) {
					spr.picnum++;
					spr.lotag = 12;
				}

				if (spr.picnum == KATIEAT + 2) {
					if (spr.extra >= 2) {
						if (engine.rand() % 100 > 50) {
							for (int j = 0; j < 4; j++) {
								spawnabaddy(i, GONZOGSH);
							}
						} else {
							for (int j = 0; j < 6; j++) {
								spawnabaddy(i, GRONSW);
							}
						}
						spr.picnum = KATIE;
						spr.extra--;
						playsound_loc(S_FIREBALL, spr.x, spr.y);
						newstatus(i, CHASE);
					}
				}

				if (spr.picnum == KATIEAT + 6) {
					if (spr.extra == 1) {
						for (short j = 0; j < MAXSPRITES; j++) {
							if (sprite[j].pal == 8) {
								sprite[j].picnum--;
								sprite[j].pal = 0;
								sprite[j].shade = 0;
								engine.changespritestat(j, FACE);
							}
						}
						spr.picnum = KATIE;
						playsound_loc(S_FIREBALL, spr.x, spr.y);
						newstatus(i, CHASE);
						spr.extra--;
					}
				}

				if (spr.picnum == KATIEAT + 16) {
					spr.picnum = KATIE;
					playsound_loc(S_FIREBALL, spr.x, spr.y);
					castspell(plr, i);
					newstatus(i, CHASE);
					spr.extra++;
				}
				checksector6(i);
			}
		};

		enemy[KATIETYPE].die = new AIState() {
			@Override
			public void process(PLAYER plr, short i) {
				SPRITE spr = sprite[i];
				spr.lotag -= TICSPERFRAME;

				if (spr.lotag <= 0) {
					spr.picnum++;
					spr.lotag = 20;

					if (spr.picnum == KATIEDEAD) {
						if (difficulty == 4)
							newstatus(i, RESURECT);
						else {
							kills++;
							newstatus(i, DEAD);
						}
					}
				}
			}
		};
	}

	public static void premap(short i) {
		SPRITE spr = sprite[i];

		spr.detail = KATIETYPE;
		engine.changespritestat(i, FACE);
		enemy[KATIETYPE].info.set(spr);
		spr.extra = 5;
	}
}
