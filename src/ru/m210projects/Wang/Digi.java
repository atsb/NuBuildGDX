package ru.m210projects.Wang;

import ru.m210projects.Wang.Type.Amb_Info;
import ru.m210projects.Wang.Type.VOC_INFO;

public class Digi {

	public static final int DIGI_NULL = 0;
	public static final int DIGI_SWORDSWOOSH = 1;
	public static final int DIGI_STAR = 2;
	public static final int DIGI_STARCLINK = 3;
	public static final int DIGI_NULL_STARWIZ = 4;
	public static final int DIGI_UZIFIRE = 5;
	public static final int DIGI_RICHOCHET1 = 6;
	public static final int DIGI_RICHOCHET2 = 7;
	public static final int DIGI_REMOVECLIP = 8;
	public static final int DIGI_REPLACECLIP = 9;
	public static final int DIGI_SHELL = 10;
	public static final int DIGI_RIOTFIRE = 11;
	public static final int DIGI_RIOTFIRE2 = 12;
	public static final int DIGI_RIOTRELOAD = 13;
	public static final int DIGI_BOLTEXPLODE = 14;
	public static final int DIGI_BOLTWIZ = 15;
	public static final int DIGI_30MMFIRE = 16;
	public static final int DIGI_30MMRELOAD = 17;
	public static final int DIGI_30MMEXPLODE = 18;
	public static final int DIGI_30MMWIZ = 19;
	public static final int DIGI_HEADFIRE = 20;
	public static final int DIGI_HEADSHOTWIZ = 21;
	public static final int DIGI_HEADSHOTHIT = 22;
	public static final int DIGI_MINETHROW = 23;
	public static final int DIGI_MINEBOUNCE = 24;
	public static final int DIGI_MINEBLOW = 25;
	public static final int DIGI_MINEBEEP = 26;
	public static final int DIGI_HEARTBEAT = 27;
	public static final int DIGI_HEARTFIRE = 28;
	public static final int DIGI_HEARTWIZ = 29;
	public static final int DIGI_MISSLFIRE = 30;
	public static final int DIGI_MISSLEXP = 31;
	public static final int DIGI_RFWIZ = 32;
	public static final int DIGI_NULL_RFWIZ = 32;
	public static final int DIGI_NAPFIRE = 33;
	public static final int DIGI_NAPWIZ = 34;
	public static final int DIGI_NAPPUFF = 35;
	public static final int DIGI_MIRVFIRE = 36;
	public static final int DIGI_MIRVWIZ = 37;
	public static final int DIGI_SPIRALFIRE = 38;
	public static final int DIGI_SPIRALWIZ = 39;
	public static final int DIGI_MAGIC1 = 40;
	public static final int DIGI_MAGIC2 = 41;
	public static final int DIGI_MAGIC3 = 42;
	public static final int DIGI_MAGIC4 = 43;
	public static final int DIGI_MAGIC5 = 44;
	public static final int DIGI_MAGIC6 = 45;
	public static final int DIGI_MAGIC7 = 46;
	public static final int DIGI_SWCLOAKUNCLOAK = 47;
	public static final int DIGI_NULL_SWCLOAK = 47;
	public static final int DIGI_DHVOMIT = 48;
	public static final int DIGI_DHCLUNK = 49;
	public static final int DIGI_DHSQUISH = 50;
	public static final int DIGI_NULL_DHSQUISH = 50;
	public static final int DIGI_PROJECTILELAVAHIT = 51;
	public static final int DIGI_PROJECTILEWATERHIT = 52;
	public static final int DIGI_KEY = 53;
	public static final int DIGI_ITEM = 54;
	public static final int DIGI_BIGITEM = 55;
	public static final int DIGI_BODYFALL1 = 56;
	public static final int DIGI_HITGROUND = 57;
	public static final int DIGI_BODYSQUISH1 = 58;
	public static final int DIGI_BODYBURN = 59;
	public static final int DIGI_BODYBURNSCREAM = 60;
	public static final int DIGI_BODYCRUSHED1 = 61;
	public static final int DIGI_BODYHACKED1 = 62;
	public static final int DIGI_BODYSINGED = 63;
	public static final int DIGI_DROWN = 64;
	public static final int DIGI_SCREAM1 = 65;
	public static final int DIGI_SCREAM2 = 66;
	public static final int DIGI_SCREAM3 = 67;
	public static final int DIGI_HIT1 = 68;
	public static final int DIGI_ELECTRICUTE1 = 69;
	public static final int DIGI_REMOVEME = 70;
	public static final int DIGI_IMPALED = 71;
	public static final int DIGI_OOF1 = 72;
	public static final int DIGI_ACTORBODYFALL1 = 73;
	public static final int DIGI_ACTORHITGROUND = 74;
	public static final int DIGI_COOLIEEXPLODE = 75;
	public static final int DIGI_COOLIESCREAM = 76;
	public static final int DIGI_COOLIEALERT = 77;
	public static final int DIGI_COOLIEAMBIENT = 78;
	public static final int DIGI_COOLIEPAIN = 79;
	public static final int DIGI_CGMATERIALIZE = 80;
	public static final int DIGI_CGALERT = 81;
	public static final int DIGI_CGTHIGHBONE = 82;
	public static final int DIGI_CGAMBIENT = 83;
	public static final int DIGI_CGPAIN = 84;
	public static final int DIGI_CGMAGIC = 85;
	public static final int DIGI_CGMAGICHIT = 86;
	public static final int DIGI_CGSCREAM = 87;
	public static final int DIGI_NINJAAMBIENT = 88;
	public static final int DIGI_NINJASTAR = 89;
	public static final int DIGI_NINJAPAIN = 90;
	public static final int DIGI_NINJASCREAM = 91;
	public static final int DIGI_NINJAALERT = 92;
	public static final int DIGI_NINJAUZIATTACK = 93;
	public static final int DIGI_NINJARIOTATTACK = 94;
	public static final int DIGI_RIPPERAMBIENT = 95;
	public static final int DIGI_RIPPERALERT = 96;
	public static final int DIGI_RIPPERATTACK = 97;
	public static final int DIGI_RIPPERPAIN = 98;
	public static final int DIGI_RIPPERSCREAM = 99;
	public static final int DIGI_RIPPERHEARTOUT = 100;
	public static final int DIGI_GRDAMBIENT = 101;
	public static final int DIGI_GRDALERT = 102;
	public static final int DIGI_GRDPAIN = 103;
	public static final int DIGI_GRDSCREAM = 104;
	public static final int DIGI_GRDFIREBALL = 105;
	public static final int DIGI_GRDSWINGAXE = 106;
	public static final int DIGI_GRDAXEHIT = 107;
	public static final int DIGI_SPAMBIENT = 108;
	public static final int DIGI_SPALERT = 109;
	public static final int DIGI_SPPAIN = 110;
	public static final int DIGI_SPSCREAM = 111;
	public static final int DIGI_SPBLADE = 112;
	public static final int DIGI_SPELEC = 113;
	public static final int DIGI_SPTELEPORT = 114;
	public static final int DIGI_AHAMBIENT = 115;
	public static final int DIGI_AHSCREAM = 116;
	public static final int DIGI_AHEXPLODE = 117;
	public static final int DIGI_AHSWOOSH = 118;
	public static final int DIGI_HORNETBUZZ = 119;
	public static final int DIGI_HORNETSTING = 120;
	public static final int DIGI_HORNETPAIN = 121;
	public static final int DIGI_HORNETDEATH = 122;
	public static final int DIGI_SERPAMBIENT = 123;
	public static final int DIGI_SERPALERT = 124;
	public static final int DIGI_SERPPAIN = 125;
	public static final int DIGI_SERPSCREAM = 126;
	public static final int DIGI_SERPDEATHEXPLODE = 127;
	public static final int DIGI_SERPSWORDATTACK = 128;
	public static final int DIGI_SERPMAGICLAUNCH = 129;
	public static final int DIGI_SERPSUMMONHEADS = 130;
	public static final int DIGI_SERPTAUNTYOU = 131;
	public static final int DIGI_LAVABOSSAMBIENT = 132;
	public static final int DIGI_LAVABOSSSWIM = 133;
	public static final int DIGI_LAVABOSSRISE = 134;
	public static final int DIGI_LAVABOSSALERT = 135;
	public static final int DIGI_LAVABOSSFLAME = 136;
	public static final int DIGI_LAVABOSSMETEOR = 137;
	public static final int DIGI_LAVABOSSMETEXP = 138;
	public static final int DIGI_LAVABOSSPAIN = 139;
	public static final int DIGI_LAVABOSSSIZZLE = 140;
	public static final int DIGI_LAVABOSSEXPLODE = 141;
	public static final int DIGI_BOATSTART = 142;
	public static final int DIGI_BOATRUN = 143;
	public static final int DIGI_BOATSTOP = 144;
	public static final int DIGI_BOATFIRE = 145;
	public static final int DIGI_TANKSTART = 146;
	public static final int DIGI_TANKRUN = 147;
	public static final int DIGI_TANKSTOP = 148;
	public static final int DIGI_TANKIDLE = 149;
	public static final int DIGI_TANKFIRE = 150;
	public static final int DIGI_TRUKRUN = 151;
	public static final int DIGI_TRUKIDLE = 152;
	public static final int DIGI_SUBRUN = 153;
	public static final int DIGI_SUBIDLE = 154;
	public static final int DIGI_SUBDOOR = 155;
	public static final int DIGI_BOMBRFLYING = 156;
	public static final int DIGI_BOMBRDROPBOMB = 157;
	public static final int DIGI_BUBBLES = 158;
	public static final int DIGI_CHAIN = 159;
	public static final int DIGI_CHAINDOOR = 160;
	public static final int DIGI_CRICKETS = 161;
	public static final int DIGI_WOODDOOROPEN = 162;
	public static final int DIGI_WOODDOORCLOSE = 163;
	public static final int DIGI_METALDOOROPEN = 164;
	public static final int DIGI_METALDOORCLOSE = 165;
	public static final int DIGI_SLIDEDOOROPEN = 166;
	public static final int DIGI_SLIDEDOORCLOSE = 167;
	public static final int DIGI_STONEDOOROPEN = 168;
	public static final int DIGI_STONEDOORCLOSE = 169;
	public static final int DIGI_SQUEAKYDOOROPEN = 170;
	public static final int DIGI_SQUEAKYDOORCLOSE = 171;
	public static final int DIGI_DRILL = 172;
	public static final int DIGI_CAVEDRIP1 = 173;
	public static final int DIGI_CAVEDRIP2 = 174;
	public static final int DIGI_DRIP = 175;
	public static final int DIGI_WATERFALL1 = 176;
	public static final int DIGI_WATERFALL2 = 177;
	public static final int DIGI_WATERFLOW1 = 178;
	public static final int DIGI_WATERFLOW2 = 179;
	public static final int DIGI_ELEVATOR = 180;
	public static final int DIGI_SMALLEXP = 181;
	public static final int DIGI_MEDIUMEXP = 182;
	public static final int DIGI_LARGEEXP = 183;
	public static final int DIGI_HUGEEXP = 184;
	public static final int DIGI_NULL_HUGEEXP = 184;
	public static final int DIGI_FIRE1 = 185;
	public static final int DIGI_FIRE2 = 186;
	public static final int DIGI_FIREBALL1 = 187;
	public static final int DIGI_FIREBALL2 = 188;
	public static final int DIGI_GEAR1 = 189;
	public static final int DIGI_GONG = 190;
	public static final int DIGI_LAVAFLOW1 = 191;
	public static final int DIGI_MACHINE1 = 192;
	public static final int DIGI_MUBBUBBLES1 = 193;
	public static final int DIGI_EARTHQUAKE = 194;
	public static final int DIGI_SEWERFLOW1 = 195;
	public static final int DIGI_SPLASH1 = 196;
	public static final int DIGI_STEAM1 = 197;
	public static final int DIGI_VOLCANOSTEAM1 = 198;
	public static final int DIGI_STOMPER = 199;
	public static final int DIGI_SWAMP = 200;
	public static final int DIGI_REGULARSWITCH = 201;
	public static final int DIGI_BIGSWITCH = 202;
	public static final int DIGI_STONESWITCH = 203;
	public static final int DIGI_GLASSSWITCH = 204;
	public static final int DIGI_HUGESWITCH = 205;
	public static final int DIGI_THUNDER = 206;
	public static final int DIGI_TELEPORT = 207;
	public static final int DIGI_UNDERWATER = 208;
	public static final int DIGI_UNLOCK = 209;
	public static final int DIGI_SQUEAKYVALVE = 210;
	public static final int DIGI_VOID1 = 211;
	public static final int DIGI_VOID2 = 212;
	public static final int DIGI_VOID3 = 213;
	public static final int DIGI_VOID4 = 214;
	public static final int DIGI_VOID5 = 215;
	public static final int DIGI_ERUPTION = 216;
	public static final int DIGI_VOLCANOPROJECTILE = 217;
	public static final int DIGI_LIGHTWIND = 218;
	public static final int DIGI_STRONGWIND = 219;
	public static final int DIGI_BREAKINGWOOD = 220;
	public static final int DIGI_BREAKSTONES = 221;
	public static final int DIGI_ENGROOM1 = 222;
	public static final int DIGI_ENGROOM2 = 223;
	public static final int DIGI_ENGROOM3 = 224;
	public static final int DIGI_ENGROOM4 = 225;
	public static final int DIGI_ENGROOM5 = 226;
	public static final int DIGI_BREAKGLASS = 227;
	public static final int DIGI_MUSSTING = 228;
	public static final int DIGI_HELI = 229;
	public static final int DIGI_BIGHART = 230;
	public static final int DIGI_WIND4 = 231;
	public static final int DIGI_SPOOKY1 = 232;
	public static final int DIGI_DRILL1 = 233;
	public static final int DIGI_JET = 234;
	public static final int DIGI_DRUMCHANT = 235;
	public static final int DIGI_BUZZZ = 236;
	public static final int DIGI_CHOP_CLICK = 237;
	public static final int DIGI_SWORD_UP = 238;
	public static final int DIGI_UZI_UP = 239;
	public static final int DIGI_SHOTGUN_UP = 240;
	public static final int DIGI_ROCKET_UP = 241;
	public static final int DIGI_GRENADE_UP = 242;
	public static final int DIGI_RAIL_UP = 243;
	public static final int DIGI_MINE_UP = 244;
	public static final int DIGI_FIRSTPLAYERVOICE = 245;
	public static final int DIGI_TAUNTAI1 = 246;
	public static final int DIGI_TAUNTAI2 = 247;
	public static final int DIGI_TAUNTAI3 = 248;
	public static final int DIGI_TAUNTAI4 = 249;
	public static final int DIGI_TAUNTAI5 = 250;
	public static final int DIGI_TAUNTAI6 = 251;
	public static final int DIGI_TAUNTAI7 = 252;
	public static final int DIGI_TAUNTAI8 = 253;
	public static final int DIGI_TAUNTAI9 = 254;
	public static final int DIGI_TAUNTAI10 = 255;
	public static final int DIGI_PLAYERPAIN1 = 256;
	public static final int DIGI_PLAYERPAIN2 = 257;
	public static final int DIGI_PLAYERPAIN3 = 258;
	public static final int DIGI_PLAYERPAIN4 = 259;
	public static final int DIGI_PLAYERPAIN5 = 260;
	public static final int DIGI_PLAYERYELL1 = 261;
	public static final int DIGI_PLAYERYELL2 = 262;
	public static final int DIGI_PLAYERYELL3 = 263;
	public static final int DIGI_SEARCHWALL = 264;
	public static final int DIGI_NOURINAL = 265;
	public static final int DIGI_FALLSCREAM = 266;
	public static final int DIGI_GOTITEM1 = 267;
	public static final int DIGI_LASTPLAYERVOICE = 268;
	public static final int DIGI_RAILFIRE = 269;
	public static final int DIGI_NULL_RAILFIRE = 269;
	public static final int DIGI_RAILREADY = 270;
	public static final int DIGI_RAILPWRUP = 271;
	public static final int DIGI_NUCLEAREXP = 272;
	public static final int DIGI_NUKESTDBY = 273;
	public static final int DIGI_NUKECDOWN = 274;
	public static final int DIGI_NUKEREADY = 275;
	public static final int DIGI_CHEMGAS = 276;
	public static final int DIGI_CHEMBOUNCE = 277;
	public static final int DIGI_THROW = 278;
	public static final int DIGI_PULL = 279;
	public static final int DIGI_MINEARM = 280;
	public static final int DIGI_HEARTDOWN = 281;
	public static final int DIGI_TOOLBOX = 282;
	public static final int DIGI_NULL_TOOLBOX = 282;
	public static final int DIGI_GASPOP = 283;
	public static final int DIGI_40MMBNCE = 284;
	public static final int DIGI_BURGLARALARM = 285;
	public static final int DIGI_CARALARM = 286;
	public static final int DIGI_CARALARMOFF = 287;
	public static final int DIGI_CALTROPS = 288;
	public static final int DIGI_NIGHTON = 289;
	public static final int DIGI_NIGHTOFF = 290;
	public static final int DIGI_SHOTSHELLSPENT = 291;
	public static final int DIGI_BUSSKID = 292;
	public static final int DIGI_BUSCRASH = 293;
	public static final int DIGI_BUSENGINE = 294;
	public static final int DIGI_ARMORHIT = 295;
	public static final int DIGI_ASIREN1 = 296;
	public static final int DIGI_FIRETRK1 = 297;
	public static final int DIGI_TRAFFIC1 = 298;
	public static final int DIGI_TRAFFIC2 = 299;
	public static final int DIGI_TRAFFIC3 = 300;
	public static final int DIGI_TRAFFIC4 = 301;
	public static final int DIGI_TRAFFIC5 = 302;
	public static final int DIGI_TRAFFIC6 = 303;
	public static final int DIGI_HELI1 = 304;
	public static final int DIGI_JET1 = 305;
	public static final int DIGI_MOTO1 = 306;
	public static final int DIGI_MOTO2 = 307;
	public static final int DIGI_NEON1 = 308;
	public static final int DIGI_SUBWAY = 309;
	public static final int DIGI_TRAIN1 = 310;
	public static final int DIGI_COINS = 311;
	public static final int DIGI_SWORDCLANK = 312;
	public static final int DIGI_RIPPER2AMBIENT = 313;
	public static final int DIGI_RIPPER2ALERT = 314;
	public static final int DIGI_RIPPER2ATTACK = 315;
	public static final int DIGI_RIPPER2PAIN = 316;
	public static final int DIGI_RIPPER2SCREAM = 317;
	public static final int DIGI_RIPPER2HEARTOUT = 318;
	public static final int DIGI_M60 = 319;
	public static final int DIGI_SUMOSCREAM = 320;
	public static final int DIGI_SUMOALERT = 321;
	public static final int DIGI_SUMOAMBIENT = 322;
	public static final int DIGI_SUMOPAIN = 323;
	public static final int DIGI_RAMUNLOCK = 324;
	public static final int DIGI_CARDUNLOCK = 325;
	public static final int DIGI_ANCIENTSECRET = 326;
	public static final int DIGI_AMERICANDRIVER = 327;
	public static final int DIGI_DRIVELIKEBABOON = 328;
	public static final int DIGI_BURNBABY = 329;
	public static final int DIGI_LIKEBIGWEAPONS = 330;
	public static final int DIGI_COWABUNGA = 331;
	public static final int DIGI_NOCHARADE = 332;
	public static final int DIGI_TIMETODIE = 333;
	public static final int DIGI_EATTHIS = 334;
	public static final int DIGI_FIRECRACKERUPASS = 335;
	public static final int DIGI_HOLYCOW = 336;
	public static final int DIGI_HOLYPEICESOFCOW = 337;
	public static final int DIGI_HOLYSHIT = 338;
	public static final int DIGI_HOLYPEICESOFSHIT = 339;
	public static final int DIGI_PAYINGATTENTION = 340;
	public static final int DIGI_EVERYBODYDEAD = 341;
	public static final int DIGI_KUNGFU = 342;
	public static final int DIGI_HOWYOULIKEMOVE = 343;
	public static final int DIGI_NOMESSWITHWANG = 344;
	public static final int DIGI_RAWREVENGE = 345;
	public static final int DIGI_YOULOOKSTUPID = 346;
	public static final int DIGI_TINYDICK = 347;
	public static final int DIGI_NOTOURNAMENT = 348;
	public static final int DIGI_WHOWANTSWANG = 349;
	public static final int DIGI_MOVELIKEYAK = 350;
	public static final int DIGI_ALLINREFLEXES = 351;
	public static final int DIGI_EVADEFOREVER = 352;
	public static final int DIGI_MRFLY = 353;
	public static final int DIGI_SHISEISI = 354;
	public static final int DIGI_LIKEFIREWORKS = 355;
	public static final int DIGI_LIKEHIROSHIMA = 356;
	public static final int DIGI_LIKENAGASAKI = 357;
	public static final int DIGI_LIKEPEARL = 358;
	public static final int DIGI_IAMSHADOW = 359;
	public static final int DIGI_ILIKENUKES = 360;
	public static final int DIGI_ILIKESWORD = 361;
	public static final int DIGI_ILIKESHURIKEN = 362;
	public static final int DIGI_BADLUCK = 363;
	public static final int DIGI_NOMOVIEMRCHAN = 364;
	public static final int DIGI_REALLIFEMRCHAN = 365;
	public static final int DIGI_NOLIKEMUSIC = 366;
	public static final int DIGI_NODIFFERENCE = 367;
	public static final int DIGI_NOFEAR = 368;
	public static final int DIGI_NOPAIN = 369;
	public static final int DIGI_NOREPAIRMAN = 370;
	public static final int DIGI_SONOFABITCH = 371;
	public static final int DIGI_PAINFORWEAK = 372;
	public static final int DIGI_GOSPEEDY = 373;
	public static final int DIGI_GETTINGSTIFF = 374;
	public static final int DIGI_TOMBRAIDER = 375;
	public static final int DIGI_STICKYGOTU1 = 376;
	public static final int DIGI_STICKYGOTU2 = 377;
	public static final int DIGI_STICKYGOTU3 = 378;
	public static final int DIGI_STICKYGOTU4 = 379;
	public static final int DIGI_SWORDGOTU1 = 380;
	public static final int DIGI_SWORDGOTU2 = 381;
	public static final int DIGI_SWORDGOTU3 = 382;
	public static final int DIGI_HURTBAD1 = 383;
	public static final int DIGI_HURTBAD2 = 384;
	public static final int DIGI_HURTBAD3 = 385;
	public static final int DIGI_HURTBAD4 = 386;
	public static final int DIGI_HURTBAD5 = 387;
	public static final int DIGI_TOILETGIRLSCREAM = 388;
	public static final int DIGI_TOILETGIRLALERT = 389;
	public static final int DIGI_TOILETGIRLAMBIENT = 390;
	public static final int DIGI_TOILETGIRLPAIN = 391;
	public static final int DIGI_TOILETGIRLTAUNT1 = 392;
	public static final int DIGI_TOILETGIRLTAUNT2 = 393;
	public static final int DIGI_SUMOFART = 394;
	public static final int DIGI_GIBS1 = 395;
	public static final int DIGI_GIBS2 = 396;
	public static final int DIGI_BIRDS1 = 397;
	public static final int DIGI_BIRDS2 = 398;
	public static final int DIGI_TOILET = 399;
	public static final int DIGI_FORKLIFTIDLE = 400;
	public static final int DIGI_FORKLIFTRUN = 401;
	public static final int DIGI_TOYCAR = 402;
	public static final int DIGI_UZIMATIC = 403;
	public static final int DIGI_COMPUTERPOWER = 404;
	public static final int DIGI_GENERATORON = 405;
	public static final int DIGI_GENERATORRUN = 406;
	public static final int DIGI_BIGDRILL = 407;
	public static final int DIGI_FLUORLIGHT = 408;
	public static final int DIGI_AMOEBA = 409;
	public static final int DIGI_BODYFALL2 = 410;
	public static final int DIGI_GIBS3 = 411;
	public static final int DIGI_NINJACHOKE = 412;
	public static final int DIGI_TRAIN3 = 413;
	public static final int DIGI_TRAINR02 = 414;
	public static final int DIGI_TRAIN8 = 415;
	public static final int DIGI_TRASHLID = 416;
	public static final int DIGI_GETMEDKIT = 417;
	public static final int DIGI_AHH = 418;
	public static final int DIGI_PALARM = 419;
	public static final int DIGI_PFLIP = 420;
	public static final int DIGI_PROLL1 = 421;
	public static final int DIGI_PROLL2 = 422;
	public static final int DIGI_PROLL3 = 423;
	public static final int DIGI_BUNNYATTACK = 424;
	public static final int DIGI_BUNNYDIE1 = 425;
	public static final int DIGI_BUNNYDIE2 = 426;
	public static final int DIGI_BUNNYDIE3 = 427;
	public static final int DIGI_BUNNYAMBIENT = 428;
	public static final int DIGI_STONESLIDE = 429;
	public static final int DIGI_NINJAINHALF = 430;
	public static final int DIGI_RIPPER2CHEST = 431;
	public static final int DIGI_WHIPME = 432;
	public static final int DIGI_ENDLEV = 433;
	public static final int DIGI_MDALARM = 434;
	public static final int DIGI_BREAKMETAL = 435;
	public static final int DIGI_BREAKDEBRIS = 436;
	public static final int DIGI_BREAKMARBELS = 437;
	public static final int DIGI_BANZAI = 438;
	public static final int DIGI_HAHA1 = 439;
	public static final int DIGI_HAHA2 = 440;
	public static final int DIGI_HAHA3 = 441;
	public static final int DIGI_ITEM_SPAWN = 442;
	public static final int DIGI_NOREPAIRMAN2 = 443;
	public static final int DIGI_NOPOWER = 444;
	public static final int DIGI_DOUBLEUZI = 445;
	public static final int DIGI_NOTORDBUNNY = 446;
	public static final int DIGI_CANBEONLYONE = 447;
	public static final int DIGI_MIRROR1 = 448;
	public static final int DIGI_MIRROR2 = 449;
	public static final int DIGI_HITTINGWALLS = 450;
	public static final int DIGI_GOTRAILGUN = 451;
	public static final int DIGI_RABBITHUMP1 = 452;
	public static final int DIGI_RABBITHUMP2 = 453;
	public static final int DIGI_RABBITHUMP3 = 454;
	public static final int DIGI_RABBITHUMP4 = 455;
	public static final int DIGI_FAGRABBIT1 = 456;
	public static final int DIGI_FAGRABBIT2 = 457;
	public static final int DIGI_FAGRABBIT3 = 458;
	public static final int DIGI_STINKLIKEBABBOON = 459;
	public static final int DIGI_WHATYOUEATBABY = 460;
	public static final int DIGI_WHATDIEDUPTHERE = 461;
	public static final int DIGI_YOUGOPOOPOO = 462;
	public static final int DIGI_PULLMYFINGER = 463;
	public static final int DIGI_SOAPYOUGOOD = 464;
	public static final int DIGI_WASHWANG = 465;
	public static final int DIGI_DROPSOAP = 466;
	public static final int DIGI_REALTITS = 467;
	public static final int DIGI_MSTRLEEP = 468;
	public static final int DIGI_SEEKLEEPADVICE = 469;
	public static final int DIGI_AVENGELEEPDEATH = 470;
	public static final int DIGI_LEEPGHOST = 471;
	public static final int DIGI_DOOR1 = 472;
	public static final int DIGI_DOOR2 = 473;
	public static final int DIGI_DOOR3 = 474;
	public static final int DIGI_FLAGWAVE = 475;
	public static final int DIGI_SURFACE = 476;
	public static final int DIGI_GASHURT = 477;
	public static final int DIGI_BONUS_GRAB = 478;
	public static final int DIGI_ANIMECRY = 479;
	public static final int DIGI_ANIMESING1 = 480;
	public static final int DIGI_ANIMEMAD1 = 481;
	public static final int DIGI_ANIMESING2 = 482;
	public static final int DIGI_ANIMEMAD2 = 483;
	public static final int DIGI_PLAYER_TELEPORT = 484;
	public static final int DIGI_INTRO_SLASH = 485;
	public static final int DIGI_WARNING = 486;
	public static final int DIGI_INTRO_WHIRL = 487;
	public static final int DIGI_TOILETGIRLFART1 = 488;
	public static final int DIGI_TOILETGIRLFART2 = 489;
	public static final int DIGI_TOILETGIRLFART3 = 490;
	public static final int DIGI_WINDCHIMES = 491;
	public static final int DIGI_MADATCARPET = 492;
	public static final int DIGI_JUMPONCARPET = 493;
	public static final int DIGI_USEBROKENVEHICLE = 494;
	public static final int DIGI_STEPONCALTROPS = 495;
	public static final int DIGI_WANGSEESERP = 496;
	public static final int DIGI_SERPTAUNTWANG = 497;
	public static final int DIGI_WANGTAUNTSERP1 = 498;
	public static final int DIGI_WANGTAUNTSERP2 = 499;
	public static final int DIGI_WANGORDER1 = 500;
	public static final int DIGI_WANGORDER2 = 501;
	public static final int DIGI_WANGDROWNING = 502;
	public static final int DIGI_ZILLAREGARDS = 503;
	public static final int DIGI_PMESSAGE = 504;
	public static final int DIGI_SHAREND_UGLY1 = 505;
	public static final int DIGI_SHAREND_UGLY2 = 506;
	public static final int DIGI_SHAREND_TELEPORT = 507;
	public static final int DIGI_HOTHEADSWITCH = 508;
	public static final int DIGI_BOATCREAK = 509;
	public static final int DIGI_BOATRUN2 = 510;
	public static final int DIGI_BOATIDLE = 511;
	public static final int DIGI_SHIPBELL = 512;
	public static final int DIGI_FOGHORN = 513;
	public static final int DIGI_CANNON = 514;
	public static final int DIGI_JG41001 = 515;
	public static final int DIGI_JG41012 = 516;
	public static final int DIGI_JG41018 = 517;
	public static final int DIGI_JG41028 = 518;
	public static final int DIGI_JG41048 = 519;
	public static final int DIGI_JG41052 = 520;
	public static final int DIGI_JG41058 = 521;
	public static final int DIGI_JG41060 = 522;
	public static final int DIGI_JG41075 = 523;
	public static final int DIGI_JG42004 = 524;
	public static final int DIGI_JG42019 = 525;
	public static final int DIGI_JG42021 = 526;
	public static final int DIGI_JG42028 = 527;
	public static final int DIGI_JG42033 = 528;
	public static final int DIGI_JG42034 = 529;
	public static final int DIGI_JG42050 = 530;
	public static final int DIGI_JG42056 = 531;
	public static final int DIGI_JG42061 = 532;
	public static final int DIGI_JG43004 = 533;
	public static final int DIGI_JG43015 = 534;
	public static final int DIGI_JG43019 = 535;
	public static final int DIGI_JG43021 = 536;
	public static final int DIGI_JG44011 = 537;
	public static final int DIGI_JG44014 = 538;
	public static final int DIGI_JG44027 = 539;
	public static final int DIGI_JG44038 = 540;
	public static final int DIGI_JG44039 = 541;
	public static final int DIGI_JG44048 = 542;
	public static final int DIGI_JG44052 = 543;
	public static final int DIGI_JG45014 = 544;
	public static final int DIGI_JG44068 = 545;
	public static final int DIGI_JG45010 = 546;
	public static final int DIGI_JG45018 = 547;
	public static final int DIGI_JG45030 = 548;
	public static final int DIGI_JG45033 = 549;
	public static final int DIGI_JG45043 = 550;
	public static final int DIGI_JG45053 = 551;
	public static final int DIGI_JG45067 = 552;
	public static final int DIGI_JG46005 = 553;
	public static final int DIGI_JG46010 = 554;
	public static final int DIGI_LANI049 = 555;
	public static final int DIGI_LANI051 = 556;
	public static final int DIGI_LANI052 = 557;
	public static final int DIGI_LANI054 = 558;
	public static final int DIGI_LANI060 = 559;
	public static final int DIGI_LANI063 = 560;
	public static final int DIGI_LANI065 = 561;
	public static final int DIGI_LANI066 = 562;
	public static final int DIGI_LANI073 = 563;
	public static final int DIGI_LANI075 = 564;
	public static final int DIGI_LANI077 = 565;
	public static final int DIGI_LANI079 = 566;
	public static final int DIGI_LANI089 = 567;
	public static final int DIGI_LANI091 = 568;
	public static final int DIGI_LANI093 = 569;
	public static final int DIGI_LANI095 = 570;
	public static final int DIGI_VENTWALK = 571;
	public static final int DIGI_CARWALK = 572;
	public static final int DIGI_JETSOAR = 573;
	public static final int DIGI_VACUUM = 574;

	public static final int DIGI_GIRLNINJAALERTT = 575;
	public static final int DIGI_GIRLNINJASCREAM = 576;
	public static final int DIGI_GIRLNINJAALERT = 577;
	public static final int DIGI_PRUNECACKLE = 578;
	public static final int DIGI_PRUNECACKLE2 = 579;
	public static final int DIGI_PRUNECACKLE3 = 580;
	public static final int DIGI_SUMOSTOMP = 581;
	public static final int DIGI_VATOR = 582;
	public static final int DIGI_JG9009 = 583;
	public static final int DIGI_Z16004 = 584;
	public static final int DIGI_Z16012 = 585;
	public static final int DIGI_Z16022 = 586;
	public static final int DIGI_Z16027 = 587;
	public static final int DIGI_JG93030 = 588;
	public static final int DIGI_JG94002 = 589;
	public static final int DIGI_Z17010 = 590;
	public static final int DIGI_Z17052 = 591;
	public static final int DIGI_Z17025 = 592;
	public static final int DIGI_ML25014 = 593;
	public static final int DIGI_ML250101 = 594;
	public static final int DIGI_JG9022 = 595;
	public static final int DIGI_JG9032 = 596;
	public static final int DIGI_JG9038 = 597;
	public static final int DIGI_JG9055 = 598;
	public static final int DIGI_JG9060 = 599;
	public static final int DIGI_JG92055 = 600;
	public static final int DIGI_ML25032 = 601;
	public static final int DIGI_JG92036 = 602;
	public static final int DIGI_JG92042 = 603;
	public static final int DIGI_ML26001 = 604;
	public static final int DIGI_JG93000 = 605;
	public static final int DIGI_JG93011 = 606;
	public static final int DIGI_JG93018 = 607;
	public static final int DIGI_JG93023 = 608;
	public static final int DIGI_ML26008 = 609;
	public static final int DIGI_ML26011 = 610;
	public static final int DIGI_JG94007 = 611;
	public static final int DIGI_JG94024 = 612;
	public static final int DIGI_JG94039 = 613;
	public static final int DIGI_JG95012 = 614;
	public static final int DIGI_ZILLASTOMP = 615;
	public static final int DIGI_ZC1 = 616;
	public static final int DIGI_ZC2 = 617;
	public static final int DIGI_ZC3 = 618;
	public static final int DIGI_ZC4 = 619;
	public static final int DIGI_ZC5 = 620;
	public static final int DIGI_ZC6 = 621;
	public static final int DIGI_ZC7 = 622;
	public static final int DIGI_ZC8 = 623;
	public static final int DIGI_ZC9 = 624;
	public static final int DIGI_Z16043 = 625;

	private static final int PRI_MAX = 100;
	// NOTE: HIGHER priority numbers have the highest precedence in the play list.
	public static final int PRI_PLAYERDEATH = 51;
	public static final int PRI_PLAYERVOICE = 50;
	private static final int PRI_HI_PLAYERWEAP = 49;
	private static final int PRI_LOW_PLAYERWEAP = 48;
	private static final int PRI_PLAYERAMBIENT = 40;
	private static final int PRI_NPCDEATH = 49;
	private static final int PRI_NPCWEAP = 47;
	private static final int PRI_NPCATTACK = 42;
	private static final int PRI_NPCAMBIENT = 39;
	private static final int PRI_ITEM = 41;
	private static final int PRI_SECTOROBJ = 30;
	private static final int PRI_ENVIRONMENT = 20;
	private static final int PRI_AMBIENT = 10;

	public static final int DIST_NORMAL = 0;
	private static final int DIST_MAXNORMAL = 16384; // This is max distance constant for normal sounds
	// This is the limiting constant in Sound_Dist function.
	private static final int DIST_WIDE = 65536; // Half Level at full volume before sound begins to fade.
	private static final int DIST_LEVELWIDE = 131072; // Full Level

	private static final int VF_NORMAL = 0;
	private static final int VF_LOOP = 1;

	public static VOC_INFO voc[] = {
			// NULL Entry used to detect a sound's presence in sprite attrib structs.
			new VOC_INFO("NULL.VOC", DIGI_NULL, 0, 0, 0, 0, 0, DIST_NORMAL, VF_NORMAL),

			// SWORD
			new VOC_INFO("SWRDSTR1.VOC", DIGI_SWORDSWOOSH, 1, PRI_HI_PLAYERWEAP, -200, 200, 0, DIST_NORMAL, VF_NORMAL),

			// SHURIKEN
			new VOC_INFO("THROW.VOC", DIGI_STAR, 2, PRI_HI_PLAYERWEAP, -100, 100, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("STRCLNK.VOC", DIGI_STARCLINK, 3, PRI_PLAYERAMBIENT, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("NULL.VOC", DIGI_NULL_STARWIZ, 4, PRI_LOW_PLAYERWEAP, 0, 0, 0, DIST_NORMAL, VF_LOOP),

			// UZI
			new VOC_INFO("UZIFIRE1.VOC", DIGI_UZIFIRE, 5, PRI_HI_PLAYERWEAP, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("RICH1.VOC", DIGI_RICHOCHET1, 6, PRI_LOW_PLAYERWEAP, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("RICH2.VOC", DIGI_RICHOCHET2, 7, PRI_LOW_PLAYERWEAP, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("RMVCLIP.VOC", DIGI_REMOVECLIP, 8, PRI_LOW_PLAYERWEAP, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("RPLCLIP.VOC", DIGI_REPLACECLIP, 9, PRI_LOW_PLAYERWEAP, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			// SPENT SHELL HITTING FLOOR
			new VOC_INFO("SHELL.VOC", DIGI_SHELL, 10, PRI_NPCATTACK, -200, 200, 0, DIST_NORMAL, VF_NORMAL),

			// CROSSRIOT
			new VOC_INFO("RIOTFIR1.VOC", DIGI_RIOTFIRE, 11, PRI_HI_PLAYERWEAP, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("SHOTGUN.VOC", DIGI_RIOTFIRE2, 12, PRI_HI_PLAYERWEAP, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("RIOTRLD.VOC", DIGI_RIOTRELOAD, 13, PRI_LOW_PLAYERWEAP, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("EXPMED.VOC", DIGI_BOLTEXPLODE, 14, PRI_HI_PLAYERWEAP, -100, 100, 0, DIST_MAXNORMAL,
					VF_NORMAL),
			new VOC_INFO("RIOTWIZ.VOC", DIGI_BOLTWIZ, 15, PRI_LOW_PLAYERWEAP, 0, 0, 0, DIST_NORMAL, VF_LOOP),

			// 30MM GRENADE LAUNCHER
			new VOC_INFO("40MMFIR2.VOC", DIGI_30MMFIRE, 16, PRI_HI_PLAYERWEAP, -100, 100, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("RIOTRLD.VOC", DIGI_30MMRELOAD, 17, PRI_LOW_PLAYERWEAP, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("40MMEXP2.VOC", DIGI_30MMEXPLODE, 18, PRI_HI_PLAYERWEAP, -100, 100, 0, DIST_WIDE, VF_NORMAL),
			new VOC_INFO("RIOTWIZ.VOC", DIGI_30MMWIZ, 19, PRI_LOW_PLAYERWEAP, 0, 0, 0, DIST_NORMAL, VF_LOOP),

			// GORO HEAD
			new VOC_INFO("GHFIR1.VOC", DIGI_HEADFIRE, 20, PRI_HI_PLAYERWEAP, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("GHWIZ.VOC", DIGI_HEADSHOTWIZ, 21, PRI_PLAYERAMBIENT, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("EXPSML.VOC", DIGI_HEADSHOTHIT, 22, PRI_LOW_PLAYERWEAP, -100, 100, 0, DIST_NORMAL, VF_NORMAL),

			// MINES
			new VOC_INFO("THROW.VOC", DIGI_MINETHROW, 23, PRI_HI_PLAYERWEAP, -100, 100, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("PHITGRND.VOC", DIGI_MINEBOUNCE, 24, PRI_LOW_PLAYERWEAP, -100, 100, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("EXPLRG.VOC", DIGI_MINEBLOW, 25, PRI_HI_PLAYERWEAP, 0, 0, 0, DIST_WIDE, VF_NORMAL),
			new VOC_INFO("STSCAN2.VOC", DIGI_MINEBEEP, 26, PRI_LOW_PLAYERWEAP, 0, 0, 0, DIST_NORMAL, VF_NORMAL),

			// HEART ATTACK
			new VOC_INFO("HBLOOP1.VOC", DIGI_HEARTBEAT, 27, PRI_LOW_PLAYERWEAP, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("HSQUEEZ1.VOC", DIGI_HEARTFIRE, 28, PRI_HI_PLAYERWEAP, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("HRTWIZ.VOC", DIGI_HEARTWIZ, 29, PRI_LOW_PLAYERWEAP, 0, 0, 0, DIST_NORMAL, VF_LOOP),

			// MISSILE BATTERY
			new VOC_INFO("RIOTFIR1.VOC", DIGI_MISSLFIRE, 30, PRI_HI_PLAYERWEAP, -75, 75, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("EXPMED.VOC", DIGI_MISSLEXP, 31, PRI_HI_PLAYERWEAP, -100, 100, 0, DIST_WIDE, VF_NORMAL),

			// RING OF FIRE SPELL
			new VOC_INFO("RFWIZ.VOC", DIGI_RFWIZ, 32, PRI_LOW_PLAYERWEAP, 0, 0, 0, DIST_NORMAL, VF_NORMAL),

			// NAPALM SPELL
			new VOC_INFO("NAPFIRE.VOC", DIGI_NAPFIRE, 33, PRI_HI_PLAYERWEAP, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("NAPTWIZ.VOC", DIGI_NAPWIZ, 34, PRI_LOW_PLAYERWEAP, 0, 0, 0, DIST_NORMAL, VF_LOOP),
			new VOC_INFO("NAPPUFF.VOC", DIGI_NAPPUFF, 35, PRI_LOW_PLAYERWEAP, 0, 0, 0, DIST_NORMAL, VF_NORMAL),

			// MAGIC MIRV SPELL
			new VOC_INFO("MMFIRE.VOC", DIGI_MIRVFIRE, 36, PRI_HI_PLAYERWEAP, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("MMWIZ.VOC", DIGI_MIRVWIZ, 37, PRI_LOW_PLAYERWEAP, 0, 0, 0, DIST_NORMAL, VF_LOOP),

			// SPIRAL SPELL
			new VOC_INFO("SPRLFIRE.VOC", DIGI_SPIRALFIRE, 38, PRI_HI_PLAYERWEAP, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("SPRLWIZ.VOC", DIGI_SPIRALWIZ, 39, PRI_LOW_PLAYERWEAP, 0, 0, 0, DIST_NORMAL, VF_LOOP),

			// MAGIC SOUNDS, GENERIC
			// (USED FOR MAGIC CARPET RIDES,ETC.),
			new VOC_INFO("MAGIC1.VOC", DIGI_MAGIC1, 40, PRI_AMBIENT, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("MAGIC2.VOC", DIGI_MAGIC2, 41, PRI_AMBIENT, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("MAGIC3.VOC", DIGI_MAGIC3, 42, PRI_AMBIENT, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("MAGIC4.VOC", DIGI_MAGIC4, 43, PRI_AMBIENT, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("MAGIC5.VOC", DIGI_MAGIC5, 44, PRI_AMBIENT, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("MAGIC6.VOC", DIGI_MAGIC6, 45, PRI_AMBIENT, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("MAGIC7.VOC", DIGI_MAGIC7, 46, PRI_AMBIENT, 0, 0, 0, DIST_NORMAL, VF_NORMAL),

			// SHADOW WARRIOR SPELL
			// #ifndef SW_SHAREWARE
			new VOC_INFO("SWSPELL.VOC", DIGI_SWCLOAKUNCLOAK, 47, PRI_LOW_PLAYERWEAP, -100, 100, 0, DIST_NORMAL,
					VF_NORMAL),
			// #else
			// new VOC_INFO("NULL.VOC", DIGI_NULL_SWCLOAK, 47, PRI_LOW_PLAYERWEAP,-100, 100,
			// 0, DIST_NORMAL, VF_NORMAL ),
			// #endif

			// PLAYER DEAD HEAD
			new VOC_INFO("DHVOMIT.VOC", DIGI_DHVOMIT, 48, PRI_PLAYERAMBIENT, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("DHCLUNK.VOC", DIGI_DHCLUNK, 49, PRI_PLAYERAMBIENT, -200, 200, 0, DIST_NORMAL, VF_NORMAL),
			// new VOC_INFO("DHSQSH.VOC", DIGI_DHSQUISH, 50, PRI_PLAYERAMBIENT, 0, 0, 0,
			// DIST_NORMAL, VF_NORMAL ),
			new VOC_INFO("NULL.VOC", DIGI_NULL_DHSQUISH, 50, PRI_PLAYERAMBIENT, 0, 0, 0, DIST_NORMAL, VF_NORMAL),

			// WEAPON RELATED
			new VOC_INFO("LAVAHIT.VOC", DIGI_PROJECTILELAVAHIT, 51, PRI_PLAYERAMBIENT, -100, 100, 0, DIST_NORMAL,
					VF_NORMAL),
			new VOC_INFO("STSPL01.VOC", DIGI_PROJECTILEWATERHIT, 52, PRI_PLAYERAMBIENT, -100, 100, 0, DIST_NORMAL,
					VF_NORMAL),

			// ITEMS
			new VOC_INFO("KEY.VOC", DIGI_KEY, 53, PRI_ITEM, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("ITEM5A.VOC", DIGI_ITEM, 54, PRI_ITEM, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("ITEMBIG2.VOC", DIGI_BIGITEM, 55, PRI_ITEM, -100, 100, 0, DIST_NORMAL, VF_NORMAL),

			// DEATH/HURT
			new VOC_INFO("BODY9.VOC", DIGI_BODYFALL1, 56, PRI_PLAYERDEATH, -200, 200, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("PHITGRND.VOC", DIGI_HITGROUND, 57, PRI_PLAYERAMBIENT, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("BODY2.VOC", DIGI_BODYSQUISH1, 58, PRI_PLAYERAMBIENT, -200, 200, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("BURN1.VOC", DIGI_BODYBURN, 59, PRI_PLAYERAMBIENT, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("BURNSCRM.VOC", DIGI_BODYBURNSCREAM, 60, PRI_PLAYERDEATH, -200, 200, 0, DIST_NORMAL,
					VF_NORMAL),
			new VOC_INFO("BODY3.VOC", DIGI_BODYCRUSHED1, 61, PRI_PLAYERAMBIENT, -200, 200, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("BODY4.VOC", DIGI_BODYHACKED1, 62, PRI_PLAYERAMBIENT, -200, 200, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("BURN2.VOC", DIGI_BODYSINGED, 63, PRI_PLAYERAMBIENT, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("DROWN1.VOC", DIGI_DROWN, 64, PRI_PLAYERDEATH, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("SCREAM1.VOC", DIGI_SCREAM1, 65, PRI_PLAYERDEATH, -200, 400, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("SCREAM2.VOC", DIGI_SCREAM2, 66, PRI_PLAYERDEATH, -200, 400, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("SCREAM3.VOC", DIGI_SCREAM3, 67, PRI_PLAYERDEATH, -200, 400, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("HIT1.VOC", DIGI_HIT1, 68, PRI_LOW_PLAYERWEAP, -200, 200, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("ELECTRC1.VOC", DIGI_ELECTRICUTE1, 69, PRI_PLAYERAMBIENT, -200, 200, 0, DIST_NORMAL,
					VF_NORMAL),
			new VOC_INFO("SWDIE02.VOC", DIGI_REMOVEME, 70, PRI_PLAYERDEATH, -200, 500, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("IMPALE1.VOC", DIGI_IMPALED, 71, PRI_PLAYERDEATH, -200, 200, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("OOF1.VOC", DIGI_OOF1, 72, PRI_PLAYERAMBIENT, -200, 200, 0, DIST_NORMAL, VF_NORMAL),

			// ACTOR SOUNDS THAT USER
			// PLAYER SOUNDS AT A LOWER PRIORITY
			new VOC_INFO("BODY1.VOC", DIGI_ACTORBODYFALL1, 73, PRI_NPCWEAP, 0, 0, DIGI_BODYFALL1, DIST_NORMAL,
					VF_NORMAL),
			new VOC_INFO("HITGRND.VOC", DIGI_ACTORHITGROUND, 74, PRI_NPCWEAP, 0, 0, DIGI_HITGROUND, DIST_NORMAL,
					VF_NORMAL),

			// NPC'S //////////////////////////////////////////////////////////////////////

			// COOLIE
			new VOC_INFO("COLEXP.VOC", DIGI_COOLIEEXPLODE, 75, PRI_NPCDEATH, -100, 100, 0, DIST_MAXNORMAL, VF_NORMAL),
			new VOC_INFO("COLSCRM.VOC", DIGI_COOLIESCREAM, 76, PRI_NPCATTACK, -100, 100, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("COLALRT.VOC", DIGI_COOLIEALERT, 77, PRI_NPCATTACK, -100, 100, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("COLAMB.VOC", DIGI_COOLIEAMBIENT, 78, PRI_NPCAMBIENT, -100, 100, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("COLPAIN.VOC", DIGI_COOLIEPAIN, 79, PRI_NPCATTACK, -100, 100, 0, DIST_NORMAL, VF_NORMAL),

			// COOLIE GHOST
			new VOC_INFO("CGMAT.VOC", DIGI_CGMATERIALIZE, 80, PRI_NPCAMBIENT, -100, 100, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("CGALRT.VOC", DIGI_CGALERT, 81, PRI_NPCATTACK, -100, 100, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("CGWHACK.VOC", DIGI_CGTHIGHBONE, 82, PRI_NPCWEAP, -100, 100, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("CGAMB.VOC", DIGI_CGAMBIENT, 83, PRI_NPCAMBIENT, -100, 100, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("CGPAIN.VOC", DIGI_CGPAIN, 84, PRI_NPCATTACK, -100, 100, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("CGSHOOT.VOC", DIGI_CGMAGIC, 85, PRI_NPCWEAP, -100, 100, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("CGHIT.VOC", DIGI_CGMAGICHIT, 86, PRI_NPCWEAP, -100, 100, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("CGSCRM.VOC", DIGI_CGSCREAM, 87, PRI_NPCDEATH, -100, 100, 0, DIST_NORMAL, VF_NORMAL),

			// NINJA
			new VOC_INFO("NINAMB.VOC", DIGI_NINJAAMBIENT, 88, PRI_NPCAMBIENT, -200, 200, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("NINSTAR.VOC", DIGI_NINJASTAR, 89, PRI_NPCWEAP, -100, 100, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("NINPAIN.VOC", DIGI_NINJAPAIN, 90, PRI_NPCWEAP, -200, 200, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("NINSCRM.VOC", DIGI_NINJASCREAM, 91, PRI_NPCDEATH, -200, 200, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("NINALRT.VOC", DIGI_NINJAALERT, 92, PRI_NPCATTACK, -200, 200, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("NINSHOOT.VOC", DIGI_NINJAUZIATTACK, 93, PRI_NPCWEAP, 0, 0, DIGI_UZIFIRE, DIST_NORMAL,
					VF_NORMAL),
			new VOC_INFO("RIOTFIR1.VOC", DIGI_NINJARIOTATTACK, 94, PRI_NPCWEAP, 0, 0, DIGI_RIOTFIRE, DIST_NORMAL,
					VF_NORMAL),

			// RIPPER
			new VOC_INFO("RIPAMB.VOC", DIGI_RIPPERAMBIENT, 95, PRI_NPCAMBIENT, -100, 100, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("RIPALRT.VOC", DIGI_RIPPERALERT, 96, PRI_NPCATTACK, -100, 100, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("RIPATCK.VOC", DIGI_RIPPERATTACK, 97, PRI_NPCATTACK, -100, 100, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("RIPPAIN.VOC", DIGI_RIPPERPAIN, 98, PRI_NPCATTACK, -100, 100, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("RIPSCRM.VOC", DIGI_RIPPERSCREAM, 99, PRI_NPCDEATH, -100, 100, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("RIPHRT.VOC", DIGI_RIPPERHEARTOUT, 100, PRI_NPCATTACK, -100, 100, 0, DIST_NORMAL, VF_NORMAL),

			// GUARDIAN
			new VOC_INFO("GRDAMB.VOC", DIGI_GRDAMBIENT, 101, PRI_NPCAMBIENT, -100, 100, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("GRDALRT.VOC", DIGI_GRDALERT, 102, PRI_NPCATTACK, -100, 100, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("GRDPAIN.VOC", DIGI_GRDPAIN, 103, PRI_NPCATTACK, -100, 100, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("GRDSCRM.VOC", DIGI_GRDSCREAM, 104, PRI_NPCDEATH, -100, 100, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("GRDFIR.VOC", DIGI_GRDFIREBALL, 105, PRI_NPCATTACK, -100, 100, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("GRDAXE.VOC", DIGI_GRDSWINGAXE, 106, PRI_NPCATTACK, -100, 100, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("GRDAXHT.VOC", DIGI_GRDAXEHIT, 107, PRI_NPCATTACK, -100, 100, 0, DIST_NORMAL, VF_NORMAL),

			// SKELETOR PRIEST
			new VOC_INFO("SPAMB.VOC", DIGI_SPAMBIENT, 108, PRI_NPCAMBIENT, -100, 100, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("SPALRT.VOC", DIGI_SPALERT, 109, PRI_NPCATTACK, -100, 100, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("SPPAIN.VOC", DIGI_SPPAIN, 110, PRI_NPCATTACK, -100, 100, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("SPSCRM.VOC", DIGI_SPSCREAM, 111, PRI_NPCDEATH, -100, 100, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("SPBLADE.VOC", DIGI_SPBLADE, 112, PRI_NPCATTACK, -100, 100, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("SPELEC.VOC", DIGI_SPELEC, 113, PRI_NPCATTACK, -100, 100, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("SPTLPRT.VOC", DIGI_SPTELEPORT, 114, PRI_NPCATTACK, -100, 100, 0, DIST_NORMAL, VF_NORMAL),

			// ACCURSED HEAD
			new VOC_INFO("AHAMB.VOC", DIGI_AHAMBIENT, 115, PRI_NPCAMBIENT, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("AHSCRM.VOC", DIGI_AHSCREAM, 116, PRI_NPCDEATH, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("AHEXP.VOC", DIGI_AHEXPLODE, 117, PRI_NPCDEATH, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("AHSHWSH.VOC", DIGI_AHSWOOSH, 118, PRI_NPCATTACK, 0, 0, 0, DIST_NORMAL, VF_LOOP),

			// HORNET
			new VOC_INFO("HBUZZ.VOC", DIGI_HORNETBUZZ, 119, PRI_NPCAMBIENT, -100, 100, 0, DIST_NORMAL, VF_LOOP),
			new VOC_INFO("HSTING.VOC", DIGI_HORNETSTING, 120, PRI_NPCATTACK, -100, 100, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("HPAIN.VOC", DIGI_HORNETPAIN, 121, PRI_NPCATTACK, -100, 100, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("HDEATH.VOC", DIGI_HORNETDEATH, 122, PRI_NPCDEATH, -100, 100, 0, DIST_NORMAL, VF_NORMAL),

			// SERPENT GOD BOSS
			new VOC_INFO("SGAMB.VOC", DIGI_SERPAMBIENT, 123, PRI_NPCAMBIENT, -100, 100, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("SGALRT.VOC", DIGI_SERPALERT, 124, PRI_NPCATTACK, -100, 100, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("SGPAIN.VOC", DIGI_SERPPAIN, 125, PRI_NPCATTACK, -100, 100, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("SGSCRM.VOC", DIGI_SERPSCREAM, 126, PRI_MAX, -100, 100, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("SGDETH.VOC", DIGI_SERPDEATHEXPLODE, 127, PRI_NPCDEATH, -100, 100, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("SGSWORD.VOC", DIGI_SERPSWORDATTACK, 128, PRI_NPCATTACK, -100, 100, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("SGMAGIC.VOC", DIGI_SERPMAGICLAUNCH, 129, PRI_NPCATTACK, -100, 100, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("SGHEADS.VOC", DIGI_SERPSUMMONHEADS, 130, PRI_NPCATTACK, -100, 100, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("SGTAUNT.VOC", DIGI_SERPTAUNTYOU, 131, PRI_NPCAMBIENT, -100, 100, 0, DIST_NORMAL, VF_NORMAL),

			// LAVA BOSS
			new VOC_INFO("LVAMB.VOC", DIGI_LAVABOSSAMBIENT, 132, PRI_NPCAMBIENT, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("LVSWIM.VOC", DIGI_LAVABOSSSWIM, 133, PRI_NPCAMBIENT, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("LVRISE.VOC", DIGI_LAVABOSSRISE, 134, PRI_NPCAMBIENT, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("LVALRT.VOC", DIGI_LAVABOSSALERT, 135, PRI_NPCATTACK, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("LVFLAME.VOC", DIGI_LAVABOSSFLAME, 136, PRI_NPCATTACK, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("LVMETEOR.VOC", DIGI_LAVABOSSMETEOR, 137, PRI_NPCATTACK, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("LVMETEXP.VOC", DIGI_LAVABOSSMETEXP, 138, PRI_NPCATTACK, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("LVPAIN.VOC", DIGI_LAVABOSSPAIN, 139, PRI_NPCATTACK, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("LVSIZZLE.VOC", DIGI_LAVABOSSSIZZLE, 140, PRI_NPCAMBIENT, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("LVEXPL.VOC", DIGI_LAVABOSSEXPLODE, 141, PRI_NPCDEATH, 0, 0, 0, DIST_NORMAL, VF_NORMAL),

			// LEVEL AND SECTOR OBJECT SOUNDS ///////////

			// MOTOR BOAT
			new VOC_INFO("BTSTRT.VOC", DIGI_BOATSTART, 142, PRI_SECTOROBJ, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("BTRUN01.VOC", DIGI_BOATRUN, 143, PRI_LOW_PLAYERWEAP, 0, 0, 0, DIST_NORMAL, VF_LOOP),
			new VOC_INFO("BTSTOP.VOC", DIGI_BOATSTOP, 144, PRI_SECTOROBJ, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("BTFIRE.VOC", DIGI_BOATFIRE, 145, PRI_HI_PLAYERWEAP, 0, 0, 0, DIST_NORMAL, VF_NORMAL),

			// ARMY TANK
			new VOC_INFO("TNKSTRT.VOC", DIGI_TANKSTART, 146, PRI_SECTOROBJ, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("TNKRUN.VOC", DIGI_TANKRUN, 147, PRI_LOW_PLAYERWEAP, 0, 0, 0, DIST_NORMAL, VF_LOOP),
			new VOC_INFO("TNKSTOP.VOC", DIGI_TANKSTOP, 148, PRI_SECTOROBJ, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("TNKIDLE.VOC", DIGI_TANKIDLE, 149, PRI_SECTOROBJ, 0, 0, 0, DIST_NORMAL, VF_LOOP),
			new VOC_INFO("TNKFIRE.VOC", DIGI_TANKFIRE, 150, PRI_HI_PLAYERWEAP, 0, 0, 0, DIST_NORMAL, VF_NORMAL),

			// ARMY TRUCK
			new VOC_INFO("TRUKRUN.VOC", DIGI_TRUKRUN, 151, PRI_SECTOROBJ, 0, 0, 0, DIST_NORMAL, VF_LOOP),
			new VOC_INFO("TRUKIDLE.VOC", DIGI_TRUKIDLE, 152, PRI_SECTOROBJ, 0, 0, 0, DIST_NORMAL, VF_LOOP),

			// SUBMARINE
			new VOC_INFO("SUBRUN.VOC", DIGI_SUBRUN, 153, PRI_SECTOROBJ, 0, 0, 0, DIST_NORMAL, VF_LOOP),
			new VOC_INFO("SUBIDLE.VOC", DIGI_SUBIDLE, 154, PRI_SECTOROBJ, 0, 0, 0, DIST_NORMAL, VF_LOOP),
			new VOC_INFO("SUBDOOR.VOC", DIGI_SUBDOOR, 155, PRI_SECTOROBJ, 0, 0, 0, DIST_NORMAL, VF_NORMAL),

			// WWII JAP BOMBER PLANE
			new VOC_INFO("BMBFLY.VOC", DIGI_BOMBRFLYING, 156, PRI_SECTOROBJ, 0, 0, 0, DIST_NORMAL, VF_LOOP),
			new VOC_INFO("BMBDROP.VOC", DIGI_BOMBRDROPBOMB, 157, PRI_SECTOROBJ, 0, 0, 0, DIST_NORMAL, VF_NORMAL),

			// BUBBLES
			new VOC_INFO("BUBBLE.VOC", DIGI_BUBBLES, 158, PRI_AMBIENT, -100, 100, 0, DIST_NORMAL, VF_NORMAL),

			// CHAIN MOVING
			// SOUND NOT AVAILABLE -- DELTED
			new VOC_INFO("CHAIN.VOC", DIGI_CHAIN, 159, PRI_ENVIRONMENT, 0, 0, 0, DIST_NORMAL, VF_NORMAL),

			// CHAIN DOOR
			new VOC_INFO("CHNDOOR.VOC", DIGI_CHAINDOOR, 160, PRI_ENVIRONMENT, 0, 0, 0, 8000, VF_NORMAL),

			// CRICKETS
			new VOC_INFO("CRCKT2.VOC", DIGI_CRICKETS, 161, PRI_AMBIENT, -100, 100, 0, DIST_NORMAL, VF_LOOP),

			// WOOD DOOR OPEN/CLOSE
			new VOC_INFO("DRWOODO.VOC", DIGI_WOODDOOROPEN, 162, PRI_ENVIRONMENT, -100, 100, 0, 8000, VF_NORMAL),
			new VOC_INFO("DRWOODC.VOC", DIGI_WOODDOORCLOSE, 163, PRI_ENVIRONMENT, -100, 100, 0, 8000, VF_NORMAL),

			// METAL DOOR OPEN/CLOSE
			new VOC_INFO("DRMETO.VOC", DIGI_METALDOOROPEN, 164, PRI_ENVIRONMENT, -100, 100, 0, 8000, VF_NORMAL),
			new VOC_INFO("DRMETC.VOC", DIGI_METALDOORCLOSE, 165, PRI_ENVIRONMENT, -100, 100, 0, 8000, VF_NORMAL),

			// SLIDING DOOR OPEN/CLOSE
			new VOC_INFO("DRSLDO.VOC", DIGI_SLIDEDOOROPEN, 166, PRI_ENVIRONMENT, -100, 100, 0, 8000, VF_NORMAL),
			new VOC_INFO("DRSLDC.VOC", DIGI_SLIDEDOORCLOSE, 167, PRI_ENVIRONMENT, -100, 100, 0, 8000, VF_NORMAL),

			// STONE SLIDING DOOR OPEN/CLOSE
			new VOC_INFO("DRSTNO.VOC", DIGI_STONEDOOROPEN, 168, PRI_ENVIRONMENT, -100, 100, 0, 8000, VF_NORMAL),
			new VOC_INFO("DRSTNC.VOC", DIGI_STONEDOORCLOSE, 169, PRI_ENVIRONMENT, -100, 100, 0, 8000, VF_NORMAL),

			// SQUEAKY DOOR OPEN/CLOSE
			new VOC_INFO("DRSQKO.VOC", DIGI_SQUEAKYDOOROPEN, 170, PRI_ENVIRONMENT, -100, 100, 0, 8000, VF_NORMAL),
			new VOC_INFO("DRSQKC.VOC", DIGI_SQUEAKYDOORCLOSE, 171, PRI_ENVIRONMENT, -100, 100, 0, 8000, VF_NORMAL),

			// GIANT DRILL MACHINE
			new VOC_INFO("DRILL.VOC", DIGI_DRILL, 172, PRI_SECTOROBJ, 0, 0, 0, DIST_NORMAL, VF_LOOP),

			// AMBIENT WATER DRIPPING IN CAVE
			new VOC_INFO("CAVE1.VOC", DIGI_CAVEDRIP1, 173, PRI_AMBIENT, -100, 100, 0, DIST_NORMAL, VF_LOOP),
			new VOC_INFO("CAVE2.VOC", DIGI_CAVEDRIP2, 174, PRI_AMBIENT, -100, 100, 0, DIST_NORMAL, VF_LOOP),

			new VOC_INFO("DRIP.VOC", DIGI_DRIP, 175, PRI_AMBIENT, -100, 100, 0, DIST_NORMAL, VF_NORMAL),

			// WATER FALL
			new VOC_INFO("WTRFAL1.VOC", DIGI_WATERFALL1, 176, PRI_AMBIENT, 0, 0, 0, DIST_NORMAL, VF_LOOP),
			// THERE IS NO WTRFAL2 -- DELETED!!!
			new VOC_INFO("WTRFAL2.VOC", DIGI_WATERFALL2, 177, PRI_AMBIENT, 0, 0, 0, DIST_NORMAL, VF_LOOP),

			// WATER FLOWING
			new VOC_INFO("WTRFLW1.VOC", DIGI_WATERFLOW1, 178, PRI_AMBIENT, -100, 100, 0, DIST_NORMAL, VF_LOOP),
			// THERE IS NO WTRFLW2 -- DELETED!!!
			new VOC_INFO("WTRFLW2.VOC", DIGI_WATERFLOW2, 179, PRI_AMBIENT, -100, 100, 0, DIST_NORMAL, VF_LOOP),

			// ELEVATOR START/STOP
			new VOC_INFO("ELEV1.VOC", DIGI_ELEVATOR, 180, PRI_ENVIRONMENT, 0, 0, 0, DIST_NORMAL, VF_NORMAL),

			// SMALL EXP
			new VOC_INFO("EXPSML.VOC", DIGI_SMALLEXP, 181, PRI_ENVIRONMENT, -200, 200, 0, DIST_NORMAL, VF_NORMAL),

			// MEDIUM EXP
			new VOC_INFO("EXPMED.VOC", DIGI_MEDIUMEXP, 182, PRI_ENVIRONMENT, -200, 200, 0, DIST_WIDE, VF_NORMAL),

			// LARGE EXP
			new VOC_INFO("EXPLRG.VOC", DIGI_LARGEEXP, 183, PRI_ENVIRONMENT, -200, 200, 0, DIST_WIDE, VF_NORMAL),

			// HUGE EXP
			// new VOC_INFO("BIGEXP.VOC", DIGI_HUGEEXP, 184, PRI_ENVIRONMENT, -200, 200, 0,
			// DIST_WIDE, VF_NORMAL ),
			new VOC_INFO("NULL.VOC", DIGI_NULL_HUGEEXP, 184, PRI_ENVIRONMENT, -200, 200, 0, DIST_WIDE, VF_NORMAL),

			// CRACKLING FIRE FOR CONTINUOUS BURN
			new VOC_INFO("FIRE1.VOC", DIGI_FIRE1, 185, PRI_AMBIENT, 0, 0, 0, DIST_NORMAL, VF_LOOP),

			// POWERFULL HIGH HEAT CONTINUOUS BURN
			new VOC_INFO("FIRE2.VOC", DIGI_FIRE2, 186, PRI_AMBIENT, 0, 0, 0, DIST_NORMAL, VF_LOOP),

			// SHOOTING FIREBALL FOR FIREBALL TRAP
			new VOC_INFO("FBALL1.VOC", DIGI_FIREBALL1, 187, PRI_SECTOROBJ, 0, 0, 0, DIST_NORMAL, VF_LOOP),

			// SHOOTING FIREBALL FOR FIREBALL TRAP
			new VOC_INFO("FIREBALL1.VOC", DIGI_FIREBALL2, 188, PRI_SECTOROBJ, 0, 0, 0, DIST_NORMAL, VF_LOOP),

			// SECTOR GEAR COG TURNING
			new VOC_INFO("GEAR1.VOC", DIGI_GEAR1, 189, PRI_ENVIRONMENT, 0, 0, 0, DIST_NORMAL, VF_LOOP),

			// AMBIENT GONG FOR USE IN TEMPLE/PALACE LEVELS
			new VOC_INFO("GONG.VOC", DIGI_GONG, 190, PRI_HI_PLAYERWEAP, -100, 100, 0, 32336, VF_NORMAL),

			// AMBIENT LAVA FLOW
			new VOC_INFO("LAVAFLW1.VOC", DIGI_LAVAFLOW1, 191, PRI_AMBIENT, -100, 100, 0, DIST_NORMAL, VF_LOOP),

			// GENERIC SECTOR OBJECT MACHINE RUNNING
			new VOC_INFO("MACHN1.VOC", DIGI_MACHINE1, 192, PRI_SECTOROBJ, 0, 0, 0, DIST_NORMAL, VF_LOOP),

			// AMBIENT MUD BUBBLES
			new VOC_INFO("MUD1.VOC", DIGI_MUBBUBBLES1, 193, PRI_AMBIENT, -100, 100, 0, DIST_NORMAL, VF_LOOP),

			// AMBIENT EARTH QUAKE
			new VOC_INFO("QUAKE1.VOC", DIGI_EARTHQUAKE, 194, PRI_AMBIENT, -100, 100, 0, DIST_NORMAL, VF_LOOP),

			// YUCKY SEWER FLOW
			new VOC_INFO("SEWER1.VOC", DIGI_SEWERFLOW1, 195, PRI_AMBIENT, 0, 0, 0, DIST_NORMAL, VF_LOOP),

			// WATER SPLASHING
			// (USE FOR PLAYER/NPC'S JUMPING AROUND IN WATER),
			new VOC_INFO("SPLASH1.VOC", DIGI_SPLASH1, 196, PRI_ENVIRONMENT, -100, 100, 0, DIST_NORMAL, VF_NORMAL),

			// STEAM FLOW
			new VOC_INFO("STEAM1.VOC", DIGI_STEAM1, 197, PRI_AMBIENT, -100, 100, 0, DIST_NORMAL, VF_LOOP),

			// VOLCANIC STEAM VENT
			new VOC_INFO("VOLSTM1.VOC", DIGI_VOLCANOSTEAM1, 198, PRI_AMBIENT, -100, 100, 0, DIST_NORMAL, VF_LOOP),

			// STOMPER THUD SECTOR OBJECT
			new VOC_INFO("STMPR.VOC", DIGI_STOMPER, 199, PRI_SECTOROBJ, -100, 100, 0, DIST_NORMAL, VF_NORMAL),

			// SCARY AMBIENT SWAMP SOUNDS
			new VOC_INFO("SWAMP1.VOC", DIGI_SWAMP, 200, PRI_AMBIENT, -100, 100, 0, DIST_NORMAL, VF_LOOP),

			// FLIP SWITCH
			new VOC_INFO("SWITCH1.VOC", DIGI_REGULARSWITCH, 201, PRI_ENVIRONMENT, -100, 100, 0, DIST_NORMAL, VF_NORMAL),

			// FLIP LARGE SWITCH
			new VOC_INFO("SWITCH2.VOC", DIGI_BIGSWITCH, 202, PRI_ENVIRONMENT, -100, 100, 0, DIST_NORMAL, VF_NORMAL),

			// STONE SWITCH
			new VOC_INFO("SWITCH3.VOC", DIGI_STONESWITCH, 203, PRI_ENVIRONMENT, -100, 100, 0, DIST_NORMAL, VF_NORMAL),

			// BREAKABLE GLASS SWITCH
			new VOC_INFO("SWITCH4.VOC", DIGI_GLASSSWITCH, 204, PRI_ENVIRONMENT, -100, 100, 0, DIST_NORMAL, VF_NORMAL),

			// HUGE ECHOING SWITCH
			new VOC_INFO("SWITCH5.VOC", DIGI_HUGESWITCH, 205, PRI_ENVIRONMENT, -100, 100, 0, DIST_NORMAL, VF_NORMAL),

			// AMBIENT ROLLING THUNDER
			new VOC_INFO("THUNDR.VOC", DIGI_THUNDER, 206, PRI_AMBIENT, -200, 200, 0, DIST_LEVELWIDE, VF_NORMAL),

			// TELEPORTER
			new VOC_INFO("TELPORT.VOC", DIGI_TELEPORT, 207, PRI_ENVIRONMENT, 0, 0, 0, DIST_NORMAL, VF_NORMAL),

			// UNDERWATER AMBIENCE
			new VOC_INFO("UNDRWTR.VOC", DIGI_UNDERWATER, 208, PRI_AMBIENT, 0, 0, 0, DIST_NORMAL, VF_LOOP),

			// UNLOCK BIG LOCKED DOOR
			new VOC_INFO("UNLOCK.VOC", DIGI_UNLOCK, 209, PRI_ENVIRONMENT, 0, 0, 0, DIST_NORMAL, VF_NORMAL),

			// SQUEAKY VALVE TURNING
			new VOC_INFO("VALVE.VOC", DIGI_SQUEAKYVALVE, 210, PRI_ENVIRONMENT, 0, 0, 0, DIST_NORMAL, VF_NORMAL),

			// SPOOKY ETHERAL VOID AMBIENCE
			// (NETHERWORLDLY SOUNDS),
			new VOC_INFO("VOID1.VOC", DIGI_VOID1, 211, PRI_AMBIENT, 0, 0, 0, DIST_NORMAL, VF_LOOP),
			new VOC_INFO("VOID2.VOC", DIGI_VOID2, 212, PRI_AMBIENT, 0, 0, 0, DIST_NORMAL, VF_LOOP),
			new VOC_INFO("VOID3.VOC", DIGI_VOID3, 213, PRI_NPCWEAP, 0, 0, 0, -8000, VF_NORMAL),
			new VOC_INFO("VOID4.VOC", DIGI_VOID4, 214, PRI_AMBIENT, 0, 0, 0, DIST_NORMAL, VF_LOOP),
			new VOC_INFO("VOID5.VOC", DIGI_VOID5, 215, PRI_AMBIENT, 0, 0, 0, DIST_NORMAL, VF_LOOP),

			// VOLCANIC ERUPTION
			new VOC_INFO("ERUPT.VOC", DIGI_ERUPTION, 216, PRI_AMBIENT, 0, 0, 0, DIST_MAXNORMAL, VF_LOOP),

			// VOLCANIC SIZZLING PROJECTILES FLYING THROUGH AIR
			new VOC_INFO("VOLPRJCT.VOC", DIGI_VOLCANOPROJECTILE, 217, PRI_AMBIENT, 0, 0, 0, DIST_NORMAL, VF_LOOP),

			// LIGHT WIND AMBIENCE
			new VOC_INFO("WIND1.VOC", DIGI_LIGHTWIND, 218, PRI_AMBIENT, 0, 0, 0, DIST_NORMAL, VF_LOOP),

			// STRONG BLOWING WIND AMBIENCE
			new VOC_INFO("WIND2.VOC", DIGI_STRONGWIND, 219, PRI_AMBIENT, 0, 0, 0, DIST_NORMAL, VF_NORMAL),

			// BREAKING WOOD AMBIENCE
			new VOC_INFO("WOODBRK.VOC", DIGI_BREAKINGWOOD, 220, PRI_AMBIENT, 0, 0, 0, DIST_NORMAL, VF_NORMAL),

			// BREAKING, TUMBLING STONES FALLING AMBIENCE
			new VOC_INFO("STONEBRK.VOC", DIGI_BREAKSTONES, 221, PRI_AMBIENT, 0, 0, 0, DIST_NORMAL, VF_NORMAL),

			// ENGINE ROOM SOUND
			new VOC_INFO("ENGROOM1.VOC", DIGI_ENGROOM1, 222, PRI_AMBIENT, 0, 0, 0, DIST_NORMAL, VF_LOOP),
			new VOC_INFO("ENGROOM2.VOC", DIGI_ENGROOM2, 223, PRI_AMBIENT, 0, 0, 0, DIST_NORMAL, VF_LOOP),
			new VOC_INFO("ENGROOM3.VOC", DIGI_ENGROOM3, 224, PRI_AMBIENT, 0, 0, 0, DIST_NORMAL, VF_LOOP),
			new VOC_INFO("ENGROOM4.VOC", DIGI_ENGROOM4, 225, PRI_AMBIENT, 0, 0, 0, DIST_NORMAL, VF_LOOP),
			new VOC_INFO("ENGROOM5.VOC", DIGI_ENGROOM5, 226, PRI_AMBIENT, 0, 0, 0, DIST_NORMAL, VF_LOOP),

			// BREAKING GLASS, LARGE WINDOW PANE
			new VOC_INFO("GLASS3.VOC", DIGI_BREAKGLASS, 227, PRI_NPCDEATH, 0, 0, 0, DIST_NORMAL, VF_NORMAL),

			// MUSICAL STINGER
			new VOC_INFO("MUSSTING.VOC", DIGI_MUSSTING, 228, PRI_AMBIENT, 0, 0, 0, DIST_NORMAL, VF_NORMAL),

			// HELICOPTER LIKE SOUND
			new VOC_INFO("HELI.VOC", DIGI_HELI, 229, PRI_SECTOROBJ, 0, 0, 0, DIST_NORMAL, VF_LOOP),

			// HUGE ECHOING HEART LIKE AMBIENCE
			new VOC_INFO("BIGHART.VOC", DIGI_BIGHART, 230, PRI_AMBIENT, 0, 0, 0, DIST_NORMAL, VF_LOOP),

			// EERIE ETHERAL TYPE WIND
			new VOC_INFO("WIND4.VOC", DIGI_WIND4, 231, PRI_AMBIENT, 0, 0, 0, DIST_NORMAL, VF_LOOP),

			// SPOOKY SINE WAVE SOUND
			new VOC_INFO("SPOOKY1.VOC", DIGI_SPOOKY1, 232, PRI_AMBIENT, 0, 0, 0, DIST_NORMAL, VF_LOOP),

			// SPOOKY SINE WAVE SOUND
			new VOC_INFO("DRILL1.VOC", DIGI_DRILL1, 233, PRI_SECTOROBJ, 0, 0, 0, DIST_NORMAL, VF_LOOP),

			// JET ENGINE
			new VOC_INFO("JET.VOC", DIGI_JET, 234, PRI_AMBIENT, 0, 0, 0, DIST_MAXNORMAL, VF_LOOP),

			// CERIMONIAL DRUM CHANT
			new VOC_INFO("DRUMCHNT.VOC", DIGI_DRUMCHANT, 235, PRI_AMBIENT, 0, 0, 0, DIST_NORMAL, VF_LOOP),

			new VOC_INFO("FLY.VOC", DIGI_BUZZZ, 236, PRI_MAX, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("UZICLK.VOC", DIGI_CHOP_CLICK, 237, PRI_MAX, 0, 0, 0, DIST_NORMAL, VF_NORMAL),

			// !IMPORTANT! Make sure all player voices stay together
			new VOC_INFO("STICKY2R.VOC", DIGI_SWORD_UP, 238, PRI_LOW_PLAYERWEAP, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("UZI1R.VOC", DIGI_UZI_UP, 239, PRI_LOW_PLAYERWEAP, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("SHOTG1R.VOC", DIGI_SHOTGUN_UP, 240, PRI_LOW_PLAYERWEAP, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("BOLT1R.VOC", DIGI_ROCKET_UP, 241, PRI_LOW_PLAYERWEAP, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("BOLT1R.VOC", DIGI_GRENADE_UP, 242, PRI_LOW_PLAYERWEAP, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("BOLT1R.VOC", DIGI_RAIL_UP, 243, PRI_LOW_PLAYERWEAP, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("STICKY1R.VOC", DIGI_MINE_UP, 244, PRI_LOW_PLAYERWEAP, 0, 0, 0, DIST_NORMAL, VF_NORMAL),

			new VOC_INFO("", DIGI_FIRSTPLAYERVOICE, 245, 0, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("BADMAN04.VOC", DIGI_TAUNTAI1, 246, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("COMEGET2.VOC", DIGI_TAUNTAI2, 247, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("GHOP07.VOC", DIGI_TAUNTAI3, 248, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("GOODDAY4.VOC", DIGI_TAUNTAI4, 249, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("KILLU05.VOC", DIGI_TAUNTAI5, 250, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("NATURAL4.VOC", DIGI_TAUNTAI6, 251, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("NOHONOR6.VOC", DIGI_TAUNTAI7, 252, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("SAYON09.VOC", DIGI_TAUNTAI8, 253, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("TAKSAN1.VOC", DIGI_TAUNTAI9, 254, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("SNATCH01.VOC", DIGI_TAUNTAI10, 255, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),

			new VOC_INFO("CHOTO7.VOC", DIGI_PLAYERPAIN1, 256, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("SWPAIN05.VOC", DIGI_PLAYERPAIN2, 257, PRI_PLAYERVOICE, -200, 200, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("SWPAIN03.VOC", DIGI_PLAYERPAIN3, 258, PRI_PLAYERVOICE, -200, 200, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("SWPAIN07.VOC", DIGI_PLAYERPAIN4, 259, PRI_PLAYERVOICE, -200, 200, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("SWPAIN22.VOC", DIGI_PLAYERPAIN5, 260, PRI_PLAYERVOICE, -200, 200, 0, DIST_NORMAL, VF_NORMAL),

			new VOC_INFO("SWYELL03.VOC", DIGI_PLAYERYELL1, 261, PRI_PLAYERVOICE, -200, 200, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("SWYELL05.VOC", DIGI_PLAYERYELL2, 262, PRI_PLAYERVOICE, -200, 200, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("SWYELL06.VOC", DIGI_PLAYERYELL3, 263, PRI_PLAYERVOICE, -200, 200, 0, DIST_NORMAL, VF_NORMAL),

			new VOC_INFO("GRUNT06.VOC", DIGI_SEARCHWALL, 264, PRI_PLAYERVOICE, -200, 200, 0, DIST_NORMAL, VF_NORMAL),

			new VOC_INFO("NOWAY1.VOC", DIGI_NOURINAL, 265, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),

			new VOC_INFO("SWDIE02.VOC", DIGI_FALLSCREAM, 266, PRI_PLAYERDEATH, -100, 100, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("AHSO5.VOC", DIGI_GOTITEM1, 267, PRI_PLAYERDEATH, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("", DIGI_LASTPLAYERVOICE, 268, 0, 0, 0, 0, DIST_NORMAL, VF_NORMAL),

			// #ifndef SW_SHAREWARE
			// was RAILB10.VOC
			new VOC_INFO("HSHOT1.VOC", DIGI_RAILFIRE, 269, PRI_HI_PLAYERWEAP, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			// #else
			// new VOC_INFO("NULL.VOC", DIGI_NULL_RAILFIRE, 269, PRI_HI_PLAYERWEAP, 0, 0, 0,
			// DIST_NORMAL, VF_NORMAL ),
			// #endif
			new VOC_INFO("RAIL2.VOC", DIGI_RAILREADY, 270, PRI_HI_PLAYERWEAP, 0, 0, 0, DIST_NORMAL, VF_LOOP),
			new VOC_INFO("RAILUP09.VOC", DIGI_RAILPWRUP, 271, PRI_HI_PLAYERWEAP, 0, 0, 0, DIST_NORMAL, VF_NORMAL),

			new VOC_INFO("HBOMB2.VOC", DIGI_NUCLEAREXP, 272, PRI_MAX, 0, 0, 0, DIST_LEVELWIDE, VF_NORMAL),
			new VOC_INFO("STANDBY.VOC", DIGI_NUKESTDBY, 273, PRI_HI_PLAYERWEAP, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("CDOWN.VOC", DIGI_NUKECDOWN, 274, PRI_HI_PLAYERWEAP, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("SYSREAD.VOC", DIGI_NUKEREADY, 275, PRI_HI_PLAYERWEAP, 0, 0, 0, DIST_NORMAL, VF_NORMAL),

			new VOC_INFO("HISS1.VOC", DIGI_CHEMGAS, 276, PRI_LOW_PLAYERWEAP, 0, 0, 0, DIST_NORMAL, VF_LOOP),
			new VOC_INFO("CHBNCE1.VOC", DIGI_CHEMBOUNCE, 277, PRI_LOW_PLAYERWEAP, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("THROW.VOC", DIGI_THROW, 278, PRI_LOW_PLAYERWEAP, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("PULL.VOC", DIGI_PULL, 279, PRI_LOW_PLAYERWEAP, 0, 0, 0, DIST_NORMAL, VF_NORMAL),

			new VOC_INFO("STSCAN2.VOC", DIGI_MINEARM, 280, PRI_LOW_PLAYERWEAP, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("HBDOWN1.VOC", DIGI_HEARTDOWN, 281, PRI_LOW_PLAYERWEAP, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			// new VOC_INFO("TOOLUSE1.VOC", DIGI_TOOLBOX, 282, PRI_LOW_PLAYERWEAP, 0, 0, 0,
			// DIST_NORMAL, VF_NORMAL ),
			new VOC_INFO("NULL.VOC", DIGI_NULL_TOOLBOX, 282, PRI_LOW_PLAYERWEAP, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("GASPOP.VOC", DIGI_GASPOP, 283, PRI_LOW_PLAYERWEAP, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("40MMBNCE.VOC", DIGI_40MMBNCE, 284, PRI_LOW_PLAYERWEAP, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("BURGALRM.VOC", DIGI_BURGLARALARM, 285, PRI_LOW_PLAYERWEAP, 0, 0, 0, DIST_NORMAL, VF_LOOP),
			new VOC_INFO("CARALRM2.VOC", DIGI_CARALARM, 286, PRI_LOW_PLAYERWEAP, 0, 0, 0, 25000, VF_NORMAL),
			new VOC_INFO("CAOFF1.VOC", DIGI_CARALARMOFF, 287, PRI_LOW_PLAYERWEAP, 0, 0, 0, 25000, VF_NORMAL),
			new VOC_INFO("TACK1.VOC", DIGI_CALTROPS, 288, PRI_LOW_PLAYERWEAP, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("NVON3.VOC", DIGI_NIGHTON, 289, PRI_LOW_PLAYERWEAP, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("NVOFF2.VOC", DIGI_NIGHTOFF, 290, PRI_LOW_PLAYERWEAP, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("SGSH01.VOC", DIGI_SHOTSHELLSPENT, 291, PRI_LOW_PLAYERWEAP, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("SKID3.VOC", DIGI_BUSSKID, 292, PRI_LOW_PLAYERWEAP, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("CRASH4.VOC", DIGI_BUSCRASH, 293, PRI_LOW_PLAYERWEAP, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("BUS1.VOC", DIGI_BUSENGINE, 294, PRI_LOW_PLAYERWEAP, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("BIMP01.VOC", DIGI_ARMORHIT, 295, PRI_LOW_PLAYERWEAP, 0, 0, 0, DIST_NORMAL, VF_NORMAL),

			new VOC_INFO("ASIREN1.VOC", DIGI_ASIREN1, 296, PRI_AMBIENT, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("FIRETRK1.VOC", DIGI_FIRETRK1, 297, PRI_AMBIENT, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("TRAFFIC1.VOC", DIGI_TRAFFIC1, 298, PRI_AMBIENT, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("TRAFFIC2.VOC", DIGI_TRAFFIC2, 299, PRI_AMBIENT, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("TRAFFIC3.VOC", DIGI_TRAFFIC3, 300, PRI_AMBIENT, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("TRAFFIC4.VOC", DIGI_TRAFFIC4, 301, PRI_AMBIENT, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("TRAFFIC5.VOC", DIGI_TRAFFIC5, 302, PRI_AMBIENT, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("TRAFFIC6.VOC", DIGI_TRAFFIC6, 303, PRI_AMBIENT, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("HELI1.VOC", DIGI_HELI1, 304, PRI_AMBIENT, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("JET1.VOC", DIGI_JET1, 305, PRI_AMBIENT, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("MOTO1.VOC", DIGI_MOTO1, 306, PRI_AMBIENT, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("MOTO2.VOC", DIGI_MOTO2, 307, PRI_AMBIENT, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("NEON1.VOC", DIGI_NEON1, 308, PRI_AMBIENT, 0, 0, 0, -8000, VF_NORMAL),
			new VOC_INFO("SUBWAY1.VOC", DIGI_SUBWAY, 309, PRI_AMBIENT, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("TRAINS01.VOC", DIGI_TRAIN1, 310, PRI_PLAYERDEATH, 0, 0, 0, DIST_NORMAL, VF_NORMAL),

			new VOC_INFO("COIN.VOC", DIGI_COINS, 311, PRI_LOW_PLAYERWEAP, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("SWRDSMK1.VOC", DIGI_SWORDCLANK, 312, PRI_HI_PLAYERWEAP, -200, 200, 0, DIST_NORMAL, VF_NORMAL),
			// RIPPER2
			new VOC_INFO("RIP2AMB.VOC", DIGI_RIPPER2AMBIENT, 313, PRI_NPCAMBIENT, -100, 100, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("RIP2ALRT.VOC", DIGI_RIPPER2ALERT, 314, PRI_NPCATTACK, -100, 100, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("RIP2ATCK.VOC", DIGI_RIPPER2ATTACK, 315, PRI_NPCATTACK, -100, 100, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("RIP2PAIN.VOC", DIGI_RIPPER2PAIN, 316, PRI_NPCATTACK, -100, 100, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("RIP2SCRM.VOC", DIGI_RIPPER2SCREAM, 317, PRI_NPCDEATH, -100, 100, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("RIP2HRT.VOC", DIGI_RIPPER2HEARTOUT, 318, PRI_NPCATTACK, -100, 100, 0, DIST_NORMAL, VF_NORMAL),

			new VOC_INFO("M60.VOC", DIGI_M60, 319, PRI_NPCATTACK, -100, 100, 0, DIST_NORMAL, VF_NORMAL),

			// SUMO
			new VOC_INFO("SUMSCRM.VOC", DIGI_SUMOSCREAM, 320, PRI_MAX, -100, 100, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("SUMALRT.VOC", DIGI_SUMOALERT, 321, PRI_NPCATTACK, -100, 100, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("SUMAMB.VOC", DIGI_SUMOAMBIENT, 322, PRI_NPCAMBIENT, -100, 100, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("SUMPAIN.VOC", DIGI_SUMOPAIN, 323, PRI_NPCATTACK, -100, 100, 0, DIST_NORMAL, VF_NORMAL),

			// UNLOCK RAM LOCKED DOOR
			new VOC_INFO("RAMLOCK.VOC", DIGI_RAMUNLOCK, 324, PRI_ENVIRONMENT, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			// UNLOCK CARD LOCKED DOOR
			new VOC_INFO("CARDLOCK.VOC", DIGI_CARDUNLOCK, 325, PRI_ENVIRONMENT, 0, 0, 0, DIST_NORMAL, VF_NORMAL),

			// More player voices
			new VOC_INFO("ACS10.VOC", DIGI_ANCIENTSECRET, 326, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("AMDRIV01.VOC", DIGI_AMERICANDRIVER, 327, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("BABOON03.VOC", DIGI_DRIVELIKEBABOON, 328, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("BBURN04.VOC", DIGI_BURNBABY, 329, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("BIGWPN01.VOC", DIGI_LIKEBIGWEAPONS, 330, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("CBUNG01.VOC", DIGI_COWABUNGA, 331, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("CHARAD09.VOC", DIGI_NOCHARADE, 332, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("DTIME.VOC", DIGI_TIMETODIE, 333, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("EAT02.VOC", DIGI_EATTHIS, 334, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("FCRACK01.VOC", DIGI_FIRECRACKERUPASS, 335, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("HCOW03.VOC", DIGI_HOLYCOW, 336, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("HCOW06.VOC", DIGI_HOLYPEICESOFCOW, 337, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("HSHIT03.VOC", DIGI_HOLYSHIT, 338, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("HSHIT04.VOC", DIGI_HOLYPEICESOFSHIT, 339, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("IHOPE01.VOC", DIGI_PAYINGATTENTION, 340, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("ILIKE01.VOC", DIGI_EVERYBODYDEAD, 341, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("KUNGFU06.VOC", DIGI_KUNGFU, 342, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("LMOVE01.VOC", DIGI_HOWYOULIKEMOVE, 343, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("LWANG05.VOC", DIGI_NOMESSWITHWANG, 344, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("RAW01.VOC", DIGI_RAWREVENGE, 345, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("STUPID01.VOC", DIGI_YOULOOKSTUPID, 346, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("TDICK02.VOC", DIGI_TINYDICK, 347, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("TOURN01.VOC", DIGI_NOTOURNAMENT, 348, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("WWANG11.VOC", DIGI_WHOWANTSWANG, 349, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("YAK02.VOC", DIGI_MOVELIKEYAK, 350, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("REFLEX08.VOC", DIGI_ALLINREFLEXES, 351, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("EVADE01.VOC", DIGI_EVADEFOREVER, 352, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("MFLY03.VOC", DIGI_MRFLY, 353, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("SHISEI03.VOC", DIGI_SHISEISI, 354, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("FWORKS01.VOC", DIGI_LIKEFIREWORKS, 355, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("HIRO03.VOC", DIGI_LIKEHIROSHIMA, 356, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("NAGA06.VOC", DIGI_LIKENAGASAKI, 357, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("PEARL03.VOC", DIGI_LIKEPEARL, 358, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("IAM01.VOC", DIGI_IAMSHADOW, 359, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("LIKNUK01.VOC", DIGI_ILIKENUKES, 360, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("LIKSRD01.VOC", DIGI_ILIKESWORD, 361, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("LIKSHK02.VOC", DIGI_ILIKESHURIKEN, 362, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("LUCK06.VOC", DIGI_BADLUCK, 363, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("MCHAN01.VOC", DIGI_NOMOVIEMRCHAN, 364, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("RCHAN13.VOC", DIGI_REALLIFEMRCHAN, 365, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("MUSIC03.VOC", DIGI_NOLIKEMUSIC, 366, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("NODIFF07.VOC", DIGI_NODIFFERENCE, 367, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("NOFEAR01.VOC", DIGI_NOFEAR, 368, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("NOPAIN.VOC", DIGI_NOPAIN, 369, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("REPMAN15.VOC", DIGI_NOREPAIRMAN, 370, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("SOB15.VOC", DIGI_SONOFABITCH, 371, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("WEAK03.VOC", DIGI_PAINFORWEAK, 372, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("SPEED04.VOC", DIGI_GOSPEEDY, 373, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("STIFF01.VOC", DIGI_GETTINGSTIFF, 374, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("TOMB05.VOC", DIGI_TOMBRAIDER, 375, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("TSTICK01.VOC", DIGI_STICKYGOTU1, 376, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("TSTICK05.VOC", DIGI_STICKYGOTU2, 377, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("TSTICK07.VOC", DIGI_STICKYGOTU3, 378, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("TSTICK10.VOC", DIGI_STICKYGOTU4, 379, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("TSWORD05.VOC", DIGI_SWORDGOTU1, 380, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("TSWORD08.VOC", DIGI_SWORDGOTU2, 381, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("TSWORD01.VOC", DIGI_SWORDGOTU3, 382, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("SWYELL22.VOC", DIGI_HURTBAD1, 383, PRI_PLAYERVOICE, -200, 200, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("SWYELL14.VOC", DIGI_HURTBAD2, 384, PRI_PLAYERVOICE, -200, 200, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("SWYELL23.VOC", DIGI_HURTBAD3, 385, PRI_PLAYERVOICE, -200, 200, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("SWYELL16.VOC", DIGI_HURTBAD4, 386, PRI_PLAYERVOICE, -200, 200, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("SWYELL13.VOC", DIGI_HURTBAD5, 387, PRI_PLAYERVOICE, -200, 200, 0, DIST_NORMAL, VF_NORMAL),

			// TOILETGIRL
			new VOC_INFO("TGSCRM.VOC", DIGI_TOILETGIRLSCREAM, 388, PRI_NPCATTACK, -100, 100, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("TGALRT.VOC", DIGI_TOILETGIRLALERT, 389, PRI_NPCATTACK, -300, 300, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("TGAMB.VOC", DIGI_TOILETGIRLAMBIENT, 390, PRI_NPCAMBIENT, -300, 300, 0, DIST_NORMAL,
					VF_NORMAL),
			new VOC_INFO("TGPAIN.VOC", DIGI_TOILETGIRLPAIN, 391, PRI_NPCATTACK, -100, 100, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("TGTNT1.VOC", DIGI_TOILETGIRLTAUNT1, 392, PRI_NPCATTACK, -100, 100, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("TGTNT2.VOC", DIGI_TOILETGIRLTAUNT2, 393, PRI_NPCATTACK, -100, 100, 0, DIST_NORMAL, VF_NORMAL),
			// MORE SUMO
			new VOC_INFO("SUMOFART.VOC", DIGI_SUMOFART, 394, PRI_PLAYERVOICE, -100, 100, 0, DIST_NORMAL, VF_NORMAL),

			new VOC_INFO("GIBS04.VOC", DIGI_GIBS1, 395, PRI_LOW_PLAYERWEAP, -200, 200, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("GIBS05.VOC", DIGI_GIBS2, 396, PRI_LOW_PLAYERWEAP, -200, 200, 0, DIST_NORMAL, VF_NORMAL),

			new VOC_INFO("BIRDS01.VOC", DIGI_BIRDS1, 397, PRI_AMBIENT, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("BIRDS02.VOC", DIGI_BIRDS2, 398, PRI_AMBIENT, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("TOILET01.VOC", DIGI_TOILET, 399, PRI_AMBIENT, -200, 200, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("FLIDLE.VOC", DIGI_FORKLIFTIDLE, 400, PRI_SECTOROBJ, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("FLRUN01.VOC", DIGI_FORKLIFTRUN, 401, PRI_SECTOROBJ, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("TOYCAR03.VOC", DIGI_TOYCAR, 402, PRI_LOW_PLAYERWEAP, 0, 0, 0, DIST_NORMAL, VF_LOOP),
			new VOC_INFO("PRESS03.VOC", DIGI_UZIMATIC, 403, PRI_AMBIENT, -200, 200, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("COMPON01.VOC", DIGI_COMPUTERPOWER, 404, PRI_LOW_PLAYERWEAP, -200, 200, 0, DIST_NORMAL,
					VF_NORMAL),
			new VOC_INFO("TURBON01.VOC", DIGI_GENERATORON, 405, PRI_LOW_PLAYERWEAP, -200, 200, 0, DIST_NORMAL,
					VF_NORMAL),
			new VOC_INFO("TURBRN01.VOC", DIGI_GENERATORRUN, 406, PRI_HI_PLAYERWEAP, 0, 0, 0, DIST_NORMAL, VF_LOOP),
			new VOC_INFO("BIGDRL03.VOC", DIGI_BIGDRILL, 407, PRI_SECTOROBJ, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("FLUOR01.VOC", DIGI_FLUORLIGHT, 408, PRI_AMBIENT, -200, 200, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("AMOEBA03.VOC", DIGI_AMOEBA, 409, PRI_SECTOROBJ, -200, 200, 0, DIST_NORMAL, VF_LOOP),
			new VOC_INFO("BODY6.VOC", DIGI_BODYFALL2, 410, PRI_PLAYERDEATH, -200, 200, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("GIBS01.VOC", DIGI_GIBS3, 411, PRI_LOW_PLAYERWEAP, -200, 200, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("CHOK01.VOC", DIGI_NINJACHOKE, 412, PRI_NPCDEATH, -200, 200, 0, DIST_NORMAL, VF_NORMAL),

			new VOC_INFO("TRAIN3.VOC", DIGI_TRAIN3, 413, PRI_AMBIENT, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("TRAINR02.VOC", DIGI_TRAINR02, 414, PRI_PLAYERDEATH, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("TRAIN8.VOC", DIGI_TRAIN8, 415, PRI_AMBIENT, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("TCLID01.VOC", DIGI_TRASHLID, 416, PRI_AMBIENT, 0, 0, 0, DIST_NORMAL, VF_NORMAL),

			// player voices
			new VOC_INFO("ACCU01.VOC", DIGI_GETMEDKIT, 417, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("AHH03.VOC", DIGI_AHH, 418, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),

			// Pachinko
			new VOC_INFO("PALARM1.VOC", DIGI_PALARM, 419, PRI_SECTOROBJ, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("PFLIP4.VOC", DIGI_PFLIP, 420, PRI_SECTOROBJ, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("PROLL1.VOC", DIGI_PROLL1, 421, PRI_SECTOROBJ, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("PROLL2.VOC", DIGI_PROLL2, 422, PRI_SECTOROBJ, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("PROLL3.VOC", DIGI_PROLL3, 423, PRI_SECTOROBJ, 0, 0, 0, DIST_NORMAL, VF_NORMAL),

			// BUNNY
			new VOC_INFO("RABATK1.VOC", DIGI_BUNNYATTACK, 424, PRI_NPCATTACK, -100, 100, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("RABDIE1.VOC", DIGI_BUNNYDIE1, 425, PRI_NPCDEATH, -100, 100, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("RABDIE2.VOC", DIGI_BUNNYDIE2, 426, PRI_NPCDEATH, -100, 100, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("RABDIE3.VOC", DIGI_BUNNYDIE3, 427, PRI_NPCDEATH, -100, 100, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("RABAMB.VOC", DIGI_BUNNYAMBIENT, 428, PRI_NPCAMBIENT, -100, 100, 0, DIST_NORMAL, VF_NORMAL),

			new VOC_INFO("STONE2.VOC", DIGI_STONESLIDE, 429, PRI_AMBIENT, 0, 0, 0, DIST_NORMAL, VF_LOOP),
			new VOC_INFO("NINCUT3.VOC", DIGI_NINJAINHALF, 430, PRI_NPCDEATH, -200, 200, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("RIPCHST1.VOC", DIGI_RIPPER2CHEST, 431, PRI_NPCATTACK, -100, 100, 0, DIST_NORMAL, VF_NORMAL),

			new VOC_INFO("WHIPIN2.VOC", DIGI_WHIPME, 432, PRI_AMBIENT, 0, 0, 0, DIST_NORMAL, VF_LOOP),
			new VOC_INFO("ENDLEV3.VOC", DIGI_ENDLEV, 433, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("MDALARM1.VOC", DIGI_MDALARM, 434, PRI_PLAYERDEATH, 0, 0, 0, DIST_NORMAL, VF_NORMAL),

			new VOC_INFO("METALBRK.VOC", DIGI_BREAKMETAL, 435, PRI_AMBIENT, -200, 200, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("DEBRIBRK.VOC", DIGI_BREAKDEBRIS, 436, PRI_AMBIENT, -200, 200, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("MARBELS.VOC", DIGI_BREAKMARBELS, 437, PRI_AMBIENT, -200, 200, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("BANZAI1.VOC", DIGI_BANZAI, 438, PRI_PLAYERDEATH, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("HAHA19.VOC", DIGI_HAHA1, 439, PRI_PLAYERDEATH, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("HAHA11.VOC", DIGI_HAHA2, 440, PRI_PLAYERDEATH, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("HAHA15.VOC", DIGI_HAHA3, 441, PRI_PLAYERDEATH, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("TELEPT02.VOC", DIGI_ITEM_SPAWN, 442, PRI_ITEM, 0, 0, 0, DIST_NORMAL, VF_NORMAL),

			// More Player Voices
			new VOC_INFO("JG1075.VOC", DIGI_NOREPAIRMAN2, 443, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("JG1082.VOC", DIGI_NOPOWER, 444, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("JG1087.VOC", DIGI_DOUBLEUZI, 445, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("JG1088.VOC", DIGI_NOTORDBUNNY, 446, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("JG1103.VOC", DIGI_CANBEONLYONE, 447, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("JG2000.VOC", DIGI_MIRROR1, 448, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("JG2005.VOC", DIGI_MIRROR2, 449, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("JG2020.VOC", DIGI_HITTINGWALLS, 450, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("JG2032.VOC", DIGI_GOTRAILGUN, 451, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("JG2053.VOC", DIGI_RABBITHUMP1, 452, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("JG2054.VOC", DIGI_RABBITHUMP2, 453, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("JG2045.VOC", DIGI_RABBITHUMP3, 454, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("JG2087.VOC", DIGI_RABBITHUMP4, 455, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("JG2074.VOC", DIGI_FAGRABBIT1, 456, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("JG2075.VOC", DIGI_FAGRABBIT2, 457, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("JG2078.VOC", DIGI_FAGRABBIT3, 458, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("JG3005.VOC", DIGI_STINKLIKEBABBOON, 459, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("JG3017.VOC", DIGI_WHATYOUEATBABY, 460, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("JG3047.VOC", DIGI_WHATDIEDUPTHERE, 461, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("JG3022.VOC", DIGI_YOUGOPOOPOO, 462, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("JG6053.VOC", DIGI_PULLMYFINGER, 463, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("JG3059.VOC", DIGI_SOAPYOUGOOD, 464, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("JG4012.VOC", DIGI_WASHWANG, 465, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("JG3070.VOC", DIGI_DROPSOAP, 466, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("JG6051.VOC", DIGI_REALTITS, 467, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("JG4002.VOC", DIGI_MSTRLEEP, 468, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("JG4024.VOC", DIGI_SEEKLEEPADVICE, 469, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("JG5042.VOC", DIGI_AVENGELEEPDEATH, 470, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("JG5049A.VOC", DIGI_LEEPGHOST, 471, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),

			new VOC_INFO("WDOOR02.VOC", DIGI_DOOR1, 472, PRI_SECTOROBJ, -200, 200, 0, 8000, VF_NORMAL),
			new VOC_INFO("MDOOR03.VOC", DIGI_DOOR2, 473, PRI_SECTOROBJ, -200, 200, 0, 8000, VF_NORMAL),
			new VOC_INFO("603981_1.VOC", DIGI_DOOR3, 474, PRI_SECTOROBJ, -200, 200, 0, 8000, VF_NORMAL),
			new VOC_INFO("FLAG03.VOC", DIGI_FLAGWAVE, 475, PRI_SECTOROBJ, 0, 0, 0, DIST_NORMAL, VF_LOOP),

			new VOC_INFO("JG7009.VOC", DIGI_SURFACE, 476, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("JG7001.VOC", DIGI_GASHURT, 477, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("JG2001.VOC", DIGI_BONUS_GRAB, 478, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),

			new VOC_INFO("ACHCRY04.VOC", DIGI_ANIMECRY, 479, PRI_SECTOROBJ, 0, 0, 0, -8000, VF_LOOP),
			new VOC_INFO("ACHS010.VOC", DIGI_ANIMESING1, 480, PRI_SECTOROBJ, 0, 0, 0, -8000, VF_NORMAL),
			new VOC_INFO("ACHT1006.VOC", DIGI_ANIMEMAD1, 481, PRI_SECTOROBJ, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("ACHS016.VOC", DIGI_ANIMESING2, 482, PRI_SECTOROBJ, 0, 0, 0, -8000, VF_NORMAL),
			new VOC_INFO("ACHT120A.VOC", DIGI_ANIMEMAD2, 483, PRI_SECTOROBJ, 0, 0, 0, DIST_NORMAL, VF_NORMAL),

			new VOC_INFO("TELEPT02.VOC", DIGI_PLAYER_TELEPORT, 484, PRI_LOW_PLAYERWEAP, 0, 0, 0, DIST_NORMAL,
					VF_NORMAL),
			new VOC_INFO("SLASH1.VOC", DIGI_INTRO_SLASH, 485, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),

			new VOC_INFO("WARNING.VOC", DIGI_WARNING, 486, PRI_HI_PLAYERWEAP, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("WHIRL1.VOC", DIGI_INTRO_WHIRL, 487, PRI_HI_PLAYERWEAP, 0, 0, 0, DIST_NORMAL, VF_NORMAL),

			new VOC_INFO("ACHF1003.VOC", DIGI_TOILETGIRLFART1, 488, PRI_NPCAMBIENT, -300, 300, 0, -16000, VF_NORMAL),
			new VOC_INFO("ACHF1002.VOC", DIGI_TOILETGIRLFART2, 489, PRI_NPCAMBIENT, -300, 300, 0, -16000, VF_NORMAL),
			new VOC_INFO("ACHF1016.VOC", DIGI_TOILETGIRLFART3, 490, PRI_NPCAMBIENT, -300, 300, 0, -16000, VF_NORMAL),

			new VOC_INFO("CHIMES4.VOC", DIGI_WINDCHIMES, 491, PRI_SECTOROBJ, 0, 0, 0, -8000, VF_LOOP),

			// Yet more LoWang voices
			new VOC_INFO("JGB023.VOC", DIGI_MADATCARPET, 492, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("JGB020.VOC", DIGI_JUMPONCARPET, 493, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("JGB080.VOC", DIGI_USEBROKENVEHICLE, 494, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("JGB106.VOC", DIGI_STEPONCALTROPS, 495, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("JGB130.VOC", DIGI_WANGSEESERP, 496, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("JGSB4.VOC", DIGI_SERPTAUNTWANG, 497, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("JGB166.VOC", DIGI_WANGTAUNTSERP1, 498, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("JGB156.VOC", DIGI_WANGTAUNTSERP2, 499, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("JGB193.VOC", DIGI_WANGORDER1, 500, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("JGB202.VOC", DIGI_WANGORDER2, 501, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("JGB340A.VOC", DIGI_WANGDROWNING, 502, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),

			new VOC_INFO("JGEN06.VOC", DIGI_ZILLAREGARDS, 503, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("MSG9.VOC", DIGI_PMESSAGE, 504, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),

			new VOC_INFO("UGLY1A.VOC", DIGI_SHAREND_UGLY1, 505, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("UGLY1B.VOC", DIGI_SHAREND_UGLY2, 506, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("TELEPT07.VOC", DIGI_SHAREND_TELEPORT, 507, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),

			new VOC_INFO("GOROSW1.VOC", DIGI_HOTHEADSWITCH, 508, PRI_HI_PLAYERWEAP, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("BTCREAK2.VOC", DIGI_BOATCREAK, 509, PRI_AMBIENT, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("BTRUN05.VOC", DIGI_BOATRUN2, 510, PRI_SECTOROBJ, 0, 0, 0, DIST_NORMAL, VF_LOOP),
			new VOC_INFO("BTIDLE4.VOC", DIGI_BOATIDLE, 511, PRI_SECTOROBJ, 0, 0, 0, DIST_NORMAL, VF_LOOP),
			new VOC_INFO("SHIPBELL.VOC", DIGI_SHIPBELL, 512, PRI_AMBIENT, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("SHIPHRN1.VOC", DIGI_FOGHORN, 513, PRI_AMBIENT, 0, 0, 0, DIST_NORMAL, VF_NORMAL),

			new VOC_INFO("EXP3.VOC", DIGI_CANNON, 514, PRI_HI_PLAYERWEAP, 0, 0, 0, DIST_WIDE, VF_NORMAL),

			// Yet even more player voices!
			new VOC_INFO("JG41001.VOC", DIGI_JG41001, 515, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("JG41012.VOC", DIGI_JG41012, 516, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("JG41018.VOC", DIGI_JG41018, 517, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("JG41028.VOC", DIGI_JG41028, 518, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("JG41048.VOC", DIGI_JG41048, 519, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("JG41052.VOC", DIGI_JG41052, 520, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("JG41058.VOC", DIGI_JG41058, 521, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("JG41060.VOC", DIGI_JG41060, 522, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("JG41075.VOC", DIGI_JG41075, 523, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("JG42004.VOC", DIGI_JG42004, 524, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("JG42019.VOC", DIGI_JG42019, 525, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("JG42021.VOC", DIGI_JG42021, 526, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("JG42028.VOC", DIGI_JG42028, 527, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("JG42033.VOC", DIGI_JG42033, 528, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("JG42034.VOC", DIGI_JG42034, 529, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("JG42050.VOC", DIGI_JG42050, 530, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("JG42056.VOC", DIGI_JG42056, 531, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("JG42061.VOC", DIGI_JG42061, 532, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("JG43004.VOC", DIGI_JG43004, 533, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("JG43015.VOC", DIGI_JG43015, 534, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("JG43019.VOC", DIGI_JG43019, 535, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("JG43021.VOC", DIGI_JG43021, 536, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("JG44011.VOC", DIGI_JG44011, 537, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("JG44014.VOC", DIGI_JG44014, 538, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("JG44027.VOC", DIGI_JG44027, 539, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("JG44038.VOC", DIGI_JG44038, 540, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("JG44039.VOC", DIGI_JG44039, 541, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("JG44048.VOC", DIGI_JG44048, 542, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("JG44052.VOC", DIGI_JG44052, 543, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("JG45014.VOC", DIGI_JG45014, 544, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("JG44068.VOC", DIGI_JG44068, 545, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("JG45010.VOC", DIGI_JG45010, 546, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("JG45018.VOC", DIGI_JG45018, 547, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("JG45030.VOC", DIGI_JG45030, 548, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("JG45033.VOC", DIGI_JG45033, 549, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("JG45043.VOC", DIGI_JG45043, 550, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("JG45053.VOC", DIGI_JG45053, 551, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("JG45067.VOC", DIGI_JG45067, 552, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("JG46005.VOC", DIGI_JG46005, 553, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("JG46010.VOC", DIGI_JG46010, 554, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),

			// And the girls talk back to Wang
			// Car Girl
			new VOC_INFO("LANI049.VOC", DIGI_LANI049, 555, PRI_NPCAMBIENT, -200, 200, 0, -16000, VF_NORMAL),
			new VOC_INFO("LANI051.VOC", DIGI_LANI051, 556, PRI_NPCAMBIENT, -200, 200, 0, -16000, VF_NORMAL),
			new VOC_INFO("LANI052.VOC", DIGI_LANI052, 557, PRI_NPCAMBIENT, -200, 200, 0, -16000, VF_NORMAL),
			new VOC_INFO("LANI054.VOC", DIGI_LANI054, 558, PRI_NPCAMBIENT, -200, 200, 0, -16000, VF_NORMAL),

			// Sailor Moon
			new VOC_INFO("LANI060.VOC", DIGI_LANI060, 559, PRI_NPCAMBIENT, -200, 200, 0, -16000, VF_NORMAL),
			new VOC_INFO("LANI063.VOC", DIGI_LANI063, 560, PRI_NPCAMBIENT, -200, 200, 0, -16000, VF_NORMAL),
			new VOC_INFO("LANI065.VOC", DIGI_LANI065, 561, PRI_NPCAMBIENT, -200, 200, 0, -16000, VF_NORMAL),
			new VOC_INFO("LANI066.VOC", DIGI_LANI066, 562, PRI_NPCAMBIENT, -200, 200, 0, -16000, VF_NORMAL),

			// Mechanic
			new VOC_INFO("LANI073.VOC", DIGI_LANI073, 563, PRI_NPCAMBIENT, -200, 200, 0, -16000, VF_NORMAL),
			new VOC_INFO("LANI075.VOC", DIGI_LANI075, 564, PRI_NPCAMBIENT, -200, 200, 0, -16000, VF_NORMAL),
			new VOC_INFO("LANI077.VOC", DIGI_LANI077, 565, PRI_NPCAMBIENT, -200, 200, 0, -16000, VF_NORMAL),
			new VOC_INFO("LANI079.VOC", DIGI_LANI079, 566, PRI_NPCAMBIENT, -200, 200, 0, -16000, VF_NORMAL),

			// Prune
			new VOC_INFO("LANI089.VOC", DIGI_LANI089, 567, PRI_NPCAMBIENT, -200, 200, 0, -16000, VF_NORMAL),
			new VOC_INFO("LANI091.VOC", DIGI_LANI091, 568, PRI_NPCAMBIENT, -200, 200, 0, -16000, VF_NORMAL),
			new VOC_INFO("LANI093.VOC", DIGI_LANI093, 569, PRI_NPCAMBIENT, -200, 200, 0, -16000, VF_NORMAL),
			new VOC_INFO("LANI095.VOC", DIGI_LANI095, 570, PRI_NPCAMBIENT, -200, 200, 0, -16000, VF_NORMAL),

			new VOC_INFO("AD5.VOC", DIGI_VENTWALK, 571, PRI_ENVIRONMENT, -300, 300, 0, 8000, VF_NORMAL),
			new VOC_INFO("AD6.VOC", DIGI_CARWALK, 572, PRI_ENVIRONMENT, -300, 300, 0, 8000, VF_NORMAL),
			new VOC_INFO("JET05.VOC", DIGI_JETSOAR, 573, PRI_SECTOROBJ, -100, 100, 0, 8000, VF_NORMAL),
			new VOC_INFO("VC04.VOC", DIGI_VACUUM, 574, PRI_SECTOROBJ, -100, 100, 0, DIST_NORMAL, VF_LOOP),

			// GIRL NINJA
			new VOC_INFO("LANI017.VOC", DIGI_GIRLNINJAALERTT, 575, PRI_NPCATTACK, -200, 200, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("LANI033.VOC", DIGI_GIRLNINJASCREAM, 576, PRI_NPCDEATH, -200, 200, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("LANI001.VOC", DIGI_GIRLNINJAALERT, 577, PRI_NPCATTACK, -200, 200, 0, DIST_NORMAL, VF_NORMAL),

			new VOC_INFO("CACKLE.VOC", DIGI_PRUNECACKLE, 578, PRI_NPCAMBIENT, -200, 200, 0, -16000, VF_NORMAL),
			new VOC_INFO("CACKLE2.VOC", DIGI_PRUNECACKLE2, 579, PRI_NPCAMBIENT, -200, 200, 0, -16000, VF_NORMAL),
			new VOC_INFO("CACKLE3.VOC", DIGI_PRUNECACKLE3, 580, PRI_NPCAMBIENT, -200, 200, 0, -16000, VF_NORMAL),

			new VOC_INFO("SUMO058.VOC", DIGI_SUMOSTOMP, 581, PRI_NPCATTACK, -100, 100, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("ELEV01.VOC", DIGI_VATOR, 582, PRI_SECTOROBJ, -100, 100, 0, DIST_NORMAL, VF_NORMAL),

			new VOC_INFO("JG9009.VOC", DIGI_JG9009, 583, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("Z16004.VOC", DIGI_Z16004, 584, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("Z16012.VOC", DIGI_Z16012, 585, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("Z16022.VOC", DIGI_Z16022, 586, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("Z16027.VOC", DIGI_Z16027, 587, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("JG93030.VOC", DIGI_JG93030, 588, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("JG94002.VOC", DIGI_JG94002, 589, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("Z17010.VOC", DIGI_Z17010, 590, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("Z17052.VOC", DIGI_Z17052, 591, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("Z17025.VOC", DIGI_Z17025, 592, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("ML25014.VOC", DIGI_ML25014, 593, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("ML250101.VOC", DIGI_ML250101, 594, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("JG9022.VOC", DIGI_JG9022, 595, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("JG9032.VOC", DIGI_JG9032, 596, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("JG9038.VOC", DIGI_JG9038, 597, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("JG9055.VOC", DIGI_JG9055, 598, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("JG9060.VOC", DIGI_JG9060, 599, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("JG92055.VOC", DIGI_JG92055, 600, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("ML25032.VOC", DIGI_ML25032, 601, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("JG92036.VOC", DIGI_JG92036, 602, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("JG92042.VOC", DIGI_JG92042, 603, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("ML26001.VOC", DIGI_ML26001, 604, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("JG93000.VOC", DIGI_JG93000, 605, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("JG93011.VOC", DIGI_JG93011, 606, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("JG93018.VOC", DIGI_JG93018, 607, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("JG93023.VOC", DIGI_JG93023, 608, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("ML26008.VOC", DIGI_ML26008, 609, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("ML26011.VOC", DIGI_ML26011, 610, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("JG94007.VOC", DIGI_JG94007, 611, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("JG94024.VOC", DIGI_JG94024, 612, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("JG94039.VOC", DIGI_JG94039, 613, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("JG95012.VOC", DIGI_JG95012, 614, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("ZWALK5.VOC", DIGI_ZILLASTOMP, 615, PRI_NPCATTACK, -100, 100, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("ZC1.VOC", DIGI_ZC1, 616, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("ZC2.VOC", DIGI_ZC2, 617, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("ZC3.VOC", DIGI_ZC3, 618, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("ZC4.VOC", DIGI_ZC4, 619, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("ZC5.VOC", DIGI_ZC5, 620, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("ZC6.VOC", DIGI_ZC6, 621, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("ZC7.VOC", DIGI_ZC7, 622, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_LOOP),
			new VOC_INFO("ZC8.VOC", DIGI_ZC8, 623, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("ZC9.VOC", DIGI_ZC9, 624, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL),
			new VOC_INFO("Z16043.VOC", DIGI_Z16043, 625, PRI_PLAYERVOICE, 0, 0, 0, DIST_NORMAL, VF_NORMAL), };

	// Ambient Flags, flags can be added whenever needed, up to 16 bits
	private static final int AMB_NONE = 0;
	private static final int AMB_PAN = 8; // 1 = Don't do panning of sound
	private static final int AMB_INTERMIT = 32; // 1 = This is a non-looping intermittant sound

	// Tic counts used for intermittent sounds
	private static final int AMB_TICRATE = 12; // 120/10 since it's only called 10x per second
	private static final int AMB_NOTICS = 0;
	private static final int AMB_5 = 5 * AMB_TICRATE;
	private static final int AMB_10 = 10 * AMB_TICRATE; // AMB_TICRATE is the game's tic rate
	private static final int AMB_20 = 20 * AMB_TICRATE;
	private static final int AMB_30 = 30 * AMB_TICRATE;
	private static final int AMB_45 = 45 * AMB_TICRATE;
	private static final int AMB_60 = 60 * AMB_TICRATE;
	private static final int AMB_120 = 120 * AMB_TICRATE;

	public static final Amb_Info[] ambarray = {
			// BUBBLES
			new Amb_Info(0, DIGI_BUBBLES, AMB_PAN, AMB_NOTICS),

			// CRICKETS
			new Amb_Info(1, DIGI_CRICKETS, AMB_PAN, AMB_NOTICS),

			// AMBIENT WATER DRIPPING IN CAVE
			new Amb_Info(2, DIGI_CAVEDRIP1, AMB_NONE, AMB_NOTICS),
			new Amb_Info(3, DIGI_CAVEDRIP2, AMB_INTERMIT, AMB_10),
			new Amb_Info(4, DIGI_DRIP, AMB_PAN | AMB_INTERMIT, AMB_20),

			// WATER FALL
			new Amb_Info(5, DIGI_WATERFALL1, AMB_NONE, AMB_NOTICS),
			new Amb_Info(6, DIGI_WATERFALL2, AMB_NONE, AMB_NOTICS),

			// WATER FLOWING
			new Amb_Info(7, DIGI_WATERFLOW1, AMB_NONE, AMB_NOTICS),
			new Amb_Info(8, DIGI_WATERFLOW2, AMB_NONE, AMB_NOTICS),

			// CRACKLING FIRE FOR CONTINUOUS BURN
			new Amb_Info(9, DIGI_FIRE1, AMB_NONE, AMB_NOTICS),

			// POWERFULL HIGH HEAT CONTINUOUS BURN
			new Amb_Info(10, DIGI_FIRE2, AMB_NONE, AMB_NOTICS),

			// AMBIENT GONG FOR USE IN TEMPLE/PALACE LEVELS
			new Amb_Info(11, DIGI_GONG, AMB_INTERMIT, AMB_120),

			// AMBIENT LAVA FLOW
			new Amb_Info(12, DIGI_LAVAFLOW1, AMB_NONE, AMB_NOTICS),

			// AMBIENT MUD BUBBLES
			new Amb_Info(13, DIGI_MUBBUBBLES1, AMB_NONE, AMB_NOTICS),

			// AMBIENT EARTH QUAKE
			new Amb_Info(14, DIGI_EARTHQUAKE, AMB_NONE, AMB_NOTICS),

			// YUCKY SEWER FLOW
			new Amb_Info(15, DIGI_SEWERFLOW1, AMB_NONE, AMB_NOTICS),

			// STEAM FLOW
			new Amb_Info(16, DIGI_STEAM1, AMB_NONE, AMB_NOTICS),

			// VOLCANIC STEAM VENT
			new Amb_Info(17, DIGI_VOLCANOSTEAM1, AMB_NONE, AMB_NOTICS),

			// SCARY AMBIENT SWAMP SOUNDS
			new Amb_Info(18, DIGI_SWAMP, AMB_NONE, AMB_NOTICS),

			// AMBIENT ROLLING THUNDER
			new Amb_Info(19, DIGI_THUNDER, AMB_PAN | AMB_INTERMIT, AMB_60),

			// UNDERWATER AMBIENCE
			new Amb_Info(20, DIGI_UNDERWATER, AMB_PAN, AMB_NOTICS),

			// SPOOKY ETHERAL VOID AMBIENCE (NETHERWORLDLY SOUNDS),
			new Amb_Info(21, DIGI_VOID1, AMB_NONE, AMB_NOTICS), new Amb_Info(22, DIGI_VOID2, AMB_NONE, AMB_NOTICS),
			new Amb_Info(23, DIGI_VOID3, AMB_NONE, AMB_NOTICS), new Amb_Info(24, DIGI_VOID4, AMB_NONE, AMB_NOTICS),
			new Amb_Info(25, DIGI_VOID5, AMB_NONE, AMB_NOTICS),

			// VOLCANIC ERUPTION
			new Amb_Info(26, DIGI_ERUPTION, AMB_NONE, AMB_NOTICS),

			// VOLCANIC SIZZLING PROJECTILES FLYING THROUGH AIR
			new Amb_Info(27, DIGI_VOLCANOPROJECTILE, AMB_NONE, AMB_NOTICS),

			// LIGHT WIND AMBIENCE
			new Amb_Info(28, DIGI_LIGHTWIND, AMB_NONE, AMB_NOTICS),

			// STRONG BLOWING WIND AMBIENCE
			new Amb_Info(29, DIGI_STRONGWIND, AMB_PAN | AMB_INTERMIT, AMB_20),

			// BREAKING WOOD AMBIENCE
			new Amb_Info(30, DIGI_BREAKINGWOOD, AMB_INTERMIT, AMB_120),

			// BREAKING, TUMBLING STONES FALLING AMBIENCE
			new Amb_Info(31, DIGI_BREAKSTONES, AMB_NONE, AMB_NOTICS),

			// MOTOR BOAT
			new Amb_Info(32, DIGI_NULL, AMB_NONE, AMB_NOTICS), new Amb_Info(33, DIGI_NULL, AMB_NONE, AMB_NOTICS),
			new Amb_Info(34, DIGI_NULL, AMB_NONE, AMB_NOTICS),

			// WWII JAP ARMY TANK
			new Amb_Info(35, DIGI_NULL, AMB_NONE, AMB_NOTICS), new Amb_Info(36, DIGI_NULL, AMB_NONE, AMB_NOTICS),
			new Amb_Info(37, DIGI_NULL, AMB_NONE, AMB_NOTICS), new Amb_Info(38, DIGI_NULL, AMB_NONE, AMB_NOTICS),
			new Amb_Info(39, DIGI_NULL, AMB_NONE, AMB_NOTICS),

			// WWII JAP BOMBER PLANE
			new Amb_Info(40, DIGI_BOMBRFLYING, AMB_NONE, AMB_NOTICS),
			new Amb_Info(41, DIGI_BOMBRDROPBOMB, AMB_NONE, AMB_NOTICS),

			// GIANT DRILL MACHINE
			new Amb_Info(42, DIGI_DRILL, AMB_NONE, AMB_NOTICS),

			// SECTOR GEAR COG TURNING
			new Amb_Info(43, DIGI_GEAR1, AMB_NONE, AMB_NOTICS),

			// GENERIC SECTOR OBJECT MACHINE RUNNING
			new Amb_Info(44, DIGI_MACHINE1, AMB_NONE, AMB_NOTICS),

			// ENGINE ROOM
			new Amb_Info(45, DIGI_ENGROOM1, AMB_NONE, AMB_NOTICS),
			new Amb_Info(46, DIGI_ENGROOM2, AMB_NONE, AMB_NOTICS),
			new Amb_Info(47, DIGI_ENGROOM3, AMB_NONE, AMB_NOTICS),
			new Amb_Info(48, DIGI_ENGROOM4, AMB_NONE, AMB_NOTICS),
			new Amb_Info(49, DIGI_ENGROOM5, AMB_NONE, AMB_NOTICS),

			// HELICOPTER, SPINNING BLADE SOUND
			new Amb_Info(50, DIGI_HELI, AMB_NONE, AMB_NOTICS),

			// ECHOING HEART
			new Amb_Info(51, DIGI_BIGHART, AMB_NONE, AMB_NOTICS),

			// ETHERAL WIND
			new Amb_Info(52, DIGI_WIND4, AMB_NONE, AMB_NOTICS),

			// SPOOKY SINE WAVE
			new Amb_Info(53, DIGI_SPOOKY1, AMB_NONE, AMB_NOTICS),

			// JET ENGINE
			new Amb_Info(54, DIGI_JET, AMB_NONE, AMB_NOTICS),

			// CEREMONIAL DRUM CHANT
			new Amb_Info(55, DIGI_DRUMCHANT, AMB_NONE, AMB_NOTICS),

			new Amb_Info(56, DIGI_ASIREN1, AMB_INTERMIT, AMB_45), new Amb_Info(57, DIGI_FIRETRK1, AMB_INTERMIT, AMB_60),
			new Amb_Info(58, DIGI_TRAFFIC1, AMB_INTERMIT, AMB_60),
			new Amb_Info(59, DIGI_TRAFFIC2, AMB_INTERMIT, AMB_60),
			new Amb_Info(60, DIGI_TRAFFIC3, AMB_INTERMIT, AMB_60),
			new Amb_Info(61, DIGI_TRAFFIC4, AMB_INTERMIT, AMB_60),
			new Amb_Info(62, DIGI_TRAFFIC5, AMB_INTERMIT, AMB_30),
			new Amb_Info(63, DIGI_TRAFFIC6, AMB_INTERMIT, AMB_60), new Amb_Info(64, DIGI_HELI1, AMB_INTERMIT, AMB_120),
			new Amb_Info(65, DIGI_JET1, AMB_INTERMIT, AMB_120), new Amb_Info(66, DIGI_MOTO1, AMB_INTERMIT, AMB_45),
			new Amb_Info(67, DIGI_MOTO2, AMB_INTERMIT, AMB_60), new Amb_Info(68, DIGI_NEON1, AMB_INTERMIT, AMB_5),
			new Amb_Info(69, DIGI_SUBWAY, AMB_INTERMIT, AMB_30), new Amb_Info(70, DIGI_TRAIN1, AMB_INTERMIT, AMB_120),
			new Amb_Info(71, DIGI_BIRDS1, AMB_INTERMIT, AMB_10), new Amb_Info(72, DIGI_BIRDS2, AMB_INTERMIT, AMB_10),
			new Amb_Info(73, DIGI_AMOEBA, AMB_NONE, AMB_NOTICS), new Amb_Info(74, DIGI_TRAIN3, AMB_INTERMIT, AMB_120),
			new Amb_Info(75, DIGI_TRAIN8, AMB_INTERMIT, AMB_120), new Amb_Info(76, DIGI_WHIPME, AMB_NONE, AMB_NOTICS),
			new Amb_Info(77, DIGI_FLAGWAVE, AMB_NONE, AMB_NOTICS),
			new Amb_Info(78, DIGI_ANIMECRY, AMB_NONE, AMB_NOTICS),
			new Amb_Info(79, DIGI_WINDCHIMES, AMB_INTERMIT, AMB_10),
			new Amb_Info(80, DIGI_BOATCREAK, AMB_INTERMIT, AMB_10),
			new Amb_Info(81, DIGI_SHIPBELL, AMB_INTERMIT, AMB_30),
			new Amb_Info(82, DIGI_FOGHORN, AMB_INTERMIT, AMB_120), };
}
