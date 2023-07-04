package ru.m210projects.Wang.Enemies;

import ru.m210projects.Wang.Type.Animator;

public class Decision {
	public short range;
	public Animator action;
	
	public Decision(int range, Animator action)
	{
		this.range = (short) range;
		this.action = action;
	}
}
