package ru.m210projects.Witchaven;

import static ru.m210projects.Witchaven.Globals.*;
import static ru.m210projects.Witchaven.AI.Ai.*;
import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Build.Net.Mmulti.*;
import static ru.m210projects.Witchaven.WHScreen.*;
import static ru.m210projects.Witchaven.Main.*;
import static ru.m210projects.Witchaven.WHSND.*;
import static ru.m210projects.Witchaven.WHOBJ.*;
import static ru.m210projects.Witchaven.Names.*;
import static ru.m210projects.Witchaven.WH1Names.*;
import static ru.m210projects.Witchaven.WH2Names.*;
import static ru.m210projects.Witchaven.WHTAG.*;
import static ru.m210projects.Witchaven.Weapons.*;
import static ru.m210projects.Witchaven.Potions.*;
import static ru.m210projects.Build.Pragmas.*;

import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Build.Types.WALL;
import ru.m210projects.Witchaven.Types.PLAYER;
import ru.m210projects.Witchaven.Types.PLOCATION;

public class WHPLR {

	public static PLAYER player[] = new PLAYER[MAXPLAYERS];
	public static PLOCATION gPrevPlayerLoc[] = new PLOCATION[MAXPLAYERS];

	public static short[] monsterangle = new short[MAXSPRITESONSCREEN],
			monsterlist = new short[MAXSPRITESONSCREEN];
	public static int shootgunzvel;

	public static boolean justteleported;

	public static int victor = 0;
	public static int autohoriz = 0; // XXX NOT FOR MULTIPLAYER

	public static int pyrn;
	public static int mapon;
	public static int damage_vel, damage_svel, damage_angvel;

	public static void viewBackupPlayerLoc( int nPlayer )
	{
		SPRITE pSprite = sprite[player[nPlayer].spritenum];
		PLOCATION pPLocation = gPrevPlayerLoc[nPlayer];
		pPLocation.x = pSprite.x;
		pPLocation.y = pSprite.y;
		pPLocation.z = player[nPlayer].z;
		pPLocation.ang = player[nPlayer].ang;
		pPLocation.horiz = player[nPlayer].horiz + player[nPlayer].jumphoriz;
	}

	public static void playerdead(PLAYER plr) {
		if (plr.dead)
			return;

		if (plr.potion[0] > 0 && plr.spiked == 0) {
			int i = plr.currentpotion;
			plr.currentpotion = 0;
			usapotion(plr);
			plr.currentpotion = i;
			return;
		}

		plr.currspikeframe = 0;

		if (plr.spiked == 1) {
			plr.spiketics = spikeanimtics[0].daweapontics;
			playsound_loc(S_GORE1, plr.x, plr.y);
			SND_Sound(S_HEARTBEAT);
		}

		SND_Sound(S_PLRDIE1);

//	 	netsendmove();
		plr.dead = true;
	}

	public static void initplayersprite(PLAYER plr) {
		if (difficulty > 1) {
			plr.currweapon = 1;
			plr.selectedgun = 1;
		} else {
			plr.currweapon = 4;
			plr.selectedgun = 4;
		}
		plr.currentpotion = 0;
		plr.helmettime = -1;
		plr.shadowtime = -1;
		plr.nightglowtime = -1;
		plr.strongtime = -1;
		plr.invisibletime = -1;
		plr.manatime = -1;
		plr.currentorb = 0;
		plr.currweaponfired = 3;
		plr.currweaponanim = 0;
		plr.currweaponattackstyle = 0;
		plr.currweaponflip = 0;

		plr.vampiretime = 0;
		plr.shieldpoints = 0;
		plr.shieldtype = 0;
		plr.dead = false;
		plr.spiked = 0;
		plr.shockme = -1;
		plr.poisoned = 0;
		plr.poisontime = -1;

		plr.oldsector = plr.sector;
		plr.horiz = 100;
		plr.height = getPlayerHeight();
		plr.z = sector[plr.sector].floorz - (plr.height << 8);

		plr.spritenum = engine.insertsprite(plr.sector, (short) 0);

		plr.onsomething = 1;

		sprite[plr.spritenum].x = plr.x;
		sprite[plr.spritenum].y = plr.y;
		sprite[plr.spritenum].z = plr.z + (plr.height << 8);
		sprite[plr.spritenum].cstat = 1 + 256;
		sprite[plr.spritenum].picnum = game.WH2 ? GRONSW : FRED;
		sprite[plr.spritenum].shade = 0;
		sprite[plr.spritenum].xrepeat = 36;
		sprite[plr.spritenum].yrepeat = 36;
		sprite[plr.spritenum].ang = (short) plr.ang;
		sprite[plr.spritenum].xvel = 0;
		sprite[plr.spritenum].yvel = 0;
		sprite[plr.spritenum].zvel = 0;
		sprite[plr.spritenum].owner = (short) (4096 + myconnectindex);
		sprite[plr.spritenum].lotag = 0;
		sprite[plr.spritenum].hitag = 0;
		sprite[plr.spritenum].pal = (short) (game.WH2 ? 10 : 1);
		if(game.WH2)
			sprite[plr.spritenum].clipdist = 48;

		plr.selectedgun = 0;

		if(game.WH2) {
			for (int i = 0; i <= 9; i++) {
				if (i < 5) {
					plr.ammo[i] = 40;
					plr.weapon[i] = 1;
				} else {
					plr.ammo[i] = 0;
					plr.weapon[i] = 0;
				}
				if (i < 8) {
					plr.orb[i] = 0;
					plr.orbammo[i] = 0;
				}
			}

			if (difficulty > 1) {
				plr.weapon[0] = plr.weapon[1] = 1;
				plr.ammo[0] = 32000;
				plr.ammo[1] = 45;
			}

		} else {

			if (difficulty > 1) {
				for (int i = 0; i <= 9; i++) {
					plr.ammo[i] = 0;
					plr.weapon[i] = 0;
					if (i < 8) {
						plr.orb[i] = 0;
						plr.orbammo[i] = 0;
					}
				}
				plr.weapon[0] = plr.weapon[1] = 1;
				plr.ammo[0] = 32000;
				plr.ammo[1] = 45;
			} else {
				for (int i = 0; i <= 9; i++) {
					plr.ammo[i] = 0;
					plr.weapon[i] = 0;
					if (i < 5) {
						plr.ammo[i] = 40;
						plr.weapon[i] = 1;
					}
					if (i < 8) {
						plr.orb[i] = 0;
						plr.orbammo[i] = 0;
					}
				}
			}
		}

		for (int i = 0; i < MAXPOTIONS; i++)
			plr.potion[i] = 0;
		for (int i = 0; i < MAXTREASURES; i++)
			plr.treasure[i] = 0;

		plr.lvl = 1;
		plr.score = 0;
		plr.health = 100;
		plr.maxhealth = 100;
		plr.armor = 0;
		plr.armortype = 0;
		plr.currentorb = 0;
		plr.currentpotion = 0;

		if (difficulty > 1)
			plr.currweapon = plr.selectedgun = 1;
		else
			plr.currweapon = plr.selectedgun = 4;

		if (game.WH2) {
			plr.potion[0] = 3;
			plr.potion[3] = 1;
			plr.currweapon = plr.selectedgun = 4;
		}

		plr.currweaponfired = 3;
		plr.currweaponflip = 0;

		for (int i = 0; i < MAXNUMORBS; i++)
			plr.orbactive[i] = -1;

		lockclock = totalclock;
		playertorch = 0;

		plr.spellbookflip = 0;

		plr.invincibletime = plr.manatime = -1;
		plr.hasshot = 0;
		plr.orbshot = 0;
		displaytime = -1;
		plr.shadowtime = -1;
		plr.helmettime = -1;
		plr.nightglowtime = -1;
		plr.strongtime = -1;
		plr.invisibletime = -1;
	}

	public static void updateviewmap(PLAYER plr) {
		int i;
		if ((i = plr.sector) > -1) {
			int wallid = sector[i].wallptr;
			show2dsector[i >> 3] |= (1 << (i & 7));
			for (int j = sector[i].wallnum; j > 0; j--) {
				WALL wal = wall[wallid++];
				i = wal.nextsector;
				if (i < 0)
					continue;
				if ((wal.cstat & 0x0071) != 0)
					continue;
				if (wall[wal.nextwall] != null && (wall[wal.nextwall].cstat & 0x0071) != 0)
					continue;
				if (sector[i] != null && sector[i].ceilingz >= sector[i].floorz)
					continue;
				show2dsector[i >> 3] |= (1 << (i & 7));
			}
		}
	}

	public static void plruse(PLAYER plr) {
		engine.neartag(plr.x, plr.y, plr.z, plr.sector, (short) plr.ang, neartag, 1024, 3);

		if (neartag.tagsector >= 0) {
			if (sector[neartag.tagsector].hitag == 0) {
				operatesector(plr, neartag.tagsector);
			} else {
				short daang = (short) plr.ang;
				int daz2 = (int) (100 - plr.horiz) * 2000;
				engine.hitscan(plr.x, plr.y, plr.z, plr.sector, // Start position
						sintable[(daang + 2560) & 2047], // X vector of 3D ang
						sintable[(daang + 2048) & 2047], // Y vector of 3D ang
						daz2, // Z vector of 3D ang
						pHitInfo, CLIPMASK1);

				if (pHitInfo.hitwall >= 0) {
					if ((klabs(plr.x - pHitInfo.hitx) + klabs(plr.y - pHitInfo.hity) < 512)
							&& (klabs((plr.z >> 8) - ((pHitInfo.hitz >> 8) - (64))) <= (512 >> 3))) {
						int pic = wall[pHitInfo.hitwall].picnum;
						if(pic == PENTADOOR1 || pic == PENTADOOR2 || (pic >= PENTADOOR3 && pic <= PENTADOOR7))
							showmessage("find door trigger", 360);
					}
				}
				playsound_loc(S_PUSH1 + (engine.krand() % 2), plr.x, plr.y);
			}
		}
		if (neartag.tagsprite >= 0) {
			if (sprite[neartag.tagsprite].lotag == 1) {
				if(sprite[neartag.tagsprite].picnum == PULLCHAIN1 || sprite[neartag.tagsprite].picnum == SKULLPULLCHAIN1) {
					sprite[neartag.tagsprite].lotag = 0;
					newstatus(neartag.tagsprite, PULLTHECHAIN);
				} else if(sprite[neartag.tagsprite].picnum == LEVERUP) {
					sprite[neartag.tagsprite].lotag = 0;
					newstatus(neartag.tagsprite, ANIMLEVERUP);
				}
				for (int i = 0; i < numsectors; i++)
					if (sector[i].hitag == sprite[neartag.tagsprite].hitag)
						operatesector(plr, i);
			} else
				operatesprite(plr, neartag.tagsprite);
		}
	}

	public static void chunksofmeat(PLAYER plr, int hitsprite, int hitx, int hity, int hitz, short hitsect, int daang) {

		int j;
		short k;
		short zgore = 0;
		int chunk = REDCHUNKSTART;
		int newchunk;

		if (!whcfg.gGameGore)
			return;

		if (sprite[hitsprite].picnum == JUDY || sprite[hitsprite].picnum == JUDYATTACK1
				|| sprite[hitsprite].picnum == JUDYATTACK2)
			return;

		switch (plr.selectedgun) {
		case 1:
		case 2:
			zgore = 1;
			break;
		case 3:
		case 4:
			zgore = 2;
			break;
		case 5:
			zgore = 3;
			break;
		case 6:
			zgore = 1;
			break;
		case 7:
			zgore = 2;
			break;
		case 8:
		case 9:
			zgore = 3;
			break;
		}

		if (sprite[hitsprite].statnum == NUKED) {
			zgore = 32;
		}

		if (sprite[hitsprite].picnum == RAT)
			zgore = 1;

		if (sprite[hitsprite].picnum == WILLOW || sprite[hitsprite].picnum == WILLOWEXPLO
				|| sprite[hitsprite].picnum == WILLOWEXPLO + 1 || sprite[hitsprite].picnum == WILLOWEXPLO + 2
				|| sprite[hitsprite].picnum == GUARDIAN || sprite[hitsprite].picnum == GUARDIANATTACK
				|| sprite[hitsprite].picnum == DEMON)
			return;

		if (sprite[hitsprite].picnum == SKELETON || sprite[hitsprite].picnum == SKELETONATTACK
				|| sprite[hitsprite].picnum == SKELETONDIE) {
			playsound_loc(S_SKELHIT1 + (engine.krand() % 2), sprite[hitsprite].x, sprite[hitsprite].y);
		} else {
			if (engine.krand() % 100 > 60)
				playsound_loc(S_GORE1 + (engine.krand() % 4), sprite[hitsprite].x, sprite[hitsprite].y);
		}

		if ((hitsprite >= 0) && (sprite[hitsprite].statnum < MAXSTATUS)) {
			for (k = 0; k < zgore; k++) {
				newchunk = 0;

				j = engine.insertsprite(hitsect, CHUNKOMEAT);
				if(j == -1)
					return;
				sprite[j].x = hitx;
				sprite[j].y = hity;
				sprite[j].z = hitz;
				sprite[j].cstat = 0;
				if (engine.krand() % 100 > 50) {
					switch (sprite[hitsprite].detail) {
					case GRONTYPE:
						chunk = REDCHUNKSTART + (engine.krand() % 8);
						break;
					case KOBOLDTYPE:
						if (sprite[hitsprite].pal == 0)
							chunk = BROWNCHUNKSTART + (engine.krand() % 8);
						if (sprite[hitsprite].pal == 4)
							chunk = GREENCHUNKSTART + (engine.krand() % 8);
						if (sprite[hitsprite].pal == 7)
							chunk = REDCHUNKSTART + (engine.krand() % 8);
						break;
					case DRAGONTYPE:
						chunk = GREENCHUNKSTART + (engine.krand() % 8);
						break;
					case DEVILTYPE:
						chunk = REDCHUNKSTART + (engine.krand() % 8);
						break;
					case FREDTYPE:
						chunk = BROWNCHUNKSTART + (engine.krand() % 8);
						break;
					case GOBLINTYPE:
					case IMPTYPE:
						if(game.WH2 && (sprite[hitsprite].picnum == IMP || sprite[hitsprite].picnum == IMPATTACK)) {
							if (sprite[hitsprite].pal == 0)
								chunk = GREENCHUNKSTART + (engine.krand() % 8);
						} else {
							if (sprite[hitsprite].pal == 0)
								chunk = GREENCHUNKSTART + (engine.krand() % 8);
							if (sprite[hitsprite].pal == 4)
								chunk = BROWNCHUNKSTART + (engine.krand() % 8);
							if (sprite[hitsprite].pal == 5)
								chunk = TANCHUNKSTART + (engine.krand() % 8);
						}
						break;
					case MINOTAURTYPE:
						chunk = TANCHUNKSTART + (engine.krand() % 8);
						break;
					case SPIDERTYPE:
						chunk = GREYCHUNKSTART + (engine.krand() % 8);
						break;
					case SKULLYTYPE:
					case FATWITCHTYPE:
					case JUDYTYPE:
						chunk = REDCHUNKSTART + (engine.krand() % 8);
						break;
					}
				} else {
					newchunk = 1;
					if (!game.WH2)
						chunk = NEWCHUNK + (engine.krand() % 9);
					else
						chunk = REDCHUNKSTART + (engine.krand() % 8);
				}

				if (sprite[hitsprite].detail == SKELETONTYPE)
					chunk = BONECHUNK1 + (engine.krand() % 9);

				if (plr.weapon[2] == 3 && plr.currweapon == 2) {
					sprite[j].picnum = ARROWFLAME;
				} else {
					sprite[j].picnum = (short) chunk; // = REDCHUNKSTART + (rand() % 8);
				}

				sprite[j].shade = -16;
				sprite[j].xrepeat = 64;
				sprite[j].yrepeat = 64;
				sprite[j].clipdist = 16;
				sprite[j].ang = (short) (((engine.krand() & 1023) - 1024) & 2047);
				sprite[j].xvel = (short) ((engine.krand() & 1023) - 512);
				sprite[j].yvel = (short) ((engine.krand() & 1023) - 512);
				sprite[j].zvel = (short) ((engine.krand() & 1023) - 512);
				if (newchunk == 1)
					sprite[j].zvel <<= 1;
				sprite[j].owner = sprite[plr.spritenum].owner;
				sprite[j].lotag = 512;
				sprite[j].hitag = 0;
				sprite[j].pal = 0;
				movesprite((short) j, ((sintable[(sprite[j].ang + 512) & 2047]) * TICSPERFRAME) << 3,
						((sintable[sprite[j].ang & 2047]) * TICSPERFRAME) << 3, 0, 4 << 8, 4 << 8, 0);
			}
		}

	}

	public static void addhealth(PLAYER plr, int hp) {
		if (plr.godMode && hp < 0)
			return;

		plr.health += hp;

		if (plr.health < 0)
			plr.health = 0;
	}

	public static void addarmor(PLAYER plr, int arm) {
		plr.armor += arm;

		if (plr.armor < 0) {
			plr.armor = 0;
			plr.armortype = 0;
		}
	}

	public static void addscore(PLAYER plr, int score) {
		if(plr == null) return;

		plr.score += score;
		expgained += score;

		goesupalevel(plr);
	}

	public static void goesupalevel(PLAYER plr) {
		if (game.WH2)
			goesupalevel2(plr);
		else
			goesupalevel1(plr);
	}

	public static void goesupalevel2(PLAYER plr) {
		switch (plr.lvl) {
		case 0:
		case 1:
			if (plr.score > 9999) {
				showmessage("thou art a warrior", 360);
				plr.lvl = 2;
				plr.maxhealth = 120;
			}
			break;
		case 2:
			if (plr.score > 19999) {
				showmessage("thou art a swordsman", 360);
				plr.lvl = 3;
				plr.maxhealth = 140;
			}
			break;
		case 3:
			if (plr.score > 29999) {
				showmessage("thou art a hero", 360);
				plr.lvl = 4;
				plr.maxhealth = 160;
			}
			break;
		case 4:
			if (plr.score > 39999) {
				showmessage("thou art a champion", 360);
				plr.lvl = 5;
				plr.maxhealth = 180;
			}
			break;
		case 5:
			if (plr.score > 49999) {
				showmessage("thou art a superhero", 360);
				plr.lvl = 6;
				plr.maxhealth = 200;
			}
			break;
		case 6:
			if (plr.score > 59999) {
				showmessage("thou art a lord", 360);
				plr.lvl = 7;
			}
		}
	}

	public static void goesupalevel1(PLAYER plr) {
		if (plr.score > 2250 && plr.score < 4499 && plr.lvl < 2) {
			showmessage("thou art 2nd level", 360);
			plr.lvl = 2;
			plr.maxhealth = 120;
		} else if (plr.score > 4500 && plr.score < 8999 && plr.lvl < 3) {
			showmessage("thou art 3rd level", 360);
			plr.lvl = 3;
			plr.maxhealth = 140;
		} else if (plr.score > 9000 && plr.score < 17999 && plr.lvl < 4) {
			showmessage("thou art 4th level", 360);
			plr.lvl = 4;
			plr.maxhealth = 160;
		} else if (plr.score > 18000 && plr.score < 35999 && plr.lvl < 5) {
			showmessage("thou art 5th level", 360);
			plr.lvl = 5;
			plr.maxhealth = 180;
		} else if (plr.score > 36000 && plr.score < 74999 && plr.lvl < 6) {
			showmessage("thou art 6th level", 360);
			plr.lvl = 6;
			plr.maxhealth = 200;
		} else if (plr.score > 75000 && plr.score < 179999 && plr.lvl < 7) {
			showmessage("thou art 7th level", 360);
			plr.lvl = 7;
		} else if (plr.score > 180000 && plr.score < 279999 && plr.lvl < 8) {
			showmessage("thou art 8th level", 360);
			plr.lvl = 8;
		} else if (plr.score > 280000 && plr.score < 379999 && plr.lvl < 9) {
			showmessage("thou art hero", 360);
			plr.lvl = 9;
		}
	}

	public static void lockon(PLAYER plr, int numshots, int shootguntype) {
		short daang, i, k, n = 0, s;
		SPRITE spr;

		for (i = 0; i < tspritelistcnt && n < numshots; i++) {
			spr = tspritelist[i];

			if (engine.cansee(plr.x, plr.y, plr.z, plr.sector, spr.x, spr.y, spr.z - (engine.getTile(spr.picnum).getHeight() << 7),
					spr.sectnum)) {
				switch (spr.detail) {
				case KOBOLDTYPE:
				case DEVILTYPE:
				case IMPTYPE:
				case MINOTAURTYPE:
				case SKELETONTYPE:
				case GRONTYPE:
				case DEMONTYPE:
				case GUARDIANTYPE:
				case WILLOWTYPE:
				case NEWGUYTYPE:
				case KURTTYPE:
				case GONZOTYPE:
				case KATIETYPE:
					monsterangle[n] = engine.getangle(tspritelist[i].x - plr.x, tspritelist[i].y - plr.y);
					monsterlist[n] = i;
					n++;
					break;
				}
			}
		}

		daang = (short) (plr.ang - ((numshots * (128 / numshots)) >> 1));
		for (k = 0, s = 0; k < numshots; k++) {
			if (n > 0) {
				spr = tspritelist[monsterlist[s]];
				daang = monsterangle[s];
				shootgunzvel = ((spr.z - (48 << 8) - plr.z) << 8)
						/ engine.ksqrt((spr.x - plr.x) * (spr.x - plr.x) + (spr.y - plr.y) * (spr.y - plr.y));
				s = (short) ((s + 1) % n);
			} else {
				daang += (128 / numshots);
			}
			shootgun(plr, daang, shootguntype);
		}
	}

	public static int getPlayerHeight()
	{
		return game.WH2 ? WH2PLAYERHEIGHT : PLAYERHEIGHT;
	}
}
