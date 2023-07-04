package ru.m210projects.Wang.Weapons;

import static ru.m210projects.Wang.Digi.DIGI_MINE_UP;
import static ru.m210projects.Wang.Digi.DIGI_PULL;
import static ru.m210projects.Wang.Factory.WangNetwork.Prediction;
import static ru.m210projects.Wang.Gameutils.FLAG_KEY_PRESSED;
import static ru.m210projects.Wang.Gameutils.FLAG_KEY_RELEASE;
import static ru.m210projects.Wang.Gameutils.FLAG_KEY_RESET;
import static ru.m210projects.Wang.Gameutils.PF_WEAPON_RETRACT;
import static ru.m210projects.Wang.Gameutils.PLAYER_MOVING;
import static ru.m210projects.Wang.Gameutils.SK_SHOOT;
import static ru.m210projects.Wang.Gameutils.TEST_SYNC_KEY;
import static ru.m210projects.Wang.Gameutils.WPN_MINE;
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
import static ru.m210projects.Wang.Panel.pKillSprite;
import static ru.m210projects.Wang.Panel.pSetState;
import static ru.m210projects.Wang.Panel.pSpawnSprite;
import static ru.m210projects.Wang.Panel.pStatePlusOne;
import static ru.m210projects.Wang.Panel.pWeaponBob;
import static ru.m210projects.Wang.Panel.pWeaponHideKeys;
import static ru.m210projects.Wang.Panel.pWeaponUnHideKeys;
import static ru.m210projects.Wang.Panel.psf_QuickCall;
import static ru.m210projects.Wang.Sound.PlaySound;
import static ru.m210projects.Wang.Sound.v3df_dontpan;
import static ru.m210projects.Wang.Sound.v3df_follow;
import static ru.m210projects.Wang.Text.PutStringInfo;
import static ru.m210projects.Wang.Type.MyTypes.BIT;
import static ru.m210projects.Wang.Type.MyTypes.TEST;
import static ru.m210projects.Wang.Type.Saveable.SaveData;
import static ru.m210projects.Wang.Weapon.InitMine;

import ru.m210projects.Wang.Type.Panel_Sprite;
import ru.m210projects.Wang.Type.Panel_Sprite_Func;
import ru.m210projects.Wang.Type.Panel_State;
import ru.m210projects.Wang.Type.PlayerStr;

public class Mine {

	private static final int MINE_YOFF = 200;
	private static final int MINE_XOFF = (160 + 50);

	private static final int Mine_REST_RATE = 24;
	private static final int Mine_ACTION_RATE = 6;

	private static final int MINE_REST = 2220;
	private static final int MINE_THROW = 2222;

	private static final int ID_MinePresent0 = MINE_REST + 0;
	private static final int ID_MinePresent1 = MINE_REST + 1;

	private static final int ID_MineThrow0 = MINE_THROW + 0;

	private static final Panel_Sprite_Func pMinePresent = new Panel_Sprite_Func() {
		@Override
		public void invoke(Panel_Sprite psp) {
			if (TEST(psp.PlayerP().Flags, PF_WEAPON_RETRACT))
				return;

			psp.y -= 3 * synctics;

			if (psp.y < MINE_YOFF) {
				psp.y = MINE_YOFF;
				psp.rotate_ang = 0;
				psp.yorig = psp.y;
				pSetState(psp, psp.RestState);
			}
		}
	};

	private static final Panel_State ps_PresentMine[] = {
			new Panel_State(ID_MinePresent0, Mine_REST_RATE, pMinePresent).setNext() };

	private static final Panel_Sprite_Func pMineRetract = new Panel_Sprite_Func() {
		@Override
		public void invoke(Panel_Sprite psp) {
			psp.y += 3 * synctics;

			if (psp.y >= MINE_YOFF + engine.getTile(psp.picndx).getHeight()) {
				psp.PlayerP().Flags &= ~(PF_WEAPON_RETRACT);
				psp.PlayerP().Wpn[WPN_MINE] = null;
				pKillSprite(psp);
			}
		}
	};

	private static final Panel_State ps_RetractMine[] = {
			new Panel_State(ID_MinePresent0, Mine_REST_RATE, pMineRetract).setNext() };

	private static final Panel_Sprite_Func pMineHide = new Panel_Sprite_Func() {
		@Override
		public void invoke(Panel_Sprite psp) {
			short picnum = psp.picndx;

			psp.y += 3 * synctics;

			if (psp.y >= MINE_YOFF + engine.getTile(picnum).getHeight()) {
				psp.y = MINE_YOFF + engine.getTile(picnum).getHeight();
				psp.x = MINE_XOFF;

				pWeaponUnHideKeys(psp, psp.PresentState);
			}
		}
	};

	private static final Panel_State ps_MineHide[] = {
			new Panel_State(ID_MinePresent0, Mine_REST_RATE, pMineHide).setNext() };

	private static final Panel_Sprite_Func pMineRest = new Panel_Sprite_Func() {
		@Override
		public void invoke(Panel_Sprite psp) {
			boolean force = !!TEST(psp.flags, PANF_UNHIDE_SHOOT);

			if (pWeaponHideKeys(psp, ps_MineHide[0]))
				return;

			pMineBobSetup(psp);
			pWeaponBob(psp, PLAYER_MOVING(psp.PlayerP()) != 0);

			if (TEST_SYNC_KEY(psp.PlayerP(), SK_SHOOT) || force) {
				if (FLAG_KEY_PRESSED(psp.PlayerP(), SK_SHOOT) || force) {
					psp.flags &= ~(PANF_UNHIDE_SHOOT);

					DoPlayerChooseYell(psp.PlayerP());
					pSetState(psp, psp.ActionState);
				}
			} else
				WeaponOK(psp.PlayerP());
		}
	};

	private static final Panel_Sprite_Func pMineUpSound = new Panel_Sprite_Func() {
		@Override
		public void invoke(Panel_Sprite psp) {
			PlayerStr pp = psp.PlayerP();

			PlaySound(DIGI_MINE_UP, pp, v3df_follow);
		}
	};

	private static final Panel_State ps_MineRest_0 = new Panel_State(ID_MinePresent0, 36, pMineRest);
	private static final Panel_State ps_MineRest_1 = new Panel_State(ID_MinePresent0, 0, pMineUpSound);
	private static final Panel_State ps_MineRest_2 = new Panel_State(ID_MinePresent1, Mine_REST_RATE, pMineRest);

	private static final Panel_State ps_MineRest[] = { ps_MineRest_0.setNext(ps_MineRest_1),
			ps_MineRest_1.setNext(ps_MineRest_2).setFlags(psf_QuickCall), ps_MineRest_2.setNext(), };

	private static final Panel_Sprite_Func pMineThrow = new Panel_Sprite_Func() {
		@Override
		public void invoke(Panel_Sprite psp) {
			InitMine(psp.PlayerP());
		}
	};

	private static final Panel_Sprite_Func pMineLower = new Panel_Sprite_Func() {
		@Override
		public void invoke(Panel_Sprite psp) {
			psp.y += 4 * synctics;

			if (psp.y > MINE_YOFF + engine.getTile(psp.picndx).getHeight()) {
				if (!WeaponOK(psp.PlayerP()))
					return;
				psp.y = MINE_YOFF + engine.getTile(psp.picndx).getHeight();
				pStatePlusOne(psp);
			}
		}
	};

	private static final Panel_Sprite_Func pMineRaise = new Panel_Sprite_Func() {
		@Override
		public void invoke(Panel_Sprite psp) {
			psp.y -= 4 * synctics;

			if (psp.y < MINE_YOFF) {
				psp.y = MINE_YOFF;
				pStatePlusOne(psp);
			}
		}
	};

	private static final Panel_Sprite_Func pMineAction = new Panel_Sprite_Func() {
		@Override
		public void invoke(Panel_Sprite psp) {
			pMineBobSetup(psp);
			pWeaponBob(psp, PLAYER_MOVING(psp.PlayerP()) != 0);
		}
	};

	private static final Panel_State ps_MineThrow_0 = new Panel_State(ID_MineThrow0, 3, null);
	private static final Panel_State ps_MineThrow_1 = new Panel_State(ID_MineThrow0, Mine_ACTION_RATE, pMineThrow);
	private static final Panel_State ps_MineThrow_2 = new Panel_State(ID_MineThrow0, Mine_ACTION_RATE, pMineLower);
	private static final Panel_State ps_MineThrow_3 = new Panel_State(ID_MineThrow0, Mine_ACTION_RATE * 5, null);
	private static final Panel_State ps_MineThrow_4 = new Panel_State(ID_MinePresent0, Mine_ACTION_RATE, pMineRaise);
	private static final Panel_State ps_MineThrow_5 = new Panel_State(ID_MinePresent0, Mine_ACTION_RATE, null);
	private static final Panel_State ps_MineThrow_6 = new Panel_State(ID_MinePresent0, 3, pMineAction);

	private static final Panel_State ps_MineThrow[] = { ps_MineThrow_0.setNext(ps_MineThrow_1),
			ps_MineThrow_1.setNext(ps_MineThrow_2).setFlags(psf_QuickCall),
			ps_MineThrow_2.setNext().setPlusOne(ps_MineThrow_3), ps_MineThrow_3.setNext(ps_MineThrow_4),
			ps_MineThrow_4.setNext().setPlusOne(ps_MineThrow_5), ps_MineThrow_5.setNext(ps_MineThrow_6),
			ps_MineThrow_6.setNext(ps_MineRest[0]), };

	public static void InitWeaponMine(PlayerStr pp) {
		Panel_Sprite psp;

		if (Prediction)
			return;

		if (pp.WpnAmmo[WPN_MINE] <= 0)
			PutStringInfo(pp, "Out of Sticky Bombs!");

		if (!TEST(pp.WpnFlags, BIT(WPN_MINE)) || pp.WpnAmmo[WPN_MINE] <= 0 || TEST(pp.Flags, PF_WEAPON_RETRACT))
			return;

		if (pp.Wpn[WPN_MINE] == null) {
			psp = pp.Wpn[WPN_MINE] = pSpawnSprite(pp, ps_PresentMine[0], PRI_MID, MINE_XOFF, MINE_YOFF);
			psp.y += engine.getTile(psp.picndx).getHeight();
		}

		if (pp.CurWpn == pp.Wpn[WPN_MINE]) {
			return;
		}

		PlayerUpdateWeapon(pp, WPN_MINE);

		pp.WpnUziType = 2; // Make uzi's go away!
		RetractCurWpn(pp);

		// Set up the new Weapon variables
		psp = pp.CurWpn = pp.Wpn[WPN_MINE];
		psp.flags |= (PANF_WEAPON_SPRITE);
		psp.ActionState = ps_MineThrow[0];
		psp.RetractState = ps_RetractMine[0];
		psp.PresentState = ps_PresentMine[0];
		psp.RestState = ps_MineRest[0];
		pSetState(psp, psp.PresentState);

		PlaySound(DIGI_PULL, pp, v3df_follow | v3df_dontpan);

		FLAG_KEY_RELEASE(psp.PlayerP(), SK_SHOOT);
		FLAG_KEY_RESET(psp.PlayerP(), SK_SHOOT);
	}

	private static void pMineBobSetup(Panel_Sprite psp) {
		if (TEST(psp.flags, PANF_BOB))
			return;

		psp.xorig = psp.x;
		psp.yorig = psp.y;

		psp.sin_amt = 12;
		psp.sin_ndx = 0;
		psp.bob_height_shift = 3;
	}

	public static void MineSaveable() {
		SaveData(ps_PresentMine);
		SaveData(ps_MineRest);
		SaveData(ps_MineHide);
		SaveData(ps_MineThrow);
		SaveData(ps_RetractMine);

		SaveData(pMinePresent);
		SaveData(pMineRetract);
		SaveData(pMineHide);
		SaveData(pMineRest);
		SaveData(pMineUpSound);
		SaveData(pMineThrow);
		SaveData(pMineLower);
		SaveData(pMineRaise);
		SaveData(pMineAction);
	}
}
