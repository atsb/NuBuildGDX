package ru.m210projects.Wang.Weapons;

import static ru.m210projects.Build.Engine.sintable;
import static ru.m210projects.Build.Net.Mmulti.myconnectindex;
import static ru.m210projects.Wang.Digi.DIGI_NUKECDOWN;
import static ru.m210projects.Wang.Digi.DIGI_NUKEREADY;
import static ru.m210projects.Wang.Digi.DIGI_NUKESTDBY;
import static ru.m210projects.Wang.Digi.DIGI_ROCKET_UP;
import static ru.m210projects.Wang.Digi.DIGI_TAUNTAI2;
import static ru.m210projects.Wang.Digi.DIGI_TAUNTAI4;
import static ru.m210projects.Wang.Digi.DIGI_WARNING;
import static ru.m210projects.Wang.Factory.WangNetwork.Prediction;
import static ru.m210projects.Wang.Game.Player;
import static ru.m210projects.Wang.Gameutils.FLAG_KEY_PRESSED;
import static ru.m210projects.Wang.Gameutils.FLAG_KEY_RELEASE;
import static ru.m210projects.Wang.Gameutils.FLAG_KEY_RESET;
import static ru.m210projects.Wang.Gameutils.NORM_ANGLE;
import static ru.m210projects.Wang.Gameutils.PF_WEAPON_RETRACT;
import static ru.m210projects.Wang.Gameutils.PLAYER_MOVING;
import static ru.m210projects.Wang.Gameutils.SK_SHOOT;
import static ru.m210projects.Wang.Gameutils.STD_RANDOM_RANGE;
import static ru.m210projects.Wang.Gameutils.TEST_SYNC_KEY;
import static ru.m210projects.Wang.Gameutils.WPN_MICRO;
import static ru.m210projects.Wang.Gameutils.synctics;
import static ru.m210projects.Wang.Main.engine;
import static ru.m210projects.Wang.Panel.DoPlayerChooseYell;
import static ru.m210projects.Wang.Panel.MICRO_HEAT;
import static ru.m210projects.Wang.Panel.MICRO_HEAT_NUM;
import static ru.m210projects.Wang.Panel.MICRO_SHOT_1;
import static ru.m210projects.Wang.Panel.MICRO_SHOT_20;
import static ru.m210projects.Wang.Panel.MICRO_SHOT_NUM;
import static ru.m210projects.Wang.Panel.MICRO_SIGHT;
import static ru.m210projects.Wang.Panel.MICRO_SIGHT_NUM;
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
import static ru.m210projects.Wang.Sound.PlayerSound;
import static ru.m210projects.Wang.Sound.v3df_dontpan;
import static ru.m210projects.Wang.Sound.v3df_follow;
import static ru.m210projects.Wang.Type.MyTypes.BIT;
import static ru.m210projects.Wang.Type.MyTypes.TEST;
import static ru.m210projects.Wang.Type.Saveable.SaveData;
import static ru.m210projects.Wang.Vis.SpawnVis;
import static ru.m210projects.Wang.Weapon.InitBunnyRocket;
import static ru.m210projects.Wang.Weapon.InitNuke;
import static ru.m210projects.Wang.Weapon.InitRocket;

import ru.m210projects.Wang.Type.Panel_Sprite;
import ru.m210projects.Wang.Type.Panel_Sprite_Func;
import ru.m210projects.Wang.Type.Panel_State;
import ru.m210projects.Wang.Type.PlayerStr;

public class Micro {

	private static final int MICRO_BOB_X_AMT = 10;
	private static final int MICRO_YOFF = 205;
	private static final int MICRO_XOFF = (150 + MICRO_BOB_X_AMT);

	private static final int Micro_REST_RATE = 24;
	private static final int Micro_ACTION_RATE = 6; // !JIM! was 9

	private static final int MICRO_REST = 2070;
	private static final int MICRO_FIRE = 2071;
	private static final int MICRO_SINGLE_FIRE = 2071;

	private static final int ID_MicroPresent0 = MICRO_REST + 0;

	private static final int ID_MicroFire0 = MICRO_FIRE + 0;
	private static final int ID_MicroFire1 = MICRO_FIRE + 1;
	private static final int ID_MicroFire2 = MICRO_FIRE + 2;
	private static final int ID_MicroFire3 = MICRO_FIRE + 3;

	private static final int ID_MicroSingleFire0 = MICRO_SINGLE_FIRE + 0;
	private static final int ID_MicroSingleFire1 = MICRO_SINGLE_FIRE + 1;
	private static final int ID_MicroSingleFire2 = MICRO_SINGLE_FIRE + 2;
	private static final int ID_MicroSingleFire3 = MICRO_SINGLE_FIRE + 3;

	private static final int MICRO_SIGHT_XOFF = 29;
	private static final int MICRO_SIGHT_YOFF = -58;

	private static final int MICRO_SHOT_XOFF = 65;
	private static final int MICRO_SHOT_YOFF = -41;

	private static final int MICRO_HEAT_XOFF = 78;
	private static final int MICRO_HEAT_YOFF = -51;

	private static final int Micro_SINGLE_RATE = 8;
	private static final int Micro_DISSIPATE_RATE = 6;

	private static final Panel_Sprite_Func pMicroAction = new Panel_Sprite_Func() {
		@Override
		public void invoke(Panel_Sprite psp) {
			pMicroBobSetup(psp);
			pWeaponBob(psp, PLAYER_MOVING(psp.PlayerP()) != 0);
		}
	};

	public static final Panel_State ps_MicroHeatFlash[] = { new Panel_State(MICRO_HEAT, 30, null),
			new Panel_State(0, 30, null), new Panel_State(MICRO_HEAT, 30, null), new Panel_State(0, 30, null),
			new Panel_State(MICRO_HEAT, 30, null), new Panel_State(0, 30, null), new Panel_State(MICRO_HEAT, 30, null),
			new Panel_State(0, 30, null), new Panel_State(MICRO_HEAT, 30, null),
			new Panel_State(0, 0, null).setNext(), };

	public static final Panel_State ps_MicroNukeFlash[] = { new Panel_State(MICRO_SHOT_20, 30, null),
			new Panel_State(0, 30, null), new Panel_State(MICRO_SHOT_20, 30, null), new Panel_State(0, 30, null),
			new Panel_State(MICRO_SHOT_20, 30, null), new Panel_State(0, 30, null),
			new Panel_State(MICRO_SHOT_20, 30, null), new Panel_State(0, 30, null),
			new Panel_State(MICRO_SHOT_20, 30, null), new Panel_State(0, 0, null).setNext(), };

	private static final Panel_Sprite_Func pMicroFire = new Panel_Sprite_Func() {
		@Override
		public void invoke(Panel_Sprite psp) {
			SpawnVis(psp.PlayerP().PlayerSprite, -1, -1, -1, -1, 20);
			switch (psp.PlayerP().WpnRocketType) {
			case 0:
				if (psp.PlayerP().BunnyMode)
					InitBunnyRocket(psp.PlayerP());
				else
					InitRocket(psp.PlayerP());
				break;
			case 1:
				if (psp.PlayerP().BunnyMode)
					InitBunnyRocket(psp.PlayerP());
				else
					InitRocket(psp.PlayerP());
				break;
			case 2:
				PlaySound(DIGI_WARNING, psp.PlayerP(), v3df_dontpan | v3df_follow);
				InitNuke(psp.PlayerP());
				psp.PlayerP().NukeInitialized = false;
				break;
			}
		}
	};

	private static final Panel_Sprite_Func pMicroRest = new Panel_Sprite_Func() {
		@Override
		public void invoke(Panel_Sprite psp) {
			PlayerStr pp = psp.PlayerP();
			boolean force = !!TEST(psp.flags, PANF_UNHIDE_SHOOT);

			if (pWeaponHideKeys(psp, ps_MicroHide[0]))
				return;

			pMicroOverlays(psp);

			pMicroBobSetup(psp);
			pWeaponBob(psp, PLAYER_MOVING(psp.PlayerP()) != 0);

			if (pp.InitingNuke) {
				int choose_voc = 0;

				pp.InitingNuke = false;
				pp.NukeInitialized = true;

				if (pp == Player[myconnectindex]) {
					choose_voc = STD_RANDOM_RANGE(1024);
					if (choose_voc > 600)
						PlayerSound(DIGI_TAUNTAI2, v3df_dontpan | v3df_follow, psp.PlayerP());
					else if (choose_voc > 300)
						PlayerSound(DIGI_TAUNTAI4, v3df_dontpan | v3df_follow, psp.PlayerP());
				}
			}

			if (TEST_SYNC_KEY(psp.PlayerP(), SK_SHOOT) || force) {
				if (FLAG_KEY_PRESSED(psp.PlayerP(), SK_SHOOT) || force) {
					psp.flags &= ~(PANF_UNHIDE_SHOOT);

					if (!WeaponOK(psp.PlayerP()))
						return;

					if (psp.PlayerP().WpnAmmo[WPN_MICRO] <= 0 && psp.PlayerP().WpnRocketType != 2) {
						psp.PlayerP().WpnRocketNuke = 0;
						WeaponOK(psp.PlayerP());
						psp.PlayerP().WpnRocketNuke = 1;
						return;
					}

					switch (psp.PlayerP().WpnRocketType) {
					case 0:
					case 1:
						pSetState(psp, ps_MicroSingleFire[0]);
						DoPlayerChooseYell(psp.PlayerP());
						break;
					case 2:
						if (psp.PlayerP().WpnRocketNuke > 0)
							pSetState(psp, ps_MicroSingleFire[0]);
						break;
					}
				}
			} else
				WeaponOK(psp.PlayerP());
		}
	};

	private static final Panel_Sprite_Func pMicroRecoilDown = new Panel_Sprite_Func() {
		@Override
		public void invoke(Panel_Sprite psp) {
			int x = castLongX(psp);
			int y = castLongY(psp);

			x += psp.vel * synctics * sintable[NORM_ANGLE(psp.ang + 512)] >> 6;
			y += psp.vel * synctics * -sintable[psp.ang] >> 6;

			setLong(psp, x, y);

			psp.vel -= 24 * synctics;

			if (psp.vel < 550) {
				psp.vel = 550;
				psp.ang = NORM_ANGLE(psp.ang + 1024);

				pStatePlusOne(psp);
			}
		}
	};

	private static final Panel_Sprite_Func pMicroRecoilUp = new Panel_Sprite_Func() {
		@Override
		public void invoke(Panel_Sprite psp) {
			int x = castLongX(psp);
			int y = castLongY(psp);

			x += psp.vel * synctics * sintable[NORM_ANGLE(psp.ang + 512)] >> 6;
			y += psp.vel * synctics * -sintable[psp.ang] >> 6;

			setLong(psp, x, y);
			psp.vel += 15 * synctics;

			if (psp.y < MICRO_YOFF) {
				psp.y = MICRO_YOFF;
				psp.x = MICRO_XOFF;

				pMicroSetRecoil(psp);

				pStatePlusOne(psp);
				psp.flags &= ~(PANF_BOB);
			}
		}
	};

	private static final Panel_State ps_MicroRest[] = {
			new Panel_State(ID_MicroPresent0, Micro_REST_RATE, pMicroRest).setNext() };

	// recoil
	private static final Panel_State ps_MicroRecoil_0 = new Panel_State(ID_MicroPresent0, Micro_ACTION_RATE,
			pMicroRecoilDown);
	private static final Panel_State ps_MicroRecoil_1 = new Panel_State(ID_MicroPresent0, Micro_ACTION_RATE,
			pMicroRecoilUp);
	// Firing delay.
	private static final Panel_State ps_MicroRecoil_2 = new Panel_State(ID_MicroPresent0, 30, null);
	// ready to fire again
	private static final Panel_State ps_MicroRecoil_3 = new Panel_State(ID_MicroPresent0, 3, null);

	private static final Panel_State ps_MicroRecoil[] = { ps_MicroRecoil_0.setNext().setPlusOne(ps_MicroRecoil_1),
			ps_MicroRecoil_1.setNext().setPlusOne(ps_MicroRecoil_2), ps_MicroRecoil_2.setNext(ps_MicroRecoil_3),
			ps_MicroRecoil_3.setNext(ps_MicroRest[0]) };

	private static final Panel_State ps_MicroSingleFire_0 = new Panel_State(ID_MicroSingleFire0, Micro_SINGLE_RATE,
			pMicroAction);
	private static final Panel_State ps_MicroSingleFire_1 = new Panel_State(ID_MicroSingleFire1, Micro_SINGLE_RATE,
			pMicroAction);
	private static final Panel_State ps_MicroSingleFire_2 = new Panel_State(ID_MicroSingleFire1, 0, pMicroFire);
	private static final Panel_State ps_MicroSingleFire_3 = new Panel_State(ID_MicroSingleFire2, Micro_DISSIPATE_RATE,
			pMicroAction);
	private static final Panel_State ps_MicroSingleFire_4 = new Panel_State(ID_MicroSingleFire3, Micro_DISSIPATE_RATE,
			pMicroAction);
	private static final Panel_State ps_MicroSingleFire_5 = new Panel_State(ID_MicroPresent0, 3, pMicroAction);

	private static final Panel_State ps_MicroSingleFire[] = {
			ps_MicroSingleFire_0.setNext(ps_MicroSingleFire_1).setFlags(psf_ShadeHalf),
			ps_MicroSingleFire_1.setNext(ps_MicroSingleFire_2).setFlags(psf_ShadeNone),
			ps_MicroSingleFire_2.setNext(ps_MicroSingleFire_3).setFlags(psf_ShadeNone | psf_QuickCall),
			ps_MicroSingleFire_3.setNext(ps_MicroSingleFire_4).setFlags(psf_ShadeNone),
			ps_MicroSingleFire_4.setNext(ps_MicroSingleFire_5).setFlags(psf_ShadeHalf),
			ps_MicroSingleFire_5.setNext(ps_MicroRecoil[0]), };

	private static final Panel_State ps_MicroFire_0 = new Panel_State(ID_MicroFire0, Micro_ACTION_RATE, pMicroAction);
	private static final Panel_State ps_MicroFire_1 = new Panel_State(ID_MicroFire1, Micro_ACTION_RATE, pMicroAction);
	private static final Panel_State ps_MicroFire_2 = new Panel_State(ID_MicroFire2, Micro_ACTION_RATE, pMicroAction);
	private static final Panel_State ps_MicroFire_3 = new Panel_State(ID_MicroFire3, Micro_ACTION_RATE, pMicroAction);
	private static final Panel_State ps_MicroFire_4 = new Panel_State(ID_MicroPresent0, 0, pMicroFire);
	// !JIM! After firing delay so rockets can't fire so fast!
	// Putting a BIG blast radius for rockets, this is better than small and fast
	// for this weap.
	private static final Panel_State ps_MicroFire_5 = new Panel_State(ID_MicroPresent0, 120, pMicroAction);
	private static final Panel_State ps_MicroFire_6 = new Panel_State(ID_MicroPresent0, 3, pMicroAction);

	private static final Panel_State ps_MicroFire[] = { ps_MicroFire_0.setNext(ps_MicroFire_1).setFlags(psf_ShadeNone),
			ps_MicroFire_1.setNext(ps_MicroFire_2).setFlags(psf_ShadeNone),
			ps_MicroFire_2.setNext(ps_MicroFire_3).setFlags(psf_ShadeHalf),
			ps_MicroFire_3.setNext(ps_MicroFire_4).setFlags(psf_ShadeHalf),
			ps_MicroFire_4.setNext(ps_MicroFire_5).setFlags(psf_ShadeNone | psf_QuickCall),
			ps_MicroFire_5.setNext(ps_MicroFire_6), ps_MicroFire_6.setNext(ps_MicroRecoil[0]), };

	private static final Panel_Sprite_Func pNukeAction = new Panel_Sprite_Func() {
		@Override
		public void invoke(Panel_Sprite psp) {
			PlayerStr pp = psp.PlayerP();

			psp.y -= 3 * synctics;

			if (psp.y < MICRO_YOFF) {
				psp.y = MICRO_YOFF;
				psp.yorig = psp.y;
			}

			pMicroBobSetup(psp);
			pWeaponBob(psp, PLAYER_MOVING(psp.PlayerP()) != 0);
			if (!pp.InitingNuke)
				pSetState(psp, psp.PresentState);
		}
	};

	private static final Panel_Sprite_Func pMicroStandBy = new Panel_Sprite_Func() {
		@Override
		public void invoke(Panel_Sprite psp) {
			PlayerStr pp = psp.PlayerP();

			pMicroOverlays(psp);
			pp.nukevochandle = PlaySound(DIGI_NUKESTDBY, psp.PlayerP(), v3df_follow | v3df_dontpan);
		}
	};

	private static final Panel_Sprite_Func pMicroCount = new Panel_Sprite_Func() {
		@Override
		public void invoke(Panel_Sprite psp) {
			PlayerStr pp = psp.PlayerP();

			pp.nukevochandle = PlaySound(DIGI_NUKECDOWN, psp.PlayerP(), v3df_follow | v3df_dontpan);
		}
	};

	private static final Panel_Sprite_Func pMicroReady = new Panel_Sprite_Func() {
		@Override
		public void invoke(Panel_Sprite psp) {
			PlayerStr pp = psp.PlayerP();

			pp.nukevochandle = PlaySound(DIGI_NUKEREADY, psp.PlayerP(), v3df_follow | v3df_dontpan);
			pp.NukeInitialized = true;
		}
	};

	private static final Panel_State ps_InitNuke_0 = new Panel_State(ID_MicroPresent0, Micro_ACTION_RATE, pNukeAction);
	private static final Panel_State ps_InitNuke_1 = new Panel_State(ID_MicroPresent0, 0, pMicroStandBy);
	private static final Panel_State ps_InitNuke_2 = new Panel_State(ID_MicroPresent0, 120 * 2, pNukeAction);
	private static final Panel_State ps_InitNuke_3 = new Panel_State(ID_MicroPresent0, 0, pMicroCount);
	private static final Panel_State ps_InitNuke_4 = new Panel_State(ID_MicroPresent0, 120 * 3, pNukeAction);
	private static final Panel_State ps_InitNuke_5 = new Panel_State(ID_MicroPresent0, 0, pMicroReady);
	private static final Panel_State ps_InitNuke_6 = new Panel_State(ID_MicroPresent0, 120 * 2, pNukeAction);
	private static final Panel_State ps_InitNuke_7 = new Panel_State(ID_MicroPresent0, 3, pNukeAction);

	private static final Panel_State ps_InitNuke[] = { ps_InitNuke_0.setNext(ps_InitNuke_1),
			ps_InitNuke_1.setNext(ps_InitNuke_2).setFlags(psf_QuickCall), ps_InitNuke_2.setNext(ps_InitNuke_3),
			ps_InitNuke_3.setNext(ps_InitNuke_4).setFlags(psf_QuickCall), ps_InitNuke_4.setNext(ps_InitNuke_5),
			ps_InitNuke_5.setNext(ps_InitNuke_6).setFlags(psf_QuickCall), ps_InitNuke_6.setNext(ps_InitNuke_7),
			ps_InitNuke_7.setNext(ps_MicroRest[0]), };

	private static final Panel_Sprite_Func pMicroPresent = new Panel_Sprite_Func() {
		@Override
		public void invoke(Panel_Sprite psp) {
			PlayerStr pp = psp.PlayerP();

			if (TEST(psp.PlayerP().Flags, PF_WEAPON_RETRACT))
				return;

			// Needed for recoil
			psp.ang = NORM_ANGLE(256 + 96);
			pMicroSetRecoil(psp);
			///

			psp.y -= 3 * synctics;

			if (psp.y < MICRO_YOFF) {
				psp.y = MICRO_YOFF;
				psp.yorig = psp.y;
				if (pp.WpnRocketType == 2 && !pp.NukeInitialized) {
					pp.TestNukeInit = false;
					pSetState(psp, ps_InitNuke[0]);
				} else
					pSetState(psp, psp.RestState);
			}
		}
	};

	private static final Panel_State ps_PresentMicro[] = {
			new Panel_State(ID_MicroPresent0, Micro_REST_RATE, pMicroPresent).setNext() };

	private static final Panel_Sprite_Func pMicroRetract = new Panel_Sprite_Func() {
		@Override
		public void invoke(Panel_Sprite psp) {
			psp.y += 3 * synctics;

			if (psp.y >= MICRO_YOFF + engine.getTile(psp.picndx).getHeight()) {
				psp.PlayerP().Flags &= ~(PF_WEAPON_RETRACT);
				psp.PlayerP().Wpn[WPN_MICRO] = null;
				pKillSprite(psp);
			}
		}
	};

	private static final Panel_State ps_RetractMicro[] = {
			new Panel_State(ID_MicroPresent0, Micro_REST_RATE, pMicroRetract).setNext() };

	private static final Panel_Sprite_Func pMicroHide = new Panel_Sprite_Func() {
		@Override
		public void invoke(Panel_Sprite psp) {
			short picnum = psp.picndx;

			psp.y += 3 * synctics;

			if (psp.y >= MICRO_YOFF + engine.getTile(picnum).getHeight() + 20) {
				psp.y = MICRO_YOFF + engine.getTile(picnum).getHeight() + 20;
				psp.x = MICRO_XOFF;

				pWeaponUnHideKeys(psp, psp.PresentState);
			}
		}
	};

	private static final Panel_State ps_MicroHide[] = {
			new Panel_State(ID_MicroPresent0, Micro_REST_RATE, pMicroHide).setNext() };

	public static void InitWeaponMicro(PlayerStr pp) {
		Panel_Sprite psp;

		if (Prediction)
			return;

		if (!TEST(pp.WpnFlags, BIT(WPN_MICRO)) || TEST(pp.Flags, PF_WEAPON_RETRACT))
			return;

		if (pp.Wpn[WPN_MICRO] == null) {
			psp = pp.Wpn[WPN_MICRO] = pSpawnSprite(pp, ps_PresentMicro[0], PRI_MID, MICRO_XOFF, MICRO_YOFF);
			psp.y += engine.getTile(psp.picndx).getHeight();
		}

		if (pp.CurWpn == pp.Wpn[WPN_MICRO]) {
			if (pp.TestNukeInit && pp.WpnRocketType == 2 && !pp.InitingNuke && pp.WpnRocketNuke != 0
					&& !pp.NukeInitialized) {
				pp.TestNukeInit = false;
				pp.InitingNuke = true;
				psp = pp.Wpn[WPN_MICRO];
				pSetState(psp, ps_InitNuke[0]);
			}
			return;
		}

		PlayerUpdateWeapon(pp, WPN_MICRO);

		pp.WpnUziType = 2; // Make uzi's go away!
		RetractCurWpn(pp);

		// Set up the new Weapon variables
		psp = pp.CurWpn = pp.Wpn[WPN_MICRO];
		psp.flags |= (PANF_WEAPON_SPRITE);
		psp.ActionState = ps_MicroFire[0];
		psp.RetractState = ps_RetractMicro[0];
		psp.RestState = ps_MicroRest[0];
		psp.PresentState = ps_PresentMicro[0];
		pSetState(psp, psp.PresentState);

		if (pp.WpnRocketType == 2 && !pp.InitingNuke && !pp.NukeInitialized)
			pp.TestNukeInit = pp.InitingNuke = true;

		PlaySound(DIGI_ROCKET_UP, pp, v3df_follow);

		FLAG_KEY_RELEASE(psp.PlayerP(), SK_SHOOT);
		FLAG_KEY_RESET(psp.PlayerP(), SK_SHOOT);
	}

	private static void pMicroSetRecoil(Panel_Sprite psp) {
		psp.vel = 900;
		psp.ang = NORM_ANGLE(-256);
	}

	private static boolean pMicroOverlays(Panel_Sprite psp) {
		if (psp.over[MICRO_SIGHT_NUM].xoff == -1) {
			psp.over[MICRO_SIGHT_NUM].xoff = MICRO_SIGHT_XOFF;
			psp.over[MICRO_SIGHT_NUM].yoff = MICRO_SIGHT_YOFF;
			psp.over[MICRO_SHOT_NUM].xoff = MICRO_SHOT_XOFF;
			psp.over[MICRO_SHOT_NUM].yoff = MICRO_SHOT_YOFF;
			psp.over[MICRO_HEAT_NUM].xoff = MICRO_HEAT_XOFF;
			psp.over[MICRO_HEAT_NUM].yoff = MICRO_HEAT_YOFF;

		}

		if (psp.PlayerP().WpnRocketNuke == 0 && psp.PlayerP().WpnRocketType == 2)
			psp.PlayerP().WpnRocketType = 0;

		switch (psp.PlayerP().WpnRocketType) {
		case 0:
			psp.over[MICRO_SIGHT_NUM].pic = MICRO_SIGHT;
			psp.over[MICRO_SHOT_NUM].pic = MICRO_SHOT_1;
			psp.over[MICRO_SHOT_NUM].flags |= (psf_ShadeNone);
			psp.over[MICRO_HEAT_NUM].pic = -1;
			return (false);
		case 1:
			if (psp.PlayerP().WpnRocketHeat != 0) {
				psp.over[MICRO_SIGHT_NUM].pic = MICRO_SIGHT;
				psp.over[MICRO_SHOT_NUM].pic = MICRO_SHOT_1;
				psp.over[MICRO_SHOT_NUM].flags |= (psf_ShadeNone);

				psp.over[MICRO_HEAT_NUM].pic = (short) (MICRO_HEAT + (5 - psp.PlayerP().WpnRocketHeat));
			} else {
				psp.over[MICRO_SIGHT_NUM].pic = MICRO_SIGHT;
				psp.over[MICRO_SHOT_NUM].pic = MICRO_SHOT_1;
				psp.over[MICRO_SHOT_NUM].flags |= (psf_ShadeNone);
				psp.over[MICRO_HEAT_NUM].pic = -1;
			}

			return (false);
		case 2:
			psp.over[MICRO_SIGHT_NUM].pic = -1;
			psp.over[MICRO_HEAT_NUM].pic = -1;

			psp.over[MICRO_SHOT_NUM].pic = MICRO_SHOT_20;
			psp.over[MICRO_SHOT_NUM].flags |= (psf_ShadeNone);
			psp.over[MICRO_HEAT_NUM].flags |= (psf_ShadeNone);
			return (true);
		}
		return (false);
	}

	private static void pMicroBobSetup(Panel_Sprite psp) {
		if (TEST(psp.flags, PANF_BOB))
			return;

		psp.xorig = psp.x;
		psp.yorig = psp.y;

		psp.sin_amt = MICRO_BOB_X_AMT;
		psp.sin_ndx = 0;
		psp.bob_height_shift = 3;
	}

	public static void MicroSaveable() {
		SaveData(ps_PresentMicro);
		SaveData(ps_MicroRest);
		SaveData(ps_MicroHide);
		SaveData(ps_InitNuke);
		SaveData(ps_MicroRecoil);
		SaveData(ps_MicroFire);
		SaveData(ps_MicroSingleFire);
		SaveData(ps_RetractMicro);
		SaveData(ps_MicroHeatFlash);
		SaveData(ps_MicroNukeFlash);

		SaveData(pMicroAction);
		SaveData(pMicroFire);
		SaveData(pMicroRest);
		SaveData(pMicroRecoilDown);
		SaveData(pMicroRecoilUp);
		SaveData(pNukeAction);
		SaveData(pMicroStandBy);
		SaveData(pMicroCount);
		SaveData(pMicroReady);
		SaveData(pMicroPresent);
		SaveData(pMicroRetract);
		SaveData(pMicroHide);
	}
}
