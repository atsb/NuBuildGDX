package ru.m210projects.Wang.Weapons;

import static ru.m210projects.Build.Net.Mmulti.myconnectindex;
import static ru.m210projects.Wang.Digi.DIGI_ILIKESHURIKEN;
import static ru.m210projects.Wang.Digi.DIGI_PULL;
import static ru.m210projects.Wang.Factory.WangNetwork.Prediction;
import static ru.m210projects.Wang.Game.Player;
import static ru.m210projects.Wang.Gameutils.FLAG_KEY_PRESSED;
import static ru.m210projects.Wang.Gameutils.FLAG_KEY_RELEASE;
import static ru.m210projects.Wang.Gameutils.FLAG_KEY_RESET;
import static ru.m210projects.Wang.Gameutils.PF_WEAPON_RETRACT;
import static ru.m210projects.Wang.Gameutils.PLAYER_MOVING;
import static ru.m210projects.Wang.Gameutils.SK_SHOOT;
import static ru.m210projects.Wang.Gameutils.STD_RANDOM_RANGE;
import static ru.m210projects.Wang.Gameutils.TEST_SYNC_KEY;
import static ru.m210projects.Wang.Gameutils.WPN_STAR;
import static ru.m210projects.Wang.Gameutils.synctics;
import static ru.m210projects.Wang.Main.engine;
import static ru.m210projects.Wang.Main.gs;
import static ru.m210projects.Wang.Panel.DoPlayerChooseYell;
import static ru.m210projects.Wang.Panel.PANF_BOB;
import static ru.m210projects.Wang.Panel.PANF_DEATH_HIDE;
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
import static ru.m210projects.Wang.Panel.psf_Invisible;
import static ru.m210projects.Wang.Panel.psf_QuickCall;
import static ru.m210projects.Wang.Player.DoPlayerSpriteThrow;
import static ru.m210projects.Wang.Sound.PlaySound;
import static ru.m210projects.Wang.Sound.PlayerSound;
import static ru.m210projects.Wang.Sound.v3df_dontpan;
import static ru.m210projects.Wang.Sound.v3df_follow;
import static ru.m210projects.Wang.Type.MyTypes.BIT;
import static ru.m210projects.Wang.Type.MyTypes.TEST;
import static ru.m210projects.Wang.Type.Saveable.SaveData;
import static ru.m210projects.Wang.Weapon.InitStar;

import ru.m210projects.Wang.Type.Panel_Sprite;
import ru.m210projects.Wang.Type.Panel_Sprite_Func;
import ru.m210projects.Wang.Type.Panel_State;
import ru.m210projects.Wang.Type.PlayerStr;

public class Star {

	public static final int STAR_REST = 2130;
	private static final int STAR_THROW = 2133;

	private static final int STAR_YOFF = 208;
	private static final int STAR_XOFF = (160 + 80);

	private static final int PRESENT_STAR_RATE = 5;

	private static final int ID_StarPresent0 = STAR_REST;
	private static final int ID_StarDown0 = STAR_REST + 1;
	private static final int ID_StarDown1 = STAR_REST + 2;

	private static final int ID_ThrowStar0 = STAR_THROW + 0;
	private static final int ID_ThrowStar1 = STAR_THROW + 1;
	private static final int ID_ThrowStar2 = STAR_THROW + 2;
	private static final int ID_ThrowStar3 = STAR_THROW + 3;
	private static final int ID_ThrowStar4 = STAR_THROW + 4;

	private static final Panel_Sprite_Func pStarPresent = new Panel_Sprite_Func() {
		@Override
		public void invoke(Panel_Sprite psp) {
			if (TEST(psp.PlayerP().Flags, PF_WEAPON_RETRACT))
				return;

			psp.y -= 3 * synctics;

			if (psp.y < STAR_YOFF) {
				psp.y = STAR_YOFF;
			}

			if (psp.y <= STAR_YOFF) {
				pSetState(psp, psp.RestState);
			}
		}
	};

	private static final Panel_Sprite_Func pStarHide = new Panel_Sprite_Func() {
		@Override
		public void invoke(Panel_Sprite psp) {
			short picnum = psp.picndx;

			psp.y += 3 * synctics;

			if (psp.y >= STAR_YOFF + engine.getTile(picnum).getHeight()) {
				psp.y = STAR_YOFF + engine.getTile(picnum).getHeight();

				pWeaponUnHideKeys(psp, psp.PresentState);
			}
		}
	};

	private static final Panel_State ps_StarHide[] = {
			new Panel_State(ID_StarPresent0, PRESENT_STAR_RATE, pStarHide).setNext() };

	private static final Panel_State ps_PresentStar[] = {
			new Panel_State(ID_StarPresent0, PRESENT_STAR_RATE, pStarPresent).setNext() };

	private static final int Star_RATE = 2; // was 5

	private static final Panel_Sprite_Func pStarRest = new Panel_Sprite_Func() {
		@Override
		public void invoke(Panel_Sprite psp) {
			boolean force = !!TEST(psp.flags, PANF_UNHIDE_SHOOT);

			if (pWeaponHideKeys(psp, ps_StarHide[0]))
				return;

			pStarBobSetup(psp);
			pWeaponBob(psp, PLAYER_MOVING(psp.PlayerP()) != 0);

			if (TEST_SYNC_KEY(psp.PlayerP(), SK_SHOOT) || force) {
				if (FLAG_KEY_PRESSED(psp.PlayerP(), SK_SHOOT) || force) {
					psp.flags &= ~(PANF_UNHIDE_SHOOT);

					if (!WeaponOK(psp.PlayerP()))
						return;

					DoPlayerChooseYell(psp.PlayerP());

					pSetState(psp, psp.ActionState);
					DoPlayerSpriteThrow(psp.PlayerP());
				}
			} else
				WeaponOK(psp.PlayerP());
		}
	};

	private static void pStarBobSetup(Panel_Sprite psp) {
		if (TEST(psp.flags, PANF_BOB))
			return;

		psp.xorig = psp.x;
		psp.yorig = psp.y;

		psp.sin_amt = 10;
		psp.sin_ndx = 0;
		psp.bob_height_shift = 3;
	}

	private static final Panel_Sprite_Func pStarRetract = new Panel_Sprite_Func() {
		@Override
		public void invoke(Panel_Sprite psp) {
			short picnum = psp.picndx;

			psp.y += 3 * synctics;

			if (psp.y >= STAR_YOFF + engine.getTile(picnum).getHeight()) {
				psp.PlayerP().Flags &= ~(PF_WEAPON_RETRACT);

				// kill only in its own routine
				psp.PlayerP().Wpn[WPN_STAR] = null;
				pKillSprite(psp);
			}
		}
	};

	private static final Panel_State ps_RetractStar[] = {
			new Panel_State(ID_StarPresent0, PRESENT_STAR_RATE, pStarRetract).setNext() };

	private static final Panel_Sprite_Func pStarRestTest = new Panel_Sprite_Func() {
		@Override
		public void invoke(Panel_Sprite psp) {
			if (TEST_SYNC_KEY(psp.PlayerP(), SK_SHOOT)) {
				if (FLAG_KEY_PRESSED(psp.PlayerP(), SK_SHOOT)) {
					if (!WeaponOK(psp.PlayerP()))
						return;

					// continue to next state to swing right
					DoPlayerChooseYell(psp.PlayerP());
					pStatePlusOne(psp);
					return;
				}
			}

			pSetState(psp, psp.RestState);
		}
	};

	private static final Panel_Sprite_Func pStarThrow = new Panel_Sprite_Func() {
		@Override
		public void invoke(Panel_Sprite psp) {
			InitStar(psp.PlayerP());
		}
	};

	private static final Panel_State ps_ThrowStar_0 = new Panel_State(ID_StarDown0, Star_RATE + 3, null);
	private static final Panel_State ps_ThrowStar_1 = new Panel_State(ID_StarDown1, Star_RATE + 3, null);
	private static final Panel_State ps_ThrowStar_2 = new Panel_State(ID_StarDown1, Star_RATE * 2, null);
	private static final Panel_State ps_ThrowStar_3 = new Panel_State(ID_StarDown1, Star_RATE, null);
	private static final Panel_State ps_ThrowStar_4 = new Panel_State(ID_StarDown0, Star_RATE, null);
	private static final Panel_State ps_ThrowStar_5 = new Panel_State(ID_ThrowStar0, 1, null);
	private static final Panel_State ps_ThrowStar_6 = new Panel_State(ID_ThrowStar0, Star_RATE, pStarThrow);
	private static final Panel_State ps_ThrowStar_7 = new Panel_State(ID_ThrowStar0, Star_RATE, null);
	private static final Panel_State ps_ThrowStar_8 = new Panel_State(ID_ThrowStar1, Star_RATE, null);
	private static final Panel_State ps_ThrowStar_9 = new Panel_State(ID_ThrowStar2, Star_RATE * 2, null);
	private static final Panel_State ps_ThrowStar_10 = new Panel_State(ID_ThrowStar3, Star_RATE * 2, null);
	private static final Panel_State ps_ThrowStar_11 = new Panel_State(ID_ThrowStar4, Star_RATE * 2, null);
	// start up
	private static final Panel_State ps_ThrowStar_12 = new Panel_State(ID_StarDown1, Star_RATE + 3, null);
	private static final Panel_State ps_ThrowStar_13 = new Panel_State(ID_StarDown0, Star_RATE + 3, null);
	private static final Panel_State ps_ThrowStar_14 = new Panel_State(ID_StarPresent0, Star_RATE + 3, null);
	// maybe to directly to rest state
	private static final Panel_State ps_ThrowStar_15 = new Panel_State(ID_StarDown0, 3, pStarRestTest);
	// if holding the fire key we get to here
	private static final Panel_State ps_ThrowStar_16 = new Panel_State(ID_ThrowStar4, 3, null);

	private static final Panel_State ps_ThrowStar[] = { ps_ThrowStar_0.setNext(ps_ThrowStar_1),
			ps_ThrowStar_1.setNext(ps_ThrowStar_2), ps_ThrowStar_2.setNext(ps_ThrowStar_3).setFlags(psf_Invisible),
			ps_ThrowStar_3.setNext(ps_ThrowStar_4), ps_ThrowStar_4.setNext(ps_ThrowStar_5),
			ps_ThrowStar_5.setNext(ps_ThrowStar_6), ps_ThrowStar_6.setNext(ps_ThrowStar_7).setFlags(psf_QuickCall),
			ps_ThrowStar_7.setNext(ps_ThrowStar_8), ps_ThrowStar_8.setNext(ps_ThrowStar_9),
			ps_ThrowStar_9.setNext(ps_ThrowStar_10), ps_ThrowStar_10.setNext(ps_ThrowStar_11),
			ps_ThrowStar_11.setNext(ps_ThrowStar_12), ps_ThrowStar_12.setNext(ps_ThrowStar_13),
			ps_ThrowStar_13.setNext(ps_ThrowStar_14), ps_ThrowStar_14.setNext(ps_ThrowStar_15),
			ps_ThrowStar_15.setNext(ps_ThrowStar_16).setFlags(psf_QuickCall),
			ps_ThrowStar_16.setNext(ps_ThrowStar_5), };

	private static final Panel_State ps_StarRest[] = {
			new Panel_State(ID_StarPresent0, Star_RATE, pStarRest).setNext().setPlusOne(ps_ThrowStar[0]) };

	public static void InitWeaponStar(PlayerStr pp) {
		Panel_Sprite psp;

		if (Prediction)
			return;

		if (!TEST(pp.WpnFlags, BIT(WPN_STAR)) || pp.WpnAmmo[WPN_STAR] < 3 || TEST(pp.Flags, PF_WEAPON_RETRACT)) {
			return;
		}

		// needed for death sequence when the STAR was your weapon when you died
		if (pp.Wpn[WPN_STAR] != null && TEST(pp.Wpn[WPN_STAR].flags, PANF_DEATH_HIDE)) {
			pp.Wpn[WPN_STAR].flags &= ~(PANF_DEATH_HIDE);
			pp.Flags &= ~(PF_WEAPON_RETRACT);
			pSetState(pp.CurWpn, pp.CurWpn.PresentState);
			return;
		}

		if (pp.Wpn[WPN_STAR] == null) {
			psp = pp.Wpn[WPN_STAR] = pSpawnSprite(pp, ps_PresentStar[0], PRI_MID, STAR_XOFF, STAR_YOFF);
			psp.y += engine.getTile(psp.picndx).getHeight();
		}

		if (pp.CurWpn == pp.Wpn[WPN_STAR]) {
			return;
		}

		PlayerUpdateWeapon(pp, WPN_STAR);

		pp.WpnUziType = 2; // Make uzi's go away!
		RetractCurWpn(pp);

		// Set up the new Weapon variables
		psp = pp.CurWpn = pp.Wpn[WPN_STAR];
		psp.flags |= (PANF_WEAPON_SPRITE);
		psp.ActionState = ps_ThrowStar[0];
		psp.RetractState = ps_RetractStar[0];
		psp.PresentState = ps_PresentStar[0];
		psp.RestState = ps_StarRest[0];
		pSetState(psp, psp.PresentState);

		PlaySound(DIGI_PULL, pp, v3df_follow | v3df_dontpan);
		if (STD_RANDOM_RANGE(1000) > 900 && pp == Player[myconnectindex]) {
			if(!gs.UseDarts)
				PlayerSound(DIGI_ILIKESHURIKEN, v3df_follow | v3df_dontpan, pp);
		}

		FLAG_KEY_RELEASE(psp.PlayerP(), SK_SHOOT);
		FLAG_KEY_RESET(psp.PlayerP(), SK_SHOOT);
	}

	public static void StarSaveable() {
		SaveData(ps_PresentStar);
		SaveData(ps_StarHide);
		SaveData(ps_StarRest);
		SaveData(ps_ThrowStar);
		SaveData(ps_RetractStar);

		SaveData(pStarPresent);
		SaveData(pStarHide);
		SaveData(pStarRest);
		SaveData(pStarRetract);
		SaveData(pStarRestTest);
		SaveData(pStarThrow);
	}
}
