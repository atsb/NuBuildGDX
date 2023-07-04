package ru.m210projects.Witchaven;

import static ru.m210projects.Witchaven.Globals.*;
import static ru.m210projects.Witchaven.WHScreen.*;
import static ru.m210projects.Witchaven.Main.*;
import static ru.m210projects.Witchaven.Names.*;
import static ru.m210projects.Witchaven.WH1Names.*;
import static ru.m210projects.Witchaven.WH2Names.*;
import static ru.m210projects.Witchaven.WHPLR.*;
import static ru.m210projects.Witchaven.WHSND.*;
import static ru.m210projects.Witchaven.WHANI.*;
import static ru.m210projects.Witchaven.WHFX.*;
import static ru.m210projects.Witchaven.Items.*;
import static ru.m210projects.Witchaven.AI.Ai.*;
import static ru.m210projects.Witchaven.AI.AIGonzo.*;
import static ru.m210projects.Build.Pragmas.*;
import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Build.Strhandler.*;

import ru.m210projects.Build.Pattern.BuildFont.TextAlign;
import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Witchaven.Types.PLAYER;

public class WHOBJ {

	public static int justwarpedcnt = 0;
	public static byte flashflag = 0x00;

	public final static int eg_onyx_effect = 1;

	public static short torchpattern[] = { 2, 2, 2, 3, 3, 3, 4, 4, 5, 5, 6, 6, 4, 4, 6, 6, 4, 4, 6, 6, 4, 4, 6, 6, 4, 4,
			6, 6, 5, 5, 4, 4, 3, 3, 3, 2, 2, 2 };

	// EG 19 Aug 2017 - Try to prevent monsters teleporting back and forth wildly
	public static int monsterwarptime;

	public static short adjusthp(int hp) {
		float factor = (engine.krand() % 20) / 100;
		int howhard = difficulty;

		if (engine.krand() % 100 > 50)
			return (short) ((hp * (factor + 1)) * howhard);
		else
			return (short) ((hp - (hp * (factor))) * howhard);
	}

	public static void timerprocess(PLAYER plr) {
		if (justwarpedfx > 0) {
			justwarpedfx -= TICSPERFRAME;
			justwarpedcnt += TICSPERFRAME << 6;

			if (justwarpedfx <= 0)
				justwarpedcnt = 0;
		}

		if (plr.poisoned == 1) {
			if (plr.poisontime >= 0) {
				plr.poisontime -= TICSPERFRAME;

				addhealth(plr, 0);

				if (plr.poisontime < 0) {
					startredflash(50);
					addhealth(plr, -10);
					plr.poisontime = 7200;
				}
			}
		}

		// EG 19 Aug 2017 - Try to prevent monsters teleporting back and forth wildly
		if (monsterwarptime > 0)
			monsterwarptime -= TICSPERFRAME;

		if (plr.vampiretime > 0)
			plr.vampiretime -= TICSPERFRAME;

		if (plr.shockme >= 0)
			plr.shockme -= TICSPERFRAME;

		if (plr.helmettime > 0)
			plr.helmettime -= TICSPERFRAME;

		if (displaytime > 0)
			displaytime -= TICSPERFRAME;

		if (plr.shadowtime >= 0)
			plr.shadowtime -= TICSPERFRAME;

		if (plr.nightglowtime >= 0) {
			plr.nightglowtime -= TICSPERFRAME;
			visibility = 256;
			if (plr.nightglowtime < 0)
				visibility = 1024;
		}

		if (plr.strongtime >= 0) {
			plr.strongtime -= TICSPERFRAME;
			whitecount = 10;
		}

		if (plr.invisibletime >= 0) {
			plr.invisibletime -= TICSPERFRAME;
		}

		if (plr.invincibletime >= 0) {
			plr.invincibletime -= TICSPERFRAME;
			whitecount = 10;
		}

		if (plr.manatime >= 0) {
			plr.manatime -= TICSPERFRAME;
			redcount = 20;
		}

		if(plr.spiked != 0)
			plr.spiketics -= TICSPERFRAME;

		if (displaytime <= 0) {
			if (plr.manatime > 0) {
				if (plr.manatime < 512) {
					if ((plr.manatime % 64) > 32) {
						return;
					}
				}
				game.getFont(1).drawText(18,24,  toCharArray("FIRE RESISTANCE"), 0, 0, TextAlign.Left, 2, false);
			} else if (plr.poisoned == 1) {
				game.getFont(1).drawText(18,24,  toCharArray("POISONED"), 0, 0, TextAlign.Left, 2, false);
			} else if (plr.orbactive[5] > 0) {
				if (plr.orbactive[5] < 512) {
					if ((plr.orbactive[5] % 64) > 32) {
						return;
					}
				}
				game.getFont(1).drawText(18,24, toCharArray("FLYING"), 0, 0, TextAlign.Left, 2, false);
			} else if (plr.vampiretime > 0) {
				game.getFont(1).drawText(18,24,  toCharArray("ORNATE HORN"), 0, 0, TextAlign.Left, 2, false);
			}
		}
	}

	public static int getPickHeight() {
		if (game.WH2)
			return PICKHEIGHT2;
		else
			return PICKHEIGHT;
	}

	// see if picked up any objects?
	// JSA 4_27 play appropriate sounds pending object picked up

	public static void processobjs(PLAYER plr) {

		int dh, dx, dy, dz, i, nexti;

		if (plr.sector < 0 || plr.sector >= numsectors)
			return;

		i = headspritesect[plr.sector];
		while (i != -1) {
			nexti = nextspritesect[i];
			dx = klabs(plr.x - sprite[i].x); // x distance to sprite
			dy = klabs(plr.y - sprite[i].y); // y distance to sprite
			dz = klabs((plr.z >> 8) - (sprite[i].z >> 8)); // z distance to sprite
			dh = engine.getTile(sprite[i].picnum).getHeight() >> 1; // height of sprite
			if (dx + dy < PICKDISTANCE && dz - dh <= getPickHeight()) {
				if(isItemSprite(i))
					items[(sprite[i].detail & 0xFF) - ITEMSBASE].pickup(plr, (short)i);

				if (sprite[i].picnum >= EXPLOSTART && sprite[i].picnum <= EXPLOEND && sprite[i].owner != sprite[plr.spritenum].owner)
					if (plr.manatime < 1)
						addhealth(plr, -1);
			}
			i = nexti;
		}
	}

	public static void newstatus(short sn, int seq) {
		switch (seq) {
		case AMBUSH:
			engine.changespritestat(sn, AMBUSH);
			break;
		case LAND:
			engine.changespritestat(sn, LAND);
			break;
		case EVILSPIRIT:
			engine.changespritestat(sn, EVILSPIRIT);
			sprite[sn].lotag = (short) (120 + (engine.krand() & 64));
			break;
		case PATROL:
			engine.changespritestat(sn, PATROL);
			break;
		case WARPFX:
			engine.changespritestat(sn, WARPFX);
			sprite[sn].lotag = 12;
			break;
		case NUKED:
			engine.changespritestat(sn, NUKED);
			if (!game.WH2)
				sprite[sn].lotag = 24;
			break;
		case BROKENVASE:
			engine.changespritestat(sn, BROKENVASE);
			switch (sprite[sn].picnum) {
			case VASEA:
				playsound_loc(S_GLASSBREAK1 + (engine.krand() % 3), sprite[sn].x, sprite[sn].y);
				sprite[sn].picnum = SHATTERVASE;
				break;
			case VASEB:
				playsound_loc(S_GLASSBREAK1 + (engine.krand() % 3), sprite[sn].x, sprite[sn].y);
				sprite[sn].picnum = SHATTERVASE2;
				break;
			case VASEC:
				playsound_loc(S_GLASSBREAK1 + (engine.krand() % 3), sprite[sn].x, sprite[sn].y);
				sprite[sn].picnum = SHATTERVASE3;
				break;
			case STAINGLASS1:
			case STAINGLASS2:
			case STAINGLASS3:
			case STAINGLASS4:
			case STAINGLASS5:
			case STAINGLASS6:
			case STAINGLASS7:
			case STAINGLASS8:
			case STAINGLASS9:
				sprite[sn].picnum++;
				SND_Sound(S_BIGGLASSBREAK1 + (engine.krand() % 3));
				break;
			case FBARRELFALL:
			case BARREL:
				playsound_loc(S_BARRELBREAK, sprite[sn].x, sprite[sn].y);
				sprite[sn].picnum = FSHATTERBARREL;
				break;
			}
			sprite[sn].lotag = 12;
			sprite[sn].cstat &= ~3;
			break;
		case DRAIN:
			engine.changespritestat(sn, DRAIN);
			sprite[sn].lotag = 24;
			sprite[sn].pal = 7;
			break;
		case ANIMLEVERDN:
			playsound_loc(S_PULLCHAIN1, sprite[sn].x, sprite[sn].y);
			sprite[sn].picnum = LEVERUP;
			engine.changespritestat(sn, ANIMLEVERDN);
			sprite[sn].lotag = 24;
			break;
		case ANIMLEVERUP:
			playsound_loc(S_PULLCHAIN1, sprite[sn].x, sprite[sn].y);
			sprite[sn].picnum = LEVERDOWN;
			engine.changespritestat(sn, ANIMLEVERUP);
			sprite[sn].lotag = 24;
			break;
		case SKULLPULLCHAIN1:
		case PULLTHECHAIN:
			playsound_loc(S_PULLCHAIN1, sprite[sn].x, sprite[sn].y);
			engine.changespritestat(sn, PULLTHECHAIN);
			SND_Sound(S_CHAIN1);
			sprite[sn].lotag = 24;
			break;
		case FROZEN:
			// JSA_NEW
			playsound_loc(S_FREEZE, sprite[sn].x, sprite[sn].y);
			engine.changespritestat(sn, FROZEN);
			sprite[sn].lotag = 3600;
			break;
		case DEVILFIRE:
			engine.changespritestat(sn, DEVILFIRE);
			sprite[sn].lotag = (short) (engine.krand() & 120 + 360);
			break;
		case DRIP:
			engine.changespritestat(sn, DRIP);
			break;
		case BLOOD:
			engine.changespritestat(sn, BLOOD);
			break;
		case WAR:
			engine.changespritestat(sn, WAR);
			break;
		case PAIN:
			sprite[sn].lotag = 36;
			switch (sprite[sn].detail) {
			case DEMONTYPE:
				sprite[sn].lotag = 24;
				playsound_loc(S_GUARDIANPAIN1 + (engine.krand() % 2), sprite[sn].x, sprite[sn].y);
				sprite[sn].picnum = DEMON - 1;
				engine.changespritestat(sn, PAIN);
				break;
			case NEWGUYTYPE:
				sprite[sn].lotag = 24;
				sprite[sn].picnum = NEWGUYPAIN;
				engine.changespritestat(sn, PAIN);
				playsound_loc(S_AGM_PAIN1, sprite[sn].x, sprite[sn].y);
				break;

			case KURTTYPE:
				sprite[sn].lotag = 24;
				sprite[sn].picnum = GONZOCSWPAIN;
				engine.changespritestat(sn, PAIN);
				playsound_loc(S_GRONPAINA + (engine.krand() % 3), sprite[sn].x, sprite[sn].y);
				break;

			case GONZOTYPE:
				sprite[sn].lotag = 24;
				switch(sprite[sn].picnum)
				{
					case KURTSTAND:
					case KURTKNEE:
					case KURTAT:
					case KURTPUNCH:
					case KURTREADY:
					case KURTREADY + 1:
					case GONZOCSW:
						sprite[sn].picnum = GONZOCSWPAIN;
						playsound_loc(S_GRONPAINA + (engine.krand() % 3), sprite[sn].x, sprite[sn].y);
						break;
					case GONZOGSW:
						sprite[sn].picnum = GONZOGSWPAIN;
						playsound_loc(S_GRONPAINA + (engine.krand() % 3), sprite[sn].x, sprite[sn].y);
						break;
					case GONZOGHM:
						sprite[sn].picnum = GONZOGHMPAIN;
						playsound_loc(S_GRONPAINA + (engine.krand() % 3), sprite[sn].x, sprite[sn].y);
						break;
					case GONZOGSH:
						sprite[sn].picnum = GONZOGSHPAIN;
						playsound_loc(S_GRONPAINA, sprite[sn].x, sprite[sn].y);
						break;
					default:
						engine.changespritestat(sn, FLEE);
						break;
				}
				engine.changespritestat(sn, PAIN);
				break;
			case KATIETYPE:
				sprite[sn].picnum = KATIEPAIN;
				engine.changespritestat(sn, PAIN);
				break;
			case JUDYTYPE:
				sprite[sn].lotag = 24;
				sprite[sn].picnum = JUDY;
				engine.changespritestat(sn, PAIN);
				break;
			case FATWITCHTYPE:
				sprite[sn].lotag = 24;
				sprite[sn].picnum = FATWITCHDIE;
				engine.changespritestat(sn, PAIN);
				break;
			case SKULLYTYPE:
				sprite[sn].lotag = 24;
				sprite[sn].picnum = SKULLYDIE;
				engine.changespritestat(sn, PAIN);
				break;
			case GUARDIANTYPE:
				sprite[sn].lotag = 24;
				// sprite[sn].picnum=GUARDIANATTACK;
				playsound_loc(S_GUARDIANPAIN1 + (engine.krand() % 2), sprite[sn].x, sprite[sn].y);

				if(game.WH2) sprite[sn].picnum = GUARDIAN;
				else sprite[sn].picnum = GUARDIANCHAR;
				engine.changespritestat(sn, PAIN);
				break;
			case GRONTYPE:
				sprite[sn].lotag = 24;
				engine.changespritestat(sn, PAIN);
				playsound_loc(S_GRONPAINA + engine.krand() % 3, sprite[sn].x, sprite[sn].y);

				if(sprite[sn].picnum == GRONHAL || sprite[sn].picnum == GRONHALATTACK)
					sprite[sn].picnum = GRONHALPAIN;
				else if(sprite[sn].picnum == GRONSW || sprite[sn].picnum == GRONSWATTACK)
					sprite[sn].picnum = GRONSWPAIN;
				else if(sprite[sn].picnum == GRONMU || sprite[sn].picnum == GRONMUATTACK)
					sprite[sn].picnum = GRONMUPAIN;
				break;
			case KOBOLDTYPE:
				sprite[sn].picnum = KOBOLDDIE;
				engine.changespritestat(sn, PAIN);
				playsound_loc(S_KPAIN1 + (engine.krand() % 2), sprite[sn].x, sprite[sn].y);
				break;
			case DEVILTYPE:
				playsound_loc(S_MPAIN1, sprite[sn].x, sprite[sn].y);
				sprite[sn].picnum = DEVILPAIN;
				engine.changespritestat(sn, PAIN);
				break;
			case FREDTYPE:
				sprite[sn].picnum = FREDPAIN;
				engine.changespritestat(sn, PAIN);
				// EG: Sounds for Fred (currently copied from ogre)
				playsound_loc(S_KPAIN1 + (engine.rand() % 2), sprite[sn].x, sprite[sn].y);
				break;
			case GOBLINTYPE:
			case IMPTYPE:
				if (game.WH2 && (sprite[sn].picnum == IMP || sprite[sn].picnum == IMPATTACK)) {
					sprite[sn].lotag = 24;
					sprite[sn].picnum = IMPPAIN;
					engine.changespritestat(sn, PAIN);
				} else {
					sprite[sn].picnum = GOBLINPAIN;
					engine.changespritestat(sn, PAIN);
					playsound_loc(S_GOBPAIN1 + (engine.krand() % 2), sprite[sn].x, sprite[sn].y);
				}
				break;
			case MINOTAURTYPE:
				sprite[sn].picnum = MINOTAURPAIN;
				engine.changespritestat(sn, PAIN);
				playsound_loc(S_MPAIN1, sprite[sn].x, sprite[sn].y);
				break;
			default:
				engine.changespritestat(sn, FLEE);
				break;
			}
			break;
		case FLOCKSPAWN:
			sprite[sn].lotag = 36;
			sprite[sn].extra = 10;
			engine.changespritestat(sn, FLOCKSPAWN);
			break;
		case FLOCK:
			sprite[sn].lotag = 128;
			sprite[sn].extra = 0;
			sprite[sn].pal = 0;
			engine.changespritestat(sn, FLOCK);
			break;
		case FINDME:
			sprite[sn].lotag = 360;
			if (sprite[sn].picnum == RAT) {
				sprite[sn].ang = (short) (((engine.krand() & 512 - 256) + sprite[sn].ang + 1024) & 2047); // NEW
				engine.changespritestat(sn, FLEE);
			} else
				engine.changespritestat(sn, FINDME);
			break;
		case SKIRMISH:
			sprite[sn].lotag = 60;
			if (sprite[sn].picnum == RAT) {
				sprite[sn].ang = (short) (((engine.krand() & 512 - 256) + sprite[sn].ang + 1024) & 2047); // NEW
				engine.changespritestat(sn, FLEE);
			} else
				engine.changespritestat(sn, SKIRMISH);
			break;
		case CHILL:
			sprite[sn].lotag = 60;
			engine.changespritestat(sn, CHILL);
			break;
		case WITCHSIT:
			sprite[sn].lotag = 12;
			engine.changespritestat(sn, WITCHSIT);
			break;
		case DORMANT:
			sprite[sn].lotag = (short) (engine.krand() & 2047 + 2047);
			break;
		case ACTIVE:
			sprite[sn].lotag = 360;
			break;
		case FLEE:
			switch (sprite[sn].detail) {
			case GONZOTYPE:

				switch (sprite[sn].picnum) {
				case GONZOCSWAT:
				case KURTSTAND:
		        case KURTKNEE:
		        case KURTAT:
		        case KURTPUNCH:
					sprite[sn].picnum = GONZOCSW;
					break;
				case GONZOGSWAT:
					sprite[sn].picnum = GONZOGSW;
					break;
				case GONZOGHMAT:
					sprite[sn].picnum = GONZOGHM;
					break;
				case GONZOGSHAT:
					sprite[sn].picnum = GONZOGSH;
					break;
				}
				break;
			case NEWGUYTYPE:
				sprite[sn].picnum = NEWGUY;
				break;
			case KURTTYPE:
				sprite[sn].picnum = GONZOCSW;
				break;
			case GRONTYPE:
				if(sprite[sn].picnum == GRONHALATTACK)
					sprite[sn].picnum = GRONHAL;
				else if(sprite[sn].picnum == GRONMUATTACK)
					sprite[sn].picnum = GRONMU;
				else if(sprite[sn].picnum == GRONSWATTACK)
					sprite[sn].picnum = GRONSW;
				break;
			case DEVILTYPE:
				sprite[sn].picnum = DEVIL;
				break;
			case KOBOLDTYPE:
				sprite[sn].picnum = KOBOLD;
				break;
			case MINOTAURTYPE:
				sprite[sn].picnum = MINOTAUR;
				break;
			case SKELETONTYPE:
				sprite[sn].picnum = SKELETON;
				break;
			case FREDTYPE:
				sprite[sn].picnum = FRED;
				break;
			case GOBLINTYPE:
				sprite[sn].picnum = GOBLIN;
				break;
			}

			engine.changespritestat(sn, FLEE);
			if (!game.WH2 && sprite[sn].picnum == DEVILATTACK && sprite[sn].picnum == DEVIL)
				sprite[sn].lotag = (short) (120 + (engine.krand() & 360));
			else
				sprite[sn].lotag = 60;
			break;
		case BOB:
			engine.changespritestat(sn, BOB);
			break;
		case LIFTUP:
			if (cartsnd == -1) {
				playsound_loc(S_CLUNK, sprite[sn].x, sprite[sn].y);
				cartsnd = playsound(S_CHAIN1, sprite[sn].x, sprite[sn].y, 5);
			}

			engine.changespritestat(sn, LIFTUP);
			break;
		case LIFTDN:
			if (cartsnd == -1) {
				playsound_loc(S_CLUNK, sprite[sn].x, sprite[sn].y);
				cartsnd = playsound(S_CHAIN1, sprite[sn].x, sprite[sn].y, 5);
			}

			engine.changespritestat(sn, LIFTDN);
			break;
		case SHOVE:
			sprite[sn].lotag = 128;
			engine.changespritestat(sn, SHOVE);
			break;
		case SHATTER:
			engine.changespritestat(sn, SHATTER);
			switch (sprite[sn].picnum) {
			case FBARRELFALL:
				sprite[sn].picnum = FSHATTERBARREL;
				break;
			}
			break;
		case YELL:
			engine.changespritestat(sn, YELL);
			sprite[sn].lotag = 12;
			break;
		case ATTACK2: //WH1
			if(game.WH2) break;
			sprite[sn].lotag = 40;
			sprite[sn].cstat |= 1;
			engine.changespritestat(sn, ATTACK2);
			sprite[sn].picnum = DRAGONATTACK2;
			playsound_loc(S_DRAGON1 + (engine.krand() % 3), sprite[sn].x, sprite[sn].y);
		case ATTACK:
			sprite[sn].lotag = 64;
			sprite[sn].cstat |= 1;
			engine.changespritestat(sn, ATTACK);
			switch (sprite[sn].detail) {
			case NEWGUYTYPE:
				if (sprite[sn].extra > 20) {
					sprite[sn].picnum = NEWGUYCAST;
					sprite[sn].lotag = 24;
				} else if (sprite[sn].extra > 10)
					sprite[sn].picnum = NEWGUYBOW;
				else if (sprite[sn].extra > 0)
					sprite[sn].picnum = NEWGUYMACE;
				else
					sprite[sn].picnum = NEWGUYPUNCH;
				break;
			case GONZOTYPE:
			case KURTTYPE:
				switch(sprite[sn].picnum)
				{
				case GONZOCSW:
					if (sprite[sn].extra > 10)
						sprite[sn].picnum = GONZOCSWAT;
					else if (sprite[sn].extra > 0) {
						sprite[sn].picnum = KURTREADY;
						sprite[sn].lotag = 12;
					} else
						sprite[sn].picnum = KURTPUNCH;
					break;
				case GONZOGSW:
					sprite[sn].picnum = GONZOGSWAT;
					break;
				case GONZOGHM:
					sprite[sn].picnum = GONZOGHMAT;
					break;
				case GONZOGSH:
					sprite[sn].picnum = GONZOGSHAT;
					break;
				}
				break;
			case KATIETYPE:
				if ((engine.krand() % 10) > 4) {
					playsound_loc(S_JUDY1, sprite[sn].x, sprite[sn].y);
				}
				sprite[sn].picnum = KATIEAT;
				break;
			case DEMONTYPE:
				playsound_loc(S_GUARDIAN1 + (engine.krand() % 2), sprite[sn].x, sprite[sn].y);
				sprite[sn].picnum = DEMON;
				break;
			case GRONTYPE:
				if(sprite[sn].picnum == GRONHAL)
					sprite[sn].picnum = GRONHALATTACK;
				else if(sprite[sn].picnum == GRONMU)
					sprite[sn].picnum = GRONMUATTACK;
				else if(sprite[sn].picnum == GRONSW)
					sprite[sn].picnum = GRONSWATTACK;
				break;
			case KOBOLDTYPE:
				sprite[sn].picnum = KOBOLDATTACK;
				if (engine.krand() % 10 > 4)
					playsound_loc(S_KSNARL1 + (engine.krand() % 4), sprite[sn].x, sprite[sn].y);
				break;
			case DRAGONTYPE:
				if ((engine.krand() % 10) > 3)
					playsound_loc(S_DRAGON1 + (engine.krand() % 2), sprite[sn].x, sprite[sn].y);

				sprite[sn].picnum = DRAGONATTACK;
				break;
			case DEVILTYPE:
				if ((engine.krand() % 10) > 4)
					playsound_loc(S_DEMON1 + (engine.krand() % 5), sprite[sn].x, sprite[sn].y);

				sprite[sn].picnum = DEVILATTACK;
				break;
			case FREDTYPE:
				sprite[sn].picnum = FREDATTACK;
				/* EG: Sounds for Fred (currently copied from Ogre) */
				if (engine.rand() % 10 > 4)
					playsound_loc(S_KSNARL1 + (engine.rand() % 4), sprite[sn].x, sprite[sn].y);
				break;
			case SKELETONTYPE:
				sprite[sn].picnum = SKELETONATTACK;
				break;
			case IMPTYPE:
				sprite[sn].lotag = 92;
				if ((engine.krand() % 10) > 5)
					playsound_loc(S_IMPGROWL1 + (engine.krand() % 3), sprite[sn].x, sprite[sn].y);
				sprite[sn].picnum = IMPATTACK;
				break;
			case GOBLINTYPE:
				if ((engine.krand() % 10) > 5)
					playsound_loc(S_GOBLIN1 + (engine.krand() % 3), sprite[sn].x, sprite[sn].y);
				sprite[sn].picnum = GOBLINATTACK;
				break;
			case MINOTAURTYPE:
				if ((engine.krand() % 10) > 4)
					playsound_loc(S_MSNARL1 + (engine.krand() % 3), sprite[sn].x, sprite[sn].y);

				sprite[sn].picnum = MINOTAURATTACK;
				break;
			case SKULLYTYPE:
				sprite[sn].picnum = SKULLYATTACK;
				break;
			case FATWITCHTYPE:
				if ((engine.krand() % 10) > 4)
					playsound_loc(S_FATLAUGH, sprite[sn].x, sprite[sn].y);
				sprite[sn].picnum = FATWITCHATTACK;
				break;
			case JUDYTYPE:
				// sprite[sn].cstat=0;
				if (engine.krand() % 2 == 0)
					sprite[sn].picnum = JUDYATTACK1;
				else
					sprite[sn].picnum = JUDYATTACK2;
				break;
			case WILLOWTYPE:
				playsound_loc(S_WISP + (engine.krand() % 2), sprite[sn].x, sprite[sn].y);
				sprite[sn].pal = 7;
				break;
			case GUARDIANTYPE:
				playsound_loc(S_GUARDIAN1 + (engine.krand() % 2), sprite[sn].x, sprite[sn].y);
				sprite[sn].picnum = GUARDIANATTACK;
				break;
			}
			break;
		case FACE:
			engine.changespritestat(sn, FACE);
			break;
		case STAND:
			engine.changespritestat(sn, FACE);
			sprite[sn].lotag = 0;
			break;
		case CHASE:
			if (sprite[sn].picnum == RAT)
				engine.changespritestat(sn, FLEE);
			else
				engine.changespritestat(sn, CHASE);
			sprite[sn].lotag = 256;
			switch (sprite[sn].detail) {
			case NEWGUYTYPE:
				sprite[sn].picnum = NEWGUY;
				break;
			case KATIETYPE:
				sprite[sn].picnum = KATIE;
				break;
			case DEMONTYPE:
				sprite[sn].picnum = DEMON;
				break;
			case KURTTYPE:
				sprite[sn].picnum = GONZOCSW;
				break;
			case GONZOTYPE:
				switch (sprite[sn].picnum) {
				case GONZOCSWAT:
				case KURTSTAND:
		        case KURTKNEE:
		        case KURTAT:
		        case KURTPUNCH:
					sprite[sn].picnum = GONZOCSW;
					break;
				case GONZOGSWAT:
					sprite[sn].picnum = GONZOGSW;
					break;
				case GONZOGHMAT:
					sprite[sn].picnum = GONZOGHM;
					break;
				case GONZOGSHAT:
					sprite[sn].picnum = GONZOGSH;
					break;
				}
				break;
			case GRONTYPE:
				if(sprite[sn].picnum == GRONHALATTACK) {
					if (sprite[sn].extra > 2)
						sprite[sn].picnum = GRONHAL;
					else
						sprite[sn].picnum = GRONMU;
				}
				else if(sprite[sn].picnum == GRONSWATTACK)
					sprite[sn].picnum = GRONSW;
				else if(sprite[sn].picnum == GRONMUATTACK) {
					if (sprite[sn].extra > 0)
						sprite[sn].picnum = GRONMU;
					else
						sprite[sn].picnum = GRONSW;
				}
				break;
			case KOBOLDTYPE:
				sprite[sn].picnum = KOBOLD;
				break;
			case DRAGONTYPE:
				sprite[sn].picnum = DRAGON;
				break;
			case DEVILTYPE:
				sprite[sn].picnum = DEVIL;
				break;
			case FREDTYPE:
				sprite[sn].picnum = FRED;
				break;
			case SKELETONTYPE:
				sprite[sn].picnum = SKELETON;
				break;
			case GOBLINTYPE:
			case IMPTYPE:
				if (game.WH2 && sprite[sn].picnum == IMPATTACK) {
					if (engine.krand() % 10 > 2)
						sprite[sn].picnum = IMP;
				} else {
					if (engine.krand() % 10 > 2)
						playsound_loc(S_GOBLIN1 + (engine.krand() % 3), sprite[sn].x, sprite[sn].y);

					sprite[sn].picnum = GOBLIN;
				}
				break;
			case MINOTAURTYPE:
				// JSA_DEMO3
				playsound_loc(S_MSNARL1 + (engine.krand() % 4), sprite[sn].x, sprite[sn].y);
				sprite[sn].picnum = MINOTAUR;
				break;
			case SKULLYTYPE:
				sprite[sn].picnum = SKULLY;
				break;
			case FATWITCHTYPE:
				sprite[sn].picnum = FATWITCH;
				break;
			case JUDYTYPE:
				sprite[sn].picnum = JUDY;
				break;
			case GUARDIANTYPE:
				sprite[sn].picnum = GUARDIAN;
				break;
			case WILLOWTYPE:
				sprite[sn].pal = 6;
				break;
			}
			break;
		case MISSILE:
			engine.changespritestat(sn, MISSILE);
			break;
		case CAST:
			engine.changespritestat(sn, CAST);
			sprite[sn].lotag = 12;

			if(sprite[sn].picnum == GRONHALATTACK
					|| sprite[sn].picnum == GONZOCSWAT
					|| sprite[sn].picnum == NEWGUY)
				sprite[sn].lotag = 24;
			else if(sprite[sn].picnum == GRONMUATTACK)
				sprite[sn].lotag = 36;
			break;
		case FX:
			engine.changespritestat(sn, FX);
			break;
		case DIE:
			if(sprite[sn].statnum == DIE || sprite[sn].statnum == DEAD) //already dying
				break;

			if(sprite[sn].detail != GONZOTYPE || sprite[sn].shade != 31)
				sprite[sn].cstat &= ~3;
			else sprite[sn].cstat &= ~1;
			switch (sprite[sn].detail) {
			case NEWGUYTYPE:
				sprite[sn].lotag = 20;
				sprite[sn].picnum = NEWGUYDIE;
				playsound_loc(S_AGM_DIE1 + (engine.krand() % 3), sprite[sn].x, sprite[sn].y);
				break;
			case KURTTYPE:
			case GONZOTYPE:
				sprite[sn].lotag = 20;
				playsound_loc(S_GRONDEATHA + engine.krand() % 3, sprite[sn].x, sprite[sn].y);
				switch (sprite[sn].picnum) {
				case KURTSTAND:
				case KURTKNEE:
				case KURTAT:
				case KURTPUNCH:
				case KURTREADY:
				case KURTREADY + 1:
				case GONZOCSW:
				case GONZOCSWAT:
				case GONZOCSWPAIN:
					sprite[sn].picnum = GONZOCSWPAIN;
					break;
				case GONZOGSW:
				case GONZOGSWAT:
				case GONZOGSWPAIN:
					sprite[sn].picnum = GONZOGSWPAIN;
					break;
				case GONZOGHM:
				case GONZOGHMAT:
				case GONZOGHMPAIN:
					sprite[sn].picnum = GONZOGHMPAIN;
					break;
				case GONZOGSH:
				case GONZOGSHAT:
				case GONZOGSHPAIN:
					sprite[sn].picnum = GONZOGSHPAIN;
					break;
				case GONZOBSHPAIN:
					sprite[sn].picnum = GONZOBSHPAIN;
					if (sprite[sn].shade > 30) {
						trailingsmoke(sn, false);
						engine.deletesprite(sn);
						return;
					}
					break;
				default:
					sprite[sn].lotag = 20;
					sprite[sn].picnum = GONZOGSWPAIN;
					System.err.println("die error " + sprite[sn].picnum);
					return;
				}
				break;
			case KATIETYPE:
				playsound_loc(S_JUDYDIE, sprite[sn].x, sprite[sn].y);
				sprite[sn].lotag = 20;
				sprite[sn].picnum = KATIEPAIN;
				break;
			case DEMONTYPE:
				playsound_loc(S_GUARDIANDIE, sprite[sn].x, sprite[sn].y);
				explosion(sn, sprite[sn].x, sprite[sn].y, sprite[sn].z, sprite[sn].owner);
				engine.deletesprite(sn);
				addscore(aiGetPlayerTarget(sn), 1500);
				kills++;
				return;
			case GRONTYPE:
				sprite[sn].lotag = 20;
				playsound_loc(S_GRONDEATHA + engine.krand() % 3, sprite[sn].x, sprite[sn].y);
				if(sprite[sn].picnum == GRONHAL || sprite[sn].picnum == GRONHALATTACK || sprite[sn].picnum == GRONHALPAIN)
					sprite[sn].picnum = GRONHALDIE;
				else if(sprite[sn].picnum == GRONSW || sprite[sn].picnum == GRONSWATTACK || sprite[sn].picnum == GRONSWPAIN)
					sprite[sn].picnum = GRONSWDIE;
				else if(sprite[sn].picnum == GRONMU || sprite[sn].picnum == GRONMUATTACK || sprite[sn].picnum == GRONMUPAIN)
					sprite[sn].picnum = GRONMUDIE;
				else {
					System.err.println("error gron" + sprite[sn].picnum);
					sprite[sn].picnum = GRONDIE;
				}
				break;
			case FISHTYPE:
			case RATTYPE:
				sprite[sn].lotag = 20;
				break;
			case KOBOLDTYPE:
				playsound_loc(S_KDIE1 + (engine.krand() % 2), sprite[sn].x, sprite[sn].y);
				sprite[sn].lotag = 20;
				sprite[sn].picnum = KOBOLDDIE;
				break;
			case DRAGONTYPE:
				playsound_loc(S_DEMONDIE1 + (engine.krand() % 2), sprite[sn].x, sprite[sn].y);
				sprite[sn].lotag = 20;
				sprite[sn].picnum = DRAGONDIE;

				break;
			case DEVILTYPE:
				playsound_loc(S_DEMONDIE1 + (engine.krand() % 2), sprite[sn].x, sprite[sn].y);
				sprite[sn].lotag = 20;
				sprite[sn].picnum = DEVILDIE;
				break;
			case FREDTYPE:
				sprite[sn].lotag = 20;
				sprite[sn].picnum = FREDDIE;
				/* EG: Sounds for Fred (currently copied from Ogre) */
				playsound_loc(S_KDIE1 + (engine.rand() % 2), sprite[sn].x, sprite[sn].y);
				break;
			case SKELETONTYPE:
				playsound_loc(S_SKELETONDIE, sprite[sn].x, sprite[sn].y);
				sprite[sn].lotag = 20;
				sprite[sn].picnum = SKELETONDIE;
				break;
			case IMPTYPE:
				playsound_loc(S_IMPDIE1 + (engine.krand() % 2), sprite[sn].x, sprite[sn].y);
				sprite[sn].lotag = 20;
				sprite[sn].picnum = IMPDIE;
				break;
			case GOBLINTYPE:
				playsound_loc(S_GOBDIE1 + (engine.krand() % 3), sprite[sn].x, sprite[sn].y);
				sprite[sn].lotag = 20;
				sprite[sn].picnum = GOBLINDIE;
				break;
			case MINOTAURTYPE:
				playsound_loc(S_MDEATH1, sprite[sn].x, sprite[sn].y);
				sprite[sn].lotag = 10;
				sprite[sn].picnum = MINOTAURDIE;
				break;
			case SPIDERTYPE:
				sprite[sn].lotag = 10;
				sprite[sn].picnum = SPIDERDIE;
				break;
			case SKULLYTYPE:
				sprite[sn].lotag = 20;
				sprite[sn].picnum = SKULLYDIE;
				playsound_loc(S_SKULLWITCHDIE, sprite[sn].x, sprite[sn].y);
				break;
			case FATWITCHTYPE:
				sprite[sn].lotag = 20;
				sprite[sn].picnum = FATWITCHDIE;
				playsound_loc(S_FATWITCHDIE, sprite[sn].x, sprite[sn].y);
				break;
			case JUDYTYPE:
				sprite[sn].lotag = 20;
				if (mapon < 24) {
					for (int j = 0; j < 8; j++)
						trailingsmoke(sn, true);
					engine.deletesprite(sn);
					return;
				} else {
					sprite[sn].picnum = JUDYDIE;
					playsound_loc(S_JUDYDIE, sprite[sn].x, sprite[sn].y);
				}
				break;
			case GUARDIANTYPE:
				playsound_loc(S_GUARDIANDIE, sprite[sn].x, sprite[sn].y);
				for (int j = 0; j < 4; j++)
					explosion(sn, sprite[sn].x, sprite[sn].y, sprite[sn].z, sprite[sn].owner);
				engine.deletesprite(sn);
				addscore(aiGetPlayerTarget(sn), 1500);
				kills++;
				return;
			case WILLOWTYPE:
				playsound_loc(S_WILLOWDIE, sprite[sn].x, sprite[sn].y);
				sprite[sn].pal = 0;
				sprite[sn].lotag = 20;
				sprite[sn].picnum = WILLOWEXPLO;
				break;
			}
			engine.changespritestat(sn, DIE);
			break;

		case RESURECT:
			sprite[sn].lotag = 7200;
			switch (sprite[sn].picnum) {
			case GONZOBSHDEAD:
				sprite[sn].cstat &= ~3;
				engine.changespritestat(sn, RESURECT);
				addscore(aiGetPlayerTarget(sn), 85);
				sprite[sn].detail = GONZOTYPE;
				break;
			case NEWGUYDEAD:
				sprite[sn].cstat &= ~3;
				engine.changespritestat(sn, RESURECT);
				addscore(aiGetPlayerTarget(sn), 55);
				sprite[sn].detail = NEWGUYTYPE;
				break;
			case GONZOCSWDEAD:
				sprite[sn].cstat &= ~3;
				engine.changespritestat(sn, RESURECT);
				addscore(aiGetPlayerTarget(sn), 55);
				sprite[sn].detail = GONZOTYPE;
				break;
			case GONZOGSWDEAD:
				sprite[sn].cstat &= ~3;
				engine.changespritestat(sn, RESURECT);
				addscore(aiGetPlayerTarget(sn), 105);
				sprite[sn].detail = GONZOTYPE;
				break;
			case GONZOGHMDEAD:
				sprite[sn].cstat &= ~3;
				engine.changespritestat(sn, RESURECT);
				addscore(aiGetPlayerTarget(sn), 100);
				sprite[sn].detail = GONZOTYPE;
				break;
			case GONZOGSHDEAD:
				sprite[sn].cstat &= ~3;
				engine.changespritestat(sn, RESURECT);
				addscore(aiGetPlayerTarget(sn), 110);
				sprite[sn].detail = GONZOTYPE;
				break;
			case KATIEDEAD:
				trailingsmoke(sn, true);
				sprite[sn].picnum = KATIEDEAD;
				sprite[sn].cstat &= ~3;
				engine.changespritestat(sn, RESURECT);
				spawnhornskull(sn);
				addscore(aiGetPlayerTarget(sn), 5000);
				sprite[sn].detail = KATIETYPE;
				break;
			case DEVILDEAD:
				trailingsmoke(sn, true);
				sprite[sn].picnum = DEVILDEAD;
				sprite[sn].cstat &= ~3;
				engine.changespritestat(sn, RESURECT);
				addscore(aiGetPlayerTarget(sn), 70);
				sprite[sn].detail = DEVILTYPE;
				break;
			case IMPDEAD:
				sprite[sn].picnum = IMPDEAD;
				sprite[sn].cstat &= ~3;
				engine.changespritestat(sn, RESURECT);
				addscore(aiGetPlayerTarget(sn), 115);
				sprite[sn].detail = IMPTYPE;
				break;
			case KOBOLDDEAD:
				sprite[sn].picnum = KOBOLDDEAD;
				sprite[sn].cstat &= ~3;
				engine.changespritestat(sn, RESURECT);
				sprite[sn].detail = KOBOLDTYPE;
				if(game.WH2) {
					switch (sprite[sn].pal) {
		               case 0:
		            	   addscore(aiGetPlayerTarget(sn), 25);
		                    break;
		               case 7:
		            	   addscore(aiGetPlayerTarget(sn), 40);
		                    break;
		               }
					addscore(aiGetPlayerTarget(sn), 10);
					break;
				}


				addscore(aiGetPlayerTarget(sn), 10);
				break;
			case DRAGONDEAD:
				sprite[sn].picnum = DRAGONDEAD;
				sprite[sn].cstat &= ~3;
				engine.changespritestat(sn, RESURECT);
				addscore(aiGetPlayerTarget(sn), 4000);
				sprite[sn].detail = DRAGONTYPE;
				break;
			case FREDDEAD:
				sprite[sn].picnum = FREDDEAD;
				sprite[sn].cstat &= ~3;
				engine.changespritestat(sn, RESURECT);
				addscore(aiGetPlayerTarget(sn), 40);
				sprite[sn].detail = FREDTYPE;
				break;
			case GOBLINDEAD:
				sprite[sn].picnum = GOBLINDEAD;
				sprite[sn].cstat &= ~3;
				engine.changespritestat(sn, RESURECT);
				addscore(aiGetPlayerTarget(sn), 25);
				sprite[sn].detail = GOBLINTYPE;
				break;
			case MINOTAURDEAD:
				sprite[sn].picnum = MINOTAURDEAD;
				sprite[sn].cstat &= ~3;
				engine.changespritestat(sn, RESURECT);
				addscore(aiGetPlayerTarget(sn), game.WH2 ? 95 : 170);
				sprite[sn].detail = MINOTAURTYPE;
				break;
			case SPIDERDEAD:
				sprite[sn].picnum = SPIDERDEAD;
				sprite[sn].cstat &= ~3;
				engine.changespritestat(sn, RESURECT);
				addscore(aiGetPlayerTarget(sn), 5);
				sprite[sn].detail = SPIDERTYPE;
				break;
			case SKULLYDEAD:
				sprite[sn].picnum = SKULLYDEAD;
				sprite[sn].cstat &= ~3;
				engine.changespritestat(sn, RESURECT);
				addscore(aiGetPlayerTarget(sn), 1000);
				sprite[sn].detail = SKULLYTYPE;
				break;
			case FATWITCHDEAD:
				sprite[sn].picnum = FATWITCHDEAD;
				sprite[sn].cstat &= ~3;
				engine.changespritestat(sn, RESURECT);
				addscore(aiGetPlayerTarget(sn), 900);
				sprite[sn].detail = FATWITCHTYPE;
				break;
			case JUDYDEAD:
				sprite[sn].picnum = JUDYDEAD;
				sprite[sn].cstat &= ~3;
				engine.changespritestat(sn, RESURECT);
				addscore(aiGetPlayerTarget(sn), 7000);
				sprite[sn].detail = JUDYTYPE;
				break;
			default:
				if(sprite[sn].picnum == SKELETONDEAD) {
					sprite[sn].picnum = SKELETONDEAD;
					sprite[sn].cstat &= ~3;
					engine.changespritestat(sn, RESURECT);
					sprite[sn].detail = SKELETONTYPE;
					addscore(aiGetPlayerTarget(sn), game.WH2 ? 20 : 10);
				} else if(sprite[sn].picnum == GRONDEAD) {
					sprite[sn].picnum = GRONDEAD;
					sprite[sn].cstat &= ~3;
					sprite[sn].extra = 3;
					sprite[sn].detail = GRONTYPE;
					engine.changespritestat(sn, RESURECT);
					if(game.WH2) {
						switch (sprite[sn].pal) {
			               case 0:
			            	   addscore(aiGetPlayerTarget(sn),125);
			                    break;
			               case 10:
			            	   addscore(aiGetPlayerTarget(sn),90);
			                    break;
			               case 11:
			            	   addscore(aiGetPlayerTarget(sn),115);
			                    break;
			               case 12:
			            	   addscore(aiGetPlayerTarget(sn),65);
			                    break;
			               }
						} else addscore(aiGetPlayerTarget(sn), 200);
				}
				break;
			}
			break;

		case DEAD:
			sprite[sn].detail = 0;
			if(sprite[sn].picnum == SKELETONDEAD) {
				sprite[sn].picnum = SKELETONDEAD;
				sprite[sn].cstat &= ~3;
				engine.changespritestat(sn, DEAD);
				if(game.WH2) {
					 addscore(aiGetPlayerTarget(sn), 70);
		            monsterweapon(sn);
				}
			} else if(sprite[sn].picnum == FISH || sprite[sn].picnum == RAT) {
				sprite[sn].cstat &= ~3;
				engine.changespritestat(sn, DEAD);
				addscore(aiGetPlayerTarget(sn), 5);
			} else if(sprite[sn].picnum == GRONDEAD) {
				sprite[sn].picnum = GRONDEAD;
				sprite[sn].cstat &= ~3;
				engine.changespritestat(sn, DEAD);
				if(game.WH2) {
					switch (sprite[sn].pal) {
		               case 0:
		            	   addscore(aiGetPlayerTarget(sn), 125);
		                    break;
		               case 10:
		            	   addscore(aiGetPlayerTarget(sn), 90);
		                    break;
		               case 11:
		            	   addscore(aiGetPlayerTarget(sn), 115);
		                    break;
		               case 12:
		            	   addscore(aiGetPlayerTarget(sn), 65);
		                    break;
		               }
				} else {
					addscore(aiGetPlayerTarget(sn), 200);
				}
				monsterweapon(sn);
			} else {
				switch (sprite[sn].picnum) {
				case GONZOBSHDEAD:
					if (netgame) {
						break;
					}
					sprite[sn].picnum = GONZOBSHDEAD;
					sprite[sn].cstat &= ~3;
					engine.changespritestat(sn, DEAD);
					if (sprite[sn].pal == 4) {
						engine.changespritestat(sn, SHADE);
						deaddude(sn);
					} else {
						engine.changespritestat(sn, DEAD);
						if (sprite[sn].shade < 25)
							monsterweapon(sn);
					}
					addscore(aiGetPlayerTarget(sn), 85);
					break;
				case GONZOCSWDEAD:
					if (netgame) {
						break;
					}
					sprite[sn].picnum = GONZOCSWDEAD;
					sprite[sn].cstat &= ~3;
					if (sprite[sn].pal == 4) {
						engine.changespritestat(sn, SHADE);
						deaddude(sn);
					} else {
						engine.changespritestat(sn, DEAD);
						monsterweapon(sn);
					}
					addscore(aiGetPlayerTarget(sn), 55);
					break;
				case GONZOGSWDEAD:
					if (netgame) {
						break;
					}
					sprite[sn].picnum = GONZOGSWDEAD;
					sprite[sn].cstat &= ~3;
					if (sprite[sn].pal == 4) {
						engine.changespritestat(sn, SHADE);
						deaddude(sn);
					} else {
						engine.changespritestat(sn, DEAD);
						monsterweapon(sn);
					}
					addscore(aiGetPlayerTarget(sn), 105);
					break;
				case GONZOGHMDEAD:
					if (netgame) {
						break;
					}
					sprite[sn].picnum = GONZOGHMDEAD;
					sprite[sn].cstat &= ~3;
					if (sprite[sn].pal == 4) {
						engine.changespritestat(sn, SHADE);
						deaddude(sn);
					} else {
						engine.changespritestat(sn, DEAD);
						monsterweapon(sn);
					}
					addscore(aiGetPlayerTarget(sn), 100);
					break;
				case NEWGUYDEAD:
					if (netgame) {
						break;
					}
					sprite[sn].picnum = NEWGUYDEAD;
					sprite[sn].cstat &= ~3;
					engine.changespritestat(sn, DEAD);
					monsterweapon(sn);
					addscore(aiGetPlayerTarget(sn), 50);
					break;
				case GONZOGSHDEAD:
					if (netgame) {
						break;
					}
					sprite[sn].picnum = GONZOGSHDEAD;
					if(sprite[sn].shade != 31)
						sprite[sn].cstat &= ~3;
					else sprite[sn].cstat &= ~1;
					if (sprite[sn].pal == 4) {
						engine.changespritestat(sn, SHADE);
						deaddude(sn);
					} else {
						engine.changespritestat(sn, DEAD);
						monsterweapon(sn);
					}
					addscore(aiGetPlayerTarget(sn), 110);
					break;
				case KATIEDEAD:
					if (netgame) {
						break;
					}
					trailingsmoke(sn, true);
					sprite[sn].picnum = DEVILDEAD;
					sprite[sn].cstat &= ~3;
					engine.changespritestat(sn, DEAD);
					spawnhornskull(sn);
					addscore(aiGetPlayerTarget(sn), 500);
					break;
				case IMPDEAD:
					if (!game.WH2)
						break;
					sprite[sn].picnum = IMPDEAD;
					sprite[sn].cstat &= ~3;
					engine.changespritestat(sn, DEAD);
					addscore(aiGetPlayerTarget(sn), 115);
					monsterweapon(sn);
					break;
				case KOBOLDDEAD:
					sprite[sn].picnum = KOBOLDDEAD;
					sprite[sn].cstat &= ~3;
					engine.changespritestat(sn, DEAD);
					addscore(aiGetPlayerTarget(sn), 10);
					break;
				case DRAGONDEAD:
					sprite[sn].picnum = DRAGONDEAD;
					sprite[sn].cstat &= ~3;
					engine.changespritestat(sn, DEAD);
					addscore(aiGetPlayerTarget(sn), 4000);
					break;
				case DEVILDEAD:
					trailingsmoke(sn, true);
					sprite[sn].picnum = DEVILDEAD;
					sprite[sn].cstat &= ~3;
					engine.changespritestat(sn, DEAD);
					addscore(aiGetPlayerTarget(sn), game.WH2 ? 70 : 50);
					if(game.WH2)
						 monsterweapon(sn);
					break;
				case FREDDEAD:
					sprite[sn].picnum = FREDDEAD;
					sprite[sn].cstat &= ~3;
					engine.changespritestat(sn, DEAD);
					addscore(aiGetPlayerTarget(sn), 40);
					break;
				case GOBLINDEAD:
					sprite[sn].picnum = GOBLINDEAD;
					sprite[sn].cstat &= ~3;
					engine.changespritestat(sn, DEAD);

					PLAYER p = aiGetPlayerTarget(sn);
					if(p != null) addscore(p, 25);

					if ((engine.rand() % 100) > 60)
						monsterweapon(sn);
					break;
				case MINOTAURDEAD:
					sprite[sn].picnum = MINOTAURDEAD;
					sprite[sn].cstat &= ~3;
					engine.changespritestat(sn, DEAD);
					addscore(aiGetPlayerTarget(sn), game.WH2 ? 95 : 70);
					if ((engine.rand() % 100) > 60)
						monsterweapon(sn);
					break;
				case SPIDERDEAD:
					sprite[sn].picnum = SPIDERDEAD;
					sprite[sn].cstat &= ~3;
					engine.changespritestat(sn, DEAD);
					addscore(aiGetPlayerTarget(sn), 5);
					break;
				case SKULLYDEAD:
					sprite[sn].picnum = SKULLYDEAD;
					sprite[sn].cstat &= ~3;
					engine.changespritestat(sn, DEAD);
					addscore(aiGetPlayerTarget(sn), 100);
					break;
				case FATWITCHDEAD:
					sprite[sn].picnum = FATWITCHDEAD;
					sprite[sn].cstat &= ~3;
					engine.changespritestat(sn, DEAD);
					addscore(aiGetPlayerTarget(sn), 900);
					break;
				case JUDYDEAD:
					sprite[sn].picnum = JUDYDEAD;
					sprite[sn].cstat &= ~3;
					engine.changespritestat(sn, DEAD);
					spawnapentagram(sn);
					addscore(aiGetPlayerTarget(sn), 7000);
					break;
				case WILLOWEXPLO + 2:
					sprite[sn].pal = 0;
					sprite[sn].cstat &= ~3;
					engine.changespritestat(sn, (short) 0);
					engine.deletesprite(sn);
					addscore(aiGetPlayerTarget(sn), game.WH2 ? 15 : 150);
					return;
				}
			}

			engine.getzrange(sprite[sn].x, sprite[sn].y, sprite[sn].z - 1, sprite[sn].sectnum,
					(sprite[sn].clipdist) << 2, CLIPMASK0);
			sprite[sn].z = zr_florz;

			if ((zr_florhit & kHitTypeMask) == kHitSector) {
				if (sprite[sn].sectnum != MAXSECTORS && (sector[sprite[sn].sectnum].floorpicnum == WATER
						|| sector[sprite[sn].sectnum].floorpicnum == SLIME)) {
					if (sprite[sn].picnum == MINOTAURDEAD) {
						sprite[sn].z += (8 << 8);
						engine.setsprite(sn, sprite[sn].x, sprite[sn].y, sprite[sn].z);
					}
				}
				if (sprite[sn].sectnum != MAXSECTORS && (sector[sprite[sn].sectnum].floorpicnum == LAVA
						|| sector[sprite[sn].sectnum].floorpicnum == LAVA1
						|| sector[sprite[sn].sectnum].floorpicnum == LAVA2)) {
					trailingsmoke(sn, true);
					engine.deletesprite(sn);
				}
			}
			break;
		}
		//
		// the control variable for monster release
		//
	}

	public static void makeafire(int i, int firetype) {
		short j = engine.insertsprite(sprite[i].sectnum, FIRE);
		game.pInt.setsprinterpolate(j, sprite[j]);

		sprite[j].x = sprite[i].x + (engine.krand() & 1024) - 512;
		sprite[j].y = sprite[i].y + (engine.krand() & 1024) - 512;
		sprite[j].z = sprite[i].z;

		sprite[j].cstat = 0;
		sprite[j].xrepeat = 64;
		sprite[j].yrepeat = 64;

		sprite[j].shade = 0;

		sprite[j].clipdist = 64;
		sprite[j].owner = sprite[i].owner;
		sprite[j].lotag = 2047;
		sprite[j].hitag = 0;
		engine.changespritestat(j, FIRE);
	}

	public static void explosion(int i, int x, int y, int z, int owner) {
		int j = engine.insertsprite(sprite[i].sectnum, EXPLO);

		boolean isWH2 = game.WH2;

		if(!isWH2) {
			sprite[j].x = x + (engine.krand() & 1024) - 512;
			sprite[j].y = y + (engine.krand() & 1024) - 512;
			sprite[j].z = z;
		} else {
			sprite[j].x = x;
			sprite[j].y = y;
			sprite[j].z = z + (16 << 8);
		}

		game.pInt.setsprinterpolate(j, sprite[j]);

		sprite[j].cstat = 0; // Hitscan does not hit smoke on wall
		sprite[j].cstat &= ~3;
		sprite[j].shade = -15;
		sprite[j].xrepeat = 64;
		sprite[j].yrepeat = 64;
		sprite[j].ang = (short) (engine.krand() & 2047);
		sprite[j].xvel = (short) ((engine.krand() & 511) - 256);
		sprite[j].yvel = (short) ((engine.krand() & 511) - 256);
		sprite[j].zvel = (short) ((engine.krand() & 511) - 256);
		sprite[j].owner = sprite[i].owner;
		sprite[j].hitag = 0;
		sprite[j].pal = 0;
		if(!isWH2) {
			sprite[j].picnum = MONSTERBALL;
			sprite[j].lotag = 256;
		} else {
			sprite[j].picnum = EXPLOSTART;
			sprite[j].lotag = 12;
		}
	}

	public static void explosion2(int i, int x, int y, int z, int owner) {
		int j = engine.insertsprite(sprite[i].sectnum, EXPLO);

		boolean isWH2 = game.WH2;

		if(!isWH2) {
			sprite[j].x = x + (engine.krand() & 256) - 128;
			sprite[j].y = y + (engine.krand() & 256) - 128;
			sprite[j].z = z;
		} else {
			sprite[j].x = x;
			sprite[j].y = y;
			sprite[j].z = z + (16 << 8);
		}

		game.pInt.setsprinterpolate(j, sprite[j]);

		sprite[j].cstat = 0;
		sprite[j].cstat &= ~3;

		sprite[j].shade = -25;
		sprite[j].xrepeat = 64;
		sprite[j].yrepeat = 64;
		sprite[j].ang = (short) (engine.krand() & 2047);
		sprite[j].xvel = (short) ((engine.krand() & 256) - 128);
		sprite[j].yvel = (short) ((engine.krand() & 256) - 128);
		sprite[j].zvel = (short) ((engine.krand() & 256) - 128);
		sprite[j].owner = sprite[i].owner;
		sprite[j].hitag = 0;
		sprite[j].pal = 0;

		if(!isWH2) {
			sprite[j].picnum = MONSTERBALL;
			sprite[j].lotag = 128;
		} else {
			sprite[j].picnum = EXPLOSTART;
			sprite[j].lotag = 12;
		}

	}

	public static void trailingsmoke(int i, boolean ball) {
		int j = engine.insertsprite(sprite[i].sectnum, SMOKE);

		sprite[j].x = sprite[i].x;
		sprite[j].y = sprite[i].y;
		sprite[j].z = sprite[i].z;

		game.pInt.setsprinterpolate(j, sprite[j]);
		sprite[j].cstat = 0x03;
		sprite[j].cstat &= ~3;
		sprite[j].picnum = SMOKEFX;
		sprite[j].shade = 0;
		if (ball) {
			sprite[j].xrepeat = 128;
			sprite[j].yrepeat = 128;
		} else {
			sprite[j].xrepeat = 32;
			sprite[j].yrepeat = 32;
		}
		sprite[j].pal = 0;

		sprite[j].owner = sprite[i].owner;
		sprite[j].lotag = 256;
		sprite[j].hitag = 0;
	}

	public static void icecubes(int i, int x, int y, int z, int owner) {
		int j = engine.insertsprite(sprite[i].sectnum, FX);

		sprite[j].x = x;
		sprite[j].y = y;

		sprite[j].z = sector[sprite[i].sectnum].floorz - (getPlayerHeight() << 8) + (engine.krand() & 4096);

		game.pInt.setsprinterpolate(j, sprite[j]);

		sprite[j].cstat = 0; // Hitscan does not hit smoke on wall
		sprite[j].picnum = ICECUBE;
		sprite[j].shade = -16;
		sprite[j].xrepeat = 16;
		sprite[j].yrepeat = 16;

		sprite[j].ang = (short) (((engine.krand() & 1023) - 1024) & 2047);
		sprite[j].xvel = (short) ((engine.krand() & 1023) - 512);
		sprite[j].yvel = (short) ((engine.krand() & 1023) - 512);
		sprite[j].zvel = (short) ((engine.krand() & 1023) - 512);

		sprite[j].pal = 6;
		sprite[j].owner = sprite[i].owner;

		if(game.WH2)
			sprite[j].lotag = 2048;
		else sprite[j].lotag = 999;
		sprite[j].hitag = 0;

	}

	public static boolean damageactor(PLAYER plr, int hitobject, short i) {
		short j = (short) (hitobject & 4095); // j is the spritenum that the bullet (spritenum i) hit
		if (j == plr.spritenum && sprite[i].owner == sprite[plr.spritenum].owner)
			return false;

		if (j == plr.spritenum && sprite[i].owner != sprite[plr.spritenum].owner) {
			if (plr.invincibletime > 0 || plr.godMode) {
				engine.deletesprite(i);
				return false;
			}
			// EG 17 Oct 2017: Mass backport of RULES.CFG behavior for resist/onyx ring
			// EG 21 Aug 2017: New RULES.CFG behavior in place of the old #ifdef
			if (plr.manatime > 0) {
				if (/* eg_resist_blocks_traps && */sprite[i].picnum != FATSPANK && sprite[i].picnum != PLASMA) {
					engine.deletesprite(i);
					return false;
				}
				// Use "fixed" version: EXPLOSION and MONSTERBALL are the fire attacks, account
				// for animation
				// FATSPANK is right in the middle of this group of tiles, so still keep an
				// exception for it.
				else if ((sprite[i].picnum >= EXPLOSION && sprite[i].picnum <= (MONSTERBALL + 2)
						&& sprite[i].picnum != FATSPANK)) {
					engine.deletesprite(i);
					return false;
				}
			}
			// EG 21 Aug 2017: onyx ring
			else if (plr.treasure[TONYXRING] == 1 /*&& eg_onyx_effect != 0*/
					&& ((sprite[i].picnum < EXPLOSION || sprite[i].picnum > MONSTERBALL + 2)
							&& sprite[i].picnum != PLASMA)) {

//				if (eg_onyx_effect == 1 || (eg_onyx_effect == 2 && ((engine.krand() & 32) > 16))) {
					engine.deletesprite(i);
					return false;
//				}
			}
			// EG 21 Aug 2017: Move this here so as not to make ouch sounds unless pain is
			// happening
			if ((engine.krand() & 9) == 0)
				playsound_loc(S_PLRPAIN1 + (engine.rand() % 2), sprite[i].x, sprite[i].y);

			if (game.WH2 && sprite[i].picnum == DART) {
				plr.poisoned = 1;
				plr.poisontime = 7200;
				showmessage("Poisoned", 360);
            }

			if (netgame) {
//						netdamageactor(j,i);
			} else {
				if (sprite[i].picnum == PLASMA)
					addhealth(plr, -((engine.krand() & 15) + 15));
				else if (sprite[i].picnum == FATSPANK) {
					playsound_loc(S_GORE1A + (engine.krand() % 3), plr.x, plr.y);
					addhealth(plr, -((engine.krand() & 10) + 10));
					if ((engine.krand() % 100) > 90) {
						plr.poisoned = 1;
						plr.poisontime = 7200;
						showmessage("Poisoned", 360);
					}
				} else if (sprite[i].picnum == THROWPIKE) {
					addhealth(plr, -((engine.krand() % 10) + 5));
				} else
					addhealth(plr, -((engine.krand() & 20) + 5));
			}

			startredflash(10);
			/* EG 2017 - Trap fix */
			engine.deletesprite(i);
			return true;
		}

		if (j != plr.spritenum && !netgame) // Les 08/11/95
			if (sprite[i].owner != j) {

//				public static final int DEMONTYPE = 1; XXX
//				public static final int DRAGONTYPE = 3;
//				public static final int FISHTYPE = 5;
//				public static final int JUDYTYPE = 12;
//				public static final int RATTYPE = 18;
//				public static final int SKULLYTYPE = 20;

				switch (sprite[j].detail) {
				case NEWGUYTYPE:
				case KURTTYPE:
				case GONZOTYPE:
				case KATIETYPE:
				case GRONTYPE:
				case KOBOLDTYPE:
				case DEVILTYPE:
				case FREDTYPE:
				case GOBLINTYPE:
				case IMPTYPE:
				case MINOTAURTYPE:
				case SPIDERTYPE:
				case SKELETONTYPE:
				case FATWITCHTYPE:
				case WILLOWTYPE:
				case GUARDIANTYPE:
					if(isBlades(sprite[i].picnum)) {
						sprite[j].hitag -= 30;
						if(sprite[i].picnum == THROWPIKE) {
							if ((engine.krand() % 2) != 0)
								playsound_loc(S_GORE1A + engine.krand() % 2, sprite[i].x, sprite[i].y);
						}
					} else {
					switch (sprite[i].picnum) {
						case PLASMA:
							sprite[j].hitag -= 40;
							break;
						case FATSPANK:
							sprite[j].hitag -= 10;
							break;
						case MONSTERBALL:
							sprite[j].hitag -= 40;
							break;
						case FIREBALL:
							if(!game.WH2)
								sprite[j].hitag -= 3;
							break;
						case BULLET:
							sprite[j].hitag -= 10;
							break;
						case DISTORTIONBLAST:
							sprite[j].hitag = 10;
							break;
						case BARREL:
							sprite[j].hitag -= 100;
							break;
						}
					}

					if (sprite[j].hitag <= 0) {
						newstatus(j, DIE);
						engine.deletesprite(i);
					} else
						newstatus(j, PAIN);
					return true;
				}

				switch(sprite[j].detail) {
				case GONZOTYPE:
				case NEWGUYTYPE:
				case KATIETYPE:
				case GRONTYPE:
				case KOBOLDTYPE:
				case DEVILTYPE:
				case FREDTYPE:
				case GOBLINTYPE:
				case MINOTAURTYPE:
				case SPIDERTYPE:
				case SKELETONTYPE:
				case IMPTYPE:
					// JSA_NEW //why is this here it's in whplr
					// raf because monsters could shatter a guy thats been frozen
					if (sprite[j].pal == 6) {
						for (int k = 0; k < 32; k++)
							icecubes(j, sprite[j].x, sprite[j].y, sprite[j].z, j);
						// EG 26 Oct 2017: Move this here from medusa (anti multi-freeze exploit)
						addscore(plr, 100);
						engine.deletesprite(j);
					}
					return true;
				}

				switch (sprite[j].picnum) {
				case BARREL:
				case VASEA:
				case VASEB:
				case VASEC:
				case STAINGLASS1:
				case STAINGLASS2:
				case STAINGLASS3:
				case STAINGLASS4:
				case STAINGLASS5:
				case STAINGLASS6:
				case STAINGLASS7:
				case STAINGLASS8:
				case STAINGLASS9:
					sprite[j].hitag = 0;
					sprite[j].lotag = 0;
					newstatus(j, BROKENVASE);
					break;
				default:
					engine.deletesprite(i);
					return true;
				}
			}

		return false;
	}

	public static final int NORMALCLIP = 0;
	public static final int PROJECTILECLIP = 1;
	public static final int CLIFFCLIP = 2;

	public static int movesprite(short spritenum, int dx, int dy, int dz, int ceildist, int flordist, int cliptype) {

		int daz, zoffs;
		int retval;
		short tempshort, dasectnum;

		SPRITE spr = sprite[spritenum];
		if (spr.statnum == MAXSTATUS)
			return (-1);

		game.pInt.setsprinterpolate(spritenum, spr);

		int dcliptype = 0;
		switch (cliptype) {
		case NORMALCLIP:
			dcliptype = CLIPMASK0;
			break;
		case PROJECTILECLIP:
			dcliptype = CLIPMASK1;
			break;
		case CLIFFCLIP:
			dcliptype = CLIPMASK0;
			break;
		}

		if ((spr.cstat & 128) == 0)
			zoffs = -((engine.getTile(spr.picnum).getHeight() * spr.yrepeat) << 1);
		else
			zoffs = 0;

		dasectnum = spr.sectnum; // Can't modify sprite sectors directly becuase of linked lists
		daz = spr.z + zoffs; // Must do this if not using the new centered centering (of course)
		retval = engine.clipmove(spr.x, spr.y, daz, dasectnum, dx, dy, (spr.clipdist) << 2, ceildist, flordist,
				dcliptype);

		if (clipmove_sectnum != -1) {
			spr.x = clipmove_x;
			spr.y = clipmove_y;
			daz = clipmove_z;
			dasectnum = clipmove_sectnum;
		}

		engine.pushmove(spr.x, spr.y, daz, dasectnum, spr.clipdist << 2, ceildist, flordist, CLIPMASK0);
		if(pushmove_sectnum != -1) {
			spr.x = pushmove_x;
			spr.y = pushmove_y;
			daz = pushmove_z;
			dasectnum = pushmove_sectnum;
		}

		if ((dasectnum != spr.sectnum) && (dasectnum >= 0))
			engine.changespritesect(spritenum, dasectnum);

		// Set the blocking bit to 0 temporarly so getzrange doesn't pick up
		// its own sprite
		tempshort = spr.cstat;
		spr.cstat &= ~1;
		engine.getzrange(spr.x, spr.y, spr.z - 1, spr.sectnum, (spr.clipdist) << 2, dcliptype);
		spr.cstat = tempshort;

		daz = spr.z + zoffs + dz;
		if ((daz <= zr_ceilz) || (daz > zr_florz)) {
			if (retval != 0)
				return (retval);
			return (16384 | dasectnum);
		}
		spr.z = (daz - zoffs);
		return (retval);
	}

	public static void trowajavlin(int s) {
		int j = engine.insertsprite(sprite[s].sectnum, JAVLIN);

		sprite[j].x = sprite[s].x;
		sprite[j].y = sprite[s].y;
		sprite[j].z = sprite[s].z;// - (40 << 8);

		game.pInt.setsprinterpolate(j, sprite[j]);

		sprite[j].cstat = 21;

		switch (sprite[s].lotag) {
		case 91:
			sprite[j].picnum = WALLARROW;
			sprite[j].ang = (short) (((sprite[s].ang + 2048) - 512) & 2047);
			sprite[j].xrepeat = 16;
			sprite[j].yrepeat = 48;
			sprite[j].clipdist = 24;
			break;
		case 92:
			sprite[j].picnum = DART;
			sprite[j].ang = (short) (((sprite[s].ang + 2048) - 512) & 2047);
			sprite[j].xrepeat = 64;
			sprite[j].yrepeat = 64;
			sprite[j].clipdist = 16;
			break;
		case 93:
			sprite[j].picnum = HORIZSPIKEBLADE;
			sprite[j].ang = (short) (((sprite[s].ang + 2048) - 512) & 2047);
			sprite[j].xrepeat = 16;
			sprite[j].yrepeat = 48;
			sprite[j].clipdist = 32;
			break;
		case 94:
			sprite[j].picnum = THROWPIKE;
			sprite[j].ang = (short) (((sprite[s].ang + 2048) - 512) & 2047);
			sprite[j].xrepeat = 24;
			sprite[j].yrepeat = 24;
			sprite[j].clipdist = 32;
			break;
		}

		sprite[j].extra = sprite[s].ang;
		sprite[j].shade = -15;
		sprite[j].xvel = (short) ((engine.krand() & 256) - 128);
		sprite[j].yvel = (short) ((engine.krand() & 256) - 128);
		sprite[j].zvel = (short) ((engine.krand() & 256) - 128);

		sprite[j].owner = 0;
		sprite[j].lotag = 0;
		sprite[j].hitag = 0;
		sprite[j].pal = 0;
	}

	public static void spawnhornskull(short i) {
		short j = engine.insertsprite(sprite[i].sectnum, (short) 0);
		sprite[j].x = sprite[i].x;
		sprite[j].y = sprite[i].y;
		sprite[j].z = sprite[i].z - (24 << 8);

		game.pInt.setsprinterpolate(j, sprite[j]);

		sprite[j].shade = -15;
		sprite[j].cstat = 0;
		sprite[j].cstat &= ~3;
		sprite[j].pal = 0;
		sprite[j].picnum = HORNEDSKULL;
		sprite[j].detail = HORNEDSKULLTYPE;
		sprite[j].xrepeat = 64;
		sprite[j].yrepeat = 64;
	}

	public static void spawnapentagram(int sn) {
		short j = engine.insertsprite(sprite[sn].sectnum, (short) 0);

		sprite[j].x = sprite[sn].x;
		sprite[j].y = sprite[sn].y;
		sprite[j].z = sprite[sn].z - (8 << 8);

		game.pInt.setsprinterpolate(j, sprite[j]);

		sprite[j].xrepeat = sprite[j].yrepeat = 64;
		sprite[j].pal = 0;
		sprite[j].shade = -15;
		sprite[j].cstat = 0;
		sprite[j].clipdist = 64;
		sprite[j].lotag = 0;
		sprite[j].hitag = 0;
		sprite[j].extra = 0;
		sprite[j].picnum = PENTAGRAM;
		sprite[j].detail = PENTAGRAMTYPE;

		engine.setsprite(j, sprite[j].x, sprite[j].y, sprite[j].z);
	}
}
