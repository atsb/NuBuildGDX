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
import static ru.m210projects.Blood.Types.DEMO.*;
import static ru.m210projects.Build.Gameutils.*;
import static ru.m210projects.Blood.DB.*;
import static ru.m210projects.Blood.EVENT.*;
import static ru.m210projects.Blood.Gameutils.*;
import static ru.m210projects.Blood.Gib.actGenerateGibs;
import static ru.m210projects.Blood.Gib.startPos;
import static ru.m210projects.Blood.Gib.startVel;
import static ru.m210projects.Blood.Globals.*;
import static ru.m210projects.Blood.Main.*;
import static ru.m210projects.Blood.SOUND.*;
import static ru.m210projects.Blood.Strings.seq;
import static ru.m210projects.Blood.Trig.Cos;
import static ru.m210projects.Blood.Trig.Sin;
import static ru.m210projects.Blood.Trigger.trTextOver;
import static ru.m210projects.Blood.Trigger.trTriggerSector;
import static ru.m210projects.Blood.Trigger.trTriggerSprite;
import static ru.m210projects.Blood.Trigger.trTriggerWall;
import static ru.m210projects.Blood.Types.DudeInfo.dudeInfo;
import static ru.m210projects.Blood.Types.DudeInfo.gPlayerTemplate;
import static ru.m210projects.Blood.Types.ScreenEffect.resetEffects;
import static ru.m210projects.Blood.Types.Seq.SeqHandling.*;
import static ru.m210projects.Blood.VERSION.getPlayerSeq;
import static ru.m210projects.Blood.VERSION.kPlayerBurn;
import static ru.m210projects.Blood.VERSION.kPlayerCrouch;
import static ru.m210projects.Blood.VERSION.kPlayerDead;
import static ru.m210projects.Blood.VERSION.kPlayerDying;
import static ru.m210projects.Blood.VERSION.kPlayerExplode;
import static ru.m210projects.Blood.VERSION.kPlayerFDying;
import static ru.m210projects.Blood.VERSION.kPlayerFSpirit;
import static ru.m210projects.Blood.VERSION.kPlayerFatality;
import static ru.m210projects.Blood.VERSION.kPlayerFatalityDead;
import static ru.m210projects.Blood.VERSION.kPlayerIdle;
import static ru.m210projects.Blood.VERSION.kPlayerSwim;
import static ru.m210projects.Blood.VERSION.kPlayerWalk;
import static ru.m210projects.Blood.View.*;
import static ru.m210projects.Blood.LOADSAVE.*;
import static ru.m210projects.Blood.Warp.gLowerLink;
import static ru.m210projects.Blood.Warp.gStartZone;

import static ru.m210projects.Blood.Warp.gStartZoneTeam1;
import static ru.m210projects.Blood.Warp.gStartZoneTeam2;
import static ru.m210projects.Blood.Warp.gTeamsSpawnUsed;

import static ru.m210projects.Blood.Weapon.WeaponLower;
import static ru.m210projects.Blood.Weapon.WeaponProcess;
import static ru.m210projects.Blood.Weapon.WeaponRaise;
import static ru.m210projects.Blood.Weapon.WeaponUpgrade;
import static ru.m210projects.Blood.Weapon.defaultOrder;
import static ru.m210projects.Blood.Weapon.kQAVEnd;
import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Build.Net.Mmulti.*;
import static ru.m210projects.Build.Pragmas.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

import ru.m210projects.Blood.Factory.BloodNetwork;
import ru.m210projects.Blood.Types.AMMOINFO;
import ru.m210projects.Blood.Types.AMMOITEMDATA;
import ru.m210projects.Blood.Types.ARMORITEMDATA;
import ru.m210projects.Blood.Types.CALLPROC;
import ru.m210projects.Blood.Types.DAMAGEINFO;
import ru.m210projects.Blood.Types.DudeInfo;
import ru.m210projects.Blood.Types.FragInfo;
import ru.m210projects.Blood.Types.INPUT;
import ru.m210projects.Blood.Types.INVITEM;
import ru.m210projects.Blood.Types.PLOCATION;
import ru.m210projects.Blood.Types.POSTURE;
import ru.m210projects.Blood.Types.QUOTE;
import ru.m210projects.Blood.Types.WEAPONITEMDATA;
import ru.m210projects.Blood.Types.XSECTOR;
import ru.m210projects.Blood.Types.XSPRITE;
import ru.m210projects.Blood.Types.XWALL;
import ru.m210projects.Blood.Types.ZONE;
import ru.m210projects.Blood.Types.Seq.SeqHandling;
import ru.m210projects.Blood.Types.Seq.SeqInst;
import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.FileHandle.Resource;
import ru.m210projects.Build.FileHandle.Compat.Path;
import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Build.Types.Tile;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Vector3;

public class PLAYER {
	public static PLOCATION[] gPrevView = new PLOCATION[kMaxPlayers];
	public static String PlayerName;

	public static final String deathMessage = "Press \"USE\" to load last saved game or press \"ENTER\" to restart level";
	public static final int kInventoryDoctorBag = 0;
	public static final int kInventoryDivingSuit = 1;
	public static final int kInventoryCrystalBall = 2;
	public static final int kInventoryBeastVision = 3;
	public static final int kInventoryJumpBoots = 4;
	public static final int kInventoryJetpack = 5;

	public static final int BLUEARMOR = 0;
	public static final int REDARMOR = 1;
	public static final int GREENARMOR = 2;

	public static final int kOrderMax = 2;

	public static final int		kWeaponNone			= 0;
	public static final int		kWeaponPitchfork	= 1;
	public static final int		kWeaponFlare		= 2;
	public static final int		kWeaponShotgun		= 3;
	public static final int		kWeaponTommy		= 4;
	public static final int		kWeaponNapalm		= 5;
	public static final int		kWeaponTNT			= 6;
	public static final int		kWeaponSprayCan		= 7;
	public static final int		kWeaponTesla		= 8;
	public static final int		kWeaponVoodoo		= 10;
	public static final int		kWeaponLifeLeach	= 9;
	public static final int		kWeaponProxyTNT		= 11;
	public static final int		kWeaponRemoteTNT	= 12;
	public static final int		kWeaponBeast		= 13;
	public static final int		kWeaponMax = 14;

	public static final int	kAmmoNone = -1;

	public static final int kLookMax = 60;
	public static final int kHorizUpMax		= 120;
	public static final int kHorizDownMax	= 180;
	public static final int kHorizDefault = 90;

	public static boolean newHoriz = true;
	public static final int newHorizDefault = 100;

	public static final int kMoveWalk = 0;
	public static final int kMoveSwim = 1;
	public static final int kMoveCrouch = 2;
	public static final int kMoveFly = 3;

	public static final int kModeHuman = 0;
	public static final int kModeBeast = 1;
	public static final int kModeHumanShrink = 2;
	public static final int kModeHumanGrown = 3;

	// distance in pixels for picking up items and pushing things
	public static final int kTouchXYDist	= 48;
	public static final int kTouchZDist		= 32;
	public static final int kPushXYDist		= 64;

	public static final int[] gLaughs = {
		734, 735, 736, 737, 738, 739, 740, 741, 3038, 3049
	};

	public static QAV[] weaponQAVs = new QAV[kQAVEnd];

	public static AMMOINFO[] gAmmoInfo = {
		new AMMOINFO( 0, 0, 255 ),
		new AMMOINFO( 100, 0, 255 ),
		new AMMOINFO( 100, 0, 4 ),
		new AMMOINFO( 500, 0, 5 ),
		new AMMOINFO( 100, 0, 255 ),
		new AMMOINFO( 50, 0, 255 ),
		new AMMOINFO( 2880, 0, 255 ),
		new AMMOINFO( 250, 0, 255 ),
		new AMMOINFO( 100, 0, 255 ),
		new AMMOINFO( 100, 0, 255 ),
		new AMMOINFO( 50, 0, 255 ),
		new AMMOINFO( 50, 0, 255 )
	};

	public static DAMAGEINFO[] gDamageInfo =
	{
		new DAMAGEINFO( -1, new int[] { 731, 732, 733, 710, 710, 710 }),
		new DAMAGEINFO( 1, new int[] { 742, 743, 744, 711, 711, 711 }),
		new DAMAGEINFO( 0, new int[] { 731, 732, 733, 712, 712, 712 }),
		new DAMAGEINFO( 1, new int[] { 731, 732, 733, 713, 713, 713 }),
		new DAMAGEINFO( -1, new int[] { 724, 724, 724, 714, 714, 714 }),
		new DAMAGEINFO( 2, new int[] { 731, 732, 733, 715, 715, 715 }),
		new DAMAGEINFO( 0, new int[] { 0, 0, 0, 0, 0, 0 })
	};

	//Player struct

	public SPRITE pSprite;
	public XSPRITE pXsprite;
	public DudeInfo pDudeInfo;
	public INPUT pInput;
	public int NPSTART;
	public int pWeaponQAV;
	public int weaponCallback;
	public boolean Run;
	public int moveState;
	public int moveDist;
	public int bobPhase;
	public int bobAmp;
	public int bobHeight;
	public int bobWidth;
	public int swayPhase;
	public int swayAmp;
	public int swayHeight;
	public int swayWidth;
	public int nPlayer;
	public int nSprite;
	public int nLifeMode;
	public int bloodlust;
	public int weapOffdZ;
	public int viewOffdZ;
	public int weaponAboveZ;
	public int viewOffZ;
	public float look;
	public float horiz;
	public int slope;
	public float horizOff;
	public boolean Underwater;
	public boolean[] hasKey = new boolean[8];
	public int hasFlag;
	public short nBlueTeam;
	public short nRedTeam;
	//field_95 12bytes
	public int[] damageShift = new int[7];

	public int CrouchMode; //GDX function
	public int LastWeapon; //GDX function
	public int currentWeapon;
	public int updateWeapon;
	public int weaponTimer;
	public int weaponState;
	public int weaponAmmo;
	public boolean[] hasWeapon = new boolean[kWeaponMax];
	public int[] weaponMode = new int[kWeaponMax];
	public int[][] weaponOrder = new int[kOrderMax][kWeaponMax];

	public int[] ammoCount = new int[12];
	public boolean fLoopQAV;
	public int fuseTime;
	public int fireClock;

	public int throwTime;
	public Vector3 aim;
	public Vector3 relAim;
	public int nAimSprite;
	public int aimCount;
	public int[] aimSprites = new int[16];

	public int deathTime;
	public int[] powerUpTimer = new int[kMaxPowerUps];
	public int fragCount;
	public int[] fragInfo = new int[kMaxPlayers];
	public int teamID;
	public int fraggerID;
	public int airTime; // set when first going underwater, decremented while underwater then takes damage
	public int bloodTime;	// set when player walks through blood, decremented when on normal surface
	public int gooTime;	// set when player walks through sewage, decremented when on normal surface
	public int wetTime;	// set when player walks through water, decremented when on normal surface
	public int bubbleTime; // set when first going underwater, decremented while underwater then takes damage

	public int kickTime;
	public int stayTime;
	public int pLaughsCount;
	public int TurnAround;

	public boolean godMode;
	public boolean fScreamed;
	public boolean pJump;
	public int showInventory;
	public int choosedInven;
	public static final int kInventoryMax = 6;
	public INVITEM[] Inventory = new INVITEM[kInventoryMax];
	public int[] ArmorAmount = new int[3];

	int voodooTarget;
	int voodooCount;
	int voodooAng;
	int voodooUnk;

	public int explosion;
	public int tilt;
	public int visibility;
	public int fireEffect;
	public int hitEffect;
	public int blindEffect;
	public int drownEffect;
	public int handCount;
	public boolean handDamage;
	public int pickupEffect;
	public int quakeTime;

	public int lookang;
	public int rotscrnang;

	public float ang;

	public PLAYER() {
		aim = new Vector3();
		relAim = new Vector3();
		pInput = new INPUT();
		for(int i = 0; i < kInventoryMax; i++)
			Inventory[i] = new INVITEM();
	}

	public void copy(PLAYER pPlayer) {

		pSprite = pPlayer.pSprite;
		pXsprite = pPlayer.pXsprite;
		pDudeInfo = pPlayer.pDudeInfo;

		pInput = pPlayer.pInput;
		ang = pPlayer.ang;

		pWeaponQAV = pPlayer.pWeaponQAV;
		weaponCallback = pPlayer.weaponCallback;
		Run = pPlayer.Run;
		moveState = pPlayer.moveState;

		bobPhase = pPlayer.bobPhase;
		bobAmp = pPlayer.bobAmp;
		bobHeight = pPlayer.bobHeight;
		bobWidth = pPlayer.bobWidth;
		swayPhase = pPlayer.swayPhase;
		swayAmp = pPlayer.swayAmp;
		swayHeight = pPlayer.swayHeight;
		swayWidth = pPlayer.swayWidth;
		nPlayer = pPlayer.nPlayer;
		nSprite = pPlayer.nSprite;
		nLifeMode = pPlayer.nLifeMode;
		bloodlust = pPlayer.bloodlust;

		weapOffdZ = pPlayer.weapOffdZ;
		viewOffdZ = pPlayer.viewOffdZ;
		weaponAboveZ = pPlayer.weaponAboveZ;
		viewOffZ = pPlayer.viewOffZ;

		look = pPlayer.look;
		horiz = pPlayer.horiz;
		slope = pPlayer.slope;
		horizOff = pPlayer.horizOff;
		Underwater = pPlayer.Underwater;

		hasFlag = pPlayer.hasFlag;
		nBlueTeam = pPlayer.nBlueTeam;
		nRedTeam = pPlayer.nRedTeam;

		System.arraycopy(pPlayer.hasKey, 0, hasKey, 0, 8);
		System.arraycopy(pPlayer.damageShift, 0, damageShift, 0, 7);

		CrouchMode = pPlayer.CrouchMode;
		LastWeapon = pPlayer.LastWeapon;
		currentWeapon = pPlayer.currentWeapon;
		updateWeapon = pPlayer.updateWeapon;
		weaponTimer = pPlayer.weaponTimer;
		weaponState = pPlayer.weaponState;
		weaponAmmo = pPlayer.weaponAmmo;
		for(int i = 0; i < 14; i++) {
			weaponMode[i] = pPlayer.weaponMode[i];
			hasWeapon[i] = pPlayer.hasWeapon[i];
			weaponOrder[0][i] = pPlayer.weaponOrder[0][i];
			weaponOrder[1][i] = pPlayer.weaponOrder[1][i];
		}
		System.arraycopy(pPlayer.ammoCount, 0, ammoCount, 0, 12);

		fLoopQAV = pPlayer.fLoopQAV;
		fuseTime = pPlayer.fuseTime;
		fireClock = pPlayer.fireClock;

		throwTime = pPlayer.throwTime;
		aim = pPlayer.aim;
		relAim = pPlayer.relAim;
		nAimSprite = pPlayer.nAimSprite;
		aimCount = pPlayer.aimCount;
		System.arraycopy(pPlayer.aimSprites, 0, aimSprites, 0, 16);

		deathTime = pPlayer.deathTime;
		System.arraycopy(pPlayer.powerUpTimer, 0, powerUpTimer, 0, kMaxPowerUps);
		fragCount = pPlayer.fragCount;
		System.arraycopy(pPlayer.fragInfo, 0, fragInfo, 0, kMaxPlayers);
		teamID = pPlayer.teamID;
		fraggerID = pPlayer.fraggerID;
		airTime = pPlayer.airTime; // set when first going underwater, decremented while underwater then takes damage
		bloodTime = pPlayer.bloodTime;	// set when player walks through blood, decremented when on normal surface
		gooTime = pPlayer.gooTime;	// set when player walks through sewage, decremented when on normal surface
		wetTime = pPlayer.wetTime;	// set when player walks through water, decremented when on normal surface
		bubbleTime = pPlayer.bubbleTime; // set when first going underwater, decremented while underwater then takes damage

		stayTime = pPlayer.stayTime;
		kickTime = pPlayer.kickTime;
		pLaughsCount = pPlayer.pLaughsCount;
		TurnAround = pPlayer.TurnAround;

		godMode = pPlayer.godMode;
		fScreamed = pPlayer.fScreamed;
		pJump = pPlayer.pJump;
		showInventory = pPlayer.showInventory;
		choosedInven = pPlayer.choosedInven;
		for( int i = 0; i < kInventoryMax; i++) {
			Inventory[i].activated = pPlayer.Inventory[i].activated;
			Inventory[i].amount = pPlayer.Inventory[i].amount;
		}
		System.arraycopy(pPlayer.ArmorAmount, 0, ArmorAmount, 0, 3);
		//voodoo
		voodooTarget = pPlayer.voodooTarget;
		voodooCount = pPlayer.voodooCount;
		voodooAng = pPlayer.voodooAng;
		voodooUnk = pPlayer.voodooUnk;

		explosion = pPlayer.explosion;
		tilt = pPlayer.tilt;
		visibility = pPlayer.visibility;
		fireEffect = pPlayer.fireEffect;
		hitEffect = pPlayer.hitEffect;
		blindEffect = pPlayer.blindEffect;
		drownEffect = pPlayer.drownEffect;
		handCount = pPlayer.handCount;
		handDamage = pPlayer.handDamage;
		pickupEffect = pPlayer.pickupEffect;
		quakeTime = pPlayer.quakeTime;

		lookang = pPlayer.lookang;
		rotscrnang = pPlayer.rotscrnang;
	}

	public void reset() {
		pSprite = null;
		pXsprite = null;
		pDudeInfo = null;

		pInput.Reset();
		ang = 0;

		pWeaponQAV = 0;
		weaponCallback = 0;
		Run = false;
		moveState = 0;

		bobPhase = 0;
		bobAmp = 0;
		bobHeight = 0;
		bobWidth = 0;
		swayPhase = 0;
		swayAmp = 0;
		swayHeight = 0;
		swayWidth = 0;
		nPlayer = 0;
		nSprite = 0;
		nLifeMode = 0;
		bloodlust = 0;

		weapOffdZ = 0;
		viewOffdZ = 0;
		weaponAboveZ = 0;
		viewOffZ = 0;

		look = 0;
		horiz = 0;
		slope = 0;
		horizOff = 0;
		Underwater = false;
		for (int i = 0; i < 8; i++)
			hasKey[i] = false;
		for (int i = 0; i < 7; i++)
			damageShift[i] = 0;

		CrouchMode = 0;
		LastWeapon = 0;
		currentWeapon = 0;
		updateWeapon = 0;
		weaponTimer = 0;
		weaponState = 0;
		weaponAmmo = 0;
		for(int i = 0; i < 14; i++) {
			weaponMode[i] = 0;
			hasWeapon[i] = false;
			weaponOrder[0][i] = 0;
			weaponOrder[1][i] = 0;
		}
		for(int i = 0; i < 12; i++)
			ammoCount[i] = 0;

		fLoopQAV = false;
		fuseTime = 0;
		fireClock = 0;

		throwTime = 0;
		aim.set(0, 0, 0);
		relAim.set(0, 0, 0);
		nAimSprite = 0;
		aimCount = 0;
		for(int i = 0; i < 16; i++)
			aimSprites[i] = 0;

		deathTime = 0;
		for(int i = 0; i < kMaxPowerUps; i++)
			powerUpTimer[i] = 0;
		fragCount = 0;
		for(int i = 0; i < kMaxPlayers; i++)
			fragInfo[i] = 0;
		teamID = 0;
		fraggerID = 0;
		airTime = 0; // set when first going underwater, decremented while underwater then takes damage
		bloodTime = 0;	// set when player walks through blood, decremented when on normal surface
		gooTime = 0;	// set when player walks through sewage, decremented when on normal surface
		wetTime = 0;	// set when player walks through water, decremented when on normal surface
		bubbleTime = 0; // set when first going underwater, decremented while underwater then takes damage

		kickTime = 0;
		stayTime = 0;
		pLaughsCount = 0;
		TurnAround = 0;

		godMode = false;
		fScreamed = false;
		pJump = false;
		showInventory = 0;
		choosedInven = 0;
		for( int i = 0; i < kInventoryMax; i++) {
			Inventory[i].activated = false;
			Inventory[i].amount = 0;
		}
		for( int i = 0; i < 3; i++)
			ArmorAmount[i] = 0;

		voodooTarget = 0;
		voodooCount = 0;
		voodooAng = 0;
		voodooUnk = 0;

		explosion = 0;
		tilt = 0;
		visibility = 0;
		fireEffect = 0;
		hitEffect = 0;
		blindEffect = 0;
		drownEffect = 0;
		handCount = 0;
		handDamage = false;
		pickupEffect = 0;
		quakeTime = 0;

		lookang = 0;
		rotscrnang = 0;
	}

	public static boolean checkPlayerSeq(PLAYER pPlayer, int nSeqID) {
		SeqInst pInst = SeqHandling.GetInstance(SS_SPRITE, pPlayer.pSprite.extra);
		return pInst.getSeqIndex() == (pPlayer.pDudeInfo.seqStartID + nSeqID) && seqFrame(SS_SPRITE, pPlayer.pSprite.extra) >= 0;
	}

	public static void resetInventory(PLAYER pPlayer)
	{
		if(pPlayer == null)game.dassert("pPlayer != NULL");
		for(int i = 0; i < 14; i++) {
			pPlayer.hasWeapon[i] = gInfiniteAmmo;
			pPlayer.weaponMode[i] = 0;
		}
		pPlayer.hasWeapon[1] = true;
		pPlayer.LastWeapon = 0;
		pPlayer.currentWeapon = 0;
		pPlayer.weaponCallback = -1;
		pPlayer.pInput.newWeapon = 1;

		for(int i = 0; i < 14; i++) {
			pPlayer.weaponOrder[0][i] = defaultOrder[i]; //kOrderAboveWater
			pPlayer.weaponOrder[1][i] = defaultOrder[i]; //kOrderBelowWater
		}

		for(int i = 0; i < 12; i++)
			pPlayer.ammoCount[i] = gInfiniteAmmo ? gAmmoInfo[i].max : 0;
		for( int i = 0; i < 3; i++)
			pPlayer.ArmorAmount[i] = 0;
		pPlayer.weaponTimer = 0;
		pPlayer.weaponState = 0;
		pPlayer.pWeaponQAV = -1;
		pPlayer.fLoopQAV = false;
		pPlayer.choosedInven = -1;
		for( int i = 0; i < kInventoryMax; i++) {
			 pPlayer.Inventory[i].activated = false;
			 pPlayer.Inventory[i].amount = 0;
		}
		gInfiniteAmmo = false;
	}

	public static void playerInit( int nPlayer, boolean nInited )
	{
		PLAYER pPlayer = gPlayer[nPlayer];

		if(!nInited) {
			System.out.println("Initializing player " + nPlayer);
			pPlayer.reset();
		}

		pPlayer.nPlayer = nPlayer;
		pPlayer.teamID = nPlayer;
		pPlayer.CrouchMode = 0;
		if ( pGameInfo.nGameType == kNetModeTeams ) {
			if(game.net.gProfile[nPlayer].team == 0)
				pPlayer.teamID = nPlayer & 1;
			else pPlayer.teamID = (game.net.gProfile[nPlayer].team - 1);
		}
		pPlayer.fragCount = 0;
		Arrays.fill(nTeamCount, 0);
		Arrays.fill(nTeamClock, 0);
		Arrays.fill(pPlayer.fragInfo, 0);
		if(!nInited)
			resetInventory(pPlayer);
	}

	public static void playerGodMode(PLAYER pPlayer, int mode)
	{
		if(mode == 1)
		{
			for(int i = 0; i < 7; i++)
				pPlayer.damageShift[i] += 1;
		}
		else
		{
			for(int i = 0; i < 7; i++)
				pPlayer.damageShift[i] -= 1;
		}
		pPlayer.godMode = mode == 1;
	}

	public static void playerReset( int nPlayer )
	{
		PLAYER pPlayer = gPlayer[nPlayer];

		// get the normal player starting position, else if in bloodbath mode,
		// randomly pick one of kMaxPlayers starting positions
		ZONE pZone = null;
		if (pGameInfo.nGameType == kNetModeOff || pGameInfo.nGameType == kNetModeCoop)
			pZone = gStartZone[ nPlayer ];

		// let's check if there is positions of teams is specified
		// if no, pick position randomly, just like it works in vanilla.
		else if (pGameInfo.nGameType == kNetModeTeams && gTeamsSpawnUsed) {
			int maxRetries = 5;
			while(maxRetries-- > 0) {
				//System.err.println("-> TRY #"+maxRetries+" SEARCHING START POINT FOR PLAYER: "+nPlayer+", TEAM: "+pPlayer.teamID);
				if (pPlayer.teamID == 0) pZone = gStartZoneTeam1[Random(kMaxPlayers / 2)];
				else pZone = gStartZoneTeam2[Random(kMaxPlayers / 2)];

				//System.err.println(pZone.x);
				//System.err.println(pZone.y);
				//System.err.println(pZone.z);
				//System.err.println(pZone.sector);

				if (maxRetries != 0) {
					// check if there is no spawned player in selected zone
					for (int i = headspritesect[pZone.sector]; i >= 0; i = nextspritesect[i]) {
						 SPRITE pSprite = sprite[i];
						 if (pZone.x == pSprite.x && pZone.y == pSprite.y && IsPlayerSprite(pSprite)){
							//System.err.println("FOUND PLAYER SPRITE ON START ZONE");
							pZone = null;
							break;
						 }
					}
				}

				if (pZone != null)
					break;
			}
		} else {
			pZone = gStartZone[ Random(kMaxPlayers) ];
		}

		if(pZone.sector == -1)
		{
			game.GameMessage("The player should be inside a map sector");
			return;
		}

		int nSprite = actSpawnSprite( pZone.sector, pZone.x, pZone.y, pZone.z, kStatDude, true );

		SPRITE pSprite = sprite[nSprite];
		if(!(pSprite.extra > 0 && pSprite.extra < kMaxXSprites)) game.dassert("pSprite.extra > 0 && pSprite.extra < kMaxXSprites");
		XSPRITE pXSprite = xsprite[pSprite.extra];

		pPlayer.pSprite = pSprite;
		pPlayer.pXsprite = pXSprite;
		pPlayer.nSprite = nSprite;
		pPlayer.pDudeInfo = dudeInfo[nPlayer + 31];

		playerSetRace( pPlayer, kModeHuman );
		seqSpawn(pPlayer.pDudeInfo.seqStartID + kSeqDudeIdle, SS_SPRITE, pSprite.extra, null);

		if ( pPlayer == gMe )
			show2dsprite[pSprite.xvel >> 3] |= 1 << (pSprite.xvel & 7);

		GetSpriteExtents( pSprite );

		pPlayer.CrouchMode = 0;
		pPlayer.LastWeapon = 0;

		if ( pGameInfo.nGameType == kNetModeTeams ) {
			if(game.net.gProfile[nPlayer].team == 0)
				pPlayer.teamID = nPlayer & 1;
			else pPlayer.teamID = (game.net.gProfile[nPlayer].team - 1);
		}

		pPlayer.pXsprite.data1 = 0; // player SEQ size scale

		pSprite.z -= extents_zBot - pSprite.z;
		pSprite.pal = (byte) ((pPlayer.teamID & 3) + 11);

		pPlayer.ang = pSprite.ang = (short) (pZone.angle & kAngleMask);
		pSprite.lotag = (short) (nPlayer + kDudePlayer1);
		pSprite.clipdist = pPlayer.pDudeInfo.clipdist;
		pSprite.hitag = (kAttrMove | kAttrGravity | kAttrFalling | kAttrAiming);
		pXSprite.burnTime = 0;
		pXSprite.burnSource = -1;
		pXSprite.health = pPlayer.pDudeInfo.startHealth << 4;
		pPlayer.pSprite.cstat &= 0x7FFF;
		pPlayer.bloodlust = 0;
		pPlayer.horiz = 0;
		pPlayer.slope = 0;
		pPlayer.look = 0;
		pPlayer.horizOff = 0;
		pPlayer.fraggerID = -1;
		pPlayer.airTime = 1200;
		pPlayer.bloodTime = 0;
		pPlayer.gooTime = 0;
		pPlayer.wetTime = 0;
		pPlayer.bubbleTime = 0;
//		pPlayer.unkTime = 0;
		pPlayer.stayTime = 0;
		pPlayer.kickTime = 0;
		pPlayer.pLaughsCount = 0;
		pPlayer.TurnAround = 0;
		pPlayer.moveState = kMoveWalk;
		pPlayer.voodooTarget = -1;
		pPlayer.voodooCount = 0;
		pPlayer.voodooAng = 0;
		pPlayer.voodooUnk = 0;
		viewUpdatePlayerLoc(pPlayer);
		pPlayer.weapOffdZ = 0;
		pPlayer.relAim.x = 1 << 14;
		pPlayer.relAim.y = 0;
		pPlayer.relAim.z = 0;
		pPlayer.nAimSprite = -1;
		pPlayer.viewOffdZ = pPlayer.weapOffdZ;

		if(pGameInfo.nGameType != kNetModeCoop) {
			for (int i = 0; i < 8; i++)
				pPlayer.hasKey[i] = (pGameInfo.nGameType >= 2);
		}

		pPlayer.hasFlag = 0;
		pPlayer.nBlueTeam = 0;
		pPlayer.nRedTeam = 0;
		for (int i = 0; i < 7; i++)
			pPlayer.damageShift[i] = 0;
		if ( pPlayer.godMode )
			playerGodMode(pPlayer, 1);

		for( int i = 0; i < kInventoryMax; i++)
			 pPlayer.Inventory[i].activated = false;

		gFullMap = false;
		pPlayer.throwTime = 0;
		pPlayer.deathTime = 0;
		pPlayer.updateWeapon = 0;

		sprXVel[pSprite.xvel] = 0;
		sprYVel[pSprite.xvel] = 0;
		sprZVel[pSprite.xvel] = 0;

		pPlayer.pInput.Turn = 0;
		pPlayer.pInput.Jump = false;
		pPlayer.pInput.Crouch = false;
		pPlayer.pInput.Shoot = false;
		pPlayer.pInput.AltShoot = false;
		pPlayer.pInput.Lookup = false;
		pPlayer.pInput.Lookdown = false;
		pPlayer.pInput.TurnAround = false;
		pPlayer.pInput.Use = false;
		pPlayer.pInput.InventoryLeft = false;
		pPlayer.pInput.InventoryRight = false;
		pPlayer.pInput.InventoryUse = false;
		pPlayer.pInput.PrevWeapon = false;
		pPlayer.pInput.NextWeapon = false;
		pPlayer.pInput.HolsterWeapon = false;
		pPlayer.pInput.LookCenter = false;
		pPlayer.pInput.Pause = false;
		pPlayer.pInput.Quit = false;
		pPlayer.pInput.Restart = false;
		pPlayer.pInput.Forward = 0;
		pPlayer.pInput.Strafe = 0;
		pPlayer.pInput.mlook = 0;
		pPlayer.explosion = 0;
		pPlayer.quakeTime = 0;
		pPlayer.tilt = 0;
		pPlayer.visibility = 0;
		pPlayer.hitEffect = 0;
		pPlayer.blindEffect = 0;
		pPlayer.drownEffect = 0;
		pPlayer.handCount = 0;
		pPlayer.weaponTimer = 0;
		pPlayer.weaponState = 0;
		pPlayer.pWeaponQAV = -1;
		pPlayer.handDamage = false;
		resetEffects();

		pPlayer.lookang = 0;
		pPlayer.rotscrnang = 0;

		for(int i = 0; i < kMaxPowerUps; i++)
			pPlayer.powerUpTimer[i] = 0;

		resetPlayerSize(pPlayer);

		if ( pPlayer == gMe ) {
			((BloodNetwork) game.pNet).PredictReset();
		}

		if ( IsUnderwaterSector(pSprite.sectnum) )
		{
		    pPlayer.moveState = kMoveSwim;
		    pPlayer.pXsprite.palette = 1;
		}
	}

	public static void playerSetRace( PLAYER pPlayer, int nLifeMode ) {
		if(!(nLifeMode >= kModeHuman && nLifeMode <= kModeHumanGrown))
			game.dassert("nLifeMode >= kModeHuman && nLifeMode <= kModeHumanGrown" );
		pPlayer.nLifeMode = nLifeMode;
		pPlayer.pDudeInfo.copy(gPlayerTemplate[nLifeMode]);
		for(int i = 0; i < 7; i++)
			pPlayer.pDudeInfo.damageShift[i] = mulscale(pPlayer.pDudeInfo.startDamage[i], pPlayerShift[pGameInfo.nDifficulty], 8);
	}

	public static boolean PickupLeech(PLAYER pPlayer, SPRITE pSprite)
	{
		for (int nSprite = headspritestat[kStatThing]; nSprite >= 0; nSprite = nextspritestat[nSprite]) {
			if(pSprite == null || pSprite.xvel != nSprite)
			{
				SPRITE pThing = sprite[nSprite];
			    if ( pThing.lotag == kThingLifeLeech && actGetBurnSource(pThing.owner) == pPlayer.nSprite )
			    	return true;
			}
		}
		return false;
	}

	public static int Action_nIndex = 0;
	public static int Action_nXIndex = 0;
	public static int ActionScan( PLAYER pPlayer )
	{
		SPRITE pSprite = pPlayer.pSprite;

		Action_nIndex = 0;
		Action_nXIndex = 0;

		int dx = Cos(pSprite.ang) >> 16;
		int dy = Sin(pSprite.ang) >> 16;
		int dz = (int) pPlayer.horizOff;

		int hitType = HitScan(pSprite, pPlayer.viewOffZ, dx, dy, dz, pHitInfo, 0x10000040, 128);
		int hitDist = (int) (engine.qdist(pSprite.x - pHitInfo.hitx, pSprite.y - pHitInfo.hity) >> 4);

		if(hitDist < kPushXYDist) {
			switch ( hitType )
			{
				case SS_SPRITE:
					Action_nIndex = pHitInfo.hitsprite;
					Action_nXIndex = sprite[Action_nIndex].extra;
					if (Action_nXIndex > 0
						&& sprite[Action_nIndex].statnum == 4
						&& sprite[Action_nIndex].lotag == kThingLifeLeech)
					{
						if ( pGameInfo.nGameType > 1 && PickupLeech(pPlayer, sprite[Action_nIndex]) )
							return -1;

						XSPRITE pXThing = xsprite[Action_nXIndex];
						pXThing.data4 = pPlayer.nPlayer;
						pXThing.isTriggered = false;
					}


					if (Action_nXIndex > 0 && xsprite[Action_nXIndex].Push )
						return SS_SPRITE;

					if ( sprite[Action_nIndex].statnum == kStatDude )
					{
						if(pGameInfo.nReviveMode && pGameInfo.nGameType == kNetModeCoop && IsPlayerSprite(sprite[Action_nIndex]))
						{
							PLAYER pReviving = gPlayer[sprite[Action_nIndex].lotag - kDudePlayer1];
							if(pReviving.deathTime > 0)
							{
								seqSpawn(pReviving.pDudeInfo.seqStartID + getPlayerSeq(kPlayerFatalityDead), SS_SPRITE, pReviving.pSprite.extra, callbacks[ReviveCallback]);
								break;
							}
						}

						XSPRITE pXDude = xsprite[Action_nXIndex];
//						dprintf("DUDE %d: ang=%d, goalAng=%d\n", Action_nIndex, pDude.ang, pXDude.goalAng);
						int mass = dudeInfo[sprite[Action_nIndex].lotag - kDudeBase].mass;
						if( mass > 0) {
							int impulse = 0xCCCCC00 / mass;
			    			sprXVel[Action_nIndex] += mulscale(impulse, dx, 16);
			    			sprYVel[Action_nIndex] += mulscale(impulse, dy, 16);
			    			sprZVel[Action_nIndex] += mulscale(impulse, dz, 16);
						}
			            if ( xsprite[Action_nXIndex].Push && xsprite[Action_nXIndex].state == 0 && !xsprite[Action_nXIndex].isTriggered )
			            	trTriggerSprite(Action_nIndex, pXDude, kCommandSpritePush);
					}
					break;

				case SS_MASKED:
				case SS_WALL:
					Action_nIndex = pHitInfo.hitwall;
					Action_nXIndex = wall[Action_nIndex].extra;
					if (Action_nXIndex > 0 && xwall[Action_nXIndex].triggerPush )
						return SS_WALL;

					if ( wall[Action_nIndex].nextsector >= 0)
					{
						Action_nIndex = wall[Action_nIndex].nextsector;
						Action_nXIndex = sector[Action_nIndex].extra;
						if (Action_nXIndex > 0 && xsector[Action_nXIndex].Wallpush )
							return SS_SECTOR;
					}
					break;

				case SS_FLOOR:
				case SS_CEILING:
					Action_nIndex = pHitInfo.hitsect;
					Action_nXIndex = sector[Action_nIndex].extra;
					if (Action_nXIndex > 0 && xsector[Action_nXIndex].Push )
						return SS_SECTOR;
					break;
			}
		}

		Action_nIndex = pSprite.sectnum;
		Action_nXIndex = sector[Action_nIndex].extra;
		if ( Action_nXIndex > 0 && xsector[Action_nXIndex].Push )
			return SS_SECTOR;

		return -1;
	}

	public static void ProcessInput( PLAYER pPlayer )
	{
		long vel;
		SPRITE pSprite = pPlayer.pSprite;
		POSTURE cp = gPosture[pPlayer.nLifeMode][pPlayer.moveState];
		int nSprite = pPlayer.nSprite;

		if(pSprite.extra == -1) //disconnected?
			return;

		XSPRITE pXSprite = pPlayer.pXsprite;
		pPlayer.Run = pPlayer.pInput.Run;



		if ( !gNoClip && !IsOriginalDemo() )
		{
			short nSector = pSprite.sectnum;
		    GetSpriteExtents(pSprite);
		    int floorDist = (extents_zBot - pSprite.z) / 4;
			int ceilDist = (pSprite.z - extents_zTop) / 4;
			int clipDist = pSprite.clipdist << 2;

		    int push = engine.pushmove(pSprite.x,pSprite.y,pSprite.z,nSector,
		    		clipDist,ceilDist,floorDist,CLIPMASK0);
		    pSprite.x = pushmove_x; pSprite.y = pushmove_y; pSprite.z = pushmove_z; nSector = pushmove_sectnum;

		    if ( push == -1 )
		    	actDamageSprite(pPlayer.nSprite, pSprite, kDamageFall, 8000);

		    if ( nSector != pSprite.sectnum )
		    {
		    	if ( nSector == -1 )
		    	{
		    		nSector = pSprite.sectnum;
		       		actDamageSprite(pSprite.xvel, pSprite, kDamageFall, 8000);
		    	}
			    if(!(nSector >= 0 && nSector < kMaxSectors)) game.dassert("nSector >= 0 && nSector < kMaxSectors");
			    changespritesect((short)pPlayer.nSprite, nSector);
		    }
		}

		if(pPlayer.pInput.Forward != 0
				|| pPlayer.pInput.Turn != 0
				|| pPlayer.pInput.Strafe != 0
				|| pPlayer.pInput.Jump
				|| pPlayer.pInput.Crouch
				|| pPlayer.pInput.Lookup
				|| pPlayer.pInput.Lookdown
				|| pPlayer.pInput.AltShoot
				|| pPlayer.pInput.Shoot)
			pPlayer.stayTime = 0;
		else if(pPlayer.stayTime >= 0)
			pPlayer.stayTime += kFrameTicks;

		WeaponProcess(pPlayer);

		// quick hack for death
		if (pXSprite.health == 0)
		{
		    boolean noLastLoad = false;
		    if(pGameInfo.nGameType == 0 && game.isCurrentScreen(gGameScreen) && lastload != null && !lastload.isEmpty() && BuildGdx.compat.checkFile(lastload, Path.User) != null)
		    {
				if(pPlayer.deathTime == 0)
			    {
			    	QUOTE quote = viewSetMessage(deathMessage, -1, 7);
			    	quote.messageTime = kTimerRate * 65536 + gFrameClock;
			    } else {
			    	if(numQuotes == 0)
			    	{
			    		QUOTE quote = viewSetMessage(deathMessage, -1, 7);
				    	quote.messageTime = kTimerRate * 65536 + gFrameClock;
			    	}
			    }

				if (pPlayer.pInput.Use)
				{
					game.changeScreen(gLoadingScreen.setTitle(lastload));
					gLoadingScreen.init(new Runnable() {
						@Override
						public void run() {
							if(!loadgame(lastload))
								game.GameMessage("Can't load game!");
						}
					});
					pPlayer.pInput.Use = false;
					return;
				}

				if (getInput().keyPressed(Keys.ENTER))
					noLastLoad = true;
			}

			boolean deathType = checkPlayerSeq(pPlayer, getPlayerSeq(kPlayerFatality));
			if( pPlayer.fraggerID != -1 && pPlayer.fraggerID != pPlayer.nSprite )
				pPlayer.ang = pSprite.ang = engine.getangle(sprite[pPlayer.fraggerID].x - pSprite.x, sprite[pPlayer.fraggerID].y - pSprite.y);
			pPlayer.deathTime += kFrameTicks;

			if(!deathType  && (pGameInfo.nGameType != kNetModeCoop || !pGameInfo.nReviveMode))
				pPlayer.horiz = mulscale((1 << 15) - (Cos(ClipHigh(pPlayer.deathTime << 3, kAngle180)) >> 15), kHorizUpMax, 16);

			BuildGdx.audio.getSound().setReverb(false, 0.0f);
			pPlayer.LastWeapon = 0;
			pPlayer.CrouchMode = 0;
			if ( pPlayer.currentWeapon != 0 )
				pPlayer.pInput.newWeapon = pPlayer.currentWeapon;	// force weapon down

			if ( pPlayer.pInput.Use || noLastLoad )
			{
				if ( deathType )
				{
					if(pGameInfo.nGameType != kNetModeCoop) {
						if ( pPlayer.deathTime > 360 ) {
					        seqSpawn(pPlayer.pDudeInfo.seqStartID + getPlayerSeq(kPlayerFatalityDead), SS_SPRITE, pPlayer.pSprite.extra, callbacks[ReviveCallback]);
					        pPlayer.pInput.Use = false;
					        return;
						}
					}
				}

				if ( !game.isCurrentScreen(gDemoScreen)
						&& (seqFrame(SS_SPRITE, pPlayer.pSprite.extra) < 0
								|| (pGameInfo.nGameType == kNetModeCoop && pGameInfo.nReviveMode && pPlayer.deathTime > 100)) )
			    {
					sfxKillAll3DSounds();
			    	if (  pPlayer.pSprite != null )
			    		pPlayer.pSprite.lotag = 426;
			    	actPostSprite(pPlayer.nSprite, 4);
			    	seqSpawn(pPlayer.pDudeInfo.seqStartID + getPlayerSeq(kPlayerDead), 3, pPlayer.pSprite.extra, null);
			    	resetInventory(pPlayer);
			    	if ( pGameInfo.nGameType == 0 && numplayers == 1 )
			    	{
//			        	if ( Demo.byte0 )
//			            	recordDemoFunc(Demo);

			    		pPlayer.pInput.Restart = true;
				        pPlayer.pInput.Use = false;
				        pPlayer.hitEffect = 0;

				        return;
			    	}

			    	playerReset(pPlayer.nPlayer);
			    }

				pPlayer.pInput.Use = false;
			}

			return; // Don't allow the player to do anything else if dead
		}

		if( pPlayer.moveState == 1) {
			if( pPlayer.pInput.Forward != 0 ) {
				sprXVel[nSprite] += mulscale(Cos(pSprite.ang), pPlayer.pInput.Forward * cp.frontAccel, 30); //4608
				sprYVel[nSprite] += mulscale(Sin(pSprite.ang), pPlayer.pInput.Forward * cp.frontAccel, 30); //4608
			}

			if( pPlayer.pInput.Strafe != 0) {
				sprXVel[nSprite] += mulscale(Sin(pSprite.ang), pPlayer.pInput.Strafe * cp.sideAccel, 30); //4608
				sprYVel[nSprite] -= mulscale(Cos(pSprite.ang), pPlayer.pInput.Strafe * cp.sideAccel, 30); //4608
			}
		} else {
			if( pXSprite.height < 256 ) {
				int zvel = 65536;
				if( pXSprite.height != 0 ) {
					zvel -= (pXSprite.height << 16) / 256;
				}

				if(pPlayer.pInput.Forward != 0) {
					if(pPlayer.pInput.Forward > 0)
						vel = pPlayer.pInput.Forward * cp.frontAccel;
					else
						vel = pPlayer.pInput.Forward * cp.backAccel;

					if ( pXSprite.height != 0)
						vel = mulscale(zvel, vel, 16);

					sprXVel[nSprite] += mulscale(Cos(pSprite.ang), vel, 30);
					sprYVel[nSprite] += mulscale(Sin(pSprite.ang), vel, 30);
				}

				if( pPlayer.pInput.Strafe != 0) {
					vel = pPlayer.pInput.Strafe * cp.sideAccel;
					if ( pXSprite.height != 0)
						vel = mulscale(zvel, vel, 16);

					sprXVel[nSprite] += mulscale(Sin(pSprite.ang), vel, 30);
					sprYVel[nSprite] -= mulscale(Cos(pSprite.ang), vel, 30);
				}
			}
		}

		// turn player


		if(IsOriginalDemo()) {
			if ( pPlayer.pInput.Turn != 0 )
				pPlayer.ang = pSprite.ang = (short) ((pSprite.ang + (kFrameTicks * (int) pPlayer.pInput.Turn >> 4)) & kAngleMask);
		} else {
			if ( pPlayer.pInput.Turn != 0 )
				pPlayer.ang = BClampAngle(pPlayer.ang + (kFrameTicks * pPlayer.pInput.Turn / 16.0f));
			pSprite.ang = (short) pPlayer.ang;
		}

		if ( pPlayer.pInput.TurnAround )
		{
			if ( pPlayer.TurnAround == 0 )
		    	pPlayer.TurnAround = -1024;
			pPlayer.pInput.TurnAround = false;
		}

		if ( pPlayer.TurnAround < 0 )
	    {
			int angSpeed;
			if ( pPlayer.moveState == kMoveSwim )
				angSpeed = 64;
			else
				angSpeed = 128;

				pPlayer.TurnAround = ClipHigh(pPlayer.TurnAround + angSpeed, 0);
			pPlayer.ang = BClampAngle(pPlayer.ang + angSpeed);
			if(IsOriginalDemo())
				pSprite.ang = (short) pPlayer.ang;
	    }

		pPlayer.lookang -= (pPlayer.lookang>>2);
	    if (pPlayer.lookang != 0 && (pPlayer.lookang >> 2) == 0)
	    	pPlayer.lookang -= ksgn(pPlayer.lookang);

		if (pPlayer.pInput.LookLeft)
	    {
			pPlayer.lookang -= 152;
//			pPlayer.rotscrnang += 24;
	    }

		if (pPlayer.pInput.LookRight)
	    {
			pPlayer.lookang += 152;
//			pPlayer.rotscrnang -= 24;
	    }

		if( !pPlayer.pInput.Jump )
			pPlayer.pJump = false;
		else pPlayer.CrouchMode = 0;

		if ( pPlayer.pInput.Crouch )
			pPlayer.CrouchMode = 0;

		if(pPlayer.moveState == kMoveSwim) {
			pPlayer.CrouchMode = pPlayer.pInput.CrouchMode ? 1 : 0;
		} else if(pPlayer.pInput.CrouchMode) pPlayer.CrouchMode ^= 1;

		if(pPlayer.CrouchMode == 1)
			pPlayer.pInput.Crouch = true;

		if(pPlayer.pInput.LastWeapon)
			pPlayer.pInput.newWeapon = pPlayer.LastWeapon;

		switch ( pPlayer.moveState ) {
		case kMoveWalk:
			if ( !pPlayer.pJump && pPlayer.pInput.Jump && pXSprite.height == 0 )
		    {
				sfxStart3DSound(pPlayer.pSprite, 700, -1, 0); //kSfxPlayJump


				if (inventoryCheck(pPlayer, kInventoryJumpBoots))
		        	sprZVel[nSprite] = -1529173;
		        else
		        	sprZVel[nSprite] = -764586;

//				if (isShrinked(pPlayer.pSprite)) sprZVel[nSprite]-= -200000;
//				else if (isGrown(pPlayer.pSprite)) sprZVel[nSprite]+= -250000;

				pPlayer.pJump = true;
		    }
			if ( pPlayer.pInput.Crouch )
		    	pPlayer.moveState = kMoveCrouch;

			break;
		case kMoveSwim:
			if ( pPlayer.pInput.Jump )
		        sprZVel[nSprite] -= 23301;
		    if ( pPlayer.pInput.Crouch )
		        sprZVel[nSprite] += 23301;

		break;
		case kMoveFly:
			if ( pPlayer.pInput.Jump )
		        sprZVel[nSprite] = -400000;
			else
		    if ( pPlayer.pInput.Crouch )
		        sprZVel[nSprite] = 400000;
		    else sprZVel[nSprite] = 0;
			break;

		case kMoveCrouch:
			if ( !pPlayer.pInput.Crouch )
		    	  pPlayer.moveState = kMoveWalk;
		break;
		}

		if( pPlayer.pInput.Use ) {
			int keyId;
			switch ( ActionScan(pPlayer) )
			{
				case SS_SECTOR:
				{
					XSECTOR pXSector = xsector[Action_nXIndex];
					keyId = pXSector.Key;

					if ( pXSector.locked != 0 && pPlayer == gMe ) {
						viewSetMessage("It's locked", pPlayer.nPlayer, 8);
						sndStartSample(3062, -1, 1, false);
					}

					if ( keyId == 0 || pPlayer.hasKey[keyId] )
						trTriggerSector(Action_nIndex, pXSector, kCommandSpritePush);
					else
						if (pPlayer == gMe)
						{
							viewSetMessage("That requires a key.", pPlayer.nPlayer, 8);
							sndStartSample(3063, -1, 1, false);
						}
					break;
				}

				case SS_WALL:
				{
					XWALL pXWall = xwall[Action_nXIndex];
					keyId = pXWall.key;

					if ( pXWall.locked != 0 && pPlayer == gMe ) {
						viewSetMessage("It's locked", pPlayer.nPlayer, 8);
						sndStartSample(3062, -1, 1, false);
					}

					if ( keyId == 0 || pPlayer.hasKey[keyId] )
						trTriggerWall(Action_nIndex, pXWall, kCommandWallPush);
					else
						if (pPlayer == gMe)
						{
							viewSetMessage("That requires a key.", pPlayer.nPlayer, 8);
							sndStartSample(3063, -1, 1, false);
						}
					break;
				}

				case SS_SPRITE:
				{
					XSPRITE pXSpr = xsprite[Action_nXIndex];
					keyId = pXSpr.key;

					if ( pXSpr.Locked != 0 && pPlayer == gMe && pXSpr.lockMsg != 0) {
//						viewSetMessage("It's locked");
						trTextOver(pXSpr.lockMsg);
					}

					if ( keyId != 0 && !pPlayer.hasKey[keyId] )
			        {
			        	if ( pPlayer == gMe )
			        	{
			        		viewSetMessage("That requires a key.", pPlayer.nPlayer, 8);
							sndStartSample(3063, -1, 1, false);
			        	}
			        }
			        else {
			        	trTriggerSprite(Action_nIndex, pXSpr, kCommandSpritePush);
			        }


					break;
				}
			}

		    if ( pPlayer.handCount > 0 ) {
		    	if(IsOriginalDemo())
		    		pPlayer.handCount = ClipLow(pPlayer.handCount - kFrameTicks * ( 6 - pGameInfo.nDifficulty), 0);
		    	else pPlayer.handCount = ClipLow(pPlayer.handCount - kFrameTicks * ( 7 - pGameInfo.nDifficulty), 0);
		    }

			if( pPlayer.handDamage && pPlayer.handCount == 0) {
				SPRITE pHand = actSpawnDude(pSprite, kDudeHand, pSprite.clipdist << 1);
				pHand.ang = (short) ((pSprite.ang + kAngle180) & kAngleMask);
				sprXVel[pHand.xvel] = sprXVel[nSprite] + mulscale(Cos(pSprite.ang) >> 16, 0x155555, 14);
				sprYVel[pHand.xvel] = sprYVel[nSprite] + mulscale(Sin(pSprite.ang) >> 16, 0x155555, 14);
		        sprZVel[pHand.xvel] = sprZVel[nSprite];
				pPlayer.handDamage = false;
			}

			pPlayer.pInput.Use = false;
		}

		pPlayer.look = BClipRange(pPlayer.look + pPlayer.pInput.mlook, -kLookMax, kLookMax);

		if(pPlayer.pInput.InventoryLeft) {
			InventoryLeft(pPlayer);
			pPlayer.pInput.InventoryLeft = false;
		}

		if(pPlayer.pInput.InventoryRight) {
			InventoryRight(pPlayer);
			pPlayer.pInput.InventoryRight = false;
		}

		if(pPlayer.pInput.InventoryUse) {
			if ( pPlayer.choosedInven != -1 && pPlayer.Inventory[pPlayer.choosedInven].amount > 0 )
				processInventory(pPlayer, pPlayer.choosedInven);
			pPlayer.pInput.InventoryUse = false;

		}

		if(pPlayer.pInput.UseBeastVision) //1
		{
			if ( pPlayer.Inventory[3].amount > 0 )
			    processInventory(pPlayer, 3);
			pPlayer.pInput.UseBeastVision = false;
		}
		if(pPlayer.pInput.UseCrystalBall) // 2
		{
			if ( pPlayer.Inventory[2].amount > 0 )
			    processInventory(pPlayer, 2);
			pPlayer.pInput.UseCrystalBall = false;
		}
		if(pPlayer.pInput.UseJumpBoots) // 4
		{
			if ( pPlayer.Inventory[4].amount > 0 )
			    processInventory(pPlayer, 4);
			pPlayer.pInput.UseJumpBoots = false;
		}
		if(pPlayer.pInput.UseMedKit) //8
		{
			if ( pPlayer.Inventory[0].amount > 0 )
			    processInventory(pPlayer, 0);
			pPlayer.pInput.UseMedKit = false;
		}

		if( pPlayer.pInput.HolsterWeapon )
		{
			pPlayer.pInput.HolsterWeapon = false;
			if ( pPlayer.currentWeapon != 0 ) {
				pPlayer.LastWeapon = pPlayer.currentWeapon;
				WeaponLower(pPlayer);
				viewSetMessage("Holstering weapon", pPlayer.nPlayer, 10);
			}
		}

		if ( pPlayer.pInput.LookCenter && !pPlayer.pInput.Lookup && !pPlayer.pInput.Lookdown )
		{
			if ( pPlayer.look < 0 )
				pPlayer.look = ClipHigh(pPlayer.look + kFrameTicks, 0);

			if ( pPlayer.look > 0 )
				pPlayer.look = ClipLow(pPlayer.look - kFrameTicks, 0);

			if ( pPlayer.look == 0 )
				pPlayer.pInput.LookCenter = false;
		}
		else
		{
			if ( pPlayer.pInput.Lookup )
				pPlayer.look = ClipHigh(pPlayer.look + kFrameTicks, kLookMax);

			if ( pPlayer.pInput.Lookdown )
				pPlayer.look = ClipLow(pPlayer.look - kFrameTicks, -kLookMax);
		}

		if(IsOriginalDemo()) {
			if ( pPlayer.look > 0 )
				pPlayer.horiz = mulscale(kHorizUpMax, Sin((int)(pPlayer.look * (kAngle90 / kLookMax))), 30);
			else if ( pPlayer.look < 0 )
				pPlayer.horiz = mulscale(kHorizDownMax, Sin((int)(pPlayer.look * (kAngle90 / kLookMax))), 30);
			else
				pPlayer.horiz = 0;
		} else {
			if ( pPlayer.look > 0 )
				pPlayer.horiz = (float) (BSinAngle(pPlayer.look * (kAngle90 / kLookMax)) * kHorizUpMax / 16384.0f);
			else if ( pPlayer.look < 0 )
				pPlayer.horiz = (float) (BSinAngle(pPlayer.look * (kAngle90 / kLookMax)) * kHorizDownMax / 16384.0f);
			else
				pPlayer.horiz = 0;
		}

		int floorhit = (gSpriteHit[pSprite.extra].floorHit & kHitTypeMask);
		if(pXSprite.height < 16 && (floorhit == kHitFloor || floorhit == 0)
			&& (sector[pSprite.sectnum].floorstat & 2) != 0)
		{
			int oldslope = engine.getflorzofslope(pSprite.sectnum, pSprite.x, pSprite.y);
			int dx = mulscale(64, Cos(pSprite.ang), 30) + pSprite.x;
			int dy = mulscale(64, Sin(pSprite.ang), 30) + pSprite.y;

			short nSector = engine.updatesector(dx, dy, pSprite.sectnum);
			if(nSector == pSprite.sectnum)
			{
				int newslope = engine.getflorzofslope(nSector, dx, dy);

		        int slope = (((oldslope - newslope) >> 3) - pPlayer.slope) << 14;
		        pPlayer.slope += (slope >> 16);
			}
		}
		else
		{
			int slope = pPlayer.slope;
		    int newslope = -slope << 14;
		    newslope = slope + (newslope >> 16);
		    pPlayer.slope = newslope;
		    if ( newslope < 0 )
		    	newslope = -(slope + ((-slope << 14) >> 16));
		    if ( newslope < 4 )
		    	pPlayer.slope = 0;
		}

		pPlayer.horizOff = -128 * pPlayer.horiz;

		PickUp(pPlayer);
	}

	public static int CheckTouchSprite( SPRITE pSprite )
	{
		int i, next;
		int dx, dy, dz;

		for (i = headspritestat[kStatItem]; i >= 0; i = next)
		{
			next = nextspritestat[i];

			if ( (sprite[i].hitag & kAttrFree) != 0 )
		        continue;

			dx = klabs(pSprite.x - sprite[i].x) >> 4;
			if (dx < kTouchXYDist)
			{
				dy = klabs(pSprite.y - sprite[i].y) >> 4;
				if (dy < kTouchXYDist)
				{
					GetSpriteExtents(pSprite);
					dz = 0;
					if ( sprite[i].z < extents_zTop )
						dz = (extents_zTop - sprite[i].z) >> 8;
					else if ( sprite[i].z > extents_zBot )
						dz = (sprite[i].z - extents_zBot) >> 8;
					if (dz < kTouchZDist)
					{
						if (engine.qdist(dx, dy) < kTouchXYDist)
						{

							GetSpriteExtents(sprite[i]);
							if (
								// center
									engine.cansee(pSprite.x, pSprite.y,  pSprite.z, pSprite.sectnum,
									sprite[i].x, sprite[i].y, sprite[i].z, sprite[i].sectnum) ||

								// top
											engine.cansee(pSprite.x, pSprite.y,  pSprite.z, pSprite.sectnum,
									sprite[i].x, sprite[i].y, extents_zTop, sprite[i].sectnum) ||

								// bottom
											engine.cansee(pSprite.x, pSprite.y,  pSprite.z, pSprite.sectnum,
									sprite[i].x, sprite[i].y, extents_zBot, sprite[i].sectnum)
							)
							return i;
						}
					}
				}
			}
		}
		return -1;
	}

	public static void PickUp( PLAYER pPlayer )
	{
		SPRITE pSprite = pPlayer.pSprite;
		String buffer = "";

		int i, next;
		int dx, dy, dz;

		for (i = headspritestat[kStatItem]; i >= 0; i = next)
		{
			next = nextspritestat[i];

			if ( (sprite[i].hitag & kAttrFree) != 0 )
		        continue;

			dx = klabs(pSprite.x - sprite[i].x) >> 4;
			if (dx <= kTouchXYDist)
			{
				dy = klabs(pSprite.y - sprite[i].y) >> 4;
				if (dy <= kTouchXYDist)
				{
					GetSpriteExtents(pSprite);
					dz = 0;
					if ( sprite[i].z < extents_zTop )
						dz = (extents_zTop - sprite[i].z) >> 8;
					else if ( sprite[i].z > extents_zBot )
						dz = (sprite[i].z - extents_zBot) >> 8;
					if (dz < kTouchZDist)
					{
						if (engine.qdist(dx, dy) <= kTouchXYDist)
						{
							GetSpriteExtents(sprite[i]);
							if (
								// center
									engine.cansee(pSprite.x, pSprite.y,  pSprite.z, pSprite.sectnum,
									sprite[i].x, sprite[i].y, sprite[i].z, sprite[i].sectnum) ||

								// top
									engine.cansee(pSprite.x, pSprite.y,  pSprite.z, pSprite.sectnum,
									sprite[i].x, sprite[i].y, extents_zTop, sprite[i].sectnum) ||

								// bottom
									engine.cansee(pSprite.x, pSprite.y,  pSprite.z, pSprite.sectnum,
									sprite[i].x, sprite[i].y, extents_zBot, sprite[i].sectnum) )
							{
								boolean bPickedUp = false; int customMsg = -1; int nType = sprite[i].lotag;
								if (nType != 80 && nType != 40) { // No pickup for random item generators.
									XSPRITE pXSprite = (sprite[i].extra >= 0) ? xsprite[sprite[i].extra] : null;
									if (pXSprite != null && pXSprite.txID != 3 && pXSprite.lockMsg > 0)
										customMsg = pXSprite.lockMsg;

									if (nType >= kItemBase && nType <= kItemMax)
									{
										bPickedUp = PickupItem( pPlayer, i, nType );
										if (bPickedUp && customMsg == -1) buffer = "Picked up " + gItemText[ nType - kItemBase ];
									}
									else if (nType >= kAmmoItemBase && nType < kAmmoItemMax) {
										bPickedUp = PickupAmmo( pPlayer, i, nType );
										if (bPickedUp && customMsg == -1) buffer = "Picked up " + gAmmoText[ nType - kAmmoItemBase ];
									}
									else if (nType >= kWeaponItemBase && nType < kWeaponItemMax)
									{
										bPickedUp = PickupWeapon( pPlayer, i, nType );
										if (bPickedUp && customMsg == -1) buffer = "Picked up " + gWeaponText[ nType - kWeaponItemBase ];
									}
								}
								if (bPickedUp)
								{
									if ( sprite[i].extra > 0)
									{
										XSPRITE pXItem = xsprite[sprite[i].extra];
										if ( pXItem.Pickup )
											trTriggerSprite(i, pXItem, kCommandSpritePickup);
									}

									if ( !actCheckRespawn( sprite[i] ) )
										actPostSprite( i, kStatFree );

									pPlayer.pickupEffect = 30;
									if (pPlayer == gMe)
										if (customMsg > 0) trTextOver(customMsg - 1);
										else viewSetMessage(buffer, pPlayer.nPlayer);

								}
							}
						}
					}
				}
			}
		}
	}

	public static void PlayerWaterBubblesCallback( int nSprite ) {
		SPRITE pSprite = sprite[nSprite];
		if(IsPlayerSprite(pSprite)) {
			PLAYER pPlayer = gPlayer[pSprite.lotag - kDudePlayer1];
			if(pPlayer == null)game.dassert("pPlayer != NULL");
			if(pPlayer.bubbleTime != 0) {
				GetSpriteExtents( pSprite );
				for( int i = 0; i < pPlayer.bubbleTime >> 6; i++) {
					Tile pic = engine.getTile(pSprite.picnum);

					int  radius = (pSprite.xrepeat * (pic.getWidth() / 2)) >> 2;
					int nAngle = Random(2048);

					int dx = pSprite.x + mulscale(Cos(nAngle), radius, 30);
					int dy = pSprite.y + mulscale(Sin(nAngle), radius, 30);

					int range = extents_zBot - Random(extents_zBot - extents_zTop);

					SPRITE pEffect = actSpawnEffect((Random(3) + 23), pSprite.sectnum, dx, dy, range, 0);
					if(pEffect != null) {
						sprXVel[pEffect.xvel] = BiRandom(0x1AAAA) + sprXVel[nSprite];
						sprYVel[pEffect.xvel] = BiRandom(0x1AAAA) + sprYVel[nSprite];
						sprZVel[pEffect.xvel] = BiRandom(0x1AAAA) + sprZVel[nSprite];
					}
				}
				evPostCallback(nSprite, SS_SPRITE, 4, 10);
			}
		}
	}

	public static boolean PickupItem( PLAYER pPlayer, int nSprite, int nItemType )
	{
		int soundId = 775;
		SPRITE pSprite = pPlayer.pSprite;
		XSPRITE pXSprite = pPlayer.pXsprite;

		int nPowerUp = nItemType - kItemBase;
		switch( nItemType )
		{


//		case kItemLtdInvisibility:
//			if (isGrown(pPlayer.pSprite)) return false;
//			case kItemShroomShrink:
//			case kItemShroomGrow:
//				switch (nItemType) {
//					case kItemShroomShrink:
//						if (isShrinked(pSprite)) return false;
//						break;
//					case kItemShroomGrow:
//						if (isGrown(pSprite)) return false;
//						break;
//				}
//				powerupActivate(pPlayer, nPowerUp);
//				break;
			case kItemKey1:
			case kItemKey2:
			case kItemKey3:
			case kItemKey4:
			case kItemKey5:
			case kItemKey6:
			case kItemKey7:
				if ( pPlayer.hasKey[nItemType - kItemKey1 + 1] )
					return false;
				pPlayer.hasKey[nItemType - kItemKey1 + 1] = true;
				soundId = 781;
				break;

			case kItemPotion1:
			case kItemMedPouch:
			case kItemLifeEssence:
			case kItemLifeSeed:
				int addPower = gPowerUpInfo[nPowerUp].addPower;
				if (sprite[nSprite].extra >= 0 && xsprite[sprite[nSprite].extra].data1 > 0 && !IsOriginalDemo())
					addPower = xsprite[sprite[nSprite].extra].data1;

				return actHealDude(pXSprite, addPower, gPowerUpInfo[nPowerUp].maxPower);
			case kItemCrystalBall:
				if ( pGameInfo.nGameType == 0 || !PickupInventryItem(pPlayer, gItemInfo[nPowerUp].nInventory) )
			        return false;
				break;
			case kItemDoctorBag:
			case kItemJumpBoots:
			case kItemDivingSuit:
			case kItemBeastVision:
				if(!PickupInventryItem(pPlayer, gItemInfo[nPowerUp].nInventory)) return false;
				break;
			case kItemGunsAkimbo:
				if (!powerupActivate(pPlayer,nPowerUp)) return false;
				break;
			case kItemBasicArmor:
			case kItemBodyArmor:
			case kItemFireArmor:
			case kItemSpiritArmor:
			case kItemSuperArmor:
				boolean ret = false;
				ARMORITEMDATA pArmorData = gArmorItemData[nItemType - kArmorItemBase];

				if( pPlayer.ArmorAmount[BLUEARMOR] < pArmorData.max[BLUEARMOR] ) {
					pPlayer.ArmorAmount[BLUEARMOR] = ClipHigh(pPlayer.ArmorAmount[BLUEARMOR] + pArmorData.count[BLUEARMOR], pArmorData.max[BLUEARMOR]);
					ret = true;
				}
				if( pPlayer.ArmorAmount[REDARMOR] < pArmorData.max[REDARMOR] ) {
					pPlayer.ArmorAmount[REDARMOR] = ClipHigh(pPlayer.ArmorAmount[REDARMOR] + pArmorData.count[REDARMOR], pArmorData.max[REDARMOR]);
					ret = true;
				}
				if( pPlayer.ArmorAmount[GREENARMOR] < pArmorData.max[GREENARMOR] ) {
					pPlayer.ArmorAmount[GREENARMOR] = ClipHigh(pPlayer.ArmorAmount[GREENARMOR] + pArmorData.count[GREENARMOR], pArmorData.max[GREENARMOR]);
					ret = true;
				}
				if(ret)
					sfxCreate3DSound(pSprite.x, pSprite.y, pSprite.z, 779, pSprite.sectnum);
				return ret;

			case kItemBlueTeamBase:
			case kItemRedTeamBase:
				 if ( pGameInfo.nGameType != 3 )
				      return false;

				 if( sprite[nSprite].extra > 0 )
				 {
					 XSPRITE pXBase = xsprite[sprite[nSprite].extra];
					 if(nItemType == kItemBlueTeamBase) {
						 if( pPlayer.teamID == 1 )
						 {
							 if ( (pPlayer.hasFlag & 1) == 0 && pXBase.state != 0)
							 {
								 pPlayer.hasFlag |= 1;
					         	 pPlayer.nBlueTeam = sprite[nSprite].xvel;
								 trTriggerSprite(pSprite.xvel, pXBase, 0);
								 sndStartSample(8007, 255, 2, false);
								 viewSetMessage(game.net.gProfile[pPlayer.nPlayer].name + " stole Blue Flag", -1, 10);
							 }
						 }
						 else
						 {
							 if ( (pPlayer.hasFlag & 1) != 0 && pXBase.state == 0 )
							 {
					        	pPlayer.nBlueTeam = -1;
					        	pPlayer.hasFlag &= ~1;
					        	trTriggerSprite(pSprite.xvel, pXBase, 1);
					        	sndStartSample(8003, 255, 2, false);
					        	viewSetMessage(game.net.gProfile[pPlayer.nPlayer].name + " returned Blue Flag", -1, 10);
							 }

							 if ( (pPlayer.hasFlag & 2) != 0 && pXBase.state != 0)
							 {
								 pPlayer.nRedTeam = -1;
								 pPlayer.hasFlag &= ~2;

								 nTeamCount[pPlayer.teamID] += 10;
							     nTeamClock[pPlayer.teamID] += 240;

							     checkFragLimit();
								 evSend(0, 0, kChannelFlag1Captured, kCommandOn);
								 sndStartSample(8001, 255, 2, false);
								 viewSetMessage(game.net.gProfile[pPlayer.nPlayer].name + " captured Red Flag!", -1, 7);
							 }
						 }
					 }
					 else //kItemRedTeamBase
					 {
						 if( pPlayer.teamID == 0 )
						 {
							 if ( (pPlayer.hasFlag & 2) == 0 && pXBase.state != 0)
							 {
								 pPlayer.hasFlag |= 2;
					             pPlayer.nRedTeam = sprite[nSprite].xvel;
								 trTriggerSprite(pSprite.xvel, pXBase, 0);
								 sndStartSample(8006, 255, 2, false);
								 viewSetMessage(game.net.gProfile[pPlayer.nPlayer].name + " stole Red Flag", -1, 7);
							 }
						 }
						 else
						 {
							 if ( (pPlayer.hasFlag & 2) != 0 && pXBase.state == 0 )
							 {
					        	pPlayer.nRedTeam = -1;
					        	pPlayer.hasFlag &= ~2;
					        	trTriggerSprite(pSprite.xvel, pXBase, 1);
					        	sndStartSample(8002, 255, 2, false);
					        	viewSetMessage(game.net.gProfile[pPlayer.nPlayer].name + " returned Red Flag", -1, 7);
							 }

							 if ( (pPlayer.hasFlag & 1) != 0 && pXBase.state != 0)
							 {
								 pPlayer.nBlueTeam = -1;
								 pPlayer.hasFlag &= ~1;
								 nTeamCount[pPlayer.teamID] += 10;
							     nTeamClock[pPlayer.teamID] += 240;

							     checkFragLimit();
								 evSend(0, 0, kChannelFlag0Captured, kCommandOn);
								 sndStartSample(8000, 255, 2, false);
								 viewSetMessage(game.net.gProfile[pPlayer.nPlayer].name + " captured Blue Flag!", -1, 10);
							 }
						 }
					 }
					 return false;
				 }
				 return false;
			case kItemBlueFlag:
				if ( pGameInfo.nGameType != 3 )
			          return false;
				pPlayer.hasFlag |= 1;
				pPlayer.nBlueTeam = sprite[nSprite].owner;
				checkEventList(pSprite.xvel, SS_SPRITE, 17);
				break;
			case kItemRedFlag:
				if ( pGameInfo.nGameType != 3 )
					return false;
				pPlayer.hasFlag |= 2;
				pPlayer.nRedTeam = sprite[nSprite].owner;
				checkEventList(pSprite.xvel, SS_SPRITE, 17);
				break;
			default:
				if (!powerupActivate( pPlayer, nPowerUp )) return false;
				break;
		}

		if(nItemType != kItemGunsAkimbo) sfxCreate3DSound(pSprite.x, pSprite.y, pSprite.z, soundId, pSprite.sectnum);
		return true;
	}

	// this constant determine when the effect starts to wear off
	public static final int kWaneTime = 512;
	public static int timer = 0;
	public static void DeliriumProcess() {
		int nDelirium = powerupCheck(gPlayer[gViewIndex], kItemShroomDelirium - kItemBase);

		if ( nDelirium != 0 )
		{
			timer += kFrameTicks;

			int maxTilt = kAngle30;
			int maxTurn = kAngle30;
			int maxPitch = 20;

			if (nDelirium < kWaneTime)
			{
				int scale = (nDelirium << 16) / kWaneTime;
				maxTilt = mulscale(maxTilt, scale, 16);
				maxTurn = mulscale(maxTurn, scale, 16);
				maxPitch = mulscale(maxPitch, scale, 16);
			}

			deliriumTilt = mulscale((Sin(timer * 2) / 2) + (Sin(timer * 3) / 2), maxTilt, 30);
			deliriumTurn = mulscale((Sin(timer * 3) / 2) + (Sin(timer * 4) / 2), maxTurn, 30);
			deliriumPitch = mulscale((Sin(timer * 4) / 2) + (Sin(timer * 5) / 2), maxPitch, 30);
		} else {
			deliriumTilt = 0;
			deliriumTurn = 0;
			deliriumPitch = 0;
		}
	}

	public static boolean shrinkPlayerSize(PLAYER pPlayer, int divider) {
		//System.err.println("SHRINK FOR "+pPlayer.pSprite.lotag);
		pPlayer.pXsprite.data1=(short)-divider;
		playerSetRace(pPlayer,kModeHumanShrink);
		return true;
	}

	public static boolean growPlayerSize(PLAYER pPlayer, int multiplier) {
		//System.err.println("GROW FOR "+pPlayer.pSprite.lotag);
		pPlayer.pXsprite.data1 = (short) multiplier;
		playerSetRace(pPlayer,kModeHumanGrown);
		return true;
	}

	public static boolean resetPlayerSize(PLAYER pPlayer) {
		//System.err.println("NORMAL FOR "+pPlayer.pSprite.lotag);
		playerSetRace(pPlayer,kModeHuman);
		pPlayer.pXsprite.data1 = 0;
		return true;
	}

//	public static void deactivateSizeShrooms(PLAYER pPlayer) {
//		powerupDeactivate(pPlayer,kItemShroomGrow - kItemBase);
//		pPlayer.powerUpTimer[kItemShroomGrow - kItemBase] = 0;
//
//		powerupDeactivate(pPlayer,kItemShroomShrink - kItemBase);
//		pPlayer.powerUpTimer[kItemShroomShrink - kItemBase] = 0;
//	}

	public static boolean powerupActivate( PLAYER pPlayer, int nPowerUp )
	{
		// skip the power-up if it is unique and already activated

		if ( powerupCheck( pPlayer, nPowerUp ) > 0 && gPowerUpInfo[nPowerUp].isUnique )
			return false;

		int nInventory = getInventoryNum(nPowerUp + kItemBase);
		if ( nInventory >= 0 )
			pPlayer.Inventory[nInventory].activated = true;

		if((!IsOriginalDemo() && nInventory == -1) || pPlayer.powerUpTimer[nPowerUp] == 0)
			pPlayer.powerUpTimer[nPowerUp] = gPowerUpInfo[nPowerUp].addPower;

		switch( nPowerUp + kItemBase ) {	// switch on of actual type

			//case kGDXItemMapLevel:
				//gFullMap = true;
				//break;
//			case kItemShroomShrink:
//				if (isGrown(pPlayer.pSprite)) deactivateSizeShrooms(pPlayer);
//				else shrinkPlayerSize(pPlayer,2);
//				break;
//			case kItemShroomGrow:
//				if (isShrinked(pPlayer.pSprite)) deactivateSizeShrooms(pPlayer);
//				else {
//					growPlayerSize(pPlayer,2);
//					if (powerupCheck(gPlayer[pPlayer.pSprite.lotag - kDudePlayer1],kItemLtdInvisibility - kItemBase) > 0) {
//						powerupDeactivate(pPlayer, kItemLtdInvisibility - kItemBase);
//						pPlayer.powerUpTimer[kItemLtdInvisibility - kItemBase] = 0;
//					}
//
//					if (ru.m210projects.Blood.AI.AIUNICULT.ceilIsTooLow(pPlayer.pSprite))
//						actDamageSprite(pPlayer.pSprite.xvel, pPlayer.pSprite,3,65535);
//				}
//				break;
			case kItemGunsAkimbo:
				if(IsOriginalDemo()
					|| pPlayer.currentWeapon == 2
					|| pPlayer.currentWeapon == 3
					|| pPlayer.currentWeapon == 4
					|| pPlayer.currentWeapon == 5
					|| pPlayer.currentWeapon == 8 )
				{
					pPlayer.pInput.newWeapon = pPlayer.currentWeapon;
					WeaponRaise(pPlayer);
				}
				break;
			case kItemDivingSuit:
				if(!pPlayer.godMode) pPlayer.damageShift[kDamageDrown]++;
			    if ( pPlayer == gMe)
			    	BuildGdx.audio.getSound().setReverb(true, 0.2f);
				break;
			case kItemGasMask:
				if(!pPlayer.godMode) pPlayer.damageShift[kDamageDrown]++;
				break;
			case kItemReflectiveShots:
				if ( pPlayer == gMe ) {
					BuildGdx.audio.getSound().setReverb(true, 0.4f);
				}
				break;
			case kItemAsbestosArmor:
				if(!pPlayer.godMode) pPlayer.damageShift[kDamageBurn]++;
				break;
			case kItemInvulnerability:
				if(!pPlayer.godMode) {
					for ( int i = 0; i < kDamageMax; i++ )
						pPlayer.damageShift[i]++;
				}
				break;
			case kItemFeatherFall:
			case kItemJumpBoots:
				if(!pPlayer.godMode) pPlayer.damageShift[kDamageFall]++;
				break;
			case kItemJetpack:
				pPlayer.moveState = kMoveFly;
				pPlayer.pSprite.hitag &= ~kAttrGravity;
				break;
			default:
				break;
		}

		sfxStart3DSound(pPlayer.pSprite, 776, -1, 0);

		return true;
	}

	public static void powerupDeactivate ( PLAYER pPlayer, int nPowerUp )
	{
		int nInventory = getInventoryNum( nPowerUp + kItemBase );

		if ( nInventory >= 0 )
			pPlayer.Inventory[nInventory].activated = false;

		switch( nPowerUp + kItemBase )	// switch off of actual type
		{
//			case kItemShroomShrink:
//				resetPlayerSize(pPlayer);
//				if (ru.m210projects.Blood.AI.AIUNICULT.ceilIsTooLow(pPlayer.pSprite))
//					actDamageSprite(pPlayer.pSprite.xvel, pPlayer.pSprite,3,65535);
//				break;
//			case kItemShroomGrow:
//				resetPlayerSize(pPlayer);
//				break;
			case kItemGunsAkimbo:
				if(IsOriginalDemo()
					|| pPlayer.currentWeapon == 2
					|| pPlayer.currentWeapon == 3
					|| pPlayer.currentWeapon == 4
					|| pPlayer.currentWeapon == 5
					|| pPlayer.currentWeapon == 8 )
				{
					pPlayer.pInput.newWeapon = pPlayer.currentWeapon;
					WeaponRaise(pPlayer);
				}
				break;
			case kItemDivingSuit:
				if(!pPlayer.godMode) pPlayer.damageShift[kDamageDrown]--;
			    if ( pPlayer == gMe ) {
			    	BuildGdx.audio.getSound().setReverb(false, 0);
			    }
				break;
			case kItemGasMask:
				if(!pPlayer.godMode) pPlayer.damageShift[kDamageDrown]--;
				break;
			case kItemReflectiveShots:
				if ( pPlayer == gMe ) {
					BuildGdx.audio.getSound().setReverb(false, 0);
				}
				break;
			case kItemAsbestosArmor:
				if(!pPlayer.godMode) pPlayer.damageShift[kDamageBurn]--;
				break;
			case kItemInvulnerability:
				if(!pPlayer.godMode) {
					for ( int i = 0; i < kDamageMax; i++ )
						pPlayer.damageShift[i]--;
				}
				break;
			case kItemFeatherFall:
			case kItemJumpBoots:
				if(!pPlayer.godMode) pPlayer.damageShift[kDamageFall]--;
				break;
			case kItemJetpack:
				pPlayer.moveState = kMoveWalk;
				pPlayer.pSprite.hitag |= kAttrGravity;
				sprZVel[pPlayer.pSprite.xvel] = 58254;
			default:
				break;
		}
	}

	public static void powerupProcess( PLAYER pPlayer ) {
		pPlayer.showInventory = ClipLow( pPlayer.showInventory - kFrameTicks, 0);
		for(int i = 48; i >= 0; i--) {
			int nInventory = getInventoryNum(i + kItemBase);
			if( nInventory < 0) {
				if(pPlayer.powerUpTimer[i] > 0) {
					pPlayer.powerUpTimer[i] = ClipLow( pPlayer.powerUpTimer[i] - kFrameTicks, 0);
					if(pPlayer.powerUpTimer[i] == 0)
						powerupDeactivate(pPlayer, i);
				}
			} else if( pPlayer.Inventory[nInventory].activated ){
				pPlayer.powerUpTimer[i] = ClipLow( pPlayer.powerUpTimer[i] - kFrameTicks, 0);
				if(i == 47) continue; //Jetpack

				if( pPlayer.powerUpTimer[i] > 0) {
					pPlayer.Inventory[nInventory].amount = 100 * pPlayer.powerUpTimer[i] / gPowerUpInfo[i].addPower;
				} else {
					powerupDeactivate(pPlayer, i);
				    if ( nInventory == pPlayer.choosedInven )
				    	pPlayer.choosedInven = 0;
				}
			}
		}

		//System.err.println("GROW TIME: "+pPlayer.powerUpTimer[kItemShroomGrow - kItemBase]);
		//System.err.println("SHRINK TIME: "+pPlayer.powerUpTimer[kItemShroomShrink - kItemBase]);
		DeliriumProcess();
	}

	public static void powerupClear( PLAYER pPlayer )
	{
		for(int i = 0; i < kMaxPowerUps; i++)
			pPlayer.powerUpTimer[i] = 0;
	}

	public static boolean PickupInventryItem( PLAYER pPlayer, int nInventory ) {
		if(nInventory >= kInventoryMax) {
			System.err.println("Unhandled pack item " + nInventory);
			System.exit(1);
		}

		if ( pPlayer.Inventory[nInventory].amount >= 100 )
		      return false;

		pPlayer.Inventory[nInventory].amount = 100;

		int type = -1;
		switch(nInventory) {
		 	case kInventoryDivingSuit:
		        type = kItemDivingSuit;
		        break;
		    case kInventoryCrystalBall:
		        type = kItemCrystalBall;
		        break;
		    case kInventoryBeastVision:
		        type = kItemBeastVision;
		        break;
		    case kInventoryJumpBoots:
		        type = kItemJumpBoots;
		        break;
		    case kInventoryDoctorBag:
		        break;
		    case kInventoryJetpack:
		    	type = kItemJetpack;
		        break;
		    default:
		    	System.err.println("Unhandled pack item " + nInventory);
				System.exit(1);
		        break;
		}
		type -= kItemBase;
		if (type == 17)
			powerupActivate(pPlayer,type);
		if ( type >= 0 )
			pPlayer.powerUpTimer[type]
					= gPowerUpInfo[type].addPower;
		if ( pPlayer.choosedInven == -1 )
			pPlayer.choosedInven = nInventory;
		if ( pPlayer.Inventory[pPlayer.choosedInven].amount == 0 )
			pPlayer.choosedInven = nInventory;
		return true;
	}


//	public static boolean isGrown(SPRITE pSprite) {
//		return (powerupCheck(gPlayer[pSprite.lotag - kDudePlayer1],kItemShroomGrow - kItemBase) > 0);
//	}
//
//	public static boolean isShrinked(SPRITE pSprite) {
//		return (powerupCheck(gPlayer[pSprite.lotag - kDudePlayer1],kItemShroomShrink - kItemBase) > 0);
//	}

	public static int powerupCheck( PLAYER pPlayer, int nPowerUp )
	{
		if(pPlayer == null)
			game.dassert("pPlayer != null" );
		if(!(nPowerUp >= 0 && nPowerUp < kMaxPowerUps))game.dassert("nPowerUp >= 0 && nPowerUp < kMaxPowerUps" );

		int nInventory = getInventoryNum(nPowerUp + kItemBase);
		if ( nInventory < 0 || inventoryCheck(pPlayer, nInventory) )
			return pPlayer.powerUpTimer[nPowerUp];
		else
			return 0;
	}

	public static void processInventory( PLAYER pPlayer, int nInventory )
	{
		int nPowerUp = -1;
		boolean activated = false;
		if ( pPlayer.Inventory[nInventory].amount > 0 )
		{
		    switch ( nInventory )
		    {
		    	case kInventoryDoctorBag:
		    		XSPRITE pXDude = pPlayer.pXsprite;
		            int health = pXDude.health >> 4;
		            if ( health < 100 )
		            {
		            	int healValue;
		            	if ( (100 - health) >= pPlayer.Inventory[kInventoryDoctorBag].amount )
		                {
		                	healValue = pPlayer.Inventory[kInventoryDoctorBag].amount;
		                	actHealDude(pXDude, healValue, 100);
		                }
		                else
		                {
		                	healValue = 100 - health;
		                	actHealDude(pXDude, 100 - health, 100);
		                }
		                pPlayer.Inventory[kInventoryDoctorBag].amount -= healValue;
		            }
		    		break;
			    case kInventoryDivingSuit:
			    	nPowerUp = kItemDivingSuit;
			        activated = true;
			        break;
			    case kInventoryCrystalBall:
			    	nPowerUp = kItemCrystalBall;
			        activated = true;
			        break;
			    case kInventoryBeastVision:
			    	nPowerUp = kItemBeastVision;
			        activated = true;
			        break;
			    case kInventoryJumpBoots:
			    	nPowerUp = kItemJumpBoots;
			        activated = true;
			        break;
			    case kInventoryJetpack:
			    	nPowerUp = kItemJetpack;
			        activated = true;
			        break;
			    default:
			    	System.err.println("Unhandled pack item " + nInventory);
			    	System.exit(1);
			     	break;
		    }
		}

		nPowerUp -= kItemBase;
		pPlayer.showInventory = 0;
	    if ( activated )
	    {
	    	if ( pPlayer.Inventory[nInventory].activated )
	    		powerupDeactivate(pPlayer, nPowerUp);
	    	else
	    		powerupActivate(pPlayer, nPowerUp);
	    }
	}

	public static int getInventoryNum(int item)
	{
		switch ( item )
		{
	    	case kItemDivingSuit:
	    		return kInventoryDivingSuit;
	    	case kItemCrystalBall:
	    		return kInventoryCrystalBall;
	    	case kItemBeastVision:
	    		return kInventoryBeastVision;
	    	case kItemJumpBoots:
	    		return kInventoryJumpBoots;
	    	case kItemJetpack:
	    		return kInventoryJetpack;
		}
		return -1;
	}

	public static boolean inventoryCheck( PLAYER pPlayer, int nInventory )
	{
		return pPlayer.Inventory[nInventory].activated;
	}

	public static int getInventoryAmount( PLAYER pPlayer, int nInventory )
	{
		return pPlayer.Inventory[nInventory].amount;
	}


	private static final boolean gInventorySwap = true;
	public static void InventoryRight(PLAYER pPlayer)
	{
		if ( pPlayer.showInventory > 0 )
		{
			if(gInventorySwap)
			{
				int nextInv = pPlayer.choosedInven + 1;
				for(int i = 0; i < kInventoryMax; i++, nextInv++)
				{
					if(nextInv == kInventoryMax) nextInv = 0;
					if ( pPlayer.Inventory[nextInv].amount != 0)
				    {
				    	pPlayer.choosedInven = nextInv;
					    break;
				    }
				}
			} else {
			    for ( int i = ClipHigh(pPlayer.choosedInven + 1, kInventoryMax); i < kInventoryMax; i++ )
			    {
					if ( pPlayer.Inventory[i].amount != 0 )
				    {
				    	pPlayer.choosedInven = i;
					    break;
				    }
			    }
			}
		}
		pPlayer.showInventory = 600;
	}

	public static void InventoryLeft(PLAYER pPlayer)
	{
		if ( pPlayer.showInventory > 0 )
		{
			if(gInventorySwap) {
				int prevInv = pPlayer.choosedInven - 1;
				for(int i = kInventoryMax; i >= 0; i--, prevInv--)
				{
					if(prevInv <= -1) prevInv = kInventoryMax - 1;
					if ( pPlayer.Inventory[prevInv].amount != 0)
				    {
				    	pPlayer.choosedInven = prevInv;
					    break;
				    }
				}
			} else {
			    for ( int i = ClipLow(pPlayer.choosedInven - 1, 0); i >= 0; --i )
			    {
					if ( pPlayer.Inventory[i].amount != 0 )
				    {
				    	pPlayer.choosedInven = i;
					    break;
				    }
			    }
			}
		}
		pPlayer.showInventory = 600;
	}

	public static boolean PickupAmmo( PLAYER pPlayer, int nSprite, int nAmmoItemType )
	{
		AMMOITEMDATA pAmmoData = gAmmoItemData[nAmmoItemType - kAmmoItemBase];
		SPRITE pSprite = sprite[nSprite];
		int nAmmoType = pAmmoData.ammoType;

		if(nAmmoType == 255) return true;
		else if (pPlayer.ammoCount[nAmmoType] >= gAmmoInfo[nAmmoType].max && !gInfiniteAmmo) return false;
		else if (pSprite.extra < 0 || xsprite[pSprite.extra].data1 <= 0)
			pPlayer.ammoCount[nAmmoType] = ClipHigh(pPlayer.ammoCount[nAmmoType] + pAmmoData.count, gAmmoInfo[nAmmoType].max);
		else
			pPlayer.ammoCount[nAmmoType] = ClipHigh(pPlayer.ammoCount[nAmmoType] + xsprite[pSprite.extra].data1, gAmmoInfo[nAmmoType].max);

		// set the hasWeapon flags for weapons which are ammo
		if (pAmmoData.weaponType != kWeaponNone) pPlayer.hasWeapon[pAmmoData.weaponType] = true;

		sfxStart3DSound(pPlayer.pSprite, 782, -1, 0);
		return true;
	}

	public static boolean PickupWeapon( PLAYER pPlayer, int nSprite, int nWeaponItem )
	{
		WEAPONITEMDATA pWeaponData = gWeaponItemData[nWeaponItem - kWeaponItemBase];
		int nWeapon = pWeaponData.weaponType;
		int nAmmo = pWeaponData.ammoType;
		SPRITE pSprite = sprite[nSprite];
		// add weapon to player inventory
		if ( pPlayer.hasWeapon[nWeapon] && pGameInfo.nWeaponSettings != 2 && pGameInfo.nWeaponSettings != 3 )
		{
			if(actGetRespawnTime(sprite[nSprite]) != 0)
			{
				if (nAmmo == kAmmoNone || (!gInfiniteAmmo && pPlayer.ammoCount[nAmmo] >= gAmmoInfo[nAmmo].max)) return false;
				else if (pSprite.extra < 0 || xsprite[pSprite.extra].data1 <= 0)
					pPlayer.ammoCount[nAmmo] = ClipHigh(pPlayer.ammoCount[nAmmo] + pWeaponData.count, gAmmoInfo[nAmmo].max);
				else
					pPlayer.ammoCount[nAmmo] = ClipHigh(pPlayer.ammoCount[nAmmo] + xsprite[pSprite.extra].data1, gAmmoInfo[nAmmo].max);

				sfxStart3DSound(pPlayer.pSprite, 777, -1, 0);
				return true;
			}
			return false;
		}
		else //pickup life leech
		if ( sprite[nSprite].lotag != kWeaponLifeLeech
			|| pGameInfo.nGameType <= kNetModeCoop || !PickupLeech(pPlayer, null) )
		{
			pPlayer.hasWeapon[nWeapon] = true;

			if ( nAmmo != kAmmoNone )
			{
				// add preloaded ammo
				if (pSprite.extra < 0 || xsprite[pSprite.extra].data1 <= 0)
					pPlayer.ammoCount[nAmmo] = ClipHigh(
						pPlayer.ammoCount[nAmmo] + pWeaponData.count, gAmmoInfo[nAmmo].max );
				else
					pPlayer.ammoCount[nAmmo] = ClipHigh(pPlayer.ammoCount[nAmmo] + xsprite[pSprite.extra].data1, gAmmoInfo[nAmmo].max);
			}

		    int weap = WeaponUpgrade(pPlayer, nWeapon);
		    if ( weap != pPlayer.currentWeapon )
		    {
		    	pPlayer.weaponState = 0;
		    	pPlayer.updateWeapon = weap;
		    }
			//SPRITE pSprite = pPlayer.pSprite;
		    sfxStart3DSound(pPlayer.pSprite, 777, -1, 0); //kSfxWeaponUp = 777

			return true;
		}

		return false;
	}

	public static void HandEffectProcess(PLAYER gPlayer)
	{
		if(gPlayer.pSprite.statnum == kStatDude && gPlayer.handDamage)
		{
		    if ( mulscale(4, vRandom(), 15) != 0) {
		    	gPlayer.handCount = ClipHigh(gPlayer.handCount + kFrameTicks, 64);
		    	gPlayer.blindEffect = ClipHigh(gPlayer.blindEffect + 2 * kFrameTicks, 128);
		    }
		}
//		original method
//		if(gPlayer.pSprite.statnum == kStatDude && gPlayer.handDamage) {
//			gPlayer.handCount = ClipHigh(gPlayer.handCount + pGameInfo.nDifficulty + 2, 64);
//			if(35 - 5 * pGameInfo.nDifficulty < gPlayer.handCount)
//				gPlayer.blindEffect = ClipHigh(gPlayer.blindEffect + 4 * pGameInfo.nDifficulty, 128);
//		}
	}

	public static void playerMove( PLAYER pPlayer )
	{
		int nSprite = pPlayer.nSprite;
		SPRITE pSprite = pPlayer.pSprite;
		XSPRITE pXSprite = pPlayer.pXsprite;

		POSTURE cp = gPosture[pPlayer.nLifeMode][pPlayer.moveState];

		powerupProcess(pPlayer);
		HandEffectProcess(pPlayer);

		short nSector;

		GetSpriteExtents( pSprite );

		int clipDist = pSprite.clipdist << 2;
		int floorDist = (extents_zBot - pSprite.z) / 4;
		int ceilDist = (pSprite.z - extents_zTop) / 4;

		if( !gNoClip) {
			nSector = pSprite.sectnum;

			int push = engine.pushmove(pSprite.x, pSprite.y, pSprite.z,
					nSector, clipDist, ceilDist, floorDist, CLIPMASK0);

			pSprite.x = pushmove_x;
			pSprite.y = pushmove_y;
			pSprite.z = pushmove_z;
			nSector = pushmove_sectnum;

			if( push == -1) {
				actDamageSprite(nSprite, pSprite, kDamageFall, 8000);
			}

			if ( nSector != pSprite.sectnum )
		    {
		      if ( nSector == -1 )
		      {
		    	  nSector = pSprite.sectnum;
		    	  actDamageSprite(pSprite.xvel, pSprite, kDamageFall, 8000);
		      }
		      if(!(nSector >= 0 && nSector < kMaxSectors)) game.dassert("nSector >= 0 && nSector < kMaxSectors");
		      changespritesect((short) nSprite, nSector);
		    }
		}

		ProcessInput(pPlayer);

		int moveDist = (int) (engine.qdist(sprXVel[nSprite], sprYVel[nSprite]) >> 16);
		pPlayer.viewOffdZ += mulscale(28672, sprZVel[nSprite] - pPlayer.viewOffdZ, 16);
		int dZv = pSprite.z - cp.viewSpeed - pPlayer.viewOffZ;
		if( dZv > 0) pPlayer.viewOffdZ += mulscale(40960, dZv << 8, 16);
		else pPlayer.viewOffdZ += mulscale(6144, dZv << 8, 16);
		pPlayer.viewOffZ += pPlayer.viewOffdZ >> 8;

		pPlayer.weapOffdZ += mulscale(20480, sprZVel[nSprite] - pPlayer.weapOffdZ, 16);
		int dZw = pSprite.z - cp.weapSpeed - pPlayer.weaponAboveZ;
		if( dZw > 0) pPlayer.weapOffdZ += mulscale(32768, dZw << 8, 16);
		else pPlayer.weapOffdZ += mulscale(3072, dZw << 8, 16);
		pPlayer.weaponAboveZ += pPlayer.weapOffdZ >> 8;

		pPlayer.bobAmp = ClipLow(pPlayer.bobAmp - kFrameTicks, 0);

		if ( pPlayer.moveState == kMoveSwim )
		{
			pPlayer.bobPhase = (pPlayer.bobPhase + kFrameTicks * kAngle360 / kTimerRate / 4) & kAngleMask;
			pPlayer.swayPhase = (pPlayer.swayPhase + kFrameTicks * kAngle360 / kTimerRate / 4) & kAngleMask;

			pPlayer.bobHeight = mulscale(cp.bobV * 10, Sin(pPlayer.bobPhase * 2), 30);
			pPlayer.bobWidth = mulscale(cp.bobH * pPlayer.bobAmp, Sin(pPlayer.bobPhase - kAngle45), 30);
			pPlayer.swayHeight = mulscale(cp.swayV * pPlayer.bobAmp, Sin(pPlayer.swayPhase * 2), 30);
			pPlayer.swayWidth = mulscale(cp.swayH * pPlayer.bobAmp, Sin(pPlayer.swayPhase - kAngle60), 30);
		} else {
			if( pXSprite.height < 256) {

				boolean Run = false; //(!adapter.cfg.gAutoRun && pPlayer.Run) || (!pPlayer.Run && adapter.cfg.gAutoRun);

				pPlayer.bobPhase = (pPlayer.bobPhase + kFrameTicks * cp.pace[Run?1:0]) & kAngleMask;
				pPlayer.swayPhase = (pPlayer.swayPhase + kFrameTicks * cp.pace[Run?1:0] / 2) & kAngleMask;

				if ( Run )
				{
					if (pPlayer.bobAmp < 60)
						pPlayer.bobAmp = ClipHigh(pPlayer.bobAmp + moveDist, 60);
				}
				else
				{
					if (pPlayer.bobAmp < 30)
						pPlayer.bobAmp = ClipHigh(pPlayer.bobAmp + moveDist, 30);
				}

				pPlayer.bobHeight = mulscale(cp.bobV * pPlayer.bobAmp, Sin(pPlayer.bobPhase * 2), 30);
				pPlayer.bobWidth = mulscale(cp.bobH * pPlayer.bobAmp, Sin(pPlayer.bobPhase - kAngle45), 30);
				pPlayer.swayHeight = mulscale(cp.swayV * pPlayer.bobAmp, Sin(pPlayer.swayPhase * 2), 30);
				pPlayer.swayWidth = mulscale(cp.swayH * pPlayer.bobAmp, Sin(pPlayer.swayPhase - kAngle60), 30);
			}
		}

		pPlayer.explosion = 0;
		pPlayer.quakeTime = ClipLow(pPlayer.quakeTime - kFrameTicks, 0);
		pPlayer.tilt = ClipLow(pPlayer.tilt - kFrameTicks, 0);
		pPlayer.visibility = ClipLow(pPlayer.visibility - kFrameTicks, 0);

		pPlayer.hitEffect = ClipLow(pPlayer.hitEffect - kFrameTicks, 0);
		pPlayer.blindEffect = ClipLow(pPlayer.blindEffect - kFrameTicks, 0);
		pPlayer.pickupEffect = ClipLow(pPlayer.pickupEffect - kFrameTicks, 0);

		if ( pXSprite.health == 0 ) {
			if(pPlayer == gMe)
				gMe.handDamage = false;
			return;
		}

		pPlayer.Underwater = false;
		if( pPlayer.moveState == kMoveSwim ) {
			pPlayer.Underwater = true;

			if( gLowerLink[pSprite.sectnum] > 0) {
				int type = sprite[gLowerLink[pSprite.sectnum]].lotag;
		        if ( type == 14 || type == 10 )
		        {
		        	if ( engine.getceilzofslope(pSprite.sectnum, pSprite.x, pSprite.y) > pPlayer.viewOffZ )
		        		pPlayer.Underwater = false;
		        }
			}
		}

		if ( !pPlayer.Underwater )
	    {
			pPlayer.airTime = 1200;
			pPlayer.drownEffect = 0;
			if ( inventoryCheck(pPlayer, kInventoryDivingSuit) )
	      		processInventory(pPlayer, kInventoryDivingSuit);
	    }

		int dudeIndex = kDudePlayer1 - kDudeBase;
		int nXSprite = pSprite.extra;

		if(nXSprite == -1) return; //Player disconnected?

		switch ( pPlayer.moveState ) {
		case kMoveWalk:
			if ( moveDist == 0 )
				seqSpawn(dudeInfo[dudeIndex].seqStartID + getPlayerSeq(kPlayerIdle), SS_SPRITE, nXSprite, null);
			else
				seqSpawn(dudeInfo[dudeIndex].seqStartID + getPlayerSeq(kPlayerWalk), SS_SPRITE, nXSprite, null);	// hack to make player walk
			break;
		case kMoveSwim:
				seqSpawn(dudeInfo[dudeIndex].seqStartID + getPlayerSeq(kPlayerSwim), SS_SPRITE, nXSprite, null);
			break;
		case kMoveCrouch:
		case kMoveFly:
			seqSpawn(dudeInfo[dudeIndex].seqStartID + getPlayerSeq(kPlayerCrouch), SS_SPRITE, nXSprite, null);
			break;
		}
	}

	public static SPRITE playerFireThing( PLAYER pPlayer, int xoffset, int relSlope, int thingType, int velocity )
	{
		if(!(thingType >= kThingBase && thingType < kThingMax))
			game.dassert("thingType >= kThingBase && thingType < kThingMax" );

		int z = pPlayer.weaponAboveZ - pPlayer.pSprite.z;
		if(newHoriz && !IsOriginalDemo()) z += 10 * pPlayer.horiz;
		return actFireThing( pPlayer.nSprite, xoffset, z, (int) pPlayer.horizOff + relSlope, thingType, velocity );
	}

	public static SPRITE playerFireMissile ( PLAYER pPlayer, int xoffset, int dx, int dy, int dz, int missileType )
	{
		int z = pPlayer.weaponAboveZ - pPlayer.pSprite.z;
		if(newHoriz && !IsOriginalDemo()) z -= 20 * pPlayer.horiz; //z += 10 * pPlayer.horiz;
		return actFireMissile(pPlayer.pSprite, xoffset, z, dx, dy, dz, missileType);
	}

	public static int playerCalcDamage(PLAYER pPlayer, int nDamageType, int nDamage)
	{
		int nArmorType = gDamageInfo[nDamageType].nArmorType;
		if ( nArmorType >= 0 )
		{
			if ( pPlayer.ArmorAmount[nArmorType] != 0 )
		    {
		    	int tmp = nDamage / 4;
		    	int damage = tmp + muldiv(pPlayer.ArmorAmount[nArmorType], (7 * nDamage / 8) - tmp, 3200);

		    	nDamage -= damage;
		    	pPlayer.ArmorAmount[nArmorType] = ClipLow(pPlayer.ArmorAmount[nArmorType] - damage, 0);
		    }
		}
		return nDamage;
	}

	public static void playerDamageSprite( PLAYER pPlayer, int nSource, int nDamageType, int nDamage )
	{
		if(!(nSource < kMaxSprites)) game.dassert("nSource < kMaxSprites");
		if(pPlayer == null) game.dassert("pPlayer != NULL");
		if(!(nDamageType >= 0 && nDamageType < kDamageMax))
			game.dassert("nDamageType >= 0 && nDamageType < kDamageMax");

		if ( pPlayer.damageShift[nDamageType] == 0 )
		{
			int nDeathSeqID = -1;

			nDamage = playerCalcDamage(pPlayer, nDamageType, nDamage);
			pPlayer.hitEffect = ClipHigh(pPlayer.hitEffect + (nDamage >> 3), 600);
			SPRITE pSprite = pPlayer.pSprite;
			XSPRITE pXSprite = pPlayer.pXsprite;

			if(pSprite == null || !IsDudeSprite(pSprite))
				return;

			DudeInfo pDudeInfo = dudeInfo[pSprite.lotag - kDudeBase];

			CALLPROC nCallback = null;
			if ( (pXSprite.health) == 0 )
			{
				if(checkPlayerSeq(pPlayer, getPlayerSeq(kPlayerFatality)))  //Fatality
				{
					if(pGameInfo.nGameType == kNetModeCoop && pGameInfo.nReviveMode)
						return;

					switch(nDamageType)
					{
						case kDamageExplode:
							actGenerateGibs(pSprite, 7, null, null);
							actGenerateGibs(pSprite, 15, null, null);
							pSprite.cstat |= 0x8000;
							nDeathSeqID = getPlayerSeq(kPlayerFDying);
						break;
						case kDamageSpirit:
							nDeathSeqID = getPlayerSeq(kPlayerFSpirit);
							sfxStart3DSound(pSprite, 716, 0, 0);
						break;
						default:
							GetSpriteExtents(pSprite);
							startPos.set(pSprite.x, pSprite.y, extents_zTop);
			            	startVel.set(sprXVel[pSprite.xvel] >> 1, sprYVel[pSprite.xvel] >> 1, -838860);
							actGenerateGibs(pSprite, 27, startPos, startVel);
							actGenerateGibs(pSprite, 7, null, null);
							actSpawnBlood(pSprite);
							actSpawnBlood(pSprite);
							nDeathSeqID = getPlayerSeq(kPlayerFDying);
						break;
					}
				}
			} else {
				int dHealth = pXSprite.health - nDamage;
				pXSprite.health = ClipLow(dHealth, 0);
				if ( pXSprite.health != 0 && pXSprite.health < 16 )
				{
					pXSprite.health = 0;
					dHealth = -25;
					nDamageType = kDamageBullet;
				}

				if(pXSprite.health == 0)
				{
					if ( pGameInfo.nGameType == kNetModeTeams )
					{
						if(pPlayer.hasFlag != 0)
						{
							if((pPlayer.hasFlag & 1) != 0)
							{
								pPlayer.hasFlag &= ~1;
								SPRITE nFlag = DropPickupObject(pPlayer.pSprite, kItemBlueFlag);
								if(nFlag != null) {
									nFlag.owner = pPlayer.nBlueTeam;
								}
								sndStartSample(8005, 255, 2, false);
								viewSetMessage(game.net.gProfile[pPlayer.nPlayer].name + " dropped Blue Flag", -1, 10);
							}

							if((pPlayer.hasFlag & 2) != 0)
							{
								pPlayer.hasFlag &= ~2;
								SPRITE nFlag = DropPickupObject(pPlayer.pSprite, kItemRedFlag);
								if(nFlag != null) {
									nFlag.owner = pPlayer.nRedTeam;
								}
								sndStartSample(8004, 255, 2, false);
								viewSetMessage(game.net.gProfile[pPlayer.nPlayer].name + " dropped Red Flag", -1, 7);
							}
						}
					}

					sfxKill3DSound(pPlayer.pSprite, -1, 441);
					pPlayer.deathTime = 0;
					if(pGameInfo.nGameType == kNetModeOff || ((pPlayer.currentWeapon != kWeaponTNT
							&& pPlayer.currentWeapon != kWeaponSprayCan)
							|| pPlayer.weaponTimer == 0) ) {
						pPlayer.fLoopQAV = false;
						pPlayer.currentWeapon = 0;
						pPlayer.LastWeapon = 0;
					}

					pPlayer.CrouchMode = 0;
					pPlayer.fraggerID = nSource;
					pPlayer.voodooCount = 0;
					if ( nDamageType == kDamageExplode && nDamage < 144 )
						nDamageType = kDamageFall;

					if(pGameInfo.nGameType == kNetModeCoop && pGameInfo.nReviveMode) //revive code
					{
						sfxStart3DSound(pSprite, gDamageInfo[nDamageType].nSoundID[3], 0, 2);
						nDeathSeqID = getPlayerSeq(kPlayerFatality);
						pXSprite.target = nSource;
						//evPostCallback(pSprite.xvel, 3, 15, 13);
					} else {
						switch(nDamageType)
						{
							case kDamageBurn:
								sfxStart3DSound(pSprite, 718, 0, 0);
								nDeathSeqID = getPlayerSeq(kPlayerBurn);
							break;
							case kDamageExplode:
								sfxStart3DSound(pSprite, 717, 0, 0);
								actGenerateGibs(pSprite, 7, null, null);
								actGenerateGibs(pSprite, 15, null, null);
								nDeathSeqID = getPlayerSeq(kPlayerExplode);
								pSprite.cstat |= 0x8000;
							break;
							case kDamageDrown:
								nDeathSeqID = getPlayerSeq(kPlayerDying);
							break;
							default:
								if ( dHealth < -20 && pGameInfo.nGameType >= kNetModeBloodBath && Chance(0x2000) )
								{
									nCallback = callbacks[FatalityDead];
									sfxStart3DSound(pSprite, gDamageInfo[nDamageType].nSoundID[3], 0, 2);
									nDeathSeqID = getPlayerSeq(kPlayerFatality);
									powerupActivate(pPlayer, kItemShroomDelirium - kItemBase);
									pXSprite.target = nSource;
									evPostCallback(pSprite.xvel, 3, 15, 13);
								} else {
									sfxStart3DSound(pSprite, 716, 0, 0);
									nDeathSeqID = getPlayerSeq(kPlayerDying);
								}
							break;
						}
					}
				} else {
					DAMAGEINFO pDamageInfo = gDamageInfo[nDamageType];
					int nSoundID;
					if ( nDamage < 160 )
						nSoundID = pDamageInfo.nSoundID[Random(3)];
					else
						nSoundID = pDamageInfo.nSoundID[0];
					if ( nDamageType == kDamageDrown && (gPlayer[gViewIndex].pXsprite.palette == 1) && !pPlayer.handDamage )
						nSoundID = 714;

					sfxStart3DSound(pSprite, nSoundID, 0, 6);
					return;
				}
			}

			if ( nDeathSeqID >= 0 )
			{
				if ( nDeathSeqID != getPlayerSeq(kPlayerFatality) || (pGameInfo.nReviveMode && pGameInfo.nGameType == kNetModeCoop))
				{
					int nXSector = sector[pSprite.sectnum].extra;
					powerupClear(pPlayer);
					if ( nXSector > 0 && xsector[nXSector].Exit )
						trTriggerSector(pSprite.sectnum, xsector[nXSector], kCommandSectorExit);
					pSprite.hitag |= 7;
					for ( int i = connecthead; i >= 0; i = connectpoint2[i] )
					{
						if ( pSprite.xvel == gPlayer[i].fraggerID && gPlayer[i].deathTime > 0 )
							gPlayer[i].fraggerID = -1;
					}
					playerFrag(pPlayer, nSource);
					trTriggerSprite(pSprite.xvel, pXSprite, kCommandOff);
				}

				if(!BuildGdx.cache.contains(pDudeInfo.seqStartID + nDeathSeqID, seq))
					game.dassert("gSysRes.Lookup(pDudeInfo.seqStartID + nDeathSeqID, \"SEQ\") != null");
				seqSpawn(pDudeInfo.seqStartID + nDeathSeqID, SS_SPRITE, pSprite.extra, nCallback);
			}
		}
	}

	public static final FragInfo[] deathAphorisms1 = {
		new FragInfo(4202, " is excrement", null),
		new FragInfo(4203, " is hamburger", null),
		new FragInfo(4204, " suffered scrotum separation", null),
		new FragInfo(4206, " volunteered for population control", null),
		new FragInfo(4207, " has suicided", null),
	};

	public static final FragInfo[] deathAphorisms2 = {
		new FragInfo(4100," boned ", " like a fish"),
		new FragInfo(4101," castrated ", null),
		new FragInfo(4102," creamed ", null),
		new FragInfo(4103," destroyed ", null),
		new FragInfo(4104," diced ", null),
		new FragInfo(4105," disemboweled ", null),
		new FragInfo(4106," flattened ", null),
		new FragInfo(4107," gave ", " Anal Justice"),
		new FragInfo(4108," gave AnAl MaDnEsS to ", null),
		new FragInfo(4109," hurt ", " real bad"),
		new FragInfo(4110," killed ", null),
		new FragInfo(4111," made mincemeat out of ", null),
		new FragInfo(4112," massacred ", null),
		new FragInfo(4113," mutilated ", null),
		new FragInfo(4114," reamed ", null),
		new FragInfo(4115," ripped ", " a new orifice"),
		new FragInfo(4116," slaughtered ", null),
		new FragInfo(4117," sliced ", null),
		new FragInfo(4118," smashed ", null),
		new FragInfo(4119," sodomized ", null),
		new FragInfo(4120," splattered ", null),
		new FragInfo(4121," squashed ", null),
		new FragInfo(4122," throttled ", null),
		new FragInfo(4123," wasted ", null),
		new FragInfo(4124," body bagged ", null),
	};

	public static void playerFrag(PLAYER pKiller, PLAYER pVictim)
	{
		if(pKiller == null) game.dassert("pKiller != NULL");
		if(pVictim == null) game.dassert("pVictim != NULL");

		// get player indices for killer and victim
		int nKiller = pKiller.pSprite.lotag - kDudePlayer1;
		if(nKiller < 0 || nKiller >= kMaxPlayers)
			game.dassert("nKiller >= 0 && nKiller < kMaxPlayers");
		int nVictim = pVictim.pSprite.lotag - kDudePlayer1;
		if(nVictim < 0 || nVictim >= kMaxPlayers)
			game.dassert("nVictim >= 0 && nVictim < kMaxPlayers");

		if(myconnectindex == connecthead)
		{
//			sub_7AC28(); XXX
		}

		if ( nKiller == nVictim )
		{
			pVictim.fraggerID = -1;	// can't target yourself
			pVictim.fragCount--;
			pVictim.fragInfo[nKiller]--;	// frags against self is negative

			if ( pGameInfo.nGameType == 3 )
			      nTeamCount[pVictim.teamID]--;
			int n = Random(deathAphorisms1.length);
			if ( pVictim.handCount <= 0 )
		    {
				String message;
				if ( pVictim != gMe && pGameInfo.nGameType > kNetModeOff && deathAphorisms1[n].nSound >= 0 ) {
					if ( pGameInfo.nGameType != kNetModeCoop)
						sndStartSample(deathAphorisms1[n].nSound, 255, 2, false);
		    		message = game.net.gProfile[nVictim].name + deathAphorisms1[n].text;
				} else message = "You killed yourself!";

				viewSetMessage(message, -1, 7);
		    }
		}
		else
		{
			pKiller.fragCount++;
			pKiller.fragInfo[nVictim]++;	// frags against others are positive

			if ( pGameInfo.nGameType == kNetModeTeams )
		    {
				if ( pKiller.teamID == pVictim.teamID )
				{
					 nTeamCount[pKiller.teamID]--;
				}
				else
				{
					 nTeamCount[pKiller.teamID]++;
					 nTeamClock[pKiller.teamID] += 120;
				}
		    }

			if(pGameInfo.nGameType > kNetModeOff) {
				int n = Random(deathAphorisms2.length);
				if ( pGameInfo.nGameType > kNetModeOff && pKiller == gMe && deathAphorisms2[n].nSound >= 0) {
					if ( pGameInfo.nGameType != kNetModeCoop )
						sndStartSample(deathAphorisms2[n].nSound, 255, 2, false);
				}
				String message = game.net.gProfile[nKiller].name + deathAphorisms2[n].text + game.net.gProfile[nVictim].name;
				if(deathAphorisms2[n].text2 != null)
					message += deathAphorisms2[n].text2;
				viewSetMessage(message, -1, 7);
			}
		}
	}

	public static void playerFrag(PLAYER pVictim, int nKiller)
	{
		if(nKiller >= 0)
		{
			SPRITE pKiller = sprite[nKiller];
			if(IsDudeSprite(pKiller)) {
				if(IsPlayerSprite(pKiller))
				{
					playerFrag(gPlayer[pKiller.lotag - kDudePlayer1], pVictim);
					int nVictimTeam = pVictim.teamID & 1;
					int nKillerTeam = gPlayer[pKiller.lotag - kDudePlayer1].teamID & 1;

					if(nKillerTeam == 0 && nVictimTeam == 0)
						evSend(0, 0, kChannelTeamADeath, kCommandToggle);
					else evSend(0, 0, kChannelTeamBDeath, kCommandToggle);
				} else {
					if(pGameInfo.nGameType > kNetModeOff) {
						int n = Random(deathAphorisms2.length);
						String message = kDudeName[pKiller.lotag - kDudeBase] + deathAphorisms2[n].text + game.net.gProfile[pVictim.nPlayer].name;
						if(deathAphorisms2[n].text2 != null)
							message += deathAphorisms2[n].text2;
						viewSetMessage(message, -1, 7);
					}
				}
			}
		}

		checkFragLimit();
	}

	public static void checkFragLimit()
	{
		if(pGameInfo.nFragLimit != 0) {
			if(pGameInfo.nGameType == kNetModeBloodBath)
			{
				int nFrags = 0;
				for ( int i = connecthead; i >= 0; i = connectpoint2[i] )
					nFrags = Math.max(nFrags, gPlayer[i].fragCount);

				if(nFrags >= pGameInfo.nFragLimit)
					evPostCallback(gMe.nSprite, SS_SPRITE, 300, 22);
			}

			if(pGameInfo.nGameType == kNetModeTeams)
			{
				int nFrags = 0;
				for(int i = 0; i < 2; i++)
					nFrags = Math.max(nFrags, nTeamCount[i]);

				if(nFrags >= pGameInfo.nFragLimit)
					evPostCallback(gMe.nSprite, SS_SPRITE, 300, 22);
			}
		}
	}

	public static void FReviveCallback( int nXIndex )
	{
		XSPRITE pXSprite = xsprite[nXIndex];
		SPRITE pSprite = sprite[pXSprite.reference];
		if(pGameInfo.nReviveMode && pGameInfo.nGameType == kNetModeCoop)
			actHealDude(pXSprite, 30, 30);
		else
			actHealDude(pXSprite, 1, 2);

		if ( pGameInfo.nGameType > kNetModeOff && numplayers > 1 )
		{
			 sfxStart3DSound(pSprite, 3009, 0, 0);
			 if(IsPlayerSprite(pSprite))
			 {
				 PLAYER pPlayer = gPlayer[pSprite.lotag - kDudePlayer1];
				 String message;
				 if ( pPlayer == gMe )
					 message = "I LIVE...AGAIN!!";
			     else
			    	 message = game.net.gProfile[pPlayer.nPlayer].name + " lives again!";

				 viewSetMessage(message, -1, 10);
				 pPlayer.pInput.newWeapon = 1;
			 }
		}
	}

	public static void FatalityDeadCallback( int nXIndex )
	{
		for ( int i = connecthead; i >= 0; i = connectpoint2[i] )
		{
			if ( xsprite[nXIndex] == gPlayer[i].pXsprite ) {
				playerDamageSprite(gPlayer[i], gPlayer[i].fraggerID, 5, 8000);
				break;
			}
		}
	}

	private ByteBuffer PlayerBuffer;
	public byte[] getBytes(int nVersion)
	{
		if(PlayerBuffer == null)
			PlayerBuffer = ByteBuffer.allocate(865);
		else PlayerBuffer.clear();
		PlayerBuffer.order(ByteOrder.LITTLE_ENDIAN);

		PlayerBuffer.putInt(NPSTART);
		PlayerBuffer.putInt(pWeaponQAV);
		PlayerBuffer.putInt(weaponCallback);
		PlayerBuffer.put(Run?(byte)1:0);
		PlayerBuffer.putInt(moveState);
		PlayerBuffer.putInt(moveDist);
		PlayerBuffer.putInt(bobAmp);
		PlayerBuffer.putInt(bobPhase);
		PlayerBuffer.putInt(bobHeight);
		PlayerBuffer.putInt(bobWidth);
		PlayerBuffer.putInt(swayAmp);
		PlayerBuffer.putInt(swayPhase);
		PlayerBuffer.putInt(swayHeight);
		PlayerBuffer.putInt(swayWidth);
		PlayerBuffer.putInt(nPlayer);
		PlayerBuffer.putInt(nSprite);
		PlayerBuffer.putInt(nLifeMode);
		PlayerBuffer.putInt(bloodlust);
		PlayerBuffer.putInt(viewOffZ);
		PlayerBuffer.putInt(viewOffdZ);
		PlayerBuffer.putInt(weaponAboveZ);
		PlayerBuffer.putInt(weapOffdZ);
		if(nVersion < 300) {
			PlayerBuffer.putInt((int)look);
			PlayerBuffer.putInt((int)horiz);
		} else {
			PlayerBuffer.putFloat(look);
			PlayerBuffer.putFloat(horiz);
		}
		PlayerBuffer.putInt(slope);
		if(nVersion < 300)
			PlayerBuffer.putInt((int)horizOff);
		else PlayerBuffer.putFloat(horizOff);

		PlayerBuffer.put(Underwater?(byte)1:0);
		for (int j = 0; j < 8; j++)
			PlayerBuffer.put(hasKey[j]?(byte)1:0);
		PlayerBuffer.put((byte)hasFlag);

		if(nVersion >= 277) {
			PlayerBuffer.putShort(nBlueTeam);
			PlayerBuffer.putShort(nRedTeam);
			for (int j = 0; j < 12; j++)
				PlayerBuffer.put((byte)0);
		}
		for (int j = 0; j < 7; j++)
			PlayerBuffer.putInt(damageShift[j]);
		PlayerBuffer.put((byte)currentWeapon);
		PlayerBuffer.put((byte)updateWeapon);
		PlayerBuffer.putInt(weaponTimer);
		PlayerBuffer.putInt(weaponState);
		PlayerBuffer.putInt(weaponAmmo);
		for(int j = 0; j < 14; j++)
			PlayerBuffer.put(hasWeapon[j]?(byte)1:0);
		for(int j = 0; j < 14; j++)
			PlayerBuffer.putInt(weaponMode[j]);
		for(int j = 0; j < 14; j++)
			PlayerBuffer.putInt(weaponOrder[0][j]);
		for(int j = 0; j < 14; j++)
			PlayerBuffer.putInt(weaponOrder[1][j]);
		for(int j = 0; j < 12; j++)
			PlayerBuffer.putInt(ammoCount[j]);
		PlayerBuffer.put(fLoopQAV?(byte)1:0);
		PlayerBuffer.putInt(fuseTime);
		PlayerBuffer.putInt(fireClock);
		PlayerBuffer.putInt(throwTime);
		PlayerBuffer.putInt((int)aim.x);
		PlayerBuffer.putInt((int)aim.y);
		PlayerBuffer.putInt((int)aim.z);
		PlayerBuffer.putInt((int)relAim.x);
		PlayerBuffer.putInt((int)relAim.y);
		PlayerBuffer.putInt((int)relAim.z);
		PlayerBuffer.putInt(nAimSprite);
		PlayerBuffer.putInt(aimCount);
		for(int j = 0; j < 16; j++)
			PlayerBuffer.putShort((short)aimSprites[j]);
		PlayerBuffer.putInt(deathTime);
		for(int j = 0; j < kMaxPowerUps; j++)
			PlayerBuffer.putInt(powerUpTimer[j]);
		PlayerBuffer.putInt(fragCount);
		for(int j = 0; j < kMaxPlayers; j++)
			PlayerBuffer.putInt(fragInfo[j]);
		PlayerBuffer.putInt(teamID);
		PlayerBuffer.putInt(fraggerID);
		PlayerBuffer.putInt(airTime); // set when first going underwater, decremented while underwater then takes damage
		PlayerBuffer.putInt(bloodTime);	// set when player walks through blood, decremented when on normal surface
		PlayerBuffer.putInt(gooTime);// set when player walks through sewage, decremented when on normal surface
		PlayerBuffer.putInt(wetTime);	// set when player walks through water, decremented when on normal surface
		PlayerBuffer.putInt(bubbleTime); // set when first going underwater, decremented while underwater then takes damage
		PlayerBuffer.putInt(0);
		PlayerBuffer.putInt(stayTime);
		PlayerBuffer.putInt(kickTime);
		PlayerBuffer.putInt(pLaughsCount);
		PlayerBuffer.putInt(TurnAround);

		PlayerBuffer.put(godMode?(byte)1:0);
		PlayerBuffer.put(fScreamed?(byte)1:0);
		PlayerBuffer.put(pJump?(byte)1:0);
		PlayerBuffer.putInt(showInventory);
		PlayerBuffer.putInt(choosedInven);
		for( int j = 0; j < 5; j++) {
			PlayerBuffer.put(Inventory[j].activated?(byte)1:0);
			PlayerBuffer.putInt(Inventory[j].amount);
		}
		for( int j = 0; j < 3; j++)
			PlayerBuffer.putInt(ArmorAmount[j]);

		PlayerBuffer.putInt(voodooTarget);
		PlayerBuffer.putInt(voodooCount);
		PlayerBuffer.putInt(voodooAng);
		PlayerBuffer.putInt(voodooUnk);

		PlayerBuffer.putInt(explosion);
		PlayerBuffer.putInt(tilt);
		PlayerBuffer.putInt(visibility);
		PlayerBuffer.putInt(hitEffect);
		PlayerBuffer.putInt(blindEffect);
		PlayerBuffer.putInt(drownEffect);
		PlayerBuffer.putInt(handCount);
		PlayerBuffer.put(handDamage?(byte)1:0);
		PlayerBuffer.putInt(pickupEffect);
		PlayerBuffer.putInt(fireEffect);
		PlayerBuffer.putInt(quakeTime);

		return PlayerBuffer.array();
	}

	public void set(Resource bb, int nVersion)
	{
		this.NPSTART = bb.readInt();
		this.pWeaponQAV = bb.readInt();
		this.weaponCallback = bb.readInt();
		this.Run = bb.readBoolean();
		this.moveState = bb.readInt();
		this.moveDist = bb.readInt();
		this.bobAmp = bb.readInt();
		this.bobPhase = bb.readInt();
		this.bobHeight = bb.readInt();
		this.bobWidth = bb.readInt();
		this.swayAmp = bb.readInt();
		this.swayPhase = bb.readInt();
		this.swayHeight = bb.readInt();
		this.swayWidth = bb.readInt();
		this.nPlayer = bb.readInt();
		this.nSprite = bb.readInt();
		this.nLifeMode = bb.readInt();
		this.bloodlust = bb.readInt();
		this.viewOffZ = bb.readInt();
		this.viewOffdZ = bb.readInt();
		this.weaponAboveZ = bb.readInt();
		this.weapOffdZ = bb.readInt();
		if(nVersion < 300) {
			this.look = bb.readInt();
			this.horiz = bb.readInt();
		} else {
			this.look = bb.readFloat();
			this.horiz = bb.readFloat();
		}
		this.slope = bb.readInt();
		if(nVersion < 300)
			this.horizOff = bb.readInt();
		else this.horizOff = bb.readFloat();

		this.Underwater = bb.readBoolean();
		for (int j = 0; j < 8; j++)
			this.hasKey[j] = bb.readBoolean();
		this.hasFlag = bb.readByte();
		if(nVersion >= 277) {
			this.nBlueTeam = bb.readShort();
			this.nRedTeam = bb.readShort();
			for (int j = 0; j < 12; j++)
				bb.readByte();
		}

		for (int j = 0; j < 7; j++)
			this.damageShift[j] = bb.readInt();
		this.currentWeapon = bb.readByte();
		this.updateWeapon = bb.readByte();
		this.weaponTimer = bb.readInt();
		this.weaponState = bb.readInt();
		this.weaponAmmo = bb.readInt();
		for(int j = 0; j < 14; j++)
			this.hasWeapon[j] = bb.readBoolean();
		for(int j = 0; j < 14; j++)
			this.weaponMode[j] = bb.readInt();
		for(int j = 0; j < 14; j++)
			this.weaponOrder[0][j] = bb.readInt();
		for(int j = 0; j < 14; j++)
			this.weaponOrder[1][j] = bb.readInt();
		for(int j = 0; j < 12; j++)
			this.ammoCount[j] = bb.readInt();

		this.fLoopQAV = bb.readBoolean();
		this.fuseTime = bb.readInt();
		this.fireClock = bb.readInt();
		this.throwTime = bb.readInt();

		this.aim.set(bb.readInt(), bb.readInt(), bb.readInt());
		this.relAim.set(bb.readInt(), bb.readInt(), bb.readInt());
		this.nAimSprite = bb.readInt();
		this.aimCount = bb.readInt();
		for(int j = 0; j < 16; j++)
			this.aimSprites[j] = bb.readShort();
		this.deathTime = bb.readInt();
		for(int j = 0; j < kMaxPowerUps; j++)
			this.powerUpTimer[j] = bb.readInt();
		this.fragCount = bb.readInt();
		for(int j = 0; j < kMaxPlayers; j++)
			this.fragInfo[j] = bb.readInt();
		this.teamID = bb.readInt();
		this.fraggerID = bb.readInt();
		this.airTime = bb.readInt(); // set when first going underwater, decremented while underwater then takes damage
		this.bloodTime = bb.readInt();	// set when player walks through blood, decremented when on normal surface
		this.gooTime = bb.readInt();// set when player walks through sewage, decremented when on normal surface
		this.wetTime = bb.readInt();	// set when player walks through water, decremented when on normal surface
		this.bubbleTime = bb.readInt(); // set when first going underwater, decremented while underwater then takes damage
		bb.readInt();
		this.stayTime = bb.readInt();
		this.kickTime = bb.readInt();
		this.pLaughsCount = bb.readInt();
		this.TurnAround = bb.readInt();

		this.godMode = bb.readBoolean();
		this.fScreamed = bb.readBoolean();
		this.pJump = bb.readBoolean();
		this.showInventory = bb.readInt();
		this.choosedInven = bb.readInt();
		for( int j = 0; j < 5; j++) {
			this.Inventory[j].activated = bb.readBoolean();
			this.Inventory[j].amount = bb.readInt();
		}
		for( int j = 0; j < 3; j++)
			this.ArmorAmount[j] = bb.readInt();

		this.voodooTarget = bb.readInt();
		this.voodooCount = bb.readInt();
		this.voodooAng = bb.readInt();
		this.voodooUnk = bb.readInt();

		this.explosion = bb.readInt();
		this.tilt = bb.readInt();
		this.visibility = bb.readInt();
		this.hitEffect = bb.readInt();
		this.blindEffect = bb.readInt();
		this.drownEffect = bb.readInt();
		this.handCount = bb.readInt();
		this.handDamage = bb.readBoolean();
		this.pickupEffect = bb.readInt();
		this.fireEffect = bb.readInt();
		this.quakeTime = bb.readInt();
	}

	@Override
	public String toString()
	{
		StringBuilder out = new StringBuilder("NPSTART " + NPSTART + " \r\n");
		out.append("pWeaponQAV ").append(pWeaponQAV).append(" \r\n");
		out.append("weaponCallback ").append(weaponCallback).append(" \r\n");
		out.append("Run ").append(Run).append(" \r\n");
		out.append("moveState ").append(moveState).append(" \r\n");
		out.append("moveDist ").append(moveDist).append(" \r\n");
		out.append("bobAmp ").append(bobAmp).append(" \r\n");
		out.append("bobPhase ").append(bobPhase).append(" \r\n");
		out.append("bobHeight ").append(bobHeight).append(" \r\n");
		out.append("bobWidth ").append(bobWidth).append(" \r\n");
		out.append("swayAmp ").append(swayAmp).append(" \r\n");
		out.append("swayPhase ").append(swayPhase).append(" \r\n");
		out.append("swayHeight ").append(swayHeight).append(" \r\n");
		out.append("swayWidth ").append(swayWidth).append(" \r\n");
		out.append("nPlayer ").append(nPlayer).append(" \r\n");
		out.append("nSprite ").append(nSprite).append(" \r\n");
		out.append("nLifeMode ").append(nLifeMode).append(" \r\n");
		out.append("bloodlust ").append(bloodlust).append(" \r\n");
		out.append("viewOffZ ").append(viewOffZ).append(" \r\n");
		out.append("viewOffdZ ").append(viewOffdZ).append(" \r\n");
		out.append("weaponAboveZ ").append(weaponAboveZ).append(" \r\n");
		out.append("weapOffdZ ").append(weapOffdZ).append(" \r\n");
		out.append("look ").append(look).append(" \r\n");
		out.append("horiz ").append(horiz).append(" \r\n");
		out.append("slope ").append(slope).append(" \r\n");
		out.append("horizOff ").append(horizOff).append(" \r\n");
		out.append("Underwater ").append(Underwater).append(" \r\n");
		for (int j = 0; j < 8; j++)
			out.append("hasKey[").append(j).append("] ").append(hasKey[j]).append(" \r\n");
		out.append("hasFlag ").append(hasFlag).append(" \r\n");
		out.append("nBlueTeam ").append(nBlueTeam).append(" \r\n");
		out.append("nRedTeam ").append(nRedTeam).append(" \r\n");
		for (int j = 0; j < 7; j++)
			out.append("damageShift[").append(j).append("] ").append(damageShift[j]).append(" \r\n");
		out.append("currentWeapon ").append(currentWeapon).append(" \r\n");
		out.append("updateWeapon ").append(updateWeapon).append(" \r\n");
		out.append("weaponTimer ").append(weaponTimer).append(" \r\n");
		out.append("weaponState ").append(weaponState).append(" \r\n");
		out.append("weaponAmmo ").append(weaponAmmo).append(" \r\n");
		for(int j = 0; j < 14; j++)
			out.append("hasWeapon[").append(j).append("] ").append(hasWeapon[j]).append(" \r\n");
		for(int j = 0; j < 14; j++)
			out.append("weaponMode[").append(j).append("] ").append(weaponMode[j]).append(" \r\n");
		for(int j = 0; j < 14; j++)
			out.append("weaponOrder[0][").append(j).append("] ").append(weaponOrder[0][j]).append(" \r\n");
		for(int j = 0; j < 14; j++)
			out.append("weaponOrder[1][").append(j).append("] ").append(weaponOrder[1][j]).append(" \r\n");
		for(int j = 0; j < 12; j++)
			out.append("ammoCount[").append(j).append("] ").append(ammoCount[j]).append(" \r\n");
		out.append("fLoopQAV ").append(fLoopQAV).append(" \r\n");
		out.append("fuseTime ").append(fuseTime).append(" \r\n");
		out.append("fireClock ").append(fireClock).append(" \r\n");
		out.append("throwTime ").append(throwTime).append(" \r\n");
		out.append("aim[").append(aim.x).append(", ").append(aim.y).append(", ").append(aim.z).append("] \r\n");
		out.append("relAim[").append(relAim.x).append(", ").append(relAim.y).append(", ").append(relAim.z).append("] \r\n");
		out.append("nAimSprite ").append(nAimSprite).append(" \r\n");
		out.append("aimCount ").append(aimCount).append(" \r\n");
		for(int j = 0; j < 16; j++)
			out.append("aimSprites[").append(j).append("] ").append(aimSprites[j]).append(" \r\n");
		out.append("deathTime ").append(deathTime).append(" \r\n");
		for(int j = 0; j < kMaxPowerUps; j++)
			out.append("powerUpTimer[").append(j).append("] ").append(powerUpTimer[j]).append(" \r\n");
		out.append("fragCount ").append(fragCount).append(" \r\n");
		for(int j = 0; j < kMaxPlayers; j++)
			out.append("fragInfo[").append(j).append("] ").append(fragInfo[j]).append(" \r\n");

		out.append("teamID ").append(teamID).append(" \r\n");
		out.append("fraggerID ").append(fraggerID).append(" \r\n");
		out.append("airTime ").append(airTime).append(" \r\n");
		out.append("bloodTime ").append(bloodTime).append(" \r\n");
		out.append("gooTime ").append(gooTime).append(" \r\n");
		out.append("wetTime ").append(wetTime).append(" \r\n");
		out.append("bubbleTime ").append(bubbleTime).append(" \r\n");
		out.append("stayTime ").append(stayTime).append(" \r\n");
		out.append("kickTime ").append(kickTime).append(" \r\n");
		out.append("pLaughsCount ").append(pLaughsCount).append(" \r\n");
		out.append("TurnAround ").append(TurnAround).append(" \r\n");

		out.append("godMode ").append(godMode).append(" \r\n");
		out.append("fScreamed ").append(fScreamed).append(" \r\n");
		out.append("pJump ").append(pJump).append(" \r\n");
		out.append("showInventory ").append(showInventory).append(" \r\n");
		out.append("choosedInven ").append(choosedInven).append(" \r\n");
		for( int j = 0; j < 5; j++) {
			out.append("Inventory[").append(j).append("].activated ").append(Inventory[j].activated).append(" \r\n");
			out.append("Inventory[").append(j).append("].amount ").append(Inventory[j].amount).append(" \r\n");
		}
		for( int j = 0; j < 3; j++)
			out.append("ArmorAmount[").append(j).append("] ").append(ArmorAmount[j]).append(" \r\n");
		out.append("voodooTarget ").append(voodooTarget).append(" \r\n");
		out.append("voodooCount ").append(voodooCount).append(" \r\n");
		out.append("voodooAng ").append(voodooAng).append(" \r\n");
		out.append("voodooUnk ").append(voodooUnk).append(" \r\n");
		out.append("explosion ").append(explosion).append(" \r\n");
		out.append("tilt ").append(tilt).append(" \r\n");
		out.append("visibility ").append(visibility).append(" \r\n");
		out.append("hitEffect ").append(hitEffect).append(" \r\n");
		out.append("blindEffect ").append(blindEffect).append(" \r\n");
		out.append("drownEffect ").append(drownEffect).append(" \r\n");
		out.append("handCount ").append(handCount).append(" \r\n");
		out.append("handDamage ").append(handDamage).append(" \r\n");
		out.append("pickupEffect ").append(pickupEffect).append(" \r\n");
		out.append("fireEffect ").append(fireEffect).append(" \r\n");
		out.append("quakeTime ").append(quakeTime).append(" \r\n");

		return out.toString();
	}


























}
