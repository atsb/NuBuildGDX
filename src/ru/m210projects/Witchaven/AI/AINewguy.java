package ru.m210projects.Witchaven.AI;

import static ru.m210projects.Build.Engine.CLIPMASK0;
import static ru.m210projects.Build.Engine.sector;
import static ru.m210projects.Build.Engine.sintable;
import static ru.m210projects.Build.Engine.sprite;
import static ru.m210projects.Build.Engine.wall;
import static ru.m210projects.Build.Engine.zr_florhit;
import static ru.m210projects.Build.Engine.zr_florz;
import static ru.m210projects.Witchaven.AI.Ai.NEWGUYTYPE;
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
import static ru.m210projects.Witchaven.AI.Ai.skullycastspell;
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
import static ru.m210projects.Witchaven.Names.ANILAVA;
import static ru.m210projects.Witchaven.Names.LAVA;
import static ru.m210projects.Witchaven.Names.LAVA1;
import static ru.m210projects.Witchaven.Names.WALLARROW;
import static ru.m210projects.Witchaven.WH2Names.NEWGUY;
import static ru.m210projects.Witchaven.WH2Names.NEWGUYBOW;
import static ru.m210projects.Witchaven.WH2Names.NEWGUYCAST;
import static ru.m210projects.Witchaven.WH2Names.NEWGUYDEAD;
import static ru.m210projects.Witchaven.WH2Names.NEWGUYDIE;
import static ru.m210projects.Witchaven.WH2Names.NEWGUYKNEE;
import static ru.m210projects.Witchaven.WH2Names.NEWGUYMACE;
import static ru.m210projects.Witchaven.WH2Names.NEWGUYPUNCH;
import static ru.m210projects.Witchaven.WH2Names.NEWGUYSTAND;
import static ru.m210projects.Witchaven.WHFX.warpsprite;
import static ru.m210projects.Witchaven.WHOBJ.adjusthp;
import static ru.m210projects.Witchaven.WHOBJ.newstatus;
import static ru.m210projects.Witchaven.WHOBJ.trailingsmoke;
import static ru.m210projects.Witchaven.WHPLR.chunksofmeat;
import static ru.m210projects.Witchaven.WHSND.S_PLRWEAPON3;
import static ru.m210projects.Witchaven.WHSND.S_WISP;
import static ru.m210projects.Witchaven.WHSND.playsound_loc;

import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Witchaven.Types.PLAYER;

public class AINewguy {

	public static void create() {
		engine.getTile(NEWGUYDIE).anm = 0;
		engine.getTile(NEWGUYDIE + 3).anm = 0;
		enemy[NEWGUYTYPE] = new Enemy();
		enemy[NEWGUYTYPE].info = new EnemyInfo(35, 35, 1024 + 256, 120, 0, 48, false, 90, 0) {
			@Override
			public int getAttackDist(SPRITE spr)
			{
				int out = attackdist;
				switch (spr.picnum) {
				case NEWGUY:
				case NEWGUYMACE:
				case NEWGUYCAST:
				case NEWGUYBOW:
					if (spr.extra > 10)
						out = 2048 << 1;
					else out = 1024 + 256;
					break;
				case NEWGUYPUNCH:
					out = 1024 + 256;
					break;
				default:
					out = 512;
					break;
				}

				return out;
			}

			@Override
			public short getHealth(SPRITE spr)
			{
				switch(spr.picnum) {
				case NEWGUYSTAND:
				case NEWGUYKNEE:
					return adjusthp(50);
				case NEWGUYCAST:
					return adjusthp(85);
				case NEWGUYBOW:
					return adjusthp(85);
				case NEWGUYMACE:
					return adjusthp(45);
				case NEWGUYPUNCH:
					return adjusthp(15);
				}

				return adjusthp(health);
			}
		};
		enemy[NEWGUYTYPE].stand = new AIState() {
			@Override
			public void process(PLAYER plr, short i) {
				SPRITE spr = sprite[i];
				if (sintable[(spr.ang + 512) & 2047] * (plr.x - spr.x)
						+ sintable[spr.ang & 2047] * (plr.y - spr.y) >= 0)
					if (engine.cansee(spr.x, spr.y, spr.z - (engine.getTile(spr.picnum).getHeight() << 7), spr.sectnum, plr.x, plr.y,
							plr.z, plr.sector) && plr.invisibletime < 0) {
						if (plr.shadowtime > 0) {
							spr.ang = (short) (((engine.krand() & 512 - 256) + spr.ang + 1024) & 2047);
							newstatus(i, FLEE);
						} else
							newstatus(i, CHASE);
					}
			}
		};

		enemy[NEWGUYTYPE].chase = new AIState() {
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
			}
		};

		enemy[NEWGUYTYPE].resurect = new AIState() {
			@Override
			public void process(PLAYER plr, short i) {
				SPRITE spr = sprite[i];

				spr.lotag -= TICSPERFRAME;
				if (spr.lotag < 0) {
					newstatus(i, FACE);
					int j = engine.krand() % 3;
					switch (j) {
					case 0:
						spr.extra = 30;
						spr.hitag = adjusthp(85);
						break;
					case 1:
						spr.extra = 20;
						spr.hitag = adjusthp(85);
						break;
					case 2:
						spr.extra = 10;
						spr.hitag = adjusthp(45);
						break;
					case 3:
						spr.extra = 0;
						spr.hitag = adjusthp(15);
						break;
					}
					spr.xrepeat = 35;
					spr.yrepeat = 35;
					spr.picnum = NEWGUY;
				}
			}
		};

		enemy[NEWGUYTYPE].skirmish = new AIState() {
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

				checksector6(i);
			}
		};

		enemy[NEWGUYTYPE].search = new AIState() {
			@Override
			public void process(PLAYER plr, short i) {
				aisearch(plr, i, false);
				checksector6(i);
			}
		};

		enemy[NEWGUYTYPE].nuked = new AIState() {
			@Override
			public void process(PLAYER plr, short i) {
				SPRITE spr = sprite[i];
				chunksofmeat(plr, i, spr.x, spr.y, spr.z, spr.sectnum, spr.ang);
				trailingsmoke(i, false);
				newstatus(i, DIE);
			}
		};

		enemy[NEWGUYTYPE].pain = new AIState() {
			@Override
			public void process(PLAYER plr, short i) {
				SPRITE spr = sprite[i];
				spr.lotag -= TICSPERFRAME;
				if (spr.lotag < 0) {
					spr.picnum = NEWGUY;
					spr.ang = (short) plr.ang;
					newstatus(i, FLEE);
				}

				aimove(i);
				processfluid(i, zr_florhit, false);
				engine.setsprite(i, spr.x, spr.y, spr.z);
			}
		};

		enemy[NEWGUYTYPE].face = new AIState() {
			@Override
			public void process(PLAYER plr, short i) {
				SPRITE spr = sprite[i];

				boolean cansee = engine.cansee(plr.x, plr.y, plr.z, plr.sector, spr.x, spr.y, spr.z - (engine.getTile(spr.picnum).getHeight() << 7),
						spr.sectnum);

				if (cansee && plr.invisibletime < 0) {
					spr.ang = engine.getangle(plr.x - spr.x, plr.y - spr.y);
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

		enemy[NEWGUYTYPE].flee = new AIState() {
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
						if(plr.invisibletime < 0)
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

		enemy[NEWGUYTYPE].attack = new AIState() {
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

				switch (spr.picnum) {
				case NEWGUYCAST:
				case NEWGUYBOW:
					sprite[i].lotag -= TICSPERFRAME;
					if (sprite[i].lotag < 0) {
						if (engine.cansee(plr.x, plr.y, plr.z, plr.sector, sprite[i].x, sprite[i].y,
								sprite[i].z - (engine.getTile(sprite[i].picnum).getHeight() << 7), sprite[i].sectnum))
							newstatus(i, CAST);
						else
							newstatus(i, CHASE);
					} else
						sprite[i].ang = engine.getangle(plr.x - sprite[i].x, plr.y - sprite[i].y);
					break;
				case NEWGUYMACE:
				case NEWGUYPUNCH:
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

					checksector6(i);
					break;
				}
			}
		};

		enemy[NEWGUYTYPE].die = new AIState() {
			@Override
			public void process(PLAYER plr, short i) {
				SPRITE spr = sprite[i];
				spr.lotag -= TICSPERFRAME;

				if (spr.lotag <= 0) {
					spr.picnum++;
					spr.lotag = 20;

					if (spr.picnum == NEWGUYDEAD) {
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

		enemy[NEWGUYTYPE].cast = new AIState() {
			@Override
			public void process(PLAYER plr, short i) {
				SPRITE spr = sprite[i];

				spr.lotag -= TICSPERFRAME;
				if (spr.lotag < 0) {
					spr.picnum++;
					spr.lotag = 12;
				}

				if (spr.picnum == NEWGUYCAST + 2) {
					spr.extra--;
					spr.picnum = NEWGUY;
					playsound_loc(S_WISP, sprite[i].x, sprite[i].y);
					skullycastspell(plr, i);
					newstatus(i, CHASE);
				}
				if (spr.picnum == NEWGUYBOW + 2) {
					spr.extra--;
					spr.picnum = NEWGUY;
					playsound_loc(S_PLRWEAPON3, sprite[i].x, sprite[i].y);
					newguyarrow(i, plr);
					newstatus(i, CHASE);
				}
				checksector6(i);
			}
		};
	}

	public static void newguyarrow(short s, PLAYER plr) {
		int j = engine.insertsprite(sprite[s].sectnum, JAVLIN);
		if (j == -1)
			return;

		SPRITE spr = sprite[j];

		spr.x = sprite[s].x;
		spr.y = sprite[s].y;
		spr.z = sprite[s].z - (40 << 8);

		spr.cstat = 21;

		spr.picnum = WALLARROW;
		spr.ang = (short) (((sprite[s].ang + 2048 + 96) - 512) & 2047);
		spr.xrepeat = 24;
		spr.yrepeat = 24;
		spr.clipdist = 32;

		spr.extra = sprite[s].ang;
		spr.shade = -15;
		spr.xvel = (short) ((engine.krand() & 256) - 128);
		spr.yvel = (short) ((engine.krand() & 256) - 128);

		spr.zvel = (short) (((plr.z + (8 << 8) - sprite[s].z) << 7) / engine
				.ksqrt((plr.x - sprite[s].x) * (plr.x - sprite[s].x) + (plr.y - sprite[s].y) * (plr.y - sprite[s].y)));

		spr.zvel += ((engine.krand() % 256) - 128);

		spr.owner = s;
		spr.lotag = 1024;
		spr.hitag = 0;
		spr.pal = 0;
	}

	public static void premap(short i) {
		SPRITE spr = sprite[i];
		spr.detail = NEWGUYTYPE;

		enemy[NEWGUYTYPE].info.set(spr);

		switch (spr.picnum) {
		case NEWGUYSTAND:
		case NEWGUYKNEE:
			engine.changespritestat(i, STAND);
			if (spr.picnum == NEWGUYSTAND)
				spr.extra = 20;
			else
				spr.extra = 30;
			break;
		case NEWGUYCAST:
		case NEWGUYBOW:
		case NEWGUYMACE:
		case NEWGUYPUNCH:
		case NEWGUY:
			switch (spr.picnum) {
			case NEWGUYCAST:
				spr.extra = 30;
				break;
			case NEWGUYBOW:
				spr.extra = 20;
				break;
			case NEWGUYMACE:
				spr.extra = 10;
				break;
			case NEWGUYPUNCH:
				spr.extra = 0;
				break;
			}
			engine.changespritestat(i, FACE);
			spr.picnum = NEWGUY;
		}
	}
}
