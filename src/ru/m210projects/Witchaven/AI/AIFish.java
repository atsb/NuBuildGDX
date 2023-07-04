package ru.m210projects.Witchaven.AI;

import static ru.m210projects.Build.Engine.CLIPMASK0;
import static ru.m210projects.Build.Engine.sector;
import static ru.m210projects.Build.Engine.sprite;
import static ru.m210projects.Build.Engine.zr_florhit;
import static ru.m210projects.Witchaven.AI.Ai.FISHTYPE;
import static ru.m210projects.Witchaven.AI.Ai.TYPELAVA;
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
import static ru.m210projects.Witchaven.Globals.DIE;
import static ru.m210projects.Witchaven.Globals.FACE;
import static ru.m210projects.Witchaven.Globals.FINDME;
import static ru.m210projects.Witchaven.Globals.FLEE;
import static ru.m210projects.Witchaven.Globals.KILLSECTOR;
import static ru.m210projects.Witchaven.Globals.SKIRMISH;
import static ru.m210projects.Witchaven.Globals.TICSPERFRAME;
import static ru.m210projects.Witchaven.Globals.kHitIndexMask;
import static ru.m210projects.Witchaven.Globals.kHitSector;
import static ru.m210projects.Witchaven.Globals.kHitSprite;
import static ru.m210projects.Witchaven.Globals.kHitTypeMask;
import static ru.m210projects.Witchaven.Main.engine;
import static ru.m210projects.Witchaven.Names.ANILAVA;
import static ru.m210projects.Witchaven.Names.LAVA;
import static ru.m210projects.Witchaven.Names.LAVA1;
import static ru.m210projects.Witchaven.WHFX.warpsprite;
import static ru.m210projects.Witchaven.WHOBJ.newstatus;

import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Witchaven.Types.PLAYER;

public class AIFish {

	public static void create() {
		enemy[FISHTYPE] = new Enemy();
		enemy[FISHTYPE].info = new EnemyInfo(1, 1, 512, 120, 0, 32, false, 10, 0);
		enemy[FISHTYPE].chase = new AIState() {
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
			}
		};

		enemy[FISHTYPE].die = new AIState() {
			@Override
			public void process(PLAYER plr, short i) {
				engine.deletesprite(i);
			}
		};

		enemy[FISHTYPE].attack = new AIState() {
			@Override
			public void process(PLAYER plr, short i) {
				SPRITE spr = sprite[i];

				spr.z = sector[sprite[i].sectnum].floorz;

				switch (checkfluid(i, zr_florhit)) {
				case TYPELAVA:
					sprite[i].hitag--;
					if (sprite[i].hitag < 0)
						newstatus(i, DIE);
					break;
				}

				engine.setsprite(i, spr.x, spr.y, spr.z);

				if (spr.lotag >= 64) {
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

		enemy[FISHTYPE].skirmish = new AIState() {
			@Override
			public void process(PLAYER plr, short i) {
				SPRITE spr = sprite[i];

				spr.lotag -= TICSPERFRAME;

				if (spr.lotag < 0)
					newstatus(i, FACE);
				short osectnum = spr.sectnum;
				if (aimove(i) != 0) {
					spr.ang = engine.getangle(plr.x - spr.x, plr.y - spr.y);
					newstatus(i, FACE);
				}
				if ((spr.sectnum != osectnum) && (sector[spr.sectnum].lotag == 10))
					warpsprite(i);

				processfluid(i, zr_florhit, false);

				engine.setsprite(i, spr.x, spr.y, spr.z);

				if (checksector6(i))
					return;
			}
		};

		enemy[FISHTYPE].search = new AIState() {
			@Override
			public void process(PLAYER plr, short i) {
				aisearch(plr, i, false);
				checksector6(i);
			}
		};

		enemy[FISHTYPE].face = new AIState() {
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
	}

	public static void premap(short i) {
		SPRITE spr = sprite[i];

		spr.detail = FISHTYPE;
		engine.changespritestat(i, FACE);
		enemy[FISHTYPE].info.set(spr);
	}
}
