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

import static java.lang.Math.*;
import static ru.m210projects.Duke3D.Factory.DukeInput.*;
import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Build.Gameutils.*;
import static ru.m210projects.Build.Pragmas.*;
import static ru.m210projects.Build.Strhandler.buildString;
import static ru.m210projects.Build.Net.Mmulti.*;
import static ru.m210projects.Duke3D.Premap.*;
import static ru.m210projects.Duke3D.Main.*;
import static ru.m210projects.Duke3D.Globals.*;
import static ru.m210projects.Duke3D.Sector.*;
import static ru.m210projects.Duke3D.Gameutils.*;
import static ru.m210projects.Duke3D.Screen.*;
import static ru.m210projects.Duke3D.Gamedef.*;
import static ru.m210projects.Duke3D.Actors.*;
import static ru.m210projects.Duke3D.Names.*;
import static ru.m210projects.Duke3D.Sounds.*;
import static ru.m210projects.Duke3D.Spawn.*;
import static ru.m210projects.Duke3D.Weapons.*;
import static ru.m210projects.Duke3D.View.*;
import static ru.m210projects.Duke3D.SoundDefs.*;

import java.util.Arrays;

import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Build.Types.WALL;
import ru.m210projects.Duke3D.Types.PlayerStruct;

public class Player {

	public static final byte[] waterpal = new byte[768], slimepal = new byte[768], titlepal = new byte[768],
			drealms = new byte[768], endingpal = new byte[768];

	public static void InitPlayers() {
		ps[myconnectindex].reset();

		ps[myconnectindex].palette = palette;
		ps[myconnectindex].aim_mode = cfg.gMouseAim ? 1 : 0;
		ps[myconnectindex].auto_aim = cfg.gAutoAim ? 1 : 0;

		for (int i = 1; i < MAXPLAYERS; i++)
			ps[i].copy(ps[myconnectindex]);

		game.net.getnames();
	}

	public static void checkavailinven(PlayerStruct p) {
		if (p.firstaid_amount > 0)
			p.inven_icon = 1;
		else if (p.steroids_amount > 0)
			p.inven_icon = 2;
		else if (p.holoduke_amount > 0)
			p.inven_icon = 3;
		else if (p.jetpack_amount > 0)
			p.inven_icon = 4;
		else if (p.heat_amount > 0)
			p.inven_icon = 5;
		else if (p.scuba_amount > 0)
			p.inven_icon = 6;
		else if (p.boot_amount > 0)
			p.inven_icon = 7;
		else
			p.inven_icon = 0;
	}

	public static void checkavailweapon(PlayerStruct p) {
		int weap;
		if (p.wantweaponfire >= 0) {
			weap = p.wantweaponfire;
			p.wantweaponfire = -1;

			if (weap == p.curr_weapon || weap >= MAX_WEAPONS)
				return;
			else if (p.gotweapon[weap] && p.ammo_amount[weap] > 0) {
				addweapon(p, weap);
				return;
			}
		}

		weap = p.curr_weapon;
		if (p.gotweapon[weap] && p.ammo_amount[weap] > 0)
			return;

		int snum = sprite[p.i].yvel, i;
		for (i = 0; i < 10; i++) {
			weap = ud.wchoice[snum][i];

			if (weap == 0)
				weap = 9;
			else
				weap--;

			if (weap == 0 || (p.gotweapon[weap] && p.ammo_amount[weap] > 0))
				break;
		}

		if (i == 10)
			weap = 0;

		// Found the weapon

		p.last_weapon = p.curr_weapon;
		p.random_club_frame = 0;
		p.curr_weapon = (short) weap;
		p.kickback_pic = 0;
		if (p.holster_weapon == 1) {
			p.holster_weapon = 0;
			p.weapon_pos = 10;
		} else
			p.weapon_pos = -1;
	}

	public static boolean inventory(SPRITE s) {
		switch (s.picnum) {
		case FIRSTAID:
		case STEROIDS:
		case HEATSENSOR:
		case BOOTS:
		case JETPACK:
		case HOLODUKE:
		case AIRTANK:
			return true;
		}
		return false;
	}

	public static void setpal(PlayerStruct p) {
		if (p.heat_on != 0)
			p.palette = slimepal;
		else
			switch (sector[p.cursectnum].ceilingpicnum) {
			case FLOORSLIME:
			case FLOORSLIME + 1:
			case FLOORSLIME + 2:
				p.palette = slimepal;
				break;
			default:
				if (sector[p.cursectnum].lotag == 2)
					p.palette = waterpal;
				else
					p.palette = palette;
				break;
			}

		changepalette = 1;
	}

	public static void incur_damage(PlayerStruct p) {
		int damage = 0, shield_damage = 0;

		sprite[p.i].extra -= p.extra_extra8 >> 8;

		damage = sprite[p.i].extra - p.last_extra;

		if (damage < 0) {
			p.extra_extra8 = 0;

			if (p.shield_amount > 0) {
				shield_damage = damage * (20 + (engine.krand() % 30)) / 100;
				damage -= shield_damage;

				p.shield_amount += shield_damage;

				if (p.shield_amount < 0) {
					damage += p.shield_amount;
					p.shield_amount = 0;
				}
			}

			sprite[p.i].extra = (short) (p.last_extra + damage);
		}
	}

	public static void quickkill(PlayerStruct p) {
		p.pals[0] = 48;
		p.pals[1] = 48;
		p.pals[2] = 48;
		p.pals_time = 48;

		sprite[p.i].extra = 0;
		sprite[p.i].cstat |= 32768;
		if (!ud.god)
			guts(sprite[p.i], JIBS6, 8, myconnectindex);
		return;
	}

	public static void forceplayerangle(PlayerStruct p) {
		int n = 128 - (engine.krand() & 255);

		p.horiz += 64;
		p.return_to_center = 9;
		p.look_ang = (short) (n >> 1);
		p.rotscrnang = (short) (n >> 1);
	}

	public static int hits(int i) {
		int zoff = 0;

		SPRITE spr = sprite[i];

		if (spr.picnum == APLAYER)
			zoff = (40 << 8);

		engine.hitscan(spr.x, spr.y, spr.z - zoff, spr.sectnum, sintable[(spr.ang + 512) & 2047],
				sintable[spr.ang & 2047], 0, pHitInfo, CLIPMASK1);

		return (FindDistance2D(pHitInfo.hitx - spr.x, pHitInfo.hity - spr.y));
	}

	public static int hitasprite(int i) // return pHitInfo.pHitInfo.hitspriteite
	{
		int zoff = 0;
		if (badguy(sprite[i]))
			zoff = (42 << 8);
		else if (sprite[i].picnum == APLAYER)
			zoff = (39 << 8);

		engine.hitscan(sprite[i].x, sprite[i].y, sprite[i].z - zoff, sprite[i].sectnum,
				sintable[(sprite[i].ang + 512) & 2047], sintable[sprite[i].ang & 2047], 0, pHitInfo, CLIPMASK1);

		if (pHitInfo.hitwall >= 0 && (wall[pHitInfo.hitwall].cstat & 16) != 0 && badguy(sprite[i]))
			return ((1 << 30));

		return (FindDistance2D(pHitInfo.hitx - sprite[i].x, pHitInfo.hity - sprite[i].y));
	}

	public static int hitawall(PlayerStruct p) // return pHitInfo.pHitInfo.hitwall
	{
		if (IsOriginalDemo()) {
			engine.hitscan(p.posx, p.posy, p.posz, p.cursectnum, sintable[((int) p.ang + 512) & 2047],
					sintable[(int) p.ang & 2047], 0, pHitInfo, CLIPMASK0);

		} else {
			engine.hitscan(p.posx, p.posy, p.posz, p.cursectnum, (int) BCosAngle(BClampAngle(p.ang)),
					(int) BSinAngle(BClampAngle(p.ang)), 0, pHitInfo, CLIPMASK0);
		}

		return (FindDistance2D(pHitInfo.hitx - p.posx, pHitInfo.hity - p.posy));
	}

	public static boolean doincrements(PlayerStruct p) {
		int snum = sprite[p.i].yvel;

		p.player_par++;

		if (p.invdisptime > 0)
			p.invdisptime--;

		if (p.tipincs > 0)
			p.tipincs--;

		if (p.last_pissed_time > 0) {
			p.last_pissed_time--;

			if (p.last_pissed_time == (26 * 219)) {
				spritesound(FLUSH_TOILET, p.i);
				if (snum == screenpeek || ud.coop == 1)
					spritesound(DUKE_PISSRELIEF, p.i);
			}

			if (p.last_pissed_time == (26 * 218)) {
				p.holster_weapon = 0;
				p.weapon_pos = 10;
			}
		}

		if (p.crack_time > 0) {
			p.crack_time--;
			if (p.crack_time == 0) {
				p.knuckle_incs = 1;
				p.crack_time = 777;
			}
		}

		if (p.steroids_amount > 0 && p.steroids_amount < 400) {
			p.steroids_amount--;
			if (p.steroids_amount == 0)
				checkavailinven(p);
			if ((p.steroids_amount & 7) == 0)
				if (snum == screenpeek || ud.coop == 1)
					spritesound(DUKE_HARTBEAT, p.i);
		}

		if (p.heat_on != 0 && p.heat_amount > 0) {
			p.heat_amount--;
			if (p.heat_amount == 0) {
				p.heat_on = 0;
				checkavailinven(p);
				spritesound(NITEVISION_ONOFF, p.i);
				setpal(p);
			}
		}

		if (p.holoduke_on >= 0) {
			p.holoduke_amount--;
			if (p.holoduke_amount <= 0) {
				spritesound(TELEPORTER, p.i);
				p.holoduke_on = -1;
				checkavailinven(p);
			}
		}

		if (p.jetpack_on != 0 && p.jetpack_amount > 0) {
			p.jetpack_amount--;
			if (p.jetpack_amount <= 0) {
				p.jetpack_on = 0;
				checkavailinven(p);
				spritesound(DUKE_JETPACK_OFF, p.i);
				stopsound(DUKE_JETPACK_IDLE, p.i);
				stopsound(DUKE_JETPACK_ON, p.i);
			}
		}

		if (p.quick_kick > 0 && sprite[p.i].pal != 1) {
			p.quick_kick--;
			if (p.quick_kick == 8)
				shoot(p.i, KNEE);
		}

		if (p.access_incs != 0 && sprite[p.i].pal != 1) {
			p.access_incs++;
			if (sprite[p.i].extra <= 0)
				p.access_incs = 12;
			if (p.access_incs == 12) {
				if (p.access_spritenum >= 0) {
					checkhitswitch(snum, p.access_spritenum, 1);
					switch (sprite[p.access_spritenum].pal) {
					case 0:
						p.got_access &= (0xffff - 0x1);
						break;
					case 21:
						p.got_access &= (0xffff - 0x2);
						break;
					case 23:
						p.got_access &= (0xffff - 0x4);
						break;
					}
					p.access_spritenum = -1;
				} else {
					checkhitswitch(snum, p.access_wallnum, 0);
					switch (wall[p.access_wallnum].pal) {
					case 0:
						p.got_access &= (0xffff - 0x1);
						break;
					case 21:
						p.got_access &= (0xffff - 0x2);
						break;
					case 23:
						p.got_access &= (0xffff - 0x4);
						break;
					}
				}
			}

			if (p.access_incs > 20) {
				p.access_incs = 0;
				p.weapon_pos = 10;
				p.kickback_pic = 0;
			}
		}

		if (p.scuba_on == 0 && p.cursectnum != -1 && sector[p.cursectnum].lotag == 2) {
			if (p.scuba_amount > 0) {
				p.scuba_on = 1;
				p.inven_icon = 6;
				FTA(76, p);
			} else {
				if (p.airleft > 0)
					p.airleft--;
				else {
					p.extra_extra8 += 32;
					if (p.last_extra < (currentGame.getCON().max_player_health >> 1) && (p.last_extra & 3) == 0)
						spritesound(DUKE_LONGTERM_PAIN, p.i);
				}
			}
		} else if (p.scuba_amount > 0 && p.scuba_on != 0) {
			p.scuba_amount--;
			if (p.scuba_amount == 0) {
				p.scuba_on = 0;
				checkavailinven(p);
			}
		}

		if (p.knuckle_incs != 0) {
			p.knuckle_incs++;
			if (p.knuckle_incs == 10) {
				if (totalclock > 1024)
					if (snum == screenpeek || ud.coop == 1) {
						if ((engine.rand() & 1) != 0)
							spritesound(DUKE_CRACK, p.i);
						else
							spritesound(DUKE_CRACK2, p.i);
					}
				spritesound(DUKE_CRACK_FIRST, p.i);
			} else if (p.knuckle_incs == 22 || (sync[snum].bits & (1 << 2)) != 0)
				p.knuckle_incs = 0;

			return true;
		}
		return false;
	}

	public static void processinput(int snum) {
		int j, i, k, doubvel, fz, cz, hz, lz, x, y;
		long truefdist;
		boolean shrunk;
		int sb_snum;
		short psect, psectlotag, tempsect, pi;

		PlayerStruct p = ps[snum];
		pi = p.i;
		SPRITE s = sprite[pi];

		if (p.cheat_phase <= 0)
			sb_snum = sync[snum].bits;
		else
			sb_snum = 0;

		psect = p.cursectnum;
		if (psect == -1) {
			if (s.extra > 0 && !ud.clipping) {
				quickkill(p);
				spritesound(SQUISHED, pi);
			}
			psect = 0;
		}

		if(!isValidSector(s.sectnum) || !isValidSector(psect))
			return;

		psectlotag = sector[psect].lotag;
		p.spritebridge = 0;

		shrunk = (s.yrepeat < 32);
		engine.getzrange(p.posx, p.posy, p.posz, psect, 163, CLIPMASK0);
		cz = zr_ceilz;
		hz = zr_ceilhit;
		fz = zr_florz;
		lz = zr_florhit;
		j = engine.getflorzofslope(psect, p.posx, p.posy);

		p.truefz = j;
		p.truecz = engine.getceilzofslope(psect, p.posx, p.posy);

		truefdist = klabs(p.posz - j);
		if ((lz & kHitTypeMask) == kHitSector && psectlotag == 1 && truefdist > PHEIGHT + (16 << 8))
			psectlotag = 0;

		hittype[pi].floorz = fz;
		hittype[pi].ceilingz = cz;

		p.ohoriz = p.horiz;
		p.ohorizoff = p.horizoff;

		if (p.aim_mode == 0 && p.on_ground && psectlotag != 2 && (sector[psect].floorstat & 2) != 0) {
			if (IsOriginalDemo()) {
				x = p.posx + (sintable[((int) p.ang + 512) & 2047] >> 5);
				y = p.posy + (sintable[(int) p.ang & 2047] >> 5);
			} else {
				x = (int) (p.posx + (BCosAngle(BClampAngle(p.ang)) / 32.0f));
				y = (int) (p.posy + (BSinAngle(BClampAngle(p.ang)) / 32.0f));
			}
			tempsect = engine.updatesector(x, y, psect);
			if (tempsect >= 0) {
				k = engine.getflorzofslope(psect, x, y);
				if (psect == tempsect)
					p.horizoff += mulscale(j - k, 160, 16);
				else if (klabs(engine.getflorzofslope(tempsect, x, y) - k) <= (4 << 8))
					p.horizoff += mulscale(j - k, 160, 16);
			}
		}
		if (p.horizoff > 0)
			p.horizoff -= ((p.horizoff >> 3) + 1);
		else if (p.horizoff < 0)
			p.horizoff += (((-p.horizoff) >> 3) + 1);

		if (hz >= 0 && (hz & kHitTypeMask) == kHitSprite) {
			hz &= kHitIndexMask;

			if (sprite[hz].statnum == 1 && sprite[hz].extra >= 0) {
				hz = 0;
				cz = p.truecz;
			}
		}

		if (lz >= 0 && (lz & kHitTypeMask) == kHitSprite) {
			j = lz & kHitIndexMask;

			if ((sprite[j].cstat & 33) == 33) {
				psectlotag = 0;
				p.footprintcount = 0;
				p.spritebridge = 1;
			} else if (badguy(sprite[j]) && sprite[j].xrepeat > 24 && klabs(s.z - sprite[j].z) < (84 << 8)) {
				j = engine.getangle(sprite[j].x - p.posx, sprite[j].y - p.posy);
				p.posxv -= sintable[(j + 512) & 2047] << 4;
				p.posyv -= sintable[j & 2047] << 4;
			}
		}

		if (s.extra > 0)
			incur_damage(p);
		else {
			s.extra = 0;
			p.shield_amount = 0;
		}

		p.last_extra = s.extra;

		if (p.loogcnt > 0)
			p.loogcnt--;
		else
			p.loogcnt = 0;

		if (p.fist_incs != 0) {
			p.fist_incs++;
			if (p.fist_incs == 28) {
				if (ud.recstat == 1 && ud.rec != null)
					ud.rec.close();
				sound(PIPEBOMB_EXPLODE);
				p.pals[0] = 64;
				p.pals[1] = 64;
				p.pals[2] = 64;
				p.pals_time = 48;
			}

			if (p.fist_incs > 42) {
				if (p.buttonpalette != 0 && ud.from_bonus == 0) {
					ud.from_bonus = ud.level_number + 1;
					if (ud.secretlevel > 0 && ud.secretlevel < 12)
						ud.level_number = ud.secretlevel - 1;
				} else {
					if (ud.from_bonus != 0) {
						ud.level_number = ud.from_bonus;
						ud.from_bonus = 0;
					} else {
						if (ud.level_number == ud.secretlevel && ud.from_bonus > 0)
							ud.level_number = ud.from_bonus;
						else
							ud.level_number++;
					}
				}

				LeaveMap();
				p.fist_incs = 0;

				return;
			}
		}

		if (p.timebeforeexit > 1 && p.last_extra > 0) {
			p.timebeforeexit--;
			if (p.timebeforeexit == 26 * 5) {
				BuildGdx.audio.getSound().stopAllSounds();
				clearsoundlocks();
				if (p.customexitsound >= 0) {
					sound(p.customexitsound);
					FTA(102, p);
				}
			} else if (p.timebeforeexit == 1) {
				LeaveMap();
				if (ud.from_bonus != 0) {
					ud.level_number = ud.from_bonus;
					ud.from_bonus = 0;
				} else {
					ud.level_number++;
				}
				return;
			}
		}

		if (p.pals_time > 0)
			p.pals_time--;

		if (s.extra <= 0) {
			if (p.dead_flag == 0) {
				if (s.pal != 1) {
					p.pals[0] = 63;
					p.pals[1] = 0;
					p.pals[2] = 0;
					p.pals_time = 63;
					p.posz -= (16 << 8);
					s.z -= (16 << 8);
				}

				if (ud.recstat == 1 && ud.multimode < 2 && ud.rec != null)
					ud.rec.close();

				if (s.pal != 1)
					p.dead_flag = (short) ((512 - ((engine.krand() & 1) << 10) + (engine.krand() & 255) - 512) & 2047);

				p.jetpack_on = 0;
				p.holoduke_on = -1;

				stopsound(DUKE_JETPACK_IDLE, p.i);
				if (p.scream_voice != null) {
					p.scream_voice.dispose();
					p.scream_voice = null;
				}

				if (s.pal != 1 && (s.cstat & 32768) == 0)
					s.cstat = 0;

				if (ud.multimode > 1 && (s.pal != 1 || (s.cstat & 32768) != 0)) {
					if (p.frag_ps != snum && p.frag_ps < ps.length) {
						ps[p.frag_ps].frag++;
						frags[p.frag_ps][snum]++;
						if (snum == screenpeek) {
							if (ud.user_name[p.frag_ps] != null && !ud.user_name[p.frag_ps].isEmpty())
								buildString(currentGame.getCON().fta_quotes[115], 0, "KILLED BY ",
										ud.user_name[p.frag_ps]);
							else
								buildString(currentGame.getCON().fta_quotes[115], 0, "KILLED BY PLAYER ",
										1 + p.frag_ps);
							FTA(115, p);
						} else {
							if (ud.user_name[snum] != null && !ud.user_name[snum].isEmpty())
								buildString(currentGame.getCON().fta_quotes[116], 0, "KILLED ", ud.user_name[snum]);
							else
								buildString(currentGame.getCON().fta_quotes[116], 0, "KILLED PLAYER ", 1 + snum);
							FTA(116, ps[p.frag_ps]);
						}
					} else
						p.fraggedself++;

					p.frag_ps = (short) snum;
				}
			}

			if (psectlotag == 2) {
				if (p.on_warping_sector == 0) {
					if (klabs(p.posz - fz) > (PHEIGHT >> 1))
						p.posz += 348;
				} else {
					s.z -= 512;
					s.zvel = -348;
				}

				engine.clipmove(p.posx, p.posy, p.posz, p.cursectnum, 0, 0, 164, (4 << 8), (4 << 8), CLIPMASK0);

				if (clipmove_sectnum != -1) {
					p.posx = clipmove_x;
					p.posy = clipmove_y;
					p.posz = clipmove_z;
					p.cursectnum = clipmove_sectnum;
				}
			}

			p.oposx = p.posx;
			p.oposy = p.posy;
			p.oposz = p.posz;
			p.oang = p.ang;
			p.opyoff = p.pyoff;

			p.horiz = 100;
			p.horizoff = 0;

			short sect = engine.updatesector(p.posx, p.posy, p.cursectnum);
			if (sect != -1)
				p.cursectnum = sect;

			engine.pushmove(p.posx, p.posy, p.posz, p.cursectnum, 128, (4 << 8), (20 << 8), CLIPMASK0);

			if (pushmove_sectnum != -1) {
				p.posx = pushmove_x;
				p.posy = pushmove_y;
				p.posz = pushmove_z;
				p.cursectnum = pushmove_sectnum;
			}

			if (fz > cz + (16 << 8) && s.pal != 1)
				p.rotscrnang = (short) ((p.dead_flag + ((fz + p.posz) >> 7)) & 2047);

			p.on_warping_sector = 0;
			return;
		}

		if (p.transporter_hold > 0) {
			p.transporter_hold--;
			if (p.transporter_hold == 0 && p.on_warping_sector != 0)
				p.transporter_hold = 2;
		}
		if (p.transporter_hold < 0)
			p.transporter_hold++;

		boolean SHOOTINCODE = false;
		if (p.newowner >= 0) {
			i = p.newowner;
			hittype[p.i].tempang = (int) p.ang;
			p.posx = sprite[i].x;
			p.posy = sprite[i].y;
			p.posz = sprite[i].z;
			p.ang = sprite[i].ang;
			p.posxv = p.posyv = s.xvel = 0;
			p.look_ang = 0;
			p.rotscrnang = 0;

			doincrements(p);

			if (p.curr_weapon == HANDREMOTE_WEAPON)
				SHOOTINCODE = true;
			else
				return;
		}

		if (!SHOOTINCODE) {
			doubvel = TICSPERFRAME;

			if (p.rotscrnang > 0)
				p.rotscrnang -= ((p.rotscrnang >> 1) + 1);
			else if (p.rotscrnang < 0)
				p.rotscrnang += (((-p.rotscrnang) >> 1) + 1);

			p.look_ang -= (p.look_ang >> 2);

			if ((sb_snum & (1 << 6)) != 0) {
				p.look_ang -= 152;
				p.rotscrnang += 24;
			}

			if ((sb_snum & (1 << 7)) != 0) {
				p.look_ang += 152;
				p.rotscrnang -= 24;
			}

			if (p.on_crane < 0) {
				j = ksgn((int) sync[snum].avel);

				if (s.xvel < 32 || !p.on_ground || p.bobcounter == 1024) {
					if ((p.weapon_sway & 2047) > (1024 + 96))
						p.weapon_sway -= 96;
					else if ((p.weapon_sway & 2047) < (1024 - 96))
						p.weapon_sway += 96;
					else
						p.weapon_sway = 1024;
				} else
					p.weapon_sway = p.bobcounter;

				s.xvel = (short) engine.ksqrt(
						(p.posx - p.bobposx) * (p.posx - p.bobposx) + (p.posy - p.bobposy) * (p.posy - p.bobposy));
				if (p.on_ground)
					p.bobcounter += sprite[p.i].xvel >> 1;

				if (!ud.clipping && (sector[p.cursectnum].floorpicnum == MIRROR || p.cursectnum < 0
						|| p.cursectnum >= MAXSECTORS)) {
					p.posx = p.oposx;
					p.posy = p.oposy;
				} else {
					p.oposx = p.posx;
					p.oposy = p.posy;
				}

				p.bobposx = p.posx;
				p.bobposy = p.posy;

				p.oposz = p.posz;
				p.opyoff = p.pyoff;
				p.oang = p.ang;

				if (p.one_eighty_count < 0) {
					p.one_eighty_count += 128;
					p.ang += 128;
				}

				// Shrinking code

				i = 40;

				if (psectlotag == 2) {
					p.jumping_counter = 0;

					p.pycount += 32;
					p.pycount &= 2047;
					p.pyoff = sintable[p.pycount] >> 7;

					if (Sound[DUKE_UNDERWATER].num == 0)
						spritesound(DUKE_UNDERWATER, pi);

					if ((sb_snum & 1) != 0) {
						if (p.poszv > 0)
							p.poszv = 0;
						p.poszv -= 348;
						if (p.poszv < -(256 * 6))
							p.poszv = -(256 * 6);
					} else if ((sb_snum & (1 << 1)) != 0) {
						if (p.poszv < 0)
							p.poszv = 0;
						p.poszv += 348;
						if (p.poszv > (256 * 6))
							p.poszv = (256 * 6);
					} else {
						if (p.poszv < 0) {
							p.poszv += 256;
							if (p.poszv > 0)
								p.poszv = 0;
						}
						if (p.poszv > 0) {
							p.poszv -= 256;
							if (p.poszv < 0)
								p.poszv = 0;
						}
					}

					if (p.poszv > 2048)
						p.poszv >>= 1;

					p.posz += p.poszv;

					if (p.posz > (fz - (15 << 8)))
						p.posz += ((fz - (15 << 8)) - p.posz) >> 1;

					if (p.posz < (cz + (4 << 8))) {
						p.posz = cz + (4 << 8);
						p.poszv = 0;
					}

					if (p.scuba_on != 0 && (engine.krand() & 255) < 8) {
						j = spawn(pi, WATERBUBBLE);

						if (IsOriginalDemo()) {
							sprite[j].x += sintable[((int) p.ang + 512 + 64 - (global_random & 128)) & 2047] >> 6;
							sprite[j].y += sintable[((int) p.ang + 64 - (global_random & 128)) & 2047] >> 6;
						} else {
							sprite[j].x += BCosAngle(BClampAngle(p.ang + 64 - (global_random & 128)) / 64.0f);
							sprite[j].y += BSinAngle(BClampAngle(p.ang + 64 - (global_random & 128)) / 64.0f);
						}
						sprite[j].xrepeat = 3;
						sprite[j].yrepeat = 2;
						sprite[j].z = p.posz + (8 << 8);
					}
				} else if (p.jetpack_on != 0) {
					p.on_ground = false;
					p.jumping_counter = 0;
					p.hard_landing = 0;
					p.falling_counter = 0;

					p.pycount += 32;
					p.pycount &= 2047;
					p.pyoff = sintable[p.pycount] >> 7;

					if (p.jetpack_on < 11) {
						p.jetpack_on++;
						p.posz -= (p.jetpack_on << 7); // Goin up
					} else if (p.jetpack_on == 11 && Sound[DUKE_JETPACK_IDLE].num < 1)
						spritesound(DUKE_JETPACK_IDLE, pi);

					if (shrunk)
						j = 512;
					else
						j = 2048;

					if ((sb_snum & 1) != 0) // A (soar high)
					{
						p.posz -= j;
						p.crack_time = 777;
					}

					if ((sb_snum & (1 << 1)) != 0) // Z (soar low)
					{
						p.posz += j;
						p.crack_time = 777;
					}

					if (!shrunk && (psectlotag == 0 || psectlotag == 2))
						k = 32;
					else
						k = 16;

					if (psectlotag != 2 && p.scuba_on == 1)
						p.scuba_on = 0;

					if (p.posz > (fz - (k << 8)))
						p.posz += ((fz - (k << 8)) - p.posz) >> 1;
					if (p.posz < (hittype[pi].ceilingz + (18 << 8)))
						p.posz = hittype[pi].ceilingz + (18 << 8);

				} else if (psectlotag != 2) {
					if (p.airleft != 15 * 26)
						p.airleft = 15 * 26; // Aprox twenty seconds.

					if (p.scuba_on == 1)
						p.scuba_on = 0;

					if (psectlotag == 1 && p.spritebridge == 0) {
						if (!shrunk) {
							i = 34;
							p.pycount += 32;
							p.pycount &= 2047;
							p.pyoff = sintable[p.pycount] >> 6;
						} else
							i = 12;

						if (!shrunk && truefdist <= PHEIGHT) {
							if (p.on_ground) {
								if (p.dummyplayersprite == -1)
									p.dummyplayersprite = (short) spawn(pi, PLAYERONWATER);

								p.footprintcount = 6;
								if (sector[p.cursectnum].floorpicnum == FLOORSLIME)
									p.footprintpal = 8;
								else
									p.footprintpal = 0;
								p.footprintshade = 0;
							}
						}
					} else {
						if (p.footprintcount > 0 && p.on_ground)
							if ((sector[p.cursectnum].floorstat & 2) != 2) {
								for (j = headspritesect[psect]; j >= 0; j = nextspritesect[j])
									if (sprite[j].picnum == FOOTPRINTS || sprite[j].picnum == FOOTPRINTS2
											|| sprite[j].picnum == FOOTPRINTS3 || sprite[j].picnum == FOOTPRINTS4)
										if (klabs(sprite[j].x - p.posx) < 384)
											if (klabs(sprite[j].y - p.posy) < 384)
												break;
								if (j < 0) {
									p.footprintcount--;
									if (sector[p.cursectnum].lotag == 0 && sector[p.cursectnum].hitag == 0) {
										switch (engine.krand() & 3) {
										case 0:
											j = spawn(pi, FOOTPRINTS);
											break;
										case 1:
											j = spawn(pi, FOOTPRINTS2);
											break;
										case 2:
											j = spawn(pi, FOOTPRINTS3);
											break;
										default:
											j = spawn(pi, FOOTPRINTS4);
											break;
										}
										sprite[j].pal = p.footprintpal;
										sprite[j].shade = (byte) p.footprintshade;
									}
								}
							}
					}

					if (p.posz < (fz - (i << 8))) // falling
					{
						if ((sb_snum & 3) == 0 && p.on_ground && (sector[psect].floorstat & 2) != 0
								&& p.posz >= (fz - (i << 8) - (16 << 8)))
							p.posz = fz - (i << 8);
						else {
							p.on_ground = false;
							p.poszv += (currentGame.getCON().gc + 80); // (TICSPERFRAME<<6);
							if (p.poszv >= (4096 + 2048))
								p.poszv = (4096 + 2048);
							if (p.poszv > 2400 && p.falling_counter < 255) {
								p.falling_counter++;
								if (p.falling_counter == 38)
									p.scream_voice = spritesound(DUKE_SCREAM, pi);
							}

							if ((p.posz + p.poszv) >= (fz - (i << 8))) // hit the ground
								if (sector[p.cursectnum].lotag != 1) {
									if (p.falling_counter > 62)
										quickkill(p);

									else if (p.falling_counter > 9) {
										j = p.falling_counter;
										s.extra -= j - (engine.krand() & 3);
										if (s.extra <= 0) {
											spritesound(SQUISHED, pi);
											p.pals[0] = 63;
											p.pals[1] = 0;
											p.pals[2] = 0;
											p.pals_time = 63;
										} else {
											spritesound(DUKE_LAND, pi);
											spritesound(DUKE_LAND_HURT, pi);
										}

										p.pals[0] = 16;
										p.pals[1] = 0;
										p.pals[2] = 0;
										p.pals_time = 32;
									} else if (p.poszv > 2048)
										spritesound(DUKE_LAND, pi);
								}
						}
					} else {
						p.falling_counter = 0;
						if (p.scream_voice != null) {
							p.scream_voice.dispose();
							p.scream_voice = null;
						}

						if (psectlotag != 1 && psectlotag != 2 && !p.on_ground && p.poszv > (6144 >> 1))
							p.hard_landing = (short) (p.poszv >> 10);

						p.on_ground = true;

						if (i == 40) {
							// Smooth on the ground
							k = ((fz - (i << 8)) - p.posz) >> 1;
							if (klabs(k) < 256)
								k = 0;
							p.posz += k;
							p.poszv -= 768;
							if (p.poszv < 0)
								p.poszv = 0;
						} else if (p.jumping_counter == 0) {
							p.posz += ((fz - (i << 7)) - p.posz) >> 1; // Smooth on the water
							if (p.on_warping_sector == 0 && p.posz > fz - (16 << 8)) {
								p.posz = fz - (16 << 8);
								p.poszv >>= 1;
							}
						}

						p.on_warping_sector = 0;

						if ((sb_snum & 2) != 0) {
							p.posz += (2048 + 768);
							p.crack_time = 777;
						}

						if ((sb_snum & 1) == 0 && p.jumping_toggle == 1)
							p.jumping_toggle = 0;
						else if ((sb_snum & 1) != 0 && p.jumping_toggle == 0) {
							if (p.jumping_counter == 0)
								if ((fz - cz) > (56 << 8)) {
									p.jumping_counter = 1;
									p.jumping_toggle = 1;
								}
						}

						if (p.jumping_counter != 0 && (sb_snum & 1) == 0)
							p.jumping_toggle = 0;
					}

					if (p.jumping_counter != 0) {
						p.crouch_toggle = 0;
						if ((sb_snum & 1) == 0 && p.jumping_toggle == 1)
							p.jumping_toggle = 0;

						if (p.jumping_counter < (1024 + 256)) {
							if (psectlotag == 1 && p.jumping_counter > 768) {
								p.jumping_counter = 0;
								p.poszv = -512;
							} else {
								p.poszv -= (sintable[(2048 - 128 + p.jumping_counter) & 2047]) / 12;
								p.jumping_counter += 180;
								p.on_ground = false;
							}
						} else {
							p.jumping_counter = 0;
							p.poszv = 0;
						}
					}

					p.posz += p.poszv;

					if (p.posz < (cz + (4 << 8))) {
						p.jumping_counter = 0;
						if (p.poszv < 0)
							p.posxv = p.posyv = 0;
						p.poszv = 128;
						p.posz = cz + (4 << 8);
					}
				}

				// Do the quick lefts and rights
				if (p.fist_incs != 0 || p.transporter_hold > 2 || p.hard_landing != 0 || p.access_incs > 0
						|| p.knee_incs > 0
						|| (p.curr_weapon == TRIPBOMB_WEAPON && p.kickback_pic > 1 && p.kickback_pic < 4)) {
					doubvel = 0;
					p.posxv = 0;
					p.posyv = 0;
				} else if (sync[snum].avel != 0) // ENGINE calculates angvel for you
				{
					if (IsOriginalDemo()) {
						int tempang = ((int) sync[snum].avel) << 1;
						if (psectlotag == 2)
							p.angvel = (tempang - (tempang >> 3)) * ksgn(doubvel);
						else
							p.angvel = tempang * ksgn(doubvel);

						p.ang += p.angvel;
						p.ang = (int) p.ang & 2047;
					} else {
						float tempang = (sync[snum].avel * 2f);
						if (psectlotag == 2)
							p.angvel = ((tempang - (tempang / 8.0f)) * sgn(doubvel));
						else
							p.angvel = (tempang * sgn(doubvel));

						p.ang += p.angvel;
						p.ang = BClampAngle(p.ang);
					}

					p.crack_time = 777;
				}

				if (p.spritebridge == 0 && isValidSector(s.sectnum)) {
					j = sector[s.sectnum].floorpicnum;

					if (j == PURPLELAVA || sector[s.sectnum].ceilingpicnum == PURPLELAVA) {
						if (p.boot_amount > 0) {
							p.boot_amount--;
							p.inven_icon = 7;
							if (p.boot_amount <= 0)
								checkavailinven(p);
						} else {
							if (Sound[DUKE_LONGTERM_PAIN].num < 1)
								spritesound(DUKE_LONGTERM_PAIN, pi);
							p.pals[0] = 0;
							p.pals[1] = 8;
							p.pals[2] = 0;
							p.pals_time = 32;
							s.extra--;
						}
					}

					k = 0;

					if (p.on_ground && truefdist <= PHEIGHT + (16 << 8)) {
						switch (j) {
						case HURTRAIL:
							if (rnd(32)) {
								if (p.boot_amount > 0)
									k = 1;
								else {
									if (Sound[DUKE_LONGTERM_PAIN].num < 1)
										spritesound(DUKE_LONGTERM_PAIN, pi);
									p.pals[0] = 64;
									p.pals[1] = 64;
									p.pals[2] = 64;
									p.pals_time = 32;
									s.extra -= 1 + (engine.krand() & 3);
									if (Sound[SHORT_CIRCUIT].num < 1)
										spritesound(SHORT_CIRCUIT, pi);
								}
							}
							break;
						case FLOORSLIME:
							if (rnd(16)) {
								if (p.boot_amount > 0)
									k = 1;
								else {
									if (Sound[DUKE_LONGTERM_PAIN].num < 1)
										spritesound(DUKE_LONGTERM_PAIN, pi);
									p.pals[0] = 0;
									p.pals[1] = 8;
									p.pals[2] = 0;
									p.pals_time = 32;
									s.extra -= 1 + (engine.krand() & 3);
								}
							}
							break;
						case FLOORPLASMA:
							if (rnd(32)) {
								if (p.boot_amount > 0)
									k = 1;
								else {
									if (Sound[DUKE_LONGTERM_PAIN].num < 1)
										spritesound(DUKE_LONGTERM_PAIN, pi);
									p.pals[0] = 8;
									p.pals[1] = 0;
									p.pals[2] = 0;
									p.pals_time = 32;
									s.extra -= 1 + (engine.krand() & 3);
								}
							}
							break;
						}
					}

					if (k != 0) {
						FTA(75, p);
						p.boot_amount -= 2;
						if (p.boot_amount <= 0)
							checkavailinven(p);
					}
				}

				if (p.posxv != 0 || p.posyv != 0 || sync[snum].fvel != 0 || sync[snum].svel != 0) {
					p.crack_time = 777;

					k = sintable[p.bobcounter & 2047] >> 12;

					if (truefdist < PHEIGHT + (8 << 8))
						if (k == 1 || k == 3) {
							if (p.spritebridge == 0 && p.walking_snd_toggle == 0 && p.on_ground) {
								switch (psectlotag) {
								case 0:

									if (lz >= 0 && (lz & kHitTypeMask) == kHitSprite)
										j = sprite[lz & kHitIndexMask].picnum;
									else
										j = sector[psect].floorpicnum;

									switch (j) {
									case PANNEL1:
									case PANNEL2:
										spritesound(DUKE_WALKINDUCTS, pi);
										p.walking_snd_toggle = 1;
										break;
									}
									break;
								case 1:
									if ((engine.krand() & 1) == 0)
										spritesound(DUKE_ONWATER, pi);
									p.walking_snd_toggle = 1;
									break;
								}
							}
						} else if (p.walking_snd_toggle > 0)
							p.walking_snd_toggle--;

					if (p.jetpack_on == 0 && p.steroids_amount > 0 && p.steroids_amount < 400)
						doubvel <<= 1;

					p.posxv += ((sync[snum].fvel * doubvel) << 6);
					p.posyv += ((sync[snum].svel * doubvel) << 6);

					if ((p.curr_weapon == KNEE_WEAPON && p.kickback_pic > 10 && p.on_ground)
							|| (p.on_ground && (sb_snum & 2) != 0)) {
						p.posxv = mulscale(p.posxv, currentGame.getCON().dukefriction - 0x2000, 16);
						p.posyv = mulscale(p.posyv, currentGame.getCON().dukefriction - 0x2000, 16);
					} else {
						if (psectlotag == 2) {
							p.posxv = mulscale(p.posxv, currentGame.getCON().dukefriction - 0x1400, 16);
							p.posyv = mulscale(p.posyv, currentGame.getCON().dukefriction - 0x1400, 16);
						} else {
							p.posxv = mulscale(p.posxv, currentGame.getCON().dukefriction, 16);
							p.posyv = mulscale(p.posyv, currentGame.getCON().dukefriction, 16);
						}
					}

					if (abs(p.posxv) < 2048 && abs(p.posyv) < 2048)
						p.posxv = p.posyv = 0;

					if (shrunk) {
						p.posxv = mulscale(p.posxv, currentGame.getCON().dukefriction
								- (currentGame.getCON().dukefriction >> 1) + (currentGame.getCON().dukefriction >> 2),
								16);
						p.posyv = mulscale(p.posyv, currentGame.getCON().dukefriction
								- (currentGame.getCON().dukefriction >> 1) + (currentGame.getCON().dukefriction >> 2),
								16);
					}
				}
			}

			if (psectlotag == 1 || p.spritebridge == 1)
				i = (4 << 8);
			else
				i = (20 << 8);

			if (sector[p.cursectnum].lotag == 2)
				k = 0;
			else
				k = 1;

			if (ud.clipping) {
				j = 0;
				p.posx += p.posxv >> 14;
				p.posy += p.posyv >> 14;
				short sect = engine.updatesector(p.posx, p.posy, p.cursectnum);
				if (sect != -1)
					p.cursectnum = sect;
				engine.changespritesect(pi, p.cursectnum);
			} else {

				j = engine.clipmove(p.posx, p.posy, p.posz, p.cursectnum, p.posxv, p.posyv, 164, (4 << 8), i,
						CLIPMASK0);

				if (clipmove_sectnum != -1) {
					p.posx = clipmove_x;
					p.posy = clipmove_y;
					p.posz = clipmove_z;
					p.cursectnum = clipmove_sectnum;
				}
			}

			if (p.jetpack_on == 0 && psectlotag != 2 && psectlotag != 1 && shrunk)
				p.posz += 32 << 8;

			if (j != 0)
				checkplayerhurt(p, j);

			if (p.jetpack_on == 0) {
				if (s.xvel > 16) {
					if (psectlotag != 1 && psectlotag != 2 && p.on_ground) {
						p.pycount += 52;
						p.pycount &= 2047;
						p.pyoff = klabs(s.xvel * sintable[p.pycount]) / 1596;
					}
				} else if (psectlotag != 2 && psectlotag != 1)
					p.pyoff = 0;
			}

			// RBG***
			engine.setsprite(pi, p.posx, p.posy, p.posz + PHEIGHT);

			if (psectlotag < 3) {
				psect = s.sectnum;
				if (!ud.clipping && isValidSector(psect) && sector[psect].lotag == 31) {
					if (sprite[sector[psect].hitag].xvel != 0 && hittype[sector[psect].hitag].temp_data[0] == 0) {
						quickkill(p);
						return;
					}
				}
			}

			if (truefdist < PHEIGHT && p.on_ground && psectlotag != 1 && !shrunk && sector[p.cursectnum].lotag == 1)
				if (Sound[DUKE_ONWATER].num == 0)
					spritesound(DUKE_ONWATER, pi);

			if (p.cursectnum != s.sectnum && isValidSector(p.cursectnum))
				engine.changespritesect(pi, p.cursectnum);

			if (!ud.clipping) {
				j = (engine.pushmove(p.posx, p.posy, p.posz, p.cursectnum, 164, (4 << 8), (4 << 8), CLIPMASK0) < 0
						&& furthestangle(pi, 8) < 512) ? 1 : 0;
				if (pushmove_sectnum != -1) {
					p.posx = pushmove_x;
					p.posy = pushmove_y;
					p.posz = pushmove_z;
					p.cursectnum = pushmove_sectnum;
				}
			} else
				j = 0;

			if (!ud.clipping) {
				if (klabs(hittype[pi].floorz - hittype[pi].ceilingz) < (48 << 8) || j != 0) {
					if (isValidSector(s.sectnum) && (sector[s.sectnum].lotag & 0x8000) == 0
							&& (isanunderoperator(sector[s.sectnum].lotag) || isanearoperator(sector[s.sectnum].lotag)))
						activatebysector(s.sectnum, pi);
					if (j != 0) {
						quickkill(p);
						return;
					}
				} else if (klabs(fz - cz) < (32 << 8) && isanunderoperator(sector[psect].lotag))
					activatebysector(psect, pi);
			}

			if ((sb_snum & (1 << 18)) != 0 || p.hard_landing != 0)
				p.return_to_center = 9;

			if ((sb_snum & (1 << 13)) != 0) {
				p.return_to_center = 9;
				if ((sb_snum & (1 << 5)) != 0)
					p.horiz += 12;
				p.horiz += 12;
			}

			else if ((sb_snum & (1 << 14)) != 0) {
				p.return_to_center = 9;
				if ((sb_snum & (1 << 5)) != 0)
					p.horiz -= 12;
				p.horiz -= 12;
			}

			else if ((sb_snum & (1 << 3)) != 0) {
				if ((sb_snum & (1 << 5)) != 0)
					p.horiz += 6;
				p.horiz += 6;
			}

			else if ((sb_snum & (1 << 4)) != 0) {
				if ((sb_snum & (1 << 5)) != 0)
					p.horiz -= 6;
				p.horiz -= 6;
			}
			if (p.return_to_center > 0)
				if ((sb_snum & (1 << 13)) == 0 && (sb_snum & (1 << 14)) == 0) {
					p.return_to_center--;
					if (IsOriginalDemo())
						p.horiz += 33 - ((int) p.horiz / 3);
					else
						p.horiz += 33 - (p.horiz / 3);
				}

			if (p.hard_landing > 0) {
				p.hard_landing--;
				p.horiz -= (p.hard_landing << 4);
			}

			if (p.aim_mode != 0) {
				if (IsOriginalDemo())
					p.horiz += (int) sync[snum].horz >> 1;
				else
					p.horiz += sync[snum].horz / 2;
			} else {
				if (p.horiz > 95 && p.horiz < 105)
					p.horiz = 100;
				if (p.horizoff > -5 && p.horizoff < 5)
					p.horizoff = 0;
			}

			if (p.horiz > 299)
				p.horiz = 299;
			else if (p.horiz < -99)
				p.horiz = -99;

			// Shooting code/changes

			if (p.show_empty_weapon > 0) {
				p.show_empty_weapon--;
				if (p.show_empty_weapon == 0) {
					if (p.last_full_weapon == EXPANDER_WEAPON)
						p.subweapon |= (1 << EXPANDER_WEAPON);
					else if (p.last_full_weapon == SHRINKER_WEAPON)
						p.subweapon &= ~(1 << EXPANDER_WEAPON);
					addweapon(p, p.last_full_weapon);
					return;
				}
			}

			if (p.knee_incs > 0) {
				p.knee_incs++;
				p.horiz -= 48;
				p.return_to_center = 9;
				if (p.knee_incs > 15) {
					p.knee_incs = 0;
					p.holster_weapon = 0;
					if (p.weapon_pos < 0)
						p.weapon_pos = (short) -p.weapon_pos;
					if (p.actorsqu >= 0 && dist(sprite[pi], sprite[p.actorsqu]) < 1400) {
						guts(sprite[p.actorsqu], JIBS6, 7, myconnectindex);
						spawn(p.actorsqu, BLOODPOOL);
						spritesound(SQUISHED, p.actorsqu);
						switch (sprite[p.actorsqu].picnum) {
						case FEM1:
						case FEM2:
						case FEM3:
						case FEM4:
						case FEM5:
						case FEM6:
						case FEM7:
						case FEM8:
						case FEM9:
						case FEM10:
						case PODFEM1:
						case NAKED1:
						case STATUE:
							if (sprite[p.actorsqu].yvel != 0)
								operaterespawns(sprite[p.actorsqu].yvel);
							break;
						}

						if (sprite[p.actorsqu].picnum == APLAYER) {
							quickkill(ps[sprite[p.actorsqu].yvel]);
							ps[sprite[p.actorsqu].yvel].frag_ps = (short) snum;
						} else if (badguy(sprite[p.actorsqu])) {
							engine.deletesprite(p.actorsqu);
							ps[connecthead].actors_killed++;
						} else
							engine.deletesprite(p.actorsqu);
					}
					p.actorsqu = -1;
				} else if (p.actorsqu >= 0)
					p.ang += getincangle(p.ang,
							engine.getangle(sprite[p.actorsqu].x - p.posx, sprite[p.actorsqu].y - p.posy)) / 4.0f;
			}

			if (doincrements(p))
				return;

			if (p.weapon_pos != 0) {
				if (p.weapon_pos == -9) {
					if (p.last_weapon >= 0) {
						p.weapon_pos = 10;
						p.last_weapon = -1;
					} else if (p.holster_weapon == 0)
						p.weapon_pos = 10;
				} else
					p.weapon_pos--;
			}
		}

		if (!shrunk)
			weaponprocess(snum);
	}

	// UPDATE THIS FILE OVER THE OLD GETSPRITESCORE/COMPUTERGETINPUT FUNCTIONS
	public static int getspritescore(int snum, int dapicnum) {
		switch (dapicnum) {
		case FIRSTGUNSPRITE:
			return (5);
		case CHAINGUNSPRITE:
			return (50);
		case RPGSPRITE:
			return (200);
		case FREEZESPRITE:
			return (25);
		case SHRINKERSPRITE:
			return (80);
		case HEAVYHBOMB:
			return (60);
		case TRIPBOMBSPRITE:
			return (50);
		case SHOTGUNSPRITE:
			return (120);
		case DEVISTATORSPRITE:
			return (120);

		case FREEZEAMMO:
			if (ps[snum].ammo_amount[FREEZE_WEAPON] < currentGame.getCON().max_ammo_amount[FREEZE_WEAPON])
				return (10);
			else
				return (0);
			// Twentieth Anniversary World Tour
		case FLAMETHROWERAMMO:
			if (ps[snum].ammo_amount[INCINERATOR_WEAPON] < currentGame.getCON().max_ammo_amount[INCINERATOR_WEAPON])
				return (10);
			else
				return (0);
		case AMMO:
			if (ps[snum].ammo_amount[SHOTGUN_WEAPON] < currentGame.getCON().max_ammo_amount[SHOTGUN_WEAPON])
				return (10);
			else
				return (0);
		case BATTERYAMMO:
			if (ps[snum].ammo_amount[CHAINGUN_WEAPON] < currentGame.getCON().max_ammo_amount[CHAINGUN_WEAPON])
				return (20);
			else
				return (0);
		case DEVISTATORAMMO:
			if (ps[snum].ammo_amount[DEVASTATOR_WEAPON] < currentGame.getCON().max_ammo_amount[DEVASTATOR_WEAPON])
				return (25);
			else
				return (0);
		case RPGAMMO:
			if (ps[snum].ammo_amount[RPG_WEAPON] < currentGame.getCON().max_ammo_amount[RPG_WEAPON])
				return (50);
			else
				return (0);
		case CRYSTALAMMO:
			if (ps[snum].ammo_amount[SHRINKER_WEAPON] < currentGame.getCON().max_ammo_amount[SHRINKER_WEAPON])
				return (10);
			else
				return (0);
		case HBOMBAMMO:
			if (ps[snum].ammo_amount[HANDBOMB_WEAPON] < currentGame.getCON().max_ammo_amount[HANDBOMB_WEAPON])
				return (30);
			else
				return (0);
		case SHOTGUNAMMO:
			if (ps[snum].ammo_amount[SHOTGUN_WEAPON] < currentGame.getCON().max_ammo_amount[SHOTGUN_WEAPON])
				return (25);
			else
				return (0);

		case COLA:
			if (sprite[ps[snum].i].extra < 100)
				return (10);
			else
				return (0);
		case SIXPAK:
			if (sprite[ps[snum].i].extra < 100)
				return (30);
			else
				return (0);
		case FIRSTAID:
			if (ps[snum].firstaid_amount < 100)
				return (100);
			else
				return (0);
		case SHIELD:
			if (ps[snum].shield_amount < 100)
				return (50);
			else
				return (0);
		case STEROIDS:
			if (ps[snum].steroids_amount < 400)
				return (30);
			else
				return (0);
		case AIRTANK:
			if (ps[snum].scuba_amount < 6400)
				return (30);
			else
				return (0);
		case JETPACK:
			if (ps[snum].jetpack_amount < 1600)
				return (100);
			else
				return (0);
		case HEATSENSOR:
			if (ps[snum].heat_amount < 1200)
				return (10);
			else
				return (0);
		case ACCESSCARD:
			return (1);
		case BOOTS:
			if (ps[snum].boot_amount < 200)
				return (50);
			else
				return (0);
		case ATOMICHEALTH:
			if (sprite[ps[snum].i].extra < currentGame.getCON().max_player_health)
				return (50);
			else
				return (0);
		case HOLODUKE:
			if (ps[snum].holoduke_amount < 2400)
				return (30);
			else
				return (0);
		}
		return (0);
	}

	public static final int[][] fdmatrix = {
			// KNEE PIST SHOT CHAIN RPG PIPE SHRI DEVI WALL FREE HAND EXPA
			{ 128, -1, -1, -1, 128, -1, -1, -1, 128, -1, 128, -1, -1 }, // KNEE
			{ 1024, 1024, 1024, 1024, 2560, 128, 2560, 2560, 1024, 2560, 2560, 2560, -1 }, // PIST
			{ 512, 512, 512, 512, 2560, 128, 2560, 2560, 1024, 2560, 2560, 2560, -1 }, // SHOT
			{ 512, 512, 512, 512, 2560, 128, 2560, 2560, 1024, 2560, 2560, 2560, -1 }, // CHAIN
			{ 2560, 2560, 2560, 2560, 2560, 2560, 2560, 2560, 2560, 2560, 2560, 2560, -1 }, // RPG
			{ 512, 512, 512, 512, 2048, 512, 2560, 2560, 512, 2560, 2560, 2560, -1 }, // PIPE
			{ 128, 128, 128, 128, 2560, 128, 2560, 2560, 128, 128, 128, 128, -1 }, // SHRI
			{ 1536, 1536, 1536, 1536, 2560, 1536, 1536, 1536, 1536, 1536, 1536, 1536, -1 }, // DEVI
			{ -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 }, // WALL
			{ 128, 128, 128, 128, 2560, 128, 2560, 2560, 128, 128, 128, 128, -1 }, // FREE
			{ 2560, 2560, 2560, 2560, 2560, 2560, 2560, 2560, 2560, 2560, 2560, 2560, -1 }, // HAND
			{ 128, 128, 128, 128, 2560, 128, 2560, 2560, 128, 128, 128, 128, -1 }, // EXPA
			{ -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 }, };

	public static int[] goalx = new int[MAXPLAYERS], goaly = new int[MAXPLAYERS], goalz = new int[MAXPLAYERS];
	public static int[] goalsect = new int[MAXPLAYERS], goalwall = new int[MAXPLAYERS],
			goalsprite = new int[MAXPLAYERS];
	public static int[] goalplayer = new int[MAXPLAYERS], clipmovecount = new int[MAXPLAYERS];
	public static short[] searchsect = new short[MAXSECTORS], searchparent = new short[MAXSECTORS];
	public static byte[] dashow2dsector = new byte[(MAXSECTORS + 7) >> 3];

	public static void computergetinput(int snum, Input syn) {
		int i, j, k, l, x1, y1, z1, x2, y2, z2, x3, y3, z3, dx, dy;
		int dist, daang, zang, fightdist, damyang;
		int startsect, endsect, splc, send, startwall, endwall;
		short damysect, dasect;

		WALL wal;

		PlayerStruct p = ps[snum];
		syn.fvel = 0;
		syn.svel = 0;
		syn.avel = 0;
		syn.horz = 0;
		syn.bits = 0;

		x1 = sprite[p.i].x;
		y1 = sprite[p.i].y;
		z1 = sprite[p.i].z;
		damyang = sprite[p.i].ang;
		damysect = sprite[p.i].sectnum;
		if ((numplayers >= 2) && (snum == myconnectindex)) {
			x1 = game.net.predict.x;
			y1 = game.net.predict.y;
			z1 = game.net.predict.z + PHEIGHT;
			damyang = (short) game.net.predict.ang;
			damysect = game.net.predict.sectnum;
		}

		if ((numframes & 7) == 0) {
			x2 = sprite[ps[goalplayer[snum]].i].x;
			y2 = sprite[ps[goalplayer[snum]].i].y;
			z2 = sprite[ps[goalplayer[snum]].i].z;

			if (!engine.cansee(x1, y1, z1 - (48 << 8), damysect, x2, y2, z2 - (48 << 8),
					sprite[ps[goalplayer[snum]].i].sectnum))
				goalplayer[snum] = snum;
		}

		if ((goalplayer[snum] == snum) || (ps[goalplayer[snum]].dead_flag != 0)) {
			j = 0x7fffffff;
			for (i = connecthead; i >= 0; i = connectpoint2[i])
				if (i != snum) {
					dist = engine.ksqrt((sprite[ps[i].i].x - x1) * (sprite[ps[i].i].x - x1)
							+ (sprite[ps[i].i].y - y1) * (sprite[ps[i].i].y - y1));

					x2 = sprite[ps[i].i].x;
					y2 = sprite[ps[i].i].y;
					z2 = sprite[ps[i].i].z;
					if (!engine.cansee(x1, y1, z1 - (48 << 8), damysect, x2, y2, z2 - (48 << 8),
							sprite[ps[i].i].sectnum))
						dist <<= 1;

					if (dist < j) {
						j = dist;
						goalplayer[snum] = i;
					}
				}
		}

		x2 = sprite[ps[goalplayer[snum]].i].x;
		y2 = sprite[ps[goalplayer[snum]].i].y;
		z2 = sprite[ps[goalplayer[snum]].i].z;

		if (p.dead_flag != 0)
			syn.bits |= (1 << 29);
		if ((p.firstaid_amount > 0) && (p.last_extra < 100))
			syn.bits |= (1 << 16);

		for (j = headspritestat[4]; j >= 0; j = nextspritestat[j]) {
			switch (sprite[j].picnum) {
			case TONGUE:
				k = 4;
				break;
			case FREEZEBLAST:
				k = 4;
				break;
			case SHRINKSPARK:
				k = 16;
				break;
			case RPG:
				k = 16;
				break;
			default:
				k = 0;
				break;
			}
			if (k != 0) {
				x3 = sprite[j].x;
				y3 = sprite[j].y;
				z3 = sprite[j].z;
				for (l = 0; l <= 8; l++) {
					if (tmulscale(x3 - x1, x3 - x1, y3 - y1, y3 - y1, (z3 - z1) >> 4, (z3 - z1) >> 4, 11) < 3072) {
						dx = sintable[(sprite[j].ang + 512) & 2047];
						dy = sintable[sprite[j].ang & 2047];
						if ((x1 - x3) * dy > (y1 - y3) * dx)
							i = -k * 512;
						else
							i = k * 512;
						syn.fvel -= mulscale(dy, i, 17);
						syn.svel += mulscale(dx, i, 17);
					}
					if (l < 7) {
						x3 += (mulscale(sprite[j].xvel, sintable[(sprite[j].ang + 512) & 2047], 14) << 2);
						y3 += (mulscale(sprite[j].xvel, sintable[sprite[j].ang & 2047], 14) << 2);
						z3 += (sprite[j].zvel << 2);
					} else {
						engine.hitscan(sprite[j].x, sprite[j].y, sprite[j].z, sprite[j].sectnum,
								mulscale(sprite[j].xvel, sintable[(sprite[j].ang + 512) & 2047], 14),
								mulscale(sprite[j].xvel, sintable[sprite[j].ang & 2047], 14), sprite[j].zvel, pHitInfo,
								CLIPMASK1);

						dasect = pHitInfo.hitsect;
						x3 = pHitInfo.hitx;
						y3 = pHitInfo.hity;
						z3 = pHitInfo.hitz;
					}
				}
			}
		}

		if ((ps[goalplayer[snum]].dead_flag == 0)
				&& ((engine.cansee(x1, y1, z1, damysect, x2, y2, z2, sprite[ps[goalplayer[snum]].i].sectnum))
						|| (engine.cansee(x1, y1, z1 - (24 << 8), damysect, x2, y2, z2 - (24 << 8),
								sprite[ps[goalplayer[snum]].i].sectnum))
						|| (engine.cansee(x1, y1, z1 - (48 << 8), damysect, x2, y2, z2 - (48 << 8),
								sprite[ps[goalplayer[snum]].i].sectnum)))) {
			syn.bits |= (1 << 2);

			if ((p.curr_weapon == HANDBOMB_WEAPON) && ((engine.rand() & 7)) == 0)
				syn.bits &= ~(1 << 2);

			if (p.curr_weapon == TRIPBOMB_WEAPON)
				syn.bits |= ((engine.rand() % MAX_WEAPONS) << 8);

			if (p.curr_weapon == RPG_WEAPON) {
				engine.hitscan(x1, y1, z1 - PHEIGHT, damysect, sintable[(damyang + 512) & 2047],
						sintable[damyang & 2047], (int) (100 - p.horiz - p.horizoff) * 32, pHitInfo, CLIPMASK1);

				dasect = pHitInfo.hitsect;
				x3 = pHitInfo.hitx;
				y3 = pHitInfo.hity;
				z3 = pHitInfo.hitz;

				if ((x3 - x1) * (x3 - x1) + (y3 - y1) * (y3 - y1) < 2560 * 2560)
					syn.bits &= ~(1 << 2);
			}

			fightdist = fdmatrix[p.curr_weapon][ps[goalplayer[snum]].curr_weapon];
			if (fightdist < 128)
				fightdist = 128;
			dist = engine.ksqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
			if (dist == 0)
				dist = 1;
			daang = engine.getangle(x2 + (ps[goalplayer[snum]].posxv >> 14) - x1,
					y2 + (ps[goalplayer[snum]].posyv >> 14) - y1);
			zang = 100 - ((z2 - z1) * 8) / dist;
			fightdist = max(fightdist, (klabs(z2 - z1) >> 4));

			if (sprite[ps[goalplayer[snum]].i].yrepeat < 32) {
				fightdist = 0;
				syn.bits &= ~(1 << 2);
			}
			if (sprite[ps[goalplayer[snum]].i].pal == 1) {
				fightdist = 0;
				syn.bits &= ~(1 << 2);
			}

			if (dist < 256)
				syn.bits |= (1 << 22);

			x3 = x2 + ((x1 - x2) * fightdist / dist);
			y3 = y2 + ((y1 - y2) * fightdist / dist);
			syn.fvel += (x3 - x1) * 2047 / dist;
			syn.svel += (y3 - y1) * 2047 / dist;

			// Strafe attack
			if (fightdist != 0) {
				j = totalclock + snum * 13468;
				i = sintable[(j << 6) & 2047];
				i += sintable[((j + 4245) << 5) & 2047];
				i += sintable[((j + 6745) << 4) & 2047];
				i += sintable[((j + 15685) << 3) & 2047];
				dx = sintable[(sprite[ps[goalplayer[snum]].i].ang + 512) & 2047];
				dy = sintable[sprite[ps[goalplayer[snum]].i].ang & 2047];
				if ((x1 - x2) * dy > (y1 - y2) * dx)
					i += 8192;
				else
					i -= 8192;
				syn.fvel += ((sintable[(daang + 1024) & 2047] * i) >> 17);
				syn.svel += ((sintable[(daang + 512) & 2047] * i) >> 17);
			}

			syn.avel = (short) min(max((((daang + 1024 - damyang) & 2047) - 1024) >> 1, -127), 127);
			syn.horz = min(max((zang - p.horiz) / 2.0f, -MAXHORIZ), MAXHORIZ);
			syn.bits |= (1 << 23);
			return;
		}

		goalsect[snum] = -1;
		if (goalsect[snum] < 0) {
			goalwall[snum] = -1;
			startsect = sprite[p.i].sectnum;
			endsect = sprite[ps[goalplayer[snum]].i].sectnum;

			Arrays.fill(dashow2dsector, (byte) 0);

			searchsect[0] = (short) startsect;
			searchparent[0] = -1;
			dashow2dsector[startsect >> 3] |= (1 << (startsect & 7));
			for (splc = 0, send = 1; splc < send; splc++) {
				startwall = sector[searchsect[splc]].wallptr;
				endwall = startwall + sector[searchsect[splc]].wallnum;
				for (i = startwall; i < endwall; i++) {
					wal = wall[i];
					j = wal.nextsector;
					if (j < 0)
						continue;

					dx = ((wall[wal.point2].x + wal.x) >> 1);
					dy = ((wall[wal.point2].y + wal.y) >> 1);
					if ((engine.getceilzofslope((short) j, dx, dy) > engine.getflorzofslope((short) j, dx, dy)
							- (28 << 8)) && ((sector[j].lotag < 15) || (sector[j].lotag > 22)))
						continue;
					if (engine.getflorzofslope((short) j, dx, dy) < engine.getflorzofslope(searchsect[splc], dx, dy)
							- (72 << 8))
						continue;
					if ((dashow2dsector[j >> 3] & (1 << (j & 7))) == 0) {
						dashow2dsector[j >> 3] |= (1 << (j & 7));
						searchsect[send] = (short) j;
						searchparent[send] = (short) splc;
						send++;
						if (j == endsect) {
							Arrays.fill(dashow2dsector, (byte) 0);
							for (k = send - 1; k >= 0; k = searchparent[k])
								dashow2dsector[searchsect[k] >> 3] |= (1 << (searchsect[k] & 7));

							for (k = send - 1; k >= 0; k = searchparent[k])
								if (searchparent[k] == 0)
									break;

							goalsect[snum] = searchsect[k];
							startwall = sector[goalsect[snum]].wallptr;
							endwall = startwall + sector[goalsect[snum]].wallnum;
							x3 = y3 = 0;
							for (i = startwall; i < endwall; i++) {
								x3 += wall[i].x;
								y3 += wall[i].y;
							}
							x3 /= (endwall - startwall);
							y3 /= (endwall - startwall);

							startwall = sector[startsect].wallptr;
							endwall = startwall + sector[startsect].wallnum;
							l = 0;
							k = startwall;
							for (i = startwall; i < endwall; i++) {
								if (wall[i].nextsector != goalsect[snum])
									continue;
								dx = wall[wall[i].point2].x - wall[i].x;
								dy = wall[wall[i].point2].y - wall[i].y;

								// if (dx*(y1-wall[i].y) <= dy*(x1-wall[i].x))
								// if (dx*(y2-wall[i].y) >= dy*(x2-wall[i].x))
								if ((x3 - x1) * (wall[i].y - y1) <= (y3 - y1) * (wall[i].x - x1))
									if ((x3 - x1) * (wall[wall[i].point2].y - y1) >= (y3 - y1)
											* (wall[wall[i].point2].x - x1)) {
										k = i;
										break;
									}

								dist = engine.ksqrt(dx * dx + dy * dy);
								if (dist > l) {
									l = dist;
									k = i;
								}
							}
							goalwall[snum] = k;
							daang = ((engine.getangle(wall[wall[k].point2].x - wall[k].x,
									wall[wall[k].point2].y - wall[k].y) + 1536) & 2047);
							goalx[snum] = ((wall[k].x + wall[wall[k].point2].x) >> 1)
									+ (sintable[(daang + 512) & 2047] >> 8);
							goaly[snum] = ((wall[k].y + wall[wall[k].point2].y) >> 1) + (sintable[daang & 2047] >> 8);
							goalz[snum] = sector[goalsect[snum]].floorz - (32 << 8);
							break;
						}
					}
				}

				for (i = headspritesect[searchsect[splc]]; i >= 0; i = nextspritesect[i])
					if (sprite[i].lotag == 7) {
						j = sprite[sprite[i].owner].sectnum;
						if ((dashow2dsector[j >> 3] & (1 << (j & 7))) == 0) {
							dashow2dsector[j >> 3] |= (1 << (j & 7));
							searchsect[send] = (short) j;
							searchparent[send] = (short) splc;
							send++;
							if (j == endsect) {
								Arrays.fill(dashow2dsector, (byte) 0);
								for (k = send - 1; k >= 0; k = searchparent[k])
									dashow2dsector[searchsect[k] >> 3] |= (1 << (searchsect[k] & 7));

								for (k = send - 1; k >= 0; k = searchparent[k])
									if (searchparent[k] == 0)
										break;

								goalsect[snum] = searchsect[k];
								startwall = sector[startsect].wallptr;
								endwall = startwall + sector[startsect].wallnum;
								l = 0;
								k = startwall;
								for (i = startwall; i < endwall; i++) {
									dx = wall[wall[i].point2].x - wall[i].x;
									dy = wall[wall[i].point2].y - wall[i].y;
									dist = engine.ksqrt(dx * dx + dy * dy);
									if ((wall[i].nextsector == goalsect[snum]) && (dist > l)) {
										l = dist;
										k = i;
									}
								}
								goalwall[snum] = k;
								daang = ((engine.getangle(wall[wall[k].point2].x - wall[k].x,
										wall[wall[k].point2].y - wall[k].y) + 1536) & 2047);
								goalx[snum] = ((wall[k].x + wall[wall[k].point2].x) >> 1)
										+ (sintable[(daang + 512) & 2047] >> 8);
								goaly[snum] = ((wall[k].y + wall[wall[k].point2].y) >> 1)
										+ (sintable[daang & 2047] >> 8);
								goalz[snum] = sector[goalsect[snum]].floorz - (32 << 8);
								break;
							}
						}
					}
				if (goalwall[snum] >= 0)
					break;
			}
		}

		if ((goalsect[snum] < 0) || (goalwall[snum] < 0)) {
			if (goalsprite[snum] < 0) {
				for (k = 0; k < 4; k++) {
					i = (engine.rand() % numsectors);
					for (j = headspritesect[i]; j >= 0; j = nextspritesect[j]) {
						if ((sprite[j].xrepeat <= 0) || (sprite[j].yrepeat <= 0))
							continue;
						if (getspritescore(snum, sprite[j].picnum) <= 0)
							continue;
						if (engine.cansee(x1, y1, z1 - (32 << 8), damysect, sprite[j].x, sprite[j].y,
								sprite[j].z - (4 << 8), (short) i)) {
							goalx[snum] = sprite[j].x;
							goaly[snum] = sprite[j].y;
							goalz[snum] = sprite[j].z;
							goalsprite[snum] = j;
							break;
						}
					}
				}
			}
			x2 = goalx[snum];
			y2 = goaly[snum];
			dist = engine.ksqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
			if (dist == 0)
				return;
			daang = engine.getangle(x2 - x1, y2 - y1);
			syn.fvel += (x2 - x1) * 2047 / dist;
			syn.svel += (y2 - y1) * 2047 / dist;
			syn.avel = (byte) min(max((((daang + 1024 - damyang) & 2047) - 1024) >> 3, -127), 127);
		} else
			goalsprite[snum] = -1;

		x3 = p.posx;
		y3 = p.posy;
		z3 = p.posz;
		dasect = p.cursectnum;
		i = engine.clipmove(x3, y3, z3, dasect, p.posxv, p.posyv, 164, 4 << 8, 4 << 8, CLIPMASK0);

		if (clipmove_sectnum != -1) {
			dasect = clipmove_sectnum;
			x3 = clipmove_x;
			y3 = clipmove_y;
			z3 = clipmove_z;
		}

		if (i == 0) {
			x3 = p.posx;
			y3 = p.posy;
			z3 = p.posz + (24 << 8);
			dasect = p.cursectnum;
			i = engine.clipmove(x3, y3, z3, dasect, p.posxv, p.posyv, 164, 4 << 8, 4 << 8, CLIPMASK0);

			if (clipmove_sectnum != -1) {
				dasect = clipmove_sectnum;
				x3 = clipmove_x;
				y3 = clipmove_y;
				z3 = clipmove_z;
			}
		}
		if (i != 0) {
			clipmovecount[snum]++;

			j = 0;
			if ((i & kHitTypeMask) == kHitWall) {
				int nwall = i & kHitIndexMask;
				if (wall[nwall].nextsector >= 0) {
					if (engine.getflorzofslope(wall[nwall].nextsector, p.posx, p.posy) <= p.posz + (24 << 8))
						j |= 1;
					if (engine.getceilzofslope(wall[nwall].nextsector, p.posx, p.posy) >= p.posz - (24 << 8))
						j |= 2;
				}
			}
			if ((i & kHitTypeMask) == kHitSprite)
				j = 1;
			if ((j & 1) != 0)
				if (clipmovecount[snum] == 4)
					syn.bits |= (1);
			if ((j & 2) != 0)
				syn.bits |= (1 << 1);

			// Strafe attack
			daang = engine.getangle(x2 - x1, y2 - y1);
			if ((i & kHitTypeMask) == kHitWall) {
				int nwall = i & kHitIndexMask;
				daang = engine.getangle(wall[wall[nwall].point2].x - wall[nwall].x,
						wall[wall[nwall].point2].y - wall[nwall].y);
			}
			j = totalclock + snum * 13468;
			i = sintable[(j << 6) & 2047];
			i += sintable[((j + 4245) << 5) & 2047];
			i += sintable[((j + 6745) << 4) & 2047];
			i += sintable[((j + 15685) << 3) & 2047];
			syn.fvel += ((sintable[(daang + 1024) & 2047] * i) >> 17);
			syn.svel += ((sintable[(daang + 512) & 2047] * i) >> 17);

			if ((clipmovecount[snum] & 31) == 2)
				syn.bits |= (1 << 29);
			if ((clipmovecount[snum] & 31) == 17)
				syn.bits |= (1 << 22);
			if (clipmovecount[snum] > 32) {
				goalsect[snum] = -1;
				goalwall[snum] = -1;
				clipmovecount[snum] = 0;
			}

			goalsprite[snum] = -1;
		} else
			clipmovecount[snum] = 0;

		if ((goalsect[snum] >= 0) && (goalwall[snum] >= 0)) {
			x2 = goalx[snum];
			y2 = goaly[snum];
			dist = engine.ksqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
			if (dist == 0)
				return;
			daang = engine.getangle(x2 - x1, y2 - y1);
			if ((goalwall[snum] >= 0) && (dist < 4096))
				daang = ((engine.getangle(wall[wall[goalwall[snum]].point2].x - wall[goalwall[snum]].x,
						wall[wall[goalwall[snum]].point2].y - wall[goalwall[snum]].y) + 1536) & 2047);
			syn.fvel += (x2 - x1) * 2047 / dist;
			syn.svel += (y2 - y1) * 2047 / dist;
			syn.avel = (byte) min(max((((daang + 1024 - damyang) & 2047) - 1024) >> 3, -127), 127);
		}
	}
}
