package ru.m210projects.Witchaven.AI;

import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Build.Gameutils.*;
import static ru.m210projects.Build.Pragmas.*;
import static ru.m210projects.Witchaven.WH2MUS.*;
import static ru.m210projects.Witchaven.AI.AIDragon.dragonProcess;
import static ru.m210projects.Witchaven.AI.AIGoblin.goblinChill;
import static ru.m210projects.Witchaven.AI.AIGoblin.goblinWarProcess;
import static ru.m210projects.Witchaven.AI.AIGonzo.gonzoProcess;
import static ru.m210projects.Witchaven.AI.AIJudy.judyOperate;
import static ru.m210projects.Witchaven.AI.AISkeleton.skeletonChill;
import static ru.m210projects.Witchaven.AI.AIWillow.willowProcess;
import static ru.m210projects.Witchaven.Globals.*;
import static ru.m210projects.Witchaven.Main.*;
import static ru.m210projects.Witchaven.Names.*;
import static ru.m210projects.Witchaven.Items.*;
import static ru.m210projects.Witchaven.WH1Names.*;
import static ru.m210projects.Witchaven.WH2Names.*;
import static ru.m210projects.Witchaven.WHFX.makemonstersplash;
import static ru.m210projects.Witchaven.WHFX.warpsprite;
import static ru.m210projects.Witchaven.WHOBJ.CLIFFCLIP;
import static ru.m210projects.Witchaven.WHOBJ.getPickHeight;
import static ru.m210projects.Witchaven.WHOBJ.movesprite;
import static ru.m210projects.Witchaven.WHOBJ.newstatus;
import static ru.m210projects.Witchaven.WHOBJ.trailingsmoke;
import static ru.m210projects.Witchaven.WHPLR.*;
import static ru.m210projects.Witchaven.WHSND.S_BREATH1;
import static ru.m210projects.Witchaven.WHSND.S_DEMONTHROW;
import static ru.m210projects.Witchaven.WHSND.S_GENSWING;
import static ru.m210projects.Witchaven.WHSND.S_GORE1;
import static ru.m210projects.Witchaven.WHSND.S_KOBOLDHIT;
import static ru.m210projects.Witchaven.WHSND.S_PLRWEAPON2;
import static ru.m210projects.Witchaven.WHSND.S_RIP1;
import static ru.m210projects.Witchaven.WHSND.S_SOCK1;
import static ru.m210projects.Witchaven.WHSND.S_SWORD1;
import static ru.m210projects.Witchaven.WHSND.playsound_loc;
import static ru.m210projects.Witchaven.WHScreen.showmessage;
import static ru.m210projects.Witchaven.WHScreen.startredflash;
import static ru.m210projects.Witchaven.WHTAG.operatesector;
import static ru.m210projects.Witchaven.Weapons.droptheshield;

import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Witchaven.Types.PLAYER;

public class Ai {

	public static final int DEMONTYPE = 1; // ok
	public static final int DEVILTYPE = 2; // nuked ok, frozen no
	public static final int DRAGONTYPE = 3; // wh1
	public static final int FATWITCHTYPE = 4; // wh1
	public static final int FISHTYPE = 5;
	public static final int FREDTYPE = 6;
	public static final int GOBLINTYPE = 7; // wh1
	public static final int GONZOTYPE = 8; // freeze nuke ok
	public static final int GRONTYPE = 9; // ok
	public static final int GUARDIANTYPE = 10; // nuke ok
	public static final int IMPTYPE = 11; // freeze nuke ok
	public static final int JUDYTYPE = 12; // wh1
	public static final int KATIETYPE = 13; // ok
	public static final int KOBOLDTYPE = 14; // freeze nuke ok
	public static final int KURTTYPE = 15;
	public static final int MINOTAURTYPE = 16; // freeze nuke ok
	public static final int NEWGUYTYPE = 17; // freeze nuke ok
	public static final int RATTYPE = 18;
	public static final int SKELETONTYPE = 19; // freezee nuke ok
	public static final int SKULLYTYPE = 20; // wh1
	public static final int SPIDERTYPE = 21; // wh1
	public static final int WILLOWTYPE = 22; //nuke ok
	public static final int MAXTYPES = 23;

	public static final Enemy[] enemy = new Enemy[MAXTYPES];

	/*
	 * ai attack ai resurect from enemyInfo goblin imp etc patrol point search
	 *
	 *
	 * AMBUSH -> LAND
	 *
	 * case KOBOLD: case IMP: case MINOTAUR: case SKELETON: case GRONSW: case
	 * NEWGUY:
	 *
	 * PATROL
	 */

	static {
		if (game.WH2)
			AIImp.create();
		else
			AIGoblin.create();
		AIDevil.create();
		AISkeleton.create();
		AIDragon.create();
		AIKobold.create();
		AIGuardian.create();
		AIWillow.create();
		AIRat.create();
		AIFred.create();
		AIFish.create();
		AISpider.create();
		AIMinotaur.create();
		AIGron.create();
		AIFatwitch.create();
		AISkully.create();
		AIJudy.create();
		AIDemon.create();
		AIKatie.create();
		AINewguy.create();
		AIGonzo.create();
		AIKurt.create(); // kurt must be initialized after gonzo
	}

	public static void aiInit() {
		for (short i = 0; i < MAXSPRITES; i++) {
			if (sprite[i].statnum >= MAXSTATUS)
				continue;

			SPRITE spr = sprite[i];
			int pic = spr.picnum;
			switch (spr.picnum) {
			default:
				if (pic == SKELETON || pic == HANGMAN) {
					AISkeleton.premap(i);
					killcnt++;
				} else if (pic == GUARDIAN) {
					AIGuardian.premap(i);
					killcnt++;
				} else if (pic == WILLOW) {
					AIWillow.premap(i);
					killcnt++;
				} else if (pic == RAT) {
					AIRat.premap(i);
				} else if (pic == FISH) {
					AIFish.premap(i);
				} else if (pic == GRONHAL || pic == GRONMU || pic == GRONSW) {
					AIGron.premap(i);
					killcnt++;
				}
				break;
			case GOBLIN: // IMP
			case GOBLINSTAND:
			case GOBLINCHILL:
				killcnt++;
				if (game.WH2 && spr.picnum == IMP) {
					AIImp.premap(i);
					break;
				}

				if (!game.WH2)
					AIGoblin.premap(i);
				break;
			case DEVIL:
			case DEVILSTAND:
				if(sprite[i].pal != 8) {
					AIDevil.premap(i);
					killcnt++;
				}
				break;
			case DRAGON:
				AIDragon.premap(i);
				killcnt++;
				break;
			case KOBOLD:
				AIKobold.premap(i);
				killcnt++;
				break;
			case FRED:
			case FREDSTAND:
				AIFred.premap(i);
				killcnt++;
				break;
			case SPIDER:
				AISpider.premap(i);
				killcnt++;
				break;
			case MINOTAUR:
				AIMinotaur.premap(i);
				killcnt++;
				break;
			case FATWITCH:
				AIFatwitch.premap(i);
				killcnt++;
				break;
			case SKULLY:
				AISkully.premap(i);
				killcnt++;
				break;
			case JUDY:
			case JUDYSIT:
				AIJudy.premap(i);
				killcnt++;
				break;
			case DEMON:
				AIDemon.premap(i);
				killcnt++;
				break;
			case KATIE:
				AIKatie.premap(i);
				killcnt++;
				break;
			case KURTSTAND:
			case KURTKNEE:
				AIKurt.premap(i);
				killcnt++;
				break;
			case NEWGUYSTAND:
			case NEWGUYKNEE:
			case NEWGUYCAST:
			case NEWGUYBOW:
			case NEWGUYMACE:
			case NEWGUYPUNCH:
			case NEWGUY:
				AINewguy.premap(i);
				killcnt++;
				break;
			case KURTAT:
			case KURTPUNCH:
			case GONZOCSW:
			case GONZOGSW:
			case GONZOGHM:
			case GONZOGSH:
				AIGonzo.premap(i);
				killcnt++;
				break;
			}
		}
	}

	public static void aiProcess() {

		PLAYER plr = player[0];

//		short daang = (short) plr.ang;
//		int daz2 = (int) (100 - plr.horiz) * 2000;
//		engine.hitscan(plr.x, plr.y, plr.z, plr.sector, // Start position
//				sintable[(daang + 2560) & 2047], // X vector of 3D ang
//				sintable[(daang + 2048) & 2047], // Y vector of 3D ang
//				daz2, // Z vector of 3D ang
//				pHitInfo, CLIPMASK0);
//
//		if(pHitInfo.hitsprite != -1)
//		{
//			int sprid = pHitInfo.hitsprite;
//			System.err.println(sprite[sprid].statnum);
//		}

		judyOperate(plr);
		gonzoProcess(plr);
		goblinWarProcess(plr);
		dragonProcess(plr);
		willowProcess(plr);

		short i, nextsprite;

		for (i = headspritestat[PATROL]; i >= 0; i = nextsprite) {
			nextsprite = nextspritestat[i];

			SPRITE spr = sprite[i];
			short movestat = (short) movesprite(i, ((sintable[(spr.ang + 512) & 2047]) * TICSPERFRAME) << 3,
					((sintable[spr.ang]) * TICSPERFRAME) << 3, 0, 4 << 8, 4 << 8, 0);
			if (zr_florz > spr.z + (48 << 8)) {
				engine.setsprite(i, spr.x, spr.y, spr.z);
				movestat = 1;
			} else {
				spr.z = zr_florz;
			}
			short j = headspritesect[spr.sectnum];
			while (j != -1) {
				short nextj = nextspritesect[j];
				SPRITE tspr = sprite[j];
				if (tspr.picnum == PATROLPOINT) {
					long dx = klabs(spr.x - tspr.x); // x distance to sprite
					long dy = klabs(spr.y - tspr.y); // y distance to sprite
					long dz = klabs((spr.z >> 8) - (tspr.z >> 8)); // z distance to sprite
					int dh = engine.getTile(tspr.picnum).getHeight() >> 4; // height of sprite
					if (dx + dy < PICKDISTANCE && dz - dh <= getPickHeight()) {
						spr.ang = tspr.ang;
					}
				}
				j = nextj;
			}
			if (sintable[(spr.ang + 2560) & 2047] * (plr.x - spr.x)
					+ sintable[(spr.ang + 2048) & 2047] * (plr.y - spr.y) >= 0) {
				if (engine.cansee(plr.x, plr.y, plr.z, plr.sector, spr.x, spr.y, spr.z - (engine.getTile(spr.picnum).getHeight() << 7),
						spr.sectnum)) {
					newstatus(i, CHASE);
				}
			} else if (movestat != 0) {
				if ((movestat & 0xc000) == 32768) { // hit a wall
					actoruse(i);
				}
				newstatus(i, FINDME);
			}
		}

		for (i = headspritestat[CHASE]; i >= 0; i = nextsprite) {
			nextsprite = nextspritestat[i];
			SPRITE spr = sprite[i];
			if (enemy[spr.detail] != null && enemy[spr.detail].chase != null)
				enemy[spr.detail].chase.process(plr, i);
		}

		for (i = headspritestat[RESURECT]; i >= 0; i = nextsprite) {
			nextsprite = nextspritestat[i];
			SPRITE spr = sprite[i];
			if (enemy[spr.detail] != null && enemy[spr.detail].resurect != null) {
				enemy[spr.detail].resurect.process(plr, i);
			}
		}

		for (i = headspritestat[FINDME]; i >= 0; i = nextsprite) {
			nextsprite = nextspritestat[i];
			SPRITE spr = sprite[i];

			if (enemy[spr.detail] != null && enemy[spr.detail].search != null)
				enemy[spr.detail].search.process(plr, i);
		}

		for (i = headspritestat[NUKED]; i >= 0; i = nextsprite) {
			nextsprite = nextspritestat[i];
			SPRITE spr = sprite[i];

			if (spr.picnum == ZFIRE) {
				spr.lotag -= TICSPERFRAME;
				if (spr.lotag <= 0)
					engine.deletesprite(i);
			} else {
				if (enemy[spr.detail] != null && enemy[spr.detail].nuked != null)
					enemy[spr.detail].nuked.process(plr, i);
			}
		}

		for (i = headspritestat[FROZEN]; i >= 0; i = nextsprite) {
			nextsprite = nextspritestat[i];
			SPRITE spr = sprite[i];
			if (enemy[spr.detail] != null && enemy[spr.detail].frozen != null)
				enemy[spr.detail].frozen.process(plr, i);
		}

		for (i = headspritestat[PAIN]; i >= 0; i = nextsprite) {
			nextsprite = nextspritestat[i];
			SPRITE spr = sprite[i];

			if (enemy[spr.detail] != null && enemy[spr.detail].pain != null)
				enemy[spr.detail].pain.process(plr, i);
		}

		for (i = headspritestat[FACE]; i >= 0; i = nextsprite) {
			nextsprite = nextspritestat[i];
			SPRITE spr = sprite[i];

			if (enemy[spr.detail] != null && enemy[spr.detail].face != null)
				enemy[spr.detail].face.process(plr, i);
		}

		for (i = headspritestat[ATTACK]; i >= 0; i = nextsprite) {
			nextsprite = nextspritestat[i];
			SPRITE spr = sprite[i];

			if (game.WH2 && attacktheme == 0) {
				attacktheme = 1;
				startsong((engine.rand() % 2) + 2);
			}

			if (enemy[spr.detail] != null && enemy[spr.detail].attack != null)
				enemy[spr.detail].attack.process(plr, i);
		}

		for (i = headspritestat[FLEE]; i >= 0; i = nextsprite) {
			nextsprite = nextspritestat[i];
			SPRITE spr = sprite[i];

			if (enemy[spr.detail] != null && enemy[spr.detail].flee != null)
				enemy[spr.detail].flee.process(plr, i);
		}

		for (i = headspritestat[CAST]; i >= 0; i = nextsprite) {
			nextsprite = nextspritestat[i];
			SPRITE spr = sprite[i];

			if (enemy[spr.detail] != null && enemy[spr.detail].cast != null)
				enemy[spr.detail].cast.process(plr, i);
		}

		for (i = headspritestat[DIE]; i >= 0; i = nextsprite) {
			nextsprite = nextspritestat[i];
			SPRITE spr = sprite[i];
			if (enemy[spr.detail] != null && enemy[spr.detail].die != null)
				enemy[spr.detail].die.process(plr, i);
		}

		for (i = headspritestat[SKIRMISH]; i >= 0; i = nextsprite) {
			nextsprite = nextspritestat[i];
			SPRITE spr = sprite[i];

			if (enemy[spr.detail] != null && enemy[spr.detail].skirmish != null)
				enemy[spr.detail].skirmish.process(plr, i);
		}

		for (i = headspritestat[STAND]; i >= 0; i = nextsprite) {
			nextsprite = nextspritestat[i];
			SPRITE spr = sprite[i];

			if (enemy[spr.detail] != null && enemy[spr.detail].stand != null)
				enemy[spr.detail].stand.process(plr, i);
		}

		for (i = headspritestat[CHILL]; i >= 0; i = nextsprite) {
			nextsprite = nextspritestat[i];
			SPRITE spr = sprite[i];
			switch (spr.detail) {
			case GOBLINTYPE:
				goblinChill.process(plr, i);
				break;
			case SKELETONTYPE:
				skeletonChill.process(plr, i);
				break;
			}
		}

		for (i = headspritestat[DEAD]; i >= 0; i = nextsprite) {
			nextsprite = nextspritestat[i];
			SPRITE spr = sprite[i];

			engine.getzrange(spr.x, spr.y, spr.z - 1, spr.sectnum, (spr.clipdist) << 2, CLIPMASK0);
			switch (checkfluid(i, zr_florhit)) {
			case TYPELAVA:
			case TYPEWATER:
				spr.z = zr_florz + (engine.getTile(spr.picnum).getHeight() << 5);
				break;
			}
		}
	}

	public static int aimove(short i) {
		int ox = sprite[i].x;
		int oy = sprite[i].y;
		int oz = sprite[i].z;
//		short osect = sprite[i].sectnum;

		int movestate = movesprite(i, ((sintable[(sprite[i].ang + 512) & 2047]) * TICSPERFRAME) << 3,
				((sintable[sprite[i].ang & 2047]) * TICSPERFRAME) << 3, 0, 4 << 8, 4 << 8, CLIFFCLIP);

		if (((zr_florz - oz) >> 4) > engine.getTile(sprite[i].picnum).getHeight() + sprite[i].yrepeat << 2
				|| (movestate & kHitTypeMask) == kHitWall) {
//			engine.changespritesect(i, osect);
//			engine.setsprite(i, ox + mulscale((sprite[i].clipdist) << 2, sintable[(sprite[i].ang + 1536) & 2047], 16),
//					oy + mulscale((sprite[i].clipdist) << 2, sintable[(sprite[i].ang + 1024) & 2047], 16), oz);

			engine.setsprite(i, ox, oy, oz);

			if ((movestate & kHitTypeMask) != kHitWall) {
				if (game.WH2)
					sprite[i].z += WH2GRAVITYCONSTANT;
				else
					sprite[i].z += GRAVITYCONSTANT;
				return 16384 | zr_florhit;
			}
		}

		sprite[i].z = zr_florz;

		return movestate;
	}

	public static int aifly(short i) {
		SPRITE spr = sprite[i];
		int movestate = movesprite(i, ((sintable[(sprite[i].ang + 512) & 2047]) * TICSPERFRAME) << 3,
				((sintable[sprite[i].ang & 2047]) * TICSPERFRAME) << 3, 0, 4 << 8, 4 << 8, CLIFFCLIP);

		spr.z -= TICSPERFRAME << 8;
		short ocs = spr.cstat;
		spr.cstat = 0;
		engine.getzrange(spr.x, spr.y, spr.z - 1, spr.sectnum, (spr.clipdist) << 2, CLIPMASK0);
		spr.cstat = ocs;
		if(spr.z > zr_florz)
			spr.z = zr_florz;
		if(spr.z - (engine.getTile(spr.picnum).getHeight() << 7) < zr_ceilz)
			spr.z = zr_ceilz + (engine.getTile(spr.picnum).getHeight() << 7);

		return movestate;
	}

	public static void aisearch(PLAYER plr, short i, boolean fly) {
		SPRITE spr = sprite[i];
		spr.lotag -= TICSPERFRAME;

//		if (plr.invisibletime > 0) {
//			newstatus(i, FACE);
//			return;
//		}

		short osectnum = spr.sectnum;

		int movestat;
		if (fly)
			movestat = aifly(i);
		else
			movestat = aimove(i);

		if (checkdist(plr, i)) {
			if (plr.shadowtime > 0) {
				spr.ang = (short) (((engine.krand() & 512 - 256) + spr.ang + 1024) & 2047); // NEW
				newstatus(i, FLEE);
			} else
				newstatus(i, ATTACK);
			return;
		}

		if (movestat != 0) {
			if (engine.cansee(plr.x, plr.y, plr.z, plr.sector, spr.x, spr.y, spr.z - (engine.getTile(spr.picnum).getHeight() << 7),
					spr.sectnum) && spr.lotag < 0) {
				spr.ang = (short) ((spr.ang + 1024) & 2047);
				newstatus(i, FLEE);
				return;
			}
			if (spr.lotag < 0) {
				if (engine.krand() % 100 > 50)
					spr.ang = (short) ((spr.ang + 512) & 2047);
				else
					spr.ang = (short) ((spr.ang + 1024) & 2047);

				spr.lotag = 30;
			} else {
				spr.ang += (TICSPERFRAME << 4) & 2047;
			}
		}

		if (engine.cansee(plr.x, plr.y, plr.z, plr.sector, spr.x, spr.y, spr.z - (engine.getTile(spr.picnum).getHeight() << 7),
				spr.sectnum) && movestat == 0 && spr.lotag < 0) {
			newstatus(i, FACE);
			return;
		}

		if ((spr.sectnum != osectnum) && (sector[spr.sectnum].lotag == 10))
			warpsprite(i);

		processfluid(i, zr_florhit, fly);

		engine.setsprite(i, spr.x, spr.y, spr.z);
	}

	public static boolean checksector6(short i) {
		SPRITE spr = sprite[i];
		if (sector[spr.sectnum].floorz - (32 << 8) < sector[spr.sectnum].ceilingz) {
			if (sector[spr.sectnum].lotag == 6)
				newstatus(i, DIE);
			else {
				engine.deletesprite(i);
				return true;
			}
		}

		return false;
	}

	public static final int TYPENONE = 0;
	public static final int TYPEWATER = 1;
	public static final int TYPELAVA = 2;

	public static int checkfluid(int i, int zr_florhit) {
		SPRITE spr = sprite[i];
		if (isValidSector(spr.sectnum) && (zr_florhit & kHitTypeMask) == kHitSector && (sector[spr.sectnum].floorpicnum == WATER
				/* || sector[spr.sectnum].floorpicnum == LAVA2 */ || sector[spr.sectnum].floorpicnum == LAVA
				|| sector[spr.sectnum].floorpicnum == SLIME || sector[spr.sectnum].floorpicnum == FLOORMIRROR
		/*
		 * || sector[spr.sectnum].floorpicnum == LAVA1 ||
		 * sector[spr.sectnum].floorpicnum == ANILAVA
		 */)) {
			if (sector[spr.sectnum].floorpicnum == WATER || sector[spr.sectnum].floorpicnum == SLIME
					|| sector[spr.sectnum].floorpicnum == FLOORMIRROR) {
				return TYPEWATER;
			} else {
				return TYPELAVA;
			}
		}

		return TYPENONE;
	}

	public static void processfluid(int i, int zr_florhit, boolean fly) {
		SPRITE spr = sprite[i];
		switch (checkfluid(i, zr_florhit)) {
		case TYPELAVA:
			if (!fly) {
				spr.z += engine.getTile(spr.picnum).getHeight() << 5;
				trailingsmoke(i, true);
				makemonstersplash(LAVASPLASH, i);
			}
			break;
		case TYPEWATER:
			if (!fly) {
				spr.z += engine.getTile(spr.picnum).getHeight() << 5;
				if (engine.krand() % 100 > 60)
					makemonstersplash(SPLASHAROO, i);
			}
			break;
		}
	}

	public static void castspell(PLAYER plr, int i) {
		int j = engine.insertsprite(sprite[i].sectnum, MISSILE);

		sprite[j].x = sprite[i].x;
		sprite[j].y = sprite[i].y;
		if (game.WH2 || sprite[i].picnum == SPAWNFIREBALL)
			sprite[j].z = sprite[i].z - ((engine.getTile(sprite[i].picnum).getHeight() >> 1) << 8);
		else
			sprite[j].z = engine.getflorzofslope(sprite[i].sectnum, sprite[i].x, sprite[i].y) - ((engine.getTile(sprite[i].picnum).getHeight() >> 1) << 8);
		sprite[j].cstat = 0; // Hitscan does not hit other bullets
		sprite[j].picnum = MONSTERBALL;
		sprite[j].shade = -15;
		sprite[j].xrepeat = 64;
		sprite[j].yrepeat = 64;
		if (sprite[i].picnum == SPAWNFIREBALL)
			sprite[j].ang = (short) ((engine.getangle(plr.x - sprite[j].x, plr.y - sprite[j].y) + 2048) & 2047);
		else
			sprite[j].ang = (short) (((engine.getangle(plr.x - sprite[j].x, plr.y - sprite[j].y) + (engine.krand() & 15)
					- 8) + 2048) & 2047);
		sprite[j].xvel = (short) (sintable[(sprite[j].ang + 2560) & 2047] >> 6);
		sprite[j].yvel = (short) (sintable[(sprite[j].ang + 2048) & 2047] >> 6);

		int discrim = engine
				.ksqrt((plr.x - sprite[j].x) * (plr.x - sprite[j].x) + (plr.y - sprite[j].y) * (plr.y - sprite[j].y));
		if (discrim == 0)
			discrim = 1;
		if (game.WH2)
			sprite[j].zvel = (short) (((plr.z + (8 << 8) - sprite[j].z) << 7) / discrim);
		else
			sprite[j].zvel = (short) (((plr.z + (48 << 8) - sprite[j].z) << 7) / discrim);

		sprite[j].owner = (short) i;
		sprite[j].clipdist = 16;
		sprite[j].lotag = 512;
		sprite[j].hitag = 0;

		game.pInt.setsprinterpolate(j, sprite[j]);
	}

	public static void skullycastspell(PLAYER plr, int i) {
		int j = engine.insertsprite(sprite[i].sectnum, MISSILE);

		sprite[j].x = sprite[i].x;
		sprite[j].y = sprite[i].y;
		if (sprite[i].picnum == SPAWNFIREBALL)
			sprite[j].z = sprite[i].z - ((engine.getTile(sprite[i].picnum).getHeight() >> 1) << 8);
		else
			sprite[j].z = engine.getflorzofslope(sprite[i].sectnum, sprite[i].x, sprite[i].y) - ((engine.getTile(sprite[i].picnum).getHeight() >> 1) << 8);
		sprite[j].cstat = 0; // Hitscan does not hit other bullets
		sprite[j].picnum = PLASMA;
		sprite[j].shade = -15;
		sprite[j].xrepeat = 64;
		sprite[j].yrepeat = 64;
		if (sprite[i].picnum == SPAWNFIREBALL)
			sprite[j].ang = (short) ((engine.getangle(plr.x - sprite[j].x, plr.y - sprite[j].y) + 2048) & 2047);
		else
			sprite[j].ang = (short) (((engine.getangle(plr.x - sprite[j].x, plr.y - sprite[j].y) + (engine.krand() & 15)
					- 8) + 2048) & 2047);
		sprite[j].xvel = (short) (sintable[(sprite[j].ang + 2560) & 2047] >> 6);
		sprite[j].yvel = (short) (sintable[(sprite[j].ang + 2048) & 2047] >> 6);

		long discrim = engine
				.ksqrt((plr.x - sprite[j].x) * (plr.x - sprite[j].x) + (plr.y - sprite[j].y) * (plr.y - sprite[j].y));
		if (discrim == 0)
			discrim = 1;
		sprite[j].zvel = (short) (((plr.z + (48 << 8) - sprite[j].z) << 7) / discrim);

		sprite[j].owner = (short) i;
		sprite[j].clipdist = 16;
		sprite[j].lotag = 512;
		sprite[j].hitag = 0;
		sprite[j].pal = 7;

		game.pInt.setsprinterpolate(j, sprite[j]);
	}

	public static void attack(PLAYER plr, int i) {
		int s = 0;
		if (plr.invincibletime > 0 || plr.godMode)
			return;

		if (plr.treasure[TADAMANTINERING] == 1 && (engine.krand() & 1) != 0)
			return;

//		if ((engine.krand() & (15 < plr.armortype ? 11 : 10)) != 0)
//			return;

		if (!droptheshield && plr.shieldpoints > 0 && plr.selectedgun > 0 && plr.selectedgun < 5) {
			short a = engine.getangle(sprite[i].x - plr.x, sprite[i].y - plr.y);
			if ((a < plr.ang && plr.ang - a < 128) || (a > plr.ang && (((short) plr.ang + a) & 2047) < 128)) {
				if (engine.krand() % 100 > 80) {
					playsound_loc(S_SWORD1 + engine.krand() % 3, plr.x, plr.y);
					return;
				} else {
					s = engine.krand() % 50;
					plr.shieldpoints -= s;
					if (engine.krand() % 100 > 50) {
						playsound_loc(S_SWORD1 + engine.krand() % 3, plr.x, plr.y);
						return;
					}
				}
			}
			if (plr.shieldpoints <= 0) {
				showmessage("Shield useless", 360);
			}
		}

		int k = 5;
		if (!game.WH2) {
			k = engine.krand() % 100;
			if (k > (plr.armortype << 3))
				k = 15;
			else
				k = 5;
		}

		switch (sprite[i].detail) {
		case SPIDER:
			k = 5;
			break;
		case FISHTYPE:
		case RATTYPE:
			k = 3;
			break;
		case SKELETONTYPE:
			playsound_loc(S_RIP1 + (engine.krand() % 3), sprite[i].x, sprite[i].y);
			if ((engine.krand() % 2) != 0)
				playsound_loc(S_GORE1 + (engine.krand() % 4), sprite[i].x, sprite[i].y);
			if ((engine.krand() % 2) != 0)
				playsound_loc(S_BREATH1 + (engine.krand() % 6), sprite[i].x, sprite[i].y);

			if (game.WH2)
				k = (engine.krand() % 5) + 5;
			else
				k >>= 2;
			break;
		case KATIETYPE: // damage 5 - 50
			playsound_loc(S_DEMONTHROW, sprite[i].x, sprite[i].y);
			k = (engine.krand() % 45) + 5;
			break;

		case DEVILTYPE:
			playsound_loc(S_DEMONTHROW, sprite[i].x, sprite[i].y);
			if (!game.WH2)
				k >>= 2;
			break;

		case KOBOLDTYPE:
			playsound_loc(S_GENSWING, sprite[i].x, sprite[i].y);
			if ((engine.krand() % 10) > 4) {
				playsound_loc(S_KOBOLDHIT, plr.x, plr.y);
				playsound_loc(S_BREATH1 + (engine.krand() % 6), plr.x, plr.y);
			}
			if (game.WH2)
				k = (engine.krand() % 5) + 5;
			else
				k >>= 2;
			break;
		case FREDTYPE:

			/* Sounds for Fred (currently copied from Goblin) */
			playsound_loc(S_GENSWING, sprite[i].x, sprite[i].y);
			if (engine.rand() % 10 > 4)
				playsound_loc(S_SWORD1 + (engine.rand() % 6), sprite[i].x, sprite[i].y);

			k >>= 3;
			break;
		case IMPTYPE:
			if (!game.WH2)
				break;
			playsound_loc(S_RIP1 + (engine.krand() % 3), sprite[i].x, sprite[i].y);
			if ((engine.krand() % 2) != 0) {
				playsound_loc(S_GORE1 + (engine.krand() % 4), sprite[i].x, sprite[i].y);
			}
			if ((engine.krand() % 2) != 0) {
				playsound_loc(S_BREATH1 + (engine.krand() % 6), sprite[i].x, sprite[i].y);
			}

			k = (engine.krand() % 5) + 5;
			if (k > 8) {
				plr.poisoned = 1;
			}
			break;
		case GOBLINTYPE:
			if (game.WH2)
				break;

			playsound_loc(S_GENSWING, sprite[i].x, sprite[i].y);
			if ((engine.krand() % 10) > 4)
				playsound_loc(S_SWORD1 + (engine.krand() % 6), sprite[i].x, sprite[i].y);
			k >>= 2;
			break;
		case NEWGUYTYPE:
			if (sprite[i].picnum == NEWGUYMACE) { // damage 5 - 20
				playsound_loc(S_PLRWEAPON2, sprite[i].x, sprite[i].y);
				if (engine.krand() % 10 > 4) {
					playsound_loc(S_KOBOLDHIT, plr.x, plr.y);
					playsound_loc(S_BREATH1 + (engine.krand() % 6), plr.x, plr.y);
				}
				k = (engine.krand() % 15) + 5;
				break;
			}
		case KURTTYPE:
		case GONZOTYPE:
			playsound_loc(S_GENSWING, sprite[i].x, sprite[i].y);
			if (sprite[i].picnum == GONZOCSWAT || sprite[i].picnum == GONZOGSWAT) { // damage 5 - 15
				if (engine.krand() % 10 > 6)
					playsound_loc(S_SWORD1 + (engine.krand() % 6), sprite[i].x, sprite[i].y);
				k = (engine.krand() % 15) + 5;
			} else if (sprite[i].picnum == GONZOGHMAT) { // damage 5 - 15
				if (engine.krand() % 10 > 6)
					playsound_loc(S_SWORD1 + (engine.krand() % 6), sprite[i].x, sprite[i].y);
				k = (engine.krand() % 10) + 5;
			} else if (sprite[i].picnum == GONZOGSHAT) { // damage 5 - 20
				if (engine.krand() % 10 > 3)
					playsound_loc(S_SWORD1 + (engine.krand() % 6), sprite[i].x, sprite[i].y);
				k = (engine.krand() % 15) + 5;
			} else if (sprite[i].picnum == KURTAT) { // damage 5 - 15
				playsound_loc(S_GENSWING, sprite[i].x, sprite[i].y);
				if (engine.krand() % 10 > 3) {
					playsound_loc(S_SWORD1 + (engine.krand() % 6), sprite[i].x, sprite[i].y);
				}
				k = (engine.krand() % 10) + 5;
			} else {
				playsound_loc(S_GENSWING, sprite[i].x, sprite[i].y);
				if (engine.krand() % 10 > 4) {
					playsound_loc(S_SOCK1 + (engine.krand() % 4), plr.x, plr.y);
					playsound_loc(S_BREATH1 + (engine.krand() % 6), plr.x, plr.y);
				}
				k = (engine.krand() % 4) + 1;
			}
			break;

		case GRONTYPE:
			if (sprite[i].picnum != GRONSWATTACK)
				break;

			if (game.WH2) {
				k = (engine.krand() % 20) + 5;
				if (sprite[i].shade > 30) {
					k += engine.krand() % 10;
				}
			} else {
				if (sprite[i].shade > 30)
					k >>= 1;
			}
			playsound_loc(S_GENSWING, sprite[i].x, sprite[i].y);
			if ((engine.krand() % 10) > 3)
				playsound_loc(S_SWORD1 + (engine.krand() % 6), sprite[i].x, sprite[i].y);

			break;
		case MINOTAURTYPE:
			playsound_loc(S_GENSWING, sprite[i].x, sprite[i].y);
			if (engine.krand() % 10 > 4)
				playsound_loc(S_SWORD1 + (engine.krand() % 6), sprite[i].x, sprite[i].y);
			if (game.WH2)
				k = (engine.krand() % 25) + 5;
			break;
		}

		if (plr.shieldpoints > 0) {
			if (s > k)
				k = 0;
			else
				k -= s;
		}

		int a;
		switch (plr.armortype) {
		case 0: // none
			addhealth(plr, -k);
			break;
		case 1: // leather
			a = engine.krand() % 5;
			if (a > k) {
				k = 0;
			} else {
				k -= a;
			}
			addarmor(plr, -a);
			addhealth(plr, -k);
			break;
		case 2: // chain
			a = engine.krand() % 10;
			if (a > k) {
				k = 0;
			} else {
				k -= a;
			}
			addarmor(plr, -a);
			addhealth(plr, -k);
			break;
		case 3: // plate
			a = engine.krand() % 20;
			if (a > k) {
				k = 0;
			} else {
				k -= a;
			}
			addarmor(plr, -a);
			addhealth(plr, -k);
			break;
		}

		startredflash(3 * k);

		if (k == 0)
			k = 1;

		k = engine.krand() % k;

		damage_angvel += k << 3;
		damage_svel += k << 3;
		damage_vel -= k << 3;

		plr.hvel += k << 2;
	}

	public static int checkmove(short i, int dax, int day) {
		int movestat = movesprite(i, dax, day, 0, 4 << 8, 4 << 8, CLIFFCLIP);

		if (movestat != 0)
			sprite[i].ang = (short) ((sprite[i].ang + TICSPERFRAME) & 2047);

		return movestat;
	}

	public static boolean checkdist(PLAYER plr, int i) {
		if(plr.invisibletime > 0 || plr.health <= 0)
			return false;

		return checkdist(i, plr.x, plr.y, plr.z);
	}

	public static boolean checkdist(int i, int x, int y, int z) {
		SPRITE spr = sprite[i];

		int attackdist = 512;
		int attackheight = 120;
		if (spr.detail > 0) {
			attackdist = enemy[spr.detail].info.getAttackDist(spr);
			attackheight = enemy[spr.detail].info.attackheight;
		}

		switch (spr.picnum) {
		case LFIRE:
		case SFIRE:
			attackdist = 1024;
			break;
		}

		if ((klabs(x - spr.x) + klabs(y - spr.y) < attackdist)
				&& (klabs((z >> 8) - ((spr.z >> 8) - (engine.getTile(spr.picnum).getHeight() >> 1))) <= attackheight))
			return true;

		return false;
	}

	public static int checksight_ang = 0;

	public static boolean checksight(PLAYER plr, int i) {
		if (plr.invisibletime > 0) {
			checksight_ang = ((engine.krand() & 512) - 256) & 2047;
			return false;
		}

		if (engine.cansee(plr.x, plr.y, plr.z, plr.sector, sprite[i].x, sprite[i].y,
				sprite[i].z - (engine.getTile(sprite[i].picnum).getHeight() << 7), sprite[i].sectnum) && plr.invisibletime < 0) {
			checksight_ang = (engine.getangle(plr.x - sprite[i].x, plr.y - sprite[i].y) & 2047);
			if (((sprite[i].ang + 2048 - checksight_ang) & 2047) < 1024)
				sprite[i].ang = (short) ((sprite[i].ang + 2048 - (TICSPERFRAME << 1)) & 2047);
			else
				sprite[i].ang = (short) ((sprite[i].ang + (TICSPERFRAME << 1)) & 2047);

			return true;
		} else
			checksight_ang = 0;

		return false;
	}

	public static void monsterweapon(int i) {

		if (sprite[i].shade > 20)
			return;

		if (sprite[i].picnum == SKELETONDEAD || sprite[i].picnum == KOBOLDDEAD)
			return;

		if ((engine.krand() % 100) < 75)
			return;

		int j = engine.insertsprite(sprite[i].sectnum, (short) 0);

		SPRITE weap = sprite[j];
		weap.x = sprite[i].x;
		weap.y = sprite[i].y;
		weap.z = sprite[i].z - (24 << 8);
		weap.shade = -15;
		weap.cstat = 0;
		weap.cstat &= ~3;
		weap.pal = 0;

		int type = (engine.krand() % 4);
		weap.picnum = (short) (FLASKBLUE + type);
		weap.detail = (short) (FLASKBLUETYPE + type);
		weap.xrepeat = 25;
		weap.yrepeat = 20;

		game.pInt.setsprinterpolate(j, weap);// XXX

		switch (sprite[i].picnum) {
		case NEWGUYDEAD:
			weap.xrepeat = 25;
			weap.yrepeat = 20;
			if (weap.extra < 20) {
				weap.picnum = WEAPON2;
				weap.detail = WEAPON2TYPE;
			} else {
				weap.picnum = QUIVER;
				weap.detail = QUIVERTYPE;
			}

			weap.pal = 0;
			break;

		case MINOTAURDEAD:
			weap.xrepeat = 25;
			weap.yrepeat = 20;
			if (!game.WH2) {
				if (engine.krand() % 100 > 50) {
					weap.picnum = WEAPON4;
				} else {
					weap.xrepeat = 20;
					weap.yrepeat = 15;
					weap.picnum = WEAPON6;
					weap.detail = WEAPON6TYPE;
				}
			} else {
				weap.picnum = WEAPON4;
				weap.detail = WEAPON4TYPE;
			}
			break;

		case GONZOBSHDEAD:
			weap.picnum = GONZOBSHIELD;
			weap.detail = GONZOSHIELDTYPE;
			weap.xrepeat = 12;
			weap.yrepeat = 12;
			break;

		case GONZOCSWDEAD:
			if (weap.extra > 10) {
				weap.picnum = WEAPON6;
				weap.detail = WEAPON6TYPE;
				weap.xrepeat = 25;
				weap.yrepeat = 20;
			} else if (weap.extra > 0) {
				weap.picnum = GOBWEAPON;
				weap.detail = GOBWEAPONTYPE;
				weap.xrepeat = 25;
				weap.yrepeat = 20;
			} else {
				weap.picnum = WEAPON1;
				weap.detail = WEAPON1TYPE;
				weap.xrepeat = 25;
				weap.yrepeat = 20;
			}
			break;
		case GONZOCSHDEAD:
			weap.picnum = GONZOCSHIELD;
			weap.detail = GONZOSHIELDTYPE;
			weap.xrepeat = 12;
			weap.yrepeat = 12;
			break;

		case GONZOGSWDEAD:
			weap.picnum = WEAPON8;
			weap.detail = WEAPON8TYPE;
			weap.xrepeat = 25;
			weap.yrepeat = 20;
			break;
		case GONZOGHMDEAD:
			weap.picnum = PLATEARMOR;
			weap.detail = PLATEARMORTYPE;
			weap.xrepeat = 26;
			weap.yrepeat = 26;
			break;
		case GONZOGSHDEAD:
			weap.picnum = GONZOGSHIELD;
			weap.detail = GONZOSHIELDTYPE;
			weap.xrepeat = 12;
			weap.yrepeat = 12;
			break;
		case GOBLINDEAD:
			weap.xrepeat = 16;
			weap.yrepeat = 16;
			weap.picnum = GOBWEAPON;
			weap.detail = GOBWEAPONTYPE;
			break;
		default:
			if (sprite[i].picnum == GRONDEAD) {
				if (netgame) {
					weap.x = sprite[i].x;
					weap.y = sprite[i].y;
					weap.z = sprite[i].z - (24 << 8);
					weap.shade = -15;
					weap.cstat = 0;
					weap.cstat &= ~3;
					weap.xrepeat = 25;
					weap.yrepeat = 20;
					int k = engine.krand() % 4;
					switch (k) {
					case 0:
						weap.picnum = WEAPON3;
						weap.detail = WEAPON3TYPE;
						weap.xrepeat = 25;
						weap.yrepeat = 20;
						break;
					case 1:
						weap.picnum = WEAPON5;
						weap.detail = WEAPON5TYPE;
						weap.xrepeat = 25;
						weap.yrepeat = 20;
						break;
					case 2:
						weap.picnum = WEAPON6;
						weap.detail = WEAPON6TYPE;
						weap.xrepeat = 20;
						weap.yrepeat = 15;
						break;
					case 3:
						weap.picnum = SHIELD;
						weap.detail = SHIELDTYPE;
						weap.xrepeat = 32;
						weap.yrepeat = 32;
						break;
					}
				} else {
					switch (weap.pal) {
					case 0:
						weap.picnum = WEAPON3;
						weap.detail = WEAPON3TYPE;
						weap.xrepeat = 25;
						weap.yrepeat = 20;
						break;
					case 10:
						weap.picnum = WEAPON5;
						weap.detail = WEAPON5TYPE;
						weap.xrepeat = 25;
						weap.yrepeat = 20;
						break;
					case 11:
						weap.picnum = WEAPON6;
						weap.detail = WEAPON6TYPE;
						weap.xrepeat = 20;
						weap.yrepeat = 15;
						break;
					case 12:
						weap.picnum = SHIELD;
						weap.detail = SHIELDTYPE;
						weap.xrepeat = 32;
						weap.yrepeat = 32;
						break;
					}
				}
				weap.pal = 0;
				break;
			}
			treasurescnt++;
			break;
		}
	}

	public static PLAYER aiGetPlayerTarget(short i) {
		if (sprite[i].owner >= 0 && sprite[i].owner < MAXSPRITES) {
			int playernum = sprite[sprite[i].owner].owner;
			if (playernum >= 4096)
				return player[playernum - 4096];
		}

		return null;
	}

	public static boolean actoruse(short i) {
		SPRITE spr = sprite[i];

		engine.neartag(spr.x, spr.y, spr.z, spr.sectnum, spr.ang, neartag, 1024, 3);

		if (neartag.tagsector >= 0) {
			if (sector[neartag.tagsector].hitag == 0) {
				if (sector[neartag.tagsector].floorz != sector[neartag.tagsector].ceilingz) {
					operatesector(player[pyrn], neartag.tagsector);
					return true;
				}
			}
		}

		return false;
	}

	public static int findplayer() {
		return 0;
	}
}
