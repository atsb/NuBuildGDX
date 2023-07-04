// This file is part of RedneckGDX.
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

package ru.m210projects.Redneck;

import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Redneck.Names.*;
import static ru.m210projects.Redneck.Sounds.*;

import java.util.HashMap;
import ru.m210projects.Redneck.Types.NetInfo;
import ru.m210projects.Redneck.Types.SoundOwner;
import ru.m210projects.Redneck.Types.GameInfo;
import ru.m210projects.Redneck.Types.Animwalltype;
import ru.m210projects.Redneck.Types.PlayerOrig;
import ru.m210projects.Redneck.Types.PlayerStruct;
import ru.m210projects.Redneck.Types.Sample;
import ru.m210projects.Redneck.Types.UserDefs;
import ru.m210projects.Redneck.Types.Weaponhit;

public class Globals {
	
	public static final int BACKBUTTON = 9216;
	public static final int MOUSECURSOR = 9217;
	public static final int RESERVED1 = 9218;
	public static final int RESERVED2 = 9219;
	
	public static final int KEYSIGN = 9220;
	public static final int GUTSMETTER = 9221;
	public static final int KILLSSIGN = 9222;
	
	public static final int WIDEHUD_LEFTSHADOW = 9223;
	public static final int WIDEHUD_RIGHTSHADOW = 9224;
	
	public static final int WIDEHUD_PART1 = 9225;
	public static final int WIDEHUD_PART2 = 9226;
	
	public static final int RR = 0;
	public static final int RR66 = 1;
	public static final int RRRA = 2;
	
	public static String boardfilename;
	public static boolean mFakeMultiplayer;
	public static int nFakePlayers;
	
	public static final int BYTEVERSIONRR = 108;
	public static final int GDXBYTEVERSION = 147;
	
	public static final int BYTEVERSION = GDXBYTEVERSION;
	
	public static HashMap<String, GameInfo> episodes = new HashMap<String, GameInfo>();
	public static GameInfo defGame;
	public static GameInfo currentGame;
	public static NetInfo pNetInfo = new NetInfo();

	public static final int TICRATE = 120;
	public static final int TICSPERFRAME = (TICRATE/26);
	public static final int TIMERUPDATESIZ = 16;
			
	public static final int kMaxTiles = MAXTILES - USERTILES;
	public static final int kUserTiles = kMaxTiles;
	
	public static final int TILE_ANIM = MAXTILES - 3;
	public static final int TILE_VIEWSCR = MAXTILES - 4;
	public static final short ANIM_PAL = (short) (NORMALPAL - 1);
	
	public static int VIEWSCR_Lock = 199;
	public static int[] zofslope = new int[2];
	
	//XXX RA
	public static short BellTime;
	public static int BellSound;
	public static short word_119BE0;
	public static int WindDir;
	public static int WindTime;
	public static int mamaspawn_count;
	public static int fakebubba_spawn;
	public static int dword_119C08;

	public static final int MAX_WEAPONS = 13;
	public static final int MAX_WEAPONSRA = 17;
	
	public static final int MAXANIMWALLS = 512;
	public static final int NUMOFFIRSTTIMEACTIVE = 192;
	
	public static final int MAXCYCLERS = 256;
	
	public static final int RECSYNCBUFSIZ = 2520; //2520 is the (LCM of 1-8)*3

	public static final int MOVEFIFOSIZ = 256;
	
	public static final int FOURSLEIGHT = 1 << 8;
	public static final int PHEIGHT = 10240;
	
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

	public static int uGameFlags = 0;
	public static final int MODE_EOL        = 1;
	public static final int MODE_END        = 2;
	public static boolean MODE_TYPE; //== 16
	
	public static int gVisibility;
	
	public static String loading_mapname;
	public static int loading_type;
	public static boolean loading_usermap;

	public static final int GET_STEROIDS     = 0;
	public static final int GET_SHIELD       = 1;
	public static final int GET_SCUBA        = 2;
	public static final int GET_HOLODUKE     = 3;
	public static final int GET_JETPACK      = 4;
	public static final int GET_ACCESS       = 6;
	public static final int GET_HEATS        = 7;
	public static final int GET_FIRSTAID     = 9;
	public static final int GET_BOOTS       = 10;
	
	
	// Defines weapon, not to be used with the 'shoot' keyword.;
	public static final int KNEE_WEAPON         = 0;
	public static final int PISTOL_WEAPON       = 1;
	public static final int SHOTGUN_WEAPON      = 2;
	public static final int RIFLEGUN_WEAPON     = 3;
	public static final int DYNAMITE_WEAPON     = 4;
	public static final int CROSSBOW_WEAPON     = 5;
	public static final int THROWSAW_WEAPON     = 6;
	public static final int ALIENBLASTER_WEAPON = 7;
	public static final int POWDERKEG_WEAPON    = 8;
	public static final int TIT_WEAPON       	= 9;
	public static final int HANDREMOTE_WEAPON   = 10;
	public static final int BUZSAW_WEAPON       = 11;
	public static final int BOWLING_WEAPON      = 12;
	public static final int MOTO_WEAPON      = 13;
	public static final int BOAT_WEAPON      = 14;
	public static final int RATE_WEAPON      = 15;
	public static final int CHICKENBOW_WEAPON  = 16;
	
	public static boolean kGameCrash;
	public static int musicvolume, musiclevel;
	
	public static int LeonardCrack;
	
	public static PlayerOrig po[] = new PlayerOrig[MAXPLAYERS];
	public static PlayerStruct ps[] = new PlayerStruct[MAXPLAYERS];
	public static Weaponhit[] hittype = new Weaponhit[MAXSPRITES];
	public static UserDefs ud = new UserDefs();
	
	// Hit definitions
	public static final int kHitTypeMask	= 0xE000;
	public static final int kHitIndexMask	= 0x1FFF;
	public static final int kHitSector		= 0x4000;
	public static final int kHitWall		= 0x8000;
	public static final int kHitSprite		= 0xC000;
	
	public static short global_random;
	public static short neartagsector, neartagwall, neartagsprite;

	public static int numframes,neartaghitdist,lockclock;

	public static short spriteq[] = new short[1024],spriteqloc,moustat;
	public static Animwalltype animwall[] = new Animwalltype[MAXANIMWALLS];
	public static short numanimwalls;
	public static int[] msx = new int[2048],msy = new int[2048];
	public static short cyclers[][] = new short[MAXCYCLERS][6], numcyclers;

	public static char[] buf = new char[80];

	public static short camsprite;
	public static short mirrorwall[] = new short[64], mirrorsector[] = new short[64], mirrorcnt;

	public static final int nMaxMaps = 11;
	public static final int nMaxEpisodes = 3;
	public static final int nMaxSkills = 5;
	
	public static int checksume;
	public static int[] soundsiz = new int[NUM_SOUNDS];
	public static short title_zoom;

	public static Sample[] Sound = new Sample[ NUM_SOUNDS ];
	public static SoundOwner[][] SoundOwner = new SoundOwner[NUM_SOUNDS][4];

	public static short numplayersprites,loadfromgrouponly,earthquaketime;

	public static int fricxv,fricyv;

	public static Input sync[] = new Input[MAXPLAYERS];
	
	  //Multiplayer syncing variables
	public static short screenpeek;

	public static int groupfile;

	public static char display_mirror,typebuflen;
	public static byte[] tempbuf = new byte[2048];
	
	public static final short weaponsandammosprites[] = {
		CROSSBOWSPRITE,
		RIFLESPRITE,
		ALIENBLASTERAMMO,
		44,
		44,
		COWPIE,
		54,
		WHISKEY,
		MOONSHINE,
		44,
		44,
		CROSSBOWSPRITE,
		44,
		TEATGUN,
		37,
	};

	public static short frags[][] = new short[MAXPLAYERS][MAXPLAYERS];
	public static byte multiwho, multipos, multiwhat, multiflag;
	public static char everyothertime, gamequit = 0;
	
	public static int totalmemory = 0;
	public static int startofdynamicinterpolations = 0;
	
	public static final int kAngleMask = 0x7FF;
	
	public static final int kAngle5 = 28;
	public static final int kAngle15 = 85;
	public static final int kAngle30 = 170;
	public static final int kAngle45 = 256;
	public static final int kAngle60 = 341;
	public static final int kAngle90 = 512;
	public static final int kAngle120 = 682;
	public static final int kAngle180 = 1024;
	public static final int kAngle360 = 2048;
}
