package ru.m210projects.Wang.Type;

import static ru.m210projects.Wang.Gameutils.MAX_CLIPBOX;
import static ru.m210projects.Wang.Gameutils.MAX_SO_POINTS;
import static ru.m210projects.Wang.Gameutils.MAX_SO_SECTOR;
import static ru.m210projects.Wang.Gameutils.MAX_SO_SPRITE;
import static ru.m210projects.Wang.Morth.MorphFloor;
import static ru.m210projects.Wang.Morth.MorphTornado;
import static ru.m210projects.Wang.Morth.ScaleSectorObject;
import static ru.m210projects.Wang.Track.DoAutoTurretObject;
import static ru.m210projects.Wang.Track.DoTornadoObject;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

import ru.m210projects.Build.FileHandle.Resource;

public class Sector_Object {
	
	public final static int sizeof = 5866;
	
	public enum SOAnimator {
		DoTornadoObject {
			@Override
			public void invoke(int sop) {
				DoTornadoObject(sop);
			}
		},

		// SCALING - PreAnimator
		ScaleSectorObject {
			@Override
			public void invoke(int sop) {
				ScaleSectorObject(sop);
			}
		},

		// Morph point - move point around
		MorphTornado {
			@Override
			public void invoke(int sop) {
				MorphTornado(sop);
			}
		},
		
		MorphFloor {
			@Override
			public void invoke(int sop) {
				MorphFloor(sop);
			}
		},
		
		DoAutoTurretObject {
			@Override
			public void invoke(int sopi) {
				DoAutoTurretObject(sopi);
			}
		};
		
		public abstract void invoke(int sop);
	}

	public SOAnimator PreMoveAnimator;
	public SOAnimator PostMoveAnimator;
	public SOAnimator Animator;
	public int controller = -1;

	public int sp_child; // child sprite that holds info for the sector object

	public int xmid, ymid, zmid, // midpoints of the sector object
			vel, // velocity
			vel_tgt, // target velocity
			player_xoff, // player x offset from the xmid
			player_yoff, // player y offset from the ymid
			zorig_floor[] = new int[MAX_SO_SECTOR], // original z values for all sectors
			zorig_ceiling[] = new int[MAX_SO_SECTOR], // original z values for all sectors
			zdelta, // z delta from original
			z_tgt, // target z delta
			z_rate, // rate at which z aproaches target
			update, // Distance from player at which you continue updating
			// only works for single player.
			bob_diff, // bobbing difference for the frame
			target_dist, // distance to next point
			floor_loz, // floor low z
			floor_hiz, // floor hi z
			morph_z, // morphing point z
			morph_z_min, // morphing point z min
			morph_z_max, bob_amt, // bob amount max in z coord
			// variables set by mappers for drivables
			drive_angspeed, drive_angslide, drive_speed, drive_slide, crush_z, flags;

	public short sector[] = new short[MAX_SO_SECTOR], // hold the sector numbers of the sector object
			sp_num[] = new short[MAX_SO_SPRITE], // hold the sprite numbers of the object
			xorig[] = new short[MAX_SO_POINTS], // save the original x & y location of each wall so it can be
			yorig[] = new short[MAX_SO_POINTS], // refreshed
			sectnum, // current secnum of midpoint
			mid_sector, // middle sector
			max_damage, // max damage
			ram_damage, // damage taken by ramming
			wait_tics, //
			num_sectors, // number of sectors
			num_walls, // number of sectors
			track, // the track # 0 to 20
			point, // the point on the track that the sector object is headed toward
			vel_rate, // rate at which velocity aproaches target
			dir, // direction traveling on the track
			ang, // angle facing
			ang_moving, // angle the SO is facing
			clipdist, // cliping distance for operational sector objects
			clipbox_dist[] = new short[MAX_CLIPBOX], // mult-clip box variables
			clipbox_xoff[] = new short[MAX_CLIPBOX], // mult-clip box variables
			clipbox_yoff[] = new short[MAX_CLIPBOX], // mult-clip box variables
			clipbox_ang[] = new short[MAX_CLIPBOX], // mult-clip box variables
			clipbox_vdist[] = new short[MAX_CLIPBOX], // mult-clip box variables
			clipbox_num, ang_tgt, // target angle
			ang_orig, // original angle
			last_ang, // last angle before started spinning
			old_ang, // holding variable for the old angle
			spin_speed, // spin_speed
			spin_ang, // spin angle
			turn_speed, // shift value determines how fast SO turns to match new angle
			bob_sine_ndx, // index into sine table
			bob_speed, // shift value for speed
			op_main_sector, // main sector operational SO moves in - for speed purposes
			save_vel, // save velocity
			save_spin_speed, // save spin speed
			match_event, // match number
			match_event_sprite, // spritenum of the match event sprite
			// SO Scaling Vector Info
			scale_type, // type of scaling - enum controled
			scale_active_type, // activated by a switch or trigger

			// values for whole SO
			scale_dist, // distance from center
			scale_speed, // speed of scaling
			scale_dist_min, // absolute min
			scale_dist_max, // absolute max
			scale_rand_freq, // freqency of direction change - based on rand(1024)

			// values for single point scaling
			scale_point_dist[] = new short[MAX_SO_POINTS], // distance from center
			scale_point_speed[] = new short[MAX_SO_POINTS], // speed of scaling
			scale_point_base_speed, // base speed of scaling
			scale_point_dist_min, // absolute min
			scale_point_dist_max, // absolute max
			scale_point_rand_freq, // freqency of direction change - based on rand(1024)

			scale_x_mult, // x multiplyer for scaling
			scale_y_mult, // y multiplyer for scaling

			// Used for center point movement
			morph_wall_point, // actual wall point to drag
			morph_ang, // angle moving from CENTER
			morph_speed, // speed of movement
			morph_dist_max, // radius boundry
			morph_rand_freq, // freq of dir change
			morph_dist, // dist from CENTER
			morph_z_speed, // z speed for morph point
			morph_xoff, // save xoff from center
			morph_yoff, // save yoff from center

			// scale_rand_reverse, // random at random interval
			// limit rotation angle
			limit_ang_center, // for limiting the angle of turning - turrets etc
			limit_ang_delta; //

	public Sector_Object() {
		Arrays.fill(sector, (short) -1);
	}

	public void reset() {
		Arrays.fill(sector, (short) -1);
		PreMoveAnimator = null;
		PostMoveAnimator = null;
		Animator = null;
		controller = -1;
		sp_child = -1;

		xmid = -1;
		ymid = -1;
		zmid = -1;
		vel = -1; // velocity
		vel_tgt = -1;
		player_xoff = -1; // player x offset from the xmid
		player_yoff = -1; // player y offset from the ymid
		Arrays.fill(zorig_floor, (short) -1);
		Arrays.fill(zorig_ceiling, (short) -1);

		zdelta = -1; // z delta from original
		z_tgt = -1; // target z delta
		z_rate = -1; // rate at which z aproaches target
		update = -1; // Distance from player at which you continue updating
		// only works for single player.
		bob_diff = -1; // bobbing difference for the frame
		target_dist = -1; // distance to next point
		floor_loz = -1; // floor low z
		floor_hiz = -1; // floor hi z
		morph_z = -1; // morphing point z
		morph_z_min = -1; // morphing point z min
		morph_z_max = -1;
		bob_amt = -1; // bob amount max in z coord
		// variables set by mappers for drivables
		drive_angspeed = -1;
		drive_angslide = -1;
		drive_speed = -1;
		drive_slide = -1;
		crush_z = -1;
		flags = -1;
		Arrays.fill(sector, (short) -1);

		Arrays.fill(sp_num, (short) -1);
		Arrays.fill(xorig, (short) -1);
		Arrays.fill(yorig, (short) -1);

		sectnum = -1; // current secnum of midpoint
		mid_sector = -1; // middle sector
		max_damage = -1; // max damage
		ram_damage = -1; // damage taken by ramming
		wait_tics = -1; //
		num_sectors = -1; // number of sectors
		num_walls = -1; // number of sectors
		track = -1; // the track # 0 to 20
		point = -1; // the point on the track that the sector object is headed toward
		vel_rate = -1; // rate at which velocity aproaches target
		dir = -1; // direction traveling on the track
		ang = -1; // angle facing
		ang_moving = -1; // angle the SO is facing
		clipdist = -1; // cliping distance for operational sector objects

		Arrays.fill(clipbox_dist, (short) -1);
		Arrays.fill(clipbox_xoff, (short) -1);
		Arrays.fill(clipbox_yoff, (short) -1);

		Arrays.fill(clipbox_ang, (short) -1);
		Arrays.fill(clipbox_vdist, (short) -1);

		clipbox_num = -1;
		ang_tgt = -1; // target angle
		ang_orig = -1; // original angle
		last_ang = -1; // last angle before started spinning
		old_ang = -1; // holding variable for the old angle
		spin_speed = -1; // spin_speed
		spin_ang = -1; // spin angle
		turn_speed = -1; // shift value determines how fast SO turns to match new angle
		bob_sine_ndx = -1; // index into sine table
		bob_speed = -1; // shift value for speed
		op_main_sector = -1; // main sector operational SO moves in - for speed purposes
		save_vel = -1; // save velocity
		save_spin_speed = -1; // save spin speed
		match_event = -1; // match number
		match_event_sprite = -1; // spritenum of the match event sprite
		// SO Scaling Vector Info
		scale_type = -1; // type of scaling - enum controled
		scale_active_type = -1; // activated by a switch or trigger

		// values for whole SO
		scale_dist = -1; // distance from center
		scale_speed = -1; // speed of scaling
		scale_dist_min = -1; // absolute min
		scale_dist_max = -1; // absolute max
		scale_rand_freq = -1; // freqency of direction change - based on rand(1024)

		// values for single point scaling
		Arrays.fill(scale_point_dist, (short) -1);
		Arrays.fill(scale_point_speed, (short) -1);
		scale_point_base_speed = -1; // base speed of scaling
		scale_point_dist_min = -1; // absolute min
		scale_point_dist_max = -1; // absolute max
		scale_point_rand_freq = -1; // freqency of direction change - based on rand(1024)

		scale_x_mult = -1; // x multiplyer for scaling
		scale_y_mult = -1; // y multiplyer for scaling

		// Used for center point movement
		morph_wall_point = -1; // actual wall point to drag
		morph_ang = -1; // angle moving from CENTER
		morph_speed = -1; // speed of movement
		morph_dist_max = -1; // radius boundry
		morph_rand_freq = -1; // freq of dir change
		morph_dist = -1; // dist from CENTER
		morph_z_speed = -1; // z speed for morph point
		morph_xoff = -1; // save xoff from center
		morph_yoff = -1; // save yoff from center

		// scale_rand_reverse = -1; // random at random interval
		// limit rotation angle
		limit_ang_center = -1; // for limiting the angle of turning - turrets etc
		limit_ang_delta = -1; //
	}
	
	private static final ByteBuffer buffer = ByteBuffer.allocate(sizeof).order(ByteOrder.LITTLE_ENDIAN);

	public byte[] getBytes() {
		buffer.clear();

		if(PreMoveAnimator != null)
			buffer.putInt(PreMoveAnimator.ordinal());
		else buffer.putInt(-1);
		
		if(PostMoveAnimator != null)
			buffer.putInt(PostMoveAnimator.ordinal());
		else buffer.putInt(-1);
		
		if(Animator != null)
			buffer.putInt(Animator.ordinal());
		else buffer.putInt(-1);

		buffer.putInt(controller);
		buffer.putInt(sp_child);

		buffer.putInt(xmid);
		buffer.putInt(ymid);
		buffer.putInt(zmid);
		buffer.putInt(vel); // velocity
		buffer.putInt(vel_tgt);
		buffer.putInt(player_xoff); // player x offset from the xmid
		buffer.putInt(player_yoff); // player y offset from the ymid
		
		for(int i = 0; i < MAX_SO_SECTOR; i++) {
			buffer.putInt(zorig_floor[i]);
			buffer.putInt(zorig_ceiling[i]);
		}

		buffer.putInt(zdelta); // z delta from original
		buffer.putInt(z_tgt); // target z delta
		buffer.putInt(z_rate); // rate at which z aproaches target
		buffer.putInt(update); // Distance from player at which you continue updating
		// only works for single player.
		buffer.putInt(bob_diff); // bobbing difference for the frame
		buffer.putInt(target_dist); // distance to next point
		buffer.putInt(floor_loz); // floor low z
		buffer.putInt(floor_hiz); // floor hi z
		buffer.putInt(morph_z); // morphing point z
		buffer.putInt(morph_z_min); // morphing point z min
		buffer.putInt(morph_z_max);
		buffer.putInt(bob_amt); // bob amount max in z coord
		// variables set by mappers for drivables
		buffer.putInt(drive_angspeed);
		buffer.putInt(drive_angslide);
		buffer.putInt(drive_speed);
		buffer.putInt(drive_slide);
		buffer.putInt(crush_z);
		buffer.putInt(flags);
		
		for(int i = 0; i < MAX_SO_SECTOR; i++) 
			buffer.putShort(sector[i]);
		for(int i = 0; i < MAX_SO_SPRITE; i++) 
			buffer.putShort(sp_num[i]);
		for(int i = 0; i < MAX_SO_POINTS; i++) {
			buffer.putShort(xorig[i]);
			buffer.putShort(yorig[i]);
		}

		buffer.putShort(sectnum); // current secnum of midpoint
		buffer.putShort(mid_sector); // middle sector
		buffer.putShort(max_damage); // max damage
		buffer.putShort(ram_damage); // damage taken by ramming
		buffer.putShort(wait_tics); //
		buffer.putShort(num_sectors); // number of sectors
		buffer.putShort(num_walls); // number of sectors
		buffer.putShort(track); // the track # 0 to 20
		buffer.putShort(point); // the point on the track that the sector object is headed toward
		buffer.putShort(vel_rate); // rate at which velocity aproaches target
		buffer.putShort(dir); // direction traveling on the track
		buffer.putShort(ang); // angle facing
		buffer.putShort(ang_moving); // angle the SO is facing
		buffer.putShort(clipdist); // cliping distance for operational sector objects

		for(int i = 0; i < MAX_CLIPBOX; i++) {
			buffer.putShort(clipbox_dist[i]);
			buffer.putShort(clipbox_xoff[i]);
			buffer.putShort(clipbox_yoff[i]);
			buffer.putShort(clipbox_ang[i]);
			buffer.putShort(clipbox_vdist[i]);
		}

		buffer.putShort(clipbox_num);
		buffer.putShort(ang_tgt); // target angle
		buffer.putShort(ang_orig); // original angle
		buffer.putShort(last_ang); // last angle before started spinning
		buffer.putShort(old_ang); // holding variable for the old angle
		buffer.putShort(spin_speed); // spin_speed
		buffer.putShort(spin_ang); // spin angle
		buffer.putShort(turn_speed); // shift value determines how fast SO turns to match new angle
		buffer.putShort(bob_sine_ndx); // index into sine table
		buffer.putShort(bob_speed); // shift value for speed
		buffer.putShort(op_main_sector); // main sector operational SO moves in - for speed purposes
		buffer.putShort(save_vel); // save velocity
		buffer.putShort(save_spin_speed); // save spin speed
		buffer.putShort(match_event); // match number
		buffer.putShort(match_event_sprite); // spritenum of the match event sprite
		// SO Scaling Vector Info
		buffer.putShort(scale_type); // type of scaling - enum controled
		buffer.putShort(scale_active_type); // activated by a switch or trigger

		// values for whole SO
		buffer.putShort(scale_dist); // distance from center
		buffer.putShort(scale_speed); // speed of scaling
		buffer.putShort(scale_dist_min); // absolute min
		buffer.putShort(scale_dist_max); // absolute max
		buffer.putShort(scale_rand_freq); // freqency of direction change - based on rand(1024)

		// values for single point scaling
		for(int i = 0; i < MAX_SO_POINTS; i++) {
			buffer.putShort(scale_point_dist[i]);
			buffer.putShort(scale_point_speed[i]);
		}

		buffer.putShort(scale_point_base_speed); // base speed of scaling
		buffer.putShort(scale_point_dist_min); // absolute min
		buffer.putShort(scale_point_dist_max); // absolute max
		buffer.putShort(scale_point_rand_freq); // freqency of direction change - based on rand(1024)

		buffer.putShort(scale_x_mult); // x multiplyer for scaling
		buffer.putShort(scale_y_mult); // y multiplyer for scaling

		// Used for center point movement
		buffer.putShort(morph_wall_point); // actual wall point to drag
		buffer.putShort(morph_ang); // angle moving from CENTER
		buffer.putShort(morph_speed); // speed of movement
		buffer.putShort(morph_dist_max); // radius boundry
		buffer.putShort(morph_rand_freq); // freq of dir change
		buffer.putShort(morph_dist); // dist from CENTER
		buffer.putShort(morph_z_speed); // z speed for morph point
		buffer.putShort(morph_xoff); // save xoff from center
		buffer.putShort(morph_yoff); // save yoff from center

		// buffer.putShort(scale_rand_reverse); // random at random interval
		// limit rotation angle
		buffer.putShort(limit_ang_center); // for limiting the angle of turning - turrets etc
		buffer.putShort(limit_ang_delta); //

		return buffer.array();
	}

	public void load(Resource res) {
		int ndx = res.readInt();
		if(ndx != -1)
			PreMoveAnimator = SOAnimator.values()[ndx];
		else PreMoveAnimator = null;
		
		ndx = res.readInt();
		if(ndx != -1)
			PostMoveAnimator = SOAnimator.values()[ndx];
		else PostMoveAnimator = null;
		
		ndx = res.readInt();
		if(ndx != -1)
			Animator = SOAnimator.values()[ndx];
		else Animator = null;

		controller = res.readInt();
		sp_child = res.readInt();

		xmid = res.readInt();
		ymid = res.readInt();
		zmid = res.readInt();
		vel = res.readInt(); // velocity
		vel_tgt = res.readInt();
		player_xoff = res.readInt(); // player x offset from the xmid
		player_yoff = res.readInt(); // player y offset from the ymid
		
		for(int i = 0; i < MAX_SO_SECTOR; i++) {
			zorig_floor[i] = res.readInt();
			zorig_ceiling[i] = res.readInt();
		}

		zdelta = res.readInt(); // z delta from original
		z_tgt = res.readInt(); // target z delta
		z_rate = res.readInt(); // rate at which z aproaches target
		update = res.readInt(); // Distance from player at which you continue updating
		// only works for single player.
		bob_diff = res.readInt(); // bobbing difference for the frame
		target_dist = res.readInt(); // distance to next point
		floor_loz = res.readInt(); // floor low z
		floor_hiz = res.readInt(); // floor hi z
		morph_z = res.readInt(); // morphing point z
		morph_z_min = res.readInt(); // morphing point z min
		morph_z_max = res.readInt();
		bob_amt = res.readInt(); // bob amount max in z coord
		// variables set by mappers for drivables
		drive_angspeed = res.readInt();
		drive_angslide = res.readInt();
		drive_speed = res.readInt();
		drive_slide = res.readInt();
		crush_z = res.readInt();
		flags = res.readInt();
		
		for(int i = 0; i < MAX_SO_SECTOR; i++) 
			sector[i] = res.readShort();
		for(int i = 0; i < MAX_SO_SPRITE; i++) 
			sp_num[i] = res.readShort();
		for(int i = 0; i < MAX_SO_POINTS; i++) {
			xorig[i] = res.readShort();
			yorig[i] = res.readShort();
		}

		sectnum = res.readShort(); // current secnum of midpoint
		mid_sector = res.readShort(); // middle sector
		max_damage = res.readShort(); // max damage
		ram_damage = res.readShort(); // damage taken by ramming
		wait_tics = res.readShort(); //
		num_sectors = res.readShort(); // number of sectors
		num_walls = res.readShort(); // number of sectors
		track = res.readShort(); // the track # 0 to 20
		point = res.readShort(); // the point on the track that the sector object is headed toward
		vel_rate = res.readShort(); // rate at which velocity aproaches target
		dir = res.readShort(); // direction traveling on the track
		ang = res.readShort(); // angle facing
		ang_moving = res.readShort(); // angle the SO is facing
		clipdist = res.readShort(); // cliping distance for operational sector objects

		for(int i = 0; i < MAX_CLIPBOX; i++) {
			clipbox_dist[i] = res.readShort();
			clipbox_xoff[i] = res.readShort();
			clipbox_yoff[i] = res.readShort();
			clipbox_ang[i] = res.readShort();
			clipbox_vdist[i] = res.readShort();
		}

		clipbox_num = res.readShort();
		ang_tgt = res.readShort(); // target angle
		ang_orig = res.readShort(); // original angle
		last_ang = res.readShort(); // last angle before started spinning
		old_ang = res.readShort(); // holding variable for the old angle
		spin_speed = res.readShort(); // spin_speed
		spin_ang = res.readShort(); // spin angle
		turn_speed = res.readShort(); // shift value determines how fast SO turns to match new angle
		bob_sine_ndx = res.readShort(); // index into sine table
		bob_speed = res.readShort(); // shift value for speed
		op_main_sector = res.readShort(); // main sector operational SO moves in - for speed purposes
		save_vel = res.readShort(); // save velocity
		save_spin_speed = res.readShort(); // save spin speed
		match_event = res.readShort(); // match number
		match_event_sprite = res.readShort(); // spritenum of the match event sprite
		// SO Scaling Vector Info
		scale_type = res.readShort(); // type of scaling - enum controled
		scale_active_type = res.readShort(); // activated by a switch or trigger

		// values for whole SO
		scale_dist = res.readShort(); // distance from center
		scale_speed = res.readShort(); // speed of scaling
		scale_dist_min = res.readShort(); // absolute min
		scale_dist_max = res.readShort(); // absolute max
		scale_rand_freq = res.readShort(); // freqency of direction change - based on rand(1024)

		// values for single point scaling
		for(int i = 0; i < MAX_SO_POINTS; i++) {
			scale_point_dist[i] = res.readShort();
			scale_point_speed[i] = res.readShort();
		}

		scale_point_base_speed = res.readShort(); // base speed of scaling
		scale_point_dist_min = res.readShort(); // absolute min
		scale_point_dist_max = res.readShort(); // absolute max
		scale_point_rand_freq = res.readShort(); // freqency of direction change - based on rand(1024)

		scale_x_mult = res.readShort(); // x multiplyer for scaling
		scale_y_mult = res.readShort(); // y multiplyer for scaling

		// Used for center point movement
		morph_wall_point = res.readShort(); // actual wall point to drag
		morph_ang = res.readShort(); // angle moving from CENTER
		morph_speed = res.readShort(); // speed of movement
		morph_dist_max = res.readShort(); // radius boundry
		morph_rand_freq = res.readShort(); // freq of dir change
		morph_dist = res.readShort(); // dist from CENTER
		morph_z_speed = res.readShort(); // z speed for morph point
		morph_xoff = res.readShort(); // save xoff from center
		morph_yoff = res.readShort(); // save yoff from center

		// scale_rand_reverse = res.readShort(); // random at random interval
		// limit rotation angle
		limit_ang_center = res.readShort(); // for limiting the angle of turning - turrets etc
		limit_ang_delta = res.readShort(); //
	}
	
	public void copy(Sector_Object src)
	{
		this.PreMoveAnimator = src.PreMoveAnimator;
		this.PostMoveAnimator = src.PostMoveAnimator;
		this.Animator = src.Animator;

		this.controller = src.controller;
		this.sp_child = src.sp_child;

		this.xmid = src.xmid;
		this.ymid = src.ymid;
		this.zmid = src.zmid;
		this.vel = src.vel;
		this.vel_tgt = src.vel_tgt;
		this.player_xoff = src.player_xoff;
		this.player_yoff = src.player_yoff;
		
		System.arraycopy(src.zorig_floor, 0, zorig_floor, 0, MAX_SO_SECTOR);
		System.arraycopy(src.zorig_ceiling, 0, zorig_ceiling, 0, MAX_SO_SECTOR);

		this.zdelta = src.zdelta;
		this.z_tgt = src.z_tgt;
		this.z_rate = src.z_rate;
		this.update = src.update;

		this.bob_diff = src.bob_diff;
		this.target_dist = src.target_dist;
		this.floor_loz = src.floor_loz;
		this.floor_hiz = src.floor_hiz;
		this.morph_z = src.morph_z;
		this.morph_z_min = src.morph_z_min;
		this.morph_z_max = src.morph_z_max;
		this.bob_amt = src.bob_amt;
		this.drive_angspeed = src.drive_angspeed;
		this.drive_angslide = src.drive_angslide;
		this.drive_speed = src.drive_speed;
		this.drive_slide = src.drive_slide;
		this.crush_z = src.crush_z;
		this.flags = src.flags;
		
		System.arraycopy(src.sector, 0, sector, 0, MAX_SO_SECTOR);
		System.arraycopy(src.sp_num, 0, sp_num, 0, MAX_SO_SPRITE);
		System.arraycopy(src.xorig, 0, xorig, 0, MAX_SO_POINTS);
		System.arraycopy(src.yorig, 0, yorig, 0, MAX_SO_POINTS);
		
		this.sectnum = src.sectnum;
		this.mid_sector = src.mid_sector;
		this.max_damage = src.max_damage;
		this.ram_damage = src.ram_damage;
		this.wait_tics = src.wait_tics;
		this.num_sectors = src.num_sectors;
		this.num_walls = src.num_walls;
		this.track = src.track;
		this.point = src.point;
		this.vel_rate = src.vel_rate;
		this.dir = src.dir;
		this.ang = src.ang;
		this.ang_moving = src.ang_moving;
		this.clipdist = src.clipdist;

		System.arraycopy(src.clipbox_dist, 0, clipbox_dist, 0, MAX_CLIPBOX);
		System.arraycopy(src.clipbox_xoff, 0, clipbox_xoff, 0, MAX_CLIPBOX);
		System.arraycopy(src.clipbox_yoff, 0, clipbox_yoff, 0, MAX_CLIPBOX);
		System.arraycopy(src.clipbox_ang, 0, clipbox_ang, 0, MAX_CLIPBOX);
		System.arraycopy(src.clipbox_vdist, 0, clipbox_vdist, 0, MAX_CLIPBOX);

		this.clipbox_num = src.clipbox_num;
		this.ang_tgt = src.ang_tgt;
		this.ang_orig = src.ang_orig;
		this.last_ang = src.last_ang;
		this.old_ang = src.old_ang;
		this.spin_speed = src.spin_speed;
		this.spin_ang = src.spin_ang;
		this.turn_speed = src.turn_speed;
		this.bob_sine_ndx = src.bob_sine_ndx;
		this.bob_speed = src.bob_speed;
		this.op_main_sector = src.op_main_sector;
		this.save_vel = src.save_vel;
		this.save_spin_speed = src.save_spin_speed;
		this.match_event = src.match_event;
		this.match_event_sprite = src.match_event_sprite;
		this.scale_type = src.scale_type;
		this.scale_active_type = src.scale_active_type;

		this.scale_dist = src.scale_dist;
		this.scale_speed = src.scale_speed;
		this.scale_dist_min = src.scale_dist_min;
		this.scale_dist_max = src.scale_dist_max;
		this.scale_rand_freq = src.scale_rand_freq;

		System.arraycopy(src.scale_point_dist, 0, scale_point_dist, 0, MAX_SO_POINTS);
		System.arraycopy(src.scale_point_speed, 0, scale_point_speed, 0, MAX_SO_POINTS);

		this.scale_point_base_speed = src.scale_point_base_speed;
		this.scale_point_dist_min = src.scale_point_dist_min;
		this.scale_point_dist_max = src.scale_point_dist_max;
		this.scale_point_rand_freq = src.scale_point_rand_freq;

		this.scale_x_mult = src.scale_x_mult;
		this.scale_y_mult = src.scale_y_mult;

		this.morph_wall_point = src.morph_wall_point;
		this.morph_ang = src.morph_ang;
		this.morph_speed = src.morph_speed;
		this.morph_dist_max = src.morph_dist_max;
		this.morph_rand_freq = src.morph_rand_freq;
		this.morph_dist = src.morph_dist;
		this.morph_z_speed = src.morph_z_speed;
		this.morph_xoff = src.morph_xoff;
		this.morph_yoff = src.morph_yoff;

		this.limit_ang_center = src.limit_ang_center;
		this.limit_ang_delta = src.limit_ang_delta;
	}
}
