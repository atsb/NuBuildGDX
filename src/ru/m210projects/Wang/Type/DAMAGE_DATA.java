package ru.m210projects.Wang.Type;

import ru.m210projects.Wang.Player.Player_Action_Func;

public class DAMAGE_DATA {
	public int id;
	public Player_Action_Func init;
	public short damage_lo;
	public short damage_hi;
	public int radius;
	public short max_ammo;
	public short min_ammo;
	public short with_weapon;
	
	public DAMAGE_DATA(int id, Player_Action_Func init, int damage_lo, int damage_hi, int radius, int max_ammo, int min_ammo, int with_weapon) {
		this.id = id;
		this.init = init;
		this.damage_lo = (short) damage_lo;
		this.damage_hi = (short) damage_hi;
		this.radius = radius;
		this.max_ammo = (short) max_ammo;
		this.min_ammo = (short) min_ammo;
		this.with_weapon = (short) with_weapon;
	}
}
