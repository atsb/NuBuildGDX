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

import static ru.m210projects.Blood.Main.*;
import static ru.m210projects.Blood.Actor.gEffectInfo;
import static ru.m210projects.Blood.Actor.kFXMax;
import static ru.m210projects.Blood.DB.kDudeBase;
import static ru.m210projects.Blood.DB.kDudeMax;
import static ru.m210projects.Blood.Types.DudeInfo.dudeInfo;
import static ru.m210projects.Blood.Types.DudeInfo.gPlayerTemplate;
import static ru.m210projects.Blood.Types.Seq.SeqHandling.*;

import ru.m210projects.Blood.Types.CALLPROC;
import ru.m210projects.Build.Architecture.BuildGdx;

import ru.m210projects.Build.OnSceenDisplay.Console;

public class VERSION {

	public static int GAMEVER = -1;
	public static final int VER100 = 0;
	public static final int VER121 = 1;

	public static boolean SHAREWARE = false;
public static boolean hasQFN = false;

	private static final int[][] DudeInfoSEQ =
	{
		{0, 0}, //kDudeBase
		{256, 4096}, //kDudeTommyCultist
		{720, 11520}, //kDudeShotgunCultist
		{272, 4352}, //kDudeAxeZombie
		{288, 4608}, //kDudeButcher
		{272, 4352}, //kDudeEarthZombie
		{304, 4864}, //kDudeFleshGargoyle
		{320, 5120}, //kDudeStoneGargoyle
		{688, 11008}, //kDudeFleshStatue
		{704, 11264}, //kDudeStoneStatue
		{336, 5376}, //kDudePhantasm
		{352, 5632}, //kDudeHound
		{368, 5888}, //kDudeHand
		{384, 6144}, //kDudeBrownSpider
		{400, 6400}, //kDudeRedSpider
		{416, 6656}, //kDudeBlackSpider
		{432, 6912}, //kDudeMotherSpider
		{448, 7168}, //kDudeGillBeast
		{464, 7424}, //kDudeEel
		{480, 7680}, //kDudeBat
		{496, 7936}, //kDudeRat
		{512, 8192}, //kDudeGreenPod
		{528, 8448}, //kDudeGreenTentacle
		{544, 8704}, //kDudeFirePod
		{560, 8960}, //kDudeFireTentacle
		{576, 9216}, //kDudeMotherPod
		{592, 9472}, //kDudeFireTentacle
		{608, 9728}, //kDudeCerberus
		{624, 9984}, //kDudeCerberus2
		{640, 10240}, //kDudeTchernobog
		{656, 4096}, //kDudeFanaticProne
		{752, 12032}, //kDudePlayer1
		{752, 12032}, //kDudePlayer2
		{752, 12032}, //kDudePlayer3
		{752, 12032}, //kDudePlayer4
		{752, 12032}, //kDudePlayer5
		{752, 12032}, //kDudePlayer6
		{752, 12032}, //kDudePlayer7
		{752, 12032}, //kDudePlayer8
		{784, 12544}, //kDudeBurning
		{256, 4096}, //kDudeCultistBurning
		{272, 4352}, //kDudeAxeZombieBurning
		{288, 4352}, //kDudeBloatedButcherBurning
		{256, 4096}, //Reserved
		{272, 4352}, //kDudeSleepZombie
		{784, 12544}, //kDudeInnocent
		{384, 11520}, //kDudeCultistProne 246
		//Plasma Pak Dudes
		{0, 12800}, //kDudeTeslaCultist
		{0, 13056}, //kDudeDynamiteCultist
		{0, 13312}, //kDudeBeastCultist
		{0, 13568}, //kDudeTinyCaleb
		{0, 10752}, //kDudeTheBeast
		{0, 13568}, //kDudeTinyCalebburning
		{0, 10752}, //kDudeTheBeastburning 253
		// GDX Dudes
		{720, 11520}, //kGDXUniversalCultist 254
		{256, 4096}, //kGDXGenDudeBurning 255
	};

	private static final int[][] gPlayerDudeSeq = {
		{752, 12032},
		{672, 10496},
	};

	private static final int[][] gEffectSEQ =
	{
		{ 4107, 49 },
		{ 4108, 50 },
		{ 4109, 51 },
		{ 4110, 52 },
		{ 7, 7 },
		{ 4102, 44 },
		{ 4103, 45 },
		{ 4104, 46 },
		{ 6, 6 },
		{ 4099, 42 },
		{ 4100, 43 },
		{ 4106, 48 },
		{ 4118, 60 },
		{ 0, 0 },
		{ 0, 0 },
		{ 0, 0 },
		{ 0, 0 },
		{ 0, 0 },
		{ 0, 0 },
		{ 0, 0 },
		{ 0, 0 },
		{ 0, 0 },
		{ 0, 0 },
		{ 0, 0 },
		{ 0, 0 },
		{ 0, 0 },
		{ 0, 0 },
		{ 0, 0 },
		{ 0, 0 },
		{ 4105,47 },
		{ 0, 0 },
		{ 0, 0 },
		{ 4113, 55 },
		{ 4114, 56 },
		{ 4115, 57 },
		{ 4116, 58 },
		{ 0, 0 },
		{ 4120, 62 },
		{ 4121, 63 },
		{ 4122, 64 },
		{ 4123, 65 },// 40
		{ 4124, 66 },
		{ 4125, 67 },
		{ 0, 0 },// BullerHole
		{ 0, 0 },
		{ 0, 0 },
		{ 0, 0 },
		{ 0, 0 },
		{ 8, 8 },
		{ 11, 11 },
		{ 11, 11 },
		{ 0, 0 },
		{ 30, 30 },
		{ 0, 0 },
		{ 0, 0 },
		{ 0, 0 },
		{ 4113, 70 },
	};

	public static final int kFireTrap1 = 0;
	public static final int kFireTrap2 = 1;
	public static final int kPadlock = 2;
	public static final int kMGunOpen = 3;
	public static final int kMGunFire = 4;
	public static final int kMGunClose = 5;
	public static final int kNapalm = 6;
	public static final int kCultProneOffset = 7;

	public static final int kPlayerIdle = 0;
	public static final int kPlayerDying = 1;
	public static final int kPlayerExplode = 2;
	public static final int kPlayerBurn = 3;
	public static final int kPlayerRecoil = 5;
	public static final int kPlayerWalk = 6;
	public static final int kPlayerSwim = 7;
	public static final int kPlayerCrouch = 8;
	public static final int kPlayerFatalityDead = 9;
	public static final int kPlayerDead = 10;
	public static final int kPlayerFatality = 12;
	public static final int kPlayerFDying = 13;
	public static final int kPlayerFSpirit = 14;

	private static final int[][] kPlayerSeq = {
		{0,0}, //idle
		{1,1}, //vector
		{2,2}, //explode
		{3,3}, //burn
		{4,4}, //e1
		{5,5}, //recoil
		{8,8}, //walk
		{12,9}, //swim
		{13,10}, //crouch
		{14,14}, //fatality dead
		{15, 15}, //dead
		{16, 12}, //d3b
		{17, 16}, //fatality dying
		{18, 17}, //fatality explode
		{19, 18}, //fatality spirit
	};

	private static final int[][] kSeqData = {
		{137, 35}, //kFireTrap1
		{138, 36}, //kFireTrap2
		{139, 37}, //kPadlock
		{140, 38}, //kMGunOpen
		{141, 39}, //kMGunFire
		{142, 40}, //kMGunClose
		{4119, 61}, //kTchernobog
		{8, 17}, //kCultProne
	};

	public static int getSeq(int kSeq)
	{
		return kSeqData[kSeq][GAMEVER];
	}

	public static CALLPROC getCallback(int callback)
	{
		switch(callback)
		{
			case 42: return callbacks[FireballTrapCallback];
			case 43: return callbacks[MGunFireCallback];
			case 44: return callbacks[MGunOpenCallback];
			case 45: //Player live again
			case 46: return callbacks[ReviveCallback];
			case 47: return callbacks[FireballCallback];
			case 48: return callbacks[TchernobogCallback2];
			case 49: return callbacks[SmokeCallback];
			case 50: return callbacks[TchernobogCallback1];
			case 51: return callbacks[DamageTreeCallback];
			case 52: return callbacks[DamageDude];
			case 53: return callbacks[DamageDude2];
		}

		game.dassert("Unknown seq action! Callback: " + callback);
		return null;
	}

	public static int getPlayerSeq(int kSeq)
	{
		return kPlayerSeq[kSeq][GAMEVER];
	}

	public static void versionInit()
	{
		//find player seqid
		if(BuildGdx.cache.contains(752, "SEQ")) {
			GAMEVER = VER100;
			Console.Println("Version < v1.10 found");
		}
		else if(BuildGdx.cache.contains(12032, "SEQ")) {
			GAMEVER = VER121;
			Console.Println("Version >= v1.10 found");
		}
		else {
			if(BuildGdx.cache.getGroup("BLOOD.RFF") != null)
				game.dassert("Version init error: unknown Blood version!");
			else game.dassert("Blood data file not found!");
		}

		if(BuildGdx.cache.contains(0, "qfn"))
			hasQFN = true;

		for(int i = 0; i < (kDudeMax - kDudeBase); i++)
			dudeInfo[i].seqStartID = DudeInfoSEQ[i][GAMEVER];
		for(int i = 0; i < 2; i++)
			gPlayerTemplate[i].seqStartID = gPlayerDudeSeq[i][GAMEVER];
		for(int i = 0; i < (kFXMax); i++)
			gEffectInfo[i].seqId = gEffectSEQ[i][GAMEVER];
	}

	public static final int kNAPUP = 0;
	public static final int kNAPIDLE = 1;
	public static final int kNAPFIRE = 2;
	public static final int kNAPDOWN = 3;
	public static final int kBSTUP = 4;
	public static final int kBSTIDLE = 5;
	public static final int kBSTATAK1 = 6; // - BSTATAK4
	public static final int kBSTDOWN = 7;
	public static final int kVDUP = 8;
	public static final int kVDIDLE1 = 9;
	public static final int kVDIDLE2 = 10;
	public static final int kVDFIRE1 = 11; // - VDFIRE6
	public static final int kVDDOWN = 12;
	public static final int kVDSPEL1 = 13;
	public static final int kSTAFUP = 14;
	public static final int kSTAFIDL1 = 15;
	public static final int kSTAFIRE1 = 16;
	public static final int kSTAFIRE4 = 17;
	public static final int kSTAFPOST = 18;
	public static final int kSTAFDOWN = 19;
	public static final int k2NAPUP = 20;
	public static final int k2NAPIDLE = 21;
	public static final int k2NAPFIRE = 22;
	public static final int k2NAPFIR2 = 23;
	public static final int k2NAPDOWN = 24;
	public static final int k2SGUNUP = 25;
	public static final int k2SGUNIDL = 26;
	public static final int k2SGUNFIR = 27;
	public static final int k2SGUNALT = 28;
	public static final int k2SGUNPRE = 29;
	public static final int k2SGUNPST = 30;
	public static final int k2SGUNDWN = 31;


	private static final int[][] kQavData = {
		{82, 89}, //0
		{83, 90}, //1
		{84, 91}, //2
		{85, 92}, //3
		{86, 93}, //4
		{87, 94}, //5
		{88, 95}, //6
		{92, 99}, //7
		{93, 100}, //8
		{94, 101}, //9
		{95, 102}, //10
		{96, 103}, //11
		{102, 109}, //12
		{103, 110}, //13
		{104, 111}, //14
		{105, 112}, //15
		{107, 114}, //16
		{109, 116}, //17
		{111, 118}, //18
		{112, 119}, //19
		{113, 120}, //20
		{114, 121}, //21
		{115, 122}, //22
		{116, 123}, //23
		{117, 124}, //24
		{0, 82}, //25
		{0, 83}, //26
		{0, 84}, //27
		{0, 85}, //28
		{0, 86}, //29
		{0, 87}, //30
		{0, 88}, //31
	};

	public static int getQAV(int nQavId)
	{
		return kQavData[nQavId][GAMEVER];
	}
}
