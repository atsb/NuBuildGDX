package ru.m210projects.Wang.Type;

public class LastLevelUser {

	public short WeaponNum;
	public short LastWeaponNum;
	public short Health;

	public void copy(USER p) {
		this.WeaponNum = p.WeaponNum;
		this.LastWeaponNum = p.LastWeaponNum;
		this.Health = p.Health;
	}

	public void reset() {
		this.WeaponNum = 0;
		this.LastWeaponNum = 0;
		this.Health = 0;
	}

}
