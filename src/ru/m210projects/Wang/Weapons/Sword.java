package ru.m210projects.Wang.Weapons;

import static ru.m210projects.Build.Engine.headspritestat;
import static ru.m210projects.Build.Engine.nextspritestat;
import static ru.m210projects.Build.Engine.pHitInfo;
import static ru.m210projects.Build.Engine.sector;
import static ru.m210projects.Build.Engine.sintable;
import static ru.m210projects.Build.Engine.sprite;
import static ru.m210projects.Build.Engine.wall;
import static ru.m210projects.Build.Net.Mmulti.myconnectindex;
import static ru.m210projects.Wang.Break.HitBreakSprite;
import static ru.m210projects.Wang.Break.HitBreakWall;
import static ru.m210projects.Wang.Digi.DIGI_ILIKESWORD;
import static ru.m210projects.Wang.Digi.DIGI_PLAYERYELL1;
import static ru.m210projects.Wang.Digi.DIGI_PLAYERYELL2;
import static ru.m210projects.Wang.Digi.DIGI_SWORDCLANK;
import static ru.m210projects.Wang.Digi.DIGI_SWORDSWOOSH;
import static ru.m210projects.Wang.Digi.DIGI_SWORD_UP;
import static ru.m210projects.Wang.Digi.DIGI_TAUNTAI2;
import static ru.m210projects.Wang.Digi.DIGI_TRASHLID;
import static ru.m210projects.Wang.Factory.WangNetwork.Prediction;
import static ru.m210projects.Wang.Game.Distance;
import static ru.m210projects.Wang.Game.Player;
import static ru.m210projects.Wang.Gameutils.CEILING_STAT_PLAX;
import static ru.m210projects.Wang.Gameutils.CLIPMASK_MISSILE;
import static ru.m210projects.Wang.Gameutils.CLIPMASK_PLAYER;
import static ru.m210projects.Wang.Gameutils.CSTAT_SPRITE_INVISIBLE;
import static ru.m210projects.Wang.Gameutils.CSTAT_SPRITE_TRANSLUCENT;
import static ru.m210projects.Wang.Gameutils.CSTAT_SPRITE_WALL;
import static ru.m210projects.Wang.Gameutils.CSTAT_SPRITE_YCENTER;
import static ru.m210projects.Wang.Gameutils.FLAG_KEY_PRESSED;
import static ru.m210projects.Wang.Gameutils.FLAG_KEY_RELEASE;
import static ru.m210projects.Wang.Gameutils.FLAG_KEY_RESET;
import static ru.m210projects.Wang.Gameutils.FindDistance3D;
import static ru.m210projects.Wang.Gameutils.MOVEx;
import static ru.m210projects.Wang.Gameutils.MOVEy;
import static ru.m210projects.Wang.Gameutils.NORM_ANGLE;
import static ru.m210projects.Wang.Gameutils.PF_DIVING;
import static ru.m210projects.Wang.Gameutils.PF_WEAPON_DOWN;
import static ru.m210projects.Wang.Gameutils.PF_WEAPON_RETRACT;
import static ru.m210projects.Wang.Gameutils.PLAYER_FACING_RANGE;
import static ru.m210projects.Wang.Gameutils.PLAYER_MOVING;
import static ru.m210projects.Wang.Gameutils.RANDOM_P2;
import static ru.m210projects.Wang.Gameutils.RANDOM_RANGE;
import static ru.m210projects.Wang.Gameutils.SEC;
import static ru.m210projects.Wang.Gameutils.SK_SHOOT;
import static ru.m210projects.Wang.Gameutils.SPRITEp_MID;
import static ru.m210projects.Wang.Gameutils.SPRX_BREAKABLE;
import static ru.m210projects.Wang.Gameutils.SPRX_PLAYER_OR_ENEMY;
import static ru.m210projects.Wang.Gameutils.STD_RANDOM_RANGE;
import static ru.m210projects.Wang.Gameutils.TEST_SYNC_KEY;
import static ru.m210projects.Wang.Gameutils.WPN_FIST;
import static ru.m210projects.Wang.Gameutils.WPN_STAR;
import static ru.m210projects.Wang.Gameutils.WPN_SWORD;
import static ru.m210projects.Wang.Gameutils.Z;
import static ru.m210projects.Wang.Gameutils.pUser;
import static ru.m210projects.Wang.Gameutils.synctics;
import static ru.m210projects.Wang.JTags.LUMINOUS;
import static ru.m210projects.Wang.Main.engine;
import static ru.m210projects.Wang.MiscActr.s_TrashCanPain;
import static ru.m210projects.Wang.Names.PACHINKO1;
import static ru.m210projects.Wang.Names.PACHINKO2;
import static ru.m210projects.Wang.Names.PACHINKO3;
import static ru.m210projects.Wang.Names.PACHINKO4;
import static ru.m210projects.Wang.Names.STAT_MISSILE;
import static ru.m210projects.Wang.Names.TRASHCAN;
import static ru.m210projects.Wang.Names.UZI_SMOKE;
import static ru.m210projects.Wang.Names.UZI_SPARK;
import static ru.m210projects.Wang.Names.ZILLA_RUN_R0;
import static ru.m210projects.Wang.Palette.PALETTE_DEFAULT;
import static ru.m210projects.Wang.Panel.DoPlayerChooseYell;
import static ru.m210projects.Wang.Panel.InsertPanelSprite;
import static ru.m210projects.Wang.Panel.PANF_BOB;
import static ru.m210projects.Wang.Panel.PANF_DEATH_HIDE;
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
import static ru.m210projects.Wang.Panel.pStatePlusOne;
import static ru.m210projects.Wang.Panel.pWeaponBob;
import static ru.m210projects.Wang.Panel.pWeaponHideKeys;
import static ru.m210projects.Wang.Panel.pWeaponUnHideKeys;
import static ru.m210projects.Wang.Panel.psf_QuickCall;
import static ru.m210projects.Wang.Panel.psf_Xflip;
import static ru.m210projects.Wang.Panel.setLong;
import static ru.m210projects.Wang.Player.DoPlayerSpriteThrow;
import static ru.m210projects.Wang.Rooms.FAFcansee;
import static ru.m210projects.Wang.Rooms.FAFhitscan;
import static ru.m210projects.Wang.Sector.ShootableSwitch;
import static ru.m210projects.Wang.Sound.PlaySound;
import static ru.m210projects.Wang.Sound.PlayerSound;
import static ru.m210projects.Wang.Sound.sndCoords;
import static ru.m210projects.Wang.Sound.v3df_dontpan;
import static ru.m210projects.Wang.Sound.v3df_doppler;
import static ru.m210projects.Wang.Sound.v3df_follow;
import static ru.m210projects.Wang.Sound.v3df_none;
import static ru.m210projects.Wang.Sprites.ChangeState;
import static ru.m210projects.Wang.Sprites.SetOwner;
import static ru.m210projects.Wang.Sprites.SpawnSprite;
import static ru.m210projects.Wang.Sprites.SpriteOverlapZ;
import static ru.m210projects.Wang.Sprites.move_missile;
import static ru.m210projects.Wang.Tags.TAG_SPRITE_HIT_MATCH;
import static ru.m210projects.Wang.Tags.TAG_WALL_BREAK;
import static ru.m210projects.Wang.Type.MyTypes.BIT;
import static ru.m210projects.Wang.Type.MyTypes.TEST;
import static ru.m210projects.Wang.Type.Saveable.SaveData;
import static ru.m210projects.Wang.Weapon.CLOSE_RANGE_DIST_FUDGE;
import static ru.m210projects.Wang.Weapon.DoDamage;
import static ru.m210projects.Wang.Weapon.HitscanSpriteAdjust;
import static ru.m210projects.Wang.Weapon.MissileHitMatch;
import static ru.m210projects.Wang.Weapon.SpawnBubble;
import static ru.m210projects.Wang.Weapon.StatDamageList;
import static ru.m210projects.Wang.Weapon.s_UziSmoke;
import static ru.m210projects.Wang.Weapon.s_UziSpark;

import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Wang.Type.List;
import ru.m210projects.Wang.Type.Panel_Sprite;
import ru.m210projects.Wang.Type.Panel_Sprite_Func;
import ru.m210projects.Wang.Type.Panel_State;
import ru.m210projects.Wang.Type.PlayerStr;
import ru.m210projects.Wang.Type.USER;

public class Sword {

	public static final int SWORD_REST = 2080;
	public static final int SWORD_SWING0 = 2081;
	public static final int SWORD_SWING1 = 2082;
	public static final int SWORD_SWING2 = 2083;

	public static final int BLOODYSWORD_REST = 4090;
	public static final int BLOODYSWORD_SWING0 = 4091;
	public static final int BLOODYSWORD_SWING1 = 4092;
	public static final int BLOODYSWORD_SWING2 = 4093;

	private static final int SWORD_PAUSE_TICS = 10;
	private static final int SWORD_SLIDE_TICS = 10;
	private static final int SWORD_MID_SLIDE_TICS = 14;

	private static final int Sword_BEAT_RATE = 24;
	private static final int SWORD_SWAY_AMT = 12;

	// left swing
	private static final int SWORD_XOFF = (320 + SWORD_SWAY_AMT);
	private static final int SWORD_YOFF = 200;

	// right swing
	private static final int SWORDR_XOFF = (0 - 80);

	private static short SwordAng = 0;

	private static final short SwordAngTable[] = { 82, 168, 256 + 64 };
	private static short dangs[] = { -256, -128, 0, 128, 256 };

	private static final Panel_Sprite_Func pSwordPresent = new Panel_Sprite_Func() {
		@Override
		public void invoke(Panel_Sprite psp) {
			if (TEST(psp.PlayerP().Flags, PF_WEAPON_RETRACT))
				return;

			psp.y -= 3 * synctics;

			if (psp.y < SWORD_YOFF) {
				psp.y = SWORD_YOFF;
				psp.yorig = psp.y;
				pSetState(psp, psp.RestState);
			}
		}
	};

	private static final Panel_Sprite_Func pSwordSlide = new Panel_Sprite_Func() {
		@Override
		public void invoke(Panel_Sprite psp) {
			short vel_adj = 24;

			int x = castLongX(psp);
			int y = castLongY(psp);

			SpawnSwordBlur(psp);

			x += psp.vel * synctics * (long) sintable[NORM_ANGLE(psp.ang + 512)] >> 6;
			y += psp.vel * synctics * (long) -sintable[psp.ang] >> 6;

			setLong(psp, x, y);

			psp.vel += vel_adj * synctics;
		}
	};

	private static final Panel_Sprite_Func pSwordAttack = new Panel_Sprite_Func() {
		@Override
		public void invoke(Panel_Sprite psp) {
			InitSwordAttack(psp.PlayerP());
		}
	};

	private static final Panel_Sprite_Func pSwordSlideDown = new Panel_Sprite_Func() {
		@Override
		public void invoke(Panel_Sprite psp) {
			short vel, vel_adj;

			int x = castLongX(psp);
			int y = castLongY(psp);

			SpawnSwordBlur(psp);
			vel_adj = 20;
			vel = 2500;

			x += psp.vel * synctics
					* (long) sintable[NORM_ANGLE(SwordAng + psp.ang + psp.PlayerP().SwordAng + 512)] >> 6;
			y += psp.vel * synctics * (long) -sintable[NORM_ANGLE(SwordAng + psp.ang + psp.PlayerP().SwordAng)] >> 6;

			setLong(psp, x, y);

			psp.vel += vel_adj * synctics;

			if (psp.x < -40) {
				// if still holding down the fire key - continue swinging
				if (TEST_SYNC_KEY(psp.PlayerP(), SK_SHOOT)) {
					if (FLAG_KEY_PRESSED(psp.PlayerP(), SK_SHOOT)) {
						DoPlayerChooseYell(psp.PlayerP());
						// continue to next state to swing right
						pStatePlusOne(psp);
						psp.x = SWORDR_XOFF;
						psp.y = SWORD_YOFF;
						psp.yorig = psp.y;
						psp.ang = 1024;
						psp.PlayerP().SwordAng = SwordAngTable[RANDOM_RANGE(SwordAngTable.length)];
						psp.vel = vel;
						DoPlayerSpriteThrow(psp.PlayerP());
						return;
					}
				}

				// NOT still holding down the fire key - stop swinging
				pSetState(psp, psp.PresentState);
				psp.x = SWORD_XOFF;
				psp.y = SWORD_YOFF;
				psp.y += engine.getTile(psp.picndx).getHeight();
				psp.yorig = psp.y;
			}
		}
	};

	private static final Panel_Sprite_Func pSwordSlideR = new Panel_Sprite_Func() {
		@Override
		public void invoke(Panel_Sprite psp) {
			SpawnSwordBlur(psp);
			short vel_adj = 24;

			int x = castLongX(psp);
			int y = castLongY(psp);

			x += psp.vel * synctics * (long) sintable[NORM_ANGLE(psp.ang + 1024 + 512)] >> 6;
			y += psp.vel * synctics * (long) -sintable[NORM_ANGLE(psp.ang + 1024)] >> 6;
			setLong(psp, x, y);
			psp.vel += vel_adj * synctics;
		}
	};

	private static final Panel_Sprite_Func pSwordSlideDownR = new Panel_Sprite_Func() {
		@Override
		public void invoke(Panel_Sprite psp) {
			short vel, vel_adj;

			int x = castLongX(psp);
			int y = castLongY(psp);

			SpawnSwordBlur(psp);
			vel_adj = 24;
			vel = 2500;

			x += psp.vel * synctics
					* (long) sintable[NORM_ANGLE(SwordAng + psp.ang - psp.PlayerP().SwordAng + 1024 + 512)] >> 6;
			y += psp.vel * synctics
					* (long) -sintable[NORM_ANGLE(SwordAng + psp.ang - psp.PlayerP().SwordAng + 1024)] >> 6;
			setLong(psp, x, y);
			psp.vel += vel_adj * synctics;

			if (psp.x > 350) {
				// if still holding down the fire key - continue swinging
				if (TEST_SYNC_KEY(psp.PlayerP(), SK_SHOOT)) {
					if (FLAG_KEY_PRESSED(psp.PlayerP(), SK_SHOOT)) {
						DoPlayerChooseYell(psp.PlayerP());
						// back to action state
						pStatePlusOne(psp);
						psp.x = SWORD_XOFF + 80;
						psp.y = SWORD_YOFF;
						psp.yorig = psp.y;
						psp.PlayerP().SwordAng = SwordAngTable[RANDOM_RANGE(SwordAngTable.length)];
						psp.ang = 1024;
						psp.vel = vel;
						DoPlayerSpriteThrow(psp.PlayerP());
						return;
					}
				}

				// NOT still holding down the fire key - stop swinging
				pSetState(psp, psp.PresentState);
				psp.x = SWORD_XOFF;
				psp.y = SWORD_YOFF;
				psp.y += engine.getTile(psp.picndx).getHeight();
				psp.yorig = psp.y;
			}
		}
	};

	private static final Panel_Sprite_Func pSwordRest = new Panel_Sprite_Func() {
		@Override
		public void invoke(Panel_Sprite psp) {

			boolean force = !!TEST(psp.flags, PANF_UNHIDE_SHOOT);

			if (pWeaponHideKeys(psp, ps_SwordHide[0]))
				return;

			psp.yorig += synctics;

			if (psp.yorig > SWORD_YOFF) {
				psp.yorig = SWORD_YOFF;
			}

			psp.y = psp.yorig;

			pSwordBobSetup(psp);
			pWeaponBob(psp, PLAYER_MOVING(psp.PlayerP()) != 0);

			force = !!TEST(psp.flags, PANF_UNHIDE_SHOOT);

			if (TEST_SYNC_KEY(psp.PlayerP(), SK_SHOOT) || force) {
				if (FLAG_KEY_PRESSED(psp.PlayerP(), SK_SHOOT) || force) {
					psp.flags &= ~(PANF_UNHIDE_SHOOT);

					pSetState(psp, psp.ActionState);

					psp.vel = 2500;

					psp.ang = 1024;
					psp.PlayerP().SwordAng = SwordAngTable[RANDOM_RANGE(SwordAngTable.length)];
					DoPlayerSpriteThrow(psp.PlayerP());
				}
			}
		}
	};

	private static final Panel_Sprite_Func pSwordHide = new Panel_Sprite_Func() {
		@Override
		public void invoke(Panel_Sprite psp) {
			short picnum = psp.picndx;

			psp.y += 3 * synctics;

			if (psp.y >= SWORD_YOFF + engine.getTile(picnum).getHeight()) {
				psp.y = SWORD_YOFF + engine.getTile(picnum).getHeight();
				psp.x = SWORD_XOFF;

				pWeaponUnHideKeys(psp, psp.PresentState);
			}
		}
	};

	private static final Panel_Sprite_Func pSwordRetract = new Panel_Sprite_Func() {
		@Override
		public void invoke(Panel_Sprite psp) {
			short picnum = psp.picndx;

			psp.y += 3 * synctics;

			if (psp.y >= SWORD_YOFF + engine.getTile(picnum).getHeight()) {
				psp.PlayerP().Flags &= ~(PF_WEAPON_RETRACT);
				psp.PlayerP().Wpn[WPN_SWORD] = null;
				pKillSprite(psp);
			}
		}
	};

	private static final Panel_Sprite_Func SwordBlur = new Panel_Sprite_Func() {
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

	private static final Panel_State ps_PresentSword[] = {
			new Panel_State(SWORD_REST, Sword_BEAT_RATE, pSwordPresent).setNext() };

	private static final Panel_State ID_SwordSwing0 = new Panel_State(SWORD_SWING0, SWORD_PAUSE_TICS, null);
	private static final Panel_State ID_SwordSwing1 = new Panel_State(SWORD_SWING1, SWORD_SLIDE_TICS,
			/* start slide */ pSwordSlide);
	private static final Panel_State ID_SwordSwing2 = new Panel_State(SWORD_SWING1, 0, /* damage */ pSwordAttack);
	private static final Panel_State ID_SwordSwing3 = new Panel_State(SWORD_SWING2, SWORD_MID_SLIDE_TICS,
			/* mid slide */ pSwordSlideDown);

	private static final Panel_State ID_SwordSwing4 = new Panel_State(SWORD_SWING2, 99,
			/* end slide */ pSwordSlideDown);

	private static final Panel_State ID_SwordSwing5 = new Panel_State(SWORD_SWING1, SWORD_SLIDE_TICS,
			/* start slide */ pSwordSlideR);
	private static final Panel_State ID_SwordSwing6 = new Panel_State(SWORD_SWING2, 0, /* damage */ pSwordAttack);
	private static final Panel_State ID_SwordSwing7 = new Panel_State(SWORD_SWING2, SWORD_MID_SLIDE_TICS,
			/* mid slide */ pSwordSlideDownR);

	private static final Panel_State ID_SwordSwing8 = new Panel_State(SWORD_SWING2, 99,
			/* end slide */ pSwordSlideDownR);
	private static final Panel_State ID_SwordSwing9 = new Panel_State(SWORD_SWING2, 2, /* end slide */ null);

	private static final Panel_State ps_SwordSwing[] = { ID_SwordSwing0.setNext(ID_SwordSwing1),
			ID_SwordSwing1.setNext(ID_SwordSwing2), ID_SwordSwing2.setNext(ID_SwordSwing3).setFlags(psf_QuickCall),
			ID_SwordSwing3.setNext(ID_SwordSwing4),

			ID_SwordSwing4.setNext().setPlusOne(ID_SwordSwing5),

			ID_SwordSwing5.setNext(ID_SwordSwing6).setFlags(psf_Xflip),
			ID_SwordSwing6.setNext(ID_SwordSwing7).setFlags(psf_QuickCall | psf_Xflip),
			ID_SwordSwing7.setNext(ID_SwordSwing8).setFlags(psf_Xflip),
			ID_SwordSwing8.setNext().setPlusOne(ID_SwordSwing9).setFlags(psf_Xflip),
			ID_SwordSwing9.setNext(ID_SwordSwing1).setPlusOne(ID_SwordSwing0).setFlags(psf_Xflip) };

	private static final Panel_State ps_SwordRest[] = {
			new Panel_State(SWORD_REST, Sword_BEAT_RATE, pSwordRest).setNext() };

	private static final Panel_State ps_SwordHide[] = {
			new Panel_State(SWORD_REST, Sword_BEAT_RATE, pSwordHide).setNext() };

	private static final Panel_State ps_RetractSword[] = {
			new Panel_State(SWORD_REST, Sword_BEAT_RATE, pSwordRetract).setNext() };

	public static void InitWeaponSword(PlayerStr pp) {
		Panel_Sprite psp;
		short rnd_num;

		if (Prediction)
			return;

		if (!TEST(pp.WpnFlags, BIT(WPN_SWORD)) || TEST(pp.Flags, PF_WEAPON_RETRACT))
			return;

		// needed for death sequence when the SWORD was your weapon when you died
		if (pp.Wpn[WPN_SWORD] != null && TEST(pp.Wpn[WPN_SWORD].flags, PANF_DEATH_HIDE)) {
			pp.Wpn[WPN_SWORD].flags &= ~(PANF_DEATH_HIDE);
			pp.Flags &= ~(PF_WEAPON_RETRACT | PF_WEAPON_DOWN);
			pSetState(pp.CurWpn, pp.CurWpn.PresentState);
			return;
		}

		if (pp.Wpn[WPN_SWORD] == null) {
			psp = pp.Wpn[WPN_SWORD] = pSpawnSprite(pp, ps_PresentSword[0], PRI_MID, SWORD_XOFF, SWORD_YOFF);
			psp.y += engine.getTile(psp.picndx).getHeight();
		}

		if (pp.CurWpn == pp.Wpn[WPN_SWORD]) {
			return;
		}

		PlayerUpdateWeapon(pp, WPN_SWORD);

		pp.WpnUziType = 2; // Make uzi's go away!
		RetractCurWpn(pp);

		// Set up the new Weapon variables
		psp = pp.CurWpn = pp.Wpn[WPN_SWORD];
		psp.flags |= (PANF_WEAPON_SPRITE);
		psp.ActionState = ps_SwordSwing[0];
		psp.RetractState = ps_RetractSword[0];
		psp.PresentState = ps_PresentSword[0];
		psp.RestState = ps_SwordRest[0];
		pSetState(psp, psp.PresentState);

		PlaySound(DIGI_SWORD_UP, pp, v3df_follow | v3df_dontpan);

		if (pp == Player[myconnectindex]) {
			rnd_num = (short) STD_RANDOM_RANGE(1024);
			if (rnd_num > 900)
				PlaySound(DIGI_TAUNTAI2, pp, v3df_follow | v3df_dontpan);
			else if (rnd_num > 800)
				PlaySound(DIGI_PLAYERYELL1, pp, v3df_follow | v3df_dontpan);
			else if (rnd_num > 700)
				PlaySound(DIGI_PLAYERYELL2, pp, v3df_follow | v3df_dontpan);
			else if (rnd_num > 600)
				PlayerSound(DIGI_ILIKESWORD, v3df_follow | v3df_dontpan, pp);
		}

		FLAG_KEY_RELEASE(psp.PlayerP(), SK_SHOOT);
		FLAG_KEY_RESET(psp.PlayerP(), SK_SHOOT);
	}

	private static int InitSwordAttack(PlayerStr pp) {
		USER u = pUser[pp.PlayerSprite], tu;
		SPRITE sp = null;
		short i, nexti, stat;
		long dist;
		short reach, face;

		PlaySound(DIGI_SWORDSWOOSH, pp, v3df_dontpan | v3df_doppler);

		if (TEST(pp.Flags, PF_DIVING)) {
			short bubble;
			SPRITE bp;
			int nx, ny;
			short random_amt;

			for (i = 0; i < dangs.length; i++) {
				if (RANDOM_RANGE(1000) < 500)
					continue; // Don't spawn bubbles every time
				bubble = (short) SpawnBubble(pp.PlayerSprite);
				if (bubble >= 0) {
					bp = sprite[bubble];

					bp.ang = pp.getAnglei();

					random_amt = (short) ((RANDOM_P2(32 << 8) >> 8) - 16);

					// back it up a bit to get it out of your face
					nx = MOVEx((1024 + 256) * 3, NORM_ANGLE(bp.ang + dangs[i] + random_amt));
					ny = MOVEy((1024 + 256) * 3, NORM_ANGLE(bp.ang + dangs[i] + random_amt));

					move_missile(bubble, nx, ny, 0, u.ceiling_dist, u.floor_dist, CLIPMASK_PLAYER, 1);
				}
			}
		}

		for (stat = 0; stat < StatDamageList.length; stat++) {
			for (i = headspritestat[StatDamageList[stat]]; i != -1; i = nexti) {
				nexti = nextspritestat[i];
				sp = sprite[i];

				if (pUser[i].PlayerP == pp.pnum)
					break;

				if (!TEST(sp.extra, SPRX_PLAYER_OR_ENEMY))
					continue;

				dist = Distance(pp.posx, pp.posy, sp.x, sp.y);

				reach = 1000; // !JIM! was 800
				face = 200;
				SPRITE psp = pp.getSprite();

				if (dist < CLOSE_RANGE_DIST_FUDGE(sp, psp, reach) && PLAYER_FACING_RANGE(pp, sp, face)) {
					if (SpriteOverlapZ(pp.PlayerSprite, i, Z(20))) {
						if (FAFcansee(sp.x, sp.y, SPRITEp_MID(sp), sp.sectnum, psp.x, psp.y, SPRITEp_MID(psp),
								psp.sectnum))
							DoDamage(i, pp.PlayerSprite);
					}
				}
			}
		}

		// all this is to break glass
		{
			short hitsect, hitwall, hitsprite;
			int hitx, hity, hitz;

			short daang = pp.getAnglei();
			int daz = ((100 - pp.getHorizi()) * 2000) + (RANDOM_RANGE(24000) - 12000);

			FAFhitscan(pp.posx, pp.posy, pp.posz, pp.cursectnum, // Start position
					sintable[NORM_ANGLE(daang + 512)], // X vector of 3D ang
					sintable[NORM_ANGLE(daang)], // Y vector of 3D ang
					daz, // Z vector of 3D ang
					pHitInfo, CLIPMASK_MISSILE);

			hitsect = pHitInfo.hitsect;
			hitwall = pHitInfo.hitwall;
			hitsprite = pHitInfo.hitsprite;
			hitx = pHitInfo.hitx;
			hity = pHitInfo.hity;
			hitz = pHitInfo.hitz;

			if (hitsect < 0)
				return (0);

			if (FindDistance3D(pp.posx - hitx, pp.posy - hity, (pp.posz - hitz) >> 4) < 700) {

				if (hitsprite >= 0) {
					SPRITE hsp = sprite[hitsprite];
					tu = pUser[hitsprite];

					if (tu != null) {
						switch (tu.ID) {
						case ZILLA_RUN_R0:
							SpawnSwordSparks(pp, hitsect, -1, hitx, hity, hitz, daang);
							PlaySound(DIGI_SWORDCLANK, sndCoords.set(hitx, hity, hitz), v3df_none);
							break;
						case TRASHCAN:
							if (tu.WaitTics <= 0) {
								tu.WaitTics = (short) SEC(2);
								ChangeState(hitsprite, s_TrashCanPain[0]);
							}
							SpawnSwordSparks(pp, hitsect, -1, hitx, hity, hitz, daang);
							PlaySound(DIGI_SWORDCLANK, sndCoords.set(hitx, hity, hitz), v3df_none);
							PlaySound(DIGI_TRASHLID, sp, v3df_none);
							break;
						case PACHINKO1:
						case PACHINKO2:
						case PACHINKO3:
						case PACHINKO4:
						case 623:
							SpawnSwordSparks(pp, hitsect, -1, hitx, hity, hitz, daang);
							PlaySound(DIGI_SWORDCLANK, sndCoords.set(hitx, hity, hitz), v3df_none);
							break;
						}
					}

					if (sprite[hitsprite].lotag == TAG_SPRITE_HIT_MATCH) {
						if (MissileHitMatch(-1, WPN_STAR, hitsprite))
							return (0);
					}

					if (TEST(hsp.extra, SPRX_BREAKABLE)) {
						HitBreakSprite(hitsprite, 0);
					}

					// hit a switch?
					if (TEST(hsp.cstat, CSTAT_SPRITE_WALL) && (hsp.lotag != 0 || hsp.hitag != 0)) {
						ShootableSwitch(hitsprite, -1);
					}

				}

				if (hitwall >= 0) {
					if (wall[hitwall].nextsector >= 0) {
						if (TEST(sector[wall[hitwall].nextsector].ceilingstat, CEILING_STAT_PLAX)) {
							if (hitz < sector[wall[hitwall].nextsector].ceilingz) {
								return (0);
							}
						}
					}

					if (wall[hitwall].lotag == TAG_WALL_BREAK) {
						HitBreakWall(hitwall, hitx, hity, hitz, daang, u.ID);
					}
					// hit non breakable wall - do sound and puff
					else {
						SpawnSwordSparks(pp, hitsect, hitwall, hitx, hity, hitz, daang);
						PlaySound(DIGI_SWORDCLANK, sndCoords.set(hitx, hity, hitz), v3df_none);
					}
				}
			}
		}

		return (0);
	}

	public static int SpawnSwordSparks(PlayerStr pp, int hitsect, int hitwall, int hitx, int hity, int hitz,
			short hitang) {
		USER u = pUser[pp.PlayerSprite];
		short j;
		SPRITE wp;
		USER wu;

		j = (short) SpawnSprite(STAT_MISSILE, UZI_SMOKE, s_UziSmoke[0], hitsect, hitx, hity, hitz, hitang, 0);
		wp = sprite[j];
		wp.shade = -40;
		wp.xrepeat = wp.yrepeat = 20;
		SetOwner(pp.PlayerSprite, j);
		wp.cstat |= (CSTAT_SPRITE_TRANSLUCENT | CSTAT_SPRITE_YCENTER);
		wp.hitag = LUMINOUS; // Always full brightness

		// Sprite starts out with center exactly on wall.
		// This moves it back enough to see it at all angles.

		wp.clipdist = 32 >> 2;

		if (hitwall != -1)
			HitscanSpriteAdjust(j, hitwall);

		j = (short) SpawnSprite(STAT_MISSILE, UZI_SPARK, s_UziSpark[0], hitsect, hitx, hity, hitz, hitang, 0);
		wp = sprite[j];
		wu = pUser[j];
		wp.shade = -40;
		wp.xrepeat = wp.yrepeat = 20;
		SetOwner(pp.PlayerSprite, j);
		wu.spal = (byte) (wp.pal = PALETTE_DEFAULT);
		wp.cstat |= (CSTAT_SPRITE_YCENTER);
		if (u.WeaponNum == WPN_FIST)
			wp.cstat |= (CSTAT_SPRITE_INVISIBLE);

		wp.clipdist = 32 >> 2;

		if (hitwall != -1)
			HitscanSpriteAdjust(j, hitwall);

		return j;
	}

	private static void pSwordBobSetup(Panel_Sprite psp) {
		if (TEST(psp.flags, PANF_BOB))
			return;

		psp.xorig = psp.x;
		psp.yorig = psp.y;

		psp.sin_amt = SWORD_SWAY_AMT;
		psp.sin_ndx = 0;
		psp.bob_height_shift = 3;
	}

	private static void SpawnSwordBlur(Panel_Sprite psp) {
		Panel_Sprite nsp;

		if (psp.PlayerP().SwordAng > 200)
			return;

		nsp = pSpawnSprite(psp.PlayerP(), null, PRI_BACK, psp.x, psp.y);

		nsp.flags |= (PANF_WEAPON_SPRITE);
		nsp.xfract = psp.xfract;
		nsp.yfract = psp.yfract;
		nsp.ang = psp.ang;
		nsp.vel = psp.vel;
		nsp.PanelSpriteFunc = SwordBlur;
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

	public static void SwordSaveable() {
		SaveData(ps_PresentSword);
		SaveData(ps_SwordRest);
		SaveData(ps_SwordHide);
		SaveData(ps_SwordSwing);
		SaveData(ps_RetractSword);

		SaveData(pSwordPresent);
		SaveData(pSwordSlide);
		SaveData(pSwordAttack);
		SaveData(pSwordSlideDown);
		SaveData(pSwordSlideR);
		SaveData(pSwordSlideDownR);
		SaveData(pSwordRest);
		SaveData(pSwordHide);
		SaveData(pSwordRetract);
		SaveData(SwordBlur);
	}
}
