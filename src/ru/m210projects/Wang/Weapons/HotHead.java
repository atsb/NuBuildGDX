package ru.m210projects.Wang.Weapons;

import static ru.m210projects.Wang.Digi.DIGI_GRDALERT;
import static ru.m210projects.Wang.Factory.WangNetwork.Prediction;
import static ru.m210projects.Wang.Gameutils.FLAG_KEY_PRESSED;
import static ru.m210projects.Wang.Gameutils.FLAG_KEY_RELEASE;
import static ru.m210projects.Wang.Gameutils.FLAG_KEY_RESET;
import static ru.m210projects.Wang.Gameutils.PF_WEAPON_RETRACT;
import static ru.m210projects.Wang.Gameutils.PLAYER_MOVING;
import static ru.m210projects.Wang.Gameutils.SK_SHOOT;
import static ru.m210projects.Wang.Gameutils.TEST_SYNC_KEY;
import static ru.m210projects.Wang.Gameutils.WPN_HOTHEAD;
import static ru.m210projects.Wang.Gameutils.synctics;
import static ru.m210projects.Wang.Main.engine;
import static ru.m210projects.Wang.Panel.DoPlayerChooseYell;
import static ru.m210projects.Wang.Panel.HEAD_MODE1;
import static ru.m210projects.Wang.Panel.HEAD_MODE2;
import static ru.m210projects.Wang.Panel.HEAD_MODE3;
import static ru.m210projects.Wang.Panel.PANF_BOB;
import static ru.m210projects.Wang.Panel.PANF_UNHIDE_SHOOT;
import static ru.m210projects.Wang.Panel.PANF_WEAPON_SPRITE;
import static ru.m210projects.Wang.Panel.PRI_MID;
import static ru.m210projects.Wang.Panel.PlayerUpdateWeapon;
import static ru.m210projects.Wang.Panel.RetractCurWpn;
import static ru.m210projects.Wang.Panel.WeaponOK;
import static ru.m210projects.Wang.Panel.pKillSprite;
import static ru.m210projects.Wang.Panel.pSetState;
import static ru.m210projects.Wang.Panel.pSpawnSprite;
import static ru.m210projects.Wang.Panel.pStatePlusOne;
import static ru.m210projects.Wang.Panel.pWeaponBob;
import static ru.m210projects.Wang.Panel.pWeaponHideKeys;
import static ru.m210projects.Wang.Panel.pWeaponUnHideKeys;
import static ru.m210projects.Wang.Panel.psf_QuickCall;
import static ru.m210projects.Wang.Panel.psf_ShadeHalf;
import static ru.m210projects.Wang.Sound.PlaySound;
import static ru.m210projects.Wang.Sound.v3df_dontpan;
import static ru.m210projects.Wang.Sound.v3df_follow;
import static ru.m210projects.Wang.Type.MyTypes.BIT;
import static ru.m210projects.Wang.Type.MyTypes.TEST;
import static ru.m210projects.Wang.Type.Saveable.SaveData;
import static ru.m210projects.Wang.Vis.SpawnVis;
import static ru.m210projects.Wang.Weapon.InitFireball;
import static ru.m210projects.Wang.Weapon.InitSpellNapalm;
import static ru.m210projects.Wang.Weapon.InitSpellRing;
import static ru.m210projects.Wang.Weapons.Uzi.pUziBobSetup;

import ru.m210projects.Build.Types.Tile;
import ru.m210projects.Wang.Type.Panel_Sprite;
import ru.m210projects.Wang.Type.Panel_Sprite_Func;
import ru.m210projects.Wang.Type.Panel_State;
import ru.m210projects.Wang.Type.PlayerStr;

public class HotHead {

	private static final int HOTHEAD_REST = 2048;
	private static final int HOTHEAD_ATTACK = 2049;
	private static final int ID_HotheadPresent0 = HOTHEAD_REST + 0;
	private static final int ID_HotheadRest0 = HOTHEAD_REST + 0;
	private static final int ID_HotheadAttack0 = HOTHEAD_ATTACK + 0;

	private static final int HOTHEAD_BOB_X_AMT = 10;

	private static final int HOTHEAD_XOFF = (200 + HOTHEAD_BOB_X_AMT + 6);
	private static final int HOTHEAD_YOFF = 200;

	private static final int HOTHEAD_FINGER_XOFF = 0;
	private static final int HOTHEAD_FINGER_YOFF = 0;

	private static final int Hothead_BEAT_RATE = 24;
	private static final int Hothead_ACTION_RATE_PRE = 2; // !JIM! Was 1

	private static final Panel_Sprite_Func pHotheadPresent = new Panel_Sprite_Func() {
		@Override
		public void invoke(Panel_Sprite psp) {
			if (TEST(psp.PlayerP().Flags, PF_WEAPON_RETRACT))
				return;

			psp.y -= 3 * synctics;

			if (psp.y < HOTHEAD_YOFF) {
				psp.y = HOTHEAD_YOFF;
				psp.yorig = psp.y;
				pSetState(psp, psp.RestState);
			}
		}
	};

	private static final Panel_Sprite_Func pHotheadHide = new Panel_Sprite_Func() {
		@Override
		public void invoke(Panel_Sprite psp) {
			short picnum = psp.picndx;
			Tile pic = engine.getTile(picnum);

			psp.x += 3 * synctics;

			if (psp.x >= HOTHEAD_XOFF + pic.getWidth() || psp.y >= HOTHEAD_YOFF + pic.getHeight()) {
				psp.x = HOTHEAD_XOFF;
				psp.y = HOTHEAD_YOFF + pic.getHeight();

				pWeaponUnHideKeys(psp, psp.PresentState);
			}
		}
	};

	private static final Panel_Sprite_Func pHotheadRetract = new Panel_Sprite_Func() {
		@Override
		public void invoke(Panel_Sprite psp) {
			short picnum = psp.picndx;

			psp.y += 3 * synctics;

			if (psp.y >= HOTHEAD_YOFF + engine.getTile(picnum).getHeight()) {
				psp.PlayerP().Flags &= ~(PF_WEAPON_RETRACT);
				psp.PlayerP().Wpn[WPN_HOTHEAD] = null;
				pKillSprite(psp);
			}
		}
	};

	private static final Panel_Sprite_Func pHotheadRest = new Panel_Sprite_Func() {
		@Override
		public void invoke(Panel_Sprite psp) {
			boolean force = !!TEST(psp.flags, PANF_UNHIDE_SHOOT);

			if (pWeaponHideKeys(psp, ps_HotheadHide[0]))
				return;

			if (HotheadRestStates[psp.PlayerP().WpnFlameType][0] != psp.RestState) {
				psp.RestState = HotheadRestStates[psp.PlayerP().WpnFlameType][0];
				pSetState(psp, HotheadRestStates[psp.PlayerP().WpnFlameType][0]);
			}

			// in rest position - only bob when in rest pos
			pHotheadBobSetup(psp);
			pWeaponBob(psp, PLAYER_MOVING(psp.PlayerP()) != 0);

			if (TEST_SYNC_KEY(psp.PlayerP(), SK_SHOOT) || force) {
				if (FLAG_KEY_PRESSED(psp.PlayerP(), SK_SHOOT) || force) {
					psp.flags &= ~(PANF_UNHIDE_SHOOT);

					// if (TEST(psp.PlayerP().Flags,PF_DIVING))
					// return;

					if (!WeaponOK(psp.PlayerP()))
						return;

					if (psp.PlayerP().WpnAmmo[WPN_HOTHEAD] < 10) {
						psp.PlayerP().WpnFlameType = 0;
						WeaponOK(psp.PlayerP());
					}

					DoPlayerChooseYell(psp.PlayerP());
					// pSetState(psp, psp.ActionState);
					pSetState(psp, HotheadAttackStates[psp.PlayerP().WpnFlameType][0]);
					psp.over[0].xoff = HOTHEAD_FINGER_XOFF - 1;
					psp.over[0].yoff = HOTHEAD_FINGER_YOFF - 10;
				}
			} else
				WeaponOK(psp.PlayerP());
		}
	};

	private static final Panel_State ps_PresentHothead[] = {
			new Panel_State(ID_HotheadPresent0, Hothead_BEAT_RATE, pHotheadPresent).setNext() };

	private static final Panel_State ps_HotheadHide[] = {
			new Panel_State(ID_HotheadRest0, Hothead_BEAT_RATE, pHotheadHide).setNext() };

	private static final Panel_State ps_RetractHothead[] = {
			new Panel_State(ID_HotheadPresent0, Hothead_BEAT_RATE, pHotheadRetract).setNext() };

	private static final Panel_State ps_HotheadRest[] = {
			new Panel_State(ID_HotheadRest0, Hothead_BEAT_RATE, pHotheadRest).setNext() };

	private static final Panel_State ps_HotheadRestRing[] = {
			new Panel_State(ID_HotheadRest0, Hothead_BEAT_RATE, pHotheadRest).setNext(ps_HotheadRest[0]) };

	private static final Panel_State ps_HotheadRestNapalm[] = {
			new Panel_State(ID_HotheadRest0, Hothead_BEAT_RATE, pHotheadRest).setNext(ps_HotheadRest[0]) };

	private static final Panel_Sprite_Func pHotheadAction = new Panel_Sprite_Func() {
		@Override
		public void invoke(Panel_Sprite psp) {

			boolean shooting = TEST_SYNC_KEY(psp.PlayerP(), SK_SHOOT) && FLAG_KEY_PRESSED(psp.PlayerP(), SK_SHOOT);

			if (shooting) {
				pUziBobSetup(psp);
				pWeaponBob(psp, PLAYER_MOVING(psp.PlayerP()) != 0 || shooting);
			}
		}
	};

	private static final Panel_Sprite_Func pHotheadAttack = new Panel_Sprite_Func() {
		@Override
		public void invoke(Panel_Sprite psp) {
			switch (psp.PlayerP().WpnFlameType) {
			case 0:
				SpawnVis(psp.PlayerP().PlayerSprite, -1, -1, -1, -1, 32);
				InitFireball(psp.PlayerP());
				break;
			case 1:
				SpawnVis(psp.PlayerP().PlayerSprite, -1, -1, -1, -1, 20);
				InitSpellRing(psp.PlayerP());
				break;
			case 2:
				SpawnVis(psp.PlayerP().PlayerSprite, -1, -1, -1, -1, 16);
				InitSpellNapalm(psp.PlayerP());
				break;
			}
		}
	};

	private static final Panel_Sprite_Func pHotheadRestTest = new Panel_Sprite_Func() {
		@Override
		public void invoke(Panel_Sprite psp) {
			if (TEST_SYNC_KEY(psp.PlayerP(), SK_SHOOT)) {
				if (FLAG_KEY_PRESSED(psp.PlayerP(), SK_SHOOT)) {
					// if (!TEST(psp.PlayerP().Flags,PF_DIVING))
					{
						if (!WeaponOK(psp.PlayerP()))
							return;

						if (psp.PlayerP().WpnAmmo[WPN_HOTHEAD] < 10) {
							psp.PlayerP().WpnFlameType = 0;
							WeaponOK(psp.PlayerP());
						}

						DoPlayerChooseYell(psp.PlayerP());
					}

					pStatePlusOne(psp);
					return;
				}
			}

			pSetState(psp, HotheadRestStates[psp.PlayerP().WpnFlameType][0]);
			psp.over[0].xoff = HOTHEAD_FINGER_XOFF;
			psp.over[0].yoff = HOTHEAD_FINGER_YOFF;
		}
	};

	private static final Panel_State ps_HotheadAttack_0 = new Panel_State(ID_HotheadAttack0, Hothead_ACTION_RATE_PRE,
			pHotheadAction);
	private static final Panel_State ps_HotheadAttack_1 = new Panel_State(ID_HotheadAttack0, 3, pHotheadAction);
	private static final Panel_State ps_HotheadAttack_2 = new Panel_State(ID_HotheadAttack0, 0, pHotheadAttack);
	private static final Panel_State ps_HotheadAttack_3 = new Panel_State(ID_HotheadAttack0, 3, pHotheadAction);
	private static final Panel_State ps_HotheadAttack_4 = new Panel_State(ID_HotheadAttack0, 0, pHotheadRestTest);
	private static final Panel_State ps_HotheadAttack_5 = new Panel_State(ID_HotheadAttack0, 0, pHotheadAction);

	private static final Panel_State ps_HotheadAttack[] = {
			ps_HotheadAttack_0.setNext(ps_HotheadAttack_1).setFlags(psf_ShadeHalf),
			ps_HotheadAttack_1.setNext(ps_HotheadAttack_2).setFlags(psf_ShadeHalf),
			ps_HotheadAttack_2.setNext(ps_HotheadAttack_3).setFlags(psf_QuickCall),
			ps_HotheadAttack_3.setNext(ps_HotheadAttack_4).setFlags(psf_ShadeHalf),
			ps_HotheadAttack_4.setNext().setPlusOne(ps_HotheadAttack_5).setFlags(psf_QuickCall),
			ps_HotheadAttack_5.setNext(ps_HotheadAttack_0).setFlags(psf_ShadeHalf), };

	private static final Panel_State ps_HotheadRing_0 = new Panel_State(ID_HotheadAttack0, Hothead_ACTION_RATE_PRE,
			pHotheadAction);
	private static final Panel_State ps_HotheadRing_1 = new Panel_State(ID_HotheadAttack0, 10, pHotheadAction);
	private static final Panel_State ps_HotheadRing_2 = new Panel_State(ID_HotheadAttack0, 0, pHotheadAttack);
	private static final Panel_State ps_HotheadRing_3 = new Panel_State(ID_HotheadAttack0, 40, pHotheadAction);
	private static final Panel_State ps_HotheadRing_4 = new Panel_State(ID_HotheadAttack0, 0, pHotheadRestTest);
	private static final Panel_State ps_HotheadRing_5 = new Panel_State(ID_HotheadAttack0, 3, pHotheadAction);

	private static final Panel_State ps_HotheadRing[] = {
			ps_HotheadRing_0.setNext(ps_HotheadRing_1).setFlags(psf_ShadeHalf),
			ps_HotheadRing_1.setNext(ps_HotheadRing_2).setFlags(psf_ShadeHalf),
			ps_HotheadRing_2.setNext(ps_HotheadRing_3).setFlags(psf_QuickCall),
			ps_HotheadRing_3.setNext(ps_HotheadRing_4).setFlags(psf_ShadeHalf),
			ps_HotheadRing_4.setNext().setPlusOne(ps_HotheadRing_5).setFlags(psf_QuickCall),
			ps_HotheadRing_5.setNext(ps_HotheadRing_0).setFlags(psf_ShadeHalf), };

	private static final Panel_State ps_HotheadNapalm_0 = new Panel_State(ID_HotheadAttack0, Hothead_ACTION_RATE_PRE,
			pHotheadAction);
	private static final Panel_State ps_HotheadNapalm_1 = new Panel_State(ID_HotheadAttack0, 3, pHotheadAction);
	private static final Panel_State ps_HotheadNapalm_2 = new Panel_State(ID_HotheadAttack0, 0, pHotheadAttack);
	private static final Panel_State ps_HotheadNapalm_3 = new Panel_State(ID_HotheadAttack0, 50, pHotheadAction);
	private static final Panel_State ps_HotheadNapalm_4 = new Panel_State(ID_HotheadAttack0, 0, pHotheadRestTest);
	private static final Panel_State ps_HotheadNapalm_5 = new Panel_State(ID_HotheadAttack0, 3, pHotheadAction);

	private static final Panel_State ps_HotheadNapalm[] = {
			ps_HotheadNapalm_0.setNext(ps_HotheadNapalm_1).setFlags(psf_ShadeHalf),
			ps_HotheadNapalm_1.setNext(ps_HotheadNapalm_2).setFlags(psf_ShadeHalf),
			ps_HotheadNapalm_2.setNext(ps_HotheadNapalm_3).setFlags(psf_QuickCall),
			ps_HotheadNapalm_3.setNext(ps_HotheadNapalm_4).setFlags(psf_ShadeHalf),
			ps_HotheadNapalm_4.setNext().setPlusOne(ps_HotheadNapalm_5).setFlags(psf_QuickCall),
			ps_HotheadNapalm_5.setNext(ps_HotheadNapalm_0).setFlags(psf_ShadeHalf), };

	private static final Panel_State[] HotheadRestStates[] = { ps_HotheadRest, ps_HotheadRestRing,
			ps_HotheadRestNapalm };

	private static final Panel_State[] HotheadAttackStates[] = { ps_HotheadAttack, ps_HotheadRing, ps_HotheadNapalm };

	public static void pHotHeadOverlays(Panel_Sprite psp, short mode) {
		switch (mode) {
		case 0: // Great balls o' fire
			psp.over[0].pic = HEAD_MODE1;
			break;
		case 1: // Ring of fire
			psp.over[0].pic = HEAD_MODE2;
			break;
		case 2: // I love the smell of napalm in the morning
			psp.over[0].pic = HEAD_MODE3;
			break;
		}
	}

	public static void InitWeaponHothead(PlayerStr pp) {
		Panel_Sprite psp = null;

		if (Prediction)
			return;

		if (!TEST(pp.WpnFlags, BIT(WPN_HOTHEAD)) || TEST(pp.Flags, PF_WEAPON_RETRACT))
			return;

		if (pp.Wpn[WPN_HOTHEAD] == null) {
			psp = pp.Wpn[WPN_HOTHEAD] = pSpawnSprite(pp, ps_PresentHothead[0], PRI_MID, HOTHEAD_XOFF, HOTHEAD_YOFF);
			psp.y += engine.getTile(psp.picndx).getHeight();
		}

		if (pp.CurWpn == pp.Wpn[WPN_HOTHEAD]) {
			return;
		}

		psp.WeaponType = WPN_HOTHEAD;
		PlayerUpdateWeapon(pp, WPN_HOTHEAD);

		pp.WpnUziType = 2; // Make uzi's go away!
		RetractCurWpn(pp);

		// Set up the new Weapon variables
		psp = pp.CurWpn = pp.Wpn[WPN_HOTHEAD];
		psp.flags |= (PANF_WEAPON_SPRITE);
		psp.ActionState = ps_HotheadAttack[0];
		psp.PresentState = ps_PresentHothead[0];
		psp.RestState = HotheadRestStates[psp.PlayerP().WpnFlameType][0];
		psp.RetractState = ps_RetractHothead[0];
		pSetState(psp, psp.PresentState);
		psp.ang = 768;
		psp.vel = 512;

		FLAG_KEY_RELEASE(psp.PlayerP(), SK_SHOOT);
		FLAG_KEY_RESET(psp.PlayerP(), SK_SHOOT);
		pHotHeadOverlays(psp, pp.WpnFlameType);
		psp.over[0].xoff = HOTHEAD_FINGER_XOFF;
		psp.over[0].yoff = HOTHEAD_FINGER_YOFF;

		PlaySound(DIGI_GRDALERT, pp, v3df_follow | v3df_dontpan);
	}

	private static void pHotheadBobSetup(Panel_Sprite psp) {
		if (TEST(psp.flags, PANF_BOB))
			return;

		psp.xorig = psp.x;
		psp.yorig = psp.y;

		psp.sin_amt = HOTHEAD_BOB_X_AMT;
		psp.sin_ndx = 0;
		psp.bob_height_shift = 2;
	}

	public static void HotHeadSaveable() {
		SaveData(ps_PresentHothead);
		SaveData(ps_HotheadHide);
		SaveData(ps_RetractHothead);
		SaveData(ps_HotheadRest);
		SaveData(ps_HotheadRestRing);
		SaveData(ps_HotheadRestNapalm);
		SaveData(ps_HotheadAttack);
		SaveData(ps_HotheadRing);
		SaveData(ps_HotheadNapalm);

		SaveData(pHotheadPresent);
		SaveData(pHotheadHide);
		SaveData(pHotheadRetract);
		SaveData(pHotheadRest);
		SaveData(pHotheadAction);
		SaveData(pHotheadAttack);
		SaveData(pHotheadRestTest);
	}
}
