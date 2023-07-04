package ru.m210projects.Wang;

public class Tags {

	//////////////////////////////////////////////////////////////////////////////////////////
	//
	// MISC TAGS
	//
	//////////////////////////////////////////////////////////////////////////////////////////

	// Stepping on this finds actors closeby that are on a track at a point defined
	// as
	// TRACK_ACTOR_WAIT_FOR_TRIGGER and activates them.
	// High tag = distance to look for actors
	public static final int TAG_TRIGGER_ACTORS = 3;

	// For ladders attached to white walls.
	public static final int TAG_LADDER = 30;

	// Shoots a fireball from a ST1 FIREBALL_TRAP (or BOLT) sprite with a matching
	// number.
	// High tag = match number to match with ST1 sprite
	public static final int TAG_TRIGGER_MISSILE_TRAP = 43;

	// Explodes wall sector
	// High tag = match number
	public static final int TAG_TRIGGER_EXPLODING_SECTOR = 44;

	//////////////////////////////////////////////////////////////////////////////////////////
	//
	// DOOR TAGS
	//
	//////////////////////////////////////////////////////////////////////////////////////////

	// High tag for z moving doors is always the speed unless otherwise noted -
	// default
	// 200

	// Red sector rotating doors. They can have up to 15 points per door and
	// rotate about any point you choose. Don't have to be rectangular, can be any
	// shape. Can
	// use masked walls with them.

	// Red sector rotating door - Master sector that has doors inside of it
	// To lock these types of doors place a SECT_LOCK_DOOR ST1 in this sector
	public static final int TAG_DOOR_ROTATE = 112;

	// Red sector rotating door - Tag the actual door with this. Rotate POSITIVE or
	// NEGATIVE 90 degrees
	public static final int TAG_DOOR_ROTATE_POS = 113;
	public static final int TAG_DOOR_ROTATE_NEG = 114;

	// Wall pivot point - MUST tag the ouside of the wall, NOT the inside!! - red
	// sector method
	// Remember - a wall is just an x,y location
	public static final int TAG_WALL_ROTATE_PIVOT = 2;

	// Tag for sliding door (Star Trek/Wolf type)
	public static final int TAG_DOOR_SLIDING = 115;
	// Wall tag for sliding door placed on the end of the door
	public static final int TAG_WALL_SLIDING_DOOR = 10;

	// Tag for exiting current level and starting up another one.
	// High Tag - Destination level number -
	// 0 - Title screen
	// 1 - e1l1
	// 2 - e1l2
	// ...
	public static final int TAG_LEVEL_EXIT_SWITCH = 116;

	//////////////////////////////////////////////////////////////////////////////////////////
	//
	// ELEVATOR TAGS
	//
	//////////////////////////////////////////////////////////////////////////////////////////

	// Mappers DONT use this!! Use ST1 SECT_VATOR.
	public static final int TAG_VATOR = 206;

	// toggles ST1 SECT_VATOR sectors
	// TAG1 = match tag
	public static final int TAG_SPRITE_SWITCH_VATOR = 206;
	public static final int TAG_SECTOR_TRIGGER_VATOR = 206;

	// toggles ST1 SCALE sector objects
	// TAG1 = match tag
	public static final int TAG_SO_SCALE_SWITCH = 208;
	public static final int TAG_SO_SCALE_TRIGGER = 208;

	// toggles ST1 SCALE sector objects
	// TAG1 = match tag
	public static final int TAG_SO_SCALE_ONCE_SWITCH = 209;
	public static final int TAG_SO_SCALE_ONCE_TRIGGER = 209;

	// TAG1 = match tag
	public static final int TAG_LIGHT_SWITCH = 210;
	public static final int TAG_LIGHT_TRIGGER = 210;

	// EVERTHING tags will trigger everything with a match tag
	// TAG1 = match tag
	public static final int TAG_SWITCH_EVERYTHING = 211;
	public static final int TAG_TRIGGER_EVERYTHING = 211;

	// TAG1 = match tag
	public static final int TAG_SWITCH_EVERYTHING_ONCE = 212;
	public static final int TAG_TRIGGER_EVERYTHING_ONCE = 212;

	public static final int TAG_COMBO_SWITCH_EVERYTHING = 213;
	public static final int TAG_COMBO_SWITCH_EVERYTHING_ONCE = 214;

	// High tag is the number to MATCH the ST1 match number.
	public static final int TAG_SO_EVENT_SWITCH = 215;
	// Same as switch but tag a sector as a trigger.
	public static final int TAG_SO_EVENT_TRIGGER = 215;

	// Spawns sprites with matching tags
	// Same as switch but tag a sector as a trigger.
	public static final int TAG_SPAWN_ACTOR_TRIGGER = 216;

	// Spawns sprites with matching tags
	// Same as switch but tag a sector as a trigger.
	public static final int TAG_SECRET_AREA_TRIGGER = 217;

	public static final int TAG_ROTATOR = 218;
	public static final int TAG_SLIDOR = 220;
	// wall tags for SLIDOR
	// hitag = match
	public static final int TAG_WALL_SLIDOR_LEFT = 220;
	public static final int TAG_WALL_SLIDOR_RIGHT = 221;
	public static final int TAG_WALL_SLIDOR_UP = 222;
	public static final int TAG_WALL_SLIDOR_DOWN = 223;

	// Step on the sector, press the space bar, and it shoves you high into the air.
	// High tag has height of jump
	public static final int TAG_SPRING_BOARD = 240;

	// Sine wave floor effect - max 5 per level
	//
	// Tag the first sector with 400, the next with 401, and so on up to 419.
	// The WAVE will actually flow from the opposite end. The WAVE direction is what
	// is
	// refered to when BEGINNING and END are referred to below.
	//
	// 1st Sector High Tag = the range in pixels the floor will undulate in the Z
	// direction
	// 2rd Sector High Tag = range decrement - takes the range from the 1st sector
	// high tag and
//	                       adjusts all the rest by this decrement - makes the WAVE start off 
//	                       small and get LARGER toward the end - THIS IS IN Z COORDINATES 
//	                       *NOT* PIXELS
	// 3nd Sector High Tag = speed of the motion - a shift value - default to 3
	// 4th Sector High Tag = a distance from one peak (top of curve) to the next -
	// default is the
//	                       number of sectors in the SINE WAVE (max of 20)
	// Last Sector High Tag= special tag to make a realistic ocean wave - modifies
	// the range
//	                       toward the END (greatest range is usually here) of the WAVE
//	                       so that it dissapates quickly. Without this it would just continue
//	                       to get bigger toward the end of the WAVE.

	public static final int TAG_SINE_WAVE_FLOOR = 400;
	public static final int TAG_SINE_WAVE_CEILING = 420;
	public static final int TAG_SINE_WAVE_BOTH = 440;

	//////////////////////////////////////////////////////////////////////////////////////////
	//
	// SECTOR OBJECT TAGS
	//
	//////////////////////////////////////////////////////////////////////////////////////////
	// Sector Objects (SOs) are groups of sectors that can be moved around on a
	////////////////////////////////////////////////////////////////////////////////////////// track.
	// All SOs must have at a minimum:
//	      1. Bounding sprites (upper left and lower right) that contains all sectors to be moved.
//	      2. A sector marked as the center of the SO.
	//
	// Tags 500-600 are set reserved for SOs. There is a maximum of 20 SOs per
	// level.
	//
	// Tags 500-504 correspond to object 1, 505-509 to object 2, 510-514 to object
	// 3, etc ...
	//
	// Bounding sprite tags are set up similar.
//	      Upper left tag of object 1 is 500, lower right is 501
//	      Upper left tag of object 2 is 505, lower right is 506
//	      Etc...
	//////////////////////////////////////////////////////////////////////////////////////////

	// The center of this sector will be the center of the SO
	// IMPORTANT: High Tag must be set to the track number
	public static final int TAG_OBJECT_CENTER = 501;

	//////////////////////////////////////////////////////////////////////////////////////////
	//
	// TRACK TAGS FOR SECTOR OBJECTS
	//
	//////////////////////////////////////////////////////////////////////////////////////////
	// Tracks allow SOs and sprites to follow a path designated by the map-maker.
	// All Tracks must have at a minimum:
//	      1. A TRACK_START.
//	      2. A TRACK_END.
	//
	// Tracks are set up by the program by connecting sprites placed by the
	// map-maker in the BUILD
	// editor. The track sprites start at location 1900 and end at 1999 so there are
	// 100 available
	// tracks. Tracks automatically loop back to the beginning when the TRACK_END
	// sprite is
	// detected. Track tags listed below can be set to modify the behavior of
	// objects moving along
	// them.
	//
	// Look at the tracks in example maps to see how they are placed.
	//
	// NOTE: Track direction is dependent on the next closest sprite to the
	// TRACK_START sprite.
	// As noted below, certain track tags are dependant on the direction the object
	// is traveling on
	// on the track.
	//
	// For placing actor sprite on the tracks:
//	      1. Put a sprite down near the point you want it to start from.
//	      2. Adjust the angle to the left to make the sprite move in the "reverse" direction
//	         and to the right to make the sprite move in the "forward" direction.
//	      3. For the pic, place the first pic of the actor found in the editart file.
	//
	// Every track can have a type set in the high tag of TRACK_START.
	//////////////////////////////////////////////////////////////////////////////////////////

	// Mark first track sprite with this
	// High tag = Type of track (defined next)
	public static final int TRACK_START = 700;

	//
	// TRACK TYPES - only valid for sprites only NOT for SOs
	//
	// Used to attach a name to a track purpose. The actors will "look around" for
	// tracks to know what actions are available to them.
	//

	// All tracks are assumed to be SEGMENTS (non-circular, generally leading in a
	// certain
	// direction) unless otherwise noted. SEGMENTS are generally kept short with a
	// few
	// exceptions.

	// Follow a circular route available for sprites to hop on and off.
	// Generally covers a larger area than TT_WANDER.
	public static final int TT_ROUTE = 1;

	// Jump up only
	public static final int TT_JUMP_UP = 2;
	// Jump down only
	public static final int TT_JUMP_DOWN = 3;
	// Jump up/down track
	public static final int TT_JUMP_BOTH = 4;
	// Ladder track. Currently only good for going up. Must jump down.
	public static final int TT_LADDER = 5;
	// Stair track. Hard for sprites to maneuver narrow stairs well without tracks.
	public static final int TT_STAIRS = 6;

	// Traverse a complex route - generally thought of to move you from point A to
	// point B
	// in complex map situations.
	public static final int TT_TRAVERSE = 7;

	// Duck behind cover and attack
	public static final int TT_DUCK_N_SHOOT = 8;
	// Hide behind cover and attack
	public static final int TT_HIDE_N_SHOOT = 9;

	// Exit tracks to exit a room/area. Probably should lead to a door alot of the
	// time.
	public static final int TT_EXIT = 10;

	// Wander track. Wander about general area until you come upon a player.
	public static final int TT_WANDER = 11;

	// Scan for other tracks. Generally a VERY short track put these in places where
	// other tracks can be seen easily. Good "vantage points".
	public static final int TT_SCAN = 12;

	// Super Jump
	public static final int TT_SUPER_JUMP_UP = 13;

	// Operate Stuff
	public static final int TT_OPERATE = 14;

	// Mark last sprite with this
	public static final int TRACK_END = 701;
	// Set the target speed and actual speed at this point
	// Valid values for target speed are 2 to 128
	public static final int TRACK_SET_SPEED = 702;
	// Stop for (seconds) in high tag
	public static final int TRACK_STOP = 703;
	// Reverse the direction
	public static final int TRACK_REVERSE = 704;

	// Note that the next two tags have the opposite effect when traveling the
	// REVERSE direction

	// Sets up a target speed by a delta (amt) in high tag
	// velocity = (current target velocity) + (speed up amount in high tag)
	// Valid values for target speed are 2 to 128
	public static final int TRACK_SPEED_UP = 705;

	// Sets up a target speed by a delta (amt) in high tag
	// velocity = (current target velocity) - (speed up amount in high tag)
	// Valid values for target speed are 2 to 128
	public static final int TRACK_SLOW_DOWN = 706;

	// The rate at which the velocity approaches the target speed.
	// This is defined as a shift value (for processing speed) and defaults to 6.
	// The valid range
	// is 1 to 12.
	public static final int TRACK_VEL_RATE = 707;

	// Moves the floor of the object by a delta PIXEL position (amt) in high tag
	public static final int TRACK_ZUP = 709;
	// Moves the floor of the object by a delta PIXEL position (amt) in high tag
	public static final int TRACK_ZDOWN = 710;
	// Sets the rate at which the ZUP/ZDOWN moves. Defaults to 256.
	public static final int TRACK_ZRATE = 711;
	// Special mode where the object looks at the zcoord of the next track point and
	// moves
	// to achieve the destination. In high tag is a negative delta Z from the
	// placement of
	// the track sprites z value.
	public static final int TRACK_ZDIFF_MODE = 712;

	// Object spins as it goes along its track. Spin speed in high tag.
	// For now, once you start it spinning you cannot stop it.
	public static final int TRACK_SPIN = 715;

	// Object stops spining. Angle to finish at in high tag.
	public static final int TRACK_SPIN_STOP = 716;

	// Bobbing for SO's.
	// High tag = Bob amt in pixels.
	public static final int TRACK_BOB_START = 717;
	// High tag = Bob speed - shift amt pixels.
	public static final int TRACK_BOB_SPEED = 718;
	// Bobbing for SO's.
	public static final int TRACK_BOB_STOP = 719;
	// Start object spinning in the opposite direction.
	public static final int TRACK_SPIN_REVERSE = 720;

	// Start object sinking to ST1 SECT_SO_SINK_DEST
	// For boats
	// High tag = speed of sinking
	public static final int TRACK_SO_SINK = 723;

	// For boats - lower whirlpool sector - tagged with ST1 SECT_SO_FORM_WHIRLPOOL
	public static final int TRACK_SO_FORM_WHIRLPOOL = 724;

	// Move Sprite Objects (not sector objects) straight up or down in the z
	// direction to the next points z height then continues along the track
	// High tag = speed of movement - default is 256
	public static final int TRACK_MOVE_VERTICAL = 725;

	// Object will wait at this point on the track for a trigger/switch to be
	// flipped. Use TAG_SO_EVENT_SWITCH and TRIGGER to free the SO to
	// continue. In addition the these switches and triggers, others that have
	// have MATCHing tags will work with this tag. The following is a list.
	//
	// TAG_SPRITE_HIT_MATCH
	// TAG_OPEN_DOOR_SWITCH
	// TAG_OPEN_DOOR_TRIGGER
	//
	// Basically anthing that has a match tag that operates on sectors can also
	// operate a TRACK_WAIT_FOR_EVENT.
	//

	// High tag = match number
	public static final int TRACK_WAIT_FOR_EVENT = 726;

	// does a DoMatchEverything
	// TAG1 = match tag
	public static final int TRACK_MATCH_EVERYTHING = 728;
	public static final int TRACK_MATCH_EVERYTHING_ONCE = 729;

	///////////////
	//
	// TRACK TAGS FOR SPRITES ONLY
	//
	///////////////

	// Set the target speed and actual speed at this point
	// Valid values for target speed are 2 to 128
	public static final int TRACK_ACTOR_SET_SPEED = 750;
	// Stop for (seconds) in high tag
	public static final int TRACK_ACTOR_STOP = 751;
	// Reverse the direction
	public static final int TRACK_ACTOR_REVERSE = 752;

	// Note that the next two tags have the opposite effect when traveling the
	// REVERSE direction

	// Sets up a target speed by a delta (amt) in high tag
	// velocity = (current target velocity) + (speed up amount in high tag)
	// Valid values for target speed are 2 to 128
	public static final int TRACK_ACTOR_SPEED_UP = 753;

	// Sets up a target speed by a delta (amt) in high tag
	// velocity = (current target velocity) - (speed up amount in high tag)
	// Valid values for target speed are 2 to 128
	public static final int TRACK_ACTOR_SLOW_DOWN = 754;

	// The rate at which the velocity approaches the target speed.
	// This is defined as a shift value (for processing speed) and defaults to 6.
	// The valid range
	// is 1 to 12.
	public static final int TRACK_ACTOR_VEL_RATE = 755;

	// Special mode where the object looks at the zcoord of the next track point and
	// moves
	// to achieve the destination. Should not be set when jumping/climbing etc.
	public static final int TRACK_ACTOR_ZDIFF_MODE = 759;

	// Note: All actions are preformed only if they exist for the character.

	// High tag = seconds
	public static final int TRACK_ACTOR_STAND = 770;
	// High tag = height value (default 384)
	public static final int TRACK_ACTOR_JUMP = 771;
	// Toggle crawl state
	public static final int TRACK_ACTOR_CRAWL = 772;
	// Toggle swim state
	public static final int TRACK_ACTOR_SWIM = 773;
	// Toggle fly spell
	public static final int TRACK_ACTOR_FLY = 774;
	// High tag = seconds
	public static final int TRACK_ACTOR_SIT = 776;
	// High tag = seconds
	public static final int TRACK_ACTOR_DEATH1 = 777;
	// High tag = seconds
	public static final int TRACK_ACTOR_DEATH2 = 778;
	// Air Death!
	// High tag = seconds
	public static final int TRACK_ACTOR_DEATH_JUMP = 779;

	// Close range attacks - in order of least powerful to most
	// High tag = seconds
	public static final int TRACK_ACTOR_CLOSE_ATTACK1 = 780;
	public static final int TRACK_ACTOR_CLOSE_ATTACK2 = 781;
	// Long range attacks - in order of least powerful to most
	// High tag = seconds
	public static final int TRACK_ACTOR_ATTACK1 = 782;
	public static final int TRACK_ACTOR_ATTACK2 = 783;
	public static final int TRACK_ACTOR_ATTACK3 = 784;
	public static final int TRACK_ACTOR_ATTACK4 = 785;
	public static final int TRACK_ACTOR_ATTACK5 = 786;
	public static final int TRACK_ACTOR_ATTACK6 = 787;

	// High tag = seconds
	public static final int TRACK_ACTOR_LOOK = 790;
	// High tag = seconds to pause
	// Point of the sprite angle in the direction of the operatable
	// sector/wall/switch
	// Actor presses the space bar to operate sector/wall/switch.
	public static final int TRACK_ACTOR_OPERATE = 791;
	// High tag = height to go up before jumping to next point
	// Sprite angle must be facing the ladder
	public static final int TRACK_ACTOR_CLIMB_LADDER = 792;
	// Set up a default jump value - for use before climbing ladders etc
	public static final int TRACK_ACTOR_SET_JUMP = 793;

	// Specail Action - depends on each actor
	// High tag = seconds
	public static final int TRACK_ACTOR_SPECIAL1 = 795;
	public static final int TRACK_ACTOR_SPECIAL2 = 796;

	// Jump if moving forward on track
	public static final int TRACK_ACTOR_JUMP_IF_FORWARD = 797;
	// Jump if moving backward on track
	public static final int TRACK_ACTOR_JUMP_IF_REVERSE = 798;

	// Wait for player to come into range before moving from this point.
	// High tag = Distance from player at which NPC can start moving.
	public static final int TRACK_ACTOR_WAIT_FOR_PLAYER = 799;

	// Wait for trigger to be tripped before moving from this point.
	// Use TAG_TRIGGER_ACTORS defined above is used to trigger the actor.
	public static final int TRACK_ACTOR_WAIT_FOR_TRIGGER = 800;

	// Quick TAGS - Used internally by ME ONLY
	public static final int TRACK_ACTOR_QUICK_JUMP = 801;
	public static final int TRACK_ACTOR_QUICK_JUMP_DOWN = 802;
	public static final int TRACK_ACTOR_QUICK_SUPER_JUMP = 803;
	public static final int TRACK_ACTOR_QUICK_SCAN = 804;
	public static final int TRACK_ACTOR_QUICK_EXIT = 805;
	public static final int TRACK_ACTOR_QUICK_LADDER = 806;
	public static final int TRACK_ACTOR_QUICK_OPERATE = 807;
	public static final int TRACK_ACTOR_QUICK_DUCK = 808;
	public static final int TRACK_ACTOR_QUICK_DEFEND = 809;

	//////////////////////////////////////////////////////////////////////////////////////////
	//
	// SPRITE TAGS
	//
	//////////////////////////////////////////////////////////////////////////////////////////

	// 100-199 are reserved for placing actors on tracks
	public static final int TAG_ACTOR_TRACK_BEGIN = 30000;
	public static final int TAG_ACTOR_TRACK_END = 30099;

	// When "operated" moves grating in the direction the sprite is pointing
	// High tag = distance to move the grate - 1024 is a good distance
	public static final int TAG_SPRITE_GRATING = 200;

	// Place an actor with this tag and it will not spawn until it is triggered.
	// High tag = Match number
	public static final int TAG_SPAWN_ACTOR = 203;
	public static final int TAG_SPRITE_HIT_MATCH = 257;

	//////////////////////////////////////////////////////////////////////////////////////////
	//
	// WALL TAGS
	//
	//////////////////////////////////////////////////////////////////////////////////////////

	// Sine wave wall effect - max 5 per level
	//
	// EXP - Tag the first wall with 300, last with 302.
//	       Use point2 to see which direction the wall goes.
	//
	// 1st Sector High Tag = range
	// 2nd Sector High Tag = speed
	// 3th Sector High Tag = a distance from one peak (top of curve) to the next

	// Sine Wave wall in y direction
	public static final int TAG_WALL_SINE_Y_BEGIN = 300;
	// Sine Wave wall in x direction
	public static final int TAG_WALL_SINE_X_BEGIN = 301;
	public static final int TAG_WALL_SINE_Y_END = 302;
	public static final int TAG_WALL_SINE_X_END = 303;

	// Switch to rotate a SO 90 degrees when pressed.
	// High tag = SO number to rotate.
	public static final int TAG_ROTATE_SO_SWITCH = 304;

	// Climbable wall that has a top.
	public static final int TAG_WALL_CLIMB = 305;

	// Smashable walls
	// High Tag is SPAWN_SPOT match tag
	public static final int TAG_WALL_BREAK = 307;

	// For SO's that rotate - tag ONE wall of the loop to make loop NOT rotate with
	// the rest of the
	// SO. Does not matter if you tag the inside or outside wall.
	// Exp - drill bit
	public static final int TAG_WALL_LOOP_DONT_SPIN = 500;
	// Reverse spin for this wall loop and sector. Tag same as DONT_SPIN.
	// Exp - pit with teath
	public static final int TAG_WALL_LOOP_REVERSE_SPIN = 501;
	// Spin twice as fast as SO. Tag same as DONT_SPIN.
	// Exp - whirlpool
	public static final int TAG_WALL_LOOP_SPIN_2X = 502;
	// Spin 4X as fast as SO. Tag same as DONT_SPIN.
	// Exp - whirlpool
	public static final int TAG_WALL_LOOP_SPIN_4X = 503;
	// Tag the outer loop of a SO with this.
	public static final int TAG_WALL_LOOP_OUTER = 504;
	// Just tag one wall so it does not move. My attempt to fix sector splitting for
	// the
	// SO outer loop. Not working real well.
	public static final int TAG_WALL_DONT_MOVE = 505;

	// Just tag one wall of closed loop teleporter.
	// High byte is speed - shift value 0-6
	public static final int TAG_WALL_LOOP_TELEPORTER_PAN = 506;

	// Just tag one wall in loop so it does not scale.
	public static final int TAG_WALL_LOOP_DONT_SCALE = 507;

	// Tag the secondary outer loop of a SO with this. Use with Rectangle Clipping
	// and Stacked sectors.
	public static final int TAG_WALL_LOOP_OUTER_SECONDARY = 508;

	public static final int TAG_WALL_ALIGN_SLOPE_TO_POINT = 550;

}
