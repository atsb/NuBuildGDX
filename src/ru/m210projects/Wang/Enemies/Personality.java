package ru.m210projects.Wang.Enemies;

import ru.m210projects.Wang.Type.Saveable;

public class Personality extends Saveable {

	public final Decision[] Battle;
	public final Decision[] Offense;
	public final Decision[] Broadcast;
	public final Decision[] Surprised;
	public final Decision[] Evasive;
	public final Decision[] LostTarget;
	public final Decision[] CloseRange;
	public final Decision[] TouchTarget;

	public Personality(Decision[] Battle, Decision[] Offense, Decision[] Broadcast, Decision[] Surprised, Decision[] Evasive,
			Decision[] LostTarget, Decision[] CloseRange, Decision[] TouchTarget) {
		this.Battle = Battle;
		this.Offense = Offense;
		this.Broadcast = Broadcast;
		this.Surprised = Surprised;
		this.Evasive = Evasive;
		this.LostTarget = LostTarget;
		this.CloseRange = CloseRange;
		this.TouchTarget = TouchTarget;
	}
}
