package ru.m210projects.Wang;

import static ru.m210projects.Build.Engine.MAXSECTORS;
import static ru.m210projects.Build.Engine.MAXTILES;
import static ru.m210projects.Build.Engine.gotpic;
import static ru.m210projects.Build.Engine.headspritestat;
import static ru.m210projects.Build.Engine.mirrorang;
import static ru.m210projects.Build.Engine.mirrorx;
import static ru.m210projects.Build.Engine.mirrory;
import static ru.m210projects.Build.Engine.nextspritestat;
import static ru.m210projects.Build.Engine.numsectors;
import static ru.m210projects.Build.Engine.numwalls;
import static ru.m210projects.Build.Engine.sector;
import static ru.m210projects.Build.Engine.sprite;
import static ru.m210projects.Build.Engine.totalclock;
import static ru.m210projects.Build.Engine.wall;
import static ru.m210projects.Build.Net.Mmulti.numplayers;
import static ru.m210projects.Build.Pragmas.klabs;
import static ru.m210projects.Wang.Digi.DIGI_CARDUNLOCK;
import static ru.m210projects.Wang.Digi.DIGI_ENGROOM1;
import static ru.m210projects.Wang.Digi.DIGI_ENGROOM4;
import static ru.m210projects.Wang.Digi.DIGI_ENGROOM5;
import static ru.m210projects.Wang.Digi.DIGI_ERUPTION;
import static ru.m210projects.Wang.Digi.DIGI_FIRE1;
import static ru.m210projects.Wang.Digi.DIGI_JET;
import static ru.m210projects.Wang.Digi.DIGI_LAVAFLOW1;
import static ru.m210projects.Wang.Digi.DIGI_RAMUNLOCK;
import static ru.m210projects.Wang.Digi.DIGI_UNLOCK;
import static ru.m210projects.Wang.Digi.DIGI_VOLCANOSTEAM1;
import static ru.m210projects.Wang.Digi.DIGI_WATERFALL1;
import static ru.m210projects.Wang.Digi.DIGI_WATERFLOW1;
import static ru.m210projects.Wang.Draw.INVISTILE;
import static ru.m210projects.Wang.Draw.aVoxelArray;
import static ru.m210projects.Wang.Draw.analyzesprites;
import static ru.m210projects.Wang.Game.Level;
import static ru.m210projects.Wang.Game.Player;
import static ru.m210projects.Wang.Game.screenpeek;
import static ru.m210projects.Wang.Game.swGetAddon;
import static ru.m210projects.Wang.Gameutils.CSTAT_SPRITE_INVISIBLE;
import static ru.m210projects.Wang.Gameutils.NORM_ANGLE;
import static ru.m210projects.Wang.Gameutils.PF_DIVING;
import static ru.m210projects.Wang.Gameutils.RESET_BOOL3;
import static ru.m210projects.Wang.Gameutils.RESET_GOTPIC;
import static ru.m210projects.Wang.Gameutils.SET_BOOL3;
import static ru.m210projects.Wang.Gameutils.SP_TAG2;
import static ru.m210projects.Wang.Gameutils.SP_TAG4;
import static ru.m210projects.Wang.Gameutils.SP_TAG5;
import static ru.m210projects.Wang.Gameutils.SP_TAG6;
import static ru.m210projects.Wang.Gameutils.SP_TAG7;
import static ru.m210projects.Wang.Gameutils.TEST_BOOL1;
import static ru.m210projects.Wang.Gameutils.TEST_BOOL11;
import static ru.m210projects.Wang.Gameutils.TEST_BOOL2;
import static ru.m210projects.Wang.Gameutils.TEST_BOOL3;
import static ru.m210projects.Wang.Gameutils.TEST_GOTPIC;
import static ru.m210projects.Wang.Gameutils.WALLFX_DONT_STICK;
import static ru.m210projects.Wang.JTags.AMBIENT_SOUND;
import static ru.m210projects.Wang.JTags.MIRROR_CAM;
import static ru.m210projects.Wang.JTags.MIRROR_SPAWNSPOT;
import static ru.m210projects.Wang.JTags.TAG_DRIPGEN;
import static ru.m210projects.Wang.JTags.TAG_ECHO_SOUND;
import static ru.m210projects.Wang.JTags.TAG_WALL_MAGIC_MIRROR;
import static ru.m210projects.Wang.Main.engine;
import static ru.m210projects.Wang.Main.gNet;
import static ru.m210projects.Wang.Main.game;
import static ru.m210projects.Wang.Main.gs;
import static ru.m210projects.Wang.Names.BETTY_R0;
import static ru.m210projects.Wang.Names.BLUE_CARD;
import static ru.m210projects.Wang.Names.BLUE_KEY;
import static ru.m210projects.Wang.Names.BRONZE_SKELKEY;
import static ru.m210projects.Wang.Names.CARD_LOCKED;
import static ru.m210projects.Wang.Names.CARD_UNLOCKED;
import static ru.m210projects.Wang.Names.GOLD_SKELKEY;
import static ru.m210projects.Wang.Names.GREEN_CARD;
import static ru.m210projects.Wang.Names.GREEN_KEY;
import static ru.m210projects.Wang.Names.ICON_ARMOR;
import static ru.m210projects.Wang.Names.ICON_BOOSTER;
import static ru.m210projects.Wang.Names.ICON_CALTROPS;
import static ru.m210projects.Wang.Names.ICON_CHEMBOMB;
import static ru.m210projects.Wang.Names.ICON_CLOAK;
import static ru.m210projects.Wang.Names.ICON_ENVIRON_SUIT;
import static ru.m210projects.Wang.Names.ICON_EXPLOSIVE_BOX;
import static ru.m210projects.Wang.Names.ICON_FIREBALL_LG_AMMO;
import static ru.m210projects.Wang.Names.ICON_FLASHBOMB;
import static ru.m210projects.Wang.Names.ICON_FLY;
import static ru.m210projects.Wang.Names.ICON_GRENADE;
import static ru.m210projects.Wang.Names.ICON_GRENADE_LAUNCHER;
import static ru.m210projects.Wang.Names.ICON_GUARD_HEAD;
import static ru.m210projects.Wang.Names.ICON_HEART;
import static ru.m210projects.Wang.Names.ICON_HEART_LG_AMMO;
import static ru.m210projects.Wang.Names.ICON_HEAT_CARD;
import static ru.m210projects.Wang.Names.ICON_LG_GRENADE;
import static ru.m210projects.Wang.Names.ICON_LG_MINE;
import static ru.m210projects.Wang.Names.ICON_LG_ROCKET;
import static ru.m210projects.Wang.Names.ICON_LG_SHOTSHELL;
import static ru.m210projects.Wang.Names.ICON_LG_UZI_AMMO;
import static ru.m210projects.Wang.Names.ICON_MEDKIT;
import static ru.m210projects.Wang.Names.ICON_MICRO_BATTERY;
import static ru.m210projects.Wang.Names.ICON_MICRO_GUN;
import static ru.m210projects.Wang.Names.ICON_NAPALM;
import static ru.m210projects.Wang.Names.ICON_NAPALMAMMO;
import static ru.m210projects.Wang.Names.ICON_NIGHT_VISION;
import static ru.m210projects.Wang.Names.ICON_NUKE;
import static ru.m210projects.Wang.Names.ICON_RAIL_AMMO;
import static ru.m210projects.Wang.Names.ICON_RAIL_GUN;
import static ru.m210projects.Wang.Names.ICON_REPAIR_KIT;
import static ru.m210projects.Wang.Names.ICON_RING;
import static ru.m210projects.Wang.Names.ICON_RINGAMMO;
import static ru.m210projects.Wang.Names.ICON_ROCKET;
import static ru.m210projects.Wang.Names.ICON_SHOTGUN;
import static ru.m210projects.Wang.Names.ICON_SM_MEDKIT;
import static ru.m210projects.Wang.Names.ICON_STAR;
import static ru.m210projects.Wang.Names.ICON_UZI;
import static ru.m210projects.Wang.Names.ICON_UZIFLOOR;
import static ru.m210projects.Wang.Names.RAMCARD_LOCKED;
import static ru.m210projects.Wang.Names.RAMCARD_UNLOCKED;
import static ru.m210projects.Wang.Names.RED_CARD;
import static ru.m210projects.Wang.Names.RED_KEY;
import static ru.m210projects.Wang.Names.RED_SKELKEY;
import static ru.m210projects.Wang.Names.SILVER_SKELKEY;
import static ru.m210projects.Wang.Names.SKEL_LOCKED;
import static ru.m210projects.Wang.Names.SKEL_UNLOCKED;
import static ru.m210projects.Wang.Names.ST1;
import static ru.m210projects.Wang.Names.STAT_AMBIENT;
import static ru.m210projects.Wang.Names.STAT_DEFAULT;
import static ru.m210projects.Wang.Names.STAT_ECHO;
import static ru.m210projects.Wang.Names.STAT_NO_STATE;
import static ru.m210projects.Wang.Names.STAT_SPAWN_SPOT;
import static ru.m210projects.Wang.Names.STAT_ST1;
import static ru.m210projects.Wang.Names.YELLOW_CARD;
import static ru.m210projects.Wang.Names.YELLOW_KEY;
import static ru.m210projects.Wang.Palette.PALETTE_PLAYER1;
import static ru.m210projects.Wang.Palette.PALETTE_PLAYER4;
import static ru.m210projects.Wang.Palette.PALETTE_PLAYER6;
import static ru.m210projects.Wang.Palette.PALETTE_PLAYER7;
import static ru.m210projects.Wang.Palette.PALETTE_PLAYER8;
import static ru.m210projects.Wang.Palette.PALETTE_PLAYER9;
import static ru.m210projects.Wang.Player.GetDeltaAngle;
import static ru.m210projects.Wang.Player.PLAYER_HORIZ_MAX;
import static ru.m210projects.Wang.Player.PLAYER_HORIZ_MIN;
import static ru.m210projects.Wang.Rooms.COVERinsertsprite;
import static ru.m210projects.Wang.Sound.COVER_SetReverb;
import static ru.m210projects.Wang.Sound.PlaySound;
import static ru.m210projects.Wang.Sound.Set3DSoundOwner;
import static ru.m210projects.Wang.Sound.v3df_ambient;
import static ru.m210projects.Wang.Sound.v3df_dontpan;
import static ru.m210projects.Wang.Sound.v3df_doppler;
import static ru.m210projects.Wang.Sound.v3df_follow;
import static ru.m210projects.Wang.Sound.v3df_init;
import static ru.m210projects.Wang.Sprites.MoveSkip2;
import static ru.m210projects.Wang.Sprites.MoveSkip4;
import static ru.m210projects.Wang.Sprites.SpawnUser;
import static ru.m210projects.Wang.Sprites.change_sprite_stat;
import static ru.m210projects.Wang.Type.MyTypes.TEST;
import static ru.m210projects.Wang.Weapon.GenerateDrips;

import java.util.ArrayList;
import java.util.List;

import ru.m210projects.Build.Settings.BuildSettings;
import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Build.Types.Tile;
import ru.m210projects.Wang.Type.MirrorType;
import ru.m210projects.Wang.Type.MirrorType.MirrorState;
import ru.m210projects.Wang.Type.OrgTile;
import ru.m210projects.Wang.Type.ParentalStruct;
import ru.m210projects.Wang.Type.PlayerStr;
import ru.m210projects.Wang.Type.USER;
import ru.m210projects.Wang.Type.VOC3D;

public class JSector {

	public static final int MAXMIRRORDIST = 3300; // At this distance, or less, the magic mirrors activate.
	public static final int MAXMIRRORMONSTERS = 4; // Max monsters any one magic mirror can spawn

	// This header is used for reserving tile space for programatic purposes
	// MAXTILES is currently at 6144 in size - anything under this is ok

	public static final int MAXMIRRORS = 8;
	// This is just some, high, blank tile number not used
	// by real graphics to put the MAXMIRRORS mirrors in
	public static final int MIRRORLABEL = 6000;

	public static final int MIRROR = 340;
	public static final int FLOORMIRROR = 341;
	public static final int CAMSPRITE = 3830;

	public static MirrorType[] mirror = new MirrorType[MAXMIRRORS];

	public static final int MAXCAMDIST = 8000;

	public static int camloopcnt = 0; // Timer to cycle through player
	// views
	public static short camplayerview = 1; // Don't show yourself!

	static int MirrorMoveSkip16 = 0;

	static boolean bAutoSize = true; // Autosizing on/off

	// Rotation angles for sprites
	static short rotang = 0;

	public static void InitJSectorStructs() {
		for (int i = 0; i < MAXTILES; i++)
			aVoxelArray[i] = new ParentalStruct();
	}

	/////////////////////////////////////////////////////
	// SpawnWallSound
	/////////////////////////////////////////////////////
	public static void SpawnWallSound(int sndnum, int i) {
		short SpriteNum;
		int midx, midy, midz;
		SPRITE sp;

		SpriteNum = COVERinsertsprite((short) 0, STAT_DEFAULT);
		if (SpriteNum < 0)
			return;

		sp = sprite[SpriteNum];
		sp.cstat = 0;
		sp.extra = 0;
		// Get wall midpoint for offset in mirror view
		midx = (wall[i].x + wall[wall[i].point2].x) / 2;
		midy = (wall[i].y + wall[wall[i].point2].y) / 2;
		midz = (sector[wall[i].nextsector].ceilingz + sector[wall[i].nextsector].floorz) / 2;
		engine.setspritez(SpriteNum, midx, midy, midz);
		sp = sprite[SpriteNum];

		VOC3D voc = PlaySound(sndnum, sp, v3df_dontpan | v3df_doppler);
		if (voc != null)
			Set3DSoundOwner(SpriteNum, voc);
	}

	public static short CheckTileSound(short picnum) {
		short sndnum = -1;

		switch (picnum) {
		case 163: // Sizzly Lava
		case 167:
			sndnum = DIGI_VOLCANOSTEAM1;
			break;
		case 175: // Flowing Lava
			sndnum = DIGI_ERUPTION;
			break;
		case 179: // Bubbly lava
			sndnum = DIGI_LAVAFLOW1;
			break;
		case 300: // Water fall tile
			sndnum = DIGI_WATERFALL1;
			break;
		case 334: // Teleporter Pad
			sndnum = DIGI_ENGROOM1;
			break;
		case 2690: // Jet engine fan
			sndnum = DIGI_JET;
			break;
		case 2672: // X-Ray Machine engine
			sndnum = DIGI_ENGROOM5;
			break;
		case 768: // Electricity
			break;
		case 2714: // Pachinko Machine
			break;
		case 2782: // Telepad
			sndnum = DIGI_ENGROOM4;
			break;
		case 3382: // Gears
			sndnum = DIGI_ENGROOM5;
			break;
		case 2801: // Computers
		case 2804:
		case 2807:
		case 3352:
		case 3385:
		case 3389:
		case 3393:
		case 3397:
		case 3401:
		case 3405:
			break;
		case 3478: // Radar screen
			break;
		default:
			sndnum = -1;
			break;
		}
		return (sndnum);
	}

/////////////////////////////////////////////////////
//Initialize any of my special use sprites
/////////////////////////////////////////////////////
	public static void JS_SpriteSetup() {
		SPRITE sp;
		short SpriteNum = 0, NextSprite;
		USER u;
		short i;
		VOC3D voc;

		for (SpriteNum = headspritestat[0]; SpriteNum != -1; SpriteNum = NextSprite) {
			NextSprite = nextspritestat[SpriteNum];
			sp = sprite[SpriteNum];
			short tag = sp.hitag;

			// Stupid Redux's fixes
			if (Level == 9 && swGetAddon() == 2 && SpriteNum == 11 && sp.picnum == BETTY_R0) {
				sp.extra = 0;
			}

			// Non static camera. Camera sprite will be drawn!
			if (tag == MIRROR_CAM && sprite[SpriteNum].picnum != ST1) {
				// Just change it to static, sprite has all the info I need
				change_sprite_stat(SpriteNum, STAT_SPAWN_SPOT);
			}

			switch (sprite[SpriteNum].picnum) {
			case ST1:
				if (tag == MIRROR_CAM) {
					// Just change it to static, sprite has all the info I need
					// ST1 cameras won't move with SOBJ's!
					change_sprite_stat(SpriteNum, STAT_ST1);
				} else if (tag == MIRROR_SPAWNSPOT) {
					// Just change it to static, sprite has all the info I need
					change_sprite_stat(SpriteNum, STAT_ST1);
				} else if (tag == AMBIENT_SOUND) {
					change_sprite_stat(SpriteNum, STAT_AMBIENT);
				} else if (tag == TAG_ECHO_SOUND) {
					change_sprite_stat(SpriteNum, STAT_ECHO);
				} else if (tag == TAG_DRIPGEN) {
					u = SpawnUser(SpriteNum, 0, null);

					u.RotNum = 0;
					u.WaitTics = (short) (sp.lotag * 120);

					u.ActorActionFunc = GenerateDrips;

					change_sprite_stat(SpriteNum, STAT_NO_STATE);
					sp.cstat |= (CSTAT_SPRITE_INVISIBLE);
				}
				break;
			// Sprites in editart that should play ambient sounds
			// automatically
			case 380:
			case 396:
			case 430:
			case 443:
			case 512:
			case 521:
			case 541:
			case 2720:
			case 3143:
			case 3157:
				if ((voc = PlaySound(DIGI_FIRE1, sp, v3df_follow | v3df_dontpan | v3df_doppler)) != null)
					Set3DSoundOwner(SpriteNum, voc);
				break;
			case 795:
			case 880:
				if ((voc = PlaySound(DIGI_WATERFLOW1, sp, v3df_follow | v3df_dontpan | v3df_doppler)) != null)
					Set3DSoundOwner(SpriteNum, voc);
				break;
			case 460: // Wind Chimes
				if ((voc = PlaySound(79, sp, v3df_ambient | v3df_init | v3df_doppler | v3df_follow)) != null)
					Set3DSoundOwner(SpriteNum, voc);
				break;
			}
		}
		// Check for certain walls to make sounds
		for (i = 0; i < numwalls; i++) {
			short picnum;

			picnum = wall[i].picnum;

			// Set the don't stick bit for liquid tiles
			switch (picnum) {
			case 175:
			case 179:
			case 300:
			case 320:
			case 330:
			case 352:
			case 780:
			case 890:
			case 2608:
			case 2616:
				wall[i].extra |= (WALLFX_DONT_STICK);
				break;
			}

		}
	}

	/////////////////////////////////////////////////////
	// Initialize the mirrors
	/////////////////////////////////////////////////////

	static short on_cam = 0;
	public static boolean mirrorinview;
	public static int mirrorcnt;

	public static boolean JS_InitMirrors() {
		short startwall, endwall;
		int i, j, s;
		short SpriteNum = 0, NextSprite;

		boolean Found_Cam = false;

		// Set all the mirror struct values to -1
		for (i = 0; i < MAXMIRRORS; i++) {
			if (mirror[i] == null)
				mirror[i] = new MirrorType();
			else
				mirror[i].reset();
		}

		mirrorinview = false; // Initially set global mirror flag
		// to no mirrors seen

		// Scan wall tags for mirrors
		mirrorcnt = 0;
		Tile pic = engine.getTile(MIRROR);
		pic.setWidth(0).setHeight(0);

		for (i = 0; i < MAXMIRRORS; i++) {
			pic = engine.getTile(i + MIRRORLABEL);
			pic.setWidth(0).setHeight(0);

			mirror[i].campic = -1;
			mirror[i].camsprite = -1;
			mirror[i].camera = -1;
			mirror[i].ismagic = false;
		}

		for (i = 0; i < numwalls; i++) {
			s = wall[i].nextsector;
			if ((s >= 0) && (wall[i].overpicnum == MIRROR) && (wall[i].cstat & 32) != 0) {
				if ((sector[s].floorstat & 1) == 0) {
					wall[i].overpicnum = (short) (MIRRORLABEL + mirrorcnt);
					wall[i].picnum = (short) (MIRRORLABEL + mirrorcnt);

					sector[s].ceilingpicnum = (short) (MIRRORLABEL + mirrorcnt);
					sector[s].floorpicnum = (short) (MIRRORLABEL + mirrorcnt);
					sector[s].floorstat |= 1;
					mirror[mirrorcnt].mirrorwall = (short) i;
					mirror[mirrorcnt].mirrorsector = (short) s;
					mirror[mirrorcnt].numspawnspots = 0;
					mirror[mirrorcnt].ismagic = false;
					if (wall[i].lotag == TAG_WALL_MAGIC_MIRROR) {
						short ii, nextii;
						SPRITE sp;

						mirror[mirrorcnt].ismagic = true;
						Found_Cam = false;

						for (ii = headspritestat[STAT_ST1]; ii != -1; ii = nextii) {
							nextii = nextspritestat[ii];
							sp = sprite[ii];
							// if correct type and matches
							if (sp.hitag == MIRROR_CAM && sp.lotag == wall[i].hitag) {
								mirror[mirrorcnt].camera = ii;
								// Set up camera varialbes
								sp.xvel = sp.ang; // Set current angle to
								// sprite angle
								Found_Cam = true;
							}
						}

						ii = nextii = 0;
						for (ii = headspritestat[STAT_SPAWN_SPOT]; ii != -1; ii = nextii) {
							nextii = nextspritestat[ii];
							sp = sprite[ii];

							// if correct type and matches
							if (sp.hitag == MIRROR_CAM && sp.lotag == wall[i].hitag) {
								mirror[mirrorcnt].camera = ii;
								// Set up camera varialbes
								sp.xvel = sp.ang; // Set current angle to
								// sprite angle
								Found_Cam = true;
							}
						}

						if (!Found_Cam) {
							game.GameCrash("Cound not find the camera view sprite for match " + wall[i].hitag
									+ "\r\nMap Coordinates: x = " + wall[i].x + ", y = " + wall[i].y);
							return false;
						}

						Found_Cam = false;
						if (TEST_BOOL1(sprite[mirror[mirrorcnt].camera])) {
							for (SpriteNum = headspritestat[0]; SpriteNum != -1; SpriteNum = NextSprite) {
								NextSprite = nextspritestat[SpriteNum];
								sp = sprite[SpriteNum];
								if (sp.picnum >= CAMSPRITE && sp.picnum < CAMSPRITE + 8 && sp.hitag == wall[i].hitag) {
									mirror[mirrorcnt].campic = sp.picnum;
									mirror[mirrorcnt].camsprite = SpriteNum;
									pic = engine.getTile(mirror[mirrorcnt].campic);
									pic.setWidth(0).setHeight(0);
									Found_Cam = true;
								}
							}

							if (!Found_Cam) {
								game.GameCrash("Did not find drawtotile for camera number " + mirrorcnt + "\r\nwall["
										+ i + "].hitag == " + wall[i].hitag + "\r\nMap Coordinates: x = " + wall[i].x
										+ ", y = " + wall[i].y);
								return false;
							}
						}

						// For magic mirrors, set allowable viewing time to 30
						// secs
						// Base rate is supposed to be 120, but time is double
						// what I expect
						mirror[mirrorcnt].maxtics = 60 * 30;

					}

					mirror[mirrorcnt].mstate = MirrorState.m_normal;

					// Set tics used to none
					mirror[mirrorcnt].tics = 0;

					if (mirror[mirrorcnt].ismagic) {

					}

					mirrorcnt++;
				} else
					wall[i].overpicnum = sector[s].ceilingpicnum;
			}
		}

		// Invalidate textures in sector behind mirror
		for (i = 0; i < mirrorcnt; i++) {
			startwall = sector[mirror[i].mirrorsector].wallptr;
			endwall = (short) (startwall + sector[mirror[i].mirrorsector].wallnum);
			for (j = startwall; j < endwall; j++) {
				wall[j].picnum = MIRROR;
				wall[j].overpicnum = MIRROR;
			}
		}

		return true;

	} // InitMirrors

	/////////////////////////////////////////////////////
	// Draw a 3d screen to a specific tile
	/////////////////////////////////////////////////////
	public static void drawroomstotile(int daposx, int daposy, int daposz, int daang, int dahoriz, int dacursectnum,
			int tilenume, int smoothratio) {
		Tile pic = engine.getTile(tilenume);
		if (pic.data == null)
			engine.loadtile(tilenume);

		engine.setviewtotile(tilenume, pic.getWidth(), pic.getHeight());

		engine.drawrooms(daposx, daposy, daposz, daang, dahoriz, (short) dacursectnum);
		analyzesprites(daposx, daposy, daposz, false, smoothratio);
		engine.drawmasks();

		engine.setviewback();

		engine.squarerotatetile(tilenume);

		engine.getrender().invalidatetile(tilenume, -1, -1);
	}

	public static void JS_ProcessEchoSpot() {
		short i, nexti;
		SPRITE tp;
		long j, dist;
		PlayerStr pp = Player[screenpeek];
		short reverb;
		boolean reverb_set = false;

		// Process echo sprites
		for (i = headspritestat[STAT_ECHO]; i != -1; i = nexti) {
			nexti = nextspritestat[i];
			dist = 0x7fffffff;

			tp = sprite[i];

			j = klabs(tp.x - pp.posx);
			j += klabs(tp.y - pp.posy);
			if (j < dist)
				dist = j;

			if (dist <= SP_TAG4(tp)) // tag4 = ang
			{
				reverb = (short) SP_TAG2(tp);
				if (reverb > 200)
					reverb = 200;
				if (reverb < 100)
					reverb = 100;

				COVER_SetReverb(reverb);
				reverb_set = true;
			}
		}
		if (!TEST(pp.Flags, PF_DIVING) && !reverb_set && pp.Reverb <= 0)
			COVER_SetReverb(0);
	}

	public static void JS_DrawMirrors(PlayerStr pp, int tx, int ty, int tz, float tpang, float tphoriz,
			int smoothratio) {
		int j, dx, dy, cnt;
		int dist;
		boolean bIsWallMirror = false;

		MirrorMoveSkip16 = (MirrorMoveSkip16 + 1) & 15;

		camloopcnt += (totalclock - game.pNet.ototalclock);
		if (camloopcnt > (60 * 5)) // 5 seconds per player view
		{
			camloopcnt = 0;
			camplayerview++;
			if (camplayerview >= numplayers)
				camplayerview = 1;
		}

		// WARNING! Assuming (MIRRORLABEL&31) = 0 and MAXMIRRORS = 64
		if ((gotpic[MIRRORLABEL >> 3] | gotpic[(MIRRORLABEL >> 3) + 1]) != 0) {
			for (cnt = MAXMIRRORS - 1; cnt >= 0; cnt--)
				if (TEST_GOTPIC(cnt + MIRRORLABEL) || TEST_GOTPIC(mirror[cnt].campic)) {
					bIsWallMirror = false;
					if (TEST_GOTPIC(cnt + MIRRORLABEL)) {
						bIsWallMirror = true;
						RESET_GOTPIC(cnt + MIRRORLABEL);
					} else if (TEST_GOTPIC(mirror[cnt].campic)) {
						RESET_GOTPIC(mirror[cnt].campic);
					}

					mirrorinview = true;

					dist = 0x7fffffff;

					if (bIsWallMirror && mirror[cnt].mirrorwall != -1) {
						j = klabs(wall[mirror[cnt].mirrorwall].x - tx);
						j += klabs(wall[mirror[cnt].mirrorwall].y - ty);
						if (j < dist)
							dist = j;
					} else if (mirror[cnt].camsprite != -1) {
						SPRITE tp = sprite[mirror[cnt].camsprite];

						j = klabs(tp.x - tx);
						j += klabs(tp.y - ty);
						if (j < dist)
							dist = j;
					}

					if (mirror[cnt].ismagic) {
						SPRITE sp = null;
						int camhoriz;
						short w;
						int dz, tdx, tdy, tdz, midx, midy;

						sp = sprite[mirror[cnt].camera];

						// Calculate the angle of the mirror wall
						w = mirror[cnt].mirrorwall;

						// Get wall midpoint for offset in mirror view
						midx = (wall[w].x + wall[wall[w].point2].x) / 2;
						midy = (wall[w].y + wall[wall[w].point2].y) / 2;

						// Finish finding offsets
						tdx = klabs(midx - tx);
						tdy = klabs(midy - ty);

						if (midx >= tx)
							dx = sp.x - tdx;
						else
							dx = sp.x + tdx;

						if (midy >= ty)
							dy = sp.y - tdy;
						else
							dy = sp.y + tdy;

						tdz = klabs(tz - sp.z);
						if (tz >= sp.z)
							dz = sp.z + tdz;
						else
							dz = sp.z - tdz;

						// Is it a TV cam or a teleporter that shows destination?
						// true = It's a TV cam
						mirror[cnt].mstate = MirrorState.m_normal;
						if (TEST_BOOL1(sp))
							mirror[cnt].mstate = MirrorState.m_viewon;

						// Show teleport destination
						// NOTE: Adding MAXSECTORS lets you draw a room, even if
						// you are outside of it!
						if (mirror[cnt].mstate != MirrorState.m_viewon) {
							Tile pic = engine.getTile(MIRROR);
							pic.setWidth(0).setHeight(0);
							// Set TV camera sprite size to 0 to show mirror
							// behind in this case!

							if (mirror[cnt].campic != -1) {
								pic = engine.getTile(mirror[cnt].campic);
								pic.setWidth(0).setHeight(0);
							}
							engine.drawrooms(dx, dy, dz, tpang, tphoriz, (short) (sp.sectnum + MAXSECTORS));
							analyzesprites(dx, dy, dz, false, smoothratio);
							engine.drawmasks();
						} else {
							boolean DoCam = false;

							if (mirror[cnt].campic == -1) {
								game.dassert("Missing campic for mirror " + cnt + "\r\nMap Coordinates: x = " + midx
										+ ", y = " + midy);
								return;
							}

							// BOOL2 = Oscilate camera
							if (TEST_BOOL2(sp) && !MoveSkip2) {
								if (TEST_BOOL3(sp)) // If true add increment to
													// angle else subtract
								{
									// Store current angle in TAG5
									sp.xvel = NORM_ANGLE((sp.xvel += 4));

									// TAG6 = Turn radius
									if (klabs(GetDeltaAngle(SP_TAG5(sp), sp.ang)) >= SP_TAG6(sp)) {
										RESET_BOOL3(sp); // Reverse turn
															// direction.
									}
								} else {
									// Store current angle in TAG5
									sp.xvel = NORM_ANGLE((sp.xvel -= 4));

									// TAG6 = Turn radius
									if (klabs(GetDeltaAngle(SP_TAG5(sp), sp.ang)) >= SP_TAG6(sp)) {
										SET_BOOL3(sp); // Reverse turn
														// direction.
									}
								}
							} else if (!TEST_BOOL2(sp)) {
								sp.xvel = sp.ang; // Copy sprite angle to
													// tag5
							}

							// See if there is a horizon value. 0 defaults to
							// 100!
							if (SP_TAG7(sp) != 0) {
								camhoriz = SP_TAG7(sp);
								if (camhoriz > PLAYER_HORIZ_MAX)
									camhoriz = PLAYER_HORIZ_MAX;
								else if (camhoriz < PLAYER_HORIZ_MIN)
									camhoriz = PLAYER_HORIZ_MIN;
							} else
								camhoriz = 100; // Default

							// If player is dead still then update at MoveSkip4
							// rate.
							if (pp.posx == pp.oposx && pp.posy == pp.oposy && pp.posz == pp.oposz)
								DoCam = true;

							// Set up the tile for drawing
							Tile pic = engine.getTile(mirror[cnt].campic);
							pic.setWidth(128).setHeight(128);

							if (MirrorMoveSkip16 == 0 || (DoCam && (MoveSkip4 == 0))) {
								if (dist < MAXCAMDIST) {
									PlayerStr cp = Player[camplayerview];

									if (TEST_BOOL11(sp) && (numplayers > 1 || gNet.FakeMultiplayer)) {
										drawroomstotile(cp.posx, cp.posy, cp.posz, cp.getAnglei(), cp.getHorizi(),
												cp.cursectnum, mirror[cnt].campic, smoothratio);
									} else {
										drawroomstotile(sp.x, sp.y, sp.z, SP_TAG5(sp), camhoriz, sp.sectnum,
												mirror[cnt].campic, smoothratio);
									}
								}
							}
						}
					} else if (mirror[cnt].mirrorwall != -1) { // It's just a mirror
						// Prepare drawrooms for drawing mirror and calculate
						// reflected
						// position into tposx, tposy, and tang (tposz == cposz)
						// Must call preparemirror before drawrooms and
						// completemirror after drawrooms

						engine.preparemirror(tx, ty, tz, tpang, tphoriz, mirror[cnt].mirrorwall,
								mirror[cnt].mirrorsector);

						engine.drawrooms(mirrorx, mirrory, tz, mirrorang, tphoriz,
								(short) (mirror[cnt].mirrorsector + MAXSECTORS));

						analyzesprites(mirrorx, mirrory, tz, true, smoothratio);
						engine.drawmasks();

						engine.completemirror(); // Reverse screen x-wise in this
						// function
					}

					// Clean up anything that the camera view might have done
					Tile pic = engine.getTile(MIRROR);
					pic.setWidth(0).setHeight(0);
					if (mirror[cnt].mirrorwall != -1)
						wall[mirror[cnt].mirrorwall].overpicnum = (short) (MIRRORLABEL + cnt);
				} else
					mirrorinview = false;
		}
	}

	public static void DoAutoSize(SPRITE tspr) {
		if (!bAutoSize)
			return;

		switch (tspr.picnum) {
		case ICON_STAR: // 1793
			break;
		case ICON_UZI: // 1797
			tspr.xrepeat = 43;
			tspr.yrepeat = 40;
			break;
		case ICON_UZIFLOOR: // 1807
			tspr.xrepeat = 43;
			tspr.yrepeat = 40;
			break;
		case ICON_LG_UZI_AMMO: // 1799
			break;
		case ICON_HEART: // 1824
			break;
		case ICON_HEART_LG_AMMO: // 1820
			break;
		case ICON_GUARD_HEAD: // 1814
			break;
		case ICON_FIREBALL_LG_AMMO: // 3035
			break;
		case ICON_ROCKET: // 1843
			break;
		case ICON_SHOTGUN: // 1794
			tspr.xrepeat = 57;
			tspr.yrepeat = 58;
			break;
		case ICON_LG_ROCKET: // 1796
			break;
		case ICON_LG_SHOTSHELL: // 1823
			break;
		case ICON_MICRO_GUN: // 1818
			break;
		case ICON_MICRO_BATTERY: // 1800
			break;
		case ICON_GRENADE_LAUNCHER: // 1817
			tspr.xrepeat = 54;
			tspr.yrepeat = 52;
			break;
		case ICON_LG_GRENADE: // 1831
			break;
		case ICON_LG_MINE: // 1842
			break;
		case ICON_RAIL_GUN: // 1811
			tspr.xrepeat = 50;
			tspr.yrepeat = 54;
			break;
		case ICON_RAIL_AMMO: // 1812
			break;
		case ICON_SM_MEDKIT: // 1802
			break;
		case ICON_MEDKIT: // 1803
			break;
		case ICON_CHEMBOMB:
			tspr.xrepeat = 64;
			tspr.yrepeat = 47;
			break;
		case ICON_FLASHBOMB:
			tspr.xrepeat = 32;
			tspr.yrepeat = 34;
			break;
		case ICON_NUKE:
			break;
		case ICON_CALTROPS:
			tspr.xrepeat = 37;
			tspr.yrepeat = 30;
			break;
		case ICON_BOOSTER: // 1810
			tspr.xrepeat = 30;
			tspr.yrepeat = 38;
			break;
		case ICON_HEAT_CARD: // 1819
			tspr.xrepeat = 46;
			tspr.yrepeat = 47;
			break;
		case ICON_REPAIR_KIT: // 1813
			break;
		case ICON_EXPLOSIVE_BOX: // 1801
			break;
		case ICON_ENVIRON_SUIT: // 1837
			break;
		case ICON_FLY: // 1782
			break;
		case ICON_CLOAK: // 1826
			break;
		case ICON_NIGHT_VISION: // 3031
			tspr.xrepeat = 59;
			tspr.yrepeat = 71;
			break;
		case ICON_NAPALM: // 3046
			break;
		case ICON_RING: // 3050
			break;
		case ICON_RINGAMMO: // 3054
			break;
		case ICON_NAPALMAMMO: // 3058
			break;
		case ICON_GRENADE: // 3059
			break;
		case ICON_ARMOR: // 3030
			tspr.xrepeat = 82;
			tspr.yrepeat = 84;
			break;
		case BLUE_KEY: // 1766
			break;
		case RED_KEY: // 1770
			break;
		case GREEN_KEY: // 1774
			break;
		case YELLOW_KEY: // 1778
			break;
		case BLUE_CARD:
		case RED_CARD:
		case GREEN_CARD:
		case YELLOW_CARD:
			tspr.xrepeat = 36;
			tspr.yrepeat = 33;
			break;
		case GOLD_SKELKEY:
		case SILVER_SKELKEY:
		case BRONZE_SKELKEY:
		case RED_SKELKEY:
			tspr.xrepeat = 39;
			tspr.yrepeat = 45;
			break;
		case SKEL_LOCKED:
		case SKEL_UNLOCKED:
			tspr.xrepeat = 47;
			tspr.yrepeat = 40;
			break;
		case RAMCARD_LOCKED:
		case RAMCARD_UNLOCKED:
		case CARD_LOCKED:
		case CARD_UNLOCKED:
			break;
		default:
			break;
		}
	}

	public static void JAnalyzeSprites(SPRITE tspr) {
		rotang += 4;
		if (rotang > 2047)
			rotang = 0;

		// Take care of autosizing
		DoAutoSize(tspr);

		// Check for voxels
		if (BuildSettings.useVoxels.get()) {
			if (aVoxelArray[tspr.picnum].Voxel >= 0) {
				// Turn on voxels
				tspr.picnum = aVoxelArray[tspr.picnum].Voxel; // Get the voxel number
				tspr.cstat |= 48; // Set stat to voxelize sprite
			} else {
				if (tspr.picnum == 764)
					tspr.cstat |= 16;
			}
		} else {
			switch (tspr.picnum) {
			case 764: // Gun barrel
				if (aVoxelArray[tspr.picnum].Voxel >= 0) {
					// Turn on voxels
					tspr.picnum = aVoxelArray[tspr.picnum].Voxel; // Get the voxel number
					tspr.cstat |= 48; // Set stat to voxelize sprite
				} else
					tspr.cstat |= 16;
				break;
			}
		}
	}

	//////////////////////////////////////////////////////////////////////////////////////////////
	// Parental Lockout Stuff
	//////////////////////////////////////////////////////////////////////////////////////////////
	public static List<OrgTile> orgwalllist = new ArrayList<OrgTile>(); // The list containing
																		// orginal wall
	// pics
	public static List<OrgTile> orgwalloverlist = new ArrayList<OrgTile>(); // The list containing
																			// orginal wall
	// over pics
	public static List<OrgTile> orgsectorceilinglist = new ArrayList<OrgTile>(); // The list
																					// containing
																					// orginal sector
	// ceiling pics
	public static List<OrgTile> orgsectorfloorlist = new ArrayList<OrgTile>(); // The list containing
																				// orginal sector
	// floor pics

	public static OrgTile InitOrgTile(List<OrgTile> thelist) {
		OrgTile tp = new OrgTile();
		thelist.add(tp);

		return tp;
	}

	public static OrgTile FindOrgTile(int index, List<OrgTile> thelist) {
		if (thelist.isEmpty())
			return null;

		for (OrgTile t : thelist) {
			if (t.index == index)
				return (t);
		}

		return null;
	}

	public static void JS_UnInitLockouts() {
		orgwalllist.clear();
		orgwalloverlist.clear();
		orgsectorceilinglist.clear();
		orgsectorfloorlist.clear();
	}

	/////////////////////////////////////////////////////
	// Initialize the original tiles list
	// Creates a list of all orginal tiles and their
	// replacements. Several tiles can use the same
	// replacement tilenum, so the list is built
	// using the original tilenums as a basis for
	// memory allocation
	// t == 1 - wall
	// t == 2 - overpicnum
	// t == 3 - ceiling
	// t == 4 - floor
	/////////////////////////////////////////////////////
	public static void JS_PlockError(int wall_num, int t) {
		String msg = "ERROR: JS_InitLockouts(), out of range tile number";
		game.dassert(msg);
	}

	public static void JS_InitLockouts() {
		short i;
		OrgTile tp;

		orgwalllist.clear(); // The list containing orginal wall
								// pics
		orgwalloverlist.clear(); // The list containing orginal wall
									// over pics
		orgsectorceilinglist.clear(); // The list containing orginal sector
										// ceiling pics
		orgsectorfloorlist.clear(); // The list containing orginal sector
									// floor pics

		// Check all walls
		for (i = 0; i < numwalls; i++) {
			short picnum;

			picnum = wall[i].picnum;
			if (aVoxelArray[picnum].Parental >= INVISTILE)
				JS_PlockError(i, 1);

			if (aVoxelArray[picnum].Parental >= 0) {
				if ((tp = FindOrgTile(i, orgwalllist)) == null)
					tp = InitOrgTile(orgwalllist);
				tp.index = i;
				tp.orgpicnum = wall[i].picnum;
			}

			picnum = wall[i].overpicnum;
			if (aVoxelArray[picnum].Parental >= INVISTILE)
				JS_PlockError(i, 2);

			if (aVoxelArray[picnum].Parental >= 0) {
				if ((tp = FindOrgTile(i, orgwalloverlist)) == null)
					tp = InitOrgTile(orgwalloverlist);
				tp.index = i;
				tp.orgpicnum = wall[i].overpicnum;
			}
		}
		// Check all ceilings and floors
		for (i = 0; i < numsectors; i++) {
			short picnum;

			picnum = sector[i].ceilingpicnum;
			if (aVoxelArray[picnum].Parental >= INVISTILE)
				JS_PlockError(i, 3);

			if (aVoxelArray[picnum].Parental >= 0) {
				if ((tp = FindOrgTile(i, orgsectorceilinglist)) == null)
					tp = InitOrgTile(orgsectorceilinglist);
				tp.index = i;
				tp.orgpicnum = sector[i].ceilingpicnum;
			}

			picnum = sector[i].floorpicnum;
			if (aVoxelArray[picnum].Parental >= INVISTILE)
				JS_PlockError(i, 2);

			if (aVoxelArray[picnum].Parental >= 0) {
				if ((tp = FindOrgTile(i, orgsectorfloorlist)) == null)
					tp = InitOrgTile(orgsectorfloorlist);
				tp.index = i;
				tp.orgpicnum = sector[i].floorpicnum;
			}
		}
	}

	/////////////////////////////////////////////////////
	// Switch back and forth between locked out stuff
	/////////////////////////////////////////////////////
	public static void JS_ToggleLockouts() {
		short i;
		OrgTile tp;

		// Check all walls
		for (i = 0; i < numwalls; i++) {
			short picnum;

			if (gs.ParentalLock) {
				picnum = wall[i].picnum;
				// be invisible
				if (aVoxelArray[picnum].Parental >= 0) {
					wall[i].picnum = aVoxelArray[picnum].Parental;
				}
			} else if ((tp = FindOrgTile(i, orgwalllist)) != null)
				wall[i].picnum = tp.orgpicnum; // Restore them

			if (gs.ParentalLock) {
				picnum = wall[i].overpicnum;

				if (aVoxelArray[picnum].Parental >= 0) {
					wall[i].overpicnum = aVoxelArray[picnum].Parental;
				}
			} else if ((tp = FindOrgTile(i, orgwalloverlist)) != null)
				wall[i].overpicnum = tp.orgpicnum; // Restore them
		}

		// Check all sectors
		for (i = 0; i < numsectors; i++) {
			short picnum;

			if (gs.ParentalLock) {
				picnum = sector[i].ceilingpicnum;

				if (aVoxelArray[picnum].Parental >= 0) {
					sector[i].ceilingpicnum = aVoxelArray[picnum].Parental;
				}
			} else if ((tp = FindOrgTile(i, orgsectorceilinglist)) != null)
				sector[i].ceilingpicnum = tp.orgpicnum; // Restore them

			if (gs.ParentalLock) {
				picnum = sector[i].floorpicnum;

				if (aVoxelArray[picnum].Parental >= 0) {
					sector[i].floorpicnum = aVoxelArray[picnum].Parental;
				}
			} else if ((tp = FindOrgTile(i, orgsectorfloorlist)) != null)
				sector[i].floorpicnum = tp.orgpicnum; // Restore them
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static void UnlockKeyLock(int key_num, int hitsprite) {
		SPRITE sp;
		int SpriteNum = 0, NextSprite = 0, color = 0;

		// Get palette by looking at key number
		switch (key_num - 1) {
		case 0: // RED_KEY
			color = PALETTE_PLAYER9;
			break;
		case 1: // BLUE_KEY
			color = PALETTE_PLAYER7;
			break;
		case 2: // GREEN_KEY
			color = PALETTE_PLAYER6;
			break;
		case 3: // YELLOW_KEY
			color = PALETTE_PLAYER4;
			break;
		case 4: // SILVER_SKELKEY
			color = PALETTE_PLAYER4;
			break;
		case 5: // GOLD_SKELKEY
			color = PALETTE_PLAYER1;
			break;
		case 6: // BRONZE_SKELKEY
			color = PALETTE_PLAYER8;
			break;
		case 7: // RED_SKELKEY
			color = PALETTE_PLAYER9;
			break;
		}

		for (SpriteNum = headspritestat[0]; SpriteNum != -1; SpriteNum = NextSprite) {
			NextSprite = nextspritestat[SpriteNum];
			sp = sprite[SpriteNum];

			switch (sp.picnum) {
			case SKEL_LOCKED:
				if (sp.pal == color) {
					PlaySound(DIGI_UNLOCK, sp, v3df_doppler | v3df_dontpan);
					if (SpriteNum == hitsprite)
						sp.picnum = SKEL_UNLOCKED;
				}
				break;
			case RAMCARD_LOCKED:
				if (sp.pal == color) {
					PlaySound(DIGI_CARDUNLOCK, sp, v3df_doppler | v3df_dontpan);
					sp.picnum = RAMCARD_UNLOCKED;
				}
				break;
			case CARD_LOCKED:
				if (sp.pal == color) {
					PlaySound(DIGI_RAMUNLOCK, sp, v3df_doppler | v3df_dontpan);
					if (SpriteNum == hitsprite)
						sp.picnum = CARD_UNLOCKED;
					else
						sp.picnum = CARD_UNLOCKED + 1;
				}
				break;
			}
		}
	}

}
