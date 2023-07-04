package ru.m210projects.Wang;

public class JTags {

//////////////////////////////////////////////////////////////////////////////////////////
//
//SPRITE TAGS (TAG THE SPRITES'S HITAG)  ST1 tags
//
//////////////////////////////////////////////////////////////////////////////////////////
	public static final int AI_NORMAL = 0;
	public static final int AI_EVASIVE = 1;
	public static final int AI_SNIPER = 2;
	public static final int AI_GUNGHO = 3;

	public static final int SWITCH_LOCKED = 29;

//My sprite sprite tags start at 1000 to be separate from Frank's

//* Magic mirror cameras
//* LOTAG is the unique camera number
	public static final int MIRROR_CAM = 1000;
//* These are spots at which a pissed off mirror will spawn a coolie ghost
//* Make sure to set the skill levels on these sprites too.
	public static final int MIRROR_SPAWNSPOT = 1001;

//* Ambient Sounds
//* LOTAG is the enumerated sound num to play
	public static final int AMBIENT_SOUND = 1002;
	public static final int TAG_NORESPAWN_FLAG = 1003;
	public static final int TAG_GET_STAR = 1004;
	public static final int TAG_ECHO_SOUND = 1005;
	public static final int TAG_DRIPGEN = 1006;
	public static final int TAG_BUBBLEGEN = 1007;
	public static final int TAG_SWARMSPOT = 1008;

	public static final int TAG_PACHINKOLIGHT = 9997;
	public static final int TAG_INVISONINJA = 9998;
	public static final int LUMINOUS = 9999;

//////////////////////////////////////////////////////////////////////////////////////////
//
//WALL TAGS (TAG THE WALL'S LOTAG)
//
//////////////////////////////////////////////////////////////////////////////////////////

//* Turns a regular mirror into a magic mirror that shows a room containing ST1 sprite at
//* sprite's angle and z height.
//* HITAG is unique camera sprite number matching the ST1 camera sprite
	public static final int TAG_WALL_MAGIC_MIRROR = 306;

//////////////////////////////////////////////////////////////////////////////////////////
//
//LIGHTING TAGS (TAG THE SECTOR'S LOTAG)
//
//////////////////////////////////////////////////////////////////////////////////////////

//* Fade effect.  Fades in and out smoothly.  
//* Ceiling is minimum darkness.
//* Floor is maximum darkness.
//* High byte is speed of flicker.  
//* The lower the number the faster.  Default is 3.  I recommend 8.
//* Use TAG_LIGHT_FADE_DIFFUSE tags around the initial torch sector just like light fade.
//* A good value to use for torches, is a 2 in high tag of TAG_LIGHT_FADE_DIFFUSE

	public static final int TAG_LIGHT_TORCH_FADE = 305;

}
