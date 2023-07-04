package ru.m210projects.Witchaven;

import static java.lang.Math.max;
import static ru.m210projects.Build.Engine.MAXSECTORS;
import static ru.m210projects.Build.Engine.MAXTILES;
import static ru.m210projects.Build.Engine.MAXWALLS;
import static ru.m210projects.Build.Engine.MAXXDIM;
import static ru.m210projects.Build.Engine.USERTILES;

import java.util.HashMap;

import ru.m210projects.Witchaven.Types.EpisodeInfo;

public class Globals {

	public static EpisodeInfo gOriginalEpisode;
	public static EpisodeInfo gCurrentEpisode;
	public static HashMap<String, EpisodeInfo> episodes = new HashMap<String, EpisodeInfo>();

	public static final int BACKBUTTON = 9216;
	public static final int BACKSCALE = 16384;
	public static final int MOUSECURSOR = 9217;
	public static final int RESERVED1 = 9218;
	public static final int RESERVED2 = 9219;
	
	public static final int WHLOGO = 9220;
	public static final int MENUFONT = 9221; //+32
	
	public static final int MINITEXTPOINT = 9254;
	public static final int MINITEXTSLASH = 9255;
	
	public static final int WH2FONTBIGLETTERS = 9256;
	public static final int WH2FONTLILLETTERS = 9282;
	public static final int WH2FONTNUMBERS = 9308;
	public static final int WH2LOGO = 9335;
	public static final int WH2FONTBACKGROUND = 9336;
	
	public static String boardfilename;

	public static boolean followmode = false;
	public static int followx, followy;
	public static int followvel, followsvel, followa;
	public static float followang;
	public static int dimension = 0;
	
	public static int hours, minutes, seconds, fortieth;

	public static int kills;
	public static int killcnt;
	public static int treasurescnt;
	public static int treasuresfound;
	public static int expgained;
	public static int sparksx, sparksy, sparksz;
	public static int playertorch = 0;

	public static final int ARROWCOUNTLIMIT = 100;
	public static final int THROWPIKELIMIT = 100;
	
	public static final int GRAVITYCONSTANT = (4 << 7) - 32;
	public static final int JUMPVEL = (4 << 10) - 256;
	
	public static final int WH2GRAVITYCONSTANT = (8 << 5);
	public static final int WH2JUMPVEL = (6 << 10); //(8 << 10)
	public static final int GROUNDBIT = 1;
	public static final int PLATFORMBIT = 2;

	public static short[] arrowsprite = new short[ARROWCOUNTLIMIT], throwpikesprite = new short[THROWPIKELIMIT];

	public static short[] floormirrorsector = new short[64];
	public static int floormirrorcnt;

	public static byte[] ceilingshadearray = new byte[MAXSECTORS];
	public static byte[] floorshadearray = new byte[MAXSECTORS];
	public static byte[] wallshadearray = new byte[MAXWALLS];

	public static int difficulty = 2;
	public static boolean netgame;

	public static final int TICSPERFRAME = 3;
	public static final int TIMERRATE = 120;

	public static int lockclock;

	public static byte[] tempbuf = new byte[max(576, MAXXDIM)];
	public static char[] tempchar = new char[max(576, MAXXDIM)];

	public static final int kExtraTile = MAXTILES - USERTILES;

	// Hit definitions
	public static final int kHitTypeMask = 0xE000;
	public static final int kHitIndexMask = 0x1FFF;
	public static final int kHitFloor = 0x4000;
	public static final int kHitSector = 0x4000;
	public static final int kHitCeiling = 0x6000;
	public static final int kHitWall = 0x8000;
	public static final int kHitSprite = 0xC000;

	public static final int PLAYERHEIGHT = 48;
	public static final int WH2PLAYERHEIGHT = 64;
	public static final int MAXNUMORBS = 8;
	public static final int MAXHEALTH = 100;
	public static final int MAXKEYS = 4;
	public static final int MAXTREASURES = 18;

	public static final int LOW = 1;
	public static final int HIGH = 2;

	public static final int TOP = 1;
	public static final int BOTTOM = 2;

	public static final short INACTIVE = 0;
	public static final short PATROL = 1;
	public static final short CHASE = 2;
	public static final short AMBUSH = 3;
	public static final short BIRTH = 4;
	public static final short DODGE = 5;
	public static final short ATTACK = 6;
	public static final short DEATH = 7;
	public static final short STAND = 8;

	public static final short MISSILE = 100;
	public static final short FX = 101;
	public static final short HEATSEEKER = 102;
	public static final short YELL = 103;
	public static final short CAST = 104;
	public static final short PUSH = 105;
	public static final short FALL = 106;
	public static final short DIE = 107;
	public static final short DEAD = 108;
	public static final short FACE = 109;
	public static final short SHOVE = 110;
	public static final short SHATTER = 111;
	public static final short FIRE = 112;
	public static final short LIFTUP = 113;
	public static final short LIFTDN = 114;
	public static final short PENDULUM = 115;
	public static final short RESURECT = 116;
	public static final short BOB = 117;
	public static final short SHOVER = 118;
	public static final short TORCHER = 119;
	public static final short MASPLASH = 120;
	public static final short CHUNKOMEAT = 121;
	public static final short FLEE = 122;
	public static final short DORMANT = 123;
	public static final short ACTIVE = 124;
	public static final short ATTACK2 = 125;
	public static final short WITCHSIT = 126;
	public static final short CHILL = 127;
	public static final short SKIRMISH = 128;
	public static final short FLOCK = 129;
	public static final short FLOCKSPAWN = 130;
	public static final short PAIN = 131;
	public static final short WAR = 132;
	public static final short TORCHLIGHT = 133;
	public static final short GLOWLIGHT = 134;
	public static final short BLOOD = 135;
	public static final short DRIP = 136;
	public static final short DEVILFIRE = 137;
	public static final short FROZEN = 138;
	public static final short PULLTHECHAIN = 139;
	public static final short FLOCKCHIRP = 140;
	public static final short CHUNKOWALL = 141;
	public static final short FINDME = 142;
	public static final short DRAIN = 143;
	public static final short RATRACE = 144;
	public static final short SMOKE = 145;
	public static final short EXPLO = 146;
	public static final short JAVLIN = 147;
	public static final short ANIMLEVERUP = 148;
	public static final short ANIMLEVERDN = 149;
	public static final short BROKENVASE = 150;
	public static final short NUKED = 151;
	public static final short WARPFX = 152;
	public static final short PATROLFLAG = 153;
	public static final short PLACECONE = 154;
	public static final short REMOVECONE = 155;
	public static final short FIRSTCONE = 156;
	public static final short GOTOCONE = 157;
	public static final short TORCHFRONT = 158;
	public static final short APATROLPOINT = 159;
	public static final short SHADE = 160;
	public static final short EVILSPIRIT = 161;
	public static final short STONETOFLESH = 162;
	public static final short SPARKS = 163;
	public static final short SPARKSUP = 164;
	public static final short SPARKSDN = 165;
	public static final short LAND = 166;
	public static final short SHARDOFGLASS = 167;
	public static final short FIRECHUNK = 168;
	public static final short DUDE = 170;
	
	public static final int   ACTIVATESECTOR  =    1;
	public static final int   ACTIVATESECTORONCE = 2;

	public static final int   DOORUPTAG   =   6;
	public static final int   DOORDOWNTAG  =  7;
	public static final int   DOORSPLITHOR =  8;
	public static final int   DOORSPLITVER =  9;
	public static final int   DOORSWINGTAG =  13;
	public static final int   DOORBOX     =   16;

	public static final int   PLATFORMELEVTAG = 1000;
	public static final int   BOXELEVTAG    = 1003;

	public static final int   SECTOREFFECT  = 104;
	public static final int   PULSELIGHT    = 0;
	public static final int   FLICKERLIGHT  = 1;
	public static final int   DELAYEFFECT  =  2;
	public static final int   XPANNING     =  3;

	public static final int   DOORDELAY    =  480; // 4 second delay for doors to close
	public static final int   DOORSPEED    =  128;
	public static final int   ELEVSPEED   =   256;

	public static final int   PICKDISTANCE =  512; // for picking up sprites
	public static final int   PICKHEIGHT  =   40;
	public static final int   PICKHEIGHT2  =  64;

	public static final int   JETPACKPIC   =  93 ;  // sprites available to pick up

	public static final int   MAXSWINGDOORS = 32;
	
	public static final int   JETPACKITEM =   0;
	public static final int   SHOTGUNITEM =   1;

	public static final int   SHOTGUNPIC   =  101;
	public static final int   SHOTGUNVIEW  =  102;

	public static final int   KILLSECTOR = 4444;
	
	public static final int TDIAMONDRING = 0; //armortype3 200units
	public static final int TSHADOWAMULET = 1; //shadow
	public static final int TGLASSSKULL = 2; //add score
	public static final int TAHNK = 3; //health 250units
	public static final int TBLUESCEPTER = 4; //lava walk
	public static final int TYELLOWSCEPTER = 5; //water walk
	public static final int TADAMANTINERING = 6; //unfinity attack protection
	public static final int TONYXRING = 7; //projectiles protection
	public static final int TPENTAGRAM = 8; //exit
	public static final int TCRYSTALSTAFF = 9; //health 250units armortype2 200units
	public static final int TAMULETOFTHEMIST = 10; //invisible
	public static final int THORNEDSKULL = 11; //end game
	public static final int TTHEHORN = 12; //vampire
	public static final int TSAPHIRERING = 13; //set armortype to 3 
	public static final int TBRASSKEY = 14;
	public static final int TBLACKKEY = 15;
	public static final int TGLASSKEY = 16;
	public static final int TIVORYKEY = 17;
}
