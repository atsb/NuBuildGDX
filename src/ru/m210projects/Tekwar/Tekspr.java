
package ru.m210projects.Tekwar;

import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Build.Gameutils.*;
import static ru.m210projects.Build.Pragmas.mulscale;
import static ru.m210projects.Tekwar.Globals.*;
import static ru.m210projects.Tekwar.Main.*;
import static ru.m210projects.Tekwar.View.*;
import static ru.m210projects.Tekwar.Names.*;
import static ru.m210projects.Tekwar.Tekchng.*;
import static ru.m210projects.Tekwar.Tekgun.*;
import static ru.m210projects.Tekwar.Tekprep.*;
import static ru.m210projects.Tekwar.Teksnd.*;
import static ru.m210projects.Tekwar.Tekstat.*;
import static ru.m210projects.Tekwar.Tektag.*;
import static ru.m210projects.Tekwar.Player.*;
import static java.lang.Math.*;

import ru.m210projects.Build.Pattern.Tools.Interpolation.ILoc;
import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Build.Types.Tile;

public class Tekspr {
	public static final short DROPSIES = 406;
	public static final int MAXDROPANGLES = 6;
	public static char dropanglecnt;
	public static short dropangles[] = { 0, 1792, 512, 768, 1536, 1024 };

	public static void fillsprite(int newspriteindex2, int x2, int y2, int z2, int cstat2, int shade2, int pal2,
			int clipdist2, int xrepeat2, int yrepeat2, int xoffset2, int yoffset2, int picnum2, int ang2, int xvel2,
			int yvel2, int zvel2, int owner2, int sectnum2, int statnum2, int lotag2, int hitag2, int extra2) {
		SPRITE spr2 = sprite[newspriteindex2];
		spr2.x = x2;
		spr2.y = y2;
		spr2.z = z2;
		spr2.cstat = (short) cstat2;
		spr2.shade = (byte) shade2;
		spr2.pal = (byte) pal2;
		spr2.clipdist = (byte) clipdist2;
		spr2.xrepeat = (short) xrepeat2;
		spr2.yrepeat = (short) yrepeat2;
		spr2.xoffset = (short) xoffset2;
		spr2.yoffset = (short) yoffset2;
		spr2.picnum = (short) picnum2;
		spr2.ang = (short) ang2;
		spr2.xvel = (short) xvel2;
		spr2.yvel = (short) yvel2;
		spr2.zvel = (short) zvel2;
		spr2.owner = (short) owner2;
		spr2.lotag = (short) lotag2;
		spr2.hitag = (short) hitag2;
		spr2.extra = -1;

		game.pInt.setsprinterpolate(newspriteindex2, spr2);
	}

	public static short movesprite(short spritenum, int dx, int dy, int dz, int ceildist, int flordist, int cliptype) {
		int daz, zoffs;
		int jumpz, deltaz;
		int px, py, pz;
		short retval, dasectnum, tempshort;
		short failedsectnum;

		SPRITE spr;

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

		spr = sprite[spritenum];

		game.pInt.setsprinterpolate(spritenum, spr);

		Tile pic = engine.getTile(spr.picnum);

		if ((spr.cstat & 128) == 0)
			zoffs = -((pic.getHeight() * spr.yrepeat) << 1);
		else
			zoffs = 0;

		dasectnum = spr.sectnum;
		px = spr.x;
		py = spr.y;
		pz = spr.z;

		daz = spr.z + zoffs;

		retval = (short) engine.clipmove(spr.x, spr.y, daz, dasectnum, dx, dy, (spr.clipdist) << 2, ceildist, flordist,
				dcliptype);

		if (clipmove_sectnum != -1) {
			spr.x = clipmove_x;
			spr.y = clipmove_y;
			daz = clipmove_z;
			dasectnum = clipmove_sectnum;
		}

		if ((dasectnum != spr.sectnum) && (dasectnum >= 0))
			engine.changespritesect(spritenum, dasectnum);

		if (dasectnum >= 0 && (sector[dasectnum].lotag == 4) && (spr.extra != -1)) {
			switch (spr.statnum) {
			case FLOATING:
				break;
			default:
				spr.z = sector[spr.sectnum].floorz - zoffs;
				break;
			}
			return (retval);
		}

		tempshort = spr.cstat;
		spr.cstat &= ~1;
		engine.getzrange(spr.x, spr.y, spr.z - 1, spr.sectnum, (spr.clipdist) << 2, dcliptype);

		spr.cstat = tempshort;
		daz = spr.z + zoffs + dz;

		if ((daz <= zr_ceilz) || (daz > zr_florz)) {
			if (retval != 0) {
				return (retval);
			} else {
				for (int j = headspritesect[dasectnum]; j >= 0; j = nextspritesect[j]) {
					int dist = (int) (engine.qdist(spr.x - sprite[j].x, spr.y - sprite[j].y) >> 2);
					if (dist > 0 && dist <= (spr.clipdist) << 2) {
						return (short) (j + 49152);
					}
				}
				return (short) (16384 + dasectnum);
			}
		}
		if ((zr_florz != pz) && (spr.extra >= 0) && (spr.extra < MAXSPRITES)) {
			spr.z = zr_florz;
			deltaz = abs(pz - zr_florz);
			jumpz = pic.getHeight() + (spr.yrepeat - 64);
			jumpz <<= 8;
			if (deltaz > jumpz) {
				failedsectnum = spr.sectnum;
				engine.setsprite(spritenum, px, py, pz);
				retval = (short) (failedsectnum + 16384);
			}
		} else {
			spr.z = daz - zoffs;
		}

		return (retval);
	}

	public static void dropit(int x, int y, int z, short sect, int pic) {
		int j, ang;

		j = jsinsertsprite(sect, DROPSIES);
		if (j != -1) {
			ang = dropangles[dropanglecnt];
			fillsprite(j, x, y, z, 0, 0, 0, 12, 16, 16, 0, 0, pic, ang, sintable[(ang + 2560) & 2047] >> 6,
					sintable[(ang + 2048) & 2047] >> 6, 30, 0, sect, DROPSIES, 0, 0, 0);
		}
		dropanglecnt++;
		if (dropanglecnt >= MAXDROPANGLES) {
			dropanglecnt = 0;
		}
	}

	public static void playerdropitems(int snum) {
		if (!validplayer(snum)) {
			game.ThrowError("dropitems on bad plrnum");
		}

		if ((gPlayer[snum].weapons & flags32[GUN2FLAG]) != 0) {
			dropit(gPlayer[snum].posx, gPlayer[snum].posy, gPlayer[snum].posz - (32 << 8), gPlayer[snum].cursectnum,
					924);
			dropit(gPlayer[snum].posx, gPlayer[snum].posy, gPlayer[snum].posz - (32 << 8), gPlayer[snum].cursectnum,
					3102);
		}
		if ((gPlayer[snum].weapons & flags32[GUN3FLAG]) != 0) {
			dropit(gPlayer[snum].posx, gPlayer[snum].posy, gPlayer[snum].posz - (32 << 8), gPlayer[snum].cursectnum,
					3104);
		}
		if ((gPlayer[snum].weapons & flags32[GUN4FLAG]) != 0) {
			dropit(gPlayer[snum].posx, gPlayer[snum].posy, gPlayer[snum].posz - (32 << 8), gPlayer[snum].cursectnum,
					3103);
			dropit(gPlayer[snum].posx, gPlayer[snum].posy, gPlayer[snum].posz - (32 << 8), gPlayer[snum].cursectnum,
					3093);
		}
		if ((gPlayer[snum].weapons & flags32[GUN5FLAG]) != 0) {
			dropit(gPlayer[snum].posx, gPlayer[snum].posy, gPlayer[snum].posz - (32 << 8), gPlayer[snum].cursectnum,
					3095);
		}
		if ((gPlayer[snum].weapons & flags32[GUN6FLAG]) != 0) {
			dropit(gPlayer[snum].posx, gPlayer[snum].posy, gPlayer[snum].posz - (32 << 8), gPlayer[snum].cursectnum,
					925);
			dropit(gPlayer[snum].posx, gPlayer[snum].posy, gPlayer[snum].posz - (32 << 8), gPlayer[snum].cursectnum,
					3101);
			dropit(gPlayer[snum].posx, gPlayer[snum].posy, gPlayer[snum].posz - (32 << 8), gPlayer[snum].cursectnum,
					3091);
		}
		if ((gPlayer[snum].weapons & flags32[GUN7FLAG]) != 0) {
			dropit(gPlayer[snum].posx, gPlayer[snum].posy, gPlayer[snum].posz - (32 << 8), gPlayer[snum].cursectnum,
					3100);
			dropit(gPlayer[snum].posx, gPlayer[snum].posy, gPlayer[snum].posz - (32 << 8), gPlayer[snum].cursectnum,
					3090);
		}
	}

	public static void checktouchsprite(int snum, int sectnum) {
		int healthmax;
		char dosound = 0;

		if ((sectnum < 0) || (sectnum >= numsectors)) {
			return;
		}

		String message = null;
		short i = headspritesect[sectnum], nexti;
		while (i != -1) {
			nexti = nextspritesect[i];
			if ((abs(gPlayer[snum].posx - sprite[i].x) + abs(gPlayer[snum].posy - sprite[i].y) < 512) && (abs(
					(gPlayer[snum].posz >> 8) - ((sprite[i].z >> 8) - (engine.getTile(sprite[i].picnum).getHeight() >> 1))) <= 40)) {
				// must jive with tekinitplayer settings
				switch (gDifficulty) {
				case 3:
					healthmax = (MAXHEALTH >> 1);
					break;
				default:
					healthmax = MAXHEALTH;
					break;
				}

				switch (sprite[i].picnum) {
				case MEDICKIT2PIC:
					if (gPlayer[snum].health < healthmax) {
						changehealth(snum, 100);
						jsdeletesprite(i);
						message = "MEDIC KIT";
						dosound++;
					}
					break;
				case MEDICKITPIC:
					if (gPlayer[snum].health < healthmax) {
						message = "MEDIC KIT";
						changehealth(snum, 250);
						jsdeletesprite(i);
						dosound++;
					}
					break;
				case 3637:
					if (gPlayer[snum].health < healthmax) {
						message = "ENERGY PELLET";
						changehealth(snum, 50);
						jsdeletesprite(i);
						dosound++;
					}
					break;
				case DONUTSPIC:
					if (gPlayer[snum].health < healthmax) {
						message = "MMMMMMM DONUTS";
						changehealth(snum, 50);
						jsdeletesprite(i);
						dosound++;
					}
					break;
				case ACCUTRAKPIC:
					gPlayer[snum].invaccutrak = 1;
					message = "ACCU TRAK";
					startwhiteflash(3);
					jsdeletesprite(i);
					dosound++;
					break;
				// gun pick ups
				case 3092:
					if (((gPlayer[snum].weapons) & flags32[GUN2FLAG]) == 0) {
						gPlayer[snum].weapons |= flags32[GUN2FLAG];

						gPlayer[snum].ammo[1] = BClipHigh((short) (gPlayer[snum].ammo[1] + 25), MAXAMMO);
						message = "PISTOL";
						jsdeletesprite(i);
						startwhiteflash(3);
						dosound++;
					}
					break;
				case 3094:
					if (((gPlayer[snum].weapons) & flags32[GUN3FLAG]) == 0) {
						gPlayer[snum].weapons |= flags32[GUN3FLAG];
						gPlayer[snum].ammo[2] = BClipHigh((short) (gPlayer[snum].ammo[2] + 15), MAXAMMO);
						message = "SHRIKE DBK";
						jsdeletesprite(i);
						startwhiteflash(3);
						dosound++;
					}
					break;
				case 3093:
					if (((gPlayer[snum].weapons) & flags32[GUN4FLAG]) == 0) {
						gPlayer[snum].weapons |= flags32[GUN4FLAG];
						gPlayer[snum].ammo[3] = BClipHigh((short) (gPlayer[snum].ammo[3] + 15), MAXAMMO);
						message = "ORLOW";
						jsdeletesprite(i);
						startwhiteflash(3);
						dosound++;
					}
					break;
				case GUN3DEMO:
					if (TEKDEMO && ((gPlayer[snum].weapons) & flags32[GUN3FLAG]) == 0) {
						gPlayer[snum].weapons |= flags32[GUN3FLAG];
						gPlayer[snum].ammo[2] = BClipHigh((short) (gPlayer[snum].ammo[2] + MAXAMMO / 2), MAXAMMO);
						message = "RAW DALOW 34 - FIND ACCUTRAK";
						jsdeletesprite(i);
						startwhiteflash(3);
						dosound++;
					}
					break;
				case 3095:
					if (((gPlayer[snum].weapons) & flags32[GUN5FLAG]) == 0) {
						gPlayer[snum].weapons |= flags32[GUN5FLAG];
						gPlayer[snum].ammo[4] = BClipHigh((short) (gPlayer[snum].ammo[4] + 15), MAXAMMO);
						message = "EMP GUN";
						jsdeletesprite(i);
						startwhiteflash(3);
						dosound++;
					}
					break;
				case 3091:
					if (((gPlayer[snum].weapons) & flags32[GUN6FLAG]) == 0) {
						gPlayer[snum].weapons |= flags32[GUN6FLAG];
						gPlayer[snum].ammo[5] = BClipHigh((short) (gPlayer[snum].ammo[5] + 15), MAXAMMO);
						message = "FLAMER";
						jsdeletesprite(i);
						startwhiteflash(3);
						dosound++;
					}
					break;
				case 3090:
					if (((gPlayer[snum].weapons) & flags32[GUN7FLAG]) == 0) {
						gPlayer[snum].weapons |= flags32[GUN7FLAG];
						gPlayer[snum].ammo[6] = BClipHigh((short) (gPlayer[snum].ammo[6] + 10), MAXAMMO);
						message = "BAZOOKA";
						jsdeletesprite(i);
						startwhiteflash(3);
						dosound++;
					}
					break;
				// ammo pick ups
				case 924:
				case 3102:
					if ((gPlayer[snum].ammo[1] < MAXAMMO)) { // && ((gPlayer[snum].weapons&flags32[GUN2FLAG]) != 0) ) {
						gPlayer[snum].ammo[1] = BClipHigh((short) (gPlayer[snum].ammo[1] + 20), MAXAMMO);
						message = "PISTOL KLIP";
						jsdeletesprite(i);
						startwhiteflash(3);
						dosound++;
					}
					break;
				case 3104:
					if ((gPlayer[snum].ammo[2] < MAXAMMO)) { // && ((gPlayer[snum].weapons&flags32[GUN3FLAG]) != 0) ) {
						gPlayer[snum].ammo[2] = BClipHigh((short) (gPlayer[snum].ammo[2] + 10), MAXAMMO);
						message = "SHRIKE CHARGES";
						jsdeletesprite(i);
						startwhiteflash(3);
						dosound++;
					}
					break;
				case 3103:
					if ((gPlayer[snum].ammo[3] < MAXAMMO)) { // && ((gPlayer[snum].weapons&flags32[GUN4FLAG]) != 0) ) {
						gPlayer[snum].ammo[3] = BClipHigh((short) (gPlayer[snum].ammo[3] + 10), MAXAMMO);
						message = "ORLOW CHARGES";
						jsdeletesprite(i);
						startwhiteflash(3);
						dosound++;
					}
					break;
				case 925:
				case 3101:
					if ((gPlayer[snum].ammo[5] < MAXAMMO)) { // && ((gPlayer[snum].weapons&flags32[GUN6FLAG]) != 0) ) {
						gPlayer[snum].ammo[5] = BClipHigh((short) (gPlayer[snum].ammo[5] + 10), MAXAMMO);
						message = "FUEL BOTTLE";
						jsdeletesprite(i);
						startwhiteflash(3);
						dosound++;
					}
					break;
				case CHARGEPAKPIC:
				case 3100:
					if ((gPlayer[snum].ammo[6] < MAXAMMO)) { // && ((gPlayer[snum].weapons&flags32[GUN7FLAG]) != 0) ) {
						gPlayer[snum].ammo[6] = BClipHigh((short) (gPlayer[snum].ammo[6] + 5), MAXAMMO);
						message = "BAZOOKA FUEL";
						jsdeletesprite(i);
						startwhiteflash(3);
						dosound++;
					}
					break;
				case 3836:
					if ((gPlayer[snum].ammo[7] < MAXAMMO)) {
						gPlayer[snum].ammo[7] = BClipHigh((short) (gPlayer[snum].ammo[7] + 25), MAXAMMO);
						message = "GLOVE CHARGE";
						jsdeletesprite(i);
						startwhiteflash(3);
						dosound++;
					}
					break;
				case RED_KEYCARD:
					if (gPlayer[snum].invredcards == 0) {
						message = "RED KEY CARD";
						gPlayer[snum].invredcards = 1;
						jsdeletesprite(i);
						startwhiteflash(3);
						dosound++;
					}
					break;
				case BLUE_KEYCARD:
					if (gPlayer[snum].invbluecards == 0) {
						message = "BLUE KEY CARD";
						gPlayer[snum].invbluecards = 1;
						jsdeletesprite(i);
						startwhiteflash(3);
						dosound++;
					}
					break;
				default:
					break;
				}
			}
			if (dosound != 0 && (screenpeek == snum)) {
				playsound(S_PICKUP_BONUS, 0, 0, 0, ST_UNIQUE);
				showmessage(message);
			}
			dosound = 0;

			i = nexti;
		}
	}

	public static short floatmovesprite(short spritenum, int dx, int dy, int dz, int ceildist, int flordist,
			int cliptype) {
		int daz, zoffs;
		short retval, dasectnum;
		SPRITE spr;

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

		spr = sprite[spritenum];
		game.pInt.setsprinterpolate(spritenum, spr);
		if ((spr.cstat & 128) == 0)
			zoffs = -((engine.getTile(spr.picnum).getHeight() * spr.yrepeat) << 1);
		else
			zoffs = 0;

		dasectnum = spr.sectnum; // Can't modify sprite sectors directly becuase of linked lists
		daz = spr.z + zoffs; // Must do this if not using the new centered centering (of course)
		retval = (short) engine.clipmove(spr.x, spr.y, daz, dasectnum, dx, dy, (spr.clipdist) << 2, ceildist, flordist,
				dcliptype);
		spr.x = clipmove_x;
		spr.y = clipmove_y;
		dasectnum = clipmove_sectnum;
		if ((dasectnum != spr.sectnum) && (dasectnum >= 0))
			engine.changespritesect(spritenum, dasectnum);

		return (retval);
	}

	public static short flymovesprite(short spritenum, int dx, int dy, int dz, int ceildist, int flordist,
			int cliptype) {
		long daz;
		short retval, dasectnum, tempshort;
		SPRITE spr;

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

		spr = sprite[spritenum];
		game.pInt.setsprinterpolate(spritenum, spr);
		dasectnum = spr.sectnum;
		retval = (short) engine.clipmove(spr.x, spr.y, spr.z, dasectnum, dx, dy, (spr.clipdist) << 2, ceildist,
				flordist, dcliptype);
		spr.x = clipmove_x;
		spr.y = clipmove_y;
		spr.z = clipmove_z;
		dasectnum = clipmove_sectnum;

		if ((dasectnum != spr.sectnum) && (dasectnum >= 0))
			engine.changespritesect(spritenum, dasectnum);

		if (spr.statnum != PINBALL) {
			tempshort = spr.cstat;
			spr.cstat &= ~1;
			engine.getzrange(spr.x, spr.y, spr.z - 1, spr.sectnum, (spr.clipdist) << 2, dcliptype);

			spr.cstat = tempshort;
			daz = (zr_florz + zr_ceilz);
			spr.z = (int) (daz >> 1);
		}

		return (retval);
	}

	public static short kenmovesprite(short spritenum, int dx, int dy, int dz, int ceildist, int flordist,
			int cliptype) {
		long daz, zoffs;
		short retval, dasectnum, tempshort;
		SPRITE spr;

		spr = sprite[spritenum];
		game.pInt.setsprinterpolate(spritenum, spr);
		if ((spr.cstat & 128) == 0)
			zoffs = -((engine.getTile(spr.picnum).getHeight() * spr.yrepeat) << 1);
		else
			zoffs = 0;

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

		dasectnum = spr.sectnum; // Can't modify sprite sectors directly becuase of linked lists
		daz = spr.z + zoffs; // Must do this if not using the new centered centering (of course)
		retval = (short) engine.clipmove(spr.x, spr.y, (int) daz, dasectnum, dx, dy, (spr.clipdist) << 2, ceildist,
				flordist, dcliptype);
		spr.x = clipmove_x;
		spr.y = clipmove_y;
		daz = clipmove_z;
		dasectnum = clipmove_sectnum;
		if ((dasectnum != spr.sectnum) && (dasectnum >= 0))
			engine.changespritesect(spritenum, dasectnum);

		// Set the blocking bit to 0 temporarly so engine.getzrange doesn't pick up
		// its own sprite
		tempshort = spr.cstat;
		spr.cstat &= ~1;
		engine.getzrange(spr.x, spr.y, spr.z - 1, spr.sectnum, (spr.clipdist) << 2, dcliptype);

		spr.cstat = tempshort;

		daz = spr.z + zoffs + dz;
		if ((daz <= zr_ceilz) || (daz > zr_florz)) {
			if (retval != 0)
				return (retval);
			return (short) (16384 + dasectnum);
		}
		spr.z = (int) (daz - zoffs);
		return (retval);
	}

	public static void analyzesprites(int dax, int day, int smoothratio) {
		int i, k;
		int ext;
		SPRITE tspr;

		for (i = 0; i < spritesortcnt; i++) {
			tspr = tsprite[i];

			ext = tspr.extra;
			if (validext(ext) != 0) {
				if (rearviewdraw == 0) {
					spriteXT[ext].aimask |= AI_WASDRAWN;
				}
			}

			ILoc oldLoc = game.pInt.getsprinterpolate(tspr.owner);
			// only interpolate certain moving things
			if (oldLoc != null && (tspr.hitag & 0x0200) == 0) {
				int x = oldLoc.x;
				int y = oldLoc.y;
				int z = oldLoc.z;
				short nAngle = oldLoc.ang;

				// interpolate sprite position
				x += mulscale(tspr.x - oldLoc.x, smoothratio, 16);
				y += mulscale(tspr.y - oldLoc.y, smoothratio, 16);
				z += mulscale(tspr.z - oldLoc.z, smoothratio, 16);
				nAngle += mulscale(((tspr.ang - oldLoc.ang + 1024) & 2047) - 1024, smoothratio, 16);

				tspr.x = x;
				tspr.y = y;
				tspr.z = z;
				tspr.ang = nAngle;
			}

			k = engine.getangle(tspr.x - dax, tspr.y - day);
			k = (((tspr.ang + 3072 + 128 - k) & 2047) >> 8) & 7;
			tspr.shade = sector[tspr.sectnum].floorshade;

			switch (tspr.picnum) {
			case DOOMGUY:
			case RUBWALKPIC:
			case FRGWALKPIC:
			case SAMWALKPIC:
			case COP1WALKPIC:
			case ANTWALKPIC:
			case SARAHWALKPIC:
			case MAWALKPIC:
			case ERWALKPIC:
			case DIWALKPIC:
			case RATPIC:
			case SUNGWALKPIC:
			case COWWALKPIC:
			case COPBWALKPIC:
			case NIKAWALKPIC:
			case REBRWALKPIC:
			case TRENWALKPIC:
			case WINGWALKPIC:
			case HALTWALKPIC:
			case REDHWALKPIC:
			case ORANWALKPIC:
			case BLKSWALKPIC:
			case SFROWALKPIC:
			case SSALWALKPIC:
			case SGOLWALKPIC:
			case SWATWALKPIC:
				if (k <= 4) {
					tspr.picnum += (k << 2);
					tspr.cstat &= ~4; // clear x-flipping bit
				} else {
					tspr.picnum += ((8 - k) << 2);
					tspr.cstat |= 4; // set x-flipping bit
				}
				break;
			case AUTOGUN:
				if (k <= 4) {
					tspr.picnum += k;
					tspr.cstat &= ~4;
				} else {
					tspr.picnum += (8 - k);
					tspr.cstat |= 4;
				}
				break;
			case JAKESTANDPIC:
			case RUBSTANDPIC:
			case FRGSTANDPIC:
			case SAMSTANDPIC:
			case COP1STNDPIC:
			case ANTSTANDPIC:
			case MASTANDPIC:
			case DISTANDPIC:
			case ERSTANDPIC:
			case SUNGSTANDPIC:
			case COWSTANDPIC:
			case COPBSTANDPIC:
			case NIKASTANDPIC:
			case REBRSTANDPIC:
			case TRENSTANDPIC:
			case WINGSTANDPIC:
			case HALTSTANDPIC:
			case REDHSTANDPIC:
			case ORANSTANDPIC:
			case BLKSSTANDPIC:
			case SFROSTANDPIC:
			case SSALSTANDPIC:
			case SGOLSTANDPIC:
			case SWATSTANDPIC:
			case PROBE1:
			case RS232:
				if (k <= 4) {
					tspr.picnum += k;
					tspr.cstat &= ~4;
				} else {
					tspr.picnum += ((8 - k));
					tspr.cstat |= 4;
				}
				break;
			case RUBATAKPIC:
			case FRGATTACKPIC:
			case SAMATTACKPIC:
			case COPATTACKPIC:
			case ANTATTACKPIC:
			case MAATTACKPIC:
			case DIATTACKPIC:
			case SUNGATTACKPIC:
			case COWATTACKPIC:
			case COPBATTACKPIC:
			case NIKAATTACKPIC:
			case REBRATTACKPIC:
			case WINGATTACKPIC:
			case HALTATTACKPIC:
			case REDHATTACKPIC:
			case ORANATTACKPIC:
			case BLKSATTACKPIC:
			case SFROATTACKPIC:
			case SSALATTACKPIC:
			case SGOLATTACKPIC:
			case SWATATTACKPIC:
				if (k <= 4) {
					tspr.picnum += (k << 1);
					tspr.cstat &= ~4; // clear x-flipping bit
				} else {
					tspr.picnum += ((8 - k) << 1);
					tspr.cstat |= 4;
				}
				break;
			case JAKEPAINPIC:
			case RUBPAINPIC:
			case FRGPAINPIC:
			case SAMPAINPIC:
			case COP1PAINPIC:
			case ANTPAINPIC:
			case MAPAINPIC:
			case ERPAINPIC:
			case SUNGPAINPIC:
			case COWPAINPIC:
			case NIKAPAINPIC:
			case REBRPAINPIC:
			case TRENPAINPIC:
			case HALTPAINPIC:
			case ORANPAINPIC:
			case BLKSPAINPIC:
			case SFROPAINPIC:
			case SGOLPAINPIC:
			case SWATPAINPIC:
			case JAKEDEATHPIC:
			case JAKEDEATHPIC + 1:
			case JAKEDEATHPIC + 2:
			case JAKEDEATHPIC + 3:
			case JAKEDEATHPIC + 4:
			case JAKEDEATHPIC + 5:
			case JAKEDEATHPIC + 6:
			case JAKEDEATHPIC + 7:
			case JAKEDEATHPIC + 8:
				if (k <= 4) {
					tspr.picnum += (k);
					tspr.cstat &= ~4; // clear x-flipping bit
				} else {
					tspr.picnum += ((8 - k));
					tspr.cstat |= 4;
				}
				break;
			case 3708:
			case 3709:
				tspr.shade = -128;
				break;
			// mirrorman
			case 1079:
			case 1074:
				if (k <= 4) {
					tspr.picnum += (k);
					tspr.cstat |= 4;
				} else {
					tspr.picnum += ((8 - k));
					tspr.cstat &= ~4; // clear x-flipping bit
				}
				break;
			case JAKEATTACKPIC:
			case JAKEATTACKPIC + 1:
				if (k <= 4) {
					tspr.picnum += (k * 3);
					tspr.cstat &= ~4;
				} else {
					tspr.picnum += ((8 - k) * 3);
					tspr.cstat |= 4;
				}
				break;
			default:
				break;
			}
		}
	}

}
