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

import static ru.m210projects.Build.Engine.MAXPALOOKUPS;
import static ru.m210projects.Build.Engine.MAXPLAYERS;
import static ru.m210projects.Build.Engine.MAXPSKYTILES;
import static ru.m210projects.Build.Engine.MAXSECTORS;
import static ru.m210projects.Build.Engine.MAXSPRITES;
import static ru.m210projects.Build.Engine.MAXSTATUS;
import static ru.m210projects.Build.Engine.headspritesect;
import static ru.m210projects.Build.Engine.headspritestat;
import static ru.m210projects.Build.Engine.nextspritesect;
import static ru.m210projects.Build.Engine.nextspritestat;
import static ru.m210projects.Build.Engine.numsectors;
import static ru.m210projects.Build.Engine.numshades;
import static ru.m210projects.Build.Engine.numwalls;
import static ru.m210projects.Build.Engine.palette;
import static ru.m210projects.Build.Engine.palookup;
import static ru.m210projects.Build.Engine.palookupfog;
import static ru.m210projects.Build.Engine.parallaxtype;
import static ru.m210projects.Build.Engine.parallaxyscale;
import static ru.m210projects.Build.Engine.pskybits;
import static ru.m210projects.Build.Engine.pskyoff;
import static ru.m210projects.Build.Engine.sector;
import static ru.m210projects.Build.Engine.show2dsector;
import static ru.m210projects.Build.Engine.show2dsprite;
import static ru.m210projects.Build.Engine.show2dwall;
import static ru.m210projects.Build.Engine.sprite;
import static ru.m210projects.Build.Engine.visibility;
import static ru.m210projects.Build.Engine.wall;
import static ru.m210projects.Build.Engine.zeropskyoff;
import static ru.m210projects.Build.Net.Mmulti.connecthead;
import static ru.m210projects.Build.Net.Mmulti.connectpoint2;
import static ru.m210projects.Build.Net.Mmulti.myconnectindex;
import static ru.m210projects.Build.Net.Mmulti.numplayers;
import static ru.m210projects.Redneck.Actors.BowlReset;
import static ru.m210projects.Redneck.Actors.UFO_SpawnCount;
import static ru.m210projects.Redneck.Actors.UFO_SpawnHulk;
import static ru.m210projects.Redneck.Actors.UFO_SpawnTime;
import static ru.m210projects.Redneck.Animate.gAnimationCount;
import static ru.m210projects.Redneck.Globals.ANIM_PAL;
import static ru.m210projects.Redneck.Globals.BOAT_WEAPON;
import static ru.m210projects.Redneck.Globals.BellSound;
import static ru.m210projects.Redneck.Globals.BellTime;
import static ru.m210projects.Redneck.Globals.HANDREMOTE_WEAPON;
import static ru.m210projects.Redneck.Globals.KNEE_WEAPON;
import static ru.m210projects.Redneck.Globals.LeonardCrack;
import static ru.m210projects.Redneck.Globals.MAXANIMWALLS;
import static ru.m210projects.Redneck.Globals.MAXCYCLERS;
import static ru.m210projects.Redneck.Globals.MAX_WEAPONSRA;
import static ru.m210projects.Redneck.Globals.MODE_END;
import static ru.m210projects.Redneck.Globals.MODE_EOL;
import static ru.m210projects.Redneck.Globals.MOTO_WEAPON;
import static ru.m210projects.Redneck.Globals.PISTOL_WEAPON;
import static ru.m210projects.Redneck.Globals.RATE_WEAPON;
import static ru.m210projects.Redneck.Globals.RRRA;
import static ru.m210projects.Redneck.Globals.Sound;
import static ru.m210projects.Redneck.Globals.WindDir;
import static ru.m210projects.Redneck.Globals.WindTime;
import static ru.m210projects.Redneck.Globals.animwall;
import static ru.m210projects.Redneck.Globals.camsprite;
import static ru.m210projects.Redneck.Globals.currentGame;
import static ru.m210projects.Redneck.Globals.cyclers;
import static ru.m210projects.Redneck.Globals.display_mirror;
import static ru.m210projects.Redneck.Globals.earthquaketime;
import static ru.m210projects.Redneck.Globals.fakebubba_spawn;
import static ru.m210projects.Redneck.Globals.frags;
import static ru.m210projects.Redneck.Globals.fricxv;
import static ru.m210projects.Redneck.Globals.fricyv;
import static ru.m210projects.Redneck.Globals.gVisibility;
import static ru.m210projects.Redneck.Globals.hittype;
import static ru.m210projects.Redneck.Globals.loadfromgrouponly;
import static ru.m210projects.Redneck.Globals.mamaspawn_count;
import static ru.m210projects.Redneck.Globals.mirrorcnt;
import static ru.m210projects.Redneck.Globals.mirrorsector;
import static ru.m210projects.Redneck.Globals.mirrorwall;
import static ru.m210projects.Redneck.Globals.numanimwalls;
import static ru.m210projects.Redneck.Globals.numcyclers;
import static ru.m210projects.Redneck.Globals.numplayersprites;
import static ru.m210projects.Redneck.Globals.po;
import static ru.m210projects.Redneck.Globals.ps;
import static ru.m210projects.Redneck.Globals.screenpeek;
import static ru.m210projects.Redneck.Globals.soundsiz;
import static ru.m210projects.Redneck.Globals.spriteq;
import static ru.m210projects.Redneck.Globals.spriteqloc;
import static ru.m210projects.Redneck.Globals.startofdynamicinterpolations;
import static ru.m210projects.Redneck.Globals.tempbuf;
import static ru.m210projects.Redneck.Globals.uGameFlags;
import static ru.m210projects.Redneck.Globals.ud;
import static ru.m210projects.Redneck.Main.cfg;
import static ru.m210projects.Redneck.Main.engine;
import static ru.m210projects.Redneck.Main.game;
import static ru.m210projects.Redneck.Main.mUserFlag;
import static ru.m210projects.Redneck.Names.APLAYER;
import static ru.m210projects.Redneck.Names.BIGFORCE;
import static ru.m210projects.Redneck.Names.CYCLER;
import static ru.m210projects.Redneck.Names.DOORKEYS;
import static ru.m210projects.Redneck.Names.FANSPRITEWORK;
import static ru.m210projects.Redneck.Names.GPSPEED;
import static ru.m210projects.Redneck.Names.JAILDOOR;
import static ru.m210projects.Redneck.Names.JAILSOUND;
import static ru.m210projects.Redneck.Names.LIGHTNIN;
import static ru.m210projects.Redneck.Names.LIGHTSWITCH2;
import static ru.m210projects.Redneck.Names.LOCKSWITCH1;
import static ru.m210projects.Redneck.Names.MINECARTKILLER;
import static ru.m210projects.Redneck.Names.MIRROR;
import static ru.m210projects.Redneck.Names.SCREENBREAK6;
import static ru.m210projects.Redneck.Names.SECTOREFFECTOR;
import static ru.m210projects.Redneck.Names.SHADESECTOR;
import static ru.m210projects.Redneck.Names.SOUNDFX;
import static ru.m210projects.Redneck.Names.STARSKY2;
import static ru.m210projects.Redneck.Names.TORCH;
import static ru.m210projects.Redneck.Player.checkavailweapon;
import static ru.m210projects.Redneck.Player.drealms;
import static ru.m210projects.Redneck.Player.drugpal;
import static ru.m210projects.Redneck.Player.endingpal;
import static ru.m210projects.Redneck.Player.setpal;
import static ru.m210projects.Redneck.Player.slimepal;
import static ru.m210projects.Redneck.Player.titlepal;
import static ru.m210projects.Redneck.Player.waterpal;
import static ru.m210projects.Redneck.ResourceHandler.InitSpecialTextures;
import static ru.m210projects.Redneck.Sounds.NUM_SOUNDS;
import static ru.m210projects.Redneck.Sounds.loadSample;
import static ru.m210projects.Redneck.Spawn.EGS;
import static ru.m210projects.Redneck.Spawn.spawn;
import static ru.m210projects.Redneck.Spawn.tempwallptr;
import static ru.m210projects.Redneck.View.animatesprites;
import static ru.m210projects.Redneck.View.gNameShowTime;

import java.io.FileNotFoundException;
import java.util.Arrays;

import ru.m210projects.Build.Architecture.BuildFrame.FrameType;
import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.FileHandle.Resource;
import ru.m210projects.Build.Render.GLRenderer;
import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Build.Types.Tile;
import ru.m210projects.Build.Types.WALL;
import ru.m210projects.Redneck.Main.UserFlag;
import ru.m210projects.Redneck.Types.PlayerStruct;

public class Premap {

	public static short[] rorsector = new short[16];
	public static byte[] rortype = new byte[16];
	public static int rorcnt;

	public static boolean shadeEffect[] = new boolean[MAXSECTORS];

	public static final int MAXJAILDOORS = 32;
	public static int jailspeed[] = new int[MAXJAILDOORS];
	public static int jaildistance[] = new int[MAXJAILDOORS];
	public static short jailsect[] = new short[MAXJAILDOORS];
	public static short jaildirection[] = new short[MAXJAILDOORS];
	public static short jailunique[] = new short[MAXJAILDOORS];
	public static short jailsound[] = new short[MAXJAILDOORS];
	public static short jailstatus[] = new short[MAXJAILDOORS];
	public static int jailcount2[] = new int[MAXJAILDOORS];

	public static final int MAXMINECARDS = 16;
	public static int minespeed[] = new int[MAXMINECARDS];
	public static int minefulldist[] = new int[MAXMINECARDS];
	public static int minedistance[] = new int[MAXMINECARDS];
	public static short minechild[] = new short[MAXMINECARDS];
	public static short mineparent[] = new short[MAXMINECARDS];
	public static short minedirection[] = new short[MAXMINECARDS];
	public static short minesound[] = new short[MAXMINECARDS];
	public static short minestatus[] = new short[MAXMINECARDS];

	public static final int MAXTORCHES = 64;
	public static short torchsector[] = new short[MAXTORCHES];
	public static byte torchshade[] = new byte[MAXTORCHES];
	public static short torchflags[] = new short[MAXTORCHES];

	public static final int MAXLIGHTNINS = 64;
	public static short lightninsector[] = new short[MAXLIGHTNINS];
	public static short lightninshade[] = new short[MAXLIGHTNINS];

	public static final int MAXAMBIENTS = 64;
	public static short ambienttype[] = new short[MAXAMBIENTS];
	public static short ambientid[] = new short[MAXAMBIENTS];
	public static short ambienthitag[] = new short[MAXAMBIENTS];

	public static final int MAXGEOMETRY = 64;
	public static short geomsector[] = new short[MAXGEOMETRY];
	public static short geoms1[] = new short[MAXGEOMETRY];
	public static int geomx1[] = new int[MAXGEOMETRY];
	public static int geomy1[] = new int[MAXGEOMETRY];
	public static int geomz1[] = new int[MAXGEOMETRY];
	public static short geoms2[] = new short[MAXGEOMETRY];
	public static int geomx2[] = new int[MAXGEOMETRY];
	public static int geomy2[] = new int[MAXGEOMETRY];
	public static int geomz2[] = new int[MAXGEOMETRY];

	public static boolean plantProcess = false;

	public static int numlightnineffects, numtorcheffects, numgeomeffects, numjaildoors, numminecart, numambients;
	private static int haveLigthning;

	public static byte[] packbuf = new byte[576];

	public static short which_palookup = 9;

	public static boolean getsound(int num) {
		if (num >= NUM_SOUNDS || cfg.noSound)
			return false;

		if (currentGame.getCON().sounds[num] == null)
			return false;

		Resource fp = BuildGdx.cache.open(currentGame.getCON().sounds[num], loadfromgrouponly);
		if (fp == null)
			return false;

		soundsiz[num] = fp.size();

		// if( (ud.level_number == 0 && ud.volume_number == 0 && (num == 189 || num ==
		// 232 || num == 99 || num == 233 || num == 17 ) ) || ( l < 12288 ) )
		{
			Sound[num].lock = 2;
			loadSample(fp.getBytes(), num);
		}
		fp.close();
		return true;
	}

	public static void xyzmirror(int i, int wn) {
		Tile pic = engine.getTile(wn);
		engine.setviewtotile(wn, pic.getHeight(), pic.getWidth());

		engine.drawrooms(sprite[i].x, sprite[i].y, sprite[i].z, sprite[i].ang, 100 + sprite[i].shade,
				sprite[i].sectnum);
		display_mirror = 1;
		animatesprites(sprite[i].x, sprite[i].y, sprite[i].z, sprite[i].ang, 65536);
		display_mirror = 0;
		engine.drawmasks();

		engine.setviewback();
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
		p.random_club_frame = 0;
		p.on_warping_sector = 0;
		p.spritebridge = 0;
		p.palette = palette;

		if (p.moonshine_amount < 400) {
			p.moonshine_amount = 0;
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
			p.kickback_pic = 22;
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

		p.field_280 = 0;
		p.field_X = 0;
		p.field_Y = 0;
		p.field_28E = 0;
		p.field_290 = 0;
		if (ud.multimode <= 1 || ud.coop == 1) {
			p.gotkey[0] = 0;
			p.gotkey[1] = 0;
			p.gotkey[2] = 0;
			p.gotkey[3] = 0;
			p.gotkey[4] = 0;
		} else {
			p.gotkey[0] = 1;
			p.gotkey[1] = 1;
			p.gotkey[2] = 1;
			p.gotkey[3] = 1;
			p.gotkey[4] = 1;
		}
		p.alcohol_meter = 1647;
		p.gut_meter = 1647;
		p.alcohol_amount = 0;
		p.gut_amount = 0;
		p.alcohol_count = 4096;
		p.gut_count = 4096;
		p.drunk = 0;
		p.shotgunstatus = 0;
		p.shotgun_splitshot = 0;
		p.field_57C = 0;
		p.kickback = 0;
		p.field_count = 0;
		LeonardCrack = 0;
		p.detonate_count = 0;

		// RA
		p.chiken_phase = 0;
		p.chiken_pic = 0;
		if (p.OnMotorcycle) {
			p.OnMotorcycle = false;
			p.gotweapon[MOTO_WEAPON] = false;
			p.curr_weapon = RATE_WEAPON;
			p.kickback_pic = 0;
			checkavailweapon(p);
		}

		p.CarVar6 = 0;
		p.CarOnGround = true;
		p.CarVar1 = 0;
		p.CarSpeed = 0;
		p.TiltStatus = 0;
		p.CarVar2 = 0;
		p.VBumpTarget = 0;
		p.VBumpNow = 0;
		p.CarVar3 = 0;
		p.TurbCount = 0;
		p.CarVar5 = 0;
		p.CarVar4 = 0;
		if (p.OnBoat) {
			p.OnBoat = false;
			p.gotweapon[BOAT_WEAPON] = false;
			p.curr_weapon = RATE_WEAPON;
			p.kickback_pic = 0;
			checkavailweapon(p);
		}
		p.NotOnWater = 0;
		p.SeaSick = 0;
		p.DrugMode = 0;
		p.drug_type = 0;
		p.drug_intensive = 0;
		p.drug_timer = 0;
		p.drug_aspect = 0;

		// last_extra check
		p.numloogs = 0; // GDX 31.10.2018 XXX
		p.truefz = 0;
		p.truecz = 0;
		p.randomflamex = 0;
		p.access_incs = 0;
		p.access_wallnum = 0;
		p.interface_toggle_flag = 0;
		p.scream_voice = null;
		p.crouch_toggle = 0;
		p.exitx = 0;
		p.exity = 0;
		p.last_used_weapon = 0;
		p.ohorizoff = 0;
	}

	public static void resetweapons(int snum) {
		int weapon;
		PlayerStruct p = ps[snum];

		for (weapon = PISTOL_WEAPON; weapon < MAX_WEAPONSRA; weapon++)
			p.gotweapon[weapon] = false;
		for (weapon = PISTOL_WEAPON; weapon < MAX_WEAPONSRA; weapon++)
			p.ammo_amount[weapon] = 0;

		Arrays.fill(p.weaprecs, (short) 0);

		p.weapon_pos = 6;
		p.kickback_pic = 5;
		p.curr_weapon = PISTOL_WEAPON;
		p.gotweapon[PISTOL_WEAPON] = true;
		p.gotweapon[KNEE_WEAPON] = true;
		p.ammo_amount[PISTOL_WEAPON] = 48;
		p.gotweapon[HANDREMOTE_WEAPON] = true;
		if (currentGame.getCON().type == RRRA)
			p.gotweapon[RATE_WEAPON] = true;
		p.last_weapon = -1;

		p.show_empty_weapon = 0;
		p.last_pissed_time = 0;
		p.holster_weapon = 0;

		p.OnMotorcycle = false;
		p.OnBoat = false;
		p.CarVar1 = 0;
	}

	public static void resetinventory(int snum) {
		PlayerStruct p = ps[snum];

		p.inven_icon = 0;
		p.boot_amount = 0;
		p.scuba_on = 0;
		p.snorkle_amount = 0;
		p.yeehaa_amount = 0;
		p.heat_on = 0;
		p.jetpack_on = 0;
		p.cowpie_amount = 0;
		p.shield_amount = (short) currentGame.getCON().max_armour_amount;
		p.holoduke_on = -1;
		p.beer_amount = 0;
		p.whishkey_amount = 0;
		p.moonshine_amount = 0;
		p.inven_icon = 0;

        p.alcohol_meter = 1647;
		p.gut_meter = 1647;
		p.alcohol_amount = 0;
		p.gut_amount = 0;
		p.alcohol_count = 0;
		p.gut_count = 0;
		p.drunk = 0;
		p.shotgunstatus = 0;
		p.shotgun_splitshot = 0;
		p.field_57C = 0;
		p.detonate_count = 0;
		p.kickback = 0;
		p.field_count = 0;
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
		WindTime = 0;
		WindDir = 0;
		fakebubba_spawn = 0;
		BellTime = 0;

		startofdynamicinterpolations = 0;

		if (((uGameFlags & MODE_EOL) != MODE_EOL && numplayers < 2) || (ud.coop != 1 && numplayers > 1)) {
			resetweapons(snum);
			resetinventory(snum);
		} else if (p.curr_weapon == HANDREMOTE_WEAPON) {
			p.ammo_amount[4]++;
			p.curr_weapon = 4;
		}

		p.timebeforeexit = 0;
		p.customexitsound = 0;

		Arrays.fill(p.pals, (short) 0);

		p.field_280 = 0;
		p.field_X = 0x20000;
		p.field_Y = 0x20000;
		p.field_28E = 0;
		p.field_290 = 0;

        p.alcohol_meter = 1647;
		p.gut_meter = 1647;
		p.alcohol_amount = 0;
		p.gut_amount = 0;
		p.alcohol_count = 0;
		p.gut_count = 0;
		p.drunk = 0;
		p.shotgunstatus = 0;
		p.shotgun_splitshot = 0;
		p.field_57C = 0;
		p.detonate_count = 0;
		p.kickback = 0;
		p.field_count = 0;

		if (numplayers >= 2) {
			UFO_SpawnCount = 32;
			UFO_SpawnTime = 0;
			UFO_SpawnHulk = 2;
		} else {
			UFO_SpawnCount = (ud.player_skill << 2) + 1;
			if (UFO_SpawnCount > 32)
				UFO_SpawnCount = 32;
			UFO_SpawnTime = 0;
			UFO_SpawnHulk = ud.player_skill + 1;
		}
	}

	public static void setupbackdrop(short sky) {
		Arrays.fill(pskyoff, (short) 0);

		parallaxyscale = 32768;

		switch (sky) {
		case 1022:
			pskyoff[6] = 1;
			pskyoff[1] = 2;
			pskyoff[4] = 2;
			pskyoff[2] = 3;
			break;
		case 1026:
			pskyoff[5] = 1;
			pskyoff[6] = 2;
			pskyoff[7] = 3;
			pskyoff[2] = 4;
			break;
		case 1031:
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
		}

		Arrays.fill(zeropskyoff, (short) 0);
		System.arraycopy(pskyoff, 0, zeropskyoff, 0, MAXPSKYTILES);

		pskybits = 2;
	}

	public static final short lotags[] = new short[65];

	public static void prelevel() {
		short i, nexti, j, startwall, endwall, lotaglist;
		Arrays.fill(lotags, (short) 0);

		Arrays.fill(show2dsector, (byte) 0);
		Arrays.fill(show2dwall, (byte) 0);
		Arrays.fill(show2dsprite, (byte) 0);

		Arrays.fill(shadeEffect, false);
		Arrays.fill(geoms1, (short) -1);
		Arrays.fill(geoms2, (short) -1);
		Arrays.fill(ambienttype, (short) -1);
		Arrays.fill(ambientid, (short) -1);
		Arrays.fill(ambienthitag, (short) -1);

		// RA
		ps[0].fogtype = 0;
		applyfog(0);
		ps[0].isSea = false;
		ps[0].isSwamp = false;
		ps[0].field_601 = 0;
		ps[0].SlotWin = 0;
		ps[0].field_607 = 0;
		ps[0].MamaEnd = 0;
		BellSound = 0;
		mamaspawn_count = 15;
		if (ud.level_number != 3 || ud.volume_number != 0) {
			if (ud.level_number == 2 && ud.volume_number == 1)
				mamaspawn_count = 10;
			else if (ud.level_number == 6 && ud.volume_number == 1)
				mamaspawn_count = 15;
			else if (ud.level_number == 4 && ud.volume_number == 1)
				ps[myconnectindex].moonshine_amount = 0;
		} else
			mamaspawn_count = 5;

		resetprestat(0);
		gVisibility = currentGame.getCON().const_visibility;

		numlightnineffects = 0;
		numtorcheffects = 0;
		numgeomeffects = 0;
		numjaildoors = 0;
		numminecart = 0;
		numambients = 0;
		haveLigthning = 0;
		plantProcess = false;

		BowlReset();

		fakebubba_spawn = 0;
		mamaspawn_count = 15;
		BellTime = 0;

		for (j = 0; j < MAXSPRITES; ++j) {
			if (sprite[j].pal == 100) {
				if (numplayers <= 1)
					sprite[j].pal = 0;
				else
					engine.deletesprite(j);
			} else if (sprite[j].pal == 101) {
				sprite[j].extra = 0;
				sprite[j].hitag = 1;
				sprite[j].pal = 0;
				engine.changespritestat(j, (short) 118);
			}
		}

		int distance = 0, speed = 0, sound = 0;
		for (i = 0; i < numsectors; i++) {
			sector[i].extra = 256;
			if (sector[i].ceilingpicnum == STARSKY2)
				haveLigthning = 1;

			switch (sector[i].lotag) {
			case 20:
			case 22:
				if (sector[i].floorz > sector[i].ceilingz)
					sector[i].lotag |= 32768;
				continue;
			case 41:
				j = headspritesect[i];
				while (j >= 0) {
					nexti = nextspritesect[j];
					if (sprite[j].picnum == JAILDOOR) {
						distance = sprite[j].lotag << 4;
						speed = sprite[j].hitag;
						engine.deletesprite(j);
					}
					if (sprite[j].picnum == JAILSOUND) {
						sound = sprite[j].lotag;
						engine.deletesprite(j);
					}
					j = nexti;
				}

				for (j = 0; j < numsectors; j++) {
					if (sector[i].hitag == sector[j].hitag && i != j) {
						if (numjaildoors > MAXJAILDOORS)
							game.dassert("Too many jaildoor sectors");

						int num = numjaildoors;
						jailspeed[num] = speed;
						jaildistance[num] = distance;
						jailsect[num] = j;
						jaildirection[num] = sector[j].lotag;
						jailunique[num] = sector[i].hitag;
						jailsound[num] = (short) sound;
						jailstatus[num] = 0;
						jailcount2[num] = 0;

						numjaildoors++;
					}
				}
				break;
			case 42:
				j = headspritesect[i];
				while (j >= 0) {
					nexti = nextspritesect[j];
					if (sprite[j].picnum == 64) {
						distance = sprite[j].lotag << 4;
						speed = sprite[j].hitag;

						for (short k = 0; k < MAXSPRITES; k++) {
							if (sprite[k].picnum == 66) {
								if (sprite[k].lotag == sprite[j].sectnum) {
									minechild[numminecart] = sprite[k].sectnum;
									engine.deletesprite(k);
								}
							}
						}
						engine.deletesprite(j);
					}
					if (sprite[j].picnum == 65) {
						sound = sprite[j].lotag;
						engine.deletesprite(j);
					}
					j = nexti;
				}

				if (numminecart > MAXMINECARDS)
					game.dassert("Too many minecart sectors");

				int num = numminecart;
				minespeed[num] = speed;
				mineparent[num] = i;
				minedirection[num] = sector[i].hitag;
				minefulldist[num] = distance;
				minedistance[num] = distance;
				minesound[num] = (short) sound;
				minestatus[num] = 1;
				numminecart++;
				break;
			}

			if ((sector[i].ceilingstat & 1) != 0) {
				setupbackdrop(sector[i].ceilingpicnum);

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
				case TORCH:
					if (numtorcheffects >= MAXTORCHES)
						game.dassert("Too many torch effects.");

					int num = numtorcheffects;
					torchsector[num] = sprite[i].sectnum;
					torchshade[num] = sector[sprite[i].sectnum].floorshade;
					torchflags[num] = sprite[i].lotag;
					numtorcheffects++;
					engine.deletesprite(i);
					break;

				case LIGHTNIN:
					if (numlightnineffects >= MAXLIGHTNINS)
						game.dassert("Too many lightnin effects.");

					int lnum = numlightnineffects;
					lightninsector[lnum] = sprite[i].sectnum;
					lightninshade[lnum] = sprite[i].lotag;
					numlightnineffects++;
					engine.deletesprite(i);
					break;

				case MINECARTKILLER:
					sprite[i].cstat |= 32768;
					break;

				case SHADESECTOR:
					shadeEffect[sprite[i].sectnum] = true;
					engine.deletesprite(i);
					break;

				case SOUNDFX:
					if (numambients >= MAXAMBIENTS)
						game.dassert("Too many ambient effects.");

					int anum = numambients;
					ambientid[anum] = i;
					ambienttype[anum] = sprite[i].lotag;
					ambienthitag[anum] = sprite[i].hitag;
					sprite[i].ang = (short) numambients;
					sprite[i].lotag = 0;
					sprite[i].hitag = 0;
					numambients++;
					break;

				case 94:
					plantProcess = true;
					break;
				}
			i = nexti;
		}

		for (i = 0; i < MAXSPRITES; i++) {
			if (sprite[i].picnum == 19) {
				if (numgeomeffects >= MAXGEOMETRY)
					game.dassert("Too many geometry effects.");
				if (sprite[i].hitag == 0) {
					geomsector[numgeomeffects] = sprite[i].sectnum;
					for (int k = 0; k < MAXSPRITES; k++) {
						if (sprite[k].lotag == sprite[i].lotag && i != k && sprite[k].picnum == 19) {
							if (sprite[k].hitag == 1) {
								geoms1[numgeomeffects] = sprite[k].sectnum;
								geomx1[numgeomeffects] = sprite[i].x - sprite[k].x;
								geomy1[numgeomeffects] = sprite[i].y - sprite[k].y;
								geomz1[numgeomeffects] = sprite[i].z - sprite[k].z;
							}
							if (sprite[k].hitag == 2) {
								geoms2[numgeomeffects] = sprite[k].sectnum;
								geomx2[numgeomeffects] = sprite[i].x - sprite[k].x;
								geomy2[numgeomeffects] = sprite[i].y - sprite[k].y;
								geomz2[numgeomeffects] = sprite[i].z - sprite[k].z;
							}
						}
					}
					numgeomeffects++;
				}

			}
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

				if (sprite[i].picnum == 19)
					engine.deletesprite(i);

				if (sprite[i].picnum == DOORKEYS) {
					sector[sprite[i].sectnum].filler = sprite[i].lotag;
					engine.deletesprite(i);
				}
			}

		lotaglist = 0;

		i = headspritestat[0];
		while (i >= 0) {
			switch (sprite[i].picnum) {
			case 85:
			case 87:
			case 89:
			case 91:
			case 93:
			case 94:
			case 95:
			case 122:
			case 124:
			case LIGHTSWITCH2 + 1:
			case 2223:
			case LOCKSWITCH1 + 1:
			case 2227:
			case 2250:
			case 2255:
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
			case FANSPRITEWORK:
				wall[0].cstat |= 65; // original typo wall->cstat |= 65 instead of wal->cstat |= 65;
				animwall[numanimwalls].wallnum = i;
				numanimwalls++;
				break;
			case BIGFORCE:

				animwall[numanimwalls].wallnum = i;
				numanimwalls++;

				continue;
			}

			wal.extra = -1;

			switch (wal.picnum) {
			case SCREENBREAK6:
				animwall[numanimwalls].wallnum = i;
				animwall[numanimwalls].tag = -1;
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

		if (haveLigthning == 0) {
//XXX	    	engine.setbrightness((ud.brightness >> 2) & 0xFF, palette);
			visibility = gVisibility;
		}

		InitSpecialTextures();

		gNameShowTime = 500;
	}

	public static void checknextlevel() {
		if (ud.level_number >= currentGame.episodes[ud.volume_number].nMaps) {
			if (ud.volume_number == 0) {
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

		if (currentGame.getCON().type == RRRA && ud.volume_number == 0 && ud.level_number == 6)
			uGameFlags |= MODE_END;
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
					ps[i].kickback_pic = 22;
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
				s.xrepeat = 24;
				s.yrepeat = 17;
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
			if (look_pos == 8)
				engine.makepalookup(54, tempbuf, 32, 32, 32, 1);
		}

		fp.read(waterpal);
		fp.read(slimepal);
		fp.read(titlepal);
		fp.read(drealms);
		fp.read(endingpal);

		palette[765] = palette[766] = palette[767] = 0;
		slimepal[765] = slimepal[766] = slimepal[767] = 0;
		waterpal[765] = waterpal[766] = waterpal[767] = 0;

		System.arraycopy(palette, 1, drugpal, 0, 767);

		fp.close();

		for (int i = 0; i < 768; i++)
			tempbuf[i] = (byte) i;
		for (int i = 0; i < 32; i++)
			tempbuf[i] = (byte) (i + 32);
		engine.makepalookup(7, tempbuf, 0, 0, 0, 1);
		for (int i = 0; i < 768; i++)
			tempbuf[i] = (byte) i;
		engine.makepalookup(30, tempbuf, 0, 0, 0, 1);
		engine.makepalookup(31, tempbuf, 0, 0, 0, 1);
		engine.makepalookup(32, tempbuf, 0, 0, 0, 1);
		engine.makepalookup(33, tempbuf, 0, 0, 0, 1);
		engine.makepalookup(105, tempbuf, 0, 0, 0, 1);

		int col = 63;
		for (int i = 64; i < 80; i++) {
			tempbuf[i] = (byte) (--col);
			tempbuf[i + 16] = (byte) (i - 24);
		}
		for (int i = 0; i < 32; i++)
			tempbuf[i] = (byte) (i + 32);
		engine.makepalookup(34, tempbuf, 0, 0, 0, 1);

		for (int i = 0; i < 768; i++)
			tempbuf[i] = (byte) i;
		for (int i = 0; i < 16; i++)
			tempbuf[i] = (byte) (i - 127);
		for (int i = 16; i < 32; i++)
			tempbuf[i] = (byte) (i - 64);
		engine.makepalookup(35, tempbuf, 0, 0, 0, 1);

		palookup[ANIM_PAL] = new byte[numshades << 8];
		for (int i = 0; i < MAXPALOOKUPS; i++)
			palookup[ANIM_PAL][i] = (byte) i;
	}

	private static boolean fogInited = false;

	private static byte[][] opalookup = new byte[5][];
	private static byte[][] opalookupfog = new byte[5][];

	public static void applyfog(int type) {
		if (!fogInited) {
			opalookup[0] = palookup[0];
			opalookup[1] = palookup[8];
			opalookup[2] = palookup[23];
			opalookup[3] = palookup[30];
			opalookup[4] = palookup[33];

			opalookupfog[0] = palookupfog[0];
			opalookupfog[1] = palookupfog[8];
			opalookupfog[2] = palookupfog[23];
			opalookupfog[3] = palookupfog[30];
			opalookupfog[4] = palookupfog[33];

			for (int i = 0; i < 256; i++)
				tempbuf[i] = (byte) i;
			engine.makepalookup(50, tempbuf, 12, 12, 12, 1);
			engine.makepalookup(51, tempbuf, 12, 12, 12, 1);

			fogInited = true;
		}

		if (type == 2) {
			palookup[0] = palookup[50];
			palookup[30] = palookup[51];
			palookup[33] = palookup[51];
			palookup[23] = palookup[51];
			palookup[8] = palookup[54];

			if (BuildGdx.graphics.getFrameType() == FrameType.GL) {
				palookupfog[0] = palookupfog[50];
				palookupfog[30] = palookupfog[51];
				palookupfog[33] = palookupfog[51];
				palookupfog[23] = palookupfog[51];
				palookupfog[8] = palookupfog[54];
			}
		}

		if (type == 0) {
			palookup[0] = opalookup[0];
			palookup[30] = opalookup[3];
			palookup[33] = opalookup[4];
			palookup[23] = opalookup[2];
			palookup[8] = opalookup[1];

			if (BuildGdx.graphics.getFrameType() == FrameType.GL) {
				palookupfog[0] = opalookupfog[0];
				palookupfog[30] = opalookupfog[3];
				palookupfog[33] = opalookupfog[4];
				palookupfog[23] = opalookupfog[2];
				palookupfog[8] = opalookupfog[1];
			}
		}

		final GLRenderer gl = engine.glrender();
		if (gl != null && gl.getTextureManager() != null) {
			gl.getTextureManager().invalidatepalookup(0);
			gl.getTextureManager().invalidatepalookup(30);
			gl.getTextureManager().invalidatepalookup(33);
			gl.getTextureManager().invalidatepalookup(23);
			gl.getTextureManager().invalidatepalookup(8);
		}
	}
}

class PlayerInfo {
	public int aimmode;
	public int autoaim;

	public int[] ammo_amount = new int[MAX_WEAPONSRA];
	public boolean[] gotweapon = new boolean[MAX_WEAPONSRA];

	public short shield_amount;
	public short curr_weapon;
	public int inven_icon;

	public short whishkey_amount;
	public short moonshine_amount;
	public short beer_amount;
	public short cowpie_amount;
	public short empty_amount;
	public short snorkle_amount;
	public short boot_amount;
	public short last_extra;

	public void set(PlayerStruct p) {
		aimmode = p.aim_mode;
		autoaim = p.auto_aim;

		if (ud.multimode > 1 && ud.coop == 1 && ud.last_level >= 0) {
			for (int j = 0; j < MAX_WEAPONSRA; j++) {
				ammo_amount[j] = p.ammo_amount[j];
				gotweapon[j] = p.gotweapon[j];
			}
			shield_amount = p.shield_amount;
			curr_weapon = p.curr_weapon;
			inven_icon = p.inven_icon;

			whishkey_amount = p.whishkey_amount;
			moonshine_amount = p.moonshine_amount;
			beer_amount = p.beer_amount;
			cowpie_amount = p.cowpie_amount;
			empty_amount = p.yeehaa_amount;
			snorkle_amount = p.snorkle_amount;
			boot_amount = p.boot_amount;
			last_extra = p.last_extra;
		}
	}

	public void restore(PlayerStruct p) {
		p.aim_mode = aimmode;
		p.auto_aim = autoaim;

		if (ud.multimode > 1 && ud.coop == 1 && ud.last_level >= 0) {
			for (int j = 0; j < MAX_WEAPONSRA; j++) {
				p.ammo_amount[j] = ammo_amount[j];
				p.gotweapon[j] = gotweapon[j];
			}
			p.shield_amount = shield_amount;
			p.curr_weapon = curr_weapon;
			p.inven_icon = inven_icon;

			p.whishkey_amount = whishkey_amount;
			p.moonshine_amount = moonshine_amount;
			p.beer_amount = beer_amount;
			p.cowpie_amount = cowpie_amount;
			p.yeehaa_amount = empty_amount;
			p.snorkle_amount = snorkle_amount;
			p.boot_amount = boot_amount;
			p.last_extra = last_extra;
		}
	}
}
