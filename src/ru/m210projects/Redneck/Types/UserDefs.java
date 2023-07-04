// This file is part of RedneckGDX
// Copyright (C) 2017-2018  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// RedneckGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// RedneckGDX is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with RedneckGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.Redneck.Types;

import ru.m210projects.Redneck.Config;

import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Redneck.Globals.MAX_WEAPONSRA;

import ru.m210projects.Build.Settings.BuildSettings;

public class UserDefs {
	public int cashman,eog,showallmap;
	public boolean god,scrollmode,clipping;
	public String[] user_name = new String[MAXPLAYERS];
	public char[][] ridecule = new char[10][];
	public String[] savegame = new String[10];
	public char[] pwlockout = new char[128];
	public String rtsname;
	public int overhead_on,last_overhead,showweapons;

	public int /*pause_on,*/from_bonus;
	public int camerasprite = -1,last_camsprite;
	public int last_level,secretlevel;

	public int camera_time,folfvel,folx,foly,fola;
	public DemoFile rec;
	public float folavel;

	public int entered_name,screen_tilting = 1,shadows,fta_on = 1,executions,auto_run;
	public int coords,coop,screen_size=2,lockout,crosshair=1, playerai;
	public int[][] wchoice = new int[MAXPLAYERS][MAX_WEAPONSRA];

	public int recstat,brightness, m_recstat, detail;
	public boolean monsters_off;
	public boolean respawn_monsters,respawn_items,respawn_inventory;
	public int ffire,multimode;
	public int player_skill,level_number,volume_number,marker;
	
	public void setDefaults(Config cfg)
	{
		shadows = 1;
		detail = 1;
		lockout = 0;
		pwlockout[0] = '\0';

		rtsname = "REDNECK.RTS";

	    ridecule[0] = "Yer as ugly as mud fence! \0".toCharArray();
	    ridecule[1] = "Duck you pecker-head! \0".toCharArray();
	    ridecule[2] = "You like that boy? \0".toCharArray();
	    ridecule[3] = "Yer lower that catfish crap! \0".toCharArray();
	    ridecule[4] = "Eat lead, you shit monkey! \0".toCharArray();
	    ridecule[5] = "You dumb-ass! \0".toCharArray();
	    ridecule[6] = "Yer slower'n a three legged dog! \0".toCharArray();
	    ridecule[7] = "Come on...Squeal like a pig! \0".toCharArray();
	    ridecule[8] = "Haw, haw, haw! \0".toCharArray();
	    ridecule[9] = "Now you gone and done it! \0".toCharArray();
	    
	    wchoice[0][0] = 3;
        wchoice[0][1] = 4;
        wchoice[0][2] = 5;
        wchoice[0][3] = 7;
        wchoice[0][4] = 8;
        wchoice[0][5] = 6;
        wchoice[0][6] = 0;
        wchoice[0][7] = 2;
        wchoice[0][8] = 9;
        wchoice[0][9] = 1;
	    
	    screen_size = cfg.screen_size;
	    crosshair = cfg.crosshair;
	    screen_tilting = cfg.screen_tilting;
	    auto_run = cfg.auto_run;
	    fta_on = cfg.fta_on;
	    
	    brightness = BuildSettings.paletteGamma.get() << 2;
	}
}
