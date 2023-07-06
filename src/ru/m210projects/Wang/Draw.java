package ru.m210projects.Wang;

import static ru.m210projects.Build.Engine.CEIL;
import static ru.m210projects.Build.Engine.FLOOR;
import static ru.m210projects.Build.Engine.MAXSPRITES;
import static ru.m210projects.Build.Engine.MAXSPRITESONSCREEN;
import static ru.m210projects.Build.Engine.MAXSTATUS;
import static ru.m210projects.Build.Engine.MAXTILES;
import static ru.m210projects.Build.Engine.getInput;
import static ru.m210projects.Build.Engine.gotpic;
import static ru.m210projects.Build.Engine.headspritesect;
import static ru.m210projects.Build.Engine.headspritestat;
import static ru.m210projects.Build.Engine.nextspritesect;
import static ru.m210projects.Build.Engine.nextspritestat;
import static ru.m210projects.Build.Engine.pHitInfo;
import static ru.m210projects.Build.Engine.sector;
import static ru.m210projects.Build.Engine.show2dsector;
import static ru.m210projects.Build.Engine.sintable;
import static ru.m210projects.Build.Engine.sprite;
import static ru.m210projects.Build.Engine.spritesortcnt;
import static ru.m210projects.Build.Engine.totalclock;
import static ru.m210projects.Build.Engine.tsprite;
import static ru.m210projects.Build.Engine.visibility;
import static ru.m210projects.Build.Engine.wall;
import static ru.m210projects.Build.Engine.windowx1;
import static ru.m210projects.Build.Engine.windowy1;
import static ru.m210projects.Build.Engine.xdim;
import static ru.m210projects.Build.Engine.ydim;
import static ru.m210projects.Build.Gameutils.BClampAngle;
import static ru.m210projects.Build.Gameutils.coordsConvertXScaled;
import static ru.m210projects.Build.Gameutils.isValidSector;
import static ru.m210projects.Build.Net.Mmulti.connecthead;
import static ru.m210projects.Build.Net.Mmulti.myconnectindex;
import static ru.m210projects.Build.Net.Mmulti.numplayers;
import static ru.m210projects.Build.Pragmas.divscale;
import static ru.m210projects.Build.Pragmas.klabs;
import static ru.m210projects.Build.Pragmas.mulscale;
import static ru.m210projects.Build.Pragmas.scale;
import static ru.m210projects.Build.Strhandler.Bitoa;
import static ru.m210projects.Build.Strhandler.buildString;
import static ru.m210projects.Wang.Cheats.LocationInfo;
import static ru.m210projects.Wang.Enemies.Sumo.BossHealthMeter;
import static ru.m210projects.Wang.Factory.WangNetwork.PredictionOn;
import static ru.m210projects.Wang.Game.ALTHUDLEFT;
import static ru.m210projects.Wang.Game.ALTHUDRIGHT;
import static ru.m210projects.Wang.Game.DemoPlaying;
import static ru.m210projects.Wang.Game.Global_PLock;
import static ru.m210projects.Wang.Game.HelpPage;
import static ru.m210projects.Wang.Game.HelpPagePic;
import static ru.m210projects.Wang.Game.Kills;
import static ru.m210projects.Wang.Game.Level;
import static ru.m210projects.Wang.Game.MessageInputMode;
import static ru.m210projects.Wang.Game.PlayClock;
import static ru.m210projects.Wang.Game.Player;
import static ru.m210projects.Wang.Game.TotalKillable;
import static ru.m210projects.Wang.Game.getMapName;
import static ru.m210projects.Wang.Game.screenpeek;
import static ru.m210projects.Wang.Game.tmp_ptr;
import static ru.m210projects.Wang.Gameutils.ACTORMOVETICS;
import static ru.m210projects.Wang.Gameutils.CEILING_STAT_PLAX;
import static ru.m210projects.Wang.Gameutils.CIRCLE_CAMERA_DIST_MIN;
import static ru.m210projects.Wang.Gameutils.CLIPMASK_MISSILE;
import static ru.m210projects.Wang.Gameutils.CLIPMASK_PLAYER;
import static ru.m210projects.Wang.Gameutils.CSTAT_SPRITE_BLOCK;
import static ru.m210projects.Wang.Gameutils.CSTAT_SPRITE_BLOCK_HITSCAN;
import static ru.m210projects.Wang.Gameutils.CSTAT_SPRITE_FLOOR;
import static ru.m210projects.Wang.Gameutils.CSTAT_SPRITE_INVISIBLE;
import static ru.m210projects.Wang.Gameutils.CSTAT_SPRITE_SLAB;
import static ru.m210projects.Wang.Gameutils.CSTAT_SPRITE_TRANSLUCENT;
import static ru.m210projects.Wang.Gameutils.CSTAT_SPRITE_TRANS_FLIP;
import static ru.m210projects.Wang.Gameutils.CSTAT_SPRITE_WALL;
import static ru.m210projects.Wang.Gameutils.CSTAT_SPRITE_XFLIP;
import static ru.m210projects.Wang.Gameutils.CSTAT_SPRITE_YFLIP;
import static ru.m210projects.Wang.Gameutils.DISTANCE;
import static ru.m210projects.Wang.Gameutils.FAF_ConnectArea;
import static ru.m210projects.Wang.Gameutils.FAF_ConnectCeiling;
import static ru.m210projects.Wang.Gameutils.FAF_ConnectFloor;
import static ru.m210projects.Wang.Gameutils.FAF_MIRROR_PIC;
import static ru.m210projects.Wang.Gameutils.FLOOR_STAT_PLAX;
import static ru.m210projects.Wang.Gameutils.FLOOR_STAT_SLOPE;
import static ru.m210projects.Wang.Gameutils.HIT_MASK;
import static ru.m210projects.Wang.Gameutils.HIT_SECTOR;
import static ru.m210projects.Wang.Gameutils.HIT_SPRITE;
import static ru.m210projects.Wang.Gameutils.MAX_SW_PLAYERS;
import static ru.m210projects.Wang.Gameutils.MESSAGE_LINE;
import static ru.m210projects.Wang.Gameutils.MOVEx;
import static ru.m210projects.Wang.Gameutils.MOVEy;
import static ru.m210projects.Wang.Gameutils.NORM_ANGLE;
import static ru.m210projects.Wang.Gameutils.NORM_SPRITE;
import static ru.m210projects.Wang.Gameutils.PF_CLIMBING;
import static ru.m210projects.Wang.Gameutils.PF_VIEW_FROM_CAMERA;
import static ru.m210projects.Wang.Gameutils.PF_VIEW_FROM_OUTSIDE;
import static ru.m210projects.Wang.Gameutils.ROTATE_SPRITE_CORNER;
import static ru.m210projects.Wang.Gameutils.ROTATE_SPRITE_IGNORE_START_MOST;
import static ru.m210projects.Wang.Gameutils.ROTATE_SPRITE_NON_MASK;
import static ru.m210projects.Wang.Gameutils.ROTATE_SPRITE_SCREEN_CLIP;
import static ru.m210projects.Wang.Gameutils.SECTFU_DONT_COPY_PALETTE;
import static ru.m210projects.Wang.Gameutils.SET_GOTPIC;
import static ru.m210projects.Wang.Gameutils.SPR2_BLUR_TAPER;
import static ru.m210projects.Wang.Gameutils.SPR2_BLUR_TAPER_FAST;
import static ru.m210projects.Wang.Gameutils.SPR2_BLUR_TAPER_SLOW;
import static ru.m210projects.Wang.Gameutils.SPR2_VIS_SHADING;
import static ru.m210projects.Wang.Gameutils.SPRITE_TAG2;
import static ru.m210projects.Wang.Gameutils.SPRITE_TAG3;
import static ru.m210projects.Wang.Gameutils.SPRITE_TAG4;
import static ru.m210projects.Wang.Gameutils.SPRITE_TAG5;
import static ru.m210projects.Wang.Gameutils.SPRITE_TAG6;
import static ru.m210projects.Wang.Gameutils.SPRITEp_BOS;
import static ru.m210projects.Wang.Gameutils.SPRITEp_SIZE_Y;
import static ru.m210projects.Wang.Gameutils.SPRITEp_SIZE_Z;
import static ru.m210projects.Wang.Gameutils.SPRITEp_TOS;
import static ru.m210projects.Wang.Gameutils.SPRX_BURNABLE;
import static ru.m210projects.Wang.Gameutils.SPRX_PLAYER_OR_ENEMY;
import static ru.m210projects.Wang.Gameutils.SPR_SHADOW;
import static ru.m210projects.Wang.Gameutils.SPR_SKIP2;
import static ru.m210projects.Wang.Gameutils.SPR_SKIP4;
import static ru.m210projects.Wang.Gameutils.SPR_XFLIP_TOGGLE;
import static ru.m210projects.Wang.Gameutils.STD_RANDOM_RANGE;
import static ru.m210projects.Wang.Gameutils.TEST_BOOL1;
import static ru.m210projects.Wang.Gameutils.TEST_GOTSECTOR;
import static ru.m210projects.Wang.Gameutils.TEXT_TEST_COL;
import static ru.m210projects.Wang.Gameutils.WPN_FIST;
import static ru.m210projects.Wang.Gameutils.WPN_SWORD;
import static ru.m210projects.Wang.Gameutils.Z;
import static ru.m210projects.Wang.Gameutils.pUser;
import static ru.m210projects.Wang.Inv.DrawInventory;
import static ru.m210projects.Wang.Inv.InventoryData;
import static ru.m210projects.Wang.JPlayer.gametext;
import static ru.m210projects.Wang.JPlayer.minigametext;
import static ru.m210projects.Wang.JPlayer.operatefta;
import static ru.m210projects.Wang.JSector.JAnalyzeSprites;
import static ru.m210projects.Wang.JSector.JS_DrawMirrors;
import static ru.m210projects.Wang.JTags.LUMINOUS;
import static ru.m210projects.Wang.Main.engine;
import static ru.m210projects.Wang.Main.gGameScreen;
import static ru.m210projects.Wang.Main.gNet;
import static ru.m210projects.Wang.Main.game;
import static ru.m210projects.Wang.Main.gs;
import static ru.m210projects.Wang.Names.BETTY_R0;
import static ru.m210projects.Wang.Names.CARGIRL_R0;
import static ru.m210projects.Wang.Names.COINCURSOR;
import static ru.m210projects.Wang.Names.COMPASS_EAST;
import static ru.m210projects.Wang.Names.COMPASS_EAST2;
import static ru.m210projects.Wang.Names.COMPASS_MID_TIC;
import static ru.m210projects.Wang.Names.COMPASS_MID_TIC2;
import static ru.m210projects.Wang.Names.COMPASS_NORTH;
import static ru.m210projects.Wang.Names.COMPASS_NORTH2;
import static ru.m210projects.Wang.Names.COMPASS_SOUTH;
import static ru.m210projects.Wang.Names.COMPASS_SOUTH2;
import static ru.m210projects.Wang.Names.COMPASS_TIC;
import static ru.m210projects.Wang.Names.COMPASS_TIC2;
import static ru.m210projects.Wang.Names.COMPASS_WEST;
import static ru.m210projects.Wang.Names.COMPASS_WEST2;
import static ru.m210projects.Wang.Names.COMPASS_X;
import static ru.m210projects.Wang.Names.COMPASS_Y;
import static ru.m210projects.Wang.Names.FIREBALL_FLAMES;
import static ru.m210projects.Wang.Names.ICON_GRENADE_LAUNCHER;
import static ru.m210projects.Wang.Names.ICON_GUARD_HEAD;
import static ru.m210projects.Wang.Names.ICON_HEART;
import static ru.m210projects.Wang.Names.ICON_MICRO_GUN;
import static ru.m210projects.Wang.Names.ICON_RAIL_GUN;
import static ru.m210projects.Wang.Names.ICON_SHOTGUN;
import static ru.m210projects.Wang.Names.ICON_STAR;
import static ru.m210projects.Wang.Names.ICON_UZI;
import static ru.m210projects.Wang.Names.MECHANICGIRL_R0;
import static ru.m210projects.Wang.Names.PRUNEGIRL_R0;
import static ru.m210projects.Wang.Names.SAILORGIRL_R0;
import static ru.m210projects.Wang.Names.SLIME;
import static ru.m210projects.Wang.Names.ST1;
import static ru.m210projects.Wang.Names.STAR1;
import static ru.m210projects.Wang.Names.STATUS_BAR;
import static ru.m210projects.Wang.Names.STAT_CEILING_FLOOR_PIC_OVERRIDE;
import static ru.m210projects.Wang.Names.STAT_DEAD_ACTOR;
import static ru.m210projects.Wang.Names.STAT_DEFAULT;
import static ru.m210projects.Wang.Names.STAT_DEMO_CAMERA;
import static ru.m210projects.Wang.Names.STAT_DONT_DRAW;
import static ru.m210projects.Wang.Names.STAT_ENEMY;
import static ru.m210projects.Wang.Names.STAT_FAF_COPY;
import static ru.m210projects.Wang.Names.STAT_FLOOR_SLOPE_DONT_DRAW;
import static ru.m210projects.Wang.Names.STAT_ITEM;
import static ru.m210projects.Wang.Names.STAT_PLAYER0;
import static ru.m210projects.Wang.Names.STAT_SKIP2_INTERP_END;
import static ru.m210projects.Wang.Names.STAT_SKIP4_INTERP_END;
import static ru.m210projects.Wang.Names.STAT_STAR_QUEUE;
import static ru.m210projects.Wang.Names.STAT_WARP_COPY_SPRITE1;
import static ru.m210projects.Wang.Names.STAT_WARP_COPY_SPRITE2;
import static ru.m210projects.Wang.Names.TOILETGIRL_R0;
import static ru.m210projects.Wang.Names.TRASHCAN;
import static ru.m210projects.Wang.Names.WASHGIRL_R0;
import static ru.m210projects.Wang.Palette.PALETTE_DEFAULT;
import static ru.m210projects.Wang.Palette.PALETTE_DIVE;
import static ru.m210projects.Wang.Palette.PALETTE_DIVE_LAVA;
import static ru.m210projects.Wang.Palette.PALETTE_FOG;
import static ru.m210projects.Wang.Palette.PALETTE_ILLUMINATE;
import static ru.m210projects.Wang.Palette.PALETTE_RED_LIGHTING;
import static ru.m210projects.Wang.Palette.PAL_XLAT_BRIGHT_GREEN;
import static ru.m210projects.Wang.Palette.PAL_XLAT_BROWN;
import static ru.m210projects.Wang.Panel.BORDER_ALTBAR;
import static ru.m210projects.Wang.Panel.BORDER_BAR;
import static ru.m210projects.Wang.Panel.BORDER_MINI_BAR;
import static ru.m210projects.Wang.Panel.PlayerUpdateKeys;
import static ru.m210projects.Wang.Panel.PlayerUpdatePanelInfo;
import static ru.m210projects.Wang.Panel.PreUpdatePanel;
import static ru.m210projects.Wang.Panel.UpdatePanel;
import static ru.m210projects.Wang.Player.DoPlayerDiveMeter;
import static ru.m210projects.Wang.Player.GetDeltaAngle;
import static ru.m210projects.Wang.Player.MSG_GAME_PAUSED;
import static ru.m210projects.Wang.Player.PLAYER_HORIZ_MAX;
import static ru.m210projects.Wang.Player.PLAYER_HORIZ_MIN;
import static ru.m210projects.Wang.Quake.QuakeViewChange;
import static ru.m210projects.Wang.Rooms.COVERinsertsprite;
import static ru.m210projects.Wang.Rooms.COVERupdatesector;
import static ru.m210projects.Wang.Rooms.DrawOverlapRoom;
import static ru.m210projects.Wang.Rooms.FAFcansee;
import static ru.m210projects.Wang.Rooms.FAFgetzrangepoint;
import static ru.m210projects.Wang.Rooms.PicInView;
import static ru.m210projects.Wang.Rooms.zofslope;
import static ru.m210projects.Wang.Sector.LevelSecrets;
import static ru.m210projects.Wang.Sector.SectUser;
import static ru.m210projects.Wang.Sector.movelava;
import static ru.m210projects.Wang.Sprites.MoveSkip2;
import static ru.m210projects.Wang.Sprites.MoveSkip4;
import static ru.m210projects.Wang.Text.DisplayMiniBarNumber;
import static ru.m210projects.Wang.Text.DisplayPanelNumber;
import static ru.m210projects.Wang.Text.DisplaySummaryString;
import static ru.m210projects.Wang.Text.DrawFragBar;
import static ru.m210projects.Wang.Type.MyTypes.DIV16;
import static ru.m210projects.Wang.Type.MyTypes.DIV2;
import static ru.m210projects.Wang.Type.MyTypes.DTEST;
import static ru.m210projects.Wang.Type.MyTypes.TEST;
import static ru.m210projects.Wang.Vis.VisViewChange;
import static ru.m210projects.Wang.Weapon.s_Star;
import static ru.m210projects.Wang.Weapon.s_StarDown;
import static ru.m210projects.Wang.Weapon.s_StarDownStuck;
import static ru.m210projects.Wang.Weapon.s_StarStuck;

import ru.m210projects.Build.Gameutils.ConvertType;
import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Build.Pattern.BuildFont;
import ru.m210projects.Build.Pattern.BuildFont.TextAlign;
import ru.m210projects.Build.Pattern.BuildGame.NetMode;
import ru.m210projects.Build.Pattern.Tools.Interpolation.ILoc;
import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Build.Types.Tile;
import ru.m210projects.Build.Types.WALL;
import ru.m210projects.Wang.Factory.WangNetwork.MultiGameTypes;
import ru.m210projects.Wang.Type.INVENTORY_DATA;
import ru.m210projects.Wang.Type.LONGp;
import ru.m210projects.Wang.Type.ParentalStruct;
import ru.m210projects.Wang.Type.PlayerStr;
import ru.m210projects.Wang.Type.Sect_User;
import ru.m210projects.Wang.Type.State;
import ru.m210projects.Wang.Type.USER;

public class Draw {

	private static short[] viewWeaponTile = { -1, -1, 524, 559, 558, 526, 589, 618, 539, 800, 525, 811, 810, -1 };

	public static int gPlayerIndex = -1;
	public static boolean bCopySpriteOffs[] = new boolean[MAXSPRITES];

	public static int ScreenHeight, ScreenWidth, ScreenMode;
//	public static int CrosshairX, CrosshairY;
	public static boolean CameraTestMode;
	public static int dimensionmode;
	public static boolean ScrollMode2D, DrawScreen;
	public static boolean HelpInputMode;
	public static boolean NoMeters = false;
	public static int gNameShowTime;

	public static int Follow_posx, Follow_posy, zoom = 768;

	private static final short RotTable8[] = { 0, 7, 6, 5, 4, 3, 2, 1 };
	private static final short RotTable5[] = { 0, 1, 2, 3, 4, 3, 2, 1 };

	public static final int INVISTILE = 6145;
	public static ParentalStruct[] aVoxelArray = new ParentalStruct[MAXTILES];

	public static boolean OverlapDraw = false;

	private static short DART_PIC = 2526;
	private static short DART_REPEAT = 16;

	public static void SetRedrawScreen(PlayerStr pp) {

	}

	public static void ShadeSprite(SPRITE tsp) {
		// set shade of sprite
		tsp.shade = (byte) (sector[tsp.sectnum].floorshade - 25);

		if (tsp.shade > -3)
			tsp.shade = -3;

		if (tsp.shade < -30)
			tsp.shade = -30;
	}

	public static short GetRotation(int tSpriteNum, int viewx, int viewy) {
		SPRITE tsp = tsprite[tSpriteNum];
		USER tu = pUser[tsp.owner];

		if (tu.RotNum == 0)
			return (0);

		// Get which of the 8 angles of the sprite to draw (0-7)
		// rotation ranges from 0-7
		int angle2 = engine.getangle(tsp.x - viewx, tsp.y - viewy);
		int rotation = ((tsp.ang + 3072 + 128 - angle2) & 2047);
		rotation = (rotation >> 8) & 7;

		if (tu.RotNum == 5) {
			if (TEST(tu.Flags, SPR_XFLIP_TOGGLE)) {
				if (rotation <= 4) {
					// leave rotation alone
					tsp.cstat &= ~(CSTAT_SPRITE_XFLIP);
				} else {
					rotation = (8 - rotation);
					tsp.cstat |= (CSTAT_SPRITE_XFLIP); // clear x-flipping bit
				}
			} else {
				if (rotation > 3 || rotation == 0) {
					// leave rotation alone
					tsp.cstat &= ~(CSTAT_SPRITE_XFLIP); // clear x-flipping bit
				} else {
					rotation = (8 - rotation);
					tsp.cstat |= (CSTAT_SPRITE_XFLIP); // set
				}
			}

			// Special case bunk
			if (tu.ID == TOILETGIRL_R0 || tu.ID == WASHGIRL_R0 || tu.ID == TRASHCAN || tu.ID == CARGIRL_R0
					|| tu.ID == MECHANICGIRL_R0 || tu.ID == PRUNEGIRL_R0 || tu.ID == SAILORGIRL_R0)
				tsp.cstat &= ~(CSTAT_SPRITE_XFLIP); // clear x-flipping bit

			return (RotTable5[rotation]);
		}

		return (RotTable8[rotation]);
	}

	/*
	 * !AIC - At draw time this is called for actor rotation. GetRotation() is more
	 * complex than needs to be in part because importing of actor rotations and
	 * x-flip directions was not standardized.
	 */

	public static int SetActorRotation(int tSpriteNum, int viewx, int viewy) {
		SPRITE tsp = tsprite[tSpriteNum];
		USER tu = pUser[tsp.owner];

		// don't modify ANY tu vars - back them up!
		State State = tu.State;
		State StateStart = tu.StateStart;

		if (tu.RotNum == 0 || tu.Rot == null)
			return (0);

		// Get the offset into the State animation
		int StateOffset = State.id - StateStart.id;

		// Get the rotation angle
		int Rotation = GetRotation(tSpriteNum, viewx, viewy);

		if (Rotation >= 5)
			return -1; // assert

		// Reset the State animation start based on the Rotation
		StateStart = tu.Rot.getState(Rotation);

		// Set the sprites state
		State = tu.Rot.getState(Rotation, StateOffset);

		// set the picnum here - may be redundant, but we just changed states and
		// thats a big deal
		tsp.picnum = State.Pic;
		return 0;
	}

	public static int DoShadowFindGroundPoint(SPRITE sp) {
		// USES TSPRITE !!!!!
		USER u = pUser[sp.owner];
		SPRITE hsp;
		int florhit;
		int loz = u.loz;
		short save_cstat, bak_cstat;

		// recursive routine to find the ground - either sector or floor sprite
		// skips over enemy and other types of sprites

		// IMPORTANT!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		// This will return invalid FAF ceiling and floor heights inside of
		// analyzesprite
		// because the ceiling and floors get moved out of the way for drawing.

		save_cstat = sp.cstat;
		sp.cstat &= ~(CSTAT_SPRITE_BLOCK | CSTAT_SPRITE_BLOCK_HITSCAN);
		FAFgetzrangepoint(sp.x, sp.y, sp.z, sp.sectnum, tmp_ptr[0], tmp_ptr[1], tmp_ptr[2], tmp_ptr[3]); // &hiz,
																											// &ceilhit,
																											// &loz,
																											// &florhit);
		sp.cstat = save_cstat;
		loz = tmp_ptr[2].value;
		florhit = tmp_ptr[3].value;

		switch (DTEST(florhit, HIT_MASK)) {
		case HIT_SPRITE: {
			hsp = sprite[NORM_SPRITE(florhit)];

			if (TEST(hsp.cstat, CSTAT_SPRITE_FLOOR)) {
				// found a sprite floor
				return (loz);
			} else {
				// reset the blocking bit of what you hit and try again -
				// recursive
				bak_cstat = hsp.cstat;
				hsp.cstat &= ~(CSTAT_SPRITE_BLOCK | CSTAT_SPRITE_BLOCK_HITSCAN);
				loz = DoShadowFindGroundPoint(sp);
				hsp.cstat = bak_cstat;
			}
		}

		case HIT_SECTOR:
			return (loz);
		}

		return (loz);
	}

	public static void DoShadows(SPRITE tsp, int viewx, int viewy, int viewz) {

		USER tu = pUser[tsp.owner];
		int ground_dist = 0;
		int view_dist = 0;
		int loz;
		short xrepeat;
		short yrepeat;

		// make sure its the correct sector
		// DoShadowFindGroundPoint calls FAFgetzrangepoint and this is sensitive
		// updatesectorz(tsp.x, tsp.y, tsp.z, &sectnum);
		short sectnum = engine.updatesector(tsp.x, tsp.y, tsp.sectnum);

		if (sectnum < 0 || spritesortcnt >= MAXSPRITESONSCREEN)
			return;

		tsp.sectnum = sectnum;
		if (tsprite[spritesortcnt] == null)
			tsprite[spritesortcnt] = new SPRITE();
		SPRITE newsp = tsprite[spritesortcnt];
		newsp.set(tsp);

		// shadow is ALWAYS draw last - status is priority
		newsp.statnum = MAXSTATUS;
		newsp.sectnum = sectnum;

		if ((tsp.yrepeat >> 2) > 4) {
			yrepeat = (short) ((tsp.yrepeat >> 2) - (SPRITEp_SIZE_Y(tsp) / 64) * 2);
			xrepeat = newsp.xrepeat;
		} else {
			yrepeat = newsp.yrepeat;
			xrepeat = newsp.xrepeat;
		}

		newsp.shade = 127;
		newsp.cstat |= (CSTAT_SPRITE_TRANSLUCENT);

		loz = tu.loz;
		if (tu.lo_sp != -1) {
			if (!TEST(sprite[tu.lo_sp].cstat, CSTAT_SPRITE_WALL | CSTAT_SPRITE_FLOOR)) {
				loz = DoShadowFindGroundPoint(tsp);
			}
		}

		int camangle = engine.getangle(viewx - tsp.x, viewy - tsp.y);
		newsp.x -= mulscale(sintable[(camangle + 512) & 2047], 100, 16);
		newsp.y += mulscale(sintable[(camangle + 1024) & 2047], 100, 16);
		// need to find the ground here
		newsp.z = loz;

		// if below or close to sprites z don't bother to draw it
		if ((viewz - loz) > -Z(8))
			return;

		// if close to shadows z shrink it
		view_dist = klabs(loz - viewz) >> 8;
		if (view_dist < 32)
			view_dist = 256 / view_dist;
		else
			view_dist = 0;

		// make shadow smaller depending on height from ground
		ground_dist = klabs(loz - SPRITEp_BOS(tsp)) >> 8;
		ground_dist = DIV16(ground_dist);

		xrepeat = (short) Math.max(xrepeat - ground_dist - view_dist, 4);
		yrepeat = (short) Math.max(yrepeat - ground_dist - view_dist, 4);
		xrepeat = (short) Math.min(xrepeat, 255);
		yrepeat = (short) Math.min(yrepeat, 255);

		newsp.xrepeat = xrepeat;
		newsp.yrepeat = yrepeat;

		// Check for voxel items and use a round generic pic if so

		spritesortcnt++;
	}

	public static void DoMotionBlur(SPRITE tsp) {
		SPRITE newsp;
		USER tu = pUser[tsp.owner];
		int nx, ny, nz = 0, dx, dy, dz;
		short i, ang;
		short xrepeat, yrepeat, repeat_adj = 0;
		int z_amt_per_pixel;

		ang = NORM_ANGLE(tsp.ang + 1024);

		if (tsp.xvel == 0) {
			return;
		}

		if (TEST(tsp.extra, SPRX_PLAYER_OR_ENEMY)) {
			z_amt_per_pixel = ((-tu.jump_speed * ACTORMOVETICS) << 16) / tsp.xvel;
		} else {
			z_amt_per_pixel = ((-tsp.zvel) << 16) / tsp.xvel;
		}

		switch (tu.motion_blur_dist) {
		case 64:
			dx = nx = MOVEx(64, ang);
			dy = ny = MOVEy(64, ang);
			nz = (z_amt_per_pixel * 64) >> 16;
			break;
		case 128:
			dx = nx = MOVEx(128, ang);
			dy = ny = MOVEy(128, ang);
			nz = (z_amt_per_pixel * 128) >> 16;
			break;
		case 256:
			dx = nx = MOVEx(256, ang);
			dy = ny = MOVEy(256, ang);
			nz = (z_amt_per_pixel * 256) >> 16;
			break;
		case 512:
			dx = nx = MOVEx(512, ang);
			dy = ny = MOVEy(512, ang);
			nz = (z_amt_per_pixel * 512) >> 16;
			break;
		default:
			dx = nx = MOVEx(tu.motion_blur_dist, ang);
			dy = ny = MOVEy(tu.motion_blur_dist, ang);
			break;
		}

		dz = nz;

		xrepeat = tsp.xrepeat;
		yrepeat = tsp.yrepeat;

		switch (DTEST(tu.Flags2, SPR2_BLUR_TAPER)) {
		case 0:
			repeat_adj = 0;
			break;
		case SPR2_BLUR_TAPER_SLOW:
			repeat_adj = (short) (xrepeat / (tu.motion_blur_num * 2));
			break;
		case SPR2_BLUR_TAPER_FAST:
			repeat_adj = (short) (xrepeat / tu.motion_blur_num);
			break;
		}

		for (i = 0; i < tu.motion_blur_num; i++) {
			if(spritesortcnt >= MAXSPRITESONSCREEN)
				return;

			if (tsprite[spritesortcnt] == null)
				tsprite[spritesortcnt] = new SPRITE();
			newsp = tsprite[spritesortcnt];
			newsp.set(tsp);
			newsp.cstat |= (CSTAT_SPRITE_TRANSLUCENT | CSTAT_SPRITE_TRANS_FLIP);

			newsp.x += dx;
			newsp.y += dy;
			dx += nx;
			dy += ny;

			newsp.z += dz;
			dz += nz;

			newsp.xrepeat = xrepeat;
			newsp.yrepeat = yrepeat;

			xrepeat -= repeat_adj;
			yrepeat -= repeat_adj;

			spritesortcnt++;
		}

	}

	public static void SetVoxelSprite(SPRITE sp, short pic) {
		sp.cstat |= (CSTAT_SPRITE_SLAB);
		sp.picnum = pic;
	}

	public static void WarpCopySprite() {
		SPRITE newsp, sp1, sp2, sp;
		short sn, nsn;
		short sn2, nsn2;
		short spnum, next_spnum;
		int xoff, yoff, zoff;
		short match;
		short sect1, sect2;

		// look for the first one
		for (sn = headspritestat[STAT_WARP_COPY_SPRITE1]; sn != -1; sn = nsn) {
			nsn = nextspritestat[sn];
			sp1 = sprite[sn];
			match = sp1.lotag;

			// look for the second one
			for (sn2 = headspritestat[STAT_WARP_COPY_SPRITE2]; sn2 != -1; sn2 = nsn2) {
				nsn2 = nextspritestat[sn2];
				sp = sprite[sn2];

				if (sp.lotag == match) {
					sp2 = sp;
					sect1 = sp1.sectnum;
					sect2 = sp2.sectnum;

					for (spnum = headspritesect[sect1]; spnum != -1; spnum = next_spnum) {
						next_spnum = nextspritesect[spnum];
						if (sprite[spnum] == sp1)
							continue;

						if (sprite[spnum].picnum == ST1)
							continue;

						if(spritesortcnt >= MAXSPRITESONSCREEN)
							continue;

						if (tsprite[spritesortcnt] == null)
							tsprite[spritesortcnt] = new SPRITE();
						newsp = tsprite[spritesortcnt];
						newsp.set(sprite[spnum]);

						spritesortcnt++;
						newsp.owner = spnum;
						newsp.statnum = 0;

						xoff = sp1.x - newsp.x;
						yoff = sp1.y - newsp.y;
						zoff = sp1.z - newsp.z;

						ILoc oldLoc = game.pInt.getsprinterpolate(spnum);
						if (oldLoc != null && !bCopySpriteOffs[spnum]) {
							oldLoc.x += (sp2.x - sp1.x);
							oldLoc.y += (sp2.y - sp1.y);
							oldLoc.z += (sp2.z - sp1.z);
							bCopySpriteOffs[spnum] = true;
						}

						newsp.x = sp2.x - xoff;
						newsp.y = sp2.y - yoff;
						newsp.z = sp2.z - zoff;
						newsp.sectnum = sp2.sectnum;
					}

					for (spnum = headspritesect[sect2]; spnum != -1; spnum = next_spnum) {
						next_spnum = nextspritesect[spnum];
						if (sprite[spnum] == sp2)
							continue;

						if (sprite[spnum].picnum == ST1)
							continue;

						if(spritesortcnt >= MAXSPRITESONSCREEN)
							continue;

						if (tsprite[spritesortcnt] == null)
							tsprite[spritesortcnt] = new SPRITE();

						newsp = tsprite[spritesortcnt];
						newsp.set(sprite[spnum]);
						spritesortcnt++;
						newsp.owner = spnum;
						newsp.statnum = 0;

						xoff = sp2.x - newsp.x;
						yoff = sp2.y - newsp.y;
						zoff = sp2.z - newsp.z;

						ILoc oldLoc = game.pInt.getsprinterpolate(spnum);
						if (oldLoc != null && !bCopySpriteOffs[spnum]) {
							oldLoc.x += (sp2.x - sp1.x);
							oldLoc.y += (sp2.y - sp1.y);
							oldLoc.z += (sp2.z - sp1.z);
							bCopySpriteOffs[spnum] = true;
						}

						newsp.x = sp1.x - xoff;
						newsp.y = sp1.y - yoff;
						newsp.z = sp1.z - zoff;
						newsp.sectnum = sp1.sectnum;
					}
				}
			}
		}
	}

	public static void DoStarView(SPRITE tsp, USER tu, int viewz) {
		int zdiff = viewz - tsp.z;

		if (klabs(zdiff) > Z(24)) {
			if (tu.StateStart == s_StarStuck[0])
				tsp.picnum = s_StarDownStuck[tu.State.id - s_StarStuck[0].id].Pic;
			else
				tsp.picnum = s_StarDown[tu.State.id - s_Star[0].id].Pic;

			if (zdiff > 0)
				tsp.cstat |= (CSTAT_SPRITE_YFLIP);
		} else {
			if (zdiff > 0)
				tsp.cstat |= (CSTAT_SPRITE_YFLIP);
		}
	}

	private static int ang = 0;

	public static void analyzesprites(int viewx, int viewy, int viewz, boolean mirror, int smoothratio) {
		int tSpriteNum;
		short SpriteNum;
		long smr4, smr2;
		SPRITE tsp;
		USER tu;

		PlayerStr pp = Player[screenpeek];
		short newshade = 0;

		ang = NORM_ANGLE(ang + 12);

		smr4 = smoothratio + (MoveSkip4 << 16);
		smr2 = smoothratio + ((MoveSkip2 ? 1 : 0) << 16);

		for (tSpriteNum = spritesortcnt - 1; tSpriteNum >= 0; tSpriteNum--) {
			SpriteNum = tsprite[tSpriteNum].owner;
			tsp = tsprite[tSpriteNum];
			tu = pUser[SpriteNum];

			// don't draw these
			if (tsp.statnum >= STAT_DONT_DRAW) {
				tsp.owner = -1;
				continue;
			}

			// Diss any parentally locked sprites
			if (gs.ParentalLock || Global_PLock) {
				if (aVoxelArray[tsp.picnum].Parental == 6145) {
					tsp.owner = -1;
					tu = null;
				} else if (aVoxelArray[tsp.picnum].Parental > 0) {
					tsp.picnum = aVoxelArray[tsp.picnum].Parental; // Change the pic
				}
			}

			if (mirror) {
				// only interpolate certain moving things
				game.pInt.dospriteinterp(tsp, smoothratio);
				game.pIntSkip2.dospriteinterp(tsp, smoothratio);
				game.pIntSkip4.dospriteinterp(tsp, smoothratio);
			}

			if (tu != null) {
				if (tsp.statnum != STAT_DEFAULT) {
					if (TEST(tu.Flags, SPR_SKIP4)) {
						if (tsp.statnum <= STAT_SKIP4_INTERP_END) {
							tsp.x = tu.ox + mulscale(tsp.x - tu.ox, smr4, 18);
							tsp.y = tu.oy + mulscale(tsp.y - tu.oy, smr4, 18);
							tsp.z = tu.oz + mulscale(tsp.z - tu.oz, smr4, 18);
						}
					}

					if (TEST(tu.Flags, SPR_SKIP2)) {
						if (tsp.statnum <= STAT_SKIP2_INTERP_END) {
							tsp.x = tu.ox + mulscale(tsp.x - tu.ox, smr2, 17);
							tsp.y = tu.oy + mulscale(tsp.y - tu.oy, smr2, 17);
							tsp.z = tu.oz + mulscale(tsp.z - tu.oz, smr2, 17);
						}
					}
				}

				if (gs.Shadows && TEST(tu.Flags, SPR_SHADOW)) {
					DoShadows(tsp, viewx, viewy, viewz);
				}

				if (gs.UseDarts)
					if (tu.ID == 1793 || tsp.picnum == 1793) {
						tsp.picnum = 2519;
						tsp.xrepeat = 27;
						tsp.yrepeat = 29;
					}

				if (tu.ID == STAR1) {
					if (gs.UseDarts) {
						tsp.picnum = DART_PIC;
						tsp.ang = NORM_ANGLE(tsp.ang - 512 - 24);
						tsp.xrepeat = tsp.yrepeat = DART_REPEAT;
						tsp.cstat |= CSTAT_SPRITE_WALL;
					} else
						DoStarView(tsp, tu, viewz);
				} else if (tu.ID == BETTY_R0) { // Mine fix
					int fz = engine.getflorzofslope(tsp.sectnum, tsp.x, tsp.y);
					if (tsp.z > fz)
						tsp.z = fz;
				}

				// rotation
				if (tu.RotNum > 0)
					SetActorRotation(tSpriteNum, viewx, viewy);

				if (tu.motion_blur_num != 0) {
					DoMotionBlur(tsp);
				}

				// set palette lookup correctly
				if (tsp.pal != sector[tsp.sectnum].floorpal) {
					if (sector[tsp.sectnum].floorpal == PALETTE_DEFAULT) {
						// default pal for sprite is stored in tu.spal
						// mostly for players and other monster types
						tsp.pal = tu.spal;
					} else {
						// if sector pal is something other than default
						Sect_User sectu = SectUser[tsp.sectnum];
						short pal = sector[tsp.sectnum].floorpal;
						boolean nosectpal = false;

						// sprite does not take on the new pal if sector flag is set
						if (sectu != null && TEST(sectu.flags, SECTFU_DONT_COPY_PALETTE)) {
							pal = PALETTE_DEFAULT;
							nosectpal = true;
						}

						// if(tu.spal == PALETTE_DEFAULT)
						if (tsp.hitag != SECTFU_DONT_COPY_PALETTE && tsp.hitag != LUMINOUS && !nosectpal
								&& pal != PALETTE_FOG && pal != PALETTE_DIVE && pal != PALETTE_DIVE_LAVA)
							tsp.pal = pal;
						else
							tsp.pal = tu.spal;

					}
				}

				// Sprite debug information mode
				if (tsp.hitag == 9997) {
					tsp.pal = PALETTE_RED_LIGHTING;
					// Turn it off, it gets reset by PrintSpriteInfo
					sprite[tu.SpriteNum].hitag = 0;
				}
			}

			if (gs.UseDarts)
				if (tsp.statnum == STAT_STAR_QUEUE) {
					tsp.picnum = DART_PIC;
					tsp.ang = NORM_ANGLE(tsp.ang - 512);
					tsp.xrepeat = tsp.yrepeat = DART_REPEAT;
					tsp.cstat |= CSTAT_SPRITE_WALL;
				}

			// Call my sprite handler
			// Does autosizing and voxel handling
			JAnalyzeSprites(tsp);

			// only do this of you are a player sprite
			if (tu != null && tu.PlayerP != -1) {
				// Shadow spell
				if (!TEST(tsp.cstat, CSTAT_SPRITE_TRANSLUCENT))
					ShadeSprite(tsp);

				// sw if its your playersprite
				if (Player[screenpeek].PlayerSprite == tu.SpriteNum) {
					pp = Player[screenpeek];
					if (mirror || TEST(pp.Flags, PF_VIEW_FROM_OUTSIDE | PF_VIEW_FROM_CAMERA)) {
						if (TEST(pp.Flags, PF_VIEW_FROM_OUTSIDE))
							tsp.cstat |= (CSTAT_SPRITE_TRANSLUCENT);

						if (TEST(pp.Flags, PF_CLIMBING)) {
							// move sprite forward some so he looks like he's
							// climbing
							tsp.x = pp.six + MOVEx(128 + 80, tsp.ang);
							tsp.y = pp.siy + MOVEy(128 + 80, tsp.ang);
						} else {
							tsp.x = pp.six;
							tsp.y = pp.siy;
						}

						tsp.z = tsp.z + pp.siz;
						tsp.ang = pp.siang;
						// continue;
					} else {
						// dont draw your sprite
						tsp.owner = -1;
						// SET(tsp.cstat, CSTAT_SPRITE_INVISIBLE);
					}
				}

				int tx = tsp.x - viewx;
				int ty = tsp.y - viewy;
				int angle = ((1024 + engine.getangle(tx, ty) - pp.getAnglei()) & 0x7FF) - 1024;
				long dist = engine.qdist(tx, ty);

				if (klabs(mulscale(angle, dist, 14)) < 4) {
					int horizoff = 100 - pp.getHorizi();
					long z1 = mulscale(dist, horizoff, 3) + viewz;

					int zTop = tsp.z;
					int zBot = zTop;
					Tile pic = engine.getTile(tsp.picnum);
					int yoffs = pic.getOffsetY();
					zTop -= (yoffs + pic.getHeight()) * (tsp.yrepeat << 2);
					zBot += -yoffs * (tsp.yrepeat << 2);

					if ((z1 < zBot) && (z1 > zTop)) {
						if (engine.cansee(viewx, viewy, viewz, pp.getSprite().sectnum, tsp.x, tsp.y, tsp.z,
								tsp.sectnum))
							gPlayerIndex = tu.PlayerP;
					}
				}
			}

			if (OverlapDraw && FAF_ConnectArea(tsp.sectnum) && tsp.owner >= 0) {
				ConnectCopySprite(tsp);
			}

			//
			// kens original sprite shade code he moved out of the engine
			//

			switch (tsp.statnum) {
			case STAT_ENEMY:
			case STAT_DEAD_ACTOR:
			case STAT_FAF_COPY:
				break;
			default:
				newshade = tsp.shade;
				newshade += 6;
				if (newshade > 127)
					newshade = 127;
				tsp.shade = (byte) newshade;
			}

			if (TEST(sector[tsp.sectnum].ceilingstat, CEILING_STAT_PLAX)) {
				newshade = tsp.shade;
				newshade += sector[tsp.sectnum].ceilingshade;
				if (newshade > 127)
					newshade = 127;
				if (newshade < -128)
					newshade = -128;
				tsp.shade = (byte) newshade;
				;
			} else {
				newshade = tsp.shade;
				newshade += sector[tsp.sectnum].floorshade;
				if (newshade > 127)
					newshade = 127;
				if (newshade < -128)
					newshade = -128;
				tsp.shade = (byte) newshade;
			}

			if (tsp.hitag == 9998)
				tsp.shade = 127; // Invisible enemy ninjas

			// Correct shades for luminous sprites
			if (tsp.hitag == LUMINOUS) {
				tsp.shade = -128;
			}

			if (pp.NightVision && TEST(tsp.extra, SPRX_PLAYER_OR_ENEMY)) {
				if (tu != null && tu.ID == TRASHCAN)
					continue; // Don't light up trashcan

				tsp.pal = PALETTE_ILLUMINATE; // Make sprites REALLY bright green.
				tsp.shade = -128;
			}

			if (tu != null && tu.PlayerP != -1) {
				if (TEST(tu.Flags2, SPR2_VIS_SHADING)) {
					if (Player[screenpeek].PlayerSprite != tu.SpriteNum) {
						if (!TEST(Player[tu.PlayerP].Flags, PF_VIEW_FROM_OUTSIDE)) {
							tsp.cstat &= ~(CSTAT_SPRITE_TRANSLUCENT);
						}
					}

					tsp.shade = (byte) (12 - STD_RANDOM_RANGE(30));
				}
			}
		}

		WarpCopySprite();
	}

	private static void show_weapon(USER pp, SPRITE tspr)
	{
		if(spritesortcnt >= MAXSPRITESONSCREEN)
			return;

		viewWeaponTile = new short[] { -1, ICON_STAR, ICON_SHOTGUN, ICON_UZI, ICON_MICRO_GUN, ICON_GRENADE_LAUNCHER, 2223, ICON_RAIL_GUN, ICON_GUARD_HEAD, ICON_HEART, -1, -1, -1, -1 };

		int WeaponNum = pp.WeaponNum;
		if (viewWeaponTile[WeaponNum] == -1)
			return;

		if (tsprite[spritesortcnt] == null)
			tsprite[spritesortcnt] = new SPRITE();
		tsprite[spritesortcnt].set(tspr);

		SPRITE pTEffect = tsprite[spritesortcnt];
		pTEffect.z = tspr.z - 0x5000;
		pTEffect.cstat = 0;
		pTEffect.ang += 512;
		pTEffect.ang &= 0x7FF;
		pTEffect.picnum = viewWeaponTile[WeaponNum];
		pTEffect.shade = tspr.shade;
		pTEffect.xrepeat = 32;
		pTEffect.yrepeat = 32;

		spritesortcnt++;
	}

	public static SPRITE get_tsprite(int SpriteNum) {
		for (int tSpriteNum = spritesortcnt - 1; tSpriteNum >= 0; tSpriteNum--) {
			if (tsprite[tSpriteNum].owner == SpriteNum)
				return (tsprite[tSpriteNum]);
		}

		return null;
	}

	public static void post_analyzesprites(int smoothratio) {
		int tSpriteNum;
		short SpriteNum;
		SPRITE tsp;
		USER tu;

		for (tSpriteNum = spritesortcnt - 1; tSpriteNum >= 0; tSpriteNum--) {
			SpriteNum = tsprite[tSpriteNum].owner;
			if (SpriteNum == -1)
				continue;
			tsp = tsprite[tSpriteNum];
			tu = pUser[SpriteNum];

			// only interpolate certain moving things
			game.pInt.dospriteinterp(tsp, smoothratio);
			game.pIntSkip2.dospriteinterp(tsp, smoothratio);
			game.pIntSkip4.dospriteinterp(tsp, smoothratio);

			if (tu != null) {
				if (tu.ID == FIREBALL_FLAMES && tu.Attach >= 0) {
					// SPRITEp atsp = &sprite[tu.Attach];
					SPRITE atsp;

					atsp = get_tsprite(tu.Attach);

					if (atsp == null) {
						continue;
					}

					tsp.x = atsp.x;
					tsp.y = atsp.y;
					// statnum is priority - draw this ALWAYS first at 0
					// statnum is priority - draw this ALWAYS last at MAXSTATUS
					if (TEST(atsp.extra, SPRX_BURNABLE)) {
						atsp.statnum = 1;
						tsp.statnum = 0;
					} else
						tsp.statnum = MAXSTATUS;
				}

				if (tu.PlayerP != -1 && tu.PlayerP != screenpeek) {
					if (gs.ShowWeapon && gNet.MultiGameType != MultiGameTypes.MULTI_GAME_NONE) {
						show_weapon(tu, tsp);
					}
				}
			}
		}
	}

	public static void BackView(LONGp nx, LONGp ny, LONGp nz, LONGp vsect, LONGp nang, int horiz) {
		SPRITE sp;
		int i, vx, vy, vz, hx, hy;
		short bakcstat, daang;
		PlayerStr pp = Player[screenpeek];
		short ang = (short) (nang.value + pp.view_outside_dang);

		// Calculate the vector (nx,ny,nz) to shoot backwards
		vx = (sintable[NORM_ANGLE(ang + 1536)] >> 3);
		vy = (sintable[NORM_ANGLE(ang + 1024)] >> 3);
		vz = (horiz - 100) * 256;

		// Player sprite of current view
		sp = sprite[pp.PlayerSprite];

		bakcstat = sp.cstat;
		sp.cstat &= ~(CSTAT_SPRITE_BLOCK | CSTAT_SPRITE_BLOCK_HITSCAN);

		// Make sure sector passed to FAFhitscan is correct
		// COVERupdatesector(*nx, *ny, vsect);

		engine.hitscan(nx.value, ny.value, nz.value, (short) vsect.value, vx, vy, vz, pHitInfo, CLIPMASK_PLAYER);

		sp.cstat = bakcstat; // Restore cstat

		hx = pHitInfo.hitx - (nx.value);
		hy = pHitInfo.hity - (ny.value);

		// If something is in the way, make pp.camera_dist lower if necessary
		if (klabs(vx) + klabs(vy) > klabs(hx) + klabs(hy)) {
			if (pHitInfo.hitwall >= 0) // Push you a little bit off the wall
			{
				vsect.value = pHitInfo.hitsect;

				daang = engine.getangle(wall[wall[pHitInfo.hitwall].point2].x - wall[pHitInfo.hitwall].x,
						wall[wall[pHitInfo.hitwall].point2].y - wall[pHitInfo.hitwall].y);

				i = vx * sintable[daang] + vy * sintable[NORM_ANGLE(daang + 1536)];
				if (klabs(vx) > klabs(vy))
					hx -= mulscale(vx, i, 28);
				else
					hy -= mulscale(vy, i, 28);
			} else if (pHitInfo.hitsprite < 0) // Push you off the ceiling/floor
			{
				vsect.value = pHitInfo.hitsect;

				if (klabs(vx) > klabs(vy))
					hx -= (vx >> 5);
				else
					hy -= (vy >> 5);
			} else {
				SPRITE hsp = sprite[pHitInfo.hitsprite];
				short flag_backup;

				// if you hit a sprite that's not a wall sprite - try again
				if (!TEST(hsp.cstat, CSTAT_SPRITE_WALL)) {
					flag_backup = hsp.cstat;
					hsp.cstat &= ~(CSTAT_SPRITE_BLOCK | CSTAT_SPRITE_BLOCK_HITSCAN);

					BackView(nx, ny, nz, vsect, nang, horiz);
					hsp.cstat = flag_backup;
					return;
				} else {
					// same as wall calculation
					daang = NORM_ANGLE(sp.ang - 512);

					i = vx * sintable[daang] + vy * sintable[NORM_ANGLE(daang + 1536)];
					if (klabs(vx) > klabs(vy))
						hx -= mulscale(vx, i, 28);
					else
						hy -= mulscale(vy, i, 28);
				}

			}

			if (klabs(vx) > klabs(vy))
				i = (hx << 16) / vx;
			else
				i = (hy << 16) / vy;

			if (i < pp.camera_dist)
				pp.camera_dist = i;
		}

		// Actually move you! (Camerdist is 65536 if nothing is in the way)
		nx.value += mulscale(vx, pp.camera_dist, 16);
		ny.value += mulscale(vy, pp.camera_dist, 16);
		nz.value += mulscale(vz, pp.camera_dist, 16);

		// Slowly increase pp.camera_dist until it reaches 65536
		// Synctics is a timer variable so it increases the same rate
		// on all speed computers
		pp.camera_dist = Math.min(pp.camera_dist + (3 << 10), 65536);

		// Make sure vsect is correct
		vsect.value = engine.updatesectorz(nx.value, ny.value, nz.value, (short) vsect.value);

		nang.value = ang;
	}

	public static void CircleCamera(LONGp nx, LONGp ny, LONGp nz, LONGp vsect, LONGp nang, int horiz) {
		SPRITE sp;
		int i, vx, vy, vz, hx, hy;
		short bakcstat, daang;
		PlayerStr pp = Player[screenpeek];
		short ang = (short) (nang.value + pp.circle_camera_ang);

		// Calculate the vector (nx,ny,nz) to shoot backwards
		vx = (sintable[NORM_ANGLE(ang + 1536)] >> 4);
		vy = (sintable[NORM_ANGLE(ang + 1024)] >> 4);

		// lengthen the vector some
		vx += DIV2(vx);
		vy += DIV2(vy);

		vz = (horiz - 100) * 256;

		// Player sprite of current view
		sp = sprite[pp.PlayerSprite];

		bakcstat = sp.cstat;
		sp.cstat &= ~(CSTAT_SPRITE_BLOCK | CSTAT_SPRITE_BLOCK_HITSCAN);

		// Make sure sector passed to hitscan is correct
		// COVERupdatesector(*nx, *ny, vsect);

		engine.hitscan(nx.value, ny.value, nz.value, (short) vsect.value, vx, vy, vz, pHitInfo, CLIPMASK_MISSILE);

		sp.cstat = bakcstat; // Restore cstat
		// ASSERT(hitsect >= 0);

		hx = pHitInfo.hitx - (nx.value);
		hy = pHitInfo.hity - (ny.value);

		// If something is in the way, make pp.circle_camera_dist lower if necessary
		if (klabs(vx) + klabs(vy) > klabs(hx) + klabs(hy)) {
			if (pHitInfo.hitwall >= 0) // Push you a little bit off the wall
			{
				vsect.value = pHitInfo.hitsect;

				daang = engine.getangle(wall[wall[pHitInfo.hitwall].point2].x - wall[pHitInfo.hitwall].x,
						wall[wall[pHitInfo.hitwall].point2].y - wall[pHitInfo.hitwall].y);

				i = vx * sintable[daang] + vy * sintable[NORM_ANGLE(daang + 1536)];
				if (klabs(vx) > klabs(vy))
					hx -= mulscale(vx, i, 28);
				else
					hy -= mulscale(vy, i, 28);
			} else if (pHitInfo.hitsprite < 0) // Push you off the ceiling/floor
			{
				vsect.value = pHitInfo.hitsect;

				if (klabs(vx) > klabs(vy))
					hx -= (vx >> 5);
				else
					hy -= (vy >> 5);
			} else {
				SPRITE hsp = sprite[pHitInfo.hitsprite];
				short flag_backup;

				// if you hit a sprite that's not a wall sprite - try again
				if (!TEST(hsp.cstat, CSTAT_SPRITE_WALL)) {
					flag_backup = hsp.cstat;
					hsp.cstat &= ~(CSTAT_SPRITE_BLOCK | CSTAT_SPRITE_BLOCK_HITSCAN);

					CircleCamera(nx, ny, nz, vsect, nang, horiz);
					hsp.cstat = flag_backup;
					return;
				}
			}

			if (klabs(vx) > klabs(vy))
				i = (hx << 16) / vx;
			else
				i = (hy << 16) / vy;

			if (i < pp.circle_camera_dist)
				pp.circle_camera_dist = i;
		}

		// Actually move you! (Camerdist is 65536 if nothing is in the way)
		nx.value += ((vx * pp.circle_camera_dist) >> 16);
		ny.value += ((vy * pp.circle_camera_dist) >> 16);
		nz.value += ((vz * pp.circle_camera_dist) >> 16);

		// Slowly increase pp.circle_camera_dist until it reaches 65536
		// Synctics is a timer variable so it increases the same rate
		// on all speed computers
		pp.circle_camera_dist = Math.min(pp.circle_camera_dist + (3 << 8), 65536);

		// Make sure vsect is correct
		vsect.value = engine.updatesectorz(nx.value, ny.value, nz.value, (short) vsect.value);

		nang.value = ang;
	}

	private static final char[] txt_buffer = new char[256];

	public static void PrintLocationInfo(PlayerStr pp) {
		int x = windowx1 + 5;
		int y = windowy1 + 5;

		if (LocationInfo != 0) {
			buildString(txt_buffer, 0, "FPS: ", BuildGdx.graphics.getFramesPerSecond());
			engine.printext256(x, y, 1, -1, txt_buffer, 1, 1.0f);

			if (LocationInfo > 1) {
				y += 7;

				buildString(txt_buffer, 0, "POSX: ", pp.posx);
				engine.printext256(x, y, 1, -1, txt_buffer, 1, 1.0f);
				y += 7;
				buildString(txt_buffer, 0, "POSY: ", pp.posy);
				engine.printext256(x, y, 1, -1, txt_buffer, 1, 1.0f);
				y += 7;
				buildString(txt_buffer, 0, "POSZ: ", pp.posz);
				engine.printext256(x, y, 1, -1, txt_buffer, 1, 1.0f);
				y += 7;
				buildString(txt_buffer, 0, "ANG: ", pp.getAnglei());
				engine.printext256(x, y, 1, -1, txt_buffer, 1, 1.0f);
				y += 7;
			}
		}
	}

	public static void SecretInfo(PlayerStr pp, int zoom) {
		int x = 5;
		int y = 150;
		if (gs.BorderNum <= BORDER_BAR - 2)
			y = 170;

		if (gs.Stats == 0 || gs.BorderNum == 0)
			return;

		boolean shadows = true;
		float viewzoom = (zoom / 65536.0f);
		BuildFont f = game.getFont(0);

		buildString(txt_buffer, 0, "Kills ");
		int alignx = f.getWidth(txt_buffer);

		int yoffset = (int) (3.2f * f.getHeight() * viewzoom);
		y -= yoffset;

		if (gNet.MultiGameType == MultiGameTypes.MULTI_GAME_COMMBAT)
			y += 8 * viewzoom;

		int statx = x;
		int staty = y;

		f.drawText(statx, staty, txt_buffer, zoom, 0, PAL_XLAT_BRIGHT_GREEN, TextAlign.Left, 10 | 256, shadows);

		int offs = Bitoa(gNet.MultiGameType == MultiGameTypes.MULTI_GAME_COMMBAT ? Player[myconnectindex].Kills : Kills,
				txt_buffer);
		if (gNet.MultiGameType != MultiGameTypes.MULTI_GAME_COMMBAT)
			offs = buildString(txt_buffer, offs, "/", TotalKillable);
		f.drawText(statx += (alignx + 2) * viewzoom, staty, txt_buffer, zoom, 0, PAL_XLAT_BROWN, TextAlign.Left,
				10 | 256, shadows);

		statx = x;
		staty = y + (int) (8 * viewzoom);

		if (gNet.MultiGameType != MultiGameTypes.MULTI_GAME_COMMBAT) {
			buildString(txt_buffer, 0, "Secrets ");
			f.drawText(statx, staty, txt_buffer, zoom, 0, PAL_XLAT_BRIGHT_GREEN, TextAlign.Left, 10 | 256, shadows);
			alignx = f.getWidth(txt_buffer);
			offs = Bitoa(Player[connecthead].SecretsFound, txt_buffer);
			offs = buildString(txt_buffer, offs, " / ", LevelSecrets);
			f.drawText(statx += (alignx + 2) * viewzoom, staty, txt_buffer, zoom, 0, PAL_XLAT_BROWN, TextAlign.Left,
					10 | 256, shadows);

			statx = x;
			staty = y + (int) (16 * viewzoom);
		}

		buildString(txt_buffer, 0, "Time ");
		f.drawText(statx, staty, txt_buffer, zoom, 0, PAL_XLAT_BRIGHT_GREEN, TextAlign.Left, 10 | 256, shadows);
		alignx = f.getWidth(txt_buffer);

		int second_tics = (PlayClock / 120);
		int minutes = (second_tics / 60);
		int sec = (second_tics % 60);

		offs = Bitoa(minutes, txt_buffer, 2);
		offs = buildString(txt_buffer, offs, ":", sec, 2);
		f.drawText(statx += (alignx + 2) * viewzoom, staty, txt_buffer, zoom, 0, PAL_XLAT_BROWN, TextAlign.Left,
				10 | 256, shadows);

    }

	public static void SpriteSortList2D(int tx, int ty) {
		spritesortcnt = 0;
		for (int i = 0; i < MAXSPRITES; i++) {
			if (sprite[i].statnum < MAXSTATUS) {
				SPRITE sp = sprite[i];

				if (!TEST(sp.cstat, CSTAT_SPRITE_INVISIBLE) && (sp.xrepeat > 0) && (sp.yrepeat > 0)
						&& (spritesortcnt < MAXSPRITESONSCREEN)) {
					if (DISTANCE(tx, ty, sp.x, sp.y) < 22000) {
						if (tsprite[spritesortcnt] == null)
							tsprite[spritesortcnt] = new SPRITE();
						tsprite[spritesortcnt].set(sp);
						tsprite[spritesortcnt++].owner = (short) i;
					}
				}
			}
		}
	}

	public static void COVERsetgamemode(int mode, int xdim, int ydim) {
		ScreenHeight = ydim;
		ScreenWidth = xdim;
		ScreenMode = mode;

		engine.setgamemode(mode, xdim, ydim);
	}

	public static void DrawMessageInput(PlayerStr pp) {
		if (Console.IsShown())
			return;

		// Used to make cursor fade in and out
		int c = 4 - (sintable[(totalclock << 4) & 2047] >> 11);

		if (MessageInputMode) {
			char[] buf = getInput().getMessageBuffer();
			int len = getInput().getMessageLength() + 1;
			if (len < buf.length)
				buf[len] = 0;

			int alignx = game.getFont(0).drawText(320 >> 1, MESSAGE_LINE, getInput().getMessageBuffer(), 0, 4,
					TextAlign.Center, ROTATE_SPRITE_SCREEN_CLIP, true);
			engine.rotatesprite((((320 + alignx + 16) >> 1)) << 16, (MESSAGE_LINE + 2) << 16, 20000, 0,
					COINCURSOR + ((totalclock >> 3) % 7), c, 0, ROTATE_SPRITE_SCREEN_CLIP, 0, 0, xdim - 1, ydim - 1);
		}
	}

	public static void DrawCrosshair(PlayerStr pp) {

		if (!gs.Crosshair)
			return;

		if (DemoPlaying || CameraTestMode)
			return;

		if (TEST(pp.Flags, PF_VIEW_FROM_OUTSIDE))
			return;

		if (dimensionmode == 6)
			return;

		int y = 100;
		if (gs.BorderNum >= BORDER_BAR)
			y -= 24;

		y += (klabs(pp.lookang) / 9);

		engine.rotatesprite(158 - (pp.lookang >> 1) << 16, y << 16, gs.CrosshairSize, 0, 2326, 10, 0, ROTATE_SPRITE_SCREEN_CLIP, 0, 0,
				xdim - 1, ydim - 1);
	}

	public static void CameraView(PlayerStr pp, LONGp tx, LONGp ty, LONGp tz, LONGp tsectnum, LONGp tang,
			LONGp thoriz) {
		int i, nexti;
		short ang;
		SPRITE sp;
		boolean found_camera = false;
		boolean player_in_camera = false;
		boolean FAFcansee_test;
		boolean ang_test;

		if (pp == Player[screenpeek]) {
			for (i = headspritestat[STAT_DEMO_CAMERA]; i != -1; i = nexti) {
				nexti = nextspritestat[i];
				sp = sprite[i];

				ang = engine.getangle(tx.value - sp.x, ty.value - sp.y);
				ang_test = GetDeltaAngle(sp.ang, ang) < sp.lotag;

				FAFcansee_test = (FAFcansee(sp.x, sp.y, sp.z, sp.sectnum, tx.value, ty.value, tz.value, pp.cursectnum)
						|| FAFcansee(sp.x, sp.y, sp.z, sp.sectnum, tx.value, ty.value,
								tz.value + SPRITEp_SIZE_Z(pp.getSprite()), pp.cursectnum));

				player_in_camera = ang_test && FAFcansee_test;

				if (player_in_camera || pp.camera_check_time_delay > 0) {

					// if your not in the camera but are still looking
					// make sure that only the last camera shows you

					if (!player_in_camera && pp.camera_check_time_delay > 0) {
						if (pp.last_camera_sp != i)
							continue;
					}

					switch (sp.clipdist) {
					case 1:
						pp.last_camera_sp = i;
						CircleCamera(tx, ty, tz, tsectnum, tang, 100);
						found_camera = true;
						break;

					default: {
						int xvect, yvect, zvect, zdiff;

						pp.last_camera_sp = i;

						xvect = sintable[NORM_ANGLE(ang + 512)] >> 3;
						yvect = sintable[NORM_ANGLE(ang)] >> 3;

						zdiff = sp.z - tz.value;
						if (klabs(sp.x - tx.value) > 1000)
							zvect = scale(xvect, zdiff, sp.x - tx.value);
						else if (klabs(sp.y - ty.value) > 1000)
							zvect = scale(yvect, zdiff, sp.y - ty.value);
						else if (sp.x - tx.value != 0)
							zvect = scale(xvect, zdiff, sp.x - tx.value);
						else if (sp.y - ty.value != 0)
							zvect = scale(yvect, zdiff, sp.y - ty.value);
						else
							zvect = 0;

						// new horiz to player
						thoriz.value = 100 - (zvect / 256);
						thoriz.value = Math.max(thoriz.value, PLAYER_HORIZ_MIN);
						thoriz.value = Math.min(thoriz.value, PLAYER_HORIZ_MAX);

						tang.value = ang;
						tx.value = sp.x;
						ty.value = sp.y;
						tz.value = sp.z;
						tsectnum.value = sp.sectnum;

						found_camera = true;
						break;
					}
					}
				}

				if (found_camera)
					break;
			}
		}

		// if you player_in_camera you definately have a camera
		if (player_in_camera) {
			pp.camera_check_time_delay = 120 / 2;
			pp.Flags |= (PF_VIEW_FROM_CAMERA);
		} else
		// if you !player_in_camera you still might have a camera
		// for a split second
		{
			if (found_camera) {
				pp.Flags |= (PF_VIEW_FROM_CAMERA);
			} else {
				pp.circle_camera_ang = 0;
				pp.circle_camera_dist = CIRCLE_CAMERA_DIST_MIN;
				pp.Flags &= ~(PF_VIEW_FROM_CAMERA);
			}
		}
	}

	public static void PreDraw() {
		short i, nexti;

		PreDrawStackedWater();

		for (i = headspritestat[STAT_FLOOR_SLOPE_DONT_DRAW]; i != -1; i = nexti) {
			nexti = nextspritestat[i];
			sector[sprite[i].sectnum].floorstat &= ~(FLOOR_STAT_SLOPE);
		}
	}

	public static void PostDraw() {
		short i, nexti;
		for (i = headspritestat[STAT_FLOOR_SLOPE_DONT_DRAW]; i != -1; i = nexti) {
			nexti = nextspritestat[i];
			sector[sprite[i].sectnum].floorstat |= (FLOOR_STAT_SLOPE);
		}

		for (i = headspritestat[STAT_FAF_COPY]; i != -1; i = nexti) {
			nexti = nextspritestat[i];
			if (pUser[i] != null)
				pUser[i] = null;

			engine.deletesprite(i);
		}
	}

	public static int CopySprite(SPRITE tsp, short newsector) {
		int newsp;
		SPRITE sp;

		newsp = COVERinsertsprite(newsector, STAT_FAF_COPY);
		sp = sprite[newsp];

		sp.x = tsp.x;
		sp.y = tsp.y;
		sp.z = tsp.z;
		sp.cstat = tsp.cstat;
		sp.picnum = tsp.picnum;
		sp.pal = tsp.pal;
		sp.xrepeat = tsp.xrepeat;
		sp.yrepeat = tsp.yrepeat;
		sp.xoffset = tsp.xoffset;
		sp.yoffset = tsp.yoffset;
		sp.ang = tsp.ang;
		sp.xvel = tsp.xvel;
		sp.yvel = tsp.yvel;
		sp.zvel = tsp.zvel;
		sp.shade = tsp.shade;

		sp.cstat &= ~(CSTAT_SPRITE_BLOCK | CSTAT_SPRITE_BLOCK_HITSCAN);

		return (newsp);
	}

	public static int ConnectCopySprite(SPRITE tsp) {
		short newsector;
		int testz;

		if (FAF_ConnectCeiling(tsp.sectnum)) {
			newsector = tsp.sectnum;
			testz = SPRITEp_TOS(tsp) - Z(10);

			if (testz < sector[tsp.sectnum].ceilingz)
				newsector = engine.updatesectorz(tsp.x, tsp.y, testz, newsector);

			if (newsector >= 0 && newsector != tsp.sectnum) {
				return (CopySprite(tsp, newsector));
			}
		}

		if (FAF_ConnectFloor(tsp.sectnum)) {
			newsector = tsp.sectnum;
			testz = SPRITEp_BOS(tsp) + Z(10);

			if (testz > sector[tsp.sectnum].floorz)
				newsector = engine.updatesectorz(tsp.x, tsp.y, testz, newsector);

			if (newsector >= 0 && newsector != tsp.sectnum) {
				return (CopySprite(tsp, newsector));
			}
		}

		return (-1);
	}

	public static void PreDrawStackedWater() {
		short si, snexti;
		short i, nexti;
		SPRITE sp;
		USER u, nu;
		int newsp;

		for (si = headspritestat[STAT_CEILING_FLOOR_PIC_OVERRIDE]; si != -1; si = snexti) {
			snexti = nextspritestat[si];
			for (i = headspritesect[sprite[si].sectnum]; i != -1; i = nexti) {
				nexti = nextspritesect[i];
				if (pUser[i] != null) {
					if (sprite[i].statnum == STAT_ITEM)
						continue;

					if (sprite[i].statnum <= STAT_DEFAULT || sprite[i].statnum > STAT_PLAYER0 + MAX_SW_PLAYERS)
						continue;

					// code so that a copied sprite will not make another copy
					if (pUser[i].xchange == -989898)
						continue;

					sp = sprite[i];
					u = pUser[i];

					newsp = ConnectCopySprite(sp);
					if (newsp >= 0) {
						// spawn a user
						pUser[newsp] = nu = new USER();

						nu.xchange = -989898;

						// copy everything reasonable from the user that
						// analyzesprites() needs to draw the image
						nu.State = u.State;
						nu.Rot = u.Rot;
						nu.StateStart = u.StateStart;
						nu.StateEnd = u.StateEnd;
						nu.ox = u.ox;
						nu.oy = u.oy;
						nu.oz = u.oz;
						nu.Flags = u.Flags;
						nu.Flags2 = u.Flags2;
						nu.RotNum = u.RotNum;
						nu.ID = u.ID;

						// set these to other sprite for players draw
						nu.SpriteNum = i;

						nu.PlayerP = u.PlayerP;
						nu.spal = u.spal;
					}
				}
			}
		}
	}

	public static void FAF_DrawRooms(int x, int y, int z, float ang, float horiz, short sectnum) {
		short i, nexti;

		for (i = headspritestat[STAT_CEILING_FLOOR_PIC_OVERRIDE]; i != -1; i = nexti) {
			nexti = nextspritestat[i];
			if (SPRITE_TAG3(i) == 0) {
				// back up ceilingpicnum and ceilingstat
				sprite[i].xvel = sector[sprite[i].sectnum].ceilingpicnum;
				sector[sprite[i].sectnum].ceilingpicnum = (short) SPRITE_TAG2(i);
				sprite[i].ang = sector[sprite[i].sectnum].ceilingstat;
				// SET(sector[sprite[i].sectnum].ceilingstat, ((long)SPRITE_TAG7(i))<<7);
				sector[sprite[i].sectnum].ceilingstat |= (SPRITE_TAG6(i));
				sector[sprite[i].sectnum].ceilingstat &= ~(CEILING_STAT_PLAX);
			} else if (SPRITE_TAG3(i) == 1) {
				sprite[i].xvel = sector[sprite[i].sectnum].floorpicnum;
				sector[sprite[i].sectnum].floorpicnum = (short) SPRITE_TAG2(i);
				sprite[i].ang = sector[sprite[i].sectnum].floorstat;
				sector[sprite[i].sectnum].floorstat |= (SPRITE_TAG6(i));
				sector[sprite[i].sectnum].floorstat &= ~(FLOOR_STAT_PLAX);
			}
		}

		engine.drawrooms(x, y, z, ang, horiz, sectnum);

		for (i = headspritestat[STAT_CEILING_FLOOR_PIC_OVERRIDE]; i != -1; i = nexti) {
			nexti = nextspritestat[i];
			// manually set gotpic
			if (TEST_GOTSECTOR(sprite[i].sectnum)) {
				SET_GOTPIC(FAF_MIRROR_PIC);
			}

			if (SPRITE_TAG3(i) == 0) {
				// restore ceilingpicnum and ceilingstat
				sector[sprite[i].sectnum].ceilingpicnum = (short) SPRITE_TAG5(i);
				sector[sprite[i].sectnum].ceilingstat = (short) SPRITE_TAG4(i);
				sector[sprite[i].sectnum].ceilingstat &= ~(CEILING_STAT_PLAX);
			} else if (SPRITE_TAG3(i) == 1) {
				sector[sprite[i].sectnum].floorpicnum = (short) SPRITE_TAG5(i);
				sector[sprite[i].sectnum].floorstat = (short) SPRITE_TAG4(i);
				sector[sprite[i].sectnum].floorstat &= ~(FLOOR_STAT_PLAX);
			}
		}
	}

	// last valid stuff
	private static short lv_sectnum = -1;
	private static int lv_x, lv_y, lv_z;
	public static int remoteOldAngle;

	public static void drawscreen(PlayerStr pp, int smoothratio) {
		int tx, ty, tz;
		short tsectnum;
		float thoriz, tang;
		short i, j;
		WALL wal;
		int bob_amt = 0;

		gPlayerIndex = -1;
		PlayerStr camerapp; // prediction player if prediction is on, else regular player

		if (HelpInputMode) {
			engine.rotatesprite(0, 0, 65536, 0, HelpPagePic[HelpPage], 0, 0, (ROTATE_SPRITE_CORNER
					| ROTATE_SPRITE_SCREEN_CLIP | ROTATE_SPRITE_NON_MASK | ROTATE_SPRITE_IGNORE_START_MOST), 0, 0,
					xdim - 1, ydim - 1);
			return;
		}

		DrawScreen = true;
		PreDraw();
		PreUpdatePanel();

		// TENSW: when rendering with prediction, the only thing that counts should
		// be the predicted player.
		if (PredictionOn && numplayers > 1 && pp == Player[myconnectindex])
			camerapp = gNet.ppp;
		else
			camerapp = pp;

		tx = camerapp.oposx + mulscale(camerapp.posx - camerapp.oposx, smoothratio, 16);
		ty = camerapp.oposy + mulscale(camerapp.posy - camerapp.oposy, smoothratio, 16);
		tz = camerapp.oposz + mulscale(camerapp.posz - camerapp.oposz, smoothratio, 16);
		tang = camerapp.oang + (BClampAngle(camerapp.pang + 1024 - camerapp.oang) - 1024) * smoothratio / 65536.0f;
		thoriz = (camerapp.ohoriz + ((camerapp.horiz - camerapp.ohoriz) * smoothratio) / 65536.0f);
		tsectnum = camerapp.cursectnum;

		tsectnum = COVERupdatesector(tx, ty, tsectnum);
		tang += camerapp.lookang;

		if (tsectnum < 0) {
			// if we hit an invalid sector move to the last valid position for drawing
			tsectnum = lv_sectnum;
			tx = lv_x;
			ty = lv_y;
			tz = lv_z;
		} else {
			// last valid stuff
			lv_sectnum = tsectnum;
			lv_x = tx;
			lv_y = ty;
			lv_z = tz;
		}

		pp.six = tx;
		pp.siy = ty;
		pp.siz = tz - pp.posz;
		pp.siang = (short) tang;

        QuakeViewChange(camerapp, tmp_ptr[0], tmp_ptr[1], tmp_ptr[2], tmp_ptr[3]);
		int quake_z = tmp_ptr[0].value;
		int quake_x = tmp_ptr[1].value;
		int quake_y = tmp_ptr[2].value;
		int quake_ang = tmp_ptr[3].value;

		VisViewChange(camerapp, tmp_ptr[0].set(visibility));
		visibility = tmp_ptr[0].value;

		tz = tz + quake_z;
		tx = tx + quake_x;
		ty = ty + quake_y;
		tang += quake_ang;
		tang = BClampAngle(tang);

		if (pp.sop_remote != -1) {
			if (TEST_BOOL1(sprite[pp.remote_sprite]))
				tang = sprite[pp.remote_sprite].ang;
			else {
				int xmid = pp.remote.oposx + mulscale(pp.remote.posx - pp.remote.oposx, smoothratio, 16);
				int ymid = pp.remote.oposy + mulscale(pp.remote.posy - pp.remote.oposy, smoothratio, 16);
				tang = engine.getangle(xmid - tx, ymid - ty);
			}
		}

		if (TEST(pp.Flags, PF_VIEW_FROM_OUTSIDE)) {
			BackView(tmp_ptr[0].set(tx), tmp_ptr[1].set(ty), tmp_ptr[2].set(tz), tmp_ptr[3].set(tsectnum),
					tmp_ptr[4].set((int) tang), (int) thoriz);

			tx = tmp_ptr[0].value;
			ty = tmp_ptr[1].value;
			tz = tmp_ptr[2].value;
			tsectnum = (short) tmp_ptr[3].value;
			tang = (short) tmp_ptr[4].value;
		} else {
			bob_amt = camerapp.obob_amt + mulscale(camerapp.bob_amt - camerapp.obob_amt, smoothratio, 16);

			if (DemoPlaying || CameraTestMode) {
				CameraView(camerapp, tmp_ptr[0].set(tx), tmp_ptr[1].set(ty), tmp_ptr[2].set(tz),
						tmp_ptr[3].set(tsectnum), tmp_ptr[4].set((int) tang), tmp_ptr[5].set((int) thoriz));

				tx = tmp_ptr[0].value;
				ty = tmp_ptr[1].value;
				tz = tmp_ptr[2].value;
				tsectnum = (short) tmp_ptr[3].value;
				tang = (short) tmp_ptr[4].value;
				thoriz = tmp_ptr[5].value;
			}
		}

		if (!TEST(pp.Flags, PF_VIEW_FROM_CAMERA | PF_VIEW_FROM_OUTSIDE)) {
			tz += bob_amt;
			tz += camerapp.obob_z + mulscale(camerapp.bob_z - camerapp.obob_z, smoothratio, 16);

			// recoil only when not in camera
			thoriz = thoriz + pp.recoil_horizoff;
			thoriz = Math.max(thoriz, PLAYER_HORIZ_MIN);
			thoriz = Math.min(thoriz, PLAYER_HORIZ_MAX);
		}

		if (tsectnum >= 0) {
			engine.getzsofslope(tsectnum, tx, ty, zofslope);
			if (tz < zofslope[CEIL] + (4 << 8))
				tz = zofslope[CEIL] + (4 << 8);
			if (tz > zofslope[FLOOR] - (4 << 8))
				tz = zofslope[FLOOR] - (4 << 8);
		}

		if (dimensionmode != 6) {
			OverlapDraw = true;
			DrawOverlapRoom(tx, ty, tz, tang, thoriz, tsectnum, smoothratio);
			OverlapDraw = false;

			if (dimensionmode != 6 && !game.isScreenSaving()) {
				// TEST this! Changed to camerapp
				JS_DrawMirrors(pp, tx, ty, tz, tang, thoriz, smoothratio);
			}

			{
				FAF_DrawRooms(tx, ty, tz, tang, thoriz, tsectnum);
				PicInView(FAF_MIRROR_PIC, false);
			}

			analyzesprites(tx, ty, tz, false, smoothratio);
			post_analyzesprites(smoothratio);
			engine.drawmasks();

			// Only animate lava if its picnum is on screen
			// gotpic is a bit array where the tile number's bit is set
			// whenever it is drawn (ceilings, walls, sprites, etc.)
			if ((gotpic[SLIME >> 3] & (1 << (SLIME & 7))) > 0) {
				gotpic[SLIME >> 3] &= ~(1 << (SLIME & 7));

				Tile pic = engine.getTile(SLIME);
				if (pic.data != null)
					movelava(pic.data);
			}
		}

		i = pp.cursectnum;
		if (isValidSector(pp.cursectnum)) {
			show2dsector[i >> 3] |= (1 << (i & 7));
			wal = wall[sector[i].wallptr];
			int w = sector[i].wallptr;
			for (j = sector[i].wallnum; j > 0; j--, w++) {
				wal = wall[w];

				i = wal.nextsector;
				if (i < 0)
					continue;
				if ((wal.cstat & 0x0071) != 0)
					continue;
				if ((wall[wal.nextwall].cstat & 0x0071) != 0)
					continue;
				if (sector[i].lotag == 32767)
					continue;
				if (sector[i].ceilingz >= sector[i].floorz)
					continue;
				show2dsector[i >> 3] |= (1 << (i & 7));
			}
		}

		if ((dimensionmode == 5 || dimensionmode == 6)) {
			tx = camerapp.oposx;
			ty = camerapp.oposy;
			if (ScrollMode2D) {
				tx = Follow_posx;
				ty = Follow_posy;
			}

			for (j = 0; j < MAXSPRITES; j++) {
				// Don't show sprites tagged with 257
				if (sprite[j].lotag == 257) {
					if (TEST(sprite[j].cstat, CSTAT_SPRITE_FLOOR)) {
						sprite[j].cstat &= ~(CSTAT_SPRITE_FLOOR);
						sprite[j].owner = -2;
					}
				}
			}

			if (dimensionmode == 6) {
				engine.clearview(0);
				engine.drawmapview(tx, ty, zoom, (short) tang);
			}

			// Draw the line map on top of texture 2d map or just stand alone
			engine.drawoverheadmap(tx, ty, zoom, (short) tang);

			int txt_x = 7, txt_y = windowy1 + 5;
			if (game.nNetMode != NetMode.Single)
				txt_y += 10;
			if (ScrollMode2D)
				minigametext(txt_x, txt_y + 7, "Follow Mode", 0, 2 + 8 + 256);
			game.getFont(0).drawText(txt_x, txt_y, getMapName(Level), 0, 4, TextAlign.Left, 2 + 8 + 256, true);
		}

		for (j = 0; j < MAXSPRITES; j++) {
			// Don't show sprites tagged with 257
			if (sprite[j].lotag == 257 && sprite[j].owner == -2)
				sprite[j].cstat |= (CSTAT_SPRITE_FLOOR);
		}
	}

	public static void drawhud(PlayerStr pp) {
		UpdatePanel();

		PrintLocationInfo(pp);

		if (gs.Stats == 1 || (gs.Stats == 2 && (dimensionmode == 5 || dimensionmode == 6)))
			SecretInfo(pp, gs.gStatSize);

		DrawCrosshair(pp);

		operatefta(); // Draw all the user quotes in the quote array

		DoPlayerDiveMeter(pp); // Do the underwater breathing bar

		// Boss Health Meter, if Boss present
		BossHealthMeter();

		DrawMessageInput(pp); // This is only used for non-multiplayer input now

		DrawFullBar(pp);

		UpdateAltBar(pp);

		UpdateMiniBar(pp);

		if (game.gPaused) {
			int w = game.getFont(1).getWidth(MSG_GAME_PAUSED);
			gametext(TEXT_TEST_COL(w), 100, MSG_GAME_PAUSED, 0, 10);
		}

		if (game.nNetMode == NetMode.Multiplayer)
			DrawFragBar();

		if (gPlayerIndex != -1 && gPlayerIndex != screenpeek) {
			int len = buildString(txt_buffer, 0, Player[gPlayerIndex].getName());
			len = buildString(txt_buffer, len, " (", pUser[Player[gPlayerIndex].PlayerSprite].Health);
			len = buildString(txt_buffer, len, "hp)");

			int shade = 16 - (totalclock & 0x3F);

			int y = scale(windowy1, 200, ydim) + 100;
			if (gs.BorderNum < BORDER_BAR)
				y += engine.getTile(STATUS_BAR).getHeight() / 2;

			game.getFont(0).drawText(160, y, txt_buffer, shade, pUser[Player[gPlayerIndex].PlayerSprite].spal,
					TextAlign.Center, 2 + 8 + 16, false);
		}

		if (screenpeek != myconnectindex) {
			buildString(txt_buffer, 0, "View from \"", Player[screenpeek].getName(), "\"");
			int shade = 16 - (totalclock & 0x3F);

			game.getFont(1).drawText(160, scale(windowy1, 200, ydim) + 30, txt_buffer, shade, 0, TextAlign.Center, 10 | 16, false);
		}

		PostDraw();

		if (game.isCurrentScreen(gGameScreen) && totalclock < gNameShowTime) {
			int transp = 0;
			if (totalclock > gNameShowTime - 20)
				transp = 1;
			if (totalclock > gNameShowTime - 10)
				transp = 33;

			if (gs.showMapInfo != 0 && !game.menu.gShowMenu)
				game.getFont(2).drawText(160, 110 + 16 + 8, getMapName(Level), -128, 0, TextAlign.Center, 2 | transp,
						false);
		}
	}

	private static void DrawFullBar(PlayerStr pp) {
		if (gs.BorderNum != BORDER_BAR)
			return;
		Tile pic = engine.getTile(53);

		int scale = divscale(ydim, 4 * pic.getWidth(), 16);
		int width = mulscale(pic.getWidth(), scale, 16);

		int framesnum = xdim / width;
		pic = engine.getTile(STATUS_BAR);
		int fy = ydim - scale(pic.getHeight(), ydim, 200);

		int statusx1 = coordsConvertXScaled(160 - pic.getWidth() / 2, ConvertType.Normal);
		int statusx2 = coordsConvertXScaled(160 + pic.getWidth() / 2, ConvertType.Normal);

		int x = 0;
		for (int i = 0; i <= framesnum; i++) {
			if (x - width <= statusx1 || x + width >= statusx2) {
				engine.rotatesprite(x << 16, fy << 16, scale, 0, 53, 0, 0, 8 | 16 | 256, 0, 0, xdim - 1, ydim - 1);
			}
			x += width;
		}
		engine.rotatesprite(160 << 16, (200 - pic.getHeight() / 2) << 16, 1 << 16, 0, STATUS_BAR, 0, 0, 10, 0, 0,
				xdim - 1, ydim - 1);

		DrawCompass(pp);

		PlayerUpdatePanelInfo(pp);
	}

	public static final int MINI_BAR_Y = 174;

	public static final int MINI_BAR_HEALTH_BOX_PIC = 2437;
	public static final int MINI_BAR_AMMO_BOX_PIC = 2437;
	public static final int MINI_BAR_INVENTORY_BOX_PIC = 2438;

	public static final int MINI_BAR_HEALTH_BOX_X = 4;
	public static final int MINI_BAR_AMMO_BOX_X = 32;
	public static final int MINI_BAR_INVENTORY_BOX_X = 64;
	public static final int MINI_BAR_INVENTORY_BOX_Y = MINI_BAR_Y;

	public static void UpdateAltBar(PlayerStr pp) {
		if (gs.BorderNum != BORDER_ALTBAR)
			return;

		USER u = pUser[pp.PlayerSprite];
		if (u == null)
			return;

		INVENTORY_DATA id;
		int x = 187;
		int y = 155;

		engine.rotatesprite(x << 16, y << 16, (1 << 16), 0, ALTHUDRIGHT, 0, 0,
				ROTATE_SPRITE_SCREEN_CLIP | ROTATE_SPRITE_CORNER | 512, 0, 0, xdim - 1, ydim - 1);

		if (pp.InventoryAmount[pp.InventoryNum] != 0) {
			id = InventoryData[pp.InventoryNum];
			// Inventory pic
			engine.rotatesprite(x + 45 << 16, y + 17 << 16, (1 << 16), 0, id.State.picndx, 0, 0,
					ROTATE_SPRITE_SCREEN_CLIP | ROTATE_SPRITE_CORNER | 512, 0, 0, xdim - 1, ydim - 1);
			DrawInventory(pp, x + 43, y + 16, 512);
		}

		if (u.WeaponNum != WPN_SWORD && u.WeaponNum != WPN_FIST) {
			DisplayPanelNumber(pp, x + 11, y + 20, pp.WpnAmmo[u.WeaponNum], 512);
		}

		if (gNet.MultiGameType != MultiGameTypes.MULTI_GAME_COMMBAT)
			PlayerUpdateKeys(pp, x + 89, y + 18, 512);
		else {
			if (gNet.TimeLimit != 0) {
				int seconds = gNet.TimeLimitClock / 120;
				int offs = Bitoa(seconds / 60, txt_buffer, 2);
				buildString(txt_buffer, offs, ":", seconds % 60, 2);
				DisplaySummaryString(pp, x + 92, y + 23, 0, 0, txt_buffer, 512);
			}
		}

		x = 0;
		y = 155;
		engine.rotatesprite(x << 16, y << 16, (1 << 16), 0, ALTHUDLEFT, 0, 0,
				ROTATE_SPRITE_SCREEN_CLIP | ROTATE_SPRITE_CORNER | 256, 0, 0, xdim - 1, ydim - 1);

		DisplayMiniBarNumber(pp, x + 23, y + 20, u.Health, 256);

		if (pp.Armor != 0) {
			DisplayMiniBarNumber(pp, x + 59, y + 20, pp.Armor, 256);
		}
	}

	public static void UpdateMiniBar(PlayerStr pp) {
		if (gs.BorderNum != BORDER_MINI_BAR)
			return;

		USER u = pUser[pp.PlayerSprite];
		if (u == null)
			return;

		INVENTORY_DATA id;

		int x = MINI_BAR_HEALTH_BOX_X;
		int y = 200 - 26;

		engine.rotatesprite(x << 16, y << 16, (1 << 16), 0, MINI_BAR_HEALTH_BOX_PIC, 0, 0,
				ROTATE_SPRITE_SCREEN_CLIP | ROTATE_SPRITE_CORNER | 256, 0, 0, xdim - 1, ydim - 1);

		DisplayMiniBarNumber(pp, x + 3, y + 5, u.Health, 256);

		if (pp.Armor != 0) {
			x += 28;
			engine.rotatesprite(x << 16, y << 16, (1 << 16), 0, MINI_BAR_AMMO_BOX_PIC, 0, 0,
					ROTATE_SPRITE_SCREEN_CLIP | ROTATE_SPRITE_CORNER | 256, 0, 0, xdim - 1, ydim - 1);

			DisplayMiniBarNumber(pp, x + 3, y + 5, pp.Armor, 256);
		}

		if (u.WeaponNum != WPN_SWORD && u.WeaponNum != WPN_FIST) {
			x += 28;
			engine.rotatesprite(x << 16, y << 16, (1 << 16), 0, MINI_BAR_AMMO_BOX_PIC, 0, 0,
					ROTATE_SPRITE_SCREEN_CLIP | ROTATE_SPRITE_CORNER | 256, 0, 0, xdim - 1, ydim - 1);

			DisplayMiniBarNumber(pp, x + 3, y + 5, pp.WpnAmmo[u.WeaponNum], 256);
		}

		if (gNet.MultiGameType != MultiGameTypes.MULTI_GAME_COMMBAT) {
			boolean hasKey = false;
			for (int i = 0; i < 8; i++) {
				if (pp.HasKey[i] != 0) {
					hasKey = true;
					break;
				}
			}

			if (hasKey) {
				x += 28;
				engine.rotatesprite(x << 16, y << 16, (1 << 16), 0, MINI_BAR_AMMO_BOX_PIC, 0, 0,
						ROTATE_SPRITE_SCREEN_CLIP | ROTATE_SPRITE_CORNER | 256, 0, 0, xdim - 1, ydim - 1);
				PlayerUpdateKeys(pp, x + 2, y + 3, 256);
			}
		} else if (gNet.TimeLimit != 0) {
			x += 28;
			engine.rotatesprite(x << 16, y << 16, (1 << 16), 0, MINI_BAR_AMMO_BOX_PIC, 0, 0,
					ROTATE_SPRITE_SCREEN_CLIP | ROTATE_SPRITE_CORNER | 256, 0, 0, xdim - 1, ydim - 1);

			int seconds = gNet.TimeLimitClock / 120;
			int offs = Bitoa(seconds / 60, txt_buffer, 2);
			buildString(txt_buffer, offs, ":", seconds % 60, 2);
			DisplaySummaryString(pp, x + 5, y + 8, 0, 0, txt_buffer, 0);
		}

		if (pp.InventoryAmount[pp.InventoryNum] != 0) {
			// Inventory Box
			x += 28;

			engine.rotatesprite(x << 16, y << 16, (1 << 16), 0, MINI_BAR_INVENTORY_BOX_PIC, 0, 0,
					ROTATE_SPRITE_SCREEN_CLIP | ROTATE_SPRITE_CORNER | 256, 0, 0, xdim - 1, ydim - 1);

			id = InventoryData[pp.InventoryNum];

			// Inventory pic
			engine.rotatesprite(x + 2 << 16, y + 3 << 16, (1 << 16), 0, id.State.picndx, 0, 0,
					ROTATE_SPRITE_SCREEN_CLIP | ROTATE_SPRITE_CORNER | 256, 0, 0, xdim - 1, ydim - 1);

			// will update the AUTO and % inventory values
			DrawInventory(pp, x, y + 1, 256);
		}
	}

	private static short CompassPic[] = { COMPASS_EAST, COMPASS_EAST2, COMPASS_TIC, COMPASS_TIC2, COMPASS_MID_TIC,
			COMPASS_MID_TIC2, COMPASS_TIC, COMPASS_TIC2,

			COMPASS_SOUTH, COMPASS_SOUTH2, COMPASS_TIC, COMPASS_TIC2, COMPASS_MID_TIC, COMPASS_MID_TIC2, COMPASS_TIC,
			COMPASS_TIC2,

			COMPASS_WEST, COMPASS_WEST2, COMPASS_TIC, COMPASS_TIC2, COMPASS_MID_TIC, COMPASS_MID_TIC2, COMPASS_TIC,
			COMPASS_TIC2,

			COMPASS_NORTH, COMPASS_NORTH2, COMPASS_TIC, COMPASS_TIC2, COMPASS_MID_TIC, COMPASS_MID_TIC2, COMPASS_TIC,
			COMPASS_TIC2, };

	public static final int NORM_CANG(int ang) {
		return (((ang) + 32) & 31);
	}

	public static short CompassShade[] = { 25, 19, 15, 9, 1, 1, 9, 15, 19, 25 };

	public static void DrawCompass(PlayerStr pp) {
		if (gs.BorderNum < BORDER_BAR || pp != Player[screenpeek])
			return;

		int ang = pp.getAnglei();
		if (pp.sop_remote != -1)
			ang = 0;

		int start_ang = ((ang + 32) >> 6);
		start_ang = NORM_CANG(start_ang - 4);

		int flags = ROTATE_SPRITE_SCREEN_CLIP | ROTATE_SPRITE_CORNER;

		int x_size = engine.getTile(COMPASS_NORTH).getWidth();
		for (int i = 0, x = COMPASS_X; i < 10; i++) {
			engine.rotatesprite(x << 16, COMPASS_Y << 16, (1 << 16), 0, CompassPic[NORM_CANG(start_ang + i)],
					CompassShade[i], 0, flags, 0, 0, xdim - 1, ydim - 1);
			x += x_size;
		}
	}

}
