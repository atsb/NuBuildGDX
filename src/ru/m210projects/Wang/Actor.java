package ru.m210projects.Wang;

import static ru.m210projects.Build.Engine.CEIL;
import static ru.m210projects.Build.Engine.FLOOR;
import static ru.m210projects.Build.Engine.sector;
import static ru.m210projects.Build.Engine.sintable;
import static ru.m210projects.Build.Engine.sprite;
import static ru.m210projects.Build.Pragmas.klabs;
import static ru.m210projects.Wang.Digi.DIGI_ACTORBODYFALL1;
import static ru.m210projects.Wang.Digi.DIGI_ACTORHITGROUND;
import static ru.m210projects.Wang.Digi.DIGI_NINJAINHALF;
import static ru.m210projects.Wang.Enemies.Ninja.s_NinjaDieSliced;
import static ru.m210projects.Wang.Game.tmp_ptr;
import static ru.m210projects.Wang.Gameutils.ACTORMOVETICS;
import static ru.m210projects.Wang.Gameutils.ACTOR_GRAVITY;
import static ru.m210projects.Wang.Gameutils.CSTAT_SPRITE_BLOCK;
import static ru.m210projects.Wang.Gameutils.CSTAT_SPRITE_BLOCK_HITSCAN;
import static ru.m210projects.Wang.Gameutils.CSTAT_SPRITE_BREAKABLE;
import static ru.m210projects.Wang.Gameutils.CSTAT_SPRITE_FLOOR;
import static ru.m210projects.Wang.Gameutils.CSTAT_SPRITE_YCENTER;
import static ru.m210projects.Wang.Gameutils.CSTAT_SPRITE_YFLIP;
import static ru.m210projects.Wang.Gameutils.NORM_ANGLE;
import static ru.m210projects.Wang.Gameutils.PIC_SIZY;
import static ru.m210projects.Wang.Gameutils.RANDOM_P2;
import static ru.m210projects.Wang.Gameutils.RANDOM_RANGE;
import static ru.m210projects.Wang.Gameutils.SECTFU_DAMAGE_ABOVE_SECTOR;
import static ru.m210projects.Wang.Gameutils.SECTFX_CURRENT;
import static ru.m210projects.Wang.Gameutils.SECTFX_SINK;
import static ru.m210projects.Wang.Gameutils.SPRITE_SIZE_Z;
import static ru.m210projects.Wang.Gameutils.SPRITEp_BOS;
import static ru.m210projects.Wang.Gameutils.SPRX_BREAKABLE;
import static ru.m210projects.Wang.Gameutils.SPR_DEAD;
import static ru.m210projects.Wang.Gameutils.SPR_FALLING;
import static ru.m210projects.Wang.Gameutils.SPR_JUMPING;
import static ru.m210projects.Wang.Gameutils.SPR_MOVED;
import static ru.m210projects.Wang.Gameutils.SPR_SLIDING;
import static ru.m210projects.Wang.Gameutils.SPR_SWIMMING;
import static ru.m210projects.Wang.Gameutils.WPN_FIST;
import static ru.m210projects.Wang.Gameutils.WPN_NM_LAVA;
import static ru.m210projects.Wang.Gameutils.WPN_NM_SECTOR_SQUISH;
import static ru.m210projects.Wang.Gameutils.Z;
import static ru.m210projects.Wang.Gameutils.pUser;
import static ru.m210projects.Wang.Gameutils.synctics;
import static ru.m210projects.Wang.Main.engine;
import static ru.m210projects.Wang.Names.BETTY_R0;
import static ru.m210projects.Wang.Names.BUNNY_RUN_R0;
import static ru.m210projects.Wang.Names.COOLG_RUN_R0;
import static ru.m210projects.Wang.Names.COOLIE_RUN_R0;
import static ru.m210projects.Wang.Names.EEL_RUN_R0;
import static ru.m210projects.Wang.Names.HORNET_RUN_R0;
import static ru.m210projects.Wang.Names.NINJA_RUN_R0;
import static ru.m210projects.Wang.Names.RIPPER2_RUN_R0;
import static ru.m210projects.Wang.Names.RIPPER_RUN_R0;
import static ru.m210projects.Wang.Names.SKEL_RUN_R0;
import static ru.m210projects.Wang.Names.SKULL_R0;
import static ru.m210projects.Wang.Names.STAR1;
import static ru.m210projects.Wang.Names.STAT_DEAD_ACTOR;
import static ru.m210projects.Wang.Names.SUMO_RUN_R0;
import static ru.m210projects.Wang.Names.UZI_SMOKE;
import static ru.m210projects.Wang.Names.ZILLA_RUN_R0;
import static ru.m210projects.Wang.Rooms.FAFgetzrangepoint;
import static ru.m210projects.Wang.Rooms.zofslope;
import static ru.m210projects.Wang.Sector.SectUser;
import static ru.m210projects.Wang.Shrap.SpawnShrap;
import static ru.m210projects.Wang.Sound.PlaySound;
import static ru.m210projects.Wang.Sound.v3df_none;
import static ru.m210projects.Wang.Sprites.ChangeState;
import static ru.m210projects.Wang.Sprites.Debris;
import static ru.m210projects.Wang.Sprites.KillSprite;
import static ru.m210projects.Wang.Sprites.NewStateGroup;
import static ru.m210projects.Wang.Sprites.SetOwner;
import static ru.m210projects.Wang.Sprites.SpawnSprite;
import static ru.m210projects.Wang.Sprites.change_sprite_stat;
import static ru.m210projects.Wang.Sprites.move_actor;
import static ru.m210projects.Wang.Sprites.move_sprite;
import static ru.m210projects.Wang.Type.MyTypes.DIV2;
import static ru.m210projects.Wang.Type.MyTypes.DIV4;
import static ru.m210projects.Wang.Type.MyTypes.TEST;
import static ru.m210projects.Wang.Weapon.DoFindGroundPoint;
import static ru.m210projects.Wang.Weapon.InitPlasmaFountain;
import static ru.m210projects.Wang.Weapon.SetSuicide;
import static ru.m210projects.Wang.Weapon.UpdateSinglePlayKills;

import ru.m210projects.Build.Types.SECTOR;
import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Wang.Type.Animator;
import ru.m210projects.Wang.Type.Sect_User;
import ru.m210projects.Wang.Type.USER;

public class Actor {

	public static int DoScaleSprite(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];
		USER u = pUser[SpriteNum];
		int scale_value;

		if (u.scale_speed != 0) {
			u.scale_value += u.scale_speed * ACTORMOVETICS;

			scale_value = u.scale_value >> 8;

			if (u.scale_speed > 0) {
				if (scale_value > u.scale_tgt)
					u.scale_speed = 0;
				else
					sp.xrepeat = sp.yrepeat = (short) scale_value;
			} else {
				if (scale_value < u.scale_tgt)
					u.scale_speed = 0;
				else
					sp.xrepeat = sp.yrepeat = (short) scale_value;
			}

		}

		return (0);
	}

	public static int DoActorDie(int SpriteNum, int weapon) {
		USER u = pUser[SpriteNum];
		SPRITE sp = sprite[SpriteNum];

		change_sprite_stat(SpriteNum, STAT_DEAD_ACTOR);
		u.Flags |= (SPR_DEAD);
		u.Flags &= ~(SPR_FALLING | SPR_JUMPING);
		u.floor_dist = (short) Z(40);

		// test for gibable dead bodies
		sp.extra |= (SPRX_BREAKABLE);
		sp.cstat |= (CSTAT_SPRITE_BREAKABLE);

		if (weapon < 0) {
			// killed by one of these non-sprites
			switch (weapon) {
			case WPN_NM_LAVA:
				ChangeState(SpriteNum, u.StateEnd);
				u.RotNum = 0;
				break;

			case WPN_NM_SECTOR_SQUISH:
				ChangeState(SpriteNum, u.StateEnd);
				u.RotNum = 0;
				break;
			}

			return (0);
		}

		// killed by one of these sprites
		switch (pUser[weapon].ID) {
		// Coolie actually explodes himself
		// he is the Sprite AND Weapon
		case COOLIE_RUN_R0:
			ChangeState(SpriteNum, u.StateEnd);
			u.RotNum = 0;
			sp.xvel <<= 1;
			u.ActorActionFunc = null;
			sprite[SpriteNum].ang = (short) NORM_ANGLE(sprite[SpriteNum].ang + 1024);
			break;

		case NINJA_RUN_R0:
			if (u.ID == NINJA_RUN_R0) // Cut in half!
			{
				SPRITE wp = sprite[weapon];

				if (pUser[weapon].WeaponNum != WPN_FIST) {
					InitPlasmaFountain(wp, SpriteNum);
					InitPlasmaFountain(wp, SpriteNum);
					PlaySound(DIGI_NINJAINHALF, sp, v3df_none);
					ChangeState(SpriteNum, s_NinjaDieSliced[0]);
				} else {
					if (RANDOM_RANGE(1000) > 500) {
						InitPlasmaFountain(sprite[weapon], SpriteNum);
					}

					ChangeState(SpriteNum, u.StateEnd);
					u.RotNum = 0;
					u.ActorActionFunc = null;
					sp.xvel = (short) (200 + RANDOM_RANGE(200));
					u.jump_speed = (short) (-200 - RANDOM_RANGE(250));
					DoActorBeginJump(SpriteNum);
					sprite[SpriteNum].ang = sprite[weapon].ang;
				}
			} else {
				// test for gibable dead bodies
				if (RANDOM_RANGE(1000) > 500)
					sp.cstat |= (CSTAT_SPRITE_YFLIP);
				ChangeState(SpriteNum, u.StateEnd);
				sp.xvel = 0;
				u.jump_speed = 0;
				DoActorBeginJump(SpriteNum);
			}

			u.RotNum = 0;

			u.ActorActionFunc = null;
			sprite[SpriteNum].ang = sprite[weapon].ang;
			break;

		case COOLG_RUN_R0:
		case SKEL_RUN_R0:
		case RIPPER_RUN_R0:
		case RIPPER2_RUN_R0:
		case EEL_RUN_R0:
		case STAR1:
		case SUMO_RUN_R0:
			ChangeState(SpriteNum, u.StateEnd);
			u.RotNum = 0;
			break;

		case UZI_SMOKE:
			if (RANDOM_RANGE(1000) > 500)
				sp.cstat |= (CSTAT_SPRITE_YFLIP);
			ChangeState(SpriteNum, u.StateEnd);
			u.RotNum = 0;
			// Rippers still gotta jump or they fall off walls weird
			if (u.ID == RIPPER_RUN_R0 || u.ID == RIPPER2_RUN_R0) {
				sp.xvel <<= 1;
				u.jump_speed = (short) (-100 - RANDOM_RANGE(250));
				DoActorBeginJump(SpriteNum);
			} else {
				sp.xvel = 0;
				u.jump_speed = (short) (-10 - RANDOM_RANGE(25));
				DoActorBeginJump(SpriteNum);
			}
			u.ActorActionFunc = null;
			// Get angle to player
			sp.ang = (short) NORM_ANGLE(engine.getangle(sprite[u.tgt_sp].x - sp.x, sprite[u.tgt_sp].y - sp.y) + 1024);
			break;

		case UZI_SMOKE + 1: // Shotgun
			if (RANDOM_RANGE(1000) > 500)
				sp.cstat |= (CSTAT_SPRITE_YFLIP);
			ChangeState(SpriteNum, u.StateEnd);
			u.RotNum = 0;

			// Rippers still gotta jump or they fall off walls weird
			if (u.ID == RIPPER_RUN_R0 || u.ID == RIPPER2_RUN_R0) {
				sp.xvel = (short) (75 + RANDOM_RANGE(100));
				u.jump_speed = (short) (-100 - RANDOM_RANGE(150));
			} else {
				sp.xvel = (short) (100 + RANDOM_RANGE(200));
				u.jump_speed = (short) (-100 - RANDOM_RANGE(250));
			}
			DoActorBeginJump(SpriteNum);
			u.ActorActionFunc = null;
			// Get angle to player
			sp.ang = (short) NORM_ANGLE(engine.getangle(sprite[u.tgt_sp].x - sp.x, sprite[u.tgt_sp].y - sp.y) + 1024);
			break;

		default:
			switch (u.ID) {
			case SKULL_R0:
			case BETTY_R0:
				ChangeState(SpriteNum, u.StateEnd);
				break;

			default:
				if (RANDOM_RANGE(1000) > 700) {
					InitPlasmaFountain(sprite[weapon], SpriteNum);
				}

				if (RANDOM_RANGE(1000) > 500)
					sp.cstat |= (CSTAT_SPRITE_YFLIP);
				ChangeState(SpriteNum, u.StateEnd);
				u.RotNum = 0;
				u.ActorActionFunc = null;
				sp.xvel = (short) (300 + RANDOM_RANGE(400));
				u.jump_speed = (short) (-300 - RANDOM_RANGE(350));
				DoActorBeginJump(SpriteNum);
				sprite[SpriteNum].ang = sprite[weapon].ang;
				break;
			}
			break;
		}

		// These are too big to flip upside down
		switch (u.ID) {
		case RIPPER2_RUN_R0:
		case COOLIE_RUN_R0:
		case SUMO_RUN_R0:
		case ZILLA_RUN_R0:
			sp.cstat &= ~(CSTAT_SPRITE_YFLIP);
			break;
		}

		u.ID = 0;

		return (0);
	}

	public static void DoDebrisCurrent(int spnum) {
		int nx, ny;
		int ret = 0;
		SPRITE sp = sprite[spnum];
		USER u = pUser[spnum];
		Sect_User sectu = SectUser[sp.sectnum];

		nx = (int) (DIV4(sectu.speed) * (long) sintable[NORM_ANGLE(sectu.ang + 512)] >> 14);
		ny = (int) (DIV4(sectu.speed) * (long) sintable[sectu.ang] >> 14);

		// faster than move_sprite
		// move_missile(sp-sprite, nx, ny, 0, Z(2), Z(0), 0, ACTORMOVETICS);
		ret = move_sprite(spnum, nx, ny, 0, u.ceiling_dist, u.floor_dist, 0, ACTORMOVETICS);

		// attempt to move away from wall
		if (ret != 0) {
			short rang = (short) RANDOM_P2(2048);

			nx = (int) (DIV4(sectu.speed) * (long) sintable[NORM_ANGLE(sectu.ang + rang + 512)] >> 14);
			ny = (int) (DIV4(sectu.speed) * (long) sintable[NORM_ANGLE(sectu.ang + rang)] >> 14);

			move_sprite(spnum, nx, ny, 0, u.ceiling_dist, u.floor_dist, 0, ACTORMOVETICS);
		}

		sp.z = u.loz;
	}

	public static boolean DoActorSectorDamage(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];
		USER u = pUser[SpriteNum];
		Sect_User sectu = SectUser[sp.sectnum];
		SECTOR sectp = sector[sp.sectnum];

		if (u.Health <= 0)
			return (false);

		if (sectu != null && sectu.damage != 0) {
			if (TEST(sectu.flags, SECTFU_DAMAGE_ABOVE_SECTOR)) {
				if ((u.DamageTics -= synctics) < 0) {
					u.DamageTics = 60;
					u.Health -= sectu.damage;

					if (u.Health <= 0) {
						UpdateSinglePlayKills(SpriteNum, null);
						DoActorDie(SpriteNum, WPN_NM_LAVA);
						return (true);
					}
				}
			} else if (SPRITEp_BOS(sp) >= sectp.floorz) {
				if ((u.DamageTics -= synctics) < 0) {
					u.DamageTics = 60;
					u.Health -= sectu.damage;

					if (u.Health <= 0) {
						UpdateSinglePlayKills(SpriteNum, null);
						DoActorDie(SpriteNum, WPN_NM_LAVA);
						return (true);
					}
				}
			}
		}

		// note that most squishing is done in vator.c
		if (u.lo_sectp != -1 && u.hi_sectp != -1 && klabs(u.loz - u.hiz) < DIV2(SPRITE_SIZE_Z(SpriteNum))) {
			u.Health = 0;
			if (SpawnShrap(SpriteNum, WPN_NM_SECTOR_SQUISH)) {
				UpdateSinglePlayKills(SpriteNum, null);
				SetSuicide(SpriteNum);
			}

			return (true);
		}

		return (false);
	}

	public static boolean move_debris(int SpriteNum, int xchange, int ychange, int zchange) {
		USER u = pUser[SpriteNum];
		u.ret = move_sprite(SpriteNum, xchange, ychange, zchange, u.ceiling_dist, u.floor_dist, 0, ACTORMOVETICS);

		return (u.ret == 0);
	}

	// !AIC - Supposed to allow floating of DEBRIS (dead bodies, flotsam, jetsam).
	// Or if water has
	// current move with the current.

	public static final Animator DoActorDebris = new Animator() {
		@Override
		public boolean invoke(int spr) {
			return DoActorDebris(spr) != 0;
		}
	};

	public static int DoActorDebris(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];
		USER u = pUser[SpriteNum];
		SECTOR sectp = sector[sp.sectnum];
		int nx, ny;

		// This was move from DoActorDie so actor's can't be walked through until they
		// are on the floor
		sp.cstat &= ~(CSTAT_SPRITE_BLOCK | CSTAT_SPRITE_BLOCK_HITSCAN);

		// Don't let some actors float
		switch (u.ID) {
		case HORNET_RUN_R0:
		case BUNNY_RUN_R0:
			KillSprite(SpriteNum);
			return (0);
		case ZILLA_RUN_R0:
			engine.getzsofslope(sp.sectnum, sp.x, sp.y, zofslope);
			u.hiz = zofslope[CEIL];
			u.loz = zofslope[FLOOR];

			u.lo_sectp = sp.sectnum;
			u.hi_sectp = sp.sectnum;
			u.lo_sp = -1;
			u.hi_sp = -1;
			break;
		}

		if (TEST(sectp.extra, SECTFX_SINK)) {
			if (TEST(sectp.extra, SECTFX_CURRENT)) {
				DoDebrisCurrent(SpriteNum);
			} else {
				nx = ACTORMOVETICS * sintable[NORM_ANGLE(sp.ang + 512)] >> 14;
				ny = ACTORMOVETICS * sintable[sp.ang] >> 14;

				if (!move_debris(SpriteNum, nx, ny, 0)) {
					sp.ang = (short) RANDOM_P2(2048);
				}
			}

			if (SectUser[sp.sectnum] != null && SectUser[sp.sectnum].depth > 10) {
				u.WaitTics = (short) ((u.WaitTics + (ACTORMOVETICS << 3)) & 1023);
				sp.z = u.loz - ((Z(2) * sintable[u.WaitTics]) >> 14);
			}
		} else {
			sp.z = u.loz;
		}

		return (0);
	}

	public static int DoFireFly(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];
		USER u = pUser[SpriteNum];
		int nx, ny;

		nx = 4 * ACTORMOVETICS * sintable[NORM_ANGLE(sp.ang + 512)] >> 14;
		ny = 4 * ACTORMOVETICS * sintable[sp.ang] >> 14;

		sp.clipdist = 256 >> 2;
		if (!move_actor(SpriteNum, nx, ny, 0)) {
			sp.ang = (short) NORM_ANGLE(sp.ang + 1024);
		}

		u.WaitTics = (short) ((u.WaitTics + (ACTORMOVETICS << 1)) & 2047);

		sp.z = u.sz + ((Z(32) * sintable[u.WaitTics]) >> 14);
		return (0);
	}

	public static int DoGenerateSewerDebris(short SpriteNum) {
		SPRITE sp = sprite[SpriteNum];
		USER u = pUser[SpriteNum];
		short n;

		u.Tics -= ACTORMOVETICS;

		if (u.Tics <= 0) {
			u.Tics = u.WaitTics;

			n = (short) SpawnSprite(STAT_DEAD_ACTOR, 0, Debris[RANDOM_P2(4 << 8) >> 8], sp.sectnum, sp.x, sp.y, sp.z,
					sp.ang, 200);
			SetOwner(SpriteNum, n);
		}

		return (0);
	}

	// !AIC - Tries to keep actors correctly on the floor. More that a bit messy.

	public static void KeepActorOnFloor(int SpriteNum) {
		USER u = pUser[SpriteNum];
		SPRITE sp = pUser[SpriteNum].getSprite();
		SECTOR sectp;
		int depth;

		sectp = sector[sp.sectnum];

		sp.cstat &= ~(CSTAT_SPRITE_YFLIP); // If upside down, reset it

		if (TEST(u.Flags, SPR_JUMPING | SPR_FALLING))
			return;

		if (u.lo_sectp != -1 && SectUser[u.lo_sectp] != null)
			depth = SectUser[u.lo_sectp].depth;
		else
			depth = 0;

		if (TEST(sectp.extra, SECTFX_SINK) && depth > 35 && u.ActorActionSet != null && u.ActorActionSet.Swim != null) {
			if (TEST(u.Flags, SPR_SWIMMING)) {
				if (u.Rot != u.ActorActionSet.Run && u.Rot != u.ActorActionSet.Swim
						&& u.Rot != u.ActorActionSet.Stand) {
					// was swimming but have now stopped
					u.Flags &= ~(SPR_SWIMMING);
					sp.cstat &= ~(CSTAT_SPRITE_YCENTER);
					u.oz = sp.z = u.loz;
					return;
				}

				if (u.Rot == u.ActorActionSet.Run) {
					NewStateGroup(SpriteNum, u.ActorActionSet.Swim);
				}

				// are swimming
				u.oz = sp.z = u.loz - Z(depth);
			} else {
				// only start swimming if you are running
				if (u.Rot == u.ActorActionSet.Run || u.Rot == u.ActorActionSet.Swim) {
					NewStateGroup(SpriteNum, u.ActorActionSet.Swim);
					u.oz = sp.z = u.loz - Z(depth);
					u.Flags |= (SPR_SWIMMING);
					sp.cstat |= (CSTAT_SPRITE_YCENTER);
				} else {
					u.Flags &= ~(SPR_SWIMMING);
					sp.cstat &= ~(CSTAT_SPRITE_YCENTER);
					u.oz = sp.z = u.loz;
				}
			}

			return;
		}

		// NOT in a swimming situation
		u.Flags &= ~(SPR_SWIMMING);
		sp.cstat &= ~(CSTAT_SPRITE_YCENTER);

		if (TEST(u.Flags, SPR_MOVED)) {
			u.oz = sp.z = u.loz;
		} else {
			FAFgetzrangepoint(sp.x, sp.y, sp.z, sp.sectnum, tmp_ptr[0], tmp_ptr[1], tmp_ptr[2].set(0), tmp_ptr[3]);
			u.oz = sp.z = tmp_ptr[2].value;
		}
	}

	public static int DoActorBeginSlide(int SpriteNum, int ang, int vel, int dec) {
		USER u = pUser[SpriteNum];
		u.Flags |= (SPR_SLIDING);

		u.slide_ang = (short) ang;
		u.slide_vel = vel;
		u.slide_dec = (short) dec;

		return (0);
	}

	// !AIC - Sliding can occur in different directions from movement of the actor.
	// Has its own set of variables

	public static boolean DoActorSlide(int SpriteNum) {
		USER u = pUser[SpriteNum];
		int nx, ny;

		nx = u.slide_vel * sintable[NORM_ANGLE(u.slide_ang + 512)] >> 14;
		ny = u.slide_vel * sintable[u.slide_ang] >> 14;

		if (!move_actor(SpriteNum, nx, ny, 0)) {
			u.Flags &= ~(SPR_SLIDING);
			return (false);
		}

		u.slide_vel -= u.slide_dec * ACTORMOVETICS;

		if (u.slide_vel < 20) {
			u.Flags &= ~(SPR_SLIDING);
		}

		return (true);
	}

	// !AIC - Actor jumping and falling

	public static int DoActorBeginJump(int SpriteNum) {
		USER u = pUser[SpriteNum];

		u.Flags |= (SPR_JUMPING);
		u.Flags &= ~(SPR_FALLING);

		// u.jump_speed = should be set before calling

		// set up individual actor jump gravity
		u.jump_grav = ACTOR_GRAVITY;

		// Change sprites state to jumping
		if (u.ActorActionSet != null) {
			if (TEST(u.Flags, SPR_DEAD))
				NewStateGroup(SpriteNum, u.ActorActionSet.DeathJump);
			else
				NewStateGroup(SpriteNum, u.ActorActionSet.Jump);
		}
		u.StateFallOverride = null;

		// DO NOT CALL DoActorJump! DoActorStopFall can cause an infinite loop and
		// stack overflow if it is called.
		// DoActorJump(SpriteNum);

		return (0);
	}

	public static int DoActorJump(int SpriteNum) {
		USER u = pUser[SpriteNum];
		SPRITE sp = pUser[SpriteNum].getSprite();

		int jump_adj;

		// precalculate jump value to adjust jump speed by
		jump_adj = u.jump_grav * ACTORMOVETICS;

		// adjust jump speed by gravity - if jump speed greater than 0 player
		// have started falling
		if ((u.jump_speed += jump_adj) > 0) {
			// Start falling
			DoActorBeginFall(SpriteNum);
			return (0);
		}

		// adjust height by jump speed
		sp.z += u.jump_speed * ACTORMOVETICS;

		// if player gets to close the ceiling while jumping
		if (sp.z < u.hiz + Z(PIC_SIZY(SpriteNum))) {
			// put player at the ceiling
			sp.z = u.hiz + Z(PIC_SIZY(SpriteNum));

			// reverse your speed to falling
			u.jump_speed = (short) -u.jump_speed;

			// Change sprites state to falling
			DoActorBeginFall(SpriteNum);
		}

		return (0);
	}

	public static int DoActorBeginFall(int SpriteNum) {
		USER u = pUser[SpriteNum];

		u.Flags |= (SPR_FALLING);
		u.Flags &= ~(SPR_JUMPING);

		u.jump_grav = ACTOR_GRAVITY;

		// Change sprites state to falling
		if (u.ActorActionSet != null) {
			if (TEST(u.Flags, SPR_DEAD)) {
				NewStateGroup(SpriteNum, u.ActorActionSet.DeathFall);
			} else
				NewStateGroup(SpriteNum, u.ActorActionSet.Fall);

			if (u.StateFallOverride != null) {
				NewStateGroup(SpriteNum, u.StateFallOverride);
			}
		}

		DoActorFall(SpriteNum);

		return (0);
	}

	public static int DoActorFall(int SpriteNum) {

		USER u = pUser[SpriteNum];
		SPRITE sp = u.getSprite();

		// adjust jump speed by gravity
		u.jump_speed += u.jump_grav * ACTORMOVETICS;

		// adjust player height by jump speed
		sp.z += u.jump_speed * ACTORMOVETICS;

		// Stick like glue when you hit the ground
		if (sp.z > u.loz) {
			DoActorStopFall(SpriteNum);
		}

		return (0);
	}

	public static int DoActorStopFall(int SpriteNum) {
		USER u = pUser[SpriteNum];
		SPRITE sp = u.getSprite();

		sp.z = u.loz;

		u.Flags &= ~(SPR_FALLING | SPR_JUMPING);
		sp.cstat &= ~(CSTAT_SPRITE_YFLIP);

		// don't stand on face or wall sprites - jump again
		if (u.lo_sp != -1 && !TEST(sprite[u.lo_sp].cstat, CSTAT_SPRITE_FLOOR)) {
			// sp.ang = NORM_ANGLE(sp.ang + (RANDOM_P2(64<<8)>>8) - 32);
			sp.ang = (short) NORM_ANGLE(sp.ang + 1024 + (RANDOM_P2(512 << 8) >> 8));
			u.jump_speed = -350;

			DoActorBeginJump(SpriteNum);
			return (0);
		}

		// Change sprites state to running
		if (u.ActorActionSet != null) {
			if (TEST(u.Flags, SPR_DEAD)) {
				NewStateGroup(SpriteNum, u.ActorActionSet.Dead);
				PlaySound(DIGI_ACTORBODYFALL1, sp, v3df_none);
			} else {
				PlaySound(DIGI_ACTORHITGROUND, sp, v3df_none);

				NewStateGroup(SpriteNum, u.ActorActionSet.Run);

				if ((u.track >= 0) && (u.jump_speed) > 800 && (u.ActorActionSet.Sit != null)) {
					u.WaitTics = 80;
					NewStateGroup(SpriteNum, u.ActorActionSet.Sit);
				}
			}
		}

		return (0);
	}

	public static int DoActorDeathMove(int SpriteNum) {
		USER u = pUser[SpriteNum];
		SPRITE sp = u.getSprite();
		int nx, ny;

		if (TEST(u.Flags, SPR_JUMPING | SPR_FALLING)) {
			if (TEST(u.Flags, SPR_JUMPING))
				DoActorJump(SpriteNum);
			else
				DoActorFall(SpriteNum);
		}

		nx = sp.xvel * sintable[NORM_ANGLE(sp.ang + 512)] >> 14;
		ny = sp.xvel * sintable[sp.ang] >> 14;

		sp.clipdist = (128 + 64) >> 2;
		move_actor(SpriteNum, nx, ny, 0);

		// only fall on top of floor sprite or sector
		DoFindGroundPoint(SpriteNum);

		return (0);
	}

	// !AIC - Jumping a falling for shrapnel and other stuff, not actors.

	public static int DoBeginJump(int SpriteNum) {
		USER u = pUser[SpriteNum];

		u.Flags |= (SPR_JUMPING);
		u.Flags &= ~(SPR_FALLING);

		// set up individual actor jump gravity
		u.jump_grav = ACTOR_GRAVITY;

		DoJump(SpriteNum);

		return (0);
	}

	public static int DoJump(int SpriteNum) {
		USER u = pUser[SpriteNum];
		SPRITE sp = u.getSprite();

		int jump_adj;

		// precalculate jump value to adjust jump speed by
		jump_adj = u.jump_grav * ACTORMOVETICS;

		// adjust jump speed by gravity - if jump speed greater than 0 player
		// have started falling
		if ((u.jump_speed += jump_adj) > 0) {
			// Start falling
			DoBeginFall(SpriteNum);
			return (0);
		}

		// adjust height by jump speed
		sp.z += u.jump_speed * ACTORMOVETICS;

		// if player gets to close the ceiling while jumping
		if (sp.z < u.hiz + Z(PIC_SIZY(SpriteNum))) {
			// put player at the ceiling
			sp.z = u.hiz + Z(PIC_SIZY(SpriteNum));

			// reverse your speed to falling
			u.jump_speed = (short) -u.jump_speed;

			// Change sprites state to falling
			DoBeginFall(SpriteNum);
		}

		return (0);
	}

	public static int DoBeginFall(int SpriteNum) {
		USER u = pUser[SpriteNum];

		u.Flags |= (SPR_FALLING);
		u.Flags &= ~(SPR_JUMPING);

		u.jump_grav = ACTOR_GRAVITY;

		DoFall(SpriteNum);

		return (0);
	}

	public static int DoFall(int SpriteNum) {
		USER u = pUser[SpriteNum];
		SPRITE sp = u.getSprite();

		// adjust jump speed by gravity
		u.jump_speed += u.jump_grav * ACTORMOVETICS;

		// adjust player height by jump speed
		sp.z += u.jump_speed * ACTORMOVETICS;

		// Stick like glue when you hit the ground
		if (sp.z > u.loz - u.floor_dist) {
			sp.z = u.loz - u.floor_dist;
			u.Flags &= ~(SPR_FALLING);
		}

		return (0);
	}

}
