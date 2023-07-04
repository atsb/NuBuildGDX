package ru.m210projects.Wang.Type;

import static ru.m210projects.Wang.Type.MyTypes.TEST;

public class Panel_State extends Saveable {

	public final int id;
	public final short picndx; // for pip stuff in conpic.h
	public final int tics;
	public final Panel_Sprite_Func Animator;
	private Panel_State NextState, PlusOne;
	private int flags;

	public Panel_State(int id, int picndx, int tics, Panel_Sprite_Func Animator) {
		this.picndx = (short) picndx;
		this.tics = tics;
		this.Animator = Animator;
		this.id = id;
	}
	
	public Panel_State(int picndx, int tics, Panel_Sprite_Func Animator) {
		this.id = 0;
		this.picndx = (short) picndx;
		this.tics = tics;
		this.Animator = Animator;
	}
	
	public boolean testFlag(int val)
	{
		return TEST(this.flags, val);
	}
	
	public Panel_State setFlags(int flags) {
		this.flags = flags;
		return this;
	}
	
	public Panel_State setPlusOne(Panel_State PlusOne) {
		this.PlusOne = PlusOne;
		return this;
	}
	
	public Panel_State setNext(Panel_State next) {
		this.NextState = next;
		if(this.PlusOne == null)
			this.PlusOne = next;
		return this;
	}

	public Panel_State setNext() {
		this.NextState = this;
		return this;
	}
	
	public Panel_State getNext()
	{
		return NextState;
	}
	
	public Panel_State getPlusOne()
	{
		return PlusOne;
	}
}
