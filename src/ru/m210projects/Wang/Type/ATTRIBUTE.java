package ru.m210projects.Wang.Type;

import ru.m210projects.Wang.Ai.Attrib_Snds;

public class ATTRIBUTE extends Saveable {
	
	public final short[] Speed;
	public final short[] TicAdjust;
	public final int MaxWeapons;
	private final int[] Sounds;
	
	public ATTRIBUTE(short[] Speed, short[] TicAdjust, int MaxWeapons, int[] Sounds) {
		this.Speed = Speed;
		this.TicAdjust = TicAdjust;
		this.MaxWeapons = MaxWeapons;
		this.Sounds = Sounds;
	}
	
	public int getSound(Attrib_Snds ndx)
	{
		int index = ndx.ordinal();
		if(index < Sounds.length)
			return Sounds[index];
		return 0;
	}
}
