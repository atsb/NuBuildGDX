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

import static ru.m210projects.Blood.Actor.*;
import static ru.m210projects.Blood.EVENT.*;
import static ru.m210projects.Blood.Gameutils.*;
import static ru.m210projects.Blood.Globals.*;
import static ru.m210projects.Blood.LEVELS.*;
import static ru.m210projects.Blood.Main.*;
import static ru.m210projects.Blood.LOADSAVE.*;
import static ru.m210projects.Blood.Mirror.*;
import static ru.m210projects.Blood.PLAYER.*;
import static ru.m210projects.Blood.SECTORFX.*;
import static ru.m210projects.Blood.SOUND.*;
import static ru.m210projects.Blood.Screen.*;
import static ru.m210projects.Blood.Trigger.*;
import static ru.m210projects.Blood.Types.DudeInfo.dudeInfo;
import static ru.m210projects.Blood.Types.GAMEINFO.*;
import static ru.m210projects.Blood.Types.Seq.SeqHandling.*;
import static ru.m210projects.Blood.View.*;
import static ru.m210projects.Blood.Warp.*;
import static ru.m210projects.Blood.Weapon.*;
import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Build.Gameutils.*;
import static ru.m210projects.Build.Strhandler.toLowerCase;
import static ru.m210projects.Build.FileHandle.Compat.*;
import static ru.m210projects.Build.Net.Mmulti.*;
import static ru.m210projects.Blood.Types.DEMO.IsOriginalDemo;

import java.util.Arrays;

import com.badlogic.gdx.ScreenAdapter;

import ru.m210projects.Blood.Types.XSECTOR;
import ru.m210projects.Blood.Types.XSPRITE;
import ru.m210projects.Blood.Types.XWALL;
import ru.m210projects.Blood.Types.ZONE;
import ru.m210projects.Build.CRC32;
import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.FileHandle.FileResource.Mode;
import ru.m210projects.Build.FileHandle.DataResource;
import ru.m210projects.Build.FileHandle.Resource;
import ru.m210projects.Build.FileHandle.Resource.Whence;
import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Build.Types.LittleEndian;
import ru.m210projects.Build.Types.SECTOR;
import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Build.Types.WALL;
import static ru.m210projects.Build.Net.Mmulti.connecthead;
import static ru.m210projects.Build.Net.Mmulti.connectpoint2;
import static ru.m210projects.Build.OnSceenDisplay.Console.*;

public class DB {

	public static final String kBloodMapExt	= "MAP";
	public static final String kBloodMapSig	=	"BLM\032";
	public static final int kBloodMapVersion6 =	0x0600;
	public static final int kBloodMapVersion7 =	0x0700;
	public static final int kMajorVersionMask =	0xFF00;
	public static final int kMinorVersionMask =	0x00FF;
	public static final int kFreeHead		=	0;

	public static final byte[] crypt = { 'M', 'a', 't', 't' };
	public static final int key = LittleEndian.getInt(crypt);

	/**
	 * Maximum number of {@link XSPRITE}s.
	 */
	public static final int kMaxXSprites	=	2048;
	public static final int kMaxXWalls		=	512;
	public static final int kMaxXSectors	=	512;

	/**
	 * Array of all loaded XSPRITES. {@link SPRITE} can access its
	 * {@link XSPRITE} by using {@link SPRITE#extra} as an index to this array;
	 */
	public static XSPRITE[] xsprite = new XSPRITE[kMaxXSprites];
	public static XWALL[] xwall = new XWALL[kMaxXWalls];
	public static XSECTOR[] xsector = new XSECTOR[kMaxXSectors];

	public static int[] nextXSprite = new int[kMaxXSprites];
	public static int[] nextXWall = new int[kMaxXWalls];
	public static int[] nextXSector = new int[kMaxXSectors];

	public static final int kMaxBusyValue = 0x10000;
	public static final int kFluxMask	  =	0x0FFFF;

	public static int gMapRev;
	public static int gSongId;
	public static int gSkyCount;

	public static int gVisibility;

	public static final int kPathMarker			= 15;
	public static final int kDudeSpawn			= 18;
	public static final int kEarthQuake			= 19;

	public static final int kSwitchBase			= 20;
	public static final int	kSwitchToggle		= kSwitchBase;
	public static final int	kSwitchMomentary = 21;
	public static final int	kSwitchCombination = 22;
	public static final int	kSwitchPadlock = 23;
	public static final int	kSwitchMax = 24;

	//////////////////////////////////New types are here
	public static final int	kGDXTypeBase = 24;
	public static final int	kGDXCustomDudeSpawn = kGDXTypeBase;
	public static final int	kGDXRandomTX = 25;
	public static final int	kGDXSequentialTX = 26;
	public static final int	kGDXSeqSpawner = 27;
	public static final int	kGDXObjPropertiesChanger = 28;
	public static final int	kGDXObjPicnumChanger = 29;
	public static final int	kGDXObjSizeChanger = 31;
	public static final int	kGDXDudeTargetChanger = 33;
	public static final int	kGDXSectorFXChanger = 34;
	public static final int	kGDXObjDataChanger = 35;
	public static final int	kGDXSpriteDamager = 36;
	public static final int	kGDXObjDataAccumulator = 37;
	public static final int	kGDXEffectSpawner = 38;
	public static final int	kGDXWindGenerator = 39;
	///////////////////////////////// End of new types


	public static final int kDudeBase = 200;
	public static final int kDudeTommyCultist = 201;
	public static final int kDudeShotgunCultist = 202;
	public static final int kDudeAxeZombie = 203;
	public static final int kDudeButcher = 204;
	public static final int kDudeEarthZombie = 205;
	public static final int kDudeFleshGargoyle = 206;
	public static final int kDudeStoneGargoyle = 207;
	public static final int kDudeFleshStatue = 208;
	public static final int kDudeStoneStatue = 209;
	public static final int kDudePhantasm = 210;
	public static final int kDudeHound = 211;
	public static final int kDudeHand = 212;
	public static final int kDudeBrownSpider = 213;
	public static final int kDudeRedSpider = 214;
	public static final int kDudeBlackSpider = 215;

	public static final int kDudeMotherSpider = 216;
	public static final int kDudeGillBeast = 217;
	public static final int kDudeEel = 218;
	public static final int kDudeBat = 219;
	public static final int kDudeRat = 220;
	public static final int kDudeGreenPod = 221;
	public static final int kDudeGreenTentacle = 222;
	public static final int kDudeFirePod = 223;
	public static final int kDudeFireTentacle = 224;
	public static final int kDudeMotherPod = 225;
	public static final int kDudeMotherTentacle = 226;
	public static final int kDudeCerberus = 227;
	public static final int kDudeCerberus2 = 228;
	public static final int kDudeTchernobog	= 229;
	public static final int kDudeFanaticProne =	230;
	public static final int kDudePlayer1 = 231;
	public static final int kDudePlayer8 = 238;
	public static final int kDudeBurning = 239;
	public static final int kDudeCultistBurning	 =	240;
	public static final int kDudeAxeZombieBurning =	241;
	public static final int kDudeBloatedButcherBurning = 242;
	//<Reserved>			243
	public static final int kDudeSleepZombie = 244;
	public static final int kDudeInnocent = 245;
	public static final int kDudeCultistProne = 246;
	public static final int kDudeTeslaCultist = 247;
	public static final int kDudeDynamiteCultist = 248;
	public static final int kDudeBeastCultist = 249;
	public static final int kDudeTinyCaleb = 250;
	public static final int kDudeTheBeast = 251;
	public static final int kDudeTinyCalebburning =	252;
	public static final int kDudeTheBeastburning =	253;

	public static final int kGDXDudeUniversalCultist = 254;
	public static final int kGDXGenDudeBurning = 255;

	public static final int kDudeMax = 256;


	public static final String[] kDudeName = {
		"kDudeBase",
		"Tommy Cultist",
		"Shotgun Cultist",
		"Axe Zombie",
		"Butcher",
		"Earth Zombie",
		"Flesh Gargoyle",
		"Stone Gargoyle",
		"Flesh Statue",
		"Stone Statue",
		"Phantasm",
		"Hound",
		"Hand",
		"Brown Spider",
		"Red Spider",
		"Black Spider",
		"Mother Spider",
		"Gill Beast",
		"Eel",
		"Bat",
		"Rat",
		"Green Pod",
		"Green Tentacle",
		"Fire Pod",
		"Fire Tentacle",
		"Mother Pod",
		"Mother Tentacle",
		"Cerberus",
		"Cerberus",
		"Tchernobo",
		"Fanatic Prone",
		"Player1",
		"Player2",
		"Player3",
		"Player4",
		"Player5",
		"Player6",
		"Player7",
		"Player8",
		"Burning",
		"Cultist Burning",
		"Axe Zombie Burning",
		"Bloated Butcher Burning",
		"<Reserved>",
		"Sleep Zombie",
		"Innocent",
		"CultistProne",
		"Tesla Cultist",
		"Dynamite Cultist",
		"Beast Cultist",
		"Tiny Caleb",
		"The Beast",
		"Tiny Calebburning",
		"The Beastburning",
		"GDX Custom dude",
		"GDX Custom burning dude",
		"kDudeMax"
	};

	public static final int kMissileBase = 300;
	public static final int kMissileButcherKnife = kMissileBase;
	public static final int kMissileFlare = 301;
	public static final int kMissileAltTesla = 302;
	public static final int kMissileStarburstFlare = 303;
	public static final int kMissileSprayFlame = 304;
	public static final int kMissileFireball = 305;
	public static final int kMissileTesla = 306;
	public static final int kMissileEctoSkull = 307;
	public static final int kMissileHoundFire = 308;
	public static final int kMissileGreenPuke = 309;
	public static final int kMissileRedPuke = 310; //nTile = 0
	public static final int kMissileBone = 311;
	public static final int kMissileNapalm = 312;
	public static final int kMissileTchernobog = 313;
	public static final int kMissileTchernobog2 = 314;
	public static final int kMissileLifeLeech = 315;
	public static final int kMissileAltLeech1 = 316;
	public static final int kMissileAltLeech2 = 317;
	public static final int kMissileMax = 318;

	public static short[] nStatSize = new short[kMaxStatus+1];

	public static final int kThingBase = 400;
	public static final int kThingTNTBarrel = kThingBase;
	public static final int kThingTNTProx = 401;
	public static final int kThingTNTRem = 402;
	public static final int kThingBlueVase = 403;
	public static final int kThingBrownVase = 404;
	public static final int kThingCrateFace = 405;
	public static final int kThingClearGlass = 406;
	public static final int kThingFluorescent = 407;
	public static final int kThingWallCrack = 408;
	public static final int kThingWoodBeam = 409;
	public static final int kThingWeb = 410;
	public static final int kThingMetalGrate1 = 411;
	public static final int kThingFlammableTree = 412;
	public static final int kThingMachineGun = 413;
	public static final int kThingFallingRock = 414;
	public static final int kThingPail = 415;
	public static final int kThingGibObject = 416;
	public static final int kThingExplodeObject = 417;
	public static final int kThingTNTStick = 418;
	public static final int kThingTNTBundle = 419;
	public static final int kThingSprayBundle = 420;
	public static final int kThingBoneClub = 421;
	//422 kThingZombieBones
	public static final int kThingWaterDrip = 423;
	public static final int kThingBloodDrip = 424;
	public static final int kThingGibSmall = 425;
	public static final int kThingGib = 426;
	public static final int kThingZombieHead = 427;
	public static final int kThingAltNapalm = 428;
	public static final int kThingPodFire = 429;
	public static final int kThingPodGreen = 430;
	public static final int kThingLifeLeech = 431;
	//432
	public static final int kGDXThingTNTProx = 433; // detects only players
	public static final int kGDXThingThrowableRock = 434; // does small damage if hits target
	public static final int kGDXThingCustomDudeLifeLeech = 435; // the same as normal, except it aims in specified target
	public static final int kThingMax = 436;


	public static final int kThingFlameTrap = 452;
	public static final int kTrapSawBlade = 454;
	public static final int kTrapPoweredZap = 456;
	public static final int kThingHiddenExploder = 459;





	public static final int kWallGlass = 511;


	public static final int kGenTrigger = 700;
	public static final int kGenWaterDrip = 701;
	public static final int kGenBloodDrip = 702;
	public static final int kGenFireball = 703;
	public static final int kGenEctoSkull = 704;
	public static final int kGenDart = 705;
	public static final int kGenBubble = 706;
	public static final int kGenMultiBubble = 707;
	public static final int kGenSFX = 708;
	public static final int kGenSectorSFX = 709;
	public static final int kGenAmbientSFX = 710;
	public static final int kGenPlayerSFX = 711;

	public static final int kSectorBase = 600;
	public static final int kSectorZMotion = kSectorBase;
	public static final int kSectorZSprite = 602;
	public static final int kSectorWarp = 603;
	public static final int kSectorTeleport = 604;

		//605
		//606
		//607
		//608
		//609
		//610
		//611

	public static final int kSectorPath = 612;
	public static final int kSectorRotateStep = 613;
	public static final int kSectorSlideMarked = 614;
	public static final int kSectorRotateMarked = 615;
	public static final int kSectorSlide = 616;
	public static final int kSectorRotate = 617;
	public static final int kSectorDamage = 618;
	public static final int kSectorCounter = 619;
	public static final int kSectorMax = 620;

	public static final int kWeaponItemBase			= 40; // also random weapon

	public static final int kWeaponLifeLeech		= 50;
	public static final int kWeaponItemMax			= 51;

	public static final int kAmmoItemBase			= 60;

	public static final int kAmmoItemRandom			= 80; // random ammo
	public static final int kAmmoItemMax			= 81;




	public static final int 	kItemBase = 100;
	public static final int		kItemKey1 = kItemBase;
	public static final int		kItemKey2 = 101;
	public static final int		kItemKey3 = 102;
	public static final int		kItemKey4 = 103;
	public static final int		kItemKey5 = 104;
	public static final int		kItemKey6 = 105;
	public static final int		kItemKey7 = 106;
	public static final int 	kItemDoctorBag = 107;
	public static final int 	kItemMedPouch = 108;
	public static final int 	kItemLifeEssence = 109;
	public static final int		kItemLifeSeed = 110;
	public static final int		kItemPotion1 = 111;
	public static final int 	kItemFeatherFall = 112;
	public static final int 	kItemLtdInvisibility = 113;
	public static final int 	kItemInvulnerability = 114;
	public static final int 	kItemJumpBoots = 115;
	public static final int 	kItemRavenFlight = 116;
	public static final int 	kItemGunsAkimbo = 117;
	public static final int 	kItemDivingSuit = 118;
	public static final int 	kItemGasMask = 119;
	public static final int 	kItemClone = 120;
	public static final int		kItemCrystalBall = 121;
	public static final int 	kItemDecoy = 122;
	public static final int 	kItemDoppleganger = 123;
	public static final int 	kItemReflectiveShots = 124;
	public static final int 	kItemBeastVision = 125;
	public static final int 	kItemShadowCloak = 126;
	public static final int 	kItemShroomRage = 127;
	public static final int 	kItemShroomDelirium = 128;
	public static final int 	kItemShroomGrow = 129;
	public static final int 	kItemShroomShrink = 130;
	public static final int 	kItemDeathMask = 131;
	public static final int 	kItemWineGoblet = 132;
	public static final int 	kItemWineBottle = 133;
	public static final int 	kItemSkullGrail = 134;
	public static final int 	kItemSilverGrail = 135;
	public static final int 	kItemTome = 136;
	public static final int 	kItemBlackChest = 137;
	public static final int 	kItemWoodenChest = 138;
	public static final int 	kItemAsbestosArmor = 139;

	public static final int 	kArmorItemBase = 140;
	public static final int 	kItemBasicArmor = kArmorItemBase;
	public static final int 	kItemBodyArmor = 141;
	public static final int 	kItemFireArmor = 142;
	public static final int 	kItemSpiritArmor = 143;
	public static final int 	kItemSuperArmor = 144;

	public static final int 	kItemBlueTeamBase = 145;
	public static final int 	kItemRedTeamBase = 146;
	public static final int 	kItemBlueFlag = 147;
	public static final int 	kItemRedFlag = 148;
	public static final int 	kItemJetpack = 149;
	//public static final int		kGDXItemMapLevel = 150; // NoOne: kItemMax > 149 causes weird crash when starting multiplayer game! must be fixed!
	public static final int		kItemMax = 149;

	public static final int kMaxItemTypes	= (kItemMax - kItemBase);
	public static final int kMaxPowerUps	= (kItemMax - kItemBase);

	public static final int	kRespawnNever		= 0;	// sprite cannot respawn
	public static final int	kRespawnOptional	= 1;	// sprite can optionally respawn in respawnTime seconds
	public static final int	kRespawnAlways		= 2;	// sprite always respawns in respawnTime seconds
	public static final int	kRespawnPermanent	= 3;	// sprite is permanent (respawnTime ignored)

	public static final String[] gItemText = { //kItemMax - kItemBase
		"Skull Key",
		"Eye Key",
		"Fire Key",
		"Dagger Key",
		"Spider Key",
		"Moon Key",
		"Key 7",
		"Doctor's Bag",
		"Medicine Pouch",
		"Life Essence",
		"Life Seed",
		"Red Potion",
		"Feather Fall",
		"Limited Invisibility",
		"INVULNERABILITY",
		"Boots of Jumping",
		"Raven Flight",
		"Guns Akimbo",
		"Diving Suit",
		"Gas mask",
		"Clone",
		"Crystal Ball",
		"Decoy",
		"Doppleganger",
		"Reflective shots",
		"Beast Vision",
		"ShadowCloak",
		"Rage shroom",
		"Delirium Shroom",
		"Grow shroom",
		"Shrink shroom",
		"Death mask",
		"Wine Goblet",
		"Wine Bottle",
		"Skull Grail",
		"Silver Grail",
		"Tome",
		"Black Chest",
		"Wooden Chest",
		"Asbestos Armor",
		"Basic Armor",
		"Body Armor",
		"Fire Armor",
		"Spirit Armor",
		"Super Armor",
		"Blue Team Base",
		"Red Team Base",
		"Blue Flag",
		"Red Flag",
		"Level map",
	};
	public static final String[] gWeaponText = {
		"RANDOM",	// kWeaponItemRandom
		"Sawed-off",		// kWeaponItemShotgun
		"Tommy Gun",		// kWeaponItemTommyGun
		"Flare Pistol",		// kWeaponItemFlareGun
		"Voodoo Doll",	// kWeaponItemVoodooDoll
		"Tesla Cannon",		// kWeaponItem
		"Napalm Launcher",	// kWeaponItem
		"Pitchfork",	// kWeaponItemPitchfork
		"Spray Can",		// kWeaponItemSprayCan
		"Dynamite",			// kWeaponItemTNT
		"Life Leech"
	};
	public static final String[] gAmmoText = {
		"Spray can",
		"Bundle of TNT*",
		"Bundle of TNT",
		"Case of TNT",
		"Proximity Detonator",
		"Remote Detonator",
		"Trapped Soul",
		"4 shotgun shells",
		"Box of shotgun shells",
		"A few bullets",
		"Voodoo Doll",
		"OBSOLETE",
		"Full drum of bullets",
		"Tesla Charge",
		"OBSOLETE",
		"OBSOLETE",
		"Flares",
		"OBSOLETE",
		"OBSOLETE",
		"Gasoline Can",
	};

	private static final short[] gHealthInfo = new short[kMaxPlayers];
	private static final PLAYER[] gPlayerInfo = new PLAYER[kMaxPlayers];

	public static final short kMaxProximitySprites = kMaxXSprites / 16;
	public static final short kMaxSightSprites = kMaxXSprites / 16;

	//// by NoOne: additional arrays for proximity and sight flag
	public static short[] gProxySpritesList = new short[kMaxProximitySprites];
	public static short gProxySpritesCount;
	public static byte gBadProxSprites;
	public static byte gMaxBadProxySprites;


	public static short[] gSightSpritesList = new short[kMaxSightSprites];
	public static short gSightSpritesCount;
	public static byte gBadSightSprites;
	public static byte gMaxBadSightSprites;
	////

	public static boolean prepareboard(final ScreenAdapter screen) {
		try
		{
			Console.Println("debug: start prepareboard()", OSDTEXT_BLUE);

			scrReset();
			PaletteView = kPalNormal;
			scrSetPalette(PaletteView);
			gViewMode = kView3D;
			gViewPos = 0;
			resetQuotes();
			gNoEnemies = 0;

			sndStopAllSounds();
			seqKillAll();

			if ((pGameInfo.uGameFlags & EndOfLevel) != 0) {
				for (int j = connecthead; j >= 0; j = connectpoint2[j]) {
					if (gPlayerInfo[j] == null)
						gPlayerInfo[j] = new PLAYER();
					gPlayerInfo[j].copy(gPlayer[j]);
					gPlayerInfo[j].pSprite = gPlayer[j].pSprite;
					gHealthInfo[j] = (short) gPlayer[j].pXsprite.health;
				}
			}

			for (int i = 0; i < kMaxXSprites; i++)
				if (xsprite[i] != null)
					xsprite[i].free();
			for (int i = 0; i < kMaxSprites; i++)
				if (sprite[i] != null)
					sprite[i].reset((byte) 0);

			Arrays.fill(sprXVel, 0);
			Arrays.fill(sprYVel, 0);
			Arrays.fill(sprZVel, 0);

			int[] 	posx = new int[1],
					posy = new int[1],
					posz = new int[1];
			short[] ang = new short[1],
					cursectnum = new short[1];

			dbLoadMap(pGameInfo.zLevelName, posx, posy, posz, ang, cursectnum);

			if (kGameCrash) {
				game.EndGame();
				return false;
			}

			sRandom(pGameInfo.uMapCRC);
			automapping = 1;

			levelResetKills();
			levelResetSecrets();

			///////
			for (short i = 0; i < kMaxProximitySprites; i++) gProxySpritesList[i] = -1;
			for (short i = 0; i < kMaxSightSprites; i++) gSightSpritesList[i] = -1;

			gProxySpritesCount = 0;		gBadProxSprites = 0;
			gSightSpritesCount = 0;		gBadSightSprites = 0;
			//////

			for (int i = 0; i < kMaxSprites; i++) {
				SPRITE pSprite = sprite[i];
				if (pSprite.statnum != kStatFree && pSprite.extra > 0) {
					XSPRITE pXSprite = xsprite[pSprite.extra];

					if (((1 << pGameInfo.nEnemyQuantity) & pXSprite.lSkill) != 0 || pXSprite.lS && pGameInfo.nGameType == 0
							|| pXSprite.lB && pGameInfo.nGameType == 2 || pXSprite.lT && pGameInfo.nGameType == 3
							|| pXSprite.lC && pGameInfo.nGameType == 1) {
								deletesprite((short) i);
								continue;
					}

					if (!IsOriginalDemo()) {

						// by NoOne: add statnum for faster dude searching
						if (sprite[i].lotag == kGDXDudeTargetChanger)
							InsertSpriteStat((short) i, (short) kStatGDXDudeTargetChanger);

						// by NoOne: make Proximity and Sight flag work not just for dudes and things...
						if (pXSprite.Proximity && gProxySpritesCount < kMaxProximitySprites) {
							switch (pSprite.statnum) {
								// exceptions
								case kStatThing: // already treated in their methods
								case kStatDude: // already treated in their methods
									if (pXSprite.Sight && pXSprite.DudeLockout) pXSprite.Proximity = false;
									break;
								case kStatEffect:
								case kStatExplosion:
								case kStatItem:
								case kStatPurge:
								case kStatMissile:
								case kStatSpares:
								case kStatFlare:
								case kStatInactive:
								case kStatFree:
								case kStatMarker:
								case kStatMarker2:
									break;
								default:
									if (pXSprite.Sight && pXSprite.DudeLockout) pXSprite.Proximity = false;
									else {

										gProxySpritesList[gProxySpritesCount++] = pSprite.xvel;
										gMaxBadProxySprites = (byte) (gProxySpritesCount >> 2);
										if (gMaxBadProxySprites <= 0) gMaxBadProxySprites = 1;
										if (gProxySpritesCount == kMaxProximitySprites) {
											String msg = "Max ("+kMaxProximitySprites+") additional Proximity sprites reached!\n";
											msg+="Please change your trigger system or stick with thing types ("+kThingBase+" - "+(kThingMax-1)+"), because\n";
											msg+="additional non-thing sprites with this flag only after this limit will not trigger anything.";

											game.GameWarning(msg);
										}

									}

									break;
							}
						}

						if (pXSprite.Sight && gSightSpritesCount < kMaxSightSprites) {
							switch (pSprite.statnum) {
								// exceptions
								case kStatEffect:
								case kStatExplosion:
								case kStatItem:
								case kStatPurge:
								case kStatMissile:
								case kStatSpares:
								case kStatFlare:
								case kStatInactive:
								case kStatFree:
								case kStatMarker:
								case kStatMarker2:
									break;
								default:
									gSightSpritesList[gSightSpritesCount++] = pSprite.xvel;
									gMaxBadSightSprites = (byte) (gSightSpritesCount >> 2);
									if (gMaxBadSightSprites <= 0) gMaxBadSightSprites = 1;
									if (gSightSpritesCount == kMaxSightSprites)
										game.GameWarning("Max ("+kMaxSightSprites+") Sight sprites reached!");
									break;
							}
						}
					}
				}
			}

			scrLoadPLUs();
			int z = posz[0];
			if (cursectnum[0] != -1)
				z = engine.getflorzofslope(cursectnum[0], posx[0], posy[0]);

			for (int i = 0; i < kMaxPlayers; i++) {
				gStartZone[i] = new ZONE();
				gStartZone[i].x = posx[0];
				gStartZone[i].y = posy[0];
				gStartZone[i].z = z;
				gStartZone[i].sector = cursectnum[0];
				gStartZone[i].angle = ang[0];

				if (i <= kMaxPlayers / 2) {
					gStartZoneTeam1[i] = new ZONE();
					gStartZoneTeam1[i].x = posx[0];
					gStartZoneTeam1[i].y = posy[0];
					gStartZoneTeam1[i].z = z;
					gStartZoneTeam1[i].sector = cursectnum[0];
					gStartZoneTeam1[i].angle = ang[0];

					gStartZoneTeam2[i] = new ZONE();
					gStartZoneTeam2[i].x = posx[0];
					gStartZoneTeam2[i].y = posy[0];
					gStartZoneTeam2[i].z = z;
					gStartZoneTeam2[i].sector = cursectnum[0];
					gStartZoneTeam2[i].angle = ang[0];
				}
			}

			InitSectorFX();
			InitPlayerStartZones();
			actInit(false, screen == gDemoScreen || cfg.gVanilla);

			// initialize all the tag buckets
			evInit(screen == gDemoScreen || cfg.gVanilla); //activate original PriorityEvent


			if(pGameInfo.nFragLimit != 0) {
				Arrays.fill(nTeamCount, 0);
				Arrays.fill(nTeamClock, 0);
			}

			for (int j = connecthead; j >= 0; j = connectpoint2[j]) {
				if ((pGameInfo.uGameFlags & EndOfLevel) == 0 || pGameInfo.nGameType > kNetModeCoop) {
					if (numplayers == 1) {
						game.net.gProfile[j].autoaim = cfg.gAutoAim;
						game.net.gProfile[j].slopetilt = cfg.gSlopeTilt;
						game.net.gProfile[j].skill = (byte) pGameInfo.nDifficulty;
					}
					playerInit(j, false);
				}
				playerReset(j);

				if(pGameInfo.nFragLimit != 0) {
					gPlayer[j].fragCount = 0;
					Arrays.fill(gPlayer[j].fragInfo, 0);
				}
			}

			if(kFakeMultiplayer) {
				for(int i = 1; i < nFakePlayers; i++) { //reset all other fake players
					if ((pGameInfo.uGameFlags & EndOfLevel) == 0 || pGameInfo.nGameType > kNetModeCoop)
						playerInit(i, false);
					playerReset(i);
				}
			}

			if ((pGameInfo.uGameFlags & EndOfLevel) != 0) {
				for (int j = connecthead; j >= 0; j = connectpoint2[j]) {
					PLAYER pPlayer = gPlayer[j];
					pPlayer.pXsprite.health = gHealthInfo[j];
					pPlayer.pWeaponQAV = gPlayerInfo[j].pWeaponQAV;
					pPlayer.currentWeapon = gPlayerInfo[j].currentWeapon;
					pPlayer.weaponState = gPlayerInfo[j].weaponState;
					pPlayer.weaponAmmo = gPlayerInfo[j].weaponAmmo;
					pPlayer.weaponCallback = gPlayerInfo[j].weaponCallback;
					pPlayer.fLoopQAV = gPlayerInfo[j].fLoopQAV;
					pPlayer.weaponTimer = gPlayerInfo[j].weaponTimer;
					pPlayer.updateWeapon = gPlayerInfo[j].updateWeapon;
					pPlayer.LastWeapon = gPlayerInfo[j].LastWeapon;

					for (int i = 0; i < kMaxPowerUps; i++) {
						int nPowerUp = i + kItemBase;
						if (cfg.gVanilla || (nPowerUp != kItemInvulnerability
								&& nPowerUp != kItemGasMask
								&& nPowerUp != kItemAsbestosArmor
								&& nPowerUp != kItemFeatherFall
								&& nPowerUp != kItemLtdInvisibility
								&& nPowerUp != kItemRavenFlight
								&& nPowerUp != kItemClone && nPowerUp != kItemDecoy
								&& nPowerUp != kItemDoppleganger
								&& nPowerUp != kItemReflectiveShots
								&& nPowerUp != kItemShadowCloak
								&& nPowerUp != kItemShroomRage
								&& nPowerUp != kItemShroomDelirium
								&& nPowerUp != kItemShroomGrow
								&& nPowerUp != kItemShroomShrink
								&& nPowerUp != kItemDeathMask
								&& nPowerUp != kItemGunsAkimbo))
							pPlayer.powerUpTimer[i] = gPlayerInfo[j].powerUpTimer[i];
					}

					if (!cfg.gVanilla && pPlayer.currentWeapon == kWeaponShotgun) { //shotgun akimbo disable
						if (gInfiniteAmmo || (pPlayer.ammoCount[2] > 1))
							pPlayer.weaponState = 3;
						else
							pPlayer.weaponState = 2;
					}

					if(pGameInfo.nGameType == 0 && pGameInfo.nPitchforkOnly)
					{
						for(int i = 0; i < 14; i++)
							pPlayer.weaponMode[i] = 0;

						pPlayer.hasWeapon[1] = true;
						pPlayer.LastWeapon = 0;
						pPlayer.currentWeapon = 0;
						pPlayer.weaponCallback = -1;
						pPlayer.pInput.newWeapon = 1;
						for(int i = 0; i < 14; i++) {
							pPlayer.weaponOrder[0][i] = defaultOrder[i];
							pPlayer.weaponOrder[1][i] = defaultOrder[i];
						}
						for(int i = 0; i < 12; i++)
							pPlayer.ammoCount[i] = 0;

						pPlayer.weaponTimer = 0;
						pPlayer.weaponState = 0;
						pPlayer.pWeaponQAV = -1;
						pPlayer.fLoopQAV = false;
					}
				}
			}

			pGameInfo.uGameFlags &= ~(EndOfLevel | EndOfGame);

			if (SplitScreen)
				InsertPlayer();

			InitMirrors();
			BuildGdx.audio.getSound().setReverb(false, 0);

			gFrame = 0;
			gFrameClock = 0;
			if(screen != gDemoScreen)
				gViewIndex = myconnectindex;

			trInit();
			ambPrepare();
			sndPlayMusic();

			Console.Println("debug: end prepareboard()", OSDTEXT_BLUE);

			return !kGameCrash;
		} catch (Exception e) {
			game.ThrowError(" in prepareboard(): " + (e.getMessage() == null ? "" : e.getMessage()) + " \r\n", e);
		}

		return false;
	}

	public static void dbLoadMap(String mapname, int[] posx, int[] posy, int[] posz, short[] angle, short[] nSector) {

		Arrays.fill(show2dsector, (byte)0);
		Arrays.fill(show2dsprite, (byte)0);
		Arrays.fill(show2dwall, (byte)0);

		int index = toLowerCase(mapname).indexOf(".map");
		if(index == -1)
			mapname = mapname + ".map";

		Resource bb = BuildGdx.cache.open(mapname, 0);

		if ( bb == null ) {
			game.GameMessage("Error opening map file " + (Path.Game.getPath() + mapname));
			return;
		}

    	String signature = bb.readString(4);
		if(signature == null || !signature.equals(kBloodMapSig)) {
			game.GameMessage("Wrong signature! Perhaps not a Blood map or map file corrupted: " + signature + " != " + kBloodMapSig);
			return;
		}

		int version = bb.readShort();
		boolean crypted = false;

		if( (version & kMajorVersionMask) != kBloodMapVersion6)
		{
			if ( (version & kMajorVersionMask) == kBloodMapVersion7 )
		    	crypted = true;
		    else {
		    	game.GameMessage("Map file is wrong version 0x" + Integer.toHexString(version & kMajorVersionMask));
		    	return;
		    }
		}

		byte[] header = new byte[37];
		bb.read(header);
		if(crypted) decryptBuffer(header, header.length, key);

		posx[0] = LittleEndian.getInt(header, 0);
		posy[0] = LittleEndian.getInt(header, 4);
		posz[0]  = LittleEndian.getInt(header, 8);
		angle[0]  = LittleEndian.getShort(header, 12);
		nSector[0]  = LittleEndian.getShort(header, 14);

		pskybits = LittleEndian.getShort(header, 16);

		visibility = LittleEndian.getInt(header, 18);
		gVisibility = visibility;
		gSongId = LittleEndian.getInt(header, 22); //key
		parallaxtype = header[26];

		gMapRev = LittleEndian.getInt(header, 27);
		numsectors = LittleEndian.getShort(header, 31);
		numwalls = LittleEndian.getShort(header, 33);
		numsprites = LittleEndian.getShort(header, 35);

		if(numsectors >= kMaxSectors || numwalls >= kMaxWalls || numsprites >= kMaxSprites) {
			game.GameMessage("Error! Map limits of sectors, walls or sprites are overflow. Map version is wrong!");
			return;
		}

		if(numsectors == 0 || numwalls == 0) {
			game.GameMessage("Empty map! (Numsectors == 0 || Numwalls == 0) or map file is corrupt");
			return;
		}

		try {

			dbInit();

			byte[] buf = new byte[128];
			int xspr = 56, xwal = 24, xsec = 60;
			if (crypted) {
				bb.read(buf);
				decryptBuffer(buf, buf.length, numwalls);
				xspr = LittleEndian.getInt(buf, 64);
				xwal = LittleEndian.getInt(buf, 68);
				xsec = LittleEndian.getInt(buf, 72);
			}
//		String copyright = new String(buf, 0, 64);

			//76 - 128 bytes = zeros

			gSkyCount = 1 << pskybits;

			buf = new byte[gSkyCount * 2];
			bb.read(buf);
			if (crypted) decryptBuffer(buf, buf.length, buf.length);

			for (int a = 0; a < buf.length; a += 2)
				pskyoff[a / 2] = LittleEndian.getShort(buf, a);

			Arrays.fill(zeropskyoff, (short) 0);
			System.arraycopy(pskyoff, 0, zeropskyoff, 0, MAXPSKYTILES);

			long dec = gMapRev * SECTOR.sizeof;
			byte[] sectorReader = new byte[SECTOR.sizeof];

			autoTotalSecrets = 0;
			buf = new byte[xsec];
			for (int i = 0; i < numsectors; i++) {
				bb.read(sectorReader);
				if (crypted) decryptBuffer(sectorReader, sectorReader.length, dec);
				SECTOR sec = new SECTOR(sectorReader);
				sector[i] = sec;
				if (sec.extra > 0) {
					bb.read(buf);
					xsector[dbInsertXSector(i)].init(buf);
					xsector[sec.extra].reference = i;
					xsector[sec.extra].busy = xsector[sec.extra].state << 16;

					if (xsector[sec.extra].txID == kChannelSecret && xsector[sec.extra].command == kCommandNumbered)
						autoTotalSecrets++;
				}
			}

			dec |= key;
			byte[] wallReader = new byte[WALL.sizeof];
			buf = new byte[xwal];
			for (int i = 0; i < numwalls; i++) {
				bb.read(wallReader);
				if (crypted) decryptBuffer(wallReader, wallReader.length, dec);
				WALL wal = new WALL(wallReader);
				wall[i] = wal;
				if (wal.extra > 0) {
					bb.read(buf);
					xwall[dbInsertXWall(i)].init(buf);
					xwall[wall[i].extra].reference = i;
					xwall[wall[i].extra].busy = xwall[wall[i].extra].state << 16;

					if (xwall[wal.extra].txID == kChannelSecret && xwall[wal.extra].command == kCommandNumbered)
						autoTotalSecrets++;
				}
			}

			initspritelists();
			dec = gMapRev * SPRITE.sizeof | key;
			byte[] spriteReader = new byte[SPRITE.sizeof];
			buf = new byte[xspr];

			for (int i = 0; i < numsprites; i++) {
				RemoveSpriteStat(i);        // remove it from the free list

				bb.read(spriteReader);
				if (crypted) decryptBuffer(spriteReader, spriteReader.length, dec);
				sprite[i].buildSprite(new DataResource(spriteReader));
				if (!isValidSector(sprite[i].sectnum) || !isValidStat(sprite[i].statnum)) {
					Console.Println("ERROR: Sprite " + i + " has an invalid flags", OSDTEXT_RED);
					if (sprite[i].extra > 0) {
						bb.seek(xspr, Whence.Current);
					}
					continue;
				}

				// insert sprite on appropriate lists
				InsertSpriteSect((short) i, sprite[i].sectnum);
				InsertSpriteStat((short) i, sprite[i].statnum);
				sprite[i].xvel = (short) i;
				if (sprite[i].extra > 0) {
					bb.read(buf);
					int nXSprite = dbInsertXSprite(i);
					if (nXSprite <= 0) return;
					XSPRITE pXSprite = xsprite[nXSprite];
					pXSprite.init(buf);
					pXSprite.reference = i;
					pXSprite.busy = pXSprite.state << 16;

					if (!crypted) {
//			        v47 = (4 * *&xsprite[v42].dropMsg) >> 31;
//			        xsprite[v42].restState_interruptable_difficulty &= 0x7Fu;
//			        *&xsprite[v42].busyTime |= v47 << 31;
					}
					if (pXSprite.txID == kChannelSecret && pXSprite.command == kCommandNumbered) {
						if (((1 << pGameInfo.nEnemyQuantity) & pXSprite.lSkill) == 0
								&& (!pXSprite.lS && pGameInfo.nGameType == 0
								|| !pXSprite.lB && pGameInfo.nGameType == 2
								|| !pXSprite.lT && pGameInfo.nGameType == 3
								|| !pXSprite.lC && pGameInfo.nGameType == 1)) {
							autoTotalSecrets++;
						}
					}
				}

				if ((sprite[i].cstat & kSpriteRMask) == kSpriteRMask) {
					Console.Println("ERROR: Sprite " + i + " has an invalid rotate", OSDTEXT_YELLOW);
					sprite[i].cstat &= ~kSpriteRMask;
				}
			}

			int mappos = bb.position();
			pGameInfo.uMapCRC = bb.readInt() & 0xFFFFFFFFL;

			byte[] mapcrc = new byte[mappos];
			bb.seek(0, Whence.Set);
			bb.read(mapcrc);
			bb.close();
			if (pGameInfo.uMapCRC != CRC32.getChecksum(mapcrc)) {
				game.GameMessage("File does not match CRC");
				return;
			}

		} catch (Exception e) {
			StringBuilder sb = new StringBuilder();
			int lvl = 0;
			for (StackTraceElement element : e.getStackTrace()) {
				sb.append("\t").append(element.toString());
				sb.append("\r\n");
				if(++lvl == 2)
					break;
			}

			game.GameMessage("Map loading error! " + e.toString() + "\r\n\r\n" + sb.toString());
			return;
		}

		PropagateMarkerReferences();
	}

	public static void PropagateMarkerReferences()
	{
		int nSprite, j;
		int nSector, nXSector;

		for (nSprite = headspritestat[kStatMarker]; nSprite != -1; nSprite = j)
		{
			j = nextspritestat[nSprite];

			switch (sprite[nSprite].lotag)
			{
				case kMarkerWarpDest:
					nSector = sprite[nSprite].owner;
					if (nSector < 0 || nSector >= numsectors)
						break;
					nXSector = sector[nSector].extra;
					if (nXSector <= 0 || nXSector >= kMaxXSectors)
						break;

					xsector[nXSector].marker0 = nSprite;
					continue;

				case kMarkerOff:
					nSector = sprite[nSprite].owner;
					if (nSector < 0 || nSector >= numsectors)
						break;
					nXSector = sector[nSector].extra;
					if (nXSector <= 0 || nXSector >= kMaxXSectors)
						break;

					xsector[nXSector].marker0 = nSprite;
					continue;

				case kMarkerOn:
					nSector = sprite[nSprite].owner;
					if (nSector < 0 || nSector >= numsectors)
						break;
					nXSector = sector[nSector].extra;
					if (nXSector <= 0 || nXSector >= kMaxXSectors)
						break;

					xsector[nXSector].marker1 = nSprite;
					continue;

				case kMarkerAxis:
					nSector = sprite[nSprite].owner;
					if (nSector < 0 || nSector >= numsectors)
						break;
					nXSector = sector[nSector].extra;
					if (nXSector <= 0 || nXSector >= kMaxXSectors)
						break;

					xsector[nXSector].marker0 = nSprite;
					continue;
			}

			System.out.println("Deleting invalid marker sprite");
			deletesprite((short)nSprite);
		}
	}

	/**
	 * Initializes {@link XSPRITE} structure in {@link #xsprite} array and links
	 * it to a {@link SPRITE} with index {@code nSprite} in
	 * {@link ru.m210projects.Build.Engine#sprite}.
	 *
	 * @param nSprite
	 * @return index of {@link XSPRITE} freshly allocated in {@link #xsprite}
	 */
	public static int dbInsertXSprite( int nSprite )
	{
		int nXSprite = RemoveFree(nextXSprite);
		if(nXSprite <= 0) {
			if(!kGameCrash)
				game.GameMessage("Out of free XSprites");
			game.EndGame();
			return 0;
		}

		xsprite[nXSprite].free();
		sprite[nSprite].extra = (short) nXSprite;

		xsprite[nXSprite].reference = nSprite;

		return nXSprite;
	}

	public static void dbDeleteXSprite( int nXSprite )
	{
		if(xsprite[nXSprite].reference < 0)
			game.dassert("xsprite[nXSprite].reference >= 0");
		if(sprite[xsprite[nXSprite].reference].extra != nXSprite)
			game.dassert("sprite[xsprite[nXSprite].reference].extra == nXSprite");
		InsertFree( nextXSprite, nXSprite );

		// clear the references
		sprite[xsprite[nXSprite].reference].extra = -1;
		xsprite[nXSprite].reference = -1;
	}

	public static int dbInsertXWall( int nWall )
	{
		int nXWall = RemoveFree(nextXWall);
		if (nXWall == 0)
			System.err.println("Out of free XWalls");

		xwall[nXWall].free();
		wall[nWall].extra = (short) nXWall;
		xwall[nXWall].reference = nWall;

		return nXWall;
	}

	public static void dbDeleteXWall( int nXWall )
	{
		if(xwall[nXWall].reference < 0)
			game.dassert("xwall[nXWall].reference >= 0");
		InsertFree( nextXWall, nXWall );

		// clear the references
		wall[xwall[nXWall].reference].extra = -1;
		xwall[nXWall].reference = -1;
	}

	public static int dbInsertXSector( int nSector )
	{
		int nXSector = RemoveFree(nextXSector);
		if (nXSector == 0)
			System.err.println("Out of free XSectors");

		xsector[nXSector].free();
		sector[nSector].extra = (short) nXSector;
		xsector[nXSector].reference = nSector;

		return nXSector;
	}

	public static void dbDeleteXSector( int nXSector )
	{
		if(xsector[nXSector].reference < 0) game.dassert("xsector[nXSector].reference >= 0");
		InsertFree( nextXSector, nXSector );

		// clear the references
		sector[xsector[nXSector].reference].extra = -1;
		xsector[nXSector].reference = -1;	// clear the reference
	}

	public static void InsertFree(int[] next, int n )
	{
		next[n] = next[kFreeHead];
		next[kFreeHead] = n;
	}

	public static void InsertSpriteSect( short nSprite, short nSector )
	{
		if(!(nSprite >= 0 && nSprite < kMaxSprites))game.dassert("nSprite >= 0 && nSprite < kMaxSprites");
		if(!(nSector >= 0 && nSector < kMaxSectors)) game.dassert("nSector >= 0 && nSector < kMaxSectors");

		int nOldHead = headspritesect[nSector];

		if (nOldHead >= 0)
		{
			// insert sprite at the tail of the list
			prevspritesect[nSprite] = prevspritesect[nOldHead];
			nextspritesect[nSprite] = -1;
			nextspritesect[prevspritesect[nOldHead]] = nSprite;
			prevspritesect[nOldHead] = nSprite;
		}
		else
		{
			prevspritesect[nSprite] = nSprite;
			nextspritesect[nSprite] = -1;
			headspritesect[nSector] = nSprite;
		}
		sprite[nSprite].sectnum = nSector;
	}

	public static void InsertSpriteStat( short nSprite, short nStat )
	{
		if(!(nSprite >= 0 && nSprite < kMaxSprites))game.dassert("nSprite >= 0 && nSprite < kMaxSprites");
		if(!isValidStat(nStat)) game.dassert("nStat >= 0 && nStat <= kMaxStatus: " + nStat);

		int nHead = headspritestat[nStat];

		if (nHead >= 0)
		{
			nextspritestat[nSprite] = -1;
		    prevspritestat[nSprite] = prevspritestat[nHead];
		    nextspritestat[prevspritestat[nHead]] = nSprite;
		    prevspritestat[nHead] = nSprite;
		}
		else
		{
			prevspritestat[nSprite] = nSprite;
			nextspritestat[nSprite] = -1;
			headspritestat[nStat] = nSprite;
		}
		sprite[nSprite].statnum = nStat;
		nStatSize[nStat]++;
	}

	public static void RemoveSpriteSect( int nSprite )
	{
		if(!(nSprite >= 0 && nSprite < kMaxSprites))
			game.dassert("nSprite >= 0 && nSprite < kMaxSprites");
		int nSector = sprite[nSprite].sectnum;

		if(!(nSector >= 0 && nSector < kMaxSectors))
			game.dassert("nSector >= 0 && nSector < kMaxSectors");

		if ( nextspritesect[nSprite] >= 0 )
			prevspritesect[nextspritesect[nSprite]] = prevspritesect[nSprite];
		else //FIXME headspritesect[nSector] == -1;
		{
			if(nSector == -1)
			{
				Console.Println("RemoveSpriteSect() nSector == -1 crash: " + sprite[nSprite], OSDTEXT_RED);

				if(sprite[nSprite] != null) {
					SPRITE pSprite = sprite[nSprite];
					Console.LogPrint("pSprite " + pSprite);
					if(pSprite.extra > 0)
						Console.LogPrint("pSprite " + xsprite[pSprite.extra]);
				}
			} else
			if(headspritesect[nSector] == -1)
			{
				if(sprite[nSprite] != null) {
					SPRITE pSprite = sprite[nSprite];
					Console.LogPrint("pSprite " + pSprite);
					if(pSprite.extra > 0)
						Console.LogPrint("pSprite " + xsprite[pSprite.extra]);

					if(lastload != null && !lastload.isEmpty()) {
						Resource res = BuildGdx.compat.open(lastload, Path.User, Mode.Read);
						if(res != null) {
							Console.LogPrint("-savedata-\n");
							Console.LogData(res.getBytes());
							Console.LogPrint("\n-end savedata-\n");
							res.close();
						}
					}
				}
			}

			prevspritesect[headspritesect[nSector]] = prevspritesect[nSprite];
		}

		if ( headspritesect[nSector] != nSprite )
			nextspritesect[prevspritesect[nSprite]] = nextspritesect[nSprite];
		else
			headspritesect[nSector] = nextspritesect[nSprite];

		sprite[nSprite].sectnum = -1;
	}

	public static void RemoveSpriteStat( int nSprite )
	{
		if(!(nSprite >= 0 && nSprite < kMaxSprites))game.dassert("nSprite >= 0 && nSprite < kMaxSprites");
		int nStat = sprite[nSprite].statnum;
		if(!(nStat >= 0 && nStat <= kMaxStatus)) game.dassert("nStat >= 0 && nStat <= kMaxStatus");

		if ( nextspritestat[nSprite] >= 0 )
			prevspritestat[nextspritestat[nSprite]] = prevspritestat[nSprite];
		else
			prevspritestat[headspritestat[nStat]] = prevspritestat[nSprite];

		if (headspritestat[nStat] != nSprite )
			nextspritestat[prevspritestat[nSprite]] = nextspritestat[nSprite];
		else
			headspritestat[nStat] = nextspritestat[nSprite];

		sprite[nSprite].statnum = -1;
		nStatSize[nStat]--;
	}

	public static void dbInit()
	{
		int i;

		for(i = 0; i < kMaxPlayers; i++) {
			if(gStartZone[i] == null)
				gStartZone[i] = new ZONE();
			else gStartZone[i].clear();
		}

		for (i = 0; i < kMaxXSprites; i++) {
			if(xsprite[i] == null)
				xsprite[i] = new XSPRITE();
			else xsprite[i].free();
		}

		for (i = 0; i < kMaxXWalls; i++) {
			if(xwall[i] == null)
				xwall[i] = new XWALL();
			else xwall[i].free();
		}

		for (i = 0; i < kMaxXSectors; i++) {
			if(xsector[i] == null)
				xsector[i] = new XSECTOR();
			else xsector[i].free();
		}

		InitFreeList(nextXSprite, kMaxXSprites);
		for (i = 1; i < kMaxXSprites; i++)
			xsprite[i].reference = -1;

		InitFreeList(nextXWall, kMaxXWalls);
		for (i = 1; i < kMaxXWalls; i++)
			xwall[i].reference = -1;

		InitFreeList(nextXSector, kMaxXSectors);
		for (i = 1; i < kMaxXSectors; i++)
			xsector[i].reference = -1;


		initspritelists();

		// initialize default cstat for sprites
		for (i = 0; i < kMaxSprites; i++)
			sprite[i].cstat = kSpriteOriginAlign;
	}

	public static void InitFreeList(int[] xlist, int xlistSize )
	{
		for (int i = 1; i < xlistSize; i++)
			xlist[i] = (i - 1);
		xlist[kFreeHead] = (xlistSize - 1);
	}

	public static int RemoveFree(int[] next)
	{
		int n = next[kFreeHead];
		next[kFreeHead] = next[n];
		return n;
	}

	public static byte[] decryptBuffer(byte[] buffer, int size, long key) {
		for (int i = 0; i < size; i++)
			buffer[i] ^= key + i;

		return buffer;
	}

	public static void initspritelists()
	{
		short i;

	     //Init doubly-linked sprite sector lists
		for (i = 0; i <= kMaxSectors; i++)
			headspritesect[i] = -1;

		for (i = 0; i <= kMaxStatus; i++) {
			headspritestat[i] = -1;
			nStatSize[i] = 0;
		}

		for (i = 0; i < kMaxSprites; i++)
		{
			if(sprite[i] == null)
				sprite[i] = new SPRITE();
			else sprite[i].reset((byte)0);
			sprite[i].sectnum = -1;
			sprite[i].xvel = -1;
			InsertSpriteStat(i, (short) kStatFree);	// add to free list
		}
		spritesortcnt = 0;
	}

	public static void deletesprite( short nSprite )
	{
		if(sprite[nSprite].statnum == kStatFree && sprite[nSprite].sectnum == -1) {
			return; //already deleted
		}

		if ( sprite[nSprite].extra > 0 )
			dbDeleteXSprite(sprite[nSprite].extra);

		if(!(sprite[nSprite].statnum >= 0 && sprite[nSprite].statnum < kMaxStatus))
		{
			Console.Println("deletesprite() statnum game.dassert: " + sprite[nSprite], OSDTEXT_RED);
			game.dassert("sprite[nSprite].statnum >= 0 && sprite[nSprite].statnum < kMaxStatus");
		}
		RemoveSpriteStat(nSprite);

		if(!(sprite[nSprite].sectnum >= 0 && sprite[nSprite].sectnum < kMaxSectors))
			game.dassert("sprite[nSprite].sectnum >= 0 && sprite[nSprite].sectnum < kMaxSectors");
		RemoveSpriteSect(nSprite);

		InsertSpriteStat(nSprite, (short) kStatFree);
	}

	public static short insertsprite( short nSector, short nStatus )
	{
		short nSprite;
		if(numsprites >= kMaxSprites)
			game.dassert( "numsprites < kMaxSprites " );

		nSprite = headspritestat[kStatFree];

		if(nSprite < 0) {
			for (nSprite = headspritestat[kStatEffect]; nSprite == -1; nSprite = nextspritestat[nSprite]) {
				if ((sprite[nSprite].hitag & kAttrFree) == 0)
					break;
			}

			if(nSprite == -1) return -1;
			actDeleteEffect(nSprite);
		}

		if (nSprite >= 0)
		{
			RemoveSpriteStat(nSprite);
			sprite[nSprite].reset((byte)0);
			InsertSpriteStat(nSprite, nStatus);
			InsertSpriteSect(nSprite, nSector);

			sprite[nSprite].cstat = kSpriteOriginAlign;
			sprite[nSprite].clipdist = 32;
			sprite[nSprite].xrepeat = 64;
			sprite[nSprite].yrepeat = 64;
			sprite[nSprite].owner = -1;
			sprite[nSprite].extra = -1;
			sprite[nSprite].xvel = nSprite;

			sprXVel[nSprite] = 0;
		    sprYVel[nSprite] = 0;
		    sprZVel[nSprite] = 0;
		}

		return nSprite;
	}

	public static short setSprite(int spritenum, long newx, long newy, long newz)
	{
		short tempsectnum;

		sprite[spritenum].x = (int) newx;
		sprite[spritenum].y = (int) newy;
		sprite[spritenum].z = (int) newz;

		tempsectnum = engine.updatesector((int) newx,(int) newy,sprite[spritenum].sectnum);
		if (tempsectnum < 0)
			return(-1);
		if (tempsectnum != sprite[spritenum].sectnum)
			changespritesect((short) spritenum,tempsectnum);

		return(0);
	}

	public static short changespritesect( short nSprite, short nSector )
	{
		if(!(nSprite >= 0 && nSprite < kMaxSprites)) game.dassert("nSprite >= 0 && nSprite < kMaxSprites");
		if(!(nSector >= 0 && nSector < kMaxSectors)) game.dassert("nSector >= 0 && nSector < kMaxSectors");
		if(!(sprite[nSprite].statnum >= 0 && sprite[nSprite].statnum < kMaxStatus)) game.dassert("sprite[nSprite].statnum >= 0 && sprite[nSprite].statnum < kMaxStatus");
		if(!(sprite[nSprite].sectnum >= 0 && sprite[nSprite].sectnum < kMaxSectors))game.dassert("sprite[nSprite].sectnum >= 0 && sprite[nSprite].sectnum < kMaxSectors");

		// changing to same sector is a valid operation, and will put sprite at tail of list
		RemoveSpriteSect(nSprite);
		InsertSpriteSect(nSprite, nSector);
		return 0;
	}

	public static short changespritestat( short nSprite, short nStatus )
	{
		if(!(nSprite >= 0 && nSprite < kMaxSprites))game.dassert("nSprite >= 0 && nSprite < kMaxSprites");
		if(!(nStatus >= 0 && nStatus <= kMaxStatus)) game.dassert("nStatus >= 0 && nStatus <= kMaxStatus");
		if(!(sprite[nSprite].statnum >= 0 && sprite[nSprite].statnum < kMaxStatus)) game.dassert("sprite[nSprite].statnum >= 0 && sprite[nSprite].statnum < kMaxStatus");
		if(!(sprite[nSprite].sectnum >= 0 && sprite[nSprite].sectnum < kMaxSectors))game.dassert("sprite[nSprite].sectnum >= 0 && sprite[nSprite].sectnum < kMaxSectors");

		// changing to same status is a valid operation, and will put sprite at tail of list
		RemoveSpriteStat(nSprite);
		InsertSpriteStat(nSprite, nStatus);
		return 0;
	}

	public static void InsertPlayer() {
		if (numplayers < kMaxPlayers) {
			connectpoint2[numplayers - 1] = numplayers;
			connectpoint2[numplayers] = -1;
			game.pNet.gNetFifoHead[numplayers] = game.pNet.gNetFifoHead[0]; // HACK 01/05/2000
			playerInit(numplayers, false);
			playerReset(numplayers);
			gPlayer[numplayers].pDudeInfo = dudeInfo[gPlayer[numplayers].pSprite.lotag
					- kDudeBase];
			gPlayer[numplayers].pSprite = sprite[gPlayer[numplayers].nSprite];
			gPlayer[numplayers].pXsprite = xsprite[gPlayer[numplayers].pSprite.extra];
			gViewIndex = numplayers;
			gMe = gPlayer[gViewIndex];

			numplayers++;
		}
	}

	public static void DeletePlayer() {
		if (numplayers > 1) {
			numplayers--;
			connectpoint2[numplayers - 1] = -1;
			deletesprite((short) gPlayer[numplayers].nSprite);
			if (gViewIndex >= numplayers) {
				gViewIndex = 0;
				gMe = gPlayer[gViewIndex];
			}
		}
	}

}
