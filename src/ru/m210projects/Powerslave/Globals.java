// This file is part of PowerslaveGDX.
// Copyright (C) 2019  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// PowerslaveGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// PowerslaveGDX is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with PowerslaveGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.Powerslave;

import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Powerslave.Main.game;

import java.util.HashMap;

import com.badlogic.gdx.Screen;

import ru.m210projects.Powerslave.Main.UserFlag;
import ru.m210projects.Powerslave.Screens.GameScreen;
import ru.m210projects.Powerslave.Type.StatusAnim;
import ru.m210projects.Powerslave.Type.BlockInfo;
import ru.m210projects.Powerslave.Type.BobStruct;
import ru.m210projects.Powerslave.Type.BubbleMachineStruct;
import ru.m210projects.Powerslave.Type.BubbleStruct;
import ru.m210projects.Powerslave.Type.DripStruct;
import ru.m210projects.Powerslave.Type.EpisodeInfo;
import ru.m210projects.Powerslave.Type.Input;
import ru.m210projects.Powerslave.Type.ItemAnimStruct;
import ru.m210projects.Powerslave.Type.MoveSectStruct;
import ru.m210projects.Powerslave.Type.ObjectStruct;
import ru.m210projects.Powerslave.Type.PlayerStruct;
import ru.m210projects.Powerslave.Type.RaStruct;
import ru.m210projects.Powerslave.Type.TrailPointStruct;
import ru.m210projects.Powerslave.Type.TrailStruct;

public class Globals {

	public static int LOGO = 3442; //3592 - Exhumed
	public static int BACKGROUND = 3352;  //3581 - Exhumed

	public static EpisodeInfo gTrainingEpisode;
	public static EpisodeInfo gOriginalEpisode;
	public static EpisodeInfo gCurrentEpisode;
	public static HashMap<String, EpisodeInfo> episodes = new HashMap<String, EpisodeInfo>();

	public static final int BACKBUTTON = 9216;
	public static final int MOUSECURSOR = 9217;
	public static final int THEFONT = 9220;
	public static final int HUDLEFT = 9218;
	public static final int HUDRIGHT = 9219;

	public static boolean followmode = false;
	public static int followx, followy;
	public static int followvel, followsvel, followa;
	public static float followang;

	public static UserFlag mUserFlag = UserFlag.None;
	public static String boardfilename;


	//public static final int nEvent1 = 0x10000;
	public static final int nEventProcess = 0x20000;
	public static final int nEvent6 = 0x70000;
	public static final int nEventDamage = 0x80000;
	public static final int nEventView = 0x90000;
	public static final int nEventRadialDamage = 0xA0000;
	//0xD0000

	public static byte[][] origpalookup = new byte[12][];
	public static PlayerStruct PlayerList[] = new PlayerStruct[8];
	public static int nLocalPlayer;
	public static int PlayerCount;

	public static int[] nPlayerDX = new int[8];
	public static int[] nPlayerDY = new int[8];
	public static String[] szPlayerName = new String[8];

	public static int[] nXDamage = new int[8];
	public static int[] nYDamage = new int[8];
	public static short[] nDoppleSprite = new short[8];
	public static short[] nPlayerOldWeapon = new short[8];
	public static short[] nPlayerClip = new short[8]; //M60 Clip
	public static short[] nPistolClip = new short[8]; //Pistol reload
	public static short[] nPlayerPushSound = new short[8];
	public static short[] nTauntTimer = new short[8];
	public static short[] nPlayerTorch = new short[8];
	public static short[] nPlayerWeapons = new short[8];
	public static short[] nPlayerLives = new short[8];
	public static short[] nPlayerItem = new short[8];
	public static short[] nPlayerInvisible = new short[8];
	public static short[] nPlayerDouble = new short[8];
	public static short[] nPlayerViewSect = new short[8];
	public static short[] nPlayerFloorSprite = new short[8];
	public static short[] nPlayerScore = new short[8];
	public static short[] nPlayerPushSect = new short[8];

	public static int[] totalvel = new int[8];
	public static short[] nNetStartSprite = new short[8];
	public static int nCurStartSprite, nNetStartSprites;
	public static int nNetPlayerCount;
	public static short[] nPlayerSwear = new short[8];
	public static short[] nPlayerGrenade = new short[8];
	public static short[] nPlayerSnake = new short[8];
	public static short[] nTemperature = new short[8];
	public static short[] nBreathTimer = new short[8];
	public static float[] nDestVertPan = new float[8];
	public static short[] dVertPan = new short[8];
	public static short[] nDeathType = new short[8];
	public static boolean[] bTouchFloor = new boolean[8];
	public static short[] nQuake = new short[8];
	public static RaStruct[] Ra = new RaStruct[8];

	public static Input[] sPlayerInput = new Input[8];
	public static BlockInfo[] sBlockInfo = new BlockInfo[100];

	public static int nRadialSpr;
	public static int nRadialDamage;
	public static int nDamageRadius;
	public static int nRadialOwner;
	public static short nRadialBullet;

	public static int nCreaturesLeft;
	public static int nCreaturesMax;
	public static int nMagicCount;

	public static short[] SectFlag = new short[MAXSECTORS];
	public static short[] SectDepth = new short[MAXSECTORS];
	public static short[] SectSpeed = new short[MAXSECTORS];
	public static short[] SectDamage = new short[MAXSECTORS];
	public static short[] SectAbove = new short[MAXSECTORS];
	public static short[] SectBelow = new short[MAXSECTORS];
	public static short[] SectSound = new short[MAXSECTORS];
	public static short[] SectSoundSect = new short[MAXSECTORS];

	public static int nMachineCount;
	public static BubbleMachineStruct[] Machine = new BubbleMachineStruct[125];

	public static int nCamerax, nCameray, nCameraz;
	public static float nCameraa, nCamerapan;

	public static short besttarget;
	public static boolean bPlayerPan;
	public static boolean bLockPan;
	public static boolean bSlipMode;

	public static int nStandHeight;
	public static int nPlayerPic;
	public static int nFreeze;

	public static StatusAnim StatusAnim[] = new StatusAnim[50];

	public static int nItemTextIndex;
	public static final short[] nItemText = { -1, -1, -1, -1, -1, -1, 18, 20, 19, 13, -1, 10, 1, 0, 2, -1, 3, -1, 4, 5,
			9, 6, 7, 8, -1, 11, -1, 13, 12, 14, 15, -1, 16, 17, -1, -1, -1, 21, 22, -1, -1, -1, -1, -1, -1, 23, 24, 25,
			26, 27, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 };

	public static final String gString[] = { "CINEMAS", "The ancient Egyptian city", "of Karnak has been seized",
			"by unknown powers, and great", "turmoil is spreading into", "neighboring lands, posing",
			"a grave threat to planet", "Earth. Militant forces from", "all parts of the globe have",
			"entered the Karnak Valley,", "but none have returned. The", "only known information",
			"regarding this crisis came", "from a dying Karnak villager", "who managed to wander out of",
			"the valley to safety.", "'They've stolen the great", "king's Mummy...', murmured",
			"the dying villager, but the", "villager died before he", "could say more. With no", "other options, world",
			"leaders have chosen to drop", "you into the valley via", "helicopter in an attempt",
			"to find and destroy the", "threatening forces and", "resolve the mystery that", "has engulfed this once",
			"peaceful land. Flying at", "high altitude to avoid", "being shot down like others",
			"before you, your copter", "mysteriously explodes in the", "air as you barely escape,",
			"with no possible contact", "with the outside world.", "Scared as hell, you descend",
			"into the heart of Karnak...", "home to the celebrated", "burial crypt of the great", "King Ramses.", "END",
			"An evil force known as the", "Kilmaat has besieged the", "sanctity of my palace and",
			"is pulling at the very", "tendrils of my existence.", "These forces intend to",
			"enslave me by reanimating", "my preserved corpse. I have", "protected my corpse with a",
			"genetic key.  If you are", "unsuccessful I cannot", "protect civilization, and",
			"chaos will prevail. I am", "being torn between worlds", "and this insidious",
			"experiment must be stopped.", "END", "I have hidden a mystical", "gauntlet at El Kab that will",
			"channel my energy through", "your hands. Find the", "gauntlet and cross the Aswan",
			"High Dam to defeat the evil", "beast Set.", "END", "Set was a formidable foe.",
			"No mortal has even conquered", "their own fear, much less", "entered mortal battle and", "taken his life.",
			"END", "You've made it halfway toward", "fullfilling your destiny.", "The Kilmaat are growing",
			"restless with your progress.", "Seek out a temple in this", "citadel where I will provide",
			"more information", "END", "The Kilmaat race has", "continued their monsterous",
			"animal-human experiments in", "an effort to solve the key of", "animating my corpse. The",
			"victory defeating Set didn't", "slow you down as much as", "they had planned. They are",
			"actively robbing a slave", "girl of her life to create", "another monsterous",
			"abomination, combining human", "and insect intent on slaying", "you.  Prepare yourself for",
			"battle as she will be waiting", "for you at the Luxor Temple. ", "END", "You've done well to defeat",
			"Selkis. You have distracted", "the Kilmaat with your", "presence and they have", "temporarily abandoned",
			"animation of my corpse.", "The alien Queen Kilmaatikhan", "has a personal vendetta",
			"against you. Arrogance is", "her weakness, and if you can", "defeat Kilmaatikhan, the",
			"battle will be won.", "END", "The Kilmaat have been", "destroyed. Unfortunately,", "your recklessness has",
			"destroyed the earth and all", "of its inhabitants.  All that", "remains is a smoldering hunk", "of rock.",
			"END", "The Kilmaat have been", "defeated and you single", "handedly saved the Earth", "from destruction.",
			"", "", "", "Your bravery and heroism", "are legendary.", "END", "ITEMS", "Life Blood", "Life", "Venom",
			"You're losing your grip", "Full Life", "Invincibility", "Invisibility", "Torch", "Sobek Mask",
			"Increased weapon power!", "The Map!", "An extra life!", ".357 Magnum!", "Grenade", "M-60",
			"Flame Thrower!", "Cobra Staff!", "The Eye of Rah Gauntlet!", "Speed Loader", "Ammo", "Fuel", "Cobra!",
			"Raw Energy", "Power Key", "Time Key", "War Key", "Earth Key", "Magic", "Location Preserved", "COPYRIGHT",
			"Lobotomy Software, Inc.", "3D engine by 3D Realms", "", "LASTLEVEL", "INCOMING MESSAGE", "",
			"OUR LATEST SCANS SHOW", "THAT THE ALIEN CRAFT IS", "POWERING UP, APPARENTLY", "IN AN EFFORT TO LEAVE.",
			"THE BAD NEWS IS THAT THEY", "SEEM TO HAVE LEFT A DEVICE", "BEHIND, AND ALL EVIDENCE",
			"SAYS ITS GOING TO BLOW A", "BIG HOLE IN OUR FINE PLANET.", "A SQUAD IS TRYING TO DISMANTLE",
			"IT RIGHT NOW, BUT NO LUCK SO", "FAR, AND TIME IS RUNNING OUT.", "", "GET ABOARD THAT CRAFT NOW",
			"BEFORE IT LEAVES, THEN FIND", "AND SHOOT ALL THE ENERGY", "TOWERS TO GAIN ACCESS TO THE",
			"CONTROL ROOM. THERE YOU NEED TO", "TAKE OUT THE CONTROL PANELS AND", "THE CENTRAL POWER SOURCE.  THIS",
			"IS THE BIG ONE BUDDY, BEST OF", "LUCK... FOR ALL OF US.", "", "", "CREDITS", "Exhumed", "",
			"Executive Producers", " ", "Brian McNeely", "Paul Lange", "", "Game Concept", " ", "Paul Lange", "",
			"Game Design", " ", "Brian McNeely", "", "Additional Design", " ", "Paul Knutzen", "Paul Lange",
			"John Van Deusen", "Kurt Pfeifer", "Dominick Meissner", "Dane Emerson", "", "Game Programming", " ",
			"Kurt Pfeifer", "John Yuill", "", "Additional Programming", " ", "Paul Haugerud", "",
			"Additional Technical Support", " ", "John Yuill", "Paul Haugerud", "Jeff Blazier", "", "Level Design",
			" ", "Paul Knutzen", "", "Additional Levels", " ", "Brian McNeely", "", "Monsters and Weapons ", " ",
			"John Van Deusen", "", "Artists", " ", "Brian McNeely", "Paul Knutzen", "John Van Deusen", "Troy Jacobson",
			"Kevin Chung", "Eric Klokstad", "Richard Nichols", "Joe Kresoja", "Jason Wiggin", "",
			"Music and Sound Effects", " ", "Scott Branston", "", "Product Testing", " ", "Dominick Meissner",
			"Tom Kristensen", "Jason Wiggin", "Mark Coates", "", "Instruction Manual", " ", "Tom Kristensen", "",
			"Special Thanks", " ", "Jacqui Lyons", "Marjacq Micro, Ltd.", "Mike Brown", "Ian Mathias", "Cheryl Luschei",
			"3D Realms", "Kenneth Silverman", "Greg Malone", "Miles Design", "REDMOND AM/PM MINI MART",
			"7-11 Double Gulp", "", "Thanks for playing", "", "The end", "", "Guess youre stuck here",
			"until the song ends", "", "Maybe this is a good", "time to think about all", "the things you can do",
			"after the music is over.", "", "Or you could just stare", "at this screen", "",
			"and watch these messages", "go by...", "", "...and wonder just how long", "we will drag this out...", "",
			"and believe me, we can drag", "it out for quite a while.", "", "should be over soon...", "",
			"any moment now...", "", "", "", "See ya", "", "END", "PASSWORDS", "HOLLY", "KIMBERLY", "LOBOCOP",
			"LOBODEITY", "LOBOLITE", "LOBOPICK", "LOBOSLIP", "LOBOSNAKE", "LOBOSPHERE", "LOBOSWAG", "LOBOXY", "",
			"PASSINFO", "", "HI SWEETIE, I LOVE YOU", "", "", "SNAKE CAM ENABLED", "FLASHES TOGGLED", "FULL MAP",
			"", "", "", "", "", "EOF", };

	public static short nItemAltSeq, nItemFrame, nItemFrames;
	public static short[] nItemSeqOffset = { 91, 72, 76, 79, 68, 87, 83 };
	public static int nItemSeq;
	public static short nItemMagic[] = { 500, 1000, 100, 500, 400, 200, 700, 0 };

	public static boolean bInDemo = false;
	public static boolean bPlayback = false;

//	public static int screensize;
	public static int airframe;

	public static short initsect;
	public static int inita;
	public static int initx, inity, initz;

	public static int lastlevel;
	public static int levelnew;
	public static int levelnum;
	public static int nBestLevel;
	public static int totalmoves;
	public static int moveframes;
	public static int flash;

	public static int nMagicSeq;
	public static int nPreMagicSeq;
	public static int nSavePointSeq;

	public static boolean bSnakeCam;
	public static int nSnakeCam = -1;
	public static boolean bMapMode;

	public static int nShadowPic;

	public static int nShadowWidth;
	public static int nFlameHeight;
	public static int nBackgroundPic;

	public static int nPilotLightBase;
	public static int nPilotLightCount;
	public static int nPilotLightFrame;
	public static int nFontFirstChar;

	public static int nHealthFrames;
	public static int nMagicFrames;
	public static int nStatusSeqOffset;
	public static int nHealthFrame;
	public static int nMagicFrame;
	public static int nHealthLevel;
	public static int nMagicLevel;
	public static int nMeterRange;
	public static int magicperline;
	public static int healthperline;
	public static int nAirFrames;
	public static int airperline;
	public static int nCounter;
	public static int nCounterDest;

	public static int nCreepyTimer;
	public static final int nCreepyTime = 180; //6sec * 30tics

	public static int nCounterBullet;

	public static boolean bNoCreatures;
	public static int nFreeCount;
	public static byte nBubblesFree[] = new byte[200];
	public static BubbleStruct[] BubbleList = new BubbleStruct[200];

	public static int nDrips;
	public static DripStruct sDrip[] = new DripStruct[50];

	public static int nTrails;
	public static int nTrailPoints;
	public static TrailStruct sTrail[] = new TrailStruct[40];
	public static TrailPointStruct sTrailPoint[] = new TrailPointStruct[100];
	public static short nTrailPointPrev[] = new short[100];
	public static short nTrailPointNext[] = new short[100];
	public static short nTrailPointVal[] = new short[100];

	public static int nEnergyChan;
	public static int nEnergyBlocks;
	public static int nEnergyTowers;
	public static int nFinaleSpr;

	public static int lCountDown;
	public static int nAlarmTicks;
	public static int nRedTicks;
	public static int nClockVal;
	public static int nButtonColor;

	public static int nDronePitch;
	public static int nFinaleStage;
	public static int lFinaleStart;
	public static int nSmokeSparks;

	public static boolean bCamera;

	public static int nLastAnim;
	public static int nFirstAnim;
	public static int nAnimsFree_X_1;

	public static int message_timer;
	public static String message_text;

	public static int nNetTime = -1;

	public static int nBobs;
	public static BobStruct sBob[] = new BobStruct[200];
	public static short sBobID[] = new short[200];

	public static int ObjectCount;
	public static ObjectStruct ObjectList[] = new ObjectStruct[128];
	public static short ObjectSeq[] = { 46, -1, 72, -1 };
	public static short ObjectStatnum[] = { 141, 152, 98, 97 };

	public static ItemAnimStruct nItemAnimInfo[] = { new ItemAnimStruct(-1, 32), new ItemAnimStruct(-1, 32),
			new ItemAnimStruct(-1, 32), new ItemAnimStruct(-1, 32), new ItemAnimStruct(-1, 32),
			new ItemAnimStruct(6, 64), new ItemAnimStruct(-1, 48), new ItemAnimStruct(0, 64), new ItemAnimStruct(1, 64),
			new ItemAnimStruct(-1, 32), new ItemAnimStruct(4, 64), new ItemAnimStruct(5, 64),
			new ItemAnimStruct(16, 64), new ItemAnimStruct(10, 64), new ItemAnimStruct(-1, 32),
			new ItemAnimStruct(8, 64), new ItemAnimStruct(9, 64), new ItemAnimStruct(-1, 40),
			new ItemAnimStruct(-1, 32), new ItemAnimStruct(7, 64), new ItemAnimStruct(-1, 32),
			new ItemAnimStruct(-1, 32), new ItemAnimStruct(-1, 32), new ItemAnimStruct(-1, 32),
			new ItemAnimStruct(-1, 32), new ItemAnimStruct(-1, 32), new ItemAnimStruct(-1, 32),
			new ItemAnimStruct(-1, 32), new ItemAnimStruct(-1, 32), new ItemAnimStruct(-1, 32),
			new ItemAnimStruct(-1, 32), new ItemAnimStruct(14, 64), new ItemAnimStruct(15, 32),
			new ItemAnimStruct(-1, 32), new ItemAnimStruct(-1, 32), new ItemAnimStruct(-1, 32),
			new ItemAnimStruct(-1, 32), new ItemAnimStruct(-1, 32), new ItemAnimStruct(-1, 32),
			new ItemAnimStruct(17, 48), new ItemAnimStruct(18, 48), new ItemAnimStruct(19, 48),
			new ItemAnimStruct(20, 48), new ItemAnimStruct(24, 64), new ItemAnimStruct(21, 64),
			new ItemAnimStruct(23, 32), new ItemAnimStruct(-1, 32), new ItemAnimStruct(-1, 32),
			new ItemAnimStruct(-1, 32), new ItemAnimStruct(-1, 32), new ItemAnimStruct(-1, 32),
			new ItemAnimStruct(-1, 32), new ItemAnimStruct(-1, 32), new ItemAnimStruct(11, 30),
			new ItemAnimStruct(-1, 32), new ItemAnimStruct(-1, 32), new ItemAnimStruct(-1, 32),
			new ItemAnimStruct(-1, 32), new ItemAnimStruct(-1, 32), new ItemAnimStruct(-1, 32) };

	public static byte[] sMoveDir = new byte[50];
	public static MoveSectStruct[] sMoveSect = new MoveSectStruct[50];
	public static int nMoveSects;

	public static boolean isOriginal()
	{
		Screen scr = game.getScreen();
		boolean isOriginal = true;
		if(scr instanceof GameScreen)
			isOriginal = ((GameScreen) scr).isOriginal();

		return isOriginal;
	}
}
