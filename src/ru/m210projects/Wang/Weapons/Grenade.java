package ru.m210projects.Wang.Weapons;

import static ru.m210projects.Build.Engine.sintable;
import static ru.m210projects.Wang.Digi.DIGI_GRENADE_UP;
import static ru.m210projects.Wang.Factory.WangNetwork.Prediction;
import static ru.m210projects.Wang.Gameutils.FLAG_KEY_PRESSED;
import static ru.m210projects.Wang.Gameutils.FLAG_KEY_RELEASE;
import static ru.m210projects.Wang.Gameutils.FLAG_KEY_RESET;
import static ru.m210projects.Wang.Gameutils.NORM_ANGLE;
import static ru.m210projects.Wang.Gameutils.PF_WEAPON_RETRACT;
import static ru.m210projects.Wang.Gameutils.PLAYER_MOVING;
import static ru.m210projects.Wang.Gameutils.SK_SHOOT;
import static ru.m210projects.Wang.Gameutils.TEST_SYNC_KEY;
import static ru.m210projects.Wang.Gameutils.WPN_GRENADE;
import static ru.m210projects.Wang.Gameutils.synctics;
import static ru.m210projects.Wang.Main.engine;
import static ru.m210projects.Wang.Panel.DoPlayerChooseYell;
import static ru.m210projects.Wang.Panel.PANF_BOB;
import static ru.m210projects.Wang.Panel.PANF_UNHIDE_SHOOT;
import static ru.m210projects.Wang.Panel.PANF_WEAPON_SPRITE;
import static ru.m210projects.Wang.Panel.PRI_MID;
import static ru.m210projects.Wang.Panel.PlayerUpdateWeapon;
import static ru.m210projects.Wang.Panel.RetractCurWpn;
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
import static ru.m210projects.Wang.Sound.v3df_follow;
import static ru.m210projects.Wang.Type.MyTypes.BIT;
import static ru.m210projects.Wang.Type.MyTypes.TEST;
import static ru.m210projects.Wang.Type.Saveable.SaveData;
import static ru.m210projects.Wang.Vis.SpawnVis;
import static ru.m210projects.Wang.Weapon.InitGrenade;

import ru.m210projects.Wang.Type.Panel_Sprite;
import ru.m210projects.Wang.Type.Panel_Sprite_Func;
import ru.m210projects.Wang.Type.Panel_State;
import ru.m210projects.Wang.Type.PlayerStr;

public class Grenade {

	private static final int GRENADE_YOFF = 200;
	private static final int GRENADE_XOFF = (160 + 20);

	private static final int Grenade_REST_RATE = 24;
	private static final int Grenade_ACTION_RATE = 6;

	private static final int GRENADE_REST = 2121;
	private static final int GRENADE_FIRE = 2122;
	private static final int GRENADE_RELOAD = 2125;

	private static final int ID_GrenadePresent0 = GRENADE_REST + 0;

	private static final int ID_GrenadeFire0 = GRENADE_FIRE + 0;
	private static final int ID_GrenadeFire1 = GRENADE_FIRE + 1;
	private static final int ID_GrenadeFire2 = GRENADE_FIRE + 2;

	private static final int ID_GrenadeReload0 = GRENADE_RELOAD + 0;
	private static final int ID_GrenadeReload1 = GRENADE_RELOAD + 1;

	private static final Panel_Sprite_Func pGrenadeAction = new Panel_Sprite_Func() {
		@Override
		public void invoke(Panel_Sprite psp) {
			pGrenadeBobSetup(psp);
			pWeaponBob(psp, PLAYER_MOVING(psp.PlayerP()) != 0);
		}
	};

	private static final Panel_Sprite_Func pGrenadeFire = new Panel_Sprite_Func() {
		@Override
		public void invoke(Panel_Sprite psp) {
			SpawnVis(psp.PlayerP().PlayerSprite, -1, -1, -1, -1, 32);
			InitGrenade(psp.PlayerP());
		}
	};

	private static final Panel_Sprite_Func pGrenadeRest = new Panel_Sprite_Func() {
		@Override
		public void invoke(Panel_Sprite psp) {
			boolean force = !!TEST(psp.flags, PANF_UNHIDE_SHOOT);

			if (pWeaponHideKeys(psp, ps_GrenadeHide[0]))
				return;

			pGrenadeBobSetup(psp);
			pWeaponBob(psp, PLAYER_MOVING(psp.PlayerP()) != 0);

			if (TEST_SYNC_KEY(psp.PlayerP(), SK_SHOOT) || force) {
				if (FLAG_KEY_PRESSED(psp.PlayerP(), SK_SHOOT) || force) {
					psp.flags &= ~(PANF_UNHIDE_SHOOT);

					if (!WeaponOK(psp.PlayerP()))
						return;

					DoPlayerChooseYell(psp.PlayerP());
					pSetState(psp, psp.ActionState);
				}
			} else
				WeaponOK(psp.PlayerP());
		}
	};

	private static final Panel_State ps_GrenadeRest[] = {
			new Panel_State(ID_GrenadePresent0, Grenade_REST_RATE, pGrenadeRest).setNext() };

	private static final Panel_Sprite_Func pGrenadeRecoilDown = new Panel_Sprite_Func() {
		@Override
		public void invoke(Panel_Sprite psp) {
			int x = castLongX(psp);
			int y = castLongY(psp);

			x += psp.vel * synctics * sintable[NORM_ANGLE(psp.ang + 512)] >> 6;
			y += psp.vel * synctics * -sintable[psp.ang] >> 6;

			setLong(psp, x, y);

			psp.vel -= 24 * synctics;

			if (psp.vel < 400) {
				psp.vel = 400;
				psp.ang = NORM_ANGLE(psp.ang + 1024);

				pStatePlusOne(psp);
			}
		}
	};

	private static final Panel_Sprite_Func pGrenadeRecoilUp = new Panel_Sprite_Func() {
		@Override
		public void invoke(Panel_Sprite psp) {
			int x = castLongX(psp);
			int y = castLongY(psp);

			x += psp.vel * synctics * sintable[NORM_ANGLE(psp.ang + 512)] >> 6;
			y += psp.vel * synctics * -sintable[psp.ang] >> 6;

			setLong(psp, x, y);

			psp.vel += 15 * synctics;

			if (psp.y < GRENADE_YOFF) {
				psp.y = GRENADE_YOFF;
				psp.x = GRENADE_XOFF;

				pGrenadeSetRecoil(psp);

				pStatePlusOne(psp);
				psp.flags &= ~(PANF_BOB);
			}
		}
	};

	private static final Panel_State ps_GrenadeRecoil_0 = new Panel_State(ID_GrenadePresent0, Grenade_REST_RATE,
			pGrenadeRecoilDown);
	private static final Panel_State ps_GrenadeRecoil_1 = new Panel_State(ID_GrenadePresent0, Grenade_REST_RATE,
			pGrenadeRecoilUp);
	// reload
	private static final Panel_State ps_GrenadeRecoil_2 = new Panel_State(ID_GrenadeReload0, Grenade_REST_RATE / 2,
			null);
	private static final Panel_State ps_GrenadeRecoil_3 = new Panel_State(ID_GrenadeReload1, Grenade_REST_RATE / 2,
			null);
	// ready to fire again
	private static final Panel_State ps_GrenadeRecoil_4 = new Panel_State(ID_GrenadePresent0, 3, null);

	private static final Panel_State ps_GrenadeRecoil[] = { ps_GrenadeRecoil_0.setNext().setPlusOne(ps_GrenadeRecoil_1),
			ps_GrenadeRecoil_1.setNext().setPlusOne(ps_GrenadeRecoil_2), ps_GrenadeRecoil_2.setNext(ps_GrenadeRecoil_3),
			ps_GrenadeRecoil_3.setNext(ps_GrenadeRecoil_4), ps_GrenadeRecoil_4.setNext(ps_GrenadeRest[0]) };
	private static final Panel_State ps_GrenadeFire_0 = new Panel_State(ID_GrenadeFire0, Grenade_ACTION_RATE,
			pGrenadeAction);
	private static final Panel_State ps_GrenadeFire_1 = new Panel_State(ID_GrenadeFire1, Grenade_ACTION_RATE,
			pGrenadeAction);
	private static final Panel_State ps_GrenadeFire_2 = new Panel_State(ID_GrenadeFire2, Grenade_ACTION_RATE,
			pGrenadeAction);
	private static final Panel_State ps_GrenadeFire_3 = new Panel_State(ID_GrenadePresent0, 0, pGrenadeFire);
	private static final Panel_State ps_GrenadeFire_4 = new Panel_State(ID_GrenadePresent0, 3, pGrenadeAction);

	private static final Panel_State ps_GrenadeFire[] = {
			ps_GrenadeFire_0.setNext(ps_GrenadeFire_1).setFlags(psf_ShadeHalf),
			ps_GrenadeFire_1.setNext(ps_GrenadeFire_2).setFlags(psf_ShadeNone),
			ps_GrenadeFire_2.setNext(ps_GrenadeFire_3).setFlags(psf_ShadeNone),
			ps_GrenadeFire_3.setNext(ps_GrenadeFire_4).setFlags(psf_QuickCall),
			ps_GrenadeFire_4.setNext(ps_GrenadeRecoil[0]) };

	private static final Panel_Sprite_Func pGrenadePresent = new Panel_Sprite_Func() {
		@Override
		public void invoke(Panel_Sprite psp) {
			int x = castLongX(psp);
			int y = castLongY(psp);

			if (TEST(psp.PlayerP().Flags, PF_WEAPON_RETRACT))
				return;

			x += psp.vel * synctics * sintable[NORM_ANGLE(psp.ang + 512)] >> 6;
			y += psp.vel * synctics * -sintable[psp.ang] >> 6;

			setLong(psp, x, y);

			psp.rotate_ang = NORM_ANGLE(psp.rotate_ang + (6 * synctics));

			if (psp.rotate_ang < 1024)
				psp.rotate_ang = 0;

			if (psp.y < GRENADE_YOFF) {
				pGrenadeSetRecoil(psp);
				psp.x = GRENADE_XOFF;
				psp.y = GRENADE_YOFF;
				psp.rotate_ang = 0;
				psp.yorig = psp.y;
				pSetState(psp, psp.RestState);
			}
		}
	};

	private static final Panel_State ps_PresentGrenade[] = {
			new Panel_State(ID_GrenadePresent0, Grenade_REST_RATE, pGrenadePresent).setNext() };

	private static final Panel_Sprite_Func pGrenadeHide = new Panel_Sprite_Func() {
		@Override
		public void invoke(Panel_Sprite psp) {
			short picnum = psp.picndx;

			psp.y += 3 * synctics;

			if (psp.y >= GRENADE_YOFF + engine.getTile(picnum).getHeight()) {
				psp.y = GRENADE_YOFF + engine.getTile(picnum).getHeight();
				psp.x = GRENADE_XOFF;

				pGrenadePresentSetup(psp);

				pWeaponUnHideKeys(psp, psp.PresentState);
			}
		}
	};

	private static final Panel_State ps_GrenadeHide[] = {
			new Panel_State(ID_GrenadePresent0, Grenade_REST_RATE, pGrenadeHide).setNext() };

	private static final Panel_Sprite_Func pGrenadeRetract = new Panel_Sprite_Func() {
		@Override
		public void invoke(Panel_Sprite psp) {
			psp.y += 3 * synctics;

			if (psp.y >= GRENADE_YOFF + engine.getTile(psp.picndx).getHeight()) {
				psp.PlayerP().Flags &= ~(PF_WEAPON_RETRACT);
				psp.PlayerP().Wpn[WPN_GRENADE] = null;
				pKillSprite(psp);
			}
		}
	};

	private static final Panel_State ps_RetractGrenade[] = {
			new Panel_State(ID_GrenadePresent0, Grenade_REST_RATE, pGrenadeRetract).setNext() };

	public static void InitWeaponGrenade(PlayerStr pp) {
		Panel_Sprite psp;

		if (Prediction)
			return;

		if (!TEST(pp.WpnFlags, BIT(WPN_GRENADE)) || TEST(pp.Flags, PF_WEAPON_RETRACT))
			return;

		if (pp.Wpn[WPN_GRENADE] == null) {
			psp = pp.Wpn[WPN_GRENADE] = pSpawnSprite(pp, ps_PresentGrenade[0], PRI_MID, GRENADE_XOFF, GRENADE_YOFF);
			psp.y += engine.getTile(psp.picndx).getHeight();
		}

		if (pp.CurWpn == pp.Wpn[WPN_GRENADE]) {
			return;
		}

		PlayerUpdateWeapon(pp, WPN_GRENADE);

		pp.WpnUziType = 2; // Make uzi's go away!
		RetractCurWpn(pp);

		// Set up the new Weapon variables
		psp = pp.CurWpn = pp.Wpn[WPN_GRENADE];
		psp = pp.CurWpn = pp.Wpn[WPN_GRENADE];
		psp.flags |= (PANF_WEAPON_SPRITE);
		psp.ActionState = ps_GrenadeFire[0];
		psp.RetractState = ps_RetractGrenade[0];
		psp.PresentState = ps_PresentGrenade[0];
		psp.RestState = ps_GrenadeRest[0];
		pSetState(psp, psp.PresentState);

		pGrenadePresentSetup(psp);

		PlaySound(DIGI_GRENADE_UP, pp, v3df_follow);

		FLAG_KEY_RELEASE(psp.PlayerP(), SK_SHOOT);
		FLAG_KEY_RESET(psp.PlayerP(), SK_SHOOT);
	}

	private static void pGrenadePresentSetup(Panel_Sprite psp) {
		psp.rotate_ang = 1800;
		psp.y += 34;
		psp.x -= 45;
		psp.ang = 256 + 128;
		psp.vel = 680;
	}

	private static void pGrenadeSetRecoil(Panel_Sprite psp) {
		psp.vel = 900;
		psp.ang = NORM_ANGLE(-256);
	}

	private static void pGrenadeBobSetup(Panel_Sprite psp) {
		if (TEST(psp.flags, PANF_BOB))
			return;

		psp.xorig = psp.x;
		psp.yorig = psp.y;

		psp.sin_amt = 12;
		psp.sin_ndx = 0;
		psp.bob_height_shift = 3;
	}

	public static void GrenadeSaveable() {
		SaveData(ps_PresentGrenade);
		SaveData(ps_GrenadeRest);
		SaveData(ps_GrenadeHide);
		SaveData(ps_GrenadeFire);
		SaveData(ps_GrenadeRecoil);
		SaveData(ps_RetractGrenade);

		SaveData(pGrenadeAction);
		SaveData(pGrenadeFire);
		SaveData(pGrenadeRest);
		SaveData(pGrenadeRecoilDown);
		SaveData(pGrenadeRecoilUp);
		SaveData(pGrenadePresent);
		SaveData(pGrenadeHide);
		SaveData(pGrenadeRetract);
	}
}
