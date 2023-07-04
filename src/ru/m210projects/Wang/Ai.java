package ru.m210projects.Wang;

import static ru.m210projects.Build.Engine.headspritestat;
import static ru.m210projects.Build.Engine.neartag;
import static ru.m210projects.Build.Engine.nextspritestat;
import static ru.m210projects.Build.Engine.pHitInfo;
import static ru.m210projects.Build.Engine.sintable;
import static ru.m210projects.Build.Engine.sprite;
import static ru.m210projects.Build.Net.Mmulti.connecthead;
import static ru.m210projects.Build.Net.Mmulti.connectpoint2;
import static ru.m210projects.Build.Pragmas.klabs;
import static ru.m210projects.Wang.Game.Distance;
import static ru.m210projects.Wang.Game.Player;
import static ru.m210projects.Wang.Game.RAND_MAX;
import static ru.m210projects.Wang.Game.tmp_ptr;
import static ru.m210projects.Wang.Gameutils.ACTORMOVETICS;
import static ru.m210projects.Wang.Gameutils.CLIPMASK_ACTOR;
import static ru.m210projects.Wang.Gameutils.CLIPMASK_MISSILE;
import static ru.m210projects.Wang.Gameutils.CSTAT_SPRITE_INVISIBLE;
import static ru.m210projects.Wang.Gameutils.CSTAT_SPRITE_RESTORE;
import static ru.m210projects.Wang.Gameutils.CSTAT_SPRITE_TRANSLUCENT;
import static ru.m210projects.Wang.Gameutils.DISTANCE;
import static ru.m210projects.Wang.Gameutils.FACING;
import static ru.m210projects.Wang.Gameutils.HIT_MASK;
import static ru.m210projects.Wang.Gameutils.HIT_SECTOR;
import static ru.m210projects.Wang.Gameutils.HIT_SPRITE;
import static ru.m210projects.Wang.Gameutils.HIT_WALL;
import static ru.m210projects.Wang.Gameutils.MAX_ACTIVE_RANGE;
import static ru.m210projects.Wang.Gameutils.NORM_ANGLE;
import static ru.m210projects.Wang.Gameutils.NORM_SPRITE;
import static ru.m210projects.Wang.Gameutils.PF_DEAD;
import static ru.m210projects.Wang.Gameutils.RANDOM;
import static ru.m210projects.Wang.Gameutils.RANDOM_P2;
import static ru.m210projects.Wang.Gameutils.SPR2_DONT_TARGET_OWNER;
import static ru.m210projects.Wang.Gameutils.SPRITEp_SIZE_Z;
import static ru.m210projects.Wang.Gameutils.SPRITEp_TOS;
import static ru.m210projects.Wang.Gameutils.SPRITEp_UPPER;
import static ru.m210projects.Wang.Gameutils.SPR_ACTIVE;
import static ru.m210projects.Wang.Gameutils.SPR_ATTACKED;
import static ru.m210projects.Wang.Gameutils.SPR_DEAD;
import static ru.m210projects.Wang.Gameutils.SPR_FALLING;
import static ru.m210projects.Wang.Gameutils.SPR_FIND_PLAYER;
import static ru.m210projects.Wang.Gameutils.SPR_JUMPING;
import static ru.m210projects.Wang.Gameutils.SPR_NO_SCAREDZ;
import static ru.m210projects.Wang.Gameutils.SPR_RUN_AWAY;
import static ru.m210projects.Wang.Gameutils.SPR_SUICIDE;
import static ru.m210projects.Wang.Gameutils.SPR_SWIMMING;
import static ru.m210projects.Wang.Gameutils.SPR_TARGETED;
import static ru.m210projects.Wang.Gameutils.STD_RANDOM;
import static ru.m210projects.Wang.Gameutils.TF_TRACK_OCCUPIED;
import static ru.m210projects.Wang.Gameutils.WPN_FIST;
import static ru.m210projects.Wang.Gameutils.Z;
import static ru.m210projects.Wang.Gameutils.pUser;
import static ru.m210projects.Wang.JTags.TAG_SWARMSPOT;
import static ru.m210projects.Wang.Main.engine;
import static ru.m210projects.Wang.Names.BUNNY_RUN_R0;
import static ru.m210projects.Wang.Names.COOLG_RUN_R0;
import static ru.m210projects.Wang.Names.EEL_RUN_R0;
import static ru.m210projects.Wang.Names.HORNET_RUN_R0;
import static ru.m210projects.Wang.Names.RIPPER2_RUN_R0;
import static ru.m210projects.Wang.Names.RIPPER_RUN_R0;
import static ru.m210projects.Wang.Names.STAT_ENEMY;
import static ru.m210projects.Wang.Names.ZOMBIE_RUN_R0;
import static ru.m210projects.Wang.Main.gNet;
import static ru.m210projects.Wang.Rooms.FAFcansee;
import static ru.m210projects.Wang.Rooms.FAFhitscan;
import static ru.m210projects.Wang.Sector.NTAG_SEARCH_LO_HI;
import static ru.m210projects.Wang.Sector.OperateSector;
import static ru.m210projects.Wang.Sector.OperateWall;
import static ru.m210projects.Wang.Sound.PlaySpriteSound;
import static ru.m210projects.Wang.Sound.v3df_follow;
import static ru.m210projects.Wang.Sound.v3df_none;
import static ru.m210projects.Wang.Sprites.DropAhead;
import static ru.m210projects.Wang.Sprites.NewStateGroup;
import static ru.m210projects.Wang.Sprites.SetEnemyInactive;
import static ru.m210projects.Wang.Sprites.move_actor;
import static ru.m210projects.Wang.Sprites.move_sprite;
import static ru.m210projects.Wang.Tags.TT_DUCK_N_SHOOT;
import static ru.m210projects.Wang.Tags.TT_EXIT;
import static ru.m210projects.Wang.Tags.TT_HIDE_N_SHOOT;
import static ru.m210projects.Wang.Tags.TT_JUMP_DOWN;
import static ru.m210projects.Wang.Tags.TT_JUMP_UP;
import static ru.m210projects.Wang.Tags.TT_LADDER;
import static ru.m210projects.Wang.Tags.TT_OPERATE;
import static ru.m210projects.Wang.Tags.TT_SCAN;
import static ru.m210projects.Wang.Tags.TT_STAIRS;
import static ru.m210projects.Wang.Tags.TT_TRAVERSE;
import static ru.m210projects.Wang.Tags.TT_WANDER;
import static ru.m210projects.Wang.Track.ActorFindTrack;
import static ru.m210projects.Wang.Track.Track;
import static ru.m210projects.Wang.Type.MyTypes.BIT;
import static ru.m210projects.Wang.Type.MyTypes.DIV2;
import static ru.m210projects.Wang.Type.MyTypes.DIV4;
import static ru.m210projects.Wang.Type.MyTypes.DIV8;
import static ru.m210projects.Wang.Type.MyTypes.DTEST;
import static ru.m210projects.Wang.Type.MyTypes.TEST;
import static ru.m210projects.Wang.Weapon.PlayerTakeDamage;

import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Wang.Enemies.Decision;
import ru.m210projects.Wang.Factory.WangNetwork.MultiGameTypes;
import ru.m210projects.Wang.Type.Animator;
import ru.m210projects.Wang.Type.LONGp;
import ru.m210projects.Wang.Type.PlayerStr;
import ru.m210projects.Wang.Type.USER;
import static ru.m210projects.Wang.Type.Saveable.*;

public class Ai {

	public static final int SLOW_SPEED = 0, NORM_SPEED = 1, MID_SPEED = 2, FAST_SPEED = 3, MAX_SPEED = 4;

	public static final int MAXATTRIBSNDS = 11;

	public enum Attrib_Snds {
		attr_ambient, attr_alert, attr_attack, attr_pain, attr_die, attr_extra1, attr_extra2, attr_extra3, attr_extra4,
		attr_extra5, attr_extra6
	};

	public static boolean ActorMoveHitReact(int SpriteNum) {
		USER u = pUser[SpriteNum];

		// Should only return true if there is a reaction to what was hit that
		// would cause the calling function to abort

		switch (DTEST(u.ret, HIT_MASK)) {
		case HIT_SPRITE: {
			short HitSprite = NORM_SPRITE(u.ret);
			USER hu;
			Animator action;

			hu = pUser[HitSprite];

			// if you ran into a player - call close range functions
			if (hu != null && hu.PlayerP != -1) {
				DoActorPickClosePlayer(SpriteNum);
				action = ChooseAction(u.Personality.TouchTarget);
				if (action != null) {
					action.invoke(SpriteNum);
					return (true);
				}
			}
			break;
		}

		case HIT_WALL:
			break;
		case HIT_SECTOR:
			break;
		}

		return (false);
	}

	public static int RandomRange(int range) {
		long rand_num;
		long value;

		if (range <= 0)
			return (0);

		rand_num = RANDOM();

		if (rand_num == 65535)
			rand_num--;

		// shift values to give more precision
		value = (rand_num << 14) / ((65535 << 14) / range);

		if (value >= range)
			value = range - 1;

		return (int) value;
	}

	public static int StdRandomRange(int range) {
		long rand_num;
		long value;

		if (range <= 0)
			return (0);

		rand_num = STD_RANDOM();

		if (rand_num == RAND_MAX)
			rand_num--;

		// shift values to give more precision
		value = (rand_num << 14) / (((RAND_MAX) << 14) / range);

		if (value >= range)
			value = range - 1;

		return (int) (value);
	}

	public static boolean ActorFlaming(int SpriteNum) {
		USER u = pUser[SpriteNum];
		SPRITE sp = pUser[SpriteNum].getSprite();

		if (u.flame >= 0) {
			SPRITE fp = sprite[u.flame];

			int size = SPRITEp_SIZE_Z(sp) - DIV4(SPRITEp_SIZE_Z(sp));

			if (SPRITEp_SIZE_Z(fp) > size)
				return (true);
		}

		return (false);
	}

	public static void DoActorSetSpeed(int SpriteNum, int speed) {
		USER u = pUser[SpriteNum];
		SPRITE sp = pUser[SpriteNum].getSprite();

		if (TEST(sp.cstat, CSTAT_SPRITE_RESTORE))
			return;

		u.speed = (byte) speed;

		if (ActorFlaming(SpriteNum))
			sp.xvel = (short) (u.Attrib.Speed[speed] + DIV2(u.Attrib.Speed[speed]));
		else
			sp.xvel = u.Attrib.Speed[speed];
	}

	/*
	 * !AIC - Does a table lookup based on a random value from 0 to 1023. These
	 * tables are defined at the top of all actor files such as ninja.c, goro.c etc.
	 */

	public static Animator ChooseAction(Decision decision[]) {
		// !JIM! Here is an opportunity for some AI, instead of randomness!
		int random_value = RANDOM_P2(1024 << 5) >> 5;

		for (int i = 0; i < 10; i++) {

			if (random_value <= decision[i].range) {
				return (decision[i].action);
			}
		}
		return null;
	}

	/*
	 * !AIC - Sometimes just want the offset of the action
	 */

	public static int ChooseActionNumber(short decision[]) {
		int random_value = RANDOM_P2(1024 << 5) >> 5;

		for (int i = 0; true; i++) {
			if (random_value <= decision[i]) {
				return (i);
			}
		}
	}

	public static int DoActorNoise(Animator Action, int SpriteNum) {
		USER u = pUser[SpriteNum];

		if (Action == InitActorAmbientNoise) {
			PlaySpriteSound(SpriteNum, Attrib_Snds.attr_ambient, v3df_follow);
		} else if (Action == InitActorAlertNoise) {
			if (u != null && !u.DidAlert) // This only allowed once
				PlaySpriteSound(SpriteNum, Attrib_Snds.attr_alert, v3df_follow);
		} else if (Action == InitActorAttackNoise) {
			PlaySpriteSound(SpriteNum, Attrib_Snds.attr_attack, v3df_follow);
		} else if (Action == InitActorPainNoise) {
			PlaySpriteSound(SpriteNum, Attrib_Snds.attr_pain, v3df_follow);
		} else if (Action == InitActorDieNoise) {
			PlaySpriteSound(SpriteNum, Attrib_Snds.attr_die, v3df_none);
		} else if (Action == InitActorExtra1Noise) {
			PlaySpriteSound(SpriteNum, Attrib_Snds.attr_extra1, v3df_follow);
		} else if (Action == InitActorExtra2Noise) {
			PlaySpriteSound(SpriteNum, Attrib_Snds.attr_extra2, v3df_follow);
		} else if (Action == InitActorExtra3Noise) {
			PlaySpriteSound(SpriteNum, Attrib_Snds.attr_extra3, v3df_follow);
		} else if (Action == InitActorExtra4Noise) {
			PlaySpriteSound(SpriteNum, Attrib_Snds.attr_extra4, v3df_follow);
		} else if (Action == InitActorExtra5Noise) {
			PlaySpriteSound(SpriteNum, Attrib_Snds.attr_extra5, v3df_follow);
		} else if (Action == InitActorExtra6Noise) {
			PlaySpriteSound(SpriteNum, Attrib_Snds.attr_extra6, v3df_follow);
		}

		return (0);
	}

	public static boolean CanSeePlayer(int SpriteNum) {
		USER u = pUser[SpriteNum];
		SPRITE sp = pUser[SpriteNum].getSprite();

		// if actor can still see the player
		int look_height = SPRITEp_TOS(sp);

		if (FAFcansee(sp.x, sp.y, look_height, sp.sectnum, sprite[u.tgt_sp].x, sprite[u.tgt_sp].y,
				SPRITEp_UPPER(sprite[u.tgt_sp]), sprite[u.tgt_sp].sectnum))
			return (true);
		else
			return (false);
	}

	public static boolean CanHitPlayer(int SpriteNum) {
		USER u = pUser[SpriteNum];
		SPRITE sp = pUser[SpriteNum].getSprite(), hp;
		int xvect, yvect, zvect;
		short ang;
		// if actor can still see the player
		int zhs, zhh;

		zhs = sp.z - DIV2(SPRITEp_SIZE_Z(sp));

		hp = sprite[u.tgt_sp];

		// get angle to target
		ang = engine.getangle(hp.x - sp.x, hp.y - sp.y);

		// get x,yvect
		xvect = sintable[NORM_ANGLE(ang + 512)];
		yvect = sintable[NORM_ANGLE(ang)];

		// get zvect
		zhh = hp.z - DIV2(SPRITEp_SIZE_Z(hp));
		if (hp.x - sp.x != 0)
			zvect = xvect * ((zhh - zhs) / (hp.x - sp.x));
		else if (hp.y - sp.y != 0)
			zvect = yvect * ((zhh - zhs) / (hp.y - sp.y));
		else
			return (false);

		FAFhitscan(sp.x, sp.y, zhs, sp.sectnum, xvect, yvect, zvect, pHitInfo, CLIPMASK_MISSILE);

		if (pHitInfo.hitsect < 0)
			return (false);

		if (pHitInfo.hitsprite == u.tgt_sp)
			return (true);

		return (false);
	}

	/*
	 * !AIC - Pick a nearby player to be the actors target
	 */

	public static void DoActorPickClosePlayer(int SpriteNum) {
		USER u = pUser[SpriteNum];
		SPRITE sp = pUser[SpriteNum].getSprite();
		int dist, near_dist = MAX_ACTIVE_RANGE;
		short pnum;
		PlayerStr pp;
		// if actor can still see the player
		int look_height = SPRITEp_TOS(sp);
		boolean found = false;
		int i, nexti;

		if (u.ID != ZOMBIE_RUN_R0 || gNet.MultiGameType != MultiGameTypes.MULTI_GAME_COOPERATIVE) {
			// Set initial target to Player 0
			u.tgt_sp = Player[connecthead].PlayerSprite;

			if (TEST(u.Flags2, SPR2_DONT_TARGET_OWNER)) {
				for (pnum = connecthead; pnum != -1; pnum = connectpoint2[pnum]) {
					pp = Player[pnum];

					if (sp.owner == pp.PlayerSprite)
						continue;

					u.tgt_sp = pp.PlayerSprite;
					break;
				}
			}

			// Set initial target to the closest player
			near_dist = MAX_ACTIVE_RANGE;
			for (pnum = connecthead; pnum != -1; pnum = connectpoint2[pnum]) {
				pp = Player[pnum];

				// Zombies don't target their masters!
				if (TEST(u.Flags2, SPR2_DONT_TARGET_OWNER)) {
					if (sp.owner == pp.PlayerSprite)
						continue;

					if (!PlayerTakeDamage(pp, SpriteNum))
						continue;

					// if co-op don't hurt teammate
					// if (gNet.MultiGameType == MULTI_GAME_COOPERATIVE && !gNet.HurtTeammate &&
					// u.spal == User[pp.PlayerSprite].spal)
					// continue;
				}

				dist = DISTANCE(sp.x, sp.y, pp.posx, pp.posy);

				if (dist < near_dist) {
					near_dist = dist;
					u.tgt_sp = pp.PlayerSprite;
				}
			}

			// see if you can find someone close that you can SEE
			near_dist = MAX_ACTIVE_RANGE;
			found = false;
			for (pnum = connecthead; pnum != -1; pnum = connectpoint2[pnum]) {
				pp = Player[pnum];

				// Zombies don't target their masters!
				if (TEST(u.Flags2, SPR2_DONT_TARGET_OWNER)) {
					if (sp.owner == pp.PlayerSprite)
						continue;

					if (!PlayerTakeDamage(pp, SpriteNum))
						continue;

					// if co-op don't hurt teammate
					// if (gNet.MultiGameType == MULTI_GAME_COOPERATIVE && !gNet.HurtTeammate &&
					// u.spal == User[pp.PlayerSprite].spal)
					// continue;
				}

				dist = DISTANCE(sp.x, sp.y, pp.posx, pp.posy);

				// ICanSee = FAFcansee(sp.x, sp.y, look_height, sp.sectnum, pp.SpriteP.x,
				// pp.SpriteP.y, SPRITEp_UPPER(pp.SpriteP), pp.SpriteP.sectnum);
				SPRITE psp = pp.getSprite();
				if (dist < near_dist && FAFcansee(sp.x, sp.y, look_height, sp.sectnum, psp.x, psp.y,
						SPRITEp_UPPER(psp), psp.sectnum)) {
					near_dist = dist;
					u.tgt_sp = pp.PlayerSprite;
					found = true;
				}
			}

		}

		// this is only for Zombies right now
		// zombie target other actors
		if (!found && TEST(u.Flags2, SPR2_DONT_TARGET_OWNER)) {
			near_dist = MAX_ACTIVE_RANGE;

			for (i = headspritestat[STAT_ENEMY]; i != -1; i = nexti) {
				nexti = nextspritestat[i];

				if (i == SpriteNum)
					continue;

				if (pUser[i] != null && TEST(pUser[i].Flags, SPR_SUICIDE | SPR_DEAD))
					continue;

				dist = DISTANCE(sp.x, sp.y, sprite[i].x, sprite[i].y);

				if (dist < near_dist && FAFcansee(sp.x, sp.y, look_height, sp.sectnum, sprite[i].x, sprite[i].y,
						SPRITEp_UPPER(sprite[i]), sprite[i].sectnum)) {
					near_dist = dist;
					u.tgt_sp = i;
				}
			}
		}
	}

	public static int GetPlayerSpriteNum(int SpriteNum) {
		USER u = pUser[SpriteNum];
		short pnum;
		PlayerStr pp;

		for (pnum = connecthead; pnum != -1; pnum = connectpoint2[pnum]) {
			pp = Player[pnum];

			if (pp.PlayerSprite == u.tgt_sp) {
				return (pp.PlayerSprite);
			}
		}
		return (0);
	}

	public static int CloseRangeDist(SPRITE sp1, SPRITE sp2) {
		int clip1 = sp1.clipdist;
		int clip2 = sp2.clipdist;

		// add clip boxes and a fudge factor
		int DIST_CLOSE_RANGE = 400;

		return ((clip1 << 2) + (clip2 << 2) + DIST_CLOSE_RANGE);
	}

	public static boolean DoActorOperate(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];
		USER u = pUser[SpriteNum];

		if (u.ID == HORNET_RUN_R0 || u.ID == EEL_RUN_R0 || u.ID == BUNNY_RUN_R0)
			return (false);

		if (u.Rot == u.ActorActionSet.Sit || u.Rot == u.ActorActionSet.Stand)
			return (false);

		if ((u.WaitTics -= ACTORMOVETICS) > 0)
			return (false);

		engine.neartag(sp.x, sp.y, sp.z - DIV2(SPRITEp_SIZE_Z(sp)), sp.sectnum, sp.ang, neartag, 1024,
				NTAG_SEARCH_LO_HI);

		if (neartag.tagsector >= 0 && neartag.taghitdist < 1024) {
			if (OperateSector(neartag.tagsector, false)) {
				u.WaitTics = 2 * 120;

				NewStateGroup(SpriteNum, u.ActorActionSet.Sit);
			}
		}

		if (neartag.tagwall >= 0 && neartag.taghitdist < 1024) {
			if (OperateWall(neartag.tagwall, false)) {
				u.WaitTics = 2 * 120;

				NewStateGroup(SpriteNum, u.ActorActionSet.Stand);
			}
		}
		return (true);
	}

	public static final Animator InitActorAttack = new Animator() {
		@Override
		public boolean invoke(int spr) {
			InitActorAttack(spr);
			return false;
		}
	};

	public static final Animator InitActorRunToward = new Animator() {
		@Override
		public boolean invoke(int SpriteNum) {
			InitActorRunToward(SpriteNum);
			return false;
		}
	};

	public static final Animator InitActorRunAway = new Animator() {
		@Override
		public boolean invoke(int SpriteNum) {
			InitActorRunAway(SpriteNum);
			return false;
		}
	};

	public static Decision GenericFlaming[] = { new Decision(30, InitActorAttack),
			new Decision(512, InitActorRunToward), new Decision(1024, InitActorRunAway), };

	/*
	 * !AIC KEY - This routine decides what the actor will do next. It is not called
	 * every time through the loop. This would be too slow. It is only called when
	 * the actor needs to know what to do next such as running into something or
	 * being targeted. It makes decisions based on the distance and viewablity of
	 * its target (u.tgt_sp). When it figures out the situatation with its target it
	 * calls ChooseAction which does a random table lookup to decide what action to
	 * initialize. Once this action is initialized it will be called until it can't
	 * figure out what to do anymore and then this routine is called again.
	 */

	public static Animator DoActorActionDecide(int SpriteNum) {
		USER u = pUser[SpriteNum];
		SPRITE sp = pUser[SpriteNum].getSprite();
		int dist;
		Animator action;
		USER pu = null;
		boolean ICanSee = false;

		// REMINDER: This function is not even called if SpriteControl doesn't let
		// it get called

		u.Dist = 0;
		action = InitActorDecide;

		if (TEST(u.Flags, SPR_JUMPING | SPR_FALLING)) {
			return (action);
		}

		// everybody on fire acts like this
		if (ActorFlaming(SpriteNum)) {
			action = ChooseAction(GenericFlaming);
			return (action);
		}

		ICanSee = CanSeePlayer(SpriteNum); // Only need to call once
											// But need the result multiple times

		// !AIC KEY - If aware of player - var is changed in SpriteControl
		if (TEST(u.Flags, SPR_ACTIVE)) {
			// Try to operate stuff
			DoActorOperate(SpriteNum);

			// if far enough away and cannot see the player
			dist = Distance(sp.x, sp.y, sprite[u.tgt_sp].x, sprite[u.tgt_sp].y);

			if (dist > 30000 && !ICanSee) {
				// Enemy goes inactive - he is still allowed to roam about for about
				// 5 seconds trying to find another player before his active_range is
				// bumped down
				SetEnemyInactive(SpriteNum);

				// You've lost the player - now decide what to do
				action = ChooseAction(u.Personality.LostTarget);
				return (action);
			}

			pu = pUser[GetPlayerSpriteNum(SpriteNum)];
			// check for short range attack possibility
			if ((dist < CloseRangeDist(sp, sprite[u.tgt_sp]) && ICanSee) || (pu != null && pu.WeaponNum == WPN_FIST
					&& u != null && u.ID != RIPPER2_RUN_R0 && u.ID != RIPPER_RUN_R0)) {
				if ((u != null && u.ID == COOLG_RUN_R0 && TEST(sp.cstat, CSTAT_SPRITE_TRANSLUCENT))
						|| TEST(sp.cstat, CSTAT_SPRITE_INVISIBLE))
					action = ChooseAction(u.Personality.Evasive);
				else
					action = ChooseAction(u.Personality.CloseRange);
				return (action);
			}

			// if player is facing me and I'm being attacked
			if (FACING(sp, sprite[u.tgt_sp]) && TEST(u.Flags, SPR_ATTACKED) && ICanSee) {
				// if I'm a target - at least one missile comming at me
				if (TEST(u.Flags, SPR_TARGETED)) {
					// not going to evade, reset the target bit
					u.Flags &= ~(SPR_TARGETED); // as far as actor
												// knows, its not a
												// target any more
					if (u.ActorActionSet.Duck != null && RANDOM_P2(1024 << 8) >> 8 < 100)
						action = InitActorDuck;
					else {
						if ((u.ID == COOLG_RUN_R0 && TEST(sp.cstat, CSTAT_SPRITE_TRANSLUCENT))
								|| TEST(sp.cstat, CSTAT_SPRITE_INVISIBLE))
							action = ChooseAction(u.Personality.Evasive);
						else
							action = ChooseAction(u.Personality.Battle);
					}
					return (action);
				}
				// if NOT a target - don't bother with evasive action and start
				// fighting
				else {
					if ((u.ID == COOLG_RUN_R0 && TEST(sp.cstat, CSTAT_SPRITE_TRANSLUCENT))
							|| TEST(sp.cstat, CSTAT_SPRITE_INVISIBLE))
						action = ChooseAction(u.Personality.Evasive);
					else
						action = ChooseAction(u.Personality.Battle);
					return (action);
				}

			}
			// if player is NOT facing me he is running or unaware of actor
			else if (ICanSee) {
				if ((u.ID == COOLG_RUN_R0 && TEST(sp.cstat, CSTAT_SPRITE_TRANSLUCENT))
						|| TEST(sp.cstat, CSTAT_SPRITE_INVISIBLE))
					action = ChooseAction(u.Personality.Evasive);
				else
					action = ChooseAction(u.Personality.Offense);
				return (action);
			} else {
				// You've lost the player - now decide what to do
				action = ChooseAction(u.Personality.LostTarget);
				return (action);
			}
		}
		// Not active - not aware of player and cannot see him
		else {
			// try and find another player
			// pick a closeby player as the (new) target
			if (sp.hitag != TAG_SWARMSPOT)
				DoActorPickClosePlayer(SpriteNum);

			// if close by
			dist = Distance(sp.x, sp.y, sprite[u.tgt_sp].x, sprite[u.tgt_sp].y);
			if (dist < 15000 || ICanSee) {
				if ((FACING(sp, sprite[u.tgt_sp]) && dist < 10000) || ICanSee) {
					DoActorOperate(SpriteNum);

					// Don't let player completely sneek up behind you
					action = ChooseAction(u.Personality.Surprised);
					if (!u.DidAlert && ICanSee) {
						DoActorNoise(InitActorAlertNoise, SpriteNum);
						u.DidAlert = true;
					}
					return (action);

				} else {
					// Player has not seen actor, to be fair let him know actor
					// are there
					DoActorNoise(ChooseAction(u.Personality.Broadcast), SpriteNum);
					return (action);
				}
			}
		}

		return (action);
	}

	/*
	 * !AIC - Setup to do the decision
	 */

	public static final Animator InitActorDecide = new Animator() {
		@Override
		public boolean invoke(int spr) {
			InitActorDecide(spr);
			return false;
		}
	};

	public static int InitActorDecide(int SpriteNum) {
		USER u = pUser[SpriteNum];

		// NOTE: It is possible to overflow the stack with too many calls to this
		// routine
		// Should use:
		// u.ActorActionFunc = DoActorDecide;
		// Instead of calling this function direcly

		u.ActorActionFunc = DoActorDecide;

		DoActorDecide.invoke(SpriteNum);
		return (0);
	}

	public static final Animator DoActorDecide = new Animator() {
		@Override
		public boolean invoke(int SpriteNum) {
			USER u = pUser[SpriteNum];
			SPRITE sp = pUser[SpriteNum].getSprite();
			Animator actor_action;

			// See what to do next
			actor_action = DoActorActionDecide(SpriteNum);

			// Fix for the GenericFlaming bug for actors that don't have attack states
			if (actor_action == InitActorAttack && u.WeaponNum == 0)
				return false; // Just let the actor do as it was doing before in this case

			// zombie is attacking a player
			if (actor_action == InitActorAttack && u != null && u.ID == ZOMBIE_RUN_R0 && u.tgt_sp != -1
					&& pUser[u.tgt_sp] != null && pUser[u.tgt_sp].PlayerP != -1) {
				// Don't let zombies shoot at master
				if (sp.owner == u.tgt_sp)
					return false;

				// if this player cannot take damage from this zombie(weapon) return out
				if (!PlayerTakeDamage(Player[pUser[u.tgt_sp].PlayerP], SpriteNum))
					return false;
			}

			if (actor_action != InitActorDecide) {
				// NOT staying put
				actor_action.invoke(SpriteNum);
			} else {
				// Actually staying put
				NewStateGroup(SpriteNum, u.ActorActionSet.Stand);
			}

			return false;
		}
	};

	public static final Animator InitActorAlertNoise = new Animator() {
		@Override
		public boolean invoke(int SpriteNum) {
			USER u = pUser[SpriteNum];
			// make some sort of noise here

			u.ActorActionFunc = DoActorDecide;
			return false;
		}
	};

	public static final Animator InitActorAmbientNoise = new Animator() {
		@Override
		public boolean invoke(int SpriteNum) {
			USER u = pUser[SpriteNum];
			// make some sort of noise here

			u.ActorActionFunc = DoActorDecide;
			return false;
		}
	};

	public static final Animator InitActorAttackNoise = new Animator() {
		@Override
		public boolean invoke(int SpriteNum) {
			USER u = pUser[SpriteNum];
			// make some sort of noise here

			u.ActorActionFunc = DoActorDecide;
			return false;
		}
	};

	public static final Animator InitActorPainNoise = new Animator() {
		@Override
		public boolean invoke(int SpriteNum) {
			USER u = pUser[SpriteNum];
			// make some sort of noise here

			u.ActorActionFunc = DoActorDecide;
			return false;
		}
	};

	public static final Animator InitActorDieNoise = new Animator() {
		@Override
		public boolean invoke(int SpriteNum) {
			USER u = pUser[SpriteNum];
			// make some sort of noise here

			u.ActorActionFunc = DoActorDecide;
			return false;
		}
	};

	public static final Animator InitActorExtra1Noise = new Animator() {
		@Override
		public boolean invoke(int SpriteNum) {
			USER u = pUser[SpriteNum];
			// make some sort of noise here

			u.ActorActionFunc = DoActorDecide;
			return false;
		}
	};

	public static final Animator InitActorExtra2Noise = new Animator() {
		@Override
		public boolean invoke(int SpriteNum) {
			USER u = pUser[SpriteNum];
			// make some sort of noise here

			u.ActorActionFunc = DoActorDecide;
			return false;
		}
	};

	public static final Animator InitActorExtra3Noise = new Animator() {
		@Override
		public boolean invoke(int SpriteNum) {
			USER u = pUser[SpriteNum];
			// make some sort of noise here

			u.ActorActionFunc = DoActorDecide;
			return false;
		}
	};

	public static final Animator InitActorExtra4Noise = new Animator() {
		@Override
		public boolean invoke(int SpriteNum) {
			USER u = pUser[SpriteNum];
			// make some sort of noise here

			u.ActorActionFunc = DoActorDecide;
			return false;
		}
	};

	public static final Animator InitActorExtra5Noise = new Animator() {
		@Override
		public boolean invoke(int SpriteNum) {
			USER u = pUser[SpriteNum];
			// make some sort of noise here

			u.ActorActionFunc = DoActorDecide;
			return false;
		}
	};

	public static final Animator InitActorExtra6Noise = new Animator() {
		@Override
		public boolean invoke(int SpriteNum) {
			USER u = pUser[SpriteNum];
			// make some sort of noise here

			u.ActorActionFunc = DoActorDecide;
			return false;
		}
	};

	/*
	 * !AIC KEY - Routines handle moving toward the player.
	 */

	public static final Animator InitActorMoveCloser = new Animator() {
		@Override
		public boolean invoke(int SpriteNum) {
			USER u = pUser[SpriteNum];
			u.ActorActionFunc = DoActorMoveCloser;

			if (u.Rot != u.ActorActionSet.Run)
				NewStateGroup(SpriteNum, u.ActorActionSet.Run);

			u.ActorActionFunc.invoke(SpriteNum);
			return false;
		}
	};

	public static int DoActorCantMoveCloser(int SpriteNum) {
		USER u = pUser[SpriteNum];
		SPRITE sp = pUser[SpriteNum].getSprite();

		// MONO_PRINT("Can't move closer\n");

		u.track = (short) FindTrackToPlayer(u);

		if (u.track >= 0) {
			sp.ang = engine.getangle((Track[u.track].TrackPoint[u.point]).x - sp.x,
					(Track[u.track].TrackPoint[u.point]).y - sp.y);

			DoActorSetSpeed(SpriteNum, MID_SPEED);
			u.Flags |= (SPR_FIND_PLAYER);

			u.ActorActionFunc = DoActorDecide;
			NewStateGroup(SpriteNum, u.ActorActionSet.Run);
			// MONO_PRINT("Trying to get to the track point\n");
		} else {
			// Try to move closer
			InitActorReposition.invoke(SpriteNum);
		}
		return (0);
	}

	public static final Animator DoActorMoveCloser = new Animator() {
		@Override
		public boolean invoke(int SpriteNum) {
			USER u = pUser[SpriteNum];
			SPRITE sp = pUser[SpriteNum].getSprite();
			int nx, ny;

			nx = sp.xvel * sintable[NORM_ANGLE(sp.ang + 512)] >> 14;
			ny = sp.xvel * sintable[NORM_ANGLE(sp.ang)] >> 14;

			// if cannot move the sprite
			if (!move_actor(SpriteNum, nx, ny, 0)) {
				if (ActorMoveHitReact(SpriteNum))
					return false;

				DoActorCantMoveCloser(SpriteNum);
				return false;
			}

			// Do a noise if ok
			DoActorNoise(ChooseAction(u.Personality.Broadcast), SpriteNum);

			// after moving a ways check to see if player is still in sight
			if (u.DistCheck > 550) {
				u.DistCheck = 0;

				// If player moved out of sight
				if (!CanSeePlayer(SpriteNum)) {
					// stay put and choose another option
					InitActorDecide(SpriteNum);
					return false;
				} else {
					// turn to face player
					sp.ang = engine.getangle(sprite[u.tgt_sp].x - sp.x, sprite[u.tgt_sp].y - sp.y);
				}
			}

			// Should be a random value test
			if (u.Dist > 512 * 3) {
				InitActorDecide(SpriteNum);
			}
			return false;
		}
	};

	/*
	 * !AIC - Find tracks of different types. Toward target, away from target, etc.
	 */

	private static int PlayerAbove[] = { BIT(TT_LADDER), BIT(TT_STAIRS), BIT(TT_JUMP_UP), BIT(TT_TRAVERSE),
			BIT(TT_OPERATE), BIT(TT_SCAN) };

	private static int PlayerBelow[] = { BIT(TT_JUMP_DOWN), BIT(TT_STAIRS), BIT(TT_TRAVERSE), BIT(TT_OPERATE),
			BIT(TT_SCAN) };

	private static int PlayerOnLevel[] = { BIT(TT_DUCK_N_SHOOT), BIT(TT_HIDE_N_SHOOT), BIT(TT_TRAVERSE), BIT(TT_EXIT),
			BIT(TT_OPERATE), BIT(TT_SCAN) };

	public static int FindTrackToPlayer(USER u) {
		SPRITE sp = u.getSprite();

		int track;
		int i, type[], size;
		int zdiff;

		zdiff = SPRITEp_UPPER(sprite[u.tgt_sp]) - (sp.z - SPRITEp_SIZE_Z(sp) + Z(8));

		if (klabs(zdiff) <= Z(20)) {
			type = PlayerOnLevel;
			size = PlayerOnLevel.length;
		} else {
			if (zdiff < 0) {
				type = PlayerAbove;
				size = PlayerAbove.length;
			} else {
				type = PlayerBelow;
				size = PlayerBelow.length;
			}
		}

		for (i = 0; i < size; i++) {
			track = ActorFindTrack(u.SpriteNum, 1, type[i], tmp_ptr[0], tmp_ptr[1]);
			if (track >= 0) {
				u.point = (short) tmp_ptr[0].value;
				u.track_dir = (short) tmp_ptr[1].value;
				Track[track].flags |= (TF_TRACK_OCCUPIED);

				return (track);
			}
		}

		return (-1);
	}

	private static int RunAwayTracks[] = { BIT(TT_EXIT), BIT(TT_LADDER), BIT(TT_TRAVERSE), BIT(TT_STAIRS),
			BIT(TT_JUMP_UP), BIT(TT_JUMP_DOWN), BIT(TT_DUCK_N_SHOOT), BIT(TT_HIDE_N_SHOOT), BIT(TT_OPERATE),
			BIT(TT_SCAN) };

	public static short FindTrackAwayFromPlayer(USER u) {
		for (int i = 0; i < RunAwayTracks.length; i++) {
			int track = ActorFindTrack(u.SpriteNum, -1, RunAwayTracks[i], tmp_ptr[0], tmp_ptr[1]);

			if (track >= 0) {
				u.point = (short) tmp_ptr[0].value;
				u.track_dir = (short) tmp_ptr[1].value;
				Track[track].flags |= (TF_TRACK_OCCUPIED);

				return (short) (track);
			}
		}
		return (-1);
	}

	private static int WanderTracks[] = { BIT(TT_DUCK_N_SHOOT), BIT(TT_HIDE_N_SHOOT), BIT(TT_WANDER), BIT(TT_JUMP_DOWN),
			BIT(TT_JUMP_UP), BIT(TT_TRAVERSE), BIT(TT_STAIRS), BIT(TT_LADDER), BIT(TT_EXIT), BIT(TT_OPERATE) };

	public static short FindWanderTrack(USER u) {
		for (int i = 0; i < WanderTracks.length; i++) {
			int track = ActorFindTrack(u.SpriteNum, -1, WanderTracks[i], tmp_ptr[0], tmp_ptr[1]);

			if (track >= 0) {
				u.point = (short) tmp_ptr[0].value;
				u.track_dir = (short) tmp_ptr[1].value;
				Track[track].flags |= (TF_TRACK_OCCUPIED);

				return (short) (track);
			}
		}

		return (-1);
	}

	public static int InitActorRunAway(int SpriteNum) {
		USER u = pUser[SpriteNum];
		SPRITE sp = pUser[SpriteNum].getSprite();

		u.ActorActionFunc = DoActorDecide;
		NewStateGroup(SpriteNum, u.ActorActionSet.Run);

		u.track = FindTrackAwayFromPlayer(u);

		if (u.track >= 0) {
			sp.ang = NORM_ANGLE(engine.getangle((Track[u.track].TrackPoint[u.point]).x - sp.x,
					(Track[u.track].TrackPoint[u.point]).y - sp.y));
			DoActorSetSpeed(SpriteNum, FAST_SPEED);
			u.Flags |= (SPR_RUN_AWAY);
		} else {
			u.Flags |= (SPR_RUN_AWAY);
			InitActorReposition.invoke(SpriteNum);
		}

		return (0);
	}

	public static int InitActorRunToward(int SpriteNum) {
		USER u = pUser[SpriteNum];

		u.ActorActionFunc = DoActorDecide;
		NewStateGroup(SpriteNum, u.ActorActionSet.Run);

		InitActorReposition.invoke(SpriteNum);
		DoActorSetSpeed(SpriteNum, FAST_SPEED);

		return (0);
	}

	/*
	 * !AIC - Where actors do their attacks. There is some special case code
	 * throughout these. Both close and long range attacks are handled here by
	 * transitioning to the correct attack state.
	 */

	public static boolean CHOOSE2(int value) {
		return (RANDOM_P2(1024) < (value));
	}

	public static int InitActorAttack(int SpriteNum) {
		USER u = pUser[SpriteNum];
		SPRITE sp = pUser[SpriteNum].getSprite();

		// zombie is attacking a player
		if (u != null && u.ID == ZOMBIE_RUN_R0 && u.tgt_sp != -1 && pUser[u.tgt_sp] != null
				&& pUser[u.tgt_sp].PlayerP != -1) {
			// Don't let zombies shoot at master
			if (sp.owner == (u.tgt_sp))
				return (0);

			// if this player cannot take damage from this zombie(weapon) return out
			if (!PlayerTakeDamage(Player[pUser[u.tgt_sp].PlayerP], SpriteNum))
				return (0);
		}

		if (TEST(sprite[u.tgt_sp].cstat, CSTAT_SPRITE_TRANSLUCENT)) {
			InitActorRunAway(SpriteNum);
			return (0);
		}

		if (u.tgt_sp != -1 && pUser[u.tgt_sp] != null && pUser[u.tgt_sp].Health <= 0) {
			DoActorPickClosePlayer(SpriteNum);
			InitActorReposition.invoke(SpriteNum);
			return (0);
		}

		if (!CanHitPlayer(SpriteNum)) {
			InitActorReposition.invoke(SpriteNum);
			return (0);
		}

		// if the guy you are after is dead, look for another and
		// reposition
		if (u != null && u.tgt_sp != -1 && pUser[u.tgt_sp] != null && pUser[u.tgt_sp].PlayerP != -1
				&& TEST(Player[pUser[u.tgt_sp].PlayerP].Flags, PF_DEAD)) {
			DoActorPickClosePlayer(SpriteNum);
			InitActorReposition.invoke(SpriteNum);
			return (0);
		}

		u.ActorActionFunc = DoActorAttack;

		// face player when attacking
		sp.ang = NORM_ANGLE(engine.getangle(sprite[u.tgt_sp].x - sp.x, sprite[u.tgt_sp].y - sp.y));

		// If it's your own kind, lay off!
		if (u != null && u.tgt_sp != -1 && pUser[u.tgt_sp] != null && u.ID == pUser[u.tgt_sp].ID
				&& pUser[u.tgt_sp].PlayerP == -1) {
			InitActorRunAway(SpriteNum);
			return (0);
		}

		// Hari Kari for Ninja's
		if (u.ActorActionSet.Death2 != null) {
			if (u.Health < 38) {
				if (CHOOSE2(100)) {
					u.ActorActionFunc = DoActorDecide;
					NewStateGroup(SpriteNum, u.ActorActionSet.Death2);
					return (0);
				}
			}
		}

		(u.ActorActionFunc).invoke(SpriteNum);
		return (0);
	}

	public static final Animator DoActorAttack = new Animator() {
		@Override
		public boolean invoke(int SpriteNum) {
			USER u = pUser[SpriteNum], pu;
			SPRITE sp = pUser[SpriteNum].getSprite();
			int rand_num;
			int dist;

			DoActorNoise(ChooseAction(u.Personality.Broadcast), SpriteNum);

			dist = DISTANCE(sp.x, sp.y, sprite[u.tgt_sp].x, sprite[u.tgt_sp].y);

			pu = pUser[GetPlayerSpriteNum(SpriteNum)];
			if (u.ActorActionSet.CloseAttack[0] != null && dist < CloseRangeDist(sp, sprite[u.tgt_sp])
					|| (pu != null && pu.WeaponNum == WPN_FIST)) {
				rand_num = ChooseActionNumber(u.ActorActionSet.CloseAttackPercent);

				NewStateGroup(SpriteNum, u.ActorActionSet.CloseAttack[rand_num]);
			} else {
				rand_num = ChooseActionNumber(u.ActorActionSet.AttackPercent);

				NewStateGroup(SpriteNum, u.ActorActionSet.Attack[rand_num]);
				u.ActorActionFunc = DoActorDecide;
			}

			return false;
		}
	};

	public static final Animator InitActorEvade = new Animator() {
		@Override
		public boolean invoke(int SpriteNum) {
			return InitActorEvade(SpriteNum) != 0;
		}
	};

	public static int InitActorEvade(int SpriteNum) {
		USER u = pUser[SpriteNum];
		SPRITE sp = pUser[SpriteNum].getSprite();

		// Evade is same thing as run away except when you get to the end of the
		// track
		// you stop and take up the fight again.

		u.ActorActionFunc = DoActorDecide;
		NewStateGroup(SpriteNum, u.ActorActionSet.Run);

		u.track = FindTrackAwayFromPlayer(u);

		if (u.track >= 0) {
			sp.ang = NORM_ANGLE(engine.getangle((Track[u.track].TrackPoint[u.point]).x - sp.x,
					(Track[u.track].TrackPoint[u.point]).y - sp.y));
			DoActorSetSpeed(SpriteNum, FAST_SPEED);
			// NOT doing a RUN_AWAY
			u.Flags &= ~(SPR_RUN_AWAY);
		}
		return (0);
	}

	public static final Animator InitActorWanderAround = new Animator() {
		@Override
		public boolean invoke(int SpriteNum) {
			USER u = pUser[SpriteNum];
			SPRITE sp = pUser[SpriteNum].getSprite();

			u.ActorActionFunc = DoActorDecide;
			NewStateGroup(SpriteNum, u.ActorActionSet.Run);

			DoActorPickClosePlayer(SpriteNum);

			u.track = FindWanderTrack(u);

			if (u.track >= 0) {
				sp.ang = engine.getangle((Track[u.track].TrackPoint[u.point]).x - sp.x,
						(Track[u.track].TrackPoint[u.point]).y - sp.y);
				DoActorSetSpeed(SpriteNum, NORM_SPEED);
			}
			return false;
		}
	};

	public static final Animator InitActorFindPlayer = new Animator() {
		@Override
		public boolean invoke(int SpriteNum) {

			USER u = pUser[SpriteNum];
			SPRITE sp = pUser[SpriteNum].getSprite();

			u.ActorActionFunc = DoActorDecide;
			NewStateGroup(SpriteNum, u.ActorActionSet.Run);

			u.track = (short) FindTrackToPlayer(u);

			if (u.track >= 0) {
				sp.ang = engine.getangle((Track[u.track].TrackPoint[u.point]).x - sp.x,
						(Track[u.track].TrackPoint[u.point]).y - sp.y);
				DoActorSetSpeed(SpriteNum, MID_SPEED);
				u.Flags |= (SPR_FIND_PLAYER);

				u.ActorActionFunc = DoActorDecide;
				NewStateGroup(SpriteNum, u.ActorActionSet.Run);
			} else {
				InitActorReposition.invoke(SpriteNum);
			}
			return false;
		}
	};

	public static final Animator InitActorDuck = new Animator() {
		@Override
		public boolean invoke(int SpriteNum) {
			USER u = pUser[SpriteNum];
			SPRITE sp = pUser[SpriteNum].getSprite();

			if (u.ActorActionSet.Duck == null) {
				u.ActorActionFunc = DoActorDecide;
				return false;
			}

			u.ActorActionFunc = DoActorDuck;
			NewStateGroup(SpriteNum, u.ActorActionSet.Duck);

			int dist = Distance(sp.x, sp.y, sprite[u.tgt_sp].x, sprite[u.tgt_sp].y);

			if (dist > 8000) {
				u.WaitTics = 190;
			} else {
				// u.WaitTics = 120;
				u.WaitTics = 60;
			}

			(u.ActorActionFunc).invoke(SpriteNum);

			return false;
		}
	};

	public static final Animator DoActorDuck = new Animator() {
		@Override
		public boolean invoke(int SpriteNum) {
			USER u = pUser[SpriteNum];

			if ((u.WaitTics -= ACTORMOVETICS) < 0) {
				NewStateGroup(SpriteNum, u.ActorActionSet.Rise);
				u.ActorActionFunc = DoActorDecide;
				// InitActorDecide(SpriteNum);
				u.Flags &= ~(SPR_TARGETED);
			}

			return false;
		}
	};

	public static final Animator DoActorMoveJump = new Animator() {
		@Override
		public boolean invoke(int SpriteNum) {
			USER u = pUser[SpriteNum];
			SPRITE sp = pUser[SpriteNum].getSprite();
			int nx, ny;

			// Move while jumping

			nx = sp.xvel * sintable[NORM_ANGLE(sp.ang + 512)] >> 14;
			ny = sp.xvel * sintable[NORM_ANGLE(sp.ang)] >> 14;

			move_actor(SpriteNum, nx, ny, 0);

			if (!TEST(u.Flags, SPR_JUMPING | SPR_FALLING)) {
				InitActorDecide(SpriteNum);
			}
			return false;
		}
	};

	private static int move_scan(int SpriteNum, short ang, int dist, LONGp stopx, LONGp stopy) {
		USER u = pUser[SpriteNum];
		SPRITE sp = pUser[SpriteNum].getSprite();

		int nx, ny;
		int cliptype = CLIPMASK_ACTOR;
		int ret;

		short sang, ss;
		int x, y, z, loz, hiz;
		int lo_sp, hi_sp;
		short lo_sectp, hi_sectp;

		// moves out a bit but keeps the sprites original postion/sector.

		// save off position info
		x = sp.x;
		y = sp.y;
		z = sp.z;
		sang = sp.ang;
		loz = u.loz;
		hiz = u.hiz;
		lo_sp = u.lo_sp;
		hi_sp = u.hi_sp;
		lo_sectp = u.lo_sectp;
		hi_sectp = u.hi_sectp;
		ss = sp.sectnum;

		// do the move
		sp.ang = NORM_ANGLE(ang);
		nx = (dist) * sintable[NORM_ANGLE(sp.ang + 512)] >> 14;
		ny = (dist) * sintable[sp.ang] >> 14;

		ret = move_sprite(SpriteNum, nx, ny, 0, u.ceiling_dist, u.floor_dist, cliptype, 1);
		// move_sprite DOES do a getzrange point?

		// should I look down with a FAFgetzrange to see where I am?

		// remember where it stopped
		stopx.value = sp.x;
		stopy.value = sp.y;

		// reset position information
		sp.x = x;
		sp.y = y;
		sp.z = z;
		sp.ang = sang;
		u.loz = loz;
		u.hiz = hiz;
		u.lo_sp = lo_sp;
		u.hi_sp = hi_sp;
		u.lo_sectp = lo_sectp;
		u.hi_sectp = hi_sectp;
		engine.changespritesect(SpriteNum, ss);

		return (ret);
	}

	public static final int TOWARD = 1;
	public static final int AWAY = -1;

	private static short toward_angle_delta[][] = { { -160, -384, 160, 384, -256, 256, -512, 512, -99 },
			{ -384, -160, 384, 160, -256, 256, -512, 512, -99 }, { 160, 384, -160, -384, 256, -256, 512, -512, -99 },
			{ 384, 160, -384, -160, 256, -256, 512, -512, -99 } };

	private static short away_angle_delta[][] = { { -768, 768, -640, 640, -896, 896, 1024, -99 },
			{ 768, -768, 640, -640, -896, 896, 1024, -99 }, { 896, -896, -768, 768, -640, 640, 1024, -99 },
			{ 896, -896, 768, -768, 640, -640, 1024, -99 } };

	private static int FindNewAngle(int SpriteNum, int dir, int DistToMove) {
		USER u = pUser[SpriteNum];
		SPRITE sp = pUser[SpriteNum].getSprite();

		short[] adp = null;
		short new_ang, oang;
		short save_ang = -1;
		int ret;

		int dist, stopx, stopy;
		// start out with mininum distance that will be accepted as a move
		int save_dist = 500;

		// if on fire, run shorter distances
		if (ActorFlaming(SpriteNum))
			DistToMove = DIV4(DistToMove) + DIV8(DistToMove);

		// Find angle to from the player
		oang = NORM_ANGLE(engine.getangle(sprite[u.tgt_sp].x - sp.x, sprite[u.tgt_sp].y - sp.y));

		// choose a random angle array
		switch (dir) {
		case TOWARD:
			adp = toward_angle_delta[RANDOM_P2(4 << 8) >> 8];
			break;
		case AWAY:
			if (CanHitPlayer(SpriteNum))
				adp = toward_angle_delta[RANDOM_P2(4 << 8) >> 8];
			else
				adp = away_angle_delta[RANDOM_P2(4 << 8) >> 8];
			break;
		}

		for (int ptr = 0; adp[ptr] != -99; ptr++) {
			new_ang = NORM_ANGLE(oang + adp[ptr]);

			// look directly ahead for a ledge
			if (!TEST(u.Flags, SPR_NO_SCAREDZ | SPR_JUMPING | SPR_FALLING | SPR_SWIMMING | SPR_DEAD)) {
				sp.ang = new_ang;
				if (DropAhead(SpriteNum, u.lo_step)) {
					sp.ang = oang;
					continue;
				}
				sp.ang = oang;
			}

			// check to see how far we can move
			ret = move_scan(SpriteNum, new_ang, DistToMove, tmp_ptr[0], tmp_ptr[1]);

			stopx = tmp_ptr[0].value;
			stopy = tmp_ptr[1].value;

			if (ret == 0) {
				// cleanly moved in new direction without hitting something
				u.TargetDist = (short) Distance(sp.x, sp.y, stopx, stopy);
				return (new_ang);
			} else {
				// hit something
				dist = Distance(sp.x, sp.y, stopx, stopy);

				if (dist > save_dist) {
					save_ang = new_ang;
					save_dist = dist;
				}
			}
		}

		if (save_ang != -1) {
			u.TargetDist = (short) save_dist;

			// If actor moved to the TargetDist it would look like he was running
			// into things.

			// To keep this from happening make the TargetDist is less than the
			// point you would hit something

			if (u.TargetDist > 4000)
				u.TargetDist -= 3500;

			sp.ang = save_ang;
			return (save_ang);
		}

		return (-1);
	}

	/*
	 * 
	 * !AIC KEY - Reposition code is called throughout this file. What this does is
	 * pick a new direction close to the target direction (or away from the target
	 * direction if running away) and a distance to move in and tries to move there
	 * with move_scan(). If it hits something it will try again. No movement is
	 * actually acomplished here. This is just testing for clear paths to move in.
	 * Location variables that are changed are saved and reset. FindNewAngle() and
	 * move_scan() are two routines (above) that go with this. This is definately
	 * not called every time through the loop. It would be majorly slow.
	 * 
	 */

	private static int AwayDist[] = { 17000, 20000, 26000, 26000, 26000, 32000, 32000, 42000 };

	private static int TowardDist[] = { 10000, 15000, 20000, 20000, 25000, 30000, 35000, 40000 };

	private static int PlayerDist[] = { 2000, 3000, 3000, 5000, 5000, 5000, 9000, 9000 };

	public static final Animator InitActorReposition = new Animator() {
		@Override
		public boolean invoke(int SpriteNum) {

			USER u = pUser[SpriteNum];
			SPRITE sp = pUser[SpriteNum].getSprite();
			short ang;
			int rnum;
			int dist;

			u.Dist = 0;

			rnum = RANDOM_P2(8 << 8) >> 8;
			dist = Distance(sp.x, sp.y, sprite[u.tgt_sp].x, sprite[u.tgt_sp].y);

			if (dist < PlayerDist[rnum] || TEST(u.Flags, SPR_RUN_AWAY)) {
				rnum = RANDOM_P2(8 << 8) >> 8;
				ang = (short) FindNewAngle(SpriteNum, AWAY, AwayDist[rnum]);
				if (ang == -1) {
					u.Vis = 8;
					InitActorPause(SpriteNum);
					return false;
				}

				sp.ang = ang;
				DoActorSetSpeed(SpriteNum, FAST_SPEED);
				u.Flags &= ~(SPR_RUN_AWAY);
			} else {
				// try to move toward player
				rnum = RANDOM_P2(8 << 8) >> 8;
				ang = (short) FindNewAngle(SpriteNum, TOWARD, TowardDist[rnum]);
				if (ang == -1) {
					// try to move away from player
					rnum = RANDOM_P2(8 << 8) >> 8;
					ang = (short) FindNewAngle(SpriteNum, AWAY, AwayDist[rnum]);
					if (ang == -1) {
						u.Vis = 8;
						InitActorPause(SpriteNum);
						return false;
					}
				} else {
					// pick random speed to move toward the player
					if (RANDOM_P2(1024) < 512)
						DoActorSetSpeed(SpriteNum, NORM_SPEED);
					else
						DoActorSetSpeed(SpriteNum, MID_SPEED);
				}

				sp.ang = ang;
			}

			u.ActorActionFunc = DoActorReposition;
			if (!TEST(u.Flags, SPR_SWIMMING))
				NewStateGroup(SpriteNum, u.ActorActionSet.Run);

			(u.ActorActionFunc).invoke(SpriteNum);

			return false;
		}
	};

	public static final Animator DoActorReposition = new Animator() {
		@Override
		public boolean invoke(int SpriteNum) {
			USER u = pUser[SpriteNum];
			SPRITE sp = pUser[SpriteNum].getSprite();
			int nx, ny;

			nx = sp.xvel * sintable[NORM_ANGLE(sp.ang + 512)] >> 14;
			ny = sp.xvel * sintable[NORM_ANGLE(sp.ang)] >> 14;

			// still might hit something and have to handle it.
			if (!move_actor(SpriteNum, nx, ny, 0)) {
				if (ActorMoveHitReact(SpriteNum))
					return false;

				u.Vis = 6;
				InitActorPause(SpriteNum);
				return false;
			}

			// if close to target distance do a Decision again
			if (u.TargetDist < 50) {
				InitActorDecide(SpriteNum);
			}

			return false;
		}
	};

	public static int InitActorPause(int SpriteNum) {
		USER u = pUser[SpriteNum];

		u.ActorActionFunc = DoActorPause;

		(u.ActorActionFunc).invoke(SpriteNum);

		return (0);
	}

	public static final Animator DoActorPause = new Animator() {
		@Override
		public boolean invoke(int SpriteNum) {
			USER u = pUser[SpriteNum];

			// Using Vis instead of WaitTics, var name sucks, but it's the same type
			// WaitTics is used by too much other actor code and causes problems here
			if ((u.Vis -= ACTORMOVETICS) < 0) {
				u.ActorActionFunc = DoActorDecide;
				u.Flags &= ~(SPR_TARGETED);
			}

			return false;
		}
	};
	
	public static void AiSaveable()
	{
		SaveData(InitActorDecide);
		SaveData(DoActorDecide);
		SaveData(InitActorAlertNoise);
		SaveData(InitActorAmbientNoise);
		SaveData(InitActorAttackNoise);
		SaveData(InitActorPainNoise);
		SaveData(InitActorDieNoise);
		SaveData(InitActorExtra1Noise);
		SaveData(InitActorExtra2Noise);
		SaveData(InitActorExtra3Noise);
		SaveData(InitActorExtra4Noise);
		SaveData(InitActorExtra5Noise);
		SaveData(InitActorExtra6Noise);
		SaveData(InitActorMoveCloser);
		SaveData(DoActorMoveCloser);
		SaveData(InitActorRunAway);
		SaveData(InitActorRunToward);
		SaveData(InitActorAttack);
		SaveData(DoActorAttack);
		SaveData(InitActorEvade);
		SaveData(InitActorWanderAround);
		SaveData(InitActorFindPlayer);
		SaveData(InitActorDuck);
		SaveData(DoActorDuck);
		SaveData(DoActorMoveJump);
		SaveData(InitActorReposition);
		SaveData(DoActorReposition);
		SaveData(DoActorPause);
	}
}
