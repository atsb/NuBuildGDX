package ru.m210projects.Wang.Weapons;

import static ru.m210projects.Wang.Digi.DIGI_BUZZZ;
import static ru.m210projects.Wang.Digi.DIGI_CHOP_CLICK;
import static ru.m210projects.Wang.Digi.DIGI_EVADEFOREVER;
import static ru.m210projects.Wang.Digi.DIGI_GETTINGSTIFF;
import static ru.m210projects.Wang.Digi.DIGI_MRFLY;
import static ru.m210projects.Wang.Digi.DIGI_SEARCHWALL;
import static ru.m210projects.Wang.Digi.DIGI_SHISEISI;
import static ru.m210projects.Wang.Factory.WangNetwork.Prediction;
import static ru.m210projects.Wang.Game.Player;
import static ru.m210projects.Wang.Gameutils.RANDOM_P2;
import static ru.m210projects.Wang.Gameutils.RANDOM_RANGE;
import static ru.m210projects.Wang.Gameutils.synctics;
import static ru.m210projects.Wang.Panel.PANF_WEAPON_SPRITE;
import static ru.m210projects.Wang.Panel.PRI_MID;
import static ru.m210projects.Wang.Panel.pKillSprite;
import static ru.m210projects.Wang.Panel.pSetState;
import static ru.m210projects.Wang.Panel.pSpawnSprite;
import static ru.m210projects.Wang.Panel.pStatePlusOne;
import static ru.m210projects.Wang.Panel.psf_QuickCall;
import static ru.m210projects.Wang.Sound.PlaySound;
import static ru.m210projects.Wang.Sound.PlayerSound;
import static ru.m210projects.Wang.Sound.v3df_dontpan;
import static ru.m210projects.Wang.Sound.v3df_follow;
import static ru.m210projects.Wang.Sound.v3df_none;
import static ru.m210projects.Wang.Type.Saveable.SaveData;
import static ru.m210projects.Wang.Main.*;

import ru.m210projects.Wang.Type.Panel_Sprite;
import ru.m210projects.Wang.Type.Panel_Sprite_Func;
import ru.m210projects.Wang.Type.Panel_State;
import ru.m210projects.Wang.Type.PlayerStr;

public class Chops {

	private static final int CHOPS_YOFF = 200;
	private static final int CHOPS_XOFF = (160 + 20);
	private static final int Chops_REST_RATE = 24;

	private static final int ID_ChopsRest = 2000;
	private static final int ID_ChopsOpen = 2001;
	private static final int ID_ChopsClose = 2002;

	private static final Panel_Sprite_Func pChopsUp = new Panel_Sprite_Func() {
		@Override
		public void invoke(Panel_Sprite psp) {
			psp.y -= 3 * synctics;

			if (psp.y < CHOPS_YOFF) {
				psp.y = CHOPS_YOFF;
				pStatePlusOne(psp);
			}
		}
	};

	private static final Panel_Sprite_Func pChopsWait = new Panel_Sprite_Func() {
		@Override
		public void invoke(Panel_Sprite psp) {
			if (RANDOM_P2(1024) < 10) {
				// random x position
				// do a random attack here
				psp.x = CHOPS_XOFF + (RANDOM_P2(128) - 64);

				PlayerStr pp = Player[psp.PlayerP];
				PlaySound(DIGI_BUZZZ, pp, v3df_none);
				pSetState(psp, psp_ChopsAttack[RANDOM_RANGE(psp_ChopsAttack.length)][0]);
			}
		}
	};

	private static final Panel_State ps_ChopsWait[] = {
			new Panel_State(ID_ChopsRest, Chops_REST_RATE, pChopsWait).setNext() };

	private static final Panel_Sprite_Func pChopsDown = new Panel_Sprite_Func() {
		@Override
		public void invoke(Panel_Sprite psp) {
			psp.y += 3 * synctics;

			if (psp.y > CHOPS_YOFF + 110) {
				psp.y = CHOPS_YOFF + 110;
				pSetState(psp, ps_ChopsWait[0]);
			}
		}
	};

	private static final Panel_Sprite_Func pChopsClick = new Panel_Sprite_Func() {
		@Override
		public void invoke(Panel_Sprite psp) {
			PlayerStr pp = Player[psp.PlayerP];
			PlaySound(DIGI_CHOP_CLICK, pp, v3df_none);

			int rnd_rng = RANDOM_RANGE(1000);
			if (rnd_rng > 950)
				PlayerSound(DIGI_SEARCHWALL, v3df_follow | v3df_dontpan, pp);
			else if (rnd_rng > 900)
				PlayerSound(DIGI_EVADEFOREVER, v3df_follow | v3df_dontpan, pp);
			else if (rnd_rng > 800)
				PlayerSound(DIGI_SHISEISI, v3df_follow | v3df_dontpan, pp);
		}
	};

	private static final Panel_State ps_ChopsAttack_0 = new Panel_State(ID_ChopsRest, Chops_REST_RATE * 3, null);
	private static final Panel_State ps_ChopsAttack_1 = new Panel_State(ID_ChopsRest, Chops_REST_RATE, pChopsUp);
	private static final Panel_State ps_ChopsAttack_2 = new Panel_State(ID_ChopsOpen, Chops_REST_RATE / 3, null);
	private static final Panel_State ps_ChopsAttack_3 = new Panel_State(ID_ChopsClose, 0, pChopsClick);
	private static final Panel_State ps_ChopsAttack_4 = new Panel_State(ID_ChopsClose, Chops_REST_RATE / 3, null);
	private static final Panel_State ps_ChopsAttack_5 = new Panel_State(ID_ChopsClose, Chops_REST_RATE, pChopsDown);

	private static final Panel_State ps_ChopsAttack1[] = { ps_ChopsAttack_0.setNext(ps_ChopsAttack_1),
			ps_ChopsAttack_1.setNext().setPlusOne(ps_ChopsAttack_2), ps_ChopsAttack_2.setNext(ps_ChopsAttack_3),
			ps_ChopsAttack_3.setNext(ps_ChopsAttack_4).setFlags(psf_QuickCall),
			ps_ChopsAttack_4.setNext(ps_ChopsAttack_5), ps_ChopsAttack_5.setNext(), };

	private static final Panel_State ps_ChopsAttack2_0 = new Panel_State(ID_ChopsOpen, Chops_REST_RATE * 3, null);
	private static final Panel_State ps_ChopsAttack2_1 = new Panel_State(ID_ChopsOpen, Chops_REST_RATE, pChopsUp);
	private static final Panel_State ps_ChopsAttack2_2 = new Panel_State(ID_ChopsOpen, 0, pChopsClick);

	private static final Panel_State ps_ChopsAttack2_3 = new Panel_State(ID_ChopsOpen, 8, null);
	private static final Panel_State ps_ChopsAttack2_4 = new Panel_State(ID_ChopsRest, Chops_REST_RATE, null);
	private static final Panel_State ps_ChopsAttack2_5 = new Panel_State(ID_ChopsRest, Chops_REST_RATE, pChopsDown);

	private static final Panel_State ps_ChopsAttack2[] = { ps_ChopsAttack2_0.setNext(ps_ChopsAttack2_1),
			ps_ChopsAttack2_1.setNext().setPlusOne(ps_ChopsAttack2_2),
			ps_ChopsAttack2_2.setNext(ps_ChopsAttack2_3).setFlags(psf_QuickCall),
			ps_ChopsAttack2_3.setNext(ps_ChopsAttack2_4), ps_ChopsAttack2_4.setNext(ps_ChopsAttack2_5),
			ps_ChopsAttack2_5.setNext(), };

	private static final Panel_Sprite_Func pChopsDownSlow = new Panel_Sprite_Func() {
		@Override
		public void invoke(Panel_Sprite psp) {
			psp.y += 1 * synctics;

			if (psp.y > CHOPS_YOFF + 110) {
				psp.y = CHOPS_YOFF + 110;
				pSetState(psp, ps_ChopsWait[0]);
			}
		}
	};

	private static final Panel_State ps_ChopsAttack3_0 = new Panel_State(ID_ChopsOpen, Chops_REST_RATE * 3, null);
	private static final Panel_State ps_ChopsAttack3_1 = new Panel_State(ID_ChopsOpen, Chops_REST_RATE, pChopsUp);

	private static final Panel_State ps_ChopsAttack3_2 = new Panel_State(ID_ChopsRest, 0, null);
	private static final Panel_State ps_ChopsAttack3_3 = new Panel_State(ID_ChopsRest, 0, pChopsClick);

	private static final Panel_State ps_ChopsAttack3_4 = new Panel_State(ID_ChopsRest, Chops_REST_RATE, null);
	private static final Panel_State ps_ChopsAttack3_5 = new Panel_State(ID_ChopsRest, 24, null);
	private static final Panel_State ps_ChopsAttack3_6 = new Panel_State(ID_ChopsOpen, 16, null);

	private static final Panel_State ps_ChopsAttack3_7 = new Panel_State(ID_ChopsRest, 0, pChopsClick);
	private static final Panel_State ps_ChopsAttack3_8 = new Panel_State(ID_ChopsRest, 16, null);
	private static final Panel_State ps_ChopsAttack3_9 = new Panel_State(ID_ChopsOpen, 16, null);

	private static final Panel_State ps_ChopsAttack3_10 = new Panel_State(ID_ChopsOpen, 8, pChopsDownSlow);
	private static final Panel_State ps_ChopsAttack3_11 = new Panel_State(ID_ChopsRest, 10, pChopsDownSlow);
	private static final Panel_State ps_ChopsAttack3_12 = new Panel_State(ID_ChopsRest, 0, pChopsClick);
	private static final Panel_State ps_ChopsAttack3_13 = new Panel_State(ID_ChopsRest, 10, pChopsDownSlow);
	private static final Panel_State ps_ChopsAttack3_14 = new Panel_State(ID_ChopsOpen, 10, pChopsDownSlow);

	private static final Panel_State ps_ChopsAttack3[] = { ps_ChopsAttack3_0.setNext(ps_ChopsAttack3_1),
			ps_ChopsAttack3_1.setNext().setPlusOne(ps_ChopsAttack3_2), ps_ChopsAttack3_2.setNext(ps_ChopsAttack3_3),
			ps_ChopsAttack3_3.setNext(ps_ChopsAttack3_4).setFlags(psf_QuickCall),
			ps_ChopsAttack3_4.setNext(ps_ChopsAttack3_5), ps_ChopsAttack3_5.setNext(ps_ChopsAttack3_6),
			ps_ChopsAttack3_6.setNext(ps_ChopsAttack3_7),
			ps_ChopsAttack3_7.setNext(ps_ChopsAttack3_8).setFlags(psf_QuickCall),
			ps_ChopsAttack3_8.setNext(ps_ChopsAttack3_9), ps_ChopsAttack3_9.setNext(ps_ChopsAttack3_10),
			ps_ChopsAttack3_10.setNext(ps_ChopsAttack3_11), ps_ChopsAttack3_11.setNext(ps_ChopsAttack3_12),
			ps_ChopsAttack3_12.setNext(ps_ChopsAttack3_13).setFlags(psf_QuickCall),
			ps_ChopsAttack3_13.setNext(ps_ChopsAttack3_14), ps_ChopsAttack3_14.setNext(ps_ChopsAttack3_11), };

	private static final Panel_Sprite_Func pChopsShake = new Panel_Sprite_Func() {
		@Override
		public void invoke(Panel_Sprite psp) {
			psp.x += (RANDOM_P2(4 << 8) >> 8) - 2;
			psp.y += (RANDOM_P2(4 << 8) >> 8) - 2;

			if (psp.y < CHOPS_YOFF) {
				psp.y = CHOPS_YOFF;
			}
		}
	};

	private static final Panel_State ps_ChopsAttack4_0 = new Panel_State(ID_ChopsOpen, Chops_REST_RATE * 3, null);
	private static final Panel_State ps_ChopsAttack4_1 = new Panel_State(ID_ChopsOpen, Chops_REST_RATE, pChopsUp);

	private static final Panel_State ps_ChopsAttack4_2 = new Panel_State(ID_ChopsOpen, 0, pChopsClick);

	private static final Panel_State ps_ChopsAttack4_3 = new Panel_State(ID_ChopsOpen, 8, null);

	private static final Panel_State ps_ChopsAttack4_4 = new Panel_State(ID_ChopsRest, Chops_REST_RATE, null);
	private static final Panel_State ps_ChopsAttack4_5 = new Panel_State(ID_ChopsRest, Chops_REST_RATE * 4,
			pChopsShake);
	private static final Panel_State ps_ChopsAttack4_6 = new Panel_State(ID_ChopsRest, Chops_REST_RATE, pChopsDown);

	private static final Panel_State ps_ChopsAttack4[] = { ps_ChopsAttack4_0.setNext(ps_ChopsAttack4_1),
			ps_ChopsAttack4_1.setNext().setPlusOne(ps_ChopsAttack4_2),
			ps_ChopsAttack4_2.setNext(ps_ChopsAttack4_3).setFlags(psf_QuickCall),
			ps_ChopsAttack4_3.setNext(ps_ChopsAttack4_4), ps_ChopsAttack4_4.setNext(ps_ChopsAttack4_5),
			ps_ChopsAttack4_5.setNext(ps_ChopsAttack4_6), ps_ChopsAttack4_6.setNext(), };

	private static final Panel_Sprite_Func pChopsRetract = new Panel_Sprite_Func() {
		@Override
		public void invoke(Panel_Sprite psp) {
			short picnum = psp.picndx;

			psp.y += 6 * synctics;

			if (psp.y >= CHOPS_YOFF + engine.getTile(picnum).getHeight()) {
				PlayerStr pp = Player[psp.PlayerP];
				if (RANDOM_RANGE(1000) > 800)
					PlayerSound(DIGI_GETTINGSTIFF, v3df_follow | v3df_dontpan, pp);
				pp.Chops = null;
				pKillSprite(psp);
			}
		}
	};

	private static final Panel_State ps_ChopsRetract[] = {
			new Panel_State(ID_ChopsRest, Chops_REST_RATE, pChopsRetract).setNext() };

	private static final Panel_State psp_ChopsAttack[][] = { ps_ChopsAttack1, ps_ChopsAttack2, ps_ChopsAttack3,
			ps_ChopsAttack4 };

	public static void InitChops(PlayerStr pp) {
		Panel_Sprite psp;

		if (pp.Chops == null) {
			psp = pp.Chops = pSpawnSprite(pp, ps_ChopsAttack1[0], PRI_MID, CHOPS_XOFF, CHOPS_YOFF);
			psp.y += engine.getTile(psp.picndx).getHeight();
		}

		if (Prediction)
			return;

		// Set up the new Weapon variables
		psp = pp.Chops;

		psp.flags |= (PANF_WEAPON_SPRITE);
		psp.ActionState = ps_ChopsAttack1[0];
		psp.PresentState = ps_ChopsAttack1[0];
		psp.RetractState = ps_ChopsRetract[0];
		psp.RestState = ps_ChopsAttack1[0];

		PlayerStr ppc = Player[psp.PlayerP];
		PlaySound(DIGI_BUZZZ, ppc, v3df_none);

		if (RANDOM_RANGE(1000) > 750)
			PlayerSound(DIGI_MRFLY, v3df_follow | v3df_dontpan, ppc);
	}

	public static void ChopsSetRetract(PlayerStr pp) {
		pSetState(pp.Chops, pp.Chops.RetractState);
	}

	public static void ChopsSaveable() {
		SaveData(ps_ChopsAttack1);
		SaveData(ps_ChopsAttack2);
		SaveData(ps_ChopsAttack3);
		SaveData(ps_ChopsAttack4);
		SaveData(ps_ChopsWait);
		SaveData(ps_ChopsRetract);

		SaveData(pChopsUp);
		SaveData(pChopsWait);
		SaveData(pChopsDown);
		SaveData(pChopsClick);
		SaveData(pChopsDownSlow);
		SaveData(pChopsShake);
		SaveData(pChopsRetract);
	}
}
