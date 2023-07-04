package ru.m210projects.Witchaven.AI;

import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Witchaven.AI.Ai.WILLOWTYPE;
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
import static ru.m210projects.Witchaven.Globals.CHASE;
import static ru.m210projects.Witchaven.Globals.DEAD;
import static ru.m210projects.Witchaven.Globals.DIE;
import static ru.m210projects.Witchaven.Globals.DRAIN;
import static ru.m210projects.Witchaven.Globals.FACE;
import static ru.m210projects.Witchaven.Globals.FINDME;
import static ru.m210projects.Witchaven.Globals.FLEE;
import static ru.m210projects.Witchaven.Globals.KILLSECTOR;
import static ru.m210projects.Witchaven.Globals.RESURECT;
import static ru.m210projects.Witchaven.Globals.difficulty;
import static ru.m210projects.Witchaven.Globals.kHitIndexMask;
import static ru.m210projects.Witchaven.Globals.kHitSector;
import static ru.m210projects.Witchaven.Globals.kHitTypeMask;
import static ru.m210projects.Witchaven.Globals.kHitWall;
import static ru.m210projects.Witchaven.Globals.kills;
import static ru.m210projects.Witchaven.Globals.TICSPERFRAME;
import static ru.m210projects.Witchaven.Main.engine;
import static ru.m210projects.Witchaven.Main.game;
import static ru.m210projects.Witchaven.Names.*;
import static ru.m210projects.Witchaven.WHFX.warpsprite;
import static ru.m210projects.Witchaven.WHOBJ.newstatus;
import static ru.m210projects.Witchaven.WHOBJ.trailingsmoke;
import static ru.m210projects.Witchaven.WHPLR.chunksofmeat;
import static ru.m210projects.Witchaven.WHSND.S_FIREBALL;
import static ru.m210projects.Witchaven.WHSND.playsound_loc;
import static ru.m210projects.Witchaven.WHScreen.showmessage;

import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Witchaven.Types.PLAYER;

public class AIWillow {

	public static void create() {
		enemy[WILLOWTYPE] = new Enemy();
		enemy[WILLOWTYPE].info = new EnemyInfo(32, 32, 512, 120, 0, 64, true, game.WH2 ? 5 : 400, 0);
		enemy[WILLOWTYPE].chase = new AIState() {
			@Override
			public void process(PLAYER plr, short i) {
				SPRITE spr = sprite[i];
				spr.lotag -= TICSPERFRAME;
				if (spr.lotag < 0)
					spr.lotag = 250;

				short osectnum = spr.sectnum;
				if (engine.krand() % 63 == 0) {
					if (engine.cansee(plr.x, plr.y, plr.z, plr.sector, sprite[i].x, sprite[i].y,
							sprite[i].z - (engine.getTile(sprite[i].picnum).getHeight() << 7), sprite[i].sectnum) && plr.invisibletime < 0)
						newstatus(i, ATTACK);
					return;
				} else {
					//sprite[i].z = sector[sprite[i].sectnum].floorz - (32 << 8);
					int dax = (sintable[(sprite[i].ang + 512) & 2047] * TICSPERFRAME) << 3;
					int day = (sintable[sprite[i].ang & 2047] * TICSPERFRAME) << 3;
					checksight(plr, i);

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

				if(spr.z > zr_florz)
					spr.z = zr_florz;
				if(spr.z < zr_ceilz - (32 << 8))
					spr.z = zr_ceilz - (32 << 8);

				if (checksector6(i))
					return;

				processfluid(i, zr_florhit, true);

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
			}
		};

		enemy[WILLOWTYPE].attack = new AIState() {
			@Override
			public void process(PLAYER plr, short i) {
				SPRITE spr = sprite[i];

				spr.lotag -= TICSPERFRAME;
				if (plr.z < spr.z)
					spr.z -= TICSPERFRAME << 8;

				if (plr.z > spr.z)
					spr.z += TICSPERFRAME << 8;

				if (spr.lotag < 0) {
					if (engine.cansee(plr.x, plr.y, plr.z, plr.sector, spr.x, spr.y, spr.z - (engine.getTile(spr.picnum).getHeight() << 7),
							spr.sectnum))
						if (checkdist(plr, i)) {
							if (plr.shockme < 0)
								if ((engine.krand() & 1) != 0) {
									plr.shockme = 120;
									if (!game.WH2) {
										plr.lvl--;
										switch (plr.lvl) {
										case 1:
											plr.score = 0;
											plr.maxhealth = 100;
											break;
										case 2:
											plr.score = 2350;
											plr.maxhealth = 120;
											break;
										case 3:
											plr.score = 4550;
											plr.maxhealth = 140;
											break;
										case 4:
											plr.score = 9300;
											plr.maxhealth = 160;
											break;
										case 5:
											plr.score = 18400;
											plr.maxhealth = 180;
											break;
										case 6:
											plr.score = 36700;
											plr.maxhealth = 200;
											break;
										case 7:
											plr.score = 75400;
											plr.maxhealth = 200;
											break;
										}
										if (plr.lvl < 1) {
											plr.lvl = 1;
											plr.health = -1;
										}
										showmessage("Level Drained", 360);
									} else
										showmessage("Shocked", 360);

								}
						} else
							newstatus(i, DRAIN);
					else
						newstatus(i, CHASE);
				}

				int floorz = engine.getflorzofslope(spr.sectnum,spr.x, spr.y) - (16 << 8);
				if(spr.z > floorz)
					spr.z = floorz;
			}
		};

		enemy[WILLOWTYPE].face = new AIState() {
			@Override
			public void process(PLAYER plr, short i) {
				SPRITE spr = sprite[i];


				if (engine.cansee(plr.x, plr.y, plr.z, plr.sector, spr.x, spr.y, spr.z - (engine.getTile(spr.picnum).getHeight() << 7),
						spr.sectnum) && plr.invisibletime < 0) {
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
					} else newstatus(i, FLEE);
				}

				if (checkdist(plr, i))
					newstatus(i, ATTACK);
			}
		};

		enemy[WILLOWTYPE].search = new AIState() {
			@Override
			public void process(PLAYER plr, short i) {
				aisearch(plr, i, true);
				checksector6(i);
			}
		};

		enemy[WILLOWTYPE].flee = new AIState() {
			@Override
			public void process(PLAYER plr, short i) {
				SPRITE spr = sprite[i];
				spr.lotag -= TICSPERFRAME;
				short osectnum = spr.sectnum;

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

		enemy[WILLOWTYPE].die = new AIState() {
			@Override
			public void process(PLAYER plr, short i) {
				SPRITE spr = sprite[i];
				spr.lotag -= TICSPERFRAME;

				if (spr.lotag <= 0) {
					spr.picnum++;
					spr.lotag = 20;
					if (sprite[i].picnum == WILLOWEXPLO || sprite[i].picnum == WILLOWEXPLO + 1
							|| sprite[i].picnum == WILLOWEXPLO + 2)
						sprite[i].xrepeat = sprite[i].yrepeat <<= 1;

					if (spr.picnum == WILLOWEXPLO + 2) {
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

		enemy[WILLOWTYPE].nuked = new AIState() {
			@Override
			public void process(PLAYER plr, short i) {
				SPRITE spr = sprite[i];

				if(game.WH2) {
					chunksofmeat(plr, i, spr.x, spr.y, spr.z, spr.sectnum, spr.ang);
					trailingsmoke(i, false);
					newstatus(i, DIE);
				}
			}
		};
	}

	public static void willowProcess(PLAYER plr)
	{
		for (short i = headspritestat[DRAIN], nextsprite; i >= 0; i = nextsprite) {
			nextsprite = nextspritestat[i];
			SPRITE spr = sprite[i];

			switch (spr.detail) {
			case WILLOWTYPE:
				willowDrain.process(plr, i);
				break;
			}
		}
	}

	public static final AIState willowDrain = new AIState() {
		@Override
		public void process(PLAYER plr, short i) {
			SPRITE spr = sprite[i];

			spr.lotag -= TICSPERFRAME;
			if (spr.lotag < 0) {
				playsound_loc(S_FIREBALL, spr.x, spr.y);
				int oldz = spr.z;
				spr.z += 6144;
				castspell(plr, i);
				spr.z = oldz;
				newstatus(i, CHASE);
			}
		}
	};

	public static void premap(short i) {
		SPRITE spr = sprite[i];

		spr.detail = WILLOWTYPE;
		enemy[WILLOWTYPE].info.set(spr);
		spr.cstat |= 128;
		spr.z -= engine.getTile(WILLOW).getHeight() << 8;
		engine.changespritestat(i, FACE);
	}
}
