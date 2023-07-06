package ru.m210projects.Witchaven.AI;

import static ru.m210projects.Build.Engine.sector;
import static ru.m210projects.Build.Engine.sprite;
import static ru.m210projects.Build.Engine.wall;
import static ru.m210projects.Build.Engine.zr_florhit;
import static ru.m210projects.Build.Pragmas.klabs;
import static ru.m210projects.Witchaven.AI.Ai.RATTYPE;
import static ru.m210projects.Witchaven.AI.Ai.aimove;
import static ru.m210projects.Witchaven.AI.Ai.checksector6;
import static ru.m210projects.Witchaven.AI.Ai.enemy;
import static ru.m210projects.Witchaven.AI.Ai.processfluid;
import static ru.m210projects.Witchaven.Globals.DIE;
import static ru.m210projects.Witchaven.Globals.FACE;
import static ru.m210projects.Witchaven.Globals.FLEE;
import static ru.m210projects.Witchaven.Globals.kHitIndexMask;
import static ru.m210projects.Witchaven.Globals.kHitSector;
import static ru.m210projects.Witchaven.Globals.kHitSprite;
import static ru.m210projects.Witchaven.Globals.kHitTypeMask;
import static ru.m210projects.Witchaven.Globals.kHitWall;
import static ru.m210projects.Witchaven.Globals.TICSPERFRAME;
import static ru.m210projects.Witchaven.Globals.kHitFloor;
import static ru.m210projects.Witchaven.Main.engine;
import static ru.m210projects.Witchaven.Names.*;
import static ru.m210projects.Witchaven.WHFX.warpsprite;
import static ru.m210projects.Witchaven.WHOBJ.newstatus;

import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Build.Types.WALL;
import ru.m210projects.Witchaven.Types.PLAYER;

public class AIRat {

	public static void create() {
		enemy[RATTYPE] = new Enemy();
		enemy[RATTYPE].info = new EnemyInfo(32, 32, 512, 120, 0, 32, false, 0, 0) {
			@Override
			public short getHealth(SPRITE spr)
			{
				return 10;
			}
		};
		enemy[RATTYPE].chase = new AIState() {
			@Override
			public void process(PLAYER plr, short i) {
				newstatus(i, FLEE);
			}
		};

		enemy[RATTYPE].search = new AIState() {
			@Override
			public void process(PLAYER plr, short i) {
				sprite[i].ang = (short) (((engine.krand() & 512 - 256) + sprite[i].ang + 1024) & 2047);
				newstatus(i, FLEE);
			}
		};

		enemy[RATTYPE].face = new AIState() {
			@Override
			public void process(PLAYER plr, short i) {
				SPRITE spr = sprite[i];
				spr.ang = engine.getangle(plr.x - spr.x, plr.y - spr.y);
				spr.ang = (short) (((engine.krand() & 512 - 256) + spr.ang + 1024) & 2047); // NEW
				spr.owner = sprite[plr.spritenum].owner;
				newstatus(i, FLEE);
			}
		};

		enemy[RATTYPE].die = new AIState() {
			@Override
			public void process(PLAYER plr, short i) {
				engine.deletesprite(i);
			}
		};

		enemy[RATTYPE].flee = new AIState() {
			@Override
			public void process(PLAYER plr, short i) {
				SPRITE spr = sprite[i];
				spr.lotag -= TICSPERFRAME;
				short osectnum = spr.sectnum;

				int movestat = aimove(i);
				if((movestat & kHitTypeMask) == kHitFloor)
				{
					spr.ang = (short) ((spr.ang + 1024) & 2047);
					return;
				}

				if ((movestat & kHitTypeMask) == kHitWall) {
					WALL wal = wall[movestat & kHitIndexMask];
					short wallang = (short) ((engine.getangle(wall[wal.point2].x - wal.x, wall[wal.point2].y - wal.y) + 512)
							& 2047);
					spr.ang = (short) (engine.krand() & 512 - 256 + wallang);
				}

				if ((movestat & kHitTypeMask) == kHitSprite) {
					SPRITE sp = sprite[movestat & kHitIndexMask];
					spr.owner = (short) (movestat & kHitIndexMask);
					spr.ang = engine.getangle(sp.x - spr.x, sp.y - spr.y);
					spr.ang = (short) (((engine.krand() & 512 - 256) + spr.ang + 1024) & 2047);
				}

				if (klabs(plr.x - spr.x) <= 1024 && klabs(plr.y - spr.y) <= 1024) {
					spr.owner = sprite[plr.spritenum].owner;
					newstatus(i, FACE);
				}

				if ((spr.sectnum != osectnum) && (sector[spr.sectnum].lotag == 10))
					warpsprite(i);


				if (checksector6(i))
					return;

				processfluid(i, zr_florhit, false);

				if ((zr_florhit & kHitTypeMask) == kHitSector && (sector[spr.sectnum].floorpicnum == LAVA
						|| sector[spr.sectnum].floorpicnum == LAVA2
						|| sector[spr.sectnum].floorpicnum == LAVA1 || sector[spr.sectnum].floorpicnum == ANILAVA)) {
					spr.hitag--;
					if (spr.hitag < 0)
						newstatus(i, DIE);
				}

				engine.setsprite(i, spr.x, spr.y, spr.z);
			}
		};
	}

	public static void premap(short i) {
		SPRITE spr = sprite[i];

		spr.detail = RATTYPE;
		enemy[RATTYPE].info.set(spr);
		engine.changespritestat(i, FACE);

		spr.shade = 12;
		spr.pal = 5;
	}
}
