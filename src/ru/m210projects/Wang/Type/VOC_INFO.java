package ru.m210projects.Wang.Type;

public class VOC_INFO {
	public static final int MAXSOURCEINTICK = 32;
	
	public final String name; // name of voc file on disk
	public VOC data; // pointer to voc data
	public int datalen; // length of voc data
	public final int pitch_lo; // lo pitch value
	public final int pitch_hi; // hi pitch value
	public final int priority; // priority at which vocs are played
	public final int voc_num; // Backward reference to parent sound
	public final int voc_distance; // Sound's distance effectiveness
	public final int voc_flags; // Various allowable flag settings for voc
	public int playing; // number of this type of sound currently playing
	public int lock;

	public VOC_INFO(String name, int id, int id_num, int pri, int pitch_lo, int pitch_hi, int voc_num, int voc_dist,
			int voc_flags) {
		this.name = name;
		this.data = null;
		this.datalen = 0;
		this.pitch_lo = pitch_lo;
		this.pitch_hi = pitch_hi;
		this.priority = pri;
		this.voc_num = voc_num;
		this.voc_distance = voc_dist;
		this.voc_flags = voc_flags;
		this.playing = 0;
		this.lock = MAXSOURCEINTICK;
	}
}
