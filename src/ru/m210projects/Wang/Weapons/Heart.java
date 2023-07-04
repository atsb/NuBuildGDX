package ru.m210projects.Wang.Weapons;

import static ru.m210projects.Wang.Digi.DIGI_HEARTBEAT;
import static ru.m210projects.Wang.Digi.DIGI_HEARTFIRE;
import static ru.m210projects.Wang.Digi.DIGI_JG9009;
import static ru.m210projects.Wang.Factory.WangNetwork.Prediction;
import static ru.m210projects.Wang.Gameutils.FLAG_KEY_PRESSED;
import static ru.m210projects.Wang.Gameutils.FLAG_KEY_RELEASE;
import static ru.m210projects.Wang.Gameutils.FLAG_KEY_RESET;
import static ru.m210projects.Wang.Gameutils.PF_WEAPON_RETRACT;
import static ru.m210projects.Wang.Gameutils.PLAYER_MOVING;
import static ru.m210projects.Wang.Gameutils.RANDOM_P2;
import static ru.m210projects.Wang.Gameutils.RANDOM_RANGE;
import static ru.m210projects.Wang.Gameutils.SK_SHOOT;
import static ru.m210projects.Wang.Gameutils.TEST_SYNC_KEY;
import static ru.m210projects.Wang.Gameutils.WPN_HEART;
import static ru.m210projects.Wang.Gameutils.synctics;
import static ru.m210projects.Wang.Main.engine;
import static ru.m210projects.Wang.Panel.DoPlayerChooseYell;
import static ru.m210projects.Wang.Panel.PANF_BOB;
import static ru.m210projects.Wang.Panel.PANF_FALLING;
import static ru.m210projects.Wang.Panel.PANF_JUMPING;
import static ru.m210projects.Wang.Panel.PANF_UNHIDE_SHOOT;
import static ru.m210projects.Wang.Panel.PANF_WEAPON_SPRITE;
import static ru.m210projects.Wang.Panel.PRI_BACK;
import static ru.m210projects.Wang.Panel.PRI_MID;
import static ru.m210projects.Wang.Panel.PlayerUpdateWeapon;
import static ru.m210projects.Wang.Panel.RetractCurWpn;
import static ru.m210projects.Wang.Panel.WeaponOK;
import static ru.m210projects.Wang.Panel.castLongX;
import static ru.m210projects.Wang.Panel.castLongY;
import static ru.m210projects.Wang.Panel.pKillSprite;
import static ru.m210projects.Wang.Panel.pSetState;
import static ru.m210projects.Wang.Panel.pSpawnSprite;
import static ru.m210projects.Wang.Panel.pSuicide;
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
import static ru.m210projects.Wang.Sound.v3df_doppler;
import static ru.m210projects.Wang.Sound.v3df_follow;
import static ru.m210projects.Wang.Sprites.MoveSkip2;
import static ru.m210projects.Wang.Type.MyTypes.BIT;
import static ru.m210projects.Wang.Type.MyTypes.TEST;
import static ru.m210projects.Wang.Type.Saveable.SaveData;
import static ru.m210projects.Wang.Weapon.InitHeartAttack;

import ru.m210projects.Wang.Type.Panel_Sprite;
import ru.m210projects.Wang.Type.Panel_Sprite_Func;
import ru.m210projects.Wang.Type.Panel_State;
import ru.m210projects.Wang.Type.PlayerStr;

public class Heart {

	private static class Panel_Shrap {
		public int xoff, yoff, skip;
		public int lo_jump_speed, hi_jump_speed, lo_xspeed, hi_xspeed;
		public Panel_State state[][];

		public Panel_Shrap(int xoff, int yoff, int skip, int lo_jump_speed, int hi_jump_speed, int lo_xspeed,
				int hi_xspeed, Panel_State state[][]) {
			this.xoff = xoff;
			this.yoff = yoff;
			this.skip = skip;
			this.lo_jump_speed = lo_jump_speed;
			this.hi_jump_speed = hi_jump_speed;
			this.lo_xspeed = lo_xspeed;
			this.hi_xspeed = hi_xspeed;
			this.state = state;
		}
	}

	private static final int HEART_REST = 2050;

	private static final int HEART_ATTACK = 2052;

	private static final int ID_HeartPresent0 = HEART_REST + 0;
	private static final int ID_HeartPresent1 = HEART_REST + 1;

	private static final int ID_HeartAttack0 = HEART_ATTACK + 0;
	private static final int ID_HeartAttack1 = HEART_ATTACK + 1;

	private static final int HEART_BLOOD = 2420;
	private static final int ID_HeartBlood0 = HEART_BLOOD + 0;
	private static final int ID_HeartBlood1 = HEART_BLOOD + 1;
	private static final int ID_HeartBlood2 = HEART_BLOOD + 2;
	private static final int ID_HeartBlood3 = HEART_BLOOD + 3;
	private static final int ID_HeartBlood4 = HEART_BLOOD + 4;
	private static final int ID_HeartBlood5 = HEART_BLOOD + 5;

	private static final int HEART_YOFF = 212;
	private static final int Heart_BEAT_RATE = 60;
	private static final int Heart_ACTION_RATE = 10;

	private static final Panel_Sprite_Func pHeartPresent = new Panel_Sprite_Func() {
		@Override
		public void invoke(Panel_Sprite psp) {
			if (TEST(psp.PlayerP().Flags, PF_WEAPON_RETRACT))
				return;

			psp.y -= 3 * synctics;

			if (psp.y < HEART_YOFF) {
				psp.y = HEART_YOFF;
				psp.yorig = psp.y;
				pSetState(psp, psp.RestState);
			}
		}
	};

	private static final Panel_State ps_PresentHeart_0 = new Panel_State(ID_HeartPresent0, Heart_BEAT_RATE,
			pHeartPresent);
	private static final Panel_State ps_PresentHeart_1 = new Panel_State(ID_HeartPresent1, Heart_BEAT_RATE,
			pHeartPresent);

	private static final Panel_State ps_PresentHeart[] = { ps_PresentHeart_0.setNext(ps_PresentHeart_1),
			ps_PresentHeart_1.setNext(ps_PresentHeart_0), };

	private static final Panel_Sprite_Func pHeartHide = new Panel_Sprite_Func() {
		@Override
		public void invoke(Panel_Sprite psp) {
			short picnum = psp.picndx;

			psp.y += 3 * synctics;

			if (psp.y >= HEART_YOFF + engine.getTile(picnum).getHeight()) {
				psp.y = HEART_YOFF + engine.getTile(picnum).getHeight();

				pWeaponUnHideKeys(psp, psp.PresentState);
			}
		}
	};

	private static final Panel_State ps_HeartHide_0 = new Panel_State(ID_HeartPresent0, Heart_BEAT_RATE, pHeartHide);
	private static final Panel_State ps_HeartHide_1 = new Panel_State(ID_HeartPresent1, Heart_BEAT_RATE, pHeartHide);

	private static final Panel_State ps_HeartHide[] = { ps_HeartHide_0.setNext(ps_HeartHide_1),
			ps_HeartHide_1.setNext(ps_HeartHide_0), };

	private static final Panel_Sprite_Func pHeartRetract = new Panel_Sprite_Func() {
		@Override
		public void invoke(Panel_Sprite psp) {
			short picnum = psp.picndx;

			psp.y += 3 * synctics;

			if (psp.y >= HEART_YOFF + engine.getTile(picnum).getHeight()) {
				psp.PlayerP().Flags &= ~(PF_WEAPON_RETRACT);
				psp.PlayerP().Wpn[WPN_HEART] = null;
				pKillSprite(psp);
			}
		}
	};

	private static final Panel_State ps_RetractHeart_0 = new Panel_State(ID_HeartPresent0, Heart_BEAT_RATE,
			pHeartRetract);
	private static final Panel_State ps_RetractHeart_1 = new Panel_State(ID_HeartPresent1, Heart_BEAT_RATE,
			pHeartRetract);

	private static final Panel_State ps_RetractHeart[] = { ps_RetractHeart_0.setNext(ps_RetractHeart_1),
			ps_RetractHeart_1.setNext(ps_RetractHeart_0), };

	private static final Panel_Sprite_Func pHeartRest = new Panel_Sprite_Func() {
		@Override
		public void invoke(Panel_Sprite psp) {
			boolean force = !!TEST(psp.flags, PANF_UNHIDE_SHOOT);

			if (pWeaponHideKeys(psp, ps_HeartHide[0]))
				return;

			psp.yorig += synctics;

			if (psp.yorig > HEART_YOFF) {
				psp.yorig = HEART_YOFF;
			}

			psp.y = psp.yorig;

			pHeartBobSetup(psp);
			pWeaponBob(psp, PLAYER_MOVING(psp.PlayerP()) != 0);

			if (TEST_SYNC_KEY(psp.PlayerP(), SK_SHOOT) || force) {
				if (FLAG_KEY_PRESSED(psp.PlayerP(), SK_SHOOT) || force) {
					FLAG_KEY_RELEASE(psp.PlayerP(), SK_SHOOT);
					psp.flags &= ~(PANF_UNHIDE_SHOOT);

					if (!WeaponOK(psp.PlayerP()))
						return;

					DoPlayerChooseYell(psp.PlayerP());
					pSetState(psp, psp.ActionState);
				}
			} else {
				FLAG_KEY_RESET(psp.PlayerP(), SK_SHOOT);
				WeaponOK(psp.PlayerP());
			}
		}
	};

	private static final int HEART_BLOOD_SMALL_RATE = 7;

	private static final Panel_Sprite_Func pHeartBlood = new Panel_Sprite_Func() {
		@Override
		public void invoke(Panel_Sprite psp) {
			int x = castLongX(psp);
			int y = castLongY(psp);

			if (TEST(psp.flags, PANF_JUMPING)) {
				DoPanelJump(psp);
			} else if (TEST(psp.flags, PANF_FALLING)) {
				DoPanelFall(psp);
			}

			x += psp.xspeed;
			setLong(psp, x, y);

			if (psp.x > 320 || psp.x < 0 || psp.y > 200) {
				pKillSprite(psp);
				return;
			}
		}
	};

	private static final Panel_State ps_HeartBloodSmall_0 = new Panel_State(ID_HeartBlood0, HEART_BLOOD_SMALL_RATE,
			pHeartBlood);
	private static final Panel_State ps_HeartBloodSmall_1 = new Panel_State(ID_HeartBlood1, HEART_BLOOD_SMALL_RATE,
			pHeartBlood);
	private static final Panel_State ps_HeartBloodSmall_2 = new Panel_State(ID_HeartBlood2, HEART_BLOOD_SMALL_RATE,
			pHeartBlood);
	private static final Panel_State ps_HeartBloodSmall_3 = new Panel_State(ID_HeartBlood3, HEART_BLOOD_SMALL_RATE,
			pHeartBlood);
	private static final Panel_State ps_HeartBloodSmall_4 = new Panel_State(ID_HeartBlood4, HEART_BLOOD_SMALL_RATE,
			pHeartBlood);
	private static final Panel_State ps_HeartBloodSmall_5 = new Panel_State(ID_HeartBlood5, HEART_BLOOD_SMALL_RATE,
			pHeartBlood);
	private static final Panel_State ps_HeartBloodSmall_6 = new Panel_State(ID_HeartBlood5, HEART_BLOOD_SMALL_RATE,
			pSuicide);

	private static final Panel_State ps_HeartBloodSmall[] = { ps_HeartBloodSmall_0.setNext(ps_HeartBloodSmall_1),
			ps_HeartBloodSmall_1.setNext(ps_HeartBloodSmall_2), ps_HeartBloodSmall_2.setNext(ps_HeartBloodSmall_3),
			ps_HeartBloodSmall_3.setNext(ps_HeartBloodSmall_4), ps_HeartBloodSmall_4.setNext(ps_HeartBloodSmall_5),
			ps_HeartBloodSmall_5.setNext(ps_HeartBloodSmall_6), ps_HeartBloodSmall_6.setNext() };

	private static final int HEART_BLOOD_RATE = 10;

	private static final Panel_State ps_HeartBlood_0 = new Panel_State(ID_HeartBlood0, HEART_BLOOD_RATE, pHeartBlood);
	private static final Panel_State ps_HeartBlood_1 = new Panel_State(ID_HeartBlood1, HEART_BLOOD_RATE, pHeartBlood);
	private static final Panel_State ps_HeartBlood_2 = new Panel_State(ID_HeartBlood2, HEART_BLOOD_RATE, pHeartBlood);
	private static final Panel_State ps_HeartBlood_3 = new Panel_State(ID_HeartBlood3, HEART_BLOOD_RATE, pHeartBlood);
	private static final Panel_State ps_HeartBlood_4 = new Panel_State(ID_HeartBlood4, HEART_BLOOD_RATE, pHeartBlood);
	private static final Panel_State ps_HeartBlood_5 = new Panel_State(ID_HeartBlood5, HEART_BLOOD_RATE, pHeartBlood);
	private static final Panel_State ps_HeartBlood_6 = new Panel_State(ID_HeartBlood5, HEART_BLOOD_RATE, pSuicide);

	private static final Panel_State ps_HeartBlood[] = { ps_HeartBlood_0.setNext(ps_HeartBlood_1),
			ps_HeartBlood_1.setNext(ps_HeartBlood_2), ps_HeartBlood_2.setNext(ps_HeartBlood_3),
			ps_HeartBlood_3.setNext(ps_HeartBlood_4), ps_HeartBlood_4.setNext(ps_HeartBlood_5),
			ps_HeartBlood_5.setNext(ps_HeartBlood_6), ps_HeartBlood_6.setNext() };

	private static final Panel_Shrap HeartShrap[] = {
			new Panel_Shrap(-10, -80, 0, -FIXED(1, 0), -FIXED(2, 0), -FIXED(1, 0), -FIXED(3, 0),
					new Panel_State[][] { ps_HeartBloodSmall, ps_HeartBloodSmall }),
			new Panel_Shrap(0, -85, 0, -FIXED(1, 0), -FIXED(5, 0), -FIXED(1, 0), -FIXED(1, 0),
					new Panel_State[][] { ps_HeartBloodSmall, ps_HeartBloodSmall }),
			new Panel_Shrap(10, -85, 0, -FIXED(1, 0), -FIXED(2, 0), FIXED(1, 0), FIXED(2, 0),
					new Panel_State[][] { ps_HeartBloodSmall, ps_HeartBloodSmall }),
			new Panel_Shrap(25, -80, 0, -FIXED(1, 0), -FIXED(2, 0), FIXED(3, 0), FIXED(4, 0),
					new Panel_State[][] { ps_HeartBloodSmall, ps_HeartBloodSmall }), };

	private static final Panel_Sprite_Func SpawnSmallHeartBlood = new Panel_Sprite_Func() {
		@Override
		public void invoke(Panel_Sprite psp) {
			PlayerStr pp = psp.PlayerP();
			Panel_Sprite blood;

			PlaySound(DIGI_HEARTBEAT, pp, v3df_follow | v3df_dontpan | v3df_doppler);

			Panel_Shrap hsp = HeartShrap[0];
			for (int p = 0; p < HeartShrap.length && hsp.lo_jump_speed != 0; p++) {
				hsp = HeartShrap[p];

				// RIGHT side
				blood = pSpawnSprite(pp, hsp.state[RANDOM_P2(2 << 8) >> 8][0], PRI_BACK, 0, 0);
				blood.x = psp.x + hsp.xoff;
				blood.y = psp.y + hsp.yoff;
				blood.xspeed = hsp.lo_xspeed + (RANDOM_RANGE((hsp.hi_xspeed - hsp.lo_xspeed) >> 4) << 4);
				blood.flags |= (PANF_WEAPON_SPRITE);

				blood.scale = 10000 + RANDOM_RANGE(30000 - 10000);

				blood.jump_speed = hsp.lo_jump_speed
						+ (RANDOM_RANGE((hsp.hi_jump_speed + hsp.lo_jump_speed) >> 4) << 4);
				DoBeginPanelJump(blood);
			}
		}
	};

	private static final Panel_State ps_HeartRest_0 = new Panel_State(ID_HeartPresent0, Heart_BEAT_RATE, pHeartRest);
	private static final Panel_State ps_HeartRest_1 = new Panel_State(ID_HeartPresent1, Heart_BEAT_RATE, pHeartRest);
	private static final Panel_State ps_HeartRest_2 = new Panel_State(ID_HeartPresent1, Heart_BEAT_RATE,
			SpawnSmallHeartBlood);
	private static final Panel_State ps_HeartRest_3 = new Panel_State(ID_HeartPresent1, 0, pHeartRest);

	private static final Panel_State ps_HeartRest[] = { ps_HeartRest_0.setNext(ps_HeartRest_1),
			ps_HeartRest_1.setNext(ps_HeartRest_2), ps_HeartRest_2.setNext(ps_HeartRest_3).setFlags(psf_QuickCall),
			ps_HeartRest_3.setNext(ps_HeartRest_0), };

	private static final Panel_Sprite_Func pHeartActionBlood = new Panel_Sprite_Func() {
		@Override
		public void invoke(Panel_Sprite psp) {
			psp.yorig -= synctics;

			if (psp.yorig < 200) {
				psp.yorig = 200;
			}

			psp.y = psp.yorig;

			pHeartBobSetup(psp);
			pWeaponBob(psp, PLAYER_MOVING(psp.PlayerP()) != 0);

			SpawnHeartBlood(psp);
		}
	};

	private static final Panel_Sprite_Func pHeartAction = new Panel_Sprite_Func() {
		@Override
		public void invoke(Panel_Sprite psp) {
			psp.yorig -= synctics;

			if (psp.yorig < 200) {
				psp.yorig = 200;
			}

			psp.y = psp.yorig;

			pHeartBobSetup(psp);
			pWeaponBob(psp, PLAYER_MOVING(psp.PlayerP()) != 0);
		}
	};

	private static final Panel_Sprite_Func pHeartAttack = new Panel_Sprite_Func() {
		@Override
		public void invoke(Panel_Sprite psp) {
			PlayerStr pp = psp.PlayerP();

			PlaySound(DIGI_HEARTFIRE, pp, v3df_follow | v3df_dontpan);
			if (RANDOM_RANGE(1000) > 800)
				PlayerSound(DIGI_JG9009, v3df_follow | v3df_dontpan, pp);
			InitHeartAttack(psp.PlayerP());
		}
	};

	// squeeze
	private static final Panel_State ps_HeartAttack_0 = new Panel_State(ID_HeartAttack0, Heart_ACTION_RATE,
			pHeartActionBlood);
	private static final Panel_State ps_HeartAttack_1 = new Panel_State(ID_HeartAttack1, Heart_ACTION_RATE,
			pHeartActionBlood);
	private static final Panel_State ps_HeartAttack_2 = new Panel_State(ID_HeartAttack1, Heart_ACTION_RATE,
			pHeartActionBlood);
	// attack
	private static final Panel_State ps_HeartAttack_3 = new Panel_State(ID_HeartAttack1, Heart_ACTION_RATE,
			pHeartAttack);
	// unsqueeze
	private static final Panel_State ps_HeartAttack_4 = new Panel_State(ID_HeartAttack1, Heart_ACTION_RATE,
			pHeartAction);
	private static final Panel_State ps_HeartAttack_5 = new Panel_State(ID_HeartAttack1, Heart_ACTION_RATE,
			pHeartAction);
	private static final Panel_State ps_HeartAttack_6 = new Panel_State(ID_HeartAttack0, Heart_ACTION_RATE,
			pHeartAction);

	private static final Panel_State ps_HeartAttack_7 = new Panel_State(ID_HeartAttack0, Heart_ACTION_RATE,
			pHeartAction);

	private static final Panel_State ps_HeartAttack[] = {
			ps_HeartAttack_0.setNext(ps_HeartAttack_1).setFlags(psf_ShadeHalf),
			ps_HeartAttack_1.setNext(ps_HeartAttack_2).setFlags(psf_ShadeNone),
			ps_HeartAttack_2.setNext(ps_HeartAttack_3).setFlags(psf_ShadeNone),
			ps_HeartAttack_3.setNext(ps_HeartAttack_4).setFlags(psf_QuickCall),
			ps_HeartAttack_4.setNext(ps_HeartAttack_5).setFlags(psf_ShadeNone),
			ps_HeartAttack_5.setNext(ps_HeartAttack_6).setFlags(psf_ShadeNone),
			ps_HeartAttack_6.setNext(ps_HeartAttack_7).setFlags(psf_ShadeHalf),
			ps_HeartAttack_7.setNext(ps_HeartRest[0]).setFlags(psf_ShadeHalf), };

	public static void InitWeaponHeart(PlayerStr pp) {
		Panel_Sprite psp;

		if (Prediction)
			return;

		if (!TEST(pp.WpnFlags, BIT(WPN_HEART)) || TEST(pp.Flags, PF_WEAPON_RETRACT))
			return;

		if (pp.Wpn[WPN_HEART] == null) {
			psp = pp.Wpn[WPN_HEART] = pSpawnSprite(pp, ps_PresentHeart[0], PRI_MID, 160 + 10, HEART_YOFF);
			psp.y += engine.getTile(psp.picndx).getHeight();
		}

		if (pp.CurWpn == pp.Wpn[WPN_HEART]) {
			return;
		}

		PlayerUpdateWeapon(pp, WPN_HEART);

		pp.WpnUziType = 2; // Make uzi's go away!
		RetractCurWpn(pp);

		PlaySound(DIGI_HEARTBEAT, pp, v3df_follow | v3df_dontpan | v3df_doppler);

		// Set up the new Weapon variables
		psp = pp.CurWpn = pp.Wpn[WPN_HEART];
		psp.flags |= (PANF_WEAPON_SPRITE);
		psp.ActionState = ps_HeartAttack[0];
		psp.RetractState = ps_RetractHeart[0];
		psp.PresentState = ps_PresentHeart[0];
		psp.RestState = ps_HeartRest[0];
		pSetState(psp, psp.PresentState);

		FLAG_KEY_RELEASE(psp.PlayerP(), SK_SHOOT);
		FLAG_KEY_RESET(psp.PlayerP(), SK_SHOOT);
	}

	private static void pHeartBobSetup(Panel_Sprite psp) {
		if (TEST(psp.flags, PANF_BOB))
			return;

		psp.xorig = psp.x;
		psp.yorig = psp.y;

		psp.sin_amt = 12;
		psp.sin_ndx = 0;
		psp.bob_height_shift = 3;
	}

	private static final int PANEL_JUMP_GRAVITY = 8000;

	private static void DoBeginPanelJump(Panel_Sprite psp) {

		psp.flags |= (PANF_JUMPING);
		psp.flags &= ~(PANF_FALLING);

		// set up individual actor jump gravity
		psp.jump_grav = PANEL_JUMP_GRAVITY;

		DoPanelJump(psp);
	}

	private static void DoPanelJump(Panel_Sprite psp) {
		int jump_adj;

		int x = castLongX(psp);
		int y = castLongY(psp);

		// precalculate jump value to adjust jump speed by
		jump_adj = psp.jump_grav;

		// adjust jump speed by gravity - if jump speed greater than 0 player
		// have started falling
		if ((psp.jump_speed += jump_adj) > 0) {
			// Start falling
			DoBeginPanelFall(psp);
			return;
		}

		// adjust height by jump speed
		y += psp.jump_speed * synctics;
		setLong(psp, x, y);
	}

	private static void DoBeginPanelFall(Panel_Sprite psp) {
		psp.flags |= (PANF_FALLING);
		psp.flags &= ~(PANF_JUMPING);

		psp.jump_grav = PANEL_JUMP_GRAVITY;

		DoPanelFall(psp);
	}

	private static void DoPanelFall(Panel_Sprite psp) {
		int x = castLongX(psp);
		int y = castLongY(psp);

		// adjust jump speed by gravity
		psp.jump_speed += psp.jump_grav;

		// adjust player height by jump speed
		y += psp.jump_speed * synctics;
		setLong(psp, x, y);
	}

	private static Panel_Shrap HeartShrap2[] = {
			new Panel_Shrap(-10, -80, 2, -FIXED(1, 32000), -FIXED(2, 32000), -FIXED(5, 32000), -FIXED(3, 32000),
					new Panel_State[][] { ps_HeartBlood, ps_HeartBlood }),
			new Panel_Shrap(0, -85, 0, -FIXED(3, 32000), -FIXED(8, 32000), -FIXED(3, 32000), -FIXED(1, 32000),
					new Panel_State[][] { ps_HeartBlood, ps_HeartBlood }),
			new Panel_Shrap(10, -85, 2, -FIXED(1, 32000), -FIXED(2, 32000), FIXED(2, 32000), FIXED(3, 32000),
					new Panel_State[][] { ps_HeartBlood, ps_HeartBlood }),
			new Panel_Shrap(25, -80, 2, -FIXED(1, 32000), -FIXED(2, 32000), FIXED(5, 32000), FIXED(6, 32000),
					new Panel_State[][] { ps_HeartBlood, ps_HeartBlood }), };

	private static int FIXED(int msw, int lsw) {
		return (msw << 16) | lsw;
	}

	private static void SpawnHeartBlood(Panel_Sprite psp) {
		PlayerStr pp = psp.PlayerP();
		Panel_Sprite blood;

		Panel_Shrap hsp = HeartShrap2[0];
		for (int p = 0; p < HeartShrap2.length && hsp.lo_jump_speed != 0; p++) {
			hsp = HeartShrap2[p];

			if (hsp.skip == 2) {
				if (MoveSkip2)
					continue;
			}

			// RIGHT side
			blood = pSpawnSprite(pp, hsp.state[RANDOM_P2(2 << 8) >> 8][0], PRI_BACK, 0, 0);
			blood.x = psp.x + hsp.xoff;
			blood.y = psp.y + hsp.yoff;
			blood.xspeed = hsp.lo_xspeed + (RANDOM_RANGE((hsp.hi_xspeed - hsp.lo_xspeed) >> 4) << 4);
			blood.flags |= (PANF_WEAPON_SPRITE);

			blood.scale = 20000 + RANDOM_RANGE(50000 - 20000);

			blood.jump_speed = hsp.lo_jump_speed + (RANDOM_RANGE((hsp.hi_jump_speed + hsp.lo_jump_speed) >> 4) << 4);
			DoBeginPanelJump(blood);
		}
	}

	public static void HeartSaveable() {
		SaveData(ps_PresentHeart);
		SaveData(ps_HeartRest);
		SaveData(ps_HeartHide);
		SaveData(ps_HeartAttack);
		SaveData(ps_RetractHeart);
		SaveData(ps_HeartBlood);
		SaveData(ps_HeartBloodSmall);
		SaveData(ps_HeartBlood);

		SaveData(pHeartPresent);
		SaveData(pHeartHide);
		SaveData(pHeartRetract);
		SaveData(pHeartRest);
		SaveData(pHeartBlood);
		SaveData(SpawnSmallHeartBlood);
		SaveData(pHeartActionBlood);
		SaveData(pHeartAction);
		SaveData(pHeartAttack);
	}
}
