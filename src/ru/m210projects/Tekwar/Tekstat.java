package ru.m210projects.Tekwar;

import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Build.Gameutils.*;
import static ru.m210projects.Tekwar.Globals.*;
import static ru.m210projects.Build.Pragmas.*;
import static ru.m210projects.Build.Net.Mmulti.*;
import static ru.m210projects.Tekwar.Main.*;
import static ru.m210projects.Tekwar.View.*;
import static ru.m210projects.Tekwar.Names.*;
import static ru.m210projects.Tekwar.Tekchng.*;
import static ru.m210projects.Tekwar.Tekgun.*;
import static ru.m210projects.Tekwar.Tekprep.*;
import static ru.m210projects.Tekwar.Teksnd.*;
import static ru.m210projects.Tekwar.Tekspr.*;
import static ru.m210projects.Tekwar.Tektag.*;
import static ru.m210projects.Tekwar.Player.*;
import static ru.m210projects.Tekwar.Tekmap.*;
import static java.lang.Math.*;

import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.FileHandle.Resource;
import ru.m210projects.Build.FileHandle.Resource.Whence;
import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Build.Types.Tile;
import ru.m210projects.Tekwar.Types.XTsavetype;
import ru.m210projects.Tekwar.Types.XTtrailertype;

public class Tekstat {
	public static sectflashtype sectflash;
	public static SPRITE pickup;

	public static int vadd;
	public static char ensfirsttime = 1;
	public static int stackedcheck;
	public static final int MAXFRAMES = 8;

	public static final boolean NOSHOWSPRITES = false;

	public static final int SPR_LOTAG_SPAWNCHASE = 2000;
	public static final int SPR_LOTAG_PLAYBACK = 2001;
	public static final int SPR_LOTAG_PUSHABLE = 2002;
	public static final int SPR_LOTAG_MORPH = 2003;
	public static final int SPR_LOTAG_PICKUP = 2004;
	public static final int SPR_LOTAG_FLAMMABLE = 2005;
	public static final int SPR_LOTAG_PICKUP_FLAMMABLE = 2006;
	public static final int SPR_LOTAG_PUSHABLE_FLAMMABLE = 2007;

	public static final int SECT_LOTAG_TRIGGERSPRITE = 5020;
	public static final int SECT_LOTAG_SHOWMESSAGE = 5030;
	public static final int SECT_LOTAG_NOSTANDING = 5040;
	public static final int SECT_LOTAG_OFFLIMITS_CIVILLIAN = 5050;
	public static final int SECT_LOTAG_OFFLIMITS_ALL = 5055;
	public static final int SECT_LOTAG_CLIMB = 5060;
	public static final int SECT_LOTAG_SUNKEN = 5065;

	public static final int DONTBOTHERDISTANCE = 20480;
	public static final int HEARGUNSHOTDIST = 10240;

	public static final short INANIMATE = 0;
	public static final short PLAYER = 8;
	public static final short INACTIVE = 100;
	public static final short STANDING = 201;
	public static final short AMBUSH = 202;
	public static final short GUARD = 203;
	public static final short STALK = 204;
	public static final short FLEE = 205;
	public static final short CHASE = 206;
	public static final short PATROL = 207;
	public static final short CRAWL = 208;
	public static final short STROLL = 209;
	public static final short VIRUS = 250;
	public static final short PLRVIRUS = 255;
	public static final short ATTACK = 300;
	public static final short DEATH = 301;
	public static final short PAIN = 302;
	public static final short TWITCH = 303;
	public static final short MORPH = 304;
	public static final short SQUAT = 305;
	public static final short UNSQUAT = 306;
	public static final short JUMP = 307;
	public static final short LEAP = 308;
	public static final short DODGE = 309;
	public static final short UNDODGE = 310;
	public static final short HIDE = 311;
	public static final short UNHIDE = 312;
	public static final short DELAYEDATTACK = 313;
	public static final short MIRRORMAN1 = 320;
	public static final short MIRRORMAN2 = 321;
	public static final short FLOATING = 322;
	public static final short PROJHIT = 400;
	public static final short PROJECTILE = 401;
	public static final short TOSS = 402;
	public static final short PINBALL = 403;
	public static final short KINDLING = 405;
	public static final short DROPSIES = 406;
	public static final short RUNTHRU = 407;
	public static final short BLOODFLOW = 408;
	public static final short FLY = 500;
	public static final short RODENT = 502;
	public static final short TIMEBOMB = 602;
	public static final short STACKED = 610;
	public static final short FALL = 611;
	public static final short GENEXPLODE1 = 800;
	public static final short GENEXPLODE2 = 801;
	public static final short VANISH = 999;
	public static final short KAPUT = MAXSTATUS;

	public static final int FORCEPROJECTILESTAT = 710;
	public static final int DARTPROJECTILESTAT = 712;
	public static final int BOMBPROJECTILESTAT = 714;
	public static final int BOMBPROJECTILESTAT2 = 716;
	public static final int MOVEBODYPARTSSTAT = 900;

	public static final int ENEMYCRITICALCONDITION = 25;

	public static final int AI_NULL = 0x0000;
	public static final int AI_FRIEND = 0x0001;
	public static final int AI_FOE = 0x0002;
	public static final int AI_JUSTSHOTAT = 0x0004;
	public static final int AI_CRITICAL = 0x0008;
	public static final int AI_WASDRAWN = 0x0010;
	public static final int AI_WASATTACKED = 0x0020;
	public static final int AI_GAVEWARNING = 0x0040;
	public static final int AI_ENCROACHMENT = 0x0080;
	public static final int AI_DIDFLEESCREAM = 0x0100;
	public static final int AI_DIDAMBUSHYELL = 0x0200;
	public static final int AI_DIDHIDEPLEA = 0x0400;
	public static final int AI_TIMETODODGE = 0x0800;

	public static final int FX_NULL = 0x0000;
	public static final int FX_HASREDCARD = 0x0001;
	public static final int FX_HASBLUECARD = 0x0002;
	public static final int FX_ANDROID = 0x0004;
	public static final int FX_HOLOGRAM = 0x0008;

	public static final int FX_NXTSTTVANISH = 0x0010;
	public static final int FX_NXTSTTPAIN = 0x0020;
	public static final int FX_NXTSTTDEATH = 0x0040;

	public static final int NO_PIC = 0;

	public static final int NORMALCLIP = 0;
	public static final int PROJECTILECLIP = 1;
	public static final int CLIFFCLIP = 2;

	public static final int MINATTACKDIST = 8192;
	public static final int CHASEATTDIST = 8192;
	public static final int GUARDATTDIST = 6144;
	public static final int STALKATTDIST = 8192;
	public static final int SCARECIVILLIANDISTANCE = 102400;

	static int pickupclock;
	private static final XTtrailertype XTtrailer = new XTtrailertype();
	private static final XTsavetype XTsave = new XTsavetype();

	public static final int MAXBOBS = 32;
	static int bobbing[] = { 0, 2, 4, 6, 8, 10, 12, 14, 16, 14, 12, 10, 8, 6, 4, 2, 0, -2, -4, -6, -8, -10, -12, -14,
			-16, -14, -12, -10, -8, -6, -4, -2 };

	// angles, 0 east start, 22.5deg(==128scaled) resolution
	static short leftof[] = { 1920, 0, 128, 256, 384, 512, 640, 768, 896, 1024, 1152, 1280, 1408, 1536, 1664, 1792,
			1920 };

	static short rightof[] = { 128, 256, 384, 512, 640, 768, 896, 1024, 1152, 1280, 1408, 1536, 1664, 1792, 1920, 0,
			128 };

	public static short jsinsertsprite(short sect, short stat) {
		int j;

		j = engine.insertsprite(sect, stat);
		if (j != -1) {
			sprite[j].x = 0;
			sprite[j].y = 0;
			sprite[j].z = 0;
			sprite[j].cstat = 0;
			sprite[j].shade = 0;
			sprite[j].pal = 0;
			sprite[j].clipdist = 32;
			sprite[j].xrepeat = 0;
			sprite[j].yrepeat = 0;
			sprite[j].xoffset = 0;
			sprite[j].yoffset = 0;
			sprite[j].picnum = 0;
			sprite[j].ang = 0;
			sprite[j].xvel = 0;
			sprite[j].yvel = 0;
			sprite[j].zvel = 0;
			sprite[j].owner = -1;
			sprite[j].lotag = 0;
			sprite[j].hitag = 0;
			sprite[j].extra = -1;
		}
		return (short) j;
	}

	public static int initsprites() {
		int i;

		for (i = 0; i < MAXSPRITES; i++) {
			if (sprite[i] == null)
				sprite[i] = new SPRITE();
			else
				sprite[i].reset((byte) 0);
			sprite[i].sectnum = (short) MAXSECTORS;
			sprite[i].statnum = MAXSTATUS;
			sprite[i].extra = -1;
		}

		// initialize blast sector flashes
		if (sectflash == null)
			sectflash = new sectflashtype();
		sectflash.sectnum = 0;
		sectflash.ovis = 0;
		sectflash.step = 0;
		return (1);
	}

	public static short jsdeletesprite(short spritenum) {
		int ext;

		ext = sprite[spritenum].extra;

		if (validext(ext) != 0) {
			spriteXT[ext].set(0); // memset(spriteXT[ext], 0, sizeof(struct spriteextension));
			spriteXT[ext].lock = 0x00;
		}

		engine.deletesprite(spritenum);
		sprite[spritenum].extra = -1;

		return (0);
	}

	public static int validext(int ext) {
		if ((ext >= 0) && (ext < MAXSPRITES)) {
			return (1);
		}
		return (0);
	}

	public static void toss(short snum) {
		int j;

		if (!validplayer(snum)) {
			game.ThrowError("toss: bad plrnum");
		}
		if (pickup.picnum == 0) {
			return;
		}
		j = jsinsertsprite(gPlayer[snum].cursectnum, TOSS);
		if (j != -1) {
			if (gPlayer[snum].drawweap == 0) {
				sprite[j].x = (int) (gPlayer[snum].posx + (BCosAngle(gPlayer[snum].ang + 2048 + 256)) / 64.0f);
				sprite[j].y = (int) (gPlayer[snum].posy + (BSinAngle(gPlayer[snum].ang + 2048 + 256)) / 64.0f);
			} else {
				sprite[j].x = (int) (gPlayer[snum].posx - (BCosAngle(gPlayer[snum].ang + 2048 + 256)) / 64.0f);
				sprite[j].y = (int) (gPlayer[snum].posy - (BSinAngle(gPlayer[snum].ang + 2048 + 256)) / 64.0f);
			}
			sprite[j].z = gPlayer[snum].posz + (4 << 8);
			sprite[j].cstat = pickup.cstat;
			sprite[j].shade = pickup.shade;
			sprite[j].pal = pickup.pal;
			sprite[j].clipdist = pickup.clipdist;
			sprite[j].xrepeat = pickup.xrepeat;
			sprite[j].yrepeat = pickup.yrepeat;
			sprite[j].xoffset = pickup.xoffset;
			sprite[j].yoffset = pickup.yoffset;
			sprite[j].ang = (short) gPlayer[snum].ang;
			switch (pickup.picnum) {
			case RATPIC:
				sprite[j].picnum = RATTHROWPIC;
				break;
			case TUBEBOMB + 1:
				sprite[j].picnum = TUBEBOMB;
				break;
			case DARTBOMB + 1:
				sprite[j].picnum = DARTBOMB;
				break;
			default:
				sprite[j].picnum = pickup.picnum;
				break;
			}

			sprite[j].xvel = (short) ((BCosAngle(gPlayer[snum].ang + 2048 + 256)) / 64.0f);
			sprite[j].yvel = (short) ((BSinAngle(gPlayer[snum].ang + 2048 + 256)) / 64.0f);
			sprite[j].zvel = (short) ((80 - (int) (gPlayer[snum].horiz)) << 6);
			sprite[j].owner = (short) (snum + 4096);
			sprite[j].sectnum = gPlayer[snum].cursectnum;
			// TOSS will be usin hi/lo tag
			sprite[j].lotag = 0;
			sprite[j].hitag = 0;
			sprite[j].extra = pickup.extra;
		}
		pickup.reset((byte) 0);
		pickup.extra = -1;
	}

	public static int pickupsprite(int sn) {
		// can only carry one at a time of these items
		if ((pickup.picnum != 0) || (game.nNetMode == NetMode.Multiplayer)) {
			return (0);
		}

		pickup.picnum = sprite[sn].picnum;
		pickup.cstat = sprite[sn].cstat;
		pickup.shade = sprite[sn].shade;
		pickup.pal = sprite[sn].pal;
		pickup.clipdist = sprite[sn].clipdist;
		pickup.xrepeat = sprite[sn].xrepeat;
		pickup.yrepeat = sprite[sn].yrepeat;
		pickup.xoffset = sprite[sn].xoffset;
		pickup.yoffset = sprite[sn].yoffset;
		// cant set lotag/hitag as TOSS needs to use them
		pickup.extra = sprite[sn].extra;

		jsdeletesprite((short) sn);

		pickupclock = lockclock;
		return (1);
	}

	public static void operatesprite(int dasprite) {
		int datag;
		SPRITE spr;
		int pu;

		if ((game.nNetMode != NetMode.Multiplayer) && (sprite[dasprite].lotag == SPR_LOTAG_PICKUP)) {
			pu = pickupsprite(dasprite);
			switch (sprite[dasprite].picnum) {
			case RATPIC:
				if (pu != 0) {
					showmessage("LIVE RAT");
				}
				break;
			case TUBEBOMB + 1:
			case DARTBOMB + 1:
				if (pu != 0) {
					showmessage("GRENADE");
				}
			default:
				break;
			}
			return;
		}

		switch (sprite[dasprite].picnum) {
		case 1361: // the witchaven poster for demo
			playsound(S_WITCH, sprite[dasprite].x, sprite[dasprite].y, 0, ST_UNIQUE);
			break;
		case 592: // the witchaven poster for demo
			playsound(S_FLUSH, sprite[dasprite].x, sprite[dasprite].y, 0, ST_UNIQUE);
			break;
		default:
			break;
		}

		spr = sprite[dasprite];
		datag = spr.lotag;
		switch (datag) {
		case 6:
			if ((sprite[dasprite].cstat & 0x001) != 0) {
				setanimpic(spr.picnum, TICSPERFRAME * 3, 4);
			} else {
				setanimpic(spr.picnum, TICSPERFRAME * 3, -4);
			}
			sprite[dasprite].cstat ^= 0x101;
//	          teksetdelayfunc(operatesprite,CLKIPS*4,dasprite);FIXME
			break;
		case 4:
			if (switchlevelsflag == 0) {
				break;
			}
			if (game.nNetMode == NetMode.Multiplayer && (spr.picnum == 182 || spr.picnum == 803)) {
				playsound(S_WH_SWITCH, sprite[dasprite].x, sprite[dasprite].y, 0, ST_UNIQUE);
//	               nextnetlevel();FIXME
			}
			break;
		}
	}

	public static final String TRAILERID = "**MAP_EXTS**\0";

	public static int initspriteXTs() {
		int i, ext;
		boolean noext = false;

		if (pickup == null)
			pickup = new SPRITE();
		else
			pickup.reset((byte) 0);
		pickup.extra = -1;

		for (i = 0; i < MAXSPRITES; i++)
			spriteXT[i].set(0);

		Resource fh = null;
		if ((fh = BuildGdx.cache.open(boardfilename, 0)) == null)
			return 0;

		// read in XTtrailer
		fh.seek(-(XTtrailertype.sizeof), Whence.End);
		byte[] buf = new byte[XTtrailertype.sizeof];

		fh.read(buf);
		XTtrailer.load(buf);

		// if no previous extension info then continue
		if (XTtrailer.ID == null || XTtrailer.ID.compareToIgnoreCase(TRAILERID) != 0)
			noext = true;

		if (!noext) {
			// load and intialize spriteXT array members
			fh.seek(XTtrailer.start, Whence.Set);
			for (i = 0; i < XTtrailer.numXTs; i++) {
				if (!XTsave.load(fh))
					break;
			}
		}

		fh.close();

		switch (gDifficulty) {
		case 0:
		case 1:
			vadd = -1;
			break;
		case 2:
			vadd = 0;
			break;
		case 3:
			vadd = 3;
			break;
		}

		// adjust speed for difficulty
		for (i = 0; i < MAXSPRITES; i++) {
			if (sprite[i].extra != -1) {
				if (NOSHOWSPRITES)
					show2dsprite[i >> 3] &= ~(1 << (i & 7));

				switch (spriteXT[sprite[i].extra].basestat) {
				case GUARD:
				case CHASE:
				case STALK:
					sprite[i].xvel += vadd;
					if (sprite[i].xvel < 0)
						sprite[i].xvel = 0;
					sprite[i].yvel += vadd;
					if (sprite[i].yvel < 0)
						sprite[i].yvel = 0;
					sprite[i].zvel += vadd;
					if (sprite[i].zvel < 0)
						sprite[i].zvel = 0;
					break;
				}
			}
		}

		// only CHASE in multiplayer
		if (game.nNetMode == NetMode.Multiplayer) {
			for (i = 0; i < MAXSPRITES; i++) {
				if ((ext = sprite[i].extra) != -1) {
					if (sprite[i].statnum != CHASE) {
						jsdeletesprite((short) i);
					} else if (spriteXT[ext].basestat != CHASE) {
						jsdeletesprite((short) i);
					}
				}
			}
		}

		if (noenemiesflag != 0) {
			for (i = 0; i < MAXSPRITES; i++) {
				if (sprite[i].extra != -1) {
					jsdeletesprite((short) i);
				}
			}
		}

		if (noguardflag != 0) {
			for (i = 0; i < MAXSPRITES; i++) {
				if (sprite[i].statnum == GUARD) {
					jsdeletesprite((short) i);
				} else {
					if (sprite[i].extra != -1) {
						if (spriteXT[sprite[i].extra].basestat == GUARD) {
							jsdeletesprite((short) i);
						}
					}
				}
			}
		}

		if (nostalkflag != 0) {
			for (i = 0; i < MAXSPRITES; i++) {
				if (sprite[i].statnum == STALK) {
					jsdeletesprite((short) i);
				} else {
					if (sprite[i].extra != -1) {
						if (spriteXT[sprite[i].extra].basestat == STALK) {
							jsdeletesprite((short) i);
						}
					}
				}
			}
		}

		if (nochaseflag != 0) {
			for (i = 0; i < MAXSPRITES; i++) {
				if (sprite[i].statnum == CHASE) {
					jsdeletesprite((short) i);
				} else {
					if (sprite[i].extra != -1) {
						if (spriteXT[sprite[i].extra].basestat == CHASE) {
							jsdeletesprite((short) i);
						}
					}
				}
			}
		}

		if (nostrollflag != 0) {
			for (i = 0; i < MAXSPRITES; i++) {
				if (sprite[i].statnum == STROLL) {
					jsdeletesprite((short) i);
				} else {
					if (sprite[i].extra != -1) {
						if (spriteXT[sprite[i].extra].basestat == STROLL) {
							jsdeletesprite((short) i);
						}
					}
				}
			}
		}

		return (1);
	}

	public static void sectortriggersprites(int snum) {
		short i, nexti, ext;
		boolean triggered = false;

		if (game.nNetMode == NetMode.Multiplayer) {
			return;
		}

		if (!validplayer(snum)) {
			game.ThrowError("sectrtrgrsprts: bad plrnum");
		}

		if (sector[gPlayer[snum].cursectnum].lotag == SECT_LOTAG_SHOWMESSAGE) {
			switch (sector[gPlayer[snum].cursectnum].hitag) {
			case 0:
				showmessage("AREA IS OFF-LIMITS");
				break;
			}
			return;
		}
		if (sector[gPlayer[snum].cursectnum].lotag != SECT_LOTAG_TRIGGERSPRITE) {
			return;
		}

		i = headspritestat[INANIMATE];
		while (i != -1) {
			nexti = nextspritestat[i];
			if (sprite[i].hitag == sector[gPlayer[snum].cursectnum].hitag) {
				triggersprite(i);
				triggered = true;
			}
			i = nexti;
		}

		i = headspritestat[AMBUSH];
		while (i != -1) {
			nexti = nextspritestat[i];
			if (sprite[i].hitag == sector[gPlayer[snum].cursectnum].hitag) {
				ext = sprite[i].extra;
				if (validext(ext) == 0) {
					noextthrowerror(i, 300);
				}
				spriteXT[i].aimask |= AI_JUSTSHOTAT;
				ambushyell(i, ext);
				triggered = true;
			}
			i = nexti;
		}

		i = headspritestat[GUARD];
		while (i != -1) {
			nexti = nextspritestat[i];
			if (sprite[i].hitag == sector[gPlayer[snum].cursectnum].hitag) {
				if ((ext = sprite[i].extra) != -1) {
					spriteXT[ext].aimask &= ~AI_GAVEWARNING;
					givewarning(i, ext);
					spriteXT[ext].aimask |= AI_ENCROACHMENT;
					triggered = true;
				}
			}
			i = nexti;
		}

		i = headspritestat[STANDING]; // check GUARDS who are standing
		while (i != -1) {
			nexti = nextspritestat[i];
			ext = sprite[i].extra;
			if ((validext(ext) == 0) || (spriteXT[ext].basestat == GUARD)) {
				if (sprite[i].hitag == sector[gPlayer[snum].cursectnum].hitag)
					spriteXT[ext].aimask &= ~AI_GAVEWARNING;
				givewarning(i, ext);
				spriteXT[ext].aimask |= AI_ENCROACHMENT;
				triggered = true;
			}
			i = nexti;
		}

		if (triggered) {
			sector[gPlayer[snum].cursectnum].hitag = 0;
		}
	}

	public static void triggersprite(short sn) {
		int datag;
		short j;
		short newext;

		if (game.nNetMode == NetMode.Multiplayer) {
			return;
		}

		if (sprite[sn].extra == -1) {
			return;
		}

		datag = sprite[sn].lotag;

		switch (datag) {

		case SPR_LOTAG_MORPH:
			break;
		case SPR_LOTAG_SPAWNCHASE:
			j = jsinsertsprite(sprite[sn].sectnum, spriteXT[sprite[sn].extra].basestat);
			if (j == -1) {
				break;
			}
			newext = (short) mapXT(j);
			if (validext(newext) == 0) {
				jsdeletesprite(j);
				break;
			}
			sprite[j].x = sprite[sn].x + (sintable[(sprite[sn].ang + 512) & 2047] >> 6);
			sprite[j].y = sprite[sn].y + (sintable[sprite[sn].ang & 2047] >> 6);
			sprite[j].z = sprite[sn].z;
			sprite[j].cstat = 0x101;
			sprite[j].picnum = (short) spriteXT[sprite[sn].extra].basepic;
			sprite[j].shade = sprite[sn].shade;
			sprite[j].sectnum = sprite[sn].sectnum;
			sprite[j].xrepeat = sprite[sn].xrepeat;
			sprite[j].yrepeat = sprite[sn].yrepeat;
			sprite[j].ang = sprite[sn].ang;
			sprite[j].xvel = sprite[sn].xvel;
			sprite[j].yvel = sprite[sn].yvel;
			sprite[j].zvel = sprite[sn].zvel;
			sprite[j].owner = -1;
			sprite[j].lotag = 0;
			sprite[j].hitag = 0;
			spriteXT[sprite[j].extra].basestat = spriteXT[sprite[sn].extra].basestat;
			spriteXT[sprite[j].extra].basepic = spriteXT[sprite[sn].extra].basepic;
			spriteXT[sprite[j].extra].walkpic = spriteXT[sprite[sn].extra].walkpic;
			spriteXT[sprite[j].extra].standpic = spriteXT[sprite[sn].extra].standpic;
			spriteXT[sprite[j].extra].runpic = spriteXT[sprite[sn].extra].runpic;
			spriteXT[sprite[j].extra].attackpic = spriteXT[sprite[sn].extra].attackpic;
			spriteXT[sprite[j].extra].deathpic = spriteXT[sprite[sn].extra].deathpic;
			spriteXT[sprite[j].extra].morphpic = spriteXT[sprite[sn].extra].morphpic;
			spriteXT[sprite[j].extra].specialpic = spriteXT[sprite[sn].extra].specialpic;
			// delete trigger tag from old sprites
			sprite[sn].lotag = 0;
			sprite[sn].hitag = 0;
			clearXTpics(sn);
			break;
		default:
			newstatus(sn, spriteXT[sprite[sn].extra].basestat);
			break;
		}
	}

	public static boolean isaplayersprite(int sprnum) {
		int j;

		for (j = connecthead; j >= 0; j = connectpoint2[j]) {
			if (gPlayer[j].playersprite == sprnum) {
				return (true);
			}
		}
		if (sprite[sprnum].statnum == 8) {
			game.ThrowError("isplrspr: non plr has statnm 8");
		}
		return (false);
	}

	public static void noextthrowerror(int i, int loc) {
		Console.Println("sprite at " + sprite[i].x + " ," + sprite[i].y + "  no extension from  " + loc);
	}

	public static void newstatus(short sn, int seq) {
		short newpic;
		short ext;
		long zoffs;

		if (isaplayersprite(sn)) {
			return;
		}

		ext = sprite[sn].extra;
		if (validext(ext) == 0) {
			noextthrowerror(sn, 0);
		}

		switch (seq) {
		case INANIMATE:
			engine.changespritestat(sn, INANIMATE);

//	               showmessage("INANIMATE \0".toCharArray());

			break;
		case INACTIVE:
			engine.changespritestat(sn, INACTIVE);

//	               showmessage("INACTIVE \0".toCharArray());

			break;
		case FLOATING:
			game.pInt.setsprinterpolate(sn, sprite[sn]);
			if (sector[sprite[sn].sectnum].lotag == 4) {
				if ((sprite[sn].cstat & 128) == 0)
					zoffs = -((engine.getTile(sprite[sn].picnum).getHeight() * sprite[sn].yrepeat) << 1);
				else
					zoffs = 0;
				sprite[sn].z = (int) (sector[sprite[sn].sectnum].floorz - zoffs);
				sprite[sn].lotag = 0;
				sprite[sn].hitag = 0;
				sprite[sn].xvel = 1;
				sprite[sn].yvel = 1;
				engine.changespritestat(sn, FLOATING);
			} else {
				engine.getzrange(sprite[sn].x, sprite[sn].y, sprite[sn].z - 1, sprite[sn].sectnum,
						(sprite[sn].clipdist) << 2, CLIPMASK0);
				sprite[sn].z = zr_florz;
				engine.changespritestat(sn, INACTIVE);
			}
			break;
		case GUARD:
			newpic = (short) spriteXT[ext].walkpic;
			if (newpic != NO_PIC) {
				engine.changespritestat(sn, GUARD);
				sprite[sn].lotag = 0;
				sprite[sn].picnum = newpic;
				spriteXT[ext].basestat = GUARD;

//	                    showmessage("GUARD \0".toCharArray());

			}
			break;
		case PATROL:
			newpic = (short) spriteXT[ext].walkpic;
			if (newpic != NO_PIC) {
				engine.changespritestat(sn, PATROL);
				sprite[sn].lotag = 0;
				sprite[sn].picnum = newpic;
				spriteXT[ext].basestat = PATROL;

//	                    showmessage("PATROL \0".toCharArray());

			}
			break;
		case PINBALL:
			newpic = (short) spriteXT[ext].walkpic;
			if (newpic != NO_PIC) {
				engine.changespritestat(sn, PINBALL);
				sprite[sn].picnum = newpic;
				spriteXT[ext].basestat = PINBALL;

//	                    showmessage("PINBALL\0".toCharArray());

			}
			break;
		case STROLL:
			newpic = (short) spriteXT[ext].walkpic;
			if (newpic != NO_PIC) {
				engine.changespritestat(sn, STROLL);
				sprite[sn].lotag = 0;
				sprite[sn].picnum = newpic;
				spriteXT[ext].basestat = STROLL;

//	                    showmessage("STROLL\0".toCharArray());

			}
			break;
		case CHASE:
			newpic = (short) spriteXT[ext].runpic;
			if (newpic != NO_PIC) {
				engine.changespritestat(sn, CHASE);
				sprite[sn].lotag = 0;
				sprite[sn].picnum = newpic;
				spriteXT[ext].basestat = CHASE;

//	                    showmessage("CHASE\0".toCharArray());

			}
			break;
		case FLEE:
			newpic = (short) spriteXT[ext].runpic;
			if (newpic != NO_PIC) {
				engine.changespritestat(sn, FLEE);
				sprite[sn].lotag = 0;
				sprite[sn].picnum = newpic;

//	                    showmessage("FLEE\0".toCharArray());

			}
			break;
		case ATTACK:
			// standard 5 angles, 2 frames/angle
			newpic = (short) spriteXT[ext].attackpic;
			if (newpic != NO_PIC) {
				engine.changespritestat(sn, ATTACK);
				sprite[sn].picnum = newpic;
				switch (spriteXT[ext].weapon) {
				case 1:
				case 2:
				case 3:
				case 4:
				case 5:
					sprite[sn].lotag = 32;
					sprite[sn].hitag = 32;
					break;
				default:
					sprite[sn].lotag = 64;
					sprite[sn].hitag = 64;
					break;
				}

//	                    showmessage("ATTACK\0".toCharArray());

			}
			break;
		case DELAYEDATTACK:
			// standard 5 angles, 2 frames/angle
			sprite[sn].lotag = 95;
			engine.changespritestat(sn, DELAYEDATTACK);

//	               showmessage("DELAYED ATTACK\0".toCharArray());

			break;
		case STALK:
			newpic = (short) spriteXT[ext].runpic;
			if (newpic != NO_PIC) {
				engine.changespritestat(sn, STALK);
				sprite[sn].lotag = 0;
				sprite[sn].picnum = newpic;
				spriteXT[ext].basestat = STALK;

//	                    showmessage("STALK\0".toCharArray());

			}
			break;
		case SQUAT:
			newpic = (short) spriteXT[ext].squatpic;
			if (newpic != NO_PIC) {
				engine.changespritestat(sn, SQUAT);
				// standard 3 frames
				// stay squat for 4 TICSPERFRAME
				sprite[sn].lotag = 47;
				sprite[sn].hitag = 64;

//	                    showmessage("SQUAT\0".toCharArray());

			}
			break;
		case UNSQUAT:
			newpic = (short) spriteXT[ext].squatpic;
			if (newpic != NO_PIC) {
				engine.changespritestat(sn, UNSQUAT);
				sprite[sn].lotag = 47;
				sprite[sn].hitag = 0;

//	                    showmessage("UNSQUAT\0".toCharArray());

			}
			break;
		case DODGE:
			newpic = (short) spriteXT[ext].squatpic;
			if (newpic != NO_PIC) {
				engine.changespritestat(sn, SQUAT);
				// standard 2 frames
				sprite[sn].lotag = 31;
				sprite[sn].hitag = 0;

//	                    showmessage("DODGE\0".toCharArray());

			}
			break;
		case UNDODGE:
			newpic = (short) spriteXT[ext].squatpic;
			if (newpic != NO_PIC) {
				engine.changespritestat(sn, UNSQUAT);
				sprite[sn].lotag = 31;
				sprite[sn].hitag = 0;

//	                    showmessage("UNDODGE\0".toCharArray());

			}
			break;
		case HIDE:
			newpic = (short) spriteXT[ext].squatpic;
			if (newpic != NO_PIC) {
				engine.changespritestat(sn, HIDE);
				// standard 2 frames
				sprite[sn].lotag = 31;
				sprite[sn].hitag = 256;

//	                    showmessage("HIDE\0".toCharArray());

			}
			break;
		case UNHIDE:
			newpic = (short) spriteXT[ext].squatpic;
			if (newpic != NO_PIC) {
				engine.changespritestat(sn, UNHIDE);
				sprite[sn].lotag = 31;
				sprite[sn].hitag = 0;

//	                    showmessage("UNHIDE\0".toCharArray());

			}
			break;
		case PAIN:
			newpic = (short) spriteXT[ext].painpic;
			if (newpic != 0) {
				engine.changespritestat(sn, PAIN);
				sprite[sn].picnum = newpic;
				sprite[sn].lotag = 16;

//	                    showmessage("PAIN\0".toCharArray());

			}
			break;
		case STANDING:
			newpic = (short) spriteXT[ext].standpic;
			if (newpic != 0) {
				sprite[sn].picnum = newpic;
				if (sprite[sn].lotag <= 0) {
					sprite[sn].lotag = (short) ((krand_intercept("STAT1732")) & 1024);
				}
				engine.changespritestat(sn, STANDING);

//	                    showmessage("STANDING\0".toCharArray());

			}
			break;
		case FLY:
			newpic = (short) spriteXT[ext].runpic;
			if (newpic != NO_PIC) {
				engine.changespritestat(sn, FLY);
				sprite[sn].lotag = 0;
				sprite[sn].picnum = newpic;
				spriteXT[ext].basestat = FLY;

//	                    showmessage("FLY\0".toCharArray());

			}
			break;
		case RODENT:
			newpic = (short) spriteXT[ext].runpic;
			if (newpic != NO_PIC) {
				engine.changespritestat(sn, RODENT);
				sprite[sn].lotag = 0;
				sprite[sn].picnum = newpic;
				spriteXT[ext].basestat = RODENT;

//	                    showmessage("RODENT\0".toCharArray());

			}
			break;
		case MORPH:
			if (spriteXT[ext].morphpic != 0) {
				engine.changespritestat(sn, MORPH);
				sprite[sn].lotag = 95;

//	                    showmessage("MORPH\0".toCharArray());

			}
			break;
		case DEATH:
			newpic = (short) spriteXT[ext].deathpic;
			sprite[sn].cstat &= ~257;
			if (newpic != 0) {
				engine.changespritestat(sn, DEATH);
				deathdropitem(sn);
				sprite[sn].lotag = (short) (((engine.getTile(newpic).getFrames()) << 4) - 1);
				sprite[sn].hitag = sprite[sn].lotag;
				deathsounds(newpic, sprite[sn].x, sprite[sn].y);
				sprite[sn].picnum = newpic;
			} else {
				engine.changespritestat(sn, INACTIVE);
			}

//	               showmessage("DEATH \0".toCharArray());

			break;
		}
	}

	public static void deathsounds(int pic, int x, int y) {
		switch (pic) {
		case ANTDEATHPIC: // anton boss
		case DIDEATHPIC: // dimarco boss
		case 2165: // miles boss
		case 2978: // sonny hokuri boss
		case 2850: // carlyle boss
		case 2662: // janus boss
		case 2550: // marty dollar boss
		case RUBDEATHPIC:
		case FRGDEATHPIC:
		case JAKEDEATHPIC:
		case COP1DEATHPIC:
		case ERDEATHPIC:
		case 2415: // rebreather
		case 2295: // black cop
		case 2455: // trenchcoat
		case 2792: // blacksmith
		case 2712: // orange guy
		case 3041: // swat guy
		case 2910: // saline suit
			playsound(S_MANDIE1 + RMOD4("STAT1378") + RMOD3("STAT1378") + RMOD3("STAT1378"), x, y, 0, ST_NOUPDATE);
			break;
		case SARAHDEATHPIC:
		case SAMDEATHPIC:
		case MADEATHPIC:
		case 2340: // nika
		case 2607: // halter top
		case 2227: // cowgirl
			playsound(S_GIRLDIE1 + RMOD4("STAT1389") + RMOD3("STAT1389"), x, y, 0, ST_NOUPDATE);
			break;
		case GLASSDEATHPIC:
			playsound(S_GLASSBREAK1 + RMOD2("STAT1392"), x, y, 0, ST_NOUPDATE);
			break;
		case 570: // autogun
			playsound(S_AUTOGUNEXPLODE, x, y, 0, ST_NOUPDATE);
			break;
		case 609: // bathroom glass
			playsound(S_SMALLGLASS1 + RMOD2("STAT1398"), 0, 0, 0, ST_IMMEDIATE);
			break;
		case 3060:
		case 3064:
		case 3068:
		case 3072:
		case 3076:
		case 3080:
		case 3084:
			playsound(S_HOLOGRAMDIE, x, y, 0, ST_NOUPDATE);
			break;
		case 3973: // matrix character death
			if (krand_intercept("STAT1364") < 32767) {
				playsound(S_MATRIX_DIE1, x, y, 0, ST_NOUPDATE);
			} else {
				playsound(S_MATRIX_DIE2, x, y, 0, ST_NOUPDATE);
			}
			break;

		}
	}

	public static void deathdropitem(int sprnum) {
		short j, ext;
		int pic;

		if (game.nNetMode == NetMode.Multiplayer) {
			return;
		}

		ext = sprite[sprnum].extra;
		if (validext(ext) == 0) {
			return;
		}

		if ((spriteXT[ext].fxmask & FX_HASREDCARD) != 0) {
			pic = RED_KEYCARD;
			spriteXT[ext].fxmask &= (~FX_HASREDCARD);
		} else if ((spriteXT[ext].fxmask & FX_HASBLUECARD) != 0) {
			pic = BLUE_KEYCARD;
			spriteXT[ext].fxmask &= (~FX_HASBLUECARD);
		} else if ((spriteXT[ext].fxmask & FX_ANDROID) != 0) {
			showmessage("WAS AN ANDROID");
			return;
		} else {
			switch (spriteXT[ext].weapon) {
			case 4:
			case 5:
				pic = KLIPPIC;
				break;
			default:
				return;
			}
		}

		j = jsinsertsprite(sprite[sprnum].sectnum, DROPSIES);
		if (j != -1) {
			fillsprite(j, sprite[sprnum].x, sprite[sprnum].y, sprite[sprnum].z - (engine.getTile(sprite[sprnum].picnum).getHeight() << 6),
					128, 0, 0, 12, 16, 16, 0, 0, pic, sprite[sprnum].ang,
					sintable[(sprite[sprnum].ang + 2560) & 2047] >> 6,
					sintable[(sprite[sprnum].ang + 2048) & 2047] >> 6, 30, sprnum + 4096, sprite[sprnum].sectnum,
					DROPSIES, 0, 0, 0);
		} else {
			return;
		}

		// tweak the size of the pic
		switch (sprite[j].picnum) {
		case KLIPPIC:
			sprite[j].xrepeat -= 2;
			sprite[j].yrepeat -= 2;
			break;
		case RED_KEYCARD:
		case BLUE_KEYCARD:
			sprite[j].xrepeat >>= 1;
			sprite[j].yrepeat >>= 1;
			break;
		default:
			break;
		}
	}

	public static int mapXT(int sn) {
		short i;

		for (i = 0; i < MAXSPRITES; i++) {
			if (spriteXT[i].lock == 0x00) {
				sprite[sn].extra = i;
				spriteXT[i].set(0);
				spriteXT[i].lock = 0xFF;
				return (i);
			}
		}

		sprite[sn].extra = -1;
		return (-1); // no free spot found
	}

	public static void clearXTpics(int spriteno) {
		short extno = sprite[spriteno].extra;

		if (extno != -1) {
			spriteXT[extno].basepic = sprite[spriteno].picnum;
			spriteXT[extno].standpic = NO_PIC;
			spriteXT[extno].walkpic = NO_PIC;
			spriteXT[extno].runpic = NO_PIC;
			spriteXT[extno].attackpic = NO_PIC;
			spriteXT[extno].deathpic = NO_PIC;
			spriteXT[extno].painpic = NO_PIC;
			spriteXT[extno].squatpic = NO_PIC;
			spriteXT[extno].morphpic = NO_PIC;
			spriteXT[extno].specialpic = NO_PIC;
		}
	}

	public static void ambushyell(int sn, int ext) {

		if (validext(ext) == 0) {
			return;
		}
		if ((spriteXT[ext].aimask & AI_DIDAMBUSHYELL) != 0)
			return;

		switch (spriteXT[ext].basepic) {
		case ANTWALKPIC:
		case RUBWALKPIC:
			playsound(S_MALE_COMEONYOU + (krand_intercept("STAT2603") & 6), sprite[sn].x, sprite[sn].y, 0,
					ST_IMMEDIATE);
			spriteXT[ext].aimask |= AI_DIDAMBUSHYELL;
			break;
		case DIWALKPIC:
			playsound(S_DIM_WANTSOMETHIS + (krand_intercept("STAT2607") & 2), sprite[sn].x, sprite[sn].y, 0,
					ST_IMMEDIATE);
			spriteXT[ext].aimask |= AI_DIDAMBUSHYELL;
			break;
		}
	}

	public static int attachvirus(short i, int pic) {
		int j, ext, nextj;

		if (game.nNetMode == NetMode.Multiplayer) {
			return (0);
		}
		if ((sprite[i].statnum == VIRUS) || (sprite[i].statnum == PINBALL)) {
			return (0);
		}
		if (isanandroid(i) != 0 || isahologram(i) != 0) {
			return (0);
		}

		j = headspritestat[VIRUS];
		while (j >= 0) {
			nextj = nextspritestat[j];
			// already hosting ?
			if (sprite[j].owner == i) {
				return (0);
			}
			j = nextj;
		}

		j = jsinsertsprite(sprite[i].sectnum, VIRUS);
		if (j == -1) {
			return (0);
		}

		sprite[j].extra = -1;
		sprite[j].x = sprite[i].x;
		sprite[j].y = sprite[i].y;
		sprite[j].z = sprite[i].z;
		sprite[j].xrepeat = 20;
		sprite[j].yrepeat = 20;
		sprite[j].cstat = 0x0000;
		sprite[j].shade = -28;
		sprite[j].picnum = (short) pic;
		sprite[j].lotag = (short) (krand_intercept("STAT1216") & 512 + 128);
		sprite[j].hitag = 0;
		sprite[j].owner = i; // host

		if (pic == FIREPIC) {
			sprite[j].xrepeat = sprite[i].xrepeat;
			sprite[j].yrepeat = (byte) (sprite[i].yrepeat + 8);
			ext = sprite[i].extra;
			playsound(S_FIRELOOP, sprite[j].x, sprite[j].y, 0, ST_UPDATE);
			if (validext(ext) != 0) {
				newstatus(i, PINBALL);
				if (sprite[i].statnum == PINBALL) {
					sprite[i].xvel += 4;
					sprite[i].yvel += 4;
				}
			}
		} else {
			playsound(S_FORCEFIELDHUMLOOP, sprite[i].x, sprite[i].y, 0, ST_UNIQUE);
		}

		return (1);
	}

	public static int playervirus(int pnum, int pic) {
		int j, nextj;

		if (!validplayer(pnum)) {
			game.ThrowError("plrvrus: bad plrnum");
		}

		j = headspritestat[PLRVIRUS];
		while (j >= 0) {
			nextj = nextspritestat[j];
			// already hosting ?
			if (sprite[j].owner == pnum) {
				return (0);
			}
			j = nextj;
		}

		j = jsinsertsprite(gPlayer[pnum].cursectnum, PLRVIRUS);
		if (j == -1) {
			return (0);
		}

		sprite[j].extra = -1;
		sprite[j].x = gPlayer[pnum].posx;
		sprite[j].y = gPlayer[pnum].posy;
		sprite[j].z = gPlayer[pnum].posz;
		sprite[j].xrepeat = 18;
		sprite[j].yrepeat = 40;
		sprite[j].cstat = 0x0000;
		sprite[j].shade = -28;
		sprite[j].picnum = (short) pic;
		sprite[j].lotag = (short) (krand_intercept("STAT1172") & 512 + 128);
		sprite[j].hitag = 0;
		sprite[j].owner = (short) pnum; // host

		playsound(S_FIRELOOP, sprite[j].x, sprite[j].y, 0, ST_UPDATE);

		return (1);
	}

	public static int damagesprite(short hitsprite, int points) {
		int ext;

		if (isaplayersprite(hitsprite)) {
			return (0);
		}

		ext = sprite[hitsprite].extra;

		switch (sprite[hitsprite].statnum) {
		case INANIMATE:
			if (validext(ext) != 0) {
				newstatus(hitsprite, DEATH);
			}
			return (0);
		case GENEXPLODE1:
			engine.changespritestat(hitsprite, INACTIVE);
			genexplosion1(hitsprite);
			jsdeletesprite(hitsprite);
			return (0);
		case GENEXPLODE2:
			engine.changespritestat(hitsprite, INACTIVE);
			genexplosion2(hitsprite);
			jsdeletesprite(hitsprite);
			return (0);
		case INACTIVE:
		case FLOATING:
			return (0);
		case PROJHIT:
		case PROJECTILE:
		case TOSS:
		case PAIN:
		case RUNTHRU:
		case TWITCH:
		case DEATH:
			return (0);
		default:
			break;
		}

		if (gDifficulty == 0) {
			points <<= 1;
		}

		if (validext(ext) != 0) {
			if ((abs(points)) > spriteXT[ext].hitpoints) {
				spriteXT[ext].hitpoints = 0;
			} else {
				spriteXT[ext].hitpoints -= points;
			}
			spriteXT[ext].aimask |= AI_JUSTSHOTAT;
			spriteXT[ext].aimask |= AI_TIMETODODGE;
			spriteXT[ext].aimask |= AI_WASATTACKED;
			if (spriteXT[ext].hitpoints < ENEMYCRITICALCONDITION)
				spriteXT[ext].aimask |= AI_CRITICAL;
			if (spriteXT[ext].hitpoints <= 0) {
				spriteXT[ext].hitpoints = 0;
				// newstatus(hitsprite,DEATH);
				spriteXT[ext].fxmask |= FX_NXTSTTDEATH;
				return (1);
			} else {
				// newstatus(hitsprite,PAIN);
				spriteXT[ext].fxmask |= FX_NXTSTTPAIN;
			}
		}

		return (0);
	}

	public static int playerhit;

	public static int playerhit(int hitsprite) {
		int j;

		for (j = connecthead; j >= 0; j = connectpoint2[j]) {
			if (gPlayer[j].playersprite == hitsprite) {
				if (sprite[hitsprite].statnum != 8) {
					game.ThrowError("plrhit: plrsprt lost sttnm 8");
				}
				playerhit = j;
				return (1);
			}
		}

		return (0);
	}

	public static void sectorflash(short s) {
		if (sectflash.step != 0) {
			return;
		}
		sectflash.sectnum = s;
		sectflash.step = 1;
		sectflash.ovis = (char) (sector[s].visibility & 0xff);
	}

	public static void genexplosion1(int i) {
		int j;

		j = jsinsertsprite(sprite[i].sectnum, (short) 5);
		if (j != -1) {
			fillsprite(j, sprite[i].x, sprite[i].y, sprite[i].z, 0, -16, 0, 0, 64, 64, 0, 0, GENEXP1PIC + 1,
					sprite[i].ang, sintable[(sprite[i].ang + 2560) & 2047] >> 6,
					sintable[(sprite[i].ang + 2048) & 2047] >> 6, 30, i + 4096, sprite[i].sectnum, 5, 24, 0, -1);
			playsound(S_SMALLGLASS1 + RMOD2("STAT4534"), sprite[i].x, sprite[i].y, 0, ST_NOUPDATE);
			return;
		}
	}

	public static void genexplosion2(int i) {
		int j;

		sectorflash(sprite[i].sectnum);
		checkblastarea(i);

		j = jsinsertsprite(sprite[i].sectnum, (short) 5);
		if (j != -1) {
			fillsprite(j, sprite[i].x, sprite[i].y, sprite[i].z - (engine.getTile(sprite[i].picnum).getHeight() << 3), 0, -16, 0, 0, 64,
					64, 0, 0, GENEXP2PIC, sprite[i].ang, sintable[(sprite[i].ang + 2560) & 2047] >> 6,
					sintable[(sprite[i].ang + 2048) & 2047] >> 6, 30, i + 4096, sprite[i].sectnum, 5, 32, 0, -1);
			playsound(S_EXPLODE1 + RMOD2("STAT4559"), sprite[i].x, sprite[i].y, 0, ST_NOUPDATE);
		}
	}

	public static int spewblood(int sprnum, int hitz, int daang) {
		int j, ext = sprite[sprnum].extra;

		if (game.nNetMode == NetMode.Multiplayer) {
			return (0);
		}
		if (validext(ext) == 0) {
			return (0);
		}
		if (isanandroid(sprnum) != 0 || isahologram(sprnum) != 0) {
			return (0);
		}

		switch (spriteXT[ext].basepic) {
		case RUBWALKPIC:
		case FRGWALKPIC:
		case COP1WALKPIC:
		case ANTWALKPIC:
		case SARAHWALKPIC:
		case MAWALKPIC:
		case DIWALKPIC:
		case ERWALKPIC:
		case SAMWALKPIC:
			j = jsinsertsprite(sprite[sprnum].sectnum, RUNTHRU);
			if (j != -1) {
				fillsprite(j, sprite[sprnum].x, sprite[sprnum].y,
						sprite[sprnum].z - (engine.getTile(sprite[sprnum].picnum).getHeight() << 6), 128, 0, 0, 12, 16, 16, 0, 0,
						BLOODSPLAT, sprite[sprnum].ang, sintable[(sprite[sprnum].ang + 2560) & 2047] >> 6,
						sintable[(sprite[sprnum].ang + 2048) & 2047] >> 6, 30, sprnum + 4096, sprite[sprnum].sectnum,
						RUNTHRU, 0, 0, 0);
				sprite[j].z = hitz;
				sprite[j].lotag = (short) (engine.getTile(BLOODSPLAT).getFrames());
				if (sprite[j].lotag > MAXFRAMES) {
					sprite[j].lotag = MAXFRAMES;
				}
				if (sprite[j].lotag < 0) {
					sprite[j].lotag = 0;
				}
				sprite[j].hitag = 0;
				return (1);
			}
			break;
		default:
			break;
		}

		return (0);
	}

	public static void checkblastarea(int spr) {
		int sect, j, xydist, zdist;
		short i, nexti;

		sect = sprite[spr].sectnum;

		for (j = connecthead; j >= 0; j = connectpoint2[j]) {
			xydist = klabs(sprite[spr].x - gPlayer[j].posx) + klabs(sprite[spr].y - gPlayer[j].posy);
			zdist = klabs(sprite[spr].z - gPlayer[j].posz);
			if ((xydist < 512) && (zdist < 10240)) {
				changehealth(j, -5000);
			} else if ((xydist < 2048) && (zdist < 20480)) {
				changehealth(j, -800);
			} else if ((xydist < 4096) && (zdist < 40960)) {
				changehealth(j, -200);
			}
		}

		if (game.nNetMode != NetMode.Multiplayer) {
			for (i = headspritesect[sect]; i >= 0; i = nexti) {
				nexti = nextspritesect[i];
				if ((i != spr) && (!isaplayersprite(i))) {
					switch (sprite[i].statnum) {
					case PLAYER:
					case BOMBPROJECTILESTAT:
					case BOMBPROJECTILESTAT2:
					case RUNTHRU:
					case INACTIVE:
					case DEATH:
						break;
					default:
						xydist = klabs(sprite[spr].x - sprite[i].x) + klabs(sprite[spr].y - sprite[i].y);
						zdist = klabs(sprite[spr].z - sprite[i].z);
						if ((xydist < 2560) && (zdist < 16384)) {
							damagesprite(i, -500);
						}
						break;
					}
				}
			}
		}
	}

	public static int isahologram(int i) {
		int ext = sprite[i].extra;

		if (validext(ext) != 0) {
			if (((spriteXT[ext].fxmask) & FX_HOLOGRAM) != 0) {
				return (1);
			}
		}

		return (0);
	}

	public static int isanandroid(int i) {
		int ext = sprite[i].extra;

		if (validext(ext) != 0) {
			if (((spriteXT[ext].fxmask) & FX_ANDROID) != 0) {
				return (1);
			}
		}

		return (0);
	}

	public static void playergunshot(int snum) {
		int j, nextj, ext;
		long dist;

		if (!validplayer(snum)) {
			game.ThrowError("plgunsht: bad plrnum");
		}

		j = headspritestat[STROLL];
		while (j != -1) {
			nextj = nextspritestat[j];

			ext = sprite[j].extra;
			if (validext(ext) == 0) {
				noextthrowerror(j, 103);
			}
			dist = abs(sprite[j].x - gPlayer[snum].posx) + abs(sprite[j].y - gPlayer[snum].posy);
			if (dist < HEARGUNSHOTDIST) {
				spriteXT[ext].aimask |= AI_JUSTSHOTAT;
			}

			j = nextj;
		}

		j = headspritestat[STANDING];
		while (j != -1) {
			nextj = nextspritestat[j];

			ext = sprite[j].extra;
			if (validext(ext) == 0) {
				noextthrowerror(j, 105);
			}
			dist = abs(sprite[j].x - gPlayer[snum].posx) + abs(sprite[j].y - gPlayer[snum].posy);
			if (dist < HEARGUNSHOTDIST) {
				spriteXT[ext].aimask |= AI_JUSTSHOTAT;
			}

			j = nextj;
		}

		j = headspritestat[GUARD];
		while (j != -1) {
			nextj = nextspritestat[j];

			ext = sprite[j].extra;
			if (validext(ext) == 0) {
				noextthrowerror(j, 106);
			}
			dist = abs(sprite[j].x - gPlayer[snum].posx) + abs(sprite[j].y - gPlayer[snum].posy);
			if (dist < HEARGUNSHOTDIST) {
				spriteXT[ext].aimask |= AI_JUSTSHOTAT;
			}
			j = nextj;
		}
	}

	public static void givewarning(int i, int ext) {
		int dist = 0;

		if (game.nNetMode == NetMode.Multiplayer) {
			return;
		}
		if (validext(ext) == 0) {
			return;
		}
		if ((spriteXT[ext].aimask & AI_GAVEWARNING) != 0)
			return;

		dist = Math.abs(gPlayer[screenpeek].posx - sprite[i].x) + Math.abs(gPlayer[screenpeek].posy - sprite[i].y);
		if (dist > 5000) {
			return;
		}

		switch (spriteXT[ext].basepic) {
		case COP1WALKPIC:
			playsound(S_GRD_WHATDOINGHERE + (krand_intercept("STAT2636") & 4), sprite[i].x, sprite[i].y, 0, ST_UNIQUE);
			spriteXT[ext].aimask |= AI_GAVEWARNING;
			break;
		}
	}

	public static void dosectorflash() {
		switch (sectflash.step) {
		case 0:
			break;
		case 1:
			sector[sectflash.sectnum].visibility = 0;
			sectflash.step = 2;
			break;
		case 2:
			sector[sectflash.sectnum].visibility = (byte) 128;
			sectflash.step = 3;
			break;
		case 3:
			sector[sectflash.sectnum].visibility = 0;
			sectflash.step = 4;
			break;
		case 4:
			sector[sectflash.sectnum].visibility = (byte) sectflash.ovis;
			sectflash.sectnum = 0;
			sectflash.ovis = 0;
			sectflash.step = 0;
		}
	}

	public static void splash(int i) {
		int j;

		if (game.nNetMode == NetMode.Multiplayer) {
			return;
		}

		j = jsinsertsprite(sprite[i].sectnum, RUNTHRU);
		if (j != -1) {
			fillsprite(j, sprite[i].x, sprite[i].y, sprite[i].z, 2, -13, 0, 32, 64, 64, 0, 0, BLUESPLASH, sprite[i].ang,
					0, 0, 0, i + 4096, sprite[i].sectnum, RUNTHRU, 0, 0, -1);

			sprite[j].lotag = (short) (engine.getTile(BLUESPLASH).getFrames());
			if (sprite[j].lotag > MAXFRAMES) {
				sprite[j].lotag = MAXFRAMES;
			}
			if (sprite[j].lotag < 0) {
				sprite[j].lotag = 0;
			}
			sprite[j].hitag = 0;
			playsound(S_SPLASH, sprite[i].x, sprite[i].y, 0, ST_NOUPDATE);
		}
	}

	public static final int HIDEDISTANCE = 4096;

	public static void statuslistcode() {
		short p, target, hitobject, daang = 0, movestat, ext;

		int dax, day, daz, dist, mindist;
		int prevx, prevy, prevz;
		short prevsect = 0;
		int seecan;
		int targx, targy, targz;
		short targsect, host, tempshort, i, nexti, hitsprite;
		int pnum;
		int px, py, pz, deltapy, zoffs;
		SPRITE spr;

		dosectorflash();

		if ((lockclock - stackedcheck) > 30) {
			stackedcheck = lockclock;
			i = headspritestat[STACKED];
			while (i >= 0) {
				nexti = nextspritestat[i];

				if (sprite[i].z != sector[sprite[i].sectnum].floorz) {
					spr = sprite[i];
					tempshort = spr.cstat;
					spr.cstat &= ~1;
					engine.getzrange(spr.x, spr.y, spr.z - 1, spr.sectnum, (spr.clipdist) << 2, 1);

					spr.cstat = tempshort;
					if (spr.z != zr_florz) {
						spr.hitag = 0;
						engine.changespritestat(i, FALL);
					}
				}

				i = nexti;
			}
		}

		i = headspritestat[VANISH];
		while (i >= 0) {
			nexti = nextspritestat[i];

			jsdeletesprite(i);

			i = nexti;
		}

		i = headspritestat[FALL];
		while (i >= 0) {
			nexti = nextspritestat[i];

			spr = sprite[i];
			game.pInt.setsprinterpolate(i, spr);
			spr.z += (TICSPERFRAME << 11);

			tempshort = spr.cstat;
			spr.cstat &= ~1;
			engine.getzrange(spr.x, spr.y, spr.z - 1, spr.sectnum, (spr.clipdist) << 2, 1);
			spr.cstat = tempshort;
			if (spr.z >= zr_florz) {
				spr.z = zr_florz;
				if (zr_florz < sector[spr.sectnum].floorz) {
					engine.changespritestat(i, STACKED);
				} else {
					engine.changespritestat(i, INANIMATE);
				}
			}
			i = nexti;
		}

		i = headspritestat[3]; // Go through smoke sprites
		while (i >= 0) {
			nexti = nextspritestat[i];
			game.pInt.setsprinterpolate(i, sprite[i]);
			sprite[i].z -= (TICSPERFRAME << 3);
			sprite[i].lotag -= TICSPERFRAME;
			if (sprite[i].lotag < 0)
				jsdeletesprite(i);

			i = nexti;
		}

		i = headspritestat[5]; // Go through explosion sprites
		while (i >= 0) {
			nexti = nextspritestat[i];

			sprite[i].lotag -= TICSPERFRAME;
			if (sprite[i].lotag < 0)
				jsdeletesprite(i);

			i = nexti;
		}

		i = headspritestat[RUNTHRU];
		while (i >= 0) {
			nexti = nextspritestat[i];

			sprite[i].hitag++;

			if (sprite[i].hitag >= TICSPERFRAME) {
				sprite[i].picnum++;
				sprite[i].hitag = 0;
				sprite[i].lotag--;
			}
			if (sprite[i].lotag <= 0) {
				jsdeletesprite(i);
			}
			i = nexti;
		}

		i = headspritestat[FLOATING];
		while (i >= 0) {
			nexti = nextspritestat[i];

			prevx = sprite[i].x;
			prevy = sprite[i].y;
			prevz = sprite[i].z;
			prevsect = sprite[i].sectnum;

			game.pInt.setsprinterpolate(i, sprite[i]);

			// bob on surface
			sprite[i].hitag++;
			if (sprite[i].hitag >= (TICSPERFRAME)) {
				sprite[i].lotag++;
				if (sprite[i].lotag >= MAXBOBS) {
					sprite[i].lotag = 0;
				}
				sprite[i].z += (bobbing[sprite[i].lotag] << 4);
				sprite[i].hitag = 0;
			}
			dax = (((sintable[(sprite[i].ang + 512) & 2047]) * sprite[i].xvel) << 3);
			day = (((sintable[sprite[i].ang & 2047]) * sprite[i].yvel) << 3);
			movestat = floatmovesprite(i, dax, day, 0, 1024, 1024, NORMALCLIP);
			if ((movestat & 0xC000) == 32768) {
				engine.setsprite(i, prevx, prevy, prevz);
				sprite[i].ang = walldeflect(movestat & 0x0FFF, sprite[i].ang);
			} else if ((movestat & 0xC000) == 49152) {
				engine.setsprite(i, prevx, prevy, prevz);
				sprite[i].ang = spritedeflect(movestat & 0x0FFF, sprite[i].ang);
			} else if (prevsect != sprite[i].sectnum) {
				engine.setsprite(i, prevx, prevy, prevz);
				sprite[i].ang = arbitraryangle();
			}

			i = nexti;
		}

		i = headspritestat[PINBALL];
		while (i >= 0) {
			nexti = nextspritestat[i];

			ext = sprite[i].extra;
			if (validext(ext) == 0) {
				noextthrowerror(i, 19);
			}
			spriteXT[ext].target = 0;

			prevx = sprite[i].x;
			prevy = sprite[i].y;
			prevz = sprite[i].z;
			prevsect = sprite[i].sectnum;

			dax = (((sintable[(sprite[i].ang + 512) & 2047]) * sprite[i].xvel) << 3);
			day = (((sintable[sprite[i].ang & 2047]) * sprite[i].yvel) << 3);
			movestat = flymovesprite(i, dax, day, 0, 1024, 1024, NORMALCLIP);

			if ((movestat & 0xC000) == 32768) {
				engine.setsprite(i, prevx, prevy, prevz);
				sprite[i].ang = walldeflect(movestat & 0x0FFF, sprite[i].ang);
				movestat = flymovesprite(i, dax, day, 0, 1024, 1024, NORMALCLIP);
			} else if ((movestat & 0xC000) == 49152) {
				hitsprite = (short) (movestat & 0x0FFF);
				engine.setsprite(i, prevx, prevy, prevz);
				int hit = playerhit(hitsprite);
				pnum = playerhit;
				if (hit != 0) {
					spriteXT[ext].target = pnum;
					newstatus(i, ATTACK);
				} else {
					sprite[i].ang = spritedeflect(hitsprite, sprite[i].ang);
					movestat = flymovesprite(i, dax, day, 0, 1024, 1024, NORMALCLIP);
				}
			}
			if (movestat != 0) {
				sprite[i].ang = arbitraryangle();
			}

			i = nexti;
		}

		i = headspritestat[TIMEBOMB];
		while (i >= 0) {
			nexti = nextspritestat[i];

			sprite[i].hitag--;
			if (sprite[i].hitag <= 0) {
				if (sector[sprite[i].sectnum].lotag == 4) {
					splash(i);
				}
				engine.changespritestat(i, INACTIVE);
				genexplosion2(i);
				jsdeletesprite(i);
			}
			i = nexti;
		}

		i = headspritestat[BLOODFLOW];
		while (i >= 0) {
			nexti = nextspritestat[i];

			sprite[i].hitag++;

			if (sprite[i].hitag >= (TICSPERFRAME * 6)) {
				sprite[i].picnum++;
				sprite[i].hitag = 0;
				sprite[i].lotag++;
			}
			if (sprite[i].lotag == 5) {
				engine.changespritestat(i, INANIMATE);
			}

			i = nexti;
		}

		i = headspritestat[DROPSIES];
		while (i != -1) {
			boolean dropsiescontinue = false;
			nexti = nextspritestat[i];

			dax = (((sprite[i].xvel) * TICSPERFRAME) << 7);
			day = (((sprite[i].yvel) * TICSPERFRAME) << 7);
			daz = 0;

			hitobject = movesprite(i, dax, day, daz, 4 << 8, 4 << 8, 1);

			sprite[i].z += sprite[i].zvel;
			sprite[i].zvel += (TICSPERFRAME << 2);
			Tile pic = engine.getTile(sprite[i].picnum);

			if (sprite[i].z < zr_ceilz + ((pic.getHeight() / 2) << 6)) {
				sprite[i].z = zr_ceilz + ((pic.getHeight() / 2) << 6);
				sprite[i].zvel = (short) -(sprite[i].zvel >> 1);
			}
			if (sprite[i].z > zr_florz - ((pic.getHeight() / 2) << 6)) {
				sprite[i].z = zr_florz - ((pic.getHeight() / 2) << 6);
				sprite[i].zvel = (short) -(sprite[i].zvel >> 1);
			}

			dax = sprite[i].xvel;
			day = sprite[i].yvel;
			dist = dax * dax + day * day;
			if (dist < 46000) {
				if (sector[sprite[i].sectnum].lotag == 4) {
					if ((sprite[i].cstat & 128) == 0) {
						zoffs = -((pic.getHeight() * sprite[i].yrepeat) << 1);
					} else {
						zoffs = 0;
					}
					sprite[i].z = sector[sprite[i].sectnum].floorz - zoffs;
					engine.changespritestat(i, FLOATING);
					sprite[i].lotag = 0;
					sprite[i].hitag = 0;
					sprite[i].xvel = 1;
					sprite[i].yvel = 1;
				} else {
					engine.changespritestat(i, INANIMATE);
				}
				sprite[i].z = sector[sprite[i].sectnum].floorz;
				sprite[i].z -= (pic.getHeight() / 2) << 6;

                dropsiescontinue = true;
			}
			if (!dropsiescontinue) {
				if (mulscale(krand_intercept("STAT2934"), dist, 30) == 0) {
					sprite[i].xvel -= ksgn(sprite[i].xvel);
					sprite[i].yvel -= ksgn(sprite[i].yvel);
					sprite[i].zvel -= ksgn(sprite[i].zvel);
				}
			}

			i = nexti;
		}

		i = headspritestat[7]; // Go through bomb spriral-explosion sprites
		while (i >= 0) {
			nexti = nextspritestat[i];
			game.pInt.setsprinterpolate(i, sprite[i]);
			sprite[i].x += ((sprite[i].xvel * TICSPERFRAME) >> 4);
			sprite[i].y += ((sprite[i].yvel * TICSPERFRAME) >> 4);
			sprite[i].z += ((sprite[i].zvel * TICSPERFRAME) >> 4);

			sprite[i].zvel += (TICSPERFRAME << 7);
			if (sprite[i].z < sector[sprite[i].sectnum].ceilingz + (4 << 8)) {
				sprite[i].z = sector[sprite[i].sectnum].ceilingz + (4 << 8);
				sprite[i].zvel = (short) -(sprite[i].zvel >> 1);
			}
			if (sprite[i].z > sector[sprite[i].sectnum].floorz - (4 << 8)) {
				sprite[i].z = sector[sprite[i].sectnum].floorz - (4 << 8);
				sprite[i].zvel = (short) -(sprite[i].zvel >> 1);
			}

			sprite[i].xrepeat = (byte) (sprite[i].lotag >> 2);
			sprite[i].yrepeat = (byte) (sprite[i].lotag >> 2);

			sprite[i].lotag -= TICSPERFRAME;
			if (sprite[i].lotag < 0)
				jsdeletesprite(i);

			i = nexti;
		}

		i = headspritestat[TOSS];
		while (i != -1) {
			boolean tosscontinue = false;
			nexti = nextspritestat[i];

			dax = (((sprite[i].xvel) * TICSPERFRAME) << 11);
			day = (((sprite[i].yvel) * TICSPERFRAME) << 11);
			daz = 0;
			movestat = kenmovesprite(i, dax, day, daz, 4 << 8, 4 << 8, 1);
			switch (movestat & 0xC000) {
			// break stuff, but dont hurt enemies unless direct hit on head
			case 49152:
				hitsprite = (short) (movestat & 0x0FFF);
				ext = sprite[hitsprite].extra;
				if (validext(ext) != 0 && (spriteXT[ext].deathpic != 0)) {
					switch (sprite[hitsprite].statnum) {
					case INANIMATE:
						damagesprite(hitsprite, 20);
						break;
					}
				}
				break;
			}

			sprite[i].z += sprite[i].zvel;
			sprite[i].zvel += (TICSPERFRAME << 5);
			Tile pic = engine.getTile(BOMB);

			if (sprite[i].z < zr_ceilz + (pic.getHeight() << 6)) {
				sprite[i].z = zr_ceilz + (pic.getHeight() << 6);
				sprite[i].zvel = (short) -(sprite[i].zvel >> 1);
			}
			if (sprite[i].z > zr_florz - (pic.getHeight() << 4)) {
				switch (zr_florhit & 0xC000) {
				case 49152:
					// direct hit on head
					hitsprite = (short) (zr_florhit & 0x0FFF);
					int hit = playerhit(hitsprite);
					pnum = playerhit;
					if (hit != 0) {
						changehealth(pnum, -40);
					} else if (sprite[hitsprite].extra != -1) {
						playsound(S_BUSHIT, sprite[hitsprite].x, sprite[hitsprite].y, 0, ST_NOUPDATE);
						damagesprite(hitsprite, 10);
					}
					break;
				case 16384:
					if ((sector[zr_florhit & 0x0FFF].lotag == 4) && (sprite[i].picnum != RATTHROWPIC)) {
						if ((sprite[i].cstat & 128) == 0)
							zoffs = -((engine.getTile(sprite[i].picnum).getHeight() * sprite[i].yrepeat) << 1);
						else
							zoffs = 0;
						game.pInt.setsprinterpolate(i, sprite[i]);
						sprite[i].z = sector[sprite[i].sectnum].floorz - zoffs;
						splash(i);
						switch (sprite[i].picnum) {
						case TUBEBOMB:
							sprite[i].picnum = TUBEBOMB + 1;
							sprite[i].hitag = (short) (krand_intercept("STAT3031") & 255);
							engine.changespritestat(i, TIMEBOMB);
							break;
						case DARTBOMB:
							sprite[i].hitag = (short) (krand_intercept("STAT3035") & 255);
							sprite[i].picnum = DARTBOMB + 1;
							engine.changespritestat(i, TIMEBOMB);
							break;
						default:
							sprite[i].lotag = 0;
							sprite[i].hitag = 0;
							sprite[i].xvel = 1;
							sprite[i].yvel = 1;
							engine.changespritestat(i, FLOATING);
							break;
						}
						tosscontinue = true;
					}
					break;
				}
				if (!tosscontinue) {
					game.pInt.setsprinterpolate(i, sprite[i]);
					sprite[i].z = zr_florz - (pic.getHeight() << 4);
					sprite[i].zvel = (short) -(sprite[i].zvel >> 1);
					sprite[i].hitag++;
				}
			}
			if (!tosscontinue) {
				dax = sprite[i].xvel;
				day = sprite[i].yvel;
				dist = dax * dax + day * day;
				if (mulscale(krand_intercept("STAT3057"), dist, 30) == 0) {
					sprite[i].xvel -= ksgn(sprite[i].xvel);
					sprite[i].yvel -= ksgn(sprite[i].yvel);
					sprite[i].zvel -= ksgn(sprite[i].zvel);
				}
				if (sprite[i].hitag >= 3) {
					switch (sprite[i].picnum) {
					case RATTHROWPIC:
						ext = sprite[i].extra;
						if (validext(ext) == 0) {
							jsdeletesprite(i);
							break;
						}
						game.pInt.setsprinterpolate(i, sprite[i]);
						sprite[i].z = sector[sprite[i].sectnum].floorz;
						sprite[i].ang = arbitraryangle();
						sprite[i].picnum = RATPIC;
						spriteXT[ext].basestat = RODENT;
						newstatus(i, RODENT);
						sprite[i].xvel = 4;
						sprite[i].yvel = 4;
						sprite[i].zvel = 0;
						sprite[i].lotag = 2004;
						sprite[i].hitag = 0;
						break;
					case TUBEBOMB:
						sprite[i].picnum = TUBEBOMB + 1;
						sprite[i].hitag = (short) (krand_intercept("STAT3083") & 255);
						engine.changespritestat(i, TIMEBOMB);
						break;
					case DARTBOMB:
						sprite[i].picnum = DARTBOMB + 1;
						sprite[i].hitag = (short) (krand_intercept("STAT3088") & 255);
						engine.changespritestat(i, TIMEBOMB);
						break;
					default:
						sprite[i].xvel = 0;
						sprite[i].yvel = 0;
						sprite[i].zvel = 0;
						sprite[i].lotag = 2004;
						if (zr_florz != sector[sprite[i].sectnum].floorz) {
							game.pInt.setsprinterpolate(i, sprite[i]);
							sprite[i].z = zr_florz;
							engine.changespritestat(i, STACKED);
						} else {
							engine.changespritestat(i, INANIMATE);
						}
						break;
					}
				}
			}

			i = nexti;
		}

		i = headspritestat[AMBUSH];
		while (i >= 0) {
			boolean ambushcontinue = false;
			nexti = nextspritestat[i];

			ext = sprite[i].extra;
			if (validext(ext) == 0) {
				noextthrowerror(i, 1);
			}

			mindist = 0x7fffffff;
			target = connecthead;
			for (p = connecthead; p >= 0; p = connectpoint2[p]) {
				dist = abs(sprite[i].x - gPlayer[p].posx) + abs(sprite[i].y - gPlayer[p].posy);
				if (dist < mindist) {
					mindist = dist;
					target = p;
				}
			}
			if (mindist > DONTBOTHERDISTANCE) {
				ambushcontinue = true;
			}

			if (!ambushcontinue) {
				dist = mindist;
				spriteXT[ext].target = target;

				if ((spriteXT[i].aimask & AI_JUSTSHOTAT) != 0 || isvisible(i, target)) {
					if (spriteXT[ext].morphpic != 0) {
						newstatus(i, MORPH);
					} else {
						ambushyell(i, sprite[i].extra);
						newstatus(i, spriteXT[ext].basestat);
					}
				}
			}

			spriteXT[ext].aimask &= ~AI_JUSTSHOTAT;
			spriteXT[ext].aimask &= ~AI_WASDRAWN;

			i = nexti;
		}

		i = headspritestat[STALK];
		while (i >= 0) {
			boolean stalkcontinue = false;
			nexti = nextspritestat[i];

			ext = sprite[i].extra;
			if (validext(ext) == 0) {
				noextthrowerror(i, 2);
			}

			mindist = 0x7fffffff;
			target = connecthead;
			for (p = connecthead; p >= 0; p = connectpoint2[p]) {
				dist = abs(sprite[i].x - gPlayer[p].posx) + abs(sprite[i].y - gPlayer[p].posy);
				if (dist < mindist) {
					mindist = dist;
					target = p;
				}
			}
			if (mindist > DONTBOTHERDISTANCE) {
				stalkcontinue = true;
			}
			if (!stalkcontinue) {
				dist = mindist;
				spriteXT[ext].target = target;

				targx = gPlayer[target].posx;
				targy = gPlayer[target].posy;
				targz = gPlayer[target].posz;
				targsect = gPlayer[target].cursectnum;

				prevx = sprite[i].x;
				prevy = sprite[i].y;
				prevz = sprite[i].z;
				prevsect = sprite[i].sectnum;

				daang = (short) (engine.getangle(targx - sprite[i].x, targy - sprite[i].y) & 2047);

				// USES SPRITESORTLIST - NOT MULTIPLAYER COMPATIBLE !

				if (((spriteXT[ext].aimask) & AI_WASDRAWN) != 0) {
					if (((sprite[i].ang + 2048 - daang) & 2047) < 1024) {
						sprite[i].ang = (short) ((sprite[i].ang + 2048 - (TICSPERFRAME << 1)) & 2047);
					} else {
						sprite[i].ang = (short) ((sprite[i].ang + (TICSPERFRAME << 1)) & 2047);
					}
					if (RMOD16("STAT3291") == 0)
						attackifclose(i, target, dist);
					if (sprite[i].statnum != ATTACK) {
						daang = (short) ((daang + 1024) & 2047);
						dax = (((sintable[(sprite[i].ang + 512) & 2047]) * sprite[i].xvel) << 3);
						day = (((sintable[sprite[i].ang & 2047]) * sprite[i].yvel) << 3);
						movestat = movesprite(i, dax, day, 0, 1024, 1024, CLIFFCLIP);
						switch (movestat & 0xC000) {
						case 32768: // blocked by a wall
							sprite[i].ang = walldeflect(movestat & 0x0FFF, sprite[i].ang);
							break;
						case 49152: // blocked by a sprite
							sprite[i].ang = spritedeflect(movestat & 0x0FFF, sprite[i].ang);
							break;
						case 16384:
							sprite[i].ang = arbitraryangle();
							break;
						}
						if (movestat != 0) {
							dax = (((sintable[(sprite[i].ang + 512) & 2047]) * sprite[i].xvel) << 3);
							day = (((sintable[sprite[i].ang & 2047]) * sprite[i].yvel) << 3);
							movestat = movesprite(i, dax, day, 0, 1024, 1024, CLIFFCLIP);
							if (movestat != 0)
								sprite[i].ang = arbitraryangle();
						}
					}
				} else {
					if (((sprite[i].ang + 2048 - daang) & 2047) < 1024) {
						sprite[i].ang = (short) ((sprite[i].ang + 2048 - (TICSPERFRAME << 1)) & 2047);
					} else {
						sprite[i].ang = (short) ((sprite[i].ang + (TICSPERFRAME << 1)) & 2047);
					}
					dax = (((sintable[(sprite[i].ang + 512) & 2047]) * sprite[i].xvel) << 4);
					day = (((sintable[sprite[i].ang & 2047]) * sprite[i].yvel) << 4);
					movestat = movesprite(i, dax, day, 0, 1024, 1024, CLIFFCLIP);
					switch (movestat & 0xC000) {
					case 32768: // blocked by a wall
						newstatus(i, SQUAT);
						sprite[i].ang = walldeflect(movestat & 0x0FFF, sprite[i].ang);
						break;
					case 49152: // blocked by a sprite
						newstatus(i, SQUAT);
						sprite[i].ang = spritedeflect(movestat & 0x0FFF, sprite[i].ang);
						break;
					case 16384:
						sprite[i].ang = arbitraryangle();
						break;
					}
					if (movestat != 0) {
						dax = (((sintable[(sprite[i].ang + 512) & 2047]) * sprite[i].xvel) << 3);
						day = (((sintable[sprite[i].ang & 2047]) * sprite[i].yvel) << 3);
						movestat = movesprite(i, dax, day, 0, 1024, 1024, CLIFFCLIP);
						if (movestat != 0)
							sprite[i].ang = arbitraryangle();
					}
				}

				if (sprite[i].sectnum != prevsect) {
					if (sector[sprite[i].sectnum].lotag == SECT_LOTAG_OFFLIMITS_ALL) {
						engine.setsprite(i, prevx, prevy, prevz);
						sprite[i].ang = arbitraryangle();
					} else {
						enemynewsector(i);
					}
				}
			}

			spriteXT[ext].aimask &= ~AI_JUSTSHOTAT;
			spriteXT[ext].aimask &= ~AI_WASDRAWN;

			i = nexti;
		}

		i = headspritestat[CHASE];
		while (i >= 0) {
			boolean chasecontinue = false;
			nexti = nextspritestat[i];

			ext = sprite[i].extra;
			if (validext(ext) == 0) {
				noextthrowerror(i, 3);
			}

			mindist = 0x7fffffff;
			target = connecthead;
			for (p = connecthead; p >= 0; p = connectpoint2[p]) {
				dist = abs(sprite[i].x - gPlayer[p].posx) + abs(sprite[i].y - gPlayer[p].posy);
				if (dist < mindist) {
					mindist = dist;
					target = p;
				}
			}
			if (mindist > DONTBOTHERDISTANCE) {
				chasecontinue = true;
			}
			while (!chasecontinue) {
				dist = mindist;
				spriteXT[ext].target = target;

				targx = gPlayer[target].posx;
				targy = gPlayer[target].posy;
				targz = gPlayer[target].posz;
				targsect = gPlayer[target].cursectnum;

				prevx = sprite[i].x;
				prevy = sprite[i].y;
				prevz = sprite[i].z;
				prevsect = sprite[i].sectnum;

				daang = (short) (engine.getangle(targx - sprite[i].x, targy - sprite[i].y) & 2047);

				if (game.nNetMode != NetMode.Multiplayer && ((spriteXT[ext].aimask) & AI_CRITICAL) != 0) {
					if (((spriteXT[ext].fxmask) & (FX_ANDROID | FX_HOLOGRAM)) == 0) {
						sprite[i].ang = (short) ((daang + 1024) & 2047);
						newstatus(i, FLEE);
						if (sprite[i].statnum == FLEE) {
							chasecontinue = true;
							break;
						}

					}
				}

				// can player see target if they squat ?
				seecan = 0;

				if (sprite[i].sectnum >= 0 && targsect >= 0 && engine.cansee(targx, targy, targz, targsect, sprite[i].x,
						sprite[i].y, sprite[i].z - (engine.getTile(sprite[i].picnum).getHeight() << 6), sprite[i].sectnum)) {
					seecan = 1;
				}

				if (seecan == 1) {
					if (((sprite[i].ang + 2048 - daang) & 2047) < 1024) {
						sprite[i].ang = (short) ((sprite[i].ang + 2048 - (TICSPERFRAME << 1)) & 2047);
					} else {
						sprite[i].ang = (short) ((sprite[i].ang + (TICSPERFRAME << 1)) & 2047);
					}
					if (RMOD4("STAT3427") == 0) {
						attackifclose(i, target, dist);
					}
					if (sprite[i].statnum != ATTACK) {
						dax = (((sintable[(sprite[i].ang + 512) & 2047]) * sprite[i].xvel) << 3);
						day = (((sintable[sprite[i].ang & 2047]) * sprite[i].yvel) << 3);
						movestat = movesprite(i, dax, day, 0, 1024, 1024, CLIFFCLIP);
						switch (movestat & 0xC000) {
						case 32768: // blocked by a wall
							sprite[i].ang = walldeflect(movestat & 0x0FFF, sprite[i].ang);
							break;
						case 49152: // blocked by a sprite
							sprite[i].ang = spritedeflect(movestat & 0x0FFF, sprite[i].ang);
							break;
						case 16384:
							sprite[i].ang = arbitraryangle();
							break;
						}
						if (movestat != 0) {
							dax = (((sintable[(sprite[i].ang + 512) & 2047]) * sprite[i].xvel) << 3);
							day = (((sintable[sprite[i].ang & 2047]) * sprite[i].yvel) << 3);
							movestat = movesprite(i, dax, day, 0, 1024, 1024, CLIFFCLIP);
							if (movestat != 0)
								sprite[i].ang = arbitraryangle();
						}
					}
				} else {
					if (((sprite[i].ang + 2048 - daang) & 2047) < 1024) {
						sprite[i].ang = (short) ((sprite[i].ang + 2048 - (TICSPERFRAME << 1)) & 2047);
					} else {
						sprite[i].ang = (short) ((sprite[i].ang + (TICSPERFRAME << 1)) & 2047);
					}
					dax = (((sintable[(sprite[i].ang + 512) & 2047]) * sprite[i].xvel) << 3);
					day = (((sintable[sprite[i].ang & 2047]) * sprite[i].yvel) << 3);
					movestat = movesprite(i, dax, day, 0, 1024, 1024, CLIFFCLIP);
					switch (movestat & 0xC000) {
					case 32768: // blocked by a wall
						newstatus(i, SQUAT);
						sprite[i].ang = walldeflect(movestat & 0x0FFF, sprite[i].ang);
						break;
					case 49152: // blocked by a sprite
						newstatus(i, SQUAT);
						sprite[i].ang = spritedeflect(movestat & 0x0FFF, sprite[i].ang);
						break;
					case 16384:
						sprite[i].ang = arbitraryangle();
						break;
					}
					if (movestat != 0) {
						dax = (((sintable[(sprite[i].ang + 512) & 2047]) * sprite[i].xvel) << 3);
						day = (((sintable[sprite[i].ang & 2047]) * sprite[i].yvel) << 3);
						movestat = movesprite(i, dax, day, 0, 1024, 1024, CLIFFCLIP);
						if (movestat != 0)
							sprite[i].ang = arbitraryangle();
					}
				}
				if (sprite[i].sectnum != prevsect) {
					if (sector[sprite[i].sectnum].lotag == SECT_LOTAG_OFFLIMITS_ALL) {
						engine.setsprite(i, prevx, prevy, prevz);
						sprite[i].ang = arbitraryangle();
					} else {
						enemynewsector(i);
					}
				}
				break;
			}

			spriteXT[ext].aimask &= ~AI_JUSTSHOTAT;
			spriteXT[ext].aimask &= ~AI_WASDRAWN;

			i = nexti;
		}

		i = headspritestat[GUARD];
		while (i >= 0) {
			boolean guardcontinue = false;
			nexti = nextspritestat[i];

			ext = sprite[i].extra;
			if (validext(ext) == 0) {
				noextthrowerror(i, 4);
			}

			mindist = 0x7fffffff;
			target = connecthead;
			for (p = connecthead; p >= 0; p = connectpoint2[p]) {
				dist = abs(sprite[i].x - gPlayer[p].posx) + abs(sprite[i].y - gPlayer[p].posy);
				if (dist < mindist) {
					mindist = dist;
					target = p;
				}
			}
			if (mindist > DONTBOTHERDISTANCE) {
				guardcontinue = true;
			}
			while (!guardcontinue) {
				dist = mindist;
				spriteXT[ext].target = target;

				targx = gPlayer[target].posx;
				targy = gPlayer[target].posy;
				targz = gPlayer[target].posz;
				targsect = gPlayer[target].cursectnum;

				prevx = sprite[i].x;
				prevy = sprite[i].y;
				prevz = sprite[i].z;
				prevsect = sprite[i].sectnum;

				if ((((spriteXT[ext].aimask) & AI_JUSTSHOTAT) != 0)) {
					if (engine.cansee(targx, targy, targz, targsect, sprite[i].x, sprite[i].y,
							sprite[i].z - (engine.getTile(sprite[i].picnum).getHeight() << 7), sprite[i].sectnum)) {
						sprite[i].ang = engine.getangle(targx - sprite[i].x, targy - sprite[i].y);
						spriteXT[ext].aimask |= AI_WASATTACKED; // guard needs to take action
					}
					guardcontinue = true;
					break;
				}

				if ((gPlayer[target].drawweap) != 0 && isvisible(i, target)
						&& ((spriteXT[ext].aimask) & (AI_WASATTACKED | AI_ENCROACHMENT)) == 0) {
					givewarning(i, ext);
					sprite[i].ang = engine.getangle(targx - sprite[i].x, targy - sprite[i].y);
					sprite[i].picnum = (short) (spriteXT[ext].attackpic + 1);
					if (dist < 1024)
						newstatus(i, ATTACK);

					guardcontinue = true;
					break;
				} else {
					sprite[i].picnum = (short) spriteXT[ext].basepic;
				}

				switch ((spriteXT[ext].aimask) & (AI_WASATTACKED | AI_ENCROACHMENT)) {
				case 0:
					spriteXT[ext].aimask &= ~AI_GAVEWARNING;
					if (RMOD16("STAT3561") == 0) {
						sprite[i].ang = engine.getangle(targx - sprite[i].x, targy - sprite[i].y);
						newstatus(i, STANDING);
					} else {
						dax = (((sintable[(sprite[i].ang + 512) & 2047]) * sprite[i].xvel) << 3);
						day = (((sintable[sprite[i].ang & 2047]) * sprite[i].yvel) << 3);
						movestat = movesprite(i, dax, day, 0, 1024, 1024, CLIFFCLIP);
						if (movestat != 0) {
							sprite[i].ang = arbitraryangle();
						}
					}
					break;
				default:
					if ((RMOD4("STAT3575") == 0) && (engine.cansee(targx, targy, targz, targsect, sprite[i].x,
							sprite[i].y, sprite[i].z - (engine.getTile(sprite[i].picnum).getHeight() << 7), sprite[i].sectnum))) {
						sprite[i].ang = engine.getangle(targx - sprite[i].x, targy - sprite[i].y);
						newstatus(i, ATTACK);
					} else {
						dax = (((sintable[(sprite[i].ang + 512) & 2047]) * (sprite[i].xvel + 2)) << 3);
						day = (((sintable[sprite[i].ang & 2047]) * (sprite[i].yvel + 2)) << 3);
						movestat = movesprite(i, dax, day, 0, 1024, 1024, CLIFFCLIP);
						if (movestat != 0) {
							sprite[i].ang = arbitraryangle();
						}
					}
					break;
				}

				if ((prevsect != sprite[i].sectnum)) {
					engine.setsprite(i, prevx, prevy, prevz);
					sprite[i].ang = arbitraryangle();
				}
				break;
			}

			spriteXT[ext].aimask &= ~AI_JUSTSHOTAT;
			spriteXT[ext].aimask &= ~AI_WASDRAWN;

			i = nexti;
		}

		i = headspritestat[FLEE];
		while (i >= 0) {
			boolean fleecontinue = false;
			nexti = nextspritestat[i];

			ext = sprite[i].extra;
			if (validext(ext) == 0) {
				noextthrowerror(i, 5);
			}

			mindist = 0x7fffffff;
			target = connecthead;
			for (p = connecthead; p >= 0; p = connectpoint2[p]) {
				dist = abs(sprite[i].x - gPlayer[p].posx) + abs(sprite[i].y - gPlayer[p].posy);
				if (dist < mindist) {
					mindist = dist;
					target = p;
				}
			}
			if ((gPlayer[target].drawweap == 0) || (mindist > DONTBOTHERDISTANCE)) {
				newstatus(i, spriteXT[ext].basestat);
				fleecontinue = true;
			}
			if (!fleecontinue) {
				dist = mindist;
				spriteXT[ext].target = target;

				targx = gPlayer[target].posx;
				targy = gPlayer[target].posy;
				targz = gPlayer[target].posz;
				targsect = gPlayer[target].cursectnum;

				prevx = sprite[i].x;
				prevy = sprite[i].y;
				prevz = sprite[i].z;
				prevsect = sprite[i].sectnum;

				int FLEESPEED = 5;
				dax = (((sintable[(sprite[i].ang + 512) & 2047]) * FLEESPEED) << 3);
				day = (((sintable[sprite[i].ang & 2047]) * FLEESPEED) << 3);
				movestat = movesprite(i, dax, day, 0, 1024, 1024, CLIFFCLIP);

				if (movestat != 0) {
					switch (movestat & 0xC000) {
					case 32768: // blocked by a wall
						sprite[i].ang = walldeflect(movestat & 0x0FFF, sprite[i].ang);
						break;
					case 49152: // blocked by a sprite
						sprite[i].ang = spritedeflect(movestat & 0x0FFF, sprite[i].ang);
						break;
					case 16384:
						sprite[i].ang = arbitraryangle();
						break;
					}
					if (engine.cansee(targx, targy, targz, targsect, sprite[i].x, sprite[i].y,
							sprite[i].z - (engine.getTile(sprite[i].picnum).getHeight() << 7), sprite[i].sectnum)) {
						attackifclose(i, target, dist);
						if (sprite[i].statnum != ATTACK) {
							daang = engine.getangle(targx - sprite[i].x, targy - sprite[i].y);
							sprite[i].ang = (short) ((daang + 1024) & 2047);
							movestat = movesprite(i, dax, day, 0, 1024, 1024, CLIFFCLIP);
							if ((movestat != 0) && RMOD2("STAT3663") != 0)
								newstatus(i, HIDE);
						}
					} else {
						if (RMOD3("STAT3668") == 0)
							newstatus(i, HIDE);
					}
				}

				if (sprite[i].sectnum != prevsect) {
					if (sector[sprite[i].sectnum].lotag == SECT_LOTAG_OFFLIMITS_ALL) {
						engine.setsprite(i, prevx, prevy, prevz);
						sprite[i].ang = arbitraryangle();
					} else {
						enemynewsector(i);
					}
				}
			}

			spriteXT[ext].aimask &= ~AI_JUSTSHOTAT;
			spriteXT[ext].aimask &= ~AI_WASDRAWN;

			i = nexti;
		}

		i = headspritestat[STROLL];
		while (i >= 0) {
			boolean strollcontinue = false;
			nexti = nextspritestat[i];

			ext = sprite[i].extra;
			if (validext(ext) == 0) {
				noextthrowerror(i, 6);
			}

			mindist = 0x7fffffff;
			target = connecthead;
			for (p = connecthead; p >= 0; p = connectpoint2[p]) {
				dist = abs(sprite[i].x - gPlayer[p].posx) + abs(sprite[i].y - gPlayer[p].posy);
				if (dist < mindist) {
					mindist = dist;
					target = p;
				}
			}
			if (mindist > DONTBOTHERDISTANCE) {
				strollcontinue = true;
			}

			while (!strollcontinue) {
				dist = mindist;
				spriteXT[ext].target = target;

				targx = gPlayer[target].posx;
				targy = gPlayer[target].posy;
				targz = gPlayer[target].posz;
				targsect = gPlayer[target].cursectnum;

				prevx = sprite[i].x;
				prevy = sprite[i].y;
				prevz = sprite[i].z;
				prevsect = sprite[i].sectnum;

				if (((spriteXT[ext].aimask) & AI_JUSTSHOTAT) != 0) {
					attackifclose(i, target, dist);
					if (sprite[i].statnum != ATTACK) {
						daang = (short) (engine.getangle(gPlayer[target].posx - sprite[i].x,
								gPlayer[target].posy - sprite[i].y) & 2047);
						sprite[i].ang = (short) ((daang + 1024) & 2047);
						fleescream(i, ext);
						newstatus(i, FLEE);
					}
					break; // goto strollcontinue;
				}

				if ((gPlayer[target].drawweap != 0) && isvisible(i, target)) {
					daang = (short) (engine.getangle(gPlayer[target].posx - sprite[i].x,
							gPlayer[target].posy - sprite[i].y) & 2047);
					sprite[i].ang = (short) ((daang + 1024) & 2047);

					if (dist < HIDEDISTANCE) {
						newstatus(i, HIDE);
					}
					if (sprite[i].statnum == HIDE) {
						hideplea(i, ext);
					} else {
						fleescream(i, ext);
						newstatus(i, FLEE);
					}
					break; // goto strollcontinue;
				}

				dax = (((sintable[(sprite[i].ang + 512) & 2047]) * sprite[i].xvel) << 3);
				day = (((sintable[sprite[i].ang & 2047]) * sprite[i].yvel) << 3);
				movestat = movesprite(i, dax, day, 0, 1024, 1024, CLIFFCLIP);

				if (movestat != 0) {
					if ((RMOD10("STAT3757") == 0) && (sector[sprite[i].sectnum].lotag != SECT_LOTAG_NOSTANDING)) {
						sprite[i].ang = (short) ((sprite[i].ang + 1024) & 2047);
						newstatus(i, STANDING);
					} else {
						switch (movestat & 0xC000) {
						case 32768: // blocked by a wall
							sprite[i].ang = walldeflect(movestat & 0x0FFF, sprite[i].ang);
							break;
						case 49152: // blocked by a sprite
							sprite[i].ang = spritedeflect(movestat & 0x0FFF, sprite[i].ang);
							break;
						case 16384:
							sprite[i].ang = arbitraryangle();
							break;
						}
					}
				}

				if (sprite[i].sectnum != prevsect) {
					if ((sector[sprite[i].sectnum].lotag == SECT_LOTAG_OFFLIMITS_CIVILLIAN)
							|| (sector[sprite[i].sectnum].lotag == SECT_LOTAG_OFFLIMITS_ALL)) {
						engine.setsprite(i, prevx, prevy, prevz);
						sprite[i].ang = arbitraryangle();
					} else {
						enemynewsector(i);
					}
				}
				break;
			}

			spriteXT[ext].aimask &= ~AI_JUSTSHOTAT;
			spriteXT[ext].aimask &= ~AI_WASDRAWN;

			i = nexti;
		}

		i = headspritestat[FLY];
		while (i >= 0) {
			boolean flycontinue = false;
			nexti = nextspritestat[i];

			ext = sprite[i].extra;
			if (validext(ext) == 0) {
				noextthrowerror(i, 7);
			}

			mindist = 0x7fffffff;
			target = connecthead;
			for (p = connecthead; p >= 0; p = connectpoint2[p]) {
				dist = abs(sprite[i].x - gPlayer[p].posx) + abs(sprite[i].y - gPlayer[p].posy);
				if (dist < mindist) {
					mindist = dist;
					target = p;
				}
			}
			if (mindist > DONTBOTHERDISTANCE) {
				flycontinue = true;
				;
			}
			if (!flycontinue) {
				dist = mindist;
				spriteXT[ext].target = target;

				if (spriteXT[ext].basepic != AUTOGUN) {
					dax = (((sintable[(sprite[i].ang + 512) & 2047]) * ((sprite[i].xvel) << 1)) << 3);
					day = (((sintable[sprite[i].ang & 2047]) * ((sprite[i].yvel) << 1)) << 3);
					daz = 0;
					movestat = flymovesprite(i, dax, day, daz, 1024, 1024, NORMALCLIP);
					if (movestat != 0) {
						sprite[i].ang = arbitraryangle();
					}
				}
				if (gPlayer[target].cursectnum >= 0 && engine.cansee(gPlayer[target].posx, gPlayer[target].posy,
						gPlayer[target].posz, gPlayer[target].cursectnum, sprite[i].x, sprite[i].y,
						sprite[i].z - (engine.getTile(sprite[i].picnum).getHeight() << 7), sprite[i].sectnum)) {
					if (spriteXT[ext].weapon == 0) {
						if (dist < 5120) {
							sprite[i].ang = engine.getangle(gPlayer[target].posx - sprite[i].x,
									gPlayer[target].posy - sprite[i].y);
						}
						if (dist < 1024) {
							sprite[i].ang = engine.getangle(gPlayer[target].posx - sprite[i].x,
									gPlayer[target].posy - sprite[i].y);
							if (RMOD4("STAT3835") == 0) {
								newstatus(i, ATTACK);
							}
						}
					} else {
						attackifclose(i, target, dist);
					}
				}
			}

			spriteXT[ext].aimask &= ~AI_JUSTSHOTAT;
			spriteXT[ext].aimask &= ~AI_WASDRAWN;

			i = nexti;
		}

		i = headspritestat[RODENT];
		while (i >= 0) {
			boolean rodentcontinue = false;
			nexti = nextspritestat[i];

			ext = sprite[i].extra;
			if (validext(ext) == 0) {
				noextthrowerror(i, 3);
			}

			mindist = 0x7fffffff;
			target = connecthead;
			for (p = connecthead; p >= 0; p = connectpoint2[p]) {
				dist = abs(sprite[i].x - gPlayer[p].posx) + abs(sprite[i].y - gPlayer[p].posy);
				if (dist < mindist) {
					mindist = dist;
					target = p;
				}
			}
			if (mindist > DONTBOTHERDISTANCE) {
				rodentcontinue = true;
			}
			if (!rodentcontinue) {
				dist = mindist;
				spriteXT[ext].target = target;

				targx = gPlayer[target].posx;
				targy = gPlayer[target].posy;
				targz = gPlayer[target].posz;
				targsect = gPlayer[target].cursectnum;

				prevx = sprite[i].x;
				prevy = sprite[i].y;
				prevz = sprite[i].z;
				prevsect = sprite[i].sectnum;

				if (((sprite[i].ang + 2048 - daang) & 2047) < 1024) {
					sprite[i].ang = (short) ((sprite[i].ang + 2048 - (TICSPERFRAME << 1)) & 2047);
				} else {
					sprite[i].ang = (short) ((sprite[i].ang + (TICSPERFRAME << 1)) & 2047);
				}
				dax = (((sintable[(sprite[i].ang + 512) & 2047]) * sprite[i].xvel) << 3);
				day = (((sintable[sprite[i].ang & 2047]) * sprite[i].yvel) << 3);

				movestat = movesprite(i, dax, day, 0, 1024, 1024, 2);

				switch (movestat & 0xC000) {
				case 32768: // blocked by a wall
					sprite[i].ang = walldeflect(movestat & 0x0FFF, sprite[i].ang);
					break;
				case 49152: // blocked by a sprite
					sprite[i].ang = spritedeflect(movestat & 0x0FFF, sprite[i].ang);
					break;
				case 16384:
					sprite[i].ang = arbitraryangle();
					break;
				}
			}

			spriteXT[ext].aimask &= ~AI_JUSTSHOTAT;
			spriteXT[ext].aimask &= ~AI_WASDRAWN;

			i = nexti;
		}

		i = headspritestat[STANDING];
		while (i >= 0) {
			nexti = nextspritestat[i];

			ext = sprite[i].extra;
			if (validext(ext) == 0) {
				noextthrowerror(i, 8);
			}

			mindist = 0x7fffffff;
			target = connecthead;
			for (p = connecthead; p >= 0; p = connectpoint2[p]) {
				dist = abs(sprite[i].x - gPlayer[p].posx) + abs(sprite[i].y - gPlayer[p].posy);
				if (dist < mindist) {
					mindist = dist;
					target = p;
				}
			}
			if (sector[sprite[i].sectnum].lotag == SECT_LOTAG_NOSTANDING) {
				sprite[i].lotag = 0;
				sprite[i].hitag = 0;
				sprite[i].picnum = (short) spriteXT[ext].basepic;
				newstatus(i, spriteXT[ext].basestat);
			}

			if (((spriteXT[ext].aimask) & AI_JUSTSHOTAT) != 0) {
				sprite[i].lotag = 0;
			}
			if (((spriteXT[ext].aimask) & AI_ENCROACHMENT) != 0) {
				sprite[i].lotag = 0;
			}
			if ((gPlayer[target].drawweap != 0)) {
				if (isvisible(i, target))
					sprite[i].lotag = 0;
			}

			sprite[i].lotag -= (TICSPERFRAME);
			if (sprite[i].lotag < 0) {
				sprite[i].lotag = 0;
				newstatus(i, spriteXT[ext].basestat);
			}

			spriteXT[ext].aimask &= ~AI_JUSTSHOTAT;
			spriteXT[ext].aimask &= ~AI_WASDRAWN;

			i = nexti;
		}

		i = headspritestat[ATTACK];
		while (i >= 0) {
			nexti = nextspritestat[i];

			ext = sprite[i].extra;
			if (validext(ext) == 0) {
				noextthrowerror(i, 9);
			}

			if (sprite[i].lotag == sprite[i].hitag) { // fire instance
				enemyshootgun(i, sprite[i].x, sprite[i].y, sprite[i].z, sprite[i].ang, 100, sprite[i].sectnum,
						spriteXT[ext].weapon);
			}

			sprite[i].lotag -= (TICSPERFRAME);

			if (sprite[i].lotag < 0) {
				sprite[i].lotag = 0;
				if (((spriteXT[ext].aimask) & AI_TIMETODODGE) != 0) {
					spriteXT[ext].aimask &= ~AI_TIMETODODGE;
					newstatus(i, DODGE);
				} else {
					newstatus(i, spriteXT[ext].basestat);
				}
			}

			spriteXT[ext].aimask &= ~AI_JUSTSHOTAT;
			spriteXT[ext].aimask &= ~AI_WASDRAWN;

			i = nexti;
		}

		i = headspritestat[DELAYEDATTACK];
		while (i >= 0) {
			nexti = nextspritestat[i];

			ext = sprite[i].extra;
			if (validext(ext) == 0) {
				noextthrowerror(i, 10);
			}

			sprite[i].lotag -= (TICSPERFRAME);

			if (sprite[i].lotag < 0) {
				sprite[i].lotag = 0;
				newstatus(i, ATTACK);
			}

			spriteXT[ext].aimask &= ~AI_JUSTSHOTAT;
			spriteXT[ext].aimask &= ~AI_WASDRAWN;

			i = nexti;
		}

		i = headspritestat[SQUAT];
		while (i >= 0) {
			nexti = nextspritestat[i];

			ext = sprite[i].extra;
			if (validext(ext) == 0) {
				noextthrowerror(i, 11);
			}

			if (sector[sprite[i].sectnum].lotag == SECT_LOTAG_NOSTANDING) {
				sprite[i].lotag = 0;
				sprite[i].hitag = 0;
				sprite[i].picnum = (short) spriteXT[ext].basepic;
				newstatus(i, spriteXT[ext].basestat);
			}

			sprite[i].lotag -= (TICSPERFRAME);
			if (sprite[i].lotag < 0) {
				sprite[i].hitag -= (TICSPERFRAME);
				if (sprite[i].hitag < 0) {
					sprite[i].lotag = 0;
					sprite[i].hitag = 0;
					newstatus(i, UNSQUAT);
				}
			} else {
				sprite[i].picnum = (short) (spriteXT[ext].squatpic + ((47 - sprite[i].lotag) >> 4));
			}

			spriteXT[ext].aimask &= ~AI_JUSTSHOTAT;
			spriteXT[ext].aimask &= ~AI_WASDRAWN;

			i = nexti;
		}

		i = headspritestat[UNSQUAT];
		while (i >= 0) {
			nexti = nextspritestat[i];

			ext = sprite[i].extra;
			if (validext(ext) == 0) {
				noextthrowerror(i, 12);
			}

			sprite[i].lotag -= (TICSPERFRAME);
			if (sprite[i].lotag < 0) {
				sprite[i].lotag = 0;
				sprite[i].hitag = 0;
				sprite[i].picnum = (short) spriteXT[ext].basepic;
				newstatus(i, spriteXT[ext].basestat);
			} else {
				sprite[i].picnum = (short) (spriteXT[ext].squatpic + (((47) >> 4)) - ((47 - sprite[i].lotag) >> 4));
			}

			spriteXT[ext].aimask &= ~AI_JUSTSHOTAT;
			spriteXT[ext].aimask &= ~AI_WASDRAWN;

			i = nexti;
		}

		i = headspritestat[HIDE];
		while (i >= 0) {
			nexti = nextspritestat[i];

			ext = sprite[i].extra;
			if (validext(ext) == 0) {
				noextthrowerror(i, 13);
			}

			if (sector[sprite[i].sectnum].lotag == SECT_LOTAG_NOSTANDING) {
				sprite[i].lotag = 0;
				sprite[i].hitag = 0;
				sprite[i].picnum = (short) spriteXT[ext].basepic;
				newstatus(i, spriteXT[ext].basestat);
			}

			if (((spriteXT[ext].aimask) & AI_JUSTSHOTAT) != 0) {
				sprite[i].lotag = 0;
				sprite[i].hitag = 0;
			}

			sprite[i].lotag -= (TICSPERFRAME);
			if (sprite[i].lotag < 0) {
				sprite[i].hitag -= (TICSPERFRAME);
				if (sprite[i].hitag < 0) {
					sprite[i].lotag = 0;
					sprite[i].hitag = 0;
					newstatus(i, UNHIDE);
				}
			} else {
				sprite[i].picnum = (short) (spriteXT[ext].squatpic + ((47 - sprite[i].lotag) >> 4));
			}

			spriteXT[ext].aimask &= ~AI_JUSTSHOTAT;
			spriteXT[ext].aimask &= ~AI_WASDRAWN;

			i = nexti;
		}

		i = headspritestat[UNHIDE];
		while (i >= 0) {
			nexti = nextspritestat[i];

			ext = sprite[i].extra;
			if (validext(ext) == 0) {
				noextthrowerror(i, 14);
			}

			sprite[i].lotag -= (TICSPERFRAME);
			if (sprite[i].lotag < 0) {
				sprite[i].lotag = 0;
				sprite[i].hitag = 0;
				sprite[i].picnum = (short) spriteXT[ext].basepic;
				newstatus(i, FLEE);
			} else {
				sprite[i].picnum = (short) (spriteXT[ext].squatpic + (((47) >> 4)) - ((47 - sprite[i].lotag) >> 4));
			}

			spriteXT[ext].aimask &= ~AI_JUSTSHOTAT;
			spriteXT[ext].aimask &= ~AI_WASDRAWN;

			i = nexti;
		}

		i = headspritestat[PAIN];
		while (i >= 0) {
			nexti = nextspritestat[i];

			ext = sprite[i].extra;
			if (validext(ext) == 0) {
				noextthrowerror(i, 15);
			}

			sprite[i].lotag -= (TICSPERFRAME);
			if (sprite[i].lotag < 0) {
				sprite[i].lotag = 0;
				newstatus(i, spriteXT[ext].basestat);
			}

			i = nexti;
		}

		i = headspritestat[PLRVIRUS];
		while (i >= 0) {
			nexti = nextspritestat[i];

			host = sprite[i].owner;
			if (!validplayer(host)) {
				game.ThrowError("plrvirus lost host");
			}

			game.pInt.setsprinterpolate(i, sprite[i]);
			sprite[i].x = gPlayer[host].posx;
			sprite[i].y = gPlayer[host].posy;
			sprite[i].z = gPlayer[host].posz + (8 << 8);

			sprite[i].lotag -= (TICSPERFRAME);
			if ((sprite[i].lotag > 0) && ((sprite[i].lotag & 3) == 0)) {
				if (changehealth(host, -64)) {
					sprite[i].lotag = 0;
				}
			}
			if (sprite[i].lotag <= 0) {
				changehealth(host, -8192);
				if (host == screenpeek) {
					showmessage("BURNED");
				}
				jsdeletesprite(i);
			}

			i = nexti;
		}

		i = headspritestat[VIRUS];
		while (i >= 0) {
			nexti = nextspritestat[i];

			host = sprite[i].owner;
			if ((host < 0) || (host >= MAXSPRITES) || (sprite[host].statnum >= MAXSTATUS)) {
				jsdeletesprite(i);
				i = nexti;
				continue;
			}
			game.pInt.setsprinterpolate(i, sprite[i]);
			sprite[i].x = sprite[host].x;
			sprite[i].y = sprite[host].y;
			if (sprite[i].picnum == FIREPIC) {
				sprite[i].z = sprite[host].z - (engine.getTile(sprite[host].picnum).getHeight() << 4);
			} else {
				sprite[i].z = sprite[host].z;
			}

			if (sprite[i].picnum == FIREPIC) {
				sprite[i].hitag += 4;
				if (sprite[i].hitag >= (TICSPERFRAME << 3)) {
					sprite[host].shade++;
					sprite[i].hitag = 0;
				}
				if (sprite[host].shade > 12) {
					damagesprite(host, 1024);
					sprite[i].lotag = 0;
				}
			} else {
				sprite[i].lotag -= (TICSPERFRAME);
				if (damagesprite(host, 4) == 1) { // killed 'em
					// NOT NETWORK COMPATIBLE
					killscore(host, screenpeek, 0);
					sprite[i].lotag = 0;
				}
			}

			if (sprite[i].lotag <= 0) {
				jsdeletesprite(i);
			}

			i = nexti;
		}

		i = headspritestat[DEATH];
		while (i >= 0) {
			boolean deathcontinue = false;
			nexti = nextspritestat[i];
			ext = sprite[i].extra;
			if (validext(ext) == 0) {
				noextthrowerror(i, 16);
			}

			if (isanandroid(i) != 0) {
				androidexplosion(i);
				jsdeletesprite(i);
				showmessage("WAS AN ANDROID");
				deathcontinue = true;
			}
			if (!deathcontinue) {
				targsect = sprite[i].sectnum;
				if (sector[targsect].lotag != 4) {
					engine.getzrange(sprite[i].x, sprite[i].y, sprite[i].z - 1, sprite[i].sectnum,
							(sprite[i].clipdist) << 2, CLIPMASK0);
					game.pInt.setsprinterpolate(i, sprite[i]);
					if (sprite[i].z < zr_florz) {
						sprite[i].z += 1024;
					} else {
						sprite[i].z = zr_florz;
					}
				}

				sprite[i].lotag -= (TICSPERFRAME << 1);
				if (sprite[i].lotag < 0) {
					sprite[i].lotag = 0;
					tweakdeathdist(i);
					sprite[i].cstat &= 0xFFFE;
					if (isahologram(i) != 0) {
						showmessage("WAS A HOLOGRAM");
						jsdeletesprite(i);
					} else {
						missionaccomplished(i);
						newstatus(i, FLOATING);
					}
				} else {
					sprite[i].picnum = (short) (spriteXT[ext].deathpic + ((sprite[i].hitag - sprite[i].lotag) >> 4));
				}
			}

			i = nexti;
		}

		i = headspritestat[MIRRORMAN1];
		while (i >= 0) {
			nexti = nextspritestat[i];

			px = gPlayer[screenpeek].posx;
			py = gPlayer[screenpeek].posy;
			pz = gPlayer[screenpeek].posz;

			if ((px < -55326) || (px > -52873) || (py > 40521) || (py < 36596)) {
				i = nexti;
				continue;
			}
			deltapy = py - 36596;

			if (gPlayer[screenpeek].drawweap != 0)
				sprite[i].picnum = 1079;
			else
				sprite[i].picnum = 1074;

			prevx = sprite[i].x;
			prevy = sprite[i].y;
			prevz = sprite[i].z;
			game.pInt.setsprinterpolate(i, sprite[i]);
			sprite[i].x = px;
			sprite[i].y = 36596 - deltapy;
			sprite[i].z = pz + (42 << 8);

			sprite[i].ang = (short) BClampAngle(gPlayer[screenpeek].ang + 1024);

			i = nexti;
		}

		i = headspritestat[MIRRORMAN2];
		while (i >= 0) {
			nexti = nextspritestat[i];

			px = gPlayer[screenpeek].posx;
			py = gPlayer[screenpeek].posy;
			pz = gPlayer[screenpeek].posz;

			if ((px < -34792) || (px > -32404) || (py > 38980) || (py < 35074)) {
				i = nexti;
				continue;
			}
			deltapy = 38980 - py;

			if (gPlayer[screenpeek].drawweap != 0)
				sprite[i].picnum = 1079;
			else
				sprite[i].picnum = 1074;

			prevx = sprite[i].x;
			prevy = sprite[i].y;
			prevz = sprite[i].z;
			game.pInt.setsprinterpolate(i, sprite[i]);
			sprite[i].x = px;
			sprite[i].y = 38980 + deltapy;
			sprite[i].z = pz + (42 << 8);
			sprite[i].ang = (short) BClampAngle(gPlayer[screenpeek].ang + 1024);
			i = nexti;
		}

		i = headspritestat[PROJECTILE];
		while (i != -1) {
			nexti = nextspritestat[i];

			dax = (((sprite[i].xvel) * TICSPERFRAME) << 11);
			day = (((sprite[i].yvel) * TICSPERFRAME) << 11);
			daz = (((sprite[i].zvel) * TICSPERFRAME) >> 2); // was 3

			hitobject = movesprite(i, dax, day, daz, 4 << 8, 4 << 8, 1);

			if (hitobject != 0) {
				if ((hitobject & 0xc000) == 16384) { // hit a ceiling or floor
				} else if ((hitobject & 0xc000) == 32768) { // hit a wall
					// playsound( ??? , sprite[i].x,sprite[i].y, 0,ST_UPDATE);
				} else if ((hitobject & 0xc000) == 49152) { // hit a sprite
					hitsprite = (short) (hitobject & 4095);
					int hit = playerhit(hitsprite);
					pnum = playerhit;
					if (hit != 0) {
						playerpainsound(pnum);
						enemywoundplayer(pnum, sprite[i].owner, 6);
					} else {
						damagesprite(hitsprite, tekgundamage(6, sprite[i].x, sprite[i].y, sprite[i].z, hitsprite));
					}
				}
				jsdeletesprite(i);
			}

			i = nexti;
		}

		for (i = 0; i < MAXSPRITES; i++) {
			ext = sprite[i].extra;
			if (validext(ext) != 0) {
				if (((spriteXT[ext].fxmask) & FX_NXTSTTDEATH) != 0) {
					spriteXT[ext].fxmask &= (~FX_NXTSTTDEATH);
					newstatus(i, DEATH);
				} else if (((spriteXT[ext].fxmask) & FX_NXTSTTPAIN) != 0) {
					spriteXT[ext].fxmask &= (~FX_NXTSTTPAIN);
					newstatus(i, PAIN);
				}
			}
		}

		gunstatuslistcode();
	}

	public static void blastmark(int i) {
		int j;

		switch (sprite[i].statnum) {
		case GENEXPLODE2:
			break;
		default:
			return;
		}
		switch (sprite[i].picnum) {
		case BARRELL:
		case 175:
			break;
		default:
			return;
		}

		j = jsinsertsprite(sprite[i].sectnum, (short) 100);
		if (j != -1) {
			fillsprite(j, sprite[i].x, sprite[i].y, sector[sprite[i].sectnum].floorz, 0x00A2, 4, 0, 0, 34, 34, 0, 0,
					465, sprite[i].ang, 0, 0, 30, i + 4096, sprite[i].sectnum, 100, 0, 0, -1);
		}
	}

	public static void bombexplosion(int i) {
		int j;

		j = jsinsertsprite(sprite[i].sectnum, (short) 5);
		if (j != -1) {
			fillsprite(j, sprite[i].x, sprite[i].y, sprite[i].z, 0, -16, 0, 0, 34, 34, 0, 0, BOMBEXP1PIC, sprite[i].ang,
					sintable[(sprite[i].ang + 2560) & 2047] >> 6, sintable[(sprite[i].ang + 2048) & 2047] >> 6, 30,
					i + 4096, sprite[i].sectnum, 5, 32, 0, -1);
			playsound(S_RIC2, sprite[i].x, sprite[i].y, 0, ST_NOUPDATE);
		}
	}

	public static void forceexplosion(int i) {
		int j, k;

		j = jsinsertsprite(sprite[i].sectnum, (short) 5);
		if (j != -1) {
			fillsprite(j, sprite[i].x, sprite[i].y, sprite[i].z, 0, -4, 0, 32, 34, 34, 0, 0, FORCEBALLPIC,
					sprite[i].ang, 0, 0, 0, sprite[i].owner, sprite[i].sectnum, 5, 31, 0, -1);
		}

		for (k = 0; k < 6; k++) {
			j = jsinsertsprite(sprite[i].sectnum, (short) 7);
			if (j != -1) {
				fillsprite(j, sprite[i].x, sprite[i].y, sprite[i].z + (8 << 10), 2, -4, 0, 32, 24, 24, 0, 0,
						FORCEBALLPIC, sprite[i].ang, (krand_intercept("STAT4497") & 511) - 256,
						(krand_intercept("STAT4497") & 511) - 256, (krand_intercept("STAT4497") & 16384) - 8192,
						sprite[i].owner, sprite[i].sectnum, 7, 96, 0, -1);
			}
		}

		playsound(S_FORCEFIELD2, sprite[i].x, sprite[i].y, 0, ST_UPDATE);
	}

	public static void enemywoundplayer(int plrhit, int sprnum, int guntype) {
		int damage = 0;

		if (!validplayer(plrhit)) {
			return;
		}

		switch (guntype) {
		case 0:
			damage = 64;
			break;
		case 1:
		case 2:
		case 3:
		case 4:
		case 5:
			damage = 128;
			break;
		case 6:
		default:
			damage = 256;
			break;
		}
		switch (gDifficulty) {
		case 0:
		case 1:
			damage >>= 4;
			break;
		case 2:
			damage >>= 3;
			break;
		case 3:
			damage >>= 2;
			break;
		}

		changehealth(plrhit, -damage);
	}

	public static void androidexplosion(int i) {
		int j;

		j = jsinsertsprite(sprite[i].sectnum, (short) 5);
		if (j != -1) {
			fillsprite(j, sprite[i].x, sprite[i].y, sprite[i].z, 0, -16, 0, 0, 34, 34, 0, 0, 456, sprite[i].ang,
					sintable[(sprite[i].ang + 2560) & 2047] >> 6, sintable[(sprite[i].ang + 2048) & 2047] >> 6, 30,
					i + 4096, sprite[i].sectnum, 5, 32, 0, -1);
		}
		playsound(S_ANDROID_DIE, sprite[i].x, sprite[i].y, 0, ST_NOUPDATE);
	}

	public static void tweakdeathdist(int i) {
		int dax, day;

		sprite[i].clipdist <<= 1;
		dax = (((sintable[(sprite[i].ang + 512) & 2047]) * sprite[i].xvel) << 3);
		day = (((sintable[sprite[i].ang & 2047]) * sprite[i].yvel) << 3);
		movesprite((short) i, dax, day, 0, 1024, 1024, CLIFFCLIP);

		if (sprite[i].extra == -1) {
			sprite[i].lotag = SPR_LOTAG_PICKUP;
		}
	}

	public static int RMOD16(String s) {
		return krand_intercept(s) & 15;
	}

	public static int RMOD10(String s) {
		return krand_intercept(s) & 9;
	}

	public static int RMOD2(String s) {
		return ((krand_intercept(s)) > 0x00008000) ? 1 : 0;
	}

	public static int RMOD3(String s) {
		return ((krand_intercept(s)) > 0x00008000) ? 1 : 0 + (((krand_intercept(s)) >> 1) & 1);
	}

	public static int RMOD4(String s) {
		return (((krand_intercept(s)) >> 3) & 0x0000000F) & 3;
	}

	public static void enemynewsector(short i) {
		int ext = sprite[i].extra;

		if (game.nNetMode == NetMode.Multiplayer) {
			return;
		}

		if (ensfirsttime == 1) {
			ensfirsttime = 0;
			return;
		}

		switch (sector[sprite[i].sectnum].lotag) {
		case 4:
			playsound(S_SPLASH, sprite[i].x, sprite[i].y, 0, ST_UPDATE);
			break;
		default:
			break;
		}
		switch (sector[sprite[i].sectnum].hitag) {
		case 1010:
		case 1011:
		case 1012:
		case 1013:
		case 1014:
		case 1015:
		case 1016:
		case 1017:
		case 1018:
		case 1019:
			if (isahologram(i) != 0) {
				jsdeletesprite(i);
				return;
			}
			if (tekexplodebody(i) != 0) {
				missionaccomplished(i);
			}
			playsound(S_BUSHIT, sprite[i].x, sprite[i].y, 0, ST_UPDATE);
			if (validext(ext) != 0) {
				switch (spriteXT[ext].basepic) {
				case ERWALKPIC:
					playsound(S_MANDIE1 + RMOD4("STAT2615") + RMOD3("STAT2615"), sprite[i].x, sprite[i].y, 0,
							ST_UPDATE);
					break;
				case SARAHWALKPIC:
					playsound(S_GIRLDIE1 + RMOD4("STAT2618") + RMOD3("STAT2618"), sprite[i].x, sprite[i].y, 0,
							ST_UPDATE);
					break;
				case SAMWALKPIC:
					playsound(S_MANDIE1 + RMOD4("STAT2621") + RMOD3("STAT2621"), sprite[i].x, sprite[i].y, 0,
							ST_UPDATE);
					break;
				}
			}
			jsdeletesprite(i);
			break;
		default:
			operatesector(sprite[i].sectnum); // JEFF TEST
			break;
		}
	}

	public static void attackifclose(short i, int snum, int dist) {
		int mindist;
		short ext;

		if (!validplayer(snum)) {
			game.ThrowError("atakifclse: bad plrnum");
		}
		if (gPlayer[snum].health <= 0) {
			return;
		}
		ext = sprite[i].extra;
		if (validext(ext) == 0) {
			return;
		}

		switch (sprite[i].statnum) {
		case CHASE:
			mindist = CHASEATTDIST;
			break;
		case PATROL:
		case GUARD:
			mindist = GUARDATTDIST;
			break;
		case STALK:
			mindist = STALKATTDIST;
			break;
		default:
			mindist = MINATTACKDIST;
			break;
		}

		switch (sprite[i].picnum) {
		case RATPIC:
			mindist = 1024;
			break;
		case BATPIC:
			if (RMOD4("STAT1968") != 0) {
				return;
			}
			mindist = 1024;
			if (dist <= 1024) {
				game.pInt.setsprinterpolate(i, sprite[i]);
				sprite[i].z = gPlayer[snum].posz - 4096;
			}

			break;
		default:
			break;
		}

		if ((dist < mindist)) {
			switch (sprite[i].statnum) {
			default:
				sprite[i].ang = engine.getangle(gPlayer[snum].posx - sprite[i].x,
						gPlayer[snum].posy - sprite[i].y);
				newstatus(i, ATTACK);
				break;
			}
		}

		return;
	}

	public static void hideplea(int sn, int ext) {
		if ((spriteXT[ext].aimask & AI_DIDHIDEPLEA) != 0 && (krand_intercept("STAT1383") & 1) != 0)
			return;

		switch (spriteXT[ext].basepic) {
		case ERWALKPIC:
			playsound(S_MALE_DONTHURT + (krand_intercept("STAT1390") & 5), sprite[sn].x, sprite[sn].y, 0, ST_UNIQUE);
			if ((krand_intercept("STAT1391") & 1) != 0) {
				spriteXT[ext].aimask |= AI_DIDHIDEPLEA;
			}
			break;
		case SARAHWALKPIC:
		case SAMWALKPIC:
			playsound(S_DIANE_DONTSHOOTP + (krand_intercept("STAT1397") & 5), sprite[sn].x, sprite[sn].y, 0, ST_UNIQUE);
			if ((krand_intercept("STAT1398") & 1) != 0) {
				spriteXT[ext].aimask |= AI_DIDHIDEPLEA;
			}
			break;
		}
	}

	public static void fleescream(int sn, int ext) {

		if (validext(ext) == 0) {
			return;
		}

		if ((spriteXT[ext].aimask & AI_DIDFLEESCREAM) != 0 && (krand_intercept("STAT1417") & 1) != 0)
			return;

		switch (spriteXT[ext].basepic) {
		case ERWALKPIC:
			playsound(S_MALE_OHMYGOD + (krand_intercept("STAT1424") & 5), sprite[sn].x, sprite[sn].y, 0, ST_UNIQUE);
			if ((krand_intercept("STAT1425") & 1) != 0) {
				spriteXT[ext].aimask |= AI_DIDFLEESCREAM;
			}
			break;
		case SARAHWALKPIC:
		case SAMWALKPIC:
			switch (RMOD4("STAT1478")) {
			case 0:
			case 2:
				playsound(S_FEM_RUNHEGOTGUN + (krand_intercept("STAT1434") & 11), sprite[sn].x, sprite[sn].y, 0,
						ST_UNIQUE);
				break;
			default:
				playsound(S_SCREAM1 + RMOD3("STAT1484"), sprite[sn].x, sprite[sn].y, 0, ST_UNIQUE);
				break;
			}
			if ((krand_intercept("STAT1440") & 1) != 0) {
				spriteXT[ext].aimask |= AI_DIDFLEESCREAM;
			}
			break;
		}
	}

	public static short spritedeflect(int sn, short angin) {
		short angout;

		switch (sprite[sn].statnum) {
		case INANIMATE:
		case INACTIVE:
		case AMBUSH:
			angout = (short) ((angin + 1024) & 2047);
			switch (RMOD2("STAT697 ")) {
			case 0:
				angout = leftof[RMOD16("STAT")];
				break;
			case 1:
			default:
				angout = rightof[RMOD16("STAT")];
				break;
			}
			break;
		default:
			angout = sprite[sn].ang;
			break;
		}

		return (angout);
	}

	public static short arbitraryangle() {
		switch (RMOD4("STAT642 ")) {
		case 0:
		case 1:
			return (leftof[RMOD16("STAT645 ")]);
		default:
			return (rightof[RMOD16("STAT648 ")]);
		}
	}

	public static short walldeflect(int wn, int angin) {
		short wnorm, refract, delta, angout;

		wnorm = wallnormal(wn);

		refract = (short) ((angin + 1024) & 2047);

		delta = (short) (wnorm - refract);

		angout = (short) (wnorm + delta);

		if (angout < 0)
			angout += 2048;

		return (angout);
	}

	public static boolean isvisible(int i, int target) {
		if (!validplayer(target))
			game.ThrowError("isvisible: bad targetnum");

		if (gPlayer[target].cursectnum >= 0
				&& sintable[(sprite[i].ang + 2560) & 2047] * (gPlayer[target].posx - sprite[i].x)
						+ sintable[(sprite[i].ang + 2048) & 2047] * (gPlayer[target].posy - sprite[i].y) >= 0) {
			boolean cansee = engine.cansee(gPlayer[target].posx, gPlayer[target].posy, (gPlayer[target].posz) >> 1,
					gPlayer[target].cursectnum, sprite[i].x, sprite[i].y,
					sprite[i].z - (engine.getTile(sprite[i].picnum).getHeight() << 7), sprite[i].sectnum);
			if (cansee) {
				return true;
			}
		}
		return false;
	}

	public static void enemygunshot(int sn) {
		int j, nextj, ext;
		int dist;

		j = headspritestat[STROLL];
		while (j != -1) {
			nextj = nextspritestat[j];

			ext = sprite[j].extra;
			if (validext(ext) == 0) {
				noextthrowerror(sn, 100);
			}
			dist = abs(sprite[sn].x - sprite[j].x) + abs(sprite[sn].y - sprite[j].y);
			if (dist < HEARGUNSHOTDIST) {
				spriteXT[ext].aimask |= AI_JUSTSHOTAT;
			}

			j = nextj;
		}

		j = headspritestat[STANDING];
		while (j != -1) {
			nextj = nextspritestat[j];

			ext = sprite[j].extra;
			if (validext(ext) == 0) {
				noextthrowerror(sn, 101);
			}

			// guards not woken by enemies gun shot
			if (spriteXT[ext].basestat != GUARD) {
				dist = abs(sprite[sn].x - sprite[j].x) + abs(sprite[sn].y - sprite[j].y);
				if (dist < HEARGUNSHOTDIST) {
					spriteXT[ext].aimask |= AI_JUSTSHOTAT;
				}
			}

			j = nextj;
		}

		j = headspritestat[GUARD];
		while (j != -1) {
			nextj = nextspritestat[j];

			if (j == sn) {
				j = nextj;
				continue;
			}

			ext = sprite[j].extra;
			if (validext(ext) == 0) {
				noextthrowerror(sn, 102);
			}
			dist = abs(sprite[sn].x - sprite[j].x) + abs(sprite[sn].y - sprite[j].y);
			if (dist < HEARGUNSHOTDIST) {
				spriteXT[ext].aimask |= AI_JUSTSHOTAT;
			}

			j = nextj;
		}
	}

	public static void enemyshootgun(short sprnum, int x, int y, int z, short daang, int dahoriz, short dasectnum,
			char guntype) {
		short hitsect, hitsprite, daang2;
		int j, daz2, hitx, hity, hitz, discrim;
		int ext, target, pnum;

		ext = sprite[sprnum].extra;
		if (validext(ext) == 0) {
			game.ThrowError("enemyshootgun: bad ext");
		}
		target = spriteXT[sprnum].target;
		if (!validplayer(target)) {
			return;
		}

		// enemy gun fire sounds
		switch (guntype) {
		case 0:
			switch (spriteXT[ext].attackpic) {
			case RATATTACKPIC:
				break;
			case 3910:
				playsound(S_WH_8, x, y, 0, ST_NOUPDATE);
				break;
			default:
				playsound(S_MATRIX_ATTACK2, x, y, 0, ST_NOUPDATE);
				break;
			}
			break;
		case 1:
		case 2:
		case 3:
		case 4:
			switch (spriteXT[ext].attackpic) {
			case 3773:
			case 3800:
			case 3805:
			case 3810:
			case 3818:
				playsound(S_MATRIX_ATTACK, x, y, 0, ST_NOUPDATE);
				break;
			case 3850:
			case 3860:
			case 3890:
			case 3909:
			case 3952:
			case 4001:
				playsound(S_MATRIX_ATTACK2, x, y, 0, ST_NOUPDATE);
				break;
			case COPATTACKPIC:
				playsound(S_ENEMYGUN1, x, y, 0, ST_NOUPDATE);
				break;
			case ANTATTACKPIC:
				playsound(S_ENEMYGUN2, x, y, 0, ST_NOUPDATE);
				break;
			case MAATTACKPIC:
				playsound(S_ENEMYGUN3, x, y, 0, ST_NOUPDATE);
				break;
			case SAMATTACKPIC:
				playsound(S_ENEMYGUN4, x, y, 0, ST_NOUPDATE);
				break;
			default:
				playsound(S_ENEMYGUN1, x, y, 0, ST_NOUPDATE);
				break;
			}
			break;
		case 5:
		case 6:
		case 7:
		case 8:
			switch (spriteXT[ext].attackpic) {
			case 3773:
			case 3800:
			case 3805:
			case 3810:
			case 3818:
				playsound(S_MATRIX_ATTACK, x, y, 0, ST_NOUPDATE);
				break;
			case 3850:
			case 3860:
			case 3890:
			case 3909:
			case 3952:
			case 4001:
				playsound(S_MATRIX_ATTACK2, x, y, 0, ST_NOUPDATE);
				break;
			default:
				playsound(S_AUTOGUN, x, y, 0, ST_NOUPDATE);
			}
			break;
		default:
			break;
		}

		// gun shot code
		switch (guntype) {
		case 0:
			if ((abs(x - gPlayer[target].posx) + abs(y - gPlayer[target].posy)) < 1024) {
				if (abs(z - gPlayer[target].posz) < 12288) {
					playerpainsound(target);
					if (((spriteXT[ext].fxmask) & FX_HOLOGRAM) != 0) {
						playsound(S_MATRIX_ATTACK, x, y, 0, ST_NOUPDATE);
						enemywoundplayer(target, sprnum, 0);
					}
					if (((spriteXT[ext].fxmask) & FX_ANDROID) != 0) {
						androidexplosion(sprnum);
						enemywoundplayer(target, sprnum, 6);
						// engine.changespritestat(sprnum, VANISH);
						jsdeletesprite(sprnum);
					} else {
						enemywoundplayer(target, sprnum, 0);
					}
				}
			}
			return;
		case GUN3FLAG:
		case GUN4FLAG:
		case GUN5FLAG:
			daang2 = daang;
			daz2 = ((100 - dahoriz) << 11);
			z = gPlayer[target].posz; // instead of calculating a dot product angle
			engine.hitscan(x, y, z, dasectnum, sintable[(daang2 + 2560) & 2047], sintable[(daang2 + 2048) & 2047], daz2,
					pHitInfo, CLIPMASK1);
			hitsect = pHitInfo.hitsect;
			hitsprite = pHitInfo.hitsprite;
			hitx = pHitInfo.hitx;
			hity = pHitInfo.hity;
			hitz = pHitInfo.hitz;

			if (hitsprite > 0) {
				int hit = playerhit(hitsprite);
				pnum = playerhit;
				if (hit != 0) {
					playerpainsound(pnum);
					enemywoundplayer(pnum, sprnum, 3);
				} else {
					damagesprite(hitsprite, tekgundamage(guntype, x, y, z, hitsprite));
				}
			} else {
				j = jsinsertsprite(hitsect, (short) 3);
				if (j != -1) {
					fillsprite(j, hitx, hity, hitz + (8 << 8), 2, -4, 0, 32, 16, 16, 0, 0, EXPLOSION, daang, 0, 0, 0,
							sprnum + MAXSPRITES, hitsect, 3, 63, 0, 0);
				}
			}
			break;
		case GUN6FLAG:
			j = jsinsertsprite(sprite[sprnum].sectnum, PROJECTILE);
			if (j == -1) {
				break;
			}
			sprite[j].x = sprite[sprnum].x;
			sprite[j].y = sprite[sprnum].y;
			switch (spriteXT[ext].basestat) {
			case FLY:
				sprite[j].z = sprite[sprnum].z;
				break;
			default:
				sprite[j].z = sector[sprite[sprnum].sectnum].floorz - 8192;
				break;
			}
			sprite[j].cstat = 0x01;
			sprite[j].picnum = PULSARPIC;
			sprite[j].shade = -32;
			sprite[j].xrepeat = 32;
			sprite[j].yrepeat = 32;
			sprite[j].ang = sprite[sprnum].ang;
			sprite[j].xvel = (short) (sintable[(sprite[j].ang + 2560) & 2047] >> 4);
			sprite[j].yvel = (short) (sintable[(sprite[j].ang + 2048) & 2047] >> 4);
			discrim = engine.ksqrt((gPlayer[target].posx - sprite[j].x) * (gPlayer[target].posx - sprite[j].x)
					+ (gPlayer[target].posy - sprite[j].y) * (gPlayer[target].posy - sprite[j].y));
			if (discrim == 0) {
				discrim = 1;
			}
			sprite[j].zvel = (short) (((gPlayer[target].posz + (8 << 8) - sprite[j].z) << 9) / discrim);
			sprite[j].owner = sprnum;
			sprite[j].clipdist = 16;
			sprite[j].lotag = 0;
			sprite[j].hitag = 0;
			break;
		default:
			break;
		}

		if (guntype != 0) {
			enemygunshot(sprnum);
		}
	}
}

class sectflashtype {
	short sectnum;
	int step;
	char ovis;
}
