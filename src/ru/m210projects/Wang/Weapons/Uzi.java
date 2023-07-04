package ru.m210projects.Wang.Weapons;

import static ru.m210projects.Build.Engine.sintable;
import static ru.m210projects.Wang.Cheats.InfinityAmmo;
import static ru.m210projects.Wang.Digi.DIGI_REMOVECLIP;
import static ru.m210projects.Wang.Digi.DIGI_REPLACECLIP;
import static ru.m210projects.Wang.Digi.DIGI_UZI_UP;
import static ru.m210projects.Wang.Factory.WangNetwork.Prediction;
import static ru.m210projects.Wang.Gameutils.FLAG_KEY_PRESSED;
import static ru.m210projects.Wang.Gameutils.FLAG_KEY_RELEASE;
import static ru.m210projects.Wang.Gameutils.FLAG_KEY_RESET;
import static ru.m210projects.Wang.Gameutils.NORM_ANGLE;
import static ru.m210projects.Wang.Gameutils.PF_PICKED_UP_AN_UZI;
import static ru.m210projects.Wang.Gameutils.PF_TWO_UZI;
import static ru.m210projects.Wang.Gameutils.PF_WEAPON_RETRACT;
import static ru.m210projects.Wang.Gameutils.PLAYER_MOVING;
import static ru.m210projects.Wang.Gameutils.RANDOM_P2;
import static ru.m210projects.Wang.Gameutils.SK_SHOOT;
import static ru.m210projects.Wang.Gameutils.TEST_SYNC_KEY;
import static ru.m210projects.Wang.Gameutils.WPN_UZI;
import static ru.m210projects.Wang.Gameutils.synctics;
import static ru.m210projects.Wang.Main.engine;
import static ru.m210projects.Wang.Panel.DoPlayerChooseYell;
import static ru.m210projects.Wang.Panel.PANF_BOB;
import static ru.m210projects.Wang.Panel.PANF_PRIMARY;
import static ru.m210projects.Wang.Panel.PANF_RELOAD;
import static ru.m210projects.Wang.Panel.PANF_SECONDARY;
import static ru.m210projects.Wang.Panel.PANF_UNHIDE_SHOOT;
import static ru.m210projects.Wang.Panel.PANF_WEAPON_SPRITE;
import static ru.m210projects.Wang.Panel.PANF_XFLIP;
import static ru.m210projects.Wang.Panel.PRI_BACK;
import static ru.m210projects.Wang.Panel.PRI_MID;
import static ru.m210projects.Wang.Panel.PlayerUpdateWeapon;
import static ru.m210projects.Wang.Panel.RetractCurWpn;
import static ru.m210projects.Wang.Panel.UZI_CLIT;
import static ru.m210projects.Wang.Panel.UZI_COPEN;
import static ru.m210projects.Wang.Panel.UZI_CRELOAD;
import static ru.m210projects.Wang.Panel.UziRecoilYadj;
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
import static ru.m210projects.Wang.Panel.psf_Xflip;
import static ru.m210projects.Wang.Panel.setLong;
import static ru.m210projects.Wang.Player.SetVisNorm;
import static ru.m210projects.Wang.Player.pSetVisNorm;
import static ru.m210projects.Wang.Sound.PlaySound;
import static ru.m210projects.Wang.Sound.v3df_dontpan;
import static ru.m210projects.Wang.Sound.v3df_doppler;
import static ru.m210projects.Wang.Sound.v3df_follow;
import static ru.m210projects.Wang.Type.MyTypes.BIT;
import static ru.m210projects.Wang.Type.MyTypes.DIV256;
import static ru.m210projects.Wang.Type.MyTypes.TEST;
import static ru.m210projects.Wang.Type.Saveable.SaveData;
import static ru.m210projects.Wang.Vis.SpawnVis;
import static ru.m210projects.Wang.Weapon.InitUzi;
import static ru.m210projects.Wang.Weapon.SpawnShell;

import ru.m210projects.Wang.Type.Panel_Sprite;
import ru.m210projects.Wang.Type.Panel_Sprite_Func;
import ru.m210projects.Wang.Type.Panel_State;
import ru.m210projects.Wang.Type.PlayerStr;

public class Uzi {

	private static final int CHAMBER_REST = 0;
	private static final int CHAMBER_FIRE = 1;
	private static final int CHAMBER_RELOAD = 2;

	private static final int UZI_CLIP_XOFF = 16;
	private static final int UZI_CLIP_YOFF = (-84);

	private static final int UZI_XOFF = (100);
	private static final int UZI_YOFF = 208;

	private static final int UZI_RELOAD_YOFF = 200;

	private static final int UZI_CHAMBER_XOFF = 32;
	private static final int UZI_CHAMBER_YOFF = -73;

	private static final int UZI_CHAMBERRELOAD_XOFF = 14;
	private static final int UZI_CHAMBERRELOAD_YOFF = -100;

	private static final int PRESENT_UZI_RATE = 6;
	private static final int RELOAD_UZI_RATE = 1;

	private static final int UZI_REST = 2004;
	private static final int UZI_FIRE_0 = 2006;
	private static final int UZI_FIRE_1 = 2008;
	private static final int UZI_EJECT = 2009;
	private static final int UZI_CLIP = 2005;
	private static final int UZI_RELOAD = 2007;

	// RIGHT UZI
	private static final int ID_UziPresent0 = UZI_REST;

	private static final int ID_UziFire0 = UZI_FIRE_0;
	private static final int ID_UziFire1 = UZI_FIRE_1;

	// LEFT UZI
	private static final int ID_Uzi2Present0 = UZI_REST;

	private static final int ID_Uzi2Fire0 = UZI_FIRE_0;
	private static final int ID_Uzi2Fire1 = UZI_FIRE_1;

	// eject
	private static final int ID_UziEject0 = UZI_EJECT;

	// clip
	private static final int ID_UziClip0 = UZI_CLIP;

	// reload
	private static final int ID_UziReload0 = UZI_RELOAD;

	private static final Panel_Sprite_Func pUziHide = new Panel_Sprite_Func() {
		@Override
		public void invoke(Panel_Sprite psp) {
			short picnum = psp.picndx;

			psp.y += 3 * synctics;

			if (psp.y >= 200 + engine.getTile(picnum).getHeight()) {
				psp.y = 200 + engine.getTile(picnum).getHeight();

				if (TEST(psp.flags, PANF_PRIMARY) && psp.PlayerP().WpnUziType != 1 && psp.sibling != null) {
					if (pWeaponUnHideKeys(psp, psp.PresentState))
						pSetState(psp.sibling, psp.sibling.PresentState);
				} else if (!TEST(psp.flags, PANF_SECONDARY)) {
					pWeaponUnHideKeys(psp, psp.PresentState);
				}
			}
		}
	};

	private static final Panel_State ps_Uzi2Hide[] = {
			new Panel_State(ID_Uzi2Present0, PRESENT_UZI_RATE, pUziHide).setNext().setFlags(psf_Xflip) };

	private static final Panel_Sprite_Func pUziPresent = new Panel_Sprite_Func() {
		@Override
		public void invoke(Panel_Sprite psp) {
			if (TEST(psp.PlayerP().Flags, PF_WEAPON_RETRACT))
				return;

			psp.y -= 3 * synctics;

			if (psp.y < UZI_YOFF) {
				psp.flags &= ~(PANF_RELOAD);

				psp.y = UZI_YOFF;
				psp.xorig = psp.x;
				psp.yorig = psp.y;
				pSetState(psp, psp.RestState);
			}
		}
	};

	private static final Panel_Sprite_Func pUziPresentReload = new Panel_Sprite_Func() {
		@Override
		public void invoke(Panel_Sprite psp) {
			if (TEST(psp.PlayerP().Flags, PF_WEAPON_RETRACT))
				return;

			psp.y -= 5 * synctics;

			if (psp.y < UZI_YOFF) {
				psp.y = UZI_YOFF;
				psp.xorig = psp.x;
				psp.yorig = psp.y;
				pSetState(psp, psp.RestState);
			}
		}
	};

	private static final Panel_Sprite_Func pUziRetract = new Panel_Sprite_Func() {
		@Override
		public void invoke(Panel_Sprite psp) {
			// Panel_Sprite sib = psp.sibling;
			short picnum = psp.picndx;

			psp.y += 3 * synctics;

			if (psp.y >= 200 + engine.getTile(picnum).getHeight()) {
				// if in the reload phase and its retracting then get rid of uzi
				// no matter whether it is PRIMARY/SECONDARY/neither.
				if (TEST(psp.flags, PANF_RELOAD)) {
					psp.PlayerP().Flags &= ~(PF_WEAPON_RETRACT);
					psp.PlayerP().Wpn[WPN_UZI] = null;
				} else {
					// NOT reloading here
					if (TEST(psp.flags, PANF_PRIMARY)) {
						// only reset when primary goes off the screen
						psp.PlayerP().Flags &= ~(PF_WEAPON_RETRACT);
						psp.PlayerP().Wpn[WPN_UZI] = null;
					} else if (TEST(psp.flags, PANF_SECONDARY)) {
						// primarily for beginning of reload sequence where seconary
						// is taken off of the screen. Lets the primary know that
						// he is alone.
						if (psp.sibling != null && psp.sibling.sibling == psp)
							psp.sibling.sibling = null;
					} else {
						// only one uzi here is retracting
						psp.PlayerP().Flags &= ~(PF_WEAPON_RETRACT);
						psp.PlayerP().Wpn[WPN_UZI] = null;
					}
				}

				pKillSprite(psp);
			}
		}
	};

	private static final Panel_State ps_PresentUzi2[] = {
			new Panel_State(ID_Uzi2Present0, PRESENT_UZI_RATE, pUziPresent).setNext().setFlags(psf_Xflip) };

	private static final Panel_State ps_RetractUzi2[] = {
			new Panel_State(ID_Uzi2Present0, PRESENT_UZI_RATE, pUziRetract).setNext().setFlags(psf_Xflip) };

	private static final Panel_State ps_UziHide[] = {
			new Panel_State(ID_UziPresent0, PRESENT_UZI_RATE, pUziHide).setNext() };

	private static final Panel_State ps_PresentUzi[] = {
			new Panel_State(ID_UziPresent0, PRESENT_UZI_RATE, pUziPresent).setNext() };

	// present of secondary uzi for reload needs to be faster
	private static final Panel_State ps_PresentUziReload[] = {
			new Panel_State(ID_UziPresent0, RELOAD_UZI_RATE, pUziPresentReload).setNext() };

	private static final Panel_State ps_RetractUzi[] = {
			new Panel_State(ID_UziPresent0, PRESENT_UZI_RATE, pUziRetract).setNext() };

	private static final Panel_Sprite_Func pUziRest = new Panel_Sprite_Func() {
		@Override
		public void invoke(Panel_Sprite psp) {
			boolean shooting;
			boolean force = !!TEST(psp.flags, PANF_UNHIDE_SHOOT);

			// If you have two uzi's, but one didn't come up, spawn it
			if (TEST(psp.PlayerP().Flags, PF_TWO_UZI) && psp.sibling == null) {
				InitWeaponUzi2(psp);
			}

			if (TEST(psp.flags, PANF_PRIMARY) && psp.sibling != null) {
				if (pWeaponHideKeys(psp, ps_UziHide[0])) {
					if (psp.sibling != null) // !JIM! Without this line, will ASSERT if reloading here
						pSetState(psp.sibling, ps_Uzi2Hide[0]);
					return;
				}
			} else if (!TEST(psp.flags, PANF_SECONDARY)) {
				if (pWeaponHideKeys(psp, ps_UziHide[0]))
					return;
			}

			if (TEST(psp.flags, PANF_SECONDARY))
				pUziOverlays(psp, CHAMBER_REST);

			SetVisNorm();

			shooting = TEST_SYNC_KEY(psp.PlayerP(), SK_SHOOT) && FLAG_KEY_PRESSED(psp.PlayerP(), SK_SHOOT);
			shooting |= force;

			pUziBobSetup(psp);
			pWeaponBob(psp, PLAYER_MOVING(psp.PlayerP()) != 0 || shooting);

			if (shooting) {
				if (!WeaponOK(psp.PlayerP()))
					return;

				psp.flags &= ~(PANF_UNHIDE_SHOOT);

				pSetState(psp, psp.ActionState);
			} else
				WeaponOK(psp.PlayerP());
		}
	};

	private static int alternate;

	private static final Panel_Sprite_Func pUziAction = new Panel_Sprite_Func() {
		@Override
		public void invoke(Panel_Sprite psp) {
			boolean shooting;

			shooting = TEST_SYNC_KEY(psp.PlayerP(), SK_SHOOT) && FLAG_KEY_PRESSED(psp.PlayerP(), SK_SHOOT);

			if (shooting) {
				if (TEST(psp.flags, PANF_SECONDARY)) {
					alternate++;
					if (alternate > 6)
						alternate = 0;
					if (alternate <= 3)
						pUziOverlays(psp, CHAMBER_FIRE);
					else
						pUziOverlays(psp, CHAMBER_REST);
				}
				// Only Recoil if shooting
				pUziBobSetup(psp);
				UziRecoilYadj = DIV256(RANDOM_P2(1024)); // global hack for
				// Weapon Bob
				pWeaponBob(psp, PLAYER_MOVING(psp.PlayerP()) != 0 || shooting);
				UziRecoilYadj = 0; // reset my global hack
				if (RANDOM_P2(1024) > 990)
					DoPlayerChooseYell(psp.PlayerP());
			} else {
				if (TEST(psp.flags, PANF_SECONDARY))
					pUziOverlays(psp, CHAMBER_REST);
				pUziBobSetup(psp);
				pWeaponBob(psp, PLAYER_MOVING(psp.PlayerP()) != 0 || shooting);
			}
		}
	};

	private static final Panel_Sprite_Func pUziFire = new Panel_Sprite_Func() {
		@Override
		public void invoke(Panel_Sprite psp) {
			PlayerStr pp = psp.PlayerP();

			if (!WeaponOK(psp.PlayerP()))
				return;

			if (TEST(psp.flags, PANF_SECONDARY) && pp.WpnUziType > 0)
				return;

			InitUzi(psp.PlayerP());
			SpawnUziShell(psp);

			// If its the second Uzi, give the shell back only if it's a reload count to
			// keep #'s even
			if(!InfinityAmmo) {
				if (TEST(psp.flags, PANF_SECONDARY)) {
					if (TEST(pp.Flags, PF_TWO_UZI) && psp.sibling != null) {
						if ((pp.WpnAmmo[WPN_UZI] % 100) == 0)
							pp.WpnAmmo[WPN_UZI]++;
					} else if ((pp.WpnAmmo[WPN_UZI] % 50) == 0)
						pp.WpnAmmo[WPN_UZI]++;
				} else {
					SpawnVis(psp.PlayerP().PlayerSprite, -1, -1, -1, -1, 32);

					if (!WeaponOK(psp.PlayerP()))
						return;

					// Reload if done with clip
					if (TEST(pp.Flags, PF_TWO_UZI) && psp.sibling != null) {
						if ((pp.WpnAmmo[WPN_UZI] % 100) == 0)
							pUziStartReload(psp);
					} else if ((pp.WpnAmmo[WPN_UZI] % 50) == 0) {
						// clip has run out
						pUziStartReload(psp);
					}
				}
			}
		}
	};

	private static final Panel_State ps_FireUzi_0 = new Panel_State(ID_UziPresent0, 3, pUziRest);
	private static final Panel_State ps_FireUzi_1 = new Panel_State(ID_UziFire0, 1, pUziAction);

	private static final Panel_State ps_FireUzi_2 = new Panel_State(ID_UziFire1, 0, pUziFire);
	private static final Panel_State ps_FireUzi_3 = new Panel_State(ID_UziFire1, 4, pUziAction);
	private static final Panel_State ps_FireUzi_4 = new Panel_State(ID_UziFire1, 0, pSetVisNorm);
	private static final Panel_State ps_FireUzi_5 = new Panel_State(ID_UziFire1, 4, pUziAction);
	private static final Panel_State ps_FireUzi_6 = new Panel_State(ID_UziFire1, 0, pUziFire);
	private static final Panel_State ps_FireUzi_7 = new Panel_State(ID_UziFire1, 4, pUziAction);
	private static final Panel_State ps_FireUzi_8 = new Panel_State(ID_UziFire1, 0, pSetVisNorm);

	private static final Panel_State ps_FireUzi_9 = new Panel_State(ID_UziFire0, 4, pUziAction);
	private static final Panel_State ps_FireUzi_10 = new Panel_State(ID_UziFire0, 0, pUziFire);
	private static final Panel_State ps_FireUzi_11 = new Panel_State(ID_UziFire0, 4, pUziAction);
	private static final Panel_State ps_FireUzi_12 = new Panel_State(ID_UziFire0, 4, pUziAction);

	private static final Panel_State ps_FireUzi_13 = new Panel_State(ID_UziFire1, 5, pUziRest);

	private static final Panel_State ps_FireUzi[] = { ps_FireUzi_0.setNext(),
			ps_FireUzi_1.setNext(ps_FireUzi_2).setFlags(psf_ShadeHalf),
			ps_FireUzi_2.setNext(ps_FireUzi_3).setFlags(psf_ShadeNone | psf_QuickCall),
			ps_FireUzi_3.setNext(ps_FireUzi_4).setFlags(psf_ShadeNone),
			ps_FireUzi_4.setNext(ps_FireUzi_5).setFlags(psf_ShadeNone | psf_QuickCall),
			ps_FireUzi_5.setNext(ps_FireUzi_6).setFlags(psf_ShadeNone),
			ps_FireUzi_6.setNext(ps_FireUzi_7).setFlags(psf_ShadeNone | psf_QuickCall),
			ps_FireUzi_7.setNext(ps_FireUzi_8).setFlags(psf_ShadeNone),
			ps_FireUzi_8.setNext(ps_FireUzi_9).setFlags(psf_ShadeNone),
			ps_FireUzi_9.setNext(ps_FireUzi_10).setFlags(psf_ShadeHalf),
			ps_FireUzi_10.setNext(ps_FireUzi_11).setFlags(psf_QuickCall),
			ps_FireUzi_11.setNext(ps_FireUzi_12).setFlags(psf_ShadeHalf),
			ps_FireUzi_12.setNext(ps_FireUzi_13).setFlags(psf_ShadeHalf),
			ps_FireUzi_13.setNext(ps_FireUzi_0).setFlags(psf_ShadeNone | psf_QuickCall), };

	private static final Panel_State ps_FireUzi2_0 = new Panel_State(ID_Uzi2Present0, 3, pUziRest);
	private static final Panel_State ps_FireUzi2_1 = new Panel_State(ID_Uzi2Fire0, 1, pUziAction);

	private static final Panel_State ps_FireUzi2_2 = new Panel_State(ID_Uzi2Fire1, 0, pUziFire);
	private static final Panel_State ps_FireUzi2_3 = new Panel_State(ID_Uzi2Fire1, 4, pUziAction);
	private static final Panel_State ps_FireUzi2_4 = new Panel_State(ID_Uzi2Fire1, 4, pUziAction);
	private static final Panel_State ps_FireUzi2_5 = new Panel_State(ID_Uzi2Fire1, 0, pUziFire);
	private static final Panel_State ps_FireUzi2_6 = new Panel_State(ID_Uzi2Fire1, 4, pUziAction);

	private static final Panel_State ps_FireUzi2_7 = new Panel_State(ID_Uzi2Fire0, 4, pUziAction);
	private static final Panel_State ps_FireUzi2_8 = new Panel_State(ID_Uzi2Fire0, 0, pUziFire);
	private static final Panel_State ps_FireUzi2_9 = new Panel_State(ID_Uzi2Fire0, 4, pUziAction);
	private static final Panel_State ps_FireUzi2_10 = new Panel_State(ID_Uzi2Fire0, 4, pUziAction);

	private static final Panel_State ps_FireUzi2_11 = new Panel_State(ID_Uzi2Fire1, 5, pUziRest);

	private static final Panel_State ps_FireUzi2[] = { ps_FireUzi2_0.setNext().setFlags(psf_Xflip),
			ps_FireUzi2_1.setNext(ps_FireUzi2_2).setFlags(psf_Xflip),
			ps_FireUzi2_2.setNext(ps_FireUzi2_3).setFlags(psf_ShadeNone | psf_QuickCall | psf_Xflip),
			ps_FireUzi2_3.setNext(ps_FireUzi2_4).setFlags(psf_ShadeNone | psf_Xflip),
			ps_FireUzi2_4.setNext(ps_FireUzi2_5).setFlags(psf_ShadeNone | psf_Xflip),
			ps_FireUzi2_5.setNext(ps_FireUzi2_6).setFlags(psf_ShadeNone | psf_QuickCall | psf_Xflip),
			ps_FireUzi2_6.setNext(ps_FireUzi2_7).setFlags(psf_ShadeNone | psf_Xflip),
			ps_FireUzi2_7.setNext(ps_FireUzi2_8).setFlags(psf_ShadeHalf | psf_Xflip),
			ps_FireUzi2_8.setNext(ps_FireUzi2_9).setFlags(psf_ShadeHalf | psf_QuickCall | psf_Xflip),
			ps_FireUzi2_9.setNext(ps_FireUzi2_10).setFlags(psf_ShadeHalf | psf_Xflip),
			ps_FireUzi2_10.setNext(ps_FireUzi2_11).setFlags(psf_ShadeHalf | psf_Xflip),
			ps_FireUzi2_11.setNext(ps_FireUzi2_0).setFlags(psf_QuickCall), };

	private static final Panel_Sprite_Func pUziEjectDown = new Panel_Sprite_Func() {
		@Override
		public void invoke(Panel_Sprite gun) {
			gun.y += 5 * synctics;

			if (gun.y > 260) {
				gun.y = 260;
				pStatePlusOne(gun);
			}
		}
	};

	private static final Panel_Sprite_Func pUziEjectUp = new Panel_Sprite_Func() {
		@Override
		public void invoke(Panel_Sprite gun) {
			pUziOverlays(gun, CHAMBER_RELOAD);

			gun.y -= 5 * synctics;

			if (gun.y < UZI_RELOAD_YOFF) {
				gun.y = UZI_RELOAD_YOFF;
				pStatePlusOne(gun);
			}
		}
	};

	private static final Panel_Sprite_Func pUziClip = new Panel_Sprite_Func() {
		@Override
		public void invoke(Panel_Sprite oclip) {
			int x = castLongX(oclip);
			int y = castLongY(oclip);

			int ox = x;
			int oy = y;

			int nx = oclip.vel * synctics * sintable[NORM_ANGLE(oclip.ang + 512)] >> 6;
			int ny = oclip.vel * synctics * -sintable[oclip.ang] >> 6;

			oclip.vel += 16 * synctics;

			x += nx;
			y += ny;
			setLong(oclip, x, y);

			if (oclip.y > UZI_RELOAD_YOFF) {
				Panel_Sprite gun = oclip.sibling;

				// as synctics gets bigger, oclip.x can be way off
				// when clip goes off the screen - recalc oclip.x from scratch
				// so it will end up the same for all synctic values
				for (x = ox, y = oy; oclip.y < UZI_RELOAD_YOFF;) {
					x += oclip.vel * sintable[NORM_ANGLE(oclip.ang + 512)] >> 6;
					y += oclip.vel * -sintable[oclip.ang] >> 6;
					setLong(oclip, x, y);
				}

				oclip.y = UZI_RELOAD_YOFF;

				gun.vel = 800;
				gun.ang = NORM_ANGLE(oclip.ang + 1024);

				pSpawnUziReload(oclip);
				pKillSprite(oclip);
			}
		}
	};

	private static final Panel_State ps_UziClip[] = {
			new Panel_State(ID_UziClip0, RELOAD_UZI_RATE, pUziClip).setNext() };

	private static final Panel_Sprite_Func pUziReload = new Panel_Sprite_Func() {
		@Override
		public void invoke(Panel_Sprite nclip) {
			long nx, ny;

			int x = castLongX(nclip);
			int y = castLongY(nclip);

			Panel_Sprite gun = nclip.sibling;
			int xgun = castLongX(gun);
			int ygun = castLongY(gun);

			nx = nclip.vel * synctics * sintable[NORM_ANGLE(nclip.ang + 512)] >> 6;
			ny = nclip.vel * synctics * -sintable[nclip.ang] >> 6;

			nclip.vel += 14 * synctics;

			x += nx;
			y += ny;

			setLong(nclip, x, y);

			nx = gun.vel * synctics * sintable[NORM_ANGLE(gun.ang + 512)] >> 6;
			ny = gun.vel * synctics * -sintable[gun.ang] >> 6;

			xgun -= nx;
			ygun -= ny;

			setLong(gun, xgun, ygun);

			if (TEST(nclip.flags, PANF_XFLIP)) {
				if (nclip.x < gun.x) {
					PlaySound(DIGI_REPLACECLIP, nclip.PlayerP(), v3df_follow | v3df_dontpan | v3df_doppler);

					nclip.x = gun.x - UZI_CLIP_XOFF;
					nclip.y = gun.y + UZI_CLIP_YOFF;
					nclip.vel = 680;
					nclip.ang = NORM_ANGLE(nclip.ang - 128 - 64);
					// go to retract phase
					pSetState(nclip, ps_UziReload[1]);
				}
			} else {
				if (nclip.x > gun.x) {
					PlaySound(DIGI_REPLACECLIP, nclip.PlayerP(), v3df_follow | v3df_dontpan | v3df_doppler);

					nclip.x = gun.x + UZI_CLIP_XOFF;
					nclip.y = gun.y + UZI_CLIP_YOFF;
					nclip.vel = 680;
					nclip.ang = NORM_ANGLE(nclip.ang + 128 + 64);
					// go to retract phase
					pSetState(nclip, ps_UziReload[1]);
				}
			}
		}
	};

	private static final Panel_Sprite_Func pUziDoneReload = new Panel_Sprite_Func() {
		@Override
		public void invoke(Panel_Sprite psp) {
			PlayerStr pp = psp.PlayerP();

			if (TEST(psp.flags, PANF_PRIMARY) && pp.WpnUziType == 3) {
				// if 2 uzi's and the first one has been reloaded
				// kill the first one and make the second one the CurWeapon
				// Set uzi's back to previous state
				Panel_Sprite newsp;

				if (pp.WpnUziType > 2)
					pp.WpnUziType -= 3;

				newsp = InitWeaponUziSecondaryReload(psp);
				pp.Wpn[WPN_UZI] = newsp;
				pp.CurWpn = newsp;
				pp.CurWpn.sibling = null;

				pKillSprite(psp);
				return;
			} else {
				// Reset everything

				// Set uzi's back to previous state
				if (pp.WpnUziType > 2)
					pp.WpnUziType -= 3;

				// reset uzi variable
				pp.Wpn[WPN_UZI] = null;
				pp.CurWpn = null;

				// kill uzi eject sequence for good
				pKillSprite(psp);

				// give the uzi back
				InitWeaponUzi(pp);
			}
		}
	};

	private static final Panel_State ps_UziDoneReload[] = {
			new Panel_State(ID_UziEject0, RELOAD_UZI_RATE, pUziDoneReload).setNext() };

	private static final Panel_Sprite_Func pUziReloadRetract = new Panel_Sprite_Func() {
		@Override
		public void invoke(Panel_Sprite nclip) {
			long nx, ny;

			int x = castLongX(nclip);
			int y = castLongY(nclip);

			Panel_Sprite gun = nclip.sibling;
			int xgun = castLongX(gun);
			int ygun = castLongY(gun);

			nx = nclip.vel * synctics * sintable[NORM_ANGLE(nclip.ang + 512)] >> 6;
			ny = nclip.vel * synctics * -sintable[nclip.ang] >> 6;

			nclip.vel += 18 * synctics;

			x -= nx;
			y -= ny;

			setLong(nclip, x, y);

			xgun -= nx;
			ygun -= ny;

			setLong(gun, xgun, ygun);

			if (gun.y > UZI_RELOAD_YOFF + engine.getTile(gun.picndx).getHeight()) {
				pSetState(gun, ps_UziDoneReload[0]);
				pKillSprite(nclip);
			}
		}
	};

	private static final Panel_State ps_UziReload[] = {
			new Panel_State(ID_UziReload0, RELOAD_UZI_RATE, pUziReload).setNext(),
			new Panel_State(ID_UziReload0, RELOAD_UZI_RATE, pUziReloadRetract).setNext(), };

	private static final Panel_Sprite_Func pSpawnUziClip = new Panel_Sprite_Func() {
		@Override
		public void invoke(Panel_Sprite gun) {
			Panel_Sprite newsp;

			PlaySound(DIGI_REMOVECLIP, gun.PlayerP(), v3df_follow | v3df_dontpan | v3df_doppler | v3df_follow);

			if (TEST(gun.flags, PANF_XFLIP)) {
				newsp = pSpawnSprite(gun.PlayerP(), ps_UziClip[0], PRI_BACK, gun.x - UZI_CLIP_XOFF,
						gun.y + UZI_CLIP_YOFF);
				newsp.flags |= (PANF_XFLIP);
				newsp.ang = NORM_ANGLE(1024 + 256 + 22);
				newsp.ang = NORM_ANGLE(newsp.ang + 512);
			} else {
				newsp = pSpawnSprite(gun.PlayerP(), ps_UziClip[0], PRI_BACK, gun.x + UZI_CLIP_XOFF,
						gun.y + UZI_CLIP_YOFF);
				newsp.ang = NORM_ANGLE(1024 + 256 - 22);
			}

			newsp.vel = 1050;
			newsp.flags |= (PANF_WEAPON_SPRITE);

			// carry Eject sprite with clip
			newsp.sibling = gun;
		}
	};

	private static final Panel_State ps_UziEject_0 = new Panel_State(ID_UziPresent0, 1, null);
	private static final Panel_State ps_UziEject_1 = new Panel_State(ID_UziPresent0, RELOAD_UZI_RATE, pUziEjectDown);
	private static final Panel_State ps_UziEject_2 = new Panel_State(ID_UziEject0, RELOAD_UZI_RATE, pUziEjectUp);
	private static final Panel_State ps_UziEject_3 = new Panel_State(ID_UziEject0, 1, null);
	private static final Panel_State ps_UziEject_4 = new Panel_State(ID_UziEject0, RELOAD_UZI_RATE, pSpawnUziClip);
	private static final Panel_State ps_UziEject_5 = new Panel_State(ID_UziEject0, RELOAD_UZI_RATE, null);

	private static final Panel_State ps_UziEject[] = { ps_UziEject_0.setNext(ps_UziEject_1),
			ps_UziEject_1.setNext().setPlusOne(ps_UziEject_2), ps_UziEject_2.setNext().setPlusOne(ps_UziEject_3),
			ps_UziEject_3.setNext(ps_UziEject_4), ps_UziEject_4.setNext(ps_UziEject_5).setFlags(psf_QuickCall),
			ps_UziEject_5.setNext() };

	public static void InitWeaponUzi(PlayerStr pp) {
		Panel_Sprite psp;

		if (Prediction)
			return;

		pp.WeaponType = WPN_UZI;

		// make sure you have the uzi, uzi ammo, and not retracting another
		// weapon
		if (!TEST(pp.WpnFlags, BIT(WPN_UZI)) || TEST(pp.Flags, PF_WEAPON_RETRACT))
			return;

		// if players uzi is null
		if (pp.Wpn[WPN_UZI] == null) {
			psp = pp.Wpn[WPN_UZI] = pSpawnSprite(pp, ps_PresentUzi[0], PRI_MID, 160 + UZI_XOFF, UZI_YOFF);
			psp.y += engine.getTile(psp.picndx).getHeight();
		}

		// if Current weapon is uzi
		if (pp.CurWpn == pp.Wpn[WPN_UZI]) {
			// Retracting other uzi?
			if (pp.CurWpn.sibling != null && pp.WpnUziType == 1) {
				RetractCurWpn(pp);
			} else
			// Is player toggling between one and two uzi's?
			if (pp.CurWpn.sibling != null && TEST(pp.Wpn[WPN_UZI].flags, PANF_PRIMARY) && pp.WpnUziType == 0) {
				if (!TEST(pp.CurWpn.flags, PANF_RELOAD))
					InitWeaponUzi2(pp.Wpn[WPN_UZI]);
			}

			// if actually picked an uzi up and don't currently have double uzi
			if (TEST(pp.Flags, PF_PICKED_UP_AN_UZI) && !TEST(pp.Wpn[WPN_UZI].flags, PANF_PRIMARY)) {
				pp.Flags &= ~(PF_PICKED_UP_AN_UZI);

				if (!TEST(pp.CurWpn.flags, PANF_RELOAD))
					InitWeaponUzi2(pp.Wpn[WPN_UZI]);
			}
			return;
		} else {
			pp.Flags &= ~(PF_PICKED_UP_AN_UZI);
		}

		PlayerUpdateWeapon(pp, WPN_UZI);

		RetractCurWpn(pp);

		// Set up the new Weapon variables
		psp = pp.CurWpn = pp.Wpn[WPN_UZI];
		psp.flags |= (PANF_WEAPON_SPRITE);
		psp.ActionState = ps_FireUzi[1];
		psp.RetractState = ps_RetractUzi[0];
		psp.PresentState = ps_PresentUzi[0];
		psp.RestState = ps_FireUzi[0];
		pSetState(psp, psp.PresentState);

		// power up
		// NOTE: PRIMARY is ONLY set when there is a powerup
		if (TEST(pp.Flags, PF_TWO_UZI)) {
			InitWeaponUzi2(psp);
		}

		PlaySound(DIGI_UZI_UP, pp, v3df_follow);

		FLAG_KEY_RELEASE(psp.PlayerP(), SK_SHOOT);
		FLAG_KEY_RESET(psp.PlayerP(), SK_SHOOT);
	}

	private static Panel_Sprite InitWeaponUzi2(Panel_Sprite uzi_orig) {
		Panel_Sprite newsp = null;
		PlayerStr pp = uzi_orig.PlayerP();

		// There is already a second uzi, or it's retracting
		if (pp.WpnUziType == 1 || pp.CurWpn.sibling != null || TEST(pp.Flags, PF_WEAPON_RETRACT))
			return (null);

		// NOTE: PRIMARY is ONLY set when there is a powerup
		uzi_orig.flags |= (PANF_PRIMARY);

		// Spawning a 2nd uzi, set weapon mode
		pp.WpnUziType = 0; // 0 is up, 1 is retract

		newsp = pSpawnSprite(pp, ps_PresentUzi2[0], PRI_MID, 160 - UZI_XOFF, UZI_YOFF);
		uzi_orig.sibling = newsp;

		newsp.y += engine.getTile(newsp.picndx).getHeight();

		// Set up the new Weapon variables
		newsp.flags |= (PANF_WEAPON_SPRITE);
		newsp.ActionState = ps_FireUzi2[1];
		newsp.RetractState = ps_RetractUzi2[0];
		newsp.PresentState = ps_PresentUzi2[0];
		newsp.RestState = ps_FireUzi2[0];
		pSetState(newsp, newsp.PresentState);

		newsp.sibling = uzi_orig;
		newsp.flags |= (PANF_SECONDARY);
		pUziOverlays(newsp, CHAMBER_REST);

		return (newsp);
	}

	private static void pUziOverlays(Panel_Sprite psp, int mode) {
		if (!TEST(psp.flags, PANF_SECONDARY))
			return;

		if (psp.over[0].xoff == -1) {
			psp.over[0].xoff = UZI_CHAMBER_XOFF;
			psp.over[0].yoff = UZI_CHAMBER_YOFF;
		}

		switch (mode) {
		case 0: // At rest
			psp.over[0].pic = UZI_COPEN;
			break;
		case 1: // Firing
			psp.over[0].pic = UZI_CLIT;
			break;
		case 2: // Reloading
			psp.over[0].pic = UZI_CRELOAD;
			psp.over[0].xoff = UZI_CHAMBERRELOAD_XOFF;
			psp.over[0].yoff = UZI_CHAMBERRELOAD_YOFF;
			break;
		}
	}

	public static void pUziBobSetup(Panel_Sprite psp) {
		if (TEST(psp.flags, PANF_BOB))
			return;

		psp.xorig = psp.x;
		psp.yorig = psp.y;

		psp.sin_amt = 12;
		psp.sin_ndx = 0;
		psp.bob_height_shift = 3;
	}

	private static void pUziStartReload(Panel_Sprite psp) {
		SetVisNorm();

		// Set uzi's to reload state
		if (psp.PlayerP().WpnUziType < 3)
			psp.PlayerP().WpnUziType += 3;

		// Uzi #1 reload - starting from a full up position
		pSetState(psp, ps_UziEject[0]);

		psp.flags |= (PANF_RELOAD);

		if (TEST(psp.flags, PANF_PRIMARY) && psp.sibling != null) {
			// this is going to KILL Uzi #2 !!!
			pSetState(psp.sibling, psp.sibling.RetractState);
		}
	}

	private static void SpawnUziShell(Panel_Sprite psp) {
		PlayerStr pp = psp.PlayerP();
		if (psp.State != null && psp.State.testFlag(psf_Xflip)) {
			// LEFT side
			pp.UziShellLeftAlt = !pp.UziShellLeftAlt;
			if (pp.UziShellLeftAlt)
				SpawnShell(pp.PlayerSprite, -3);
		} else {
			// RIGHT side
			pp.UziShellRightAlt = !pp.UziShellRightAlt;
			if (pp.UziShellRightAlt)
				SpawnShell(pp.PlayerSprite, -2);
		}
	}

	private static void pSpawnUziReload(Panel_Sprite oclip) {
		Panel_Sprite nclip;

		nclip = pSpawnSprite(oclip.PlayerP(), ps_UziReload[0], PRI_BACK, oclip.x, UZI_RELOAD_YOFF);
		nclip.flags |= (PANF_WEAPON_SPRITE);

		if (TEST(oclip.flags, PANF_XFLIP))
			nclip.flags |= (PANF_XFLIP);

		// move Reload in oposite direction of clip
		nclip.ang = NORM_ANGLE(oclip.ang + 1024);
		nclip.vel = 900;

		// move gun sprite from clip to reload
		nclip.sibling = oclip.sibling;
	}

	private static Panel_Sprite InitWeaponUziSecondaryReload(Panel_Sprite uzi_orig) {
		Panel_Sprite newsp;
		PlayerStr pp = uzi_orig.PlayerP();

		newsp = pSpawnSprite(pp, ps_PresentUzi[0], PRI_MID, 160 - UZI_XOFF, UZI_YOFF);
		newsp.y += engine.getTile(newsp.picndx).getHeight();

		newsp.flags |= (PANF_XFLIP);

		// Set up the new Weapon variables
		newsp.flags |= (PANF_WEAPON_SPRITE);
		newsp.ActionState = ps_UziEject[0];
		newsp.RetractState = ps_RetractUzi[0];
		newsp.PresentState = ps_PresentUzi[0];
		newsp.RestState = ps_UziEject[0];
		// pSetState(new, new.PresentState);
		pSetState(newsp, ps_PresentUziReload[0]);

		newsp.sibling = uzi_orig;
		newsp.flags |= (PANF_SECONDARY | PANF_RELOAD);

		return (newsp);
	}

	public static void UziSaveable() {
		SaveData(ps_FireUzi);
		SaveData(ps_UziHide);
		SaveData(ps_PresentUzi);
		SaveData(ps_PresentUziReload);
		SaveData(ps_RetractUzi);
		SaveData(ps_FireUzi2);
		SaveData(ps_PresentUzi2);
		SaveData(ps_Uzi2Hide);
		SaveData(ps_RetractUzi2);
		SaveData(ps_UziEject);
		SaveData(ps_UziClip);
		SaveData(ps_UziReload);
		SaveData(ps_UziDoneReload);

		SaveData(pUziHide);
		SaveData(pUziPresent);
		SaveData(pUziPresentReload);
		SaveData(pUziRetract);
		SaveData(pUziRest);
		SaveData(pUziAction);
		SaveData(pUziFire);
		SaveData(pUziEjectDown);
		SaveData(pUziEjectUp);
		SaveData(pUziClip);
		SaveData(pUziReload);
		SaveData(pUziDoneReload);
		SaveData(pUziReloadRetract);
		SaveData(pSpawnUziClip);
	}
}
