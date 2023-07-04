package ru.m210projects.Witchaven.AI;

import static ru.m210projects.Build.Engine.CLIPMASK0;
import static ru.m210projects.Build.Engine.headspritesect;
import static ru.m210projects.Build.Engine.nextspritesect;
import static ru.m210projects.Build.Engine.sector;
import static ru.m210projects.Build.Engine.sintable;
import static ru.m210projects.Build.Engine.sprite;
import static ru.m210projects.Build.Engine.wall;
import static ru.m210projects.Build.Engine.zr_florhit;
import static ru.m210projects.Build.Engine.zr_florz;
import static ru.m210projects.Build.Pragmas.klabs;
import static ru.m210projects.Witchaven.AI.Ai.SKELETONTYPE;
import static ru.m210projects.Witchaven.AI.Ai.TYPELAVA;
import static ru.m210projects.Witchaven.AI.Ai.TYPEWATER;
import static ru.m210projects.Witchaven.AI.Ai.aimove;
import static ru.m210projects.Witchaven.AI.Ai.aisearch;
import static ru.m210projects.Witchaven.AI.Ai.attack;
import static ru.m210projects.Witchaven.AI.Ai.checkdist;
import static ru.m210projects.Witchaven.AI.Ai.checkfluid;
import static ru.m210projects.Witchaven.AI.Ai.checksector6;
import static ru.m210projects.Witchaven.AI.Ai.checksight;
import static ru.m210projects.Witchaven.AI.Ai.checksight_ang;
import static ru.m210projects.Witchaven.AI.Ai.enemy;
import static ru.m210projects.Witchaven.AI.Ai.processfluid;
import static ru.m210projects.Witchaven.Globals.ATTACK;
import static ru.m210projects.Witchaven.Globals.CHASE;
import static ru.m210projects.Witchaven.Globals.CHILL;
import static ru.m210projects.Witchaven.Globals.DEAD;
import static ru.m210projects.Witchaven.Globals.DIE;
import static ru.m210projects.Witchaven.Globals.FACE;
import static ru.m210projects.Witchaven.Globals.FINDME;
import static ru.m210projects.Witchaven.Globals.FLEE;
import static ru.m210projects.Witchaven.Globals.KILLSECTOR;
import static ru.m210projects.Witchaven.Globals.PICKDISTANCE;
import static ru.m210projects.Witchaven.Globals.RESURECT;
import static ru.m210projects.Witchaven.Globals.SKIRMISH;
import static ru.m210projects.Witchaven.Globals.STAND;
import static ru.m210projects.Witchaven.Globals.TICSPERFRAME;
import static ru.m210projects.Witchaven.Globals.difficulty;
import static ru.m210projects.Witchaven.Globals.kHitFloor;
import static ru.m210projects.Witchaven.Globals.kHitIndexMask;
import static ru.m210projects.Witchaven.Globals.kHitSector;
import static ru.m210projects.Witchaven.Globals.kHitSprite;
import static ru.m210projects.Witchaven.Globals.kHitTypeMask;
import static ru.m210projects.Witchaven.Globals.kHitWall;
import static ru.m210projects.Witchaven.Globals.kills;
import static ru.m210projects.Witchaven.Main.engine;
import static ru.m210projects.Witchaven.Main.game;
import static ru.m210projects.Witchaven.Names.ANILAVA;
import static ru.m210projects.Witchaven.Names.EXPLO2;
import static ru.m210projects.Witchaven.Names.HANGMAN;
import static ru.m210projects.Witchaven.Names.LAVA;
import static ru.m210projects.Witchaven.Names.LAVA1;
import static ru.m210projects.Witchaven.Names.MONSTERBALL;
import static ru.m210projects.Witchaven.Names.SKELETON;
import static ru.m210projects.Witchaven.Names.SKELETONDEAD;
import static ru.m210projects.Witchaven.Names.SMOKEFX;
import static ru.m210projects.Witchaven.WH1Names.SKELETONCHAR;
import static ru.m210projects.Witchaven.WHFX.warpsprite;
import static ru.m210projects.Witchaven.WHOBJ.adjusthp;
import static ru.m210projects.Witchaven.WHOBJ.getPickHeight;
import static ru.m210projects.Witchaven.WHOBJ.newstatus;
import static ru.m210projects.Witchaven.WHOBJ.trailingsmoke;
import static ru.m210projects.Witchaven.WHPLR.chunksofmeat;
import static ru.m210projects.Witchaven.WHSND.S_SKELSEE;
import static ru.m210projects.Witchaven.WHSND.playsound_loc;

import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Witchaven.Types.PLAYER;

public class AISkeleton {

	public static void create() {
		enemy[SKELETONTYPE] = new Enemy();
		enemy[SKELETONTYPE].info = new EnemyInfo(game.WH2 ? 35 : 24, game.WH2 ? 35 : 24, 1024, 120, 0, 64, false, game.WH2 ? 25 : 30, 0);
		enemy[SKELETONTYPE].chase = new AIState() {
			@Override
			public void process(PLAYER plr, short i) {
				SPRITE spr = sprite[i];
				spr.lotag -= TICSPERFRAME;
				if (spr.lotag < 0)
					spr.lotag = 250;

				short osectnum = spr.sectnum;
				if (engine.cansee(plr.x, plr.y, plr.z, plr.sector, spr.x, spr.y, spr.z - (engine.getTile(spr.picnum).getHeight() << 7),
						spr.sectnum) && plr.invisibletime < 0) {
					if (checkdist(plr, i)) {
						if (plr.shadowtime > 0) {
							spr.ang = (short) (((engine.krand() & 512 - 256) + spr.ang + 1024) & 2047);
							newstatus(i, FLEE);
						} else
							newstatus(i, ATTACK);
					} else if (engine.krand() % 63 > 60) {
						spr.ang = (short) (((engine.krand() & 128 - 256) + spr.ang + 1024) & 2047);
						newstatus(i, FLEE);
					} else {
						int movestat = aimove(i);
						if((movestat & kHitTypeMask) == kHitFloor)
						{
							spr.ang = (short) ((spr.ang + 1024) & 2047);
							newstatus(i, FLEE);
							return;
						}

						if ((movestat & kHitTypeMask) == kHitSprite) {
							if ((movestat & kHitIndexMask) != plr.spritenum) {
								short daang = (short) ((spr.ang - 256) & 2047);
								spr.ang = daang;
								if (plr.shadowtime > 0) {
									spr.ang = (short) (((engine.krand() & 512 - 256) + spr.ang + 1024) & 2047);
									newstatus(i, FLEE);
								} else
									newstatus(i, SKIRMISH);
							} else {
								spr.ang = (short) (((engine.krand() & 512 - 256) + spr.ang + 1024) & 2047);
								newstatus(i, SKIRMISH);
							}
						}
					}
				} else {
					spr.ang = (short) (((engine.krand() & 512 - 256) + spr.ang + 1024) & 2047);
					newstatus(i, FLEE);
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

				if ((zr_florhit & kHitTypeMask) == kHitSector && (sector[spr.sectnum].floorpicnum == LAVA
						|| sector[spr.sectnum].floorpicnum == LAVA1 || sector[spr.sectnum].floorpicnum == ANILAVA)) {
					spr.hitag--;
					if (spr.hitag < 0)
						newstatus(i, DIE);
				}

				checkexpl(plr, i);
			}
		};

		enemy[SKELETONTYPE].die = new AIState() {
			@Override
			public void process(PLAYER plr, short i) {
				SPRITE spr = sprite[i];
				spr.lotag -= TICSPERFRAME;

				if (spr.lotag <= 0) {
					spr.picnum++;
					spr.lotag = 20;

					if (spr.picnum == SKELETONDEAD) {
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

		enemy[SKELETONTYPE].face = new AIState() {
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

				checkexpl(plr, i);
			}
		};

		enemy[SKELETONTYPE].flee = new AIState() {
			@Override
			public void process(PLAYER plr, short i) {
				SPRITE spr = sprite[i];
				spr.picnum = SKELETON;

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

				if (spr.lotag < 0)
					newstatus(i, FACE);

				if ((spr.sectnum != osectnum) && (sector[spr.sectnum].lotag == 10))
					warpsprite(i);

				if (checksector6(i))
					return;

				processfluid(i, zr_florhit, false);

				engine.setsprite(i, spr.x, spr.y, spr.z);

				checkexpl(plr, i);
			}
		};

		enemy[SKELETONTYPE].stand = new AIState() {
			@Override
			public void process(PLAYER plr, short i) {
				SPRITE spr = sprite[i];

				spr.ang = engine.getangle(plr.x - spr.x, plr.y - spr.y);
				if (sintable[(spr.ang + 2560) & 2047] * (plr.x - spr.x)
						+ sintable[(spr.ang + 2048) & 2047] * (plr.y - spr.y) >= 0)
					if (engine.cansee(plr.x, plr.y, plr.z, plr.sector, spr.x, spr.y, spr.z - (engine.getTile(spr.picnum).getHeight() << 7),
							spr.sectnum) && plr.invisibletime < 0) {

						if(spr.picnum == HANGMAN) {
							newstatus(i, CHILL);
							playsound_loc(S_SKELSEE, spr.x, spr.y);
						} else {
							if (plr.shadowtime > 0) {
								spr.ang = (short) (((engine.krand() & 512 - 256) + spr.ang + 1024) & 2047); // NEW
								newstatus(i, FLEE);
							} else
								newstatus(i, CHASE);
						}
					}

				checksector6(i);
			}
		};

		enemy[SKELETONTYPE].attack = new AIState() {
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

				spr.ang = engine.getangle(plr.x - spr.x, plr.y - spr.y);
				if (spr.lotag == 16) {
					if (checksight(plr, i))
						if (checkdist(plr, i)) {
							spr.ang = (short) checksight_ang;
							attack(plr, i);
						}
				} else if (spr.lotag < 0) {
					if (plr.shadowtime > 0) {
						spr.ang = (short) (((engine.krand() & 512 - 256) + spr.ang + 1024) & 2047); // NEW
						newstatus(i, FLEE);
					} else
						newstatus(i, CHASE);
				}
				spr.lotag -= TICSPERFRAME;

				checksector6(i);
			}
		};

		enemy[SKELETONTYPE].resurect = new AIState() {
			@Override
			public void process(PLAYER plr, short i) {
				SPRITE spr = sprite[i];

				spr.lotag -= TICSPERFRAME;
				if (spr.lotag < 0) {
					newstatus(i, FACE);
					spr.picnum = SKELETON;
					spr.hitag = adjusthp(10);
					spr.lotag = 100;
					spr.cstat |= 1;
				}
			}
		};

		enemy[SKELETONTYPE].search = new AIState() {
			@Override
			public void process(PLAYER plr, short i) {
				aisearch(plr, i, false);
				if (!checksector6(i))
					checkexpl(plr, i);
			}
		};

		enemy[SKELETONTYPE].frozen = new AIState() {
			@Override
			public void process(PLAYER plr, short i) {
				SPRITE spr = sprite[i];

				spr.lotag -= TICSPERFRAME;
				if (spr.lotag < 0) {
					spr.pal = 0;
					spr.picnum = SKELETON;
					newstatus(i, FACE);
				}
			}
		};

		enemy[SKELETONTYPE].nuked = new AIState() {
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
					if (spr.picnum == SKELETONCHAR + 4) {
						trailingsmoke(i, false);
						engine.deletesprite(i);
					}
				}
			}
		};

		enemy[SKELETONTYPE].skirmish = new AIState() {
			@Override
			public void process(PLAYER plr, short i) {
				SPRITE spr = sprite[i];

				spr.lotag -= TICSPERFRAME;

				if (spr.lotag < 0)
					newstatus(i, FACE);
				short osectnum = spr.sectnum;
				int movestat = aimove(i);
				if ((movestat & kHitTypeMask) != kHitFloor && movestat != 0) {
					spr.ang = engine.getangle(plr.x - spr.x, plr.y - spr.y);
					newstatus(i, FACE);
				}
				if ((spr.sectnum != osectnum) && (sector[spr.sectnum].lotag == 10))
					warpsprite(i);

				processfluid(i, zr_florhit, false);

				engine.setsprite(i, spr.x, spr.y, spr.z);

				if (checksector6(i))
					return;

				checkexpl(plr, i);
			}
		};
	}

	public static final AIState skeletonChill = new AIState() {
		@Override
		public void process(PLAYER plr, short i) {
			SPRITE spr = sprite[i];

			spr.lotag -= TICSPERFRAME;
			if (spr.lotag < 0) {
				spr.picnum++;
				spr.lotag = 18;
				if (spr.picnum == HANGMAN + 10) {
					spr.picnum = SKELETON;
					newstatus(i, FACE);
				}
			}
		}
	};

	public static void checkexpl(PLAYER plr, short i) {
		SPRITE spr = sprite[i];
		short j = headspritesect[spr.sectnum];
		while (j != -1) {
			short nextj = nextspritesect[j];
			long dx = klabs(spr.x - sprite[j].x); // x distance to sprite
			long dy = klabs(spr.y - sprite[j].y); // y distance to sprite
			long dz = klabs((spr.z >> 8) - (sprite[j].z >> 8)); // z distance to sprite
			long dh = engine.getTile(sprite[j].picnum).getHeight() >> 1; // height of sprite
			if (dx + dy < PICKDISTANCE && dz - dh <= getPickHeight()) {
				if(sprite[j].picnum == EXPLO2
						|| sprite[j].picnum == SMOKEFX
						|| sprite[j].picnum == MONSTERBALL) {
					spr.hitag -= TICSPERFRAME << 2;
					if (spr.hitag < 0) {
						newstatus(i, DIE);
					}
				}
			}
			j = nextj;
		}
	}

	public static void premap(short i) {
		SPRITE spr = sprite[i];

		spr.detail = SKELETONTYPE;
		enemy[SKELETONTYPE].info.set(spr);

		if (spr.picnum == HANGMAN) {
			spr.xrepeat = 28;
			spr.yrepeat = 28;
			engine.changespritestat(i, STAND);
			return;
		}

		engine.changespritestat(i, FACE);
	}
}
