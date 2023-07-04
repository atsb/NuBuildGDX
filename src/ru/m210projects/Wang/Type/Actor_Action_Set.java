package ru.m210projects.Wang.Type;

import ru.m210projects.Wang.Sprites.StateGroup;

public class Actor_Action_Set extends Saveable {

	public static final int MAX_ACTOR_CLOSE_ATTACK = 2;
	public static final int MAX_ACTOR_ATTACK = 6;

	public final StateGroup Stand;
	public final StateGroup Run;
	public final StateGroup Jump;
	public final StateGroup Fall;
	public final StateGroup Crawl;
	public final StateGroup Swim;
	public final StateGroup Fly;
	public final StateGroup Rise;
	public final StateGroup Sit;
	public final StateGroup Look;
	public final StateGroup Climb;
	public final StateGroup Pain;
	public final StateGroup Death1;
	public final StateGroup Death2;
	public final StateGroup Dead;
	public final StateGroup DeathJump;
	public final StateGroup DeathFall;

	public final StateGroup[] CloseAttack;
	public final short[] CloseAttackPercent;

	public final StateGroup[] Attack;
	public final short[] AttackPercent;

	public final StateGroup[] Special;
	public final StateGroup Duck;
	public final StateGroup Dive;

	public Actor_Action_Set(StateGroup Stand, StateGroup Run, StateGroup Jump, StateGroup Fall, StateGroup Crawl,
			StateGroup Swim, StateGroup Fly, StateGroup Rise, StateGroup Sit, StateGroup Look, StateGroup Climb,
			StateGroup Pain, StateGroup Death1, StateGroup Death2, StateGroup Dead, StateGroup DeathJump,
			StateGroup DeathFall,

			StateGroup[] CloseAttack, short[] CloseAttackPercent,

			StateGroup[] Attack, short[] AttackPercent,

			StateGroup[] Special, StateGroup Duck, StateGroup Dive) {
		this.Stand = Stand;
		this.Run = Run;
		this.Jump = Jump;
		this.Fall = Fall;
		this.Crawl = Crawl;
		this.Swim = Swim;
		this.Fly = Fly;
		this.Rise = Rise;
		this.Sit = Sit;
		this.Look = Look;
		this.Climb = Climb;
		this.Pain = Pain;
		this.Death1 = Death1;
		this.Death2 = Death2;
		this.Dead = Dead;
		this.DeathJump = DeathJump;
		this.DeathFall = DeathFall;
		this.CloseAttack = CloseAttack;
		this.CloseAttackPercent = CloseAttackPercent;
		this.Attack = Attack;
		this.AttackPercent = AttackPercent;
		this.Special = Special;
		this.Duck = Duck;
		this.Dive = Dive;
	}

}
