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

package ru.m210projects.Redneck;

import static java.lang.Math.*;
import static ru.m210projects.Redneck.Factory.RRInput.*;
import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Build.Pragmas.*;
import static ru.m210projects.Build.Strhandler.buildString;
import static ru.m210projects.Redneck.Main.*;
import static ru.m210projects.Redneck.Gamedef.getincangle;
import static ru.m210projects.Redneck.Gameutils.sgn;
import static ru.m210projects.Redneck.Premap.*;
import static ru.m210projects.Redneck.Gameutils.FindDistance2D;
import static ru.m210projects.Build.Gameutils.*;
import static ru.m210projects.Build.Net.Mmulti.*;
import static ru.m210projects.Redneck.Spawn.*;
import static ru.m210projects.Redneck.Animate.*;
import static ru.m210projects.Redneck.Types.ANIMATION.*;
import static ru.m210projects.Redneck.Globals.*;
import static ru.m210projects.Redneck.Sector.*;
import static ru.m210projects.Redneck.Gameutils.*;
import static ru.m210projects.Redneck.Gamedef.*;
import static ru.m210projects.Redneck.Actors.*;
import static ru.m210projects.Redneck.Screen.*;
import static ru.m210projects.Redneck.Names.*;
import static ru.m210projects.Redneck.Sounds.*;
import static ru.m210projects.Redneck.View.*;
import static ru.m210projects.Redneck.SoundDefs.*;
import static ru.m210projects.Redneck.Weapons.*;

import java.util.Arrays;

import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Build.Types.WALL;
import ru.m210projects.Redneck.Types.PlayerStruct;

public class Player {

	public static final byte[] waterpal = new byte[768], slimepal = new byte[768], titlepal = new byte[768],
			drealms = new byte[768], endingpal = new byte[768], drugpal = new byte[768];

	public static void InitPlayers() {
		ps[myconnectindex].reset();

		ps[myconnectindex].palette = palette;
		ps[myconnectindex].aim_mode = cfg.gMouseAim ? 1 : 0;
		ps[myconnectindex].auto_aim = cfg.gAutoAim ? 1 : 0;

		for (int i = 1; i < MAXPLAYERS; i++)
			ps[i].copy(ps[myconnectindex]);

		game.net.getnames();
	}

	public static void setpal(PlayerStruct p) {
		if (p.DrugMode != 0)
			p.palette = drugpal;
		else if (p.heat_on != 0)
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

	public static void getMoto(PlayerStruct p, short i) {
		if (!p.OnMotorcycle) {
			if (i != -1) {
				p.posx = sprite[i].x;
				p.posy = sprite[i].y;
				p.ang = sprite[i].ang;
				p.ammo_amount[MOTO_WEAPON] = sprite[i].owner;
				p.UpdatePlayerLoc();
				engine.deletesprite(i);
			}
			over_shoulder_on = 0;
			p.OnMotorcycle = true;
			p.last_full_weapon = p.curr_weapon;
			p.curr_weapon = MOTO_WEAPON;
			p.gotweapon[MOTO_WEAPON] = true;
			p.posxv = 0;
			p.posyv = 0;
			p.horiz = 100;
		}
		if (Sound[186].num == 0)
			spritesound(186, p.i);
	}

	public static void leaveMoto(PlayerStruct p) {
		if (p.OnMotorcycle) {
			if (Sound[188].num > 0)
				stopsound(Sound[188].num, p.i);
			if (Sound[187].num > 0)
				stopsound(Sound[187].num, p.i);
			if (Sound[186].num > 0)
				stopsound(Sound[186].num, p.i);
			if (Sound[214].num > 0)
				stopsound(Sound[214].num, p.i);
			if (Sound[42].num == 0)
				spritesound(42, p.i);

			p.OnMotorcycle = false;
			p.gotweapon[MOTO_WEAPON] = false;
			p.curr_weapon = p.last_full_weapon;
			checkavailweapon(p);
			p.horiz = 100;
			p.CarVar6 = 0;
			p.CarSpeed = 0;
			p.TiltStatus = 0;
			p.CarVar2 = 0;
			p.VBumpTarget = 0;
			p.VBumpNow = 0;
			p.TurbCount = 0;
			p.posxv = 0;
			p.posyv = 0;
			p.posxv -= sintable[((short) p.ang + 512) & kAngleMask] << 7;
			p.posyv -= sintable[((short) p.ang) & kAngleMask] << 7;
			int j = spawn(p.i, MOTORCYCLE);
			sprite[j].ang = (short) p.ang;
			sprite[j].xvel += sintable[((short) p.ang + 512) & kAngleMask] << 7;
			sprite[j].yvel += sintable[((short) p.ang) & kAngleMask] << 7;
			sprite[j].owner = (short) p.ammo_amount[MOTO_WEAPON];
		}
	}

	public static void getBoard(PlayerStruct p, short i) {
		if (!p.OnBoat) {
			if (i != -1) {
				p.posx = sprite[i].x;
				p.posy = sprite[i].y;
				p.ang = sprite[i].ang;
				p.ammo_amount[BOAT_WEAPON] = sprite[i].owner;
				p.UpdatePlayerLoc();
				engine.deletesprite(i);
			}
			over_shoulder_on = 0;
			p.OnBoat = true;
			p.last_full_weapon = p.curr_weapon;
			p.curr_weapon = BOAT_WEAPON;
			p.gotweapon[BOAT_WEAPON] = true;
			p.posxv = 0;
			p.posyv = 0;
			p.horiz = 100;
		}
		if (Sound[186].num == 0)
			spritesound(186, p.i);
	}

	public static void leaveBoard(PlayerStruct p) {
		if (p.OnBoat) {
			p.OnBoat = false;
			p.gotweapon[BOAT_WEAPON] = false;
			p.curr_weapon = p.last_full_weapon;
			checkavailweapon(p);
			p.horiz = 100;
			p.CarVar6 = 0;
			p.CarSpeed = 0;
			p.TiltStatus = 0;
			p.CarVar2 = 0;
			p.VBumpTarget = 0;
			p.VBumpNow = 0;
			p.TurbCount = 0;
			p.posxv = 0;
			p.posyv = 0;
			p.posxv -= sintable[((short) p.ang + 512) & kAngleMask] << 7;
			p.posyv -= sintable[((short) p.ang) & kAngleMask] << 7;
			int j = spawn(p.i, SWAMPBUGGY);
			sprite[j].ang = (short) p.ang;
			sprite[j].xvel += sintable[((short) p.ang + 512) & kAngleMask] << 7;
			sprite[j].yvel += sintable[((short) p.ang) & kAngleMask] << 7;
			sprite[j].owner = (short) p.ammo_amount[BOAT_WEAPON];
		}
	}

	public static void incur_damage(PlayerStruct p) {
		int damage = 0, shield_damage = 0;

		sprite[p.i].extra -= p.extra_extra8 >> 8;

		damage = sprite[p.i].extra - p.last_extra;

		if (damage < 0) {
			p.extra_extra8 = 0;

			if (p.moonshine_amount > 0 && p.moonshine_amount < 400)
				damage -= damage * (20 + (engine.krand() % 30)) / 100;
			if (p.alcohol_amount > 31 && p.alcohol_amount < 65)
				shield_damage++;
			if (p.gut_amount > 31 && p.gut_amount < 65)
				shield_damage++;

			if (shield_damage == 1)
				damage *= 0.75;
			if (shield_damage == 2)
				damage *= 0.25;

			sprite[p.i].extra = (short) (p.last_extra + damage);
		}
	}

	public static void checkavailinven(PlayerStruct p) {

		if (p.whishkey_amount > 0)
			p.inven_icon = 1;
		else if (p.moonshine_amount > 0)
			p.inven_icon = 2;
		else if (p.beer_amount > 0)
			p.inven_icon = 3;
		else if (p.cowpie_amount > 0)
			p.inven_icon = 4;
		else if (p.yeehaa_amount > 0)
			p.inven_icon = 5;
		else if (p.snorkle_amount > 0)
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

	public static boolean inventory(SPRITE s) {
		switch (s.picnum) {
		case WHISKEY:
		case MOONSHINE:
		case SNORKLE:
		case COWPIE:
		case 59: // empty
		case BOOTS:
		case 1350: // 6 банок
			return true;
		}
		return false;
	}

	public static boolean doincrements(PlayerStruct p) {
		int snum = sprite[p.i].yvel;

		p.player_par++;
		checkTrack();

		if (currentGame.getCON().type == RRRA) {
			if (WindTime <= 0) {
				if ((engine.krand() & 0x7F) == 8) {
					WindTime = 4 * (engine.krand() & 0x3F) + 120;
					WindDir = engine.krand() & kAngleMask;
				}
			} else
				WindTime--;
		}

		if (BellTime > 0 && --BellTime == 0)
			sprite[BellTime >> 16].picnum++;

		if (p.chiken_phase > 0)
			p.chiken_phase--;

		if (p.SeaSick != 0) {
			if (--p.SeaSick == 0)
				p.isSea = false;
		}

		if (p.field_count != 0)
			p.field_count--;

		if (p.detonate_count > 0) {
			p.detonate_count++;
			p.field_57C--;
		}

		if (p.DrugMode > 0 && !MODE_TYPE) {
			if (p.drug_type != 0) {
				if (p.drug_type == 3)
					p.drug_intensive--;
				else if (p.drug_type == 2) {
					if (p.drug_timer <= 30)
						p.drug_timer++;
				} else if (p.drug_timer >= 1)
					p.drug_timer--;
			} else p.drug_intensive++;

        	if ( p.drug_type != 0)
        	{
        		if ( p.drug_type == 3)
        		{
        			int new_aspect = 5000 * p.drug_intensive + oviewingrange;
        			if ( new_aspect >= oviewingrange )
        			{
        				p.drug_aspect = new_aspect;
        			}
        			else
        			{
        				p.DrugMode = 0;
        				p.drug_type = 0;
        				p.drug_timer = 0;
        				p.drug_intensive = 0;
        				setpal(p);
        			}
        		}
        		else if ( p.drug_type == 2 )
                {
        			if ( p.drug_timer <= 30 )
        			{
        				p.drug_aspect = 3 * oviewingrange + 500 * p.drug_timer;
        			}
        			else p.drug_type = 1;
                } 
        		else if ( p.drug_timer >= 1 )
                {
        			p.drug_aspect = 3 * oviewingrange + 500 * p.drug_timer;
                }
        		else
        		{
        			p.drug_type = 2;
        			if ( --p.DrugMode == 1 )
        				p.drug_type = 3;
        		}
        	} 
        	else 
        	{
        		int new_aspect = 5000 * p.drug_intensive + oviewingrange;
        		if ( 3 * oviewingrange >= new_aspect )
        		{
        			p.drug_aspect = new_aspect;
                }
                else
                {
                	p.drug_aspect = 3 * oviewingrange;
                	p.drug_type = 2;
                }
        	}
		}

		p.alcohol_count--;
		if (p.alcohol_count <= 0) {
			p.alcohol_count = 1024;
			if (p.alcohol_amount != 0) {
				p.drunk = 0;
				p.alcohol_amount--;
			}
		}

		p.alcohol_meter = (short) ((8 * p.alcohol_amount + 1647) & 2047);
		if (p.alcohol_amount >= 100) {
			p.alcohol_amount = 100;
			p.alcohol_meter = 400;
		}

		p.gut_count--;
		if (p.gut_count <= 0) {
			p.gut_count = 1024;
			if (p.gut_amount != 0)
				p.gut_amount--;
		}

		if (p.alcohol_amount == 100) {
			p.drunk = 1;
			if (Sound[420].num == 0)
				spritesound(420, p.i);

			p.alcohol_amount -= 9;
			p.gut_amount >>= 1;
		}

		p.gut_meter = (short) ((8 * p.gut_amount + 1647) & 2047);

		if (p.gut_amount >= 100)
			p.gut_amount = 100;

		if (p.gut_amount >= 31) {
			if (engine.krand() < p.alcohol_amount) {
				switch (engine.krand() & 3) {
				case 0:
					spritesound(404, p.i);
					break;
				case 1:
					spritesound(422, p.i);
					break;
				case 2:
					spritesound(423, p.i);
					break;
				case 3:
					spritesound(424, p.i);
					break;
				}
				if (numplayers < 2) {
					p.field_290 = 0x4000;
					sub_64EF0(snum);
					p.posxv += mulscale(16, sintable[((int) p.ang + 512) & kAngleMask], 16);
					p.posyv += mulscale(16, sintable[(int) p.ang & kAngleMask], 16);
				}
				p.gut_amount -= 4;
				if (p.gut_amount < 0)
					p.gut_amount = 0;
			}
		}

		if (p.curr_weapon == CHICKENBOW_WEAPON) // GDX 15.10.2018
			p.chiken_pic = (byte) (engine.krand() & 15);

		if (p.invdisptime > 0)
			p.invdisptime--;

		if (p.tipincs > 0)
			p.tipincs--;

		if (p.last_pissed_time > 0) {
			p.last_pissed_time--;

			if (p.alcohol_amount > 66 && ((p.last_pissed_time) % 26) == 0)
				p.alcohol_amount--;
			if (ud.lockout == 0) {
				switch (p.last_pissed_time) {
				case 5662:
				case 5567:
				case 5014:
					spritesound(434, p.i);
					break;
				case 5072:
					spritesound(435, p.i);
					break;
				case 4919:
				case 5472:
					spritesound(433, p.i);
					break;
				}
			}

			if (p.last_pissed_time == (5668)) {
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

		if (p.moonshine_amount > 0 && p.moonshine_amount < 400) {
			p.moonshine_amount--;
			if (p.moonshine_amount == 0) {
				checkavailinven(p);
				p.gut_amount = p.alcohol_amount = p.moonshine_amount;
				p.gut_meter = p.alcohol_meter = 1647;
			}
			if ((p.moonshine_amount & 14) == 0)
				if (snum == screenpeek || ud.coop == 1)
					spritesound(217, p.i);
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
						p.gotkey[1] = 1;
						break;
					case 21:
						p.gotkey[2] = 1;
						break;
					case 23:
						p.gotkey[3] = 1;
						break;
					}
					p.access_spritenum = -1;
				} else {
					checkhitswitch(snum, p.access_wallnum, 0);
					switch (wall[p.access_wallnum].pal) {
					case 0:
						p.gotkey[1] = 1;
						break;
					case 21:
						p.gotkey[2] = 1;
						break;
					case 23:
						p.gotkey[3] = 1;
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
			if (p.snorkle_amount > 0) {
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
		} else if (p.snorkle_amount > 0 && p.scuba_on != 0) {
			p.snorkle_amount--;
			if (p.snorkle_amount == 0) {
				p.scuba_on = 0;
				checkavailinven(p);
			}
		}

		if (p.knuckle_incs != 0) {
			p.knuckle_incs++;
			if (p.knuckle_incs == 9) {
				if (LeonardCrack != 0) {
					if (totalclock > 1024)
						if (snum == screenpeek || ud.coop == 1) {
							if ((engine.rand() & 1) != 0)
								spritesound(DUKE_CRACK, p.i);
							else
								spritesound(DUKE_CRACK2, p.i);
						}

					return true;
				}

				int snd = -1;
				LeonardCrack = 1;

				if (ud.volume_number == 1) {
					switch (ud.level_number) {
					case 0:
						snd = 105;
						break;
					case 1:
						snd = 176;
						break;
					case 2:
						snd = 177;
						break;
					case 3:
						snd = 198;
						break;
					case 4:
						snd = 230;
						break;
					case 5:
						snd = 255;
						break;
					case 6:
						snd = 283;
						break;
					case 7:
						snd = 391;
						break; // EndGame
					}
				}

				if (ud.volume_number == 0) {
					switch (ud.level_number) {
					case 0:
						if (currentGame.getCON().type == RRRA)
							snd = 63;
						else
							snd = 391;
						break;
					case 1:
						snd = 64;
						break;
					case 2:
						snd = 77;
						break;
					case 3:
						snd = 80;
						break;
					case 4:
						snd = 102;
						break;
					case 5:
						snd = 103;
						break;
					case 6:
						snd = 104;
						break;
					}
				}

				if (snd == -1)
					snd = 391;
				spritesound(snd, p.i);
			} else if (p.knuckle_incs == 21 || (sync[snum].bits & (1 << 2)) != 0)
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
		if(psect == -1)
	    {
	        if(s.extra > 0 && !ud.clipping)
	        {
	            quickkill(p);
	            spritesound(SQUISHED,pi);
	        }
	        psect = 0;
	    }

		if(!isValidSector(s.sectnum) || !isValidSector(psect))
			return;

		p.ohoriz = p.horiz;
		p.ohorizoff = p.horizoff;
		
		 p.look_ang += sync[snum].carang;

		if (p.OnMotorcycle) {
			boolean left = (sb_snum & 16) != 0;
			boolean right = (sb_snum & 64) != 0;
			if (p.CarSpeed != 0 && p.on_ground) {
				if (left || p.CarVar2 < 0) {
					p.TiltStatus = BClipLow(p.TiltStatus - 1, -10);
				} else if (right || p.CarVar2 > 0) {
					p.TiltStatus = BClipHigh(p.TiltStatus + 1, 10);
				} else {
					if (p.TiltStatus < 0)
						p.TiltStatus++;
					if (p.TiltStatus > 0)
						p.TiltStatus--;
				}
			} else if (left)
				p.TiltStatus = BClipLow(p.TiltStatus - 1, -10);
			else if (right)
				p.TiltStatus = BClipHigh(p.TiltStatus + 1, 10);

			if (p.CarVar1 != 0)
				p.CarSpeed = 0;
		}

		if (p.OnBoat) {
			boolean left = (sb_snum & 16) != 0;
			boolean right = (sb_snum & 64) != 0;
			if (p.CarSpeed != 0) {
				if (left || p.CarVar2 < 0) {
					if (p.NotOnWater == 0)
						p.TiltStatus = BClipLow(p.TiltStatus - 1, -10);
				} else if (right || p.CarVar2 > 0) {
					if (p.NotOnWater == 0)
						p.TiltStatus = BClipHigh(p.TiltStatus + 1, 10);
				} else if (p.NotOnWater == 0) {
					if (p.TiltStatus < 0)
						p.TiltStatus++;
					if (p.TiltStatus > 0)
						p.TiltStatus--;
				}
			} else if (p.NotOnWater == 0) {
				if (left)
					p.TiltStatus = BClipLow(p.TiltStatus - 1, -10);
				else if (right)
					p.TiltStatus = BClipHigh(p.TiltStatus + 1, 10);
			}
		}

		if (p.OnMotorcycle && s.extra > 0) {
			if (p.CarSpeed < 0)
				p.CarSpeed = 0;
			int var = 0, var2 = 0;
			if ((sb_snum & 2) != 0) {
				var = 1;
				sb_snum &= ~2;
			}

			if ((sb_snum & 1) != 0) {
				var2 = 1;
				sb_snum &= ~1;
				if (p.on_ground) {
					if (p.CarSpeed == 0 && var != 0) {
						if (Sound[187].num == 0)
							spritesound(187, pi);
					} else if (p.CarSpeed != 0 || Sound[214].num != 0) {
						if (p.CarSpeed < 50 || Sound[188].num != 0) {
							if (Sound[188].num == 0 && Sound[214].num == 0)
								spritesound(188, pi);
						} else {
							spritesound(188, pi);
						}
					} else {
						if (Sound[187].num > 0)
							stopsound(Sound[187].num, pi);
						spritesound(214, pi);
					}
				}
			} else {
				var2 = 0;
				if (Sound[214].num > 0) {
					stopsound(Sound[214].num, pi);
					if (Sound[189].num == 0)
						spritesound(189, pi);
				}
				if (Sound[188].num > 0) {
					stopsound(Sound[188].num, pi);
					if (Sound[189].num == 0)
						spritesound(189, pi);
				}
				if (Sound[189].num == 0 && Sound[187].num == 0)
					spritesound(187, pi);
			}
			int var3 = 0, var4 = 0, var5 = 0;
			if ((sb_snum & 8) != 0) {
				var3 = 1;
				sb_snum &= ~8;
			}

			if ((sb_snum & 16) != 0) {
				var4 = 1;
				sb_snum &= ~16;
			}

			if ((sb_snum & 64) != 0) {
				var5 = 1;
				sb_snum &= ~64;
			}

			if (p.alcohol_amount <= 88 || p.CarVar2 != 0) {
				if (p.alcohol_amount > 99 && p.CarVar2 == 0) {
					int ch = (engine.krand() & 0x1F);
					if (ch == 1) {
						p.CarVar2 = -20;
					} else if (ch == 2) {
						p.CarVar2 = 20;
					}
				}
			} else {
				int ch = (engine.krand() & 0x3F);
				if (ch == 1) {
					p.CarVar2 = -10;
				} else if (ch == 2) {
					p.CarVar2 = 10;
				}
			}

			if (p.on_ground) {
				if (var != 0 && p.CarSpeed > 0) {
					if (p.CarVar4 != 0)
						p.CarSpeed -= 2;
					else
						p.CarSpeed -= 4;
					if (p.CarSpeed < 0)
						p.CarSpeed = 0;
					p.VBumpTarget = -30;
					p.CarVar6 = 1;
				} else if (var2 != 0 && var == 0) {
					if (p.CarSpeed < 40) {
						p.VBumpTarget = 70;
						p.CarVar3 = 1;
					}
					p.CarSpeed += 2;
					if (p.CarSpeed > 120)
						p.CarSpeed = 120;
					if (p.NotOnWater == 0 && p.CarSpeed > 80)
						p.CarSpeed = 80;
				} else if (p.CarSpeed > 0)
					p.CarSpeed--;
				if (p.CarVar6 != 0 && (var == 0 || p.CarSpeed == 0)) {
					p.VBumpTarget = 0;
					p.CarVar6 = 0;
				}
				if (var3 != 0 && p.CarSpeed <= 0 && var == 0) {
					p.CarSpeed = -15;
					int swap = var5;
					var5 = var4;
					var4 = swap;
				}
			}
			if (p.CarSpeed != 0 && p.on_ground) {
				if (p.VBumpNow == 0 && (engine.krand() & 3) == 2)
					p.VBumpTarget = (short) (((engine.krand() & 7) - 4) * (p.CarSpeed >> 4));

				if (var4 != 0 || p.CarVar2 < 0) {
					if (p.CarVar2 < 0)
						++p.CarVar2;
				} else if ((var5 != 0 || p.CarVar2 > 0) && p.CarVar2 > 0)
					p.CarVar2--;
			}

			if (p.TurbCount != 0) {
				if (p.TurbCount > 1) {
					p.horiz = (engine.krand() & 0xF) + 93;
					--p.TurbCount;
					p.CarVar2 = (short) ((engine.krand() & 3) - 2);
				} else {
					p.horiz = 100;
					p.TurbCount = 0;
					p.VBumpTarget = 0;
					p.VBumpNow = 0;
				}
			} else if (p.VBumpTarget <= p.VBumpNow) {
				if (p.VBumpTarget >= p.VBumpNow) {
					p.VBumpTarget = 0;
					p.CarVar3 = 0;
				} else {
					if (p.CarVar3 != 0)
						p.VBumpNow -= 6;
					else
						p.VBumpNow--;
					if (p.VBumpTarget > p.VBumpNow)
						p.VBumpNow = p.VBumpTarget;
					p.horiz = (p.VBumpNow) / 3 + 100;
				}
			} else {
				if (p.CarVar3 != 0)
					p.VBumpNow += 6;
				else
					p.VBumpNow++;
				if (p.VBumpTarget < p.VBumpNow)
					p.VBumpNow = p.VBumpTarget;
				p.horiz = (p.VBumpNow) / 3 + 100;
			}

//	    	if (snum == myconnectindex && numplayers > 1)
//	    		game.net.predict.horiz = p.horiz;

			if (p.CarSpeed >= 20 && p.on_ground && (var4 != 0 || var5 != 0)) {
				int angvel = (int) (p.ang - 510);
				if (var4 != 0)
					angvel = (int) (p.ang + 510);

				short dang = 350;
				if (var4 == 0)
					dang = -350;

				if (p.CarVar5 != 0 || p.CarVar4 != 0 || p.NotOnWater == 0) {
					int speed = 4 * p.CarSpeed;
					if (p.CarVar4 != 0)
						speed = 8 * p.CarSpeed;

					if (p.CarVar6 != 0) {
						p.posxv += (speed >> 5) * 16 * sintable[(angvel + 512) & kAngleMask];
						p.posyv += (speed >> 5) * 16 * sintable[angvel & kAngleMask];
						p.ang = ((short) p.ang - (dang >> 2)) & kAngleMask;
					} else {
						p.posxv += (speed >> 7) * 16 * sintable[(angvel + 512) & kAngleMask];
						p.posyv += (speed >> 7) * 16 * sintable[angvel & kAngleMask];
						p.ang = ((short) p.ang - (dang >> 6)) & kAngleMask;
					}
					p.CarVar5 = 0;
					p.CarVar4 = 0;
				} else if (p.CarVar6 != 0) {
					p.posxv += (p.CarSpeed >> 5) * 16 * sintable[(angvel + 512) & kAngleMask];
					p.posyv += (p.CarSpeed >> 5) * 16 * sintable[angvel & kAngleMask];
					p.ang = ((short) p.ang - (dang >> 4)) & kAngleMask;
					if (Sound[220].num == 0)
						spritesound(220, p.i);
				} else {
					p.posxv += (p.CarSpeed >> 7) * 16 * sintable[(angvel + 512) & kAngleMask];
					p.posyv += (p.CarSpeed >> 7) * 16 * sintable[angvel & kAngleMask];
					p.ang = ((short) p.ang - (dang >> 4)) & kAngleMask;
				}
			} else if (p.CarSpeed >= 20 && p.on_ground && (p.CarVar5 != 0 || p.CarVar4 != 0)) {
				int angvel = (int) (p.ang + 510);
				if ((engine.krand() & 1) == 1)
					angvel = (int) (p.ang - 510);

				int speed = 5 * p.CarSpeed;
				if (p.CarVar4 != 0)
					speed = 10 * p.CarSpeed;

				p.posxv += (speed >> 7) * 16 * sintable[(angvel + 512) & kAngleMask];
				p.posyv += (speed >> 7) * 16 * sintable[angvel & kAngleMask];
			}
			p.CarVar5 = 0;
			p.CarVar4 = 0;
		} else if (p.OnBoat && s.extra > 0) {
			if (p.NotOnWater != 0) {
				if (p.CarSpeed <= 0) {
					if (Sound[87].num == 0)
						spritesound(87, pi);
				} else if (Sound[88].num == 0)
					spritesound(88, pi);
			}
			if (p.CarSpeed < 0)
				p.CarSpeed = 0;
			boolean var1 = false, var2 = false, var3 = false, var4 = false, var5 = false, var6 = false;
			if ((sb_snum & 2) != 0 && (sb_snum & 1) != 0) {
				var1 = true;
				sb_snum &= ~3;
			}

			if ((sb_snum & 1) != 0) {
				var2 = true;
				sb_snum &= ~1;
				if (p.CarSpeed != 0 || Sound[89].num != 0) {
					if (p.CarSpeed < 50 || Sound[88].num != 0) {
						if (Sound[88].num == 0 && Sound[89].num == 0)
							spritesound(88, pi);
					} else
						spritesound(88, pi);
				} else {
					if (Sound[87].num > 0)
						stopsound(Sound[87].num, pi);
					spritesound(89, pi);
				}
			} else {
				var2 = false;
				if (Sound[89].num > 0) {
					stopsound(Sound[89].num, pi);
					if (Sound[90].num == 0)
						spritesound(90, pi);
				}
				if (Sound[88].num > 0) {
					stopsound(Sound[88].num, pi);
					if (Sound[90].num == 0)
						spritesound(90, pi);
				}
				if (Sound[90].num == 0 && Sound[87].num == 0)
					spritesound(87, pi);
			}
			if ((sb_snum & 2) != 0) {
				var3 = true;
				sb_snum &= ~2;
			}

			if ((sb_snum & 8) != 0) {
				var4 = true;
				sb_snum &= ~8;
			}

			if ((sb_snum & 16) != 0) {
				var5 = true;
				sb_snum &= ~16;
				if (Sound[91].num == 0 && p.CarSpeed > 30 && p.NotOnWater == 0)
					spritesound(91, pi);
			}

			if ((sb_snum & 64) != 0) {
				var6 = true;
				sb_snum &= ~64;
				if (Sound[91].num == 0 && p.CarSpeed > 30 && p.NotOnWater == 0)
					spritesound(91, pi);
			}

			if (p.NotOnWater == 0) {
				if (p.alcohol_amount <= 88 || p.CarVar2 != 0) {
					if (p.alcohol_amount > 99 && p.CarVar2 == 0) {
						int ch = engine.krand() & 0x1F;
						if (ch == 1)
							p.CarVar2 = -20;
						else if (ch == 2)
							p.CarVar2 = 20;
					}
				} else {
					int ch = engine.krand() & 0x3F;
					if (ch == 1)
						p.CarVar2 = -10;
					else if (ch == 2)
						p.CarVar2 = 10;
				}
			}
			if (p.on_ground) {
				if (var1) {
					if (p.CarSpeed > 25) {
						p.CarSpeed -= 2;
						if (p.CarSpeed < 0)
							p.CarSpeed = 0;
						p.VBumpTarget = 30;
						p.CarVar6 = 1;
					} else {
						++p.CarSpeed;
						if (Sound[182].num == 0)
							spritesound(182, pi);
					}
				} else if (var3 && p.CarSpeed > 0) {
					p.CarSpeed -= 2;
					if (p.CarSpeed < 0)
						p.CarSpeed = 0;
					p.VBumpTarget = 30;
					p.CarVar6 = 1;
				} else if (var2) {
					if (p.CarSpeed < 40 && p.NotOnWater == 0) {
						p.VBumpTarget = -30;
						p.CarVar3 = 1;
					}
					if (++p.CarSpeed > 120)
						p.CarSpeed = 120;
				} else if (p.CarSpeed > 0)
					--p.CarSpeed;

				if (p.CarVar6 != 0 && (!var3 || p.CarSpeed == 0)) {
					p.VBumpTarget = 0;
					p.CarVar6 = 0;
				}
				if (var4 && p.CarSpeed == 0 && !var3) {
					if (p.NotOnWater != 0)
						p.CarSpeed = -20;
					else
						p.CarSpeed = -25;
					boolean swap = var6;
					var6 = var5;
					var5 = swap;
				}
			}
			if (p.CarSpeed != 0 && p.on_ground) {
				if (p.VBumpNow == 0 && (engine.krand() & 0xF) == 14)
					p.VBumpTarget = (short) (((engine.krand() & 3) - 2) * (p.CarSpeed >> 4));
				if (var5 || p.CarVar2 < 0) {
					if (p.CarVar2 < 0)
						++p.CarVar2;
				} else if ((var6 || p.CarVar2 > 0) && p.CarVar2 > 0)
					--p.CarVar2;

			}
			if (p.TurbCount != 0) {
				if (p.TurbCount > 1) {
					p.horiz = (engine.krand() & 0xF) + 93;
					p.CarVar2 = (short) ((engine.krand() & 3) - 2);
					p.TurbCount--;
				} else {
					p.horiz = 100;
					p.TurbCount = 0;
					p.VBumpTarget = 0;
					p.VBumpNow = 0;
				}
			} else if (p.VBumpTarget <= p.VBumpNow) {
				if (p.VBumpTarget >= p.VBumpNow) {
					p.VBumpTarget = 0;
					p.CarVar3 = 0;
				} else {
					if (p.CarVar3 != 0)
						p.VBumpNow -= 6;
					else
						--p.VBumpNow;
					if (p.VBumpTarget > p.VBumpNow)
						p.VBumpNow = p.VBumpTarget;
					p.horiz = (p.VBumpNow) / 3 + 100;
				}
			} else {
				if (p.CarVar3 != 0)
					p.VBumpNow += 6;
				else
					++p.VBumpNow;
				if (p.VBumpTarget < p.VBumpNow)
					p.VBumpNow = p.VBumpTarget;
				p.horiz = (p.VBumpNow) / 3 + 100;
			}

//	      if (snum == myconnectindex && numplayers > 1)
//	    	  game.net.predict.horiz = p.horiz;

			if (p.CarSpeed > 0 && p.on_ground && (var5 || var6)) {
				int angvel = (int) (p.ang - 510);
				if (var5)
					angvel = (int) (p.ang + 510);
				short dang = 350;
				if (!var5)
					dang = -350;

				int speed = 4 * p.CarSpeed;
				if (p.CarVar6 != 0) {
					p.posxv += (speed >> 6) * 16 * sintable[(angvel + 512) & 0x7FF];
					p.posyv += (speed >> 6) * 16 * sintable[angvel & 0x7FF];
					p.ang = ((short) p.ang - (dang >> 5)) & kAngleMask;
				} else {
					p.posxv += (speed >> 7) * 16 * sintable[(angvel + 512) & 0x7FF];
					p.posyv += (speed >> 7) * 16 * sintable[angvel & 0x7FF];
					p.ang = ((short) p.ang - (dang >> 6)) & kAngleMask;
				}
			}

			if (p.NotOnWater != 0 && p.CarSpeed > 50)
				p.CarSpeed -= p.CarSpeed >> 1;
		}

		if (psect == -1) {
			if (s.extra > 0 && !ud.clipping) {
				quickkill(p);
				spritesound(SQUISHED, pi);
			}
			psect = 0;
		}

		psectlotag = sector[psect].lotag;
		if (psectlotag == 867) {
			for (i = headspritestat[psect]; i >= 0; i = nextspritestat[i]) {
				if (sprite[i].picnum == 380 && sprite[i].z - 2048 < p.posz)
					psectlotag = 2;
			}
		} else if (psectlotag == 7777 && ud.volume_number == 1 && ud.level_number == 6) {
			LeaveMap();
			ud.level_number = 7; // gEndGame
			return;
		}

		if (psectlotag == 848 && sector[psect].floorpicnum == 1045)
			psectlotag = 1;
		if (psectlotag == 857)
			s.clipdist = 1;
		else
			s.clipdist = 64;

		p.spritebridge = 0;

		shrunk = (s.yrepeat < 8);
		
		if (s.clipdist == 64)
			engine.getzrange(p.posx, p.posy, p.posz, psect, 163, CLIPMASK0);
		else
			engine.getzrange(p.posx, p.posy, p.posz, psect, 4, CLIPMASK0);

		cz = zr_ceilz;
		hz = zr_ceilhit;
		fz = zr_florz;
		lz = zr_florhit;

		j = p.truefz = engine.getflorzofslope(psect, p.posx, p.posy);
		p.truecz = engine.getceilzofslope(psect, p.posx, p.posy);

		truefdist = klabs(p.posz - j);
		if ((lz & kHitTypeMask) == kHitSector && psectlotag == 1 && truefdist > PHEIGHT + (16 << 8))
			psectlotag = 0;

		hittype[pi].floorz = fz;
		hittype[pi].ceilingz = cz;

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

			if (sprite[hz].picnum == 3587) {
				if (p.field_280 != 0)
					p.field_280--;
				else {
					p.field_280 = 10;
					if ((sb_snum & 1) != 0 && !p.OnMotorcycle) {
						hz = 0;
						cz = p.truecz;
					}
				}
			}
		}

		if (lz >= 0 && (lz & kHitTypeMask) == kHitSprite) {
			j = lz & kHitIndexMask;

			if ((sprite[j].cstat & 33) == 33) {
				psectlotag = 0;
				p.footprintcount = 0;
				p.spritebridge = 1;
			}

			if (p.OnMotorcycle && badguy(sprite[j])) {
				hittype[j].picnum = 7170;
				hittype[j].extra = (p.CarSpeed >> 1) + 2;
				p.CarSpeed -= p.CarSpeed >> 4;
			}
			if (p.OnBoat) {
				if (badguy(sprite[j])) {
					hittype[j].picnum = 7170;
					hittype[j].extra = (p.CarSpeed >> 1) + 2;
					p.CarSpeed -= p.CarSpeed >> 4;
				}
			} else if (badguy(sprite[j]) && sprite[j].xrepeat > 24 && klabs(s.z - sprite[j].z) < (84 << 8)) {
				j = engine.getangle(sprite[j].x - p.posx, sprite[j].y - p.posy);
				p.posxv -= sintable[(j + 512) & 2047] << 4;
				p.posyv -= sintable[j & 2047] << 4;
			}

			if (sprite[j].picnum == 3587) {
				if (p.field_280 != 0)
					p.field_280--;
				else {
					p.field_280 = 10;
					if ((sb_snum & 2) != 0 && !p.OnMotorcycle) {
						cz = sprite[j].z;
						hz = 0;
						fz = cz + 1024;
					}
				}
			} else if ((sprite[j].picnum == 1098 || sprite[j].picnum == 2121) && (sb_snum & 2) != 0
					&& Sound[436].num == 0 && !p.OnMotorcycle) {
				spritesound(436, p.i);
				p.last_pissed_time = 4000;
				p.gut_amount = 0;
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
			if (p.fist_incs == 27) {
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
					if (ud.secretlevel > 0 && ud.secretlevel < 9)
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
				ud.level_number++;
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

				stopsound(DUKE_JETPACK_IDLE, pi);
				if (p.scream_voice != null) {
					p.scream_voice.dispose();
					p.scream_voice = null;
				}

				if (s.pal != 1 && (s.cstat & 32768) == 0)
					s.cstat = 0;

				if (ud.multimode > 1 && (s.pal != 1 || (s.cstat & 32768) != 0)) {
					if (p.frag_ps != snum) {
						ps[p.frag_ps].frag++;
						frags[p.frag_ps][snum]++;

						if (ud.user_name != null && ud.user_name[p.frag_ps] != null
								&& ud.user_name[p.frag_ps].codePointAt(0) != 0) {
							if (snum == screenpeek) {
								buildString(currentGame.getCON().fta_quotes[115], 0, "KILLED BY ",
										ud.user_name[p.frag_ps]);
								FTA(115, p);
							} else {
								buildString(currentGame.getCON().fta_quotes[116], 0, "KILLED ", ud.user_name[snum]);
								FTA(116, ps[p.frag_ps]);
							}
						} else {
							if (snum == screenpeek) {
								buildString(currentGame.getCON().fta_quotes[115], 0, "KILLED BY PLAYER ",
										1 + p.frag_ps);
								FTA(115, p);
							} else {
								buildString(currentGame.getCON().fta_quotes[116], 0, "KILLED PLAYER ", 1 + snum);
								FTA(116, ps[p.frag_ps]);
							}
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
					p.cursectnum = (short) clipmove_sectnum;
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
				p.cursectnum = (short) pushmove_sectnum;
			}

			if (fz > cz + (16 << 8) && s.pal != 1)
				p.rotscrnang = (short) ((p.dead_flag + ((fz + p.posz) >> 7)) & 2047);

			p.on_warping_sector = 0;

			if (p.OnMotorcycle)
				leaveMoto(p);
			if (p.OnBoat)
				leaveBoard(p);

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

			if ((!p.OnMotorcycle && !p.OnBoat) || p.CarSpeed >= 80)
				p.look_ang -= (p.look_ang >> 2);
			else
				p.look_ang = (short) BClipRange(p.look_ang, -512, 512);

			if ((sb_snum & (1 << 6)) != 0 && !p.OnMotorcycle) {
				p.look_ang -= 152;
				p.rotscrnang += 24;
			}

			if ((sb_snum & (1 << 7)) != 0 && !p.OnMotorcycle) {
				p.look_ang += 152;
				p.rotscrnang -= 24;
			}

			if (p.SeaSick != 0) {
				if (p.SeaSick < 250) {
					if (p.SeaSick >= 130 && p.SeaSick < 180)
						p.rotscrnang -= 24;
					else if (p.SeaSick >= 20)
						p.rotscrnang += 24;
					p.look_ang += (engine.krand() & 255) - 128;
				}
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

				if (psectlotag == 17 || psectlotag == 18) {
					if (getanimationgoal(sector[p.cursectnum], FLOORZ) < 0)
						stopsound(432, pi);
					else if (Sound[432].num == 0)
						spritesound(432, pi);
				}

				if (p.isSea) {
					p.pycount += 32;
					p.pycount &= kAngleMask;

					if (p.SeaSick != 0)
						p.pyoff = sintable[p.pycount] >> 2;
					else
						p.pyoff = sintable[p.pycount] >> 7;
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

					if ((sb_snum & 1) != 0 && !p.OnMotorcycle) {
						if (p.poszv > 0)
							p.poszv = 0;
						p.poszv -= 348;
						if (p.poszv < -(256 * 6))
							p.poszv = -(256 * 6);
					} else if ((sb_snum & (1 << 1)) != 0 && !p.OnMotorcycle) {
						if (p.poszv < 0)
							p.poszv = 0;
						p.poszv += 348;
						if (p.poszv > (256 * 6))
							p.poszv = (256 * 6);
					} else if (p.OnMotorcycle) {
						if (p.poszv < 0)
							p.poszv = 0;
						p.poszv += 348;
						if (p.poszv > 1536)
							p.poszv = 1536;
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
								if (sector[p.cursectnum].floorpicnum == FLOORSLIME) {
									p.footprintpal = 8;
									p.footprintshade = 0;
								} else if (sector[p.cursectnum].floorpicnum != 7756
										&& sector[p.cursectnum].floorpicnum != 7888) {
									p.footprintpal = 0;
									p.footprintshade = 0;
								} else {
									p.footprintpal = 0;
									p.footprintshade = 40;
								}
							}
						}
					} 
					else 
					{
						if (!p.OnMotorcycle && p.footprintcount > 0 && p.on_ground)
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
							if ((p.OnMotorcycle || p.OnBoat) && fz - (i << 9) > p.posz) {
								if (p.CarOnGround) {
									p.VBumpTarget = 80;
									p.CarVar3 = 1;
									p.poszv -= (p.CarSpeed >> 4) * currentGame.getCON().gc;
									p.CarOnGround = false;
									if (Sound[188].num > 0)
										stopsound(Sound[188].num, pi);
									spritesound(189, pi);
								} else {
									p.poszv += 120 - p.CarSpeed + currentGame.getCON().gc - 80;
									if (Sound[189].num == 0 && Sound[190].num == 0)
										spritesound(190, pi);
								}
							} else
								p.poszv += (currentGame.getCON().gc + 80); // (TICSPERFRAME<<6);

							if (p.poszv >= (4096 + 2048))
								p.poszv = (4096 + 2048);
							if (p.poszv > 2400 && p.falling_counter < 255) {
								p.falling_counter++;
								if (p.falling_counter == 37)
									p.scream_voice = spritesound(DUKE_SCREAM, pi);
							}

							if ((p.posz + p.poszv) >= (fz - (i << 8))) // hit the ground
								if (sector[p.cursectnum].lotag != 1) {
									p.CarOnGround = true;
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
									} else {
										if (p.poszv <= 2048) {
											if (p.poszv > 1024 && p.OnMotorcycle) {
												spritesound(DUKE_LAND, pi);
												p.TurbCount = 12;
											}
										} else if (p.OnMotorcycle) {
											if (Sound[190].num > 0)
												stopsound(Sound[190].num, pi);
											spritesound(191, pi);
											p.TurbCount = 12;
										} else
											spritesound(DUKE_LAND, pi);
									}
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

						if ((sb_snum & 2) != 0 && !p.OnMotorcycle) {
							p.posz += (2048 + 768);
							p.crack_time = 777;
						}

						if ((sb_snum & 1) == 0 && p.jumping_toggle == 1 && !p.OnMotorcycle)
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
						if ((sb_snum & 1) == 0 && !p.OnMotorcycle && p.jumping_toggle == 1)
							p.jumping_toggle = 0;

						if (p.jumping_counter < 768) {
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
						|| (p.curr_weapon == POWDERKEG_WEAPON && p.kickback_pic > 1 && p.kickback_pic < 4)) {
					doubvel = 0;
					p.posxv = 0;
					p.posyv = 0;
				} else if (sync[snum].avel != 0) // p.ang += syncangvel * constant
				{ // ENGINE calculates angvel for you
					if (IsOriginalDemo()) {
						int tempang = ((int) sync[snum].avel) << 1;
						if (psectlotag == 2)
							p.angvel = (tempang - (tempang >> 3)) * ksgn(doubvel);
						else
							p.angvel = (int) tempang * ksgn(doubvel);

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

				if (p.spritebridge == 0 && s.sectnum >= 0 && s.sectnum < MAXSECTORS) {
					j = sector[s.sectnum].floorpicnum;

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
						case 7768:
						case 7820:
							if ((engine.krand() & 3) == 1 && p.on_ground) {
								if (p.OnMotorcycle)
									s.extra -= 2;
								else
									s.extra -= 4;
								spritesound(211, pi);
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
					if (p.spritebridge == 0 && p.on_ground) {
						switch (psectlotag) {
						case 1:
							p.NotOnWater = 0;
							break;
						default:
							if (p.OnBoat)
								p.NotOnWater = psectlotag != 1234 ? 1 : 0;
							else
								p.NotOnWater = 1;
							break;
						}
					}

					if (truefdist < PHEIGHT + (8 << 8))
						if (k == 1 || k == 3) {
							if (p.spritebridge == 0 && p.walking_snd_toggle == 0 && p.on_ground) {
								switch (psectlotag) {
								case 1:
									if ((engine.krand() & 1) == 0 && !p.OnBoat && !p.OnMotorcycle)
										spritesound(DUKE_ONWATER, pi);
									p.walking_snd_toggle = 1;
									break;
								}
							}
						} else if (p.walking_snd_toggle > 0)
							p.walking_snd_toggle--;

					if (p.jetpack_on == 0 && p.moonshine_amount > 0 && p.moonshine_amount < 400)
						doubvel <<= 1;

					p.posxv += ((sync[snum].fvel * doubvel) << 6);
					p.posyv += ((sync[snum].svel * doubvel) << 6);

					if (psectlotag == 2) {
						p.posxv = mulscale(p.posxv, currentGame.getCON().dukefriction - 0x1400, 16);
						p.posyv = mulscale(p.posyv, currentGame.getCON().dukefriction - 0x1400, 16);
					} else {
						p.posxv = mulscale(p.posxv, currentGame.getCON().dukefriction, 16);
						p.posyv = mulscale(p.posyv, currentGame.getCON().dukefriction, 16);
					}

					switch (sector[p.cursectnum].floorpicnum) {
					case 7888:
						if (p.OnMotorcycle && p.on_ground)
							p.CarVar4 = 1;
						break;
					case 7889:
						if (p.OnMotorcycle) {
							if (p.on_ground)
								p.CarVar5 = 1;
						} else if (p.boot_amount <= 0) {
							p.posxv = mulscale(p.posxv, currentGame.getCON().dukefriction, 16);
							p.posyv = mulscale(p.posyv, currentGame.getCON().dukefriction, 16);
						} else
							p.boot_amount--;

						break;
					case 3073:
					case 2702:
						if (p.OnMotorcycle) {
							if (p.on_ground) {
								p.posxv = mulscale(p.posxv, currentGame.getCON().dukefriction - 6144, 16);
								p.posyv = mulscale(p.posyv, currentGame.getCON().dukefriction - 6144, 16);
							}
						} else if (p.boot_amount <= 0) {
							p.posxv = mulscale(p.posxv, currentGame.getCON().dukefriction - 6144, 16);
							p.posyv = mulscale(p.posyv, currentGame.getCON().dukefriction - 6144, 16);
						} else
							p.boot_amount--;
						break;
					}

					if (klabs(p.posxv) < 2048 && klabs(p.posyv) < 2048)
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

//	        if(sector[p.cursectnum].lotag == 2) k = 0;
//	        else k = 1;

			if (ud.clipping) {
				j = 0;
				p.posx += p.posxv >> 14;
				p.posy += p.posyv >> 14;
				short sect = engine.updatesector(p.posx, p.posy, p.cursectnum);
				if (sect != -1)
					p.cursectnum = sect;
				engine.changespritesect(pi, p.cursectnum);
			} else {
				j = engine.clipmove(p.posx, p.posy, p.posz, p.cursectnum, p.posxv, p.posyv, 164, (4 << 8), i, CLIPMASK0);

				if (clipmove_sectnum != -1) {
					p.posx = clipmove_x;
					p.posy = clipmove_y;
					p.posz = clipmove_z;
					p.cursectnum = (short) clipmove_sectnum;
				}
			}

			if (p.jetpack_on == 0 && psectlotag != 2 && psectlotag != 1 && shrunk)
				p.posz += 32 << 8;

			if (j != 0)
				checkplayerhurt(p, j);
			else if (p.hurt_delay > 0)
				p.hurt_delay--;

			if ((j & kHitTypeMask) == kHitWall) {
				int nwall = j & kHitIndexMask;
				if (p.OnMotorcycle) {
					int dang = (int) klabs((short) p.ang - engine.getangle(wall[wall[nwall].point2].x - wall[nwall].x,
							wall[wall[nwall].point2].y - wall[nwall].y));
					if ((engine.krand() & 1) == 1)
						p.ang = ((short) p.ang - (p.CarSpeed >> 1)) & kAngleMask;
					else
						p.ang = ((short) p.ang + (p.CarSpeed >> 1)) & kAngleMask;

					int damage = 0;
					if (dang >= 441 && dang <= 581) {
						damage = (p.CarSpeed * p.CarSpeed) >> 8;
						p.CarSpeed = 0;
						if (Sound[238].num == 0)
							spritesound(238, p.i);
					} else if (dang >= 311 && dang <= 711) {
						damage = (p.CarSpeed * p.CarSpeed) >> 11;
						p.CarSpeed -= ((p.CarSpeed >> 2) + (p.CarSpeed >> 1));
						if (Sound[238].num == 0)
							spritesound(238, p.i);
					} else if (dang >= 111 && dang <= 911) {
						damage = (p.CarSpeed * p.CarSpeed) >> 14;
						p.CarSpeed -= (p.CarSpeed >> 1);
						if (Sound[238].num == 0)
							spritesound(238, p.i);
					} else {
						damage = (p.CarSpeed * p.CarSpeed) >> 15;
						p.CarSpeed -= (p.CarSpeed >> 3);
						if (Sound[240].num == 0)
							spritesound(240, p.i);
					}
					s.extra -= damage;
					if (s.extra > 0) {
						if (damage != 0)
							spritesound(200, pi);
					} else {
						spritesound(69, pi);
						p.pals[0] = 63;
						p.pals[1] = 0;
						p.pals[2] = 0;
						p.pals_time = 63;
					}
				} else if (p.OnBoat) {
					int dang = (int) klabs((short) p.ang - engine.getangle(wall[wall[nwall].point2].x - wall[nwall].x,
							wall[wall[nwall].point2].y - wall[nwall].y));
					if ((engine.krand() & 1) == 1)
						p.ang = ((short) p.ang - (p.CarSpeed >> 2)) & kAngleMask;
					else
						p.ang = ((short) p.ang + (p.CarSpeed >> 2)) & kAngleMask;

					if (dang >= 441 && dang <= 581) {
						p.CarSpeed = (short) (((p.CarSpeed >> 1) * (p.CarSpeed >> 2)) >> 2);
						if (psectlotag == 1 && Sound[178].num == 0)
							spritesound(178, p.i);
					} else if (dang >= 311 && dang <= 711) {
						p.CarSpeed -= ((p.CarSpeed >> 1) + (p.CarSpeed >> 2)) >> 3;
						if (psectlotag == 1 && Sound[179].num == 0)
							spritesound(179, p.i);
					} else if (dang >= 111 && dang <= 911) {
						p.CarSpeed -= (p.CarSpeed >> 4);
						if (psectlotag == 1 && Sound[180].num == 0)
							spritesound(180, p.i);
					} else {
						p.CarSpeed -= (p.CarSpeed >> 6);
						if (Sound[181].num == 0)
							spritesound(181, p.i);
					}
				} else if (wall[nwall].lotag >= 40 && wall[nwall].lotag <= 44) {
					if (wall[nwall].lotag < 44)
						pushwall(nwall, p.cursectnum, snum);
					engine.pushmove(p.posx, p.posy, p.posz, p.cursectnum, 172, (4 << 8), (4 << 8), CLIPMASK0);
					if (pushmove_sectnum != -1) { // v0.751
						p.posx = pushmove_x;
						p.posy = pushmove_y;
						p.posz = pushmove_z;
						p.cursectnum = (short) pushmove_sectnum;
					}
				}
			}

			if ((j & kHitTypeMask) == kHitSprite) {
				int nspr = j & kHitIndexMask;
				if (p.OnMotorcycle) {
					if (badguy(sprite[nspr]) || sprite[nspr].picnum == APLAYER) {
						if (sprite[nspr].picnum == APLAYER)
							hittype[nspr].owner = p.i;
						else if (numplayers == 1) {
							movesprite((short) nspr, sintable[((short) p.ang + 20 * p.TiltStatus + 512) & 0x7FF] >> 8,
									sintable[((short) p.ang + 20 * p.TiltStatus) & 0x7FF] >> 8, sprite[nspr].zvel,
									CLIPMASK0);
						}
						hittype[nspr].picnum = 7170;
						hittype[nspr].extra = p.CarSpeed >> 1;
						p.CarSpeed -= p.CarSpeed >> 2;
						p.TurbCount = 6;
					} else if ((sprite[nspr].picnum == 2431 || sprite[nspr].picnum == 2443
							|| sprite[nspr].picnum == 2451 || sprite[nspr].picnum == 2455) && sprite[nspr].picnum != 4
							&& p.CarSpeed > 45) {
						spritesound(69, nspr);
						if (sprite[nspr].picnum != 2431 && sprite[nspr].picnum != 2451)
							guts(sprite[nspr], 2465, 3, myconnectindex);
						else {
							if (sprite[nspr].lotag != 0) {
								for (int l = 0; l < 4096; ++l) {
									if ((sprite[l].picnum == 2431 || sprite[l].picnum == 2451) && sprite[l].pal == 4
											&& sprite[nspr].lotag == sprite[l].lotag) {
										sprite[l].xrepeat = 0;
										sprite[l].yrepeat = 0;
									}
								}
							}
							guts(sprite[nspr], 2460, 12, myconnectindex);
							guts(sprite[nspr], 2465, 3, myconnectindex);
						}
						guts(sprite[nspr], 2465, 3, myconnectindex);
						sprite[nspr].xrepeat = 0;
						sprite[nspr].yrepeat = 0;
					}
				} else if (p.OnBoat) {
					if (badguy(sprite[nspr]) || sprite[nspr].picnum == APLAYER) {
						if (sprite[nspr].picnum == APLAYER)
							hittype[nspr].owner = p.i;
						else if (numplayers == 1) {
							movesprite((short) nspr, sintable[((short) p.ang + 20 * p.TiltStatus + 512) & 0x7FF] >> 9,
									sintable[((short) p.ang + 20 * p.TiltStatus) & 0x7FF] >> 9, sprite[nspr].zvel,
									CLIPMASK0);
						}
						hittype[nspr].picnum = 7170;
						hittype[nspr].extra = p.CarSpeed >> 2;
						p.CarSpeed -= p.CarSpeed >> 2;
						p.TurbCount = 6;
					}
				} else if (badguy(sprite[nspr])) {
					if (sprite[nspr].statnum != 1) {
						hittype[nspr].timetosleep = 0;
						if (sprite[nspr].picnum == BILLYSHOOT)
							spritesound(404, nspr);
						else
							check_fta_sounds(nspr);
						engine.changespritestat((short) nspr, (short) 1);
					}
				} else if (sprite[nspr].picnum == 3410) {
					quickkill(p);
					spritesound(446, pi);
				} else if (sprite[nspr].picnum == 2443 && sprite[nspr].pal == 19) {
					sprite[nspr].pal = 0;
					p.DrugMode = 5;
					sprite[p.i].extra = (short) currentGame.getCON().max_player_health;
					setpal(p);
				}
			}

			if (p.jetpack_on == 0) {
				if (s.xvel > 16) {
					if (psectlotag != 1 && psectlotag != 2 && p.on_ground && !p.isSea) {
						p.pycount += 52;
						p.pycount &= 2047;
						p.pyoff = (int) (klabs(s.xvel * sintable[p.pycount]) / 1596);
					}
				} else if (psectlotag != 2 && psectlotag != 1 && !p.isSea)
					p.pyoff = 0;
			}

			// RBG***
			engine.setsprite(pi, p.posx, p.posy, p.posz + PHEIGHT);
			if (psectlotag == 800) {
				quickkill(p);
				return;
			}

			if (psectlotag < 3) {
				psect = s.sectnum;
				if (!ud.clipping && psect >= 0 && psect < MAXSECTORS && sector[psect].lotag == 31) {
					if (sprite[sector[psect].hitag].xvel != 0 && hittype[sector[psect].hitag].temp_data[0] == 0) {
						quickkill(p);
						return;
					}
				}
			}

			if (truefdist < PHEIGHT && p.on_ground && psectlotag != 1 && !shrunk && sector[p.cursectnum].lotag == 1)
				if (Sound[DUKE_ONWATER].num == 0 && !p.OnBoat && !p.OnMotorcycle && sector[p.cursectnum].hitag != 321)
					spritesound(DUKE_ONWATER, pi);

			if (p.cursectnum != s.sectnum && isValidSector(p.cursectnum))
				engine.changespritesect(pi, p.cursectnum);

			if (!ud.clipping) {

				if (s.clipdist == 64)
					j = (engine.pushmove(p.posx, p.posy, p.posz, p.cursectnum, 128, (4 << 8), (4 << 8), CLIPMASK0) < 0
							&& furthestangle(pi, 8) < 512) ? 1 : 0;
				else
					j = (engine.pushmove(p.posx, p.posy, p.posz, p.cursectnum, 16, (4 << 8), (4 << 8), CLIPMASK0) < 0
							&& furthestangle(pi, 8) < 512) ? 1 : 0;
				if (pushmove_sectnum != -1) {
					p.posx = pushmove_x;
					p.posy = pushmove_y;
					p.posz = pushmove_z;
					p.cursectnum = (short) pushmove_sectnum;
				}
			} else
				j = 0;

			if (!ud.clipping) {
				if (klabs(hittype[pi].floorz - hittype[pi].ceilingz) < (48 << 8) || j != 0) {
					if (isValidSector(s.sectnum) && (sector[s.sectnum].lotag & 0x8000) == 0
							&& (isanunderoperator(sector[s.sectnum].lotag) || isanearoperator(sector[s.sectnum].lotag)))
						activatebysector(s.sectnum, snum);
					if (j != 0) {
						quickkill(p);
						return;
					}
				} else if (klabs(fz - cz) < (32 << 8) && isanunderoperator(sector[psect].lotag))
					activatebysector(psect, snum);

				if (p.cursectnum != -1 && sector[p.cursectnum].floorz - 3072 < sector[p.cursectnum].ceilingz) {
					quickkill(p);
					return;
				}
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

			else if ((sb_snum & (1 << 3)) != 0 && !p.OnMotorcycle) {
				if ((sb_snum & (1 << 5)) != 0)
					p.horiz += 6;
				p.horiz += 6;
			}

			else if ((sb_snum & (1 << 4)) != 0 && !p.OnMotorcycle) {
				if ((sb_snum & (1 << 5)) != 0)
					p.horiz -= 6;
				p.horiz -= 6;
			}

			if (p.kickback == 0 || p.kickback_pic != 0) {
				if (p.return_to_center > 0)
					if ((sb_snum & (1 << 13)) == 0 && (sb_snum & (1 << 14)) == 0) {
						p.return_to_center--;
						p.horiz += 33 - (p.horiz / 3);
					}
			} else {
				int dhoriz = p.kickback >> 1;
				if (dhoriz == 0)
					dhoriz = 1;
				p.kickback -= dhoriz;
				p.horiz -= dhoriz;
			}

			if (p.hard_landing > 0) {
				p.hard_landing--;
				p.horiz -= (p.hard_landing << 4);
			}

			if (p.aim_mode != 0)
				p.horiz += sync[snum].horz / 2;
			else {
				if (p.kickback == 0) {
					if (p.horiz > 95 && p.horiz < 105)
						p.horiz = 100;
					if (p.horizoff > -5 && p.horizoff < 5)
						p.horizoff = 0;
				}
			}

			if (p.horiz > 299)
				p.horiz = 299;
			else if (p.horiz < -99)
				p.horiz = -99;

			// Shooting code/changes

			if (p.show_empty_weapon > 0) {
				p.show_empty_weapon--;
				if (p.show_empty_weapon == 1) {
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
						case 1115:
						case 1168:
						case 5581:
							if (sprite[p.actorsqu].yvel != 0)
								operaterespawns(sprite[p.actorsqu].yvel);
							break;
						}

						if (sprite[p.actorsqu].picnum == APLAYER) {
							quickkill(ps[sprite[p.actorsqu].yvel]);
							ps[sprite[p.actorsqu].yvel].frag_ps = (short) snum;
						} else if (badguy(sprite[p.actorsqu])) {
							engine.deletesprite(p.actorsqu);
							p.actors_killed++;
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

		if (p.detonate_count > 0) {
			if (ud.god) {
				p.field_57C = 45;
				p.detonate_count = 0;
			} else if (p.field_57C <= 0 && p.kickback_pic < 5) {
				sound(14);
				quickkill(p);
			}
		}

		weaponprocess(snum);
	}

	// UPDATE THIS FILE OVER THE OLD GETSPRITESCORE/COMPUTERGETINPUT FUNCTIONS
	public static int getspritescore(int snum, int dapicnum) {
		switch (dapicnum) {
		case FIRSTGUNSPRITE:
			return (5);
		case 22:
			return (50);
		case 23:
			return (200);
		case 24:
			return (25);
		case 25:
			return (80);
		case 26:
			return (60);
		case 27:
			return (50);
		case SHOTGUNSPRITE:
			return (120);
		case 29:
			return (120);

		case 42:
			if (ps[snum].ammo_amount[TIT_WEAPON] < currentGame.getCON().max_ammo_amount[TIT_WEAPON])
				return (25);
			else
				return (0);
		case AMMO:
			if (ps[snum].ammo_amount[SHOTGUN_WEAPON] < currentGame.getCON().max_ammo_amount[SHOTGUN_WEAPON])
				return (10);
			else
				return (0);
		case 41:
			if (ps[snum].ammo_amount[RIFLEGUN_WEAPON] < currentGame.getCON().max_ammo_amount[RIFLEGUN_WEAPON])
				return (20);
			else
				return (0);
		case 37:
			if (ps[snum].ammo_amount[ALIENBLASTER_WEAPON] < currentGame.getCON().max_ammo_amount[ALIENBLASTER_WEAPON])
				return (10);
			else
				return (0);
		case 44:
			if (ps[snum].ammo_amount[DYNAMITE_WEAPON] < currentGame.getCON().max_ammo_amount[DYNAMITE_WEAPON])
				return (50);
			else
				return (0);
		case 46:
			if (ps[snum].ammo_amount[THROWSAW_WEAPON] < currentGame.getCON().max_ammo_amount[THROWSAW_WEAPON])
				return (10);
			else
				return (0);
		case HBOMBAMMO:
			if (ps[snum].ammo_amount[4] < currentGame.getCON().max_ammo_amount[4])
				return (30);
			else
				return (0);
		case SHOTGUNAMMO:
			if (ps[snum].ammo_amount[SHOTGUN_WEAPON] < currentGame.getCON().max_ammo_amount[SHOTGUN_WEAPON])
				return (25);
			else
				return (0);

		case 51:
			if (sprite[ps[snum].i].extra < 100)
				return (10);
			else
				return (0); // BEER
		case 52:
			if (sprite[ps[snum].i].extra < 100)
				return (30);
			else
				return (0); // PORKBALLS
		case 53:
			if (ps[snum].whishkey_amount < 100)
				return (100);
			else
				return (0);
		case 54:
			if (ps[snum].shield_amount < 100)
				return (50);
			else
				return (0);
		case 55:
			if (ps[snum].moonshine_amount == 400)
				return (30);
			else
				return (0);
		case 56:
			if (ps[snum].snorkle_amount < 6400)
				return (30);
			else
				return (0);
		case 57:
			if (ps[snum].cowpie_amount < 600)
				return (100);
			else
				return (0);
		case 59:
			if (ps[snum].yeehaa_amount < 1200)
				return (10);
			else
				return (0);
		case 60:
			return (1);
		case BOOTS:
			if (ps[snum].boot_amount < 2000)
				return (50);
			else
				return (0);
		case 5595:
			if (sprite[ps[snum].i].extra < currentGame.getCON().max_player_health)
				return (50);
			else
				return (0);
		case 3437:
			if (ps[snum].beer_amount < 2400)
				return (30);
			else
				return (0);
		}
		return (0);
	}

	public static boolean checkaccess(short sectnum, int snum) {
		if (sectnum == -1)
			return false;

		if (sector[sectnum].filler == 0)
			return true;

		if (sector[sectnum].filler > 6)
			return true;

		if (sector[sectnum].filler > 3)
			sector[sectnum].filler -= 3;

		if (ps[snum].gotkey[sector[sectnum].filler] != 0) {
			sector[sectnum].filler = 0;
			return true;
		}

		return false;
	}

	public static int[] goalx = new int[MAXPLAYERS], goaly = new int[MAXPLAYERS], goalz = new int[MAXPLAYERS];
	public static int[] goalsect = new int[MAXPLAYERS], goalwall = new int[MAXPLAYERS],
			goalsprite = new int[MAXPLAYERS];
	public static int[] goalplayer = new int[MAXPLAYERS], clipmovecount = new int[MAXPLAYERS];
	public static short[] searchsect = new short[MAXSECTORS], searchparent = new short[MAXSECTORS];
	public static byte[] dashow2dsector = new byte[(MAXSECTORS + 7) >> 3];

	public static void computergetinput(int snum, Input syn) {
		int i, j, k, l, x1, y1, z1, x2, y2, z2, x3, y3, z3, dx, dy;
		int dist, daang, zang, fightdist;
		float damyang;
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
		if ((p.whishkey_amount > 0) && (p.last_extra < 100))
			syn.bits |= (1 << 16);

		for (j = headspritestat[4]; j >= 0; j = nextspritestat[j]) {
			switch (sprite[j].picnum) {
			case TONGUE:
				k = 4;
				break;
			case ALIENBLAST:
				k = 4;
				break;
			case CIRCLESAW:
				k = 16;
				break;
			case CROSSBOW:
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

						dasect = (short) pHitInfo.hitsect;
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

			if ((p.curr_weapon == CROSSBOW_WEAPON) && ((engine.rand() & 7)) == 0)
				syn.bits &= ~(1 << 2);

			if (p.curr_weapon == POWDERKEG_WEAPON)
				syn.bits |= ((engine.rand() % MAX_WEAPONS) << 8);

			if (p.curr_weapon == DYNAMITE_WEAPON) {
				engine.hitscan(x1, y1, z1 - PHEIGHT, damysect, sintable[((int) (damyang + 512)) & 2047],
						sintable[(int) damyang & 2047], (100 - (int) p.horiz - p.horizoff) * 32, pHitInfo, CLIPMASK1);

				dasect = (short) pHitInfo.hitsect;
				x3 = pHitInfo.hitx;
				y3 = pHitInfo.hity;
				z3 = pHitInfo.hitz;

				if ((x3 - x1) * (x3 - x1) + (y3 - y1) * (y3 - y1) < 2560 * 2560)
					syn.bits &= ~(1 << 2);
			}

			fightdist = 128;
			switch (p.curr_weapon) {
			case 0:
			case 11:
				fightdist = 256;
				break;
			case 5:
				fightdist = 2048;
				break;
			case 4:
				fightdist = 512;
				if ((engine.rand() & 0x1F) == 0)
					syn.bits &= ~(1 << 2);
				break;
			case 10:
				fightdist = 4096;
				break;
			case 9:
				fightdist = 1024;
				break;
			case 1:
			case 2:
			case 3:
			case 6:
			case 7:
			case 12:
				fightdist = 512;
				break;
			}

			dist = engine.ksqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
			if (dist == 0)
				dist = 1;
			daang = engine.getangle(x2 + (ps[goalplayer[snum]].posxv >> 14) - x1,
					y2 + (ps[goalplayer[snum]].posyv >> 14) - y1);
			zang = 100 - ((z2 - z1) * 8) / dist;
			fightdist = (int) max(fightdist, (klabs(z2 - z1) >> 4));

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

			syn.avel = min(max(((BClampAngle(daang + 1024 - damyang)) - 1024) / 2.0f, -127), 127);
			syn.horz = (float) min(max((zang - p.horiz) / 2.0f, -MAXHORIZ), MAXHORIZ);
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
			syn.avel = min(max(((BClampAngle(daang + 1024 - damyang)) - 1024) / 8.0f, -127), 127);
		} else
			goalsprite[snum] = -1;

		x3 = p.posx;
		y3 = p.posy;
		z3 = p.posz;
		dasect = p.cursectnum;
		i = engine.clipmove(x3, y3, z3, dasect, p.posxv, p.posyv, 164, 4 << 8, 4 << 8, CLIPMASK0);

		if (clipmove_sectnum != -1) {
			dasect = (short) clipmove_sectnum;
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
				dasect = (short) clipmove_sectnum;
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
					syn.bits |= (1 << 0);
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
			syn.avel = min(max(((BClampAngle(daang + 1024 - damyang)) - 1024) / 8.0f, -127), 127);
		}
	}
}
