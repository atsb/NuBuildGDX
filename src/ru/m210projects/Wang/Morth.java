package ru.m210projects.Wang;

import static ru.m210projects.Build.Engine.sector;
import static ru.m210projects.Build.Engine.sintable;
import static ru.m210projects.Build.Engine.sprite;
import static ru.m210projects.Wang.Game.Distance;
import static ru.m210projects.Wang.Gameutils.MAX_SECTOR_OBJECTS;
import static ru.m210projects.Wang.Gameutils.NORM_ANGLE;
import static ru.m210projects.Wang.Gameutils.RANDOM_P2;
import static ru.m210projects.Wang.Gameutils.RANDOM_RANGE;
import static ru.m210projects.Wang.Gameutils.RESET_BOOL3;
import static ru.m210projects.Wang.Gameutils.SECTFU_SO_SLOPE_CEILING_TO_POINT;
import static ru.m210projects.Wang.Gameutils.SET_BOOL3;
import static ru.m210projects.Wang.Gameutils.SOBJ_DYNAMIC;
import static ru.m210projects.Wang.Gameutils.SOBJ_WAIT_FOR_EVENT;
import static ru.m210projects.Wang.Gameutils.SP_TAG3;
import static ru.m210projects.Wang.Gameutils.SP_TAG7;
import static ru.m210projects.Wang.Gameutils.SQ;
import static ru.m210projects.Wang.Gameutils.TEST_BOOL1;
import static ru.m210projects.Wang.Gameutils.TEST_BOOL2;
import static ru.m210projects.Wang.Gameutils.TEST_BOOL3;
import static ru.m210projects.Wang.Gameutils.Z;
import static ru.m210projects.Wang.Main.engine;
import static ru.m210projects.Wang.Main.game;
import static ru.m210projects.Wang.Sector.SO_SCALE_CYCLE;
import static ru.m210projects.Wang.Sector.SO_SCALE_DEST;
import static ru.m210projects.Wang.Sector.SO_SCALE_HOLD;
import static ru.m210projects.Wang.Sector.SO_SCALE_NONE;
import static ru.m210projects.Wang.Sector.SO_SCALE_RANDOM;
import static ru.m210projects.Wang.Sector.SO_SCALE_RANDOM_POINT;
import static ru.m210projects.Wang.Sector.SectUser;
import static ru.m210projects.Wang.Sprites.SectorObject;
import static ru.m210projects.Wang.Type.MyTypes.MAXLONG;
import static ru.m210projects.Wang.Type.MyTypes.OFF;
import static ru.m210projects.Wang.Type.MyTypes.ON;
import static ru.m210projects.Wang.Type.MyTypes.TEST;

import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Wang.Type.Sector_Object;
import ru.m210projects.Wang.Type.Sector_Object.SOAnimator;
import ru.m210projects.Wang.Type.Vector2i;

public class Morth {

	public static short DoSectorObjectSetScale(int match) {
		Sector_Object sop;

		for (int s = 0; s < MAX_SECTOR_OBJECTS; s++) {
			sop = SectorObject[s];
			if (sop.xmid == MAXLONG)
				continue;

			if (sop.match_event == match) {
				sop.flags |= (SOBJ_DYNAMIC);
				sop.PreMoveAnimator = SOAnimator.ScaleSectorObject;

				switch (sop.scale_active_type) {
				case SO_SCALE_RANDOM_POINT:
					if (sop.scale_type == SO_SCALE_HOLD || sop.scale_type == SO_SCALE_NONE) {
						// if holding start it up
						sop.scale_type = sop.scale_active_type;
					} else {
						// if moving set to hold
						sop.scale_type = SO_SCALE_HOLD;
					}
					break;

				case SO_SCALE_DEST:

					sop.scale_type = sop.scale_active_type;

					if (sop.scale_dist == sop.scale_dist_max) {
						// make it negative
						if (sop.scale_speed > 0)
							sop.scale_speed = (short) -sop.scale_speed;
					} else if (sop.scale_dist == sop.scale_dist_min) {
						// make it positive
						if (sop.scale_speed < 0)
							sop.scale_speed = (short) -sop.scale_speed;
					} else {
						// make it positive
						if (sop.scale_speed < 0)
							sop.scale_speed = (short) -sop.scale_speed;
					}
					break;

				case SO_SCALE_RANDOM:
				case SO_SCALE_CYCLE:
					if (sop.scale_type == SO_SCALE_HOLD) {
						// if holding start it up
						sop.scale_type = sop.scale_active_type;
					} else {
						// if moving set to hold
						sop.scale_type = SO_SCALE_HOLD;
					}
					break;
				}
			}
		}
		return (0);
	}

	public static short DoSOevent(int match, int state) {
		Sector_Object sop;
		SPRITE me_sp;
		short vel_adj = 0, spin_adj = 0;

		for (int s = 0; s < MAX_SECTOR_OBJECTS; s++) {
			sop = SectorObject[s];

			if (sop.xmid == MAXLONG)
				continue;

			if (sop.match_event == match) {
				if (TEST(sop.flags, SOBJ_WAIT_FOR_EVENT)) {
					if (sop.save_vel > 0 || sop.save_spin_speed > 0) {
						sop.flags &= ~(SOBJ_WAIT_FOR_EVENT);
						sop.vel = sop.save_vel;
						sop.spin_speed = sop.save_spin_speed;
					}
				}

				if (sop.match_event_sprite == -1)
					continue;

				me_sp = sprite[sop.match_event_sprite];

				// toggle
				if (state == -1) {
					if (TEST_BOOL3(me_sp)) {
						RESET_BOOL3(me_sp);
						state = OFF;
					} else {
						SET_BOOL3(me_sp);
						state = ON;
					}
				}

				if (state == ON) {
					spin_adj = (short) SP_TAG3(me_sp);
					vel_adj = (short) SP_TAG7(me_sp);
				} else if (state == OFF) {
					spin_adj = (short) -SP_TAG3(me_sp);
					vel_adj = (short) -SP_TAG7(me_sp);
				}

				sop.spin_speed += spin_adj;

				if (TEST_BOOL1(me_sp))
					sop.vel_tgt += vel_adj;
				else
					sop.vel += vel_adj;

				if (TEST_BOOL2(me_sp)) {
					sop.dir *= -1;
				}
			}
		}
		return (0);
	}

	public static void ScaleSectorObject(int sopi) {
		Sector_Object sop = SectorObject[sopi];
		switch (sop.scale_type) {
		case SO_SCALE_NONE:
			break;

		case SO_SCALE_HOLD:
			break;

		// to dest
		case SO_SCALE_DEST:
			sop.scale_dist += sop.scale_speed;

			if (sop.scale_dist > sop.scale_dist_max) {
				sop.scale_dist = sop.scale_dist_max;
				sop.scale_type = SO_SCALE_HOLD;
			} else if (sop.scale_dist < sop.scale_dist_min) {
				sop.scale_dist = sop.scale_dist_min;
				sop.scale_type = SO_SCALE_HOLD;
			}

			break;

		// random direction change
		case SO_SCALE_RANDOM:

			sop.scale_dist += sop.scale_speed;
			if (sop.scale_dist > sop.scale_dist_max) {
				sop.scale_speed *= -1;
				sop.scale_dist = sop.scale_dist_max;
			} else if (sop.scale_dist < sop.scale_dist_min) {
				sop.scale_speed *= -1;
				sop.scale_dist = sop.scale_dist_min;
			}

			if (RANDOM_P2(1024) < sop.scale_rand_freq << 3) {
				sop.scale_speed *= -1;
			}

			break;

		// cycle through max and min
		case SO_SCALE_CYCLE:
			sop.scale_dist += sop.scale_speed;

			if (sop.scale_dist > sop.scale_dist_max) {
				sop.scale_speed *= -1;
				sop.scale_dist = sop.scale_dist_max;
			} else if (sop.scale_dist < sop.scale_dist_min) {
				sop.scale_speed *= -1;
				sop.scale_dist = sop.scale_dist_min;
			}
			break;
		}
	}

	private static Vector2i vScaleRandomPoint = new Vector2i();

	public static Vector2i ScaleRandomPoint(Sector_Object sop, short k, short ang, int x, int y) {
		int xmul, ymul;

		sop.scale_point_dist[k] += sop.scale_point_speed[k];
		if (sop.scale_point_dist[k] > sop.scale_point_dist_max) {
			sop.scale_point_speed[k] *= -1;
			sop.scale_point_dist[k] = sop.scale_point_dist_max;
		} else if (sop.scale_point_dist[k] < sop.scale_point_dist_min) {
			sop.scale_point_speed[k] *= -1;
			sop.scale_point_dist[k] = sop.scale_point_dist_min;
		}

		if (RANDOM_P2(1024) < sop.scale_point_rand_freq) {
			sop.scale_point_speed[k] *= -1;
		}

		// change up speed at random
		if (RANDOM_P2(1024) < (sop.scale_point_rand_freq / 2)) {
			short half = (short) (sop.scale_point_base_speed / 2);
			short quart = (short) (sop.scale_point_base_speed / 4);
			sop.scale_point_speed[k] = (short) (sop.scale_point_base_speed + (RANDOM_RANGE(half) - quart));
		}

		xmul = (sop.scale_point_dist[k] * sop.scale_x_mult) >> 8;
		ymul = (sop.scale_point_dist[k] * sop.scale_y_mult) >> 8;

		vScaleRandomPoint.x = x + ((xmul * sintable[NORM_ANGLE(ang + 512)]) >> 14);
		vScaleRandomPoint.y = y + ((ymul * sintable[ang]) >> 14);

		return vScaleRandomPoint;
	}

	public static void MorphTornado(int sopi) {
		int mx, my;
		int ceilingz;
		int floorz;
		int j;
		int x, y, sx, sy;
		
		Sector_Object sop = SectorObject[sopi];

		// z direction
		sop.morph_z += Z(sop.morph_z_speed);

		// move vector
		if (sop.morph_wall_point < 0)
			return;

		// place at correct x,y offset from center
		x = sop.xmid - sop.morph_xoff;
		y = sop.ymid - sop.morph_yoff;
	
		sx = x;
		sy = y;

		// move it from last x,y
		mx = x + (((sop.morph_speed) * sintable[NORM_ANGLE(sop.morph_ang + 512)]) >> 14);
		my = y + (((sop.morph_speed) * sintable[sop.morph_ang]) >> 14);
	
		// bound check radius
		if (engine.ksqrt(SQ(sop.xmid - mx) + SQ(sop.ymid - my)) > sop.morph_dist_max + sop.scale_dist) {
			// find angle
			sop.morph_ang = (short) NORM_ANGLE(engine.getangle(mx - sop.xmid, my - sop.ymid));
			// reverse angle
			sop.morph_ang = (short) NORM_ANGLE(sop.morph_ang + 1024);

			// move back some from last point
			mx = sx + (((sop.morph_speed * 2) * sintable[NORM_ANGLE(sop.morph_ang + 512)]) >> 14);
			my = sy + (((sop.morph_speed * 2) * sintable[sop.morph_ang]) >> 14);

			sop.morph_xoff = (short) (sop.xmid - mx);
			sop.morph_yoff = (short) (sop.ymid - my);
		}

		// save x,y back as offset info
		sop.morph_xoff = (short) (sop.xmid - mx);
		sop.morph_yoff = (short) (sop.ymid - my);

		if ((RANDOM_P2(1024 << 4) >> 4) < sop.morph_rand_freq)
			sop.morph_ang = (short) RANDOM_P2(2048);

		// move it x,y
		engine.dragpoint(sop.morph_wall_point, mx, my);
		
		// bound the Z
		ceilingz = sector[sop.op_main_sector].ceilingz;
		floorz = sector[sop.op_main_sector].floorz;
	
		for (j = 0; sop.sector[j] != -1; j++) {
			if (SectUser[sop.sector[j]] != null
					&& TEST(SectUser[sop.sector[j]].flags, SECTFU_SO_SLOPE_CEILING_TO_POINT)) {
				if (sop.morph_z > floorz) {
					sop.morph_z_speed *= -1;
					sop.morph_z = floorz;
				} else if (sop.morph_z < ceilingz) {
					sop.morph_z_speed *= -1;
					sop.morph_z = ceilingz;
				}

				game.pInt.setcheinuminterpolate(sop.sector[j], sector[sop.sector[j]]);
				engine.alignceilslope(sop.sector[j], mx, my, sop.morph_z);
			}
		}
	}

	public static final int MORPH_FLOOR_ZRANGE = Z(300);

	// moves center point around and aligns slope
	public static void MorphFloor(int sopi) {
		int mx, my;
		int floorz;
		int j;
		int x, y;
		
		Sector_Object sop = SectorObject[sopi];

		// z direction
		sop.morph_z -= Z(sop.morph_z_speed);

		// move vector
		if (sop.morph_wall_point < 0)
			return;

		// place at correct x,y offset from center
		x = sop.xmid - sop.morph_xoff;
		y = sop.ymid - sop.morph_yoff;

		// move it from last x,y
		mx = x + (((sop.morph_speed) * sintable[NORM_ANGLE(sop.morph_ang + 512)]) >> 14);
		my = y + (((sop.morph_speed) * sintable[sop.morph_ang]) >> 14);

		// save x,y back as offset info
		sop.morph_xoff = (short) (sop.xmid - mx);
		sop.morph_yoff = (short) (sop.ymid - my);

		// bound check radius
		if (Distance(sop.xmid, sop.ymid, mx, my) > sop.morph_dist_max) {
			// go in the other direction
			// sop.morph_speed *= -1;
			sop.morph_ang = (short) NORM_ANGLE(sop.morph_ang + 1024);

			// back it up and save it off
			mx = x + (((sop.morph_speed) * sintable[NORM_ANGLE(sop.morph_ang + 512)]) >> 14);
			my = y + (((sop.morph_speed) * sintable[sop.morph_ang]) >> 14);
			sop.morph_xoff = (short) (sop.xmid - mx);
			sop.morph_yoff = (short) (sop.ymid - my);

			// turn it all the way around and then do a random -512 to 512 from there
			// sop.morph_ang = NORM_ANGLE(sop.morph_ang + 1024 + (RANDOM_P2(1024) - 512));
		}

		if ((RANDOM_P2(1024 << 4) >> 4) < sop.morph_rand_freq)
			sop.morph_ang = (short) RANDOM_P2(2048);

		// move x,y point "just like in build"
		engine.dragpoint(sop.morph_wall_point, mx, my);

		// bound the Z
		floorz = sector[sop.op_main_sector].floorz;

		if (sop.morph_z > MORPH_FLOOR_ZRANGE) {
			sop.morph_z = MORPH_FLOOR_ZRANGE;
			// sop.morph_ang = NORM_ANGLE(sop.morph_ang + 1024);
			sop.morph_z_speed *= -1;
		} else if (sop.morph_z < -MORPH_FLOOR_ZRANGE) {
			sop.morph_z = -MORPH_FLOOR_ZRANGE;
			// sop.morph_ang = NORM_ANGLE(sop.morph_ang + 1024);
			sop.morph_z_speed *= -1;
		}

		for (j = 0; sop.sector[j] != -1; j++) {
			if (SectUser[sop.sector[j]] != null
					&& TEST(SectUser[sop.sector[j]].flags, SECTFU_SO_SLOPE_CEILING_TO_POINT)) {
				game.pInt.setfheinuminterpolate(sop.sector[j], sector[sop.sector[j]]);
				engine.alignflorslope(sop.sector[j], mx, my, floorz + sop.morph_z);
			}
		}
	}

	public static void SOBJ_AlignFloorToPoint(Sector_Object sop, int x, int y, int z) {
		for (int j = 0; sop.sector[j] != -1; j++) {
			if (SectUser[sop.sector[j]] != null
					&& TEST(SectUser[sop.sector[j]].flags, SECTFU_SO_SLOPE_CEILING_TO_POINT)) {
				game.pInt.setfheinuminterpolate(sop.sector[j], sector[sop.sector[j]]);
				engine.alignflorslope(sop.sector[j], x, y, z);
			}
		}
	}

	public static void SOBJ_AlignCeilingToPoint(Sector_Object sop, int x, int y, int z) {
		for (int j = 0; sop.sector[j] != -1; j++) {
			if (SectUser[sop.sector[j]] != null
					&& TEST(SectUser[sop.sector[j]].flags, SECTFU_SO_SLOPE_CEILING_TO_POINT)) {
				game.pInt.setcheinuminterpolate(sop.sector[j], sector[sop.sector[j]]);
				engine.alignceilslope(sop.sector[j], x, y, z);
			}
		}
	}

	public static void SOBJ_AlignFloorCeilingToPoint(Sector_Object sop, int x, int y, int z) {
		for (int j = 0; sop.sector[j] != -1; j++) {
			if (SectUser[sop.sector[j]] != null
					&& TEST(SectUser[sop.sector[j]].flags, SECTFU_SO_SLOPE_CEILING_TO_POINT)) {
				game.pInt.setfheinuminterpolate(sop.sector[j], sector[sop.sector[j]]);
				game.pInt.setcheinuminterpolate(sop.sector[j], sector[sop.sector[j]]);
				engine.alignflorslope(sop.sector[j], x, y, z);
				engine.alignceilslope(sop.sector[j], x, y, z);
			}
		}
	}

	// moves center point around and aligns slope
	public static void SpikeFloor(Sector_Object sop) {
		int mx, my;
		int floorz;
		int x, y;

		// z direction
		sop.morph_z -= Z(sop.morph_z_speed);

		// move vector
		if (sop.morph_wall_point < 0)
			return;

		// place at correct x,y offset from center
		x = sop.xmid - sop.morph_xoff;
		y = sop.ymid - sop.morph_yoff;

		// move it from last x,y
		mx = x;
		my = y;

		// bound the Z
		floorz = sector[sop.op_main_sector].floorz;

		if (sop.morph_z > MORPH_FLOOR_ZRANGE) {
			sop.morph_z = MORPH_FLOOR_ZRANGE;
			sop.morph_z_speed *= -1;
		} else if (sop.morph_z < -MORPH_FLOOR_ZRANGE) {
			sop.morph_z = -MORPH_FLOOR_ZRANGE;
			sop.morph_z_speed *= -1;
		}

		SOBJ_AlignFloorToPoint(sop, mx, my, floorz + sop.morph_z);
	}

}
