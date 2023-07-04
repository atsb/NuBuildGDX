package ru.m210projects.Tekwar;

public class Globals {

	
	public static final int BACKBUTTON = 9216;
	public static final int BACKSCALE = 16384;
	public static final int MOUSECURSOR = 9217;
	public static final int RESERVED1 = 9218;
	public static final int RESERVED2 = 9219;
	
	public static int gDifficulty = 0;
	public static boolean kGameCrash = false;
	public static final int kNetModeOff = 0;
	public static final int kNetModeCoop = 1;
	public static final int kNetModeBloodBath = 2;
	public static final int kNetModeTeams = 3;

	public static char notininventory;
	public static int noenemiesflag;
	public static int noguardflag;
	public static int nostalkflag;
	public static int nochaseflag;
	public static int nostrollflag;

	public static final int GUN1FLAG = 1;
	public static final int GUN2FLAG = 2;
	public static final int GUN3FLAG = 3;
	public static final int GUN4FLAG = 4;
	public static final int GUN5FLAG = 5;
	public static final int GUN6FLAG = 6;
	public static final int GUN7FLAG = 7;
	public static final int GUN8FLAG = 8;

	public static final int ST_NULL = 0x0000;
	public static final int ST_IMMEDIATE = 0x0001;
	public static final int ST_UPDATE = 0x0002;
	public static final int ST_NOUPDATE = 0x0004;
	public static final int ST_UNIQUE = 0x0008;
	public static final int ST_DELAYED = 0x0010;
	public static final int ST_VARPITCH = 0x0020;
	public static final int ST_BACKWARDS = 0x0040;
	public static final int ST_AMBUPDATE = 0x0060;
	public static final int ST_VEHUPDATE = 0x0080;
	public static final int ST_TOGGLE = 0x0100;

	public static final int BETA = 1;
	public static final int DEBUG = 1;
	public static final int CLKIPS = 120;
	public static final int DOOMGUY = 999;
	public static int KENSPLAYERHEIGHT = 34;
	public static final int TICSPERFRAME = 3;
	public static final int MOVEFIFOSIZ = 256;

	public static final int NUMOPTIONS = 8;
	public static final int NUMKEYS = 32;
	public static final int MAXMOREOPTIONS = 21;
	public static final int MAXTOGGLES = 6;
	public static final int MAXGAMESTUFF = 16;

	public static final int MAXHEALTH = 1000;
	public static final int MAXSTUN = 1000;
	public static final short MAXAMMO = 100;
}
