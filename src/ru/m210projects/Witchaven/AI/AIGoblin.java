package ru.m210projects.Witchaven.AI;

import static ru.m210projects.Build.Engine.CLIPMASK0;
import static ru.m210projects.Build.Engine.MAXSPRITES;
import static ru.m210projects.Build.Engine.headspritesect;
import static ru.m210projects.Build.Engine.headspritestat;
import static ru.m210projects.Build.Engine.nextspritesect;
import static ru.m210projects.Build.Engine.nextspritestat;
import static ru.m210projects.Build.Engine.sector;
import static ru.m210projects.Build.Engine.sintable;
import static ru.m210projects.Build.Engine.sprite;
import static ru.m210projects.Build.Engine.wall;
import static ru.m210projects.Build.Engine.zr_florhit;
import static ru.m210projects.Build.Engine.zr_florz;
import static ru.m210projects.Build.Pragmas.klabs;
import static ru.m210projects.Witchaven.AI.Ai.GOBLINTYPE;
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
import static ru.m210projects.Witchaven.Globals.WAR;
import static ru.m210projects.Witchaven.Globals.difficulty;
import static ru.m210projects.Witchaven.Globals.kHitFloor;
import static ru.m210projects.Witchaven.Globals.kHitIndexMask;
import static ru.m210projects.Witchaven.Globals.kHitSector;
import static ru.m210projects.Witchaven.Globals.kHitSprite;
import static ru.m210projects.Witchaven.Globals.kHitTypeMask;
import static ru.m210projects.Witchaven.Globals.kHitWall;
import static ru.m210projects.Witchaven.Globals.kills;
import static ru.m210projects.Witchaven.Main.engine;
import static ru.m210projects.Witchaven.Names.ANILAVA;
import static ru.m210projects.Witchaven.Names.EXPLO2;
import static ru.m210projects.Witchaven.Names.LAVA;
import static ru.m210projects.Witchaven.Names.LAVA1;
import static ru.m210projects.Witchaven.Names.MONSTERBALL;
import static ru.m210projects.Witchaven.Names.SMOKEFX;
import static ru.m210projects.Witchaven.WH1Names.GOBLIN;
import static ru.m210projects.Witchaven.WH1Names.GOBLINCHAR;
import static ru.m210projects.Witchaven.WH1Names.GOBLINCHILL;
import static ru.m210projects.Witchaven.WH1Names.GOBLINDEAD;
import static ru.m210projects.Witchaven.WH1Names.GOBLINDIE;
import static ru.m210projects.Witchaven.WH1Names.GOBLINSURPRISE;
import static ru.m210projects.Witchaven.WHFX.warpsprite;
import static ru.m210projects.Witchaven.WHOBJ.adjusthp;
import static ru.m210projects.Witchaven.WHOBJ.getPickHeight;
import static ru.m210projects.Witchaven.WHOBJ.newstatus;
import static ru.m210projects.Witchaven.WHOBJ.trailingsmoke;
import static ru.m210projects.Witchaven.WHPLR.addhealth;
import static ru.m210projects.Witchaven.WHSND.S_GENSWING;
import static ru.m210projects.Witchaven.WHSND.S_GOBPAIN1;
import static ru.m210projects.Witchaven.WHSND.S_SWORD1;
import static ru.m210projects.Witchaven.WHSND.playsound_loc;

import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Witchaven.Types.PLAYER;

public class AIGoblin {

	public static void create() {
		enemy[GOBLINTYPE] = new Enemy();
		enemy[GOBLINTYPE].info = new EnemyInfo(36, 36, 1024, 120, 0, 64, false, 15, 0) {
			@Override
			public short getHealth(SPRITE spr)
			{
				if (spr.pal == 5)
					return adjusthp(35);
				else if (spr.pal == 4)
					return adjusthp(25);

				return adjusthp(health);
			}
		};
		enemy[GOBLINTYPE].chase = new AIState() {
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

		enemy[GOBLINTYPE].die = new AIState() {
			@Override
			public void process(PLAYER plr, short i) {
				SPRITE spr = sprite[i];
				spr.lotag -= TICSPERFRAME;

				if (spr.lotag <= 0) {
					spr.picnum++;
					spr.lotag = 20;

					if (spr.picnum == GOBLINDEAD) {
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

		enemy[GOBLINTYPE].pain = new AIState() {
			@Override
			public void process(PLAYER plr, short i) {
				SPRITE spr = sprite[i];
				spr.lotag -= TICSPERFRAME;
				if (spr.lotag < 0) {
					spr.picnum = GOBLIN;
					spr.ang = (short) plr.ang;
					newstatus(i, FLEE);
				}

				aimove(i);
				processfluid(i, zr_florhit, false);
				engine.setsprite(i, spr.x, spr.y, spr.z);

				checkexpl(plr, i);
			}
		};

		enemy[GOBLINTYPE].face = new AIState() {
			@Override
			public void process(PLAYER plr, short i) {
				SPRITE spr = sprite[i];

				boolean cansee = engine.cansee(plr.x, plr.y, plr.z, plr.sector, spr.x, spr.y, spr.z - (engine.getTile(spr.picnum).getHeight() << 7),
						spr.sectnum);

				if (cansee && plr.invisibletime < 0) {
					spr.ang = engine.getangle(plr.x - spr.x, plr.y - spr.y);
					spr.picnum = GOBLIN;
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

		enemy[GOBLINTYPE].flee = new AIState() {
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

		enemy[GOBLINTYPE].stand = new AIState() {
			@Override
			public void process(PLAYER plr, short i) {
				SPRITE spr = sprite[i];

				spr.ang = engine.getangle(plr.x - spr.x, plr.y - spr.y);
				if (sintable[(spr.ang + 2560) & 2047] * (plr.x - spr.x)
						+ sintable[(spr.ang + 2048) & 2047] * (plr.y - spr.y) >= 0)
					if (engine.cansee(plr.x, plr.y, plr.z, plr.sector, spr.x, spr.y, spr.z - (engine.getTile(spr.picnum).getHeight() << 7),
							spr.sectnum) && plr.invisibletime < 0) {
						switch (spr.picnum) {
						case GOBLINCHILL:
							spr.picnum = GOBLINSURPRISE;
							playsound_loc(S_GOBPAIN1 + (engine.krand() % 2), spr.x, spr.y);
							newstatus(i, CHILL);
							break;
						default:
							spr.picnum = GOBLIN;
							if (plr.shadowtime > 0) {
								spr.ang = (short) (((engine.krand() & 512 - 256) + spr.ang + 1024) & 2047); // NEW
								newstatus(i, FLEE);
							} else
								newstatus(i, CHASE);
							break;
						}
					}

				checksector6(i);
			}
		};

		enemy[GOBLINTYPE].attack = new AIState() {
			@Override
			public void process(PLAYER plr, short i) {
				SPRITE spr = sprite[i];

				engine.getzrange(spr.x, spr.y, spr.z - 1, spr.sectnum, (spr.clipdist) << 2, CLIPMASK0);
				spr.z = zr_florz;

				switch (checkfluid(i, zr_florhit)) {
				case TYPELAVA:
					sprite[i].hitag--;
					if (sprite[i].hitag < 0)
						newstatus(i, DIE);
				case TYPEWATER:
					spr.z += engine.getTile(spr.picnum).getHeight() << 5;
					break;
				}

				engine.setsprite(i, spr.x, spr.y, spr.z);

				if (spr.lotag == 31) {
					if (checksight(plr, i))
						if (checkdist(plr, i)) {
							spr.ang = (short) checksight_ang;
							attack(plr, i);
						}
				} else if (spr.lotag < 0) {
					spr.picnum = GOBLIN;
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

		enemy[GOBLINTYPE].resurect = new AIState() {
			@Override
			public void process(PLAYER plr, short i) {
				SPRITE spr = sprite[i];

				spr.lotag -= TICSPERFRAME;
				if (spr.lotag < 0) {
					newstatus(i, FACE);
					spr.picnum = GOBLIN;
					spr.hitag = adjusthp(35);
					spr.lotag = 100;
					spr.cstat |= 1;
				}
			}
		};

		enemy[GOBLINTYPE].search = new AIState() {
			@Override
			public void process(PLAYER plr, short i) {
				aisearch(plr, i, false);
				if (!checksector6(i))
					checkexpl(plr, i);
			}
		};

		enemy[GOBLINTYPE].frozen = new AIState() {
			@Override
			public void process(PLAYER plr, short i) {
				SPRITE spr = sprite[i];

				spr.lotag -= TICSPERFRAME;
				if (spr.lotag < 0) {
					spr.pal = 0;
					spr.picnum = GOBLIN;
					newstatus(i, FACE);
				}
			}
		};

		enemy[GOBLINTYPE].nuked = new AIState() {
			@Override
			public void process(PLAYER plr, short i) {
				SPRITE spr = sprite[i];

				spr.lotag -= TICSPERFRAME;
				if (spr.lotag < 0) {
					spr.picnum++;
					spr.lotag = 24;
					if (spr.picnum == GOBLINCHAR + 4) {
						trailingsmoke(i, false);
						engine.deletesprite(i);
					}
				}
			}
		};

		enemy[GOBLINTYPE].skirmish = new AIState() {
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

	public static final AIState goblinChill = new AIState() {
		@Override
		public void process(PLAYER plr, short i) {
			SPRITE spr = sprite[i];

			spr.lotag -= TICSPERFRAME;
			if (spr.lotag < 0) {
				spr.picnum++;
				spr.lotag = 18;
				if (spr.picnum == GOBLINSURPRISE + 5) {
					spr.picnum = GOBLIN;
					newstatus(i, FACE);
				}
			}
		}
	};

	public static final AIState goblinWar = new AIState() {
		@Override
		public void process(PLAYER plr, short i) {
			SPRITE spr = sprite[i];
			short k;

			if (spr.lotag > 256) {
				spr.lotag = 100;
				spr.extra = 0;
			}

			switch (spr.extra) {
			case 0: // find new target
				long olddist = 1024 << 4;
				boolean found = false;
				for (k = 0; k < MAXSPRITES; k++) {
					if (sprite[k].picnum == GOBLIN && spr.pal != sprite[k].pal && spr.hitag == sprite[k].hitag) {
						long dist = klabs(spr.x - sprite[k].x) + klabs(spr.y - sprite[k].y);
						if (dist < olddist) {
							found = true;
							olddist = dist;
							spr.owner = k;
							spr.ang = engine.getangle(sprite[k].x - spr.x, sprite[k].y - spr.y);
							spr.extra = 1;
						}
					}
				}
				if (!found) {
					if (spr.pal == 5)
						spr.hitag = adjusthp(35);
					else if (spr.pal == 4)
						spr.hitag = adjusthp(25);
					else
						spr.hitag = adjusthp(15);
					if (plr.shadowtime > 0) {
						spr.ang = (short) (((engine.krand() & 512 - 256) + spr.ang + 1024) & 2047); // NEW
						newstatus(i, FLEE);
					} else
						newstatus(i, FACE);
				}
				break;
			case 1: // chase
				k = spr.owner;

				int movehit = aimove(i);
				if(movehit == 0)
					spr.ang = engine.getangle(sprite[k].x - spr.x, sprite[k].y - spr.y);
				else if((movehit & kHitTypeMask) == kHitWall) {
					spr.extra = 3;
					spr.ang = (short) ((spr.ang + (engine.krand() & 256 - 128)) & 2047);
					spr.lotag = 60;
				}
				else if((movehit & kHitTypeMask) == kHitSprite) {
					int sprnum = movehit & kHitIndexMask;
					if(sprnum != k) {
						spr.extra = 3;
						spr.ang = (short) ((spr.ang + (engine.krand() & 256 - 128)) & 2047);
						spr.lotag = 60;
					}
					else spr.ang = engine.getangle(sprite[k].x - spr.x, sprite[k].y - spr.y);
				}

				processfluid(i, zr_florhit, false);

				engine.setsprite(i, spr.x, spr.y, spr.z);
				if (checkdist(i, sprite[k].x, sprite[k].y, sprite[k].z)) {
					spr.extra = 2;
				} else
					spr.picnum = GOBLIN;

				if (checksector6(i))
					return;

				break;
			case 2: // attack
				k = spr.owner;
				if (checkdist(i, sprite[k].x, sprite[k].y, sprite[k].z)) {
					if ((engine.krand() & 1) != 0) {
						// goblins are fighting
						// JSA_DEMO
						if (engine.krand() % 10 > 6)
							playsound_loc(S_GENSWING, spr.x, spr.y);
						if (engine.krand() % 10 > 6)
							playsound_loc(S_SWORD1 + (engine.krand() % 6), spr.x, spr.y);

						if (checkdist(plr, i))
							addhealth(plr, -(engine.krand() & 5));

						if (engine.krand() % 100 > 90) { // if k is dead
							spr.extra = 0; // needs to
							spr.picnum = GOBLIN;
							sprite[k].extra = 4;
							sprite[k].picnum = GOBLINDIE;
							sprite[k].lotag = 20;
							sprite[k].hitag = 0;
							newstatus(k, DIE);
						} else { // i attack k flee
							spr.extra = 0;
							sprite[k].extra = 3;
							sprite[k].ang = (short) ((spr.ang + (engine.krand() & 256 - 128)) & 2047);
							sprite[k].lotag = 60;
						}
					}
				} else {
					spr.extra = 1;
				}

				processfluid(i, zr_florhit, false);

				engine.setsprite(i, spr.x, spr.y, spr.z);

				if (checksector6(i))
					return;

				break;
			case 3: // flee
				spr.lotag -= TICSPERFRAME;

				if (aimove(i) != 0)
					spr.ang = (short) (engine.krand() & 2047);
				processfluid(i, zr_florhit, false);

				engine.setsprite(i, spr.x, spr.y, spr.z);

				if (spr.lotag < 0) {
					spr.lotag = 0;
					spr.extra = 0;
				}

				if (checksector6(i))
					return;

				break;
			case 4: // pain
				spr.picnum = GOBLINDIE;
				break;
			case 5: // cast
				break;
			}

			checkexpl(plr, i);
		}
	};

	public static void goblinWarProcess(PLAYER plr)
	{
		for (short i = headspritestat[WAR], nextsprite; i >= 0; i = nextsprite) {
			nextsprite = nextspritestat[i];
			SPRITE spr = sprite[i];
			switch (spr.detail) {
			case GOBLINTYPE:
				goblinWar.process(plr, i);
				break;
			}
		}
	}

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

		spr.detail = GOBLINTYPE;

		if (spr.hitag < 90 || spr.hitag > 99)
			enemy[GOBLINTYPE].info.set(spr);
		else {
			short ohitag = spr.hitag;
			enemy[GOBLINTYPE].info.set(spr);
			if (spr.pal != 0)
				spr.xrepeat = 30;
			spr.extra = 0;
			spr.owner = 0;
			spr.hitag = ohitag;
			return;
		}

		if (spr.picnum == GOBLINCHILL) {
			engine.changespritestat(i, STAND);
			spr.lotag = 30;
			if (engine.krand() % 100 > 50)
				spr.extra = 1;
			return;
		}

		engine.changespritestat(i, FACE);
		if (engine.krand() % 100 > 50)
			spr.extra = 1;
	}
}
