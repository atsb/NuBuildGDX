package ru.m210projects.Wang;

public class Names {

	public static final int FONT_LARGE_ALPHA = 3706;
	public static final int FONT_LARGE_DIGIT = 3732;

	public static final int STAT_SCREEN_PIC = 5114;
	public static final int TITLE_PIC = 2324;
	public static final int THREED_REALMS_PIC = 2325;
	public static final int PAL_SIZE = (256 * 3);

	public static final int LOADSCREEN = 2324;

	public static final int STAT_DEFAULT = 0;
	public static final int STAT_MISC = 1;

	public static final int STAT_SKIP2_START = 2;
	public static final int STAT_ENEMY = 2;
	public static final int STAT_DEAD_ACTOR = 3; // misc actor stuff - dead guys etc
	public static final int STAT_MISSILE = 4;
	public static final int STAT_SHRAP = 5;
	public static final int STAT_SKIP2_END = 5;
	public static final int STAT_SKIP2_INTERP_END = 5;

	public static final int STAT_SKIP4_START = 6;
	public static final int STAT_SKIP4 = 6;
	public static final int STAT_MISSILE_SKIP4 = 7;
	public static final int STAT_MINE_STUCK = 8; // solely for mines
	public static final int STAT_ENEMY_SKIP4 = 9;
	public static final int STAT_SKIP4_INTERP_END = 9;

	public static final int STAT_ITEM = 10;
	public static final int STAT_SKIP4_END = 10;

	public static final int STAT_FAF_COPY = 11;
	public static final int STAT_MAX = 20; // everything below here can move
	public static final int STAT_PLAYER0 = 21;
	public static final int STAT_PLAYER1 = 22;
	public static final int STAT_PLAYER2 = 23;
	public static final int STAT_PLAYER3 = 24;

	public static final int STAT_PLAYER4 = 25;
	public static final int STAT_PLAYER5 = 26;
	public static final int STAT_PLAYER6 = 27;
	public static final int STAT_PLAYER7 = 28;
	public static final int STAT_PLAYER_UNDER0 = 29;

	public static final int STAT_PLAYER_UNDER1 = 30;
	public static final int STAT_PLAYER_UNDER2 = 31;
	public static final int STAT_PLAYER_UNDER3 = 32;
	public static final int STAT_PLAYER_UNDER4 = 33;
	public static final int STAT_PLAYER_UNDER5 = 34;

	public static final int STAT_PLAYER_UNDER6 = 35;
	public static final int STAT_PLAYER_UNDER7 = 36;
	public static final int STAT_GENERIC_QUEUE = 37;
	public static final int STAT_OBJECT_SPRITE = 38;// sprites that move with objects
	public static final int STAT_WALLBLOOD_QUEUE = 39;
	public static final int STAT_FLOORBLOOD_QUEUE = 40;
	public static final int STAT_NO_STATE = 41; // sprites that don't need state control
	public static final int STAT_STATIC_FIRE = 42;
	public static final int STAT_STAR_QUEUE = 43;
	public static final int STAT_HOLE_QUEUE = 44;
	public static final int STAT_BREAKABLE = 45;
	public static final int STAT_SPRITE_HIT_MATCH = 46;// TAG_SPRITE_HIT_MATCH
	//
	// Everything from here down does not get drawn
	//
	public static final int STAT_DONT_DRAW = 47;
	public static final int STAT_SUICIDE = 48;
	public static final int STAT_DIVE_AREA = 49;
	public static final int STAT_UNDERWATER = 50;
	public static final int STAT_UNDERWATER2 = 51;

	public static final int STAT_CEILING_FLOOR_PIC_OVERRIDE = 52;
	public static final int STAT_CLIMB_MARKER = 53;
	public static final int STAT_ALL = 54;
	public static final int STAT_SO_SHOOT_POINT = 55; // operational SO shooting point
	public static final int STAT_SPAWN_TRIGGER = 56;// triggers spawn sprites
	public static final int STAT_TRAP = 57;// triggered traps - spear/fireball etc
	public static final int STAT_TRIGGER = 58;// attempt to replace tagging sectors - use ST1 sprites
	public static final int STAT_DEMO_CAMERA = 59;// demo camera placement
	public static final int STAT_FAF = 60;// floor above floor stat
	public static final int STAT_SO_SP_CHILD = 61;
	public static final int STAT_WARP = 62;
	public static final int STAT_WARP_COPY_SPRITE1 = 63;

	public static final int STAT_WARP_COPY_SPRITE2 = 64;
	public static final int STAT_SOUND_SPOT = 65;
	public static final int STAT_STOP_SOUND_SPOT = 66;
	public static final int STAT_SPAWN_SPOT = 67;
	public static final int STAT_AMBIENT = 68;
	public static final int STAT_ECHO = 69;
	public static final int STAT_VATOR = 70;
	public static final int STAT_ROTATOR = 71;
	public static final int STAT_ROTATOR_PIVOT = 72;
	public static final int STAT_SLIDOR = 73;
	public static final int STAT_FLOOR_SLOPE_DONT_DRAW = 74;

	public static final int STAT_EXPLODING_CEIL_FLOOR = 75;
	public static final int STAT_COPY_SOURCE = 76;
	public static final int STAT_COPY_DEST = 77;
	public static final int STAT_WALL_MOVE = 78;
	public static final int STAT_WALL_MOVE_CANSEE = 79;

	public static final short STAT_SPAWN_ITEMS = 80;
	public static final short STAT_DELETE_SPRITE = 81;
	public static final short STAT_SPIKE = 82;
	public static final short STAT_LIGHTING = 83;
	public static final short STAT_LIGHTING_DIFFUSE = 84;
	public static final short STAT_WALL_DONT_MOVE_UPPER = 85;
	public static final short STAT_WALL_DONT_MOVE_LOWER = 86;
	public static final short STAT_FLOOR_PAN = 87;
	public static final short STAT_CEILING_PAN = 88;
	public static final short STAT_WALL_PAN = 89;
	public static final short STAT_NO_RIDE = 90;
	public static final short STAT_QUAKE_SPOT = 91;
	public static final short STAT_QUAKE_ON = 92;
	public static final short STAT_VIS_ON = 93;
	public static final short STAT_CHANGOR = 94;

	public static final int STAT_TRACK = 200;
	public static final int STAT_ST1 = 500;
	public static final int STAT_QUICK_JUMP = 501;
	public static final int STAT_QUICK_JUMP_DOWN = 502;
	public static final int STAT_QUICK_SUPER_JUMP = 503;
	public static final int STAT_QUICK_SCAN = 504;
	public static final int STAT_QUICK_EXIT = 505;
	public static final int STAT_QUICK_LADDER = 506;
	public static final int STAT_QUICK_OPERATE = 507;
	public static final int STAT_QUICK_DUCK = 508;
	public static final int STAT_QUICK_DEFEND = 509;

	// multi player start
	public static final int STAT_MULTI_START = 600;
	public static final int STAT_MULTI_START1 = 601;
	public static final int STAT_MULTI_START2 = 602;
	public static final int STAT_MULTI_START3 = 603;
	public static final int STAT_MULTI_START4 = 604;
	public static final int STAT_MULTI_START5 = 605;
	public static final int STAT_MULTI_START6 = 606;
	public static final int STAT_MULTI_START7 = 607;

	public static final int STAT_CO_OP_START = 610;
	public static final int STAT_CO_OP_START1 = 611;
	public static final int STAT_CO_OP_START2 = 612;
	public static final int STAT_CO_OP_START3 = 613;
	public static final int STAT_CO_OP_START4 = 614;
	public static final int STAT_CO_OP_START5 = 615;
	public static final int STAT_CO_OP_START6 = 616;
	public static final int STAT_CO_OP_START7 = 617;

	// ******************************************************************************

	public static final int SWITCH_ON = 562;
	public static final int SWITCH_OFF = 561;

	public static final int TRACK_SPRITE = 1900; // start of track sprites
	public static final int ST1 = 2307;
	public static final int ST2 = 2308;
	public static final int ST_QUICK_JUMP = 2309;
	public static final int ST_QUICK_JUMP_DOWN = 2310;
	public static final int ST_QUICK_SUPER_JUMP = 2311;
	public static final int ST_QUICK_SCAN = 2312;
	public static final int ST_QUICK_EXIT = 2313;
	public static final int ST_QUICK_OPERATE = 2314;
	public static final int ST_QUICK_DUCK = 2315;
	public static final int ST_QUICK_DEFEND = 2316;

	//////////////////////
	//
	// TIMERS
	//
	//////////////////////

	public static final int TICS_PER_SEC = 120;

	public static final int FLY_INVENTORY_TIME = 30;
	public static final int CLOAK_INVENTORY_TIME = 30;
	public static final int ENVIRON_SUIT_INVENTORY_TIME = 30;

	public static final int DAMAGE_TIME = (1 * TICS_PER_SEC);

	//////////////////////
	//
	// BLADES
	//
	//////////////////////

	public static final int BLADE1 = 360;
	public static final int BLADE2 = 361;
	public static final int BLADE3 = 362;

	//////////////////////
	//
	// WEAPON RELATED
	//
	//////////////////////

	public static final int ICON_STAR = 1793;

	public static final int ICON_UZI = 1797;
	public static final int ICON_UZIFLOOR = 1807;
	public static final int ICON_LG_UZI_AMMO = 1799;

	public static final int ICON_HEART = 1824;
	public static final int ICON_HEART_LG_AMMO = 1820;

	public static final int ICON_GUARD_HEAD = 1814;
	public static final int ICON_FIREBALL_LG_AMMO = 3035;

	public static final int NAPALM_MIN_AMMO = 10;
	public static final int RING_MIN_AMMO = 30;

	public static final int ICON_ROCKET = 1843;
	public static final int ICON_SHOTGUN = 1794;
	public static final int ICON_LG_ROCKET = 1796;
	public static final int ICON_LG_SHOTSHELL = 1823;
	public static final int ICON_AUTORIOT = 1822;

	public static final int ICON_MICRO_GUN = 1818;
	public static final int ICON_MICRO_BATTERY = 1800;

	public static final int ICON_GRENADE_LAUNCHER = 1817;
	public static final int ICON_LG_GRENADE = 1831;

	public static final int ICON_LG_MINE = 1842;

	public static final int ICON_RAIL_GUN = 1811;
	public static final int ICON_RAIL_AMMO = 1812;

	// not used now
	public static final int ICON_SPELL = 1880;
	public static final int ICON_ELECTRO = 1822;

	public static final int ICON_EXPLOSIVE_BOX = 1801;
	public static final int ICON_ENVIRON_SUIT = 1837;
	public static final int ICON_FLY = 1782;
	public static final int ICON_CLOAK = 1804;
	public static final int ICON_NIGHT_VISION = 3031;
	public static final int ICON_NAPALM = 3046;
	public static final int ICON_RING = 3050;
	public static final int ICON_GOROAMMO = 3035;
	public static final int ICON_HEARTAMMO = 1820;
	public static final int ICON_RINGAMMO = 3054;
	public static final int ICON_NAPALMAMMO = 3058;
	public static final int ICON_GRENADE = 3059;
	public static final int ICON_OXYGEN = 1800;
	public static final int ICON_ARMOR = 3030;
	public static final int ICON_SM_MEDKIT = 1802;
	public static final int ICON_BOOSTER = 1810;
	public static final int ICON_HEAT_CARD = 1819;
	public static final int ICON_REPAIR_KIT = 1813;
	public static final int ICON_MEDKIT = 1803;
	public static final int ICON_CHEMBOMB = 1808;
	public static final int ICON_FLASHBOMB = 1805;
	public static final int ICON_NUKE = 1809;
	public static final int ICON_CALTROPS = 1829;
	public static final int ICON_FLAG = 2520;

	public static final int STAR = 2039;
	public static final int ELECTRO = 2025;

	public static final int UZI_SMOKE = 2146;
	public static final int UZI_SPARK = 2140;
	public static final int SPIKES = 2092;
	public static final int GRENADE = 2019;
	public static final int BLANK = 2200;

	public static final int BODY = 1002;
	public static final int BODY_BURN = 1003;
	public static final int BODY_SIZZLE = 1011;

	public static final int DART_R0 = 2130;
	public static final int DART_R1 = 2131;
	public static final int DART_R2 = 2132;
	public static final int DART_R3 = 2133;
	public static final int DART_R4 = 2134;
	public static final int DART_R5 = 2135;
	public static final int DART_R6 = 2136;
	public static final int DART_R7 = 2137;

	public static final int BOLT_THINMAN_FRAMES = 1;
	public static final int BOLT_THINMAN_R0 = 2018;
	public static final int BOLT_THINMAN_R1 = BOLT_THINMAN_R0 - (BOLT_THINMAN_FRAMES * 1);
	public static final int BOLT_THINMAN_R2 = BOLT_THINMAN_R0 - (BOLT_THINMAN_FRAMES * 2);
	public static final int BOLT_THINMAN_R3 = BOLT_THINMAN_R0 - (BOLT_THINMAN_FRAMES * 3);
	public static final int BOLT_THINMAN_R4 = BOLT_THINMAN_R0 - (BOLT_THINMAN_FRAMES * 4);

	public static final int SPEAR_FRAMES = 1;
	public static final int SPEAR_R0 = 2030;
	public static final int SPEAR_R1 = SPEAR_R0 + (SPEAR_FRAMES * 1);
	public static final int SPEAR_R2 = SPEAR_R0 + (SPEAR_FRAMES * 2);
	public static final int SPEAR_R3 = SPEAR_R0 + (SPEAR_FRAMES * 3);
	public static final int SPEAR_R4 = SPEAR_R0 + (SPEAR_FRAMES * 4);

	public static final int STAR1 = 2049;
	public static final int EXP = 3100; // Use new digitized explosion for big stuff
	public static final int EXP2 = 2160;// My old explosion is still used for goro head
	public static final int FIREBALL = 2035;
	public static final int FIREBALL_FLAMES = 3212;

	//////////////////////
	//
	// MISC
	//
	//////////////////////

	public static final int COINCURSOR = 2440; // Jim uses this for a text input cursor
	public static final int STARTALPHANUM = 4608; // New SW font for typing in stuff, It's in ASCII order.
	public static final int ENDALPHANUM = 4701;
	public static final int MINIFONT = 2930; // Start of small font, it's blue for good palette swapping

	public static final int STATUS_BAR = 2434;
	public static final int STATUS_KEYS = 2881;
	public static final int STATUS_NUMBERS = 2887;
	public static final int BORDER_TILE = 2604;

	public static final int BLUE_KEY = 1766;
	public static final int RED_KEY = 1770;
	public static final int GREEN_KEY = 1774;
	public static final int YELLOW_KEY = 1778;
	public static final int GOLD_SKELKEY = 1765;
	public static final int SILVER_SKELKEY = 1769;
	public static final int BRONZE_SKELKEY = 1773;
	public static final int RED_SKELKEY = 1777;
	public static final int BLUE_CARD = 1767;
	public static final int RED_CARD = 1771;
	public static final int GREEN_CARD = 1775;
	public static final int YELLOW_CARD = 1779;

	public static final int BLUE_KEY_STATUE = BLUE_KEY + 2;
	public static final int RED_KEY_STATUE = RED_KEY + 2;
	public static final int GREEN_KEY_STATUE = GREEN_KEY + 2;
	public static final int YELLOW_KEY_STATUE = YELLOW_KEY + 2;
	public static final int SKEL_LOCKED = 1846;
	public static final int SKEL_UNLOCKED = 1847;
	public static final int RAMCARD_LOCKED = 1850;
	public static final int RAMCARD_UNLOCKED = 1851;
	public static final int CARD_LOCKED = 1852;
	public static final int CARD_UNLOCKED = 1853;

	public static final int WATER_BEGIN = 320;
	public static final int WATER_END = 320 + 8;

	public static final int WATER_BOIL = 2305;

	public static final int FIRE_FLY0 = 630;
	public static final int FIRE_FLY1 = 631;
	public static final int FIRE_FLY2 = 632;
	public static final int FIRE_FLY3 = 633;
	public static final int FIRE_FLY4 = 634;

	public static final int FIRE_FLY_RATE = 50;

	public static final int BREAK_LIGHT = 443;
	public static final int BREAK_LIGHT_ANIM = 447;
	public static final int BREAK_BARREL = 453;
	public static final int BREAK_PEDISTAL = 463;
	public static final int BREAK_BOTTLE1 = 468;
	public static final int BREAK_BOTTLE2 = 475;
	public static final int BREAK_MUSHROOM = 666;

	//////////////////////
	//
	// COOLIE
	//
	//////////////////////

	public static final int COOLIE_CHARGE_FRAMES = 4;
	public static final int COOLIE_CHARGE_R0 = 1420;
	public static final int COOLIE_CHARGE_R1 = COOLIE_CHARGE_R0 + (COOLIE_CHARGE_FRAMES * 1);
	public static final int COOLIE_CHARGE_R2 = COOLIE_CHARGE_R0 + (COOLIE_CHARGE_FRAMES * 2);
	public static final int COOLIE_CHARGE_R3 = COOLIE_CHARGE_R0 + (COOLIE_CHARGE_FRAMES * 3);
	public static final int COOLIE_CHARGE_R4 = COOLIE_CHARGE_R0 + (COOLIE_CHARGE_FRAMES * 4);

	public static final int COOLIE_RUN_FRAMES = 4;
	public static final int COOLIE_RUN_R0 = 1400;
	public static final int COOLIE_RUN_R1 = COOLIE_RUN_R0 + (COOLIE_RUN_FRAMES * 1);
	public static final int COOLIE_RUN_R2 = COOLIE_RUN_R0 + (COOLIE_RUN_FRAMES * 2);
	public static final int COOLIE_RUN_R3 = COOLIE_RUN_R0 + (COOLIE_RUN_FRAMES * 3);
	public static final int COOLIE_RUN_R4 = COOLIE_RUN_R0 + (COOLIE_RUN_FRAMES * 4);

	public static final int COOLIE_PAIN_FRAMES = 1;
	public static final int COOLIE_PAIN_R0 = 1420;
	public static final int COOLIE_PAIN_R1 = COOLIE_PAIN_R0;
	public static final int COOLIE_PAIN_R2 = COOLIE_PAIN_R0;
	public static final int COOLIE_PAIN_R3 = COOLIE_PAIN_R0;
	public static final int COOLIE_PAIN_R4 = COOLIE_PAIN_R0;

	public static final int COOLG_RUN_FRAMES = 4;
	public static final int COOLG_RUN_R0 = 4277;
	public static final int COOLG_RUN_R1 = COOLG_RUN_R0 + (COOLG_RUN_FRAMES * 1);
	public static final int COOLG_RUN_R2 = COOLG_RUN_R0 + (COOLG_RUN_FRAMES * 2);
	public static final int COOLG_RUN_R3 = COOLG_RUN_R0 + (COOLG_RUN_FRAMES * 3);
	public static final int COOLG_RUN_R4 = COOLG_RUN_R0 + (COOLG_RUN_FRAMES * 4);

	public static final int COOLG_CLUB_FRAMES = 2;
	public static final int COOLG_CLUB_R0 = 1451;
	public static final int COOLG_CLUB_R1 = COOLG_CLUB_R0 + (COOLG_CLUB_FRAMES * 1);
	public static final int COOLG_CLUB_R2 = COOLG_CLUB_R0 + (COOLG_CLUB_FRAMES * 2);
	public static final int COOLG_CLUB_R3 = COOLG_CLUB_R0 + (COOLG_CLUB_FRAMES * 3);
	public static final int COOLG_CLUB_R4 = COOLG_CLUB_R0 + (COOLG_CLUB_FRAMES * 4);

	public static final int COOLG_FIRE_FRAMES = 3;
	public static final int COOLG_FIRE_R0 = 4297;
	public static final int COOLG_FIRE_R1 = COOLG_FIRE_R0 + (COOLG_FIRE_FRAMES * 1);
	public static final int COOLG_FIRE_R2 = COOLG_FIRE_R0 + (COOLG_FIRE_FRAMES * 2);

	public static final int COOLG_PAIN_FRAMES = 1;
	public static final int COOLG_PAIN_R0 = 4306;
	public static final int COOLG_PAIN_R1 = COOLG_PAIN_R0; // + (COOLIE_PAIN_FRAMES * 1);
	public static final int COOLG_PAIN_R2 = COOLG_PAIN_R0; // + (COOLIE_PAIN_FRAMES * 2);
	public static final int COOLG_PAIN_R3 = COOLG_PAIN_R0; // + (COOLIE_PAIN_FRAMES * 3);
	public static final int COOLG_PAIN_R4 = COOLG_PAIN_R0; // + (COOLIE_PAIN_FRAMES * 4);

	public static final int COOLIE_DEAD_NOHEAD = 1440;
	public static final int COOLIE_DIE = 4260;
	public static final int COOLIE_DEAD = 4268;

	//////////////////////
	//
	// PLAYER NINJA
	//
	//////////////////////

	public static final int PLAYER_NINJA_SHOOT_FRAMES = 1;
	public static final int PLAYER_NINJA_SHOOT_R0 = 1069;
	public static final int PLAYER_NINJA_SHOOT_R1 = PLAYER_NINJA_SHOOT_R0 + (PLAYER_NINJA_SHOOT_FRAMES * 2);
	public static final int PLAYER_NINJA_SHOOT_R2 = PLAYER_NINJA_SHOOT_R0 + (PLAYER_NINJA_SHOOT_FRAMES * 3);
	public static final int PLAYER_NINJA_SHOOT_R3 = PLAYER_NINJA_SHOOT_R0 + (PLAYER_NINJA_SHOOT_FRAMES * 4);
	public static final int PLAYER_NINJA_SHOOT_R4 = PLAYER_NINJA_SHOOT_R0 + (PLAYER_NINJA_SHOOT_FRAMES * 1);

	public static final int PLAYER_NINJA_RUN_FRAMES = 4;
	public static final int PLAYER_NINJA_RUN_R0 = 1094;
	public static final int PLAYER_NINJA_RUN_R1 = PLAYER_NINJA_RUN_R0 + (PLAYER_NINJA_RUN_FRAMES * 1);
	public static final int PLAYER_NINJA_RUN_R2 = PLAYER_NINJA_RUN_R0 + (PLAYER_NINJA_RUN_FRAMES * 2);
	public static final int PLAYER_NINJA_RUN_R3 = PLAYER_NINJA_RUN_R0 + (PLAYER_NINJA_RUN_FRAMES * 3);
	public static final int PLAYER_NINJA_RUN_R4 = PLAYER_NINJA_RUN_R0 + (PLAYER_NINJA_RUN_FRAMES * 4);

	public static final int ZOMBIE_RUN_R0 = PLAYER_NINJA_RUN_R0 + 1;

	public static final int PLAYER_NINJA_STAND_FRAMES = 1;
	public static final int PLAYER_NINJA_STAND_R0 = PLAYER_NINJA_RUN_R0;
	public static final int PLAYER_NINJA_STAND_R1 = PLAYER_NINJA_RUN_R1;
	public static final int PLAYER_NINJA_STAND_R2 = PLAYER_NINJA_RUN_R2;
	public static final int PLAYER_NINJA_STAND_R3 = PLAYER_NINJA_RUN_R3;
	public static final int PLAYER_NINJA_STAND_R4 = PLAYER_NINJA_RUN_R4;

	public static final int PLAYER_NINJA_JUMP_FRAMES = 4;
	public static final int PLAYER_NINJA_JUMP_R0 = 1074;
	public static final int PLAYER_NINJA_JUMP_R1 = PLAYER_NINJA_JUMP_R0 + (PLAYER_NINJA_JUMP_FRAMES * 1);
	public static final int PLAYER_NINJA_JUMP_R2 = PLAYER_NINJA_JUMP_R0 + (PLAYER_NINJA_JUMP_FRAMES * 2);
	public static final int PLAYER_NINJA_JUMP_R3 = PLAYER_NINJA_JUMP_R0 + (PLAYER_NINJA_JUMP_FRAMES * 3);
	public static final int PLAYER_NINJA_JUMP_R4 = PLAYER_NINJA_JUMP_R0 + (PLAYER_NINJA_JUMP_FRAMES * 4);

	public static final int PLAYER_NINJA_FALL_R0 = PLAYER_NINJA_JUMP_R0 + 2;
	public static final int PLAYER_NINJA_FALL_R1 = PLAYER_NINJA_JUMP_R1 + 2;
	public static final int PLAYER_NINJA_FALL_R2 = PLAYER_NINJA_JUMP_R2 + 2;
	public static final int PLAYER_NINJA_FALL_R3 = PLAYER_NINJA_JUMP_R3 + 2;
	public static final int PLAYER_NINJA_FALL_R4 = PLAYER_NINJA_JUMP_R4 + 2;

	public static final int PLAYER_NINJA_CLIMB_FRAMES = 4;
	public static final int PLAYER_NINJA_CLIMB_R0 = 1024;
	public static final int PLAYER_NINJA_CLIMB_R1 = PLAYER_NINJA_CLIMB_R0 + (PLAYER_NINJA_CLIMB_FRAMES * 1);
	public static final int PLAYER_NINJA_CLIMB_R2 = PLAYER_NINJA_CLIMB_R0 + (PLAYER_NINJA_CLIMB_FRAMES * 2);
	public static final int PLAYER_NINJA_CLIMB_R3 = PLAYER_NINJA_CLIMB_R0 + (PLAYER_NINJA_CLIMB_FRAMES * 3);
	public static final int PLAYER_NINJA_CLIMB_R4 = PLAYER_NINJA_CLIMB_R0 + (PLAYER_NINJA_CLIMB_FRAMES * 4);

	public static final int PLAYER_NINJA_CRAWL_FRAMES = 4;
	public static final int PLAYER_NINJA_CRAWL_R0 = 1044;
	public static final int PLAYER_NINJA_CRAWL_R1 = PLAYER_NINJA_CRAWL_R0 + (PLAYER_NINJA_CRAWL_FRAMES * 1);
	public static final int PLAYER_NINJA_CRAWL_R2 = PLAYER_NINJA_CRAWL_R0 + (PLAYER_NINJA_CRAWL_FRAMES * 2);
	public static final int PLAYER_NINJA_CRAWL_R3 = PLAYER_NINJA_CRAWL_R0 + (PLAYER_NINJA_CRAWL_FRAMES * 3);
	public static final int PLAYER_NINJA_CRAWL_R4 = PLAYER_NINJA_CRAWL_R0 + (PLAYER_NINJA_CRAWL_FRAMES * 4);

	public static final int PLAYER_NINJA_SWIM_FRAMES = 4;
	public static final int PLAYER_NINJA_SWIM_R0 = 1114;
	public static final int PLAYER_NINJA_SWIM_R1 = PLAYER_NINJA_SWIM_R0 + (PLAYER_NINJA_SWIM_FRAMES * 1);
	public static final int PLAYER_NINJA_SWIM_R2 = PLAYER_NINJA_SWIM_R0 + (PLAYER_NINJA_SWIM_FRAMES * 2);
	public static final int PLAYER_NINJA_SWIM_R3 = PLAYER_NINJA_SWIM_R0 + (PLAYER_NINJA_SWIM_FRAMES * 3);
	public static final int PLAYER_NINJA_SWIM_R4 = PLAYER_NINJA_SWIM_R0 + (PLAYER_NINJA_SWIM_FRAMES * 4);

	public static final int PLAYER_NINJA_SWORD_FRAMES = 3;
	public static final int PLAYER_NINJA_SWORD_R0 = 1161;
	public static final int PLAYER_NINJA_SWORD_R1 = PLAYER_NINJA_SWORD_R0 + (PLAYER_NINJA_SWORD_FRAMES * 1);
	public static final int PLAYER_NINJA_SWORD_R2 = PLAYER_NINJA_SWORD_R0 + (PLAYER_NINJA_SWORD_FRAMES * 2);
	public static final int PLAYER_NINJA_SWORD_R3 = PLAYER_NINJA_SWORD_R0 + (PLAYER_NINJA_SWORD_FRAMES * 3);
	public static final int PLAYER_NINJA_SWORD_R4 = PLAYER_NINJA_SWORD_R0 + (PLAYER_NINJA_SWORD_FRAMES * 4);

	public static final int PLAYER_NINJA_PUNCH_FRAMES = 2;
	public static final int PLAYER_NINJA_PUNCH_R0 = 1176;
	public static final int PLAYER_NINJA_PUNCH_R1 = PLAYER_NINJA_PUNCH_R0 + (PLAYER_NINJA_PUNCH_FRAMES * 1);
	public static final int PLAYER_NINJA_PUNCH_R2 = PLAYER_NINJA_PUNCH_R0 + (PLAYER_NINJA_PUNCH_FRAMES * 2);
	public static final int PLAYER_NINJA_PUNCH_R3 = PLAYER_NINJA_PUNCH_R0 + (PLAYER_NINJA_PUNCH_FRAMES * 3);
	public static final int PLAYER_NINJA_PUNCH_R4 = PLAYER_NINJA_PUNCH_R0 + (PLAYER_NINJA_PUNCH_FRAMES * 4);

	public static final int PLAYER_NINJA_KICK_FRAMES = 2;
	public static final int PLAYER_NINJA_KICK_R0 = 1186;
	public static final int PLAYER_NINJA_KICK_R1 = PLAYER_NINJA_KICK_R0 + (PLAYER_NINJA_KICK_FRAMES * 1);
	public static final int PLAYER_NINJA_KICK_R2 = PLAYER_NINJA_KICK_R0 + (PLAYER_NINJA_KICK_FRAMES * 2);
	public static final int PLAYER_NINJA_KICK_R3 = PLAYER_NINJA_KICK_R0 + (PLAYER_NINJA_KICK_FRAMES * 3);
	public static final int PLAYER_NINJA_KICK_R4 = PLAYER_NINJA_KICK_R0 + (PLAYER_NINJA_KICK_FRAMES * 4);

	public static final int PLAYER_NINJA_DIE = 1152;
	public static final int PLAYER_NINJA_FLY = 1069;

	//////////////////////
	//
	// EVIL NINJA
	//
	//////////////////////

	public static final int NINJA_RUN_FRAMES = 4;
	public static final int NINJA_RUN_R0 = 4096;
	public static final int NINJA_RUN_R1 = NINJA_RUN_R0 + (NINJA_RUN_FRAMES * 1);
	public static final int NINJA_RUN_R2 = NINJA_RUN_R0 + (NINJA_RUN_FRAMES * 2);
	public static final int NINJA_RUN_R3 = NINJA_RUN_R0 + (NINJA_RUN_FRAMES * 3);
	public static final int NINJA_RUN_R4 = NINJA_RUN_R0 + (NINJA_RUN_FRAMES * 4);

	public static final int NINJA_STAND_FRAMES = 1;
	public static final int NINJA_STAND_R0 = NINJA_RUN_R0;
	public static final int NINJA_STAND_R1 = NINJA_RUN_R1;
	public static final int NINJA_STAND_R2 = NINJA_RUN_R2;
	public static final int NINJA_STAND_R3 = NINJA_RUN_R3;
	public static final int NINJA_STAND_R4 = NINJA_RUN_R4;

	public static final int NINJA_CRAWL_FRAMES = 4;
	public static final int NINJA_CRAWL_R0 = 4162;
	public static final int NINJA_CRAWL_R1 = NINJA_CRAWL_R0 + (NINJA_CRAWL_FRAMES * 1);
	public static final int NINJA_CRAWL_R2 = NINJA_CRAWL_R0 + (NINJA_CRAWL_FRAMES * 2);
	public static final int NINJA_CRAWL_R3 = NINJA_CRAWL_R0 + (NINJA_CRAWL_FRAMES * 3);
	public static final int NINJA_CRAWL_R4 = NINJA_CRAWL_R0 + (NINJA_CRAWL_FRAMES * 4);

	public static final int NINJA_SWIM_FRAMES = 4;
	public static final int NINJA_SWIM_R0 = 4122;
	public static final int NINJA_SWIM_R1 = NINJA_SWIM_R0 + (NINJA_SWIM_FRAMES * 1);
	public static final int NINJA_SWIM_R2 = NINJA_SWIM_R0 + (NINJA_SWIM_FRAMES * 2);
	public static final int NINJA_SWIM_R3 = NINJA_SWIM_R0 + (NINJA_SWIM_FRAMES * 3);
	public static final int NINJA_SWIM_R4 = NINJA_SWIM_R0 + (NINJA_SWIM_FRAMES * 4);

	public static final int NINJA_HARI_KARI_FRAMES = 8;
	public static final int NINJA_HARI_KARI_R0 = 4211;
	public static final int NINJA_HARI_KARI_R1 = NINJA_HARI_KARI_R0;
	public static final int NINJA_HARI_KARI_R2 = NINJA_HARI_KARI_R0;
	public static final int NINJA_HARI_KARI_R3 = NINJA_HARI_KARI_R0;
	public static final int NINJA_HARI_KARI_R4 = NINJA_HARI_KARI_R0;

	public static final int NINJA_CLIMB_FRAMES = 4;
	public static final int NINJA_CLIMB_R0 = 4182;
	public static final int NINJA_CLIMB_R1 = NINJA_CLIMB_R0 + (NINJA_CLIMB_FRAMES * 1);
	public static final int NINJA_CLIMB_R2 = NINJA_CLIMB_R0 + (NINJA_CLIMB_FRAMES * 2);
	public static final int NINJA_CLIMB_R3 = NINJA_CLIMB_R0 + (NINJA_CLIMB_FRAMES * 3);
	public static final int NINJA_CLIMB_R4 = NINJA_CLIMB_R0 + (NINJA_CLIMB_FRAMES * 4);

	public static final int NINJA_THROW_FRAMES = 3;
	public static final int NINJA_THROW_R0 = 4202;
	public static final int NINJA_THROW_R1 = NINJA_THROW_R0 + (NINJA_THROW_FRAMES * 1);
	public static final int NINJA_THROW_R2 = NINJA_THROW_R0 + (NINJA_THROW_FRAMES * 2);

	// don't have rotations for pain frames currently

	public static final int NINJA_JUMP_FRAMES = 4;
	public static final int NINJA_JUMP_R0 = 4142;
	public static final int NINJA_JUMP_R1 = NINJA_JUMP_R0 + (NINJA_JUMP_FRAMES * 1);
	public static final int NINJA_JUMP_R2 = NINJA_JUMP_R0 + (NINJA_JUMP_FRAMES * 2);
	public static final int NINJA_JUMP_R3 = NINJA_JUMP_R0 + (NINJA_JUMP_FRAMES * 3);
	public static final int NINJA_JUMP_R4 = NINJA_JUMP_R0 + (NINJA_JUMP_FRAMES * 4);

	public static final int NINJA_FALL_R0 = NINJA_JUMP_R0 + 2;
	public static final int NINJA_FALL_R1 = NINJA_JUMP_R1 + 2;
	public static final int NINJA_FALL_R2 = NINJA_JUMP_R2 + 2;
	public static final int NINJA_FALL_R3 = NINJA_JUMP_R3 + 2;
	public static final int NINJA_FALL_R4 = NINJA_JUMP_R4 + 2;

	public static final int NINJA_LAND_R0 = NINJA_JUMP_R0 + 3;
	public static final int NINJA_LAND_R1 = NINJA_JUMP_R1 + 3;
	public static final int NINJA_LAND_R2 = NINJA_JUMP_R2 + 3;
	public static final int NINJA_LAND_R3 = NINJA_JUMP_R3 + 3;
	public static final int NINJA_LAND_R4 = NINJA_JUMP_R4 + 3;

	public static final int NINJA_KNEEL_R0 = NINJA_CRAWL_R0;
	public static final int NINJA_KNEEL_R1 = NINJA_CRAWL_R1;
	public static final int NINJA_KNEEL_R2 = NINJA_CRAWL_R2;
	public static final int NINJA_KNEEL_R3 = NINJA_CRAWL_R3;
	public static final int NINJA_KNEEL_R4 = NINJA_CRAWL_R4;

	public static final int NINJA_FLY_FRAMES = 2;
	public static final int NINJA_FLY_R0 = 4116;
	public static final int NINJA_FLY_R1 = NINJA_FLY_R0 + (NINJA_FLY_FRAMES * 1);
	public static final int NINJA_FLY_R2 = NINJA_FLY_R0 + (NINJA_FLY_FRAMES * 2);
	public static final int NINJA_FLY_R3 = NINJA_FLY_R0 + (NINJA_FLY_FRAMES * 3);
	public static final int NINJA_FLY_R4 = NINJA_FLY_R0 + (NINJA_FLY_FRAMES * 4);

	public static final int NINJA_FIRE_FRAMES = 2;
	public static final int NINJA_FIRE_R0 = 4116;
	public static final int NINJA_FIRE_R1 = NINJA_FIRE_R0 + (NINJA_FIRE_FRAMES * 1);
	public static final int NINJA_FIRE_R2 = NINJA_FIRE_R0 + (NINJA_FIRE_FRAMES * 2);
	public static final int NINJA_FIRE_R3 = NINJA_FIRE_R0 + (NINJA_FIRE_FRAMES * 3);
	public static final int NINJA_FIRE_R4 = NINJA_FIRE_R0 + (NINJA_FIRE_FRAMES * 4);

	public static final int NINJA_PAIN_FRAMES = 2;
	public static final int NINJA_PAIN_R0 = 4219;
	public static final int NINJA_PAIN_R1 = NINJA_PAIN_R0;
	public static final int NINJA_PAIN_R2 = NINJA_PAIN_R0;
	public static final int NINJA_PAIN_R3 = NINJA_PAIN_R0;
	public static final int NINJA_PAIN_R4 = NINJA_PAIN_R0;

	public static final int NINJA_DIE = 4219;
	public static final int NINJA_DEAD = NINJA_DIE + 8;

	public static final int NINJA_SLICED = 4227;
	public static final int NINJA_DEAD_SLICED = NINJA_SLICED + 9;

	public static final int NINJA_Head_FRAMES = 1;
	public static final int NINJA_Head_R0 = 1142;
	public static final int NINJA_Head_R1 = NINJA_Head_R0 + (NINJA_Head_FRAMES * 1);
	public static final int NINJA_Head_R2 = NINJA_Head_R0 + (NINJA_Head_FRAMES * 2);
	public static final int NINJA_Head_R3 = NINJA_Head_R0 + (NINJA_Head_FRAMES * 3);
	public static final int NINJA_Head_R4 = NINJA_Head_R0 + (NINJA_Head_FRAMES * 4);

	public static final int NINJA_HeadHurl_FRAMES = 1;
	public static final int NINJA_HeadHurl_R0 = 1147;
	public static final int NINJA_HeadHurl_R1 = NINJA_HeadHurl_R0 + (NINJA_HeadHurl_FRAMES * 1);
	public static final int NINJA_HeadHurl_R2 = NINJA_HeadHurl_R0 + (NINJA_HeadHurl_FRAMES * 2);
	public static final int NINJA_HeadHurl_R3 = NINJA_HeadHurl_R0 + (NINJA_HeadHurl_FRAMES * 3);
	public static final int NINJA_HeadHurl_R4 = NINJA_HeadHurl_R0 + (NINJA_HeadHurl_FRAMES * 4);

	//////////////////////
	//
	// SERP BOSS
	//
	//////////////////////

	public static final int SERP_RUN_FRAMES = 3;
	public static final int SERP_RUN_R0 = 1300;
	public static final int SERP_RUN_R1 = SERP_RUN_R0 + (SERP_RUN_FRAMES * 1);
	public static final int SERP_RUN_R2 = SERP_RUN_R0 + (SERP_RUN_FRAMES * 2);
	public static final int SERP_RUN_R3 = SERP_RUN_R0 + (SERP_RUN_FRAMES * 3);
	public static final int SERP_RUN_R4 = SERP_RUN_R0 + (SERP_RUN_FRAMES * 4);

	public static final int SERP_SLASH_FRAMES = 5;
	public static final int SERP_SLASH_R0 = 972;
	public static final int SERP_SLASH_R1 = SERP_SLASH_R0 + (SERP_SLASH_FRAMES * 1);
	public static final int SERP_SLASH_R2 = SERP_SLASH_R0 + (SERP_SLASH_FRAMES * 2);
	public static final int SERP_SLASH_R3 = SERP_SLASH_R0 + (SERP_SLASH_FRAMES * 3);
	public static final int SERP_SLASH_R4 = SERP_SLASH_R0 + (SERP_SLASH_FRAMES * 4);

	public static final int SERP_SPELL_FRAMES = 4;
	public static final int SERP_SPELL_R0 = 997;
	public static final int SERP_SPELL_R1 = SERP_SPELL_R0 + (SERP_SPELL_FRAMES * 1);
	public static final int SERP_SPELL_R2 = SERP_SPELL_R0 + (SERP_SPELL_FRAMES * 2);
	public static final int SERP_SPELL_R3 = SERP_SPELL_R0 + (SERP_SPELL_FRAMES * 3);
	public static final int SERP_SPELL_R4 = SERP_SPELL_R0 + (SERP_SPELL_FRAMES * 4);

	public static final int SERP_DIE = 960;
	public static final int SERP_DEAD = 969;

	//////////////////////
	//
	// LAVA BOSS
	//
	//////////////////////

	public static final int LAVA_RUN_FRAMES = 1;
	public static final int LAVA_RUN_R0 = 2355;
	public static final int LAVA_RUN_R1 = LAVA_RUN_R0; // + (LAVA_RUN_FRAMES * 1);
	public static final int LAVA_RUN_R2 = LAVA_RUN_R0; // + (LAVA_RUN_FRAMES * 2);
	public static final int LAVA_RUN_R3 = LAVA_RUN_R0; // + (LAVA_RUN_FRAMES * 3);
	public static final int LAVA_RUN_R4 = LAVA_RUN_R0; // + (LAVA_RUN_FRAMES * 4);

	public static final int LAVA_THROW_FRAMES = 1;
	public static final int LAVA_THROW_R0 = 2355;
	public static final int LAVA_THROW_R1 = LAVA_THROW_R0; // + (LAVA_THROW_FRAMES * 1);
	public static final int LAVA_THROW_R2 = LAVA_THROW_R0; // + (LAVA_THROW_FRAMES * 2);
	public static final int LAVA_THROW_R3 = LAVA_THROW_R0; // + (LAVA_THROW_FRAMES * 3);
	public static final int LAVA_THROW_R4 = LAVA_THROW_R0; // + (LAVA_THROW_FRAMES * 4);

	public static final int LAVA_FLAME_FRAMES = 1;
	public static final int LAVA_FLAME_R0 = 2355;
	public static final int LAVA_FLAME_R1 = LAVA_FLAME_R0; // + (LAVA_FLAME_FRAMES * 1);
	public static final int LAVA_FLAME_R2 = LAVA_FLAME_R0; // + (LAVA_FLAME_FRAMES * 2);
	public static final int LAVA_FLAME_R3 = LAVA_FLAME_R0; // + (LAVA_FLAME_FRAMES * 3);
	public static final int LAVA_FLAME_R4 = LAVA_FLAME_R0; // + (LAVA_FLAME_FRAMES * 4);

	public static final int LAVA_DIE = 2355;
	public static final int LAVA_DEAD = 2355;

	// 10 frames
	public static final int LAVA_BOULDER = 2196;

	//////////////////////
	//
	// SKEL
	//
	//////////////////////

	public static final int SKEL_RUN_FRAMES = 6;
	public static final int SKEL_RUN_R0 = 1367;
	public static final int SKEL_RUN_R1 = SKEL_RUN_R0 + (SKEL_RUN_FRAMES * 1);
	public static final int SKEL_RUN_R2 = SKEL_RUN_R0 + (SKEL_RUN_FRAMES * 2);
	public static final int SKEL_RUN_R3 = SKEL_RUN_R0 + (SKEL_RUN_FRAMES * 3);
	public static final int SKEL_RUN_R4 = SKEL_RUN_R0 + (SKEL_RUN_FRAMES * 4);

	public static final int SKEL_SLASH_FRAMES = 3;
	public static final int SKEL_SLASH_R0 = 1326;
	public static final int SKEL_SLASH_R1 = SKEL_SLASH_R0 + (SKEL_SLASH_FRAMES * 1);
	public static final int SKEL_SLASH_R2 = SKEL_SLASH_R0 + (SKEL_SLASH_FRAMES * 2);
	public static final int SKEL_SLASH_R3 = SKEL_SLASH_R0 + (SKEL_SLASH_FRAMES * 3);
	public static final int SKEL_SLASH_R4 = SKEL_SLASH_R0 + (SKEL_SLASH_FRAMES * 4);

	public static final int SKEL_SPELL_FRAMES = 4;
	public static final int SKEL_SPELL_R0 = 1341;
	public static final int SKEL_SPELL_R1 = SKEL_SPELL_R0 + (SKEL_SPELL_FRAMES * 1);
	public static final int SKEL_SPELL_R2 = SKEL_SPELL_R0 + (SKEL_SPELL_FRAMES * 2);
	public static final int SKEL_SPELL_R3 = SKEL_SPELL_R0 + (SKEL_SPELL_FRAMES * 3);
	public static final int SKEL_SPELL_R4 = SKEL_SPELL_R0 + (SKEL_SPELL_FRAMES * 4);

	public static final int SKEL_PAIN_FRAMES = 1;
	public static final int SKEL_PAIN_R0 = SKEL_SLASH_R0 + 1;
	public static final int SKEL_PAIN_R1 = SKEL_SLASH_R1 + 1;
	public static final int SKEL_PAIN_R2 = SKEL_SLASH_R2 + 1;
	public static final int SKEL_PAIN_R3 = SKEL_SLASH_R3 + 1;
	public static final int SKEL_PAIN_R4 = SKEL_SLASH_R4 + 1;

	public static final int SKEL_TELEPORT = 1361;
	public static final int SKEL_DIE = 1320;

	//////////////////////
	//
	// GORO
	//
	//////////////////////

	public static final int GORO_RUN_FRAMES = 4;
	public static final int GORO_RUN_R0 = 1469;
	public static final int GORO_RUN_R1 = GORO_RUN_R0 + (GORO_RUN_FRAMES * 1);
	public static final int GORO_RUN_R2 = GORO_RUN_R0 + (GORO_RUN_FRAMES * 2);
	public static final int GORO_RUN_R3 = GORO_RUN_R0 + (GORO_RUN_FRAMES * 3);
	public static final int GORO_RUN_R4 = GORO_RUN_R0 + (GORO_RUN_FRAMES * 4);

	public static final int GORO_CHOP_FRAMES = 3;
	public static final int GORO_CHOP_R0 = 1489;
	public static final int GORO_CHOP_R1 = GORO_CHOP_R0 + (GORO_CHOP_FRAMES * 1);
	public static final int GORO_CHOP_R2 = GORO_CHOP_R0 + (GORO_CHOP_FRAMES * 2);
	public static final int GORO_CHOP_R3 = GORO_RUN_R3;
	public static final int GORO_CHOP_R4 = GORO_RUN_R4;

	public static final int GORO_STAND_R0 = GORO_CHOP_R0;
	public static final int GORO_STAND_R1 = GORO_CHOP_R1;
	public static final int GORO_STAND_R2 = GORO_CHOP_R2;
	public static final int GORO_STAND_R3 = GORO_CHOP_R3;
	public static final int GORO_STAND_R4 = GORO_CHOP_R4;

	public static final int GORO_SPELL_FRAMES = 2;
	public static final int GORO_SPELL_R0 = 1513;
	public static final int GORO_SPELL_R1 = GORO_SPELL_R0 + (GORO_SPELL_FRAMES * 1);
	public static final int GORO_SPELL_R2 = GORO_SPELL_R0 + (GORO_SPELL_FRAMES * 2);
	public static final int GORO_SPELL_R3 = GORO_RUN_R3;
	public static final int GORO_SPELL_R4 = GORO_RUN_R4;

	public static final int GORO_DIE = 1504;
	public static final int GORO_DEAD = 1504 + 8;

	//////////////////////
	//
	// FIREBALL
	//
	//////////////////////

	public static final int FIREBALL_FRAMES = 4;
	public static final int FIREBALL_R0 = 3192;
	public static final int FIREBALL_R1 = FIREBALL_R0 + (FIREBALL_FRAMES * 1);
	public static final int FIREBALL_R2 = FIREBALL_R0 + (FIREBALL_FRAMES * 2);
	public static final int FIREBALL_R3 = FIREBALL_R0 + (FIREBALL_FRAMES * 3);
	public static final int FIREBALL_R4 = FIREBALL_R0 + (FIREBALL_FRAMES * 4);

	//////////////////////
	//
	// HORNET
	//
	//////////////////////

	public static final int HORNET_RUN_FRAMES = 2;
	public static final int HORNET_RUN_R0 = 800;
	public static final int HORNET_RUN_R1 = HORNET_RUN_R0 + (HORNET_RUN_FRAMES * 1);
	public static final int HORNET_RUN_R2 = HORNET_RUN_R0 + (HORNET_RUN_FRAMES * 2);
	public static final int HORNET_RUN_R3 = HORNET_RUN_R0 + (HORNET_RUN_FRAMES * 3);
	public static final int HORNET_RUN_R4 = HORNET_RUN_R0 + (HORNET_RUN_FRAMES * 4);

	public static final int HORNET_DIE = 810;
	public static final int HORNET_DEAD = 811;

	//////////////////////
	//
	// SKULL
	//
	//////////////////////

	public static final int SKULL_FRAMES = 1;
	public static final int SKULL_R0 = 820;
	public static final int SKULL_R1 = SKULL_R0 + (SKULL_FRAMES * 1);
	public static final int SKULL_R2 = SKULL_R0 + (SKULL_FRAMES * 2);
	public static final int SKULL_R3 = SKULL_R0 + (SKULL_FRAMES * 3);
	public static final int SKULL_R4 = SKULL_R0 + (SKULL_FRAMES * 4);

	public static final int SKULL_DIE = 825;
	public static final int SKULL_EXPLODE = 825;

	public static final int SKULL_SERP = (SKULL_R0 + 2);

	// MECHANICAL BETTY VERSION
	public static final int BETTY_FRAMES = 3;
	public static final int BETTY_R0 = 817;
	public static final int BETTY_R1 = BETTY_R0;
	public static final int BETTY_R2 = BETTY_R0;
	public static final int BETTY_R3 = BETTY_R0;
	public static final int BETTY_R4 = BETTY_R0;

	//////////////////////
	//
	// RIPPER
	//
	//////////////////////
	public static final int RIPPER_RUN_FRAMES = 4;
	public static final int RIPPER_RUN_R0 = 1580;
	public static final int RIPPER_RUN_R1 = RIPPER_RUN_R0 + (RIPPER_RUN_FRAMES * 1);
	public static final int RIPPER_RUN_R2 = RIPPER_RUN_R0 + (RIPPER_RUN_FRAMES * 2);
	public static final int RIPPER_RUN_R3 = RIPPER_RUN_R0 + (RIPPER_RUN_FRAMES * 3);
	public static final int RIPPER_RUN_R4 = RIPPER_RUN_R0 + (RIPPER_RUN_FRAMES * 4);

	public static final int RIPPER_JUMP_FRAMES = 2;
	public static final int RIPPER_JUMP_R0 = 1620;
	public static final int RIPPER_JUMP_R1 = RIPPER_JUMP_R0 + (RIPPER_JUMP_FRAMES * 1);
	public static final int RIPPER_JUMP_R2 = RIPPER_JUMP_R0 + (RIPPER_JUMP_FRAMES * 2);
	public static final int RIPPER_JUMP_R3 = RIPPER_JUMP_R0 + (RIPPER_JUMP_FRAMES * 3);
	public static final int RIPPER_JUMP_R4 = RIPPER_JUMP_R0 + (RIPPER_JUMP_FRAMES * 4);

	public static final int RIPPER_FALL_R0 = RIPPER_JUMP_R0 + 1;
	public static final int RIPPER_FALL_R1 = RIPPER_JUMP_R1 + 1;
	public static final int RIPPER_FALL_R2 = RIPPER_JUMP_R2 + 1;
	public static final int RIPPER_FALL_R3 = RIPPER_JUMP_R3 + 1;
	public static final int RIPPER_FALL_R4 = RIPPER_JUMP_R4 + 1;

	public static final int RIPPER_STAND_R0 = RIPPER_JUMP_R0;
	public static final int RIPPER_STAND_R1 = RIPPER_JUMP_R1;
	public static final int RIPPER_STAND_R2 = RIPPER_JUMP_R2;
	public static final int RIPPER_STAND_R3 = RIPPER_JUMP_R3;
	public static final int RIPPER_STAND_R4 = RIPPER_JUMP_R4;

	public static final int RIPPER_HANG_FRAMES = 1;
	public static final int RIPPER_HANG_R0 = 1645;
	public static final int RIPPER_HANG_R1 = 1645;
	public static final int RIPPER_HANG_R2 = 1639;
	public static final int RIPPER_HANG_R3 = 1637;
	public static final int RIPPER_HANG_R4 = 1635;

	public static final int RIPPER_SWIPE_FRAMES = 4;
	public static final int RIPPER_SWIPE_R0 = 1600;
	public static final int RIPPER_SWIPE_R1 = RIPPER_SWIPE_R0 + (RIPPER_SWIPE_FRAMES * 1);
	public static final int RIPPER_SWIPE_R2 = RIPPER_SWIPE_R0 + (RIPPER_SWIPE_FRAMES * 2);
	public static final int RIPPER_SWIPE_R3 = RIPPER_SWIPE_R0 + (RIPPER_SWIPE_FRAMES * 3);
	public static final int RIPPER_SWIPE_R4 = RIPPER_SWIPE_R0 + (RIPPER_SWIPE_FRAMES * 4);

	public static final int RIPPER_HEART_FRAMES = 1;
	public static final int RIPPER_HEART_R0 = 1635;
	public static final int RIPPER_HEART_R1 = RIPPER_HEART_R0;
	public static final int RIPPER_HEART_R2 = RIPPER_HEART_R0;
	public static final int RIPPER_HEART_R3 = RIPPER_HEART_R0;
	public static final int RIPPER_HEART_R4 = RIPPER_HEART_R0;

	public static final int RIPPER_DIE = 1640;
	public static final int RIPPER_DEAD = 1643;

	// The new Ripper
	public static final int RIPPER2_RUN_FRAMES = 4;
	public static final int RIPPER2_RUN_R0 = 4320;
	public static final int RIPPER2_RUN_R1 = RIPPER2_RUN_R0 + (RIPPER2_RUN_FRAMES * 1);
	public static final int RIPPER2_RUN_R2 = RIPPER2_RUN_R0 + (RIPPER2_RUN_FRAMES * 2);
	public static final int RIPPER2_RUN_R3 = RIPPER2_RUN_R0 + (RIPPER2_RUN_FRAMES * 3);
	public static final int RIPPER2_RUN_R4 = RIPPER2_RUN_R0 + (RIPPER2_RUN_FRAMES * 4);

	public static final int RIPPER2_RUNFAST_FRAMES = 4;
	public static final int RIPPER2_RUNFAST_R0 = 4340;
	public static final int RIPPER2_RUNFAST_R1 = RIPPER2_RUNFAST_R0 + (RIPPER2_RUNFAST_FRAMES * 1);
	public static final int RIPPER2_RUNFAST_R2 = RIPPER2_RUNFAST_R0 + (RIPPER2_RUNFAST_FRAMES * 2);
	public static final int RIPPER2_RUNFAST_R3 = RIPPER2_RUNFAST_R0 + (RIPPER2_RUNFAST_FRAMES * 3);
	public static final int RIPPER2_RUNFAST_R4 = RIPPER2_RUNFAST_R0 + (RIPPER2_RUNFAST_FRAMES * 4);

	public static final int RIPPER2_JUMP_FRAMES = 4;
	public static final int RIPPER2_JUMP_R0 = 4374;
	public static final int RIPPER2_JUMP_R1 = RIPPER2_JUMP_R0 + (RIPPER2_JUMP_FRAMES * 1);
	public static final int RIPPER2_JUMP_R2 = RIPPER2_JUMP_R0 + (RIPPER2_JUMP_FRAMES * 2);
	public static final int RIPPER2_JUMP_R3 = RIPPER2_JUMP_R0 + (RIPPER2_JUMP_FRAMES * 3);
	public static final int RIPPER2_JUMP_R4 = RIPPER2_JUMP_R0 + (RIPPER2_JUMP_FRAMES * 4);

	public static final int RIPPER2_FALL_R0 = RIPPER2_JUMP_R0 + 2;
	public static final int RIPPER2_FALL_R1 = RIPPER2_JUMP_R1 + 2;
	public static final int RIPPER2_FALL_R2 = RIPPER2_JUMP_R2 + 2;
	public static final int RIPPER2_FALL_R3 = RIPPER2_JUMP_R3 + 2;
	public static final int RIPPER2_FALL_R4 = RIPPER2_JUMP_R4 + 2;

	public static final int RIPPER2_STAND_R0 = RIPPER2_JUMP_R0 + 3;
	public static final int RIPPER2_STAND_R1 = RIPPER2_JUMP_R1 + 3;
	public static final int RIPPER2_STAND_R2 = RIPPER2_JUMP_R2 + 3;
	public static final int RIPPER2_STAND_R3 = RIPPER2_JUMP_R3 + 3;
	public static final int RIPPER2_STAND_R4 = RIPPER2_JUMP_R4 + 3;

	public static final int RIPPER2_HANG_FRAMES = 1;
	public static final int RIPPER2_HANG_R0 = 4369;
	public static final int RIPPER2_HANG_R1 = RIPPER2_HANG_R0 + (RIPPER2_HANG_FRAMES * 1);
	public static final int RIPPER2_HANG_R2 = RIPPER2_HANG_R0 + (RIPPER2_HANG_FRAMES * 2);
	public static final int RIPPER2_HANG_R3 = RIPPER2_HANG_R0 + (RIPPER2_HANG_FRAMES * 3);
	public static final int RIPPER2_HANG_R4 = RIPPER2_HANG_R0 + (RIPPER2_HANG_FRAMES * 4);

	public static final int RIPPER2_SWIPE_FRAMES = 4;
	public static final int RIPPER2_SWIPE_R0 = 4394;
	public static final int RIPPER2_SWIPE_R1 = RIPPER2_SWIPE_R0 + (RIPPER2_SWIPE_FRAMES * 1);
	public static final int RIPPER2_SWIPE_R2 = RIPPER2_SWIPE_R0 + (RIPPER2_SWIPE_FRAMES * 2);
	public static final int RIPPER2_SWIPE_R3 = RIPPER2_SWIPE_R0 + (RIPPER2_SWIPE_FRAMES * 3);
	public static final int RIPPER2_SWIPE_R4 = RIPPER2_SWIPE_R0 + (RIPPER2_SWIPE_FRAMES * 4);

	public static final int RIPPER2_MEKONG_FRAMES = 3;
	public static final int RIPPER2_MEKONG_R0 = 4360;
	public static final int RIPPER2_MEKONG_R1 = RIPPER2_MEKONG_R0 + (RIPPER2_MEKONG_FRAMES * 1);
	public static final int RIPPER2_MEKONG_R2 = RIPPER2_MEKONG_R0 + (RIPPER2_MEKONG_FRAMES * 2);
	public static final int RIPPER2_MEKONG_R3 = RIPPER2_MEKONG_R0 + (RIPPER2_MEKONG_FRAMES * 3);
	public static final int RIPPER2_MEKONG_R4 = RIPPER2_MEKONG_R0 + (RIPPER2_MEKONG_FRAMES * 4);

	public static final int RIPPER2_HEART_FRAMES = 2;
	public static final int RIPPER2_HEART_R0 = 4422;
	public static final int RIPPER2_HEART_R1 = RIPPER2_HEART_R0;
	public static final int RIPPER2_HEART_R2 = RIPPER2_HEART_R0;
	public static final int RIPPER2_HEART_R3 = RIPPER2_HEART_R0;
	public static final int RIPPER2_HEART_R4 = RIPPER2_HEART_R0;

	public static final int RIPPER2_DIE = 4414; // Has 8 frames now
	public static final int RIPPER2_DEAD = 4421;

	//////////////////////
	//
	// EEL
	//
	//////////////////////

	public static final int EEL_RUN_FRAMES = 3;
	public static final int EEL_RUN_R0 = 3780;
	public static final int EEL_RUN_R1 = EEL_RUN_R0 + (EEL_RUN_FRAMES * 1);
	public static final int EEL_RUN_R2 = EEL_RUN_R0 + (EEL_RUN_FRAMES * 2);
	public static final int EEL_RUN_R3 = EEL_RUN_R0 + (EEL_RUN_FRAMES * 3);
	public static final int EEL_RUN_R4 = EEL_RUN_R0 + (EEL_RUN_FRAMES * 4);

	public static final int EEL_FIRE_FRAMES = 3;
	public static final int EEL_FIRE_R0 = 3760;
	public static final int EEL_FIRE_R1 = EEL_FIRE_R0 + (EEL_FIRE_FRAMES * 1);
	public static final int EEL_FIRE_R2 = EEL_FIRE_R0 + (EEL_FIRE_FRAMES * 2);
	public static final int EEL_FIRE_R3 = EEL_FIRE_R0 + (EEL_FIRE_FRAMES * 3);
	public static final int EEL_FIRE_R4 = EEL_RUN_R0 + (EEL_RUN_FRAMES * 4);

	public static final int EEL_DIE = 3795;
	public static final int EEL_DEAD = 3795;

	//////////////////////
	//
	// SUMO
	//
	//////////////////////

	public static final int SUMO_RUN_FRAMES = 4;
	public static final int SUMO_RUN_R0 = 1210;
	public static final int SUMO_RUN_R1 = SUMO_RUN_R0 + (SUMO_RUN_FRAMES * 1);
	public static final int SUMO_RUN_R2 = SUMO_RUN_R0 + (SUMO_RUN_FRAMES * 2);
	public static final int SUMO_RUN_R3 = SUMO_RUN_R0 + (SUMO_RUN_FRAMES * 3);
	public static final int SUMO_RUN_R4 = SUMO_RUN_R0 + (SUMO_RUN_FRAMES * 4);

	public static final int SUMO_PAIN_FRAMES = 1;
	public static final int SUMO_PAIN_R0 = 1265;
	public static final int SUMO_PAIN_R1 = SUMO_PAIN_R0; // + (SUMO_PAIN_FRAMES * 1);
	public static final int SUMO_PAIN_R2 = SUMO_PAIN_R0; // + (SUMO_PAIN_FRAMES * 2);
	public static final int SUMO_PAIN_R3 = SUMO_PAIN_R0; // + (SUMO_PAIN_FRAMES * 3);
	public static final int SUMO_PAIN_R4 = SUMO_PAIN_R0; // + (SUMO_PAIN_FRAMES * 4);

	public static final int SUMO_STOMP_FRAMES = 3;
	public static final int SUMO_STOMP_R0 = 1230;
	public static final int SUMO_STOMP_R1 = SUMO_STOMP_R0 + (SUMO_STOMP_FRAMES * 1);
	public static final int SUMO_STOMP_R2 = SUMO_STOMP_R0 + (SUMO_STOMP_FRAMES * 2);
	public static final int SUMO_STOMP_R3 = SUMO_STOMP_R0 + (SUMO_STOMP_FRAMES * 3);
	public static final int SUMO_STOMP_R4 = SUMO_STOMP_R0 + (SUMO_STOMP_FRAMES * 4);

	public static final int SUMO_CLAP_FRAMES = 4;
	public static final int SUMO_CLAP_R0 = 1245;
	public static final int SUMO_CLAP_R1 = SUMO_CLAP_R0 + (SUMO_CLAP_FRAMES * 1);
	public static final int SUMO_CLAP_R2 = SUMO_CLAP_R0 + (SUMO_CLAP_FRAMES * 2);
	public static final int SUMO_CLAP_R3 = SUMO_CLAP_R0 + (SUMO_CLAP_FRAMES * 3);
	public static final int SUMO_CLAP_R4 = SUMO_CLAP_R0 + (SUMO_CLAP_FRAMES * 4);

	public static final int SUMO_FART_FRAMES = 4;
	public static final int SUMO_FART_R0 = 1280;
	public static final int SUMO_FART_R1 = SUMO_FART_R0;
	public static final int SUMO_FART_R2 = SUMO_FART_R0;
	public static final int SUMO_FART_R3 = SUMO_FART_R0;
	public static final int SUMO_FART_R4 = SUMO_FART_R0;

	public static final int SUMO_DIE = 1270;
	public static final int SUMO_DEAD = 1277;

	//////////////////////
	//
	// GIRLS
	//
	//////////////////////
	public static final int TOILETGIRL_R0 = 5023;
	public static final int WASHGIRL_R0 = 5032;
	public static final int MECHANICGIRL_R0 = 4590;
	public static final int CARGIRL_R0 = 4594;
	public static final int SAILORGIRL_R0 = 4600;
	public static final int PRUNEGIRL_R0 = 4604;

	// Pachinko machines
	public static final int PACHINKO1 = 4768;
	public static final int PACHINKO2 = 4792;
	public static final int PACHINKO3 = 4816;
	public static final int PACHINKO4 = 4840;

	/////////////////////////////////////////////////////////////////////////////////

	//////////////////////
	//
	// BUNNY
	//
	//////////////////////
	public static final int BUNNY_RUN_FRAMES = 5;
	public static final int BUNNY_RUN_R0 = 4550;
	public static final int BUNNY_RUN_R1 = BUNNY_RUN_R0 + (BUNNY_RUN_FRAMES * 1);
	public static final int BUNNY_RUN_R2 = BUNNY_RUN_R0 + (BUNNY_RUN_FRAMES * 2);
	public static final int BUNNY_RUN_R3 = BUNNY_RUN_R0 + (BUNNY_RUN_FRAMES * 3);
	public static final int BUNNY_RUN_R4 = BUNNY_RUN_R0 + (BUNNY_RUN_FRAMES * 4);

	public static final int BUNNY_STAND_R0 = BUNNY_RUN_R0;
	public static final int BUNNY_STAND_R1 = BUNNY_RUN_R1;
	public static final int BUNNY_STAND_R2 = BUNNY_RUN_R2;
	public static final int BUNNY_STAND_R3 = BUNNY_RUN_R3;
	public static final int BUNNY_STAND_R4 = BUNNY_RUN_R4;

	public static final int BUNNY_SWIPE_FRAMES = 5;
	public static final int BUNNY_SWIPE_R0 = 4575;
	public static final int BUNNY_SWIPE_R1 = BUNNY_SWIPE_R0 + (BUNNY_SWIPE_FRAMES * 1);
	public static final int BUNNY_SWIPE_R2 = BUNNY_SWIPE_R0 + (BUNNY_SWIPE_FRAMES * 1);
	public static final int BUNNY_SWIPE_R3 = BUNNY_SWIPE_R0 + (BUNNY_SWIPE_FRAMES * 1);
	public static final int BUNNY_SWIPE_R4 = BUNNY_SWIPE_R0 + (BUNNY_SWIPE_FRAMES * 1);

	public static final int BUNNY_DIE = 4580;
	public static final int BUNNY_DEAD = 4584;

	//////////////////////
	//
	// EVIL GIRLNINJA
	//
	//////////////////////

	public static final int GIRLNINJA_RUN_FRAMES = 4;
	public static final int GIRLNINJA_RUN_R0 = 5162;
	public static final int GIRLNINJA_RUN_R1 = GIRLNINJA_RUN_R0 + (GIRLNINJA_RUN_FRAMES * 1);
	public static final int GIRLNINJA_RUN_R2 = GIRLNINJA_RUN_R0 + (GIRLNINJA_RUN_FRAMES * 2);
	public static final int GIRLNINJA_RUN_R3 = GIRLNINJA_RUN_R0 + (GIRLNINJA_RUN_FRAMES * 3);
	public static final int GIRLNINJA_RUN_R4 = GIRLNINJA_RUN_R0 + (GIRLNINJA_RUN_FRAMES * 4);

	public static final int GIRLNINJA_STAND_FRAMES = 1;
	public static final int GIRLNINJA_STAND_R0 = GIRLNINJA_RUN_R0;
	public static final int GIRLNINJA_STAND_R1 = GIRLNINJA_RUN_R1;
	public static final int GIRLNINJA_STAND_R2 = GIRLNINJA_RUN_R2;
	public static final int GIRLNINJA_STAND_R3 = GIRLNINJA_RUN_R3;
	public static final int GIRLNINJA_STAND_R4 = GIRLNINJA_RUN_R4;

	public static final int GIRLNINJA_CRAWL_FRAMES = 1;
	public static final int GIRLNINJA_CRAWL_R0 = 5211;
	public static final int GIRLNINJA_CRAWL_R1 = GIRLNINJA_CRAWL_R0 + (GIRLNINJA_CRAWL_FRAMES * 1);
	public static final int GIRLNINJA_CRAWL_R2 = GIRLNINJA_CRAWL_R0 + (GIRLNINJA_CRAWL_FRAMES * 2);
	public static final int GIRLNINJA_CRAWL_R3 = GIRLNINJA_CRAWL_R0 + (GIRLNINJA_CRAWL_FRAMES * 3);
	public static final int GIRLNINJA_CRAWL_R4 = GIRLNINJA_CRAWL_R0 + (GIRLNINJA_CRAWL_FRAMES * 4);

	public static final int GIRLNINJA_THROW_FRAMES = 3;
	public static final int GIRLNINJA_THROW_R0 = 5246;
	public static final int GIRLNINJA_THROW_R1 = GIRLNINJA_THROW_R0 + (GIRLNINJA_THROW_FRAMES * 1);
	public static final int GIRLNINJA_THROW_R2 = GIRLNINJA_THROW_R0 + (GIRLNINJA_THROW_FRAMES * 2);

	// don't have rotations for pain frames currently
	
	public static final int GIRLNINJA_PAIN_FRAMES = 1;
	public static final int GIRLNINJA_PAIN_R0 = 5192;
	public static final int GIRLNINJA_PAIN_R1 = GIRLNINJA_PAIN_R0;
	public static final int GIRLNINJA_PAIN_R2 = GIRLNINJA_PAIN_R0;
	public static final int GIRLNINJA_PAIN_R3 = GIRLNINJA_PAIN_R0;
	public static final int GIRLNINJA_PAIN_R4 = GIRLNINJA_PAIN_R0;

	public static final int GIRLNINJA_JUMP_FRAMES = 4;
	public static final int GIRLNINJA_JUMP_R0 = 5226;
	public static final int GIRLNINJA_JUMP_R1 = GIRLNINJA_JUMP_R0 + (GIRLNINJA_JUMP_FRAMES * 1);
	public static final int GIRLNINJA_JUMP_R2 = GIRLNINJA_JUMP_R0 + (GIRLNINJA_JUMP_FRAMES * 2);
	public static final int GIRLNINJA_JUMP_R3 = GIRLNINJA_JUMP_R0 + (GIRLNINJA_JUMP_FRAMES * 3);
	public static final int GIRLNINJA_JUMP_R4 = GIRLNINJA_JUMP_R0 + (GIRLNINJA_JUMP_FRAMES * 4);

	public static final int GIRLNINJA_FALL_R0 = GIRLNINJA_JUMP_R0 + 2;
	public static final int GIRLNINJA_FALL_R1 = GIRLNINJA_JUMP_R1 + 2;
	public static final int GIRLNINJA_FALL_R2 = GIRLNINJA_JUMP_R2 + 2;
	public static final int GIRLNINJA_FALL_R3 = GIRLNINJA_JUMP_R3 + 2;
	public static final int GIRLNINJA_FALL_R4 = GIRLNINJA_JUMP_R4 + 2;

	public static final int GIRLNINJA_LAND_R0 = GIRLNINJA_JUMP_R0 + 3;
	public static final int GIRLNINJA_LAND_R1 = GIRLNINJA_JUMP_R1 + 3;
	public static final int GIRLNINJA_LAND_R2 = GIRLNINJA_JUMP_R2 + 3;
	public static final int GIRLNINJA_LAND_R3 = GIRLNINJA_JUMP_R3 + 3;
	public static final int GIRLNINJA_LAND_R4 = GIRLNINJA_JUMP_R4 + 3;

	public static final int GIRLNINJA_KNEEL_R0 = GIRLNINJA_CRAWL_R0;
	public static final int GIRLNINJA_KNEEL_R1 = GIRLNINJA_CRAWL_R1;
	public static final int GIRLNINJA_KNEEL_R2 = GIRLNINJA_CRAWL_R2;
	public static final int GIRLNINJA_KNEEL_R3 = GIRLNINJA_CRAWL_R3;
	public static final int GIRLNINJA_KNEEL_R4 = GIRLNINJA_CRAWL_R4;

	public static final int GIRLNINJA_FIRE_FRAMES = 2;
	public static final int GIRLNINJA_FIRE_R0 = 5182;
	public static final int GIRLNINJA_FIRE_R1 = GIRLNINJA_FIRE_R0 + (GIRLNINJA_FIRE_FRAMES * 1);
	public static final int GIRLNINJA_FIRE_R2 = GIRLNINJA_FIRE_R0 + (GIRLNINJA_FIRE_FRAMES * 2);
	public static final int GIRLNINJA_FIRE_R3 = GIRLNINJA_FIRE_R0 + (GIRLNINJA_FIRE_FRAMES * 3);
	public static final int GIRLNINJA_FIRE_R4 = GIRLNINJA_FIRE_R0 + (GIRLNINJA_FIRE_FRAMES * 4);

	public static final int GIRLNINJA_STAB_FRAMES = 1;
	public static final int GIRLNINJA_STAB_R0 = 5206;
	public static final int GIRLNINJA_STAB_R1 = GIRLNINJA_STAB_R0 + (GIRLNINJA_STAB_FRAMES * 1);
	public static final int GIRLNINJA_STAB_R2 = GIRLNINJA_STAB_R0 + (GIRLNINJA_STAB_FRAMES * 2);
	public static final int GIRLNINJA_STAB_R3 = GIRLNINJA_STAB_R0 + (GIRLNINJA_STAB_FRAMES * 3);
	public static final int GIRLNINJA_STAB_R4 = GIRLNINJA_STAB_R0 + (GIRLNINJA_STAB_FRAMES * 4);

	public static final int GIRLNINJA_DIE = 5197;
	public static final int GIRLNINJA_DEAD = GIRLNINJA_DIE + 8;

	public static final int GIRLNINJA_SLICED = 4227;
	public static final int GIRLNINJA_DEAD_SLICED = GIRLNINJA_SLICED + 9;

	//////////////////////
	//
	// ZILLA
	//
	//////////////////////

	public static final int ZILLA_RUN_FRAMES = 4;
	public static final int ZILLA_RUN_R0 = 5426;
	public static final int ZILLA_RUN_R1 = ZILLA_RUN_R0 - (ZILLA_RUN_FRAMES * 1);
	public static final int ZILLA_RUN_R2 = ZILLA_RUN_R0 - (ZILLA_RUN_FRAMES * 2);
	public static final int ZILLA_RUN_R3 = ZILLA_RUN_R0 - (ZILLA_RUN_FRAMES * 3);
	public static final int ZILLA_RUN_R4 = ZILLA_RUN_R0 - (ZILLA_RUN_FRAMES * 4);

	public static final int ZILLA_PAIN_FRAMES = 1;
	public static final int ZILLA_PAIN_R0 = 5524;
	public static final int ZILLA_PAIN_R1 = ZILLA_PAIN_R0 - (ZILLA_PAIN_FRAMES * 1);
	public static final int ZILLA_PAIN_R2 = ZILLA_PAIN_R0 - (ZILLA_PAIN_FRAMES * 2);
	public static final int ZILLA_PAIN_R3 = ZILLA_PAIN_R0 - (ZILLA_PAIN_FRAMES * 3);
	public static final int ZILLA_PAIN_R4 = ZILLA_PAIN_R0 - (ZILLA_PAIN_FRAMES * 4);

	public static final int ZILLA_ROCKET_FRAMES = 4;
	public static final int ZILLA_ROCKET_R0 = 5446;
	public static final int ZILLA_ROCKET_R1 = ZILLA_ROCKET_R0 - (ZILLA_ROCKET_FRAMES * 1);
	public static final int ZILLA_ROCKET_R2 = ZILLA_ROCKET_R0 - (ZILLA_ROCKET_FRAMES * 2);
	public static final int ZILLA_ROCKET_R3 = ZILLA_ROCKET_R0 - (ZILLA_ROCKET_FRAMES * 3);
	public static final int ZILLA_ROCKET_R4 = ZILLA_ROCKET_R0 - (ZILLA_ROCKET_FRAMES * 4);

	public static final int ZILLA_RAIL_FRAMES = 4;
	public static final int ZILLA_RAIL_R0 = 5466;
	public static final int ZILLA_RAIL_R1 = ZILLA_RAIL_R0 - (ZILLA_RAIL_FRAMES * 1);
	public static final int ZILLA_RAIL_R2 = ZILLA_RAIL_R0 - (ZILLA_RAIL_FRAMES * 2);
	public static final int ZILLA_RAIL_R3 = ZILLA_RAIL_R0 - (ZILLA_RAIL_FRAMES * 3);
	public static final int ZILLA_RAIL_R4 = ZILLA_RAIL_R0 - (ZILLA_RAIL_FRAMES * 4);

	public static final int ZILLA_SHOOT_FRAMES = 4;
	public static final int ZILLA_SHOOT_R0 = 5506;
	public static final int ZILLA_SHOOT_R1 = ZILLA_SHOOT_R0 - (ZILLA_SHOOT_FRAMES * 1);
	public static final int ZILLA_SHOOT_R2 = ZILLA_SHOOT_R0 - (ZILLA_SHOOT_FRAMES * 2);
	public static final int ZILLA_SHOOT_R3 = ZILLA_SHOOT_R0 - (ZILLA_SHOOT_FRAMES * 3);
	public static final int ZILLA_SHOOT_R4 = ZILLA_SHOOT_R0 - (ZILLA_SHOOT_FRAMES * 4);

	public static final int ZILLA_RECHARGE_FRAMES = 4;
	public static final int ZILLA_RECHARGE_R0 = 5486;
	public static final int ZILLA_RECHARGE_R1 = ZILLA_RECHARGE_R0 - (ZILLA_RECHARGE_FRAMES * 1);
	public static final int ZILLA_RECHARGE_R2 = ZILLA_RECHARGE_R0 - (ZILLA_RECHARGE_FRAMES * 2);
	public static final int ZILLA_RECHARGE_R3 = ZILLA_RECHARGE_R0 - (ZILLA_RECHARGE_FRAMES * 3);
	public static final int ZILLA_RECHARGE_R4 = ZILLA_RECHARGE_R0 - (ZILLA_RECHARGE_FRAMES * 4);

	public static final int ZILLA_DIE = 5510;
	public static final int ZILLA_DEAD = 5518;

	////////////////////////////////////
	public static final int TRASHCAN = 2540;

	public static final int SCROLL = 516;
	public static final int SCROLL_FIRE = 524;

//////////////////////////////////// PAL

	////
	//
	// Misc Defines
	//
	////

	public static final int ALPHABET = 85;
	public static final int MENU_MAIN = 295;
	public static final int MENU_OPTION = 296;
	public static final int MENU_WHICH = 297;
	public static final int MENU_CURSOR = 298;
	public static final int DART1 = 2000;
	public static final int DART2 = 2001;
	public static final int DART3 = 2002;
	public static final int DART4 = 2003;
	public static final int DART = 2004;
	public static final int FIRE1 = 2005;
	public static final int FIRE2 = 2006;
	public static final int FIRE3 = 2007;
	public static final int FIRE4 = 2008;
	public static final int FIRE5 = 2009;
	public static final int FIREBALL1 = 2010;
	public static final int FIREBALL2 = 2011;
	public static final int FIREBALL3 = 2012;
	public static final int FIREBALL4 = 2013;
	public static final int FLAMES1 = 2014;
	public static final int FLAMES2 = 2015;
	public static final int FLAMES3 = 2016;
	public static final int FLAMES4 = 2017;
	public static final int FLAMES5 = 2018;
	public static final int GAS1 = 2019;
	public static final int GAS2 = 2020;
	public static final int GAS3 = 2021;
	public static final int GAS4 = 2022;
	public static final int GAS5 = 2023;
	public static final int GAS6 = 2024;
	public static final int GAS7 = 2025;
	public static final int GASCLOUD1 = 2026;
	public static final int GASCLOUD2 = 2027;
	public static final int GASCLOUD3 = 2028;
	public static final int GASCLOUD4 = 2029;
	public static final int GASCLOUD5 = 2030;
	public static final int GASCLOUD6 = 2031;
	public static final int GASCLOUD7 = 2032;
	public static final int GASCLOUD8 = 2033;
	public static final int GASCLOUD9 = 2034;
	public static final int LIGHT1 = 2035;
	public static final int LIGHT2 = 2036;
	public static final int LIGHT3 = 2037;
	public static final int LIGHT4 = 2038;
	public static final int LIGHT5 = 2039;
	public static final int LIGHT6 = 2040;
	public static final int ELECTRO1 = 2041;
	public static final int ELECTRO2 = 2042;
	public static final int ELECTRO3 = 2043;
	public static final int RIFLE1 = 2044;
	public static final int RIFLE2 = 2045;
	public static final int RIFLE3 = 2046;
	public static final int DONTUSEEXTRA = 2047;
	public static final int RIFLE4 = 2048;
	public static final int CARTRIGE3 = 2053;
	public static final int STATUE1 = 2054;
	public static final int STATUE2 = 2055;
	public static final int STATUE3 = 2056;
	public static final int STATUE4 = 2057;
	public static final int SWORD3 = 2065;
	public static final int SWORD4 = 2066;
	public static final int SWORD1 = 2067;
	public static final int SWORD2 = 2068;
	public static final int SWORD5 = 2069;
	public static final int BLACK = 2306;
	public static final int FragBarErase = 2375;
	public static final int FragBarErase2 = 2376;

	public static final int DART_PIC = 2526;
	public static final int DART_REPEAT = 16;
	public static final int SLIME = 2305;

	public static final int COMPASS_TIC = 2380;
	public static final int COMPASS_TIC2 = 2381;

	public static final int COMPASS_NORTH = 2382;
	public static final int COMPASS_NORTH2 = 2383;

	public static final int COMPASS_SOUTH = 2384;
	public static final int COMPASS_SOUTH2 = 2385;

	public static final int COMPASS_EAST = 2386;
	public static final int COMPASS_EAST2 = 2387;

	public static final int COMPASS_WEST = 2388;
	public static final int COMPASS_WEST2 = 2389;

	public static final int COMPASS_MID_TIC = 2390;
	public static final int COMPASS_MID_TIC2 = 2391;

	public static final int COMPASS_X = 140;
	public static final int COMPASS_Y = (162 - 5);
}
