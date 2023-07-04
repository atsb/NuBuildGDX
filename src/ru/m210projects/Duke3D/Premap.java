//Copyright (C) 1996, 2003 - 3D Realms Entertainment
//
//This file is part of Duke Nukem 3D version 1.5 - Atomic Edition
//
//Duke Nukem 3D is free software; you can redistribute it and/or
//modify it under the terms of the GNU General Public License
//as published by the Free Software Foundation; either version 2
//of the License, or (at your option) any later version.
//
//This program is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
//
//See the GNU General Public License for more details.
//
//You should have received a copy of the GNU General Public License
//along with this program; if not, write to the Free Software
//Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
//
//Original Source: 1996 - Todd Replogle
//Prepared for public release: 03/21/2003 - Charlie Wiederhold, 3D Realms
//This file has been modified by Jonathon Fowler (jf@jonof.id.au)
//and Alexander Makarov-[M210] (m210-2007@mail.ru)

package ru.m210projects.Duke3D;

import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Build.Net.Mmulti.*;
import static ru.m210projects.Duke3D.Main.*;
import static ru.m210projects.Duke3D.Animate.*;
import static ru.m210projects.Duke3D.ResourceHandler.*;
import static ru.m210projects.Duke3D.Names.*;
import static ru.m210projects.Duke3D.Sounds.*;
import static ru.m210projects.Duke3D.View.*;
import static ru.m210projects.Duke3D.Spawn.*;
import static ru.m210projects.Duke3D.Player.*;
import static ru.m210projects.Duke3D.Globals.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;

import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.FileHandle.FileEntry;
import ru.m210projects.Build.FileHandle.Resource;
import ru.m210projects.Build.Render.Renderer.RenderType;
import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Build.Types.Tile;
import ru.m210projects.Build.Types.WALL;
import ru.m210projects.Duke3D.Types.PlayerStruct;

public class Premap {

	public static byte[] packbuf = new byte[576];
	public static byte[] oldgotpics = new byte[(MAXTILES + 7) >> 3];
	public static short[] rorsector = new short[16];
	public static byte[] rortype = new byte[16];
	public static int rorcnt;

	public static short which_palookup = 9;

	public static boolean getsound(int num) {
		if (num >= NUM_SOUNDS || cfg.noSound)
			return false;

		if (currentGame.getCON().sounds[num] == null)
			return false;

		String filename = currentGame.getCON().sounds[num];

		// Twentieth Anniversary World Tour
		if (cfg.bLegacyDukeTalk && pDukeLegacyDir != null && (currentGame.getCON().soundm[num] & 4) != 0) {
			FileEntry fil = BuildGdx.compat.checkFile(filename);
			if (fil != null)
				filename = pDukeLegacyDir.getRelativePath() + File.separator + fil.getName();
		}
		Resource fp = BuildGdx.cache.open(filename, loadfromgrouponly);
		if (fp == null)
			return false;

		soundsiz[num] = fp.size();

		if (currentGame.getCON().type != 20 || ((ud.level_number == 0 && ud.volume_number == 0
				&& (num == 189 || num == 232 || num == 99 || num == 233 || num == 17)) || (fp.size() < 12288))) {
			Sound[num].lock = 2;
			loadSample(fp.getBytes(), num);
		}
		fp.close();
		return true;
	}

	public static void xyzmirror(int i, int wn) {
		System.arraycopy(gotpic, 0, oldgotpics, 0, gotpic.length);

		Tile pic = engine.getTile(wn);
		engine.setviewtotile(wn, pic.getHeight(), pic.getWidth());

		engine.drawrooms(sprite[i].x, sprite[i].y, sprite[i].z, sprite[i].ang, 100 + sprite[i].shade,
				sprite[i].sectnum);
		display_mirror = 1;
		animatesprites(sprite[i].x, sprite[i].y, sprite[i].z, sprite[i].ang, 65536);
		display_mirror = 0;
		engine.drawmasks();

		engine.setviewback();

		if (engine.getrender().getType() == RenderType.Software)
			engine.squarerotatetile(wn);

		System.arraycopy(oldgotpics, 0, gotpic, 0, gotpic.length);
	}

	public static void pickrandomspot(int snum) {
		int i;

		PlayerStruct p = ps[snum];

		if (ud.multimode > 1 && ud.coop != 1)
			i = engine.krand() % numplayersprites;
		else
			i = snum;

		p.bobposx = p.oposx = p.posx = po[i].ox;
		p.bobposy = p.oposy = p.posy = po[i].oy;
		p.oposz = p.posz = po[i].oz;
		p.ang = po[i].oa;
		p.cursectnum = po[i].os;
	}

	public static void resetplayerstats(int snum) {
		PlayerStruct p = ps[snum];

		ud.showallmap = 0;
		p.dead_flag = 0;
		p.wackedbyactor = -1;
		p.falling_counter = 0;
		p.quick_kick = 0;
		p.subweapon = 0;
		p.last_full_weapon = 0;
		p.tipincs = 0;
		p.buttonpalette = 0;
		p.actorsqu = -1;
		p.invdisptime = 0;
		p.refresh_inventory = false;
		p.last_pissed_time = 0;
		p.holster_weapon = 0;
		p.pycount = 0;
		p.pyoff = 0;
		p.opyoff = 0;
		p.loogcnt = 0;
		p.angvel = 0;
		p.weapon_sway = 0;
		p.extra_extra8 = 0;
		p.show_empty_weapon = 0;
		p.dummyplayersprite = -1;
		p.crack_time = 0;
		p.hbomb_hold_delay = 0;
		p.transporter_hold = 0;
		p.wantweaponfire = -1;
		p.hurt_delay = 0;
		p.footprintcount = 0;
		p.footprintpal = 0;
		p.footprintshade = 0;
		p.jumping_toggle = 0;
		p.ohoriz = p.horiz = 140;
		p.horizoff = 0;
		p.bobcounter = 0;
		p.on_ground = false;
		p.player_par = 0;
		p.return_to_center = 9;
		p.airleft = 15 * 26;
		p.rapid_fire_hold = 0;
		p.toggle_key_flag = 0;
		p.access_spritenum = -1;
		if (ud.multimode > 1 && ud.coop != 1)
			p.got_access = 7;
		else
			p.got_access = 0;
		p.random_club_frame = 0;
		p.on_warping_sector = 0;
		p.spritebridge = 0;
		p.palette = palette;

		if (p.steroids_amount < 400) {
			p.steroids_amount = 0;
			p.inven_icon = 0;
		}
		p.heat_on = 0;
		p.jetpack_on = 0;
		p.holoduke_on = -1;

		p.look_ang = (short) (512 - ((ud.level_number & 1) << 10));

		p.rotscrnang = 0;
		p.newowner = -1;
		p.jumping_counter = 0;
		p.hard_landing = 0;
		p.posxv = 0;
		p.posyv = 0;
		p.poszv = 0;
		fricxv = 0;
		fricyv = 0;
		p.somethingonplayer = -1;
		p.one_eighty_count = 0;
		p.cheat_phase = 0;

		p.on_crane = -1;

		if (p.curr_weapon == PISTOL_WEAPON)
			p.kickback_pic = 5;
		else
			p.kickback_pic = 0;

		p.weapon_pos = 6;
		p.walking_snd_toggle = 0;
		p.weapon_ang = 0;

		p.knuckle_incs = 1;
		p.fist_incs = 0;
		p.knee_incs = 0;
		p.jetpack_on = 0;
		setpal(p);

		p.numloogs = 0; // GDX 31.10.2018
		p.exitx = 0;
		p.exity = 0;
		p.truefz = 0;
		p.truecz = 0;
		p.randomflamex = 0;
		p.access_incs = 0;
		p.access_wallnum = 0;
		p.interface_toggle_flag = 0;
		p.scream_voice = null;
		p.crouch_toggle = 0;
		p.last_used_weapon = 0;
	}

	public static void resetweapons(int snum) {
		int weapon;
		PlayerStruct p = ps[snum];

		for (weapon = PISTOL_WEAPON; weapon < MAX_WEAPONS; weapon++)
			p.gotweapon[weapon] = false;
		for (weapon = PISTOL_WEAPON; weapon < MAX_WEAPONS; weapon++)
			p.ammo_amount[weapon] = 0;
		Arrays.fill(p.weaprecs, (short) 0);

		p.weapon_pos = 6;
		p.kickback_pic = 5;
		p.curr_weapon = PISTOL_WEAPON;
		p.gotweapon[PISTOL_WEAPON] = true;
		p.gotweapon[KNEE_WEAPON] = true;
		p.ammo_amount[PISTOL_WEAPON] = 48;
		p.gotweapon[HANDREMOTE_WEAPON] = true;
		p.last_weapon = -1;

		p.show_empty_weapon = 0;
		p.last_pissed_time = 0;
		p.holster_weapon = 0;
	}

	public static void resetinventory(int snum) {
		PlayerStruct p = ps[snum];

		p.inven_icon = 0;
		p.boot_amount = 0;
		p.scuba_on = 0;
		p.scuba_amount = 0;
		p.heat_amount = 0;
		p.heat_on = 0;
		p.jetpack_on = 0;
		p.jetpack_amount = 0;
		p.shield_amount = (short) currentGame.getCON().max_armour_amount;
		p.holoduke_on = -1;
		p.holoduke_amount = 0;
		p.firstaid_amount = 0;
		p.steroids_amount = 0;
		p.inven_icon = 0;
	}

	public static void resetprestat(int snum) {
		PlayerStruct p = ps[snum];

		spriteqloc = 0;
		for (int i = 0; i < currentGame.getCON().spriteqamount; i++)
			spriteq[i] = -1;

		p.hbomb_on = 0;
		p.cheat_phase = 0;
		p.pals_time = 0;
		p.toggle_key_flag = 0;
		p.secret_rooms = 0;
		p.max_secret_rooms = 0;
		p.actors_killed = 0;
		p.max_actors_killed = 0;
		p.lastrandomspot = 0;
		p.weapon_pos = 6;
		p.kickback_pic = 5;
		p.last_weapon = -1;
		p.weapreccnt = 0;
		p.show_empty_weapon = 0;
		p.holster_weapon = 0;
		p.last_pissed_time = 0;

		p.one_parallax_sectnum = -1;

		screenpeek = myconnectindex;
		numanimwalls = 0;
		numcyclers = 0;
		gAnimationCount = 0;
		parallaxtype = 0;
		engine.srand(17);
		game.gPaused = false;
		ud.camerasprite = -1;
		ud.eog = 0;
		tempwallptr = 0;
		camsprite = -1;
		earthquaketime = 0;

		startofdynamicinterpolations = 0;

		if (((uGameFlags & MODE_EOL) != MODE_EOL && numplayers < 2) || (ud.coop != 1 && numplayers > 1)) {
			resetweapons(snum);
			resetinventory(snum);
		} else if (p.curr_weapon == HANDREMOTE_WEAPON) {
			p.ammo_amount[HANDBOMB_WEAPON]++;
			p.curr_weapon = HANDBOMB_WEAPON;
		}

		p.timebeforeexit = 0;
		p.customexitsound = 0;

		Arrays.fill(p.pals, (short) 0);
	}

	public static void setupbackdrop(short sky) {
		Arrays.fill(pskyoff, (short) 0);

		if (parallaxyscale != 65536)
			parallaxyscale = 32768;
		parallaxyoffs = 0;

		switch (sky) {
		case CLOUDYOCEAN:
			parallaxyscale = 65536;
			break;
		case MOONSKY1:
			pskyoff[6] = 1;
			pskyoff[1] = 2;
			pskyoff[4] = 2;
			pskyoff[2] = 3;
			break;
		case BIGORBIT1: // orbit
			pskyoff[5] = 1;
			pskyoff[6] = 2;
			pskyoff[7] = 3;
			pskyoff[2] = 4;
			break;
		case LA:
			parallaxyscale = 16384 + 1024;
			pskyoff[0] = 1;
			pskyoff[1] = 2;
			pskyoff[2] = 1;
			pskyoff[3] = 3;
			pskyoff[4] = 4;
			pskyoff[5] = 0;
			pskyoff[6] = 2;
			pskyoff[7] = 3;
			break;

		case 5284: // Twentieth Anniversary World Tour skies
		case 5412:
		case 5420:
		case 5450:
		case 5540:
		case 5548:
		case 5556:
		case 5720:
		case 5814:
			if (currentGame.getCON().type == 20) {
				parallaxyscale = 65536;
				if (sky != 5284)
					parallaxyoffs = 48;

				switch (sky) {
				case 5450:
					for (int i = 0; i < 8; i++)
						pskyoff[i] = (short) ((i + 7) & 7);
					break;
				case 5556:
					for (int i = 0; i < 8; i++)
						pskyoff[i] = (short) ((i + 1) & 7);
					break;
				case 5720:
					for (int i = 0; i < 8; i++)
						pskyoff[i] = (short) ((i + 4) & 7);
					break;
				default:
					for (int i = 0; i < 8; i++)
						pskyoff[i] = (short) i;
					break;
				}
			}
			break;
		}

		Arrays.fill(zeropskyoff, (short) 0);
		System.arraycopy(pskyoff, 0, zeropskyoff, 0, MAXPSKYTILES);

		pskybits = 3;
	}

	public static final short[] lotags = new short[65];

	public static void prelevel() {
		short i, nexti, j, startwall, endwall, lotaglist;
		Arrays.fill(lotags, (short) 0);

		Arrays.fill(show2dsector, (byte) 0);
		Arrays.fill(show2dwall, (byte) 0);
		Arrays.fill(show2dsprite, (byte) 0);

		resetprestat(0);
		numclouds = 0;
		gVisibility = currentGame.getCON().const_visibility;

		for (i = 0; i < numsectors; i++) {
			sector[i].extra = 256;

			switch (sector[i].lotag) {
			case 20:
			case 22:
				if (sector[i].floorz > sector[i].ceilingz)
					sector[i].lotag |= 32768;
				continue;
			}

			if ((sector[i].ceilingstat & 1) != 0) {
				setupbackdrop(sector[i].ceilingpicnum);

				if (sector[i].ceilingpicnum == CLOUDYSKIES && numclouds < 127)
					clouds[numclouds++] = i;

				if (ps[0].one_parallax_sectnum == -1)
					ps[0].one_parallax_sectnum = i;
			}

			if (sector[i].lotag == 32767) // Found a secret room
			{
				ps[0].max_secret_rooms++;
				continue;
			}

			if (sector[i].lotag == -1) {
				ps[0].exitx = wall[sector[i].wallptr].x;
				ps[0].exity = wall[sector[i].wallptr].y;
				continue;
			}
		}

		i = headspritestat[0];
		while (i >= 0) {
			nexti = nextspritestat[i];

			if (sprite[i].lotag == -1 && (sprite[i].cstat & 16) != 0) {
				ps[0].exitx = sprite[i].x;
				ps[0].exity = sprite[i].y;
			} else
				switch (sprite[i].picnum) {
				case GPSPEED:
					sector[sprite[i].sectnum].extra = sprite[i].lotag;
					engine.deletesprite(i);
					break;

				case CYCLER:
					if (numcyclers >= MAXCYCLERS)
						game.dassert("\nToo many cycling sectors.");
					cyclers[numcyclers][0] = sprite[i].sectnum;
					cyclers[numcyclers][1] = sprite[i].lotag;
					cyclers[numcyclers][2] = sprite[i].shade;
					cyclers[numcyclers][3] = sector[sprite[i].sectnum].floorshade;
					cyclers[numcyclers][4] = sprite[i].hitag;
					cyclers[numcyclers][5] = (short) ((sprite[i].ang == 1536) ? 1 : 0);
					numcyclers++;
					engine.deletesprite(i);
					break;
				}
			i = nexti;
		}

		for (i = 0; i < MAXSPRITES; i++) {
			if (sprite[i].statnum < MAXSTATUS) {
				if (sprite[i].picnum == SECTOREFFECTOR && sprite[i].lotag == 14)
					continue;
				spawn(-1, i);
			}
		}

		for (i = 0; i < MAXSPRITES; i++)
			if (sprite[i].statnum < MAXSTATUS) {
				if (sprite[i].picnum == SECTOREFFECTOR && sprite[i].lotag == 14)
					spawn(-1, i);
			}

		lotaglist = 0;

		i = headspritestat[0];
		while (i >= 0) {
			switch (sprite[i].picnum) {
			case DIPSWITCH:
			case DIPSWITCH2:
			case ACCESSSWITCH:
			case PULLSWITCH:
			case HANDSWITCH:
			case SLOTDOOR:
			case LIGHTSWITCH:
			case SPACELIGHTSWITCH:
			case SPACEDOORSWITCH:
			case FRANKENSTINESWITCH:
			case LIGHTSWITCH2:
			case POWERSWITCH1:
			case LOCKSWITCH1:
			case POWERSWITCH2:
				break;
			case DIPSWITCH + 1:
			case DIPSWITCH2 + 1:
			case PULLSWITCH + 1:
			case HANDSWITCH + 1:
			case SLOTDOOR + 1:
			case LIGHTSWITCH + 1:
			case SPACELIGHTSWITCH + 1:
			case SPACEDOORSWITCH + 1:
			case FRANKENSTINESWITCH + 1:
			case LIGHTSWITCH2 + 1:
			case POWERSWITCH1 + 1:
			case LOCKSWITCH1 + 1:
			case POWERSWITCH2 + 1:
				for (j = 0; j < lotaglist; j++)
					if (sprite[i].lotag == lotags[j])
						break;

				if (j == lotaglist) {
					lotags[lotaglist] = sprite[i].lotag;
					lotaglist++;
					if (lotaglist > 64)
						game.dassert("\nToo many switches (64 max).");

					j = headspritestat[3];
					while (j >= 0) {
						if (sprite[j].lotag == 12 && sprite[j].hitag == sprite[i].lotag)
							hittype[j].temp_data[0] = 1;
						j = nextspritestat[j];
					}
				}
				break;
			}
			i = nextspritestat[i];
		}

		mirrorcnt = 0;

		for (i = 0; i < numwalls; i++) {
			WALL wal = wall[i];

			if (wal.overpicnum == MIRROR && (wal.cstat & 32) != 0) {
				j = wal.nextsector;

				if (mirrorcnt > 63)
					game.dassert("\nToo many mirrors (64 max.)");
				if ((j >= 0) && sector[j].ceilingpicnum != MIRROR) {
					sector[j].ceilingpicnum = MIRROR;
					sector[j].floorpicnum = MIRROR;
					mirrorwall[mirrorcnt] = i;
					mirrorsector[mirrorcnt] = j;
					mirrorcnt++;
					continue;
				}
			}

			if (numanimwalls >= MAXANIMWALLS)
				game.dassert("\nToo many 'anim' walls (max 512.)");

			animwall[numanimwalls].tag = 0;
			animwall[numanimwalls].wallnum = 0;

			switch (wal.overpicnum) {
			case FANSHADOW:
			case FANSPRITE:
				wall[0].cstat |= 65; // original typo wall->cstat |= 65 instead of wal->cstat |= 65;
				animwall[numanimwalls].wallnum = i;
				numanimwalls++;
				break;

			case W_FORCEFIELD:
			case W_FORCEFIELD + 1:
			case W_FORCEFIELD + 2:
				if (wal.shade > 31)
					wal.cstat = 0;
				else
					wal.cstat |= 85 + 256;

				if (wal.lotag != 0 && wal.nextwall >= 0)
					wall[wal.nextwall].lotag = wal.lotag;
			case BIGFORCE:

				animwall[numanimwalls].wallnum = i;
				numanimwalls++;

				continue;
			}

			wal.extra = -1;

			switch (wal.picnum) {
			case W_TECHWALL1:
			case W_TECHWALL2:
			case W_TECHWALL3:
			case W_TECHWALL4:
				animwall[numanimwalls].wallnum = i;
				numanimwalls++;
				break;
			case SCREENBREAK6:
			case SCREENBREAK7:
			case SCREENBREAK8:
				animwall[numanimwalls].wallnum = i;
				animwall[numanimwalls].tag = -1;
				numanimwalls++;
				break;

			case FEMPIC1:
			case FEMPIC2:
			case FEMPIC3:
				wal.extra = wal.picnum;
				animwall[numanimwalls].tag = -1;
				if (ud.lockout != 0) {
					if (wal.picnum == FEMPIC1)
						wal.picnum = BLANKSCREEN;
					else
						wal.picnum = SCREENBREAK6;
				}

				animwall[numanimwalls].wallnum = i;
				animwall[numanimwalls].tag = wal.picnum;
				numanimwalls++;
				break;

			case SCREENBREAK1:
			case SCREENBREAK2:
			case SCREENBREAK3:
			case SCREENBREAK4:
			case SCREENBREAK5:

			case SCREENBREAK9:
			case SCREENBREAK10:
			case SCREENBREAK11:
			case SCREENBREAK12:
			case SCREENBREAK13:
			case SCREENBREAK14:
			case SCREENBREAK15:
			case SCREENBREAK16:
			case SCREENBREAK17:
			case SCREENBREAK18:
			case SCREENBREAK19:

				animwall[numanimwalls].wallnum = i;
				animwall[numanimwalls].tag = wal.picnum;
				numanimwalls++;
				break;
			}
		}

		// Invalidate textures in sector behind mirror
		for (i = 0; i < mirrorcnt; i++) {
			startwall = sector[mirrorsector[i]].wallptr;
			endwall = (short) (startwall + sector[mirrorsector[i]].wallnum);
			for (j = startwall; j < endwall; j++) {
				wall[j].picnum = MIRROR;
				wall[j].overpicnum = MIRROR;
			}
		}

		InitSpecialTextures();
	}

	public static PlayerInfo[] info = new PlayerInfo[MAXPLAYERS];

	public static void resetpspritevars() {
		short i, j, nexti;

		EGS(ps[0].cursectnum, ps[0].posx, ps[0].posy, ps[0].posz, APLAYER, 0, 0, 0, (short) ps[0].ang, 0, 0, 0,
				(short) 10);

		if (ud.recstat != 2)
			for (i = 0; i < MAXPLAYERS; i++) {
				if (info[i] == null)
					info[i] = new PlayerInfo();
				info[i].set(ps[i]);
			}

		resetplayerstats(0);

		for (i = 1; i < MAXPLAYERS; i++)
			ps[i].copy(ps[0]);

		if (ud.recstat != 2)
			for (i = 0; i < MAXPLAYERS; i++) {
				info[i].restore(ps[i]);
				if (ps[i].curr_weapon == PISTOL_WEAPON)
					ps[i].kickback_pic = 5;
				else
					ps[i].kickback_pic = 0;
			}

		numplayersprites = 0;

		which_palookup = 9;
		j = connecthead;
		i = headspritestat[10];
		while (i >= 0) {
			nexti = nextspritestat[i];
			SPRITE s = sprite[i];

			if (numplayersprites == MAXPLAYERS)
				game.dassert("\nToo many player sprites (max 16.)");

			po[numplayersprites].ox = s.x;
			po[numplayersprites].oy = s.y;
			po[numplayersprites].oz = s.z;
			po[numplayersprites].oa = s.ang;
			po[numplayersprites].os = s.sectnum;

			numplayersprites++;
			if (j >= 0) {
				s.owner = i;
				s.shade = 0;
				s.xrepeat = 42;
				s.yrepeat = 36;
				s.cstat = 1 + 256;
				s.xoffset = 0;
				s.clipdist = 64;

				if ((uGameFlags & MODE_EOL) != MODE_EOL || ps[j].last_extra == 0) {
					ps[j].last_extra = (short) currentGame.getCON().max_player_health;
					s.extra = (short) currentGame.getCON().max_player_health;
				} else
					s.extra = ps[j].last_extra;

				s.yvel = j;

				if (s.pal == 0) {
					s.pal = ps[j].palookup = which_palookup;
					which_palookup++;
					if (which_palookup >= 17)
						which_palookup = 9;
				} else
					ps[j].palookup = s.pal;

				ps[j].i = i;
				ps[j].frag_ps = j;
				hittype[i].owner = i;

				ps[j].bobposx = ps[j].oposx = ps[j].posx = s.x;
				ps[j].bobposy = ps[j].oposy = ps[j].posy = s.y;
				ps[j].oposz = ps[j].posz = s.z;
				ps[j].oang = ps[j].ang = s.ang;
				ps[j].cursectnum = engine.updatesector(s.x, s.y, ps[j].cursectnum);
				j = connectpoint2[j];
			} else
				engine.deletesprite(i);
			i = nexti;
		}

		for (i = numplayersprites; i < MAXPLAYERS; i++)
			ps[i].copy(ps[i % numplayersprites]); // players without position
	}

	public static void clearfrags() {
		for (int i = 0; i < MAXPLAYERS; i++) {
			ps[i].frag = ps[i].fraggedself = 0;
			Arrays.fill(frags[i], (short) 0);
		}
	}

	public static void genspriteremaps() throws FileNotFoundException {
		int j;
		int look_pos;
		int numl = 0;

		Resource fp = BuildGdx.cache.open("lookup.dat", 0);
		if (fp != null)
			numl = fp.readByte();
		else
			throw new FileNotFoundException("\nERROR: File 'LOOKUP.DAT' not found.");

		for (j = 0; j < numl; j++) {
			look_pos = fp.readByte();
			fp.read(tempbuf, 0, 256);
			engine.makepalookup(look_pos, tempbuf, 0, 0, 0, 1);
		}

		fp.read(waterpal);
		fp.read(slimepal);
		fp.read(titlepal);
		fp.read(drealms);
		fp.read(endingpal);

		palette[765] = palette[766] = palette[767] = 0;
		slimepal[765] = slimepal[766] = slimepal[767] = 0;
		waterpal[765] = waterpal[766] = waterpal[767] = 0;

		fp.close();
	}

	public static void checknextlevel() {
		if (ud.level_number >= currentGame.episodes[ud.volume_number].nMaps) {
			if (ud.volume_number == 0 && currentGame.nEpisodes > 0) {
				ud.level_number = 0;
				ud.volume_number = 1;
			} else
				ud.level_number = 0;
		}
	}

	public static void LeaveMap() {
		System.err.println("LeaveMap");
		if (numplayers > 1 && game.pNet.bufferJitter >= 0 && myconnectindex == connecthead)
			for (int i = 0; i <= game.pNet.bufferJitter; i++)
				game.pNet.GetNetworkInput(); // wait for other player before level end

		if (!game.pNet.WaitForAllPlayers(5000)) {
			game.pNet.NetDisconnect(myconnectindex);
			return;
		}

		uGameFlags |= MODE_EOL;
		if (mUserFlag != UserFlag.UserMap && (uGameFlags & MODE_END) == 0
				&& ud.level_number >= currentGame.episodes[ud.volume_number].nMaps)
			uGameFlags |= MODE_END;

		if (ud.rec != null)
			ud.rec.close();
	}
}

class PlayerInfo {
	public int aimmode;
	public int autoaim;

	public int[] ammo_amount = new int[MAX_WEAPONS];
	public boolean[] gotweapon = new boolean[MAX_WEAPONS];

	public short shield_amount;
	public short curr_weapon;
	public int inven_icon;

	public short firstaid_amount;
	public short steroids_amount;
	public short holoduke_amount;
	public short jetpack_amount;
	public short heat_amount;
	public short scuba_amount;
	public short boot_amount;
	public short last_extra;

	public void set(PlayerStruct p) {
		aimmode = p.aim_mode;
		autoaim = p.auto_aim;

		if (ud.multimode > 1 && ud.coop == 1 && ud.last_level >= 0) {
			for (int j = 0; j < MAX_WEAPONS; j++) {
				ammo_amount[j] = p.ammo_amount[j];
				gotweapon[j] = p.gotweapon[j];
			}
			shield_amount = p.shield_amount;
			curr_weapon = p.curr_weapon;
			inven_icon = p.inven_icon;

			firstaid_amount = p.firstaid_amount;
			steroids_amount = p.steroids_amount;
			holoduke_amount = p.holoduke_amount;
			jetpack_amount = p.jetpack_amount;
			heat_amount = p.heat_amount;
			scuba_amount = p.scuba_amount;
			boot_amount = p.boot_amount;
			last_extra = p.last_extra;
		}
	}

	public void restore(PlayerStruct p) {
		p.aim_mode = aimmode;
		p.auto_aim = autoaim;

		if (ud.multimode > 1 && ud.coop == 1 && ud.last_level >= 0) {
			for (int j = 0; j < MAX_WEAPONS; j++) {
				p.ammo_amount[j] = ammo_amount[j];
				p.gotweapon[j] = gotweapon[j];
			}
			p.shield_amount = shield_amount;
			p.curr_weapon = curr_weapon;
			p.inven_icon = inven_icon;

			p.firstaid_amount = firstaid_amount;
			p.steroids_amount = steroids_amount;
			p.holoduke_amount = holoduke_amount;
			p.jetpack_amount = jetpack_amount;
			p.heat_amount = heat_amount;
			p.scuba_amount = scuba_amount;
			p.boot_amount = boot_amount;
			p.last_extra = last_extra;
		}
	}
}
