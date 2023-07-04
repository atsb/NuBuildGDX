package ru.m210projects.Wang.Type;

import static ru.m210projects.Wang.Game.Player;

import com.badlogic.gdx.utils.Pool.Poolable;

import ru.m210projects.Build.FileHandle.BufferResource;
import ru.m210projects.Build.FileHandle.Resource;

public class Panel_Sprite implements Poolable {

	public Panel_Sprite Prev, Next;

	public int siblingNdx = -1; // temporary variable to help loading the game
	public Panel_Sprite sibling;
	public Panel_State State, RetractState, PresentState, ActionState, RestState;

	public int PlayerP = -1;
	// Do not change the order of this line
	public int xfract;
	public int x;
	public int yfract;
	public int y; // Do not change the order of this
	// line

	public PANEL_SPRITE_OVERLAY[] over = new PANEL_SPRITE_OVERLAY[8];
	public Panel_Sprite_Func PanelSpriteFunc;
	public short ID; // id for finding sprite types on the list
	public short picndx; // for pip stuff in conpic.h
	public short picnum; // bypass pip stuff in conpic.h
	public short x1, y1, x2, y2; // for rotatesprites box cliping
	public short vel, vel_adj;
	public short numpages;
	public int xorig, yorig, flags, priority;
	public int scale;
	public int jump_speed, jump_grav; // jumping vars
	public int xspeed;
	public short tics, delay; // time vars
	public short ang, rotate_ang;
	public short sin_ndx, sin_amt, sin_arc_speed;
	public short bob_height_shift;
	public short shade, pal;
	public short kill_tics;
	public short WeaponType; // remember my own weapon type for weapons with secondary function

	public Panel_Sprite() {
		for (int i = 0; i < 8; i++)
			over[i] = new PANEL_SPRITE_OVERLAY();
	}

	public PlayerStr PlayerP() {
		return PlayerP != -1 ? Player[PlayerP] : null;
	}

	public Panel_Sprite InitList() {
		Next = this;
		return this;
	}

	public void save(BufferResource fil) {
		fil.writeInt(flags);

		fil.writeInt(State != null ? State.ordinal() : -1);
		fil.writeInt(RetractState != null ? RetractState.ordinal() : -1);
		fil.writeInt(PresentState != null ? PresentState.ordinal() : -1);
		fil.writeInt(ActionState != null ? ActionState.ordinal() : -1);
		fil.writeInt(RestState != null ? RestState.ordinal() : -1);

		fil.writeInt(PlayerP);
		fil.writeInt(xfract);
		fil.writeInt(x);
		fil.writeInt(yfract);
		fil.writeInt(y);

		for (int i = 0; i < 8; i++)
			over[i].save(fil);

		fil.writeInt(PanelSpriteFunc != null ? PanelSpriteFunc.ordinal() : -1);

		fil.writeShort(ID);
		fil.writeShort(picndx);
		fil.writeShort(picnum);
		fil.writeShort(x1);
		fil.writeShort(y1);
		fil.writeShort(x2);
		fil.writeShort(y2);
		fil.writeShort(vel);
		fil.writeShort(vel_adj);
		fil.writeShort(numpages);
		fil.writeInt(xorig);
		fil.writeInt(yorig);
		fil.writeInt(flags);
		fil.writeInt(yfract);
		fil.writeInt(priority);
		fil.writeInt(scale);
		fil.writeInt(jump_speed);
		fil.writeInt(jump_grav);
		fil.writeInt(xspeed);
		fil.writeShort(tics);
		fil.writeShort(delay);
		fil.writeShort(ang);
		fil.writeShort(rotate_ang);
		fil.writeShort(sin_ndx);
		fil.writeShort(sin_amt);
		fil.writeShort(sin_arc_speed);
		fil.writeShort(bob_height_shift);
		fil.writeShort(shade);
		fil.writeShort(pal);
		fil.writeShort(kill_tics);
		fil.writeShort(WeaponType);
	}

	public void load(Resource res) {
		flags = res.readInt();

		State = (Panel_State) Saveable.valueOf(res.readInt());
		RetractState = (Panel_State) Saveable.valueOf(res.readInt());
		PresentState = (Panel_State) Saveable.valueOf(res.readInt());
		ActionState = (Panel_State) Saveable.valueOf(res.readInt());
		RestState = (Panel_State) Saveable.valueOf(res.readInt());

		PlayerP = res.readInt();
		xfract = res.readInt();
		x = res.readInt();
		yfract = res.readInt();
		y = res.readInt();

		for (int i = 0; i < 8; i++)
			over[i].load(res);

		PanelSpriteFunc = (Panel_Sprite_Func) Saveable.valueOf(res.readInt());

		ID = res.readShort();
		picndx = res.readShort();
		picnum = res.readShort();
		x1 = res.readShort();
		y1 = res.readShort();
		x2 = res.readShort();
		y2 = res.readShort();
		vel = res.readShort();
		vel_adj = res.readShort();
		numpages = res.readShort();
		xorig = res.readInt();
		yorig = res.readInt();
		flags = res.readInt();
		yfract = res.readInt();
		priority = res.readInt();
		scale = res.readInt();
		jump_speed = res.readInt();
		jump_grav = res.readInt();
		xspeed = res.readInt();
		tics = res.readShort();
		delay = res.readShort();
		ang = res.readShort();
		rotate_ang = res.readShort();
		sin_ndx = res.readShort();
		sin_amt = res.readShort();
		sin_arc_speed = res.readShort();
		bob_height_shift = res.readShort();
		shade = res.readShort();
		pal = res.readShort();
		kill_tics = res.readShort();
		WeaponType = res.readShort();
	}

	@Override
	public void reset() {
		Prev = Next = null;
		siblingNdx = -1;
		sibling = null;
		State = RetractState = PresentState = ActionState = RestState = null;

		PlayerP = -1;
		xfract = 0;
		x = 0;
		yfract = 0;
		y = 0;

		for (int i = 0; i < over.length; i++) {
			over[i].State = null;
			over[i].flags = 0;
			over[i].tics = 0;
			over[i].pic = -1;
			over[i].xoff = -1;
			over[i].yoff = -1;
		}

		PanelSpriteFunc = null;
		ID = 0;
		picndx = 0;
		picnum = 0;
		x1 = y1 = x2 = y2 = 0;
		vel = vel_adj = 0;

		xorig = yorig = flags = priority = 0;
		scale = 0;
		jump_speed = jump_grav = 0;
		xspeed = 0;
		tics = delay = 0;
		ang = rotate_ang = 0;
		sin_ndx = sin_amt = sin_arc_speed = 0;
		bob_height_shift = 0;
		shade = pal = 0;
		kill_tics = 0;
		WeaponType = 0;
	}

	public void copy(Panel_Sprite src)
	{
//		Prev, Next;
//		siblingNdx = -1;
//		sibling;
		
		this.State = src.State;
		this.RetractState = src.RetractState;
		this.PresentState = src.PresentState;
		this.ActionState = src.ActionState;
		this.RestState = src.RestState;
		
		this.PlayerP = src.PlayerP;
		this.xfract = src.xfract;
		this.x = src.x;
		this.yfract = src.yfract;
		this.y = src.y;

		for (int i = 0; i < 8; i++)
			this.over[i].copy(src.over[i]);
		this.PanelSpriteFunc = src.PanelSpriteFunc;
		this.ID = src.ID;
		this.picndx = src.picndx;
		this.picnum = src.picnum;
		this.x1 = src.x1;
		this.y1 = src.y1;
		this.x2 = src.x2;
		this.y2 = src.y2;
		this.vel = src.vel;
		this.vel_adj = src.vel_adj;
		this.numpages = src.numpages;
		this.xorig = src.xorig;
		this.yorig = src.yorig;
		this.flags = src.flags;
		this.yfract = src.yfract;
		this.priority = src.priority;
		this.scale = src.scale;
		this.jump_speed = src.jump_speed;
		this.jump_grav = src.jump_grav;
		this.xspeed = src.xspeed;
		this.tics = src.tics;
		this.delay = src.delay;
		this.ang = src.ang;
		this.rotate_ang = src.rotate_ang;
		this.sin_ndx = src.sin_ndx;
		this.sin_amt = src.sin_amt;
		this.sin_arc_speed = src.sin_arc_speed;
		this.bob_height_shift = src.bob_height_shift;
		this.shade = src.shade;
		this.pal = src.pal;
		this.kill_tics = src.kill_tics;
		this.WeaponType = src.WeaponType;
	}
}
