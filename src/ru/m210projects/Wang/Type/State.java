package ru.m210projects.Wang.Type;

import ru.m210projects.Wang.Sprites.StateGroup;

public class State extends Saveable {

	public final short Pic;
	public final int Tics;
	public final Animator Animator;
	public int id;

	private State NextState;
	private StateGroup Nextgroup;
	
	public State(int pic, int tics, Animator anim)
	{
		this.id = 0;
		this.Pic = (short) pic;
		this.Tics = tics;
		this.Animator = anim;
	}

	public State setNext(State pState) {
		this.NextState = pState;
		return this;
	}
	
	public State setNext() {
		this.NextState = this;
		return this;
	}
	
	public State getNext() {
		return NextState;
	}
	
	public State setNextGroup(StateGroup group) {
		this.Nextgroup = group;
		return this;
	}
	
	public StateGroup getNextGroup() {
		return Nextgroup;
	}
	
	public static void InitState(State[] list) {
		int len = list.length;
		for (int st = 0; st < len; st++) {
			if (list[st].getNext() == null)
				list[st].setNext(list[(st + 1) % len]);
			list[st].id = st;
		}
	}
}
