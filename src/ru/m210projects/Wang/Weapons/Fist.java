package ru.m210projects.Wang.Weapons;

import static ru.m210projects.Build.Engine.sintable;
import static ru.m210projects.Wang.Digi.DIGI_PLAYERYELL1;
import static ru.m210projects.Wang.Digi.DIGI_PLAYERYELL2;
import static ru.m210projects.Wang.Digi.DIGI_TAUNTAI2;
import static ru.m210projects.Wang.Factory.WangNetwork.Prediction;
import static ru.m210projects.Wang.Game.Player;
import static ru.m210projects.Wang.Gameutils.FLAG_KEY_PRESSED;
import static ru.m210projects.Wang.Gameutils.FLAG_KEY_RELEASE;
import static ru.m210projects.Wang.Gameutils.FLAG_KEY_RESET;
import static ru.m210projects.Wang.Gameutils.NORM_ANGLE;
import static ru.m210projects.Wang.Gameutils.PF_WEAPON_RETRACT;
import static ru.m210projects.Wang.Gameutils.PLAYER_MOVING;
import static ru.m210projects.Wang.Gameutils.RANDOM_P2;
import static ru.m210projects.Wang.Gameutils.RANDOM_RANGE;
import static ru.m210projects.Wang.Gameutils.SK_SHOOT;
import static ru.m210projects.Wang.Gameutils.TEST_SYNC_KEY;
import static ru.m210projects.Wang.Gameutils.WPN_FIST;
import static ru.m210projects.Wang.Gameutils.WPN_SWORD;
import static ru.m210projects.Wang.Gameutils.synctics;
import static ru.m210projects.Wang.Main.engine;
import static ru.m210projects.Wang.Panel.DoPlayerChooseYell;
import static ru.m210projects.Wang.Panel.InsertPanelSprite;
import static ru.m210projects.Wang.Panel.PANF_BOB;
import static ru.m210projects.Wang.Panel.PANF_TRANSLUCENT;
import static ru.m210projects.Wang.Panel.PANF_TRANS_FLIP;
import static ru.m210projects.Wang.Panel.PANF_UNHIDE_SHOOT;
import static ru.m210projects.Wang.Panel.PANF_WEAPON_SPRITE;
import static ru.m210projects.Wang.Panel.PANF_XFLIP;
import static ru.m210projects.Wang.Panel.PRI_BACK;
import static ru.m210projects.Wang.Panel.PRI_MID;
import static ru.m210projects.Wang.Panel.PlayerUpdateWeapon;
import static ru.m210projects.Wang.Panel.RetractCurWpn;
import static ru.m210projects.Wang.Panel.castLongX;
import static ru.m210projects.Wang.Panel.castLongY;
import static ru.m210projects.Wang.Panel.pKillSprite;
import static ru.m210projects.Wang.Panel.pSetState;
import static ru.m210projects.Wang.Panel.pSpawnSprite;
import static ru.m210projects.Wang.Panel.pWeaponBob;
import static ru.m210projects.Wang.Panel.pWeaponHideKeys;
import static ru.m210projects.Wang.Panel.pWeaponUnHideKeys;
import static ru.m210projects.Wang.Panel.psf_QuickCall;
import static ru.m210projects.Wang.Panel.psf_Xflip;
import static ru.m210projects.Wang.Panel.setLong;
import static ru.m210projects.Wang.Player.DoPlayerSpriteThrow;
import static ru.m210projects.Wang.Sound.PlaySound;
import static ru.m210projects.Wang.Sound.v3df_dontpan;
import static ru.m210projects.Wang.Sound.v3df_follow;
import static ru.m210projects.Wang.Type.MyTypes.BIT;
import static ru.m210projects.Wang.Type.MyTypes.TEST;
import static ru.m210projects.Wang.Type.Saveable.SaveData;
import static ru.m210projects.Wang.Weapon.InitFistAttack;
import static ru.m210projects.Wang.Weapons.Sword.InitWeaponSword;

import ru.m210projects.Wang.Type.List;
import ru.m210projects.Wang.Type.Panel_Sprite;
import ru.m210projects.Wang.Type.Panel_Sprite_Func;
import ru.m210projects.Wang.Type.Panel_State;
import ru.m210projects.Wang.Type.PlayerStr;

public class Fist {

	private static final int FIST_SWAY_AMT = 12;

	// left swing
	private static final int FIST_XOFF = (290 + FIST_SWAY_AMT);
	private static final int FIST_YOFF = 200;

	// right swing
	private static final int FISTR_XOFF = (0 - 80);

	private static final int Fist_BEAT_RATE = 16;

	public static final int FIST_REST = 4070;
	public static final int FIST_SWING0 = 4071;
	public static final int FIST_SWING1 = 4072;
	public static final int FIST_SWING2 = 4073;
	public static final int FIST2_REST = 4050;

	private static final int ID_FistPresent0 = FIST_REST;
	private static final int ID_Fist2Present0 = FIST2_REST;

	private static final int ID_FistSwing0 = FIST_SWING0;
	private static final int ID_FistSwing1 = FIST_SWING1;
	private static final int ID_FistSwing2 = FIST_SWING2;

	private static final int ID_Kick0 = 4080;
	private static final int ID_Kick1 = 4081;

	private static short FistAngTable[] = { 82, 168, 256 + 64 };

	private static final Panel_Sprite_Func pFistRest = new Panel_Sprite_Func() {
		@Override
		public void invoke(Panel_Sprite psp) {
			boolean force = !!TEST(psp.flags, PANF_UNHIDE_SHOOT);

			if (pWeaponHideKeys(psp, ps_FistHide[0]))
				return;

			psp.yorig += synctics;

			if (psp.yorig > FIST_YOFF) {
				psp.yorig = FIST_YOFF;
			}

			psp.y = psp.yorig;

			PlayerStr pp = Player[psp.PlayerP];

			pFistBobSetup(psp);
			pWeaponBob(psp, PLAYER_MOVING(pp) != 0);

			force = !!TEST(psp.flags, PANF_UNHIDE_SHOOT);

			if (psp.ActionState == ps_Kick[0])
				psp.ActionState = ps_FistSwing[0];

			// Reset move to default
			pp.WpnKungFuMove = 0;

			if (TEST_SYNC_KEY(pp, SK_SHOOT) || force) {
				if (FLAG_KEY_PRESSED(pp, SK_SHOOT) || force) {
					psp.flags &= ~(PANF_UNHIDE_SHOOT);

					psp.ActionState = ps_FistSwing[0];
					pp.WpnKungFuMove = 0;

					pSetState(psp, psp.ActionState);

					psp.vel = 5500;

					psp.ang = 1024;
					pp.FistAng = FistAngTable[RANDOM_RANGE(FistAngTable.length)];
					DoPlayerSpriteThrow(pp);
				}
			}
		}
	};

	private static final Panel_State ps_FistRest[] = {
			new Panel_State(ID_FistPresent0, Fist_BEAT_RATE, pFistRest).setNext(), };

	private static final Panel_Sprite_Func pFistPresent = new Panel_Sprite_Func() {
		@Override
		public void invoke(Panel_Sprite psp) {
			int rnd;

			if (TEST(Player[psp.PlayerP].Flags, PF_WEAPON_RETRACT))
				return;

			psp.y -= 3 * synctics;

			if (psp.y < FIST_YOFF) {
				psp.y = FIST_YOFF;
				psp.yorig = psp.y;

				rnd = RANDOM_RANGE(1000);
				if (rnd > 500) {
					psp.PresentState = ps_PresentFist[0];
					psp.RestState = ps_FistRest[0];
				} else {
					psp.PresentState = ps_PresentFist2[0];
					psp.RestState = ps_Fist2Rest[0];
				}
				pSetState(psp, psp.RestState);
			}
		}
	};

	private static final Panel_State ps_PresentFist[] = {
			new Panel_State(ID_FistPresent0, Fist_BEAT_RATE, pFistPresent).setNext(), };

	private static final Panel_Sprite_Func pFistHide = new Panel_Sprite_Func() {
		@Override
		public void invoke(Panel_Sprite psp) {
			short picnum = psp.picndx;

			psp.y += 3 * synctics;

			if (psp.y >= FIST_YOFF + engine.getTile(picnum).getHeight()) {
				psp.y = FIST_YOFF + engine.getTile(picnum).getHeight();

				pWeaponUnHideKeys(psp, psp.PresentState);
			}
		}
	};

	private static final Panel_State ps_FistHide[] = {
			new Panel_State(ID_FistPresent0, Fist_BEAT_RATE, pFistHide).setNext(), };

	private static final Panel_State ps_PresentFist2[] = {
			new Panel_State(ID_Fist2Present0, Fist_BEAT_RATE, pFistPresent).setNext(), };

	private static final Panel_State ps_Fist2Rest[] = {
			new Panel_State(ID_Fist2Present0, Fist_BEAT_RATE, pFistRest).setNext(), };

	private static final Panel_Sprite_Func pFistRetract = new Panel_Sprite_Func() {
		@Override
		public void invoke(Panel_Sprite psp) {
			short picnum = psp.picndx;

			psp.y += 3 * synctics;

			if (psp.y >= FIST_YOFF + engine.getTile(picnum).getHeight()) {
				PlayerStr pp = Player[psp.PlayerP];
				pp.Flags &= ~(PF_WEAPON_RETRACT);
				pp.Wpn[WPN_FIST] = null;
				pKillSprite(psp);
			}
		}
	};

	private static final Panel_State ps_RetractFist[] = {
			new Panel_State(ID_FistPresent0, Fist_BEAT_RATE, pFistRetract).setNext(), };

	private static final int FIST_PAUSE_TICS = 6;
	private static final int FIST_SLIDE_TICS = 6;
	private static final int FIST_MID_SLIDE_TICS = 16;

	private static final Panel_Sprite_Func pFistAttack = new Panel_Sprite_Func() {
		@Override
		public void invoke(Panel_Sprite psp) {
			InitFistAttack(psp.PlayerP());
		}
	};

	private static final Panel_Sprite_Func pFistSlideDown = new Panel_Sprite_Func() {
		@Override
		public void invoke(Panel_Sprite psp) {
			short vel, vel_adj;

			int x = castLongX(psp);
			int y = castLongY(psp);

			SpawnFistBlur(psp);
			vel_adj = 48;
			vel = 3500;

			if (psp.ActionState == ps_Kick[0] || psp.PlayerP().WpnKungFuMove == 3)
				y += (psp.vel * synctics * -sintable[NORM_ANGLE(psp.ang + psp.PlayerP().FistAng)] >> 6);
			else {
				x -= psp.vel * synctics * sintable[NORM_ANGLE(psp.ang + psp.PlayerP().FistAng)] >> 6;
				y += 3 * (psp.vel * synctics * -sintable[NORM_ANGLE(psp.ang + psp.PlayerP().FistAng)] >> 6);
			}

			setLong(psp, x, y);

			psp.vel += vel_adj * synctics;

			if (psp.y > 440) {
				// if still holding down the fire key - continue swinging
				if (TEST_SYNC_KEY(psp.PlayerP(), SK_SHOOT)) {
					if (FLAG_KEY_PRESSED(psp.PlayerP(), SK_SHOOT)) {
						DoPlayerChooseYell(psp.PlayerP());

						if (RANDOM_RANGE(1000) > 500) {
							psp.ActionState = ps_FistSwing[0];
							psp.PlayerP().WpnKungFuMove = 0;
							pSetState(psp, psp.ActionState);

							psp.x = FIST_XOFF;
							psp.y = FIST_YOFF;
							psp.yorig = psp.y;
							psp.PlayerP().FistAng = FistAngTable[RANDOM_RANGE(FistAngTable.length)];
							psp.ang = 1024;
							psp.vel = vel;
							DoPlayerSpriteThrow(psp.PlayerP());
							return;
						} else {
							pSetState(psp,
									ps_FistSwing[((psp.State.id - psp.ActionState.id) + 2) % ps_FistSwing.length]);
							psp.ActionState = ps_FistSwing[0];
							psp.PlayerP().WpnKungFuMove = 0;
						}

						psp.x = FISTR_XOFF + 100;
						psp.y = FIST_YOFF;
						psp.yorig = psp.y;
						psp.ang = 1024;
						psp.PlayerP().FistAng = FistAngTable[RANDOM_RANGE(FistAngTable.length)];
						psp.vel = vel;
						DoPlayerSpriteThrow(psp.PlayerP());
						return;
					}
				}

				// NOT still holding down the fire key - stop swinging
				pSetState(psp, psp.PresentState);
				psp.x = FIST_XOFF;
				psp.y = FIST_YOFF;
				psp.y += engine.getTile(psp.picndx).getHeight();
				psp.yorig = psp.y;
			}
		}
	};

	private static final Panel_Sprite_Func pFistSlideR = new Panel_Sprite_Func() {
		@Override
		public void invoke(Panel_Sprite psp) {
			short vel_adj;

			int x = castLongX(psp);
			int y = castLongY(psp);

			SpawnFistBlur(psp);
			vel_adj = 68;

			y += psp.vel * synctics * -sintable[NORM_ANGLE(psp.ang + 1024)] >> 6;

			setLong(psp, x, y);
			psp.vel += vel_adj * synctics;
		}
	};

	private static final Panel_Sprite_Func pFistSlideDownR = new Panel_Sprite_Func() {
		@Override
		public void invoke(Panel_Sprite psp) {
			short vel, vel_adj;

			int x = castLongX(psp);
			int y = castLongY(psp);

			SpawnFistBlur(psp);
			vel_adj = 48;
			vel = 3500;

			if (psp.ActionState == ps_Kick[0] || psp.PlayerP().WpnKungFuMove == 3)
				y += (psp.vel * synctics * -sintable[NORM_ANGLE(psp.ang + psp.PlayerP().FistAng)] >> 6);
			else {
				x -= psp.vel * synctics * sintable[NORM_ANGLE(psp.ang + psp.PlayerP().FistAng)] >> 6;
				y += 3 * (psp.vel * synctics * -sintable[NORM_ANGLE(psp.ang + psp.PlayerP().FistAng)] >> 6);
			}

			setLong(psp, x, y);
			psp.vel += vel_adj * synctics;

			if (psp.y > 440) {
				// if still holding down the fire key - continue swinging
				if (TEST_SYNC_KEY(psp.PlayerP(), SK_SHOOT)) {
					if (FLAG_KEY_PRESSED(psp.PlayerP(), SK_SHOOT)) {
						DoPlayerChooseYell(psp.PlayerP());

						if (RANDOM_RANGE(1000) > 500) {
							psp.ActionState = ps_FistSwing[5];
							psp.PlayerP().WpnKungFuMove = 0;

							pSetState(psp, psp.ActionState);

							psp.x = FISTR_XOFF + 100;
							psp.y = FIST_YOFF;
							psp.yorig = psp.y;
							psp.ang = 1024;
							psp.PlayerP().FistAng = FistAngTable[RANDOM_RANGE(FistAngTable.length)];
							psp.vel = vel;
							DoPlayerSpriteThrow(psp.PlayerP());
							return;
						} else {
							pSetState(psp,
									ps_FistSwing[((psp.State.id - psp.ActionState.id) + 2) % ps_FistSwing.length / 2]);
							psp.ActionState = ps_FistSwing[0];
							psp.PlayerP().WpnKungFuMove = 0;
						}

						psp.x = FIST_XOFF;
						psp.y = FIST_YOFF;
						psp.yorig = psp.y;
						psp.PlayerP().FistAng = FistAngTable[RANDOM_RANGE(FistAngTable.length)];
						psp.ang = 1024;
						psp.vel = vel;
						DoPlayerSpriteThrow(psp.PlayerP());
						return;
					}
				}

				// NOT still holding down the fire key - stop swinging
				pSetState(psp, psp.PresentState);
				psp.x = FIST_XOFF;
				psp.y = FIST_YOFF;
				psp.y += engine.getTile(psp.picndx).getHeight();
				psp.yorig = psp.y;
			}
		}
	};

	private static final Panel_State ps_FistSwing_0 = new Panel_State(0, ID_FistSwing0, FIST_PAUSE_TICS, null);
	private static final Panel_State ps_FistSwing_1 = new Panel_State(1, ID_FistSwing1, FIST_SLIDE_TICS,
			/* start slide */ null);
	private static final Panel_State ps_FistSwing_2 = new Panel_State(2, ID_FistSwing2, 0, /* damage */ pFistAttack);
	private static final Panel_State ps_FistSwing_3 = new Panel_State(3, ID_FistSwing2, FIST_MID_SLIDE_TICS,
			/* mid slide */ pFistSlideDown);

	private static final Panel_State ps_FistSwing_4 = new Panel_State(4, ID_FistSwing2, 2,
			/* end slide */ pFistSlideDown);

	private static final Panel_State ps_FistSwing_5 = new Panel_State(5, ID_FistSwing1, FIST_SLIDE_TICS,
			/* start slide */ pFistSlideR);
	private static final Panel_State ps_FistSwing_6 = new Panel_State(6, ID_FistSwing2, 0, /* damage */ pFistAttack);
	private static final Panel_State ps_FistSwing_7 = new Panel_State(7, ID_FistSwing2, FIST_MID_SLIDE_TICS,
			/* mid slide */ pFistSlideDownR);

	private static final Panel_State ps_FistSwing_8 = new Panel_State(8, ID_FistSwing2, 2,
			/* end slide */ pFistSlideDownR);
	private static final Panel_State ps_FistSwing_9 = new Panel_State(9, ID_FistSwing2, 2, /* end slide */ null);

	private static final Panel_State ps_FistSwing[] = { ps_FistSwing_0.setNext(ps_FistSwing_1),
			ps_FistSwing_1.setNext(ps_FistSwing_2), ps_FistSwing_2.setNext(ps_FistSwing_3).setFlags(psf_QuickCall),
			ps_FistSwing_3.setNext(ps_FistSwing_4), ps_FistSwing_4.setNext(),
			ps_FistSwing_5.setNext(ps_FistSwing_6).setFlags(psf_Xflip),
			ps_FistSwing_6.setNext(ps_FistSwing_7).setFlags(psf_QuickCall | psf_Xflip),
			ps_FistSwing_7.setNext(ps_FistSwing_8).setFlags(psf_Xflip), ps_FistSwing_8.setNext().setFlags(psf_Xflip),
			ps_FistSwing_9.setNext(ps_FistSwing_1).setFlags(psf_Xflip), };

	private static final int KICK_PAUSE_TICS = 40;
	private static final int KICK_SLIDE_TICS = 30;
	private static final int KICK_MID_SLIDE_TICS = 20;

	private static final Panel_State ps_Kick_0 = new Panel_State(0, ID_Kick0, KICK_PAUSE_TICS, null);
	private static final Panel_State ps_Kick_1 = new Panel_State(1, ID_Kick1, 0, /* damage */ pFistAttack);
	private static final Panel_State ps_Kick_2 = new Panel_State(2, ID_Kick1, KICK_SLIDE_TICS, /* start slide */ null);
	private static final Panel_State ps_Kick_3 = new Panel_State(3, ID_Kick1, KICK_MID_SLIDE_TICS,
			/* mid slide */pFistSlideDown);

	private static final Panel_State ps_Kick_4 = new Panel_State(4, ID_Kick1, 30, /* end slide */ pFistSlideDown);

	private static final Panel_State ps_Kick_5 = new Panel_State(5, ID_Kick0, KICK_SLIDE_TICS, /* start slide */ null);
	private static final Panel_State ps_Kick_6 = new Panel_State(6, ID_Kick1, 0, /* damage */ pFistAttack);
	private static final Panel_State ps_Kick_7 = new Panel_State(7, ID_Kick1, KICK_MID_SLIDE_TICS,
			/* mid slide */pFistSlideDownR);

	private static final Panel_State ps_Kick_8 = new Panel_State(8, ID_Kick1, 30, /* end slide */ pFistSlideDownR);
	private static final Panel_State ps_Kick_9 = new Panel_State(9, ID_Kick1, 30, /* end slide */ null);

	private static final Panel_State ps_Kick[] = { ps_Kick_0.setNext(ps_Kick_1),
			ps_Kick_1.setNext(ps_Kick_2).setFlags(psf_QuickCall), ps_Kick_2.setNext(ps_Kick_3),
			ps_Kick_3.setNext(ps_Kick_4), ps_Kick_4.setNext(), ps_Kick_5.setNext(ps_Kick_6).setFlags(psf_Xflip),
			ps_Kick_6.setNext(ps_Kick_7).setFlags(psf_QuickCall | psf_Xflip),
			ps_Kick_7.setNext(ps_Kick_8).setFlags(psf_Xflip), ps_Kick_8.setNext().setFlags(psf_Xflip),
			ps_Kick_9.setNext(ps_Kick_1).setFlags(psf_Xflip), };

	public static void InitWeaponFist(PlayerStr pp) {
		Panel_Sprite psp;

		if (Prediction)
			return;

		if (!TEST(pp.WpnFlags, BIT(WPN_FIST)) || TEST(pp.Flags, PF_WEAPON_RETRACT)) {
			pp.WpnFirstType = WPN_SWORD;
			InitWeaponSword(pp);
			return;
		}

		if (pp.Wpn[WPN_FIST] == null) {
			psp = pp.Wpn[WPN_FIST] = pSpawnSprite(pp, ps_PresentFist[0], PRI_MID, FIST_XOFF, FIST_YOFF);
			psp.y += engine.getTile(psp.picndx).getHeight();
		}

		if (pp.CurWpn == pp.Wpn[WPN_FIST]) {
			return;
		}

		PlayerUpdateWeapon(pp, WPN_FIST);

		pp.WpnUziType = 2; // Make uzi's go away!
		RetractCurWpn(pp);

		// Set up the new Weapon variables
		psp = pp.CurWpn = pp.Wpn[WPN_FIST];
		psp.flags |= (PANF_WEAPON_SPRITE);
		psp.ActionState = ps_FistSwing[0];
		psp.RetractState = ps_RetractFist[0];
		psp.PresentState = ps_PresentFist[0];
		psp.RestState = ps_FistRest[0];
		pSetState(psp, psp.PresentState);

		pp.WpnKungFuMove = 0; // Set to default strike

		int rnd_num = RANDOM_P2(1024);
		if (rnd_num > 900)
			PlaySound(DIGI_TAUNTAI2, pp, v3df_follow | v3df_dontpan);
		else if (rnd_num > 800)
			PlaySound(DIGI_PLAYERYELL1, pp, v3df_follow | v3df_dontpan);
		else if (rnd_num > 700)
			PlaySound(DIGI_PLAYERYELL2, pp, v3df_follow | v3df_dontpan);

		FLAG_KEY_RELEASE(psp.PlayerP(), SK_SHOOT);
		FLAG_KEY_RESET(psp.PlayerP(), SK_SHOOT);
	}

	private static void pFistBobSetup(Panel_Sprite psp) {
		if (TEST(psp.flags, PANF_BOB))
			return;

		psp.xorig = psp.x;
		psp.yorig = psp.y;

		psp.sin_amt = FIST_SWAY_AMT;
		psp.sin_ndx = 0;
		psp.bob_height_shift = 3;
	}

	private static final Panel_Sprite_Func FistBlur = new Panel_Sprite_Func() {
		@Override
		public void invoke(Panel_Sprite psp) {
			psp.kill_tics -= synctics;
			if (psp.kill_tics <= 0) {
				pKillSprite(psp);
				return;
			} else if (psp.kill_tics <= 6) {
				psp.flags |= (PANF_TRANS_FLIP);
			}

			psp.shade += 10;
			// change sprites priority
			List.Delete(psp);
			psp.priority--;
			InsertPanelSprite(psp.PlayerP(), psp);
		}
	};

	private static void SpawnFistBlur(Panel_Sprite psp) {
		Panel_Sprite nsp;
		if (psp.PlayerP().FistAng > 200)
			return;

		nsp = pSpawnSprite(psp.PlayerP(), null, PRI_BACK, psp.x, psp.y);

		nsp.flags |= (PANF_WEAPON_SPRITE);
		nsp.xfract = psp.xfract;
		nsp.yfract = psp.yfract;
		nsp.ang = psp.ang;
		nsp.vel = psp.vel;
		nsp.PanelSpriteFunc = FistBlur;
		nsp.kill_tics = 9;
		nsp.shade = (short) (psp.shade + 10);

		nsp.picndx = -1;
		nsp.picnum = psp.picndx;

		if (psp.State.testFlag(psf_Xflip))
			nsp.flags |= (PANF_XFLIP);

		nsp.rotate_ang = psp.rotate_ang;
		nsp.scale = psp.scale;

		nsp.flags |= (PANF_TRANSLUCENT);
	}

	public static void FistSaveable() {
		SaveData(ps_PresentFist);
		SaveData(ps_FistRest);
		SaveData(ps_FistHide);
		SaveData(ps_PresentFist2);
		SaveData(ps_Fist2Rest);
		SaveData(ps_FistSwing);
		SaveData(ps_Kick);
		SaveData(ps_RetractFist);

		SaveData(pFistRest);
		SaveData(pFistPresent);
		SaveData(pFistHide);
		SaveData(pFistRetract);
		SaveData(pFistAttack);
		SaveData(pFistSlideDown);
		SaveData(pFistSlideR);
		SaveData(pFistSlideDownR);
		SaveData(FistBlur);
	}
}
