package ru.m210projects.Wang.Weapons;

import static ru.m210projects.Build.Engine.sintable;
import static ru.m210projects.Wang.Cheats.InfinityAmmo;
import static ru.m210projects.Wang.Digi.DIGI_ROCKET_UP;
import static ru.m210projects.Wang.Digi.DIGI_SHOTGUN_UP;
import static ru.m210projects.Wang.Factory.WangNetwork.Prediction;
import static ru.m210projects.Wang.Gameutils.FLAG_KEY_PRESSED;
import static ru.m210projects.Wang.Gameutils.FLAG_KEY_RELEASE;
import static ru.m210projects.Wang.Gameutils.FLAG_KEY_RESET;
import static ru.m210projects.Wang.Gameutils.NORM_ANGLE;
import static ru.m210projects.Wang.Gameutils.PF_WEAPON_RETRACT;
import static ru.m210projects.Wang.Gameutils.PLAYER_MOVING;
import static ru.m210projects.Wang.Gameutils.SK_SHOOT;
import static ru.m210projects.Wang.Gameutils.TEST_SYNC_KEY;
import static ru.m210projects.Wang.Gameutils.WPN_SHOTGUN;
import static ru.m210projects.Wang.Gameutils.synctics;
import static ru.m210projects.Wang.Main.engine;
import static ru.m210projects.Wang.Panel.DoPlayerChooseYell;
import static ru.m210projects.Wang.Panel.PANF_BOB;
import static ru.m210projects.Wang.Panel.PANF_REST_POS;
import static ru.m210projects.Wang.Panel.PANF_UNHIDE_SHOOT;
import static ru.m210projects.Wang.Panel.PANF_WEAPON_SPRITE;
import static ru.m210projects.Wang.Panel.PRI_MID;
import static ru.m210projects.Wang.Panel.PlayerUpdateWeapon;
import static ru.m210projects.Wang.Panel.RetractCurWpn;
import static ru.m210projects.Wang.Panel.SHOTGUN_AUTO;
import static ru.m210projects.Wang.Panel.SHOTGUN_AUTO_NUM;
import static ru.m210projects.Wang.Panel.WeaponOK;
import static ru.m210projects.Wang.Panel.castLongX;
import static ru.m210projects.Wang.Panel.castLongY;
import static ru.m210projects.Wang.Panel.pKillSprite;
import static ru.m210projects.Wang.Panel.pSetState;
import static ru.m210projects.Wang.Panel.pSpawnSprite;
import static ru.m210projects.Wang.Panel.pStatePlusOne;
import static ru.m210projects.Wang.Panel.pWeaponBob;
import static ru.m210projects.Wang.Panel.pWeaponHideKeys;
import static ru.m210projects.Wang.Panel.pWeaponUnHideKeys;
import static ru.m210projects.Wang.Panel.psf_QuickCall;
import static ru.m210projects.Wang.Panel.psf_ShadeHalf;
import static ru.m210projects.Wang.Panel.psf_ShadeNone;
import static ru.m210projects.Wang.Panel.setLong;
import static ru.m210projects.Wang.Sound.PlaySound;
import static ru.m210projects.Wang.Sound.v3df_dontpan;
import static ru.m210projects.Wang.Sound.v3df_doppler;
import static ru.m210projects.Wang.Sound.v3df_follow;
import static ru.m210projects.Wang.Type.MyTypes.BIT;
import static ru.m210projects.Wang.Type.MyTypes.TEST;
import static ru.m210projects.Wang.Type.Saveable.SaveData;
import static ru.m210projects.Wang.Vis.SpawnVis;
import static ru.m210projects.Wang.Weapon.InitShotgun;
import static ru.m210projects.Wang.Weapon.SpawnShell;

import ru.m210projects.Wang.Type.Panel_Sprite;
import ru.m210projects.Wang.Type.Panel_Sprite_Func;
import ru.m210projects.Wang.Type.Panel_State;
import ru.m210projects.Wang.Type.PlayerStr;

public class Shotgun {

	private static final int SHOTGUN_YOFF = 200;
	private static final int SHOTGUN_XOFF = (160 + 42);

	private static final int Shotgun_BEAT_RATE = 24;
	private static final int Shotgun_ACTION_RATE = 4;

	private static final int SHOTGUN_AUTO_XOFF = 28;
	private static final int SHOTGUN_AUTO_YOFF = -17;

	public static final int SHOTGUN_REST = 2213;
	private static final int SHOTGUN_FIRE = 2214;
	public static final int SHOTGUN_RELOAD0 = 2216;
	public static final int SHOTGUN_RELOAD1 = 2211;
	public static final int SHOTGUN_RELOAD2 = 2212;

	private static final int ID_ShotgunPresent0 = SHOTGUN_REST;

	private static final int ID_ShotgunReload0 = SHOTGUN_RELOAD0;
	private static final int ID_ShotgunReload1 = SHOTGUN_RELOAD1;
	private static final int ID_ShotgunReload2 = SHOTGUN_RELOAD2;

	private static final int ID_ShotgunFire0 = SHOTGUN_FIRE;
	private static final int ID_ShotgunFire1 = SHOTGUN_FIRE + 1;

	private static final Panel_Sprite_Func pShotgunPresent = new Panel_Sprite_Func() {
		@Override
		public void invoke(Panel_Sprite psp) {
			if (TEST(psp.PlayerP().Flags, PF_WEAPON_RETRACT))
				return;

			// Needed for recoil
			psp.ang = NORM_ANGLE(256 + 128);
			pShotgunSetRecoil(psp);

			psp.y -= 3 * synctics;

			if (psp.y < SHOTGUN_YOFF) {
				psp.y = SHOTGUN_YOFF;
				psp.yorig = psp.y;
				pSetState(psp, psp.RestState);
			}
		}
	};

	private static final Panel_Sprite_Func pShotgunRest = new Panel_Sprite_Func() {
		@Override
		public void invoke(Panel_Sprite psp) {
			boolean force = !!TEST(psp.flags, PANF_UNHIDE_SHOOT);
			short ammo = psp.PlayerP().WpnAmmo[WPN_SHOTGUN];
			byte lastammo = psp.PlayerP().WpnShotgunLastShell;

			if (pWeaponHideKeys(psp, ps_ShotgunHide[0]))
				return;

			if (psp.PlayerP().WpnShotgunType == 1 && ammo > 0 && ((ammo % 4) != 0) && lastammo != ammo
					&& TEST(psp.flags, PANF_REST_POS)) {
				force = true;
			}

			pShotgunOverlays(psp);

			pShotgunBobSetup(psp);
			pWeaponBob(psp, PLAYER_MOVING(psp.PlayerP()) != 0);

			if (TEST_SYNC_KEY(psp.PlayerP(), SK_SHOOT) || force) {
				if (FLAG_KEY_PRESSED(psp.PlayerP(), SK_SHOOT) || force) {
					psp.flags &= ~(PANF_UNHIDE_SHOOT);

					if (!WeaponOK(psp.PlayerP()))
						return;

					psp.flags |= (PANF_REST_POS); // Used for reload checking in autofire

					if (psp.PlayerP().WpnShotgunType == 0)
						psp.PlayerP().WpnShotgunLastShell = (byte) (ammo - 1);

					DoPlayerChooseYell(psp.PlayerP());
					if (psp.PlayerP().WpnShotgunType == 0)
						pSetState(psp, ps_ShotgunFire[0]);
					else
						pSetState(psp, ps_ShotgunAutoFire[0]);
				}
				if (!WeaponOK(psp.PlayerP()))
					return;
			} else
				WeaponOK(psp.PlayerP());
		}
	};

	private static final Panel_Sprite_Func pShotgunHide = new Panel_Sprite_Func() {
		@Override
		public void invoke(Panel_Sprite psp) {
			short picnum = psp.picndx;

			psp.y += 3 * synctics;

			if (psp.y >= SHOTGUN_YOFF + engine.getTile(picnum).getHeight()) {
				psp.y = SHOTGUN_YOFF + engine.getTile(picnum).getHeight();

				pWeaponUnHideKeys(psp, psp.PresentState);
			}
		}
	};

	private static final Panel_Sprite_Func pShotgunRetract = new Panel_Sprite_Func() {
		@Override
		public void invoke(Panel_Sprite psp) {
			short picnum = psp.picndx;

			psp.y += 3 * synctics;

			if (psp.y >= SHOTGUN_YOFF + engine.getTile(picnum).getHeight() + 50) {
				psp.PlayerP().Flags &= ~(PF_WEAPON_RETRACT);
				psp.PlayerP().Wpn[psp.WeaponType] = null;
				pKillSprite(psp);
			}
		}
	};

	private static final Panel_State ps_RetractShotgun[] = {
			new Panel_State(ID_ShotgunPresent0, Shotgun_BEAT_RATE, pShotgunRetract).setNext(), };

	private static final Panel_State ps_PresentShotgun[] = {
			new Panel_State(ID_ShotgunPresent0, Shotgun_BEAT_RATE, pShotgunPresent).setNext(), };

	private static final Panel_State ps_ShotgunRest[] = {
			new Panel_State(ID_ShotgunPresent0, Shotgun_BEAT_RATE, pShotgunRest).setNext(), };

	private static final Panel_State ps_ShotgunHide[] = {
			new Panel_State(ID_ShotgunPresent0, Shotgun_BEAT_RATE, pShotgunHide).setNext(), };

	private static final Panel_Sprite_Func pShotgunAction = new Panel_Sprite_Func() {
		@Override
		public void invoke(Panel_Sprite psp) {
			pShotgunBobSetup(psp);
			pWeaponBob(psp, PLAYER_MOVING(psp.PlayerP()) != 0);
		}
	};

	private static final Panel_Sprite_Func pShotgunFire = new Panel_Sprite_Func() {
		@Override
		public void invoke(Panel_Sprite psp) {
			SpawnVis(psp.PlayerP().PlayerSprite, -1, -1, -1, -1, 32);
			InitShotgun(psp.PlayerP());
		}
	};

	private static final Panel_Sprite_Func SpawnShotgunShell = new Panel_Sprite_Func() {
		@Override
		public void invoke(Panel_Sprite psp) {
			PlayerStr pp = psp.PlayerP();
			SpawnShell(pp.PlayerSprite, -4);
		}
	};

	private static final Panel_State ps_ShotgunFire_0 = new Panel_State(ID_ShotgunFire0, Shotgun_ACTION_RATE,
			pShotgunAction);
	private static final Panel_State ps_ShotgunFire_1 = new Panel_State(ID_ShotgunFire1, Shotgun_ACTION_RATE,
			pShotgunFire);
	private static final Panel_State ps_ShotgunFire_2 = new Panel_State(ID_ShotgunFire1, Shotgun_ACTION_RATE,
			pShotgunAction);
	private static final Panel_State ps_ShotgunFire_3 = new Panel_State(ID_ShotgunReload0, 0, SpawnShotgunShell);
	private static final Panel_State ps_ShotgunFire_4 = new Panel_State(ID_ShotgunReload0, Shotgun_ACTION_RATE,
			pShotgunAction);

	private static final Panel_Sprite_Func pShotgunRecoilDown = new Panel_Sprite_Func() {
		@Override
		public void invoke(Panel_Sprite psp) {
			short targetvel;

			int x = castLongX(psp);
			int y = castLongY(psp);

			if (psp.PlayerP().WpnShotgunType == 1)
				targetvel = 890;
			else
				targetvel = 780;

			x += psp.vel * synctics * sintable[NORM_ANGLE(psp.ang + 512)] >> 6;
			y += psp.vel * synctics * -sintable[psp.ang] >> 6;

			setLong(psp, x, y);

			psp.vel -= 24 * synctics;

			if (psp.vel < targetvel) {
				psp.vel = targetvel;
				psp.ang = NORM_ANGLE(psp.ang + 1024);

				pStatePlusOne(psp);
			}
		}
	};

	private static final Panel_Sprite_Func pShotgunRecoilUp = new Panel_Sprite_Func() {
		@Override
		public void invoke(Panel_Sprite psp) {
			int x = castLongX(psp);
			int y = castLongY(psp);

			x += psp.vel * synctics * sintable[NORM_ANGLE(psp.ang + 512)] >> 6;
			y += psp.vel * synctics * -sintable[psp.ang] >> 6;

			setLong(psp, x, y);
			psp.vel += 15 * synctics;

			if (psp.y < SHOTGUN_YOFF) {
				psp.y = SHOTGUN_YOFF;
				psp.x = SHOTGUN_XOFF;

				pShotgunSetRecoil(psp);

				pStatePlusOne(psp);
				psp.flags &= ~(PANF_BOB);
			}
		}
	};

	private static final Panel_Sprite_Func pShotgunRestTest = new Panel_Sprite_Func() {
		@Override
		public void invoke(Panel_Sprite psp) {
			boolean force = !!TEST(psp.flags, PANF_UNHIDE_SHOOT);

			if (psp.PlayerP().WpnShotgunType == 1 && !pShotgunReloadTest(psp))
				force = true;

			if (pShotgunReloadTest(psp))
				return;

			if (pWeaponHideKeys(psp, ps_ShotgunHide[0]))
				return;

			pShotgunBobSetup(psp);
			pWeaponBob(psp, PLAYER_MOVING(psp.PlayerP()) != 0);

			if (TEST_SYNC_KEY(psp.PlayerP(), SK_SHOOT) || force) {
				if (FLAG_KEY_PRESSED(psp.PlayerP(), SK_SHOOT) || force) {
					psp.flags &= ~(PANF_UNHIDE_SHOOT);

					if (!WeaponOK(psp.PlayerP()))
						return;

					DoPlayerChooseYell(psp.PlayerP());
					return;
				}
			}

			pSetState(psp, psp.RestState);
		}
	};

	private static final Panel_Sprite_Func pShotgunReloadDown = new Panel_Sprite_Func() {
		@Override
		public void invoke(Panel_Sprite psp) {
			short picnum = psp.picndx;

			psp.y += 3 * synctics;
			if (psp.y >= SHOTGUN_YOFF + (engine.getTile(picnum).getHeight() / 2)) {
				PlaySound(DIGI_ROCKET_UP, psp.PlayerP(), v3df_follow | v3df_dontpan | v3df_doppler);

				psp.y = SHOTGUN_YOFF + (engine.getTile(picnum).getHeight() / 2);

				pStatePlusOne(psp);
			}
		}
	};

	private static final Panel_Sprite_Func pShotgunReloadUp = new Panel_Sprite_Func() {
		@Override
		public void invoke(Panel_Sprite psp) {
			psp.x = SHOTGUN_XOFF;

			psp.y -= 3 * synctics;
			if (psp.y < SHOTGUN_YOFF) {
				PlaySound(DIGI_SHOTGUN_UP, psp.PlayerP(), v3df_follow | v3df_dontpan | v3df_doppler);

				psp.y = SHOTGUN_YOFF;

				pStatePlusOne(psp);
				psp.flags &= ~(PANF_BOB);
			}
		}
	};

	private static final Panel_State ps_ShotgunReload_0 = new Panel_State(ID_ShotgunPresent0, Shotgun_BEAT_RATE,
			pShotgunReloadDown);
	private static final Panel_State ps_ShotgunReload_1 = new Panel_State(ID_ShotgunPresent0, 30, null);
	// make reload sound here
	private static final Panel_State ps_ShotgunReload_2 = new Panel_State(ID_ShotgunPresent0, Shotgun_BEAT_RATE, null);
	private static final Panel_State ps_ShotgunReload_3 = new Panel_State(ID_ShotgunPresent0, 30, null);
	private static final Panel_State ps_ShotgunReload_4 = new Panel_State(ID_ShotgunPresent0, Shotgun_BEAT_RATE,
			pShotgunReloadUp);
	private static final Panel_State ps_ShotgunReload_5 = new Panel_State(ID_ShotgunPresent0, 3, null);

	private static final Panel_State ps_ShotgunReload[] = { ps_ShotgunReload_0.setNext().setPlusOne(ps_ShotgunReload_1),
			ps_ShotgunReload_1.setNext(ps_ShotgunReload_2),
			ps_ShotgunReload_2.setNext(ps_ShotgunReload_3).setFlags(psf_QuickCall),
			ps_ShotgunReload_3.setNext(ps_ShotgunReload_4), ps_ShotgunReload_4.setNext().setPlusOne(ps_ShotgunReload_5),
			ps_ShotgunReload_5.setNext(ps_ShotgunRest[0]) };

	private static final boolean pShotgunReloadTest(Panel_Sprite psp) {
		if(InfinityAmmo)
			return false;

		short ammo = psp.PlayerP().WpnAmmo[WPN_SHOTGUN];

		// Reload if done with clip
		if (ammo > 0 && (ammo % 4) == 0) {
			// clip has run out
			psp.flags &= ~(PANF_REST_POS);
			pSetState(psp, ps_ShotgunReload[0]);
			return (true);
		}

		return (false);
	}

	// recoil
	private static final Panel_State ps_ShotgunRecoil_0 = new Panel_State(ID_ShotgunReload0, Shotgun_ACTION_RATE,
			pShotgunRecoilDown);
	private static final Panel_State ps_ShotgunRecoil_1 = new Panel_State(ID_ShotgunReload0, Shotgun_ACTION_RATE,
			pShotgunRecoilUp);
	// reload
	private static final Panel_State ps_ShotgunRecoil_2 = new Panel_State(ID_ShotgunReload0, Shotgun_ACTION_RATE * 5,
			null);
	private static final Panel_State ps_ShotgunRecoil_3 = new Panel_State(ID_ShotgunReload1, Shotgun_ACTION_RATE, null);
	private static final Panel_State ps_ShotgunRecoil_4 = new Panel_State(ID_ShotgunReload2, Shotgun_ACTION_RATE * 5,
			null);
	private static final Panel_State ps_ShotgunRecoil_5 = new Panel_State(ID_ShotgunPresent0, Shotgun_ACTION_RATE,
			pShotgunRestTest);
	private static final Panel_State ps_ShotgunRecoil_6 = new Panel_State(ID_ShotgunPresent0, Shotgun_ACTION_RATE / 2,
			pShotgunAction);
	private static final Panel_State ps_ShotgunRecoil_7 = new Panel_State(ID_ShotgunPresent0, Shotgun_ACTION_RATE / 2,
			pShotgunAction);
	// ready to fire again
	private static final Panel_State ps_ShotgunRecoil_8 = new Panel_State(ID_ShotgunPresent0, 3, null);

	private static final Panel_State ps_ShotgunRecoil[] = { ps_ShotgunRecoil_0.setNext().setPlusOne(ps_ShotgunRecoil_1),
			ps_ShotgunRecoil_1.setNext().setPlusOne(ps_ShotgunRecoil_2), ps_ShotgunRecoil_2.setNext(ps_ShotgunRecoil_3),
			ps_ShotgunRecoil_3.setNext(ps_ShotgunRecoil_4), ps_ShotgunRecoil_4.setNext(ps_ShotgunRecoil_5),
			ps_ShotgunRecoil_5.setNext(ps_ShotgunRecoil_6), ps_ShotgunRecoil_6.setNext(ps_ShotgunRecoil_7),
			ps_ShotgunRecoil_7.setNext(ps_ShotgunRecoil_8), ps_ShotgunRecoil_8.setNext(ps_ShotgunRest[0]), };

	private static final Panel_State ps_ShotgunFire[] = {
			ps_ShotgunFire_0.setNext(ps_ShotgunFire_1).setFlags(psf_ShadeHalf),
			ps_ShotgunFire_1.setNext(ps_ShotgunFire_2).setFlags(psf_ShadeNone | psf_QuickCall),
			ps_ShotgunFire_2.setNext(ps_ShotgunFire_3).setFlags(psf_ShadeNone),
			ps_ShotgunFire_3.setNext(ps_ShotgunFire_4).setFlags(psf_QuickCall),
			ps_ShotgunFire_4.setNext(ps_ShotgunRecoil[0]), };

	// recoil
	private static final Panel_State ps_ShotgunRecoilAuto_0 = new Panel_State(ID_ShotgunReload0, 1, pShotgunRecoilDown);
	private static final Panel_State ps_ShotgunRecoilAuto_1 = new Panel_State(ID_ShotgunReload0, 1, pShotgunRecoilUp);
	// Reload
	private static final Panel_State ps_ShotgunRecoilAuto_2 = new Panel_State(ID_ShotgunReload0, 1, null);
	private static final Panel_State ps_ShotgunRecoilAuto_3 = new Panel_State(ID_ShotgunReload0, 1, null);
	private static final Panel_State ps_ShotgunRecoilAuto_4 = new Panel_State(ID_ShotgunReload0, 1, null);
	private static final Panel_State ps_ShotgunRecoilAuto_5 = new Panel_State(ID_ShotgunPresent0, 1, pShotgunRestTest);
	private static final Panel_State ps_ShotgunRecoilAuto_6 = new Panel_State(ID_ShotgunPresent0, 1, pShotgunAction);
	private static final Panel_State ps_ShotgunRecoilAuto_7 = new Panel_State(ID_ShotgunPresent0, 1, pShotgunRest);

	private static final Panel_State ps_ShotgunRecoilAuto[] = {
			ps_ShotgunRecoilAuto_0.setNext().setPlusOne(ps_ShotgunRecoilAuto_1),
			ps_ShotgunRecoilAuto_1.setNext().setPlusOne(ps_ShotgunRecoilAuto_2),
			ps_ShotgunRecoilAuto_2.setNext(ps_ShotgunRecoilAuto_3),
			ps_ShotgunRecoilAuto_3.setNext(ps_ShotgunRecoilAuto_4),
			ps_ShotgunRecoilAuto_4.setNext(ps_ShotgunRecoilAuto_5),
			ps_ShotgunRecoilAuto_5.setNext(ps_ShotgunRecoilAuto_6),
			ps_ShotgunRecoilAuto_6.setNext(ps_ShotgunRecoilAuto_7),
			ps_ShotgunRecoilAuto_7.setNext(ps_ShotgunRest[0]).setFlags(psf_QuickCall) };

	private static final Panel_State ps_ShotgunAutoFire_0 = new Panel_State(ID_ShotgunFire1, 2, pShotgunAction);
	private static final Panel_State ps_ShotgunAutoFire_1 = new Panel_State(ID_ShotgunFire1, 2, pShotgunFire);
	private static final Panel_State ps_ShotgunAutoFire_2 = new Panel_State(ID_ShotgunFire1, 2, pShotgunAction);
	private static final Panel_State ps_ShotgunAutoFire_3 = new Panel_State(ID_ShotgunReload0, 0, SpawnShotgunShell);
	private static final Panel_State ps_ShotgunAutoFire_4 = new Panel_State(ID_ShotgunReload0, 1, pShotgunAction);

	private static final Panel_State ps_ShotgunAutoFire[] = {
			ps_ShotgunAutoFire_0.setNext(ps_ShotgunAutoFire_1).setFlags(psf_ShadeHalf),
			ps_ShotgunAutoFire_1.setNext(ps_ShotgunAutoFire_2).setFlags(psf_ShadeNone),
			ps_ShotgunAutoFire_2.setNext(ps_ShotgunAutoFire_3).setFlags(psf_ShadeNone),
			ps_ShotgunAutoFire_3.setNext(ps_ShotgunAutoFire_4).setFlags(psf_QuickCall),
			ps_ShotgunAutoFire_4.setNext(ps_ShotgunRecoilAuto[0]), };

	public static void InitWeaponShotgun(PlayerStr pp) {
		Panel_Sprite psp = null;

		if (Prediction)
			return;

		pp.WeaponType = WPN_SHOTGUN;

		if (!TEST(pp.WpnFlags, BIT(pp.WeaponType)) || TEST(pp.Flags, PF_WEAPON_RETRACT))
			return;

		if (pp.Wpn[pp.WeaponType] == null) {
			psp = pp.Wpn[pp.WeaponType] = pSpawnSprite(pp, ps_PresentShotgun[0], PRI_MID, SHOTGUN_XOFF, SHOTGUN_YOFF);
			psp.y += engine.getTile(psp.picndx).getHeight();
		}

		if (pp.CurWpn == pp.Wpn[pp.WeaponType]) {
			return;
		}

		psp.WeaponType = pp.WeaponType;
		PlayerUpdateWeapon(pp, pp.WeaponType);

		pp.WpnUziType = 2; // Make uzi's go away!
		RetractCurWpn(pp);

		// Set up the new Weapon variables
		psp = pp.CurWpn = pp.Wpn[pp.WeaponType];
		psp.flags |= (PANF_WEAPON_SPRITE);
		psp.ActionState = ps_ShotgunFire[0];
		psp.RetractState = ps_RetractShotgun[0];
		psp.PresentState = ps_PresentShotgun[0];
		psp.RestState = ps_ShotgunRest[0];
		pSetState(psp, psp.PresentState);

		PlaySound(DIGI_SHOTGUN_UP, pp, v3df_follow);

		FLAG_KEY_RELEASE(psp.PlayerP(), SK_SHOOT);
		FLAG_KEY_RESET(psp.PlayerP(), SK_SHOOT);
	}

	private static boolean pShotgunOverlays(Panel_Sprite psp) {

		if (psp.over[SHOTGUN_AUTO_NUM].xoff == -1) {
			psp.over[SHOTGUN_AUTO_NUM].xoff = SHOTGUN_AUTO_XOFF;
			psp.over[SHOTGUN_AUTO_NUM].yoff = SHOTGUN_AUTO_YOFF;
		}

		// if(psp.PlayerP().WpnShotgunAuto == 0 && psp.PlayerP().WpnRocketType == 1)
		// psp.PlayerP().WpnShotgunType--;

		switch (psp.PlayerP().WpnShotgunType) {
		case 0:
			psp.over[SHOTGUN_AUTO_NUM].pic = -1;
			psp.over[SHOTGUN_AUTO_NUM].flags |= (psf_ShadeNone);
			return (false);
		case 1:
			psp.over[SHOTGUN_AUTO_NUM].pic = SHOTGUN_AUTO;
			psp.over[SHOTGUN_AUTO_NUM].flags |= (psf_ShadeNone);
			return (false);
		}
		return (false);
	}

	private static void pShotgunBobSetup(Panel_Sprite psp) {
		if (TEST(psp.flags, PANF_BOB))
			return;

		psp.xorig = psp.x;
		psp.yorig = psp.y;

		psp.sin_amt = 12;
		psp.sin_ndx = 0;
		psp.bob_height_shift = 3;
	}

	private static void pShotgunSetRecoil(Panel_Sprite psp) {
		psp.vel = 900;
		psp.ang = NORM_ANGLE(-256);
	}

	public static void ShotgunSaveable() {
		SaveData(ps_PresentShotgun);
		SaveData(ps_ShotgunRest);
		SaveData(ps_ShotgunHide);
		SaveData(ps_ShotgunRecoil);
		SaveData(ps_ShotgunRecoilAuto);
		SaveData(ps_ShotgunFire);
		SaveData(ps_ShotgunAutoFire);
		SaveData(ps_ShotgunReload);
		SaveData(ps_RetractShotgun);

		SaveData(pShotgunPresent);
		SaveData(pShotgunRest);
		SaveData(pShotgunHide);
		SaveData(pShotgunRetract);
		SaveData(pShotgunAction);
		SaveData(pShotgunFire);
		SaveData(SpawnShotgunShell);
		SaveData(pShotgunRecoilDown);
		SaveData(pShotgunRecoilUp);
		SaveData(pShotgunRestTest);
		SaveData(pShotgunReloadDown);
		SaveData(pShotgunReloadUp);
	}
}
