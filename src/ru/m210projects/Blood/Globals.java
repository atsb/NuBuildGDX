// This file is part of BloodGDX.
// Copyright (C) 2017-2019  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// BloodGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// BloodGDX is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with BloodGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.Blood;

import static ru.m210projects.Build.Engine.*;

import ru.m210projects.Blood.Types.BloodIniFile;
import ru.m210projects.Blood.Types.GAMEINFO;
import ru.m210projects.Blood.Types.LOADITEM;
import ru.m210projects.Blood.Types.NETINFO;
import ru.m210projects.Blood.Types.POSTURE;

public class Globals {

	public static final int BACKBUTTON = 9216;
	public static final int MOUSECURSOR = 9217;
	public static final int RESERVED1 = 9218;
	public static final int RESERVED2 = 9219;

	public static final int kHUDEye = 9220;
	public static final int kHUDLeft2 = 9221;
	public static final int kHUDRight2 = 9222;
	public static final int kWideLoading = 9223;
	public static final int kUltraWideLoading = 9224;

	public static BloodIniFile MainINI;
	public static PLAYER gMe;
	public static final GAMEINFO defGameInfo = new GAMEINFO(0, 2, 0, 0, null,
			null, 2, null, null, 0, 0, 0, 1, 0, 0, 0, 0, 0, 2, 3600, 1800,
			1800, 7200);
	public static final GAMEINFO pGameInfo = new GAMEINFO();
	public static final NETINFO pNetInfo = new NETINFO();
	public static boolean maphack_highlight = false;

	public static Fire fire;

	public static boolean kGameCrash = false;
	public static final int kNetModeOff = 0;
	public static final int kNetModeCoop = 1;
	public static final int kNetModeBloodBath = 2;
	public static final int kNetModeTeams = 3;

	public static int nFakePlayers = 2;
	public static boolean kFakeMultiplayer;
	public static final int kPalLookups = 64;

	public static String boardfilename;

	public static boolean gTextInput = false;
	public static boolean gFogMode = false;
	public static boolean gMapScrollMode = false;
	public static boolean gInfiniteAmmo = false;
	public static boolean gFullMap = false;
	public static boolean SplitScreen = false;

	public static final byte SS_WALL = 0;
	public static final byte SS_CEILING = 1;
	public static final byte SS_FLOOR = 2;
	public static final byte SS_SPRITE = 3;
	public static final byte SS_MASKED = 4;

	public static final byte SS_SECTOR = 6;

	// Hit definitions
	public static final int kHitTypeMask = 0xE000;
	public static final int kHitIndexMask = 0x1FFF;
	public static final int kHitFloor = 0x4000;
	public static final int kHitSector = 0x4000;
	public static final int kHitCeiling = 0x6000;
	public static final int kHitWall = 0x8000;
	public static final int kHitSprite = 0xC000;

	public static final int kSectorParallax = 0x01;
	public static final int kSectorSloped = 0x02;
	public static final int kSectorSwapXY = 0x04;
	public static final int kSectorExpand = 0x08;
	public static final int kSectorFlipX = 0x10;
	public static final int kSectorFlipY = 0x20;
	public static final int kSectorFlipMask = 0x34;
	public static final int kSectorRelAlign = 0x40;
	public static final int kSectorFloorShade = 0x8000;

	public static final int kWallBlocking = 0x0001;
	public static final int kWallBottomSwap = 0x0002;
	public static final int kWallBottomOrg = 0x0004;
	public static final int kWallOutsideOrg = 0x0004;
	public static final int kWallFlipX = 0x0008;
	public static final int kWallMasked = 0x0010;
	public static final int kWallOneWay = 0x0020;
	public static final int kWallHitscan = 0x0040;
	public static final int kWallTranslucent = 0x0080;
	public static final int kWallFlipY = 0x0100;
	public static final int kWallFlipMask = 0x0108;
	public static final int kWallTranslucentR = 0x0200;
	public static final int kWallMapSecret = 0x0400;
	public static final int kWallMapNever = 0x0800;
	public static final int kWallMapAlways = 0x1000;
	public static final int kWallMoveMask = 0xC000;
	public static final int kWallMoveNone = 0x0000;
	public static final int kWallMoveForward = 0x4000;
	public static final int kWallMoveBackward = 0x8000;

	public static final int kMaxStatus = MAXSTATUS;
	public static int kMaxSectors = MAXSECTORS;
	public static int kMaxWalls = MAXWALLS;
	public static int kMaxSprites = MAXSPRITES;
	public static final int kMaxTiles = MAXTILES - USERTILES;
	public static final int kUserTiles = kMaxTiles;

	public static final int TILTBUFFER = 4078;
	public static final int BALLBUFFER = 4079;
	public static final int BALLBUFFER2 = 4077;

	public static final int kMaxPlayers = 8;
	public static final int kMaxViewSprites = 1024;

	public static final int kMinZVel = (6 << 4);
	public static final int kAirDrag = 0x0100;

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

	public static final int kZUnitsPerMeter = (32 << 8);
	public static final int kXUnitsPerMeter = (32 << 4);

	// Game clock timer resolution
	public static final int kTimerRate = 120;

	// number of clock ticks per game frame
	public static int kFrameTicks = 4; // 30 frames/sec

	public static final int kStatDefault = 0; // inactive items, like torches and stuff, but NOT dudes
	public static final int kStatEffect = 1; // non-damaging ricochets, splashes, etc.
	public static final int kStatExplosion = 2;
	public static final int kStatItem = 3; // items that can be picked up
	public static final int kStatThing = 4; // things that can be destroyed/moved
	public static final int kStatMissile = 5; // player/enemy missiles that do damage
	public static final int kStatDude = 6; // an active dude
	public static final int kStatInactive = 7; // an inactive dude
	public static final int kStatRespawn = 8;
	public static final int kStatPurge = 9; // use these for purgeable sprites
	public static final int kStatMarker = 10;
	public static final int kStatTraps = 11;
	public static final int kStatAmbient = 12; // sprites triggered by proximity
	public static final int kStatSpares = 13; // allocated invisible sprites
	public static final int kStatFlare = 14; // flares that are stuck in dudes
	public static final int kStatDebris = 15; // moving non-Thing debris
	public static final int kStatMarker2 = 16;
	public static final int kStatGDXDudeTargetChanger = 20; // target changer generator sprite
	public static final int kStatFree = 1024;

	public static final int kSpriteBlocking = 0x0001;
	public static final int kSpriteTranslucent = 0x0002;
	public static final int kSpriteFlipX = 0x0004;
	public static final int kSpriteFlipY = 0x0008;

	public static final int kSpriteFace = 0x0000;
	public static final int kSpriteWall = 0x0010;
	public static final int kSpriteFloor = 0x0020;
	public static final int kSpriteSpin = 0x0030;
	public static final int kSpriteRMask = 0x0030;

	public static final int kSpriteOneSided = 0x0040;
	public static final int kSpriteOriginAlign = 0x0080;
	public static final int kSpriteHitscan = 0x0100;
	public static final int kSpriteTranslucentR = 0x0200;

	public static final int kSpriteMapSecret = 0x0400;
	public static final int kSpriteMapNever = 0x0800;
	public static final int kSpritePushable = 0x1000;

	public static final int kSpriteMoveMask = 0x6000;
	public static final int kSpriteMoveNone = 0x0000;
	public static final int kSpriteMoveForward = 0x2000;
	public static final int kSpriteMoveFloor = 0x2000;
	public static final int kSpriteMoveReverse = 0x4000;
	public static final int kSpriteMoveCeiling = 0x4000;
	public static final int kSpriteInvisible = 0x8000;

	public static final int kMarkerPlayerStart = 1;
	public static final int kMarkerDeathStart = 2;
	public static final int kMarkerOff = 3;
	public static final int kMarkerOn = 4;
	public static final int kMarkerAxis = 5;
	public static final int kMarkerLowerLink = 6;
	public static final int kMarkerUpperLink = 7;
	public static final int kMarkerWarpDest = 8;
	public static final int kMarkerUpperWater = 9;
	public static final int kMarkerLowerWater = 10;
	public static final int kMarkerUpperStack = 11;
	public static final int kMarkerLowerStack = 12;
	public static final int kMarkerUpperGoo = 13;
	public static final int kMarkerLowerGoo = 14;
	public static final int kNothing = -1;

	public static final int kSeqDudeIdle = 0;
	public static final int kSeqDudeDeath1 = 1; // normal death
	public static final int kSeqDudeDeath2 = 2; // exploding death
	public static final int kSeqDudeDeath3 = 3; // burning death
	public static final int kSeqDudeTesla = 4;
	public static final int kSeqDudeRecoil = 5;
	public static final int kSeqDudeAttack = 6;
	public static final int kSeqDudeWalk = 8;

	public static final int kSeqRatWalk = 7;

	public static final int kSeqDudeMax = 16;

	public static int[] zofslope = new int[2];
	public static PLAYER[] gPlayer = new PLAYER[kMaxPlayers];

	public static int[] nTeamCount = new int[kMaxPlayers];
	public static int[] nTeamClock = new int[kMaxPlayers];

	public static int gFrame = 0;
	public static int gTicks = 0;
	public static boolean gNoClip = false;

	public static byte[] gStdColor = new byte[32]; // standard 32 colors
	public static int[][] StdPal =
	{
		{  0,  0,  0},
		{  0,  0,170},
		{  0,170,  0},
		{  0,170,170},
		{170,  0,  0},
		{170,  0,170},
		{170, 85,  0},
		{170,170,170},
		{ 85, 85, 85},
		{ 85, 85,255},
		{ 85,255, 85},
		{ 85,255,255},
		{255, 85, 85},
		{255, 85,255},
		{255,255, 85},
		{255,255,255},
		{241,241,241},
		{226,226,226},
		{211,211,211},
		{196,196,196},
		{181,181,181},
		{166,166,166},
		{151,151,151},
		{136,136,136},
		{120,120,120},
		{105,105,105},
		{ 90, 90, 90},
		{ 75, 75, 75},
		{ 60, 60, 60},
		{ 45, 45, 45},
		{ 30, 30, 30},
		{ 15, 15, 15},
	};

	public static POSTURE[][] gPosture = {

		// normal human
		{
			new POSTURE(16384, 16384, 16384, new int[] { 14, 17 }, 24, 16, 32, 80, 5632, 4608, 3072, 144),
			new POSTURE(4608, 4608, 4608, new int[] { 14, 17 }, 24, 16, 32, 80, 5120, 4096, -1536, 176),
			new POSTURE(8192, 8192, 8192, new int[] { 22, 28 }, 24, 16, 16, 40, 2048, 1536, -1536, 176),
			new POSTURE(16384, 16384, 16384, new int[] { 14, 17 }, 24, 16, 32, 80, 5632, 4608, 3072, 144),
		},

		// normal beast
		{
			new POSTURE(16384, 16384, 16384, new int[] { 14, 17 }, 24, 16, 32, 80, 5632, 2560, 3072, 144),
			new POSTURE(4608, 4608, 4608, new int[] { 14, 17 }, 24, 16, 32, 80, 5120, 3072,	-1536, 176),
			new POSTURE(8192, 8192, 8192, new int[] { 22, 28 }, 24, 16, 16, 40, 2048, 1024,-1536, 176),
			new POSTURE(16384, 16384, 16384, new int[] { 14, 17 }, 24, 16, 32, 80, 5632, 2560, 3072, 144),
		}
		,

		// shrink human
		{
			new POSTURE(10384, 12384, 12384, new int[] { 14, 17 }, 24, 16, 32, 80, 5632, 4608, 3072, 144),
			new POSTURE(2108, 2108, 2108, new int[] { 14, 17 }, 24, 16, 32, 80, 5120, 4096, -1536, 176),
			new POSTURE(2192, 3192, 4192, new int[] { 22, 28 }, 24, 16, 16, 40, 2048, 1536, -1536, 176),
			new POSTURE(10384, 11384, 12384, new int[] { 14, 17 }, 24, 16, 32, 80, 5632, 4608, 3072, 144),
		},

		// grown human
		{
			new POSTURE(19384, 15384, 15384, new int[] { 14, 17 }, 24, 16, 32, 80, 5632, 4608, 3072, 144),
			new POSTURE(5608, 5608, 5608, new int[] { 14, 17 }, 24, 16, 32, 80, 5120, 4096, -1536, 176),
			new POSTURE(11192, 11192, 11192, new int[] { 22, 28 }, 24, 16, 16, 40, 2048, 1536, -1536, 176),
			new POSTURE(19384, 19384, 19384, new int[] { 14, 17 }, 24, 16, 32, 80, 5632, 4608, 3072, 144),
		},
	};

	// Weight coefficients for color matching
	public static final int kWeightR = 1;
	public static final int kWeightG = 1;
	public static final int kWeightB = 1;

//	public static int gNetPlayers = 1;

	public static final int kRotateNormal = 0;
	public static final int kRotateTranslucent = 0x01;
	public static final int kRotateScale = 0x02;
	public static final int kRotateYFlip = 0x04;
	public static final int kRotateUnclipped = 0x08;
	public static final int kRotateStatus = 0x0A;
	public static final int kRotateCorner = 0x10;
	public static final int kRotateTranslucentR = 0x20; // is translucency level two (33% opacity). It won't work if
														// kRotateTranslucent is not set.
	public static final int kRotateNoMask = 0x40; // forced masking off is set. It discards translucency too.
	public static final int kRotateAllPages = 0x80; // "permanent" tile (deprecated)

	public static boolean cheatsOn = false;
	public static int gFrameClock = 0;
//	public static int gGameClock = 0;

	public static final int kPLUNormal = 0;
	public static final int kPLUSaturate = 1;
	public static final int kPLURed = 2;
	public static final int kPLUCultist2 = 3;
	public static final int kPLUSpider3 = 4;
	public static final int kPLUGray = 5;
	public static final int kPLUGrayish = 6;
	public static final int kPLUSpider1 = 7;
	public static final int kPLUSpider2 = 8;
	public static final int kPLUFlame = 9;
	public static final int kPLUCold = 10;
	public static final int kPLUPlayer1 = 11; // also kPLUPlayer5
	public static final int kPLUPlayer2 = 12; // also kPLUPlayer6
	public static final int kPLUPlayer3 = 13; // also kPLUPlayer7
	public static final int kPLUPlayer4 = 14; // also kPLUPlayer8

	public static final int kMaxPLU = 15;

	public static final int kPalNormal = 0;
	public static final int kPalWater = 1;
	public static final int kPalBeast = 2;
	public static final int kPalSewer = 3;
	public static final int kPalInvuln1 = 4;
	public static final int kMaxPalettes = 5;

	public static LOADITEM[] PLU =
	{
		new LOADITEM( kPLUNormal,	"NORMAL" ),
		new LOADITEM( kPLUSaturate,	"SATURATE" ),
		new LOADITEM( kPLURed,		"BEAST" ),
		new LOADITEM( kPLUCultist2,	"TOMMY" ),
		new LOADITEM( kPLUSpider3,	"SPIDER3" ),
		new LOADITEM( kPLUGray,		"GRAY" ),
		new LOADITEM( kPLUGrayish,	"GRAYISH" ),
		new LOADITEM( kPLUSpider1,	"SPIDER1" ),
		new LOADITEM( kPLUSpider2,	"SPIDER2" ),
		new LOADITEM( kPLUFlame,	"FLAME" ),
		new LOADITEM( kPLUCold,		"COLD" ),
		new LOADITEM( kPLUPlayer1,	"P1" ),	// also kPLUPlayer5
		new LOADITEM( kPLUPlayer2,	"P2" ),	// also kPLUPlayer6
		new LOADITEM( kPLUPlayer3,	"P3" ),	// also kPLUPlayer7
		new LOADITEM( kPLUPlayer4,	"P4" ),	// also kPLUPlayer8
	};

	public static LOADITEM[] PAL =
	{
		new LOADITEM( kPalNormal,	"BLOOD" ),
		new LOADITEM( kPalWater,	"WATER" ),
		new LOADITEM( kPalBeast,	"BEAST" ),
		new LOADITEM( kPalSewer,	"SEWER" ),
		new LOADITEM( kPalInvuln1,	"INVULN1" )
	};

}
