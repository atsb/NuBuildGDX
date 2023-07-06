package ru.m210projects.Wang;

import static ru.m210projects.Wang.Main.*;
import static ru.m210projects.Wang.Player.*;
import static ru.m210projects.Wang.Ai.*;
import static ru.m210projects.Build.Engine.*;
import static ru.m210projects.Build.Gameutils.*;
import static ru.m210projects.Build.Pragmas.*;
import static ru.m210projects.Wang.Type.MyTypes.*;

import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Wang.Type.PlayerStr;
import ru.m210projects.Wang.Type.USER;

public class Gameutils {

//	TRAVERSE_SPRITE_SECT(l, o, n)    for ((o) = (l); (n) = nextspritesect[o], (o) != -1; (o) = (n))   for (i = headspritesect[sectnum]; i != -1; nexti = nextspritesect[i], i = nexti)
//	TRAVERSE_SPRITE_STAT(l, o, n)    for (o = l; o != -1; n = nextspritestat[o], o = n);  for (i = headspritestat[stat]; i != -1; nexti = nextspritestat[i], i = nexti)
//	TRAVERSE_CONNECT(i)   for (i = connecthead; i != -1; i = connectpoint2[i])
//	for (pnum = connecthead; pnum != -1; pnum = connectpoint2[pnum])

	// weapons takes up 4 bits
	public static final int SK_WEAPON_BIT0 = 0;
	public static final int SK_WEAPON_BIT1 = 1;
	public static final int SK_WEAPON_BIT2 = 2;
	public static final int SK_WEAPON_BIT3 = 3;
	public static final int SK_WEAPON_MASK = (BIT(SK_WEAPON_BIT0) | BIT(SK_WEAPON_BIT1) | BIT(SK_WEAPON_BIT2)
			| BIT(SK_WEAPON_BIT3));

	public static final int SK_INV_HOTKEY_BIT0 = 4;
	public static final int SK_INV_HOTKEY_BIT1 = 5;
	public static final int SK_INV_HOTKEY_BIT2 = 6;
	public static final int SK_INV_HOTKEY_MASK = (BIT(SK_INV_HOTKEY_BIT0) | BIT(SK_INV_HOTKEY_BIT1)
			| BIT(SK_INV_HOTKEY_BIT2));

	public static final int SK_AUTO_AIM = 7;
	public static final int SK_CENTER_VIEW = 8;
	public static final int SK_PAUSE = 9;
	public static final int SK_RUN_LOCK = 10;

	public static final int SK_MESSAGE = 11;
	public static final int SK_LOOK_UP = 12;
	public static final int SK_LOOK_DOWN = 13;
	public static final int SK_CRAWL_LOCK = 14;
	public static final int SK_FLY = 15;

	public static final int SK_RUN = 16;
	public static final int SK_SHOOT = 17;
	public static final int SK_OPERATE = 18;
	public static final int SK_JUMP = 19;
	public static final int SK_CRAWL = 20;
	public static final int SK_SNAP_UP = 21;
	public static final int SK_SNAP_DOWN = 22;

	public static final int SK_TILT_LEFT = 23;
	public static final int SK_TILT_RIGHT = 24;

	public static final int SK_TURN_180 = 25;

	public static final int SK_INV_LEFT = 26;
	public static final int SK_INV_RIGHT = 27;

	public static final int SK_QUIT_GAME = 28;

	public static final int SK_INV_USE = 29;
	public static final int SK_HIDE_WEAPON = 30;
	public static final int SK_SPACE_BAR = 31;

	//
	// Defines
	//

	public static final int WPN_FIST = 0;
	public static final int WPN_STAR = 1;
	public static final int WPN_SHOTGUN = 2;
	public static final int WPN_UZI = 3;
	public static final int WPN_MICRO = 4;
	public static final int WPN_GRENADE = 5;
	public static final int WPN_MINE = 6;
	public static final int WPN_RAIL = 7;
	public static final int WPN_HOTHEAD = 8;
	public static final int WPN_HEART = 9;
	public static final int WPN_NAPALM = 10;
	public static final int WPN_RING = 11;
	public static final int WPN_ROCKET = 12;
	public static final int WPN_SWORD = 13;

	public static final int DMG_NAPALM = 14;
	public static final int DMG_MIRV_METEOR = 15;
	public static final int DMG_SERP_METEOR = 16;

	// radius damage
	public static final int DMG_ELECTRO_SHARD = 17;
	public static final int DMG_SECTOR_EXP = 18;
	public static final int DMG_BOLT_EXP = 19;
	public static final int DMG_TANK_SHELL_EXP = 20;
	public static final int DMG_FIREBALL_EXP = 21;
	public static final int DMG_NAPALM_EXP = 22;
	public static final int DMG_SKULL_EXP = 23;
	public static final int DMG_BASIC_EXP = 24;
	public static final int DMG_GRENADE_EXP = 25;
	public static final int DMG_MINE_EXP = 26;
	public static final int DMG_MINE_SHRAP = 27;
	public static final int DMG_MICRO_EXP = 28;
	public static final int DMG_NUCLEAR_EXP = 29;
	public static final int DMG_RADIATION_CLOUD = 30;
	public static final int DMG_FLASHBOMB = 31;
	public static final int DMG_FIREBALL_FLAMES = 32;
	public static final int DMG_RIPPER_SLASH = 33;
	public static final int DMG_SKEL_SLASH = 34;
	public static final int DMG_COOLG_BASH = 35;
	public static final int DMG_COOLG_FIRE = 36;
	public static final int DMG_GORO_CHOP = 37;
	public static final int DMG_GORO_FIREBALL = 38;
	public static final int DMG_SERP_SLASH = 39;
	public static final int DMG_LAVA_BOULDER = 40;
	public static final int DMG_LAVA_SHARD = 41;
	public static final int DMG_HORNET_STING = 42;
	public static final int DMG_EEL_ELECTRO = 43;
	public static final int DMG_SPEAR_TRAP = 44;
	public static final int DMG_VOMIT = 45;
	public static final int DMG_BLADE = 46;

	public static USER[] pUser = new USER[MAXSPRITES];

	public static final int CIRCLE_CAMERA_DIST_MIN = 12000;

	// dist at which actors will not move (unless shot?? to do)
	public static final int MAX_ACTIVE_RANGE = 42000;
	// dist at which actors roam about on their own
	public static final int MIN_ACTIVE_RANGE = 20000;

	// Defines for reading in ST1 sprite tagging
	public static final int SP_TAG1(SPRITE sp) {
		return sp.hitag;
	}

	public static final int SP_TAG2(SPRITE sp) {
		return sp.lotag;
	}

	public static final int SP_TAG3(SPRITE sp) {
		return sp.clipdist;
	}

	public static final int SP_TAG4(SPRITE sp) {
		return sp.ang;
	}

	public static final int SP_TAG5(SPRITE sp) {
		return sp.xvel;
	}

	public static final int SP_TAG6(SPRITE sp) {
		return sp.yvel;
	}

	public static final int SP_TAG7(SPRITE sp) {
		return (sp.zvel >> 8) & 0xFF;
	}

	public static final void SET_SP_TAG7(SPRITE sp, int var) {
		sp.zvel = (short) (SP_TAG8(sp) | ((var & 0xFF) << 8));
	}

	public static final int SP_TAG8(SPRITE sp) {
		return sp.zvel & 0xFF;
	}

	public static final void SET_SP_TAG8(SPRITE sp, int var) {
		sp.zvel = (short) ((var & 0xFF) | (SP_TAG7(sp) << 8));
	}

	public static final int SP_TAG9(SPRITE sp) {
		return (sp.owner >> 8) & 0xFF;
	}

	public static final void SET_SP_TAG9(SPRITE sp, int var) {
		sp.owner = (short) (SP_TAG10(sp) | ((var & 0xFF) << 8));
	}

	public static final int SP_TAG10(SPRITE sp) {
		return sp.owner & 0xFF;
	}

	public static final void SET_SP_TAG10(SPRITE sp, int var) {
		sp.owner = (short) ((var & 0xFF) | (SP_TAG9(sp) << 8));
	}

	public static final int SP_TAG11(SPRITE sp) {
		return sp.shade;
	}

	public static final int SP_TAG12(SPRITE sp) {
		return sp.pal;
	}

	public static final int SP_TAG13(SPRITE sp) {
		return (sp.xoffset & 0xFF) | ((sp.yoffset & 0xFF) << 8);
	}

	public static final void SET_SP_TAG13(SPRITE sp, int value) {
		sp.xoffset = (short) ( ( value >>> 0 ) & 0xFF );
		sp.yoffset = (short) ( ( value >>> 8 ) & 0xFF );
	}

	public static final int SP_TAG14(SPRITE sp) {
		return (sp.xrepeat & 0xFF) | ((sp.yrepeat & 0xFF) << 8);
	}

	public static final void SET_SP_TAG14(SPRITE sp, int value) {
		sp.xrepeat = (short) ( ( value >>> 0 ) & 0xFF );
		sp.yrepeat = (short) ( ( value >>> 8 ) & 0xFF );
	}

	public static final int SP_TAG15(SPRITE sp) {
		return sp.z;
	}

	public static final int SPRITE_TAG1(int sp) {
		return sprite[sp].hitag;
	}

	public static final int SPRITE_TAG2(int sp) {
		return sprite[sp].lotag;
	}

	public static final int SPRITE_TAG3(int sp) {
		return sprite[sp].clipdist;
	}

	public static final int SPRITE_TAG4(int sp) {
		return sprite[sp].ang;
	}

	public static final int SPRITE_TAG5(int sp) {
		return sprite[sp].xvel;
	}

	public static final int SPRITE_TAG6(int sp) {
		return sprite[sp].yvel;
	}

	// this will get you the other wall moved by dragpoint
	public static final int DRAG_WALL(int w) {
		return (wall[wall[(w)].nextwall].point2);
	}

	// OVER and UNDER water macros
	public static final boolean SpriteInDiveArea(SPRITE sp) {
		return !isValidSector((sp).sectnum) ? false : TEST(sector[(sp).sectnum].extra, SECTFX_DIVE_AREA);
	}

	public static final boolean SpriteInUnderwaterArea(SPRITE sp) {
		return !isValidSector((sp).sectnum) ? false : TEST(sector[(sp).sectnum].extra, SECTFX_UNDERWATER | SECTFX_UNDERWATER2);
	}

	public static final boolean SectorIsDiveArea(int sect) {
		return !isValidSector(sect) ? false : TEST(sector[sect].extra, SECTFX_DIVE_AREA);
	}

	public static final boolean SectorIsUnderwaterArea(int sect) {
		return !isValidSector(sect) ? false : TEST(sector[sect].extra, SECTFX_UNDERWATER | SECTFX_UNDERWATER2);
	}

	// Key Press Flags macros

	public static final boolean FLAG_KEY_PRESSED(PlayerStr pp, int sync_key) {
		return TEST(pp.KeyPressFlags, 1 << sync_key);
	}

	public static final boolean FLAG_KEY_RELEASE(PlayerStr pp, int sync_key) {
		return (pp.KeyPressFlags &= ~(1 << sync_key)) != 0;
	}

	public static final int FLAG_KEY_RESET(PlayerStr pp, int sync_key) {
		return pp.KeyPressFlags |= (1 << sync_key);
	}

	public static final boolean TEST_SYNC_KEY(PlayerStr player, int sync_num) {
		return TEST(player.input.bits, (1 << (sync_num)));
	}

	public static final int RESET_SYNC_KEY(PlayerStr player, int sync_num) {
		return player.input.bits &= ~((1 << (sync_num)));
	}

	public static final short NORM_ANGLE(int ang) {
		return (short) ((ang) & 2047);
	}

	public static final int ANGLE_2_PLAYER(PlayerStr pp, int x, int y) {
		return (NORM_ANGLE(engine.getangle(pp.posx - (x), pp.posy - (y))));
	}

	public static final int STD_RANDOM_P2(int pwr_of_2) {
		return (MOD_P2(engine.rand(), (pwr_of_2)));
	}

	public static final int STD_RANDOM_RANGE(int range) {
		return (StdRandomRange(range));
	}

	public static final int STD_RANDOM() {
		return (engine.rand());
	}

	public static final int MOVEx(int vel, int ang) {
		return (((vel) * sintable[NORM_ANGLE((ang) + 512)]) >> 14);
	}

	public static final int MOVEy(int vel, int ang) {
		return (((vel) * sintable[NORM_ANGLE((ang))]) >> 14);
	}

	public static final int DIST(int x1, int y1, int x2, int y2) {
		return engine.ksqrt(SQ((x1) - (x2)) + SQ((y1) - (y2)));
	}

	public static final int PIC_SIZX(int sn) {
		return engine.getTile(sprite[sn].picnum).getWidth();
	}

	public static final int PIC_SIZY(int sn) {
		return engine.getTile(sprite[sn].picnum).getHeight();
	}

	// Distance macro - tx, ty, tmin are holding vars that must be declared in the
	// routine
	// that uses this macro
	public static int dist_x, dist_y, dist_min;

	public static final int DISTANCE(int x1, int y1, int x2, int y2) {
		dist_x = klabs(x2 - x1);
		dist_y = klabs(y2 - y1);
		dist_min = Math.min(dist_x, dist_y);
		return dist_x + dist_y - DIV2(dist_min);
	}

	public static final int SPRITE_SIZE_X(int sp_num) {
		return ((sprite[sp_num].xrepeat == 64) ? engine.getTile(sprite[sp_num].picnum).getWidth()
				: ((sprite[sp_num].xrepeat * engine.getTile(sprite[sp_num].picnum).getWidth()) >> 6));
	}

	public static final int SPRITE_SIZE_Y(int sp_num) {
		return ((sprite[sp_num].yrepeat == 64) ? engine.getTile(sprite[sp_num].picnum).getHeight()
				: ((sprite[sp_num].yrepeat * engine.getTile(sprite[sp_num].picnum).getHeight()) >> 6));
	}

	public static final int SPRITE_SIZE_Z(int sp_num) {
		return ((sprite[sp_num].yrepeat == 64) ? Z(engine.getTile(sprite[sp_num].picnum).getHeight())
				: ((sprite[sp_num].yrepeat * engine.getTile(sprite[sp_num].picnum).getHeight()) << 2));
	}

	public static final int SPRITEp_SIZE_X(SPRITE sp) {
		return (((sp).xrepeat == 64) ? engine.getTile(sp.picnum).getWidth() : (((sp).xrepeat * engine.getTile(sp.picnum).getWidth()) >> 6));
	}

	public static final int SPRITEp_SIZE_Y(SPRITE sp) {
		return (((sp).yrepeat == 64) ? engine.getTile(sp.picnum).getHeight() : (((sp).yrepeat * engine.getTile(sp.picnum).getHeight()) >> 6));
	}

	public static final int SPRITEp_SIZE_Z(SPRITE sp) {
		return (((sp).yrepeat == 64) ? Z(engine.getTile(sp.picnum).getHeight()) : (((sp).yrepeat * engine.getTile(sp.picnum).getHeight()) << 2));
	}

	// Given a z height and sprite return the correct x repeat value
	public static final int SPRITEp_SIZE_X_2_XREPEAT(SPRITE sp, int x) {
		return (((x) * 64) / engine.getTile(sp.picnum).getWidth());
	}

	// Given a z height and sprite return the correct y repeat value
	public static final int SPRITEp_SIZE_Z_2_YREPEAT(SPRITE sp, int zh) {
		return ((zh) / (4 * engine.getTile(sp.picnum).getHeight()));
	}

	public static final int SPRITEp_SIZE_Y_2_YREPEAT(SPRITE sp, int y) {
		return (((y) * 64) / engine.getTile(sp.picnum).getHeight());
	}

	// x & y offset of tile
	public static final int TILE_XOFF(int picnum) {
		return engine.getTile(picnum).getOffsetX();
	}

	public static final int TILE_YOFF(int picnum) {
		return engine.getTile(picnum).getOffsetY();
	}

	// x & y offset of current sprite tile
	public static final int SPRITEp_XOFF(SPRITE sp) {
		return engine.getTile(sp.picnum).getOffsetX();
	}

	public static final int SPRITEp_YOFF(SPRITE sp) {
		return engine.getTile(sp.picnum).getOffsetY();
	}

	// Z size of top (TOS) and bottom (BOS) part of sprite
	public static final int SPRITEp_SIZE_TOS(SPRITE sp) {
		return (DIV2(SPRITEp_SIZE_Z(sp)) + Z(SPRITEp_YOFF(sp)));
	}

	public static final int SPRITEp_SIZE_BOS(SPRITE sp) {
		return (DIV2(SPRITEp_SIZE_Z(sp)) - Z(SPRITEp_YOFF(sp)));
	}

	// acual Z for TOS and BOS - handles both WYSIWYG and old style
	public static final int SPRITEp_TOS(SPRITE sp) {
		return (TEST((sp).cstat, CSTAT_SPRITE_YCENTER) ? ((sp).z - SPRITEp_SIZE_TOS(sp))
				: ((sp).z - SPRITEp_SIZE_Z(sp)));
	}

	public static final int SPRITEp_BOS(SPRITE sp) {
		return (TEST((sp).cstat, CSTAT_SPRITE_YCENTER) ? ((sp).z + SPRITEp_SIZE_BOS(sp)) : (sp).z);
	}

	// mid and upper/lower sprite caluculations
	public static final int SPRITEp_MID(SPRITE sp) {
		return (DIV2(SPRITEp_TOS(sp) + SPRITEp_BOS(sp)));
	}

	public static final int SPRITEp_UPPER(SPRITE sp) {
		return (SPRITEp_TOS(sp) + DIV4(SPRITEp_SIZE_Z(sp)));
	}

	public static final int SPRITEp_LOWER(SPRITE sp) {
		return (SPRITEp_BOS(sp) - DIV4(SPRITEp_SIZE_Z(sp)));
	}

	public static final int Z(int value) {
		return (value << 8);
	}

	public static final int PIXZ(int value) {
		return (value >> 8);
	}

	public static final int SQ(int val) {
		return ((val) * (val));
	}

	public static final boolean KENFACING_PLAYER(PlayerStr pp, SPRITE sp) {
		return (sintable[NORM_ANGLE(sp.ang + 512)] * (pp.posy - sp.y) >= sintable[NORM_ANGLE(sp.ang)]
				* (pp.posx - sp.x));
	}

	public static final boolean FACING_PLAYER(PlayerStr pp, SPRITE sp) {
		return (klabs(
				GetDeltaAngle((sp).ang, NORM_ANGLE(engine.getangle((pp).posx - (sp).x, (pp).posy - (sp).y)))) < 512);
	}

	public static final boolean PLAYER_FACING(PlayerStr pp, SPRITE sp) {
		return (klabs(
				GetDeltaAngle(pp.getAnglei(), NORM_ANGLE(engine.getangle((sp).x - (pp).posx, (sp).y - (pp).posy)))) < 320);
	}

	public static final boolean FACING(SPRITE sp1, SPRITE sp2) {
		return (klabs(
				GetDeltaAngle((sp2).ang, NORM_ANGLE(engine.getangle((sp1).x - (sp2).x, (sp1).y - (sp2).y)))) < 512);
	}

	public static final boolean FACING_PLAYER_RANGE(PlayerStr pp, SPRITE sp, int range) {
		return (klabs(GetDeltaAngle((sp).ang,
				NORM_ANGLE(engine.getangle((pp).posx - (sp).x, (pp).posy - (sp).y)))) < (range));
	}

	public static final boolean PLAYER_FACING_RANGE(PlayerStr pp, SPRITE sp, int range) {
		return (klabs(GetDeltaAngle(pp.getAnglei(),
				NORM_ANGLE(engine.getangle((sp).x - (pp).posx, (sp).y - (pp).posy)))) < (range));
	}

	public static final boolean FACING_RANGE(SPRITE sp1, SPRITE sp2, int range) {
		return (klabs(
				GetDeltaAngle((sp2).ang, NORM_ANGLE(engine.getangle((sp1).x - (sp2).x, (sp1).y - (sp2).y)))) < (range));
	}

	// two vectors
	// can determin direction
	public static final int DOT_PRODUCT_2D(int x1, int y1, int x2, int y2) {
		return (mulscale((x1), (x2), 16) + mulscale((y1), (y2), 16));
	}

	public static final int DOT_PRODUCT_3D(int x1, int y1, int z1, int x2, int y2, int z2) {
		return (mulscale((x1), (x2), 16) + mulscale((y1), (y2), 16) + mulscale((z1), (z2), 16));
	}

	// just determine if the player is moving
	public static final int PLAYER_MOVING(PlayerStr pp) {
		return ((pp).xvect | (pp).yvect);
	}

	public static final boolean TEST_GOTSECTOR(int sect_num) {
		return (TEST(gotsector[(sect_num) >> 3], 1 << ((sect_num) & 7)));
	}

	public static final int RESET_GOTSECTOR(int sect_num) {
		return (gotsector[(sect_num) >> 3] &= ~(1 << ((sect_num) & 7)));
	}

	public static final int SET_GOTSECTOR(int sect_num) {
		return (gotsector[(sect_num) >> 3] |= (1 << ((sect_num) & 7)));
	}

	public static final boolean TEST_GOTPIC(int tile_num) {
		return tile_num == -1 ? false : (TEST(gotpic[(tile_num) >> 3], 1 << ((tile_num) & 7)));
	}

	public static final int RESET_GOTPIC(int tile_num) {
		return (gotpic[(tile_num) >> 3] &= ~(1 << ((tile_num) & 7)));
	}

	public static final int SET_GOTPIC(int tile_num) {
		return (gotpic[(tile_num) >> 3] |= (1 << ((tile_num) & 7)));
	}

	public static final int LOW_TAG(int sectnum) {
		return isValidSector(sectnum) ? (sector[sectnum].lotag) : 0;
	}

	public static final int HIGH_TAG(int sectnum) {
		return isValidSector(sectnum) ? (sector[sectnum].hitag) : 0;
	}

	public static final int LOW_TAG_SPRITE(int spnum) {
		return isValidSprite(spnum) ? (sprite[(spnum)].lotag) : 0;
	}

	public static final int HIGH_TAG_SPRITE(int spnum) {
		return isValidSprite(spnum) ? (sprite[(spnum)].hitag) : 0;
	}

	public static final int LOW_TAG_WALL(int wallnum) {
		return isValidWall(wallnum) ? (wall[(wallnum)].lotag) : 0;
	}

	public static final int HIGH_TAG_WALL(int wallnum) {
		return isValidWall(wallnum) ? (wall[(wallnum)].hitag) : 0;
	}

	public static final int SEC(int value) {
		return ((value) * 120);
	}

	public static final int CEILING_DIST = (Z(4));
	public static final int FLOOR_DIST = (Z(4));

	// Attributes for monochrome text
	public static final int MDA_BLANK = 0x00;
	public static final int MDA_NORMAL = 0x07;
	public static final int MDA_BLINK = 0x87;
	public static final int MDA_HIGH = 0x0F;
	public static final int MDA_HIGHBLINK = 0x8F;
	public static final int MDA_UNDER = 0x01;
	public static final int MDA_UNDERBLINK = 0x81;
	public static final int MDA_UNDERHIGH = 0x09;
	public static final int MDA_UNDERHIGHBLINK = 0x89;
	public static final int MDA_REVERSE = 0x70;
	public static final int MDA_REVERSEBLINK = 0xF0;

	// defines for move_sprite return value
	public static final int HIT_MASK = (1 << 13) | (1 << 14) | (1 << 15); // (BIT(13) | BIT(14) | BIT(15));
	public static final int HIT_SPRITE = (1 << 14) | (1 << 15); // (BIT(14) | BIT(15));
	public static final int HIT_WALL = 1 << 15; // BIT(15);
	public static final int HIT_SECTOR = 1 << 14; // BIT(14);
	public static final int HIT_PLAX_WALL = 1 << 13; // BIT(13);

	public static final short NORM_SPRITE(int val) {
		return (short) ((val) & (MAXSPRITES - 1));
	}

	public static final short NORM_WALL(int val) {
		return (short) ((val) & (MAXWALLS - 1));
	}

	public static final short NORM_SECTOR(int val) {
		return (short) ((val) & (MAXSECTORS - 1));
	}

	// overwritesprite flags
	public static final int OVER_SPRITE_MIDDLE = (BIT(0));
	public static final int OVER_SPRITE_VIEW_CLIP = (BIT(1));
	public static final int OVER_SPRITE_TRANSLUCENT = (BIT(2));
	public static final int OVER_SPRITE_XFLIP = (BIT(3));
	public static final int OVER_SPRITE_YFLIP = (BIT(4));

	// rotatesprite flags
	public static final int ROTATE_SPRITE_TRANSLUCENT = (BIT(0));
	public static final int ROTATE_SPRITE_VIEW_CLIP = (BIT(1)); // clip to view
	public static final int ROTATE_SPRITE_YFLIP = (BIT(2));
	public static final int ROTATE_SPRITE_IGNORE_START_MOST = (BIT(3)); // don't clip to startumost
	public static final int ROTATE_SPRITE_SCREEN_CLIP = (BIT(1) | BIT(3));// use window
	public static final int ROTATE_SPRITE_CORNER = (BIT(4)); // place sprite from upper left corner
	public static final int ROTATE_SPRITE_TRANS_FLIP = (BIT(5));
	public static final int ROTATE_SPRITE_NON_MASK = (BIT(6)); // non masked sprites
	public static final int ROTATE_SPRITE_ALL_PAGES = (BIT(7)); // copies to all pages

	public static final int RS_SCALE = BIT(16);

	// system defines for status bits
	public static final int CEILING_STAT_PLAX = BIT(0);
	public static final int CEILING_STAT_SLOPE = BIT(1);
	public static final int CEILING_STAT_SWAPXY = BIT(2);
	public static final int CEILING_STAT_SMOOSH = BIT(3);
	public static final int CEILING_STAT_XFLIP = BIT(4);
	public static final int CEILING_STAT_YFLIP = BIT(5);
	public static final int CEILING_STAT_RELATIVE = BIT(6);
	public static final int CEILING_STAT_TYPE_MASK = (BIT(7) | BIT(8));
	public static final int CEILING_STAT_MASKED = BIT(7);
	public static final int CEILING_STAT_TRANS = BIT(8);
	public static final int CEILING_STAT_TRANS_FLIP = (BIT(7) | BIT(8));
	public static final int CEILING_STAT_FAF_BLOCK_HITSCAN = BIT(15);

	public static final int FLOOR_STAT_PLAX = BIT(0);
	public static final int FLOOR_STAT_SLOPE = BIT(1);
	public static final int FLOOR_STAT_SWAPXY = BIT(2);
	public static final int FLOOR_STAT_SMOOSH = BIT(3);
	public static final int FLOOR_STAT_XFLIP = BIT(4);
	public static final int FLOOR_STAT_YFLIP = BIT(5);
	public static final int FLOOR_STAT_RELATIVE = BIT(6);
	public static final int FLOOR_STAT_TYPE_MASK = (BIT(7) | BIT(8));
	public static final int FLOOR_STAT_MASKED = BIT(7);
	public static final int FLOOR_STAT_TRANS = BIT(8);
	public static final int FLOOR_STAT_TRANS_FLIP = (BIT(7) | BIT(8));
	public static final int FLOOR_STAT_FAF_BLOCK_HITSCAN = BIT(15);

	public static final int CSTAT_WALL_BLOCK = BIT(0);
	public static final int CSTAT_WALL_BOTTOM_SWAP = BIT(1);
	public static final int CSTAT_WALL_ALIGN_BOTTOM = BIT(2);
	public static final int CSTAT_WALL_XFLIP = BIT(3);
	public static final int CSTAT_WALL_MASKED = BIT(4);
	public static final int CSTAT_WALL_1WAY = BIT(5);
	public static final int CSTAT_WALL_BLOCK_HITSCAN = BIT(6);
	public static final int CSTAT_WALL_TRANSLUCENT = BIT(7);
	public static final int CSTAT_WALL_YFLIP = BIT(8);
	public static final int CSTAT_WALL_TRANS_FLIP = BIT(9);
	public static final int CSTAT_WALL_BLOCK_ACTOR = (BIT(14)); // my def
	public static final int CSTAT_WALL_WARP_HITSCAN = (BIT(15)); // my def

	// cstat, bit 0: 1 = Blocking sprite (use with clipmove, getzrange) "B"
//	       bit 1: 1 = 50/50 transluscence, 0 = normal                   "T"
//	       bit 2: 1 = x-flipped, 0 = normal                             "F"
//	       bit 3: 1 = y-flipped, 0 = normal                             "F"
//	       bits 5-4: 00 = FACE sprite (default)                         "R"
//	                 01 = WALL sprite (like masked walls)
//	                 10 = FLOOR sprite (parallel to ceilings&floors)
//	                 11 = SPIN sprite (face sprite that can spin 2draw style - not done yet)
//	       bit 6: 1 = 1-sided sprite, 0 = normal                        "1"
//	       bit 7: 1 = Real centered centering, 0 = foot center          "C"
//	       bit 8: 1 = Blocking sprite (use with hitscan)                "H"
//	       bit 9: reserved
//	       bit 10: reserved
//	       bit 11: reserved
//	       bit 12: reserved
//	       bit 13: reserved
//	       bit 14: reserved
//	       bit 15: 1 = Invisible sprite, 0 = not invisible

	public static final int CSTAT_SPRITE_BLOCK = BIT(0);
	public static final int CSTAT_SPRITE_TRANSLUCENT = BIT(1);
	public static final int CSTAT_SPRITE_XFLIP = BIT(2);
	public static final int CSTAT_SPRITE_YFLIP = BIT(3);
	public static final int CSTAT_SPRITE_WALL = BIT(4);
	public static final int CSTAT_SPRITE_FLOOR = BIT(5);
	public static final int CSTAT_SPRITE_SLAB = (BIT(4) | BIT(5));
	public static final int CSTAT_SPRITE_ONE_SIDE = BIT(6);
	public static final int CSTAT_SPRITE_YCENTER = BIT(7);
	public static final int CSTAT_SPRITE_BLOCK_HITSCAN = BIT(8);
	public static final int CSTAT_SPRITE_TRANS_FLIP = BIT(9);

	public static final int CSTAT_SPRITE_RESTORE = BIT(12); // my def
	public static final int CSTAT_SPRITE_CLOSE_FLOOR = BIT(13); // my def - tells whether a sprite
	// started out close to a ceiling or floor
	public static final int CSTAT_SPRITE_BLOCK_MISSILE = BIT(14); // my def
	public static final int CSTAT_SPRITE_INVISIBLE = BIT(15);

	public static final int CSTAT_SPRITE_BREAKABLE = (CSTAT_SPRITE_BLOCK_HITSCAN | CSTAT_SPRITE_BLOCK_MISSILE);

	// new define more readable defines

	// Clip Sprite adjustment
	public static final int CS(int sprite_bit) {
		return ((sprite_bit) << 16);
	}

	// for players to clip against walls
	public static final int CLIPMASK_PLAYER = (CS(CSTAT_SPRITE_BLOCK) | CSTAT_WALL_BLOCK);

	// for actors to clip against walls
	public static final int CLIPMASK_ACTOR = (CS(CSTAT_SPRITE_BLOCK) | CSTAT_WALL_BLOCK | CSTAT_WALL_BLOCK_ACTOR);

	// for missiles to clip against actors
	public static final int CLIPMASK_MISSILE = (CS(CSTAT_SPRITE_BLOCK_HITSCAN | CSTAT_SPRITE_BLOCK_MISSILE)
			| CSTAT_WALL_BLOCK_HITSCAN);

	public static final int CLIPMASK_WARP_HITSCAN = (CS(CSTAT_SPRITE_BLOCK_HITSCAN) | CSTAT_WALL_BLOCK_HITSCAN
			| CSTAT_WALL_WARP_HITSCAN);

	// break up of picanm[]
	public static final int TILE_ANIM_NUM = (0xF | BIT(4) | BIT(5));
	public static final int TILE_ANIM_TYPE = (BIT(6) | BIT(7));
	public static final int TILE_SPEED = (0xF << 20);

	//
	// Directions
	//

	public static final int DEGREE_45 = 256;
	public static final int DEGREE_90 = 512;

	public static final int ORD_NORTH = 0, ORD_NE = 1, ORD_EAST = 2, ORD_SE = 3, ORD_SOUTH = 4, ORD_SW = 5,
			ORD_WEST = 6, ORD_NW = 7;

	public static final int NORTH = ORD_NORTH * DEGREE_45, NE = ORD_NE * DEGREE_45, EAST = ORD_EAST * DEGREE_45,
			SE = ORD_SE * DEGREE_45, SOUTH = ORD_SOUTH * DEGREE_45, SW = ORD_SW * DEGREE_45,
			WEST = ORD_WEST * DEGREE_45, NW = ORD_NW * DEGREE_45;

	//
	// nextsectorneighborz defines - what a god-awful name!
	//

	public static final int SEARCH_DOWN = 1;
	public static final int SEARCH_UP = -1;
	public static final int SEARCH_FLOOR = 1;
	public static final int SEARCH_CEILING = -1;

	public static final int UP_DIR = -1;
	public static final int DOWN_DIR = 1;

	public static final int OPEN = 0;
	public static final int CLOSE = 1;

	//
	// State Flags
	//

	public static final int SF_TICS_MASK = 0xFFFF;
	public static final int SF_QUICK_CALL = BIT(16);
	public static final int SF_PLAYER_FUNC = BIT(17); // only for players to execute
	public static final int SF_TIC_ADJUST = BIT(18); // use tic adjustment for these frames
	public static final int SF_WALL_STATE = BIT(19); // use for walls instead of sprite

	public static final int MAXSHELLS = 32;
	public static final int MAX_PAIN = 5;
	public static final int MAX_TAUNTAI = 33;
	public static final int MAX_GETSOUNDS = 5;
	public static final int MAX_YELLSOUNDS = 3;

///////////////////////////////////////////////////////////////////////////////////////////
//
//JPlayer
//
///////////////////////////////////////////////////////////////////////////////////////////
	public static final int MESSAGE_LINE = 142; // Used to be 164
	public static final int MAXUSERQUOTES = 6;
	public static final int MAXCONQUOTES = 13;

///////////////////////////////////////////////////////////////////////////////////////////
//
//Weapon
//
///////////////////////////////////////////////////////////////////////////////////////////

	public static final int MAX_WEAPONS_KEYS = 10;
	public static final int MAX_WEAPONS_EXTRA = 4; // extra weapons like the two extra head attacks
	public static final int MAX_WEAPONS = (MAX_WEAPONS_KEYS + MAX_WEAPONS_EXTRA);

//weapons that not missile type sprites
	public static final int WPN_NM_LAVA = (-8);
	public static final int WPN_NM_SECTOR_SQUISH = (-9);

	public static final int MAX_SW_PLAYERS = (8);
	public static final int MAX_LEVELS = 29;
	public static final int PACK = 1;

//FIFO queue to hold values while faketimerhandler is called from within the drawing routing
	public static final int MOVEFIFOSIZ = 256;

	public static final int MAXSYNCBYTES = 16;
// TENSW: on really bad network connections, the sync FIFO queue can overflow if it is the
// same size as the move fifo.
	public static final int SYNCFIFOSIZ = 1024;

// Key stuff
	public static final int NUM_KEYS = 8;

	public static final int PLAYER_DEATH_FLIP = 0, PLAYER_DEATH_CRUMBLE = 1, PLAYER_DEATH_EXPLODE = 2,
			PLAYER_DEATH_RIPPER = 3, PLAYER_DEATH_SQUISH = 4, PLAYER_DEATH_DROWN = 5, MAX_PLAYER_DEATHS = 7;

//
//Player Flags
//

	public static final int PF_DEAD = (BIT(1));
	public static final int PF_JUMPING = (BIT(2));
	public static final int PF_FALLING = (BIT(3));
	public static final int PF_LOCK_CRAWL = (BIT(4));
	public static final int PF_LOCK_HORIZ = (BIT(5));
	public static final int PF_LOOKING = (BIT(6));
	public static final int PF_PLAYER_MOVED = (BIT(7));
	public static final int PF_PLAYER_RIDING = (BIT(8));
	public static final int PF_AUTO_AIM = (BIT(9));
	public static final int PF_RECOIL = (BIT(10));

	public static final int PF_FLYING = (BIT(11));
	public static final int PF_WEAPON_RETRACT = (BIT(12));
	public static final int PF_PICKED_UP_AN_UZI = (BIT(13));
	public static final int PF_CRAWLING = (BIT(14));
	public static final int PF_CLIMBING = (BIT(15));
	public static final int PF_SWIMMING = (BIT(16));
	public static final int PF_DIVING = (BIT(17));
	public static final int PF_DIVING_IN_LAVA = (BIT(18));
	public static final int PF_TWO_UZI = (BIT(19));
	public static final int PF_LOCK_RUN = (BIT(20));
	public static final int PF_TURN_180 = (BIT(21));
	public static final int PF_DEAD_HEAD = (BIT(22)); // are your a dead head;
	public static final int PF_HEAD_CONTROL = (BIT(23)); // have control of turning when a head?;
	public static final int PF_CLIP_CHEAT = (BIT(24)); // cheat for wall clipping;
	public static final int PF_SLIDING = (BIT(25)); // cheat for wall clipping;
	public static final int PF_VIEW_FROM_OUTSIDE = (BIT(26));
	public static final int PF_VIEW_OUTSIDE_WEAPON = (BIT(27));
	public static final int PF_VIEW_FROM_CAMERA = (BIT(28));
	public static final int PF_TANK = (BIT(29)); // Doin the tank thang;
//	public static final int PF_MOUSE_AIMING_ON = (BIT(30));
	public static final int PF_WEAPON_DOWN = (BIT(31));

	public static final int PF2_TELEPORTED = (BIT(0));

	enum FootType {
		WATER_FOOT, BLOOD_FOOT
	};

///////////////////////////////////////////////////////////////////////////////////////////
//
//Actor
//
///////////////////////////////////////////////////////////////////////////////////////////

//
//Hit Points
//

	public static final int HEALTH_RIPPER = 70;
	public static final int HEALTH_RIPPER2 = 200;
	public static final int HEALTH_MOMMA_RIPPER = 500;
	public static final int HEALTH_NINJA = 40;
	public static final int HEALTH_RED_NINJA = 160;
	public static final int HEALTH_COOLIE = 120;
	public static final int HEALTH_COOLIE_GHOST = 65;
	public static final int HEALTH_SKEL_PRIEST = 90;
	public static final int HEALTH_GORO = 200;
	public static final int HEALTH_HORNET = 4;
	public static final int HEALTH_SKULL = 4;
	public static final int HEALTH_EEL = 100;

	public static final int HEALTH_SERP_GOD = 3800;

	public static final int MAX_ACTOR_CLOSE_ATTACK = 2;
	public static final int MAX_ACTOR_ATTACK = 6;

	// sprite.extra flags
	// BUILD AND GAME - DO NOT MOVE THESE
	public static final int SPRX_SKILL = (BIT(0) | BIT(1) | BIT(2));

	// BIT(4) ST1 BUILD AND GAME
	public static final int SPRX_STAY_PUT_VATOR = (BIT(5)); // BUILD AND GAME - will not move with vators etc;
	// DO NOT MOVE THIS

	public static final int SPRX_STAG = (BIT(6)); // BUILD AND GAME - NON-ST1 sprite with ST1 type tagging;
	// DO NOT MOVE

	public static final int SPRX_QUEUE_SPRITE = (BIT(7)); // Queue sprite -check queue when deleting;
	public static final int SPRX_MULTI_ITEM = (BIT(9)); // BUILD AND GAME - multi player item;

	// have users - could be moved
	public static final int SPRX_PLAYER_OR_ENEMY = (BIT(11)); // for checking quickly if sprite is a ;
	// player or actor
	// do not need Users
	public static final int SPRX_FOUND = (BIT(12)); // BUILD ONLY INTERNAL - used for finding sprites;
	public static final int SPRX_BLADE = (BIT(12)); // blade sprite ;
	public static final int SPRX_BREAKABLE = (BIT(13)); // breakable items;
	public static final int SPRX_BURNABLE = (BIT(14)); // used for burnable sprites in the game;

	// temp use
	public static final int SPRX_BLOCK = (BIT(15)); // BUILD AND GAME
	// BUILD - tell which actors should not spawn
	// GAME - used for internal game code
	// ALT-M debug mode

	// !LIGHT
	// all three bits set - should never happen with skill
	// public static final int SPRX_USER_NON_STANDARD (BIT(0)|BIT(1)|BIT(2)) // used
	// for lighting

	// boolean flags carried over from build
	public static final int SPRX_BOOL11 = (BIT(5));
	public static final int SPRX_BOOL1 = (BIT(6));
	public static final int SPRX_BOOL2 = (BIT(7));
	public static final int SPRX_BOOL3 = (BIT(8));
	public static final int SPRX_BOOL4 = (BIT(9));
	public static final int SPRX_BOOL5 = (BIT(10));
	public static final int SPRX_BOOL6 = (BIT(11));
	public static final int SPRX_BOOL7 = (BIT(4)); // bit 12 was used build
	public static final int SPRX_BOOL8 = (BIT(13));
	public static final int SPRX_BOOL9 = (BIT(14));
	public static final int SPRX_BOOL10 = (BIT(15));

	public static final int SET_BOOL1(SPRITE sp) {
		return (sp).extra |= (SPRX_BOOL1);
	}

	public static final int SET_BOOL2(SPRITE sp) {
		return (sp).extra |= (SPRX_BOOL2);
	}

	public static final int SET_BOOL3(SPRITE sp) {
		return (sp).extra |= (SPRX_BOOL3);
	}

	public static final int SET_BOOL4(SPRITE sp) {
		return (sp).extra |= (SPRX_BOOL4);
	}

	public static final int SET_BOOL5(SPRITE sp) {
		return (sp).extra |= (SPRX_BOOL5);
	}

	public static final int SET_BOOL6(SPRITE sp) {
		return (sp).extra |= (SPRX_BOOL6);
	}

	public static final int SET_BOOL7(SPRITE sp) {
		return (sp).extra |= (SPRX_BOOL7);
	}

	public static final int SET_BOOL8(SPRITE sp) {
		return (sp).extra |= (SPRX_BOOL8);
	}

	public static final int SET_BOOL9(SPRITE sp) {
		return (sp).extra |= (SPRX_BOOL9);
	}

	public static final int SET_BOOL10(SPRITE sp) {
		return (sp).extra |= (SPRX_BOOL10);
	}

	public static final int SET_BOOL11(SPRITE sp) {
		return (sp).extra |= (SPRX_BOOL11);
	}

	public static final int RESET_BOOL1(SPRITE sp) {
		return (sp).extra &= ~(SPRX_BOOL1);
	}

	public static final int RESET_BOOL2(SPRITE sp) {
		return (sp).extra &= ~(SPRX_BOOL2);
	}

	public static final int RESET_BOOL3(SPRITE sp) {
		return (sp).extra &= ~(SPRX_BOOL3);
	}

	public static final int RESET_BOOL4(SPRITE sp) {
		return (sp).extra &= ~(SPRX_BOOL4);
	}

	public static final int RESET_BOOL5(SPRITE sp) {
		return (sp).extra &= ~(SPRX_BOOL5);
	}

	public static final int RESET_BOOL6(SPRITE sp) {
		return (sp).extra &= ~(SPRX_BOOL6);
	}

	public static final int RESET_BOOL7(SPRITE sp) {
		return (sp).extra &= ~(SPRX_BOOL7);
	}

	public static final int RESET_BOOL8(SPRITE sp) {
		return (sp).extra &= ~(SPRX_BOOL8);
	}

	public static final int RESET_BOOL9(SPRITE sp) {
		return (sp).extra &= ~(SPRX_BOOL9);
	}

	public static final int RESET_BOOL10(SPRITE sp) {
		return (sp).extra &= ~(SPRX_BOOL10);
	}

	public static final int RESET_BOOL11(SPRITE sp) {
		return (sp).extra &= ~(SPRX_BOOL11);
	}

	public static final boolean TEST_BOOL1(SPRITE sp) {
		return TEST((sp).extra, SPRX_BOOL1);
	}

	public static final boolean TEST_BOOL2(SPRITE sp) {
		return TEST((sp).extra, SPRX_BOOL2);
	}

	public static final boolean TEST_BOOL3(SPRITE sp) {
		return TEST((sp).extra, SPRX_BOOL3);
	}

	public static final boolean TEST_BOOL4(SPRITE sp) {
		return TEST((sp).extra, SPRX_BOOL4);
	}

	public static final boolean TEST_BOOL5(SPRITE sp) {
		return TEST((sp).extra, SPRX_BOOL5);
	}

	public static final boolean TEST_BOOL6(SPRITE sp) {
		return TEST((sp).extra, SPRX_BOOL6);
	}

	public static final boolean TEST_BOOL7(SPRITE sp) {
		return TEST((sp).extra, SPRX_BOOL7);
	}

	public static final boolean TEST_BOOL8(SPRITE sp) {
		return TEST((sp).extra, SPRX_BOOL8);
	}

	public static final boolean TEST_BOOL9(SPRITE sp) {
		return TEST((sp).extra, SPRX_BOOL9);
	}

	public static final boolean TEST_BOOL10(SPRITE sp) {
		return TEST((sp).extra, SPRX_BOOL10);
	}

	public static final boolean TEST_BOOL11(SPRITE sp) {
		return TEST((sp).extra, SPRX_BOOL11);
	}

	// User.Flags flags
	public static final int SPR_MOVED = BIT(0); // Did actor move;
	public static final int SPR_ATTACKED = BIT(1); // Is sprite being attacked?;
	public static final int SPR_TARGETED = BIT(2); // Is sprite a target of a weapon?;
	public static final int SPR_ACTIVE = BIT(3); // Is sprite aware of the player?;
	public static final int SPR_ELECTRO_TOLERANT = BIT(4); // Electro spell does not slow actor;
	public static final int SPR_JUMPING = BIT(5); // Actor is jumping;
	public static final int SPR_FALLING = BIT(6); // Actor is falling;
	public static final int SPR_CLIMBING = BIT(7); // Actor is falling;
	public static final int SPR_DEAD = BIT(8);// Actor is dying ;

	public static final int SPR_ZDIFF_MODE = BIT(10); // For following tracks at different z heights;
	public static final int SPR_SPEED_UP = BIT(11); // For following tracks at different speeds;
	public static final int SPR_SLOW_DOWN = BIT(12); // For following tracks at different speeds;
	public static final int SPR_DONT_UPDATE_ANG = BIT(13); // For tracks - don't update the angle for a while;

	public static final int SPR_SO_ATTACHED = BIT(14); // sprite is part of a sector object;
	public static final int SPR_SUICIDE = BIT(15); // sprite is set to kill itself;

	public static final int SPR_RUN_AWAY = BIT(16); // sprite is in "Run Away" track mode.;
	public static final int SPR_FIND_PLAYER = BIT(17); // sprite is in "Find Player" track mode.;

	public static final int SPR_SWIMMING = BIT(18); // Actor is swimming;
	public static final int SPR_WAIT_FOR_PLAYER = BIT(19); // Track Mode - Actor is waiting for player to come close;
	public static final int SPR_WAIT_FOR_TRIGGER = BIT(20); // Track Mode - Actor is waiting for player to trigger;
	public static final int SPR_SLIDING = BIT(21); // Actor is sliding;
	public static final int SPR_ON_SO_SECTOR = BIT(22); // sprite is on a sector object sector;

	public static final int SPR_SHADE_DIR = BIT(23); // sprite is on a sector object sector;
	public static final int SPR_XFLIP_TOGGLE = BIT(24); // sprite rotation xflip bit;
	public static final int SPR_NO_SCAREDZ = BIT(25); // not afraid of falling;

	public static final int SPR_SET_POS_DONT_KILL = BIT(26); // Don't kill sprites in MissileSetPos;
	public static final int SPR_SKIP2 = BIT(27); // 20 moves ps;
	public static final int SPR_SKIP4 = BIT(28); // 10 moves ps;

	public static final int SPR_BOUNCE = BIT(29); // For shrapnel types that can bounce once;
	public static final int SPR_UNDERWATER = BIT(30); // For missiles etc ;

	public static final int SPR_SHADOW = BIT(31); // Sprites that have shadows ;

	// User.Flags2 flags
	public static final int SPR2_BLUR_TAPER = (BIT(13) | BIT(14)); // taper type;
	public static final int SPR2_BLUR_TAPER_FAST = 1 << 13; // (BIT(13)); // taper fast;
	public static final int SPR2_BLUR_TAPER_SLOW = 1 << 14; // (BIT(14)); // taper slow;
	public static final int SPR2_SPRITE_FAKE_BLOCK = (BIT(15)); // fake blocking bit for damage;
	public static final int SPR2_NEVER_RESPAWN = (BIT(16)); // for item respawning;
	public static final int SPR2_ATTACH_WALL = (BIT(17));
	public static final int SPR2_ATTACH_FLOOR = (BIT(18));
	public static final int SPR2_ATTACH_CEILING = (BIT(19));
	public static final int SPR2_CHILDREN = (BIT(20)); // sprite OWNS children
	public static final int SPR2_SO_MISSILE = (BIT(21)); // this is a missile from a SO
	public static final int SPR2_DYING = (BIT(22)); // Sprite is currently dying
	public static final int SPR2_VIS_SHADING = (BIT(23)); // Sprite shading to go along with vis adjustments
	public static final int SPR2_DONT_TARGET_OWNER = (BIT(24));

///////////////////////////////////////////////////////////////////////////////////////////
//
//Sector Stuff - Sector Objects and Tracks
//
///////////////////////////////////////////////////////////////////////////////////////////

//flags in EXTRA variable
	public static final int SECTFX_SINK = BIT(0);
	public static final int SECTFX_OPERATIONAL = BIT(1);
	public static final int SECTFX_WARP_SECTOR = BIT(2);
	public static final int SECTFX_CURRENT = BIT(3);
	public static final int SECTFX_Z_ADJUST = BIT(4); // adjust ceiling/floor
	public static final int SECTFX_NO_RIDE = BIT(5); // moving sector - don't ride it
	public static final int SECTFX_DYNAMIC_AREA = BIT(6);
	public static final int SECTFX_DIVE_AREA = BIT(7); // Diving area
	public static final int SECTFX_UNDERWATER = BIT(8); // Underwater area
	public static final int SECTFX_UNDERWATER2 = BIT(9); // Underwater area

	public static final int SECTFX_LIQUID_MASK = (BIT(10) | BIT(11));// only valid for sectors with depth
	public static final int SECTFX_LIQUID_NONE = (0);
	public static final int SECTFX_LIQUID_LAVA = BIT(10);
	public static final int SECTFX_LIQUID_WATER = BIT(11);
	public static final int SECTFX_SECTOR_OBJECT = BIT(12); // for collision detection
	public static final int SECTFX_VATOR = BIT(13); // denotes that this is a vertical moving sector
// vator type
	public static final int SECTFX_TRIGGER = BIT(14); // trigger type to replace tags.h trigger types

//flags in sector USER structure
	public static final int SECTFU_SO_DONT_BOB = BIT(0);
	public static final int SECTFU_SO_SINK_DEST = BIT(1);
	public static final int SECTFU_SO_DONT_SINK = BIT(2);
	public static final int SECTFU_DONT_COPY_PALETTE = BIT(3);
	public static final int SECTFU_SO_SLOPE_FLOOR_TO_POINT = BIT(4);
	public static final int SECTFU_SO_SLOPE_CEILING_TO_POINT = BIT(5);
	public static final int SECTFU_DAMAGE_ABOVE_SECTOR = BIT(6);
	public static final int SECTFU_VATOR_BOTH = BIT(7); // vators set up for both ceiling and floor
	public static final int SECTFU_CANT_SURFACE = BIT(8); // for diving
	public static final int SECTFU_SLIDE_SECTOR = BIT(9); // for diving

	public static final int WALLFX_LOOP_DONT_SPIN = BIT(0);
	public static final int WALLFX_LOOP_REVERSE_SPIN = BIT(1);
	public static final int WALLFX_LOOP_SPIN_2X = BIT(2);
	public static final int WALLFX_LOOP_SPIN_4X = BIT(3);
	public static final int WALLFX_LOOP_OUTER = BIT(4); // for sector object
	public static final int WALLFX_DONT_MOVE = BIT(5); // for sector object
	public static final int WALLFX_SECTOR_OBJECT = BIT(6); // for collision detection
	public static final int WALLFX_DONT_STICK = BIT(7); // for bullet holes and stars
	public static final int WALLFX_DONT_SCALE = BIT(8); // for sector object
	public static final int WALLFX_LOOP_OUTER_SECONDARY = BIT(9); // for sector object

	public static final int MAX_TARGET_SORT = 16;
	public static final int MAX_DOOR_AUTO_CLOSE = 16;
	public static final int MAXANIM = 256;

//Regular track flags
	public static final int TF_TRACK_OCCUPIED = BIT(0);

	public static final int MAX_SO_SECTOR = 40;
	public static final int MAX_SO_POINTS = (MAX_SO_SECTOR * 15);
	public static final int MAX_SO_SPRITE = 60;
	public static final int MAX_CLIPBOX = 32;

	public static final int MAX_SECTOR_OBJECTS = 25;

	public static final int SOBJ_SPEED_UP = BIT(0);
	public static final int SOBJ_SLOW_DOWN = BIT(1);
	public static final int SOBJ_ZUP = BIT(2);
	public static final int SOBJ_ZDOWN = BIT(3);
	public static final int SOBJ_ZDIFF_MODE = BIT(4);
	public static final int SOBJ_MOVE_VERTICAL = BIT(5); // for sprite objects - move straight up/down
	public static final int SOBJ_ABSOLUTE_ANGLE = BIT(7);
	public static final int SOBJ_SPRITE_OBJ = BIT(8);
	public static final int SOBJ_DONT_ROTATE = BIT(9);
	public static final int SOBJ_WAIT_FOR_EVENT = BIT(10);
	public static final int SOBJ_HAS_WEAPON = BIT(11);
	public static final int SOBJ_SYNC1 = BIT(12); // for syncing up several SO's perfectly
	public static final int SOBJ_SYNC2 = BIT(13); // for syncing up several SO's perfectly
	public static final int SOBJ_DYNAMIC = BIT(14); // denotes scaling or morphing object
	public static final int SOBJ_ZMID_FLOOR = BIT(15); // can't remember which sector objects need this
	// think its the bobbing and sinking ones
	public static final int SOBJ_SLIDE = BIT(16);

	public static final int SOBJ_OPERATIONAL = BIT(17);
	public static final int SOBJ_KILLABLE = BIT(18);
	public static final int SOBJ_DIE_HARD = BIT(19);
	public static final int SOBJ_UPDATE_ONCE = BIT(20);
	public static final int SOBJ_UPDATE = BIT(21);
	public static final int SOBJ_NO_QUAKE = BIT(22);
	public static final int SOBJ_REMOTE_ONLY = BIT(23);
	public static final int SOBJ_RECT_CLIP = BIT(24);
	public static final int SOBJ_BROKEN = BIT(25);

// track set to these to tell them apart
	public static final int SO_OPERATE_TRACK_START = 90;
	public static final int SO_TURRET_MGUN = 96;// machine gun
	public static final int SO_TURRET = 97;
	public static final int SO_TANK = 98;
	public static final int SO_SPEED_BOAT = 99;

	public static final int FAF_PLACE_MIRROR_PIC = 341;
	public static final short FAF_MIRROR_PIC = 2356;

	public static final boolean FAF_ConnectCeiling(int sectnum) {
		return isValidSector(sectnum) ? (sector[(sectnum)].ceilingpicnum == FAF_MIRROR_PIC) : false;
	}

	public static final boolean FAF_ConnectFloor(int sectnum) {
		return  isValidSector(sectnum) ? (sector[(sectnum)].floorpicnum == FAF_MIRROR_PIC) : false;
	}

	public static final boolean FAF_ConnectArea(int sectnum) {
		return (FAF_ConnectCeiling(sectnum) || FAF_ConnectFloor(sectnum));
	}

	public static final int ACTOR_GRAVITY = 8;

	public static final int synctics = 3;
	public static final int ACTORMOVETICS = (synctics << 1);
	public static final int TICSPERMOVEMENT = synctics;

	public static final int GETZRANGE_CLIP_ADJ = 8;

	public static final int TEXT_TEST_LINE = (200 / 2);

	public static final int TEXT_XCENTER(int width) {
		return ((320 - width) / 2);
	}

	public static final int TEXT_YCENTER(int height) {
		return ((200 - height) / 2);
	}

	public static final int TEXT_TEST_COL(int width) {
		return TEXT_XCENTER(width);
	}

	public static final int TEXT_TEST_TIME = 2;

	public static final int MASTER_SWITCHING = 1;

	public static final int STAT_DAMAGE_LIST_SIZE = 20;

	public static final int COLOR_PAIN = 128; // Light red range

	public static final int MAXSO = (MAXLONG);

	public static final int RANDOM_P2(int pwr_of_2) {
		return (MOD_P2(engine.krand(), (pwr_of_2)));
	}

	public static final int RANDOM_RANGE(int range) {
		return RandomRange(range);
	}

	public static final int RANDOM() {
		return engine.krand();
	}

	public static int FindDistance2D(int dx, int dy) {
		dx = klabs(dx);
		dy = klabs(dy);
		if (dx == 0)
			return (dy);
		if (dy == 0)
			return (dx);
		if (dy < dx) {
			int i = dx;
			dx = dy;
			dy = i;
		} // swap x, y
		dx += (dx >> 1);
		return ((dx >> 6) + (dx >> 2) + dy - (dy >> 5) - (dy >> 7)); // handle 1 octant
	}

	public static int FindDistance3D(int dx, int dy, int dz) {
		dx = klabs(dx);
		dy = klabs(dy);
		dz = klabs(dz);

		if (dx < dy) {
			int i = dx;
			dx = dy;
			dy = i;
		} // swap x, y
		if (dx < dz) {
			int i = dx;
			dx = dz;
			dz = i;
		} // swap x, z

		int t = dy + dz;

		return (dx - (dx >> 4) + (t >> 2) + (t >> 3));
	}

}
