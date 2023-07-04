// This file is part of DukeGDX.
// Copyright (C) 2019  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// DukeGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// DukeGDX is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with DukeGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.Duke3D.Types;

import static ru.m210projects.Duke3D.Main.*;
import static ru.m210projects.Duke3D.Globals.*;
import static ru.m210projects.Duke3D.SoundDefs.DUKE_SCREAM;
import static ru.m210projects.Duke3D.Sounds.spritesound;

import ru.m210projects.Build.Audio.Source;
import ru.m210projects.Build.FileHandle.Resource;
import ru.m210projects.Build.Pattern.Tools.Interpolation.ILoc;
import ru.m210projects.Build.Types.LittleEndian;

public class PlayerStruct {
	
	public static final int sizeof = 1090 + (3 * MAX_WEAPONS); //1126
	
	public PLocation prevView = new PLocation();

	public int oposx,oposy,oposz;
	public int ohorizoff;
	public float ohoriz, oang;
	
	public int exitx,exity,numloogs,loogcnt;
	public int posx, posy, posz, invdisptime;
	public float horiz, ang, angvel;
	public int bobposx,bobposy,pyoff,opyoff;
	public int posxv,posyv,poszv,last_pissed_time,truefz,truecz;
	public int player_par;
	public int bobcounter,weapon_sway;
	public int pals_time,randomflamex,crack_time;

	public int aim_mode, auto_aim;

	public short cursectnum,look_ang,last_extra,subweapon;
	public short wackedbyactor, frag, fraggedself;
	public int[] ammo_amount = new int[MAX_WEAPONS];
			
	public short curr_weapon, last_weapon, tipincs, horizoff, wantweaponfire;
	public short holoduke_amount,newowner,hurt_delay,hbomb_hold_delay;
	public short jumping_counter,airleft,knee_incs,access_incs;
	public short access_wallnum,access_spritenum;
	public short kickback_pic,got_access,weapon_ang,firstaid_amount;
	public short somethingonplayer,on_crane,i,one_parallax_sectnum;
	public short random_club_frame,fist_incs;
	public short one_eighty_count,cheat_phase;
	public short dummyplayersprite,extra_extra8,quick_kick;
	public short heat_amount,actorsqu,timebeforeexit,customexitsound;

	public short[] weaprecs = new short[16];
	public short weapreccnt;
	public short interface_toggle_flag;

	public short rotscrnang,dead_flag,show_empty_weapon;
	public short scuba_amount,jetpack_amount,steroids_amount,shield_amount;
	public short holoduke_on,pycount,weapon_pos,frag_ps;
	public short transporter_hold,last_full_weapon,footprintshade,boot_amount;

	public Source scream_voice;

	public short on_warping_sector,footprintcount;
	public short hbomb_on,jumping_toggle,rapid_fire_hold;
	public boolean on_ground;
	public int inven_icon, buttonpalette, jetpack_on;

	public short spritebridge,lastrandomspot;
	public short scuba_on,footprintpal,heat_on;

	public short  holster_weapon,falling_counter;
	public boolean[] gotweapon = new boolean[MAX_WEAPONS];
	public boolean refresh_inventory;
	public byte[] palette;

	public short toggle_key_flag,knuckle_incs;
	public short walking_snd_toggle, palookup, hard_landing;
	public short max_secret_rooms;
	public short secret_rooms;
	public short[] pals = new short[3];
	public short max_actors_killed,actors_killed,return_to_center;
	
	public byte last_used_weapon;
	public byte crouch_toggle;
	
	public void UpdatePlayerLoc() {
		ILoc oldLoc = game.pInt.getsprinterpolate(i);
        if(oldLoc != null)
        {
        	oldLoc.x = posx;
        	oldLoc.y = posy;
        	oldLoc.z = posz;
        }
        
		prevView.x = posx;
		prevView.y = posy;
		prevView.z = posz;
		prevView.ang = ang;
		prevView.lookang = look_ang;
		prevView.rotscrnang = rotscrnang;
		prevView.horizoff = horizoff;
		prevView.horiz = horiz;
	}

	public void copy(PlayerStruct src)
	{
		this.exitx = src.exitx;
		this.exity = src.exity;
		this.numloogs = src.numloogs;
		this.loogcnt = src.loogcnt;
		this.posx = src.posx;
		this.posy = src.posy;
		this.posz = src.posz;
		this.horiz = src.horiz;
		this.ohoriz = src.ohoriz;
		this.ohorizoff = src.ohorizoff;
		this.invdisptime = src.invdisptime;
		this.bobposx = src.bobposx;
		this.bobposy = src.bobposy;
		this.oposx = src.oposx;
		this.oposy = src.oposy;
		this.oposz = src.oposz;
		this.pyoff = src.pyoff;
		this.opyoff = src.opyoff;
		this.posxv = src.posxv;
		this.posyv = src.posyv;
		this.poszv = src.poszv;
		this.last_pissed_time = src.last_pissed_time;
		this.truefz = src.truefz;
		this.truecz = src.truecz;
		this.player_par = src.player_par;
		this.bobcounter = src.bobcounter;
		this.weapon_sway = src.weapon_sway;
		this.pals_time = src.pals_time;
		this.randomflamex = src.randomflamex;
		this.crack_time = src.crack_time;
		this.aim_mode = src.aim_mode;
		this.auto_aim = src.auto_aim;
		this.ang = src.ang;
		this.oang = src.oang;
		this.angvel = src.angvel;
		this.cursectnum = src.cursectnum;
		this.look_ang = src.look_ang;
		this.last_extra = src.last_extra;
		this.subweapon = src.subweapon;
		this.wackedbyactor = src.wackedbyactor;
		this.frag = src.frag;
		this.fraggedself = src.fraggedself;
		System.arraycopy(src.ammo_amount, 0, this.ammo_amount, 0, MAX_WEAPONS);
		this.curr_weapon = src.curr_weapon;
		this.last_weapon = src.last_weapon;
		this.tipincs = src.tipincs;
		this.horizoff = src.horizoff;
		this.wantweaponfire = src.wantweaponfire;
		this.holoduke_amount = src.holoduke_amount;
		this.newowner = src.newowner;
		this.hurt_delay = src.hurt_delay;
		this.hbomb_hold_delay = src.hbomb_hold_delay;
		this.jumping_counter = src.jumping_counter;
		this.airleft = src.airleft;
		this.knee_incs = src.knee_incs;
		this.access_incs = src.access_incs;
		this.access_wallnum = src.access_wallnum;
		this.access_spritenum = src.access_spritenum;
		this.kickback_pic = src.kickback_pic;
		this.got_access = src.got_access;
		this.weapon_ang = src.weapon_ang;
		this.firstaid_amount = src.firstaid_amount;
		this.somethingonplayer = src.somethingonplayer;
		this.on_crane = src.on_crane;
		this.i = src.i;
		this.one_parallax_sectnum = src.one_parallax_sectnum;
		this.random_club_frame = src.random_club_frame;
		this.fist_incs = src.fist_incs;
		this.one_eighty_count = src.one_eighty_count;
		this.cheat_phase = src.cheat_phase;
		this.dummyplayersprite = src.dummyplayersprite;
		this.extra_extra8 = src.extra_extra8;
		this.quick_kick = src.quick_kick;
		this.heat_amount = src.heat_amount;
		this.actorsqu = src.actorsqu;
		this.timebeforeexit = src.timebeforeexit;
		this.customexitsound = src.customexitsound;
		System.arraycopy(src.weaprecs, 0, this.weaprecs, 0, 16);
		this.weapreccnt = src.weapreccnt;
		this.interface_toggle_flag = src.interface_toggle_flag;
		this.rotscrnang = src.rotscrnang;
		this.dead_flag = src.dead_flag;
		this.show_empty_weapon = src.show_empty_weapon;
		this.scuba_amount = src.scuba_amount;
		this.jetpack_amount = src.jetpack_amount;
		this.steroids_amount = src.steroids_amount;
		this.shield_amount = src.shield_amount;
		this.holoduke_on = src.holoduke_on;
		this.pycount = src.pycount;
		this.weapon_pos = src.weapon_pos;
		this.frag_ps = src.frag_ps;
		this.transporter_hold = src.transporter_hold;
		this.last_full_weapon = src.last_full_weapon;
		this.footprintshade = src.footprintshade;
		this.boot_amount = src.boot_amount;
		this.scream_voice = src.scream_voice;
//		this.gm = src.gm;
		this.on_warping_sector = src.on_warping_sector;
		this.footprintcount = src.footprintcount;
		this.hbomb_on = src.hbomb_on;
		this.jumping_toggle = src.jumping_toggle;
		this.rapid_fire_hold = src.rapid_fire_hold;
		this.on_ground = src.on_ground;
		this.inven_icon = src.inven_icon;
		this.buttonpalette = src.buttonpalette;
		this.jetpack_on = src.jetpack_on;
		this.spritebridge = src.spritebridge;
		this.lastrandomspot = src.lastrandomspot;
		this.scuba_on = src.scuba_on;
		this.footprintpal = src.footprintpal;
		this.heat_on = src.heat_on;
		this.holster_weapon = src.holster_weapon;
		this.falling_counter = src.falling_counter;
		System.arraycopy(src.gotweapon, 0, this.gotweapon, 0, MAX_WEAPONS);
		this.refresh_inventory = src.refresh_inventory;
		this.palette = src.palette;
		this.toggle_key_flag = src.toggle_key_flag;
		this.knuckle_incs = src.knuckle_incs;
		this.walking_snd_toggle = src.walking_snd_toggle;
		this.palookup = src.palookup;
		this.hard_landing = src.hard_landing;
		this.max_secret_rooms = src.max_secret_rooms;
		this.secret_rooms = src.secret_rooms;
		System.arraycopy(src.pals, 0, this.pals, 0, 3);
		this.max_actors_killed = src.max_actors_killed;
		this.actors_killed = src.actors_killed;
		this.return_to_center = src.return_to_center;
		
		this.last_used_weapon = src.last_used_weapon;
		this.crouch_toggle = src.crouch_toggle;
	}
	
	private final byte[] buf = new byte[sizeof];
	public byte[] getBytes() {
		int ptr = 0;
		LittleEndian.putInt(buf, ptr, exitx); ptr+=4;
		LittleEndian.putInt(buf, ptr, exity); ptr+=4;

		LittleEndian.putInt(buf, ptr, numloogs); ptr+=4;
		LittleEndian.putInt(buf, ptr, loogcnt); ptr+=4;
		LittleEndian.putInt(buf, ptr, posx); ptr+=4;
		LittleEndian.putInt(buf, ptr, posy); ptr+=4;
		LittleEndian.putInt(buf, ptr, posz); ptr+=4;
		LittleEndian.putFloat(buf, ptr, horiz); ptr+=4;
		LittleEndian.putFloat(buf, ptr, ohoriz); ptr+=4;
		LittleEndian.putInt(buf, ptr, ohorizoff); ptr+=4;
		LittleEndian.putInt(buf, ptr, invdisptime); ptr+=4;
		LittleEndian.putInt(buf, ptr, bobposx); ptr+=4;
		LittleEndian.putInt(buf, ptr, bobposy); ptr+=4;
		LittleEndian.putInt(buf, ptr, oposx); ptr+=4;
		LittleEndian.putInt(buf, ptr, oposy); ptr+=4;
		LittleEndian.putInt(buf, ptr, oposz); ptr+=4;
		LittleEndian.putInt(buf, ptr, pyoff); ptr+=4;
		LittleEndian.putInt(buf, ptr, opyoff); ptr+=4;
		LittleEndian.putInt(buf, ptr, posxv); ptr+=4;
		LittleEndian.putInt(buf, ptr, posyv); ptr+=4;
		LittleEndian.putInt(buf, ptr, poszv); ptr+=4;
		LittleEndian.putInt(buf, ptr, last_pissed_time); ptr+=4;
		LittleEndian.putInt(buf, ptr, truefz); ptr+=4;
		LittleEndian.putInt(buf, ptr, truecz); ptr+=4;
		LittleEndian.putInt(buf, ptr, player_par); ptr+=4;
		LittleEndian.putInt(buf, ptr, bobcounter); ptr+=4;
		LittleEndian.putInt(buf, ptr, weapon_sway); ptr+=4;
		LittleEndian.putInt(buf, ptr, pals_time); ptr+=4;
		LittleEndian.putInt(buf, ptr, randomflamex); ptr+=4;
		LittleEndian.putInt(buf, ptr, crack_time); ptr+=4;
		LittleEndian.putInt(buf, ptr, aim_mode); ptr+=4;
		buf[ptr++] = (byte) auto_aim;
		LittleEndian.putFloat(buf, ptr, ang); ptr+=4;
		LittleEndian.putFloat(buf, ptr, oang); ptr+=4;
		LittleEndian.putFloat(buf, ptr, angvel); ptr+=4;
		LittleEndian.putShort(buf, ptr, cursectnum); ptr+=2;
		LittleEndian.putShort(buf, ptr, look_ang); ptr+=2;
		LittleEndian.putShort(buf, ptr, last_extra); ptr+=2;
		LittleEndian.putShort(buf, ptr, subweapon); ptr+=2;
		LittleEndian.putShort(buf, ptr, wackedbyactor); ptr+=2;
		LittleEndian.putShort(buf, ptr, frag); ptr+=2;
		LittleEndian.putShort(buf, ptr, fraggedself); ptr+=2;
		for(int i = 0; i < MAX_WEAPONS; i++)
		{
			LittleEndian.putShort(buf, ptr, (short)ammo_amount[i]); ptr+=2;
		}
		LittleEndian.putShort(buf, ptr, curr_weapon); ptr+=2;
		LittleEndian.putShort(buf, ptr, last_weapon); ptr+=2;	
		LittleEndian.putShort(buf, ptr, tipincs); ptr+=2;	
		LittleEndian.putShort(buf, ptr, horizoff); ptr+=2;	
		LittleEndian.putShort(buf, ptr, wantweaponfire); ptr+=2;	
		LittleEndian.putShort(buf, ptr, holoduke_amount); ptr+=2;	
		LittleEndian.putShort(buf, ptr, newowner); ptr+=2;	
		LittleEndian.putShort(buf, ptr, hurt_delay); ptr+=2;	
		LittleEndian.putShort(buf, ptr, hbomb_hold_delay); ptr+=2;	
		LittleEndian.putShort(buf, ptr, jumping_counter); ptr+=2;	
		LittleEndian.putShort(buf, ptr, airleft); ptr+=2;	
		LittleEndian.putShort(buf, ptr, knee_incs); ptr+=2;	
		LittleEndian.putShort(buf, ptr, access_incs); ptr+=2;	
		LittleEndian.putShort(buf, ptr, access_wallnum); ptr+=2;	
		LittleEndian.putShort(buf, ptr, access_spritenum); ptr+=2;	
		LittleEndian.putShort(buf, ptr, kickback_pic); ptr+=2;	
		LittleEndian.putShort(buf, ptr, got_access); ptr+=2;	
		LittleEndian.putShort(buf, ptr, weapon_ang); ptr+=2;	
		LittleEndian.putShort(buf, ptr, firstaid_amount); ptr+=2;	
		LittleEndian.putShort(buf, ptr, somethingonplayer); ptr+=2;	
		LittleEndian.putShort(buf, ptr, on_crane); ptr+=2;	
		LittleEndian.putShort(buf, ptr, i); ptr+=2;	
		LittleEndian.putShort(buf, ptr, one_parallax_sectnum); ptr+=2;	
		LittleEndian.putShort(buf, ptr, random_club_frame); ptr+=2;
		LittleEndian.putShort(buf, ptr, fist_incs); ptr+=2;
		LittleEndian.putShort(buf, ptr, one_eighty_count); ptr+=2;
		LittleEndian.putShort(buf, ptr, cheat_phase); ptr+=2;
		LittleEndian.putShort(buf, ptr, dummyplayersprite); ptr+=2;
		LittleEndian.putShort(buf, ptr, extra_extra8); ptr+=2;
		LittleEndian.putShort(buf, ptr, quick_kick); ptr+=2;
		LittleEndian.putShort(buf, ptr, heat_amount); ptr+=2;
		LittleEndian.putShort(buf, ptr, actorsqu); ptr+=2;
		LittleEndian.putShort(buf, ptr, timebeforeexit); ptr+=2;
		LittleEndian.putShort(buf, ptr, customexitsound); ptr+=2;
		for(int i = 0; i < 16; i++)
		{
			LittleEndian.putShort(buf, ptr, weaprecs[i]); ptr+=2;
		}
		LittleEndian.putShort(buf, ptr, weapreccnt); ptr+=2;
		LittleEndian.putShort(buf, ptr, interface_toggle_flag); ptr+=2;
		LittleEndian.putShort(buf, ptr, rotscrnang); ptr+=2;
		LittleEndian.putShort(buf, ptr, dead_flag); ptr+=2;
		LittleEndian.putShort(buf, ptr, show_empty_weapon); ptr+=2;
		LittleEndian.putShort(buf, ptr, scuba_amount); ptr+=2;
		LittleEndian.putShort(buf, ptr, jetpack_amount); ptr+=2;
		LittleEndian.putShort(buf, ptr, steroids_amount); ptr+=2;
		LittleEndian.putShort(buf, ptr, shield_amount); ptr+=2;
		LittleEndian.putShort(buf, ptr, holoduke_on); ptr+=2;
		LittleEndian.putShort(buf, ptr, pycount); ptr+=2;
		LittleEndian.putShort(buf, ptr, weapon_pos); ptr+=2;
		LittleEndian.putShort(buf, ptr, frag_ps); ptr+=2;
		LittleEndian.putShort(buf, ptr, transporter_hold); ptr+=2;
		LittleEndian.putShort(buf, ptr, last_full_weapon); ptr+=2;
		LittleEndian.putShort(buf, ptr, footprintshade); ptr+=2;
		LittleEndian.putShort(buf, ptr, boot_amount); ptr+=2;
		buf[ptr++] = scream_voice != null?(byte)1:0;
		buf[ptr++] = (byte)on_warping_sector;
		buf[ptr++] = (byte)footprintcount;
		buf[ptr++] = (byte)hbomb_on;
		buf[ptr++] = (byte)jumping_toggle;
		buf[ptr++] = (byte)rapid_fire_hold;
		buf[ptr++] = on_ground?(byte)1:0;
		buf[ptr++] = (byte)inven_icon;		
		buf[ptr++] = (byte)buttonpalette;		
		buf[ptr++] = (byte)jetpack_on;		
		buf[ptr++] = (byte)spritebridge;	
		buf[ptr++] = (byte)lastrandomspot;	
		buf[ptr++] = (byte)scuba_on;	
		buf[ptr++] = (byte)footprintpal;	
		buf[ptr++] = (byte)heat_on;	
		buf[ptr++] = (byte)holster_weapon;	
		buf[ptr++] = (byte)falling_counter;	
		for(int i = 0; i < MAX_WEAPONS; i++)
			buf[ptr++] = gotweapon[i]?(byte)1:0;
		buf[ptr++] = refresh_inventory?(byte)1:0;
		System.arraycopy(palette, 0, buf, ptr, 768); ptr += 768;
		buf[ptr++] = (byte)toggle_key_flag;	
		buf[ptr++] = (byte)knuckle_incs;	
		buf[ptr++] = (byte)walking_snd_toggle;	
		buf[ptr++] = (byte)palookup;	
		buf[ptr++] = (byte)hard_landing;	
		LittleEndian.putShort(buf, ptr, max_secret_rooms); ptr+=2;
		LittleEndian.putShort(buf, ptr, secret_rooms); ptr+=2;
		for(int i = 0; i < 3; i++) 
			buf[ptr++] = (byte)pals[i];
		LittleEndian.putShort(buf, ptr, max_actors_killed); ptr+=2;
		LittleEndian.putShort(buf, ptr, actors_killed); ptr+=2;
		buf[ptr++] = (byte)return_to_center;	
		
		buf[ptr++] = last_used_weapon;
		buf[ptr++] = crouch_toggle;

		return buf;
	}
	
	public void set(Resource bb)
	{
		exitx = bb.readInt();
		exity = bb.readInt();
		
		numloogs = bb.readInt();
		loogcnt = bb.readInt();
		posx = bb.readInt();
		posy = bb.readInt();
		posz = bb.readInt();
		horiz = bb.readFloat();
		ohoriz = bb.readFloat();
		ohorizoff = bb.readInt();
		invdisptime = bb.readInt();
		bobposx = bb.readInt();
		bobposy = bb.readInt();
		oposx = bb.readInt();
		oposy = bb.readInt();
		oposz = bb.readInt();
		pyoff = bb.readInt();
		opyoff = bb.readInt();
		posxv = bb.readInt();
		posyv = bb.readInt();
		poszv = bb.readInt();
		last_pissed_time = bb.readInt();
		truefz = bb.readInt();
		truecz = bb.readInt();
		player_par = bb.readInt();
		bobcounter = bb.readInt();
		weapon_sway = bb.readInt();
		pals_time = bb.readInt();
		randomflamex = bb.readInt();
		crack_time = bb.readInt();
		aim_mode = bb.readInt();
		auto_aim = bb.readByte() & 0xFF;
		ang = bb.readFloat();
		oang = bb.readFloat();
		angvel = bb.readFloat();
		cursectnum = bb.readShort();
		look_ang = bb.readShort();
		last_extra = bb.readShort();
		subweapon = bb.readShort();
		wackedbyactor = bb.readShort();
		frag = bb.readShort();
		fraggedself = bb.readShort();
		for(int i = 0; i < MAX_WEAPONS; i++)
			ammo_amount[i] = bb.readShort();
		curr_weapon = bb.readShort();
		last_weapon = bb.readShort();	
		tipincs = bb.readShort();	
		horizoff = bb.readShort();	
		wantweaponfire = bb.readShort();	
		holoduke_amount = bb.readShort();	
		newowner = bb.readShort();	
		hurt_delay = bb.readShort();	
		hbomb_hold_delay = bb.readShort();	
		jumping_counter = bb.readShort();	
		airleft = bb.readShort();	
		knee_incs = bb.readShort();	
		access_incs = bb.readShort();		
		access_wallnum = bb.readShort();	
		access_spritenum = bb.readShort();	
		kickback_pic = bb.readShort();	
		got_access = bb.readShort();	
		weapon_ang = bb.readShort();	
		firstaid_amount = bb.readShort();	
		somethingonplayer = bb.readShort();	
		on_crane = bb.readShort();	
		i = bb.readShort();	
		one_parallax_sectnum = bb.readShort();	
		random_club_frame = bb.readShort();
		fist_incs = bb.readShort();
		one_eighty_count = bb.readShort();
		cheat_phase = bb.readShort();
		dummyplayersprite = bb.readShort();
		extra_extra8 = bb.readShort();
		quick_kick = bb.readShort();
		heat_amount = bb.readShort();
		actorsqu = bb.readShort();
		timebeforeexit = bb.readShort();
		customexitsound = bb.readShort();
		for(int i = 0; i < 16; i++)
			weaprecs[i] = bb.readShort();
		
		weapreccnt = bb.readShort();
		interface_toggle_flag = bb.readShort();
		rotscrnang = bb.readShort();
		dead_flag = bb.readShort();
		show_empty_weapon = bb.readShort();
		scuba_amount = bb.readShort();
		jetpack_amount = bb.readShort();
		steroids_amount = bb.readShort();
		shield_amount = bb.readShort();
		holoduke_on = bb.readShort();
		pycount = bb.readShort();
		weapon_pos = bb.readShort();
		frag_ps = bb.readShort();
		transporter_hold = bb.readShort();
		last_full_weapon = bb.readShort();
		footprintshade = bb.readShort();
		boot_amount = bb.readShort();
		boolean svoice = bb.readBoolean();
		if(svoice) 
			 scream_voice = spritesound(DUKE_SCREAM, i);
		on_warping_sector = bb.readByte();
		footprintcount = bb.readByte();
		hbomb_on = bb.readByte();
		jumping_toggle = bb.readByte();
		rapid_fire_hold = bb.readByte();
		on_ground = bb.readBoolean();
		inven_icon  = bb.readByte();	
		buttonpalette  = bb.readByte();		
		jetpack_on  = bb.readByte();	
		spritebridge = bb.readByte();	
		lastrandomspot = bb.readByte();
		scuba_on = bb.readByte();
		footprintpal = bb.readByte();	
		heat_on = bb.readByte();	
		holster_weapon = bb.readByte();
		falling_counter = bb.readByte();	
		for(int i = 0; i < MAX_WEAPONS; i++)
			gotweapon[i] = bb.readBoolean();
		refresh_inventory = bb.readBoolean();
		this.palette = new byte[768];
		bb.read(this.palette);
		toggle_key_flag = bb.readByte();	
		knuckle_incs = bb.readByte();
		walking_snd_toggle = bb.readByte();
		palookup = bb.readByte();
		hard_landing = bb.readByte();	
		max_secret_rooms = bb.readShort();
		secret_rooms = bb.readShort();
		for(int i = 0; i < 3; i++) 
			pals[i] = bb.readByte();
		max_actors_killed = bb.readShort();
		actors_killed = bb.readShort();
		return_to_center = bb.readByte();
		
		last_used_weapon = bb.readByte(); 
		crouch_toggle = bb.readByte(); 
	}
	
	public void reset()
	{
		exitx = 0;
		exity = 0;
		numloogs = 0;
		loogcnt = 0;
		posx = 0;
		posy = 0;
		posz = 0;
		horiz = 0;
		ohoriz = 0;
		ohorizoff = 0;
		invdisptime = 0;
		bobposx = 0;
		bobposy = 0;
		oposx = 0;
		oposy = 0;
		oposz = 0;
		pyoff = 0;
		opyoff = 0;
		posxv = 0;
		posyv = 0;
		poszv = 0;
		last_pissed_time = 0;
		truefz = 0;
		truecz = 0;
		player_par = 0;
		bobcounter = 0;
		weapon_sway = 0;
		pals_time = 0;
		randomflamex = 0;
		crack_time = 0;
		aim_mode = 0;
		auto_aim = 0;
		ang = 0;
		oang = 0;
		angvel = 0;
		cursectnum = 0;
		look_ang = 0;
		last_extra = 0;
		subweapon = 0;
		wackedbyactor = 0;
		frag = 0;
		fraggedself = 0;
		for(int i = 0; i < MAX_WEAPONS; i++)
			ammo_amount[i] = 0;
		curr_weapon = 0;
		last_weapon = 0;	
		tipincs = 0;	
		horizoff = 0;	
		wantweaponfire = 0;	
		holoduke_amount = 0;	
		newowner = 0;	
		hurt_delay = 0;	
		hbomb_hold_delay = 0;	
		jumping_counter = 0;	
		airleft = 0;	
		knee_incs = 0;	
		access_incs = 0;		
		access_wallnum = 0;	
		access_spritenum = 0;	
		kickback_pic = 0;	
		got_access = 0;	
		weapon_ang = 0;	
		firstaid_amount = 0;	
		somethingonplayer = 0;	
		on_crane = 0;	
		i = 0;	
		one_parallax_sectnum = 0;	
		random_club_frame = 0;
		fist_incs = 0;
		one_eighty_count = 0;
		cheat_phase = 0;
		dummyplayersprite = 0;
		extra_extra8 = 0;
		quick_kick = 0;
		heat_amount = 0;
		actorsqu = 0;
		timebeforeexit = 0;
		customexitsound = 0;
		for(int i = 0; i < 16; i++)
			weaprecs[i] = 0;
		
		weapreccnt = 0;
		interface_toggle_flag = 0;
		rotscrnang = 0;
		dead_flag = 0;
		show_empty_weapon = 0;
		scuba_amount = 0;
		jetpack_amount = 0;
		steroids_amount = 0;
		shield_amount = 0;
		holoduke_on = 0;
		pycount = 0;
		weapon_pos = 0;
		frag_ps = 0;
		transporter_hold = 0;
		last_full_weapon = 0;
		footprintshade = 0;
		boot_amount = 0;
		scream_voice = null;
		on_warping_sector = 0;
		footprintcount = 0;
		hbomb_on = 0;
		jumping_toggle = 0;
		rapid_fire_hold = 0;
		on_ground = false;
		inven_icon  = 0;	
		buttonpalette  = 0;		
		jetpack_on  = 0;	
		spritebridge = 0;	
		lastrandomspot = 0;
		scuba_on = 0;
		footprintpal = 0;	
		heat_on = 0;	
		holster_weapon = 0;
		falling_counter = 0;	
		for(int i = 0; i < MAX_WEAPONS; i++)
			gotweapon[i] = false;
		refresh_inventory = false;
		palette = new byte[768];
		toggle_key_flag = 0;	
		knuckle_incs = 0;
		walking_snd_toggle = 0;
		palookup = 0;
		hard_landing = 0;	
		max_secret_rooms = 0;
		secret_rooms = 0;
		for(int i = 0; i < 3; i++) 
			pals[i] = 0;
		max_actors_killed = 0;
		actors_killed = 0;
		return_to_center = 0;
		
		last_used_weapon = 0; 
		crouch_toggle = 0; 
	}
	
	public String toString()
	{
		String out = "exitx " + exitx + " \r\n";
		out += "exity " + exity + " \r\n";
		out += "numloogs " + numloogs + " \r\n";
		out += "loogcnt " + loogcnt + " \r\n";
		out += "posx " + posx + " \r\n";
		out += "posy " + posy + " \r\n";
		out += "posz " + posz + " \r\n";
		out += "horiz " + horiz + " \r\n";
		out += "ohoriz " + ohoriz + " \r\n";
		out += "ohorizoff " + ohorizoff + " \r\n";
		out += "invdisptime " + invdisptime + " \r\n";
		out += "bobposx " + bobposx + " \r\n";
		out += "bobposy " + bobposy + " \r\n";
		out += "oposx " + oposx + " \r\n";
		out += "oposy " + oposy + " \r\n";
		out += "oposz " + oposz + " \r\n";
		out += "pyoff " + pyoff + " \r\n";
		out += "opyoff " + opyoff + " \r\n";
		out += "posxv " + posxv + " \r\n";
		out += "posyv " + posyv + " \r\n";
		out += "poszv " + poszv + " \r\n";
		out += "last_pissed_time " + last_pissed_time + " \r\n";
		out += "truefz " + truefz + " \r\n";
		out += "truecz " + truecz + " \r\n";
		out += "player_par " + player_par + " \r\n";
		out += "bobcounter " + bobcounter + " \r\n";
		out += "weapon_sway " + weapon_sway + " \r\n";
		out += "pals_time " + pals_time + " \r\n";
		out += "randomflamex " + randomflamex + " \r\n";
		out += "crack_time " + crack_time + " \r\n";
		out += "aim_mode " + aim_mode + " \r\n";
		out += "auto_aim " + auto_aim + " \r\n";
		out += "ang " + ang + " \r\n";
		out += "oang " + oang + " \r\n";
		out += "angvel " + angvel + " \r\n";
		out += "cursectnum " + cursectnum + " \r\n";
		out += "look_ang " + look_ang + " \r\n";
		out += "last_extra " + last_extra + " \r\n";
		out += "subweapon " + subweapon + " \r\n";
		out += "wackedbyactor " + wackedbyactor + " \r\n";
		out += "frag " + frag + " \r\n";
		out += "fraggedself " + fraggedself + " \r\n";
		for(int i = 0; i < MAX_WEAPONS; i++)
		{
			out += "ammo_amount[" + i + "] " + ammo_amount[i] + " \r\n";
		}
		out += "curr_weapon " + curr_weapon + " \r\n";
		out += "last_weapon " + last_weapon + " \r\n";
		out += "tipincs " + tipincs + " \r\n";
		out += "horizoff " + horizoff + " \r\n";
		out += "wantweaponfire " + wantweaponfire + " \r\n";
		out += "holoduke_amount " + holoduke_amount + " \r\n";
		out += "newowner " + newowner + " \r\n";
		out += "hurt_delay " + hurt_delay + " \r\n";
		out += "hbomb_hold_delay " + hbomb_hold_delay + " \r\n";
		out += "jumping_counter " + jumping_counter + " \r\n";
		out += "airleft " + airleft + " \r\n";
		out += "knee_incs " + knee_incs + " \r\n";
		out += "access_incs " + access_incs + " \r\n";
		out += "access_wallnum " + access_wallnum + " \r\n";
		out += "access_spritenum " + access_spritenum + " \r\n";
		out += "kickback_pic " + kickback_pic + " \r\n";
		out += "got_access " + got_access + " \r\n";
		out += "weapon_ang " + weapon_ang + " \r\n";
		out += "firstaid_amount " + firstaid_amount + " \r\n";
		out += "somethingonplayer " + somethingonplayer + " \r\n";
		out += "on_crane " + on_crane + " \r\n";
		out += "i " + i + " \r\n";
		out += "one_parallax_sectnum " + one_parallax_sectnum + " \r\n";
		out += "random_club_frame " + random_club_frame + " \r\n";
		out += "fist_incs " + fist_incs + " \r\n";
		out += "one_eighty_count " + one_eighty_count + " \r\n";
		out += "cheat_phase " + cheat_phase + " \r\n";
		out += "dummyplayersprite " + dummyplayersprite + " \r\n";
		out += "extra_extra8 " + extra_extra8 + " \r\n";
		out += "quick_kick " + quick_kick + " \r\n";
		out += "heat_amount " + heat_amount + " \r\n";
		out += "actorsqu " + actorsqu + " \r\n";
		out += "timebeforeexit " + timebeforeexit + " \r\n";
		out += "customexitsound " + customexitsound + " \r\n";
		for(int i = 0; i < 16; i++)
		{
			out += "weaprecs[" + i + "] " + weaprecs[i] + " \r\n";
		}
		out += "weapreccnt " + weapreccnt + " \r\n";
		out += "interface_toggle_flag " + interface_toggle_flag + " \r\n";
		out += "rotscrnang " + rotscrnang + " \r\n";
		out += "dead_flag " + dead_flag + " \r\n";
		out += "show_empty_weapon " + show_empty_weapon + " \r\n";
		out += "scuba_amount " + scuba_amount + " \r\n";
		out += "jetpack_amount " + jetpack_amount + " \r\n";
		out += "steroids_amount " + steroids_amount + " \r\n";
		out += "shield_amount " + shield_amount + " \r\n";
		out += "holoduke_on " + holoduke_on + " \r\n";
		out += "pycount " + pycount + " \r\n";
		out += "weapon_pos " + weapon_pos + " \r\n";
		out += "frag_ps " + frag_ps + " \r\n";
		out += "transporter_hold " + transporter_hold + " \r\n";
		out += "last_full_weapon " + last_full_weapon + " \r\n";
		out += "footprintshade " + footprintshade + " \r\n";
		out += "boot_amount " + boot_amount + " \r\n";
		out += "scream_voice " + scream_voice + " \r\n";

		out += "on_warping_sector " + on_warping_sector + " \r\n";
		out += "footprintcount " + footprintcount + " \r\n";
		out += "hbomb_on " + hbomb_on + " \r\n";
		out += "jumping_toggle " + jumping_toggle + " \r\n";
		out += "rapid_fire_hold " + rapid_fire_hold + " \r\n";
		out += "on_ground " + on_ground + " \r\n";
		out += "inven_icon " + inven_icon + " \r\n";
		out += "buttonpalette " + buttonpalette + " \r\n";
		out += "jetpack_on " + jetpack_on + " \r\n";
		out += "spritebridge " + spritebridge + " \r\n";
		out += "lastrandomspot " + lastrandomspot + " \r\n";
		out += "scuba_on " + scuba_on + " \r\n";
		out += "footprintpal " + footprintpal + " \r\n";
		out += "heat_on " + heat_on + " \r\n";
		out += "holster_weapon " + holster_weapon + " \r\n";
		out += "falling_counter " + falling_counter + " \r\n";
		for(int i = 0; i < MAX_WEAPONS; i++)
		{
			out += "gotweapon[" + i + "] " + gotweapon[i] + " \r\n";
		}
		out += "refresh_inventory " + refresh_inventory + " \r\n";
		out += "palette " + game.net.Checksum(palette,768) + " \r\n";
		out += "toggle_key_flag " + toggle_key_flag + " \r\n";
		out += "knuckle_incs " + knuckle_incs + " \r\n";
		out += "walking_snd_toggle " + walking_snd_toggle + " \r\n";
		out += "palookup " + palookup + " \r\n";
		out += "hard_landing " + hard_landing + " \r\n";
		out += "max_secret_rooms " + max_secret_rooms + " \r\n";
		out += "secret_rooms " + secret_rooms + " \r\n";
		for(int i = 0; i < 3; i++)
		{
			out += "pals[" + i + "] " + pals[i] + " \r\n";
		}
		out += "max_actors_killed " + max_actors_killed + " \r\n";
		out += "actors_killed " + actors_killed + " \r\n";
		out += "return_to_center " + return_to_center + " \r\n";
				
		out += "last_used_weapon " + last_used_weapon + " \r\n";
		out += "crouch_toggle " + crouch_toggle + " \r\n";

		return out;
	}
}
