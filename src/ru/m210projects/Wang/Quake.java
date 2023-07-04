package ru.m210projects.Wang;

import static ru.m210projects.Build.Engine.headspritestat;
import static ru.m210projects.Build.Engine.nextspritestat;
import static ru.m210projects.Build.Engine.sprite;
import static ru.m210projects.Build.Pragmas.divscale;
import static ru.m210projects.Build.Pragmas.mulscale;
import static ru.m210projects.Wang.Digi.DIGI_ERUPTION;
import static ru.m210projects.Wang.Gameutils.FindDistance3D;
import static ru.m210projects.Wang.Gameutils.RANDOM_RANGE;
import static ru.m210projects.Wang.Gameutils.RESET_BOOL3;
import static ru.m210projects.Wang.Gameutils.SP_TAG13;
import static ru.m210projects.Wang.Gameutils.SP_TAG2;
import static ru.m210projects.Wang.Gameutils.SP_TAG3;
import static ru.m210projects.Wang.Gameutils.SP_TAG4;
import static ru.m210projects.Wang.Gameutils.SP_TAG5;
import static ru.m210projects.Wang.Gameutils.SP_TAG6;
import static ru.m210projects.Wang.Gameutils.SP_TAG7;
import static ru.m210projects.Wang.Gameutils.SP_TAG8;
import static ru.m210projects.Wang.Gameutils.*;
import static ru.m210projects.Wang.Gameutils.STD_RANDOM_RANGE;
import static ru.m210projects.Wang.Gameutils.TEST_BOOL1;
import static ru.m210projects.Wang.Gameutils.TEST_BOOL2;
import static ru.m210projects.Wang.Gameutils.TEST_BOOL3;
import static ru.m210projects.Wang.Gameutils.Z;
import static ru.m210projects.Wang.Gameutils.synctics;
import static ru.m210projects.Wang.Main.game;
import static ru.m210projects.Wang.Names.STAT_QUAKE_ON;
import static ru.m210projects.Wang.Names.STAT_QUAKE_SPOT;
import static ru.m210projects.Wang.Rooms.COVERinsertsprite;
import static ru.m210projects.Wang.Sound.DeleteNoSoundOwner;
import static ru.m210projects.Wang.Sound.PlaySound;
import static ru.m210projects.Wang.Sound.Set3DSoundOwner;
import static ru.m210projects.Wang.Sound.v3df_dontpan;
import static ru.m210projects.Wang.Sound.v3df_follow;
import static ru.m210projects.Wang.Sprites.KillSprite;
import static ru.m210projects.Wang.Sprites.change_sprite_stat;

import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Wang.Type.LONGp;
import ru.m210projects.Wang.Type.PlayerStr;

public class Quake {

	public static final int QUAKE_Match(SPRITE sp) {
		return (SP_TAG2(sp));
	}

	public static final int QUAKE_Zamt(SPRITE sp) {
		return (SP_TAG3(sp));
	}

	public static final int QUAKE_Radius(SPRITE sp) {
		return (SP_TAG4(sp));
	}

	public static final int QUAKE_Duration(SPRITE sp) {
		return (SP_TAG5(sp));
	}

	public static final int QUAKE_WaitSecs(SPRITE sp) {
		return (SP_TAG6(sp));
	}

	public static final int QUAKE_AngAmt(SPRITE sp) {
		return (SP_TAG7(sp));
	}

	public static final int QUAKE_PosAmt(SPRITE sp) {
		return (SP_TAG8(sp));
	}

	public static final int QUAKE_RandomTest(SPRITE sp) {
		return (SP_TAG9(sp));
	}

	public static final int QUAKE_WaitTics(SPRITE sp) {
		return (SP_TAG13(sp));
	}

	public static final boolean QUAKE_TestDontTaper(SPRITE sp) {
		return (TEST_BOOL1(sp));
	}

	public static final boolean QUAKE_KillAfterQuake(SPRITE sp) {
		return (TEST_BOOL2(sp));
	}

	// only for timed quakes
	public static final boolean QUAKE_WaitForTrigger(SPRITE sp) {
		return (TEST_BOOL3(sp));
	}

	public static int CopyQuakeSpotToOn(SPRITE sp) {
		int newsp = COVERinsertsprite(sp.sectnum, STAT_QUAKE_SPOT);
		SPRITE np = sprite[newsp];

		np.set(sp);

		np.cstat = 0;
		np.extra = 0;
		np.owner = -1;

		change_sprite_stat(newsp, STAT_QUAKE_ON);

		np.xvel *= 120; // QUAKE_Duration(np) *= 120;

		return (newsp);
	}

	public static void DoQuakeMatch(int match) {
		short i, nexti;
		SPRITE sp;

		for (i = headspritestat[STAT_QUAKE_SPOT]; i != -1; i = nexti) {
			nexti = nextspritestat[i];
			sp = sprite[i];

			if (QUAKE_Match(sp) == match) {
				if (QUAKE_WaitTics(sp) > 0) {
					// its not waiting any more
					RESET_BOOL3(sp);
				} else {
					CopyQuakeSpotToOn(sp);
					if (QUAKE_KillAfterQuake(sp)) {
						KillSprite(i);
						continue;
					}
				}
			}
		}
	}

	public static void ProcessQuakeOn() {
		short i, nexti;
		SPRITE sp;

		for (i = headspritestat[STAT_QUAKE_ON]; i != -1; i = nexti) {
			nexti = nextspritestat[i];
			sp = sprite[i];

			// get rid of quake when timer runs out
			sp.xvel -= synctics * 4; // QUAKE_Duration(sp) -= synctics*4;
			if (QUAKE_Duration(sp) < 0) {
				KillSprite(i);
				continue;
			}
		}
	}

	public static void ProcessQuakeSpot() {
		short i, nexti;
		SPRITE sp;
		long rand_test;

		// check timed quakes and random quakes
		for (i = headspritestat[STAT_QUAKE_SPOT]; i != -1; i = nexti) {
			nexti = nextspritestat[i];
			sp = sprite[i];

			// not a timed quake
			if (QUAKE_WaitSecs(sp) == 0)
				continue;

			// don't process unless triggered
			if (QUAKE_WaitForTrigger(sp))
				continue;

			// spawn a quake if time is up
			SET_SP_TAG13(sp, (QUAKE_WaitTics(sp)-4*synctics));
			if (QUAKE_WaitTics(sp) < 0) {
				// reset timer - add in Duration of quake
				SET_SP_TAG13(sp, (((QUAKE_WaitSecs(sp)*10) + QUAKE_Duration(sp)) * 120));

				// spawn a quake if condition is met
				rand_test = QUAKE_RandomTest(sp);
				// wrong - all quakes need to happen at the same time on all computerssg
				// if (!rand_test || (rand_test && STD_RANDOM_RANGE(128) < rand_test))
				if (rand_test == 0 || (rand_test != 0 && RANDOM_RANGE(128) < rand_test)) {
					CopyQuakeSpotToOn(sp);
					// kill quake spot if needed
					if (QUAKE_KillAfterQuake(sp)) {
						DeleteNoSoundOwner(i);
						KillSprite(i);
						continue;
					}
				}
			}
		}
	}

	public static void QuakeViewChange(PlayerStr pp, LONGp z_diff, LONGp x_diff, LONGp y_diff, LONGp ang_diff) {
		short i, nexti;
		SPRITE sp;
		SPRITE save_sp = null;
		int dist, save_dist = 999999;
		int dist_diff, scale_value;
		int ang_amt;
		int radius;
		int pos_amt;

		z_diff.value = 0;
		x_diff.value = 0;
		y_diff.value = 0;
		ang_diff.value = 0;

		if (game.gPaused)
			return;

		// find the closest quake - should be a strength value
		for (i = headspritestat[STAT_QUAKE_ON]; i != -1; i = nexti) {
			nexti = nextspritestat[i];
			sp = sprite[i];

			dist = FindDistance3D(pp.posx - sp.x, pp.posy - sp.y, (pp.posz - sp.z) >> 4);

			// shake whole level
			if (QUAKE_TestDontTaper(sp)) {
				save_dist = dist;
				save_sp = sp;
				break;
			}

			if (dist < save_dist) {
				save_dist = dist;
				save_sp = sp;
			}
		}

		if (save_sp == null)
			return;
		else
			sp = save_sp;

		radius = QUAKE_Radius(sp) * 8;
		if (save_dist > radius)
			return;

		z_diff.value = Z(STD_RANDOM_RANGE(QUAKE_Zamt(sp)) - (QUAKE_Zamt(sp) / 2));

		ang_amt = QUAKE_AngAmt(sp) * 4;
		ang_diff.value = STD_RANDOM_RANGE(ang_amt) - (ang_amt / 2);

		pos_amt = QUAKE_PosAmt(sp) * 4;
		x_diff.value = STD_RANDOM_RANGE(pos_amt) - (pos_amt / 2);
		y_diff.value = STD_RANDOM_RANGE(pos_amt) - (pos_amt / 2);

		if (!QUAKE_TestDontTaper(sp)) {
			// scale values from epicenter
			dist_diff = radius - save_dist;
			scale_value = divscale(dist_diff, radius, 16);

			z_diff.value = mulscale(z_diff.value, scale_value, 16);
			ang_diff.value = mulscale(ang_diff.value, scale_value, 16);
			x_diff.value = mulscale(x_diff.value, scale_value, 16);
			y_diff.value = mulscale(y_diff.value, scale_value, 16);
		}
	}

	public static int SpawnQuake(short sectnum, int x, int y, int z, int tics, int amt, int radius) {
		int SpriteNum;
		SPRITE sp;

		SpriteNum = COVERinsertsprite(sectnum, STAT_QUAKE_ON);
		sp = sprite[SpriteNum];

		sp.x = x;
		sp.y = y;
		sp.z = z;
		sp.cstat = 0;
		sp.owner = -1;
		sp.extra = 0;

		sp.lotag = -1;
		sp.clipdist = amt;
		sp.ang = (short) (radius / 8);
		sp.xvel = (short) tics;
		sp.zvel = 8;
		
		Set3DSoundOwner(SpriteNum, PlaySound(DIGI_ERUPTION, sp, v3df_follow | v3df_dontpan));

		return (SpriteNum);
	}

	public static boolean SetQuake(PlayerStr pp, short tics, short amt) {
		SpawnQuake(pp.cursectnum, pp.posx, pp.posy, pp.posz, tics, amt, 30000);
		return (false);
	}

	public static int SetExpQuake(int Weapon) {
		SPRITE sp = sprite[Weapon];

		SpawnQuake(sp.sectnum, sp.x, sp.y, sp.z, 40, 4, 20000); // !JIM! was 8, 40000
		return (0);
	}

	public static int SetGunQuake(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];
		SpawnQuake(sp.sectnum, sp.x, sp.y, sp.z, 40, 8, 40000);

		return (0);
	}

	public static int SetPlayerQuake(PlayerStr pp) {
		SpawnQuake(pp.cursectnum, pp.posx, pp.posy, pp.posz, 40, 8, 40000);

		return (0);
	}

	public static int SetNuclearQuake(int Weapon) {
		SPRITE sp = sprite[Weapon];

		SpawnQuake(sp.sectnum, sp.x, sp.y, sp.z, 400, 8, 64000);
		return (0);
	}

	public static int SetSumoQuake(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];

		SpawnQuake(sp.sectnum, sp.x, sp.y, sp.z, 120, 4, 20000);
		return (0);
	}

	public static int SetSumoFartQuake(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];

		SpawnQuake(sp.sectnum, sp.x, sp.y, sp.z, 60, 4, 4000);
		return (0);
	}

}
