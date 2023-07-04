package ru.m210projects.Witchaven.AI;

import static ru.m210projects.Build.Engine.headspritesect;
import static ru.m210projects.Build.Engine.nextspritesect;
import static ru.m210projects.Build.Engine.sintable;
import static ru.m210projects.Build.Engine.sprite;
import static ru.m210projects.Build.Pragmas.klabs;
import static ru.m210projects.Witchaven.AI.Ai.GONZOTYPE;
import static ru.m210projects.Witchaven.AI.Ai.KURTTYPE;
import static ru.m210projects.Witchaven.AI.Ai.enemy;
import static ru.m210projects.Witchaven.Globals.CHASE;
import static ru.m210projects.Witchaven.Globals.DIE;
import static ru.m210projects.Witchaven.Globals.FLEE;
import static ru.m210projects.Witchaven.Globals.PICKDISTANCE;
import static ru.m210projects.Witchaven.Globals.STAND;
import static ru.m210projects.Witchaven.Globals.TICSPERFRAME;
import static ru.m210projects.Witchaven.Main.engine;
import static ru.m210projects.Witchaven.WH2Names.GONZOCSW;
import static ru.m210projects.Witchaven.WH2Names.GONZOCSWAT;
import static ru.m210projects.Witchaven.WH2Names.KURTAT;
import static ru.m210projects.Witchaven.WH2Names.KURTKNEE;
import static ru.m210projects.Witchaven.WH2Names.KURTSTAND;
import static ru.m210projects.Witchaven.WHOBJ.getPickHeight;
import static ru.m210projects.Witchaven.WHOBJ.newstatus;
import static ru.m210projects.Witchaven.WHOBJ.trailingsmoke;
import static ru.m210projects.Witchaven.WHPLR.chunksofmeat;

import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Witchaven.Types.PLAYER;

public class AIKurt {

	public static void create() {
		enemy[KURTTYPE] = new Enemy();
		enemy[KURTTYPE].info = new EnemyInfo(35, 35, 1024 + 256, 120, 0, 48, false, 50, 0) {
			@Override
			public int getAttackDist(SPRITE spr) {
				int out = attackdist;
				switch (spr.picnum) {
				case KURTAT:
				case GONZOCSW:
				case GONZOCSWAT:
					if (spr.extra > 10)
						out = 2048 << 1;
					break;
				}

				return out;
			}
		};
		enemy[KURTTYPE].stand = new AIState() {
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

		enemy[KURTTYPE].nuked = new AIState() {
			@Override
			public void process(PLAYER plr, short i) {
				SPRITE spr = sprite[i];
				chunksofmeat(plr, i, spr.x, spr.y, spr.z, spr.sectnum, spr.ang);
				trailingsmoke(i, false);
				newstatus(i, DIE);
			}
		};

		enemy[KURTTYPE].chase = enemy[GONZOTYPE].chase;

		enemy[KURTTYPE].resurect = enemy[GONZOTYPE].resurect;

		enemy[KURTTYPE].skirmish = enemy[GONZOTYPE].skirmish;

		enemy[KURTTYPE].search = enemy[GONZOTYPE].search;

		enemy[KURTTYPE].frozen = enemy[GONZOTYPE].frozen;

		enemy[KURTTYPE].pain = enemy[GONZOTYPE].pain;

		enemy[KURTTYPE].face = enemy[GONZOTYPE].face;

		enemy[KURTTYPE].attack = enemy[GONZOTYPE].attack;

		enemy[KURTTYPE].cast = enemy[GONZOTYPE].cast;

		enemy[KURTTYPE].flee = enemy[GONZOTYPE].flee;

		enemy[KURTTYPE].die = enemy[GONZOTYPE].die;
	}

	public static final AIState kurtExplo = new AIState() {
		@Override
		public void process(PLAYER plr, short i) {
			SPRITE spr = sprite[i];
			spr.lotag -= TICSPERFRAME;
			spr.picnum++;
			if (spr.lotag < 0)
				spr.lotag = 12;

			short j = headspritesect[spr.sectnum];
			while (j != -1) {
				short nextj = nextspritesect[j];
				long dx = klabs(spr.x - sprite[j].x); // x distance to sprite
				long dy = klabs(spr.y - sprite[j].y); // y distance to sprite
				long dz = klabs((spr.z >> 8) - (sprite[j].z >> 8)); // z distance to sprite
				long dh = engine.getTile(sprite[j].picnum).getHeight() >> 1; // height of sprite
				if (dx + dy < PICKDISTANCE && dz - dh <= getPickHeight()) {
					if (sprite[j].detail == KURTTYPE) {
						sprite[j].hitag -= TICSPERFRAME << 4;
						if (sprite[j].hitag < 0) {
							newstatus(j, DIE);
						}
					}
				}
				j = nextj;
			}
		}
	};

	public static void premap(short i) {
		SPRITE spr = sprite[i];
		spr.detail = KURTTYPE;
		enemy[KURTTYPE].info.set(spr);
		engine.changespritestat(i, STAND);

		switch (spr.picnum) {
		case KURTSTAND:
			spr.extra = 20;
			break;
		case KURTKNEE:
			spr.extra = 10;
			break;
		}
	}
}
