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

package ru.m210projects.Duke3D;

import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Duke3D.Names.*;
import static ru.m210projects.Duke3D.Sounds.*;

import java.util.HashMap;
import ru.m210projects.Duke3D.Types.SoundOwner;
import ru.m210projects.Duke3D.Types.Animwalltype;
import ru.m210projects.Duke3D.Types.PlayerOrig;
import ru.m210projects.Duke3D.Types.PlayerStruct;
import ru.m210projects.Duke3D.Types.Sample;
import ru.m210projects.Duke3D.Types.UserDefs;
import ru.m210projects.Duke3D.Types.Weaponhit;
import ru.m210projects.Duke3D.Types.GameInfo;
import ru.m210projects.Duke3D.Types.NetInfo;

public class Globals {
	
	public static final int BACKBUTTON = 9216;
	public static final int MOUSECURSOR = 9217;
	public static final int ALTHUDLEFT = 9218;
	public static final int ALTHUDRIGHT = 9219;
	
	public static final int HUDARMOR = 9220;
	public static final int HUDKEYS = 9221;
	
	public static String boardfilename;
	public static boolean mFakeMultiplayer;
	public static int nFakePlayers;
	
	public static final int BYTEVERSION15 = 116;
	public static final int JFBYTEVERSION = 127;
	public static final int GDXBYTEVERSION = 147;
	
	public static final int BYTEVERSION = GDXBYTEVERSION;
	
	public static HashMap<String, GameInfo> episodes = new HashMap<String, GameInfo>();
	public static GameInfo defGame;
	public static GameInfo currentGame;
	public static NetInfo pNetInfo = new NetInfo();
	
	public static int musicvolume, musiclevel;
	public static boolean kGameCrash;
	
	public static final int nMaxMaps = 11;
	public static final int nMaxEpisodes = 5;
	public static final int nMaxSkills = 5;

	public static final int TICRATE = 120;
	public static final int TICSPERFRAME = (TICRATE/26);
	public static final int TIMERUPDATESIZ = 16;
	
	public static final int kMaxTiles = MAXTILES - USERTILES;
	public static final int kUserTiles = kMaxTiles;
	
	public static final int TILE_LOADSHOT = MAXTILES - 2;
	public static final int TILE_ANIM = MAXTILES - 3;
	public static final int TILE_VIEWSCR = MAXTILES - 4;
	
	public static int VIEWSCR_Lock = 199;
	public static int[] zofslope = new int[2];
	
	public static final int MAX_WEAPONS = 13;

	// Defines weapon, not to be used with the 'shoot' keyword.;
	public static final int KNEE_WEAPON         = 0;
	public static final int PISTOL_WEAPON       = 1;
	public static final int SHOTGUN_WEAPON      = 2;
	public static final int CHAINGUN_WEAPON     = 3;
	public static final int RPG_WEAPON          = 4;
	public static final int HANDBOMB_WEAPON     = 5;
	public static final int SHRINKER_WEAPON     = 6;
	public static final int DEVASTATOR_WEAPON   = 7;
	public static final int TRIPBOMB_WEAPON     = 8;
	public static final int FREEZE_WEAPON       = 9;
	public static final int HANDREMOTE_WEAPON   = 10;
	public static final int EXPANDER_WEAPON         = 11;
	public static final int INCINERATOR_WEAPON = 12; //Twentieth Anniversary World Tour

	public static final int MAXANIMWALLS = 512;
	public static final int NUMOFFIRSTTIMEACTIVE = 256;
	
	public static final int MAXCYCLERS = 256;
	
	public static final int RECSYNCBUFSIZ = 2520; //2520 is the (LCM of 1-8)*3
	
	public static final int FOURSLEIGHT = 1 << 8;
	public static final int PHEIGHT = (38<<8);

	public static final int MAXSLEEPDIST = 16384;
	public static final int SLEEPTIME = 24*64;
	
	public static final int AUTO_AIM_ANGLE = 48;

	// These tile positions are reserved!;
	public static final int RESERVEDSLOT1 = 6132;
	public static final int RESERVEDSLOT2 = 6133;
	public static final int RESERVEDSLOT3 = 6134;
	public static final int RESERVEDSLOT4 = 6135;
	public static final int RESERVEDSLOT5 = 6136;
	public static final int RESERVEDSLOT6 = 6137;
	public static final int RESERVEDSLOT7 = 6138;
	public static final int RESERVEDSLOT8 = 6139;
	public static final int RESERVEDSLOT9 = 6140;
	public static final int RESERVEDSLOT10 = 6141;
	public static final int RESERVEDSLOT11 = 6142;
	public static final int RESERVEDSLOT12 = 6143;

	public static int gVisibility;
	
	public static int uGameFlags = 0;
	public static final int MODE_EOL        = 1;
	public static final int MODE_END        = 2;
//	public static final int MODE_RESTART    = 4;

	public static boolean MODE_TYPE;

	// Some misc public static final ints;
	public static final int NO       = 0;
	public static final int YES      = 1;

	public static final int GET_STEROIDS     = 0;
	public static final int GET_SHIELD       = 1;
	public static final int GET_SCUBA        = 2;
	public static final int GET_HOLODUKE     = 3;
	public static final int GET_JETPACK      = 4;
	public static final int GET_ACCESS       = 6;
	public static final int GET_HEATS        = 7;
	public static final int GET_FIRSTAID     = 9;
	public static final int GET_BOOTS       = 10;
	
	public static PlayerOrig[] po = new PlayerOrig[MAXPLAYERS];
	public static PlayerStruct[] ps = new PlayerStruct[MAXPLAYERS];
	public static Weaponhit[] hittype = new Weaponhit[MAXSPRITES];
	public static UserDefs ud = new UserDefs();
	
	// Hit definitions
	public static final int kHitTypeMask	= 0xE000;
	public static final int kHitIndexMask	= 0x1FFF;
	public static final int kHitSector		= 0x4000;
	public static final int kHitWall		= 0x8000;
	public static final int kHitSprite		= 0xC000;
	
	public static final int kAngleMask = 0x7FF;

	public static short global_random;
	public static short neartagsector, neartagwall, neartagsprite;

	public static int numframes, neartaghitdist,lockclock;

	public static short[] spriteq = new short[1024];
    public static short spriteqloc;
    public static short moustat;
	public static Animwalltype[] animwall = new Animwalltype[MAXANIMWALLS];
	public static short numanimwalls;

	public static int[] msx = new int[2048],msy = new int[2048];
	public static short[][] cyclers = new short[MAXCYCLERS][6];
    public static short numcyclers;

	public static char[] buf = new char[80];

	public static short camsprite;
	public static short[] mirrorwall = new short[64];
    public static short[] mirrorsector = new short[64];
    public static short mirrorcnt;

	public static int checksume;
	public static int[] soundsiz = new int[NUM_SOUNDS];

	public static short title_zoom;

	public static Sample[] Sound = new Sample[ NUM_SOUNDS ];
	public static SoundOwner[][] SoundOwner = new SoundOwner[NUM_SOUNDS][4];

	public static short numplayersprites,loadfromgrouponly,earthquaketime;

	public static int fricxv,fricyv;
	public static Input[] sync = new Input[MAXPLAYERS];

	  //Multiplayer syncing variables
	public static short screenpeek;

	 //Game recording variables
	public static int groupfile;

	public static char display_mirror,typebuflen;
	public static byte[] tempbuf = new byte[2048];
	
	public static final short[] weaponsandammosprites = {
	        RPGSPRITE,
	        CHAINGUNSPRITE,
	        DEVISTATORAMMO,
	        RPGAMMO,
	        RPGAMMO,
	        JETPACK,
	        SHIELD,
	        FIRSTAID,
	        STEROIDS,
	        RPGAMMO,
	        RPGAMMO,
	        RPGSPRITE,
	        RPGAMMO,
	        FREEZESPRITE,
	        FREEZEAMMO
	    };

	        //GLOBAL.C - replace the end "my's" with this
	public static short[][] frags = new short[MAXPLAYERS][MAXPLAYERS];
	public static byte multiwho, multipos, multiwhat, multiflag;
	
	public static char everyothertime, gamequit = 0;
	public static short numclouds;
    public static short[] clouds = new short[128];
    public static short[] cloudx = new short[128];
    public static short[] cloudy = new short[128];
	public static int cloudtotalclock = 0,totalmemory = 0;
	public static int startofdynamicinterpolations = 0;
}
