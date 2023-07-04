package ru.m210projects.Witchaven.AI;

import static ru.m210projects.Build.Engine.CLIPMASK0;
import static ru.m210projects.Build.Engine.headspritestat;
import static ru.m210projects.Build.Engine.nextspritestat;
import static ru.m210projects.Build.Engine.sector;
import static ru.m210projects.Build.Engine.sprite;
import static ru.m210projects.Build.Engine.wall;
import static ru.m210projects.Build.Engine.zr_florhit;
import static ru.m210projects.Build.Engine.zr_florz;
import static ru.m210projects.Witchaven.AI.Ai.JUDYTYPE;
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
import static ru.m210projects.Witchaven.AI.Ai.skullycastspell;
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
import static ru.m210projects.Witchaven.Globals.WITCHSIT;
import static ru.m210projects.Witchaven.Globals.difficulty;
import static ru.m210projects.Witchaven.Globals.kHitFloor;
import static ru.m210projects.Witchaven.Globals.kHitIndexMask;
import static ru.m210projects.Witchaven.Globals.kHitTypeMask;
import static ru.m210projects.Witchaven.Globals.kHitWall;
import static ru.m210projects.Witchaven.Globals.killcnt;
import static ru.m210projects.Witchaven.Globals.kills;
import static ru.m210projects.Witchaven.Main.engine;
import static ru.m210projects.Witchaven.Main.game;
import static ru.m210projects.Witchaven.Names.GRONSW;
import static ru.m210projects.Witchaven.Names.SKELETON;
import static ru.m210projects.Witchaven.Names.WILLOW;
import static ru.m210projects.Witchaven.WH1Names.JUDY;
import static ru.m210projects.Witchaven.WH1Names.JUDYATTACK1;
import static ru.m210projects.Witchaven.WH1Names.JUDYATTACK2;
import static ru.m210projects.Witchaven.WH1Names.JUDYCHAR;
import static ru.m210projects.Witchaven.WH1Names.JUDYDEAD;
import static ru.m210projects.Witchaven.WH1Names.JUDYSIT;
import static ru.m210projects.Witchaven.WH1Names.SPIDER;
import static ru.m210projects.Witchaven.WH2Names.GONZOGSH;
import static ru.m210projects.Witchaven.WHFX.warpsprite;
import static ru.m210projects.Witchaven.WHOBJ.adjusthp;
import static ru.m210projects.Witchaven.WHOBJ.newstatus;
import static ru.m210projects.Witchaven.WHOBJ.trailingsmoke;
import static ru.m210projects.Witchaven.WHPLR.addarmor;
import static ru.m210projects.Witchaven.WHPLR.addhealth;
import static ru.m210projects.Witchaven.WHPLR.mapon;
import static ru.m210projects.Witchaven.WHSND.S_JUDY1;
import static ru.m210projects.Witchaven.WHSND.playsound_loc;

import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Witchaven.Types.PLAYER;

public class AIJudy {

	public static void judyOperate(PLAYER plr)
	{
		short nextsprite;
		for (short i = headspritestat[WITCHSIT]; i >= 0; i = nextsprite) {
			nextsprite = nextspritestat[i];

			sprite[i].ang = (short) (engine.getangle(plr.x - sprite[i].x, plr.y - sprite[i].y) & 2047);
			if (engine.cansee(plr.x, plr.y, plr.z, plr.sector, sprite[i].x, sprite[i].y,
					sprite[i].z - (engine.getTile(sprite[i].picnum).getHeight() << 7), sprite[i].sectnum)) {
				sprite[i].lotag -= TICSPERFRAME;
				if (sprite[i].lotag < 0) {
					sprite[i].picnum++;
					sprite[i].lotag = 12;
					if (sprite[i].picnum == JUDYSIT + 4) {
						sprite[i].picnum = JUDY;
						newstatus(i, FACE);
					}
				}
			}
		}
	}

	public static void create() {
		enemy[JUDYTYPE] = new Enemy();
		enemy[JUDYTYPE].info = new EnemyInfo(32, 32, 2048, 120, 0, 64, false, 500, 0);
		enemy[JUDYTYPE].chase = new AIState() {
			@Override
			public void process(PLAYER plr, short i) {
				SPRITE spr = sprite[i];
				spr.lotag -= TICSPERFRAME;
				if (spr.lotag < 0)
					spr.lotag = 250;

				short osectnum = spr.sectnum;

				if (mapon < 24) {
					sprite[i].extra -= TICSPERFRAME;
					if (sprite[i].extra < 0) {
						for (int j = 0; j < 8; j++)
							trailingsmoke(i, true);
						engine.deletesprite(i);
						return;
					}
				}

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

		enemy[JUDYTYPE].resurect = new AIState() {
			@Override
			public void process(PLAYER plr, short i) {
				SPRITE spr = sprite[i];

				spr.lotag -= TICSPERFRAME;
				if (spr.lotag < 0) {
					newstatus(i, FACE);
					spr.picnum = JUDY;
					spr.hitag = adjusthp(200);
					spr.lotag = 100;
					spr.cstat |= 1;
				}
			}
		};

		enemy[JUDYTYPE].search = new AIState() {
			@Override
			public void process(PLAYER plr, short i) {
				aisearch(plr, i, false);
				checksector6(i);
			}
		};

		enemy[JUDYTYPE].nuked = new AIState() {
			@Override
			public void process(PLAYER plr, short i) {
				SPRITE spr = sprite[i];

				spr.lotag -= TICSPERFRAME;
				if (spr.lotag < 0) {
					spr.picnum++;
					spr.lotag = 24;
					if (spr.picnum == JUDYCHAR + 4) {
						trailingsmoke(i, false);
						engine.deletesprite(i);
					}
				}
			}
		};

		enemy[JUDYTYPE].pain = new AIState() {
			@Override
			public void process(PLAYER plr, short i) {
				SPRITE spr = sprite[i];
				spr.lotag -= TICSPERFRAME;
				if (spr.lotag < 0) {
					spr.picnum = JUDY;
					spr.ang = (short) plr.ang;
					newstatus(i, FLEE);
				}

				aimove(i);
				processfluid(i, zr_florhit, false);
				engine.setsprite(i, spr.x, spr.y, spr.z);
			}
		};

		enemy[JUDYTYPE].face = new AIState() {
			@Override
			public void process(PLAYER plr, short i) {
				SPRITE spr = sprite[i];

				spr.ang = engine.getangle(plr.x - spr.x, plr.y - spr.y);

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

		enemy[JUDYTYPE].attack = new AIState() {
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

				sprite[i].extra -= TICSPERFRAME;
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

		enemy[JUDYTYPE].flee = new AIState() {
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
			}
		};

		enemy[JUDYTYPE].cast = new AIState() {
			@Override
			public void process(PLAYER plr, short i) {
				SPRITE spr = sprite[i];

				spr.lotag -= TICSPERFRAME;
				if (spr.lotag < 0) {
					spr.picnum++;
					spr.lotag = 12;
				}

				if (spr.picnum == JUDYATTACK1 + 3) {
					sprite[i].picnum = JUDYATTACK1;
					playsound_loc(S_JUDY1 + engine.krand() % 4, sprite[i].x, sprite[i].y);
					if (engine.krand() % 100 > 70) {
						castspell(plr, i);
					} else {
						if (engine.krand() % 100 > 40) {
							// raise the dead
							short j = headspritestat[DEAD];
							while (j >= 0) {
								short nextj = nextspritestat[j];
								sprite[j].lotag = (short) ((engine.krand() % 120) + 120);
								kills--;
								newstatus(j, RESURECT);
								j = nextj;
							}
						} else {
							if (engine.krand() % 100 > 50) {
								// curse
								for (int j = 1; j < 9; j++) {
									plr.ammo[j] = 3;
								}
							} else {
								int j = engine.krand() % 5;
								switch (j) {
								case 0:// SPAWN WILLOW
									spawnabaddy(i, WILLOW);
									break;
								case 1:// SPAWN 10 SPIDERS
									for (j = 0; j < 4; j++) {
										spawnabaddy(i, SPIDER);
									}
									break;
								case 2:// SPAWN 2 GRONSW
									for (j = 0; j < 2; j++) {
										spawnabaddy(i, GRONSW);
									}
									break;
								case 3:// SPAWN SKELETONS
									for (j = 0; j < 4; j++) {
										spawnabaddy(i, SKELETON);
									}
									break;
								case 4:
									castspell(plr, i);
									break;
								}
							}
						}
					}
					newstatus(i, CHASE);
				} else if (spr.picnum == JUDYATTACK2 + 8) {
					sprite[i].picnum = JUDYATTACK2;
					playsound_loc(S_JUDY1 + engine.krand() % 4, sprite[i].x, sprite[i].y);
					if (engine.krand() % 100 > 50)
						skullycastspell(plr, i);
					else {
						if (engine.krand() % 100 > 70) {
							if (engine.krand() % 100 > 50) {
								plr.health = 0;
								addhealth(plr, 1);
							} else {
								addarmor(plr, -(plr.armor));
								plr.armortype = 0;
							}
						} else {
							int j = engine.krand() % 5;
							switch (j) {
							case 0:// SPAWN WILLOW
								spawnabaddy(i, WILLOW);
								break;
							case 1:// SPAWN 6 SPIDERS
								for (j = 0; j < 4; j++) {
									spawnabaddy(i, SPIDER);
								}
								break;
							case 2:// SPAWN 2 GRONSW
								for (j = 0; j < 2; j++) {
									spawnabaddy(i, GRONSW);
								}
								break;
							case 3:// SPAWN SKELETONS
								for (j = 0; j < 4; j++) {
									spawnabaddy(i, SKELETON);
								}
								break;
							case 4:
								castspell(plr, i);
								break;
							}
						}

					}
					newstatus(i, CHASE);
				}
				checksector6(i);
			}
		};

		enemy[JUDYTYPE].die = new AIState() {
			@Override
			public void process(PLAYER plr, short i) {
				SPRITE spr = sprite[i];
				spr.lotag -= TICSPERFRAME;

				if (spr.lotag <= 0) {
					spr.picnum++;
					spr.lotag = 20;

					if (spr.picnum == JUDYDEAD) {
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

	public static void spawnabaddy(int i, int monster) {
		short j = engine.insertsprite(sprite[i].sectnum, FACE);

		sprite[j].x = sprite[i].x + (engine.krand() & 2048) - 1024;
		sprite[j].y = sprite[i].y + (engine.krand() & 2048) - 1024;
		sprite[j].z = sprite[i].z;

		sprite[j].pal = 0;
		sprite[j].shade = 0;
		sprite[j].cstat = 0;

		if(monster == WILLOW)
			AIWillow.premap(j);
		else if(monster == SPIDER)
			AISpider.premap(j);
		else if(monster == GRONSW)
			AIGron.premap(j);
		else if(monster == SKELETON)
			AISkeleton.premap(j);
		else if(monster == GONZOGSH)
			AIGonzo.premap(j);

		sprite[j].picnum = (short) monster;
		killcnt++;

		engine.setsprite(j, sprite[j].x, sprite[j].y, sprite[j].z);
		game.pInt.setsprinterpolate(j, sprite[j]);
	}

	public static void premap(short i) {
		SPRITE spr = sprite[i];
		spr.detail = JUDYTYPE;

		enemy[JUDYTYPE].info.set(spr);

		if (mapon > 24)
			spr.hitag = adjusthp(700);

		if (spr.picnum == JUDYSIT) {
			engine.changespritestat(i, WITCHSIT);
			spr.extra = 1200;
		} else
			engine.changespritestat(i, FACE);
	}
}
