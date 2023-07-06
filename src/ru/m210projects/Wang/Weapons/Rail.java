package ru.m210projects.Wang.Weapons;

import static ru.m210projects.Build.Engine.sintable;
import static ru.m210projects.Wang.Digi.DIGI_RAILREADY;
import static ru.m210projects.Wang.Digi.DIGI_RAIL_UP;
import static ru.m210projects.Wang.Factory.WangNetwork.Prediction;
import static ru.m210projects.Wang.Gameutils.FLAG_KEY_PRESSED;
import static ru.m210projects.Wang.Gameutils.FLAG_KEY_RELEASE;
import static ru.m210projects.Wang.Gameutils.FLAG_KEY_RESET;
import static ru.m210projects.Wang.Gameutils.NORM_ANGLE;
import static ru.m210projects.Wang.Gameutils.PF_WEAPON_RETRACT;
import static ru.m210projects.Wang.Gameutils.PLAYER_MOVING;
import static ru.m210projects.Wang.Gameutils.SK_SHOOT;
import static ru.m210projects.Wang.Gameutils.TEST_SYNC_KEY;
import static ru.m210projects.Wang.Gameutils.WPN_RAIL;
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
import static ru.m210projects.Wang.Panel.psf_ShadeNone;
import static ru.m210projects.Wang.Panel.setLong;
import static ru.m210projects.Wang.Sound.DeleteNoSoundOwner;
import static ru.m210projects.Wang.Sound.PlaySound;
import static ru.m210projects.Wang.Sound.Set3DSoundOwner;
import static ru.m210projects.Wang.Sound.v3df_dontpan;
import static ru.m210projects.Wang.Sound.v3df_follow;
import static ru.m210projects.Wang.Type.MyTypes.BIT;
import static ru.m210projects.Wang.Type.MyTypes.TEST;
import static ru.m210projects.Wang.Type.Saveable.SaveData;
import static ru.m210projects.Wang.Vis.SpawnVis;
import static ru.m210projects.Wang.Weapon.InitEMP;
import static ru.m210projects.Wang.Weapon.InitRail;

import ru.m210projects.Wang.Type.Panel_Sprite;
import ru.m210projects.Wang.Type.Panel_Sprite_Func;
import ru.m210projects.Wang.Type.Panel_State;
import ru.m210projects.Wang.Type.PlayerStr;
import ru.m210projects.Wang.Type.VOC3D;

public class Rail {

	private static final int RAIL_YOFF = 200;
	private static final int RAIL_XOFF = (160 + 6);

	private static final int RAIL_REST = 2010;
	private static final int RAIL_CHARGE = 2015;
	private static final int RAIL_FIRE = 2018;

	private static final int ID_RailPresent0 = RAIL_REST + 0;

	private static final int ID_RailRest0 = RAIL_REST + 0;
	private static final int ID_RailRest1 = RAIL_REST + 1;
	private static final int ID_RailRest2 = RAIL_REST + 2;
	private static final int ID_RailRest3 = RAIL_REST + 3;
	private static final int ID_RailRest4 = RAIL_REST + 4;

	private static final int ID_RailFire0 = RAIL_FIRE + 0;
	private static final int ID_RailFire1 = RAIL_FIRE + 1;

	private static final int ID_RailCharge0 = RAIL_CHARGE + 0;
	private static final int ID_RailCharge1 = RAIL_CHARGE + 1;
	private static final int ID_RailCharge2 = RAIL_CHARGE + 2;

	private static final int Rail_BEAT_RATE = 24;
	private static final int Rail_ACTION_RATE = 3; // !JIM! Was 10
	private static final int Rail_CHARGE_RATE = 3;

	private static final Panel_Sprite_Func pRailHide = new Panel_Sprite_Func() {
		@Override
		public void invoke(Panel_Sprite psp) {
			short picnum = psp.picndx;

			psp.y += 3 * synctics;

			if (psp.y >= RAIL_YOFF + engine.getTile(picnum).getHeight()) {
				psp.y = RAIL_YOFF + engine.getTile(picnum).getHeight();

				pWeaponUnHideKeys(psp, psp.PresentState);
			}
		}
	};

	private static final Panel_State ps_RailHide[] = {
			new Panel_State(ID_RailPresent0, Rail_BEAT_RATE, pRailHide).setNext().setFlags(psf_ShadeNone) };

	private static final Panel_Sprite_Func pRailRest = new Panel_Sprite_Func() {
		@Override
		public void invoke(Panel_Sprite psp) {
			boolean force = !!TEST(psp.flags, PANF_UNHIDE_SHOOT);

			if (pWeaponHideKeys(psp, ps_RailHide[0]))
				return;

			pRailBobSetup(psp);
			pWeaponBob(psp, PLAYER_MOVING(psp.PlayerP()) != 0);

			if (TEST_SYNC_KEY(psp.PlayerP(), SK_SHOOT) || force) {
				if (FLAG_KEY_PRESSED(psp.PlayerP(), SK_SHOOT) || force) {
					psp.flags &= ~(PANF_UNHIDE_SHOOT);

					if (!WeaponOK(psp.PlayerP()))
						return;

                    DoPlayerChooseYell(psp.PlayerP());
					if (psp.PlayerP().WpnRailType == 0)
						pSetState(psp, ps_RailFire[0]);
					else
						pSetState(psp, ps_RailFireEMP[0]);
				}
			} else
				WeaponOK(psp.PlayerP());
		}
	};

	private static final Panel_State ps_RailRest_0 = new Panel_State(ID_RailRest0, Rail_BEAT_RATE, pRailRest);
	private static final Panel_State ps_RailRest_1 = new Panel_State(ID_RailRest1, Rail_BEAT_RATE, pRailRest);
	private static final Panel_State ps_RailRest_2 = new Panel_State(ID_RailRest2, Rail_BEAT_RATE, pRailRest);
	private static final Panel_State ps_RailRest_3 = new Panel_State(ID_RailRest3, Rail_BEAT_RATE, pRailRest);
	private static final Panel_State ps_RailRest_4 = new Panel_State(ID_RailRest4, Rail_BEAT_RATE, pRailRest);

	private static final Panel_State ps_RailRest[] = { ps_RailRest_0.setNext(ps_RailRest_1).setFlags(psf_ShadeNone),
			ps_RailRest_1.setNext(ps_RailRest_2).setFlags(psf_ShadeNone),
			ps_RailRest_2.setNext(ps_RailRest_3).setFlags(psf_ShadeNone),
			ps_RailRest_3.setNext(ps_RailRest_4).setFlags(psf_ShadeNone),
			ps_RailRest_4.setNext(ps_RailRest_0).setFlags(psf_ShadeNone),

	};

	private static final Panel_Sprite_Func pRailAction = new Panel_Sprite_Func() {
		@Override
		public void invoke(Panel_Sprite psp) {
			pRailBobSetup(psp);
			pWeaponBob(psp, PLAYER_MOVING(psp.PlayerP()) != 0);
		}
	};

	private static final Panel_Sprite_Func pRailRestTest = new Panel_Sprite_Func() {
		@Override
		public void invoke(Panel_Sprite psp) {
			boolean force = !!TEST(psp.flags, PANF_UNHIDE_SHOOT);

			if (pWeaponHideKeys(psp, ps_RailHide[0]))
				return;

			pRailBobSetup(psp);
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

	private static final Panel_Sprite_Func pRailFire = new Panel_Sprite_Func() {
		@Override
		public void invoke(Panel_Sprite psp) {
			SpawnVis(psp.PlayerP().PlayerSprite, -1, -1, -1, -1, 16);
			if (psp.PlayerP().WpnRailType == 0)
				InitRail(psp.PlayerP());
			else
				InitEMP(psp.PlayerP());
		}
	};

	private static final Panel_State ps_RailFireEMP_0 = new Panel_State(ID_RailCharge0, Rail_CHARGE_RATE, pRailAction);
	private static final Panel_State ps_RailFireEMP_1 = new Panel_State(ID_RailCharge1, Rail_CHARGE_RATE, pRailAction);
	private static final Panel_State ps_RailFireEMP_2 = new Panel_State(ID_RailCharge2, Rail_CHARGE_RATE, pRailAction);
	private static final Panel_State ps_RailFireEMP_3 = new Panel_State(ID_RailCharge1, Rail_CHARGE_RATE, pRailAction);

	private static final Panel_State ps_RailFireEMP_4 = new Panel_State(ID_RailFire0, Rail_ACTION_RATE, pRailAction);
	private static final Panel_State ps_RailFireEMP_5 = new Panel_State(ID_RailFire1, Rail_ACTION_RATE, pRailAction);
	private static final Panel_State ps_RailFireEMP_6 = new Panel_State(ID_RailFire1, Rail_ACTION_RATE, pRailAction);
	private static final Panel_State ps_RailFireEMP_7 = new Panel_State(ID_RailFire1, 0, pRailFire);

	private static final Panel_State ps_RailFireEMP_8 = new Panel_State(ID_RailCharge0, Rail_ACTION_RATE,
			pRailRestTest);
	private static final Panel_State ps_RailFireEMP_9 = new Panel_State(ID_RailCharge1, Rail_ACTION_RATE, pRailRest);

	private static final Panel_State ps_RailFireEMP[] = {
			ps_RailFireEMP_0.setNext(ps_RailFireEMP_1).setFlags(psf_ShadeNone),
			ps_RailFireEMP_1.setNext(ps_RailFireEMP_2).setFlags(psf_ShadeNone),
			ps_RailFireEMP_2.setNext(ps_RailFireEMP_3).setFlags(psf_ShadeNone),
			ps_RailFireEMP_3.setNext(ps_RailFireEMP_4).setFlags(psf_ShadeNone),
			ps_RailFireEMP_4.setNext(ps_RailFireEMP_5).setFlags(psf_ShadeNone),
			ps_RailFireEMP_5.setNext(ps_RailFireEMP_6).setFlags(psf_ShadeNone),
			ps_RailFireEMP_6.setNext(ps_RailFireEMP_7).setFlags(psf_ShadeNone),
			ps_RailFireEMP_7.setNext(ps_RailFireEMP_8).setFlags(psf_ShadeNone | psf_QuickCall),
			ps_RailFireEMP_8.setNext(ps_RailFireEMP_9).setFlags(psf_ShadeNone),
			ps_RailFireEMP_9.setNext(ps_RailRest[0]).setFlags(psf_ShadeNone), };

	private static final Panel_Sprite_Func pRailRecoilDown = new Panel_Sprite_Func() {
		@Override
		public void invoke(Panel_Sprite psp) {
			int x = castLongX(psp);
			int y = castLongY(psp);

			x += psp.vel * synctics * sintable[NORM_ANGLE(psp.ang + 512)] >> 6;
			y += psp.vel * synctics * -sintable[psp.ang] >> 6;
			setLong(psp, x, y);

			psp.vel -= 24 * synctics;

			if (psp.vel < 800) {
				psp.vel = 800;
				psp.ang = NORM_ANGLE(psp.ang + 1024);

				pStatePlusOne(psp);
			}
		}
	};

	private static final Panel_Sprite_Func pRailRecoilUp = new Panel_Sprite_Func() {
		@Override
		public void invoke(Panel_Sprite psp) {
			int x = castLongX(psp);
			int y = castLongY(psp);

			x += psp.vel * synctics * sintable[NORM_ANGLE(psp.ang + 512)] >> 6;
			y += psp.vel * synctics * -sintable[psp.ang] >> 6;
			setLong(psp, x, y);

			psp.vel += 15 * synctics;

			if (psp.y < RAIL_YOFF) {
				psp.y = RAIL_YOFF;
				psp.x = RAIL_XOFF;

				pRailSetRecoil(psp);

				pStatePlusOne(psp);
				psp.flags &= ~(PANF_BOB);
			}
		}
	};

	private static final Panel_Sprite_Func pRailOkTest = new Panel_Sprite_Func() {
		@Override
		public void invoke(Panel_Sprite psp) {
			if (pWeaponHideKeys(psp, ps_RailHide[0]))
				return;

			WeaponOK(psp.PlayerP());
		}
	};

	private static final Panel_State ps_RailFire_0 = new Panel_State(ID_RailCharge0, Rail_CHARGE_RATE, pRailAction);
	private static final Panel_State ps_RailFire_1 = new Panel_State(ID_RailCharge1, Rail_CHARGE_RATE, pRailAction);
	private static final Panel_State ps_RailFire_2 = new Panel_State(ID_RailCharge2, Rail_CHARGE_RATE, pRailAction);
	private static final Panel_State ps_RailFire_3 = new Panel_State(ID_RailCharge1, Rail_CHARGE_RATE, pRailAction);

	private static final Panel_State ps_RailFire_4 = new Panel_State(ID_RailFire0, Rail_ACTION_RATE, pRailAction);
	private static final Panel_State ps_RailFire_5 = new Panel_State(ID_RailFire1, Rail_ACTION_RATE, pRailAction);
	private static final Panel_State ps_RailFire_6 = new Panel_State(ID_RailFire1, Rail_ACTION_RATE, pRailAction);
	private static final Panel_State ps_RailFire_7 = new Panel_State(ID_RailFire1, 0, pRailFire);

	// recoil
	private static final Panel_State ps_RailFire_8 = new Panel_State(ID_RailPresent0, Rail_BEAT_RATE, pRailRecoilDown);
	private static final Panel_State ps_RailFire_9 = new Panel_State(ID_RailPresent0, Rail_BEAT_RATE, pRailRecoilUp);
	// !JIM! I added these to introduce firing delay, that looks like a charge down.
	private static final Panel_State ps_RailFire_10 = new Panel_State(ID_RailCharge0, Rail_CHARGE_RATE, pRailOkTest);
	private static final Panel_State ps_RailFire_11 = new Panel_State(ID_RailCharge1, Rail_CHARGE_RATE, pRailAction);
	private static final Panel_State ps_RailFire_12 = new Panel_State(ID_RailCharge2, Rail_CHARGE_RATE, pRailOkTest);
	private static final Panel_State ps_RailFire_13 = new Panel_State(ID_RailCharge1, Rail_CHARGE_RATE, pRailAction);
	private static final Panel_State ps_RailFire_14 = new Panel_State(ID_RailCharge0, Rail_CHARGE_RATE + 1,
			pRailAction);
	private static final Panel_State ps_RailFire_15 = new Panel_State(ID_RailCharge1, Rail_CHARGE_RATE + 1,
			pRailAction);
	private static final Panel_State ps_RailFire_16 = new Panel_State(ID_RailCharge2, Rail_CHARGE_RATE + 1,
			pRailAction);
	private static final Panel_State ps_RailFire_17 = new Panel_State(ID_RailCharge1, Rail_CHARGE_RATE + 2,
			pRailAction);
	private static final Panel_State ps_RailFire_18 = new Panel_State(ID_RailCharge0, Rail_CHARGE_RATE + 2,
			pRailAction);
	private static final Panel_State ps_RailFire_19 = new Panel_State(ID_RailCharge1, Rail_CHARGE_RATE + 2,
			pRailAction);
	private static final Panel_State ps_RailFire_20 = new Panel_State(ID_RailCharge2, Rail_CHARGE_RATE + 3,
			pRailAction);
	private static final Panel_State ps_RailFire_21 = new Panel_State(ID_RailCharge1, Rail_CHARGE_RATE + 3,
			pRailAction);
	private static final Panel_State ps_RailFire_22 = new Panel_State(ID_RailCharge0, Rail_CHARGE_RATE + 4,
			pRailAction);
	private static final Panel_State ps_RailFire_23 = new Panel_State(ID_RailCharge1, Rail_CHARGE_RATE + 4,
			pRailAction);
	private static final Panel_State ps_RailFire_24 = new Panel_State(ID_RailCharge2, Rail_CHARGE_RATE + 4,
			pRailAction);
	private static final Panel_State ps_RailFire_25 = new Panel_State(ID_RailCharge0, Rail_CHARGE_RATE + 5,
			pRailAction);
	private static final Panel_State ps_RailFire_26 = new Panel_State(ID_RailCharge1, Rail_CHARGE_RATE + 5,
			pRailAction);
	private static final Panel_State ps_RailFire_27 = new Panel_State(ID_RailCharge2, Rail_CHARGE_RATE + 5,
			pRailAction);

	private static final Panel_State ps_RailFire_28 = new Panel_State(ID_RailCharge0, Rail_ACTION_RATE, pRailRestTest);
	private static final Panel_State ps_RailFire_29 = new Panel_State(ID_RailCharge1, Rail_ACTION_RATE, pRailRest);

	private static final Panel_State ps_RailFire[] = { ps_RailFire_0.setNext(ps_RailFire_1).setFlags(psf_ShadeNone),
			ps_RailFire_1.setNext(ps_RailFire_2).setFlags(psf_ShadeNone),
			ps_RailFire_2.setNext(ps_RailFire_3).setFlags(psf_ShadeNone),
			ps_RailFire_3.setNext(ps_RailFire_4).setFlags(psf_ShadeNone),
			ps_RailFire_4.setNext(ps_RailFire_5).setFlags(psf_ShadeNone),
			ps_RailFire_5.setNext(ps_RailFire_6).setFlags(psf_ShadeNone),
			ps_RailFire_6.setNext(ps_RailFire_7).setFlags(psf_ShadeNone),
			ps_RailFire_7.setNext(ps_RailFire_8).setFlags(psf_ShadeNone | psf_QuickCall),
			ps_RailFire_8.setNext().setPlusOne(ps_RailFire_9).setFlags(psf_ShadeNone),
			ps_RailFire_9.setNext().setPlusOne(ps_RailFire_10).setFlags(psf_ShadeNone),
			ps_RailFire_10.setNext(ps_RailFire_11).setFlags(psf_ShadeNone),
			ps_RailFire_11.setNext(ps_RailFire_12).setFlags(psf_ShadeNone),
			ps_RailFire_12.setNext(ps_RailFire_13).setFlags(psf_ShadeNone),
			ps_RailFire_13.setNext(ps_RailFire_14).setFlags(psf_ShadeNone),
			ps_RailFire_14.setNext(ps_RailFire_15).setFlags(psf_ShadeNone),
			ps_RailFire_15.setNext(ps_RailFire_16).setFlags(psf_ShadeNone),
			ps_RailFire_16.setNext(ps_RailFire_17).setFlags(psf_ShadeNone),
			ps_RailFire_17.setNext(ps_RailFire_18).setFlags(psf_ShadeNone),
			ps_RailFire_18.setNext(ps_RailFire_19).setFlags(psf_ShadeNone),
			ps_RailFire_19.setNext(ps_RailFire_20).setFlags(psf_ShadeNone),
			ps_RailFire_20.setNext(ps_RailFire_21).setFlags(psf_ShadeNone),
			ps_RailFire_21.setNext(ps_RailFire_22).setFlags(psf_ShadeNone),
			ps_RailFire_22.setNext(ps_RailFire_23).setFlags(psf_ShadeNone),
			ps_RailFire_23.setNext(ps_RailFire_24).setFlags(psf_ShadeNone),
			ps_RailFire_24.setNext(ps_RailFire_25).setFlags(psf_ShadeNone),
			ps_RailFire_25.setNext(ps_RailFire_26).setFlags(psf_ShadeNone),
			ps_RailFire_26.setNext(ps_RailFire_27).setFlags(psf_ShadeNone),
			ps_RailFire_27.setNext(ps_RailFire_28).setFlags(psf_ShadeNone),
			ps_RailFire_28.setNext(ps_RailFire_29).setFlags(psf_ShadeNone),
			ps_RailFire_29.setNext(ps_RailRest[0]).setFlags(psf_ShadeNone), };

	private static final Panel_Sprite_Func pRailPresent = new Panel_Sprite_Func() {
		@Override
		public void invoke(Panel_Sprite psp) {
			if (TEST(psp.PlayerP().Flags, PF_WEAPON_RETRACT))
				return;

			// Needed for recoil
			psp.ang = NORM_ANGLE(256 + 128);
			pRailSetRecoil(psp);

			psp.y -= 3 * synctics;

			if (psp.y < RAIL_YOFF) {
				psp.y = RAIL_YOFF;
				psp.yorig = psp.y;
				pSetState(psp, psp.RestState);
			}
		}
	};

	private static final Panel_State ps_PresentRail[] = {
			new Panel_State(ID_RailPresent0, Rail_BEAT_RATE, pRailPresent).setNext().setFlags(psf_ShadeNone) };

	private static final Panel_Sprite_Func pRailRetract = new Panel_Sprite_Func() {
		@Override
		public void invoke(Panel_Sprite psp) {
			short picnum = psp.picndx;

			psp.y += 3 * synctics;

			if (psp.y >= RAIL_YOFF + engine.getTile(picnum).getHeight() + 50) {
				psp.PlayerP().Flags &= ~(PF_WEAPON_RETRACT);
				psp.PlayerP().Wpn[psp.WeaponType] = null;
				DeleteNoSoundOwner(psp.PlayerP().PlayerSprite);
				pKillSprite(psp);
			}
		}
	};

	private static final Panel_State ps_RetractRail[] = {
			new Panel_State(ID_RailPresent0, Rail_BEAT_RATE, pRailRetract).setNext().setFlags(psf_ShadeNone) };

	public static void InitWeaponRail(PlayerStr pp) {
		Panel_Sprite psp = null;

		if (Prediction)
			return;

		pp.WeaponType = WPN_RAIL;

		if (!TEST(pp.WpnFlags, BIT(pp.WeaponType)) || TEST(pp.Flags, PF_WEAPON_RETRACT))
			return;

		if (pp.Wpn[pp.WeaponType] == null) {
			psp = pp.Wpn[pp.WeaponType] = pSpawnSprite(pp, ps_PresentRail[0], PRI_MID, RAIL_XOFF, RAIL_YOFF);
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
		psp.ActionState = ps_RailFire[0];
		psp.RetractState = ps_RetractRail[0];
		psp.PresentState = ps_PresentRail[0];
		psp.RestState = ps_RailRest[0];
		pSetState(psp, psp.PresentState);

		PlaySound(DIGI_RAIL_UP, pp, v3df_follow);
		VOC3D voc = PlaySound(DIGI_RAILREADY, pp, v3df_follow | v3df_dontpan);
		Set3DSoundOwner(psp.PlayerP().PlayerSprite, voc);

		FLAG_KEY_RELEASE(psp.PlayerP(), SK_SHOOT);
		FLAG_KEY_RESET(psp.PlayerP(), SK_SHOOT);
	}

	private static void pRailSetRecoil(Panel_Sprite psp) {
		psp.vel = 900;
		psp.ang = NORM_ANGLE(-256);
	}

	private static void pRailBobSetup(Panel_Sprite psp) {
		if (TEST(psp.flags, PANF_BOB))
			return;

		psp.xorig = psp.x;
		psp.yorig = psp.y;

		psp.sin_amt = 12;
		psp.sin_ndx = 0;
		psp.bob_height_shift = 3;
	}

	public static void RailSaveable() {
		SaveData(ps_PresentRail);
		SaveData(ps_RailRest);
		SaveData(ps_RailHide);
		SaveData(ps_RailFire);
		SaveData(ps_RailFireEMP);
		SaveData(ps_RetractRail);

		SaveData(pRailHide);
		SaveData(pRailRest);
		SaveData(pRailAction);
		SaveData(pRailRestTest);
		SaveData(pRailFire);
		SaveData(pRailRecoilDown);
		SaveData(pRailRecoilUp);
		SaveData(pRailOkTest);
		SaveData(pRailPresent);
		SaveData(pRailRetract);
	}
}
