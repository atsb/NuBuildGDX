package ru.m210projects.Blood.Types;

public class GENDUDESND {
	public int defaultSndId;
	public int randomRange;
	public int sndIdOffset; // relative to data3
	public boolean aiPlaySound; // false = sfxStart3DSound();
	
	public GENDUDESND (int defSnd, int rndRange, int sndOffs, boolean aiSnd) {
		
		this.defaultSndId = defSnd;
		this.randomRange = rndRange;
		this.sndIdOffset = sndOffs;
		this.aiPlaySound = aiSnd;
	}

}
