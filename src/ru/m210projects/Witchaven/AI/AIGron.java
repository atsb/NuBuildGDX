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
import static ru.m210projects.Witchaven.AI.Ai.GRONTYPE;
import static ru.m210projects.Witchaven.AI.Ai.TYPELAVA;
import static ru.m210projects.Witchaven.AI.Ai.TYPEWATER;
import static ru.m210projects.Witchaven.AI.Ai.aimove;
import static ru.m210projects.Witchaven.AI.Ai.aisearch;
import static ru.m210projects.Witchaven.AI.Ai.attack;
import static ru.m210projects.Witchaven.AI.Ai.castspell;
import static ru.m210projects.Witchaven.AI.Ai.checkdist;
import static ru.m210projects.Witchaven.AI.Ai.checkfluid;
import static ru.m210projects.Witchaven.AI.Ai.checksector6;
import static ru.m210projects.Witchaven.AI.Ai.checksight;
import static ru.m210projects.Witchaven.AI.Ai.checksight_ang;
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
import static ru.m210projects.Witchaven.Globals.JAVLIN;
import static ru.m210projects.Witchaven.Globals.KILLSECTOR;
import static ru.m210projects.Witchaven.Globals.PICKDISTANCE;
import static ru.m210projects.Witchaven.Globals.RESURECT;
import static ru.m210projects.Witchaven.Globals.SKIRMISH;
import static ru.m210projects.Witchaven.Globals.TICSPERFRAME;
import static ru.m210projects.Witchaven.Globals.difficulty;
import static ru.m210projects.Witchaven.Globals.kHitFloor;
import static ru.m210projects.Witchaven.Globals.kHitIndexMask;
import static ru.m210projects.Witchaven.Globals.kHitSector;
import static ru.m210projects.Witchaven.Globals.kHitSprite;
import static ru.m210projects.Witchaven.Globals.kHitTypeMask;
import static ru.m210projects.Witchaven.Globals.kHitWall;
import static ru.m210projects.Witchaven.Globals.kills;
import static ru.m210projects.Witchaven.Items.THROWHALBERDTYPE;
import static ru.m210projects.Witchaven.Main.engine;
import static ru.m210projects.Witchaven.Main.game;
import static ru.m210projects.Witchaven.Names.ANILAVA;
import static ru.m210projects.Witchaven.Names.EXPLO2;
import static ru.m210projects.Witchaven.Names.GRONDEAD;
import static ru.m210projects.Witchaven.Names.GRONDIE;
import static ru.m210projects.Witchaven.Names.GRONHAL;
import static ru.m210projects.Witchaven.Names.GRONHALATTACK;
import static ru.m210projects.Witchaven.Names.GRONHALDIE;
import static ru.m210projects.Witchaven.Names.GRONHALPAIN;
import static ru.m210projects.Witchaven.Names.GRONMU;
import static ru.m210projects.Witchaven.Names.GRONMUATTACK;
import static ru.m210projects.Witchaven.Names.GRONMUDIE;
import static ru.m210projects.Witchaven.Names.GRONMUPAIN;
import static ru.m210projects.Witchaven.Names.GRONSW;
import static ru.m210projects.Witchaven.Names.GRONSWATTACK;
import static ru.m210projects.Witchaven.Names.GRONSWDIE;
import static ru.m210projects.Witchaven.Names.GRONSWPAIN;
import static ru.m210projects.Witchaven.Names.LAVA;
import static ru.m210projects.Witchaven.Names.LAVA1;
import static ru.m210projects.Witchaven.Names.MONSTERBALL;
import static ru.m210projects.Witchaven.Names.THROWHALBERD;
import static ru.m210projects.Witchaven.WH1Names.GRONCHAR;
import static ru.m210projects.Witchaven.WHFX.warpsprite;
import static ru.m210projects.Witchaven.WHOBJ.adjusthp;
import static ru.m210projects.Witchaven.WHOBJ.getPickHeight;
import static ru.m210projects.Witchaven.WHOBJ.movesprite;
import static ru.m210projects.Witchaven.WHOBJ.newstatus;
import static ru.m210projects.Witchaven.WHOBJ.trailingsmoke;
import static ru.m210projects.Witchaven.WHPLR.chunksofmeat;
import static ru.m210projects.Witchaven.WHSND.S_SPELL2;
import static ru.m210projects.Witchaven.WHSND.S_THROWPIKE;
import static ru.m210projects.Witchaven.WHSND.playsound_loc;

import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Witchaven.Types.PLAYER;

public class AIGron {

	public static void create() {
		enemy[GRONTYPE] = new Enemy();
		enemy[GRONTYPE].info = new EnemyInfo(game.WH2 ? 35 : 30, game.WH2 ? 35 : 30, -1, 120, 0, 64, false, 300, 0) {
			@Override
			public int getAttackDist(SPRITE spr)
			{
				int out = attackdist;
				int pic = spr.picnum;

				if(pic == GRONHAL || pic == GRONHALATTACK)
					out = 1024 + 512;
				else if(pic == GRONMU || pic == GRONMUATTACK)
					out = 2048;
				else  if(pic == GRONSW || pic == GRONSWATTACK)
					out = 1024 + 256;

				return out;
			}

			@Override
			public short getHealth(SPRITE spr)
			{
				if(game.WH2) {
					if(spr.picnum == GRONHAL)
						return adjusthp(65);
					if(spr.picnum == GRONMU)
						return adjusthp(70);
				}
				return adjusthp(health);
			}
		};
		enemy[GRONTYPE].chase = new AIState() {
			@Override
			public void process(PLAYER plr, short i) {
				SPRITE spr = sprite[i];
				spr.lotag -= TICSPERFRAME;
				if (spr.lotag < 0)
					spr.lotag = 250;

				short osectnum = spr.sectnum;

				if (spr.picnum == GRONSW) {
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
				} else {
					if (engine.krand() % 63 == 0) {
						if (engine.cansee(plr.x, plr.y, plr.z, plr.sector, sprite[i].x, sprite[i].y,
								sprite[i].z - (engine.getTile(sprite[i].picnum).getHeight() << 7), sprite[i].sectnum))// && invisibletime < 0)
							newstatus(i, ATTACK);
					} else {
						checksight(plr, i);
						if (!checkdist(plr, i)) {
							if((aimove(i) & kHitTypeMask) == kHitFloor)
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

		enemy[GRONTYPE].resurect = new AIState() {
			@Override
			public void process(PLAYER plr, short i) {
				SPRITE spr = sprite[i];

				spr.lotag -= TICSPERFRAME;
				if (spr.lotag < 0) {
					newstatus(i, FACE);
					switch (engine.krand() % 3) {
					case 0:
						sprite[i].picnum = GRONHAL;
						sprite[i].hitag = adjusthp(120);
						sprite[i].extra = 3;
						break;
					case 1:
						sprite[i].picnum = GRONSW;
						sprite[i].hitag = adjusthp(120);
						sprite[i].extra = 0;
						break;
					case 2:
						sprite[i].picnum = GRONMU;
						sprite[i].hitag = adjusthp(120);
						sprite[i].extra = 2;
						break;
					}
					spr.lotag = 100;
					spr.cstat |= 1;
				}
			}
		};

		enemy[GRONTYPE].skirmish = new AIState() {
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

		enemy[GRONTYPE].search = new AIState() {
			@Override
			public void process(PLAYER plr, short i) {
				aisearch(plr, i, false);
				if (!checksector6(i))
					checkexpl(plr, i);
			}
		};

		enemy[GRONTYPE].nuked = new AIState() {
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
					if (spr.picnum == GRONCHAR + 4) {
						trailingsmoke(i, false);
						engine.deletesprite(i);
					}
				}
			}
		};

		enemy[GRONTYPE].frozen = new AIState() {
			@Override
			public void process(PLAYER plr, short i) {
				SPRITE spr = sprite[i];

				spr.lotag -= TICSPERFRAME;
				if (spr.lotag < 0) {
					spr.pal = 0;
					if(sprite[i].picnum == GRONHALDIE)
						sprite[i].picnum = GRONHAL;
					else if(sprite[i].picnum == GRONSWDIE)
						sprite[i].picnum = GRONSW;
					else if(sprite[i].picnum == GRONMUDIE)
						sprite[i].picnum = GRONMU;
					newstatus(i, FACE);
				}
			}
		};

		enemy[GRONTYPE].pain = new AIState() {
			@Override
			public void process(PLAYER plr, short i) {
				SPRITE spr = sprite[i];
				spr.lotag -= TICSPERFRAME;
				if (spr.lotag < 0) {
					if(sprite[i].picnum == GRONHALPAIN)
						sprite[i].picnum = GRONHAL;
					else if(sprite[i].picnum == GRONSWPAIN)
						sprite[i].picnum = GRONSW;
					else if(sprite[i].picnum == GRONMUPAIN)
						sprite[i].picnum = GRONMU;

					spr.ang = (short) plr.ang;
					newstatus(i, FLEE);
				}

				aimove(i);
				processfluid(i, zr_florhit, false);
				engine.setsprite(i, spr.x, spr.y, spr.z);

				checkexpl(plr, i);
			}
		};

		enemy[GRONTYPE].face = new AIState() {
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

		enemy[GRONTYPE].attack = new AIState() {
			@Override
			public void process(PLAYER plr, short i) {
				SPRITE spr = sprite[i];

				if (spr.picnum == GRONSWATTACK) {
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
						if (plr.shadowtime > 0) {
							spr.ang = (short) (((engine.krand() & 512 - 256) + spr.ang + 1024) & 2047); // NEW
							newstatus(i, FLEE);
						} else
							newstatus(i, CHASE);
					}
					spr.lotag -= TICSPERFRAME;
				} else {
					spr.lotag -= TICSPERFRAME;
					if (spr.lotag < 0) {
						if (engine.cansee(plr.x, plr.y, plr.z, plr.sector, sprite[i].x, sprite[i].y,
								spr.z - (engine.getTile(sprite[i].picnum).getHeight() << 7), sprite[i].sectnum))
							newstatus(i, CAST);
						else
							newstatus(i, CHASE);
					} else
						spr.ang = engine.getangle(plr.x - sprite[i].x, plr.y - sprite[i].y);
				}

				checksector6(i);
			}
		};

		enemy[GRONTYPE].flee = new AIState() {
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

		enemy[GRONTYPE].cast = new AIState() {
			@Override
			public void process(PLAYER plr, short i) {
				SPRITE spr = sprite[i];

				spr.lotag -= TICSPERFRAME;
				if (spr.lotag < 0) {
					if (spr.picnum == GRONHALATTACK) {
						spr.extra--;
						playsound_loc(S_THROWPIKE, sprite[i].x, sprite[i].y);
						throwhalberd(i);
						newstatus(i, CHASE);
					} else if (spr.picnum == GRONMUATTACK) {
						spr.extra--;
						playsound_loc(S_SPELL2, sprite[i].x, sprite[i].y);
						castspell(plr, i);
						newstatus(i, CHASE);
					}
				}

				checksector6(i);
			}
		};

		enemy[GRONTYPE].die = new AIState() {
			@Override
			public void process(PLAYER plr, short i) {
				SPRITE spr = sprite[i];
				spr.lotag -= TICSPERFRAME;

				if(spr.picnum == GRONSWDIE || spr.picnum == GRONHALDIE || spr.picnum == GRONMUDIE)
				{
					if (spr.lotag < 0) {
						spr.picnum = GRONDIE;
						spr.lotag = 20;
					} else
						return;
				}

				if (spr.lotag <= 0) {
					spr.picnum++;
					spr.lotag = 20;

					if (spr.picnum == GRONDEAD) {
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

	public static void throwhalberd(int s) {
		int j = engine.insertsprite(sprite[s].sectnum, JAVLIN);

		if (j == -1)
			return;
		SPRITE spr = sprite[j];
		spr.x = sprite[s].x;
		spr.y = sprite[s].y;
		spr.z = sprite[s].z - (40 << 8);

		spr.cstat = 17;

		spr.picnum = THROWHALBERD;
		spr.detail = THROWHALBERDTYPE;
		spr.ang = (short) (((sprite[s].ang + 2048) - 512) & 2047);
		spr.xrepeat = 8;
		spr.yrepeat = 16;
		spr.clipdist = 32;

		spr.extra = sprite[s].ang;
		spr.shade = -15;
		spr.xvel = (short) ((engine.krand() & 256) - 128);
		spr.yvel = (short) ((engine.krand() & 256) - 128);
		spr.zvel = (short) ((engine.krand() & 256) - 128);
		spr.owner = (short) s;
		spr.lotag = 0;
		spr.hitag = 0;
		spr.pal = 0;

		spr.cstat = 0;
		int daz = (((spr.zvel) * TICSPERFRAME) >> 3);
		movesprite((short) j, ((sintable[(spr.extra + 512) & 2047]) * TICSPERFRAME) << 7,
				((sintable[spr.extra & 2047]) * TICSPERFRAME) << 7, daz, 4 << 8, 4 << 8, 1);
		spr.cstat = 21;
	}

	public static void premap(short i) {
		SPRITE spr = sprite[i];

		if (spr.picnum == GRONSW && spr.pal == 10)
			engine.deletesprite(i);

		spr.detail = GRONTYPE;
		enemy[GRONTYPE].info.set(spr);
		engine.changespritestat(i, FACE);

		if(spr.picnum == GRONHAL)
			spr.extra = 4;
		else if(spr.picnum == GRONSW)
			spr.extra = 0;
		else if(spr.picnum == GRONMU)
			spr.extra = 2;
	}
}
