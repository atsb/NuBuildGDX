package ru.m210projects.Witchaven;

import static ru.m210projects.Build.Engine.sprite;
import static ru.m210projects.Build.Gameutils.*;
import static ru.m210projects.Build.Strhandler.*;
import static ru.m210projects.Witchaven.Globals.*;
import static ru.m210projects.Witchaven.Main.engine;
import static ru.m210projects.Witchaven.Main.game;
import static ru.m210projects.Witchaven.Names.*;
import static ru.m210projects.Witchaven.WH1Names.*;
import static ru.m210projects.Witchaven.WH2Names.*;
import static ru.m210projects.Witchaven.WHScreen.*;
import static ru.m210projects.Witchaven.WHOBJ.*;
import static ru.m210projects.Witchaven.WHSND.*;
import static ru.m210projects.Witchaven.WHPLR.*;
import static ru.m210projects.Witchaven.Weapons.*;
import static ru.m210projects.Witchaven.AI.Ai.*;

import ru.m210projects.Build.Pattern.BuildFont.TextAlign;
import ru.m210projects.Witchaven.Types.PLAYER;
import ru.m210projects.Witchaven.Types.WEAPON;

public class Spellbooks {

	public static final WEAPON[][] sspellbookanim = {
			// SCARE
			{ new WEAPON(8, SSPELLBOOK8, -78, -96), new WEAPON(8, SSPELLBOOK8 + 1, -78, -104),
					new WEAPON(8, SSPELLBOOK8 + 2, -78, -100), new WEAPON(8, SSPELLBOOK8 + 3, -78, -98),
					new WEAPON(8, SSPELLBOOK8 + 4, -78, -99), new WEAPON(8, SSPELLBOOK8 + 5, -78, -99),
					new WEAPON(8, SSPELLBOOK8 + 6, -78, -99), new WEAPON(8, SSPELLBOOK8 + 7, -78, -98),
					new WEAPON(8, SSPELLBOOK8 + 7, -78, -98), },
			// NIGHT VISION
			{ new WEAPON(8, SSPELLBOOK6, -78, -96), new WEAPON(8, SSPELLBOOK6 + 1, -78, -104),
					new WEAPON(8, SSPELLBOOK6 + 2, -78, -101), new WEAPON(8, SSPELLBOOK6 + 3, -78, -99),
					new WEAPON(8, SSPELLBOOK6 + 4, -78, -98), new WEAPON(8, SSPELLBOOK6 + 5, -78, -99),
					new WEAPON(8, SSPELLBOOK6 + 6, -78, -99), new WEAPON(8, SSPELLBOOK6 + 7, -78, -98),
					new WEAPON(8, SSPELLBOOK6 + 7, -78, -98), },
			// FREEZE
			{ new WEAPON(8, SSPELLBOOK3, -78, -99), new WEAPON(8, SSPELLBOOK3 + 1, -78, -104),
					new WEAPON(8, SSPELLBOOK3 + 2, -78, -101), new WEAPON(8, SSPELLBOOK3 + 3, -78, -99),
					new WEAPON(8, SSPELLBOOK3 + 4, -78, -98), new WEAPON(8, SSPELLBOOK3 + 5, -78, -97),
					new WEAPON(8, SSPELLBOOK3 + 6, -78, -99), new WEAPON(8, SSPELLBOOK3 + 7, -78, -99),
					new WEAPON(8, SSPELLBOOK3 + 7, -78, -99), },
			// MAGIC ARROW
			{ new WEAPON(8, SSPELLBOOKBLANK, -78, -98), new WEAPON(8, SSPELLBOOKBLANK + 1, -78, -104),
					new WEAPON(8, SSPELLBOOKBLANK + 2, -78, -101), new WEAPON(8, SSPELLBOOKBLANK + 3, -78, -99),
					new WEAPON(8, SSPELLBOOKBLANK + 4, -78, -98), new WEAPON(8, SSPELLBOOKBLANK + 5, -78, -97),
					new WEAPON(8, SSPELLBOOKBLANK + 6, -78, -97), new WEAPON(8, SSPELLBOOKBLANK + 7, -78, -97),
					new WEAPON(8, SSPELLBOOKBLANK + 7, -78, -97), },
			// OPEN DOORS
			{ new WEAPON(8, SSPELLBOOK7, -78, -98), new WEAPON(8, SSPELLBOOK7 + 1, -78, -104),
					new WEAPON(8, SSPELLBOOK7 + 2, -78, -101), new WEAPON(8, SSPELLBOOK7 + 3, -78, -99),
					new WEAPON(8, SSPELLBOOK7 + 4, -78, -99), new WEAPON(8, SSPELLBOOK7 + 5, -78, -99),
					new WEAPON(8, SSPELLBOOK7 + 6, -78, -97), new WEAPON(8, SSPELLBOOK7 + 7, -78, -98),
					new WEAPON(8, SSPELLBOOK7 + 7, -78, -98), },
			// FLY
			{ new WEAPON(8, SSPELLBOOK2, -78, -98), new WEAPON(8, SSPELLBOOK2 + 1, -78, -104),
					new WEAPON(8, SSPELLBOOK2 + 2, -78, -101), new WEAPON(8, SSPELLBOOK2 + 3, -78, -99),
					new WEAPON(8, SSPELLBOOK2 + 4, -78, -96), new WEAPON(8, SSPELLBOOK2 + 5, -78, -98),
					new WEAPON(8, SSPELLBOOK2 + 6, -78, -96), new WEAPON(8, SSPELLBOOK2 + 7, -78, -98),
					new WEAPON(8, SSPELLBOOK2 + 7, -78, -98), },
			// FIRE BALL
			{ new WEAPON(8, SSPELLBOOK4, -78, -97), new WEAPON(8, SSPELLBOOK4 + 1, -78, -104),
					new WEAPON(8, SSPELLBOOK4 + 2, -78, -101), new WEAPON(8, SSPELLBOOK4 + 3, -78, -99),
					new WEAPON(8, SSPELLBOOK4 + 4, -78, -94), new WEAPON(8, SSPELLBOOK4 + 5, -78, -95),
					new WEAPON(8, SSPELLBOOK4 + 6, -78, -97), new WEAPON(8, SSPELLBOOK4 + 6, -78, -97),
					new WEAPON(8, SSPELLBOOK4 + 6, -78, -97), },
			// NUKE!
			{ new WEAPON(8, SSPELLBOOK5, -78, -99), new WEAPON(8, SSPELLBOOK5 + 1, -78, -104),
					new WEAPON(8, SSPELLBOOK5 + 2, -78, -99), new WEAPON(8, SSPELLBOOK5 + 3, -78, -99),
					new WEAPON(8, SSPELLBOOK5 + 4, -78, -98), new WEAPON(8, SSPELLBOOK5 + 5, -78, -96),
					new WEAPON(8, SSPELLBOOK5 + 6, -78, -100), new WEAPON(8, SSPELLBOOK5 + 6, -78, -100),
					new WEAPON(8, SSPELLBOOK5 + 6, -78, -100) } };

	public static void activatedaorb(PLAYER plr) {
		if (plr.orbammo[plr.currentorb] <= 0)
			return;

		switch (plr.currentorb) {
		case 0: // SCARE
			// shadowtime=1200+(plr.lvl*120);
			break;
		case 1: // NIGHT VISION
			// nightglowtime=2400+(plr.lvl*600);
			break;
		case 2: // FREEZE
			plr.orbactive[plr.currentorb] = -1;
			break;
		case 3: // MAGIC ARROW
			plr.orbactive[plr.currentorb] = -1;
			break;
		case 4: // OPEN DOORS
			plr.orbactive[plr.currentorb] = -1;
			break;
		case 5: // FLY
			// plr.orbactive[currentorb]=3600+(plr.lvl*600);
			break;
		case 6: // FIREBALL
			plr.orbactive[plr.currentorb] = -1;
			break;
		case 7: // NUKE
			plr.orbactive[plr.currentorb] = -1;
			break;
		}

		if (plr.orbammo[plr.currentorb] <= 0) {
			plr.orb[plr.currentorb] = 0;
			return;
		} else
			plr.orbammo[plr.currentorb]--;

		plr.currweaponfired = 4;
		plr.currweapontics = game.WH2 ? wh2throwanimtics[plr.currentorb][0].daweapontics : throwanimtics[plr.currentorb][0].daweapontics;
	}

	public static void castaorb(PLAYER plr) {
		int k;
		float daang;

		switch (plr.currentorb) {
		case 0: // SCARE
			if (game.WH2)
				playsound_loc(S_GENERALMAGIC4, plr.x, plr.y);
			plr.shadowtime = ((plr.lvl + 1) * 120) << 2;
			break;
		case 1: // NIGHTVISION
			plr.nightglowtime = 3600 + (plr.lvl * 120);
			break;
		case 2: // FREEZE
			if (game.WH2)
				playsound_loc(S_GENERALMAGIC3, plr.x, plr.y);
			else
				playsound_loc(S_SPELL1, plr.x, plr.y);
			daang = plr.ang;
			shootgun(plr, daang, 6);
			break;
		case 3: // MAGIC ARROW
			if (game.WH2) {
				lockon(plr,10,2);
				playsound_loc(S_GENERALMAGIC2, plr.x, plr.y);
			}
			else {
				daang = BClampAngle(plr.ang - 36);
				for (k = 0; k < 10; k++) {
					daang = BClampAngle(daang + (k << 1));
					shootgun(plr, daang, 2);
				}
				playsound_loc(S_SPELL1, plr.x, plr.y);
			}
			break;
		case 4: // OPEN DOORS
			daang = plr.ang;
			shootgun(plr, daang, 7);
			if (game.WH2)
				playsound_loc(S_DOORSPELL, plr.x, plr.y);
			else
				playsound_loc(S_SPELL1, plr.x, plr.y);
			break;
		case 5: // FLY
			plr.orbactive[plr.currentorb] = 3600 + (plr.lvl * 120);
			if (game.WH2)
				playsound_loc(S_GENERALMAGIC1, plr.x, plr.y);
			else
				playsound_loc(S_SPELL1, plr.x, plr.y);
			break;
		case 6: // FIREBALL
			if (game.WH2) {
				lockon(plr,3,3);
				playsound_loc(S_FIRESPELL, plr.x, plr.y);
			}
			else {
				daang = plr.ang;
				shootgun(plr, daang, 3);
				playsound_loc(S_SPELL1, plr.x, plr.y);
			}
			break;
		case 7: // NUKE
			daang = plr.ang;
			shootgun(plr, daang, 4);
			if (game.WH2)
				playsound_loc(S_NUKESPELL, plr.x, plr.y);
			else
				playsound_loc(S_SPELL1, plr.x, plr.y);
			break;
		}
	}
	
	public static void spellswitch(PLAYER plr, int j)
	{
		int i = plr.currentorb;
		while(i >= 0 && i < MAXNUMORBS) {
			i += j;
			
			if(i == -1) i = MAXNUMORBS - 1;
            else if(i == MAXNUMORBS) i = 0;
			
			if(plr.spellbookflip != 0 || i == plr.currentorb)
				break;
			
			if (changebook(plr, i)) {
				displayspelltext(plr);
				plr.spelltime = 360;
				break;
			} 
		}
	}

	public static void bookprocess(int snum) {
		PLAYER plr = player[snum];
		
		if (plr.currweaponanim == 0 && plr.currweaponflip == 0) {
			int bits = plr.pInput.bits;
			int spell = ((bits & (15 << 12)) >> 12) - 1;
	
			if(spell != -1 && spell < 10)
			{
				if(spell != 9 && spell != 8) {
					if (changebook(plr, spell)) {
						displayspelltext(plr);
						plr.spelltime = 360;
					} else return;
				} else 
					spellswitch(plr, spell == 9 ? -1 : 1);
				plr.orbshot = 0;
			}
		}

		for (int j = 0; j < MAXNUMORBS; j++) {
			if (plr.orbactive[j] > -1) {
				plr.orbactive[j] -= TICSPERFRAME;
			}
		}
	}

	public static boolean changebook(PLAYER plr, int i) {
		if(plr.orbammo[i] <= 0 || plr.currentorb == i)
			return false;
		plr.currentorb = i;
		if (plr.spellbookflip == 0) {
			plr.spellbook = 0;
			plr.spellbooktics = 10;
			plr.spellbookflip = 1;
			SND_Sound(S_PAGE);
			return true;
		}
		return false;
	}

	public static boolean lvlspellcheck(PLAYER plr) {
		if(game.WH2) return true;
		
		switch (plr.currentorb) {
		case 0:
		case 1:
			return true;
		case 2:
			if (plr.lvl > 1)
				return true;
			else
				showmessage("must attain 2nd level", 360);
			break;
		case 3:
			if (plr.lvl > 1)
				return true;
			else
				showmessage("must attain 2nd level", 360);
			break;
		case 4:
			if (plr.lvl > 2)
				return true;
			else
				showmessage("must attain 3rd level", 360);
			break;
		case 5:
			if (plr.lvl > 2)
				return true;
			else
				showmessage("must attain 3rd level", 360);

			break;
		case 6:
			if (plr.lvl > 3)
				return true;
			else
				showmessage("must attain 4th level", 360);
			break;
		case 7:
			if (plr.lvl > 4)
				return true;
			else
				showmessage("must attain 5th level", 360);
			break;
		}
		return false;
	}

	public static void speelbookprocess(PLAYER plr) {
		if (plr.spelltime > 0)
			plr.spelltime -= TICSPERFRAME;

		if (plr.spellbookflip == 1) {
			plr.spellbooktics -= TICSPERFRAME;
			if (plr.spellbooktics < 0)
				plr.spellbook++;
			if (plr.spellbook > 8)
				plr.spellbook = 8;
			if (plr.spellbook == 8)
				plr.spellbookflip = 0;
		}
	}

	public static void nukespell(PLAYER plr, short j) {
		if(sprite[j].detail != WILLOWTYPE && sprite[j].pal == 6) //don't nuke freezed enemies
			return;

		if (game.WH2) {
			// dont nuke a shade
			if (sprite[j].shade > 30)
				return;

			newstatus(j, NUKED);
			sprite[j].pal = 0;
			sprite[j].cstat |= 1;
			sprite[j].cstat &= ~3;
			sprite[j].shade = 6;
			sprite[j].lotag = 360;
			sprite[j].ang = (short) plr.ang;
			sprite[j].hitag = 0;
			addscore(plr, 150);

			int k = engine.insertsprite(sprite[j].sectnum, NUKED);
			sprite[k].lotag = 360;
			sprite[k].xrepeat = 30;
			sprite[k].yrepeat = 12;
			sprite[k].picnum = ZFIRE;
			sprite[k].pal = 0;
			sprite[k].ang = sprite[j].ang;
			sprite[k].x = sprite[j].x;
			sprite[k].y = sprite[j].y;
			sprite[k].z = sprite[j].z;
			sprite[k].cstat = sprite[j].cstat;

			return;
		}

		switch (sprite[j].detail) {
		case WILLOWTYPE:
		case SPIDERTYPE:
			engine.deletesprite((short) j);
			addscore(plr, 10);
			break;
		case KOBOLDTYPE:
			sprite[j].picnum = KOBOLDCHAR;
			newstatus(j, NUKED);
			sprite[j].pal = 0;
			sprite[j].cstat |= 1;
			addscore(plr, 150);
			break;
		case DEVILTYPE:
			sprite[j].picnum = DEVILCHAR;
			newstatus(j, NUKED);
			sprite[j].pal = 0;
			sprite[j].cstat |= 1;
			addscore(plr, 150);
			break;
		case GOBLINTYPE:
		case IMPTYPE:
			sprite[j].picnum = GOBLINCHAR;
			newstatus(j, NUKED);
			sprite[j].pal = 0;
			sprite[j].cstat |= 1;
			addscore(plr, 150);
			break;
		case MINOTAURTYPE:
			sprite[j].picnum = MINOTAURCHAR;
			newstatus(j, NUKED);
			sprite[j].pal = 0;
			sprite[j].cstat |= 1;
			addscore(plr, 150);
			break;
		case SKELETONTYPE:
			sprite[j].picnum = SKELETONCHAR;
			newstatus(j, NUKED);
			sprite[j].pal = 0;
			sprite[j].cstat |= 1;
			addscore(plr, 150);
			break;
		case GRONTYPE:
			sprite[j].picnum = GRONCHAR;
			newstatus(j, NUKED);
			sprite[j].pal = 0;
			sprite[j].cstat |= 1;
			addscore(plr, 150);
			break;
		case DRAGONTYPE:
			sprite[j].picnum = DRAGONCHAR;
			newstatus(j, NUKED);
			sprite[j].pal = 0;
			sprite[j].cstat |= 1;
			addscore(plr, 150);
			break;
		case GUARDIANTYPE:
			sprite[j].picnum = GUARDIANCHAR;
			newstatus(j, NUKED);
			sprite[j].pal = 0;
			sprite[j].cstat |= 1;
			addscore(plr, 150);
			break;
		case FATWITCHTYPE:
			sprite[j].picnum = FATWITCHCHAR;
			newstatus(j, NUKED);
			sprite[j].pal = 0;
			sprite[j].cstat |= 1;
			addscore(plr, 150);
			break;
		case SKULLYTYPE:
			sprite[j].picnum = SKULLYCHAR;
			newstatus(j, NUKED);
			sprite[j].pal = 0;
			sprite[j].cstat |= 1;
			addscore(plr, 150);
			break;
		case JUDYTYPE:
			if (mapon < 24) {
				sprite[j].picnum = JUDYCHAR;
				newstatus(j, NUKED);
				sprite[j].pal = 0;
				sprite[j].cstat |= 1;
				addscore(plr, 150);
			}
			break;
		}
	}

	public static void medusa(PLAYER plr, short j) {
		if(sprite[j].hitag <= 0) //don't freeze dead enemies
			return;
		
		newstatus(j, FROZEN);
		int pic = sprite[j].picnum;
		switch (sprite[j].detail) {

		case NEWGUYTYPE:
			sprite[j].picnum = NEWGUYPAIN;
			break;
		case KURTTYPE:
			sprite[j].picnum = GONZOCSWPAIN;
			break;	
		case GONZOTYPE:
			if(pic == GONZOCSW || pic == GONZOCSWAT)
				sprite[j].picnum = GONZOCSWPAIN;
			else if(pic == GONZOGSW || pic == GONZOGSWAT)
				sprite[j].picnum = GONZOGSWPAIN;
			else if(pic == GONZOGHM || pic == GONZOGHMAT 
					|| pic == GONZOGSH || pic == GONZOGSHAT)
				sprite[j].picnum = GONZOGHMPAIN;
			break;
		case KATIETYPE:
			sprite[j].picnum = KATIEPAIN;
			break;	
		case KOBOLDTYPE:
			sprite[j].picnum = KOBOLDDIE;
			break;
		case DEVILTYPE:
			sprite[j].picnum = DEVILDIE;
			break;
		case FREDTYPE:
			sprite[j].picnum = FREDDIE;
			break;
		case GOBLINTYPE:
		case IMPTYPE:
			if(game.WH2) sprite[j].picnum = IMPDIE;
			else sprite[j].picnum = GOBLINDIE;
			break;	
		case MINOTAURTYPE:
			sprite[j].picnum = MINOTAURDIE;
			break;
		case SPIDERTYPE:
			sprite[j].picnum = SPIDERDIE;
			break;
		case SKELETONTYPE:
			sprite[j].picnum = SKELETONDIE;
			break;
		case GRONTYPE:
			if(pic == GRONHAL || pic == GRONHALATTACK)
				sprite[j].picnum = (short) GRONHALDIE;
			else if(pic == GRONMU || pic == GRONMUATTACK)
				sprite[j].picnum = (short) GRONMUDIE;
			else if(pic == GRONSW || pic == GRONSWATTACK)
				sprite[j].picnum = (short) GRONSWDIE;
			break;
		}
		sprite[j].pal = 6;
		sprite[j].cstat |= 1;
		addscore(plr, 100);
	}

	public static void displayspelltext(PLAYER plr) {
		switch (plr.currentorb) {
		case 0:
			showmessage("scare spell", 360);
			break;
		case 1:
			showmessage("night vision spell", 360);
			break;
		case 2:
			showmessage("freeze spell", 360);
			break;
		case 3:
			showmessage("magic arrow spell", 360);
			break;
		case 4:
			showmessage("open door spell", 360);
			break;
		case 5:
			showmessage("fly spell", 360);
			break;
		case 6:
			showmessage("fireball spell", 360);
			break;
		case 7:
			showmessage("nuke spell", 360);
			break;
		}
	}

	public static void orbpic(PLAYER plr, int currentorb) {
		if (plr.orbammo[currentorb] < 0)
			plr.orbammo[currentorb] = 0;

		Bitoa(plr.orbammo[currentorb], tempchar);

		int y = 382;
		if (currentorb == 2)
			y = 381;
		if (currentorb == 3)
			y = 383;
		if (currentorb == 6)
			y = 383;
		if (currentorb == 7)
			y = 380;

		int spellbookpage = sspellbookanim[currentorb][8].daweaponframe;
		overwritesprite(121 << 1, y, spellbookpage, 0, 0, 0);
		game.getFont(4).drawText(126 << 1,439, tempchar, 0, 0, TextAlign.Left, 0, false);
	}
}
