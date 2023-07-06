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

import static ru.m210projects.Blood.Types.DEMO.*;

import static ru.m210projects.Blood.Actor.*;
import static ru.m210projects.Blood.DB.*;
import static ru.m210projects.Blood.EVENT.checkEventList;
import static ru.m210projects.Blood.EVENT.evPost;
import static ru.m210projects.Blood.EVENT.evPostCallback;
import static ru.m210projects.Blood.EVENT.evSend;
import static ru.m210projects.Blood.EVENT.kCommandOn;
import static ru.m210projects.Blood.EVENT.kCommandSpriteProximity;
import static ru.m210projects.Blood.EVENT.kCommandSpritePush;
import static ru.m210projects.Blood.EVENT.kPQueueSize;
import static ru.m210projects.Blood.Gameutils.BiRandom;
import static ru.m210projects.Blood.Gameutils.BiRandom2;
import static ru.m210projects.Blood.Gameutils.ClipHigh;
import static ru.m210projects.Blood.Gameutils.ClipLow;
import static ru.m210projects.Blood.Gameutils.GetSpriteExtents;
import static ru.m210projects.Blood.Gameutils.IsPlayerSprite;
import static ru.m210projects.Blood.Gameutils.M2X;
import static ru.m210projects.Blood.Gameutils.Random;
import static ru.m210projects.Blood.Gameutils.bRandom;
import static ru.m210projects.Blood.Gameutils.extents_zBot;
import static ru.m210projects.Blood.Gameutils.extents_zTop;
import static ru.m210projects.Blood.Globals.*;
import static ru.m210projects.Blood.Main.*;
import static ru.m210projects.Blood.VERSION.*;
import static ru.m210projects.Blood.PLAYER.kAmmoNone;
import static ru.m210projects.Blood.PLAYER.kWeaponBeast;
import static ru.m210projects.Blood.PLAYER.kWeaponFlare;
import static ru.m210projects.Blood.PLAYER.kWeaponLifeLeach;
import static ru.m210projects.Blood.PLAYER.kWeaponNapalm;
import static ru.m210projects.Blood.PLAYER.kWeaponPitchfork;
import static ru.m210projects.Blood.PLAYER.kWeaponProxyTNT;
import static ru.m210projects.Blood.PLAYER.kWeaponRemoteTNT;
import static ru.m210projects.Blood.PLAYER.kWeaponShotgun;
import static ru.m210projects.Blood.PLAYER.kWeaponSprayCan;
import static ru.m210projects.Blood.PLAYER.kWeaponTNT;
import static ru.m210projects.Blood.PLAYER.kWeaponTesla;
import static ru.m210projects.Blood.PLAYER.kWeaponTommy;
import static ru.m210projects.Blood.PLAYER.kWeaponVoodoo;
import static ru.m210projects.Blood.PLAYER.newHoriz;
import static ru.m210projects.Blood.PLAYER.playerFireMissile;
import static ru.m210projects.Blood.PLAYER.playerFireThing;
import static ru.m210projects.Blood.PLAYER.powerupActivate;
import static ru.m210projects.Blood.PLAYER.powerupCheck;
import static ru.m210projects.Blood.PLAYER.weaponQAVs;
import static ru.m210projects.Blood.QAV.*;
import static ru.m210projects.Blood.SOUND.sfxKill3DSound;
import static ru.m210projects.Blood.SOUND.sfxStart3DSound;
import static ru.m210projects.Blood.Strings.qav;
import static ru.m210projects.Blood.Tile.tileLoadVoxel;
import static ru.m210projects.Blood.Trig.Cos;
import static ru.m210projects.Blood.Trig.RotateVector;
import static ru.m210projects.Blood.Trig.Sin;
import static ru.m210projects.Blood.Trig.rotated;
import static ru.m210projects.Blood.Types.DudeInfo.dudeInfo;
import static ru.m210projects.Blood.Types.Seq.SeqHandling.seqSpawn;
import static ru.m210projects.Blood.VERSION.VER100;
import static ru.m210projects.Blood.VERSION.getQAV;
import static ru.m210projects.Blood.VERSION.k2NAPDOWN;
import static ru.m210projects.Blood.VERSION.k2NAPFIRE;
import static ru.m210projects.Blood.VERSION.k2NAPIDLE;
import static ru.m210projects.Blood.VERSION.k2NAPUP;
import static ru.m210projects.Blood.VERSION.kBSTATAK1;
import static ru.m210projects.Blood.VERSION.kBSTDOWN;
import static ru.m210projects.Blood.VERSION.kBSTIDLE;
import static ru.m210projects.Blood.VERSION.kBSTUP;
import static ru.m210projects.Blood.VERSION.kNAPDOWN;
import static ru.m210projects.Blood.VERSION.kNAPFIRE;
import static ru.m210projects.Blood.VERSION.kNAPIDLE;
import static ru.m210projects.Blood.VERSION.kNAPUP;
import static ru.m210projects.Blood.VERSION.kSTAFDOWN;
import static ru.m210projects.Blood.VERSION.kSTAFIDL1;
import static ru.m210projects.Blood.VERSION.kSTAFIRE1;
import static ru.m210projects.Blood.VERSION.kSTAFIRE4;
import static ru.m210projects.Blood.VERSION.kSTAFPOST;
import static ru.m210projects.Blood.VERSION.kSTAFUP;
import static ru.m210projects.Blood.VERSION.kVDDOWN;
import static ru.m210projects.Blood.VERSION.kVDFIRE1;
import static ru.m210projects.Blood.VERSION.kVDIDLE1;
import static ru.m210projects.Blood.VERSION.kVDIDLE2;
import static ru.m210projects.Blood.VERSION.kVDSPEL1;
import static ru.m210projects.Blood.VERSION.kVDUP;
import static ru.m210projects.Blood.View.kStatBarHeight;
import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Build.Pragmas.divscale;
import static ru.m210projects.Build.Pragmas.klabs;
import static ru.m210projects.Build.Pragmas.mulscale;
import static ru.m210projects.Build.Net.Mmulti.numplayers;

import ru.m210projects.Blood.Types.DudeInfo;
import ru.m210projects.Blood.Types.QAVCALLBACKFUNC;
import ru.m210projects.Blood.Types.TeslaData;
import ru.m210projects.Blood.Types.WEAPONDATA;
import ru.m210projects.Blood.Types.WeaponAim;
import ru.m210projects.Blood.Types.XSPRITE;
import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.FileHandle.Resource;
import ru.m210projects.Build.Types.SPRITE;

public class Weapon {
	public static final int kWeaponMax = 14;
	public static final int kFindWeapon = -1;

	public static final int kQAVEnd = 126; // 118 - v1.0; 125 - v1.21
	public static final int kQAVEnd100 = 118;

	public static final int kQAVSprayDown = 125;

	public static final int kQAVForkUp = 0;
	public static final int kQAVForkIdle = 1;
	public static final int kQAVForkStab = 2;
	public static final int kQAVForkDown = 3;

	public static final int kQAVFlareUp = 41;
	public static final int kQAVFlareIdle = 42;
	public static final int kQAVFlareFire = 43;
	public static final int kQAVFlareDown = 44;

	public static final int kQAV2FlareUp = 45;
	public static final int kQAV2FlareIdle = 46;
	public static final int kQAV2FlareFire1 = 47;
	public static final int kQAV2FlareFire2 = 48;
	public static final int kQAV2FlareDown = 49;

	public static final int kQAVShotgunUp = 50;
	public static final int kQAVShotgunIdle3 = 51;
	public static final int kQAVShotgunIdle2 = 52;
	public static final int kQAVShotgunIdle1 = 53;
	public static final int kQAVShotgunFire1 = 54;
	public static final int kQAVShotgunFire2 = 55;
	public static final int kQAVShotgunFire3 = 56;
	public static final int kQAVShotgunReload = 57;
	public static final int kQAVShotgunDown = 58;

	public static final int kQAV2ShotgunUp = 59;
	public static final int kQAV2ShotgunIdle = 60;
	public static final int kQAV2ShotgunFire2 = 61;
	public static final int kQAV2ShotgunFire1 = 62;
	public static final int kQAV2ShotgunDown = 63;

	public static final int kQAVTommygunUp = 64;
	public static final int kQAVTommygunIdle = 65;
	public static final int kQAVTommygunFire = 66;
	public static final int kQAVTommygunFire2 = 67;
	public static final int kQAVTommygunDown = 68;
	public static final int kQAV2TommygunUp = 69;
	public static final int kQAV2TommygunIdle = 70;
	public static final int kQAV2TommygunFire = 71;
	public static final int kQAV2TommygunDown = 72;
	public static final int kQAV2TommygunFire2 = 73;

	public final static WEAPONDATA[] gWeaponInfo = { new WEAPONDATA(0, -1),
			new WEAPONDATA(1, -1), new WEAPONDATA(1, 1), new WEAPONDATA(1, 2),
			new WEAPONDATA(1, 3), new WEAPONDATA(1, 4), new WEAPONDATA(1, 5),
			new WEAPONDATA(1, 6), new WEAPONDATA(1, 7), new WEAPONDATA(1, 8),
			new WEAPONDATA(1, 9), new WEAPONDATA(1, 10), new WEAPONDATA(1, 11),
			new WEAPONDATA(0, -1), };

	public final static int[] gNextWeapon = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 1, 1 };
	public final static int[] gPrevWeapon = { 12, 12, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 1 };
	public static final int[] defaultOrder = { 3, 4, 2, 8, 9, 10, 7, 1, 1, 1, 1, 1, 1, 1 };

	public static final boolean[][] pWeapUpgrade = {
			// 0 1 2 3 4 5 6 7 8 9 10 11 12
			{ false, true, true, true, true, true, true, true, true, true, true, true, true },
			{ false, true, true, true, true, true, true, true, true, true, true, true, true },
			{ false, false, true, true, true, false, false, false, false, false, false, false, false },
			{ false, false, false, true, true, false, false, false, false, false, false, false, false },
			{ false, false, false, false, true, false, false, false, false, false, false, false, false },
			{ false, false, false, false, false, true, false, false, false, false, false, false, false },
			{ false, false, true, true, true, false, true, false, false, false, false, false, false },
			{ false, false, true, true, true, false, false, true, false, false, false, false, false },
			{ false, false, false, false, false, false, false, false, true, false, false, false, false },
			{ false, false, false, false, false, false, false, false, true, true, false, false, false },
			{ false, false, false, true, true, false, false, false, false, false, true, false, false },
			{ false, false, false, false, false, false, false, false, false, false, false, true, false },
			{ false, false, false, false, false, false, false, false, false, false, false, false, true }, };

	public static final int kMaxClients = 64;
	public static final int kAimMaxDist = M2X(100);
	public static final int kAimMaxSlope = 9460;

	public static WeaponAim[] gWeaponAimData = { new WeaponAim(0, 0, 0, 0, 0), //1.21 ok
			new WeaponAim(24576, 24576, 113, 85, 1118481),
			new WeaponAim(32768, 32768, 113, 85, 2796202),
			new WeaponAim(65536, 65536, 56, 28, 0),
			new WeaponAim(24576, 32768, 56, 28, 0),
			new WeaponAim(24576, 24576, 56, 28, 2796202),
			new WeaponAim(24576, 24576, 113, 85, 0),
			new WeaponAim(24576, 24576, 113, 56, 0),
			new WeaponAim(32768, 65536, 113, 85, 2446677),
			new WeaponAim(65536, 65536, 113, 0, 0),
			new WeaponAim(65536, 65536, 170, 0, 0),
			new WeaponAim(24576, 24576, 113, 85, 0),
			new WeaponAim(24576, 24576, 113, 85, 0),
			new WeaponAim(24576, 24576, 113, 85, 0) };

	public static int[] gVoodooData = { 0x0A000, 0x0C000, 0x0E000, 0x10000 };

	public static final TeslaData[] gTeslaData = {
		new TeslaData (0, 306, 1, 470, 20, 1),
		new TeslaData (-140, 306, 1, 470, 30, 1),
		new TeslaData (140, 306, 1, 470, 30, 1),
		new TeslaData (0, 302, 35, 471, 40, 1),
		new TeslaData (-140, 302, 35, 471, 50, 1),
		new TeslaData (140, 302, 35, 471, 50, 1)
	};

	public static QAVCALLBACKFUNC[] gWeaponCallback = {
	   new QAVCALLBACKFUNC() {
		@Override
		public void run(int triggerId, Object data) {
			FirePitchfork(triggerId, (PLAYER) data); //0
		}
	}, new QAVCALLBACKFUNC() {
		@Override
		public void run(int triggerId, Object data) {
			FireSpray(triggerId, (PLAYER) data); //1
		}
	}, new QAVCALLBACKFUNC() {
		@Override
		public void run(int triggerId, Object data) {
			ThrowSpray(triggerId, (PLAYER) data); //2
		}
	}, new QAVCALLBACKFUNC() {
		@Override
		public void run(int triggerId, Object data) {
			DropSpray(triggerId, (PLAYER) data); //3
		}
	}, new QAVCALLBACKFUNC() {
		@Override
		public void run(int triggerId, Object data) {
			ExplodeSpray(triggerId, (PLAYER) data); //4
		}
	}, new QAVCALLBACKFUNC() {
		@Override
		public void run(int triggerId, Object data) {
			ThrowBundle(triggerId, (PLAYER) data); //5
		}
	}, new QAVCALLBACKFUNC() {
		@Override
		public void run(int triggerId, Object data) {
			DropBundle(triggerId, (PLAYER) data); //6
		}
	}, new QAVCALLBACKFUNC() {
		@Override
		public void run(int triggerId, Object data) {
			ExplodeBundle(triggerId, (PLAYER) data); //7
		}
	}, new QAVCALLBACKFUNC() {
		@Override
		public void run(int triggerId, Object data) {
			ThrowProxy(triggerId, (PLAYER) data); //8
		}
	}, new QAVCALLBACKFUNC() {
		@Override
		public void run(int triggerId, Object data) {
			DropProxy(triggerId, (PLAYER) data); //9
		}
	}, new QAVCALLBACKFUNC() {
		@Override
		public void run(int triggerId, Object data) {
			ThrowRemote(triggerId, (PLAYER) data); //10
		}
	}, new QAVCALLBACKFUNC() {
		@Override
		public void run(int triggerId, Object data) {
			DropRemote(triggerId, (PLAYER) data); //11
		}
	}, new QAVCALLBACKFUNC() {
		@Override
		public void run(int triggerId, Object data) {
			FireRemote(triggerId, (PLAYER) data); //12
		}
	}, new QAVCALLBACKFUNC() {
		@Override
		public void run(int triggerId, Object data) {
			FireShotgun(triggerId, (PLAYER) data); //13
		}
	}, new QAVCALLBACKFUNC() {
		@Override
		public void run(int triggerId, Object data) {
			ShotgunReload(triggerId, (PLAYER) data); //14
		}
	}, new QAVCALLBACKFUNC() {
		@Override
		public void run(int triggerId, Object data) {
			FireTommy(triggerId, (PLAYER) data); //15
		}
	}, new QAVCALLBACKFUNC() {
		@Override
		public void run(int triggerId, Object data) {
			FireTommy2(triggerId, (PLAYER) data); //16
		}
	}, new QAVCALLBACKFUNC() {
		@Override
		public void run(int triggerId, Object data) {
			FireATommy2(triggerId, (PLAYER) data); //17
		}
	}, new QAVCALLBACKFUNC() {
		@Override
		public void run(int triggerId, Object data) {
			FireFlare(triggerId, (PLAYER) data); //18
		}
	}, new QAVCALLBACKFUNC() {
		@Override
		public void run(int triggerId, Object data) {
			FireFlare2(triggerId, (PLAYER) data); //19
		}
	}, new QAVCALLBACKFUNC() {
		@Override
		public void run(int triggerId, Object data) {
			FireVoodoo(triggerId, (PLAYER) data); //20
		}
	}, new QAVCALLBACKFUNC() {
		@Override
		public void run(int triggerId, Object data) {
			FireVoodoo2(triggerId, (PLAYER) data); //21
		}
	}, new QAVCALLBACKFUNC() {
		@Override
		public void run(int triggerId, Object data) {
			FireTesla_100(triggerId, (PLAYER) data); //22
		}
	}, new QAVCALLBACKFUNC() {
		@Override
		public void run(int triggerId, Object data) {
			if(GAMEVER == VER100)
				FireTesla2_100(triggerId, (PLAYER) data); //23
			else
				FireTesla121(triggerId, (PLAYER) data);
		}
	}, new QAVCALLBACKFUNC() {
		@Override
		public void run(int triggerId, Object data) {
			FireNapalm(triggerId, (PLAYER) data); //24
		}
	}, new QAVCALLBACKFUNC() {
		@Override
		public void run(int triggerId, Object data) {
			FireNapalm2(triggerId, (PLAYER) data); //25
		}
	}, new QAVCALLBACKFUNC() {
		@Override
		public void run(int triggerId, Object data) {
			FireLeech(triggerId, (PLAYER) data); //26
		}
	}, new QAVCALLBACKFUNC() {
		@Override
		public void run(int triggerId, Object data) {
			System.out.println("weaponCallback 27"); //27
		}
	}, new QAVCALLBACKFUNC() {
		@Override
		public void run(int triggerId, Object data) {
			FireBeast(triggerId, (PLAYER) data); //28
		}

	},};

	public static void InitSprayFix(int nSprayId) {
		Resource hQAV = BuildGdx.cache.open(nSprayId, qav);
		if (hQAV != null) {
			weaponQAVs[nSprayId] = new QAV(hQAV);
			hQAV.close();
		}
	}

	public static void WeaponInit() {
		// get pointers to all the QAV resources
		for (int i = 0; i < kQAVEnd; i++) {
			if (GAMEVER == VER100 && i >= kQAVEnd100)
				return;

			Resource hQAV = BuildGdx.cache.open(i, qav);
			if (hQAV == null) {
				System.err.println("Could not load weapon " + i + ".QAV");
				continue;
			}
			weaponQAVs[i] = new QAV(hQAV);
			hQAV.close();
		}

		InitSprayFix(kQAVSprayDown);
	}

	public static int WeaponUpgrade(PLAYER pPlayer, int nWeapon) {
		int out = pPlayer.currentWeapon;
		if (!checkFired6or7(pPlayer)
				&& pWeapUpgrade[pPlayer.currentWeapon][nWeapon])
			out = nWeapon;

		return out;
	}

	public static boolean CheckNewWeapon(PLAYER pPlayer, int nWeapon,
			int nAmmo, int minAmmo) {

		if (gInfiniteAmmo)
			return true;

		if (nAmmo == -1)
			return true;

		if (nWeapon != 12 || pPlayer.weaponAmmo != 11
				|| pPlayer.weaponState != 11) {
			if (nWeapon == 9 && pPlayer.pXsprite.health != 0)
				return true;
			else
				return pPlayer.ammoCount[nAmmo] >= minAmmo;
		}
		return true;
	}

	public static boolean CheckAmmo2(PLAYER pPlayer, int nAmmo, int minAmmo) {
		if (gInfiniteAmmo)
			return true;

		if (nAmmo == kAmmoNone)
			return true;

		return pPlayer.ammoCount[nAmmo] >= minAmmo;
	}

	public static boolean CheckAmmo(PLAYER pPlayer, int nAmmo, int minAmmo) {
		if (gInfiniteAmmo)
			return true;

		if (nAmmo == kAmmoNone)
			return true;

		if (pPlayer.currentWeapon != 12 || pPlayer.weaponAmmo != 11
				|| pPlayer.weaponState != 11) {
			if (pPlayer.currentWeapon == 9 && pPlayer.pXsprite.health != 0)
				return true;
			else
				return pPlayer.ammoCount[nAmmo] >= minAmmo;
		}
		return true;
	}

	public static int ProxyProcess(PLAYER pPlayer) {
		if (pPlayer.weaponState == 9) {
			pPlayer.throwTime = ClipHigh(
					divscale(gFrameClock - pPlayer.fireClock, 240, 16),
					65536);
			if (!(pPlayer.pInput.Shoot)) {
				pPlayer.weaponState = 8;
				StartQAV(pPlayer, 29, 8, false);
			}
			return 1;
		} else
			return 0;
	}

	public static int RemoteProcess(PLAYER pPlayer) {
		if (pPlayer.weaponState == 13) {
			pPlayer.throwTime = ClipHigh(
					divscale(gFrameClock - pPlayer.fireClock, 240, 16),
					65536);
			if (!(pPlayer.pInput.Shoot)) {
				pPlayer.weaponState = 11;
				StartQAV(pPlayer, 39, 10, false);
			}
			return 1;
		} else
			return 0;
	}

	public static int TeslaProcess(PLAYER pPlayer) {
		if (pPlayer.weaponState < 5) {
			if (pPlayer.weaponState == 4) {
				pPlayer.weaponState = 5;

				if(GAMEVER != VER100) {
					if ( CheckAmmo2(pPlayer, 7, 10) && powerupCheck(pPlayer, kItemGunsAkimbo - kItemBase) != 0 )
						StartQAV(pPlayer, 84, 23, true);
				    else
				    	StartQAV(pPlayer, 77, 23, true);
				} else StartQAV(pPlayer, 77, 22, true);

				return 1;
			}
			return 0;
		}

		if (pPlayer.weaponState == 7 || (!(pPlayer.pInput.Shoot) && pPlayer.weaponState == 5)) {
			pPlayer.weaponState = 2;

			if ( GAMEVER != VER100 && CheckAmmo2(pPlayer, 7, 10) && powerupCheck(pPlayer, kItemGunsAkimbo - kItemBase) != 0 )
				StartQAV(pPlayer, 87, -1, false);
		    else
		    	StartQAV(pPlayer, 80, -1, false);
			return 1;
		}
		return 0;
	}

	public static int LeachProcess(PLAYER pPlayer) {
		if (pPlayer.weaponState < 6) {
			if (pPlayer.weaponState == 4) {
				pPlayer.weaponState = 6;
				StartQAV(pPlayer, getQAV(kSTAFIRE1), 26, true);
				return 1;
			}
			return 0;
		}
		if (pPlayer.weaponState > 6) {
			if (pPlayer.weaponState != 8)
				return 0;
			pPlayer.weaponState = 2;
			StartQAV(pPlayer, getQAV(kSTAFPOST), -1, false);
			return 1;
		}
		if (!(pPlayer.pInput.AltShoot)) {
			pPlayer.weaponState = 2;
			StartQAV(pPlayer, getQAV(kSTAFPOST), -1, false);
			return 1;
		}
		return 0;
	}

	public static int TNTProcess(PLAYER pPlayer) {
		int nState = pPlayer.weaponState;
		if (nState < 5) {
			if (nState != 4)
				return 0;

			if (!pPlayer.pInput.AltShoot)
				pPlayer.weaponState = 5;
			return 1;
		} else if (nState == 5) {
			if (pPlayer.pInput.AltShoot) {
				pPlayer.weaponState = 1;
				pPlayer.fuseTime = pPlayer.weaponTimer;
				StartQAV(pPlayer, 22, 6, false); // drop
			} else if (pPlayer.pInput.Shoot) {
				pPlayer.weaponState = 6;
				pPlayer.fuseTime = 0;
				pPlayer.fireClock = gFrameClock;
			}
			return 1;
		} else {
			if (nState != 6)
				return 0;

			pPlayer.throwTime = ClipHigh(
					divscale(gFrameClock - pPlayer.fireClock, 240, 16),
					65536);

			if (!(pPlayer.pInput.Shoot)) {
				if (pPlayer.fuseTime == 0)
					pPlayer.fuseTime = pPlayer.weaponTimer;
				pPlayer.weaponState = 1;
				StartQAV(pPlayer, 23, 5, false); // throw
			}
			return 1;
		}
	}

	public static int SprayProcess(PLAYER pPlayer) {
		int nState = pPlayer.weaponState;
		if (nState < 6) {
			if (nState != 5)
				return 0;

			if (!pPlayer.pInput.AltShoot)
				pPlayer.weaponState = 6;
			return 1;
		} else if (nState == 6) {
			if (pPlayer.pInput.AltShoot) {
				pPlayer.weaponState = 3;
				pPlayer.fuseTime = pPlayer.weaponTimer;
				StartQAV(pPlayer, 13, 3, false); // drop
			} else if (pPlayer.pInput.Shoot) {
				pPlayer.weaponState = 7;
				pPlayer.fuseTime = 0;
				pPlayer.fireClock = gFrameClock;
			}
			return 1;
		} else {
			if (nState != 7)
				return 0;
			pPlayer.throwTime = ClipHigh(
					divscale(gFrameClock - pPlayer.fireClock, 240, 16),
					65536);
			if (!(pPlayer.pInput.Shoot)) {
				if (pPlayer.fuseTime == 0)
					pPlayer.fuseTime = pPlayer.weaponTimer;
				pPlayer.weaponState = 1;
				StartQAV(pPlayer, 14, 2, false); // throw
			}
			return 1;
		}
	}

	public static int WeaponChange(PLAYER pPlayer, boolean next) {
		int weap = pPlayer.currentWeapon;
		do {
			if (next)
				weap = gNextWeapon[weap];
			else
				weap = gPrevWeapon[weap];

			if(pPlayer.Underwater) {
				while(weap == 6 || weap == 7) {
					if (next)
						weap = gNextWeapon[weap];
					else
						weap = gPrevWeapon[weap];
				}
			}

			if (gWeaponInfo[weap].update != 0 && pPlayer.hasWeapon[weap])
		    {
		    	int ammoType = gWeaponInfo[weap].ammoType;
		    	if ( weap == kWeaponVoodoo ? CheckAmmo(pPlayer, ammoType, 1) : CheckAmmo2(pPlayer, ammoType, 1) )
		    		break;
		    }
		} while (pPlayer.currentWeapon != weap);

		if (pPlayer.currentWeapon == weap
				&& (gWeaponInfo[weap].update == 0 || !CheckAmmo(pPlayer,
						gWeaponInfo[weap].ammoType, 1)))
			return 1;

		return weap;
	}

	public static void UpdateAimVector(PLAYER pPlayer) {
		if(pPlayer == null)game.dassert("pPlayer != NULL");
		SPRITE pSprite = pPlayer.pSprite;

		int ax, ay, az;

		int x = pSprite.x;
		int y = pSprite.y;
		int z = pPlayer.weaponAboveZ;

		ax = Cos(pSprite.ang) >> 16;
		ay = Sin(pSprite.ang) >> 16;
		az = (int) pPlayer.horizOff;

		WeaponAim pAimData = gWeaponAimData[pPlayer.currentWeapon];

		int nAimSprite = -1;

		pPlayer.aimCount = 0;
		boolean autoAim = (game.net.gProfile[pPlayer.nPlayer].autoaim || (pPlayer.currentWeapon == kWeaponVoodoo)
				|| pPlayer.currentWeapon == kWeaponLifeLeach || IsOriginalDemo());

		if (autoAim) {
			int closest = 0x7FFFFFFF;
			for (int nTarget = headspritestat[kStatDude]; nTarget >= 0; nTarget = nextspritestat[nTarget]) {
				SPRITE pTarget = sprite[nTarget];

				// don't target yourself!
				if (pTarget == pSprite)
					continue;

				if(!IsOriginalDemo() && IsPlayerSprite(pTarget) && pGameInfo.nFriendlyFire == 0
						&& (pGameInfo.nGameType == kNetModeCoop
						|| (pGameInfo.nGameType == kNetModeTeams
						&& pPlayer.teamID == gPlayer[pTarget.lotag - kDudePlayer1].teamID)))
					continue;

				if ((pTarget.hitag & kAttrFree) != 0)
					continue;

				if(!IsOriginalDemo()) {
					if ((pTarget.hitag & kAttrAiming) == 0
							&& pTarget.lotag != kDudeBrownSpider
							&& pTarget.lotag != kDudeRedSpider
							&& pTarget.lotag != kDudeBlackSpider) {
						continue;
					}
				} else if ((pTarget.hitag & kAttrAiming) == 0)
					continue;

				int tx, ty, tz;

				tx = pTarget.x;
				ty = pTarget.y;
				tz = pTarget.z;

				int dist = (int) engine.qdist(tx - x, ty - y);
				if (dist == 0 || dist > kAimMaxDist)
					continue;

				if (pAimData.kSeeker != 0) {
					int kSeek = (dist << 12) / pAimData.kSeeker;
					tx += kSeek * sprXVel[nTarget] >> 12;
					ty += kSeek * sprYVel[nTarget] >> 12;
					tz += kSeek * sprZVel[nTarget] >> 8;
				}

				int dx = mulscale(dist, Cos(pSprite.ang), 30) + x;
				int dy = mulscale(dist, Sin(pSprite.ang), 30) + y;

				int z1 = mulscale(dist, (int) pPlayer.horizOff, 10) + z;
				int z2 = mulscale(kAimMaxSlope, dist, 10);

				GetSpriteExtents(pTarget);

				if ((z1 - z2 > extents_zBot) || (z1 + z2 < extents_zTop))
					continue;

				int ang = engine.getangle(tx - x, ty - y);
				if (klabs(((ang - pSprite.ang + kAngle180) & kAngleMask)
						- kAngle180) > pAimData.kDudeAngle)
					continue;

				if (pPlayer.aimCount < 16
						&& engine.cansee(x, y, z, pSprite.sectnum, tx, ty, tz,
								pTarget.sectnum))
					pPlayer.aimSprites[pPlayer.aimCount++] = nTarget;

				int dist2 = engine.ksqrt(((dx - tx) >> 4) * ((dx - tx) >> 4)
						+ ((dy - ty) >> 4) * ((dy - ty) >> 4)
						+ ((z1 - tz) >> 8) * ((z1 - tz) >> 8));

				if (dist2 < closest) {
					int dz = tz - 4
							* dudeInfo[pTarget.lotag - kDudeBase].aimHeight
							* pTarget.yrepeat - z;

					if (engine.cansee(x, y, z, pSprite.sectnum, tx, ty, tz,
							pTarget.sectnum)) {
						closest = dist2;

						ax = Cos(ang) >> 16;
						ay = Sin(ang) >> 16;
						az = divscale(dz, dist, 10);
						nAimSprite = nTarget;
					}
				}
			}


			if (pAimData.kThingAngle > 0) {
				for (int nTarget = headspritestat[kStatThing]; nTarget >= 0; nTarget = nextspritestat[nTarget]) {
					SPRITE pTarget = sprite[nTarget];

					if ((pTarget.hitag & kAttrAiming) == 0)
						continue;

					int tx, ty, tz;

					tx = pTarget.x;
					ty = pTarget.y;
					tz = pTarget.z;

					int dist = (int) engine.qdist(tx - x, ty - y);
					if (dist == 0 || dist > kAimMaxDist)
						continue;

					int dx = mulscale(dist, Cos(pSprite.ang), 30) + x;
					int dy = mulscale(dist, Sin(pSprite.ang), 30) + y;

					int z1 = mulscale(dist, (int) pPlayer.horizOff, 10) + z;
					int z2 = mulscale(kAimMaxSlope, dist, 10);

					GetSpriteExtents(pTarget);

					if ((z1 - z2 > extents_zBot) || (z1 + z2 < extents_zTop))
						continue;

					int ang = engine.getangle(tx - x, ty - y);
					if (klabs(((ang - pSprite.ang + kAngle180) & kAngleMask)
							- kAngle180) > pAimData.kThingAngle)
						continue;

					if (pPlayer.aimCount < 16
							&& engine.cansee(x, y, z, pSprite.sectnum, tx, ty, tz,
									pTarget.sectnum)) {
						pPlayer.aimSprites[pPlayer.aimCount++] = nTarget;
					}

					int dist2 = engine.ksqrt(((dx - tx) >> 4) * ((dx - tx) >> 4)
							+ ((dy - ty) >> 4) * ((dy - ty) >> 4)
							+ ((z1 - tz) >> 8) * ((z1 - tz) >> 8));

					if (dist2 < closest) {
						if (engine.cansee(x, y, z, pSprite.sectnum, tx, ty, tz,
								pTarget.sectnum)) {
							closest = dist2;

							ax = Cos(ang) >> 16;
							ay = Sin(ang) >> 16;
							az = divscale(tz - z, dist, 10);
							nAimSprite = nTarget;
						}
					}
				}
			}
		}

		RotateVector(ax, ay, -pSprite.ang);
		ax = (int) rotated.x;
		ay = (int) rotated.y;
		az -= pPlayer.horizOff;

		pPlayer.relAim.x += mulscale((int) (ax - pPlayer.relAim.x), pAimData.kTrackXYRate, 16);
		pPlayer.relAim.y += mulscale((int) (ay - pPlayer.relAim.y), pAimData.kTrackXYRate, 16);
		pPlayer.relAim.z += mulscale((int) (az - pPlayer.relAim.z), pAimData.kTrackZRate, 16);

		pPlayer.aim.set(pPlayer.relAim);

		RotateVector((int) pPlayer.aim.x, (int) pPlayer.aim.y, pSprite.ang);

		pPlayer.aim.x = rotated.x;
		pPlayer.aim.y = rotated.y;
		pPlayer.aim.z += pPlayer.horizOff;

		if(newHoriz && !IsOriginalDemo() && game.net.gProfile[pPlayer.nPlayer].slopetilt) {
			if(!game.net.gProfile[pPlayer.nPlayer].autoaim || nAimSprite < 0)
				pPlayer.aim.z -= 120 * pPlayer.slope;
		}

		pPlayer.nAimSprite = nAimSprite;

	}

	public static boolean checkFired6or7(PLAYER pPlayer) {
		switch (pPlayer.currentWeapon) {
		case 6:
			if (pPlayer.weaponState == 4 || pPlayer.weaponState == 5
					|| pPlayer.weaponState == 6) // alt throw or drop
				return true;
			break;
		case 7:
			if (pPlayer.weaponState == 5 || pPlayer.weaponState == 6) // alt throw or drop
				return true;
			break;
		}
		return false;
	}

	public static void WeaponProcess(PLAYER pPlayer) {

		if(!IsOriginalDemo())
		{
			if(!weaponIdle(pPlayer)) {
				pPlayer.pInput.NextWeapon = false;
				pPlayer.pInput.PrevWeapon = false;
			}

			if(gTNTCount >= kPQueueSize / 2)
			{
				if(pPlayer.currentWeapon == kWeaponRemoteTNT && pPlayer.weaponState == 10)
				{
					pPlayer.weaponState = 11;
					StartQAV(pPlayer, 32, -1, false);
				}
			}
		}

		if (pPlayer.fireEffect != 0)
			pPlayer.fireEffect -= 1;

		if (pPlayer.pXsprite.health == 0) {
			pPlayer.fLoopQAV = false;
			sfxKill3DSound(pPlayer.pSprite, 1, -1);
		}

		if (pPlayer.Underwater
				&& (pPlayer.currentWeapon == 7 || pPlayer.currentWeapon == 6)) {
			if (checkFired6or7(pPlayer)) {
				if (pPlayer.currentWeapon == 7) {
					pPlayer.fuseTime = pPlayer.weaponTimer;
					DropSpray(1, pPlayer);
					pPlayer.weaponState = 3;
				} else if (pPlayer.currentWeapon == 6) {
					pPlayer.fuseTime = pPlayer.weaponTimer;
					DropBundle(1, pPlayer);
					pPlayer.weaponState = 1;
				}
			}
			WeaponLower(pPlayer);
			pPlayer.throwTime = 0;
		}

		WeaponPlay(pPlayer);

		UpdateAimVector(pPlayer);

		pPlayer.weaponTimer -= kFrameTicks;

		if (pPlayer.fLoopQAV) {
			if(pPlayer.pXsprite.health != 0) {
				if (pPlayer.pInput.Shoot && CheckAmmo(pPlayer, pPlayer.weaponAmmo, 1)) {
					while (pPlayer.weaponTimer <= 0)
						pPlayer.weaponTimer += weaponQAVs[pPlayer.pWeaponQAV].duration;
				} else {
					pPlayer.weaponTimer = 0;
					pPlayer.fLoopQAV = false;
				}
				return;
			}
		}

		pPlayer.weaponTimer = ClipLow(pPlayer.weaponTimer, 0);

		// special processing for TNT
		switch (pPlayer.currentWeapon) {
		case kWeaponSprayCan:
			if (SprayProcess(pPlayer) != 0) {
				return;
			}
			break;
		case kWeaponTNT:
			if (TNTProcess(pPlayer) != 0) {
				return;
			}
			break;
		case kWeaponProxyTNT:
			if (ProxyProcess(pPlayer) != 0) {
				return;
			}
			break;
		case kWeaponRemoteTNT:
			if (RemoteProcess(pPlayer) != 0) {
				return;
			}
			break;
		}

		// return if weapon is busy
		if (pPlayer.weaponTimer > 0)
			return;

		if (pPlayer.pXsprite.health == 0 || pPlayer.currentWeapon == 0)
			pPlayer.pWeaponQAV = -1;

		if (pPlayer.currentWeapon == kWeaponTesla) {
			if (TeslaProcess(pPlayer) != 0) {
				return;
			}
		}

		if (pPlayer.currentWeapon == kWeaponLifeLeach) {
			if (LeachProcess(pPlayer) != 0) {
				return;
			}
		}

		if (pPlayer.updateWeapon != 0) {
			sfxKill3DSound(pPlayer.pSprite, -1, 441);
			pPlayer.pInput.newWeapon = pPlayer.updateWeapon;
			pPlayer.weaponState = 0;
			pPlayer.updateWeapon = 0;
		}

		if (pPlayer.pInput.NextWeapon) {
			pPlayer.pInput.NextWeapon = false;
			pPlayer.weaponState = 0;
			pPlayer.updateWeapon = 0;
			int wnum = WeaponChange(pPlayer, true);
			pPlayer.weaponMode[wnum] = 0;
			if (pPlayer.currentWeapon != 0) {
				if(!IsOriginalDemo() && (wnum == 6 || wnum == 7)) {
					pPlayer.pInput.newWeapon = wnum;
					WeaponLower(pPlayer);
				} else {
					WeaponLower(pPlayer);
					pPlayer.updateWeapon = wnum;
				}
				return;
			}
			pPlayer.pInput.newWeapon = wnum;
		}

		if (pPlayer.pInput.PrevWeapon) {
			pPlayer.pInput.PrevWeapon = false;
			pPlayer.weaponState = 0;
			pPlayer.updateWeapon = 0;
			int wnum = WeaponChange(pPlayer, false);
			pPlayer.weaponMode[wnum] = 0;
			if (pPlayer.currentWeapon != 0) {
				if(!IsOriginalDemo() && (wnum == 6 || wnum == 7)) {
					pPlayer.pInput.newWeapon = wnum;
					WeaponLower(pPlayer);
				} else {
					WeaponLower(pPlayer);
					pPlayer.updateWeapon = wnum;
				}
				return;
			}
			pPlayer.pInput.newWeapon = wnum;
		}

		// pPlayer.weapon is set to kMaxWeapons in the fire processing below
		if (pPlayer.weaponState == kFindWeapon) {
			pPlayer.weaponState = 0;
			int weap = WeaponFindLoaded(pPlayer, true);
			pPlayer.weaponMode[weap] = FindLoaded;
			if (pPlayer.currentWeapon != 0) {
				WeaponLower(pPlayer);
				pPlayer.updateWeapon = weap;
				return;
			}
			pPlayer.pInput.newWeapon = weap;
		}

		// weapon select
		if (pPlayer.pInput.newWeapon != 0) {
			if (pPlayer.pInput.newWeapon == kWeaponTNT) {
				switch (pPlayer.currentWeapon) {
				case kWeaponTNT:
					if (CheckAmmo2(pPlayer, 10, 1)) {
						pPlayer.pInput.newWeapon = kWeaponProxyTNT;
						break;
					}
					if (CheckAmmo2(pPlayer, 11, 1)) {
						pPlayer.pInput.newWeapon = kWeaponRemoteTNT;
						break;
					}
					break;
				case kWeaponProxyTNT:
					if (CheckAmmo2(pPlayer, 11, 1)) {
						pPlayer.pInput.newWeapon = kWeaponRemoteTNT;
						break;
					}
					if (CheckAmmo2(pPlayer, 5, 1) && !pPlayer.Underwater) {
						pPlayer.pInput.newWeapon = kWeaponTNT;
						break;
					}
					break;
				case kWeaponRemoteTNT:
					if (CheckAmmo2(pPlayer, 5, 1) && !pPlayer.Underwater) {
						pPlayer.pInput.newWeapon = kWeaponTNT;
						break;
					}
					if (CheckAmmo2(pPlayer, 10, 1)) {
						pPlayer.pInput.newWeapon = kWeaponProxyTNT;
						break;
					}
					break;
				default:
					if (CheckAmmo2(pPlayer, 5, 1) && !pPlayer.Underwater) {
						pPlayer.pInput.newWeapon = kWeaponTNT;
						break;
					}
					if (CheckAmmo2(pPlayer, 10, 1)) {
						pPlayer.pInput.newWeapon = kWeaponProxyTNT;
						break;
					}
					if (CheckAmmo2(pPlayer, 11, 1)) {
						pPlayer.pInput.newWeapon = kWeaponRemoteTNT;
						break;
					}
					break;
				}
			}
			if (pPlayer.pXsprite.health == 0
					|| !pPlayer.hasWeapon[pPlayer.pInput.newWeapon]) {
				pPlayer.pInput.newWeapon = 0;
				return;
			}

			if (pPlayer.Underwater) {

				if ((pPlayer.pInput.newWeapon == 7 || pPlayer.pInput.newWeapon == 6)) {
					if (!checkFired6or7(pPlayer)) {
						pPlayer.pInput.newWeapon = 0;
						return;
					}
				}
			}

			if (pPlayer.currentWeapon != 0) {
				if (pPlayer.pInput.newWeapon != pPlayer.currentWeapon
						|| gWeaponInfo[pPlayer.pInput.newWeapon].update > 1) {
					int i = 0;
					if (pPlayer.pInput.newWeapon == pPlayer.currentWeapon)
						i = 1;

					pPlayer.LastWeapon = pPlayer.currentWeapon;
					while (i <= gWeaponInfo[pPlayer.pInput.newWeapon].update) {
						int upd = (i + pPlayer.weaponMode[pPlayer.pInput.newWeapon])
								% gWeaponInfo[pPlayer.pInput.newWeapon].update;
						int nType = gWeaponInfo[pPlayer.pInput.newWeapon].ammoType;
						if (CheckNewWeapon(pPlayer, pPlayer.pInput.newWeapon,
								nType, 1)) {
							WeaponLower(pPlayer);
							pPlayer.weaponMode[pPlayer.pInput.newWeapon] = upd;
							return;
						}
						i++;
					}
				}
			} else {
				int nAmmo = gWeaponInfo[pPlayer.pInput.newWeapon].ammoType;
				if (gWeaponInfo[pPlayer.pInput.newWeapon].update <= 1) {

					if (CheckNewWeapon(pPlayer, pPlayer.pInput.newWeapon, nAmmo, 1))
						WeaponRaise(pPlayer);
					else {
						pPlayer.weaponState = 0;
						int found = WeaponFindLoaded(pPlayer, true);
						pPlayer.weaponMode[found] = FindLoaded;
						if (pPlayer.currentWeapon != 0) {
							WeaponLower(pPlayer);
							pPlayer.updateWeapon = found;
						} else {
							pPlayer.pInput.newWeapon = found;
						}
					}
					return;
				}

				if (CheckAmmo(pPlayer,
						gWeaponInfo[pPlayer.pInput.newWeapon].ammoType, 1)
						|| nAmmo == 11)
					WeaponRaise(pPlayer);
			}

			pPlayer.pInput.newWeapon = 0;
			return;
		}

		if (pPlayer.currentWeapon != 0
				&& !CheckAmmo(pPlayer, pPlayer.weaponAmmo, 1)
				&& pPlayer.weaponAmmo != kWeaponProxyTNT) {
			pPlayer.weaponState = -1;
			return;
		}

		// fire key
		if (pPlayer.pInput.Shoot) {
			switch (pPlayer.currentWeapon) {
			case kWeaponPitchfork:
				StartQAV(pPlayer, kQAVForkStab, 0, false);
				return;
			case kWeaponFlare:
				if (powerupCheck(pPlayer, kItemGunsAkimbo - kItemBase) != 0 && CheckAmmo2(pPlayer, 1, 2))
					StartQAV(pPlayer, kQAV2FlareFire2, 18, false);
				else
					StartQAV(pPlayer, kQAVFlareFire, 18, false);
				return;
			case kWeaponShotgun:
				if (pPlayer.weaponState == 2) {
					pPlayer.weaponState = 1;
					StartQAV(pPlayer, kQAVShotgunFire2, 13, false);
					return;
				}
				if (pPlayer.weaponState == 3) {
					pPlayer.weaponState = 2;
					StartQAV(pPlayer, kQAVShotgunFire1, 13, false);
					return;
				}
				if (pPlayer.weaponState == 7) {
					pPlayer.weaponState = 6;
					StartQAV(pPlayer, kQAV2ShotgunFire2, 13, false);
					return;
				}
				break;
			case kWeaponTNT:
				if (pPlayer.weaponState == 3) {
					pPlayer.weaponState = 6;
					pPlayer.fuseTime = -1;
					pPlayer.fireClock = gFrameClock;
					StartQAV(pPlayer, 21, 7, false);
					return;
				}
				break;
			case kWeaponSprayCan:
				if (pPlayer.weaponState == 3) {
					pPlayer.weaponState = 4;
					StartQAV(pPlayer, 10, 1, true);
					return;
				}
				break;
			case kWeaponProxyTNT:
				if (pPlayer.weaponState == 7) {
					pPlayer.pWeaponQAV = 27;
					pPlayer.weaponState = 9;
					pPlayer.fireClock = gFrameClock;
					return;
				}
				break;
			case kWeaponRemoteTNT:
				if (pPlayer.weaponState == 10) {
					pPlayer.pWeaponQAV = 36;
					pPlayer.weaponState = 13;
					pPlayer.fireClock = gFrameClock;
					return;
				}
				if (pPlayer.weaponState == 11) {
					pPlayer.weaponState = 12;
					StartQAV(pPlayer, 40, 12, false);
					return;
				}
				break;
			case kWeaponTommy:
				if (powerupCheck(pPlayer, kItemGunsAkimbo - kItemBase) != 0 && CheckAmmo2(pPlayer, 3, 2))
					StartQAV(pPlayer, kQAV2TommygunFire, 15, true);
				else
					StartQAV(pPlayer, kQAVTommygunFire, 15, true);
				return;
			case kWeaponVoodoo:
				int voodooChance = 2 * bRandom();
				int nStabType;
				for (nStabType = 0; voodooChance > gVoodooData[nStabType]; ++nStabType);
				pPlayer.voodooTarget = pPlayer.nAimSprite;
				if (pPlayer.voodooTarget == -1
						|| sprite[pPlayer.voodooTarget].statnum != 6)
					nStabType = kVoodooStabSelf;
				StartQAV(pPlayer, nStabType + getQAV(kVDFIRE1), 20, false);
				return;
			case kWeaponTesla:
				if(GAMEVER != VER100) {
					if (pPlayer.weaponState == 2)
		            {
						pPlayer.weaponState = 4;
						if ( CheckAmmo2(pPlayer, 7, 10)
								&& powerupCheck(pPlayer, kItemGunsAkimbo - kItemBase) != 0 )
			                StartQAV(pPlayer, 84, 23, false);
						else StartQAV(pPlayer, 77, 23, false);
						return;
		            }
					if (pPlayer.weaponState == 5)
		            {
		            	if ( CheckAmmo2(pPlayer, 7, 10)
		            			&& powerupCheck(pPlayer, kItemGunsAkimbo - kItemBase) != 0 )
		                	StartQAV(pPlayer, 84, 23, false);
		            	else StartQAV(pPlayer, 77, 23, false);
		            	return;
		            }
				} else {
					if (pPlayer.weaponState == 2) {
						pPlayer.weaponState = 4;
						StartQAV(pPlayer, 77, 22, false);
						return;
					}
					if (pPlayer.weaponState == 5) {
						StartQAV(pPlayer, 77, 22, false);
						return;
					}
				}
				break;
			case kWeaponNapalm:
				if (powerupCheck(pPlayer, kItemGunsAkimbo - kItemBase) != 0 && (IsOriginalDemo() || CheckAmmo2(pPlayer, 4, 2)))
					StartQAV(pPlayer, getQAV(k2NAPFIRE), 24, false);
				else
					StartQAV(pPlayer, getQAV(kNAPFIRE), 24, false);
				return;
			case kWeaponLifeLeach:
				sfxStart3DSound(pPlayer.pSprite, 494, 1, 0);
				StartQAV(pPlayer, getQAV(kSTAFIRE4), 26, false);
				return;
			case kWeaponBeast:
				StartQAV(pPlayer, getQAV(kBSTATAK1) + Random(4), 28, false);
				return;
			}
		}

		// alternate fire key
		if (pPlayer.pInput.AltShoot) {
			switch (pPlayer.currentWeapon) {
			case kWeaponPitchfork:
				StartQAV(pPlayer, kQAVForkStab, 0, false);
				return;
			case kWeaponLifeLeach:
				if(GAMEVER == VER100)
				{
					sfxStart3DSound(pPlayer.pSprite, 494, 2, 0);
					StartQAV(pPlayer, getQAV(kSTAFIRE4), 26, false);
				}
				else
				{
					if ( pGameInfo.nGameType <= 1 && !CheckAmmo2(pPlayer, 8, 1) && pPlayer.pXsprite.health < 400 )
					{
						sfxStart3DSound(pPlayer.pSprite, 494, 2, 0);
						StartQAV(pPlayer, getQAV(kSTAFIRE4), 26, false);
						return;
					}
			        StartQAV(pPlayer, 119, -1, false);
			        DropLeech(pPlayer);
			        pPlayer.weaponState = -1;
				}
				return;
			case kWeaponSprayCan:
				if (pPlayer.weaponState == 3) {
					pPlayer.weaponState = 5;
					StartQAV(pPlayer, 12, 4, false);
					return;
				}
				break;
			case kWeaponTNT:
				switch (pPlayer.weaponState) {
				case 3:
					pPlayer.weaponState = 4;
					StartQAV(pPlayer, 21, 7, false);
					break;
				case 7:
					pPlayer.weaponState = 8;
					StartQAV(pPlayer, 28, 9, false);
					break;
				case 10:
					pPlayer.weaponState = 11;
					StartQAV(pPlayer, 38, 11, false);
					break;
				case 11:
					if (pPlayer.ammoCount[11] > 0) {
						pPlayer.weaponState = 10;
						StartQAV(pPlayer, 30, -1, false);
					}
					break;
				}
				break;
			case kWeaponProxyTNT:
				if (pPlayer.weaponState == 7) {
					pPlayer.weaponState = 8;
					StartQAV(pPlayer, 28, 9, false);
					return;
				}
				break;
			case kWeaponRemoteTNT:
				if (pPlayer.weaponState == 10) {
					pPlayer.weaponState = 11;
					StartQAV(pPlayer, 38, 11, false);
					return;
				}

				if(gTNTCount >= kPQueueSize / 2)
					break;

				if (pPlayer.ammoCount[11] > 0) {
					pPlayer.weaponState = 10;
					StartQAV(pPlayer, 30, -1, false);
					return;
				}
				break;
			case kWeaponShotgun:
				if (pPlayer.weaponState == 2) {
					pPlayer.weaponState = 1;
					StartQAV(pPlayer, kQAVShotgunFire2, 13, false);
					return;
				}
				if (pPlayer.weaponState == 3) {
					pPlayer.weaponState = 1;
					StartQAV(pPlayer, kQAVShotgunFire3, 13, false);
					return;
				}
				if (pPlayer.weaponState == 7) {
					pPlayer.weaponState = 6;
					StartQAV(pPlayer, kQAV2ShotgunFire1, 13, false);
					return;
				}
				break;
			case kWeaponTommy:
				if (powerupCheck(pPlayer, kItemGunsAkimbo - kItemBase) != 0 && CheckAmmo2(pPlayer, 3, 2))
					StartQAV(pPlayer, kQAV2TommygunFire2, 16, false);
				else
					StartQAV(pPlayer, kQAVTommygunFire2, 16, false);
				return;
			case kWeaponVoodoo:
				sfxStart3DSound(pPlayer.pSprite, 461, 2, 0);
				StartQAV(pPlayer, getQAV(kVDSPEL1), 21, false); //22
				return;
			case kWeaponTesla:
				if(GAMEVER != VER100) {
					if ( CheckAmmo2(pPlayer, 7, 35) )
			        {
			        	if ( CheckAmmo2(pPlayer, 7, 70) && powerupCheck(pPlayer, kItemGunsAkimbo - kItemBase) != 0 )
			            	StartQAV(pPlayer, 85, 23, false);
			            else
			            	StartQAV(pPlayer, 78, 23, false);
			            return;
			        }
			        if ( CheckAmmo2(pPlayer, 7, 10) && powerupCheck(pPlayer, kItemGunsAkimbo - kItemBase) != 0 )
			        {
			            StartQAV(pPlayer, 84, 23, false);
			            return;
			        }
			        StartQAV(pPlayer, 77, 23, false);
				} else {
					if(CheckAmmo2(pPlayer, 7, 35))
						StartQAV(pPlayer, 78, 23, false);
					else StartQAV(pPlayer, 77, 22, false);
				}
				return;
			case kWeaponNapalm:
				if (powerupCheck(pPlayer, kItemGunsAkimbo - kItemBase) != 0 && (IsOriginalDemo() || CheckAmmo2(pPlayer, 4, 13)))
					StartQAV(pPlayer, getQAV(k2NAPFIRE), 25, false);
				else
					StartQAV(pPlayer, getQAV(kNAPFIRE), 25, false);
				return;
			case kWeaponFlare:
				if (CheckAmmo(pPlayer, 1, 8)) {
					if (powerupCheck(pPlayer, kItemGunsAkimbo - kItemBase) != 0 && CheckAmmo2(pPlayer, 1, 16))
						StartQAV(pPlayer, 48, 19, false);
					else
						StartQAV(pPlayer, 43, 19, false);
				} else {
					if (powerupCheck(pPlayer, kItemGunsAkimbo - kItemBase) != 0 && CheckAmmo2(pPlayer, 1, 2))
						StartQAV(pPlayer, 48, 18, false);
					else
						StartQAV(pPlayer, 43, 18, false);
				}
				return;
			}
		}

		WeaponUpdateState(pPlayer);
	}

	public static void WeaponLower(PLAYER pPlayer) {
		if(pPlayer == null)game.dassert("pPlayer != NULL");

		int nWeapon = pPlayer.currentWeapon;
		int nState = pPlayer.weaponState;

		if (checkFired6or7(pPlayer))
			return;

		pPlayer.throwTime = 0;

		switch (nWeapon) {
		case kWeaponPitchfork:
			StartQAV(pPlayer, kQAVForkDown, -1, false);
			break;
		case kWeaponFlare:
			if (powerupCheck(pPlayer, kItemGunsAkimbo - kItemBase) != 0 && nState == 3)
				StartQAV(pPlayer, kQAV2FlareDown, -1, false);
			else
				StartQAV(pPlayer, kQAVFlareDown, -1, false);
			break;
		case kWeaponShotgun:
			if (powerupCheck(pPlayer, kItemGunsAkimbo - kItemBase) != 0 && (IsOriginalDemo() || CheckAmmo2(pPlayer, 2, 4)))
				StartQAV(pPlayer, kQAV2ShotgunDown, -1, false);
			else
				StartQAV(pPlayer, kQAVShotgunDown, -1, false);
			break;
		case kWeaponTommy:
			if (powerupCheck(pPlayer, kItemGunsAkimbo - kItemBase) != 0 && nState == 1)
				StartQAV(pPlayer, kQAV2TommygunDown, -1, false);
			else
				StartQAV(pPlayer, kQAVTommygunDown, -1, false);
			break;
		case kWeaponNapalm:
			if (powerupCheck(pPlayer, kItemGunsAkimbo - kItemBase) != 0 && (IsOriginalDemo() || CheckAmmo2(pPlayer, 4, 2)))
				StartQAV(pPlayer, getQAV(k2NAPDOWN), -1, false);
			else
				StartQAV(pPlayer, getQAV(kNAPDOWN), -1, false);
			break;
		case kWeaponTNT:
			switch (nState) {
			case 1:
				StartQAV(pPlayer, 7, -1, false);
				break;
			case 2:
				WeaponRaise(pPlayer);
				break;
			case 0: // for next/prev weapon
			case 3:
				if (pPlayer.pInput.newWeapon == 7) {
					pPlayer.weaponState = 2;
					StartQAV(pPlayer, 17, -1, false);
				} else {
					StartQAV(pPlayer, 19, -1, false);
				}
				break;
			}
			break;
		case kWeaponSprayCan:
			sfxKill3DSound(pPlayer.pSprite, -1, 441);
			switch (nState) {
			case 1:
				StartQAV(pPlayer, 7, -1, false);
				break;
			case 2:
				pPlayer.weaponState = 1;
				WeaponRaise(pPlayer);
				return;
			case 0: // for next/prev weapon
			case 3:
				if (pPlayer.pInput.newWeapon == 6) {
					pPlayer.weaponState = 2;
					StartQAV(pPlayer, 11, -1, false);
					return;
				}
				if (pPlayer.pInput.newWeapon != 7) {
					pPlayer.weaponState = 1;
					StartQAV(pPlayer, (IsOriginalDemo() || weaponQAVs[kQAVSprayDown] == null) ? 11 : kQAVSprayDown, -1, false);
					pPlayer.currentWeapon = 0;
					pPlayer.fLoopQAV = false;
					return;
				}
				pPlayer.weaponState = 1;
				StartQAV(pPlayer, 11, -1, false);
				pPlayer.pInput.newWeapon = 0;
				break;
			case 4:
				pPlayer.weaponState = 1;
				StartQAV(pPlayer, 11, -1, false);
				pPlayer.pInput.newWeapon = 0;
				WeaponLower(pPlayer);
				break;
			}
			break;

		case kWeaponTesla:
			if ( GAMEVER != VER100 && CheckAmmo2(pPlayer, 7, 10) && powerupCheck(pPlayer, kItemGunsAkimbo - kItemBase) != 0 )
		        StartQAV(pPlayer, 88, -1, false);
		    else
		       StartQAV(pPlayer, 81, -1, false);
			break;
		case kWeaponVoodoo:
			StartQAV(pPlayer, getQAV(kVDDOWN), -1, false);
			break;
		case kWeaponLifeLeach:
			StartQAV(pPlayer, getQAV(kSTAFDOWN), -1, false);
			break;
		case kWeaponProxyTNT:
			if (nState == 7)
				StartQAV(pPlayer, 26, -1, false);
			break;
		case kWeaponRemoteTNT:
			if (nState == 10)
				StartQAV(pPlayer, 34, -1, false);
			else if (nState == 11)
				StartQAV(pPlayer, 35, -1, false);
			break;
		case kWeaponBeast:
			StartQAV(pPlayer, getQAV(kBSTDOWN), -1, false);
			break;
		}
		pPlayer.currentWeapon = 0;
		pPlayer.fLoopQAV = false;

		if(!IsOriginalDemo()) {
			if (nWeapon != kWeaponTNT)
				pPlayer.weaponState = 0;
		}
	}

	public static void WeaponRaise(PLAYER pPlayer) {
		if(pPlayer == null)game.dassert("pPlayer != NULL");
		int oldWeapon = pPlayer.currentWeapon;
		pPlayer.currentWeapon = pPlayer.pInput.newWeapon;
		int nWeapon = pPlayer.currentWeapon;
		int nState = pPlayer.weaponState;
		pPlayer.pInput.newWeapon = 0;
		pPlayer.weaponAmmo = gWeaponInfo[nWeapon].ammoType;
		switch (nWeapon) {
		case kWeaponPitchfork:
			pPlayer.weaponState = 0;
			StartQAV(pPlayer, kQAVForkUp, -1, false);
			break;
		case kWeaponFlare:

			if (powerupCheck(pPlayer, kItemGunsAkimbo - kItemBase) != 0 && CheckAmmo2(pPlayer, 1, 2)) {
				StartQAV(pPlayer, kQAV2FlareUp, -1, false);
				pPlayer.weaponState = 3;
			}
			else {
				StartQAV(pPlayer, kQAVFlareUp, -1, false);
				pPlayer.weaponState = 2;
			}
			break;
		case kWeaponShotgun:
			if (powerupCheck(pPlayer, kItemGunsAkimbo - kItemBase) != 0) {
				if (gInfiniteAmmo || pPlayer.ammoCount[2] >= 4) {
					StartQAV(pPlayer, kQAV2ShotgunUp, -1, false);
					pPlayer.weaponState = 7;
				} else {
					if (pPlayer.ammoCount[2] > 1)
						pPlayer.weaponState = 3;
					else {
						if (pPlayer.ammoCount[2] == 0)
							pPlayer.weaponState = 1;
						else
							pPlayer.weaponState = 2;
					}
					StartQAV(pPlayer, 50, -1, false);
				}
			} else {
				if (gInfiniteAmmo || (pPlayer.ammoCount[2] > 1))
					pPlayer.weaponState = 3;
				else if (pPlayer.ammoCount[2] <= 0)
					pPlayer.weaponState = 1;
				else
					pPlayer.weaponState = 2;
				StartQAV(pPlayer, kQAVShotgunUp, -1, false);
			}
			break;
		case kWeaponTommy:
			pPlayer.weaponState = 0;
			if (powerupCheck(pPlayer, kItemGunsAkimbo - kItemBase) != 0 && CheckAmmo2(pPlayer, 3, 2) )
			{
				StartQAV(pPlayer, kQAV2TommygunUp, -1, false);
				pPlayer.weaponState = 1;
			}
			else {
				StartQAV(pPlayer, kQAVTommygunUp, -1, false);
				pPlayer.weaponState = 0;
			}
			break;
		case kWeaponNapalm:
			if (powerupCheck(pPlayer, kItemGunsAkimbo - kItemBase) != 0 && (IsOriginalDemo() || CheckAmmo2(pPlayer, 4, 2))) {
				StartQAV(pPlayer, getQAV(k2NAPUP), -1, false);
				pPlayer.weaponState = 3;
			} else {
				StartQAV(pPlayer, getQAV(kNAPUP), -1, false);
				pPlayer.weaponState = 2;
			}
			break;
		case kWeaponTNT:
			if (gInfiniteAmmo || CheckAmmo2(pPlayer, 5, 1)) {
				pPlayer.weaponState = 3;
				if (oldWeapon == 7)
					StartQAV(pPlayer, 16, -1, false);
				else
					StartQAV(pPlayer, 18, -1, false);
			}
			break;
		case kWeaponSprayCan:
			if (nState == 2) {
				pPlayer.weaponState = 3;
				StartQAV(pPlayer, 8, -1, false);
			} else {
				pPlayer.weaponState = 0;
				StartQAV(pPlayer, 4, -1, false);
			}
			break;
		case kWeaponTesla:
			if( GAMEVER != VER100 )
			{
				if ( CheckAmmo2(pPlayer, 7, 1) )
			    {
					pPlayer.weaponState = 2;
			        if ( powerupCheck(pPlayer, kItemGunsAkimbo - kItemBase) != 0 )
			        	StartQAV(pPlayer, 82, -1, false);
			        else
			        	StartQAV(pPlayer, 74, -1, false);
			    }
			    else
			    {
			    	pPlayer.weaponState = 3;
			        StartQAV(pPlayer, 74, -1, false);
				}
			}
			else
			{
				pPlayer.weaponState = (pPlayer.ammoCount[7] <= 0) ? 1 : 2;
				StartQAV(pPlayer, 74, -1, false);
			}
			break;
		case kWeaponVoodoo:
			if (gInfiniteAmmo || CheckAmmo2(pPlayer, 9, 1)) {
				pPlayer.weaponState = 2;
				StartQAV(pPlayer, getQAV(kVDUP), -1, false);
			}
			break;
		case kWeaponLifeLeach:
			pPlayer.weaponState = 2;
			StartQAV(pPlayer, getQAV(kSTAFUP), -1, false);
			break;
		case kWeaponProxyTNT:
			if (gInfiniteAmmo || CheckAmmo2(pPlayer, 10, 1)) {
				pPlayer.weaponState = 7;
				StartQAV(pPlayer, 25, -1, false);
			}
			break;
		case kWeaponRemoteTNT:
			if (gInfiniteAmmo || CheckAmmo2(pPlayer, 11, 1)) {
				pPlayer.weaponState = 10;
				StartQAV(pPlayer, 31, -1, false);
			} else {
				pPlayer.weaponState = 11;
				StartQAV(pPlayer, 32, -1, false);
			}
			break;
		case kWeaponBeast:
			pPlayer.weaponState = 2;
			StartQAV(pPlayer, getQAV(kBSTUP), -1, false);
			break;
		}
	}

	public static int FindLoaded = 0;

	public static int WeaponFindLoaded(PLAYER pPlayer, boolean out) {
		int ret = 1;
		if (gWeaponInfo[pPlayer.currentWeapon].update > 1) {
			for (int i = 0; i < gWeaponInfo[pPlayer.currentWeapon].update; i++) {
				if (CheckAmmo(pPlayer,
						gWeaponInfo[pPlayer.currentWeapon].ammoType, 1)) {
					ret = pPlayer.currentWeapon;
					break;
				}
			}
		}
		if (ret == 1) {
			for (int j = 0; j < kWeaponMax; ++j) {
				int newWeapon = pPlayer.weaponOrder[0][j];
				if (pPlayer.hasWeapon[newWeapon]) {
					for (int k = 0; k < gWeaponInfo[newWeapon].update; ++k) {
						if (CheckNewWeapon(pPlayer, newWeapon,
								gWeaponInfo[newWeapon].ammoType, 1)) {
							ret = newWeapon;
							if (out)
								FindLoaded = k;
							return ret;
						}
					}
				}
			}
		}
		if (out)
			FindLoaded = 0;
		return ret;
	}

	public static void WeaponUpdateState(PLAYER pPlayer) {

		int nWeapon = pPlayer.currentWeapon;
		int nState = pPlayer.weaponState;

		switch (nWeapon) {
		case kWeaponPitchfork:
			pPlayer.pWeaponQAV = kQAVForkIdle;
			break;
		case kWeaponFlare:
			if (powerupCheck(pPlayer, kItemGunsAkimbo - kItemBase) != 0)
			{
				if ( nState == 3 && CheckAmmo2(pPlayer, 1, 2) )
					pPlayer.pWeaponQAV = kQAV2FlareIdle;
		        else
		        {
		        	pPlayer.pWeaponQAV = kQAVFlareIdle;
		        	pPlayer.weaponState = 2;
		        }
			} else pPlayer.pWeaponQAV = kQAVFlareIdle;
			break;
		case kWeaponShotgun:
			switch (nState) {
			case 1:
				if (CheckAmmo(pPlayer, 2, 1)) {
					sfxStart3DSound(pPlayer.pSprite, 410, 2, 2);
					StartQAV(pPlayer, kQAVShotgunReload, 14, false);
					if (gInfiniteAmmo || (pPlayer.ammoCount[2] > 1))
						pPlayer.weaponState = 3;
					else
						pPlayer.weaponState = 2;
				} else
					pPlayer.pWeaponQAV = kQAVShotgunIdle3;
				break;
			case 2:
				pPlayer.pWeaponQAV = kQAVShotgunIdle2;
				break;
			case 3:
				pPlayer.pWeaponQAV = kQAVShotgunIdle1;
				break;
			case 6:
				if (powerupCheck(pPlayer, kItemGunsAkimbo - kItemBase) != 0) {
					if (gInfiniteAmmo || CheckAmmo(pPlayer, 2, 4)) {
						pPlayer.weaponState = 7;
						return;
					}
				}
				pPlayer.weaponState = 1;
				break;
			case 7:
				pPlayer.pWeaponQAV = kQAV2ShotgunIdle;
				break;
			}
			break;
		case kWeaponTommy:
			if (powerupCheck(pPlayer, kItemGunsAkimbo - kItemBase) != 0 && CheckAmmo2(pPlayer, 3, 2)) {
				pPlayer.pWeaponQAV = kQAV2TommygunIdle;
				pPlayer.weaponState = 1;
			}
			else {
				pPlayer.pWeaponQAV = kQAVTommygunIdle;
				pPlayer.weaponState = 0;
			}
			break;
		case kWeaponNapalm:
			if (nState == 2)
				pPlayer.pWeaponQAV = getQAV(kNAPIDLE);
			if (nState == 3) {
				if (powerupCheck(pPlayer, kItemGunsAkimbo - kItemBase) != 0
						&& (gInfiniteAmmo || CheckAmmo(pPlayer, 4, 2)))
					pPlayer.pWeaponQAV = getQAV(k2NAPIDLE);
				else
					pPlayer.pWeaponQAV = getQAV(kNAPIDLE);
			}
			break;
		case kWeaponTNT:
			switch (nState) {
			case 0:
				pPlayer.weaponState = 1;
				StartQAV(pPlayer, 5, -1, false);
				break;
			case 1:
				if (pPlayer.weaponAmmo == 5) {
					if (CheckAmmo(pPlayer, 5, 1)) {
						pPlayer.weaponState = 3;
						StartQAV(pPlayer, 16, -1, false);
					}
				}
				break;
			case 2:
				if (pPlayer.ammoCount[5] <= 0) {
					pPlayer.pWeaponQAV = 6;
				} else {
					pPlayer.weaponState = 3;
					StartQAV(pPlayer, 16, -1, false);
				}
				break;
			case 3:
				pPlayer.pWeaponQAV = 20;
				break;
			}
			break;
		case kWeaponSprayCan:
			switch (nState) {
			case 0:
				pPlayer.weaponState = 1;
				StartQAV(pPlayer, 5, -1, false);
				break;
			case 1:
				if (!CheckAmmo(pPlayer, 6, 1))
					pPlayer.pWeaponQAV = 6;
				pPlayer.weaponState = 3;
				StartQAV(pPlayer, 8, -1, false);
				break;
			case 3:
				pPlayer.pWeaponQAV = 9;
				break;
			case 4:
				if (CheckAmmo(pPlayer, 6, 1)) {
					pPlayer.pWeaponQAV = 9;
					pPlayer.weaponState = 3;
				} else {
					pPlayer.weaponState = 1;
					StartQAV(pPlayer, 11, -1, false);
				}
				sfxKill3DSound(pPlayer.pSprite, 1, 441);
				break;
			}
			break;
		case kWeaponTesla:
		    if ( nState == 2 )
		    {
		    	if ( GAMEVER != VER100 && CheckAmmo2(pPlayer, 7, 10) && powerupCheck(pPlayer, kItemGunsAkimbo - kItemBase) != 0 )
		    		pPlayer.pWeaponQAV = 83;
		        else
		        	pPlayer.pWeaponQAV = 75;
		    }
		    if ( nState == 3 )
		    	pPlayer.pWeaponQAV = 76;
			break;
		case kWeaponVoodoo:
			if (pPlayer.pXsprite.height >= 0x100)
				pPlayer.pWeaponQAV = getQAV(kVDIDLE1);
			int sway = klabs(pPlayer.swayHeight);
			if ((IsOriginalDemo() && sway <= 768) || (!IsOriginalDemo() && sway <= 16))
				pPlayer.pWeaponQAV = getQAV(kVDIDLE1);
			else
				pPlayer.pWeaponQAV = getQAV(kVDIDLE2);

			break;
		case kWeaponLifeLeach:
			if (nState == 2)
				pPlayer.pWeaponQAV = getQAV(kSTAFIDL1);
			break;
		case kWeaponProxyTNT:
			if (nState == 7)
				pPlayer.pWeaponQAV = 27;
			if (nState == 8) {
				pPlayer.weaponState = 7;
				StartQAV(pPlayer, 25, -1, false);
			}
			break;
		case kWeaponRemoteTNT:
			if (nState == 10)
				pPlayer.pWeaponQAV = 36;
			if (nState == 11)
				pPlayer.pWeaponQAV = 37;

			if (nState == 12) {
				if (pPlayer.ammoCount[11] <= 0) {
					pPlayer.weaponState = -1;
				} else {
					pPlayer.weaponState = 10;
					StartQAV(pPlayer, 31, -1, false);
				}
			}
			break;
		case kWeaponBeast:
			pPlayer.pWeaponQAV = getQAV(kBSTIDLE);
			break;
		}
	}

	public static void StartQAV(PLAYER pPlayer, int nWeaponQAV, int callback,
			boolean fLoop) {
		if(nWeaponQAV >= kQAVEnd) game.dassert("nWeaponQAV < kQAVEnd");
		pPlayer.pWeaponQAV = nWeaponQAV;
		pPlayer.weaponTimer = weaponQAVs[pPlayer.pWeaponQAV].duration;
		pPlayer.weaponCallback = callback;
		pPlayer.fLoopQAV = fLoop;

		weaponQAVs[pPlayer.pWeaponQAV].Preload();
		WeaponPlay(pPlayer);
		pPlayer.weaponTimer -= kFrameTicks;
	}

	public static void WeaponPlay(PLAYER pPlayer) {
		if(pPlayer == null)game.dassert("pPlayer != NULL");

		if (pPlayer.pWeaponQAV == -1)
			return;

		QAV pQAV = weaponQAVs[pPlayer.pWeaponQAV];

		if (pQAV == null)
			return;

		int t = pQAV.duration - pPlayer.weaponTimer;
		pQAV.sprite = pPlayer.pSprite;
		pQAV.Play(t - kFrameTicks, t, pPlayer.weaponCallback, pPlayer);
	}

	public static void WeaponDraw(PLAYER pPlayer, int nShade, int x, int y, int nPLU, int nScale) {
		if(pPlayer == null)game.dassert("pPlayer != NULL");
		int pQAVnum = pPlayer.pWeaponQAV;

		int t;

		if (pQAVnum == -1)
			return;

		QAV pQAV = weaponQAVs[pQAVnum];
		if (pPlayer.weaponTimer == 0) { // playing idle QAV?
			if(!IsOriginalDemo() && (pPlayer.weaponState == -1 || (pPlayer.currentWeapon == 3 && pPlayer.weaponState == 7)))
				t = pQAV.duration - 1;
			else t = totalclock % pQAV.duration;
		} else t = pQAV.duration - pPlayer.weaponTimer;

		pQAV.origin.x = x - (pPlayer.lookang>>1);
		pQAV.origin.y = y + (klabs(pPlayer.lookang) / 9);

		if(cfg.gViewSize == 3 && !game.menu.gShowMenu)
			pQAV.origin.y -= kStatBarHeight / 2;

		if(pQAV.mindisp < 0 && pQAV.origin.y - 199 < pQAV.mindisp)
			pQAV.origin.y += (pQAV.mindisp - (pQAV.origin.y - 199)); //ycoord correct

		int nPowerRemaining = powerupCheck(pPlayer, kItemLtdInvisibility - kItemBase);
		int nFlags = kQFrameScale | kQFrameUnclipped;

		if (nPowerRemaining >= (kTimerRate * 8) || nPowerRemaining != 0
				&& (totalclock & 32) != 0) {
			nFlags |= (kQFrameTranslucent);
			nShade = -128;
		}

		pQAV.Draw(t, nShade, nFlags, nPLU, nScale);
	}

	static void FireSpray(int nTrigger, PLAYER pPlayer) {
		playerFireMissile(pPlayer, 0, (int) pPlayer.aim.x, (int) pPlayer.aim.y,
				(int) pPlayer.aim.z, kMissileSprayFlame);
		UseAmmo(pPlayer, 6, kFrameTicks); // kAmmoSprayCan
		if (CheckAmmo(pPlayer, 6, 1))
			sfxStart3DSound(pPlayer.pSprite, 441, 1, 2);
		else
			sfxKill3DSound(pPlayer.pSprite, 1, 441);
	}

	public static final int kSlopeThrow = -9460;
	public static final int kMinThrowVel = 419430; // 6 * 65536
	public static final int kMaxThrowVel = 1957341; // 29 * 65536

	public static void ThrowBundle(int nTrigger, PLAYER pPlayer) {
		sfxKill3DSound(pPlayer.pSprite, 16, -1);

		int velocity = mulscale(kMaxThrowVel - kMinThrowVel, pPlayer.throwTime, 16) + kMinThrowVel;
		sfxStart3DSound(pPlayer.pSprite, 455, 1, 0);

		SPRITE pThing = playerFireThing(pPlayer, 0, kSlopeThrow, kThingTNTBundle, velocity);
		if (pPlayer.fuseTime < 0)
			xsprite[pThing.extra].Impact = true;
		else
			evPost(pThing.xvel, SS_SPRITE, pPlayer.fuseTime, kCommandOn);

		UseAmmo(pPlayer, 5, 1);
		pPlayer.throwTime = 0;
	}

	public static void ThrowSpray(int nTrigger, PLAYER pPlayer) {
		sfxKill3DSound(pPlayer.pSprite, -1, 441);
		int velocity = mulscale(kMaxThrowVel - kMinThrowVel, pPlayer.throwTime, 16) + kMinThrowVel;
		sfxStart3DSound(pPlayer.pSprite, 455, 1, 0);
		SPRITE pThing = playerFireThing(pPlayer, 0, kSlopeThrow, kThingSprayBundle, velocity);
		pThing.shade = -128;

		xsprite[pThing.extra].Impact = true;
		evPost(pThing.xvel, SS_SPRITE, pPlayer.fuseTime, kCommandOn);

		UseAmmo(pPlayer, 6, gAmmoItemData[0].count);
		pPlayer.throwTime = 0;
	}

	public static void DropSpray(int nTrigger, PLAYER pPlayer) {
		sfxKill3DSound(pPlayer.pSprite, -1, 441);
		SPRITE pThing = playerFireThing(pPlayer, 0, 0, kThingSprayBundle, 0);
		if(pThing != null)
		{
			evPost(pThing.xvel, SS_SPRITE, pPlayer.fuseTime, kCommandOn);
			UseAmmo(pPlayer, 6, gAmmoItemData[0].count);
		}
	}

	public static void ExplodeSpray(int nTrigger, PLAYER pPlayer) {
		sfxKill3DSound(pPlayer.pSprite, -1, 441);
		SPRITE pThing = playerFireThing(pPlayer, 0, 0, kThingSprayBundle, 0);
		evPost(pThing.xvel, SS_SPRITE, 0, kCommandOn); // trigger it immediately
		UseAmmo(pPlayer, 6, gAmmoItemData[0].count);
		StartQAV(pPlayer, 15, -1, false); // kQAVSprayExplode
		pPlayer.currentWeapon = 0; // drop the hands
		pPlayer.throwTime = 0;
	}

	public static void DropBundle(int nTrigger, PLAYER pPlayer) {
		sfxKill3DSound(pPlayer.pSprite, 16, -1);
		SPRITE pThing = playerFireThing(pPlayer, 0, 0, kThingTNTBundle, 0);
		evPost(pThing.xvel, SS_SPRITE, pPlayer.fuseTime, kCommandOn);
		UseAmmo(pPlayer, 5, 1);
	}

	public static void ExplodeBundle(int nTrigger, PLAYER pPlayer) {
		sfxKill3DSound(pPlayer.pSprite, 16, -1);
		SPRITE pThing = playerFireThing(pPlayer, 0, 0, kThingTNTBundle, 0);
		evPost(pThing.xvel, SS_SPRITE, 0, kCommandOn); // trigger it immediately
		UseAmmo(pPlayer, 5, 1);
		StartQAV(pPlayer, 24, -1, false); // kQAVTNTExplode
		pPlayer.currentWeapon = 0; // drop the hands
		pPlayer.throwTime = 0;
	}

	public static void ThrowProxy(int nTrigger, PLAYER pPlayer) {
		int velocity = mulscale(kMaxThrowVel - kMinThrowVel, pPlayer.throwTime, 16) + kMinThrowVel;
		sfxStart3DSound(pPlayer.pSprite, 455, 1, 0);
		SPRITE pThing = playerFireThing(pPlayer, 0, kSlopeThrow, kThingTNTProx, velocity);

		evPost(pThing.xvel, SS_SPRITE, 240, kCommandOn);

		UseAmmo(pPlayer, 10, 1);
		pPlayer.throwTime = 0;
	}

	static void DropProxy(int nTrigger, PLAYER pPlayer) {
		SPRITE pThing = playerFireThing(pPlayer, 0, 0, kThingTNTProx, 0);
		evPost(pThing.xvel, SS_SPRITE, 240, kCommandOn);
		UseAmmo(pPlayer, 10, 1);
	}

	public static void ThrowRemote(int nTrigger, PLAYER pPlayer) {
		int velocity = mulscale(kMaxThrowVel - kMinThrowVel, pPlayer.throwTime,
				16) + kMinThrowVel;
		sfxStart3DSound(pPlayer.pSprite, 455, 1, 0);
		SPRITE pThing = playerFireThing(pPlayer, 0, kSlopeThrow, kThingTNTRem, velocity);
		xsprite[pThing.extra].rxID = (short) (pPlayer.pSprite.lotag - 141);
		UseAmmo(pPlayer, 11, 1);
		pPlayer.throwTime = 0;
	}

	public static void DropRemote(int nTrigger, PLAYER pPlayer) {
		SPRITE pThing = playerFireThing(pPlayer, 0, 0, kThingTNTRem, 0);

		xsprite[pThing.extra].rxID = (short) (pPlayer.pSprite.lotag - 141);

		UseAmmo(pPlayer, 11, 1);
	}

	public static void FireRemote(int nTrigger, PLAYER pPlayer) {
		evSend(0, 0, pPlayer.pSprite.lotag - 141, kCommandOn);
	}

	public static void FirePitchfork(int nTrigger, PLAYER pPlayer) {
		// dispersal modifiers
		int dx = BiRandom(2000);
		int dy = BiRandom(2000);
		int dz = BiRandom(2000);

		int z = pPlayer.weaponAboveZ - pPlayer.pSprite.z;
		if(newHoriz && !IsOriginalDemo()) z += 10 * pPlayer.horiz;

		for (int i = 0; i < 4; i++) {
			int xOffset = 40 * (2 * i - 3);
			actFireVector(pPlayer.pSprite, xOffset, z, dx + (int) pPlayer.aim.x, dy
					+ (int) pPlayer.aim.y, dz + (int) pPlayer.aim.z, kVectorTine);
		}
	}

	public static final int kMaxShotgunBarrels = 4;
	public static final int kVectorsPerBarrel = 16;

	public static void FireShotgun(int nTrigger, PLAYER pPlayer) {
		if(!(nTrigger > 0 && nTrigger <= kMaxShotgunBarrels))
			game.dassert("nTrigger > 0 && nTrigger <= kMaxShotgunBarrels");
		int nVectors = nTrigger * kVectorsPerBarrel;
		SPRITE pSprite = pPlayer.pSprite;
		int z = pPlayer.weaponAboveZ - pSprite.z;
		if(newHoriz && !IsOriginalDemo()) z += 10 * pPlayer.horiz;
		int ddx, ddy, ddz, vectorType;

		for (int i = 0; i < nVectors; i++) {
			// dispersal modifiers
			if (nTrigger == 1) {
				ddx = BiRandom2(1500);
				ddy = BiRandom2(1500);
				ddz = BiRandom2(500);
				vectorType = 1;
			} else {
				ddx = BiRandom2(2500);
				ddy = BiRandom2(2500);
				ddz = BiRandom2(1500);
				vectorType = 4;
			}

			actFireVector(pPlayer.pSprite, 0, z, ddx + (int) pPlayer.aim.x, ddy
					+ (int) pPlayer.aim.y, ddz + (int) pPlayer.aim.z,
					vectorType);
		}
		if (nTrigger == 1) {
			sfxStart3DSound(pSprite, 411, 1, 0); // kSfxShotFire
			pPlayer.tilt = 30;
			pPlayer.visibility = 20;
		} else {
			sfxStart3DSound(pSprite, 412, 1, 0); // kSfxShotFire2
			pPlayer.tilt = 50;
			pPlayer.visibility = 40;
		}
		UseAmmo(pPlayer, pPlayer.weaponAmmo, nTrigger);
		pPlayer.fireEffect = 1;
	}

	public static void ShotgunReload(int nTrigger, PLAYER pPlayer) {
		SpawnSShells(pPlayer, 25, 35);
		SpawnSShells(pPlayer, 48, 35);
	}

	public static void SpawnSShells(PLAYER pPlayer, int offset, int vel) {
		pPlayer.viewOffZ = pPlayer.pSprite.z
				- gPosture[pPlayer.nLifeMode][pPlayer.moveState].viewSpeed;
		int z = pPlayer.weaponAboveZ - pPlayer.viewOffZ;
		if(newHoriz && !IsOriginalDemo()) z += 10 * pPlayer.horiz;
		actSpawnSSheels(pPlayer.pSprite, (z >> 2) + pPlayer.weaponAboveZ - z,
				offset, vel);
	}

	public static void SpawnTShells(PLAYER pPlayer, int offset, int vel) {
		pPlayer.viewOffZ = pPlayer.pSprite.z - gPosture[pPlayer.nLifeMode][pPlayer.moveState].viewSpeed;
		int z = pPlayer.weaponAboveZ - pPlayer.viewOffZ;
		if(newHoriz && !IsOriginalDemo()) z += 10 * pPlayer.horiz;
		actSpawnTSheels(pPlayer.pSprite, pPlayer.weaponAboveZ - z / 2, offset, vel);
	}

	public static void FireTommy(int nTrigger, PLAYER pPlayer) {
		SPRITE pSprite = pPlayer.pSprite;
		int z = pPlayer.weaponAboveZ - pSprite.z;
		if(newHoriz && !IsOriginalDemo()) z += 10 * pPlayer.horiz;
		sfxStart3DSound(pSprite, 431, -1, 0);

		int ddx, ddy, ddz;
		if (nTrigger == 1) {
			ddz = BiRandom2(400);
			ddy = BiRandom2(1200);
			ddx = BiRandom2(1200);

			actFireVector(pPlayer.pSprite, 0, z, ddx + (int) pPlayer.aim.x, ddy
					+ (int) pPlayer.aim.y, ddz + (int) pPlayer.aim.z, 5);
			SpawnTShells(pPlayer, -15, -45);
			pPlayer.visibility = 20;
		}
		if (nTrigger == 2) {
			ddz = BiRandom2(400);
			ddy = BiRandom2(1200);
			ddx = BiRandom2(1200);

			actFireVector(pPlayer.pSprite, -120, z, ddx + (int) pPlayer.aim.x,
					ddy + (int) pPlayer.aim.y, ddz + (int) pPlayer.aim.z, 5);
			SpawnTShells(pPlayer, -140, -45);

			ddz = BiRandom2(400);
			ddy = BiRandom2(1200);
			ddx = BiRandom2(1200);

			actFireVector(pPlayer.pSprite, 120, z, ddx + (int) pPlayer.aim.x,
					ddy + (int) pPlayer.aim.y, ddz + (int) pPlayer.aim.z, 5);
			SpawnTShells(pPlayer, 140, 45);
			pPlayer.visibility = 30;
		}
		UseAmmo(pPlayer, pPlayer.weaponAmmo, nTrigger);
		pPlayer.fireEffect = 1;
	}

	public static final int kMaxSpread = 14;

	public static void FireTommy2(int nTrigger, PLAYER pPlayer) {
		if(!(nTrigger > 0 && nTrigger <= kMaxSpread))
			game.dassert("nTrigger > 0 && nTrigger <= kMaxSpread");

		SPRITE pSprite = pPlayer.pSprite;
		int z = pPlayer.weaponAboveZ - pSprite.z;
		if(newHoriz && !IsOriginalDemo()) z += 10 * pPlayer.horiz;
		int nAngle = (112 * (nTrigger - 1) / 14) - 56 + engine.getangle((int) pPlayer.aim.x, (int) pPlayer.aim.y);
		sfxStart3DSound(pSprite, 431, -1, 0);

		int ax = Cos(nAngle) >> 16;
		int ay = Sin(nAngle) >> 16;

		int ammocount = 1;
		if ( powerupCheck(pPlayer, 17) != 0 && CheckAmmo2(pPlayer, 3, 2) )
		{
			int ddz = BiRandom2(300);
			int ddy = BiRandom2(600);
			int ddx = BiRandom2(600);

			actFireVector(pPlayer.pSprite, -120, z, ddx + ax,
					ddy + ay, ddz + (int) pPlayer.aim.z, 3);
			int vel = BiRandom(45);
			int offset = BiRandom(120);
			SpawnTShells(pPlayer, offset, vel);
			ddz = BiRandom2(300);
			ddy = BiRandom2(600);
			ddx = BiRandom2(600);

			actFireVector(pPlayer.pSprite, 120, z, ddx + ax,
					ddy + ay, ddz + (int) pPlayer.aim.z, 3);
			vel = BiRandom(-45);
			offset = BiRandom(-120);
			SpawnTShells(pPlayer, offset, vel);

			pPlayer.tilt = 30;
			pPlayer.visibility = 45;
			ammocount = 2;
		}
		else
		{
			int ddz = BiRandom2(300);
			int ddy = BiRandom2(600);
			int ddx = BiRandom2(600);

			actFireVector(pPlayer.pSprite, 0, z, ddx + ax,
				ddy + ay, ddz + (int) pPlayer.aim.z, 3);

			int vel = BiRandom(90);
			int offset = BiRandom(30);
			SpawnTShells(pPlayer, offset, vel);
			pPlayer.visibility = 30;
			pPlayer.tilt = 20;

		}
		UseAmmo(pPlayer, pPlayer.weaponAmmo, ammocount);
		pPlayer.fireEffect = 1;
		if ( !CheckAmmo2(pPlayer, 3, 1) )
		{
			WeaponLower(pPlayer);
		    pPlayer.weaponState = -1;
		}
	}

	public static void FireATommy2(int nTrigger, PLAYER pPlayer) {
		if(!(nTrigger > 0 && nTrigger <= kMaxSpread))
			game.dassert("nTrigger > 0 && nTrigger <= kMaxSpread");

		SPRITE pSprite = pPlayer.pSprite;
		int z = pPlayer.weaponAboveZ - pSprite.z;
		if(newHoriz && !IsOriginalDemo()) z += 10 * pPlayer.horiz;
		int nAngle = (112 * (nTrigger - 1) / 14) - 56
				+ engine.getangle((int) pPlayer.aim.x, (int) pPlayer.aim.y);

		int ax = Cos(nAngle) >> 16;
		int ay = Sin(nAngle) >> 16;

		int ddz = BiRandom2(300);
		int ddy = BiRandom2(600);
		int ddx = BiRandom2(600);

		actFireVector(pPlayer.pSprite, -120, z, ddx + ax,
				ddy + ay, ddz + (int) pPlayer.aim.z, 3);
		int vel = BiRandom(45);
		int offset = BiRandom(120);
		SpawnTShells(pPlayer, offset, vel);

		ddz = BiRandom2(300);
		ddy = BiRandom2(600);
		ddx = BiRandom2(600);

		actFireVector(pPlayer.pSprite, 120, z, ddx + ax,
				ddy + ay, ddz + (int) pPlayer.aim.z, 3);
		vel = BiRandom(-45);
		offset = BiRandom(-120);
		SpawnTShells(pPlayer, offset, vel);

		pPlayer.tilt = 20;
		pPlayer.visibility = 30;
		UseAmmo(pPlayer, pPlayer.weaponAmmo, 2);
		pPlayer.fireEffect = 1;
		sfxStart3DSound(pSprite, 431, -1, 0);
	}

	static void FireFlare(int nTrigger, PLAYER pPlayer) {
		SPRITE pSprite = pPlayer.pSprite;
		int xoffset = 0;

		if (nTrigger == 2)
			xoffset = -120;
		if (nTrigger == 3)
			xoffset = 120;

		playerFireMissile(pPlayer, xoffset, (int) pPlayer.aim.x,
				(int) pPlayer.aim.y, (int) pPlayer.aim.z, kMissileFlare);
		UseAmmo(pPlayer, 1, 1);
		pPlayer.visibility = 30;
		pPlayer.fireEffect = 1;
		sfxStart3DSound(pSprite, 420, 1, 0);
	}

	static void FireFlare2(int nTrigger, PLAYER pPlayer) {
		SPRITE pSprite = pPlayer.pSprite;
		int xoffset = 0;
		if (nTrigger == 2)
			xoffset = -120;
		if (nTrigger == 3)
			xoffset = 120;

		playerFireMissile(pPlayer, xoffset, (int) pPlayer.aim.x,
				(int) pPlayer.aim.y, (int) pPlayer.aim.z,
				kMissileStarburstFlare);
		UseAmmo(pPlayer, 1, 8);
		pPlayer.visibility = 45;
		pPlayer.fireEffect = 1;
		sfxStart3DSound(pSprite, 420, 1, 0);
	}

	public static int UseAmmo(PLAYER pPlayer, int nAmmo, int nCount) {
		if (gInfiniteAmmo)
			return 9999;

		if (nAmmo == kAmmoNone)
			return 9999;

		return pPlayer.ammoCount[nAmmo] = ClipLow(pPlayer.ammoCount[nAmmo] - nCount, 0);
	}

	public static final int kVoodooStabChest = 0;
	public static final int kVoodooStabShoulder = 1;
	public static final int kVoodooStabEye = 2;
	public static final int kVoodooStabGroin = 3;
	public static final int kVoodooStabSelf = 4;

	public static void FireVoodoo(int nStabType, PLAYER pPlayer) {
		int nDamage;
		nStabType -= 1;
		SPRITE pSprite = pPlayer.pSprite;
		SPRITE pTarget = null;
		int nSelfSprite = pPlayer.nSprite;

		if (nStabType == kVoodooStabSelf) {
			actDamageSprite(nSelfSprite, pPlayer.pSprite, kDamageBullet, 16);
            return;
		}

		if(pPlayer.voodooTarget < 0)
			game.dassert("pPlayer.voodooTarget >= 0");
		pTarget = sprite[pPlayer.voodooTarget];

		switch (nStabType) {
		case kVoodooStabChest:
			sfxStart3DSound(pSprite, 460, 2, 0); // kSfxVoodooHit
			actSpawnBlood(pTarget);
			nDamage = actDamageSprite(nSelfSprite, pTarget, kDamageSpirit, 272);
			UseAmmo(pPlayer, 9, nDamage / 4);
			break;

		case kVoodooStabShoulder:
			sfxStart3DSound(pSprite, 460, 2, 0);
			actSpawnBlood(pTarget);
			nDamage = actDamageSprite(nSelfSprite, pTarget, kDamageSpirit, 144);
			if (pTarget.lotag >= kDudePlayer1 && pTarget.lotag <= kDudePlayer8) {
				int nPlayerHit = pTarget.lotag - kDudePlayer1;
				WeaponLower(gPlayer[nPlayerHit]);
			}
			UseAmmo(pPlayer, 9, nDamage / 4);
			break;

		case kVoodooStabGroin:
			sfxStart3DSound(pSprite, 463, 2, 0);
			actSpawnBlood(pTarget);
			nDamage = actDamageSprite(nSelfSprite, pTarget, kDamageSpirit, 784);
			UseAmmo(pPlayer, 9, nDamage / 4);
			break;

		case kVoodooStabEye:
			sfxStart3DSound(pSprite, 460, 2, 0);
			actSpawnBlood(pTarget);
			nDamage = actDamageSprite(nSelfSprite, pTarget, kDamageSpirit, 176);
			if (IsPlayerSprite(pTarget)) {
				PLAYER pPlayerHit = gPlayer[pTarget.lotag - kDudePlayer1];
				pPlayerHit.blindEffect = 128;
			}
			UseAmmo(pPlayer, 9, nDamage / 4);
			break;
		}
	}

	/* Voodoo v1.00
		static void FireVoodoo2(int nStabType, PLAYER pPlayer) {
			if (nStabType == 1) {
				pPlayer.voodooCount = 8;
				pPlayer.voodooUnk = 0;
				pPlayer.voodooAng = pPlayer.pSprite.ang;
			} else if (nStabType == 2) {
				UseAmmo(pPlayer, 9, 100);
				pPlayer.hasWeapon[10] = false;
			}
		}
	*/

	static void FireVoodoo2(int nStabType, PLAYER pPlayer) {

		if (nStabType == 2) {
			int ammo = pPlayer.ammoCount[9];
		    if ( pPlayer.ammoCount[9] >= pPlayer.aimCount )
		    	ammo = pPlayer.aimCount;

		    if(ammo > 0)
		    {
//		    	int force = pPlayer.ammoCount[9] - ammo * (pPlayer.ammoCount[9] / ammo);
		    	for(int i = 0; i < pPlayer.aimCount; i++)
		    	{
		    		SPRITE pTarget = sprite[pPlayer.aimSprites[i]];
		    		long dist = engine.qdist(pTarget.x - pPlayer.pSprite.x, pTarget.y - pPlayer.pSprite.y);
		    		if(dist > 0 && dist < 51200)
		    		{
		    	        int nDamage = actDamageSprite(
	    	                  pPlayer.nSprite,
	    	                  pTarget,
	    	                  kDamageSpirit,
	    	                  (16 * ((2 * pPlayer.ammoCount[9]) + BiRandom(pPlayer.ammoCount[9] >> 3)) * (51200 - (int) dist + 1)) / 51200);

		    			UseAmmo(pPlayer, 9, nDamage);
		    			if ( IsPlayerSprite(pTarget) )
		    	        {
		    				PLAYER pPlayerHit = gPlayer[pTarget.lotag - kDudePlayer1];
		    	            if ( !pPlayerHit.godMode || powerupCheck(pPlayerHit, 14) == 0 )
		    	              powerupActivate(pPlayerHit, 28);
		    	        }
		    	        actSpawnBlood(pTarget);
		    		}
		    	}
		    }

			UseAmmo(pPlayer, 9, pPlayer.ammoCount[9]);
			pPlayer.hasWeapon[10] = false;
			pPlayer.weaponState = -1;
		}
	}

	static void FireTesla121(int nStabType, PLAYER pPlayer) {
		if ( nStabType > 0 && nStabType <= 6 )
		{
			TeslaData data = gTeslaData[nStabType - 1];
			if ( CheckAmmo2(pPlayer, 7, data.nCount)
					|| CheckAmmo2(pPlayer, 7, gTeslaData[0].nCount) )
			{
				playerFireMissile(pPlayer, data.xoffset, (int) pPlayer.aim.x, (int) pPlayer.aim.y, (int) pPlayer.aim.z, data.missileType);
			    UseAmmo(pPlayer, pPlayer.weaponAmmo, data.nCount);
			    sfxStart3DSound(pPlayer.pSprite, data.soundId, 1, 0); // 0 = pPlayer
			    pPlayer.visibility = data.visibility;
			    pPlayer.fireEffect = data.fireEffect;
		    }
		    else
		    {
		    	pPlayer.weaponState = -1;
		     	pPlayer.pWeaponQAV = 76;
		     	pPlayer.fireEffect = 0;
		    }
		}
	}

	static void FireTesla_100(int nStabType, PLAYER pPlayer) {
		if ( CheckAmmo2(pPlayer, 7, 1) )
		{
			playerFireMissile(pPlayer, 0, (int) pPlayer.aim.x, (int) pPlayer.aim.y, (int) pPlayer.aim.z, 306);
			UseAmmo(pPlayer, pPlayer.weaponAmmo, 1);
			sfxStart3DSound(pPlayer.pSprite, 470, 1, 0);
			pPlayer.visibility = 20;
			pPlayer.fireEffect = 1;
		} else
		{
			pPlayer.weaponState = -1;
	     	pPlayer.pWeaponQAV = 76;
	     	pPlayer.fireEffect = 0;
		}
	}

	static void FireTesla2_100(int nStabType, PLAYER pPlayer) {
		playerFireMissile(pPlayer, 0, (int) pPlayer.aim.x, (int) pPlayer.aim.y, (int) pPlayer.aim.z, 302);
		UseAmmo(pPlayer, pPlayer.weaponAmmo, 35);
		sfxStart3DSound(pPlayer.pSprite, 471, 1, 0);
		pPlayer.visibility = 50;
		pPlayer.fireEffect = 1;
	}

	static void FireNapalm(int nTrigger, PLAYER pPlayer) {
		SPRITE pSprite = pPlayer.pSprite;
		int xoffset = 0;
		if (nTrigger == 2)
			xoffset = -50;
		if (nTrigger == 3)
			xoffset = 50;

		playerFireMissile(pPlayer, xoffset, (int) pPlayer.aim.x,
				(int) pPlayer.aim.y, (int) pPlayer.aim.z, 312);
		UseAmmo(pPlayer, 4, 1);
		pPlayer.visibility = 30;
		pPlayer.fireEffect = 1;
		sfxStart3DSound(pSprite, 480, 1, 0);
	}

	static void FireNapalm2(int nTrigger, PLAYER pPlayer) {

		SPRITE pSprite = playerFireThing(pPlayer, 0, -4730, 428, 1188385);
		if(pSprite != null)
		{
			XSPRITE pXSprite = xsprite[pSprite.extra];
			pXSprite.data4 = 4 * ClipHigh(pPlayer.ammoCount[4], 12);
			UseAmmo(pPlayer, 4, pXSprite.data4 / 4);
			seqSpawn(22, SS_SPRITE, pSprite.extra, null);
			pXSprite.burnSource = actSetBurnSource(pPlayer.pSprite.xvel);
			pXSprite.burnTime = ClipHigh(pXSprite.burnTime + 600, 1200);
			evPostCallback(pSprite.xvel, SS_SPRITE, 0, 0);
			sfxStart3DSound(pSprite, 480, 2, 0);
			pPlayer.visibility = 30;
			pPlayer.fireEffect = 1;
		}

    }

	static void FireLeech(int nTrigger, PLAYER pPlayer) {
		if (CheckAmmo(pPlayer, 8, 1)) {
			int ddx = BiRandom(2000);
			int ddy = BiRandom(2000);
			int ddz = BiRandom(1000);
			SPRITE pMissile = playerFireMissile(pPlayer, 0, (int) pPlayer.aim.x
					+ ddx, (int) pPlayer.aim.y + ddy,
					(int) pPlayer.aim.z + ddz, kMissileLifeLeech);
			if (pMissile != null) {
				xsprite[pMissile.extra].target = pPlayer.nAimSprite;
				if (nTrigger == 2)
					pMissile.ang = 1024;
				else
					pMissile.ang = 0;
			}

			if (CheckAmmo2(pPlayer, 8, 1)) {
				UseAmmo(pPlayer, 8, 1);
			} else
				actDamageSprite(pPlayer.nSprite, pPlayer.pSprite, kDamageSpirit, 16);

			pPlayer.visibility = ClipHigh(pPlayer.visibility + 5, 50);
		}
	}

	static void LeechOperate(SPRITE pSprite, XSPRITE pXSprite, int evCommand)
	{
		switch(evCommand)
		{
			case kCommandSpritePush:
				if(pXSprite.data4 >= 0 && pXSprite.data4 < numplayers)
				{
					PLAYER pPlayer = gPlayer[pXSprite.data4];
					if(pPlayer.pXsprite.health != 0)
					{
						pPlayer.ammoCount[8] = ClipHigh(pPlayer.ammoCount[8] + pXSprite.data3, 100);
						pPlayer.hasWeapon[9] = true;
						if ( pPlayer.currentWeapon != 9 )
					    {
							pPlayer.weaponState = 0;
							pPlayer.updateWeapon = 9;
					    }
					    checkEventList(pSprite.xvel, SS_SPRITE);
					}
				}
				actPostSprite(pSprite.xvel, kStatFree);
				break;
			case kCommandSpriteProximity:
				if(pXSprite.target >= 0 && pXSprite.target < kMaxSprites && pXSprite.stateTimer == 0)
				{
					SPRITE pTarget = sprite[pXSprite.target];

					if(IsDudeSprite(pTarget) && (pTarget.hitag & kAttrFree) == 0)
					{
						if(pTarget.extra > 0 && pTarget.extra < kMaxXSprites)
						{
							GetSpriteExtents(pSprite);
							DudeInfo pDudeInfo = dudeInfo[pTarget.lotag - kDudeBase];
							int dz = extents_zTop - pSprite.z - 256;
							long dist = engine.qdist(pTarget.x - pSprite.x, pTarget.y - pSprite.y);
							if(dist != 0)
							{
								if(engine.cansee(pSprite.x, pSprite.y, extents_zTop, pSprite.sectnum,
										pTarget.x, pTarget.y, pTarget.z, pTarget.sectnum))
								{
									short oldang = pSprite.ang;
									long vel = divscale(dist, 0x1AAAAA, 12);
									pSprite.ang = engine.getangle(
										mulscale(vel, sprXVel[pTarget.xvel], 12) + pTarget.x - pSprite.x,
										mulscale(vel, sprYVel[pTarget.xvel], 12) + pTarget.y - pSprite.y);
									int eyeAboveZ = pTarget.z - (pDudeInfo.aimHeight * pTarget.yrepeat << 2) - extents_zTop - 256;
									int ax = Cos(pSprite.ang) >> 16;
									int ay = Sin(pSprite.ang) >> 16;
									int az = divscale(eyeAboveZ, dist, 10);
									int time = 12;
									int missileType = kMissileAltLeech1;
									if(pXSprite.data3 != 0) { //has ammo
										time = 36;
										missileType += 1;
									}

									SPRITE pMissile = actFireMissile(pSprite, 0, dz, ax, ay, az, missileType);
									if(pMissile != null)
									{
										pMissile.owner = pSprite.owner;
										pXSprite.stateTimer = 1;
										pXSprite.data3 = (short) ClipLow(pXSprite.data3 - 1, 0);
										evPostCallback(pSprite.xvel, SS_SPRITE, time, 20);
									}
									pSprite.ang = oldang;
								}
							}
						}
					}
				}
				break;
			default:
				actPostSprite(pSprite.xvel, kStatFree);
				break;
		}
	}

	static void DropLeech(PLAYER pPlayer) {
		sfxStart3DSound(pPlayer.pSprite, 455, 2, 0);
		SPRITE pThing = playerFireThing(pPlayer, 0, -4730, kThingLifeLeech, 104857);

		if(pThing != null)
		{
			XSPRITE pXThing = xsprite[pThing.extra];
			pThing.cstat |= kSpritePushable;
			pXThing.Push = true;
			pXThing.DudeLockout = true;
			pXThing.Proximity = true;
			pXThing.stateTimer = 1;
		    evPostCallback(pThing.xvel, SS_SPRITE, 120, 20);
		    if ( pGameInfo.nGameType > 1 )
		    {
		    	pXThing.data3 = (short) pPlayer.ammoCount[8];
		    	pPlayer.ammoCount[8] = 0;
		    }
		    else
		    {
		    	short ammo = (short) pPlayer.ammoCount[8];
		    	if(ammo < 25)
		    	{
		    		if(pPlayer.pXsprite.health > (25 - ammo) << 4 )
		    		{
		    			actDamageSprite(pPlayer.nSprite, pPlayer.pSprite, kDamageSpirit, (25 - ammo) << 4);
		    			ammo = 25;
		    		}
		    	}
		    	pXThing.data3 = ammo;
		    	UseAmmo(pPlayer, 8, ammo);
		    }
		    pPlayer.hasWeapon[9] = false;
		    tileLoadVoxel(pThing.picnum);
		}
	}

	static void LeechCallback(int nIndex)
	{
		SPRITE pSprite = sprite[nIndex];
		if (pSprite.statnum == 4 && (pSprite.hitag & kAttrFree) == 0) {

			switch(pSprite.lotag) {
				case kThingLifeLeech:
				case kGDXThingCustomDudeLifeLeech:
					xsprite[pSprite.extra].stateTimer = 0;
					break;
			}
		}
	}

	static void FireBeast(int nTrigger, PLAYER pPlayer) {
		int ddx = BiRandom(2000);
		int ddy = BiRandom(2000);
		int ddz = BiRandom(2000);
		int z = pPlayer.weaponAboveZ - pPlayer.pSprite.z;
		if(newHoriz && !IsOriginalDemo()) z += 10 * pPlayer.horiz;
		actFireVector(pPlayer.pSprite, 0, z, ddx + (int) pPlayer.aim.x, ddy
				+ (int) pPlayer.aim.y, ddz + (int) pPlayer.aim.z, 9);
	}

	public static boolean weaponIdle(PLAYER pPlayer)
	{
		switch (pPlayer.currentWeapon) {
			case 0:
				return pPlayer.updateWeapon == 0;
			case 1:
				if (pPlayer.pWeaponQAV != kQAVForkIdle)
					return false;
				break;
			case 2:
				if (pPlayer.pWeaponQAV != kQAVFlareIdle && pPlayer.pWeaponQAV != kQAV2FlareIdle)
					return false;
				break;
			case 3:
				if (pPlayer.pWeaponQAV != kQAVShotgunIdle1
				&& pPlayer.pWeaponQAV != kQAVShotgunIdle2
				&& pPlayer.pWeaponQAV != kQAVShotgunIdle3
				&& pPlayer.pWeaponQAV != kQAV2ShotgunIdle)
					return false;
				break;
			case 4:
				if (pPlayer.pWeaponQAV != kQAVTommygunIdle
				&& pPlayer.pWeaponQAV != kQAV2TommygunIdle)
					return false;
				break;
			case 5:
				if (pPlayer.pWeaponQAV != getQAV(kNAPIDLE)
					&& pPlayer.pWeaponQAV != getQAV(k2NAPIDLE))
					return false;
				break;
			case 6:
				if (pPlayer.pWeaponQAV != 20)
					return false;
				break;
			case 7:
				if (pPlayer.pWeaponQAV != 9)
					return false;
				break;
			case 8:
				if (pPlayer.pWeaponQAV != 75 && pPlayer.pWeaponQAV != 83)
					return false;
				break;
			case 9:
				if (pPlayer.pWeaponQAV != 112)
					return false;
				break;
			case kWeaponVoodoo:
				if (pPlayer.pWeaponQAV != 101 && pPlayer.pWeaponQAV != 102)
					return false;
				break;
			case 11:
				if (pPlayer.pWeaponQAV != 27)
					return false;
				break;
			case 12:
				if (pPlayer.pWeaponQAV != 36)
					return false;
				break;
		}


		return true;
	}
}
