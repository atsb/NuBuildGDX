package ru.m210projects.Wang;

import static ru.m210projects.Build.Engine.clipmove_sectnum;
import static ru.m210projects.Build.Engine.clipmove_x;
import static ru.m210projects.Build.Engine.clipmove_y;
import static ru.m210projects.Build.Engine.clipmove_z;
import static ru.m210projects.Build.Engine.headspritesect;
import static ru.m210projects.Build.Engine.headspritestat;
import static ru.m210projects.Build.Engine.neartag;
import static ru.m210projects.Build.Engine.nextspritesect;
import static ru.m210projects.Build.Engine.nextspritestat;
import static ru.m210projects.Build.Engine.numsectors;
import static ru.m210projects.Build.Engine.pHitInfo;
import static ru.m210projects.Build.Engine.sector;
import static ru.m210projects.Build.Engine.sintable;
import static ru.m210projects.Build.Engine.sprite;
import static ru.m210projects.Build.Engine.wall;
import static ru.m210projects.Build.Net.Mmulti.connecthead;
import static ru.m210projects.Build.Net.Mmulti.connectpoint2;
import static ru.m210projects.Build.Pragmas.klabs;
import static ru.m210projects.Wang.Actor.DoActorBeginJump;
import static ru.m210projects.Wang.Ai.DoActorDuck;
import static ru.m210projects.Wang.Ai.DoActorMoveJump;
import static ru.m210projects.Wang.Ai.DoActorPickClosePlayer;
import static ru.m210projects.Wang.Ai.DoActorSetSpeed;
import static ru.m210projects.Wang.Ai.FindTrackAwayFromPlayer;
import static ru.m210projects.Wang.Ai.FindTrackToPlayer;
import static ru.m210projects.Wang.Ai.InitActorDuck;
import static ru.m210projects.Wang.Ai.NORM_SPEED;
import static ru.m210projects.Wang.Ai.SLOW_SPEED;
import static ru.m210projects.Wang.Enemies.Ninja.NinjaJumpActionFunc;
import static ru.m210projects.Wang.Enemies.Ripper.PickJumpSpeed;
import static ru.m210projects.Wang.Factory.WangNetwork.Prediction;
import static ru.m210projects.Wang.Game.Distance;
import static ru.m210projects.Wang.Game.Player;
import static ru.m210projects.Wang.Game.tmp_ptr;
import static ru.m210projects.Wang.Game.totalsynctics;
import static ru.m210projects.Wang.Gameutils.*;
import static ru.m210projects.Wang.Main.engine;
import static ru.m210projects.Wang.Main.game;
import static ru.m210projects.Wang.Morth.ScaleRandomPoint;
import static ru.m210projects.Wang.Names.ST1;
import static ru.m210projects.Wang.Names.STAT_AMBIENT;
import static ru.m210projects.Wang.Names.STAT_CLIMB_MARKER;
import static ru.m210projects.Wang.Names.STAT_DEFAULT;
import static ru.m210projects.Wang.Names.STAT_DELETE_SPRITE;
import static ru.m210projects.Wang.Names.STAT_ENEMY;
import static ru.m210projects.Wang.Names.STAT_ITEM;
import static ru.m210projects.Wang.Names.STAT_MISC;
import static ru.m210projects.Wang.Names.STAT_NO_STATE;
import static ru.m210projects.Wang.Names.STAT_QUICK_DEFEND;
import static ru.m210projects.Wang.Names.STAT_QUICK_DUCK;
import static ru.m210projects.Wang.Names.STAT_QUICK_EXIT;
import static ru.m210projects.Wang.Names.STAT_QUICK_JUMP;
import static ru.m210projects.Wang.Names.STAT_QUICK_JUMP_DOWN;
import static ru.m210projects.Wang.Names.STAT_QUICK_LADDER;
import static ru.m210projects.Wang.Names.STAT_QUICK_OPERATE;
import static ru.m210projects.Wang.Names.STAT_QUICK_SCAN;
import static ru.m210projects.Wang.Names.STAT_QUICK_SUPER_JUMP;
import static ru.m210projects.Wang.Names.STAT_SOUND_SPOT;
import static ru.m210projects.Wang.Names.STAT_SO_SHOOT_POINT;
import static ru.m210projects.Wang.Names.STAT_SO_SP_CHILD;
import static ru.m210projects.Wang.Names.STAT_SPAWN_SPOT;
import static ru.m210projects.Wang.Names.STAT_SPAWN_TRIGGER;
import static ru.m210projects.Wang.Names.STAT_SPRITE_HIT_MATCH;
import static ru.m210projects.Wang.Names.STAT_ST1;
import static ru.m210projects.Wang.Names.STAT_TRACK;
import static ru.m210projects.Wang.Names.STAT_TRAP;
import static ru.m210projects.Wang.Names.STAT_WALLBLOOD_QUEUE;
import static ru.m210projects.Wang.Names.STAT_WALL_MOVE;
import static ru.m210projects.Wang.Player.DoPlayerZrange;
import static ru.m210projects.Wang.Player.FindMainSector;
import static ru.m210projects.Wang.Player.FindNearSprite;
import static ru.m210projects.Wang.Player.GetDeltaAngle;
import static ru.m210projects.Wang.Player.PLAYER_CRAWL_HEIGHT;
import static ru.m210projects.Wang.Player.PLAYER_HEIGHT;
import static ru.m210projects.Wang.Player.UpdatePlayerSprite;
import static ru.m210projects.Wang.Rooms.COVERinsertsprite;
import static ru.m210projects.Wang.Rooms.COVERupdatesector;
import static ru.m210projects.Wang.Rooms.FAFcansee;
import static ru.m210projects.Wang.Rooms.FAFhitscan;
import static ru.m210projects.Wang.Sector.AnimGetGoal;
import static ru.m210projects.Wang.Sector.AnimSet;
import static ru.m210projects.Wang.Sector.AnimSetCallback;
import static ru.m210projects.Wang.Sector.AnimSetVelAdj;
import static ru.m210projects.Wang.Sector.DoMatchEverything;
import static ru.m210projects.Wang.Sector.DoSoundSpotMatch;
import static ru.m210projects.Wang.Sector.DoSoundSpotStopSound;
import static ru.m210projects.Wang.Sector.DoSpawnSpotsForDamage;
import static ru.m210projects.Wang.Sector.NTAG_SEARCH_LO_HI;
import static ru.m210projects.Wang.Sector.OperateSector;
import static ru.m210projects.Wang.Sector.OperateSprite;
import static ru.m210projects.Wang.Sector.OperateWall;
import static ru.m210projects.Wang.Sector.SCALE_POINT_SPEED;
import static ru.m210projects.Wang.Sector.SO_SCALE_CYCLE;
import static ru.m210projects.Wang.Sector.SO_SCALE_NONE;
import static ru.m210projects.Wang.Sector.SO_SCALE_RANDOM_POINT;
import static ru.m210projects.Wang.Sector.SectUser;
import static ru.m210projects.Wang.Sector.SectorMidPoint;
import static ru.m210projects.Wang.Sprites.DoActorZrange;
import static ru.m210projects.Wang.Sprites.GetSectUser;
import static ru.m210projects.Wang.Sprites.KillSprite;
import static ru.m210projects.Wang.Sprites.MoveSkip2;
import static ru.m210projects.Wang.Sprites.NewStateGroup;
import static ru.m210projects.Wang.Sprites.SectorObject;
import static ru.m210projects.Wang.Sprites.SpawnSprite;
import static ru.m210projects.Wang.Sprites.SpawnUser;
import static ru.m210projects.Wang.Sprites.change_sprite_stat;
import static ru.m210projects.Wang.Sprites.move_sprite;
import static ru.m210projects.Wang.Stag.SECT_SO_CENTER;
import static ru.m210projects.Wang.Stag.SECT_SO_CLIP_DIST;
import static ru.m210projects.Wang.Stag.SECT_SO_DONT_ROTATE;
import static ru.m210projects.Wang.Stag.SECT_SO_FORM_WHIRLPOOL;
import static ru.m210projects.Wang.Stag.SECT_SO_SPRITE_OBJ;
import static ru.m210projects.Wang.Stag.SO_AMOEBA;
import static ru.m210projects.Wang.Stag.SO_ANGLE;
import static ru.m210projects.Wang.Stag.SO_AUTO_TURRET;
import static ru.m210projects.Wang.Stag.SO_BOB_START;
import static ru.m210projects.Wang.Stag.SO_CLIP_BOX;
import static ru.m210projects.Wang.Stag.SO_DRIVABLE_ATTRIB;
import static ru.m210projects.Wang.Stag.SO_FLOOR_MORPH;
import static ru.m210projects.Wang.Stag.SO_KILLABLE;
import static ru.m210projects.Wang.Stag.SO_LIMIT_TURN;
import static ru.m210projects.Wang.Stag.SO_MATCH_EVENT;
import static ru.m210projects.Wang.Stag.SO_MAX_DAMAGE;
import static ru.m210projects.Wang.Stag.SO_RAM_DAMAGE;
import static ru.m210projects.Wang.Stag.SO_SCALE_INFO;
import static ru.m210projects.Wang.Stag.SO_SCALE_POINT_INFO;
import static ru.m210projects.Wang.Stag.SO_SCALE_XY_MULT;
import static ru.m210projects.Wang.Stag.SO_SET_SPEED;
import static ru.m210projects.Wang.Stag.SO_SHOOT_POINT;
import static ru.m210projects.Wang.Stag.SO_SPIN;
import static ru.m210projects.Wang.Stag.SO_SPIN_REVERSE;
import static ru.m210projects.Wang.Stag.SO_SYNC1;
import static ru.m210projects.Wang.Stag.SO_SYNC2;
import static ru.m210projects.Wang.Stag.SO_TORNADO;
import static ru.m210projects.Wang.Stag.SO_TURN_SPEED;
import static ru.m210projects.Wang.Stag.SPAWN_SPOT;
import static ru.m210projects.Wang.Tags.TAG_ACTOR_TRACK_BEGIN;
import static ru.m210projects.Wang.Tags.TAG_ACTOR_TRACK_END;
import static ru.m210projects.Wang.Tags.TAG_OBJECT_CENTER;
import static ru.m210projects.Wang.Tags.TAG_WALL_ALIGN_SLOPE_TO_POINT;
import static ru.m210projects.Wang.Tags.TRACK_ACTOR_ATTACK1;
import static ru.m210projects.Wang.Tags.TRACK_ACTOR_ATTACK2;
import static ru.m210projects.Wang.Tags.TRACK_ACTOR_ATTACK3;
import static ru.m210projects.Wang.Tags.TRACK_ACTOR_ATTACK4;
import static ru.m210projects.Wang.Tags.TRACK_ACTOR_ATTACK5;
import static ru.m210projects.Wang.Tags.TRACK_ACTOR_ATTACK6;
import static ru.m210projects.Wang.Tags.TRACK_ACTOR_CLIMB_LADDER;
import static ru.m210projects.Wang.Tags.TRACK_ACTOR_CLOSE_ATTACK1;
import static ru.m210projects.Wang.Tags.TRACK_ACTOR_CLOSE_ATTACK2;
import static ru.m210projects.Wang.Tags.TRACK_ACTOR_CRAWL;
import static ru.m210projects.Wang.Tags.TRACK_ACTOR_DEATH1;
import static ru.m210projects.Wang.Tags.TRACK_ACTOR_DEATH2;
import static ru.m210projects.Wang.Tags.TRACK_ACTOR_DEATH_JUMP;
import static ru.m210projects.Wang.Tags.TRACK_ACTOR_FLY;
import static ru.m210projects.Wang.Tags.TRACK_ACTOR_JUMP;
import static ru.m210projects.Wang.Tags.TRACK_ACTOR_JUMP_IF_FORWARD;
import static ru.m210projects.Wang.Tags.TRACK_ACTOR_JUMP_IF_REVERSE;
import static ru.m210projects.Wang.Tags.TRACK_ACTOR_OPERATE;
import static ru.m210projects.Wang.Tags.TRACK_ACTOR_QUICK_DEFEND;
import static ru.m210projects.Wang.Tags.TRACK_ACTOR_QUICK_DUCK;
import static ru.m210projects.Wang.Tags.TRACK_ACTOR_QUICK_JUMP;
import static ru.m210projects.Wang.Tags.TRACK_ACTOR_QUICK_JUMP_DOWN;
import static ru.m210projects.Wang.Tags.TRACK_ACTOR_QUICK_OPERATE;
import static ru.m210projects.Wang.Tags.TRACK_ACTOR_QUICK_SCAN;
import static ru.m210projects.Wang.Tags.TRACK_ACTOR_QUICK_SUPER_JUMP;
import static ru.m210projects.Wang.Tags.TRACK_ACTOR_REVERSE;
import static ru.m210projects.Wang.Tags.TRACK_ACTOR_SET_JUMP;
import static ru.m210projects.Wang.Tags.TRACK_ACTOR_SIT;
import static ru.m210projects.Wang.Tags.TRACK_ACTOR_SLOW_DOWN;
import static ru.m210projects.Wang.Tags.TRACK_ACTOR_SPEED_UP;
import static ru.m210projects.Wang.Tags.TRACK_ACTOR_STAND;
import static ru.m210projects.Wang.Tags.TRACK_ACTOR_SWIM;
import static ru.m210projects.Wang.Tags.TRACK_ACTOR_VEL_RATE;
import static ru.m210projects.Wang.Tags.TRACK_ACTOR_WAIT_FOR_PLAYER;
import static ru.m210projects.Wang.Tags.TRACK_ACTOR_WAIT_FOR_TRIGGER;
import static ru.m210projects.Wang.Tags.TRACK_ACTOR_ZDIFF_MODE;
import static ru.m210projects.Wang.Tags.TRACK_BOB_SPEED;
import static ru.m210projects.Wang.Tags.TRACK_BOB_START;
import static ru.m210projects.Wang.Tags.TRACK_BOB_STOP;
import static ru.m210projects.Wang.Tags.TRACK_END;
import static ru.m210projects.Wang.Tags.TRACK_MATCH_EVERYTHING;
import static ru.m210projects.Wang.Tags.TRACK_MATCH_EVERYTHING_ONCE;
import static ru.m210projects.Wang.Tags.TRACK_MOVE_VERTICAL;
import static ru.m210projects.Wang.Tags.TRACK_REVERSE;
import static ru.m210projects.Wang.Tags.TRACK_SET_SPEED;
import static ru.m210projects.Wang.Tags.TRACK_SLOW_DOWN;
import static ru.m210projects.Wang.Tags.TRACK_SO_FORM_WHIRLPOOL;
import static ru.m210projects.Wang.Tags.TRACK_SO_SINK;
import static ru.m210projects.Wang.Tags.TRACK_SPEED_UP;
import static ru.m210projects.Wang.Tags.TRACK_SPIN;
import static ru.m210projects.Wang.Tags.TRACK_SPIN_REVERSE;
import static ru.m210projects.Wang.Tags.TRACK_SPIN_STOP;
import static ru.m210projects.Wang.Tags.TRACK_START;
import static ru.m210projects.Wang.Tags.TRACK_STOP;
import static ru.m210projects.Wang.Tags.TRACK_VEL_RATE;
import static ru.m210projects.Wang.Tags.TRACK_WAIT_FOR_EVENT;
import static ru.m210projects.Wang.Tags.TRACK_ZDIFF_MODE;
import static ru.m210projects.Wang.Tags.TRACK_ZDOWN;
import static ru.m210projects.Wang.Tags.TRACK_ZRATE;
import static ru.m210projects.Wang.Tags.TRACK_ZUP;
import static ru.m210projects.Wang.Tags.TT_DUCK_N_SHOOT;
import static ru.m210projects.Wang.Tags.TT_EXIT;
import static ru.m210projects.Wang.Tags.TT_HIDE_N_SHOOT;
import static ru.m210projects.Wang.Tags.TT_JUMP_DOWN;
import static ru.m210projects.Wang.Tags.TT_JUMP_UP;
import static ru.m210projects.Wang.Tags.TT_LADDER;
import static ru.m210projects.Wang.Tags.TT_OPERATE;
import static ru.m210projects.Wang.Tags.TT_SCAN;
import static ru.m210projects.Wang.Tags.TT_SUPER_JUMP_UP;
import static ru.m210projects.Wang.Tags.TT_TRAVERSE;
import static ru.m210projects.Wang.Type.MyTypes.BETWEEN;
import static ru.m210projects.Wang.Type.MyTypes.BIT;
import static ru.m210projects.Wang.Type.MyTypes.DIV2;
import static ru.m210projects.Wang.Type.MyTypes.DIV256;
import static ru.m210projects.Wang.Type.MyTypes.DIV4;
import static ru.m210projects.Wang.Type.MyTypes.MAXLONG;
import static ru.m210projects.Wang.Type.MyTypes.TEST;
import static ru.m210projects.Wang.Weapon.DoBladeDamage;
import static ru.m210projects.Wang.Weapon.InitTurretMgun;
import static ru.m210projects.Wang.Weapon.SpawnVehicleSmoke;

import java.util.Arrays;

import ru.m210projects.Build.Engine.Point;
import ru.m210projects.Build.Types.SECTOR;
import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Build.Types.WALL;
import ru.m210projects.Wang.Sound.SoundType;
import ru.m210projects.Wang.Sprites.StateGroup;
import ru.m210projects.Wang.Type.Anim;
import ru.m210projects.Wang.Type.Anim.AnimCallback;
import ru.m210projects.Wang.Type.Anim.AnimType;
import ru.m210projects.Wang.Type.Animator;
import ru.m210projects.Wang.Type.LONGp;
import ru.m210projects.Wang.Type.PlayerStr;
import ru.m210projects.Wang.Type.Sect_User;
import ru.m210projects.Wang.Type.Sector_Object;
import ru.m210projects.Wang.Type.Sector_Object.SOAnimator;
import ru.m210projects.Wang.Type.TRACK;
import ru.m210projects.Wang.Type.TRACK_POINT;
import ru.m210projects.Wang.Type.USER;
import ru.m210projects.Wang.Type.Vector2i;
import ru.m210projects.Wang.Type.Vector3i;

public class Track {

	public static int GlobSpeedSO;

	public static final int ACTOR_STD_JUMP = (-384);

	public static final int MAX_TRACKS = 100;

	public static TRACK Track[] = new TRACK[MAX_TRACKS];

	public static boolean TrackTowardPlayer(int sp, TRACK t, int start_point) {
		TRACK_POINT end_point;
		int end_dist, start_dist;

		// determine which end of the Track we are starting from
		if (start_point == 0) {
			end_point = t.TrackPoint[t.NumPoints - 1];
		} else {
			end_point = t.TrackPoint[0];
		}

		end_dist = Distance(end_point.x, end_point.y, sprite[sp].x, sprite[sp].y);
		start_dist = Distance(t.TrackPoint[start_point].x, t.TrackPoint[start_point].y, sprite[sp].x, sprite[sp].y);

		if (end_dist < start_dist) {
			return (true);
		}

		return (false);

	}

	public static boolean TrackStartCloserThanEnd(int SpriteNum, TRACK t, int start_point) {
		SPRITE sp = pUser[SpriteNum].getSprite();

		TRACK_POINT end_point;
		long end_dist, start_dist;

		// determine which end of the Track we are starting from
		if (start_point == 0) {
			end_point = t.TrackPoint[t.NumPoints - 1];
		} else {
			end_point = t.TrackPoint[0];
		}

		end_dist = Distance(end_point.x, end_point.y, sp.x, sp.y);
		start_dist = Distance(t.TrackPoint[start_point].x, t.TrackPoint[start_point].y, sp.x, sp.y);

		if (start_dist < end_dist) {
			return (true);
		}

		return (false);
	}

	/*
	 * 
	 * !AIC - Looks at endpoints to figure direction of the track and the closest
	 * point to the sprite.
	 * 
	 */
	public static final int TOWARD_PLAYER = 1;
	public static final int AWAY_FROM_PLAYER = -1;
	public static short end_point[] = { 0, 0 };

	public static int ActorFindTrack(short SpriteNum, int player_dir, int track_type, LONGp track_point_num,
			LONGp track_dir) {
		USER u = pUser[SpriteNum];
		SPRITE sp = pUser[SpriteNum].getSprite();

		int dist, near_dist = 999999, zdiff;
		short track_sect = 0;
		short i;

		TRACK t = null;
		int near_track = -1;
		TRACK_POINT near_tp = null;
		

		// look at all tracks finding the closest endpoint
		for (int ti = 0; ti < MAX_TRACKS; ti++) {
			t = Track[ti];
			int tp = 0;

			// Skip if high tag is not ONE of the track type we are looking for
			if (!TEST(t.ttflags, track_type))
				continue;

			// Skip if already someone on this track
			if (TEST(t.flags, TF_TRACK_OCCUPIED)) {
				continue;
			}

			switch (track_type) {
			case 1 << (TT_DUCK_N_SHOOT): {
				if (u.ActorActionSet.Duck == null)
					return (-1);

				end_point[1] = 0;
				break;
			}

			// for ladders only look at first track point
			case 1 << (TT_LADDER): {
				if (u.ActorActionSet.Climb == null)
					return (-1);

				end_point[1] = 0;
				break;
			}

			case 1 << (TT_JUMP_UP):
			case 1 << (TT_JUMP_DOWN): {
				if (u.ActorActionSet.Jump == null)
					return (-1);

				end_point[1] = 0;
				break;
			}

			case 1 << (TT_TRAVERSE): {
				if (u.ActorActionSet.Crawl == null || u.ActorActionSet.Jump == null)
					return (-1);

				break;
			}

			// look at end point also
			default:
				end_point[1] = (short) (t.NumPoints - 1);
				break;
			}

			zdiff = Z(16);

			// Look at both track end points to see wich is closer
			for (i = 0; i < 2; i++) {
				tp = end_point[i];

				dist = Distance(t.TrackPoint[tp].x, t.TrackPoint[tp].y, sp.x, sp.y);

				if (dist < 15000 && dist < near_dist) {
					// make sure track start is on approximate z level - skip if
					// not
					if (klabs(sp.z - t.TrackPoint[tp].z) > zdiff) {
						continue;
					}

					// determine if the track leads in the direction we want it
					// to
					if (player_dir == TOWARD_PLAYER) {
						if (!TrackTowardPlayer(u.tgt_sp, t, tp)) {
							continue;
						}
					} else if (player_dir == AWAY_FROM_PLAYER) {
						if (TrackTowardPlayer(u.tgt_sp, t, tp)) {
							continue;
						}
					}

					// make sure the start distance is closer than the end
					// distance
					if (!TrackStartCloserThanEnd(SpriteNum, t, tp)) {
						continue;
					}

					near_dist = dist;
					near_track = ti;
					near_tp = t.TrackPoint[tp];

					track_point_num.value = end_point[i];
					track_dir.value = i != 0 ? -1 : 1;
				}
			}
		}

		if (near_dist < 15000) {
			// get the sector number of the point
			
			track_sect = COVERupdatesector(near_tp.x, near_tp.y, track_sect);
			if(track_sect == -1)
				return -1;

			// if can see the point, return the track number
			if (FAFcansee(sp.x, sp.y, sp.z - Z(16), sp.sectnum, near_tp.x, near_tp.y,
					sector[track_sect].floorz - Z(32), track_sect)) {
				return near_track;
			}

			return (-1);
		} else {
			return (-1);
		}
	}

	public static void NextTrackPoint(Sector_Object sop) {
		sop.point += sop.dir;

		if (sop.point > Track[sop.track].NumPoints - 1)
			sop.point = 0;

		if (sop.point < 0)
			sop.point = (short) (Track[sop.track].NumPoints - 1);
	}

	public static void NextActorTrackPoint(int SpriteNum) {
		USER u = pUser[SpriteNum];

		u.point += u.track_dir;

		if (u.point > Track[u.track].NumPoints - 1)
			u.point = 0;

		if (u.point < 0)
			u.point = (short) (Track[u.track].NumPoints - 1);
	}

	public static void TrackAddPoint(TRACK t, TRACK_POINT[] tp, int SpriteNum) {
		SPRITE sp = sprite[SpriteNum];
		if (tp[t.NumPoints] == null)
			tp[t.NumPoints] = new TRACK_POINT();
		TRACK_POINT tpoint = tp[t.NumPoints];

		tpoint.x = sp.x;
		tpoint.y = sp.y;
		tpoint.z = sp.z;
		tpoint.ang = sp.ang;
		tpoint.tag_low = sp.lotag;
		tpoint.tag_high = sp.hitag;

		t.NumPoints++;

		KillSprite(SpriteNum);
	}

	public static int TrackClonePoint(int SpriteNum) {
		SPRITE sp = sprite[SpriteNum], np;
		short newsp = COVERinsertsprite(sp.sectnum, sp.statnum);

		np = sprite[newsp];

		np.cstat = np.extra = 0;
		np.x = sp.x;
		np.y = sp.y;
		np.z = sp.z;
		np.ang = sp.ang;
		np.lotag = sp.lotag;
		np.hitag = sp.hitag;

		return (newsp);
	}

	public static void QuickJumpSetup(int stat, int lotag, int type) {
		short SpriteNum = 0, NextSprite, ndx;
		TRACK_POINT[] tp;
		TRACK t;
		SPRITE nsp;
		int start_sprite, end_sprite;

		// make short quick jump tracks
		for (SpriteNum = headspritestat[stat]; SpriteNum != -1; SpriteNum = NextSprite) {
			NextSprite = nextspritestat[SpriteNum];
			// find an open track
			for (ndx = 0; ndx < MAX_TRACKS; ndx++) {
				if (Track[ndx].NumPoints == 0)
					break;
			}

			Track[ndx].TrackPoint = new TRACK_POINT[4];

			tp = Track[ndx].TrackPoint;
			t = Track[ndx];

			// set track type
			t.ttflags |= (BIT(type));
			t.flags = 0;

			// clone point
			end_sprite = TrackClonePoint(SpriteNum);
			start_sprite = TrackClonePoint(SpriteNum);

			// add start point
			nsp = sprite[start_sprite];
			nsp.lotag = TRACK_START;
			nsp.hitag = 0;
			TrackAddPoint(t, tp, start_sprite);

			// add jump point
			nsp = sprite[SpriteNum];
			nsp.x += 64 * sintable[NORM_ANGLE(nsp.ang + 512)] >> 14;
			nsp.y += 64 * sintable[nsp.ang] >> 14;
			nsp.lotag = (short) lotag;
			TrackAddPoint(t, tp, SpriteNum);

			// add end point
			nsp = sprite[end_sprite];
			nsp.x += 2048 * sintable[NORM_ANGLE(nsp.ang + 512)] >> 14;
			nsp.y += 2048 * sintable[nsp.ang] >> 14;
			nsp.lotag = TRACK_END;
			nsp.hitag = 0;
			TrackAddPoint(t, tp, end_sprite);
		}
	}

	public static void QuickScanSetup(int stat, int lotag, int type) {
		short SpriteNum = 0, NextSprite, ndx;
		TRACK_POINT[] tp;
		TRACK t;
		SPRITE nsp;
		short start_sprite, end_sprite;

		// make short quick jump tracks
		for (SpriteNum = headspritestat[stat]; SpriteNum != -1; SpriteNum = NextSprite) {
			NextSprite = nextspritestat[SpriteNum];
			// find an open track
			for (ndx = 0; ndx < MAX_TRACKS; ndx++) {
				if (Track[ndx].NumPoints == 0)
					break;
			}

			// save space for 3 points
			Track[ndx].TrackPoint = new TRACK_POINT[4];

			tp = Track[ndx].TrackPoint;
			t = Track[ndx];

			// set track type
			t.ttflags |= (BIT(type));
			t.flags = 0;

			// clone point
			end_sprite = (short) TrackClonePoint(SpriteNum);
			start_sprite = (short) TrackClonePoint(SpriteNum);

			// add start point
			nsp = sprite[start_sprite];
			nsp.lotag = TRACK_START;
			nsp.hitag = 0;
			nsp.x += 64 * sintable[NORM_ANGLE(nsp.ang + 1024 + 512)] >> 14;
			nsp.y += 64 * sintable[NORM_ANGLE(nsp.ang + 1024)] >> 14;
			TrackAddPoint(t, tp, start_sprite);

			// add jump point
			nsp = sprite[SpriteNum];
			nsp.lotag = (short) lotag;
			TrackAddPoint(t, tp, SpriteNum);

			// add end point
			nsp = sprite[end_sprite];
			nsp.x += 64 * sintable[NORM_ANGLE(nsp.ang + 512)] >> 14;
			nsp.y += 64 * sintable[nsp.ang] >> 14;
			nsp.lotag = TRACK_END;
			nsp.hitag = 0;
			TrackAddPoint(t, tp, end_sprite);
		}
	}

	public static void QuickExitSetup(int stat, int type) {
		short SpriteNum = 0, NextSprite, ndx;
		TRACK_POINT[] tp;
		TRACK t;
		SPRITE nsp;
		short start_sprite, end_sprite;

		for (SpriteNum = headspritestat[stat]; SpriteNum != -1; SpriteNum = NextSprite) {
			NextSprite = nextspritestat[SpriteNum];
			// find an open track
			for (ndx = 0; ndx < MAX_TRACKS; ndx++) {
				if (Track[ndx].NumPoints == 0)
					break;
			}

			// save space for 3 points
			Track[ndx].TrackPoint = new TRACK_POINT[4];

			tp = Track[ndx].TrackPoint;
			t = Track[ndx];

			// set track type
			t.ttflags |= (BIT(type));
			t.flags = 0;

			// clone point
			end_sprite = (short) TrackClonePoint(SpriteNum);
			start_sprite = (short) TrackClonePoint(SpriteNum);

			// add start point
			nsp = sprite[start_sprite];
			nsp.lotag = TRACK_START;
			nsp.hitag = 0;
			TrackAddPoint(t, tp, start_sprite);

			KillSprite(SpriteNum);

			// add end point
			nsp = sprite[end_sprite];
			nsp.x += 1024 * sintable[NORM_ANGLE(nsp.ang + 512)] >> 14;
			nsp.y += 1024 * sintable[nsp.ang] >> 14;
			nsp.lotag = TRACK_END;
			nsp.hitag = 0;
			TrackAddPoint(t, tp, end_sprite);
		}
	}

	public static void QuickLadderSetup(int stat, int lotag, int type) {
		short SpriteNum = 0, NextSprite, ndx;
		TRACK_POINT[] tp;
		TRACK t;
		SPRITE nsp;
		short start_sprite, end_sprite;

		for (SpriteNum = headspritestat[stat]; SpriteNum != -1; SpriteNum = NextSprite) {
			NextSprite = nextspritestat[SpriteNum];
			// find an open track
			for (ndx = 0; ndx < MAX_TRACKS; ndx++) {
				if (Track[ndx].NumPoints == 0)
					break;
			}

			// save space for 3 points
			Track[ndx].TrackPoint = new TRACK_POINT[4];

			tp = Track[ndx].TrackPoint;
			t = Track[ndx];

			// set track type
			t.ttflags |= (BIT(type));
			t.flags = 0;

			// clone point
			end_sprite = (short) TrackClonePoint(SpriteNum);
			start_sprite = (short) TrackClonePoint(SpriteNum);

			// add start point
			nsp = sprite[start_sprite];
			nsp.lotag = TRACK_START;
			nsp.hitag = 0;
			nsp.x += MOVEx(256, nsp.ang + 1024);
			nsp.y += MOVEy(256, nsp.ang + 1024);
			TrackAddPoint(t, tp, start_sprite);

			// add climb point
			nsp = sprite[SpriteNum];
			nsp.lotag = (short) lotag;
			TrackAddPoint(t, tp, SpriteNum);

			// add end point
			nsp = sprite[end_sprite];
			nsp.x += MOVEx(512, nsp.ang);
			nsp.y += MOVEy(512, nsp.ang);
			nsp.lotag = TRACK_END;
			nsp.hitag = 0;
			TrackAddPoint(t, tp, end_sprite);
		}
	}

	public static void TrackSetup() {
		short SpriteNum = 0, NextSprite, ndx;
		TRACK_POINT[] tp;
		TRACK t;
		TRACK_POINT[] newp;
		int size;

		// put points on track
		for (ndx = 0; ndx < MAX_TRACKS; ndx++) {
			if (headspritestat[STAT_TRACK + ndx] == -1) {
				// for some reason I need at least one record allocated
				// can't remember why at this point
				if (Track[ndx] == null)
					Track[ndx] = new TRACK();
				else Track[ndx].reset();
				Track[ndx].TrackPoint = new TRACK_POINT[1];
				continue;
			}

			// make the track array rather large. I'll resize it to correct size
			// later.
			if (Track[ndx] == null)
				Track[ndx] = new TRACK();
			else Track[ndx].reset();
			Track[ndx].TrackPoint = new TRACK_POINT[500];

			tp = Track[ndx].TrackPoint;
			t = Track[ndx];

			// find the first point and save it
			for (SpriteNum = headspritestat[STAT_TRACK + ndx]; SpriteNum != -1; SpriteNum = NextSprite) {
				NextSprite = nextspritestat[SpriteNum];
				if (LOW_TAG_SPRITE(SpriteNum) == TRACK_START) {
					TrackAddPoint(t, tp, SpriteNum);
					break;
				}
			}
			
			if(t.NumPoints <= 0)
				continue;

			// set up flags for track types
			if (tp[0].tag_low == TRACK_START && tp[0].tag_high != 0)
				t.ttflags |= (BIT(tp[0].tag_high));

			// while there are still sprites on this status list
			while (headspritestat[STAT_TRACK + ndx] != -1) {
				short next_sprite = -1;
				int dist, low_dist = 999999;

				// find the closest point to the last point
				for (SpriteNum = headspritestat[STAT_TRACK + ndx]; SpriteNum != -1; SpriteNum = NextSprite) {
					NextSprite = nextspritestat[SpriteNum];
					dist = Distance((tp[t.NumPoints - 1]).x, (tp[t.NumPoints - 1]).y, sprite[SpriteNum].x,
							sprite[SpriteNum].y);

					if (dist < low_dist) {
						next_sprite = SpriteNum;
						low_dist = dist;
					}

				}

				// save the closest one off and kill it
				if (next_sprite != -1) {
					TrackAddPoint(t, tp, next_sprite);
				}

			}

			size = (Track[ndx].NumPoints + 1);
			newp = new TRACK_POINT[size]; // CallocMem(size, 1);
			for (int i = 0; i < Track[ndx].NumPoints; i++)
				newp[i] = Track[ndx].TrackPoint[i];

			Track[ndx].TrackPoint = newp;
		}

		QuickJumpSetup(STAT_QUICK_JUMP, TRACK_ACTOR_QUICK_JUMP, TT_JUMP_UP);
		QuickJumpSetup(STAT_QUICK_JUMP_DOWN, TRACK_ACTOR_QUICK_JUMP_DOWN, TT_JUMP_DOWN);
		QuickJumpSetup(STAT_QUICK_SUPER_JUMP, TRACK_ACTOR_QUICK_SUPER_JUMP, TT_SUPER_JUMP_UP);
		QuickScanSetup(STAT_QUICK_SCAN, TRACK_ACTOR_QUICK_SCAN, TT_SCAN);
		QuickLadderSetup(STAT_QUICK_LADDER, TRACK_ACTOR_CLIMB_LADDER, TT_LADDER);
		QuickExitSetup(STAT_QUICK_EXIT, TT_EXIT);
		QuickJumpSetup(STAT_QUICK_OPERATE, TRACK_ACTOR_QUICK_OPERATE, TT_OPERATE);
		QuickJumpSetup(STAT_QUICK_DUCK, TRACK_ACTOR_QUICK_DUCK, TT_DUCK_N_SHOOT);
		QuickJumpSetup(STAT_QUICK_DEFEND, TRACK_ACTOR_QUICK_DEFEND, TT_HIDE_N_SHOOT);
	}

	public static int FindBoundSprite(int tag) {
		short sn, next_sn;

		for (sn = headspritestat[STAT_ST1]; sn != -1; sn = next_sn) {
			next_sn = nextspritestat[sn];
			if (sprite[sn].hitag == tag) {
				return sn;
			}
		}

		return -1;
	}

	private static final short StatList[] = { STAT_DEFAULT, STAT_MISC, STAT_ITEM, STAT_TRAP, STAT_SPAWN_SPOT,
			STAT_SOUND_SPOT, STAT_WALL_MOVE, STAT_WALLBLOOD_QUEUE, STAT_SPRITE_HIT_MATCH, STAT_AMBIENT,
			STAT_DELETE_SPRITE, STAT_SPAWN_TRIGGER, // spawing monster trigger - for Randy's bullet train.
	};

	private static boolean SectorObjectSetupBounds(int sopi) {
		int xlow, ylow, xhigh, yhigh;
		short sp_num, next_sp_num, sn, startwall, endwall;
		int i, k, j;
		boolean FoundOutsideLoop = false;
		boolean SectorInBounds;
		SECTOR sectp;

		Sector_Object sop = SectorObject[sopi];

		USER u = pUser[sop.sp_child];

		// search for 2 sprite bounding tags
		int BoundSpritei = FindBoundSprite(500 + (sopi * 5));
		if (BoundSpritei == -1)
			return false;
		SPRITE BoundSprite = sprite[BoundSpritei];

		xlow = BoundSprite.x;
		ylow = BoundSprite.y;

		KillSprite(BoundSpritei);

		BoundSpritei = FindBoundSprite(501 + (sopi * 5));
		BoundSprite = sprite[BoundSpritei];
		xhigh = BoundSprite.x;
		yhigh = BoundSprite.y;

		KillSprite(BoundSpritei);

		// set radius for explosion checking - based on bounding box
		u.Radius = DIV4((xhigh - xlow) + (yhigh - ylow));
		u.Radius -= DIV4(u.Radius); // trying to get it a good size

		// search for center sprite if it exists

		BoundSpritei = FindBoundSprite(SECT_SO_CENTER);
		if (BoundSpritei != -1) {
			BoundSprite = sprite[BoundSpritei];
			sop.xmid = BoundSprite.x;
			sop.ymid = BoundSprite.y;
			sop.zmid = BoundSprite.z;
			KillSprite(BoundSpritei);
		}

		// look through all sectors for whole sectors that are IN bounds
		for (k = 0; k < numsectors; k++) {
			startwall = sector[k].wallptr;
			endwall = (short) (startwall + sector[k].wallnum - 1);

			SectorInBounds = true;

			for (j = startwall; j <= endwall; j++) {
				// all walls have to be in bounds to be in sector object
				if (!(wall[j].x > xlow && wall[j].x < xhigh && wall[j].y > ylow && wall[j].y < yhigh)) {
					SectorInBounds = false;
					break;
				}
			}

			if (SectorInBounds) {
				sop.sector[sop.num_sectors] = (short) k;

				// all sectors in sector object have this flag set - for colision
				// detection and recognition
				sector[k].extra |= (SECTFX_SECTOR_OBJECT);

				sop.zorig_floor[sop.num_sectors] = sector[k].floorz;
				sop.zorig_ceiling[sop.num_sectors] = sector[k].ceilingz;

				if (TEST(sector[k].extra, SECTFX_SINK))
					sop.zorig_floor[sop.num_sectors] += Z(SectUser[k].depth);

				// lowest and highest floorz's
				if (sector[k].floorz > sop.floor_loz)
					sop.floor_loz = sector[k].floorz;

				if (sector[k].floorz < sop.floor_hiz)
					sop.floor_hiz = sector[k].floorz;

				sop.num_sectors++;
			}
		}

		//
		// Make sure every sector object has an outer loop tagged - important
		//

		FoundOutsideLoop = false;

		for (j = 0; sop.sector[j] != -1; j++) {
			sectp = sector[sop.sector[j]];

			startwall = (sectp).wallptr;
			endwall = (short) (startwall + (sectp).wallnum - 1);

			// move all walls in sectors
			for (k = startwall; k <= endwall; k++) {
				// for morph point - tornado style
				if (wall[k].lotag == TAG_WALL_ALIGN_SLOPE_TO_POINT)
					sop.morph_wall_point = (short) k;

				if (wall[k].extra != 0 && TEST(wall[k].extra, WALLFX_LOOP_OUTER))
					FoundOutsideLoop = true;

				// each wall has this set - for collision detection
				wall[k].extra |= (WALLFX_SECTOR_OBJECT | WALLFX_DONT_STICK);
				if (wall[k].nextwall >= 0)
					wall[wall[k].nextwall].extra |= (WALLFX_SECTOR_OBJECT | WALLFX_DONT_STICK);
			}
		}

		if (!FoundOutsideLoop) {
			game.GameCrash("Forgot to tag outer loop for Sector Object #" + sopi);
			return false;
		}

		for (i = 0; i < StatList.length; i++) {
			for (sp_num = headspritestat[StatList[i]]; sp_num != -1; sp_num = next_sp_num) {
				next_sp_num = nextspritestat[sp_num];
				SPRITE sp = sprite[sp_num];

				if (sp.x > xlow && sp.x < xhigh && sp.y > ylow && sp.y < yhigh) {
					// some delete sprites ride others don't
					if (sp.statnum == STAT_DELETE_SPRITE) {
						if (!TEST_BOOL2(sp))
							continue;
					}

					if (pUser[sp_num] == null)
						u = SpawnUser(sp_num, 0, null);
					else
						u = pUser[sp_num];

					u.RotNum = 0;

					u.ox = sp.x;
					u.oy = sp.y;
					u.oz = sp.z;

					switch (sp.statnum) {
					case STAT_WALL_MOVE:

						break;
					case STAT_DEFAULT:
						switch (sp.hitag) {
						case SO_CLIP_BOX: {
							short ang2;
							sop.clipdist = 0;
							sop.clipbox_dist[sop.clipbox_num] = sp.lotag;
							sop.clipbox_xoff[sop.clipbox_num] = (short) (sop.xmid - sp.x);
							sop.clipbox_yoff[sop.clipbox_num] = (short) (sop.ymid - sp.y);

							sop.clipbox_vdist[sop.clipbox_num] = (short) engine
									.ksqrt(SQ(sop.xmid - sp.x) + SQ(sop.ymid - sp.y));

							ang2 = engine.getangle(sp.x - sop.xmid, sp.y - sop.ymid);
							sop.clipbox_ang[sop.clipbox_num] = (short) GetDeltaAngle(sop.ang, ang2);

							sop.clipbox_num++;
							KillSprite(sp_num);

							continue;
						}
						case SO_SHOOT_POINT:
							sp.owner = -1;
							change_sprite_stat(sp_num, STAT_SO_SHOOT_POINT);
							sp.cstat &= ~(CSTAT_SPRITE_BLOCK | CSTAT_SPRITE_BLOCK_HITSCAN);
							break;
						default:
							break;
						}
						break;
					}

					u.sx = sop.xmid - sp.x;
					u.sy = sop.ymid - sp.y;
					u.sz = sector[sop.mid_sector].floorz - sp.z;

					u.Flags |= (SPR_SO_ATTACHED);

					u.sang = sp.ang;
					u.spal = (byte) sp.pal;

					// search SO's sectors to make sure that it is not on a
					// sector

					// place all sprites on list
					for (sn = 0; sn < sop.sp_num.length; sn++) {
						if (sop.sp_num[sn] == -1)
							break;
					}

					sop.sp_num[sn] = sp_num;

					if (!TEST(sop.flags, SOBJ_SPRITE_OBJ)) {
						// determine if sprite is on a SO sector - set flag if
						// true
						for (j = 0; j < sop.num_sectors; j++) {
							if (sop.sector[j] == sp.sectnum) {
								u.Flags |= (SPR_ON_SO_SECTOR);
								u.sz = sector[sp.sectnum].floorz - sp.z;
								break;
							}
						}
					}
				}

				continue;
			}
		}

		// for SPRITE OBJECT sprites, set the u.sz value to the difference
		// between the zmid and the sp.z
		if (TEST(sop.flags, SOBJ_SPRITE_OBJ)) {
			SPRITE sp;

			int zmid = -9999999;

			// choose the lowest sprite for the zmid
			for (i = 0; sop.sp_num[i] != -1; i++) {
				sp = sprite[sop.sp_num[i]];
				u = pUser[sop.sp_num[i]];

				if (sp.z > zmid)
					zmid = sp.z;
			}
			sop.zmid = zmid;

			for (i = 0; sop.sp_num[i] != -1; i++) {
				sp = sprite[sop.sp_num[i]];
				u = pUser[sop.sp_num[i]];

				u.sz = sop.zmid - sp.z;
			}

		}
		
		return true;
	}

	public static boolean SetupSectorObject(int sectnum, int tag) {
		SPRITE sp;

		short object_num, SpriteNum, NextSprite;
		short j;
		short new1;
		USER u;

		tag -= (TAG_OBJECT_CENTER - 1);

		object_num = (short) (tag / 5);
		Sector_Object sop = SectorObject[object_num];

		// initialize stuff first time through
		if (sop.num_sectors == -1) {
			Arrays.fill(sop.sector, (short) -1);

			sop.crush_z = 0;
			sop.drive_angspeed = 0;
			sop.drive_angslide = 0;
			sop.drive_slide = 0;
			sop.drive_speed = 0;
			sop.num_sectors = 0;
			sop.update = 15000;
			sop.flags = 0;
			sop.clipbox_num = 0;
			sop.bob_amt = 0;
			sop.vel_rate = 6;
			sop.z_rate = 256;
			sop.zdelta = sop.z_tgt = 0;
			sop.wait_tics = 0;
			sop.spin_speed = 0;
			sop.spin_ang = 0;
			sop.ang_orig = 0;
			sop.clipdist = 1024;
			sop.target_dist = 0;
			sop.turn_speed = 4;
			sop.floor_loz = -9999999;
			sop.floor_hiz = 9999999;
			sop.player_xoff = sop.player_yoff = 0;
			sop.ang_tgt = sop.ang = sop.ang_moving = 0;
			sop.op_main_sector = -1;
			sop.ram_damage = 0;
			sop.max_damage = -9999;

			sop.scale_type = SO_SCALE_NONE;
			sop.scale_dist = 0;
			sop.scale_speed = 20;
			sop.scale_dist_min = -1024;
			sop.scale_dist_max = 1024;
			sop.scale_rand_freq = 64 >> 3;

			sop.scale_x_mult = 256;
			sop.scale_y_mult = 256;

			sop.morph_ang = (short) RANDOM_P2(2048);
			sop.morph_z_speed = 20;
			sop.morph_speed = 32;
			sop.morph_dist_max = 1024;
			sop.morph_rand_freq = 64;
			sop.morph_dist = 0;
			sop.morph_xoff = 0;
			sop.morph_yoff = 0;

			sop.PreMoveAnimator = null;
			sop.PostMoveAnimator = null;
			sop.Animator = null;
		}

		switch (tag % 5) {
		case TAG_OBJECT_CENTER - 500:

			sop.mid_sector = (short) sectnum;
			Vector3i p = SectorMidPoint(sectnum); // , &sop.xmid, &sop.ymid, &sop.zmid);
			sop.xmid = p.x;
			sop.ymid = p.y;
			sop.zmid = p.z;

			sop.dir = 1;
			sop.track = (short) HIGH_TAG(sectnum);

			// spawn a sprite to make it easier to integrate with sprite routines
			new1 = (short) SpawnSprite(STAT_SO_SP_CHILD, 0, null, sectnum, sop.xmid, sop.ymid, sop.zmid, 0, 0);
			sop.sp_child = new1;
			u = pUser[new1];
			u.sop_parent = object_num;
			u.Flags2 |= (SPR2_SPRITE_FAKE_BLOCK); // for damage test

			// check for any ST1 sprites laying on the center sector
			for (SpriteNum = headspritesect[sectnum]; SpriteNum != -1; SpriteNum = NextSprite) {
				NextSprite = nextspritesect[SpriteNum];
				sp = sprite[SpriteNum];

				if (sp.statnum == STAT_ST1) {
					switch (sp.hitag) {
					case SO_SCALE_XY_MULT:
						if (SP_TAG5(sp) != 0)
							sop.scale_x_mult = (short) SP_TAG5(sp);
						if (SP_TAG6(sp) != 0)
							sop.scale_y_mult = (short) SP_TAG6(sp);
						KillSprite(SpriteNum);
						break;

					case SO_SCALE_POINT_INFO:
						Arrays.fill(sop.scale_point_dist, (short) 0);

						sop.scale_point_base_speed = (short) SP_TAG2(sp);
						for (j = 0; j < sop.scale_point_speed.length; j++) {
							sop.scale_point_speed[j] = (short) SP_TAG2(sp);
						}

						if (SP_TAG4(sp) != 0)
							sop.scale_point_rand_freq = (short) SP_TAG4(sp);
						else
							sop.scale_point_rand_freq = 64;

						sop.scale_point_dist_min = (short) -SP_TAG5(sp);
						sop.scale_point_dist_max = (short) SP_TAG6(sp);
						KillSprite(SpriteNum);
						break;

					case SO_SCALE_INFO:
						sop.flags |= (SOBJ_DYNAMIC);
						sop.scale_speed = (short) SP_TAG2(sp);
						sop.scale_dist_min = (short) -SP_TAG5(sp);
						sop.scale_dist_max = (short) SP_TAG6(sp);

						sop.scale_type = (short) SP_TAG4(sp);
						sop.scale_active_type = (short) SP_TAG7(sp);

						if (SP_TAG8(sp) != 0)
							sop.scale_rand_freq = (short) SP_TAG8(sp);
						else
							sop.scale_rand_freq = 64 >> 3;

						if (SP_TAG3(sp) == 0)
							sop.scale_dist = sop.scale_dist_min;
						else if (SP_TAG3(sp) == 1)
							sop.scale_dist = sop.scale_dist_max;

						KillSprite(SpriteNum);
						break;

					case SPAWN_SPOT:
						if (sp.clipdist == 3) {
							change_sprite_stat(SpriteNum, STAT_NO_STATE);
							u = SpawnUser(SpriteNum, 0, null);
							u.ActorActionFunc = null;
						}
						break;

					case SO_AUTO_TURRET:
						sop.Animator = SOAnimator.DoAutoTurretObject;
						KillSprite(SpriteNum);
						break;

					case SO_TORNADO:
						sop.vel = 120;
						sop.flags |= (SOBJ_DYNAMIC);
						sop.scale_type = SO_SCALE_CYCLE;
						// spin stuff
						sop.spin_speed = 16;
						sop.last_ang = sop.ang;
						// animators
						sop.Animator = SOAnimator.DoTornadoObject;
						sop.PreMoveAnimator = SOAnimator.ScaleSectorObject;
						sop.PostMoveAnimator = SOAnimator.MorphTornado;
						// clip
						sop.clipdist = 2500;
						// morph point
						sop.morph_speed = 16;
						sop.morph_z_speed = 6;
						sop.morph_dist_max = 1024;
						sop.morph_rand_freq = 8;
						sop.scale_dist_min = -768;
						KillSprite(SpriteNum);
						break;
					case SO_FLOOR_MORPH:
						sop.flags |= (SOBJ_DYNAMIC);
						sop.scale_type = SO_SCALE_NONE;
						sop.morph_speed = 120;
						sop.morph_z_speed = 7;
						sop.PostMoveAnimator = SOAnimator.MorphFloor;
						sop.morph_dist_max = 4000;
						sop.morph_rand_freq = 8;
						KillSprite(SpriteNum);
						break;

					case SO_AMOEBA:
						sop.flags |= (SOBJ_DYNAMIC);
						// sop.scale_type = SO_SCALE_CYCLE;
						sop.scale_type = SO_SCALE_RANDOM_POINT;
						sop.PreMoveAnimator = SOAnimator.ScaleSectorObject;
						Arrays.fill(sop.scale_point_dist, (short) 0);
						sop.scale_point_base_speed = (short) SCALE_POINT_SPEED;
						for (j = 0; j < sop.scale_point_speed.length; j++)
							sop.scale_point_speed[j] = (short) SCALE_POINT_SPEED;

						sop.scale_point_dist_min = -256;
						sop.scale_point_dist_max = 256;
						sop.scale_point_rand_freq = 32;
						KillSprite(SpriteNum);
						break;
					case SO_MAX_DAMAGE:
						u.MaxHealth = (short) SP_TAG2(sp);
						if (SP_TAG5(sp) != 0)
							sop.max_damage = (short) SP_TAG5(sp);
						else
							sop.max_damage = u.MaxHealth;

						switch (sp.clipdist) {
						case 0:
							break;
						case 1:
							sop.flags |= (SOBJ_DIE_HARD);
							break;
						}
						KillSprite(SpriteNum);
						break;

					case SO_DRIVABLE_ATTRIB:

						sop.drive_angspeed = SP_TAG2(sp);
						sop.drive_angspeed <<= 5;
						sop.drive_angslide = SP_TAG3(sp);
						if (sop.drive_angslide <= 0 || sop.drive_angslide == 32)
							sop.drive_angslide = 1;

						sop.drive_speed = SP_TAG6(sp);
						sop.drive_speed <<= 5;
						sop.drive_slide = SP_TAG7(sp);
						if (sop.drive_slide <= 0)
							sop.drive_slide = 1;

						if (TEST_BOOL1(sp))
							sop.flags |= (SOBJ_NO_QUAKE);

						if (TEST_BOOL3(sp))
							sop.flags |= (SOBJ_REMOTE_ONLY);

						if (TEST_BOOL4(sp)) {
							sop.crush_z = sp.z;
							sop.flags |= (SOBJ_RECT_CLIP);
						}
						break;

					case SO_RAM_DAMAGE:
						sop.ram_damage = sp.lotag;
						KillSprite(SpriteNum);
						break;
					case SECT_SO_CLIP_DIST:
						sop.clipdist = sp.lotag;
						KillSprite(SpriteNum);
						break;
					case SECT_SO_SPRITE_OBJ:
						sop.flags |= (SOBJ_SPRITE_OBJ);
						KillSprite(SpriteNum);
						break;
					case SECT_SO_DONT_ROTATE:
						sop.flags |= (SOBJ_DONT_ROTATE);
						KillSprite(SpriteNum);
						break;
					case SO_LIMIT_TURN:
						sop.limit_ang_center = sp.ang;
						sop.limit_ang_delta = sp.lotag;
						KillSprite(SpriteNum);
						break;
					case SO_MATCH_EVENT:
						sop.match_event = sp.lotag;
						sop.match_event_sprite = SpriteNum;
						break;
					case SO_SET_SPEED:
						sop.vel = sp.lotag * 256;
						sop.vel_tgt = sop.vel;
						KillSprite(SpriteNum);
						break;
					case SO_SPIN:
						if (sop.spin_speed != 0)
							break;
						sop.spin_speed = sp.lotag;
						sop.last_ang = sop.ang;
						KillSprite(SpriteNum);
						break;
					case SO_ANGLE:
						sop.ang = sop.ang_moving = sp.ang;
						sop.last_ang = sop.ang_orig = sop.ang;
						sop.spin_ang = 0;
						KillSprite(SpriteNum);
						break;
					case SO_SPIN_REVERSE:

						sop.spin_speed = sp.lotag;
						sop.last_ang = sop.ang;

						if (sop.spin_speed >= 0)
							sop.spin_speed = (short) -sop.spin_speed;

						KillSprite(SpriteNum);
						break;
					case SO_BOB_START:
						sop.bob_amt = Z(sp.lotag);
						sop.bob_sine_ndx = 0;
						sop.bob_speed = 4;
						KillSprite(SpriteNum);
						break;
					case SO_TURN_SPEED:
						sop.turn_speed = sp.lotag;
						KillSprite(SpriteNum);
						break;
					case SO_SYNC1:
						sop.flags |= (SOBJ_SYNC1);
						KillSprite(SpriteNum);
						break;
					case SO_SYNC2:
						sop.flags |= (SOBJ_SYNC2);
						KillSprite(SpriteNum);
						break;
					case SO_KILLABLE:
						sop.flags |= (SOBJ_KILLABLE);
						KillSprite(SpriteNum);
						break;
					}
				}
			}

			if (sop.vel == -1)
				sop.vel = sop.vel_tgt = 8 * 256;

			if(!SectorObjectSetupBounds(object_num))
				return false;

			if (sop.track >= SO_OPERATE_TRACK_START) {
				switch (sop.track) {
				case SO_TURRET_MGUN:
				case SO_TURRET:
				case SO_TANK:
					sop.vel = 0;
					sop.flags |= (SOBJ_OPERATIONAL);
					break;
				case SO_SPEED_BOAT:
					sop.vel = 0;
					sop.bob_amt = Z(2);
					sop.bob_speed = 4;
					sop.flags |= (SOBJ_OPERATIONAL);
					break;
				default:
					sop.flags |= (SOBJ_OPERATIONAL);
					break;
				}
			}

			sector[sectnum].lotag = 0;
			sector[sectnum].hitag = 0;

			if (sop.max_damage <= 0)
				VehicleSetSmoke(sop, SpawnVehicleSmoke);

			break;
		}

		return true;
	}

	public static void PostSetupSectorObject() {
		for (int s = 0; s < MAX_SECTOR_OBJECTS; s++) {
			Sector_Object sop = SectorObject[s];
			if (sop.xmid == MAXLONG)
				continue;
			FindMainSector(s);
		}
	}

	public static int PlayerOnObject(int sectnum_match) {
		short i, j;
		Sector_Object sop;

		// place each sector object on the track
		for (i = 0; (i < MAX_SECTOR_OBJECTS); i++) {
			sop = SectorObject[i];

			if (sop.track < SO_OPERATE_TRACK_START)
				continue;

			for (j = 0; j < sop.num_sectors; j++) {
				if (sop.sector[j] == sectnum_match && TEST(sector[sectnum_match].extra, SECTFX_OPERATIONAL)) {
					return i;
				}
			}
		}

		return (-1);
	}

	public static void PlaceSectorObjectsOnTracks() {
		short i, j, k, startwall, endwall;
		boolean found;

		// place each sector object on the track
		for (i = 0; i < MAX_SECTOR_OBJECTS; i++) {
			int low_dist = 999999, dist;
			Sector_Object sop = SectorObject[i];
			TRACK_POINT[] tpoint = null;

			if (sop.xmid == MAXLONG)
				continue;

			// save off the original x and y locations of the walls AND sprites
			sop.num_walls = 0;
			for (j = 0; sop.sector[j] != -1; j++) {
				startwall = sector[sop.sector[j]].wallptr;
				endwall = (short) (startwall + sector[sop.sector[j]].wallnum - 1);

				// move all walls in sectors
				for (k = startwall; k <= endwall; k++) {
					sop.xorig[sop.num_walls] = (short) (sop.xmid - wall[k].x);
					sop.yorig[sop.num_walls] = (short) (sop.ymid - wall[k].y);
					sop.num_walls++;
				}
			}

			if (sop.track <= -1)
				continue;

			if (sop.track >= SO_OPERATE_TRACK_START)
				continue;

			found = false;
			// find the closest point on the track and put SOBJ on it
			for (j = 0; j < Track[sop.track].NumPoints; j++) {
				tpoint = Track[sop.track].TrackPoint;

				dist = Distance((tpoint[j]).x, (tpoint[j]).y, sop.xmid, sop.ymid);

				if (dist < low_dist) {
					low_dist = dist;
					sop.point = j;
					found = true;
				}
			}

			if (!found) {
				sop.track = -1;
				continue;
			}

			NextTrackPoint(sop);

			sop.ang = engine.getangle((tpoint[sop.point]).x - sop.xmid, (tpoint[sop.point]).y - sop.ymid);

			sop.ang_moving = sop.ang_tgt = sop.ang;
		}
	}

	public static void PlaceActorsOnTracks() {
		short i, nexti, j, tag;
		SPRITE sp;
		USER u;
		TRACK_POINT[] tpoint = null;

		// place each actor on the track
		for (i = headspritestat[STAT_ENEMY]; i != -1; i = nexti) {
			nexti = nextspritestat[i];
			int low_dist = 999999, dist;

			sp = pUser[i].getSprite();
			u = pUser[i];

			tag = (short) LOW_TAG_SPRITE(i);

			if (tag < TAG_ACTOR_TRACK_BEGIN || tag > TAG_ACTOR_TRACK_END)
				continue;

			// setup sprite track defaults
			u.track = (short) (tag - TAG_ACTOR_TRACK_BEGIN);

			// if facing left go backward
			if (BETWEEN(sp.ang, 513, 1535)) {
				u.track_dir = -1;
			} else {
				u.track_dir = 1;
			}

			u.track_vel = sp.xvel * 256;
			u.vel_tgt = u.track_vel;
			u.vel_rate = 6;

			// find the closest point on the track and put SOBJ on it
			for (j = 0; j < Track[u.track].NumPoints; j++) {
				tpoint = Track[u.track].TrackPoint;

				dist = Distance((tpoint[j]).x, (tpoint[j]).y, sp.x, sp.y);

				if (dist < low_dist) {
					low_dist = dist;
					u.point = j;
				}
			}

			NextActorTrackPoint(i);

			// check angle in the "forward" direction
			sp.ang = engine.getangle((tpoint[u.point]).x - sp.x, (tpoint[u.point]).y - sp.y);
		}
	}

	public static void MovePlayer(PlayerStr pp, int sopi, int nx, int ny) {
		// make sure your standing on the so
		if (TEST(pp.Flags, PF_JUMPING | PF_FALLING | PF_FLYING))
			return;

		Sector_Object sop = SectorObject[sopi];
		
		pp.sop_riding = sopi;

		// if player has NOT moved and player is NOT riding
		// set up the player for riding
		if (!TEST(pp.Flags, PF_PLAYER_MOVED) && !TEST(pp.Flags, PF_PLAYER_RIDING)) {
			pp.Flags |= (PF_PLAYER_RIDING);

			pp.RevolveAng = pp.getAnglei();
			pp.RevolveX = pp.posx;
			pp.RevolveY = pp.posy;

			// set the delta angle to 0 when moving
			pp.RevolveDeltaAng = 0;
		}

		pp.posx += (nx);
		pp.posy += (ny);

		if (TEST(sop.flags, SOBJ_DONT_ROTATE)) {
			UpdatePlayerSprite(pp);
			return;
		}

		if (TEST(pp.Flags, PF_PLAYER_MOVED)) {
			// Player is moving

			// save the current information so when Player stops
			// moving then you
			// know where he was last
			pp.RevolveAng = pp.getAnglei();
			pp.RevolveX = pp.posx;
			pp.RevolveY = pp.posy;

			// set the delta angle to 0 when moving
			pp.RevolveDeltaAng = 0;
		} else {
			// Player is NOT moving

			// Move saved x&y variables
			pp.RevolveX += (nx);
			pp.RevolveY += (ny);

			// Last known angle is now adjusted by the delta angle
			pp.RevolveAng = (short) NORM_ANGLE(pp.getAnglei() - pp.RevolveDeltaAng);
		}

		// increment Players delta angle
		pp.RevolveDeltaAng = (short) NORM_ANGLE(pp.RevolveDeltaAng + GlobSpeedSO);

		Point p = engine.rotatepoint(sop.xmid, sop.ymid, pp.RevolveX, pp.RevolveY, pp.RevolveDeltaAng);
		pp.posx = p.getX();
		pp.posy = p.getY();
		// THIS WAS CAUSING PROLEMS!!!!
		// Sectors are still being manipulated so you can end up in a void (-1) sector
		// COVERupdatesector(pp.posx, pp.posy, &pp.cursectnum);

		// New angle is formed by taking last known angle and
		// adjusting by the delta angle
		pp.pang = (short) NORM_ANGLE(pp.RevolveAng + pp.RevolveDeltaAng);

		UpdatePlayerSprite(pp);
	}

	public static void MovePoints(int sopi, short delta_ang, int nx, int ny, int skip) {
		int j, rx, ry;
		short startwall, endwall, pnum;
		PlayerStr pp;
		SECTOR sectp;
		SPRITE sp;
		WALL wp;
		USER u;
		short i, rot_ang;
		boolean PlayerMove = true;
		
		Sector_Object sop = SectorObject[sopi];

		if (sop.xmid >= MAXSO)
			PlayerMove = false;

		// move along little midpoint
		sop.xmid += (nx);
		sop.ymid += (ny);

		if (sop.xmid >= MAXSO)
			PlayerMove = false;

		// move child sprite along also
		sprite[sop.sp_child].x = sop.xmid;
		sprite[sop.sp_child].y = sop.ymid;

		// setting floorz if need be
		// if (!TEST(sop.flags, SOBJ_SPRITE_OBJ))
		if (TEST(sop.flags, SOBJ_ZMID_FLOOR))
			sop.zmid = sector[sop.mid_sector].floorz;

		short k;
		for (j = 0; sop.sector[j] != -1; j++) {
			sectp = sector[sop.sector[j]];

			if (!TEST(sop.flags, SOBJ_SPRITE_OBJ | SOBJ_DONT_ROTATE))
			{
				startwall = (sectp).wallptr;
				endwall = (short) (startwall + (sectp).wallnum - 1);
	
				// move all walls in sectors
				for (k = startwall; k <= endwall; k++) {
					wp = wall[k];
					if (TEST(wp.extra, WALLFX_LOOP_DONT_SPIN | WALLFX_DONT_MOVE))
						continue;
	
					if (wp.extra != 0 && TEST(wp.extra, WALLFX_LOOP_OUTER)) {
						engine.dragpoint(skip, (short) k, wp.x += nx, wp.y += ny);
					} else {
						engine.getInterpolation(skip).setwallinterpolate(k, wp);
						wp.x += nx;
						wp.y += ny;
					}
	
					rot_ang = delta_ang;
	
					if (TEST(wp.extra, WALLFX_LOOP_REVERSE_SPIN))
						rot_ang = (short) -delta_ang;
	
					if (TEST(wp.extra, WALLFX_LOOP_SPIN_2X))
						rot_ang = (short) NORM_ANGLE(rot_ang * 2);
	
					if (TEST(wp.extra, WALLFX_LOOP_SPIN_4X))
						rot_ang = (short) NORM_ANGLE(rot_ang * 4);
	
					Point p = engine.rotatepoint(sop.xmid, sop.ymid, wp.x, wp.y, rot_ang);
					rx = p.getX();
					ry = p.getY();

					if (wp.extra != 0 && TEST(wp.extra, WALLFX_LOOP_OUTER)) {
						engine.dragpoint(skip, (short) k, rx, ry);
					} else {
						engine.getInterpolation(skip).setwallinterpolate(k, wp);
						wp.x = rx;
						wp.y = ry;
					}
				}
			}

			for (pnum = connecthead; pnum != -1; pnum = connectpoint2[pnum]) {
				pp = Player[pnum];

				// if controlling a sector object
				if (pp.sop != -1)
					continue;

				if (pp.lo_sectp == -1)
					continue;

				if (TEST(sector[pp.lo_sectp].extra, SECTFX_NO_RIDE)) 
					continue;

				// move the player
				if (pp.lo_sectp == sop.sector[j]) {
					if (PlayerMove)
						MovePlayer(pp, sopi, nx, ny);
				}
			}
		}

		for (i = 0; sop.sp_num[i] != -1; i++) {
			sp = sprite[sop.sp_num[i]];
			u = pUser[sop.sp_num[i]];
			
			engine.getInterpolation(skip).setsprinterpolate(sop.sp_num[i], sp);

			// if its a player sprite || NOT attached
			if (u == null || u.PlayerP != -1 || !TEST(u.Flags, SPR_SO_ATTACHED))
				continue;

			// move the player
			for (pnum = connecthead; pnum != -1; pnum = connectpoint2[pnum]) {
				pp = Player[pnum];

				if (pp.lo_sp != -1 && pp.lo_sp == sop.sp_num[i]) {
					if (PlayerMove)
						MovePlayer(pp, sopi, nx, ny);
				}
			}

			sp.x = sop.xmid - u.sx;
			sp.y = sop.ymid - u.sy;

			// sprites z update
			if (TEST(sop.flags, SOBJ_SPRITE_OBJ)) {
				// Sprite Objects follow zmid
				sp.z = sop.zmid - u.sz;
			} else {
				// Sector Objects can either have sprites ON or OFF of the sector
				if (TEST(u.Flags, SPR_ON_SO_SECTOR)) {
					// move with sector its on
					sp.z = sector[sp.sectnum].floorz - u.sz;
				} else {
					// move with the mid sector
					sp.z = sector[sop.mid_sector].floorz - u.sz;
				}
			}

			sp.ang = u.sang;

			if (TEST(u.Flags, SPR_ON_SO_SECTOR)) {
				if (TEST(sop.flags, SOBJ_DONT_ROTATE))
					continue;

				// IS part of a sector - sprite can do things based on the
				// current sector it is in
				if (TEST(wall[sector[sp.sectnum].wallptr].extra, WALLFX_LOOP_DONT_SPIN))
					continue;

				if (TEST(wall[sector[sp.sectnum].wallptr].extra, WALLFX_LOOP_REVERSE_SPIN)) {
					Point p = engine.rotatepoint(sop.xmid, sop.ymid, sp.x, sp.y, (short) -delta_ang);
					sp.x = p.getX();
					sp.y = p.getY();
					sp.ang = (short) NORM_ANGLE(sp.ang - delta_ang);
				} else {
					Point p = engine.rotatepoint(sop.xmid, sop.ymid, sp.x, sp.y, delta_ang);
					sp.x = p.getX();
					sp.y = p.getY();
					sp.ang = (short) NORM_ANGLE(sp.ang + delta_ang);
				}

			} else {
				if (!TEST(sop.flags, SOBJ_DONT_ROTATE)) {
					// NOT part of a sector - independant of any sector
					Point p = engine.rotatepoint(sop.xmid, sop.ymid, sp.x, sp.y, delta_ang);
					sp.x = p.getX();
					sp.y = p.getY();
					sp.ang = (short) NORM_ANGLE(sp.ang + delta_ang);
				}

				// Does not necessarily move with the sector so must accout for
				// moving across sectors
				if (sop.xmid < (long) MAXSO) // special case for operating SO's
					engine.setspritez(sop.sp_num[i], sp.x, sp.y, sp.z);
			}

			if (TEST(sp.extra, SPRX_BLADE)) {
				DoBladeDamage(sop.sp_num[i]);
			}
		}

		for (pnum = connecthead; pnum != -1; pnum = connectpoint2[pnum]) {
			pp = Player[pnum];

			// if player was on a sector object
			if (pp.sop_riding != -1) {
				// update here AFTER sectors/player has been manipulated
				// prevents you from falling into map HOLEs created by moving
				// Sectors and sprites around.
				// if (sop.xmid < (long)MAXSO)
				pp.cursectnum = COVERupdatesector(pp.posx, pp.posy, pp.cursectnum);

				// in case you are in a whirlpool
				// move perfectly with the ride in the z direction
				if (TEST(pp.Flags, PF_CRAWLING)) {
					// move up some for really fast moving plats
					// pp.posz -= PLAYER_HEIGHT + Z(12);
					DoPlayerZrange(pp);
					pp.posz = pp.loz - PLAYER_CRAWL_HEIGHT;
					pp.getSprite().z = pp.loz;
				} else {
					// move up some for really fast moving plats
					// pp.posz -= Z(24);
					DoPlayerZrange(pp);

					if (!TEST(pp.Flags, PF_JUMPING | PF_FALLING | PF_FLYING)) {
						pp.posz = pp.loz - PLAYER_HEIGHT;
						pp.getSprite().z = pp.loz;
					}
				}
			} else {
				// if player was not on any sector object set Riding flag to false
				pp.Flags &= ~(PF_PLAYER_RIDING);
			}
		}
	}

	public static void RefreshPoints(int sopi, int nx, int ny, boolean dynamic, int skip) {
		short wallcount = 0, j, k, startwall, endwall, delta_ang_from_orig;
		SECTOR sectp;
		WALL wp;
		short ang;
		int dx, dy, x, y;

		Sector_Object sop = SectorObject[sopi];
		// do scaling
		if (dynamic && sop.PreMoveAnimator != null)
			sop.PreMoveAnimator.invoke(sopi);

		for (j = 0; sop.sector[j] != -1; j++) {
			sectp = sector[sop.sector[j]];

			if (!TEST(sop.flags, SOBJ_SPRITE_OBJ)) {
				startwall = sectp.wallptr;
				endwall = (short) (startwall + sectp.wallnum - 1);

				// move all walls in sectors back to the original position
				for (k = startwall; k <= endwall; k++) {
					wp = wall[k];

					if (!(wp.extra != 0 && TEST(wp.extra, WALLFX_DONT_MOVE))) {
						dx = x = sop.xmid - sop.xorig[wallcount];
						dy = y = sop.ymid - sop.yorig[wallcount];

						if (dynamic && sop.scale_type != 0) {
							if (!TEST(wp.extra, WALLFX_DONT_SCALE)) {
								ang = (short) NORM_ANGLE(engine.getangle(x - sop.xmid, y - sop.ymid));

								if (sop.scale_type == SO_SCALE_RANDOM_POINT) {
									// was causing memory overwrites
									Vector2i d = ScaleRandomPoint(sop, wallcount, ang, x, y);
									dx = d.x;
									dy = d.y;
								} else {
									int xmul = (sop.scale_dist * sop.scale_x_mult) >> 8;
									int ymul = (sop.scale_dist * sop.scale_y_mult) >> 8;

									dx = x + ((xmul * sintable[NORM_ANGLE(ang + 512)]) >> 14);
									dy = y + ((ymul * sintable[ang]) >> 14);
								}
							}
						}

						if (wp.extra != 0 && TEST(wp.extra, WALLFX_LOOP_OUTER)) {
							engine.dragpoint(skip, k, dx, dy);
						} else { 
							engine.getInterpolation(skip).setwallinterpolate(k, wp);
							wp.x = dx;
							wp.y = dy;
						}
					}

					wallcount++;
				}
			}
		}

		if (sop.spin_speed != 0) {
			// same as below - ignore the objects angle
			// last_ang is the last true angle before SO started spinning
			delta_ang_from_orig = (short) NORM_ANGLE(sop.last_ang + sop.spin_ang - sop.ang_orig);
		} else {
			// angle traveling + the new spin angle all offset from the original
			// angle
			delta_ang_from_orig = (short) NORM_ANGLE(sop.ang + sop.spin_ang - sop.ang_orig);
		}

		// Note that this delta angle is from the original angle
		// nx,ny are 0 so the points are not moved, just rotated
		MovePoints(sopi, delta_ang_from_orig, nx, ny, skip);

		// do morphing - angle independent
		if (dynamic && sop.PostMoveAnimator != null)
			sop.PostMoveAnimator.invoke(sopi);
	}

	public static void KillSectorObjectSprites(Sector_Object sop) {
		SPRITE sp;
		USER u;
		int i;

		for (i = 0; sop.sp_num[i] != -1; i++) {
			sp = sprite[sop.sp_num[i]];
			u = pUser[sop.sp_num[i]];

			// not a part of the so anymore
			u.Flags &= ~(SPR_SO_ATTACHED);

			if (sp.picnum == ST1 && sp.hitag == SPAWN_SPOT)
				continue;

			KillSprite(sop.sp_num[i]);
		}

		// clear the list
		sop.sp_num[0] = -1;
	}

	public static void UpdateSectorObjectSprites(Sector_Object sop) {
		SPRITE sp;
		int i;

		for (i = 0; sop.sp_num[i] != -1; i++) {
			sp = sprite[sop.sp_num[i]];
			engine.setspritez(sop.sp_num[i], sp.x, sp.y, sp.z);
		}
	}

	public static int DetectSectorObject(SECTOR sectph) {
		short j;
		SECTOR sectp;
		Sector_Object sop;

		// collapse the SO to a single point
		// move all points to nx,ny
		for (int s = 0; s < MAX_SECTOR_OBJECTS; s++) {
			sop = SectorObject[s];
			if (sop.xmid == MAXLONG || sop.xmid == MAXSO)
				continue;

			for (j = 0; sop.sector[j] != -1; j++) {
				sectp = sector[sop.sector[j]];
				if (sectph == sectp)
					return s;
			}
		}

		return -1;
	}

	public static Sector_Object DetectSectorObjectByWall(WALL wph) {
		short j, k, startwall, endwall;
		SECTOR sectp;
		WALL wp;
		Sector_Object sop;

		// collapse the SO to a single point
		// move all points to nx,ny
		for (int s = 0; s < MAX_SECTOR_OBJECTS; s++) {
			sop = SectorObject[s];
			if (sop.xmid == MAXLONG || sop.xmid == MAXSO)
				continue;

			for (j = 0; sop.sector[j] != -1; j++) {
				sectp = sector[sop.sector[j]];

				startwall = sectp.wallptr;
				endwall = (short) (startwall + sectp.wallnum - 1);

				for (k = startwall; k <= endwall; k++) {
					wp = wall[k];
					// if outer wall check the NEXTWALL also
					if (TEST(wp.extra, WALLFX_LOOP_OUTER)) {
						if (wph == wall[wp.nextwall])
							return (sop);
					}

					if (wph == wp)
						return (sop);
				}
			}
		}

		return null;
	}

	public static void CollapseSectorObject(Sector_Object sop, int nx, int ny) {
		short j, k, startwall, endwall;
		SECTOR sectp;
		WALL wp;

		// collapse the SO to a single point
		// move all points to nx,ny
		for (j = 0; sop.sector[j] != -1; j++) {
			sectp = sector[sop.sector[j]];

			if (!TEST(sop.flags, SOBJ_SPRITE_OBJ)) {
				startwall = (sectp).wallptr;
				endwall = (short) (startwall + (sectp).wallnum - 1);

				// move all walls in sectors back to the original position
				for (k = startwall; k <= endwall; k++) {
					wp = wall[k];
					if (TEST(wp.extra, WALLFX_DONT_MOVE))
						continue;

					if (wp.extra != 0 && TEST(wp.extra, WALLFX_LOOP_OUTER)) {
						engine.dragpoint(k, nx, ny);
					} else {
						wp.x = nx;
						wp.y = ny;
					}
				}
			}
		}
	}

	public static void MoveZ(Sector_Object sop) {
		short i;
		SECTOR sectp;

		if (sop.bob_amt != 0) {
			sop.bob_sine_ndx = (short) ((totalsynctics << sop.bob_speed) & 2047);
			sop.bob_diff = ((sop.bob_amt * sintable[sop.bob_sine_ndx]) >> 14);

			// for all sectors
			for (i = 0; sop.sector[i] != -1; i++) {
				sectp = sector[sop.sector[i]];
				if (SectUser[sop.sector[i]] != null && TEST(SectUser[sop.sector[i]].flags, SECTFU_SO_DONT_BOB))
					continue;

				(sectp).floorz = sop.zorig_floor[i] + sop.bob_diff;
			}
		}

		if (TEST(sop.flags, SOBJ_MOVE_VERTICAL)) {
			i = (short) AnimGetGoal(sop, AnimType.SectorObjectZ);
			if (i < 0)
				sop.flags &= ~(SOBJ_MOVE_VERTICAL);
		}

		if (TEST(sop.flags, SOBJ_ZDIFF_MODE)) {
			return;
		}

		// move all floors
		if (TEST(sop.flags, SOBJ_ZDOWN)) {
			for (i = 0; sop.sector[i] != -1; i++) {
				sectp = sector[sop.sector[i]];
				AnimSet(sop.sector[i], sop.zorig_floor[i] + sop.z_tgt, sop.z_rate, AnimType.FloorZ);
			}

			sop.flags &= ~(SOBJ_ZDOWN);
		} else if (TEST(sop.flags, SOBJ_ZUP)) {
			for (i = 0; sop.sector[i] != -1; i++) {
				sectp = sector[sop.sector[i]];
				AnimSet(sop.sector[i], sop.zorig_floor[i] + sop.z_tgt, sop.z_rate, AnimType.FloorZ);
			}

			sop.flags &= ~(SOBJ_ZUP);
		}
	}

	public static void CallbackSOsink(Anim ap, Sector_Object data) {
		Sector_Object sop;
		SPRITE sp;
		USER u;
		short startwall, endwall, j;
		short dest_sector = -1;
		short src_sector = -1;
		short i, nexti, ndx;
		int tgt_depth;

		sop = data;

		for (i = 0; sop.sector[i] != -1; i++) {
			if (SectUser[sop.sector[i]] != null && TEST(SectUser[sop.sector[i]].flags, SECTFU_SO_SINK_DEST)) {
				src_sector = sop.sector[i];
				break;
			}
		}

		for (i = 0; sop.sector[i] != -1; i++) {
			if (ap.ptr == sector[sop.sector[i]] && ap.index == sop.sector[i] && ap.type == AnimType.FloorZ) {
				dest_sector = sop.sector[i];
				break;
			}
		}

		sector[dest_sector].floorpicnum = sector[src_sector].floorpicnum;
		sector[dest_sector].floorshade = sector[src_sector].floorshade;

		sector[dest_sector].floorstat &= ~(FLOOR_STAT_RELATIVE);

		tgt_depth = (GetSectUser(src_sector)).depth;

		short sectnum;
		for (sectnum = 0; sectnum < numsectors; sectnum++) {
			if (sectnum == dest_sector) {
				// This is interesting
				// Added a depth_fract to the struct so I could do a
				// 16.16 Fixed point representation to change the depth
				// in a more precise way
				ndx = (short) AnimSet(sectnum, tgt_depth << 16, (ap.vel << 8) >> 8, AnimType.SectUserDepth);
				AnimSetVelAdj(ndx, ap.vel_adj);
				break;
			}
		}

		for (i = headspritesect[dest_sector]; i != -1; i = nexti) {
			nexti = nextspritesect[i];
			sp = sprite[i];
			u = pUser[i];

			if (u == null || u.PlayerP != -1 || !TEST(u.Flags, SPR_SO_ATTACHED))
				continue;

			// move sprite WAY down in water
			ndx = (short) AnimSet(i, -u.sz - SPRITEp_SIZE_Z(sp) - Z(100), ap.vel >> 8, AnimType.UserZ);
			AnimSetVelAdj(ndx, ap.vel_adj);
		}

		// Take out any blocking walls
		startwall = sector[dest_sector].wallptr;
		endwall = (short) (startwall + sector[dest_sector].wallnum - 1);
		for (j = startwall; j <= endwall; j++) {
			wall[j].cstat &= ~(CSTAT_WALL_BLOCK);
		}
	}

	public static void MoveSectorObjects(int sopi, int locktics, int skip) {
		Sector_Object sop = SectorObject[sopi];
		short speed;
		short delta_ang;

		if (sop.track >= SO_OPERATE_TRACK_START) {
			if (TEST(sop.flags, SOBJ_UPDATE_ONCE)) {
				sop.flags &= ~(SOBJ_UPDATE_ONCE);
				RefreshPoints(sopi, 0, 0, false, skip);
			}
			return;
		}

		int nx = 0;
		int ny = 0;

		// if pausing the return
		if (sop.wait_tics != 0) {
			sop.wait_tics -= locktics;
			if (sop.wait_tics <= 0)
				sop.wait_tics = 0;

			return;
		}

		delta_ang = 0;

		if (sop.track > -1) {
			DoTrack(sopi, locktics, tmp_ptr[0].set(0), tmp_ptr[1].set(0));
			nx = tmp_ptr[0].value;
			ny = tmp_ptr[1].value;
		}

		// get delta to target angle
		delta_ang = (short) GetDeltaAngle(sop.ang_tgt, sop.ang);

		sop.ang = (short) NORM_ANGLE(sop.ang + (delta_ang >> sop.turn_speed));
		delta_ang = (short) (delta_ang >> sop.turn_speed);

		// move z values
		MoveZ(sop);

		// calculate the spin speed
		speed = (short) (sop.spin_speed * locktics);
		// spin_ang is incremented by the spin_speed
		sop.spin_ang = (short) NORM_ANGLE(sop.spin_ang + speed);

		if (sop.spin_speed != 0) {
			// ignore delta angle if spinning
			GlobSpeedSO = speed;
		} else {
			// The actual delta from the last frame
			GlobSpeedSO = speed;
			GlobSpeedSO += delta_ang;
		}

		if (TEST(sop.flags, SOBJ_DYNAMIC)) {
			// trick tricks
			RefreshPoints(sopi, nx, ny, true, skip);
		} else {
			// Update the points so there will be no warping
			if (TEST(sop.flags, SOBJ_UPDATE | SOBJ_UPDATE_ONCE) || sop.vel != 0 || (sop.ang != sop.ang_tgt)
					|| GlobSpeedSO != 0) {
				sop.flags &= ~(SOBJ_UPDATE_ONCE);
				RefreshPoints(sopi, nx, ny, false, skip);
			}
		}
	}

	public static final int TRACK_POINT_SIZE = 200;

	public static void DoTrack(int sopi, int locktics, LONGp nx, LONGp ny) {
		TRACK_POINT tpoint;
		int dx, dy, dz;
		int dist;

		Sector_Object sop = SectorObject[sopi];

		tpoint = Track[sop.track].TrackPoint[sop.point];

		// calculate an angle to the target

		if (sop.vel != 0)
			sop.ang_moving = sop.ang_tgt = engine.getangle(tpoint.x - sop.xmid, tpoint.y - sop.ymid);

		// NOTE: Jittery ride - try new value out here
		// NOTE: Put a loop around this (locktics) to make it more acuruate

		if (sop.target_dist < 100) {
			switch (tpoint.tag_low) {
			case TRACK_MATCH_EVERYTHING:
				DoMatchEverything(null, tpoint.tag_high, -1);
				break;

			case TRACK_MATCH_EVERYTHING_ONCE:
				DoMatchEverything(null, tpoint.tag_high, -1);
				tpoint.tag_low = 0;
				tpoint.tag_high = 0;
				break;

			case TRACK_SPIN:
				if (sop.spin_speed != 0)
					break;

				sop.spin_speed = tpoint.tag_high;
				sop.last_ang = sop.ang;
				break;

			case TRACK_SPIN_REVERSE: {
				if (sop.spin_speed == 0)
					break;

				if (sop.spin_speed >= 0) {
					sop.spin_speed = (short) -sop.spin_speed;
				}
			}
				break;

			case TRACK_SPIN_STOP:
				if (sop.spin_speed == 0)
					break;

				sop.spin_speed = 0;
				break;

			case TRACK_BOB_START:
				sop.flags |= (SOBJ_ZMID_FLOOR);
				sop.bob_amt = Z(tpoint.tag_high);
				sop.bob_sine_ndx = 0;
				sop.bob_speed = 4;
				break;

			case TRACK_BOB_STOP:
				sop.bob_speed = 0;
				sop.bob_sine_ndx = 0;
				sop.bob_amt = 0;
				break;

			case TRACK_BOB_SPEED:
				sop.bob_speed = tpoint.tag_high;
				break;

			case TRACK_REVERSE:
				sop.dir *= -1;
				break;
			case TRACK_STOP:
				sop.vel = 0;
				sop.wait_tics = (short) (tpoint.tag_high * 128);
				break;
			case TRACK_SET_SPEED:
				sop.vel = tpoint.tag_high * 256;
				sop.vel_tgt = sop.vel;
				break;

			//
			// Controls the velocity
			//

			case TRACK_VEL_RATE:
				sop.vel_rate = tpoint.tag_high;
				break;
			case TRACK_SPEED_UP:
				sop.flags &= ~(SOBJ_SLOW_DOWN | SOBJ_SPEED_UP);
				if (sop.dir < 0) {
					// set target to new slower target
					sop.vel_tgt = sop.vel_tgt - (tpoint.tag_high * 256);
					sop.flags |= (SOBJ_SLOW_DOWN);
				} else {
					sop.vel_tgt = sop.vel_tgt + (tpoint.tag_high * 256);
					sop.flags |= (SOBJ_SPEED_UP);
				}

				break;

			case TRACK_SLOW_DOWN:
				sop.flags &= ~(SOBJ_SLOW_DOWN | SOBJ_SPEED_UP);
				if (sop.dir > 0) {
					sop.vel_tgt = sop.vel_tgt - (tpoint.tag_high * 256);
					sop.flags |= (SOBJ_SLOW_DOWN);
				} else {
					sop.vel_tgt = sop.vel_tgt + (tpoint.tag_high * 256);
					sop.flags |= (SOBJ_SPEED_UP);
				}
				break;

			//
			// Controls z
			//

			case TRACK_SO_SINK: {
				short dest_sector = -1;
				short i, ndx;

				for (i = 0; sop.sector[i] != -1; i++) {
					if (SectUser[sop.sector[i]] != null && TEST(SectUser[sop.sector[i]].flags, SECTFU_SO_SINK_DEST)) {
						dest_sector = sop.sector[i];
						break;
					}
				}

				sop.bob_speed = 0;
				sop.bob_sine_ndx = 0;
				sop.bob_amt = 0;

				for (i = 0; sop.sector[i] != -1; i++) {
					if (SectUser[sop.sector[i]] != null && TEST(SectUser[sop.sector[i]].flags, SECTFU_SO_DONT_SINK))
						continue;

					ndx = (short) AnimSet(sop.sector[i], sector[dest_sector].floorz, tpoint.tag_high, AnimType.FloorZ);
					AnimSetCallback(ndx, AnimCallback.SOsink, sopi);
					AnimSetVelAdj(ndx, 6);
				}

				break;
			}

			case TRACK_SO_FORM_WHIRLPOOL: {
				// for lowering the whirlpool in level 1
				SECTOR sectp;
				short i;
				Sect_User sectu;

				for (i = 0; sop.sector[i] != -1; i++) {
					sectp = sector[sop.sector[i]];
					sectu = SectUser[sop.sector[i]];

					if (sectu != null && sectu.stag == SECT_SO_FORM_WHIRLPOOL) {
						AnimSet(sop.sector[i], (sectp).floorz + Z(sectu.height), 128, AnimType.FloorZ);
						sectp.floorshade += sectu.height / 6;
						(sectp).extra &= ~(SECTFX_NO_RIDE);
					}
				}

				break;
			}

			case TRACK_MOVE_VERTICAL: {
				int zr;
				sop.flags |= (SOBJ_MOVE_VERTICAL);

				if (tpoint.tag_high > 0)
					zr = tpoint.tag_high;
				else
					zr = 256;

				// look at the next point
				NextTrackPoint(sop);
				tpoint = Track[sop.track].TrackPoint[sop.point];

				// set anim
				AnimSet(sopi, tpoint.z, zr, AnimType.SectorObjectZ);

				// move back to current point by reversing direction
				sop.dir *= -1;
				NextTrackPoint(sop);
				tpoint = Track[sop.track].TrackPoint[sop.point];
				sop.dir *= -1;

				break;
			}

			case TRACK_WAIT_FOR_EVENT: {
				if (tpoint.tag_high == -1)
					break;

				sop.flags |= (SOBJ_WAIT_FOR_EVENT);
				sop.save_vel = (short) sop.vel;
				sop.save_spin_speed = sop.spin_speed;

				sop.vel = sop.spin_speed = 0;
				// only set event if non-zero
				if (tpoint.tag_high != 0)
					sop.match_event = tpoint.tag_high;
				tpoint.tag_high = -1;
				break;
			}

			case TRACK_ZDIFF_MODE:
				sop.flags |= (SOBJ_ZDIFF_MODE);
				sop.zdelta = Z(tpoint.tag_high);
				break;
			case TRACK_ZRATE:
				sop.z_rate = Z(tpoint.tag_high);
				break;
			case TRACK_ZUP:
				sop.flags &= ~(SOBJ_ZDOWN | SOBJ_ZUP);
				if (sop.dir < 0) {
					sop.z_tgt = sop.z_tgt + Z(tpoint.tag_high);
					sop.flags |= (SOBJ_ZDOWN);
				} else {
					sop.z_tgt = sop.z_tgt - Z(tpoint.tag_high);
					sop.flags |= (SOBJ_ZUP);
				}
				break;
			case TRACK_ZDOWN:
				sop.flags &= ~(SOBJ_ZDOWN | SOBJ_ZUP);
				if (sop.dir > 0) {
					sop.z_tgt = sop.z_tgt + Z(tpoint.tag_high);
					sop.flags |= (SOBJ_ZDOWN);
				} else {
					sop.z_tgt = sop.z_tgt - Z(tpoint.tag_high);
					sop.flags |= (SOBJ_ZUP);
				}
				break;
			}

			// get the next point
			NextTrackPoint(sop);
			tpoint = Track[sop.track].TrackPoint[sop.point];

			// calculate distance to target poing
			sop.target_dist = Distance(sop.xmid, sop.ymid, tpoint.x, tpoint.y);

			// calculate a new angle to the target
			sop.ang_moving = sop.ang_tgt = engine.getangle(tpoint.x - sop.xmid, tpoint.y - sop.ymid);

			if (TEST(sop.flags, SOBJ_ZDIFF_MODE)) {
				short i;

				// set dx,dy,dz up for finding the z magnitude
				dx = tpoint.x;
				dy = tpoint.y;
				dz = tpoint.z - sop.zdelta;

				// find the distance to the target (player)
				dist = DIST(dx, dy, sop.xmid, sop.ymid);

				// (velocity * difference between the target and the object)
				// / distance
				sop.z_rate = (sop.vel * (sop.zmid - dz)) / dist;

				// take absolute value and convert to pixels (divide by 256)
				sop.z_rate = PIXZ(klabs(sop.z_rate));

				if (TEST(sop.flags, SOBJ_SPRITE_OBJ)) {
					// only modify zmid for sprite_objects
					AnimSet(sopi, dz, sop.z_rate, AnimType.SectorObjectZ);
				} else {
					// churn through sectors setting their new z values
					for (i = 0; sop.sector[i] != -1; i++) {
						AnimSet(sop.sector[i], dz - (sector[sop.mid_sector].floorz - sector[sop.sector[i]].floorz),
								sop.z_rate, AnimType.FloorZ);
					}
				}
			}
		} else {

			// make velocity approach the target velocity
			if (TEST(sop.flags, SOBJ_SPEED_UP)) {
				if ((sop.vel += (locktics << sop.vel_rate)) >= sop.vel_tgt) {
					sop.vel = sop.vel_tgt;
					sop.flags &= ~(SOBJ_SPEED_UP);
				}
			} else if (TEST(sop.flags, SOBJ_SLOW_DOWN)) {
				if ((sop.vel -= (locktics << sop.vel_rate)) <= sop.vel_tgt) {
					sop.vel = sop.vel_tgt;
					sop.flags &= ~(SOBJ_SLOW_DOWN);
				}
			}
		}

		// calculate a new x and y
		if (sop.vel != 0 && !TEST(sop.flags, SOBJ_MOVE_VERTICAL)) {
			nx.value = (DIV256(sop.vel)) * locktics * sintable[NORM_ANGLE(sop.ang_moving + 512)] >> 14;
			ny.value = (DIV256(sop.vel)) * locktics * sintable[sop.ang_moving] >> 14;

			dist = Distance(sop.xmid, sop.ymid, sop.xmid + nx.value, sop.ymid + ny.value);
			sop.target_dist -= dist;
		}
	}

	public static void OperateSectorObject(int sopi, float newang, int newx, int newy, int skip) {
		int i;
		SECTOR sectp;

		if (Prediction)
			return;

		Sector_Object sop = SectorObject[sopi];
		if (sop.track < SO_OPERATE_TRACK_START)
			return;

		if (sop.bob_amt != 0) {
			sop.bob_sine_ndx = (short) ((totalsynctics << sop.bob_speed) & 2047);
			sop.bob_diff = ((sop.bob_amt * sintable[sop.bob_sine_ndx]) >> 14);

			// for all sectors
			for (i = 0; sop.sector[i] != -1; i++) {
				sectp = sector[sop.sector[i]];

				if (SectUser[sop.sector[i]] != null && TEST(SectUser[sop.sector[i]].flags, SECTFU_SO_DONT_BOB))
					continue;

				sectp.floorz = sop.zorig_floor[i] + sop.bob_diff;
			}
		}

		GlobSpeedSO = 0;

		// sop.ang_tgt = newang;
		sop.ang_moving = (short) newang;

		sop.spin_ang = 0;
		sop.ang = (short) newang;

		RefreshPoints(sopi, newx - sop.xmid, newy - sop.ymid, false, skip);
	}

	public static void PlaceSectorObject(int sopi, int newang, int newx, int newy) {
		Sector_Object sop = SectorObject[sopi];
		RefreshPoints(sopi, newx - sop.xmid, newy - sop.ymid, false, 1);
	}

	public static void VehicleSetSmoke(Sector_Object sop, Animator animator) {
		short SpriteNum, NextSprite;
		SPRITE sp;
		USER u;
		for (int j = 0; sop.sector[j] != -1; j++) {
			for (SpriteNum = headspritesect[sop.sector[j]]; SpriteNum != -1; SpriteNum = NextSprite) {
				NextSprite = nextspritesect[SpriteNum];
				sp = sprite[SpriteNum];
				u = pUser[SpriteNum];

				switch (sp.hitag) {

				case SPAWN_SPOT:
					if (sp.clipdist == 3) {
						if (animator != null) {
							if (sp.statnum == STAT_NO_STATE)
								break;

							change_sprite_stat(SpriteNum, STAT_NO_STATE);
							DoSoundSpotMatch(sp.lotag, 1, SoundType.SOUND_OBJECT_TYPE);
							DoSpawnSpotsForDamage(sp.lotag);
						} else {
							change_sprite_stat(SpriteNum, STAT_SPAWN_SPOT);
							DoSoundSpotStopSound(sp.lotag);
						}

						u.ActorActionFunc = animator;
					}
					break;
				}
			}
		}
	}

	public static void KillSectorObject(int sopi) {
		int newx = MAXSO;
		int newy = MAXSO;
		short newang = 0;

		Sector_Object sop = SectorObject[sopi];
		if (sop.track < SO_OPERATE_TRACK_START)
			return;

		sop.ang_tgt = sop.ang_moving = newang;

		sop.spin_ang = 0;
		sop.ang = sop.ang_tgt;

		RefreshPoints(sopi, newx - sop.xmid, newy - sop.ymid, false, 1);
	}

	public static void TornadoSpin(Sector_Object sop) {
		short delta_ang, speed;
		short locktics = synctics;

		// get delta to target angle
		delta_ang = (short) GetDeltaAngle(sop.ang_tgt, sop.ang);

		sop.ang = (short) NORM_ANGLE(sop.ang + (delta_ang >> sop.turn_speed));
		delta_ang = (short) (delta_ang >> sop.turn_speed);

		// move z values
		MoveZ(sop);

		// calculate the spin speed
		speed = (short) (sop.spin_speed * locktics);
		// spin_ang is incremented by the spin_speed
		sop.spin_ang = (short) NORM_ANGLE(sop.spin_ang + speed);

		if (sop.spin_speed != 0) {
			// ignore delta angle if spinning
			GlobSpeedSO = speed;
		} else {
			// The actual delta from the last frame
			GlobSpeedSO = speed;
			GlobSpeedSO += delta_ang;
		}
	}

	public static void DoTornadoObject(int sopi) {
		int xvect, yvect;
		short cursect;
		// this made them move together more or less - cool!
		// static short ang = 1024;
		int floor_dist;
		int x, y, z;
		int ret;
		
		Sector_Object sop = SectorObject[sopi];

		xvect = (sop.vel * sintable[NORM_ANGLE(sop.ang_moving + 512)]);
		yvect = (sop.vel * sintable[NORM_ANGLE(sop.ang_moving)]);

		cursect = sop.op_main_sector; // for sop.vel
		floor_dist = DIV4(klabs(sector[cursect].ceilingz - sector[cursect].floorz));
		x = sop.xmid;
		y = sop.ymid;
		z = floor_dist;

		PlaceSectorObject(sopi, sop.ang_moving, MAXSO, MAXSO);
		ret = engine.clipmove(x, y, z, cursect, xvect, yvect, sop.clipdist, Z(0), floor_dist, CLIPMASK_ACTOR);

		x = clipmove_x;
		y = clipmove_y;
		z = clipmove_z;
		cursect = clipmove_sectnum;

		if (ret != 0) {
			sop.ang_moving = (short) NORM_ANGLE(sop.ang_moving + 1024 + RANDOM_P2(512) - 256);
		}

		TornadoSpin(sop);
		RefreshPoints(sopi, x - sop.xmid, y - sop.ymid, true, 1);
	}

	public static void DoAutoTurretObject(int sopi) {
		Sector_Object sop = SectorObject[sopi];
		
		short SpriteNum = (short) sop.sp_child;
		SPRITE shootp;
		USER u = pUser[SpriteNum];
		short delta_ang;
		int diff;
		short i;

		if (sop.max_damage != -9999 && sop.max_damage <= 0)
			return;

		u.WaitTics -= synctics;

		// check for new player if doesn't have a target or time limit expired
		if (u.tgt_sp == -1 || u.WaitTics < 0) {
			// 4 seconds
			u.WaitTics = 4 * 120;
			DoActorPickClosePlayer(SpriteNum);
		}

		if (!MoveSkip2) {
			for (i = 0; sop.sp_num[i] != -1; i++) {
				if (sprite[sop.sp_num[i]].statnum == STAT_SO_SHOOT_POINT) {
					shootp = sprite[sop.sp_num[i]];

					if (!FAFcansee(shootp.x, shootp.y, shootp.z - Z(4), shootp.sectnum, sprite[u.tgt_sp].x,
							sprite[u.tgt_sp].y, SPRITEp_UPPER(sprite[u.tgt_sp]), sprite[u.tgt_sp].sectnum)) {
						return;
					}
				}
			}

			// FirePausing
			if (u.Counter > 0) {
				u.Counter -= synctics * 2;
				if (u.Counter <= 0)
					u.Counter = 0;
			}

			if (u.Counter == 0) {
				shootp = null;
				for (i = 0; sop.sp_num[i] != -1; i++) {
					if (sprite[sop.sp_num[i]].statnum == STAT_SO_SHOOT_POINT) {
						shootp = sprite[sop.sp_num[i]];

						if (SP_TAG5(shootp) != 0)
							u.Counter = (short) SP_TAG5(shootp);
						else
							u.Counter = 12;
						InitTurretMgun(sop);
					}
				}
			}

			// sop.ang_tgt = getangle(sop.xmid - u.tgt_sp.x, sop.ymid - u.tgt_sp.y);
			sop.ang_tgt = engine.getangle(sprite[u.tgt_sp].x - sop.xmid, sprite[u.tgt_sp].y - sop.ymid);

			// get delta to target angle
			delta_ang = (short) GetDeltaAngle(sop.ang_tgt, sop.ang);

			// sop.ang += delta_ang >> 4;
			sop.ang = (short) NORM_ANGLE(sop.ang + (delta_ang >> 3));
			// sop.ang += delta_ang >> 2;

			if (sop.limit_ang_center >= 0) {
				diff = GetDeltaAngle(sop.ang, sop.limit_ang_center);

				if (klabs(diff) >= sop.limit_ang_delta) {
					if (diff < 0)
						sop.ang = (short) (sop.limit_ang_center - sop.limit_ang_delta);
					else
						sop.ang = (short) (sop.limit_ang_center + sop.limit_ang_delta);

				}
			}

			OperateSectorObject(sopi, sop.ang, sop.xmid, sop.ymid, 2);
		}
	}

	public static void DoActorHitTrackEndPoint(USER u) {
		SPRITE sp = u.getSprite();

		Track[u.track].flags &= ~(TF_TRACK_OCCUPIED);

		// jump the current track & determine if you should go to another
		if (TEST(u.Flags, SPR_RUN_AWAY)) {

			// look for another track leading away from the player
			u.track = FindTrackAwayFromPlayer(u);

			if (u.track >= 0) {
				sp.ang = (short) NORM_ANGLE(engine.getangle((Track[u.track].TrackPoint[u.point]).x - sp.x,
						(Track[u.track].TrackPoint[u.point]).y - sp.y));
			} else {
				u.Flags &= ~(SPR_RUN_AWAY);
				DoActorSetSpeed(u.SpriteNum, NORM_SPEED);
				u.track = -1;
			}
		} else if (TEST(u.Flags, SPR_FIND_PLAYER)) {
			// look for another track leading away from the player
			u.track = (short) FindTrackToPlayer(u);

			if (u.track >= 0) {
				sp.ang = (short) NORM_ANGLE(engine.getangle((Track[u.track].TrackPoint[u.point]).x - sp.x,
						(Track[u.track].TrackPoint[u.point]).y - sp.y));
			} else {
				u.Flags &= ~(SPR_FIND_PLAYER);
				DoActorSetSpeed(u.SpriteNum, NORM_SPEED);
				u.track = -1;
			}
		} else {
			u.track = -1;
		}
	}

	public static void ActorLeaveTrack(int SpriteNum) {
		USER u = pUser[SpriteNum];

		if (u.track == -1)
			return;

		u.Flags &= ~(SPR_FIND_PLAYER | SPR_RUN_AWAY | SPR_CLIMBING);
		Track[u.track].flags &= ~(TF_TRACK_OCCUPIED);
		u.track = -1;
	}

	private static int[] z = new int[2];

	public static boolean ActorTrackDecide(TRACK_POINT tpoint, int SpriteNum) {
		SPRITE sp;
		USER u = pUser[SpriteNum];

		sp = u.getSprite();

		switch (tpoint.tag_low) {
		case TRACK_START:

			// if track has a type and actor is going the right direction jump
			// the track
			if (Track[u.track].ttflags != 0) {
				if (u.track_dir == -1) {
					DoActorHitTrackEndPoint(u);
					return (false);
				}
			}

			break;

		case TRACK_END:
			// if track has a type and actor is going to right direction jump the
			// track
			if (Track[u.track].ttflags != 0) {
				if (u.track_dir == 1) {
					DoActorHitTrackEndPoint(u);
					return (false);
				}
			}

			break;

		case TRACK_ACTOR_WAIT_FOR_PLAYER: {
			u.Flags |= (SPR_WAIT_FOR_PLAYER);
			u.Dist = tpoint.tag_high;
			break;
		}

		case TRACK_ACTOR_WAIT_FOR_TRIGGER: {
			u.Flags |= (SPR_WAIT_FOR_TRIGGER);
			u.Dist = tpoint.tag_high;
			break;
		}

		//
		// Controls the velocity
		//

		case TRACK_ACTOR_VEL_RATE:
			u.vel_rate = tpoint.tag_high;
			break;
		case TRACK_ACTOR_SPEED_UP:
			u.Flags &= ~(SPR_SLOW_DOWN | SPR_SPEED_UP);
			if (u.track_dir < 0) {
				// set target to new slower target
				u.vel_tgt = u.vel_tgt - (tpoint.tag_high * 256);
				u.Flags |= (SPR_SLOW_DOWN);
			} else {
				u.vel_tgt = u.vel_tgt + (tpoint.tag_high * 256);
				u.Flags |= (SPR_SPEED_UP);
			}

			break;

		case TRACK_ACTOR_SLOW_DOWN:
			u.Flags &= ~(SPR_SLOW_DOWN | SPR_SPEED_UP);
			if (u.track_dir > 0) {
				u.vel_tgt = u.vel_tgt - (tpoint.tag_high * 256);
				u.Flags |= (SPR_SLOW_DOWN);
			} else {
				u.vel_tgt = u.vel_tgt + (tpoint.tag_high * 256);
				u.Flags |= (SPR_SPEED_UP);
			}
			break;

		// Reverse it
		case TRACK_ACTOR_REVERSE:
			u.track_dir *= -1;
			break;

		case TRACK_ACTOR_STAND:
			NewStateGroup(SpriteNum, u.ActorActionSet.Stand);
			break;

		case TRACK_ACTOR_JUMP:
			if (u.ActorActionSet.Jump != null) {
				sp.ang = tpoint.ang;

				if (tpoint.tag_high == 0)
					u.jump_speed = ACTOR_STD_JUMP;
				else
					u.jump_speed = (short) -tpoint.tag_high;

				DoActorBeginJump(SpriteNum);
				u.ActorActionFunc = DoActorMoveJump;
			}

			break;

		case TRACK_ACTOR_QUICK_JUMP:
		case TRACK_ACTOR_QUICK_SUPER_JUMP:
			if (u.ActorActionSet.Jump != null) {
				short hitwall, hitsprite;
				int zdiff;

				sp.ang = tpoint.ang;

				ActorLeaveTrack(SpriteNum);

				if (tpoint.tag_high != 0) {
					u.jump_speed = (short) -tpoint.tag_high;
				} else {
					sp.cstat &= ~(CSTAT_SPRITE_BLOCK);

					FAFhitscan(sp.x, sp.y, sp.z - Z(24), sp.sectnum, // Start position
							sintable[NORM_ANGLE(sp.ang + 512)], // X vector of 3D ang
							sintable[sp.ang], // Y vector of 3D ang
							0, // Z vector of 3D ang
							pHitInfo, CLIPMASK_MISSILE);
					hitwall = pHitInfo.hitwall;
					hitsprite = pHitInfo.hitsprite;

					sp.cstat |= (CSTAT_SPRITE_BLOCK);

					if (hitsprite >= 0)
						return (false);

					if (hitwall < 0 || wall[hitwall].nextsector < 0)
						return (false);

					zdiff = klabs(sp.z - sector[wall[hitwall].nextsector].floorz) >> 8;

					u.jump_speed = (short) PickJumpSpeed(SpriteNum, zdiff);
				}

				DoActorBeginJump(SpriteNum);
				u.ActorActionFunc = DoActorMoveJump;

				return (false);
			}

			break;

		case TRACK_ACTOR_QUICK_JUMP_DOWN:

			if (u.ActorActionSet.Jump != null) {

				sp.ang = tpoint.ang;

				ActorLeaveTrack(SpriteNum);

				if (tpoint.tag_high != 0) {
					u.jump_speed = (short) -tpoint.tag_high;
				} else {
					u.jump_speed = -350;
				}

				DoActorBeginJump(SpriteNum);
				u.ActorActionFunc = DoActorMoveJump;
				return (false);
			}

			break;

		case TRACK_ACTOR_QUICK_SCAN:

			if (u.ActorActionSet.Jump != null) {
				ActorLeaveTrack(SpriteNum);
				return (false);
			}

			break;

		case TRACK_ACTOR_QUICK_DUCK:

			if (u.Rot != u.ActorActionSet.Duck) {
				sp.ang = tpoint.ang;

				ActorLeaveTrack(SpriteNum);

				if (tpoint.tag_high == 0)
					u.WaitTics = 4 * 120;
				else
					u.WaitTics = (short) (tpoint.tag_high * 128);

				InitActorDuck.invoke(SpriteNum);
				u.ActorActionFunc = DoActorDuck;
				return (false);
			}

			break;

		case TRACK_ACTOR_OPERATE:
		case TRACK_ACTOR_QUICK_OPERATE: {
			short nearsector = -1, nearwall = -1, nearsprite = -1;
			int nearhitdist = -1;

			int i;

			if (u.Rot == u.ActorActionSet.Sit || u.Rot == u.ActorActionSet.Stand)
				return (false);

			sp.ang = tpoint.ang;

			z[0] = sp.z - SPRITEp_SIZE_Z(sp) + Z(5);
			z[1] = sp.z - DIV2(SPRITEp_SIZE_Z(sp));

			for (i = 0; i < z.length; i++) {

				engine.neartag(sp.x, sp.y, z[i], sp.sectnum, sp.ang, neartag, 1024, NTAG_SEARCH_LO_HI);
				nearsector = neartag.tagsector;
				nearwall = neartag.tagwall;
				nearsprite = neartag.tagsprite;
				nearhitdist = neartag.taghitdist;

				if (nearsprite >= 0 && nearhitdist < 1024) {
					if (OperateSprite(nearsprite, false)) {
						if (tpoint.tag_high == 0)
							u.WaitTics = 2 * 120;
						else
							u.WaitTics = (short) (tpoint.tag_high * 128);

						NewStateGroup(SpriteNum, u.ActorActionSet.Stand);
					}
				}
			}

			if (nearsector >= 0 && nearhitdist < 1024) {
				if (OperateSector(nearsector, false)) {
					if (tpoint.tag_high == 0)
						u.WaitTics = 2 * 120;
					else
						u.WaitTics = (short) (tpoint.tag_high * 128);

					NewStateGroup(SpriteNum, u.ActorActionSet.Sit);
				}
			}

			if (nearwall >= 0 && nearhitdist < 1024) {
				if (OperateWall(nearwall, false)) {
					if (tpoint.tag_high == 0)
						u.WaitTics = 2 * 120;
					else
						u.WaitTics = (short) (tpoint.tag_high * 128);

					NewStateGroup(SpriteNum, u.ActorActionSet.Stand);
				}
			}

			break;
		}

		case TRACK_ACTOR_JUMP_IF_FORWARD:
			if (u.ActorActionSet.Jump != null && u.track_dir == 1) {
				if (tpoint.tag_high == 0)
					u.jump_speed = ACTOR_STD_JUMP;
				else
					u.jump_speed = (short) -tpoint.tag_high;

				DoActorBeginJump(SpriteNum);
			}

			break;

		case TRACK_ACTOR_JUMP_IF_REVERSE:
			if (u.ActorActionSet.Jump != null && u.track_dir == -1) {
				if (tpoint.tag_high == 0)
					u.jump_speed = ACTOR_STD_JUMP;
				else
					u.jump_speed = (short) -tpoint.tag_high;

				DoActorBeginJump(SpriteNum);
			}

			break;

		case TRACK_ACTOR_CRAWL:
			if (u.Rot != u.ActorActionSet.Crawl)
				NewStateGroup(SpriteNum, u.ActorActionSet.Crawl);
			else
				NewStateGroup(SpriteNum, u.ActorActionSet.Rise);
			break;

		case TRACK_ACTOR_SWIM:
			if (u.Rot != u.ActorActionSet.Swim)
				NewStateGroup(SpriteNum, u.ActorActionSet.Swim);
			else
				NewStateGroup(SpriteNum, u.ActorActionSet.Rise);
			break;

		case TRACK_ACTOR_FLY:
			NewStateGroup(SpriteNum, u.ActorActionSet.Fly);
			break;

		case TRACK_ACTOR_SIT:

			if (u.ActorActionSet.Sit != null) {
				if (tpoint.tag_high == 0)
					u.WaitTics = 3 * 120;
				else
					u.WaitTics = (short) (tpoint.tag_high * 128);

				NewStateGroup(SpriteNum, u.ActorActionSet.Sit);
			}

			break;

		case TRACK_ACTOR_DEATH1:
			if (u.ActorActionSet.Death2 != null) {
				u.WaitTics = 4 * 120;
				NewStateGroup(SpriteNum, u.ActorActionSet.Death1);
			}
			break;

		case TRACK_ACTOR_DEATH2:

			if (u.ActorActionSet.Death2 != null) {
				u.WaitTics = 4 * 120;
				NewStateGroup(SpriteNum, u.ActorActionSet.Death2);
			}

			break;

		case TRACK_ACTOR_DEATH_JUMP:

			if (u.ActorActionSet.DeathJump != null) {
				u.Flags |= (SPR_DEAD);
				sp.xvel <<= 1;
				u.jump_speed = -495;
				DoActorBeginJump(SpriteNum);
				NewStateGroup(SpriteNum, u.ActorActionSet.DeathJump);
			}

			break;

		case TRACK_ACTOR_CLOSE_ATTACK1:

			if (u.ActorActionSet.CloseAttack[0] != null) {
				if (tpoint.tag_high == 0)
					u.WaitTics = 2 * 120;
				else
					u.WaitTics = (short) (tpoint.tag_high * 128);

				NewStateGroup(SpriteNum, u.ActorActionSet.CloseAttack[0]);
			}

			break;

		case TRACK_ACTOR_CLOSE_ATTACK2:

			if (u.ActorActionSet.CloseAttack[1] != null) {
				if (tpoint.tag_high == 0)
					u.WaitTics = 4 * 120;
				else
					u.WaitTics = (short) (tpoint.tag_high * 128);

				NewStateGroup(SpriteNum, u.ActorActionSet.CloseAttack[1]);
			}

			break;

		case TRACK_ACTOR_ATTACK1:
		case TRACK_ACTOR_ATTACK2:
		case TRACK_ACTOR_ATTACK3:
		case TRACK_ACTOR_ATTACK4:
		case TRACK_ACTOR_ATTACK5:
		case TRACK_ACTOR_ATTACK6: {
			StateGroup ap = u.ActorActionSet.Attack[tpoint.tag_low - TRACK_ACTOR_ATTACK1];

			if (ap != null) {
				if (tpoint.tag_high == 0)
					u.WaitTics = 4 * 120;
				else
					u.WaitTics = (short) (tpoint.tag_high * 128);

				NewStateGroup(SpriteNum, ap);
			}

			break;
		}

		case TRACK_ACTOR_ZDIFF_MODE:
			if (TEST(u.Flags, SPR_ZDIFF_MODE)) {
				u.Flags &= ~(SPR_ZDIFF_MODE);
				sp.z = sector[sp.sectnum].floorz;
				sp.zvel = 0;
			} else {
				u.Flags |= (SPR_ZDIFF_MODE);
			}
			break;

		case TRACK_ACTOR_CLIMB_LADDER:

			if (u.ActorActionSet.Jump != null) {
				short hitwall;
				int bos_z, nx, ny;
			
				//
				// Get angle and x,y pos from CLIMB_MARKER
				//
				int lspi = FindNearSprite(sp, STAT_CLIMB_MARKER);

				if (lspi == -1) {
					ActorLeaveTrack(SpriteNum);
					return (false);
				}

				SPRITE lsp = sprite[lspi];

				// determine where the player is supposed to be in relation to the ladder
				// move out in front of the ladder
				nx = MOVEx(100, lsp.ang);
				ny = MOVEy(100, lsp.ang);

				sp.x = lsp.x + nx;
				sp.y = lsp.y + ny;

				sp.ang = (short) NORM_ANGLE(lsp.ang + 1024);

				//
				// Get the z height to climb
				//

				engine.neartag(sp.x, sp.y, SPRITEp_TOS(sp) - DIV2(SPRITEp_SIZE_Z(sp)), sp.sectnum, sp.ang, neartag, 600,
						NTAG_SEARCH_LO_HI);
				hitwall = neartag.tagwall;

				if (hitwall < 0) {
					ActorLeaveTrack(SpriteNum);
					return (false);
				}

				// destination z for climbing
				u.sz = sector[wall[hitwall].nextsector].floorz;

				DoActorZrange(SpriteNum);

				//
				// Adjust for YCENTERING
				//

				sp.cstat |= (CSTAT_SPRITE_YCENTER);
				bos_z = SPRITEp_BOS(sp);
				if (bos_z > u.loz) {
					u.sy = (bos_z - sp.z);
					sp.z -= u.sy;
				}

				//
				// Misc climb setup
				//

				u.Flags |= (SPR_CLIMBING);
				NewStateGroup(SpriteNum, u.ActorActionSet.Climb);

				sp.zvel = (short) -Z(1);
			}

			break;

		case TRACK_ACTOR_SET_JUMP:
			u.jump_speed = (short) -tpoint.tag_high;
			break;
		}

		return (true);
	}

	/*
	 * 
	 * !AIC - This is where actors follow tracks. Its massy, hard to read, and more
	 * complex than it needs to be. It was taken from sector object track movement
	 * code. The routine above ActorTrackDecide() is where a track tag is recognized
	 * and acted upon. There are quite a few of these that are not useful to us at
	 * present time.
	 * 
	 */

	public static boolean ActorFollowTrack(int SpriteNum, int locktics) {
		USER u = pUser[SpriteNum];
		SPRITE sp = pUser[SpriteNum].getSprite();
		PlayerStr pp;

		TRACK_POINT tpoint;
		short pnum;
		int nx = 0, ny = 0, nz = 0, dx, dy, dz;
		int dist;

		// if not on a track then better not go here
		if (u.track == -1)
			return (true);

		// if lying in wait for player
		if (TEST(u.Flags, SPR_WAIT_FOR_PLAYER | SPR_WAIT_FOR_TRIGGER)) {
			if (TEST(u.Flags, SPR_WAIT_FOR_PLAYER)) {
				for (pnum = connecthead; pnum != -1; pnum = connectpoint2[pnum]) {
					pp = Player[pnum];

					if (Distance(sp.x, sp.y, pp.posx, pp.posy) < u.Dist) {
						u.tgt_sp = pp.PlayerSprite;
						u.Flags &= ~(SPR_WAIT_FOR_PLAYER);
						return (true);
					}
				}
			}

			u.Tics = 0;
			return (true);
		}

		// if pausing the return
		if (u.WaitTics != 0) {
			u.WaitTics -= locktics;
			if (u.WaitTics <= 0) {
				u.Flags &= ~(SPR_DONT_UPDATE_ANG);
				NewStateGroup(SpriteNum, u.ActorActionSet.Run);
				u.WaitTics = 0;
			}

			return (true);
		}

		tpoint = Track[u.track].TrackPoint[u.point];
		if(tpoint == null)
			return false;

		if (!(TEST(u.Flags, SPR_CLIMBING | SPR_DONT_UPDATE_ANG))) {
			sp.ang = engine.getangle(tpoint.x - sp.x, tpoint.y - sp.y);
		}

		if ((dist = Distance(sp.x, sp.y, tpoint.x, tpoint.y)) < 200) // 64
		{
			if (!ActorTrackDecide(tpoint, SpriteNum))
				return (true);

			// get the next point
			NextActorTrackPoint(SpriteNum);
			tpoint = Track[u.track].TrackPoint[u.point];

			if (!(TEST(u.Flags, SPR_CLIMBING | SPR_DONT_UPDATE_ANG))) {
				// calculate a new angle to the target
				sp.ang = engine.getangle(tpoint.x - sp.x, tpoint.y - sp.y);
			}

			if (TEST(u.Flags, SPR_ZDIFF_MODE)) {

				// set dx,dy,dz up for finding the z magnitude
				dx = tpoint.x;
				dy = tpoint.y;
				dz = tpoint.z;

				// find the distance to the target (player)
				dist = DIST(dx, dy, sp.x, sp.y);

				// (velocity * difference between the target and the object) /
				// distance
				sp.zvel = (short) -((sp.xvel * (sp.z - dz)) / dist);
			}
		} else {
			// make velocity approach the target velocity
			if (TEST(u.Flags, SPR_SPEED_UP)) {
				if ((u.track_vel += (locktics << u.vel_rate)) >= u.vel_tgt) {
					u.track_vel = u.vel_tgt;
					u.Flags &= ~(SPR_SPEED_UP);
				}

				// update the real velocity
				sp.xvel = (short) DIV256(u.track_vel);
			} else if (TEST(u.Flags, SPR_SLOW_DOWN)) {
				if ((u.track_vel -= (locktics << u.vel_rate)) <= u.vel_tgt) {
					u.track_vel = u.vel_tgt;
					u.Flags &= ~(SOBJ_SLOW_DOWN);
				}

				sp.xvel = (short) DIV256(u.track_vel);
			}

			nx = 0;
			ny = 0;

			if (TEST(u.Flags, SPR_CLIMBING)) {
				if (SPRITEp_TOS(sp) + DIV4(SPRITEp_SIZE_Z(sp)) < u.sz) {
					u.Flags &= ~(SPR_CLIMBING);

					sp.zvel = 0;

					sp.ang = engine.getangle(tpoint.x - sp.x, tpoint.y - sp.y);

					ActorLeaveTrack(SpriteNum);
					sp.cstat &= ~(CSTAT_SPRITE_YCENTER);
					sp.z += u.sy;

					DoActorSetSpeed(SpriteNum, SLOW_SPEED);
					u.ActorActionFunc = NinjaJumpActionFunc;
					u.jump_speed = -650;
					DoActorBeginJump(SpriteNum);

					return (true);
				}
			} else {
				// calculate a new x and y
				nx = sp.xvel * sintable[NORM_ANGLE(sp.ang + 512)] >> 14;
				ny = sp.xvel * sintable[sp.ang] >> 14;
			}

			nz = 0;

			if (sp.zvel != 0)
				nz = sp.zvel * locktics;
		}

		u.ret = move_sprite(SpriteNum, nx, ny, nz, u.ceiling_dist, u.floor_dist, 0, locktics);

		if (u.ret != 0) {
			if (!TEST(u.Flags, SPR_JUMPING | SPR_FALLING))
				ActorLeaveTrack(SpriteNum);
		}

		return (true);
	}
}
