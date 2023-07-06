package ru.m210projects.Wang;

import static ru.m210projects.Build.Engine.CLIPMASK0;
import static ru.m210projects.Build.Engine.CLIPMASK1;
import static ru.m210projects.Build.Engine.MAXSECTORS;
import static ru.m210projects.Build.Engine.MAXWALLS;
import static ru.m210projects.Build.Engine.clipmove_sectnum;
import static ru.m210projects.Build.Engine.clipmove_x;
import static ru.m210projects.Build.Engine.clipmove_y;
import static ru.m210projects.Build.Engine.clipmove_z;
import static ru.m210projects.Build.Engine.headspritesect;
import static ru.m210projects.Build.Engine.headspritestat;
import static ru.m210projects.Build.Engine.nextspritesect;
import static ru.m210projects.Build.Engine.nextspritestat;
import static ru.m210projects.Build.Engine.numsectors;
import static ru.m210projects.Build.Engine.pHitInfo;
import static ru.m210projects.Build.Engine.sector;
import static ru.m210projects.Build.Engine.sintable;
import static ru.m210projects.Build.Engine.sprite;
import static ru.m210projects.Build.Engine.totalclock;
import static ru.m210projects.Build.Engine.wall;
import static ru.m210projects.Build.Net.Mmulti.connecthead;
import static ru.m210projects.Build.Net.Mmulti.connectpoint2;
import static ru.m210projects.Build.Net.Mmulti.myconnectindex;
import static ru.m210projects.Build.Pragmas.klabs;
import static ru.m210projects.Build.Pragmas.mulscale;
import static ru.m210projects.Build.Pragmas.tmulscale;
import static ru.m210projects.Wang.Draw.SetRedrawScreen;
import static ru.m210projects.Wang.Factory.WangInput.MAXANGVEL;
import static ru.m210projects.Wang.Game.MessageInputMode;
import static ru.m210projects.Wang.Game.Player;
import static ru.m210projects.Wang.Gameutils.MAXUSERQUOTES;
import static ru.m210projects.Wang.Gameutils.MAX_SW_PLAYERS;
import static ru.m210projects.Wang.Gameutils.MESSAGE_LINE;
import static ru.m210projects.Wang.Gameutils.NORM_ANGLE;
import static ru.m210projects.Wang.Gameutils.PF_DEAD;
import static ru.m210projects.Wang.Gameutils.SK_AUTO_AIM;
import static ru.m210projects.Wang.Gameutils.SK_CRAWL;
import static ru.m210projects.Wang.Gameutils.SK_INV_LEFT;
import static ru.m210projects.Wang.Gameutils.*;
import static ru.m210projects.Wang.Gameutils.SK_JUMP;
import static ru.m210projects.Wang.Gameutils.SK_OPERATE;
import static ru.m210projects.Wang.Gameutils.SK_SHOOT;
import static ru.m210projects.Wang.Gameutils.SK_SPACE_BAR;
import static ru.m210projects.Wang.Gameutils.STD_RANDOM_RANGE;
import static ru.m210projects.Wang.Gameutils.WPN_GRENADE;
import static ru.m210projects.Wang.Gameutils.WPN_MICRO;
import static ru.m210projects.Wang.Gameutils.WPN_RAIL;
import static ru.m210projects.Wang.Gameutils.pUser;
import static ru.m210projects.Wang.Inv.INVENTORY_MEDKIT;
import static ru.m210projects.Wang.Main.NETTEST;
import static ru.m210projects.Wang.Main.engine;
import static ru.m210projects.Wang.Main.gNet;
import static ru.m210projects.Wang.Main.game;
import static ru.m210projects.Wang.Main.gs;
import static ru.m210projects.Wang.Names.BOLT_THINMAN_R0;
import static ru.m210projects.Wang.Names.FIREBALL;
import static ru.m210projects.Wang.Names.ICON_CALTROPS;
import static ru.m210projects.Wang.Names.ICON_CHEMBOMB;
import static ru.m210projects.Wang.Names.ICON_FIREBALL_LG_AMMO;
import static ru.m210projects.Wang.Names.ICON_FLASHBOMB;
import static ru.m210projects.Wang.Names.ICON_GRENADE_LAUNCHER;
import static ru.m210projects.Wang.Names.ICON_GUARD_HEAD;
import static ru.m210projects.Wang.Names.ICON_HEART;
import static ru.m210projects.Wang.Names.ICON_HEART_LG_AMMO;
import static ru.m210projects.Wang.Names.ICON_LG_GRENADE;
import static ru.m210projects.Wang.Names.ICON_LG_MINE;
import static ru.m210projects.Wang.Names.ICON_LG_ROCKET;
import static ru.m210projects.Wang.Names.ICON_LG_SHOTSHELL;
import static ru.m210projects.Wang.Names.ICON_LG_UZI_AMMO;
import static ru.m210projects.Wang.Names.ICON_MEDKIT;
import static ru.m210projects.Wang.Names.ICON_MICRO_BATTERY;
import static ru.m210projects.Wang.Names.ICON_MICRO_GUN;
import static ru.m210projects.Wang.Names.ICON_NUKE;
import static ru.m210projects.Wang.Names.ICON_RAIL_AMMO;
import static ru.m210projects.Wang.Names.ICON_RAIL_GUN;
import static ru.m210projects.Wang.Names.ICON_ROCKET;
import static ru.m210projects.Wang.Names.ICON_SHOTGUN;
import static ru.m210projects.Wang.Names.ICON_STAR;
import static ru.m210projects.Wang.Names.ICON_UZI;
import static ru.m210projects.Wang.Names.ICON_UZIFLOOR;
import static ru.m210projects.Wang.Names.ST1;
import static ru.m210projects.Wang.Names.ST2;
import static ru.m210projects.Wang.Names.STAT_MISSILE;
import static ru.m210projects.Wang.Names.ST_QUICK_EXIT;
import static ru.m210projects.Wang.Names.ST_QUICK_JUMP;
import static ru.m210projects.Wang.Names.ST_QUICK_JUMP_DOWN;
import static ru.m210projects.Wang.Names.ST_QUICK_SCAN;
import static ru.m210projects.Wang.Names.ST_QUICK_SUPER_JUMP;
import static ru.m210projects.Wang.Names.TRACK_SPRITE;
import static ru.m210projects.Wang.Panel.BORDER_BAR;
import static ru.m210projects.Wang.Player.PLAYER_HEIGHT;
import static ru.m210projects.Wang.Player.PLAYER_HORIZ_MAX;
import static ru.m210projects.Wang.Rooms.FAFcansee;
import static ru.m210projects.Wang.Sprites.MoveSkip4;
import static ru.m210projects.Wang.Sprites.MoveSkip8;
import static ru.m210projects.Wang.Type.MyTypes.BIT;
import static ru.m210projects.Wang.Type.MyTypes.TEST;
import static ru.m210projects.Wang.Weapon.DamageData;

import java.util.Arrays;

import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Build.Pattern.BuildFont.TextAlign;
import ru.m210projects.Build.Types.SPRITE;
import ru.m210projects.Build.Types.WALL;
import ru.m210projects.Wang.Type.Input;
import ru.m210projects.Wang.Type.PlayerStr;
import ru.m210projects.Wang.Type.USER;

public class JPlayer {

	// PLAYER QUOTES TO OTHER PLAYERS
	// ////////////////////////////////////////////////////////////

	public static int STARTALPHANUM = 4608; // New SW font for typing in stuff, It's in ASCII order.
	public static int ENDALPHANUM = 4701;
	public static int MINIFONT = 2930; // Start of small font, it's blue for good palette swapping

	public static int NUMOFFIRSTTIMEACTIVE = 100; // You can save up to 100 strings in the message history queue

	public char[][] fta_quotes = new char[NUMOFFIRSTTIMEACTIVE][64];

	public static int quotebot, quotebotgoal;
	public static int user_quote_time[] = new int[MAXUSERQUOTES];
	public static String user_quote[] = new String[MAXUSERQUOTES];

    public static int gametext(int x, int y, String t, int s, int dabits) {
		boolean centre = (x == (320 >> 1));
		return game.getFont(1).drawText(x, y, t, s, 0, centre ? TextAlign.Center : TextAlign.Left, dabits, false);
	}

	public static int minigametext(int x, int y, String t, int s, int dabits) {
		boolean centre = (x == (320 >> 1));
		return game.getFont(0).drawText(x, y, t, s, 4, centre ? TextAlign.Center : TextAlign.Left, dabits, true);
	}

	public static int minitext(int x, int y, String t, int p, int sb) {
		return game.getFont(1).drawText(x, y, t, 0, p, TextAlign.Left, sb, false);
	}

	public static int minitextshade(int x, int y, String t, int s, int p, int sb) {
		return game.getFont(1).drawText(x, y, t, s, p, TextAlign.Left, sb, false);
	}

	public static void adduserquote(String daquote) {
		SetRedrawScreen(Player[myconnectindex]);

		for (int i = MAXUSERQUOTES - 1; i > 0; i--) {
			user_quote[i] = user_quote[i - 1];
			user_quote_time[i] = user_quote_time[i - 1];
		}
		user_quote[0] = daquote;
		user_quote_time[0] = 180;

		Console.Println(daquote);
	}

	public static void operatefta() {
		int i, j, k;

		j = MESSAGE_LINE; // Base line position on screen
		quotebot = Math.min(quotebot, j);
		quotebotgoal = Math.min(quotebotgoal, j);
		if (MessageInputMode)
			j -= 6; // Bump all lines up one to make room for new line
		quotebotgoal = j;
		j = quotebot;

		for (i = 0; i < MAXUSERQUOTES; i++) {
			k = user_quote_time[i];
			if (k <= 0)
				break;

			if (gs.BorderNum <= BORDER_BAR + 1) 
			{
				// dont fade out
				if (k > 4)
					minigametext(320 >> 1, j, user_quote[i], 0, 2 + 8);
				else if (k > 2)
					minigametext(320 >> 1, j, user_quote[i], 0, 2 + 8 + 1);
				else
					minigametext(320 >> 1, j, user_quote[i], 0, 2 + 8 + 1 + 32);
			}
			else {
				// dont fade out
				minigametext(320 >> 1, j, user_quote[i], 0, 2 + 8);
			}

			j -= 6;
		}
	}

    ////////////////////////////////////////////////
	//Draw a string using a small graphic font
	////////////////////////////////////////////////
	public static void MNU_DrawSmallString(int x, int y, String string, int shade, int pal) {
		game.getFont(0).drawText(x, y, string, shade, pal, TextAlign.Left, 10, false);
	}

	// BOT STUFF
	// ////////////////////////////////////////////////////////////////////////////////

	public static void BOT_UseInventory(PlayerStr p, int targetitem, Input syn) {
		// Try to get to item
		if (p.InventoryNum == targetitem)
			syn.bits |= (1 << SK_INV_USE);
		else {
			syn.bits |= (1 << SK_INV_LEFT); // Scroll to it
			syn.bits |= (1 << SK_INV_USE); // Use whatever you're on too
		}
	}

	public static void BOT_ChooseWeapon(PlayerStr p, USER u, Input syn) {
		short weap;

		// If you have a nuke, fire it
		if (u.WeaponNum == WPN_MICRO && p.WpnRocketNuke != 0 && p.WpnRocketType != 2) {
			syn.bits ^= 15;
			syn.bits |= 4;
		} else
			for (weap = 9; weap >= 0; weap--) {
				if (weap <= u.WeaponNum)
					break;
				if (TEST(p.WpnFlags, BIT(weap)) && p.WpnAmmo[weap] > DamageData[weap].min_ammo) {
					syn.bits ^= 15;
					syn.bits |= weap;
					break;
				}
			}
	}

	public static int getspritescore(int snum, int dapicnum) {

		switch (dapicnum) {
		case ICON_STAR:
			return (5);
		case ICON_UZI:
			return (20);
		case ICON_UZIFLOOR:
			return (20);
		case ICON_LG_UZI_AMMO:
			return (15);
		case ICON_HEART:
			return (160);
		case ICON_HEART_LG_AMMO:
			return (60);
		case ICON_GUARD_HEAD:
			return (170);
		case ICON_FIREBALL_LG_AMMO:
			return (70);
		case ICON_ROCKET:
			return (100);
		case ICON_SHOTGUN:
			return (130);
		case ICON_LG_ROCKET:
			return (100);
		case ICON_LG_SHOTSHELL:
			return (30);
		case ICON_MICRO_GUN:
			return (200);
		case ICON_MICRO_BATTERY:
			return (100);
		case ICON_GRENADE_LAUNCHER:
			return (150);
		case ICON_LG_GRENADE:
			return (50);
		case ICON_LG_MINE:
			return (150);
		case ICON_RAIL_GUN:
			return (180);
		case ICON_RAIL_AMMO:
			return (80);

		case ST_QUICK_EXIT:
		case ST_QUICK_SCAN:
		case ICON_MEDKIT:
		case ICON_CHEMBOMB:
		case ICON_FLASHBOMB:
		case ICON_NUKE:
		case ICON_CALTROPS:
		case TRACK_SPRITE:
		case ST1:
		case ST2:
		case ST_QUICK_JUMP:
		case ST_QUICK_JUMP_DOWN:
		case ST_QUICK_SUPER_JUMP:
			return (120);
		}

		return (0);
	}

	public static int[] goalx = new int[MAX_SW_PLAYERS], goaly = new int[MAX_SW_PLAYERS],
			goalz = new int[MAX_SW_PLAYERS];
	public static int[] goalsect = new int[MAX_SW_PLAYERS], goalwall = new int[MAX_SW_PLAYERS],
			goalsprite = new int[MAX_SW_PLAYERS];
	public static int[] goalplayer = new int[MAX_SW_PLAYERS], clipmovecount = new int[MAX_SW_PLAYERS];
	public static short[] searchsect = new short[MAXSECTORS], searchparent = new short[MAXSECTORS];
	public static byte[] dashow2dsector = new byte[(MAXSECTORS + 7) >> 3];

	public static int fdmatrix[][] = {
			// SWRD SHUR UZI SHOT RPG 40MM MINE RAIL HEAD HEAD2 HEAD3 HEART
			{ 128, 128, 128, 128, 128, 128, 128, 128, 128, 128, 128, 128, 128, 0 }, // SWRD
			{ 1024, 512, 128, 128, 2560, 128, 2560, 128, 2560, 2560, 2560, 128, 128, 0 }, // SHUR
			{ 2560, 1024, 512, 512, 2560, 128, 2560, 2560, 1024, 2560, 2560, 2560, 2560, 0 }, // UZI
			{ 512, 512, 512, 512, 2560, 128, 2560, 512, 512, 512, 512, 512, 512, 0 }, // SHOT
			{ 2560, 2560, 2560, 2560, 2560, 2560, 2560, 2560, 2560, 2560, 2560, 2560, 2560, 0 }, // RPG
			{ 512, 512, 512, 512, 2048, 512, 2560, 2560, 512, 2560, 2560, 2560, 2560, 0 }, // 40MM
			{ 128, 128, 128, 128, 512, 128, 128, 128, 128, 128, 128, 128, 128, 0 }, // MINE
			{ 1536, 1536, 1536, 1536, 2560, 1536, 1536, 1536, 1536, 1536, 1536, 1536, 1536, 0 }, // RAIL
			{ 2560, 1024, 512, 1024, 1024, 1024, 2560, 512, 1024, 2560, 2560, 512, 512, 0 }, // HEAD1
			{ 128, 128, 128, 128, 128, 128, 128, 128, 128, 128, 128, 128, 128, 0 }, // HEAD2
			{ 512, 512, 512, 512, 512, 512, 512, 512, 512, 512, 512, 512, 512, 0 }, // HEAD3
			{ 1024, 512, 128, 128, 2560, 512, 2560, 1024, 128, 2560, 1024, 1024, 1024, 0 }, // HEART
			{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, // NULL
			{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, // NULL
	};

	public static void computergetinput(int snum, Input syn) {
		int i, j, k, l, x1, y1, z1, x2, y2, z2, x3, y3, z3, dx, dy, nextj;
		int dist, daang, zang, fightdist, damyang;
		int startsect, endsect, splc, send, startwall, endwall;
		short dasect, damysect;
		PlayerStr p;
		WALL wal;
		USER u;
		
		if (!NETTEST && MoveSkip4 == 0)
			return; // Make it so the bots don't slow the game down so bad!

		p = Player[snum];
		u = pUser[p.PlayerSprite]; // Set user struct

		int WpnNum = p.getWeapon();

		// Init local position variables
		x1 = p.posx;
		y1 = p.posy;
		z1 = p.posz - PLAYER_HEIGHT / 2;
		damyang = p.getAnglei();
		damysect = p.cursectnum;

		// Reset input bits
		syn.vel = 0;
		syn.svel = 0;
		syn.angvel = 0;
		syn.aimvel = 0;
		syn.bits = 0;

		// Always operate everything
		if (MoveSkip8 == 0)
			syn.bits |= (1 << SK_OPERATE);
			
		// If bot can't see the goal enemy, set target to himself so that he
		// will pick a new target
		if (TEST(Player[goalplayer[snum]].Flags, PF_DEAD) || STD_RANDOM_RANGE(1000) > 800)
			goalplayer[snum] = snum;
		else {
			x2 = Player[goalplayer[snum]].posx;
			y2 = Player[goalplayer[snum]].posy;
			z2 = Player[goalplayer[snum]].posz;
			if (!FAFcansee(x1, y1, z1 - (48 << 8), damysect, x2, y2, z2 - (48 << 8),
					sprite[Player[goalplayer[snum]].PlayerSprite].sectnum))
				goalplayer[snum] = snum;
		}

		// Pick a new target player if goal is dead or target is itself
		if (goalplayer[snum] == snum) {
			j = 0x7fffffff;
			for (i = connecthead; i >= 0; i = connectpoint2[i])
				if (i != snum) {
					if (TEST(Player[i].Flags, PF_DEAD))
						continue;

					x2 = Player[i].posx;
					y2 = Player[i].posy;
					z2 = Player[i].posz;

					if (!FAFcansee(x1, y1, z1 - (48 << 8), damysect, x2, y2, z2 - (48 << 8),
							sprite[Player[i].PlayerSprite].sectnum))
						continue;

					dist = engine.ksqrt((sprite[Player[i].PlayerSprite].x - x1)
							* (sprite[Player[i].PlayerSprite].x - x1)
							+ (sprite[Player[i].PlayerSprite].y - y1) * (sprite[Player[i].PlayerSprite].y - y1));

					if (dist < j) {
						j = dist;
						goalplayer[snum] = i;
						
						goalx[snum] = sprite[Player[i].PlayerSprite].x;
						goaly[snum] = sprite[Player[i].PlayerSprite].y;
					}
				}
		}

		// Pick a weapon
		BOT_ChooseWeapon(p, u, syn);

		// x2,y2,z2 is the coordinates of the target sprite
		x2 = Player[goalplayer[snum]].posx;
		y2 = Player[goalplayer[snum]].posy;
		z2 = Player[goalplayer[snum]].posz;

		// If bot is dead, either barf or respawn
		if (TEST(p.Flags, PF_DEAD)) {
			if (STD_RANDOM_RANGE(1000) > 990) {
				syn.bits |= (1 << SK_SPACE_BAR); // Respawn
			} else
				syn.bits |= (1 << SK_SHOOT); // Try to barf
		}

		// Need Health?
		if (u.Health < p.MaxHealth)
			BOT_UseInventory(p, INVENTORY_MEDKIT, syn);

		// Check the missile stat lists to see what's being fired and
		// take the appropriate action

		for (j = headspritestat[STAT_MISSILE]; j != -1; j = nextj) {
			nextj = nextspritestat[j];
			switch (sprite[j].picnum) {
			case FIREBALL:
				k = 0;
				break;
			case BOLT_THINMAN_R0:
				k = 0;
				syn.bits |= (1 << SK_JUMP); // Always jump when rockets being fired!
				break;
			default:
				k = 0;
				break;
			}
			if (k != 0) {
				x3 = sprite[j].x;
				y3 = sprite[j].y;
				z3 = sprite[j].z;
				for (l = 0; l <= 8; l++) {
					if (tmulscale(x3 - x1, x3 - x1, y3 - y1, y3 - y1, (z3 - z1) >> 4, (z3 - z1) >> 4, 11) < 3072) {
						dx = sintable[(sprite[j].ang + 512) & 2047];
						dy = sintable[sprite[j].ang & 2047];
						if ((x1 - x3) * dy > (y1 - y3) * dx)
							i = -k * 512;
						else
							i = k * 512;
						syn.vel -= mulscale(dy, i, 17);
						syn.svel += mulscale(dx, i, 17);
					}

					if (l < 7) {
						x3 += (mulscale(sprite[j].xvel, sintable[(sprite[j].ang + 512) & 2047], 14) << 2);
						y3 += (mulscale(sprite[j].xvel, sintable[sprite[j].ang & 2047], 14) << 2);
						z3 += (sprite[j].zvel << 2);
					} else {
						engine.hitscan(sprite[j].x, sprite[j].y, sprite[j].z, sprite[j].sectnum,
								mulscale(sprite[j].xvel, sintable[(sprite[j].ang + 512) & 2047], 14),
								mulscale(sprite[j].xvel, sintable[sprite[j].ang & 2047], 14), sprite[j].zvel, pHitInfo,
								CLIPMASK1);

						dasect = pHitInfo.hitsect;
						x3 = pHitInfo.hitx;
						y3 = pHitInfo.hity;
						z3 = pHitInfo.hitz;

					}
				}
			}
		}

		if (!TEST(Player[goalplayer[snum]].Flags, PF_DEAD) && snum != goalplayer[snum]
				&& ((FAFcansee(x1, y1, z1 - (24 << 8), damysect, x2, y2, z2 - (24 << 8),
						sprite[Player[goalplayer[snum]].PlayerSprite].sectnum))
						|| (FAFcansee(x1, y1, z1 - (48 << 8), damysect, x2, y2, z2 - (48 << 8),
								sprite[Player[goalplayer[snum]].PlayerSprite].sectnum)))) {
			// Shoot how often by skill level
			short shootrnd = (short) STD_RANDOM_RANGE(1000);
			if ((gNet.BotSkill <= 0 && shootrnd > 990) || (gNet.BotSkill == 1 && shootrnd > 550) || (gNet.BotSkill == 2 && shootrnd > 350)
					|| (gNet.BotSkill == 3)) {
				syn.bits |= (1 << SK_SHOOT);
			}
			else
				syn.bits &= ~(1 << SK_SHOOT);

			// Jump sometimes, to try to be evasive
			if (STD_RANDOM_RANGE(256) > 252)
				syn.bits |= (1 << SK_JUMP);

			// Make sure selected weapon is in range

			// Only fire explosive type weaps if you are not too close to the target!
			if (u.WeaponNum == WPN_MICRO || u.WeaponNum == WPN_GRENADE || u.WeaponNum == WPN_RAIL) {
				int x4, y4;
				engine.hitscan(x1, y1, z1 - PLAYER_HEIGHT, damysect, sintable[(damyang + 512) & 2047],
						sintable[damyang & 2047], (100 - p.getHorizi() - (int) p.horizoff) * 32, pHitInfo, CLIPMASK1);

				dasect = pHitInfo.hitsect;
				x4 = pHitInfo.hitx;
				y4 = pHitInfo.hity;

				if ((x4 - x1) * (x4 - x1) + (y4 - y1) * (y4 - y1) < 2560 * 2560)
					syn.bits &= ~(1 << SK_SHOOT);
			}

			// Get fighting distance based on you and your opponents current weapons
			fightdist = WpnNum < fdmatrix.length ? fdmatrix[WpnNum][Player[goalplayer[snum]].getWeapon()] : 128;
			if (fightdist < 128)
				fightdist = 128;

			// Figure out your distance from the enemy target sprite
			dist = engine.ksqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
			if (dist == 0)
				dist = 1;
			daang = NORM_ANGLE(engine.getangle(x2 + (Player[goalplayer[snum]].xvect >> 14) - x1,
					y2 + (Player[goalplayer[snum]].yvect >> 14) - y1));

			zang = 100 - ((z2 - z1) * 8) / dist;
			fightdist = Math.max(fightdist, (klabs(z2 - z1) >> 4));

			x3 = x2 + ((x1 - x2) * fightdist / dist);
			y3 = y2 + ((y1 - y2) * fightdist / dist);
			syn.vel += (x3 - x1) * 2047 / dist;
			syn.svel += (y3 - y1) * 2047 / dist;

			// Strafe attack
			if (fightdist != 0) {
				j = totalclock + snum * 13468;
				i = sintable[(j << 6) & 2047];
				i += sintable[((j + 4245) << 5) & 2047];
				i += sintable[((j + 6745) << 4) & 2047];
				i += sintable[((j + 15685) << 3) & 2047];
				dx = sintable[(sprite[Player[goalplayer[snum]].PlayerSprite].ang + 512) & 2047];
				dy = sintable[sprite[Player[goalplayer[snum]].PlayerSprite].ang & 2047];
				if ((x1 - x2) * dy > (y1 - y2) * dx)
					i += 8192;
				else
					i -= 8192;
				syn.vel += ((sintable[(daang + 1024) & 2047] * i) >> 17);
				syn.svel += ((sintable[(daang + 512) & 2047] * i) >> 17);
			}

			// Make aiming and running speed suck by skill level
			if (gNet.BotSkill <= 0) {
				daang = NORM_ANGLE((daang - 256) + STD_RANDOM_RANGE(512));
				syn.vel -= syn.vel / 2;
				syn.svel -= syn.svel / 2;
			} else if (gNet.BotSkill == 1) {
				daang = NORM_ANGLE((daang - 128) + STD_RANDOM_RANGE(256));
				syn.vel -= syn.vel / 8;
				syn.svel -= syn.svel / 8;
			} else if (gNet.BotSkill == 2)
				daang = NORM_ANGLE((daang - 64) + STD_RANDOM_RANGE(128));

			// Below formula fails in certain cases
			if(NETTEST)
				syn.angvel = Math.min(Math.max((((daang + 1024 - damyang) & 2047) - 1024) >> 3, -MAXANGVEL), MAXANGVEL);
			else p.pang = (short) daang;
			syn.aimvel = Math.min(Math.max((zang - p.getHorizi()) >> 1, -PLAYER_HORIZ_MAX), PLAYER_HORIZ_MAX);
			// Sets type of aiming, auto aim for bots
			syn.bits |= (1 << SK_AUTO_AIM);
			return;
		}

		goalsect[snum] = -1;
		if (goalsect[snum] < 0) {
			goalwall[snum] = -1;
			startsect = sprite[p.PlayerSprite].sectnum;
			endsect = sprite[Player[goalplayer[snum]].PlayerSprite].sectnum;

			Arrays.fill(dashow2dsector, (byte) 0);

			searchsect[0] = (short) startsect;
			searchparent[0] = -1;
			dashow2dsector[startsect >> 3] |= (1 << (startsect & 7));
			for (splc = 0, send = 1; splc < send; splc++) {
				startwall = sector[searchsect[splc]].wallptr;
				endwall = startwall + sector[searchsect[splc]].wallnum;
				for (i = startwall; i < endwall; i++) {
					wal = wall[i];
					j = wal.nextsector;
					if (j < 0)
						continue;

					dx = ((wall[wal.point2].x + wal.x) >> 1);
					dy = ((wall[wal.point2].y + wal.y) >> 1);
					if ((engine.getceilzofslope((short) j, dx, dy) > engine.getflorzofslope((short) j, dx, dy)
							- (28 << 8)) && ((sector[j].lotag < 15) || (sector[j].lotag > 22)))
						continue;
					if (engine.getceilzofslope((short) j, dx, dy) < engine.getflorzofslope(searchsect[splc], dx, dy)
							- (72 << 8))
						continue;
					
					if ((dashow2dsector[j >> 3] & (1 << (j & 7))) == 0) {
						dashow2dsector[j >> 3] |= (1 << (j & 7));
						searchsect[send] = (short) j;
						searchparent[send] = (short) splc;
						send++;
						if (j == endsect) {
							Arrays.fill(dashow2dsector, (byte) 0);
							for (k = send - 1; k >= 0; k = searchparent[k])
								dashow2dsector[searchsect[k] >> 3] |= (1 << (searchsect[k] & 7));

							for (k = send - 1; k >= 0; k = searchparent[k])
								if (searchparent[k] == 0)
									break;

							goalsect[snum] = searchsect[k];
							startwall = sector[goalsect[snum]].wallptr;
							endwall = startwall + sector[goalsect[snum]].wallnum;
							x3 = y3 = 0;
							for (i = startwall; i < endwall; i++) {
								x3 += wall[i].x;
								y3 += wall[i].y;
							}
							x3 /= (endwall - startwall);
							y3 /= (endwall - startwall);

							startwall = sector[startsect].wallptr;
							endwall = startwall + sector[startsect].wallnum;
							l = 0;
							k = startwall;
							for (i = startwall; i < endwall; i++) {
								if (wall[i].nextsector != goalsect[snum])
									continue;
								dx = wall[wall[i].point2].x - wall[i].x;
								dy = wall[wall[i].point2].y - wall[i].y;

								if ((x3 - x1) * (wall[i].y - y1) <= (y3 - y1) * (wall[i].x - x1))
									if ((x3 - x1) * (wall[wall[i].point2].y - y1) >= (y3 - y1)
											* (wall[wall[i].point2].x - x1)) {
										k = i;
										break;
									}

								dist = engine.ksqrt(dx * dx + dy * dy);
								if (dist > l) {
									l = dist;
									k = i;
								}
							}
							goalwall[snum] = k;
							daang = ((engine.getangle(wall[wall[k].point2].x - wall[k].x,
									wall[wall[k].point2].y - wall[k].y) + 1536) & 2047);
							goalx[snum] = ((wall[k].x + wall[wall[k].point2].x) >> 1)
									+ (sintable[(daang + 512) & 2047] >> 8);
							goaly[snum] = ((wall[k].y + wall[wall[k].point2].y) >> 1) + (sintable[daang & 2047] >> 8);
							goalz[snum] = sector[goalsect[snum]].floorz - (32 << 8);
							break;
						}
					}
				}

			}
		}

		if ((goalsect[snum] < 0) || (goalwall[snum] < 0)) {
			if (goalsprite[snum] < 0) {
				for (k = 0; k < 4; k++) {
					i = (STD_RANDOM_RANGE(32767) % numsectors);
					for (j = headspritesect[i]; j >= 0; j = nextspritesect[j]) {
						if ((sprite[j].xrepeat <= 0) || (sprite[j].yrepeat <= 0))
							continue;
						if (getspritescore(snum, sprite[j].picnum) <= 0)
							continue;

						if (FAFcansee(x1, y1, z1 - (32 << 8), damysect, sprite[j].x, sprite[j].y,
								sprite[j].z - (4 << 8), (short) i)) {
							goalx[snum] = sprite[j].x;
							goaly[snum] = sprite[j].y;
							goalz[snum] = sprite[j].z;
							goalsprite[snum] = j;
							break;
						}
					}
				}
			}

			x2 = goalx[snum];
			y2 = goaly[snum];
			dist = engine.ksqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
			if (dist <= 164) {
				for (k = 0; k < 8; k++) {
					i = (STD_RANDOM_RANGE(32767) % numsectors);
					for (j = headspritesect[i]; j >= 0; j = nextspritesect[j]) {
						if ((sprite[j].xrepeat <= 0) || (sprite[j].yrepeat <= 0) || j == goalsprite[snum])
							continue;
						
							SPRITE sp = sprite[j];
							if (FAFcansee(x1, y1, z1 - (32 << 8), damysect, sp.x, sp.y, sp.z - (4 << 8), (short) i)) {
								dist = engine.ksqrt((sp.x - x1) * (sp.x - x1) + (sp.y - y1) * (sp.y - y1));
								if(dist < 2048 || STD_RANDOM_RANGE(1000) > 500)
									continue;
					
								goalx[snum] = sp.x;
								goaly[snum] = sp.y;
								goalz[snum] = sp.z;
								goalsprite[snum] = j;
								return;
							}
					}
				}
				int daz = ((100 - p.getHorizi()) * 2000);
				engine.hitscan(x1, y1, z1, p.cursectnum, // Start position
						sintable[NORM_ANGLE(p.getAnglei() + 512)], // X vector of 3D ang
						sintable[NORM_ANGLE(p.getAnglei())], // Y vector of 3D ang
						daz, // Z vector of 3D ang
						pHitInfo, 0xFFFFFFFF);
	
				dist = engine.ksqrt((pHitInfo.hitx - x1) * (pHitInfo.hitx - x1) + (pHitInfo.hity - y1) * (pHitInfo.hity - y1));
				if(dist < 512) {
					syn.angvel += STD_RANDOM_RANGE(256);
				}
				else {
					goalx[snum] = pHitInfo.hitx;
					goaly[snum] = pHitInfo.hity;
					goalsprite[snum] = -1;
				}
				
				return;
			}

			daang = engine.getangle(x2 - x1, y2 - y1);
			syn.vel += (x2 - x1) * 2047 / dist;
			syn.svel += (y2 - y1) * 2047 / dist;
			syn.angvel = Math.min(Math.max((((daang + 1024 - damyang) & 2047) - 1024) >> 3, -MAXANGVEL), MAXANGVEL);
		} else
			goalsprite[snum] = -1;

		x3 = p.posx;
		y3 = p.posy;
		z3 = p.posz;
		dasect = p.cursectnum;
		i = engine.clipmove(x3, y3, z3, dasect, p.xvect, p.yvect, 164, 4 << 8, 4 << 8, CLIPMASK0);
		if (clipmove_sectnum != -1) {
			dasect = (short) clipmove_sectnum;
			x3 = clipmove_x;
			y3 = clipmove_y;
			z3 = clipmove_z;
		}

		if (i == 0) {
			x3 = p.posx;
			y3 = p.posy;
			z3 = p.posz + (24 << 8);
			dasect = p.cursectnum;
			i = engine.clipmove(x3, y3, z3, dasect, p.xvect, p.yvect, 164, 4 << 8, 4 << 8, CLIPMASK0);

			if (clipmove_sectnum != -1) {
				dasect = (short) clipmove_sectnum;
				x3 = clipmove_x;
				y3 = clipmove_y;
				z3 = clipmove_z;
			}
		}
		
		if (i != 0) {
			clipmovecount[snum]++;

			j = 0;
			if ((i & 0xc000) == 32768) // Hit a wall (49152 for sprite)
				if (wall[i & (MAXWALLS - 1)].nextsector >= 0) {
					if (engine.getflorzofslope(wall[i & (MAXWALLS - 1)].nextsector, p.posx, p.posy) <= p.posz
							+ (24 << 8))
						j |= 1;
					if (engine.getceilzofslope(wall[i & (MAXWALLS - 1)].nextsector, p.posx, p.posy) >= p.posz
							- (24 << 8))
						j |= 2;
				}
			
			if ((i & 0xc000) == 49152)
				j = 1;
			// Jump
			if ((j & 1) != 0)
				if (clipmovecount[snum] == 4)
					syn.bits |= (1 << SK_JUMP);
			// Crawl
			if ((j & 2) != 0)
				syn.bits |= (1 << SK_CRAWL);

			// Strafe attack
			daang = engine.getangle(x2 - x1, y2 - y1);
			if ((i & 0xc000) == 32768)
				daang = engine.getangle(wall[wall[i & (MAXWALLS - 1)].point2].x - wall[i & (MAXWALLS - 1)].x,
						wall[wall[i & (MAXWALLS - 1)].point2].y - wall[i & (MAXWALLS - 1)].y);
			j = totalclock + snum * 13468;
			i = sintable[(j << 6) & 2047];
			i += sintable[((j + 4245) << 5) & 2047];
			i += sintable[((j + 6745) << 4) & 2047];
			i += sintable[((j + 15685) << 3) & 2047];
			syn.vel += ((sintable[(daang + 1024) & 2047] * i) >> 17);
			syn.svel += ((sintable[(daang + 512) & 2047] * i) >> 17);
		
			// Try to Open
			if ((clipmovecount[snum] & 31) == 2)
				syn.bits |= (1 << SK_OPERATE);
			// *TODO: In Duke, this is Kick, but I need to select sword then fire in SW
//	        if ((clipmovecount[snum]&31) == 17) syn.bits |= (1<<22);
			if (clipmovecount[snum] > 32) {
				syn.vel = syn.svel = 0;
				
				int daz = ((100 - p.getHorizi()) * 2000);
				engine.hitscan(x1, y1, z1, p.cursectnum, // Start position
						sintable[NORM_ANGLE(p.getAnglei() + 512)], // X vector of 3D ang
						sintable[NORM_ANGLE(p.getAnglei())], // Y vector of 3D ang
						daz, // Z vector of 3D ang
						pHitInfo, 0xFFFFFFFF);
	
				dist = engine.ksqrt((pHitInfo.hitx - x1) * (pHitInfo.hitx - x1) + (pHitInfo.hity - y1) * (pHitInfo.hity - y1));
				if(dist < 512) {
					syn.bits |= (1 << SK_TURN_180);
					syn.angvel += STD_RANDOM_RANGE(256);
				} else {
					goalx[snum] = pHitInfo.hitx;
					goaly[snum] = pHitInfo.hity;
					goalz[snum] = pHitInfo.hitz;

					goalsect[snum] = -1;
					goalwall[snum] = -1;
					clipmovecount[snum] = 0;
				}
			}

			goalsprite[snum] = -1;
		} else
			clipmovecount[snum] = 0;

		if(p.getHorizi() < 100)
			syn.aimvel += 1;
		
		if(p.getHorizi() > 100)
			syn.aimvel -= 1;

		if ((goalsect[snum] >= 0) && (goalwall[snum] >= 0)) {
			x2 = goalx[snum];
			y2 = goaly[snum];
			dist = engine.ksqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
			if (dist == 0)
				return;
			daang = engine.getangle(x2 - x1, y2 - y1);
			if ((goalwall[snum] >= 0) && (dist < 4096))
				daang = ((engine.getangle(wall[wall[goalwall[snum]].point2].x - wall[goalwall[snum]].x,
						wall[wall[goalwall[snum]].point2].y - wall[goalwall[snum]].y) + 1536) & 2047);
			syn.vel += (x2 - x1) * 2047 / dist;
			syn.svel += (y2 - y1) * 2047 / dist;
			syn.angvel = Math.min(Math.max((((daang + 1024 - damyang) & 2047) - 1024) >> 3, -MAXANGVEL), MAXANGVEL);
		}
	}

}
