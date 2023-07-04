package ru.m210projects.Wang.Type;

import static ru.m210projects.Build.Engine.*;

import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Build.FileHandle.BufferResource;
import ru.m210projects.Build.FileHandle.Resource;
import ru.m210projects.Wang.Enemies.Personality;
import ru.m210projects.Wang.Sprites.StateGroup;

public class USER {

	//
	// Variables that can be used by actors and Player
	//
	public RotatorStr rotator;

	// wall vars for lighting
	public int WallCount;
	public byte[] WallShade; // malloced - save off wall shades for lighting

	public int WallP = -1; // operate on wall instead of sprite
	public State State;
	public StateGroup Rot;
	public State StateStart;
	public State StateEnd;
	public StateGroup StateFallOverride; // a bit kludgy - override std fall state

	public Animator ActorActionFunc;
	public Actor_Action_Set ActorActionSet;
	public Personality Personality;
	public ATTRIBUTE Attrib;
	public int sop_parent = -1; // denotes that this sprite is a part of the
	// sector object - contains info for the SO

	public int ox, oy, oz;

	public int Flags;
	public int Flags2;
	public int Tics;

	public short RotNum;
	public short ID;

	// Health/Pain related
	public short Health;
	public short MaxHealth;

	public short LastDamage; // last damage amount taken
	public short PainThreshold; // amount of damage that can be taken before
	// going into pain frames.

	// jump & fall
	public short jump_speed;
	public short jump_grav;

	// clipmove
	public short ceiling_dist;
	public short floor_dist;
	public short lo_step;
	public int hiz, loz;
	public int zclip; // z height to move up for clipmove
	public short hi_sectp = -1, lo_sectp = -1;
	public int hi_sp = -1, lo_sp = -1;

	public int active_range;

	public short SpriteNum;
	public short Attach; // attach to sprite if needed - electro snake
//	public SPRITE SpriteP;

	// if a player's sprite points to player structure
	public int PlayerP = -1;
	public short Sibling;

	//
	// Possibly used by both.
	//

	// precalculated vectors
	public int xchange, ychange, zchange;

	public int z_tgt;

	// velocity
	public int vel_tgt;
	public short vel_rate;
	public byte speed; // Ordinal Speed Range 0-3 from slow to fast

	public short Counter;
	public short Counter2;
	public short Counter3;
	public short DamageTics;
	public short BladeDamageTics;

	public short WpnGoal;
	public int Radius; // for distance checking
	public int OverlapZ; // for z overlap variable

	//
	// Only have a place for actors
	//

	// For actors on fire
	public short flame;

	// target player for the enemy - can only handle one player at at time
	// PlayerStr tgt_player;
	public int tgt_sp = -1;

	// scaling
	public short scale_speed;
	public int scale_value;
	public short scale_tgt;

	// zig zagging
	public short DistCheck;

	public short Dist;
	public short TargetDist;
	public short WaitTics;

	// track
	public short track;
	public short point;
	public short track_dir;
	public int track_vel;

	// sliding variables - slide backwards etc
	public short slide_ang;
	public int slide_vel;
	public short slide_dec;

	public short motion_blur_dist;
	public short motion_blur_num;

	public short wait_active_check; // for enemy checking of player
	public short inactive_time; // length of time actor has been unaware of his tgt
	public int sx, sy, sz;
	public short sang;
	public byte spal; // save off default palette number

	public int ret; // holder for move_sprite return value

	// Need to get rid of these flags
	public int Flag1;

	public short LastWeaponNum;
	public short WeaponNum;

	public short bounce; // count bounces off wall for killing shrap stuff
	// !JIM! my extensions
	public int ShellNum; // This is shell no. 0 to whatever
	// Shell gets deleted when ShellNum < (ShellCount - MAXSHELLS)
	public short FlagOwner; // The spritenum of the original flag
	public short Vis; // Shading upgrade, for shooting, etc...
	public boolean DidAlert; // Has actor done his alert noise before?

	public void save(BufferResource fil) {
		fil.writeBoolean(rotator != null);
		if (rotator != null) {
			fil.writeInt(rotator.pos);
			fil.writeInt(rotator.open_dest);
			fil.writeInt(rotator.tgt);
			fil.writeInt(rotator.speed);
			fil.writeInt(rotator.orig_speed);
			fil.writeInt(rotator.vel);
			fil.writeInt(rotator.num_walls);

			fil.writeInt(rotator.orig != null ? rotator.orig.length : -1);
			if (rotator.orig != null) {
				for (int i = 0; i < rotator.orig.length; i++) {
					fil.writeInt(rotator.orig[i].x);
					fil.writeInt(rotator.orig[i].y);
				}
			}
		}

		fil.writeInt(WallCount);

		fil.writeInt(WallShade != null ? WallShade.length : -1);
		if (WallShade != null)
			fil.writeBytes(WallShade);

		fil.writeInt(WallP);
		fil.writeInt(State != null ? State.ordinal() : -1);
		fil.writeInt(Rot != null ? Rot.index() : -1);
		fil.writeInt(StateStart != null ? StateStart.ordinal() : -1);
		fil.writeInt(StateEnd != null ? StateEnd.ordinal() : -1);
		fil.writeInt(StateFallOverride != null ? StateFallOverride.index() : -1);
		fil.writeInt(ActorActionFunc != null ? ActorActionFunc.ordinal() : -1);
		fil.writeInt(ActorActionSet != null ? ActorActionSet.ordinal() : -1);
		fil.writeInt(Personality != null ? Personality.ordinal() : -1);
		fil.writeInt(Attrib != null ? Attrib.ordinal() : -1);
		fil.writeInt(sop_parent);
		fil.writeInt(ox);
		fil.writeInt(oy);
		fil.writeInt(oz);
		fil.writeInt(Flags);
		fil.writeInt(Flags2);
		fil.writeInt(Tics);
		fil.writeShort(RotNum);
		fil.writeShort(ID);

		// Health/Pain related
		fil.writeShort(Health);
		fil.writeShort(MaxHealth);
		fil.writeShort(LastDamage);
		fil.writeShort(PainThreshold);
		fil.writeShort(jump_speed);
		fil.writeShort(jump_grav);
		fil.writeShort(ceiling_dist);
		fil.writeShort(floor_dist);
		fil.writeShort(lo_step);
		fil.writeInt(hiz);
		fil.writeInt(loz);
		fil.writeInt(zclip);
		fil.writeShort(hi_sectp);
		fil.writeShort(lo_sectp);
		fil.writeInt(hi_sp);
		fil.writeInt(lo_sp);
		fil.writeInt(active_range);
		fil.writeShort(SpriteNum);
		fil.writeShort(Attach);
		fil.writeInt(PlayerP);
		fil.writeShort(Sibling);
		fil.writeInt(xchange);
		fil.writeInt(ychange);
		fil.writeInt(zchange);
		fil.writeInt(z_tgt);
		fil.writeInt(vel_tgt);
		fil.writeShort(vel_rate);
		fil.writeByte(speed);
		fil.writeShort(Counter);
		fil.writeShort(Counter2);
		fil.writeShort(Counter3);
		fil.writeShort(DamageTics);
		fil.writeShort(BladeDamageTics);
		fil.writeShort(WpnGoal);
		fil.writeInt(Radius);
		fil.writeInt(OverlapZ);
		fil.writeShort(flame);
		fil.writeInt(tgt_sp);
		fil.writeShort(scale_speed);
		fil.writeInt(scale_value);
		fil.writeShort(scale_tgt);
		fil.writeShort(DistCheck);
		fil.writeShort(Dist);
		fil.writeShort(TargetDist);
		fil.writeShort(WaitTics);
		fil.writeShort(track);
		fil.writeShort(point);
		fil.writeShort(track_dir);
		fil.writeInt(track_vel);
		fil.writeShort(slide_ang);
		fil.writeInt(slide_vel);
		fil.writeShort(slide_dec);
		fil.writeShort(motion_blur_dist);
		fil.writeShort(motion_blur_num);
		fil.writeShort(wait_active_check);
		fil.writeShort(inactive_time);
		fil.writeInt(sx);
		fil.writeInt(sy);
		fil.writeInt(sz);
		fil.writeShort(sang);
		fil.writeByte(spal);
		fil.writeInt(ret);
		fil.writeInt(Flag1);
		fil.writeShort(LastWeaponNum);
		fil.writeShort(WeaponNum);
		fil.writeShort(bounce);
		fil.writeInt(ShellNum);
		fil.writeShort(FlagOwner);
		fil.writeShort(Vis);
		fil.writeBoolean(DidAlert);
	}

	public void load(Resource res) {
		boolean hasRotator = res.readBoolean();
		if (hasRotator) {
			if (rotator == null)
				rotator = new RotatorStr();
			rotator.pos = res.readInt();
			rotator.open_dest = res.readInt();
			rotator.tgt = res.readInt();
			rotator.speed = res.readInt();
			rotator.orig_speed = res.readInt();
			rotator.vel = res.readInt();
			rotator.num_walls = res.readInt();

			int len = res.readInt();
			if (len != -1) {
				rotator.orig = new Vector2i[len];
				for (int i = 0; i < len; i++) {
					rotator.orig[i] = new Vector2i();
					rotator.orig[i].x = res.readInt();
					rotator.orig[i].y = res.readInt();
				}
			}
		}

		WallCount = res.readInt();
		int ShadeLen = res.readInt();
		if (ShadeLen != -1) {
			WallShade = new byte[ShadeLen];
			res.read(WallShade);
		}

		WallP = res.readInt();
		State = (State) Saveable.valueOf(res.readInt());
		Rot = Saveable.getGroup(res.readInt());
		StateStart = (State) Saveable.valueOf(res.readInt());
		StateEnd = (State) Saveable.valueOf(res.readInt());
		StateFallOverride = Saveable.getGroup(res.readInt());
		ActorActionFunc = (Animator) Saveable.valueOf(res.readInt());
		ActorActionSet = (Actor_Action_Set) Saveable.valueOf(res.readInt());
		Personality = (Personality) Saveable.valueOf(res.readInt());
		Attrib = (ATTRIBUTE) Saveable.valueOf(res.readInt());
		sop_parent = res.readInt();
		ox = res.readInt();
		oy = res.readInt();
		oz = res.readInt();
		Flags = res.readInt();
		Flags2 = res.readInt();
		Tics = res.readInt();
		RotNum = res.readShort();
		ID = res.readShort();

		// Health/Pain related
		Health = res.readShort();
		MaxHealth = res.readShort();
		LastDamage = res.readShort();
		PainThreshold = res.readShort();
		jump_speed = res.readShort();
		jump_grav = res.readShort();
		ceiling_dist = res.readShort();
		floor_dist = res.readShort();
		lo_step = res.readShort();
		hiz = res.readInt();
		loz = res.readInt();
		zclip = res.readInt();
		hi_sectp = res.readShort();
		lo_sectp = res.readShort();
		hi_sp = res.readInt();
		lo_sp = res.readInt();
		active_range = res.readInt();
		SpriteNum = res.readShort();
		Attach = res.readShort();
		PlayerP = res.readInt();
		Sibling = res.readShort();
		xchange = res.readInt();
		ychange = res.readInt();
		zchange = res.readInt();
		z_tgt = res.readInt();
		vel_tgt = res.readInt();
		vel_rate = res.readShort();
		speed = res.readByte();
		Counter = res.readShort();
		Counter2 = res.readShort();
		Counter3 = res.readShort();
		DamageTics = res.readShort();
		BladeDamageTics = res.readShort();
		WpnGoal = res.readShort();
		Radius = res.readInt();
		OverlapZ = res.readInt();
		flame = res.readShort();
		tgt_sp = res.readInt();
		scale_speed = res.readShort();
		scale_value = res.readInt();
		scale_tgt = res.readShort();
		DistCheck = res.readShort();
		Dist = res.readShort();
		TargetDist = res.readShort();
		WaitTics = res.readShort();
		track = res.readShort();
		point = res.readShort();
		track_dir = res.readShort();
		track_vel = res.readInt();
		slide_ang = res.readShort();
		slide_vel = res.readInt();
		slide_dec = res.readShort();
		motion_blur_dist = res.readShort();
		motion_blur_num = res.readShort();
		wait_active_check = res.readShort();
		inactive_time = res.readShort();
		sx = res.readInt();
		sy = res.readInt();
		sz = res.readInt();
		sang = res.readShort();
		spal = res.readByte();
		ret = res.readInt();
		Flag1 = res.readInt();
		LastWeaponNum = res.readShort();
		WeaponNum = res.readShort();
		bounce = res.readShort();
		ShellNum = res.readInt();
		FlagOwner = res.readShort();
		Vis = res.readShort();
		DidAlert = res.readBoolean();
	}

	public void copy(USER src) {
		if (src.rotator != null) {
			if (rotator == null)
				rotator = new RotatorStr();
			rotator.copy(src.rotator);
		} else
			this.rotator = null;

		this.WallCount = src.WallCount;
		if (src.WallShade != null) {
			if (this.WallShade == null)
				this.WallShade = new byte[src.WallShade.length];
			System.arraycopy(src.WallShade, 0, this.WallShade, 0, src.WallShade.length);
		} else
			this.WallShade = null;

		this.WallP = src.WallP;
		this.State = src.State;
		this.Rot = src.Rot;
		this.StateStart = src.StateStart;
		this.StateEnd = src.StateEnd;
		this.StateFallOverride = src.StateFallOverride;
		this.ActorActionFunc = src.ActorActionFunc;
		this.ActorActionSet = src.ActorActionSet;
		this.Personality = src.Personality;
		this.Attrib = src.Attrib;

		this.sop_parent = src.sop_parent;
		this.ox = src.ox;
		this.oy = src.oy;
		this.oz = src.oz;
		this.Flags = src.Flags;
		this.Flags2 = src.Flags2;
		this.Tics = src.Tics;
		this.RotNum = src.RotNum;
		this.ID = src.ID;

		// Health/Pain related
		this.Health = src.Health;
		this.MaxHealth = src.MaxHealth;
		this.LastDamage = src.LastDamage;
		this.PainThreshold = src.PainThreshold;
		this.jump_speed = src.jump_speed;
		this.jump_grav = src.jump_grav;
		this.ceiling_dist = src.ceiling_dist;
		this.floor_dist = src.floor_dist;
		this.lo_step = src.lo_step;
		this.hiz = src.hiz;
		this.loz = src.loz;
		this.zclip = src.zclip;
		this.hi_sectp = src.hi_sectp;
		this.lo_sectp = src.lo_sectp;
		this.hi_sp = src.hi_sp;
		this.lo_sp = src.lo_sp;
		this.active_range = src.active_range;
		this.SpriteNum = src.SpriteNum;
		this.Attach = src.Attach;
		this.PlayerP = src.PlayerP;
		this.Sibling = src.Sibling;
		this.xchange = src.xchange;
		this.ychange = src.ychange;
		this.zchange = src.zchange;
		this.z_tgt = src.z_tgt;
		this.vel_tgt = src.vel_tgt;
		this.vel_rate = src.vel_rate;
		this.speed = src.speed;
		this.Counter = src.Counter;
		this.Counter2 = src.Counter2;
		this.Counter3 = src.Counter3;
		this.DamageTics = src.DamageTics;
		this.BladeDamageTics = src.BladeDamageTics;
		this.WpnGoal = src.WpnGoal;
		this.Radius = src.Radius;
		this.OverlapZ = src.OverlapZ;
		this.flame = src.flame;
		this.tgt_sp = src.tgt_sp;
		this.scale_speed = src.scale_speed;
		this.scale_value = src.scale_value;
		this.scale_tgt = src.scale_tgt;
		this.DistCheck = src.DistCheck;
		this.Dist = src.Dist;
		this.TargetDist = src.TargetDist;
		this.WaitTics = src.WaitTics;
		this.track = src.track;
		this.point = src.point;
		this.track_dir = src.track_dir;
		this.track_vel = src.track_vel;
		this.slide_ang = src.slide_ang;
		this.slide_vel = src.slide_vel;
		this.slide_dec = src.slide_dec;
		this.motion_blur_dist = src.motion_blur_dist;
		this.motion_blur_num = src.motion_blur_num;
		this.wait_active_check = src.wait_active_check;
		this.inactive_time = src.inactive_time;
		this.sx = src.sx;
		this.sy = src.sy;
		this.sz = src.sz;
		this.sang = src.sang;
		this.spal = src.spal;
		this.ret = src.ret;
		this.Flag1 = src.Flag1;
		this.LastWeaponNum = src.LastWeaponNum;
		this.WeaponNum = src.WeaponNum;
		this.bounce = src.bounce;
		this.ShellNum = src.ShellNum;
		this.FlagOwner = src.FlagOwner;
		this.Vis = src.Vis;
		this.DidAlert = src.DidAlert;
	}

	public SPRITE getSprite() {
		return SpriteNum != -1 ? sprite[SpriteNum] : null;
	}
}
